/*     */ package org.gudy.azureus2.core3.tracker.client.impl.bt;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperResponseImpl;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.SystemTime.ChangeListener;
/*     */ import org.gudy.azureus2.core3.util.Timer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TrackerChecker
/*     */   implements AEDiagnosticsEvidenceGenerator, SystemTime.ChangeListener, TimerEventPerformer
/*     */ {
/*  42 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */ 
/*     */ 
/*     */   private final HashMap trackers;
/*     */   
/*     */ 
/*  49 */   private final AEMonitor trackers_mon = new AEMonitor("TrackerChecker:trackers");
/*     */   
/*     */ 
/*     */ 
/*     */   private final TRTrackerBTScraperImpl scraper;
/*     */   
/*     */ 
/*     */   private long nextScrapeCheckOn;
/*     */   
/*     */ 
/*     */   TRTrackerBTScraperResponseImpl oldResponse;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TrackerChecker(TRTrackerBTScraperImpl _scraper)
/*     */   {
/*  65 */     this.scraper = _scraper;
/*     */     
/*  67 */     this.trackers = new HashMap();
/*     */     
/*  69 */     if (!COConfigurationManager.getBooleanParameter("Tracker Client Scrape Total Disable"))
/*     */     {
/*  71 */       runScrapes();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  76 */     AEDiagnostics.addEvidenceGenerator(this);
/*     */     
/*  78 */     SystemTime.registerClockChangeListener(this);
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
/*     */   protected TRTrackerScraperResponseImpl getHashData(TRTrackerAnnouncer tracker_client)
/*     */   {
/*     */     try
/*     */     {
/*  93 */       return getHashData(tracker_client.getTrackerURL(), tracker_client.getTorrent().getHashWrapper());
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/*  97 */       Debug.printStackTrace(e); }
/*  98 */     return null;
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
/*     */   protected TRTrackerScraperResponseImpl getHashData(TOTorrent torrent, URL target_url)
/*     */   {
/*     */     try
/*     */     {
/* 113 */       return getHashData(target_url == null ? torrent.getAnnounceURL() : target_url, torrent.getHashWrapper());
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 117 */       Debug.printStackTrace(e); }
/* 118 */     return null;
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
/*     */ 
/*     */ 
/*     */   protected TRTrackerScraperResponseImpl getHashData(URL trackerUrl, HashWrapper hash)
/*     */   {
/* 133 */     if (trackerUrl == null) {
/* 134 */       return null;
/*     */     }
/*     */     
/* 137 */     TRTrackerScraperResponseImpl data = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 144 */     String url_str = trackerUrl.toString();
/*     */     
/* 146 */     TrackerStatus ts = null;
/*     */     try
/*     */     {
/* 149 */       this.trackers_mon.enter();
/*     */       
/* 151 */       ts = (TrackerStatus)this.trackers.get(url_str);
/*     */       
/* 153 */       if (ts != null)
/*     */       {
/* 155 */         data = ts.getHashData(hash);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 161 */         ts = new TrackerStatus(this, this.scraper.getScraper(), trackerUrl);
/*     */         
/* 163 */         this.trackers.put(url_str, ts);
/*     */         
/* 165 */         if (!ts.isTrackerScrapeUrlValid())
/*     */         {
/* 167 */           if (Logger.isEnabled()) {
/* 168 */             Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, 3, "Can't scrape using url '" + trackerUrl + "' as it doesn't end in " + "'/announce', skipping."));
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 177 */       this.trackers_mon.exit();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 183 */     if (data == null)
/*     */     {
/* 185 */       data = ts.addHash(hash);
/*     */     }
/*     */     
/* 188 */     return data;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRTrackerScraperResponseImpl peekHashData(TOTorrent torrent, URL target_url)
/*     */   {
/*     */     try
/*     */     {
/* 197 */       URL trackerUrl = target_url == null ? torrent.getAnnounceURL() : target_url;
/*     */       
/* 199 */       if (trackerUrl == null) {
/* 200 */         return null;
/*     */       }
/*     */       
/* 203 */       String url_str = trackerUrl.toString();
/*     */       try
/*     */       {
/* 206 */         this.trackers_mon.enter();
/*     */         
/* 208 */         TrackerStatus ts = (TrackerStatus)this.trackers.get(url_str);
/*     */         
/* 210 */         if (ts != null)
/*     */         {
/* 212 */           return ts.getHashData(torrent.getHashWrapper());
/*     */         }
/*     */       }
/*     */       finally {
/* 216 */         this.trackers_mon.exit();
/*     */       }
/*     */     }
/*     */     catch (TOTorrentException e) {
/* 220 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 223 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void removeHash(TOTorrent torrent)
/*     */   {
/*     */     try
/*     */     {
/* 231 */       removeHash(torrent.getAnnounceURL().toString(), torrent.getHashWrapper());
/*     */       
/* 233 */       TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */       
/* 235 */       for (int i = 0; i < sets.length; i++)
/*     */       {
/* 237 */         URL[] urls = sets[i].getAnnounceURLs();
/*     */         
/* 239 */         for (int j = 0; j < urls.length; j++)
/*     */         {
/* 241 */           removeHash(urls[j].toString(), torrent.getHashWrapper());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 247 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeHash(String trackerUrl, HashWrapper hash)
/*     */   {
/* 256 */     TrackerStatus ts = (TrackerStatus)this.trackers.get(trackerUrl);
/* 257 */     if (ts != null)
/*     */     {
/* 259 */       ts.removeHash(hash);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void syncUpdate(TOTorrent torrent, URL target_url)
/*     */   {
/* 270 */     if (torrent == null) {
/* 271 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 275 */       HashWrapper hash = torrent.getHashWrapper();
/*     */       
/* 277 */       TrackerStatus matched_ts = null;
/*     */       try
/*     */       {
/* 280 */         this.trackers_mon.enter();
/*     */         
/* 282 */         Iterator iter = this.trackers.values().iterator();
/*     */         
/* 284 */         while (iter.hasNext())
/*     */         {
/* 286 */           TrackerStatus ts = (TrackerStatus)iter.next();
/*     */           
/* 288 */           if ((target_url == null) || (target_url.toString().equals(ts.getTrackerURL().toString())))
/*     */           {
/*     */ 
/* 291 */             Map hashmap = ts.getHashes();
/*     */             try
/*     */             {
/* 294 */               ts.getHashesMonitor().enter();
/*     */               
/* 296 */               if (hashmap.get(hash) != null)
/*     */               {
/* 298 */                 matched_ts = ts;
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 304 */                 ts.getHashesMonitor().exit(); break; } } finally { ts.getHashesMonitor().exit();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 310 */         this.trackers_mon.exit();
/*     */       }
/*     */       
/* 313 */       if (matched_ts != null)
/*     */       {
/* 315 */         matched_ts.updateSingleHash(hash, true, false);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 319 */       Debug.out("scrape syncUpdate() exception", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void perform(TimerEvent event)
/*     */   {
/* 325 */     runScrapes();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void runScrapes()
/*     */   {
/* 336 */     TRTrackerBTScraperResponseImpl nextResponseScraping = checkForNextScrape();
/*     */     
/* 338 */     if ((Logger.isEnabled()) && (nextResponseScraping != this.oldResponse) && (nextResponseScraping != null)) {
/* 339 */       Logger.log(new LogEvent(TorrentUtils.getDownloadManager(nextResponseScraping.getHash()), LOGID, 0, "Next scrape will be " + nextResponseScraping.getURL() + " in " + (nextResponseScraping.getNextScrapeStartTime() - SystemTime.getCurrentTime()) / 1000L + " sec,type=" + (nextResponseScraping.getTrackerStatus().getSupportsMultipeHashScrapes() ? "multi" : "single") + ",active=" + nextResponseScraping.getTrackerStatus().getNumActiveScrapes()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     long delay;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     long delay;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 356 */     if (nextResponseScraping == null)
/*     */     {
/* 358 */       delay = 60000L;
/*     */     }
/*     */     else
/*     */     {
/* 362 */       long scrape_time = nextResponseScraping.getNextScrapeStartTime();
/*     */       
/* 364 */       long time_to_scrape = scrape_time - SystemTime.getCurrentTime() + 25L;
/*     */       
/*     */ 
/* 367 */       if (time_to_scrape <= 0L) {
/*     */         long delay;
/* 369 */         if (nextResponseScraping.getTrackerStatus().getNumActiveScrapes() > 0)
/*     */         {
/*     */ 
/*     */ 
/* 373 */           delay = 2000L;
/*     */         } else {
/*     */           try
/*     */           {
/* 377 */             nextResponseScraping.getTrackerStatus().updateSingleHash(nextResponseScraping.getHash(), false);
/*     */             
/*     */ 
/* 380 */             delay = 0L;
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 384 */             Debug.printStackTrace(e);
/*     */             
/* 386 */             long delay = 30000L;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 391 */         delay = time_to_scrape;
/*     */         
/* 393 */         if (delay > 30000L) {
/* 394 */           delay = 30000L;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 399 */     this.nextScrapeCheckOn = (SystemTime.getCurrentTime() + delay);
/* 400 */     this.oldResponse = nextResponseScraping;
/*     */     
/* 402 */     TRTrackerBTAnnouncerImpl.tracker_timer.addEvent(this.nextScrapeCheckOn, this);
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
/*     */   private TRTrackerBTScraperResponseImpl checkForNextScrape()
/*     */   {
/* 415 */     long earliestBlocked = Long.MAX_VALUE;
/* 416 */     TRTrackerBTScraperResponseImpl earliestBlockedResponse = null;
/* 417 */     long earliestNonBlocked = Long.MAX_VALUE;
/* 418 */     TRTrackerBTScraperResponseImpl earliestNonBlockedResponse = null;
/*     */     try
/*     */     {
/* 421 */       this.trackers_mon.enter();
/*     */       
/* 423 */       Iterator iter = this.trackers.values().iterator();
/*     */       
/* 425 */       while (iter.hasNext())
/*     */       {
/* 427 */         TrackerStatus ts = (TrackerStatus)iter.next();
/*     */         
/* 429 */         if (ts.isTrackerScrapeUrlValid())
/*     */         {
/*     */ 
/*     */ 
/* 433 */           boolean hasActiveScrapes = ts.getNumActiveScrapes() > 0;
/*     */           
/* 435 */           Map hashmap = ts.getHashes();
/*     */           try
/*     */           {
/* 438 */             ts.getHashesMonitor().enter();
/*     */             
/* 440 */             Iterator iterHashes = hashmap.values().iterator();
/*     */             
/* 442 */             while (iterHashes.hasNext())
/*     */             {
/* 444 */               TRTrackerBTScraperResponseImpl response = (TRTrackerBTScraperResponseImpl)iterHashes.next();
/*     */               
/* 446 */               if (response.getStatus() != 3) {
/* 447 */                 long nextScrapeStartTime = response.getNextScrapeStartTime();
/*     */                 
/* 449 */                 if (hasActiveScrapes) {
/* 450 */                   if (nextScrapeStartTime < earliestBlocked) {
/* 451 */                     earliestBlocked = nextScrapeStartTime;
/* 452 */                     earliestBlockedResponse = response;
/*     */                   }
/*     */                 }
/* 455 */                 else if (nextScrapeStartTime < earliestNonBlocked) {
/* 456 */                   earliestNonBlocked = nextScrapeStartTime;
/* 457 */                   earliestNonBlockedResponse = response;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 464 */             ts.getHashesMonitor().exit();
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/* 469 */       this.trackers_mon.exit();
/*     */     }
/*     */     
/* 472 */     boolean hasEarlierBlockedScrape = (earliestBlocked != Long.MAX_VALUE) && (earliestBlocked < earliestNonBlocked);
/*     */     
/*     */ 
/*     */ 
/* 476 */     if ((hasEarlierBlockedScrape) && (earliestNonBlocked - SystemTime.getCurrentTime() > 2000L))
/*     */     {
/* 478 */       return earliestBlockedResponse;
/*     */     }
/* 480 */     return earliestNonBlockedResponse;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clockChangeDetected(long current_time, long offset)
/*     */   {
/* 490 */     if (Math.abs(offset) < 60000L)
/*     */     {
/* 492 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 496 */       this.trackers_mon.enter();
/*     */       
/* 498 */       Iterator iter = this.trackers.values().iterator();
/*     */       
/* 500 */       while (iter.hasNext())
/*     */       {
/* 502 */         TrackerStatus ts = (TrackerStatus)iter.next();
/*     */         
/* 504 */         Map hashmap = ts.getHashes();
/*     */         try
/*     */         {
/* 507 */           ts.getHashesMonitor().enter();
/*     */           
/* 509 */           Iterator iterHashes = hashmap.values().iterator();
/*     */           
/* 511 */           while (iterHashes.hasNext())
/*     */           {
/* 513 */             TRTrackerBTScraperResponseImpl response = (TRTrackerBTScraperResponseImpl)iterHashes.next();
/*     */             
/* 515 */             long time = response.getNextScrapeStartTime();
/*     */             
/* 517 */             if (time > 0L)
/*     */             {
/* 519 */               response.setNextScrapeStartTime(time + offset);
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/* 524 */           ts.getHashesMonitor().exit();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 529 */       this.trackers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clockChangeCompleted(long current_time, long offset) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 545 */     writer.println("BTScraper - now = " + SystemTime.getCurrentTime());
/*     */     try
/*     */     {
/* 548 */       writer.indent();
/*     */       try
/*     */       {
/* 551 */         this.trackers_mon.enter();
/*     */         
/* 553 */         Iterator iter = this.trackers.entrySet().iterator();
/*     */         
/* 555 */         while (iter.hasNext())
/*     */         {
/* 557 */           Map.Entry entry = (Map.Entry)iter.next();
/*     */           
/* 559 */           TrackerStatus ts = (TrackerStatus)entry.getValue();
/*     */           
/* 561 */           writer.println("Tracker: " + ts.getString());
/*     */           try
/*     */           {
/* 564 */             writer.indent();
/*     */             
/* 566 */             ts.getHashesMonitor().enter();
/*     */             
/* 568 */             Map hashmap = ts.getHashes();
/*     */             
/* 570 */             Iterator iter_hashes = hashmap.entrySet().iterator();
/*     */             
/* 572 */             while (iter_hashes.hasNext())
/*     */             {
/* 574 */               Map.Entry hash_entry = (Map.Entry)iter_hashes.next();
/*     */               
/* 576 */               TRTrackerBTScraperResponseImpl response = (TRTrackerBTScraperResponseImpl)hash_entry.getValue();
/*     */               
/* 578 */               writer.println(response.getString());
/*     */             }
/*     */           }
/*     */           finally {
/* 582 */             ts.getHashesMonitor().exit();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 589 */         this.trackers_mon.exit();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 594 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */   public long getNextScrapeCheckOn() {
/* 599 */     return this.nextScrapeCheckOn;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/bt/TrackerChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */