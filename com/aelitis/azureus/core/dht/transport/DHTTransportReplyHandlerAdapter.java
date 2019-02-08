/*     */ package com.aelitis.azureus.core.dht.transport;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.util.DHTTransportStatsImpl;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ public abstract class DHTTransportReplyHandlerAdapter
/*     */   implements DHTTransportReplyHandler
/*     */ {
/*     */   private int elapsed;
/*     */   
/*     */   public void pingReply(DHTTransportContact contact, int _elapsed)
/*     */   {
/*  44 */     this.elapsed = _elapsed;
/*     */     
/*  46 */     DHTTransportStats stats = contact.getTransport().getStats();
/*     */     
/*  48 */     if ((stats instanceof DHTTransportStatsImpl))
/*     */     {
/*  50 */       if (_elapsed >= 0)
/*     */       {
/*  52 */         ((DHTTransportStatsImpl)stats).receivedRTT(_elapsed);
/*     */       }
/*     */     }
/*     */     
/*  56 */     pingReply(contact);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void pingReply(DHTTransportContact contact)
/*     */   {
/*  63 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getElapsed()
/*     */   {
/*  69 */     return this.elapsed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void statsReply(DHTTransportContact contact, DHTTransportFullStats stats)
/*     */   {
/*  77 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void storeReply(DHTTransportContact contact, byte[] diversifications)
/*     */   {
/*  85 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void queryStoreReply(DHTTransportContact contact, List<byte[]> response)
/*     */   {
/*  93 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void findNodeReply(DHTTransportContact contact, DHTTransportContact[] contacts)
/*     */   {
/* 101 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void findValueReply(DHTTransportContact contact, DHTTransportValue[] values, byte diversification_type, boolean more_to_come)
/*     */   {
/* 111 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void findValueReply(DHTTransportContact contact, DHTTransportContact[] contacts)
/*     */   {
/* 119 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void keyBlockRequest(DHTTransportContact contact, byte[] key, byte[] key_signature)
/*     */   {
/* 128 */     Debug.out("keyblock not handled");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void keyBlockReply(DHTTransportContact _contact)
/*     */   {
/* 135 */     throw new RuntimeException("Not implemented");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/DHTTransportReplyHandlerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */