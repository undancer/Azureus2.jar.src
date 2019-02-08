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
/*    */ public abstract class ListenerGetOffSWT
/*    */   implements Listener
/*    */ {
/*    */   public void handleEvent(final Event event)
/*    */   {
/* 39 */     Utils.getOffOfSWTThread(new AERunnable() {
/*    */       public void runSupport() {
/* 41 */         ListenerGetOffSWT.this.handleEventOffSWT(event);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   abstract void handleEventOffSWT(Event paramEvent);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ListenerGetOffSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */