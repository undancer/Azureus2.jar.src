/*    */ package com.aelitis.azureus.core.peermanager.control;
/*    */ 
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public abstract interface PeerControlScheduler
/*    */ {
/* 23 */   public static final int SCHEDULE_PERIOD_MILLIS = COConfigurationManager.getIntParameter("peermanager.schedule.time");
/* 24 */   public static final int SCHEDULE_PERIOD_MAX_CATCHUP = SCHEDULE_PERIOD_MILLIS >> 2;
/*    */   
/*    */   public abstract void register(PeerControlInstance paramPeerControlInstance);
/*    */   
/*    */   public abstract void unregister(PeerControlInstance paramPeerControlInstance);
/*    */   
/*    */   public abstract void updateScheduleOrdering();
/*    */   
/*    */   public abstract SpeedTokenDispenser getSpeedTokenDispenser();
/*    */   
/*    */   public abstract void overrideWeightedPriorities(boolean paramBoolean);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/control/PeerControlScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */