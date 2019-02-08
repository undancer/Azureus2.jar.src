/*    */ package com.aelitis.azureus.core.peermanager.control;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.control.impl.PeerControlSchedulerImpl;
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
/*    */ public class PeerControlSchedulerFactory
/*    */ {
/*    */   public static PeerControlScheduler getSingleton(int id)
/*    */   {
/* 27 */     return PeerControlSchedulerImpl.getSingleton(id);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void overrideWeightedPriorities(boolean b)
/*    */   {
/* 34 */     PeerControlSchedulerImpl.overrideAllWeightedPriorities(b);
/*    */   }
/*    */   
/*    */   public static void updateScheduleOrdering() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/PeerControlSchedulerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */