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
/*     */ public class DHTUDPPacketRequestFindValue
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   private byte[] id;
/*     */   private byte flags;
/*     */   private byte maximum_values;
/*     */   
/*     */   public DHTUDPPacketRequestFindValue(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  49 */     super(_transport, 1030, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestFindValue(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  61 */     super(network_handler, is, 1030, con_id, trans_id);
/*     */     
/*  63 */     this.id = DHTUDPUtils.deserialiseByteArray(is, 64);
/*     */     
/*  65 */     this.flags = is.readByte();
/*     */     
/*  67 */     this.maximum_values = is.readByte();
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
/*  80 */     DHTUDPUtils.serialiseByteArray(os, this.id, 64);
/*     */     
/*  82 */     os.writeByte(this.flags);
/*     */     
/*  84 */     os.writeByte(this.maximum_values);
/*     */     
/*  86 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setID(byte[] _id)
/*     */   {
/*  93 */     this.id = _id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getID()
/*     */   {
/*  99 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getFlags()
/*     */   {
/* 105 */     return this.flags;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setFlags(byte _flags)
/*     */   {
/* 112 */     this.flags = _flags;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setMaximumValues(int max)
/*     */   {
/* 119 */     if (max > 255)
/*     */     {
/* 121 */       max = 255;
/*     */     }
/*     */     
/* 124 */     this.maximum_values = ((byte)max);
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getMaximumValues()
/*     */   {
/* 130 */     return this.maximum_values & 0xFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 136 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestFindValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */