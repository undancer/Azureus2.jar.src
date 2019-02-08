/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTUDPPacketData
/*     */   extends DHTUDPPacketRequest
/*     */ {
/*     */   public static final byte PT_READ_REQUEST = 0;
/*     */   public static final byte PT_READ_REPLY = 1;
/*     */   public static final byte PT_WRITE_REQUEST = 2;
/*     */   public static final byte PT_WRITE_REPLY = 3;
/*     */   private byte packet_type;
/*     */   private byte[] transfer_key;
/*     */   private byte[] key;
/*     */   private byte[] data;
/*     */   private int start_position;
/*     */   private int length;
/*     */   private int total_length;
/*     */   public static final int MAX_DATA_SIZE = 1317;
/*     */   
/*     */   public DHTUDPPacketData(DHTTransportUDPImpl _transport, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  65 */     super(_transport, 1035, _connection_id, _local_contact, _remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketData(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  77 */     super(network_handler, is, 1035, con_id, trans_id);
/*     */     
/*  79 */     this.packet_type = is.readByte();
/*  80 */     this.transfer_key = DHTUDPUtils.deserialiseByteArray(is, 64);
/*     */     
/*     */     int max_key_size;
/*     */     int max_key_size;
/*  84 */     if (getProtocolVersion() >= 24)
/*     */     {
/*  86 */       max_key_size = 255;
/*     */     }
/*     */     else
/*     */     {
/*  90 */       max_key_size = 64;
/*     */     }
/*     */     
/*  93 */     this.key = DHTUDPUtils.deserialiseByteArray(is, max_key_size);
/*  94 */     this.start_position = is.readInt();
/*  95 */     this.length = is.readInt();
/*  96 */     this.total_length = is.readInt();
/*  97 */     this.data = DHTUDPUtils.deserialiseByteArray(is, 65535);
/*     */     
/*  99 */     super.postDeserialise(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 108 */     super.serialise(os);
/*     */     
/* 110 */     os.writeByte(this.packet_type);
/* 111 */     DHTUDPUtils.serialiseByteArray(os, this.transfer_key, 64);
/*     */     
/*     */     int max_key_size;
/*     */     int max_key_size;
/* 115 */     if (getProtocolVersion() >= 24)
/*     */     {
/* 117 */       max_key_size = 255;
/*     */     }
/*     */     else
/*     */     {
/* 121 */       max_key_size = 64;
/*     */     }
/*     */     
/* 124 */     DHTUDPUtils.serialiseByteArray(os, this.key, max_key_size);
/* 125 */     os.writeInt(this.start_position);
/* 126 */     os.writeInt(this.length);
/* 127 */     os.writeInt(this.total_length);
/*     */     
/* 129 */     if (this.data.length > 0)
/*     */     {
/* 131 */       DHTUDPUtils.serialiseByteArray(os, this.data, this.start_position, this.length, 65535);
/*     */     }
/*     */     else
/*     */     {
/* 135 */       DHTUDPUtils.serialiseByteArray(os, this.data, 65535);
/*     */     }
/*     */     
/* 138 */     super.postSerialise(os);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDetails(byte _packet_type, byte[] _transfer_key, byte[] _key, byte[] _data, int _start_pos, int _length, int _total_length)
/*     */   {
/* 151 */     this.packet_type = _packet_type;
/* 152 */     this.transfer_key = _transfer_key;
/* 153 */     this.key = _key;
/* 154 */     this.data = _data;
/* 155 */     this.start_position = _start_pos;
/* 156 */     this.length = _length;
/* 157 */     this.total_length = _total_length;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getPacketType()
/*     */   {
/* 163 */     return this.packet_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getTransferKey()
/*     */   {
/* 169 */     return this.transfer_key;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getRequestKey()
/*     */   {
/* 175 */     return this.key;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getData()
/*     */   {
/* 181 */     return this.data;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStartPosition()
/*     */   {
/* 187 */     return this.start_position;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 193 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTotalLength()
/*     */   {
/* 199 */     return this.total_length;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 205 */     return super.getString() + "tk=" + DHTLog.getString2(this.transfer_key) + ",rk=" + DHTLog.getString2(this.key) + ",data=" + this.data.length + ",st=" + this.start_position + ",len=" + this.length + ",tot=" + this.total_length;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */