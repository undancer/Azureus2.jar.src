/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableDataSourceChangedListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableLifeCycleListener;
/*     */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*     */ import com.aelitis.azureus.ui.common.table.TableView;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerTPSListener;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.maketorrent.MultiTrackerEditor;
/*     */ import org.gudy.azureus2.ui.swt.maketorrent.TrackerEditorListener;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventImpl;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableSelectedRowsListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWTMenuFillListener;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewFactory;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;
/*     */ import org.gudy.azureus2.ui.swt.views.table.impl.TableViewTab;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.CompletedItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.IntervalItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.LastUpdateItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.LeechersItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.NameItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.PeersItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.SeedsItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.StatusItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.TypeItem;
/*     */ import org.gudy.azureus2.ui.swt.views.tableitems.tracker.UpdateInItem;
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
/*     */ public class TrackerView
/*     */   extends TableViewTab<TrackerPeerSource>
/*     */   implements TableLifeCycleListener, TableDataSourceChangedListener, DownloadManagerTPSListener, TableViewSWTMenuFillListener
/*     */ {
/*  69 */   private static boolean registeredCoreSubViews = false;
/*     */   
/*  71 */   private static final TableColumnCore[] basicItems = { new TypeItem("Trackers"), new NameItem("Trackers"), new StatusItem("Trackers"), new PeersItem("Trackers"), new SeedsItem("Trackers"), new LeechersItem("Trackers"), new CompletedItem("Trackers"), new UpdateInItem("Trackers"), new IntervalItem("Trackers"), new LastUpdateItem("Trackers") };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String MSGID_PREFIX = "TrackerView";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private DownloadManager manager;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  87 */   private boolean enable_tabs = true;
/*     */   
/*     */ 
/*     */   private TableViewSWT<TrackerPeerSource> tv;
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerView()
/*     */   {
/*  96 */     super("TrackerView");
/*     */   }
/*     */   
/*     */ 
/*     */   public TableViewSWT<TrackerPeerSource> initYourTableView()
/*     */   {
/* 102 */     this.tv = TableViewFactory.createTableViewSWT(TrackerPeerSource.class, "Trackers", getPropertiesPrefix(), basicItems, basicItems[0].getName(), 268500994);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 110 */     this.tv.addLifeCycleListener(this);
/* 111 */     this.tv.addMenuFillListener(this);
/* 112 */     this.tv.addTableDataSourceChangedListener(this, true);
/*     */     
/* 114 */     this.tv.setEnableTabViews(this.enable_tabs, true, null);
/*     */     
/* 116 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 117 */     if (uiFunctions != null) {
/* 118 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 120 */       if ((pluginUI != null) && (!registeredCoreSubViews))
/*     */       {
/* 122 */         pluginUI.addView("Trackers", "ScrapeInfoView", ScrapeInfoView.class, this.manager);
/*     */         
/*     */ 
/* 125 */         registeredCoreSubViews = true;
/*     */       }
/*     */     }
/*     */     
/* 129 */     return this.tv;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillMenu(String sColumnName, Menu menu)
/*     */   {
/* 137 */     final Object[] sources = this.tv.getSelectedDataSources().toArray();
/*     */     
/* 139 */     boolean found_tracker = false;
/* 140 */     boolean found_dht_tracker = false;
/* 141 */     boolean update_ok = false;
/* 142 */     boolean delete_ok = false;
/*     */     
/* 144 */     for (Object o : sources)
/*     */     {
/* 146 */       TrackerPeerSource ps = (TrackerPeerSource)o;
/*     */       
/* 148 */       if (ps.getType() == 1)
/*     */       {
/* 150 */         found_tracker = true;
/*     */       }
/*     */       
/*     */ 
/* 154 */       if (ps.getType() == 3)
/*     */       {
/* 156 */         found_dht_tracker = true;
/*     */       }
/*     */       
/* 159 */       int state = ps.getStatus();
/*     */       
/* 161 */       if (((state == 5) || (state == 3) || (state == 6)) && (!ps.isUpdating()) && (ps.canManuallyUpdate()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */         update_ok = true;
/*     */       }
/*     */       
/* 170 */       if (ps.canDelete())
/*     */       {
/* 172 */         delete_ok = true;
/*     */       }
/*     */     }
/*     */     
/* 176 */     boolean needs_sep = false;
/*     */     
/* 178 */     if ((found_tracker) || (found_dht_tracker))
/*     */     {
/* 180 */       MenuItem update_item = new MenuItem(menu, 8);
/*     */       
/* 182 */       Messages.setLanguageText(update_item, "GeneralView.label.trackerurlupdate");
/*     */       
/* 184 */       update_item.setEnabled(update_ok);
/*     */       
/* 186 */       update_item.addListener(13, new TableSelectedRowsListener(this.tv)
/*     */       {
/*     */ 
/*     */ 
/*     */         public void run(TableRowCore row)
/*     */         {
/*     */ 
/*     */ 
/* 194 */           for (Object o : sources)
/*     */           {
/* 196 */             TrackerPeerSource ps = (TrackerPeerSource)o;
/*     */             
/* 198 */             if (ps.canManuallyUpdate())
/*     */             {
/* 200 */               ps.manualUpdate();
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */       
/* 206 */       if (found_tracker)
/*     */       {
/* 208 */         MenuItem edit_item = new MenuItem(menu, 8);
/*     */         
/* 210 */         Messages.setLanguageText(edit_item, "MyTorrentsView.menu.editTracker");
/*     */         
/* 212 */         edit_item.addListener(13, new TableSelectedRowsListener(this.tv)
/*     */         {
/*     */ 
/*     */ 
/*     */           public boolean run(TableRowCore[] rows)
/*     */           {
/*     */ 
/*     */ 
/* 220 */             final TOTorrent torrent = TrackerView.this.manager.getTorrent();
/*     */             
/* 222 */             if (torrent != null)
/*     */             {
/* 224 */               Utils.execSWTThread(new Runnable()
/*     */               {
/*     */ 
/*     */                 public void run()
/*     */                 {
/*     */ 
/* 230 */                   List<List<String>> group = TorrentUtils.announceGroupsToList(torrent);
/*     */                   
/* 232 */                   new MultiTrackerEditor(null, null, group, new TrackerEditorListener() {
/*     */                     public void trackersChanged(String str, String str2, List<List<String>> _group) {
/* 234 */                       TorrentUtils.listToAnnounceGroups(_group, TrackerView.2.1.this.val$torrent);
/*     */                       try
/*     */                       {
/* 237 */                         TorrentUtils.writeToFile(TrackerView.2.1.this.val$torrent);
/*     */                       }
/*     */                       catch (Throwable e2) {
/* 240 */                         Debug.printStackTrace(e2);
/*     */                       }
/*     */                       
/* 243 */                       TRTrackerAnnouncer tc = TrackerView.this.manager.getTrackerClient();
/*     */                       
/* 245 */                       if (tc != null)
/*     */                       {
/* 247 */                         tc.resetTrackerUrl(true); } } }, true, true);
/*     */                 }
/*     */               });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 256 */             return true;
/*     */           }
/*     */           
/* 259 */         });
/* 260 */         TOTorrent torrent = this.manager.getTorrent();
/*     */         
/* 262 */         edit_item.setEnabled((torrent != null) && (!TorrentUtils.isReallyPrivate(torrent)));
/*     */       }
/*     */       
/* 265 */       needs_sep = true;
/*     */     }
/*     */     
/* 268 */     if (delete_ok)
/*     */     {
/* 270 */       MenuItem delete_item = new MenuItem(menu, 8);
/*     */       
/* 272 */       Messages.setLanguageText(delete_item, "Button.remove");
/* 273 */       Utils.setMenuItemImage(delete_item, "delete");
/*     */       
/* 275 */       delete_item.addListener(13, new TableSelectedRowsListener(this.tv)
/*     */       {
/*     */ 
/*     */ 
/*     */         public void run(TableRowCore row)
/*     */         {
/*     */ 
/*     */ 
/* 283 */           for (Object o : sources)
/*     */           {
/* 285 */             TrackerPeerSource ps = (TrackerPeerSource)o;
/*     */             
/* 287 */             if (ps.canDelete())
/*     */             {
/* 289 */               ps.delete();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 294 */       });
/* 295 */       needs_sep = true;
/*     */     }
/*     */     
/* 298 */     if (needs_sep)
/*     */     {
/* 300 */       new MenuItem(menu, 2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addThisColumnSubMenu(String columnName, Menu menuThisColumn) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void trackerPeerSourcesChanged()
/*     */   {
/* 314 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 320 */         if ((TrackerView.this.manager == null) || (TrackerView.this.tv.isDisposed()))
/*     */         {
/* 322 */           return;
/*     */         }
/*     */         
/* 325 */         TrackerView.this.tv.removeAllTableRows();
/*     */         
/* 327 */         TrackerView.this.addExistingDatasources();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tableDataSourceChanged(Object newDataSource)
/*     */   {
/* 336 */     DownloadManager newManager = ViewUtils.getDownloadManagerFromDataSource(newDataSource);
/*     */     
/* 338 */     if (newManager == this.manager) {
/* 339 */       this.tv.setEnabled(this.manager != null);
/* 340 */       return;
/*     */     }
/*     */     
/* 343 */     if (this.manager != null) {
/* 344 */       this.manager.removeTPSListener(this);
/*     */     }
/*     */     
/* 347 */     this.manager = newManager;
/*     */     
/* 349 */     if (this.tv.isDisposed()) {
/* 350 */       return;
/*     */     }
/*     */     
/* 353 */     this.tv.removeAllTableRows();
/* 354 */     this.tv.setEnabled(this.manager != null);
/*     */     
/* 356 */     if (this.manager != null) {
/* 357 */       this.manager.addTPSListener(this);
/* 358 */       addExistingDatasources();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewInitialized()
/*     */   {
/* 365 */     if (this.manager != null)
/*     */     {
/* 367 */       this.manager.addTPSListener(this);
/*     */       
/* 369 */       addExistingDatasources();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 374 */       TableViewSWT_TabsCommon tabs = this.tv.getTabsCommon();
/*     */       
/* 376 */       if (tabs != null)
/*     */       {
/* 378 */         tabs.triggerTabViewsDataSourceChanged(this.tv);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void tableViewDestroyed()
/*     */   {
/* 386 */     if (this.manager != null)
/*     */     {
/* 388 */       this.manager.removeTPSListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void addExistingDatasources()
/*     */   {
/* 395 */     if ((this.manager == null) || (this.tv.isDisposed()))
/*     */     {
/* 397 */       return;
/*     */     }
/*     */     
/* 400 */     List<TrackerPeerSource> tps = this.manager.getTrackerPeerSources();
/*     */     
/* 402 */     this.tv.addDataSources(tps.toArray(new TrackerPeerSource[tps.size()]));
/*     */     
/* 404 */     this.tv.processDataSourceQueueSync();
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 408 */     switch (event.getType())
/*     */     {
/*     */     case 0: 
/* 411 */       if ((event instanceof UISWTViewEventImpl))
/*     */       {
/* 413 */         String parent = ((UISWTViewEventImpl)event).getParentID();
/*     */         
/* 415 */         this.enable_tabs = ((parent != null) && (parent.equals("TorrentDetailsView"))); }
/* 416 */       break;
/*     */     
/*     */ 
/*     */     case 3: 
/* 420 */       String id = "DMDetails_Sources";
/* 421 */       if (this.manager != null) {
/* 422 */         if (this.manager.getTorrent() != null) {
/* 423 */           id = id + "." + this.manager.getInternalName();
/*     */         } else {
/* 425 */           id = id + ":" + this.manager.getSize();
/*     */         }
/* 427 */         SelectedContentManager.changeCurrentlySelectedContent(id, new SelectedContent[] { new SelectedContent(this.manager) });
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 432 */         SelectedContentManager.changeCurrentlySelectedContent(id, null);
/*     */       }
/*     */       
/* 435 */       break;
/*     */     
/*     */     case 4: 
/* 438 */       SelectedContentManager.clearCurrentlySelectedContent();
/*     */     }
/*     */     
/*     */     
/* 442 */     return super.eventOccurred(event);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/TrackerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */