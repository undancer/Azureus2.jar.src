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
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionAdapter;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionListener;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo2;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryDropListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.dnd.DropTarget;
/*     */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*     */ import org.eclipse.swt.dnd.DropTargetEvent;
/*     */ import org.eclipse.swt.dnd.FileTransfer;
/*     */ import org.eclipse.swt.dnd.HTMLTransfer;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.category.Category;
/*     */ import org.gudy.azureus2.core3.category.CategoryManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManagerListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDirContents;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
/*     */ import org.gudy.azureus2.ui.swt.CategoryAdderWindow;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.myshares.CategoryItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.myshares.NameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.myshares.PersistentItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.myshares.TypeItem;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MySharesView
/*     */   extends TableViewTab<ShareResource>
/*     */   implements ShareManagerListener, TableLifeCycleListener, TableViewSWTMenuFillListener, TableRefreshListener, TableSelectionListener, ViewTitleInfo2, UIPluginViewToolBarListener
/*     */ {
/*  90 */   private static final TableColumnCore[] basicItems = { new NameItem(), new TypeItem(), new CategoryItem(), new PersistentItem() };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */   protected static final TorrentAttribute category_attribute = TorrentManagerImpl.getSingleton().getAttribute("Category");
/*     */   
/*     */ 
/*     */   private Menu menuCategory;
/*     */   
/*     */   private TableViewSWT<ShareResource> tv;
/*     */   
/*     */   private DropTarget dropTarget;
/*     */   
/*     */ 
/*     */   public MySharesView()
/*     */   {
/* 109 */     super("MySharesView");
/* 110 */     this.tv = TableViewFactory.createTableViewSWT(ShareResource.class, "MyShares", getPropertiesPrefix(), basicItems, "name", 268503042);
/*     */     
/*     */ 
/*     */ 
/* 114 */     this.tv.addSelectionListener(new TableSelectionAdapter()
/*     */     {
/* 116 */       public void defaultSelected(TableRowCore[] rows, int stateMask) { MySharesView.this.defaultSelected(rows); } }, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 121 */     this.tv.addLifeCycleListener(this);
/* 122 */     this.tv.addMenuFillListener(this);
/* 123 */     this.tv.addRefreshListener(this, false);
/* 124 */     this.tv.addSelectionListener(this, false);
/*     */   }
/*     */   
/*     */   public TableViewSWT initYourTableView() {
/* 128 */     return this.tv;
/*     */   }
/*     */   
/*     */   private void defaultSelected(TableRowCore[] rows) {
/* 132 */     ShareResource share = (ShareResource)this.tv.getFirstSelectedDataSource();
/* 133 */     if (share == null) {
/* 134 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 140 */     List dms = AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManagers();
/*     */     
/* 142 */     for (int i = 0; i < dms.size(); i++) {
/* 143 */       org.gudy.azureus2.core3.download.DownloadManager dm = (org.gudy.azureus2.core3.download.DownloadManager)dms.get(i);
/*     */       try
/*     */       {
/* 146 */         byte[] share_hash = null;
/*     */         
/* 148 */         if (share.getType() == 2)
/*     */         {
/* 150 */           share_hash = ((ShareResourceDir)share).getItem().getTorrent().getHash();
/*     */         }
/* 152 */         else if (share.getType() == 1)
/*     */         {
/* 154 */           share_hash = ((ShareResourceFile)share).getItem().getTorrent().getHash();
/*     */         }
/*     */         
/* 157 */         if (Arrays.equals(share_hash, dm.getTorrent().getHash()))
/*     */         {
/* 159 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 160 */           if (uiFunctions != null) {
/* 161 */             uiFunctions.getMDI().showEntryByID("DMDetails", dm);
/*     */           }
/*     */           
/*     */ 
/* 165 */           break;
/*     */         }
/*     */       } catch (Throwable e) {
/* 168 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void tableViewInitialized() {
/* 174 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 176 */         MySharesView.this.createRows(core);
/*     */       }
/*     */     });
/*     */     
/* 180 */     if (this.dropTarget == null) {
/* 181 */       this.dropTarget = this.tv.createDropTarget(31);
/*     */       
/* 183 */       if (this.dropTarget != null) {
/* 184 */         this.dropTarget.setTransfer(new Transfer[] { HTMLTransfer.getInstance(), URLTransfer.getInstance(), FileTransfer.getInstance(), TextTransfer.getInstance() });
/*     */         
/*     */ 
/*     */ 
/* 188 */         this.dropTarget.addDropListener(new DropTargetAdapter() {
/*     */           public void drop(DropTargetEvent event) {
/* 190 */             if (!MySharesView.this.share(event.data)) {
/* 191 */               TorrentOpener.openDroppedTorrents(event, true);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean share(Object eventData) {
/* 200 */     boolean shared = false;
/* 201 */     if (((eventData instanceof String[])) || ((eventData instanceof String))) {
/* 202 */       String[] sourceNames = { (eventData instanceof String[]) ? (String[])eventData : (String)eventData };
/*     */       
/*     */ 
/*     */ 
/* 206 */       if (sourceNames == null) {
/* 207 */         return false;
/*     */       }
/* 209 */       for (int i = 0; i < sourceNames.length; i++) {
/* 210 */         File source = new File(sourceNames[i]);
/* 211 */         String filename = source.getAbsolutePath();
/*     */         try {
/* 213 */           if ((source.isFile()) && (!TorrentUtils.isTorrentFile(filename))) {
/* 214 */             ShareUtils.shareFile(filename);
/* 215 */             shared = true;
/* 216 */           } else if (source.isDirectory()) {
/* 217 */             ShareUtils.shareDir(filename);
/* 218 */             shared = true;
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     }
/* 224 */     return shared;
/*     */   }
/*     */   
/*     */   public void tableViewDestroyed() {
/*     */     try {
/* 229 */       PluginInitializer.getDefaultInterface().getShareManager().removeListener(this);
/*     */     }
/*     */     catch (ShareException e) {
/* 232 */       Debug.printStackTrace(e);
/*     */     }
/*     */     catch (Throwable ignore) {}
/*     */   }
/*     */   
/*     */   private void createRows(AzureusCore core)
/*     */   {
/*     */     try {
/* 240 */       ShareManager sm = core.getPluginManager().getDefaultPluginInterface().getShareManager();
/*     */       
/* 242 */       ShareResource[] shares = sm.getShares();
/*     */       
/* 244 */       for (int i = 0; i < shares.length; i++)
/*     */       {
/* 246 */         resourceAdded(shares[i]);
/*     */       }
/*     */       
/* 249 */       sm.addListener(this);
/*     */     }
/*     */     catch (ShareException e)
/*     */     {
/* 253 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 261 */     Shell shell = menu.getShell();
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
/* 272 */     this.menuCategory = new Menu(shell, 4);
/* 273 */     MenuItem itemCategory = new MenuItem(menu, 64);
/* 274 */     Messages.setLanguageText(itemCategory, "MyTorrentsView.menu.setCategory");
/*     */     
/* 276 */     itemCategory.setMenu(this.menuCategory);
/*     */     
/* 278 */     addCategorySubMenu();
/*     */     
/* 280 */     new MenuItem(menu, 2);
/*     */     
/* 282 */     MenuItem itemRemove = new MenuItem(menu, 8);
/* 283 */     Messages.setLanguageText(itemRemove, "MySharesView.menu.remove");
/* 284 */     Utils.setMenuItemImage(itemRemove, "delete");
/*     */     
/*     */ 
/* 287 */     Object[] shares = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 289 */     itemRemove.setEnabled(shares.length > 0);
/*     */     
/* 291 */     itemRemove.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 293 */         MySharesView.this.removeSelectedShares();
/*     */       }
/*     */       
/* 296 */     });
/* 297 */     new MenuItem(menu, 2);
/*     */   }
/*     */   
/*     */   public void resourceAdded(ShareResource resource) {
/* 301 */     this.tv.addDataSource(resource);
/*     */   }
/*     */   
/*     */   public void resourceModified(ShareResource old_resource, ShareResource new_resource) {
/* 305 */     this.tv.removeDataSource(old_resource);
/* 306 */     this.tv.addDataSource(new_resource);
/*     */   }
/*     */   
/*     */   public void resourceDeleted(ShareResource resource) {
/* 310 */     this.tv.removeDataSource(resource);
/*     */   }
/*     */   
/*     */   public void reportProgress(int percent_complete) {}
/*     */   
/*     */   public void reportCurrentTask(String task_description) {}
/*     */   
/*     */   public void tableRefresh() {
/* 318 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 319 */     if (uiFunctions != null) {
/* 320 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */   private void addCategorySubMenu() {
/* 325 */     MenuItem[] items = this.menuCategory.getItems();
/*     */     
/* 327 */     for (int i = 0; i < items.length; i++) {
/* 328 */       items[i].dispose();
/*     */     }
/*     */     
/* 331 */     Category[] categories = CategoryManager.getCategories();
/* 332 */     Arrays.sort(categories);
/*     */     
/* 334 */     if (categories.length > 0) {
/* 335 */       Category catUncat = CategoryManager.getCategory(2);
/* 336 */       if (catUncat != null) {
/* 337 */         MenuItem itemCategory = new MenuItem(this.menuCategory, 8);
/* 338 */         Messages.setLanguageText(itemCategory, catUncat.getName());
/* 339 */         itemCategory.setData("Category", catUncat);
/* 340 */         itemCategory.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 342 */             MenuItem item = (MenuItem)event.widget;
/* 343 */             MySharesView.this.assignSelectedToCategory((Category)item.getData("Category"));
/*     */           }
/*     */           
/* 346 */         });
/* 347 */         new MenuItem(this.menuCategory, 2);
/*     */       }
/*     */       
/* 350 */       for (i = 0; i < categories.length; i++) {
/* 351 */         if (categories[i].getType() == 0) {
/* 352 */           MenuItem itemCategory = new MenuItem(this.menuCategory, 8);
/* 353 */           itemCategory.setText(categories[i].getName());
/* 354 */           itemCategory.setData("Category", categories[i]);
/*     */           
/* 356 */           itemCategory.addListener(13, new Listener() {
/*     */             public void handleEvent(Event event) {
/* 358 */               MenuItem item = (MenuItem)event.widget;
/* 359 */               MySharesView.this.assignSelectedToCategory((Category)item.getData("Category"));
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/* 365 */       new MenuItem(this.menuCategory, 2);
/*     */     }
/*     */     
/* 368 */     MenuItem itemAddCategory = new MenuItem(this.menuCategory, 8);
/* 369 */     Messages.setLanguageText(itemAddCategory, "MyTorrentsView.menu.setCategory.add");
/*     */     
/*     */ 
/* 372 */     itemAddCategory.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 374 */         MySharesView.this.addCategory();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void addCategory()
/*     */   {
/* 381 */     CategoryAdderWindow adderWindow = new CategoryAdderWindow(Display.getDefault());
/* 382 */     Category newCategory = adderWindow.getNewCategory();
/* 383 */     if (newCategory != null)
/* 384 */       assignSelectedToCategory(newCategory);
/*     */   }
/*     */   
/*     */   private void assignSelectedToCategory(final Category category) {
/* 388 */     this.tv.runForSelectedRows(new TableGroupRowRunner() {
/*     */       public void run(TableRowCore row) {
/*     */         String value;
/*     */         String value;
/* 392 */         if (category == null)
/*     */         {
/* 394 */           value = null;
/*     */         } else { String value;
/* 396 */           if (category == CategoryManager.getCategory(2))
/*     */           {
/* 398 */             value = null;
/*     */           }
/*     */           else
/*     */           {
/* 402 */             value = category.getName();
/*     */           }
/*     */         }
/* 405 */         ((ShareResource)row.getDataSource(true)).setAttribute(MySharesView.category_attribute, value);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 412 */     boolean start = false;boolean stop = false;boolean remove = false;
/*     */     
/* 414 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 415 */       return;
/*     */     }
/*     */     
/* 418 */     List items = getSelectedItems();
/*     */     
/* 420 */     if (items.size() > 0)
/*     */     {
/* 422 */       PluginInterface pi = PluginInitializer.getDefaultInterface();
/*     */       
/* 424 */       org.gudy.azureus2.plugins.download.DownloadManager dm = pi.getDownloadManager();
/*     */       
/* 426 */       remove = true;
/*     */       
/* 428 */       for (int i = 0; i < items.size(); i++)
/*     */       {
/* 430 */         ShareItem item = (ShareItem)items.get(i);
/*     */         try
/*     */         {
/* 433 */           Torrent t = item.getTorrent();
/*     */           
/* 435 */           Download download = dm.getDownload(t);
/*     */           
/* 437 */           if (download != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 442 */             int dl_state = download.getState();
/*     */             
/* 444 */             if (dl_state != 8)
/*     */             {
/* 446 */               if (dl_state != 7)
/*     */               {
/* 448 */                 stop = true;
/*     */               }
/*     */               else
/*     */               {
/* 452 */                 start = true; }
/*     */             }
/*     */           }
/*     */         } catch (Throwable e) {
/* 456 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 461 */     list.put("start", Long.valueOf(start ? 1L : 0L));
/* 462 */     list.put("stop", Long.valueOf(stop ? 1L : 0L));
/* 463 */     list.put("remove", Long.valueOf(remove ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 469 */     String itemKey = item.getID();
/*     */     
/* 471 */     if (itemKey.equals("remove")) {
/* 472 */       removeSelectedShares();
/* 473 */       return true; }
/* 474 */     if (itemKey.equals("stop")) {
/* 475 */       stopSelectedShares();
/* 476 */       return true; }
/* 477 */     if (itemKey.equals("start")) {
/* 478 */       startSelectedShares();
/* 479 */       return true;
/*     */     }
/* 481 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private List getSelectedItems()
/*     */   {
/* 487 */     Object[] shares = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 489 */     List items = new ArrayList();
/*     */     
/* 491 */     if (shares.length > 0)
/*     */     {
/* 493 */       for (int i = 0; i < shares.length; i++)
/*     */       {
/* 495 */         ShareResource share = (ShareResource)shares[i];
/*     */         
/* 497 */         int type = share.getType();
/*     */         
/* 499 */         if (type == 2)
/*     */         {
/* 501 */           ShareResourceDir sr = (ShareResourceDir)share;
/*     */           
/* 503 */           items.add(sr.getItem());
/*     */         }
/* 505 */         else if (type == 1)
/*     */         {
/* 507 */           ShareResourceFile sr = (ShareResourceFile)share;
/*     */           
/* 509 */           items.add(sr.getItem());
/*     */         }
/*     */         else
/*     */         {
/* 513 */           ShareResourceDirContents cont = (ShareResourceDirContents)share;
/*     */           
/* 515 */           List entries = new ArrayList();
/*     */           
/* 517 */           getEntries(entries, cont);
/*     */           
/* 519 */           for (int j = 0; j < entries.size(); j++)
/*     */           {
/* 521 */             share = (ShareResource)entries.get(j);
/*     */             
/* 523 */             type = share.getType();
/*     */             
/* 525 */             if (type == 2)
/*     */             {
/* 527 */               ShareResourceDir sr = (ShareResourceDir)share;
/*     */               
/* 529 */               items.add(sr.getItem());
/*     */             }
/* 531 */             else if (type == 1)
/*     */             {
/* 533 */               ShareResourceFile sr = (ShareResourceFile)share;
/*     */               
/* 535 */               items.add(sr.getItem());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 542 */     return items;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getEntries(List entries, ShareResourceDirContents cont)
/*     */   {
/* 550 */     ShareResource[] kids = cont.getChildren();
/*     */     
/* 552 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 554 */       ShareResource share = kids[i];
/*     */       
/* 556 */       int type = share.getType();
/*     */       
/* 558 */       if (type == 3)
/*     */       {
/* 560 */         getEntries(entries, (ShareResourceDirContents)share);
/*     */       }
/*     */       else
/*     */       {
/* 564 */         entries.add(share);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void startStopSelectedShares(boolean do_stop)
/*     */   {
/* 573 */     List items = getSelectedItems();
/* 574 */     if (items.size() == 0) {
/* 575 */       return;
/*     */     }
/*     */     
/* 578 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*     */     
/* 580 */     org.gudy.azureus2.plugins.download.DownloadManager dm = pi.getDownloadManager();
/*     */     
/* 582 */     Tracker tracker = pi.getTracker();
/*     */     
/*     */ 
/* 585 */     for (int i = 0; i < items.size(); i++)
/*     */     {
/* 587 */       ShareItem item = (ShareItem)items.get(i);
/*     */       try
/*     */       {
/* 590 */         Torrent t = item.getTorrent();
/*     */         
/* 592 */         TrackerTorrent tracker_torrent = tracker.getTorrent(t);
/*     */         
/* 594 */         Download download = dm.getDownload(t);
/*     */         
/* 596 */         if ((tracker_torrent == null) || (download != null))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 601 */           int dl_state = download.getState();
/*     */           
/* 603 */           if (dl_state != 8)
/*     */           {
/* 605 */             if (dl_state != 7)
/*     */             {
/* 607 */               if (do_stop)
/*     */               {
/*     */                 try {
/* 610 */                   download.stop();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */                 try
/*     */                 {
/* 615 */                   tracker_torrent.stop();
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/*     */             }
/* 622 */             else if (!do_stop)
/*     */             {
/*     */               try {
/* 625 */                 download.restart();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */               try
/*     */               {
/* 630 */                 tracker_torrent.start();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (Throwable e) {
/* 637 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void startSelectedShares()
/*     */   {
/* 645 */     startStopSelectedShares(false);
/*     */   }
/*     */   
/*     */ 
/*     */   private void stopSelectedShares()
/*     */   {
/* 651 */     startStopSelectedShares(true);
/*     */   }
/*     */   
/*     */ 
/*     */   private void removeSelectedShares()
/*     */   {
/* 657 */     stopSelectedShares();
/* 658 */     Object[] shares = this.tv.getSelectedDataSources().toArray();
/* 659 */     for (int i = 0; i < shares.length; i++) {
/*     */       try {
/* 661 */         ((ShareResource)shares[i]).delete();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 665 */         Logger.log(new LogAlert(shares[i], false, "{globalmanager.download.remove.veto}", e));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */   public void defaultSelected(TableRowCore[] rows, int stateMask) {}
/*     */   
/*     */ 
/*     */   public void deselected(TableRowCore[] rows)
/*     */   {
/* 680 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 681 */     if (uiFunctions != null) {
/* 682 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */   public void focusChanged(TableRowCore focus)
/*     */   {
/* 688 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 689 */     if (uiFunctions != null) {
/* 690 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void mouseEnter(TableRowCore row) {}
/*     */   
/*     */ 
/*     */   public void mouseExit(TableRowCore row) {}
/*     */   
/*     */ 
/*     */   public void selected(TableRowCore[] row)
/*     */   {
/* 704 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 705 */     if (uiFunctions != null) {
/* 706 */       uiFunctions.refreshIconBar();
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getTitleInfoProperty(int propertyID) {
/* 711 */     return null;
/*     */   }
/*     */   
/*     */   public void titleInfoLinked(MultipleDocumentInterface mdi, MdiEntry mdiEntry)
/*     */   {
/* 716 */     mdiEntry.addListener(new MdiEntryDropListener() {
/*     */       public boolean mdiEntryDrop(MdiEntry entry, Object droppedObject) {
/* 718 */         return MySharesView.this.share(droppedObject);
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/MySharesView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */