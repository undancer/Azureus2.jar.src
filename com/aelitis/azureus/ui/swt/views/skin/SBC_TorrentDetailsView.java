/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener2;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryDatasourceListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiSWTMenuHackListener;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.TabbedMdiInterface;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.util.DataSourceUtils;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.debug.ObfusticateTab;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance.UISWTViewEventListenerWrapper;
/*     */ import org.gudy.azureus2.ui.swt.views.MyTorrentsView;
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
/*     */ public class SBC_TorrentDetailsView
/*     */   extends SkinView
/*     */   implements DownloadManagerListener, UIPluginViewToolBarListener, SelectedContentListener
/*     */ {
/*     */   private DownloadManager manager;
/*     */   private TabbedMdiInterface tabbedMDI;
/*     */   private Composite parent;
/*     */   private MdiEntrySWT mdi_entry;
/*     */   private Object dataSource;
/*     */   
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/*  97 */     this.dataSource = newDataSource;
/*     */     
/*  99 */     if (this.manager != null) {
/* 100 */       this.manager.removeListener(this);
/*     */     }
/*     */     
/* 103 */     this.manager = DataSourceUtils.getDM(newDataSource);
/*     */     
/* 105 */     if ((this.tabbedMDI != null) && ((newDataSource instanceof Object[])) && ((((Object[])(Object[])newDataSource)[0] instanceof PEPeer)))
/*     */     {
/* 107 */       this.tabbedMDI.showEntryByID("PeersView");
/*     */     }
/*     */     
/* 110 */     if (this.manager != null) {
/* 111 */       this.manager.addListener(this);
/*     */     }
/*     */     
/* 114 */     if (this.tabbedMDI != null) {
/* 115 */       MdiEntry[] entries = this.tabbedMDI.getEntries();
/* 116 */       for (MdiEntry entry : entries) {
/* 117 */         entry.setDatasource(newDataSource);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void delete() {
/* 123 */     if (this.manager != null) {
/* 124 */       this.manager.removeListener(this);
/*     */     }
/*     */     
/* 127 */     SelectedContentManager.removeCurrentlySelectedContentListener(this);
/*     */     
/* 129 */     Utils.disposeSWTObjects(new Object[] { this.parent });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void initialize(Composite composite)
/*     */   {
/* 136 */     Composite main_area = new Composite(composite, 0);
/* 137 */     main_area.setLayout(new FormLayout());
/*     */     
/*     */ 
/*     */ 
/* 141 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */     
/* 143 */     this.parent = composite;
/* 144 */     if (this.tabbedMDI == null) {
/* 145 */       this.tabbedMDI = uiFunctions.createTabbedMDI(main_area, "detailsview");
/*     */     } else {
/* 147 */       System.out.println("ManagerView::initialize : folder isn't null !!!");
/*     */     }
/*     */     
/* 150 */     if ((composite.getLayout() instanceof FormLayout)) {
/* 151 */       main_area.setLayoutData(Utils.getFilledFormData());
/* 152 */     } else if ((composite.getLayout() instanceof GridLayout)) {
/* 153 */       main_area.setLayoutData(new GridData(1808));
/*     */     }
/* 155 */     composite.layout();
/*     */     
/*     */ 
/* 158 */     if (uiFunctions != null) {
/* 159 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 161 */       if (pluginUI != null)
/*     */       {
/* 163 */         MyTorrentsView.registerPluginViews(pluginUI);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 168 */         for (String id : new String[] { "MyTorrents", "TorrentDetailsView" })
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 173 */           UISWTInstance.UISWTViewEventListenerWrapper[] pluginViews = pluginUI.getViewListeners(id);
/*     */           
/* 175 */           for (UISWTInstance.UISWTViewEventListenerWrapper l : pluginViews)
/*     */           {
/* 177 */             if ((id != "MyTorrents") || (l.getViewID() != "PieceInfoView"))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 183 */               if (l != null) {
/*     */                 try
/*     */                 {
/* 186 */                   this.tabbedMDI.createEntryFromEventListener(null, "TorrentDetailsView", l, l.getViewID(), false, this.manager, null);
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */ 
/* 192 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 200 */     SelectedContentManager.addCurrentlySelectedContentListener(this);
/*     */     
/* 202 */     this.tabbedMDI.addListener(new MdiSWTMenuHackListener()
/*     */     {
/*     */       public void menuWillBeShown(MdiEntry entry, Menu menuTree) {
/* 205 */         menuTree.setData("downloads", new DownloadManager[] { SBC_TorrentDetailsView.this.manager });
/*     */         
/*     */ 
/* 208 */         menuTree.setData("is_detailed_view", Boolean.valueOf(true));
/*     */         
/* 210 */         MenuFactory.buildTorrentMenu(menuTree);
/*     */       }
/*     */     });
/*     */     
/* 214 */     if (((this.dataSource instanceof Object[])) && ((((Object[])(Object[])this.dataSource)[0] instanceof PEPeer)))
/*     */     {
/* 216 */       this.tabbedMDI.showEntryByID("PeersView");
/*     */     } else {
/* 218 */       MdiEntry[] entries = this.tabbedMDI.getEntries();
/* 219 */       if (entries.length > 0) {
/* 220 */         this.tabbedMDI.showEntry(entries[0]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewId) {}
/*     */   
/*     */ 
/*     */ 
/*     */   private void refresh()
/*     */   {
/* 234 */     this.tabbedMDI.updateUI();
/*     */   }
/*     */   
/*     */   protected static String escapeAccelerators(String str) {
/* 238 */     if (str == null)
/*     */     {
/* 240 */       return str;
/*     */     }
/*     */     
/* 243 */     return str.replaceAll("&", "&&");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void refreshToolBarItems(Map<String, Long> list)
/*     */   {
/* 250 */     BaseMdiEntry activeView = getActiveView();
/* 251 */     if (activeView == null) {
/* 252 */       return;
/*     */     }
/* 254 */     activeView.refreshToolBarItems(list);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource)
/*     */   {
/* 262 */     BaseMdiEntry activeView = getActiveView();
/* 263 */     if (activeView == null) {
/* 264 */       return false;
/*     */     }
/* 266 */     return activeView.toolBarItemActivated(item, activationType, datasource);
/*     */   }
/*     */   
/*     */ 
/*     */   public void downloadComplete(DownloadManager manager) {}
/*     */   
/*     */ 
/*     */   public void completionChanged(DownloadManager manager, boolean bCompleted) {}
/*     */   
/*     */ 
/*     */   public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file) {}
/*     */   
/*     */   public void stateChanged(DownloadManager manager, int state)
/*     */   {
/* 280 */     if ((this.tabbedMDI == null) || (this.tabbedMDI.isDisposed())) {
/* 281 */       return;
/*     */     }
/* 283 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 285 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 286 */         if (uiFunctions != null) {
/* 287 */           uiFunctions.refreshIconBar();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void positionChanged(DownloadManager download, int oldPosition, int newPosition) {}
/*     */   
/*     */   public DownloadManager getDownload()
/*     */   {
/* 298 */     return this.manager;
/*     */   }
/*     */   
/*     */   public boolean isSelected(String itemKey)
/*     */   {
/* 303 */     return false;
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 308 */     return "DMDetails";
/*     */   }
/*     */   
/*     */   public void updateUI() {
/* 312 */     refresh();
/*     */   }
/*     */   
/*     */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*     */   {
/* 317 */     SWTSkinObject soListArea = getSkinObject("torrentdetails-list-area");
/* 318 */     if (soListArea == null) {
/* 319 */       return null;
/*     */     }
/*     */     
/* 322 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*     */     
/* 324 */     if (mdi != null)
/*     */     {
/* 326 */       this.mdi_entry = mdi.getEntryFromSkinObject(skinObject);
/*     */       
/* 328 */       if (this.mdi_entry == null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 333 */         Debug.out("Failed to get MDI entry from skin object, reverting to using 'current'");
/*     */         
/* 335 */         this.mdi_entry = mdi.getCurrentEntrySWT();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 340 */     initialize((Composite)soListArea.getControl());
/* 341 */     return null;
/*     */   }
/*     */   
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/* 346 */     delete();
/* 347 */     return super.skinObjectDestroyed(skinObject, params);
/*     */   }
/*     */   
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params)
/*     */   {
/* 352 */     dataSourceChanged(params);
/* 353 */     return null;
/*     */   }
/*     */   
/*     */   private BaseMdiEntry getActiveView() {
/* 357 */     if ((this.tabbedMDI == null) || (this.tabbedMDI.isDisposed())) {
/* 358 */       return null;
/*     */     }
/* 360 */     return (BaseMdiEntry)this.tabbedMDI.getCurrentEntrySWT();
/*     */   }
/*     */   
/*     */ 
/*     */   public static class TorrentDetailMdiEntry
/*     */     implements MdiSWTMenuHackListener, MdiCloseListener, MdiEntryDatasourceListener, UIUpdatable, ViewTitleInfo, ObfusticateTab
/*     */   {
/* 367 */     int lastCompleted = -1;
/*     */     
/*     */     protected GlobalManagerAdapter gmListener;
/*     */     private BaseMdiEntry entry;
/*     */     
/*     */     public static void register(MultipleDocumentInterfaceSWT mdi)
/*     */     {
/* 374 */       mdi.registerEntry("DMDetails.*", new MdiEntryCreationListener2()
/*     */       {
/*     */         public MdiEntry createMDiEntry(MultipleDocumentInterface mdi, String id, Object datasource, Map<?, ?> params)
/*     */         {
/* 378 */           String hash = DataSourceUtils.getHash(datasource);
/* 379 */           if (hash != null) {
/* 380 */             id = "DMDetails_" + hash;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 386 */           return new SBC_TorrentDetailsView.TorrentDetailMdiEntry().createTorrentDetailEntry(mdi, id, datasource);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public MdiEntry createTorrentDetailEntry(MultipleDocumentInterface mdi, String id, Object ds)
/*     */     {
/* 394 */       if (ds == null) {
/* 395 */         return null;
/*     */       }
/* 397 */       this.entry = ((BaseMdiEntry)mdi.createEntryFromSkinRef("header.transfers", id, "torrentdetails", "", null, ds, true, null));
/*     */       
/*     */ 
/* 400 */       this.entry.addListeners(this);
/* 401 */       this.entry.setViewTitleInfo(this);
/*     */       
/* 403 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */       {
/*     */         public void azureusCoreRunning(AzureusCore core) {
/* 406 */           GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 407 */           SBC_TorrentDetailsView.TorrentDetailMdiEntry.this.gmListener = new GlobalManagerAdapter() {
/*     */             public void downloadManagerRemoved(DownloadManager dm) {
/* 409 */               Object ds = SBC_TorrentDetailsView.TorrentDetailMdiEntry.this.entry.getDatasourceCore();
/* 410 */               DownloadManager manager = DataSourceUtils.getDM(ds);
/* 411 */               if (dm.equals(manager)) {
/* 412 */                 Utils.execSWTThread(new AERunnable() {
/*     */                   public void runSupport() {
/* 414 */                     SBC_TorrentDetailsView.TorrentDetailMdiEntry.this.entry.closeView();
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/* 419 */           };
/* 420 */           gm.addListener(SBC_TorrentDetailsView.TorrentDetailMdiEntry.this.gmListener, false);
/*     */         }
/*     */         
/* 423 */       });
/* 424 */       UIFunctionsManager.getUIFunctions().getUIUpdater().addUpdater(this);
/*     */       
/* 426 */       return this.entry;
/*     */     }
/*     */     
/*     */     public Object getTitleInfoProperty(int propertyID)
/*     */     {
/* 431 */       Object ds = this.entry.getDatasourceCore();
/* 432 */       if (propertyID == 10)
/* 433 */         return DataSourceUtils.getHash(ds);
/* 434 */       if (propertyID == 7)
/* 435 */         return "DMDetails";
/* 436 */       if (propertyID == 2) {
/* 437 */         return "image.sidebar.details";
/*     */       }
/*     */       
/* 440 */       DownloadManager manager = DataSourceUtils.getDM(ds);
/* 441 */       if (manager == null) {
/* 442 */         return null;
/*     */       }
/*     */       
/* 445 */       if (propertyID == 5) {
/* 446 */         return manager.getDisplayName();
/*     */       }
/*     */       
/* 449 */       if (propertyID == 0) {
/* 450 */         int completed = manager.getStats().getPercentDoneExcludingDND();
/* 451 */         if (completed != 1000) {
/* 452 */           return completed / 10 + "%";
/*     */         }
/* 454 */       } else if (propertyID == 1) {
/* 455 */         String s = "";
/* 456 */         int completed = manager.getStats().getPercentDoneExcludingDND();
/* 457 */         if (completed != 1000) {
/* 458 */           s = completed / 10 + "% Complete\n";
/*     */         }
/* 460 */         String eta = DisplayFormatters.formatETA(manager.getStats().getSmoothedETA());
/*     */         
/* 462 */         if (eta.length() > 0) {
/* 463 */           s = s + MessageText.getString("TableColumn.header.eta") + ": " + eta + "\n";
/*     */         }
/*     */         
/*     */ 
/* 467 */         return manager.getDisplayName() + (s.length() == 0 ? "" : new StringBuilder().append(": ").append(s).toString());
/*     */       }
/* 469 */       return null;
/*     */     }
/*     */     
/*     */     public void updateUI()
/*     */     {
/* 474 */       DownloadManager manager = DataSourceUtils.getDM(this.entry.getDatasourceCore());
/* 475 */       int completed = manager == null ? -1 : manager.getStats().getPercentDoneExcludingDND();
/*     */       
/* 477 */       if (this.lastCompleted != completed) {
/* 478 */         ViewTitleInfoManager.refreshTitleInfo(this);
/* 479 */         this.lastCompleted = completed;
/*     */       }
/*     */     }
/*     */     
/*     */     public String getUpdateUIName()
/*     */     {
/* 485 */       return this.entry == null ? "DMD" : this.entry.getId();
/*     */     }
/*     */     
/*     */     public void mdiEntryClosed(MdiEntry entry, boolean userClosed)
/*     */     {
/* 490 */       UIFunctionsManager.getUIFunctions().getUIUpdater().removeUpdater(this);
/*     */       try {
/* 492 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 493 */         gm.removeListener(this.gmListener);
/*     */       } catch (Exception e) {
/* 495 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */     public void mdiEntryDatasourceChanged(final MdiEntry entry) {
/* 500 */       Object newDataSource = ((BaseMdiEntry)entry).getDatasourceCore();
/* 501 */       if ((newDataSource instanceof String)) {
/* 502 */         final String s = (String)newDataSource;
/* 503 */         if (!AzureusCoreFactory.isCoreRunning()) {
/* 504 */           AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */           {
/*     */             public void azureusCoreRunning(AzureusCore core) {
/* 507 */               entry.setDatasource(DataSourceUtils.getDM(s));
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/* 513 */       ViewTitleInfoManager.refreshTitleInfo(this);
/*     */     }
/*     */     
/*     */     public void menuWillBeShown(MdiEntry entry, Menu menuTree)
/*     */     {
/* 518 */       TableView<?> tv = SelectedContentManager.getCurrentlySelectedTableView();
/* 519 */       menuTree.setData("TableView", tv);
/* 520 */       DownloadManager manager = DataSourceUtils.getDM(((BaseMdiEntry)entry).getDatasourceCore());
/* 521 */       if (manager != null) {
/* 522 */         menuTree.setData("downloads", new DownloadManager[] { manager });
/*     */       }
/*     */       
/*     */ 
/* 526 */       menuTree.setData("is_detailed_view", Boolean.TRUE);
/*     */       
/* 528 */       MenuFactory.buildTorrentMenu(menuTree);
/*     */     }
/*     */     
/*     */     public String getObfusticatedHeader()
/*     */     {
/* 533 */       Object ds = this.entry.getDatasourceCore();
/* 534 */       DownloadManager manager = DataSourceUtils.getDM(ds);
/* 535 */       if (manager == null) {
/* 536 */         return null;
/*     */       }
/* 538 */       int completed = manager.getStats().getCompleted();
/* 539 */       return DisplayFormatters.formatPercentFromThousands(completed) + " : " + manager.toString().replaceFirst("DownloadManagerImpl", "DM");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/SBC_TorrentDetailsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */