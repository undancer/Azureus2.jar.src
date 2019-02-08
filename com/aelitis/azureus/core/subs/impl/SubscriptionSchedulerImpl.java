/*     */ package com.aelitis.azureus.core.subs.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManagerFactory;
/*     */ import com.aelitis.azureus.core.metasearch.Engine;
/*     */ import com.aelitis.azureus.core.metasearch.impl.web.WebEngine;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionDownloadListener;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionException;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionManagerListener;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionScheduler;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.plugins.utils.search.SearchProvider;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
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
/*     */ public class SubscriptionSchedulerImpl
/*     */   implements SubscriptionScheduler, SubscriptionManagerListener
/*     */ {
/*  75 */   private static final Object SCHEDULER_NEXT_SCAN_KEY = new Object();
/*  76 */   private static final Object SCHEDULER_FAILED_SCAN_CONSEC_KEY = new Object();
/*  77 */   private static final Object SCHEDULER_FAILED_SCAN_TIME_KEY = new Object();
/*     */   
/*     */   private static final int FAIL_INIT_DELAY = 600000;
/*     */   
/*     */   private static final int FAIL_MAX_DELAY = 28800000;
/*     */   
/*     */   private SubscriptionManagerImpl manager;
/*  84 */   private Map active_subscription_downloaders = new HashMap();
/*     */   
/*     */   private boolean active_subs_download_is_auto;
/*  87 */   private Map<String, Long> rate_limit_map = new HashMap();
/*     */   
/*  89 */   private Set active_result_downloaders = new HashSet();
/*     */   
/*  91 */   private ThreadPool result_downloader = new ThreadPool("SubscriptionDownloader", 5, true);
/*     */   
/*     */   private boolean schedulng_permitted;
/*     */   
/*     */   private TimerEvent schedule_event;
/*     */   
/*     */   private boolean schedule_in_progress;
/*     */   
/*     */   private long last_schedule;
/*     */   
/*     */   private String last_sched_str;
/*     */   
/*     */   protected SubscriptionSchedulerImpl(SubscriptionManagerImpl _manager)
/*     */   {
/* 105 */     this.manager = _manager;
/*     */     
/* 107 */     this.manager.addListener(this);
/*     */     
/* 109 */     DelayedTask delayed_task = UtilitiesImpl.addDelayedTask("Subscriptions Scheduler", new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 115 */         synchronized (SubscriptionSchedulerImpl.this)
/*     */         {
/* 117 */           SubscriptionSchedulerImpl.this.schedulng_permitted = true;
/*     */         }
/*     */         
/* 120 */         SubscriptionSchedulerImpl.this.calculateSchedule();
/*     */       }
/*     */       
/* 123 */     });
/* 124 */     delayed_task.queue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadAsync(Subscription subs, boolean is_auto)
/*     */     throws SubscriptionException
/*     */   {
/* 134 */     download(subs, is_auto, new SubscriptionDownloadListener()
/*     */     {
/*     */       public void complete(Subscription subs) {}
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
/*     */       public void failed(Subscription subs, SubscriptionException error)
/*     */       {
/* 150 */         SubscriptionSchedulerImpl.this.log("Async download of " + subs.getName() + " failed", error);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void download(final Subscription subs, final boolean is_auto, final SubscriptionDownloadListener listener)
/*     */   {
/* 161 */     new AEThread2("SS:download", true)
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 167 */           SubscriptionSchedulerImpl.this.download(subs, is_auto);
/*     */           
/* 169 */           listener.complete(subs);
/*     */         }
/*     */         catch (SubscriptionException e)
/*     */         {
/* 173 */           listener.failed(subs, e);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 177 */           listener.failed(subs, new SubscriptionException("Download failed", e));
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean download(Subscription subs, boolean is_auto)
/*     */     throws SubscriptionException
/*     */   {
/* 192 */     AESemaphore sem = null;
/*     */     
/* 194 */     String rate_limits = this.manager.getRateLimits().trim();
/*     */     SubscriptionDownloader downloader;
/* 196 */     synchronized (this.active_subscription_downloaders)
/*     */     {
/* 198 */       if (rate_limits.length() > 0) {
/*     */         try
/*     */         {
/* 201 */           Engine engine = subs.getEngine();
/*     */           
/* 203 */           if ((engine instanceof WebEngine))
/*     */           {
/* 205 */             String url_str = ((WebEngine)engine).getSearchUrl(true);
/*     */             
/* 207 */             String host = new URL(url_str).getHost();
/*     */             
/* 209 */             String[] bits = rate_limits.split(",");
/*     */             
/* 211 */             for (String bit : bits)
/*     */             {
/* 213 */               String[] temp = bit.trim().split("=");
/*     */               
/* 215 */               if (temp.length == 2)
/*     */               {
/* 217 */                 String lhs = temp[0].trim();
/*     */                 
/* 219 */                 if (lhs.equals(host))
/*     */                 {
/* 221 */                   int mins = Integer.parseInt(temp[1].trim());
/*     */                   
/* 223 */                   if (mins > 0)
/*     */                   {
/* 225 */                     long now = SystemTime.getMonotonousTime();
/*     */                     
/* 227 */                     Long last = (Long)this.rate_limit_map.get(host);
/*     */                     
/* 229 */                     if ((last != null) && (now - last.longValue() < mins * 60 * 1000))
/*     */                     {
/* 231 */                       if (is_auto)
/*     */                       {
/* 233 */                         return false;
/*     */                       }
/*     */                       
/*     */ 
/* 237 */                       throw new SubscriptionException("Rate limiting prevents download from " + host);
/*     */                     }
/*     */                     
/*     */ 
/* 241 */                     this.rate_limit_map.put(host, Long.valueOf(now));
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (SubscriptionException e) {
/* 249 */           throw e;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 253 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */       
/* 257 */       List waiting = (List)this.active_subscription_downloaders.get(subs);
/*     */       
/* 259 */       if (waiting != null)
/*     */       {
/* 261 */         sem = new AESemaphore("SS:waiter");
/*     */         
/* 263 */         waiting.add(sem);
/*     */         
/* 265 */         if (!is_auto)
/*     */         {
/* 267 */           this.active_subs_download_is_auto = false;
/*     */         }
/*     */       }
/*     */       else {
/* 271 */         this.active_subscription_downloaders.put(subs, new ArrayList());
/*     */         
/* 273 */         this.active_subs_download_is_auto = is_auto;
/*     */       }
/*     */       
/* 276 */       downloader = new SubscriptionDownloader(this.manager, (SubscriptionImpl)subs);
/*     */     }
/*     */     try
/*     */     {
/* 280 */       if (sem == null)
/*     */       {
/* 282 */         downloader.download();
/*     */       }
/*     */       else
/*     */       {
/* 286 */         sem.reserve(); }
/*     */       List waiting;
/*     */       int i;
/* 289 */       boolean was_auto; return (boolean)1;
/*     */     }
/*     */     finally
/*     */     {
/*     */       boolean was_auto;
/*     */       
/* 295 */       synchronized (this.active_subscription_downloaders)
/*     */       {
/* 297 */         List waiting = (List)this.active_subscription_downloaders.remove(subs);
/*     */         
/* 299 */         if (waiting != null)
/*     */         {
/* 301 */           for (int i = 0; i < waiting.size(); i++)
/*     */           {
/* 303 */             ((AESemaphore)waiting.get(i)).release();
/*     */           }
/*     */         }
/*     */         
/* 307 */         was_auto = this.active_subs_download_is_auto;
/*     */       }
/*     */       
/* 310 */       ((SubscriptionImpl)subs).fireDownloaded(was_auto);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void download(final Subscription subs, final SubscriptionResult original_result)
/*     */   {
/* 319 */     String download_link = original_result.getDownloadLink();
/*     */     
/* 321 */     if (download_link == null)
/*     */     {
/* 323 */       log(subs.getName() + ": can't download " + original_result.getID() + " as no direct download link available");
/*     */       
/* 325 */       return;
/*     */     }
/*     */     
/* 328 */     if (UrlFilter.getInstance().isWhitelisted(download_link))
/*     */     {
/* 330 */       ContentNetwork cn = ContentNetworkManagerFactory.getSingleton().getContentNetworkForURL(download_link);
/*     */       
/* 332 */       if (cn == null)
/*     */       {
/* 334 */         cn = ConstantsVuze.getDefaultContentNetwork();
/*     */       }
/*     */       
/* 337 */       download_link = cn.appendURLSuffix(download_link, false, true);
/*     */     }
/*     */     
/* 340 */     final String key = subs.getID() + ":" + original_result.getID();
/* 341 */     final String dl = download_link;
/*     */     
/* 343 */     synchronized (this.active_result_downloaders)
/*     */     {
/* 345 */       if (this.active_result_downloaders.contains(key))
/*     */       {
/* 347 */         return;
/*     */       }
/*     */       
/* 350 */       log(subs.getName() + ": queued result for download - " + original_result.getID() + "/" + download_link);
/*     */       
/* 352 */       this.active_result_downloaders.add(key);
/*     */       
/* 354 */       this.result_downloader.run(new AERunnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*     */ 
/* 362 */           SubscriptionResult result = subs.getHistory().getResult(original_result.getID());
/*     */           
/* 364 */           boolean success = false;
/*     */           try
/*     */           {
/* 367 */             if (result == null)
/*     */             {
/* 369 */               SubscriptionSchedulerImpl.this.log(subs.getName() + ": result has been deleted - " + original_result.getID());
/*     */               
/* 371 */               success = true;
/*     */             }
/* 373 */             else if (result.getRead())
/*     */             {
/* 375 */               SubscriptionSchedulerImpl.this.log(subs.getName() + ": result already marked as read, skipping - " + result.getID());
/*     */               
/* 377 */               success = true;
/*     */             }
/*     */             else
/*     */             {
/* 381 */               boolean retry = true;
/*     */               
/* 383 */               boolean use_ref = subs.getHistory().getDownloadWithReferer();
/*     */               
/* 385 */               boolean tried_ref_switch = false;
/*     */               
/* 387 */               while (retry)
/*     */               {
/* 389 */                 retry = false;
/*     */                 try
/*     */                 {
/* 392 */                   TorrentUtils.setTLSDescription("Subscription: " + subs.getName());
/*     */                   
/* 394 */                   URL original_url = new URL(dl);
/*     */                   
/* 396 */                   AEProxyFactory.PluginProxy plugin_proxy = null;
/*     */                   
/* 398 */                   if (dl.startsWith("tor:"))
/*     */                   {
/* 400 */                     String target_resource = dl.substring(4);
/*     */                     
/* 402 */                     original_url = new URL(target_resource);
/*     */                     
/* 404 */                     Map<String, Object> options = new HashMap();
/*     */                     
/* 406 */                     options.put("peer_networks", new String[] { "Tor" });
/*     */                     
/* 408 */                     plugin_proxy = AEProxyFactory.getPluginProxy("Subscription result download of '" + target_resource + "'", original_url, options, true);
/*     */                     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 415 */                     if (plugin_proxy == null)
/*     */                     {
/* 417 */                       throw new Exception("No Tor plugin proxy available for '" + dl + "'");
/*     */                     }
/*     */                   }
/*     */                   
/* 421 */                   URL current_url = plugin_proxy == null ? original_url : plugin_proxy.getURL();
/*     */                   
/* 423 */                   Torrent torrent = null;
/*     */                   
/*     */ 
/*     */                   try
/*     */                   {
/*     */                     try
/*     */                     {
/* 430 */                       ResourceDownloaderFactory rdf = StaticUtilities.getResourceDownloaderFactory();
/*     */                       
/* 432 */                       ResourceDownloader url_rd = rdf.create(current_url, plugin_proxy == null ? null : plugin_proxy.getProxy());
/*     */                       
/* 434 */                       if (plugin_proxy != null)
/*     */                       {
/* 436 */                         url_rd.setProperty("URL_HOST", plugin_proxy.getURLHostRewrite() + (current_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(current_url.getPort()).toString()));
/*     */                       }
/*     */                       
/* 439 */                       String referer = use_ref ? subs.getReferer() : null;
/*     */                       
/* 441 */                       UrlUtils.setBrowserHeaders(url_rd, referer);
/*     */                       
/* 443 */                       Engine engine = subs.getEngine();
/*     */                       
/* 445 */                       if ((engine instanceof WebEngine))
/*     */                       {
/* 447 */                         WebEngine we = (WebEngine)engine;
/*     */                         
/* 449 */                         if (we.isNeedsAuth())
/*     */                         {
/* 451 */                           String cookies = we.getCookies();
/*     */                           
/* 453 */                           if ((cookies != null) && (cookies.length() > 0))
/*     */                           {
/* 455 */                             url_rd.setProperty("URL_Cookie", cookies);
/*     */                           }
/*     */                         }
/*     */                       }
/*     */                       
/* 460 */                       ResourceDownloader mr_rd = rdf.getMetaRefreshDownloader(url_rd);
/*     */                       
/* 462 */                       InputStream is = mr_rd.download();
/*     */                       
/* 464 */                       torrent = new TorrentImpl(TOTorrentFactory.deserialiseFromBEncodedInputStream(is));
/*     */ 
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/*     */ 
/* 470 */                       while (plugin_proxy == null)
/*     */                       {
/* 472 */                         plugin_proxy = AEProxyFactory.getPluginProxy("Subscription result download", original_url);
/*     */                         
/* 474 */                         if (plugin_proxy == null)
/*     */                           break;
/* 476 */                         current_url = plugin_proxy.getURL();
/*     */                       }
/*     */                       
/*     */ 
/*     */ 
/*     */ 
/* 482 */                       throw e;
/*     */                     }
/*     */                   }
/*     */                   finally
/*     */                   {
/* 487 */                     if (plugin_proxy != null)
/*     */                     {
/* 489 */                       plugin_proxy.setOK(torrent != null);
/*     */                     }
/*     */                   }
/*     */                   
/*     */ 
/* 494 */                   byte[] hash = torrent.getHash();
/*     */                   
/*     */ 
/*     */ 
/* 498 */                   DownloadManager dm = PluginInitializer.getDefaultInterface().getDownloadManager();
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 505 */                   boolean stop_override = (subs.getTagID() >= 0L) || (subs.getHistory().getDownloadNetworks() != null);
/*     */                   
/*     */ 
/* 508 */                   boolean auto_start = SubscriptionSchedulerImpl.this.manager.shouldAutoStart(torrent);
/*     */                   
/* 510 */                   SubscriptionSchedulerImpl.this.manager.addPrepareTrigger(hash, new Subscription[] { subs }, new SubscriptionResult[] { result });
/*     */                   Download download;
/*     */                   try { Download download;
/* 513 */                     if ((auto_start) && (!stop_override))
/*     */                     {
/* 515 */                       download = dm.addDownload(torrent);
/*     */                     }
/*     */                     else
/*     */                     {
/* 519 */                       download = dm.addDownloadStopped(torrent, null, null);
/*     */                     }
/*     */                   }
/*     */                   finally {
/* 523 */                     SubscriptionSchedulerImpl.this.manager.removePrepareTrigger(hash);
/*     */                   }
/*     */                   
/* 526 */                   SubscriptionSchedulerImpl.this.log(subs.getName() + ": added download " + download.getName() + ": auto-start=" + auto_start);
/*     */                   
/*     */ 
/*     */ 
/* 530 */                   SubscriptionSchedulerImpl.this.manager.prepareDownload(download, new Subscription[] { subs }, new SubscriptionResult[] { result });
/*     */                   
/* 532 */                   subs.addAssociation(hash);
/*     */                   
/* 534 */                   if ((auto_start) && (stop_override))
/*     */                   {
/* 536 */                     download.restart();
/*     */                   }
/*     */                   
/* 539 */                   result.setRead(true);
/*     */                   
/* 541 */                   success = true;
/*     */                   
/* 543 */                   if (tried_ref_switch)
/*     */                   {
/* 545 */                     subs.getHistory().setDownloadWithReferer(use_ref);
/*     */                   }
/*     */                 }
/*     */                 catch (Throwable e) {
/* 549 */                   SubscriptionSchedulerImpl.this.log(subs.getName() + ": Failed to download result " + dl, e);
/*     */                   
/* 551 */                   if (((e instanceof TOTorrentException)) && (!tried_ref_switch))
/*     */                   {
/* 553 */                     use_ref = !use_ref;
/*     */                     
/* 555 */                     tried_ref_switch = true;
/*     */                     
/* 557 */                     retry = true;
/*     */                     
/* 559 */                     SubscriptionSchedulerImpl.this.log(subs.getName() + ": Retrying " + (use_ref ? "with referer" : "without referer"));
/*     */                   }
/*     */                 }
/*     */                 finally {
/* 563 */                   TorrentUtils.setTLSDescription(null);
/*     */                 }
/*     */               }
/*     */             }
/*     */           } finally { try { int rad;
/*     */               long rad_millis;
/*     */               long time_found;
/* 570 */               if (!success)
/*     */               {
/* 572 */                 if ((dl.startsWith("azplug:")) || (dl.startsWith("chat:")))
/*     */                 {
/*     */ 
/*     */ 
/* 576 */                   result.setRead(true);
/*     */                 }
/*     */                 else
/*     */                 {
/* 580 */                   int rad = SubscriptionSchedulerImpl.this.manager.getAutoDownloadMarkReadAfterDays();
/*     */                   
/* 582 */                   if (rad > 0)
/*     */                   {
/* 584 */                     long rad_millis = rad * 24 * 60 * 60 * 1000L;
/*     */                     
/* 586 */                     long time_found = result.getTimeFound();
/*     */                     
/*     */ 
/*     */ 
/* 590 */                     if ((time_found > 0L) && (time_found + rad_millis < SystemTime.getCurrentTime()))
/*     */                     {
/* 592 */                       SubscriptionSchedulerImpl.this.log(subs.getName() + ": result expired, marking as read - " + result.getID());
/*     */                       
/* 594 */                       result.setRead(true);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 601 */               Debug.out(e);
/*     */             }
/*     */             
/* 604 */             synchronized (SubscriptionSchedulerImpl.this.active_result_downloaders)
/*     */             {
/* 606 */               SubscriptionSchedulerImpl.this.active_result_downloaders.remove(key);
/*     */             }
/*     */             
/* 609 */             SubscriptionSchedulerImpl.this.calculateSchedule();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void calculateSchedule()
/*     */   {
/* 619 */     Subscription[] subs = this.manager.getSubscriptions(true);
/*     */     
/* 621 */     synchronized (this)
/*     */     {
/* 623 */       if (!this.schedulng_permitted)
/*     */       {
/* 625 */         return;
/*     */       }
/*     */       
/* 628 */       if (this.schedule_in_progress)
/*     */       {
/* 630 */         return;
/*     */       }
/*     */       
/* 633 */       long next_ready_time = Long.MAX_VALUE;
/*     */       
/* 635 */       Subscription next_ready_subs = null;
/*     */       
/* 637 */       for (int i = 0; i < subs.length; i++)
/*     */       {
/* 639 */         Subscription sub = subs[i];
/*     */         
/* 641 */         SubscriptionHistory history = sub.getHistory();
/*     */         
/* 643 */         if (history.isEnabled())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 648 */           long next_scan = getNextScan(sub);
/*     */           
/* 650 */           sub.setUserData(SCHEDULER_NEXT_SCAN_KEY, new Long(next_scan));
/*     */           
/* 652 */           if (next_scan < next_ready_time)
/*     */           {
/* 654 */             next_ready_time = next_scan;
/*     */             
/* 656 */             next_ready_subs = sub;
/*     */           }
/*     */         }
/*     */       }
/* 660 */       long old_when = 0L;
/*     */       
/* 662 */       if (this.schedule_event != null)
/*     */       {
/* 664 */         old_when = this.schedule_event.getWhen();
/*     */         
/* 666 */         this.schedule_event.cancel();
/*     */         
/* 668 */         this.schedule_event = null;
/*     */       }
/*     */       
/* 671 */       if (next_ready_time < Long.MAX_VALUE)
/*     */       {
/* 673 */         long now = SystemTime.getCurrentTime();
/*     */         
/* 675 */         if ((now < this.last_schedule) || (now - this.last_schedule < 30000L))
/*     */         {
/*     */ 
/* 678 */           if (next_ready_time - now < 30000L)
/*     */           {
/* 680 */             next_ready_time = now + 30000L;
/*     */           }
/*     */         }
/*     */         
/* 684 */         if (next_ready_time < now)
/*     */         {
/* 686 */           next_ready_time = now;
/*     */         }
/*     */         
/* 689 */         String sched_str = "Calculate : old_time=" + new SimpleDateFormat().format(new Date(old_when)) + ", new_time=" + new SimpleDateFormat().format(new Date(next_ready_time)) + ", next_sub=" + next_ready_subs.getName();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 695 */         if ((this.last_sched_str == null) || (!sched_str.equals(this.last_sched_str)))
/*     */         {
/* 697 */           this.last_sched_str = sched_str;
/*     */           
/* 699 */           log(sched_str);
/*     */         }
/*     */         
/* 702 */         this.schedule_event = SimpleTimer.addEvent("SS:Scheduler", next_ready_time, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/* 711 */             synchronized (SubscriptionSchedulerImpl.this)
/*     */             {
/* 713 */               if (SubscriptionSchedulerImpl.this.schedule_in_progress)
/*     */               {
/* 715 */                 return;
/*     */               }
/*     */               
/* 718 */               SubscriptionSchedulerImpl.this.schedule_in_progress = true;
/*     */               
/* 720 */               SubscriptionSchedulerImpl.this.last_schedule = SystemTime.getCurrentTime();
/*     */               
/* 722 */               SubscriptionSchedulerImpl.this.schedule_event = null;
/*     */             }
/*     */             
/* 725 */             new AEThread2("SS:Sched", true)
/*     */             {
/*     */               public void run()
/*     */               {
/*     */                 try
/*     */                 {
/* 731 */                   SubscriptionSchedulerImpl.this.schedule();
/*     */                 }
/*     */                 finally
/*     */                 {
/* 735 */                   synchronized (SubscriptionSchedulerImpl.this)
/*     */                   {
/* 737 */                     SubscriptionSchedulerImpl.this.schedule_in_progress = false;
/*     */                   }
/*     */                   
/* 740 */                   SubscriptionSchedulerImpl.this.calculateSchedule();
/*     */                 }
/*     */               }
/*     */             }.start();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void schedule()
/*     */   {
/* 753 */     Subscription[] subs = this.manager.getSubscriptions(true);
/*     */     
/* 755 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 757 */     subs = (Subscription[])subs.clone();
/*     */     
/* 759 */     synchronized (this)
/*     */     {
/* 761 */       Arrays.sort(subs, new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int compare(Subscription s1, Subscription s2)
/*     */         {
/*     */ 
/*     */ 
/* 770 */           Long l1 = (Long)s1.getUserData(SubscriptionSchedulerImpl.SCHEDULER_NEXT_SCAN_KEY);
/* 771 */           Long l2 = (Long)s2.getUserData(SubscriptionSchedulerImpl.SCHEDULER_NEXT_SCAN_KEY);
/*     */           
/* 773 */           if (l1 == l2)
/*     */           {
/* 775 */             return 0;
/*     */           }
/* 777 */           if (l1 == null)
/*     */           {
/* 779 */             return 1;
/*     */           }
/* 781 */           if (l2 == null)
/*     */           {
/* 783 */             return -1;
/*     */           }
/*     */           
/*     */ 
/* 787 */           long diff = l1.longValue() - l2.longValue();
/*     */           
/* 789 */           if (diff < 0L)
/*     */           {
/* 791 */             return -1;
/*     */           }
/* 793 */           if (diff < 0L)
/*     */           {
/* 795 */             return 1;
/*     */           }
/*     */           
/* 798 */           return 0;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 805 */     for (int i = 0; i < subs.length; i++)
/*     */     {
/* 807 */       Subscription sub = subs[i];
/*     */       
/* 809 */       SubscriptionHistory history = sub.getHistory();
/*     */       
/* 811 */       if (history.isEnabled())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 816 */         synchronized (this)
/*     */         {
/* 818 */           Long scan_due = (Long)sub.getUserData(SCHEDULER_NEXT_SCAN_KEY);
/*     */           
/* 820 */           if (scan_due == null) {
/*     */             continue;
/*     */           }
/*     */           
/*     */ 
/* 825 */           long diff = now - scan_due.longValue();
/*     */           
/* 827 */           if (diff < -10000L) {
/*     */             continue;
/*     */           }
/*     */           
/*     */ 
/* 832 */           sub.setUserData(SCHEDULER_NEXT_SCAN_KEY, null);
/*     */         }
/*     */         
/* 835 */         long last_scan = history.getLastScanTime();
/*     */         
/* 837 */         boolean download_attempted = true;
/*     */         try
/*     */         {
/* 840 */           download_attempted = download(sub, true);
/*     */         }
/*     */         catch (Throwable e) {}finally
/*     */         {
/*     */           long new_last_scan;
/*     */           long new_last_scan;
/* 846 */           if (download_attempted)
/*     */           {
/* 848 */             long new_last_scan = history.getLastScanTime();
/*     */             
/* 850 */             if (new_last_scan == last_scan)
/*     */             {
/* 852 */               scanFailed(sub);
/*     */             }
/*     */             else
/*     */             {
/* 856 */               scanSuccess(sub);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getNextScan(Subscription sub)
/*     */   {
/* 867 */     SubscriptionHistory history = sub.getHistory();
/*     */     
/* 869 */     Long fail_count = (Long)sub.getUserData(SCHEDULER_FAILED_SCAN_CONSEC_KEY);
/*     */     
/* 871 */     if (fail_count != null)
/*     */     {
/* 873 */       long fail_time = ((Long)sub.getUserData(SCHEDULER_FAILED_SCAN_TIME_KEY)).longValue();
/*     */       
/* 875 */       long fails = fail_count.longValue();
/*     */       
/* 877 */       long backoff = 600000L;
/*     */       
/* 879 */       for (int i = 1; i < fails; i++)
/*     */       {
/* 881 */         backoff <<= 1;
/*     */         
/* 883 */         if (backoff > 28800000L)
/*     */         {
/* 885 */           backoff = 28800000L;
/*     */           
/* 887 */           break;
/*     */         }
/*     */       }
/*     */       
/* 891 */       return fail_time + backoff;
/*     */     }
/*     */     
/* 894 */     return history.getNextScanTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void scanSuccess(Subscription sub)
/*     */   {
/* 901 */     sub.setUserData(SCHEDULER_FAILED_SCAN_CONSEC_KEY, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void scanFailed(Subscription sub)
/*     */   {
/* 908 */     sub.setUserData(SCHEDULER_FAILED_SCAN_TIME_KEY, new Long(SystemTime.getCurrentTime()));
/*     */     
/* 910 */     Long fail_count = (Long)sub.getUserData(SCHEDULER_FAILED_SCAN_CONSEC_KEY);
/*     */     
/* 912 */     if (fail_count == null)
/*     */     {
/* 914 */       fail_count = new Long(1L);
/*     */     }
/*     */     else
/*     */     {
/* 918 */       fail_count = new Long(fail_count.longValue() + 1L);
/*     */     }
/*     */     
/* 921 */     sub.setUserData(SCHEDULER_FAILED_SCAN_CONSEC_KEY, fail_count);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 928 */     this.manager.log("Scheduler: " + str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, Throwable e)
/*     */   {
/* 936 */     this.manager.log("Scheduler: " + str, e);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void subscriptionAdded(Subscription subscription)
/*     */   {
/* 943 */     calculateSchedule();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void subscriptionChanged(Subscription subscription)
/*     */   {
/* 950 */     calculateSchedule();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void subscriptionSelected(Subscription subscription) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void subscriptionRemoved(Subscription subscription)
/*     */   {
/* 963 */     calculateSchedule();
/*     */   }
/*     */   
/*     */   public void associationsChanged(byte[] association_hash) {}
/*     */   
/*     */   public void subscriptionRequested(URL url, Map<String, Object> options) {}
/*     */   
/*     */   public void subscriptionRequested(SearchProvider sp, Map<String, Object> properties)
/*     */     throws SubscriptionException
/*     */   {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionSchedulerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */