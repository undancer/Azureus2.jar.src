/*    */ package com.aelitis.azureus.core.dht.transport.loopback;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*    */ import com.aelitis.azureus.core.dht.transport.util.DHTTransportStatsImpl;
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
/*    */ public class DHTTransportLoopbackStatsImpl
/*    */   extends DHTTransportStatsImpl
/*    */ {
/*    */   protected DHTTransportLoopbackStatsImpl(byte pv)
/*    */   {
/* 38 */     super(pv);
/*    */   }
/*    */   
/*    */ 
/*    */   public long getPacketsSent()
/*    */   {
/* 44 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getPacketsReceived()
/*    */   {
/* 50 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getRequestsTimedOut()
/*    */   {
/* 56 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getBytesSent()
/*    */   {
/* 62 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getBytesReceived()
/*    */   {
/* 68 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getRouteablePercentage()
/*    */   {
/* 74 */     return -1;
/*    */   }
/*    */   
/*    */ 
/*    */   public DHTTransportStats snapshot()
/*    */   {
/* 80 */     DHTTransportStatsImpl res = new DHTTransportLoopbackStatsImpl(getProtocolVersion());
/*    */     
/* 82 */     snapshotSupport(res);
/*    */     
/* 84 */     return res;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/loopback/DHTTransportLoopbackStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */