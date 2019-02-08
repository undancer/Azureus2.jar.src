/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.devices.Device;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager.DeviceManufacturer;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager.UnassociatedDevice;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerDiscoveryListener;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerListener;
/*      */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*      */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloaderManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceSearchListener;
/*      */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*      */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformDevicesMessenger;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.utils.PowerManagementListener;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DeviceManagerImpl
/*      */   implements DeviceManager, DeviceOfflineDownloaderManager, PowerManagementListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final String LOGGER_NAME = "Devices";
/*      */   private static final String CONFIG_FILE = "devices.config";
/*      */   private static final String AUTO_SEARCH_CONFIG_KEY = "devices.config.auto_search";
/*      */   private static final int AUTO_HIDE_OLD_DAYS_DEFAULT = 31;
/*      */   private static final String AUTO_HIDE_OLD_CONFIG_KEY = "devices.config.auto_hide_old";
/*      */   private static final String RSS_ENABLE_CONFIG_KEY = "devices.config.rss_enable";
/*      */   private static final String OD_ENABLED_CONFIG_KEY = "devices.config.od.enabled";
/*      */   private static final String OD_IS_AUTO_CONFIG_KEY = "devices.config.od.auto";
/*      */   private static final String OD_INCLUDE_PRIVATE_CONFIG_KEY = "devices.config.od.inc_priv";
/*      */   private static final String TRANSCODE_DIR_DEFAULT = "transcodes";
/*      */   private static final String CONFIG_DEFAULT_WORK_DIR = "devices.config.def_work_dir";
/*      */   private static final String CONFIG_DISABLE_SLEEP = "devices.config.disable_sleep";
/*      */   protected static final int DEVICE_UPDATE_PERIOD = 5000;
/*      */   protected static final int DEVICE_AUTO_HIDE_CHECK_PERIOD = 120000;
/*      */   protected static final int DEVICE_AUTO_HIDE_CHECK_TICKS = 24;
/*      */   private static boolean pre_initialised;
/*      */   private static DeviceManagerImpl singleton;
/*      */   private AzureusCore azureus_core;
/*      */   private TorrentAttribute od_manual_ta;
/*      */   
/*      */   public static void preInitialise()
/*      */   {
/*  111 */     synchronized (DeviceManagerImpl.class)
/*      */     {
/*  113 */       if (pre_initialised)
/*      */       {
/*  115 */         return;
/*      */       }
/*      */       
/*  118 */       pre_initialised = true;
/*      */     }
/*      */     
/*  121 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void process(VuzeFile[] files, int expected_types)
/*      */       {
/*      */ 
/*      */ 
/*  129 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  131 */           VuzeFile vf = files[i];
/*      */           
/*  133 */           VuzeFileComponent[] comps = vf.getComponents();
/*      */           
/*  135 */           for (int j = 0; j < comps.length; j++)
/*      */           {
/*  137 */             VuzeFileComponent comp = comps[j];
/*      */             
/*  139 */             int type = comp.getType();
/*      */             
/*  141 */             if (type == 512) {
/*      */               try
/*      */               {
/*  144 */                 ((DeviceManagerImpl)DeviceManagerImpl.getSingleton()).importVuzeFile(comp.getContent(), (expected_types & 0x200) == 0);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  149 */                 comp.setProcessed();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  153 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public static DeviceManager getSingleton()
/*      */   {
/*  165 */     synchronized (DeviceManagerImpl.class)
/*      */     {
/*  167 */       if (singleton == null)
/*      */       {
/*  169 */         singleton = new DeviceManagerImpl();
/*      */       }
/*      */     }
/*      */     
/*  173 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  181 */   private List<DeviceImpl> device_list = new ArrayList();
/*  182 */   private Map<String, DeviceImpl> device_map = new HashMap();
/*      */   
/*      */   private DeviceTivoManager tivo_manager;
/*      */   
/*      */   private DeviceManagerUPnPImpl upnp_manager;
/*      */   private DeviceDriveManager drive_manager;
/*  188 */   private Set<Device> disable_events = Collections.synchronizedSet(new HashSet());
/*      */   
/*      */   private static final int LT_DEVICE_ADDED = 1;
/*      */   
/*      */   private static final int LT_DEVICE_CHANGED = 2;
/*      */   
/*      */   private static final int LT_DEVICE_ATTENTION = 3;
/*      */   
/*      */   private static final int LT_DEVICE_REMOVED = 4;
/*      */   
/*      */   private static final int LT_INITIALIZED = 5;
/*  199 */   private ListenerManager<DeviceManagerListener> listeners = ListenerManager.createAsyncManager("DM:ld", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(DeviceManagerListener listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  210 */       DeviceImpl device = (DeviceImpl)value;
/*      */       
/*  212 */       switch (type)
/*      */       {
/*      */ 
/*      */       case 1: 
/*  216 */         listener.deviceAdded(device);
/*      */         
/*  218 */         break;
/*      */       
/*      */ 
/*      */       case 2: 
/*  222 */         if (deviceAdded(device))
/*      */         {
/*  224 */           device.fireChanged();
/*      */           
/*  226 */           listener.deviceChanged(device);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         break;
/*      */       case 3: 
/*  233 */         if (deviceAdded(device))
/*      */         {
/*  235 */           listener.deviceAttentionRequest(device);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         break;
/*      */       case 4: 
/*  242 */         listener.deviceRemoved(device);
/*      */         
/*  244 */         break;
/*      */       
/*      */ 
/*      */       case 5: 
/*  248 */         listener.deviceManagerLoaded();
/*      */       }
/*      */       
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     protected boolean deviceAdded(Device device)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: getfield 66	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl$2:this$0	Lcom/aelitis/azureus/core/devices/impl/DeviceManagerImpl;
/*      */       //   4: dup
/*      */       //   5: astore_2
/*      */       //   6: monitorenter
/*      */       //   7: aload_0
/*      */       //   8: getfield 66	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl$2:this$0	Lcom/aelitis/azureus/core/devices/impl/DeviceManagerImpl;
/*      */       //   11: invokestatic 68	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl:access$100	(Lcom/aelitis/azureus/core/devices/impl/DeviceManagerImpl;)Ljava/util/List;
/*      */       //   14: aload_1
/*      */       //   15: invokeinterface 77 2 0
/*      */       //   20: aload_2
/*      */       //   21: monitorexit
/*      */       //   22: ireturn
/*      */       //   23: astore_3
/*      */       //   24: aload_2
/*      */       //   25: monitorexit
/*      */       //   26: aload_3
/*      */       //   27: athrow
/*      */       // Line number table:
/*      */       //   Java source line #259	-> byte code offset #0
/*      */       //   Java source line #261	-> byte code offset #7
/*      */       //   Java source line #262	-> byte code offset #23
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	28	0	this	2
/*      */       //   0	28	1	device	Device
/*      */       //   5	20	2	Ljava/lang/Object;	Object
/*      */       //   23	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   7	22	23	finally
/*      */       //   23	26	23	finally
/*      */     }
/*  199 */   });
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean auto_search;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  268 */   private int auto_hide_old_days = 31;
/*      */   
/*      */   private DeviceManagerRSSFeed rss_publisher;
/*      */   
/*      */   private boolean od_enabled;
/*      */   
/*      */   private boolean od_is_auto;
/*      */   
/*      */   private boolean od_include_private;
/*      */   
/*      */   private boolean closing;
/*      */   
/*      */   private boolean config_unclean;
/*      */   
/*      */   private boolean config_dirty;
/*      */   
/*      */   private int explicit_search;
/*      */   private volatile TranscodeManagerImpl transcode_manager;
/*  286 */   private CopyOnWriteList<DeviceManagerDiscoveryListener> discovery_listeners = new CopyOnWriteList();
/*      */   
/*      */   private int getMimeType_fails;
/*      */   
/*  290 */   private Object logger_lock = new Object();
/*      */   
/*      */   private AEDiagnosticsLogger logger;
/*  293 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher(10000);
/*      */   
/*  295 */   private AESemaphore init_sem = new AESemaphore("dm:init");
/*      */   
/*  297 */   private volatile boolean initialized = false;
/*      */   
/*  299 */   private Object lsn_lock = new Object();
/*      */   
/*      */   private String local_service_name;
/*      */   
/*      */   protected DeviceManagerImpl()
/*      */   {
/*  305 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  307 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*      */       public void azureusCoreRunning(AzureusCore core) {
/*  309 */         DeviceManagerImpl.this.initWithCore(core);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private TranscodeManager ensureInitialised(boolean partial)
/*      */   {
/*  318 */     AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */     
/*  320 */     if (core.isStarted())
/*      */     {
/*  322 */       initWithCore(core);
/*      */     }
/*  324 */     else if (core.isInitThread())
/*      */     {
/*  326 */       Debug.out("This is bad");
/*      */       
/*  328 */       initWithCore(core);
/*      */     }
/*      */     
/*  331 */     if (partial)
/*      */     {
/*  333 */       long start = SystemTime.getMonotonousTime();
/*      */       
/*  335 */       while (!this.init_sem.reserve(250L))
/*      */       {
/*  337 */         if (this.transcode_manager == null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  342 */           if (SystemTime.getMonotonousTime() - start >= 30000L)
/*      */           {
/*  344 */             Debug.out("Timeout waiting for init");
/*      */             
/*  346 */             AEDiagnostics.dumpThreads();
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */     }
/*  352 */     else if (!this.init_sem.reserve(30000L))
/*      */     {
/*  354 */       Debug.out("Timeout waiting for init");
/*      */       
/*  356 */       AEDiagnostics.dumpThreads();
/*      */     }
/*      */     
/*      */ 
/*  360 */     return this.transcode_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initWithCore(AzureusCore core)
/*      */   {
/*  367 */     synchronized (this)
/*      */     {
/*  369 */       if (this.azureus_core != null)
/*      */       {
/*  371 */         return;
/*      */       }
/*      */       
/*  374 */       this.azureus_core = core;
/*      */     }
/*      */     try
/*      */     {
/*  378 */       this.od_manual_ta = PluginInitializer.getDefaultInterface().getTorrentManager().getPluginAttribute("device.manager.od.ta.manual");
/*      */       
/*  380 */       this.rss_publisher = new DeviceManagerRSSFeed(this);
/*      */       
/*      */ 
/*      */ 
/*  384 */       COConfigurationManager.addAndFireParameterListeners(new String[] { "devices.config.auto_search", "devices.config.auto_hide_old" }, new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String name)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  395 */           DeviceManagerImpl.this.auto_search = COConfigurationManager.getBooleanParameter("devices.config.auto_search", true);
/*  396 */           DeviceManagerImpl.this.auto_hide_old_days = COConfigurationManager.getIntParameter("devices.config.auto_hide_old", 31);
/*      */         }
/*      */         
/*  399 */       });
/*  400 */       COConfigurationManager.addAndFireParameterListeners(new String[] { "devices.config.od.enabled", "devices.config.od.auto", "devices.config.od.inc_priv" }, new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String name)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  412 */           boolean new_od_enabled = COConfigurationManager.getBooleanParameter("devices.config.od.enabled", true);
/*  413 */           boolean new_od_is_auto = COConfigurationManager.getBooleanParameter("devices.config.od.auto", true);
/*  414 */           boolean new_od_include_private_priv = COConfigurationManager.getBooleanParameter("devices.config.od.inc_priv", false);
/*      */           
/*  416 */           if ((new_od_enabled != DeviceManagerImpl.this.od_enabled) || (new_od_is_auto != DeviceManagerImpl.this.od_is_auto) || (new_od_include_private_priv != DeviceManagerImpl.this.od_include_private))
/*      */           {
/*  418 */             DeviceManagerImpl.this.od_enabled = new_od_enabled;
/*  419 */             DeviceManagerImpl.this.od_is_auto = new_od_is_auto;
/*  420 */             DeviceManagerImpl.this.od_include_private = new_od_include_private_priv;
/*      */             
/*  422 */             DeviceManagerImpl.this.manageOD();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*  428 */       });
/*  429 */       this.tivo_manager = new DeviceTivoManager(this);
/*      */       
/*  431 */       this.upnp_manager = new DeviceManagerUPnPImpl(this);
/*      */       
/*  433 */       loadConfig();
/*      */       
/*  435 */       new DeviceiTunesManager(this);
/*      */       
/*  437 */       this.drive_manager = new DeviceDriveManager(this);
/*      */       
/*  439 */       this.transcode_manager = new TranscodeManagerImpl(this);
/*      */       
/*  441 */       this.transcode_manager.initialise();
/*      */       
/*  443 */       core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void stopping(AzureusCore core)
/*      */         {
/*      */ 
/*  450 */           synchronized (DeviceManagerImpl.this)
/*      */           {
/*  452 */             if ((DeviceManagerImpl.this.config_dirty) || (DeviceManagerImpl.this.config_unclean))
/*      */             {
/*  454 */               DeviceManagerImpl.this.saveConfig();
/*      */             }
/*      */             
/*  457 */             DeviceManagerImpl.this.closing = true;
/*      */             
/*  459 */             DeviceManagerImpl.this.transcode_manager.close();
/*      */             
/*  461 */             DeviceImpl[] devices = DeviceManagerImpl.this.getDevices();
/*      */             
/*  463 */             for (DeviceImpl device : devices)
/*      */             {
/*  465 */               device.close();
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  470 */       });
/*  471 */       this.upnp_manager.initialise();
/*      */       
/*  473 */       SimpleTimer.addPeriodicEvent("DeviceManager:update", 5000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  478 */         private int tick_count = 0;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*  486 */           this.tick_count += 1;
/*      */           
/*  488 */           DeviceManagerImpl.this.transcode_manager.updateStatus(this.tick_count);
/*      */           List<DeviceImpl> copy;
/*  490 */           synchronized (DeviceManagerImpl.this)
/*      */           {
/*  492 */             if (DeviceManagerImpl.this.device_list.size() == 0)
/*      */             {
/*  494 */               return;
/*      */             }
/*      */             
/*  497 */             copy = new ArrayList(DeviceManagerImpl.this.device_list);
/*      */           }
/*      */           
/*  500 */           for (DeviceImpl device : copy)
/*      */           {
/*  502 */             device.updateStatus(this.tick_count);
/*      */           }
/*      */           
/*  505 */           if ((DeviceManagerImpl.this.auto_hide_old_days > 0) && (this.tick_count % 24 == 0))
/*      */           {
/*      */ 
/*  508 */             long now = SystemTime.getCurrentTime();
/*      */             
/*  510 */             int num_hidden = 0;
/*      */             
/*  512 */             for (DeviceImpl device : copy)
/*      */             {
/*  514 */               if ((device.isLivenessDetectable()) && (!device.isTagged()))
/*      */               {
/*      */ 
/*  517 */                 int type = device.getType();
/*      */                 
/*  519 */                 if (type != 2)
/*      */                 {
/*  521 */                   if (type == 3)
/*      */                   {
/*  523 */                     DeviceMediaRenderer rend = (DeviceMediaRenderer)device;
/*      */                     
/*  525 */                     if (rend.getRendererSpecies() != 6) {}
/*      */ 
/*      */                   }
/*      */                   
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/*  534 */                   long age = now - device.getLastSeen();
/*      */                   
/*  536 */                   if (age > DeviceManagerImpl.this.auto_hide_old_days * 24 * 60 * 60 * 1000L)
/*      */                   {
/*  538 */                     if (!device.isHidden())
/*      */                     {
/*  540 */                       DeviceManagerImpl.this.log("Auto-hiding '" + device.getName() + "'");
/*      */                       
/*  542 */                       device.setHidden(true);
/*      */                       
/*  544 */                       device.setAutoHidden(true);
/*      */                       
/*  546 */                       num_hidden++;
/*      */                     }
/*      */                     
/*      */                   }
/*  550 */                   else if ((device.isHidden()) && (device.isAutoHidden()))
/*      */                   {
/*  552 */                     DeviceManagerImpl.this.log("Auto-showing '" + device.getName() + "'");
/*      */                     
/*  554 */                     device.setAutoHidden(false);
/*      */                     
/*  556 */                     device.setHidden(false);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  562 */             if (num_hidden > 0)
/*      */             {
/*  564 */               Logger.log(new LogAlert(true, 0, MessageText.getString("device.autohide.alert", new String[] { String.valueOf(num_hidden), String.valueOf(DeviceManagerImpl.this.auto_hide_old_days) })));
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  578 */       });
/*  579 */       this.initialized = true;
/*      */       
/*  581 */       this.listeners.dispatch(5, null);
/*      */       
/*  583 */       core.addPowerManagementListener(this);
/*      */     }
/*      */     finally
/*      */     {
/*  587 */       this.init_sem.releaseForever();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void manageOD()
/*      */   {
/*  594 */     DeviceImpl[] devices = getDevices();
/*      */     
/*  596 */     for (DeviceImpl device : devices)
/*      */     {
/*  598 */       if (device.getType() == 5)
/*      */       {
/*  600 */         ((DeviceOfflineDownloaderImpl)device).checkConfig();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void UPnPManagerStarted()
/*      */   {
/*  608 */     this.tivo_manager.startUp();
/*      */     
/*  610 */     DeviceImpl[] devices = getDevices();
/*      */     
/*  612 */     for (DeviceImpl device : devices)
/*      */     {
/*  614 */       if ((device instanceof DeviceUPnPImpl))
/*      */       {
/*  616 */         ((DeviceUPnPImpl)device).UPnPInitialised();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected AzureusCore getAzureusCore()
/*      */   {
/*  624 */     return this.azureus_core;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DeviceManagerUPnPImpl getUPnPManager()
/*      */   {
/*  630 */     return this.upnp_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getLocalServiceName()
/*      */   {
/*  636 */     synchronized (this.lsn_lock)
/*      */     {
/*  638 */       if (this.local_service_name == null)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  643 */           IPCInterface ipc = getUPnPManager().getUPnPAVIPC();
/*      */           
/*  645 */           if (ipc != null)
/*      */           {
/*  647 */             this.local_service_name = ((String)ipc.invoke("getServiceName", new Object[0]));
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*  653 */         if (this.local_service_name == null)
/*      */         {
/*  655 */           this.local_service_name = Constants.APP_NAME;
/*      */           try
/*      */           {
/*  658 */             String cn = PlatformManagerFactory.getPlatformManager().getComputerName();
/*      */             
/*  660 */             if ((cn != null) && (cn.length() > 0))
/*      */             {
/*  662 */               this.local_service_name = (this.local_service_name + " on " + cn);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*  669 */       return this.local_service_name;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTiVoEnabled()
/*      */   {
/*  676 */     return this.tivo_manager.isEnabled();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTiVoEnabled(boolean enabled)
/*      */   {
/*  683 */     this.tivo_manager.setEnabled(enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeProvider[] getProviders()
/*      */   {
/*  689 */     TranscodeManager tm = ensureInitialised(true);
/*      */     
/*  691 */     if (tm == null)
/*      */     {
/*  693 */       return new TranscodeProvider[0];
/*      */     }
/*      */     
/*  696 */     return tm.getProviders();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DeviceTemplate[] getDeviceTemplates(int device_type)
/*      */   {
/*  703 */     if ((this.transcode_manager == null) || (device_type != 3))
/*      */     {
/*  705 */       return new DeviceTemplate[0];
/*      */     }
/*      */     
/*  708 */     TranscodeProvider[] providers = this.transcode_manager.getProviders();
/*      */     
/*  710 */     List<DeviceTemplate> result = new ArrayList();
/*      */     
/*  712 */     for (TranscodeProvider provider : providers)
/*      */     {
/*  714 */       TranscodeProfile[] profiles = provider.getProfiles();
/*      */       
/*  716 */       Map<String, DeviceMediaRendererTemplateImpl> class_map = new HashMap();
/*      */       
/*  718 */       for (TranscodeProfile profile : profiles)
/*      */       {
/*  720 */         String classification = profile.getDeviceClassification();
/*      */         
/*  722 */         if (classification.startsWith("apple."))
/*      */         {
/*  724 */           classification = "apple.";
/*      */         }
/*      */         
/*  727 */         boolean auto = (classification.equals("sony.PS3")) || (classification.equals("microsoft.XBox")) || (classification.equals("apple.")) || (classification.equals("nintendo.Wii")) || (classification.equals("browser.generic"));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  734 */         DeviceMediaRendererTemplateImpl temp = (DeviceMediaRendererTemplateImpl)class_map.get(classification);
/*      */         
/*  736 */         if (temp == null)
/*      */         {
/*  738 */           temp = new DeviceMediaRendererTemplateImpl(this, classification, auto);
/*      */           
/*  740 */           class_map.put(classification, temp);
/*      */           
/*  742 */           result.add(temp);
/*      */         }
/*      */         
/*  745 */         temp.addProfile(profile);
/*      */       }
/*      */     }
/*      */     
/*  749 */     return (DeviceTemplate[])result.toArray(new DeviceTemplate[result.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DeviceManager.DeviceManufacturer[] getDeviceManufacturers(int device_type)
/*      */   {
/*  756 */     DeviceTemplate[] templates = getDeviceTemplates(device_type);
/*      */     
/*  758 */     Map<String, DeviceManufacturerImpl> map = new HashMap();
/*      */     
/*  760 */     for (DeviceTemplate template : templates)
/*      */     {
/*  762 */       if (template.getType() == device_type)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  767 */         String man_str = template.getManufacturer();
/*      */         
/*  769 */         DeviceManufacturerImpl man = (DeviceManufacturerImpl)map.get(man_str);
/*      */         
/*  771 */         if (man == null)
/*      */         {
/*  773 */           man = new DeviceManufacturerImpl(man_str);
/*      */           
/*  775 */           map.put(man_str, man);
/*      */         }
/*      */         
/*  778 */         man.addTemplate(template);
/*      */       }
/*      */     }
/*  781 */     return (DeviceManager.DeviceManufacturer[])map.values().toArray(new DeviceManager.DeviceManufacturer[map.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Device addVirtualDevice(int type, String uid, String classification, String name)
/*      */     throws DeviceManagerException
/*      */   {
/*  793 */     return createDevice(type, uid, classification, name, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Device addInetDevice(int type, String uid, String classification, String name, InetAddress address)
/*      */     throws DeviceManagerException
/*      */   {
/*  806 */     Device device = createDevice(type, uid, classification, name, false);
/*  807 */     device.setAddress(address);
/*  808 */     return device;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Device createDevice(int device_type, String uid, String classification, String name, boolean manual)
/*      */     throws DeviceManagerException
/*      */   {
/*  821 */     if (device_type == 3)
/*      */     {
/*      */       DeviceImpl res;
/*      */       
/*  825 */       if (manual)
/*      */       {
/*  827 */         res = new DeviceMediaRendererManual(this, uid, classification, true, name);
/*      */       }
/*      */       else
/*      */       {
/*  831 */         res = new DeviceMediaRendererImpl(this, uid, classification, true, name);
/*      */       }
/*      */       
/*  834 */       DeviceImpl res = addDevice(res);
/*      */       
/*  836 */       return res;
/*      */     }
/*      */     
/*      */ 
/*  840 */     throw new DeviceManagerException("Can't manually create this device type");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void search(final int millis, final DeviceSearchListener listener)
/*      */   {
/*  849 */     new AEThread2("DM:search", true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  854 */         synchronized (DeviceManagerImpl.this)
/*      */         {
/*  856 */           DeviceManagerImpl.access$1208(DeviceManagerImpl.this);
/*      */         }
/*      */         
/*  859 */         DeviceManagerImpl.this.tivo_manager.search();
/*      */         
/*  861 */         DeviceManagerImpl.this.drive_manager.search();
/*      */         
/*  863 */         AESemaphore sem = new AESemaphore("DM:search");
/*      */         
/*  865 */         Object dm_listener = new DeviceManagerListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void deviceAdded(Device device)
/*      */           {
/*      */ 
/*  872 */             DeviceManagerImpl.8.this.val$listener.deviceFound(device);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void deviceChanged(Device device) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void deviceAttentionRequest(Device device) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void deviceRemoved(Device device) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void deviceManagerLoaded() {}
/*      */         };
/*      */         
/*      */ 
/*      */         try
/*      */         {
/*  899 */           DeviceManagerImpl.this.addListener((DeviceManagerListener)dm_listener);
/*      */           
/*  901 */           DeviceManagerImpl.this.upnp_manager.search();
/*      */           
/*  903 */           sem.reserve(millis);
/*      */         }
/*      */         finally
/*      */         {
/*  907 */           synchronized (DeviceManagerImpl.this)
/*      */           {
/*  909 */             DeviceManagerImpl.access$1210(DeviceManagerImpl.this);
/*      */           }
/*      */           
/*  912 */           DeviceManagerImpl.this.removeListener((DeviceManagerListener)dm_listener);
/*      */           
/*  914 */           listener.complete();
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected DeviceImpl getDevice(String id)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_2
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 1065	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl:device_map	Ljava/util/Map;
/*      */     //   8: aload_1
/*      */     //   9: invokeinterface 1258 2 0
/*      */     //   14: checkcast 605	com/aelitis/azureus/core/devices/impl/DeviceImpl
/*      */     //   17: aload_2
/*      */     //   18: monitorexit
/*      */     //   19: areturn
/*      */     //   20: astore_3
/*      */     //   21: aload_2
/*      */     //   22: monitorexit
/*      */     //   23: aload_3
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #924	-> byte code offset #0
/*      */     //   Java source line #926	-> byte code offset #4
/*      */     //   Java source line #927	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	25	0	this	DeviceManagerImpl
/*      */     //   0	25	1	id	String
/*      */     //   2	20	2	Ljava/lang/Object;	Object
/*      */     //   20	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   protected DeviceImpl addDevice(DeviceImpl device)
/*      */   {
/*  934 */     return addDevice(device, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceImpl addDevice(DeviceImpl device, boolean is_alive)
/*      */   {
/*  945 */     DeviceImpl existing = null;
/*      */     
/*  947 */     synchronized (this)
/*      */     {
/*  949 */       existing = (DeviceImpl)this.device_map.get(device.getID());
/*      */       
/*  951 */       if (existing != null)
/*      */       {
/*  953 */         existing.updateFrom(device, is_alive);
/*      */ 
/*      */ 
/*      */       }
/*  957 */       else if (device.getType() == 3)
/*      */       {
/*  959 */         DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*      */         
/*  961 */         if ((renderer.getRendererSpecies() == 2) && (!renderer.isManual()))
/*      */         {
/*  963 */           for (DeviceImpl d : this.device_list)
/*      */           {
/*  965 */             if (d.getType() == 3)
/*      */             {
/*  967 */               DeviceMediaRenderer r = (DeviceMediaRenderer)d;
/*      */               
/*  969 */               if ((r.getRendererSpecies() == 2) && (r.isManual()))
/*      */               {
/*  971 */                 existing = d;
/*      */                 
/*  973 */                 log("Merging " + device.getString() + " -> " + existing.getString());
/*      */                 
/*  975 */                 String secondary_id = device.getID();
/*      */                 
/*  977 */                 existing.setSecondaryID(secondary_id);
/*      */                 
/*  979 */                 existing.updateFrom(device, is_alive);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  987 */       if (existing == null)
/*      */       {
/*  989 */         this.device_list.add(device);
/*      */         
/*  991 */         this.device_map.put(device.getID(), device);
/*      */       }
/*      */     }
/*      */     
/*  995 */     if (existing != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1004 */       applyUpdates(existing);
/*      */       
/* 1006 */       return existing;
/*      */     }
/*      */     try
/*      */     {
/* 1010 */       this.disable_events.add(device);
/*      */       
/* 1012 */       device.initialise();
/*      */       
/* 1014 */       if (is_alive)
/*      */       {
/* 1016 */         device.alive();
/*      */       }
/*      */       
/* 1019 */       applyUpdates(device);
/*      */     }
/*      */     finally
/*      */     {
/* 1023 */       this.disable_events.remove(device);
/*      */     }
/* 1025 */     deviceAdded(device);
/*      */     
/* 1027 */     configDirty();
/*      */     
/* 1029 */     return device;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void applyUpdates(DeviceImpl device)
/*      */   {
/* 1036 */     if (device.getType() == 3)
/*      */     {
/* 1038 */       DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*      */       
/* 1040 */       if ((renderer instanceof DeviceUPnPImpl))
/*      */       {
/* 1042 */         UPnPDevice upnp_device = ((DeviceUPnPImpl)renderer).getUPnPDevice();
/*      */         
/* 1044 */         if (upnp_device != null)
/*      */         {
/* 1046 */           String lc_manufacturer = getOptionalLC(upnp_device.getManufacturer());
/* 1047 */           String lc_model = getOptionalLC(upnp_device.getModelName());
/* 1048 */           String lc_fname = getOptionalLC(upnp_device.getFriendlyName());
/*      */           
/* 1050 */           if (lc_manufacturer.startsWith("samsung"))
/*      */           {
/* 1052 */             device.setPersistentStringProperty("tt_rend_class", "samsung.generic");
/*      */             
/* 1054 */             TranscodeProfile[] profiles = device.getTranscodeProfiles();
/*      */             
/* 1056 */             if (profiles.length == 0)
/*      */             {
/* 1058 */               device.setTranscodeRequirement(1);
/*      */             }
/*      */             else
/*      */             {
/* 1062 */               device.setTranscodeRequirement(2);
/*      */             }
/* 1064 */           } else if (lc_manufacturer.startsWith("western digital"))
/*      */           {
/* 1066 */             device.setPersistentStringProperty("tt_rend_class", "western.digital.generic");
/*      */             
/* 1068 */             TranscodeProfile[] profiles = device.getTranscodeProfiles();
/*      */             
/* 1070 */             if (profiles.length == 0)
/*      */             {
/* 1072 */               device.setTranscodeRequirement(1);
/*      */             }
/*      */             else
/*      */             {
/* 1076 */               device.setTranscodeRequirement(2);
/*      */             }
/* 1078 */           } else if ((lc_manufacturer.startsWith("sony")) && (lc_fname.startsWith("bravia")))
/*      */           {
/* 1080 */             device.setPersistentStringProperty("tt_rend_class", "sony.bravia");
/*      */           }
/* 1082 */           else if (lc_model.equals("windows media player"))
/*      */           {
/* 1084 */             String model_number = upnp_device.getModelNumber();
/*      */             
/* 1086 */             if (model_number != null) {
/*      */               try
/*      */               {
/* 1089 */                 int num = Integer.parseInt(model_number);
/*      */                 
/* 1091 */                 if (num >= 12)
/*      */                 {
/* 1093 */                   device.setPersistentStringProperty("tt_rend_class", "ms_wmp.generic");
/*      */                   
/* 1095 */                   TranscodeProfile[] profiles = device.getTranscodeProfiles();
/*      */                   
/* 1097 */                   if (profiles.length == 0)
/*      */                   {
/* 1099 */                     device.setTranscodeRequirement(1);
/*      */                   }
/*      */                   else
/*      */                   {
/* 1103 */                     device.setTranscodeRequirement(2);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getOptionalLC(String str)
/*      */   {
/* 1120 */     if (str == null)
/*      */     {
/* 1122 */       return "";
/*      */     }
/*      */     
/* 1125 */     return str.toLowerCase().trim();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeDevice(DeviceImpl device)
/*      */   {
/* 1132 */     synchronized (this)
/*      */     {
/* 1134 */       DeviceImpl existing = (DeviceImpl)this.device_map.remove(device.getID());
/*      */       
/* 1136 */       if (existing == null)
/*      */       {
/* 1138 */         return;
/*      */       }
/*      */       
/* 1141 */       this.device_list.remove(device);
/*      */       
/* 1143 */       String secondary_id = device.getSecondaryID();
/*      */       
/* 1145 */       if (secondary_id != null)
/*      */       {
/* 1147 */         this.device_map.remove(secondary_id);
/*      */       }
/*      */     }
/*      */     
/* 1151 */     device.destroy();
/*      */     
/* 1153 */     deviceRemoved(device);
/*      */     
/* 1155 */     configDirty();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isBusy(int device_type)
/*      */   {
/* 1164 */     if ((device_type == 0) || (device_type == 3))
/*      */     {
/* 1166 */       if (getTranscodeManager().getQueue().isTranscoding())
/*      */       {
/* 1168 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1172 */     DeviceImpl[] devices = getDevices();
/*      */     
/* 1174 */     for (DeviceImpl device : devices)
/*      */     {
/* 1176 */       if (device.isBusy())
/*      */       {
/* 1178 */         if ((device_type == 0) || (device_type == device.getType()))
/*      */         {
/* 1180 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1185 */     return false;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public DeviceImpl[] getDevices()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 1064	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl:device_list	Ljava/util/List;
/*      */     //   8: aload_0
/*      */     //   9: getfield 1064	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl:device_list	Ljava/util/List;
/*      */     //   12: invokeinterface 1250 1 0
/*      */     //   17: anewarray 605	com/aelitis/azureus/core/devices/impl/DeviceImpl
/*      */     //   20: invokeinterface 1255 2 0
/*      */     //   25: checkcast 587	[Lcom/aelitis/azureus/core/devices/impl/DeviceImpl;
/*      */     //   28: aload_1
/*      */     //   29: monitorexit
/*      */     //   30: areturn
/*      */     //   31: astore_2
/*      */     //   32: aload_1
/*      */     //   33: monitorexit
/*      */     //   34: aload_2
/*      */     //   35: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1191	-> byte code offset #0
/*      */     //   Java source line #1193	-> byte code offset #4
/*      */     //   Java source line #1194	-> byte code offset #31
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	36	0	this	DeviceManagerImpl
/*      */     //   2	31	1	Ljava/lang/Object;	Object
/*      */     //   31	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	30	31	finally
/*      */     //   31	34	31	finally
/*      */   }
/*      */   
/*      */   public boolean getAutoSearch()
/*      */   {
/* 1200 */     return this.auto_search;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoSearch(boolean auto)
/*      */   {
/* 1207 */     COConfigurationManager.setParameter("devices.config.auto_search", auto);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAutoHideOldDevicesDays()
/*      */   {
/* 1213 */     return COConfigurationManager.getIntParameter("devices.config.auto_hide_old", 31);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoHideOldDevicesDays(int days)
/*      */   {
/* 1220 */     COConfigurationManager.setParameter("devices.config.auto_hide_old", days);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRSSPublishEnabled()
/*      */   {
/* 1226 */     return COConfigurationManager.getBooleanParameter("devices.config.rss_enable", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRSSPublishEnabled(boolean enabled)
/*      */   {
/* 1233 */     COConfigurationManager.setParameter("devices.config.rss_enable", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRSSLink()
/*      */   {
/* 1239 */     return this.rss_publisher.getFeedURL();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DeviceOfflineDownloaderManager getOfflineDownlaoderManager()
/*      */   {
/* 1247 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOfflineDownloadingEnabled()
/*      */   {
/* 1253 */     return this.od_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setOfflineDownloadingEnabled(boolean enabled)
/*      */   {
/* 1260 */     COConfigurationManager.setParameter("devices.config.od.enabled", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getOfflineDownloadingIsAuto()
/*      */   {
/* 1266 */     return this.od_is_auto;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setOfflineDownloadingIsAuto(boolean auto)
/*      */   {
/* 1273 */     COConfigurationManager.setParameter("devices.config.od.auto", auto);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getOfflineDownloadingIncludePrivate()
/*      */   {
/* 1279 */     return this.od_include_private;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setOfflineDownloadingIncludePrivate(boolean include)
/*      */   {
/* 1286 */     COConfigurationManager.setParameter("devices.config.od.inc_priv", include);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isManualDownload(Download download)
/*      */   {
/* 1293 */     return download.getBooleanAttribute(this.od_manual_ta);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addManualDownloads(Download[] downloads)
/*      */   {
/* 1300 */     for (Download d : downloads)
/*      */     {
/* 1302 */       d.setBooleanAttribute(this.od_manual_ta, true);
/*      */     }
/*      */     
/* 1305 */     manageOD();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeManualDownloads(Download[] downloads)
/*      */   {
/* 1312 */     for (Download d : downloads)
/*      */     {
/* 1314 */       d.setBooleanAttribute(this.od_manual_ta, false);
/*      */     }
/*      */     
/* 1317 */     manageOD();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isExplicitSearch()
/*      */   {
/* 1325 */     synchronized (this)
/*      */     {
/* 1327 */       return this.explicit_search > 0;
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected boolean isClosing()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 1045	com/aelitis/azureus/core/devices/impl/DeviceManagerImpl:closing	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1334	-> byte code offset #0
/*      */     //   Java source line #1336	-> byte code offset #4
/*      */     //   Java source line #1337	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	DeviceManagerImpl
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   protected void loadConfig()
/*      */   {
/* 1343 */     if (!FileUtil.resilientConfigFileExists("devices.config"))
/*      */     {
/* 1345 */       return;
/*      */     }
/*      */     
/* 1348 */     log("Loading configuration");
/*      */     
/* 1350 */     synchronized (this)
/*      */     {
/* 1352 */       Map map = FileUtil.readResilientConfigFile("devices.config");
/*      */       
/* 1354 */       List l_devices = (List)map.get("devices");
/*      */       
/* 1356 */       if (l_devices != null)
/*      */       {
/* 1358 */         for (int i = 0; i < l_devices.size(); i++)
/*      */         {
/* 1360 */           Map m = (Map)l_devices.get(i);
/*      */           try
/*      */           {
/* 1363 */             DeviceImpl device = DeviceImpl.importFromBEncodedMapStatic(this, m);
/*      */             
/* 1365 */             this.device_list.add(device);
/*      */             
/* 1367 */             this.device_map.put(device.getID(), device);
/*      */             
/* 1369 */             String secondary_id = device.getSecondaryID();
/*      */             
/* 1371 */             if (secondary_id != null)
/*      */             {
/* 1373 */               this.device_map.put(secondary_id, device);
/*      */             }
/*      */             
/* 1376 */             device.initialise();
/*      */             
/* 1378 */             log("    loaded " + device.getString());
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1382 */             log("Failed to import subscription from " + m, e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void configDirty(DeviceImpl device, boolean save_changes)
/*      */   {
/* 1394 */     deviceChanged(device, save_changes);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void configDirty()
/*      */   {
/* 1400 */     synchronized (this)
/*      */     {
/* 1402 */       if (this.config_dirty)
/*      */       {
/* 1404 */         return;
/*      */       }
/*      */       
/* 1407 */       this.config_dirty = true;
/*      */       
/* 1409 */       new DelayedEvent("Subscriptions:save", 5000L, new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 1416 */           synchronized (DeviceManagerImpl.this)
/*      */           {
/* 1418 */             if (!DeviceManagerImpl.this.config_dirty)
/*      */             {
/* 1420 */               return;
/*      */             }
/*      */             
/* 1423 */             DeviceManagerImpl.this.saveConfig();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveConfig()
/*      */   {
/* 1433 */     log("Saving configuration");
/*      */     
/* 1435 */     synchronized (this)
/*      */     {
/* 1437 */       if (this.closing)
/*      */       {
/*      */ 
/*      */ 
/* 1441 */         return;
/*      */       }
/*      */       
/* 1444 */       this.config_dirty = false;
/* 1445 */       this.config_unclean = false;
/*      */       
/* 1447 */       if (this.device_list.size() == 0)
/*      */       {
/* 1449 */         FileUtil.deleteResilientConfigFile("devices.config");
/*      */       }
/*      */       else
/*      */       {
/* 1453 */         Map map = new HashMap();
/*      */         
/* 1455 */         List l_devices = new ArrayList();
/*      */         
/* 1457 */         map.put("devices", l_devices);
/*      */         
/* 1459 */         Iterator<DeviceImpl> it = this.device_list.iterator();
/*      */         
/* 1461 */         while (it.hasNext())
/*      */         {
/* 1463 */           DeviceImpl device = (DeviceImpl)it.next();
/*      */           try
/*      */           {
/* 1466 */             Map d = new HashMap();
/*      */             
/* 1468 */             device.exportToBEncodedMap(d, false);
/*      */             
/* 1470 */             l_devices.add(d);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1474 */             log("Failed to save device " + device.getString(), e);
/*      */           }
/*      */         }
/*      */         
/* 1478 */         FileUtil.writeResilientConfigFile("devices.config", map);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deviceAdded(DeviceImpl device)
/*      */   {
/* 1487 */     configDirty();
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1493 */       PlatformDevicesMessenger.qosFoundDevice(device);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1497 */       Debug.out(e);
/*      */     }
/*      */     
/* 1500 */     this.listeners.dispatch(1, device);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void deviceChanged(DeviceImpl device, boolean save_changes)
/*      */   {
/* 1509 */     if (save_changes)
/*      */     {
/* 1511 */       configDirty();
/*      */     }
/*      */     else
/*      */     {
/* 1515 */       synchronized (this)
/*      */       {
/* 1517 */         this.config_unclean = true;
/*      */       }
/*      */     }
/*      */     
/* 1521 */     if (!this.disable_events.contains(device))
/*      */     {
/*      */ 
/*      */ 
/* 1525 */       this.listeners.dispatch(2, device);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deviceRemoved(DeviceImpl device)
/*      */   {
/* 1533 */     configDirty();
/*      */     
/* 1535 */     this.listeners.dispatch(4, device);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void requestAttention(DeviceImpl device)
/*      */   {
/* 1542 */     this.listeners.dispatch(3, device);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected URL getStreamURL(TranscodeFileImpl file, String host)
/*      */   {
/* 1550 */     IPCInterface ipc = this.upnp_manager.getUPnPAVIPC();
/*      */     
/* 1552 */     if (ipc != null) {
/*      */       try
/*      */       {
/* 1555 */         DiskManagerFileInfo f = file.getTargetFile();
/*      */         
/* 1557 */         String str = (String)ipc.invoke("getContentURL", new Object[] { f });
/*      */         
/* 1559 */         if ((str != null) && (str.length() > 0))
/*      */         {
/* 1561 */           if (host != null)
/*      */           {
/* 1563 */             str = str.replace("127.0.0.1", host);
/*      */           }
/*      */           
/* 1566 */           return new URL(str);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 1573 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getMimeType(TranscodeFileImpl file)
/*      */   {
/* 1580 */     if (this.getMimeType_fails > 5)
/*      */     {
/* 1582 */       return null;
/*      */     }
/*      */     
/* 1585 */     IPCInterface ipc = this.upnp_manager.getUPnPAVIPC();
/*      */     
/* 1587 */     if (ipc != null) {
/*      */       try
/*      */       {
/* 1590 */         DiskManagerFileInfo f = file.getTargetFile();
/*      */         
/* 1592 */         String[] strs = (String[])ipc.invoke("getMimeTypes", new Object[] { f });
/*      */         
/* 1594 */         if ((strs != null) && (strs.length > 0))
/*      */         {
/* 1596 */           return strs[0];
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1600 */         this.getMimeType_fails += 1;
/*      */         
/* 1602 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */     
/* 1606 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public File getDefaultWorkingDirectory()
/*      */   {
/* 1612 */     return getDefaultWorkingDirectory(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public File getDefaultWorkingDirectory(boolean persist)
/*      */   {
/* 1619 */     String def = COConfigurationManager.getStringParameter("devices.config.def_work_dir", "").trim();
/*      */     
/* 1621 */     if (def.length() == 0)
/*      */     {
/* 1623 */       String def_dir = COConfigurationManager.getStringParameter("Default save path");
/*      */       
/* 1625 */       def = def_dir + File.separator + "transcodes";
/*      */     }
/*      */     
/* 1628 */     File f = new File(def);
/*      */     
/* 1630 */     if (!f.exists())
/*      */     {
/*      */ 
/*      */ 
/* 1634 */       if (f.getName().equals("transcodes"))
/*      */       {
/* 1636 */         String parent = f.getParentFile().getName();
/*      */         
/* 1638 */         if ((parent.equals("Azureus Downloads")) || (parent.equals("Vuze Downloads")))
/*      */         {
/* 1640 */           String def_dir = COConfigurationManager.getStringParameter("Default save path");
/*      */           
/* 1642 */           f = new File(def_dir, "transcodes");
/*      */         }
/*      */       }
/* 1645 */       if (persist)
/*      */       {
/* 1647 */         f.mkdirs();
/*      */       }
/*      */     }
/*      */     
/* 1651 */     return f;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDefaultWorkingDirectory(File dir)
/*      */   {
/* 1658 */     File existing = getDefaultWorkingDirectory(false);
/*      */     
/* 1660 */     if (!existing.getAbsolutePath().equals(dir.getAbsolutePath()))
/*      */     {
/*      */ 
/*      */ 
/* 1664 */       DeviceImpl[] devices = getDevices();
/*      */       
/* 1666 */       for (DeviceImpl device : devices)
/*      */       {
/* 1668 */         device.resetWorkingDirectory();
/*      */       }
/*      */     }
/*      */     
/* 1672 */     COConfigurationManager.setParameter("devices.config.def_work_dir", dir.getAbsolutePath());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getDisableSleep()
/*      */   {
/* 1678 */     return COConfigurationManager.getBooleanParameter("devices.config.disable_sleep", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDisableSleep(boolean b)
/*      */   {
/* 1685 */     COConfigurationManager.setParameter("devices.config.disable_sleep", b);
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeManagerImpl getTranscodeManager()
/*      */   {
/* 1691 */     if (this.transcode_manager == null)
/*      */     {
/* 1693 */       ensureInitialised(false);
/*      */     }
/*      */     
/* 1696 */     return this.transcode_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public DeviceManager.UnassociatedDevice[] getUnassociatedDevices()
/*      */   {
/* 1702 */     return this.upnp_manager.getUnassociatedDevices();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPowerName()
/*      */   {
/* 1708 */     return "Transcode";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean requestPowerStateChange(int new_state, Object data)
/*      */   {
/* 1716 */     if (getDisableSleep())
/*      */     {
/* 1718 */       if (getTranscodeManager().getQueue().isTranscoding())
/*      */       {
/* 1720 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1724 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void informPowerStateChange(int new_state, Object data) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(DeviceManagerListener listener)
/*      */   {
/* 1739 */     this.listeners.addListener(listener);
/*      */     
/* 1741 */     if (this.initialized)
/*      */     {
/* 1743 */       this.listeners.dispatch(listener, 5, null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean browseReceived(TrackerWebPageRequest request, Map<String, Object> browser_args)
/*      */   {
/* 1752 */     for (DeviceManagerDiscoveryListener l : this.discovery_listeners) {
/*      */       try
/*      */       {
/* 1755 */         if (l.browseReceived(request, browser_args))
/*      */         {
/* 1757 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1761 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1765 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected VuzeFile exportVuzeFile(DeviceImpl device)
/*      */     throws IOException
/*      */   {
/* 1774 */     VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*      */     
/* 1776 */     Map map = new HashMap();
/*      */     
/* 1778 */     Map device_map = new HashMap();
/*      */     
/* 1780 */     map.put("device", device_map);
/*      */     
/* 1782 */     device.exportToBEncodedMap(device_map, true);
/*      */     
/* 1784 */     vf.addComponent(512, map);
/*      */     
/* 1786 */     return vf;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void importVuzeFile(Map map, boolean warn_user)
/*      */   {
/* 1794 */     Map m = (Map)map.get("device");
/*      */     try
/*      */     {
/* 1797 */       DeviceImpl device = DeviceImpl.importFromBEncodedMapStatic(this, m);
/*      */       
/*      */       DeviceImpl existing;
/*      */       
/* 1801 */       synchronized (this)
/*      */       {
/* 1803 */         existing = (DeviceImpl)this.device_map.get(device.getID());
/*      */       }
/*      */       
/* 1806 */       if (existing == null)
/*      */       {
/* 1808 */         if (warn_user)
/*      */         {
/* 1810 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 1812 */           String details = MessageText.getString("device.import.desc", new String[] { device.getName() });
/*      */           
/*      */ 
/*      */ 
/* 1816 */           long res = ui_manager.showMessageBox("device.import.title", "!" + details + "!", 12L);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1821 */           if (res != 4L)
/*      */           {
/* 1823 */             return;
/*      */           }
/*      */         }
/*      */         
/* 1827 */         addDevice(device, false);
/*      */ 
/*      */ 
/*      */       }
/* 1831 */       else if (warn_user)
/*      */       {
/* 1833 */         UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */         
/* 1835 */         String details = MessageText.getString("device.import.dup.desc", new String[] { existing.getName() });
/*      */         
/*      */ 
/*      */ 
/* 1839 */         ui_manager.showMessageBox("device.import.dup.title", "!" + details + "!", 1L);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1848 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDiscoveryListener(DeviceManagerDiscoveryListener listener)
/*      */   {
/* 1856 */     this.discovery_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeDiscoveryListener(DeviceManagerDiscoveryListener listener)
/*      */   {
/* 1863 */     this.discovery_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DeviceManagerListener listener)
/*      */   {
/* 1870 */     this.listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */   public Device findDevice(UPnPDevice upnpDevice) {
/* 1874 */     DeviceImpl[] devices = getDevices();
/* 1875 */     for (DeviceImpl device : devices) {
/* 1876 */       if ((device instanceof DeviceUPnPImpl)) {
/* 1877 */         DeviceUPnPImpl deviceUPnP = (DeviceUPnPImpl)device;
/* 1878 */         UPnPDevice uPnPDevice2 = deviceUPnP.getUPnPDevice();
/* 1879 */         if (upnpDevice.equals(uPnPDevice2)) {
/* 1880 */           return device;
/*      */         }
/*      */       }
/*      */     }
/* 1884 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected AEDiagnosticsLogger getLogger()
/*      */   {
/* 1890 */     synchronized (this.logger_lock)
/*      */     {
/* 1892 */       if (this.logger == null)
/*      */       {
/* 1894 */         this.logger = AEDiagnostics.getLogger("Devices");
/*      */       }
/*      */       
/* 1897 */       return this.logger;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void log(String s, Throwable e)
/*      */   {
/* 1906 */     AEDiagnosticsLogger diag_logger = getLogger();
/*      */     
/* 1908 */     diag_logger.log(s);
/* 1909 */     diag_logger.log(e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(String s)
/*      */   {
/* 1916 */     AEDiagnosticsLogger diag_logger = getLogger();
/*      */     
/* 1918 */     diag_logger.log(s);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1925 */     writer.println("Devices");
/*      */     try
/*      */     {
/* 1928 */       writer.indent();
/*      */       
/* 1930 */       DeviceImpl[] devices = getDevices();
/*      */       
/* 1932 */       for (DeviceImpl device : devices)
/*      */       {
/* 1934 */         device.generate(writer);
/*      */       }
/*      */       
/* 1937 */       if (this.transcode_manager != null)
/*      */       {
/* 1939 */         this.transcode_manager.generate(writer);
/*      */       }
/*      */     }
/*      */     finally {
/* 1943 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class DeviceManufacturerImpl
/*      */     implements DeviceManager.DeviceManufacturer
/*      */   {
/*      */     private String name;
/*      */     
/* 1953 */     private List<DeviceTemplate> templates = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */     protected DeviceManufacturerImpl(String _name)
/*      */     {
/* 1959 */       this.name = _name;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void addTemplate(DeviceTemplate t)
/*      */     {
/* 1966 */       this.templates.add(t);
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 1972 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     public DeviceTemplate[] getDeviceTemplates()
/*      */     {
/* 1978 */       return (DeviceTemplate[])this.templates.toArray(new DeviceTemplate[this.templates.size()]);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */