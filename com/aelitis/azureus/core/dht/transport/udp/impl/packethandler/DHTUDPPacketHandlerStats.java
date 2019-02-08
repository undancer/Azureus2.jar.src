/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerStats;
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
/*     */ public class DHTUDPPacketHandlerStats
/*     */ {
/*     */   private long packets_sent;
/*     */   private long packets_received;
/*     */   private long bytes_sent;
/*     */   private long bytes_received;
/*     */   private long timeouts;
/*     */   private final PRUDPPacketHandlerStats stats;
/*     */   
/*     */   protected DHTUDPPacketHandlerStats(PRUDPPacketHandler _handler)
/*     */   {
/*  41 */     this.stats = _handler.getStats();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketHandlerStats(DHTUDPPacketHandlerStats _originator, PRUDPPacketHandlerStats _stats)
/*     */   {
/*  49 */     this.packets_sent = _originator.packets_sent;
/*  50 */     this.packets_received = _originator.packets_received;
/*  51 */     this.bytes_sent = _originator.bytes_sent;
/*  52 */     this.bytes_received = _originator.bytes_received;
/*  53 */     this.timeouts = _originator.timeouts;
/*     */     
/*  55 */     this.stats = _stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void timeout()
/*     */   {
/*  63 */     this.timeouts += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void packetSent(long bytes)
/*     */   {
/*  70 */     this.packets_sent += 1L;
/*  71 */     this.bytes_sent += bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void packetReceived(long bytes)
/*     */   {
/*  78 */     this.packets_received += 1L;
/*  79 */     this.bytes_received += bytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getPacketsSent()
/*     */   {
/*  86 */     return this.packets_sent;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPacketsReceived()
/*     */   {
/*  92 */     return this.packets_received;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRequestsTimedOut()
/*     */   {
/*  98 */     return this.timeouts;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesSent()
/*     */   {
/* 104 */     return this.bytes_sent;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReceived()
/*     */   {
/* 110 */     return this.bytes_received;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSendQueueLength()
/*     */   {
/* 116 */     return this.stats.getSendQueueLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getReceiveQueueLength()
/*     */   {
/* 122 */     return this.stats.getReceiveQueueLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTUDPPacketHandlerStats snapshot()
/*     */   {
/* 128 */     return new DHTUDPPacketHandlerStats(this, this.stats.snapshot());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketHandlerStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */