/*    */ package com.aelitis.azureus.core.dht;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*    */ import com.aelitis.azureus.core.dht.impl.DHTImpl;
/*    */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherAdapter;
/*    */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*    */ import java.util.Properties;
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
/*    */ 
/*    */ public class DHTFactory
/*    */ {
/*    */   public static DHT create(DHTTransport transport, Properties properties, DHTStorageAdapter storage_adapter, DHTNATPuncherAdapter nat_adapter, DHTLogger logger)
/*    */   {
/* 46 */     return new DHTImpl(transport, properties, storage_adapter, nat_adapter, logger);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static DHT create(DHTTransport transport, DHTRouter router, DHTDB database, Properties properties, DHTStorageAdapter storage_adapter, DHTLogger logger)
/*    */   {
/* 58 */     return new DHTImpl(transport, router, database, properties, storage_adapter, logger);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/DHTFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */