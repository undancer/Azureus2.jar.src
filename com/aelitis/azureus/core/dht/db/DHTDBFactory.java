/*    */ package com.aelitis.azureus.core.dht.db;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.DHTLogger;
/*    */ import com.aelitis.azureus.core.dht.DHTStorageAdapter;
/*    */ import com.aelitis.azureus.core.dht.db.impl.DHTDBImpl;
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
/*    */ public class DHTDBFactory
/*    */ {
/*    */   public static DHTDB create(DHTStorageAdapter adapter, int original_republish_interval, int cache_republish_interval, byte protocol_version, DHTLogger logger)
/*    */   {
/* 42 */     return new DHTDBImpl(adapter, original_republish_interval, cache_republish_interval, protocol_version, logger);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/db/DHTDBFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */