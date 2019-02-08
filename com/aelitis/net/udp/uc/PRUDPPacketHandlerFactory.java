/*    */ package com.aelitis.net.udp.uc;
/*    */ 
/*    */ import com.aelitis.net.udp.uc.impl.PRUDPPacketHandlerFactoryImpl;
/*    */ import java.net.InetAddress;
/*    */ import java.util.List;
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
/*    */ public class PRUDPPacketHandlerFactory
/*    */ {
/*    */   public static PRUDPPacketHandler getHandler(int port)
/*    */   {
/* 42 */     return getHandler(port, null);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static PRUDPPacketHandler getHandler(int port, PRUDPRequestHandler handler)
/*    */   {
/* 50 */     return PRUDPPacketHandlerFactoryImpl.getHandler(port, null, handler);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static PRUDPPacketHandler getHandler(int port, InetAddress bind_ip, PRUDPRequestHandler handler)
/*    */   {
/* 59 */     return PRUDPPacketHandlerFactoryImpl.getHandler(port, bind_ip, handler);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static PRUDPReleasablePacketHandler getReleasableHandler(int port)
/*    */   {
/* 66 */     return PRUDPPacketHandlerFactoryImpl.getReleasableHandler(port, null);
/*    */   }
/*    */   
/*    */ 
/*    */   public static List<PRUDPPacketHandler> getHandlers()
/*    */   {
/* 72 */     return PRUDPPacketHandlerFactoryImpl.getHandlers();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/PRUDPPacketHandlerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */