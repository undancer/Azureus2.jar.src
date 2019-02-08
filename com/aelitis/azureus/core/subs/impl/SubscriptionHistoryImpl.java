/*      */ package com.aelitis.azureus.core.subs.impl;
/*      */ 
/*      */ import com.aelitis.azureus.activities.LocalActivityManager;
/*      */ import com.aelitis.azureus.activities.LocalActivityManager.LocalActivityCallback;
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionResult;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionScheduler;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SubscriptionHistoryImpl
/*      */   implements SubscriptionHistory
/*      */ {
/*   53 */   private static AsyncDispatcher dispatcher = new AsyncDispatcher("subspost");
/*      */   
/*      */   private final SubscriptionManagerImpl manager;
/*      */   
/*      */   private final SubscriptionImpl subs;
/*      */   
/*      */   private boolean enabled;
/*      */   private boolean auto_dl;
/*      */   private boolean post_notifications;
/*      */   private long last_scan;
/*      */   private long last_new_result;
/*      */   private int num_unread;
/*      */   private int num_read;
/*   66 */   private long max_results = -1L;
/*   67 */   private String[] networks = null;
/*      */   
/*      */   private String last_error;
/*      */   
/*      */   private boolean auth_failed;
/*      */   
/*      */   private int consec_fails;
/*      */   private boolean auto_dl_supported;
/*   75 */   private boolean dl_with_ref = true;
/*      */   
/*      */ 
/*      */   private int interval_override;
/*      */   
/*      */ 
/*      */ 
/*      */   protected SubscriptionHistoryImpl(SubscriptionManagerImpl _manager, SubscriptionImpl _subs)
/*      */   {
/*   84 */     this.manager = _manager;
/*   85 */     this.subs = _subs;
/*      */     
/*   87 */     loadConfig();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SubscriptionResultImpl[] reconcileResults(Engine engine, SubscriptionResultImpl[] latest_results)
/*      */   {
/*   95 */     this.auto_dl_supported = (engine.getAutoDownloadSupported() == 1);
/*      */     
/*   97 */     int new_unread = 0;
/*   98 */     int new_read = 0;
/*      */     
/*  100 */     if (this.last_scan == 0L)
/*      */     {
/*      */ 
/*      */ 
/*  104 */       GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*      */       
/*  106 */       for (int i = 0; i < latest_results.length; i++)
/*      */       {
/*  108 */         SubscriptionResultImpl result = latest_results[i];
/*      */         
/*  110 */         result.setReadInternal(true);
/*      */         
/*      */ 
/*      */         try
/*      */         {
/*  115 */           String hash_str = result.getAssetHash();
/*      */           
/*  117 */           if (hash_str != null)
/*      */           {
/*  119 */             byte[] hash = Base32.decode(hash_str);
/*      */             
/*  121 */             DownloadManager dm = gm.getDownloadManager(new HashWrapper(hash));
/*      */             
/*  123 */             if (dm != null)
/*      */             {
/*  125 */               log("Adding existing association on first read for '" + dm.getDisplayName());
/*      */               
/*  127 */               this.subs.addAssociation(hash);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  132 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  137 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*      */ 
/*  141 */     int max_results = getMaxNonDeletedResults();
/*      */     
/*  143 */     if (max_results < 0)
/*      */     {
/*  145 */       max_results = this.manager.getMaxNonDeletedResults();
/*      */     }
/*      */     
/*  148 */     SubscriptionResultImpl first_new_result = null;
/*      */     SubscriptionResultImpl[] result;
/*  150 */     synchronized (this)
/*      */     {
/*  152 */       boolean got_new_or_changed_result = false;
/*      */       
/*  154 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  156 */       SubscriptionResultImpl[] existing_results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  158 */       ByteArrayHashMap result_key_map = new ByteArrayHashMap();
/*  159 */       ByteArrayHashMap result_key2_map = new ByteArrayHashMap();
/*      */       
/*  161 */       List new_results = new ArrayList();
/*      */       
/*  163 */       for (int i = 0; i < existing_results.length; i++)
/*      */       {
/*  165 */         SubscriptionResultImpl r = existing_results[i];
/*      */         
/*  167 */         result_key_map.put(r.getKey1(), r);
/*      */         
/*  169 */         byte[] key2 = r.getKey2();
/*      */         
/*  171 */         if (key2 != null)
/*      */         {
/*  173 */           result_key2_map.put(key2, r);
/*      */         }
/*      */         
/*  176 */         new_results.add(r);
/*      */         
/*  178 */         if (!r.isDeleted())
/*      */         {
/*  180 */           if (r.getRead())
/*      */           {
/*  182 */             new_read++;
/*      */           }
/*      */           else
/*      */           {
/*  186 */             new_unread++;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  191 */       for (int i = 0; i < latest_results.length; i++)
/*      */       {
/*  193 */         SubscriptionResultImpl r = latest_results[i];
/*      */         
/*      */ 
/*      */ 
/*  197 */         SubscriptionResultImpl existing = (SubscriptionResultImpl)result_key_map.get(r.getKey1());
/*      */         
/*  199 */         if (existing == null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  204 */           byte[] key2 = r.getKey2();
/*      */           
/*  206 */           if (key2 != null)
/*      */           {
/*  208 */             existing = (SubscriptionResultImpl)result_key2_map.get(key2);
/*      */           }
/*      */         }
/*      */         
/*  212 */         if (existing == null)
/*      */         {
/*  214 */           this.last_new_result = now;
/*      */           
/*  216 */           new_results.add(r);
/*      */           
/*  218 */           result_key_map.put(r.getKey1(), r);
/*      */           
/*  220 */           byte[] key2 = r.getKey2();
/*      */           
/*  222 */           if (key2 != null)
/*      */           {
/*  224 */             result_key2_map.put(key2, r);
/*      */           }
/*      */           
/*  227 */           got_new_or_changed_result = true;
/*      */           
/*  229 */           if (r.getRead())
/*      */           {
/*  231 */             new_read++;
/*      */           }
/*      */           else
/*      */           {
/*  235 */             new_unread++;
/*      */             
/*  237 */             if (first_new_result == null)
/*      */             {
/*  239 */               first_new_result = r;
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  244 */         else if (existing.updateFrom(r))
/*      */         {
/*  246 */           got_new_or_changed_result = true;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  253 */       if ((max_results > 0) && (new_unread + new_read > max_results))
/*      */       {
/*  255 */         for (int i = 0; i < new_results.size(); i++)
/*      */         {
/*  257 */           SubscriptionResultImpl r = (SubscriptionResultImpl)new_results.get(i);
/*      */           
/*  259 */           if (!r.isDeleted())
/*      */           {
/*  261 */             if (r.getRead())
/*      */             {
/*  263 */               new_read--;
/*      */             }
/*      */             else
/*      */             {
/*  267 */               new_unread--;
/*      */             }
/*      */             
/*  270 */             r.deleteInternal();
/*      */             
/*  272 */             got_new_or_changed_result = true;
/*      */             
/*  274 */             if (new_unread + new_read <= max_results) {
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  282 */       if (got_new_or_changed_result)
/*      */       {
/*  284 */         SubscriptionResultImpl[] result = (SubscriptionResultImpl[])new_results.toArray(new SubscriptionResultImpl[new_results.size()]);
/*      */         
/*  286 */         this.manager.saveResults(this.subs, result);
/*      */       }
/*      */       else
/*      */       {
/*  290 */         result = existing_results;
/*      */       }
/*      */       
/*  293 */       this.last_scan = now;
/*  294 */       this.num_unread = new_unread;
/*  295 */       this.num_read = new_read;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  300 */     saveConfig(2);
/*      */     
/*  302 */     if ((this.post_notifications) && (first_new_result != null))
/*      */     {
/*  304 */       dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*  311 */           Map<String, String> cb_data = new HashMap();
/*      */           
/*  313 */           cb_data.put("subname", SubscriptionHistoryImpl.this.subs.getName());
/*  314 */           cb_data.put("subid", SubscriptionHistoryImpl.this.subs.getID());
/*      */           
/*  316 */           cb_data.put("allowReAdd", "true");
/*      */           
/*  318 */           String date_str = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(SystemTime.getCurrentTime()));
/*      */           
/*  320 */           LocalActivityManager.addLocalActivity("NewResults:" + SubscriptionHistoryImpl.this.subs.getID(), "rss", MessageText.getString("subs.activity.new.results", new String[] { SubscriptionHistoryImpl.this.subs.getName(), String.valueOf(SubscriptionHistoryImpl.this.num_unread) }) + ": " + date_str, new String[] { MessageText.getString("label.view") }, SubscriptionHistoryImpl.ActivityCallback.class, cb_data);
/*      */         }
/*      */       });
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
/*  333 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static class ActivityCallback
/*      */     implements LocalActivityManager.LocalActivityCallback
/*      */   {
/*      */     public void actionSelected(String action, Map<String, String> data)
/*      */     {
/*  344 */       SubscriptionManager subs_man = SubscriptionManagerFactory.getSingleton();
/*      */       
/*  346 */       String sub_id = (String)data.get("subid");
/*      */       
/*  348 */       Subscription sub = subs_man.getSubscriptionByID(sub_id);
/*      */       
/*  350 */       if (sub != null)
/*      */       {
/*  352 */         sub.requestAttention();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  360 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean _enabled)
/*      */   {
/*  367 */     if (_enabled != this.enabled)
/*      */     {
/*  369 */       this.enabled = _enabled;
/*      */       
/*  371 */       saveConfig(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAutoDownload()
/*      */   {
/*  378 */     return this.auto_dl;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAutoDownload(boolean _auto_dl)
/*      */   {
/*  385 */     if (_auto_dl != this.auto_dl)
/*      */     {
/*  387 */       this.auto_dl = _auto_dl;
/*      */       
/*  389 */       saveConfig(1);
/*      */       
/*  391 */       if (this.auto_dl)
/*      */       {
/*  393 */         downloadNow();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxNonDeletedResults()
/*      */   {
/*  401 */     return this.max_results < 0L ? -1 : (int)this.max_results;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxNonDeletedResults(int _max_results)
/*      */   {
/*  408 */     if (_max_results != this.max_results)
/*      */     {
/*  410 */       this.max_results = _max_results;
/*      */       
/*  412 */       saveConfig(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String[] getDownloadNetworks()
/*      */   {
/*  419 */     return this.networks;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDownloadNetworks(String[] nets)
/*      */   {
/*  426 */     this.networks = nets;
/*      */     
/*  428 */     saveConfig(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getNotificationPostEnabled()
/*      */   {
/*  434 */     return this.post_notifications;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNotificationPostEnabled(boolean enabled)
/*      */   {
/*  441 */     if (enabled != this.post_notifications)
/*      */     {
/*  443 */       this.post_notifications = enabled;
/*      */       
/*  445 */       saveConfig(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDetails(boolean _enabled, boolean _auto_dl)
/*      */   {
/*  454 */     if ((this.enabled != _enabled) || (this.auto_dl != _auto_dl))
/*      */     {
/*  456 */       this.enabled = _enabled;
/*  457 */       this.auto_dl = _auto_dl;
/*      */       
/*  459 */       saveConfig(1);
/*      */       
/*  461 */       if ((this.enabled) && (this.auto_dl))
/*      */       {
/*  463 */         downloadNow();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void downloadNow()
/*      */   {
/*      */     try
/*      */     {
/*  472 */       this.subs.getManager().getScheduler().downloadAsync(this.subs, false);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  476 */       log("Failed to initiate download", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastScanTime()
/*      */   {
/*  483 */     return this.last_scan;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastNewResultTime()
/*      */   {
/*  489 */     return this.last_new_result;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getNextScanTime()
/*      */   {
/*  495 */     if (this.interval_override > 0)
/*      */     {
/*  497 */       if (this.last_scan == 0L)
/*      */       {
/*      */ 
/*      */ 
/*  501 */         return SystemTime.getCurrentTime();
/*      */       }
/*      */       
/*      */ 
/*  505 */       return this.last_scan + this.interval_override * 60 * 1000;
/*      */     }
/*      */     
/*      */ 
/*  509 */     Map schedule = this.subs.getScheduleConfig();
/*      */     
/*  511 */     if (schedule.size() == 0)
/*      */     {
/*  513 */       log("Schedule is empty!");
/*      */       
/*  515 */       return Long.MAX_VALUE;
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  521 */       long interval_min = ((Long)schedule.get("interval")).longValue();
/*      */       
/*  523 */       if ((interval_min == 2147483647L) || (interval_min == Long.MAX_VALUE))
/*      */       {
/*  525 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/*  528 */       if (this.last_scan == 0L)
/*      */       {
/*      */ 
/*      */ 
/*  532 */         return SystemTime.getCurrentTime();
/*      */       }
/*      */       
/*      */ 
/*  536 */       return this.last_scan + interval_min * 60L * 1000L;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  540 */       log("Failed to decode schedule " + schedule, e);
/*      */     }
/*  542 */     return Long.MAX_VALUE;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCheckFrequencyMins()
/*      */   {
/*  550 */     if (this.interval_override > 0)
/*      */     {
/*  552 */       return this.interval_override;
/*      */     }
/*      */     
/*  555 */     Map schedule = this.subs.getScheduleConfig();
/*      */     
/*  557 */     if (schedule.size() == 0)
/*      */     {
/*  559 */       return 120;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  564 */       return ((Long)schedule.get("interval")).intValue();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*  570 */     return 120;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setCheckFrequencyMins(int mins)
/*      */   {
/*  579 */     this.interval_override = mins;
/*      */     
/*  581 */     saveConfig(1);
/*      */     
/*  583 */     this.subs.fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNumUnread()
/*      */   {
/*  589 */     return this.num_unread;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNumRead()
/*      */   {
/*  595 */     return this.num_read;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SubscriptionResult[] getResults(boolean include_deleted)
/*      */   {
/*      */     SubscriptionResult[] results;
/*      */     
/*  604 */     synchronized (this)
/*      */     {
/*  606 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  608 */       results = (SubscriptionResult[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */     }
/*      */     
/*  611 */     if (include_deleted)
/*      */     {
/*  613 */       return results;
/*      */     }
/*      */     
/*      */ 
/*  617 */     List l = new ArrayList(results.length);
/*      */     
/*  619 */     for (int i = 0; i < results.length; i++)
/*      */     {
/*  621 */       if (!results[i].isDeleted())
/*      */       {
/*  623 */         l.add(results[i]);
/*      */       }
/*      */     }
/*      */     
/*  627 */     return (SubscriptionResult[])l.toArray(new SubscriptionResult[l.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public SubscriptionResult getResult(String result_id)
/*      */   {
/*  635 */     synchronized (this)
/*      */     {
/*  637 */       LinkedHashMap<String, SubscriptionResultImpl> results = this.manager.loadResults(this.subs);
/*      */       
/*  639 */       return (SubscriptionResult)results.get(result_id);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateResult(SubscriptionResultImpl result)
/*      */   {
/*  647 */     byte[] key = result.getKey1();
/*      */     
/*  649 */     boolean changed = false;
/*      */     
/*  651 */     synchronized (this)
/*      */     {
/*  653 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  655 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  657 */       for (int i = 0; i < results.length; i++)
/*      */       {
/*  659 */         if (Arrays.equals(results[i].getKey1(), key))
/*      */         {
/*  661 */           results[i] = result;
/*      */           
/*  663 */           changed = true;
/*      */         }
/*      */       }
/*      */       
/*  667 */       if (changed)
/*      */       {
/*  669 */         updateReadUnread(results);
/*      */         
/*  671 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */     }
/*      */     
/*  675 */     if (changed)
/*      */     {
/*  677 */       saveConfig(2);
/*      */     }
/*      */     
/*  680 */     if ((isAutoDownload()) && (!result.getRead()) && (!result.isDeleted()))
/*      */     {
/*  682 */       this.manager.getScheduler().download(this.subs, result);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void deleteResults(String[] result_ids)
/*      */   {
/*  691 */     ByteArrayHashMap rids = new ByteArrayHashMap();
/*      */     
/*  693 */     for (int i = 0; i < result_ids.length; i++)
/*      */     {
/*  695 */       rids.put(Base32.decode(result_ids[i]), "");
/*      */     }
/*      */     
/*  698 */     boolean changed = false;
/*      */     
/*  700 */     synchronized (this)
/*      */     {
/*  702 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  704 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  706 */       for (int i = 0; i < results.length; i++)
/*      */       {
/*  708 */         SubscriptionResultImpl result = results[i];
/*      */         
/*  710 */         if ((!result.isDeleted()) && (rids.containsKey(result.getKey1())))
/*      */         {
/*  712 */           changed = true;
/*      */           
/*  714 */           result.deleteInternal();
/*      */         }
/*      */       }
/*      */       
/*  718 */       if (changed)
/*      */       {
/*  720 */         updateReadUnread(results);
/*      */         
/*  722 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */     }
/*      */     
/*  726 */     if (changed)
/*      */     {
/*  728 */       saveConfig(2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void deleteAllResults()
/*      */   {
/*  735 */     boolean changed = false;
/*      */     
/*  737 */     synchronized (this)
/*      */     {
/*  739 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  741 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  743 */       for (int i = 0; i < results.length; i++)
/*      */       {
/*  745 */         SubscriptionResultImpl result = results[i];
/*      */         
/*  747 */         if (!result.isDeleted())
/*      */         {
/*  749 */           changed = true;
/*      */           
/*  751 */           result.deleteInternal();
/*      */         }
/*      */       }
/*      */       
/*  755 */       if (changed)
/*      */       {
/*  757 */         updateReadUnread(results);
/*      */         
/*  759 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */     }
/*      */     
/*  763 */     if (changed)
/*      */     {
/*  765 */       saveConfig(2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void markAllResultsRead()
/*      */   {
/*  772 */     boolean changed = false;
/*      */     
/*  774 */     synchronized (this)
/*      */     {
/*  776 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  778 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  780 */       for (int i = 0; i < results.length; i++)
/*      */       {
/*  782 */         SubscriptionResultImpl result = results[i];
/*      */         
/*  784 */         if (!result.getRead())
/*      */         {
/*  786 */           changed = true;
/*      */           
/*  788 */           result.setReadInternal(true);
/*      */         }
/*      */       }
/*      */       
/*  792 */       if (changed)
/*      */       {
/*  794 */         updateReadUnread(results);
/*      */         
/*  796 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */     }
/*      */     
/*  800 */     if (changed)
/*      */     {
/*  802 */       saveConfig(2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void markAllResultsUnread()
/*      */   {
/*  809 */     boolean changed = false;
/*      */     
/*  811 */     synchronized (this)
/*      */     {
/*  813 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  815 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  817 */       for (int i = 0; i < results.length; i++)
/*      */       {
/*  819 */         SubscriptionResultImpl result = results[i];
/*      */         
/*  821 */         if (result.getRead())
/*      */         {
/*  823 */           changed = true;
/*      */           
/*  825 */           result.setReadInternal(false);
/*      */         }
/*      */       }
/*      */       
/*  829 */       if (changed)
/*      */       {
/*  831 */         updateReadUnread(results);
/*      */         
/*  833 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */     }
/*      */     
/*  837 */     if (changed)
/*      */     {
/*  839 */       saveConfig(2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void markResults(String[] result_ids, boolean[] reads)
/*      */   {
/*  848 */     ByteArrayHashMap rid_map = new ByteArrayHashMap();
/*      */     
/*  850 */     for (int i = 0; i < result_ids.length; i++)
/*      */     {
/*  852 */       rid_map.put(Base32.decode(result_ids[i]), Boolean.valueOf(reads[i]));
/*      */     }
/*      */     
/*  855 */     boolean changed = false;
/*      */     
/*  857 */     List newly_unread = new ArrayList();
/*      */     
/*  859 */     synchronized (this)
/*      */     {
/*  861 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  863 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  865 */       for (int i = 0; i < results.length; i++)
/*      */       {
/*  867 */         SubscriptionResultImpl result = results[i];
/*      */         
/*  869 */         if (!result.isDeleted())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  874 */           Boolean b_read = (Boolean)rid_map.get(result.getKey1());
/*      */           
/*  876 */           if (b_read != null)
/*      */           {
/*  878 */             boolean read = b_read.booleanValue();
/*      */             
/*  880 */             if (result.getRead() != read)
/*      */             {
/*  882 */               changed = true;
/*      */               
/*  884 */               result.setReadInternal(read);
/*      */               
/*  886 */               if (!read)
/*      */               {
/*  888 */                 newly_unread.add(result);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  894 */       if (changed)
/*      */       {
/*  896 */         updateReadUnread(results);
/*      */         
/*  898 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */     }
/*      */     
/*  902 */     if (changed)
/*      */     {
/*  904 */       saveConfig(2);
/*      */     }
/*      */     
/*  907 */     if (isAutoDownload())
/*      */     {
/*  909 */       for (int i = 0; i < newly_unread.size(); i++)
/*      */       {
/*  911 */         this.manager.getScheduler().download(this.subs, (SubscriptionResult)newly_unread.get(i));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/*  919 */     synchronized (this)
/*      */     {
/*  921 */       LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */       
/*  923 */       SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */       
/*  925 */       if (results.length > 0)
/*      */       {
/*  927 */         results = new SubscriptionResultImpl[0];
/*      */         
/*  929 */         this.manager.saveResults(this.subs, results);
/*      */       }
/*      */       
/*  932 */       updateReadUnread(results);
/*      */     }
/*      */     
/*  935 */     this.last_error = null;
/*  936 */     this.last_new_result = 0L;
/*  937 */     this.last_scan = 0L;
/*      */     
/*  939 */     saveConfig(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkMaxResults(int max_results)
/*      */   {
/*  946 */     if (max_results <= 0)
/*      */     {
/*  948 */       return;
/*      */     }
/*      */     
/*  951 */     boolean changed = false;
/*      */     
/*  953 */     synchronized (this)
/*      */     {
/*  955 */       if (this.num_unread + this.num_read > max_results)
/*      */       {
/*  957 */         LinkedHashMap<String, SubscriptionResultImpl> results_map = this.manager.loadResults(this.subs);
/*      */         
/*  959 */         SubscriptionResultImpl[] results = (SubscriptionResultImpl[])results_map.values().toArray(new SubscriptionResultImpl[results_map.size()]);
/*      */         
/*  961 */         for (int i = 0; i < results.length; i++)
/*      */         {
/*  963 */           SubscriptionResultImpl r = results[i];
/*      */           
/*  965 */           if (!r.isDeleted())
/*      */           {
/*  967 */             if (r.getRead())
/*      */             {
/*  969 */               this.num_read -= 1;
/*      */             }
/*      */             else
/*      */             {
/*  973 */               this.num_unread -= 1;
/*      */             }
/*      */             
/*  976 */             r.deleteInternal();
/*      */             
/*  978 */             changed = true;
/*      */             
/*  980 */             if (this.num_unread + this.num_read <= max_results) {
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  987 */         if (changed)
/*      */         {
/*  989 */           this.manager.saveResults(this.subs, results);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  994 */     if (changed)
/*      */     {
/*  996 */       saveConfig(2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateReadUnread(SubscriptionResultImpl[] results)
/*      */   {
/* 1004 */     int new_unread = 0;
/* 1005 */     int new_read = 0;
/*      */     
/* 1007 */     for (int i = 0; i < results.length; i++)
/*      */     {
/* 1009 */       SubscriptionResultImpl result = results[i];
/*      */       
/* 1011 */       if (!result.isDeleted())
/*      */       {
/* 1013 */         if (result.getRead())
/*      */         {
/* 1015 */           new_read++;
/*      */         }
/*      */         else
/*      */         {
/* 1019 */           new_unread++;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1024 */     this.num_read = new_read;
/* 1025 */     this.num_unread = new_unread;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isAutoDownloadSupported()
/*      */   {
/* 1031 */     return this.auto_dl_supported;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setFatalError(String _error)
/*      */   {
/* 1038 */     this.last_error = _error;
/* 1039 */     this.consec_fails = 1024;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setLastError(String _last_error, boolean _auth_failed)
/*      */   {
/* 1047 */     this.last_error = _last_error;
/* 1048 */     this.auth_failed = _auth_failed;
/*      */     
/* 1050 */     if (this.last_error == null)
/*      */     {
/* 1052 */       this.consec_fails = 0;
/*      */     }
/*      */     else
/*      */     {
/* 1056 */       this.consec_fails += 1;
/*      */     }
/*      */     
/* 1059 */     this.subs.fireChanged(1);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getLastError()
/*      */   {
/* 1065 */     return this.last_error;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAuthFail()
/*      */   {
/* 1071 */     return this.auth_failed;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getConsecFails()
/*      */   {
/* 1077 */     return this.consec_fails;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getDownloadWithReferer()
/*      */   {
/* 1083 */     return this.dl_with_ref;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDownloadWithReferer(boolean b)
/*      */   {
/* 1090 */     if (b != this.dl_with_ref)
/*      */     {
/* 1092 */       this.dl_with_ref = b;
/*      */       
/* 1094 */       saveConfig(1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void loadConfig()
/*      */   {
/* 1101 */     Map map = this.subs.getHistoryConfig();
/*      */     
/* 1103 */     Long l_enabled = (Long)map.get("enabled");
/* 1104 */     this.enabled = (l_enabled == null);
/*      */     
/* 1106 */     Long l_auto_dl = (Long)map.get("auto_dl");
/* 1107 */     this.auto_dl = (l_auto_dl != null);
/*      */     
/* 1109 */     Long l_last_scan = (Long)map.get("last_scan");
/* 1110 */     this.last_scan = (l_last_scan == null ? 0L : l_last_scan.longValue());
/*      */     
/* 1112 */     Long l_last_new = (Long)map.get("last_new");
/* 1113 */     this.last_new_result = (l_last_new == null ? 0L : l_last_new.longValue());
/*      */     
/* 1115 */     Long l_num_unread = (Long)map.get("num_unread");
/* 1116 */     this.num_unread = (l_num_unread == null ? 0 : l_num_unread.intValue());
/*      */     
/* 1118 */     Long l_num_read = (Long)map.get("num_read");
/* 1119 */     this.num_read = (l_num_read == null ? 0 : l_num_read.intValue());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1124 */     Long l_auto_dl_s = (Long)map.get("auto_dl_supported");
/* 1125 */     this.auto_dl_supported = (this.last_scan > 0L);
/*      */     
/* 1127 */     Long l_dl_with_ref = (Long)map.get("dl_with_ref");
/* 1128 */     this.dl_with_ref = (l_dl_with_ref == null);
/*      */     
/* 1130 */     Long l_interval_override = (Long)map.get("interval_override");
/* 1131 */     this.interval_override = (l_interval_override == null ? 0 : l_interval_override.intValue());
/*      */     
/* 1133 */     Long l_max_results = (Long)map.get("max_results");
/* 1134 */     this.max_results = (l_max_results == null ? -1L : l_max_results.longValue());
/*      */     
/* 1136 */     String s_networks = ImportExportUtils.importString(map, "nets", null);
/*      */     
/* 1138 */     if (s_networks != null) {
/* 1139 */       this.networks = s_networks.split(",");
/* 1140 */       for (int i = 0; i < this.networks.length; i++) {
/* 1141 */         this.networks[i] = AENetworkClassifier.internalise(this.networks[i]);
/*      */       }
/*      */     }
/*      */     
/* 1145 */     Long l_post_noto = (Long)map.get("post_noti");
/* 1146 */     this.post_notifications = (l_post_noto != null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void saveConfig(int reason)
/*      */   {
/* 1154 */     Map map = new HashMap();
/*      */     
/* 1156 */     map.put("enabled", new Long(this.enabled ? 1L : 0L));
/* 1157 */     map.put("auto_dl", new Long(this.auto_dl ? 1L : 0L));
/* 1158 */     map.put("auto_dl_supported", new Long(this.auto_dl_supported ? 1L : 0L));
/* 1159 */     map.put("last_scan", new Long(this.last_scan));
/* 1160 */     map.put("last_new", new Long(this.last_new_result));
/* 1161 */     map.put("num_unread", new Long(this.num_unread));
/* 1162 */     map.put("num_read", new Long(this.num_read));
/* 1163 */     map.put("dl_with_ref", new Long(this.dl_with_ref ? 1L : 0L));
/* 1164 */     map.put("max_results", new Long(this.max_results));
/*      */     
/* 1166 */     if (this.interval_override > 0) {
/* 1167 */       map.put("interval_override", new Long(this.interval_override));
/*      */     }
/* 1169 */     if (this.networks != null) {
/* 1170 */       String str = "";
/* 1171 */       for (String net : this.networks) {
/* 1172 */         str = str + (str.length() == 0 ? "" : ",") + net;
/*      */       }
/* 1174 */       map.put("nets", str);
/*      */     }
/* 1176 */     if (this.post_notifications) {
/* 1177 */       map.put("post_noti", Integer.valueOf(1));
/*      */     }
/* 1179 */     this.subs.updateHistoryConfig(map, reason);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1186 */     this.subs.log("History: " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1194 */     this.subs.log("History: " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getString()
/*      */   {
/* 1200 */     return "unread=" + this.num_unread + ",read=" + this.num_read + ",last_err=" + this.last_error;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/subs/impl/SubscriptionHistoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */