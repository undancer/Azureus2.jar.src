/*     */ package org.gudy.azureus2.core3.tracker.client.impl.bt;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperImpl;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperResponseImpl;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class TRTrackerBTScraperImpl
/*     */ {
/*     */   protected static TRTrackerBTScraperImpl singleton;
/*  43 */   protected static final AEMonitor class_mon = new AEMonitor("TRTrackerBTScraper");
/*     */   
/*     */   private final TRTrackerScraperImpl scraper;
/*     */   
/*     */   private final TrackerChecker tracker_checker;
/*     */   
/*     */   public static TRTrackerBTScraperImpl create(TRTrackerScraperImpl _scraper)
/*     */   {
/*     */     try
/*     */     {
/*  53 */       class_mon.enter();
/*     */       
/*  55 */       if (singleton == null)
/*     */       {
/*  57 */         singleton = new TRTrackerBTScraperImpl(_scraper);
/*     */       }
/*     */       
/*  60 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  64 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRTrackerBTScraperImpl(TRTrackerScraperImpl _scraper)
/*     */   {
/*  72 */     this.scraper = _scraper;
/*     */     
/*  74 */     this.tracker_checker = new TrackerChecker(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected TRTrackerScraperImpl getScraper()
/*     */   {
/*  80 */     return this.scraper;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScrape(TOTorrent torrent, URL url, DownloadScrapeResult result)
/*     */   {
/*  89 */     if ((torrent != null) && (result != null))
/*     */     {
/*  91 */       TRTrackerScraperResponseImpl resp = this.tracker_checker.getHashData(torrent, url);
/*     */       
/*  93 */       URL result_url = result.getURL();
/*     */       
/*  95 */       boolean update_is_dht = TorrentUtils.isDecentralised(result_url);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 100 */       if ((resp != null) && ((resp.getStatus() == 1) || ((resp.isDHTBackup()) && (update_is_dht))))
/*     */       {
/*     */ 
/*     */ 
/* 104 */         resp.setDHTBackup(update_is_dht);
/*     */         
/* 106 */         resp.setScrapeStartTime(result.getScrapeStartTime());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 111 */         resp.setStatus(result.getResponseType() == 1 ? 2 : 1, result.getStatus() + " (" + (result_url == null ? "<null>" : update_is_dht ? MessageText.getString("dht.backup.only") : result_url.getHost()) + ")");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 118 */         resp.setSeedsPeers(result.getSeedCount(), result.getNonSeedCount());
/*     */         
/* 120 */         this.scraper.scrapeReceived(resp);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TOTorrent torrent, URL target_url, boolean force)
/*     */   {
/* 131 */     if (torrent == null)
/*     */     {
/* 133 */       return null;
/*     */     }
/*     */     
/* 136 */     if (force)
/*     */     {
/* 138 */       this.tracker_checker.syncUpdate(torrent, target_url);
/*     */     }
/*     */     
/* 141 */     TRTrackerScraperResponse res = this.tracker_checker.getHashData(torrent, target_url);
/*     */     
/*     */ 
/*     */ 
/* 145 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse peekScrape(TOTorrent torrent, URL target_url)
/*     */   {
/* 153 */     if (torrent == null)
/*     */     {
/* 155 */       return null;
/*     */     }
/*     */     
/* 158 */     TRTrackerScraperResponse res = this.tracker_checker.peekHashData(torrent, target_url);
/*     */     
/* 160 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TRTrackerAnnouncer tracker_client)
/*     */   {
/* 167 */     TRTrackerScraperResponse res = this.tracker_checker.getHashData(tracker_client);
/*     */     
/*     */ 
/*     */ 
/* 171 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove(TOTorrent torrent)
/*     */   {
/* 178 */     this.tracker_checker.removeHash(torrent);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/bt/TRTrackerBTScraperImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */