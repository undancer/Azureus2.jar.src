/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ public class UIExitUtilsSWT
/*     */ {
/*  40 */   private static boolean skipCloseCheck = false;
/*     */   
/*  42 */   private static CopyOnWriteList<canCloseListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(canCloseListener l)
/*     */   {
/*  48 */     listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(canCloseListener l)
/*     */   {
/*  55 */     listeners.remove(l);
/*     */   }
/*     */   
/*     */   public static void setSkipCloseCheck(boolean b) {
/*  59 */     skipCloseCheck = b;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean canClose(GlobalManager globalManager, boolean bForRestart)
/*     */   {
/*  67 */     if (skipCloseCheck) {
/*  68 */       return true;
/*     */     }
/*     */     
/*  71 */     Shell mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/*  72 */     if ((mainShell != null) && ((!mainShell.isVisible()) || (mainShell.getMinimized())) && (COConfigurationManager.getBooleanParameter("Password enabled")))
/*     */     {
/*     */ 
/*     */ 
/*  76 */       if (!PasswordWindow.showPasswordWindow(Display.getCurrent())) {
/*  77 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  82 */     if ((COConfigurationManager.getBooleanParameter("confirmationOnExit")) && 
/*  83 */       (!getExitConfirmation(bForRestart))) {
/*  84 */       return false;
/*     */     }
/*     */     
/*     */ 
/*  88 */     for (canCloseListener listener : listeners)
/*     */     {
/*  90 */       if (!listener.canClose())
/*     */       {
/*  92 */         return false;
/*     */       }
/*     */     }
/*     */     
/*  96 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean getExitConfirmation(boolean for_restart)
/*     */   {
/* 105 */     MessageBoxShell mb = new MessageBoxShell(200, for_restart ? "MainWindow.dialog.restartconfirmation" : "MainWindow.dialog.exitconfirmation", (String[])null);
/*     */     
/*     */ 
/* 108 */     mb.open(null);
/*     */     
/* 110 */     return mb.waitUntilClosed() == 64;
/*     */   }
/*     */   
/*     */   public static void uiShutdown()
/*     */   {
/* 115 */     if (SystemProperties.isJavaWebStartInstance())
/*     */     {
/* 117 */       Thread close = new AEThread("JWS Force Terminate") {
/*     */         public void runSupport() {
/*     */           try {
/* 120 */             Thread.sleep(2500L);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 124 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/* 127 */           SESecurityManager.exitVM(1);
/*     */         }
/*     */         
/* 130 */       };
/* 131 */       close.setDaemon(true);
/*     */       
/* 133 */       close.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface canCloseListener
/*     */   {
/*     */     public abstract boolean canClose();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/UIExitUtilsSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */