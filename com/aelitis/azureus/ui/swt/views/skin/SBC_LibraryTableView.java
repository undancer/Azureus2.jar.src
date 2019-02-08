/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableSelectionAdapter;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedVuzeFileContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.columns.utils.TableColumnCreatorV3;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import com.aelitis.azureus.util.DataSourceUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableRowRefreshListener;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTorrentsSuperView;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableRowSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.utils.TableColumnCreator;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
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
/*     */ public class SBC_LibraryTableView
/*     */   extends SkinView
/*     */   implements UIUpdatable, ObfusticateImage, UIPluginViewToolBarListener
/*     */ {
/*     */   private static final String ID = "SBC_LibraryTableView";
/*     */   private Composite viewComposite;
/*     */   private TableViewSWT<?> tv;
/*  95 */   protected int torrentFilterMode = 0;
/*     */   
/*     */   private SWTSkinObject soParent;
/*     */   
/*     */   private MyTorrentsView torrentView;
/*     */   
/*     */   private UISWTViewEventListener swtViewListener;
/*     */   private UISWTViewImpl view;
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 106 */     this.soParent = skinObject.getParent();
/*     */     
/* 108 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(final AzureusCore core) {
/* 110 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 112 */             if ((SBC_LibraryTableView.this.soParent == null) || (SBC_LibraryTableView.this.soParent.isDisposed())) {
/* 113 */               return;
/*     */             }
/* 115 */             SBC_LibraryTableView.this.initShow(core);
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 120 */     });
/* 121 */     return null;
/*     */   }
/*     */   
/*     */   public void initShow(AzureusCore core) {
/* 125 */     Object data = this.soParent.getControl().getData("TorrentFilterMode");
/* 126 */     if ((data instanceof Long)) {
/* 127 */       this.torrentFilterMode = ((int)((Long)data).longValue());
/*     */     }
/*     */     
/* 130 */     data = this.soParent.getControl().getData("DataSource");
/*     */     
/* 132 */     boolean useBigTable = useBigTable();
/*     */     
/* 134 */     SWTSkinObjectTextbox soFilter = (SWTSkinObjectTextbox)this.skin.getSkinObject("library-filter", this.soParent.getParent());
/*     */     
/* 136 */     Text txtFilter = soFilter == null ? null : soFilter.getTextControl();
/*     */     
/* 138 */     SWTSkinObjectContainer soCats = (SWTSkinObjectContainer)this.skin.getSkinObject("library-categories", this.soParent.getParent());
/*     */     
/* 140 */     Composite cCats = soCats == null ? null : soCats.getComposite();
/*     */     
/*     */ 
/* 143 */     TableColumnCore[] columns = (useBigTable) || (this.torrentFilterMode != 0) ? getColumns() : null;
/*     */     
/*     */ 
/*     */ 
/* 147 */     if (null != columns) {
/* 148 */       TableColumnManager tcManager = TableColumnManager.getInstance();
/* 149 */       tcManager.addColumns(columns);
/*     */     }
/*     */     
/* 152 */     if (useBigTable) {
/* 153 */       if ((this.torrentFilterMode == 1) || (this.torrentFilterMode == 2) || (this.torrentFilterMode == 3))
/*     */       {
/*     */ 
/*     */ 
/* 157 */         this.swtViewListener = (this.torrentView = new MyTorrentsView_Big(core, this.torrentFilterMode, columns, txtFilter, cCats));
/*     */       }
/*     */       else
/*     */       {
/* 161 */         this.swtViewListener = (this.torrentView = new MyTorrentsView_Big(core, this.torrentFilterMode, columns, txtFilter, cCats));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 166 */       String tableID = SB_Transfers.getTableIdFromFilterMode(this.torrentFilterMode, false);
/*     */       
/* 168 */       if (this.torrentFilterMode == 1) {
/* 169 */         this.swtViewListener = (this.torrentView = new MyTorrentsView(core, tableID, true, columns, txtFilter, cCats, true));
/*     */ 
/*     */       }
/* 172 */       else if (this.torrentFilterMode == 2) {
/* 173 */         this.swtViewListener = (this.torrentView = new MyTorrentsView(core, tableID, false, columns, txtFilter, cCats, true));
/*     */ 
/*     */       }
/* 176 */       else if (this.torrentFilterMode == 3) {
/* 177 */         this.swtViewListener = (this. = new MyTorrentsView(core, tableID, true, columns, txtFilter, cCats, true)
/*     */         {
/*     */           public boolean isOurDownloadManager(DownloadManager dm) {
/* 180 */             if (PlatformTorrentUtils.getHasBeenOpened(dm)) {
/* 181 */               return false;
/*     */             }
/* 183 */             return super.isOurDownloadManager(dm);
/*     */           }
/*     */         });
/*     */       } else {
/* 187 */         this.swtViewListener = new MyTorrentsSuperView(txtFilter, cCats) {
/*     */           public void initializeDone() {
/* 189 */             MyTorrentsView seedingview = getSeedingview();
/* 190 */             if (seedingview != null) {
/* 191 */               seedingview.overrideDefaultSelected(new TableSelectionAdapter() {
/*     */                 public void defaultSelected(TableRowCore[] rows, int stateMask) {
/* 193 */                   SBC_LibraryTableView.doDefaultClick(rows, stateMask, false);
/*     */                 }
/* 195 */               });
/* 196 */               MyTorrentsView torrentview = getTorrentview();
/* 197 */               if (torrentview != null) {
/* 198 */                 torrentview.overrideDefaultSelected(new TableSelectionAdapter() {
/*     */                   public void defaultSelected(TableRowCore[] rows, int stateMask) {
/* 200 */                     SBC_LibraryTableView.doDefaultClick(rows, stateMask, false);
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }
/*     */         };
/*     */       }
/*     */       
/* 209 */       if (this.torrentView != null) {
/* 210 */         this.torrentView.overrideDefaultSelected(new TableSelectionAdapter() {
/*     */           public void defaultSelected(TableRowCore[] rows, int stateMask) {
/* 212 */             SBC_LibraryTableView.doDefaultClick(rows, stateMask, false);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/* 218 */     if (this.torrentView != null) {
/* 219 */       this.tv = this.torrentView.getTableView();
/* 220 */       if (this.torrentFilterMode == 3) {
/* 221 */         this.torrentView.setRebuildListOnFocusGain(true);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 226 */       this.view = new UISWTViewImpl("SBC_LibraryTableView" + this.torrentFilterMode, "Main", false);
/* 227 */       this.view.setDatasource(data);
/* 228 */       this.view.setEventListener(this.swtViewListener, true);
/*     */     } catch (Exception e) {
/* 230 */       Debug.out(e);
/*     */     }
/*     */     
/* 233 */     SWTSkinObjectContainer soContents = new SWTSkinObjectContainer(this.skin, this.skin.getSkinProperties(), getUpdateUIName(), "", this.soMain);
/*     */     
/*     */ 
/* 236 */     this.skin.layout();
/*     */     
/* 238 */     this.viewComposite = soContents.getComposite();
/* 239 */     this.viewComposite.setLayoutData(Utils.getFilledFormData());
/* 240 */     GridLayout gridLayout = new GridLayout();
/* 241 */     gridLayout.horizontalSpacing = (gridLayout.verticalSpacing = gridLayout.marginHeight = gridLayout.marginWidth = 0);
/* 242 */     this.viewComposite.setLayout(gridLayout);
/*     */     
/* 244 */     this.view.initialize(this.viewComposite);
/*     */     
/*     */ 
/* 247 */     SWTSkinObject soSizeSlider = this.skin.getSkinObject("table-size-slider", this.soParent.getParent());
/* 248 */     if ((soSizeSlider instanceof SWTSkinObjectContainer)) {
/* 249 */       SWTSkinObjectContainer so = (SWTSkinObjectContainer)soSizeSlider;
/* 250 */       if ((this.tv != null) && (!this.tv.enableSizeSlider(so.getComposite(), 16, 100))) {
/* 251 */         so.setVisible(false);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 256 */     if ((this.torrentFilterMode == 0) && (this.tv != null))
/*     */     {
/* 258 */       this.tv.addRefreshListener(new TableRowRefreshListener() {
/*     */         public void rowRefresh(TableRow row) {
/* 260 */           TableRowSWT rowCore = (TableRowSWT)row;
/* 261 */           Object ds = rowCore.getDataSource(true);
/* 262 */           if (!(ds instanceof DownloadManager)) {
/* 263 */             return;
/*     */           }
/* 265 */           DownloadManager dm = (DownloadManager)ds;
/* 266 */           boolean changed = false;
/* 267 */           boolean assumedComplete = dm.getAssumedComplete();
/* 268 */           if (!assumedComplete) {
/* 269 */             changed |= rowCore.setAlpha(160);
/* 270 */           } else if (!PlatformTorrentUtils.getHasBeenOpened(dm)) {
/* 271 */             changed |= rowCore.setAlpha(255);
/*     */           } else {
/* 273 */             changed |= rowCore.setAlpha(255);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 279 */     this.viewComposite.getParent().layout(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void doDefaultClick(TableRowCore[] rows, int stateMask, boolean neverPlay)
/*     */   {
/* 288 */     if ((rows == null) || (rows.length != 1)) {
/* 289 */       return;
/*     */     }
/*     */     
/* 292 */     Object ds = rows[0].getDataSource(true);
/*     */     
/* 294 */     boolean webInBrowser = COConfigurationManager.getBooleanParameter("Library.LaunchWebsiteInBrowser");
/*     */     
/* 296 */     if (webInBrowser)
/*     */     {
/* 298 */       DiskManagerFileInfo fileInfo = DataSourceUtils.getFileInfo(ds);
/*     */       
/* 300 */       if (fileInfo != null)
/*     */       {
/* 302 */         if (!ManagerUtils.browseWebsite(fileInfo)) {}
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 308 */         DownloadManager dm = DataSourceUtils.getDM(ds);
/*     */         
/* 310 */         if (dm != null)
/*     */         {
/* 312 */           if (ManagerUtils.browseWebsite(dm))
/*     */           {
/* 314 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 320 */     String mode = COConfigurationManager.getStringParameter("list.dm.dblclick");
/*     */     
/* 322 */     if (mode.equals("1"))
/*     */     {
/*     */ 
/*     */ 
/* 326 */       if (!UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("DMDetails", ds)) {}
/*     */ 
/*     */ 
/*     */     }
/* 330 */     else if (mode.equals("2"))
/*     */     {
/*     */ 
/*     */ 
/* 334 */       boolean openMode = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/* 335 */       DiskManagerFileInfo file = DataSourceUtils.getFileInfo(ds);
/* 336 */       if (file != null) {
/* 337 */         ManagerUtils.open(file, openMode);
/* 338 */         return;
/*     */       }
/* 340 */       DownloadManager dm = DataSourceUtils.getDM(ds);
/* 341 */       if (dm != null) {
/* 342 */         ManagerUtils.open(dm, openMode);
/* 343 */         return;
/*     */       }
/* 345 */     } else if ((mode.equals("3")) || (mode.equals("4")))
/*     */     {
/*     */ 
/* 348 */       DiskManagerFileInfo file = DataSourceUtils.getFileInfo(ds);
/* 349 */       if (file != null) {
/* 350 */         if ((mode.equals("4")) && (file.getDownloaded() == file.getLength()) && (Utils.isQuickViewSupported(file)))
/*     */         {
/*     */ 
/*     */ 
/* 354 */           Utils.setQuickViewActive(file, true);
/*     */         } else {
/* 356 */           TorrentUtil.runDataSources(new Object[] { file });
/*     */         }
/* 358 */         return;
/*     */       }
/* 360 */       DownloadManager dm = DataSourceUtils.getDM(ds);
/* 361 */       if (dm != null) {
/* 362 */         TorrentUtil.runDataSources(new Object[] { dm });
/* 363 */         return;
/*     */       }
/* 365 */     } else if (mode.equals("5")) {
/* 366 */       DiskManagerFileInfo fileInfo = DataSourceUtils.getFileInfo(ds);
/* 367 */       if (fileInfo != null) {
/* 368 */         ManagerUtils.browse(fileInfo);
/* 369 */         return;
/*     */       }
/* 371 */       DownloadManager dm = DataSourceUtils.getDM(ds);
/* 372 */       if (dm != null) {
/* 373 */         ManagerUtils.browse(dm);
/* 374 */         return;
/*     */       }
/*     */     }
/*     */     
/* 378 */     if (neverPlay) {
/* 379 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 384 */     if ((PlayUtils.canPlayDS(ds, -1, true)) || ((stateMask & 0x40000) != 0)) {
/* 385 */       TorrentListViewsUtils.playOrStreamDataSource(ds, "dblclick", false, true);
/*     */       
/* 387 */       return;
/*     */     }
/*     */     
/* 390 */     if (PlayUtils.canStreamDS(ds, -1, true)) {
/* 391 */       TorrentListViewsUtils.playOrStreamDataSource(ds, "dblclick", true, false);
/*     */       
/* 393 */       return;
/*     */     }
/*     */     
/* 396 */     DownloadManager dm = DataSourceUtils.getDM(ds);
/* 397 */     DiskManagerFileInfo file = DataSourceUtils.getFileInfo(ds);
/* 398 */     TOTorrent torrent = DataSourceUtils.getTorrent(ds);
/* 399 */     if ((torrent == null) && (file != null)) {
/* 400 */       DownloadManager dmFile = file.getDownloadManager();
/* 401 */       if (dmFile != null) {
/* 402 */         torrent = dmFile.getTorrent();
/*     */       }
/*     */     }
/* 405 */     if ((file != null) && (file.getDownloaded() == file.getLength())) {
/* 406 */       TorrentUtil.runDataSources(new Object[] { file });
/* 407 */     } else if (dm != null) {
/* 408 */       TorrentUtil.runDataSources(new Object[] { dm });
/*     */     }
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 414 */     return "SBC_LibraryTableView";
/*     */   }
/*     */   
/*     */   public void updateUI()
/*     */   {
/* 419 */     if ((this.viewComposite == null) || (this.viewComposite.isDisposed()) || (!this.viewComposite.isVisible()) || (this.view == null))
/*     */     {
/* 421 */       return;
/*     */     }
/* 423 */     this.view.triggerEvent(5, null);
/*     */   }
/*     */   
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/* 428 */     super.skinObjectShown(skinObject, params);
/*     */     
/* 430 */     if (this.view != null) {
/* 431 */       this.view.triggerEvent(3, null);
/*     */     }
/*     */     
/* 434 */     Utils.execSWTThreadLater(0, new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 437 */         SBC_LibraryTableView.this.updateUI();
/*     */       }
/*     */       
/* 440 */     });
/* 441 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/* 446 */     if (this.view != null) {
/* 447 */       this.view.triggerEvent(4, null);
/*     */     }
/*     */     
/* 450 */     return super.skinObjectHidden(skinObject, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 457 */     if (!isVisible()) {
/* 458 */       return;
/*     */     }
/* 460 */     if (this.view != null) {
/* 461 */       this.view.refreshToolBarItems(list);
/*     */     }
/* 463 */     if (this.tv == null) {
/* 464 */       return;
/*     */     }
/* 466 */     ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
/* 467 */     boolean has1Selection = currentContent.length == 1;
/* 468 */     list.put("play", Long.valueOf((has1Selection) && (!(currentContent[0] instanceof ISelectedVuzeFileContent)) && (PlayUtils.canPlayDS(currentContent[0], currentContent[0].getFileIndex(), false)) ? 1L : 0L));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 475 */     list.put("stream", Long.valueOf((has1Selection) && (!(currentContent[0] instanceof ISelectedVuzeFileContent)) && (PlayUtils.canStreamDS(currentContent[0], currentContent[0].getFileIndex(), false)) ? 1L : 0L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 486 */     if ((isVisible()) && (this.view != null)) {
/* 487 */       return this.view.toolBarItemActivated(item, activationType, datasource);
/*     */     }
/* 489 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getTableMode()
/*     */   {
/* 498 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean useBigTable()
/*     */   {
/* 507 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TableColumnCore[] getColumns()
/*     */   {
/* 516 */     if (this.torrentFilterMode == 1)
/* 517 */       return TableColumnCreator.createCompleteDM("MySeeders");
/* 518 */     if (this.torrentFilterMode == 2)
/* 519 */       return TableColumnCreator.createIncompleteDM("MyTorrents");
/* 520 */     if (this.torrentFilterMode == 3) {
/* 521 */       return TableColumnCreatorV3.createUnopenedDM("Unopened", false);
/*     */     }
/* 523 */     if (this.torrentFilterMode == 0) {
/* 524 */       return TableColumnCreator.createCompleteDM("MyLibrary.big");
/*     */     }
/*     */     
/* 527 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 532 */     if (this.view != null) {
/* 533 */       this.view.triggerEvent(7, null);
/*     */     }
/* 535 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */   public Image obfusticatedImage(Image image)
/*     */   {
/* 540 */     if ((this.view instanceof ObfusticateImage)) {
/* 541 */       ObfusticateImage oi = (ObfusticateImage)this.view;
/* 542 */       return oi.obfusticatedImage(image);
/*     */     }
/* 544 */     return image;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_LibraryTableView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */