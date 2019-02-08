/*     */ package org.gudy.azureus2.pluginsimpl.local.download;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
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
/*     */ 
/*     */ public class DownloadScrapeResultImpl
/*     */   implements DownloadScrapeResult
/*     */ {
/*     */   protected DownloadImpl download;
/*     */   protected TRTrackerScraperResponse response;
/*     */   
/*     */   protected DownloadScrapeResultImpl(DownloadImpl _download, TRTrackerScraperResponse _response)
/*     */   {
/*  49 */     this.download = _download;
/*  50 */     this.response = _response;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setContent(TRTrackerScraperResponse _response)
/*     */   {
/*  57 */     this.response = _response;
/*     */   }
/*     */   
/*     */ 
/*     */   public Download getDownload()
/*     */   {
/*  63 */     return this.download;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseType()
/*     */   {
/*  69 */     if ((this.response != null) && (this.response.isValid()))
/*     */     {
/*  71 */       return 1;
/*     */     }
/*     */     
/*     */ 
/*  75 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/*  82 */     return this.response == null ? -1 : this.response.getSeeds();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNonSeedCount()
/*     */   {
/*  88 */     return this.response == null ? -1 : this.response.getPeers();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeStartTime()
/*     */   {
/*  94 */     return this.response == null ? -1L : this.response.getScrapeStartTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNextScrapeStartTime(long nextScrapeStartTime)
/*     */   {
/* 101 */     TRTrackerScraperResponse current_response = getCurrentResponse();
/*     */     
/* 103 */     if (current_response != null) {
/* 104 */       current_response.setNextScrapeStartTime(nextScrapeStartTime);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getNextScrapeStartTime()
/*     */   {
/* 114 */     TRTrackerScraperResponse current_response = getCurrentResponse();
/*     */     
/* 116 */     return current_response == null ? -1L : current_response.getNextScrapeStartTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus()
/*     */   {
/* 122 */     if (this.response != null) {
/* 123 */       return this.response.getStatusString();
/*     */     }
/*     */     
/* 126 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 132 */     if (this.response != null) {
/* 133 */       return this.response.getURL();
/*     */     }
/* 135 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected TRTrackerScraperResponse getCurrentResponse()
/*     */   {
/* 141 */     TRTrackerScraperResponse current = this.download.getDownload().getTrackerScrapeResponse();
/*     */     
/* 143 */     if (current == null)
/*     */     {
/* 145 */       current = this.response;
/*     */     }
/*     */     
/* 148 */     return current;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadScrapeResultImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */