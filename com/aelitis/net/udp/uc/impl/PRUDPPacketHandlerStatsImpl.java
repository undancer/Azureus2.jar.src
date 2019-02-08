/*     */ package com.aelitis.net.udp.uc.impl;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerStats;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class PRUDPPacketHandlerStatsImpl
/*     */   implements PRUDPPacketHandlerStats, Cloneable
/*     */ {
/*     */   private PRUDPPacketHandlerImpl packet_handler;
/*     */   private long packets_sent;
/*     */   private long packets_received;
/*     */   private long requests_timeout;
/*     */   private long bytes_sent;
/*     */   private long bytes_received;
/*     */   
/*     */   protected PRUDPPacketHandlerStatsImpl(PRUDPPacketHandlerImpl _packet_handler)
/*     */   {
/*  47 */     this.packet_handler = _packet_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPacketsSent()
/*     */   {
/*  53 */     return this.packets_sent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void packetSent(int len)
/*     */   {
/*  60 */     this.packets_sent += 1L;
/*  61 */     this.bytes_sent += len;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPacketsReceived()
/*     */   {
/*  67 */     return this.packets_received;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void packetReceived(int len)
/*     */   {
/*  74 */     this.packets_received += 1L;
/*  75 */     this.bytes_received += len;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void primordialPacketSent(int len) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void primordialPacketReceived(int len) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getRequestsTimedOut()
/*     */   {
/*  93 */     return this.requests_timeout;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void requestTimedOut()
/*     */   {
/*  99 */     this.requests_timeout += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesSent()
/*     */   {
/* 105 */     return this.bytes_sent;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReceived()
/*     */   {
/* 111 */     return this.bytes_received;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSendQueueLength()
/*     */   {
/* 117 */     return this.packet_handler.getSendQueueLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getReceiveQueueLength()
/*     */   {
/* 123 */     return this.packet_handler.getReceiveQueueLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public PRUDPPacketHandlerStats snapshot()
/*     */   {
/*     */     try
/*     */     {
/* 131 */       return (PRUDPPacketHandlerStats)clone();
/*     */     }
/*     */     catch (CloneNotSupportedException e)
/*     */     {
/* 135 */       Debug.printStackTrace(e);
/*     */     }
/* 137 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */