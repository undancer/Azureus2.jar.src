/*     */ package org.gudy.azureus2.core3.tracker.server.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerStats;
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
/*     */ 
/*     */ public class TRTrackerServerStatsImpl
/*     */   implements TRTrackerServerStats
/*     */ {
/*     */   private final TRTrackerServerImpl server;
/*     */   private long announces;
/*     */   private long scrapes;
/*     */   private long bytes_in;
/*     */   private long bytes_out;
/*     */   private long announce_time;
/*     */   private long scrape_time;
/*     */   
/*     */   protected TRTrackerServerStatsImpl(TRTrackerServerImpl _server)
/*     */   {
/*  51 */     this.server = _server;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTorrentCount()
/*     */   {
/*  57 */     return this.server.getTorrentCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesIn()
/*     */   {
/*  63 */     return this.bytes_in;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesOut()
/*     */   {
/*  69 */     return this.bytes_out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void update(int request_type, int in, int out)
/*     */   {
/*  78 */     this.bytes_in += in;
/*  79 */     this.bytes_out += out;
/*     */     
/*  81 */     if ((request_type == 1) || (request_type == 4))
/*     */     {
/*  83 */       this.announces += 1L;
/*     */     }
/*     */     else {
/*  86 */       this.scrapes += 1L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateTime(int request_type, long time)
/*     */   {
/*  95 */     if (request_type == 1)
/*     */     {
/*  97 */       this.announce_time += time;
/*     */     }
/*     */     else
/*     */     {
/* 101 */       this.scrape_time += time;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceCount()
/*     */   {
/* 108 */     return this.announces;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeCount()
/*     */   {
/* 114 */     return this.scrapes;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAnnounceTime()
/*     */   {
/* 120 */     return this.announce_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getScrapeTime()
/*     */   {
/* 126 */     return this.scrape_time;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */