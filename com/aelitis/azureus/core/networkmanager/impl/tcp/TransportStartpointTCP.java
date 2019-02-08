/*    */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.ProtocolStartpoint;
/*    */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
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
/*    */ public class TransportStartpointTCP
/*    */   implements TransportStartpoint, ProtocolStartpoint
/*    */ {
/*    */   private final TransportEndpointTCP ep;
/*    */   
/*    */   public TransportStartpointTCP(TransportEndpointTCP _ep)
/*    */   {
/* 40 */     this.ep = _ep;
/*    */   }
/*    */   
/*    */ 
/*    */   public ProtocolStartpoint getProtocolStartpoint()
/*    */   {
/* 46 */     return this;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getType()
/*    */   {
/* 52 */     return 1;
/*    */   }
/*    */   
/*    */ 
/*    */   public InetSocketAddress getAddress()
/*    */   {
/* 58 */     SocketChannel channel = this.ep.getSocketChannel();
/*    */     
/* 60 */     if (channel != null)
/*    */     {
/* 62 */       Socket socket = channel.socket();
/*    */       
/* 64 */       if (socket != null)
/*    */       {
/* 66 */         return (InetSocketAddress)socket.getLocalSocketAddress();
/*    */       }
/*    */     }
/*    */     
/* 70 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getDescription()
/*    */   {
/* 76 */     InetSocketAddress address = getAddress();
/*    */     
/* 78 */     if (address == null)
/*    */     {
/* 80 */       return "not connected";
/*    */     }
/*    */     
/* 83 */     return address.toString();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TransportStartpointTCP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */