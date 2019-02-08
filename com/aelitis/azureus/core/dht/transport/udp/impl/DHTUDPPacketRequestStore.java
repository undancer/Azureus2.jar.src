/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
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
/*     */ public class DHTUDPPacketRequestStore
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   public static final int MAX_KEYS_PER_PACKET = 255;
/*     */   public static final int MAX_VALUES_PER_KEY = 255;
/*     */   private int random_id;
/*     */   private byte[][] keys;
/*     */   private DHTTransportValue[][] value_sets;
/*     */   
/*     */   public DHTUDPPacketRequestStore(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  55 */     super(_transport, 1026, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequestStore(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  67 */     super(network_handler, is, 1026, con_id, trans_id);
/*     */     
/*  69 */     if (getProtocolVersion() >= 7)
/*     */     {
/*  71 */       this.random_id = is.readInt();
/*     */     }
/*     */     
/*  74 */     this.keys = DHTUDPUtils.deserialiseByteArrayArray(is, 255);
/*     */     
/*     */ 
/*     */ 
/*  78 */     this.value_sets = DHTUDPUtils.deserialiseTransportValuesArray(this, is, getClockSkew(), 255);
/*     */     
/*  80 */     super.postDeserialise(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  89 */     super.serialise(os);
/*     */     
/*  91 */     if (getProtocolVersion() >= 7)
/*     */     {
/*  93 */       os.writeInt(this.random_id);
/*     */     }
/*     */     
/*  96 */     DHTUDPUtils.serialiseByteArrayArray(os, this.keys, 255);
/*     */     try
/*     */     {
/*  99 */       DHTUDPUtils.serialiseTransportValuesArray(this, os, this.value_sets, 0L, 255);
/*     */     }
/*     */     catch (DHTTransportException e)
/*     */     {
/* 103 */       throw new IOException(e.getMessage());
/*     */     }
/*     */     
/* 106 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setRandomID(int _random_id)
/*     */   {
/* 113 */     this.random_id = _random_id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getRandomID()
/*     */   {
/* 119 */     return this.random_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setValueSets(DHTTransportValue[][] _values)
/*     */   {
/* 126 */     this.value_sets = _values;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportValue[][] getValueSets()
/*     */   {
/* 132 */     return this.value_sets;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setKeys(byte[][] _key)
/*     */   {
/* 139 */     this.keys = _key;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[][] getKeys()
/*     */   {
/* 145 */     return this.keys;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 151 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequestStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */