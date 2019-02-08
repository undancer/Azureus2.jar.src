/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.IUIIntializer;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.ui.swt.UISwitcherListener;
/*     */ import org.gudy.azureus2.ui.swt.UISwitcherUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ public class SWTThread
/*     */ {
/*     */   private static SWTThread instance;
/*     */   Display display;
/*     */   private boolean sleak;
/*     */   private boolean terminated;
/*     */   private Thread runner;
/*     */   private final IUIIntializer initializer;
/*     */   private Monitor primaryMonitor;
/*     */   protected boolean displayDispoed;
/*     */   private boolean isRetinaDisplay;
/*     */   
/*     */   public static SWTThread getInstance()
/*     */   {
/*  51 */     return instance;
/*     */   }
/*     */   
/*     */   public static void createInstance(IUIIntializer initializer) throws SWTThreadAlreadyInstanciatedException {
/*  55 */     if (instance != null) {
/*  56 */       throw new SWTThreadAlreadyInstanciatedException();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  61 */     new SWTThread(initializer);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private SWTThread(final IUIIntializer app)
/*     */   {
/*  81 */     this.initializer = app;
/*  82 */     instance = this;
/*  83 */     Display.setAppName(Constants.APP_NAME);
/*     */     try
/*     */     {
/*  86 */       this.display = Display.getCurrent();
/*  87 */       if (this.display == null) {
/*  88 */         this.display = new Display();
/*  89 */         this.sleak = false;
/*     */       } else {
/*  91 */         this.sleak = true;
/*     */       }
/*     */     } catch (Exception e) {
/*  94 */       this.display = new Display();
/*  95 */       this.sleak = false;
/*     */     } catch (UnsatisfiedLinkError ue) {
/*  97 */       String sMin = "3.4";
/*     */       try {
/*  99 */         sMin = "" + SWT.getVersion() / 100 / 10.0D;
/*     */       }
/*     */       catch (Throwable t) {}
/*     */       try {
/* 103 */         String tempDir = System.getProperty("swt.library.path");
/* 104 */         if (tempDir == null) {
/* 105 */           tempDir = System.getProperty("java.io.tmpdir");
/*     */         }
/* 107 */         Debug.out("Loading SWT Libraries failed. Typical causes:\n\n(1) swt.jar is not for your os architecture (" + System.getProperty("os.arch") + ").  " + "You can get a new swt.jar (Min Version: " + sMin + ") " + "from http://eclipse.org/swt" + "\n\n" + "(2) No write access to '" + tempDir + "'. SWT will extract libraries contained in the swt.jar to this dir.\n", ue);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */         app.stopIt(false, false);
/* 118 */         this.terminated = true;
/*     */         
/* 120 */         PlatformManagerFactory.getPlatformManager().dispose();
/*     */       }
/*     */       catch (Throwable t) {}
/* 123 */       return;
/*     */     }
/* 125 */     Thread.currentThread().setName("SWT Thread");
/*     */     
/* 127 */     Utils.initialize(this.display);
/*     */     
/* 129 */     this.primaryMonitor = this.display.getPrimaryMonitor();
/* 130 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator() {
/*     */       public void generate(IndentWriter writer) {
/* 132 */         writer.println("SWT");
/*     */         try
/*     */         {
/* 135 */           writer.indent();
/*     */           
/* 137 */           writer.println("SWT Version:" + SWT.getVersion() + "/" + SWT.getPlatform());
/*     */           
/*     */ 
/* 140 */           writer.println("org.eclipse.swt.browser.XULRunnerPath: " + System.getProperty("org.eclipse.swt.browser.XULRunnerPath", ""));
/*     */           
/* 142 */           writer.println("MOZILLA_FIVE_HOME: " + org.gudy.azureus2.core3.util.SystemProperties.getEnvironmentalVariable("MOZILLA_FIVE_HOME"));
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 147 */           writer.exdent();
/*     */         }
/*     */         
/*     */       }
/* 151 */     });
/* 152 */     UISwitcherUtil.addListener(new UISwitcherListener() {
/*     */       public void uiSwitched(String ui) {
/* 154 */         MessageBoxShell mb = new MessageBoxShell(MessageText.getString("dialog.uiswitcher.restart.title"), MessageText.getString("dialog.uiswitcher.restart.text"), new String[] { MessageText.getString("UpdateWindow.restart"), MessageText.getString("UpdateWindow.restartLater") }, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 161 */         mb.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int result) {
/* 163 */             if (result != 0) {
/* 164 */               return;
/*     */             }
/* 166 */             UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 167 */             if (uif != null) {
/* 168 */               uif.dispose(true, false);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */     
/*     */     try
/*     */     {
/* 177 */       Field fldOpenDoc = SWT.class.getDeclaredField("OpenDocument");
/* 178 */       int SWT_OpenDocument = fldOpenDoc.getInt(null);
/*     */       
/* 180 */       this.display.addListener(SWT_OpenDocument, new Listener() {
/*     */         public void handleEvent(final Event event) {
/* 182 */           com.aelitis.azureus.core.AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */             public void azureusCoreRunning(AzureusCore core) {
/* 184 */               UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentOpenOptions(Utils.findAnyShell(), null, new String[] { event.text }, false, false);
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable t) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 196 */     Listener lShowMainWindow = new Listener() {
/*     */       public void handleEvent(Event event) {
/* 198 */         if (event.type == 26)
/*     */         {
/* 200 */           if (AERunStateHandler.isDelayedUI())
/*     */           {
/* 202 */             Debug.out("Ignoring activate event as delay start");
/*     */             
/* 204 */             return;
/*     */           }
/*     */         }
/* 207 */         Shell as = Display.getDefault().getActiveShell();
/* 208 */         if (as != null) {
/* 209 */           as.setVisible(true);
/* 210 */           as.forceActive();
/* 211 */           return;
/*     */         }
/* 213 */         UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 214 */         if (uif != null) {
/* 215 */           Shell mainShell = uif.getMainShell();
/* 216 */           if (((mainShell == null) || (!mainShell.isVisible()) || (mainShell.getMinimized())) && 
/* 217 */             (!org.gudy.azureus2.core3.config.COConfigurationManager.getBooleanParameter("Reduce Auto Activate Window")))
/*     */           {
/* 219 */             uif.bringToFront(false);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 224 */     };
/* 225 */     this.display.addListener(26, lShowMainWindow);
/* 226 */     this.display.addListener(13, lShowMainWindow);
/*     */     
/* 228 */     this.display.addListener(12, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 230 */         SWTThread.this.displayDispoed = true;
/*     */       }
/*     */     });
/*     */     
/* 234 */     if (Constants.isOSX)
/*     */     {
/*     */ 
/*     */ 
/* 238 */       this.display.addListener(21, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 240 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 241 */           if (uiFunctions != null) {
/* 242 */             event.doit = uiFunctions.dispose(false, false);
/*     */           }
/*     */           
/*     */         }
/* 246 */       });
/* 247 */       String platform = SWT.getPlatform();
/*     */       
/*     */ 
/* 250 */       if (platform.equals("carbon")) {
/*     */         try
/*     */         {
/* 253 */           Class<?> ehancerClass = Class.forName("org.gudy.azureus2.ui.swt.osx.CarbonUIEnhancer");
/*     */           
/* 255 */           Constructor<?> constructor = ehancerClass.getConstructor(new Class[0]);
/*     */           
/* 257 */           constructor.newInstance(new Object[0]);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 261 */           Debug.printStackTrace(e);
/*     */         }
/* 263 */       } else if (platform.equals("cocoa")) {
/*     */         try
/*     */         {
/* 266 */           Class<?> ehancerClass = Class.forName("org.gudy.azureus2.ui.swt.osx.CocoaUIEnhancer");
/*     */           
/* 268 */           Method mGetInstance = ehancerClass.getMethod("getInstance", new Class[0]);
/* 269 */           Object claObj = mGetInstance.invoke(null, new Object[0]);
/*     */           
/* 271 */           Method mHookAppMenu = claObj.getClass().getMethod("hookApplicationMenu", new Class[0]);
/* 272 */           if (mHookAppMenu != null) {
/* 273 */             mHookAppMenu.invoke(claObj, new Object[0]);
/*     */           }
/*     */           
/* 276 */           Method mHookDocOpen = claObj.getClass().getMethod("hookDocumentOpen", new Class[0]);
/* 277 */           if (mHookDocOpen != null) {
/* 278 */             mHookDocOpen.invoke(claObj, new Object[0]);
/*     */           }
/*     */           
/* 281 */           Method mIsRetinaDisplay = claObj.getClass().getMethod("isRetinaDisplay", new Class[0]);
/* 282 */           if (mIsRetinaDisplay != null) {
/* 283 */             this.isRetinaDisplay = ((Boolean)mIsRetinaDisplay.invoke(claObj, new Object[0])).booleanValue();
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 289 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 294 */     if (app != null) {
/* 295 */       app.runInSWTThread();
/* 296 */       this.runner = new Thread(new org.gudy.azureus2.core3.util.AERunnable()
/*     */       {
/* 298 */         public void runSupport() { app.run(); } }, "Main Thread");
/*     */       
/*     */ 
/* 301 */       this.runner.start();
/*     */     }
/*     */     
/*     */ 
/* 305 */     if (!this.sleak) {
/* 306 */       while ((!this.display.isDisposed()) && (!this.terminated)) {
/*     */         try {
/* 308 */           if (!this.display.readAndDispatch()) {
/* 309 */             this.display.sleep();
/*     */           }
/*     */         } catch (Throwable e) {
/* 312 */           if (this.terminated) {
/* 313 */             Logger.log(new LogEvent(LogIDs.GUI, "Weird non-critical error after terminated in readAndDispatch: " + e.toString()));
/*     */           }
/*     */           else
/*     */           {
/* 317 */             String stackTrace = Debug.getStackTrace(e);
/* 318 */             if ((Constants.isOSX) && (stackTrace.indexOf("Device.dispose") > 0) && (stackTrace.indexOf("DropTarget") > 0))
/*     */             {
/*     */ 
/* 321 */               Logger.log(new LogEvent(LogIDs.GUI, "Weird non-critical display disposal in readAndDispatch"));
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 326 */               if (SWT.getVersion() < 3119)
/* 327 */                 e.printStackTrace();
/* 328 */               if (Constants.isCVSVersion()) {
/* 329 */                 Logger.log(new org.gudy.azureus2.core3.logging.LogAlert(false, MessageText.getString("SWT.alert.erroringuithread"), e));
/*     */               } else {
/* 331 */                 Debug.out(MessageText.getString("SWT.alert.erroringuithread"), e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 340 */       if (!this.terminated)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 345 */         if (app != null) {
/* 346 */           app.stopIt(false, false);
/*     */         }
/* 348 */         this.terminated = true;
/*     */       }
/*     */       try
/*     */       {
/* 352 */         if (!this.display.isDisposed()) {
/* 353 */           this.display.dispose();
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 357 */         if (SWT.getVersion() < 3119) {
/* 358 */           t.printStackTrace();
/*     */         } else {
/* 360 */           Debug.printStackTrace(t);
/*     */         }
/*     */       }
/*     */       
/* 364 */       PlatformManagerFactory.getPlatformManager().dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void terminate()
/*     */   {
/* 371 */     this.terminated = true;
/*     */     
/*     */ 
/* 374 */     if (!this.display.isDisposed()) {
/*     */       try {
/* 376 */         Shell[] shells = this.display.getShells();
/* 377 */         for (int i = 0; i < shells.length; i++) {
/*     */           try {
/* 379 */             Shell shell = shells[i];
/* 380 */             shell.dispose();
/*     */           } catch (Throwable t) {
/* 382 */             Debug.out(t);
/*     */           }
/*     */         }
/*     */       } catch (Throwable t) {
/* 386 */         Debug.out(t);
/*     */       }
/* 388 */       this.display.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public Display getDisplay() {
/* 393 */     return this.display;
/*     */   }
/*     */   
/*     */   public boolean isTerminated()
/*     */   {
/* 398 */     return (this.terminated) || (this.displayDispoed) || (this.display.isDisposed());
/*     */   }
/*     */   
/*     */   public IUIIntializer getInitializer() {
/* 402 */     return this.initializer;
/*     */   }
/*     */   
/*     */   public Monitor getPrimaryMonitor() {
/* 406 */     return this.primaryMonitor;
/*     */   }
/*     */   
/*     */   public boolean isRetinaDisplay() {
/* 410 */     return this.isRetinaDisplay;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/SWTThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */