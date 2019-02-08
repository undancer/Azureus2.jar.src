/*     */ package org.gudy.azureus2.ui.swt.views.clientstats;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.net.InetAddress;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Layout;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClientStatsView
/*     */   extends TableViewTab<ClientStatsDataSource>
/*     */   implements TableLifeCycleListener, GlobalManagerListener, DownloadManagerPeerListener
/*     */ {
/*     */   private static final String CONFIG_FILE = "ClientStats.dat";
/*     */   private static final String CONFIG_FILE_ARCHIVE = "ClientStats_%1.dat";
/*     */   private static final int BLOOMFILTER_SIZE = 100000;
/*     */   private static final int BLOOMFILTER_PEERID_SIZE = 50000;
/*     */   private static final String TABLEID = "ClientStats";
/*     */   private AzureusCore core;
/*     */   private TableViewSWT<ClientStatsDataSource> tv;
/*     */   private boolean columnsAdded;
/*  83 */   private final Map<String, ClientStatsDataSource> mapData = new HashMap();
/*     */   
/*     */   private Composite parent;
/*     */   
/*     */   private BloomFilter bloomFilter;
/*     */   
/*     */   private BloomFilter bloomFilterPeerId;
/*     */   
/*     */   private ClientStatsOverall overall;
/*     */   
/*     */   private long startedListeningOn;
/*     */   
/*     */   private long totalTime;
/*     */   
/*     */   private long lastAdd;
/*     */   
/*  99 */   private GregorianCalendar calendar = new GregorianCalendar();
/*     */   
/*     */   private int lastAddMonth;
/*     */   
/* 103 */   private static boolean registered = false;
/*     */   
/*     */   public ClientStatsView() {
/* 106 */     super("ClientStats");
/*     */     
/* 108 */     initAndLoad();
/*     */     
/* 110 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 112 */         ClientStatsView.this.initColumns(core);
/* 113 */         ClientStatsView.this.register(core);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public Composite initComposite(Composite composite) {
/* 119 */     this.parent = new Composite(composite, 2048);
/* 120 */     this.parent.setLayout(new FormLayout());
/* 121 */     Layout layout = composite.getLayout();
/* 122 */     if ((layout instanceof GridLayout)) {
/* 123 */       Utils.setLayoutData(this.parent, new GridData(4, 4, true, true));
/* 124 */     } else if ((layout instanceof FormLayout)) {
/* 125 */       Utils.setLayoutData(this.parent, Utils.getFilledFormData());
/*     */     }
/*     */     
/* 128 */     return this.parent;
/*     */   }
/*     */   
/*     */   public void tableViewTabInitComplete() {
/* 132 */     Composite cTV = (Composite)this.parent.getChildren()[0];
/* 133 */     Composite cBottom = new Composite(this.parent, 0);
/*     */     
/* 135 */     FormData fd = Utils.getFilledFormData();
/* 136 */     fd.bottom = new FormAttachment(cBottom);
/* 137 */     Utils.setLayoutData(cTV, fd);
/* 138 */     fd = Utils.getFilledFormData();
/* 139 */     fd.top = null;
/* 140 */     Utils.setLayoutData(cBottom, fd);
/* 141 */     cBottom.setLayout(new FormLayout());
/*     */     
/* 143 */     Button btnCopy = new Button(cBottom, 8);
/* 144 */     Utils.setLayoutData(btnCopy, new FormData());
/* 145 */     btnCopy.setText("Copy");
/* 146 */     btnCopy.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 148 */         TableRowCore[] rows = ClientStatsView.this.tv.getRows();
/* 149 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 151 */         sb.append(new SimpleDateFormat("MMM yyyy").format(new Date()));
/* 152 */         sb.append("\n");
/*     */         
/* 154 */         sb.append("Hits,Client,Bytes Sent,Bytes Received,Bad Bytes\n");
/* 155 */         for (TableRowCore row : rows) {
/* 156 */           ClientStatsDataSource stat = (ClientStatsDataSource)row.getDataSource();
/* 157 */           if (stat != null)
/*     */           {
/*     */ 
/* 160 */             sb.append(stat.count);
/* 161 */             sb.append(",");
/* 162 */             sb.append(stat.client.replaceAll(",", ""));
/* 163 */             sb.append(",");
/* 164 */             sb.append(stat.bytesSent);
/* 165 */             sb.append(",");
/* 166 */             sb.append(stat.bytesReceived);
/* 167 */             sb.append(",");
/* 168 */             sb.append(stat.bytesDiscarded);
/* 169 */             sb.append("\n");
/*     */           } }
/* 171 */         ClipboardCopy.copyToClipBoard(sb.toString());
/*     */       }
/*     */       
/* 174 */     });
/* 175 */     Button btnCopyShort = new Button(cBottom, 8);
/* 176 */     Utils.setLayoutData(btnCopyShort, new FormData());
/* 177 */     btnCopyShort.setText("Copy > 1%");
/* 178 */     btnCopyShort.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 180 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 182 */         sb.append(new SimpleDateFormat("MMM ''yy").format(new Date()));
/* 183 */         sb.append("] ");
/* 184 */         sb.append(ClientStatsView.this.overall.count);
/* 185 */         sb.append(": ");
/*     */         
/*     */         ClientStatsDataSource[] stats;
/*     */         
/* 189 */         synchronized (ClientStatsView.this.mapData)
/*     */         {
/* 191 */           stats = (ClientStatsDataSource[])ClientStatsView.this.mapData.values().toArray(new ClientStatsDataSource[0]);
/*     */         }
/*     */         
/* 194 */         Arrays.sort(stats, new Comparator() {
/*     */           public int compare(ClientStatsDataSource o1, ClientStatsDataSource o2) {
/* 196 */             if (o1.count == o2.count) {
/* 197 */               return 0;
/*     */             }
/* 199 */             return o1.count > o2.count ? -1 : 1;
/*     */           }
/*     */           
/* 202 */         });
/* 203 */         boolean first = true;
/* 204 */         for (ClientStatsDataSource stat : stats) {
/* 205 */           int pct = (int)(stat.count * 1000 / ClientStatsView.this.overall.count);
/* 206 */           if (pct >= 10)
/*     */           {
/*     */ 
/* 209 */             if (first) {
/* 210 */               first = false;
/*     */             } else {
/* 212 */               sb.append(", ");
/*     */             }
/* 214 */             sb.append(DisplayFormatters.formatPercentFromThousands(pct));
/* 215 */             sb.append(" ");
/* 216 */             sb.append(stat.client);
/*     */           }
/*     */         }
/* 219 */         Arrays.sort(stats, new Comparator() {
/*     */           public int compare(ClientStatsDataSource o1, ClientStatsDataSource o2) {
/* 221 */             float v1 = (float)o1.bytesReceived / o1.count;
/* 222 */             float v2 = (float)o2.bytesReceived / o2.count;
/* 223 */             if (v1 == v2) {
/* 224 */               return 0;
/*     */             }
/* 226 */             return v1 > v2 ? -1 : 1;
/*     */           }
/*     */           
/* 229 */         });
/* 230 */         int top = 5;
/* 231 */         first = true;
/* 232 */         sb.append("\nBest Seeders (");
/* 233 */         long total = 0L;
/* 234 */         for (ClientStatsDataSource stat : stats) {
/* 235 */           total += stat.bytesReceived;
/*     */         }
/* 237 */         sb.append(DisplayFormatters.formatByteCountToKiBEtc(total, false, true, 0));
/*     */         
/* 239 */         sb.append(" Downloaded): ");
/* 240 */         for (ClientStatsDataSource stat : stats) {
/* 241 */           if (first) {
/* 242 */             first = false;
/*     */           } else {
/* 244 */             sb.append(", ");
/*     */           }
/* 246 */           sb.append(DisplayFormatters.formatByteCountToKiBEtc(stat.bytesReceived / stat.count, false, true, 0));
/*     */           
/* 248 */           sb.append(" per ");
/* 249 */           sb.append(stat.client);
/* 250 */           sb.append("(x");
/* 251 */           sb.append(stat.count);
/* 252 */           sb.append(")");
/* 253 */           top--; if (top <= 0) {
/*     */             break;
/*     */           }
/*     */         }
/*     */         
/* 258 */         Arrays.sort(stats, new Comparator() {
/*     */           public int compare(ClientStatsDataSource o1, ClientStatsDataSource o2) {
/* 260 */             float v1 = (float)o1.bytesDiscarded / o1.count;
/* 261 */             float v2 = (float)o2.bytesDiscarded / o2.count;
/* 262 */             if (v1 == v2) {
/* 263 */               return 0;
/*     */             }
/* 265 */             return v1 > v2 ? -1 : 1;
/*     */           }
/* 267 */         });
/* 268 */         top = 5;
/* 269 */         first = true;
/* 270 */         sb.append("\nMost Discarded (");
/* 271 */         total = 0L;
/* 272 */         for (ClientStatsDataSource stat : stats) {
/* 273 */           total += stat.bytesDiscarded;
/*     */         }
/* 275 */         sb.append(DisplayFormatters.formatByteCountToKiBEtc(total, false, true, 0));
/*     */         
/* 277 */         sb.append(" Discarded): ");
/* 278 */         for (ClientStatsDataSource stat : stats) {
/* 279 */           if (first) {
/* 280 */             first = false;
/*     */           } else {
/* 282 */             sb.append(", ");
/*     */           }
/* 284 */           sb.append(DisplayFormatters.formatByteCountToKiBEtc(stat.bytesDiscarded / stat.count, false, true, 0));
/*     */           
/* 286 */           sb.append(" per ");
/* 287 */           sb.append(stat.client);
/* 288 */           sb.append("(x");
/* 289 */           sb.append(stat.count);
/* 290 */           sb.append(")");
/* 291 */           top--; if (top <= 0) {
/*     */             break;
/*     */           }
/*     */         }
/*     */         
/* 296 */         Arrays.sort(stats, new Comparator() {
/*     */           public int compare(ClientStatsDataSource o1, ClientStatsDataSource o2) {
/* 298 */             float v1 = (float)o1.bytesSent / o1.count;
/* 299 */             float v2 = (float)o2.bytesSent / o2.count;
/* 300 */             if (v1 == v2) {
/* 301 */               return 0;
/*     */             }
/* 303 */             return v1 > v2 ? -1 : 1;
/*     */           }
/* 305 */         });
/* 306 */         top = 5;
/* 307 */         first = true;
/* 308 */         sb.append("\nMost Fed (");
/* 309 */         total = 0L;
/* 310 */         for (ClientStatsDataSource stat : stats) {
/* 311 */           total += stat.bytesSent;
/*     */         }
/* 313 */         sb.append(DisplayFormatters.formatByteCountToKiBEtc(total, false, true, 0));
/*     */         
/* 315 */         sb.append(" Sent): ");
/* 316 */         for (ClientStatsDataSource stat : stats) {
/* 317 */           if (first) {
/* 318 */             first = false;
/*     */           } else {
/* 320 */             sb.append(", ");
/*     */           }
/* 322 */           sb.append(DisplayFormatters.formatByteCountToKiBEtc(stat.bytesSent / stat.count, false, true, 0));
/*     */           
/* 324 */           sb.append(" per ");
/* 325 */           sb.append(stat.client);
/* 326 */           sb.append("(x");
/* 327 */           sb.append(stat.count);
/* 328 */           sb.append(")");
/* 329 */           top--; if (top <= 0) {
/*     */             break;
/*     */           }
/*     */         }
/*     */         
/* 334 */         ClipboardCopy.copyToClipBoard(sb.toString());
/*     */       }
/* 336 */     });
/* 337 */     fd = new FormData();
/* 338 */     fd.left = new FormAttachment(btnCopy, 5);
/* 339 */     Utils.setLayoutData(btnCopyShort, fd);
/*     */   }
/*     */   
/*     */   public TableViewSWT<ClientStatsDataSource> initYourTableView() {
/* 343 */     this.tv = TableViewFactory.createTableViewSWT(ClientStatsDataSource.class, "ClientStats", getPropertiesPrefix(), new TableColumnCore[0], "count", 268500994);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 352 */     this.tv.addLifeCycleListener(this);
/*     */     
/* 354 */     return this.tv;
/*     */   }
/*     */   
/*     */   private void initColumns(AzureusCore core) {
/* 358 */     synchronized (ClientStatsView.class)
/*     */     {
/* 360 */       if (this.columnsAdded)
/*     */       {
/* 362 */         return;
/*     */       }
/*     */       
/* 365 */       this.columnsAdded = true;
/*     */     }
/*     */     
/* 368 */     UIManager uiManager = PluginInitializer.getDefaultInterface().getUIManager();
/*     */     
/* 370 */     TableManager tableManager = uiManager.getTableManager();
/*     */     
/* 372 */     tableManager.registerColumn(ClientStatsDataSource.class, "name", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 375 */         new ColumnCS_Name(column);
/*     */       }
/* 377 */     });
/* 378 */     tableManager.registerColumn(ClientStatsDataSource.class, "count", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 381 */         new ColumnCS_Count(column);
/*     */       }
/* 383 */     });
/* 384 */     tableManager.registerColumn(ClientStatsDataSource.class, "discarded", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 387 */         new ColumnCS_Discarded(column);
/*     */       }
/* 389 */     });
/* 390 */     tableManager.registerColumn(ClientStatsDataSource.class, "received", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 393 */         new ColumnCS_Received(column);
/*     */       }
/* 395 */     });
/* 396 */     tableManager.registerColumn(ClientStatsDataSource.class, "received.per", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 399 */         new ColumnCS_ReceivedPer(column);
/*     */       }
/* 401 */     });
/* 402 */     tableManager.registerColumn(ClientStatsDataSource.class, "sent", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 405 */         new ColumnCS_Sent(column);
/*     */       }
/* 407 */     });
/* 408 */     tableManager.registerColumn(ClientStatsDataSource.class, "percent", new TableColumnCreationListener()
/*     */     {
/*     */       public void tableColumnCreated(TableColumn column) {
/* 411 */         new ColumnCS_Pct(column);
/*     */       }
/*     */     });
/*     */     
/* 415 */     for (final String network : AENetworkClassifier.AT_NETWORKS) {
/* 416 */       tableManager.registerColumn(ClientStatsDataSource.class, network + "." + "sent", new TableColumnCreationListener()
/*     */       {
/*     */         public void tableColumnCreated(TableColumn column) {
/* 419 */           column.setUserData("network", network);
/* 420 */           new ColumnCS_Sent(column);
/*     */         }
/* 422 */       });
/* 423 */       tableManager.registerColumn(ClientStatsDataSource.class, network + "." + "discarded", new TableColumnCreationListener()
/*     */       {
/*     */         public void tableColumnCreated(TableColumn column) {
/* 426 */           column.setUserData("network", network);
/* 427 */           new ColumnCS_Discarded(column);
/*     */         }
/* 429 */       });
/* 430 */       tableManager.registerColumn(ClientStatsDataSource.class, network + "." + "received", new TableColumnCreationListener()
/*     */       {
/*     */         public void tableColumnCreated(TableColumn column) {
/* 433 */           column.setUserData("network", network);
/* 434 */           new ColumnCS_Received(column);
/*     */         }
/* 436 */       });
/* 437 */       tableManager.registerColumn(ClientStatsDataSource.class, network + "." + "count", new TableColumnCreationListener()
/*     */       {
/*     */         public void tableColumnCreated(TableColumn column) {
/* 440 */           column.setUserData("network", network);
/* 441 */           new ColumnCS_Count(column);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 446 */     TableColumnManager tcManager = TableColumnManager.getInstance();
/* 447 */     tcManager.setDefaultColumnNames("ClientStats", new String[] { "name", "percent", "count", "received", "sent", "discarded" });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tableViewDestroyed() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initAndLoad()
/*     */   {
/* 462 */     synchronized (this.mapData) {
/* 463 */       Map map = FileUtil.readResilientConfigFile("ClientStats.dat");
/*     */       
/* 465 */       this.totalTime = MapUtils.getMapLong(map, "time", 0L);
/*     */       
/* 467 */       this.lastAdd = MapUtils.getMapLong(map, "lastadd", 0L);
/* 468 */       if (this.lastAdd != 0L) {
/* 469 */         this.calendar.setTimeInMillis(this.lastAdd);
/* 470 */         this.lastAddMonth = this.calendar.get(2);
/*     */         
/* 472 */         Map mapBloom = MapUtils.getMapMap(map, "bloomfilter", null);
/* 473 */         if (mapBloom != null) {
/* 474 */           this.bloomFilter = BloomFilterFactory.deserialiseFromMap(mapBloom);
/*     */         }
/* 476 */         mapBloom = MapUtils.getMapMap(map, "bloomfilterPeerId", null);
/* 477 */         if (mapBloom != null) {
/* 478 */           this.bloomFilterPeerId = BloomFilterFactory.deserialiseFromMap(mapBloom);
/*     */         }
/*     */       }
/* 481 */       if (this.bloomFilter == null) {
/* 482 */         this.bloomFilter = BloomFilterFactory.createRotating(BloomFilterFactory.createAddOnly(100000), 2);
/*     */       }
/*     */       
/* 485 */       if (this.bloomFilterPeerId == null) {
/* 486 */         this.bloomFilterPeerId = BloomFilterFactory.createRotating(BloomFilterFactory.createAddOnly(50000), 2);
/*     */       }
/*     */       
/*     */ 
/* 490 */       this.overall = new ClientStatsOverall();
/*     */       
/* 492 */       List listSavedData = MapUtils.getMapList(map, "data", null);
/* 493 */       if (listSavedData != null) {
/* 494 */         for (Object val : listSavedData) {
/*     */           try {
/* 496 */             Map mapVal = (Map)val;
/* 497 */             if (mapVal != null) {
/* 498 */               ClientStatsDataSource ds = new ClientStatsDataSource(mapVal);
/* 499 */               ds.overall = this.overall;
/*     */               
/* 501 */               if (!this.mapData.containsKey(ds.client)) {
/* 502 */                 this.mapData.put(ds.client, ds);
/* 503 */                 this.overall.count += ds.count;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void save(String filename)
/*     */   {
/* 516 */     Map<String, Object> map = new HashMap();
/* 517 */     synchronized (this.mapData) {
/* 518 */       map.put("data", new ArrayList(this.mapData.values()));
/* 519 */       map.put("bloomfilter", this.bloomFilter.serialiseToMap());
/* 520 */       map.put("bloomfilterPeerId", this.bloomFilterPeerId.serialiseToMap());
/* 521 */       map.put("lastadd", Long.valueOf(SystemTime.getCurrentTime()));
/* 522 */       if (this.startedListeningOn > 0L) {
/* 523 */         map.put("time", Long.valueOf(this.totalTime + (SystemTime.getCurrentTime() - this.startedListeningOn)));
/*     */       }
/*     */       else {
/* 526 */         map.put("time", Long.valueOf(this.totalTime));
/*     */       }
/*     */     }
/* 529 */     FileUtil.writeResilientConfigFile(filename, map);
/*     */   }
/*     */   
/*     */   public void tableViewInitialized() {
/* 533 */     synchronized (this.mapData) {
/* 534 */       if (this.mapData.values().size() > 0) {
/* 535 */         this.tv.addDataSources(this.mapData.values().toArray(new ClientStatsDataSource[0]));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void register(AzureusCore core) {
/* 541 */     this.core = core;
/* 542 */     core.getGlobalManager().addListener(this);
/* 543 */     synchronized (this.mapData) {
/* 544 */       this.startedListeningOn = SystemTime.getCurrentTime();
/*     */     }
/* 546 */     registered = true;
/*     */   }
/*     */   
/*     */   public void destroyInitiated()
/*     */   {
/* 551 */     if (this.core == null) {
/* 552 */       return;
/*     */     }
/* 554 */     this.core.getGlobalManager().removeListener(this);
/* 555 */     List downloadManagers = this.core.getGlobalManager().getDownloadManagers();
/* 556 */     for (Object object : downloadManagers) {
/* 557 */       ((DownloadManager)object).removePeerListener(this);
/*     */     }
/* 559 */     registered = false;
/* 560 */     save("ClientStats.dat");
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroyed() {}
/*     */   
/*     */   public void downloadManagerAdded(DownloadManager dm)
/*     */   {
/* 568 */     if (!dm.getDownloadState().getFlag(16L)) {
/* 569 */       dm.addPeerListener(this, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void downloadManagerRemoved(DownloadManager dm) {
/* 574 */     dm.removePeerListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void seedingStatusChanged(boolean seedingOnlyMode, boolean potentiallySeedingOnlyMode) {}
/*     */   
/*     */   public void peerAdded(PEPeer peer)
/*     */   {
/* 582 */     peer.addListener(new PEPeerListener()
/*     */     {
/*     */       public void stateChanged(PEPeer peer, int newState) {
/* 585 */         if (newState == 30) {
/* 586 */           ClientStatsView.this.addPeer(peer);
/* 587 */         } else if ((newState == 40) || (newState == 50))
/*     */         {
/* 589 */           peer.removeListener(this);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void sentBadChunk(PEPeer peer, int pieceNum, int totalBadChunks) {}
/*     */       
/*     */ 
/*     */       public void removeAvailability(PEPeer peer, BitFlags peerHavePieces) {}
/*     */       
/*     */ 
/*     */       public void addAvailability(PEPeer peer, BitFlags peerHavePieces) {}
/*     */     });
/*     */   }
/*     */   
/*     */   protected void addPeer(PEPeer peer)
/*     */   {
/* 606 */     long now = SystemTime.getCurrentTime();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 612 */     byte[] address = null;
/* 613 */     byte[] peerId = peer.getId();
/* 614 */     InetAddress ip = peer.getAlternativeIPv6();
/* 615 */     if (ip == null) {
/*     */       try {
/* 617 */         ip = AddressUtils.getByName(peer.getIp());
/* 618 */         address = ip.getAddress();
/*     */       } catch (Throwable e) {
/* 620 */         String ipString = peer.getIp();
/* 621 */         if (ipString != null) {
/* 622 */           address = ByteFormatter.intToByteArray(ipString.hashCode());
/*     */         }
/*     */       }
/*     */     } else
/* 626 */       address = ip.getAddress();
/*     */     byte[] bloomId;
/* 628 */     byte[] bloomId; if (address == null) {
/* 629 */       bloomId = peerId;
/*     */     } else {
/* 631 */       bloomId = new byte[8 + address.length];
/* 632 */       System.arraycopy(peerId, 0, bloomId, 0, 8);
/* 633 */       System.arraycopy(address, 0, bloomId, 8, address.length);
/*     */     }
/*     */     
/* 636 */     synchronized (this.mapData)
/*     */     {
/* 638 */       this.calendar.setTimeInMillis(now);
/* 639 */       int thisMonth = this.calendar.get(2);
/* 640 */       if (thisMonth != this.lastAddMonth) {
/* 641 */         if (this.lastAddMonth == 0) {
/* 642 */           this.lastAddMonth = thisMonth;
/*     */         } else {
/* 644 */           String s = new SimpleDateFormat("yyyy-MM").format(new Date(this.lastAdd));
/* 645 */           String filename = "ClientStats_%1.dat".replace("%1", s);
/* 646 */           save(filename);
/*     */           
/* 648 */           this.lastAddMonth = thisMonth;
/* 649 */           this.lastAdd = 0L;
/* 650 */           this.bloomFilter = BloomFilterFactory.createRotating(BloomFilterFactory.createAddOnly(100000), 2);
/*     */           
/* 652 */           this.bloomFilterPeerId = BloomFilterFactory.createRotating(BloomFilterFactory.createAddOnly(50000), 2);
/*     */           
/* 654 */           this.overall = new ClientStatsOverall();
/* 655 */           this.mapData.clear();
/* 656 */           if (this.tv != null) {
/* 657 */             this.tv.removeAllTableRows();
/*     */           }
/* 659 */           this.totalTime = 0L;
/* 660 */           this.startedListeningOn = 0L;
/*     */         }
/*     */       }
/*     */       
/* 664 */       String id = getID(peer);
/*     */       
/* 666 */       ClientStatsDataSource stat = (ClientStatsDataSource)this.mapData.get(id);
/* 667 */       boolean needNew = stat == null;
/* 668 */       if (needNew) {
/* 669 */         stat = new ClientStatsDataSource();
/* 670 */         stat.overall = this.overall;
/* 671 */         stat.client = id;
/* 672 */         this.mapData.put(id, stat);
/*     */       }
/*     */       
/* 675 */       boolean inBloomFilter = (this.bloomFilter.contains(bloomId)) || (this.bloomFilterPeerId.contains(peerId));
/*     */       
/* 677 */       if (!inBloomFilter) {
/* 678 */         this.bloomFilter.add(bloomId);
/* 679 */         this.bloomFilterPeerId.add(peerId);
/*     */         
/* 681 */         this.lastAdd = now;
/* 682 */         synchronized (this.overall)
/*     */         {
/* 684 */           this.overall.count += 1L;
/*     */         }
/* 686 */         stat.count += 1;
/*     */       }
/*     */       
/* 689 */       stat.current += 1;
/*     */       
/* 691 */       long existingBytesReceived = peer.getStats().getTotalDataBytesReceived();
/* 692 */       long existingBytesSent = peer.getStats().getTotalDataBytesSent();
/* 693 */       long existingBytesDiscarded = peer.getStats().getTotalBytesDiscarded();
/*     */       
/* 695 */       if (existingBytesReceived > 0L) {
/* 696 */         stat.bytesReceived -= existingBytesReceived;
/* 697 */         if (stat.bytesReceived < 0L) {
/* 698 */           stat.bytesReceived = 0L;
/*     */         }
/*     */       }
/* 701 */       if (existingBytesSent > 0L) {
/* 702 */         stat.bytesSent -= existingBytesSent;
/* 703 */         if (stat.bytesSent < 0L) {
/* 704 */           stat.bytesSent = 0L;
/*     */         }
/*     */       }
/* 707 */       if (existingBytesDiscarded > 0L) {
/* 708 */         stat.bytesDiscarded -= existingBytesDiscarded;
/* 709 */         if (stat.bytesDiscarded < 0L) {
/* 710 */           stat.bytesDiscarded = 0L;
/*     */         }
/*     */       }
/*     */       
/* 714 */       if ((peer instanceof PEPeerTransport)) {
/* 715 */         PeerItem identity = ((PEPeerTransport)peer).getPeerItemIdentity();
/* 716 */         if (identity != null) {
/* 717 */           String network = identity.getNetwork();
/* 718 */           if (network != null) {
/* 719 */             Map<String, Object> map = (Map)stat.perNetworkStats.get(network);
/* 720 */             if (map == null) {
/* 721 */               map = new HashMap();
/* 722 */               stat.perNetworkStats.put(network, map);
/*     */             }
/* 724 */             if (!inBloomFilter) {
/* 725 */               long count = MapUtils.getMapLong(map, "count", 0L);
/* 726 */               map.put("count", Long.valueOf(count + 1L));
/*     */             }
/*     */             
/* 729 */             if (existingBytesReceived > 0L) {
/* 730 */               long bytesReceived = MapUtils.getMapLong(map, "bytesReceived", 0L);
/*     */               
/* 732 */               bytesReceived -= existingBytesReceived;
/* 733 */               if (bytesReceived < 0L) {
/* 734 */                 bytesReceived = 0L;
/*     */               }
/* 736 */               map.put("bytesReceived", Long.valueOf(bytesReceived));
/*     */             }
/* 738 */             if (existingBytesSent > 0L) {
/* 739 */               long bytesSent = MapUtils.getMapLong(map, "bytesSent", 0L);
/* 740 */               bytesSent -= existingBytesSent;
/* 741 */               if (bytesSent < 0L) {
/* 742 */                 bytesSent = 0L;
/*     */               }
/* 744 */               map.put("bytesSent", Long.valueOf(bytesSent));
/*     */             }
/* 746 */             if (existingBytesDiscarded > 0L) {
/* 747 */               long bytesDiscarded = MapUtils.getMapLong(map, "bytesDiscarded", 0L);
/*     */               
/* 749 */               bytesDiscarded -= existingBytesDiscarded;
/* 750 */               if (bytesDiscarded < 0L) {
/* 751 */                 bytesDiscarded = 0L;
/*     */               }
/* 753 */               map.put("bytesDiscarded", Long.valueOf(bytesDiscarded));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 760 */       if (this.tv != null) {
/* 761 */         if (needNew) {
/* 762 */           this.tv.addDataSource(stat);
/*     */         } else {
/* 764 */           TableRowCore row = this.tv.getRow(stat);
/* 765 */           if (row != null) {
/* 766 */             row.invalidate();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void peerManagerAdded(PEPeerManager manager) {}
/*     */   
/*     */ 
/*     */   public void peerManagerRemoved(PEPeerManager manager) {}
/*     */   
/*     */   public void peerManagerWillBeAdded(PEPeerManager manager) {}
/*     */   
/*     */   public void peerRemoved(PEPeer peer)
/*     */   {
/* 783 */     synchronized (this.mapData) {
/* 784 */       ClientStatsDataSource stat = (ClientStatsDataSource)this.mapData.get(getID(peer));
/* 785 */       if ((peer.getStats().getTotalDataBytesSent() > 0L) && 
/* 786 */         (stat != null)) {
/* 787 */         stat.current -= 1;
/*     */         
/* 789 */         String network = null;
/* 790 */         if ((peer instanceof PEPeerTransport)) {
/* 791 */           PeerItem identity = ((PEPeerTransport)peer).getPeerItemIdentity();
/* 792 */           if (identity != null) {
/* 793 */             network = identity.getNetwork();
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 798 */         stat.bytesReceived += peer.getStats().getTotalDataBytesReceived();
/* 799 */         stat.bytesSent += peer.getStats().getTotalDataBytesSent();
/* 800 */         stat.bytesDiscarded += peer.getStats().getTotalBytesDiscarded();
/*     */         
/* 802 */         if (network != null) {
/* 803 */           Map<String, Object> map = (Map)stat.perNetworkStats.get(network);
/* 804 */           if (map == null) {
/* 805 */             map = new HashMap();
/* 806 */             stat.perNetworkStats.put(network, map);
/*     */           }
/* 808 */           long bytesReceived = MapUtils.getMapLong(map, "bytesReceived", 0L);
/* 809 */           map.put("bytesReceived", Long.valueOf(bytesReceived + peer.getStats().getTotalDataBytesReceived()));
/*     */           
/* 811 */           long bytesSent = MapUtils.getMapLong(map, "bytesSent", 0L);
/* 812 */           map.put("bytesSent", Long.valueOf(bytesSent + peer.getStats().getTotalDataBytesSent()));
/*     */           
/* 814 */           long bytesDiscarded = MapUtils.getMapLong(map, "bytesDiscarded", 0L);
/* 815 */           map.put("bytesDiscarded", Long.valueOf(bytesDiscarded + peer.getStats().getTotalBytesDiscarded()));
/*     */         }
/*     */         
/*     */ 
/* 819 */         if (this.tv != null) {
/* 820 */           TableRowCore row = this.tv.getRow(stat);
/* 821 */           if (row != null) {
/* 822 */             row.invalidate();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private String getID(PEPeer peer) {
/* 830 */     String s = peer.getClientNameFromPeerID();
/* 831 */     if (s == null) {
/* 832 */       s = peer.getClient();
/* 833 */       if (s.startsWith("HTTP Seed")) {
/* 834 */         return "HTTP Seed";
/*     */       }
/*     */     }
/* 837 */     return s.replaceAll(" v?[0-9._]+", "");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/clientstats/ClientStatsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */