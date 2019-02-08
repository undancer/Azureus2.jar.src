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
/*    */ public class DHTUDPPacketReplyKeyBlock
/*    */   extends DHTUDPPacketReply
/*    */ {
/*    */   public DHTUDPPacketReplyKeyBlock(DHTTransportUDPImpl transport, DHTUDPPacketRequestKeyBlock request, DHTTransportContact local_contact, DHTTransportContact remote_contact)
/*    */   {
/* 47 */     super(transport, 1037, request, local_contact, remote_contact);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected DHTUDPPacketReplyKeyBlock(DHTUDPPacketNetworkHandler network_handler, InetSocketAddress originator, DataInputStream is, int trans_id)
/*    */     throws IOException
/*    */   {
/* 59 */     super(network_handler, originator, is, 1037, trans_id);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serialise(DataOutputStream os)
/*    */     throws IOException
/*    */   {
/* 68 */     super.serialise(os);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTUDPPacketReplyKeyBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */