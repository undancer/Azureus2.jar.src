/*      */ package com.aelitis.azureus.ui.swt.views.skin;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*      */ import com.aelitis.azureus.ui.common.ToolBarItem.ToolBarItemListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.ISelectedVuzeFileContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinProperties;
/*      */ import com.aelitis.azureus.ui.swt.toolbar.ToolBarItemSO;
/*      */ import com.aelitis.azureus.util.PlayUtils;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.eclipse.swt.SWT;
/*      */ import org.eclipse.swt.layout.FormAttachment;
/*      */ import org.eclipse.swt.layout.FormData;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarActivationListener;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarEnablerBase;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarItem;
/*      */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarItemImpl;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarManagerCore;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarManagerImpl;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarManagerImpl.ToolBarManagerListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ToolBarView
/*      */   extends SkinView
/*      */   implements SelectedContentListener, UIToolBarManagerImpl.ToolBarManagerListener, ToolBarItem.ToolBarItemListener
/*      */ {
/*   68 */   private static boolean DEBUG = false;
/*      */   
/*      */   private static toolbarButtonListener buttonListener;
/*      */   
/*   72 */   private Map<UIToolBarItem, ToolBarItemSO> mapToolBarItemToSO = new HashMap();
/*      */   
/*   74 */   private boolean showText = true;
/*      */   
/*   76 */   private boolean initComplete = false;
/*      */   
/*   78 */   private boolean showCalled = false;
/*      */   
/*   80 */   private ArrayList<ToolBarViewListener> listeners = new ArrayList(1);
/*      */   
/*      */ 
/*      */   private UIToolBarManagerCore tbm;
/*      */   
/*   85 */   private boolean firstTimeEver = true;
/*      */   
/*      */   public ToolBarView() {
/*   88 */     this.tbm = ((UIToolBarManagerCore)UIToolBarManagerImpl.getInstance());
/*      */   }
/*      */   
/*      */   private ToolBarItem createItem(ToolBarView tbv, String id, String imageid, String textID)
/*      */   {
/*   93 */     UIToolBarItemImpl base = new UIToolBarItemImpl(id);
/*   94 */     base.setImageID(imageid);
/*   95 */     base.setTextID(textID);
/*   96 */     return base;
/*      */   }
/*      */   
/*      */   public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params)
/*      */   {
/*  101 */     boolean uiClassic = COConfigurationManager.getStringParameter("ui").equals("az2");
/*      */     
/*      */ 
/*  104 */     if ((uiClassic) && (!"global-toolbar".equals(skinObject.getViewID()))) {
/*  105 */       skinObject.setVisible(false);
/*  106 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  111 */     SWTSkinObject temp = skinObject;
/*      */     
/*  113 */     while (temp != null)
/*      */     {
/*  115 */       int visible = temp.getSkin().getSkinProperties().getIntValue("mdientry.toolbar.visible", 1);
/*      */       
/*  117 */       if (visible == 0)
/*      */       {
/*  119 */         skinObject.setVisible(false);
/*      */         
/*  121 */         return null;
/*      */       }
/*      */       
/*  124 */       temp = temp.getParent();
/*      */     }
/*      */     
/*  127 */     buttonListener = new toolbarButtonListener(null);
/*      */     
/*      */ 
/*  130 */     if (this.firstTimeEver) {
/*  131 */       this.firstTimeEver = false;
/*  132 */       setupToolBarItems(uiClassic);
/*      */     }
/*  134 */     this.tbm.addListener(this);
/*      */     
/*  136 */     if (uiClassic) {
/*  137 */       bulkSetupItems("classic", "toolbar.area.sitem");
/*      */     }
/*  139 */     bulkSetupItems("main", "toolbar.area.sitem");
/*  140 */     bulkSetupItems("views", "toolbar.area.vitem");
/*      */     
/*  142 */     String[] groupIDs = this.tbm.getGroupIDs();
/*  143 */     for (String groupID : groupIDs) {
/*  144 */       if ((!"classic".equals(groupID)) && (!"main".equals(groupID)) && (!"views".equals(groupID)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  149 */         bulkSetupItems(groupID, "toolbar.area.sitem");
/*      */       }
/*      */     }
/*  152 */     this.initComplete = true;
/*      */     
/*  154 */     synchronized (this.listeners) {
/*  155 */       for (ToolBarViewListener l : this.listeners) {
/*      */         try {
/*  157 */           l.toolbarViewInitialized(this);
/*      */         } catch (Exception e) {
/*  159 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  164 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupToolBarItems(boolean uiClassic)
/*      */   {
/*  170 */     if (uiClassic)
/*      */     {
/*  172 */       ToolBarItem item = createItem(this, "open", "image.toolbar.open", "Button.add");
/*  173 */       item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */       {
/*      */         public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  176 */           if (activationType != 0L) {
/*  177 */             return false;
/*      */           }
/*  179 */           UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentWindow();
/*  180 */           return true;
/*      */         }
/*  182 */       });
/*  183 */       item.setAlwaysAvailable(true);
/*  184 */       item.setGroupID("classic");
/*  185 */       this.tbm.addToolBarItem(item, false);
/*      */       
/*      */ 
/*  188 */       item = createItem(this, "search", "search", "Button.search");
/*  189 */       item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */       {
/*      */         public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  192 */           if (activationType != 0L) {
/*  193 */             return false;
/*      */           }
/*  195 */           UIFunctionsManagerSWT.getUIFunctionsSWT().promptForSearch();
/*  196 */           return true;
/*      */         }
/*  198 */       });
/*  199 */       item.setAlwaysAvailable(true);
/*  200 */       item.setGroupID("classic");
/*  201 */       this.tbm.addToolBarItem(item, false);
/*      */     }
/*      */     
/*  204 */     if (!uiClassic)
/*      */     {
/*  206 */       ToolBarItem item = createItem(this, "play", "image.button.play", "iconBar.play");
/*  207 */       item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */       {
/*      */         public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  210 */           if (activationType != 0L) {
/*  211 */             return false;
/*      */           }
/*  213 */           ISelectedContent[] sc = SelectedContentManager.getCurrentlySelectedContent();
/*  214 */           if ((sc != null) && (sc.length > 0))
/*      */           {
/*  216 */             if (PlayUtils.canStreamDS(sc[0], sc[0].getFileIndex(), true)) {
/*  217 */               TorrentListViewsUtils.playOrStreamDataSource(sc[0], "toolbar", true, false);
/*      */             }
/*      */             else {
/*  220 */               TorrentListViewsUtils.playOrStreamDataSource(sc[0], "toolbar", false, true);
/*      */             }
/*      */           }
/*      */           
/*  224 */           return false;
/*      */         }
/*  226 */       });
/*  227 */       this.tbm.addToolBarItem(item, false);
/*      */     }
/*      */     
/*      */ 
/*  231 */     ToolBarItem item = createItem(this, "run", "image.toolbar.run", "iconBar.run");
/*  232 */     item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */     {
/*      */       public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  235 */         if (activationType != 0L) {
/*  236 */           return false;
/*      */         }
/*  238 */         TableView tv = SelectedContentManager.getCurrentlySelectedTableView();
/*      */         Object[] ds;
/*  240 */         Object[] ds; if (tv != null) {
/*  241 */           ds = tv.getSelectedDataSources().toArray();
/*      */         } else {
/*  243 */           ds = SelectedContentManager.getDMSFromSelectedContent();
/*      */         }
/*  245 */         if (ds != null) {
/*  246 */           TorrentUtil.runDataSources(ds);
/*  247 */           return true;
/*      */         }
/*  249 */         return false;
/*      */       }
/*  251 */     });
/*  252 */     this.tbm.addToolBarItem(item, false);
/*      */     
/*      */ 
/*  255 */     if (uiClassic)
/*      */     {
/*  257 */       item = createItem(this, "top", "image.toolbar.top", "iconBar.top");
/*  258 */       item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */       {
/*      */         public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  261 */           if (activationType == 0L) {
/*  262 */             return ToolBarView.this.moveTop();
/*      */           }
/*      */           
/*  265 */           return false;
/*      */         }
/*  267 */       });
/*  268 */       this.tbm.addToolBarItem(item, false);
/*      */     }
/*      */     
/*      */ 
/*  272 */     item = createItem(this, "up", "image.toolbar.up", "v3.iconBar.up");
/*  273 */     item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */     {
/*      */       public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  276 */         if (activationType == 0L) {
/*  277 */           if (!AzureusCoreFactory.isCoreRunning()) {
/*  278 */             return false;
/*      */           }
/*  280 */           DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
/*  281 */           if (dms != null) {
/*  282 */             Arrays.sort(dms, new Comparator() {
/*      */               public int compare(DownloadManager a, DownloadManager b) {
/*  284 */                 return a.getPosition() - b.getPosition();
/*      */               }
/*  286 */             });
/*  287 */             GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*  288 */             for (int i = 0; i < dms.length; i++) {
/*  289 */               DownloadManager dm = dms[i];
/*  290 */               if (gm.isMoveableUp(dm)) {
/*  291 */                 gm.moveUp(dm);
/*      */               }
/*      */             }
/*      */           }
/*  295 */         } else if (activationType == 1L) {
/*  296 */           return ToolBarView.this.moveTop();
/*      */         }
/*  298 */         return false;
/*      */       }
/*  300 */     });
/*  301 */     this.tbm.addToolBarItem(item, false);
/*      */     
/*      */ 
/*  304 */     item = createItem(this, "down", "image.toolbar.down", "v3.iconBar.down");
/*  305 */     item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */     {
/*      */       public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  308 */         if (activationType == 0L) {
/*  309 */           if (!AzureusCoreFactory.isCoreRunning()) {
/*  310 */             return false;
/*      */           }
/*      */           
/*  313 */           GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*  314 */           DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
/*  315 */           if (dms != null) {
/*  316 */             Arrays.sort(dms, new Comparator() {
/*      */               public int compare(DownloadManager a, DownloadManager b) {
/*  318 */                 return b.getPosition() - a.getPosition();
/*      */               }
/*      */             });
/*  321 */             for (int i = 0; i < dms.length; i++) {
/*  322 */               DownloadManager dm = dms[i];
/*  323 */               if (gm.isMoveableDown(dm)) {
/*  324 */                 gm.moveDown(dm);
/*      */               }
/*      */             }
/*  327 */             return true;
/*      */           }
/*  329 */         } else if (activationType == 1L) {
/*  330 */           return ToolBarView.this.moveBottom();
/*      */         }
/*  332 */         return false;
/*      */       }
/*  334 */     });
/*  335 */     this.tbm.addToolBarItem(item, false);
/*      */     
/*  337 */     if (uiClassic)
/*      */     {
/*  339 */       item = createItem(this, "bottom", "image.toolbar.bottom", "iconBar.bottom");
/*      */       
/*  341 */       item.setDefaultActivationListener(new UIToolBarActivationListener()
/*      */       {
/*      */         public boolean toolBarItemActivated(ToolBarItem item, long activationType, Object datasource) {
/*  344 */           if (activationType != 0L) {
/*  345 */             return false;
/*      */           }
/*  347 */           return ToolBarView.this.moveBottom();
/*      */         }
/*  349 */       });
/*  350 */       this.tbm.addToolBarItem(item, false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  389 */     item = createItem(this, "startstop", "image.toolbar.startstop.start", "iconBar.startstop");
/*      */     
/*  391 */     item.setDefaultActivationListener(new UIToolBarActivationListener_OffSWT(0L)
/*      */     {
/*      */ 
/*      */       public void toolBarItemActivated_OffSWT(ToolBarItem item, long activationType, Object datasource)
/*      */       {
/*  396 */         ISelectedContent[] selected = SelectedContentManager.getCurrentlySelectedContent();
/*  397 */         TorrentUtil.stopOrStartDataSources(selected);
/*      */       }
/*  399 */     });
/*  400 */     this.tbm.addToolBarItem(item, false);
/*      */     
/*      */ 
/*  403 */     item = createItem(this, "remove", "image.toolbar.remove", "iconBar.remove");
/*  404 */     item.setDefaultActivationListener(new UIToolBarActivationListener_OffSWT(0L)
/*      */     {
/*      */ 
/*      */       public void toolBarItemActivated_OffSWT(ToolBarItem item, long activationType, Object datasource)
/*      */       {
/*  409 */         ISelectedContent[] selected = SelectedContentManager.getCurrentlySelectedContent();
/*  410 */         TorrentUtil.removeDataSources(selected);
/*      */       }
/*  412 */     });
/*  413 */     this.tbm.addToolBarItem(item, false);
/*      */     
/*      */ 
/*      */ 
/*  417 */     if (COConfigurationManager.getBooleanParameter("Library.EnableSimpleView"))
/*      */     {
/*      */ 
/*  420 */       item = createItem(this, "modeBig", "image.toolbar.table_large", "v3.iconBar.view.big");
/*      */       
/*  422 */       item.setGroupID("views");
/*  423 */       this.tbm.addToolBarItem(item, false);
/*      */       
/*      */ 
/*  426 */       item = createItem(this, "modeSmall", "image.toolbar.table_normal", "v3.iconBar.view.small");
/*      */       
/*  428 */       item.setGroupID("views");
/*  429 */       this.tbm.addToolBarItem(item, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewID)
/*      */   {
/*  436 */     refreshCoreToolBarItems();
/*  437 */     UIFunctionsManagerSWT.getUIFunctionsSWT().refreshTorrentMenu();
/*      */   }
/*      */   
/*      */ 
/*      */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*      */   {
/*  443 */     if (this.showCalled) {
/*  444 */       return null;
/*      */     }
/*  446 */     this.showCalled = true;
/*      */     
/*  448 */     Object object = super.skinObjectShown(skinObject, params);
/*      */     
/*  450 */     ToolBarItem[] allToolBarItems = this.tbm.getAllSWTToolBarItems();
/*  451 */     for (int i = 0; i < allToolBarItems.length; i++) {
/*  452 */       ToolBarItem toolBarItem = allToolBarItems[i];
/*  453 */       toolBarItem.addToolBarItemListener(this);
/*  454 */       uiFieldChanged(toolBarItem);
/*      */     }
/*      */     
/*      */ 
/*  458 */     SelectedContentManager.addCurrentlySelectedContentListener(this);
/*  459 */     return object;
/*      */   }
/*      */   
/*      */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*      */   {
/*  464 */     this.showCalled = false;
/*  465 */     SelectedContentManager.removeCurrentlySelectedContentListener(this);
/*      */     
/*  467 */     ToolBarItem[] allToolBarItems = this.tbm.getAllSWTToolBarItems();
/*  468 */     for (int i = 0; i < allToolBarItems.length; i++) {
/*  469 */       ToolBarItem toolBarItem = allToolBarItems[i];
/*  470 */       toolBarItem.removeToolBarItemListener(this);
/*      */     }
/*      */     
/*  473 */     return super.skinObjectHidden(skinObject, params);
/*      */   }
/*      */   
/*      */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*      */   {
/*  478 */     this.tbm.removeListener(this);
/*      */     
/*  480 */     return super.skinObjectDestroyed(skinObject, params);
/*      */   }
/*      */   
/*      */   public boolean triggerToolBarItem(ToolBarItem item, long activationType, Object datasource)
/*      */   {
/*  485 */     if (!isVisible()) {
/*  486 */       if (DEBUG) {
/*  487 */         Debug.out("Trying to triggerToolBarItem when toolbar is not visible");
/*      */       }
/*  489 */       return false;
/*      */     }
/*  491 */     if (triggerViewToolBar(item, activationType, datasource)) {
/*  492 */       return true;
/*      */     }
/*      */     
/*  495 */     UIToolBarActivationListener defaultActivation = item.getDefaultActivationListener();
/*  496 */     if (defaultActivation != null) {
/*  497 */       return defaultActivation.toolBarItemActivated(item, activationType, datasource);
/*      */     }
/*      */     
/*      */ 
/*  501 */     if (DEBUG) {
/*  502 */       String viewID = SelectedContentManager.getCurrentySelectedViewID();
/*  503 */       System.out.println("Warning: Fallback of toolbar button " + item.getID() + " via " + viewID + " view");
/*      */     }
/*      */     
/*      */ 
/*  507 */     return false;
/*      */   }
/*      */   
/*      */   protected boolean moveBottom() {
/*  511 */     if (!AzureusCoreFactory.isCoreRunning()) {
/*  512 */       return false;
/*      */     }
/*      */     
/*  515 */     GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*  516 */     DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
/*  517 */     if (dms != null) {
/*  518 */       gm.moveEnd(dms);
/*      */     }
/*  520 */     return true;
/*      */   }
/*      */   
/*      */   protected boolean moveTop() {
/*  524 */     if (!AzureusCoreFactory.isCoreRunning()) {
/*  525 */       return false;
/*      */     }
/*  527 */     GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*  528 */     DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
/*  529 */     if (dms != null) {
/*  530 */       gm.moveTop(dms);
/*      */     }
/*  532 */     return true;
/*      */   }
/*      */   
/*  535 */   private FrequencyLimitedDispatcher refresh_limiter = new FrequencyLimitedDispatcher(new AERunnable()
/*      */   {
/*  537 */     private AERunnable lock = this;
/*      */     private boolean refresh_pending;
/*      */     
/*      */     public void runSupport()
/*      */     {
/*  542 */       synchronized (this.lock)
/*      */       {
/*  544 */         if (this.refresh_pending)
/*      */         {
/*  546 */           return;
/*      */         }
/*  548 */         this.refresh_pending = true;
/*      */       }
/*      */       
/*  551 */       if (ToolBarView.DEBUG) {
/*  552 */         System.out.println("refreshCoreItems via " + Debug.getCompressedStackTrace());
/*      */       }
/*      */       
/*      */ 
/*  556 */       Utils.execSWTThread(new AERunnable()
/*      */       {
/*      */         public void runSupport() {
/*  559 */           synchronized (ToolBarView.11.this.lock)
/*      */           {
/*  561 */             ToolBarView.11.this.refresh_pending = false;
/*      */           }
/*      */           
/*  564 */           ToolBarView.this._refreshCoreToolBarItems();
/*      */         }
/*      */       });
/*      */     }
/*  535 */   }, 250);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  570 */   private IdentityHashMap<DownloadManager, DownloadManagerListener> dm_listener_map = new IdentityHashMap();
/*      */   private SWTSkinObject soLastGroup;
/*      */   
/*      */   public void refreshCoreToolBarItems()
/*      */   {
/*  575 */     if (DEBUG) {
/*  576 */       System.out.println("refreshCoreItems Start via " + Debug.getCompressedStackTrace());
/*      */     }
/*      */     
/*  579 */     this.refresh_limiter.dispatch();
/*      */   }
/*      */   
/*      */   public void _refreshCoreToolBarItems() {
/*  583 */     if ((DEBUG) && (!isVisible())) {
/*  584 */       Debug.out("Trying to refresh core toolbar items when toolbar is not visible " + this + getMainSkinObject());
/*      */     }
/*      */     
/*      */ 
/*  588 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*      */     
/*  590 */     if (mdi != null) {
/*  591 */       UIToolBarItem[] allToolBarItems = this.tbm.getAllToolBarItems();
/*  592 */       MdiEntrySWT entry = mdi.getCurrentEntrySWT();
/*  593 */       Map<String, Long> mapStates = new HashMap();
/*  594 */       if (entry != null) {
/*  595 */         UIToolBarEnablerBase[] enablers = entry.getToolbarEnablers();
/*  596 */         for (UIToolBarEnablerBase enabler : enablers) {
/*  597 */           if ((enabler instanceof UIPluginViewToolBarListener)) {
/*      */             try {
/*  599 */               ((UIPluginViewToolBarListener)enabler).refreshToolBarItems(mapStates);
/*      */             } catch (Throwable e) {
/*  601 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  607 */       ISelectedContent[] currentContent = SelectedContentManager.getCurrentlySelectedContent();
/*      */       
/*      */ 
/*  610 */       synchronized (this.dm_listener_map)
/*      */       {
/*  612 */         Map<DownloadManager, DownloadManagerListener> copy = new IdentityHashMap(this.dm_listener_map);
/*      */         
/*      */ 
/*  615 */         for (ISelectedContent content : currentContent)
/*      */         {
/*  617 */           DownloadManager dm = content.getDownloadManager();
/*      */           
/*  619 */           if (dm != null)
/*      */           {
/*  621 */             copy.remove(dm);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  626 */             if (!this.dm_listener_map.containsKey(dm))
/*      */             {
/*  628 */               DownloadManagerListener l = new DownloadManagerListener() {
/*      */                 public void stateChanged(DownloadManager manager, int state) {
/*  630 */                   ToolBarView.this.refreshCoreToolBarItems();
/*      */                 }
/*      */                 
/*      */                 public void downloadComplete(DownloadManager manager) {
/*  634 */                   ToolBarView.this.refreshCoreToolBarItems();
/*      */                 }
/*      */                 
/*      */                 public void completionChanged(DownloadManager manager, boolean bCompleted)
/*      */                 {
/*  639 */                   ToolBarView.this.refreshCoreToolBarItems();
/*      */                 }
/*      */                 
/*      */                 public void positionChanged(DownloadManager download, int oldPosition, int newPosition)
/*      */                 {
/*  644 */                   ToolBarView.this.refreshCoreToolBarItems();
/*      */                 }
/*      */                 
/*      */                 public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file)
/*      */                 {
/*  649 */                   ToolBarView.this.refreshCoreToolBarItems();
/*      */                 }
/*      */                 
/*  652 */               };
/*  653 */               dm.addListener(l, false);
/*      */               
/*  655 */               this.dm_listener_map.put(dm, l);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  662 */         for (Map.Entry<DownloadManager, DownloadManagerListener> e : copy.entrySet())
/*      */         {
/*  664 */           DownloadManager dm = (DownloadManager)e.getKey();
/*      */           
/*  666 */           dm.removeListener((DownloadManagerListener)e.getValue());
/*      */           
/*  668 */           this.dm_listener_map.remove(dm);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  674 */       boolean has1Selection = currentContent.length == 1;
/*      */       
/*  676 */       boolean can_play = false;
/*  677 */       boolean can_stream = false;
/*      */       
/*  679 */       boolean stream_permitted = false;
/*      */       
/*  681 */       if (has1Selection)
/*      */       {
/*  683 */         if (!(currentContent[0] instanceof ISelectedVuzeFileContent))
/*      */         {
/*  685 */           can_play = PlayUtils.canPlayDS(currentContent[0], currentContent[0].getFileIndex(), false);
/*      */           
/*  687 */           can_stream = PlayUtils.canStreamDS(currentContent[0], currentContent[0].getFileIndex(), false);
/*      */           
/*      */ 
/*  690 */           if (can_stream)
/*      */           {
/*  692 */             stream_permitted = PlayUtils.isStreamPermitted();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  699 */       if (mapStates.containsKey("play")) {
/*  700 */         can_play |= (((Long)mapStates.get("play")).longValue() & 1L) > 0L;
/*      */       }
/*  702 */       if (mapStates.containsKey("stream")) {
/*  703 */         can_stream |= (((Long)mapStates.get("stream")).longValue() & 1L) > 0L;
/*      */       }
/*      */       
/*  706 */       mapStates.put("play", Long.valueOf((can_play | can_stream) ? 1L : 0L));
/*      */       
/*      */ 
/*  709 */       UIToolBarItem pitem = this.tbm.getToolBarItem("play");
/*      */       
/*  711 */       if (pitem != null)
/*      */       {
/*  713 */         if (can_stream)
/*      */         {
/*  715 */           pitem.setImageID(stream_permitted ? "image.button.stream" : "image.button.pstream");
/*      */           
/*  717 */           pitem.setTextID(stream_permitted ? "iconBar.stream" : "iconBar.pstream");
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  722 */           pitem.setImageID("image.button.play");
/*  723 */           pitem.setTextID("iconBar.play");
/*      */         }
/*      */       }
/*      */       
/*  727 */       UIToolBarItem ssItem = this.tbm.getToolBarItem("startstop");
/*  728 */       if (ssItem != null)
/*      */       {
/*      */         boolean shouldStopGroup;
/*      */         
/*      */ 
/*      */ 
/*      */         boolean shouldStopGroup;
/*      */         
/*      */ 
/*  737 */         if ((currentContent.length == 0) && (mapStates.containsKey("start")) && ((!mapStates.containsKey("stop")) || ((((Long)mapStates.get("stop")).longValue() & 1L) == 0L)) && ((((Long)mapStates.get("start")).longValue() & 1L) > 0L))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  742 */           shouldStopGroup = false;
/*      */         }
/*      */         else {
/*  745 */           shouldStopGroup = TorrentUtil.shouldStopGroup(currentContent);
/*      */         }
/*      */         
/*  748 */         ssItem.setTextID(shouldStopGroup ? "iconBar.stop" : "iconBar.start");
/*  749 */         ssItem.setImageID("image.toolbar.startstop." + (shouldStopGroup ? "stop" : "start"));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  754 */         if ((currentContent.length == 0) && (!mapStates.containsKey("startstop")))
/*      */         {
/*  756 */           boolean can_stop = (mapStates.containsKey("stop")) && ((((Long)mapStates.get("stop")).longValue() & 1L) > 0L);
/*      */           
/*  758 */           boolean can_start = (mapStates.containsKey("start")) && ((((Long)mapStates.get("start")).longValue() & 1L) > 0L);
/*      */           
/*      */ 
/*  761 */           if ((can_start) && (can_stop))
/*      */           {
/*  763 */             can_stop = false;
/*      */           }
/*      */           
/*  766 */           if ((can_start) || (can_stop)) {
/*  767 */             ssItem.setTextID(can_stop ? "iconBar.stop" : "iconBar.start");
/*  768 */             ssItem.setImageID("image.toolbar.startstop." + (can_stop ? "stop" : "start"));
/*      */             
/*      */ 
/*  771 */             mapStates.put("startstop", Long.valueOf(1L));
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  776 */       Map<String, Long> fallBackStates = TorrentUtil.calculateToolbarStates(currentContent, null);
/*  777 */       for (String key : fallBackStates.keySet()) {
/*  778 */         if (!mapStates.containsKey(key)) {
/*  779 */           mapStates.put(key, fallBackStates.get(key));
/*      */         }
/*      */       }
/*      */       
/*  783 */       String[] TBKEYS = { "play", "run", "top", "up", "down", "bottom", "start", "stop", "startstop", "remove" };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  795 */       for (String key : TBKEYS) {
/*  796 */         if (!mapStates.containsKey(key)) {
/*  797 */           mapStates.put(key, Long.valueOf(0L));
/*      */         }
/*      */       }
/*      */       
/*  801 */       for (int i = 0; i < allToolBarItems.length; i++) {
/*  802 */         UIToolBarItem toolBarItem = allToolBarItems[i];
/*  803 */         Long state = (Long)mapStates.get(toolBarItem.getID());
/*  804 */         if (state != null) {
/*  805 */           toolBarItem.setState(state.longValue());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean triggerViewToolBar(ToolBarItem item, long activationType, Object datasource)
/*      */   {
/*  814 */     if ((DEBUG) && (!isVisible())) {
/*  815 */       Debug.out("Trying to triggerViewToolBar when toolbar is not visible");
/*  816 */       return false;
/*      */     }
/*  818 */     MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/*  819 */     if (mdi != null) {
/*  820 */       MdiEntrySWT entry = mdi.getCurrentEntrySWT();
/*  821 */       UIToolBarEnablerBase[] enablers = entry.getToolbarEnablers();
/*  822 */       for (UIToolBarEnablerBase enabler : enablers) {
/*  823 */         if (((enabler instanceof UIPluginViewToolBarListener)) && 
/*  824 */           (((UIPluginViewToolBarListener)enabler).toolBarItemActivated(item, activationType, datasource)))
/*      */         {
/*  826 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  832 */     return false;
/*      */   }
/*      */   
/*      */   private void bulkSetupItems(String groupID, String templatePrefix) {
/*  836 */     String[] idsByGroup = this.tbm.getToolBarIDsByGroup(groupID);
/*  837 */     SWTSkinObjectContainer groupSO = getGroupSO(groupID);
/*  838 */     SWTSkinObject[] children = groupSO.getChildren();
/*  839 */     for (SWTSkinObject so : children) {
/*  840 */       so.dispose();
/*      */     }
/*      */     
/*  843 */     for (int i = 0; i < idsByGroup.length; i++) {
/*  844 */       String itemID = idsByGroup[i];
/*  845 */       UIToolBarItem item = this.tbm.getToolBarItem(itemID);
/*  846 */       if ((item instanceof ToolBarItem))
/*      */       {
/*      */ 
/*  849 */         int position = 0;
/*  850 */         int size = idsByGroup.length;
/*  851 */         if (size == 1) {
/*  852 */           position = 4;
/*  853 */         } else if (i == 0) {
/*  854 */           position = 16384;
/*  855 */         } else if (i == size - 1) {
/*  856 */           addSeperator(groupID);
/*  857 */           position = 131072;
/*      */         } else {
/*  859 */           addSeperator(groupID);
/*      */         }
/*  861 */         createItemSO((ToolBarItem)item, templatePrefix, position);
/*      */       }
/*      */     }
/*      */     
/*  865 */     addNonToolBar("toolbar.area.sitem.left2", groupID);
/*      */   }
/*      */   
/*      */   private Control getLastControl(String groupID) {
/*  869 */     SWTSkinObjectContainer groupSO = getGroupSO(groupID);
/*  870 */     SWTSkinObject[] children = groupSO.getChildren();
/*  871 */     if ((children == null) || (children.length == 0)) {
/*  872 */       return null;
/*      */     }
/*  874 */     return children[(children.length - 1)].getControl();
/*      */   }
/*      */   
/*      */ 
/*      */   private void createItemSO(ToolBarItem item, String templatePrefix, int position)
/*      */   {
/*  880 */     ToolBarItemSO existingItemSO = (ToolBarItemSO)this.mapToolBarItemToSO.get(item);
/*  881 */     if (existingItemSO != null) {
/*  882 */       SWTSkinObject so = existingItemSO.getSO();
/*  883 */       if (so != null) {
/*  884 */         so.dispose();
/*      */       }
/*      */     }
/*      */     
/*  888 */     String templateID = templatePrefix;
/*  889 */     if (position == 131072) {
/*  890 */       templateID = templateID + ".right";
/*  891 */     } else if (position == 16384) {
/*  892 */       templateID = templateID + ".left";
/*  893 */     } else if (position == 4) {
/*  894 */       templateID = templateID + ".lr";
/*      */     }
/*      */     
/*  897 */     Control attachToControl = getLastControl(item.getGroupID());
/*  898 */     String id = "toolbar:" + item.getID();
/*  899 */     SWTSkinObject so = this.skin.createSkinObject(id, templateID, getGroupSO(item.getGroupID()));
/*  900 */     if (so != null)
/*      */     {
/*  902 */       ToolBarItemSO itemSO = new ToolBarItemSO((UIToolBarItemImpl)item, so);
/*      */       
/*  904 */       if (attachToControl != null) {
/*  905 */         FormData fd = (FormData)so.getControl().getLayoutData();
/*  906 */         fd.left = new FormAttachment(attachToControl);
/*      */       }
/*      */       
/*  909 */       initSO(so, itemSO);
/*      */       
/*  911 */       if (this.initComplete) {
/*  912 */         Utils.relayout(so.getControl().getParent());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private SWTSkinObjectContainer getGroupSO(String groupID) {
/*  918 */     String soID = "toolbar-group-" + groupID;
/*  919 */     SWTSkinObjectContainer soGroup = (SWTSkinObjectContainer)this.skin.getSkinObjectByID(soID, this.soMain);
/*      */     
/*      */ 
/*  922 */     if (soGroup == null) {
/*  923 */       soGroup = (SWTSkinObjectContainer)this.skin.createSkinObject(soID, "toolbar.group", this.soMain);
/*      */       
/*  925 */       FormData fd = (FormData)soGroup.getControl().getLayoutData();
/*  926 */       if (this.soLastGroup != null) {
/*  927 */         fd.left = new FormAttachment(this.soLastGroup.getControl(), 0, 131072);
/*      */       } else {
/*  929 */         fd.left = new FormAttachment(0, 2);
/*      */       }
/*      */     }
/*      */     
/*  933 */     this.soLastGroup = soGroup;
/*      */     
/*  935 */     return soGroup;
/*      */   }
/*      */   
/*      */   private void initSO(SWTSkinObject so, ToolBarItemSO itemSO) {
/*  939 */     ToolBarItem item = itemSO.getBase();
/*  940 */     itemSO.setSO(so);
/*  941 */     String toolTip = item.getToolTip();
/*  942 */     if (toolTip != null) {
/*  943 */       so.setTooltipID("!" + toolTip + "!");
/*      */     } else {
/*  945 */       so.setTooltipID(item.getTooltipID());
/*      */     }
/*  947 */     so.setData("toolbaritem", item);
/*  948 */     SWTSkinButtonUtility btn = (SWTSkinButtonUtility)so.getData("btn");
/*  949 */     if (btn == null) {
/*  950 */       btn = new SWTSkinButtonUtility(so, "toolbar-item-image");
/*  951 */       so.setData("btn", btn);
/*      */     }
/*  953 */     btn.setImage(item.getImageID());
/*  954 */     btn.addSelectionListener(buttonListener);
/*  955 */     itemSO.setSkinButton(btn);
/*      */     
/*  957 */     SWTSkinObject soTitle = this.skin.getSkinObject("toolbar-item-title", so);
/*  958 */     if ((soTitle instanceof SWTSkinObjectText)) {
/*  959 */       ((SWTSkinObjectText)soTitle).setTextID(item.getTextID());
/*  960 */       itemSO.setSkinTitle((SWTSkinObjectText)soTitle);
/*      */     }
/*  962 */     this.mapToolBarItemToSO.put(item, itemSO);
/*      */   }
/*      */   
/*      */   public void uiFieldChanged(ToolBarItem item)
/*      */   {
/*  967 */     ToolBarItemSO itemSO = (ToolBarItemSO)this.mapToolBarItemToSO.get(item);
/*  968 */     if (itemSO != null) {
/*  969 */       itemSO.updateUI();
/*      */     }
/*      */   }
/*      */   
/*      */   private void addSeperator(String groupID) {
/*  974 */     addSeperator("toolbar.area.sitem.sep", groupID);
/*      */   }
/*      */   
/*      */   private void addSeperator(String id, String groupID) {
/*  978 */     SWTSkinObjectContainer soGroup = getGroupSO(groupID);
/*  979 */     Control lastControl = getLastControl(groupID);
/*  980 */     SWTSkinObject so = this.skin.createSkinObject("toolbar_sep" + Math.random(), id, soGroup);
/*      */     
/*  982 */     if ((so != null) && 
/*  983 */       (lastControl != null)) {
/*  984 */       FormData fd = (FormData)so.getControl().getLayoutData();
/*  985 */       fd.left = new FormAttachment(lastControl, fd.left == null ? 0 : fd.left.offset);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void addNonToolBar(String skinid, String groupID)
/*      */   {
/*  992 */     SWTSkinObjectContainer soGroup = getGroupSO(groupID);
/*  993 */     Control lastControl = getLastControl(groupID);
/*  994 */     SWTSkinObject so = this.skin.createSkinObject("toolbar_d" + Math.random(), skinid, soGroup);
/*      */     
/*  996 */     if ((so != null) && 
/*  997 */       (lastControl != null)) {
/*  998 */       FormData fd = (FormData)so.getControl().getLayoutData();
/*  999 */       fd.left = new FormAttachment(lastControl, fd.left == null ? 0 : fd.left.offset);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setShowText(boolean showText)
/*      */   {
/* 1009 */     this.showText = showText;
/* 1010 */     UIToolBarItem[] allToolBarItems = this.tbm.getAllToolBarItems();
/* 1011 */     for (int i = 0; i < allToolBarItems.length; i++) {
/* 1012 */       UIToolBarItem tbi = allToolBarItems[i];
/* 1013 */       SWTSkinObject so = ((ToolBarItemSO)tbi).getSkinButton().getSkinObject();
/* 1014 */       SWTSkinObject soTitle = this.skin.getSkinObject("toolbar-item-title", so);
/* 1015 */       if (soTitle != null) {
/* 1016 */         soTitle.setVisible(showText);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getShowText()
/*      */   {
/* 1025 */     return this.showText;
/*      */   }
/*      */   
/*      */   private static class toolbarButtonListener
/*      */     extends SWTSkinButtonUtility.ButtonListenerAdapter
/*      */   {
/*      */     public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask)
/*      */     {
/* 1033 */       ToolBarItem item = (ToolBarItem)buttonUtility.getSkinObject().getData("toolbaritem");
/*      */       
/* 1035 */       boolean rightClick = (stateMask & (0x200000 | SWT.MOD4)) > 0;
/* 1036 */       Object o = SelectedContentManager.convertSelectedContentToObject(null);
/* 1037 */       item.triggerToolBarItem(rightClick ? 2L : 0L, o);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean held(SWTSkinButtonUtility buttonUtility)
/*      */     {
/* 1043 */       ToolBarItem item = (ToolBarItem)buttonUtility.getSkinObject().getData("toolbaritem");
/*      */       
/* 1045 */       buttonUtility.getSkinObject().switchSuffix("", 0, false, true);
/*      */       
/* 1047 */       Object o = SelectedContentManager.convertSelectedContentToObject(null);
/* 1048 */       boolean triggerToolBarItemHold = item.triggerToolBarItem(1L, o);
/*      */       
/* 1050 */       return triggerToolBarItemHold;
/*      */     }
/*      */   }
/*      */   
/*      */   public void addListener(ToolBarViewListener l) {
/* 1055 */     synchronized (this.listeners) {
/* 1056 */       this.listeners.add(l);
/*      */       
/* 1058 */       if (this.initComplete) {
/*      */         try {
/* 1060 */           l.toolbarViewInitialized(this);
/*      */         } catch (Exception e) {
/* 1062 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeListener(ToolBarViewListener l) {
/* 1069 */     synchronized (this.listeners) {
/* 1070 */       this.listeners.remove(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void toolbarItemRemoved(final UIToolBarItem toolBarItem)
/*      */   {
/* 1081 */     ToolBarItemSO itemSO = (ToolBarItemSO)this.mapToolBarItemToSO.get(toolBarItem);
/* 1082 */     if (itemSO == null) {
/* 1083 */       return;
/*      */     }
/*      */     
/* 1086 */     itemSO.dispose();
/* 1087 */     final SWTSkinObject so = itemSO.getSO();
/* 1088 */     if (so != null) {
/* 1089 */       Utils.execSWTThread(new AERunnable()
/*      */       {
/*      */         public void runSupport() {
/* 1092 */           String groupID = toolBarItem.getGroupID();
/*      */           
/* 1094 */           String[] idsByGroup = ToolBarView.this.tbm.getToolBarIDsByGroup(groupID);
/*      */           
/* 1096 */           if (idsByGroup.length <= 1) {
/* 1097 */             boolean b = ToolBarView.this.initComplete;
/* 1098 */             ToolBarView.this.initComplete = false;
/* 1099 */             ToolBarView.this.bulkSetupItems(groupID, "toolbar.area.sitem");
/* 1100 */             ToolBarView.this.initComplete = b;
/* 1101 */             so.getParent().relayout();
/* 1102 */             return;
/*      */           }
/*      */           
/* 1105 */           int posToolBarItem = -1;
/* 1106 */           String id = toolBarItem.getID();
/*      */           
/*      */ 
/* 1109 */           Control soControl = so.getControl();
/*      */           
/*      */ 
/* 1112 */           SWTSkinObject middleSO = ((ToolBarItemSO)ToolBarView.this.mapToolBarItemToSO.get(ToolBarView.this.tbm.getToolBarItem(idsByGroup[(idsByGroup.length / 2)]))).getSO();
/*      */           
/*      */ 
/*      */ 
/* 1116 */           SWTSkinObject[] children = ((SWTSkinObjectContainer)so.getParent()).getChildren();
/* 1117 */           int middle = -1;
/* 1118 */           for (int i = 0; i < children.length; i++) {
/* 1119 */             if (children[i] == middleSO) {
/* 1120 */               middle = i;
/* 1121 */               break;
/*      */             }
/*      */           }
/*      */           
/* 1125 */           if (middle == -1) {
/* 1126 */             return;
/*      */           }
/*      */           
/*      */ 
/* 1130 */           children[middle].dispose();
/* 1131 */           children[(middle + 1)].dispose();
/*      */           
/* 1133 */           Control controlLeft = children[(middle - 1)].getControl();
/* 1134 */           FormData fd = (FormData)children[(middle + 2)].getControl().getLayoutData();
/* 1135 */           fd.left.control = controlLeft;
/* 1136 */           Utils.relayout(children[(middle + 2)].getControl());
/*      */           
/* 1138 */           int positionInGroup = 0;
/* 1139 */           UIToolBarItem curItem = ToolBarView.this.tbm.getToolBarItem(idsByGroup[positionInGroup]);
/*      */           
/* 1141 */           children = ((SWTSkinObjectContainer)so.getParent()).getChildren();
/* 1142 */           for (int i = 0; i < children.length; i++) {
/* 1143 */             SWTSkinObject child = children[i];
/*      */             
/* 1145 */             ToolBarItem item = (ToolBarItem)child.getData("toolbaritem");
/* 1146 */             if ((item != null) && (item.getGroupID().equals(groupID)))
/*      */             {
/* 1148 */               ToolBarItemSO toolBarItemSO = (ToolBarItemSO)ToolBarView.this.mapToolBarItemToSO.get(curItem);
/* 1149 */               ToolBarView.this.initSO(child, toolBarItemSO);
/* 1150 */               positionInGroup++;
/* 1151 */               if (positionInGroup >= idsByGroup.length) {
/*      */                 break;
/*      */               }
/* 1154 */               curItem = ToolBarView.this.tbm.getToolBarItem(idsByGroup[positionInGroup]);
/*      */             }
/*      */           }
/*      */           
/* 1158 */           so.getParent().relayout();
/*      */         }
/*      */       });
/*      */     }
/* 1162 */     this.mapToolBarItemToSO.remove(toolBarItem);
/*      */   }
/*      */   
/*      */   public void toolbarItemAdded(final UIToolBarItem item)
/*      */   {
/* 1167 */     if ((isVisible()) && 
/* 1168 */       ((item instanceof ToolBarItem))) {
/* 1169 */       ToolBarItem toolBarItem = (ToolBarItem)item;
/* 1170 */       toolBarItem.addToolBarItemListener(this);
/*      */     }
/*      */     
/*      */ 
/* 1174 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1176 */         boolean b = ToolBarView.this.initComplete;
/* 1177 */         ToolBarView.this.initComplete = false;
/* 1178 */         ToolBarView.this.bulkSetupItems(item.getGroupID(), "toolbar.area.sitem");
/* 1179 */         ToolBarView.this.initComplete = b;
/*      */         
/* 1181 */         Utils.execSWTThreadLater(0, new Runnable() {
/*      */           public void run() {
/* 1183 */             Utils.relayout(ToolBarView.this.soMain.getControl());
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static abstract class UIToolBarActivationListener_OffSWT implements UIToolBarActivationListener
/*      */   {
/*      */     private long onlyOnActivationType;
/*      */     
/*      */     public UIToolBarActivationListener_OffSWT(long onlyOnActivationType)
/*      */     {
/* 1196 */       this.onlyOnActivationType = onlyOnActivationType;
/*      */     }
/*      */     
/*      */     public UIToolBarActivationListener_OffSWT() {
/* 1200 */       this.onlyOnActivationType = -1L;
/*      */     }
/*      */     
/*      */     public final boolean toolBarItemActivated(final ToolBarItem item, final long activationType, Object datasource)
/*      */     {
/* 1205 */       if ((this.onlyOnActivationType >= 0L) && (activationType != this.onlyOnActivationType)) {
/* 1206 */         return false;
/*      */       }
/* 1208 */       Utils.getOffOfSWTThread(new AERunnable()
/*      */       {
/*      */         public void runSupport() {
/* 1211 */           ToolBarView.UIToolBarActivationListener_OffSWT.this.toolBarItemActivated_OffSWT(item, activationType, this.val$datasource);
/*      */         }
/* 1213 */       });
/* 1214 */       return true;
/*      */     }
/*      */     
/*      */     public abstract void toolBarItemActivated_OffSWT(ToolBarItem paramToolBarItem, long paramLong, Object paramObject);
/*      */   }
/*      */   
/*      */   public static abstract interface ToolBarViewListener
/*      */   {
/*      */     public abstract void toolbarViewInitialized(ToolBarView paramToolBarView);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/ToolBarView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */