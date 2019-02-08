/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
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
/*     */ 
/*     */ public class DHTUDPPacketRequest
/*     */   extends PRUDPPacketRequest
/*     */   implements DHTUDPPacket
/*     */ {
/*     */   public static final int DHT_HEADER_SIZE = 43;
/*     */   private final DHTTransportUDPImpl transport;
/*     */   private byte protocol_version;
/*  59 */   private byte vendor_id = -1;
/*     */   
/*     */   private int network;
/*     */   
/*     */   private byte originator_version;
/*     */   
/*     */   private long originator_time;
/*     */   
/*     */   private InetSocketAddress originator_address;
/*     */   
/*     */   private int originator_instance_id;
/*     */   
/*     */   private byte flags;
/*     */   
/*     */   private byte flags2;
/*     */   
/*     */   private long skew;
/*     */   
/*     */   public DHTUDPPacketRequest(DHTTransportUDPImpl _transport, int _type, long _connection_id, DHTTransportUDPContactImpl _local_contact, DHTTransportUDPContactImpl _remote_contact)
/*     */   {
/*  79 */     super(_type, _connection_id);
/*     */     
/*  81 */     this.transport = _transport;
/*     */     
/*     */ 
/*     */ 
/*  85 */     this.protocol_version = _remote_contact.getProtocolVersion();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  92 */     if (this.protocol_version > _transport.getProtocolVersion())
/*     */     {
/*  94 */       this.protocol_version = _transport.getProtocolVersion();
/*     */     }
/*     */     
/*  97 */     this.originator_address = _local_contact.getExternalAddress();
/*  98 */     this.originator_instance_id = _local_contact.getInstanceID();
/*  99 */     this.originator_time = SystemTime.getCurrentTime();
/*     */     
/* 101 */     this.flags = this.transport.getGenericFlags();
/* 102 */     this.flags2 = this.transport.getGenericFlags2();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketRequest(DHTUDPPacketNetworkHandler network_handler, DataInputStream is, int type, long con_id, int trans_id)
/*     */     throws IOException
/*     */   {
/* 115 */     super(type, con_id, trans_id);
/*     */     
/*     */ 
/*     */ 
/* 119 */     this.protocol_version = is.readByte();
/*     */     
/*     */ 
/*     */ 
/* 123 */     if (this.protocol_version >= 14)
/*     */     {
/* 125 */       this.vendor_id = is.readByte();
/*     */     }
/*     */     
/* 128 */     if (this.protocol_version >= 9)
/*     */     {
/* 130 */       this.network = is.readInt();
/*     */     }
/*     */     
/* 133 */     if (this.protocol_version < (this.network == 1 ? DHTTransportUDP.PROTOCOL_VERSION_MIN_CVS : DHTTransportUDP.PROTOCOL_VERSION_MIN))
/*     */     {
/* 135 */       throw DHTUDPUtils.INVALID_PROTOCOL_VERSION_EXCEPTION;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 140 */     this.transport = network_handler.getTransport(this);
/*     */     
/* 142 */     if (this.protocol_version >= 9)
/*     */     {
/* 144 */       this.originator_version = is.readByte();
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 151 */       this.originator_version = this.protocol_version;
/*     */     }
/*     */     
/* 154 */     this.originator_address = DHTUDPUtils.deserialiseAddress(is);
/*     */     
/* 156 */     this.originator_instance_id = is.readInt();
/*     */     
/* 158 */     this.originator_time = is.readLong();
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
/* 173 */     this.skew = (SystemTime.getCurrentTime() - this.originator_time);
/*     */     
/* 175 */     this.transport.recordSkew(this.originator_address, this.skew);
/*     */     
/* 177 */     if (this.protocol_version >= 51)
/*     */     {
/* 179 */       this.flags = is.readByte();
/*     */     }
/*     */     
/* 182 */     if (this.protocol_version >= 53)
/*     */     {
/* 184 */       this.flags2 = is.readByte();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void postDeserialise(DataInputStream is)
/*     */     throws IOException
/*     */   {
/* 194 */     if (this.protocol_version < 9)
/*     */     {
/* 196 */       if (is.available() > 0)
/*     */       {
/* 198 */         this.originator_version = is.readByte();
/*     */       }
/*     */       else
/*     */       {
/* 202 */         this.originator_version = this.protocol_version;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */       if (this.originator_version > getTransport().getProtocolVersion())
/*     */       {
/* 211 */         this.originator_version = getTransport().getProtocolVersion();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 222 */     super.serialise(os);
/*     */     
/*     */ 
/*     */ 
/* 226 */     os.writeByte(this.protocol_version);
/*     */     
/* 228 */     if (this.protocol_version >= 14)
/*     */     {
/* 230 */       os.writeByte(0);
/*     */     }
/*     */     
/* 233 */     if (this.protocol_version >= 9)
/*     */     {
/* 235 */       os.writeInt(this.network);
/*     */     }
/*     */     
/* 238 */     if (this.protocol_version >= 9)
/*     */     {
/*     */ 
/*     */ 
/* 242 */       os.writeByte(getTransport().getProtocolVersion());
/*     */     }
/*     */     try
/*     */     {
/* 246 */       DHTUDPUtils.serialiseAddress(os, this.originator_address);
/*     */     }
/*     */     catch (DHTTransportException e)
/*     */     {
/* 250 */       throw new IOException(e.getMessage());
/*     */     }
/*     */     
/* 253 */     os.writeInt(this.originator_instance_id);
/*     */     
/* 255 */     os.writeLong(this.originator_time);
/*     */     
/* 257 */     if (this.protocol_version >= 51)
/*     */     {
/* 259 */       os.writeByte(this.flags);
/*     */     }
/*     */     
/* 262 */     if (this.protocol_version >= 53)
/*     */     {
/* 264 */       os.writeByte(this.flags2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void postSerialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 274 */     if (this.protocol_version < 9)
/*     */     {
/*     */ 
/*     */ 
/* 278 */       os.writeByte(getTransport().getProtocolVersion());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTTransportUDPImpl getTransport()
/*     */   {
/* 285 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getClockSkew()
/*     */   {
/* 291 */     return this.skew;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getProtocolVersion()
/*     */   {
/* 297 */     return this.protocol_version;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getVendorID()
/*     */   {
/* 303 */     return this.vendor_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetwork()
/*     */   {
/* 309 */     return this.network;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNetwork(int _network)
/*     */   {
/* 316 */     this.network = _network;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getGenericFlags()
/*     */   {
/* 322 */     return this.flags;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getGenericFlags2()
/*     */   {
/* 328 */     return this.flags2;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getOriginatorVersion()
/*     */   {
/* 334 */     return this.originator_version;
/*     */   }
/*     */   
/*     */ 
/*     */   protected InetSocketAddress getOriginatorAddress()
/*     */   {
/* 340 */     return this.originator_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setOriginatorAddress(InetSocketAddress address)
/*     */   {
/* 347 */     this.originator_address = address;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getOriginatorInstanceID()
/*     */   {
/* 353 */     return this.originator_instance_id;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 359 */     return super.getString() + ",[prot=" + this.protocol_version + ",ven=" + this.vendor_id + ",net=" + this.network + ",ov=" + this.originator_version + ",fl=" + this.flags + "/" + this.flags2 + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */