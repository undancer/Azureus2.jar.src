/*    */ package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;
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
/*    */ public class DHTUDPPacketHandlerException
/*    */   extends Exception
/*    */ {
/*    */   public DHTUDPPacketHandlerException(String str)
/*    */   {
/* 31 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DHTUDPPacketHandlerException(Throwable cause)
/*    */   {
/* 38 */     super(cause.getMessage() == null ? cause.toString() : cause.getMessage());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHTUDPPacketHandlerException(String str, Throwable cause)
/*    */   {
/* 46 */     super(str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketHandlerException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */