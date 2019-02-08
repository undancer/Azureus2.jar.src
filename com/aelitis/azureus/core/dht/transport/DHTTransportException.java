/*    */ package com.aelitis.azureus.core.dht.transport;
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
/*    */ public class DHTTransportException
/*    */   extends Exception
/*    */ {
/*    */   public DHTTransportException(String str)
/*    */   {
/* 36 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DHTTransportException(String str, Throwable cause)
/*    */   {
/* 44 */     super(str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */