/*     */ package org.gudy.azureus2.core3.tracker.client.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public abstract class TRTrackerScraperResponseImpl
/*     */   implements TRTrackerScraperResponse
/*     */ {
/*     */   private final HashWrapper hash;
/*     */   private int seeds;
/*     */   private int peers;
/*     */   private int completed;
/*     */   private long scrapeStartTime;
/*     */   private long nextScrapeStartTime;
/*  44 */   private String sStatus = "";
/*  45 */   private String sLastStatus = "";
/*     */   
/*     */   private int status;
/*     */   
/*     */   private int last_status;
/*     */   
/*     */   private int last_status_set_time;
/*     */   
/*     */   protected TRTrackerScraperResponseImpl(HashWrapper _hash)
/*     */   {
/*  55 */     this(_hash, -1, -1, -1, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TRTrackerScraperResponseImpl(HashWrapper _hash, int _seeds, int _peers, int completed, long _scrapeStartTime)
/*     */   {
/*  66 */     this.last_status_set_time = ((int)(SystemTime.getCurrentTime() / 1000L));
/*     */     
/*  68 */     this.hash = _hash;
/*  69 */     this.seeds = _seeds;
/*  70 */     this.completed = completed;
/*  71 */     this.peers = _peers;
/*     */     
/*  73 */     this.scrapeStartTime = _scrapeStartTime;
/*     */     
/*  75 */     this.status = (!isValid() ? 0 : 2);
/*  76 */     this.nextScrapeStartTime = -1L;
/*     */   }
/*     */   
/*     */   public int getCompleted()
/*     */   {
/*  81 */     return this.completed;
/*     */   }
/*     */   
/*     */   public void setCompleted(int completed) {
/*  85 */     if (completed >= 0)
/*     */     {
/*  87 */       this.completed = completed;
/*     */     }
/*     */   }
/*     */   
/*     */   public HashWrapper getHash() {
/*  92 */     return this.hash;
/*     */   }
/*     */   
/*     */   public int getSeeds()
/*     */   {
/*  97 */     return this.seeds;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSeeds(int s)
/*     */   {
/* 104 */     this.seeds = s;
/*     */   }
/*     */   
/*     */   public int getPeers() {
/* 108 */     return this.peers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPeers(int p)
/*     */   {
/* 115 */     this.peers = p;
/*     */   }
/*     */   
/*     */   public int getStatus()
/*     */   {
/* 120 */     return this.status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setStatus(int s)
/*     */   {
/* 127 */     this.last_status_set_time = ((int)(SystemTime.getCurrentTime() / 1000L));
/*     */     
/* 129 */     this.status = s;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setStatus(String str)
/*     */   {
/* 136 */     this.last_status_set_time = ((int)(SystemTime.getCurrentTime() / 1000L));
/*     */     
/* 138 */     this.sStatus = str;
/*     */   }
/*     */   
/*     */   public void setStatus(int iNewStatus, String sNewStatus) {
/* 142 */     this.last_status_set_time = ((int)(SystemTime.getCurrentTime() / 1000L));
/*     */     
/* 144 */     if ((this.last_status != this.status) && (iNewStatus != this.status))
/* 145 */       this.last_status = this.status;
/* 146 */     if (iNewStatus == 2) {
/* 147 */       this.status = (!isValid() ? 0 : 2);
/*     */     } else {
/* 149 */       this.status = iNewStatus;
/*     */     }
/*     */     
/* 152 */     if (sNewStatus == null) {
/* 153 */       return;
/*     */     }
/* 155 */     if (!this.sLastStatus.equals(this.sStatus)) {
/* 156 */       this.sLastStatus = this.sStatus;
/*     */     }
/* 158 */     this.sStatus = StringInterner.intern(sNewStatus);
/*     */   }
/*     */   
/*     */   public void revertStatus() {
/* 162 */     this.status = this.last_status;
/* 163 */     this.sStatus = this.sLastStatus;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getScrapeTime()
/*     */   {
/* 169 */     return this.last_status_set_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setScrapeStartTime(long time)
/*     */   {
/* 175 */     this.scrapeStartTime = time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getScrapeStartTime()
/*     */   {
/* 182 */     return this.scrapeStartTime;
/*     */   }
/*     */   
/*     */   public long getNextScrapeStartTime() {
/* 186 */     return this.nextScrapeStartTime;
/*     */   }
/*     */   
/*     */   public void setNextScrapeStartTime(long _nextScrapeStartTime) {
/* 190 */     this.nextScrapeStartTime = _nextScrapeStartTime;
/*     */   }
/*     */   
/*     */   public String getStatusString() {
/* 194 */     return this.sStatus;
/*     */   }
/*     */   
/*     */   public boolean isValid() {
/* 198 */     return (this.seeds != -1) || (this.peers != -1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 209 */   private static final int scrapeFuzzAdd = (int)(Math.random() * 3.0D * 60.0D);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void setDHTBackup(boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int calcScrapeIntervalSecs(int iRecIntervalSecs, int iNumSeeds)
/*     */   {
/* 222 */     int MIN = 900;
/* 223 */     int MAX = 10800;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 229 */     int scrapeInterval = 900 + iNumSeeds * 10;
/* 230 */     if (iRecIntervalSecs > scrapeInterval) {
/* 231 */       scrapeInterval = iRecIntervalSecs;
/*     */     }
/*     */     
/* 234 */     scrapeInterval += scrapeFuzzAdd;
/*     */     
/* 236 */     if (scrapeInterval > 10800) {
/* 237 */       scrapeInterval = 10800;
/*     */     }
/* 239 */     return scrapeInterval;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 245 */     return getURL() + ": " + ByteFormatter.encodeString(this.hash.getBytes()) + ",seeds=" + this.seeds + ",peers=" + this.peers + ",state=" + this.status + "/" + this.sStatus + ",last=" + this.last_status + "/" + this.sLastStatus + ",start=" + this.scrapeStartTime + ",next=" + this.nextScrapeStartTime;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerScraperResponseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */