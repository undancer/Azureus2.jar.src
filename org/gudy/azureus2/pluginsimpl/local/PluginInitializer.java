/*      */ package org.gudy.azureus2.pluginsimpl.local;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*      */ import com.aelitis.azureus.core.AzureusCoreException;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.net.URLConnection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AEVerifier;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginException;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginManagerDefaults;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ import org.gudy.azureus2.pluginsimpl.local.launch.PluginLauncherImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.update.UpdateManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.runnableWithException;
/*      */ import org.gudy.azureus2.update.UpdaterUpdateChecker;
/*      */ import org.gudy.azureus2.update.UpdaterUtils;
/*      */ 
/*      */ 
/*      */ public class PluginInitializer
/*      */   implements GlobalManagerListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   public static final boolean DISABLE_PLUGIN_VERIFICATION = false;
/*   69 */   private static final LogIDs LOGID = LogIDs.CORE;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final String INTERNAL_PLUGIN_ID = "<internal>";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   85 */   private String[][] builtin_plugins = { { "Start/Stop Rules", "com.aelitis.azureus.plugins.startstoprules.defaultplugin.StartStopRulesDefaultPlugin", "azbpstartstoprules", "", "true", "true" }, { "Torrent Removal Rules", "com.aelitis.azureus.plugins.removerules.DownloadRemoveRulesPlugin", "azbpremovalrules", "", "true", "false" }, { "Share Hoster", "com.aelitis.azureus.plugins.sharing.hoster.ShareHosterPlugin", "azbpsharehoster", "ShareHoster", "true", "false" }, { "Plugin Update Checker", "org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin", "azbppluginupdate", "PluginUpdate", "true", "true" }, { "UPnP", "com.aelitis.azureus.plugins.upnp.UPnPPlugin", "azbpupnp", "UPnP", "true", "false" }, { "DHT", "com.aelitis.azureus.plugins.dht.DHTPlugin", "azbpdht", "DHT", "true", "false" }, { "DHT Tracker", "com.aelitis.azureus.plugins.tracker.dht.DHTTrackerPlugin", "azbpdhdtracker", "DHT Tracker", "true", "false" }, { "Magnet URI Handler", "com.aelitis.azureus.plugins.magnet.MagnetPlugin", "azbpmagnet", "Magnet URI Handler", "true", "false" }, { "Core Update Checker", "org.gudy.azureus2.update.CoreUpdateChecker", "azbpcoreupdater", "CoreUpdater", "true", "true" }, { "Core Patch Checker", "org.gudy.azureus2.update.CorePatchChecker", "azbpcorepatcher", "CorePatcher", "true", "true" }, { "Platform Checker", "org.gudy.azureus2.platform.PlatformManagerPluginDelegate", "azplatform2", "azplatform2", "true", "false" }, { "External Seed", "com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin", "azextseed", "azextseed", "true", "false" }, { "Local Tracker", "com.aelitis.azureus.plugins.tracker.local.LocalTrackerPlugin", "azlocaltracker", "azlocaltracker", "true", "false" }, { "Network Status", "com.aelitis.azureus.plugins.net.netstatus.NetStatusPlugin", "aznetstat", "aznetstat", "true", "false" }, { "Buddy", "com.aelitis.azureus.plugins.net.buddy.BuddyPlugin", "azbuddy", "azbuddy", "true", "false" }, { "RSS", "com.aelitis.azureus.core.rssgen.RSSGeneratorPlugin", "azintrss", "azintrss", "true", "false" } };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static VerifiedPluginHolder verified_plugin_holder;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*  200 */     synchronized (PluginInitializer.class)
/*      */     {
/*  202 */       verified_plugin_holder = new VerifiedPluginHolder(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  208 */   private static String[][] default_version_details = { { "org.cneclipse.multiport.MultiPortPlugin", "multi-ports", "Mutli-Port Trackers", "1.0" } };
/*      */   
/*      */ 
/*      */ 
/*      */   private static PluginInitializer singleton;
/*      */   
/*      */ 
/*  215 */   private static AEMonitor class_mon = new AEMonitor("PluginInitializer");
/*      */   
/*  217 */   private static List registration_queue = new ArrayList();
/*      */   
/*  219 */   private static List initThreads = new ArrayList(1);
/*      */   
/*  221 */   private static AsyncDispatcher async_dispatcher = new AsyncDispatcher();
/*  222 */   private static List<PluginEvent> plugin_event_history = new ArrayList();
/*      */   
/*      */ 
/*      */   private AzureusCore azureus_core;
/*      */   
/*      */   private PluginInterfaceImpl default_plugin;
/*      */   
/*      */   private PluginManager plugin_manager;
/*      */   
/*  231 */   private ClassLoader root_class_loader = getClass().getClassLoader();
/*      */   
/*  233 */   private List loaded_pi_list = new ArrayList();
/*      */   
/*      */   private static boolean loading_builtin;
/*      */   
/*  237 */   private List<Plugin> s_plugins = new ArrayList();
/*  238 */   private List<PluginInterfaceImpl> s_plugin_interfaces = new ArrayList();
/*      */   
/*      */   private boolean initialisation_complete;
/*      */   
/*      */   private volatile boolean plugins_initialised;
/*      */   
/*  244 */   private Set<String> vc_disabled_plugins = VersionCheckClient.getSingleton().getDisabledPluginIDs();
/*      */   
/*      */ 
/*      */   public static PluginInitializer getSingleton(AzureusCore azureus_core)
/*      */   {
/*      */     try
/*      */     {
/*  251 */       class_mon.enter();
/*      */       
/*  253 */       if (singleton == null)
/*      */       {
/*  255 */         singleton = new PluginInitializer(azureus_core);
/*      */       }
/*      */       
/*  258 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*  262 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private static PluginInitializer peekSingleton()
/*      */   {
/*      */     try
/*      */     {
/*  270 */       class_mon.enter();
/*      */       
/*  272 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*  276 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static void queueRegistration(Class _class)
/*      */   {
/*      */     try
/*      */     {
/*  285 */       class_mon.enter();
/*      */       
/*  287 */       if (singleton == null)
/*      */       {
/*  289 */         registration_queue.add(_class);
/*      */       }
/*      */       else {
/*      */         try
/*      */         {
/*  294 */           singleton.initializePluginFromClass(_class, "<internal>", _class.getName(), false, false, true);
/*      */ 
/*      */         }
/*      */         catch (PluginException e) {}
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  302 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void queueRegistration(Plugin plugin, String id, String config_key)
/*      */   {
/*      */     try
/*      */     {
/*  313 */       class_mon.enter();
/*      */       
/*  315 */       if (singleton == null)
/*      */       {
/*  317 */         registration_queue.add(new Object[] { plugin, id, config_key });
/*      */       }
/*      */       else {
/*      */         try
/*      */         {
/*  322 */           singleton.initializePluginFromInstance(plugin, id, config_key);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  326 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  331 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static boolean isLoadingBuiltin()
/*      */   {
/*  338 */     return loading_builtin;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void checkAzureusVersion(String name, Properties props, boolean alert_on_fail)
/*      */     throws PluginException
/*      */   {
/*  348 */     String required_version = (String)props.get("plugin.azureus.min_version");
/*  349 */     if (required_version == null) return;
/*  350 */     if (Constants.compareVersions("5.7.6.0", required_version) < 0) {
/*  351 */       String plugin_name_bit = name.length() > 0 ? name + " " : "";
/*  352 */       String msg = "Plugin " + plugin_name_bit + "requires " + Constants.APP_NAME + " version " + required_version + " or higher";
/*  353 */       if (alert_on_fail) {
/*  354 */         Logger.log(new LogAlert(true, 3, msg));
/*      */       }
/*  356 */       throw new PluginException(msg);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void checkJDKVersion(String name, Properties props, boolean alert_on_fail)
/*      */     throws PluginException
/*      */   {
/*  368 */     String required_jdk = (String)props.get("plugin.jdk.min_version");
/*      */     
/*  370 */     if (required_jdk != null)
/*      */     {
/*  372 */       String actual_jdk = Constants.JAVA_VERSION;
/*      */       
/*  374 */       required_jdk = normaliseJDK(required_jdk);
/*  375 */       actual_jdk = normaliseJDK(actual_jdk);
/*      */       
/*  377 */       if ((required_jdk.length() == 0) || (actual_jdk.length() == 0))
/*      */       {
/*  379 */         return;
/*      */       }
/*      */       
/*  382 */       if (Constants.compareVersions(actual_jdk, required_jdk) < 0)
/*      */       {
/*  384 */         String msg = "Plugin " + (name.length() > 0 ? name + " " : "") + "requires Java version " + required_jdk + " or higher";
/*      */         
/*  386 */         if (alert_on_fail)
/*      */         {
/*  388 */           Logger.log(new LogAlert(true, 3, msg));
/*      */         }
/*      */         
/*  391 */         throw new PluginException(msg);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static String normaliseJDK(String jdk)
/*      */   {
/*      */     try
/*      */     {
/*  401 */       String str = "";
/*      */       
/*      */ 
/*      */ 
/*  405 */       for (int i = 0; i < jdk.length(); i++)
/*      */       {
/*  407 */         char c = jdk.charAt(i);
/*      */         
/*  409 */         if ((c != '.') && (!Character.isDigit(c)))
/*      */           break;
/*  411 */         str = str + c;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  421 */       if (Integer.parseInt("" + str.charAt(0)) > 1) {}
/*      */       
/*  423 */       return "1." + str;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  430 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected PluginInitializer(AzureusCore _azureus_core)
/*      */   {
/*  438 */     this.azureus_core = _azureus_core;
/*      */     
/*  440 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  442 */     this.azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void componentCreated(AzureusCore core, AzureusCoreComponent comp)
/*      */       {
/*      */ 
/*      */ 
/*  450 */         if ((comp instanceof GlobalManager))
/*      */         {
/*  452 */           GlobalManager gm = (GlobalManager)comp;
/*      */           
/*  454 */           gm.addListener(PluginInitializer.this);
/*      */         }
/*      */         
/*      */       }
/*  458 */     });
/*  459 */     UpdateManagerImpl.getSingleton(this.azureus_core);
/*      */     
/*  461 */     this.plugin_manager = PluginManagerImpl.getSingleton(this);
/*      */     
/*  463 */     String dynamic_plugins = System.getProperty("azureus.dynamic.plugins", null);
/*      */     
/*  465 */     if (dynamic_plugins != null)
/*      */     {
/*  467 */       String[] classes = dynamic_plugins.split(";");
/*      */       
/*  469 */       for (String c : classes) {
/*      */         try
/*      */         {
/*  472 */           queueRegistration(Class.forName(c));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  476 */           Debug.out("Registration of dynamic plugin '" + c + "' failed", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  481 */     UpdaterUtils.checkBootstrapPlugins();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireCreated(PluginInterfaceImpl pi)
/*      */   {
/*  488 */     this.azureus_core.triggerLifeCycleComponentCreated(pi);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fireOperational(PluginInterfaceImpl pi, boolean op)
/*      */   {
/*  496 */     fireEventSupport(op ? 8 : 9, pi);
/*      */   }
/*      */   
/*      */ 
/*      */   public static void addInitThread()
/*      */   {
/*  502 */     synchronized (initThreads)
/*      */     {
/*  504 */       if (initThreads.contains(Thread.currentThread()))
/*      */       {
/*  506 */         Debug.out("Already added");
/*      */       }
/*      */       
/*  509 */       initThreads.add(Thread.currentThread());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void removeInitThread()
/*      */   {
/*  516 */     synchronized (initThreads)
/*      */     {
/*  518 */       initThreads.remove(Thread.currentThread());
/*      */     }
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
/*      */ 
/*      */   protected boolean isInitialisationThread()
/*      */   {
/*  534 */     return isInitThread();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List loadPlugins(AzureusCore core, boolean bSkipAlreadyLoaded, boolean load_external_plugins, boolean loading_for_startup, boolean initialise_plugins)
/*      */   {
/*  545 */     if (bSkipAlreadyLoaded)
/*      */     {
/*      */       List pis;
/*      */       
/*      */ 
/*      */ 
/*  551 */       synchronized (this.s_plugin_interfaces)
/*      */       {
/*  553 */         pis = new ArrayList(this.s_plugin_interfaces);
/*      */       }
/*      */       
/*  556 */       for (int i = 0; i < pis.size(); i++)
/*      */       {
/*  558 */         PluginInterfaceImpl pi = (PluginInterfaceImpl)pis.get(i);
/*      */         
/*  560 */         Plugin p = pi.getPlugin();
/*      */         
/*  562 */         if ((p instanceof FailedPlugin))
/*      */         {
/*  564 */           unloadPlugin(pi);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  570 */     List pluginLoaded = new ArrayList();
/*      */     
/*  572 */     PluginManagerImpl.setStartDetails(core);
/*      */     
/*  574 */     getRootClassLoader();
/*      */     
/*      */ 
/*      */ 
/*  578 */     File user_dir = FileUtil.getUserFile("plugins");
/*      */     
/*  580 */     File app_dir = FileUtil.getApplicationFile("plugins");
/*      */     
/*  582 */     int user_plugins = 0;
/*  583 */     int app_plugins = 0;
/*      */     
/*  585 */     if ((user_dir.exists()) && (user_dir.isDirectory()))
/*      */     {
/*  587 */       user_plugins = user_dir.listFiles().length;
/*      */     }
/*      */     
/*      */ 
/*  591 */     if ((app_dir.exists()) && (app_dir.isDirectory()))
/*      */     {
/*  593 */       app_plugins = app_dir.listFiles().length;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  599 */     if (load_external_plugins) {
/*  600 */       pluginLoaded.addAll(loadPluginsFromDir(user_dir, 0, user_plugins + app_plugins, bSkipAlreadyLoaded, loading_for_startup, initialise_plugins));
/*      */       
/*      */ 
/*  603 */       if (!user_dir.equals(app_dir))
/*      */       {
/*  605 */         pluginLoaded.addAll(loadPluginsFromDir(app_dir, user_plugins, user_plugins + app_plugins, bSkipAlreadyLoaded, loading_for_startup, initialise_plugins));
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*  610 */     else if (Logger.isEnabled()) {
/*  611 */       Logger.log(new LogEvent(LOGID, "Loading of external plugins skipped"));
/*      */     }
/*      */     
/*      */ 
/*  615 */     if (Logger.isEnabled()) {
/*  616 */       Logger.log(new LogEvent(LOGID, "Loading built-in plugins"));
/*      */     }
/*  618 */     PluginManagerDefaults def = PluginManager.getDefaults();
/*      */     
/*  620 */     for (int i = 0; i < this.builtin_plugins.length; i++)
/*      */     {
/*  622 */       if (def.isDefaultPluginEnabled(this.builtin_plugins[i][0])) {
/*      */         try
/*      */         {
/*  625 */           loading_builtin = true;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  631 */           Class cla = this.root_class_loader.loadClass(this.builtin_plugins[i][1]);
/*      */           
/*  633 */           Method load_method = cla.getMethod("load", new Class[] { PluginInterface.class });
/*      */           
/*  635 */           load_method.invoke(null, new Object[] { getDefaultInterfaceSupport() });
/*      */           
/*  637 */           Logger.log(new LogEvent(LOGID, 1, "Built-in plugin '" + this.builtin_plugins[i][0] + "' ok"));
/*      */ 
/*      */         }
/*      */         catch (NoSuchMethodException e) {}catch (Throwable e)
/*      */         {
/*      */ 
/*  643 */           if (this.builtin_plugins[i][4].equalsIgnoreCase("true"))
/*      */           {
/*  645 */             Debug.printStackTrace(e);
/*      */             
/*  647 */             Logger.log(new LogAlert(false, "Load of built in plugin '" + this.builtin_plugins[i][2] + "' fails", e));
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  652 */           loading_builtin = false;
/*      */         }
/*      */         
/*  655 */       } else if (Logger.isEnabled()) {
/*  656 */         Logger.log(new LogEvent(LOGID, 1, "Built-in plugin '" + this.builtin_plugins[i][2] + "' is disabled"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  661 */     if (Logger.isEnabled()) {
/*  662 */       Logger.log(new LogEvent(LOGID, "Loading dynamically registered plugins"));
/*      */     }
/*  664 */     for (int i = 0; i < registration_queue.size(); i++)
/*      */     {
/*  666 */       Object entry = registration_queue.get(i);
/*      */       
/*      */       String id;
/*      */       Class cla;
/*      */       String id;
/*  671 */       if ((entry instanceof Class))
/*      */       {
/*  673 */         Class cla = (Class)entry;
/*      */         
/*  675 */         id = cla.getName();
/*      */       }
/*      */       else {
/*  678 */         Object[] x = (Object[])entry;
/*      */         
/*  680 */         Plugin plugin = (Plugin)x[0];
/*      */         
/*  682 */         cla = plugin.getClass();
/*      */         
/*  684 */         id = (String)x[1];
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  692 */         Method load_method = cla.getMethod("load", new Class[] { PluginInterface.class });
/*      */         
/*  694 */         load_method.invoke(null, new Object[] { getDefaultInterfaceSupport() });
/*      */ 
/*      */       }
/*      */       catch (NoSuchMethodException e) {}catch (Throwable e)
/*      */       {
/*      */ 
/*  700 */         Debug.printStackTrace(e);
/*      */         
/*  702 */         Logger.log(new LogAlert(false, "Load of dynamic plugin '" + id + "' fails", e));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  707 */     return pluginLoaded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void getRootClassLoader()
/*      */   {
/*  715 */     File user_dir = FileUtil.getUserFile("shared");
/*      */     
/*  717 */     getRootClassLoader(user_dir);
/*      */     
/*  719 */     File app_dir = FileUtil.getApplicationFile("shared");
/*      */     
/*  721 */     if (!user_dir.equals(app_dir))
/*      */     {
/*  723 */       getRootClassLoader(app_dir);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void getRootClassLoader(File dir)
/*      */   {
/*  731 */     dir = new File(dir, "lib");
/*      */     
/*  733 */     if ((dir.exists()) && (dir.isDirectory()))
/*      */     {
/*  735 */       File[] files = dir.listFiles();
/*      */       
/*  737 */       if (files != null)
/*      */       {
/*  739 */         files = PluginLauncherImpl.getHighestJarVersions(files, new String[] { null }, new String[] { null }, false);
/*      */         
/*  741 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  743 */           if (Logger.isEnabled()) {
/*  744 */             Logger.log(new LogEvent(LOGID, "Share class loader extended by " + files[i].toString()));
/*      */           }
/*  746 */           this.root_class_loader = PluginLauncherImpl.addFileToClassPath(PluginInitializer.class.getClassLoader(), this.root_class_loader, files[i]);
/*      */         }
/*      */       }
/*      */     }
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
/*      */ 
/*      */   private List loadPluginsFromDir(File pluginDirectory, int plugin_offset, int plugin_total, boolean bSkipAlreadyLoaded, boolean loading_for_startup, boolean initialise)
/*      */   {
/*  764 */     List dirLoadedPIs = new ArrayList();
/*      */     
/*  766 */     if (Logger.isEnabled()) {
/*  767 */       Logger.log(new LogEvent(LOGID, "Plugin Directory is " + pluginDirectory));
/*      */     }
/*  769 */     if (!pluginDirectory.exists())
/*      */     {
/*  771 */       FileUtil.mkdirs(pluginDirectory);
/*      */     }
/*      */     
/*  774 */     if (pluginDirectory.isDirectory())
/*      */     {
/*  776 */       File[] pluginsDirectory = pluginDirectory.listFiles();
/*      */       
/*  778 */       for (int i = 0; i < pluginsDirectory.length; i++)
/*      */       {
/*  780 */         if (pluginsDirectory[i].getName().equals("CVS"))
/*      */         {
/*  782 */           if (Logger.isEnabled()) {
/*  783 */             Logger.log(new LogEvent(LOGID, "Skipping plugin " + pluginsDirectory[i].getName()));
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  790 */           if (Logger.isEnabled()) {
/*  791 */             Logger.log(new LogEvent(LOGID, "Loading plugin " + pluginsDirectory[i].getName()));
/*      */           }
/*      */           
/*      */           try
/*      */           {
/*  796 */             List loaded_pis = loadPluginFromDir(pluginsDirectory[i], bSkipAlreadyLoaded, loading_for_startup, initialise);
/*      */             
/*      */ 
/*      */ 
/*  800 */             this.loaded_pi_list.add(loaded_pis);
/*  801 */             dirLoadedPIs.addAll(loaded_pis);
/*      */           }
/*      */           catch (PluginException e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  810 */     return dirLoadedPIs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private List loadPluginFromDir(File directory, boolean bSkipAlreadyLoaded, boolean loading_for_startup, boolean initialise)
/*      */     throws PluginException
/*      */   {
/*  822 */     List loaded_pis = new ArrayList();
/*      */     
/*  824 */     ClassLoader plugin_class_loader = this.root_class_loader;
/*      */     
/*  826 */     if (!directory.isDirectory())
/*      */     {
/*  828 */       return loaded_pis;
/*      */     }
/*      */     
/*  831 */     String pluginName = directory.getName();
/*      */     
/*  833 */     File[] pluginContents = directory.listFiles();
/*      */     
/*  835 */     if ((pluginContents == null) || (pluginContents.length == 0))
/*      */     {
/*  837 */       return loaded_pis;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  843 */     boolean looks_like_plugin = false;
/*      */     
/*  845 */     for (int i = 0; i < pluginContents.length; i++)
/*      */     {
/*  847 */       String name = pluginContents[i].getName().toLowerCase();
/*      */       
/*  849 */       if ((name.endsWith(".jar")) || (name.equals("plugin.properties")))
/*      */       {
/*  851 */         looks_like_plugin = true;
/*      */         
/*  853 */         break;
/*      */       }
/*      */     }
/*      */     
/*  857 */     if (!looks_like_plugin)
/*      */     {
/*  859 */       if (Logger.isEnabled()) {
/*  860 */         Logger.log(new LogEvent(LOGID, 1, "Plugin directory '" + directory + "' has no plugin.properties " + "or .jar files, skipping"));
/*      */       }
/*      */       
/*      */ 
/*  864 */       return loaded_pis;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  869 */     String[] plugin_version = { null };
/*  870 */     String[] plugin_id = { null };
/*      */     
/*  872 */     pluginContents = PluginLauncherImpl.getHighestJarVersions(pluginContents, plugin_version, plugin_id, true);
/*      */     
/*  874 */     for (int i = 0; i < pluginContents.length; i++)
/*      */     {
/*  876 */       File jar_file = pluginContents[i];
/*      */       
/*      */ 
/*      */ 
/*  880 */       if (pluginContents.length > 1)
/*      */       {
/*  882 */         String name = jar_file.getName();
/*      */         
/*  884 */         if (name.startsWith("i18nPlugin_"))
/*      */         {
/*      */ 
/*      */ 
/*  888 */           if (Logger.isEnabled()) {
/*  889 */             Logger.log(new LogEvent(LOGID, "renaming '" + name + "' to conform with versioning system"));
/*      */           }
/*      */           
/*  892 */           jar_file.renameTo(new File(jar_file.getParent(), "i18nAZ_0.1.jar  "));
/*      */           
/*  894 */           continue;
/*      */         }
/*      */       }
/*      */       
/*  898 */       plugin_class_loader = PluginLauncherImpl.addFileToClassPath(this.root_class_loader, plugin_class_loader, jar_file);
/*      */     }
/*      */     
/*  901 */     String plugin_class_string = null;
/*      */     try
/*      */     {
/*  904 */       Properties props = new Properties();
/*      */       
/*  906 */       File properties_file = new File(directory.toString() + File.separator + "plugin.properties");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  913 */         if (properties_file.exists())
/*      */         {
/*  915 */           FileInputStream fis = null;
/*      */           try
/*      */           {
/*  918 */             fis = new FileInputStream(properties_file);
/*      */             
/*  920 */             props.load(fis);
/*      */           }
/*      */           finally
/*      */           {
/*  924 */             if (fis != null)
/*      */             {
/*  926 */               fis.close();
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*  932 */         else if ((plugin_class_loader instanceof URLClassLoader))
/*      */         {
/*  934 */           URLClassLoader current = (URLClassLoader)plugin_class_loader;
/*      */           
/*  936 */           URL url = current.findResource("plugin.properties");
/*      */           
/*  938 */           if (url != null) {
/*  939 */             URLConnection connection = url.openConnection();
/*      */             
/*  941 */             InputStream is = connection.getInputStream();
/*      */             
/*  943 */             props.load(is);
/*      */           }
/*      */           else
/*      */           {
/*  947 */             throw new Exception("failed to load plugin.properties from jars");
/*      */           }
/*      */         }
/*      */         else {
/*  951 */           throw new Exception("failed to load plugin.properties from dir or jars");
/*      */         }
/*      */         
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  957 */         Debug.printStackTrace(e);
/*      */         
/*  959 */         String msg = "Can't read 'plugin.properties' for plugin '" + pluginName + "': file may be missing";
/*      */         
/*  961 */         Logger.log(new LogAlert(false, 3, msg));
/*      */         
/*  963 */         System.out.println(msg);
/*      */         
/*  965 */         throw new PluginException(msg, e);
/*      */       }
/*      */       
/*  968 */       checkJDKVersion(pluginName, props, true);
/*  969 */       checkAzureusVersion(pluginName, props, true);
/*      */       
/*  971 */       plugin_class_string = (String)props.get("plugin.class");
/*      */       
/*  973 */       if (plugin_class_string == null)
/*      */       {
/*  975 */         plugin_class_string = (String)props.get("plugin.classes");
/*      */         
/*  977 */         if (plugin_class_string == null)
/*      */         {
/*      */ 
/*      */ 
/*  981 */           plugin_class_string = "";
/*      */         }
/*      */       }
/*      */       
/*  985 */       String plugin_name_string = (String)props.get("plugin.name");
/*      */       
/*  987 */       if (plugin_name_string == null)
/*      */       {
/*  989 */         plugin_name_string = (String)props.get("plugin.names");
/*      */       }
/*      */       
/*  992 */       int pos1 = 0;
/*  993 */       int pos2 = 0;
/*      */       for (;;)
/*      */       {
/*  996 */         int p1 = plugin_class_string.indexOf(";", pos1);
/*      */         
/*      */         String plugin_class;
/*      */         String plugin_class;
/* 1000 */         if (p1 == -1) {
/* 1001 */           plugin_class = plugin_class_string.substring(pos1).trim();
/*      */         } else {
/* 1003 */           plugin_class = plugin_class_string.substring(pos1, p1).trim();
/* 1004 */           pos1 = p1 + 1;
/*      */         }
/*      */         
/* 1007 */         PluginInterfaceImpl existing_pi = getPluginFromClass(plugin_class);
/*      */         
/* 1009 */         if (existing_pi != null)
/*      */         {
/* 1011 */           if (bSkipAlreadyLoaded) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1017 */           File this_parent = directory.getParentFile();
/* 1018 */           File existing_parent = null;
/*      */           
/* 1020 */           if ((existing_pi.getInitializerKey() instanceof File))
/*      */           {
/* 1022 */             existing_parent = ((File)existing_pi.getInitializerKey()).getParentFile();
/*      */           }
/*      */           
/* 1025 */           if ((this_parent.equals(FileUtil.getApplicationFile("plugins"))) && (existing_parent != null) && (existing_parent.equals(FileUtil.getUserFile("plugins"))))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1031 */             if (Logger.isEnabled()) {
/* 1032 */               Logger.log(new LogEvent(LOGID, "Plugin '" + plugin_name_string + "/" + plugin_class + ": shared version overridden by user-specific one"));
/*      */             }
/*      */             
/*      */ 
/* 1036 */             return new ArrayList();
/*      */           }
/*      */           
/* 1039 */           Logger.log(new LogAlert(false, 1, "Error loading '" + plugin_name_string + "', plugin class '" + plugin_class + "' is already loaded"));
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1046 */           String plugin_name = null;
/*      */           
/* 1048 */           if (plugin_name_string != null)
/*      */           {
/* 1050 */             int p2 = plugin_name_string.indexOf(";", pos2);
/*      */             
/*      */ 
/* 1053 */             if (p2 == -1) {
/* 1054 */               plugin_name = plugin_name_string.substring(pos2).trim();
/*      */             } else {
/* 1056 */               plugin_name = plugin_name_string.substring(pos2, p2).trim();
/* 1057 */               pos2 = p2 + 1;
/*      */             }
/*      */           }
/*      */           
/* 1061 */           Properties new_props = (Properties)props.clone();
/*      */           
/* 1063 */           for (int j = 0; j < default_version_details.length; j++)
/*      */           {
/* 1065 */             if (plugin_class.equals(default_version_details[j][0]))
/*      */             {
/* 1067 */               if (new_props.get("plugin.id") == null)
/*      */               {
/* 1069 */                 new_props.put("plugin.id", default_version_details[j][1]);
/*      */               }
/*      */               
/* 1072 */               if (plugin_name == null)
/*      */               {
/* 1074 */                 plugin_name = default_version_details[j][2];
/*      */               }
/*      */               
/* 1077 */               if (new_props.get("plugin.version") == null)
/*      */               {
/*      */ 
/*      */ 
/* 1081 */                 if (plugin_version[0] != null)
/*      */                 {
/* 1083 */                   new_props.put("plugin.version", plugin_version[0]);
/*      */                 }
/*      */                 else
/*      */                 {
/* 1087 */                   new_props.put("plugin.version", default_version_details[j][3]);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1093 */           new_props.put("plugin.class", plugin_class);
/*      */           
/* 1095 */           if (plugin_name != null)
/*      */           {
/* 1097 */             new_props.put("plugin.name", plugin_name);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1106 */           Throwable load_failure = null;
/*      */           
/* 1108 */           String pid = plugin_id[0] == null ? directory.getName() : plugin_id[0];
/*      */           
/* 1110 */           List<File> verified_files = null;
/*      */           
/* 1112 */           Plugin plugin = null;
/*      */           
/* 1114 */           if (this.vc_disabled_plugins.contains(pid))
/*      */           {
/* 1116 */             log("Plugin '" + pid + "' has been administratively disabled");
/*      */           }
/*      */           else {
/*      */             try {
/* 1120 */               String cl_key = "plugin.cl.ext." + pid;
/*      */               
/* 1122 */               String str = COConfigurationManager.getStringParameter(cl_key, null);
/*      */               
/* 1124 */               if ((str != null) && (str.length() > 0))
/*      */               {
/* 1126 */                 COConfigurationManager.removeParameter(cl_key);
/*      */                 
/* 1128 */                 plugin_class_loader = PluginLauncherImpl.extendClassLoader(this.root_class_loader, plugin_class_loader, new URL(str));
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/* 1133 */             if (pid.endsWith("_v"))
/*      */             {
/* 1135 */               verified_files = new ArrayList();
/*      */               
/*      */ 
/*      */ 
/* 1139 */               log("Re-verifying " + pid);
/*      */               
/* 1141 */               for (int i = 0; i < pluginContents.length; i++)
/*      */               {
/* 1143 */                 File jar_file = pluginContents[i];
/*      */                 
/* 1145 */                 if (jar_file.getName().endsWith(".jar")) {
/*      */                   try
/*      */                   {
/* 1148 */                     log("    verifying " + jar_file);
/*      */                     
/* 1150 */                     AEVerifier.verifyData(jar_file);
/*      */                     
/* 1152 */                     verified_files.add(jar_file);
/*      */                     
/* 1154 */                     log("    OK");
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 1158 */                     String msg = "Error loading plugin '" + pluginName + "' / '" + plugin_class_string + "'";
/*      */                     
/* 1160 */                     Logger.log(new LogAlert(false, msg, e));
/*      */                     
/* 1162 */                     plugin = new FailedPlugin(plugin_name, directory.getAbsolutePath());
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1168 */             if (plugin == null)
/*      */             {
/* 1170 */               plugin = PluginLauncherImpl.getPreloadedPlugin(plugin_class);
/*      */               
/* 1172 */               if (plugin == null) {
/*      */                 try
/*      */                 {
/*      */                   try {
/* 1176 */                     Class<Plugin> c = PlatformManagerFactory.getPlatformManager().loadClass(plugin_class_loader, plugin_class);
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/* 1181 */                     plugin = (Plugin)c.newInstance();
/*      */                     
/*      */ 
/*      */                     try
/*      */                     {
/* 1186 */                       if ((plugin_class_loader instanceof URLClassLoader))
/*      */                       {
/* 1188 */                         URL[] urls = ((URLClassLoader)plugin_class_loader).getURLs();
/*      */                         
/* 1190 */                         for (URL u : urls)
/*      */                         {
/* 1192 */                           String path = u.getPath();
/*      */                           
/* 1194 */                           if (path.endsWith(".jar"))
/*      */                           {
/* 1196 */                             int s1 = path.lastIndexOf('/');
/* 1197 */                             int s2 = path.lastIndexOf('\\');
/*      */                             
/* 1199 */                             path = path.substring(Math.max(s1, s2) + 1);
/*      */                             
/* 1201 */                             s2 = path.indexOf('_');
/*      */                             
/* 1203 */                             if (s2 > 0)
/*      */                             {
/* 1205 */                               path = path.substring(0, s2);
/*      */                               
/* 1207 */                               path = path.replaceAll("-", "");
/*      */                               
/* 1209 */                               String cl = "plugin.preinit." + pid + ".PI" + path;
/*      */                               try
/*      */                               {
/* 1212 */                                 Class pic = plugin_class_loader.loadClass(cl);
/*      */                                 
/* 1214 */                                 if (pic != null)
/*      */                                 {
/* 1216 */                                   pic.newInstance();
/*      */                                 }
/*      */                               }
/*      */                               catch (Throwable e) {}
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {}
/*      */                   }
/*      */                   catch (PlatformManagerException e) {
/* 1228 */                     throw e.getCause();
/*      */                   }
/*      */                 }
/*      */                 catch (UnsupportedClassVersionError e) {
/* 1232 */                   plugin = new FailedPlugin(plugin_name, directory.getAbsolutePath());
/*      */                   
/*      */ 
/* 1235 */                   load_failure = new UnsupportedClassVersionError(e.getMessage());
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1239 */                   if ((!(e instanceof ClassNotFoundException)) || (!props.getProperty("plugin.install_if_missing", "no").equalsIgnoreCase("yes")))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1246 */                     load_failure = e;
/*      */                   }
/*      */                   
/* 1249 */                   plugin = new FailedPlugin(plugin_name, directory.getAbsolutePath());
/*      */                 }
/*      */                 
/*      */               } else {
/* 1253 */                 plugin_class_loader = plugin.getClass().getClassLoader();
/*      */               }
/*      */             }
/*      */             
/* 1257 */             MessageText.integratePluginMessages((String)props.get("plugin.langfile"), plugin_class_loader);
/*      */             
/* 1259 */             PluginInterfaceImpl plugin_interface = new PluginInterfaceImpl(plugin, this, directory, plugin_class_loader, verified_files, directory.getName(), new_props, directory.getAbsolutePath(), pid, plugin_version[0]);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1272 */             boolean bEnabled = loading_for_startup ? plugin_interface.getPluginState().isLoadedAtStartup() : initialise;
/*      */             
/* 1274 */             plugin_interface.getPluginState().setDisabled(!bEnabled);
/*      */             
/*      */             try
/*      */             {
/* 1278 */               Method load_method = plugin.getClass().getMethod("load", new Class[] { PluginInterface.class });
/*      */               
/* 1280 */               load_method.invoke(plugin, new Object[] { plugin_interface });
/*      */ 
/*      */             }
/*      */             catch (NoSuchMethodException e) {}catch (Throwable e)
/*      */             {
/*      */ 
/* 1286 */               load_failure = e;
/*      */             }
/*      */             
/* 1289 */             loaded_pis.add(plugin_interface);
/*      */             
/* 1291 */             if (load_failure != null)
/*      */             {
/* 1293 */               plugin_interface.setAsFailed();
/*      */               
/*      */ 
/*      */ 
/* 1297 */               if (!pid.equals(UpdaterUpdateChecker.getPluginID()))
/*      */               {
/* 1299 */                 String msg = MessageText.getString("plugin.init.load.failed", new String[] { plugin_name == null ? pluginName : plugin_name, directory.getAbsolutePath() });
/*      */                 
/*      */ 
/*      */                 LogAlert la;
/*      */                 
/*      */ 
/*      */                 LogAlert la;
/*      */                 
/*      */ 
/* 1308 */                 if ((load_failure instanceof UnsupportedClassVersionError))
/*      */                 {
/* 1310 */                   la = new LogAlert(false, 3, msg + ".\n\n" + MessageText.getString("plugin.install.class_version_error"));
/*      */                 } else { LogAlert la;
/* 1312 */                   if ((load_failure instanceof ClassNotFoundException))
/*      */                   {
/* 1314 */                     la = new LogAlert(false, 3, msg + ".\n\n" + MessageText.getString("plugin.init.load.failed.classmissing") + "\n\n", load_failure);
/*      */                   }
/*      */                   else
/*      */                   {
/* 1318 */                     la = new LogAlert(false, msg, load_failure);
/*      */                   }
/*      */                 }
/* 1321 */                 Logger.log(la);
/*      */                 
/* 1323 */                 System.out.println(msg + ": " + load_failure);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1328 */         if (p1 == -1) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1334 */       return loaded_pis;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1338 */       if ((e instanceof PluginException))
/*      */       {
/* 1340 */         throw ((PluginException)e);
/*      */       }
/*      */       
/* 1343 */       Debug.printStackTrace(e);
/*      */       
/* 1345 */       String msg = "Error loading plugin '" + pluginName + "' / '" + plugin_class_string + "'";
/*      */       
/* 1347 */       Logger.log(new LogAlert(false, msg, e));
/*      */       
/* 1349 */       System.out.println(msg + ": " + e);
/*      */       
/* 1351 */       throw new PluginException(msg, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void log(String str)
/*      */   {
/* 1359 */     if (Logger.isEnabled()) {
/* 1360 */       Logger.log(new LogEvent(LOGID, str));
/*      */     }
/*      */   }
/*      */   
/*      */   public void initialisePlugins()
/*      */   {
/*      */     try
/*      */     {
/* 1368 */       addInitThread();
/*      */       
/* 1370 */       final LinkedList<Runnable> initQueue = new LinkedList();
/*      */       
/* 1372 */       for (int i = 0; i < this.loaded_pi_list.size(); i++) {
/* 1373 */         final int idx = i;
/* 1374 */         initQueue.add(new Runnable() {
/*      */           public void run() {
/*      */             try {
/* 1377 */               List l = (List)PluginInitializer.this.loaded_pi_list.get(idx);
/*      */               
/* 1379 */               if (l.size() > 0) {
/* 1380 */                 PluginInterfaceImpl plugin_interface = (PluginInterfaceImpl)l.get(0);
/*      */                 
/* 1382 */                 if (Logger.isEnabled()) {
/* 1383 */                   Logger.log(new LogEvent(PluginInitializer.LOGID, "Initializing plugin '" + plugin_interface.getPluginName() + "'"));
/*      */                 }
/*      */                 
/* 1386 */                 PluginInitializer.this.initialisePlugin(l);
/*      */                 
/* 1388 */                 if (Logger.isEnabled()) {
/* 1389 */                   Logger.log(new LogEvent(PluginInitializer.LOGID, "Initialization of plugin '" + plugin_interface.getPluginName() + "' complete"));
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1400 */             Logger.doRedirects();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1409 */       initQueue.add(new Runnable() {
/*      */         public void run() {
/* 1411 */           if (Logger.isEnabled()) {
/* 1412 */             Logger.log(new LogEvent(PluginInitializer.LOGID, "Initializing built-in plugins"));
/*      */           }
/*      */           
/*      */         }
/*      */         
/* 1417 */       });
/* 1418 */       final PluginManagerDefaults def = PluginManager.getDefaults();
/*      */       
/* 1420 */       for (int i = 0; i < this.builtin_plugins.length; i++)
/*      */       {
/* 1422 */         final int idx = i;
/*      */         
/* 1424 */         initQueue.add(new Runnable() {
/*      */           public void run() {
/* 1426 */             if (def.isDefaultPluginEnabled(PluginInitializer.this.builtin_plugins[idx][0])) {
/* 1427 */               String id = PluginInitializer.this.builtin_plugins[idx][2];
/* 1428 */               String key = PluginInitializer.this.builtin_plugins[idx][3];
/*      */               try
/*      */               {
/* 1431 */                 Class cla = PluginInitializer.this.root_class_loader.loadClass(PluginInitializer.this.builtin_plugins[idx][1]);
/*      */                 
/*      */ 
/* 1434 */                 if (Logger.isEnabled()) {
/* 1435 */                   Logger.log(new LogEvent(PluginInitializer.LOGID, "Initializing built-in plugin '" + PluginInitializer.this.builtin_plugins[idx][2] + "'"));
/*      */                 }
/*      */                 
/* 1438 */                 PluginInitializer.this.initializePluginFromClass(cla, id, key, "true".equals(PluginInitializer.this.builtin_plugins[idx][5]), true, true);
/*      */                 
/* 1440 */                 if (Logger.isEnabled()) {
/* 1441 */                   Logger.log(new LogEvent(PluginInitializer.LOGID, 1, "Initialization of built in plugin '" + PluginInitializer.this.builtin_plugins[idx][2] + "' complete"));
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/*      */                 try {
/* 1446 */                   PluginInitializer.this.initializePluginFromClass(FailedPlugin.class, id, key, false, false, true);
/*      */                 }
/*      */                 catch (Throwable f) {}
/*      */                 
/*      */ 
/* 1451 */                 if (PluginInitializer.this.builtin_plugins[idx][4].equalsIgnoreCase("true")) {
/* 1452 */                   Debug.printStackTrace(e);
/* 1453 */                   Logger.log(new LogAlert(false, "Initialization of built in plugin '" + PluginInitializer.this.builtin_plugins[idx][2] + "' fails", e));
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/* 1459 */             else if (Logger.isEnabled()) {
/* 1460 */               Logger.log(new LogEvent(PluginInitializer.LOGID, 1, "Built-in plugin '" + PluginInitializer.this.builtin_plugins[idx][2] + "' is disabled"));
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1469 */       initQueue.add(new Runnable() {
/*      */         public void run() {
/* 1471 */           if (Logger.isEnabled()) {
/* 1472 */             Logger.log(new LogEvent(PluginInitializer.LOGID, "Initializing dynamically registered plugins"));
/*      */           }
/*      */         }
/*      */       });
/*      */       
/* 1477 */       for (int i = 0; i < registration_queue.size(); i++)
/*      */       {
/* 1479 */         final int idx = i;
/*      */         
/* 1481 */         initQueue.add(new Runnable() {
/*      */           public void run() {
/*      */             try {
/* 1484 */               Object entry = PluginInitializer.registration_queue.get(idx);
/*      */               
/* 1486 */               if ((entry instanceof Class))
/*      */               {
/* 1488 */                 Class cla = (Class)entry;
/*      */                 
/* 1490 */                 PluginInitializer.singleton.initializePluginFromClass(cla, "<internal>", cla.getName(), false, true, true);
/*      */               }
/*      */               else
/*      */               {
/* 1494 */                 Object[] x = (Object[])entry;
/*      */                 
/* 1496 */                 Plugin plugin = (Plugin)x[0];
/*      */                 
/* 1498 */                 PluginInitializer.singleton.initializePluginFromInstance(plugin, (String)x[1], (String)x[2]);
/*      */               }
/*      */             }
/*      */             catch (PluginException e) {}
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1506 */       AEThread2 secondaryInitializer = new AEThread2("2nd PluginInitializer Thread", true)
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/*      */             
/*      */             
/*      */ 
/*      */             for (;;)
/*      */             {
/*      */               Runnable toRun;
/*      */               
/* 1519 */               synchronized (initQueue)
/*      */               {
/* 1521 */                 if (initQueue.isEmpty()) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 1526 */                 toRun = (Runnable)initQueue.remove(0);
/*      */               }
/*      */               try
/*      */               {
/* 1530 */                 toRun.run();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1534 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 1539 */             PluginInitializer.removeInitThread();
/*      */           }
/*      */         }
/* 1542 */       };
/* 1543 */       secondaryInitializer.start();
/*      */       
/*      */       for (;;)
/*      */       {
/*      */         Runnable toRun;
/*      */         
/* 1549 */         synchronized (initQueue)
/*      */         {
/* 1551 */           if (initQueue.isEmpty()) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1556 */           toRun = (Runnable)initQueue.remove(0);
/*      */         }
/*      */         try
/*      */         {
/* 1560 */           toRun.run();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1564 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/* 1568 */       secondaryInitializer.join();
/*      */       
/* 1570 */       registration_queue.clear();
/*      */       
/* 1572 */       this.plugins_initialised = true;
/*      */       
/* 1574 */       fireEvent(7);
/*      */     }
/*      */     finally
/*      */     {
/* 1578 */       removeInitThread();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkPluginsInitialised()
/*      */   {
/* 1585 */     if (!this.plugins_initialised)
/*      */     {
/* 1587 */       Debug.out("Wait until plugin initialisation is complete until doing this!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isInitialized()
/*      */   {
/* 1594 */     return this.plugins_initialised;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void initialisePlugin(List l)
/*      */     throws PluginException
/*      */   {
/* 1603 */     PluginException last_load_failure = null;
/*      */     
/* 1605 */     for (int i = 0; i < l.size(); i++)
/*      */     {
/* 1607 */       final PluginInterfaceImpl plugin_interface = (PluginInterfaceImpl)l.get(i);
/*      */       
/* 1609 */       if (plugin_interface.getPluginState().isDisabled())
/*      */       {
/* 1611 */         synchronized (this.s_plugin_interfaces)
/*      */         {
/* 1613 */           this.s_plugin_interfaces.add(plugin_interface);
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 1619 */       else if (!plugin_interface.getPluginState().isOperational())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1624 */         Throwable load_failure = null;
/*      */         
/* 1626 */         final Plugin plugin = plugin_interface.getPlugin();
/*      */         
/*      */         try
/*      */         {
/* 1630 */           UtilitiesImpl.callWithPluginThreadContext(plugin_interface, new UtilitiesImpl.runnableWithException()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void run()
/*      */               throws PluginException
/*      */             {
/*      */ 
/* 1638 */               PluginInitializer.this.fireCreated(plugin_interface);
/*      */               
/* 1640 */               plugin.initialize(plugin_interface);
/*      */               
/* 1642 */               if (!(plugin instanceof FailedPlugin))
/*      */               {
/* 1644 */                 plugin_interface.getPluginStateImpl().setOperational(true, false);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1651 */           load_failure = e;
/*      */         }
/*      */         
/* 1654 */         synchronized (this.s_plugin_interfaces)
/*      */         {
/* 1656 */           this.s_plugins.add(plugin);
/*      */           
/* 1658 */           this.s_plugin_interfaces.add(plugin_interface);
/*      */         }
/*      */         
/* 1661 */         if (load_failure != null)
/*      */         {
/* 1663 */           Debug.printStackTrace(load_failure);
/*      */           
/* 1665 */           String msg = "Error initializing plugin '" + plugin_interface.getPluginName() + "'";
/*      */           
/* 1667 */           Logger.log(new LogAlert(false, msg, load_failure));
/*      */           
/* 1669 */           System.out.println(msg + " : " + load_failure);
/*      */           
/* 1671 */           last_load_failure = new PluginException(msg, load_failure);
/*      */         }
/*      */       }
/*      */     }
/* 1675 */     if (last_load_failure != null)
/*      */     {
/* 1677 */       throw last_load_failure;
/*      */     }
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
/*      */   protected void initializePluginFromClass(final Class plugin_class, final String plugin_id, String plugin_config_key, boolean force_enabled, boolean loading_for_startup, boolean initialise)
/*      */     throws PluginException
/*      */   {
/* 1693 */     if ((plugin_class != FailedPlugin.class) && (getPluginFromClass(plugin_class) != null))
/*      */     {
/*      */ 
/* 1696 */       Logger.log(new LogAlert(false, 1, "Error loading '" + plugin_id + "', plugin class '" + plugin_class.getName() + "' is already loaded"));
/*      */       
/*      */ 
/*      */ 
/* 1700 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1704 */       final Plugin plugin = (Plugin)plugin_class.newInstance();
/*      */       
/*      */       String plugin_name;
/*      */       
/* 1708 */       if (plugin_config_key.length() == 0)
/*      */       {
/* 1710 */         String plugin_name = plugin_class.getName();
/*      */         
/* 1712 */         int pos = plugin_name.lastIndexOf(".");
/*      */         
/* 1714 */         if (pos != -1)
/*      */         {
/* 1716 */           plugin_name = plugin_name.substring(pos + 1);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1721 */         plugin_name = plugin_config_key;
/*      */       }
/*      */       
/* 1724 */       Properties properties = new Properties();
/*      */       
/*      */ 
/*      */ 
/* 1728 */       properties.put("plugin.name", plugin_name);
/*      */       
/* 1730 */       final PluginInterfaceImpl plugin_interface = new PluginInterfaceImpl(plugin, this, plugin_class, plugin_class.getClassLoader(), null, plugin_config_key, properties, "", plugin_id, null);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1743 */       boolean bEnabled = loading_for_startup ? plugin_interface.getPluginState().isLoadedAtStartup() : initialise;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1748 */       if ((force_enabled) && (!bEnabled)) {
/* 1749 */         plugin_interface.getPluginState().setLoadedAtStartup(true);
/* 1750 */         bEnabled = true;
/* 1751 */         Logger.log(new LogAlert(false, 1, MessageText.getString("plugins.init.force_enabled", new String[] { plugin_id })));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1756 */       plugin_interface.getPluginState().setDisabled(!bEnabled);
/*      */       
/* 1758 */       final boolean f_enabled = bEnabled;
/*      */       
/* 1760 */       UtilitiesImpl.callWithPluginThreadContext(plugin_interface, new UtilitiesImpl.runnableWithException()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */           throws PluginException
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/* 1770 */             Method load_method = plugin_class.getMethod("load", new Class[] { PluginInterface.class });
/*      */             
/* 1772 */             load_method.invoke(plugin, new Object[] { plugin_interface });
/*      */ 
/*      */           }
/*      */           catch (NoSuchMethodException e) {}catch (Throwable e)
/*      */           {
/*      */ 
/* 1778 */             Debug.printStackTrace(e);
/*      */             
/* 1780 */             Logger.log(new LogAlert(false, "Load of built in plugin '" + plugin_id + "' fails", e));
/*      */           }
/*      */           
/*      */ 
/* 1784 */           if (f_enabled)
/*      */           {
/* 1786 */             PluginInitializer.this.fireCreated(plugin_interface);
/*      */             
/* 1788 */             plugin.initialize(plugin_interface);
/*      */             
/* 1790 */             if (!(plugin instanceof FailedPlugin))
/*      */             {
/* 1792 */               plugin_interface.getPluginStateImpl().setOperational(true, false);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */       
/* 1798 */       synchronized (this.s_plugin_interfaces)
/*      */       {
/* 1800 */         this.s_plugins.add(plugin);
/*      */         
/* 1802 */         this.s_plugin_interfaces.add(plugin_interface);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1806 */       Debug.printStackTrace(e);
/*      */       
/* 1808 */       String msg = "Error loading internal plugin '" + plugin_class.getName() + "'";
/*      */       
/* 1810 */       Logger.log(new LogAlert(false, msg, e));
/*      */       
/* 1812 */       System.out.println(msg + " : " + e);
/*      */       
/* 1814 */       throw new PluginException(msg, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initializePluginFromInstance(final Plugin plugin, String plugin_id, String plugin_config_key)
/*      */     throws PluginException
/*      */   {
/*      */     try
/*      */     {
/* 1827 */       final PluginInterfaceImpl plugin_interface = new PluginInterfaceImpl(plugin, this, plugin.getClass(), plugin.getClass().getClassLoader(), null, plugin_config_key, new Properties(), "", plugin_id, null);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1840 */       UtilitiesImpl.callWithPluginThreadContext(plugin_interface, new UtilitiesImpl.runnableWithException()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void run()
/*      */           throws PluginException
/*      */         {
/*      */ 
/*      */ 
/* 1849 */           PluginInitializer.this.fireCreated(plugin_interface);
/*      */           
/* 1851 */           plugin.initialize(plugin_interface);
/*      */           
/* 1853 */           if (!(plugin instanceof FailedPlugin))
/*      */           {
/* 1855 */             plugin_interface.getPluginStateImpl().setOperational(true, false);
/*      */           }
/*      */         }
/*      */       });
/*      */       
/* 1860 */       synchronized (this.s_plugin_interfaces)
/*      */       {
/* 1862 */         this.s_plugins.add(plugin);
/*      */         
/* 1864 */         this.s_plugin_interfaces.add(plugin_interface);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1868 */       Debug.printStackTrace(e);
/*      */       
/* 1870 */       String msg = "Error loading internal plugin '" + plugin.getClass().getName() + "'";
/*      */       
/* 1872 */       Logger.log(new LogAlert(false, msg, e));
/*      */       
/* 1874 */       System.out.println(msg + " : " + e);
/*      */       
/* 1876 */       throw new PluginException(msg, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void unloadPlugin(PluginInterfaceImpl pi)
/*      */   {
/* 1884 */     synchronized (this.s_plugin_interfaces)
/*      */     {
/* 1886 */       this.s_plugins.remove(pi.getPlugin());
/*      */       
/* 1888 */       this.s_plugin_interfaces.remove(pi);
/*      */     }
/*      */     
/* 1891 */     pi.unloadSupport();
/*      */     
/* 1893 */     for (int i = 0; i < this.loaded_pi_list.size(); i++)
/*      */     {
/* 1895 */       List l = (List)this.loaded_pi_list.get(i);
/*      */       
/* 1897 */       if (l.remove(pi))
/*      */       {
/* 1899 */         if (l.size() != 0)
/*      */           break;
/* 1901 */         this.loaded_pi_list.remove(i); break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1908 */     verified_plugin_holder.removeValue(pi);
/*      */   }
/*      */   
/*      */   protected void reloadPlugin(PluginInterfaceImpl pi) throws PluginException {
/* 1912 */     reloadPlugin(pi, false, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void reloadPlugin(PluginInterfaceImpl pi, boolean loading_for_startup, boolean initialise)
/*      */     throws PluginException
/*      */   {
/* 1920 */     unloadPlugin(pi);
/*      */     
/* 1922 */     Object key = pi.getInitializerKey();
/* 1923 */     String config_key = pi.getPluginConfigKey();
/*      */     
/* 1925 */     if ((key instanceof File))
/*      */     {
/* 1927 */       List pis = loadPluginFromDir((File)key, false, loading_for_startup, initialise);
/*      */       
/* 1929 */       initialisePlugin(pis);
/*      */     }
/*      */     else {
/* 1932 */       initializePluginFromClass((Class)key, pi.getPluginID(), config_key, false, loading_for_startup, initialise);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected AzureusCore getAzureusCore()
/*      */   {
/* 1939 */     return this.azureus_core;
/*      */   }
/*      */   
/*      */ 
/*      */   protected GlobalManager getGlobalManager()
/*      */   {
/* 1945 */     return this.azureus_core.getGlobalManager();
/*      */   }
/*      */   
/*      */ 
/*      */   public static PluginInterface getDefaultInterface()
/*      */   {
/* 1951 */     if (singleton == null) {
/* 1952 */       throw new AzureusCoreException("PluginInitializer not instantiated by AzureusCore.create yet");
/*      */     }
/*      */     
/* 1955 */     return singleton.getDefaultInterfaceSupport();
/*      */   }
/*      */   
/*      */ 
/*      */   protected PluginInterface getDefaultInterfaceSupport()
/*      */   {
/* 1961 */     synchronized (this.s_plugin_interfaces)
/*      */     {
/* 1963 */       if (this.default_plugin == null) {
/*      */         try
/*      */         {
/* 1966 */           this.default_plugin = new PluginInterfaceImpl(new Plugin() { public void initialize(PluginInterface pi) {} }, this, getClass(), getClass().getClassLoader(), null, "default", new Properties(), null, "<internal>", null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
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
/* 1988 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1993 */     return this.default_plugin;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void destroyInitiated()
/*      */   {
/*      */     List plugin_interfaces;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2013 */     synchronized (this.s_plugin_interfaces)
/*      */     {
/* 2015 */       plugin_interfaces = new ArrayList(this.s_plugin_interfaces);
/*      */     }
/*      */     
/* 2018 */     for (int i = 0; i < plugin_interfaces.size(); i++)
/*      */     {
/* 2020 */       ((PluginInterfaceImpl)plugin_interfaces.get(i)).closedownInitiated();
/*      */     }
/*      */     
/* 2023 */     if (this.default_plugin != null)
/*      */     {
/* 2025 */       this.default_plugin.closedownInitiated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroyed()
/*      */   {
/*      */     List plugin_interfaces;
/*      */     
/* 2034 */     synchronized (this.s_plugin_interfaces)
/*      */     {
/* 2036 */       plugin_interfaces = new ArrayList(this.s_plugin_interfaces);
/*      */     }
/*      */     
/* 2039 */     for (int i = 0; i < plugin_interfaces.size(); i++)
/*      */     {
/* 2041 */       ((PluginInterfaceImpl)plugin_interfaces.get(i)).closedownComplete();
/*      */     }
/*      */     
/* 2044 */     if (this.default_plugin != null)
/*      */     {
/* 2046 */       this.default_plugin.closedownComplete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void runPEVTask(AERunnable run)
/*      */   {
/* 2059 */     async_dispatcher.dispatch(run);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected List<PluginEvent> getPEVHistory()
/*      */   {
/* 2066 */     return plugin_event_history;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fireEventSupport(final int type, final Object value)
/*      */   {
/* 2074 */     async_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 2080 */         PluginEvent ev = new PluginEvent()
/*      */         {
/*      */ 
/*      */           public int getType()
/*      */           {
/*      */ 
/* 2086 */             return PluginInitializer.12.this.val$type;
/*      */           }
/*      */           
/*      */ 
/*      */           public Object getValue()
/*      */           {
/* 2092 */             return PluginInitializer.12.this.val$value;
/*      */           }
/*      */         };
/*      */         
/* 2096 */         if ((type == 1) || (type == 2) || (type == 5) || (type == 6) || (type == 7))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2102 */           PluginInitializer.plugin_event_history.add(ev);
/*      */           
/* 2104 */           if (PluginInitializer.plugin_event_history.size() > 1024)
/*      */           {
/* 2106 */             Debug.out("Plugin event history too large!!!!");
/*      */             
/* 2108 */             PluginInitializer.plugin_event_history.remove(0);
/*      */           }
/*      */         }
/*      */         
/*      */         List plugin_interfaces;
/*      */         
/* 2114 */         synchronized (PluginInitializer.this.s_plugin_interfaces)
/*      */         {
/* 2116 */           plugin_interfaces = new ArrayList(PluginInitializer.this.s_plugin_interfaces);
/*      */         }
/*      */         
/* 2119 */         for (int i = 0; i < plugin_interfaces.size(); i++) {
/*      */           try
/*      */           {
/* 2122 */             ((PluginInterfaceImpl)plugin_interfaces.get(i)).firePluginEventSupport(ev);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2126 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/* 2130 */         if (PluginInitializer.this.default_plugin != null)
/*      */         {
/* 2132 */           PluginInitializer.this.default_plugin.firePluginEventSupport(ev);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void waitForEvents()
/*      */   {
/* 2141 */     if (async_dispatcher.isDispatchThread())
/*      */     {
/* 2143 */       Debug.out("Deadlock - recode this monkey boy");
/*      */     }
/*      */     else
/*      */     {
/* 2147 */       final AESemaphore sem = new AESemaphore("waiter");
/*      */       
/* 2149 */       async_dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 2155 */           sem.release();
/*      */         }
/*      */       });
/*      */       
/* 2159 */       if (!sem.reserve(10000L))
/*      */       {
/* 2161 */         Debug.out("Timeout waiting for event dispatch");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void fireEvent(int type)
/*      */   {
/* 2171 */     singleton.fireEventSupport(type, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void fireEvent(int type, Object value)
/*      */   {
/* 2179 */     singleton.fireEventSupport(type, value);
/*      */   }
/*      */   
/*      */ 
/*      */   public static void waitForPluginEvents()
/*      */   {
/* 2185 */     singleton.waitForEvents();
/*      */   }
/*      */   
/*      */ 
/*      */   public void initialisationComplete()
/*      */   {
/* 2191 */     this.initialisation_complete = true;
/*      */     
/* 2193 */     UIManagerImpl.initialisationComplete();
/*      */     
/*      */     List plugin_interfaces;
/*      */     
/* 2197 */     synchronized (this.s_plugin_interfaces)
/*      */     {
/* 2199 */       plugin_interfaces = new ArrayList(this.s_plugin_interfaces);
/*      */     }
/*      */     
/* 2202 */     for (int i = 0; i < plugin_interfaces.size(); i++)
/*      */     {
/* 2204 */       ((PluginInterfaceImpl)plugin_interfaces.get(i)).initialisationComplete();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2210 */     if (this.default_plugin != null)
/*      */     {
/* 2212 */       this.default_plugin.initialisationComplete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isInitialisationComplete()
/*      */   {
/* 2219 */     return this.initialisation_complete;
/*      */   }
/*      */   
/*      */   public static List<PluginInterfaceImpl> getPluginInterfaces()
/*      */   {
/* 2224 */     return singleton.getPluginInterfacesSupport(false);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PluginInterface[] getPlugins()
/*      */   {
/* 2243 */     return getPlugins(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PluginInterface[] getPlugins(boolean expect_partial_result)
/*      */   {
/* 2250 */     List pis = getPluginInterfacesSupport(expect_partial_result);
/*      */     
/* 2252 */     PluginInterface[] res = new PluginInterface[pis.size()];
/*      */     
/* 2254 */     pis.toArray(res);
/*      */     
/* 2256 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   protected PluginManager getPluginManager()
/*      */   {
/* 2262 */     return this.plugin_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected PluginInterfaceImpl getPluginFromClass(Class cla)
/*      */   {
/* 2269 */     return getPluginFromClass(cla.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected PluginInterfaceImpl getPluginFromClass(String class_name)
/*      */   {
/*      */     List plugin_interfaces;
/*      */     
/* 2278 */     synchronized (this.s_plugin_interfaces)
/*      */     {
/* 2280 */       plugin_interfaces = new ArrayList(this.s_plugin_interfaces);
/*      */     }
/*      */     
/* 2283 */     for (int i = 0; i < plugin_interfaces.size(); i++)
/*      */     {
/* 2285 */       PluginInterfaceImpl pi = (PluginInterfaceImpl)plugin_interfaces.get(i);
/*      */       
/* 2287 */       if (pi.getPlugin().getClass().getName().equals(class_name))
/*      */       {
/* 2289 */         return pi;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2295 */     for (int i = 0; i < this.loaded_pi_list.size(); i++)
/*      */     {
/* 2297 */       List l = (List)this.loaded_pi_list.get(i);
/*      */       
/* 2299 */       for (int j = 0; j < l.size(); j++)
/*      */       {
/* 2301 */         PluginInterfaceImpl pi = (PluginInterfaceImpl)l.get(j);
/*      */         
/* 2303 */         if (pi.getPlugin().getClass().getName().equals(class_name))
/*      */         {
/* 2305 */           return pi;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2310 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 2318 */     writer.println("Plugins");
/*      */     try
/*      */     {
/* 2321 */       writer.indent();
/*      */       
/*      */       List plugin_interfaces;
/*      */       
/* 2325 */       synchronized (this.s_plugin_interfaces)
/*      */       {
/* 2327 */         plugin_interfaces = new ArrayList(this.s_plugin_interfaces);
/*      */       }
/*      */       
/* 2330 */       for (int i = 0; i < plugin_interfaces.size(); i++)
/*      */       {
/* 2332 */         PluginInterfaceImpl pi = (PluginInterfaceImpl)plugin_interfaces.get(i);
/*      */         
/* 2334 */         pi.generateEvidence(writer);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2339 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void setVerified(PluginInterfaceImpl pi, Plugin plugin, boolean v, boolean bad)
/*      */     throws PluginException
/*      */   {
/* 2352 */     Object[] existing = (Object[])verified_plugin_holder.setValue(pi, new Object[] { plugin, Boolean.valueOf(v) });
/*      */     
/* 2354 */     if ((existing != null) && ((existing[0] != plugin) || (((Boolean)existing[1]).booleanValue() != v)))
/*      */     {
/* 2356 */       throw new PluginException("Verified status change not permitted");
/*      */     }
/*      */     
/* 2359 */     if (bad)
/*      */     {
/* 2361 */       throw new RuntimeException("Plugin verification failed");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isVerified(PluginInterface pi, Plugin plugin)
/*      */   {
/* 2370 */     if (!(pi instanceof PluginInterfaceImpl))
/*      */     {
/* 2372 */       return false;
/*      */     }
/*      */     
/* 2375 */     VerifiedPluginHolder holder = verified_plugin_holder;
/*      */     
/* 2377 */     if (holder.getClass() != VerifiedPluginHolder.class)
/*      */     {
/* 2379 */       Debug.out("class mismatch");
/*      */       
/* 2381 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2391 */     Object[] ver = (Object[])verified_plugin_holder.getValue(pi);
/*      */     
/* 2393 */     return (ver != null) && (ver[0] == plugin) && (((Boolean)ver[1]).booleanValue());
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean isCoreOrVerifiedPlugin()
/*      */   {
/* 2399 */     Class<?>[] stack = SESecurityManager.getClassContext();
/*      */     
/* 2401 */     ClassLoader core = PluginInitializer.class.getClassLoader();
/*      */     
/* 2403 */     PluginInitializer singleton = peekSingleton();
/*      */     
/* 2405 */     PluginInterface[] pis = singleton == null ? new PluginInterface[0] : singleton.getPlugins();
/*      */     
/* 2407 */     Set<ClassLoader> ok_loaders = new HashSet();
/*      */     
/* 2409 */     ok_loaders.add(core);
/*      */     
/* 2411 */     for (Class<?> c : stack)
/*      */     {
/* 2413 */       ClassLoader cl = c.getClassLoader();
/*      */       
/* 2415 */       if ((cl != null) && (!ok_loaders.contains(cl)))
/*      */       {
/* 2417 */         boolean ok = false;
/*      */         
/* 2419 */         for (PluginInterface pi : pis)
/*      */         {
/* 2421 */           Plugin plugin = pi.getPlugin();
/*      */           
/* 2423 */           if (plugin.getClass().getClassLoader() == cl)
/*      */           {
/* 2425 */             if (isVerified(pi, plugin))
/*      */             {
/* 2427 */               ok_loaders.add(cl);
/*      */               
/* 2429 */               ok = true;
/*      */               
/* 2431 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2436 */         if (!ok)
/*      */         {
/* 2438 */           Debug.out("Class " + c.getCanonicalName() + " with loader " + cl + " isn't trusted");
/*      */           
/* 2440 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2445 */     return true;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public static boolean isInitThread()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: getstatic 1249	org/gudy/azureus2/pluginsimpl/local/PluginInitializer:initThreads	Ljava/util/List;
/*      */     //   3: dup
/*      */     //   4: astore_0
/*      */     //   5: monitorenter
/*      */     //   6: getstatic 1249	org/gudy/azureus2/pluginsimpl/local/PluginInitializer:initThreads	Ljava/util/List;
/*      */     //   9: invokestatic 1320	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*      */     //   12: invokeinterface 1467 2 0
/*      */     //   17: aload_0
/*      */     //   18: monitorexit
/*      */     //   19: ireturn
/*      */     //   20: astore_1
/*      */     //   21: aload_0
/*      */     //   22: monitorexit
/*      */     //   23: aload_1
/*      */     //   24: athrow
/*      */     // Line number table:
/*      */     //   Java source line #525	-> byte code offset #0
/*      */     //   Java source line #527	-> byte code offset #6
/*      */     //   Java source line #528	-> byte code offset #20
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   4	18	0	Ljava/lang/Object;	Object
/*      */     //   20	4	1	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   6	19	20	finally
/*      */     //   20	23	20	finally
/*      */   }
/*      */   
/*      */   public void downloadManagerAdded(DownloadManager dm) {}
/*      */   
/*      */   public void downloadManagerRemoved(DownloadManager dm) {}
/*      */   
/*      */   public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*      */   
/*      */   /* Error */
/*      */   private List<PluginInterfaceImpl> getPluginInterfacesSupport(boolean expect_partial_result)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: iload_1
/*      */     //   1: ifne +7 -> 8
/*      */     //   4: aload_0
/*      */     //   5: invokevirtual 1388	org/gudy/azureus2/pluginsimpl/local/PluginInitializer:checkPluginsInitialised	()V
/*      */     //   8: aload_0
/*      */     //   9: getfield 1253	org/gudy/azureus2/pluginsimpl/local/PluginInitializer:s_plugin_interfaces	Ljava/util/List;
/*      */     //   12: dup
/*      */     //   13: astore_2
/*      */     //   14: monitorenter
/*      */     //   15: new 797	java/util/ArrayList
/*      */     //   18: dup
/*      */     //   19: aload_0
/*      */     //   20: getfield 1253	org/gudy/azureus2/pluginsimpl/local/PluginInitializer:s_plugin_interfaces	Ljava/util/List;
/*      */     //   23: invokespecial 1332	java/util/ArrayList:<init>	(Ljava/util/Collection;)V
/*      */     //   26: aload_2
/*      */     //   27: monitorexit
/*      */     //   28: areturn
/*      */     //   29: astore_3
/*      */     //   30: aload_2
/*      */     //   31: monitorexit
/*      */     //   32: aload_3
/*      */     //   33: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2229	-> byte code offset #0
/*      */     //   Java source line #2231	-> byte code offset #4
/*      */     //   Java source line #2234	-> byte code offset #8
/*      */     //   Java source line #2236	-> byte code offset #15
/*      */     //   Java source line #2237	-> byte code offset #29
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	34	0	this	PluginInitializer
/*      */     //   0	34	1	expect_partial_result	boolean
/*      */     //   13	18	2	Ljava/lang/Object;	Object
/*      */     //   29	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   15	28	29	finally
/*      */     //   29	32	29	finally
/*      */   }
/*      */   
/*      */   private static final class VerifiedPluginHolder
/*      */   {
/* 2451 */     private static final Object NULL_VALUE = new Object();
/*      */     
/*      */     private volatile boolean initialised;
/*      */     
/* 2455 */     private AESemaphore request_sem = new AESemaphore("ValueHolder");
/*      */     
/* 2457 */     private List<Object[]> request_queue = new ArrayList();
/*      */     
/*      */ 
/*      */     private VerifiedPluginHolder()
/*      */     {
/* 2462 */       Class[] context = SESecurityManager.getClassContext();
/*      */       
/* 2464 */       if (context.length == 0)
/*      */       {
/* 2466 */         return;
/*      */       }
/*      */       
/* 2469 */       if (context[2] != PluginInitializer.class)
/*      */       {
/* 2471 */         Debug.out("Illegal operation");
/*      */         
/* 2473 */         return;
/*      */       }
/*      */       
/* 2476 */       AEThread2 t = new AEThread2("PluginVerifier")
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 2482 */           Map<Object, Object> values = new IdentityHashMap();
/*      */           
/*      */           for (;;)
/*      */           {
/* 2486 */             PluginInitializer.VerifiedPluginHolder.this.request_sem.reserve();
/*      */             
/*      */             Object[] req;
/*      */             
/* 2490 */             synchronized (PluginInitializer.VerifiedPluginHolder.this.request_queue)
/*      */             {
/* 2492 */               req = (Object[])PluginInitializer.VerifiedPluginHolder.this.request_queue.remove(0);
/*      */             }
/*      */             
/* 2495 */             if (req[1] == null)
/*      */             {
/* 2497 */               req[1] = values.get(req[0]);
/*      */             }
/*      */             else
/*      */             {
/* 2501 */               Object existing = values.get(req[0]);
/*      */               
/* 2503 */               if (req[1] == PluginInitializer.VerifiedPluginHolder.NULL_VALUE)
/*      */               {
/* 2505 */                 req[1] = existing;
/*      */                 
/* 2507 */                 values.remove(req[0]);
/*      */ 
/*      */               }
/* 2510 */               else if (existing != null)
/*      */               {
/* 2512 */                 req[1] = existing;
/*      */               }
/*      */               else
/*      */               {
/* 2516 */                 values.put(req[0], req[1]);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 2521 */             ((AESemaphore)req[2]).release();
/*      */           }
/*      */           
/*      */         }
/* 2525 */       };
/* 2526 */       t.start();
/*      */       
/* 2528 */       this.initialised = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object removeValue(Object key)
/*      */     {
/* 2535 */       if (!this.initialised)
/*      */       {
/* 2537 */         return null;
/*      */       }
/*      */       
/* 2540 */       AESemaphore sem = new AESemaphore("ValueHolder:remove");
/*      */       
/* 2542 */       Object[] request = { key, NULL_VALUE, sem };
/*      */       
/* 2544 */       synchronized (this.request_queue)
/*      */       {
/* 2546 */         this.request_queue.add(request);
/*      */       }
/*      */       
/* 2549 */       this.request_sem.release();
/*      */       
/* 2551 */       sem.reserve();
/*      */       
/* 2553 */       return request[1];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public Object setValue(Object key, Object value)
/*      */     {
/* 2561 */       if (!this.initialised)
/*      */       {
/* 2563 */         return null;
/*      */       }
/*      */       
/* 2566 */       AESemaphore sem = new AESemaphore("ValueHolder:set");
/*      */       
/* 2568 */       Object[] request = { key, value, sem };
/*      */       
/* 2570 */       synchronized (this.request_queue)
/*      */       {
/* 2572 */         this.request_queue.add(request);
/*      */       }
/*      */       
/* 2575 */       this.request_sem.release();
/*      */       
/* 2577 */       sem.reserve();
/*      */       
/* 2579 */       return request[1];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object getValue(Object key)
/*      */     {
/* 2586 */       if (!this.initialised)
/*      */       {
/* 2588 */         return null;
/*      */       }
/*      */       
/* 2591 */       AESemaphore sem = new AESemaphore("ValueHolder:get");
/*      */       
/* 2593 */       Object[] request = { key, null, sem };
/*      */       
/* 2595 */       synchronized (this.request_queue)
/*      */       {
/* 2597 */         this.request_queue.add(request);
/*      */       }
/*      */       
/* 2600 */       this.request_sem.release();
/*      */       
/* 2602 */       sem.reserve();
/*      */       
/* 2604 */       return request[1];
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginInitializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */