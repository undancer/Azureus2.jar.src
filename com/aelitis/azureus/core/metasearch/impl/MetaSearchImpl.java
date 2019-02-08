/*      */ package com.aelitis.azureus.core.metasearch.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformMetaSearchMessenger;
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformMetaSearchMessenger.templateDetails;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearch;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchException;
/*      */ import com.aelitis.azureus.core.metasearch.MetaSearchListener;
/*      */ import com.aelitis.azureus.core.metasearch.Result;
/*      */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*      */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*      */ import com.aelitis.azureus.core.metasearch.impl.plugin.PluginEngine;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.rss.RSSEngine;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.plugins.utils.search.SearchProvider;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MetaSearchImpl
/*      */   implements MetaSearch
/*      */ {
/*      */   private static final String CONFIG_FILE = "metasearch.config";
/*      */   private MetaSearchManagerImpl manager;
/*   69 */   private CopyOnWriteList<EngineImpl> engines = new CopyOnWriteList();
/*   70 */   private Map<String, Long> plugin_map = new HashMap();
/*      */   
/*      */   private boolean config_dirty;
/*      */   
/*   74 */   private CopyOnWriteList<MetaSearchListener> listeners = new CopyOnWriteList();
/*      */   
/*      */   private TimerEventPeriodic update_check_timer;
/*      */   
/*      */   private static final int UPDATE_CHECK_PERIOD = 900000;
/*      */   
/*      */   private static final int MIN_UPDATE_CHECK_SECS = 600;
/*   81 */   private Object MS_UPDATE_CONSEC_FAIL_KEY = new Object();
/*      */   
/*   83 */   private AsyncDispatcher update_dispatcher = new AsyncDispatcher();
/*      */   
/*      */ 
/*      */ 
/*      */   protected MetaSearchImpl(MetaSearchManagerImpl _manager)
/*      */   {
/*   89 */     this.manager = _manager;
/*      */     
/*   91 */     loadConfig();
/*      */   }
/*      */   
/*      */ 
/*      */   public MetaSearchManagerImpl getManager()
/*      */   {
/*   97 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine importFromBEncodedMap(Map<String, Object> map)
/*      */     throws IOException
/*      */   {
/*  106 */     return EngineImpl.importFromBEncodedMap(this, map);
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
/*      */   public Engine importFromJSONString(int type, long id, long last_updated, float rank_bias, String name, String content)
/*      */     throws IOException
/*      */   {
/*  120 */     return EngineImpl.importFromJSONString(this, type, id, last_updated, rank_bias, name, content);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public EngineImpl importFromPlugin(String _pid, SearchProvider provider)
/*      */     throws IOException
/*      */   {
/*  130 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  136 */       Iterator<String> it = this.plugin_map.keySet().iterator();
/*      */       
/*  138 */       while (it.hasNext())
/*      */       {
/*  140 */         if (((String)it.next()).length() > 1024)
/*      */         {
/*  142 */           Debug.out("plugin_map corrupted, resetting");
/*      */           
/*  144 */           this.plugin_map.clear();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  150 */       String pid = Base32.encode(_pid.getBytes("UTF-8"));
/*      */       
/*  152 */       Long l_id = (Long)this.plugin_map.get(pid);
/*      */       
/*      */       long id;
/*      */       
/*  156 */       if (l_id == null)
/*      */       {
/*  158 */         long id = this.manager.getLocalTemplateID();
/*      */         
/*  160 */         this.plugin_map.put(pid, new Long(id));
/*      */         
/*  162 */         configDirty();
/*      */       }
/*      */       else
/*      */       {
/*  166 */         id = l_id.longValue();
/*      */       }
/*      */       
/*  169 */       EngineImpl engine = (EngineImpl)getEngine(id);
/*      */       
/*  171 */       if (engine == null)
/*      */       {
/*  173 */         engine = new PluginEngine(this, id, provider);
/*      */         
/*  175 */         engine.setSource(2);
/*      */         
/*  177 */         engine.setSelectionState(2);
/*      */         
/*  179 */         addEngine(engine);
/*      */ 
/*      */ 
/*      */       }
/*  183 */       else if ((engine instanceof PluginEngine))
/*      */       {
/*  185 */         ((PluginEngine)engine).setProvider(provider);
/*      */       }
/*      */       else
/*      */       {
/*  189 */         Debug.out("Inconsistent: plugin must be a PluginEngine!");
/*      */         
/*  191 */         this.plugin_map.remove(pid);
/*      */         
/*  193 */         removeEngine(engine);
/*      */         
/*  195 */         throw new IOException("Inconsistent");
/*      */       }
/*      */       
/*      */ 
/*  199 */       return engine;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SearchProvider resolveProvider(PluginEngine for_engine)
/*      */   {
/*  207 */     List<EngineImpl> l = this.engines.getList();
/*      */     
/*  209 */     for (EngineImpl e : l)
/*      */     {
/*  211 */       if ((e instanceof PluginEngine))
/*      */       {
/*  213 */         PluginEngine pe = (PluginEngine)e;
/*      */         
/*  215 */         SearchProvider provider = pe.getProvider();
/*      */         
/*  217 */         if (provider != null)
/*      */         {
/*  219 */           if (pe.getName().equals(for_engine.getName()))
/*      */           {
/*  221 */             return provider;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  227 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine createRSSEngine(String name, URL url)
/*      */     throws MetaSearchException
/*      */   {
/*  237 */     EngineImpl engine = new RSSEngine(this, this.manager.getLocalTemplateID(), SystemTime.getCurrentTime(), 1.0F, name, url.toExternalForm(), false, "transparent", null, new String[0]);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  250 */     engine.setSource(3);
/*      */     
/*  252 */     addEngine(engine, false);
/*      */     
/*  254 */     log("Created RSS engine '" + url + "'");
/*      */     
/*  256 */     return engine;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void enableUpdateChecks()
/*      */   {
/*  262 */     synchronized (this)
/*      */     {
/*  264 */       if (this.update_check_timer == null)
/*      */       {
/*  266 */         this.update_check_timer = SimpleTimer.addPeriodicEvent("MS:updater", 900000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*  275 */             MetaSearchImpl.this.checkUpdates();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkUpdates()
/*      */   {
/*  284 */     this.update_dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  290 */         MetaSearchImpl.this.checkUpdatesSupport();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkUpdatesSupport()
/*      */   {
/*  298 */     Iterator<EngineImpl> it = this.engines.iterator();
/*      */     
/*  300 */     while (it.hasNext())
/*      */     {
/*  302 */       EngineImpl engine = (EngineImpl)it.next();
/*      */       
/*  304 */       String update_url = engine.getUpdateURL();
/*      */       
/*  306 */       if (update_url != null)
/*      */       {
/*  308 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  310 */         long last_check = engine.getLastUpdateCheck();
/*      */         
/*  312 */         if (last_check > now)
/*      */         {
/*  314 */           last_check = now;
/*      */           
/*  316 */           engine.setLastUpdateCheck(now);
/*      */         }
/*      */         
/*  319 */         long check_secs = engine.getUpdateCheckSecs();
/*      */         
/*  321 */         if (check_secs < 600L)
/*      */         {
/*  323 */           log("Engine '" + engine.getName() + "': Update check period too small (" + check_secs + " secs) adjusting to " + 600 + ": " + engine.getName());
/*      */           
/*  325 */           check_secs = 600L;
/*      */         }
/*      */         
/*  328 */         long check_millis = check_secs * 1000L;
/*      */         
/*  330 */         long next_check = last_check + check_millis;
/*      */         
/*  332 */         Object consec_fails_o = engine.getUserData(this.MS_UPDATE_CONSEC_FAIL_KEY);
/*      */         
/*  334 */         int consec_fails = consec_fails_o == null ? 0 : ((Integer)consec_fails_o).intValue();
/*      */         
/*  336 */         if (consec_fails > 0)
/*      */         {
/*  338 */           next_check += (900000 << consec_fails);
/*      */         }
/*      */         
/*  341 */         if (next_check < now)
/*      */         {
/*  343 */           if (updateEngine(engine))
/*      */           {
/*  345 */             consec_fails = 0;
/*      */             
/*  347 */             engine.setLastUpdateCheck(now);
/*      */           }
/*      */           else
/*      */           {
/*  351 */             consec_fails++;
/*      */             
/*  353 */             if (consec_fails > 3)
/*      */             {
/*  355 */               consec_fails = 0;
/*      */               
/*      */ 
/*      */ 
/*  359 */               engine.setLastUpdateCheck(now);
/*      */             }
/*      */           }
/*      */           
/*  363 */           engine.setUserData(this.MS_UPDATE_CONSEC_FAIL_KEY, consec_fails == 0 ? null : new Integer(consec_fails));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean updateEngine(EngineImpl engine)
/*      */   {
/*  373 */     String update_url = engine.getUpdateURL();
/*      */     
/*  375 */     int pos = update_url.indexOf('?');
/*      */     
/*  377 */     if (pos == -1)
/*      */     {
/*  379 */       update_url = update_url + "?";
/*      */     }
/*      */     else
/*      */     {
/*  383 */       update_url = update_url + "&";
/*      */     }
/*      */     
/*  386 */     update_url = update_url + "az_template_uid=" + engine.getUID() + "&az_template_version=" + engine.getVersion() + "&az_version=" + "5.7.6.0" + "&az_locale=" + MessageText.getCurrentLocale().toString() + "&az_rand=" + RandomUtils.nextAbsoluteLong();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  392 */     log("Engine " + engine.getName() + ": auto-update check via " + update_url);
/*      */     try
/*      */     {
/*  395 */       ResourceDownloaderFactory rdf = StaticUtilities.getResourceDownloaderFactory();
/*      */       
/*  397 */       ResourceDownloader url_rd = rdf.create(new URL(update_url));
/*      */       
/*  399 */       ResourceDownloader rd = rdf.getMetaRefreshDownloader(url_rd);
/*      */       
/*  401 */       InputStream is = rd.download();
/*      */       try
/*      */       {
/*  404 */         Map<String, Object> map = BDecoder.decode(new BufferedInputStream(is));
/*      */         
/*  406 */         log("    update check reply: " + map);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  411 */         Map<String, Object> response = (Map)map.get("response");
/*      */         int check_secs;
/*  413 */         if (response != null)
/*      */         {
/*  415 */           Long update_secs = (Long)response.get("update_url_check_secs");
/*      */           
/*  417 */           if (update_secs == null)
/*      */           {
/*  419 */             engine.setLocalUpdateCheckSecs(0);
/*      */           }
/*      */           else
/*      */           {
/*  423 */             check_secs = update_secs.intValue();
/*      */             
/*  425 */             if (check_secs < 600)
/*      */             {
/*  427 */               log("    update check secs for to small, min is 600");
/*      */               
/*  429 */               check_secs = 600;
/*      */             }
/*      */             
/*  432 */             engine.setLocalUpdateCheckSecs(check_secs);
/*      */           }
/*      */           
/*  435 */           return 1;
/*      */         }
/*      */         
/*      */ 
/*  439 */         VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(map);
/*      */         
/*  441 */         if (vf == null)
/*      */         {
/*  443 */           log("    failed to decode vuze file");
/*      */           
/*  445 */           return 0;
/*      */         }
/*      */         
/*  448 */         Engine[] updated_engines = this.manager.loadFromVuzeFile(vf);
/*      */         String existing_uid;
/*  450 */         if (updated_engines.length > 0)
/*      */         {
/*  452 */           existing_uid = engine.getUID();
/*      */           
/*  454 */           boolean found = false;
/*      */           
/*  456 */           String engine_str = "";
/*      */           
/*  458 */           for (int i = 0; i < updated_engines.length; i++)
/*      */           {
/*  460 */             Engine updated_engine = updated_engines[i];
/*      */             
/*  462 */             engine_str = engine_str + (i == 0 ? "" : ",") + updated_engine.getName() + ": uid=" + updated_engine.getUID() + ",version=" + updated_engine.getVersion();
/*      */             
/*  464 */             if (updated_engine.getUID().equals(existing_uid))
/*      */             {
/*  466 */               found = true;
/*      */             }
/*      */           }
/*      */           
/*  470 */           if (!found)
/*      */           {
/*  472 */             log("    existing engine not found in updated set, deleting");
/*      */             
/*  474 */             engine.delete();
/*      */           }
/*      */           
/*      */ 
/*  478 */           log("    update complete: new engines=" + engine_str);
/*      */         }
/*      */         else
/*      */         {
/*  482 */           log("    no engines found in vuze file");
/*      */         }
/*      */         
/*  485 */         return 1;
/*      */       }
/*      */       finally
/*      */       {
/*  489 */         is.close();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  495 */       return false;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  493 */       log("    update check failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addEngine(Engine engine)
/*      */   {
/*  503 */     addEngine((EngineImpl)engine, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine addEngine(long id)
/*      */     throws MetaSearchException
/*      */   {
/*      */     try
/*      */     {
/*  514 */       PlatformMetaSearchMessenger.templateDetails details = PlatformMetaSearchMessenger.getTemplate(this.manager.getExtensionKey(), id);
/*      */       
/*  516 */       log("Downloading definition of template " + id);
/*  517 */       log(details.getValue());
/*      */       
/*  519 */       if (details.isVisible())
/*      */       {
/*  521 */         Engine engine = importFromJSONString(details.getType() == 1 ? 2 : 1, details.getId(), details.getModifiedDate(), details.getRankBias(), details.getName(), details.getValue());
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  530 */         engine.setSource(1);
/*  531 */         engine.setSelectionState(0);
/*      */         
/*  533 */         addEngine(engine);
/*      */         
/*  535 */         return engine;
/*      */       }
/*      */       
/*      */ 
/*  539 */       throw new MetaSearchException("Search template is not visible");
/*      */     }
/*      */     catch (MetaSearchException e)
/*      */     {
/*  543 */       throw e;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  547 */       throw new MetaSearchException("Template load failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addEngine(EngineImpl new_engine, boolean loading)
/*      */   {
/*  556 */     boolean add_op = true;
/*      */     
/*  558 */     synchronized (this)
/*      */     {
/*  560 */       Iterator<EngineImpl> it = this.engines.iterator();
/*      */       
/*  562 */       while (it.hasNext())
/*      */       {
/*  564 */         Engine existing_engine = (Engine)it.next();
/*      */         
/*  566 */         if (existing_engine.getId() == new_engine.getId())
/*      */         {
/*  568 */           log("Updating engine with same ID " + existing_engine.getId() + ": " + existing_engine.getName() + "/" + existing_engine.getUID());
/*      */           
/*  570 */           it.remove();
/*      */           
/*  572 */           new_engine.setUID(existing_engine.getUID());
/*      */           
/*  574 */           if (existing_engine.sameLogicAs(new_engine))
/*      */           {
/*  576 */             new_engine.setVersion(existing_engine.getVersion());
/*      */           }
/*      */           else
/*      */           {
/*  580 */             new_engine.setVersion(existing_engine.getVersion() + 1);
/*      */             
/*  582 */             log("    new version=" + new_engine.getVersion());
/*      */           }
/*      */           
/*  585 */           add_op = false;
/*      */         }
/*  587 */         else if (existing_engine.getUID().equals(new_engine.getUID()))
/*      */         {
/*  589 */           log("Removing engine with same UID " + existing_engine.getUID() + "(" + existing_engine.getName() + ")");
/*      */           
/*  591 */           it.remove();
/*      */         }
/*      */       }
/*      */       
/*  595 */       this.engines.add(new_engine);
/*      */     }
/*      */     
/*  598 */     if (new_engine.getUpdateURL() != null)
/*      */     {
/*  600 */       enableUpdateChecks();
/*      */     }
/*      */     
/*  603 */     if (!loading)
/*      */     {
/*  605 */       log("Engine '" + new_engine.getName() + "' added");
/*      */       
/*  607 */       saveConfig();
/*      */       
/*  609 */       Iterator<MetaSearchListener> it = this.listeners.iterator();
/*      */       
/*  611 */       while (it.hasNext())
/*      */       {
/*  613 */         MetaSearchListener listener = (MetaSearchListener)it.next();
/*      */         try
/*      */         {
/*  616 */           if (add_op)
/*      */           {
/*  618 */             listener.engineAdded(new_engine);
/*      */           }
/*      */           else
/*      */           {
/*  622 */             listener.engineUpdated(new_engine);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  626 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeEngine(Engine engine)
/*      */   {
/*  636 */     if (this.engines.remove((EngineImpl)engine))
/*      */     {
/*  638 */       log("Engine '" + engine.getName() + "' removed");
/*      */       
/*  640 */       saveConfig();
/*      */       
/*  642 */       Iterator<MetaSearchListener> it = this.listeners.iterator();
/*      */       
/*  644 */       while (it.hasNext())
/*      */       {
/*      */         try
/*      */         {
/*  648 */           ((MetaSearchListener)it.next()).engineRemoved(engine);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  652 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void stateChanged(Engine engine)
/*      */   {
/*  662 */     Iterator<MetaSearchListener> it = this.listeners.iterator();
/*      */     
/*  664 */     while (it.hasNext())
/*      */     {
/*  666 */       MetaSearchListener listener = (MetaSearchListener)it.next();
/*      */       try
/*      */       {
/*  669 */         listener.engineStateChanged(engine);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  673 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getFUD()
/*      */   {
/*  681 */     List<EngineImpl> l = this.engines.getList();
/*      */     
/*  683 */     List<Long> ids = new ArrayList();
/*      */     
/*  685 */     for (EngineImpl engine : l)
/*      */     {
/*  687 */       if (engine.getSource() == 1)
/*      */       {
/*  689 */         ids.add(Long.valueOf(engine.getId()));
/*      */       }
/*      */     }
/*      */     
/*  693 */     Collections.sort(ids);
/*      */     
/*  695 */     String fud = "";
/*      */     
/*  697 */     for (Long id : ids)
/*      */     {
/*  699 */       fud = fud + (fud.length() == 0 ? "" : ",") + id;
/*      */     }
/*      */     
/*  702 */     return fud;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addPotentialAssociation(EngineImpl engine, String key)
/*      */   {
/*  710 */     this.manager.addPotentialAssociation(engine, key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine[] getEngines(boolean active_only, boolean ensure_up_to_date)
/*      */   {
/*  718 */     if (ensure_up_to_date)
/*      */     {
/*  720 */       this.manager.ensureEnginesUpToDate();
/*      */     }
/*      */     
/*  723 */     List<EngineImpl> l = this.engines.getList();
/*      */     
/*      */     List<EngineImpl> result;
/*      */     
/*  727 */     if (active_only)
/*      */     {
/*  729 */       List<EngineImpl> result = new ArrayList();
/*      */       
/*  731 */       for (int i = 0; i < l.size(); i++)
/*      */       {
/*  733 */         EngineImpl e = (EngineImpl)l.get(i);
/*      */         
/*  735 */         if (e.isActive())
/*      */         {
/*  737 */           result.add(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  742 */       result = l;
/*      */     }
/*      */     
/*  745 */     return (Engine[])result.toArray(new Engine[result.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Engine getEngine(long id)
/*      */   {
/*  752 */     List<EngineImpl> l = this.engines.getList();
/*      */     
/*  754 */     for (int i = 0; i < l.size(); i++)
/*      */     {
/*  756 */       Engine e = (Engine)l.get(i);
/*      */       
/*  758 */       if (e.getId() == id)
/*      */       {
/*  760 */         return e;
/*      */       }
/*      */     }
/*      */     
/*  764 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Engine getEngineByUID(String uid)
/*      */   {
/*  771 */     List<EngineImpl> l = this.engines.getList();
/*      */     
/*  773 */     for (int i = 0; i < l.size(); i++)
/*      */     {
/*  775 */       Engine e = (Engine)l.get(i);
/*      */       
/*  777 */       if (e.getUID().equals(uid))
/*      */       {
/*  779 */         return e;
/*      */       }
/*      */     }
/*      */     
/*  783 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getEngineCount()
/*      */   {
/*  789 */     return this.engines.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine[] search(ResultListener original_listener, SearchParameter[] searchParameters, String headers, int max_results_per_engine)
/*      */   {
/*  799 */     return search(original_listener, searchParameters, headers, new HashMap(), max_results_per_engine);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine[] search(ResultListener original_listener, SearchParameter[] searchParameters, String headers, Map<String, String> context, int max_results_per_engine)
/*      */   {
/*  810 */     return search(null, original_listener, searchParameters, headers, context, max_results_per_engine);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Engine[] search(Engine[] engines, ResultListener listener, SearchParameter[] search_parameters, String headers, int max_results_per_engine)
/*      */   {
/*  821 */     return search(engines, listener, search_parameters, headers, new HashMap(), max_results_per_engine);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void enginePreferred(Engine engine)
/*      */   {
/*  828 */     Engine[] engines = getEngines(true, false);
/*      */     
/*  830 */     int num_other_preferred = 0;
/*      */     
/*  832 */     for (Engine e : engines)
/*      */     {
/*  834 */       if (e.getId() == engine.getId())
/*      */       {
/*  836 */         e.setPreferredDelta(1.0F);
/*      */ 
/*      */ 
/*      */       }
/*  840 */       else if (e.getPreferredWeighting() > 0.0F)
/*      */       {
/*  842 */         num_other_preferred++;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  847 */     if (num_other_preferred > 0)
/*      */     {
/*  849 */       float negative_weighting = -1.0F / num_other_preferred;
/*      */       
/*  851 */       for (Engine e : engines)
/*      */       {
/*  853 */         if ((e.getId() != engine.getId()) && (e.getPreferredWeighting() > 0.0F))
/*      */         {
/*  855 */           e.setPreferredDelta(negative_weighting);
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
/*      */   public Engine[] search(Engine[] engines, final ResultListener original_listener, SearchParameter[] searchParameters, String headers, Map<String, String> context, int max_results_per_engine)
/*      */   {
/*  870 */     String batch_millis_str = (String)context.get("batch_millis");
/*      */     
/*  872 */     final long batch_millis = batch_millis_str == null ? 0L : Long.parseLong(batch_millis_str);
/*      */     
/*  874 */     String rem_dups_str = (String)context.get("remove_dup_hash");
/*      */     
/*  876 */     final boolean rem_dups = rem_dups_str == null ? false : rem_dups_str.equalsIgnoreCase("true");
/*      */     
/*  878 */     ResultListener listener = new ResultListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  883 */       private AsyncDispatcher dispatcher = new AsyncDispatcher(5000);
/*      */       
/*  885 */       private final Map<Engine, List<Result[]>> pending_results = new HashMap();
/*      */       
/*  887 */       private final Map<Engine, Set<String>> result_hashes = new HashMap();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void contentReceived(final Engine engine, final String content)
/*      */       {
/*  894 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  900 */             MetaSearchImpl.3.this.val$original_listener.contentReceived(engine, content);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void matchFound(final Engine engine, final String[] fields)
/*      */       {
/*  910 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  916 */             MetaSearchImpl.3.this.val$original_listener.matchFound(engine, fields);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void resultsReceived(final Engine engine, final Result[] results)
/*      */       {
/*  926 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  932 */             Result[] results_to_return = null;
/*      */             
/*  934 */             if (MetaSearchImpl.3.this.val$batch_millis > 0L)
/*      */             {
/*  936 */               List<Result[]> list = (List)MetaSearchImpl.3.this.pending_results.get(engine);
/*      */               
/*  938 */               if (list == null)
/*      */               {
/*  940 */                 results_to_return = results;
/*      */                 
/*  942 */                 MetaSearchImpl.3.this.pending_results.put(engine, new ArrayList());
/*      */                 
/*  944 */                 new DelayedEvent("SearchBatcher", MetaSearchImpl.3.this.val$batch_millis, new AERunnable()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void runSupport()
/*      */                   {
/*      */ 
/*      */ 
/*  952 */                     MetaSearchImpl.3.this.dispatcher.dispatch(new AERunnable()
/*      */                     {
/*      */ 
/*      */                       public void runSupport()
/*      */                       {
/*      */ 
/*  958 */                         MetaSearchImpl.3.this.batchResultsComplete(MetaSearchImpl.3.3.this.val$engine);
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 });
/*      */               }
/*      */               else {
/*  965 */                 list.add(results);
/*      */               }
/*      */             }
/*      */             else {
/*  969 */               results_to_return = results;
/*      */             }
/*      */             
/*  972 */             if (results_to_return != null)
/*      */             {
/*  974 */               results_to_return = MetaSearchImpl.3.this.truncateResults(engine, results_to_return, MetaSearchImpl.3.this.val$max_results_per_engine);
/*      */               
/*  976 */               MetaSearchImpl.3.this.val$original_listener.resultsReceived(engine, results_to_return);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void resultsComplete(final Engine engine)
/*      */       {
/*  986 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  992 */             if (MetaSearchImpl.3.this.val$batch_millis > 0L)
/*      */             {
/*  994 */               MetaSearchImpl.3.this.batchResultsComplete(engine);
/*      */             }
/*      */             
/*  997 */             MetaSearchImpl.3.this.val$original_listener.resultsComplete(engine);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       protected void batchResultsComplete(Engine engine)
/*      */       {
/* 1006 */         List<Result[]> list = (List)this.pending_results.remove(engine);
/*      */         
/* 1008 */         if (list != null)
/*      */         {
/* 1010 */           List<Result> x = new ArrayList();
/*      */           
/* 1012 */           for (Result[] y : list)
/*      */           {
/* 1014 */             x.addAll(Arrays.asList(y));
/*      */           }
/*      */           
/* 1017 */           Result[] results = (Result[])x.toArray(new Result[x.size()]);
/*      */           
/* 1019 */           results = truncateResults(engine, results, rem_dups);
/*      */           
/* 1021 */           original_listener.resultsReceived(engine, results);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       protected Result[] truncateResults(Engine engine, Result[] a_results, int max)
/*      */       {
/* 1031 */         Set<String> hash_set = (Set)this.result_hashes.get(engine);
/*      */         
/* 1033 */         if (hash_set == null)
/*      */         {
/* 1035 */           hash_set = new HashSet();
/*      */           
/* 1037 */           this.result_hashes.put(engine, hash_set);
/*      */         }
/*      */         
/* 1040 */         List<Result> results = new ArrayList(a_results.length);
/*      */         
/* 1042 */         for (Result r : a_results)
/*      */         {
/* 1044 */           String name = r.getName();
/*      */           
/* 1046 */           if ((name != null) && (name.trim().length() != 0))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1051 */             if (this.val$rem_dups)
/*      */             {
/* 1053 */               String hash = r.getHash();
/*      */               
/* 1055 */               if ((hash == null) || (hash.length() == 0))
/*      */               {
/*      */ 
/* 1058 */                 results.add(r);
/*      */ 
/*      */ 
/*      */               }
/* 1062 */               else if (!hash_set.contains(hash))
/*      */               {
/* 1064 */                 results.add(r);
/*      */                 
/* 1066 */                 hash_set.add(hash);
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 1071 */               results.add(r);
/*      */             }
/*      */           }
/*      */         }
/* 1075 */         if (max < results.size())
/*      */         {
/* 1077 */           MetaSearchImpl.this.log("Truncating search results for " + engine.getName() + " from " + results.size() + " to " + max);
/*      */           
/* 1079 */           Collections.sort(results, new Comparator()
/*      */           {
/*      */ 
/*      */ 
/* 1083 */             Map<Result, Float> ranks = new HashMap();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public int compare(Result r1, Result r2)
/*      */             {
/* 1090 */               Float rank1 = (Float)this.ranks.get(r1);
/*      */               
/* 1092 */               if (rank1 == null) {
/* 1093 */                 rank1 = new Float(r1.getRank());
/* 1094 */                 this.ranks.put(r1, rank1);
/*      */               }
/*      */               
/* 1097 */               Float rank2 = (Float)this.ranks.get(r2);
/*      */               
/* 1099 */               if (rank2 == null) {
/* 1100 */                 rank2 = new Float(r2.getRank());
/* 1101 */                 this.ranks.put(r2, rank2);
/*      */               }
/*      */               
/* 1104 */               return rank2.compareTo(rank1);
/*      */             }
/*      */             
/* 1107 */           });
/* 1108 */           Result[] x = new Result[max];
/*      */           
/* 1110 */           int pos = 0;
/*      */           
/* 1112 */           while (pos < max)
/*      */           {
/* 1114 */             x[pos] = ((Result)results.get(pos));
/*      */             
/* 1116 */             pos++;
/*      */           }
/*      */           
/* 1119 */           return x;
/*      */         }
/*      */         
/*      */ 
/* 1123 */         return (Result[])results.toArray(new Result[results.size()]);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineFailed(final Engine engine, final Throwable e)
/*      */       {
/* 1132 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 1138 */             MetaSearchImpl.3.this.val$original_listener.engineFailed(engine, e);
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineRequiresLogin(final Engine engine, final Throwable e)
/*      */       {
/* 1148 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 1154 */             MetaSearchImpl.3.this.val$original_listener.engineRequiresLogin(engine, e);
/*      */           }
/*      */           
/*      */         });
/*      */       }
/* 1159 */     };
/* 1160 */     SearchExecuter se = new SearchExecuter(context, listener);
/*      */     
/* 1162 */     if (engines == null)
/*      */     {
/* 1164 */       engines = getEngines(true, true);
/*      */     }
/*      */     
/* 1167 */     String engines_str = "";
/*      */     
/* 1169 */     for (int i = 0; i < engines.length; i++)
/*      */     {
/* 1171 */       engines_str = engines_str + (i == 0 ? "" : ",") + engines[i].getId();
/*      */     }
/*      */     
/* 1174 */     log("Search: engines=" + engines_str);
/*      */     
/* 1176 */     for (int i = 0; i < engines.length; i++)
/*      */     {
/* 1178 */       se.search(engines[i], searchParameters, headers, max_results_per_engine);
/*      */     }
/*      */     
/* 1181 */     return engines;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void exportEngines(File target)
/*      */     throws MetaSearchException
/*      */   {
/* 1190 */     Engine[] engines = getEngines(true, false);
/*      */     
/* 1192 */     VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*      */     
/* 1194 */     for (Engine engine : engines) {
/*      */       try
/*      */       {
/* 1197 */         vf.addComponent(1, engine.exportToBencodedMap());
/*      */ 
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */ 
/* 1203 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1209 */       vf.write(target);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1213 */       throw new MetaSearchException("Failed to write file", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(MetaSearchListener listener)
/*      */   {
/* 1221 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(MetaSearchListener listener)
/*      */   {
/* 1228 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void loadConfig()
/*      */   {
/* 1234 */     log("Loading configuration");
/*      */     
/* 1236 */     synchronized (this)
/*      */     {
/* 1238 */       Map<String, Object> map = FileUtil.readResilientConfigFile("metasearch.config");
/*      */       
/* 1240 */       List<Map<String, Object>> l_engines = (List)map.get("engines");
/*      */       
/* 1242 */       if (l_engines != null)
/*      */       {
/* 1244 */         for (int i = 0; i < l_engines.size(); i++)
/*      */         {
/* 1246 */           Map<String, Object> m = (Map)l_engines.get(i);
/*      */           try
/*      */           {
/* 1249 */             Engine e = importFromBEncodedMap(m);
/*      */             
/* 1251 */             addEngine((EngineImpl)e, true);
/*      */             
/* 1253 */             log("    loaded " + e.getString());
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1257 */             log("Failed to import engine from " + m, e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1262 */       Map<String, Long> p_map = (Map)map.get("plugin_map");
/*      */       
/* 1264 */       if (p_map != null)
/*      */       {
/* 1266 */         this.plugin_map = p_map;
/*      */       }
/*      */       
/* 1269 */       if (this.update_check_timer != null)
/*      */       {
/* 1271 */         checkUpdates();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void configDirty()
/*      */   {
/* 1279 */     synchronized (this)
/*      */     {
/* 1281 */       if (this.config_dirty)
/*      */       {
/* 1283 */         return;
/*      */       }
/*      */       
/* 1286 */       this.config_dirty = true;
/*      */       
/* 1288 */       new DelayedEvent("MetaSearch:save", 5000L, new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/* 1295 */           synchronized (MetaSearchImpl.this)
/*      */           {
/* 1297 */             if (!MetaSearchImpl.this.config_dirty)
/*      */             {
/* 1299 */               return;
/*      */             }
/*      */             
/* 1302 */             MetaSearchImpl.this.saveConfig();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void saveConfig()
/*      */   {
/* 1312 */     log("Saving configuration");
/*      */     
/* 1314 */     synchronized (this)
/*      */     {
/* 1316 */       this.config_dirty = false;
/*      */       
/* 1318 */       Map<String, Object> map = new HashMap();
/*      */       
/* 1320 */       List<Map<String, Object>> l_engines = new ArrayList();
/*      */       
/* 1322 */       map.put("engines", l_engines);
/*      */       
/* 1324 */       Iterator<EngineImpl> it = this.engines.iterator();
/*      */       
/* 1326 */       while (it.hasNext())
/*      */       {
/* 1328 */         Engine e = (Engine)it.next();
/*      */         
/*      */         try
/*      */         {
/* 1332 */           l_engines.add(e.exportToBencodedMap());
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/* 1336 */           log("Failed to export engine " + e.getName(), f);
/*      */         }
/*      */       }
/*      */       
/* 1340 */       if (this.plugin_map != null)
/*      */       {
/* 1342 */         map.put("plugin_map", this.plugin_map);
/*      */       }
/*      */       
/* 1345 */       FileUtil.writeResilientConfigFile("metasearch.config", map);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1353 */     this.manager.log("search :" + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1361 */     this.manager.log("search :" + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void generate(IndentWriter writer)
/*      */   {
/* 1368 */     Iterator<EngineImpl> it = this.engines.iterator();
/*      */     
/* 1370 */     while (it.hasNext())
/*      */     {
/* 1372 */       EngineImpl e = (EngineImpl)it.next();
/*      */       
/* 1374 */       writer.println(e.getString(true));
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/MetaSearchImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */