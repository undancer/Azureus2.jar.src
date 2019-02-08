/*     */ package org.gudy.azureus2.core3.tracker.server.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrentStats;
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
/*     */ public class TRTrackerServerTorrentStatsImpl
/*     */   implements TRTrackerServerTorrentStats
/*     */ {
/*     */   private final TRTrackerServerTorrentImpl torrent;
/*     */   private long announce_count;
/*     */   private long scrape_count;
/*     */   private long completed_count;
/*     */   private long uploaded;
/*     */   private long downloaded;
/*     */   private long left;
/*     */   private long biased_uploaded;
/*     */   private long biased_downloaded;
/*     */   private long bytes_in;
/*     */   private long bytes_out;
/*     */   
/*     */   protected TRTrackerServerTorrentStatsImpl(TRTrackerServerTorrentImpl _torrent)
/*     */   {
/*  54 */     this.torrent = _torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addAnnounce(long ul_diff, long dl_diff, long le_diff, boolean biased_peer)
/*     */   {
/*  64 */     this.announce_count += 1L;
/*     */     
/*  66 */     this.uploaded += (ul_diff < 0L ? 0L : ul_diff);
/*  67 */     this.downloaded += (dl_diff < 0L ? 0L : dl_diff);
/*  68 */     this.left += le_diff;
/*     */     
/*  70 */     if (this.left < 0L)
/*     */     {
/*  72 */       this.left = 0L;
/*     */     }
/*     */     
/*  75 */     if (biased_peer)
/*     */     {
/*  77 */       this.biased_uploaded += (ul_diff < 0L ? 0L : ul_diff);
/*  78 */       this.biased_downloaded += (dl_diff < 0L ? 0L : dl_diff);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeLeft(long _left)
/*     */   {
/*  86 */     this.left -= _left;
/*     */     
/*  88 */     if (this.left < 0L)
/*     */     {
/*  90 */       this.left = 0L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceCount()
/*     */   {
/*  97 */     return this.announce_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addScrape()
/*     */   {
/* 103 */     this.scrape_count += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeCount()
/*     */   {
/* 109 */     return this.scrape_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addCompleted()
/*     */   {
/* 115 */     this.completed_count += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCompletedCount()
/*     */   {
/* 121 */     return this.completed_count;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/* 127 */     return this.uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 133 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBiasedUploaded()
/*     */   {
/* 139 */     return this.biased_uploaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBiasedDownloaded()
/*     */   {
/* 145 */     return this.biased_downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAmountLeft()
/*     */   {
/* 151 */     return this.left;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addXferStats(int in, int out)
/*     */   {
/* 159 */     this.bytes_in += in;
/* 160 */     this.bytes_out += out;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesIn()
/*     */   {
/* 166 */     return this.bytes_in;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesOut()
/*     */   {
/* 172 */     return this.bytes_out;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 178 */     return this.torrent.getSeedCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeecherCount()
/*     */   {
/* 184 */     return this.torrent.getLeecherCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getQueuedCount()
/*     */   {
/* 190 */     return this.torrent.getQueuedCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getBadNATPeerCount()
/*     */   {
/* 196 */     return this.torrent.getBadNATPeerCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 202 */     return "an=" + this.announce_count + ",sc=" + this.scrape_count + ",co=" + this.completed_count + ",le=" + getLeecherCount() + ",se=" + getSeedCount() + ",q=" + getQueuedCount() + ",bi=" + this.bytes_in + ",bo=" + this.bytes_out;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerTorrentStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */