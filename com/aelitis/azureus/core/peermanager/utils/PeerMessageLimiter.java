/*    */ package com.aelitis.azureus.core.peermanager.utils;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*    */ public class PeerMessageLimiter
/*    */ {
/* 33 */   private final HashMap message_counts = new HashMap();
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
/*    */   public boolean countIncomingMessage(String message_id, int max_counts, int time_limit_ms)
/*    */   {
/* 51 */     CountData data = (CountData)this.message_counts.get(message_id);
/*    */     
/* 53 */     if (data == null) {
/* 54 */       data = new CountData(max_counts, time_limit_ms, null);
/* 55 */       this.message_counts.put(message_id, data);
/*    */     }
/*    */     
/* 58 */     long now = SystemTime.getCurrentTime();
/*    */     
/* 60 */     data.counts.addLast(new Long(now));
/*    */     
/* 62 */     if (data.counts.size() > data.max_counts)
/*    */     {
/* 64 */       long cutoff = now - data.time_limit;
/*    */       
/*    */ 
/* 67 */       for (Iterator it = data.counts.iterator(); it.hasNext();) {
/* 68 */         long time = ((Long)it.next()).longValue();
/*    */         
/* 70 */         if (time >= cutoff) break;
/* 71 */         it.remove();
/*    */       }
/*    */       
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 78 */       if (data.counts.size() > data.max_counts) {
/* 79 */         return false;
/*    */       }
/*    */     }
/*    */     
/* 83 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   private static class CountData
/*    */   {
/*    */     private final int max_counts;
/*    */     
/*    */     private final int time_limit;
/* 92 */     private final LinkedList counts = new LinkedList();
/*    */     
/*    */     private CountData(int max_counts, int time_limit) {
/* 95 */       this.max_counts = max_counts;
/* 96 */       this.time_limit = time_limit;
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/utils/PeerMessageLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */