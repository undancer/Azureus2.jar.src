/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableGroupRowRunner;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRefreshListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryManager;
/*     */ import org.gudy.azureus2.core3.category.CategoryManagerListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostListener;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
/*     */ import org.gudy.azureus2.ui.swt.CategoryAdderWindow;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.AnnounceCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.AverageBytesInItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.AverageBytesOutItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.BadNATCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.CategoryItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.CompletedCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.DateAddedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.DownloadedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.LeftItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.NameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.PassiveItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.PeerCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.PersistentItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.ScrapeCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.SeedCountItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.StatusItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.TotalBytesInItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.TotalBytesOutItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.TrackerItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.mytracker.UploadedItem;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MyTrackerView
/*     */   extends TableViewTab<TRHostTorrent>
/*     */   implements TRHostListener, CategoryManagerListener, TableLifeCycleListener, TableSelectionListener, TableViewSWTMenuFillListener, TableRefreshListener, UIPluginViewToolBarListener
/*     */ {
/*  89 */   private static TableColumnCore[] basicItems = null;
/*     */   
/*  91 */   protected static final TorrentAttribute category_attribute = TorrentManagerImpl.getSingleton().getAttribute("Category");
/*     */   
/*     */   private Menu menuCategory;
/*     */   
/*     */   private TableViewSWT<TRHostTorrent> tv;
/*     */   
/*     */   public MyTrackerView()
/*     */   {
/*  99 */     super("MyTrackerView");
/* 100 */     if (basicItems == null) {
/* 101 */       basicItems = new TableColumnCore[] { new NameItem(), new TrackerItem(), new StatusItem(), new CategoryItem(), new PassiveItem(), new PersistentItem(), new SeedCountItem(), new PeerCountItem(), new BadNATCountItem(), new AnnounceCountItem(), new ScrapeCountItem(), new CompletedCountItem(), new UploadedItem(), new DownloadedItem(), new LeftItem(), new TotalBytesInItem(), new AverageBytesInItem(), new TotalBytesOutItem(), new AverageBytesOutItem(), new DateAddedItem() };
/*     */     }
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
/*     */ 
/* 125 */     this.tv = TableViewFactory.createTableViewSWT(TrackerTorrent.class, "MyTracker", getPropertiesPrefix(), basicItems, "name", 268503042);
/*     */     
/*     */ 
/* 128 */     this.tv.addLifeCycleListener(this);
/* 129 */     this.tv.addSelectionListener(this, false);
/* 130 */     this.tv.addMenuFillListener(this);
/* 131 */     this.tv.addRefreshListener(this, false);
/*     */   }
/*     */   
/*     */   public TableViewSWT<TRHostTorrent> initYourTableView() {
/* 135 */     return this.tv;
/*     */   }
/*     */   
/*     */   public void tableViewInitialized()
/*     */   {
/* 140 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 142 */         core.getTrackerHost().addListener(MyTrackerView.this);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void tableViewDestroyed()
/*     */   {
/*     */     try {
/* 150 */       AzureusCoreFactory.getSingleton().getTrackerHost().removeListener(this);
/*     */     }
/*     */     catch (Exception ignore) {}
/*     */   }
/*     */   
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask)
/*     */   {
/* 157 */     final TRHostTorrent torrent = (TRHostTorrent)this.tv.getFirstSelectedDataSource();
/* 158 */     if (torrent == null)
/* 159 */       return;
/* 160 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 163 */         DownloadManager dm = core.getGlobalManager().getDownloadManager(torrent.getTorrent());
/*     */         
/* 165 */         if (dm != null) {
/* 166 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 167 */           if (uiFunctions != null) {
/* 168 */             uiFunctions.getMDI().showEntryByID("DMDetails", dm);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 177 */     this.menuCategory = new Menu(getComposite().getShell(), 4);
/* 178 */     MenuItem itemCategory = new MenuItem(menu, 64);
/* 179 */     Messages.setLanguageText(itemCategory, "MyTorrentsView.menu.setCategory");
/*     */     
/* 181 */     itemCategory.setMenu(this.menuCategory);
/*     */     
/* 183 */     addCategorySubMenu();
/*     */     
/* 185 */     new MenuItem(menu, 2);
/*     */     
/* 187 */     MenuItem itemStart = new MenuItem(menu, 8);
/* 188 */     Messages.setLanguageText(itemStart, "MyTorrentsView.menu.start");
/* 189 */     Utils.setMenuItemImage(itemStart, "start");
/*     */     
/* 191 */     MenuItem itemStop = new MenuItem(menu, 8);
/* 192 */     Messages.setLanguageText(itemStop, "MyTorrentsView.menu.stop");
/* 193 */     Utils.setMenuItemImage(itemStop, "stop");
/*     */     
/* 195 */     MenuItem itemRemove = new MenuItem(menu, 8);
/* 196 */     Messages.setLanguageText(itemRemove, "MyTorrentsView.menu.remove");
/* 197 */     Utils.setMenuItemImage(itemRemove, "delete");
/*     */     
/* 199 */     Object[] hostTorrents = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 201 */     itemStart.setEnabled(false);
/* 202 */     itemStop.setEnabled(false);
/* 203 */     itemRemove.setEnabled(false);
/*     */     
/* 205 */     if (hostTorrents.length > 0)
/*     */     {
/* 207 */       boolean start_ok = true;
/* 208 */       boolean stop_ok = true;
/* 209 */       boolean remove_ok = true;
/*     */       
/* 211 */       for (int i = 0; i < hostTorrents.length; i++)
/*     */       {
/* 213 */         TRHostTorrent host_torrent = (TRHostTorrent)hostTorrents[i];
/*     */         
/* 215 */         int status = host_torrent.getStatus();
/*     */         
/* 217 */         if (status != 1)
/*     */         {
/* 219 */           start_ok = false;
/*     */         }
/*     */         
/*     */ 
/* 223 */         if (status != 2)
/*     */         {
/* 225 */           stop_ok = false;
/*     */         }
/*     */       }
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
/* 239 */       itemStart.setEnabled(start_ok);
/* 240 */       itemStop.setEnabled(stop_ok);
/* 241 */       itemRemove.setEnabled(remove_ok);
/*     */     }
/*     */     
/* 244 */     itemStart.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 246 */         MyTrackerView.this.startSelectedTorrents();
/*     */       }
/*     */       
/* 249 */     });
/* 250 */     itemStop.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 252 */         MyTrackerView.this.stopSelectedTorrents();
/*     */       }
/*     */       
/* 255 */     });
/* 256 */     itemRemove.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 258 */         MyTrackerView.this.removeSelectedTorrents();
/*     */       }
/*     */       
/* 261 */     });
/* 262 */     new MenuItem(menu, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void torrentAdded(TRHostTorrent host_torrent)
/*     */   {
/* 273 */     this.tv.addDataSource(host_torrent);
/*     */   }
/*     */   
/*     */ 
/*     */   public void torrentChanged(TRHostTorrent t) {}
/*     */   
/*     */ 
/*     */   public void torrentRemoved(TRHostTorrent host_torrent)
/*     */   {
/* 282 */     this.tv.removeDataSource(host_torrent);
/*     */   }
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
/*     */   public boolean handleExternalRequest(InetSocketAddress client, String user, String url, URL absolute_url, String header, InputStream is, OutputStream os, AsyncController async)
/*     */     throws IOException
/*     */   {
/* 298 */     return false;
/*     */   }
/*     */   
/*     */   public void tableRefresh()
/*     */   {
/* 303 */     if ((getComposite() == null) || (getComposite().isDisposed()))
/*     */     {
/* 305 */       return;
/*     */     }
/*     */     
/* 308 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 309 */     if (uiFunctions != null) {
/* 310 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 316 */     TableRowCore[] rows = this.tv.getRows();
/* 317 */     for (int x = 0; x < rows.length; x++) {
/* 318 */       TableRowSWT row = (TableRowSWT)rows[x];
/*     */       
/* 320 */       if (row != null)
/*     */       {
/*     */ 
/*     */ 
/* 324 */         TRHostTorrent host_torrent = (TRHostTorrent)rows[x].getDataSource(true);
/*     */         
/* 326 */         if (host_torrent != null)
/*     */         {
/*     */ 
/*     */ 
/* 330 */           long uploaded = host_torrent.getTotalUploaded();
/* 331 */           long downloaded = host_torrent.getTotalDownloaded();
/* 332 */           long left = host_torrent.getTotalLeft();
/*     */           
/* 334 */           int seed_count = host_torrent.getSeedCount();
/*     */           
/* 336 */           host_torrent.setData("GUI_PeerCount", new Long(host_torrent.getLeecherCount()));
/* 337 */           host_torrent.setData("GUI_SeedCount", new Long(seed_count));
/* 338 */           host_torrent.setData("GUI_BadNATCount", new Long(host_torrent.getBadNATCount()));
/* 339 */           host_torrent.setData("GUI_Uploaded", new Long(uploaded));
/* 340 */           host_torrent.setData("GUI_Downloaded", new Long(downloaded));
/* 341 */           host_torrent.setData("GUI_Left", new Long(left));
/*     */           
/* 343 */           if (seed_count != 0) {
/* 344 */             Color fg = row.getForeground();
/*     */             
/* 346 */             if ((fg != null) && (fg.equals(org.gudy.azureus2.ui.swt.mainwindow.Colors.blues[7])))
/* 347 */               row.setForeground(org.gudy.azureus2.ui.swt.mainwindow.Colors.blues[7]);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 354 */   public void refreshToolBarItems(Map<String, Long> list) { boolean start = false;boolean stop = false;boolean remove = false;
/* 355 */     Object[] hostTorrents = this.tv.getSelectedDataSources().toArray();
/* 356 */     if (hostTorrents.length > 0) {
/* 357 */       remove = true;
/* 358 */       for (int i = 0; i < hostTorrents.length; i++) {
/* 359 */         TRHostTorrent host_torrent = (TRHostTorrent)hostTorrents[i];
/*     */         
/* 361 */         int status = host_torrent.getStatus();
/*     */         
/* 363 */         if (status == 1) {
/* 364 */           start = true;
/*     */         }
/*     */         
/* 367 */         if (status == 2) {
/* 368 */           stop = true;
/*     */         }
/*     */       }
/*     */     }
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
/* 383 */     list.put("start", Long.valueOf(start ? 1L : 0L));
/* 384 */     list.put("stop", Long.valueOf(stop ? 1L : 0L));
/* 385 */     list.put("remove", Long.valueOf(remove ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 392 */     String itemKey = item.getID();
/*     */     
/* 394 */     if (itemKey.equals("start")) {
/* 395 */       startSelectedTorrents();
/* 396 */       return true;
/*     */     }
/* 398 */     if (itemKey.equals("stop")) {
/* 399 */       stopSelectedTorrents();
/* 400 */       return true;
/*     */     }
/* 402 */     if (itemKey.equals("remove")) {
/* 403 */       removeSelectedTorrents();
/* 404 */       return true;
/*     */     }
/*     */     
/* 407 */     return false;
/*     */   }
/*     */   
/*     */   private void stopSelectedTorrents() {
/* 411 */     this.tv.runForSelectedRows(new TableGroupRowRunner() {
/*     */       public void run(TableRowCore row) {
/* 413 */         TRHostTorrent torrent = (TRHostTorrent)row.getDataSource(true);
/* 414 */         if (torrent.getStatus() == 2)
/* 415 */           torrent.stop();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void startSelectedTorrents() {
/* 421 */     this.tv.runForSelectedRows(new TableGroupRowRunner() {
/*     */       public void run(TableRowCore row) {
/* 423 */         TRHostTorrent torrent = (TRHostTorrent)row.getDataSource(true);
/* 424 */         if (torrent.getStatus() == 1)
/* 425 */           torrent.start();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void removeSelectedTorrents() {
/* 431 */     this.tv.runForSelectedRows(new TableGroupRowRunner() {
/*     */       public void run(TableRowCore row) {
/* 433 */         TRHostTorrent torrent = (TRHostTorrent)row.getDataSource(true);
/*     */         try {
/* 435 */           torrent.remove();
/*     */         }
/*     */         catch (TRHostTorrentRemovalVetoException f)
/*     */         {
/* 439 */           Logger.log(new LogAlert(torrent, false, "{globalmanager.download.remove.veto}", f));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void addCategorySubMenu()
/*     */   {
/* 447 */     MenuItem[] items = this.menuCategory.getItems();
/*     */     
/* 449 */     for (int i = 0; i < items.length; i++) {
/* 450 */       items[i].dispose();
/*     */     }
/*     */     
/* 453 */     Category[] categories = CategoryManager.getCategories();
/* 454 */     Arrays.sort(categories);
/*     */     
/* 456 */     if (categories.length > 0) {
/* 457 */       Category catUncat = CategoryManager.getCategory(2);
/* 458 */       if (catUncat != null) {
/* 459 */         MenuItem itemCategory = new MenuItem(this.menuCategory, 8);
/* 460 */         Messages.setLanguageText(itemCategory, catUncat.getName());
/* 461 */         itemCategory.setData("Category", catUncat);
/* 462 */         itemCategory.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 464 */             MenuItem item = (MenuItem)event.widget;
/* 465 */             MyTrackerView.this.assignSelectedToCategory((Category)item.getData("Category"));
/*     */           }
/*     */           
/* 468 */         });
/* 469 */         new MenuItem(this.menuCategory, 2);
/*     */       }
/*     */       
/* 472 */       for (i = 0; i < categories.length; i++) {
/* 473 */         if (categories[i].getType() == 0) {
/* 474 */           MenuItem itemCategory = new MenuItem(this.menuCategory, 8);
/* 475 */           itemCategory.setText(categories[i].getName());
/* 476 */           itemCategory.setData("Category", categories[i]);
/*     */           
/* 478 */           itemCategory.addListener(13, new Listener() {
/*     */             public void handleEvent(Event event) {
/* 480 */               MenuItem item = (MenuItem)event.widget;
/* 481 */               MyTrackerView.this.assignSelectedToCategory((Category)item.getData("Category"));
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/* 487 */       new MenuItem(this.menuCategory, 2);
/*     */     }
/*     */     
/* 490 */     MenuItem itemAddCategory = new MenuItem(this.menuCategory, 8);
/* 491 */     Messages.setLanguageText(itemAddCategory, "MyTorrentsView.menu.setCategory.add");
/*     */     
/*     */ 
/* 494 */     itemAddCategory.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 496 */         MyTrackerView.this.addCategory();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void categoryAdded(Category category)
/*     */   {
/* 505 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 511 */         MyTrackerView.this.addCategorySubMenu();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void categoryRemoved(Category category)
/*     */   {
/* 520 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 526 */         MyTrackerView.this.addCategorySubMenu();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void categoryChanged(Category category) {}
/*     */   
/*     */   private void addCategory()
/*     */   {
/* 535 */     CategoryAdderWindow adderWindow = new CategoryAdderWindow(SWTThread.getInstance().getDisplay());
/* 536 */     Category newCategory = adderWindow.getNewCategory();
/* 537 */     if (newCategory != null)
/* 538 */       assignSelectedToCategory(newCategory);
/*     */   }
/*     */   
/*     */   private void assignSelectedToCategory(final Category category) {
/* 542 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 544 */         MyTrackerView.this.assignSelectedToCategory(core, category);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void assignSelectedToCategory(final AzureusCore core, final Category category)
/*     */   {
/* 551 */     this.tv.runForSelectedRows(new TableGroupRowRunner()
/*     */     {
/*     */       public void run(TableRowCore row) {
/* 554 */         TRHostTorrent tr_torrent = (TRHostTorrent)row.getDataSource(true);
/*     */         
/* 556 */         TOTorrent torrent = tr_torrent.getTorrent();
/*     */         
/* 558 */         DownloadManager dm = core.getGlobalManager().getDownloadManager(torrent);
/*     */         
/* 560 */         if (dm != null)
/*     */         {
/* 562 */           dm.getDownloadState().setCategory(category);
/*     */         }
/*     */         else
/*     */         {
/*     */           String cat_str;
/*     */           String cat_str;
/* 568 */           if (category == null)
/*     */           {
/* 570 */             cat_str = null;
/*     */           } else { String cat_str;
/* 572 */             if (category == CategoryManager.getCategory(2))
/*     */             {
/* 574 */               cat_str = null;
/*     */             }
/*     */             else
/*     */             {
/* 578 */               cat_str = category.getName();
/*     */             }
/*     */           }
/*     */           
/* 582 */           TorrentUtils.setPluginStringProperty(torrent, "azcoreplugins.category", cat_str);
/*     */           try
/*     */           {
/* 585 */             TorrentUtils.writeToFile(torrent);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 589 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void deselected(TableRowCore[] rows)
/*     */   {
/* 598 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 599 */     if (uiFunctions != null) {
/* 600 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */   public void focusChanged(TableRowCore focus)
/*     */   {
/* 606 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 607 */     if (uiFunctions != null) {
/* 608 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */   public void selected(TableRowCore[] rows)
/*     */   {
/* 614 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 615 */     if (uiFunctions != null) {
/* 616 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */   public void mouseExit(TableRowCore row) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/MyTrackerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */