/*      */ package com.aelitis.azureus.core.metasearch.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.custom.Customization;
/*      */ import com.aelitis.azureus.core.custom.CustomizationManager;
/*      */ import com.aelitis.azureus.core.custom.CustomizationManagerFactory;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformMetaSearchMessenger;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformMetaSearchMessenger.templateDetails;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformMetaSearchMessenger.templateInfo;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchException;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManager;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchManagerListener;
/*      */ import com.aelitis.azureus.core.metasearch.Result;
/*      */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*      */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*      */ import com.aelitis.azureus.core.metasearch.impl.plugin.PluginEngine;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*      */ import com.aelitis.azureus.util.ConstantsVuze;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import java.io.File;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils.torrentAttributeListener;
/*      */ import org.gudy.azureus2.core3.xml.util.XUXmlWriter;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureDetails;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureManagerListener;
/*      */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.search.Search;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchException;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchInstance;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchListener;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchObserver;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchProvider;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchProviderResults;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchResult;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.UtilitiesImpl.searchManager;
/*      */ 
/*      */ 
/*      */ public class MetaSearchManagerImpl
/*      */   implements MetaSearchManager, UtilitiesImpl.searchManager, AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static final boolean AUTO_MODE_DEFAULT = true;
/*      */   private static final String LOGGER_NAME = "MetaSearch";
/*      */   private static final int REFRESH_MILLIS = 82800000;
/*      */   private static MetaSearchManagerImpl singleton;
/*      */   private MetaSearchImpl meta_search;
/*      */   
/*      */   public static void preInitialise()
/*      */   {
/*   93 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void process(VuzeFile[] files, int expected_types)
/*      */       {
/*      */ 
/*      */ 
/*  101 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  103 */           VuzeFile vf = files[i];
/*      */           
/*  105 */           VuzeFileComponent[] comps = vf.getComponents();
/*      */           
/*  107 */           for (int j = 0; j < comps.length; j++)
/*      */           {
/*  109 */             VuzeFileComponent comp = comps[j];
/*      */             
/*  111 */             int comp_type = comp.getType();
/*      */             
/*  113 */             if (comp_type == 1)
/*      */             {
/*      */               try {
/*  116 */                 Engine e = MetaSearchManagerImpl.getSingleton().importEngine(comp.getContent(), (expected_types & 0x1) == 0);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  121 */                 comp.setProcessed();
/*      */                 
/*  123 */                 if (e != null)
/*      */                 {
/*  125 */                   comp.setData(Engine.VUZE_FILE_COMPONENT_ENGINE_KEY, e);
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/*  129 */                 Debug.printStackTrace(e);
/*      */               }
/*  131 */             } else if (comp_type == 256)
/*      */             {
/*  133 */               MetaSearchManagerImpl.getSingleton().addOperation(comp.getContent());
/*      */               
/*  135 */               comp.setProcessed();
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*  141 */     });
/*  142 */     TorrentUtils.addTorrentAttributeListener(new TorrentUtils.torrentAttributeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void attributeSet(TOTorrent torrent, String attribute, Object value)
/*      */       {
/*      */ 
/*      */ 
/*  151 */         if ((attribute == "obtained_from") && (!TorrentUtils.isReallyPrivate(torrent)))
/*      */         {
/*      */           try
/*      */           {
/*  155 */             MetaSearchManagerImpl.getSingleton().checkPotentialAssociations(torrent.getHash(), (String)value);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  159 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public static synchronized MetaSearchManagerImpl getSingleton()
/*      */   {
/*  169 */     if (singleton == null)
/*      */     {
/*  171 */       singleton = new MetaSearchManagerImpl();
/*      */     }
/*  173 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*  177 */   private AsyncDispatcher dispatcher = new AsyncDispatcher(10000);
/*      */   
/*  179 */   private AESemaphore initial_refresh_sem = new AESemaphore("MetaSearch:initrefresh");
/*      */   
/*  181 */   private AESemaphore refresh_sem = new AESemaphore("MetaSearch:refresh", 1);
/*      */   
/*      */   private boolean checked_customization;
/*      */   
/*  185 */   private AsyncDispatcher op_dispatcher = new AsyncDispatcher(5000);
/*  186 */   private List<MetaSearchManagerListener> listeners = new ArrayList();
/*  187 */   private List<Map> operations = new ArrayList();
/*      */   
/*      */   private String extension_key;
/*      */   
/*  191 */   private Map<String, EngineImpl> potential_associations = new LinkedHashMap(32, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, EngineImpl> eldest)
/*      */     {
/*      */ 
/*  198 */       return size() > 32;
/*      */     }
/*      */   };
/*      */   private boolean proxy_requests_enabled;
/*      */   
/*      */   protected MetaSearchManagerImpl()
/*      */   {
/*  205 */     COConfigurationManager.addAndFireParameterListener("metasearch.config.proxy.enable", new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*  211 */         MetaSearchManagerImpl.this.proxy_requests_enabled = COConfigurationManager.getBooleanParameter("metasearch.config.proxy.enable", false);
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  218 */     });
/*  219 */     this.meta_search = new MetaSearchImpl(this);
/*      */     
/*  221 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  223 */     this.extension_key = COConfigurationManager.getStringParameter("metasearch.extkey.latest", "");
/*      */     
/*  225 */     if (this.extension_key.length() == 0)
/*      */     {
/*  227 */       this.extension_key = null;
/*      */     }
/*      */     
/*  230 */     setupExtensions();
/*      */     
/*  232 */     SimpleTimer.addPeriodicEvent("MetaSearchRefresh", 82800000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  241 */         MetaSearchManagerImpl.this.refresh();
/*      */       }
/*      */       
/*  244 */     });
/*  245 */     refresh();
/*      */     
/*  247 */     UtilitiesImpl.addSearchManager(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addProvider(PluginInterface pi, SearchProvider provider)
/*      */   {
/*  255 */     String id = pi.getPluginID() + "." + provider.getProperty(1);
/*      */     try
/*      */     {
/*  258 */       this.meta_search.importFromPlugin(id, provider);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  262 */       Debug.out("Failed to add search provider '" + id + "' (" + provider + ")", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeProvider(PluginInterface pi, SearchProvider provider)
/*      */   {
/*  271 */     String id = pi.getPluginID() + "." + provider.getProperty(1);
/*      */     try
/*      */     {
/*  274 */       Engine[] engines = this.meta_search.getEngines(false, false);
/*      */       
/*  276 */       for (Engine engine : engines)
/*      */       {
/*  278 */         if ((engine instanceof PluginEngine))
/*      */         {
/*  280 */           PluginEngine pe = (PluginEngine)engine;
/*      */           
/*  282 */           if (pe.getProvider() == provider)
/*      */           {
/*  284 */             engine.delete();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  290 */       Debug.out("Failed to remove search provider '" + id + "' (" + provider + ")", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public SearchProvider[] getProviders()
/*      */   {
/*  297 */     Engine[] engines = this.meta_search.getEngines(true, false);
/*      */     
/*  299 */     SearchProvider[] result = new SearchProvider[engines.length];
/*      */     
/*  301 */     for (int i = 0; i < engines.length; i++)
/*      */     {
/*  303 */       result[i] = new engineInfo(engines[i]);
/*      */     }
/*      */     
/*  306 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Engine getEngine(SearchProvider sp)
/*      */   {
/*  313 */     Engine[] engines = this.meta_search.getEngines(false, false);
/*      */     
/*  315 */     for (Engine engine : engines)
/*      */     {
/*  317 */       if ((engine instanceof PluginEngine))
/*      */       {
/*  319 */         PluginEngine pe = (PluginEngine)engine;
/*      */         
/*  321 */         if (pe.getProvider() == sp)
/*      */         {
/*  323 */           return pe;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  328 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Search createSearch(String provider_ids, String properties_str)
/*      */     throws SearchException
/*      */   {
/*  338 */     String[] bits = XUXmlWriter.splitWithEscape(provider_ids, ',');
/*      */     
/*  340 */     long[] pids = new long[bits.length];
/*      */     
/*  342 */     for (int i = 0; i < bits.length; i++)
/*      */     {
/*  344 */       pids[i] = Long.parseLong(bits[i]);
/*      */     }
/*      */     
/*  347 */     Map<String, String> properties = new HashMap();
/*      */     
/*  349 */     bits = XUXmlWriter.splitWithEscape(properties_str, ',');
/*      */     
/*  351 */     for (int i = 0; i < bits.length; i++)
/*      */     {
/*  353 */       String[] x = XUXmlWriter.splitWithEscape(bits[i], '=');
/*      */       
/*  355 */       properties.put(x[0].trim(), x[1].trim());
/*      */     }
/*      */     
/*  358 */     return createSearch(pids, properties, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Search createSearch(SearchProvider[] providers, Map<String, String> properties, SearchListener listener)
/*      */     throws SearchException
/*      */   {
/*      */     long[] pids;
/*      */     
/*      */ 
/*      */     long[] pids;
/*      */     
/*  371 */     if (providers == null)
/*      */     {
/*  373 */       pids = new long[0];
/*      */     }
/*      */     else
/*      */     {
/*  377 */       pids = new long[providers.length];
/*      */       
/*  379 */       for (int i = 0; i < pids.length; i++)
/*      */       {
/*  381 */         Long id = (Long)providers[i].getProperty(0);
/*      */         
/*  383 */         if (id == null)
/*      */         {
/*  385 */           throw new SearchException("Unknown provider - no id available");
/*      */         }
/*      */         
/*  388 */         pids[i] = id.longValue();
/*      */       }
/*      */     }
/*      */     
/*  392 */     return createSearch(pids, properties, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Search createSearch(long[] provider_ids, Map<String, String> properties, SearchListener listener)
/*      */     throws SearchException
/*      */   {
/*  403 */     List<SearchParameter> sps = new ArrayList();
/*      */     
/*  405 */     String search_term = (String)properties.get("search_term");
/*      */     
/*  407 */     if (search_term == null)
/*      */     {
/*  409 */       throw new SearchException("Search term is mandatory");
/*      */     }
/*      */     
/*  412 */     sps.add(new SearchParameter("s", search_term));
/*      */     
/*  414 */     String mature = (String)properties.get("mature");
/*      */     
/*  416 */     if (mature != null)
/*      */     {
/*  418 */       sps.add(new SearchParameter("m", mature.toString()));
/*      */     }
/*      */     
/*  421 */     SearchParameter[] parameters = (SearchParameter[])sps.toArray(new SearchParameter[sps.size()]);
/*      */     
/*  423 */     Map<String, String> context = new HashMap();
/*      */     
/*  425 */     context.put("force_full", "true");
/*      */     
/*  427 */     String headers = null;
/*  428 */     int max_per_engine = 256;
/*      */     
/*  430 */     SearchObject search = new SearchObject(listener);
/*      */     
/*      */     Engine[] used_engines;
/*      */     Engine[] used_engines;
/*  434 */     if (provider_ids.length == 0)
/*      */     {
/*  436 */       used_engines = getMetaSearch().search(search, parameters, headers, context, max_per_engine);
/*      */     }
/*      */     else
/*      */     {
/*  440 */       List<Engine> selected_engines = new ArrayList();
/*      */       
/*  442 */       for (long id : provider_ids)
/*      */       {
/*  444 */         Engine engine = this.meta_search.getEngine(id);
/*      */         
/*  446 */         if (engine == null)
/*      */         {
/*  448 */           throw new SearchException("Unknown engine id - " + id);
/*      */         }
/*      */         
/*      */ 
/*  452 */         selected_engines.add(engine);
/*      */       }
/*      */       
/*      */ 
/*  456 */       Engine[] engines = (Engine[])selected_engines.toArray(new Engine[selected_engines.size()]);
/*      */       
/*  458 */       used_engines = getMetaSearch().search(engines, search, parameters, headers, context, max_per_engine);
/*      */     }
/*      */     
/*  461 */     search.setEnginesUsed(used_engines);
/*      */     
/*  463 */     return search;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void refresh()
/*      */   {
/*  469 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  475 */         if (MetaSearchManagerImpl.this.dispatcher.getQueueSize() == 0) {
/*      */           try
/*      */           {
/*  478 */             MetaSearchManagerImpl.this.syncRefresh();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void ensureEnginesUpToDate()
/*      */   {
/*  491 */     long timeout = this.meta_search.getEngineCount() == 0 ? 30000L : 10000L;
/*      */     
/*  493 */     if (!this.initial_refresh_sem.reserve(timeout))
/*      */     {
/*  495 */       log("Timeout waiting for initial refresh to complete, continuing");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void syncRefresh()
/*      */     throws MetaSearchException
/*      */   {
/*  504 */     boolean refresh_completed = false;
/*  505 */     boolean first_run = false;
/*      */     try
/*      */     {
/*  508 */       this.refresh_sem.reserve();
/*      */       
/*  510 */       first_run = COConfigurationManager.getBooleanParameter("metasearch.refresh.first_run", true);
/*      */       
/*  512 */       if (!this.checked_customization)
/*      */       {
/*  514 */         this.checked_customization = true;
/*      */         
/*  516 */         CustomizationManager cust_man = CustomizationManagerFactory.getSingleton();
/*      */         
/*  518 */         Customization cust = cust_man.getActiveCustomization();
/*      */         
/*  520 */         if (cust != null)
/*      */         {
/*  522 */           String cust_name = COConfigurationManager.getStringParameter("metasearch.custom.name", "");
/*  523 */           String cust_version = COConfigurationManager.getStringParameter("metasearch.custom.version", "0");
/*      */           
/*  525 */           boolean new_name = !cust_name.equals(cust.getName());
/*  526 */           boolean new_version = Constants.compareVersions(cust_version, cust.getVersion()) < 0;
/*      */           
/*  528 */           if ((new_name) || (new_version))
/*      */           {
/*  530 */             log("Customization: checking templates for " + cust.getName() + "/" + cust.getVersion());
/*      */             try
/*      */             {
/*  533 */               streams = cust.getResources("metasearch");
/*      */               
/*  535 */               if ((streams.length > 0) && (new_name))
/*      */               {
/*      */ 
/*      */ 
/*  539 */                 log("    setting auto-mode to false");
/*      */                 
/*  541 */                 setAutoMode(false);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  556 */               for (i = 0; i < streams.length;)
/*      */               {
/*  558 */                 InputStream is = streams[i];
/*      */                 try
/*      */                 {
/*  561 */                   VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(is);
/*      */                   
/*  563 */                   if (vf != null)
/*      */                   {
/*  565 */                     VuzeFileComponent[] comps = vf.getComponents();
/*      */                     
/*  567 */                     for (int j = 0; j < comps.length; j++)
/*      */                     {
/*  569 */                       VuzeFileComponent comp = comps[j];
/*      */                       
/*  571 */                       if (comp.getType() == 1) {
/*      */                         try
/*      */                         {
/*  574 */                           Engine e = getSingleton().importEngine(comp.getContent(), false);
/*      */                           
/*      */ 
/*  577 */                           log("    updated " + e.getName());
/*      */                           
/*  579 */                           e.setSelectionState(2);
/*      */                         }
/*      */                         catch (Throwable e)
/*      */                         {
/*  583 */                           Debug.printStackTrace(e);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/*      */                   try
/*      */                   {
/*  591 */                     is.close();
/*      */                   }
/*      */                   catch (Throwable e) {}
/*  556 */                   i++;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 finally
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   try
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  591 */                     is.close();
/*      */                   }
/*      */                   catch (Throwable e) {}
/*      */                 }
/*      */               }
/*      */             } finally {
/*      */               InputStream[] streams;
/*      */               int i;
/*  599 */               COConfigurationManager.setParameter("metasearch.custom.name", cust.getName());
/*  600 */               COConfigurationManager.setParameter("metasearch.custom.version", cust.getVersion());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  606 */       log("Refreshing engines");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  612 */       Map<Long, PlatformMetaSearchMessenger.templateInfo> vuze_selected_ids = new HashMap();
/*  613 */       Map<Long, PlatformMetaSearchMessenger.templateInfo> vuze_preload_ids = new HashMap();
/*      */       
/*  615 */       Set<Long> featured_ids = new HashSet();
/*  616 */       Set<Long> popular_ids = new HashSet();
/*  617 */       Set<Long> manual_vuze_ids = new HashSet();
/*      */       
/*  619 */       boolean auto_mode = isAutoMode();
/*      */       
/*  621 */       Engine[] engines = this.meta_search.getEngines(false, false);
/*      */       
/*  623 */       String fud = this.meta_search.getFUD();
/*      */       try
/*      */       {
/*  626 */         PlatformMetaSearchMessenger.templateInfo[] featured = PlatformMetaSearchMessenger.listFeaturedTemplates(this.extension_key, fud);
/*      */         
/*  628 */         String featured_str = "";
/*      */         
/*  630 */         for (int i = 0; i < featured.length; i++)
/*      */         {
/*  632 */           PlatformMetaSearchMessenger.templateInfo template = featured[i];
/*      */           
/*  634 */           if (template.isVisible())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  639 */             Long key = new Long(template.getId());
/*      */             
/*  641 */             vuze_selected_ids.put(key, template);
/*      */             
/*  643 */             featured_ids.add(key);
/*      */             
/*  645 */             featured_str = featured_str + (featured_str.length() == 0 ? "" : ",") + key;
/*      */           }
/*      */         }
/*  648 */         log("Featured templates: " + featured_str);
/*      */         
/*  650 */         if ((auto_mode) || (first_run))
/*      */         {
/*  652 */           PlatformMetaSearchMessenger.templateInfo[] popular = PlatformMetaSearchMessenger.listTopPopularTemplates(this.extension_key, fud);
/*      */           
/*  654 */           String popular_str = "";
/*  655 */           String preload_str = "";
/*      */           
/*  657 */           for (int i = 0; i < popular.length; i++)
/*      */           {
/*  659 */             PlatformMetaSearchMessenger.templateInfo template = popular[i];
/*      */             
/*  661 */             if (template.isVisible())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  666 */               Long key = new Long(template.getId());
/*      */               
/*  668 */               if (auto_mode)
/*      */               {
/*  670 */                 if (!vuze_selected_ids.containsKey(key))
/*      */                 {
/*  672 */                   vuze_selected_ids.put(key, template);
/*      */                   
/*  674 */                   popular_ids.add(key);
/*      */                   
/*  676 */                   popular_str = popular_str + (popular_str.length() == 0 ? "" : ",") + key;
/*      */                 }
/*      */                 
/*      */               }
/*  680 */               else if (!vuze_preload_ids.containsKey(key))
/*      */               {
/*  682 */                 vuze_preload_ids.put(key, template);
/*      */                 
/*  684 */                 preload_str = preload_str + (preload_str.length() == 0 ? "" : ",") + key;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  689 */           log("Popular templates: " + popular_str);
/*      */           
/*  691 */           if (preload_str.length() > 0)
/*      */           {
/*  693 */             log("Pre-load templates: " + popular_str);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  699 */         String manual_str = "";
/*      */         
/*  701 */         for (int i = 0; i < engines.length; i++)
/*      */         {
/*  703 */           Engine engine = engines[i];
/*      */           
/*  705 */           Long key = new Long(engine.getId());
/*      */           
/*  707 */           if ((engine.getSource() == 1) && (engine.getSelectionState() == 2) && (!vuze_selected_ids.containsKey(key)))
/*      */           {
/*      */ 
/*      */ 
/*  711 */             manual_vuze_ids.add(key);
/*      */           }
/*      */         }
/*      */         
/*  715 */         if (manual_vuze_ids.size() > 0)
/*      */         {
/*  717 */           long[] manual_ids = new long[manual_vuze_ids.size()];
/*      */           
/*  719 */           Iterator<Long> it = manual_vuze_ids.iterator();
/*      */           
/*  721 */           int pos = 0;
/*      */           
/*  723 */           while (it.hasNext())
/*      */           {
/*  725 */             manual_ids[(pos++)] = ((Long)it.next()).longValue();
/*      */           }
/*      */           
/*  728 */           PlatformMetaSearchMessenger.templateInfo[] manual = PlatformMetaSearchMessenger.getTemplateDetails(this.extension_key, manual_ids);
/*      */           
/*  730 */           for (int i = 0; i < manual.length; i++)
/*      */           {
/*  732 */             PlatformMetaSearchMessenger.templateInfo template = manual[i];
/*      */             
/*  734 */             if (template.isVisible())
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  739 */               Long key = new Long(template.getId());
/*      */               
/*  741 */               vuze_selected_ids.put(key, template);
/*      */               
/*  743 */               manual_str = manual_str + (manual_str.length() == 0 ? "" : ",") + key;
/*      */             }
/*      */           }
/*      */         }
/*  747 */         log("Manual templates: " + manual_str);
/*      */         
/*  749 */         Map<Long, Engine> existing_engine_map = new HashMap();
/*      */         
/*  751 */         String existing_str = "";
/*      */         
/*  753 */         for (int i = 0; i < engines.length; i++)
/*      */         {
/*  755 */           Engine engine = engines[i];
/*      */           
/*  757 */           Long key = new Long(engine.getId());
/*      */           
/*  759 */           existing_engine_map.put(key, engine);
/*      */           
/*  761 */           existing_str = existing_str + (existing_str.length() == 0 ? "" : ",") + key + "[source=" + Engine.ENGINE_SOURCE_STRS[engine.getSource()] + ",type=" + engine.getType() + ",selected=" + Engine.SEL_STATE_STRINGS[engine.getSelectionState()] + "]";
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  767 */         log("Existing templates: " + existing_str);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  773 */         Iterator<Map.Entry<Long, PlatformMetaSearchMessenger.templateInfo>> it = vuze_selected_ids.entrySet().iterator();
/*      */         
/*  775 */         while (it.hasNext())
/*      */         {
/*  777 */           Object entry = (Map.Entry)it.next();
/*      */           
/*  779 */           vuze_preload_ids.remove(((Map.Entry)entry).getKey());
/*      */           
/*  781 */           long id = ((Long)((Map.Entry)entry).getKey()).longValue();
/*      */           
/*  783 */           PlatformMetaSearchMessenger.templateInfo template = (PlatformMetaSearchMessenger.templateInfo)((Map.Entry)entry).getValue();
/*      */           
/*  785 */           long modified = template.getModifiedDate();
/*      */           
/*  787 */           Engine this_engine = (Engine)existing_engine_map.get(new Long(id));
/*      */           
/*  789 */           boolean update = (this_engine == null) || (this_engine.getLastUpdated() < modified);
/*      */           
/*  791 */           if (update)
/*      */           {
/*  793 */             PlatformMetaSearchMessenger.templateDetails details = PlatformMetaSearchMessenger.getTemplate(this.extension_key, id);
/*      */             
/*  795 */             log("Downloading definition of template " + id);
/*  796 */             log(details.getValue());
/*      */             
/*  798 */             if (details.isVisible()) {
/*      */               try
/*      */               {
/*  801 */                 this_engine = this.meta_search.importFromJSONString(details.getType() == 1 ? 2 : 1, details.getId(), details.getModifiedDate(), details.getRankBias(), details.getName(), details.getValue());
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  810 */                 this_engine.setSource(1);
/*      */                 
/*  812 */                 this.meta_search.addEngine(this_engine);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  816 */                 log("Failed to import engine '" + details.getValue() + "'", e);
/*      */               }
/*      */             }
/*  819 */           } else if (this_engine.getRankBias() != template.getRankBias())
/*      */           {
/*  821 */             this_engine.setRankBias(template.getRankBias());
/*      */             
/*  823 */             log("Updating rank bias for " + this_engine.getString() + " to " + template.getRankBias());
/*      */           }
/*      */           else
/*      */           {
/*  827 */             log("Not updating " + this_engine.getString() + " as unchanged");
/*      */           }
/*      */           
/*  830 */           if (this_engine != null)
/*      */           {
/*  832 */             int sel_state = this_engine.getSelectionState();
/*      */             
/*  834 */             if (sel_state == 0)
/*      */             {
/*  836 */               log("Auto-selecting " + this_engine.getString());
/*      */               
/*  838 */               this_engine.setSelectionState(1);
/*      */             }
/*  840 */             else if ((auto_mode) && (sel_state == 2))
/*      */             {
/*  842 */               log("Switching Manual to Auto select for " + this_engine.getString());
/*      */               
/*  844 */               this_engine.setSelectionState(1);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  851 */         it = vuze_preload_ids.entrySet().iterator();
/*      */         
/*  853 */         while (it.hasNext())
/*      */         {
/*  855 */           Object entry = (Map.Entry)it.next();
/*      */           
/*  857 */           long id = ((Long)((Map.Entry)entry).getKey()).longValue();
/*      */           
/*  859 */           Engine this_engine = (Engine)existing_engine_map.get(new Long(id));
/*      */           
/*  861 */           if (this_engine == null)
/*      */           {
/*  863 */             PlatformMetaSearchMessenger.templateDetails details = PlatformMetaSearchMessenger.getTemplate(this.extension_key, id);
/*      */             
/*  865 */             log("Downloading pre-load definition of template " + id);
/*  866 */             log(details.getValue());
/*      */             
/*  868 */             if (details.isVisible()) {
/*      */               try
/*      */               {
/*  871 */                 this_engine = this.meta_search.importFromJSONString(details.getType() == 1 ? 2 : 1, details.getId(), details.getModifiedDate(), details.getRankBias(), details.getName(), details.getValue());
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  880 */                 this_engine.setSource(1);
/*      */                 
/*  882 */                 this_engine.setSelectionState(0);
/*      */                 
/*  884 */                 this.meta_search.addEngine(this_engine);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  888 */                 log("Failed to import engine '" + details.getValue() + "'", e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  896 */         for (int i = 0; i < engines.length; i++)
/*      */         {
/*  898 */           Engine engine = engines[i];
/*      */           
/*  900 */           if ((engine.getSource() == 1) && (engine.getSelectionState() == 1) && (!vuze_selected_ids.containsKey(new Long(engine.getId()))))
/*      */           {
/*      */ 
/*      */ 
/*  904 */             log("Deselecting " + engine.getString() + " as no longer visible on Vuze");
/*      */             
/*  906 */             engine.setSelectionState(0);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  912 */         for (int i = 0; i < engines.length; i++)
/*      */         {
/*  914 */           Engine engine = engines[i];
/*      */           
/*  916 */           if ((engine.getSource() == 1) && (engine.getSelectionState() == 2))
/*      */           {
/*      */ 
/*  919 */             engine.recordSelectionState();
/*      */           }
/*      */           else
/*      */           {
/*  923 */             engine.checkSelectionStateRecorded();
/*      */           }
/*      */         }
/*      */         
/*  927 */         refresh_completed = true;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  931 */         log("Refresh failed", e);
/*      */         
/*  933 */         throw new MetaSearchException("Refresh failed", e);
/*      */       }
/*      */     }
/*      */     finally {
/*  937 */       if ((first_run) && (refresh_completed))
/*      */       {
/*  939 */         COConfigurationManager.setParameter("metasearch.refresh.first_run", false);
/*      */       }
/*      */       
/*  942 */       this.refresh_sem.release();
/*      */       
/*  944 */       this.initial_refresh_sem.releaseForever();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public MetaSearch getMetaSearch()
/*      */   {
/*  951 */     return this.meta_search;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAutoMode()
/*      */   {
/*  957 */     return COConfigurationManager.getBooleanParameter("metasearch.auto.mode", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setAutoMode(boolean auto)
/*      */   {
/*  964 */     COConfigurationManager.setParameter("metasearch.auto.mode", auto);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSelectedEngines(long[] ids, boolean auto)
/*      */     throws MetaSearchException
/*      */   {
/*      */     try
/*      */     {
/*  975 */       String s = "";
/*      */       
/*  977 */       for (int i = 0; i < ids.length; i++)
/*      */       {
/*  979 */         s = s + (i == 0 ? "" : ",") + ids[i];
/*      */       }
/*      */       
/*  982 */       log("setSelectedIds: " + s + ", auto=" + auto);
/*      */       
/*      */ 
/*      */ 
/*  986 */       COConfigurationManager.setParameter("metasearch.auto.mode", auto);
/*      */       
/*  988 */       Engine[] engines = this.meta_search.getEngines(false, false);
/*      */       
/*  990 */       Map<Long, Engine> engine_map = new HashMap();
/*      */       
/*  992 */       for (int i = 0; i < engines.length; i++)
/*      */       {
/*  994 */         engine_map.put(new Long(engines[i].getId()), engines[i]);
/*      */       }
/*      */       
/*  997 */       Set<Engine> selected_engine_set = new HashSet();
/*      */       
/*  999 */       for (int i = 0; i < ids.length; i++)
/*      */       {
/* 1001 */         long id = ids[i];
/*      */         
/* 1003 */         Engine existing = (Engine)engine_map.get(new Long(id));
/*      */         
/* 1005 */         if (existing != null)
/*      */         {
/* 1007 */           existing.setSelectionState(2);
/*      */           
/* 1009 */           selected_engine_set.add(existing);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1015 */       syncRefresh();
/*      */       
/* 1017 */       engines = this.meta_search.getEngines(false, false);
/*      */       
/*      */ 
/*      */ 
/* 1021 */       for (int i = 0; i < ids.length; i++)
/*      */       {
/* 1023 */         long id = ids[i];
/*      */         
/* 1025 */         Engine existing = (Engine)engine_map.get(new Long(id));
/*      */         
/* 1027 */         if (existing == null)
/*      */         {
/* 1029 */           PlatformMetaSearchMessenger.templateDetails details = PlatformMetaSearchMessenger.getTemplate(this.extension_key, id);
/*      */           
/* 1031 */           log("Downloading definition of template " + id);
/* 1032 */           log(details.getValue());
/*      */           
/* 1034 */           Engine new_engine = this.meta_search.importFromJSONString(details.getType() == 1 ? 2 : 1, details.getId(), details.getModifiedDate(), details.getRankBias(), details.getName(), details.getValue());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1043 */           new_engine.setSelectionState(2);
/*      */           
/* 1045 */           new_engine.setSource(1);
/*      */           
/* 1047 */           this.meta_search.addEngine(new_engine);
/*      */           
/* 1049 */           selected_engine_set.add(new_engine);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1055 */       for (int i = 0; i < engines.length; i++)
/*      */       {
/* 1057 */         Engine e = engines[i];
/*      */         
/* 1059 */         if (e.getSelectionState() == 2)
/*      */         {
/* 1061 */           if (!selected_engine_set.contains(e))
/*      */           {
/* 1063 */             e.setSelectionState(0);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1069 */       e.printStackTrace();
/*      */       
/* 1071 */       if ((e instanceof MetaSearchException))
/*      */       {
/* 1073 */         throw ((MetaSearchException)e);
/*      */       }
/*      */       
/* 1076 */       throw new MetaSearchException("Failed to set selected engines", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine addEngine(long id, int type, String name, String json_value)
/*      */     throws MetaSearchException
/*      */   {
/* 1089 */     if (id == -1L)
/*      */     {
/* 1091 */       id = getLocalTemplateID();
/*      */     }
/*      */     try
/*      */     {
/* 1095 */       Engine engine = this.meta_search.importFromJSONString(type, id, SystemTime.getCurrentTime(), 1.0F, name, json_value);
/*      */       
/*      */ 
/*      */ 
/* 1099 */       engine.setSource(2);
/*      */       
/* 1101 */       engine.setSelectionState(2);
/*      */       
/* 1103 */       this.meta_search.addEngine(engine);
/*      */       
/* 1105 */       return engine;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1109 */       throw new MetaSearchException("Failed to add engine", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isImportable(VuzeFile vf)
/*      */   {
/* 1117 */     VuzeFileComponent[] comps = vf.getComponents();
/*      */     
/* 1119 */     for (int j = 0; j < comps.length; j++)
/*      */     {
/* 1121 */       VuzeFileComponent comp = comps[j];
/*      */       
/* 1123 */       int comp_type = comp.getType();
/*      */       
/* 1125 */       if (comp_type == 1) {
/*      */         try
/*      */         {
/* 1128 */           EngineImpl engine = (EngineImpl)this.meta_search.importFromBEncodedMap(comp.getContent());
/*      */           
/* 1130 */           long id = engine.getId();
/*      */           
/* 1132 */           Engine existing = this.meta_search.getEngine(id);
/*      */           
/* 1134 */           if (existing != null)
/*      */           {
/* 1136 */             int state = existing.getSelectionState();
/*      */             
/* 1138 */             if ((state == 0) || (state == 3) || (!existing.sameLogicAs(engine)))
/*      */             {
/*      */ 
/*      */ 
/* 1142 */               return true;
/*      */             }
/*      */           } else {
/*      */             try {
/* 1146 */               Engine[] engines = this.meta_search.getEngines(false, false);
/*      */               
/* 1148 */               boolean is_new = true;
/*      */               
/* 1150 */               for (Engine e : engines)
/*      */               {
/* 1152 */                 int state = e.getSelectionState();
/*      */                 
/* 1154 */                 if ((state != 0) && (state != 3) && (e.sameLogicAs(engine)))
/*      */                 {
/*      */ 
/*      */ 
/* 1158 */                   is_new = false;
/*      */                   
/* 1160 */                   break;
/*      */                 }
/*      */               }
/*      */               
/* 1164 */               if (is_new)
/*      */               {
/* 1166 */                 return true;
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/* 1176 */     return false;
/*      */   }
/*      */   
/* 1179 */   private static Object import_lock = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine importEngine(Map map, boolean warn_user)
/*      */     throws MetaSearchException
/*      */   {
/* 1188 */     synchronized (import_lock) {
/*      */       try {
/* 1190 */         EngineImpl engine = (EngineImpl)this.meta_search.importFromBEncodedMap(map);
/*      */         
/* 1192 */         long id = engine.getId();
/*      */         
/* 1194 */         Engine existing = this.meta_search.getEngine(id);
/*      */         
/* 1196 */         if (existing != null)
/*      */         {
/* 1198 */           if (existing.sameLogicAs(engine))
/*      */           {
/* 1200 */             if (warn_user)
/*      */             {
/* 1202 */               UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */               
/* 1204 */               String details = MessageText.getString("metasearch.addtemplate.dup.desc", new String[] { engine.getName() });
/*      */               
/*      */ 
/*      */ 
/* 1208 */               ui_manager.showMessageBox("metasearch.addtemplate.dup.title", "!" + details + "!", 1L);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1214 */             return existing;
/*      */           }
/*      */         }
/*      */         else {
/*      */           try {
/* 1219 */             Engine[] engines = this.meta_search.getEngines(false, false);
/*      */             
/* 1221 */             for (Engine e : engines)
/*      */             {
/* 1223 */               if (e.sameLogicAs(engine))
/*      */               {
/* 1225 */                 return e;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/* 1233 */         if (warn_user)
/*      */         {
/* 1235 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 1237 */           String details = MessageText.getString("metasearch.addtemplate.desc", new String[] { engine.getName() });
/*      */           
/*      */ 
/*      */ 
/* 1241 */           long res = ui_manager.showMessageBox("metasearch.addtemplate.title", "!" + details + "!", 12L);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1246 */           if (res != 4L)
/*      */           {
/* 1248 */             throw new MetaSearchException("User declined the template");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1254 */         if ((id >= 0L) && (id < 2147483647L))
/*      */         {
/* 1256 */           id = getLocalTemplateID();
/*      */           
/* 1258 */           engine.setId(id);
/*      */         }
/*      */         
/* 1261 */         engine.setSource(2);
/*      */         
/* 1263 */         engine.setSelectionState(2);
/*      */         
/* 1265 */         this.meta_search.addEngine(engine);
/*      */         
/* 1267 */         if (warn_user)
/*      */         {
/* 1269 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 1271 */           String details = MessageText.getString("metasearch.addtemplate.done.desc", new String[] { engine.getName() });
/*      */           
/*      */ 
/*      */ 
/* 1275 */           ui_manager.showMessageBox("metasearch.addtemplate.done.title", "!" + details + "!", 1L);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1281 */         return engine;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1285 */         if (warn_user)
/*      */         {
/* 1287 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 1289 */           String details = MessageText.getString("metasearch.addtemplate.failed.desc", new String[] { Debug.getNestedExceptionMessage(e) });
/*      */           
/*      */ 
/*      */ 
/* 1293 */           ui_manager.showMessageBox("metasearch.addtemplate.failed.title", "!" + details + "!", 1L);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1299 */         throw new MetaSearchException("Failed to add engine", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addPotentialAssociation(EngineImpl engine, String key)
/*      */   {
/* 1309 */     if ((engine.isShareable()) && (!engine.isAuthenticated()))
/*      */     {
/* 1311 */       synchronized (this.potential_associations)
/*      */       {
/* 1313 */         this.potential_associations.put(key, engine);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkPotentialAssociations(byte[] hash, String key)
/*      */   {
/*      */     EngineImpl engine;
/*      */     
/*      */ 
/* 1325 */     synchronized (this.potential_associations)
/*      */     {
/* 1327 */       engine = (EngineImpl)this.potential_associations.remove(key);
/*      */     }
/*      */     
/* 1330 */     if (engine != null)
/*      */     {
/* 1332 */       Subscription subs = engine.getSubscription();
/*      */       
/* 1334 */       if (subs != null)
/*      */       {
/* 1336 */         subs.setSubscribed(true);
/*      */         
/* 1338 */         subs.addAssociation(hash);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Engine[] loadFromVuzeFile(File file)
/*      */   {
/* 1347 */     VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(file.getAbsolutePath());
/*      */     
/* 1349 */     if (vf != null)
/*      */     {
/* 1351 */       return loadFromVuzeFile(vf);
/*      */     }
/*      */     
/* 1354 */     return new Engine[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Engine[] loadFromVuzeFile(VuzeFile vf)
/*      */   {
/* 1361 */     List<Engine> result = new ArrayList();
/*      */     
/* 1363 */     VuzeFileComponent[] comps = vf.getComponents();
/*      */     
/* 1365 */     for (int j = 0; j < comps.length; j++)
/*      */     {
/* 1367 */       VuzeFileComponent comp = comps[j];
/*      */       
/* 1369 */       if (comp.getType() == 1) {
/*      */         try
/*      */         {
/* 1372 */           result.add(importEngine(comp.getContent(), false));
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1376 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1381 */     return (Engine[])result.toArray(new Engine[result.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLocalTemplateID()
/*      */   {
/* 1387 */     synchronized (this)
/*      */     {
/* 1389 */       Random random = new Random();
/*      */       
/*      */ 
/*      */ 
/* 1393 */       long id = 2147483647L + random.nextInt(Integer.MAX_VALUE);
/*      */       
/* 1395 */       if (this.meta_search.getEngine(id) == null)
/*      */       {
/* 1397 */         return id;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getProxyRequestsEnabled()
/*      */   {
/* 1406 */     return this.proxy_requests_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setProxyRequestsEnabled(boolean enabled)
/*      */   {
/* 1413 */     COConfigurationManager.setParameter("metasearch.config.proxy.enable", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(MetaSearchManagerListener listener)
/*      */   {
/* 1420 */     synchronized (this.listeners)
/*      */     {
/* 1422 */       this.listeners.add(listener);
/*      */     }
/*      */     
/* 1425 */     dispatchOps();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeListener(MetaSearchManagerListener listener) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addOperation(Map map)
/*      */   {
/* 1439 */     synchronized (this.listeners)
/*      */     {
/* 1441 */       this.operations.add(map);
/*      */     }
/*      */     
/* 1444 */     dispatchOps();
/*      */   }
/*      */   
/*      */ 
/*      */   private void dispatchOps()
/*      */   {
/* 1450 */     this.op_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*      */         List<MetaSearchManagerListener> l;
/*      */         
/*      */ 
/*      */         List<Map> o;
/*      */         
/* 1459 */         synchronized (MetaSearchManagerImpl.this.listeners)
/*      */         {
/* 1461 */           if ((MetaSearchManagerImpl.this.listeners.size() == 0) || (MetaSearchManagerImpl.this.operations.size() == 0))
/*      */           {
/* 1463 */             return;
/*      */           }
/*      */           
/* 1466 */           l = new ArrayList(MetaSearchManagerImpl.this.listeners);
/*      */           
/* 1468 */           o = new ArrayList(MetaSearchManagerImpl.this.operations);
/*      */           
/* 1470 */           MetaSearchManagerImpl.this.operations.clear();
/*      */         }
/*      */         
/* 1473 */         for (Iterator i$ = l.iterator(); i$.hasNext();) { listener = (MetaSearchManagerListener)i$.next();
/*      */           
/* 1475 */           for (Map operation : o)
/*      */           {
/*      */             try
/*      */             {
/* 1479 */               int type = ImportExportUtils.importInt(operation, "type", -1);
/*      */               
/* 1481 */               if (type == 1)
/*      */               {
/* 1483 */                 String term = ImportExportUtils.importString(operation, "term", null);
/*      */                 
/* 1485 */                 if (term == null)
/*      */                 {
/* 1487 */                   Debug.out("search term missing");
/*      */                 }
/*      */                 else
/*      */                 {
/* 1491 */                   listener.searchRequest(term);
/*      */                 }
/*      */               }
/*      */               else {
/* 1495 */                 Debug.out("unknown operation type " + type);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 1499 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         MetaSearchManagerListener listener;
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private void setupExtensions()
/*      */   {
/* 1510 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*      */     
/* 1512 */     final FeatureManager fm = pi.getUtilities().getFeatureManager();
/*      */     
/* 1514 */     fm.addListener(new FeatureManager.FeatureManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void licenceAdded(FeatureManager.Licence licence)
/*      */       {
/*      */ 
/* 1521 */         MetaSearchManagerImpl.this.getExtensions(fm, false);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void licenceChanged(FeatureManager.Licence licence)
/*      */       {
/* 1528 */         MetaSearchManagerImpl.this.getExtensions(fm, false);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void licenceRemoved(FeatureManager.Licence licence)
/*      */       {
/* 1535 */         MetaSearchManagerImpl.this.getExtensions(fm, false);
/*      */       }
/*      */     });
/*      */     
/* 1539 */     if (pi.getPluginState().isInitialisationComplete())
/*      */     {
/* 1541 */       getExtensions(fm, true);
/*      */     }
/*      */     else
/*      */     {
/* 1545 */       pi.addListener(new PluginListener()
/*      */       {
/*      */ 
/*      */         public void initializationComplete()
/*      */         {
/*      */ 
/* 1551 */           MetaSearchManagerImpl.this.getExtensions(fm, false);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void closedownInitiated() {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void closedownComplete() {}
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void getExtensions(FeatureManager fm, boolean init)
/*      */   {
/* 1572 */     String existing_ext = this.extension_key;
/* 1573 */     String latest_ext = null;
/*      */     
/* 1575 */     FeatureManager.FeatureDetails[] fds = fm.getFeatureDetails("core");
/*      */     
/* 1577 */     for (FeatureManager.FeatureDetails fd : fds)
/*      */     {
/* 1579 */       if (!fd.hasExpired())
/*      */       {
/* 1581 */         String finger_print = (String)fd.getProperty("Fingerprint");
/*      */         
/* 1583 */         if (finger_print != null)
/*      */         {
/* 1585 */           latest_ext = fd.getLicence().getShortID() + "-" + finger_print;
/*      */           
/* 1587 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1592 */     if (existing_ext != latest_ext)
/*      */     {
/* 1594 */       if ((existing_ext == null) || (latest_ext == null) || (!existing_ext.equals(latest_ext)))
/*      */       {
/* 1596 */         this.extension_key = latest_ext;
/*      */         
/* 1598 */         COConfigurationManager.setParameter("metasearch.extkey.latest", latest_ext == null ? "" : latest_ext);
/*      */         
/* 1600 */         if (!init)
/*      */         {
/* 1602 */           refresh();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getExtensionKey()
/*      */   {
/* 1611 */     return this.extension_key;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void log(String s, Throwable e)
/*      */   {
/* 1619 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("MetaSearch");
/*      */     
/* 1621 */     diag_logger.log(s);
/* 1622 */     diag_logger.log(e);
/*      */     
/* 1624 */     if (ConstantsVuze.DIAG_TO_STDOUT)
/*      */     {
/* 1626 */       System.out.println(Thread.currentThread().getName() + "|" + System.currentTimeMillis() + "] " + s + ": " + Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void log(String s)
/*      */   {
/* 1635 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("MetaSearch");
/*      */     
/* 1637 */     diag_logger.log(s);
/*      */     
/* 1639 */     if (ConstantsVuze.DIAG_TO_STDOUT)
/*      */     {
/* 1641 */       System.out.println(Thread.currentThread().getName() + "|" + System.currentTimeMillis() + "] " + s);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1650 */     writer.println("Metasearch: auto=" + isAutoMode());
/*      */     try
/*      */     {
/* 1653 */       writer.indent();
/*      */       
/* 1655 */       this.meta_search.generate(writer);
/*      */     }
/*      */     finally
/*      */     {
/* 1659 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class SearchObject
/*      */     implements Search, ResultListener
/*      */   {
/*      */     private SearchListener listener;
/*      */     
/* 1669 */     private Map<Long, MetaSearchManagerImpl.engineInfo> engine_map = new HashMap();
/*      */     
/*      */     private boolean engines_set;
/* 1672 */     private List<SearchProviderResults> pending_results = new ArrayList();
/*      */     
/*      */ 
/*      */     private boolean is_complete;
/*      */     
/*      */ 
/*      */     protected SearchObject(SearchListener _listener)
/*      */     {
/* 1680 */       this.listener = _listener;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setEnginesUsed(Engine[] engines)
/*      */     {
/*      */       boolean report_complete;
/*      */       
/* 1689 */       synchronized (this.engine_map)
/*      */       {
/* 1691 */         for (Engine e : engines)
/*      */         {
/* 1693 */           getInfo(e);
/*      */         }
/*      */         
/* 1696 */         this.engines_set = true;
/*      */         
/* 1698 */         report_complete = reportOverallComplete();
/*      */       }
/*      */       
/* 1701 */       if ((this.listener != null) && (report_complete))
/*      */       {
/* 1703 */         this.listener.completed();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean reportOverallComplete()
/*      */     {
/* 1710 */       if ((this.is_complete) || (!this.engines_set))
/*      */       {
/* 1712 */         return false;
/*      */       }
/*      */       
/* 1715 */       for (MetaSearchManagerImpl.engineInfo info : this.engine_map.values())
/*      */       {
/* 1717 */         if (!info.isComplete())
/*      */         {
/* 1719 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 1723 */       this.is_complete = true;
/*      */       
/* 1725 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected MetaSearchManagerImpl.engineInfo getInfo(Engine engine)
/*      */     {
/* 1732 */       synchronized (this.engine_map)
/*      */       {
/* 1734 */         MetaSearchManagerImpl.engineInfo res = (MetaSearchManagerImpl.engineInfo)this.engine_map.get(Long.valueOf(engine.getId()));
/*      */         
/* 1736 */         if (res == null)
/*      */         {
/* 1738 */           res = new MetaSearchManagerImpl.engineInfo(engine);
/*      */           
/* 1740 */           this.engine_map.put(Long.valueOf(engine.getId()), res);
/*      */         }
/*      */         
/* 1743 */         return res;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void contentReceived(Engine engine, String content) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void matchFound(Engine engine, String[] fields) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void resultsReceived(Engine engine, final Result[] results)
/*      */     {
/*      */       SearchProviderResults result;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1770 */       synchronized (this.engine_map)
/*      */       {
/* 1772 */         final MetaSearchManagerImpl.engineInfo info = getInfo(engine);
/*      */         
/* 1774 */         result = new SearchProviderResults()
/*      */         {
/*      */ 
/*      */           public SearchProvider getProvider()
/*      */           {
/*      */ 
/* 1780 */             return info;
/*      */           }
/*      */           
/*      */ 
/*      */           public SearchResult[] getResults()
/*      */           {
/* 1786 */             return MetaSearchManagerImpl.SearchObject.this.wrapResults(results);
/*      */           }
/*      */           
/*      */ 
/*      */           public boolean isComplete()
/*      */           {
/* 1792 */             return false;
/*      */           }
/*      */           
/*      */ 
/*      */           public SearchException getError()
/*      */           {
/* 1798 */             return null;
/*      */           }
/*      */           
/* 1801 */         };
/* 1802 */         this.pending_results.add(result);
/*      */       }
/*      */       
/* 1805 */       if (this.listener != null)
/*      */       {
/* 1807 */         this.listener.receivedResults(new SearchProviderResults[] { result });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void resultsComplete(Engine engine)
/*      */     {
/*      */       boolean report_complete;
/*      */       
/*      */       SearchProviderResults result;
/*      */       
/* 1818 */       synchronized (this.engine_map)
/*      */       {
/* 1820 */         final MetaSearchManagerImpl.engineInfo info = getInfo(engine);
/*      */         
/* 1822 */         info.setComplete();
/*      */         
/* 1824 */         report_complete = reportOverallComplete();
/*      */         
/* 1826 */         result = new SearchProviderResults()
/*      */         {
/*      */ 
/*      */           public SearchProvider getProvider()
/*      */           {
/*      */ 
/* 1832 */             return info;
/*      */           }
/*      */           
/*      */ 
/*      */           public SearchResult[] getResults()
/*      */           {
/* 1838 */             return new SearchResult[0];
/*      */           }
/*      */           
/*      */ 
/*      */           public boolean isComplete()
/*      */           {
/* 1844 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */           public SearchException getError()
/*      */           {
/* 1850 */             return null;
/*      */           }
/*      */           
/* 1853 */         };
/* 1854 */         this.pending_results.add(result);
/*      */       }
/*      */       
/* 1857 */       if (this.listener != null)
/*      */       {
/* 1859 */         this.listener.receivedResults(new SearchProviderResults[] { result });
/*      */         
/* 1861 */         if (report_complete)
/*      */         {
/* 1863 */           this.listener.completed();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void failed(Engine engine, final SearchException error)
/*      */     {
/*      */       boolean report_complete;
/*      */       
/*      */       SearchProviderResults result;
/*      */       
/* 1876 */       synchronized (this.engine_map)
/*      */       {
/* 1878 */         final MetaSearchManagerImpl.engineInfo info = getInfo(engine);
/*      */         
/* 1880 */         info.setComplete();
/*      */         
/* 1882 */         report_complete = reportOverallComplete();
/*      */         
/* 1884 */         result = new SearchProviderResults()
/*      */         {
/*      */ 
/*      */           public SearchProvider getProvider()
/*      */           {
/*      */ 
/* 1890 */             return info;
/*      */           }
/*      */           
/*      */ 
/*      */           public SearchResult[] getResults()
/*      */           {
/* 1896 */             return new SearchResult[0];
/*      */           }
/*      */           
/*      */ 
/*      */           public boolean isComplete()
/*      */           {
/* 1902 */             return false;
/*      */           }
/*      */           
/*      */ 
/*      */           public SearchException getError()
/*      */           {
/* 1908 */             return error;
/*      */           }
/*      */           
/* 1911 */         };
/* 1912 */         this.pending_results.add(result);
/*      */       }
/*      */       
/* 1915 */       if (this.listener != null)
/*      */       {
/* 1917 */         this.listener.receivedResults(new SearchProviderResults[] { result });
/*      */         
/* 1919 */         if (report_complete)
/*      */         {
/* 1921 */           this.listener.completed();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void engineFailed(Engine engine, Throwable cause)
/*      */     {
/* 1931 */       failed(engine, new SearchException("Search failed", cause));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void engineRequiresLogin(Engine engine, Throwable cause)
/*      */     {
/* 1939 */       failed(engine, new SearchException("Authentication required", cause));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected SearchResult[] wrapResults(Result[] res)
/*      */     {
/* 1946 */       SearchResult[] x = new SearchResult[res.length];
/*      */       
/* 1948 */       for (int i = 0; i < x.length; i++)
/*      */       {
/* 1950 */         x[i] = new resultWrapper(res[i]);
/*      */       }
/*      */       
/* 1953 */       return x;
/*      */     }
/*      */     
/*      */ 
/*      */     public SearchProviderResults[] getResults()
/*      */     {
/* 1959 */       synchronized (this.engine_map)
/*      */       {
/* 1961 */         SearchProviderResults[] result = (SearchProviderResults[])this.pending_results.toArray(new SearchProviderResults[this.pending_results.size()]);
/*      */         
/* 1963 */         this.pending_results.clear();
/*      */         
/* 1965 */         return result;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isComplete()
/*      */     {
/* 1972 */       synchronized (this.engine_map)
/*      */       {
/* 1974 */         if (!this.is_complete)
/*      */         {
/* 1976 */           return false;
/*      */         }
/*      */         
/* 1979 */         if (this.pending_results.size() > 0)
/*      */         {
/* 1981 */           return false;
/*      */         }
/*      */         
/* 1984 */         return true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected static class resultWrapper
/*      */       implements SearchResult
/*      */     {
/*      */       private Result result;
/*      */       
/*      */ 
/*      */       protected resultWrapper(Result _result)
/*      */       {
/* 1998 */         this.result = _result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public Object getProperty(int property_name)
/*      */       {
/* 2005 */         switch (property_name)
/*      */         {
/*      */         case 22: 
/* 2008 */           return Long.valueOf(-1L);
/*      */         
/*      */ 
/*      */         case 1: 
/* 2012 */           return this.result.getName();
/*      */         
/*      */ 
/*      */         case 2: 
/* 2016 */           return this.result.getPublishedDate();
/*      */         
/*      */ 
/*      */         case 3: 
/* 2020 */           return Long.valueOf(this.result.getSize());
/*      */         
/*      */ 
/*      */         case 4: 
/* 2024 */           return new Long(this.result.getNbPeers());
/*      */         
/*      */ 
/*      */         case 5: 
/* 2028 */           return new Long(this.result.getNbSeeds());
/*      */         
/*      */ 
/*      */         case 6: 
/* 2032 */           return new Long(this.result.getNbSuperSeeds());
/*      */         
/*      */ 
/*      */         case 7: 
/* 2036 */           return this.result.getCategory();
/*      */         
/*      */ 
/*      */         case 8: 
/* 2040 */           return new Long(this.result.getComments());
/*      */         
/*      */ 
/*      */         case 9: 
/* 2044 */           return new Long(this.result.getVotes());
/*      */         
/*      */ 
/*      */         case 10: 
/* 2048 */           return this.result.getContentType();
/*      */         
/*      */ 
/*      */         case 11: 
/* 2052 */           return this.result.getCDPLink();
/*      */         
/*      */ 
/*      */         case 12: 
/* 2056 */           return this.result.getDownloadLink();
/*      */         
/*      */ 
/*      */         case 23: 
/* 2060 */           return this.result.getTorrentLink();
/*      */         
/*      */ 
/*      */         case 13: 
/* 2064 */           return this.result.getPlayLink();
/*      */         
/*      */ 
/*      */         case 14: 
/* 2068 */           return Boolean.valueOf(this.result.isPrivate());
/*      */         
/*      */ 
/*      */         case 15: 
/* 2072 */           return this.result.getDRMKey();
/*      */         
/*      */ 
/*      */         case 16: 
/* 2076 */           return this.result.getDownloadButtonLink();
/*      */         
/*      */ 
/*      */         case 17: 
/* 2080 */           float rank = this.result.getRank();
/*      */           
/* 2082 */           return new Long(rank == -1.0F ? -1L : (rank * 100.0F));
/*      */         
/*      */ 
/*      */         case 18: 
/* 2086 */           float accuracy = this.result.getAccuracy();
/*      */           
/* 2088 */           return new Long(accuracy == -1.0F ? -1L : (accuracy * 100.0F));
/*      */         
/*      */ 
/*      */         case 19: 
/* 2092 */           return new Long(this.result.getVotesDown());
/*      */         
/*      */ 
/*      */         case 20: 
/* 2096 */           return this.result.getUID();
/*      */         
/*      */ 
/*      */         case 21: 
/* 2100 */           String base32_hash = this.result.getHash();
/*      */           
/* 2102 */           if (base32_hash != null)
/*      */           {
/* 2104 */             return Base32.decode(base32_hash);
/*      */           }
/*      */           
/* 2107 */           return null;
/*      */         }
/*      */         
/*      */         
/* 2111 */         Debug.out("Unknown property type " + property_name);
/*      */         
/*      */ 
/*      */ 
/* 2115 */         return null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static class engineInfo
/*      */     implements SearchProvider
/*      */   {
/*      */     private Engine engine;
/*      */     
/*      */     private boolean complete;
/*      */     
/*      */ 
/*      */     protected engineInfo(Engine _engine)
/*      */     {
/* 2132 */       this.engine = _engine;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void setComplete()
/*      */     {
/* 2138 */       this.complete = true;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isComplete()
/*      */     {
/* 2144 */       return this.complete;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public SearchInstance search(Map<String, Object> search_parameters, SearchObserver observer)
/*      */       throws SearchException
/*      */     {
/* 2154 */       throw new SearchException("Not supported");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object getProperty(int property)
/*      */     {
/* 2161 */       if (property == 0)
/*      */       {
/* 2163 */         return Long.valueOf(this.engine.getId());
/*      */       }
/* 2165 */       if (property == 1)
/*      */       {
/* 2167 */         return this.engine.getName();
/*      */       }
/*      */       
/*      */ 
/* 2171 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setProperty(int property, Object value)
/*      */     {
/* 2180 */       Debug.out("Not supported");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 2189 */       VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*      */       
/* 2191 */       Map contents = new HashMap();
/*      */       
/* 2193 */       contents.put("type", new Long(1L));
/* 2194 */       contents.put("term", "donkey");
/*      */       
/* 2196 */       vf.addComponent(256, contents);
/*      */       
/*      */ 
/*      */ 
/* 2200 */       vf.write(new File("C:\\temp\\search.vuze"));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2204 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/MetaSearchManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */