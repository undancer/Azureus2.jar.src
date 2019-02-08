/*      */ package org.gudy.azureus2.core3.tracker.client.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerDataProvider;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory.DataProvider;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.bt.TRTrackerBTAnnouncerImpl;
/*      */ import org.gudy.azureus2.core3.tracker.client.impl.dht.TRTrackerDHTAnnouncerImpl;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
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
/*      */ public class TRTrackerAnnouncerMuxer
/*      */   extends TRTrackerAnnouncerImpl
/*      */ {
/*      */   private static final int ACT_CHECK_INIT_DELAY = 2500;
/*      */   private static final int ACT_CHECK_INTERIM_DELAY = 10000;
/*      */   private static final int ACT_CHECK_IDLE_DELAY = 30000;
/*      */   private static final int ACT_CHECK_SEEDING_SHORT_DELAY = 60000;
/*      */   private static final int ACT_CHECK_SEEDING_LONG_DELAY = 180000;
/*      */   private TRTrackerAnnouncerFactory.DataProvider f_provider;
/*      */   private boolean is_manual;
/*   66 */   private final long create_time = SystemTime.getMonotonousTime();
/*      */   
/*   68 */   private final CopyOnWriteList<TRTrackerAnnouncerHelper> announcers = new CopyOnWriteList();
/*   69 */   private final Set<TRTrackerAnnouncerHelper> activated = new HashSet();
/*      */   private long last_activation_time;
/*   71 */   private final Set<String> failed_urls = new HashSet();
/*      */   
/*      */   private volatile TimerEvent event;
/*      */   
/*      */   private TRTrackerAnnouncerDataProvider provider;
/*      */   
/*      */   private String ip_override;
/*      */   
/*      */   private boolean complete;
/*      */   
/*      */   private boolean stopped;
/*      */   private boolean destroyed;
/*      */   private String[] current_networks;
/*      */   private TRTrackerAnnouncerHelper last_best_active;
/*      */   private long last_best_active_set_time;
/*   86 */   final Map<String, StatusSummary> recent_responses = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private TRTrackerAnnouncerResponse last_response_informed;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TRTrackerAnnouncerMuxer(TOTorrent _torrent, TRTrackerAnnouncerFactory.DataProvider _f_provider, boolean _manual)
/*      */     throws TRTrackerAnnouncerException
/*      */   {
/*   99 */     super(_torrent);
/*      */     try
/*      */     {
/*  102 */       this.last_response_informed = new TRTrackerAnnouncerResponseImpl(null, _torrent.getHashWrapper(), 0, 60L, "Initialising");
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*  106 */       Logger.log(new LogEvent(_torrent, LOGID, "Torrent hash retrieval fails", e));
/*      */       
/*  108 */       throw new TRTrackerAnnouncerException("TRTrackerAnnouncer: URL encode fails");
/*      */     }
/*      */     
/*  111 */     this.is_manual = _manual;
/*  112 */     this.f_provider = _f_provider;
/*      */     
/*  114 */     split(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void split(boolean first_time)
/*      */     throws TRTrackerAnnouncerException
/*      */   {
/*  123 */     String[] networks = this.f_provider == null ? null : this.f_provider.getNetworks();
/*      */     
/*  125 */     boolean force_recreate = false;
/*      */     
/*  127 */     if (!first_time)
/*      */     {
/*  129 */       if (this.current_networks != networks)
/*      */       {
/*  131 */         if ((this.current_networks == null) || (networks == null))
/*      */         {
/*  133 */           force_recreate = true;
/*      */ 
/*      */ 
/*      */         }
/*  137 */         else if (networks.length != this.current_networks.length)
/*      */         {
/*  139 */           force_recreate = true;
/*      */         }
/*      */         else
/*      */         {
/*  143 */           for (String net1 : this.current_networks)
/*      */           {
/*  145 */             boolean match = false;
/*      */             
/*  147 */             for (String net2 : networks)
/*      */             {
/*  149 */               if (net1 == net2)
/*      */               {
/*  151 */                 match = true;
/*      */               }
/*      */             }
/*      */             
/*  155 */             if (!match)
/*      */             {
/*  157 */               force_recreate = true;
/*      */               
/*  159 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  167 */     this.current_networks = networks;
/*      */     
/*  169 */     TRTrackerAnnouncerHelper to_activate = null;
/*      */     
/*  171 */     synchronized (this)
/*      */     {
/*  173 */       if ((this.stopped) || (this.destroyed))
/*      */       {
/*  175 */         return;
/*      */       }
/*      */       
/*  178 */       TOTorrent torrent = getTorrent();
/*      */       
/*  180 */       TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*      */       
/*      */ 
/*      */ 
/*  184 */       if (sets.length == 0)
/*      */       {
/*  186 */         sets = new TOTorrentAnnounceURLSet[] { torrent.getAnnounceURLGroup().createAnnounceURLSet(new URL[] { torrent.getAnnounceURL() }) };
/*      */       }
/*      */       else
/*      */       {
/*  190 */         boolean found_decentralised = false;
/*  191 */         boolean modified = false;
/*      */         
/*  193 */         for (int i = 0; i < sets.length; i++)
/*      */         {
/*  195 */           TOTorrentAnnounceURLSet set = sets[i];
/*      */           
/*  197 */           URL[] urls = (URL[])set.getAnnounceURLs().clone();
/*      */           
/*  199 */           for (int j = 0; j < urls.length; j++)
/*      */           {
/*  201 */             URL u = urls[j];
/*      */             
/*  203 */             if ((u != null) && (TorrentUtils.isDecentralised(u)))
/*      */             {
/*  205 */               if (found_decentralised)
/*      */               {
/*  207 */                 modified = true;
/*      */                 
/*  209 */                 urls[j] = null;
/*      */               }
/*      */               else
/*      */               {
/*  213 */                 found_decentralised = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  219 */         if (modified)
/*      */         {
/*  221 */           List<TOTorrentAnnounceURLSet> s_list = new ArrayList();
/*      */           
/*  223 */           for (TOTorrentAnnounceURLSet set : sets)
/*      */           {
/*  225 */             URL[] urls = set.getAnnounceURLs();
/*      */             
/*  227 */             List<URL> u_list = new ArrayList(urls.length);
/*      */             
/*  229 */             for (URL u : urls)
/*      */             {
/*  231 */               if (u != null)
/*      */               {
/*  233 */                 u_list.add(u);
/*      */               }
/*      */             }
/*      */             
/*  237 */             if (u_list.size() > 0)
/*      */             {
/*  239 */               s_list.add(torrent.getAnnounceURLGroup().createAnnounceURLSet((URL[])u_list.toArray(new URL[u_list.size()])));
/*      */             }
/*      */           }
/*      */           
/*  243 */           sets = (TOTorrentAnnounceURLSet[])s_list.toArray(new TOTorrentAnnounceURLSet[s_list.size()]);
/*      */         }
/*      */       }
/*      */       
/*  247 */       List<TOTorrentAnnounceURLSet[]> new_sets = new ArrayList();
/*      */       
/*  249 */       if ((this.is_manual) || (sets.length < 2))
/*      */       {
/*  251 */         new_sets.add(sets);
/*      */       }
/*      */       else
/*      */       {
/*  255 */         List<TOTorrentAnnounceURLSet> list = new ArrayList(Arrays.asList(sets));
/*      */         
/*      */ 
/*      */ 
/*  259 */         while (list.size() > 0)
/*      */         {
/*  261 */           TOTorrentAnnounceURLSet set1 = (TOTorrentAnnounceURLSet)list.remove(0);
/*      */           
/*  263 */           boolean done = false;
/*      */           
/*  265 */           URL[] urls1 = set1.getAnnounceURLs();
/*      */           
/*  267 */           if (urls1.length == 1)
/*      */           {
/*  269 */             URL url1 = urls1[0];
/*      */             
/*  271 */             String prot1 = url1.getProtocol().toLowerCase();
/*  272 */             String host1 = url1.getHost();
/*      */             
/*  274 */             for (int i = 0; i < list.size(); i++)
/*      */             {
/*  276 */               TOTorrentAnnounceURLSet set2 = (TOTorrentAnnounceURLSet)list.get(i);
/*      */               
/*  278 */               URL[] urls2 = set2.getAnnounceURLs();
/*      */               
/*  280 */               if (urls2.length == 1)
/*      */               {
/*  282 */                 URL url2 = urls2[0];
/*      */                 
/*  284 */                 String prot2 = url2.getProtocol().toLowerCase();
/*  285 */                 String host2 = url2.getHost();
/*      */                 
/*  287 */                 if (host1.equals(host2))
/*      */                 {
/*  289 */                   if (((prot1.equals("udp")) && (prot2.startsWith("http"))) || ((prot2.equals("udp")) && (prot1.startsWith("http"))))
/*      */                   {
/*      */ 
/*  292 */                     list.remove(i);
/*      */                     
/*  294 */                     new_sets.add(new TOTorrentAnnounceURLSet[] { set1, set2 });
/*      */                     
/*  296 */                     done = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  303 */           if (!done)
/*      */           {
/*  305 */             new_sets.add(new TOTorrentAnnounceURLSet[] { set1 });
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  312 */       Iterator<TOTorrentAnnounceURLSet[]> ns_it = new_sets.iterator();
/*      */       
/*      */ 
/*      */ 
/*  316 */       List<TRTrackerAnnouncerHelper> existing_announcers = new ArrayList(this.announcers.getList());
/*      */       
/*  318 */       List<TRTrackerAnnouncerHelper> new_announcers = new ArrayList();
/*      */       
/*      */ 
/*      */ 
/*  322 */       if (!force_recreate)
/*      */       {
/*  324 */         while (ns_it.hasNext())
/*      */         {
/*  326 */           TOTorrentAnnounceURLSet[] ns = (TOTorrentAnnounceURLSet[])ns_it.next();
/*      */           
/*  328 */           Iterator<TRTrackerAnnouncerHelper> a_it = existing_announcers.iterator();
/*      */           
/*  330 */           while (a_it.hasNext())
/*      */           {
/*  332 */             TRTrackerAnnouncerHelper a = (TRTrackerAnnouncerHelper)a_it.next();
/*      */             
/*  334 */             TOTorrentAnnounceURLSet[] os = a.getAnnounceSets();
/*      */             
/*  336 */             if (same(ns, os))
/*      */             {
/*  338 */               ns_it.remove();
/*  339 */               a_it.remove();
/*      */               
/*  341 */               new_announcers.add(a);
/*      */               
/*  343 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  351 */       TRTrackerAnnouncerHelper existing_dht_announcer = null;
/*  352 */       TOTorrentAnnounceURLSet[] new_dht_set = null;
/*      */       
/*  354 */       ns_it = new_sets.iterator();
/*      */       
/*  356 */       while (ns_it.hasNext())
/*      */       {
/*  358 */         TOTorrentAnnounceURLSet[] x = (TOTorrentAnnounceURLSet[])ns_it.next();
/*      */         
/*  360 */         if (TorrentUtils.isDecentralised(x[0].getAnnounceURLs()[0]))
/*      */         {
/*  362 */           new_dht_set = x;
/*      */           
/*  364 */           ns_it.remove();
/*      */           
/*  366 */           break;
/*      */         }
/*      */       }
/*      */       
/*  370 */       Iterator<TRTrackerAnnouncerHelper> an_it = existing_announcers.iterator();
/*      */       
/*  372 */       while (an_it.hasNext())
/*      */       {
/*  374 */         TRTrackerAnnouncerHelper a = (TRTrackerAnnouncerHelper)an_it.next();
/*      */         
/*  376 */         TOTorrentAnnounceURLSet[] x = a.getAnnounceSets();
/*      */         
/*  378 */         if (TorrentUtils.isDecentralised(x[0].getAnnounceURLs()[0]))
/*      */         {
/*  380 */           existing_dht_announcer = a;
/*      */           
/*  382 */           an_it.remove();
/*      */           
/*  384 */           break;
/*      */         }
/*      */       }
/*      */       
/*  388 */       if ((existing_dht_announcer != null) && (new_dht_set != null))
/*      */       {
/*  390 */         new_announcers.add(existing_dht_announcer);
/*      */       }
/*  392 */       else if (existing_dht_announcer != null)
/*      */       {
/*  394 */         this.activated.remove(existing_dht_announcer);
/*      */         
/*  396 */         existing_dht_announcer.destroy();
/*      */       }
/*  398 */       else if (new_dht_set != null)
/*      */       {
/*  400 */         TRTrackerAnnouncerHelper a = create(torrent, networks, new_dht_set);
/*      */         
/*  402 */         new_announcers.add(a);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  407 */       ns_it = new_sets.iterator();
/*      */       
/*  409 */       while (ns_it.hasNext())
/*      */       {
/*  411 */         TOTorrentAnnounceURLSet[] s = (TOTorrentAnnounceURLSet[])ns_it.next();
/*      */         
/*  413 */         TRTrackerAnnouncerHelper a = create(torrent, networks, s);
/*      */         
/*  415 */         new_announcers.add(a);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  420 */       Iterator<TRTrackerAnnouncerHelper> a_it = this.announcers.iterator();
/*      */       
/*  422 */       while (a_it.hasNext())
/*      */       {
/*  424 */         TRTrackerAnnouncerHelper a = (TRTrackerAnnouncerHelper)a_it.next();
/*      */         
/*  426 */         if (!new_announcers.contains(a))
/*      */         {
/*  428 */           a_it.remove();
/*      */           try
/*      */           {
/*  431 */             if ((this.activated.contains(a)) && (torrent.getPrivate()) && ((a instanceof TRTrackerBTAnnouncerImpl)))
/*      */             {
/*      */ 
/*      */ 
/*  435 */               URL url = a.getTrackerURL();
/*      */               
/*  437 */               if (url != null)
/*      */               {
/*  439 */                 forceStop((TRTrackerBTAnnouncerImpl)a, networks, url);
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/*  444 */             if (Logger.isEnabled()) {
/*  445 */               Logger.log(new LogEvent(getTorrent(), LOGID, "Deactivating " + getString(a.getAnnounceSets())));
/*      */             }
/*      */             
/*  448 */             this.activated.remove(a);
/*      */             
/*  450 */             a.destroy();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  455 */       a_it = new_announcers.iterator();
/*      */       
/*  457 */       while (a_it.hasNext())
/*      */       {
/*  459 */         TRTrackerAnnouncerHelper a = (TRTrackerAnnouncerHelper)a_it.next();
/*      */         
/*  461 */         if (!this.announcers.contains(a))
/*      */         {
/*  463 */           this.announcers.add(a);
/*      */         }
/*      */       }
/*      */       
/*  467 */       if ((!this.is_manual) && (this.announcers.size() > 0))
/*      */       {
/*  469 */         if (this.activated.size() == 0)
/*      */         {
/*  471 */           TRTrackerAnnouncerHelper a = (TRTrackerAnnouncerHelper)this.announcers.get(0);
/*      */           
/*  473 */           if (Logger.isEnabled()) {
/*  474 */             Logger.log(new LogEvent(getTorrent(), LOGID, "Activating " + getString(a.getAnnounceSets())));
/*      */           }
/*      */           
/*  477 */           this.activated.add(a);
/*      */           
/*  479 */           this.last_activation_time = SystemTime.getMonotonousTime();
/*      */           
/*  481 */           if (this.provider != null)
/*      */           {
/*  483 */             to_activate = a;
/*      */           }
/*      */         }
/*      */         
/*  487 */         setupActivationCheck(2500);
/*      */       }
/*      */     }
/*      */     
/*  491 */     if (to_activate != null)
/*      */     {
/*  493 */       if (this.complete)
/*      */       {
/*  495 */         to_activate.complete(true);
/*      */       }
/*      */       else
/*      */       {
/*  499 */         to_activate.update(false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setupActivationCheck(int delay)
/*      */   {
/*  508 */     if (this.announcers.size() > this.activated.size())
/*      */     {
/*  510 */       this.event = SimpleTimer.addEvent("TRMuxer:check", SystemTime.getOffsetTime(delay), new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  519 */           TRTrackerAnnouncerMuxer.this.checkActivation(false);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkActivation(boolean force)
/*      */   {
/*  529 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/*  533 */       if ((this.destroyed) || (this.stopped) || (this.announcers.size() <= this.activated.size())) {
/*      */         return;
/*      */       }
/*      */       
/*      */       int next_check_delay;
/*      */       
/*      */       int next_check_delay;
/*  540 */       if (this.provider == null)
/*      */       {
/*  542 */         next_check_delay = 2500;
/*      */       }
/*      */       else
/*      */       {
/*  546 */         boolean activate = force;
/*      */         
/*  548 */         boolean seeding = this.provider.getRemaining() == 0L;
/*      */         int next_check_delay;
/*  550 */         if ((seeding) && (this.activated.size() > 0))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  555 */           int connected = this.provider.getConnectedConnectionCount();
/*      */           int next_check_delay;
/*  557 */           if (connected < 1)
/*      */           {
/*  559 */             activate = SystemTime.getMonotonousTime() - this.last_activation_time >= 60000L;
/*      */             
/*  561 */             next_check_delay = 60000;
/*      */           } else { int next_check_delay;
/*  563 */             if (connected < 3)
/*      */             {
/*  565 */               next_check_delay = 180000;
/*      */             }
/*      */             else
/*      */             {
/*  569 */               next_check_delay = 0;
/*      */             }
/*      */           }
/*      */         } else {
/*  573 */           int allowed = this.provider.getMaxNewConnectionsAllowed("");
/*  574 */           int pending = this.provider.getPendingConnectionCount();
/*  575 */           int connected = this.provider.getConnectedConnectionCount();
/*      */           
/*  577 */           int online = 0;
/*      */           
/*  579 */           for (TRTrackerAnnouncerHelper a : this.activated)
/*      */           {
/*  581 */             TRTrackerAnnouncerResponse response = a.getLastResponse();
/*      */             
/*  583 */             if ((response != null) && (response.getStatus() == 2))
/*      */             {
/*      */ 
/*  586 */               online++;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           int next_check_delay;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  601 */           if (online == 0)
/*      */           {
/*  603 */             activate = true;
/*      */             
/*      */ 
/*      */ 
/*  607 */             next_check_delay = 2500;
/*      */           }
/*      */           else
/*      */           {
/*  611 */             int potential = connected + pending;
/*      */             int next_check_delay;
/*  613 */             if (potential < 10)
/*      */             {
/*      */ 
/*      */ 
/*  617 */               activate = true;
/*      */               
/*  619 */               next_check_delay = 2500;
/*      */             } else { int next_check_delay;
/*  621 */               if ((allowed < 0) || ((allowed >= 5) && (pending < 3 * allowed / 4)))
/*      */               {
/*      */ 
/*      */ 
/*  625 */                 activate = true;
/*      */                 
/*  627 */                 next_check_delay = 10000;
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  632 */                 next_check_delay = 30000;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  637 */         if (activate)
/*      */         {
/*  639 */           for (TRTrackerAnnouncerHelper a : this.announcers)
/*      */           {
/*  641 */             if (!this.activated.contains(a))
/*      */             {
/*  643 */               if (Logger.isEnabled()) {
/*  644 */                 Logger.log(new LogEvent(getTorrent(), LOGID, "Activating " + getString(a.getAnnounceSets())));
/*      */               }
/*      */               
/*  647 */               this.activated.add(a);
/*      */               
/*  649 */               this.last_activation_time = SystemTime.getMonotonousTime();
/*      */               
/*  651 */               if (this.complete)
/*      */               {
/*  653 */                 a.complete(true); break;
/*      */               }
/*      */               
/*      */ 
/*  657 */               a.update(false);
/*      */               
/*      */ 
/*  660 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  666 */       if (next_check_delay > 0)
/*      */       {
/*  668 */         setupActivationCheck(next_check_delay);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getString(TOTorrentAnnounceURLSet[] sets)
/*      */   {
/*  677 */     StringBuilder str = new StringBuilder();
/*      */     
/*  679 */     str.append("[");
/*      */     
/*  681 */     int num1 = 0;
/*      */     
/*  683 */     for (TOTorrentAnnounceURLSet s : sets)
/*      */     {
/*  685 */       if (num1++ > 0) {
/*  686 */         str.append(", ");
/*      */       }
/*      */       
/*  689 */       str.append("[");
/*      */       
/*  691 */       URL[] urls = s.getAnnounceURLs();
/*      */       
/*  693 */       int num2 = 0;
/*      */       
/*  695 */       for (URL u : urls)
/*      */       {
/*  697 */         if (num2++ > 0) {
/*  698 */           str.append(", ");
/*      */         }
/*      */         
/*  701 */         str.append(u.toExternalForm());
/*      */       }
/*      */       
/*  704 */       str.append("]");
/*      */     }
/*      */     
/*  707 */     str.append("]");
/*      */     
/*  709 */     return str.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean same(TOTorrentAnnounceURLSet[] s1, TOTorrentAnnounceURLSet[] s2)
/*      */   {
/*  717 */     boolean res = sameSupport(s1, s2);
/*      */     
/*      */ 
/*      */ 
/*  721 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean sameSupport(TOTorrentAnnounceURLSet[] s1, TOTorrentAnnounceURLSet[] s2)
/*      */   {
/*  729 */     if (s1.length != s2.length)
/*      */     {
/*  731 */       return false;
/*      */     }
/*      */     
/*  734 */     for (int i = 0; i < s1.length; i++)
/*      */     {
/*  736 */       URL[] u1 = s1[i].getAnnounceURLs();
/*  737 */       URL[] u2 = s2[i].getAnnounceURLs();
/*      */       
/*  739 */       if (u1.length != u2.length)
/*      */       {
/*  741 */         return false;
/*      */       }
/*      */       
/*  744 */       if (u1.length == 1)
/*      */       {
/*  746 */         return u1[0].toExternalForm().equals(u2[0].toExternalForm());
/*      */       }
/*      */       
/*  749 */       Set<String> set1 = new HashSet();
/*      */       
/*  751 */       for (URL u : u1)
/*      */       {
/*  753 */         set1.add(u.toExternalForm());
/*      */       }
/*      */       
/*  756 */       Set<String> set2 = new HashSet();
/*      */       
/*  758 */       for (URL u : u2)
/*      */       {
/*  760 */         set2.add(u.toExternalForm());
/*      */       }
/*      */       
/*  763 */       if (!set1.equals(set2))
/*      */       {
/*  765 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  769 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void forceStop(final TRTrackerBTAnnouncerImpl announcer, final String[] networks, final URL url)
/*      */   {
/*  778 */     if (Logger.isEnabled()) {
/*  779 */       Logger.log(new LogEvent(getTorrent(), LOGID, "Force stopping " + url + " as private torrent"));
/*      */     }
/*      */     
/*  782 */     new AEThread2("TRMux:fs", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*  788 */           TRTrackerBTAnnouncerImpl an = new TRTrackerBTAnnouncerImpl(TRTrackerAnnouncerMuxer.this.getTorrent(), new TOTorrentAnnounceURLSet[0], networks, true, TRTrackerAnnouncerMuxer.this.getHelper());
/*      */           
/*      */ 
/*  791 */           an.cloneFrom(announcer);
/*      */           
/*  793 */           an.setTrackerURL(url);
/*      */           
/*  795 */           an.stop(false);
/*      */           
/*  797 */           an.destroy();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private TRTrackerAnnouncerHelper create(TOTorrent torrent, String[] networks, TOTorrentAnnounceURLSet[] sets)
/*      */     throws TRTrackerAnnouncerException
/*      */   {
/*      */     boolean decentralised;
/*      */     
/*      */ 
/*      */ 
/*      */     boolean decentralised;
/*      */     
/*      */ 
/*      */ 
/*  818 */     if (sets.length == 0)
/*      */     {
/*  820 */       decentralised = TorrentUtils.isDecentralised(torrent.getAnnounceURL());
/*      */     }
/*      */     else
/*      */     {
/*  824 */       decentralised = TorrentUtils.isDecentralised(sets[0].getAnnounceURLs()[0]); }
/*      */     TRTrackerAnnouncerHelper announcer;
/*      */     TRTrackerAnnouncerHelper announcer;
/*  827 */     if (decentralised)
/*      */     {
/*  829 */       announcer = new TRTrackerDHTAnnouncerImpl(torrent, networks, this.is_manual, getHelper());
/*      */     }
/*      */     else
/*      */     {
/*  833 */       announcer = new TRTrackerBTAnnouncerImpl(torrent, sets, networks, this.is_manual, getHelper());
/*      */     }
/*      */     
/*  836 */     for (TOTorrentAnnounceURLSet set : sets)
/*      */     {
/*  838 */       URL[] urls = set.getAnnounceURLs();
/*      */       
/*  840 */       for (URL u : urls)
/*      */       {
/*  842 */         String key = u.toExternalForm();
/*      */         
/*  844 */         StatusSummary summary = (StatusSummary)this.recent_responses.get(key);
/*      */         
/*  846 */         if (summary == null)
/*      */         {
/*  848 */           summary = new StatusSummary(announcer, u);
/*      */           
/*  850 */           this.recent_responses.put(key, summary);
/*      */         }
/*      */         else
/*      */         {
/*  854 */           summary.setHelper(announcer);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  859 */     if (this.provider != null)
/*      */     {
/*  861 */       announcer.setAnnounceDataProvider(this.provider);
/*      */     }
/*      */     
/*  864 */     if (this.ip_override != null)
/*      */     {
/*  866 */       announcer.setIPOverride(this.ip_override);
/*      */     }
/*      */     
/*  869 */     return announcer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRTrackerAnnouncerResponse getLastResponse()
/*      */   {
/*  876 */     TRTrackerAnnouncerResponse result = null;
/*      */     
/*  878 */     TRTrackerAnnouncerHelper best = getBestActive();
/*      */     
/*  880 */     if (best != null)
/*      */     {
/*  882 */       result = best.getLastResponse();
/*      */     }
/*      */     
/*  885 */     if (result == null)
/*      */     {
/*  887 */       result = this.last_response_informed;
/*      */     }
/*      */     
/*  890 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informResponse(TRTrackerAnnouncerHelper helper, TRTrackerAnnouncerResponse response)
/*      */   {
/*  900 */     URL url = response.getURL();
/*      */     
/*      */ 
/*      */ 
/*  904 */     if (url != null)
/*      */     {
/*  906 */       synchronized (this)
/*      */       {
/*  908 */         String key = url.toExternalForm();
/*      */         
/*  910 */         StatusSummary summary = (StatusSummary)this.recent_responses.get(key);
/*      */         
/*  912 */         if (summary != null)
/*      */         {
/*  914 */           summary.updateFrom(response);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  919 */     this.last_response_informed = response;
/*      */     
/*      */ 
/*      */ 
/*  923 */     this.last_best_active_set_time = 0L;
/*      */     
/*  925 */     super.informResponse(helper, response);
/*      */     
/*  927 */     if (response.getStatus() != 2)
/*      */     {
/*  929 */       URL u = response.getURL();
/*      */       
/*  931 */       if (u != null)
/*      */       {
/*  933 */         String s = u.toExternalForm();
/*      */         
/*  935 */         synchronized (this.failed_urls)
/*      */         {
/*  937 */           if (this.failed_urls.contains(s))
/*      */           {
/*  939 */             return;
/*      */           }
/*      */           
/*  942 */           this.failed_urls.add(s);
/*      */         }
/*      */       }
/*      */       
/*  946 */       checkActivation(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isManual()
/*      */   {
/*  953 */     return this.is_manual;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAnnounceDataProvider(TRTrackerAnnouncerDataProvider _provider)
/*      */   {
/*      */     List<TRTrackerAnnouncerHelper> to_set;
/*      */     
/*  962 */     synchronized (this)
/*      */     {
/*  964 */       this.provider = _provider;
/*      */       
/*  966 */       to_set = this.announcers.getList();
/*      */     }
/*      */     
/*  969 */     for (Object announcer : to_set)
/*      */     {
/*  971 */       ((TRTrackerAnnouncer)announcer).setAnnounceDataProvider(this.provider);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected TRTrackerAnnouncerHelper getBestActive()
/*      */   {
/*  978 */     long now = SystemTime.getMonotonousTime();
/*      */     
/*  980 */     if (now - this.last_best_active_set_time < 1000L)
/*      */     {
/*  982 */       return this.last_best_active;
/*      */     }
/*      */     
/*  985 */     this.last_best_active = getBestActiveSupport();
/*      */     
/*  987 */     this.last_best_active_set_time = now;
/*      */     
/*  989 */     return this.last_best_active;
/*      */   }
/*      */   
/*      */ 
/*      */   protected TRTrackerAnnouncerHelper getBestActiveSupport()
/*      */   {
/*  995 */     List<TRTrackerAnnouncerHelper> x = this.announcers.getList();
/*      */     
/*  997 */     TRTrackerAnnouncerHelper error_resp = null;
/*      */     
/*  999 */     for (TRTrackerAnnouncerHelper announcer : x)
/*      */     {
/* 1001 */       TRTrackerAnnouncerResponse response = announcer.getLastResponse();
/*      */       
/* 1003 */       if (response != null)
/*      */       {
/* 1005 */         int resp_status = response.getStatus();
/*      */         
/* 1007 */         if (resp_status == 2)
/*      */         {
/* 1009 */           return announcer;
/*      */         }
/* 1011 */         if ((error_resp == null) && (resp_status == 1))
/*      */         {
/* 1013 */           error_resp = announcer;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1018 */     if (error_resp != null)
/*      */     {
/* 1020 */       return error_resp;
/*      */     }
/*      */     
/* 1023 */     if (x.size() > 0)
/*      */     {
/* 1025 */       return (TRTrackerAnnouncerHelper)x.get(0);
/*      */     }
/*      */     
/* 1028 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public URL getTrackerURL()
/*      */   {
/* 1034 */     TRTrackerAnnouncerHelper active = getBestActive();
/*      */     
/* 1036 */     if (active != null)
/*      */     {
/* 1038 */       return active.getTrackerURL();
/*      */     }
/*      */     
/* 1041 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTrackerURL(URL url)
/*      */   {
/* 1048 */     List<List<String>> groups = new ArrayList();
/*      */     
/* 1050 */     List<String> group = new ArrayList();
/*      */     
/* 1052 */     group.add(url.toExternalForm());
/*      */     
/* 1054 */     groups.add(group);
/*      */     
/* 1056 */     TorrentUtils.listToAnnounceGroups(groups, getTorrent());
/*      */     
/* 1058 */     resetTrackerUrl(false);
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetTrackerUrl(boolean shuffle)
/*      */   {
/*      */     try
/*      */     {
/* 1066 */       split(false);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1070 */       Debug.out(e);
/*      */     }
/*      */     
/* 1073 */     for (TRTrackerAnnouncer announcer : this.announcers)
/*      */     {
/* 1075 */       announcer.resetTrackerUrl(shuffle);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setIPOverride(String override)
/*      */   {
/*      */     List<TRTrackerAnnouncerHelper> to_set;
/*      */     
/* 1085 */     synchronized (this)
/*      */     {
/* 1087 */       to_set = this.announcers.getList();
/*      */       
/* 1089 */       this.ip_override = override;
/*      */     }
/*      */     
/* 1092 */     for (Object announcer : to_set)
/*      */     {
/* 1094 */       ((TRTrackerAnnouncer)announcer).setIPOverride(override);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearIPOverride()
/*      */   {
/*      */     List<TRTrackerAnnouncerHelper> to_clear;
/*      */     
/* 1103 */     synchronized (this)
/*      */     {
/* 1105 */       to_clear = this.announcers.getList();
/*      */       
/* 1107 */       this.ip_override = null;
/*      */     }
/*      */     
/* 1110 */     for (Object announcer : to_clear)
/*      */     {
/* 1112 */       ((TRTrackerAnnouncer)announcer).clearIPOverride();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRefreshDelayOverrides(int percentage)
/*      */   {
/* 1120 */     for (TRTrackerAnnouncer announcer : this.announcers)
/*      */     {
/* 1122 */       announcer.setRefreshDelayOverrides(percentage);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTimeUntilNextUpdate()
/*      */   {
/* 1129 */     TRTrackerAnnouncerHelper active = getBestActive();
/*      */     
/* 1131 */     if (active != null)
/*      */     {
/* 1133 */       return active.getTimeUntilNextUpdate();
/*      */     }
/*      */     
/* 1136 */     return Integer.MAX_VALUE;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getLastUpdateTime()
/*      */   {
/* 1142 */     TRTrackerAnnouncerHelper active = getBestActive();
/*      */     
/* 1144 */     if (active != null)
/*      */     {
/* 1146 */       return active.getLastUpdateTime();
/*      */     }
/*      */     
/* 1149 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void update(boolean force)
/*      */   {
/*      */     List<TRTrackerAnnouncerHelper> to_update;
/*      */     
/* 1158 */     synchronized (this)
/*      */     {
/* 1160 */       to_update = this.is_manual ? this.announcers.getList() : new ArrayList(this.activated);
/*      */     }
/*      */     
/* 1163 */     for (Object announcer : to_update)
/*      */     {
/* 1165 */       ((TRTrackerAnnouncer)announcer).update(force);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void complete(boolean already_reported)
/*      */   {
/*      */     List<TRTrackerAnnouncerHelper> to_complete;
/*      */     
/* 1175 */     synchronized (this)
/*      */     {
/* 1177 */       this.complete = true;
/*      */       
/* 1179 */       to_complete = this.is_manual ? this.announcers.getList() : new ArrayList(this.activated);
/*      */     }
/*      */     
/* 1182 */     for (Object announcer : to_complete)
/*      */     {
/* 1184 */       ((TRTrackerAnnouncer)announcer).complete(already_reported);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stop(boolean for_queue)
/*      */   {
/*      */     List<TRTrackerAnnouncerHelper> to_stop;
/*      */     
/* 1194 */     synchronized (this)
/*      */     {
/* 1196 */       this.stopped = true;
/*      */       
/* 1198 */       to_stop = this.is_manual ? this.announcers.getList() : new ArrayList(this.activated);
/*      */       
/* 1200 */       this.activated.clear();
/*      */     }
/*      */     
/* 1203 */     for (Object announcer : to_stop)
/*      */     {
/* 1205 */       ((TRTrackerAnnouncer)announcer).stop(for_queue);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 1212 */     TRTrackerAnnouncerFactoryImpl.destroy(this);
/*      */     
/*      */     List<TRTrackerAnnouncerHelper> to_destroy;
/*      */     
/* 1216 */     synchronized (this)
/*      */     {
/* 1218 */       this.destroyed = true;
/*      */       
/* 1220 */       to_destroy = this.announcers.getList();
/*      */     }
/*      */     
/* 1223 */     for (Object announcer : to_destroy)
/*      */     {
/* 1225 */       ((TRTrackerAnnouncer)announcer).destroy();
/*      */     }
/*      */     
/* 1228 */     TimerEvent ev = this.event;
/*      */     
/* 1230 */     if (ev != null)
/*      */     {
/* 1232 */       ev.cancel();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getStatus()
/*      */   {
/* 1239 */     TRTrackerAnnouncer max_announcer = getBestAnnouncer();
/*      */     
/* 1241 */     return max_announcer == null ? -1 : max_announcer.getStatus();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatusString()
/*      */   {
/* 1247 */     TRTrackerAnnouncer max_announcer = getBestAnnouncer();
/*      */     
/* 1249 */     return max_announcer == null ? "" : max_announcer.getStatusString();
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerAnnouncer getBestAnnouncer()
/*      */   {
/* 1255 */     int max = -1;
/*      */     
/* 1257 */     TRTrackerAnnouncer max_announcer = null;
/*      */     
/* 1259 */     for (TRTrackerAnnouncer announcer : this.announcers)
/*      */     {
/* 1261 */       int status = announcer.getStatus();
/*      */       
/* 1263 */       if (status > max)
/*      */       {
/* 1265 */         max_announcer = announcer;
/* 1266 */         max = status;
/*      */       }
/*      */     }
/*      */     
/* 1270 */     return max_announcer == null ? this : max_announcer;
/*      */   }
/*      */   
/*      */ 
/*      */   public void refreshListeners()
/*      */   {
/* 1276 */     informURLRefresh();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAnnounceResult(DownloadAnnounceResult result)
/*      */   {
/* 1285 */     for (TRTrackerAnnouncer announcer : this.announcers)
/*      */     {
/* 1287 */       if ((announcer instanceof TRTrackerDHTAnnouncerImpl))
/*      */       {
/* 1289 */         announcer.setAnnounceResult(result);
/*      */         
/* 1291 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1298 */     List<TRTrackerAnnouncerHelper> x = this.announcers.getList();
/*      */     
/* 1300 */     if (x.size() > 0)
/*      */     {
/* 1302 */       ((TRTrackerAnnouncerHelper)x.get(0)).setAnnounceResult(result);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getPeerCacheLimit()
/*      */   {
/* 1309 */     synchronized (this)
/*      */     {
/* 1311 */       if (this.activated.size() < this.announcers.size())
/*      */       {
/* 1313 */         return 0;
/*      */       }
/*      */     }
/*      */     
/* 1317 */     if (SystemTime.getMonotonousTime() - this.create_time < 15000L)
/*      */     {
/* 1319 */       return 0;
/*      */     }
/*      */     
/* 1322 */     TRTrackerAnnouncer active = getBestActive();
/*      */     
/* 1324 */     if ((active != null) && (this.provider != null) && (active.getStatus() == 2))
/*      */     {
/* 1326 */       if ((this.provider.getMaxNewConnectionsAllowed("") != 0) && (this.provider.getPendingConnectionCount() == 0))
/*      */       {
/*      */ 
/* 1329 */         return 5;
/*      */       }
/*      */       
/*      */ 
/* 1333 */       return 0;
/*      */     }
/*      */     
/*      */ 
/* 1337 */     return 10;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TrackerPeerSource getTrackerPeerSource(TOTorrentAnnounceURLSet set)
/*      */   {
/* 1344 */     URL[] urls = set.getAnnounceURLs();
/*      */     
/* 1346 */     final String[] url_strs = new String[urls.length];
/*      */     
/* 1348 */     for (int i = 0; i < urls.length; i++)
/*      */     {
/* 1350 */       url_strs[i] = urls[i].toExternalForm();
/*      */     }
/*      */     
/* 1353 */     new TrackerPeerSource()
/*      */     {
/*      */       private TRTrackerAnnouncerMuxer.StatusSummary _summary;
/*      */       
/*      */       private boolean enabled;
/*      */       
/*      */       private long fixup_time;
/*      */       
/*      */       private TRTrackerAnnouncerMuxer.StatusSummary fixup()
/*      */       {
/* 1363 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 1365 */         if (now - this.fixup_time > 1000L)
/*      */         {
/* 1367 */           long most_recent = 0L;
/* 1368 */           TRTrackerAnnouncerMuxer.StatusSummary summary = null;
/*      */           
/* 1370 */           synchronized (TRTrackerAnnouncerMuxer.this)
/*      */           {
/* 1372 */             for (String str : url_strs)
/*      */             {
/* 1374 */               TRTrackerAnnouncerMuxer.StatusSummary s = (TRTrackerAnnouncerMuxer.StatusSummary)TRTrackerAnnouncerMuxer.this.recent_responses.get(str);
/*      */               
/* 1376 */               if (s != null)
/*      */               {
/* 1378 */                 if ((summary == null) || (s.getTime() > most_recent))
/*      */                 {
/* 1380 */                   summary = s;
/* 1381 */                   most_recent = s.getTime();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1387 */           if (TRTrackerAnnouncerMuxer.this.provider != null)
/*      */           {
/* 1389 */             this.enabled = TRTrackerAnnouncerMuxer.this.provider.isPeerSourceEnabled("Tracker");
/*      */           }
/*      */           
/* 1392 */           if (summary != null)
/*      */           {
/* 1394 */             this._summary = summary;
/*      */           }
/*      */           
/* 1397 */           this.fixup_time = now;
/*      */         }
/*      */         
/* 1400 */         return this._summary;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getType()
/*      */       {
/* 1406 */         return 1;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getName()
/*      */       {
/* 1412 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1414 */         if (summary != null)
/*      */         {
/* 1416 */           String str = summary.getURL().toExternalForm();
/*      */           
/* 1418 */           int pos = str.indexOf('?');
/*      */           
/* 1420 */           if (pos != -1)
/*      */           {
/* 1422 */             str = str.substring(0, pos);
/*      */           }
/*      */           
/* 1425 */           return str;
/*      */         }
/*      */         
/* 1428 */         return url_strs[0];
/*      */       }
/*      */       
/*      */ 
/*      */       public int getStatus()
/*      */       {
/* 1434 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1436 */         if (!this.enabled)
/*      */         {
/* 1438 */           return 1;
/*      */         }
/*      */         
/* 1441 */         if (summary != null)
/*      */         {
/* 1443 */           return summary.getStatus();
/*      */         }
/*      */         
/* 1446 */         return 3;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getStatusString()
/*      */       {
/* 1452 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1454 */         if ((summary != null) && (this.enabled))
/*      */         {
/* 1456 */           return summary.getStatusString();
/*      */         }
/*      */         
/* 1459 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSeedCount()
/*      */       {
/* 1465 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1467 */         if (summary != null)
/*      */         {
/* 1469 */           return summary.getSeedCount();
/*      */         }
/*      */         
/* 1472 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLeecherCount()
/*      */       {
/* 1478 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1480 */         if (summary != null)
/*      */         {
/* 1482 */           return summary.getLeecherCount();
/*      */         }
/*      */         
/* 1485 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getCompletedCount()
/*      */       {
/* 1491 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1493 */         if (summary != null)
/*      */         {
/* 1495 */           return summary.getCompletedCount();
/*      */         }
/*      */         
/* 1498 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getPeers()
/*      */       {
/* 1504 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1506 */         if (summary != null)
/*      */         {
/* 1508 */           return summary.getPeers();
/*      */         }
/*      */         
/* 1511 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getLastUpdate()
/*      */       {
/* 1517 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1519 */         if (summary != null)
/*      */         {
/* 1521 */           long time = summary.getTime();
/*      */           
/* 1523 */           if (time == 0L)
/*      */           {
/* 1525 */             return 0;
/*      */           }
/*      */           
/* 1528 */           long elapsed = SystemTime.getMonotonousTime() - time;
/*      */           
/* 1530 */           return (int)((SystemTime.getCurrentTime() - elapsed) / 1000L);
/*      */         }
/*      */         
/* 1533 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getSecondsToUpdate()
/*      */       {
/* 1539 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1541 */         if (summary != null)
/*      */         {
/* 1543 */           return summary.getSecondsToUpdate();
/*      */         }
/*      */         
/* 1546 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getInterval()
/*      */       {
/* 1552 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1554 */         if (summary != null)
/*      */         {
/* 1556 */           return summary.getInterval();
/*      */         }
/*      */         
/* 1559 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getMinInterval()
/*      */       {
/* 1565 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1567 */         if ((summary != null) && (this.enabled))
/*      */         {
/* 1569 */           return summary.getMinInterval();
/*      */         }
/*      */         
/* 1572 */         return -1;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isUpdating()
/*      */       {
/* 1578 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1580 */         if ((summary != null) && (this.enabled))
/*      */         {
/* 1582 */           return summary.isUpdating();
/*      */         }
/*      */         
/* 1585 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean canManuallyUpdate()
/*      */       {
/* 1591 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1593 */         if (summary == null)
/*      */         {
/* 1595 */           return false;
/*      */         }
/*      */         
/* 1598 */         return summary.canManuallyUpdate();
/*      */       }
/*      */       
/*      */ 
/*      */       public void manualUpdate()
/*      */       {
/* 1604 */         TRTrackerAnnouncerMuxer.StatusSummary summary = fixup();
/*      */         
/* 1606 */         if (summary != null)
/*      */         {
/* 1608 */           summary.manualUpdate();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean canDelete()
/*      */       {
/* 1615 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */       public void delete()
/*      */       {
/* 1621 */         Debug.out("derp");
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 1630 */     for (TRTrackerAnnouncer announcer : this.announcers)
/*      */     {
/* 1632 */       announcer.generateEvidence(writer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class StatusSummary
/*      */   {
/*      */     private TRTrackerAnnouncerHelper helper;
/*      */     
/*      */     private long time;
/*      */     private final URL url;
/*      */     private int status;
/*      */     private String status_str;
/* 1645 */     private int seeds = -1;
/* 1646 */     private int leechers = -1;
/* 1647 */     private int peers = -1;
/* 1648 */     private int completed = -1;
/*      */     
/*      */ 
/*      */     private int interval;
/*      */     
/*      */     private int min_interval;
/*      */     
/*      */ 
/*      */     protected StatusSummary(TRTrackerAnnouncerHelper _helper, URL _url)
/*      */     {
/* 1658 */       this.helper = _helper;
/* 1659 */       this.url = _url;
/*      */       
/* 1661 */       this.status = 3;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setHelper(TRTrackerAnnouncerHelper _helper)
/*      */     {
/* 1668 */       this.helper = _helper;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void updateFrom(TRTrackerAnnouncerResponse response)
/*      */     {
/* 1675 */       this.time = SystemTime.getMonotonousTime();
/*      */       
/* 1677 */       int state = response.getStatus();
/*      */       
/* 1679 */       if (state == 2)
/*      */       {
/* 1681 */         this.status = 5;
/*      */         
/* 1683 */         this.seeds = response.getScrapeCompleteCount();
/* 1684 */         this.leechers = response.getScrapeIncompleteCount();
/* 1685 */         this.completed = response.getScrapeDownloadedCount();
/* 1686 */         this.peers = response.getPeers().length;
/*      */       }
/*      */       else
/*      */       {
/* 1690 */         this.status = 6;
/*      */       }
/*      */       
/* 1693 */       this.status_str = response.getStatusString();
/*      */       
/* 1695 */       this.interval = ((int)this.helper.getInterval());
/* 1696 */       this.min_interval = ((int)this.helper.getMinInterval());
/*      */     }
/*      */     
/*      */ 
/*      */     public long getTime()
/*      */     {
/* 1702 */       return this.time;
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getURL()
/*      */     {
/* 1708 */       return this.url;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getStatus()
/*      */     {
/* 1714 */       return this.status;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getStatusString()
/*      */     {
/* 1720 */       return this.status_str;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getSeedCount()
/*      */     {
/* 1726 */       return this.seeds;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getLeecherCount()
/*      */     {
/* 1732 */       return this.leechers;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getCompletedCount()
/*      */     {
/* 1738 */       return this.completed;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getPeers()
/*      */     {
/* 1744 */       return this.peers;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isUpdating()
/*      */     {
/* 1750 */       return this.helper.isUpdating();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getInterval()
/*      */     {
/* 1756 */       return this.interval;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMinInterval()
/*      */     {
/* 1762 */       return this.min_interval;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getSecondsToUpdate()
/*      */     {
/* 1768 */       return this.helper.getTimeUntilNextUpdate();
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean canManuallyUpdate()
/*      */     {
/* 1774 */       return SystemTime.getCurrentTime() / 1000L - this.helper.getLastUpdateTime() >= 60L;
/*      */     }
/*      */     
/*      */ 
/*      */     public void manualUpdate()
/*      */     {
/* 1780 */       this.helper.update(true);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/client/impl/TRTrackerAnnouncerMuxer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */