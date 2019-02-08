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
/*     */ public class DHTUDPPacketRequestPing
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*  39 */   private static final int[] EMPTY_INTS = new int[0];
/*     */   
/*  41 */   private int[] alt_networks = EMPTY_INTS;
/*  42 */   private int[] alt_network_counts = EMPTY_INTS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTUDPPacketRequestPing(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  51 */     super(_transport, 1024, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestPing(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  63 */     super(network_handler, is, 1024, con_id, trans_id);
/*     */     
/*  65 */     if (getProtocolVersion() >= 52)
/*     */     {
/*  67 */       DHTUDPUtils.deserialiseAltContactRequest(this, is);
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
/*  81 */     if (getProtocolVersion() >= 52)
/*     */     {
/*  83 */       DHTUDPUtils.serialiseAltContactRequest(this, os);
/*     */     }
/*     */     
/*  86 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setAltContactRequest(int[] networks, int[] counts)
/*     */   {
/*  94 */     this.alt_networks = networks;
/*  95 */     this.alt_network_counts = counts;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int[] getAltNetworks()
/*     */   {
/* 101 */     return this.alt_networks;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int[] getAltNetworkCounts()
/*     */   {
/* 107 */     return this.alt_network_counts;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 113 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestPing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */