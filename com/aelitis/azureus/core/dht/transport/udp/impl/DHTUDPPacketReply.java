/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class DHTUDPPacketReply
/*     */   extends PRUDPPacketReply
/*     */   implements DHTUDPPacket
/*     */ {
/*     */   public static final int DHT_HEADER_SIZE = 26;
/*     */   private DHTTransportUDPImpl transport;
/*     */   private long connection_id;
/*     */   private byte protocol_version;
/*  60 */   private byte vendor_id = -1;
/*     */   
/*     */ 
/*     */   private int network;
/*     */   
/*     */   private int target_instance_id;
/*     */   
/*     */   private byte flags;
/*     */   
/*     */   private byte flags2;
/*     */   
/*     */   private long skew;
/*     */   
/*     */   private DHTNetworkPosition[] network_positions;
/*     */   
/*     */   private short processing_time;
/*     */   
/*     */   private long request_receive_time;
/*     */   
/*     */ 
/*     */   public DHTUDPPacketReply(DHTTransportUDPImpl _transport, int _type, DHTUDPPacketRequest _request, DHTTransportContact _local_contact, DHTTransportContact _remote_contact)
/*     */   {
/*  82 */     super(_type, _request.getTransactionId());
/*     */     
/*  84 */     this.transport = _transport;
/*     */     
/*  86 */     this.connection_id = _request.getConnectionId();
/*     */     
/*  88 */     this.protocol_version = _remote_contact.getProtocolVersion();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  95 */     if (this.protocol_version > _transport.getProtocolVersion())
/*     */     {
/*  97 */       this.protocol_version = _transport.getProtocolVersion();
/*     */     }
/*     */     
/* 100 */     this.target_instance_id = _local_contact.getInstanceID();
/*     */     
/* 102 */     this.skew = _remote_contact.getClockSkew();
/*     */     
/* 104 */     this.flags = this.transport.getGenericFlags();
/*     */     
/* 106 */     this.flags2 = this.transport.getGenericFlags2();
/*     */     
/* 108 */     this.request_receive_time = _request.getReceiveTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReply(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int type, int trans_id)
/*     */     throws IOException
/*     */   {
/* 121 */     super(type, trans_id);
/*     */     
/* 123 */     setAddress(originator);
/*     */     
/* 125 */     this.connection_id = is.readLong();
/*     */     
/* 127 */     this.protocol_version = is.readByte();
/*     */     
/*     */ 
/*     */ 
/* 131 */     if (this.protocol_version >= 14)
/*     */     {
/* 133 */       this.vendor_id = is.readByte();
/*     */     }
/*     */     
/* 136 */     if (this.protocol_version >= 9)
/*     */     {
/* 138 */       this.network = is.readInt();
/*     */     }
/*     */     
/* 141 */     if (this.protocol_version < (this.network == 1 ? DHTTransportUDP.PROTOCOL_VERSION_MIN_CVS : DHTTransportUDP.PROTOCOL_VERSION_MIN))
/*     */     {
/* 143 */       throw DHTUDPUtils.INVALID_PROTOCOL_VERSION_EXCEPTION;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 148 */     this.transport = network_handler.getTransport(this);
/*     */     
/* 150 */     this.target_instance_id = is.readInt();
/*     */     
/* 152 */     if (this.protocol_version >= 51)
/*     */     {
/* 154 */       this.flags = is.readByte();
/*     */     }
/*     */     
/* 157 */     if (this.protocol_version >= 53)
/*     */     {
/* 159 */       this.flags2 = is.readByte();
/*     */     }
/*     */     
/* 162 */     if (this.protocol_version >= 54)
/*     */     {
/* 164 */       this.processing_time = is.readShort();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportUDPImpl getTransport()
/*     */   {
/* 171 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getTargetInstanceID()
/*     */   {
/* 177 */     return this.target_instance_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getConnectionId()
/*     */   {
/* 183 */     return this.connection_id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getClockSkew()
/*     */   {
/* 189 */     return this.skew;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/* 195 */     return this.protocol_version;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getVendorID()
/*     */   {
/* 201 */     return this.vendor_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetwork()
/*     */   {
/* 207 */     return this.network;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getGenericFlags()
/*     */   {
/* 213 */     return this.flags;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getGenericFlags2()
/*     */   {
/* 219 */     return this.flags2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNetwork(int _network)
/*     */   {
/* 226 */     this.network = _network;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTNetworkPosition[] getNetworkPositions()
/*     */   {
/* 232 */     return this.network_positions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setNetworkPositions(DHTNetworkPosition[] _network_positions)
/*     */   {
/* 239 */     this.network_positions = _network_positions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 248 */     super.serialise(os);
/*     */     
/*     */ 
/*     */ 
/* 252 */     os.writeLong(this.connection_id);
/*     */     
/* 254 */     os.writeByte(this.protocol_version);
/*     */     
/* 256 */     if (this.protocol_version >= 14)
/*     */     {
/* 258 */       os.writeByte(0);
/*     */     }
/*     */     
/* 261 */     if (this.protocol_version >= 9)
/*     */     {
/* 263 */       os.writeInt(this.network);
/*     */     }
/*     */     
/* 266 */     os.writeInt(this.target_instance_id);
/*     */     
/* 268 */     if (this.protocol_version >= 51)
/*     */     {
/* 270 */       os.writeByte(this.flags);
/*     */     }
/*     */     
/* 273 */     if (this.protocol_version >= 53)
/*     */     {
/* 275 */       os.writeByte(this.flags2);
/*     */     }
/*     */     
/* 278 */     if (this.protocol_version >= 54)
/*     */     {
/* 280 */       if (this.request_receive_time == 0L)
/*     */       {
/* 282 */         os.writeShort(0);
/*     */       }
/*     */       else
/*     */       {
/* 286 */         short processing_time = (short)(int)(SystemTime.getCurrentTime() - this.request_receive_time);
/*     */         
/* 288 */         if (processing_time <= 0)
/*     */         {
/* 290 */           processing_time = 1;
/*     */         }
/*     */         
/* 293 */         os.writeShort(processing_time);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getProcessingTime()
/*     */   {
/* 301 */     return this.processing_time & 0xFFFF;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 307 */     return super.getString() + ",[con=" + this.connection_id + ",prot=" + this.protocol_version + ",ven=" + this.vendor_id + ",net=" + this.network + ",fl=" + this.flags + "/" + this.flags2 + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */