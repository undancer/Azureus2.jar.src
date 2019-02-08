/*     */ package com.aelitis.azureus.plugins.extseed;
/*     */ 
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*     */ import com.aelitis.azureus.plugins.extseed.impl.getright.ExternalSeedReaderFactoryGetRight;
/*     */ import com.aelitis.azureus.plugins.extseed.impl.webseed.ExternalSeedReaderFactoryWebSeed;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*     */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.Monitor;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExternalSeedPlugin
/*     */   implements Plugin, DownloadManagerListener
/*     */ {
/*  52 */   private static ExternalSeedReaderFactory[] factories = { new ExternalSeedReaderFactoryGetRight(), new ExternalSeedReaderFactoryWebSeed() };
/*     */   
/*     */ 
/*     */   private PluginInterface plugin_interface;
/*     */   
/*     */   private DownloadManagerStats dm_stats;
/*     */   
/*     */   private UITextField status_field;
/*     */   
/*     */   private LoggerChannel log;
/*     */   
/*  63 */   private Random random = new Random();
/*     */   
/*  65 */   private Map download_map = new HashMap();
/*     */   
/*     */   private Monitor download_mon;
/*     */   
/*     */ 
/*     */   public static void load(PluginInterface plugin_interface)
/*     */   {
/*  72 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  73 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "External Seed");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */   {
/*  80 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  82 */     this.dm_stats = this.plugin_interface.getDownloadManager().getStats();
/*     */     
/*  84 */     this.log = this.plugin_interface.getLogger().getTimeStampedChannel("External Seeds");
/*     */     
/*  86 */     final BasicPluginViewModel view_model = this.plugin_interface.getUIManager().createBasicPluginViewModel("Plugin.extseed.name");
/*     */     
/*     */ 
/*     */ 
/*  90 */     view_model.getActivity().setVisible(false);
/*  91 */     view_model.getProgress().setVisible(false);
/*     */     
/*  93 */     this.log.addListener(new LoggerChannelListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void messageLogged(int type, String content)
/*     */       {
/*     */ 
/*     */ 
/* 101 */         view_model.getLogArea().appendText(content + "\n");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageLogged(String str, Throwable error)
/*     */       {
/* 109 */         if (str.length() > 0) {
/* 110 */           view_model.getLogArea().appendText(str + "\n");
/*     */         }
/*     */         
/* 113 */         StringWriter sw = new StringWriter();
/*     */         
/* 115 */         PrintWriter pw = new PrintWriter(sw);
/*     */         
/* 117 */         error.printStackTrace(pw);
/*     */         
/* 119 */         pw.flush();
/*     */         
/* 121 */         view_model.getLogArea().appendText(sw.toString() + "\n");
/*     */       }
/*     */       
/* 124 */     });
/* 125 */     this.status_field = view_model.getStatus();
/*     */     
/* 127 */     setStatus("Initialising");
/*     */     
/* 129 */     this.download_mon = this.plugin_interface.getUtilities().getMonitor();
/*     */     
/* 131 */     Utilities utilities = this.plugin_interface.getUtilities();
/*     */     
/* 133 */     DelayedTask dt = this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 138 */         AEThread2 t = new AEThread2("ExternalSeedInitialise", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 144 */             ExternalSeedPlugin.this.setStatus("Running");
/*     */             
/* 146 */             ExternalSeedPlugin.this.plugin_interface.getDownloadManager().addListener(ExternalSeedPlugin.this);
/*     */           }
/*     */           
/* 149 */         };
/* 150 */         t.setPriority(1);
/*     */         
/* 152 */         t.start();
/*     */       }
/*     */       
/*     */ 
/* 156 */     });
/* 157 */     dt.queue();
/*     */     
/* 159 */     UTTimer timer = utilities.createTimer("ExternalPeerScheduler", true);
/*     */     
/* 161 */     timer.addPeriodicEvent(5000L, new UTTimerEventPerformer()
/*     */     {
/*     */ 
/*     */       public void perform(UTTimerEvent event)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 170 */           Iterator it = ExternalSeedPlugin.this.download_map.values().iterator();
/*     */           
/* 172 */           while (it.hasNext())
/*     */           {
/* 174 */             List peers = ExternalSeedPlugin.this.randomiseList((List)it.next());
/*     */             
/* 176 */             for (int i = 0; i < peers.size(); i++)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 181 */               if (((ExternalSeedPeer)peers.get(i)).checkConnection()) {
/*     */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     });
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
/*     */ 
/*     */   public void downloadAdded(Download download)
/*     */   {
/* 205 */     Torrent torrent = download.getTorrent();
/*     */     
/* 207 */     if (torrent == null)
/*     */     {
/* 209 */       return;
/*     */     }
/*     */     
/* 212 */     List peers = new ArrayList();
/*     */     
/* 214 */     for (int i = 0; i < factories.length; i++)
/*     */     {
/*     */ 
/* 217 */       String attributeID = "no-ext-seeds-" + factories[i].getClass().getSimpleName();
/* 218 */       TorrentAttribute attribute = this.plugin_interface.getTorrentManager().getPluginAttribute(attributeID);
/*     */       
/* 220 */       boolean noExternalSeeds = download.getBooleanAttribute(attribute);
/* 221 */       if (!noExternalSeeds)
/*     */       {
/*     */ 
/*     */ 
/* 225 */         ExternalSeedReader[] x = factories[i].getSeedReaders(this, download);
/*     */         
/* 227 */         if (x.length == 0) {
/* 228 */           download.setBooleanAttribute(attribute, true);
/*     */         }
/*     */         else {
/* 231 */           for (int j = 0; j < x.length; j++)
/*     */           {
/* 233 */             ExternalSeedReader reader = x[j];
/*     */             
/* 235 */             ExternalSeedPeer peer = new ExternalSeedPeer(this, download, reader);
/*     */             
/* 237 */             peers.add(peer);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 242 */     addPeers(download, peers);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void downloadChanged(Download download)
/*     */   {
/* 249 */     downloadRemoved(download);
/*     */     
/* 251 */     downloadAdded(download);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<ExternalSeedPeer> addSeed(Download download, Map config)
/*     */   {
/* 259 */     Torrent torrent = download.getTorrent();
/*     */     
/* 261 */     List<ExternalSeedPeer> peers = new ArrayList();
/*     */     
/* 263 */     if (torrent != null)
/*     */     {
/* 265 */       for (int i = 0; i < factories.length; i++)
/*     */       {
/* 267 */         String attributeID = "no-ext-seeds-" + factories[i].getClass().getSimpleName();
/* 268 */         TorrentAttribute attribute = this.plugin_interface.getTorrentManager().getPluginAttribute(attributeID);
/*     */         
/* 270 */         ExternalSeedReader[] x = factories[i].getSeedReaders(this, download, config);
/*     */         
/* 272 */         download.setBooleanAttribute(attribute, x.length == 0);
/*     */         
/* 274 */         for (int j = 0; j < x.length; j++)
/*     */         {
/* 276 */           ExternalSeedReader reader = x[j];
/*     */           
/* 278 */           ExternalSeedPeer peer = new ExternalSeedPeer(this, download, reader);
/*     */           
/* 280 */           peers.add(peer);
/*     */         }
/*     */       }
/*     */       
/* 284 */       addPeers(download, peers);
/*     */     }
/*     */     
/* 287 */     return peers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addPeers(final Download download, List _peers)
/*     */   {
/* 295 */     List peers = new ArrayList();
/*     */     
/* 297 */     peers.addAll(_peers);
/*     */     
/* 299 */     if (peers.size() > 0)
/*     */     {
/* 301 */       boolean add_listener = false;
/*     */       try
/*     */       {
/* 304 */         this.download_mon.enter();
/*     */         
/* 306 */         List existing_peers = (List)this.download_map.get(download);
/*     */         
/* 308 */         if (existing_peers == null)
/*     */         {
/* 310 */           add_listener = true;
/*     */           
/* 312 */           existing_peers = new ArrayList();
/*     */           
/* 314 */           this.download_map.put(download, existing_peers);
/*     */         }
/*     */         
/* 317 */         Iterator it = peers.iterator();
/*     */         
/* 319 */         while (it.hasNext())
/*     */         {
/* 321 */           ExternalSeedPeer peer = (ExternalSeedPeer)it.next();
/*     */           
/* 323 */           boolean skip = false;
/*     */           
/* 325 */           for (int j = 0; j < existing_peers.size(); j++)
/*     */           {
/* 327 */             ExternalSeedPeer existing_peer = (ExternalSeedPeer)existing_peers.get(j);
/*     */             
/* 329 */             if (existing_peer.sameAs(peer))
/*     */             {
/* 331 */               skip = true;
/*     */               
/* 333 */               break;
/*     */             }
/*     */           }
/*     */           
/* 337 */           if (skip)
/*     */           {
/* 339 */             it.remove();
/*     */           }
/*     */           else
/*     */           {
/* 343 */             log(download.getName() + " found seed " + peer.getName());
/*     */             
/*     */ 
/* 346 */             existing_peers.add(peer);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 351 */         setStatus("Running: Downloads with external seeds = " + this.download_map.size());
/*     */       }
/*     */       finally
/*     */       {
/* 355 */         this.download_mon.exit();
/*     */       }
/*     */       
/* 358 */       if (add_listener)
/*     */       {
/* 360 */         download.addPeerListener(new DownloadPeerListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void peerManagerAdded(Download download, PeerManager peer_manager)
/*     */           {
/*     */ 
/*     */ 
/* 368 */             List existing_peers = getPeers();
/*     */             
/* 370 */             if (existing_peers == null)
/*     */             {
/* 372 */               return;
/*     */             }
/*     */             
/* 375 */             for (int i = 0; i < existing_peers.size(); i++)
/*     */             {
/* 377 */               ExternalSeedPeer peer = (ExternalSeedPeer)existing_peers.get(i);
/*     */               
/* 379 */               peer.setManager(peer_manager);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           public void peerManagerRemoved(Download download, PeerManager peer_manager)
/*     */           {
/* 388 */             List existing_peers = getPeers();
/*     */             
/* 390 */             if (existing_peers == null)
/*     */             {
/* 392 */               return;
/*     */             }
/*     */             
/* 395 */             for (int i = 0; i < existing_peers.size(); i++)
/*     */             {
/* 397 */               ExternalSeedPeer peer = (ExternalSeedPeer)existing_peers.get(i);
/*     */               
/* 399 */               peer.setManager(null);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */           protected List getPeers()
/*     */           {
/* 406 */             List existing_peers = null;
/*     */             try
/*     */             {
/* 409 */               ExternalSeedPlugin.this.download_mon.enter();
/*     */               
/* 411 */               List temp = (List)ExternalSeedPlugin.this.download_map.get(download);
/*     */               
/* 413 */               if (temp != null)
/*     */               {
/* 415 */                 existing_peers = new ArrayList(temp.size());
/*     */                 
/* 417 */                 existing_peers.addAll(temp);
/*     */               }
/*     */             }
/*     */             finally {
/* 421 */               ExternalSeedPlugin.this.download_mon.exit();
/*     */             }
/*     */             
/* 424 */             return existing_peers;
/*     */           }
/*     */           
/*     */         });
/*     */       }
/*     */       else
/*     */       {
/* 431 */         PeerManager existing_pm = download.getPeerManager();
/*     */         
/* 433 */         if (existing_pm != null)
/*     */         {
/* 435 */           for (int i = 0; i < peers.size(); i++)
/*     */           {
/* 437 */             ExternalSeedPeer peer = (ExternalSeedPeer)peers.get(i);
/*     */             
/* 439 */             if (peer.getManager() == null)
/*     */             {
/* 441 */               peer.setManager(existing_pm);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removePeer(ExternalSeedPeer peer)
/*     */   {
/* 453 */     Download download = peer.getDownload();
/*     */     try
/*     */     {
/* 456 */       this.download_mon.enter();
/*     */       
/* 458 */       List existing_peers = (List)this.download_map.get(download);
/*     */       
/* 460 */       if (existing_peers != null)
/*     */       {
/* 462 */         if (existing_peers.remove(peer))
/*     */         {
/* 464 */           log(download.getName() + " removed seed " + peer.getName());
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 469 */       this.download_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void downloadRemoved(Download download)
/*     */   {
/*     */     try
/*     */     {
/* 478 */       this.download_mon.enter();
/*     */       
/* 480 */       this.download_map.remove(download);
/*     */       
/* 482 */       setStatus("Running: Downloads with external seeds = " + this.download_map.size());
/*     */     }
/*     */     finally
/*     */     {
/* 486 */       this.download_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ExternalSeedManualPeer[] getManualWebSeeds(Download download)
/*     */   {
/*     */     try
/*     */     {
/* 495 */       this.download_mon.enter();
/*     */       
/* 497 */       List peers = (List)this.download_map.get(download);
/*     */       
/* 499 */       if (peers == null)
/*     */       {
/* 501 */         return new ExternalSeedManualPeer[0];
/*     */       }
/*     */       
/* 504 */       ExternalSeedManualPeer[] result = new ExternalSeedManualPeer[peers.size()];
/*     */       
/* 506 */       for (int i = 0; i < peers.size(); i++)
/*     */       {
/* 508 */         result[i] = new ExternalSeedManualPeer((ExternalSeedPeer)peers.get(i));
/*     */       }
/*     */       
/* 511 */       return result;
/*     */     }
/*     */     finally
/*     */     {
/* 515 */       this.download_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ExternalSeedReader[] getManualWebSeeds(Torrent torrent)
/*     */   {
/* 523 */     List<ExternalSeedReader> result = new ArrayList();
/*     */     
/* 525 */     for (int i = 0; i < factories.length; i++)
/*     */     {
/* 527 */       ExternalSeedReader[] peers = factories[i].getSeedReaders(this, torrent);
/*     */       
/* 529 */       result.addAll(Arrays.asList(peers));
/*     */     }
/*     */     
/* 532 */     return (ExternalSeedReader[])result.toArray(new ExternalSeedReader[result.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerPeerSource getTrackerPeerSource(final Download download)
/*     */   {
/* 539 */     new TrackerPeerSourceAdapter()
/*     */     {
/*     */       private long fixup_time;
/*     */       
/*     */       private ExternalSeedManualPeer[] peers;
/*     */       
/*     */       private boolean running;
/*     */       
/*     */ 
/*     */       public int getType()
/*     */       {
/* 550 */         return 2;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getStatus()
/*     */       {
/* 556 */         fixup();
/*     */         
/* 558 */         if (this.running)
/*     */         {
/* 560 */           return this.peers.length == 0 ? 8 : 7;
/*     */         }
/*     */         
/*     */ 
/* 564 */         return 2;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public String getName()
/*     */       {
/* 571 */         fixup();
/*     */         
/* 573 */         if (this.peers.length == 0)
/*     */         {
/* 575 */           return "";
/*     */         }
/*     */         
/* 578 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 580 */         for (ExternalSeedManualPeer peer : this.peers)
/*     */         {
/* 582 */           if (sb.length() > 0)
/*     */           {
/* 584 */             sb.append(", ");
/*     */           }
/*     */           
/* 587 */           String str = peer.getDelegate().getURL().toExternalForm();
/*     */           
/* 589 */           int pos = str.indexOf('?');
/*     */           
/* 591 */           if (pos != -1)
/*     */           {
/* 593 */             str = str.substring(0, pos);
/*     */           }
/*     */           
/* 596 */           sb.append(str);
/*     */         }
/*     */         
/* 599 */         return sb.toString();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getSeedCount()
/*     */       {
/* 605 */         fixup();
/*     */         
/* 607 */         if (this.running)
/*     */         {
/* 609 */           return this.peers.length == 0 ? -1 : this.peers.length;
/*     */         }
/*     */         
/*     */ 
/* 613 */         return -1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       protected void fixup()
/*     */       {
/* 620 */         long now = SystemTime.getMonotonousTime();
/*     */         
/* 622 */         if ((this.peers == null) || (now - this.fixup_time > 10000L))
/*     */         {
/* 624 */           this.fixup_time = now;
/*     */           
/* 626 */           this.peers = ExternalSeedPlugin.this.getManualWebSeeds(download);
/*     */           
/* 628 */           int state = download.getState();
/*     */           
/* 630 */           this.running = ((state == 4) || (state == 5));
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerPeerSource getTrackerPeerSource(final Torrent torrent)
/*     */   {
/* 640 */     new TrackerPeerSourceAdapter()
/*     */     {
/*     */ 
/* 643 */       private ExternalSeedReader[] peers = ExternalSeedPlugin.this.getManualWebSeeds(torrent);
/*     */       
/*     */ 
/*     */       public int getType()
/*     */       {
/* 648 */         return 2;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getStatus()
/*     */       {
/* 654 */         return this.peers.length == 0 ? 8 : 7;
/*     */       }
/*     */       
/*     */ 
/*     */       public String getName()
/*     */       {
/* 660 */         if (this.peers.length == 0)
/*     */         {
/* 662 */           return "";
/*     */         }
/*     */         
/* 665 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 667 */         for (ExternalSeedReader peer : this.peers)
/*     */         {
/* 669 */           if (sb.length() > 0)
/*     */           {
/* 671 */             sb.append(", ");
/*     */           }
/*     */           
/* 674 */           String str = peer.getURL().toExternalForm();
/*     */           
/* 676 */           int pos = str.indexOf('?');
/*     */           
/* 678 */           if (pos != -1)
/*     */           {
/* 680 */             str = str.substring(0, pos);
/*     */           }
/*     */           
/* 683 */           sb.append(str);
/*     */         }
/*     */         
/* 686 */         return sb.toString();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getSeedCount()
/*     */       {
/* 692 */         return this.peers.length == 0 ? -1 : this.peers.length;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public int getGlobalDownloadRateBytesPerSec()
/*     */   {
/* 700 */     return this.dm_stats.getDataAndProtocolReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setStatus(String str)
/*     */   {
/* 707 */     this.status_field.setText(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void log(String str)
/*     */   {
/* 714 */     this.log.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void log(String str, Throwable e)
/*     */   {
/* 722 */     this.log.log(str, e);
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getPluginInterface()
/*     */   {
/* 728 */     return this.plugin_interface;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected List randomiseList(List l)
/*     */   {
/* 735 */     if (l.size() < 2)
/*     */     {
/* 737 */       return l;
/*     */     }
/*     */     
/* 740 */     List new_list = new ArrayList();
/*     */     
/* 742 */     for (int i = 0; i < l.size(); i++)
/*     */     {
/* 744 */       new_list.add(this.random.nextInt(new_list.size() + 1), l.get(i));
/*     */     }
/*     */     
/* 747 */     return new_list;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */