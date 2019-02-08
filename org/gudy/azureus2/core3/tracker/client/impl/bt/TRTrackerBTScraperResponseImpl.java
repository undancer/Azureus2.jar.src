/*     */ package org.gudy.azureus2.core3.tracker.client.impl.bt;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperResponseImpl;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
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
/*     */ public class TRTrackerBTScraperResponseImpl
/*     */   extends TRTrackerScraperResponseImpl
/*     */ {
/*     */   private final TrackerStatus ts;
/*     */   private boolean is_dht_backup;
/*     */   
/*     */   protected TRTrackerBTScraperResponseImpl(TrackerStatus _ts, HashWrapper _hash)
/*     */   {
/*  47 */     this(_ts, _hash, -1, -1, -1, -1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TRTrackerBTScraperResponseImpl(TrackerStatus _ts, HashWrapper _hash, int _seeds, int _peers, int completed, long _scrapeStartTime)
/*     */   {
/*  59 */     super(_hash, _seeds, _peers, completed, _scrapeStartTime);
/*     */     
/*  61 */     this.ts = _ts;
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerStatus getTrackerStatus()
/*     */   {
/*  67 */     return this.ts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSeedsPeers(int iSeeds, int iPeers)
/*     */   {
/*  74 */     setSeeds(iSeeds);
/*  75 */     setPeers(iPeers);
/*     */     
/*  77 */     if (isValid()) {
/*  78 */       setStatus(2);
/*  79 */       setStatus(MessageText.getString("Scrape.status.ok"));
/*     */     } else {
/*  81 */       setStatus(0);
/*     */     }
/*     */     
/*  84 */     this.ts.scrapeReceived(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/*  90 */     return this.ts.getTrackerURL();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDHTBackup(boolean is_backup)
/*     */   {
/*  97 */     this.is_dht_backup = is_backup;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDHTBackup()
/*     */   {
/* 103 */     return this.is_dht_backup;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/bt/TRTrackerBTScraperResponseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */