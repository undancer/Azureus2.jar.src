/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTUDPPacketReplyError
/*     */   extends DHTUDPPacketReply
/*     */ {
/*     */   public static final int ET_UNKNOWN = 0;
/*     */   public static final int ET_ORIGINATOR_ADDRESS_WRONG = 1;
/*     */   public static final int ET_KEY_BLOCKED = 2;
/*  47 */   private int error_type = 0;
/*     */   
/*     */ 
/*     */   private InetSocketAddress originator_address;
/*     */   
/*     */ 
/*     */   private byte[] key_block_request;
/*     */   
/*     */   private byte[] key_block_signature;
/*     */   
/*     */ 
/*     */   public DHTUDPPacketReplyError(DHTTransportUDPImpl transport, DHTUDPPacketRequest request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*     */   {
/*  60 */     super(transport, 1032, request, local_contact, remote_contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTUDPPacketReplyError(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  72 */     super(network_handler, originator, is, 1032, trans_id);
/*     */     
/*  74 */     this.error_type = is.readInt();
/*     */     
/*  76 */     if (this.error_type == 1)
/*     */     {
/*  78 */       this.originator_address = DHTUDPUtils.deserialiseAddress(is);
/*     */     }
/*  80 */     else if (this.error_type == 2)
/*     */     {
/*  82 */       this.key_block_request = DHTUDPUtils.deserialiseByteArray(is, 255);
/*  83 */       this.key_block_signature = DHTUDPUtils.deserialiseByteArray(is, 65535);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setErrorType(int error)
/*     */   {
/*  91 */     this.error_type = error;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getErrorType()
/*     */   {
/*  97 */     return this.error_type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setOriginatingAddress(InetSocketAddress a)
/*     */   {
/* 104 */     this.originator_address = a;
/*     */   }
/*     */   
/*     */ 
/*     */   protected InetSocketAddress getOriginatingAddress()
/*     */   {
/* 110 */     return this.originator_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setKeyBlockDetails(byte[] kbr, byte[] sig)
/*     */   {
/* 118 */     this.key_block_request = kbr;
/* 119 */     this.key_block_signature = sig;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getKeyBlockRequest()
/*     */   {
/* 125 */     return this.key_block_request;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getKeyBlockSignature()
/*     */   {
/* 131 */     return this.key_block_signature;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/* 140 */     super.serialise(os);
/*     */     
/* 142 */     os.writeInt(this.error_type);
/*     */     
/* 144 */     if (this.error_type == 1)
/*     */     {
/*     */       try {
/* 147 */         DHTUDPUtils.serialiseAddress(os, this.originator_address);
/*     */       }
/*     */       catch (DHTTransportException e)
/*     */       {
/* 151 */         Debug.printStackTrace(e);
/*     */         
/* 153 */         throw new IOException(e.getMessage());
/*     */       }
/* 155 */     } else if (this.error_type == 2)
/*     */     {
/* 157 */       DHTUDPUtils.serialiseByteArray(os, this.key_block_request, 255);
/* 158 */       DHTUDPUtils.serialiseByteArray(os, this.key_block_signature, 65535);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */