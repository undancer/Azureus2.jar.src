/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
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
/*     */ public class DHTUDPPacketReplyStats
/*     */   extends DHTUDPPacketReply
/*     */ {
/*  42 */   private int stats_type = 1;
/*     */   
/*     */ 
/*     */   private DHTTransportFullStats original_stats;
/*     */   
/*     */ 
/*     */   private byte[] new_stats;
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTUDPPacketReplyStats(DHTTransportUDPImpl transport, DHTUDPPacketRequestStats request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*     */   {
/*  54 */     super(transport, 1033, request, local_contact, remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReplyStats(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  66 */     super(network_handler, originator, is, 1033, trans_id);
/*     */     
/*  68 */     if (getProtocolVersion() >= 15)
/*     */     {
/*  70 */       this.stats_type = is.readInt();
/*     */       
/*  72 */       if (this.stats_type == 1)
/*     */       {
/*  74 */         this.original_stats = DHTUDPUtils.deserialiseStats(getProtocolVersion(), is);
/*     */       }
/*     */       else
/*     */       {
/*  78 */         this.new_stats = DHTUDPUtils.deserialiseByteArray(is, 65535);
/*     */       }
/*     */     }
/*     */     else {
/*  82 */       this.original_stats = DHTUDPUtils.deserialiseStats(getProtocolVersion(), is);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatsType()
/*     */   {
/*  89 */     return this.stats_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportFullStats getOriginalStats()
/*     */   {
/*  95 */     return this.original_stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOriginalStats(DHTTransportFullStats _stats)
/*     */   {
/* 102 */     this.stats_type = 1;
/* 103 */     this.original_stats = _stats;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getNewStats()
/*     */   {
/* 109 */     return this.new_stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNewStats(byte[] _stats, int _stats_type)
/*     */   {
/* 117 */     this.stats_type = _stats_type;
/* 118 */     this.new_stats = _stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 127 */     super.serialise(os);
/*     */     
/* 129 */     if (getProtocolVersion() >= 15)
/*     */     {
/* 131 */       os.writeInt(this.stats_type);
/*     */       
/* 133 */       if (this.stats_type == 1)
/*     */       {
/* 135 */         DHTUDPUtils.serialiseStats(getProtocolVersion(), os, this.original_stats);
/*     */       }
/*     */       else
/*     */       {
/* 139 */         DHTUDPUtils.serialiseByteArray(os, this.new_stats, 65535);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 144 */       DHTUDPUtils.serialiseStats(getProtocolVersion(), os, this.original_stats);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */