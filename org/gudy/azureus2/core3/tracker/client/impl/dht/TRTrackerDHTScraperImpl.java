/*     */ package org.gudy.azureus2.core3.tracker.client.impl.dht;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperClientResolver;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperImpl;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class TRTrackerDHTScraperImpl
/*     */ {
/*     */   protected static TRTrackerDHTScraperImpl singleton;
/*  47 */   protected static final AEMonitor class_mon = new AEMonitor("TRTrackerDHTScraper");
/*     */   
/*     */   private final TRTrackerScraperImpl scraper;
/*     */   
/*  51 */   private final Map<HashWrapper, TRTrackerDHTScraperResponseImpl> responses = new HashMap();
/*     */   
/*     */ 
/*     */   public static TRTrackerDHTScraperImpl create(TRTrackerScraperImpl _scraper)
/*     */   {
/*     */     try
/*     */     {
/*  58 */       class_mon.enter();
/*     */       
/*  60 */       if (singleton == null)
/*     */       {
/*  62 */         singleton = new TRTrackerDHTScraperImpl(_scraper);
/*     */       }
/*     */       
/*  65 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  69 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRTrackerDHTScraperImpl(TRTrackerScraperImpl _scraper)
/*     */   {
/*  77 */     this.scraper = _scraper;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScrape(TOTorrent torrent, URL url, DownloadScrapeResult result)
/*     */   {
/*  86 */     if ((torrent != null) && (result != null)) {
/*     */       try
/*     */       {
/*  89 */         TRTrackerDHTScraperResponseImpl resp = new TRTrackerDHTScraperResponseImpl(torrent.getHashWrapper(), result.getURL());
/*     */         
/*     */ 
/*  92 */         resp.setSeedsPeers(result.getSeedCount(), result.getNonSeedCount());
/*     */         
/*  94 */         resp.setScrapeStartTime(result.getScrapeStartTime());
/*     */         
/*  96 */         resp.setNextScrapeStartTime(result.getNextScrapeStartTime());
/*     */         
/*  98 */         resp.setStatus(result.getResponseType() == 1 ? 2 : 1, result.getStatus());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 104 */         synchronized (this.responses)
/*     */         {
/* 106 */           this.responses.put(torrent.getHashWrapper(), resp);
/*     */         }
/*     */         
/* 109 */         this.scraper.scrapeReceived(resp);
/*     */       }
/*     */       catch (TOTorrentException e)
/*     */       {
/* 113 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TOTorrent torrent, URL unused_target_url, boolean unused_force)
/*     */   {
/* 124 */     if (torrent != null) {
/*     */       try
/*     */       {
/* 127 */         HashWrapper hw = torrent.getHashWrapper();
/*     */         
/*     */         TRTrackerDHTScraperResponseImpl response;
/*     */         
/* 131 */         synchronized (this.responses)
/*     */         {
/* 133 */           response = (TRTrackerDHTScraperResponseImpl)this.responses.get(hw);
/*     */         }
/*     */         
/* 136 */         if (response == null)
/*     */         {
/* 138 */           TRTrackerScraperClientResolver resolver = this.scraper.getClientResolver();
/*     */           
/* 140 */           if (resolver != null)
/*     */           {
/* 142 */             int[] cache = resolver.getCachedScrape(hw);
/*     */             
/* 144 */             if (cache != null)
/*     */             {
/* 146 */               response = new TRTrackerDHTScraperResponseImpl(hw, torrent.getAnnounceURL());
/*     */               
/*     */ 
/*     */ 
/* 150 */               response.setSeedsPeers(cache[0], cache[1]);
/*     */               
/* 152 */               long now = SystemTime.getCurrentTime();
/*     */               
/* 154 */               response.setScrapeStartTime(now);
/*     */               
/* 156 */               response.setNextScrapeStartTime(now + 300000L);
/*     */               
/* 158 */               response.setStatus(2, MessageText.getString("Scrape.status.cached"));
/*     */               
/*     */ 
/*     */ 
/* 162 */               synchronized (this.responses)
/*     */               {
/* 164 */                 this.responses.put(torrent.getHashWrapper(), response);
/*     */               }
/*     */               
/* 167 */               this.scraper.scrapeReceived(response);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 172 */         return response;
/*     */       }
/*     */       catch (TOTorrentException e)
/*     */       {
/* 176 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 180 */     return null;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public TRTrackerScraperResponse peekScrape(TOTorrent torrent, URL unused_target_url)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ifnull +48 -> 49
/*     */     //   4: aload_1
/*     */     //   5: invokeinterface 197 1 0
/*     */     //   10: astore_3
/*     */     //   11: aload_0
/*     */     //   12: getfield 172	org/gudy/azureus2/core3/tracker/client/impl/dht/TRTrackerDHTScraperImpl:responses	Ljava/util/Map;
/*     */     //   15: dup
/*     */     //   16: astore 4
/*     */     //   18: monitorenter
/*     */     //   19: aload_0
/*     */     //   20: getfield 172	org/gudy/azureus2/core3/tracker/client/impl/dht/TRTrackerDHTScraperImpl:responses	Ljava/util/Map;
/*     */     //   23: aload_3
/*     */     //   24: invokeinterface 193 2 0
/*     */     //   29: checkcast 95	org/gudy/azureus2/core3/tracker/client/TRTrackerScraperResponse
/*     */     //   32: aload 4
/*     */     //   34: monitorexit
/*     */     //   35: areturn
/*     */     //   36: astore 5
/*     */     //   38: aload 4
/*     */     //   40: monitorexit
/*     */     //   41: aload 5
/*     */     //   43: athrow
/*     */     //   44: astore_3
/*     */     //   45: aload_3
/*     */     //   46: invokestatic 191	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   49: aconst_null
/*     */     //   50: areturn
/*     */     // Line number table:
/*     */     //   Java source line #188	-> byte code offset #0
/*     */     //   Java source line #191	-> byte code offset #4
/*     */     //   Java source line #193	-> byte code offset #11
/*     */     //   Java source line #195	-> byte code offset #19
/*     */     //   Java source line #196	-> byte code offset #36
/*     */     //   Java source line #197	-> byte code offset #44
/*     */     //   Java source line #199	-> byte code offset #45
/*     */     //   Java source line #203	-> byte code offset #49
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	51	0	this	TRTrackerDHTScraperImpl
/*     */     //   0	51	1	torrent	TOTorrent
/*     */     //   0	51	2	unused_target_url	URL
/*     */     //   10	14	3	hw	HashWrapper
/*     */     //   44	2	3	e	TOTorrentException
/*     */     //   16	23	4	Ljava/lang/Object;	Object
/*     */     //   36	6	5	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   19	35	36	finally
/*     */     //   36	41	36	finally
/*     */     //   4	35	44	org/gudy/azureus2/core3/torrent/TOTorrentException
/*     */     //   36	44	44	org/gudy/azureus2/core3/torrent/TOTorrentException
/*     */   }
/*     */   
/*     */   public TRTrackerScraperResponse scrape(TRTrackerAnnouncer tracker_client)
/*     */   {
/* 210 */     return scrape(tracker_client.getTorrent(), null, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove(TOTorrent torrent)
/*     */   {
/*     */     try
/*     */     {
/* 218 */       synchronized (this.responses)
/*     */       {
/* 220 */         this.responses.remove(torrent.getHashWrapper());
/*     */       }
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 225 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/dht/TRTrackerDHTScraperImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */