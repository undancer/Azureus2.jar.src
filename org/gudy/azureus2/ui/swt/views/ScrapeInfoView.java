/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedTruncatedLabel;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.maketorrent.MultiTrackerEditor;
/*     */ import org.gudy.azureus2.ui.swt.maketorrent.TrackerEditorListener;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
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
/*     */ public class ScrapeInfoView
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private DownloadManager manager;
/*     */   private Composite cParent;
/*     */   private Composite cScrapeInfoView;
/*     */   private BufferedTruncatedLabel tracker_status;
/*     */   private Button updateButton;
/*     */   private BufferedLabel trackerUpdateIn;
/*     */   private Menu menuTracker;
/*     */   private MenuItem itemSelect;
/*     */   private BufferedTruncatedLabel trackerUrlValue;
/*     */   private long lastRefreshSecs;
/*     */   private UISWTView swtView;
/*     */   
/*     */   private String getFullTitle()
/*     */   {
/*  82 */     return MessageText.getString("ScrapeInfoView.title");
/*     */   }
/*     */   
/*     */   private void initialize(Composite parent) {
/*  86 */     this.cParent = parent;
/*     */     
/*     */ 
/*  89 */     final Display display = parent.getDisplay();
/*     */     
/*  91 */     if ((this.cScrapeInfoView == null) || (this.cScrapeInfoView.isDisposed())) {
/*  92 */       this.cScrapeInfoView = new Composite(parent, 0);
/*     */     }
/*     */     
/*  95 */     GridData gridData = new GridData(1808);
/*  96 */     this.cScrapeInfoView.setLayoutData(gridData);
/*     */     
/*  98 */     GridLayout layoutInfo = new GridLayout();
/*  99 */     layoutInfo.numColumns = 4;
/* 100 */     this.cScrapeInfoView.setLayout(layoutInfo);
/*     */     
/* 102 */     Label label = new Label(this.cScrapeInfoView, 16384);
/* 103 */     Messages.setLanguageText(label, "GeneralView.label.trackerurl");
/* 104 */     label.setCursor(display.getSystemCursor(21));
/* 105 */     label.setForeground(Colors.blue);
/* 106 */     label.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 108 */         String announce = ScrapeInfoView.this.trackerUrlValue.getText();
/* 109 */         if ((announce != null) && (announce.length() != 0)) {
/* 110 */           new Clipboard(display).setContents(new Object[] { announce }, new Transfer[] { TextTransfer.getInstance() });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void mouseDown(MouseEvent arg0)
/*     */       {
/* 119 */         String announce = ScrapeInfoView.this.trackerUrlValue.getText();
/* 120 */         if ((announce != null) && (announce.length() != 0)) {
/* 121 */           new Clipboard(display).setContents(new Object[] { announce }, new Transfer[] { TextTransfer.getInstance() });
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 129 */     });
/* 130 */     this.menuTracker = new Menu(parent.getShell(), 8);
/* 131 */     this.itemSelect = new MenuItem(this.menuTracker, 64);
/* 132 */     Messages.setLanguageText(this.itemSelect, "GeneralView.menu.selectTracker");
/* 133 */     MenuItem itemEdit = new MenuItem(this.menuTracker, 0);
/* 134 */     Messages.setLanguageText(itemEdit, "MyTorrentsView.menu.editTracker");
/*     */     
/* 136 */     this.cScrapeInfoView.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 138 */         ScrapeInfoView.this.menuTracker.dispose();
/*     */       }
/*     */       
/* 141 */     });
/* 142 */     itemEdit.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 144 */         final TOTorrent torrent = ScrapeInfoView.this.manager.getTorrent();
/*     */         
/* 146 */         if (torrent == null) {
/* 147 */           return;
/*     */         }
/*     */         
/* 150 */         List<List<String>> group = TorrentUtils.announceGroupsToList(torrent);
/*     */         
/* 152 */         new MultiTrackerEditor(null, null, group, new TrackerEditorListener() {
/*     */           public void trackersChanged(String str, String str2, List<List<String>> _group) {
/* 154 */             TorrentUtils.listToAnnounceGroups(_group, torrent);
/*     */             try
/*     */             {
/* 157 */               TorrentUtils.writeToFile(torrent);
/*     */             }
/*     */             catch (Throwable e2) {
/* 160 */               Debug.printStackTrace(e2);
/*     */             }
/*     */             
/* 163 */             TRTrackerAnnouncer tc = ScrapeInfoView.this.manager.getTrackerClient();
/*     */             
/* 165 */             if (tc != null)
/*     */             {
/* 167 */               tc.resetTrackerUrl(true); } } }, true, true);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 173 */     });
/* 174 */     TOTorrent torrent = this.manager == null ? null : this.manager.getTorrent();
/*     */     
/* 176 */     itemEdit.setEnabled((torrent != null) && (!TorrentUtils.isReallyPrivate(torrent)));
/*     */     
/* 178 */     final Listener menuListener = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 180 */         if ((e.widget instanceof MenuItem))
/*     */         {
/* 182 */           String text = ((MenuItem)e.widget).getText();
/*     */           
/* 184 */           TOTorrent torrent = ScrapeInfoView.this.manager.getTorrent();
/*     */           
/* 186 */           TorrentUtils.announceGroupsSetFirst(torrent, text);
/*     */           try
/*     */           {
/* 189 */             TorrentUtils.writeToFile(torrent);
/*     */           }
/*     */           catch (TOTorrentException f)
/*     */           {
/* 193 */             Debug.printStackTrace(f);
/*     */           }
/*     */           
/* 196 */           TRTrackerAnnouncer tc = ScrapeInfoView.this.manager.getTrackerClient();
/*     */           
/* 198 */           if (tc != null)
/*     */           {
/* 200 */             tc.resetTrackerUrl(false);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 205 */     };
/* 206 */     this.menuTracker.addListener(22, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 208 */         Menu menuSelect = ScrapeInfoView.this.itemSelect.getMenu();
/* 209 */         if ((menuSelect != null) && (!menuSelect.isDisposed())) {
/* 210 */           menuSelect.dispose();
/*     */         }
/* 212 */         if ((ScrapeInfoView.this.manager == null) || (ScrapeInfoView.this.cScrapeInfoView == null) || (ScrapeInfoView.this.cScrapeInfoView.isDisposed()))
/*     */         {
/* 214 */           return;
/*     */         }
/* 216 */         List<List<String>> groups = TorrentUtils.announceGroupsToList(ScrapeInfoView.this.manager.getTorrent());
/* 217 */         menuSelect = new Menu(ScrapeInfoView.this.cScrapeInfoView.getShell(), 4);
/* 218 */         ScrapeInfoView.this.itemSelect.setMenu(menuSelect);
/*     */         
/* 220 */         for (List<String> trackers : groups) {
/* 221 */           MenuItem menuItem = new MenuItem(menuSelect, 64);
/* 222 */           Messages.setLanguageText(menuItem, "wizard.multitracker.group");
/* 223 */           menu = new Menu(ScrapeInfoView.this.cScrapeInfoView.getShell(), 4);
/* 224 */           menuItem.setMenu(menu);
/*     */           
/* 226 */           for (String url : trackers) {
/* 227 */             MenuItem menuItemTracker = new MenuItem(menu, 64);
/* 228 */             menuItemTracker.setText(url);
/* 229 */             menuItemTracker.addListener(13, menuListener);
/*     */           }
/*     */         }
/*     */         Menu menu;
/*     */       }
/* 234 */     });
/* 235 */     this.trackerUrlValue = new BufferedTruncatedLabel(this.cScrapeInfoView, 16384, 70);
/*     */     
/* 237 */     this.trackerUrlValue.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDown(MouseEvent event) {
/* 239 */         if ((event.button == 3) || ((event.button == 1) && (event.stateMask == 262144)))
/*     */         {
/* 241 */           ScrapeInfoView.this.menuTracker.setVisible(true);
/* 242 */         } else if (event.button == 1) {
/* 243 */           String url = ScrapeInfoView.this.trackerUrlValue.getText();
/* 244 */           if ((url.startsWith("http://")) || (url.startsWith("https://"))) {
/* 245 */             int pos = -1;
/* 246 */             if ((pos = url.indexOf("/announce")) != -1) {
/* 247 */               url = url.substring(0, pos + 1);
/*     */             }
/* 249 */             Utils.launch(url);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 254 */     });
/* 255 */     gridData = new GridData(768);
/* 256 */     gridData.horizontalSpan = 3;
/* 257 */     this.trackerUrlValue.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 261 */     label = new Label(this.cScrapeInfoView, 16384);
/* 262 */     Messages.setLanguageText(label, "GeneralView.label.tracker");
/* 263 */     this.tracker_status = new BufferedTruncatedLabel(this.cScrapeInfoView, 16384, 150);
/* 264 */     gridData = new GridData(768);
/* 265 */     gridData.horizontalSpan = 3;
/* 266 */     this.tracker_status.setLayoutData(gridData);
/*     */     
/* 268 */     label = new Label(this.cScrapeInfoView, 16384);
/* 269 */     Messages.setLanguageText(label, "GeneralView.label.updatein");
/* 270 */     this.trackerUpdateIn = new BufferedLabel(this.cScrapeInfoView, 16384);
/* 271 */     gridData = new GridData(260);
/* 272 */     this.trackerUpdateIn.setLayoutData(gridData);
/*     */     
/* 274 */     this.updateButton = new Button(this.cScrapeInfoView, 8);
/* 275 */     Messages.setLanguageText(this.updateButton, "GeneralView.label.trackerurlupdate");
/* 276 */     this.updateButton.setLayoutData(new GridData());
/* 277 */     this.updateButton.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent event) {
/* 279 */         new AEThread2("SIV:async")
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/* 284 */             if (ScrapeInfoView.this.manager.getTrackerClient() != null)
/*     */             {
/* 286 */               ScrapeInfoView.this.manager.requestTrackerAnnounce(false);
/*     */             }
/*     */             else
/*     */             {
/* 290 */               ScrapeInfoView.this.manager.requestTrackerScrape(true);
/*     */             }
/*     */             
/*     */           }
/*     */         }.start();
/*     */       }
/* 296 */     });
/* 297 */     this.cScrapeInfoView.layout(true);
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 302 */     if (this.manager == null) {
/* 303 */       return;
/*     */     }
/*     */     
/* 306 */     long thisRefreshSecs = SystemTime.getCurrentTime() / 1000L;
/* 307 */     if (this.lastRefreshSecs != thisRefreshSecs) {
/* 308 */       this.lastRefreshSecs = thisRefreshSecs;
/* 309 */       setTracker();
/*     */     }
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 314 */     return this.cScrapeInfoView;
/*     */   }
/*     */   
/*     */   private void setTracker() {
/* 318 */     if ((this.cScrapeInfoView == null) || (this.cScrapeInfoView.isDisposed())) {
/* 319 */       return;
/*     */     }
/*     */     
/* 322 */     Display display = this.cScrapeInfoView.getDisplay();
/*     */     
/* 324 */     String status = this.manager.getTrackerStatus();
/* 325 */     int time = this.manager.getTrackerTime();
/*     */     
/* 327 */     TRTrackerAnnouncer trackerClient = this.manager.getTrackerClient();
/*     */     
/* 329 */     if (trackerClient != null)
/*     */     {
/* 331 */       this.tracker_status.setText(trackerClient.getStatusString());
/*     */       
/* 333 */       time = trackerClient.getTimeUntilNextUpdate();
/*     */     }
/*     */     else
/*     */     {
/* 337 */       this.tracker_status.setText(status);
/*     */     }
/*     */     
/* 340 */     if (time < 0)
/*     */     {
/* 342 */       this.trackerUpdateIn.setText(MessageText.getString("GeneralView.label.updatein.querying"));
/*     */     }
/*     */     else
/*     */     {
/* 346 */       this.trackerUpdateIn.setText(TimeFormatter.formatColon(time));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 351 */     String trackerURL = null;
/*     */     
/* 353 */     if (trackerClient != null)
/*     */     {
/* 355 */       URL temp = trackerClient.getTrackerURL();
/*     */       
/* 357 */       if (temp != null)
/*     */       {
/* 359 */         trackerURL = temp.toString();
/*     */       }
/*     */     }
/*     */     
/* 363 */     if (trackerURL == null)
/*     */     {
/* 365 */       TOTorrent torrent = this.manager.getTorrent();
/*     */       
/* 367 */       if (torrent != null)
/*     */       {
/* 369 */         trackerURL = torrent.getAnnounceURL().toString();
/*     */       }
/*     */     }
/*     */     
/* 373 */     if (trackerURL != null)
/*     */     {
/* 375 */       this.trackerUrlValue.setText(trackerURL);
/*     */       
/* 377 */       if ((trackerURL.startsWith("http://")) || (trackerURL.startsWith("https://"))) {
/* 378 */         this.trackerUrlValue.setForeground(Colors.blue);
/* 379 */         this.trackerUrlValue.setCursor(display.getSystemCursor(21));
/* 380 */         Messages.setLanguageText(this.trackerUrlValue.getWidget(), "GeneralView.label.trackerurlopen.tooltip", true);
/*     */       }
/*     */       else {
/* 383 */         this.trackerUrlValue.setForeground(null);
/* 384 */         this.trackerUrlValue.setCursor(null);
/* 385 */         Messages.setLanguageText(this.trackerUrlValue.getWidget(), null);
/* 386 */         this.trackerUrlValue.setToolTipText(null);
/*     */       } }
/*     */     boolean update_state;
/*     */     boolean update_state;
/* 390 */     if (trackerClient != null)
/*     */     {
/* 392 */       update_state = SystemTime.getCurrentTime() / 1000L - trackerClient.getLastUpdateTime() >= 60L;
/*     */     }
/*     */     else
/*     */     {
/* 396 */       TRTrackerScraperResponse sr = this.manager.getTrackerScrapeResponse();
/*     */       boolean update_state;
/* 398 */       if (sr == null)
/*     */       {
/* 400 */         update_state = true;
/*     */       }
/*     */       else
/*     */       {
/* 404 */         update_state = SystemTime.getCurrentTime() - sr.getScrapeStartTime() >= 120000L;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 409 */     if (this.updateButton.getEnabled() != update_state)
/*     */     {
/* 411 */       this.updateButton.setEnabled(update_state);
/*     */     }
/* 413 */     this.cScrapeInfoView.layout();
/*     */   }
/*     */   
/*     */   private void setDownlaodManager(DownloadManager dm) {
/* 417 */     this.manager = dm;
/* 418 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 420 */         if (ScrapeInfoView.this.cScrapeInfoView != null) {
/* 421 */           Utils.disposeComposite(ScrapeInfoView.this.cScrapeInfoView, false);
/*     */         }
/* 423 */         if ((ScrapeInfoView.this.cParent != null) && (!ScrapeInfoView.this.cParent.isDisposed()))
/*     */         {
/* 425 */           ScrapeInfoView.this.initialize(ScrapeInfoView.this.cParent);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 432 */     switch (event.getType()) {
/*     */     case 0: 
/* 434 */       this.swtView = ((UISWTView)event.getData());
/* 435 */       this.swtView.setTitle(getFullTitle());
/* 436 */       break;
/*     */     
/*     */     case 7: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 2: 
/* 443 */       initialize((Composite)event.getData());
/* 444 */       break;
/*     */     
/*     */     case 6: 
/* 447 */       Messages.updateLanguageForControl(getComposite());
/* 448 */       this.swtView.setTitle(getFullTitle());
/* 449 */       break;
/*     */     
/*     */     case 1: 
/* 452 */       Object ds = event.getData();
/* 453 */       if (((ds instanceof Object[])) && (((Object[])ds).length > 0)) {
/* 454 */         ds = ((Object[])(Object[])ds)[0];
/*     */       }
/* 456 */       if ((ds instanceof DownloadManager)) {
/* 457 */         DownloadManager dm = (DownloadManager)ds;
/* 458 */         setDownlaodManager(dm); }
/* 459 */       break;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */ 
/*     */     case 5: 
/* 466 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 470 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/ScrapeInfoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */