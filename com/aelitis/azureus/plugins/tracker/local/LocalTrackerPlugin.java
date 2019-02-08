/*     */ package com.aelitis.azureus.plugins.tracker.local;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerListener;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked;
/*     */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked.TrackTarget;
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*     */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginConfigListener;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
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
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.Formatters;
/*     */ import org.gudy.azureus2.plugins.utils.Monitor;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ 
/*     */ 
/*     */ public class LocalTrackerPlugin
/*     */   implements Plugin, AZInstanceManagerListener, DownloadManagerListener, DownloadListener
/*     */ {
/*     */   private static final String PLUGIN_NAME = "LAN Peer Finder";
/*     */   private static final String PLUGIN_CONFIGSECTION_ID = "Plugin.localtracker.name";
/*     */   private static final long ANNOUNCE_PERIOD = 300000L;
/*     */   private static final long RE_ANNOUNCE_PERIOD = 60000L;
/*     */   private PluginInterface plugin_interface;
/*     */   private AZInstanceManager instance_manager;
/*     */   private boolean active;
/*     */   private TorrentAttribute ta_networks;
/*     */   private TorrentAttribute ta_peer_sources;
/*  76 */   private Map<Download, long[]> downloads = new HashMap();
/*     */   
/*  78 */   private Map<String, Map<String, Long>> track_times = new HashMap();
/*     */   
/*  80 */   private String last_autoadd = "";
/*  81 */   private String last_subnets = "";
/*     */   
/*     */   private BooleanParameter enabled;
/*     */   
/*     */   private long plugin_start_time;
/*     */   
/*     */   private long current_time;
/*     */   
/*     */   private LoggerChannel log;
/*     */   
/*     */   private Monitor mon;
/*  92 */   private AsyncDispatcher dispatcher = new AsyncDispatcher(30000);
/*     */   
/*     */ 
/*     */ 
/*     */   public static void load(PluginInterface plugin_interface)
/*     */   {
/*  98 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  99 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "LAN Peer Finder");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */   {
/* 106 */     this.plugin_interface = _plugin_interface;
/*     */     
/* 108 */     this.ta_networks = this.plugin_interface.getTorrentManager().getAttribute("Networks");
/* 109 */     this.ta_peer_sources = this.plugin_interface.getTorrentManager().getAttribute("PeerSources");
/*     */     
/* 111 */     this.mon = this.plugin_interface.getUtilities().getMonitor();
/*     */     
/* 113 */     this.log = this.plugin_interface.getLogger().getTimeStampedChannel("LAN Peer Finder");
/*     */     
/* 115 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*     */     
/* 117 */     BasicPluginConfigModel config = ui_manager.createBasicPluginConfigModel("plugins", "Plugin.localtracker.name");
/*     */     
/* 119 */     config.addLabelParameter2("Plugin.localtracker.info");
/*     */     
/* 121 */     this.enabled = config.addBooleanParameter2("Plugin.localtracker.enable", "Plugin.localtracker.enable", true);
/*     */     
/* 123 */     config.addLabelParameter2("Plugin.localtracker.networks.info");
/*     */     
/* 125 */     final StringParameter subnets = config.addStringParameter2("Plugin.localtracker.networks", "Plugin.localtracker.networks", "");
/*     */     
/* 127 */     final BooleanParameter include_wellknown = config.addBooleanParameter2("Plugin.localtracker.wellknownlocals", "Plugin.localtracker.wellknownlocals", true);
/*     */     
/* 129 */     config.addLabelParameter2("Plugin.localtracker.autoadd.info");
/*     */     
/* 131 */     final StringParameter autoadd = config.addStringParameter2("Plugin.localtracker.autoadd", "Plugin.localtracker.autoadd", "");
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
/* 143 */     final BasicPluginViewModel view_model = this.plugin_interface.getUIManager().createBasicPluginViewModel("Plugin.localtracker.name");
/*     */     
/*     */ 
/* 146 */     view_model.setConfigSectionID("Plugin.localtracker.name");
/* 147 */     view_model.getActivity().setVisible(false);
/* 148 */     view_model.getProgress().setVisible(false);
/*     */     
/* 150 */     this.log.addListener(new LoggerChannelListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void messageLogged(int type, String content)
/*     */       {
/*     */ 
/*     */ 
/* 158 */         view_model.getLogArea().appendText(content + "\n");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageLogged(String str, Throwable error)
/*     */       {
/* 166 */         if (str.length() > 0) {
/* 167 */           view_model.getLogArea().appendText(str + "\n");
/*     */         }
/*     */         
/* 170 */         StringWriter sw = new StringWriter();
/*     */         
/* 172 */         PrintWriter pw = new PrintWriter(sw);
/*     */         
/* 174 */         error.printStackTrace(pw);
/*     */         
/* 176 */         pw.flush();
/*     */         
/* 178 */         view_model.getLogArea().appendText(sw.toString() + "\n");
/*     */       }
/*     */       
/* 181 */     });
/* 182 */     this.plugin_start_time = this.plugin_interface.getUtilities().getCurrentSystemTime();
/*     */     
/*     */ 
/* 185 */     this.instance_manager = AzureusCoreFactory.getSingleton().getInstanceManager();
/*     */     
/* 187 */     this.instance_manager.addListener(this);
/*     */     
/* 189 */     this.plugin_interface.getPluginconfig().addListener(new PluginConfigListener()
/*     */     {
/*     */ 
/*     */       public void configSaved()
/*     */       {
/*     */ 
/* 195 */         LocalTrackerPlugin.this.processSubNets(subnets.getValue(), include_wellknown.getValue());
/* 196 */         LocalTrackerPlugin.this.processAutoAdd(autoadd.getValue());
/*     */       }
/*     */       
/* 199 */     });
/* 200 */     processSubNets(subnets.getValue(), include_wellknown.getValue());
/* 201 */     processAutoAdd(autoadd.getValue());
/*     */     
/* 203 */     DelayedTask dt = this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 208 */         LocalTrackerPlugin.this.plugin_interface.getDownloadManager().addListener(LocalTrackerPlugin.this);
/*     */       }
/*     */       
/*     */ 
/* 212 */     });
/* 213 */     dt.queue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void instanceFound(AZInstance instance)
/*     */   {
/* 220 */     if (!this.enabled.getValue())
/*     */     {
/* 222 */       return;
/*     */     }
/*     */     
/* 225 */     this.log.log("Found: " + instance.getString());
/*     */     try
/*     */     {
/* 228 */       this.mon.enter();
/*     */       
/* 230 */       this.track_times.put(instance.getID(), new HashMap());
/*     */     }
/*     */     finally
/*     */     {
/* 234 */       this.mon.exit();
/*     */     }
/*     */     
/* 237 */     checkActivation();
/*     */   }
/*     */   
/*     */   protected void checkActivation()
/*     */   {
/*     */     try
/*     */     {
/* 244 */       this.mon.enter();
/*     */       
/* 246 */       if (this.active) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 251 */       this.active = true;
/*     */       
/* 253 */       this.plugin_interface.getUtilities().createThread("Tracker", new Runnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 260 */           LocalTrackerPlugin.this.track();
/*     */         }
/*     */       });
/*     */     }
/*     */     finally
/*     */     {
/* 266 */       this.mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void instanceChanged(AZInstance instance)
/*     */   {
/* 274 */     if (!this.enabled.getValue())
/*     */     {
/* 276 */       return;
/*     */     }
/*     */     
/* 279 */     this.log.log("Changed: " + instance.getString());
/*     */   }
/*     */   
/*     */ 
/*     */   public void instanceLost(AZInstance instance)
/*     */   {
/*     */     try
/*     */     {
/* 287 */       this.mon.enter();
/*     */       
/* 289 */       this.track_times.remove(instance.getID());
/*     */     }
/*     */     finally
/*     */     {
/* 293 */       this.mon.exit();
/*     */     }
/*     */     
/* 296 */     if (!this.enabled.getValue())
/*     */     {
/* 298 */       return;
/*     */     }
/*     */     
/* 301 */     this.log.log("Lost: " + instance.getString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void instanceTracked(AZInstanceTracked instance)
/*     */   {
/* 308 */     if (!this.enabled.getValue())
/*     */     {
/* 310 */       return;
/*     */     }
/*     */     
/* 313 */     handleTrackResult(instance);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void track()
/*     */   {
/* 319 */     long now = this.plugin_interface.getUtilities().getCurrentSystemTime();
/*     */     
/* 321 */     if (now - this.plugin_start_time < 60000L)
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 326 */         Thread.sleep(15000L);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 333 */     this.plugin_interface.getUtilities().createTimer("LanPeerFinder:Tracker", true).addPeriodicEvent(30000L, new UTTimerEventPerformer()
/*     */     {
/*     */ 
/*     */       public void perform(UTTimerEvent event)
/*     */       {
/*     */ 
/* 339 */         LocalTrackerPlugin.this.current_time = LocalTrackerPlugin.this.plugin_interface.getUtilities().getCurrentSystemTime();
/*     */         
/*     */         try
/*     */         {
/* 343 */           List<Download> todo = new ArrayList();
/*     */           try
/*     */           {
/* 346 */             LocalTrackerPlugin.this.mon.enter();
/*     */             
/* 348 */             Iterator<Map.Entry<Download, long[]>> it = LocalTrackerPlugin.this.downloads.entrySet().iterator();
/*     */             
/* 350 */             while (it.hasNext())
/*     */             {
/* 352 */               Map.Entry<Download, long[]> entry = (Map.Entry)it.next();
/*     */               
/* 354 */               Download dl = (Download)entry.getKey();
/* 355 */               long when = ((long[])entry.getValue())[0];
/*     */               
/* 357 */               if ((when > LocalTrackerPlugin.this.current_time) || (LocalTrackerPlugin.this.current_time - when > 300000L))
/*     */               {
/* 359 */                 todo.add(dl);
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 365 */             LocalTrackerPlugin.this.mon.exit();
/*     */           }
/*     */           
/* 368 */           for (int i = 0; i < todo.size(); i++)
/*     */           {
/* 370 */             LocalTrackerPlugin.this.track((Download)todo.get(i));
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 375 */           LocalTrackerPlugin.this.log.log(e);
/*     */         }
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
/*     */   protected void track(Download download)
/*     */   {
/* 390 */     long now = this.plugin_interface.getUtilities().getCurrentSystemTime();
/*     */     
/* 392 */     boolean ok = false;
/*     */     try
/*     */     {
/* 395 */       this.mon.enter();
/*     */       
/* 397 */       long[] data = (long[])this.downloads.get(download);
/*     */       
/* 399 */       if (data == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 404 */       long last_track = data[0];
/*     */       
/* 406 */       if ((last_track > now) || (now - last_track > 60000L))
/*     */       {
/* 408 */         ok = true;
/*     */         
/* 410 */         data[0] = now;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 415 */       this.mon.exit();
/*     */     }
/*     */     
/* 418 */     if (ok)
/*     */     {
/* 420 */       trackSupport(download);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void trackSupport(final Download download)
/*     */   {
/* 428 */     if (!this.enabled.getValue())
/*     */     {
/* 430 */       return;
/*     */     }
/*     */     
/* 433 */     int state = download.getState();
/*     */     
/* 435 */     if ((state == 8) || (state == 7))
/*     */     {
/* 437 */       return;
/*     */     }
/*     */     
/* 440 */     String[] sources = download.getListAttribute(this.ta_peer_sources);
/*     */     
/* 442 */     boolean ok = false;
/*     */     
/* 444 */     if (sources != null)
/*     */     {
/* 446 */       for (int i = 0; i < sources.length; i++)
/*     */       {
/* 448 */         if (sources[i].equalsIgnoreCase("Plugin"))
/*     */         {
/* 450 */           ok = true;
/*     */           
/* 452 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 457 */     if (!ok)
/*     */     {
/* 459 */       return;
/*     */     }
/*     */     
/* 462 */     if (download.getTorrent() == null)
/*     */     {
/* 464 */       return;
/*     */     }
/*     */     
/* 467 */     byte[] hash = new SHA1Simple().calculateHash(download.getTorrent().getHash());
/*     */     
/*     */ 
/* 470 */     AZInstanceTracked[] peers = this.instance_manager.track(hash, new AZInstanceTracked.TrackTarget()
/*     */     {
/*     */ 
/*     */ 
/*     */       public Object getTarget()
/*     */       {
/*     */ 
/*     */ 
/* 478 */         return download;
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean isSeed()
/*     */       {
/* 484 */         return download.isComplete();
/*     */       }
/*     */       
/* 487 */     });
/* 488 */     int total_seeds = 0;
/* 489 */     int total_leechers = 0;
/* 490 */     int total_peers = 0;
/*     */     
/* 492 */     for (int i = 0; i < peers.length; i++)
/*     */     {
/* 494 */       int res = handleTrackResult(peers[i]);
/*     */       
/* 496 */       if (res == 1) {
/* 497 */         total_seeds++;
/* 498 */       } else if (res == 2) {
/* 499 */         total_leechers++;
/* 500 */       } else if (res == 3) {
/* 501 */         total_seeds++;
/* 502 */         total_peers++;
/* 503 */       } else if (res == 4) {
/* 504 */         total_leechers++;
/* 505 */         total_peers++;
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 510 */       this.mon.enter();
/*     */       
/* 512 */       long[] data = (long[])this.downloads.get(download);
/*     */       
/* 514 */       if (data != null)
/*     */       {
/* 516 */         data[1] = total_seeds;
/* 517 */         data[2] = total_leechers;
/* 518 */         data[3] = total_peers;
/*     */       }
/*     */     }
/*     */     finally {
/* 522 */       this.mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void forceTrack(final Download download)
/*     */   {
/*     */     try
/*     */     {
/* 531 */       this.mon.enter();
/*     */       
/* 533 */       long[] data = (long[])this.downloads.get(download);
/*     */       
/* 535 */       if (data == null)
/*     */       {
/* 537 */         data = new long[4];
/*     */         
/* 539 */         this.downloads.put(download, data);
/*     */       }
/*     */       else
/*     */       {
/* 543 */         data[0] = 0L;
/*     */       }
/*     */       
/* 546 */       String dl_key = this.plugin_interface.getUtilities().getFormatters().encodeBytesToString(download.getTorrent().getHash());
/*     */       
/* 548 */       Iterator<Map<String, Long>> it = this.track_times.values().iterator();
/*     */       
/* 550 */       while (it.hasNext())
/*     */       {
/* 552 */         ((Map)it.next()).remove(dl_key);
/*     */       }
/*     */     }
/*     */     finally {
/* 556 */       this.mon.exit();
/*     */     }
/*     */     
/* 559 */     this.dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 565 */         LocalTrackerPlugin.this.track(download);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int handleTrackResult(AZInstanceTracked tracked_inst)
/*     */   {
/* 574 */     AZInstance inst = tracked_inst.getInstance();
/*     */     
/* 576 */     Download download = (Download)tracked_inst.getTarget().getTarget();
/*     */     
/* 578 */     boolean is_seed = tracked_inst.isSeed();
/*     */     
/* 580 */     long now = this.plugin_interface.getUtilities().getCurrentSystemTime();
/*     */     
/* 582 */     boolean skip = false;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 587 */       this.mon.enter();
/*     */       
/* 589 */       Map<String, Long> map = (Map)this.track_times.get(inst.getID());
/*     */       
/* 591 */       if (map == null)
/*     */       {
/* 593 */         map = new HashMap();
/*     */         
/* 595 */         this.track_times.put(inst.getID(), map);
/*     */       }
/*     */       
/* 598 */       String dl_key = this.plugin_interface.getUtilities().getFormatters().encodeBytesToString(download.getTorrent().getHash());
/*     */       
/* 600 */       Long last_track = (Long)map.get(dl_key);
/*     */       
/* 602 */       if (last_track != null)
/*     */       {
/* 604 */         long lt = last_track.longValue();
/*     */         
/* 606 */         if (now - lt < 30000L)
/*     */         {
/* 608 */           skip = true;
/*     */         }
/*     */       }
/*     */       
/* 612 */       map.put(dl_key, new Long(now));
/*     */     }
/*     */     finally
/*     */     {
/* 616 */       this.mon.exit();
/*     */     }
/*     */     
/* 619 */     if (skip)
/*     */     {
/* 621 */       return -1;
/*     */     }
/*     */     
/* 624 */     this.log.log("Tracked: " + inst.getString() + ": " + download.getName() + ", seed = " + is_seed);
/*     */     
/* 626 */     if ((download.isComplete()) && (is_seed))
/*     */     {
/* 628 */       return 1;
/*     */     }
/*     */     
/* 631 */     PeerManager peer_manager = download.getPeerManager();
/*     */     
/* 633 */     if (peer_manager != null)
/*     */     {
/* 635 */       String peer_ip = inst.getInternalAddress().getHostAddress();
/* 636 */       int peer_tcp_port = inst.getTCPListenPort();
/* 637 */       int peer_udp_port = inst.getUDPListenPort();
/*     */       
/* 639 */       this.log.log("    " + download.getName() + ": Injecting peer " + peer_ip + ":" + peer_tcp_port + "/" + peer_udp_port);
/*     */       
/* 641 */       peer_manager.addPeer(peer_ip, peer_tcp_port, peer_udp_port, false);
/*     */     }
/*     */     
/* 644 */     return is_seed ? 3 : 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public void downloadAdded(Download download)
/*     */   {
/*     */     try
/*     */     {
/* 652 */       this.mon.enter();
/*     */       
/* 654 */       Torrent torrent = download.getTorrent();
/*     */       
/* 656 */       if (torrent == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 661 */       if (TorrentUtils.isReallyPrivate(PluginCoreUtils.unwrap(torrent)))
/*     */       {
/* 663 */         this.log.log("Not tracking " + download.getName() + ": torrent is private");
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 668 */         String[] networks = download.getListAttribute(this.ta_networks);
/*     */         
/* 670 */         boolean public_net = false;
/*     */         
/* 672 */         if (networks != null)
/*     */         {
/* 674 */           for (int i = 0; i < networks.length; i++)
/*     */           {
/* 676 */             if (networks[i].equalsIgnoreCase("Public"))
/*     */             {
/* 678 */               public_net = true;
/*     */               
/* 680 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 685 */         if (!public_net)
/*     */         {
/* 687 */           this.log.log("Not tracking " + download.getName() + ": torrent has no public network");
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 692 */           if (this.enabled.getValue())
/*     */           {
/* 694 */             this.log.log("Tracking " + download.getName());
/*     */           }
/*     */           
/* 697 */           long[] data = (long[])this.downloads.get(download);
/*     */           
/* 699 */           if (data == null)
/*     */           {
/* 701 */             data = new long[4];
/*     */             
/* 703 */             this.downloads.put(download, data);
/*     */           }
/*     */           else
/*     */           {
/* 707 */             data[0] = 0L;
/*     */           }
/*     */           
/* 710 */           download.addListener(this);
/*     */         }
/*     */       }
/*     */     } finally {
/* 714 */       this.mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void downloadRemoved(Download download)
/*     */   {
/*     */     try
/*     */     {
/* 723 */       this.mon.enter();
/*     */       
/* 725 */       this.downloads.remove(download);
/*     */       
/* 727 */       download.removeListener(this);
/*     */     }
/*     */     finally
/*     */     {
/* 731 */       this.mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerPeerSource getTrackerPeerSource(final Download download)
/*     */   {
/* 739 */     new TrackerPeerSourceAdapter()
/*     */     {
/*     */       private long[] _last_data;
/*     */       
/*     */       private boolean enabled;
/*     */       
/*     */       private boolean running;
/*     */       private long fixup_time;
/*     */       
/*     */       private long[] fixup()
/*     */       {
/* 750 */         long now = SystemTime.getMonotonousTime();
/*     */         
/* 752 */         if (now - this.fixup_time > 1000L)
/*     */         {
/*     */           try {
/* 755 */             LocalTrackerPlugin.this.mon.enter();
/*     */             
/* 757 */             this._last_data = ((long[])LocalTrackerPlugin.this.downloads.get(download));
/*     */           }
/*     */           finally
/*     */           {
/* 761 */             LocalTrackerPlugin.this.mon.exit();
/*     */           }
/*     */           
/* 764 */           this.enabled = LocalTrackerPlugin.this.enabled.getValue();
/*     */           
/* 766 */           if (this.enabled)
/*     */           {
/* 768 */             int ds = download.getState();
/*     */             
/* 770 */             this.running = ((ds == 4) || (ds == 5));
/*     */           }
/*     */           else
/*     */           {
/* 774 */             this.running = false;
/*     */           }
/*     */           
/* 777 */           this.fixup_time = now;
/*     */         }
/*     */         
/* 780 */         return this._last_data;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getType()
/*     */       {
/* 786 */         return 4;
/*     */       }
/*     */       
/*     */ 
/*     */       public String getName()
/*     */       {
/* 792 */         return MessageText.getString("tps.lan.details", new String[] { String.valueOf(LocalTrackerPlugin.this.instance_manager.getOtherInstanceCount(false)) });
/*     */       }
/*     */       
/*     */ 
/*     */       public int getStatus()
/*     */       {
/* 798 */         long[] last_data = fixup();
/*     */         
/* 800 */         if ((last_data == null) || (!this.enabled))
/*     */         {
/* 802 */           return 1;
/*     */         }
/*     */         
/* 805 */         if (this.running)
/*     */         {
/* 807 */           return 5;
/*     */         }
/*     */         
/* 810 */         return 2;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getSeedCount()
/*     */       {
/* 816 */         long[] last_data = fixup();
/*     */         
/* 818 */         if ((last_data == null) || (!this.running))
/*     */         {
/* 820 */           return -1;
/*     */         }
/*     */         
/* 823 */         return (int)last_data[1];
/*     */       }
/*     */       
/*     */ 
/*     */       public int getLeecherCount()
/*     */       {
/* 829 */         long[] last_data = fixup();
/*     */         
/* 831 */         if ((last_data == null) || (!this.running))
/*     */         {
/* 833 */           return -1;
/*     */         }
/*     */         
/* 836 */         return (int)last_data[2];
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPeers()
/*     */       {
/* 842 */         long[] last_data = fixup();
/*     */         
/* 844 */         if ((last_data == null) || (!this.running))
/*     */         {
/* 846 */           return -1;
/*     */         }
/*     */         
/* 849 */         return (int)last_data[3];
/*     */       }
/*     */       
/*     */ 
/*     */       public int getSecondsToUpdate()
/*     */       {
/* 855 */         long[] last_data = fixup();
/*     */         
/* 857 */         if ((last_data == null) || (!this.running))
/*     */         {
/* 859 */           return Integer.MIN_VALUE;
/*     */         }
/*     */         
/* 862 */         return (int)((300000L - (SystemTime.getCurrentTime() - last_data[0])) / 1000L);
/*     */       }
/*     */       
/*     */ 
/*     */       public int getInterval()
/*     */       {
/* 868 */         if (this.running)
/*     */         {
/* 870 */           return 300;
/*     */         }
/*     */         
/* 873 */         return -1;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getMinInterval()
/*     */       {
/* 879 */         if (this.running)
/*     */         {
/* 881 */           return 60;
/*     */         }
/*     */         
/* 884 */         return -1;
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean isUpdating()
/*     */       {
/* 890 */         int su = getSecondsToUpdate();
/*     */         
/* 892 */         if ((su == Integer.MIN_VALUE) || (su >= 0))
/*     */         {
/* 894 */           return false;
/*     */         }
/*     */         
/* 897 */         return true;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stateChanged(Download download, int old_state, int new_state)
/*     */   {
/* 908 */     if ((new_state == 4) || (new_state == 5))
/*     */     {
/*     */ 
/* 911 */       forceTrack(download);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void positionChanged(Download download, int oldPosition, int newPosition) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void processSubNets(String subnets, boolean include_well_known)
/*     */   {
/* 928 */     if (include_well_known != this.instance_manager.getIncludeWellKnownLANs())
/*     */     {
/* 930 */       this.instance_manager.setIncludeWellKnownLANs(include_well_known);
/*     */       
/* 932 */       this.log.log("Include well known local networks set to " + include_well_known);
/*     */     }
/*     */     
/* 935 */     if (subnets.equals(this.last_subnets))
/*     */     {
/* 937 */       return;
/*     */     }
/*     */     
/* 940 */     this.last_subnets = subnets;
/*     */     
/* 942 */     StringTokenizer tok = new StringTokenizer(subnets, ";");
/*     */     
/* 944 */     while (tok.hasMoreTokens())
/*     */     {
/* 946 */       String net = tok.nextToken().trim();
/*     */       
/*     */       try
/*     */       {
/* 950 */         if (this.instance_manager.addLANSubnet(net))
/*     */         {
/* 952 */           this.log.log("Added network '" + net + "'");
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 957 */         this.log.log("Failed to add network '" + net + "'", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void processAutoAdd(String autoadd)
/*     */   {
/* 966 */     if (autoadd.equals(this.last_autoadd))
/*     */     {
/* 968 */       return;
/*     */     }
/*     */     
/* 971 */     this.last_autoadd = autoadd;
/*     */     
/* 973 */     StringTokenizer tok = new StringTokenizer(autoadd, ";");
/*     */     
/* 975 */     while (tok.hasMoreTokens())
/*     */     {
/* 977 */       String peer = tok.nextToken();
/*     */       
/*     */       try
/*     */       {
/* 981 */         InetAddress p = InetAddress.getByName(peer.trim());
/*     */         
/* 983 */         if (this.instance_manager.addInstance(p))
/*     */         {
/* 985 */           this.log.log("Added peer '" + peer + "'");
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 989 */         this.log.log("Failed to decode peer '" + peer + "'", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/tracker/local/LocalTrackerPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */