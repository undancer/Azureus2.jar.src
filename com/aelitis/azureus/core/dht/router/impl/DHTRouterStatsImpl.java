/*    */ package com.aelitis.azureus.core.dht.router.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.router.DHTRouterStats;
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
/*    */ public class DHTRouterStatsImpl
/*    */   implements DHTRouterStats
/*    */ {
/*    */   private final DHTRouterImpl router;
/*    */   
/*    */   protected DHTRouterStatsImpl(DHTRouterImpl _router)
/*    */   {
/* 39 */     this.router = _router;
/*    */   }
/*    */   
/*    */ 
/*    */   public long[] getStats()
/*    */   {
/* 45 */     return this.router.getStatsSupport();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/impl/DHTRouterStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */