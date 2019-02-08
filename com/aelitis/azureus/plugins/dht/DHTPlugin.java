/*      */ package com.aelitis.azureus.plugins.dht;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*      */ import com.aelitis.azureus.core.dht.control.DHTControlActivity;
/*      */ import com.aelitis.azureus.core.dht.control.DHTControlContact;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*      */ import com.aelitis.azureus.core.dht.router.DHTRouterContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTTransportUDPImpl;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import com.aelitis.azureus.plugins.dht.impl.DHTPluginContactImpl;
/*      */ import com.aelitis.azureus.plugins.dht.impl.DHTPluginImpl;
/*      */ import com.aelitis.azureus.plugins.dht.impl.DHTPluginImplAdapter;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPMapping;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginConfigListener;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseEvent;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*      */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseListener;
/*      */ import org.gudy.azureus2.plugins.logging.Logger;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextField;
/*      */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.IntParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.LabelParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTPlugin
/*      */   implements Plugin, DHTPluginInterface
/*      */ {
/*      */   public static final int EVENT_DHT_AVAILABLE = 1024;
/*      */   public static final int STATUS_DISABLED = 1;
/*      */   public static final int STATUS_INITALISING = 2;
/*      */   public static final int STATUS_RUNNING = 3;
/*      */   public static final int STATUS_FAILED = 4;
/*      */   public static final byte FLAG_SINGLE_VALUE = 0;
/*      */   public static final byte FLAG_DOWNLOADING = 1;
/*      */   public static final byte FLAG_SEEDING = 2;
/*      */   public static final byte FLAG_MULTI_VALUE = 4;
/*      */   public static final byte FLAG_STATS = 8;
/*      */   public static final byte FLAG_ANON = 16;
/*      */   public static final byte FLAG_PRECIOUS = 32;
/*      */   public static final byte DT_NONE = 1;
/*      */   public static final byte DT_FREQUENCY = 2;
/*      */   public static final byte DT_SIZE = 3;
/*      */   public static final int NW_MAIN = 0;
/*      */   public static final int NW_CVS = 1;
/*      */   public static final int MAX_VALUE_SIZE = 512;
/*      */   private static final String PLUGIN_VERSION = "1.0";
/*      */   private static final String PLUGIN_NAME = "Distributed DB";
/*      */   private static final String PLUGIN_CONFIGSECTION_ID = "plugins.dht";
/*      */   private static final String PLUGIN_RESOURCE_ID = "ConfigView.section.plugins.dht";
/*  109 */   private static final boolean MAIN_DHT_ENABLE = COConfigurationManager.getBooleanParameter("dht.net.main_v4.enable", true);
/*  110 */   private static final boolean CVS_DHT_ENABLE = COConfigurationManager.getBooleanParameter("dht.net.cvs_v4.enable", true);
/*  111 */   private static final boolean MAIN_DHT_V6_ENABLE = COConfigurationManager.getBooleanParameter("dht.net.main_v6.enable", true);
/*      */   
/*      */ 
/*      */   private PluginInterface plugin_interface;
/*      */   
/*  116 */   private int status = 1;
/*      */   
/*      */   private DHTPluginImpl[] dhts;
/*      */   
/*      */   private DHTPluginImpl main_dht;
/*      */   
/*      */   private DHTPluginImpl cvs_dht;
/*      */   
/*      */   private DHTPluginImpl main_v6_dht;
/*      */   private ActionParameter reseed;
/*      */   private boolean enabled;
/*      */   private int dht_data_port;
/*      */   private boolean got_extended_use;
/*      */   private boolean extended_use;
/*  130 */   private AESemaphore init_sem = new AESemaphore("DHTPlugin:init");
/*      */   
/*  132 */   private AEMonitor port_change_mon = new AEMonitor("DHTPlugin:portChanger");
/*      */   
/*      */   private boolean port_changing;
/*      */   private int port_change_outstanding;
/*  136 */   private boolean[] ipfilter_logging = new boolean[1];
/*      */   
/*      */   private BooleanParameter warn_user;
/*      */   
/*      */   private UPnPMapping upnp_mapping;
/*      */   
/*      */   private LoggerChannel log;
/*      */   private DHTLogger dht_log;
/*  144 */   private List listeners = new ArrayList();
/*      */   
/*  146 */   private long start_mono_time = SystemTime.getMonotonousTime();
/*      */   
/*      */ 
/*      */ 
/*      */   public static void load(PluginInterface plugin_interface)
/*      */   {
/*  152 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  153 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Distributed DB");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*  160 */     this.status = 2;
/*      */     
/*  162 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  164 */     this.dht_data_port = UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber();
/*      */     
/*  166 */     this.log = this.plugin_interface.getLogger().getTimeStampedChannel("Distributed DB");
/*      */     
/*  168 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*  170 */     final BasicPluginViewModel model = ui_manager.createBasicPluginViewModel("ConfigView.section.plugins.dht");
/*      */     
/*      */ 
/*  173 */     model.setConfigSectionID("plugins.dht");
/*      */     
/*  175 */     BasicPluginConfigModel config = ui_manager.createBasicPluginConfigModel("plugins", "plugins.dht");
/*      */     
/*  177 */     config.addLabelParameter2("dht.info");
/*      */     
/*  179 */     BooleanParameter enabled_param = config.addBooleanParameter2("dht.enabled", "dht.enabled", true);
/*      */     
/*  181 */     this.plugin_interface.getPluginconfig().addListener(new PluginConfigListener()
/*      */     {
/*      */ 
/*      */       public void configSaved()
/*      */       {
/*      */ 
/*  187 */         int new_dht_data_port = UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber();
/*      */         
/*  189 */         if (new_dht_data_port != DHTPlugin.this.dht_data_port)
/*      */         {
/*  191 */           DHTPlugin.this.changePort(new_dht_data_port);
/*      */         }
/*      */         
/*      */       }
/*  195 */     });
/*  196 */     LabelParameter reseed_label = config.addLabelParameter2("dht.reseed.label");
/*      */     
/*  198 */     final StringParameter reseed_ip = config.addStringParameter2("dht.reseed.ip", "dht.reseed.ip", "");
/*  199 */     final IntParameter reseed_port = config.addIntParameter2("dht.reseed.port", "dht.reseed.port", 0);
/*      */     
/*  201 */     this.reseed = config.addActionParameter2("dht.reseed.info", "dht.reseed");
/*      */     
/*  203 */     this.reseed.setEnabled(false);
/*      */     
/*  205 */     config.createGroup("dht.reseed.group", new Parameter[] { reseed_label, reseed_ip, reseed_port, this.reseed });
/*      */     
/*      */ 
/*  208 */     final BooleanParameter ipfilter_logging_param = config.addBooleanParameter2("dht.ipfilter.log", "dht.ipfilter.log", true);
/*  209 */     this.ipfilter_logging[0] = ipfilter_logging_param.getValue();
/*  210 */     ipfilter_logging_param.addListener(new ParameterListener() {
/*      */       public void parameterChanged(Parameter p) {
/*  212 */         DHTPlugin.this.ipfilter_logging[0] = ipfilter_logging_param.getValue();
/*      */       }
/*      */       
/*  215 */     });
/*  216 */     this.warn_user = config.addBooleanParameter2("dht.warn.user", "dht.warn.user", true);
/*      */     
/*  218 */     final BooleanParameter advanced = config.addBooleanParameter2("dht.advanced", "dht.advanced", false);
/*      */     
/*  220 */     LabelParameter advanced_label = config.addLabelParameter2("dht.advanced.label");
/*      */     
/*  222 */     final StringParameter override_ip = config.addStringParameter2("dht.override.ip", "dht.override.ip", "");
/*      */     
/*  224 */     config.createGroup("dht.advanced.group", new Parameter[] { advanced_label, override_ip });
/*      */     
/*      */ 
/*  227 */     advanced.addEnabledOnSelection(advanced_label);
/*  228 */     advanced.addEnabledOnSelection(override_ip);
/*      */     
/*  230 */     final StringParameter command = config.addStringParameter2("dht.execute.command", "dht.execute.command", "print");
/*      */     
/*  232 */     ActionParameter execute = config.addActionParameter2("dht.execute.info", "dht.execute");
/*      */     
/*  234 */     final BooleanParameter logging = config.addBooleanParameter2("dht.logging", "dht.logging", false);
/*      */     
/*  236 */     config.createGroup("dht.diagnostics.group", new Parameter[] { command, execute, logging });
/*      */     
/*      */ 
/*  239 */     logging.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  246 */         if (DHTPlugin.this.dhts != null)
/*      */         {
/*  248 */           for (int i = 0; i < DHTPlugin.this.dhts.length; i++)
/*      */           {
/*  250 */             DHTPlugin.this.dhts[i].setLogging(logging.getValue());
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  255 */     });
/*  256 */     final DHTPluginOperationListener log_polistener = new DHTPluginOperationListener()
/*      */     {
/*      */ 
/*      */       public boolean diversified()
/*      */       {
/*      */ 
/*  262 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void starts(byte[] key) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */       {
/*  276 */         DHTPlugin.this.log.log("valueRead: " + new String(value.getValue()) + " from " + originator.getName() + "/" + originator.getAddress() + ", flags=" + Integer.toHexString(value.getFlags() & 0xFF));
/*      */         
/*  278 */         if ((value.getFlags() & 0x8) != 0)
/*      */         {
/*  280 */           DHTPluginKeyStats stats = DHTPlugin.this.decodeStats(value);
/*      */           
/*  282 */           DHTPlugin.this.log.log("    stats: size=" + (stats == null ? "null" : Integer.valueOf(stats.getSize())));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void valueWritten(DHTPluginContact target, DHTPluginValue value)
/*      */       {
/*  291 */         DHTPlugin.this.log.log("valueWritten:" + new String(value.getValue()) + " to " + target.getName() + "/" + target.getAddress());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void complete(byte[] key, boolean timeout_occurred)
/*      */       {
/*  299 */         DHTPlugin.this.log.log("complete: timeout = " + timeout_occurred);
/*      */       }
/*      */       
/*  302 */     };
/*  303 */     execute.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  310 */         AEThread2 t = new AEThread2("DHT:commandrunner", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*  317 */               if (DHTPlugin.this.dhts == null)
/*      */               {
/*  319 */                 return;
/*      */               }
/*      */               
/*  322 */               String c = DHTPlugin.5.this.val$command.getValue().trim();
/*  323 */               String lc = c.toLowerCase();
/*      */               
/*  325 */               if (lc.equals("suspend"))
/*      */               {
/*  327 */                 if (!DHTPlugin.this.setSuspended(true))
/*      */                 {
/*  329 */                   Debug.out("Suspend failed");
/*      */                 }
/*      */                 
/*  332 */                 return;
/*      */               }
/*  334 */               if (lc.equals("resume"))
/*      */               {
/*  336 */                 if (!DHTPlugin.this.setSuspended(false))
/*      */                 {
/*  338 */                   Debug.out("Resume failed");
/*      */                 }
/*      */                 
/*  341 */                 return;
/*      */               }
/*  343 */               if (lc.equals("bridge_put"))
/*      */               {
/*      */                 try {
/*  346 */                   List<DistributedDatabase> ddbs = DHTPlugin.this.plugin_interface.getUtilities().getDistributedDatabases(new String[] { "I2P" });
/*      */                   
/*  348 */                   DistributedDatabase ddb = (DistributedDatabase)ddbs.get(0);
/*      */                   
/*  350 */                   DistributedDatabaseKey key = ddb.createKey("fred");
/*      */                   
/*  352 */                   key.setFlags(2);
/*      */                   
/*  354 */                   ddb.write(new DistributedDatabaseListener() { public void event(DistributedDatabaseEvent event) {} }, key, ddb.createValue("bill"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  365 */                   e.printStackTrace();
/*      */                 }
/*      */                 
/*  368 */                 return;
/*      */               }
/*      */               
/*  371 */               for (int i = 0; i < DHTPlugin.this.dhts.length; i++)
/*      */               {
/*  373 */                 DHT dht = DHTPlugin.this.dhts[i].getDHT();
/*      */                 
/*  375 */                 DHTTransportUDP transport = (DHTTransportUDP)dht.getTransport();
/*      */                 
/*  377 */                 if (lc.equals("print"))
/*      */                 {
/*  379 */                   dht.print(true);
/*      */                   
/*  381 */                   DHTPlugin.this.dhts[i].logStats();
/*      */                 }
/*  383 */                 else if (lc.equals("pingall"))
/*      */                 {
/*  385 */                   if (i == 1)
/*      */                   {
/*  387 */                     dht.getControl().pingAll();
/*      */                   }
/*      */                 }
/*  390 */                 else if (lc.equals("versions"))
/*      */                 {
/*  392 */                   List<DHTRouterContact> contacts = dht.getRouter().getAllContacts();
/*      */                   
/*  394 */                   Map<Byte, Integer> counts = new TreeMap();
/*      */                   
/*  396 */                   for (DHTRouterContact r : contacts)
/*      */                   {
/*  398 */                     DHTControlContact contact = (DHTControlContact)r.getAttachment();
/*      */                     
/*  400 */                     byte v = contact.getTransportContact().getProtocolVersion();
/*      */                     
/*  402 */                     Integer count = (Integer)counts.get(Byte.valueOf(v));
/*      */                     
/*  404 */                     if (count == null)
/*      */                     {
/*  406 */                       counts.put(Byte.valueOf(v), Integer.valueOf(1));
/*      */                     }
/*      */                     else
/*      */                     {
/*  410 */                       counts.put(Byte.valueOf(v), Integer.valueOf(count.intValue() + 1));
/*      */                     }
/*      */                   }
/*      */                   
/*  414 */                   DHTPlugin.this.log.log("Net " + dht.getTransport().getNetwork());
/*      */                   
/*  416 */                   int total = contacts.size();
/*      */                   
/*  418 */                   if (total == 0)
/*      */                   {
/*  420 */                     DHTPlugin.this.log.log("   no contacts");
/*      */                   }
/*      */                   else
/*      */                   {
/*  424 */                     String ver = "";
/*      */                     
/*  426 */                     for (Map.Entry<Byte, Integer> entry : counts.entrySet())
/*      */                     {
/*  428 */                       ver = ver + (ver.length() == 0 ? "" : ", ") + entry.getKey() + "=" + 100 * ((Integer)entry.getValue()).intValue() / total + "%";
/*      */                     }
/*      */                     
/*  431 */                     DHTPlugin.this.log.log("    contacts=" + total + ": " + ver);
/*      */                   }
/*  433 */                 } else if (lc.equals("testca"))
/*      */                 {
/*  435 */                   ((DHTTransportUDPImpl)transport).testExternalAddressChange();
/*      */                 }
/*  437 */                 else if (lc.equals("testnd"))
/*      */                 {
/*  439 */                   ((DHTTransportUDPImpl)transport).testNetworkAlive(false);
/*      */                 }
/*  441 */                 else if (lc.equals("testna"))
/*      */                 {
/*  443 */                   ((DHTTransportUDPImpl)transport).testNetworkAlive(true);
/*      */                 }
/*      */                 else
/*      */                 {
/*  447 */                   int pos = c.indexOf(' ');
/*      */                   
/*  449 */                   if (pos != -1)
/*      */                   {
/*  451 */                     String lhs = lc.substring(0, pos);
/*  452 */                     String rhs = c.substring(pos + 1);
/*      */                     
/*  454 */                     if (lhs.equals("set"))
/*      */                     {
/*  456 */                       pos = rhs.indexOf('=');
/*      */                       
/*  458 */                       if (pos != -1)
/*      */                       {
/*  460 */                         DHTPlugin.this.put(rhs.substring(0, pos).getBytes(), "DHT Plugin: set", rhs.substring(pos + 1).getBytes(), (byte)0, DHTPlugin.5.this.val$log_polistener);
/*      */ 
/*      */                       }
/*      */                       
/*      */ 
/*      */ 
/*      */                     }
/*  467 */                     else if (lhs.equals("get"))
/*      */                     {
/*  469 */                       DHTPlugin.this.get(rhs.getBytes("UTF-8"), "DHT Plugin: get", (byte)0, 1, 10000L, true, false, DHTPlugin.5.this.val$log_polistener);
/*      */ 
/*      */                     }
/*  472 */                     else if (lhs.equals("query"))
/*      */                     {
/*  474 */                       DHTPlugin.this.get(rhs.getBytes("UTF-8"), "DHT Plugin: get", (byte)8, 1, 10000L, true, false, DHTPlugin.5.this.val$log_polistener);
/*      */ 
/*      */                     }
/*  477 */                     else if (lhs.equals("punch"))
/*      */                     {
/*  479 */                       Map originator_data = new HashMap();
/*      */                       
/*  481 */                       originator_data.put("hello", "mum");
/*      */                       
/*  483 */                       DHTNATPuncher puncher = dht.getNATPuncher();
/*      */                       
/*  485 */                       if (puncher != null)
/*      */                       {
/*  487 */                         puncher.punch("Test", transport.getLocalContact(), null, originator_data);
/*      */                       }
/*  489 */                     } else if (lhs.equals("stats"))
/*      */                     {
/*      */                       try {
/*  492 */                         pos = rhs.lastIndexOf(":");
/*      */                         
/*      */                         DHTTransportContact contact;
/*      */                         DHTTransportContact contact;
/*  496 */                         if (pos == -1)
/*      */                         {
/*  498 */                           contact = transport.getLocalContact();
/*      */                         }
/*      */                         else
/*      */                         {
/*  502 */                           String host = rhs.substring(0, pos);
/*  503 */                           int port = Integer.parseInt(rhs.substring(pos + 1));
/*      */                           
/*  505 */                           contact = transport.importContact(new InetSocketAddress(host, port), transport.getProtocolVersion(), false);
/*      */                         }
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*  511 */                         DHTPlugin.this.log.log("Stats request to " + contact.getName());
/*      */                         
/*  513 */                         DHTTransportFullStats stats = contact.getStats();
/*      */                         
/*  515 */                         DHTPlugin.this.log.log("Stats:" + (stats == null ? "<null>" : stats.getString()));
/*      */                         
/*  517 */                         DHTControlActivity[] activities = dht.getControl().getActivities();
/*      */                         
/*  519 */                         for (int j = 0; j < activities.length; j++)
/*      */                         {
/*  521 */                           DHTPlugin.this.log.log("    act:" + activities[j].getString());
/*      */                         }
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/*  526 */                         Debug.printStackTrace(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  534 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/*  538 */         };
/*  539 */         t.start();
/*      */       }
/*      */       
/*  542 */     });
/*  543 */     this.reseed.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  550 */         DHTPlugin.this.reseed.setEnabled(false);
/*      */         
/*  552 */         AEThread2 t = new AEThread2("DHT:reseeder", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*  559 */               String ip = DHTPlugin.6.this.val$reseed_ip.getValue().trim();
/*      */               
/*  561 */               if (DHTPlugin.this.dhts == null) {
/*      */                 return;
/*      */               }
/*      */               
/*      */ 
/*  566 */               int port = DHTPlugin.6.this.val$reseed_port.getValue();
/*      */               
/*  568 */               for (int i = 0; i < DHTPlugin.this.dhts.length; i++)
/*      */               {
/*  570 */                 DHTPluginImpl dht = DHTPlugin.this.dhts[i];
/*      */                 
/*  572 */                 if ((ip.length() == 0) || (port == 0))
/*      */                 {
/*  574 */                   dht.checkForReSeed(true);
/*      */                 }
/*      */                 else
/*      */                 {
/*  578 */                   DHTTransportContact seed = dht.importSeed(ip, port);
/*      */                   
/*  580 */                   if (seed != null)
/*      */                   {
/*  582 */                     dht.integrateDHT(false, seed);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/*  589 */               DHTPlugin.this.reseed.setEnabled(true);
/*      */             }
/*      */             
/*      */           }
/*  593 */         };
/*  594 */         t.start();
/*      */       }
/*      */       
/*  597 */     });
/*  598 */     model.getActivity().setVisible(false);
/*  599 */     model.getProgress().setVisible(false);
/*      */     
/*  601 */     this.log.addListener(new LoggerChannelListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageLogged(int type, String message)
/*      */       {
/*      */ 
/*      */ 
/*  609 */         model.getLogArea().appendText(message + "\n");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageLogged(String str, Throwable error)
/*      */       {
/*  617 */         model.getLogArea().appendText(error.toString() + "\n");
/*      */       }
/*      */       
/*  620 */     });
/*  621 */     this.dht_log = new DHTLogger()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void log(String str)
/*      */       {
/*      */ 
/*  628 */         DHTPlugin.this.log.log(str);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void log(Throwable e)
/*      */       {
/*  635 */         DHTPlugin.this.log.log(e);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void log(int log_type, String str)
/*      */       {
/*  643 */         if (isEnabled(log_type))
/*      */         {
/*  645 */           DHTPlugin.this.log.log(str);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public boolean isEnabled(int log_type)
/*      */       {
/*  653 */         if (log_type == 2)
/*      */         {
/*  655 */           return DHTPlugin.this.ipfilter_logging[0];
/*      */         }
/*      */         
/*  658 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */       public PluginInterface getPluginInterface()
/*      */       {
/*  664 */         return DHTPlugin.this.log.getLogger().getPluginInterface();
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*  669 */     if (!enabled_param.getValue())
/*      */     {
/*  671 */       model.getStatus().setText("Disabled");
/*      */       
/*  673 */       this.status = 1;
/*      */       
/*  675 */       this.init_sem.releaseForever();
/*      */       
/*  677 */       return;
/*      */     }
/*      */     
/*  680 */     setPluginInfo();
/*      */     
/*  682 */     this.plugin_interface.addListener(new PluginListener()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*  688 */         PluginInterface pi_upnp = DHTPlugin.this.plugin_interface.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*      */         
/*  690 */         if (pi_upnp == null)
/*      */         {
/*  692 */           DHTPlugin.this.log.log("UPnP plugin not found, can't map port");
/*      */         }
/*      */         else
/*      */         {
/*  696 */           DHTPlugin.this.upnp_mapping = ((UPnPPlugin)pi_upnp.getPlugin()).addMapping(DHTPlugin.this.plugin_interface.getPluginName(), false, DHTPlugin.this.dht_data_port, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  703 */         String ip = null;
/*      */         
/*  705 */         if (advanced.getValue())
/*      */         {
/*  707 */           ip = override_ip.getValue().trim();
/*      */           
/*  709 */           if (ip.length() == 0)
/*      */           {
/*  711 */             ip = null;
/*      */           }
/*      */         }
/*      */         
/*  715 */         DHTPlugin.this.initComplete(model.getStatus(), logging.getValue(), ip);
/*      */       }
/*      */       
/*      */ 
/*      */       public void closedownInitiated()
/*      */       {
/*  721 */         if (DHTPlugin.this.dhts != null)
/*      */         {
/*  723 */           for (int i = 0; i < DHTPlugin.this.dhts.length; i++)
/*      */           {
/*  725 */             DHTPlugin.this.dhts[i].closedownInitiated();
/*      */           }
/*      */         }
/*      */         
/*  729 */         DHTPlugin.this.saveClockSkew();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*  737 */     });
/*  738 */     int sample_frequency = 60000;
/*  739 */     int sample_stats_ticks = 15;
/*      */     
/*  741 */     this.plugin_interface.getUtilities().createTimer("DHTStats", true).addPeriodicEvent(60000L, new UTTimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(UTTimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  749 */         if (DHTPlugin.this.dhts != null)
/*      */         {
/*  751 */           for (int i = 0; i < DHTPlugin.this.dhts.length; i++)
/*      */           {
/*  753 */             DHTPlugin.this.dhts[i].updateStats(15);
/*      */           }
/*      */         }
/*      */         
/*  757 */         DHTPlugin.this.setPluginInfo();
/*      */         
/*  759 */         DHTPlugin.this.saveClockSkew();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void changePort(int _new_port)
/*      */   {
/*      */     try
/*      */     {
/*  773 */       this.port_change_mon.enter();
/*      */       
/*  775 */       this.port_change_outstanding = _new_port;
/*      */       
/*  777 */       if (this.port_changing) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  782 */       this.port_changing = true;
/*      */     }
/*      */     finally
/*      */     {
/*  786 */       this.port_change_mon.exit();
/*      */     }
/*      */     
/*  789 */     new AEThread2("DHTPlugin:portChanger", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         for (;;)
/*      */         {
/*      */           int new_port;
/*      */           
/*      */           try
/*      */           {
/*  799 */             DHTPlugin.this.port_change_mon.enter();
/*      */             
/*  801 */             new_port = DHTPlugin.this.port_change_outstanding;
/*      */           }
/*      */           finally
/*      */           {
/*  805 */             DHTPlugin.this.port_change_mon.exit();
/*      */           }
/*      */           try
/*      */           {
/*  809 */             DHTPlugin.this.dht_data_port = new_port;
/*      */             
/*  811 */             if (DHTPlugin.this.upnp_mapping != null)
/*      */             {
/*  813 */               if (DHTPlugin.this.upnp_mapping.getPort() != new_port)
/*      */               {
/*  815 */                 DHTPlugin.this.upnp_mapping.setPort(new_port);
/*      */               }
/*      */             }
/*      */             
/*  819 */             if (DHTPlugin.this.status == 3)
/*      */             {
/*  821 */               if (DHTPlugin.this.dhts != null)
/*      */               {
/*  823 */                 for (int i = 0; i < DHTPlugin.this.dhts.length; i++)
/*      */                 {
/*  825 */                   if (DHTPlugin.this.dhts[i].getPort() != new_port)
/*      */                   {
/*  827 */                     DHTPlugin.this.dhts[i].setPort(new_port);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/*      */             try {
/*  835 */               DHTPlugin.this.port_change_mon.enter();
/*      */               
/*  837 */               if (new_port == DHTPlugin.this.port_change_outstanding)
/*      */               {
/*  839 */                 DHTPlugin.this.port_changing = false;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  846 */                 DHTPlugin.this.port_change_mon.exit(); break; } } finally { DHTPlugin.this.port_change_mon.exit();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initComplete(final UITextField status_area, final boolean logging, final String override_ip)
/*      */   {
/*  860 */     AEThread2 t = new AEThread2("DHTPlugin.init", true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  866 */         boolean went_async = false;
/*      */         
/*      */         try
/*      */         {
/*  870 */           DHTPlugin.this.enabled = VersionCheckClient.getSingleton().DHTEnableAllowed();
/*      */           
/*  872 */           if (DHTPlugin.this.enabled)
/*      */           {
/*  874 */             status_area.setText("Initialising");
/*      */             
/*  876 */             DelayedTask dt = DHTPlugin.this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/*  883 */                 new AEThread2("DHTPlugin.init2", true)
/*      */                 {
/*      */                   public void run()
/*      */                   {
/*      */                     try
/*      */                     {
/*  889 */                       List plugins = new ArrayList();
/*      */                       
/*      */ 
/*      */ 
/*  893 */                       DHTPluginImplAdapter adapter = new DHTPluginImplAdapter()
/*      */                       {
/*      */ 
/*      */ 
/*      */                         public void localContactChanged(DHTPluginContact local_contact)
/*      */                         {
/*      */ 
/*  900 */                           for (int i = 0; i < DHTPlugin.this.listeners.size(); i++)
/*      */                           {
/*  902 */                             ((DHTPluginListener)DHTPlugin.this.listeners.get(i)).localAddressChanged(local_contact);
/*      */                           }
/*      */                         }
/*      */                       };
/*      */                       
/*  907 */                       if (DHTPlugin.MAIN_DHT_ENABLE)
/*      */                       {
/*  909 */                         DHTPlugin.this.main_dht = new DHTPluginImpl(DHTPlugin.this.plugin_interface, AzureusCoreFactory.getSingleton().getNATTraverser(), adapter, DHTTransportUDP.PROTOCOL_VERSION_MAIN, 0, false, DHTPlugin.12.this.val$override_ip, DHTPlugin.this.dht_data_port, DHTPlugin.this.reseed, DHTPlugin.this.warn_user, DHTPlugin.12.this.val$logging, DHTPlugin.this.log, DHTPlugin.this.dht_log);
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  924 */                         plugins.add(DHTPlugin.this.main_dht);
/*      */                         
/*  926 */                         adapter = null;
/*      */                       }
/*      */                       
/*  929 */                       if (DHTPlugin.MAIN_DHT_V6_ENABLE)
/*      */                       {
/*  931 */                         if (NetworkAdmin.getSingleton().hasDHTIPV6())
/*      */                         {
/*  933 */                           DHTPlugin.this.main_v6_dht = new DHTPluginImpl(DHTPlugin.this.plugin_interface, AzureusCoreFactory.getSingleton().getNATTraverser(), adapter, DHTTransportUDP.PROTOCOL_VERSION_MAIN, 3, true, null, DHTPlugin.this.dht_data_port, DHTPlugin.this.reseed, DHTPlugin.this.warn_user, DHTPlugin.12.this.val$logging, DHTPlugin.this.log, DHTPlugin.this.dht_log);
/*      */                           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  948 */                           plugins.add(DHTPlugin.this.main_v6_dht);
/*      */                           
/*  950 */                           adapter = null;
/*      */                         }
/*      */                       }
/*      */                       
/*  954 */                       if ((Constants.isCVSVersion()) && (DHTPlugin.CVS_DHT_ENABLE))
/*      */                       {
/*  956 */                         DHTPlugin.this.cvs_dht = new DHTPluginImpl(DHTPlugin.this.plugin_interface, AzureusCoreFactory.getSingleton().getNATTraverser(), adapter, DHTTransportUDP.PROTOCOL_VERSION_CVS, 1, false, DHTPlugin.12.this.val$override_ip, DHTPlugin.this.dht_data_port, DHTPlugin.this.reseed, DHTPlugin.this.warn_user, DHTPlugin.12.this.val$logging, DHTPlugin.this.log, DHTPlugin.this.dht_log);
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  971 */                         plugins.add(DHTPlugin.this.cvs_dht);
/*      */                         
/*  973 */                         adapter = null;
/*      */                       }
/*      */                       
/*  976 */                       DHTPluginImpl[] _dhts = new DHTPluginImpl[plugins.size()];
/*      */                       
/*  978 */                       plugins.toArray(_dhts);
/*      */                       
/*  980 */                       DHTPlugin.this.dhts = _dhts;
/*      */                       
/*  982 */                       DHTPlugin.this.status = DHTPlugin.this.dhts[0].getStatus();
/*      */                       
/*  984 */                       DHTPlugin.12.this.val$status_area.setText(DHTPlugin.this.dhts[0].getStatusText());
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/*  988 */                       DHTPlugin.this.enabled = false;
/*      */                       
/*  990 */                       DHTPlugin.this.status = 1;
/*      */                       
/*  992 */                       DHTPlugin.12.this.val$status_area.setText("Disabled due to error during initialisation");
/*      */                       
/*  994 */                       DHTPlugin.this.log.log(e);
/*      */                       
/*  996 */                       Debug.printStackTrace(e);
/*      */                     }
/*      */                     finally
/*      */                     {
/* 1000 */                       DHTPlugin.this.init_sem.releaseForever();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/* 1005 */                     if (DHTPlugin.this.status == 3)
/*      */                     {
/* 1007 */                       DHTPlugin.this.changePort(DHTPlugin.this.dht_data_port);
/*      */                     }
/*      */                     
/*      */                   }
/*      */                 }.start();
/*      */               }
/* 1013 */             });
/* 1014 */             dt.queue();
/*      */             
/* 1016 */             went_async = true;
/*      */           }
/*      */           else
/*      */           {
/* 1020 */             DHTPlugin.this.status = 1;
/*      */             
/* 1022 */             status_area.setText("Disabled administratively due to network problems");
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 1026 */           DHTPlugin.this.enabled = false;
/*      */           
/* 1028 */           DHTPlugin.this.status = 1;
/*      */           
/* 1030 */           status_area.setText("Disabled due to error during initialisation");
/*      */           
/* 1032 */           DHTPlugin.this.log.log(e);
/*      */           
/* 1034 */           Debug.printStackTrace(e);
/*      */         }
/*      */         finally
/*      */         {
/* 1038 */           if (!went_async)
/*      */           {
/* 1040 */             DHTPlugin.this.init_sem.releaseForever();
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 1045 */     };
/* 1046 */     t.start();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setPluginInfo()
/*      */   {
/* 1052 */     boolean reachable = this.plugin_interface.getPluginconfig().getPluginBooleanParameter("dht.reachable.0", true);
/*      */     
/* 1054 */     this.plugin_interface.getPluginconfig().setPluginParameter("plugin.info", reachable ? "1" : "0");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/* 1062 */     if (this.plugin_interface == null)
/*      */     {
/* 1064 */       Debug.out("Called too early!");
/*      */       
/* 1066 */       return false;
/*      */     }
/*      */     
/* 1069 */     if (this.plugin_interface.isInitialisationThread())
/*      */     {
/* 1071 */       if (!this.init_sem.isReleasedForever())
/*      */       {
/* 1073 */         Debug.out("Initialisation deadlock detected");
/*      */         
/* 1075 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 1079 */     this.init_sem.reserve();
/*      */     
/* 1081 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean peekEnabled()
/*      */   {
/* 1087 */     if (this.init_sem.isReleasedForever())
/*      */     {
/* 1089 */       return this.enabled;
/*      */     }
/*      */     
/* 1092 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInitialising()
/*      */   {
/* 1098 */     return !this.init_sem.isReleasedForever();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean setSuspended(boolean susp)
/*      */   {
/* 1105 */     if (!this.init_sem.isReleasedForever())
/*      */     {
/* 1107 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1111 */     synchronized (this)
/*      */     {
/* 1113 */       for (DHTPluginImpl dht : this.dhts)
/*      */       {
/* 1115 */         dht.setSuspended(susp);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1120 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isExtendedUseAllowed()
/*      */   {
/* 1126 */     if (!isEnabled())
/*      */     {
/* 1128 */       return false;
/*      */     }
/*      */     
/* 1131 */     if (!this.got_extended_use)
/*      */     {
/* 1133 */       this.got_extended_use = true;
/*      */       
/* 1135 */       this.extended_use = VersionCheckClient.getSingleton().DHTExtendedUseAllowed();
/*      */     }
/*      */     
/* 1138 */     return this.extended_use;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNetwork()
/*      */   {
/* 1144 */     return "Public";
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isReachable()
/*      */   {
/* 1150 */     if (!isEnabled())
/*      */     {
/* 1152 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1155 */     return this.dhts[0].isReachable();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDiversified(byte[] key)
/*      */   {
/* 1162 */     if (!isEnabled())
/*      */     {
/* 1164 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1167 */     return this.dhts[0].isDiversified(key);
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
/* 1178 */     put(key, description, value, flags, true, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void put(byte[] key, String description, byte[] value, byte flags, boolean high_priority, final DHTPluginOperationListener listener)
/*      */   {
/* 1190 */     if (!isEnabled())
/*      */     {
/* 1192 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1195 */     if (this.dhts.length == 1)
/*      */     {
/* 1197 */       this.dhts[0].put(key, description, value, flags, high_priority, listener);
/*      */     }
/*      */     else
/*      */     {
/* 1201 */       final int[] completes_to_go = { this.dhts.length };
/*      */       
/* 1203 */       DHTPluginOperationListener main_listener = new DHTPluginOperationListener()
/*      */       {
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/*      */ 
/* 1209 */           return listener.diversified();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key)
/*      */         {
/* 1216 */           listener.starts(key);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/* 1224 */           listener.valueRead(originator, value);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueWritten(DHTPluginContact target, DHTPluginValue value)
/*      */         {
/* 1232 */           listener.valueWritten(target, value);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(byte[] key, boolean timeout_occurred)
/*      */         {
/* 1240 */           synchronized (completes_to_go)
/*      */           {
/* 1242 */             completes_to_go[0] -= 1;
/*      */             
/* 1244 */             if (completes_to_go[0] == 0)
/*      */             {
/* 1246 */               listener.complete(key, timeout_occurred);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 1251 */       };
/* 1252 */       this.dhts[0].put(key, description, value, flags, high_priority, main_listener);
/*      */       
/* 1254 */       for (int i = 1; i < this.dhts.length; i++)
/*      */       {
/* 1256 */         this.dhts[i].put(key, description, value, flags, high_priority, new DHTPluginOperationListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean diversified()
/*      */           {
/*      */ 
/* 1263 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void starts(byte[] key) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void complete(byte[] key, boolean timeout_occurred)
/*      */           {
/* 1291 */             synchronized (completes_to_go)
/*      */             {
/* 1293 */               completes_to_go[0] -= 1;
/*      */               
/* 1295 */               if (completes_to_go[0] == 0)
/*      */               {
/* 1297 */                 listener.complete(key, timeout_occurred);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTPluginValue getLocalValue(byte[] key)
/*      */   {
/* 1310 */     if (this.main_dht != null)
/*      */     {
/* 1312 */       return this.main_dht.getLocalValue(key);
/*      */     }
/* 1314 */     if (this.cvs_dht != null)
/*      */     {
/* 1316 */       return this.cvs_dht.getLocalValue(key);
/*      */     }
/* 1318 */     if (this.main_v6_dht != null)
/*      */     {
/* 1320 */       return this.main_v6_dht.getLocalValue(key);
/*      */     }
/*      */     
/*      */ 
/* 1324 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<DHTPluginValue> getValues()
/*      */   {
/* 1331 */     if (this.main_dht != null)
/*      */     {
/* 1333 */       return this.main_dht.getValues();
/*      */     }
/* 1335 */     if (this.cvs_dht != null)
/*      */     {
/* 1337 */       return this.cvs_dht.getValues();
/*      */     }
/* 1339 */     if (this.main_v6_dht != null)
/*      */     {
/* 1341 */       return this.main_v6_dht.getValues();
/*      */     }
/*      */     
/* 1344 */     return new ArrayList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<DHTPluginValue> getValues(byte[] key)
/*      */   {
/* 1352 */     if (this.main_dht != null)
/*      */     {
/* 1354 */       return this.main_dht.getValues(key);
/*      */     }
/* 1356 */     if (this.cvs_dht != null)
/*      */     {
/* 1358 */       return this.cvs_dht.getValues(key);
/*      */     }
/* 1360 */     if (this.main_v6_dht != null)
/*      */     {
/* 1362 */       return this.main_v6_dht.getValues(key);
/*      */     }
/*      */     
/* 1365 */     return new ArrayList();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List<DHTPluginValue> getValues(int network, boolean ipv6)
/*      */   {
/* 1374 */     DHTPluginImpl dht = null;
/*      */     
/* 1376 */     if (network == 0)
/*      */     {
/* 1378 */       if (ipv6)
/*      */       {
/* 1380 */         dht = this.main_v6_dht;
/*      */       }
/*      */       else
/*      */       {
/* 1384 */         dht = this.main_dht;
/*      */       }
/*      */       
/*      */     }
/* 1388 */     else if (!ipv6)
/*      */     {
/* 1390 */       dht = this.cvs_dht;
/*      */     }
/*      */     
/*      */ 
/* 1394 */     if (dht == null)
/*      */     {
/* 1396 */       return new ArrayList();
/*      */     }
/*      */     
/*      */ 
/* 1400 */     return dht.getValues();
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
/*      */   public void get(final byte[] original_key, String description, byte flags, int max_values, final long timeout, boolean exhaustive, boolean high_priority, final DHTPluginOperationListener original_listener)
/*      */   {
/* 1415 */     if (!isEnabled())
/*      */     {
/* 1417 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/*      */     DHTPluginOperationListener main_listener;
/*      */     final DHTPluginOperationListener main_listener;
/* 1422 */     if (this.cvs_dht == null)
/*      */     {
/* 1424 */       main_listener = original_listener;
/*      */     }
/*      */     else
/*      */     {
/* 1428 */       if ((this.main_dht == null) && (this.main_v6_dht == null))
/*      */       {
/*      */ 
/*      */ 
/* 1432 */         this.cvs_dht.get(original_key, description, flags, max_values, timeout, exhaustive, high_priority, original_listener);
/*      */         
/* 1434 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1439 */       final int[] completes_to_go = { 2 };
/* 1440 */       final boolean[] main_timeout = { false };
/*      */       
/* 1442 */       main_listener = new DHTPluginOperationListener()
/*      */       {
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/*      */ 
/* 1448 */           return original_listener.diversified();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key)
/*      */         {
/* 1455 */           original_listener.starts(original_key);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/* 1463 */           original_listener.valueRead(originator, value);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueWritten(DHTPluginContact target, DHTPluginValue value)
/*      */         {
/* 1471 */           original_listener.valueWritten(target, value);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(byte[] key, boolean timeout_occurred)
/*      */         {
/* 1479 */           synchronized (completes_to_go)
/*      */           {
/* 1481 */             completes_to_go[0] -= 1;
/*      */             
/* 1483 */             main_timeout[0] = timeout_occurred;
/*      */             
/* 1485 */             if (completes_to_go[0] == 0)
/*      */             {
/* 1487 */               original_listener.complete(original_key, timeout_occurred);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 1492 */       };
/* 1493 */       this.cvs_dht.get(original_key, description, flags, max_values, timeout, exhaustive, high_priority, new DHTPluginOperationListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/*      */ 
/* 1500 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(byte[] key, boolean timeout_occurred)
/*      */         {
/* 1528 */           synchronized (completes_to_go)
/*      */           {
/* 1530 */             completes_to_go[0] -= 1;
/*      */             
/* 1532 */             if (completes_to_go[0] == 0)
/*      */             {
/* 1534 */               original_listener.complete(original_key, main_timeout[0]);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1541 */     if ((this.main_dht != null) && (this.main_v6_dht == null))
/*      */     {
/* 1543 */       this.main_dht.get(original_key, description, flags, max_values, timeout, exhaustive, high_priority, main_listener);
/*      */     }
/* 1545 */     else if ((this.main_dht == null) && (this.main_v6_dht != null))
/*      */     {
/* 1547 */       this.main_v6_dht.get(original_key, description, flags, max_values, timeout, exhaustive, high_priority, main_listener);
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/* 1555 */       byte[] v4_key = original_key;
/* 1556 */       final byte[] v6_key = (byte[])original_key.clone();
/*      */       
/* 1558 */       DHTPluginOperationListener dual_listener = new DHTPluginOperationListener()
/*      */       {
/*      */ 
/* 1561 */         private long start_time = SystemTime.getCurrentTime();
/*      */         
/*      */         private boolean started;
/*      */         
/* 1565 */         private int complete_count = 0;
/* 1566 */         private int result_count = 0;
/*      */         
/*      */ 
/*      */         public boolean diversified()
/*      */         {
/* 1571 */           return main_listener.diversified();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void starts(byte[] key)
/*      */         {
/* 1578 */           synchronized (this)
/*      */           {
/* 1580 */             if (this.started)
/*      */             {
/* 1582 */               return;
/*      */             }
/*      */             
/* 1585 */             this.started = true;
/*      */           }
/*      */           
/* 1588 */           main_listener.starts(original_key);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueRead(DHTPluginContact originator, DHTPluginValue value)
/*      */         {
/* 1596 */           synchronized (this)
/*      */           {
/* 1598 */             this.result_count += 1;
/*      */             
/*      */ 
/*      */ 
/* 1602 */             if (this.complete_count < 2)
/*      */             {
/* 1604 */               main_listener.valueRead(originator, value);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void valueWritten(DHTPluginContact target, DHTPluginValue value)
/*      */         {
/* 1614 */           Debug.out("eh?");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void complete(final byte[] timeout_key, boolean timeout_occurred)
/*      */         {
/* 1624 */           synchronized (this)
/*      */           {
/* 1626 */             this.complete_count += 1;
/*      */             
/* 1628 */             if (this.complete_count == 2)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1633 */               main_listener.complete(original_key, this.result_count > 0 ? false : timeout_occurred);
/*      */               
/* 1635 */               return;
/*      */             }
/* 1637 */             if (this.complete_count > 2)
/*      */             {
/* 1639 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1646 */             if (timeout_occurred)
/*      */             {
/* 1648 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1653 */             if (timeout_key == v6_key)
/*      */             {
/* 1655 */               return;
/*      */             }
/*      */             
/* 1658 */             long now = SystemTime.getCurrentTime();
/*      */             
/* 1660 */             long elapsed = now - this.start_time;
/*      */             
/* 1662 */             long rem = timeout - elapsed;
/*      */             
/* 1664 */             if (rem <= 0L)
/*      */             {
/* 1666 */               complete(timeout_key, true);
/*      */             }
/*      */             else
/*      */             {
/* 1670 */               SimpleTimer.addEvent("DHTPlugin:dual_dht_early_timeout", now + rem, new TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/*      */ 
/* 1679 */                   DHTPlugin.17.this.complete(timeout_key, true);
/*      */                 }
/*      */                 
/*      */ 
/*      */               });
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/* 1689 */       };
/* 1690 */       this.main_dht.get(v4_key, description, flags, max_values, timeout, exhaustive, high_priority, dual_listener);
/*      */       
/* 1692 */       this.main_v6_dht.get(v6_key, description, flags, max_values, timeout, exhaustive, high_priority, dual_listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasLocalKey(byte[] hash)
/*      */   {
/* 1700 */     if (!isEnabled())
/*      */     {
/* 1702 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1705 */     return this.dhts[0].getLocalValue(hash) != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void remove(final byte[] key, final String description, DHTPluginOperationListener listener)
/*      */   {
/* 1714 */     if (!isEnabled())
/*      */     {
/* 1716 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1719 */     this.dhts[0].remove(key, description, listener);
/*      */     
/* 1721 */     for (int i = 1; i < this.dhts.length; i++)
/*      */     {
/* 1723 */       final int f_i = i;
/*      */       
/* 1725 */       new AEThread2("multi-dht: remove", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 1730 */           DHTPlugin.this.dhts[f_i].remove(key, description, new DHTPluginOperationListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public boolean diversified()
/*      */             {
/*      */ 
/* 1737 */               return true;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void starts(byte[] key) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void complete(byte[] key, boolean timeout_occurred) {}
/*      */           });
/*      */         }
/*      */       }.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void remove(DHTPluginContact[] targets, byte[] key, String description, DHTPluginOperationListener listener)
/*      */   {
/* 1779 */     if (!isEnabled())
/*      */     {
/* 1781 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1784 */     Map dht_map = new HashMap();
/*      */     
/* 1786 */     for (int i = 0; i < targets.length; i++)
/*      */     {
/* 1788 */       DHTPluginContactImpl target = (DHTPluginContactImpl)targets[i];
/*      */       
/* 1790 */       DHTPluginImpl dht = target.getDHT();
/*      */       
/* 1792 */       List c = (List)dht_map.get(dht);
/*      */       
/* 1794 */       if (c == null)
/*      */       {
/* 1796 */         c = new ArrayList();
/*      */         
/* 1798 */         dht_map.put(dht, c);
/*      */       }
/*      */       
/* 1801 */       c.add(target);
/*      */     }
/*      */     
/* 1804 */     Iterator it = dht_map.entrySet().iterator();
/*      */     
/* 1806 */     boolean primary = true;
/*      */     
/* 1808 */     while (it.hasNext())
/*      */     {
/* 1810 */       Map.Entry entry = (Map.Entry)it.next();
/*      */       
/* 1812 */       DHTPluginImpl dht = (DHTPluginImpl)entry.getKey();
/* 1813 */       List contacts = (List)entry.getValue();
/*      */       
/* 1815 */       DHTPluginContact[] dht_targets = new DHTPluginContact[contacts.size()];
/*      */       
/* 1817 */       contacts.toArray(dht_targets);
/*      */       
/* 1819 */       if (primary)
/*      */       {
/* 1821 */         primary = false;
/*      */         
/* 1823 */         dht.remove(dht_targets, key, description, listener);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1829 */         dht.remove(dht_targets, key, description, new DHTPluginOperationListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean diversified()
/*      */           {
/*      */ 
/* 1836 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void starts(byte[] key) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueRead(DHTPluginContact originator, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void valueWritten(DHTPluginContact target, DHTPluginValue value) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void complete(byte[] key, boolean timeout_occurred) {}
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTPluginContact importContact(Map<String, Object> map)
/*      */   {
/* 1874 */     if (!isEnabled())
/*      */     {
/* 1876 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1881 */     return this.dhts[0].importContact(map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTPluginContact importContact(InetSocketAddress address)
/*      */   {
/* 1888 */     if (!isEnabled())
/*      */     {
/* 1890 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1895 */     return this.dhts[0].importContact(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTPluginContact importContact(InetSocketAddress address, byte version)
/*      */   {
/* 1903 */     if (!isEnabled())
/*      */     {
/* 1905 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1908 */     InetAddress contact_address = address.getAddress();
/*      */     
/* 1910 */     for (DHTPluginImpl dht : this.dhts)
/*      */     {
/* 1912 */       InetAddress dht_address = dht.getLocalAddress().getAddress().getAddress();
/*      */       
/* 1914 */       if ((((contact_address instanceof Inet4Address)) && ((dht_address instanceof Inet4Address))) || (((contact_address instanceof Inet6Address)) && ((dht_address instanceof Inet6Address))))
/*      */       {
/*      */ 
/* 1917 */         return dht.importContact(address, version);
/*      */       }
/*      */     }
/*      */     
/* 1921 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTPluginContact importContact(InetSocketAddress address, byte version, boolean is_cvs)
/*      */   {
/* 1930 */     if (!isEnabled())
/*      */     {
/* 1932 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1935 */     InetAddress contact_address = address.getAddress();
/*      */     
/* 1937 */     int target_network = is_cvs ? 1 : 0;
/*      */     
/* 1939 */     for (DHTPluginImpl dht : this.dhts)
/*      */     {
/* 1941 */       if (dht.getDHT().getTransport().getNetwork() == target_network)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1946 */         InetAddress dht_address = dht.getLocalAddress().getAddress().getAddress();
/*      */         
/* 1948 */         if ((((contact_address instanceof Inet4Address)) && ((dht_address instanceof Inet4Address))) || (((contact_address instanceof Inet6Address)) && ((dht_address instanceof Inet6Address))))
/*      */         {
/*      */ 
/* 1951 */           return dht.importContact(address, version);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1957 */     return importContact(address, version);
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginContact getLocalAddress()
/*      */   {
/* 1963 */     if (!isEnabled())
/*      */     {
/* 1965 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1970 */     return this.dhts[0].getLocalAddress();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerHandler(byte[] handler_key, DHTPluginTransferHandler handler, Map<String, Object> options)
/*      */   {
/* 1981 */     if (!isEnabled())
/*      */     {
/* 1983 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 1986 */     for (int i = 0; i < this.dhts.length; i++)
/*      */     {
/* 1988 */       this.dhts[i].registerHandler(handler_key, handler, options);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void unregisterHandler(byte[] handler_key, DHTPluginTransferHandler handler)
/*      */   {
/* 1997 */     if (!isEnabled())
/*      */     {
/* 1999 */       throw new RuntimeException("DHT isn't enabled");
/*      */     }
/*      */     
/* 2002 */     for (int i = 0; i < this.dhts.length; i++)
/*      */     {
/* 2004 */       this.dhts[i].unregisterHandler(handler_key, handler);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getStatus()
/*      */   {
/* 2011 */     return this.status;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSleeping()
/*      */   {
/* 2017 */     return AERunStateHandler.isDHTSleeping();
/*      */   }
/*      */   
/*      */ 
/*      */   public DHT[] getDHTs()
/*      */   {
/* 2023 */     if (this.dhts == null)
/*      */     {
/* 2025 */       return new DHT[0];
/*      */     }
/*      */     
/* 2028 */     DHT[] res = new DHT[this.dhts.length];
/*      */     
/* 2030 */     for (int i = 0; i < res.length; i++)
/*      */     {
/* 2032 */       res[i] = this.dhts[i].getDHT();
/*      */     }
/*      */     
/* 2035 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHT getDHT(int network)
/*      */   {
/* 2042 */     if (this.dhts == null)
/*      */     {
/* 2044 */       return null;
/*      */     }
/*      */     
/* 2047 */     for (int i = 0; i < this.dhts.length; i++)
/*      */     {
/* 2049 */       if (this.dhts[i].getDHT().getTransport().getNetwork() == network)
/*      */       {
/* 2051 */         return this.dhts[i].getDHT();
/*      */       }
/*      */     }
/*      */     
/* 2055 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTPluginInterface.DHTInterface[] getDHTInterfaces()
/*      */   {
/* 2061 */     if (this.dhts == null)
/*      */     {
/* 2063 */       return new DHTPluginInterface.DHTInterface[0];
/*      */     }
/*      */     
/* 2066 */     DHTPluginInterface.DHTInterface[] result = new DHTPluginInterface.DHTInterface[this.dhts.length];
/*      */     
/* 2068 */     System.arraycopy(this.dhts, 0, result, 0, this.dhts.length);
/*      */     
/* 2070 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long loadClockSkew()
/*      */   {
/* 2076 */     return this.plugin_interface.getPluginconfig().getPluginLongParameter("dht.skew", 0L);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveClockSkew()
/*      */   {
/* 2082 */     long existing = loadClockSkew();
/* 2083 */     long current = getClockSkew();
/*      */     
/* 2085 */     if (Math.abs(existing - current) > 5000L)
/*      */     {
/* 2087 */       this.plugin_interface.getPluginconfig().setPluginParameter("dht.skew", getClockSkew());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getClockSkew()
/*      */   {
/* 2094 */     if ((this.dhts == null) || (this.dhts.length == 0))
/*      */     {
/* 2096 */       return 0L;
/*      */     }
/*      */     
/* 2099 */     long uptime = SystemTime.getMonotonousTime() - this.start_mono_time;
/*      */     
/* 2101 */     if (uptime < 300000L)
/*      */     {
/* 2103 */       return loadClockSkew();
/*      */     }
/*      */     
/* 2106 */     long skew = this.dhts[0].getClockSkew();
/*      */     
/* 2108 */     if (skew > 86400000L)
/*      */     {
/* 2110 */       skew = 0L;
/*      */     }
/*      */     
/* 2113 */     skew = skew / 500L * 500L;
/*      */     
/* 2115 */     return skew;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTPluginKeyStats decodeStats(DHTPluginValue value)
/*      */   {
/* 2122 */     return this.dhts[0].decodeStats(value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DHTPluginListener l)
/*      */   {
/* 2129 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DHTPluginListener l)
/*      */   {
/* 2136 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(String str)
/*      */   {
/* 2143 */     this.log.log(str);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/dht/DHTPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */