/*      */ package com.aelitis.azureus.core.pairing.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstance;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminHTTPProxy;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterface;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterfaceAddress;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSocksProxy;
/*      */ import com.aelitis.azureus.core.pairing.PairedNode;
/*      */ import com.aelitis.azureus.core.pairing.PairedService;
/*      */ import com.aelitis.azureus.core.pairing.PairedServiceRequestHandler;
/*      */ import com.aelitis.azureus.core.pairing.PairingConnectionData;
/*      */ import com.aelitis.azureus.core.pairing.PairingException;
/*      */ import com.aelitis.azureus.core.pairing.PairingManager;
/*      */ import com.aelitis.azureus.core.pairing.PairingManagerListener;
/*      */ import com.aelitis.azureus.core.pairing.PairingTest;
/*      */ import com.aelitis.azureus.core.pairing.PairingTestListener;
/*      */ import com.aelitis.azureus.core.security.CryptoHandler;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*      */ import com.aelitis.azureus.plugins.upnp.UPnPPluginService;
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import com.aelitis.net.upnp.UPnPRootDevice;
/*      */ import com.aelitis.net.upnp.UPnPService;
/*      */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketTimeoutException;
/*      */ import java.net.URL;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AEVerifier;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
/*      */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.config.ActionParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.HyperlinkParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.InfoParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.LabelParameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*      */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*      */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*      */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
/*      */ 
/*      */ public class PairingManagerImpl
/*      */   implements PairingManager, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final boolean DEBUG = false;
/*      */   private static final String SERVICE_URL;
/*      */   
/*      */   static
/*      */   {
/*  113 */     String url = System.getProperty("az.pairing.url", "");
/*      */     
/*  115 */     if (url.length() == 0)
/*      */     {
/*  117 */       SERVICE_URL = "https://pair.vuze.com/pairing";
/*      */     }
/*      */     else
/*      */     {
/*  121 */       SERVICE_URL = url;
/*      */     }
/*      */   }
/*      */   
/*  125 */   private static final PairingManagerImpl singleton = new PairingManagerImpl();
/*      */   private static final int GLOBAL_UPDATE_PERIOD = 60000;
/*      */   private static final int CD_REFRESH_PERIOD = 82800000;
/*      */   
/*      */   public static PairingManager getSingleton() {
/*  130 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */   private static final int CD_REFRESH_TICKS = 1380;
/*      */   
/*      */   private static final int CONNECT_TEST_PERIOD_MILLIS = 1800000;
/*      */   
/*      */   private AzureusCore azureus_core;
/*      */   
/*      */   final BooleanParameter param_enable;
/*      */   
/*      */   private final InfoParameter param_ac_info;
/*      */   
/*      */   private final InfoParameter param_status_info;
/*      */   
/*      */   private final InfoParameter param_last_error;
/*      */   
/*      */   private final HyperlinkParameter param_view;
/*      */   
/*      */   final BooleanParameter param_srp_enable;
/*      */   
/*      */   private final LabelParameter param_srp_state;
/*      */   
/*      */   private final BooleanParameter param_e_enable;
/*      */   
/*      */   private final StringParameter param_public_ipv4;
/*      */   
/*      */   private final StringParameter param_public_ipv6;
/*      */   private final StringParameter param_host;
/*      */   private final BooleanParameter param_net_enable;
/*      */   private final StringParameter param_local_ipv4;
/*      */   private final StringParameter param_local_ipv6;
/*      */   private final BooleanParameter param_icon_enable;
/*  164 */   private final Map<String, PairedServiceImpl> services = new HashMap();
/*      */   
/*  166 */   private final AESemaphore init_sem = new AESemaphore("PM:init");
/*      */   
/*      */   private TimerEventPeriodic global_update_event;
/*      */   
/*      */   private InetAddress current_v4;
/*      */   
/*      */   private InetAddress current_v6;
/*  173 */   private String local_v4 = "";
/*  174 */   private String local_v6 = "";
/*      */   
/*      */   private PairingManagerTunnelHandler tunnel_handler;
/*      */   
/*      */   private boolean update_outstanding;
/*      */   
/*      */   private boolean updates_enabled;
/*      */   
/*      */   private static final int MIN_UPDATE_PERIOD_DEFAULT = 10000;
/*      */   private static final int MAX_UPDATE_PERIOD_DEFAULT = 3600000;
/*  184 */   private int min_update_period = 10000;
/*  185 */   private int max_update_period = 3600000;
/*      */   
/*      */ 
/*  188 */   private final AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */   
/*      */   private boolean must_update_once;
/*      */   private boolean update_in_progress;
/*      */   private TimerEvent deferred_update_event;
/*  193 */   private long last_update_time = -1L;
/*      */   
/*      */   private int consec_update_fails;
/*  196 */   private long qr_version = COConfigurationManager.getLongParameter("pairing.qr.ver", 0L);
/*      */   
/*      */   private String last_message;
/*      */   
/*  200 */   final Map<String, Object[]> local_address_checks = new HashMap();
/*      */   
/*  202 */   private final CopyOnWriteList<PairingManagerListener> listeners = new CopyOnWriteList();
/*      */   
/*      */   private UIAdapter ui;
/*      */   
/*  206 */   private int tests_in_progress = 0;
/*      */   
/*      */ 
/*      */   protected PairingManagerImpl()
/*      */   {
/*  211 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     try
/*      */     {
/*  214 */       this.ui = ((UIAdapter)Class.forName("com.aelitis.azureus.core.pairing.impl.swt.PMSWTImpl").newInstance());
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*  220 */     this.must_update_once = COConfigurationManager.getBooleanParameter("pairing.updateoutstanding");
/*      */     
/*  222 */     PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */     
/*  224 */     final UIManager ui_manager = default_pi.getUIManager();
/*      */     
/*  226 */     BasicPluginConfigModel configModel = ui_manager.createBasicPluginConfigModel("server", "Pairing");
/*      */     
/*      */ 
/*  229 */     configModel.addHyperlinkParameter2("ConfigView.label.please.visit.here", MessageText.getString("ConfigView.section.connection.pairing.url"));
/*      */     
/*  231 */     this.param_enable = configModel.addBooleanParameter2("pairing.enable", "pairing.enable", false);
/*      */     
/*  233 */     String access_code = readAccessCode();
/*      */     
/*  235 */     this.param_ac_info = configModel.addInfoParameter2("pairing.accesscode", access_code);
/*      */     
/*  237 */     this.param_status_info = configModel.addInfoParameter2("pairing.status.info", "");
/*      */     
/*  239 */     this.param_last_error = configModel.addInfoParameter2("pairing.last.error", "");
/*      */     
/*  241 */     this.param_view = configModel.addHyperlinkParameter2("pairing.view.registered", SERVICE_URL + "/web/view?ac=" + access_code);
/*      */     
/*  243 */     if (access_code.length() == 0)
/*      */     {
/*  245 */       this.param_view.setEnabled(false);
/*      */     }
/*      */     
/*  248 */     COConfigurationManager.registerExportedParameter("pairing.enable", this.param_enable.getConfigKeyName());
/*  249 */     COConfigurationManager.registerExportedParameter("pairing.access_code", this.param_ac_info.getConfigKeyName());
/*      */     
/*  251 */     final ActionParameter ap = configModel.addActionParameter2("pairing.ac.getnew", "pairing.ac.getnew.create");
/*      */     
/*  253 */     ap.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  261 */           ap.setEnabled(false);
/*      */           
/*  263 */           PairingManagerImpl.this.allocateAccessCode(false);
/*      */           
/*  265 */           SimpleTimer.addEvent("PM:enabler", SystemTime.getOffsetTime(30000L), new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/*  274 */               PairingManagerImpl.1.this.val$ap.setEnabled(true);
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  280 */           ap.setEnabled(true);
/*      */           
/*  282 */           String details = MessageText.getString("pairing.alloc.fail", new String[] { Debug.getNestedExceptionMessage(e) });
/*      */           
/*      */ 
/*      */ 
/*  286 */           ui_manager.showMessageBox("pairing.op.fail", "!" + details + "!", 1L);
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  295 */     });
/*  296 */     LabelParameter param_srp_info = configModel.addLabelParameter2("pairing.srp.info");
/*      */     
/*  298 */     HyperlinkParameter param_srp_link = configModel.addHyperlinkParameter2("label.more.info.here", MessageText.getString("ConfigView.section.connection.pairing.srp.url"));
/*      */     
/*  300 */     this.param_srp_enable = configModel.addBooleanParameter2("pairing.srp.enable", "pairing.srp.enable", false);
/*      */     
/*  302 */     COConfigurationManager.registerExportedParameter("pairing.srp_enable", this.param_srp_enable.getConfigKeyName());
/*      */     
/*  304 */     this.param_srp_state = configModel.addLabelParameter2("");
/*      */     
/*  306 */     updateSRPState();
/*      */     
/*  308 */     final ActionParameter param_srp_set = configModel.addActionParameter2("pairing.srp.setpw", "pairing.srp.setpw.doit");
/*      */     
/*  310 */     param_srp_set.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  317 */         param_srp_set.setEnabled(false);
/*      */         
/*  319 */         new AEThread2("getpw")
/*      */         {
/*      */           public void run()
/*      */           {
/*      */             try
/*      */             {
/*  325 */               if (PairingManagerImpl.this.ui != null)
/*      */               {
/*  327 */                 char[] password = PairingManagerImpl.this.ui.getSRPPassword();
/*      */                 
/*  329 */                 if (password != null)
/*      */                 {
/*  331 */                   PairingManagerImpl.this.tunnel_handler.setSRPPassword(password);
/*      */                 }
/*      */               }
/*      */               else {
/*  335 */                 Debug.out("No UI available");
/*      */               }
/*      */             }
/*      */             finally {
/*  339 */               PairingManagerImpl.2.this.val$param_srp_set.setEnabled(true);
/*      */             }
/*      */             
/*      */           }
/*      */         }.start();
/*      */       }
/*  345 */     });
/*  346 */     this.param_srp_enable.addListener(new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  353 */         boolean active = PairingManagerImpl.this.param_srp_enable.getValue();
/*      */         
/*  355 */         PairingManagerImpl.this.tunnel_handler.setActive(active);
/*      */         
/*  357 */         PairingManagerImpl.this.updateSRPState();
/*      */       }
/*      */       
/*  360 */     });
/*  361 */     this.param_srp_enable.addEnabledOnSelection(this.param_srp_state);
/*  362 */     this.param_srp_enable.addEnabledOnSelection(param_srp_set);
/*      */     
/*  364 */     configModel.createGroup("pairing.group.srp", new Parameter[] { param_srp_info, param_srp_link, this.param_srp_enable, this.param_srp_state, param_srp_set });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  376 */     this.param_net_enable = configModel.addBooleanParameter2("pairing.nets.enable", "pairing.nets.enable", false);
/*      */     
/*  378 */     configModel.createGroup("pairing.group.optional", new Parameter[] { this.param_net_enable });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  385 */     LabelParameter param_e_info = configModel.addLabelParameter2("pairing.explicit.info");
/*      */     
/*  387 */     this.param_e_enable = configModel.addBooleanParameter2("pairing.explicit.enable", "pairing.explicit.enable", false);
/*      */     
/*  389 */     this.param_public_ipv4 = configModel.addStringParameter2("pairing.ipv4", "pairing.ipv4", "");
/*  390 */     this.param_public_ipv6 = configModel.addStringParameter2("pairing.ipv6", "pairing.ipv6", "");
/*  391 */     this.param_host = configModel.addStringParameter2("pairing.host", "pairing.host", "");
/*      */     
/*  393 */     LabelParameter spacer = configModel.addLabelParameter2("blank.resource");
/*      */     
/*  395 */     this.param_local_ipv4 = configModel.addStringParameter2("pairing.local.ipv4", "pairing.local.ipv4", "");
/*  396 */     this.param_local_ipv6 = configModel.addStringParameter2("pairing.local.ipv6", "pairing.local.ipv6", "");
/*      */     
/*      */ 
/*  399 */     this.param_public_ipv4.setGenerateIntermediateEvents(false);
/*  400 */     this.param_public_ipv6.setGenerateIntermediateEvents(false);
/*  401 */     this.param_host.setGenerateIntermediateEvents(false);
/*      */     
/*  403 */     ParameterListener change_listener = new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(Parameter param)
/*      */       {
/*      */ 
/*  410 */         PairingManagerImpl.this.updateNeeded();
/*      */         
/*  412 */         if (param == PairingManagerImpl.this.param_enable)
/*      */         {
/*  414 */           PairingManagerImpl.this.fireChanged();
/*      */         }
/*      */         
/*      */       }
/*  418 */     };
/*  419 */     this.param_enable.addListener(change_listener);
/*  420 */     this.param_e_enable.addListener(change_listener);
/*  421 */     this.param_public_ipv4.addListener(change_listener);
/*  422 */     this.param_public_ipv6.addListener(change_listener);
/*  423 */     this.param_local_ipv4.addListener(change_listener);
/*  424 */     this.param_local_ipv6.addListener(change_listener);
/*  425 */     this.param_host.addListener(change_listener);
/*  426 */     this.param_net_enable.addListener(change_listener);
/*      */     
/*  428 */     this.param_e_enable.addEnabledOnSelection(this.param_public_ipv4);
/*  429 */     this.param_e_enable.addEnabledOnSelection(this.param_public_ipv6);
/*  430 */     this.param_e_enable.addEnabledOnSelection(this.param_local_ipv4);
/*  431 */     this.param_e_enable.addEnabledOnSelection(this.param_local_ipv6);
/*  432 */     this.param_e_enable.addEnabledOnSelection(this.param_host);
/*      */     
/*  434 */     configModel.createGroup("pairing.group.explicit", new Parameter[] { param_e_info, this.param_e_enable, this.param_public_ipv4, this.param_public_ipv6, this.param_host, spacer, this.param_local_ipv4, this.param_local_ipv6 });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  447 */     this.param_icon_enable = configModel.addBooleanParameter2("pairing.config.icon.show", "pairing.config.icon.show", true);
/*      */     
/*  449 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void azureusCoreRunning(AzureusCore core)
/*      */       {
/*      */ 
/*  456 */         PairingManagerImpl.this.initialise(core);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initialise(AzureusCore _core)
/*      */   {
/*  466 */     synchronized (this)
/*      */     {
/*  468 */       this.azureus_core = _core;
/*      */     }
/*      */     try
/*      */     {
/*  472 */       this.tunnel_handler = new PairingManagerTunnelHandler(this, this.azureus_core);
/*      */       
/*  474 */       PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */       
/*  476 */       DelayedTask dt = default_pi.getUtilities().createDelayedTask(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*  482 */           new DelayedEvent("PM:delayinit", 10000L, new AERunnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/*      */ 
/*  490 */               PairingManagerImpl.this.enableUpdates();
/*      */             }
/*      */             
/*      */           });
/*      */         }
/*  495 */       });
/*  496 */       dt.queue();
/*      */       
/*  498 */       if (this.ui != null) {
/*      */         try
/*      */         {
/*  501 */           this.ui.initialise(default_pi, this.param_icon_enable);
/*      */ 
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  509 */       this.init_sem.releaseForever();
/*      */       
/*  511 */       updateSRPState();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void waitForInitialisation()
/*      */     throws PairingException
/*      */   {
/*  520 */     if (!this.init_sem.reserve(30000L))
/*      */     {
/*  522 */       throw new PairingException("Timeout waiting for initialisation");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  529 */     return this.param_enable.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean enabled)
/*      */   {
/*  536 */     this.param_enable.setValue(enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSRPEnabled()
/*      */   {
/*  542 */     return this.param_srp_enable.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSRPEnabled(boolean enabled)
/*      */   {
/*  549 */     this.param_srp_enable.setValue(enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setGroup(String group)
/*      */   {
/*  556 */     COConfigurationManager.setParameter("pairing.groupcode", group);
/*      */     
/*  558 */     updateNeeded();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getGroup()
/*      */   {
/*  564 */     return COConfigurationManager.getStringParameter("pairing.groupcode", null);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<PairedNode> listGroup()
/*      */     throws PairingException
/*      */   {
/*      */     try
/*      */     {
/*  573 */       URL url = new URL(SERVICE_URL + "/remote/listGroup?gc=" + getGroup());
/*      */       
/*  575 */       InputStream is = new ResourceDownloaderFactoryImpl().create(url).download();
/*      */       
/*  577 */       Map json = JSONUtils.decodeJSON(new String(FileUtil.readInputStreamAsByteArray(is), "UTF-8"));
/*      */       
/*  579 */       List<Map> list = (List)json.get("result");
/*      */       
/*  581 */       List<PairedNode> result = new ArrayList();
/*      */       
/*  583 */       String my_ac = peekAccessCode();
/*      */       
/*  585 */       if (list != null)
/*      */       {
/*  587 */         for (Map m : list)
/*      */         {
/*  589 */           PairedNodeImpl node = new PairedNodeImpl(m);
/*      */           
/*  591 */           if ((my_ac == null) || (!my_ac.equals(node.getAccessCode())))
/*      */           {
/*  593 */             result.add(node);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  598 */       return result;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  602 */       throw new PairingException("Failed to list group", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<PairedService> lookupServices(String access_code)
/*      */     throws PairingException
/*      */   {
/*      */     try
/*      */     {
/*  613 */       URL url = new URL(SERVICE_URL + "/remote/listBindings?ac=" + access_code + "&jsoncallback=");
/*      */       
/*  615 */       InputStream is = new ResourceDownloaderFactoryImpl().create(url).download();
/*      */       
/*  617 */       String reply = new String(FileUtil.readInputStreamAsByteArray(is), "UTF-8");
/*      */       
/*      */ 
/*      */ 
/*  621 */       reply = reply.substring(1, reply.length() - 1);
/*      */       
/*  623 */       Map json = JSONUtils.decodeJSON(reply);
/*      */       
/*  625 */       Map error = (Map)json.get("error");
/*      */       
/*  627 */       if (error != null)
/*      */       {
/*  629 */         throw new PairingException((String)error.get("msg"));
/*      */       }
/*      */       
/*  632 */       List<Map> list = (List)json.get("result");
/*      */       
/*  634 */       List<PairedService> result = new ArrayList();
/*      */       
/*  636 */       if (list != null)
/*      */       {
/*  638 */         for (Map m : list)
/*      */         {
/*  640 */           result.add(new PairedService2Impl((String)m.get("sid"), m));
/*      */         }
/*      */       }
/*      */       
/*  644 */       return result;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  648 */       throw new PairingException("Failed to lookup services", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setStatus(String str)
/*      */   {
/*  656 */     String last_status = this.param_status_info.getValue();
/*      */     
/*  658 */     if (!last_status.equals(str))
/*      */     {
/*  660 */       this.param_status_info.setValue(str);
/*      */       
/*  662 */       fireChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatus()
/*      */   {
/*  669 */     return this.param_status_info.getValue();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSRPStatus()
/*      */   {
/*  675 */     if (!isSRPEnabled())
/*      */     {
/*  677 */       return "Not enabled";
/*      */     }
/*  679 */     if (this.tunnel_handler == null)
/*      */     {
/*  681 */       return "Initialising";
/*      */     }
/*      */     
/*  684 */     return this.tunnel_handler.getStatus();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setLastServerError(String error)
/*      */   {
/*  692 */     String last_error = this.param_last_error.getValue();
/*      */     
/*  694 */     if (error == null)
/*      */     {
/*  696 */       error = "";
/*      */     }
/*      */     
/*  699 */     if (!last_error.equals(error))
/*      */     {
/*  701 */       this.param_last_error.setValue(error);
/*      */       
/*  703 */       if (error.contains("generate a new one"))
/*      */       {
/*  705 */         Logger.log(new LogAlert(true, 1, "The pairing access code is invalid.\n\nCreate a new one via Tools->Options->Connection->Pairing or disable the pairing feature."));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  712 */       fireChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getLastServerError()
/*      */   {
/*  719 */     String last_error = this.param_last_error.getValue();
/*      */     
/*  721 */     if (last_error.length() == 0)
/*      */     {
/*  723 */       last_error = null;
/*      */     }
/*      */     
/*  726 */     return last_error;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasActionOutstanding()
/*      */   {
/*  732 */     synchronized (this)
/*      */     {
/*  734 */       if (!isEnabled())
/*      */       {
/*  736 */         return false;
/*      */       }
/*      */       
/*  739 */       return (!this.updates_enabled) || (this.update_outstanding) || (this.deferred_update_event != null) || (this.update_in_progress);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected String readAccessCode()
/*      */   {
/*  746 */     return COConfigurationManager.getStringParameter("pairing.accesscode", "");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void writeAccessCode(String ac)
/*      */   {
/*  753 */     COConfigurationManager.setParameter("pairing.accesscode", ac);
/*      */     
/*      */ 
/*      */ 
/*  757 */     COConfigurationManager.save();
/*      */     
/*  759 */     this.param_ac_info.setValue(ac);
/*      */     
/*  761 */     this.param_view.setHyperlink(SERVICE_URL + "/web/view?ac=" + ac);
/*      */     
/*  763 */     this.param_view.setEnabled(ac.length() > 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private File receiveQR(String ac, Map<String, Object> response)
/*      */   {
/*      */     try
/*      */     {
/*  772 */       byte[] bytes = (byte[])response.get("qr_b");
/*      */       
/*  774 */       if (bytes == null)
/*      */       {
/*  776 */         return null;
/*      */       }
/*      */       
/*  779 */       long ver = ((Long)response.get("qr_v")).longValue();
/*      */       
/*  781 */       File cache_dir = new File(SystemProperties.getUserPath(), "cache");
/*      */       
/*  783 */       File qr_file = new File(cache_dir, "qr_" + ac + "_" + ver + ".png");
/*      */       
/*  785 */       if (FileUtil.writeBytesAsFile2(qr_file.getAbsolutePath(), bytes))
/*      */       {
/*  787 */         return qr_file;
/*      */       }
/*      */       
/*  790 */       return null;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  794 */       Debug.out(e);
/*      */     }
/*  796 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public File getQRCode()
/*      */   {
/*  805 */     String existing = readAccessCode();
/*      */     
/*  807 */     if (existing == null)
/*      */     {
/*  809 */       return null;
/*      */     }
/*      */     
/*  812 */     if (this.qr_version > 0L)
/*      */     {
/*  814 */       File cache_dir = new File(SystemProperties.getUserPath(), "cache");
/*      */       
/*  816 */       File qr_file = new File(cache_dir, "qr_" + existing + "_" + this.qr_version + ".png");
/*      */       
/*  818 */       if (qr_file.exists())
/*      */       {
/*  820 */         return qr_file;
/*      */       }
/*      */     }
/*      */     
/*  824 */     Map<String, Object> request = new HashMap();
/*      */     
/*  826 */     request.put("ac", existing);
/*      */     
/*      */     try
/*      */     {
/*  830 */       Map<String, Object> response = sendRequest("get_qr", request);
/*      */       
/*  832 */       return receiveQR(existing, response);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  836 */       Debug.out(e);
/*      */     }
/*  838 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String allocateAccessCode(boolean updating)
/*      */     throws PairingException
/*      */   {
/*  848 */     Map<String, Object> request = new HashMap();
/*      */     
/*  850 */     String existing = readAccessCode();
/*      */     
/*  852 */     request.put("ac", existing);
/*  853 */     request.put("qr", Long.valueOf(1L));
/*      */     
/*  855 */     Map<String, Object> response = sendRequest("allocate", request);
/*      */     try
/*      */     {
/*  858 */       String code = getString(response, "ac");
/*      */       
/*  860 */       receiveQR(code, response);
/*      */       
/*  862 */       writeAccessCode(code);
/*      */       
/*  864 */       if (!updating)
/*      */       {
/*  866 */         updateNeeded();
/*      */       }
/*      */       
/*  869 */       fireChanged();
/*      */       
/*  871 */       return code;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  875 */       throw new PairingException("allocation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String peekAccessCode()
/*      */   {
/*  882 */     return readAccessCode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getAccessCode()
/*      */     throws PairingException
/*      */   {
/*  890 */     waitForInitialisation();
/*      */     
/*  892 */     String ac = readAccessCode();
/*      */     
/*  894 */     if ((ac == null) || (ac.length() == 0))
/*      */     {
/*  896 */       ac = allocateAccessCode(false);
/*      */     }
/*      */     
/*  899 */     return ac;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void getAccessCode(final PairingManagerListener listener)
/*      */     throws PairingException
/*      */   {
/*  908 */     new AEThread2("PM:gac", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*  914 */           PairingManagerImpl.this.getAccessCode();
/*      */ 
/*      */         }
/*      */         catch (Throwable e) {}finally
/*      */         {
/*      */ 
/*  920 */           listener.somethingChanged(PairingManagerImpl.this);
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getReplacementAccessCode()
/*      */     throws PairingException
/*      */   {
/*  931 */     waitForInitialisation();
/*      */     
/*  933 */     String new_code = allocateAccessCode(false);
/*      */     
/*  935 */     return new_code;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public PairedService addService(String sid, PairedServiceRequestHandler handler)
/*      */   {
/*  943 */     synchronized (this)
/*      */     {
/*  945 */       PairedServiceImpl result = (PairedServiceImpl)this.services.get(sid);
/*      */       
/*  947 */       if (result == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  953 */         result = new PairedServiceImpl(sid, handler);
/*      */         
/*  955 */         this.services.put(sid, result);
/*      */       }
/*      */       else {
/*  958 */         result.setHandler(handler);
/*      */       }
/*      */       
/*  961 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PairedServiceImpl getService(String sid)
/*      */   {
/*  969 */     synchronized (this)
/*      */     {
/*  971 */       PairedServiceImpl result = (PairedServiceImpl)this.services.get(sid);
/*      */       
/*  973 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void remove(PairedServiceImpl service)
/*      */   {
/*  981 */     synchronized (this)
/*      */     {
/*  983 */       String sid = service.getSID();
/*      */       
/*  985 */       if (this.services.remove(sid) == null) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  993 */     updateNeeded();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void sync(PairedServiceImpl service)
/*      */   {
/* 1000 */     updateNeeded();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected InetAddress updateAddress(InetAddress current, InetAddress latest, boolean v6)
/*      */   {
/* 1009 */     if (v6)
/*      */     {
/* 1011 */       if ((latest instanceof Inet4Address))
/*      */       {
/* 1013 */         return current;
/*      */       }
/*      */       
/*      */     }
/* 1017 */     else if ((latest instanceof Inet6Address))
/*      */     {
/* 1019 */       return current;
/*      */     }
/*      */     
/*      */ 
/* 1023 */     if (current == latest)
/*      */     {
/* 1025 */       return current;
/*      */     }
/*      */     
/* 1028 */     if ((current == null) || (latest == null))
/*      */     {
/* 1030 */       return latest;
/*      */     }
/*      */     
/* 1033 */     if (!current.equals(latest))
/*      */     {
/* 1035 */       return latest;
/*      */     }
/*      */     
/* 1038 */     return current;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateGlobals(boolean is_updating)
/*      */   {
/* 1045 */     final long now = SystemTime.getMonotonousTime();
/*      */     
/* 1047 */     NetworkAdmin network_admin = NetworkAdmin.getSingleton();
/*      */     
/* 1049 */     InetAddress latest_v4 = this.azureus_core.getInstanceManager().getMyInstance().getExternalAddress();
/*      */     
/* 1051 */     synchronized (this)
/*      */     {
/* 1053 */       InetAddress temp_v4 = updateAddress(this.current_v4, latest_v4, false);
/*      */       
/* 1055 */       InetAddress latest_v6 = network_admin.getDefaultPublicAddressV6();
/*      */       
/* 1057 */       InetAddress temp_v6 = updateAddress(this.current_v6, latest_v6, true);
/*      */       
/* 1059 */       TreeSet<String> latest_v4_locals = new TreeSet();
/* 1060 */       final TreeSet<String> latest_v6_locals = new TreeSet();
/*      */       
/* 1062 */       NetworkAdminNetworkInterface[] interfaces = network_admin.getInterfaces();
/*      */       
/* 1064 */       List<Runnable> to_do = new ArrayList();
/*      */       
/*      */       Set<String> existing_checked;
/*      */       
/* 1068 */       synchronized (this.local_address_checks)
/*      */       {
/* 1070 */         existing_checked = new HashSet(this.local_address_checks.keySet());
/*      */       }
/*      */       
/* 1073 */       for (NetworkAdminNetworkInterface intf : interfaces)
/*      */       {
/* 1075 */         NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
/*      */         
/* 1077 */         for (NetworkAdminNetworkInterfaceAddress address : addresses)
/*      */         {
/* 1079 */           final InetAddress ia = address.getAddress();
/*      */           
/* 1081 */           if (!ia.isLoopbackAddress())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1086 */             if ((ia.isLinkLocalAddress()) || (ia.isSiteLocalAddress()))
/*      */             {
/* 1088 */               final String a_str = ia.getHostAddress();
/*      */               
/* 1090 */               existing_checked.remove(a_str);
/*      */               
/*      */               Object[] check;
/*      */               
/* 1094 */               synchronized (this.local_address_checks)
/*      */               {
/* 1096 */                 check = (Object[])this.local_address_checks.get(a_str);
/*      */               }
/*      */               
/* 1099 */               boolean run_check = (check == null) || (now - ((Long)check[0]).longValue() > 1800000L);
/*      */               
/* 1101 */               if (run_check)
/*      */               {
/* 1103 */                 to_do.add(new Runnable()
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*      */ 
/* 1109 */                     Socket socket = new Socket();
/*      */                     
/* 1111 */                     String result = a_str;
/*      */                     try
/*      */                     {
/* 1114 */                       socket.bind(new InetSocketAddress(ia, 0));
/*      */                       
/* 1116 */                       socket.connect(new InetSocketAddress("www.google.com", 80), 10000);
/*      */                       
/* 1118 */                       result = result + "*";
/*      */                       
/*      */ 
/*      */ 
/*      */                       try
/*      */                       {
/* 1124 */                         socket.close();
/*      */                       }
/*      */                       catch (Throwable e) {}
/*      */                       
/*      */ 
/*      */ 
/* 1130 */                       synchronized (PairingManagerImpl.this.local_address_checks)
/*      */                       {
/* 1132 */                         PairingManagerImpl.this.local_address_checks.put(a_str, new Object[] { new Long(now), result });
/*      */                         
/* 1134 */                         if ((ia instanceof Inet4Address))
/*      */                         {
/* 1136 */                           latest_v6_locals.add(result);
/*      */                         }
/*      */                         else
/*      */                         {
/* 1140 */                           this.val$latest_v6_locals.add(result);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {}finally
/*      */                     {
/*      */                       try
/*      */                       {
/* 1124 */                         socket.close();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                       }
/*      */                       catch (Throwable e) {}
/*      */ 
/*      */ 
/*      */ 
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */                 });
/*      */ 
/*      */ 
/*      */               }
/* 1148 */               else if ((ia instanceof Inet4Address))
/*      */               {
/* 1150 */                 latest_v4_locals.add((String)check[1]);
/*      */               }
/*      */               else
/*      */               {
/* 1154 */                 latest_v6_locals.add((String)check[1]);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1161 */       if (to_do.size() > 0)
/*      */       {
/* 1163 */         final AESemaphore sem = new AESemaphore("PM:check");
/*      */         
/* 1165 */         for (final Runnable r : to_do)
/*      */         {
/* 1167 */           new AEThread2("PM:check:", true)
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try
/*      */               {
/* 1173 */                 r.run();
/*      */               }
/*      */               finally
/*      */               {
/* 1177 */                 sem.release();
/*      */               }
/*      */             }
/*      */           }.start();
/*      */         }
/*      */         
/* 1183 */         for (int i = 0; i < to_do.size(); i++)
/*      */         {
/* 1185 */           sem.reserve();
/*      */         }
/*      */       }
/*      */       
/* 1189 */       synchronized (this.local_address_checks)
/*      */       {
/* 1191 */         for (String excess : existing_checked)
/*      */         {
/* 1193 */           this.local_address_checks.remove(excess);
/*      */         }
/*      */       }
/*      */       
/* 1197 */       String v4_locals_str = getString(latest_v4_locals);
/* 1198 */       String v6_locals_str = getString(latest_v6_locals);
/*      */       
/*      */ 
/* 1201 */       if ((temp_v4 != this.current_v4) || (temp_v6 != this.current_v6) || (!v4_locals_str.equals(this.local_v4)) || (!v6_locals_str.equals(this.local_v6)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1206 */         this.current_v4 = temp_v4;
/* 1207 */         this.current_v6 = temp_v6;
/* 1208 */         this.local_v4 = v4_locals_str;
/* 1209 */         this.local_v6 = v6_locals_str;
/*      */         
/* 1211 */         if (!is_updating)
/*      */         {
/* 1213 */           updateNeeded();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getString(Set<String> set)
/*      */   {
/* 1224 */     String str = "";
/*      */     
/* 1226 */     for (String s : set)
/*      */     {
/* 1228 */       str = str + (str.length() == 0 ? "" : ",") + s;
/*      */     }
/*      */     
/* 1231 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void enableUpdates()
/*      */   {
/* 1237 */     synchronized (this)
/*      */     {
/* 1239 */       this.updates_enabled = true;
/*      */       
/* 1241 */       if (this.update_outstanding)
/*      */       {
/* 1243 */         this.update_outstanding = false;
/*      */         
/* 1245 */         updateNeeded();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateNeeded()
/*      */   {
/* 1257 */     synchronized (this)
/*      */     {
/* 1259 */       if (this.updates_enabled)
/*      */       {
/* 1261 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 1267 */             PairingManagerImpl.this.doUpdate();
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*      */       else
/*      */       {
/* 1274 */         setStatus(MessageText.getString("pairing.status.initialising"));
/*      */         
/* 1276 */         this.update_outstanding = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void doUpdate()
/*      */   {
/* 1284 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 1286 */     synchronized (this)
/*      */     {
/* 1288 */       if (this.deferred_update_event != null)
/*      */       {
/* 1290 */         return;
/*      */       }
/*      */       
/* 1293 */       long time_since_last_update = now - this.last_update_time;
/*      */       
/* 1295 */       if ((this.last_update_time > 0L) && (time_since_last_update < this.min_update_period))
/*      */       {
/* 1297 */         deferUpdate(this.min_update_period - time_since_last_update);
/*      */         
/* 1299 */         return;
/*      */       }
/*      */       
/* 1302 */       this.update_in_progress = true;
/*      */     }
/*      */     try
/*      */     {
/* 1306 */       Map<String, Object> payload = new HashMap();
/*      */       
/* 1308 */       boolean is_enabled = this.param_enable.getValue();
/*      */       
/* 1310 */       boolean has_services = false;
/*      */       
/* 1312 */       synchronized (this)
/*      */       {
/* 1314 */         List<Map<String, String>> list = new ArrayList();
/*      */         
/* 1316 */         payload.put("s", list);
/*      */         
/* 1318 */         if ((this.services.size() > 0) && (is_enabled))
/*      */         {
/* 1320 */           if (this.global_update_event == null)
/*      */           {
/* 1322 */             this.global_update_event = SimpleTimer.addPeriodicEvent("PM:updater", 60000L, new TimerEventPerformer()
/*      */             {
/*      */               private int tick_count;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void perform(TimerEvent event)
/*      */               {
/* 1334 */                 this.tick_count += 1;
/*      */                 
/* 1336 */                 PairingManagerImpl.this.updateGlobals(false);
/*      */                 
/* 1338 */                 if (this.tick_count % 1380 == 0)
/*      */                 {
/* 1340 */                   PairingManagerImpl.this.updateNeeded();
/*      */                 }
/*      */                 
/*      */               }
/* 1344 */             });
/* 1345 */             updateGlobals(true);
/*      */           }
/*      */           
/* 1348 */           boolean enable_nets = this.param_net_enable.getValue();
/*      */           
/* 1350 */           for (PairedServiceImpl service : this.services.values())
/*      */           {
/* 1352 */             list.add(service.toMap(enable_nets));
/*      */           }
/*      */           
/* 1355 */           has_services = list.size() > 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/* 1362 */         else if (this.global_update_event == null)
/*      */         {
/* 1364 */           if ((this.consec_update_fails == 0) && (!this.must_update_once))
/*      */           {
/* 1366 */             this.update_in_progress = false;
/*      */             
/* 1368 */             setStatus(MessageText.getString(is_enabled ? "pairing.status.noservices" : "pairing.status.disabled"));
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/* 1374 */           this.global_update_event.cancel();
/*      */           
/* 1376 */           this.global_update_event = null;
/*      */         }
/*      */         
/*      */ 
/* 1380 */         this.last_update_time = now;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1385 */       String ac = readAccessCode();
/*      */       
/* 1387 */       if (ac.length() == 0)
/*      */       {
/* 1389 */         ac = allocateAccessCode(true);
/*      */       }
/*      */       
/* 1392 */       payload.put("ac", ac);
/*      */       
/* 1394 */       String gc = getGroup();
/*      */       
/* 1396 */       if ((gc != null) && (gc.length() > 0))
/*      */       {
/* 1398 */         payload.put("gc", gc);
/*      */       }
/*      */       
/* 1401 */       if ((is_enabled) && (has_services) && (this.param_srp_enable.getValue()))
/*      */       {
/* 1403 */         this.tunnel_handler.setActive(true);
/*      */         
/* 1405 */         this.tunnel_handler.updateRegistrationData(payload);
/*      */       }
/*      */       else
/*      */       {
/* 1409 */         this.tunnel_handler.setActive(false);
/*      */       }
/*      */       
/* 1412 */       synchronized (this)
/*      */       {
/* 1414 */         if (this.current_v4 != null)
/*      */         {
/* 1416 */           payload.put("c_v4", this.current_v4.getHostAddress());
/*      */         }
/*      */         
/* 1419 */         if (this.current_v6 != null)
/*      */         {
/* 1421 */           payload.put("c_v6", this.current_v6.getHostAddress());
/*      */         }
/*      */         
/* 1424 */         if (this.local_v4.length() > 0)
/*      */         {
/* 1426 */           payload.put("l_v4", this.local_v4);
/*      */         }
/*      */         
/* 1429 */         if (this.local_v6.length() > 0)
/*      */         {
/* 1431 */           payload.put("l_v6", this.local_v6);
/*      */         }
/*      */         
/* 1434 */         if (this.param_e_enable.getValue())
/*      */         {
/* 1436 */           String host = this.param_host.getValue().trim();
/*      */           
/* 1438 */           if (host.length() > 0)
/*      */           {
/* 1440 */             payload.put("e_h", host);
/*      */           }
/*      */           
/* 1443 */           String v4 = this.param_public_ipv4.getValue().trim();
/*      */           
/* 1445 */           if (v4.length() > 0)
/*      */           {
/* 1447 */             payload.put("e_v4", v4);
/*      */           }
/*      */           
/* 1450 */           String v6 = this.param_public_ipv6.getValue().trim();
/*      */           
/* 1452 */           if (v6.length() > 0)
/*      */           {
/* 1454 */             payload.put("e_v6", v6);
/*      */           }
/*      */           
/* 1457 */           String l_v4 = this.param_local_ipv4.getValue().trim();
/*      */           
/* 1459 */           if (l_v4.length() > 0)
/*      */           {
/* 1461 */             payload.put("e_l_v4", l_v4);
/*      */           }
/*      */           
/* 1464 */           String l_v6 = this.param_local_ipv6.getValue().trim();
/*      */           
/* 1466 */           if (l_v6.length() > 0)
/*      */           {
/* 1468 */             payload.put("e_l_v6", l_v6);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 1475 */           PluginInterface pi_upnp = this.azureus_core.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*      */           
/* 1477 */           if (pi_upnp != null)
/*      */           {
/* 1479 */             UPnPPlugin upnp = (UPnPPlugin)pi_upnp.getPlugin();
/*      */             
/* 1481 */             if (upnp.isEnabled())
/*      */             {
/* 1483 */               Object upnp_list = new ArrayList();
/*      */               
/* 1485 */               payload.put("upnp", upnp_list);
/*      */               
/* 1487 */               UPnPPluginService[] services = upnp.getServices();
/*      */               
/* 1489 */               Set<UPnPRootDevice> devices = new HashSet();
/*      */               
/* 1491 */               for (UPnPPluginService service : services)
/*      */               {
/*      */ 
/*      */ 
/* 1495 */                 if (((List)upnp_list).size() > 10) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 1500 */                 UPnPRootDevice root_device = service.getService().getGenericService().getDevice().getRootDevice();
/*      */                 
/* 1502 */                 if (!devices.contains(root_device))
/*      */                 {
/* 1504 */                   devices.add(root_device);
/*      */                   
/* 1506 */                   Map<String, String> map = new HashMap();
/*      */                   
/* 1508 */                   ((List)upnp_list).add(map);
/*      */                   
/* 1510 */                   map.put("i", root_device.getInfo());
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         try
/*      */         {
/* 1519 */           NetworkAdmin admin = NetworkAdmin.getSingleton();
/*      */           
/* 1521 */           NetworkAdminHTTPProxy http_proxy = admin.getHTTPProxy();
/*      */           
/* 1523 */           if (http_proxy != null)
/*      */           {
/* 1525 */             payload.put("hp", http_proxy.getName());
/*      */           }
/*      */           
/* 1528 */           NetworkAdminSocksProxy[] socks_proxies = admin.getSocksProxies();
/*      */           
/* 1530 */           if (socks_proxies.length > 0)
/*      */           {
/* 1532 */             payload.put("sp", socks_proxies[0].getName());
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/* 1537 */         payload.put("_enabled", Long.valueOf(is_enabled ? 1L : 0L));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1544 */       sendRequest("update", payload);
/*      */       
/* 1546 */       synchronized (this)
/*      */       {
/* 1548 */         this.consec_update_fails = 0;
/*      */         
/* 1550 */         this.must_update_once = false;
/*      */         
/* 1552 */         if (this.deferred_update_event == null)
/*      */         {
/* 1554 */           COConfigurationManager.setParameter("pairing.updateoutstanding", false);
/*      */         }
/*      */         
/* 1557 */         this.update_in_progress = false;
/*      */         
/* 1559 */         if (this.global_update_event == null)
/*      */         {
/* 1561 */           setStatus(MessageText.getString(is_enabled ? "pairing.status.noservices" : "pairing.status.disabled"));
/*      */         }
/*      */         else
/*      */         {
/* 1565 */           setStatus(MessageText.getString("pairing.status.registered", new String[] { new SimpleDateFormat().format(new Date(SystemTime.getCurrentTime())) }));
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1573 */       synchronized (this)
/*      */       {
/*      */         try {
/* 1576 */           this.consec_update_fails += 1;
/*      */           
/* 1578 */           long back_off = this.min_update_period;
/*      */           
/* 1580 */           for (int i = 0; i < this.consec_update_fails; i++)
/*      */           {
/* 1582 */             back_off *= 2L;
/*      */             
/* 1584 */             if (back_off > this.max_update_period)
/*      */             {
/* 1586 */               back_off = this.max_update_period;
/*      */               
/* 1588 */               break;
/*      */             }
/*      */           }
/*      */           
/* 1592 */           deferUpdate(back_off);
/*      */         }
/*      */         finally
/*      */         {
/* 1596 */           this.update_in_progress = false;
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1601 */       synchronized (this)
/*      */       {
/* 1603 */         if (this.update_in_progress)
/*      */         {
/* 1605 */           Debug.out("Something didn't clear update_in_progress!!!!");
/*      */           
/* 1607 */           this.update_in_progress = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deferUpdate(long millis)
/*      */   {
/* 1617 */     millis += 5000L;
/*      */     
/* 1619 */     long target = SystemTime.getOffsetTime(millis);
/*      */     
/* 1621 */     this.deferred_update_event = SimpleTimer.addEvent("PM:defer", target, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1631 */         synchronized (PairingManagerImpl.this)
/*      */         {
/* 1633 */           PairingManagerImpl.this.deferred_update_event = null;
/*      */         }
/*      */         
/* 1636 */         COConfigurationManager.setParameter("pairing.updateoutstanding", false);
/*      */         
/* 1638 */         PairingManagerImpl.this.updateNeeded();
/*      */       }
/*      */       
/* 1641 */     });
/* 1642 */     setStatus(MessageText.getString("pairing.status.pending", new String[] { new SimpleDateFormat().format(new Date(target)) }));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1647 */     COConfigurationManager.setParameter("pairing.updateoutstanding", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Map<String, Object> sendRequest(String command, Map<String, Object> payload)
/*      */     throws PairingException
/*      */   {
/*      */     try
/*      */     {
/* 1659 */       Map<String, Object> request = new HashMap();
/*      */       
/* 1661 */       CryptoManager cman = CryptoManagerFactory.getSingleton();
/*      */       
/* 1663 */       String azid = Base32.encode(cman.getSecureID());
/*      */       
/* 1665 */       payload.put("_azid", azid);
/*      */       try
/*      */       {
/* 1668 */         String pk = Base32.encode(cman.getECCHandler().getPublicKey("pairing"));
/*      */         
/* 1670 */         payload.put("_pk", pk);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/* 1675 */       request.put("req", payload);
/*      */       
/* 1677 */       String request_str = Base32.encode(BEncoder.encode(request));
/*      */       
/* 1679 */       String sig = null;
/*      */       try
/*      */       {
/* 1682 */         sig = Base32.encode(cman.getECCHandler().sign(request_str.getBytes("UTF-8"), "pairing"));
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/* 1687 */       String other_params = "&ver=" + UrlUtils.encode("5.7.6.0") + "&app=" + UrlUtils.encode(SystemProperties.getApplicationName()) + "&locale=" + UrlUtils.encode(MessageText.getCurrentLocale().toString());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1692 */       if (sig != null)
/*      */       {
/* 1694 */         other_params = other_params + "&sig=" + sig;
/*      */       }
/*      */       
/* 1697 */       URL target = new URL(SERVICE_URL + "/client/" + command + "?request=" + request_str + other_params);
/*      */       
/* 1699 */       Properties http_properties = new Properties();
/*      */       
/* 1701 */       http_properties.put("URL", target);
/*      */       try
/*      */       {
/* 1704 */         ClientIDManagerImpl.getSingleton().generateHTTPProperties(null, http_properties);
/*      */       }
/*      */       catch (ClientIDException e)
/*      */       {
/* 1708 */         throw new IOException(e.getMessage());
/*      */       }
/*      */       
/* 1711 */       target = (URL)http_properties.get("URL");
/*      */       
/* 1713 */       HttpURLConnection connection = (HttpURLConnection)target.openConnection();
/*      */       
/* 1715 */       connection.setConnectTimeout(30000);
/*      */       
/* 1717 */       InputStream is = connection.getInputStream();
/*      */       
/* 1719 */       Map<String, Object> response = BDecoder.decode(new BufferedInputStream(is));
/*      */       
/* 1721 */       synchronized (this)
/*      */       {
/* 1723 */         Long min_retry = (Long)response.get("min_secs");
/*      */         
/* 1725 */         if (min_retry != null)
/*      */         {
/* 1727 */           this.min_update_period = (min_retry.intValue() * 1000);
/*      */         }
/*      */         
/* 1730 */         Long max_retry = (Long)response.get("max_secs");
/*      */         
/* 1732 */         if (max_retry != null)
/*      */         {
/* 1734 */           this.max_update_period = (max_retry.intValue() * 1000);
/*      */         }
/*      */       }
/*      */       
/* 1738 */       final String message = getString(response, "message");
/*      */       
/* 1740 */       if (message != null)
/*      */       {
/* 1742 */         if ((this.last_message == null) || (!this.last_message.equals(message)))
/*      */         {
/* 1744 */           this.last_message = message;
/*      */           try
/*      */           {
/* 1747 */             byte[] message_sig = (byte[])response.get("message_sig");
/*      */             
/* 1749 */             AEVerifier.verifyData(message, message_sig);
/*      */             
/* 1751 */             new AEThread2("PairMsg", true)
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/* 1756 */                 UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */                 
/* 1758 */                 if (ui_manager != null)
/*      */                 {
/* 1760 */                   ui_manager.showMessageBox("pairing.server.warning.title", "!" + message + "!", 1L);
/*      */                 }
/*      */               }
/*      */             }.start();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1773 */       String error = getString(response, "error");
/*      */       
/* 1775 */       if (error != null)
/*      */       {
/* 1777 */         throw new PairingException(error);
/*      */       }
/*      */       
/* 1780 */       setLastServerError(null);
/*      */       
/* 1782 */       Map<String, Object> reply = (Map)response.get("rep");
/*      */       
/* 1784 */       Long qr_v = (Long)reply.get("qr_v");
/*      */       
/* 1786 */       if (qr_v != null)
/*      */       {
/* 1788 */         if (this.qr_version != qr_v.longValue())
/*      */         {
/* 1790 */           this.qr_version = qr_v.longValue();
/*      */           
/* 1792 */           COConfigurationManager.setParameter("pairing.qr.ver", this.qr_version);
/*      */         }
/*      */       }
/* 1795 */       return reply;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1799 */       setLastServerError(Debug.getNestedExceptionMessage(e));
/*      */       
/* 1801 */       if ((e instanceof PairingException))
/*      */       {
/* 1803 */         throw ((PairingException)e);
/*      */       }
/*      */       
/* 1806 */       throw new PairingException("invocation failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PairingTest testService(String sid, PairingTestListener listener)
/*      */     throws PairingException
/*      */   {
/* 1818 */     return new TestServiceImpl(sid, listener);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateSRPState()
/*      */   {
/*      */     String text;
/*      */     String text;
/* 1826 */     if (this.param_srp_enable.getValue()) {
/*      */       String text;
/* 1828 */       if (this.tunnel_handler == null)
/*      */       {
/* 1830 */         text = MessageText.getString("pairing.status.initialising") + "...";
/*      */       }
/*      */       else
/*      */       {
/* 1834 */         text = this.tunnel_handler.getStatus();
/*      */       }
/*      */     }
/*      */     else {
/* 1838 */       text = MessageText.getString("MyTorrentsView.menu.setSpeed.disabled");
/*      */     }
/*      */     
/* 1841 */     this.param_srp_state.setLabelText(MessageText.getString("pairing.srp.state", new String[] { text }));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSRPPassword(char[] password)
/*      */   {
/* 1848 */     this.init_sem.reserve();
/*      */     
/* 1850 */     this.tunnel_handler.setSRPPassword(password);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean handleLocalTunnel(TrackerWebPageRequest request, TrackerWebPageResponse response)
/*      */     throws IOException
/*      */   {
/* 1860 */     this.init_sem.reserve();
/*      */     
/* 1862 */     return this.tunnel_handler.handleLocalTunnel(request, response);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void recordRequest(String name, String ip, boolean good)
/*      */   {
/* 1871 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1876 */       if (this.tests_in_progress > 0)
/*      */       {
/* 1878 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1882 */     if (this.ui != null) {
/*      */       try
/*      */       {
/* 1885 */         this.ui.recordRequest(name, ip, good);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void fireChanged()
/*      */   {
/* 1896 */     for (PairingManagerListener l : this.listeners) {
/*      */       try
/*      */       {
/* 1899 */         l.somethingChanged(this);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1903 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(PairingManagerListener l)
/*      */   {
/* 1912 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(PairingManagerListener l)
/*      */   {
/* 1919 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getString(Map<String, Object> map, String name)
/*      */     throws IOException
/*      */   {
/* 1929 */     byte[] bytes = (byte[])map.get(name);
/*      */     
/* 1931 */     if (bytes == null)
/*      */     {
/* 1933 */       return null;
/*      */     }
/*      */     
/* 1936 */     return new String(bytes, "UTF-8");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1943 */     writer.println("Pairing Manager");
/*      */     try
/*      */     {
/* 1946 */       writer.indent();
/*      */       
/* 1948 */       if (this.tunnel_handler != null)
/*      */       {
/* 1950 */         this.tunnel_handler.generateEvidence(writer);
/*      */       }
/*      */     }
/*      */     finally {
/* 1954 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class TestServiceImpl
/*      */     implements PairingTest
/*      */   {
/*      */     private final String sid;
/*      */     
/*      */     private final PairingTestListener listener;
/* 1965 */     private volatile int outcome = 0;
/*      */     
/*      */ 
/*      */     private volatile String error_message;
/*      */     
/*      */     private volatile boolean cancelled;
/*      */     
/*      */ 
/*      */     protected TestServiceImpl(String _sid, PairingTestListener _listener)
/*      */     {
/* 1975 */       this.sid = _sid;
/* 1976 */       this.listener = _listener;
/*      */       
/* 1978 */       new AEThread2("PM:test")
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/* 1984 */             String access_code = null;
/* 1985 */             long sid_wait_start = -1L;
/*      */             
/*      */             do
/*      */             {
/* 1989 */               if (!PairingManagerImpl.this.isEnabled())
/*      */               {
/* 1991 */                 throw new Exception("Pairing is disabled");
/*      */               }
/*      */               
/* 1994 */               access_code = PairingManagerImpl.this.peekAccessCode();
/*      */               
/* 1996 */               if (access_code != null)
/*      */               {
/* 1998 */                 if (!PairingManagerImpl.this.hasActionOutstanding())
/*      */                 {
/* 2000 */                   if (PairingManagerImpl.this.getService(PairingManagerImpl.TestServiceImpl.this.sid) != null) {
/*      */                     break;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 2006 */                   long now = SystemTime.getMonotonousTime();
/*      */                   
/* 2008 */                   if (sid_wait_start == -1L)
/*      */                   {
/* 2010 */                     sid_wait_start = now;
/*      */                   }
/*      */                   else
/*      */                   {
/* 2014 */                     if (now - sid_wait_start > 5000L) {
/*      */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 2023 */               Thread.sleep(500L);
/*      */             }
/* 2025 */             while (!PairingManagerImpl.TestServiceImpl.this.cancelled);
/*      */             
/* 2027 */             PairingManagerImpl.TestServiceImpl.this.outcome = 6; return;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2033 */             PairedService service = PairingManagerImpl.this.getService(PairingManagerImpl.TestServiceImpl.this.sid);
/*      */             
/* 2035 */             if (service == null)
/*      */             {
/* 2037 */               throw new Exception("Service not found");
/*      */             }
/*      */             
/* 2040 */             PairingManagerImpl.TestServiceImpl.this.listener.testStarted(PairingManagerImpl.TestServiceImpl.this);
/*      */             
/* 2042 */             String other_params = "&ver=" + UrlUtils.encode("5.7.6.0") + "&app=" + UrlUtils.encode(SystemProperties.getApplicationName()) + "&locale=" + UrlUtils.encode(MessageText.getCurrentLocale().toString());
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2047 */             URL target = new URL(PairingManagerImpl.SERVICE_URL + "/web/test?sid=" + PairingManagerImpl.TestServiceImpl.this.sid + "&ac=" + access_code + "&format=bencode" + other_params);
/*      */             
/* 2049 */             HttpURLConnection connection = (HttpURLConnection)target.openConnection();
/*      */             
/* 2051 */             connection.setConnectTimeout(10000);
/*      */             try
/*      */             {
/* 2054 */               synchronized (PairingManagerImpl.this)
/*      */               {
/* 2056 */                 PairingManagerImpl.access$808(PairingManagerImpl.this);
/*      */               }
/*      */               
/* 2059 */               InputStream is = connection.getInputStream();
/*      */               
/* 2061 */               Object response = BDecoder.decode(new BufferedInputStream(is));
/*      */               
/* 2063 */               response = BDecoder.decodeStrings((Map)response);
/*      */               
/* 2065 */               Long code = (Long)((Map)response).get("code");
/*      */               
/* 2067 */               if (code == null)
/*      */               {
/* 2069 */                 throw new Exception("Code missing from reply");
/*      */               }
/*      */               
/* 2072 */               PairingManagerImpl.TestServiceImpl.this.error_message = ((String)((Map)response).get("msg"));
/*      */               
/* 2074 */               if (code.longValue() == 1L)
/*      */               {
/* 2076 */                 PairingManagerImpl.TestServiceImpl.this.outcome = 1;
/*      */               }
/* 2078 */               else if (code.longValue() == 2L)
/*      */               {
/* 2080 */                 PairingManagerImpl.TestServiceImpl.this.outcome = 4;
/*      */               }
/* 2082 */               else if (code.longValue() == 3L)
/*      */               {
/* 2084 */                 PairingManagerImpl.TestServiceImpl.this.outcome = 5;
/*      */               }
/* 2086 */               else if (code.longValue() == 4L)
/*      */               {
/* 2088 */                 PairingManagerImpl.TestServiceImpl.this.outcome = 2;
/*      */                 
/* 2090 */                 PairingManagerImpl.TestServiceImpl.this.error_message = "Connect timeout";
/*      */               }
/* 2092 */               else if (code.longValue() == 5L)
/*      */               {
/* 2094 */                 PairingManagerImpl.TestServiceImpl.this.outcome = 2;
/*      */               }
/*      */               else
/*      */               {
/* 2098 */                 PairingManagerImpl.TestServiceImpl.this.outcome = 5;
/*      */                 
/* 2100 */                 PairingManagerImpl.TestServiceImpl.this.error_message = ("Unknown response code " + code);
/*      */               }
/*      */             }
/*      */             catch (SocketTimeoutException e) {
/* 2104 */               PairingManagerImpl.TestServiceImpl.this.outcome = 3;
/*      */               
/* 2106 */               PairingManagerImpl.TestServiceImpl.this.error_message = "Connect timeout";
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2111 */             PairingManagerImpl.TestServiceImpl.this.outcome = 3;
/*      */             
/* 2113 */             PairingManagerImpl.TestServiceImpl.this.error_message = Debug.getNestedExceptionMessage(e);
/*      */           }
/*      */           finally
/*      */           {
/*      */             try {
/* 2118 */               PairingManagerImpl.TestServiceImpl.this.listener.testComplete(PairingManagerImpl.TestServiceImpl.this);
/*      */             }
/*      */             finally
/*      */             {
/* 2122 */               synchronized (PairingManagerImpl.this)
/*      */               {
/* 2124 */                 PairingManagerImpl.access$810(PairingManagerImpl.this);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getOutcome()
/*      */     {
/* 2135 */       return this.outcome;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getErrorMessage()
/*      */     {
/* 2141 */       return this.error_message;
/*      */     }
/*      */     
/*      */ 
/*      */     public void cancel()
/*      */     {
/* 2147 */       this.cancelled = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class PairedServiceImpl
/*      */     implements PairedService, PairingConnectionData
/*      */   {
/*      */     private final String sid;
/* 2156 */     private final Map<String, String> attributes = new HashMap();
/*      */     
/*      */ 
/*      */     private PairedServiceRequestHandler request_handler;
/*      */     
/*      */ 
/*      */ 
/*      */     protected PairedServiceImpl(String _sid, PairedServiceRequestHandler _request_handler)
/*      */     {
/* 2165 */       this.sid = _sid;
/* 2166 */       this.request_handler = _request_handler;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getSID()
/*      */     {
/* 2172 */       return this.sid;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setHandler(PairedServiceRequestHandler _h)
/*      */     {
/* 2179 */       this.request_handler = _h;
/*      */     }
/*      */     
/*      */ 
/*      */     protected PairedServiceRequestHandler getHandler()
/*      */     {
/* 2185 */       return this.request_handler;
/*      */     }
/*      */     
/*      */ 
/*      */     public PairingConnectionData getConnectionData()
/*      */     {
/* 2191 */       return this;
/*      */     }
/*      */     
/*      */ 
/*      */     public void remove()
/*      */     {
/* 2197 */       PairingManagerImpl.this.remove(this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAttribute(String name, String value)
/*      */     {
/* 2205 */       synchronized (this)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2211 */         if (value == null)
/*      */         {
/* 2213 */           this.attributes.remove(name);
/*      */         }
/*      */         else
/*      */         {
/* 2217 */           this.attributes.put(name, value);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     /* Error */
/*      */     public String getAttribute(String name)
/*      */     {
/*      */       // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: dup
/*      */       //   2: astore_2
/*      */       //   3: monitorenter
/*      */       //   4: aload_0
/*      */       //   5: getfield 84	com/aelitis/azureus/core/pairing/impl/PairingManagerImpl$PairedServiceImpl:attributes	Ljava/util/Map;
/*      */       //   8: aload_1
/*      */       //   9: invokeinterface 90 2 0
/*      */       //   14: checkcast 47	java/lang/String
/*      */       //   17: aload_2
/*      */       //   18: monitorexit
/*      */       //   19: areturn
/*      */       //   20: astore_3
/*      */       //   21: aload_2
/*      */       //   22: monitorexit
/*      */       //   23: aload_3
/*      */       //   24: athrow
/*      */       // Line number table:
/*      */       //   Java source line #2226	-> byte code offset #0
/*      */       //   Java source line #2228	-> byte code offset #4
/*      */       //   Java source line #2229	-> byte code offset #20
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	signature
/*      */       //   0	25	0	this	PairedServiceImpl
/*      */       //   0	25	1	name	String
/*      */       //   2	20	2	Ljava/lang/Object;	Object
/*      */       //   20	4	3	localObject1	Object
/*      */       // Exception table:
/*      */       //   from	to	target	type
/*      */       //   4	19	20	finally
/*      */       //   20	23	20	finally
/*      */     }
/*      */     
/*      */     public void sync()
/*      */     {
/* 2235 */       PairingManagerImpl.this.sync(this);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected Map<String, String> toMap(boolean enable_nets)
/*      */     {
/* 2242 */       Map<String, String> result = new HashMap();
/*      */       
/* 2244 */       result.put("sid", this.sid);
/*      */       
/* 2246 */       synchronized (this)
/*      */       {
/* 2248 */         result.putAll(this.attributes);
/*      */       }
/*      */       
/* 2251 */       if (!enable_nets)
/*      */       {
/* 2253 */         result.remove("I2P");
/*      */         
/* 2255 */         result.remove("Tor");
/*      */       }
/*      */       
/* 2258 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class PairedNodeImpl
/*      */     implements PairedNode
/*      */   {
/*      */     private final Map map;
/*      */     
/*      */ 
/*      */     protected PairedNodeImpl(Map _map)
/*      */     {
/* 2272 */       this.map = _map;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getAccessCode()
/*      */     {
/* 2278 */       return (String)this.map.get("ac");
/*      */     }
/*      */     
/*      */ 
/*      */     public List<InetAddress> getAddresses()
/*      */     {
/* 2284 */       Set<InetAddress> addresses = new HashSet();
/*      */       
/* 2286 */       addAddress(addresses, "c_v4");
/* 2287 */       addAddress(addresses, "c_v6");
/* 2288 */       addAddress(addresses, "l_v4");
/* 2289 */       addAddress(addresses, "l_v6");
/* 2290 */       addAddress(addresses, "e_v4");
/* 2291 */       addAddress(addresses, "e_v6");
/* 2292 */       addAddress(addresses, "e_l_v4");
/* 2293 */       addAddress(addresses, "e_l_v6");
/* 2294 */       addAddress(addresses, "e_h");
/*      */       
/* 2296 */       return new ArrayList(addresses);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addAddress(Set<InetAddress> addresses, String key)
/*      */     {
/* 2304 */       String str = (String)this.map.get(key);
/*      */       
/* 2306 */       if (str != null)
/*      */       {
/* 2308 */         String[] bits = str.split(",");
/*      */         
/* 2310 */         for (String bit : bits)
/*      */         {
/* 2312 */           bit = bit.trim();
/*      */           
/* 2314 */           if (bit.length() != 0)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2320 */             if (bit.endsWith("*"))
/*      */             {
/* 2322 */               bit = bit.substring(0, bit.length() - 1);
/*      */             }
/*      */             try
/*      */             {
/* 2326 */               addresses.add(InetAddress.getByName(bit));
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public List<PairedService> getServices()
/*      */     {
/* 2337 */       Map<String, Map> smap = (Map)this.map.get("services");
/*      */       
/* 2339 */       List<PairedService> services = new ArrayList();
/*      */       
/* 2341 */       for (Map.Entry<String, Map> entry : smap.entrySet())
/*      */       {
/* 2343 */         services.add(new PairingManagerImpl.PairedService2Impl((String)entry.getKey(), (Map)entry.getValue()));
/*      */       }
/*      */       
/* 2346 */       return services;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class PairedService2Impl
/*      */     implements PairedService
/*      */   {
/*      */     private final String sid;
/*      */     
/*      */     private final Map map;
/*      */     
/*      */ 
/*      */     protected PairedService2Impl(String _sid, Map _map)
/*      */     {
/* 2362 */       this.sid = _sid;
/* 2363 */       this.map = _map;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getSID()
/*      */     {
/* 2369 */       return this.sid;
/*      */     }
/*      */     
/*      */ 
/*      */     public PairingConnectionData getConnectionData()
/*      */     {
/* 2375 */       return new PairingManagerImpl.PairingConnectionData2(this.map);
/*      */     }
/*      */     
/*      */ 
/*      */     public void remove()
/*      */     {
/* 2381 */       throw new RuntimeException("Not supported");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class PairingConnectionData2
/*      */     implements PairingConnectionData
/*      */   {
/*      */     private final Map map;
/*      */     
/*      */ 
/*      */     protected PairingConnectionData2(Map _map)
/*      */     {
/* 2395 */       this.map = _map;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAttribute(String name, String value)
/*      */     {
/* 2403 */       throw new RuntimeException("Not supported");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getAttribute(String name)
/*      */     {
/* 2410 */       return (String)this.map.get(name);
/*      */     }
/*      */     
/*      */ 
/*      */     public void sync()
/*      */     {
/* 2416 */       throw new RuntimeException("Not supported");
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface UIAdapter
/*      */   {
/*      */     public abstract void initialise(PluginInterface paramPluginInterface, BooleanParameter paramBooleanParameter);
/*      */     
/*      */     public abstract void recordRequest(String paramString1, String paramString2, boolean paramBoolean);
/*      */     
/*      */     public abstract char[] getSRPPassword();
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/impl/PairingManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */