/*    */ package org.gudy.azureus2.core3.global.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*    */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*    */ import org.gudy.azureus2.core3.stats.StatsWriterFactory;
/*    */ import org.gudy.azureus2.core3.stats.StatsWriterPeriodic;
/*    */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
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
/*    */ public class GlobalManagerStatsWriter
/*    */ {
/*    */   protected final StatsWriterPeriodic stats_writer;
/*    */   
/*    */   protected GlobalManagerStatsWriter(AzureusCore core, GlobalManagerStats stats)
/*    */   {
/* 43 */     StatsFactory.initialize(core, stats);
/*    */     
/* 45 */     this.stats_writer = StatsWriterFactory.createPeriodicDumper(core);
/*    */     
/* 47 */     core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*    */     {
/*    */ 
/*    */ 
/*    */       public void started(AzureusCore core)
/*    */       {
/*    */ 
/* 54 */         GlobalManagerStatsWriter.this.stats_writer.start();
/*    */         
/* 56 */         core.removeLifecycleListener(this);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected void destroy()
/*    */   {
/* 65 */     if (this.stats_writer != null)
/*    */     {
/* 67 */       this.stats_writer.stop();
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/impl/GlobalManagerStatsWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */