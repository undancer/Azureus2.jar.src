/*     */ package com.aelitis.azureus.ui.swt.shells.main;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleListener;
/*     */ import com.aelitis.azureus.ui.IUIIntializer;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctions.TagReturner;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import com.aelitis.azureus.ui.UIFunctionsUserPrompter;
/*     */ import com.aelitis.azureus.ui.UIStatusTextClickListener;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.TabbedMdiInterface;
/*     */ import com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler.RunStateChangeListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
/*     */ import org.gudy.azureus2.ui.swt.UIExitUtilsSWT;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainMenu;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainWindow;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.minibar.AllTransfersBar;
/*     */ import org.gudy.azureus2.ui.swt.minibar.MiniBarManager;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTInstanceImpl;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;
/*     */ import org.gudy.azureus2.ui.systray.SystemTraySWT;
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
/*     */ public class MainWindowDelayStub
/*     */   implements MainWindow
/*     */ {
/*     */   private Display display;
/*     */   private IUIIntializer initialiser;
/*     */   private Shell shell;
/*     */   private AzureusCore core;
/*  66 */   private AESemaphore core_sem = new AESemaphore("");
/*     */   
/*     */   private volatile MainWindow main_window;
/*     */   
/*     */   private SystemTraySWT swt_tray;
/*     */   
/*  72 */   private volatile UIFunctionsSWT delayed_uif = new UIFunctionsSWTImpl(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MainWindowDelayStub(AzureusCore _core, Display _display, IUIIntializer _uiInitializer)
/*     */   {
/*  80 */     this.core = _core;
/*  81 */     this.display = _display;
/*  82 */     this.initialiser = _uiInitializer;
/*     */     
/*  84 */     init();
/*     */     
/*  86 */     this.core_sem.releaseForever();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MainWindowDelayStub(Display _display, IUIIntializer _uiInitializer)
/*     */   {
/*  94 */     this.display = _display;
/*  95 */     this.initialiser = _uiInitializer;
/*     */     
/*  97 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */   private void init()
/*     */   {
/* 103 */     final AESemaphore sem = new AESemaphore("shell:create");
/*     */     
/* 105 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 112 */           MainWindowDelayStub.this.shell = new Shell(MainWindowDelayStub.this.display, 1264);
/*     */           
/* 114 */           UIFunctionsManagerSWT.setUIFunctions(MainWindowDelayStub.this.delayed_uif);
/*     */           
/* 116 */           boolean bEnableTray = COConfigurationManager.getBooleanParameter("Enable System Tray");
/*     */           
/* 118 */           if (bEnableTray)
/*     */           {
/* 120 */             MainWindowDelayStub.this.swt_tray = SystemTraySWT.getTray();
/*     */           }
/*     */           
/* 123 */           MainHelpers.initTransferBar();
/*     */           
/* 125 */           if (MainWindowDelayStub.this.initialiser != null)
/*     */           {
/* 127 */             MainWindowDelayStub.this.initialiser.initializationComplete();
/*     */             
/* 129 */             MainWindowDelayStub.this.initialiser.abortProgress();
/*     */           }
/*     */           
/* 132 */           AERunStateHandler.addListener(new AERunStateHandler.RunStateChangeListener()
/*     */           {
/*     */ 
/* 135 */             private boolean handled = false;
/*     */             
/*     */ 
/*     */ 
/*     */             public void runStateChanged(long run_state)
/*     */             {
/* 141 */               if ((AERunStateHandler.isDelayedUI()) || (this.handled))
/*     */               {
/* 143 */                 return;
/*     */               }
/*     */               
/* 146 */               this.handled = true;
/*     */               
/* 148 */               MainWindowDelayStub.this.checkMainWindow(); } }, false);
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 153 */           sem.release();
/*     */         }
/*     */         
/*     */       }
/* 157 */     });
/* 158 */     sem.reserve();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void log(String str)
/*     */   {
/* 165 */     Debug.out(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void init(AzureusCore _core)
/*     */   {
/* 172 */     this.core = _core;
/*     */     
/* 174 */     this.core_sem.releaseForever();
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
/*     */   private static abstract interface Fixup
/*     */   {
/*     */     public abstract void fix(MainWindow paramMainWindow);
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
/*     */   private static abstract interface Fixup2
/*     */   {
/*     */     public abstract Object fix(MainWindow paramMainWindow);
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
/*     */   private void checkMainWindow()
/*     */   {
/* 215 */     boolean activated = false;
/*     */     
/* 217 */     synchronized (this)
/*     */     {
/* 219 */       if (this.main_window == null)
/*     */       {
/* 221 */         final AESemaphore wait_sem = new AESemaphore("cmw");
/*     */         
/* 223 */         AzureusCoreLifecycleListener listener = new AzureusCoreLifecycleAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*     */           {
/*     */ 
/*     */ 
/* 231 */             if ((component instanceof UIFunctions))
/*     */             {
/* 233 */               wait_sem.release();
/*     */             }
/*     */             
/*     */           }
/* 237 */         };
/* 238 */         this.core.addLifecycleListener(listener);
/*     */         
/* 240 */         this.main_window = new MainWindowImpl(this.core, this.display, null);
/*     */         
/* 242 */         if (!wait_sem.reserve(30000L))
/*     */         {
/* 244 */           Debug.out("Gave up waiting for UIFunction component to be created");
/*     */         }
/*     */         
/* 247 */         activated = true;
/*     */       }
/*     */     }
/*     */     
/* 251 */     if (activated)
/*     */     {
/* 253 */       AERunStateHandler.setResourceMode(0L);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void fixup(Fixup f)
/*     */   {
/* 261 */     this.core_sem.reserve();
/*     */     
/* 263 */     checkMainWindow();
/*     */     
/* 265 */     f.fix(this.main_window);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object fixup(Fixup2 f)
/*     */   {
/* 272 */     this.core_sem.reserve();
/*     */     
/* 274 */     checkMainWindow();
/*     */     
/* 276 */     return f.fix(this.main_window);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void fixup(Fixup3 f)
/*     */   {
/* 283 */     this.core_sem.reserve();
/*     */     
/* 285 */     checkMainWindow();
/*     */     
/* 287 */     UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */     
/* 289 */     if (uif == this.delayed_uif)
/*     */     {
/* 291 */       Debug.out("eh?");
/*     */     }
/*     */     else
/*     */     {
/* 295 */       f.fix(uif);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object fixup(Fixup4 f)
/*     */   {
/* 303 */     this.core_sem.reserve();
/*     */     
/* 305 */     checkMainWindow();
/*     */     
/* 307 */     UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */     
/* 309 */     if (uif == this.delayed_uif)
/*     */     {
/* 311 */       Debug.out("eh?");
/*     */       
/* 313 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 317 */     return f.fix(uif);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Shell getShell()
/*     */   {
/* 326 */     return this.shell;
/*     */   }
/*     */   
/*     */ 
/*     */   public IMainMenu getMainMenu()
/*     */   {
/* 332 */     (IMainMenu)fixup(new Fixup2() { public Object fix(MainWindow mw) { return mw.getMainMenu(); }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public IMainStatusBar getMainStatusBar()
/*     */   {
/* 339 */     if (this.main_window != null)
/*     */     {
/* 341 */       return this.main_window.getMainStatusBar();
/*     */     }
/*     */     
/* 344 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isReady()
/*     */   {
/* 350 */     log("isReady");
/*     */     
/* 352 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setVisible(final boolean visible, final boolean tryTricks)
/*     */   {
/* 360 */     fixup(new Fixup() { public void fix(MainWindow mw) { mw.setVisible(visible, tryTricks); }
/*     */     });
/*     */   }
/*     */   
/*     */   public UISWTInstanceImpl getUISWTInstanceImpl()
/*     */   {
/* 366 */     log("getUISWTInstanceImpl");
/*     */     
/* 368 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSelectedLanguageItem()
/*     */   {
/* 374 */     log("setSelectedLanguageItem");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean dispose(final boolean for_restart, boolean close_already_in_progress)
/*     */   {
/* 382 */     if (this.main_window != null)
/*     */     {
/* 384 */       return this.main_window.dispose(for_restart, close_already_in_progress);
/*     */     }
/*     */     
/* 387 */     log("dispose");
/*     */     
/* 389 */     UIExitUtilsSWT.uiShutdown();
/*     */     
/* 391 */     if (this.swt_tray != null)
/*     */     {
/* 393 */       this.swt_tray.dispose();
/*     */     }
/*     */     try
/*     */     {
/* 397 */       AllTransfersBar transfer_bar = AllTransfersBar.getBarIfOpen(this.core.getGlobalManager());
/*     */       
/* 399 */       if (transfer_bar != null)
/*     */       {
/* 401 */         transfer_bar.forceSaveLocation();
/*     */       }
/*     */     }
/*     */     catch (Exception ignore) {}
/*     */     
/* 406 */     if (!SWTThread.getInstance().isTerminated()) {
/* 407 */       Utils.getOffOfSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 409 */           if (!SWTThread.getInstance().isTerminated()) {
/* 410 */             SWTThread.getInstance().getInitializer().stopIt(for_restart, false);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 416 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isVisible(int windowElement)
/*     */   {
/* 425 */     log("isVisible");
/*     */     
/* 427 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setVisible(int windowElement, boolean value)
/*     */   {
/* 435 */     log("setVisible");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setHideAll(boolean hide)
/*     */   {
/* 442 */     log("setHideAll");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Rectangle getMetrics(int windowElement)
/*     */   {
/* 449 */     log("getMetrics");
/*     */     
/* 451 */     return null;
/*     */   }
/*     */   
/*     */   private static abstract interface Fixup3 { public abstract void fix(UIFunctionsSWT paramUIFunctionsSWT);
/*     */   }
/*     */   
/*     */   private static abstract interface Fixup4 { public abstract Object fix(UIFunctionsSWT paramUIFunctionsSWT); }
/*     */   
/*     */   private class UIFunctionsSWTImpl implements UIFunctionsSWT { private UIFunctionsSWTImpl() {}
/*     */     
/* 461 */     public int getUIType() { return 1; }
/*     */     
/*     */ 
/*     */ 
/*     */     public void bringToFront()
/*     */     {
/* 467 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.bringToFront(); }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public void bringToFront(final boolean tryTricks)
/*     */     {
/* 474 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.bringToFront(tryTricks); }
/*     */       });
/*     */     }
/*     */     
/*     */     public int getVisibilityState()
/*     */     {
/* 480 */       UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */       
/* 482 */       if ((uif != null) && (uif != this))
/*     */       {
/* 484 */         return uif.getVisibilityState();
/*     */       }
/*     */       
/* 487 */       return 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void runOnUIThread(final int ui_type, final Runnable runnable)
/*     */     {
/* 495 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.runOnUIThread(ui_type, runnable); }
/*     */       });
/*     */     }
/*     */     
/*     */     public void refreshLanguage()
/*     */     {
/* 501 */       MainWindowDelayStub.this.log("refreshLanguage");
/*     */     }
/*     */     
/*     */ 
/*     */     public void refreshIconBar()
/*     */     {
/* 507 */       MainWindowDelayStub.this.log("refreshIconBar");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setStatusText(String string) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setStatusText(int statustype, String string, UIStatusTextClickListener l)
/*     */     {
/* 522 */       MainWindowDelayStub.this.log("setStatusText");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean dispose(boolean for_restart, boolean close_already_in_progress)
/*     */     {
/* 530 */       return MainWindowDelayStub.this.dispose(for_restart, close_already_in_progress);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean viewURL(String url, String target, int w, int h, boolean allowResize, boolean isModal)
/*     */     {
/* 542 */       MainWindowDelayStub.this.log("viewURL");
/*     */       
/* 544 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean viewURL(String url, String target, double wPct, double hPct, boolean allowResize, boolean isModal)
/*     */     {
/* 556 */       MainWindowDelayStub.this.log("viewURL");
/*     */       
/* 558 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void viewURL(String url, String target, String sourceRef)
/*     */     {
/* 567 */       MainWindowDelayStub.this.log("viewURL");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public UIFunctionsUserPrompter getUserPrompter(String title, String text, String[] buttons, int defaultOption)
/*     */     {
/* 578 */       MainWindowDelayStub.this.log("getUserPrompter");
/*     */       
/* 580 */       return null;
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
/*     */     public void promptUser(String title, String text, String[] buttons, int defaultOption, String rememberID, String rememberText, boolean bRememberByDefault, int autoCloseInMS, UserPrompterResultListener l)
/*     */     {
/* 595 */       MainWindowDelayStub.this.log("promptUser");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public UIUpdater getUIUpdater()
/*     */     {
/* 602 */       return UIUpdaterSWT.getInstance();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void openView(final int viewID, final Object datasource)
/*     */     {
/* 613 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.openView(viewID, datasource); }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public void doSearch(String searchText)
/*     */     {
/* 620 */       MainWindowDelayStub.this.log("doSearch");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void doSearch(String searchText, boolean toSubscribe)
/*     */     {
/* 628 */       MainWindowDelayStub.this.log("doSearch");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void installPlugin(String plugin_id, String resource_prefix, UIFunctions.actionListener listener)
/*     */     {
/* 637 */       MainWindowDelayStub.this.log("installPlugin");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void performAction(final int action_id, final Object args, final UIFunctions.actionListener listener)
/*     */     {
/* 649 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.performAction(action_id, args, listener); }
/*     */       });
/*     */     }
/*     */     
/*     */     public MultipleDocumentInterface getMDI()
/*     */     {
/* 655 */       MainWindowDelayStub.this.log("getMDI");
/*     */       
/* 657 */       return null;
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
/*     */     public void forceNotify(int iconID, String title, String text, String details, Object[] relatedObjects, int timeoutSecs)
/*     */     {
/* 670 */       MainWindowDelayStub.this.log("forceNotify");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Shell getMainShell()
/*     */     {
/* 677 */       return MainWindowDelayStub.this.shell;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addPluginView(String viewID, UISWTViewEventListener l)
/*     */     {
/* 686 */       MainWindowDelayStub.this.log("addPluginView");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void closeDownloadBars() {}
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isGlobalTransferBarShown()
/*     */     {
/* 698 */       if (!AzureusCoreFactory.isCoreRunning()) {
/* 699 */         return false;
/*     */       }
/*     */       
/* 702 */       return AllTransfersBar.getManager().isOpen(AzureusCoreFactory.getSingleton().getGlobalManager());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void showGlobalTransferBar()
/*     */     {
/* 709 */       AllTransfersBar.open(getMainShell());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void closeGlobalTransferBar() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public UISWTView[] getPluginViews()
/*     */     {
/* 722 */       MainWindowDelayStub.this.log("getPluginViews");
/*     */       
/* 724 */       return new UISWTView[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void openPluginView(String sParentID, String sViewID, UISWTViewEventListener l, Object dataSource, boolean bSetFocus)
/*     */     {
/* 736 */       MainWindowDelayStub.this.log("openPluginView");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void openPluginView(UISWTViewCore view, String name)
/*     */     {
/* 744 */       MainWindowDelayStub.this.log("openPluginView");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void removePluginView(String viewID)
/*     */     {
/* 752 */       MainWindowDelayStub.this.log("removePluginView");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void closePluginView(UISWTViewCore view)
/*     */     {
/* 760 */       MainWindowDelayStub.this.log("closePluginView");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void closePluginViews(String sViewID)
/*     */     {
/* 767 */       MainWindowDelayStub.this.log("closePluginViews");
/*     */     }
/*     */     
/*     */ 
/*     */     public UISWTInstance getUISWTInstance()
/*     */     {
/* 773 */       MainWindowDelayStub.this.log("getUISWTInstance");
/*     */       
/* 775 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void refreshTorrentMenu()
/*     */     {
/* 781 */       MainWindowDelayStub.this.log("refreshTorrentMenu");
/*     */     }
/*     */     
/*     */ 
/*     */     public IMainStatusBar getMainStatusBar()
/*     */     {
/* 787 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public IMainMenu createMainMenu(final Shell shell)
/*     */     {
/* 797 */       (IMainMenu)MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup4() { public Object fix(UIFunctionsSWT uif) { return uif.createMainMenu(shell); }
/*     */       });
/*     */     }
/*     */     
/*     */     public IMainWindow getMainWindow()
/*     */     {
/* 803 */       return MainWindowDelayStub.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public void closeAllDetails()
/*     */     {
/* 809 */       MainWindowDelayStub.this.log("closeAllDetails");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean hasDetailViews()
/*     */     {
/* 816 */       MainWindowDelayStub.this.log("hasDetailViews");
/*     */       
/* 818 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public Shell showCoreWaitDlg()
/*     */     {
/* 824 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean isProgramInstalled(final String extension, final String name)
/*     */     {
/* 832 */       ((Boolean)MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup4() { public Object fix(UIFunctionsSWT uif) { return Boolean.valueOf(uif.isProgramInstalled(extension, name)); }
/*     */       })).booleanValue();
/*     */     }
/*     */     
/*     */     public MultipleDocumentInterfaceSWT getMDISWT()
/*     */     {
/* 838 */       MainWindowDelayStub.this.log("getMDISWT");
/*     */       
/* 840 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void promptForSearch()
/*     */     {
/* 846 */       MainWindowDelayStub.this.log("promptForSearch");
/*     */     }
/*     */     
/*     */ 
/*     */     public UIToolBarManager getToolBarManager()
/*     */     {
/* 852 */       MainWindowDelayStub.this.log("getToolBarManager");
/*     */       
/* 854 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void openRemotePairingWindow()
/*     */     {
/* 860 */       MainWindowDelayStub.this.log("openRemotePairingWindow");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void playOrStreamDataSource(Object ds, String referal, boolean launch_already_checked, boolean complete_only)
/*     */     {
/* 870 */       MainWindowDelayStub.this.log("playOrStreamDataSource");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setHideAll(boolean hidden)
/*     */     {
/* 877 */       MainWindowDelayStub.this.log("setHideAll");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void showErrorMessage(String keyPrefix, String details, String[] textParams)
/*     */     {
/* 886 */       MainWindowDelayStub.this.log("showErrorMessage");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void showCreateTagDialog(UIFunctions.TagReturner tagReturner)
/*     */     {
/* 893 */       MainWindowDelayStub.this.log("showAddTagDialog");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean addTorrentWithOptions(final boolean force, final TorrentOpenOptions torrentOptions)
/*     */     {
/* 901 */       ((Boolean)MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup4() { public Object fix(UIFunctionsSWT uif) { return Boolean.valueOf(uif.addTorrentWithOptions(force, torrentOptions)); }
/*     */       })).booleanValue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean addTorrentWithOptions(final TorrentOpenOptions torrentOptions, final Map<String, Object> options)
/*     */     {
/* 909 */       ((Boolean)MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup4() { public Object fix(UIFunctionsSWT uif) { return Boolean.valueOf(uif.addTorrentWithOptions(torrentOptions, options)); }
/*     */       })).booleanValue();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void openTorrentOpenOptions(final Shell shell, final String sPathOfFilesToOpen, final String[] sFilesToOpen, final boolean defaultToStopped, final boolean forceOpen)
/*     */     {
/* 920 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.openTorrentOpenOptions(shell, sPathOfFilesToOpen, sFilesToOpen, defaultToStopped, forceOpen); }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void openTorrentOpenOptions(final Shell shell, final String sPathOfFilesToOpen, final String[] sFilesToOpen, final Map<String, Object> options)
/*     */     {
/* 930 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.openTorrentOpenOptions(shell, sPathOfFilesToOpen, sFilesToOpen, options); }
/*     */       });
/*     */     }
/*     */     
/*     */     public void openTorrentWindow()
/*     */     {
/* 936 */       MainWindowDelayStub.this.fixup(new MainWindowDelayStub.Fixup3() { public void fix(UIFunctionsSWT uif) { uif.openTorrentWindow(); }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public TabbedMdiInterface createTabbedMDI(Composite parent, String id)
/*     */     {
/* 943 */       MainWindowDelayStub.this.log("createTabbedMDI");
/* 944 */       return null;
/*     */     }
/*     */     
/*     */     public int adjustPXForDPI(int px) {
/* 948 */       MainWindowDelayStub.this.log("adjustPXForDPI");
/* 949 */       return px;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainWindowDelayStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */