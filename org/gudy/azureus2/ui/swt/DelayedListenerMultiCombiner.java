/*    */ package org.gudy.azureus2.ui.swt;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.core3.util.AERunnable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class DelayedListenerMultiCombiner
/*    */   implements Listener
/*    */ {
/* 38 */   private Object lock = new Object();
/* 39 */   private boolean pending = false;
/*    */   
/*    */   public final void handleEvent(final Event event) {
/* 42 */     synchronized (this.lock) {
/* 43 */       if (this.pending) {
/* 44 */         return;
/*    */       }
/*    */       
/* 47 */       this.pending = true;
/*    */     }
/*    */     
/* 50 */     Utils.execSWTThreadLater(0, new AERunnable() {
/*    */       public void runSupport() {
/* 52 */         synchronized (DelayedListenerMultiCombiner.this.lock) {
/* 53 */           DelayedListenerMultiCombiner.this.pending = false;
/*    */         }
/* 55 */         DelayedListenerMultiCombiner.this.handleDelayedEvent(event);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public abstract void handleDelayedEvent(Event paramEvent);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/DelayedListenerMultiCombiner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */