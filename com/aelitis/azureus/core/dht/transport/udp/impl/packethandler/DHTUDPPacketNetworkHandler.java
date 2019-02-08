/*    */ package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTTransportUDPImpl;
/*    */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacket;
/*    */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketReply;
/*    */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketRequest;
/*    */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*    */ import com.aelitis.net.udp.uc.PRUDPRequestHandler;
/*    */ import java.io.IOException;
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
/*    */ public class DHTUDPPacketNetworkHandler
/*    */   implements PRUDPRequestHandler
/*    */ {
/*    */   private final DHTUDPPacketHandlerFactory factory;
/*    */   private final int port;
/*    */   
/*    */   protected DHTUDPPacketNetworkHandler(DHTUDPPacketHandlerFactory _factory, int _port)
/*    */   {
/* 43 */     this.factory = _factory;
/* 44 */     this.port = _port;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHTTransportUDPImpl getTransport(DHTUDPPacket packet)
/*    */     throws IOException
/*    */   {
/* 54 */     if ((packet instanceof DHTUDPPacketRequest))
/*    */     {
/* 56 */       return this.factory.getTransport(this.port, ((DHTUDPPacketRequest)packet).getNetwork());
/*    */     }
/*    */     
/*    */ 
/* 60 */     return this.factory.getTransport(this.port, ((DHTUDPPacketReply)packet).getNetwork());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void process(PRUDPPacketRequest _request)
/*    */   {
/* 68 */     if ((_request instanceof DHTUDPPacketRequest))
/*    */     {
/* 70 */       DHTUDPPacketRequest request = (DHTUDPPacketRequest)_request;
/*    */       
/* 72 */       this.factory.process(this.port, request);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketNetworkHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */