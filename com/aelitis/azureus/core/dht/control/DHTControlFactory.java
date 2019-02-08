/*    */ package com.aelitis.azureus.core.dht.control;
/*    */ 
/*    */ import com.aelitis.azureus.core.dht.DHTLogger;
/*    */ import com.aelitis.azureus.core.dht.control.impl.DHTControlImpl;
/*    */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*    */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*    */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHTControlFactory
/*    */ {
/*    */   public static DHTControl create(DHTControlAdapter adapter, DHTTransport transport, int K, int B, int max_rep_per_node, int search_concurrency, int lookup_concurrency, int original_republish_interval, int cache_republish_interval, int cache_at_closest_n, boolean encode_keys, boolean enable_random_poking, DHTLogger logger)
/*    */   {
/* 53 */     return new DHTControlImpl(adapter, transport, K, B, max_rep_per_node, search_concurrency, lookup_concurrency, original_republish_interval, cache_republish_interval, cache_at_closest_n, encode_keys, enable_random_poking, logger);
/*    */   }
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
/*    */   public static DHTControl create(DHTControlAdapter adapter, DHTTransport transport, DHTRouter router, DHTDB database, int K, int B, int max_rep_per_node, int search_concurrency, int lookup_concurrency, int original_republish_interval, int cache_republish_interval, int cache_at_closest_n, boolean encode_keys, boolean enable_random_poking, DHTLogger logger)
/*    */   {
/* 85 */     return new DHTControlImpl(adapter, transport, router, database, K, B, max_rep_per_node, search_concurrency, lookup_concurrency, original_republish_interval, cache_republish_interval, cache_at_closest_n, encode_keys, enable_random_poking, logger);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/DHTControlFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */