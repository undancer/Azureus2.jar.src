/*    */ package org.gudy.azureus2.ui.swt.mainwindow;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ListenerNeedingCoreRunning
/*    */   implements Listener
/*    */ {
/*    */   public final void handleEvent(final Event event)
/*    */   {
/* 35 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*    */       public void azureusCoreRunning(AzureusCore core) {
/* 37 */         ListenerNeedingCoreRunning.this.handleEvent(core, event);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public abstract void handleEvent(AzureusCore paramAzureusCore, Event paramEvent);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/ListenerNeedingCoreRunning.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */