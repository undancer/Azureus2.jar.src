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
/*     */ 
/*     */ public class DHTUDPPacketRequestFindNode
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   private byte[] id;
/*     */   private int node_status;
/*     */   private int estimated_dht_size;
/*     */   
/*     */   public DHTUDPPacketRequestFindNode(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  51 */     super(_transport, 1028, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestFindNode(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  63 */     super(network_handler, is, 1028, con_id, trans_id);
/*     */     
/*  65 */     this.id = DHTUDPUtils.deserialiseByteArray(is, 64);
/*     */     
/*  67 */     if (getProtocolVersion() >= 22)
/*     */     {
/*  69 */       this.node_status = is.readInt();
/*  70 */       this.estimated_dht_size = is.readInt();
/*     */     }
/*     */     
/*  73 */     super.postDeserialise(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  82 */     super.serialise(os);
/*     */     
/*  84 */     DHTUDPUtils.serialiseByteArray(os, this.id, 64);
/*     */     
/*  86 */     if (getProtocolVersion() >= 22)
/*     */     {
/*  88 */       os.writeInt(this.node_status);
/*     */       
/*  90 */       os.writeInt(this.estimated_dht_size);
/*     */     }
/*     */     
/*  93 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setID(byte[] _id)
/*     */   {
/* 100 */     this.id = _id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getID()
/*     */   {
/* 106 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNodeStatus(int ns)
/*     */   {
/* 113 */     this.node_status = ns;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getNodeStatus()
/*     */   {
/* 119 */     return this.node_status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setEstimatedDHTSize(int s)
/*     */   {
/* 126 */     this.estimated_dht_size = s;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getEstimatedDHTSize()
/*     */   {
/* 132 */     return this.estimated_dht_size;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 138 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestFindNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */