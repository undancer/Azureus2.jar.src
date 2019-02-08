/*     */ package org.gudy.azureus2.core3.download.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*     */ import com.aelitis.azureus.plugins.tracker.dht.DHTTrackerPlugin;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerAvailability;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerDataProvider;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory.DataProvider;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class DownloadManagerAvailabilityImpl
/*     */   implements DownloadManagerAvailability
/*     */ {
/*  54 */   private final List<TrackerPeerSource> peer_sources = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   private TRTrackerAnnouncer tracker_client;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DownloadManagerAvailabilityImpl(TOTorrent to_torrent, List<List<String>> updated_trackers, String[] _enabled_peer_sources, final String[] _enabled_networks)
/*     */   {
/*  65 */     if (to_torrent == null)
/*     */     {
/*  67 */       return;
/*     */     }
/*     */     
/*  70 */     Set<String> enabled_peer_sources = new HashSet(Arrays.asList(_enabled_peer_sources));
/*  71 */     Set<String> enabled_networks = new HashSet(Arrays.asList(_enabled_networks));
/*     */     
/*  73 */     if (enabled_peer_sources.contains("Tracker"))
/*     */     {
/*     */       TOTorrentAnnounceURLSet[] sets;
/*     */       TOTorrentAnnounceURLSet[] sets;
/*  77 */       if (updated_trackers == null)
/*     */       {
/*  79 */         sets = to_torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*     */       }
/*     */       else
/*     */       {
/*  83 */         sets = TorrentUtils.listToAnnounceSets(updated_trackers, to_torrent);
/*     */         try
/*     */         {
/*  86 */           to_torrent = TorrentUtils.cloneTorrent(to_torrent);
/*     */           
/*  88 */           TorrentUtils.setMemoryOnly(to_torrent, true);
/*     */           
/*  90 */           to_torrent.getAnnounceURLGroup().setAnnounceURLSets(sets);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  94 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */       
/*  98 */       if (sets.length == 0)
/*     */       {
/* 100 */         sets = new TOTorrentAnnounceURLSet[] { to_torrent.getAnnounceURLGroup().createAnnounceURLSet(new URL[] { to_torrent.getAnnounceURL() }) };
/*     */       }
/*     */       try
/*     */       {
/* 104 */         this.tracker_client = TRTrackerAnnouncerFactory.create(to_torrent, new TRTrackerAnnouncerFactory.DataProvider()
/*     */         {
/*     */ 
/*     */ 
/*     */           public String[] getNetworks()
/*     */           {
/*     */ 
/*     */ 
/* 112 */             return _enabled_networks;
/*     */           }
/*     */           
/* 115 */         });
/* 116 */         final long torrent_size = to_torrent.getSize();
/*     */         
/* 118 */         this.tracker_client.setAnnounceDataProvider(new TRTrackerAnnouncerDataProvider()
/*     */         {
/*     */ 
/*     */           public String getName()
/*     */           {
/*     */ 
/* 124 */             return "Availability checker";
/*     */           }
/*     */           
/*     */ 
/*     */           public long getTotalSent()
/*     */           {
/* 130 */             return 0L;
/*     */           }
/*     */           
/*     */ 
/*     */           public long getTotalReceived()
/*     */           {
/* 136 */             return 0L;
/*     */           }
/*     */           
/*     */ 
/*     */           public long getRemaining()
/*     */           {
/* 142 */             return torrent_size;
/*     */           }
/*     */           
/*     */ 
/*     */           public long getFailedHashCheck()
/*     */           {
/* 148 */             return 0L;
/*     */           }
/*     */           
/*     */ 
/*     */           public String getExtensions()
/*     */           {
/* 154 */             return null;
/*     */           }
/*     */           
/*     */ 
/*     */           public int getMaxNewConnectionsAllowed(String network)
/*     */           {
/* 160 */             return 1;
/*     */           }
/*     */           
/*     */ 
/*     */           public int getPendingConnectionCount()
/*     */           {
/* 166 */             return 0;
/*     */           }
/*     */           
/*     */ 
/*     */           public int getConnectedConnectionCount()
/*     */           {
/* 172 */             return 0;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public int getUploadSpeedKBSec(boolean estimate)
/*     */           {
/* 179 */             return 0;
/*     */           }
/*     */           
/*     */ 
/*     */           public int getCryptoLevel()
/*     */           {
/* 185 */             return 0;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public boolean isPeerSourceEnabled(String peer_source)
/*     */           {
/* 192 */             return true;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void setPeerSources(String[] allowed_sources) {}
/* 201 */         });
/* 202 */         this.tracker_client.update(true);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 206 */         Debug.out(e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 211 */       for (final TOTorrentAnnounceURLSet set : sets)
/*     */       {
/* 213 */         final URL[] urls = set.getAnnounceURLs();
/*     */         
/* 215 */         if ((urls.length != 0) && (!TorrentUtils.isDecentralised(urls[0])))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 220 */           this.peer_sources.add(new TrackerPeerSource()
/*     */           {
/*     */             private TrackerPeerSource _delegate;
/*     */             
/*     */             private TRTrackerAnnouncer ta;
/*     */             
/*     */             private long ta_fixup;
/*     */             
/*     */             private long last_scrape_fixup_time;
/*     */             
/*     */             private Object[] last_scrape;
/*     */             
/*     */             private TrackerPeerSource fixup()
/*     */             {
/* 234 */               long now = SystemTime.getMonotonousTime();
/*     */               
/* 236 */               if (now - this.ta_fixup > 1000L)
/*     */               {
/* 238 */                 TRTrackerAnnouncer current_ta = DownloadManagerAvailabilityImpl.this.tracker_client;
/*     */                 
/* 240 */                 if (current_ta == this.ta)
/*     */                 {
/* 242 */                   if ((current_ta != null) && (this._delegate == null))
/*     */                   {
/* 244 */                     this._delegate = current_ta.getTrackerPeerSource(set);
/*     */                   }
/*     */                 }
/*     */                 else {
/* 248 */                   if (current_ta == null)
/*     */                   {
/* 250 */                     this._delegate = null;
/*     */                   }
/*     */                   else
/*     */                   {
/* 254 */                     this._delegate = current_ta.getTrackerPeerSource(set);
/*     */                   }
/*     */                   
/* 257 */                   this.ta = current_ta;
/*     */                 }
/*     */                 
/* 260 */                 this.ta_fixup = now;
/*     */               }
/*     */               
/* 263 */               return this._delegate;
/*     */             }
/*     */             
/*     */ 
/*     */             protected Object[] getScrape()
/*     */             {
/* 269 */               long now = SystemTime.getMonotonousTime();
/*     */               
/* 271 */               if ((now - this.last_scrape_fixup_time > 30000L) || (this.last_scrape == null))
/*     */               {
/*     */ 
/*     */ 
/* 275 */                 this.last_scrape = new Object[] { Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1), "" };
/*     */                 
/* 277 */                 this.last_scrape_fixup_time = now;
/*     */               }
/*     */               
/* 280 */               return this.last_scrape;
/*     */             }
/*     */             
/*     */ 
/*     */             public int getType()
/*     */             {
/* 286 */               return 1;
/*     */             }
/*     */             
/*     */ 
/*     */             public String getName()
/*     */             {
/* 292 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 294 */               if (delegate == null)
/*     */               {
/* 296 */                 return urls[0].toExternalForm();
/*     */               }
/*     */               
/* 299 */               return delegate.getName();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getStatus()
/*     */             {
/* 305 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 307 */               if (delegate == null)
/*     */               {
/* 309 */                 return 2;
/*     */               }
/*     */               
/* 312 */               return delegate.getStatus();
/*     */             }
/*     */             
/*     */ 
/*     */             public String getStatusString()
/*     */             {
/* 318 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 320 */               if (delegate == null)
/*     */               {
/* 322 */                 return (String)getScrape()[5];
/*     */               }
/*     */               
/* 325 */               return delegate.getStatusString();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getSeedCount()
/*     */             {
/* 331 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 333 */               if (delegate == null)
/*     */               {
/* 335 */                 return ((Integer)getScrape()[0]).intValue();
/*     */               }
/*     */               
/* 338 */               int seeds = delegate.getSeedCount();
/*     */               
/* 340 */               if (seeds < 0)
/*     */               {
/* 342 */                 seeds = ((Integer)getScrape()[0]).intValue();
/*     */               }
/*     */               
/* 345 */               return seeds;
/*     */             }
/*     */             
/*     */ 
/*     */             public int getLeecherCount()
/*     */             {
/* 351 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 353 */               if (delegate == null)
/*     */               {
/* 355 */                 return ((Integer)getScrape()[1]).intValue();
/*     */               }
/*     */               
/* 358 */               int leechers = delegate.getLeecherCount();
/*     */               
/* 360 */               if (leechers < 0)
/*     */               {
/* 362 */                 leechers = ((Integer)getScrape()[1]).intValue();
/*     */               }
/*     */               
/* 365 */               return leechers;
/*     */             }
/*     */             
/*     */ 
/*     */             public int getCompletedCount()
/*     */             {
/* 371 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 373 */               if (delegate == null)
/*     */               {
/* 375 */                 return ((Integer)getScrape()[4]).intValue();
/*     */               }
/*     */               
/* 378 */               int comp = delegate.getCompletedCount();
/*     */               
/* 380 */               if (comp < 0)
/*     */               {
/* 382 */                 comp = ((Integer)getScrape()[4]).intValue();
/*     */               }
/*     */               
/* 385 */               return comp;
/*     */             }
/*     */             
/*     */ 
/*     */             public int getPeers()
/*     */             {
/* 391 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 393 */               if (delegate == null)
/*     */               {
/* 395 */                 return -1;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 401 */               if ((delegate.getSeedCount() > 0) || (delegate.getLeecherCount() > 0))
/*     */               {
/* 403 */                 return -1;
/*     */               }
/*     */               
/* 406 */               return delegate.getPeers();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getInterval()
/*     */             {
/* 412 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 414 */               if (delegate == null)
/*     */               {
/* 416 */                 Object[] si = getScrape();
/*     */                 
/* 418 */                 int last = ((Integer)si[2]).intValue();
/* 419 */                 int next = ((Integer)si[3]).intValue();
/*     */                 
/* 421 */                 if ((last > 0) && (next < Integer.MAX_VALUE) && (last < next))
/*     */                 {
/* 423 */                   return next - last;
/*     */                 }
/*     */                 
/* 426 */                 return -1;
/*     */               }
/*     */               
/* 429 */               return delegate.getInterval();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getMinInterval()
/*     */             {
/* 435 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 437 */               if (delegate == null)
/*     */               {
/* 439 */                 return -1;
/*     */               }
/*     */               
/* 442 */               return delegate.getMinInterval();
/*     */             }
/*     */             
/*     */ 
/*     */             public boolean isUpdating()
/*     */             {
/* 448 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 450 */               if (delegate == null)
/*     */               {
/* 452 */                 return false;
/*     */               }
/*     */               
/* 455 */               return delegate.isUpdating();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getLastUpdate()
/*     */             {
/* 461 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 463 */               if (delegate == null)
/*     */               {
/* 465 */                 return ((Integer)getScrape()[2]).intValue();
/*     */               }
/*     */               
/* 468 */               return delegate.getLastUpdate();
/*     */             }
/*     */             
/*     */ 
/*     */             public int getSecondsToUpdate()
/*     */             {
/* 474 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 476 */               if (delegate == null)
/*     */               {
/* 478 */                 return -1;
/*     */               }
/*     */               
/* 481 */               return delegate.getSecondsToUpdate();
/*     */             }
/*     */             
/*     */ 
/*     */             public boolean canManuallyUpdate()
/*     */             {
/* 487 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 489 */               if (delegate == null)
/*     */               {
/* 491 */                 return false;
/*     */               }
/*     */               
/* 494 */               return delegate.canManuallyUpdate();
/*     */             }
/*     */             
/*     */ 
/*     */             public void manualUpdate()
/*     */             {
/* 500 */               TrackerPeerSource delegate = fixup();
/*     */               
/* 502 */               if (delegate != null)
/*     */               {
/* 504 */                 delegate.manualUpdate();
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */             public boolean canDelete()
/*     */             {
/* 511 */               return false;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             public void delete() {}
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 522 */     Torrent torrent = PluginCoreUtils.wrap(to_torrent);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 527 */       ExternalSeedPlugin esp = DownloadManagerController.getExternalSeedPlugin();
/*     */       
/* 529 */       if (esp != null)
/*     */       {
/* 531 */         TrackerPeerSource ext_ps = esp.getTrackerPeerSource(torrent);
/*     */         
/* 533 */         if (ext_ps.getSeedCount() > 0)
/*     */         {
/* 535 */           this.peer_sources.add(ext_ps);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 541 */     if ((enabled_peer_sources.contains("DHT")) && (enabled_networks.contains("Public")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 546 */       if (!torrent.isPrivate())
/*     */       {
/*     */         try
/*     */         {
/* 550 */           PluginInterface dht_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(DHTTrackerPlugin.class);
/*     */           
/* 552 */           if (dht_pi != null)
/*     */           {
/* 554 */             this.peer_sources.addAll(Arrays.asList(((DHTTrackerPlugin)dht_pi.getPlugin()).getTrackerPeerSources(torrent)));
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/* 561 */     if (enabled_peer_sources.contains("DHT"))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 566 */       if (!torrent.isPrivate()) {
/*     */         try
/*     */         {
/* 569 */           PluginInterface i2p_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azneti2phelper", true);
/*     */           
/* 571 */           if (i2p_pi != null)
/*     */           {
/* 573 */             IPCInterface ipc = i2p_pi.getIPC();
/*     */             
/* 575 */             Map<String, Object> options = new HashMap();
/*     */             
/* 577 */             options.put("peer_networks", _enabled_networks);
/*     */             
/* 579 */             final int[] lookup_status = { 9, -1, -1, -1 };
/*     */             
/* 581 */             IPCInterface callback = new IPCInterface()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               public Object invoke(String methodName, Object[] params)
/*     */                 throws IPCException
/*     */               {
/*     */ 
/*     */ 
/* 591 */                 if (methodName.equals("statusUpdate"))
/*     */                 {
/* 593 */                   synchronized (lookup_status)
/*     */                   {
/* 595 */                     lookup_status[0] = ((Integer)params[0]).intValue();
/*     */                     
/* 597 */                     if (params.length >= 4)
/*     */                     {
/* 599 */                       lookup_status[1] = ((Integer)params[1]).intValue();
/* 600 */                       lookup_status[2] = ((Integer)params[2]).intValue();
/* 601 */                       lookup_status[3] = ((Integer)params[3]).intValue();
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 
/* 606 */                 return null;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public boolean canInvoke(String methodName, Object[] params)
/*     */               {
/* 614 */                 return true;
/*     */               }
/*     */               
/* 617 */             };
/* 618 */             TrackerPeerSource ps = new TrackerPeerSourceAdapter()
/*     */             {
/*     */ 
/*     */               public int getType()
/*     */               {
/*     */ 
/* 624 */                 return 3;
/*     */               }
/*     */               
/*     */ 
/*     */               public String getName()
/*     */               {
/* 630 */                 return "I2P DHT";
/*     */               }
/*     */               
/*     */               /* Error */
/*     */               public int getStatus()
/*     */               {
/*     */                 // Byte code:
/*     */                 //   0: aload_0
/*     */                 //   1: getfield 39	org/gudy/azureus2/core3/download/impl/DownloadManagerAvailabilityImpl$5:val$lookup_status	[I
/*     */                 //   4: dup
/*     */                 //   5: astore_1
/*     */                 //   6: monitorenter
/*     */                 //   7: aload_0
/*     */                 //   8: getfield 39	org/gudy/azureus2/core3/download/impl/DownloadManagerAvailabilityImpl$5:val$lookup_status	[I
/*     */                 //   11: iconst_0
/*     */                 //   12: iaload
/*     */                 //   13: aload_1
/*     */                 //   14: monitorexit
/*     */                 //   15: ireturn
/*     */                 //   16: astore_2
/*     */                 //   17: aload_1
/*     */                 //   18: monitorexit
/*     */                 //   19: aload_2
/*     */                 //   20: athrow
/*     */                 // Line number table:
/*     */                 //   Java source line #636	-> byte code offset #0
/*     */                 //   Java source line #638	-> byte code offset #7
/*     */                 //   Java source line #639	-> byte code offset #16
/*     */                 // Local variable table:
/*     */                 //   start	length	slot	name	signature
/*     */                 //   0	21	0	this	5
/*     */                 //   5	13	1	Ljava/lang/Object;	Object
/*     */                 //   16	4	2	localObject1	Object
/*     */                 // Exception table:
/*     */                 //   from	to	target	type
/*     */                 //   7	15	16	finally
/*     */                 //   16	19	16	finally
/*     */               }
/*     */               
/*     */               public int getSeedCount()
/*     */               {
/* 645 */                 synchronized (lookup_status)
/*     */                 {
/* 647 */                   int seeds = lookup_status[1];
/* 648 */                   int peers = lookup_status[3];
/*     */                   
/* 650 */                   if ((seeds == 0) && (peers > 0))
/*     */                   {
/* 652 */                     return -1;
/*     */                   }
/*     */                   
/* 655 */                   return seeds;
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */               public int getLeecherCount()
/*     */               {
/* 662 */                 synchronized (lookup_status)
/*     */                 {
/* 664 */                   int leechers = lookup_status[2];
/* 665 */                   int peers = lookup_status[3];
/*     */                   
/* 667 */                   if ((leechers == 0) && (peers > 0))
/*     */                   {
/* 669 */                     return -1;
/*     */                   }
/*     */                   
/* 672 */                   return leechers;
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */               public int getPeers()
/*     */               {
/* 679 */                 synchronized (lookup_status)
/*     */                 {
/* 681 */                   int peers = lookup_status[3];
/*     */                   
/* 683 */                   return peers == 0 ? -1 : peers;
/*     */                 }
/*     */                 
/*     */               }
/*     */               
/* 688 */             };
/* 689 */             ipc.invoke("lookupTorrent", new Object[] { "Availability lookup for '" + torrent.getName() + "'", torrent.getHash(), options, callback });
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 698 */             this.peer_sources.add(ps);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public List<TrackerPeerSource> getTrackerPeerSources()
/*     */   {
/* 709 */     return this.peer_sources;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 715 */     if (this.tracker_client != null)
/*     */     {
/* 717 */       this.tracker_client.destroy();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerAvailabilityImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */