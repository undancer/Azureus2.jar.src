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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTUDPPacketRequestKeyBlock
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   private int random_id;
/*     */   private byte[] key_block_request;
/*     */   private byte[] key_block_signature;
/*     */   
/*     */   public DHTUDPPacketRequestKeyBlock(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  50 */     super(_transport, 1036, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestKeyBlock(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  62 */     super(network_handler, is, 1036, con_id, trans_id);
/*     */     
/*  64 */     this.random_id = is.readInt();
/*     */     
/*  66 */     this.key_block_request = DHTUDPUtils.deserialiseByteArray(is, 255);
/*  67 */     this.key_block_signature = DHTUDPUtils.deserialiseByteArray(is, 65535);
/*     */     
/*  69 */     super.postDeserialise(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  78 */     super.serialise(os);
/*     */     
/*  80 */     os.writeInt(this.random_id);
/*     */     
/*  82 */     DHTUDPUtils.serialiseByteArray(os, this.key_block_request, 255);
/*  83 */     DHTUDPUtils.serialiseByteArray(os, this.key_block_signature, 65535);
/*     */     
/*  85 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setKeyBlockDetails(byte[] req, byte[] sig)
/*     */   {
/*  93 */     this.key_block_request = req;
/*  94 */     this.key_block_signature = sig;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setRandomID(int _random_id)
/*     */   {
/* 101 */     this.random_id = _random_id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getRandomID()
/*     */   {
/* 107 */     return this.random_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getKeyBlockRequest()
/*     */   {
/* 113 */     return this.key_block_request;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getKeyBlockSignature()
/*     */   {
/* 119 */     return this.key_block_signature;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 125 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestKeyBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */