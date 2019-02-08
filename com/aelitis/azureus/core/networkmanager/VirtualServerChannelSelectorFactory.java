/*    */ package com.aelitis.azureus.core.networkmanager;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.impl.tcp.VirtualBlockingServerChannelSelector;
/*    */ import com.aelitis.azureus.core.networkmanager.impl.tcp.VirtualNonBlockingServerChannelSelector;
/*    */ import java.net.InetSocketAddress;
/*    */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*    */ public class VirtualServerChannelSelectorFactory
/*    */ {
/*    */   public static VirtualServerChannelSelector createBlocking(InetSocketAddress bind_address, int so_rcvbuf_size, VirtualServerChannelSelector.SelectListener listener)
/*    */   {
/* 38 */     return new VirtualBlockingServerChannelSelector(bind_address, so_rcvbuf_size, listener);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static VirtualServerChannelSelector createNonBlocking(InetSocketAddress bind_address, int so_rcvbuf_size, VirtualServerChannelSelector.SelectListener listener)
/*    */   {
/* 47 */     return new VirtualNonBlockingServerChannelSelector(bind_address, so_rcvbuf_size, listener);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static VirtualServerChannelSelector createTest(InetSocketAddress bind_address, int so_rcvbuf_size, VirtualServerChannelSelector.SelectListener listener)
/*    */   {
/* 58 */     int range = COConfigurationManager.getIntParameter("TCP.Listen.Port.Range", -1);
/*    */     
/* 60 */     if (range == -1)
/*    */     {
/* 62 */       return createBlocking(bind_address, so_rcvbuf_size, listener);
/*    */     }
/*    */     
/*    */ 
/* 66 */     return new VirtualNonBlockingServerChannelSelector(bind_address.getAddress(), bind_address.getPort(), range, so_rcvbuf_size, listener);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/VirtualServerChannelSelectorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */