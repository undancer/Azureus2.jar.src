/*     */ package org.gudy.azureus2.pluginsimpl.remote.download;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
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
/*     */ 
/*     */ public class RPDownloadStats
/*     */   extends RPObject
/*     */   implements DownloadStats
/*     */ {
/*     */   protected transient DownloadStats delegate;
/*     */   public long downloaded;
/*     */   public long uploaded;
/*     */   public int completed;
/*     */   public int downloadCompletedLive;
/*     */   public int downloadCompletedStored;
/*     */   public String status;
/*     */   public String status_localised;
/*     */   public long upload_average;
/*     */   public long download_average;
/*     */   public String eta;
/*     */   public int share_ratio;
/*     */   public float availability;
/*     */   public long bytesUnavailable;
/*     */   public int health;
/*     */   
/*     */   public static RPDownloadStats create(DownloadStats _delegate)
/*     */   {
/*  62 */     RPDownloadStats res = (RPDownloadStats)_lookupLocal(_delegate);
/*     */     
/*  64 */     if (res == null)
/*     */     {
/*  66 */       res = new RPDownloadStats(_delegate);
/*     */     }
/*     */     
/*  69 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPDownloadStats(DownloadStats _delegate)
/*     */   {
/*  76 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  83 */     this.delegate = ((DownloadStats)_delegate);
/*     */     
/*  85 */     this.downloaded = this.delegate.getDownloaded();
/*  86 */     this.uploaded = this.delegate.getUploaded();
/*  87 */     this.completed = this.delegate.getCompleted();
/*  88 */     this.downloadCompletedLive = this.delegate.getDownloadCompleted(true);
/*  89 */     this.downloadCompletedStored = this.delegate.getDownloadCompleted(false);
/*  90 */     this.status = this.delegate.getStatus();
/*  91 */     this.status_localised = this.delegate.getStatus(true);
/*  92 */     this.upload_average = this.delegate.getUploadAverage();
/*  93 */     this.download_average = this.delegate.getDownloadAverage();
/*  94 */     this.eta = this.delegate.getETA();
/*  95 */     this.share_ratio = this.delegate.getShareRatio();
/*  96 */     this.availability = this.delegate.getAvailability();
/*  97 */     this.bytesUnavailable = this.delegate.getBytesUnavailable();
/*  98 */     this.health = this.delegate.getHealth();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/* 106 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/* 113 */     String method = request.getMethod();
/*     */     
/* 115 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getStatus()
/*     */   {
/* 124 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus(boolean localised)
/*     */   {
/* 130 */     return localised ? this.status_localised : this.status;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDownloadDirectory()
/*     */   {
/* 136 */     notSupported();
/*     */     
/* 138 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTargetFileOrDir()
/*     */   {
/* 144 */     notSupported();
/*     */     
/* 146 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getTrackerStatus()
/*     */   {
/* 152 */     notSupported();
/*     */     
/* 154 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCompleted()
/*     */   {
/* 160 */     return this.completed;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadCompleted(boolean bLive)
/*     */   {
/* 166 */     return bLive ? this.downloadCompletedLive : this.downloadCompletedStored;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getCheckingDoneInThousandNotation()
/*     */   {
/* 173 */     notSupported();
/*     */     
/* 175 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetUploadedDownloaded(long l1, long l2)
/*     */   {
/* 183 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 189 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDownloaded(boolean include_protocol)
/*     */   {
/* 196 */     notSupported();
/*     */     
/* 198 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/* 204 */     return this.uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getUploaded(boolean include_protocol)
/*     */   {
/* 211 */     notSupported();
/*     */     
/* 213 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRemaining()
/*     */   {
/* 219 */     notSupported();
/*     */     
/* 221 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDiscarded()
/*     */   {
/* 227 */     notSupported();
/*     */     
/* 229 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloadAverage()
/*     */   {
/* 235 */     return this.download_average;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDownloadAverage(boolean include_protocol)
/*     */   {
/* 242 */     notSupported();
/*     */     
/* 244 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploadAverage()
/*     */   {
/* 250 */     return this.upload_average;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getUploadAverage(boolean include_protocol)
/*     */   {
/* 257 */     notSupported();
/*     */     
/* 259 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalAverage()
/*     */   {
/* 265 */     notSupported();
/*     */     
/* 267 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getElapsedTime()
/*     */   {
/* 273 */     notSupported();
/*     */     
/* 275 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getETA()
/*     */   {
/* 281 */     return this.eta;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getETASecs()
/*     */   {
/* 287 */     notSupported();
/* 288 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getHashFails()
/*     */   {
/* 294 */     notSupported();
/*     */     
/* 296 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getShareRatio()
/*     */   {
/* 302 */     return this.share_ratio;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeStarted()
/*     */   {
/* 308 */     notSupported();
/* 309 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public float getAvailability()
/*     */   {
/* 315 */     return this.availability;
/*     */   }
/*     */   
/*     */   public long getSecondsDownloading() {
/* 319 */     notSupported();
/* 320 */     return 0L;
/*     */   }
/*     */   
/*     */   public long getSecondsOnlySeeding() {
/* 324 */     notSupported();
/* 325 */     return 0L;
/*     */   }
/*     */   
/*     */   public long getTimeStartedSeeding() {
/* 329 */     notSupported();
/* 330 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSecondsSinceLastDownload()
/*     */   {
/* 336 */     notSupported();
/* 337 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSecondsSinceLastUpload()
/*     */   {
/* 343 */     notSupported();
/* 344 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHealth()
/*     */   {
/* 350 */     return this.health;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getBytesUnavailable()
/*     */   {
/* 357 */     return this.bytesUnavailable;
/*     */   }
/*     */   
/*     */   public long getRemainingExcludingDND()
/*     */   {
/* 362 */     notSupported();
/*     */     
/* 364 */     return 0L;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/download/RPDownloadStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */