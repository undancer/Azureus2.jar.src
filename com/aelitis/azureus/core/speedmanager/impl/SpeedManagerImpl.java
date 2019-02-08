/*      */ package com.aelitis.azureus.core.speedmanager.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;
/*      */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContact;
/*      */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContactListener;
/*      */ import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterListener;
/*      */ import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerAdapter;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerListener;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*      */ import com.aelitis.azureus.core.speedmanager.impl.v1.SpeedManagerAlgorithmProviderV1;
/*      */ import com.aelitis.azureus.core.speedmanager.impl.v2.SpeedManagerAlgorithmProviderV2;
/*      */ import com.aelitis.azureus.core.speedmanager.impl.v3.SpeedManagerAlgorithmProviderV3;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SpeedManagerImpl
/*      */   implements SpeedManager, SpeedManagerAlgorithmProviderAdapter, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   protected static final int UPDATE_PERIOD_MILLIS = 3000;
/*      */   private static final int CONTACT_NUMBER = 3;
/*      */   private static final int CONTACT_PING_SECS = 3;
/*      */   private static final int LONG_PERIOD_SECS = 3600;
/*      */   private static final int LONG_PERIOD_TICKS = 1200;
/*      */   private static final int SHORT_ESTIMATE_SECS = 15;
/*      */   private static final int MEDIUM_ESTIMATE_SECS = 150;
/*      */   static final int SHORT_ESTIMATE_SAMPLES = 5;
/*      */   static final int MEDIUM_ESTIMATE_SAMPLES = 50;
/*      */   private static final int SAVE_PERIOD_SECS = 900;
/*      */   private static final int SAVE_PERIOD_TICKS = 300;
/*      */   private static final int AUTO_ADJUST_PERIOD_SECS = 60;
/*      */   private static final int AUTO_ADJUST_PERIOD_TICKS = 20;
/*      */   private static final int SPEED_AVERAGE_PERIOD = 3000;
/*      */   private static boolean DEBUG;
/*      */   public static final String CONFIG_VERSION_STR = "Auto_Upload_Speed_Version_String";
/*      */   public static final String CONFIG_VERSION = "Auto Upload Speed Version";
/*      */   private static final String CONFIG_AVAIL = "AutoSpeed Available";
/*      */   private static final String CONFIG_DEBUG = "Auto Upload Speed Debug Enabled";
/*  105 */   private static final String[] CONFIG_PARAMS = { "Auto Upload Speed Debug Enabled" };
/*      */   private static boolean emulated_ping_source;
/*      */   final AzureusCore core;
/*      */   
/*  109 */   static { COConfigurationManager.addAndFireParameterListeners(CONFIG_PARAMS, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  117 */         SpeedManagerImpl.access$002(COConfigurationManager.getBooleanParameter("Auto Upload Speed Debug Enabled"));
/*      */       }
/*      */     }); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private DHTSpeedTester speed_tester;
/*      */   
/*      */ 
/*      */ 
/*      */   final SpeedManagerAdapter adapter;
/*      */   
/*      */ 
/*      */ 
/*  132 */   private SpeedManagerAlgorithmProvider provider = new nullProvider();
/*      */   
/*      */ 
/*  135 */   private int provider_version = -1;
/*      */   
/*      */   private boolean enabled;
/*      */   
/*      */   private static final boolean pm_enabled = true;
/*  140 */   final Map contacts = new HashMap();
/*      */   private volatile int total_contacts;
/*  142 */   private pingContact[] contacts_array = new pingContact[0];
/*      */   
/*      */   private Object original_limits;
/*      */   
/*  146 */   final AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */   
/*      */   final SpeedManagerPingMapperImpl ping_mapper;
/*      */   
/*      */   final SpeedManagerPingMapperImpl[] ping_mappers;
/*      */   
/*  152 */   private final CopyOnWriteList transient_mappers = new CopyOnWriteList();
/*      */   
/*      */   private final AEDiagnosticsLogger logger;
/*      */   
/*      */   private String asn;
/*      */   
/*  158 */   private final CopyOnWriteList listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SpeedManagerImpl(AzureusCore _core, SpeedManagerAdapter _adapter)
/*      */   {
/*  165 */     this.core = _core;
/*  166 */     this.adapter = _adapter;
/*      */     
/*  168 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  170 */     this.logger = AEDiagnostics.getLogger("SpeedMan");
/*      */     
/*  172 */     this.ping_mapper = new SpeedManagerPingMapperImpl(this, "Var", 1200, true, false);
/*      */     
/*  174 */     if (Constants.isCVSVersion())
/*      */     {
/*  176 */       SpeedManagerPingMapperImpl pm2 = new SpeedManagerPingMapperImpl(this, "Abs", 1200, false, false);
/*      */       
/*  178 */       this.ping_mappers = new SpeedManagerPingMapperImpl[] { pm2, this.ping_mapper };
/*      */     }
/*      */     else
/*      */     {
/*  182 */       this.ping_mappers = new SpeedManagerPingMapperImpl[] { this.ping_mapper };
/*      */     }
/*      */     
/*  185 */     final File config_dir = new File(SystemProperties.getUserPath(), "net");
/*      */     
/*  187 */     if (!config_dir.exists())
/*      */     {
/*  189 */       config_dir.mkdirs();
/*      */     }
/*      */     
/*  192 */     NetworkAdmin.getSingleton().addAndFirePropertyChangeListener(new NetworkAdminPropertyChangeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void propertyChanged(String property)
/*      */       {
/*      */ 
/*  199 */         if (property == "AS")
/*      */         {
/*  201 */           NetworkAdminASN net_asn = NetworkAdmin.getSingleton().getCurrentASN();
/*      */           
/*  203 */           String as = net_asn.getAS();
/*      */           
/*  205 */           if (as.length() == 0)
/*      */           {
/*  207 */             as = "default";
/*      */           }
/*      */           
/*  210 */           File history = new File(config_dir, "pm_" + FileUtil.convertOSSpecificChars(as, false) + ".dat");
/*      */           
/*  212 */           SpeedManagerImpl.this.ping_mapper.loadHistory(history);
/*      */           
/*  214 */           SpeedManagerImpl.this.asn = net_asn.getASName();
/*      */           
/*  216 */           if (SpeedManagerImpl.this.asn.length() == 0)
/*      */           {
/*  218 */             SpeedManagerImpl.this.asn = "Unknown";
/*      */           }
/*      */           
/*  221 */           SpeedManagerImpl.this.informListeners(1);
/*      */         }
/*      */         
/*      */       }
/*  225 */     });
/*  226 */     this.core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void stopping(AzureusCore core)
/*      */       {
/*      */ 
/*  233 */         SpeedManagerImpl.this.ping_mapper.saveHistory();
/*      */       }
/*      */       
/*  236 */     });
/*  237 */     COConfigurationManager.addAndFireParameterListener("Auto Upload Speed Version", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(final String name)
/*      */       {
/*      */ 
/*      */ 
/*  245 */         SpeedManagerImpl.this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  251 */             boolean do_reset = SpeedManagerImpl.this.provider_version == -1;
/*      */             
/*  253 */             int version = COConfigurationManager.getIntParameter(name);
/*      */             
/*  255 */             if (version != SpeedManagerImpl.this.provider_version)
/*      */             {
/*  257 */               SpeedManagerImpl.this.provider_version = version;
/*      */               
/*  259 */               if (SpeedManagerImpl.this.isEnabled())
/*      */               {
/*  261 */                 SpeedManagerImpl.this.setEnabledSupport(false);
/*      */                 
/*  263 */                 SpeedManagerImpl.this.setEnabledSupport(true);
/*      */               }
/*      */             }
/*      */             
/*  267 */             if (do_reset)
/*      */             {
/*  269 */               SpeedManagerImpl.this.enableOrAlgChanged();
/*      */             }
/*      */             
/*      */           }
/*      */         });
/*      */       }
/*  275 */     });
/*  276 */     COConfigurationManager.setParameter("AutoSpeed Available", false);
/*      */     
/*  278 */     SimpleTimer.addPeriodicEvent("SpeedManager:timer", 3000L, new TimerEventPerformer()
/*      */     {
/*      */       private int tick_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*  292 */         if (SpeedManagerImpl.this.contacts_array.length == 0)
/*      */         {
/*  294 */           int x = SpeedManagerImpl.this.adapter.getCurrentDataUploadSpeed(3000) + SpeedManagerImpl.this.adapter.getCurrentProtocolUploadSpeed(3000);
/*  295 */           int y = SpeedManagerImpl.this.adapter.getCurrentDataDownloadSpeed(3000) + SpeedManagerImpl.this.adapter.getCurrentProtocolDownloadSpeed(3000);
/*      */           
/*  297 */           for (int i = 0; i < SpeedManagerImpl.this.ping_mappers.length; i++)
/*      */           {
/*  299 */             SpeedManagerImpl.this.ping_mappers[i].addSpeed(x, y);
/*      */           }
/*      */         }
/*      */         
/*  303 */         this.tick_count += 1;
/*      */         
/*  305 */         if (this.tick_count % 300 == 0)
/*      */         {
/*  307 */           SpeedManagerImpl.this.ping_mapper.saveHistory();
/*      */         }
/*      */         
/*  310 */         if (this.tick_count % 20 == 0)
/*      */         {
/*  312 */           SpeedManagerImpl.this.autoAdjust();
/*      */         }
/*      */         
/*      */       }
/*  316 */     });
/*  317 */     COConfigurationManager.addAndFireParameterListener("Auto Adjust Transfer Defaults", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  325 */         SpeedManagerImpl.this.autoAdjust();
/*      */       }
/*      */       
/*  328 */     });
/*  329 */     emulated_ping_source = false;
/*      */     
/*  331 */     if (emulated_ping_source)
/*      */     {
/*  333 */       Debug.out("Emulated ping source!!!!");
/*      */       
/*  335 */       setSpeedTester(new TestPingSourceRandom(this));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManager getSpeedManager()
/*      */   {
/*  342 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getASN()
/*      */   {
/*  348 */     return this.asn;
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerLimitEstimate getEstimatedUploadCapacityBytesPerSec()
/*      */   {
/*  354 */     return this.ping_mapper.getEstimatedUploadCapacityBytesPerSec();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEstimatedUploadCapacityBytesPerSec(int bytes_per_sec, float metric)
/*      */   {
/*  362 */     this.ping_mapper.setEstimatedUploadCapacityBytesPerSec(bytes_per_sec, metric);
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerLimitEstimate getEstimatedDownloadCapacityBytesPerSec()
/*      */   {
/*  368 */     return this.ping_mapper.getEstimatedDownloadCapacityBytesPerSec();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEstimatedDownloadCapacityBytesPerSec(int bytes_per_sec, float metric)
/*      */   {
/*  376 */     this.ping_mapper.setEstimatedDownloadCapacityBytesPerSec(bytes_per_sec, metric);
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/*  382 */     this.ping_mapper.reset();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void enableOrAlgChanged()
/*      */   {
/*  388 */     this.total_contacts = 0;
/*      */     
/*  390 */     SpeedManagerAlgorithmProvider old_provider = this.provider;
/*      */     
/*  392 */     if (this.provider_version == 1)
/*      */     {
/*  394 */       if (!(this.provider instanceof SpeedManagerAlgorithmProviderV1))
/*      */       {
/*  396 */         this.provider = new SpeedManagerAlgorithmProviderV1(this);
/*      */       }
/*  398 */     } else if (this.provider_version == 2)
/*      */     {
/*  400 */       if (!(this.provider instanceof SpeedManagerAlgorithmProviderV2))
/*      */       {
/*  402 */         this.provider = new SpeedManagerAlgorithmProviderV2(this);
/*      */       }
/*      */     }
/*  405 */     else if (this.provider_version == 3)
/*      */     {
/*  407 */       this.provider = new SpeedManagerAlgorithmProviderV3(this);
/*      */     }
/*      */     else
/*      */     {
/*  411 */       Debug.out("Unknown provider version " + this.provider_version);
/*      */       
/*  413 */       if (!(this.provider instanceof nullProvider))
/*      */       {
/*  415 */         this.provider = new nullProvider();
/*      */       }
/*      */     }
/*      */     
/*  419 */     if (old_provider != this.provider)
/*      */     {
/*  421 */       log("Algorithm set to " + this.provider.getClass().getName());
/*      */     }
/*      */     
/*  424 */     if (old_provider != null)
/*      */     {
/*  426 */       old_provider.destroy();
/*      */     }
/*      */     
/*  429 */     this.provider.reset();
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerPingMapper createTransientPingMapper()
/*      */   {
/*  435 */     SpeedManagerPingMapper res = new SpeedManagerPingMapperImpl(this, "Transient", 1200, true, true);
/*      */     
/*  437 */     this.transient_mappers.add(res);
/*      */     
/*  439 */     if (this.transient_mappers.size() > 32)
/*      */     {
/*  441 */       Debug.out("Transient mappers are growing too large");
/*      */     }
/*      */     
/*  444 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void destroy(SpeedManagerPingMapper mapper)
/*      */   {
/*  451 */     this.transient_mappers.remove(mapper);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSpeedTester(DHTSpeedTester _tester)
/*      */   {
/*  458 */     if (_tester == this.speed_tester)
/*      */     {
/*  460 */       return;
/*      */     }
/*      */     
/*  463 */     if (this.speed_tester != null)
/*      */     {
/*  465 */       if (!emulated_ping_source)
/*      */       {
/*  467 */         Debug.out("speed tester already set!");
/*      */       }
/*      */       
/*  470 */       return;
/*      */     }
/*      */     
/*  473 */     COConfigurationManager.setParameter("AutoSpeed Available", true);
/*      */     
/*  475 */     this.speed_tester = _tester;
/*      */     
/*  477 */     this.speed_tester.addListener(new DHTSpeedTesterListener()
/*      */     {
/*      */ 
/*  480 */       private DHTSpeedTesterContact[] last_contact_group = new DHTSpeedTesterContact[0];
/*      */       
/*      */ 
/*      */ 
/*      */       public void contactAdded(DHTSpeedTesterContact contact)
/*      */       {
/*  486 */         if (SpeedManagerImpl.this.core.getInstanceManager().isLANAddress(contact.getAddress().getAddress()))
/*      */         {
/*  488 */           contact.destroy();
/*      */         }
/*      */         else {
/*  491 */           SpeedManagerImpl.this.log("activePing: " + contact.getString());
/*      */           
/*  493 */           contact.setPingPeriod(3);
/*      */           
/*  495 */           synchronized (SpeedManagerImpl.this.contacts)
/*      */           {
/*  497 */             SpeedManagerImpl.pingContact source = new SpeedManagerImpl.pingContact(contact);
/*      */             
/*  499 */             SpeedManagerImpl.this.contacts.put(contact, source);
/*      */             
/*  501 */             SpeedManagerImpl.this.contacts_array = new SpeedManagerImpl.pingContact[SpeedManagerImpl.this.contacts.size()];
/*      */             
/*  503 */             SpeedManagerImpl.this.contacts.values().toArray(SpeedManagerImpl.this.contacts_array);
/*      */             
/*  505 */             SpeedManagerImpl.access$508(SpeedManagerImpl.this);
/*      */             
/*  507 */             SpeedManagerImpl.this.provider.pingSourceFound(source, SpeedManagerImpl.this.total_contacts > 3);
/*      */           }
/*      */           
/*  510 */           contact.addListener(new DHTSpeedTesterContactListener()
/*      */           {
/*      */             public void ping(DHTSpeedTesterContact contact, int round_trip_time) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void pingFailed(DHTSpeedTesterContact contact) {}
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void contactDied(DHTSpeedTesterContact contact)
/*      */             {
/*  530 */               SpeedManagerImpl.this.log("deadPing: " + contact.getString());
/*      */               
/*  532 */               synchronized (SpeedManagerImpl.this.contacts)
/*      */               {
/*  534 */                 SpeedManagerImpl.pingContact source = (SpeedManagerImpl.pingContact)SpeedManagerImpl.this.contacts.remove(contact);
/*      */                 
/*  536 */                 if (source != null)
/*      */                 {
/*  538 */                   SpeedManagerImpl.this.contacts_array = new SpeedManagerImpl.pingContact[SpeedManagerImpl.this.contacts.size()];
/*      */                   
/*  540 */                   SpeedManagerImpl.this.contacts.values().toArray(SpeedManagerImpl.this.contacts_array);
/*      */                   
/*  542 */                   SpeedManagerImpl.this.provider.pingSourceFailed(source);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void resultGroup(DHTSpeedTesterContact[] st_contacts, int[] round_trip_times)
/*      */       {
/*  565 */         boolean sources_changed = false;
/*      */         
/*  567 */         for (int i = 0; i < st_contacts.length; i++)
/*      */         {
/*  569 */           boolean found = false;
/*      */           
/*  571 */           for (int j = 0; j < this.last_contact_group.length; j++)
/*      */           {
/*  573 */             if (st_contacts[i] == this.last_contact_group[j])
/*      */             {
/*  575 */               found = true;
/*      */               
/*  577 */               break;
/*      */             }
/*      */           }
/*      */           
/*  581 */           if (!found)
/*      */           {
/*  583 */             sources_changed = true;
/*      */             
/*  585 */             break;
/*      */           }
/*      */         }
/*      */         
/*  589 */         this.last_contact_group = st_contacts;
/*      */         
/*  591 */         SpeedManagerImpl.pingContact[] sources = new SpeedManagerImpl.pingContact[st_contacts.length];
/*      */         
/*  593 */         boolean miss = false;
/*      */         
/*  595 */         int worst_value = -1;
/*  596 */         int min_value = Integer.MAX_VALUE;
/*      */         
/*  598 */         int num_values = 0;
/*  599 */         int total = 0;
/*      */         
/*  601 */         synchronized (SpeedManagerImpl.this.contacts)
/*      */         {
/*  603 */           for (int i = 0; i < st_contacts.length; i++)
/*      */           {
/*  605 */             SpeedManagerImpl.pingContact source = sources[i] = (SpeedManagerImpl.pingContact)SpeedManagerImpl.this.contacts.get(st_contacts[i]);
/*      */             
/*  607 */             if (source != null)
/*      */             {
/*  609 */               int rtt = round_trip_times[i];
/*      */               
/*  611 */               if (rtt >= 0)
/*      */               {
/*  613 */                 if (rtt > worst_value)
/*      */                 {
/*  615 */                   worst_value = rtt;
/*      */                 }
/*      */                 
/*  618 */                 if (rtt < min_value)
/*      */                 {
/*  620 */                   min_value = rtt;
/*      */                 }
/*      */                 
/*  623 */                 num_values++;
/*      */                 
/*  625 */                 total += rtt;
/*      */               }
/*      */               
/*  628 */               source.setPingTime(rtt);
/*      */             }
/*      */             else
/*      */             {
/*  632 */               miss = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  637 */         if (!miss)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  643 */           SpeedManagerImpl.this.provider.calculate(sources);
/*      */           
/*      */ 
/*      */ 
/*  647 */           if (num_values > 1)
/*      */           {
/*  649 */             total -= worst_value;
/*  650 */             num_values--;
/*      */           }
/*      */           
/*  653 */           if (num_values > 0)
/*      */           {
/*  655 */             int average = total / num_values;
/*      */             
/*      */ 
/*      */ 
/*  659 */             average = (average + min_value) / 2;
/*      */             
/*  661 */             SpeedManagerImpl.this.addPingHistory(average, sources_changed);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void destroyed()
/*      */       {
/*  669 */         SpeedManagerImpl.this.speed_tester = null;
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  674 */     });
/*  675 */     this.speed_tester.setContactNumber(3);
/*      */     
/*      */ 
/*  678 */     SimpleTimer.addPeriodicEvent("SpeedManager:stats", 1000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  687 */         if (SpeedManagerImpl.this.enabled)
/*      */         {
/*  689 */           SpeedManagerImpl.this.provider.updateStats();
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
/*      */   protected void addPingHistory(int rtt, boolean re_base)
/*      */   {
/*  702 */     int x = this.adapter.getCurrentDataUploadSpeed(3000) + this.adapter.getCurrentProtocolUploadSpeed(3000);
/*  703 */     int y = this.adapter.getCurrentDataDownloadSpeed(3000) + this.adapter.getCurrentProtocolDownloadSpeed(3000);
/*      */     
/*  705 */     for (int i = 0; i < this.ping_mappers.length; i++)
/*      */     {
/*  707 */       this.ping_mappers[i].addPing(x, y, rtt, re_base);
/*      */     }
/*      */     
/*  710 */     Iterator it = this.transient_mappers.iterator();
/*      */     
/*  712 */     while (it.hasNext())
/*      */     {
/*  714 */       ((SpeedManagerPingMapperImpl)it.next()).addPing(x, y, rtt, re_base);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAvailable()
/*      */   {
/*  721 */     return this.speed_tester != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEnabled(final boolean _enabled)
/*      */   {
/*  732 */     final AESemaphore sem = new AESemaphore("SpeedManagerImpl.setEnabled");
/*      */     
/*      */ 
/*      */ 
/*  736 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */         try
/*      */         {
/*  743 */           SpeedManagerImpl.this.setEnabledSupport(_enabled);
/*      */         }
/*      */         finally
/*      */         {
/*  747 */           sem.release();
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*  752 */     if (!sem.reserve(10000L))
/*      */     {
/*  754 */       Debug.out("operation didn't complete in time");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setEnabledSupport(boolean _enabled)
/*      */   {
/*  762 */     if (this.enabled != _enabled)
/*      */     {
/*  764 */       log("Enabled set to " + _enabled);
/*      */       
/*  766 */       if (_enabled)
/*      */       {
/*  768 */         this.original_limits = this.adapter.getLimits();
/*      */       }
/*      */       else
/*      */       {
/*  772 */         this.ping_mapper.saveHistory();
/*      */       }
/*      */       
/*  775 */       enableOrAlgChanged();
/*      */       
/*  777 */       this.enabled = _enabled;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  787 */       if (!this.enabled)
/*      */       {
/*  789 */         this.adapter.setLimits(this.original_limits, true, this.provider.getAdjustsDownloadLimits());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  797 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTSpeedTester getSpeedTester()
/*      */   {
/*  803 */     return this.speed_tester;
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerPingSource[] getPingSources()
/*      */   {
/*  809 */     return this.contacts_array;
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerPingMapper getActiveMapper()
/*      */   {
/*  815 */     return this.ping_mapper;
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerPingMapper getPingMapper()
/*      */   {
/*  821 */     return getActiveMapper();
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerPingMapper[] getMappers()
/*      */   {
/*  827 */     return this.ping_mappers;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getIdlePingMillis()
/*      */   {
/*  833 */     return this.provider.getIdlePingMillis();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentPingMillis()
/*      */   {
/*  839 */     return this.provider.getCurrentPingMillis();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxPingMillis()
/*      */   {
/*  845 */     return this.provider.getMaxPingMillis();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCurrentChokeSpeed()
/*      */   {
/*  856 */     return this.provider.getCurrentChokeSpeed();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxUploadSpeed()
/*      */   {
/*  862 */     return this.provider.getMaxUploadSpeed();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentUploadLimit()
/*      */   {
/*  868 */     return this.adapter.getCurrentUploadLimit();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCurrentUploadLimit(int bytes_per_second)
/*      */   {
/*  875 */     if (this.enabled)
/*      */     {
/*  877 */       this.adapter.setCurrentUploadLimit(bytes_per_second);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentDownloadLimit()
/*      */   {
/*  884 */     return this.adapter.getCurrentDownloadLimit();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCurrentDownloadLimit(int bytes_per_second)
/*      */   {
/*  891 */     if (this.enabled)
/*      */     {
/*  893 */       this.adapter.setCurrentDownloadLimit(bytes_per_second);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentProtocolUploadSpeed()
/*      */   {
/*  900 */     return this.adapter.getCurrentProtocolUploadSpeed(-1);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentDataUploadSpeed()
/*      */   {
/*  906 */     return this.adapter.getCurrentDataUploadSpeed(-1);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentDataDownloadSpeed()
/*      */   {
/*  912 */     return this.adapter.getCurrentDataDownloadSpeed(-1);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCurrentProtocolDownloadSpeed()
/*      */   {
/*  918 */     return this.adapter.getCurrentProtocolDownloadSpeed(-1);
/*      */   }
/*      */   
/*      */ 
/*      */   private void autoAdjust()
/*      */   {
/*  924 */     if (COConfigurationManager.getBooleanParameter("Auto Adjust Transfer Defaults"))
/*      */     {
/*  926 */       int up_limit_bytes_per_sec = getEstimatedUploadCapacityBytesPerSec().getBytesPerSec();
/*  927 */       int down_limit_bytes_per_sec = getEstimatedDownloadCapacityBytesPerSec().getBytesPerSec();
/*      */       
/*  929 */       int up_kbs = up_limit_bytes_per_sec / 1024;
/*      */       
/*      */ 
/*  932 */       int[][] settings = { { 56, 2, 20, 40 }, { 96, 3, 30, 60 }, { 128, 3, 40, 80 }, { 192, 4, 50, 100 }, { 256, 4, 60, 200 }, { 512, 5, 70, 300 }, { 1024, 6, 80, 400 }, { 2048, 8, 90, 500 }, { 5120, 10, 100, 600 }, { 10240, 20, 110, 750 }, { 20480, 30, 120, 900 }, { 51200, 40, 130, 1100 }, { 102400, 50, 140, 1300 }, { -1, 60, 150, 1500 } };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  950 */       int[] selected = settings[(settings.length - 1)];
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  955 */       for (int i = 3; i < settings.length; i++)
/*      */       {
/*  957 */         int[] setting = settings[i];
/*      */         
/*  959 */         int line_kilobit_sec = setting[0];
/*      */         
/*      */ 
/*      */ 
/*  963 */         int limit = line_kilobit_sec / 8 * 4 / 5;
/*      */         
/*  965 */         if (up_kbs <= limit)
/*      */         {
/*  967 */           selected = setting;
/*      */           
/*  969 */           break;
/*      */         }
/*      */       }
/*      */       
/*  973 */       int upload_slots = selected[1];
/*  974 */       int connections_torrent = selected[2];
/*  975 */       int connections_global = selected[3];
/*      */       
/*      */ 
/*  978 */       if (upload_slots != COConfigurationManager.getIntParameter("Max Uploads"))
/*      */       {
/*  980 */         COConfigurationManager.setParameter("Max Uploads", upload_slots);
/*  981 */         COConfigurationManager.setParameter("Max Uploads Seeding", upload_slots);
/*      */       }
/*      */       
/*  984 */       if (connections_torrent != COConfigurationManager.getIntParameter("Max.Peer.Connections.Per.Torrent"))
/*      */       {
/*  986 */         COConfigurationManager.setParameter("Max.Peer.Connections.Per.Torrent", connections_torrent);
/*      */         
/*  988 */         COConfigurationManager.setParameter("Max.Peer.Connections.Per.Torrent.When.Seeding", connections_torrent / 2);
/*      */       }
/*      */       
/*  991 */       if (connections_global != COConfigurationManager.getIntParameter("Max.Peer.Connections.Total"))
/*      */       {
/*  993 */         COConfigurationManager.setParameter("Max.Peer.Connections.Total", connections_global);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setLoggingEnabled(boolean enabled)
/*      */   {
/* 1002 */     COConfigurationManager.setParameter("Auto Upload Speed Debug Enabled", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void log(String str)
/*      */   {
/* 1009 */     if (DEBUG)
/*      */     {
/* 1011 */       this.logger.log(str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void informDownCapChanged()
/*      */   {
/* 1018 */     informListeners(3);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void informUpCapChanged()
/*      */   {
/* 1024 */     informListeners(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informListeners(int type)
/*      */   {
/* 1031 */     Iterator it = this.listeners.iterator();
/*      */     
/* 1033 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1036 */         ((SpeedManagerListener)it.next()).propertyChanged(type);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1040 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(SpeedManagerListener l)
/*      */   {
/* 1049 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(SpeedManagerListener l)
/*      */   {
/* 1056 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1063 */     writer.println("SpeedManager: enabled=" + this.enabled + ",provider=" + this.provider);
/*      */     try
/*      */     {
/* 1066 */       writer.indent();
/*      */       
/* 1068 */       this.ping_mapper.generateEvidence(writer);
/*      */     }
/*      */     finally
/*      */     {
/* 1072 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class pingContact
/*      */     implements SpeedManagerPingSource
/*      */   {
/*      */     private final DHTSpeedTesterContact contact;
/*      */     
/*      */     private int ping_time;
/*      */     
/*      */ 
/*      */     protected pingContact(DHTSpeedTesterContact _contact)
/*      */     {
/* 1088 */       this.contact = _contact;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setPingTime(int time)
/*      */     {
/* 1095 */       this.ping_time = time;
/*      */     }
/*      */     
/*      */ 
/*      */     public InetSocketAddress getAddress()
/*      */     {
/* 1101 */       return this.contact.getAddress();
/*      */     }
/*      */     
/*      */ 
/*      */     public int getPingTime()
/*      */     {
/* 1107 */       return this.ping_time;
/*      */     }
/*      */     
/*      */ 
/*      */     public void destroy()
/*      */     {
/* 1113 */       this.contact.destroy();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class SMUnlimited
/*      */     implements SpeedManagerAlgorithmProvider
/*      */   {
/*      */     private int good_signals;
/*      */     
/*      */ 
/*      */     protected SMUnlimited() {}
/*      */     
/*      */ 
/*      */     public void destroy() {}
/*      */     
/*      */     public void reset()
/*      */     {
/* 1131 */       SpeedManagerImpl.this.adapter.setCurrentDownloadLimit(0);
/* 1132 */       SpeedManagerImpl.this.adapter.setCurrentUploadLimit(0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void updateStats() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void pingSourceFailed(SpeedManagerPingSource source) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void calculate(SpeedManagerPingSource[] sources)
/*      */     {
/* 1159 */       SpeedManagerLimitEstimate est = SpeedManagerImpl.this.ping_mapper.getEstimatedUploadLimit(true);
/*      */       
/* 1161 */       if (est != null)
/*      */       {
/* 1163 */         double metric_rating = SpeedManagerImpl.this.ping_mapper.getCurrentMetricRating();
/*      */         
/* 1165 */         if (metric_rating == 1.0D)
/*      */         {
/* 1167 */           this.good_signals += 1;
/*      */         }
/*      */         else
/*      */         {
/* 1171 */           this.good_signals = 0;
/*      */         }
/*      */         
/* 1174 */         if (metric_rating == -1.0D)
/*      */         {
/* 1176 */           SpeedManagerImpl.this.adapter.setCurrentUploadLimit(est.getBytesPerSec() + (this.good_signals < 3 ? 64512 : 1024));
/*      */         }
/* 1178 */         else if (metric_rating <= 0.0D)
/*      */         {
/* 1180 */           SpeedManagerImpl.this.adapter.setCurrentUploadLimit(est.getBytesPerSec() + 1024);
/*      */         }
/*      */         else
/*      */         {
/* 1184 */           SpeedManagerImpl.this.adapter.setCurrentUploadLimit(est.getBytesPerSec() + 5120);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public int getIdlePingMillis()
/*      */     {
/* 1193 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getCurrentPingMillis()
/*      */     {
/* 1199 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMaxPingMillis()
/*      */     {
/* 1205 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getCurrentChokeSpeed()
/*      */     {
/* 1211 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMaxUploadSpeed()
/*      */     {
/* 1217 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getAdjustsDownloadLimits()
/*      */     {
/* 1223 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static class nullProvider
/*      */     implements SpeedManagerAlgorithmProvider
/*      */   {
/*      */     public void reset() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void destroy() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void updateStats() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void pingSourceFailed(SpeedManagerPingSource source) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void calculate(SpeedManagerPingSource[] sources) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getIdlePingMillis()
/*      */     {
/* 1268 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getCurrentPingMillis()
/*      */     {
/* 1274 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMaxPingMillis()
/*      */     {
/* 1280 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getCurrentChokeSpeed()
/*      */     {
/* 1286 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMaxUploadSpeed()
/*      */     {
/* 1292 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean getAdjustsDownloadLimits()
/*      */     {
/* 1298 */       return false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/SpeedManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */