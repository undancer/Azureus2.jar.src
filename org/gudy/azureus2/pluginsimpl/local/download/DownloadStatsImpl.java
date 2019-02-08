/*     */ package org.gudy.azureus2.pluginsimpl.local.download;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
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
/*     */ public class DownloadStatsImpl
/*     */   implements DownloadStats
/*     */ {
/*     */   protected DownloadManager dm;
/*     */   protected DownloadManagerStats dm_stats;
/*     */   
/*     */   protected DownloadStatsImpl(DownloadManager _dm)
/*     */   {
/*  46 */     this.dm = _dm;
/*  47 */     this.dm_stats = this.dm.getStats();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus()
/*     */   {
/*  53 */     return DisplayFormatters.formatDownloadStatusDefaultLocale(this.dm);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus(boolean localised)
/*     */   {
/*  59 */     return localised ? DisplayFormatters.formatDownloadStatus(this.dm) : getStatus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDownloadDirectory()
/*     */   {
/*  66 */     return this.dm.getSaveLocation().getParent();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTargetFileOrDir()
/*     */   {
/*  72 */     return this.dm.getSaveLocation().toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTrackerStatus()
/*     */   {
/*  78 */     return this.dm.getTrackerStatus();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCompleted()
/*     */   {
/*  84 */     return this.dm_stats.getCompleted();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadCompleted(boolean bLive)
/*     */   {
/*  90 */     return this.dm_stats.getDownloadCompleted(bLive);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getCheckingDoneInThousandNotation()
/*     */   {
/*  97 */     DiskManager disk = this.dm.getDiskManager();
/*     */     
/*  99 */     if (disk != null)
/*     */     {
/* 101 */       return disk.getCompleteRecheckStatus();
/*     */     }
/*     */     
/* 104 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetUploadedDownloaded(long new_up, long new_down)
/*     */   {
/* 112 */     this.dm_stats.resetTotalBytesSentReceived(new_up, new_down);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 118 */     return this.dm_stats.getTotalDataBytesReceived();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDownloaded(boolean include_protocol)
/*     */   {
/* 125 */     long res = this.dm_stats.getTotalDataBytesReceived();
/*     */     
/* 127 */     if (include_protocol)
/*     */     {
/* 129 */       res += this.dm_stats.getTotalProtocolBytesReceived();
/*     */     }
/*     */     
/* 132 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRemaining()
/*     */   {
/* 138 */     return this.dm_stats.getRemaining();
/*     */   }
/*     */   
/*     */   public long getRemainingExcludingDND() {
/* 142 */     return this.dm_stats.getRemainingExcludingDND();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/* 148 */     return this.dm_stats.getTotalDataBytesSent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getUploaded(boolean include_protocol)
/*     */   {
/* 155 */     long res = this.dm_stats.getTotalDataBytesSent();
/*     */     
/* 157 */     if (include_protocol)
/*     */     {
/* 159 */       res += this.dm_stats.getTotalProtocolBytesSent();
/*     */     }
/*     */     
/* 162 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDiscarded()
/*     */   {
/* 168 */     return this.dm_stats.getDiscarded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloadAverage()
/*     */   {
/* 174 */     return this.dm_stats.getDataReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDownloadAverage(boolean include_protocol)
/*     */   {
/* 181 */     long res = this.dm_stats.getDataReceiveRate();
/*     */     
/* 183 */     if (include_protocol)
/*     */     {
/* 185 */       res += this.dm_stats.getProtocolReceiveRate();
/*     */     }
/*     */     
/* 188 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploadAverage()
/*     */   {
/* 194 */     return this.dm_stats.getDataSendRate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getUploadAverage(boolean include_protocol)
/*     */   {
/* 201 */     long res = this.dm_stats.getDataSendRate();
/*     */     
/* 203 */     if (include_protocol)
/*     */     {
/* 205 */       res += this.dm_stats.getProtocolSendRate();
/*     */     }
/*     */     
/* 208 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalAverage()
/*     */   {
/* 214 */     return this.dm_stats.getTotalAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getElapsedTime()
/*     */   {
/* 220 */     return this.dm_stats.getElapsedTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getETA()
/*     */   {
/* 229 */     return DisplayFormatters.formatETA(this.dm_stats.getSmoothedETA());
/*     */   }
/*     */   
/*     */ 
/*     */   public long getETASecs()
/*     */   {
/* 235 */     return this.dm_stats.getSmoothedETA();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getHashFails()
/*     */   {
/* 241 */     return this.dm_stats.getHashFailCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getShareRatio()
/*     */   {
/* 247 */     return this.dm_stats.getShareRatio();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getTimeStarted()
/*     */   {
/* 254 */     return this.dm_stats.getTimeStarted();
/*     */   }
/*     */   
/*     */ 
/*     */   public float getAvailability()
/*     */   {
/* 260 */     return this.dm_stats.getAvailability();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesUnavailable()
/*     */   {
/* 266 */     return this.dm_stats.getBytesUnavailable();
/*     */   }
/*     */   
/*     */   public long getSecondsOnlySeeding() {
/* 270 */     return this.dm_stats.getSecondsOnlySeeding();
/*     */   }
/*     */   
/*     */   public long getSecondsDownloading() {
/* 274 */     return this.dm_stats.getSecondsDownloading();
/*     */   }
/*     */   
/*     */   public long getTimeStartedSeeding() {
/* 278 */     return this.dm_stats.getTimeStartedSeeding();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSecondsSinceLastDownload()
/*     */   {
/* 284 */     return this.dm_stats.getTimeSinceLastDataReceivedInSeconds();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSecondsSinceLastUpload()
/*     */   {
/* 290 */     return this.dm_stats.getTimeSinceLastDataSentInSeconds();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHealth()
/*     */   {
/* 296 */     switch (this.dm.getHealthStatus())
/*     */     {
/*     */ 
/*     */     case 1: 
/* 300 */       return 1;
/*     */     
/*     */ 
/*     */ 
/*     */     case 2: 
/* 305 */       return 2;
/*     */     
/*     */ 
/*     */ 
/*     */     case 3: 
/* 310 */       return 3;
/*     */     
/*     */ 
/*     */ 
/*     */     case 4: 
/* 315 */       return 4;
/*     */     
/*     */ 
/*     */ 
/*     */     case 5: 
/* 320 */       return 5;
/*     */     
/*     */ 
/*     */ 
/*     */     case 6: 
/* 325 */       return 6;
/*     */     }
/*     */     
/*     */     
/* 329 */     Debug.out("Invalid health status");
/*     */     
/* 331 */     return this.dm.getHealthStatus();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */