/*    */ package org.gudy.azureus2.core3.util;
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
/*    */ public class DelayedEvent
/*    */ {
/*    */   private final TimerEvent event;
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
/*    */   public DelayedEvent(String name, long delay_millis, final AERunnable target)
/*    */   {
/* 37 */     this.event = SimpleTimer.addEvent(name, SystemTime.getCurrentTime() + delay_millis, new TimerEventPerformer()
/*    */     {
/*    */ 
/*    */ 
/*    */       public void perform(TimerEvent event)
/*    */       {
/*    */ 
/*    */         try
/*    */         {
/*    */ 
/* 47 */           target.run();
/*    */         }
/*    */         finally {}
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */   public void cancel()
/*    */   {
/* 57 */     this.event.cancel();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/DelayedEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */