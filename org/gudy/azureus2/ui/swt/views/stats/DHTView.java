/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlActivity;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlListener;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlStats;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDBStats;
/*     */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.PingGraphic;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.SpeedGraphic;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTView
/*     */   implements UISWTViewEventListener
/*     */ {
/*     */   public static final int DHT_TYPE_MAIN = 0;
/*     */   public static final int DHT_TYPE_CVS = 1;
/*     */   public static final int DHT_TYPE_MAIN_V6 = 3;
/*     */   public static final String MSGID_PREFIX = "DHTView";
/*  70 */   public static Color[] rttColours = { Colors.grey, Colors.fadedGreen, Colors.fadedRed };
/*     */   private boolean auto_dht;
/*     */   DHT dht;
/*     */   Composite panel;
/*     */   String yes_str;
/*     */   String no_str;
/*     */   Label lblUpTime;
/*     */   Label lblNumberOfUsers;
/*     */   Label lblNodes;
/*     */   Label lblLeaves;
/*     */   Label lblContacts;
/*     */   Label lblReplacements;
/*     */   Label lblLive;
/*     */   Label lblUnknown;
/*     */   Label lblDying;
/*     */   Label lblSkew;
/*     */   Label lblRendezvous;
/*     */   Label lblReachable;
/*     */   Label lblKeys;
/*     */   Label lblValues;
/*     */   Label lblSize;
/*     */   Label lblLocal;
/*     */   Label lblDirect;
/*     */   Label lblIndirect;
/*  94 */   Label lblDivFreq; Label lblDivSize; Label lblReceivedPackets; Label lblReceivedBytes; Label lblSentPackets; Label lblSentBytes; Label[] lblPings = new Label[4];
/*  95 */   Label[] lblFindNodes = new Label[4];
/*  96 */   Label[] lblFindValues = new Label[4];
/*  97 */   Label[] lblStores = new Label[4];
/*  98 */   Label[] lblData = new Label[4];
/*     */   
/*     */ 
/*     */   Canvas in;
/*     */   
/*     */   Canvas out;
/*     */   Canvas rtt;
/*     */   SpeedGraphic inGraph;
/*     */   SpeedGraphic outGraph;
/*     */   PingGraphic rttGraph;
/*     */   boolean activityChanged;
/*     */   DHTControlListener controlListener;
/*     */   Table activityTable;
/*     */   DHTControlActivity[] activities;
/*     */   private int dht_type;
/*     */   protected AzureusCore core;
/*     */   
/* 115 */   public DHTView() { this(true); }
/*     */   
/*     */   public DHTView(boolean _auto_dht) {
/* 118 */     this.auto_dht = _auto_dht;
/* 119 */     this.inGraph = SpeedGraphic.getInstance();
/* 120 */     this.outGraph = SpeedGraphic.getInstance();
/* 121 */     this.rttGraph = PingGraphic.getInstance();
/*     */     
/* 123 */     this.rttGraph.setColors(rttColours);
/* 124 */     this.rttGraph.setExternalAverage(true);
/*     */   }
/*     */   
/*     */   private void init(AzureusCore core) {
/*     */     try {
/* 129 */       PluginInterface dht_pi = core.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */       
/* 131 */       if (dht_pi == null)
/*     */       {
/* 133 */         return;
/*     */       }
/*     */       
/* 136 */       DHT[] dhts = ((DHTPlugin)dht_pi.getPlugin()).getDHTs();
/*     */       
/* 138 */       for (int i = 0; i < dhts.length; i++) {
/* 139 */         if (dhts[i].getTransport().getNetwork() == this.dht_type) {
/* 140 */           this.dht = dhts[i];
/* 141 */           break;
/*     */         }
/*     */       }
/*     */       
/* 145 */       if (this.dht == null)
/*     */       {
/* 147 */         return;
/*     */       }
/*     */       
/* 150 */       this.controlListener = new DHTControlListener() {
/*     */         public void activityChanged(DHTControlActivity activity, int type) {
/* 152 */           DHTView.this.activityChanged = true;
/*     */         }
/* 154 */       };
/* 155 */       this.dht.getControl().addListener(this.controlListener);
/*     */     }
/*     */     catch (Exception e) {
/* 158 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDHT(DHT _dht)
/*     */   {
/* 166 */     if (this.dht == null)
/*     */     {
/* 168 */       this.dht = _dht;
/*     */       
/* 170 */       this.controlListener = new DHTControlListener() {
/*     */         public void activityChanged(DHTControlActivity activity, int type) {
/* 172 */           DHTView.this.activityChanged = true;
/*     */         }
/* 174 */       };
/* 175 */       this.dht.getControl().addListener(this.controlListener);
/*     */     }
/* 177 */     else if (this.dht != _dht)
/*     */     {
/*     */ 
/*     */ 
/* 181 */       Debug.out("Not Supported ");
/*     */     }
/*     */   }
/*     */   
/*     */   public void initialize(Composite composite) {
/* 186 */     if (this.auto_dht) {
/* 187 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */       {
/*     */         public void azureusCoreRunning(AzureusCore core) {
/* 190 */           DHTView.this.core = core;
/* 191 */           DHTView.this.init(core);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 196 */     this.panel = new Composite(composite, 0);
/* 197 */     GridLayout layout = new GridLayout();
/* 198 */     layout.numColumns = 2;
/* 199 */     this.panel.setLayout(layout);
/*     */     
/* 201 */     this.yes_str = MessageText.getString("Button.yes").replaceAll("&", "");
/* 202 */     this.no_str = MessageText.getString("Button.no").replaceAll("&", "");
/*     */     
/* 204 */     initialiseGeneralGroup();
/* 205 */     initialiseDBGroup();
/*     */     
/* 207 */     initialiseTransportDetailsGroup();
/* 208 */     initialiseOperationDetailsGroup();
/*     */     
/* 210 */     initialiseActivityGroup();
/*     */   }
/*     */   
/*     */   private void initialiseGeneralGroup() {
/* 214 */     Group gGeneral = new Group(this.panel, 0);
/* 215 */     Messages.setLanguageText(gGeneral, "DHTView.general.title");
/*     */     
/* 217 */     GridData data = new GridData();
/* 218 */     data.verticalAlignment = 1;
/* 219 */     data.widthHint = 350;
/* 220 */     Utils.setLayoutData(gGeneral, data);
/*     */     
/* 222 */     GridLayout layout = new GridLayout();
/* 223 */     layout.numColumns = 6;
/* 224 */     gGeneral.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 228 */     Label label = new Label(gGeneral, 0);
/* 229 */     Messages.setLanguageText(label, "DHTView.general.uptime");
/*     */     
/* 231 */     this.lblUpTime = new Label(gGeneral, 0);
/* 232 */     Utils.setLayoutData(this.lblUpTime, new GridData(4, 128, true, false));
/*     */     
/* 234 */     label = new Label(gGeneral, 0);
/* 235 */     Messages.setLanguageText(label, "DHTView.general.users");
/*     */     
/* 237 */     this.lblNumberOfUsers = new Label(gGeneral, 0);
/* 238 */     Utils.setLayoutData(this.lblNumberOfUsers, new GridData(4, 128, true, false));
/*     */     
/* 240 */     label = new Label(gGeneral, 0);
/* 241 */     Messages.setLanguageText(label, "DHTView.general.reachable");
/*     */     
/* 243 */     this.lblReachable = new Label(gGeneral, 0);
/* 244 */     Utils.setLayoutData(this.lblReachable, new GridData(4, 128, true, false));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 249 */     label = new Label(gGeneral, 0);
/* 250 */     Messages.setLanguageText(label, "DHTView.general.nodes");
/*     */     
/* 252 */     this.lblNodes = new Label(gGeneral, 0);
/* 253 */     Utils.setLayoutData(this.lblNodes, new GridData(4, 128, true, false));
/*     */     
/* 255 */     label = new Label(gGeneral, 0);
/* 256 */     Messages.setLanguageText(label, "DHTView.general.leaves");
/*     */     
/* 258 */     this.lblLeaves = new Label(gGeneral, 0);
/* 259 */     Utils.setLayoutData(this.lblLeaves, new GridData(4, 128, true, false));
/*     */     
/* 261 */     label = new Label(gGeneral, 0);
/* 262 */     Messages.setLanguageText(label, "DHTView.general.rendezvous");
/*     */     
/* 264 */     this.lblRendezvous = new Label(gGeneral, 0);
/* 265 */     Utils.setLayoutData(this.lblRendezvous, new GridData(4, 128, true, false));
/*     */     
/*     */ 
/*     */ 
/* 269 */     label = new Label(gGeneral, 0);
/* 270 */     Messages.setLanguageText(label, "DHTView.general.contacts");
/*     */     
/* 272 */     this.lblContacts = new Label(gGeneral, 0);
/* 273 */     Utils.setLayoutData(this.lblContacts, new GridData(4, 128, true, false));
/*     */     
/* 275 */     label = new Label(gGeneral, 0);
/* 276 */     Messages.setLanguageText(label, "DHTView.general.replacements");
/*     */     
/* 278 */     this.lblReplacements = new Label(gGeneral, 0);
/* 279 */     Utils.setLayoutData(this.lblReplacements, new GridData(4, 128, true, false));
/*     */     
/* 281 */     label = new Label(gGeneral, 0);
/* 282 */     Messages.setLanguageText(label, "DHTView.general.live");
/*     */     
/* 284 */     this.lblLive = new Label(gGeneral, 0);
/* 285 */     Utils.setLayoutData(this.lblLive, new GridData(4, 128, true, false));
/*     */     
/*     */ 
/*     */ 
/* 289 */     label = new Label(gGeneral, 0);
/* 290 */     Messages.setLanguageText(label, "DHTView.general.skew");
/*     */     
/* 292 */     this.lblSkew = new Label(gGeneral, 0);
/* 293 */     Utils.setLayoutData(this.lblSkew, new GridData(4, 128, true, false));
/*     */     
/* 295 */     label = new Label(gGeneral, 0);
/* 296 */     Messages.setLanguageText(label, "DHTView.general.unknown");
/*     */     
/* 298 */     this.lblUnknown = new Label(gGeneral, 0);
/* 299 */     Utils.setLayoutData(this.lblUnknown, new GridData(4, 128, true, false));
/*     */     
/* 301 */     label = new Label(gGeneral, 0);
/* 302 */     Messages.setLanguageText(label, "DHTView.general.dying");
/*     */     
/* 304 */     this.lblDying = new Label(gGeneral, 0);
/* 305 */     Utils.setLayoutData(this.lblDying, new GridData(4, 128, true, false));
/*     */   }
/*     */   
/*     */   private void initialiseDBGroup() {
/* 309 */     Group gDB = new Group(this.panel, 0);
/* 310 */     Messages.setLanguageText(gDB, "DHTView.db.title");
/*     */     
/* 312 */     GridData data = new GridData(768);
/* 313 */     data.verticalAlignment = 4;
/* 314 */     Utils.setLayoutData(gDB, data);
/*     */     
/* 316 */     GridLayout layout = new GridLayout();
/* 317 */     layout.numColumns = 6;
/* 318 */     layout.makeColumnsEqualWidth = true;
/* 319 */     gDB.setLayout(layout);
/*     */     
/* 321 */     Label label = new Label(gDB, 0);
/* 322 */     Messages.setLanguageText(label, "DHTView.db.keys");
/*     */     
/* 324 */     this.lblKeys = new Label(gDB, 0);
/* 325 */     Utils.setLayoutData(this.lblKeys, new GridData(4, 128, true, false));
/*     */     
/* 327 */     label = new Label(gDB, 0);
/* 328 */     Messages.setLanguageText(label, "DHTView.db.values");
/*     */     
/* 330 */     this.lblValues = new Label(gDB, 0);
/* 331 */     Utils.setLayoutData(this.lblValues, new GridData(4, 128, true, false));
/*     */     
/* 333 */     label = new Label(gDB, 0);
/* 334 */     Messages.setLanguageText(label, "TableColumn.header.size");
/*     */     
/* 336 */     this.lblSize = new Label(gDB, 0);
/* 337 */     Utils.setLayoutData(this.lblSize, new GridData(4, 128, true, false));
/*     */     
/* 339 */     label = new Label(gDB, 0);
/* 340 */     Messages.setLanguageText(label, "DHTView.db.local");
/*     */     
/* 342 */     this.lblLocal = new Label(gDB, 0);
/* 343 */     Utils.setLayoutData(this.lblLocal, new GridData(4, 128, true, false));
/*     */     
/* 345 */     label = new Label(gDB, 0);
/* 346 */     Messages.setLanguageText(label, "DHTView.db.direct");
/*     */     
/* 348 */     this.lblDirect = new Label(gDB, 0);
/* 349 */     Utils.setLayoutData(this.lblDirect, new GridData(4, 128, true, false));
/*     */     
/* 351 */     label = new Label(gDB, 0);
/* 352 */     Messages.setLanguageText(label, "DHTView.db.indirect");
/*     */     
/* 354 */     this.lblIndirect = new Label(gDB, 0);
/* 355 */     Utils.setLayoutData(this.lblIndirect, new GridData(4, 128, true, false));
/*     */     
/*     */ 
/* 358 */     label = new Label(gDB, 0);
/* 359 */     Messages.setLanguageText(label, "DHTView.db.divfreq");
/*     */     
/* 361 */     this.lblDivFreq = new Label(gDB, 0);
/* 362 */     Utils.setLayoutData(this.lblDivFreq, new GridData(4, 128, true, false));
/*     */     
/* 364 */     label = new Label(gDB, 0);
/* 365 */     Messages.setLanguageText(label, "DHTView.db.divsize");
/*     */     
/* 367 */     this.lblDivSize = new Label(gDB, 0);
/* 368 */     Utils.setLayoutData(this.lblDivSize, new GridData(4, 128, true, false));
/*     */   }
/*     */   
/*     */   private void initialiseTransportDetailsGroup() {
/* 372 */     Group gTransport = new Group(this.panel, 0);
/* 373 */     Messages.setLanguageText(gTransport, "DHTView.transport.title");
/*     */     
/* 375 */     GridData data = new GridData(1040);
/* 376 */     data.widthHint = 350;
/* 377 */     data.verticalSpan = 2;
/* 378 */     Utils.setLayoutData(gTransport, data);
/*     */     
/* 380 */     GridLayout layout = new GridLayout();
/* 381 */     layout.numColumns = 3;
/* 382 */     layout.makeColumnsEqualWidth = true;
/* 383 */     gTransport.setLayout(layout);
/*     */     
/*     */ 
/* 386 */     Label label = new Label(gTransport, 0);
/*     */     
/* 388 */     label = new Label(gTransport, 0);
/* 389 */     Messages.setLanguageText(label, "DHTView.transport.packets");
/* 390 */     Utils.setLayoutData(label, new GridData(4, 128, true, false));
/*     */     
/* 392 */     label = new Label(gTransport, 0);
/* 393 */     Messages.setLanguageText(label, "DHTView.transport.bytes");
/* 394 */     Utils.setLayoutData(label, new GridData(4, 128, true, false));
/*     */     
/* 396 */     label = new Label(gTransport, 0);
/* 397 */     Messages.setLanguageText(label, "DHTView.transport.received");
/*     */     
/* 399 */     this.lblReceivedPackets = new Label(gTransport, 0);
/* 400 */     Utils.setLayoutData(this.lblReceivedPackets, new GridData(4, 128, true, false));
/*     */     
/* 402 */     this.lblReceivedBytes = new Label(gTransport, 0);
/* 403 */     Utils.setLayoutData(this.lblReceivedBytes, new GridData(4, 128, true, false));
/*     */     
/* 405 */     label = new Label(gTransport, 0);
/* 406 */     Messages.setLanguageText(label, "DHTView.transport.sent");
/*     */     
/* 408 */     this.lblSentPackets = new Label(gTransport, 0);
/* 409 */     Utils.setLayoutData(this.lblSentPackets, new GridData(4, 128, true, false));
/*     */     
/* 411 */     this.lblSentBytes = new Label(gTransport, 0);
/* 412 */     Utils.setLayoutData(this.lblSentBytes, new GridData(4, 128, true, false));
/*     */     
/* 414 */     label = new Label(gTransport, 0);
/* 415 */     Messages.setLanguageText(label, "DHTView.transport.in");
/* 416 */     data = new GridData();
/* 417 */     data.horizontalSpan = 3;
/* 418 */     Utils.setLayoutData(label, data);
/*     */     
/* 420 */     this.in = new Canvas(gTransport, 262144);
/* 421 */     data = new GridData(1808);
/* 422 */     data.horizontalSpan = 3;
/* 423 */     Utils.setLayoutData(this.in, data);
/* 424 */     this.inGraph.initialize(this.in);
/*     */     
/* 426 */     label = new Label(gTransport, 0);
/* 427 */     Messages.setLanguageText(label, "DHTView.transport.out");
/* 428 */     data = new GridData();
/* 429 */     data.horizontalSpan = 3;
/* 430 */     Utils.setLayoutData(label, data);
/*     */     
/* 432 */     this.out = new Canvas(gTransport, 262144);
/* 433 */     data = new GridData(1808);
/* 434 */     data.horizontalSpan = 3;
/* 435 */     Utils.setLayoutData(this.out, data);
/* 436 */     this.outGraph.initialize(this.out);
/*     */     
/* 438 */     label = new Label(gTransport, 0);
/* 439 */     Messages.setLanguageText(label, "DHTView.transport.rtt");
/* 440 */     data = new GridData();
/* 441 */     data.horizontalSpan = 3;
/* 442 */     Utils.setLayoutData(label, data);
/*     */     
/* 444 */     this.rtt = new Canvas(gTransport, 262144);
/* 445 */     data = new GridData(1808);
/* 446 */     data.horizontalSpan = 3;
/* 447 */     Utils.setLayoutData(this.rtt, data);
/* 448 */     this.rttGraph.initialize(this.rtt);
/*     */     
/* 450 */     data = new GridData(768);
/* 451 */     data.horizontalSpan = 3;
/*     */     
/* 453 */     Legend.createLegendComposite(gTransport, rttColours, new String[] { "DHTView.rtt.legend.average", "DHTView.rtt.legend.best", "DHTView.rtt.legend.worst" }, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initialiseOperationDetailsGroup()
/*     */   {
/* 464 */     Group gOperations = new Group(this.panel, 0);
/* 465 */     Messages.setLanguageText(gOperations, "DHTView.operations.title");
/* 466 */     Utils.setLayoutData(gOperations, new GridData(4, 1, true, false));
/*     */     
/* 468 */     GridLayout layout = new GridLayout();
/* 469 */     layout.numColumns = 5;
/* 470 */     layout.makeColumnsEqualWidth = true;
/* 471 */     gOperations.setLayout(layout);
/*     */     
/*     */ 
/* 474 */     Label label = new Label(gOperations, 0);
/*     */     
/* 476 */     label = new Label(gOperations, 0);
/* 477 */     Messages.setLanguageText(label, "DHTView.operations.sent");
/* 478 */     Utils.setLayoutData(label, new GridData(4, 128, true, false));
/*     */     
/* 480 */     label = new Label(gOperations, 0);
/* 481 */     Messages.setLanguageText(label, "DHTView.operations.ok");
/* 482 */     Utils.setLayoutData(label, new GridData(4, 128, true, false));
/*     */     
/* 484 */     label = new Label(gOperations, 0);
/* 485 */     Messages.setLanguageText(label, "DHTView.operations.failed");
/* 486 */     Utils.setLayoutData(label, new GridData(4, 128, true, false));
/*     */     
/* 488 */     label = new Label(gOperations, 0);
/* 489 */     Messages.setLanguageText(label, "DHTView.operations.received");
/* 490 */     Utils.setLayoutData(label, new GridData(4, 128, true, false));
/*     */     
/*     */ 
/* 493 */     label = new Label(gOperations, 0);
/* 494 */     Messages.setLanguageText(label, "DHTView.operations.ping");
/*     */     
/* 496 */     for (int i = 0; i < 4; i++) {
/* 497 */       this.lblPings[i] = new Label(gOperations, 0);
/* 498 */       Utils.setLayoutData(this.lblPings[i], new GridData(4, 128, true, false));
/*     */     }
/*     */     
/*     */ 
/* 502 */     label = new Label(gOperations, 0);
/* 503 */     Messages.setLanguageText(label, "DHTView.operations.findNode");
/*     */     
/* 505 */     for (int i = 0; i < 4; i++) {
/* 506 */       this.lblFindNodes[i] = new Label(gOperations, 0);
/* 507 */       Utils.setLayoutData(this.lblFindNodes[i], new GridData(4, 128, true, false));
/*     */     }
/*     */     
/*     */ 
/* 511 */     label = new Label(gOperations, 0);
/* 512 */     Messages.setLanguageText(label, "DHTView.operations.findValue");
/*     */     
/* 514 */     for (int i = 0; i < 4; i++) {
/* 515 */       this.lblFindValues[i] = new Label(gOperations, 0);
/* 516 */       Utils.setLayoutData(this.lblFindValues[i], new GridData(4, 128, true, false));
/*     */     }
/*     */     
/*     */ 
/* 520 */     label = new Label(gOperations, 0);
/* 521 */     Messages.setLanguageText(label, "DHTView.operations.store");
/*     */     
/* 523 */     for (int i = 0; i < 4; i++) {
/* 524 */       this.lblStores[i] = new Label(gOperations, 0);
/* 525 */       Utils.setLayoutData(this.lblStores[i], new GridData(4, 128, true, false));
/*     */     }
/*     */     
/* 528 */     label = new Label(gOperations, 0);
/* 529 */     Messages.setLanguageText(label, "DHTView.operations.data");
/*     */     
/* 531 */     for (int i = 0; i < 4; i++) {
/* 532 */       this.lblData[i] = new Label(gOperations, 0);
/* 533 */       Utils.setLayoutData(this.lblData[i], new GridData(4, 128, true, false));
/*     */     }
/*     */   }
/*     */   
/*     */   private void initialiseActivityGroup() {
/* 538 */     Group gActivity = new Group(this.panel, 0);
/* 539 */     Messages.setLanguageText(gActivity, "DHTView.activity.title");
/* 540 */     Utils.setLayoutData(gActivity, new GridData(4, 4, true, true));
/* 541 */     gActivity.setLayout(new GridLayout());
/*     */     
/* 543 */     this.activityTable = new Table(gActivity, 268503044);
/* 544 */     Utils.setLayoutData(this.activityTable, new GridData(1808));
/*     */     
/* 546 */     final TableColumn colStatus = new TableColumn(this.activityTable, 16384);
/* 547 */     Messages.setLanguageText(colStatus, "DHTView.activity.status");
/* 548 */     colStatus.setWidth(Utils.adjustPXForDPI(80));
/*     */     
/* 550 */     final TableColumn colType = new TableColumn(this.activityTable, 16384);
/* 551 */     Messages.setLanguageText(colType, "DHTView.activity.type");
/* 552 */     colType.setWidth(Utils.adjustPXForDPI(80));
/*     */     
/* 554 */     final TableColumn colName = new TableColumn(this.activityTable, 16384);
/* 555 */     Messages.setLanguageText(colName, "DHTView.activity.target");
/* 556 */     colName.setWidth(Utils.adjustPXForDPI(80));
/*     */     
/* 558 */     final TableColumn colDetails = new TableColumn(this.activityTable, 16384);
/* 559 */     Messages.setLanguageText(colDetails, "DHTView.activity.details");
/* 560 */     colDetails.setWidth(Utils.adjustPXForDPI(300));
/* 561 */     colDetails.setResizable(false);
/*     */     
/*     */ 
/* 564 */     this.activityTable.setHeaderVisible(true);
/* 565 */     Listener computeLastRowWidthListener = new Listener()
/*     */     {
/*     */ 
/* 568 */       boolean inUse = false;
/*     */       
/* 570 */       public void handleEvent(Event event) { if (this.inUse) {
/* 571 */           return;
/*     */         }
/*     */         
/* 574 */         this.inUse = true;
/*     */         try {
/* 576 */           if ((DHTView.this.activityTable == null) || (DHTView.this.activityTable.isDisposed())) return;
/* 577 */           int totalWidth = DHTView.this.activityTable.getClientArea().width;
/* 578 */           int remainingWidth = totalWidth - colStatus.getWidth() - colType.getWidth() - colName.getWidth();
/*     */           
/*     */ 
/*     */ 
/* 582 */           if (remainingWidth > 0) {
/* 583 */             colDetails.setWidth(remainingWidth);
/*     */           }
/*     */         } finally {
/* 586 */           this.inUse = false;
/*     */         }
/*     */       }
/* 589 */     };
/* 590 */     this.activityTable.addListener(11, computeLastRowWidthListener);
/* 591 */     colStatus.addListener(11, computeLastRowWidthListener);
/* 592 */     colType.addListener(11, computeLastRowWidthListener);
/* 593 */     colName.addListener(11, computeLastRowWidthListener);
/*     */     
/* 595 */     this.activityTable.addListener(36, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 597 */         TableItem item = (TableItem)event.item;
/* 598 */         int index = DHTView.this.activityTable.indexOf(item);
/* 599 */         item.setText(0, MessageText.getString("DHTView.activity.status." + DHTView.this.activities[index].isQueued()));
/* 600 */         item.setText(1, MessageText.getString("DHTView.activity.type." + DHTView.this.activities[index].getType()));
/* 601 */         item.setText(2, ByteFormatter.nicePrint(DHTView.this.activities[index].getTarget()));
/* 602 */         item.setText(3, DHTView.this.activities[index].getDescription());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 610 */     Utils.disposeComposite(this.panel);
/* 611 */     if (this.dht != null) {
/* 612 */       this.dht.getControl().removeListener(this.controlListener);
/*     */     }
/* 614 */     this.outGraph.dispose();
/* 615 */     this.inGraph.dispose();
/* 616 */     this.rttGraph.dispose();
/*     */   }
/*     */   
/*     */   private String getTitleID() {
/* 620 */     if (this.dht_type == 0)
/*     */     {
/* 622 */       return "DHTView.title.full";
/*     */     }
/* 624 */     if (this.dht_type == 1)
/*     */     {
/* 626 */       return "DHTView.title.fullcvs";
/*     */     }
/*     */     
/* 629 */     return "DHTView.title.full_v6";
/*     */   }
/*     */   
/*     */   private Composite getComposite()
/*     */   {
/* 634 */     return this.panel;
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 639 */     this.inGraph.refresh(false);
/* 640 */     this.outGraph.refresh(false);
/* 641 */     this.rttGraph.refresh();
/*     */     
/* 643 */     if (this.dht == null) {
/* 644 */       if (this.core != null)
/*     */       {
/* 646 */         init(this.core);
/*     */       }
/* 648 */       return;
/*     */     }
/*     */     
/* 651 */     refreshGeneral();
/* 652 */     refreshDB();
/* 653 */     refreshTransportDetails();
/* 654 */     refreshOperationDetails();
/* 655 */     refreshActivity();
/*     */   }
/*     */   
/*     */   private void refreshGeneral() {
/* 659 */     DHTControlStats controlStats = this.dht.getControl().getStats();
/* 660 */     DHTRouterStats routerStats = this.dht.getRouter().getStats();
/* 661 */     DHTTransport transport = this.dht.getTransport();
/* 662 */     DHTTransportStats transportStats = transport.getStats();
/* 663 */     this.lblUpTime.setText(TimeFormatter.format(controlStats.getRouterUptime() / 1000L));
/* 664 */     this.lblNumberOfUsers.setText("" + controlStats.getEstimatedDHTSize());
/* 665 */     int percent = transportStats.getRouteablePercentage();
/* 666 */     this.lblReachable.setText((transport.isReachable() ? this.yes_str : this.no_str) + (percent == -1 ? "" : new StringBuilder().append(" ").append(percent).append("%").toString()));
/*     */     
/* 668 */     DHTNATPuncher puncher = this.dht.getNATPuncher();
/*     */     
/*     */     String puncher_str;
/*     */     String puncher_str;
/* 672 */     if (puncher == null) {
/* 673 */       puncher_str = "";
/*     */     } else {
/* 675 */       puncher_str = puncher.operational() ? this.yes_str : this.no_str;
/*     */     }
/*     */     
/* 678 */     this.lblRendezvous.setText(transport.isReachable() ? "" : puncher_str);
/* 679 */     long[] stats = routerStats.getStats();
/* 680 */     this.lblNodes.setText("" + stats[0]);
/* 681 */     this.lblLeaves.setText("" + stats[1]);
/* 682 */     this.lblContacts.setText("" + stats[2]);
/* 683 */     this.lblReplacements.setText("" + stats[3]);
/* 684 */     this.lblLive.setText("" + stats[4]);
/* 685 */     this.lblUnknown.setText("" + stats[5]);
/* 686 */     this.lblDying.setText("" + stats[6]);
/*     */     
/* 688 */     long skew_average = transportStats.getSkewAverage();
/*     */     
/* 690 */     this.lblSkew.setText((skew_average < 0L ? "-" : "") + TimeFormatter.format100ths(Math.abs(skew_average)));
/*     */   }
/*     */   
/* 693 */   private int refreshIter = 0;
/*     */   private UISWTView swtView;
/*     */   
/*     */   private void refreshDB() {
/* 697 */     if (this.refreshIter == 0) {
/* 698 */       DHTDBStats dbStats = this.dht.getDataBase().getStats();
/* 699 */       this.lblKeys.setText("" + dbStats.getKeyCount() + " (" + dbStats.getLocalKeyCount() + ")");
/* 700 */       int[] stats = dbStats.getValueDetails();
/* 701 */       this.lblValues.setText("" + stats[0]);
/* 702 */       this.lblSize.setText(DisplayFormatters.formatByteCountToKiBEtc(dbStats.getSize()));
/* 703 */       this.lblDirect.setText(DisplayFormatters.formatByteCountToKiBEtc(stats[2]));
/* 704 */       this.lblIndirect.setText(DisplayFormatters.formatByteCountToKiBEtc(stats[3]));
/* 705 */       this.lblLocal.setText(DisplayFormatters.formatByteCountToKiBEtc(stats[1]));
/*     */       
/* 707 */       DHTStorageAdapter sa = this.dht.getStorageAdapter();
/*     */       
/*     */       String rem_size;
/*     */       String rem_freq;
/*     */       String rem_size;
/* 712 */       if (sa == null) {
/* 713 */         String rem_freq = "-";
/* 714 */         rem_size = "-";
/*     */       } else {
/* 716 */         rem_freq = "" + sa.getRemoteFreqDivCount();
/* 717 */         rem_size = "" + sa.getRemoteSizeDivCount();
/*     */       }
/*     */       
/* 720 */       this.lblDivFreq.setText("" + stats[4] + " (" + rem_freq + ")");
/* 721 */       this.lblDivSize.setText("" + stats[5] + " (" + rem_size + ")");
/*     */     } else {
/* 723 */       this.refreshIter += 1;
/* 724 */       if (this.refreshIter == 100) this.refreshIter = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   private void refreshTransportDetails()
/*     */   {
/* 730 */     DHTTransportStats transportStats = this.dht.getTransport().getStats();
/* 731 */     this.lblReceivedBytes.setText(DisplayFormatters.formatByteCountToKiBEtc(transportStats.getBytesReceived()));
/* 732 */     this.lblSentBytes.setText(DisplayFormatters.formatByteCountToKiBEtc(transportStats.getBytesSent()));
/* 733 */     this.lblReceivedPackets.setText("" + transportStats.getPacketsReceived());
/* 734 */     this.lblSentPackets.setText("" + transportStats.getPacketsSent());
/*     */   }
/*     */   
/*     */   private void refreshOperationDetails() {
/* 738 */     DHTTransportStats transportStats = this.dht.getTransport().getStats();
/* 739 */     long[] pings = transportStats.getPings();
/* 740 */     for (int i = 0; i < 4; i++) {
/* 741 */       this.lblPings[i].setText("" + pings[i]);
/*     */     }
/*     */     
/* 744 */     long[] findNodes = transportStats.getFindNodes();
/* 745 */     for (int i = 0; i < 4; i++) {
/* 746 */       this.lblFindNodes[i].setText("" + findNodes[i]);
/*     */     }
/*     */     
/* 749 */     long[] findValues = transportStats.getFindValues();
/* 750 */     for (int i = 0; i < 4; i++) {
/* 751 */       this.lblFindValues[i].setText("" + findValues[i]);
/*     */     }
/*     */     
/* 754 */     long[] stores = transportStats.getStores();
/* 755 */     long[] qstores = transportStats.getQueryStores();
/*     */     
/* 757 */     for (int i = 0; i < 4; i++) {
/* 758 */       this.lblStores[i].setText("" + stores[i] + " (" + qstores[i] + ")");
/*     */     }
/* 760 */     long[] data = transportStats.getData();
/* 761 */     for (int i = 0; i < 4; i++) {
/* 762 */       this.lblData[i].setText("" + data[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   private void refreshActivity() {
/* 767 */     if (this.activityChanged) {
/* 768 */       this.activityChanged = false;
/* 769 */       this.activities = this.dht.getControl().getActivities();
/* 770 */       this.activityTable.setItemCount(this.activities.length);
/* 771 */       this.activityTable.clearAll();
/*     */       
/* 773 */       this.activityTable.redraw();
/*     */     }
/*     */   }
/*     */   
/*     */   public void periodicUpdate() {
/* 778 */     if (this.dht == null) { return;
/*     */     }
/* 780 */     DHTTransportFullStats fullStats = this.dht.getTransport().getLocalContact().getStats();
/* 781 */     if (fullStats != null) {
/* 782 */       this.inGraph.addIntValue((int)fullStats.getAverageBytesReceived());
/* 783 */       this.outGraph.addIntValue((int)fullStats.getAverageBytesSent());
/*     */     }
/* 785 */     DHTTransportStats stats = this.dht.getTransport().getStats();
/* 786 */     int[] rtts = (int[])stats.getRTTHistory().clone();
/*     */     
/* 788 */     Arrays.sort(rtts);
/*     */     
/* 790 */     int rtt_total = 0;
/* 791 */     int rtt_num = 0;
/*     */     
/* 793 */     int start = 0;
/*     */     
/* 795 */     for (int rtt : rtts)
/*     */     {
/* 797 */       if (rtt > 0) {
/* 798 */         rtt_total += rtt;
/* 799 */         rtt_num++;
/*     */       } else {
/* 801 */         start++;
/*     */       }
/*     */     }
/*     */     
/* 805 */     int average = 0;
/* 806 */     int best = 0;
/* 807 */     int worst = 0;
/*     */     
/*     */ 
/* 810 */     if (rtt_num > 0) {
/* 811 */       average = rtt_total / rtt_num;
/*     */     }
/*     */     
/* 814 */     int chunk = rtt_num / 3;
/*     */     
/* 816 */     int max_best = start + chunk;
/* 817 */     int min_worst = rtts.length - 1 - chunk;
/*     */     
/* 819 */     int worst_total = 0;
/* 820 */     int worst_num = 0;
/*     */     
/* 822 */     int best_total = 0;
/* 823 */     int best_num = 0;
/*     */     
/* 825 */     for (int i = start; i < rtts.length; i++)
/*     */     {
/* 827 */       if (i < max_best) {
/* 828 */         best_total += rtts[i];
/* 829 */         best_num++;
/* 830 */       } else if (i > min_worst) {
/* 831 */         worst_total += rtts[i];
/* 832 */         worst_num++;
/*     */       }
/*     */     }
/*     */     
/* 836 */     if (best_num > 0) {
/* 837 */       best = best_total / best_num;
/*     */     }
/*     */     
/* 840 */     if (worst_num > 0) {
/* 841 */       worst = worst_total / worst_num;
/*     */     }
/*     */     
/* 844 */     this.rttGraph.addIntsValue(new int[] { average, best, worst });
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 848 */     switch (event.getType()) {
/*     */     case 0: 
/* 850 */       this.swtView = ((UISWTView)event.getData());
/* 851 */       this.swtView.setTitle(MessageText.getString(getTitleID()));
/* 852 */       break;
/*     */     
/*     */     case 7: 
/* 855 */       delete();
/* 856 */       break;
/*     */     
/*     */     case 2: 
/* 859 */       initialize((Composite)event.getData());
/* 860 */       break;
/*     */     
/*     */     case 6: 
/* 863 */       Messages.updateLanguageForControl(getComposite());
/* 864 */       if (this.swtView != null) {
/* 865 */         this.swtView.setTitle(MessageText.getString(getTitleID()));
/*     */       }
/*     */       
/*     */       break;
/*     */     case 1: 
/* 870 */       if ((event.getData() instanceof Number)) {
/* 871 */         this.dht_type = ((Number)event.getData()).intValue();
/* 872 */         if (this.swtView != null) {
/* 873 */           this.swtView.setTitle(MessageText.getString(getTitleID()));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     case 3: 
/*     */       break;
/*     */     case 5: 
/* 882 */       refresh();
/* 883 */       break;
/*     */     
/*     */     case 256: 
/* 886 */       periodicUpdate();
/*     */     }
/*     */     
/*     */     
/* 890 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/DHTView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */