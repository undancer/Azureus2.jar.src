/*    */ package com.aelitis.azureus.core.networkmanager;
/*    */ 
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */ public class EventWaiter
/*    */ {
/*    */   private boolean sleeping;
/*    */   private boolean wakeup_outstanding;
/*    */   
/*    */   public boolean waitForEvent(long timeout)
/*    */   {
/* 39 */     synchronized (this)
/*    */     {
/* 41 */       if (this.wakeup_outstanding)
/*    */       {
/* 43 */         this.wakeup_outstanding = false;
/*    */         
/* 45 */         return false;
/*    */       }
/*    */       try
/*    */       {
/* 49 */         this.sleeping = true;
/*    */         
/* 51 */         wait(timeout);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 55 */         Debug.printStackTrace(e);
/*    */       }
/*    */       finally
/*    */       {
/* 59 */         this.sleeping = false;
/*    */       }
/*    */       
/* 62 */       return true;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void eventOccurred()
/*    */   {
/* 69 */     synchronized (this)
/*    */     {
/* 71 */       if (!this.sleeping)
/*    */       {
/* 73 */         this.wakeup_outstanding = true;
/*    */       }
/*    */       else
/*    */       {
/* 77 */         notify();
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/EventWaiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */