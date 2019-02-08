/*      */ package com.aelitis.azureus.core.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*      */ import com.aelitis.azureus.core.AzureusCoreException;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleListener;
/*      */ import com.aelitis.azureus.core.AzureusCoreOperation;
/*      */ import com.aelitis.azureus.core.AzureusCoreOperationListener;
/*      */ import com.aelitis.azureus.core.AzureusCoreOperationTask;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.backup.BackupManagerFactory;
/*      */ import com.aelitis.azureus.core.custom.CustomizationManager;
/*      */ import com.aelitis.azureus.core.custom.CustomizationManagerFactory;
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter.StateListener;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerAdapter.VCPublicAddress;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManagerFactory;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceTracked.TrackTarget;
/*      */ import com.aelitis.azureus.core.nat.NATTraverser;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterface;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterfaceAddress;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.pairing.PairingManagerFactory;
/*      */ import com.aelitis.azureus.core.peermanager.PeerManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedLimitHandler;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*      */ import com.aelitis.azureus.core.update.AzureusRestarter;
/*      */ import com.aelitis.azureus.core.update.AzureusRestarterFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*      */ import com.aelitis.azureus.launcher.classloading.PrimaryClassloader;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import com.aelitis.azureus.plugins.startstoprules.defaultplugin.DefaultRankCalculator;
/*      */ import com.aelitis.azureus.plugins.tracker.dht.DHTTrackerPlugin;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.util.MapUtils;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.lang.reflect.Method;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileLock;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtil;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.ThreadPool;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.platform.PlatformManagerListener;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginEventListener;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentDownloader;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.PowerManagementListener;
/*      */ import org.gudy.azureus2.plugins.utils.ScriptProvider;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.PluginUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ 
/*      */ public class AzureusCoreImpl implements AzureusCore
/*      */ {
/*  124 */   private static final LogIDs LOGID = LogIDs.CORE;
/*      */   protected static AzureusCore singleton;
/*  126 */   protected static final AEMonitor class_mon = new AEMonitor("AzureusCore:class");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final String DM_ANNOUNCE_KEY = "AzureusCore:announce_key";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final boolean LOAD_PLUGINS_IN_OTHER_THREAD = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  141 */   static List<AzureusCoreRunningListener> coreRunningListeners = new ArrayList(1);
/*      */   
/*  143 */   static final AEMonitor mon_coreRunningListeners = new AEMonitor("CoreCreationListeners");
/*      */   final PluginInitializer pi;
/*      */   private GlobalManager global_manager;
/*      */   private final AZInstanceManager instance_manager;
/*      */   private SpeedManager speed_manager;
/*      */   private final CryptoManager crypto_manager;
/*      */   
/*      */   public static AzureusCore create() throws AzureusCoreException {
/*  151 */     try { class_mon.enter();
/*      */       
/*  153 */       if (singleton != null)
/*      */       {
/*  155 */         throw new AzureusCoreException("Azureus core already instantiated");
/*      */       }
/*      */       
/*  158 */       singleton = new AzureusCoreImpl();
/*      */       
/*  160 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*  164 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isCoreAvailable()
/*      */   {
/*  171 */     return singleton != null;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isCoreRunning()
/*      */   {
/*  177 */     return (singleton != null) && (singleton.isStarted());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static AzureusCore getSingleton()
/*      */     throws AzureusCoreException
/*      */   {
/*  185 */     if (singleton == null)
/*      */     {
/*  187 */       throw new AzureusCoreException("core not instantiated");
/*      */     }
/*      */     
/*  190 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final NATTraverser nat_traverser;
/*      */   
/*      */ 
/*      */   private final long create_time;
/*      */   
/*      */ 
/*      */   private volatile boolean started;
/*      */   
/*      */   private volatile boolean stopped;
/*      */   
/*      */   private volatile boolean restarting;
/*      */   
/*  207 */   final CopyOnWriteList lifecycle_listeners = new CopyOnWriteList();
/*  208 */   private final List operation_listeners = new ArrayList();
/*      */   
/*  210 */   private final CopyOnWriteList<PowerManagementListener> power_listeners = new CopyOnWriteList();
/*      */   
/*  212 */   final AESemaphore stopping_sem = new AESemaphore("AzureusCore::stopping");
/*      */   
/*  214 */   private final AEMonitor this_mon = new AEMonitor("AzureusCore");
/*      */   
/*  216 */   public static boolean SUPPRESS_CLASSLOADER_ERRORS = true;
/*      */   
/*  218 */   private boolean ca_shutdown_computer_after_stop = false;
/*  219 */   private long ca_last_time_downloading = -1L;
/*  220 */   private long ca_last_time_seeding = -1L;
/*      */   
/*  222 */   private boolean ra_restarting = false;
/*  223 */   private long ra_last_total_data = -1L;
/*  224 */   private long ra_last_data_time = 0L;
/*      */   
/*  226 */   private boolean prevent_sleep_remove_trigger = false;
/*      */   private FileLock file_lock;
/*      */   private boolean js_plugin_install_tried;
/*      */   
/*      */   protected AzureusCoreImpl() {
/*  231 */     this.create_time = SystemTime.getCurrentTime();
/*      */     
/*  233 */     if ((!SUPPRESS_CLASSLOADER_ERRORS) && (!(getClass().getClassLoader() instanceof PrimaryClassloader))) {
/*  234 */       System.out.println("###\nWarning: Core not instantiated through a PrimaryClassloader, this can lead to restricted functionality or bugs in future versions\n###");
/*      */     }
/*  236 */     COConfigurationManager.initialise();
/*      */     
/*  238 */     MessageText.loadBundle();
/*      */     
/*  240 */     AEDiagnostics.startup(COConfigurationManager.getBooleanParameter("diags.enable.pending.writes", false));
/*      */     
/*  242 */     COConfigurationManager.setParameter("diags.enable.pending.writes", false);
/*      */     
/*  244 */     AEDiagnostics.markDirty();
/*      */     
/*  246 */     AETemporaryFileHandler.startup();
/*      */     
/*  248 */     AEThread2.setOurThread();
/*      */     
/*      */ 
/*      */ 
/*  252 */     COConfigurationManager.setParameter("azureus.application.directory", new File(SystemProperties.getApplicationPath()).getAbsolutePath());
/*  253 */     COConfigurationManager.setParameter("azureus.user.directory", new File(SystemProperties.getUserPath()).getAbsolutePath());
/*      */     
/*  255 */     this.crypto_manager = CryptoManagerFactory.getSingleton();
/*      */     
/*  257 */     PlatformManagerFactory.getPlatformManager().addListener(new PlatformManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public int eventOccurred(int type)
/*      */       {
/*      */ 
/*  264 */         if (type == 1)
/*      */         {
/*  266 */           if (Logger.isEnabled()) {
/*  267 */             Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Platform manager requested shutdown"));
/*      */           }
/*      */           
/*  270 */           COConfigurationManager.save();
/*      */           
/*  272 */           AzureusCoreImpl.this.requestStop();
/*      */           
/*  274 */           return 0;
/*      */         }
/*  276 */         if (type == 2)
/*      */         {
/*  278 */           if (Logger.isEnabled()) {
/*  279 */             Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Platform manager requested suspend"));
/*      */           }
/*      */           
/*  282 */           COConfigurationManager.save();
/*      */         }
/*  284 */         else if (type == 3)
/*      */         {
/*  286 */           if (Logger.isEnabled()) {
/*  287 */             Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Platform manager requested resume"));
/*      */           }
/*      */           
/*  290 */           AzureusCoreImpl.this.announceAll(true);
/*      */         }
/*      */         
/*  293 */         return -1;
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  298 */     });
/*  299 */     CustomizationManagerFactory.getSingleton().initialize();
/*      */     
/*  301 */     com.aelitis.azureus.core.proxy.AEProxySelectorFactory.getSelector();
/*      */     
/*  303 */     NetworkManager.getSingleton();
/*      */     
/*  305 */     PeerManager.getSingleton();
/*      */     
/*      */ 
/*      */ 
/*  309 */     com.aelitis.azureus.plugins.clientid.ClientIDPlugin.initialize(this);
/*      */     
/*  311 */     this.pi = PluginInitializer.getSingleton(this);
/*      */     
/*      */ 
/*  314 */     this.instance_manager = AZInstanceManagerFactory.getSingleton(new AZInstanceManagerAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public String getID()
/*      */       {
/*      */ 
/*  321 */         return COConfigurationManager.getStringParameter("ID", "");
/*      */       }
/*      */       
/*      */ 
/*      */       public java.net.InetAddress getPublicAddress()
/*      */       {
/*  327 */         return PluginInitializer.getDefaultInterface().getUtilities().getPublicAddress();
/*      */       }
/*      */       
/*      */ 
/*      */       public int[] getPorts()
/*      */       {
/*  333 */         return new int[] { TCPNetworkManager.getSingleton().getTCPListeningPortNumber(), UDPNetworkManager.getSingleton().getUDPListeningPortNumber(), UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber() };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public AZInstanceManagerAdapter.VCPublicAddress getVCPublicAddress()
/*      */       {
/*  342 */         new AZInstanceManagerAdapter.VCPublicAddress()
/*      */         {
/*      */ 
/*  345 */           private final VersionCheckClient vcc = VersionCheckClient.getSingleton();
/*      */           
/*      */ 
/*      */           public String getAddress()
/*      */           {
/*  350 */             return this.vcc.getExternalIpAddress(true, false);
/*      */           }
/*      */           
/*      */ 
/*      */           public long getCacheTime()
/*      */           {
/*  356 */             return VersionCheckClient.getSingleton().getCacheTime(false);
/*      */           }
/*      */         };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public AZInstanceTracked.TrackTarget track(byte[] hash)
/*      */       {
/*  365 */         List dms = AzureusCoreImpl.this.getGlobalManager().getDownloadManagers();
/*      */         
/*  367 */         Iterator it = dms.iterator();
/*      */         
/*  369 */         org.gudy.azureus2.core3.download.DownloadManager matching_dm = null;
/*      */         try
/*      */         {
/*  372 */           while (it.hasNext())
/*      */           {
/*  374 */             org.gudy.azureus2.core3.download.DownloadManager dm = (org.gudy.azureus2.core3.download.DownloadManager)it.next();
/*      */             
/*  376 */             TOTorrent torrent = dm.getTorrent();
/*      */             
/*  378 */             if (torrent != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  383 */               byte[] sha1_hash = (byte[])dm.getData("AZInstanceManager::sha1_hash");
/*      */               
/*  385 */               if (sha1_hash == null)
/*      */               {
/*  387 */                 sha1_hash = new SHA1Simple().calculateHash(torrent.getHash());
/*      */                 
/*  389 */                 dm.setData("AZInstanceManager::sha1_hash", sha1_hash);
/*      */               }
/*      */               
/*  392 */               if (java.util.Arrays.equals(hash, sha1_hash))
/*      */               {
/*  394 */                 matching_dm = dm;
/*      */                 
/*  396 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         } catch (Throwable e) {
/*  401 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*  404 */         if (matching_dm == null)
/*      */         {
/*  406 */           return null;
/*      */         }
/*      */         
/*  409 */         if (!matching_dm.getDownloadState().isPeerSourceEnabled("Plugin"))
/*      */         {
/*  411 */           return null;
/*      */         }
/*      */         
/*  414 */         int dm_state = matching_dm.getState();
/*      */         
/*  416 */         if ((dm_state == 100) || (dm_state == 70))
/*      */         {
/*  418 */           return null;
/*      */         }
/*      */         
/*      */         try
/*      */         {
/*  423 */           final Object target = org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl.getDownloadStatic(matching_dm);
/*      */           
/*  425 */           final boolean is_seed = matching_dm.isDownloadComplete(true);
/*      */           
/*  427 */           new AZInstanceTracked.TrackTarget()
/*      */           {
/*      */ 
/*      */             public Object getTarget()
/*      */             {
/*      */ 
/*  433 */               return target;
/*      */             }
/*      */             
/*      */ 
/*      */             public boolean isSeed()
/*      */             {
/*  439 */               return is_seed;
/*      */             }
/*      */           };
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*  445 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public DHTPlugin getDHTPlugin()
/*      */       {
/*  452 */         PluginInterface pi = AzureusCoreImpl.this.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*      */         
/*  454 */         if (pi != null)
/*      */         {
/*  456 */           return (DHTPlugin)pi.getPlugin();
/*      */         }
/*      */         
/*  459 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public UPnPPlugin getUPnPPlugin()
/*      */       {
/*  465 */         PluginInterface pi = AzureusCoreImpl.this.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*      */         
/*  467 */         if (pi != null)
/*      */         {
/*  469 */           return (UPnPPlugin)pi.getPlugin();
/*      */         }
/*      */         
/*  472 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void addListener(final AZInstanceManagerAdapter.StateListener listener)
/*      */       {
/*  479 */         AzureusCoreImpl.this.addLifecycleListener(new com.aelitis.azureus.core.AzureusCoreLifecycleAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void started(AzureusCore core)
/*      */           {
/*      */ 
/*  486 */             listener.started();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void stopping(AzureusCore core)
/*      */           {
/*  493 */             listener.stopped();
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */     
/*  499 */     if (COConfigurationManager.getBooleanParameter("speedmanager.enable", true))
/*      */     {
/*  501 */       this.speed_manager = com.aelitis.azureus.core.speedmanager.SpeedManagerFactory.createSpeedManager(this, new com.aelitis.azureus.core.speedmanager.SpeedManagerAdapter()
/*      */       {
/*      */         private static final int UPLOAD_SPEED_ADJUST_MIN_KB_SEC = 10;
/*      */         
/*      */ 
/*      */         private static final int DOWNLOAD_SPEED_ADJUST_MIN_KB_SEC = 300;
/*      */         
/*      */ 
/*      */         private boolean setting_limits;
/*      */         
/*      */ 
/*      */ 
/*      */         public int getCurrentProtocolUploadSpeed(int average_period)
/*      */         {
/*  515 */           if (AzureusCoreImpl.this.global_manager != null)
/*      */           {
/*  517 */             GlobalManagerStats stats = AzureusCoreImpl.this.global_manager.getStats();
/*      */             
/*  519 */             return stats.getProtocolSendRateNoLAN(average_period);
/*      */           }
/*      */           
/*      */ 
/*  523 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public int getCurrentDataUploadSpeed(int average_period)
/*      */         {
/*  531 */           if (AzureusCoreImpl.this.global_manager != null)
/*      */           {
/*  533 */             GlobalManagerStats stats = AzureusCoreImpl.this.global_manager.getStats();
/*      */             
/*  535 */             return stats.getDataSendRateNoLAN(average_period);
/*      */           }
/*      */           
/*      */ 
/*  539 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public int getCurrentProtocolDownloadSpeed(int average_period)
/*      */         {
/*  547 */           if (AzureusCoreImpl.this.global_manager != null) {
/*  548 */             GlobalManagerStats stats = AzureusCoreImpl.this.global_manager.getStats();
/*  549 */             return stats.getProtocolReceiveRateNoLAN(average_period);
/*      */           }
/*  551 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public int getCurrentDataDownloadSpeed(int average_period)
/*      */         {
/*  559 */           if (AzureusCoreImpl.this.global_manager != null) {
/*  560 */             GlobalManagerStats stats = AzureusCoreImpl.this.global_manager.getStats();
/*  561 */             return stats.getDataReceiveRateNoLAN(average_period);
/*      */           }
/*  563 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public int getCurrentUploadLimit()
/*      */         {
/*  570 */           String key = TransferSpeedValidator.getActiveUploadParameter(AzureusCoreImpl.this.global_manager);
/*      */           
/*  572 */           int k_per_second = COConfigurationManager.getIntParameter(key);
/*      */           
/*      */           int bytes_per_second;
/*      */           int bytes_per_second;
/*  576 */           if (k_per_second == 0)
/*      */           {
/*  578 */             bytes_per_second = Integer.MAX_VALUE;
/*      */           }
/*      */           else
/*      */           {
/*  582 */             bytes_per_second = k_per_second * 1024;
/*      */           }
/*      */           
/*  585 */           return bytes_per_second;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void setCurrentUploadLimit(int bytes_per_second)
/*      */         {
/*  592 */           if (bytes_per_second != getCurrentUploadLimit())
/*      */           {
/*  594 */             String key = TransferSpeedValidator.getActiveUploadParameter(AzureusCoreImpl.this.global_manager);
/*      */             
/*      */             int k_per_second;
/*      */             int k_per_second;
/*  598 */             if (bytes_per_second == Integer.MAX_VALUE)
/*      */             {
/*  600 */               k_per_second = 0;
/*      */             }
/*      */             else
/*      */             {
/*  604 */               k_per_second = (bytes_per_second + 1023) / 1024;
/*      */             }
/*      */             
/*  607 */             if (k_per_second > 0)
/*      */             {
/*  609 */               k_per_second = Math.max(k_per_second, 10);
/*      */             }
/*      */             
/*  612 */             COConfigurationManager.setParameter(key, k_per_second);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public int getCurrentDownloadLimit()
/*      */         {
/*  619 */           return TransferSpeedValidator.getGlobalDownloadRateLimitBytesPerSecond();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void setCurrentDownloadLimit(int bytes_per_second)
/*      */         {
/*  626 */           if (bytes_per_second == Integer.MAX_VALUE)
/*      */           {
/*  628 */             bytes_per_second = 0;
/*      */           }
/*      */           
/*  631 */           if (bytes_per_second > 0)
/*      */           {
/*  633 */             bytes_per_second = Math.max(bytes_per_second, 307200);
/*      */           }
/*      */           
/*  636 */           TransferSpeedValidator.setGlobalDownloadRateLimitBytesPerSecond(bytes_per_second);
/*      */         }
/*      */         
/*      */ 
/*      */         public Object getLimits()
/*      */         {
/*  642 */           String up_key = TransferSpeedValidator.getActiveUploadParameter(AzureusCoreImpl.this.global_manager);
/*  643 */           String down_key = TransferSpeedValidator.getDownloadParameter();
/*      */           
/*  645 */           return new Object[] { up_key, new Integer(COConfigurationManager.getIntParameter(up_key)), down_key, new Integer(COConfigurationManager.getIntParameter(down_key)) };
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
/*      */         public void setLimits(Object limits, boolean do_up, boolean do_down)
/*      */         {
/*  660 */           if (limits == null)
/*      */           {
/*  662 */             return;
/*      */           }
/*      */           try {
/*  665 */             if (this.setting_limits) {
/*      */               return;
/*      */             }
/*      */             
/*      */ 
/*  670 */             this.setting_limits = true;
/*      */             
/*  672 */             Object[] bits = (Object[])limits;
/*      */             
/*  674 */             if (do_up)
/*      */             {
/*  676 */               COConfigurationManager.setParameter((String)bits[0], ((Integer)bits[1]).intValue());
/*      */             }
/*      */             
/*  679 */             if (do_down)
/*      */             {
/*  681 */               COConfigurationManager.setParameter((String)bits[2], ((Integer)bits[3]).intValue());
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/*  686 */             this.setting_limits = false;
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  693 */     this.nat_traverser = new NATTraverser(this);
/*      */     
/*  695 */     com.aelitis.azureus.core.peermanager.nat.PeerNATTraverser.initialise(this);
/*      */     
/*  697 */     BackupManagerFactory.getManager(this);
/*      */     
/*      */ 
/*      */ 
/*  701 */     SimpleTimer.addEvent("AzureusCore:gc", SystemTime.getOffsetTime(60000L), new TimerEventPerformer()
/*      */     {
/*      */       public void perform(TimerEvent event) {}
/*      */     });
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
/*      */   public long getCreateTime()
/*      */   {
/*  718 */     return this.create_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void announceAll(boolean force)
/*      */   {
/*  725 */     Logger.log(new LogEvent(LOGID, "Updating trackers"));
/*      */     
/*  727 */     GlobalManager gm = getGlobalManager();
/*      */     
/*  729 */     if (gm != null)
/*      */     {
/*  731 */       List downloads = gm.getDownloadManagers();
/*      */       
/*  733 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  735 */       for (int i = 0; i < downloads.size(); i++)
/*      */       {
/*  737 */         org.gudy.azureus2.core3.download.DownloadManager dm = (org.gudy.azureus2.core3.download.DownloadManager)downloads.get(i);
/*      */         
/*  739 */         Long last_announce_l = (Long)dm.getUserData("AzureusCore:announce_key");
/*      */         
/*  741 */         long last_announce = last_announce_l == null ? this.create_time : last_announce_l.longValue();
/*      */         
/*  743 */         TRTrackerAnnouncer an = dm.getTrackerClient();
/*      */         
/*  745 */         if (an != null)
/*      */         {
/*  747 */           TRTrackerAnnouncerResponse last_announce_response = an.getLastResponse();
/*      */           
/*  749 */           if ((now - last_announce > 900000L) || (last_announce_response == null) || (last_announce_response.getStatus() == 0) || (force))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  754 */             dm.setUserData("AzureusCore:announce_key", new Long(now));
/*      */             
/*  756 */             Logger.log(new LogEvent(LOGID, "    updating tracker for " + dm.getDisplayName()));
/*      */             
/*  758 */             dm.requestTrackerAnnounce(true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  764 */     PluginInterface dht_tracker_pi = getPluginManager().getPluginInterfaceByClass(DHTTrackerPlugin.class);
/*      */     
/*  766 */     if (dht_tracker_pi != null)
/*      */     {
/*  768 */       ((DHTTrackerPlugin)dht_tracker_pi.getPlugin()).announceAll();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public LocaleUtil getLocaleUtil()
/*      */   {
/*  775 */     return LocaleUtil.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */   public File getLockFile()
/*      */   {
/*  781 */     return new File(SystemProperties.getUserPath(), ".azlock");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canStart()
/*      */   {
/*  789 */     if (System.getProperty("azureus.instance.lock.disable", "0").equals("1"))
/*      */     {
/*  791 */       return true;
/*      */     }
/*      */     
/*  794 */     synchronized (this)
/*      */     {
/*  796 */       if (this.file_lock != null)
/*      */       {
/*  798 */         return true;
/*      */       }
/*      */       
/*  801 */       File lock_file = getLockFile();
/*      */       try
/*      */       {
/*  804 */         RandomAccessFile raf = new RandomAccessFile(lock_file, "rw");
/*      */         
/*  806 */         FileChannel channel = raf.getChannel();
/*      */         
/*  808 */         for (int i = 0; i < 15; i++)
/*      */         {
/*  810 */           this.file_lock = channel.tryLock();
/*      */           
/*  812 */           if (this.file_lock != null)
/*      */           {
/*  814 */             return true;
/*      */           }
/*      */           try
/*      */           {
/*  818 */             Thread.sleep(1000L);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*  826 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void start()
/*      */     throws AzureusCoreException
/*      */   {
/*  835 */     if (!canStart())
/*      */     {
/*  837 */       throw new AzureusCoreException("Core: already started (alternative process)");
/*      */     }
/*      */     
/*  840 */     AEThread2.setOurThread();
/*      */     try
/*      */     {
/*  843 */       this.this_mon.enter();
/*      */       
/*  845 */       if (this.started)
/*      */       {
/*  847 */         throw new AzureusCoreException("Core: already started");
/*      */       }
/*      */       
/*  850 */       if (this.stopped)
/*      */       {
/*  852 */         throw new AzureusCoreException("Core: already stopped");
/*      */       }
/*      */       
/*  855 */       this.started = true;
/*      */     }
/*      */     finally
/*      */     {
/*  859 */       this.this_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*  863 */     if ("1".equals(System.getProperty("azureus.safemode"))) {
/*  864 */       if (Logger.isEnabled()) {
/*  865 */         Logger.log(new LogEvent(LOGID, "Safe mode enabled"));
/*      */       }
/*  867 */       Constants.isSafeMode = true;
/*  868 */       System.setProperty("azureus.loadplugins", "0");
/*  869 */       System.setProperty("azureus.disabledownloads", "1");
/*  870 */       System.setProperty("azureus.skipSWTcheck", "1");
/*      */       
/*      */ 
/*  873 */       Logger.log(new LogAlert(false, 1, "You are running " + Constants.APP_NAME + " in safe mode - you " + "can change your configuration, but any downloads added will " + "not be remembered when you close " + Constants.APP_NAME + "."));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  883 */     String sDelayCore = System.getProperty("delay.core", null);
/*  884 */     if (sDelayCore != null) {
/*      */       try {
/*  886 */         long delayCore = Long.parseLong(sDelayCore);
/*  887 */         Thread.sleep(delayCore);
/*      */       } catch (Exception e) {
/*  889 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  895 */     AEThread2 pluginload = new AEThread2("PluginLoader", true)
/*      */     {
/*      */       public void run() {
/*  898 */         if (Logger.isEnabled())
/*  899 */           Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Loading of Plugins starts"));
/*  900 */         AzureusCoreImpl.this.pi.loadPlugins(AzureusCoreImpl.this, false, !"0".equals(System.getProperty("azureus.loadplugins")), true, true);
/*  901 */         if (Logger.isEnabled()) {
/*  902 */           Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Loading of Plugins complete"));
/*      */ 
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*  909 */     };
/*  910 */     pluginload.run();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  925 */     this.global_manager = org.gudy.azureus2.core3.global.GlobalManagerFactory.create(this, null, 0L);
/*      */     
/*  927 */     if (this.stopped) {
/*  928 */       System.err.println("Core stopped while starting");
/*  929 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  937 */     if (this.stopped) {
/*  938 */       System.err.println("Core stopped while starting");
/*  939 */       return;
/*      */     }
/*      */     
/*  942 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void process(VuzeFile[] files, int expected_types)
/*      */       {
/*      */ 
/*      */ 
/*  950 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  952 */           VuzeFile vf = files[i];
/*      */           
/*  954 */           VuzeFileComponent[] comps = vf.getComponents();
/*      */           
/*  956 */           for (int j = 0; j < comps.length; j++)
/*      */           {
/*  958 */             VuzeFileComponent comp = comps[j];
/*      */             
/*  960 */             int comp_type = comp.getType();
/*      */             
/*  962 */             if (comp_type == 2048)
/*      */             {
/*  964 */               PluginInterface default_pi = AzureusCoreImpl.this.getPluginManager().getDefaultPluginInterface();
/*      */               
/*  966 */               Map map = comp.getContent();
/*      */               
/*      */ 
/*      */               try
/*      */               {
/*  971 */                 String url = MapUtils.getMapString(map, "torrent_url", null);
/*      */                 Torrent torrent;
/*  973 */                 if (url != null)
/*      */                 {
/*  975 */                   TorrentDownloader dl = default_pi.getTorrentManager().getURLDownloader(new java.net.URL(url));
/*      */                   
/*  977 */                   torrent = dl.download();
/*      */                 }
/*      */                 else
/*      */                 {
/*  981 */                   String tf = MapUtils.getMapString(map, "torrent_file", null);
/*      */                   Torrent torrent;
/*  983 */                   if (tf != null)
/*      */                   {
/*  985 */                     File file = new File(tf);
/*      */                     
/*  987 */                     if ((!file.canRead()) || (file.isDirectory()))
/*      */                     {
/*  989 */                       throw new Exception("torrent_file '" + tf + "' is invalid");
/*      */                     }
/*      */                     
/*  992 */                     torrent = default_pi.getTorrentManager().createFromBEncodedFile(file);
/*      */                   }
/*      */                   else
/*      */                   {
/*  996 */                     throw new Exception("torrent_url or torrent_file must be specified");
/*      */                   }
/*      */                 }
/*      */                 Torrent torrent;
/* 1000 */                 File dest = null;
/*      */                 
/* 1002 */                 String save_folder = MapUtils.getMapString(map, "save_folder", null);
/*      */                 
/* 1004 */                 if (save_folder != null)
/*      */                 {
/* 1006 */                   dest = new File(save_folder, torrent.getName());
/*      */                 }
/*      */                 else
/*      */                 {
/* 1010 */                   String save_file = MapUtils.getMapString(map, "save_file", null);
/*      */                   
/* 1012 */                   if (save_file != null)
/*      */                   {
/* 1014 */                     dest = new File(save_file);
/*      */                   }
/*      */                 }
/*      */                 
/* 1018 */                 if (dest != null)
/*      */                 {
/* 1020 */                   dest.getParentFile().mkdirs();
/*      */                 }
/*      */                 
/* 1023 */                 default_pi.getDownloadManager().addDownload(torrent, null, dest);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1027 */                 Debug.out(e);
/*      */               }
/*      */               
/* 1030 */               comp.setProcessed();
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/* 1038 */     });
/* 1039 */     triggerLifeCycleComponentCreated(this.global_manager);
/*      */     
/* 1041 */     this.pi.initialisePlugins();
/*      */     
/* 1043 */     if (this.stopped) {
/* 1044 */       System.err.println("Core stopped while starting");
/* 1045 */       return;
/*      */     }
/*      */     
/* 1048 */     if (Logger.isEnabled()) {
/* 1049 */       Logger.log(new LogEvent(LOGID, "Initializing Plugins complete"));
/*      */     }
/*      */     try {
/* 1052 */       PluginInterface dht_pi = getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*      */       
/* 1054 */       if (dht_pi != null)
/*      */       {
/* 1056 */         dht_pi.addEventListener(new PluginEventListener()
/*      */         {
/*      */ 
/* 1059 */           private boolean first_dht = true;
/*      */           
/*      */ 
/*      */ 
/*      */           public void handleEvent(PluginEvent ev)
/*      */           {
/* 1065 */             if (ev.getType() == 1024)
/*      */             {
/* 1067 */               if (this.first_dht)
/*      */               {
/* 1069 */                 this.first_dht = false;
/*      */                 
/* 1071 */                 DHT dht = (DHT)ev.getValue();
/*      */                 
/* 1073 */                 dht.addListener(new com.aelitis.azureus.core.dht.DHTListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void speedTesterAvailable(DHTSpeedTester tester)
/*      */                   {
/*      */ 
/* 1080 */                     if (AzureusCoreImpl.this.speed_manager != null)
/*      */                     {
/* 1082 */                       AzureusCoreImpl.this.speed_manager.setSpeedTester(tester);
/*      */                     }
/*      */                     
/*      */                   }
/* 1086 */                 });
/* 1087 */                 AzureusCoreImpl.this.global_manager.addListener(new org.gudy.azureus2.core3.global.GlobalManagerAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void seedingStatusChanged(boolean seeding_only_mode, boolean b)
/*      */                   {
/*      */ 
/*      */ 
/* 1095 */                     AzureusCoreImpl.7.this.checkConfig();
/*      */                   }
/*      */                   
/* 1098 */                 });
/* 1099 */                 COConfigurationManager.addAndFireParameterListeners(new String[] { "Auto Upload Speed Enabled", "Auto Upload Speed Seeding Enabled" }, new ParameterListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void parameterChanged(String parameterName)
/*      */                   {
/*      */ 
/*      */ 
/* 1108 */                     AzureusCoreImpl.7.this.checkConfig();
/*      */                   }
/*      */                 });
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           protected void checkConfig()
/*      */           {
/* 1119 */             if (AzureusCoreImpl.this.speed_manager != null)
/*      */             {
/* 1121 */               AzureusCoreImpl.this.speed_manager.setEnabled(TransferSpeedValidator.isAutoSpeedActive(AzureusCoreImpl.this.global_manager));
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/* 1130 */     if (COConfigurationManager.getBooleanParameter("Resume Downloads On Start"))
/*      */     {
/* 1132 */       this.global_manager.resumeDownloads();
/*      */     }
/*      */     
/* 1135 */     VersionCheckClient.getSingleton().initialise();
/*      */     
/* 1137 */     this.instance_manager.initialize();
/*      */     
/* 1139 */     NetworkManager.getSingleton().initialize(this);
/*      */     
/* 1141 */     SpeedLimitHandler.getSingleton(this);
/*      */     
/* 1143 */     Runtime.getRuntime().addShutdownHook(new AEThread("Shutdown Hook") {
/*      */       public void runSupport() {
/* 1145 */         Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Shutdown hook triggered"));
/* 1146 */         AzureusCoreImpl.this.stop();
/*      */       }
/*      */       
/*      */ 
/* 1150 */     });
/* 1151 */     DelayedTask delayed_task = org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.addDelayedTask("Core", new Runnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*      */ 
/* 1159 */         new AEThread2("core:delayTask", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/* 1164 */             AEDiagnostics.checkDumpsAndNatives();
/*      */             
/* 1166 */             COConfigurationManager.setParameter("diags.enable.pending.writes", true);
/*      */             
/* 1168 */             AEDiagnostics.flushPendingLogs();
/*      */             
/* 1170 */             NetworkAdmin na = NetworkAdmin.getSingleton();
/*      */             
/* 1172 */             na.runInitialChecks(AzureusCoreImpl.this);
/*      */             
/* 1174 */             na.addPropertyChangeListener(new com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener()
/*      */             {
/*      */               private String last_as;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void propertyChanged(String property)
/*      */               {
/* 1183 */                 NetworkAdmin na = NetworkAdmin.getSingleton();
/*      */                 
/* 1185 */                 if (property.equals("Network Interfaces"))
/*      */                 {
/* 1187 */                   boolean found_usable = false;
/*      */                   
/* 1189 */                   NetworkAdminNetworkInterface[] intf = na.getInterfaces();
/*      */                   
/* 1191 */                   for (int i = 0; i < intf.length; i++)
/*      */                   {
/* 1193 */                     NetworkAdminNetworkInterfaceAddress[] addresses = intf[i].getAddresses();
/*      */                     
/* 1195 */                     for (int j = 0; j < addresses.length; j++)
/*      */                     {
/* 1197 */                       if (!addresses[j].isLoopback())
/*      */                       {
/* 1199 */                         found_usable = true;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 1206 */                   if (!found_usable)
/*      */                   {
/* 1208 */                     return;
/*      */                   }
/*      */                   
/* 1211 */                   Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Network interfaces have changed (new=" + na.getNetworkInterfacesAsString() + ")"));
/*      */                   
/* 1213 */                   AzureusCoreImpl.this.announceAll(false);
/*      */                 }
/* 1215 */                 else if (property.equals("AS"))
/*      */                 {
/* 1217 */                   String as = na.getCurrentASN().getAS();
/*      */                   
/* 1219 */                   if (this.last_as == null)
/*      */                   {
/* 1221 */                     this.last_as = as;
/*      */                   }
/* 1223 */                   else if (!as.equals(this.last_as))
/*      */                   {
/* 1225 */                     Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "AS has changed (new=" + as + ")"));
/*      */                     
/* 1227 */                     this.last_as = as;
/*      */                     
/* 1229 */                     AzureusCoreImpl.this.announceAll(false);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/* 1234 */             });
/* 1235 */             AzureusCoreImpl.this.setupSleepAndCloseActions();
/*      */           }
/*      */           
/*      */         }.start();
/*      */       }
/* 1240 */     });
/* 1241 */     delayed_task.queue();
/*      */     
/* 1243 */     if (this.stopped) {
/* 1244 */       System.err.println("Core stopped while starting");
/* 1245 */       return;
/*      */     }
/*      */     
/* 1248 */     PairingManagerFactory.getSingleton();
/*      */     
/*      */ 
/* 1251 */     mon_coreRunningListeners.enter();
/*      */     AzureusCoreRunningListener[] runningListeners;
/* 1253 */     try { AzureusCoreRunningListener[] runningListeners; if (coreRunningListeners == null) {
/* 1254 */         runningListeners = new AzureusCoreRunningListener[0];
/*      */       } else {
/* 1256 */         runningListeners = (AzureusCoreRunningListener[])coreRunningListeners.toArray(new AzureusCoreRunningListener[0]);
/* 1257 */         coreRunningListeners = null;
/*      */       }
/*      */     }
/*      */     finally {
/* 1261 */       mon_coreRunningListeners.exit();
/*      */     }
/*      */     
/*      */ 
/* 1265 */     new AEThread2("Plugin Init Complete", false)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/* 1271 */           PlatformManagerFactory.getPlatformManager().startup(AzureusCoreImpl.this);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1275 */           Debug.out("PlatformManager: init failed", e);
/*      */         }
/*      */         
/* 1278 */         Iterator it = AzureusCoreImpl.this.lifecycle_listeners.iterator();
/*      */         
/* 1280 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/* 1283 */             AzureusCoreLifecycleListener listener = (AzureusCoreLifecycleListener)it.next();
/*      */             
/* 1285 */             if (!listener.requiresPluginInitCompleteBeforeStartedEvent())
/*      */             {
/* 1287 */               listener.started(AzureusCoreImpl.this);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1291 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/* 1295 */         AzureusCoreImpl.this.pi.initialisationComplete();
/*      */         
/* 1297 */         it = AzureusCoreImpl.this.lifecycle_listeners.iterator();
/*      */         
/* 1299 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/* 1302 */             AzureusCoreLifecycleListener listener = (AzureusCoreLifecycleListener)it.next();
/*      */             
/* 1304 */             if (listener.requiresPluginInitCompleteBeforeStartedEvent())
/*      */             {
/* 1306 */               listener.started(AzureusCoreImpl.this);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1310 */             Debug.printStackTrace(e);
/*      */ 
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/* 1319 */     }.start();
/* 1320 */     ThreadPool tp = new ThreadPool("Trigger AzureusCoreRunning Listeners", 3);
/* 1321 */     for (final AzureusCoreRunningListener l : runningListeners) {
/*      */       try {
/* 1323 */         tp.run(new AERunnable() {
/*      */           public void runSupport() {
/* 1325 */             l.azureusCoreRunning(AzureusCoreImpl.this);
/*      */           }
/*      */         });
/*      */       } catch (Throwable t) {
/* 1329 */         Debug.out(t);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isInitThread()
/*      */   {
/* 1339 */     return AEThread2.isOurThread(Thread.currentThread());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStarted()
/*      */   {
/* 1345 */     mon_coreRunningListeners.enter();
/*      */     try {
/* 1347 */       return (this.started) && (coreRunningListeners == null);
/*      */     } finally {
/* 1349 */       mon_coreRunningListeners.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void triggerLifeCycleComponentCreated(AzureusCoreComponent component)
/*      */   {
/* 1357 */     Iterator it = this.lifecycle_listeners.iterator();
/*      */     
/* 1359 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1362 */         ((AzureusCoreLifecycleListener)it.next()).componentCreated(this, component);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1366 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void runNonDaemon(final Runnable r)
/*      */     throws AzureusCoreException
/*      */   {
/* 1377 */     if (!Thread.currentThread().isDaemon())
/*      */     {
/* 1379 */       r.run();
/*      */     }
/*      */     else
/*      */     {
/* 1383 */       final AESemaphore sem = new AESemaphore("AzureusCore:runNonDaemon");
/*      */       
/* 1385 */       final Throwable[] error = { null };
/*      */       
/* 1387 */       new AEThread2("AzureusCore:runNonDaemon", false)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/* 1394 */             r.run();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1398 */             error[0] = e;
/*      */           }
/*      */           finally
/*      */           {
/* 1402 */             sem.release();
/*      */           }
/*      */           
/*      */         }
/* 1406 */       }.start();
/* 1407 */       sem.reserve();
/*      */       
/* 1409 */       if (error[0] != null)
/*      */       {
/* 1411 */         if ((error[0] instanceof AzureusCoreException))
/*      */         {
/* 1413 */           throw ((AzureusCoreException)error[0]);
/*      */         }
/*      */         
/*      */ 
/* 1417 */         throw new AzureusCoreException("Operation failed", error[0]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stop()
/*      */     throws AzureusCoreException
/*      */   {
/* 1428 */     runNonDaemon(new AERunnable() {
/*      */       public void runSupport() {
/* 1430 */         if (Logger.isEnabled()) {
/* 1431 */           Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Stop operation starts"));
/*      */         }
/* 1433 */         AzureusCoreImpl.this.stopSupport(false, true);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void stopSupport(final boolean for_restart, final boolean apply_updates)
/*      */     throws AzureusCoreException
/*      */   {
/* 1445 */     AEDiagnostics.flushPendingLogs();
/*      */     
/* 1447 */     boolean wait_and_return = false;
/*      */     try
/*      */     {
/* 1450 */       this.this_mon.enter();
/*      */       
/* 1452 */       if (this.stopped)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1457 */         COConfigurationManager.save();
/*      */         
/* 1459 */         wait_and_return = true;
/*      */       }
/*      */       else
/*      */       {
/* 1463 */         this.stopped = true;
/*      */         
/* 1465 */         if (!this.started)
/*      */         {
/* 1467 */           Logger.log(new LogEvent(LOGID, "Core not started"));
/*      */           
/*      */ 
/*      */ 
/* 1471 */           if (AEDiagnostics.isDirty())
/*      */           {
/* 1473 */             AEDiagnostics.markClean();
/*      */           }
/*      */           
/* 1476 */           this.stopping_sem.releaseForever(); return;
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1483 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1486 */     if (wait_and_return)
/*      */     {
/* 1488 */       Logger.log(new LogEvent(LOGID, "Waiting for stop to complete"));
/*      */       
/* 1490 */       this.stopping_sem.reserve();
/*      */       
/* 1492 */       return;
/*      */     }
/*      */     
/* 1495 */     SimpleTimer.addEvent("ShutFail", SystemTime.getOffsetTime(30000L), new TimerEventPerformer()
/*      */     {
/*      */       boolean die_die_die;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */         
/*      */         
/*      */ 
/*      */ 
/* 1508 */         if (this.die_die_die)
/*      */         {
/* 1510 */           Debug.out("Shutdown blocked, force exiting");
/*      */           
/* 1512 */           AzureusCoreImpl.this.stopping_sem.releaseForever();
/*      */           
/*      */ 
/*      */ 
/* 1516 */           if (for_restart)
/*      */           {
/* 1518 */             AzureusRestarterFactory.create(AzureusCoreImpl.this).restart(false);
/*      */           }
/* 1520 */           else if (apply_updates)
/*      */           {
/* 1522 */             if (AzureusCoreImpl.this.getPluginManager().getDefaultPluginInterface().getUpdateManager().getInstallers().length > 0)
/*      */             {
/* 1524 */               AzureusRestarterFactory.create(AzureusCoreImpl.this).restart(true);
/*      */             }
/*      */           }
/*      */           
/* 1528 */           if (AzureusCoreImpl.this.ca_shutdown_computer_after_stop)
/*      */           {
/* 1530 */             if (apply_updates)
/*      */             {
/*      */               try
/*      */               {
/* 1534 */                 Thread.sleep(10000L);
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */             
/*      */ 
/*      */             try
/*      */             {
/* 1542 */               PlatformManagerFactory.getPlatformManager().shutdown(1);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1546 */               Debug.out("PlatformManager: shutdown failed", e);
/*      */             }
/*      */           }
/*      */           
/* 1550 */           SESecurityManager.exitVM(0);
/*      */         }
/*      */         
/* 1553 */         this.die_die_die = true;
/*      */         
/* 1555 */         SimpleTimer.addEvent("ShutFail", SystemTime.getOffsetTime(30000L), this);
/*      */       }
/*      */       
/* 1558 */     });
/* 1559 */     Object sync_listeners = new ArrayList();
/* 1560 */     List async_listeners = new ArrayList();
/*      */     
/* 1562 */     Iterator it = this.lifecycle_listeners.iterator();
/*      */     
/* 1564 */     while (it.hasNext()) {
/* 1565 */       AzureusCoreLifecycleListener l = (AzureusCoreLifecycleListener)it.next();
/*      */       
/* 1567 */       if (l.syncInvokeRequired()) {
/* 1568 */         ((List)sync_listeners).add(l);
/*      */       } else {
/* 1570 */         async_listeners.add(l);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1575 */       if (Logger.isEnabled()) {
/* 1576 */         Logger.log(new LogEvent(LOGID, "Invoking synchronous 'stopping' listeners"));
/*      */       }
/* 1578 */       for (int i = 0; i < ((List)sync_listeners).size(); i++) {
/*      */         try {
/* 1580 */           ((AzureusCoreLifecycleListener)((List)sync_listeners).get(i)).stopping(this);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1584 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/* 1588 */       if (Logger.isEnabled()) {
/* 1589 */         Logger.log(new LogEvent(LOGID, "Invoking asynchronous 'stopping' listeners"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1595 */       ListenerManager.dispatchWithTimeout(async_listeners, new ListenerManagerDispatcher()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void dispatch(Object listener, int type, Object value) {
/* 1605 */           ((AzureusCoreLifecycleListener)listener).stopping(AzureusCoreImpl.this); } }, 10000L);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1611 */       if (Logger.isEnabled()) {
/* 1612 */         Logger.log(new LogEvent(LOGID, "Stopping global manager"));
/*      */       }
/* 1614 */       if (this.global_manager != null) {
/* 1615 */         this.global_manager.stopGlobalManager();
/*      */       }
/*      */       
/* 1618 */       if (Logger.isEnabled()) {
/* 1619 */         Logger.log(new LogEvent(LOGID, "Invoking synchronous 'stopped' listeners"));
/*      */       }
/* 1621 */       for (int i = 0; i < ((List)sync_listeners).size(); i++) {
/*      */         try {
/* 1623 */           ((AzureusCoreLifecycleListener)((List)sync_listeners).get(i)).stopped(this);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1627 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/* 1631 */       if (Logger.isEnabled()) {
/* 1632 */         Logger.log(new LogEvent(LOGID, "Invoking asynchronous 'stopped' listeners"));
/*      */       }
/* 1634 */       ListenerManager.dispatchWithTimeout(async_listeners, new ListenerManagerDispatcher()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void dispatch(Object listener, int type, Object value) {
/* 1644 */           ((AzureusCoreLifecycleListener)listener).stopped(AzureusCoreImpl.this); } }, 10000L);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1649 */       if (Logger.isEnabled()) {
/* 1650 */         Logger.log(new LogEvent(LOGID, "Waiting for quiescence"));
/*      */       }
/* 1652 */       org.gudy.azureus2.core3.util.NonDaemonTaskRunner.waitUntilIdle();
/*      */       
/*      */ 
/*      */ 
/* 1656 */       AEDiagnostics.markClean();
/*      */       
/* 1658 */       if (Logger.isEnabled()) {
/* 1659 */         Logger.log(new LogEvent(LOGID, "Stop operation completes"));
/*      */       }
/*      */       
/*      */ 
/* 1663 */       if ((apply_updates) && (getPluginManager().getDefaultPluginInterface().getUpdateManager().getInstallers().length > 0))
/*      */       {
/*      */ 
/* 1666 */         AzureusRestarterFactory.create(this).restart(true);
/*      */       }
/*      */       
/* 1669 */       if (System.getProperty("skip.shutdown.nondeamon.check", "0").equals("1")) {
/*      */         return;
/*      */       }
/*      */       try
/*      */       {
/* 1674 */         Class c = Class.forName("sun.awt.AWTAutoShutdown");
/*      */         
/* 1676 */         if (c != null) {
/* 1677 */           c.getMethod("notifyToolkitThreadFree", new Class[0]).invoke(null, new Object[0]);
/*      */         }
/*      */       }
/*      */       catch (Throwable t) {}
/*      */       
/* 1682 */       if (this.ca_shutdown_computer_after_stop)
/*      */       {
/* 1684 */         if (apply_updates)
/*      */         {
/*      */           try
/*      */           {
/* 1688 */             Thread.sleep(10000L);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1696 */           PlatformManagerFactory.getPlatformManager().shutdown(1);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1700 */           Debug.out("PlatformManager: shutdown failed", e);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 1705 */         ThreadGroup tg = Thread.currentThread().getThreadGroup();
/*      */         
/* 1707 */         while (tg.getParent() != null)
/*      */         {
/* 1709 */           tg = tg.getParent();
/*      */         }
/*      */         
/* 1712 */         Thread[] threads = new Thread[tg.activeCount() + 1024];
/*      */         
/* 1714 */         tg.enumerate(threads, true);
/*      */         
/* 1716 */         boolean bad_found = false;
/*      */         
/* 1718 */         for (int i = 0; i < threads.length; i++)
/*      */         {
/* 1720 */           Thread t = threads[i];
/*      */           
/* 1722 */           if ((t != null) && (t.isAlive()) && (t != Thread.currentThread()) && (!t.isDaemon()) && (!AEThread2.isOurThread(t)))
/*      */           {
/* 1724 */             bad_found = true;
/*      */             
/* 1726 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1730 */         if (bad_found)
/*      */         {
/* 1732 */           new AEThread2("VMKiller", true)
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try
/*      */               {
/* 1738 */                 Thread.sleep(10000L);
/*      */                 
/* 1740 */                 ThreadGroup tg = Thread.currentThread().getThreadGroup();
/*      */                 
/* 1742 */                 Thread[] threads = new Thread[tg.activeCount() + 1024];
/*      */                 
/* 1744 */                 tg.enumerate(threads, true);
/*      */                 
/* 1746 */                 String bad_found = "";
/*      */                 
/* 1748 */                 for (int i = 0; i < threads.length; i++)
/*      */                 {
/* 1750 */                   Thread t = threads[i];
/*      */                   
/* 1752 */                   if ((t != null) && (t.isAlive()) && (!t.isDaemon()) && (!AEThread2.isOurThread(t)))
/*      */                   {
/* 1754 */                     String details = t.getName();
/*      */                     
/* 1756 */                     StackTraceElement[] trace = t.getStackTrace();
/*      */                     
/* 1758 */                     if (trace.length > 0)
/*      */                     {
/* 1760 */                       details = details + "[";
/*      */                       
/* 1762 */                       for (int j = 0; j < trace.length; j++)
/*      */                       {
/* 1764 */                         details = details + (j == 0 ? "" : ",") + trace[j];
/*      */                       }
/*      */                       
/* 1767 */                       details = details + "]";
/*      */                     }
/*      */                     
/* 1770 */                     bad_found = bad_found + (bad_found.length() == 0 ? "" : ", ") + details;
/*      */                   }
/*      */                 }
/*      */                 
/* 1774 */                 Debug.out("Non-daemon thread(s) found: '" + bad_found + "' - force closing VM");
/*      */                 
/* 1776 */                 SESecurityManager.exitVM(0);
/*      */ 
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */             
/*      */ 
/*      */           }.start();
/*      */         }
/*      */         
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     finally
/*      */     {
/* 1791 */       this.stopping_sem.releaseForever();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestStop()
/*      */     throws AzureusCoreException
/*      */   {
/* 1801 */     if (this.stopped) {
/* 1802 */       return;
/*      */     }
/* 1804 */     runNonDaemon(new AERunnable()
/*      */     {
/*      */       public void runSupport() {
/* 1807 */         Iterator it = AzureusCoreImpl.this.lifecycle_listeners.iterator();
/*      */         
/* 1809 */         while (it.hasNext())
/*      */         {
/* 1811 */           if (!((AzureusCoreLifecycleListener)it.next()).stopRequested(AzureusCoreImpl.this))
/*      */           {
/* 1813 */             if (Logger.isEnabled()) {
/* 1814 */               Logger.log(new LogEvent(AzureusCoreImpl.LOGID, 1, "Request to stop the core has been denied"));
/*      */             }
/*      */             
/* 1817 */             return;
/*      */           }
/*      */         }
/*      */         
/* 1821 */         AzureusCoreImpl.this.stop();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void restart()
/*      */     throws AzureusCoreException
/*      */   {
/* 1831 */     runNonDaemon(new AERunnable() {
/*      */       public void runSupport() {
/* 1833 */         if (Logger.isEnabled()) {
/* 1834 */           Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Restart operation starts"));
/*      */         }
/* 1836 */         AzureusCoreImpl.this.checkRestartSupported();
/*      */         
/* 1838 */         AzureusCoreImpl.this.restarting = true;
/*      */         
/* 1840 */         AzureusCoreImpl.this.stopSupport(true, false);
/*      */         
/* 1842 */         if (Logger.isEnabled()) {
/* 1843 */           Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Restart operation: stop complete,restart initiated"));
/*      */         }
/*      */         
/* 1846 */         AzureusRestarterFactory.create(AzureusCoreImpl.this).restart(false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestRestart()
/*      */     throws AzureusCoreException
/*      */   {
/* 1856 */     runNonDaemon(new AERunnable() {
/*      */       public void runSupport() {
/* 1858 */         AzureusCoreImpl.this.checkRestartSupported();
/*      */         
/* 1860 */         Iterator it = AzureusCoreImpl.this.lifecycle_listeners.iterator();
/*      */         
/* 1862 */         while (it.hasNext()) {
/* 1863 */           AzureusCoreLifecycleListener l = (AzureusCoreLifecycleListener)it.next();
/*      */           
/* 1865 */           if (!l.restartRequested(AzureusCoreImpl.this))
/*      */           {
/* 1867 */             if (Logger.isEnabled()) {
/* 1868 */               Logger.log(new LogEvent(AzureusCoreImpl.LOGID, 1, "Request to restart the core has been denied"));
/*      */             }
/*      */             
/*      */ 
/* 1872 */             return;
/*      */           }
/*      */         }
/*      */         
/* 1876 */         AzureusCoreImpl.this.restart();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRestarting()
/*      */   {
/* 1884 */     return this.restarting;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void checkRestartSupported()
/*      */     throws AzureusCoreException
/*      */   {
/* 1892 */     if (getPluginManager().getPluginInterfaceByClass("org.gudy.azureus2.update.UpdaterPatcher") == null) {
/* 1893 */       Logger.log(new LogAlert(true, 3, "Can't restart without the 'azupdater' plugin installed"));
/*      */       
/*      */ 
/* 1896 */       throw new AzureusCoreException("Can't restart without the 'azupdater' plugin installed");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void saveState()
/*      */   {
/* 1903 */     GlobalManager gm = this.global_manager;
/*      */     
/* 1905 */     if (gm != null)
/*      */     {
/* 1907 */       gm.saveState();
/*      */     }
/*      */     
/* 1910 */     COConfigurationManager.save();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public GlobalManager getGlobalManager()
/*      */     throws AzureusCoreException
/*      */   {
/* 1918 */     if (this.global_manager == null)
/*      */     {
/* 1920 */       throw new AzureusCoreException("Core not running");
/*      */     }
/*      */     
/* 1923 */     return this.global_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public org.gudy.azureus2.core3.tracker.host.TRHost getTrackerHost()
/*      */     throws AzureusCoreException
/*      */   {
/* 1931 */     return org.gudy.azureus2.core3.tracker.host.TRHostFactory.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public org.gudy.azureus2.plugins.PluginManagerDefaults getPluginManagerDefaults()
/*      */     throws AzureusCoreException
/*      */   {
/* 1939 */     return PluginManager.getDefaults();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PluginManager getPluginManager()
/*      */     throws AzureusCoreException
/*      */   {
/* 1949 */     return PluginInitializer.getDefaultInterface().getPluginManager();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public org.gudy.azureus2.core3.ipfilter.IpFilterManager getIpFilterManager()
/*      */     throws AzureusCoreException
/*      */   {
/* 1957 */     return IpFilterManagerFactory.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */   public AZInstanceManager getInstanceManager()
/*      */   {
/* 1963 */     return this.instance_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManager getSpeedManager()
/*      */   {
/* 1969 */     return this.speed_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public CryptoManager getCryptoManager()
/*      */   {
/* 1975 */     return this.crypto_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public NATTraverser getNATTraverser()
/*      */   {
/* 1981 */     return this.nat_traverser;
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupSleepAndCloseActions()
/*      */   {
/* 1987 */     if (PlatformManagerFactory.getPlatformManager().hasCapability(org.gudy.azureus2.platform.PlatformManagerCapabilities.PreventComputerSleep))
/*      */     {
/* 1989 */       COConfigurationManager.addAndFireParameterListeners(new String[] { "Prevent Sleep Downloading", "Prevent Sleep FP Seeding" }, new ParameterListener()
/*      */       {
/*      */         private TimerEventPeriodic timer_event;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String parameterName)
/*      */         {
/* 2002 */           synchronized (this)
/*      */           {
/* 2004 */             boolean dl = COConfigurationManager.getBooleanParameter("Prevent Sleep Downloading");
/* 2005 */             boolean se = COConfigurationManager.getBooleanParameter("Prevent Sleep FP Seeding");
/*      */             
/* 2007 */             boolean active = (dl) || (se);
/*      */             try
/*      */             {
/* 2010 */               AzureusCoreImpl.this.setPreventComputerSleep(PlatformManagerFactory.getPlatformManager(), active, "config change");
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2014 */               Debug.out(e);
/*      */             }
/*      */             
/*      */ 
/* 2018 */             if (!active)
/*      */             {
/* 2020 */               if (this.timer_event != null)
/*      */               {
/* 2022 */                 this.timer_event.cancel();
/*      */                 
/* 2024 */                 this.timer_event = null;
/*      */               }
/*      */               
/*      */             }
/* 2028 */             else if (this.timer_event == null)
/*      */             {
/* 2030 */               this.timer_event = SimpleTimer.addPeriodicEvent("core:sleepAct", 120000L, new TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 2040 */                   if (!AzureusCoreImpl.this.stopped)
/*      */                   {
/* 2042 */                     AzureusCoreImpl.this.checkSleepActions();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/* 2053 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "On Downloading Complete Do", "On Seeding Complete Do", "Auto Restart When Idle" }, new ParameterListener()
/*      */     {
/*      */       private TimerEventPeriodic timer_event;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/* 2067 */         String dl_act = COConfigurationManager.getStringParameter("On Downloading Complete Do");
/* 2068 */         String se_act = COConfigurationManager.getStringParameter("On Seeding Complete Do");
/*      */         
/* 2070 */         int restart_after = COConfigurationManager.getIntParameter("Auto Restart When Idle");
/*      */         
/* 2072 */         synchronized (this)
/*      */         {
/* 2074 */           boolean dl_nothing = dl_act.equals("Nothing");
/* 2075 */           boolean se_nothing = se_act.equals("Nothing");
/*      */           
/* 2077 */           if (dl_nothing)
/*      */           {
/* 2079 */             AzureusCoreImpl.this.ca_last_time_downloading = -1L;
/*      */           }
/*      */           
/* 2082 */           if (se_nothing)
/*      */           {
/* 2084 */             AzureusCoreImpl.this.ca_last_time_seeding = -1L;
/*      */           }
/*      */           
/* 2087 */           if ((dl_nothing) && (se_nothing) && (restart_after == 0))
/*      */           {
/* 2089 */             if (this.timer_event != null)
/*      */             {
/* 2091 */               this.timer_event.cancel();
/*      */               
/* 2093 */               this.timer_event = null;
/*      */             }
/*      */           }
/*      */           else {
/* 2097 */             if (this.timer_event == null)
/*      */             {
/* 2099 */               this.timer_event = SimpleTimer.addPeriodicEvent("core:closeAct", 30000L, new TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/* 2109 */                   if (!AzureusCoreImpl.this.stopped)
/*      */                   {
/* 2111 */                     if (!AzureusCoreImpl.this.checkRestartAction())
/*      */                     {
/* 2113 */                       AzureusCoreImpl.this.checkCloseActions();
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             
/* 2120 */             AzureusCoreImpl.this.checkCloseActions();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkSleepActions()
/*      */   {
/* 2130 */     boolean ps_downloading = COConfigurationManager.getBooleanParameter("Prevent Sleep Downloading");
/* 2131 */     boolean ps_fp_seed = COConfigurationManager.getBooleanParameter("Prevent Sleep FP Seeding");
/*      */     
/* 2133 */     String declining_subsystems = "";
/*      */     
/* 2135 */     for (PowerManagementListener l : this.power_listeners) {
/*      */       try
/*      */       {
/* 2138 */         if (!l.requestPowerStateChange(1, null))
/*      */         {
/* 2140 */           declining_subsystems = declining_subsystems + (declining_subsystems.length() == 0 ? "" : ",") + l.getPowerName();
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2145 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 2149 */     if ((declining_subsystems.length() == 0) && (!ps_downloading) && (!ps_fp_seed))
/*      */     {
/* 2151 */       return;
/*      */     }
/*      */     
/* 2154 */     PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*      */     
/* 2156 */     boolean prevent_sleep = false;
/* 2157 */     String prevent_reason = null;
/*      */     
/* 2159 */     if (declining_subsystems.length() > 0)
/*      */     {
/* 2161 */       prevent_sleep = true;
/* 2162 */       prevent_reason = "subsystems declined sleep: " + declining_subsystems;
/*      */     }
/*      */     else
/*      */     {
/* 2166 */       List<org.gudy.azureus2.core3.download.DownloadManager> managers = getGlobalManager().getDownloadManagers();
/*      */       
/* 2168 */       for (org.gudy.azureus2.core3.download.DownloadManager manager : managers)
/*      */       {
/* 2170 */         int state = manager.getState();
/*      */         
/* 2172 */         if ((state == 55) || (manager.getDownloadState().getFlag(512L)))
/*      */         {
/*      */ 
/* 2175 */           if (ps_downloading)
/*      */           {
/* 2177 */             prevent_sleep = true;
/* 2178 */             prevent_reason = "active downloads";
/*      */             
/* 2180 */             break;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 2185 */           if (state == 50)
/*      */           {
/* 2187 */             PEPeerManager pm = manager.getPeerManager();
/*      */             
/* 2189 */             if (pm != null)
/*      */             {
/* 2191 */               if (pm.hasDownloadablePiece())
/*      */               {
/* 2193 */                 if (ps_downloading)
/*      */                 {
/* 2195 */                   prevent_sleep = true;
/* 2196 */                   prevent_reason = "active downloads";
/*      */                   
/* 2198 */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/*      */               else {
/* 2204 */                 state = 60;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 2209 */           if ((state == 60) && (ps_fp_seed))
/*      */           {
/* 2211 */             DiskManager disk_manager = manager.getDiskManager();
/*      */             
/* 2213 */             if ((disk_manager != null) && (disk_manager.getCompleteRecheckStatus() != -1))
/*      */             {
/*      */ 
/*      */ 
/* 2217 */               if (ps_downloading)
/*      */               {
/* 2219 */                 prevent_sleep = true;
/* 2220 */                 prevent_reason = "active downloads";
/*      */                 
/* 2222 */                 break;
/*      */               }
/*      */             }
/*      */             else {
/*      */               try
/*      */               {
/* 2228 */                 DefaultRankCalculator calc = com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin.getRankCalculator(org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils.wrap(manager));
/*      */                 
/* 2230 */                 if (calc.getCachedIsFP())
/*      */                 {
/* 2232 */                   prevent_sleep = true;
/* 2233 */                   prevent_reason = "first-priority seeding";
/*      */                   
/* 2235 */                   break;
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2246 */     if (prevent_sleep != platform.getPreventComputerSleep())
/*      */     {
/* 2248 */       if (prevent_sleep)
/*      */       {
/* 2250 */         this.prevent_sleep_remove_trigger = false;
/*      */ 
/*      */ 
/*      */       }
/* 2254 */       else if (!this.prevent_sleep_remove_trigger)
/*      */       {
/* 2256 */         this.prevent_sleep_remove_trigger = true;
/*      */         
/* 2258 */         return;
/*      */       }
/*      */       
/*      */ 
/* 2262 */       if (prevent_reason == null)
/*      */       {
/* 2264 */         if ((ps_downloading) && (ps_fp_seed))
/*      */         {
/* 2266 */           prevent_reason = "no active downloads or first-priority seeding";
/*      */         }
/* 2268 */         else if (ps_downloading)
/*      */         {
/* 2270 */           prevent_reason = "no active downloads";
/*      */         }
/*      */         else
/*      */         {
/* 2274 */           prevent_reason = "no active first-priority seeding";
/*      */         }
/*      */       }
/*      */       
/* 2278 */       setPreventComputerSleep(platform, prevent_sleep, prevent_reason);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setPreventComputerSleep(PlatformManager platform, boolean prevent_sleep, String prevent_reason)
/*      */   {
/* 2288 */     for (PowerManagementListener l : this.power_listeners) {
/*      */       try
/*      */       {
/* 2291 */         l.informPowerStateChange(1, new Object[] { Boolean.valueOf(prevent_sleep), prevent_reason });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2295 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 2299 */     Logger.log(new LogEvent(LOGID, "Computer sleep prevented state changed to '" + prevent_sleep + "' due to " + prevent_reason));
/*      */     try
/*      */     {
/* 2302 */       platform.setPreventComputerSleep(prevent_sleep);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2306 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean checkRestartAction()
/*      */   {
/* 2313 */     if (this.ra_restarting)
/*      */     {
/* 2315 */       return true;
/*      */     }
/*      */     
/* 2318 */     int restart_after = COConfigurationManager.getIntParameter("Auto Restart When Idle");
/*      */     
/* 2320 */     if (restart_after > 0)
/*      */     {
/* 2322 */       List<org.gudy.azureus2.core3.download.DownloadManager> managers = getGlobalManager().getDownloadManagers();
/*      */       
/* 2324 */       boolean active = false;
/*      */       
/* 2326 */       for (org.gudy.azureus2.core3.download.DownloadManager manager : managers)
/*      */       {
/* 2328 */         int state = manager.getState();
/*      */         
/* 2330 */         if ((state == 50) || (state == 60))
/*      */         {
/*      */ 
/* 2333 */           active = true;
/*      */           
/* 2335 */           break;
/*      */         }
/*      */       }
/*      */       
/* 2339 */       if (active)
/*      */       {
/* 2341 */         GlobalManagerStats stats = this.global_manager.getStats();
/*      */         
/* 2343 */         long totals = stats.getTotalDataBytesReceived() + stats.getTotalDataBytesSent();
/*      */         
/* 2345 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 2347 */         if (totals == this.ra_last_total_data)
/*      */         {
/* 2349 */           if (now - this.ra_last_data_time >= 60000 * restart_after)
/*      */           {
/* 2351 */             this.ra_restarting = true;
/*      */             
/* 2353 */             String message = MessageText.getString("core.restart.alert", new String[] { String.valueOf(restart_after) });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2360 */             UIFunctions ui_functions = UIFunctionsManager.getUIFunctions();
/*      */             
/* 2362 */             if (ui_functions != null)
/*      */             {
/* 2364 */               ui_functions.forceNotify(0, null, message, null, new Object[0], -1);
/*      */             }
/*      */             
/* 2367 */             Logger.log(new LogAlert(false, 0, message));
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2373 */             new DelayedEvent("CoreRestart", 10000L, new AERunnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*      */ 
/* 2381 */                 AzureusCoreImpl.this.requestRestart();
/*      */               }
/*      */               
/* 2384 */             });
/* 2385 */             return true;
/*      */           }
/*      */         }
/*      */         else {
/* 2389 */           this.ra_last_total_data = totals;
/* 2390 */           this.ra_last_data_time = now;
/*      */         }
/*      */       }
/*      */       else {
/* 2394 */         this.ra_last_total_data = -1L;
/*      */       }
/*      */     }
/*      */     else {
/* 2398 */       this.ra_last_total_data = -1L;
/*      */     }
/*      */     
/* 2401 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkCloseActions()
/*      */   {
/* 2407 */     List<org.gudy.azureus2.core3.download.DownloadManager> managers = getGlobalManager().getDownloadManagers();
/*      */     
/* 2409 */     boolean is_downloading = false;
/* 2410 */     boolean is_seeding = false;
/*      */     
/* 2412 */     for (org.gudy.azureus2.core3.download.DownloadManager manager : managers)
/*      */     {
/* 2414 */       if (manager.isPaused())
/*      */       {
/*      */ 
/*      */ 
/* 2418 */         return;
/*      */       }
/*      */       
/* 2421 */       if (manager.getDownloadState().getFlag(512L))
/*      */       {
/*      */ 
/*      */ 
/* 2425 */         return;
/*      */       }
/*      */       
/* 2428 */       if (!manager.getDownloadState().getFlag(16L))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2433 */         int state = manager.getState();
/*      */         
/* 2435 */         if (state == 55)
/*      */         {
/* 2437 */           is_downloading = true;
/*      */         }
/*      */         else
/*      */         {
/* 2441 */           if (state == 50)
/*      */           {
/* 2443 */             PEPeerManager pm = manager.getPeerManager();
/*      */             
/* 2445 */             if (pm != null)
/*      */             {
/* 2447 */               if (pm.hasDownloadablePiece())
/*      */               {
/* 2449 */                 is_downloading = true;
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/* 2455 */                 state = 60;
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 2460 */           else if (!manager.isDownloadComplete(false))
/*      */           {
/* 2462 */             if ((state != 70) && (state != 100))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2469 */               is_downloading = true;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 2474 */           if (state == 60)
/*      */           {
/* 2476 */             DiskManager disk_manager = manager.getDiskManager();
/*      */             
/* 2478 */             if ((disk_manager != null) && (disk_manager.getCompleteRecheckStatus() != -1))
/*      */             {
/*      */ 
/*      */ 
/* 2482 */               is_downloading = true;
/*      */             }
/*      */             else
/*      */             {
/* 2486 */               is_seeding = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2492 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 2494 */     if (is_downloading)
/*      */     {
/* 2496 */       this.ca_last_time_downloading = now;
/* 2497 */       this.ca_last_time_seeding = -1L;
/*      */     }
/* 2499 */     else if (is_seeding)
/*      */     {
/* 2501 */       this.ca_last_time_seeding = now;
/*      */     }
/*      */     
/* 2504 */     String dl_act = COConfigurationManager.getStringParameter("On Downloading Complete Do");
/*      */     
/* 2506 */     if (!dl_act.equals("Nothing"))
/*      */     {
/* 2508 */       if ((this.ca_last_time_downloading >= 0L) && (!is_downloading) && (now - this.ca_last_time_downloading >= 30000L))
/*      */       {
/* 2510 */         executeInternalCloseAction(true, true, dl_act, null);
/*      */       }
/*      */     }
/*      */     
/* 2514 */     String se_act = COConfigurationManager.getStringParameter("On Seeding Complete Do");
/*      */     
/* 2516 */     if (!se_act.equals("Nothing"))
/*      */     {
/* 2518 */       if ((this.ca_last_time_seeding >= 0L) && (!is_seeding) && (now - this.ca_last_time_seeding >= 30000L))
/*      */       {
/* 2520 */         executeInternalCloseAction(true, false, se_act, null);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void executeCloseAction(String action, String reason)
/*      */   {
/* 2530 */     executeInternalCloseAction(false, false, action, reason);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void executeInternalCloseAction(boolean obey_reset, boolean download_trigger, String action, String reason)
/*      */   {
/* 2542 */     this.ca_last_time_downloading = -1L;
/* 2543 */     this.ca_last_time_seeding = -1L;
/*      */     
/* 2545 */     String type_str = reason == null ? MessageText.getString("core.shutdown." + (download_trigger ? "dl" : "se")) : reason;
/* 2546 */     String action_str = MessageText.getString("ConfigView.label.stop." + action);
/*      */     
/* 2548 */     String message = MessageText.getString("core.shutdown.alert", new String[] { action_str, type_str });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2556 */     UIFunctions ui_functions = UIFunctionsManager.getUIFunctions();
/*      */     
/* 2558 */     if (ui_functions != null)
/*      */     {
/* 2560 */       ui_functions.forceNotify(0, null, message, null, new Object[0], -1);
/*      */     }
/*      */     
/* 2563 */     Logger.log(new LogAlert(false, 0, message));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2569 */     if (COConfigurationManager.getBooleanParameter("Prompt To Abort Shutdown"))
/*      */     {
/* 2571 */       UIManager ui_manager = org.gudy.azureus2.plugins.utils.StaticUtilities.getUIManager(30000L);
/*      */       
/* 2573 */       if (ui_manager != null)
/*      */       {
/* 2575 */         Map<String, Object> options = new HashMap();
/*      */         
/* 2577 */         options.put("auto-close-ms", Integer.valueOf(30000));
/*      */         
/* 2579 */         if (ui_manager.showMessageBox("core.shutdown.prompt.title", "core.shutdown.prompt.msg", 66L, options) == 2L)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2585 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2590 */     executeCloseActionSupport(obey_reset, download_trigger, action, reason);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void executeCloseActionSupport(boolean obey_reset, final boolean download_trigger, final String action, String reason)
/*      */   {
/* 2602 */     this.ca_last_time_downloading = -1L;
/* 2603 */     this.ca_last_time_seeding = -1L;
/*      */     
/* 2605 */     boolean reset = (obey_reset) && (COConfigurationManager.getBooleanParameter("Stop Triggers Auto Reset"));
/*      */     
/* 2607 */     if (reset)
/*      */     {
/* 2609 */       if (download_trigger)
/*      */       {
/* 2611 */         COConfigurationManager.setParameter("On Downloading Complete Do", "Nothing");
/*      */       }
/*      */       else
/*      */       {
/* 2615 */         COConfigurationManager.setParameter("On Seeding Complete Do", "Nothing");
/*      */       }
/*      */     }
/*      */     
/* 2619 */     new DelayedEvent("CoreShutdown", 10000L, new AERunnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*      */ 
/* 2627 */         Logger.log(new LogEvent(AzureusCoreImpl.LOGID, "Executing close action '" + action + "' due to " + (download_trigger ? "downloading" : "seeding") + " completion"));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2633 */         if (action.equals("QuitVuze"))
/*      */         {
/* 2635 */           AzureusCoreImpl.this.requestStop();
/*      */         }
/* 2637 */         else if ((action.equals("Sleep")) || (action.equals("Hibernate")))
/*      */         {
/* 2639 */           AzureusCoreImpl.this.announceAll(true);
/*      */           try
/*      */           {
/* 2642 */             PlatformManagerFactory.getPlatformManager().shutdown(action.equals("Sleep") ? 4 : 2);
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2647 */             Debug.out("PlatformManager: shutdown failed", e);
/*      */           }
/*      */         }
/* 2650 */         else if (action.equals("Shutdown"))
/*      */         {
/* 2652 */           AzureusCoreImpl.this.ca_shutdown_computer_after_stop = true;
/*      */           
/* 2654 */           AzureusCoreImpl.this.requestStop();
/*      */         }
/* 2656 */         else if (action.startsWith("RunScript"))
/*      */         {
/*      */           String script;
/*      */           String script;
/* 2660 */           if (download_trigger)
/*      */           {
/* 2662 */             script = COConfigurationManager.getStringParameter("On Downloading Complete Script", "");
/*      */           }
/*      */           else
/*      */           {
/* 2666 */             script = COConfigurationManager.getStringParameter("On Seeding Complete Script", "");
/*      */           }
/*      */           
/* 2669 */           AzureusCoreImpl.this.executeScript(script, action, download_trigger);
/*      */         }
/*      */         else
/*      */         {
/* 2673 */           Debug.out("Unknown close action '" + action + "'");
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
/*      */ 
/*      */   private void executeScript(String script, String action, boolean download_trigger)
/*      */   {
/* 2687 */     String script_type = "";
/*      */     
/* 2689 */     if ((script.length() >= 10) && (script.substring(0, 10).toLowerCase(java.util.Locale.US).startsWith("javascript")))
/*      */     {
/* 2691 */       int p1 = script.indexOf('(');
/*      */       
/* 2693 */       int p2 = script.lastIndexOf(')');
/*      */       
/* 2695 */       if ((p1 != -1) && (p2 != -1))
/*      */       {
/* 2697 */         script = script.substring(p1 + 1, p2).trim();
/*      */         
/* 2699 */         if ((script.startsWith("\"")) && (script.endsWith("\"")))
/*      */         {
/* 2701 */           script = script.substring(1, script.length() - 1);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 2706 */         script = script.replaceAll("\\\\\"", "\"");
/*      */         
/* 2708 */         script_type = "javascript";
/*      */       }
/*      */     }
/*      */     
/* 2712 */     File script_file = null;
/*      */     
/* 2714 */     if (script_type == "")
/*      */     {
/* 2716 */       script_file = new File(script.trim());
/*      */       
/* 2718 */       if (!script_file.isFile())
/*      */       {
/* 2720 */         Logger.log(new LogEvent(LOGID, "Script failed to run - '" + script_file + "' isn't a valid script file"));
/*      */         
/* 2722 */         Debug.out("Invalid script: " + script_file);
/*      */         
/* 2724 */         return;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 2729 */       boolean close_vuze = action.equals("RunScriptAndClose");
/*      */       
/* 2731 */       if (!close_vuze)
/*      */       {
/*      */ 
/*      */ 
/* 2735 */         announceAll(true);
/*      */       }
/*      */       
/* 2738 */       if (script_file != null)
/*      */       {
/* 2740 */         getPluginManager().getDefaultPluginInterface().getUtilities().createProcess(script_file.getAbsolutePath());
/*      */       }
/*      */       else
/*      */       {
/* 2744 */         boolean provider_found = false;
/*      */         
/* 2746 */         List<ScriptProvider> providers = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getUtilities().getScriptProviders();
/*      */         
/* 2748 */         for (ScriptProvider p : providers)
/*      */         {
/* 2750 */           if (p.getScriptType() == script_type)
/*      */           {
/* 2752 */             provider_found = true;
/*      */             
/* 2754 */             Map<String, Object> bindings = new HashMap();
/*      */             
/* 2756 */             String intent = "shutdown(\"" + action + "\")";
/*      */             
/* 2758 */             bindings.put("intent", intent);
/*      */             
/* 2760 */             bindings.put("is_downloading_complete", Boolean.valueOf(download_trigger));
/*      */             
/* 2762 */             p.eval(script, bindings);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 2767 */         if (!provider_found)
/*      */         {
/* 2769 */           if (!this.js_plugin_install_tried)
/*      */           {
/* 2771 */             this.js_plugin_install_tried = true;
/*      */             
/* 2773 */             PluginUtils.installJavaScriptPlugin();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2778 */       if (close_vuze)
/*      */       {
/* 2780 */         requestStop();
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2785 */       Logger.log(new LogAlert(true, 3, "Script failed to run - '" + script + "'", e));
/*      */       
/* 2787 */       Debug.out("Invalid script: " + script, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void createOperation(final int type, AzureusCoreOperationTask task)
/*      */   {
/* 2796 */     final AzureusCoreOperationTask[] f_task = { task };
/*      */     
/* 2798 */     AzureusCoreOperation op = new AzureusCoreOperation()
/*      */     {
/*      */ 
/*      */       public int getOperationType()
/*      */       {
/*      */ 
/* 2804 */         return type;
/*      */       }
/*      */       
/*      */ 
/*      */       public AzureusCoreOperationTask getTask()
/*      */       {
/* 2810 */         return f_task[0];
/*      */       }
/*      */     };
/*      */     
/*      */ 
/* 2815 */     for (int i = 0; i < this.operation_listeners.size(); i++)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2820 */       if (((AzureusCoreOperationListener)this.operation_listeners.get(i)).operationCreated(op))
/*      */       {
/* 2822 */         f_task[0] = null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2828 */     if (f_task[0] != null)
/*      */     {
/* 2830 */       task.run(op);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addLifecycleListener(AzureusCoreLifecycleListener l)
/*      */   {
/* 2838 */     this.lifecycle_listeners.add(l);
/*      */     
/* 2840 */     if (this.global_manager != null)
/*      */     {
/* 2842 */       l.componentCreated(this, this.global_manager);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeLifecycleListener(AzureusCoreLifecycleListener l)
/*      */   {
/* 2850 */     this.lifecycle_listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addOperationListener(AzureusCoreOperationListener l)
/*      */   {
/* 2857 */     this.operation_listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeOperationListener(AzureusCoreOperationListener l)
/*      */   {
/* 2864 */     this.operation_listeners.remove(l);
/*      */   }
/*      */   
/*      */   public static void addCoreRunningListener(AzureusCoreRunningListener l) {
/* 2868 */     mon_coreRunningListeners.enter();
/*      */     try {
/* 2870 */       if (coreRunningListeners != null) {
/* 2871 */         coreRunningListeners.add(l); return;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2876 */       mon_coreRunningListeners.exit();
/*      */     }
/*      */     
/* 2879 */     l.azureusCoreRunning(getSingleton());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPowerManagementListener(PowerManagementListener listener)
/*      */   {
/* 2886 */     this.power_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePowerManagementListener(PowerManagementListener listener)
/*      */   {
/* 2893 */     this.power_listeners.remove(listener);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/impl/AzureusCoreImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */