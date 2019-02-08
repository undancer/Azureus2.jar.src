/*     */ package org.gudy.azureus2.pluginsimpl.local.download;
/*     */ 
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerStats;
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
/*     */ public class DownloadManagerStatsImpl
/*     */   implements DownloadManagerStats
/*     */ {
/*     */   private GlobalManagerStats global_manager_stats;
/*     */   private OverallStats overall_stats;
/*     */   
/*     */   protected DownloadManagerStatsImpl(GlobalManager _gm)
/*     */   {
/*  40 */     this.global_manager_stats = _gm.getStats();
/*     */     
/*  42 */     this.overall_stats = StatsFactory.getStats();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getOverallDataBytesReceived()
/*     */   {
/*  48 */     return this.overall_stats.getDownloadedBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getOverallDataBytesSent()
/*     */   {
/*  54 */     return this.overall_stats.getUploadedBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSessionUptimeSeconds()
/*     */   {
/*  60 */     return this.overall_stats.getSessionUpTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataReceiveRate()
/*     */   {
/*  66 */     return this.global_manager_stats.getDataReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getProtocolReceiveRate()
/*     */   {
/*  72 */     return this.global_manager_stats.getProtocolReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataAndProtocolReceiveRate()
/*     */   {
/*  78 */     return this.global_manager_stats.getDataAndProtocolReceiveRate();
/*     */   }
/*     */   
/*     */   public int getDataSendRate()
/*     */   {
/*  83 */     return this.global_manager_stats.getDataSendRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getProtocolSendRate()
/*     */   {
/*  89 */     return this.global_manager_stats.getProtocolSendRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataAndProtocolSendRate()
/*     */   {
/*  95 */     return this.global_manager_stats.getDataAndProtocolSendRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDataBytesReceived()
/*     */   {
/* 101 */     return this.global_manager_stats.getTotalDataBytesReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getProtocolBytesReceived()
/*     */   {
/* 107 */     return this.global_manager_stats.getTotalProtocolBytesReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDataBytesSent()
/*     */   {
/* 113 */     return this.global_manager_stats.getTotalDataBytesSent();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getProtocolBytesSent()
/*     */   {
/* 119 */     return this.global_manager_stats.getTotalProtocolBytesSent();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSmoothedReceiveRate()
/*     */   {
/* 125 */     return this.global_manager_stats.getSmoothedReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSmoothedSendRate()
/*     */   {
/* 131 */     return this.global_manager_stats.getSmoothedSendRate();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */