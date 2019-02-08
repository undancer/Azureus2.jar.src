/*    */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*    */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHTUDPPacketReplyStore
/*    */   extends DHTUDPPacketReply
/*    */ {
/*    */   private byte[] diversify;
/*    */   
/*    */   public DHTUDPPacketReplyStore(DHTTransportUDPImpl transport, DHTUDPPacketRequestStore request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*    */   {
/* 50 */     super(transport, 1027, request, local_contact, remote_contact);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected DHTUDPPacketReplyStore(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*    */     throws IOException
/*    */   {
/* 62 */     super(network_handler, originator, is, 1027, trans_id);
/*    */     
/* 64 */     if (getProtocolVersion() >= 6)
/*    */     {
/* 66 */       this.diversify = DHTUDPUtils.deserialiseByteArray(is, 255);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serialise(DataOutputStream os)
/*    */     throws IOException
/*    */   {
/* 76 */     super.serialise(os);
/*    */     
/* 78 */     if (getProtocolVersion() >= 6)
/*    */     {
/* 80 */       DHTUDPUtils.serialiseByteArray(os, this.diversify, 255);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setDiversificationTypes(byte[] _diversify)
/*    */   {
/* 88 */     this.diversify = _diversify;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getDiversificationTypes()
/*    */   {
/* 94 */     return this.diversify;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */