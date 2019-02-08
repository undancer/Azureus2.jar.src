/*      */ package org.gudy.azureus2.ui.webplugin;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.pairing.PairedService;
/*      */ import com.aelitis.azureus.core.pairing.PairingConnectionData;
/*      */ import com.aelitis.azureus.core.pairing.PairingManager;
/*      */ import com.aelitis.azureus.core.pairing.PairingManagerFactory;
/*      */ import com.aelitis.azureus.core.pairing.PairingManagerListener;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPMapping;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.ipfilter.IPRange;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*      */ import org.gudy.azureus2.plugins.tracker.TrackerException;
/*      */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.HyperlinkParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.InfoParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.LabelParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*      */ import org.gudy.azureus2.plugins.ui.config.PasswordParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringListParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.json.simple.JSONObject;
/*      */ 
/*      */ public class WebPlugin implements org.gudy.azureus2.plugins.Plugin, org.gudy.azureus2.plugins.tracker.web.TrackerWebPageGenerator
/*      */ {
/*      */   public static final String PR_ENABLE = "Enable";
/*      */   public static final String PR_DISABLABLE = "Disablable";
/*      */   public static final String PR_PORT = "Port";
/*      */   public static final String PR_BIND_IP = "Bind IP";
/*      */   public static final String PR_ROOT_RESOURCE = "Root Resource";
/*      */   public static final String PR_HOME_PAGE = "Home Page";
/*      */   public static final String PR_ROOT_DIR = "Root Dir";
/*      */   public static final String PR_ACCESS = "Access";
/*      */   public static final String PR_LOG = "DefaultLoggerChannel";
/*      */   public static final String PR_CONFIG_MODEL_PARAMS = "DefaultConfigModelParams";
/*      */   public static final String PR_CONFIG_MODEL = "DefaultConfigModel";
/*      */   public static final String PR_VIEW_MODEL = "DefaultViewModel";
/*      */   public static final String PR_HIDE_RESOURCE_CONFIG = "DefaultHideResourceConfig";
/*      */   public static final String PR_ENABLE_KEEP_ALIVE = "DefaultEnableKeepAlive";
/*      */   public static final String PR_PAIRING_SID = "PairingSID";
/*      */   public static final String PR_NON_BLOCKING = "NonBlocking";
/*      */   public static final String PR_ENABLE_PAIRING = "EnablePairing";
/*      */   public static final String PR_ENABLE_I2P = "EnableI2P";
/*      */   public static final String PR_ENABLE_TOR = "EnableTor";
/*      */   public static final String PR_ENABLE_UPNP = "EnableUPNP";
/*      */   public static final String PROPERTIES_MIGRATED = "Properties Migrated";
/*      */   public static final String CONFIG_MIGRATED = "Config Migrated";
/*      */   public static final String PAIRING_MIGRATED = "Pairing Migrated";
/*      */   public static final String PAIRING_SESSION_KEY = "Pairing Session Key";
/*      */   public static final String CONFIG_PASSWORD_ENABLE = "Password Enable";
/*      */   public static final boolean CONFIG_PASSWORD_ENABLE_DEFAULT = false;
/*      */   public static final String CONFIG_PAIRING_ENABLE = "Pairing Enable";
/*      */   public static final boolean CONFIG_PAIRING_ENABLE_DEFAULT = true;
/*      */   public static final String CONFIG_PORT_OVERRIDE = "Port Override";
/*      */   public static final String CONFIG_PAIRING_AUTO_AUTH = "Pairing Auto Auth";
/*      */   public static final boolean CONFIG_PAIRING_AUTO_AUTH_DEFAULT = true;
/*      */   public static final String CONFIG_ENABLE = "Enable";
/*  103 */   public boolean CONFIG_ENABLE_DEFAULT = true;
/*      */   
/*      */   public static final String CONFIG_USER = "User";
/*      */   
/*      */   public static final String CONFIG_USER_DEFAULT = "";
/*      */   public static final String CONFIG_PASSWORD = "Password";
/*  109 */   public static final byte[] CONFIG_PASSWORD_DEFAULT = new byte[0];
/*      */   
/*      */   public static final String CONFIG_PORT = "Port";
/*  112 */   public int CONFIG_PORT_DEFAULT = 8089;
/*      */   
/*      */   public static final String CONFIG_BIND_IP = "Bind IP";
/*  115 */   public String CONFIG_BIND_IP_DEFAULT = "";
/*      */   
/*      */   public static final String CONFIG_PROTOCOL = "Protocol";
/*      */   
/*      */   public static final String CONFIG_PROTOCOL_DEFAULT = "HTTP";
/*      */   public static final String CONFIG_UPNP_ENABLE = "UPnP Enable";
/*  121 */   public boolean CONFIG_UPNP_ENABLE_DEFAULT = true;
/*      */   
/*      */   public static final String CONFIG_HOME_PAGE = "Home Page";
/*  124 */   public String CONFIG_HOME_PAGE_DEFAULT = "index.html";
/*      */   
/*      */   public static final String CONFIG_ROOT_DIR = "Root Dir";
/*  127 */   public String CONFIG_ROOT_DIR_DEFAULT = "";
/*      */   
/*      */   public static final String CONFIG_ROOT_RESOURCE = "Root Resource";
/*  130 */   public String CONFIG_ROOT_RESOURCE_DEFAULT = "";
/*      */   
/*      */   public static final String CONFIG_MODE = "Mode";
/*      */   
/*      */   public static final String CONFIG_MODE_FULL = "full";
/*      */   public static final String CONFIG_MODE_DEFAULT = "full";
/*      */   public static final String CONFIG_ACCESS = "Access";
/*  137 */   public String CONFIG_ACCESS_DEFAULT = "all";
/*      */   
/*      */   protected static final String NL = "\r\n";
/*      */   
/*  141 */   protected static final String[] welcome_pages = { "index.html", "index.htm", "index.php", "index.tmpl" };
/*      */   
/*      */   protected static File[] welcome_files;
/*  144 */   private static final AsyncDispatcher network_dispatcher = new AsyncDispatcher("webplugin:netdispatch", 5000);
/*      */   
/*      */   protected PluginInterface plugin_interface;
/*      */   
/*      */   private LoggerChannel log;
/*      */   
/*      */   private PluginConfig plugin_config;
/*      */   
/*      */   private BasicPluginViewModel view_model;
/*      */   
/*      */   private BasicPluginConfigModel config_model;
/*      */   
/*      */   private String p_sid;
/*      */   
/*      */   private StringParameter param_home;
/*      */   
/*      */   private StringParameter param_rootdir;
/*      */   
/*      */   private StringParameter param_rootres;
/*      */   
/*      */   private IntParameter param_port;
/*      */   
/*      */   private StringListParameter param_protocol;
/*      */   
/*      */   private StringParameter param_bind;
/*      */   
/*      */   private StringParameter param_access;
/*      */   
/*      */   private InfoParameter param_i2p_dest;
/*      */   
/*      */   private InfoParameter param_tor_dest;
/*      */   private BooleanParameter p_upnp_enable;
/*      */   private BooleanParameter pw_enable;
/*      */   private StringParameter p_user_name;
/*      */   private PasswordParameter p_password;
/*      */   private BooleanParameter param_auto_auth;
/*      */   private IntParameter param_port_or;
/*      */   private boolean setting_auto_auth;
/*      */   private String pairing_access_code;
/*      */   private String pairing_session_code;
/*      */   private boolean plugin_enabled;
/*      */   private boolean na_intf_listener_added;
/*      */   private String home_page;
/*      */   private String file_root;
/*      */   private String resource_root;
/*      */   private String root_dir;
/*  190 */   private boolean ip_range_all = false;
/*      */   
/*      */   private List<IPRange> ip_ranges;
/*      */   
/*      */   private TrackerWebContext tracker_context;
/*      */   
/*      */   private UPnPMapping upnp_mapping;
/*      */   private PairingManagerListener pairing_listener;
/*      */   private Properties properties;
/*  199 */   private static ThreadLocal<String> tls = new ThreadLocal()
/*      */   {
/*      */ 
/*      */     public String initialValue()
/*      */     {
/*      */ 
/*  205 */       return null;
/*      */     }
/*      */   };
/*      */   
/*      */   private static final int LOGOUT_GRACE_MILLIS = 5000;
/*      */   
/*      */   private static final String GRACE_PERIOD_MARKER = "<grace_period>";
/*  212 */   private Map<String, Long> logout_timer = new HashMap();
/*      */   
/*      */   private boolean unloaded;
/*      */   
/*      */ 
/*      */   public WebPlugin()
/*      */   {
/*  219 */     this.properties = new Properties();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public WebPlugin(Properties defaults)
/*      */   {
/*  226 */     this.properties = defaults;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */     throws org.gudy.azureus2.plugins.PluginException
/*      */   {
/*  235 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  237 */     this.plugin_config = this.plugin_interface.getPluginconfig();
/*      */     
/*  239 */     Properties plugin_properties = this.plugin_interface.getPluginProperties();
/*      */     
/*  241 */     if (plugin_properties != null)
/*      */     {
/*  243 */       Object o = plugin_properties.get("plugin." + "Root Dir".replaceAll(" ", "_"));
/*      */       
/*  245 */       if ((o instanceof String))
/*      */       {
/*  247 */         this.properties.put("Root Dir", o);
/*      */       }
/*      */     }
/*      */     
/*  251 */     Boolean pr_enable = (Boolean)this.properties.get("Enable");
/*      */     
/*  253 */     if (pr_enable != null)
/*      */     {
/*  255 */       this.CONFIG_ENABLE_DEFAULT = pr_enable.booleanValue();
/*      */     }
/*      */     
/*  258 */     Integer pr_port = (Integer)this.properties.get("Port");
/*      */     
/*  260 */     if (pr_port != null)
/*      */     {
/*  262 */       this.CONFIG_PORT_DEFAULT = pr_port.intValue();
/*      */     }
/*      */     
/*  265 */     String pr_bind_ip = (String)this.properties.get("Bind IP");
/*      */     
/*  267 */     if (pr_bind_ip != null)
/*      */     {
/*  269 */       this.CONFIG_BIND_IP_DEFAULT = pr_bind_ip.trim();
/*      */     }
/*      */     
/*  272 */     String pr_root_resource = (String)this.properties.get("Root Resource");
/*      */     
/*  274 */     if (pr_root_resource != null)
/*      */     {
/*  276 */       this.CONFIG_ROOT_RESOURCE_DEFAULT = pr_root_resource;
/*      */     }
/*      */     
/*  279 */     String pr_home_page = (String)this.properties.get("Home Page");
/*      */     
/*  281 */     if (pr_home_page != null)
/*      */     {
/*  283 */       this.CONFIG_HOME_PAGE_DEFAULT = pr_home_page;
/*      */     }
/*      */     
/*  286 */     String pr_root_dir = (String)this.properties.get("Root Dir");
/*      */     
/*  288 */     if (pr_root_dir != null)
/*      */     {
/*  290 */       this.CONFIG_ROOT_DIR_DEFAULT = pr_root_dir;
/*      */     }
/*      */     
/*  293 */     String pr_access = (String)this.properties.get("Access");
/*      */     
/*  295 */     if (pr_access != null)
/*      */     {
/*  297 */       this.CONFIG_ACCESS_DEFAULT = pr_access;
/*      */     }
/*      */     
/*  300 */     Boolean pr_enable_upnp = (Boolean)this.properties.get("EnableUPNP");
/*      */     
/*  302 */     if (pr_enable_upnp != null)
/*      */     {
/*  304 */       this.CONFIG_UPNP_ENABLE_DEFAULT = pr_enable_upnp.booleanValue();
/*      */     }
/*      */     
/*  307 */     Boolean pr_hide_resource_config = (Boolean)this.properties.get("DefaultHideResourceConfig");
/*      */     
/*  309 */     this.log = ((LoggerChannel)this.properties.get("DefaultLoggerChannel"));
/*      */     
/*  311 */     if (this.log == null)
/*      */     {
/*  313 */       this.log = this.plugin_interface.getLogger().getChannel("WebPlugin");
/*      */     }
/*      */     
/*  316 */     Boolean prop_pairing_enable = (Boolean)this.properties.get("EnablePairing");
/*      */     
/*  318 */     if ((prop_pairing_enable == null) || (prop_pairing_enable.booleanValue()))
/*      */     {
/*      */ 
/*      */ 
/*  322 */       this.p_sid = ((String)this.properties.get("PairingSID"));
/*      */     }
/*      */     
/*  325 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*  327 */     this.view_model = ((BasicPluginViewModel)this.properties.get("DefaultViewModel"));
/*      */     
/*  329 */     if (this.view_model == null)
/*      */     {
/*  331 */       this.view_model = ui_manager.createBasicPluginViewModel(this.plugin_interface.getPluginName());
/*      */     }
/*      */     
/*  334 */     String plugin_id = this.plugin_interface.getPluginID();
/*      */     
/*  336 */     String sConfigSectionID = "plugins." + plugin_id;
/*      */     
/*  338 */     this.view_model.setConfigSectionID(sConfigSectionID);
/*  339 */     this.view_model.getStatus().setText("Running");
/*  340 */     this.view_model.getActivity().setVisible(false);
/*  341 */     this.view_model.getProgress().setVisible(false);
/*      */     
/*  343 */     this.log.addListener(new org.gudy.azureus2.plugins.logging.LoggerChannelListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageLogged(int type, String message)
/*      */       {
/*      */ 
/*      */ 
/*  351 */         WebPlugin.this.view_model.getLogArea().appendText(message + "\n");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageLogged(String str, Throwable error)
/*      */       {
/*  359 */         WebPlugin.this.view_model.getLogArea().appendText(str + "\n");
/*  360 */         WebPlugin.this.view_model.getLogArea().appendText(error.toString() + "\n");
/*      */       }
/*      */       
/*      */ 
/*  364 */     });
/*  365 */     this.config_model = ((BasicPluginConfigModel)this.properties.get("DefaultConfigModel"));
/*      */     
/*  367 */     if (this.config_model == null)
/*      */     {
/*  369 */       String[] cm_params = (String[])this.properties.get("DefaultConfigModelParams");
/*      */       
/*  371 */       if ((cm_params == null) || (cm_params.length == 0))
/*      */       {
/*  373 */         this.config_model = ui_manager.createBasicPluginConfigModel("plugins", sConfigSectionID);
/*      */       }
/*  375 */       else if (cm_params.length == 1)
/*      */       {
/*  377 */         this.config_model = ui_manager.createBasicPluginConfigModel(cm_params[0]);
/*      */       }
/*      */       else
/*      */       {
/*  381 */         this.config_model = ui_manager.createBasicPluginConfigModel(cm_params[0], cm_params[1]);
/*      */       }
/*      */     }
/*      */     
/*  385 */     boolean save_needed = false;
/*      */     
/*  387 */     if (!this.plugin_config.getPluginBooleanParameter("Config Migrated", false))
/*      */     {
/*  389 */       this.plugin_config.setPluginParameter("Config Migrated", true);
/*      */       
/*  391 */       save_needed = true;
/*      */       
/*  393 */       this.plugin_config.setPluginParameter("Password Enable", this.plugin_config.getBooleanParameter("Tracker Password Enable Web", false));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  398 */       this.plugin_config.setPluginParameter("User", this.plugin_config.getStringParameter("Tracker Username", ""));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  403 */       this.plugin_config.setPluginParameter("Password", this.plugin_config.getByteParameter("Tracker Password", CONFIG_PASSWORD_DEFAULT));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  410 */     if (!this.plugin_config.getPluginBooleanParameter("Properties Migrated", false))
/*      */     {
/*  412 */       this.plugin_config.setPluginParameter("Properties Migrated", true);
/*      */       
/*  414 */       Properties props = this.plugin_interface.getPluginProperties();
/*      */       
/*      */ 
/*      */ 
/*  418 */       if (props.getProperty("port", "").length() > 0)
/*      */       {
/*  420 */         save_needed = true;
/*      */         
/*  422 */         String prop_port = props.getProperty("port", "" + this.CONFIG_PORT_DEFAULT);
/*  423 */         String prop_protocol = props.getProperty("protocol", "HTTP");
/*  424 */         String prop_home = props.getProperty("homepage", this.CONFIG_HOME_PAGE_DEFAULT);
/*  425 */         String prop_rootdir = props.getProperty("rootdir", this.CONFIG_ROOT_DIR_DEFAULT);
/*  426 */         String prop_rootres = props.getProperty("rootresource", this.CONFIG_ROOT_RESOURCE_DEFAULT);
/*  427 */         String prop_mode = props.getProperty("mode", "full");
/*  428 */         String prop_access = props.getProperty("access", this.CONFIG_ACCESS_DEFAULT);
/*      */         
/*  430 */         int prop_port_int = this.CONFIG_PORT_DEFAULT;
/*      */         try
/*      */         {
/*  433 */           prop_port_int = Integer.parseInt(prop_port);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*  438 */         this.plugin_config.setPluginParameter("Port", prop_port_int);
/*  439 */         this.plugin_config.setPluginParameter("Protocol", prop_protocol);
/*  440 */         this.plugin_config.setPluginParameter("Home Page", prop_home);
/*  441 */         this.plugin_config.setPluginParameter("Root Dir", prop_rootdir);
/*  442 */         this.plugin_config.setPluginParameter("Root Resource", prop_rootres);
/*  443 */         this.plugin_config.setPluginParameter("Mode", prop_mode);
/*  444 */         this.plugin_config.setPluginParameter("Access", prop_access);
/*      */         
/*  446 */         File props_file = new File(this.plugin_interface.getPluginDirectoryName(), "plugin.properties");
/*      */         
/*  448 */         PrintWriter pw = null;
/*      */         try
/*      */         {
/*  451 */           File backup = new File(this.plugin_interface.getPluginDirectoryName(), "plugin.properties.bak");
/*      */           
/*  453 */           props_file.renameTo(backup);
/*      */           
/*  455 */           pw = new PrintWriter(new java.io.FileWriter(props_file));
/*      */           
/*  457 */           pw.println("plugin.class=" + props.getProperty("plugin.class"));
/*  458 */           pw.println("plugin.name=" + props.getProperty("plugin.name"));
/*  459 */           pw.println("plugin.version=" + props.getProperty("plugin.version"));
/*  460 */           pw.println("plugin.id=" + props.getProperty("plugin.id"));
/*  461 */           pw.println("");
/*  462 */           pw.println("# configuration has been migrated to plugin config - see view->config->plugins");
/*  463 */           pw.println("# in the SWT user interface");
/*      */           
/*  465 */           this.log.logAlert(1, this.plugin_interface.getPluginName() + " - plugin.properties settings migrated to plugin configuration.");
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  470 */           Debug.printStackTrace(e);
/*      */           
/*  472 */           this.log.logAlert(3, this.plugin_interface.getPluginName() + " - plugin.properties settings migration failed.");
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/*  477 */           if (pw != null)
/*      */           {
/*  479 */             pw.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  485 */     if (save_needed)
/*      */     {
/*  487 */       this.plugin_config.save();
/*      */     }
/*      */     
/*  490 */     Boolean disablable = (Boolean)this.properties.get("Disablable");
/*      */     
/*      */     BooleanParameter param_enable;
/*      */     
/*  494 */     if ((disablable != null) && (disablable.booleanValue()))
/*      */     {
/*  496 */       BooleanParameter param_enable = this.config_model.addBooleanParameter2("Enable", "webui.enable", this.CONFIG_ENABLE_DEFAULT);
/*      */       
/*      */ 
/*  499 */       this.plugin_enabled = param_enable.getValue();
/*      */     }
/*      */     else {
/*  502 */       param_enable = null;
/*      */       
/*  504 */       this.plugin_enabled = true;
/*      */     }
/*      */     
/*  507 */     initStage(1);
/*      */     
/*      */ 
/*      */ 
/*  511 */     this.param_port = this.config_model.addIntParameter2("Port", "webui.port", this.CONFIG_PORT_DEFAULT);
/*      */     
/*  513 */     this.param_port.setGenerateIntermediateEvents(false);
/*      */     
/*  515 */     this.param_bind = this.config_model.addStringParameter2("Bind IP", "webui.bindip", this.CONFIG_BIND_IP_DEFAULT);
/*      */     
/*  517 */     this.param_bind.setGenerateIntermediateEvents(false);
/*      */     
/*  519 */     this.param_protocol = this.config_model.addStringListParameter2("Protocol", "webui.protocol", new String[] { "http", "https" }, "HTTP");
/*      */     
/*      */ 
/*      */ 
/*  523 */     this.param_protocol.setGenerateIntermediateEvents(false);
/*      */     
/*  525 */     ParameterListener update_server_listener = new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  532 */         WebPlugin.this.setupServer();
/*      */       }
/*      */       
/*  535 */     };
/*  536 */     this.param_port.addListener(update_server_listener);
/*  537 */     this.param_bind.addListener(update_server_listener);
/*  538 */     this.param_protocol.addListener(update_server_listener);
/*      */     
/*  540 */     this.param_i2p_dest = this.config_model.addInfoParameter2("webui.i2p_dest", "");
/*  541 */     this.param_i2p_dest.setVisible(false);
/*      */     
/*  543 */     this.param_tor_dest = this.config_model.addInfoParameter2("webui.tor_dest", "");
/*  544 */     this.param_tor_dest.setVisible(false);
/*      */     
/*  546 */     if (param_enable != null) {
/*  547 */       COConfigurationManager.registerExportedParameter(plugin_id + ".enable", param_enable.getConfigKeyName());
/*      */     }
/*  549 */     COConfigurationManager.registerExportedParameter(plugin_id + ".port", this.param_port.getConfigKeyName());
/*  550 */     COConfigurationManager.registerExportedParameter(plugin_id + ".protocol", this.param_protocol.getConfigKeyName());
/*      */     
/*  552 */     this.p_upnp_enable = this.config_model.addBooleanParameter2("UPnP Enable", "webui.upnpenable", this.CONFIG_UPNP_ENABLE_DEFAULT);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  558 */     this.p_upnp_enable.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  565 */         WebPlugin.this.setupUPnP();
/*      */       }
/*      */       
/*  568 */     });
/*  569 */     this.plugin_interface.addListener(new org.gudy.azureus2.plugins.PluginListener()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*  575 */         WebPlugin.this.setupUPnP();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownInitiated() {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*      */     });
/*      */     
/*      */     final LabelParameter pairing_info;
/*      */     
/*      */     final BooleanParameter pairing_enable;
/*      */     
/*      */     final HyperlinkParameter pairing_test;
/*      */     
/*      */     final HyperlinkParameter connection_test;
/*      */     
/*  595 */     if (this.p_sid != null)
/*      */     {
/*  597 */       final PairingManager pm = PairingManagerFactory.getSingleton();
/*      */       
/*  599 */       LabelParameter pairing_info = this.config_model.addLabelParameter2("webui.pairing.info." + (pm.isEnabled() ? "y" : "n"));
/*      */       
/*  601 */       final BooleanParameter pairing_enable = this.config_model.addBooleanParameter2("Pairing Enable", "webui.pairingenable", true);
/*      */       
/*  603 */       if (!this.plugin_config.getPluginBooleanParameter("Pairing Migrated", false))
/*      */       {
/*      */ 
/*      */ 
/*  607 */         boolean has_pw_enabled = this.plugin_config.getPluginBooleanParameter("Password Enable", false);
/*      */         
/*  609 */         if (has_pw_enabled)
/*      */         {
/*  611 */           this.plugin_config.setPluginParameter("Pairing Auto Auth", false);
/*      */         }
/*      */         
/*  614 */         this.plugin_config.setPluginParameter("Pairing Migrated", true);
/*      */       }
/*      */       
/*  617 */       this.param_port_or = this.config_model.addIntParameter2("Port Override", "webui.port.override", 0);
/*      */       
/*  619 */       this.param_auto_auth = this.config_model.addBooleanParameter2("Pairing Auto Auth", "webui.pairing.autoauth", true);
/*      */       
/*  621 */       this.param_auto_auth.addListener(new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(Parameter param)
/*      */         {
/*      */ 
/*  628 */           if ((pairing_enable.getValue()) && (pm.isEnabled()))
/*      */           {
/*  630 */             WebPlugin.this.setupAutoAuth();
/*      */           }
/*      */           else
/*      */           {
/*  634 */             WebPlugin.this.setupSessionCode(null);
/*      */           }
/*      */           
/*      */         }
/*  638 */       });
/*  639 */       HyperlinkParameter connection_test = this.config_model.addHyperlinkParameter2("webui.connectiontest", getConnectionTestURL(this.p_sid));
/*      */       
/*  641 */       HyperlinkParameter pairing_test = this.config_model.addHyperlinkParameter2("webui.pairingtest", "http://remote.vuze.com/?sid=" + this.p_sid);
/*      */       
/*      */ 
/*      */ 
/*  645 */       String sid_key = "Plugin." + plugin_id + ".pairing.sid";
/*      */       
/*  647 */       COConfigurationManager.setStringDefault(sid_key, this.p_sid);
/*      */       
/*  649 */       COConfigurationManager.registerExportedParameter(plugin_id + ".pairing.sid", sid_key);
/*  650 */       COConfigurationManager.registerExportedParameter(plugin_id + ".pairing.enable", pairing_enable.getConfigKeyName());
/*  651 */       COConfigurationManager.registerExportedParameter(plugin_id + ".pairing.auto_auth", this.param_auto_auth.getConfigKeyName());
/*      */     }
/*      */     else {
/*  654 */       pairing_info = null;
/*  655 */       pairing_enable = null;
/*  656 */       this.param_auto_auth = null;
/*  657 */       this.param_port_or = null;
/*  658 */       pairing_test = null;
/*  659 */       connection_test = null;
/*      */     }
/*      */     
/*  662 */     this.config_model.createGroup("ConfigView.section.Pairing", new Parameter[] { pairing_info, pairing_enable, this.param_port_or, this.param_auto_auth, connection_test, pairing_test });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  668 */     this.config_model.createGroup("ConfigView.section.server", new Parameter[] { this.param_port, this.param_bind, this.param_protocol, this.param_i2p_dest, this.param_tor_dest, this.p_upnp_enable });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  674 */     this.param_home = this.config_model.addStringParameter2("Home Page", "webui.homepage", this.CONFIG_HOME_PAGE_DEFAULT);
/*  675 */     this.param_rootdir = this.config_model.addStringParameter2("Root Dir", "webui.rootdir", this.CONFIG_ROOT_DIR_DEFAULT);
/*  676 */     this.param_rootres = this.config_model.addStringParameter2("Root Resource", "webui.rootres", this.CONFIG_ROOT_RESOURCE_DEFAULT);
/*      */     
/*  678 */     if ((pr_hide_resource_config != null) && (pr_hide_resource_config.booleanValue()))
/*      */     {
/*  680 */       this.param_home.setVisible(false);
/*  681 */       this.param_rootdir.setVisible(false);
/*  682 */       this.param_rootres.setVisible(false);
/*      */     }
/*      */     else
/*      */     {
/*  686 */       ParameterListener update_resources_listener = new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(Parameter param)
/*      */         {
/*      */ 
/*  693 */           WebPlugin.this.setupResources();
/*      */         }
/*      */         
/*  696 */       };
/*  697 */       this.param_home.addListener(update_resources_listener);
/*  698 */       this.param_rootdir.addListener(update_resources_listener);
/*  699 */       this.param_rootres.addListener(update_resources_listener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  704 */     LabelParameter a_label1 = this.config_model.addLabelParameter2("webui.mode.info");
/*  705 */     StringListParameter param_mode = this.config_model.addStringListParameter2("Mode", "webui.mode", new String[] { "full", "view" }, "full");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  710 */     LabelParameter a_label2 = this.config_model.addLabelParameter2("webui.access.info");
/*      */     
/*  712 */     this.param_access = this.config_model.addStringParameter2("Access", "webui.access", this.CONFIG_ACCESS_DEFAULT);
/*      */     
/*  714 */     this.param_access.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  721 */         WebPlugin.this.setupAccess();
/*      */       }
/*      */       
/*  724 */     });
/*  725 */     this.pw_enable = this.config_model.addBooleanParameter2("Password Enable", "webui.passwordenable", false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  731 */     this.p_user_name = this.config_model.addStringParameter2("User", "webui.user", "");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  737 */     this.p_password = this.config_model.addPasswordParameter2("Password", "webui.password", 2, CONFIG_PASSWORD_DEFAULT);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  744 */     this.pw_enable.addEnabledOnSelection(this.p_user_name);
/*  745 */     this.pw_enable.addEnabledOnSelection(this.p_password);
/*      */     
/*  747 */     ParameterListener auth_change_listener = new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  754 */         if (WebPlugin.this.param_auto_auth != null)
/*      */         {
/*  756 */           if (!WebPlugin.this.setting_auto_auth)
/*      */           {
/*  758 */             WebPlugin.this.log("Disabling pairing auto-authentication as overridden by user");
/*      */             
/*  760 */             WebPlugin.this.param_auto_auth.setValue(false);
/*      */           }
/*      */         }
/*      */         
/*  764 */         if ((param == WebPlugin.this.p_user_name) || (param == WebPlugin.this.p_password))
/*      */         {
/*  766 */           WebPlugin.this.setupSessionCode(null);
/*      */         }
/*      */         
/*      */       }
/*  770 */     };
/*  771 */     this.p_user_name.addListener(auth_change_listener);
/*  772 */     this.p_password.addListener(auth_change_listener);
/*  773 */     this.pw_enable.addListener(auth_change_listener);
/*      */     
/*  775 */     this.config_model.createGroup("webui.group.access", new Parameter[] { a_label1, param_mode, a_label2, this.param_access, this.pw_enable, this.p_user_name, this.p_password });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  782 */     if (this.p_sid != null)
/*      */     {
/*  784 */       final PairingManager pm = PairingManagerFactory.getSingleton();
/*      */       
/*  786 */       pairing_enable.addListener(new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(Parameter param)
/*      */         {
/*      */ 
/*  793 */           boolean enabled = pairing_enable.getValue();
/*      */           
/*  795 */           WebPlugin.this.param_auto_auth.setEnabled((pm.isEnabled()) && (enabled));
/*  796 */           WebPlugin.this.param_port_or.setEnabled((pm.isEnabled()) && (enabled));
/*      */           
/*  798 */           boolean test_ok = (pm.isEnabled()) && (pairing_enable.getValue()) && (pm.peekAccessCode() != null) && (!pm.hasActionOutstanding());
/*      */           
/*  800 */           pairing_test.setEnabled(test_ok);
/*  801 */           connection_test.setEnabled(test_ok);
/*      */           
/*  803 */           WebPlugin.this.setupPairing(WebPlugin.this.p_sid, enabled);
/*      */         }
/*      */         
/*  806 */       });
/*  807 */       this.pairing_listener = new PairingManagerListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void somethingChanged(PairingManager pm)
/*      */         {
/*      */ 
/*  814 */           pairing_info.setLabelKey("webui.pairing.info." + (pm.isEnabled() ? "y" : "n"));
/*      */           
/*  816 */           if (WebPlugin.this.plugin_enabled)
/*      */           {
/*  818 */             pairing_enable.setEnabled(pm.isEnabled());
/*      */             
/*  820 */             WebPlugin.this.param_auto_auth.setEnabled((pm.isEnabled()) && (pairing_enable.getValue()));
/*  821 */             WebPlugin.this.param_port_or.setEnabled((pm.isEnabled()) && (pairing_enable.getValue()));
/*      */             
/*  823 */             boolean test_ok = (pm.isEnabled()) && (pairing_enable.getValue()) && (pm.peekAccessCode() != null) && (!pm.hasActionOutstanding());
/*      */             
/*  825 */             pairing_test.setEnabled(test_ok);
/*  826 */             connection_test.setEnabled(test_ok);
/*      */           }
/*      */           
/*  829 */           connection_test.setHyperlink(WebPlugin.this.getConnectionTestURL(WebPlugin.this.p_sid));
/*      */           
/*  831 */           WebPlugin.this.setupPairing(WebPlugin.this.p_sid, pairing_enable.getValue());
/*      */         }
/*      */         
/*  834 */       };
/*  835 */       this.pairing_listener.somethingChanged(pm);
/*      */       
/*  837 */       pm.addListener(this.pairing_listener);
/*      */       
/*  839 */       setupPairing(this.p_sid, pairing_enable.getValue());
/*      */       
/*  841 */       Object update_pairing_listener = new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(Parameter param)
/*      */         {
/*      */ 
/*  848 */           WebPlugin.this.updatePairing(WebPlugin.this.p_sid);
/*      */           
/*  850 */           WebPlugin.this.setupUPnP();
/*      */         }
/*      */         
/*  853 */       };
/*  854 */       this.param_port.addListener((ParameterListener)update_pairing_listener);
/*      */       
/*  856 */       this.param_port_or.addListener((ParameterListener)update_pairing_listener);
/*      */       
/*  858 */       this.param_protocol.addListener((ParameterListener)update_pairing_listener);
/*      */     }
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
/*  896 */     if (param_enable != null)
/*      */     {
/*  898 */       final List<Parameter> changed_params = new ArrayList();
/*      */       
/*  900 */       if (!this.plugin_enabled)
/*      */       {
/*  902 */         Parameter[] params = this.config_model.getParameters();
/*      */         
/*  904 */         for (Parameter param : params)
/*      */         {
/*  906 */           if (param != param_enable)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  911 */             if (param.isEnabled())
/*      */             {
/*  913 */               changed_params.add(param);
/*      */               
/*  915 */               param.setEnabled(false);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  920 */       param_enable.addListener(new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void parameterChanged(Parameter e_p)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  931 */           WebPlugin.this.plugin_enabled = ((BooleanParameter)e_p).getValue();
/*      */           
/*  933 */           if (WebPlugin.this.plugin_enabled)
/*      */           {
/*  935 */             for (Parameter p : changed_params)
/*      */             {
/*  937 */               p.setEnabled(true);
/*      */             }
/*      */           }
/*      */           else {
/*  941 */             changed_params.clear();
/*      */             
/*  943 */             Parameter[] params = WebPlugin.this.config_model.getParameters();
/*      */             
/*  945 */             for (Parameter param : params)
/*      */             {
/*  947 */               if (param != e_p)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  952 */                 if (param.isEnabled())
/*      */                 {
/*  954 */                   changed_params.add(param);
/*      */                   
/*  956 */                   param.setEnabled(false);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*  961 */           WebPlugin.this.setupServer();
/*      */           
/*  963 */           WebPlugin.this.setupUPnP();
/*      */           
/*  965 */           if (WebPlugin.this.p_sid != null)
/*      */           {
/*  967 */             WebPlugin.this.setupPairing(WebPlugin.this.p_sid, pairing_enable.getValue());
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  975 */     setupResources();
/*      */     
/*  977 */     setupAccess();
/*      */     
/*  979 */     setupServer();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initStage(int num) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getConnectionTestURL(String sid)
/*      */   {
/*  992 */     String res = "http://pair.vuze.com/pairing/web/test?sid=" + sid;
/*      */     
/*  994 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*      */     
/*  996 */     if (pm.isEnabled())
/*      */     {
/*  998 */       String ac = pm.peekAccessCode();
/*      */       
/* 1000 */       if (ac != null)
/*      */       {
/* 1002 */         res = res + "&ac=" + ac;
/*      */       }
/*      */     }
/*      */     
/* 1006 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isPluginEnabled()
/*      */   {
/* 1012 */     return this.plugin_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void unloadPlugin()
/*      */   {
/* 1018 */     if (this.view_model != null)
/*      */     {
/* 1020 */       this.view_model.destroy();
/*      */       
/* 1022 */       this.view_model = null;
/*      */     }
/*      */     
/* 1025 */     if (this.config_model != null)
/*      */     {
/* 1027 */       this.config_model.destroy();
/*      */       
/* 1029 */       this.config_model = null;
/*      */     }
/*      */     
/* 1032 */     if (this.tracker_context != null)
/*      */     {
/* 1034 */       this.tracker_context.destroy();
/*      */       
/* 1036 */       this.tracker_context = null;
/*      */     }
/*      */     
/* 1039 */     if (this.upnp_mapping != null)
/*      */     {
/* 1041 */       this.upnp_mapping.destroy();
/*      */       
/* 1043 */       this.upnp_mapping = null;
/*      */     }
/*      */     
/* 1046 */     if (this.pairing_listener != null)
/*      */     {
/* 1048 */       PairingManager pm = PairingManagerFactory.getSingleton();
/*      */       
/* 1050 */       pm.removeListener(this.pairing_listener);
/*      */       
/* 1052 */       this.pairing_listener = null;
/*      */     }
/*      */     
/* 1055 */     this.unloaded = true;
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupResources()
/*      */   {
/* 1061 */     this.home_page = this.param_home.getValue().trim();
/*      */     
/* 1063 */     if (this.home_page.length() == 0)
/*      */     {
/* 1065 */       this.home_page = null;
/*      */     }
/* 1067 */     else if (!this.home_page.startsWith("/"))
/*      */     {
/* 1069 */       this.home_page = ("/" + this.home_page);
/*      */     }
/*      */     
/* 1072 */     this.resource_root = this.param_rootres.getValue().trim();
/*      */     
/* 1074 */     if (this.resource_root.length() == 0)
/*      */     {
/* 1076 */       this.resource_root = null;
/*      */     }
/* 1078 */     else if (this.resource_root.startsWith("/"))
/*      */     {
/* 1080 */       this.resource_root = this.resource_root.substring(1);
/*      */     }
/*      */     
/* 1083 */     this.root_dir = this.param_rootdir.getValue().trim();
/*      */     
/* 1085 */     if (this.root_dir.length() == 0)
/*      */     {
/* 1087 */       this.file_root = this.plugin_interface.getPluginDirectoryName();
/*      */       
/* 1089 */       if (this.file_root == null)
/*      */       {
/* 1091 */         this.file_root = (SystemProperties.getUserPath() + "web");
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/* 1097 */     else if ((this.root_dir.startsWith(File.separator)) || (this.root_dir.contains(":")))
/*      */     {
/* 1099 */       this.file_root = this.root_dir;
/*      */     }
/*      */     else
/*      */     {
/* 1103 */       if ((File.separatorChar != '/') && (this.root_dir.contains("/")))
/*      */       {
/* 1105 */         this.root_dir = this.root_dir.replace('/', File.separatorChar);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1110 */       this.file_root = this.plugin_interface.getPluginDirectoryName();
/*      */       
/* 1112 */       if (this.file_root != null)
/*      */       {
/* 1114 */         this.file_root = (this.file_root + File.separator + this.root_dir);
/*      */         
/* 1116 */         if (!new File(this.file_root).exists())
/*      */         {
/*      */           try
/*      */           {
/* 1120 */             String pluginClass = this.plugin_interface.getPluginProperties().getProperty("plugin.class");
/*      */             
/* 1122 */             this.file_root = new File(Class.forName(pluginClass).getProtectionDomain().getCodeSource().getLocation().getPath(), this.root_dir).getAbsolutePath();
/*      */             
/*      */ 
/*      */ 
/* 1126 */             if (!new File(this.file_root).exists())
/*      */             {
/* 1128 */               this.file_root = null;
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1136 */       if (this.file_root == null)
/*      */       {
/* 1138 */         this.file_root = (SystemProperties.getUserPath() + "web" + File.separator + this.root_dir);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1143 */     File f_root = new File(this.file_root);
/*      */     
/* 1145 */     if (!f_root.exists())
/*      */     {
/* 1147 */       String error = "WebPlugin: root dir '" + this.file_root + "' doesn't exist";
/*      */       
/* 1149 */       this.log.log(3, error);
/*      */     }
/* 1151 */     else if (!f_root.isDirectory())
/*      */     {
/* 1153 */       String error = "WebPlugin: root dir '" + this.file_root + "' isn't a directory";
/*      */       
/* 1155 */       this.log.log(3, error);
/*      */     }
/*      */     
/* 1158 */     welcome_files = new File[welcome_pages.length];
/*      */     
/* 1160 */     for (int i = 0; i < welcome_pages.length; i++)
/*      */     {
/* 1162 */       welcome_files[i] = new File(this.file_root + File.separator + welcome_pages[i]);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void setupAccess()
/*      */   {
/* 1169 */     String access_str = this.param_access.getValue().trim();
/*      */     
/* 1171 */     String ip_ranges_str = "";
/*      */     
/* 1173 */     this.ip_ranges = null;
/* 1174 */     this.ip_range_all = false;
/*      */     
/* 1176 */     if ((access_str.length() > 7) && (Character.isDigit(access_str.charAt(0))))
/*      */     {
/* 1178 */       String[] ranges = access_str.replace(';', ',').split(",");
/*      */       
/* 1180 */       this.ip_ranges = new ArrayList();
/*      */       
/* 1182 */       for (String range : ranges)
/*      */       {
/* 1184 */         range = range.trim();
/*      */         
/* 1186 */         if (range.length() > 7)
/*      */         {
/* 1188 */           IPRange ip_range = this.plugin_interface.getIPFilter().createRange(true);
/*      */           
/* 1190 */           int sep = range.indexOf("-");
/*      */           
/* 1192 */           if (sep == -1)
/*      */           {
/* 1194 */             ip_range.setStartIP(range);
/*      */             
/* 1196 */             ip_range.setEndIP(range);
/*      */           }
/*      */           else
/*      */           {
/* 1200 */             ip_range.setStartIP(range.substring(0, sep).trim());
/*      */             
/* 1202 */             ip_range.setEndIP(range.substring(sep + 1).trim());
/*      */           }
/*      */           
/* 1205 */           ip_range.checkValid();
/*      */           
/* 1207 */           if (!ip_range.isValid())
/*      */           {
/* 1209 */             this.log.log(3, "Access parameter '" + range + "' is invalid");
/*      */           }
/*      */           else
/*      */           {
/* 1213 */             this.ip_ranges.add(ip_range);
/*      */             
/* 1215 */             ip_ranges_str = ip_ranges_str + (ip_ranges_str.length() == 0 ? "" : ", ") + ip_range.getStartIP() + " - " + ip_range.getEndIP();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1220 */       if (this.ip_ranges.size() == 0)
/*      */       {
/* 1222 */         this.ip_ranges = null;
/*      */       }
/*      */       
/*      */     }
/* 1226 */     else if ((access_str.equalsIgnoreCase("all")) || (access_str.length() == 0))
/*      */     {
/* 1228 */       this.ip_range_all = true;
/*      */     }
/*      */     
/*      */ 
/* 1232 */     this.log.log(1, "Acceptable IP range = " + (this.ip_ranges == null ? "local" : this.ip_range_all ? "all" : ip_ranges_str));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setupServer()
/*      */   {
/*      */     try
/*      */     {
/* 1243 */       if (!this.plugin_enabled)
/*      */       {
/* 1245 */         if (this.tracker_context != null)
/*      */         {
/* 1247 */           this.tracker_context.destroy();
/*      */           
/* 1249 */           this.tracker_context = null;
/*      */         }
/*      */         
/* 1252 */         return;
/*      */       }
/*      */       
/* 1255 */       final int port = this.param_port.getValue();
/*      */       
/* 1257 */       String protocol_str = this.param_protocol.getValue().trim();
/*      */       
/* 1259 */       String bind_str = this.param_bind.getValue().trim();
/*      */       
/* 1261 */       InetAddress bind_ip = null;
/*      */       
/* 1263 */       if (bind_str.length() > 0)
/*      */       {
/*      */         try {
/* 1266 */           bind_ip = InetAddress.getByName(bind_str);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/* 1271 */         if (bind_ip == null)
/*      */         {
/*      */ 
/*      */ 
/* 1275 */           final NetworkAdmin na = NetworkAdmin.getSingleton();
/*      */           
/* 1277 */           InetAddress[] addresses = na.resolveBindAddresses(bind_str);
/*      */           
/* 1279 */           if (addresses.length > 0)
/*      */           {
/* 1281 */             bind_ip = addresses[0];
/*      */             
/* 1283 */             if (!this.na_intf_listener_added)
/*      */             {
/* 1285 */               this.na_intf_listener_added = true;
/*      */               
/* 1287 */               na.addPropertyChangeListener(new com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void propertyChanged(String property)
/*      */                 {
/*      */ 
/* 1294 */                   if (WebPlugin.this.unloaded)
/*      */                   {
/* 1296 */                     na.removePropertyChangeListener(this);
/*      */ 
/*      */ 
/*      */                   }
/* 1300 */                   else if (property == "Network Interfaces")
/*      */                   {
/* 1302 */                     new AEThread2("setupserver")
/*      */                     {
/*      */ 
/*      */                       public void run()
/*      */                       {
/* 1307 */                         WebPlugin.this.setupServer();
/*      */                       }
/*      */                     }.start();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1318 */         if (bind_ip == null)
/*      */         {
/* 1320 */           this.log.log(3, "Bind IP parameter '" + bind_str + "' is invalid");
/*      */         }
/*      */       }
/*      */       
/* 1324 */       if (this.tracker_context != null)
/*      */       {
/* 1326 */         URL url = this.tracker_context.getURLs()[0];
/*      */         
/* 1328 */         String existing_protocol = url.getProtocol();
/* 1329 */         int existing_port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
/* 1330 */         InetAddress existing_bind_ip = this.tracker_context.getBindIP();
/*      */         
/* 1332 */         if ((existing_port == port) && (existing_protocol.equalsIgnoreCase(protocol_str)) && (sameAddress(bind_ip, existing_bind_ip)))
/*      */         {
/*      */ 
/*      */ 
/* 1336 */           return;
/*      */         }
/*      */         
/* 1339 */         this.tracker_context.destroy();
/*      */         
/* 1341 */         this.tracker_context = null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1346 */       int protocol = protocol_str.equalsIgnoreCase("HTTP") ? 1 : 2;
/*      */       
/* 1348 */       Map<String, Object> tc_properties = new HashMap();
/*      */       
/* 1350 */       Boolean prop_non_blocking = (Boolean)this.properties.get("NonBlocking");
/*      */       
/* 1352 */       if ((prop_non_blocking != null) && (prop_non_blocking.booleanValue()))
/*      */       {
/* 1354 */         tc_properties.put("nonblocking", Boolean.valueOf(true));
/*      */       }
/*      */       
/* 1357 */       this.log.log(1, "Server initialisation: port=" + port + (bind_ip == null ? "" : new StringBuilder().append(", bind=").append(bind_str).append("->").append(bind_ip).append(")").toString()) + ", protocol=" + protocol_str + (this.root_dir.length() == 0 ? "" : new StringBuilder().append(", root=").append(this.root_dir).toString()) + (this.properties.size() == 0 ? "" : new StringBuilder().append(", props=").append(this.properties).toString()));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1364 */       this.tracker_context = this.plugin_interface.getTracker().createWebContext(Constants.APP_NAME + " - " + this.plugin_interface.getPluginName(), port, protocol, bind_ip, tc_properties);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1369 */       Boolean prop_enable_i2p = (Boolean)this.properties.get("EnableI2P");
/*      */       
/* 1371 */       if ((prop_enable_i2p == null) || (prop_enable_i2p.booleanValue()))
/*      */       {
/* 1373 */         network_dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 1379 */             Map<String, Object> options = new HashMap();
/*      */             
/* 1381 */             options.put("port", Integer.valueOf(port));
/*      */             
/* 1383 */             Map<String, Object> reply = AEProxyFactory.getPluginServerProxy(WebPlugin.this.plugin_interface.getPluginName(), "I2P", WebPlugin.this.plugin_interface.getPluginID(), options);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1390 */             if (reply != null)
/*      */             {
/* 1392 */               WebPlugin.this.param_i2p_dest.setVisible(true);
/*      */               
/* 1394 */               String host = (String)reply.get("host");
/*      */               
/* 1396 */               if (!WebPlugin.this.param_i2p_dest.getValue().equals(host))
/*      */               {
/* 1398 */                 WebPlugin.this.param_i2p_dest.setValue(host);
/*      */                 
/* 1400 */                 if (WebPlugin.this.p_sid != null)
/*      */                 {
/* 1402 */                   WebPlugin.this.updatePairing(WebPlugin.this.p_sid);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1410 */       Boolean prop_enable_tor = (Boolean)this.properties.get("EnableTor");
/*      */       
/* 1412 */       if ((prop_enable_tor == null) || (prop_enable_tor.booleanValue()))
/*      */       {
/* 1414 */         network_dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 1420 */             Map<String, Object> options = new HashMap();
/*      */             
/* 1422 */             options.put("port", Integer.valueOf(port));
/*      */             
/* 1424 */             Map<String, Object> reply = AEProxyFactory.getPluginServerProxy(WebPlugin.this.plugin_interface.getPluginName(), "Tor", WebPlugin.this.plugin_interface.getPluginID(), options);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1431 */             if (reply != null)
/*      */             {
/* 1433 */               WebPlugin.this.param_tor_dest.setVisible(true);
/*      */               
/* 1435 */               String host = (String)reply.get("host");
/*      */               
/* 1437 */               if (!WebPlugin.this.param_tor_dest.getValue().equals(host))
/*      */               {
/* 1439 */                 WebPlugin.this.param_tor_dest.setValue(host);
/*      */                 
/* 1441 */                 if (WebPlugin.this.p_sid != null)
/*      */                 {
/* 1443 */                   WebPlugin.this.updatePairing(WebPlugin.this.p_sid);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 1452 */       Boolean pr_enable_keep_alive = (Boolean)this.properties.get("DefaultEnableKeepAlive");
/*      */       
/* 1454 */       if ((pr_enable_keep_alive != null) && (pr_enable_keep_alive.booleanValue()))
/*      */       {
/* 1456 */         this.tracker_context.setEnableKeepAlive(true);
/*      */       }
/*      */       
/* 1459 */       this.tracker_context.addPageGenerator(this);
/*      */       
/* 1461 */       this.tracker_context.addAuthenticationListener(new org.gudy.azureus2.plugins.tracker.web.TrackerAuthenticationAdapter()
/*      */       {
/*      */ 
/* 1464 */         private String last_pw = "";
/* 1465 */         private byte[] last_hash = new byte[0];
/*      */         
/* 1467 */         private final int DELAY = 10000;
/*      */         
/* 1469 */         private Map<String, Object[]> fail_map = new HashMap();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean authenticate(String headers, URL resource, String user, String pw)
/*      */         {
/* 1480 */           long now = SystemTime.getMonotonousTime();
/*      */           
/* 1482 */           String client_address = getHeaderField(headers, "X-Real-IP");
/*      */           
/* 1484 */           if (client_address == null)
/*      */           {
/* 1486 */             client_address = "<unknown>";
/*      */           }
/*      */           
/* 1489 */           synchronized (WebPlugin.this.logout_timer)
/*      */           {
/* 1491 */             Long logout_time = (Long)WebPlugin.this.logout_timer.get(client_address);
/*      */             
/* 1493 */             if ((logout_time != null) && (now - logout_time.longValue() <= 5000L))
/*      */             {
/* 1495 */               WebPlugin.tls.set("<grace_period>");
/*      */               
/* 1497 */               return true;
/*      */             }
/*      */           }
/*      */           
/* 1501 */           boolean result = authenticateSupport(headers, resource, user, pw);
/*      */           
/* 1503 */           if (!result)
/*      */           {
/*      */ 
/*      */ 
/* 1507 */             if (!pw.equals(""))
/*      */             {
/* 1509 */               AESemaphore waiter = null;
/*      */               
/* 1511 */               synchronized (this.fail_map)
/*      */               {
/*      */ 
/* 1514 */                 Object[] x = (Object[])this.fail_map.get(client_address);
/*      */                 
/* 1516 */                 if (x == null)
/*      */                 {
/* 1518 */                   x = new Object[] { new AESemaphore("af:waiter"), new Long(-1L), new Long(-1L), Long.valueOf(now) };
/*      */                   
/* 1520 */                   this.fail_map.put(client_address, x);
/*      */                 }
/*      */                 else
/*      */                 {
/* 1524 */                   x[1] = x[2];
/* 1525 */                   x[2] = x[3];
/* 1526 */                   x[3] = Long.valueOf(now);
/*      */                   
/* 1528 */                   long t = ((Long)x[1]).longValue();
/*      */                   
/* 1530 */                   if (now - t < 10000L)
/*      */                   {
/* 1532 */                     WebPlugin.this.log("Too many recent authentication failures from '" + client_address + "' - rate limiting");
/*      */                     
/* 1534 */                     x[2] = Long.valueOf(now + 10000L);
/*      */                     
/*      */ 
/* 1537 */                     this.last_pw = "";
/* 1538 */                     waiter = (AESemaphore)x[0];
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/* 1543 */               if (waiter != null)
/*      */               {
/* 1545 */                 waiter.reserve(10000L);
/*      */ 
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 1556 */             synchronized (this.fail_map)
/*      */             {
/* 1558 */               this.fail_map.remove(client_address);
/*      */             }
/*      */             
/* 1561 */             String cookies = getHeaderField(headers, "Cookie");
/*      */             
/* 1563 */             if (WebPlugin.this.pairing_session_code != null)
/*      */             {
/* 1565 */               if ((cookies == null) || (!cookies.contains(WebPlugin.this.pairing_session_code)))
/*      */               {
/* 1567 */                 WebPlugin.tls.set(WebPlugin.this.pairing_session_code);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1572 */           WebPlugin.this.recordAuthRequest(client_address, result);
/*      */           
/* 1574 */           if (!result) {}
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
/* 1588 */           return result;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         private boolean authenticateSupport(String headers, URL resource, String user, String pw)
/*      */         {
/* 1600 */           boolean auto_auth = (WebPlugin.this.param_auto_auth != null) && (WebPlugin.this.param_auto_auth.getValue());
/*      */           boolean result;
/* 1602 */           boolean result; if (!WebPlugin.this.pw_enable.getValue())
/*      */           {
/* 1604 */             result = true;
/*      */           }
/*      */           else
/*      */           {
/* 1608 */             if (auto_auth)
/*      */             {
/* 1610 */               user = user.trim().toLowerCase();
/*      */               
/* 1612 */               pw = pw.toUpperCase();
/*      */             }
/*      */             boolean result;
/* 1615 */             if (!user.equals(WebPlugin.this.p_user_name.getValue()))
/*      */             {
/* 1617 */               result = false;
/*      */             }
/*      */             else
/*      */             {
/* 1621 */               byte[] hash = this.last_hash;
/*      */               
/* 1623 */               if (!this.last_pw.equals(pw))
/*      */               {
/* 1625 */                 hash = WebPlugin.this.plugin_interface.getUtilities().getSecurityManager().calculateSHA1(auto_auth ? pw.toUpperCase().getBytes() : pw.getBytes());
/*      */                 
/*      */ 
/* 1628 */                 this.last_pw = pw;
/* 1629 */                 this.last_hash = hash;
/*      */               }
/*      */               
/* 1632 */               result = Arrays.equals(hash, WebPlugin.this.p_password.getValue());
/*      */             }
/*      */           }
/*      */           
/* 1636 */           if (result)
/*      */           {
/*      */ 
/*      */ 
/* 1640 */             checkCookieSet(headers, resource);
/*      */           }
/* 1642 */           else if (auto_auth)
/*      */           {
/*      */ 
/*      */ 
/* 1646 */             int x = checkCookieSet(headers, resource);
/*      */             
/* 1648 */             if (x == 1)
/*      */             {
/* 1650 */               result = true;
/*      */             }
/* 1652 */             else if (x == 0)
/*      */             {
/* 1654 */               result = WebPlugin.this.hasOurCookie(getHeaderField(headers, "Cookie"));
/*      */             }
/*      */           }
/*      */           else {
/* 1658 */             result = WebPlugin.this.hasOurCookie(getHeaderField(headers, "Cookie"));
/*      */           }
/*      */           
/* 1661 */           return result;
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
/*      */         private int checkCookieSet(String headers, URL resource)
/*      */         {
/* 1676 */           if (WebPlugin.this.pairing_access_code == null)
/*      */           {
/* 1678 */             return 2;
/*      */           }
/*      */           
/* 1681 */           String[] locations = { resource.getQuery(), getHeaderField(headers, "Referer") };
/*      */           
/* 1683 */           for (String location : locations)
/*      */           {
/* 1685 */             if (location != null)
/*      */             {
/* 1687 */               boolean skip_fail = false;
/* 1688 */               int param_len = 0;
/*      */               
/* 1690 */               int p1 = location.indexOf("vuze_pairing_ac=");
/*      */               
/* 1692 */               if (p1 == -1)
/*      */               {
/* 1694 */                 p1 = location.indexOf("ac=");
/*      */                 
/* 1696 */                 if (p1 != -1)
/*      */                 {
/* 1698 */                   param_len = 3;
/*      */                   
/* 1700 */                   skip_fail = true;
/*      */                 }
/*      */               }
/*      */               else {
/* 1704 */                 param_len = 16;
/*      */               }
/*      */               
/* 1707 */               if (p1 != -1)
/*      */               {
/* 1709 */                 int p2 = location.indexOf('&', p1);
/*      */                 
/* 1711 */                 String ac = location.substring(p1 + param_len, p2 == -1 ? location.length() : p2).trim();
/*      */                 
/* 1713 */                 p2 = ac.indexOf('#');
/*      */                 
/* 1715 */                 if (p2 != -1)
/*      */                 {
/* 1717 */                   ac = ac.substring(0, p2);
/*      */                 }
/*      */                 
/* 1720 */                 if (ac.equalsIgnoreCase(WebPlugin.this.pairing_access_code))
/*      */                 {
/* 1722 */                   WebPlugin.tls.set(WebPlugin.this.pairing_session_code);
/*      */                   
/* 1724 */                   return 1;
/*      */                 }
/*      */                 
/*      */ 
/* 1728 */                 if (!skip_fail)
/*      */                 {
/* 1730 */                   return 2;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1737 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         private String getHeaderField(String headers, String field)
/*      */         {
/* 1745 */           String lc_headers = headers.toLowerCase();
/*      */           
/* 1747 */           int p1 = lc_headers.indexOf(field.toLowerCase() + ":");
/*      */           
/* 1749 */           if (p1 != -1)
/*      */           {
/* 1751 */             int p2 = lc_headers.indexOf('\n', p1);
/*      */             
/* 1753 */             if (p2 != -1)
/*      */             {
/* 1755 */               return headers.substring(p1 + field.length() + 1, p2).trim();
/*      */             }
/*      */           }
/*      */           
/* 1759 */           return null;
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (TrackerException e)
/*      */     {
/* 1765 */       this.log.log("Server initialisation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean hasOurCookie(String cookies)
/*      */   {
/* 1773 */     if (cookies == null)
/*      */     {
/* 1775 */       return false;
/*      */     }
/*      */     
/* 1778 */     String[] cookie_list = cookies.split(";");
/*      */     
/* 1780 */     for (String cookie : cookie_list)
/*      */     {
/* 1782 */       String[] bits = cookie.split("=");
/*      */       
/* 1784 */       if (bits.length == 2)
/*      */       {
/* 1786 */         if (bits[0].trim().equals("vuze_pairing_sc"))
/*      */         {
/* 1788 */           if (bits[1].trim().equals(this.pairing_session_code))
/*      */           {
/* 1790 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1796 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean sameAddress(InetAddress a1, InetAddress a2)
/*      */   {
/* 1804 */     if ((a1 == null) && (a2 == null))
/*      */     {
/* 1806 */       return true;
/*      */     }
/* 1808 */     if ((a1 == null) || (a2 == null))
/*      */     {
/* 1810 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1814 */     return a1.equals(a2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setupUPnP()
/*      */   {
/* 1821 */     if ((!this.plugin_enabled) || (!this.p_upnp_enable.getValue()))
/*      */     {
/* 1823 */       if (this.upnp_mapping != null)
/*      */       {
/* 1825 */         log("Removing UPnP mapping");
/*      */         
/* 1827 */         this.upnp_mapping.destroy();
/*      */         
/* 1829 */         this.upnp_mapping = null;
/*      */       }
/*      */       
/* 1832 */       return;
/*      */     }
/*      */     
/* 1835 */     PluginInterface pi_upnp = this.plugin_interface.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*      */     
/* 1837 */     if (pi_upnp == null)
/*      */     {
/* 1839 */       this.log.log("No UPnP plugin available, not attempting port mapping");
/*      */     }
/*      */     else
/*      */     {
/* 1843 */       int port = this.param_port.getValue();
/*      */       
/* 1845 */       if (this.upnp_mapping != null)
/*      */       {
/* 1847 */         if (this.upnp_mapping.getPort() == port)
/*      */         {
/* 1849 */           return;
/*      */         }
/*      */         
/* 1852 */         log("Updating UPnP mapping");
/*      */         
/* 1854 */         this.upnp_mapping.destroy();
/*      */       }
/*      */       else
/*      */       {
/* 1858 */         log("Creating UPnP mapping");
/*      */       }
/*      */       
/* 1861 */       this.upnp_mapping = ((UPnPPlugin)pi_upnp.getPlugin()).addMapping(this.plugin_interface.getPluginName(), true, port, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setupPairing(String sid, boolean pairing_enabled)
/*      */   {
/* 1870 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*      */     
/* 1872 */     PairedService service = pm.getService(sid);
/*      */     
/* 1874 */     if ((this.plugin_enabled) && (pairing_enabled) && (pm.isEnabled()))
/*      */     {
/* 1876 */       setupAutoAuth();
/*      */       
/* 1878 */       if (service == null)
/*      */       {
/* 1880 */         log("Adding pairing service");
/*      */         
/* 1882 */         service = pm.addService(sid, new com.aelitis.azureus.core.pairing.PairedServiceRequestHandler()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public byte[] handleRequest(InetAddress originator, String endpoint_url, byte[] request)
/*      */             throws IOException
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1895 */             return WebPlugin.this.handleTunnelRequest(originator, endpoint_url, request);
/*      */           }
/*      */           
/* 1898 */         });
/* 1899 */         PairingConnectionData cd = service.getConnectionData();
/*      */         try
/*      */         {
/* 1902 */           updatePairing(cd);
/*      */         }
/*      */         finally
/*      */         {
/* 1906 */           cd.sync();
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1911 */       this.pairing_access_code = null;
/*      */       
/* 1913 */       setupSessionCode(null);
/*      */       
/* 1915 */       if (service != null)
/*      */       {
/* 1917 */         log("Removing pairing service");
/*      */         
/* 1919 */         service.remove();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setupSessionCode(String key)
/*      */   {
/* 1928 */     if (key == null)
/*      */     {
/* 1930 */       key = Base32.encode(this.p_user_name.getValue().getBytes()) + Base32.encode(this.p_password.getValue());
/*      */     }
/*      */     
/* 1933 */     synchronized (this)
/*      */     {
/* 1935 */       String existing_key = this.plugin_config.getPluginStringParameter("Pairing Session Key", "");
/*      */       
/* 1937 */       String[] bits = existing_key.split("=");
/*      */       
/* 1939 */       if ((bits.length == 2) && (bits[0].equals(key)))
/*      */       {
/* 1941 */         this.pairing_session_code = bits[1];
/*      */       }
/*      */       else
/*      */       {
/* 1945 */         this.pairing_session_code = Base32.encode(org.gudy.azureus2.core3.util.RandomUtils.nextSecureHash());
/*      */         
/* 1947 */         this.plugin_config.setPluginParameter("Pairing Session Key", key + "=" + this.pairing_session_code);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setupAutoAuth()
/*      */   {
/* 1955 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*      */     
/* 1957 */     String ac = pm.peekAccessCode();
/*      */     
/* 1959 */     this.pairing_access_code = ac;
/*      */     
/*      */ 
/*      */ 
/* 1963 */     if ((this.pairing_access_code != null) && (this.param_auto_auth.getValue()))
/*      */     {
/* 1965 */       setupSessionCode(ac);
/*      */       try
/*      */       {
/* 1968 */         this.setting_auto_auth = true;
/*      */         
/* 1970 */         if (!this.p_user_name.getValue().equals("vuze"))
/*      */         {
/* 1972 */           this.p_user_name.setValue("vuze");
/*      */         }
/*      */         
/* 1975 */         SHA1Hasher hasher = new SHA1Hasher();
/*      */         
/* 1977 */         byte[] encoded = hasher.calculateHash(this.pairing_access_code.getBytes());
/*      */         
/* 1979 */         if (!Arrays.equals(this.p_password.getValue(), encoded))
/*      */         {
/* 1981 */           this.p_password.setValue(this.pairing_access_code);
/*      */         }
/*      */         
/* 1984 */         if (!this.pw_enable.getValue())
/*      */         {
/* 1986 */           this.pw_enable.setValue(true);
/*      */         }
/*      */       }
/*      */       finally {
/* 1990 */         this.setting_auto_auth = false;
/*      */       }
/*      */     }
/*      */     else {
/* 1994 */       setupSessionCode(null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updatePairing(String sid)
/*      */   {
/* 2002 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*      */     
/* 2004 */     PairedService service = pm.getService(sid);
/*      */     
/* 2006 */     if (service != null)
/*      */     {
/* 2008 */       PairingConnectionData cd = service.getConnectionData();
/*      */       
/* 2010 */       log("Updating pairing information");
/*      */       try
/*      */       {
/* 2013 */         updatePairing(cd);
/*      */       }
/*      */       finally
/*      */       {
/* 2017 */         cd.sync();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updatePairing(PairingConnectionData cd)
/*      */   {
/* 2026 */     cd.setAttribute("port", String.valueOf(this.param_port.getValue()));
/*      */     
/* 2028 */     int override = this.param_port_or == null ? 0 : this.param_port_or.getValue();
/*      */     
/* 2030 */     if (override > 0)
/*      */     {
/* 2032 */       cd.setAttribute("port_or", String.valueOf(override));
/*      */     }
/*      */     else
/*      */     {
/* 2036 */       cd.setAttribute("port_or", null);
/*      */     }
/*      */     
/* 2039 */     cd.setAttribute("protocol", this.param_protocol.getValue());
/*      */     
/* 2041 */     if (this.param_i2p_dest.isVisible())
/*      */     {
/* 2043 */       String host = this.param_i2p_dest.getValue();
/*      */       
/* 2045 */       if (host.length() > 0)
/*      */       {
/* 2047 */         cd.setAttribute("I2P", host);
/*      */       }
/*      */     }
/*      */     
/* 2051 */     if (this.param_tor_dest.isVisible())
/*      */     {
/* 2053 */       String host = this.param_tor_dest.getValue();
/*      */       
/* 2055 */       if (host.length() > 0)
/*      */       {
/* 2057 */         cd.setAttribute("Tor", host);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public InetAddress getServerBindIP()
/*      */   {
/* 2065 */     if (this.tracker_context == null)
/*      */     {
/* 2067 */       return new InetSocketAddress(0).getAddress();
/*      */     }
/*      */     
/* 2070 */     InetAddress address = this.tracker_context.getBindIP();
/*      */     
/* 2072 */     if (address == null)
/*      */     {
/* 2074 */       return new InetSocketAddress(0).getAddress();
/*      */     }
/*      */     
/* 2077 */     return address;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getServerPort()
/*      */   {
/* 2083 */     if (this.tracker_context == null)
/*      */     {
/* 2085 */       return 0;
/*      */     }
/*      */     
/* 2088 */     URL url = this.tracker_context.getURLs()[0];
/*      */     
/* 2090 */     return url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/* 2096 */     return this.param_port.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProtocol()
/*      */   {
/* 2102 */     return this.param_protocol.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUserAndPassword(String user, String password)
/*      */   {
/* 2110 */     this.p_user_name.setValue(user);
/* 2111 */     this.p_password.setValue(password);
/* 2112 */     this.pw_enable.setValue(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public void unsetUserAndPassword()
/*      */   {
/* 2118 */     this.pw_enable.setValue(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void recordAuthRequest(String client_ip, boolean good)
/*      */   {
/* 2126 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*      */     
/* 2128 */     pm.recordRequest(this.plugin_interface.getPluginName(), client_ip, good);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void recordRequest(TrackerWebPageRequest request, boolean good, boolean is_tunnel)
/*      */   {
/* 2137 */     PairingManager pm = PairingManagerFactory.getSingleton();
/*      */     
/* 2139 */     String str = request.getClientAddress();
/*      */     
/* 2141 */     if (is_tunnel)
/*      */     {
/* 2143 */       str = "Tunnel (" + str + ")";
/*      */     }
/*      */     
/* 2146 */     pm.recordRequest(this.plugin_interface.getPluginName(), str, good);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean generateSupport(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*      */     throws IOException
/*      */   {
/* 2156 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private byte[] handleTunnelRequest(final InetAddress originator, String endpoint_url, final byte[] request_bytes)
/*      */     throws IOException
/*      */   {
/* 2167 */     int q_pos = endpoint_url.indexOf('?');
/*      */     
/* 2169 */     boolean raw = true;
/*      */     
/* 2171 */     if (q_pos != -1)
/*      */     {
/* 2173 */       String params = endpoint_url.substring(q_pos + 1);
/*      */       
/* 2175 */       String[] args = params.split("&");
/*      */       
/* 2177 */       String new_endpoint = endpoint_url.substring(0, q_pos);
/*      */       
/* 2179 */       String sep = "?";
/*      */       
/* 2181 */       for (String arg : args)
/*      */       {
/* 2183 */         if (arg.startsWith("tunnel_format="))
/*      */         {
/* 2185 */           String temp = arg.substring(14);
/*      */           
/* 2187 */           if (temp.startsWith("h"))
/*      */           {
/* 2189 */             raw = false;
/*      */           }
/*      */         }
/*      */         else {
/* 2193 */           new_endpoint = new_endpoint + sep + arg;
/*      */           
/* 2195 */           sep = "&";
/*      */         }
/*      */       }
/*      */       
/* 2199 */       endpoint_url = new_endpoint;
/*      */     }
/*      */     
/* 2202 */     final String f_endpoint_url = endpoint_url;
/* 2203 */     final JSONObject request_headers = new JSONObject();
/*      */     
/*      */     int data_start;
/*      */     final int data_start;
/* 2207 */     if (raw)
/*      */     {
/* 2209 */       data_start = 0;
/*      */     }
/*      */     else {
/* 2212 */       int request_header_len = request_bytes[0] << 8 & 0xFF00 | request_bytes[1] & 0xFF;
/*      */       
/* 2214 */       String reply_json_str = new String(request_bytes, 2, request_header_len, "UTF-8");
/*      */       
/* 2216 */       request_headers.putAll(JSONUtils.decodeJSON(reply_json_str));
/*      */       
/* 2218 */       data_start = request_header_len + 2;
/*      */     }
/*      */     
/* 2221 */     TrackerWebPageRequest request = new TrackerWebPageRequest()
/*      */     {
/*      */ 
/*      */       public Tracker getTracker()
/*      */       {
/*      */ 
/* 2227 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getClientAddress()
/*      */       {
/* 2233 */         return originator.getHostAddress();
/*      */       }
/*      */       
/*      */ 
/*      */       public InetSocketAddress getClientAddress2()
/*      */       {
/* 2239 */         return new InetSocketAddress(originator, 0);
/*      */       }
/*      */       
/*      */ 
/*      */       public InetSocketAddress getLocalAddress()
/*      */       {
/* 2245 */         return new InetSocketAddress("127.0.0.1", 0);
/*      */       }
/*      */       
/*      */ 
/*      */       public String getUser()
/*      */       {
/* 2251 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getURL()
/*      */       {
/* 2257 */         String url = (String)request_headers.get("HTTP-URL");
/*      */         
/* 2259 */         if (url != null)
/*      */         {
/* 2261 */           return url;
/*      */         }
/*      */         
/* 2264 */         return f_endpoint_url;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getHeader()
/*      */       {
/* 2270 */         return "";
/*      */       }
/*      */       
/*      */ 
/*      */       public Map getHeaders()
/*      */       {
/* 2276 */         return request_headers;
/*      */       }
/*      */       
/*      */ 
/*      */       public InputStream getInputStream()
/*      */       {
/* 2282 */         return new java.io.ByteArrayInputStream(request_bytes, data_start, request_bytes.length - data_start);
/*      */       }
/*      */       
/*      */       public URL getAbsoluteURL()
/*      */       {
/*      */         try
/*      */         {
/* 2289 */           return new URL("http://127.0.0.1" + getURL());
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/* 2293 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public TrackerWebContext getContext()
/*      */       {
/* 2300 */         return null;
/*      */       }
/* 2302 */     };
/* 2303 */     final ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */     
/* 2305 */     final Map reply_headers = new HashMap();
/*      */     
/* 2307 */     TrackerWebPageResponse response = new TrackerWebPageResponse()
/*      */     {
/*      */ 
/*      */       public OutputStream getOutputStream()
/*      */       {
/*      */ 
/* 2313 */         return baos;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setReplyStatus(int status)
/*      */       {
/* 2320 */         reply_headers.put("HTTP-Status", String.valueOf(status));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setContentType(String type)
/*      */       {
/* 2327 */         reply_headers.put("Content-Type", type);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void setLastModified(long time) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void setExpires(long time) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void setHeader(String name, String value)
/*      */       {
/* 2347 */         reply_headers.put(name, value);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void setGZIP(boolean gzip) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean useFile(String root_dir, String relative_url)
/*      */         throws IOException
/*      */       {
/* 2363 */         Debug.out("Not supported");
/*      */         
/* 2365 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void useStream(String file_type, InputStream stream)
/*      */         throws IOException
/*      */       {
/* 2375 */         Debug.out("Not supported");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void writeTorrent(TrackerTorrent torrent)
/*      */         throws IOException
/*      */       {
/* 2384 */         Debug.out("Not supported");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void setAsynchronous(boolean async)
/*      */         throws IOException
/*      */       {
/* 2393 */         Debug.out("Not supported");
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean getAsynchronous()
/*      */       {
/* 2399 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public OutputStream getRawOutputStream()
/*      */         throws IOException
/*      */       {
/* 2407 */         Debug.out("Not supported");
/*      */         
/* 2409 */         throw new IOException("Not supported");
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isActive()
/*      */       {
/* 2415 */         return true;
/*      */       }
/*      */     };
/*      */     try
/*      */     {
/*      */       byte[] bytes;
/*      */       byte[] bytes;
/* 2422 */       if (generate2(request, response, true))
/*      */       {
/* 2424 */         bytes = baos.toByteArray();
/*      */       }
/*      */       else
/*      */       {
/* 2428 */         Debug.out("Tunnelled request not handled: " + request.getURL());
/*      */         
/* 2430 */         response.setReplyStatus(404);
/*      */         
/* 2432 */         bytes = new byte[0];
/*      */       }
/*      */       
/* 2435 */       if (raw)
/*      */       {
/* 2437 */         return bytes;
/*      */       }
/*      */       
/*      */ 
/* 2441 */       String accept_encoding = (String)request_headers.get("Accept-Encoding");
/*      */       
/* 2443 */       if ((accept_encoding != null) && (accept_encoding.contains("gzip")))
/*      */       {
/* 2445 */         reply_headers.put("Content-Encoding", "gzip");
/*      */         
/* 2447 */         ByteArrayOutputStream temp = new ByteArrayOutputStream(bytes.length + 512);
/*      */         
/* 2449 */         GZIPOutputStream gos = new GZIPOutputStream(temp);
/*      */         
/* 2451 */         gos.write(bytes);
/*      */         
/* 2453 */         gos.close();
/*      */         
/* 2455 */         bytes = temp.toByteArray();
/*      */       }
/*      */       
/* 2458 */       ByteArrayOutputStream baos2 = new ByteArrayOutputStream(bytes.length + 512);
/*      */       
/* 2460 */       String header_json = JSONUtils.encodeToJSON(reply_headers);
/*      */       
/* 2462 */       byte[] header_bytes = header_json.getBytes("UTF-8");
/*      */       
/* 2464 */       int header_len = header_bytes.length;
/*      */       
/* 2466 */       byte[] header_len_bytes = { (byte)(header_len >> 8), (byte)header_len };
/*      */       
/* 2468 */       baos2.write(header_len_bytes);
/* 2469 */       baos2.write(header_bytes);
/* 2470 */       baos2.write(bytes);
/*      */       
/* 2472 */       return baos2.toByteArray();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2476 */       Debug.out(e);
/*      */     }
/* 2478 */     return new byte[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean generate(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*      */     throws IOException
/*      */   {
/* 2489 */     String url = request.getURL();
/*      */     
/* 2491 */     if (url.startsWith("/pairing/tunnel/"))
/*      */     {
/* 2493 */       long error_code = 1L;
/*      */       try
/*      */       {
/* 2496 */         PairingManager pm = PairingManagerFactory.getSingleton();
/*      */         
/* 2498 */         if (pm.isEnabled())
/*      */         {
/* 2500 */           if (pm.isSRPEnabled())
/*      */           {
/* 2502 */             return pm.handleLocalTunnel(request, response);
/*      */           }
/*      */           
/*      */ 
/* 2506 */           error_code = 5L;
/*      */           
/* 2508 */           throw new IOException("Secure pairing is not enabled");
/*      */         }
/*      */         
/*      */ 
/* 2512 */         error_code = 5L;
/*      */         
/* 2514 */         throw new IOException("Pairing is not enabled");
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2518 */         JSONObject json = new JSONObject();
/*      */         
/* 2520 */         JSONObject error = new JSONObject();
/*      */         
/* 2522 */         json.put("error", error);
/*      */         
/* 2524 */         error.put("msg", Debug.getNestedExceptionMessage(e));
/* 2525 */         error.put("code", Long.valueOf(error_code));
/*      */         
/* 2527 */         return returnJSON(response, JSONUtils.encodeToJSON(json));
/*      */       }
/*      */     }
/*      */     
/* 2531 */     return generate2(request, response, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean generate2(TrackerWebPageRequest request, TrackerWebPageResponse response, boolean is_tunnel)
/*      */     throws IOException
/*      */   {
/* 2544 */     String client = request.getClientAddress();
/*      */     
/* 2546 */     if (!this.ip_range_all)
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/* 2551 */         boolean valid_ip = true;
/*      */         
/* 2553 */         InetAddress client_ia = InetAddress.getByName(client);
/*      */         
/* 2555 */         if (this.ip_ranges == null)
/*      */         {
/* 2557 */           if (!client_ia.isLoopbackAddress())
/*      */           {
/* 2559 */             InetAddress bind_ia = getServerBindIP();
/*      */             
/* 2561 */             if ((bind_ia.isAnyLocalAddress()) || (!bind_ia.equals(client_ia)))
/*      */             {
/* 2563 */               this.log.log(3, "Client '" + client + "' is not local, rejecting");
/*      */               
/* 2565 */               valid_ip = false;
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 2570 */           boolean ok = false;
/*      */           
/* 2572 */           for (IPRange range : this.ip_ranges)
/*      */           {
/* 2574 */             if (range.isInRange(client_ia.getHostAddress()))
/*      */             {
/* 2576 */               ok = true;
/*      */             }
/*      */           }
/*      */           
/* 2580 */           if (!ok)
/*      */           {
/* 2582 */             this.log.log(3, "Client '" + client + "' (" + client_ia.getHostAddress() + ") is not in range, rejecting");
/*      */             
/* 2584 */             valid_ip = false;
/*      */           }
/*      */         }
/*      */         
/* 2588 */         if (!valid_ip)
/*      */         {
/* 2590 */           response.setReplyStatus(403);
/*      */           
/* 2592 */           recordRequest(request, false, is_tunnel);
/*      */           
/* 2594 */           return returnTextPlain(response, "Cannot access resource from this IP address.");
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2599 */         Debug.printStackTrace(e);
/*      */         
/* 2601 */         recordRequest(request, false, is_tunnel);
/*      */         
/* 2603 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 2607 */     recordRequest(request, true, is_tunnel);
/*      */     
/* 2609 */     String url = request.getURL();
/*      */     
/* 2611 */     if (url.toString().endsWith(".class"))
/*      */     {
/* 2613 */       System.out.println("WebPlugin::generate:" + url);
/*      */     }
/*      */     
/* 2616 */     String cookie_to_set = (String)tls.get();
/*      */     
/* 2618 */     if (cookie_to_set == "<grace_period>")
/*      */     {
/* 2620 */       return returnTextPlain(response, "Logout in progress, please try again later.");
/*      */     }
/*      */     
/* 2623 */     if (cookie_to_set != null)
/*      */     {
/*      */ 
/*      */ 
/* 2627 */       response.setHeader("Set-Cookie", "vuze_pairing_sc=" + cookie_to_set + "; path=/; HttpOnly");
/*      */       
/* 2629 */       tls.set(null);
/*      */     }
/*      */     
/* 2632 */     URL full_url = request.getAbsoluteURL();
/*      */     
/* 2634 */     String full_url_path = full_url.getPath();
/*      */     
/* 2636 */     if (full_url_path.equals("/isPairedServiceAvailable"))
/*      */     {
/* 2638 */       String redirect = getArgumentFromURL(full_url, "redirect_to");
/*      */       
/* 2640 */       if (redirect != null) {
/*      */         try
/*      */         {
/* 2643 */           URL target = new URL(redirect);
/*      */           
/* 2645 */           String host = target.getHost();
/*      */           
/* 2647 */           if (!Constants.isAzureusDomain(host))
/*      */           {
/* 2649 */             if (!InetAddress.getByName(host).isLoopbackAddress())
/*      */             {
/* 2651 */               log("Invalid redirect host: " + host);
/*      */               
/* 2653 */               redirect = null;
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 2658 */           Debug.out(e);
/*      */           
/* 2660 */           redirect = null;
/*      */         }
/*      */       }
/*      */       
/* 2664 */       if (redirect != null)
/*      */       {
/* 2666 */         response.setReplyStatus(302);
/*      */         
/* 2668 */         response.setHeader("Location", redirect);
/*      */         
/* 2670 */         return true;
/*      */       }
/*      */       
/* 2673 */       String callback = getArgumentFromURL(full_url, "jsoncallback");
/*      */       
/* 2675 */       if (callback != null)
/*      */       {
/* 2677 */         return returnTextPlain(response, callback + "( {'pairedserviceavailable':true} )"); }
/*      */     } else {
/* 2679 */       if (full_url_path.equals("/isServicePaired"))
/*      */       {
/* 2681 */         boolean paired = (cookie_to_set != null) || (hasOurCookie((String)request.getHeaders().get("cookie")));
/*      */         
/*      */ 
/*      */ 
/* 2685 */         return returnTextPlain(response, "{ 'servicepaired': " + (paired ? "true" : "false") + " }");
/*      */       }
/* 2687 */       if (full_url_path.equals("/pairedServiceLogout"))
/*      */       {
/* 2689 */         synchronized (this.logout_timer)
/*      */         {
/* 2691 */           this.logout_timer.put(client, Long.valueOf(SystemTime.getMonotonousTime()));
/*      */         }
/*      */         
/* 2694 */         response.setHeader("Set-Cookie", "vuze_pairing_sc=<deleted>, expires=" + org.gudy.azureus2.core3.util.TimeFormatter.getCookieDate(0L));
/*      */         
/* 2696 */         String redirect = getArgumentFromURL(full_url, "redirect_to");
/*      */         
/* 2698 */         if (redirect != null) {
/*      */           try
/*      */           {
/* 2701 */             URL target = new URL(redirect);
/*      */             
/* 2703 */             String host = target.getHost();
/*      */             
/* 2705 */             if (!Constants.isAzureusDomain(host))
/*      */             {
/* 2707 */               if (!InetAddress.getByName(host).isLoopbackAddress())
/*      */               {
/* 2709 */                 log("Invalid redirect host: " + host);
/*      */                 
/* 2711 */                 redirect = null;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 2716 */             Debug.out(e);
/*      */             
/* 2718 */             redirect = null;
/*      */           }
/*      */         }
/* 2721 */         if (redirect == null)
/*      */         {
/* 2723 */           return returnTextPlain(response, "");
/*      */         }
/*      */         
/*      */ 
/* 2727 */         response.setReplyStatus(302);
/*      */         
/* 2729 */         response.setHeader("Location", redirect);
/*      */         
/* 2731 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 2735 */     request.getHeaders().put("x-vuze-is-tunnel", is_tunnel ? "true" : "false");
/*      */     
/* 2737 */     if (generateSupport(request, response))
/*      */     {
/* 2739 */       return true;
/*      */     }
/*      */     
/* 2742 */     if (is_tunnel)
/*      */     {
/* 2744 */       return false;
/*      */     }
/*      */     
/* 2747 */     if ((url.equals("/")) || (url.startsWith("/?")))
/*      */     {
/* 2749 */       url = "/";
/*      */       
/* 2751 */       if (this.home_page != null)
/*      */       {
/* 2753 */         url = this.home_page;
/*      */       }
/*      */       else
/*      */       {
/* 2757 */         for (int i = 0; i < welcome_files.length; i++)
/*      */         {
/* 2759 */           if (welcome_files[i].exists())
/*      */           {
/* 2761 */             url = "/" + welcome_pages[i];
/*      */             
/* 2763 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2771 */     if (useFile(request, response, this.file_root, UrlUtils.decode(url)))
/*      */     {
/* 2773 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2778 */     String resource_name = url;
/*      */     
/* 2780 */     if (resource_name.startsWith("/"))
/*      */     {
/* 2782 */       resource_name = resource_name.substring(1);
/*      */     }
/*      */     
/* 2785 */     int pos = resource_name.lastIndexOf(".");
/*      */     
/* 2787 */     if (pos != -1)
/*      */     {
/* 2789 */       String type = resource_name.substring(pos + 1);
/*      */       
/* 2791 */       ClassLoader cl = this.plugin_interface.getPluginClassLoader();
/*      */       
/* 2793 */       InputStream is = cl.getResourceAsStream(resource_name);
/*      */       
/* 2795 */       if (is == null)
/*      */       {
/*      */ 
/*      */ 
/* 2799 */         if (this.resource_root != null)
/*      */         {
/* 2801 */           resource_name = this.resource_root + "/" + resource_name;
/*      */           
/* 2803 */           is = cl.getResourceAsStream(resource_name);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2809 */       if (is != null)
/*      */       {
/*      */         try {
/* 2812 */           response.useStream(type, is);
/*      */         }
/*      */         finally
/*      */         {
/* 2816 */           is.close();
/*      */         }
/*      */         
/* 2819 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 2823 */     return false;
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
/*      */ 
/*      */ 
/*      */   protected boolean useFile(TrackerWebPageRequest request, TrackerWebPageResponse response, String root, String relative_url)
/*      */     throws IOException
/*      */   {
/* 2845 */     return response.useFile(this.file_root, relative_url);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String getArgumentFromURL(URL url, String argument)
/*      */   {
/* 2853 */     String query = url.getQuery();
/*      */     
/* 2855 */     if (query != null)
/*      */     {
/* 2857 */       String[] args = query.split("&");
/*      */       
/* 2859 */       for (String arg : args)
/*      */       {
/* 2861 */         String[] x = arg.split("=");
/*      */         
/* 2863 */         if (x.length == 2)
/*      */         {
/* 2865 */           if (x[0].equals(argument))
/*      */           {
/* 2867 */             return UrlUtils.decode(x[1]);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2873 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean returnTextPlain(TrackerWebPageResponse response, String str)
/*      */   {
/* 2881 */     return returnStuff(response, "text/plain", str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean returnJSON(TrackerWebPageResponse response, String str)
/*      */     throws IOException
/*      */   {
/* 2891 */     response.setContentType("application/json; charset=UTF-8");
/*      */     
/* 2893 */     OutputStream os = response.getOutputStream();
/*      */     
/* 2895 */     os.write(str.getBytes("UTF-8"));
/*      */     
/* 2897 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean returnStuff(TrackerWebPageResponse response, String content_type, String str)
/*      */   {
/* 2906 */     response.setContentType(content_type);
/*      */     
/* 2908 */     PrintWriter pw = new PrintWriter(response.getOutputStream());
/*      */     
/* 2910 */     pw.println(str);
/*      */     
/* 2912 */     pw.flush();
/*      */     
/* 2914 */     pw.close();
/*      */     
/* 2916 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   protected BasicPluginConfigModel getConfigModel()
/*      */   {
/* 2922 */     return this.config_model;
/*      */   }
/*      */   
/*      */   protected BasicPluginViewModel getViewModel() {
/* 2926 */     return this.view_model;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 2933 */     this.log.log(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 2941 */     this.log.log(str, e);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/webplugin/WebPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */