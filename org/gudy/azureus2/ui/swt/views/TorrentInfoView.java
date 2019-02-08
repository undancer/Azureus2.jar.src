/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ScrollBar;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeComplete;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTypeIncomplete;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.FakeTableCell;
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
/*     */ public class TorrentInfoView
/*     */   implements UISWTViewCoreEventListener, UIPluginViewToolBarListener
/*     */ {
/*     */   public static final String MSGID_PREFIX = "TorrentInfoView";
/*     */   private DownloadManager download_manager;
/*     */   private Composite outer_panel;
/*     */   private Font headerFont;
/*     */   private FakeTableCell[] cells;
/*     */   private ScrolledComposite sc;
/*     */   private Composite parent;
/*     */   private UISWTView swtView;
/*     */   
/*     */   private void initialize(Composite composite)
/*     */   {
/*  86 */     this.parent = composite;
/*     */     
/*  88 */     if (this.download_manager == null) {
/*  89 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  95 */     if ((this.sc != null) && (!this.sc.isDisposed())) {
/*  96 */       this.sc.dispose();
/*     */     }
/*     */     
/*  99 */     this.sc = new ScrolledComposite(composite, 768);
/* 100 */     this.sc.getVerticalBar().setIncrement(16);
/* 101 */     this.sc.setExpandHorizontal(true);
/* 102 */     this.sc.setExpandVertical(true);
/* 103 */     GridData gridData = new GridData(4, 4, true, true, 1, 1);
/* 104 */     Utils.setLayoutData(this.sc, gridData);
/*     */     
/* 106 */     this.outer_panel = this.sc;
/*     */     
/* 108 */     Composite panel = new Composite(this.sc, 0);
/*     */     
/* 110 */     this.sc.setContent(panel);
/*     */     
/*     */ 
/*     */ 
/* 114 */     GridLayout layout = new GridLayout();
/* 115 */     layout.marginHeight = 0;
/* 116 */     layout.marginWidth = 0;
/* 117 */     layout.numColumns = 1;
/* 118 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 124 */     Composite cHeader = new Composite(panel, 2048);
/* 125 */     GridLayout configLayout = new GridLayout();
/* 126 */     configLayout.marginHeight = 3;
/* 127 */     configLayout.marginWidth = 0;
/* 128 */     cHeader.setLayout(configLayout);
/* 129 */     gridData = new GridData(772);
/* 130 */     Utils.setLayoutData(cHeader, gridData);
/*     */     
/* 132 */     Display d = panel.getDisplay();
/* 133 */     cHeader.setBackground(d.getSystemColor(26));
/* 134 */     cHeader.setForeground(d.getSystemColor(27));
/*     */     
/* 136 */     Label lHeader = new Label(cHeader, 0);
/* 137 */     lHeader.setBackground(d.getSystemColor(26));
/* 138 */     lHeader.setForeground(d.getSystemColor(27));
/* 139 */     FontData[] fontData = lHeader.getFont().getFontData();
/* 140 */     fontData[0].setStyle(1);
/* 141 */     int fontHeight = (int)(fontData[0].getHeight() * 1.2D);
/* 142 */     fontData[0].setHeight(fontHeight);
/* 143 */     this.headerFont = new Font(d, fontData);
/* 144 */     lHeader.setFont(this.headerFont);
/* 145 */     lHeader.setText(" " + MessageText.getString("authenticator.torrent") + " : " + this.download_manager.getDisplayName().replaceAll("&", "&&"));
/* 146 */     gridData = new GridData(772);
/* 147 */     Utils.setLayoutData(lHeader, gridData);
/*     */     
/* 149 */     Composite gTorrentInfo = new Composite(panel, 0);
/* 150 */     gridData = new GridData(272);
/* 151 */     Utils.setLayoutData(gTorrentInfo, gridData);
/* 152 */     layout = new GridLayout();
/* 153 */     layout.numColumns = 2;
/* 154 */     gTorrentInfo.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 159 */     Label label = new Label(gTorrentInfo, 0);
/* 160 */     gridData = new GridData();
/* 161 */     Utils.setLayoutData(label, gridData);
/* 162 */     label.setText(MessageText.getString("TorrentInfoView.torrent.encoding") + ": ");
/*     */     
/* 164 */     TOTorrent torrent = this.download_manager.getTorrent();
/* 165 */     BufferedLabel blabel = new BufferedLabel(gTorrentInfo, 0);
/* 166 */     gridData = new GridData();
/*     */     
/* 168 */     Utils.setLayoutData(blabel, gridData);
/* 169 */     blabel.setText(torrent == null ? "" : LocaleTorrentUtil.getCurrentTorrentEncoding(torrent));
/*     */     
/*     */ 
/*     */ 
/* 173 */     label = new Label(gTorrentInfo, 0);
/* 174 */     gridData = new GridData();
/* 175 */     Utils.setLayoutData(label, gridData);
/* 176 */     label.setText(MessageText.getString("MyTrackerView.tracker") + ": ");
/*     */     
/* 178 */     String trackers = "";
/*     */     
/* 180 */     if (torrent != null)
/*     */     {
/* 182 */       TOTorrentAnnounceURLGroup group = torrent.getAnnounceURLGroup();
/*     */       
/* 184 */       TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*     */       
/* 186 */       List<String> tracker_list = new ArrayList();
/*     */       
/* 188 */       URL url = torrent.getAnnounceURL();
/*     */       
/* 190 */       tracker_list.add(url.getHost() + (url.getPort() == -1 ? "" : new StringBuilder().append(":").append(url.getPort()).toString()));
/*     */       
/* 192 */       for (int i = 0; i < sets.length; i++)
/*     */       {
/* 194 */         TOTorrentAnnounceURLSet set = sets[i];
/*     */         
/* 196 */         URL[] urls = set.getAnnounceURLs();
/*     */         
/* 198 */         for (int j = 0; j < urls.length; j++)
/*     */         {
/* 200 */           url = urls[j];
/*     */           
/* 202 */           String str = url.getHost() + (url.getPort() == -1 ? "" : new StringBuilder().append(":").append(url.getPort()).toString());
/*     */           
/* 204 */           if (!tracker_list.contains(str))
/*     */           {
/* 206 */             tracker_list.add(str);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 211 */       TRTrackerAnnouncer announcer = this.download_manager.getTrackerClient();
/*     */       
/* 213 */       URL active_url = null;
/*     */       
/* 215 */       if (announcer != null)
/*     */       {
/* 217 */         active_url = announcer.getTrackerURL();
/*     */       }
/*     */       else
/*     */       {
/* 221 */         TRTrackerScraperResponse scrape = this.download_manager.getTrackerScrapeResponse();
/*     */         
/* 223 */         if (scrape != null)
/*     */         {
/* 225 */           active_url = scrape.getURL();
/*     */         }
/*     */       }
/*     */       
/* 229 */       if (active_url == null)
/*     */       {
/* 231 */         active_url = torrent.getAnnounceURL();
/*     */       }
/*     */       
/* 234 */       trackers = active_url.getHost() + (active_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(active_url.getPort()).toString());
/*     */       
/* 236 */       tracker_list.remove(trackers);
/*     */       
/* 238 */       if (tracker_list.size() > 0)
/*     */       {
/* 240 */         trackers = trackers + " (";
/*     */         
/* 242 */         for (int i = 0; i < tracker_list.size(); i++)
/*     */         {
/* 244 */           trackers = trackers + (i == 0 ? "" : ", ") + (String)tracker_list.get(i);
/*     */         }
/*     */         
/* 247 */         trackers = trackers + ")";
/*     */       }
/*     */     }
/*     */     
/* 251 */     blabel = new BufferedLabel(gTorrentInfo, 64);
/* 252 */     Utils.setLayoutData(blabel, Utils.getWrappableLabelGridData(1, 768));
/* 253 */     blabel.setText(trackers);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 258 */     Group gColumns = new Group(panel, 0);
/* 259 */     Messages.setLanguageText(gColumns, "TorrentInfoView.columns");
/* 260 */     gridData = new GridData(1808);
/* 261 */     Utils.setLayoutData(gColumns, gridData);
/* 262 */     layout = new GridLayout();
/* 263 */     layout.numColumns = 4;
/* 264 */     gColumns.setLayout(layout);
/*     */     
/* 266 */     Map<String, FakeTableCell> usable_cols = new HashMap();
/*     */     
/* 268 */     TableColumnManager col_man = TableColumnManager.getInstance();
/*     */     
/* 270 */     TableColumnCore[][] cols_sets = { col_man.getAllTableColumnCoreAsArray(DownloadTypeIncomplete.class, "MyTorrents"), col_man.getAllTableColumnCoreAsArray(DownloadTypeComplete.class, "MySeeders") };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 277 */     for (int i = 0; i < cols_sets.length; i++)
/*     */     {
/* 279 */       TableColumnCore[] cols = cols_sets[i];
/*     */       
/* 281 */       for (int j = 0; j < cols.length; j++)
/*     */       {
/* 283 */         TableColumnCore col = cols[j];
/*     */         
/* 285 */         String id = col.getName();
/*     */         
/* 287 */         if (!usable_cols.containsKey(id))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 292 */           FakeTableCell fakeTableCell = null;
/*     */           try {
/* 294 */             fakeTableCell = new FakeTableCell(col, this.download_manager);
/* 295 */             fakeTableCell.setOrentation(16384);
/* 296 */             fakeTableCell.setWrapText(false);
/* 297 */             col.invokeCellAddedListeners(fakeTableCell);
/*     */             
/* 299 */             fakeTableCell.refresh();
/* 300 */             usable_cols.put(id, fakeTableCell);
/*     */           }
/*     */           catch (Throwable t) {
/*     */             try {
/* 304 */               if (fakeTableCell != null) {
/* 305 */                 fakeTableCell.dispose();
/*     */               }
/*     */             }
/*     */             catch (Throwable t2) {}
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 314 */     Collection<FakeTableCell> values = usable_cols.values();
/*     */     
/* 316 */     this.cells = new FakeTableCell[values.size()];
/*     */     
/* 318 */     values.toArray(this.cells);
/*     */     
/* 320 */     Arrays.sort(this.cells, new Comparator()
/*     */     {
/*     */ 
/*     */       public int compare(FakeTableCell o1, FakeTableCell o2)
/*     */       {
/* 325 */         TableColumnCore c1 = (TableColumnCore)o1.getTableColumn();
/* 326 */         TableColumnCore c2 = (TableColumnCore)o2.getTableColumn();
/*     */         
/* 328 */         String key1 = MessageText.getString(c1.getTitleLanguageKey());
/* 329 */         String key2 = MessageText.getString(c2.getTitleLanguageKey());
/*     */         
/* 331 */         return key1.compareToIgnoreCase(key2);
/*     */       }
/*     */     });
/*     */     
/* 335 */     for (int i = 0; i < this.cells.length; i++)
/*     */     {
/* 337 */       final FakeTableCell cell = this.cells[i];
/*     */       
/* 339 */       label = new Label(gColumns, 0);
/* 340 */       gridData = new GridData();
/* 341 */       if (i % 2 == 1) {
/* 342 */         gridData.horizontalIndent = 16;
/*     */       }
/* 344 */       Utils.setLayoutData(label, gridData);
/* 345 */       String key = ((TableColumnCore)cell.getTableColumn()).getTitleLanguageKey();
/* 346 */       label.setText(MessageText.getString(key) + ": ");
/* 347 */       label.setToolTipText(MessageText.getString(key + ".info", ""));
/*     */       
/* 349 */       final Composite c = new Composite(gColumns, 536870912);
/* 350 */       gridData = new GridData(768);
/* 351 */       gridData.heightHint = 16;
/* 352 */       Utils.setLayoutData(c, gridData);
/* 353 */       cell.setControl(c);
/* 354 */       cell.invalidate();
/* 355 */       cell.refresh();
/* 356 */       c.addListener(32, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 358 */           Object toolTip = cell.getToolTip();
/* 359 */           if ((toolTip instanceof String)) {
/* 360 */             String s = (String)toolTip;
/* 361 */             c.setToolTipText(s);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 367 */     refresh();
/*     */     
/* 369 */     this.sc.setMinSize(panel.computeSize(-1, -1));
/*     */   }
/*     */   
/*     */ 
/*     */   private void refresh()
/*     */   {
/* 375 */     if (this.cells != null)
/*     */     {
/* 377 */       for (int i = 0; i < this.cells.length; i++)
/*     */       {
/* 379 */         TableCellCore cell = this.cells[i];
/* 380 */         try { cell.refresh();
/* 381 */         } catch (Exception e) { Debug.printStackTrace(e, "Error refreshing cell: " + this.cells[i].getTableColumn().getName());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 390 */     return this.outer_panel;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFullTitle()
/*     */   {
/* 396 */     return MessageText.getString("TorrentInfoView.title.full");
/*     */   }
/*     */   
/*     */ 
/*     */   private void delete()
/*     */   {
/* 402 */     if (this.headerFont != null)
/*     */     {
/* 404 */       this.headerFont.dispose();
/*     */     }
/*     */     
/* 407 */     if (this.cells != null)
/*     */     {
/* 409 */       for (int i = 0; i < this.cells.length; i++)
/*     */       {
/* 411 */         TableCellCore cell = this.cells[i];
/*     */         
/* 413 */         cell.dispose();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void dataSourceChanged(Object newDataSource) {
/* 419 */     if ((newDataSource instanceof DownloadManager)) {
/* 420 */       this.download_manager = ((DownloadManager)newDataSource);
/*     */     }
/*     */     
/* 423 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 425 */         if ((TorrentInfoView.this.parent != null) && (!TorrentInfoView.this.parent.isDisposed())) {
/* 426 */           TorrentInfoView.this.initialize(TorrentInfoView.this.parent);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 433 */     switch (event.getType()) {
/*     */     case 0: 
/* 435 */       this.swtView = ((UISWTView)event.getData());
/* 436 */       this.swtView.setTitle(getFullTitle());
/* 437 */       this.swtView.setToolBarListener(this);
/* 438 */       break;
/*     */     
/*     */     case 7: 
/* 441 */       delete();
/* 442 */       break;
/*     */     
/*     */     case 2: 
/* 445 */       initialize((Composite)event.getData());
/* 446 */       break;
/*     */     
/*     */     case 6: 
/* 449 */       Messages.updateLanguageForControl(getComposite());
/* 450 */       this.swtView.setTitle(getFullTitle());
/* 451 */       break;
/*     */     
/*     */     case 1: 
/* 454 */       dataSourceChanged(event.getData());
/* 455 */       break;
/*     */     
/*     */     case 3: 
/* 458 */       String id = "DMDetails_Info";
/* 459 */       if (this.download_manager != null) {
/* 460 */         if (this.download_manager.getTorrent() != null) {
/* 461 */           id = id + "." + this.download_manager.getInternalName();
/*     */         } else {
/* 463 */           id = id + ":" + this.download_manager.getSize();
/*     */         }
/* 465 */         SelectedContentManager.changeCurrentlySelectedContent(id, new SelectedContent[] { new SelectedContent(this.download_manager) });
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 470 */         SelectedContentManager.changeCurrentlySelectedContent(id, null);
/*     */       }
/* 472 */       break;
/*     */     
/*     */     case 4: 
/* 475 */       SelectedContentManager.clearCurrentlySelectedContent();
/* 476 */       break;
/*     */     
/*     */     case 5: 
/* 479 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 483 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 491 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 498 */     Map<String, Long> states = TorrentUtil.calculateToolbarStates(SelectedContentManager.getCurrentlySelectedContent(), null);
/*     */     
/* 500 */     list.putAll(states);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/TorrentInfoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */