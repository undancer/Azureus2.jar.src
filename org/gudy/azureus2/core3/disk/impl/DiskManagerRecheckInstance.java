/*    */ package org.gudy.azureus2.core3.disk.impl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiskManagerRecheckInstance
/*    */ {
/*    */   private final DiskManagerRecheckScheduler scheduler;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final long metric;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final int piece_length;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final boolean low_priority;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected DiskManagerRecheckInstance(DiskManagerRecheckScheduler _scheduler, long _size, int _piece_length, boolean _low_priority)
/*    */   {
/* 37 */     this.scheduler = _scheduler;
/* 38 */     this.metric = ((_low_priority ? 0L : 8070450532247928832L) + _size);
/* 39 */     this.piece_length = _piece_length;
/* 40 */     this.low_priority = _low_priority;
/*    */   }
/*    */   
/*    */ 
/*    */   protected long getMetric()
/*    */   {
/* 46 */     return this.metric;
/*    */   }
/*    */   
/*    */ 
/*    */   protected int getPieceLength()
/*    */   {
/* 52 */     return this.piece_length;
/*    */   }
/*    */   
/*    */ 
/*    */   protected boolean isLowPriority()
/*    */   {
/* 58 */     return this.low_priority;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean getPermission()
/*    */   {
/* 64 */     return this.scheduler.getPermission(this);
/*    */   }
/*    */   
/*    */ 
/*    */   public void unregister()
/*    */   {
/* 70 */     this.scheduler.unregister(this);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerRecheckInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */