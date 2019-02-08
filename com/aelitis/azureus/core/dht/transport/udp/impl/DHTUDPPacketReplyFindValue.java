/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTUDPPacketReplyFindValue
/*     */   extends DHTUDPPacketReply
/*     */ {
/*     */   public static final int DHT_FIND_VALUE_HEADER_SIZE = 30;
/*     */   public static final int DHT_FIND_VALUE_TV_HEADER_SIZE = 26;
/*     */   private DHTTransportContact[] contacts;
/*     */   private DHTTransportValue[] values;
/*     */   private boolean has_continuation;
/*  52 */   private byte diversification_type = 1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTUDPPacketReplyFindValue(DHTTransportUDPImpl transport, DHTUDPPacketRequestFindValue request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*     */   {
/*  61 */     super(transport, 1031, request, local_contact, remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReplyFindValue(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  73 */     super(network_handler, originator, is, 1031, trans_id);
/*     */     
/*  75 */     if (getProtocolVersion() >= 6)
/*     */     {
/*  77 */       this.has_continuation = is.readBoolean();
/*     */     }
/*     */     
/*  80 */     boolean is_value = is.readBoolean();
/*     */     
/*  82 */     if (is_value)
/*     */     {
/*  84 */       if (getProtocolVersion() >= 6)
/*     */       {
/*  86 */         this.diversification_type = is.readByte();
/*     */       }
/*     */       
/*  89 */       this.values = DHTUDPUtils.deserialiseTransportValues(this, is, 0L);
/*     */     }
/*     */     else
/*     */     {
/*  93 */       this.contacts = DHTUDPUtils.deserialiseContacts(getTransport(), is);
/*     */       
/*  95 */       if (getProtocolVersion() >= 16)
/*     */       {
/*  97 */         DHTUDPUtils.deserialiseVivaldi(this, is);
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
/* 108 */     super.serialise(os);
/*     */     
/* 110 */     if (getProtocolVersion() >= 6)
/*     */     {
/* 112 */       os.writeBoolean(this.has_continuation);
/*     */     }
/*     */     
/* 115 */     os.writeBoolean(this.values != null);
/*     */     
/* 117 */     if (this.values == null)
/*     */     {
/* 119 */       DHTUDPUtils.serialiseContacts(os, this.contacts);
/*     */       
/* 121 */       if (getProtocolVersion() >= 16)
/*     */       {
/* 123 */         DHTUDPUtils.serialiseVivaldi(this, os);
/*     */       }
/*     */     }
/*     */     else {
/* 127 */       if (getProtocolVersion() >= 6)
/*     */       {
/* 129 */         os.writeByte(this.diversification_type);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 135 */         DHTUDPUtils.serialiseTransportValues(this, os, this.values, -getClockSkew());
/*     */       }
/*     */       catch (DHTTransportException e)
/*     */       {
/* 139 */         throw new IOException(e.getMessage());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasContinuation()
/*     */   {
/* 147 */     return this.has_continuation;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setValues(DHTTransportValue[] _values, byte _diversification_type, boolean _has_continuation)
/*     */   {
/* 156 */     this.has_continuation = _has_continuation;
/* 157 */     this.diversification_type = _diversification_type;
/* 158 */     this.values = _values;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportValue[] getValues()
/*     */   {
/* 164 */     return this.values;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getDiversificationType()
/*     */   {
/* 170 */     return this.diversification_type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setContacts(DHTTransportContact[] _contacts)
/*     */   {
/* 177 */     this.contacts = _contacts;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTTransportContact[] getContacts()
/*     */   {
/* 183 */     return this.contacts;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 189 */     return super.getString() + ",contacts=" + (this.contacts == null ? "null" : new StringBuilder().append("").append(this.contacts.length).toString());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyFindValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */