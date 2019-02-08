/*    */ package org.gudy.azureus2.core3.stats.transfer;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*    */ import org.gudy.azureus2.core3.stats.transfer.impl.LongTermStatsWrapper;
/*    */ import org.gudy.azureus2.core3.stats.transfer.impl.OverallStatsImpl;
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
/*    */ public class StatsFactory
/*    */ {
/*    */   private static OverallStats overall_stats;
/*    */   private static LongTermStats longterm_stats;
/* 43 */   private static final Map<String, LongTermStats> generic_longterm_stats = new HashMap();
/*    */   
/*    */ 
/*    */   public static OverallStats getStats()
/*    */   {
/* 48 */     return overall_stats;
/*    */   }
/*    */   
/*    */ 
/*    */   public static LongTermStats getLongTermStats()
/*    */   {
/* 54 */     return longterm_stats;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void initialize(AzureusCore core, GlobalManagerStats stats)
/*    */   {
/* 62 */     overall_stats = new OverallStatsImpl(core, stats);
/* 63 */     longterm_stats = new LongTermStatsWrapper(core, stats);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static LongTermStats getGenericLongTermStats(String id, LongTermStats.GenericStatsSource source)
/*    */   {
/* 71 */     synchronized (generic_longterm_stats)
/*    */     {
/* 73 */       LongTermStats result = (LongTermStats)generic_longterm_stats.get(id);
/*    */       
/* 75 */       if (result == null)
/*    */       {
/* 77 */         result = new LongTermStatsWrapper(id, source);
/*    */         
/* 79 */         generic_longterm_stats.put(id, result);
/*    */       }
/*    */       
/* 82 */       return result;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public static void clearLongTermStats()
/*    */   {
/* 89 */     longterm_stats.reset();
/*    */     
/* 91 */     synchronized (generic_longterm_stats)
/*    */     {
/* 93 */       for (LongTermStats lts : generic_longterm_stats.values())
/*    */       {
/* 95 */         lts.reset();
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/StatsFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */