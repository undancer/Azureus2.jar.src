/*     */ package com.aelitis.azureus.core.dht.transport.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFindValueReply;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportQueryStoreReply;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportRequestHandler;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStoreReply;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTTransportRequestCounter
/*     */   implements DHTTransportRequestHandler
/*     */ {
/*     */   private final DHTTransportRequestHandler delegate;
/*     */   private final DHTTransportStatsImpl stats;
/*     */   
/*     */   public DHTTransportRequestCounter(DHTTransportRequestHandler _delegate, DHTTransportStatsImpl _stats)
/*     */   {
/*  49 */     this.delegate = _delegate;
/*  50 */     this.stats = _stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void pingRequest(DHTTransportContact contact)
/*     */   {
/*  57 */     this.stats.pingReceived();
/*     */     
/*  59 */     this.delegate.pingRequest(contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void keyBlockRequest(DHTTransportContact contact, byte[] key_block_request, byte[] key_block_signature)
/*     */   {
/*  68 */     this.stats.keyBlockReceived();
/*     */     
/*  70 */     this.delegate.keyBlockRequest(contact, key_block_request, key_block_signature);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DHTTransportFullStats statsRequest(DHTTransportContact contact)
/*     */   {
/*  77 */     this.stats.statsReceived();
/*     */     
/*  79 */     return this.delegate.statsRequest(contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportStoreReply storeRequest(DHTTransportContact contact, byte[][] keys, DHTTransportValue[][] value_sets)
/*     */   {
/*  88 */     this.stats.storeReceived();
/*     */     
/*  90 */     return this.delegate.storeRequest(contact, keys, value_sets);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportQueryStoreReply queryStoreRequest(DHTTransportContact contact, int header_len, List<Object[]> keys)
/*     */   {
/*  99 */     this.stats.queryStoreReceived();
/*     */     
/* 101 */     return this.delegate.queryStoreRequest(contact, header_len, keys);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportContact[] findNodeRequest(DHTTransportContact contact, byte[] id)
/*     */   {
/* 109 */     this.stats.findNodeReceived();
/*     */     
/* 111 */     return this.delegate.findNodeRequest(contact, id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHTTransportFindValueReply findValueRequest(DHTTransportContact contact, byte[] key, int max, short flags)
/*     */   {
/* 121 */     this.stats.findValueReceived();
/*     */     
/* 123 */     return this.delegate.findValueRequest(contact, key, max, flags);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void contactImported(DHTTransportContact contact, boolean is_bootstrap)
/*     */   {
/* 131 */     this.delegate.contactImported(contact, is_bootstrap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void contactRemoved(DHTTransportContact contact)
/*     */   {
/* 138 */     this.delegate.contactRemoved(contact);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTransportEstimatedDHTSize()
/*     */   {
/* 144 */     return this.delegate.getTransportEstimatedDHTSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTransportEstimatedDHTSize(int size)
/*     */   {
/* 151 */     this.delegate.setTransportEstimatedDHTSize(size);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/util/DHTTransportRequestCounter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */