/*    */ package org.gudy.azureus2.ui.swt.shells;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*    */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*    */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*    */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*    */ import java.io.PrintStream;
/*    */ import org.eclipse.swt.widgets.Shell;
/*    */ import org.gudy.azureus2.core3.util.AERunnable;
/*    */ import org.gudy.azureus2.core3.util.AEThread2;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CoreWaiterSWT
/*    */ {
/* 35 */   private static boolean DEBUG = false;
/*    */   private static Shell shell;
/*    */   
/* 38 */   public static enum TriggerInThread { SWT_THREAD,  ANY_THREAD,  NEW_THREAD;
/*    */     
/*    */     private TriggerInThread() {}
/*    */   }
/*    */   
/*    */   public static void waitForCoreRunning(AzureusCoreRunningListener l) {
/* 44 */     waitForCore(TriggerInThread.SWT_THREAD, l);
/*    */   }
/*    */   
/*    */   public static void waitForCore(TriggerInThread triggerInThread, final AzureusCoreRunningListener l)
/*    */   {
/* 49 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*    */       public void azureusCoreRunning(final AzureusCore core) {
/* 51 */         if (this.val$triggerInThread == CoreWaiterSWT.TriggerInThread.ANY_THREAD) {
/* 52 */           l.azureusCoreRunning(core);
/* 53 */         } else if (this.val$triggerInThread == CoreWaiterSWT.TriggerInThread.NEW_THREAD) {
/* 54 */           new AEThread2("CoreWaiterInvoke", true) {
/*    */             public void run() {
/* 56 */               CoreWaiterSWT.1.this.val$l.azureusCoreRunning(core);
/*    */             }
/*    */           }.start();
/*    */         }
/* 60 */         Utils.execSWTThread(new AERunnable()
/*    */         {
/*    */           public void runSupport() {
/* 63 */             if ((CoreWaiterSWT.shell != null) && (!CoreWaiterSWT.shell.isDisposed())) {
/* 64 */               CoreWaiterSWT.shell.dispose();
/* 65 */               CoreWaiterSWT.access$002(null);
/*    */             }
/*    */             
/* 68 */             if (CoreWaiterSWT.1.this.val$triggerInThread == CoreWaiterSWT.TriggerInThread.SWT_THREAD) {
/* 69 */               CoreWaiterSWT.1.this.val$l.azureusCoreRunning(core);
/*    */             }
/*    */           }
/*    */         });
/*    */       }
/*    */     });
/*    */     
/* 76 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 77 */       if (DEBUG) {
/* 78 */         System.out.println("NOT AVAIL FOR " + Debug.getCompressedStackTrace());
/*    */       }
/* 80 */       Utils.execSWTThread(new AERunnable()
/*    */       {
/*    */         public void runSupport() {}
/*    */       });
/*    */     }
/* 85 */     else if (DEBUG) {
/* 86 */       System.out.println("NO NEED TO WAIT.. CORE AVAIL! " + Debug.getCompressedStackTrace());
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   protected static void showWaitWindow()
/*    */   {
/* 93 */     if ((shell != null) && (!shell.isDisposed())) {
/* 94 */       shell.forceActive();
/* 95 */       return;
/*    */     }
/*    */     
/* 98 */     shell = UIFunctionsManagerSWT.getUIFunctionsSWT().showCoreWaitDlg();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/CoreWaiterSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */