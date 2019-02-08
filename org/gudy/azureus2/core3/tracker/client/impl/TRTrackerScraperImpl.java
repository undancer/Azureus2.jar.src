/*     */ package org.gudy.azureus2.core3.tracker.client.impl;
/*     */ 
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraper;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperClientResolver;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperListener;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.bt.TRTrackerBTScraperImpl;
/*     */ import org.gudy.azureus2.core3.tracker.client.impl.dht.TRTrackerDHTScraperImpl;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.ListenerManager;
/*     */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
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
/*     */ public class TRTrackerScraperImpl
/*     */   implements TRTrackerScraper
/*     */ {
/*     */   private static TRTrackerScraperImpl singleton;
/*  44 */   private static final AEMonitor class_mon = new AEMonitor("TRTrackerScraper");
/*     */   
/*     */ 
/*     */   private final TRTrackerBTScraperImpl bt_scraper;
/*     */   
/*     */   private final TRTrackerDHTScraperImpl dht_scraper;
/*     */   
/*     */   private TRTrackerScraperClientResolver client_resolver;
/*     */   
/*     */   private static final int LDT_SCRAPE_RECEIVED = 1;
/*     */   
/*  55 */   private final ListenerManager listeners = ListenerManager.createManager("TrackerScraper:ListenDispatcher", new ListenerManagerDispatcher()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public void dispatch(Object _listener, int type, Object value)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  65 */       TRTrackerScraperListener listener = (TRTrackerScraperListener)_listener;
/*     */       
/*  67 */       listener.scrapeReceived((TRTrackerScraperResponse)value);
/*     */     }
/*  55 */   });
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
/*     */   public static TRTrackerScraperImpl create()
/*     */   {
/*     */     try
/*     */     {
/*  75 */       class_mon.enter();
/*     */       
/*  77 */       if (singleton == null)
/*     */       {
/*  79 */         singleton = new TRTrackerScraperImpl();
/*     */       }
/*     */       
/*  82 */       return singleton;
/*     */     }
/*     */     finally {
/*  85 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected TRTrackerScraperImpl()
/*     */   {
/*  92 */     this.bt_scraper = TRTrackerBTScraperImpl.create(this);
/*     */     
/*  94 */     this.dht_scraper = TRTrackerDHTScraperImpl.create(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TOTorrent torrent)
/*     */   {
/* 101 */     return scrape(torrent, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TOTorrent torrent, URL target_url)
/*     */   {
/* 109 */     return scrape(torrent, target_url, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TOTorrent torrent, boolean force)
/*     */   {
/* 117 */     return scrape(torrent, null, force);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setScrape(TOTorrent torrent, URL target_url, DownloadScrapeResult result)
/*     */   {
/* 126 */     if (torrent != null)
/*     */     {
/* 128 */       if (((target_url == null) && (TorrentUtils.isDecentralised(torrent))) || (TorrentUtils.isDecentralised(target_url)))
/*     */       {
/*     */ 
/* 131 */         this.dht_scraper.setScrape(torrent, target_url, result);
/*     */       }
/*     */       else
/*     */       {
/* 135 */         this.bt_scraper.setScrape(torrent, target_url, result);
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
/* 146 */     if (torrent == null)
/*     */     {
/* 148 */       return null;
/*     */     }
/*     */     
/* 151 */     if (((target_url == null) && (TorrentUtils.isDecentralised(torrent))) || (TorrentUtils.isDecentralised(target_url)))
/*     */     {
/*     */ 
/* 154 */       return this.dht_scraper.scrape(torrent, target_url, force);
/*     */     }
/*     */     
/*     */ 
/* 158 */     return this.bt_scraper.scrape(torrent, target_url, force);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse peekScrape(TOTorrent torrent, URL target_url)
/*     */   {
/* 167 */     if (torrent == null)
/*     */     {
/* 169 */       return null;
/*     */     }
/*     */     
/* 172 */     if (((target_url == null) && (TorrentUtils.isDecentralised(torrent))) || (TorrentUtils.isDecentralised(target_url)))
/*     */     {
/*     */ 
/* 175 */       return this.dht_scraper.peekScrape(torrent, target_url);
/*     */     }
/*     */     
/*     */ 
/* 179 */     return this.bt_scraper.peekScrape(torrent, target_url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TRTrackerScraperResponse scrape(TRTrackerAnnouncer tracker_client)
/*     */   {
/* 187 */     TOTorrent torrent = tracker_client.getTorrent();
/*     */     
/* 189 */     if (TorrentUtils.isDecentralised(torrent))
/*     */     {
/* 191 */       return this.dht_scraper.scrape(tracker_client);
/*     */     }
/*     */     
/*     */ 
/* 195 */     return this.bt_scraper.scrape(tracker_client);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void remove(TOTorrent torrent)
/*     */   {
/* 203 */     if (TorrentUtils.isDecentralised(torrent))
/*     */     {
/* 205 */       this.dht_scraper.remove(torrent);
/*     */     }
/*     */     else
/*     */     {
/* 209 */       this.bt_scraper.remove(torrent);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void scrapeReceived(TRTrackerScraperResponse response)
/*     */   {
/* 217 */     this.listeners.dispatch(1, response);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setClientResolver(TRTrackerScraperClientResolver resolver)
/*     */   {
/* 224 */     this.client_resolver = resolver;
/*     */   }
/*     */   
/*     */ 
/*     */   public TRTrackerScraperClientResolver getClientResolver()
/*     */   {
/* 230 */     return this.client_resolver;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isTorrentScrapable(HashWrapper hash)
/*     */   {
/* 237 */     if (this.client_resolver == null)
/*     */     {
/* 239 */       return false;
/*     */     }
/*     */     
/* 242 */     return this.client_resolver.isScrapable(hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNetworkEnabled(HashWrapper hash, URL url)
/*     */   {
/* 250 */     if (this.client_resolver == null)
/*     */     {
/* 252 */       return false;
/*     */     }
/*     */     
/* 255 */     return this.client_resolver.isNetworkEnabled(hash, url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getEnabledNetworks(HashWrapper hash)
/*     */   {
/* 262 */     if (this.client_resolver == null)
/*     */     {
/* 264 */       return null;
/*     */     }
/*     */     
/* 267 */     return this.client_resolver.getEnabledNetworks(hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object[] getExtensions(HashWrapper hash)
/*     */   {
/* 274 */     if (this.client_resolver == null)
/*     */     {
/* 276 */       return null;
/*     */     }
/*     */     
/* 279 */     return this.client_resolver.getExtensions(hash);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean redirectTrackerUrl(HashWrapper hash, URL old_url, URL new_url)
/*     */   {
/* 288 */     return this.client_resolver.redirectTrackerUrl(hash, old_url, new_url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TRTrackerScraperListener l)
/*     */   {
/* 295 */     this.listeners.addListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(TRTrackerScraperListener l)
/*     */   {
/* 302 */     this.listeners.removeListener(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerScraperImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */