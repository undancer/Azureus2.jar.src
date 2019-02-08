/*      */ package org.gudy.azureus2.pluginsimpl.local;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginEventListener;
/*      */ import org.gudy.azureus2.plugins.PluginException;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDManager;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTManager;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*      */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
/*      */ import org.gudy.azureus2.plugins.messaging.MessageManager;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManager;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.config.ConfigSection;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*      */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ddb.DDBaseImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.deprecate.PluginDeprecation;
/*      */ import org.gudy.azureus2.pluginsimpl.local.dht.mainline.MainlineDHTManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ipc.IPCInterfaceImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ipfilter.IPFilterImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.logging.LoggerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.network.ConnectionManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.sharing.ShareManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.tracker.TrackerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ConfigSectionHolder;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ConfigSectionRepository;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterRepository;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.config.PluginConfigUIFactoryImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.update.UpdateManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.ShortCutsImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class PluginInterfaceImpl
/*      */   implements PluginInterface, AzureusCoreComponent
/*      */ {
/*   83 */   private static final LogIDs LOGID = LogIDs.PLUGIN;
/*      */   
/*      */   private Plugin plugin;
/*      */   
/*      */   private PluginInitializer initialiser;
/*      */   private Object initialiser_key;
/*      */   protected ClassLoader class_loader;
/*   90 */   private CopyOnWriteList<PluginListener> listeners = new CopyOnWriteList();
/*   91 */   private Set<PluginListener> init_complete_fired_set = new HashSet();
/*      */   
/*   93 */   private CopyOnWriteList<PluginEventListener> event_listeners = new CopyOnWriteList();
/*      */   private String key;
/*      */   private String pluginConfigKey;
/*      */   private Properties props;
/*      */   private String pluginDir;
/*      */   private PluginConfigImpl config;
/*      */   private String plugin_version;
/*      */   private org.gudy.azureus2.plugins.logging.Logger logger;
/*      */   private IPCInterfaceImpl ipc_interface;
/*  102 */   protected List children = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private PluginStateImpl state;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String given_plugin_id;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String plugin_id_to_use;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected PluginInterfaceImpl(Plugin _plugin, PluginInitializer _initialiser, Object _initialiser_key, ClassLoader _class_loader, List<File> _verified_files, String _key, Properties _props, String _pluginDir, String _plugin_id, String _plugin_version)
/*      */     throws PluginException
/*      */   {
/*  136 */     StackTraceElement[] stack = Thread.currentThread().getStackTrace();
/*      */     
/*  138 */     int pos = 0;
/*      */     
/*  140 */     while (!stack[pos].getClassName().equals(PluginInterfaceImpl.class.getName()))
/*      */     {
/*  142 */       pos++;
/*      */     }
/*      */     
/*  145 */     String caller_class = stack[(pos + 1)].getClassName();
/*      */     
/*  147 */     if ((!caller_class.equals("org.gudy.azureus2.pluginsimpl.local.PluginInitializer")) && (!caller_class.equals("org.gudy.azureus2.pluginsimpl.local.PluginInterfaceImpl")))
/*      */     {
/*      */ 
/*  150 */       throw new PluginException("Invalid caller");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  155 */     String class_name = getClass().getCanonicalName();
/*      */     
/*  157 */     if ((class_name == null) || (!class_name.equals("org.gudy.azureus2.pluginsimpl.local.PluginInterfaceImpl")))
/*      */     {
/*  159 */       throw new PluginException("Subclassing not permitted");
/*      */     }
/*      */     
/*  162 */     this.plugin = _plugin;
/*  163 */     this.initialiser = _initialiser;
/*  164 */     this.initialiser_key = _initialiser_key;
/*  165 */     this.class_loader = _class_loader;
/*  166 */     this.key = _key;
/*  167 */     this.pluginConfigKey = ("Plugin." + _key);
/*  168 */     this.props = new propertyWrapper(_props);
/*  169 */     this.pluginDir = _pluginDir;
/*  170 */     this.config = new PluginConfigImpl(this, this.pluginConfigKey);
/*  171 */     this.given_plugin_id = _plugin_id;
/*  172 */     this.plugin_version = _plugin_version;
/*  173 */     this.ipc_interface = new IPCInterfaceImpl(this.initialiser, this.plugin);
/*  174 */     this.state = new PluginStateImpl(this, this.initialiser);
/*      */     
/*  176 */     boolean verified = false;
/*  177 */     boolean bad = false;
/*      */     
/*  179 */     if (_plugin_id.endsWith("_v")) {
/*      */       File jar;
/*  181 */       if (this.plugin.getClass() == FailedPlugin.class)
/*      */       {
/*  183 */         verified = true;
/*      */ 
/*      */       }
/*  186 */       else if (_verified_files != null)
/*      */       {
/*  188 */         jar = FileUtil.getJarFileFromClass(this.plugin.getClass());
/*      */         
/*  190 */         if (jar != null)
/*      */         {
/*  192 */           for (File file : _verified_files)
/*      */           {
/*  194 */             if (file.equals(jar))
/*      */             {
/*  196 */               verified = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  203 */       if (!verified)
/*      */       {
/*  205 */         bad = true;
/*      */       }
/*      */     }
/*      */     
/*  209 */     PluginInitializer.setVerified(this, this.plugin, verified, bad);
/*      */   }
/*      */   
/*      */ 
/*      */   public Plugin getPlugin()
/*      */   {
/*  215 */     return this.plugin;
/*      */   }
/*      */   
/*      */   public boolean isOperational() {
/*  219 */     PluginDeprecation.call("isOperational", this.given_plugin_id);
/*  220 */     return getPluginState().isOperational();
/*      */   }
/*      */   
/*      */ 
/*      */   public Object getInitializerKey()
/*      */   {
/*  226 */     return this.initialiser_key;
/*      */   }
/*      */   
/*      */ 
/*      */   public PluginManager getPluginManager()
/*      */   {
/*  232 */     return this.initialiser.getPluginManager();
/*      */   }
/*      */   
/*      */   public String getApplicationName() {
/*  236 */     return Constants.APP_NAME;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getAzureusName()
/*      */   {
/*  242 */     return "Azureus";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getAzureusVersion()
/*      */   {
/*  248 */     return "5.7.6.0";
/*      */   }
/*      */   
/*      */ 
/*      */   public void addConfigSection(ConfigSection section)
/*      */   {
/*  254 */     ConfigSectionRepository.getInstance().addConfigSection(section, this);
/*      */   }
/*      */   
/*      */   public void removeConfigSection(ConfigSection section)
/*      */   {
/*  259 */     ConfigSectionRepository.getInstance().removeConfigSection(section);
/*      */   }
/*      */   
/*      */   public ConfigSection[] getConfigSections() {
/*  263 */     ArrayList<ConfigSection> list = ConfigSectionRepository.getInstance().getList();
/*  264 */     for (Iterator<ConfigSection> iter = list.iterator(); iter.hasNext();) {
/*  265 */       ConfigSection configSection = (ConfigSection)iter.next();
/*  266 */       if (((configSection instanceof ConfigSectionHolder)) && 
/*  267 */         (((ConfigSectionHolder)configSection).getPluginInterface() != this)) {
/*  268 */         iter.remove();
/*      */       }
/*      */     }
/*      */     
/*  272 */     return (ConfigSection[])list.toArray(new ConfigSection[0]);
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public void openTorrentFile(String fileName) {
/*  279 */     PluginDeprecation.call("openTorrentFile", getPluginID());
/*      */     try {
/*  281 */       getDownloadManager().addDownload(new File(fileName));
/*      */     } catch (DownloadException e) {
/*  283 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public void openTorrentURL(String url) {
/*  291 */     PluginDeprecation.call("openTorrentURL", getPluginID());
/*      */     try {
/*  293 */       getDownloadManager().addDownload(new URL(url));
/*      */     } catch (Throwable e) {
/*  295 */       throw new RuntimeException(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPluginName(String name)
/*      */   {
/*  303 */     this.props.put("plugin.name", name);
/*      */   }
/*      */   
/*      */   public String getPluginName()
/*      */   {
/*  308 */     String name = null;
/*      */     
/*  310 */     if (this.props != null)
/*      */     {
/*  312 */       name = (String)this.props.get("plugin.name");
/*      */     }
/*      */     
/*  315 */     if (name == null)
/*      */     {
/*      */       try
/*      */       {
/*  319 */         name = new File(this.pluginDir).getName();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  326 */     if ((name == null) || (name.length() == 0))
/*      */     {
/*  328 */       name = this.plugin.getClass().getName();
/*      */     }
/*      */     
/*  331 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPluginVersion(String version)
/*      */   {
/*  338 */     this.props.put("plugin.version", version);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPluginVersion()
/*      */   {
/*  344 */     String version = (String)this.props.get("plugin.version");
/*      */     
/*  346 */     if (version == null)
/*      */     {
/*  348 */       version = this.plugin_version;
/*      */     }
/*      */     
/*  351 */     return version;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPluginID()
/*      */   {
/*  357 */     String id = (String)this.props.get("plugin.id");
/*      */     
/*      */ 
/*      */ 
/*  361 */     if ((id != null) && (id.equals("azupdater")))
/*      */     {
/*  363 */       this.plugin_id_to_use = id;
/*      */     }
/*      */     
/*  366 */     if (this.plugin_id_to_use != null) { return this.plugin_id_to_use;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  372 */     if (id == null) id = this.given_plugin_id;
/*  373 */     if (id == null) { id = "<none>";
/*      */     }
/*  375 */     this.plugin_id_to_use = id;
/*  376 */     return this.plugin_id_to_use;
/*      */   }
/*      */   
/*      */   public boolean isMandatory() {
/*  380 */     PluginDeprecation.call("isMandatory", this.given_plugin_id);
/*  381 */     return getPluginState().isMandatory();
/*      */   }
/*      */   
/*      */   public boolean isBuiltIn() {
/*  385 */     PluginDeprecation.call("isBuiltIn", this.given_plugin_id);
/*  386 */     return getPluginState().isBuiltIn();
/*      */   }
/*      */   
/*      */   public Properties getPluginProperties()
/*      */   {
/*  391 */     return this.props;
/*      */   }
/*      */   
/*      */ 
/*  395 */   public String getPluginDirectoryName() { return this.pluginDir; }
/*      */   
/*      */   public String getPerUserPluginDirectoryName() {
/*      */     String name;
/*      */     String name;
/*  400 */     if (this.pluginDir == null) {
/*  401 */       name = getPluginID();
/*      */     } else {
/*  403 */       name = new File(this.pluginDir).getName();
/*      */     }
/*      */     
/*  406 */     String str = new File(new File(SystemProperties.getUserPath(), "plugins"), name).getAbsolutePath();
/*      */     
/*  408 */     if (this.pluginDir == null)
/*      */     {
/*  410 */       return str;
/*      */     }
/*      */     try
/*      */     {
/*  414 */       if (new File(this.pluginDir).getCanonicalPath().equals(new File(str).getCanonicalPath()))
/*      */       {
/*  416 */         return this.pluginDir;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*  422 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPluginDirectoryName(String name)
/*      */   {
/*  429 */     this.initialiser_key = new File(name);
/*      */     
/*  431 */     this.pluginDir = name;
/*      */   }
/*      */   
/*      */   public void addConfigUIParameters(Parameter[] parameters, String displayName) {
/*  435 */     ParameterRepository.getInstance().addPlugin(parameters, displayName);
/*      */   }
/*      */   
/*      */   public PluginConfig getPluginconfig()
/*      */   {
/*  440 */     return this.config;
/*      */   }
/*      */   
/*      */   public PluginConfigUIFactory getPluginConfigUIFactory()
/*      */   {
/*  445 */     return new PluginConfigUIFactoryImpl(this.config, this.pluginConfigKey);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPluginConfigKey()
/*      */   {
/*  451 */     return this.pluginConfigKey;
/*      */   }
/*      */   
/*      */   public Tracker getTracker() {
/*  455 */     return TrackerImpl.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ShareManager getShareManager()
/*      */     throws ShareException
/*      */   {
/*  463 */     return ShareManagerImpl.getSingleton();
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadManager getDownloadManager()
/*      */   {
/*  469 */     return DownloadManagerImpl.getSingleton(this.initialiser.getAzureusCore());
/*      */   }
/*      */   
/*      */   public MainlineDHTManager getMainlineDHTManager() {
/*  473 */     return new MainlineDHTManagerImpl(this.initialiser.getAzureusCore());
/*      */   }
/*      */   
/*      */ 
/*      */   public TorrentManager getTorrentManager()
/*      */   {
/*  479 */     return TorrentManagerImpl.getSingleton().specialise(this);
/*      */   }
/*      */   
/*      */   public org.gudy.azureus2.plugins.logging.Logger getLogger()
/*      */   {
/*  484 */     if (this.logger == null)
/*      */     {
/*  486 */       this.logger = new LoggerImpl(this);
/*      */     }
/*      */     
/*  489 */     return this.logger;
/*      */   }
/*      */   
/*      */ 
/*      */   public IPFilter getIPFilter()
/*      */   {
/*  495 */     return new IPFilterImpl();
/*      */   }
/*      */   
/*      */ 
/*      */   public Utilities getUtilities()
/*      */   {
/*  501 */     return new UtilitiesImpl(this.initialiser.getAzureusCore(), this);
/*      */   }
/*      */   
/*      */ 
/*      */   public ShortCuts getShortCuts()
/*      */   {
/*  507 */     return new ShortCutsImpl(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public UIManager getUIManager()
/*      */   {
/*  513 */     return new UIManagerImpl(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public UpdateManager getUpdateManager()
/*      */   {
/*  519 */     return UpdateManagerImpl.getSingleton(this.initialiser.getAzureusCore());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void unloadSupport()
/*      */   {
/*  526 */     this.ipc_interface.unload();
/*      */     
/*  528 */     UIManagerImpl.unload(this);
/*      */   }
/*      */   
/*      */   public boolean isUnloadable() {
/*  532 */     PluginDeprecation.call("unloadable", this.given_plugin_id);
/*  533 */     return getPluginState().isUnloadable();
/*      */   }
/*      */   
/*      */   public void reload() throws PluginException {
/*  537 */     PluginDeprecation.call("reload", this.given_plugin_id);
/*  538 */     getPluginState().reload();
/*      */   }
/*      */   
/*      */   public void unload() throws PluginException {
/*  542 */     PluginDeprecation.call("unload", this.given_plugin_id);
/*  543 */     getPluginState().unload();
/*      */   }
/*      */   
/*      */   public void uninstall() throws PluginException {
/*  547 */     PluginDeprecation.call("uninstall", this.given_plugin_id);
/*  548 */     getPluginState().uninstall();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInitialisationThread()
/*      */   {
/*  554 */     return this.initialiser.isInitialisationThread();
/*      */   }
/*      */   
/*      */ 
/*      */   public ClientIDManager getClientIDManager()
/*      */   {
/*  560 */     return ClientIDManagerImpl.getSingleton();
/*      */   }
/*      */   
/*      */   public ConnectionManager getConnectionManager()
/*      */   {
/*  565 */     return ConnectionManagerImpl.getSingleton(this.initialiser.getAzureusCore());
/*      */   }
/*      */   
/*      */   public MessageManager getMessageManager() {
/*  569 */     return MessageManagerImpl.getSingleton(this.initialiser.getAzureusCore());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DistributedDatabase getDistributedDatabase()
/*      */   {
/*  576 */     return DDBaseImpl.getSingleton(this.initialiser.getAzureusCore());
/*      */   }
/*      */   
/*      */ 
/*      */   public PlatformManager getPlatformManager()
/*      */   {
/*  582 */     return PlatformManagerFactory.getPlatformManager();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialisationComplete()
/*      */   {
/*  588 */     Iterator<PluginListener> it = this.listeners.iterator();
/*      */     
/*  590 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  593 */         fireInitComplete((PluginListener)it.next());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  597 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  601 */     for (int i = 0; i < this.children.size(); i++)
/*      */     {
/*  603 */       ((PluginInterfaceImpl)this.children.get(i)).initialisationComplete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void fireInitComplete(PluginListener listener)
/*      */   {
/*  611 */     synchronized (this.init_complete_fired_set)
/*      */     {
/*  613 */       if (this.init_complete_fired_set.contains(listener))
/*      */       {
/*  615 */         return;
/*      */       }
/*      */       
/*  618 */       this.init_complete_fired_set.add(listener);
/*      */     }
/*      */     try
/*      */     {
/*  622 */       listener.initializationComplete();
/*      */     } catch (Exception e) {
/*  624 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void closedownInitiated()
/*      */   {
/*  631 */     Iterator it = this.listeners.iterator();
/*      */     
/*  633 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  636 */         ((PluginListener)it.next()).closedownInitiated();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  640 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  644 */     for (int i = 0; i < this.children.size(); i++)
/*      */     {
/*  646 */       ((PluginInterfaceImpl)this.children.get(i)).closedownInitiated();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void closedownComplete()
/*      */   {
/*  653 */     Iterator it = this.listeners.iterator();
/*      */     
/*  655 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  658 */         ((PluginListener)it.next()).closedownComplete();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  662 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  666 */     for (int i = 0; i < this.children.size(); i++)
/*      */     {
/*  668 */       ((PluginInterfaceImpl)this.children.get(i)).closedownComplete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public ClassLoader getPluginClassLoader()
/*      */   {
/*  675 */     return this.class_loader;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PluginInterface getLocalPluginInterface(Class plugin_class, String id)
/*      */     throws PluginException
/*      */   {
/*      */     try
/*      */     {
/*  686 */       Plugin p = (Plugin)plugin_class.newInstance();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  691 */       Properties local_props = new Properties(this.props);
/*  692 */       local_props.remove("plugin.id");
/*      */       
/*      */ 
/*  695 */       if (id.endsWith("_v"))
/*      */       {
/*  697 */         throw new Exception("Verified plugins must be loaded from a jar");
/*      */       }
/*      */       
/*  700 */       PluginInterfaceImpl pi = new PluginInterfaceImpl(p, this.initialiser, this.initialiser_key, this.class_loader, null, this.key + "." + id, local_props, this.pluginDir, getPluginID() + "." + id, this.plugin_version);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  713 */       this.initialiser.fireCreated(pi);
/*      */       
/*  715 */       p.initialize(pi);
/*      */       
/*  717 */       this.children.add(pi);
/*      */       
/*  719 */       return pi;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  723 */       if ((e instanceof PluginException))
/*      */       {
/*  725 */         throw ((PluginException)e);
/*      */       }
/*      */       
/*  728 */       throw new PluginException("Local initialisation fails", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public IPCInterfaceImpl getIPC()
/*      */   {
/*  735 */     return this.ipc_interface;
/*      */   }
/*      */   
/*      */   public boolean isShared() {
/*  739 */     PluginDeprecation.call("isShared", this.given_plugin_id);
/*  740 */     return getPluginState().isShared();
/*      */   }
/*      */   
/*      */ 
/*      */   void setAsFailed()
/*      */   {
/*  746 */     getPluginState().setDisabled(true);
/*  747 */     this.state.failed = true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  753 */     this.class_loader = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  758 */     this.plugin = new FailedPlugin("Plugin '" + getPluginID() + "' has been unloaded!", null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(PluginListener l)
/*      */   {
/*  765 */     this.listeners.add(l);
/*      */     
/*  767 */     if (this.initialiser.isInitialisationComplete())
/*      */     {
/*  769 */       fireInitComplete(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(final PluginListener l)
/*      */   {
/*  777 */     this.listeners.remove(l);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  782 */     new DelayedEvent("PIL:clear", 10000L, new AERunnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  789 */         synchronized (PluginInterfaceImpl.this.init_complete_fired_set)
/*      */         {
/*  791 */           PluginInterfaceImpl.this.init_complete_fired_set.remove(l);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addEventListener(final PluginEventListener l)
/*      */   {
/*  801 */     this.initialiser.runPEVTask(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  807 */         List<PluginEvent> events = PluginInterfaceImpl.this.initialiser.getPEVHistory();
/*      */         
/*  809 */         for (PluginEvent event : events) {
/*      */           try
/*      */           {
/*  812 */             l.handleEvent(event);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  816 */             Debug.out(e);
/*      */           }
/*      */         }
/*  819 */         PluginInterfaceImpl.this.event_listeners.add(l);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeEventListener(final PluginEventListener l)
/*      */   {
/*  828 */     this.initialiser.runPEVTask(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  834 */         PluginInterfaceImpl.this.event_listeners.remove(l);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void firePluginEvent(final PluginEvent event)
/*      */   {
/*  843 */     this.initialiser.runPEVTask(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  849 */         PluginInterfaceImpl.this.firePluginEventSupport(event);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void firePluginEventSupport(PluginEvent event)
/*      */   {
/*  858 */     Iterator<PluginEventListener> it = this.event_listeners.iterator();
/*      */     
/*  860 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/*  863 */         PluginEventListener listener = (PluginEventListener)it.next();
/*      */         
/*  865 */         listener.handleEvent(event);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  869 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  873 */     for (int i = 0; i < this.children.size(); i++)
/*      */     {
/*  875 */       ((PluginInterfaceImpl)this.children.get(i)).firePluginEvent(event);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void generateEvidence(IndentWriter writer)
/*      */   {
/*  883 */     writer.println(getPluginName());
/*      */     try
/*      */     {
/*  886 */       writer.indent();
/*      */       
/*  888 */       writer.println("id:" + getPluginID() + ",version:" + getPluginVersion());
/*      */       
/*  890 */       String user_dir = FileUtil.getUserFile("plugins").toString();
/*  891 */       String shared_dir = FileUtil.getApplicationFile("plugins").toString();
/*      */       
/*  893 */       String plugin_dir = getPluginDirectoryName();
/*      */       
/*      */ 
/*  896 */       boolean built_in = false;
/*      */       String type;
/*  898 */       String type; if (plugin_dir.startsWith(shared_dir))
/*      */       {
/*  900 */         type = "shared";
/*      */       } else { String type;
/*  902 */         if (plugin_dir.startsWith(user_dir))
/*      */         {
/*  904 */           type = "per-user";
/*      */         }
/*      */         else
/*      */         {
/*  908 */           built_in = true;
/*      */           
/*  910 */           type = "built-in";
/*      */         }
/*      */       }
/*  913 */       PluginState ps = getPluginState();
/*      */       
/*  915 */       String info = getPluginconfig().getPluginStringParameter("plugin.info");
/*      */       
/*  917 */       writer.println("type:" + type + ",enabled=" + (!ps.isDisabled()) + ",load_at_start=" + ps.isLoadedAtStartup() + ",operational=" + ps.isOperational() + ((info == null) || (info.length() == 0) ? "" : new StringBuilder().append(",info=").append(info).toString()));
/*      */       
/*  919 */       if (ps.isOperational())
/*      */       {
/*  921 */         Plugin plugin = getPlugin();
/*      */         
/*  923 */         if ((plugin instanceof AEDiagnosticsEvidenceGenerator)) {
/*      */           try
/*      */           {
/*  926 */             writer.indent();
/*      */             
/*  928 */             ((AEDiagnosticsEvidenceGenerator)plugin).generate(writer);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  932 */             writer.println("Failed to generate plugin-specific info: " + Debug.getNestedExceptionMessage(e));
/*      */ 
/*      */           }
/*      */           finally {}
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*  940 */       else if (!built_in)
/*      */       {
/*  942 */         File dir = new File(plugin_dir);
/*      */         
/*  944 */         if (dir.exists())
/*      */         {
/*  946 */           String[] files = dir.list();
/*      */           
/*  948 */           if (files != null)
/*      */           {
/*  950 */             String files_str = "";
/*      */             
/*  952 */             for (String f : files)
/*      */             {
/*  954 */               files_str = files_str + (files_str.length() == 0 ? "" : ", ") + f;
/*      */             }
/*      */             
/*  957 */             writer.println("    files: " + files_str);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  964 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isDisabled() {
/*  969 */     PluginDeprecation.call("isDisabled", this.given_plugin_id);
/*  970 */     return getPluginState().isDisabled();
/*      */   }
/*      */   
/*      */   public void setDisabled(boolean disabled) {
/*  974 */     PluginDeprecation.call("setDisabled", this.given_plugin_id);
/*  975 */     getPluginState().setDisabled(disabled);
/*      */   }
/*      */   
/*      */   public PluginState getPluginState() {
/*  979 */     return this.state;
/*      */   }
/*      */   
/*      */   PluginStateImpl getPluginStateImpl() {
/*  983 */     return this.state;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected class propertyWrapper
/*      */     extends Properties
/*      */   {
/*  996 */     protected boolean initialising = true;
/*      */     
/*      */ 
/*      */ 
/*      */     protected propertyWrapper(Properties _props)
/*      */     {
/* 1002 */       Iterator it = _props.keySet().iterator();
/*      */       
/* 1004 */       while (it.hasNext())
/*      */       {
/* 1006 */         Object key = it.next();
/*      */         
/* 1008 */         put(key, _props.get(key));
/*      */       }
/*      */       
/* 1011 */       this.initialising = false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Object setProperty(String str, String val)
/*      */     {
/* 1021 */       if ((!PluginInterfaceImpl.this.plugin.getClass().getName().startsWith("org.gudy")) && (!PluginInterfaceImpl.this.plugin.getClass().getName().startsWith("com.aelitis.")))
/*      */       {
/* 1023 */         if ((str.equalsIgnoreCase("plugin.id")) || (str.equalsIgnoreCase("plugin.version")))
/*      */         {
/* 1025 */           if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 1026 */             org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(PluginInterfaceImpl.LOGID, 1, "Plugin '" + PluginInterfaceImpl.this.getPluginName() + "' tried to set property '" + str + "' - action ignored"));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1031 */           return null;
/*      */         }
/*      */       }
/*      */       
/* 1035 */       return super.setProperty(str, val);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Object put(Object key, Object value)
/*      */     {
/* 1045 */       if ((!PluginInterfaceImpl.this.plugin.getClass().getName().startsWith("org.gudy")) && (!PluginInterfaceImpl.this.plugin.getClass().getName().startsWith("com.aelitis.")))
/*      */       {
/* 1047 */         if ((!this.initialising) && ((key instanceof String)))
/*      */         {
/* 1049 */           String k_str = (String)key;
/*      */           
/* 1051 */           if ((k_str.equalsIgnoreCase("plugin.id")) || (k_str.equalsIgnoreCase("plugin.version")))
/*      */           {
/* 1053 */             if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 1054 */               org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(PluginInterfaceImpl.LOGID, 1, "Plugin '" + PluginInterfaceImpl.this.getPluginName() + "' tried to set property '" + k_str + "' - action ignored"));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1059 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1064 */       return super.put(key, value);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object get(Object key)
/*      */     {
/* 1071 */       return super.get(key);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginInterfaceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */