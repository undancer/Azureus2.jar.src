/*      */ package com.aelitis.azureus.plugins.upnp;
/*      */ 
/*      */ import com.aelitis.net.natpmp.NatPMPDeviceFactory;
/*      */ import com.aelitis.net.natpmp.upnp.NatPMPUPnP;
/*      */ import com.aelitis.net.upnp.UPnP;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import com.aelitis.net.upnp.UPnPException;
/*      */ import com.aelitis.net.upnp.UPnPFactory;
/*      */ import com.aelitis.net.upnp.UPnPListener;
/*      */ import com.aelitis.net.upnp.UPnPLogListener;
/*      */ import com.aelitis.net.upnp.UPnPRootDevice;
/*      */ import com.aelitis.net.upnp.UPnPService;
/*      */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*      */ import com.aelitis.net.upnp.services.UPnPWANConnectionListener;
/*      */ import com.aelitis.net.upnp.services.UPnPWANConnectionPortMapping;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.logging.Logger;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.network.ConnectionManager;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*      */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.LabelParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.Formatters;
/*      */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.plugins.utils.security.SESecurityManager;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*      */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentFactory;
/*      */ 
/*      */ public class UPnPPlugin implements org.gudy.azureus2.plugins.Plugin, UPnPListener, UPnPMappingListener, UPnPWANConnectionListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final String UPNP_PLUGIN_CONFIGSECTION_ID = "UPnP";
/*      */   private static final String NATPMP_PLUGIN_CONFIGSECTION_ID = "NATPMP";
/*      */   private static final String STATS_DISCOVER = "discover";
/*      */   private static final String STATS_FOUND = "found";
/*      */   private static final String STATS_READ_OK = "read_ok";
/*      */   private static final String STATS_READ_BAD = "read_bad";
/*      */   private static final String STATS_MAP_OK = "map_ok";
/*      */   private static final String STATS_MAP_BAD = "map_bad";
/*   72 */   private static final String[] STATS_KEYS = { "discover", "found", "read_ok", "read_bad", "map_ok", "map_bad" };
/*      */   
/*      */   private PluginInterface plugin_interface;
/*      */   
/*      */   private LoggerChannel log;
/*   77 */   private UPnPMappingManager mapping_manager = UPnPMappingManager.getSingleton(this);
/*      */   
/*      */   private UPnP upnp;
/*      */   
/*      */   private UPnPLogListener upnp_log_listener;
/*      */   
/*      */   private NatPMPUPnP nat_pmp_upnp;
/*      */   
/*      */   private BooleanParameter natpmp_enable_param;
/*      */   
/*      */   private StringParameter nat_pmp_router;
/*      */   
/*      */   private BooleanParameter upnp_enable_param;
/*      */   
/*      */   private BooleanParameter trace_to_log;
/*      */   private StringParameter desc_prefix_param;
/*      */   private BooleanParameter alert_success_param;
/*      */   private BooleanParameter grab_ports_param;
/*      */   private BooleanParameter alert_other_port_param;
/*      */   private BooleanParameter alert_device_probs_param;
/*      */   private BooleanParameter release_mappings_param;
/*      */   private StringParameter selected_interfaces_param;
/*      */   private StringParameter selected_addresses_param;
/*      */   private BooleanParameter ignore_bad_devices;
/*      */   private LabelParameter ignored_devices_list;
/*  102 */   private List<UPnPMapping> mappings = new ArrayList();
/*  103 */   private List<UPnPPluginService> services = new ArrayList();
/*      */   
/*  105 */   private Map<URL, String> root_info_map = new HashMap();
/*  106 */   private Map<String, String> log_no_repeat_map = new HashMap();
/*      */   
/*  108 */   protected AEMonitor this_mon = new AEMonitor("UPnPPlugin");
/*      */   
/*      */ 
/*      */ 
/*      */   public static void load(PluginInterface plugin_interface)
/*      */   {
/*  114 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  115 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Universal Plug and Play (UPnP)");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*  122 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  124 */     this.log = this.plugin_interface.getLogger().getTimeStampedChannel("UPnP");
/*  125 */     this.log.setDiagnostic();
/*  126 */     this.log.setForce(true);
/*      */     
/*  128 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*  130 */     final BasicPluginViewModel model = ui_manager.createBasicPluginViewModel("UPnP");
/*      */     
/*      */ 
/*  133 */     model.setConfigSectionID("UPnP");
/*      */     
/*  135 */     BasicPluginConfigModel upnp_config = ui_manager.createBasicPluginConfigModel("plugins", "UPnP");
/*      */     
/*      */ 
/*      */ 
/*  139 */     BasicPluginConfigModel natpmp_config = ui_manager.createBasicPluginConfigModel("UPnP", "NATPMP");
/*      */     
/*  141 */     natpmp_config.addLabelParameter2("natpmp.info");
/*      */     
/*  143 */     ActionParameter natpmp_wiki = natpmp_config.addActionParameter2("Utils.link.visit", "MainWindow.about.internet.wiki");
/*      */     
/*  145 */     natpmp_wiki.setStyle(2);
/*      */     
/*  147 */     natpmp_wiki.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  155 */           UPnPPlugin.this.plugin_interface.getUIManager().openURL(new URL("http://wiki.vuze.com/w/NATPMP"));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  159 */           e.printStackTrace();
/*      */         }
/*      */         
/*      */       }
/*  163 */     });
/*  164 */     this.natpmp_enable_param = natpmp_config.addBooleanParameter2("natpmp.enable", "natpmp.enable", false);
/*      */     
/*      */ 
/*  167 */     this.nat_pmp_router = natpmp_config.addStringParameter2("natpmp.routeraddress", "natpmp.routeraddress", "");
/*      */     
/*  169 */     this.natpmp_enable_param.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  176 */         UPnPPlugin.this.setNATPMPEnableState();
/*      */       }
/*      */       
/*  179 */     });
/*  180 */     this.natpmp_enable_param.addEnabledOnSelection(this.nat_pmp_router);
/*      */     
/*      */ 
/*      */ 
/*  184 */     upnp_config.addLabelParameter2("upnp.info");
/*  185 */     upnp_config.addHyperlinkParameter2("upnp.wiki_link", "http://wiki.vuze.com/w/UPnP");
/*      */     
/*      */ 
/*  188 */     this.upnp_enable_param = upnp_config.addBooleanParameter2("upnp.enable", "upnp.enable", true);
/*      */     
/*      */ 
/*  191 */     this.grab_ports_param = upnp_config.addBooleanParameter2("upnp.grabports", "upnp.grabports", false);
/*      */     
/*  193 */     this.release_mappings_param = upnp_config.addBooleanParameter2("upnp.releasemappings", "upnp.releasemappings", true);
/*      */     
/*  195 */     ActionParameter refresh_param = upnp_config.addActionParameter2("upnp.refresh.label", "upnp.refresh.button");
/*      */     
/*  197 */     refresh_param.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  204 */         UPnPPlugin.this.refreshMappings();
/*      */       }
/*      */       
/*      */ 
/*  208 */     });
/*  209 */     final BooleanParameter auto_refresh_on_bad_nat_param = upnp_config.addBooleanParameter2("upnp.refresh_on_bad_nat", "upnp.refresh_mappings_on_bad_nat", false);
/*  210 */     this.plugin_interface.getUtilities().createTimer("upnp mapping auto-refresh", true).addPeriodicEvent(60000L, new org.gudy.azureus2.plugins.utils.UTTimerEventPerformer() {
/*  211 */       private long last_bad_nat = 0L;
/*      */       
/*  213 */       public void perform(UTTimerEvent event) { if (UPnPPlugin.this.upnp == null) return;
/*  214 */         if (!auto_refresh_on_bad_nat_param.getValue()) return;
/*  215 */         if (!UPnPPlugin.this.upnp_enable_param.getValue()) return;
/*  216 */         int status = UPnPPlugin.this.plugin_interface.getConnectionManager().getNATStatus();
/*  217 */         if (status == 3)
/*      */         {
/*      */ 
/*      */ 
/*  221 */           long now = UPnPPlugin.this.plugin_interface.getUtilities().getCurrentSystemTime();
/*  222 */           if (this.last_bad_nat + 900000L < now) {
/*  223 */             this.last_bad_nat = now;
/*  224 */             UPnPPlugin.this.log.log(2, "NAT status is firewalled - trying to refresh UPnP mappings");
/*  225 */             UPnPPlugin.this.refreshMappings(true);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  230 */     });
/*  231 */     upnp_config.addLabelParameter2("blank.resource");
/*      */     
/*  233 */     this.alert_success_param = upnp_config.addBooleanParameter2("upnp.alertsuccess", "upnp.alertsuccess", false);
/*      */     
/*  235 */     this.alert_other_port_param = upnp_config.addBooleanParameter2("upnp.alertothermappings", "upnp.alertothermappings", true);
/*      */     
/*  237 */     this.alert_device_probs_param = upnp_config.addBooleanParameter2("upnp.alertdeviceproblems", "upnp.alertdeviceproblems", true);
/*      */     
/*  239 */     this.selected_interfaces_param = upnp_config.addStringParameter2("upnp.selectedinterfaces", "upnp.selectedinterfaces", "");
/*  240 */     this.selected_interfaces_param.setGenerateIntermediateEvents(false);
/*      */     
/*  242 */     this.selected_addresses_param = upnp_config.addStringParameter2("upnp.selectedaddresses", "upnp.selectedaddresses", "");
/*  243 */     this.selected_addresses_param.setGenerateIntermediateEvents(false);
/*      */     
/*  245 */     this.desc_prefix_param = upnp_config.addStringParameter2("upnp.descprefix", "upnp.descprefix", "Azureus UPnP");
/*  246 */     this.desc_prefix_param.setGenerateIntermediateEvents(false);
/*      */     
/*  248 */     this.ignore_bad_devices = upnp_config.addBooleanParameter2("upnp.ignorebaddevices", "upnp.ignorebaddevices", true);
/*      */     
/*  250 */     this.ignored_devices_list = upnp_config.addLabelParameter2("upnp.ignorebaddevices.info");
/*      */     
/*  252 */     ActionParameter reset_param = upnp_config.addActionParameter2("upnp.ignorebaddevices.reset", "upnp.ignorebaddevices.reset.action");
/*      */     
/*  254 */     reset_param.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  261 */         PluginConfig pc = UPnPPlugin.this.plugin_interface.getPluginconfig();
/*      */         
/*  263 */         for (int i = 0; i < UPnPPlugin.STATS_KEYS.length; i++)
/*      */         {
/*  265 */           String key = "upnp.device.stats." + UPnPPlugin.STATS_KEYS[i];
/*      */           
/*  267 */           pc.setPluginMapParameter(key, new HashMap());
/*      */         }
/*      */         
/*  270 */         pc.setPluginMapParameter("upnp.device.ignorelist", new HashMap());
/*      */         
/*  272 */         UPnPPlugin.this.updateIgnoreList();
/*      */       }
/*      */       
/*  275 */     });
/*  276 */     this.trace_to_log = upnp_config.addBooleanParameter2("upnp.trace_to_log", "upnp.trace_to_log", false);
/*      */     
/*  278 */     final boolean enabled = this.upnp_enable_param.getValue();
/*      */     
/*  280 */     this.upnp_enable_param.addEnabledOnSelection(this.alert_success_param);
/*  281 */     this.upnp_enable_param.addEnabledOnSelection(this.grab_ports_param);
/*  282 */     this.upnp_enable_param.addEnabledOnSelection(refresh_param);
/*  283 */     auto_refresh_on_bad_nat_param.addEnabledOnSelection(refresh_param);
/*  284 */     this.upnp_enable_param.addEnabledOnSelection(this.alert_other_port_param);
/*  285 */     this.upnp_enable_param.addEnabledOnSelection(this.alert_device_probs_param);
/*  286 */     this.upnp_enable_param.addEnabledOnSelection(this.release_mappings_param);
/*  287 */     this.upnp_enable_param.addEnabledOnSelection(this.selected_interfaces_param);
/*  288 */     this.upnp_enable_param.addEnabledOnSelection(this.selected_addresses_param);
/*  289 */     this.upnp_enable_param.addEnabledOnSelection(this.desc_prefix_param);
/*  290 */     this.upnp_enable_param.addEnabledOnSelection(this.ignore_bad_devices);
/*  291 */     this.upnp_enable_param.addEnabledOnSelection(this.ignored_devices_list);
/*  292 */     this.upnp_enable_param.addEnabledOnSelection(reset_param);
/*  293 */     this.upnp_enable_param.addEnabledOnSelection(this.trace_to_log);
/*      */     
/*  295 */     this.natpmp_enable_param.setEnabled(enabled);
/*      */     
/*  297 */     model.getStatus().setText(enabled ? "Running" : "Disabled");
/*      */     
/*  299 */     this.upnp_enable_param.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter p)
/*      */       {
/*      */ 
/*  306 */         boolean e = UPnPPlugin.this.upnp_enable_param.getValue();
/*      */         
/*  308 */         UPnPPlugin.this.natpmp_enable_param.setEnabled(e);
/*      */         
/*  310 */         model.getStatus().setText(e ? "Running" : "Disabled");
/*      */         
/*  312 */         if (e)
/*      */         {
/*  314 */           UPnPPlugin.this.startUp();
/*      */         }
/*      */         else
/*      */         {
/*  318 */           UPnPPlugin.this.closeDown(true);
/*      */         }
/*      */         
/*  321 */         UPnPPlugin.this.setNATPMPEnableState();
/*      */       }
/*      */       
/*  324 */     });
/*  325 */     model.getActivity().setVisible(false);
/*  326 */     model.getProgress().setVisible(false);
/*      */     
/*  328 */     this.log.addListener(new org.gudy.azureus2.plugins.logging.LoggerChannelListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageLogged(int type, String message)
/*      */       {
/*      */ 
/*      */ 
/*  336 */         model.getLogArea().appendText(message + "\n");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageLogged(String str, Throwable error)
/*      */       {
/*  344 */         model.getLogArea().appendText(error.toString() + "\n");
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  350 */     });
/*  351 */     DelayedTask dt = this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*  355 */         if (enabled)
/*      */         {
/*  357 */           UPnPPlugin.this.updateIgnoreList();
/*      */           
/*  359 */           UPnPPlugin.this.startUp();
/*      */         }
/*      */       }
/*  362 */     });
/*  363 */     dt.queue();
/*      */     
/*  365 */     this.plugin_interface.addListener(new org.gudy.azureus2.plugins.PluginListener()
/*      */     {
/*      */       public void initializationComplete() {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownInitiated()
/*      */       {
/*  376 */         if (UPnPPlugin.this.services.size() == 0)
/*      */         {
/*  378 */           UPnPPlugin.this.plugin_interface.getPluginconfig().setPluginParameter("plugin.info", "");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void closedownComplete()
/*      */       {
/*  385 */         UPnPPlugin.this.closeDown(true);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected void updateIgnoreList()
/*      */   {
/*      */     try
/*      */     {
/*  394 */       String param = "";
/*      */       
/*  396 */       if (this.ignore_bad_devices.getValue())
/*      */       {
/*  398 */         PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */         
/*  400 */         Map ignored = pc.getPluginMapParameter("upnp.device.ignorelist", new HashMap());
/*      */         
/*  402 */         Iterator it = ignored.entrySet().iterator();
/*      */         
/*  404 */         while (it.hasNext())
/*      */         {
/*  406 */           Map.Entry entry = (Map.Entry)it.next();
/*      */           
/*  408 */           Map value = (Map)entry.getValue();
/*      */           
/*  410 */           param = param + "\n    " + entry.getKey() + ": " + new String((byte[])value.get("Location"));
/*      */         }
/*      */         
/*  413 */         if (ignored.size() > 0)
/*      */         {
/*  415 */           this.log.log("Devices currently being ignored: " + param);
/*      */         }
/*      */       }
/*      */       
/*  419 */       String text = this.plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText("upnp.ignorebaddevices.info", new String[] { param });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  424 */       this.ignored_devices_list.setLabelText(text);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  428 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void ignoreDevice(String USN, URL location)
/*      */   {
/*  439 */     if (this.ignore_bad_devices.getValue()) {
/*      */       try
/*      */       {
/*  442 */         PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */         
/*  444 */         Map ignored = pc.getPluginMapParameter("upnp.device.ignorelist", new HashMap());
/*      */         
/*  446 */         Map entry = (Map)ignored.get(USN);
/*      */         
/*  448 */         if (entry == null)
/*      */         {
/*  450 */           entry = new HashMap();
/*      */           
/*  452 */           entry.put("Location", location.toString().getBytes());
/*      */           
/*  454 */           ignored.put(USN, entry);
/*      */           
/*  456 */           pc.setPluginMapParameter("upnp.device.ignorelist", ignored);
/*      */           
/*  458 */           updateIgnoreList();
/*      */           
/*  460 */           String text = this.plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText("upnp.ignorebaddevices.alert", new String[] { location.toString() });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  465 */           this.log.logAlertRepeatable(2, text);
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  470 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void startUp()
/*      */   {
/*  478 */     if (this.upnp != null)
/*      */     {
/*      */ 
/*      */ 
/*  482 */       refreshMappings();
/*      */       
/*  484 */       return;
/*      */     }
/*      */     
/*  487 */     final LoggerChannel core_log = this.plugin_interface.getLogger().getChannel("UPnP Core");
/*      */     try
/*      */     {
/*  490 */       this.upnp = UPnPFactory.getSingleton(new com.aelitis.net.upnp.UPnPAdapter()
/*      */       {
/*      */ 
/*  493 */         Set exception_traces = new java.util.HashSet();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public SimpleXMLParserDocument parseXML(String data)
/*      */           throws org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentException
/*      */         {
/*  501 */           return UPnPPlugin.this.plugin_interface.getUtilities().getSimpleXMLParserDocumentFactory().create(data);
/*      */         }
/*      */         
/*      */ 
/*      */         public ResourceDownloaderFactory getResourceDownloaderFactory()
/*      */         {
/*  507 */           return UPnPPlugin.this.plugin_interface.getUtilities().getResourceDownloaderFactory();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public UTTimer createTimer(String name)
/*      */         {
/*  514 */           return UPnPPlugin.this.plugin_interface.getUtilities().createTimer(name, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void createThread(String name, Runnable runnable)
/*      */         {
/*  522 */           UPnPPlugin.this.plugin_interface.getUtilities().createThread(name, runnable);
/*      */         }
/*      */         
/*      */ 
/*      */         public java.util.Comparator getAlphanumericComparator()
/*      */         {
/*  528 */           return UPnPPlugin.this.plugin_interface.getUtilities().getFormatters().getAlphanumericComparator(true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void trace(String str)
/*      */         {
/*  535 */           core_log.log(str);
/*  536 */           if (UPnPPlugin.this.trace_to_log.getValue()) {
/*  537 */             UPnPPlugin.this.upnp_log_listener.log(str);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void log(Throwable e)
/*      */         {
/*  545 */           String nested = Debug.getNestedExceptionMessage(e);
/*      */           
/*  547 */           if (!this.exception_traces.contains(nested))
/*      */           {
/*  549 */             this.exception_traces.add(nested);
/*      */             
/*  551 */             if (this.exception_traces.size() > 128)
/*      */             {
/*  553 */               this.exception_traces.clear();
/*      */             }
/*      */             
/*  556 */             core_log.log(e);
/*      */           }
/*      */           else
/*      */           {
/*  560 */             core_log.log(nested);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void log(String str)
/*      */         {
/*  568 */           UPnPPlugin.this.log.log(str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  574 */         public String getTraceDir() { return UPnPPlugin.this.plugin_interface.getUtilities().getAzureusUserDir(); } }, getSelectedInterfaces());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  579 */       this.upnp.addRootDeviceListener(this);
/*      */       
/*  581 */       this.upnp_log_listener = new UPnPLogListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void log(String str)
/*      */         {
/*      */ 
/*  588 */           UPnPPlugin.this.log.log(str);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void logAlert(String str, boolean error, int type)
/*      */         {
/*  597 */           boolean logged = false;
/*      */           
/*  599 */           if (UPnPPlugin.this.alert_device_probs_param.getValue())
/*      */           {
/*  601 */             if (type == 1)
/*      */             {
/*  603 */               UPnPPlugin.this.log.logAlertRepeatable(error ? 3 : 2, str);
/*      */               
/*      */ 
/*      */ 
/*  607 */               logged = true;
/*      */             }
/*      */             else
/*      */             {
/*  611 */               boolean do_it = false;
/*      */               
/*  613 */               if (type == 3)
/*      */               {
/*  615 */                 byte[] fp = UPnPPlugin.this.plugin_interface.getUtilities().getSecurityManager().calculateSHA1(str.getBytes());
/*      */                 
/*      */ 
/*      */ 
/*  619 */                 String key = "upnp.alert.fp." + UPnPPlugin.this.plugin_interface.getUtilities().getFormatters().encodeBytesToString(fp);
/*      */                 
/*  621 */                 PluginConfig pc = UPnPPlugin.this.plugin_interface.getPluginconfig();
/*      */                 
/*  623 */                 if (!pc.getPluginBooleanParameter(key, false))
/*      */                 {
/*  625 */                   pc.setPluginParameter(key, true);
/*      */                   
/*  627 */                   do_it = true;
/*      */                 }
/*      */               }
/*      */               else {
/*  631 */                 do_it = true;
/*      */               }
/*      */               
/*  634 */               if (do_it)
/*      */               {
/*  636 */                 UPnPPlugin.this.log.logAlert(error ? 3 : 2, str);
/*      */                 
/*      */ 
/*      */ 
/*  640 */                 logged = true;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  645 */           if (!logged)
/*      */           {
/*  647 */             UPnPPlugin.this.log.log(str);
/*      */           }
/*      */           
/*      */         }
/*  651 */       };
/*  652 */       this.upnp.addLogListener(this.upnp_log_listener);
/*      */       
/*  654 */       this.mapping_manager.addListener(new UPnPMappingManagerListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void mappingAdded(UPnPMapping mapping)
/*      */         {
/*      */ 
/*  661 */           UPnPPlugin.this.addMapping(mapping);
/*      */         }
/*      */         
/*  664 */       });
/*  665 */       UPnPMapping[] upnp_mappings = this.mapping_manager.getMappings();
/*      */       
/*  667 */       for (int i = 0; i < upnp_mappings.length; i++)
/*      */       {
/*  669 */         addMapping(upnp_mappings[i]);
/*      */       }
/*      */       
/*  672 */       setNATPMPEnableState();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  676 */       this.log.log(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void closeDown(final boolean end_of_day)
/*      */   {
/*  687 */     final AESemaphore sem = new AESemaphore("UPnPPlugin:closeTimeout");
/*      */     
/*      */ 
/*  690 */     new AEThread2("UPnPPlugin:closeTimeout")
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*  696 */           for (int i = 0; i < UPnPPlugin.this.mappings.size(); i++)
/*      */           {
/*  698 */             UPnPMapping mapping = (UPnPMapping)UPnPPlugin.this.mappings.get(i);
/*      */             
/*  700 */             if (mapping.isEnabled())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  705 */               for (int j = 0; j < UPnPPlugin.this.services.size(); j++)
/*      */               {
/*  707 */                 UPnPPluginService service = (UPnPPluginService)UPnPPlugin.this.services.get(j);
/*      */                 
/*  709 */                 service.removeMapping(UPnPPlugin.this.log, mapping, end_of_day);
/*      */               }
/*      */             }
/*      */           }
/*      */         } finally {
/*  714 */           sem.release();
/*      */         }
/*      */       }
/*      */     }.start();
/*      */     
/*      */ 
/*  720 */     if (!sem.reserve(end_of_day ? 15000L : 0L))
/*      */     {
/*  722 */       String msg = "A UPnP device is taking a long time to release its port mappings, consider disabling this via the UPnP configuration.";
/*      */       
/*  724 */       if (this.upnp_log_listener != null)
/*      */       {
/*  726 */         this.upnp_log_listener.logAlert(msg, false, 2);
/*      */       }
/*      */       else
/*      */       {
/*  730 */         this.log.logAlertRepeatable(2, msg);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean deviceDiscovered(String USN, URL location)
/*      */   {
/*  740 */     String[] addresses = getSelectedAddresses();
/*      */     
/*  742 */     if (addresses.length > 0)
/*      */     {
/*  744 */       String address = location.getHost();
/*      */       
/*  746 */       boolean found = false;
/*      */       
/*  748 */       boolean all_exclude = true;
/*      */       
/*  750 */       for (int i = 0; i < addresses.length; i++)
/*      */       {
/*  752 */         String this_address = addresses[i];
/*      */         
/*  754 */         boolean include = true;
/*      */         
/*  756 */         if (this_address.startsWith("+"))
/*      */         {
/*  758 */           this_address = this_address.substring(1);
/*      */           
/*  760 */           all_exclude = false;
/*      */         }
/*  762 */         else if (this_address.startsWith("-"))
/*      */         {
/*  764 */           this_address = this_address.substring(1);
/*      */           
/*  766 */           include = false;
/*      */         }
/*      */         else
/*      */         {
/*  770 */           all_exclude = false;
/*      */         }
/*      */         
/*  773 */         if (this_address.equals(address))
/*      */         {
/*  775 */           if (!include)
/*      */           {
/*  777 */             logNoRepeat(USN, "Device '" + location + "' is being ignored as excluded in address list");
/*      */             
/*  779 */             return false;
/*      */           }
/*      */           
/*  782 */           found = true;
/*      */           
/*  784 */           break;
/*      */         }
/*      */       }
/*      */       
/*  788 */       if (!found)
/*      */       {
/*  790 */         if (!all_exclude)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  795 */           logNoRepeat(USN, "Device '" + location + "' is being ignored as not in address list");
/*      */           
/*  797 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  802 */     if (!this.ignore_bad_devices.getValue())
/*      */     {
/*  804 */       return true;
/*      */     }
/*      */     
/*  807 */     incrementDeviceStats(USN, "discover");
/*      */     
/*  809 */     boolean ok = checkDeviceStats(USN, location);
/*      */     
/*  811 */     String stats = "";
/*      */     
/*  813 */     for (int i = 0; i < STATS_KEYS.length; i++)
/*      */     {
/*  815 */       stats = stats + (i == 0 ? "" : ",") + STATS_KEYS[i] + "=" + getDeviceStats(USN, STATS_KEYS[i]);
/*      */     }
/*      */     
/*  818 */     if (!ok)
/*      */     {
/*  820 */       logNoRepeat(USN, "Device '" + location + "' is being ignored: " + stats);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  825 */       logNoRepeat(USN, "Device '" + location + "' is ok: " + stats);
/*      */     }
/*      */     
/*  828 */     return ok;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logNoRepeat(String usn, String msg)
/*      */   {
/*  836 */     synchronized (this.log_no_repeat_map)
/*      */     {
/*  838 */       String last = (String)this.log_no_repeat_map.get(usn);
/*      */       
/*  840 */       if ((last != null) && (last.equals(msg)))
/*      */       {
/*  842 */         return;
/*      */       }
/*      */       
/*  845 */       this.log_no_repeat_map.put(usn, msg);
/*      */     }
/*      */     
/*  848 */     this.log.log(msg);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void rootDeviceFound(UPnPRootDevice device)
/*      */   {
/*  855 */     incrementDeviceStats(device.getUSN(), "found");
/*      */     
/*  857 */     checkDeviceStats(device);
/*      */     try
/*      */     {
/*  860 */       int interesting = processDevice(device.getDevice());
/*      */       
/*  862 */       if (interesting > 0) {
/*      */         try
/*      */         {
/*  865 */           this.this_mon.enter();
/*      */           
/*  867 */           this.root_info_map.put(device.getLocation(), device.getInfo());
/*      */           
/*  869 */           Iterator<String> it = this.root_info_map.values().iterator();
/*      */           
/*  871 */           String all_info = "";
/*      */           
/*  873 */           List reported_info = new ArrayList();
/*      */           
/*  875 */           while (it.hasNext())
/*      */           {
/*  877 */             String info = (String)it.next();
/*      */             
/*  879 */             if ((info != null) && (!reported_info.contains(info)))
/*      */             {
/*  881 */               reported_info.add(info);
/*      */               
/*  883 */               all_info = all_info + (all_info.length() == 0 ? "" : ",") + info;
/*      */             }
/*      */           }
/*      */           
/*  887 */           if (all_info.length() > 0)
/*      */           {
/*  889 */             this.plugin_interface.getPluginconfig().setPluginParameter("plugin.info", all_info);
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  894 */           this.this_mon.exit();
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  899 */       this.log.log("Root device processing fails", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean checkDeviceStats(UPnPRootDevice root)
/*      */   {
/*  907 */     return checkDeviceStats(root.getUSN(), root.getLocation());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean checkDeviceStats(String USN, URL location)
/*      */   {
/*  915 */     long discovers = getDeviceStats(USN, "discover");
/*  916 */     long founds = getDeviceStats(USN, "found");
/*      */     
/*  918 */     if ((discovers > 3L) && (founds == 0L))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  923 */       ignoreDevice(USN, location);
/*      */       
/*  925 */       return false;
/*      */     }
/*  927 */     if (founds > 0L)
/*      */     {
/*      */ 
/*      */ 
/*  931 */       setDeviceStats(USN, "discover", 0L);
/*  932 */       setDeviceStats(USN, "found", 0L);
/*      */     }
/*      */     
/*  935 */     long map_ok = getDeviceStats(USN, "map_ok");
/*  936 */     long map_bad = getDeviceStats(USN, "map_bad");
/*      */     
/*  938 */     if ((map_bad > 5L) && (map_ok == 0L))
/*      */     {
/*  940 */       ignoreDevice(USN, location);
/*      */       
/*  942 */       return false;
/*      */     }
/*  944 */     if (map_ok > 0L)
/*      */     {
/*  946 */       setDeviceStats(USN, "map_ok", 0L);
/*  947 */       setDeviceStats(USN, "map_bad", 0L);
/*      */     }
/*      */     
/*  950 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long incrementDeviceStats(String USN, String stat_key)
/*      */   {
/*  958 */     String key = "upnp.device.stats." + stat_key;
/*      */     
/*  960 */     PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */     
/*  962 */     Map counts = pc.getPluginMapParameter(key, new HashMap());
/*      */     
/*  964 */     Long count = (Long)counts.get(USN);
/*      */     
/*  966 */     if (count == null)
/*      */     {
/*  968 */       count = new Long(1L);
/*      */     }
/*      */     else
/*      */     {
/*  972 */       count = new Long(count.longValue() + 1L);
/*      */     }
/*      */     
/*  975 */     counts.put(USN, count);
/*      */     
/*  977 */     pc.getPluginMapParameter(key, counts);
/*      */     
/*  979 */     return count.longValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getDeviceStats(String USN, String stat_key)
/*      */   {
/*  987 */     String key = "upnp.device.stats." + stat_key;
/*      */     
/*  989 */     PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */     
/*  991 */     Map counts = pc.getPluginMapParameter(key, new HashMap());
/*      */     
/*  993 */     Long count = (Long)counts.get(USN);
/*      */     
/*  995 */     if (count == null)
/*      */     {
/*  997 */       return 0L;
/*      */     }
/*      */     
/* 1000 */     return count.longValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setDeviceStats(String USN, String stat_key, long value)
/*      */   {
/* 1009 */     String key = "upnp.device.stats." + stat_key;
/*      */     
/* 1011 */     PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */     
/* 1013 */     Map counts = pc.getPluginMapParameter(key, new HashMap());
/*      */     
/* 1015 */     counts.put(USN, new Long(value));
/*      */     
/* 1017 */     pc.getPluginMapParameter(key, counts);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mappingResult(UPnPWANConnection connection, boolean ok)
/*      */   {
/* 1025 */     UPnPRootDevice root = connection.getGenericService().getDevice().getRootDevice();
/*      */     
/* 1027 */     incrementDeviceStats(root.getUSN(), ok ? "map_ok" : "map_bad");
/*      */     
/* 1029 */     checkDeviceStats(root);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mappingsReadResult(UPnPWANConnection connection, boolean ok)
/*      */   {
/* 1037 */     UPnPRootDevice root = connection.getGenericService().getDevice().getRootDevice();
/*      */     
/* 1039 */     incrementDeviceStats(root.getUSN(), ok ? "read_ok" : "read_bad");
/*      */   }
/*      */   
/*      */ 
/*      */   protected String[] getSelectedInterfaces()
/*      */   {
/* 1045 */     String si = this.selected_interfaces_param.getValue().trim();
/*      */     
/* 1047 */     StringTokenizer tok = new StringTokenizer(si, ";");
/*      */     
/* 1049 */     List res = new ArrayList();
/*      */     
/* 1051 */     while (tok.hasMoreTokens())
/*      */     {
/* 1053 */       String s = tok.nextToken().trim();
/*      */       
/* 1055 */       if (s.length() > 0)
/*      */       {
/* 1057 */         res.add(s);
/*      */       }
/*      */     }
/*      */     
/* 1061 */     return (String[])res.toArray(new String[res.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String[] getSelectedAddresses()
/*      */   {
/* 1067 */     String si = this.selected_addresses_param.getValue().trim();
/*      */     
/* 1069 */     StringTokenizer tok = new StringTokenizer(si, ";");
/*      */     
/* 1071 */     List res = new ArrayList();
/*      */     
/* 1073 */     while (tok.hasMoreTokens())
/*      */     {
/* 1075 */       String s = tok.nextToken().trim();
/*      */       
/* 1077 */       if (s.length() > 0)
/*      */       {
/* 1079 */         res.add(s);
/*      */       }
/*      */     }
/*      */     
/* 1083 */     return (String[])res.toArray(new String[res.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int processDevice(UPnPDevice device)
/*      */     throws UPnPException
/*      */   {
/* 1092 */     int interesting = processServices(device, device.getServices());
/*      */     
/* 1094 */     UPnPDevice[] kids = device.getSubDevices();
/*      */     
/* 1096 */     for (int i = 0; i < kids.length; i++)
/*      */     {
/* 1098 */       interesting += processDevice(kids[i]);
/*      */     }
/*      */     
/* 1101 */     return interesting;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int processServices(UPnPDevice device, UPnPService[] device_services)
/*      */     throws UPnPException
/*      */   {
/* 1111 */     int interesting = 0;
/*      */     
/* 1113 */     for (int i = 0; i < device_services.length; i++)
/*      */     {
/* 1115 */       UPnPService s = device_services[i];
/*      */       
/* 1117 */       String service_type = s.getServiceType();
/*      */       
/* 1119 */       if ((service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANIPConnection:1")) || (service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANPPPConnection:1")))
/*      */       {
/*      */ 
/* 1122 */         final UPnPWANConnection wan_service = (UPnPWANConnection)s.getSpecificService();
/*      */         
/* 1124 */         device.getRootDevice().addListener(new com.aelitis.net.upnp.UPnPRootDeviceListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void lost(UPnPRootDevice root, boolean replaced)
/*      */           {
/*      */ 
/*      */ 
/* 1132 */             UPnPPlugin.this.removeService(wan_service, replaced);
/*      */           }
/*      */           
/* 1135 */         });
/* 1136 */         addService(wan_service);
/*      */         
/* 1138 */         interesting++;
/*      */       }
/* 1140 */       else if (!service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1")) {}
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
/* 1162 */     return interesting;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addService(UPnPWANConnection wan_service)
/*      */     throws UPnPException
/*      */   {
/* 1171 */     wan_service.addListener(this);
/*      */     
/* 1173 */     this.mapping_manager.serviceFound(wan_service);
/*      */     
/* 1175 */     this.log.log("    Found " + (!wan_service.getGenericService().getServiceType().contains("PPP") ? "WANIPConnection" : "WANPPPConnection"));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1180 */     String usn = wan_service.getGenericService().getDevice().getRootDevice().getUSN();
/*      */     UPnPWANConnectionPortMapping[] ports;
/* 1182 */     if ((getDeviceStats(usn, "read_ok") == 0L) && (getDeviceStats(usn, "read_bad") > 2L))
/*      */     {
/* 1184 */       UPnPWANConnectionPortMapping[] ports = new UPnPWANConnectionPortMapping[0];
/*      */       
/* 1186 */       wan_service.periodicallyRecheckMappings(false);
/*      */       
/* 1188 */       this.log.log("    Not reading port mappings from device due to previous failures");
/*      */     }
/*      */     else
/*      */     {
/* 1192 */       ports = wan_service.getPortMappings();
/*      */     }
/*      */     
/* 1195 */     for (int j = 0; j < ports.length; j++)
/*      */     {
/* 1197 */       this.log.log("      mapping [" + j + "] " + ports[j].getExternalPort() + "/" + (ports[j].isTCP() ? "TCP" : "UDP") + " [" + ports[j].getDescription() + "] -> " + ports[j].getInternalHost());
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1202 */       this.this_mon.enter();
/*      */       
/* 1204 */       this.services.add(new UPnPPluginService(wan_service, ports, this.desc_prefix_param, this.alert_success_param, this.grab_ports_param, this.alert_other_port_param, this.release_mappings_param));
/*      */       
/* 1206 */       if (this.services.size() > 1)
/*      */       {
/*      */ 
/*      */ 
/* 1210 */         String new_usn = wan_service.getGenericService().getDevice().getRootDevice().getUSN();
/*      */         
/* 1212 */         boolean multiple_found = false;
/*      */         
/* 1214 */         for (int i = 0; i < this.services.size() - 1; i++)
/*      */         {
/* 1216 */           UPnPPluginService service = (UPnPPluginService)this.services.get(i);
/*      */           
/* 1218 */           String existing_usn = service.getService().getGenericService().getDevice().getRootDevice().getUSN();
/*      */           
/* 1220 */           if (!new_usn.equals(existing_usn))
/*      */           {
/* 1222 */             multiple_found = true;
/*      */             
/* 1224 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1228 */         if (multiple_found)
/*      */         {
/* 1230 */           PluginConfig pc = this.plugin_interface.getPluginconfig();
/*      */           
/* 1232 */           if (!pc.getPluginBooleanParameter("upnp.device.multipledevices.warned", false))
/*      */           {
/* 1234 */             pc.setPluginParameter("upnp.device.multipledevices.warned", true);
/*      */             
/* 1236 */             String text = MessageText.getString("upnp.alert.multipledevice.warning");
/*      */             
/* 1238 */             this.log.logAlertRepeatable(2, text);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1243 */       checkState();
/*      */     }
/*      */     finally
/*      */     {
/* 1247 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void removeService(UPnPWANConnection wan_service, boolean replaced)
/*      */   {
/*      */     try
/*      */     {
/* 1257 */       this.this_mon.enter();
/*      */       
/* 1259 */       String name = !wan_service.getGenericService().getServiceType().contains("PPP") ? "WANIPConnection" : "WANPPPConnection";
/*      */       
/*      */ 
/* 1262 */       String text = MessageText.getString("upnp.alert.lostdevice", new String[] { name, wan_service.getGenericService().getDevice().getRootDevice().getLocation().getHost() });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1267 */       this.log.log(text);
/*      */       
/* 1269 */       if ((!replaced) && (this.alert_device_probs_param.getValue()))
/*      */       {
/* 1271 */         this.log.logAlertRepeatable(2, text);
/*      */       }
/*      */       
/* 1274 */       for (int i = 0; i < this.services.size(); i++)
/*      */       {
/* 1276 */         UPnPPluginService ps = (UPnPPluginService)this.services.get(i);
/*      */         
/* 1278 */         if (ps.getService() == wan_service)
/*      */         {
/* 1280 */           this.services.remove(i);
/*      */           
/* 1282 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1287 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void addMapping(UPnPMapping mapping)
/*      */   {
/*      */     try
/*      */     {
/* 1296 */       this.this_mon.enter();
/*      */       
/* 1298 */       this.mappings.add(mapping);
/*      */       
/* 1300 */       this.log.log("Mapping request: " + mapping.getString() + ", enabled = " + mapping.isEnabled());
/*      */       
/* 1302 */       mapping.addListener(this);
/*      */       
/* 1304 */       checkState();
/*      */     }
/*      */     finally
/*      */     {
/* 1308 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void mappingChanged(UPnPMapping mapping)
/*      */   {
/* 1316 */     checkState();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void mappingDestroyed(UPnPMapping mapping)
/*      */   {
/*      */     try
/*      */     {
/* 1325 */       this.this_mon.enter();
/*      */       
/* 1327 */       this.mappings.remove(mapping);
/*      */       
/* 1329 */       this.log.log("Mapping request removed: " + mapping.getString());
/*      */       
/* 1331 */       for (int j = 0; j < this.services.size(); j++)
/*      */       {
/* 1333 */         UPnPPluginService service = (UPnPPluginService)this.services.get(j);
/*      */         
/* 1335 */         service.removeMapping(this.log, mapping, false);
/*      */       }
/*      */     }
/*      */     finally {
/* 1339 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void checkState()
/*      */   {
/*      */     try
/*      */     {
/* 1347 */       this.this_mon.enter();
/*      */       
/* 1349 */       for (int i = 0; i < this.mappings.size(); i++)
/*      */       {
/* 1351 */         UPnPMapping mapping = (UPnPMapping)this.mappings.get(i);
/*      */         
/* 1353 */         for (int j = 0; j < this.services.size(); j++)
/*      */         {
/* 1355 */           UPnPPluginService service = (UPnPPluginService)this.services.get(j);
/*      */           
/* 1357 */           service.checkMapping(this.log, mapping);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1362 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String[] getExternalIPAddresses()
/*      */   {
/* 1369 */     List res = new ArrayList();
/*      */     try
/*      */     {
/* 1372 */       this.this_mon.enter();
/*      */       
/* 1374 */       for (int j = 0; j < this.services.size(); j++)
/*      */       {
/* 1376 */         UPnPPluginService service = (UPnPPluginService)this.services.get(j);
/*      */         try
/*      */         {
/* 1379 */           String address = service.getService().getExternalIPAddress();
/*      */           
/* 1381 */           if (address != null)
/*      */           {
/* 1383 */             res.add(address);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1387 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1392 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1395 */     return (String[])res.toArray(new String[res.size()]);
/*      */   }
/*      */   
/*      */   public UPnPPluginService[] getServices()
/*      */   {
/*      */     try
/*      */     {
/* 1402 */       this.this_mon.enter();
/*      */       
/* 1404 */       return (UPnPPluginService[])this.services.toArray(new UPnPPluginService[this.services.size()]);
/*      */     }
/*      */     finally
/*      */     {
/* 1408 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public UPnPPluginService[] getServices(UPnPDevice device)
/*      */   {
/* 1416 */     String target_usn = device.getRootDevice().getUSN();
/*      */     
/* 1418 */     List<UPnPPluginService> res = new ArrayList();
/*      */     try
/*      */     {
/* 1421 */       this.this_mon.enter();
/*      */       
/* 1423 */       for (UPnPPluginService service : this.services)
/*      */       {
/* 1425 */         String this_usn = service.getService().getGenericService().getDevice().getRootDevice().getUSN();
/*      */         
/* 1427 */         if (this_usn.equals(target_usn))
/*      */         {
/* 1429 */           res.add(service);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1434 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1437 */     return (UPnPPluginService[])res.toArray(new UPnPPluginService[res.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UPnPMapping addMapping(String desc_resource, boolean tcp, int port, boolean enabled)
/*      */   {
/* 1449 */     return this.mapping_manager.addMapping(desc_resource, tcp, port, enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public UPnPMapping getMapping(boolean tcp, int port)
/*      */   {
/* 1457 */     return this.mapping_manager.getMapping(tcp, port);
/*      */   }
/*      */   
/*      */ 
/*      */   public UPnPMapping[] getMappings()
/*      */   {
/* 1463 */     return this.mapping_manager.getMappings();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/* 1469 */     return this.upnp_enable_param.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setNATPMPEnableState()
/*      */   {
/* 1475 */     boolean enabled = (this.natpmp_enable_param.getValue()) && (this.upnp_enable_param.getValue());
/*      */     try
/*      */     {
/* 1478 */       if (enabled)
/*      */       {
/* 1480 */         if (this.nat_pmp_upnp == null)
/*      */         {
/* 1482 */           this.nat_pmp_upnp = com.aelitis.net.natpmp.upnp.NatPMPUPnPFactory.create(this.upnp, NatPMPDeviceFactory.getSingleton(new com.aelitis.net.natpmp.NATPMPDeviceAdapter()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public String getRouterAddress()
/*      */             {
/*      */ 
/*      */ 
/* 1491 */               return UPnPPlugin.this.nat_pmp_router.getValue();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void log(String str)
/*      */             {
/* 1498 */               UPnPPlugin.this.log.log("NAT-PMP: " + str);
/*      */             }
/*      */             
/* 1501 */           }));
/* 1502 */           this.nat_pmp_upnp.addListener(this);
/*      */         }
/*      */         
/* 1505 */         this.nat_pmp_upnp.setEnabled(true);
/*      */ 
/*      */       }
/* 1508 */       else if (this.nat_pmp_upnp != null)
/*      */       {
/* 1510 */         this.nat_pmp_upnp.setEnabled(false);
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1515 */       this.log.log("Failed to initialise NAT-PMP subsystem", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logAlert(int type, String resource, String[] params)
/*      */   {
/* 1524 */     String text = this.plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText(resource, params);
/*      */     
/*      */ 
/*      */ 
/* 1528 */     this.log.logAlertRepeatable(type, text);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void refreshMappings()
/*      */   {
/* 1535 */     refreshMappings(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void refreshMappings(boolean force)
/*      */   {
/* 1542 */     if (force) {
/* 1543 */       closeDown(true);
/* 1544 */       startUp();
/*      */     }
/*      */     else {
/* 1547 */       this.upnp.reset();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/*      */     List<UPnPMapping> mappings_copy;
/*      */     
/*      */     List<UPnPPluginService> services_copy;
/*      */     try
/*      */     {
/* 1559 */       this.this_mon.enter();
/*      */       
/* 1561 */       mappings_copy = new ArrayList(this.mappings);
/*      */       
/* 1563 */       services_copy = new ArrayList(this.services);
/*      */     }
/*      */     finally
/*      */     {
/* 1567 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1570 */     writer.println("Mappings");
/*      */     try
/*      */     {
/* 1573 */       writer.indent();
/*      */       
/* 1575 */       for (UPnPMapping mapping : mappings_copy)
/*      */       {
/* 1577 */         if (mapping.isEnabled())
/*      */         {
/* 1579 */           writer.println(mapping.getString());
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1584 */       writer.exdent();
/*      */     }
/*      */     
/* 1587 */     writer.println("Services");
/*      */     try
/*      */     {
/* 1590 */       writer.indent();
/*      */       
/* 1592 */       for (UPnPPluginService service : services_copy)
/*      */       {
/* 1594 */         writer.println(service.getString());
/*      */       }
/*      */     }
/*      */     finally {
/* 1598 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/upnp/UPnPPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */