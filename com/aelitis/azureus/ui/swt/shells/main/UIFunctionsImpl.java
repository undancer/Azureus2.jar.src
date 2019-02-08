/*      */ package com.aelitis.azureus.ui.swt.shells.main;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginHTTPProxy;
/*      */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.ui.InitializerListener;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctions.TagReturner;
/*      */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*      */ import com.aelitis.azureus.ui.UIStatusTextClickListener;
/*      */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*      */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntryOpenListener;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.Initializer;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.TabbedMdiInterface;
/*      */ import com.aelitis.azureus.ui.swt.plugininstall.SimplePluginInstaller;
/*      */ import com.aelitis.azureus.ui.swt.search.SearchHandler;
/*      */ import com.aelitis.azureus.ui.swt.shells.BrowserWindow;
/*      */ import com.aelitis.azureus.ui.swt.shells.opentorrent.OpenTorrentOptionsWindow;
/*      */ import com.aelitis.azureus.ui.swt.shells.opentorrent.OpenTorrentWindow;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectBrowser;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectButton;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*      */ import com.aelitis.azureus.ui.swt.skin.SWTSkinUtils;
/*      */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*      */ import com.aelitis.azureus.ui.swt.utils.TagUIUtilsV3;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SBC_PlusFTUX;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinViewManager;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog.SkinnedDialogClosedListener;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.ToolBarView;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.TorrentListViewsUtils;
/*      */ import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBarEntrySWT;
/*      */ import com.aelitis.azureus.util.ConstantsVuze;
/*      */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*      */ import com.aelitis.azureus.util.UrlFilter;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.events.PaintEvent;
/*      */ import org.eclipse.swt.events.PaintListener;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.program.Program;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.history.DownloadHistoryManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
/*      */ import org.gudy.azureus2.ui.swt.FileDownloadWindow;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.IMainMenu;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.IMainWindow;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.minibar.AllTransfersBar;
/*      */ import org.gudy.azureus2.ui.swt.minibar.MiniBarManager;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewImpl;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarManagerImpl;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageSlideShell;
/*      */ import org.gudy.azureus2.ui.swt.update.FullUpdateWindow;
/*      */ 
/*      */ public class UIFunctionsImpl implements UIFunctionsSWT
/*      */ {
/*      */   private static final boolean PROXY_VIEW_URL = false;
/*      */   private static final String MSG_ALREADY_EXISTS = "OpenTorrentWindow.mb.alreadyExists";
/*      */   private static final String MSG_ALREADY_EXISTS_NAME = "OpenTorrentWindow.mb.alreadyExists.default.name";
/*  124 */   private static final LogIDs LOGID = LogIDs.GUI;
/*      */   
/*      */ 
/*      */ 
/*      */   private final MainWindow mainWindow;
/*      */   
/*      */ 
/*  131 */   private SWTSkin skin = null;
/*      */   
/*      */ 
/*      */   protected boolean isTorrentMenuVisible;
/*      */   
/*      */ 
/*      */ 
/*      */   public UIFunctionsImpl(MainWindow window)
/*      */   {
/*  140 */     this.mainWindow = window;
/*      */     
/*  142 */     COConfigurationManager.addAndFireParameterListener("show_torrents_menu", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/*  145 */         UIFunctionsImpl.this.isTorrentMenuVisible = COConfigurationManager.getBooleanParameter("show_torrents_menu");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUIType()
/*      */   {
/*  153 */     return 1;
/*      */   }
/*      */   
/*      */   public void addPluginView(final String viewID, final UISWTViewEventListener l)
/*      */   {
/*      */     try
/*      */     {
/*  160 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  162 */           PluginsMenuHelper.getInstance().addPluginView(viewID, l);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Exception e) {
/*  167 */       Logger.log(new LogEvent(LOGID, "addPluginView", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void bringToFront()
/*      */   {
/*  174 */     bringToFront(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void bringToFront(final boolean tryTricks)
/*      */   {
/*  180 */     String debug = COConfigurationManager.getStringParameter("adv.setting.ui.debug.window.show", "");
/*      */     
/*  182 */     if (debug.equals("1")) {
/*  183 */       Debug.out("UIF::bringToFront");
/*      */     }
/*      */     
/*  186 */     Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/*      */         try {
/*  190 */           UIFunctionsImpl.this.mainWindow.setVisible(true, tryTricks);
/*      */         }
/*      */         catch (Exception e) {
/*  193 */           Logger.log(new LogEvent(UIFunctionsImpl.LOGID, "bringToFront", e));
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getVisibilityState()
/*      */   {
/*  203 */     final Shell shell = getMainShell();
/*      */     
/*  205 */     if (shell == null)
/*      */     {
/*  207 */       return 2;
/*      */     }
/*      */     
/*      */ 
/*  211 */     final int[] result = { 2 };
/*      */     
/*  213 */     final AESemaphore sem = new AESemaphore("getVisibilityState");
/*      */     
/*  215 */     if (Utils.execSWTThread(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  223 */           if (!shell.isVisible())
/*      */           {
/*  225 */             result[0] = 2;
/*      */           }
/*  227 */           else if (shell.getMinimized())
/*      */           {
/*  229 */             result[0] = 3;
/*      */           }
/*      */           else
/*      */           {
/*  233 */             result[0] = 4;
/*      */           }
/*      */         }
/*      */         finally {
/*  237 */           sem.release();
/*      */         }
/*      */       }
/*  215 */     }))
/*      */     {
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
/*  242 */       sem.reserve(30000L);
/*      */     }
/*      */     
/*  245 */     return result[0];
/*      */   }
/*      */   
/*      */   public void closeDownloadBars()
/*      */   {
/*      */     try
/*      */     {
/*  252 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*      */         public void runSupport() {
/*  254 */           MiniBarManager.getManager().closeAll();
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Exception e) {
/*  259 */       Logger.log(new LogEvent(LOGID, "closeDownloadBars", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void closePluginView(UISWTViewCore view)
/*      */   {
/*      */     try
/*      */     {
/*  269 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  270 */       if (mdi == null)
/*      */         return;
/*      */       String id;
/*      */       String id;
/*  274 */       if ((view instanceof UISWTViewImpl)) {
/*  275 */         id = ((UISWTViewImpl)view).getViewID();
/*      */       } else {
/*  277 */         id = view.getClass().getName();
/*  278 */         int i = id.lastIndexOf('.');
/*  279 */         if (i > 0) {
/*  280 */           id = id.substring(i + 1);
/*      */         }
/*      */       }
/*  283 */       mdi.closeEntry(id);
/*      */     }
/*      */     catch (Exception e) {
/*  286 */       Logger.log(new LogEvent(LOGID, "closePluginView", e));
/*      */     }
/*      */   }
/*      */   
/*      */   public void closePluginViews(String sViewID)
/*      */   {
/*      */     try
/*      */     {
/*  294 */       MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  295 */       if (mdi == null) {
/*  296 */         return;
/*      */       }
/*  298 */       mdi.closeEntry(sViewID);
/*      */     }
/*      */     catch (Exception e) {
/*  301 */       Logger.log(new LogEvent(LOGID, "closePluginViews", e));
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean dispose(boolean for_restart, boolean close_already_in_progress)
/*      */   {
/*      */     try
/*      */     {
/*  309 */       return this.mainWindow.dispose(for_restart, close_already_in_progress);
/*      */     } catch (Exception e) {
/*  311 */       Logger.log(new LogEvent(LOGID, "Disposing MainWindow", e));
/*      */     }
/*  313 */     return false;
/*      */   }
/*      */   
/*      */   public Shell getMainShell()
/*      */   {
/*  318 */     return this.mainWindow == null ? null : this.mainWindow.getShell();
/*      */   }
/*      */   
/*      */   public UISWTView[] getPluginViews()
/*      */   {
/*      */     try {
/*  324 */       return new UISWTView[0];
/*      */     } catch (Exception e) {
/*  326 */       Logger.log(new LogEvent(LOGID, "getPluginViews", e));
/*      */     }
/*      */     
/*  329 */     return new UISWTView[0];
/*      */   }
/*      */   
/*      */   public UISWTInstanceImpl getSWTPluginInstanceImpl()
/*      */   {
/*      */     try {
/*  335 */       return this.mainWindow.getUISWTInstanceImpl();
/*      */     } catch (Exception e) {
/*  337 */       Logger.log(new LogEvent(LOGID, "getSWTPluginInstanceImpl", e));
/*      */     }
/*      */     
/*  340 */     return null;
/*      */   }
/*      */   
/*      */   public void openPluginView(String sParentID, String sViewID, UISWTViewEventListener l, Object dataSource, boolean bSetFocus)
/*      */   {
/*      */     try
/*      */     {
/*  347 */       MultipleDocumentInterfaceSWT mdi = getMDISWT();
/*      */       
/*  349 */       if (mdi != null)
/*      */       {
/*  351 */         String sidebarParentID = null;
/*      */         
/*  353 */         if ("MyTorrents".equals(sParentID)) {
/*  354 */           sidebarParentID = "header.transfers";
/*  355 */         } else if ("Main".equals(sParentID)) {
/*  356 */           sidebarParentID = "header.plugins";
/*      */         } else {
/*  358 */           System.err.println("Can't find parent " + sParentID + " for " + sViewID);
/*      */         }
/*      */         
/*  361 */         MdiEntry entry = mdi.createEntryFromEventListener(sidebarParentID, l, sViewID, true, dataSource, null);
/*      */         
/*  363 */         if (bSetFocus) {
/*  364 */           mdi.showEntryByID(sViewID);
/*  365 */         } else if ((entry instanceof BaseMdiEntry))
/*      */         {
/*      */ 
/*      */ 
/*  369 */           ((BaseMdiEntry)entry).build();
/*      */         }
/*      */       }
/*      */     } catch (Exception e) {
/*  373 */       Logger.log(new LogEvent(LOGID, "openPluginView", e));
/*      */     }
/*      */   }
/*      */   
/*      */   public void refreshIconBar()
/*      */   {
/*      */     try
/*      */     {
/*  381 */       SkinView[] tbSkinViews = SkinViewManager.getMultiByClass(ToolBarView.class);
/*  382 */       if (tbSkinViews != null) {
/*  383 */         for (SkinView skinview : tbSkinViews) {
/*  384 */           if ((skinview instanceof ToolBarView)) {
/*  385 */             ToolBarView tb = (ToolBarView)skinview;
/*  386 */             if (tb.isVisible()) {
/*  387 */               tb.refreshCoreToolBarItems();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/*  394 */       Logger.log(new LogEvent(LOGID, "refreshIconBar", e));
/*      */     }
/*      */   }
/*      */   
/*      */   public void refreshLanguage()
/*      */   {
/*      */     try
/*      */     {
/*  402 */       this.mainWindow.setSelectedLanguageItem();
/*      */     }
/*      */     catch (Exception e) {
/*  405 */       Logger.log(new LogEvent(LOGID, "refreshLanguage", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removePluginView(String viewID)
/*      */   {
/*      */     try
/*      */     {
/*  414 */       PluginsMenuHelper.getInstance().removePluginViews(viewID);
/*      */     }
/*      */     catch (Exception e) {
/*  417 */       Logger.log(new LogEvent(LOGID, "removePluginView", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setStatusText(final String string)
/*      */   {
/*  424 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  426 */         IMainStatusBar sb = UIFunctionsImpl.this.getMainStatusBar();
/*  427 */         if (sb != null) {
/*  428 */           sb.setStatusText(string);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void setStatusText(final int statustype, final String string, final UIStatusTextClickListener l)
/*      */   {
/*  437 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*      */       public void runSupport() {
/*  439 */         IMainStatusBar sb = UIFunctionsImpl.this.getMainStatusBar();
/*  440 */         if (sb != null) {
/*  441 */           sb.setStatusText(statustype, string, l);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public IMainStatusBar getMainStatusBar()
/*      */   {
/*  449 */     if (this.mainWindow == null) {
/*  450 */       return null;
/*      */     }
/*  452 */     return this.mainWindow.getMainStatusBar();
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public void openView(int viewID, Object data)
/*      */   {
/*  459 */     MultipleDocumentInterface mdi = getMDI();
/*  460 */     if (viewID == 4) {
/*  461 */       mdi.showEntryByID("ConfigView", data);
/*  462 */     } else if (viewID == 5) {
/*  463 */       mdi.showEntryByID("DMDetails", data);
/*      */     }
/*  465 */     else if (viewID == 8) {
/*  466 */       mdi.showEntryByID("Library", data);
/*      */     }
/*      */     else {
/*  469 */       System.err.println("DEPRECATED -- Use getMDI().showEntryByID(" + viewID + "..)");
/*      */     }
/*      */   }
/*      */   
/*      */   public UISWTInstance getUISWTInstance()
/*      */   {
/*  475 */     UISWTInstanceImpl impl = this.mainWindow.getUISWTInstanceImpl();
/*  476 */     if (impl == null) {
/*  477 */       Debug.out("No uiswtinstanceimpl");
/*      */     }
/*  479 */     return impl;
/*      */   }
/*      */   
/*      */   public void viewURL(String url, String target, String sourceRef)
/*      */   {
/*  484 */     viewURL(url, target, 0, 0, true, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean viewURL(final String url, final String target, final int w, final int h, final boolean allowResize, final boolean isModal)
/*      */   {
/*  490 */     this.mainWindow.getShell().getDisplay().syncExec(new AERunnable() {
/*      */       public void runSupport() {
/*  492 */         String realURL = url;
/*  493 */         ContentNetwork cn = ContentNetworkUtils.getContentNetworkFromTarget(target);
/*  494 */         if ((!realURL.startsWith("http")) && (!realURL.startsWith("#")))
/*      */         {
/*  496 */           if ("_blank".equals(target)) {
/*  497 */             realURL = cn.getExternalSiteRelativeURL(realURL, false);
/*      */           } else {
/*  499 */             realURL = cn.getSiteRelativeURL(realURL, false);
/*      */           }
/*      */         }
/*  502 */         if (target == null) {
/*  503 */           if (UrlFilter.getInstance().urlCanRPC(realURL)) {
/*  504 */             realURL = cn.appendURLSuffix(realURL, false, true);
/*      */           }
/*      */           
/*  507 */           AEProxyFactory.PluginHTTPProxy proxy = null;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  517 */             BrowserWindow window = new BrowserWindow(UIFunctionsImpl.this.mainWindow.getShell(), realURL, w, h, allowResize, isModal);
/*      */             
/*      */ 
/*  520 */             window.waitUntilClosed();
/*      */ 
/*      */           }
/*      */           catch (Throwable e) {}finally
/*      */           {
/*      */ 
/*  526 */             if (proxy != null)
/*      */             {
/*  528 */               proxy.destroy();
/*      */             }
/*      */           }
/*      */         } else {
/*  532 */           UIFunctionsImpl.this.showURL(realURL, target);
/*      */         }
/*      */       }
/*  535 */     });
/*  536 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean viewURL(final String url, final String target, final double w, double h, final boolean allowResize, boolean isModal)
/*      */   {
/*  542 */     this.mainWindow.getShell().getDisplay().syncExec(new AERunnable() {
/*      */       public void runSupport() {
/*  544 */         String realURL = url;
/*  545 */         ContentNetwork cn = ContentNetworkUtils.getContentNetworkFromTarget(target);
/*  546 */         if (!realURL.startsWith("http")) {
/*  547 */           if ("_blank".equals(target)) {
/*  548 */             realURL = cn.getExternalSiteRelativeURL(realURL, false);
/*      */           } else {
/*  550 */             realURL = cn.getSiteRelativeURL(realURL, false);
/*      */           }
/*      */         }
/*  553 */         if (target == null) {
/*  554 */           if (UrlFilter.getInstance().urlCanRPC(realURL)) {
/*  555 */             realURL = cn.appendURLSuffix(realURL, false, true);
/*      */           }
/*      */           
/*  558 */           AEProxyFactory.PluginHTTPProxy proxy = null;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  568 */             BrowserWindow window = new BrowserWindow(UIFunctionsImpl.this.mainWindow.getShell(), realURL, w, allowResize, this.val$allowResize, this.val$isModal);
/*      */             
/*  570 */             window.waitUntilClosed();
/*      */ 
/*      */           }
/*      */           catch (Throwable e) {}finally
/*      */           {
/*      */ 
/*  576 */             if (proxy != null)
/*      */             {
/*  578 */               proxy.destroy();
/*      */             }
/*      */           }
/*      */         } else {
/*  582 */           UIFunctionsImpl.this.showURL(realURL, target);
/*      */         }
/*      */       }
/*  585 */     });
/*  586 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void showURL(final String url, String target)
/*      */   {
/*  596 */     if ("_blank".equalsIgnoreCase(target)) {
/*  597 */       Utils.launch(url);
/*  598 */       return;
/*      */     }
/*      */     
/*  601 */     if (target.startsWith("tab-")) {
/*  602 */       target = target.substring(4);
/*      */     }
/*      */     
/*  605 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*      */     
/*  607 */     if ("Plus".equals(target)) {
/*  608 */       SBC_PlusFTUX.setSourceRef(url.substring(1));
/*  609 */       mdi.showEntryByID(target);
/*  610 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  616 */     if ((mdi == null) || (!mdi.showEntryByID(target))) {
/*  617 */       Utils.launch(url);
/*  618 */       return;
/*      */     }
/*      */     
/*  621 */     MdiEntry entry = mdi.getEntry(target);
/*  622 */     entry.addListener(new MdiEntryOpenListener()
/*      */     {
/*      */       public void mdiEntryOpen(MdiEntry entry) {
/*  625 */         entry.removeListener(this);
/*      */         
/*  627 */         UIFunctionsImpl.this.mainWindow.setVisible(true, true);
/*      */         
/*  629 */         if (!(entry instanceof SideBarEntrySWT)) {
/*  630 */           return;
/*      */         }
/*  632 */         SideBarEntrySWT entrySWT = (SideBarEntrySWT)entry;
/*      */         
/*  634 */         SWTSkinObjectBrowser soBrowser = SWTSkinUtils.findBrowserSO(entrySWT.getSkinObject());
/*      */         
/*  636 */         if (soBrowser != null)
/*      */         {
/*  638 */           if ((url == null) || (url.length() == 0)) {
/*  639 */             soBrowser.restart();
/*      */           } else {
/*  641 */             String fullURL = url;
/*  642 */             if (UrlFilter.getInstance().urlCanRPC(url))
/*      */             {
/*      */ 
/*  645 */               fullURL = ConstantsVuze.getDefaultContentNetwork().appendURLSuffix(url, false, true);
/*      */             }
/*      */             
/*      */ 
/*  649 */             soBrowser.setURL(fullURL);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void promptUser(String title, String text, String[] buttons, int defaultOption, String rememberID, String rememberText, boolean rememberByDefault, int autoCloseInMS, UserPrompterResultListener l)
/*      */   {
/*  660 */     MessageBoxShell.open(getMainShell(), title, text, buttons, defaultOption, rememberID, rememberText, rememberByDefault, autoCloseInMS, l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UIFunctionsUserPrompter getUserPrompter(String title, String text, String[] buttons, int defaultOption)
/*      */   {
/*  669 */     MessageBoxShell mb = new MessageBoxShell(title, text, buttons, defaultOption);
/*      */     
/*  671 */     return mb;
/*      */   }
/*      */   
/*      */   public boolean isGlobalTransferBarShown() {
/*  675 */     if (!AzureusCoreFactory.isCoreRunning()) {
/*  676 */       return false;
/*      */     }
/*  678 */     return AllTransfersBar.getManager().isOpen(AzureusCoreFactory.getSingleton().getGlobalManager());
/*      */   }
/*      */   
/*      */   public void showGlobalTransferBar()
/*      */   {
/*  683 */     AllTransfersBar.open(getMainShell());
/*      */   }
/*      */   
/*      */ 
/*      */   public void closeGlobalTransferBar() {}
/*      */   
/*      */   public void refreshTorrentMenu()
/*      */   {
/*  691 */     if (!this.isTorrentMenuVisible) {
/*  692 */       return;
/*      */     }
/*      */     try {
/*  695 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  697 */           MenuItem torrentItem = MenuFactory.findMenuItem(UIFunctionsImpl.this.mainWindow.getMainMenu().getMenu("menu.bar"), "MainWindow.menu.torrent", false);
/*      */           
/*      */ 
/*      */ 
/*  701 */           if (null != torrentItem)
/*      */           {
/*  703 */             org.gudy.azureus2.core3.download.DownloadManager[] dms = SelectedContentManager.getDMSFromSelectedContent();
/*      */             
/*  705 */             org.gudy.azureus2.core3.download.DownloadManager[] dm_final = dms;
/*  706 */             boolean detailed_view_final = false;
/*  707 */             if (null == dm_final) {
/*  708 */               torrentItem.setEnabled(false);
/*      */             } else {
/*  710 */               com.aelitis.azureus.ui.common.table.TableView<?> tv = SelectedContentManager.getCurrentlySelectedTableView();
/*      */               
/*  712 */               torrentItem.getMenu().setData("TableView", tv);
/*  713 */               torrentItem.getMenu().setData("downloads", dm_final);
/*  714 */               torrentItem.getMenu().setData("is_detailed_view", Boolean.valueOf(false));
/*      */               
/*  716 */               torrentItem.setEnabled(true);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Exception e) {
/*  723 */       Logger.log(new LogEvent(LOGID, "refreshTorrentMenu", e));
/*      */     }
/*      */   }
/*      */   
/*      */   public IMainMenu createMainMenu(Shell shell)
/*      */   {
/*  729 */     boolean uiClassic = COConfigurationManager.getStringParameter("ui").equals("az2");
/*  730 */     IMainMenu menu; IMainMenu menu; if (uiClassic) {
/*  731 */       menu = new org.gudy.azureus2.ui.swt.mainwindow.MainMenu(shell);
/*      */     } else {
/*  733 */       menu = new MainMenu(this.skin, shell);
/*      */     }
/*  735 */     return menu;
/*      */   }
/*      */   
/*      */   public SWTSkin getSkin() {
/*  739 */     return this.skin;
/*      */   }
/*      */   
/*      */   public void setSkin(SWTSkin skin) {
/*  743 */     this.skin = skin;
/*      */   }
/*      */   
/*      */   public IMainWindow getMainWindow() {
/*  747 */     return this.mainWindow;
/*      */   }
/*      */   
/*      */   public UIUpdater getUIUpdater()
/*      */   {
/*  752 */     return com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT.getInstance();
/*      */   }
/*      */   
/*      */   public void closeAllDetails()
/*      */   {
/*  757 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  758 */     if (mdi == null) {
/*  759 */       return;
/*      */     }
/*  761 */     MdiEntry[] sideBarEntries = mdi.getEntries();
/*  762 */     for (int i = 0; i < sideBarEntries.length; i++) {
/*  763 */       MdiEntry entry = sideBarEntries[i];
/*  764 */       String id = entry.getId();
/*  765 */       if ((id != null) && (id.startsWith("DMDetails_"))) {
/*  766 */         mdi.closeEntry(id);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasDetailViews()
/*      */   {
/*  774 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  775 */     if (mdi == null) {
/*  776 */       return false;
/*      */     }
/*      */     
/*  779 */     MdiEntry[] sideBarEntries = mdi.getEntries();
/*  780 */     for (int i = 0; i < sideBarEntries.length; i++) {
/*  781 */       MdiEntry entry = sideBarEntries[i];
/*  782 */       String id = entry.getId();
/*  783 */       if ((id != null) && (id.startsWith("DMDetails_"))) {
/*  784 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  788 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void performAction(int action_id, Object args, final UIFunctions.actionListener listener)
/*      */   {
/*  797 */     if (action_id == 1)
/*      */     {
/*  799 */       FullUpdateWindow.handleUpdate((String)args, listener);
/*      */     }
/*  801 */     else if (action_id == 2)
/*      */     {
/*  803 */       String MSG_PREFIX = "UpdateMonitor.messagebox.";
/*      */       
/*  805 */       String title = MessageText.getString(MSG_PREFIX + "restart.title");
/*      */       
/*  807 */       String text = MessageText.getString(MSG_PREFIX + "restart.text");
/*      */       
/*  809 */       bringToFront();
/*      */       
/*  811 */       boolean no_timeout = ((args instanceof Boolean)) && (((Boolean)args).booleanValue());
/*      */       
/*  813 */       int timeout = 180000;
/*      */       
/*  815 */       if ((no_timeout) || (!PluginInitializer.getDefaultInterface().getPluginManager().isSilentRestartEnabled()))
/*      */       {
/*  817 */         timeout = -1;
/*      */       }
/*      */       
/*  820 */       MessageBoxShell messageBoxShell = new MessageBoxShell(title, text, new String[] { MessageText.getString("UpdateWindow.restart"), MessageText.getString("UpdateWindow.restartLater") }, 0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  825 */       messageBoxShell.setAutoCloseInMS(timeout);
/*  826 */       messageBoxShell.setParent(getMainShell());
/*  827 */       messageBoxShell.setOneInstanceOf(MSG_PREFIX);
/*  828 */       messageBoxShell.open(new UserPrompterResultListener() {
/*      */         public void prompterClosed(int result) {
/*  830 */           listener.actionComplete(Boolean.valueOf(result == 0));
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/*  835 */       Debug.out("Unknown action " + action_id);
/*      */     }
/*      */   }
/*      */   
/*      */   public Shell showCoreWaitDlg()
/*      */   {
/*  841 */     final SkinnedDialog closeDialog = new SkinnedDialog("skin3_dlg_coreloading", "coreloading.body", 67616);
/*      */     
/*      */ 
/*      */ 
/*  845 */     closeDialog.setTitle(MessageText.getString("dlg.corewait.title"));
/*  846 */     SWTSkin skin = closeDialog.getSkin();
/*  847 */     SWTSkinObjectButton soButton = (SWTSkinObjectButton)skin.getSkinObject("close");
/*      */     
/*  849 */     final SWTSkinObjectText soWaitTask = (SWTSkinObjectText)skin.getSkinObject("task");
/*      */     
/*  851 */     final SWTSkinObject soWaitProgress = skin.getSkinObject("progress");
/*  852 */     if (soWaitProgress != null) {
/*  853 */       soWaitProgress.getControl().addPaintListener(new PaintListener() {
/*      */         public void paintControl(PaintEvent e) {
/*  855 */           Control c = (Control)e.widget;
/*  856 */           Point size = c.getSize();
/*  857 */           e.gc.setBackground(ColorCache.getColor(e.display, "#23a7df"));
/*  858 */           Object data = soWaitProgress.getData("progress");
/*  859 */           if ((data instanceof Long)) {
/*  860 */             int waitProgress = ((Long)data).intValue();
/*  861 */             int breakX = size.x * waitProgress / 100;
/*  862 */             e.gc.fillRectangle(0, 0, breakX, size.y);
/*  863 */             e.gc.setBackground(ColorCache.getColor(e.display, "#cccccc"));
/*  864 */             e.gc.fillRectangle(breakX, 0, size.x - breakX, size.y);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  870 */     if (!AzureusCoreFactory.isCoreRunning()) {
/*  871 */       final Initializer initializer = Initializer.getLastInitializer();
/*  872 */       if (initializer != null) {
/*  873 */         initializer.addListener(new InitializerListener() {
/*      */           public void reportPercent(final int percent) {
/*  875 */             Utils.execSWTThread(new AERunnable() {
/*      */               public void runSupport() {
/*  877 */                 if ((UIFunctionsImpl.14.this.val$soWaitProgress != null) && (!UIFunctionsImpl.14.this.val$soWaitProgress.isDisposed())) {
/*  878 */                   UIFunctionsImpl.14.this.val$soWaitProgress.setData("progress", new Long(percent));
/*  879 */                   UIFunctionsImpl.14.this.val$soWaitProgress.getControl().redraw();
/*  880 */                   UIFunctionsImpl.14.this.val$soWaitProgress.getControl().update();
/*      */                 }
/*      */               }
/*      */             });
/*  884 */             if (percent > 100) {
/*  885 */               initializer.removeListener(this);
/*      */             }
/*      */           }
/*      */           
/*      */           public void reportCurrentTask(String currentTask) {
/*  890 */             if ((soWaitTask != null) && (!soWaitTask.isDisposed())) {
/*  891 */               soWaitTask.setText(currentTask);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*  898 */     if (soButton != null) {
/*  899 */       soButton.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*      */       {
/*      */         public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/*  902 */           closeDialog.close();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  907 */     closeDialog.addCloseListener(new SkinnedDialog.SkinnedDialogClosedListener()
/*      */     {
/*      */       public void skinDialogClosed(SkinnedDialog dialog) {}
/*      */ 
/*  911 */     });
/*  912 */     closeDialog.open();
/*  913 */     return closeDialog.getShell();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void doSearch(final String sSearchText)
/*      */   {
/*  922 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  924 */         UIFunctionsImpl.this.doSearch(sSearchText, false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void doSearch(String sSearchText, boolean toSubscribe)
/*      */   {
/*  934 */     if (sSearchText.length() == 0) {
/*  935 */       return;
/*      */     }
/*      */     
/*  938 */     if (checkForSpecialSearchTerm(sSearchText))
/*      */     {
/*  940 */       return;
/*      */     }
/*      */     
/*  943 */     SearchHandler.handleSearch(sSearchText, toSubscribe);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static boolean checkForSpecialSearchTerm(String str)
/*      */   {
/*  950 */     str = str.trim();
/*      */     
/*  952 */     String hit = UrlUtils.parseTextForURL(str, true, true);
/*      */     
/*  954 */     if (hit == null)
/*      */     {
/*      */       try {
/*  957 */         File f = new File(str);
/*      */         
/*  959 */         if (f.isFile())
/*      */         {
/*  961 */           String name = f.getName().toLowerCase();
/*      */           
/*  963 */           if ((name.endsWith(".torrent")) || (name.endsWith(".vuze")))
/*      */           {
/*  965 */             UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*      */             
/*  967 */             if (uif != null)
/*      */             {
/*  969 */               uif.openTorrentOpenOptions(null, null, new String[] { f.getAbsolutePath() }, false, false);
/*      */               
/*      */ 
/*      */ 
/*  973 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*  980 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  987 */       URL hit_url = new URL(hit);
/*      */       
/*      */       URL url;
/*      */       URL url;
/*  991 */       if (hit_url.getProtocol().equals("tor"))
/*      */       {
/*  993 */         url = new URL(hit.substring(4));
/*      */       }
/*      */       else
/*      */       {
/*  997 */         url = hit_url;
/*      */       }
/*      */       
/* 1000 */       String path = url.getPath();
/*      */       
/* 1002 */       if (((path.length() == 0) || (path.equals("/"))) && (url.getQuery() == null))
/*      */       {
/* 1004 */         Utils.launch(hit_url.toExternalForm());
/*      */         
/* 1006 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1011 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*      */     
/* 1013 */     new FileDownloadWindow(uiFunctions.getMainShell(), hit, null, null, true);
/*      */     
/* 1015 */     return true;
/*      */   }
/*      */   
/*      */   public void promptForSearch()
/*      */   {
/* 1020 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("Button.search", "search.dialog.text");
/* 1021 */     entryWindow.prompt(new org.gudy.azureus2.plugins.ui.UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver receiver) {
/* 1023 */         if (receiver.hasSubmittedInput()) {
/* 1024 */           UIFunctionsImpl.this.doSearch(receiver.getSubmittedInput());
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public MultipleDocumentInterface getMDI() {
/* 1031 */     return (MultipleDocumentInterface)SkinViewManager.getByViewID("mdi");
/*      */   }
/*      */   
/*      */   public MultipleDocumentInterfaceSWT getMDISWT() {
/* 1035 */     return (MultipleDocumentInterfaceSWT)SkinViewManager.getByViewID("mdi");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void showErrorMessage(final String keyPrefix, final String details, final String[] textParams)
/*      */   {
/* 1046 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1048 */         Shell mainShell = UIFunctionsImpl.this.getMainShell();
/* 1049 */         if ((mainShell.getDisplay().getActiveShell() != null) || (mainShell.isFocusControl()))
/*      */         {
/* 1051 */           new MessageSlideShell(Display.getCurrent(), 1, keyPrefix, details, textParams, -1);
/*      */         }
/*      */         else {
/* 1054 */           MessageBoxShell mb = new MessageBoxShell(32, keyPrefix, textParams);
/*      */           
/* 1056 */           mb.open(null);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public void forceNotify(final int iconID, final String title, final String text, final String details, final Object[] relatedObjects, final int timeoutSecs)
/*      */   {
/* 1065 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1067 */         int swtIconID = 2;
/* 1068 */         switch (iconID) {
/*      */         case 1: 
/* 1070 */           swtIconID = 8;
/* 1071 */           break;
/*      */         
/*      */         case 2: 
/* 1074 */           swtIconID = 1;
/*      */         }
/*      */         
/*      */         
/* 1078 */         new MessageSlideShell(SWTThread.getInstance().getDisplay(), swtIconID, title, text, details, relatedObjects, timeoutSecs);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void installPlugin(String plugin_id, String resource_prefix, UIFunctions.actionListener listener)
/*      */   {
/* 1091 */     new SimplePluginInstaller(plugin_id, resource_prefix, listener);
/*      */   }
/*      */   
/*      */   public UIToolBarManager getToolBarManager() {
/* 1095 */     return UIToolBarManagerImpl.getInstance();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void runOnUIThread(int ui_type, Runnable runnable)
/*      */   {
/* 1103 */     if (ui_type == 1)
/*      */     {
/* 1105 */       Utils.execSWTThread(runnable);
/*      */     }
/*      */     else
/*      */     {
/* 1109 */       runnable.run();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isProgramInstalled(String extension, String name)
/*      */   {
/* 1118 */     if (!extension.startsWith("."))
/*      */     {
/* 1120 */       extension = "." + extension;
/*      */     }
/*      */     
/* 1123 */     Program program = Program.findProgram(extension);
/*      */     
/* 1125 */     return program == null ? false : program.getName().toLowerCase(Locale.US).contains(name.toLowerCase(Locale.US));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void openRemotePairingWindow() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void playOrStreamDataSource(Object ds, String referal, boolean launch_already_checked, boolean complete_only)
/*      */   {
/* 1142 */     TorrentListViewsUtils.playOrStreamDataSource(ds, referal, launch_already_checked, complete_only);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHideAll(boolean hidden)
/*      */   {
/* 1149 */     this.mainWindow.setHideAll(hidden);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean addTorrentWithOptions(boolean force, TorrentOpenOptions torrentOptions)
/*      */   {
/* 1157 */     Map<String, Object> add_options = new java.util.HashMap();
/*      */     
/* 1159 */     add_options.put("forceOpen", Boolean.valueOf(force));
/*      */     
/* 1161 */     return addTorrentWithOptions(torrentOptions, add_options);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean addTorrentWithOptions(final TorrentOpenOptions torrentOptions, Map<String, Object> addOptions)
/*      */   {
/* 1169 */     Boolean is_silent = (Boolean)addOptions.get("silent");
/*      */     
/* 1171 */     if (is_silent == null)
/*      */     {
/* 1173 */       is_silent = Boolean.valueOf(false);
/*      */     }
/*      */     
/* 1176 */     if (AzureusCoreFactory.isCoreRunning())
/*      */     {
/* 1178 */       AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */       
/* 1180 */       GlobalManager gm = core.getGlobalManager();
/*      */       
/*      */ 
/*      */ 
/* 1184 */       TOTorrent torrent = torrentOptions.getTorrent();
/*      */       
/* 1186 */       org.gudy.azureus2.core3.download.DownloadManager existingDownload = gm.getDownloadManager(torrent);
/*      */       
/* 1188 */       if (existingDownload != null)
/*      */       {
/* 1190 */         if (!is_silent.booleanValue())
/*      */         {
/* 1192 */           final String fExistingName = existingDownload.getDisplayName();
/* 1193 */           final org.gudy.azureus2.core3.download.DownloadManager fExistingDownload = existingDownload;
/*      */           
/* 1195 */           fExistingDownload.fireGlobalManagerEvent(1);
/*      */           
/* 1197 */           Utils.execSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/* 1199 */               boolean can_merge = TorrentUtils.canMergeAnnounceURLs(torrentOptions.getTorrent(), fExistingDownload.getTorrent());
/*      */               
/*      */ 
/* 1202 */               long existed_for = SystemTime.getCurrentTime() - fExistingDownload.getCreationTime();
/*      */               
/* 1204 */               Shell mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/*      */               
/* 1206 */               if (((Display.getDefault().getActiveShell() == null) || (!mainShell.isVisible()) || (mainShell.getMinimized())) && (!can_merge))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1214 */                 if (existed_for > 15000L)
/*      */                 {
/* 1216 */                   new MessageSlideShell(Display.getCurrent(), 2, "OpenTorrentWindow.mb.alreadyExists", null, new String[] { ":" + torrentOptions.sOriginatingLocation, fExistingName, MessageText.getString("OpenTorrentWindow.mb.alreadyExists.default.name") }, new Object[] { fExistingDownload }, -1);
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/* 1227 */               else if (can_merge)
/*      */               {
/* 1229 */                 String text = MessageText.getString("OpenTorrentWindow.mb.alreadyExists.text", new String[] { ":" + torrentOptions.sOriginatingLocation, fExistingName, MessageText.getString("OpenTorrentWindow.mb.alreadyExists.default.name") });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1236 */                 text = text + "\n\n" + MessageText.getString("openTorrentWindow.mb.alreadyExists.merge");
/*      */                 
/*      */ 
/* 1239 */                 MessageBoxShell mb = new MessageBoxShell(192, MessageText.getString("OpenTorrentWindow.mb.alreadyExists.title"), text);
/*      */                 
/*      */ 
/* 1242 */                 mb.open(new UserPrompterResultListener() {
/*      */                   public void prompterClosed(int result) {
/* 1244 */                     if (result == 64)
/*      */                     {
/* 1246 */                       TorrentUtils.mergeAnnounceURLs(UIFunctionsImpl.21.this.val$torrentOptions.getTorrent(), UIFunctionsImpl.21.this.val$fExistingDownload.getTorrent());
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/*      */ 
/*      */                 });
/*      */               }
/* 1254 */               else if (existed_for > 15000L)
/*      */               {
/* 1256 */                 MessageBoxShell mb = new MessageBoxShell(32, "OpenTorrentWindow.mb.alreadyExists", new String[] { ":" + torrentOptions.sOriginatingLocation, fExistingName, MessageText.getString("OpenTorrentWindow.mb.alreadyExists.default.name") });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1262 */                 mb.open(null);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1270 */         if (torrentOptions.bDeleteFileOnCancel)
/*      */         {
/* 1272 */           File torrentFile = new File(torrentOptions.sFileName);
/*      */           
/* 1274 */           torrentFile.delete();
/*      */         }
/*      */         
/* 1277 */         return true;
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1282 */         final DownloadStub archived = core.getPluginManager().getDefaultPluginInterface().getDownloadManager().lookupDownloadStub(torrent.getHash());
/*      */         
/* 1284 */         if (archived != null)
/*      */         {
/* 1286 */           if (is_silent.booleanValue())
/*      */           {
/*      */ 
/*      */ 
/* 1290 */             archived.destubbify();
/*      */             
/* 1292 */             if (torrentOptions.bDeleteFileOnCancel)
/*      */             {
/* 1294 */               File torrentFile = new File(torrentOptions.sFileName);
/*      */               
/* 1296 */               torrentFile.delete();
/*      */             }
/*      */             
/* 1299 */             return true;
/*      */           }
/*      */           
/*      */ 
/* 1303 */           Utils.execSWTThread(new AERunnable()
/*      */           {
/*      */             public void runSupport() {
/* 1306 */               Shell mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/*      */               
/* 1308 */               String existingName = archived.getName();
/*      */               
/* 1310 */               if ((Display.getDefault().getActiveShell() == null) || (!mainShell.isVisible()) || (mainShell.getMinimized()))
/*      */               {
/*      */ 
/*      */ 
/* 1314 */                 new MessageSlideShell(Display.getCurrent(), 2, "OpenTorrentWindow.mb.inArchive", null, new String[] { existingName }, new Object[0], -1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1326 */                 MessageBoxShell mb = new MessageBoxShell(32, "OpenTorrentWindow.mb.inArchive", new String[] { existingName });
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1334 */                 mb.open(null);
/*      */               }
/*      */               
/*      */             }
/* 1338 */           });
/* 1339 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1344 */         Debug.out(e);
/*      */       }
/*      */       
/* 1347 */       if (!is_silent.booleanValue()) {
/*      */         try
/*      */         {
/* 1350 */           DownloadHistoryManager dlm = (DownloadHistoryManager)core.getGlobalManager().getDownloadHistoryManager();
/*      */           
/* 1352 */           final long[] existing = dlm.getDates(torrentOptions.getTorrent().getHash());
/*      */           
/* 1354 */           if (existing != null)
/*      */           {
/* 1356 */             long redownloaded = existing[3];
/*      */             
/* 1358 */             if (SystemTime.getCurrentTime() - redownloaded > 600000L)
/*      */             {
/* 1360 */               Utils.execSWTThread(new AERunnable() {
/*      */                 public void runSupport() {
/* 1362 */                   Shell mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/*      */                   
/* 1364 */                   if ((mainShell != null) && (!mainShell.isDisposed()))
/*      */                   {
/* 1366 */                     new MessageSlideShell(mainShell.getDisplay(), 2, "OpenTorrentWindow.mb.inHistory", null, new String[] { torrentOptions.getTorrentName(), new SimpleDateFormat().format(new Date(existing[0])) }, new Object[0], -1);
/*      */ 
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */ 
/*      */               });
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*      */ 
/* 1383 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1389 */     Boolean force = (Boolean)addOptions.get("forceOpen");
/*      */     
/* 1391 */     if (force == null)
/*      */     {
/* 1393 */       force = Boolean.valueOf(false);
/*      */     }
/*      */     
/* 1396 */     if (!force.booleanValue())
/*      */     {
/* 1398 */       TOTorrent torrent = torrentOptions.getTorrent();
/*      */       
/* 1400 */       boolean is_featured = (torrent != null) && (PlatformTorrentUtils.isFeaturedContent(torrent));
/*      */       
/* 1402 */       String showAgainMode = COConfigurationManager.getStringParameter("ui.addtorrent.openoptions");
/*      */       
/* 1404 */       if ((is_featured) || (is_silent.booleanValue()) || ((showAgainMode != null) && ((showAgainMode.equals("never")) || ((showAgainMode.equals("many")) && (torrentOptions.getFiles() != null) && (torrentOptions.getFiles().length == 1)))))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1412 */         boolean looks_good = false;
/*      */         
/* 1414 */         String save_loc = torrentOptions.getParentDir().trim();
/*      */         
/* 1416 */         if (save_loc.length() != 0)
/*      */         {
/*      */ 
/*      */ 
/* 1420 */           if (!save_loc.startsWith("."))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1425 */             File f = new File(save_loc);
/*      */             
/* 1427 */             if (!f.exists())
/*      */             {
/* 1429 */               f.mkdirs();
/*      */             }
/*      */             
/* 1432 */             if ((f.isDirectory()) && (FileUtil.canWriteToDirectory(f)))
/*      */             {
/* 1434 */               if (!f.equals(AETemporaryFileHandler.getTempDirectory()))
/*      */               {
/* 1436 */                 looks_good = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1441 */         if (looks_good)
/*      */         {
/* 1443 */           TorrentManagerImpl t_man = TorrentManagerImpl.getSingleton();
/*      */           
/* 1445 */           t_man.optionsAdded(torrentOptions);
/*      */           
/* 1447 */           t_man.optionsAccepted(torrentOptions);
/*      */           
/* 1449 */           boolean ok = TorrentOpener.addTorrent(torrentOptions);
/*      */           
/* 1451 */           t_man.optionsRemoved(torrentOptions);
/*      */           
/* 1453 */           return ok;
/*      */         }
/*      */         
/* 1456 */         torrentOptions.setParentDir("");
/*      */         
/* 1458 */         if (is_silent.booleanValue())
/*      */         {
/* 1460 */           return false;
/*      */         }
/*      */         
/*      */ 
/* 1464 */         MessageBoxShell mb = new MessageBoxShell(33, "OpenTorrentWindow.mb.invaliddefsave", new String[] { save_loc });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1470 */         mb.open(new UserPrompterResultListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void prompterClosed(int result)
/*      */           {
/*      */ 
/* 1477 */             OpenTorrentOptionsWindow.addTorrent(torrentOptions);
/*      */           }
/*      */           
/*      */ 
/* 1481 */         });
/* 1482 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1486 */     if (is_silent.booleanValue())
/*      */     {
/* 1488 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1492 */     OpenTorrentOptionsWindow.addTorrent(torrentOptions);
/*      */     
/* 1494 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void openTorrentOpenOptions(Shell shell, String sPathOfFilesToOpen, String[] sFilesToOpen, boolean defaultToStopped, boolean forceOpen)
/*      */   {
/* 1504 */     TorrentOpenOptions torrentOptions = new TorrentOpenOptions();
/* 1505 */     if (defaultToStopped) {
/* 1506 */       torrentOptions.setStartMode(1);
/*      */     }
/* 1508 */     if (sFilesToOpen == null) {
/* 1509 */       new OpenTorrentWindow(shell);
/*      */     }
/*      */     else {
/* 1512 */       TorrentOpener.openTorrentsFromStrings(torrentOptions, shell, sPathOfFilesToOpen, sFilesToOpen, null, null, forceOpen);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void openTorrentOpenOptions(Shell shell, String sPathOfFilesToOpen, String[] sFilesToOpen, Map<String, Object> options)
/*      */   {
/* 1524 */     Boolean _defaultToStopped = (Boolean)options.get("defaultStopped");
/* 1525 */     boolean defaultToStopped = _defaultToStopped != null ? _defaultToStopped.booleanValue() : false;
/*      */     
/* 1527 */     Boolean _hideErrors = (Boolean)options.get("hideErrors");
/* 1528 */     boolean hideErrors = _hideErrors != null ? _hideErrors.booleanValue() : false;
/*      */     
/* 1530 */     TorrentOpenOptions torrentOptions = new TorrentOpenOptions();
/* 1531 */     if (defaultToStopped) {
/* 1532 */       torrentOptions.setStartMode(1);
/*      */     }
/* 1534 */     torrentOptions.setHideErrors(hideErrors);
/*      */     
/* 1536 */     if (sFilesToOpen == null) {
/* 1537 */       new OpenTorrentWindow(shell);
/*      */     }
/*      */     else
/*      */     {
/* 1541 */       Boolean _forceOpen = (Boolean)options.get("forceOpen");
/* 1542 */       boolean forceOpen = _forceOpen != null ? _forceOpen.booleanValue() : false;
/*      */       
/* 1544 */       TorrentOpener.openTorrentsFromStrings(torrentOptions, shell, sPathOfFilesToOpen, sFilesToOpen, null, null, forceOpen);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void openTorrentWindow()
/*      */   {
/* 1554 */     new OpenTorrentWindow(Utils.findAnyShell());
/*      */   }
/*      */   
/*      */   public void showCreateTagDialog(UIFunctions.TagReturner tagReturner)
/*      */   {
/* 1559 */     TagUIUtilsV3.showCreateTagDialog(tagReturner);
/*      */   }
/*      */   
/*      */   public TabbedMdiInterface createTabbedMDI(Composite parent, String id) {
/* 1563 */     return new com.aelitis.azureus.ui.swt.mdi.TabbedMDI(parent, id);
/*      */   }
/*      */   
/*      */   public int adjustPXForDPI(int px) {
/* 1567 */     return Utils.adjustPXForDPI(px);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/UIFunctionsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */