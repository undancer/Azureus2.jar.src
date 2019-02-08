/*      */ package org.gudy.azureus2.core3.tracker.client.impl.bt;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.UnknownHostException;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerFactory;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Proxy;
/*      */ import java.net.SocketException;
/*      */ import java.net.SocketTimeoutException;
/*      */ import java.net.URL;
/*      */ import java.net.URLEncoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperImpl;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.TRTrackerScraperResponseImpl;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyConnect;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyError;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyScrape;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyScrape2;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestConnect;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestScrape;
/*      */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPTrackerCodecs;
/*      */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.BEncodingException;
/*      */ import org.gudy.azureus2.core3.util.ByteEncodedKeyHashMap;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.StringInterner;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.ThreadPool;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*      */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
/*      */ 
/*      */ public class TrackerStatus
/*      */ {
/*   75 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*      */   
/*      */   private static final String SS = "Scrape.status.";
/*      */   
/*      */   private static final String SSErr = "Scrape.status.error.";
/*      */   
/*      */   private static final int FAULTY_SCRAPE_RETRY_INTERVAL = 600000;
/*      */   
/*      */   private static final int NOHASH_RETRY_INTERVAL = 10800000;
/*      */   
/*      */   private static final int GROUP_SCRAPES_MS = 900000;
/*      */   
/*      */   private static final int GROUP_SCRAPES_LIMIT = 20;
/*      */   
/*      */   private static boolean tcpScrapeEnabled;
/*      */   
/*      */   private static boolean udpScrapeEnabled;
/*      */   private static boolean udpProbeEnabled;
/*      */   
/*      */   static
/*      */   {
/*   96 */     PRUDPTrackerCodecs.registerCodecs();
/*      */     
/*   98 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Tracker Client Enable TCP", "Server Enable UDP", "Tracker UDP Probe Enable" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  106 */         TrackerStatus.access$002(COConfigurationManager.getBooleanParameter("Tracker Client Enable TCP"));
/*  107 */         TrackerStatus.access$102(COConfigurationManager.getBooleanParameter("Server Enable UDP"));
/*  108 */         TrackerStatus.access$202(COConfigurationManager.getBooleanParameter("Tracker UDP Probe Enable"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*  113 */   private byte autoUDPscrapeEvery = 1;
/*      */   
/*      */   private int scrapeCount;
/*  116 */   private static final List logged_invalid_urls = new ArrayList();
/*  117 */   private static final ThreadPool thread_pool = new ThreadPool("TrackerStatus", 10, true);
/*      */   
/*      */   private final URL tracker_url;
/*      */   
/*      */   private boolean az_tracker;
/*      */   private boolean enable_sni_hack;
/*      */   private boolean internal_error_hack;
/*      */   private boolean dh_hack;
/*  125 */   private String scrapeURL = null;
/*      */   
/*      */ 
/*      */   private final HashMap<HashWrapper, TRTrackerScraperResponseImpl> hashes;
/*      */   
/*      */   private final TRTrackerScraperImpl scraper;
/*      */   
/*  132 */   private boolean bSingleHashScrapes = false;
/*      */   
/*  134 */   protected final AEMonitor hashes_mon = new AEMonitor("TrackerStatus:hashes");
/*      */   
/*      */   private final TrackerChecker checker;
/*  137 */   private final AtomicInteger numActiveScrapes = new AtomicInteger(0);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TrackerStatus(TrackerChecker _checker, TRTrackerScraperImpl _scraper, URL _tracker_url)
/*      */   {
/*  145 */     this.checker = _checker;
/*  146 */     this.scraper = _scraper;
/*  147 */     this.tracker_url = _tracker_url;
/*      */     
/*  149 */     this.az_tracker = TRTrackerUtils.isAZTracker(this.tracker_url);
/*      */     
/*  151 */     this.bSingleHashScrapes = COConfigurationManager.getBooleanParameter("Tracker Client Scrape Single Only");
/*      */     
/*  153 */     String trackerUrl = this.tracker_url.toString();
/*      */     
/*  155 */     this.hashes = new HashMap();
/*      */     try
/*      */     {
/*  158 */       trackerUrl = trackerUrl.replaceAll(" ", "");
/*  159 */       String lc_trackerUrl = trackerUrl.toLowerCase(Locale.US);
/*      */       
/*  161 */       int position = trackerUrl.lastIndexOf('/');
/*  162 */       if ((position >= 0) && (trackerUrl.length() >= position + 9) && (trackerUrl.substring(position + 1, position + 9).equals("announce")))
/*      */       {
/*      */ 
/*      */ 
/*  166 */         this.scrapeURL = (trackerUrl.substring(0, position + 1) + "scrape" + trackerUrl.substring(position + 9));
/*      */ 
/*      */       }
/*  169 */       else if (lc_trackerUrl.startsWith("udp:"))
/*      */       {
/*      */ 
/*  172 */         this.scrapeURL = trackerUrl;
/*  173 */       } else if ((lc_trackerUrl.startsWith("ws:")) || (lc_trackerUrl.startsWith("wss:")))
/*      */       {
/*      */ 
/*      */ 
/*  177 */         this.scrapeURL = trackerUrl;
/*      */         
/*  179 */         this.bSingleHashScrapes = true;
/*      */       }
/*  181 */       else if ((position >= 0) && (trackerUrl.lastIndexOf('.') < position))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  186 */         this.scrapeURL = (trackerUrl + (trackerUrl.endsWith("/") ? "" : "/") + "scrape");
/*      */ 
/*      */       }
/*  189 */       else if (!logged_invalid_urls.contains(trackerUrl))
/*      */       {
/*  191 */         logged_invalid_urls.add(trackerUrl);
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  196 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isTrackerScrapeUrlValid()
/*      */   {
/*  204 */     return this.scrapeURL != null;
/*      */   }
/*      */   
/*      */   protected TRTrackerScraperResponseImpl getHashData(HashWrapper hash)
/*      */   {
/*      */     try
/*      */     {
/*  211 */       this.hashes_mon.enter();
/*      */       
/*  213 */       return (TRTrackerScraperResponseImpl)this.hashes.get(hash);
/*      */     }
/*      */     finally {
/*  216 */       this.hashes_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateSingleHash(HashWrapper hash, boolean force)
/*      */   {
/*  228 */     updateSingleHash(hash, force, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateSingleHash(HashWrapper hash, boolean force, boolean async)
/*      */   {
/*  239 */     if (this.scrapeURL == null) {
/*  240 */       if (Logger.isEnabled()) {
/*  241 */         Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, "TrackerStatus: " + this.scrapeURL + ": scrape cancelled.. url null"));
/*      */       }
/*      */       
/*      */ 
/*  245 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  249 */       ArrayList<TRTrackerScraperResponseImpl> responsesToUpdate = new ArrayList();
/*      */       
/*      */       TRTrackerScraperResponseImpl response;
/*      */       try
/*      */       {
/*  254 */         this.hashes_mon.enter();
/*      */         
/*  256 */         response = (TRTrackerScraperResponseImpl)this.hashes.get(hash);
/*      */       }
/*      */       finally
/*      */       {
/*  260 */         this.hashes_mon.exit();
/*      */       }
/*      */       
/*  263 */       if (response == null)
/*      */       {
/*  265 */         response = addHash(hash);
/*      */       }
/*      */       
/*  268 */       long lMainNextScrapeStartTime = response.getNextScrapeStartTime();
/*      */       
/*  270 */       if ((!force) && (lMainNextScrapeStartTime > SystemTime.getCurrentTime())) {
/*  271 */         if (Logger.isEnabled()) {
/*  272 */           Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, "TrackerStatus: " + this.scrapeURL + ": scrape cancelled.. not forced and still " + (lMainNextScrapeStartTime - SystemTime.getCurrentTime()) + "ms"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  277 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  283 */       response.setStatus(3, MessageText.getString("Scrape.status.scraping.queued"));
/*      */       
/*  285 */       if (Logger.isEnabled()) {
/*  286 */         Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, "TrackerStatus: " + this.scrapeURL + ": setting to scraping"));
/*      */       }
/*      */       
/*      */ 
/*  290 */       responsesToUpdate.add(response);
/*      */       
/*      */ 
/*      */ 
/*  294 */       if (!this.bSingleHashScrapes) {
/*      */         try
/*      */         {
/*  297 */           this.hashes_mon.enter();
/*      */           
/*  299 */           Iterator iterHashes = this.hashes.values().iterator();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  305 */           while ((iterHashes.hasNext()) && (responsesToUpdate.size() < 20))
/*      */           {
/*  307 */             TRTrackerScraperResponseImpl r = (TRTrackerScraperResponseImpl)iterHashes.next();
/*      */             
/*  309 */             if (!r.getHash().equals(hash))
/*      */             {
/*  311 */               long lTimeDiff = Math.abs(lMainNextScrapeStartTime - r.getNextScrapeStartTime());
/*      */               
/*  313 */               if ((lTimeDiff <= 900000L) && (r.getStatus() != 3))
/*      */               {
/*  315 */                 r.setStatus(3, MessageText.getString("Scrape.status.scraping.queued"));
/*      */                 
/*  317 */                 if (Logger.isEnabled()) {
/*  318 */                   Logger.log(new LogEvent(TorrentUtils.getDownloadManager(r.getHash()), LOGID, "TrackerStatus:" + this.scrapeURL + ": setting to scraping via group scrape"));
/*      */                 }
/*      */                 
/*      */ 
/*  322 */                 responsesToUpdate.add(r);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  328 */           this.hashes_mon.exit();
/*      */         }
/*      */       }
/*      */       
/*  332 */       runScrapes(responsesToUpdate, force, async);
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  336 */       Debug.out("updateSingleHash() exception", t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void runScrapes(final ArrayList<TRTrackerScraperResponseImpl> responses, final boolean force, boolean async)
/*      */   {
/*  346 */     this.numActiveScrapes.incrementAndGet();
/*      */     
/*  348 */     if (async)
/*      */     {
/*  350 */       thread_pool.run(new org.gudy.azureus2.core3.util.AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*  356 */           TrackerStatus.this.runScrapesSupport(responses, force);
/*      */         }
/*      */       });
/*      */       
/*  360 */       if (Logger.isEnabled()) {
/*  361 */         Logger.log(new LogEvent(LOGID, "TrackerStatus: queuing '" + this.scrapeURL + "', for " + responses.size() + " of " + this.hashes.size() + " hashes" + ", single_hash_scrapes: " + (this.bSingleHashScrapes ? "Y" : "N") + ", queue size=" + thread_pool.getQueueSize()));
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  368 */       runScrapesSupport(responses, force);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void runScrapesSupport(ArrayList<TRTrackerScraperResponseImpl> allResponses, boolean force)
/*      */   {
/*      */     try
/*      */     {
/*  379 */       if (Logger.isEnabled()) {
/*  380 */         Logger.log(new LogEvent(LOGID, "TrackerStatus: scraping '" + this.scrapeURL + "', for " + allResponses.size() + " of " + this.hashes.size() + " hashes" + ", single_hash_scrapes: " + (this.bSingleHashScrapes ? "Y" : "N")));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  385 */       boolean original_bSingleHashScrapes = this.bSingleHashScrapes;
/*      */       
/*  387 */       boolean disable_all_scrapes = !COConfigurationManager.getBooleanParameter("Tracker Client Scrape Enable");
/*      */       
/*  389 */       byte[] scrape_reply = null;
/*      */       
/*  391 */       List<HashWrapper> hashesInQuery = new ArrayList(allResponses.size());
/*  392 */       List<TRTrackerScraperResponseImpl> responsesInQuery = new ArrayList(allResponses.size());
/*      */       
/*  394 */       List<HashWrapper> hashesForUDP = new ArrayList();
/*  395 */       List<TRTrackerScraperResponseImpl> responsesForUDP = new ArrayList();
/*      */       
/*  397 */       List<TRTrackerScraperResponseImpl> activeResponses = responsesInQuery;
/*      */       
/*      */       String msg;
/*      */       Iterator i$;
/*      */       try
/*      */       {
/*  403 */         HashWrapper one_of_the_hashes = null;
/*      */         
/*      */ 
/*  406 */         char first_separator = this.scrapeURL.indexOf('?') == -1 ? '?' : '&';
/*      */         
/*  408 */         String info_hash = "";
/*      */         
/*  410 */         String flags = "";
/*      */         
/*  412 */         for (TRTrackerScraperResponseImpl response : allResponses)
/*      */         {
/*  414 */           HashWrapper hash = response.getHash();
/*      */           
/*  416 */           if (Logger.isEnabled()) {
/*  417 */             Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, "TrackerStatus: " + this.scrapeURL + ": scraping, single_hash_scrapes = " + this.bSingleHashScrapes));
/*      */           }
/*      */           
/*      */ 
/*  421 */           if (!this.scraper.isNetworkEnabled(hash, this.tracker_url))
/*      */           {
/*  423 */             response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */             
/*      */ 
/*  426 */             response.setStatus(1, MessageText.getString("Scrape.status.networkdisabled"));
/*      */             
/*      */ 
/*  429 */             this.scraper.scrapeReceived(response);
/*      */           }
/*  431 */           else if ((!force) && ((disable_all_scrapes) || (!this.scraper.isTorrentScrapable(hash))))
/*      */           {
/*      */ 
/*      */ 
/*  435 */             response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */             
/*      */ 
/*  438 */             response.setStatus(1, MessageText.getString("Scrape.status.disabled"));
/*      */             
/*      */ 
/*  441 */             this.scraper.scrapeReceived(response);
/*      */           }
/*      */           else
/*      */           {
/*  445 */             hashesInQuery.add(hash);
/*  446 */             responsesInQuery.add(response);
/*      */             
/*  448 */             response.setStatus(3, MessageText.getString("Scrape.status.scraping"));
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  453 */             this.scraper.scrapeReceived(response);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  459 */             info_hash = info_hash + (one_of_the_hashes != null ? '&' : first_separator) + "info_hash=";
/*      */             
/*      */ 
/*  462 */             info_hash = info_hash + URLEncoder.encode(new String(hash.getBytes(), "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*      */             
/*      */ 
/*      */ 
/*  466 */             Object[] extensions = this.scraper.getExtensions(hash);
/*      */             
/*  468 */             if (extensions != null)
/*      */             {
/*  470 */               if (extensions[0] != null)
/*      */               {
/*  472 */                 info_hash = info_hash + (String)extensions[0];
/*      */               }
/*      */               
/*  475 */               flags = flags + (Character)extensions[1];
/*      */             }
/*      */             else
/*      */             {
/*  479 */               flags = flags + org.gudy.azureus2.core3.tracker.client.TRTrackerScraperClientResolver.FL_NONE;
/*      */             }
/*      */             
/*      */ 
/*  483 */             one_of_the_hashes = hash;
/*      */             
/*      */ 
/*      */ 
/*  487 */             if (hashesForUDP.size() < 70)
/*      */             {
/*  489 */               hashesForUDP.add(hash);
/*  490 */               responsesForUDP.add(response);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  495 */         if (one_of_the_hashes == null) {
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/*  500 */         String request = this.scrapeURL + info_hash;
/*      */         
/*  502 */         if (this.az_tracker)
/*      */         {
/*  504 */           String port_details = TRTrackerUtils.getPortsForURL();
/*      */           
/*  506 */           request = request + port_details;
/*      */           
/*  508 */           request = request + "&azsf=" + flags + "&azver=" + 3;
/*      */         }
/*      */         
/*  511 */         reqUrl = new URL(request);
/*      */         
/*  513 */         if (Logger.isEnabled()) {
/*  514 */           Logger.log(new LogEvent(LOGID, "Accessing scrape interface using url : " + reqUrl));
/*      */         }
/*      */         
/*  517 */         ByteArrayOutputStream message = new ByteArrayOutputStream();
/*      */         
/*  519 */         scrapeStartTime = SystemTime.getCurrentTime();
/*      */         
/*  521 */         redirect_url = null;
/*      */         
/*  523 */         String protocol = reqUrl.getProtocol();
/*      */         
/*  525 */         URL udpScrapeURL = null;
/*      */         
/*  527 */         boolean auto_probe = false;
/*      */         
/*  529 */         if (protocol.equalsIgnoreCase("udp"))
/*      */         {
/*  531 */           if (udpScrapeEnabled)
/*      */           {
/*  533 */             udpScrapeURL = reqUrl;
/*      */           }
/*      */           else
/*      */           {
/*  537 */             throw new IOException("UDP Tracker protocol disabled");
/*      */           }
/*      */         }
/*  540 */         else if ((protocol.equalsIgnoreCase("http")) && (!this.az_tracker) && (this.scrapeCount % this.autoUDPscrapeEvery == 0) && (udpProbeEnabled) && (udpScrapeEnabled))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  545 */           String tracker_network = AENetworkClassifier.categoriseAddress(reqUrl.getHost());
/*      */           
/*  547 */           if (tracker_network == "Public")
/*      */           {
/*  549 */             udpScrapeURL = new URL(reqUrl.toString().replaceFirst("^http", "udp"));
/*      */             
/*  551 */             auto_probe = true;
/*      */           }
/*      */         }
/*      */         
/*  555 */         if (udpScrapeURL == null)
/*      */         {
/*  557 */           if ((!this.az_tracker) && (!tcpScrapeEnabled))
/*      */           {
/*  559 */             String tracker_network = AENetworkClassifier.categoriseAddress(reqUrl.getHost());
/*      */             
/*  561 */             if (tracker_network == "Public")
/*      */             {
/*  563 */               throw new IOException("HTTP Tracker protocol disabled");
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/*  571 */           TorrentUtils.setTLSTorrentHash(one_of_the_hashes);
/*      */           
/*  573 */           if (udpScrapeURL != null)
/*      */           {
/*  575 */             activeResponses = responsesForUDP;
/*      */             
/*  577 */             boolean success = scrapeUDP(reqUrl, message, hashesForUDP, !auto_probe);
/*      */             
/*  579 */             if (((!success) || (message.size() == 0)) && (!protocol.equalsIgnoreCase("udp")))
/*      */             {
/*  581 */               udpScrapeURL = null;
/*  582 */               message.reset();
/*  583 */               if (this.autoUDPscrapeEvery < 16)
/*  584 */                 this.autoUDPscrapeEvery = ((byte)(this.autoUDPscrapeEvery << 1));
/*  585 */               if (Logger.isEnabled())
/*  586 */                 Logger.log(new LogEvent(LOGID, 0, "redirection of http scrape [" + this.scrapeURL + "] to udp failed, will retry in " + this.autoUDPscrapeEvery + " scrapes"));
/*  587 */             } else if ((success) && (!protocol.equalsIgnoreCase("udp")))
/*      */             {
/*  589 */               if (Logger.isEnabled())
/*  590 */                 Logger.log(new LogEvent(LOGID, 0, "redirection of http scrape [" + this.scrapeURL + "] to udp successful"));
/*  591 */               this.autoUDPscrapeEvery = 1;
/*  592 */               TRTrackerUtils.setUDPProbeResult(reqUrl, true);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  597 */           this.scrapeCount += 1;
/*      */           
/*  599 */           if (udpScrapeURL == null)
/*      */           {
/*  601 */             activeResponses = responsesInQuery;
/*      */             
/*  603 */             redirect_url = scrapeHTTP(hashesInQuery, reqUrl, message);
/*      */           }
/*      */         }
/*      */         finally {
/*  607 */           TorrentUtils.setTLSTorrentHash(null);
/*      */         }
/*      */         
/*  610 */         scrape_reply = message.toByteArray();
/*      */         
/*  612 */         Map map = org.gudy.azureus2.core3.util.BDecoder.decode(scrape_reply);
/*      */         
/*  614 */         boolean this_is_az_tracker = map.get("aztracker") != null;
/*      */         
/*  616 */         if (this.az_tracker != this_is_az_tracker)
/*      */         {
/*  618 */           this.az_tracker = this_is_az_tracker;
/*      */           
/*  620 */           TRTrackerUtils.setAZTracker(this.tracker_url, this.az_tracker);
/*      */         }
/*      */         
/*  623 */         mapFiles = (Map)map.get("files");
/*      */         
/*  625 */         if (Logger.isEnabled()) {
/*  626 */           Logger.log(new LogEvent(LOGID, "Response from scrape interface " + this.scrapeURL + ": " + (mapFiles == null ? "null" : new StringBuilder().append("").append(mapFiles.size()).toString()) + " returned"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  631 */         iMinRequestInterval = 0;
/*  632 */         if (map != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  648 */           Map mapFlags = (Map)map.get("flags");
/*  649 */           if (mapFlags != null) {
/*  650 */             Long longScrapeValue = (Long)mapFlags.get("min_request_interval");
/*      */             
/*  652 */             if (longScrapeValue != null) {
/*  653 */               iMinRequestInterval = longScrapeValue.intValue();
/*      */             }
/*  655 */             if (Logger.isEnabled()) {
/*  656 */               Logger.log(new LogEvent(LOGID, "Received min_request_interval of " + iMinRequestInterval));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  661 */         if ((mapFiles == null) || (mapFiles.size() == 0))
/*      */         {
/*  663 */           if ((this.bSingleHashScrapes) && (map.containsKey("complete")) && (map.containsKey("incomplete"))) {
/*  664 */             int complete = MapUtils.getMapInt(map, "complete", 0);
/*  665 */             int incomplete = MapUtils.getMapInt(map, "incomplete", 0);
/*  666 */             TRTrackerScraperResponseImpl response = (TRTrackerScraperResponseImpl)activeResponses.get(0);
/*      */             
/*  668 */             response.setPeers(incomplete);
/*  669 */             response.setSeeds(complete);
/*      */             
/*  671 */             int minRequestInterval = MapUtils.getMapInt(map, "interval", 600000);
/*      */             
/*  673 */             int scrapeInterval = TRTrackerScraperResponseImpl.calcScrapeIntervalSecs(minRequestInterval, complete);
/*      */             
/*      */ 
/*  676 */             long nextScrapeTime = SystemTime.getCurrentTime() + scrapeInterval * 1000;
/*      */             
/*  678 */             response.setNextScrapeStartTime(nextScrapeTime);
/*  679 */             response.setStatus(2, "Tracker returned Announce from scrape call");
/*  680 */             response.setScrapeStartTime(scrapeStartTime);
/*      */             
/*  682 */             this.scraper.scrapeReceived(response); return;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  689 */           byte[] failure_reason_bytes = map == null ? null : (byte[])map.get("failure reason");
/*      */           
/*      */           long nextScrapeTime;
/*  692 */           if (failure_reason_bytes != null) {
/*  693 */             nextScrapeTime = SystemTime.getCurrentTime() + (iMinRequestInterval == 0 ? 600000 : iMinRequestInterval * 1000);
/*      */             
/*      */ 
/*      */ 
/*  697 */             for (TRTrackerScraperResponseImpl response : activeResponses)
/*      */             {
/*  699 */               response.setNextScrapeStartTime(nextScrapeTime);
/*      */               
/*  701 */               response.setStatus(1, MessageText.getString("Scrape.status.error") + new String(failure_reason_bytes, "UTF8"));
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  708 */               this.scraper.scrapeReceived(response);
/*      */             }
/*      */             
/*      */           }
/*  712 */           else if (activeResponses.size() > 1)
/*      */           {
/*      */ 
/*  715 */             this.bSingleHashScrapes = true;
/*  716 */             if (Logger.isEnabled()) {
/*  717 */               Logger.log(new LogEvent(LOGID, 1, this.scrapeURL + " doesn't properly support " + "multi-hash scrapes"));
/*      */             }
/*      */             
/*  720 */             for (TRTrackerScraperResponseImpl response : activeResponses)
/*      */             {
/*  722 */               response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.invalid"));
/*      */               
/*      */ 
/*      */ 
/*  726 */               this.scraper.scrapeReceived(response);
/*      */             }
/*      */           } else {
/*  729 */             long nextScrapeTime = SystemTime.getCurrentTime() + (iMinRequestInterval == 0 ? 10800000 : iMinRequestInterval * 1000);
/*      */             
/*      */ 
/*      */ 
/*  733 */             TRTrackerScraperResponseImpl response = (TRTrackerScraperResponseImpl)activeResponses.get(0);
/*  734 */             response.setNextScrapeStartTime(nextScrapeTime);
/*  735 */             response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.nohash"));
/*      */             
/*      */ 
/*      */ 
/*  739 */             this.scraper.scrapeReceived(response);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  751 */         if ((!this.bSingleHashScrapes) && (activeResponses.size() > 1) && (mapFiles.size() == 1))
/*      */         {
/*  753 */           this.bSingleHashScrapes = true;
/*  754 */           if (Logger.isEnabled()) {
/*  755 */             Logger.log(new LogEvent(LOGID, 1, this.scrapeURL + " only returned " + mapFiles.size() + " hash scrape(s), but we asked for " + activeResponses.size()));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  760 */         for (TRTrackerScraperResponseImpl response : activeResponses)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  766 */           Map scrapeMap = (Map)mapFiles.get(new String(response.getHash().getBytes(), "ISO-8859-1"));
/*      */           
/*      */ 
/*  769 */           if (scrapeMap == null)
/*      */           {
/*  771 */             if ((activeResponses.size() == 1) || (mapFiles.size() != 1))
/*      */             {
/*  773 */               response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 10800000L);
/*      */               
/*      */ 
/*  776 */               response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.nohash"));
/*      */               
/*      */ 
/*      */ 
/*  780 */               this.scraper.scrapeReceived(response);
/*  781 */             } else if (this.scraper.isTorrentScrapable(response.getHash()))
/*      */             {
/*      */ 
/*      */ 
/*  785 */               response.revertStatus();
/*      */               
/*  787 */               if (response.getStatus() == 3)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  795 */                 response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */                 
/*      */ 
/*  798 */                 response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.invalid"));
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*      */ 
/*  806 */                 this.bSingleHashScrapes = true;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  811 */                 if (original_bSingleHashScrapes)
/*      */                 {
/*  813 */                   response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  819 */               this.scraper.scrapeReceived(response);
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*      */ 
/*  831 */             Long l_seeds = (Long)scrapeMap.get("complete");
/*  832 */             Long l_peers = (Long)scrapeMap.get("incomplete");
/*  833 */             Long l_comp = (Long)scrapeMap.get("downloaded");
/*      */             
/*  835 */             int seeds = l_seeds == null ? 0 : l_seeds.intValue();
/*  836 */             int peers = l_peers == null ? 0 : l_peers.intValue();
/*  837 */             int completed = l_comp == null ? -1 : l_comp.intValue();
/*      */             
/*      */ 
/*  840 */             if ((seeds < 0) || (peers < 0) || (completed < -1)) {
/*  841 */               if (Logger.isEnabled()) {
/*  842 */                 HashWrapper hash = response.getHash();
/*  843 */                 Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, "Invalid scrape response from '" + reqUrl + "': map = " + scrapeMap));
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  852 */               if ((activeResponses.size() > 1) && (this.bSingleHashScrapes))
/*      */               {
/*  854 */                 response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.invalid"));
/*      */                 
/*      */ 
/*      */ 
/*  858 */                 this.scraper.scrapeReceived(response);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  863 */                 response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */                 
/*  865 */                 response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.invalid") + " " + (seeds < 0 ? MessageText.getString("MyTorrentsView.seeds") + " == " + seeds + ". " : "") + (peers < 0 ? MessageText.getString("MyTorrentsView.peers") + " == " + peers + ". " : "") + (completed < 0 ? MessageText.getString("MyTorrentsView.completed") + " == " + completed + ". " : ""));
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  879 */                 this.scraper.scrapeReceived(response);
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/*  884 */               int scrapeInterval = TRTrackerScraperResponseImpl.calcScrapeIntervalSecs(iMinRequestInterval, seeds);
/*      */               
/*      */ 
/*  887 */               long nextScrapeTime = SystemTime.getCurrentTime() + scrapeInterval * 1000;
/*      */               
/*  889 */               response.setNextScrapeStartTime(nextScrapeTime);
/*      */               
/*      */ 
/*  892 */               response.setScrapeStartTime(scrapeStartTime);
/*  893 */               response.setSeeds(seeds);
/*  894 */               response.setPeers(peers);
/*  895 */               response.setCompleted(completed);
/*  896 */               response.setStatus(2, MessageText.getString("Scrape.status.ok"));
/*      */               
/*      */ 
/*      */ 
/*  900 */               this.scraper.scrapeReceived(response);
/*      */               try
/*      */               {
/*  903 */                 if ((activeResponses.size() == 1) && (redirect_url != null))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  908 */                   String redirect_str = redirect_url.toString();
/*      */                   
/*  910 */                   int s_pos = redirect_str.indexOf("/scrape");
/*      */                   
/*  912 */                   if (s_pos != -1)
/*      */                   {
/*  914 */                     URL new_url = new URL(redirect_str.substring(0, s_pos) + "/announce" + redirect_str.substring(s_pos + 7));
/*      */                     
/*      */ 
/*  917 */                     if (this.scraper.redirectTrackerUrl(response.getHash(), this.tracker_url, new_url))
/*      */                     {
/*  919 */                       removeHash(response.getHash());
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/*  925 */                 Debug.printStackTrace(e);
/*      */               } } } } } catch (NoClassDefFoundError ignoreSSL) { URL reqUrl;
/*      */         long scrapeStartTime;
/*      */         URL redirect_url;
/*      */         Map mapFiles;
/*      */         int iMinRequestInterval;
/*  931 */         for (TRTrackerScraperResponseImpl response : activeResponses) {
/*  932 */           response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */           
/*  934 */           response.setStatus(1, MessageText.getString("Scrape.status.error") + ignoreSSL.getMessage());
/*      */           
/*      */ 
/*      */ 
/*  938 */           this.scraper.scrapeReceived(response);
/*      */         }
/*      */       } catch (FileNotFoundException e) {
/*  941 */         for (TRTrackerScraperResponseImpl response : activeResponses) {
/*  942 */           response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */           
/*  944 */           response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("DownloadManager.error.filenotfound"));
/*      */           
/*      */ 
/*      */ 
/*  948 */           this.scraper.scrapeReceived(response);
/*      */         }
/*      */       } catch (SocketException e) {
/*  951 */         setAllError(activeResponses, e);
/*      */       } catch (SocketTimeoutException e) {
/*  953 */         setAllError(activeResponses, e);
/*      */       } catch (UnknownHostException e) {
/*  955 */         setAllError(activeResponses, e);
/*      */       } catch (PRUDPPacketHandlerException e) {
/*  957 */         setAllError(activeResponses, e);
/*      */       } catch (BEncodingException e) {
/*  959 */         setAllError(activeResponses, e);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*      */ 
/*  966 */         String error_message = e.getMessage();
/*      */         
/*  968 */         if (error_message != null) {
/*  969 */           if ((error_message.contains(" 500 ")) || (error_message.contains(" 400 ")) || (error_message.contains(" 403 ")) || (error_message.contains(" 404 ")) || (error_message.contains(" 501 ")))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  976 */             setAllError(activeResponses, e); return;
/*      */           }
/*      */           
/*      */ 
/*  980 */           if ((error_message.contains("414")) && (!this.bSingleHashScrapes))
/*      */           {
/*  982 */             this.bSingleHashScrapes = true; return;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  988 */         msg = Debug.getNestedExceptionMessage(e);
/*      */         
/*  990 */         if (scrape_reply != null)
/*      */         {
/*      */           String trace_data;
/*      */           String trace_data;
/*  994 */           if (scrape_reply.length <= 150)
/*      */           {
/*  996 */             trace_data = new String(scrape_reply);
/*      */           }
/*      */           else
/*      */           {
/* 1000 */             trace_data = new String(scrape_reply, 0, 150) + "...";
/*      */           }
/*      */           
/* 1003 */           msg = msg + " [" + trace_data + "]";
/*      */         }
/*      */         
/* 1006 */         i$ = activeResponses.iterator(); } while (i$.hasNext()) { TRTrackerScraperResponseImpl response = (TRTrackerScraperResponseImpl)i$.next();
/*      */         
/* 1008 */         if (Logger.isEnabled()) {
/* 1009 */           HashWrapper hash = response.getHash();
/* 1010 */           Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, 3, "Error from scrape interface " + this.scrapeURL + " : " + msg + " (" + e.getClass() + ")"));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1015 */         response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */         
/* 1017 */         response.setStatus(1, MessageText.getString("Scrape.status.error") + msg);
/*      */         
/*      */ 
/*      */ 
/* 1021 */         this.scraper.scrapeReceived(response);
/*      */       }
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/* 1026 */       Debug.out("runScrapesSupport failed", t);
/*      */     } finally {
/* 1028 */       this.numActiveScrapes.decrementAndGet();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setAllError(List<TRTrackerScraperResponseImpl> responses, Exception e)
/*      */   {
/*      */     String msg;
/*      */     
/*      */ 
/* 1039 */     if ((e instanceof BEncodingException))
/*      */     {
/* 1041 */       String msg = e.getLocalizedMessage();
/*      */       
/* 1043 */       if (msg.contains("html"))
/*      */       {
/* 1045 */         msg = "Could not decode response, appears to be a website instead of tracker scrape: " + msg.replace('\n', ' ');
/*      */       }
/*      */       else
/*      */       {
/* 1049 */         msg = "Bencoded response malformed: " + msg;
/*      */       }
/*      */     }
/*      */     else {
/* 1053 */       msg = Debug.getNestedExceptionMessage(e);
/*      */     }
/*      */     
/* 1056 */     for (TRTrackerScraperResponseImpl response : responses)
/*      */     {
/* 1058 */       if (Logger.isEnabled()) {
/* 1059 */         HashWrapper hash = response.getHash();
/* 1060 */         Logger.log(new LogEvent(TorrentUtils.getDownloadManager(hash), LOGID, 1, "Error from scrape interface " + this.scrapeURL + " : " + msg));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1066 */       response.setNextScrapeStartTime(SystemTime.getCurrentTime() + 600000L);
/*      */       
/* 1068 */       response.setStatus(1, StringInterner.intern(MessageText.getString("Scrape.status.error") + msg));
/*      */       
/*      */ 
/* 1071 */       this.scraper.scrapeReceived(response);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private URL scrapeHTTP(List<HashWrapper> hashesInQuery, URL reqUrl, ByteArrayOutputStream message)
/*      */     throws Exception
/*      */   {
/* 1083 */     byte[] example_hash = ((HashWrapper)hashesInQuery.get(0)).getBytes();
/*      */     try
/*      */     {
/* 1086 */       return scrapeHTTPSupport(reqUrl, example_hash, null, message);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1090 */       if (AENetworkClassifier.categoriseAddress(reqUrl.getHost()) != "Public")
/*      */       {
/* 1092 */         Map<String, Object> opts = new HashMap();
/*      */         String[] nets;
/* 1094 */         if (hashesInQuery.size() == 1)
/*      */         {
/* 1096 */           opts.put("peer_networks", this.scraper.getEnabledNetworks((HashWrapper)hashesInQuery.get(0)));
/*      */         }
/*      */         else
/*      */         {
/* 1100 */           String[] current_nets = null;
/*      */           
/* 1102 */           for (HashWrapper hash : hashesInQuery)
/*      */           {
/* 1104 */             nets = this.scraper.getEnabledNetworks(hash);
/*      */             
/* 1106 */             if (nets == null)
/*      */             {
/* 1108 */               nets = new String[0];
/*      */             }
/*      */             
/* 1111 */             if (current_nets == null)
/*      */             {
/* 1113 */               current_nets = nets;
/*      */             }
/*      */             else
/*      */             {
/* 1117 */               boolean ok = false;
/*      */               
/* 1119 */               if (nets.length == current_nets.length)
/*      */               {
/* 1121 */                 ok = true;
/*      */                 
/* 1123 */                 for (String net1 : nets)
/*      */                 {
/* 1125 */                   boolean match = false;
/*      */                   
/* 1127 */                   for (String net2 : current_nets)
/*      */                   {
/* 1129 */                     if (net1 == net2)
/*      */                     {
/* 1131 */                       match = true;
/*      */                       
/* 1133 */                       break;
/*      */                     }
/*      */                   }
/*      */                   
/* 1137 */                   if (!match)
/*      */                   {
/* 1139 */                     ok = false;
/*      */                     
/* 1141 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               else {
/* 1146 */                 ok = false;
/*      */               }
/*      */               
/*      */ 
/* 1150 */               if (!ok)
/*      */               {
/* 1152 */                 this.bSingleHashScrapes = true;
/*      */                 
/* 1154 */                 throw new Exception("Mixed networks, forcing single-hash scrapes");
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1159 */           if (current_nets != null)
/*      */           {
/* 1161 */             opts.put("peer_networks", current_nets);
/*      */           }
/*      */         }
/*      */         
/* 1165 */         AEProxyFactory.PluginProxy proxy = com.aelitis.azureus.core.proxy.AEProxyFactory.getPluginProxy("Tracker scrape", reqUrl, opts, true);
/*      */         
/* 1167 */         if (proxy != null)
/*      */         {
/* 1169 */           boolean ok = false;
/*      */           
/*      */           try
/*      */           {
/* 1173 */             URL result = scrapeHTTPSupport(proxy.getURL(), example_hash, proxy.getProxy(), message);
/*      */             
/* 1175 */             ok = true;
/*      */             
/* 1177 */             return result;
/*      */ 
/*      */           }
/*      */           catch (Throwable f) {}finally
/*      */           {
/*      */ 
/* 1183 */             proxy.setOK(ok);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1188 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private URL scrapeHTTPSupport(URL reqUrl, byte[] example_hash, Proxy proxy, ByteArrayOutputStream message)
/*      */     throws IOException
/*      */   {
/*      */     URL redirect_url;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1204 */     for (int connect_loop = 0; connect_loop < 3; connect_loop++)
/*      */     {
/* 1206 */       redirect_url = null;
/*      */       
/* 1208 */       TRTrackerUtils.checkForBlacklistedURLs(reqUrl);
/*      */       
/* 1210 */       reqUrl = TRTrackerUtils.adjustURLForHosting(reqUrl);
/*      */       
/* 1212 */       reqUrl = AddressUtils.adjustURL(reqUrl);
/*      */       
/*      */ 
/*      */ 
/* 1216 */       Properties http_properties = new Properties();
/*      */       
/* 1218 */       http_properties.put("URL", reqUrl);
/*      */       
/* 1220 */       if (proxy != null)
/*      */       {
/* 1222 */         http_properties.put("Proxy", proxy);
/*      */       }
/*      */       
/* 1225 */       if (this.enable_sni_hack)
/*      */       {
/* 1227 */         http_properties.put("SNI-Hack", Boolean.valueOf(true));
/*      */       }
/*      */       try
/*      */       {
/* 1231 */         ClientIDManagerImpl.getSingleton().generateHTTPProperties(example_hash, http_properties);
/*      */       }
/*      */       catch (ClientIDException e)
/*      */       {
/* 1235 */         throw new IOException(e.getMessage());
/*      */       }
/*      */       
/* 1238 */       reqUrl = (URL)http_properties.get("URL");
/*      */       
/* 1240 */       InputStream is = null;
/*      */       try
/*      */       {
/* 1243 */         HttpURLConnection con = null;
/*      */         
/* 1245 */         if (reqUrl.getProtocol().equalsIgnoreCase("https"))
/*      */         {
/*      */           HttpsURLConnection ssl_con;
/*      */           
/*      */           HttpsURLConnection ssl_con;
/*      */           
/* 1251 */           if (proxy == null)
/*      */           {
/* 1253 */             ssl_con = (HttpsURLConnection)reqUrl.openConnection();
/*      */           }
/*      */           else
/*      */           {
/* 1257 */             ssl_con = (HttpsURLConnection)reqUrl.openConnection(proxy);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1262 */           if (!this.internal_error_hack)
/*      */           {
/* 1264 */             ssl_con.setHostnameVerifier(new HostnameVerifier()
/*      */             {
/*      */               public boolean verify(String host, SSLSession session) {
/* 1267 */                 return true;
/*      */               }
/*      */             });
/*      */           }
/*      */           
/* 1272 */           if (this.dh_hack)
/*      */           {
/* 1274 */             UrlUtils.DHHackIt(ssl_con);
/*      */           }
/*      */           
/* 1277 */           if (connect_loop > 0)
/*      */           {
/*      */ 
/*      */ 
/* 1281 */             javax.net.ssl.TrustManager[] trustAllCerts = SESecurityManager.getAllTrustingTrustManager();
/*      */             try
/*      */             {
/* 1284 */               SSLContext sc = SSLContext.getInstance("SSL");
/*      */               
/* 1286 */               sc.init(null, trustAllCerts, RandomUtils.SECURE_RANDOM);
/*      */               
/* 1288 */               SSLSocketFactory factory = sc.getSocketFactory();
/*      */               
/* 1290 */               ssl_con.setSSLSocketFactory(factory);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/* 1296 */           con = ssl_con;
/*      */ 
/*      */ 
/*      */         }
/* 1300 */         else if (proxy == null)
/*      */         {
/* 1302 */           con = (HttpURLConnection)reqUrl.openConnection();
/*      */         }
/*      */         else
/*      */         {
/* 1306 */           con = (HttpURLConnection)reqUrl.openConnection(proxy);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1313 */         con.setInstanceFollowRedirects(true);
/*      */         
/* 1315 */         String user_agent = (String)http_properties.get("User-Agent");
/*      */         
/* 1317 */         if (user_agent != null)
/*      */         {
/* 1319 */           con.setRequestProperty("User-Agent", user_agent);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1324 */         con.addRequestProperty("Accept-Encoding", "gzip");
/*      */         
/* 1326 */         con.setRequestProperty("Connection", "close");
/*      */         try
/*      */         {
/* 1329 */           con.connect();
/*      */         }
/*      */         catch (AEProxyFactory.UnknownHostException e)
/*      */         {
/* 1333 */           throw new UnknownHostException(e.getMessage());
/*      */         }
/*      */         
/* 1336 */         is = con.getInputStream();
/*      */         
/* 1338 */         String resulting_url_str = con.getURL().toString();
/*      */         
/* 1340 */         if (!reqUrl.toString().equals(resulting_url_str))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1346 */           String marker = "permredirect=1";
/*      */           
/* 1348 */           int pos = resulting_url_str.indexOf(marker);
/*      */           
/* 1350 */           if (pos != -1)
/*      */           {
/* 1352 */             pos -= 1;
/*      */             try
/*      */             {
/* 1355 */               redirect_url = new URL(resulting_url_str.substring(0, pos));
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1359 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1364 */         String encoding = con.getHeaderField("content-encoding");
/*      */         
/* 1366 */         boolean gzip = (encoding != null) && (encoding.equalsIgnoreCase("gzip"));
/*      */         
/*      */ 
/*      */ 
/* 1370 */         if (gzip)
/*      */         {
/* 1372 */           is = new GZIPInputStream(is);
/*      */         }
/*      */         
/* 1375 */         byte[] data = new byte['Ѐ'];
/*      */         
/* 1377 */         int num_read = 0;
/*      */         try
/*      */         {
/*      */           for (;;)
/*      */           {
/* 1382 */             int len = is.read(data);
/*      */             
/* 1384 */             if (len > 0)
/*      */             {
/* 1386 */               message.write(data, 0, len);
/*      */               
/* 1388 */               num_read += len;
/*      */               
/* 1390 */               if (num_read > 131072)
/*      */               {
/*      */ 
/*      */ 
/* 1394 */                 message.reset();
/*      */                 
/* 1396 */                 throw new Exception("Tracker response invalid (too large)");
/*      */               }
/*      */             }
/* 1399 */             else if (len == 0)
/*      */             {
/* 1401 */               Thread.sleep(20L);
/*      */             }
/*      */             else
/*      */             {
/*      */               break;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1477 */           if (is == null) {
/*      */             continue;
/*      */           }
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 1409 */           if (Logger.isEnabled()) {
/* 1410 */             Logger.log(new LogEvent(LOGID, 3, "Error from scrape interface " + this.scrapeURL + " : " + Debug.getNestedExceptionMessage(e)));
/*      */           }
/*      */           
/*      */ 
/* 1414 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 1479 */           is.close();
/*      */         }
/*      */         catch (IOException e1) {}
/*      */         String msg;
/*      */         boolean try_again;
/* 1484 */         return redirect_url;
/*      */       }
/*      */       catch (SSLException e)
/*      */       {
/* 1419 */         if (connect_loop < 3)
/*      */         {
/* 1421 */           msg = Debug.getNestedExceptionMessage(e);
/*      */           
/* 1423 */           try_again = false;
/*      */           
/* 1425 */           if (msg.contains("unrecognized_name"))
/*      */           {
/*      */ 
/*      */ 
/* 1429 */             if (!this.enable_sni_hack)
/*      */             {
/* 1431 */               this.enable_sni_hack = true;
/*      */               
/* 1433 */               try_again = true;
/*      */             }
/* 1435 */           } else if (msg.contains("internal_error"))
/*      */           {
/* 1437 */             if (!this.internal_error_hack)
/*      */             {
/* 1439 */               this.internal_error_hack = true;
/*      */               
/* 1441 */               try_again = true;
/*      */             }
/* 1443 */           } else if (msg.contains("DH keypair"))
/*      */           {
/* 1445 */             if (!this.dh_hack)
/*      */             {
/* 1447 */               this.dh_hack = true;
/*      */               
/* 1449 */               try_again = true;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1460 */           if ((SESecurityManager.installServerCertificates(reqUrl) != null) || (connect_loop == 0))
/*      */           {
/*      */ 
/*      */ 
/* 1464 */             try_again = true;
/*      */           }
/*      */           
/* 1467 */           if (!try_again) {}
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1473 */         throw e;
/*      */       }
/*      */       finally
/*      */       {
/* 1477 */         if (is != null) {
/*      */           try {
/* 1479 */             is.close();
/*      */           }
/*      */           catch (IOException e1) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1487 */     throw new IOException("Shouldn't get here");
/*      */   }
/*      */   
/*      */   protected boolean scrapeUDP(URL reqUrl, ByteArrayOutputStream message, List hashes, boolean do_auth_test) throws Exception {
/* 1491 */     Map rootMap = new HashMap();
/* 1492 */     Map files = new ByteEncodedKeyHashMap();
/* 1493 */     rootMap.put("files", files);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1528 */     reqUrl = TRTrackerUtils.adjustURLForHosting(reqUrl);
/*      */     
/* 1530 */     java.net.PasswordAuthentication auth = null;
/* 1531 */     boolean auth_ok = false;
/*      */     try
/*      */     {
/* 1534 */       if ((do_auth_test) && (UrlUtils.queryHasParameter(reqUrl.getQuery(), "auth", false))) {
/* 1535 */         auth = SESecurityManager.getPasswordAuthentication("UDP Tracker", reqUrl);
/*      */       }
/*      */       
/* 1538 */       int port = UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber();
/*      */       
/* 1540 */       PRUDPPacketHandler handler = PRUDPPacketHandlerFactory.getHandler(port);
/*      */       
/* 1542 */       InetSocketAddress destination = new InetSocketAddress(reqUrl.getHost(), reqUrl.getPort() == -1 ? 80 : reqUrl.getPort());
/*      */       
/* 1544 */       handler = handler.openSession(destination);
/*      */       String failure_reason;
/*      */       try {
/* 1547 */         failure_reason = null;
/*      */         
/* 1549 */         for (int retry_loop = 0; retry_loop < 1; retry_loop++) {
/*      */           try
/*      */           {
/* 1552 */             PRUDPPacket connect_request = new PRUDPPacketRequestConnect();
/*      */             
/* 1554 */             PRUDPPacket reply = handler.sendAndReceive(auth, connect_request, destination);
/*      */             
/* 1556 */             if (reply.getAction() == 0)
/*      */             {
/* 1558 */               PRUDPPacketReplyConnect connect_reply = (PRUDPPacketReplyConnect)reply;
/*      */               
/* 1560 */               long my_connection = connect_reply.getConnectionId();
/*      */               
/* 1562 */               PRUDPPacketRequestScrape scrape_request = new PRUDPPacketRequestScrape(my_connection, hashes);
/*      */               
/* 1564 */               reply = handler.sendAndReceive(auth, scrape_request, destination);
/*      */               
/* 1566 */               if (reply.getAction() == 2)
/*      */               {
/* 1568 */                 auth_ok = true;
/*      */                 
/* 1570 */                 if (org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketTracker.VERSION == 1) {
/* 1571 */                   PRUDPPacketReplyScrape scrape_reply = (PRUDPPacketReplyScrape)reply;
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1582 */                   byte[][] reply_hashes = scrape_reply.getHashes();
/* 1583 */                   int[] complete = scrape_reply.getComplete();
/* 1584 */                   int[] downloaded = scrape_reply.getDownloaded();
/* 1585 */                   int[] incomplete = scrape_reply.getIncomplete();
/*      */                   
/*      */ 
/* 1588 */                   for (int i = 0; i < reply_hashes.length; i++)
/*      */                   {
/* 1590 */                     file = new HashMap();
/*      */                     
/* 1592 */                     byte[] resp_hash = reply_hashes[i];
/*      */                     
/*      */ 
/*      */ 
/* 1596 */                     files.put(new String(resp_hash, "ISO-8859-1"), file);
/*      */                     
/* 1598 */                     file.put("complete", new Long(complete[i]));
/* 1599 */                     file.put("downloaded", new Long(downloaded[i]));
/* 1600 */                     file.put("incomplete", new Long(incomplete[i]));
/*      */                   }
/*      */                   
/* 1603 */                   byte[] data = BEncoder.encode(rootMap);
/*      */                   
/* 1605 */                   message.write(data);
/*      */                   
/* 1607 */                   Map file = 1;
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1686 */                   handler.closeSession();
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1693 */                   return file;
/*      */                 }
/* 1609 */                 PRUDPPacketReplyScrape2 scrape_reply = (PRUDPPacketReplyScrape2)reply;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1621 */                 int[] complete = scrape_reply.getComplete();
/* 1622 */                 int[] downloaded = scrape_reply.getDownloaded();
/* 1623 */                 int[] incomplete = scrape_reply.getIncomplete();
/*      */                 
/* 1625 */                 int i = 0;
/* 1626 */                 for (Iterator it = hashes.iterator(); (it.hasNext()) && (i < complete.length); i++)
/*      */                 {
/* 1628 */                   hash = (HashWrapper)it.next();
/* 1629 */                   Map file = new HashMap();
/* 1630 */                   file.put("complete", new Long(complete[i]));
/* 1631 */                   file.put("downloaded", new Long(downloaded[i]));
/* 1632 */                   file.put("incomplete", new Long(incomplete[i]));
/* 1633 */                   files.put(new String(hash.getBytes(), "ISO-8859-1"), file);
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/* 1638 */                 byte[] data = BEncoder.encode(rootMap);
/*      */                 
/* 1640 */                 message.write(data);
/*      */                 
/* 1642 */                 HashWrapper hash = 1;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1686 */                 handler.closeSession();
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1693 */                 return hash;
/*      */               }
/* 1646 */               failure_reason = ((PRUDPPacketReplyError)reply).getMessage();
/*      */               
/* 1648 */               if (Logger.isEnabled()) {
/* 1649 */                 Logger.log(new LogEvent(LOGID, 3, "Response from scrape interface " + reqUrl + " : " + failure_reason));
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1656 */               failure_reason = ((PRUDPPacketReplyError)reply).getMessage();
/*      */               
/* 1658 */               if (Logger.isEnabled()) {
/* 1659 */                 Logger.log(new LogEvent(LOGID, 3, "Response from scrape interface " + reqUrl + " : " + ((PRUDPPacketReplyError)reply).getMessage()));
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */           catch (PRUDPPacketHandlerException e)
/*      */           {
/* 1667 */             if ((e.getMessage() == null) || (!e.getMessage().contains("timed out")))
/*      */             {
/* 1669 */               throw e;
/*      */             }
/*      */             
/* 1672 */             failure_reason = "Timeout";
/*      */           }
/*      */         }
/*      */         
/* 1676 */         if (failure_reason != null)
/*      */         {
/* 1678 */           rootMap.put("failure reason", failure_reason.getBytes());
/* 1679 */           rootMap.remove("files");
/*      */           
/* 1681 */           byte[] data = BEncoder.encode(rootMap);
/* 1682 */           message.write(data);
/*      */         }
/*      */       }
/*      */       finally {
/* 1686 */         handler.closeSession();
/*      */       }
/*      */       
/* 1689 */       return 0;
/*      */     } finally {
/* 1691 */       if (auth != null)
/*      */       {
/* 1693 */         SESecurityManager.setPasswordAuthenticationOutcome("UDP Tracker", reqUrl, auth_ok);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getURLParam(String url, String param)
/*      */   {
/* 1703 */     int p1 = url.indexOf(param + "=");
/*      */     
/* 1705 */     if (p1 == -1)
/*      */     {
/* 1707 */       return null;
/*      */     }
/*      */     
/* 1710 */     int p2 = url.indexOf("&", p1);
/*      */     
/* 1712 */     if (p2 == -1)
/*      */     {
/* 1714 */       return url.substring(p1 + param.length() + 1);
/*      */     }
/*      */     
/* 1717 */     return url.substring(p1 + param.length() + 1, p2);
/*      */   }
/*      */   
/*      */ 
/*      */   protected TRTrackerScraperResponseImpl addHash(HashWrapper hash)
/*      */   {
/*      */     TRTrackerScraperResponseImpl response;
/*      */     try
/*      */     {
/* 1726 */       this.hashes_mon.enter();
/*      */       
/* 1728 */       response = (TRTrackerScraperResponseImpl)this.hashes.get(hash);
/*      */       
/* 1730 */       if (response == null)
/*      */       {
/* 1732 */         response = new TRTrackerBTScraperResponseImpl(this, hash);
/*      */         
/* 1734 */         if (this.scrapeURL == null)
/*      */         {
/* 1736 */           response.setStatus(1, MessageText.getString("Scrape.status.error") + MessageText.getString("Scrape.status.error.badURL"));
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1741 */           response.setStatus(0, MessageText.getString("Scrape.status.initializing"));
/*      */         }
/*      */         
/*      */ 
/* 1745 */         response.setNextScrapeStartTime(this.checker.getNextScrapeCheckOn());
/*      */         
/* 1747 */         this.hashes.put(hash, response);
/*      */       }
/*      */     }
/*      */     finally {
/* 1751 */       this.hashes_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1756 */     this.scraper.scrapeReceived(response);
/*      */     
/* 1758 */     return response;
/*      */   }
/*      */   
/*      */   protected void removeHash(HashWrapper hash) {
/*      */     try {
/* 1763 */       this.hashes_mon.enter();
/*      */       
/* 1765 */       this.hashes.remove(hash);
/*      */     }
/*      */     finally
/*      */     {
/* 1769 */       this.hashes_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected URL getTrackerURL()
/*      */   {
/* 1776 */     return this.tracker_url;
/*      */   }
/*      */   
/*      */   protected Map getHashes() {
/* 1780 */     return this.hashes;
/*      */   }
/*      */   
/*      */ 
/*      */   protected AEMonitor getHashesMonitor()
/*      */   {
/* 1786 */     return this.hashes_mon;
/*      */   }
/*      */   
/*      */   protected void scrapeReceived(TRTrackerScraperResponse response) {
/* 1790 */     this.scraper.scrapeReceived(response);
/*      */   }
/*      */   
/*      */   public boolean getSupportsMultipeHashScrapes() {
/* 1794 */     return !this.bSingleHashScrapes;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getString()
/*      */   {
/* 1800 */     return this.tracker_url + ", " + this.scrapeURL + ", multi-scrape=" + (!this.bSingleHashScrapes);
/*      */   }
/*      */   
/*      */   public int getNumActiveScrapes() {
/* 1804 */     return this.numActiveScrapes.get();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/bt/TrackerStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */