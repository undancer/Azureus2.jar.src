/*    */ package com.aelitis.azureus.core.dht.speed;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.DHT;
/*    */ import com.aelitis.azureus.core.dht.DHTLogger;
/*    */ import com.aelitis.azureus.core.dht.speed.impl.DHTSpeedTesterImpl;
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
/*    */ public class DHTSpeedTesterFactory
/*    */ {
/*    */   public static DHTSpeedTester create(DHT dht)
/*    */   {
/* 34 */     if (dht.getLogger().getPluginInterface() == null)
/*    */     {
/* 36 */       return null;
/*    */     }
/*    */     
/* 39 */     return new DHTSpeedTesterImpl(dht);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/speed/DHTSpeedTesterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */