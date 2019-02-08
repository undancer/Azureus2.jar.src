/*      */ package com.aelitis.azureus.plugins.dht.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.DHTFactory;
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.DHTOperationListener;
/*      */ import com.aelitis.azureus.core.dht.DHTStorageKeyStats;
/*      */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*      */ import com.aelitis.azureus.core.dht.control.DHTControlStats;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDBStats;
/*      */ import com.aelitis.azureus.core.dht.db.DHTDBValue;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherAdapter;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterStats;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportFactory;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportProgressListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportTransferHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*      */ import com.aelitis.azureus.core.util.DNSUtils;
/*      */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginInterface.DHTInterface;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginKeyStats;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginOperationListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginProgressListener;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginTransferHandler;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPluginValue;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.File;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.peers.Peer;
/*      */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*      */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTPluginImpl
/*      */   implements DHTPluginInterface.DHTInterface
/*      */ {
/*      */   private static final String SEED_ADDRESS_V4 = "dht.vuze.com";
/*      */   private static final String SEED_ADDRESS_V6 = "dht6.vuze.com";
/*      */   private static final int SEED_PORT = 6881;
/*      */   private static final long MIN_ROOT_SEED_IMPORT_PERIOD = 28800000L;
/*      */   private PluginInterface plugin_interface;
/*      */   private int status;
/*      */   private String status_text;
/*      */   private ActionParameter reseed_param;
/*      */   private BooleanParameter warn_user_param;
/*      */   private DHT dht;
/*      */   private int port;
/*      */   private byte protocol_version;
/*      */   private int network;
/*      */   private boolean v6;
/*      */   private DHTTransportUDP transport;
/*      */   private DHTPluginStorageManager storage_manager;
/*      */   private long last_root_seed_import_time;
/*      */   private LoggerChannel log;
/*      */   private DHTLogger dht_log;
/*      */   private int stats_ticks;
/*      */   
/*      */   public DHTPluginImpl(PluginInterface _plugin_interface, DHTNATPuncherAdapter _nat_adapter, DHTPluginImplAdapter _adapter, byte _protocol_version, int _network, boolean _v6, String _ip, int _port, ActionParameter _reseed, BooleanParameter _warn_user_param, boolean _logging, LoggerChannel _log, DHTLogger _dht_log)
/*      */   {
/*  141 */     this.plugin_interface = _plugin_interface;
/*  142 */     this.protocol_version = _protocol_version;
/*  143 */     this.network = _network;
/*  144 */     this.v6 = _v6;
/*  145 */     this.port = _port;
/*  146 */     this.reseed_param = _reseed;
/*  147 */     this.warn_user_param = _warn_user_param;
/*  148 */     this.log = _log;
/*  149 */     this.dht_log = _dht_log;
/*      */     
/*  151 */     final DHTPluginImplAdapter adapter = _adapter;
/*      */     try
/*      */     {
/*  154 */       this.storage_manager = new DHTPluginStorageManager(this.network, this.dht_log, getDataDir(_network));
/*      */       
/*  156 */       PluginConfig conf = this.plugin_interface.getPluginconfig();
/*      */       
/*  158 */       int send_delay = conf.getPluginIntParameter("dht.senddelay", 25);
/*  159 */       int recv_delay = conf.getPluginIntParameter("dht.recvdelay", 10);
/*      */       
/*  161 */       boolean bootstrap = conf.getPluginBooleanParameter("dht.bootstrapnode", false);
/*      */       
/*      */ 
/*      */ 
/*  165 */       boolean initial_reachable = conf.getPluginBooleanParameter("dht.reachable." + this.network, true);
/*      */       
/*  167 */       this.transport = DHTTransportFactory.createUDP(_protocol_version, _network, _v6, _ip, this.storage_manager.getMostRecentAddress(), _port, 3, 1, 10000L, send_delay, recv_delay, bootstrap, initial_reachable, this.dht_log);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  184 */       this.transport.addListener(new DHTTransportListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void localContactChanged(DHTTransportContact local_contact)
/*      */         {
/*      */ 
/*  191 */           DHTPluginImpl.this.storage_manager.localContactChanged(local_contact);
/*      */           
/*  193 */           if (adapter != null)
/*      */           {
/*  195 */             adapter.localContactChanged(DHTPluginImpl.this.getLocalAddress());
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void resetNetworkPositions() {}
/*      */         
/*      */ 
/*      */ 
/*      */         public void currentAddress(String address)
/*      */         {
/*  208 */           DHTPluginImpl.this.storage_manager.recordCurrentAddress(address);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void reachabilityChanged(boolean reacheable) {}
/*  217 */       });
/*  218 */       Properties props = new Properties();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  226 */       if (_network == 1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  232 */         props.put("CacheRepublishInterval", new Integer(3600000));
/*      */       }
/*      */       
/*  235 */       this.dht = DHTFactory.create(this.transport, props, this.storage_manager, _nat_adapter, this.dht_log);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  242 */       this.plugin_interface.firePluginEvent(new PluginEvent()
/*      */       {
/*      */ 
/*      */         public int getType()
/*      */         {
/*      */ 
/*  248 */           return 1024;
/*      */         }
/*      */         
/*      */ 
/*      */         public Object getValue()
/*      */         {
/*  254 */           return DHTPluginImpl.this.dht;
/*      */         }
/*      */         
/*  257 */       });
/*  258 */       this.dht.setLogging(_logging);
/*      */       
/*  260 */       DHTTransportContact root_seed = importRootSeed();
/*      */       
/*  262 */       this.storage_manager.importContacts(this.dht);
/*      */       
/*  264 */       this.plugin_interface.getUtilities().createTimer("DHTExport", true).addPeriodicEvent(120000L, new UTTimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*  268 */         private int tick_count = 0;
/*      */         
/*      */ 
/*      */ 
/*      */         public void perform(UTTimerEvent event)
/*      */         {
/*  274 */           this.tick_count += 1;
/*      */           
/*  276 */           if ((this.tick_count == 1) || (this.tick_count % 5 == 0))
/*      */           {
/*  278 */             DHTPluginImpl.this.checkForReSeed(false);
/*      */             
/*  280 */             DHTPluginImpl.this.storage_manager.exportContacts(DHTPluginImpl.this.dht);
/*      */           }
/*      */           
/*      */         }
/*  284 */       });
/*  285 */       integrateDHT(true, root_seed);
/*      */       
/*  287 */       this.status = 3;
/*      */       
/*  289 */       this.status_text = "Running";
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  293 */       Debug.printStackTrace(e);
/*      */       
/*  295 */       this.log.log("DHT integrtion fails", e);
/*      */       
/*  297 */       this.status_text = ("DHT Integration fails: " + Debug.getNestedExceptionMessage(e));
/*      */       
/*  299 */       this.status = 4;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updateStats(int sample_stats_ticks)
/*      */   {
/*  307 */     this.stats_ticks += 1;
/*      */     
/*  309 */     if (this.transport != null)
/*      */     {
/*  311 */       PluginConfig conf = this.plugin_interface.getPluginconfig();
/*      */       
/*  313 */       boolean current_reachable = this.transport.isReachable();
/*      */       
/*  315 */       if (current_reachable != conf.getPluginBooleanParameter("dht.reachable." + this.network, true))
/*      */       {
/*      */ 
/*      */ 
/*  319 */         conf.setPluginParameter("dht.reachable." + this.network, current_reachable);
/*      */         
/*  321 */         if (!current_reachable)
/*      */         {
/*  323 */           String msg = "If you have a router/firewall, please check that you have port " + this.port + " UDP open.\nDecentralised tracking requires this.";
/*      */           
/*      */ 
/*  326 */           int warned_port = this.plugin_interface.getPluginconfig().getPluginIntParameter("udp_warned_port", 0);
/*      */           
/*  328 */           if ((warned_port == this.port) || (!this.warn_user_param.getValue()))
/*      */           {
/*  330 */             this.log.log(msg);
/*      */           }
/*      */           else
/*      */           {
/*  334 */             this.plugin_interface.getPluginconfig().setPluginParameter("udp_warned_port", this.port);
/*      */             
/*  336 */             this.log.logAlert(2, msg);
/*      */           }
/*      */         }
/*      */         else {
/*  340 */           this.log.log("Reachability changed for the better");
/*      */         }
/*      */       }
/*      */       
/*  344 */       if (this.stats_ticks % sample_stats_ticks == 0)
/*      */       {
/*  346 */         logStats();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getStatus()
/*      */   {
/*  354 */     return this.status;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatusText()
/*      */   {
/*  360 */     return this.status_text;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isReachable()
/*      */   {
/*  366 */     return this.transport.isReachable();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLogging(boolean l)
/*      */   {
/*  373 */     this.dht.setLogging(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void tick() {}
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  384 */     return this.port;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPort(int new_port)
/*      */   {
/*  391 */     this.port = new_port;
/*      */     try
/*      */     {
/*  394 */       this.transport.setPort(this.port);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  398 */       this.log.log(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getClockSkew()
/*      */   {
/*  405 */     return this.transport.getStats().getSkewAverage();
/*      */   }
/*      */   
/*      */ 
/*      */   public void logStats()
/*      */   {
/*  411 */     DHTDBStats d_stats = this.dht.getDataBase().getStats();
/*  412 */     DHTControlStats c_stats = this.dht.getControl().getStats();
/*  413 */     DHTRouterStats r_stats = this.dht.getRouter().getStats();
/*  414 */     DHTTransportStats t_stats = this.transport.getStats();
/*      */     
/*  416 */     long[] rs = r_stats.getStats();
/*      */     
/*  418 */     this.log.log("DHT:ip=" + this.transport.getLocalContact().getAddress() + ",net=" + this.transport.getNetwork() + ",prot=V" + this.transport.getProtocolVersion() + ",reach=" + this.transport.isReachable() + ",flags=" + Integer.toString(this.transport.getGenericFlags() & 0xFF, 16));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  424 */     this.log.log("Router:nodes=" + rs[0] + ",leaves=" + rs[1] + ",contacts=" + rs[2] + ",replacement=" + rs[3] + ",live=" + rs[4] + ",unknown=" + rs[5] + ",failing=" + rs[6]);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  433 */     this.log.log("Transport:" + t_stats.getString());
/*      */     
/*      */ 
/*  436 */     int[] dbv_details = d_stats.getValueDetails();
/*      */     
/*  438 */     this.log.log("Control:dht=" + c_stats.getEstimatedDHTSize() + ", Database:keys=" + d_stats.getKeyCount() + ",vals=" + dbv_details[0] + ",loc=" + dbv_details[1] + ",dir=" + dbv_details[2] + ",ind=" + dbv_details[3] + ",div_f=" + dbv_details[4] + ",div_s=" + dbv_details[5]);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  447 */     DHTNATPuncher np = this.dht.getNATPuncher();
/*      */     
/*  449 */     if (np != null) {
/*  450 */       this.log.log("NAT: " + np.getStats());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected File getDataDir(int network)
/*      */   {
/*  458 */     File dir = new File(this.plugin_interface.getUtilities().getAzureusUserDir(), "dht");
/*      */     
/*  460 */     if (network != 0)
/*      */     {
/*  462 */       dir = new File(dir, "net" + network);
/*      */     }
/*      */     
/*  465 */     FileUtil.mkdirs(dir);
/*      */     
/*  467 */     return dir;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void integrateDHT(boolean first, DHTTransportContact remove_afterwards)
/*      */   {
/*      */     try
/*      */     {
/*  476 */       this.reseed_param.setEnabled(false);
/*      */       
/*  478 */       this.log.log("DHT " + (first ? "" : "re-") + "integration starts");
/*      */       
/*  480 */       long start = SystemTime.getCurrentTime();
/*      */       
/*  482 */       this.dht.integrate(false);
/*      */       
/*  484 */       if (remove_afterwards != null)
/*      */       {
/*  486 */         this.log.log("Removing seed " + remove_afterwards.getString());
/*      */         
/*  488 */         remove_afterwards.remove();
/*      */       }
/*      */       
/*  491 */       long end = SystemTime.getCurrentTime();
/*      */       
/*  493 */       this.log.log("DHT " + (first ? "" : "re-") + "integration complete: elapsed = " + (end - start));
/*      */       
/*  495 */       this.dht.print(false);
/*      */     }
/*      */     finally
/*      */     {
/*  499 */       this.reseed_param.setEnabled(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void checkForReSeed(boolean force)
/*      */   {
/*  507 */     int seed_limit = 32;
/*      */     
/*      */     try
/*      */     {
/*  511 */       long[] router_stats = this.dht.getRouter().getStats().getStats();
/*      */       
/*  513 */       if ((router_stats[4] < seed_limit) || (force))
/*      */       {
/*  515 */         if (force)
/*      */         {
/*  517 */           this.log.log("Reseeding");
/*      */         }
/*      */         else
/*      */         {
/*  521 */           this.log.log("Less than 32 live contacts, reseeding");
/*      */         }
/*      */         
/*  524 */         int peers_imported = 0;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  529 */         if ((this.network == 0) || (this.network == 3))
/*      */         {
/*      */ 
/*      */ 
/*  533 */           Download[] downloads = this.plugin_interface.getDownloadManager().getDownloads();
/*      */           
/*      */ 
/*      */ 
/*  537 */           for (int i = 0; i < downloads.length; i++)
/*      */           {
/*  539 */             Download download = downloads[i];
/*      */             
/*  541 */             PeerManager pm = download.getPeerManager();
/*      */             
/*  543 */             if (pm != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  548 */               Peer[] peers = pm.getPeers();
/*      */               
/*  550 */               for (int j = 0; j < peers.length; j++)
/*      */               {
/*  552 */                 Peer p = peers[j];
/*      */                 
/*  554 */                 int peer_udp_port = p.getUDPNonDataListenPort();
/*      */                 
/*  556 */                 if (peer_udp_port != 0)
/*      */                 {
/*  558 */                   boolean is_v6 = p.getIp().contains(":");
/*      */                   
/*  560 */                   if (is_v6 == this.v6)
/*      */                   {
/*  562 */                     String ip = p.getIp();
/*      */                     
/*  564 */                     if (AENetworkClassifier.categoriseAddress(ip) == "Public")
/*      */                     {
/*  566 */                       if (importSeed(ip, peer_udp_port) != null)
/*      */                       {
/*  568 */                         peers_imported++;
/*      */                         
/*  570 */                         if (peers_imported > seed_limit) {
/*      */                           break label252;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           label252:
/*  581 */           if (peers_imported < 16)
/*      */           {
/*  583 */             List<InetSocketAddress> list = VersionCheckClient.getSingleton().getDHTBootstrap(this.network == 0);
/*      */             
/*  585 */             for (InetSocketAddress address : list)
/*      */             {
/*  587 */               if (importSeed(address) != null)
/*      */               {
/*  589 */                 peers_imported++;
/*      */                 
/*  591 */                 if (peers_imported > seed_limit) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  600 */         DHTTransportContact root_to_remove = null;
/*      */         
/*  602 */         if (peers_imported == 0)
/*      */         {
/*  604 */           root_to_remove = importRootSeed();
/*      */           
/*  606 */           if (root_to_remove != null)
/*      */           {
/*  608 */             peers_imported++;
/*      */           }
/*      */         }
/*      */         
/*  612 */         if (peers_imported > 0)
/*      */         {
/*  614 */           integrateDHT(false, root_to_remove);
/*      */         }
/*      */         else
/*      */         {
/*  618 */           this.log.log("No valid peers found to reseed from");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  624 */       this.log.log(e);
/*      */     }
/*      */   }
/*      */   
/*      */   protected DHTTransportContact importRootSeed()
/*      */   {
/*      */     try
/*      */     {
/*  632 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  634 */       if (now - this.last_root_seed_import_time > 28800000L)
/*      */       {
/*  636 */         this.last_root_seed_import_time = now;
/*      */         
/*  638 */         return importSeed(getSeedAddress());
/*      */       }
/*      */       
/*      */ 
/*  642 */       this.log.log("    root seed imported too recently, ignoring");
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  646 */       this.log.log(e);
/*      */     }
/*      */     
/*  649 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTTransportContact importSeed(String ip, int port)
/*      */   {
/*      */     try
/*      */     {
/*  658 */       return this.transport.importContact(checkResolve(new InetSocketAddress(ip, port)), this.protocol_version, true);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  662 */       this.log.log(e);
/*      */     }
/*  664 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DHTTransportContact importSeed(InetAddress ia, int port)
/*      */   {
/*      */     try
/*      */     {
/*  675 */       return this.transport.importContact(new InetSocketAddress(ia, port), this.protocol_version, true);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  679 */       this.log.log(e);
/*      */     }
/*  681 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DHTTransportContact importSeed(InetSocketAddress ia)
/*      */   {
/*      */     try
/*      */     {
/*  691 */       return this.transport.importContact(ia, this.protocol_version, true);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  695 */       this.log.log(e);
/*      */     }
/*  697 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected InetSocketAddress getSeedAddress()
/*      */   {
/*  704 */     return checkResolve(new InetSocketAddress(this.v6 ? "dht6.vuze.com" : "dht.vuze.com", 6881));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private InetSocketAddress checkResolve(InetSocketAddress isa)
/*      */   {
/*  711 */     if (this.v6)
/*      */     {
/*  713 */       if (isa.isUnresolved()) {
/*      */         try
/*      */         {
/*  716 */           DNSUtils.DNSUtilsIntf dns_utils = DNSUtils.getSingleton();
/*      */           
/*  718 */           if (dns_utils != null)
/*      */           {
/*  720 */             String host = dns_utils.getIPV6ByName(isa.getHostName()).getHostAddress();
/*      */             
/*  722 */             isa = new InetSocketAddress(host, isa.getPort());
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*  729 */     return isa;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDiversified(byte[] key)
/*      */   {
/*  736 */     return this.dht.isDiversified(key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void put(byte[] key, String description, byte[] value, byte flags, DHTPluginOperationListener listener)
/*      */   {
/*  747 */     put(key, description, value, flags, true, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void put(final byte[] key, String description, byte[] value, byte flags, boolean high_priority, final DHTPluginOperationListener listener)
/*      */   {
/*  759 */     this.dht.put(key, description, value, (short)flags, high_priority, new DHTOperationListener()
/*      */     {
/*      */       private boolean started;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void searching(DHTTransportContact contact, int level, int active_searches)
/*      */       {
/*  774 */         if (listener != null)
/*      */         {
/*  776 */           synchronized (this)
/*      */           {
/*  778 */             if (this.started)
/*      */             {
/*  780 */               return;
/*      */             }
/*      */             
/*  783 */             this.started = true;
/*      */           }
/*      */           
/*  786 */           listener.starts(key);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean diversified(String desc)
/*      */       {
/*  794 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void read(DHTTransportContact _contact, DHTTransportValue _value)
/*      */       {
/*  809 */         Debug.out("read operation not supported for puts");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void wrote(DHTTransportContact _contact, DHTTransportValue _value)
/*      */       {
/*  819 */         if (listener != null)
/*      */         {
/*  821 */           listener.valueWritten(new DHTPluginContactImpl(DHTPluginImpl.this, _contact), DHTPluginImpl.this.mapValue(_value));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(boolean timeout)
/*      */       {
/*  832 */         if (listener != null)
/*      */         {
/*  834 */           listener.complete(key, timeout);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTPluginValue getLocalValue(byte[] key)
/*      */   {
/*  844 */     DHTTransportValue val = this.dht.getLocalValue(key);
/*      */     
/*  846 */     if (val == null)
/*      */     {
/*  848 */       return null;
/*      */     }
/*      */     
/*  851 */     return mapValue(val);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<DHTPluginValue> getValues()
/*      */   {
/*  857 */     DHTDB db = this.dht.getDataBase();
/*      */     
/*  859 */     Iterator<HashWrapper> keys = db.getKeys();
/*      */     
/*  861 */     List<DHTPluginValue> vals = new ArrayList();
/*      */     
/*  863 */     while (keys.hasNext())
/*      */     {
/*  865 */       DHTDBValue val = db.getAnyValue((HashWrapper)keys.next());
/*      */       
/*  867 */       if (val != null)
/*      */       {
/*  869 */         vals.add(mapValue(val));
/*      */       }
/*      */     }
/*      */     
/*  873 */     return vals;
/*      */   }
/*      */   
/*      */ 
/*      */   public List<DHTPluginValue> getValues(byte[] key)
/*      */   {
/*  879 */     List<DHTPluginValue> vals = new ArrayList();
/*      */     
/*  881 */     if (this.dht != null) {
/*      */       try
/*      */       {
/*  884 */         List<DHTTransportValue> values = this.dht.getStoredValues(key);
/*      */         
/*  886 */         for (DHTTransportValue v : values)
/*      */         {
/*  888 */           vals.add(mapValue(v));
/*      */         }
/*      */         
/*  891 */         return vals;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  895 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  899 */     return vals;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void get(final byte[] key, String description, byte flags, int max_values, long timeout, boolean exhaustive, boolean high_priority, final DHTPluginOperationListener listener)
/*      */   {
/*  913 */     this.dht.get(key, description, (short)flags, max_values, timeout, exhaustive, high_priority, new DHTOperationListener()
/*      */     {
/*      */ 
/*  916 */       private boolean started = false;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void searching(DHTTransportContact contact, int level, int active_searches)
/*      */       {
/*  924 */         if (listener != null)
/*      */         {
/*  926 */           synchronized (this)
/*      */           {
/*  928 */             if (this.started)
/*      */             {
/*  930 */               return;
/*      */             }
/*      */             
/*  933 */             this.started = true;
/*      */           }
/*      */           
/*  936 */           listener.starts(key);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean diversified(String desc)
/*      */       {
/*  944 */         if (listener != null)
/*      */         {
/*  946 */           return listener.diversified();
/*      */         }
/*      */         
/*  949 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void read(DHTTransportContact contact, DHTTransportValue value)
/*      */       {
/*  966 */         if (listener != null)
/*      */         {
/*  968 */           listener.valueRead(new DHTPluginContactImpl(DHTPluginImpl.this, value.getOriginator()), DHTPluginImpl.this.mapValue(value));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void wrote(DHTTransportContact contact, DHTTransportValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(boolean _timeout)
/*      */       {
/*  986 */         if (listener != null)
/*      */         {
/*  988 */           listener.complete(key, _timeout);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void remove(final byte[] key, String description, final DHTPluginOperationListener listener)
/*      */   {
/* 1000 */     this.dht.remove(key, description, new DHTOperationListener()
/*      */     {
/*      */       private boolean started;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void searching(DHTTransportContact contact, int level, int active_searches)
/*      */       {
/* 1012 */         if (listener != null)
/*      */         {
/* 1014 */           synchronized (this)
/*      */           {
/* 1016 */             if (this.started)
/*      */             {
/* 1018 */               return;
/*      */             }
/*      */             
/* 1021 */             this.started = true;
/*      */           }
/*      */           
/* 1024 */           listener.starts(key);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean diversified(String desc)
/*      */       {
/* 1039 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void read(DHTTransportContact contact, DHTTransportValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void wrote(DHTTransportContact contact, DHTTransportValue value)
/*      */       {
/* 1056 */         if (listener != null)
/*      */         {
/* 1058 */           listener.valueWritten(new DHTPluginContactImpl(DHTPluginImpl.this, contact), DHTPluginImpl.this.mapValue(value));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(boolean timeout)
/*      */       {
/* 1068 */         if (listener != null)
/*      */         {
/* 1070 */           listener.complete(key, timeout);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void remove(DHTPluginContact[] targets, final byte[] key, String description, final DHTPluginOperationListener listener)
/*      */   {
/* 1083 */     DHTTransportContact[] t_contacts = new DHTTransportContact[targets.length];
/*      */     
/* 1085 */     for (int i = 0; i < targets.length; i++)
/*      */     {
/* 1087 */       t_contacts[i] = ((DHTPluginContactImpl)targets[i]).getContact();
/*      */     }
/*      */     
/* 1090 */     this.dht.remove(t_contacts, key, description, new DHTOperationListener()
/*      */     {
/*      */       private boolean started;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void searching(DHTTransportContact contact, int level, int active_searches)
/*      */       {
/* 1103 */         if (listener != null)
/*      */         {
/* 1105 */           synchronized (this)
/*      */           {
/* 1107 */             if (this.started)
/*      */             {
/* 1109 */               return;
/*      */             }
/*      */             
/* 1112 */             this.started = true;
/*      */           }
/*      */           
/* 1115 */           listener.starts(key);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean diversified(String desc)
/*      */       {
/* 1130 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void read(DHTTransportContact contact, DHTTransportValue value) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void wrote(DHTTransportContact contact, DHTTransportValue value)
/*      */       {
/* 1147 */         if (listener != null)
/*      */         {
/* 1149 */           listener.valueWritten(new DHTPluginContactImpl(DHTPluginImpl.this, contact), DHTPluginImpl.this.mapValue(value));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(boolean timeout)
/*      */       {
/* 1159 */         if (listener != null)
/*      */         {
/* 1161 */           listener.complete(key, timeout);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginContact getLocalAddress()
/*      */   {
/* 1170 */     return new DHTPluginContactImpl(this, this.transport.getLocalContact());
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginContact importContact(Map<String, Object> map)
/*      */   {
/*      */     try
/*      */     {
/* 1178 */       return new DHTPluginContactImpl(this, this.transport.importContact(map));
/*      */     }
/*      */     catch (DHTTransportException e)
/*      */     {
/* 1182 */       Debug.printStackTrace(e);
/*      */     }
/* 1184 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTPluginContact importContact(InetSocketAddress address)
/*      */   {
/*      */     try
/*      */     {
/* 1193 */       return new DHTPluginContactImpl(this, this.transport.importContact(address, this.protocol_version, false));
/*      */     }
/*      */     catch (DHTTransportException e)
/*      */     {
/* 1197 */       Debug.printStackTrace(e);
/*      */     }
/* 1199 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTPluginContact importContact(InetSocketAddress address, byte version)
/*      */   {
/*      */     try
/*      */     {
/* 1209 */       return new DHTPluginContactImpl(this, this.transport.importContact(address, version, false));
/*      */     }
/*      */     catch (DHTTransportException e)
/*      */     {
/* 1213 */       Debug.printStackTrace(e);
/*      */     }
/* 1215 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1221 */   private Map<DHTPluginTransferHandler, DHTTransportTransferHandler> handler_map = new HashMap();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerHandler(byte[] handler_key, final DHTPluginTransferHandler handler, Map<String, Object> options)
/*      */   {
/* 1229 */     DHTTransportTransferHandler h = new DHTTransportTransferHandler()
/*      */     {
/*      */ 
/*      */       public String getName()
/*      */       {
/*      */ 
/* 1235 */         return handler.getName();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public byte[] handleRead(DHTTransportContact originator, byte[] key)
/*      */       {
/* 1243 */         return handler.handleRead(new DHTPluginContactImpl(DHTPluginImpl.this, originator), key);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public byte[] handleWrite(DHTTransportContact originator, byte[] key, byte[] value)
/*      */       {
/* 1252 */         return handler.handleWrite(new DHTPluginContactImpl(DHTPluginImpl.this, originator), key, value);
/*      */       }
/*      */     };
/*      */     
/* 1256 */     synchronized (this.handler_map)
/*      */     {
/* 1258 */       if (this.handler_map.containsKey(handler))
/*      */       {
/* 1260 */         Debug.out("Warning: handler already exists");
/*      */       }
/*      */       else {
/* 1263 */         this.handler_map.put(handler, h);
/*      */       }
/*      */     }
/*      */     
/* 1267 */     this.dht.getTransport().registerTransferHandler(handler_key, h, options);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterHandler(byte[] handler_key, DHTPluginTransferHandler handler)
/*      */   {
/*      */     DHTTransportTransferHandler h;
/*      */     
/*      */ 
/* 1277 */     synchronized (this.handler_map)
/*      */     {
/* 1279 */       h = (DHTTransportTransferHandler)this.handler_map.remove(handler);
/*      */     }
/*      */     
/* 1282 */     if (h == null)
/*      */     {
/* 1284 */       Debug.out("Mapping not found for handler");
/*      */     }
/*      */     else {
/*      */       try
/*      */       {
/* 1289 */         getDHT().getTransport().unregisterTransferHandler(handler_key, h);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1293 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] read(final DHTPluginProgressListener listener, DHTPluginContact target, byte[] handler_key, byte[] key, long timeout)
/*      */   {
/*      */     try
/*      */     {
/* 1307 */       this.dht.getTransport().readTransfer(listener == null ? null : new DHTTransportProgressListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void reportSize(long size)
/*      */         {
/*      */ 
/*      */ 
/* 1315 */           listener.reportSize(size);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void reportActivity(String str)
/*      */         {
/* 1322 */           listener.reportActivity(str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1329 */         public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, ((DHTPluginContactImpl)target).getContact(), handler_key, key, timeout);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (DHTTransportException e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1339 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(final DHTPluginProgressListener listener, DHTPluginContact target, byte[] handler_key, byte[] key, byte[] data, long timeout)
/*      */   {
/*      */     try
/*      */     {
/* 1353 */       this.dht.getTransport().writeTransfer(listener == null ? null : new DHTTransportProgressListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void reportSize(long size)
/*      */         {
/*      */ 
/*      */ 
/* 1361 */           listener.reportSize(size);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void reportActivity(String str)
/*      */         {
/* 1368 */           listener.reportActivity(str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1375 */         public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, ((DHTPluginContactImpl)target).getContact(), handler_key, key, data, timeout);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (DHTTransportException e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1386 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] call(final DHTPluginProgressListener listener, DHTPluginContact target, byte[] handler_key, byte[] data, long timeout)
/*      */   {
/*      */     try
/*      */     {
/* 1399 */       this.dht.getTransport().writeReadTransfer(listener == null ? null : new DHTTransportProgressListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void reportSize(long size)
/*      */         {
/*      */ 
/*      */ 
/* 1408 */           listener.reportSize(size);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void reportActivity(String str)
/*      */         {
/* 1415 */           listener.reportActivity(str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1422 */         public void reportCompleteness(int percent) { listener.reportCompleteness(percent); } }, ((DHTPluginContactImpl)target).getContact(), handler_key, data, timeout);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (DHTTransportException e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1432 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public DHT getDHT()
/*      */   {
/* 1439 */     return this.dht;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSuspended(boolean susp)
/*      */   {
/* 1446 */     this.dht.setSuspended(susp);
/*      */   }
/*      */   
/*      */ 
/*      */   public void closedownInitiated()
/*      */   {
/* 1452 */     this.storage_manager.exportContacts(this.dht);
/*      */     
/* 1454 */     this.dht.destroy();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isRecentAddress(String address)
/*      */   {
/* 1461 */     return this.storage_manager.isRecentAddress(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTPluginValue mapValue(DHTTransportValue value)
/*      */   {
/* 1468 */     if (value == null)
/*      */     {
/* 1470 */       return null;
/*      */     }
/*      */     
/* 1473 */     return new DHTPluginValueImpl(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTPluginKeyStats decodeStats(DHTPluginValue value)
/*      */   {
/* 1481 */     if ((value.getFlags() & 0x8) == 0)
/*      */     {
/* 1483 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 1487 */       DataInputStream dis = new DataInputStream(new ByteArrayInputStream(value.getValue()));
/*      */       
/* 1489 */       final DHTStorageKeyStats stats = this.storage_manager.deserialiseStats(dis);
/*      */       
/* 1491 */       new DHTPluginKeyStats()
/*      */       {
/*      */ 
/*      */         public int getEntryCount()
/*      */         {
/*      */ 
/* 1497 */           return stats.getEntryCount();
/*      */         }
/*      */         
/*      */ 
/*      */         public int getSize()
/*      */         {
/* 1503 */           return stats.getSize();
/*      */         }
/*      */         
/*      */ 
/*      */         public int getReadsPerMinute()
/*      */         {
/* 1509 */           return stats.getReadsPerMinute();
/*      */         }
/*      */         
/*      */ 
/*      */         public byte getDiversification()
/*      */         {
/* 1515 */           return stats.getDiversification();
/*      */         }
/*      */       };
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1521 */       Debug.printStackTrace(e);
/*      */     }
/* 1523 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[] getID()
/*      */   {
/* 1530 */     return this.dht.getRouter().getID();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isIPV6()
/*      */   {
/* 1536 */     return this.dht.getTransport().isIPV6();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNetwork()
/*      */   {
/* 1542 */     return this.dht.getTransport().getNetwork();
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginContact[] getReachableContacts()
/*      */   {
/* 1548 */     DHTTransportContact[] contacts = this.dht.getTransport().getReachableContacts();
/*      */     
/* 1550 */     DHTPluginContact[] result = new DHTPluginContact[contacts.length];
/*      */     
/* 1552 */     for (int i = 0; i < contacts.length; i++)
/*      */     {
/* 1554 */       result[i] = new DHTPluginContactImpl(this, contacts[i]);
/*      */     }
/*      */     
/* 1557 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginContact[] getRecentContacts()
/*      */   {
/* 1563 */     DHTTransportContact[] contacts = this.dht.getTransport().getRecentContacts();
/*      */     
/* 1565 */     DHTPluginContact[] result = new DHTPluginContact[contacts.length];
/*      */     
/* 1567 */     for (int i = 0; i < contacts.length; i++)
/*      */     {
/* 1569 */       result[i] = new DHTPluginContactImpl(this, contacts[i]);
/*      */     }
/*      */     
/* 1572 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<DHTPluginContact> getClosestContacts(byte[] to_id, boolean live_only)
/*      */   {
/* 1580 */     List<DHTTransportContact> contacts = this.dht.getControl().getClosestKContactsList(to_id, live_only);
/*      */     
/* 1582 */     List<DHTPluginContact> result = new ArrayList(contacts.size());
/*      */     
/* 1584 */     for (DHTTransportContact contact : contacts)
/*      */     {
/* 1586 */       result.add(new DHTPluginContactImpl(this, contact));
/*      */     }
/*      */     
/* 1589 */     return result;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/impl/DHTPluginImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */