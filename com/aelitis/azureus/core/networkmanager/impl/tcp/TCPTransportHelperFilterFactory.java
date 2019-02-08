/*    */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*    */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilterTransparent;
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
/*    */ public class TCPTransportHelperFilterFactory
/*    */ {
/*    */   public static TransportHelperFilter createTransparentFilter(SocketChannel channel)
/*    */   {
/* 33 */     return new TransportHelperFilterTransparent(new TCPTransportHelper(channel), false);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TCPTransportHelperFilterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */