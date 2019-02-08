/*    */ package org.gudy.azureus2.core3.stats;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import org.gudy.azureus2.core3.stats.impl.StatsWriterPeriodicImpl;
/*    */ import org.gudy.azureus2.core3.stats.impl.StatsWriterStreamerImpl;
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
/*    */ public class StatsWriterFactory
/*    */ {
/*    */   public static StatsWriterPeriodic createPeriodicDumper(AzureusCore core)
/*    */   {
/* 41 */     return StatsWriterPeriodicImpl.create(core);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static StatsWriterStreamer createStreamer(AzureusCore core)
/*    */   {
/* 48 */     return new StatsWriterStreamerImpl(core);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/StatsWriterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */