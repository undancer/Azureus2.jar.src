/*      */ package org.gudy.azureus2.core3.history.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerFactory;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateFactory;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.history.DownloadHistory;
/*      */ import org.gudy.azureus2.core3.history.DownloadHistoryEvent;
/*      */ import org.gudy.azureus2.core3.history.DownloadHistoryListener;
/*      */ import org.gudy.azureus2.core3.history.DownloadHistoryManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*      */ public class DownloadHistoryManagerImpl
/*      */   implements DownloadHistoryManager
/*      */ {
/*      */   private static final String CONFIG_ENABLED = "Download History Enabled";
/*      */   private static final String CONFIG_ACTIVE_FILE = "dlhistorya.config";
/*      */   private static final String CONFIG_DEAD_FILE = "dlhistoryd.config";
/*      */   private static final String CONFIG_ACTIVE_SIZE = "download.history.active.size";
/*      */   private static final String CONFIG_DEAD_SIZE = "download.history.dead.size";
/*      */   private static final int UPDATE_TYPE_ACTIVE = 1;
/*      */   private static final int UPDATE_TYPE_DEAD = 16;
/*      */   private static final int UPDATE_TYPE_BOTH = 17;
/*      */   private final AzureusCore azureus_core;
/*   74 */   private final ListenerManager<DownloadHistoryListener> listeners = ListenerManager.createAsyncManager("DHM", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(DownloadHistoryListener listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*   85 */       listener.downloadHistoryEventOccurred((DownloadHistoryEvent)value);
/*      */     }
/*   74 */   });
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
/*   89 */   final Object lock = new Object();
/*      */   
/*   91 */   private WeakReference<Map<Long, DownloadHistoryImpl>> history_active = new WeakReference(null);
/*   92 */   private WeakReference<Map<Long, DownloadHistoryImpl>> history_dead = new WeakReference(null);
/*      */   
/*   94 */   private volatile int active_history_size = COConfigurationManager.getIntParameter("download.history.active.size", 0);
/*   95 */   private volatile int dead_history_size = COConfigurationManager.getIntParameter("download.history.dead.size", 0);
/*      */   
/*      */   private Map<Long, DownloadHistoryImpl> active_dirty;
/*      */   
/*      */   private Map<Long, DownloadHistoryImpl> dead_dirty;
/*      */   
/*      */   private TimerEvent write_pending_event;
/*      */   
/*      */   private long active_load_time;
/*      */   private long dead_load_time;
/*  105 */   private boolean history_escaped = false;
/*      */   
/*  107 */   private final Map<Long, Long> redownload_cache = new HashMap();
/*      */   
/*      */   private boolean enabled;
/*      */   
/*      */ 
/*      */   public DownloadHistoryManagerImpl()
/*      */   {
/*  114 */     this.azureus_core = AzureusCoreFactory.getSingleton();
/*      */     
/*  116 */     COConfigurationManager.addAndFireParameterListener("Download History Enabled", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*  120 */       private boolean first_time = true;
/*      */       
/*      */       public void parameterChanged(String name)
/*      */       {
/*  124 */         DownloadHistoryManagerImpl.this.setEnabledSupport(COConfigurationManager.getBooleanParameter(name), this.first_time);
/*      */         
/*  126 */         this.first_time = false;
/*      */       }
/*      */       
/*  129 */     });
/*  130 */     this.azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*      */       {
/*      */ 
/*      */ 
/*  138 */         if ((component instanceof GlobalManager))
/*      */         {
/*  140 */           GlobalManager global_manager = (GlobalManager)component;
/*      */           
/*  142 */           global_manager.addListener(new GlobalManagerAdapter()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void downloadManagerAdded(DownloadManager dm)
/*      */             {
/*      */ 
/*  149 */               synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */               {
/*  151 */                 if ((!DownloadHistoryManagerImpl.this.enabled) || (!DownloadHistoryManagerImpl.this.isMonitored(dm)))
/*      */                 {
/*  153 */                   return;
/*      */                 }
/*      */                 
/*  156 */                 Map<Long, DownloadHistoryManagerImpl.DownloadHistoryImpl> active_history = DownloadHistoryManagerImpl.this.getActiveHistory();
/*      */                 
/*  158 */                 DownloadHistoryManagerImpl.DownloadHistoryImpl new_dh = new DownloadHistoryManagerImpl.DownloadHistoryImpl(DownloadHistoryManagerImpl.this, active_history, dm, null);
/*      */                 
/*  160 */                 long uid = new_dh.getUID();
/*      */                 
/*  162 */                 DownloadHistoryManagerImpl.DownloadHistoryImpl old_dh = (DownloadHistoryManagerImpl.DownloadHistoryImpl)active_history.put(Long.valueOf(uid), new_dh);
/*      */                 
/*  164 */                 if (old_dh != null)
/*      */                 {
/*  166 */                   DownloadHistoryManagerImpl.this.historyUpdated(old_dh, 2, 1);
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  171 */                 old_dh = (DownloadHistoryManagerImpl.DownloadHistoryImpl)DownloadHistoryManagerImpl.this.getDeadHistory().remove(Long.valueOf(uid));
/*      */                 
/*  173 */                 if (old_dh != null)
/*      */                 {
/*  175 */                   DownloadHistoryManagerImpl.this.historyUpdated(old_dh, 2, 16);
/*      */                 }
/*      */                 
/*  178 */                 DownloadHistoryManagerImpl.this.historyUpdated(new_dh, 1, 1);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void downloadManagerRemoved(DownloadManager dm)
/*      */             {
/*  186 */               synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */               {
/*  188 */                 if ((!DownloadHistoryManagerImpl.this.enabled) || (!DownloadHistoryManagerImpl.this.isMonitored(dm)))
/*      */                 {
/*  190 */                   return;
/*      */                 }
/*      */                 
/*  193 */                 long uid = DownloadHistoryManagerImpl.getUID(dm);
/*      */                 
/*  195 */                 DownloadHistoryManagerImpl.DownloadHistoryImpl dh = (DownloadHistoryManagerImpl.DownloadHistoryImpl)DownloadHistoryManagerImpl.this.getActiveHistory().remove(Long.valueOf(uid));
/*      */                 
/*  197 */                 if (dh != null)
/*      */                 {
/*  199 */                   Map<Long, DownloadHistoryManagerImpl.DownloadHistoryImpl> dead_history = DownloadHistoryManagerImpl.this.getDeadHistory();
/*      */                   
/*  201 */                   dead_history.put(Long.valueOf(uid), dh);
/*      */                   
/*  203 */                   DownloadHistoryManagerImpl.DownloadHistoryImpl.access$800(dh, dead_history);
/*      */                   
/*  205 */                   DownloadHistoryManagerImpl.DownloadHistoryImpl.access$900(dh, SystemTime.getCurrentTime());
/*      */                   
/*  207 */                   DownloadHistoryManagerImpl.this.historyUpdated(dh, 3, 17); } } } }, false);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  213 */           DownloadManagerFactory.addGlobalDownloadListener(new DownloadManagerAdapter()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void completionChanged(DownloadManager dm, boolean comp)
/*      */             {
/*      */ 
/*      */ 
/*  222 */               synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */               {
/*  224 */                 if ((!DownloadHistoryManagerImpl.this.enabled) || (!DownloadHistoryManagerImpl.this.isMonitored(dm)))
/*      */                 {
/*  226 */                   return;
/*      */                 }
/*      */                 
/*  229 */                 long uid = DownloadHistoryManagerImpl.getUID(dm);
/*      */                 
/*  231 */                 DownloadHistoryManagerImpl.DownloadHistoryImpl dh = (DownloadHistoryManagerImpl.DownloadHistoryImpl)DownloadHistoryManagerImpl.this.getActiveHistory().get(Long.valueOf(uid));
/*      */                 
/*  233 */                 if (dh != null)
/*      */                 {
/*  235 */                   if (DownloadHistoryManagerImpl.DownloadHistoryImpl.access$1000(dh, dm.getDownloadState()))
/*      */                   {
/*  237 */                     DownloadHistoryManagerImpl.this.historyUpdated(dh, 3, 1);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*      */             }
/*  243 */           });
/*  244 */           DownloadManagerStateFactory.addGlobalListener(new DownloadManagerStateAttributeListener()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void attributeEventOccurred(DownloadManager dm, String attribute, int event_type)
/*      */             {
/*      */ 
/*      */ 
/*  253 */               synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */               {
/*  255 */                 if ((!DownloadHistoryManagerImpl.this.enabled) || (!DownloadHistoryManagerImpl.this.isMonitored(dm)))
/*      */                 {
/*  257 */                   return;
/*      */                 }
/*      */                 
/*  260 */                 long uid = DownloadHistoryManagerImpl.getUID(dm);
/*      */                 
/*  262 */                 DownloadHistoryManagerImpl.DownloadHistoryImpl dh = (DownloadHistoryManagerImpl.DownloadHistoryImpl)DownloadHistoryManagerImpl.this.getActiveHistory().get(Long.valueOf(uid));
/*      */                 
/*  264 */                 if (dh != null)
/*      */                 {
/*  266 */                   if (DownloadHistoryManagerImpl.DownloadHistoryImpl.access$1100(dh, dm))
/*      */                   {
/*  268 */                     DownloadHistoryManagerImpl.this.historyUpdated(dh, 3, 1); } } } } }, "canosavedir", 1);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  275 */           if (DownloadHistoryManagerImpl.this.enabled)
/*      */           {
/*  277 */             if (!FileUtil.resilientConfigFileExists("dlhistorya.config"))
/*      */             {
/*  279 */               DownloadHistoryManagerImpl.this.resetHistory();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void stopping(AzureusCore core)
/*      */       {
/*  289 */         synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */         {
/*  291 */           DownloadHistoryManagerImpl.this.writeHistory();
/*      */           
/*  293 */           COConfigurationManager.setParameter("download.history.active.size", DownloadHistoryManagerImpl.this.active_history_size);
/*  294 */           COConfigurationManager.setParameter("download.history.dead.size", DownloadHistoryManagerImpl.this.dead_history_size);
/*      */         }
/*      */         
/*      */       }
/*  298 */     });
/*  299 */     SimpleTimer.addPeriodicEvent("DHM:timer", 60000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*  306 */         synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */         {
/*  308 */           DownloadHistoryManagerImpl.this.checkDiscard();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/*  317 */     return this.enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean enabled)
/*      */   {
/*  324 */     COConfigurationManager.setParameter("Download History Enabled", enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setEnabledSupport(boolean b, boolean startup)
/*      */   {
/*  332 */     synchronized (this.lock)
/*      */     {
/*  334 */       if (this.enabled == b)
/*      */       {
/*  336 */         return;
/*      */       }
/*      */       
/*  339 */       this.enabled = b;
/*      */       
/*  341 */       if (!startup)
/*      */       {
/*  343 */         if (this.enabled)
/*      */         {
/*  345 */           resetHistory();
/*      */         }
/*      */         else
/*      */         {
/*  349 */           clearHistory();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean isMonitored(DownloadManager dm)
/*      */   {
/*  359 */     if (dm.isPersistent())
/*      */     {
/*  361 */       long flags = dm.getDownloadState().getFlags();
/*      */       
/*  363 */       if ((flags & 0x210) != 0L)
/*      */       {
/*  365 */         return false;
/*      */       }
/*      */       
/*      */ 
/*  369 */       return true;
/*      */     }
/*      */     
/*      */ 
/*  373 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void syncFromExisting(GlobalManager global_manager)
/*      */   {
/*  380 */     if (global_manager == null)
/*      */     {
/*  382 */       return;
/*      */     }
/*      */     
/*  385 */     synchronized (this.lock)
/*      */     {
/*  387 */       List<DownloadManager> dms = global_manager.getDownloadManagers();
/*      */       
/*  389 */       Map<Long, DownloadHistoryImpl> history = getActiveHistory();
/*      */       
/*  391 */       if (history.size() > 0)
/*      */       {
/*  393 */         List<DownloadHistory> existing = new ArrayList(history.values());
/*      */         
/*  395 */         history.clear();
/*      */         
/*  397 */         historyUpdated(new ArrayList(existing), 2, 1);
/*      */       }
/*      */       
/*  400 */       for (DownloadManager dm : dms)
/*      */       {
/*  402 */         if (isMonitored(dm))
/*      */         {
/*  404 */           DownloadHistoryImpl new_dh = new DownloadHistoryImpl(history, dm, null);
/*      */           
/*  406 */           history.put(Long.valueOf(new_dh.getUID()), new_dh);
/*      */         }
/*      */       }
/*      */       
/*  410 */       historyUpdated(new ArrayList(history.values()), 1, 1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private List<DownloadHistory> getHistory()
/*      */   {
/*  417 */     synchronized (this.lock)
/*      */     {
/*  419 */       Map<Long, DownloadHistoryImpl> active = getActiveHistory();
/*  420 */       Map<Long, DownloadHistoryImpl> dead = getDeadHistory();
/*      */       
/*  422 */       List<DownloadHistory> result = new ArrayList(active.size() + dead.size());
/*      */       
/*  424 */       result.addAll(active.values());
/*  425 */       result.addAll(dead.values());
/*      */       
/*  427 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getHistoryCount()
/*      */   {
/*  434 */     return this.active_history_size + this.dead_history_size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeHistory(List<DownloadHistory> to_remove)
/*      */   {
/*  441 */     synchronized (this.lock)
/*      */     {
/*  443 */       List<DownloadHistory> removed = new ArrayList(to_remove.size());
/*      */       
/*  445 */       int update_type = 0;
/*      */       
/*  447 */       Map<Long, DownloadHistoryImpl> active = getActiveHistory();
/*  448 */       Map<Long, DownloadHistoryImpl> dead = getDeadHistory();
/*      */       
/*  450 */       for (DownloadHistory h : to_remove)
/*      */       {
/*  452 */         long uid = h.getUID();
/*      */         
/*  454 */         DownloadHistoryImpl r = (DownloadHistoryImpl)active.remove(Long.valueOf(uid));
/*      */         
/*  456 */         if (r != null)
/*      */         {
/*  458 */           removed.add(r);
/*      */           
/*  460 */           update_type |= 0x1;
/*      */         }
/*      */         else
/*      */         {
/*  464 */           r = (DownloadHistoryImpl)dead.remove(Long.valueOf(uid));
/*      */           
/*  466 */           if (r != null)
/*      */           {
/*  468 */             removed.add(r);
/*      */             
/*  470 */             update_type |= 0x10;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  475 */       if (removed.size() > 0)
/*      */       {
/*  477 */         historyUpdated(removed, 2, update_type);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void clearHistory()
/*      */   {
/*  485 */     synchronized (this.lock)
/*      */     {
/*  487 */       Map<Long, DownloadHistoryImpl> active = getActiveHistory();
/*  488 */       Map<Long, DownloadHistoryImpl> dead = getDeadHistory();
/*      */       
/*  490 */       int update_type = 0;
/*      */       
/*  492 */       List<DownloadHistory> entries = getHistory();
/*      */       
/*  494 */       if (active.size() > 0)
/*      */       {
/*  496 */         active.clear();
/*      */         
/*  498 */         update_type |= 0x1;
/*      */       }
/*      */       
/*  501 */       if (dead.size() > 0)
/*      */       {
/*  503 */         dead.clear();
/*      */         
/*  505 */         update_type |= 0x10;
/*      */       }
/*      */       
/*  508 */       if (update_type != 0)
/*      */       {
/*  510 */         historyUpdated(entries, 2, update_type);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void resetHistory()
/*      */   {
/*  518 */     synchronized (this.lock)
/*      */     {
/*  520 */       clearHistory();
/*      */       
/*  522 */       syncFromExisting(this.azureus_core.getGlobalManager());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long[] getDates(byte[] hash)
/*      */   {
/*  530 */     List<DownloadHistory> history = getHistory();
/*      */     
/*  532 */     for (DownloadHistory dh : history)
/*      */     {
/*  534 */       if (Arrays.equals(hash, dh.getTorrentHash()))
/*      */       {
/*  536 */         Long rdl = (Long)this.redownload_cache.remove(Long.valueOf(dh.getUID()));
/*      */         
/*  538 */         long[] result = { dh.getAddTime(), dh.getCompleteTime(), dh.getRemoveTime(), rdl == null ? 0L : rdl.longValue() };
/*      */         
/*  540 */         return result;
/*      */       }
/*      */     }
/*      */     
/*  544 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setRedownloading(DownloadHistory dh)
/*      */   {
/*  551 */     this.redownload_cache.put(Long.valueOf(dh.getUID()), Long.valueOf(SystemTime.getCurrentTime()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static long getUID(DownloadManager dm)
/*      */   {
/*  558 */     TOTorrent torrent = dm.getTorrent();
/*      */     
/*      */     long lhs;
/*      */     long lhs;
/*  562 */     if (torrent == null)
/*      */     {
/*  564 */       lhs = 0L;
/*      */     }
/*      */     else {
/*      */       try
/*      */       {
/*  569 */         byte[] hash = torrent.getHash();
/*      */         
/*  571 */         lhs = hash[0] << 24 & 0xFF000000 | hash[1] << 16 & 0xFF0000 | hash[2] << 8 & 0xFF00 | hash[3] & 0xFF;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  575 */         lhs = 0L;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  580 */     long date_added = dm.getDownloadState().getLongAttribute("stats.download.added.time");
/*      */     
/*  582 */     long rhs = date_added / 1000L;
/*      */     
/*  584 */     return lhs << 32 | rhs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(DownloadHistoryListener listener, boolean fire_for_existing)
/*      */   {
/*  592 */     synchronized (this.lock)
/*      */     {
/*  594 */       this.history_escaped = true;
/*      */       
/*  596 */       this.listeners.addListener(listener);
/*      */       
/*  598 */       if (fire_for_existing)
/*      */       {
/*  600 */         List<DownloadHistory> history = getHistory();
/*      */         
/*  602 */         this.listeners.dispatch(listener, 0, new DownloadHistoryEventImpl(1, history, null));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DownloadHistoryListener listener)
/*      */   {
/*  611 */     synchronized (this.lock)
/*      */     {
/*  613 */       this.listeners.removeListener(listener);
/*      */       
/*  615 */       if (this.listeners.size() == 0L)
/*      */       {
/*  617 */         this.history_escaped = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private Map<Long, DownloadHistoryImpl> getActiveHistory()
/*      */   {
/*  625 */     Map<Long, DownloadHistoryImpl> ref = (Map)this.history_active.get();
/*      */     
/*  627 */     if (ref == null)
/*      */     {
/*  629 */       ref = loadHistory("dlhistorya.config");
/*      */       
/*  631 */       this.active_load_time = SystemTime.getMonotonousTime();
/*      */       
/*  633 */       this.history_active = new WeakReference(ref);
/*      */       
/*  635 */       this.active_history_size = ref.size();
/*      */     }
/*      */     
/*  638 */     return ref;
/*      */   }
/*      */   
/*      */ 
/*      */   private Map<Long, DownloadHistoryImpl> getDeadHistory()
/*      */   {
/*  644 */     Map<Long, DownloadHistoryImpl> ref = (Map)this.history_dead.get();
/*      */     
/*  646 */     if (ref == null)
/*      */     {
/*  648 */       ref = loadHistory("dlhistoryd.config");
/*      */       
/*  650 */       this.dead_load_time = SystemTime.getMonotonousTime();
/*      */       
/*  652 */       this.history_dead = new WeakReference(ref);
/*      */       
/*  654 */       this.dead_history_size = ref.size();
/*      */     }
/*      */     
/*  657 */     return ref;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void historyUpdated(DownloadHistory dh, int action, int type)
/*      */   {
/*  666 */     List<DownloadHistory> list = new ArrayList(1);
/*      */     
/*  668 */     list.add(dh);
/*      */     
/*  670 */     historyUpdated(list, action, type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void historyUpdated(Collection<DownloadHistory> list, int action, int type)
/*      */   {
/*  679 */     if ((type & 0x1) != 0)
/*      */     {
/*  681 */       Map<Long, DownloadHistoryImpl> active = getActiveHistory();
/*      */       
/*  683 */       this.active_history_size = active.size();
/*      */       
/*  685 */       this.active_dirty = active;
/*      */     }
/*      */     
/*  688 */     if ((type & 0x10) != 0)
/*      */     {
/*  690 */       Map<Long, DownloadHistoryImpl> dead = getDeadHistory();
/*      */       
/*  692 */       this.dead_history_size = dead.size();
/*      */       
/*  694 */       this.dead_dirty = dead;
/*      */     }
/*      */     
/*  697 */     if (this.write_pending_event == null)
/*      */     {
/*  699 */       this.write_pending_event = SimpleTimer.addEvent("DHL:write", SystemTime.getOffsetTime(15000L), new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  707 */           synchronized (DownloadHistoryManagerImpl.this.lock)
/*      */           {
/*  709 */             DownloadHistoryManagerImpl.this.write_pending_event = null;
/*      */             
/*  711 */             DownloadHistoryManagerImpl.this.writeHistory();
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  717 */     this.listeners.dispatch(0, new DownloadHistoryEventImpl(action, new ArrayList(list), null));
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkDiscard()
/*      */   {
/*  723 */     if (this.history_escaped)
/*      */     {
/*  725 */       return;
/*      */     }
/*      */     
/*  728 */     long now = SystemTime.getMonotonousTime();
/*      */     
/*  730 */     if ((now - this.active_load_time > 30000L) && (this.active_dirty == null) && (this.history_active.get() != null))
/*      */     {
/*  732 */       this.history_active.clear();
/*      */     }
/*      */     
/*  735 */     if ((now - this.dead_load_time > 30000L) && (this.dead_dirty == null) && (this.history_dead.get() != null))
/*      */     {
/*  737 */       this.history_dead.clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void writeHistory()
/*      */   {
/*  744 */     if (this.active_dirty != null)
/*      */     {
/*  746 */       saveHistory("dlhistorya.config", this.active_dirty);
/*      */       
/*  748 */       this.active_dirty = null;
/*      */     }
/*      */     
/*  751 */     if (this.dead_dirty != null)
/*      */     {
/*  753 */       saveHistory("dlhistoryd.config", this.dead_dirty);
/*      */       
/*  755 */       this.dead_dirty = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Map<Long, DownloadHistoryImpl> loadHistory(String file)
/*      */   {
/*  763 */     Map<Long, DownloadHistoryImpl> result = new HashMap();
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  768 */       if (FileUtil.resilientConfigFileExists(file))
/*      */       {
/*  770 */         Map map = FileUtil.readResilientConfigFile(file);
/*      */         
/*  772 */         List<Map<String, Object>> list = (List)map.get("records");
/*      */         
/*  774 */         for (Map<String, Object> m : list) {
/*      */           try
/*      */           {
/*  777 */             DownloadHistoryImpl record = new DownloadHistoryImpl(result, m, null);
/*      */             
/*  779 */             result.put(Long.valueOf(record.getUID()), record);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  783 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  789 */       Debug.out(e);
/*      */     }
/*      */     
/*  792 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void saveHistory(String file, Map<Long, DownloadHistoryImpl> records)
/*      */   {
/*      */     try
/*      */     {
/*  803 */       Map<String, Object> map = new HashMap();
/*      */       
/*  805 */       List<Map<String, Object>> list = new ArrayList(records.size());
/*      */       
/*  807 */       map.put("records", list);
/*      */       
/*  809 */       for (DownloadHistoryImpl record : records.values()) {
/*      */         try
/*      */         {
/*  812 */           Map<String, Object> m = record.exportToMap();
/*      */           
/*  814 */           list.add(m);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  818 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  822 */       FileUtil.writeResilientConfigFile(file, map);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  826 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class DownloadHistoryEventImpl
/*      */     implements DownloadHistoryEvent
/*      */   {
/*      */     private final int type;
/*      */     
/*      */     private final List<DownloadHistory> history;
/*      */     
/*      */ 
/*      */     private DownloadHistoryEventImpl(int _type, List<DownloadHistory> _history)
/*      */     {
/*  842 */       this.type = _type;
/*  843 */       this.history = _history;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getEventType()
/*      */     {
/*  849 */       return this.type;
/*      */     }
/*      */     
/*      */ 
/*      */     public List<DownloadHistory> getHistory()
/*      */     {
/*  855 */       return this.history;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private class DownloadHistoryImpl
/*      */     implements DownloadHistory
/*      */   {
/*      */     private final long uid;
/*      */     private final byte[] hash;
/*      */     private final long size;
/*  866 */     private String name = "test test test";
/*  867 */     private String save_location = "somewhere or other";
/*  868 */     private long add_time = -1L;
/*  869 */     private long complete_time = -1L;
/*  870 */     private long remove_time = -1L;
/*      */     
/*      */ 
/*      */     private Map<Long, DownloadHistoryImpl> history_ref;
/*      */     
/*      */ 
/*      */ 
/*      */     private DownloadHistoryImpl(DownloadManager _history_ref)
/*      */     {
/*  879 */       this.history_ref = _history_ref;
/*      */       
/*  881 */       this.uid = DownloadHistoryManagerImpl.getUID(dm);
/*      */       
/*  883 */       byte[] h = null;
/*      */       
/*  885 */       TOTorrent torrent = dm.getTorrent();
/*      */       
/*  887 */       if (torrent != null) {
/*      */         try
/*      */         {
/*  890 */           h = torrent.getHash();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*  896 */       this.hash = h;
/*      */       
/*  898 */       this.name = dm.getDisplayName();
/*      */       
/*  900 */       this.size = dm.getSize();
/*      */       
/*  902 */       this.save_location = dm.getSaveLocation().getAbsolutePath();
/*      */       
/*  904 */       DownloadManagerState dms = dm.getDownloadState();
/*      */       
/*  906 */       this.add_time = dms.getLongParameter("stats.download.added.time");
/*      */       
/*  908 */       updateCompleteTime(dms);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private DownloadHistoryImpl(Map<String, Object> _history_ref)
/*      */       throws IOException
/*      */     {
/*  918 */       this.history_ref = _history_ref;
/*      */       try
/*      */       {
/*  921 */         this.uid = ((Long)map.get("u")).longValue();
/*  922 */         this.hash = ((byte[])map.get("h"));
/*      */         
/*  924 */         this.name = new String((byte[])map.get("n"), "UTF-8");
/*  925 */         this.save_location = new String((byte[])map.get("s"), "UTF-8");
/*      */         
/*  927 */         Long l_size = (Long)map.get("z");
/*      */         
/*  929 */         this.size = (l_size == null ? 0L : l_size.longValue());
/*      */         
/*  931 */         this.add_time = ((Long)map.get("a")).longValue();
/*  932 */         this.complete_time = ((Long)map.get("c")).longValue();
/*  933 */         this.remove_time = ((Long)map.get("r")).longValue();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  937 */         throw e;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  941 */         throw new IOException("History decode failed: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setHistoryReference(Map<Long, DownloadHistoryImpl> ref)
/*      */     {
/*  949 */       this.history_ref = ref;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private Map<String, Object> exportToMap()
/*      */       throws IOException
/*      */     {
/*  957 */       Map<String, Object> map = new LightHashMap();
/*      */       
/*  959 */       map.put("u", Long.valueOf(this.uid));
/*  960 */       map.put("h", this.hash);
/*  961 */       map.put("n", this.name.getBytes("UTF-8"));
/*  962 */       map.put("z", Long.valueOf(this.size));
/*  963 */       map.put("s", this.save_location.getBytes("UTF-8"));
/*  964 */       map.put("a", Long.valueOf(this.add_time));
/*  965 */       map.put("c", Long.valueOf(this.complete_time));
/*  966 */       map.put("r", Long.valueOf(this.remove_time));
/*      */       
/*  968 */       return map;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean updateCompleteTime(DownloadManagerState dms)
/*      */     {
/*  975 */       long old_time = this.complete_time;
/*      */       
/*  977 */       long comp = dms.getLongAttribute("complt");
/*      */       
/*  979 */       if (comp == 0L)
/*      */       {
/*  981 */         this.complete_time = dms.getLongParameter("stats.download.completed.time");
/*      */       }
/*      */       else
/*      */       {
/*  985 */         this.complete_time = comp;
/*      */       }
/*      */       
/*  988 */       return this.complete_time != old_time;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean updateSaveLocation(DownloadManager dm)
/*      */     {
/*  995 */       String old_location = this.save_location;
/*      */       
/*  997 */       String loc = dm.getSaveLocation().getAbsolutePath();
/*      */       
/*  999 */       if (!loc.equals(old_location))
/*      */       {
/* 1001 */         this.save_location = loc;
/*      */         
/* 1003 */         return true;
/*      */       }
/*      */       
/*      */ 
/* 1007 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long getUID()
/*      */     {
/* 1014 */       return this.uid;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getTorrentHash()
/*      */     {
/* 1020 */       return this.hash;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getName()
/*      */     {
/* 1026 */       return this.name;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getSize()
/*      */     {
/* 1032 */       return this.size;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getSaveLocation()
/*      */     {
/* 1038 */       return this.save_location;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getAddTime()
/*      */     {
/* 1044 */       return this.add_time;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCompleteTime()
/*      */     {
/* 1050 */       return this.complete_time;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setRemoveTime(long time)
/*      */     {
/* 1057 */       this.remove_time = time;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getRemoveTime()
/*      */     {
/* 1063 */       return this.remove_time;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setRedownloading()
/*      */     {
/* 1069 */       DownloadHistoryManagerImpl.this.setRedownloading(this);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/history/impl/DownloadHistoryManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */