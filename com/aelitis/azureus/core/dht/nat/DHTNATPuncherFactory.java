/*    */ package com.aelitis.azureus.core.dht.nat;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.DHT;
/*    */ import com.aelitis.azureus.core.dht.nat.impl.DHTNATPuncherImpl;
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
/*    */ public class DHTNATPuncherFactory
/*    */ {
/*    */   public static DHTNATPuncher create(DHTNATPuncherAdapter adapter, DHT dht)
/*    */   {
/* 33 */     return new DHTNATPuncherImpl(adapter, dht);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/nat/DHTNATPuncherFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */