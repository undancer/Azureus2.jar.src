/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketHandlerStats;
/*     */ import com.aelitis.azureus.core.dht.transport.util.DHTTransportStatsImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTTransportUDPStatsImpl
/*     */   extends DHTTransportStatsImpl
/*     */ {
/*     */   private final DHTTransportUDPImpl transport;
/*     */   private DHTUDPPacketHandlerStats stats;
/*     */   
/*     */   protected DHTTransportUDPStatsImpl(DHTTransportUDPImpl _transport, byte _pv, DHTUDPPacketHandlerStats _stats)
/*     */   {
/*  45 */     super(_pv);
/*     */     
/*  47 */     this.transport = _transport;
/*  48 */     this.stats = _stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setStats(DHTUDPPacketHandlerStats _stats)
/*     */   {
/*  55 */     this.stats = _stats;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPacketsSent()
/*     */   {
/*  61 */     return this.stats.getPacketsSent();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPacketsReceived()
/*     */   {
/*  67 */     return this.stats.getPacketsReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRequestsTimedOut()
/*     */   {
/*  73 */     return this.stats.getRequestsTimedOut();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesSent()
/*     */   {
/*  79 */     return this.stats.getBytesSent();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReceived()
/*     */   {
/*  85 */     return this.stats.getBytesReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRouteablePercentage()
/*     */   {
/*  91 */     return this.transport.getRouteablePercentage();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportStats snapshot()
/*     */   {
/*  97 */     DHTTransportStatsImpl res = new DHTTransportUDPStatsImpl(this.transport, getProtocolVersion(), this.stats.snapshot());
/*     */     
/*  99 */     snapshotSupport(res);
/*     */     
/* 101 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 107 */     return super.getString() + "," + "packsent:" + getPacketsSent() + "," + "packrecv:" + getPacketsReceived() + "," + "bytesent:" + getBytesSent() + "," + "byterecv:" + getBytesReceived() + "," + "timeout:" + getRequestsTimedOut() + "," + "sendq:" + this.stats.getSendQueueLength() + "," + "recvq:" + this.stats.getReceiveQueueLength();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTTransportUDPStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */