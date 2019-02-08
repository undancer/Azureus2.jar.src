/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
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
/*     */ public class DHTUDPPacketRequestStats
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   public static final int STATS_TYPE_ORIGINAL = 1;
/*     */   public static final int STATS_TYPE_NP_VER2 = 2;
/*  42 */   private int stats_type = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTUDPPacketRequestStats(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  51 */     super(_transport, 1034, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestStats(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  63 */     super(network_handler, is, 1034, con_id, trans_id);
/*     */     
/*  65 */     if (getProtocolVersion() >= 15)
/*     */     {
/*  67 */       this.stats_type = is.readInt();
/*     */     }
/*     */     
/*  70 */     super.postDeserialise(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  79 */     super.serialise(os);
/*     */     
/*  81 */     if (getProtocolVersion() >= 15)
/*     */     {
/*  83 */       os.writeInt(this.stats_type);
/*     */     }
/*     */     
/*  86 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setStatsType(int _type)
/*     */   {
/*  93 */     this.stats_type = _type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatsType()
/*     */   {
/*  99 */     return this.stats_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 105 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */