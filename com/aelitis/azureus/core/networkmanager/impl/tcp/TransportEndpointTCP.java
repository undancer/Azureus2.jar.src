/*    */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*    */ import com.aelitis.azureus.core.networkmanager.TransportEndpoint;
/*    */ import java.nio.channels.SocketChannel;
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
/*    */ public class TransportEndpointTCP
/*    */   implements TransportEndpoint
/*    */ {
/*    */   private final ProtocolEndpoint pe;
/*    */   private final SocketChannel sc;
/*    */   
/*    */   public TransportEndpointTCP(ProtocolEndpoint _pe, SocketChannel _sc)
/*    */   {
/* 39 */     this.pe = _pe;
/* 40 */     this.sc = _sc;
/*    */   }
/*    */   
/*    */ 
/*    */   public ProtocolEndpoint getProtocolEndpoint()
/*    */   {
/* 46 */     return this.pe;
/*    */   }
/*    */   
/*    */ 
/*    */   public SocketChannel getSocketChannel()
/*    */   {
/* 52 */     return this.sc;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TransportEndpointTCP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */