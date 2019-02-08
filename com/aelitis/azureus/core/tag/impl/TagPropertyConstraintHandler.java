/*      */ package com.aelitis.azureus.core.tag.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagPropertyListener;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.TagTypeListener;
/*      */ import com.aelitis.azureus.core.tag.TagTypeListener.TagEvent;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.tag.TaggableLifecycleAdapter;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
/*      */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*      */ public class TagPropertyConstraintHandler
/*      */   implements TagTypeListener, DownloadListener
/*      */ {
/*      */   private final AzureusCore azureus_core;
/*      */   private final TagManagerImpl tag_manager;
/*      */   private boolean initialised;
/*      */   private boolean initial_assignment_complete;
/*      */   private boolean stopping;
/*   70 */   final Map<Tag, TagConstraint> constrained_tags = new HashMap();
/*      */   
/*      */   private boolean dm_listener_added;
/*      */   
/*   74 */   final Map<Tag, Map<org.gudy.azureus2.core3.download.DownloadManager, Long>> apply_history = new HashMap();
/*      */   
/*   76 */   private final AsyncDispatcher dispatcher = new AsyncDispatcher("tag:constraints");
/*      */   
/*   78 */   private final FrequencyLimitedDispatcher freq_lim_dispatcher = new FrequencyLimitedDispatcher(new AERunnable()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void runSupport()
/*      */     {
/*      */ 
/*   85 */       TagPropertyConstraintHandler.this.checkFreqLimUpdates();
/*      */     }
/*   78 */   }, 5000);
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
/*   90 */   final IdentityHashMap<org.gudy.azureus2.core3.download.DownloadManager, List<TagConstraint>> freq_lim_pending = new IdentityHashMap();
/*      */   
/*      */ 
/*      */   private TimerEventPeriodic timer;
/*      */   
/*      */ 
/*      */   private TagPropertyConstraintHandler()
/*      */   {
/*   98 */     this.azureus_core = null;
/*   99 */     this.tag_manager = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TagPropertyConstraintHandler(AzureusCore _core, TagManagerImpl _tm)
/*      */   {
/*  107 */     this.azureus_core = _core;
/*  108 */     this.tag_manager = _tm;
/*      */     
/*  110 */     if (this.azureus_core != null)
/*      */     {
/*  112 */       this.azureus_core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void stopping(AzureusCore core)
/*      */         {
/*      */ 
/*  119 */           TagPropertyConstraintHandler.this.stopping = true;
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  124 */     this.tag_manager.addTaggableLifecycleListener(2L, new TaggableLifecycleAdapter()
/*      */     {
/*      */ 
/*      */       public void initialised(List<Taggable> current_taggables)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/*  133 */           TagType tt = TagPropertyConstraintHandler.this.tag_manager.getTagType(3);
/*      */           
/*  135 */           tt.addTagTypeListener(TagPropertyConstraintHandler.this, true);
/*      */         }
/*      */         finally
/*      */         {
/*  139 */           AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void azureusCoreRunning(AzureusCore core)
/*      */             {
/*      */ 
/*  146 */               synchronized (TagPropertyConstraintHandler.this.constrained_tags)
/*      */               {
/*  148 */                 TagPropertyConstraintHandler.this.initialised = true;
/*      */                 
/*  150 */                 TagPropertyConstraintHandler.this.apply(core.getGlobalManager().getDownloadManagers(), true);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void taggableCreated(Taggable taggable)
/*      */       {
/*  161 */         TagPropertyConstraintHandler.this.apply((org.gudy.azureus2.core3.download.DownloadManager)taggable, null, false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*  166 */   private static Object process_lock = new Object();
/*      */   
/*      */   private static int processing_disabled_count;
/*  169 */   private static List<Object[]> processing_queue = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */   public void setProcessingEnabled(boolean enabled)
/*      */   {
/*  175 */     synchronized (process_lock)
/*      */     {
/*  177 */       if (enabled)
/*      */       {
/*  179 */         processing_disabled_count -= 1;
/*      */         
/*  181 */         if (processing_disabled_count == 0)
/*      */         {
/*  183 */           List<Object[]> to_do = new ArrayList(processing_queue);
/*      */           
/*  185 */           processing_queue.clear();
/*      */           
/*  187 */           for (Object[] entry : to_do)
/*      */           {
/*  189 */             TagConstraint constraint = (TagConstraint)entry[0];
/*  190 */             Object target = entry[1];
/*      */             
/*      */             try
/*      */             {
/*  194 */               if ((target instanceof org.gudy.azureus2.core3.download.DownloadManager))
/*      */               {
/*  196 */                 constraint.apply((org.gudy.azureus2.core3.download.DownloadManager)target);
/*      */               }
/*      */               else
/*      */               {
/*  200 */                 constraint.apply((List)target);
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  204 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  210 */         processing_disabled_count += 1;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean canProcess(TagConstraint constraint, org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */   {
/*  220 */     synchronized (process_lock)
/*      */     {
/*  222 */       if (processing_disabled_count == 0)
/*      */       {
/*  224 */         return true;
/*      */       }
/*      */       
/*      */ 
/*  228 */       processing_queue.add(new Object[] { constraint, dm });
/*      */       
/*  230 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean canProcess(TagConstraint constraint, List<org.gudy.azureus2.core3.download.DownloadManager> dms)
/*      */   {
/*  240 */     synchronized (process_lock)
/*      */     {
/*  242 */       if (processing_disabled_count == 0)
/*      */       {
/*  244 */         return true;
/*      */       }
/*      */       
/*      */ 
/*  248 */       processing_queue.add(new Object[] { constraint, dms });
/*      */       
/*  250 */       return false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tagTypeChanged(TagType tag_type) {}
/*      */   
/*      */ 
/*      */ 
/*      */   public void tagEventOccurred(TagTypeListener.TagEvent event)
/*      */   {
/*  263 */     int type = event.getEventType();
/*  264 */     Tag tag = event.getTag();
/*  265 */     if (type == 0) {
/*  266 */       tagAdded(tag);
/*  267 */     } else if (type == 2) {
/*  268 */       tagRemoved(tag);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void tagAdded(Tag tag)
/*      */   {
/*  276 */     TagFeatureProperties tfp = (TagFeatureProperties)tag;
/*      */     
/*  278 */     TagFeatureProperties.TagProperty prop = tfp.getProperty("constraint");
/*      */     
/*  280 */     if (prop != null)
/*      */     {
/*  282 */       prop.addListener(new TagFeatureProperties.TagPropertyListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void propertyChanged(TagFeatureProperties.TagProperty property)
/*      */         {
/*      */ 
/*  289 */           TagPropertyConstraintHandler.this.handleProperty(property);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void propertySync(TagFeatureProperties.TagProperty property) {}
/*  298 */       });
/*  299 */       handleProperty(prop);
/*      */     }
/*      */     
/*  302 */     tag.addTagListener(new TagListener()
/*      */     {
/*      */       public void taggableSync(Tag tag) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void taggableRemoved(Tag tag, Taggable tagged)
/*      */       {
/*  316 */         TagPropertyConstraintHandler.this.apply((org.gudy.azureus2.core3.download.DownloadManager)tagged, tag, true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  324 */       public void taggableAdded(Tag tag, Taggable tagged) { TagPropertyConstraintHandler.this.apply((org.gudy.azureus2.core3.download.DownloadManager)tagged, tag, true); } }, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkTimer()
/*      */   {
/*  334 */     if (this.constrained_tags.size() > 0)
/*      */     {
/*  336 */       if (this.timer == null)
/*      */       {
/*  338 */         this.timer = SimpleTimer.addPeriodicEvent("tag:constraint:timer", 30000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  348 */             TagPropertyConstraintHandler.this.apply_history.clear();
/*      */             
/*  350 */             TagPropertyConstraintHandler.this.apply();
/*      */           }
/*      */           
/*  353 */         });
/*  354 */         AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void azureusCoreRunning(AzureusCore core)
/*      */           {
/*      */ 
/*  361 */             synchronized (TagPropertyConstraintHandler.this.constrained_tags)
/*      */             {
/*  363 */               if (TagPropertyConstraintHandler.this.timer != null)
/*      */               {
/*  365 */                 TagPropertyConstraintHandler.this.azureus_core.getPluginManager().getDefaultPluginInterface().getDownloadManager().getGlobalDownloadEventNotifier().addListener(TagPropertyConstraintHandler.this);
/*      */                 
/*  367 */                 TagPropertyConstraintHandler.this.dm_listener_added = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*  374 */     else if (this.timer != null)
/*      */     {
/*  376 */       this.timer.cancel();
/*      */       
/*  378 */       this.timer = null;
/*      */       
/*  380 */       if (this.dm_listener_added)
/*      */       {
/*  382 */         this.azureus_core.getPluginManager().getDefaultPluginInterface().getDownloadManager().getGlobalDownloadEventNotifier().removeListener(this);
/*      */       }
/*      */       
/*  385 */       this.apply_history.clear();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkFreqLimUpdates()
/*      */   {
/*  392 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  398 */         synchronized (TagPropertyConstraintHandler.this.freq_lim_pending)
/*      */         {
/*  400 */           for (Iterator i$ = TagPropertyConstraintHandler.this.freq_lim_pending.entrySet().iterator(); i$.hasNext();) { entry = (Map.Entry)i$.next();
/*      */             
/*  402 */             for (TagPropertyConstraintHandler.TagConstraint con : (List)entry.getValue())
/*      */             {
/*  404 */               TagPropertyConstraintHandler.TagConstraint.access$600(con, (org.gudy.azureus2.core3.download.DownloadManager)entry.getKey());
/*      */             }
/*      */           }
/*      */           Map.Entry<org.gudy.azureus2.core3.download.DownloadManager, List<TagPropertyConstraintHandler.TagConstraint>> entry;
/*  408 */           TagPropertyConstraintHandler.this.freq_lim_pending.clear();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stateChanged(Download download, int old_state, int new_state)
/*      */   {
/*  420 */     List<TagConstraint> interesting = new ArrayList();
/*      */     
/*  422 */     synchronized (this.constrained_tags)
/*      */     {
/*  424 */       if (!this.initialised)
/*      */       {
/*  426 */         return;
/*      */       }
/*      */       
/*  429 */       for (TagConstraint tc : this.constrained_tags.values())
/*      */       {
/*  431 */         if (tc.dependOnDownloadState())
/*      */         {
/*  433 */           interesting.add(tc);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  438 */     if (interesting.size() > 0)
/*      */     {
/*  440 */       org.gudy.azureus2.core3.download.DownloadManager dm = PluginCoreUtils.unwrap(download);
/*      */       
/*  442 */       synchronized (this.freq_lim_pending)
/*      */       {
/*  444 */         this.freq_lim_pending.put(dm, interesting);
/*      */       }
/*      */       
/*  447 */       this.freq_lim_dispatcher.dispatch();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void positionChanged(Download download, int oldPosition, int newPosition) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void tagRemoved(Tag tag)
/*      */   {
/*  463 */     synchronized (this.constrained_tags)
/*      */     {
/*  465 */       if (this.constrained_tags.containsKey(tag))
/*      */       {
/*  467 */         this.constrained_tags.remove(tag);
/*      */         
/*  469 */         checkTimer();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean isStopping()
/*      */   {
/*  477 */     return this.stopping;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void handleProperty(TagFeatureProperties.TagProperty property)
/*      */   {
/*  484 */     Tag tag = property.getTag();
/*      */     
/*  486 */     synchronized (this.constrained_tags)
/*      */     {
/*  488 */       String[] value = property.getStringList();
/*      */       
/*      */       String options;
/*      */       String constraint;
/*      */       String options;
/*  493 */       if (value == null)
/*      */       {
/*  495 */         String constraint = "";
/*  496 */         options = "";
/*      */       }
/*      */       else
/*      */       {
/*  500 */         constraint = (value.length > 0) && (value[0] != null) ? value[0].trim() : "";
/*  501 */         options = (value.length > 1) && (value[1] != null) ? value[1].trim() : "";
/*      */       }
/*      */       
/*  504 */       if (constraint.length() == 0)
/*      */       {
/*  506 */         if (this.constrained_tags.containsKey(tag))
/*      */         {
/*  508 */           this.constrained_tags.remove(tag);
/*      */         }
/*      */       }
/*      */       else {
/*  512 */         TagConstraint con = (TagConstraint)this.constrained_tags.get(tag);
/*      */         
/*  514 */         if ((con != null) && (con.getConstraint().equals(constraint)) && (con.getOptions().equals(options)))
/*      */         {
/*  516 */           return;
/*      */         }
/*      */         
/*  519 */         con = new TagConstraint(this, tag, constraint, options, null);
/*      */         
/*  521 */         this.constrained_tags.put(tag, con);
/*      */         
/*  523 */         if (this.initialised)
/*      */         {
/*  525 */           apply(con);
/*      */         }
/*      */       }
/*      */       
/*  529 */       checkTimer();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void apply(final org.gudy.azureus2.core3.download.DownloadManager dm, Tag related_tag, boolean auto)
/*      */   {
/*  539 */     if (dm.isDestroyed())
/*      */     {
/*  541 */       return;
/*      */     }
/*      */     
/*  544 */     synchronized (this.constrained_tags)
/*      */     {
/*  546 */       if ((this.constrained_tags.size() == 0) || (!this.initialised))
/*      */       {
/*  548 */         return;
/*      */       }
/*      */       
/*  551 */       if ((auto) && (!this.initial_assignment_complete))
/*      */       {
/*  553 */         return;
/*      */       }
/*      */     }
/*      */     
/*  557 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*      */         List<TagPropertyConstraintHandler.TagConstraint> cons;
/*      */         
/*      */ 
/*      */ 
/*  565 */         synchronized (TagPropertyConstraintHandler.this.constrained_tags)
/*      */         {
/*  567 */           cons = new ArrayList(TagPropertyConstraintHandler.this.constrained_tags.values());
/*      */         }
/*      */         
/*  570 */         for (TagPropertyConstraintHandler.TagConstraint con : cons)
/*      */         {
/*  572 */           TagPropertyConstraintHandler.TagConstraint.access$600(con, dm);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void apply(final List<org.gudy.azureus2.core3.download.DownloadManager> dms, final boolean initial_assignment)
/*      */   {
/*  583 */     synchronized (this.constrained_tags)
/*      */     {
/*  585 */       if ((this.constrained_tags.size() == 0) || (!this.initialised))
/*      */       {
/*  587 */         return;
/*      */       }
/*      */     }
/*      */     
/*  591 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */       public void runSupport()
/*      */       {
/*      */         List<TagPropertyConstraintHandler.TagConstraint> cons;
/*      */         
/*      */ 
/*      */ 
/*  599 */         synchronized (TagPropertyConstraintHandler.this.constrained_tags)
/*      */         {
/*  601 */           cons = new ArrayList(TagPropertyConstraintHandler.this.constrained_tags.values());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  606 */         for (TagPropertyConstraintHandler.TagConstraint con : cons)
/*      */         {
/*  608 */           TagPropertyConstraintHandler.TagConstraint.access$700(con, dms);
/*      */         }
/*      */         
/*  611 */         if (initial_assignment)
/*      */         {
/*  613 */           synchronized (TagPropertyConstraintHandler.this.constrained_tags)
/*      */           {
/*  615 */             TagPropertyConstraintHandler.this.initial_assignment_complete = true;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  620 */           for (TagPropertyConstraintHandler.TagConstraint con : cons)
/*      */           {
/*  622 */             TagPropertyConstraintHandler.TagConstraint.access$700(con, dms);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void apply(final TagConstraint constraint)
/*      */   {
/*  633 */     synchronized (this.constrained_tags)
/*      */     {
/*  635 */       if (!this.initialised)
/*      */       {
/*  637 */         return;
/*      */       }
/*      */     }
/*      */     
/*  641 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  647 */         List<org.gudy.azureus2.core3.download.DownloadManager> dms = TagPropertyConstraintHandler.this.azureus_core.getGlobalManager().getDownloadManagers();
/*      */         
/*  649 */         TagPropertyConstraintHandler.TagConstraint.access$700(constraint, dms);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void apply()
/*      */   {
/*  657 */     synchronized (this.constrained_tags)
/*      */     {
/*  659 */       if ((this.constrained_tags.size() == 0) || (!this.initialised))
/*      */       {
/*  661 */         return;
/*      */       }
/*      */     }
/*      */     
/*  665 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  671 */         List<org.gudy.azureus2.core3.download.DownloadManager> dms = TagPropertyConstraintHandler.this.azureus_core.getGlobalManager().getDownloadManagers();
/*      */         
/*      */         List<TagPropertyConstraintHandler.TagConstraint> cons;
/*      */         
/*  675 */         synchronized (TagPropertyConstraintHandler.this.constrained_tags)
/*      */         {
/*  677 */           cons = new ArrayList(TagPropertyConstraintHandler.this.constrained_tags.values());
/*      */         }
/*      */         
/*  680 */         for (TagPropertyConstraintHandler.TagConstraint con : cons)
/*      */         {
/*  682 */           TagPropertyConstraintHandler.TagConstraint.access$700(con, dms);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private TagPropertyConstraintHandler.TagConstraint.ConstraintExpr compileConstraint(String expr)
/*      */   {
/*  692 */     return new TagConstraint(this, null, expr, null, null).expr;
/*      */   }
/*      */   
/*      */ 
/*      */   private static class TagConstraint
/*      */   {
/*      */     private final TagPropertyConstraintHandler handler;
/*      */     
/*      */     private final Tag tag;
/*      */     
/*      */     private final String constraint;
/*      */     private final boolean auto_add;
/*      */     private final boolean auto_remove;
/*      */     private final ConstraintExpr expr;
/*      */     private boolean depends_on_download_state;
/*      */     private static final int FT_HAS_TAG = 1;
/*      */     private static final int FT_IS_PRIVATE = 2;
/*      */     private static final int FT_GE = 3;
/*      */     private static final int FT_GT = 4;
/*      */     private static final int FT_LE = 5;
/*      */     private static final int FT_LT = 6;
/*      */     
/*      */     private TagConstraint(TagPropertyConstraintHandler _handler, Tag _tag, String _constraint, String options)
/*      */     {
/*  716 */       this.handler = _handler;
/*  717 */       this.tag = _tag;
/*  718 */       this.constraint = _constraint;
/*      */       
/*  720 */       if (options == null)
/*      */       {
/*  722 */         this.auto_add = true;
/*  723 */         this.auto_remove = true;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  728 */         this.auto_add = (!options.contains("am=2;"));
/*  729 */         this.auto_remove = (!options.contains("am=1;"));
/*      */       }
/*      */       
/*  732 */       ConstraintExpr compiled_expr = null;
/*      */       try
/*      */       {
/*  735 */         compiled_expr = compileStart(this.constraint, new HashMap());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  739 */         Debug.out("Invalid constraint: " + this.constraint + " - " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */       finally
/*      */       {
/*  743 */         this.expr = compiled_expr;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean dependOnDownloadState()
/*      */     {
/*  750 */       return this.depends_on_download_state;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private ConstraintExpr compileStart(String str, Map<String, ConstraintExpr> context)
/*      */     {
/*  758 */       str = str.trim();
/*      */       
/*  760 */       if (str.equalsIgnoreCase("true"))
/*      */       {
/*  762 */         return new ConstraintExprTrue(null);
/*      */       }
/*      */       
/*  765 */       char[] chars = str.toCharArray();
/*      */       
/*  767 */       boolean in_quote = false;
/*      */       
/*  769 */       int level = 0;
/*  770 */       int bracket_start = 0;
/*      */       
/*  772 */       StringBuilder result = new StringBuilder(str.length());
/*      */       
/*  774 */       for (int i = 0; i < chars.length; i++)
/*      */       {
/*  776 */         char c = chars[i];
/*      */         
/*  778 */         if (c == '"')
/*      */         {
/*  780 */           if ((i == 0) || (chars[(i - 1)] != '\\'))
/*      */           {
/*  782 */             in_quote = !in_quote;
/*      */           }
/*      */         }
/*      */         
/*  786 */         if (!in_quote)
/*      */         {
/*  788 */           if (c == '(')
/*      */           {
/*  790 */             level++;
/*      */             
/*  792 */             if (level == 1)
/*      */             {
/*  794 */               bracket_start = i + 1;
/*      */             }
/*  796 */           } else if (c == ')')
/*      */           {
/*  798 */             level--;
/*      */             
/*  800 */             if (level == 0)
/*      */             {
/*  802 */               String bracket_text = new String(chars, bracket_start, i - bracket_start).trim();
/*      */               
/*  804 */               if ((result.length() > 0) && (Character.isLetterOrDigit(result.charAt(result.length() - 1))))
/*      */               {
/*      */ 
/*      */ 
/*  808 */                 String key = "{" + context.size() + "}";
/*      */                 
/*  810 */                 context.put(key, new ConstraintExprParams(bracket_text, null));
/*      */                 
/*  812 */                 result.append("(").append(key).append(")");
/*      */               }
/*      */               else
/*      */               {
/*  816 */                 ConstraintExpr sub_expr = compileStart(bracket_text, context);
/*      */                 
/*  818 */                 String key = "{" + context.size() + "}";
/*      */                 
/*  820 */                 context.put(key, sub_expr);
/*      */                 
/*  822 */                 result.append(key);
/*      */               }
/*      */             }
/*  825 */           } else if (level == 0)
/*      */           {
/*  827 */             if (!Character.isWhitespace(c))
/*      */             {
/*  829 */               result.append(c);
/*      */             }
/*      */           }
/*  832 */         } else if (level == 0)
/*      */         {
/*  834 */           result.append(c);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  839 */       if (level != 0)
/*      */       {
/*  841 */         throw new RuntimeException("Unmatched '(' in \"" + str + "\"");
/*      */       }
/*      */       
/*  844 */       if (in_quote)
/*      */       {
/*  846 */         throw new RuntimeException("Unmatched '\"' in \"" + str + "\"");
/*      */       }
/*      */       
/*  849 */       return compileBasic(result.toString(), context);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private ConstraintExpr compileBasic(String str, Map<String, ConstraintExpr> context)
/*      */     {
/*  857 */       if (str.startsWith("{"))
/*      */       {
/*  859 */         return (ConstraintExpr)context.get(str);
/*      */       }
/*  861 */       if (str.contains("||"))
/*      */       {
/*  863 */         String[] bits = str.split("\\|\\|");
/*      */         
/*  865 */         return new ConstraintExprOr(compile(bits, context), null);
/*      */       }
/*  867 */       if (str.contains("&&"))
/*      */       {
/*  869 */         String[] bits = str.split("&&");
/*      */         
/*  871 */         return new ConstraintExprAnd(compile(bits, context), null);
/*      */       }
/*  873 */       if (str.contains("^"))
/*      */       {
/*  875 */         String[] bits = str.split("\\^");
/*      */         
/*  877 */         return new ConstraintExprXor(compile(bits, context), null);
/*      */       }
/*  879 */       if (str.startsWith("!"))
/*      */       {
/*  881 */         return new ConstraintExprNot(compileBasic(str.substring(1).trim(), context), null);
/*      */       }
/*      */       
/*      */ 
/*  885 */       int pos = str.indexOf('(');
/*      */       
/*  887 */       if ((pos > 0) && (str.endsWith(")")))
/*      */       {
/*  889 */         String func = str.substring(0, pos);
/*      */         
/*  891 */         String key = str.substring(pos + 1, str.length() - 1).trim();
/*      */         
/*  893 */         ConstraintExprParams params = (ConstraintExprParams)context.get(key);
/*      */         
/*  895 */         return new ConstraintExprFunction(func, params, null);
/*      */       }
/*      */       
/*      */ 
/*  899 */       throw new RuntimeException("Unsupported construct: " + str);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private ConstraintExpr[] compile(String[] bits, Map<String, ConstraintExpr> context)
/*      */     {
/*  909 */       ConstraintExpr[] res = new ConstraintExpr[bits.length];
/*      */       
/*  911 */       for (int i = 0; i < bits.length; i++)
/*      */       {
/*  913 */         res[i] = compileBasic(bits[i].trim(), context);
/*      */       }
/*      */       
/*  916 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getConstraint()
/*      */     {
/*  922 */       return this.constraint;
/*      */     }
/*      */     
/*      */ 
/*      */     private String getOptions()
/*      */     {
/*  928 */       if (this.auto_add)
/*  929 */         return "am=1;";
/*  930 */       if (this.auto_remove) {
/*  931 */         return "am=2;";
/*      */       }
/*  933 */       return "am=0;";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void apply(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */     {
/*  941 */       if ((dm.isDestroyed()) || (!dm.isPersistent()))
/*      */       {
/*  943 */         return;
/*      */       }
/*      */       
/*  946 */       if (this.expr == null)
/*      */       {
/*  948 */         return;
/*      */       }
/*      */       
/*  951 */       if (this.handler.isStopping())
/*      */       {
/*  953 */         return;
/*      */       }
/*      */       
/*  956 */       if (!TagPropertyConstraintHandler.canProcess(this, dm))
/*      */       {
/*  958 */         return;
/*      */       }
/*      */       
/*  961 */       Set<Taggable> existing = this.tag.getTagged();
/*      */       
/*  963 */       if (testConstraint(dm))
/*      */       {
/*  965 */         if (this.auto_add)
/*      */         {
/*  967 */           if (!existing.contains(dm))
/*      */           {
/*  969 */             if (canAddTaggable(dm))
/*      */             {
/*  971 */               if (this.handler.isStopping())
/*      */               {
/*  973 */                 return;
/*      */               }
/*      */               
/*  976 */               this.tag.addTaggable(dm);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*  982 */       else if (this.auto_remove)
/*      */       {
/*  984 */         if (existing.contains(dm))
/*      */         {
/*  986 */           if (this.handler.isStopping())
/*      */           {
/*  988 */             return;
/*      */           }
/*      */           
/*  991 */           this.tag.removeTaggable(dm);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void apply(List<org.gudy.azureus2.core3.download.DownloadManager> dms)
/*      */     {
/* 1001 */       if (this.expr == null)
/*      */       {
/* 1003 */         return;
/*      */       }
/*      */       
/* 1006 */       if (this.handler.isStopping())
/*      */       {
/* 1008 */         return;
/*      */       }
/*      */       
/* 1011 */       if (!TagPropertyConstraintHandler.canProcess(this, dms))
/*      */       {
/* 1013 */         return;
/*      */       }
/*      */       
/* 1016 */       Set<Taggable> existing = this.tag.getTagged();
/*      */       
/* 1018 */       for (org.gudy.azureus2.core3.download.DownloadManager dm : dms)
/*      */       {
/* 1020 */         if ((!dm.isDestroyed()) && (dm.isPersistent()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1025 */           if (testConstraint(dm))
/*      */           {
/* 1027 */             if (this.auto_add)
/*      */             {
/* 1029 */               if (!existing.contains(dm))
/*      */               {
/* 1031 */                 if (canAddTaggable(dm))
/*      */                 {
/* 1033 */                   if (this.handler.isStopping())
/*      */                   {
/* 1035 */                     return;
/*      */                   }
/*      */                   
/* 1038 */                   this.tag.addTaggable(dm);
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */           }
/* 1044 */           else if (this.auto_remove)
/*      */           {
/* 1046 */             if (existing.contains(dm))
/*      */             {
/* 1048 */               if (this.handler.isStopping())
/*      */               {
/* 1050 */                 return;
/*      */               }
/*      */               
/* 1053 */               this.tag.removeTaggable(dm);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean canAddTaggable(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */     {
/* 1066 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 1068 */       Map<org.gudy.azureus2.core3.download.DownloadManager, Long> recent_dms = (Map)this.handler.apply_history.get(this.tag);
/*      */       
/* 1070 */       if (recent_dms != null)
/*      */       {
/* 1072 */         Long time = (Long)recent_dms.get(dm);
/*      */         
/* 1074 */         if ((time != null) && (now - time.longValue() < 1000L))
/*      */         {
/* 1076 */           System.out.println("Not applying constraint as too recently actioned: " + dm.getDisplayName() + "/" + this.tag.getTagName(true));
/*      */           
/* 1078 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 1082 */       if (recent_dms == null)
/*      */       {
/* 1084 */         recent_dms = new HashMap();
/*      */         
/* 1086 */         this.handler.apply_history.put(this.tag, recent_dms);
/*      */       }
/*      */       
/* 1089 */       recent_dms.put(dm, Long.valueOf(now));
/*      */       
/* 1091 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private boolean testConstraint(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */     {
/* 1098 */       List<Tag> dm_tags = this.handler.tag_manager.getTagsForTaggable(dm);
/*      */       
/* 1100 */       return this.expr.eval(dm, dm_tags);
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
/*      */     private static class ConstraintExprTrue
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1124 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1130 */         return "true";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private static class ConstraintExprParams
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       private final String value;
/*      */       
/*      */ 
/*      */       private ConstraintExprParams(String _value)
/*      */       {
/* 1144 */         this.value = _value.trim();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1152 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */       public Object[] getValues()
/*      */       {
/* 1158 */         if (this.value.length() == 0)
/*      */         {
/* 1160 */           return new String[0];
/*      */         }
/* 1162 */         if (!this.value.contains(","))
/*      */         {
/* 1164 */           return new Object[] { this.value };
/*      */         }
/*      */         
/*      */ 
/* 1168 */         char[] chars = this.value.toCharArray();
/*      */         
/* 1170 */         boolean in_quote = false;
/*      */         
/* 1172 */         List<String> params = new ArrayList(16);
/*      */         
/* 1174 */         StringBuilder current_param = new StringBuilder(this.value.length());
/*      */         
/* 1176 */         for (int i = 0; i < chars.length; i++)
/*      */         {
/* 1178 */           char c = chars[i];
/*      */           
/* 1180 */           if (c == '"')
/*      */           {
/* 1182 */             if ((i == 0) || (chars[(i - 1)] != '\\'))
/*      */             {
/* 1184 */               in_quote = !in_quote;
/*      */             }
/*      */           }
/*      */           
/* 1188 */           if ((c == ',') && (!in_quote))
/*      */           {
/* 1190 */             params.add(current_param.toString());
/*      */             
/* 1192 */             current_param.setLength(0);
/*      */ 
/*      */ 
/*      */           }
/* 1196 */           else if ((in_quote) || (!Character.isWhitespace(c)))
/*      */           {
/* 1198 */             current_param.append(c);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1203 */         params.add(current_param.toString());
/*      */         
/* 1205 */         return params.toArray(new Object[params.size()]);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1212 */         return this.value;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private static class ConstraintExprNot
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       private final TagPropertyConstraintHandler.TagConstraint.ConstraintExpr expr;
/*      */       
/*      */ 
/*      */       private ConstraintExprNot(TagPropertyConstraintHandler.TagConstraint.ConstraintExpr e)
/*      */       {
/* 1226 */         this.expr = e;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1234 */         return !this.expr.eval(dm, tags);
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1240 */         return "!(" + this.expr.getString() + ")";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private static class ConstraintExprOr
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       private final TagPropertyConstraintHandler.TagConstraint.ConstraintExpr[] exprs;
/*      */       
/*      */ 
/*      */       private ConstraintExprOr(TagPropertyConstraintHandler.TagConstraint.ConstraintExpr[] _exprs)
/*      */       {
/* 1254 */         this.exprs = _exprs;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1262 */         for (TagPropertyConstraintHandler.TagConstraint.ConstraintExpr expr : this.exprs)
/*      */         {
/* 1264 */           if (expr.eval(dm, tags))
/*      */           {
/* 1266 */             return true;
/*      */           }
/*      */         }
/*      */         
/* 1270 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1276 */         String res = "";
/*      */         
/* 1278 */         for (int i = 0; i < this.exprs.length; i++)
/*      */         {
/* 1280 */           res = res + (i == 0 ? "" : "||") + this.exprs[i].getString();
/*      */         }
/*      */         
/* 1283 */         return "(" + res + ")";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private static class ConstraintExprAnd
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       private final TagPropertyConstraintHandler.TagConstraint.ConstraintExpr[] exprs;
/*      */       
/*      */ 
/*      */       private ConstraintExprAnd(TagPropertyConstraintHandler.TagConstraint.ConstraintExpr[] _exprs)
/*      */       {
/* 1297 */         this.exprs = _exprs;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1305 */         for (TagPropertyConstraintHandler.TagConstraint.ConstraintExpr expr : this.exprs)
/*      */         {
/* 1307 */           if (!expr.eval(dm, tags))
/*      */           {
/* 1309 */             return false;
/*      */           }
/*      */         }
/*      */         
/* 1313 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1319 */         String res = "";
/*      */         
/* 1321 */         for (int i = 0; i < this.exprs.length; i++)
/*      */         {
/* 1323 */           res = res + (i == 0 ? "" : "&&") + this.exprs[i].getString();
/*      */         }
/*      */         
/* 1326 */         return "(" + res + ")";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private static class ConstraintExprXor
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       private final TagPropertyConstraintHandler.TagConstraint.ConstraintExpr[] exprs;
/*      */       
/*      */ 
/*      */       private ConstraintExprXor(TagPropertyConstraintHandler.TagConstraint.ConstraintExpr[] _exprs)
/*      */       {
/* 1340 */         this.exprs = _exprs;
/*      */         
/* 1342 */         if (this.exprs.length < 2)
/*      */         {
/* 1344 */           throw new RuntimeException("Two or more arguments required for ^");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1353 */         boolean res = this.exprs[0].eval(dm, tags);
/*      */         
/* 1355 */         for (int i = 1; i < this.exprs.length; i++)
/*      */         {
/* 1357 */           res ^= this.exprs[i].eval(dm, tags);
/*      */         }
/*      */         
/* 1360 */         return res;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getString()
/*      */       {
/* 1366 */         String res = "";
/*      */         
/* 1368 */         for (int i = 0; i < this.exprs.length; i++)
/*      */         {
/* 1370 */           res = res + (i == 0 ? "" : "^") + this.exprs[i].getString();
/*      */         }
/*      */         
/* 1373 */         return "(" + res + ")";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private static final int FT_EQ = 7;
/*      */     
/*      */     private static final int FT_NEQ = 8;
/*      */     
/*      */     private static final int FT_CONTAINS = 9;
/*      */     
/*      */     private static final int FT_MATCHES = 10;
/*      */     
/*      */     private static final int FT_HAS_NET = 11;
/*      */     
/*      */     private static final int FT_IS_COMPLETE = 12;
/*      */     
/*      */     private static final int FT_CAN_ARCHIVE = 13;
/*      */     
/*      */     private static final int FT_IS_FORCE_START = 14;
/*      */     
/*      */     private static final int FT_JAVASCRIPT = 15;
/*      */     
/*      */     private static final int FT_IS_CHECKING = 16;
/*      */     
/*      */     private static final int FT_IS_STOPPED = 17;
/*      */     private static final int FT_IS_PAUSED = 18;
/*      */     private static final int FT_IS_ERROR = 19;
/* 1401 */     static final Map<String, Integer> keyword_map = new HashMap();
/*      */     private static final int KW_SHARE_RATIO = 0;
/*      */     private static final int KW_AGE = 1;
/*      */     private static final int KW_PERCENT = 2;
/*      */     private static final int KW_DOWNLOADING_FOR = 3;
/*      */     private static final int KW_SEEDING_FOR = 4;
/*      */     private static final int KW_SWARM_MERGE = 5;
/*      */     private static final int KW_LAST_ACTIVE = 6;
/*      */     private static final int KW_SEED_COUNT = 7;
/*      */     private static final int KW_PEER_COUNT = 8;
/*      */     private static final int KW_SEED_PEER_RATIO = 9;
/*      */     private static final int KW_RESUME_IN = 10;
/*      */     private static final int KW_MIN_OF_HOUR = 11;
/*      */     private static final int KW_HOUR_OF_DAY = 12;
/*      */     private static final int KW_DAY_OF_WEEK = 13;
/*      */     private static final int KW_TAG_AGE = 14;
/*      */     
/*      */     static
/*      */     {
/* 1420 */       keyword_map.put("shareratio", Integer.valueOf(0));
/* 1421 */       keyword_map.put("share_ratio", Integer.valueOf(0));
/* 1422 */       keyword_map.put("age", Integer.valueOf(1));
/* 1423 */       keyword_map.put("percent", Integer.valueOf(2));
/* 1424 */       keyword_map.put("downloadingfor", Integer.valueOf(3));
/* 1425 */       keyword_map.put("downloading_for", Integer.valueOf(3));
/* 1426 */       keyword_map.put("seedingfor", Integer.valueOf(4));
/* 1427 */       keyword_map.put("seeding_for", Integer.valueOf(4));
/* 1428 */       keyword_map.put("swarmmergebytes", Integer.valueOf(5));
/* 1429 */       keyword_map.put("swarm_merge_bytes", Integer.valueOf(5));
/* 1430 */       keyword_map.put("lastactive", Integer.valueOf(6));
/* 1431 */       keyword_map.put("last_active", Integer.valueOf(6));
/* 1432 */       keyword_map.put("seedcount", Integer.valueOf(7));
/* 1433 */       keyword_map.put("seed_count", Integer.valueOf(7));
/* 1434 */       keyword_map.put("peercount", Integer.valueOf(8));
/* 1435 */       keyword_map.put("peer_count", Integer.valueOf(8));
/* 1436 */       keyword_map.put("seedpeerratio", Integer.valueOf(9));
/* 1437 */       keyword_map.put("seed_peer_ratio", Integer.valueOf(9));
/* 1438 */       keyword_map.put("resumein", Integer.valueOf(10));
/* 1439 */       keyword_map.put("resume_in", Integer.valueOf(10));
/*      */       
/* 1441 */       keyword_map.put("minofhour", Integer.valueOf(11));
/* 1442 */       keyword_map.put("min_of_hour", Integer.valueOf(11));
/* 1443 */       keyword_map.put("hourofday", Integer.valueOf(12));
/* 1444 */       keyword_map.put("hour_of_day", Integer.valueOf(12));
/* 1445 */       keyword_map.put("dayofweek", Integer.valueOf(13));
/* 1446 */       keyword_map.put("day_of_week", Integer.valueOf(13));
/* 1447 */       keyword_map.put("tagage", Integer.valueOf(14));
/* 1448 */       keyword_map.put("tag_age", Integer.valueOf(14));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private class ConstraintExprFunction
/*      */       implements TagPropertyConstraintHandler.TagConstraint.ConstraintExpr
/*      */     {
/*      */       private final String func_name;
/*      */       
/*      */ 
/*      */       private final TagPropertyConstraintHandler.TagConstraint.ConstraintExprParams params_expr;
/*      */       
/*      */       private final Object[] params;
/*      */       
/*      */       private final int fn_type;
/*      */       
/*      */ 
/*      */       private ConstraintExprFunction(String _func_name, TagPropertyConstraintHandler.TagConstraint.ConstraintExprParams _params)
/*      */       {
/* 1468 */         this.func_name = _func_name;
/* 1469 */         this.params_expr = _params;
/*      */         
/* 1471 */         this.params = _params.getValues();
/*      */         
/* 1473 */         boolean params_ok = false;
/*      */         
/* 1475 */         if (this.func_name.equals("hasTag"))
/*      */         {
/* 1477 */           this.fn_type = 1;
/*      */           
/* 1479 */           params_ok = (this.params.length == 1) && (getStringLiteral(this.params, 0));
/*      */         }
/* 1481 */         else if (this.func_name.equals("hasNet"))
/*      */         {
/* 1483 */           this.fn_type = 11;
/*      */           
/* 1485 */           params_ok = (this.params.length == 1) && (getStringLiteral(this.params, 0));
/*      */           
/* 1487 */           if (params_ok)
/*      */           {
/* 1489 */             this.params[0] = AENetworkClassifier.internalise((String)this.params[0]);
/*      */             
/* 1491 */             params_ok = this.params[0] != null;
/*      */           }
/* 1493 */         } else if (this.func_name.equals("isPrivate"))
/*      */         {
/* 1495 */           this.fn_type = 2;
/*      */           
/* 1497 */           params_ok = this.params.length == 0;
/*      */         }
/* 1499 */         else if (this.func_name.equals("isForceStart"))
/*      */         {
/* 1501 */           this.fn_type = 14;
/*      */           
/* 1503 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */           
/* 1505 */           params_ok = this.params.length == 0;
/*      */         }
/* 1507 */         else if (this.func_name.equals("isChecking"))
/*      */         {
/* 1509 */           this.fn_type = 16;
/*      */           
/* 1511 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */           
/* 1513 */           params_ok = this.params.length == 0;
/*      */         }
/* 1515 */         else if (this.func_name.equals("isComplete"))
/*      */         {
/* 1517 */           this.fn_type = 12;
/*      */           
/* 1519 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */           
/* 1521 */           params_ok = this.params.length == 0;
/*      */         }
/* 1523 */         else if (this.func_name.equals("isStopped"))
/*      */         {
/* 1525 */           this.fn_type = 17;
/*      */           
/* 1527 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */           
/* 1529 */           params_ok = this.params.length == 0;
/*      */         }
/* 1531 */         else if (this.func_name.equals("isError"))
/*      */         {
/* 1533 */           this.fn_type = 19;
/*      */           
/* 1535 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */           
/* 1537 */           params_ok = this.params.length == 0;
/*      */         }
/* 1539 */         else if (this.func_name.equals("isPaused"))
/*      */         {
/* 1541 */           this.fn_type = 18;
/*      */           
/* 1543 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */           
/* 1545 */           params_ok = this.params.length == 0;
/*      */         }
/* 1547 */         else if (this.func_name.equals("canArchive"))
/*      */         {
/* 1549 */           this.fn_type = 13;
/*      */           
/* 1551 */           params_ok = this.params.length == 0;
/*      */         }
/* 1553 */         else if (this.func_name.equals("isGE"))
/*      */         {
/* 1555 */           this.fn_type = 3;
/*      */           
/* 1557 */           params_ok = this.params.length == 2;
/*      */         }
/* 1559 */         else if (this.func_name.equals("isGT"))
/*      */         {
/* 1561 */           this.fn_type = 4;
/*      */           
/* 1563 */           params_ok = this.params.length == 2;
/*      */         }
/* 1565 */         else if (this.func_name.equals("isLE"))
/*      */         {
/* 1567 */           this.fn_type = 5;
/*      */           
/* 1569 */           params_ok = this.params.length == 2;
/*      */         }
/* 1571 */         else if (this.func_name.equals("isLT"))
/*      */         {
/* 1573 */           this.fn_type = 6;
/*      */           
/* 1575 */           params_ok = this.params.length == 2;
/*      */         }
/* 1577 */         else if (this.func_name.equals("isEQ"))
/*      */         {
/* 1579 */           this.fn_type = 7;
/*      */           
/* 1581 */           params_ok = this.params.length == 2;
/*      */         }
/* 1583 */         else if (this.func_name.equals("isNEQ"))
/*      */         {
/* 1585 */           this.fn_type = 8;
/*      */           
/* 1587 */           params_ok = this.params.length == 2;
/*      */         }
/* 1589 */         else if (this.func_name.equals("contains"))
/*      */         {
/* 1591 */           this.fn_type = 9;
/*      */           
/* 1593 */           params_ok = this.params.length == 2;
/*      */         }
/* 1595 */         else if (this.func_name.equals("matches"))
/*      */         {
/* 1597 */           this.fn_type = 10;
/*      */           
/* 1599 */           params_ok = (this.params.length == 2) && (getStringLiteral(this.params, 1));
/*      */         }
/* 1601 */         else if (this.func_name.equals("javascript"))
/*      */         {
/* 1603 */           this.fn_type = 15;
/*      */           
/* 1605 */           params_ok = (this.params.length == 1) && (getStringLiteral(this.params, 0));
/*      */           
/* 1607 */           TagPropertyConstraintHandler.TagConstraint.this.depends_on_download_state = true;
/*      */         }
/*      */         else
/*      */         {
/* 1611 */           throw new RuntimeException("Unsupported function '" + this.func_name + "'");
/*      */         }
/*      */         
/* 1614 */         if (!params_ok)
/*      */         {
/* 1616 */           throw new RuntimeException("Invalid parameters for function '" + this.func_name + "': " + this.params_expr.getString());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean eval(org.gudy.azureus2.core3.download.DownloadManager dm, List<Tag> tags)
/*      */       {
/* 1626 */         switch (this.fn_type)
/*      */         {
/*      */         case 1: 
/* 1629 */           String tag_name = (String)this.params[0];
/*      */           
/* 1631 */           for (Tag t : tags)
/*      */           {
/* 1633 */             if (t.getTagName(true).equals(tag_name))
/*      */             {
/* 1635 */               return true;
/*      */             }
/*      */           }
/*      */           
/* 1639 */           return false;
/*      */         
/*      */ 
/*      */         case 11: 
/* 1643 */           String net_name = (String)this.params[0];
/*      */           
/* 1645 */           if (net_name != null)
/*      */           {
/* 1647 */             String[] nets = dm.getDownloadState().getNetworks();
/*      */             
/* 1649 */             if (nets != null)
/*      */             {
/* 1651 */               for (String net : nets)
/*      */               {
/* 1653 */                 if (net == net_name)
/*      */                 {
/* 1655 */                   return true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1661 */           return false;
/*      */         
/*      */ 
/*      */         case 2: 
/* 1665 */           TOTorrent t = dm.getTorrent();
/*      */           
/* 1667 */           return (t != null) && (t.getPrivate());
/*      */         
/*      */ 
/*      */         case 14: 
/* 1671 */           return dm.isForceStart();
/*      */         
/*      */ 
/*      */         case 16: 
/* 1675 */           int state = dm.getState();
/*      */           
/* 1677 */           if (state == 30)
/*      */           {
/* 1679 */             return true;
/*      */           }
/* 1681 */           if (state == 60)
/*      */           {
/* 1683 */             DiskManager disk_manager = dm.getDiskManager();
/*      */             
/* 1685 */             if (disk_manager != null)
/*      */             {
/* 1687 */               return disk_manager.getCompleteRecheckStatus() != -1;
/*      */             }
/*      */           }
/*      */           
/* 1691 */           return false;
/*      */         
/*      */ 
/*      */         case 12: 
/* 1695 */           return dm.isDownloadComplete(false);
/*      */         
/*      */ 
/*      */         case 17: 
/* 1699 */           int state = dm.getState();
/*      */           
/* 1701 */           return (state == 70) && (!dm.isPaused());
/*      */         
/*      */ 
/*      */         case 19: 
/* 1705 */           int state = dm.getState();
/*      */           
/* 1707 */           return state == 100;
/*      */         
/*      */ 
/*      */         case 18: 
/* 1711 */           return dm.isPaused();
/*      */         
/*      */ 
/*      */         case 13: 
/* 1715 */           Download dl = PluginCoreUtils.wrap(dm);
/*      */           
/* 1717 */           return (dl != null) && (dl.canStubbify());
/*      */         
/*      */ 
/*      */         case 3: 
/*      */         case 4: 
/*      */         case 5: 
/*      */         case 6: 
/*      */         case 7: 
/*      */         case 8: 
/* 1726 */           Number n1 = getNumeric(dm, this.params, 0);
/* 1727 */           Number n2 = getNumeric(dm, this.params, 1);
/*      */           
/* 1729 */           switch (this.fn_type)
/*      */           {
/*      */           case 3: 
/* 1732 */             return n1.doubleValue() >= n2.doubleValue();
/*      */           case 4: 
/* 1734 */             return n1.doubleValue() > n2.doubleValue();
/*      */           case 5: 
/* 1736 */             return n1.doubleValue() <= n2.doubleValue();
/*      */           case 6: 
/* 1738 */             return n1.doubleValue() < n2.doubleValue();
/*      */           case 7: 
/* 1740 */             return n1.doubleValue() == n2.doubleValue();
/*      */           case 8: 
/* 1742 */             return n1.doubleValue() != n2.doubleValue();
/*      */           }
/*      */           
/* 1745 */           return false;
/*      */         
/*      */ 
/*      */         case 9: 
/* 1749 */           String s1 = getString(dm, this.params, 0);
/* 1750 */           String s2 = getString(dm, this.params, 1);
/*      */           
/* 1752 */           return s1.contains(s2);
/*      */         
/*      */ 
/*      */         case 10: 
/* 1756 */           String s1 = getString(dm, this.params, 0);
/*      */           
/* 1758 */           if (this.params[1] == null)
/*      */           {
/* 1760 */             return false;
/*      */           }
/* 1762 */           if ((this.params[1] instanceof Pattern))
/*      */           {
/* 1764 */             return ((Pattern)this.params[1]).matcher(s1).find();
/*      */           }
/*      */           
/*      */           try
/*      */           {
/* 1769 */             Pattern p = Pattern.compile((String)this.params[1], 2);
/*      */             
/* 1771 */             this.params[1] = p;
/*      */             
/* 1773 */             return p.matcher(s1).find();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1777 */             Debug.out("Invalid constraint pattern: " + this.params[1]);
/*      */             
/* 1779 */             this.params[1] = null;
/*      */             
/*      */ 
/*      */ 
/* 1783 */             return false;
/*      */           }
/*      */         
/*      */         case 15: 
/* 1787 */           Object result = TagPropertyConstraintHandler.this.tag_manager.evalScript(TagPropertyConstraintHandler.TagConstraint.this.tag, "javascript( " + (String)this.params[0] + ")", dm, "inTag");
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1794 */           if ((result instanceof Boolean))
/*      */           {
/* 1796 */             return ((Boolean)result).booleanValue();
/*      */           }
/*      */           
/* 1799 */           return false;
/*      */         }
/*      */         
/*      */         
/* 1803 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private boolean getStringLiteral(Object[] args, int index)
/*      */       {
/* 1811 */         Object _arg = args[index];
/*      */         
/* 1813 */         if ((_arg instanceof String))
/*      */         {
/* 1815 */           String arg = (String)_arg;
/*      */           
/* 1817 */           if ((arg.startsWith("\"")) && (arg.endsWith("\"")))
/*      */           {
/* 1819 */             args[index] = arg.substring(1, arg.length() - 1);
/*      */             
/* 1821 */             return true;
/*      */           }
/*      */         }
/*      */         
/* 1825 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private String getString(org.gudy.azureus2.core3.download.DownloadManager dm, Object[] args, int index)
/*      */       {
/* 1834 */         String str = (String)args[index];
/*      */         
/* 1836 */         if ((str.startsWith("\"")) && (str.endsWith("\"")))
/*      */         {
/* 1838 */           return str.substring(1, str.length() - 1);
/*      */         }
/* 1840 */         if (str.equals("name"))
/*      */         {
/* 1842 */           return dm.getDisplayName();
/*      */         }
/*      */         
/*      */ 
/* 1846 */         Debug.out("Invalid constraint string: " + str);
/*      */         
/* 1848 */         String result = "\"\"";
/*      */         
/* 1850 */         args[index] = result;
/*      */         
/* 1852 */         return result;
/*      */       }
/*      */       
/*      */       /* Error */
/*      */       private Number getNumeric(org.gudy.azureus2.core3.download.DownloadManager dm, Object[] args, int index)
/*      */       {
/*      */         // Byte code:
/*      */         //   0: aload_2
/*      */         //   1: iload_3
/*      */         //   2: aaload
/*      */         //   3: astore 4
/*      */         //   5: aload 4
/*      */         //   7: instanceof 278
/*      */         //   10: ifeq +9 -> 19
/*      */         //   13: aload 4
/*      */         //   15: checkcast 278	java/lang/Number
/*      */         //   18: areturn
/*      */         //   19: aload 4
/*      */         //   21: checkcast 281	java/lang/String
/*      */         //   24: astore 5
/*      */         //   26: iconst_0
/*      */         //   27: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   30: astore 6
/*      */         //   32: aload 5
/*      */         //   34: iconst_0
/*      */         //   35: invokevirtual 484	java/lang/String:charAt	(I)C
/*      */         //   38: invokestatic 471	java/lang/Character:isDigit	(C)Z
/*      */         //   41: ifeq +53 -> 94
/*      */         //   44: aload 5
/*      */         //   46: ldc 11
/*      */         //   48: invokevirtual 485	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */         //   51: ifeq +16 -> 67
/*      */         //   54: aload 5
/*      */         //   56: invokestatic 474	java/lang/Float:parseFloat	(Ljava/lang/String;)F
/*      */         //   59: invokestatic 473	java/lang/Float:valueOf	(F)Ljava/lang/Float;
/*      */         //   62: astore 6
/*      */         //   64: goto +13 -> 77
/*      */         //   67: aload 5
/*      */         //   69: invokestatic 478	java/lang/Long:parseLong	(Ljava/lang/String;)J
/*      */         //   72: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   75: astore 6
/*      */         //   77: aload 6
/*      */         //   79: astore 7
/*      */         //   81: aload 6
/*      */         //   83: ifnull +8 -> 91
/*      */         //   86: aload_2
/*      */         //   87: iload_3
/*      */         //   88: aload 6
/*      */         //   90: aastore
/*      */         //   91: aload 7
/*      */         //   93: areturn
/*      */         //   94: getstatic 452	com/aelitis/azureus/core/tag/impl/TagPropertyConstraintHandler$TagConstraint:keyword_map	Ljava/util/Map;
/*      */         //   97: aload 5
/*      */         //   99: getstatic 458	java/util/Locale:US	Ljava/util/Locale;
/*      */         //   102: invokevirtual 490	java/lang/String:toLowerCase	(Ljava/util/Locale;)Ljava/lang/String;
/*      */         //   105: invokeinterface 511 2 0
/*      */         //   110: checkcast 275	java/lang/Integer
/*      */         //   113: astore 7
/*      */         //   115: aload 7
/*      */         //   117: ifnonnull +43 -> 160
/*      */         //   120: new 282	java/lang/StringBuilder
/*      */         //   123: dup
/*      */         //   124: invokespecial 491	java/lang/StringBuilder:<init>	()V
/*      */         //   127: ldc 12
/*      */         //   129: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */         //   132: aload 5
/*      */         //   134: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */         //   137: invokevirtual 492	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */         //   140: invokestatic 503	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */         //   143: aload 6
/*      */         //   145: astore 8
/*      */         //   147: aload 6
/*      */         //   149: ifnull +8 -> 157
/*      */         //   152: aload_2
/*      */         //   153: iload_3
/*      */         //   154: aload 6
/*      */         //   156: aastore
/*      */         //   157: aload 8
/*      */         //   159: areturn
/*      */         //   160: aload 7
/*      */         //   162: invokevirtual 475	java/lang/Integer:intValue	()I
/*      */         //   165: tableswitch	default:+1050->1215, 0:+75->240, 1:+189->354, 2:+145->310, 3:+261->426, 4:+293->458, 5:+648->813, 6:+325->490, 7:+682->847, 8:+754->919, 9:+826->991, 10:+403->568, 11:+480->645, 12:+536->701, 13:+592->757, 14:+964->1129
/*      */         //   240: aconst_null
/*      */         //   241: astore 6
/*      */         //   243: aload_1
/*      */         //   244: invokeinterface 522 1 0
/*      */         //   249: invokeinterface 529 1 0
/*      */         //   254: istore 8
/*      */         //   256: iload 8
/*      */         //   258: iconst_m1
/*      */         //   259: if_icmpne +23 -> 282
/*      */         //   262: ldc 1
/*      */         //   264: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   267: astore 9
/*      */         //   269: aload 6
/*      */         //   271: ifnull +8 -> 279
/*      */         //   274: aload_2
/*      */         //   275: iload_3
/*      */         //   276: aload 6
/*      */         //   278: aastore
/*      */         //   279: aload 9
/*      */         //   281: areturn
/*      */         //   282: new 274	java/lang/Float
/*      */         //   285: dup
/*      */         //   286: iload 8
/*      */         //   288: i2f
/*      */         //   289: ldc 3
/*      */         //   291: fdiv
/*      */         //   292: invokespecial 472	java/lang/Float:<init>	(F)V
/*      */         //   295: astore 9
/*      */         //   297: aload 6
/*      */         //   299: ifnull +8 -> 307
/*      */         //   302: aload_2
/*      */         //   303: iload_3
/*      */         //   304: aload 6
/*      */         //   306: aastore
/*      */         //   307: aload 9
/*      */         //   309: areturn
/*      */         //   310: aconst_null
/*      */         //   311: astore 6
/*      */         //   313: aload_1
/*      */         //   314: invokeinterface 522 1 0
/*      */         //   319: invokeinterface 528 1 0
/*      */         //   324: istore 8
/*      */         //   326: new 274	java/lang/Float
/*      */         //   329: dup
/*      */         //   330: iload 8
/*      */         //   332: i2f
/*      */         //   333: ldc 2
/*      */         //   335: fdiv
/*      */         //   336: invokespecial 472	java/lang/Float:<init>	(F)V
/*      */         //   339: astore 9
/*      */         //   341: aload 6
/*      */         //   343: ifnull +8 -> 351
/*      */         //   346: aload_2
/*      */         //   347: iload_3
/*      */         //   348: aload 6
/*      */         //   350: aastore
/*      */         //   351: aload 9
/*      */         //   353: areturn
/*      */         //   354: aconst_null
/*      */         //   355: astore 6
/*      */         //   357: aload_1
/*      */         //   358: invokeinterface 521 1 0
/*      */         //   363: ldc 42
/*      */         //   365: invokeinterface 527 2 0
/*      */         //   370: lstore 8
/*      */         //   372: lload 8
/*      */         //   374: lconst_0
/*      */         //   375: lcmp
/*      */         //   376: ifgt +22 -> 398
/*      */         //   379: iconst_0
/*      */         //   380: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   383: astore 10
/*      */         //   385: aload 6
/*      */         //   387: ifnull +8 -> 395
/*      */         //   390: aload_2
/*      */         //   391: iload_3
/*      */         //   392: aload 6
/*      */         //   394: aastore
/*      */         //   395: aload 10
/*      */         //   397: areturn
/*      */         //   398: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   401: lload 8
/*      */         //   403: lsub
/*      */         //   404: ldc2_w 258
/*      */         //   407: ldiv
/*      */         //   408: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   411: astore 10
/*      */         //   413: aload 6
/*      */         //   415: ifnull +8 -> 423
/*      */         //   418: aload_2
/*      */         //   419: iload_3
/*      */         //   420: aload 6
/*      */         //   422: aastore
/*      */         //   423: aload 10
/*      */         //   425: areturn
/*      */         //   426: aconst_null
/*      */         //   427: astore 6
/*      */         //   429: aload_1
/*      */         //   430: invokeinterface 522 1 0
/*      */         //   435: invokeinterface 530 1 0
/*      */         //   440: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   443: astore 8
/*      */         //   445: aload 6
/*      */         //   447: ifnull +8 -> 455
/*      */         //   450: aload_2
/*      */         //   451: iload_3
/*      */         //   452: aload 6
/*      */         //   454: aastore
/*      */         //   455: aload 8
/*      */         //   457: areturn
/*      */         //   458: aconst_null
/*      */         //   459: astore 6
/*      */         //   461: aload_1
/*      */         //   462: invokeinterface 522 1 0
/*      */         //   467: invokeinterface 531 1 0
/*      */         //   472: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   475: astore 8
/*      */         //   477: aload 6
/*      */         //   479: ifnull +8 -> 487
/*      */         //   482: aload_2
/*      */         //   483: iload_3
/*      */         //   484: aload 6
/*      */         //   486: aastore
/*      */         //   487: aload 8
/*      */         //   489: areturn
/*      */         //   490: aconst_null
/*      */         //   491: astore 6
/*      */         //   493: aload_1
/*      */         //   494: invokeinterface 521 1 0
/*      */         //   499: astore 8
/*      */         //   501: aload 8
/*      */         //   503: ldc 38
/*      */         //   505: invokeinterface 526 2 0
/*      */         //   510: lstore 9
/*      */         //   512: lload 9
/*      */         //   514: lconst_0
/*      */         //   515: lcmp
/*      */         //   516: ifgt +24 -> 540
/*      */         //   519: ldc2_w 260
/*      */         //   522: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   525: astore 11
/*      */         //   527: aload 6
/*      */         //   529: ifnull +8 -> 537
/*      */         //   532: aload_2
/*      */         //   533: iload_3
/*      */         //   534: aload 6
/*      */         //   536: aastore
/*      */         //   537: aload 11
/*      */         //   539: areturn
/*      */         //   540: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   543: lload 9
/*      */         //   545: lsub
/*      */         //   546: ldc2_w 258
/*      */         //   549: ldiv
/*      */         //   550: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   553: astore 11
/*      */         //   555: aload 6
/*      */         //   557: ifnull +8 -> 565
/*      */         //   560: aload_2
/*      */         //   561: iload_3
/*      */         //   562: aload 6
/*      */         //   564: aastore
/*      */         //   565: aload 11
/*      */         //   567: areturn
/*      */         //   568: aconst_null
/*      */         //   569: astore 6
/*      */         //   571: aload_1
/*      */         //   572: invokeinterface 515 1 0
/*      */         //   577: lstore 8
/*      */         //   579: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   582: lstore 10
/*      */         //   584: lload 8
/*      */         //   586: lconst_0
/*      */         //   587: lcmp
/*      */         //   588: ifle +11 -> 599
/*      */         //   591: lload 8
/*      */         //   593: lload 10
/*      */         //   595: lcmp
/*      */         //   596: ifgt +22 -> 618
/*      */         //   599: iconst_0
/*      */         //   600: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   603: astore 12
/*      */         //   605: aload 6
/*      */         //   607: ifnull +8 -> 615
/*      */         //   610: aload_2
/*      */         //   611: iload_3
/*      */         //   612: aload 6
/*      */         //   614: aastore
/*      */         //   615: aload 12
/*      */         //   617: areturn
/*      */         //   618: lload 8
/*      */         //   620: lload 10
/*      */         //   622: lsub
/*      */         //   623: ldc2_w 258
/*      */         //   626: ldiv
/*      */         //   627: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   630: astore 12
/*      */         //   632: aload 6
/*      */         //   634: ifnull +8 -> 642
/*      */         //   637: aload_2
/*      */         //   638: iload_3
/*      */         //   639: aload 6
/*      */         //   641: aastore
/*      */         //   642: aload 12
/*      */         //   644: areturn
/*      */         //   645: aconst_null
/*      */         //   646: astore 6
/*      */         //   648: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   651: lstore 8
/*      */         //   653: new 285	java/util/GregorianCalendar
/*      */         //   656: dup
/*      */         //   657: invokespecial 496	java/util/GregorianCalendar:<init>	()V
/*      */         //   660: astore 10
/*      */         //   662: aload 10
/*      */         //   664: new 284	java/util/Date
/*      */         //   667: dup
/*      */         //   668: lload 8
/*      */         //   670: invokespecial 495	java/util/Date:<init>	(J)V
/*      */         //   673: invokevirtual 498	java/util/GregorianCalendar:setTime	(Ljava/util/Date;)V
/*      */         //   676: aload 10
/*      */         //   678: bipush 12
/*      */         //   680: invokevirtual 497	java/util/GregorianCalendar:get	(I)I
/*      */         //   683: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   686: astore 11
/*      */         //   688: aload 6
/*      */         //   690: ifnull +8 -> 698
/*      */         //   693: aload_2
/*      */         //   694: iload_3
/*      */         //   695: aload 6
/*      */         //   697: aastore
/*      */         //   698: aload 11
/*      */         //   700: areturn
/*      */         //   701: aconst_null
/*      */         //   702: astore 6
/*      */         //   704: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   707: lstore 8
/*      */         //   709: new 285	java/util/GregorianCalendar
/*      */         //   712: dup
/*      */         //   713: invokespecial 496	java/util/GregorianCalendar:<init>	()V
/*      */         //   716: astore 10
/*      */         //   718: aload 10
/*      */         //   720: new 284	java/util/Date
/*      */         //   723: dup
/*      */         //   724: lload 8
/*      */         //   726: invokespecial 495	java/util/Date:<init>	(J)V
/*      */         //   729: invokevirtual 498	java/util/GregorianCalendar:setTime	(Ljava/util/Date;)V
/*      */         //   732: aload 10
/*      */         //   734: bipush 11
/*      */         //   736: invokevirtual 497	java/util/GregorianCalendar:get	(I)I
/*      */         //   739: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   742: astore 11
/*      */         //   744: aload 6
/*      */         //   746: ifnull +8 -> 754
/*      */         //   749: aload_2
/*      */         //   750: iload_3
/*      */         //   751: aload 6
/*      */         //   753: aastore
/*      */         //   754: aload 11
/*      */         //   756: areturn
/*      */         //   757: aconst_null
/*      */         //   758: astore 6
/*      */         //   760: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   763: lstore 8
/*      */         //   765: new 285	java/util/GregorianCalendar
/*      */         //   768: dup
/*      */         //   769: invokespecial 496	java/util/GregorianCalendar:<init>	()V
/*      */         //   772: astore 10
/*      */         //   774: aload 10
/*      */         //   776: new 284	java/util/Date
/*      */         //   779: dup
/*      */         //   780: lload 8
/*      */         //   782: invokespecial 495	java/util/Date:<init>	(J)V
/*      */         //   785: invokevirtual 498	java/util/GregorianCalendar:setTime	(Ljava/util/Date;)V
/*      */         //   788: aload 10
/*      */         //   790: bipush 7
/*      */         //   792: invokevirtual 497	java/util/GregorianCalendar:get	(I)I
/*      */         //   795: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   798: astore 11
/*      */         //   800: aload 6
/*      */         //   802: ifnull +8 -> 810
/*      */         //   805: aload_2
/*      */         //   806: iload_3
/*      */         //   807: aload 6
/*      */         //   809: aastore
/*      */         //   810: aload 11
/*      */         //   812: areturn
/*      */         //   813: aconst_null
/*      */         //   814: astore 6
/*      */         //   816: aload_1
/*      */         //   817: invokeinterface 521 1 0
/*      */         //   822: ldc 40
/*      */         //   824: invokeinterface 526 2 0
/*      */         //   829: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   832: astore 8
/*      */         //   834: aload 6
/*      */         //   836: ifnull +8 -> 844
/*      */         //   839: aload_2
/*      */         //   840: iload_3
/*      */         //   841: aload 6
/*      */         //   843: aastore
/*      */         //   844: aload 8
/*      */         //   846: areturn
/*      */         //   847: aconst_null
/*      */         //   848: astore 6
/*      */         //   850: aload_1
/*      */         //   851: invokeinterface 524 1 0
/*      */         //   856: astore 8
/*      */         //   858: aload_1
/*      */         //   859: invokeinterface 513 1 0
/*      */         //   864: istore 9
/*      */         //   866: aload 8
/*      */         //   868: ifnull +27 -> 895
/*      */         //   871: aload 8
/*      */         //   873: invokeinterface 535 1 0
/*      */         //   878: ifeq +17 -> 895
/*      */         //   881: iload 9
/*      */         //   883: aload 8
/*      */         //   885: invokeinterface 534 1 0
/*      */         //   890: invokestatic 479	java/lang/Math:max	(II)I
/*      */         //   893: istore 9
/*      */         //   895: iconst_0
/*      */         //   896: iload 9
/*      */         //   898: invokestatic 479	java/lang/Math:max	(II)I
/*      */         //   901: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   904: astore 10
/*      */         //   906: aload 6
/*      */         //   908: ifnull +8 -> 916
/*      */         //   911: aload_2
/*      */         //   912: iload_3
/*      */         //   913: aload 6
/*      */         //   915: aastore
/*      */         //   916: aload 10
/*      */         //   918: areturn
/*      */         //   919: aconst_null
/*      */         //   920: astore 6
/*      */         //   922: aload_1
/*      */         //   923: invokeinterface 524 1 0
/*      */         //   928: astore 8
/*      */         //   930: aload_1
/*      */         //   931: invokeinterface 513 1 0
/*      */         //   936: istore 9
/*      */         //   938: aload 8
/*      */         //   940: ifnull +27 -> 967
/*      */         //   943: aload 8
/*      */         //   945: invokeinterface 535 1 0
/*      */         //   950: ifeq +17 -> 967
/*      */         //   953: iload 9
/*      */         //   955: aload 8
/*      */         //   957: invokeinterface 533 1 0
/*      */         //   962: invokestatic 479	java/lang/Math:max	(II)I
/*      */         //   965: istore 9
/*      */         //   967: iconst_0
/*      */         //   968: iload 9
/*      */         //   970: invokestatic 479	java/lang/Math:max	(II)I
/*      */         //   973: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   976: astore 10
/*      */         //   978: aload 6
/*      */         //   980: ifnull +8 -> 988
/*      */         //   983: aload_2
/*      */         //   984: iload_3
/*      */         //   985: aload 6
/*      */         //   987: aastore
/*      */         //   988: aload 10
/*      */         //   990: areturn
/*      */         //   991: aconst_null
/*      */         //   992: astore 6
/*      */         //   994: aload_1
/*      */         //   995: invokeinterface 524 1 0
/*      */         //   1000: astore 8
/*      */         //   1002: aload_1
/*      */         //   1003: invokeinterface 513 1 0
/*      */         //   1008: istore 9
/*      */         //   1010: aload_1
/*      */         //   1011: invokeinterface 513 1 0
/*      */         //   1016: istore 10
/*      */         //   1018: aload 8
/*      */         //   1020: ifnull +41 -> 1061
/*      */         //   1023: aload 8
/*      */         //   1025: invokeinterface 535 1 0
/*      */         //   1030: ifeq +31 -> 1061
/*      */         //   1033: iload 9
/*      */         //   1035: aload 8
/*      */         //   1037: invokeinterface 534 1 0
/*      */         //   1042: invokestatic 479	java/lang/Math:max	(II)I
/*      */         //   1045: istore 9
/*      */         //   1047: iload 10
/*      */         //   1049: aload 8
/*      */         //   1051: invokeinterface 533 1 0
/*      */         //   1056: invokestatic 479	java/lang/Math:max	(II)I
/*      */         //   1059: istore 10
/*      */         //   1061: iload 10
/*      */         //   1063: iflt +8 -> 1071
/*      */         //   1066: iload 9
/*      */         //   1068: ifge +9 -> 1077
/*      */         //   1071: fconst_0
/*      */         //   1072: fstore 11
/*      */         //   1074: goto +35 -> 1109
/*      */         //   1077: iload 10
/*      */         //   1079: ifne +21 -> 1100
/*      */         //   1082: iload 9
/*      */         //   1084: ifne +9 -> 1093
/*      */         //   1087: fconst_0
/*      */         //   1088: fstore 11
/*      */         //   1090: goto +19 -> 1109
/*      */         //   1093: ldc 4
/*      */         //   1095: fstore 11
/*      */         //   1097: goto +12 -> 1109
/*      */         //   1100: iload 9
/*      */         //   1102: i2f
/*      */         //   1103: iload 10
/*      */         //   1105: i2f
/*      */         //   1106: fdiv
/*      */         //   1107: fstore 11
/*      */         //   1109: fload 11
/*      */         //   1111: invokestatic 473	java/lang/Float:valueOf	(F)Ljava/lang/Float;
/*      */         //   1114: astore 12
/*      */         //   1116: aload 6
/*      */         //   1118: ifnull +8 -> 1126
/*      */         //   1121: aload_2
/*      */         //   1122: iload_3
/*      */         //   1123: aload 6
/*      */         //   1125: aastore
/*      */         //   1126: aload 12
/*      */         //   1128: areturn
/*      */         //   1129: aconst_null
/*      */         //   1130: astore 6
/*      */         //   1132: aload_0
/*      */         //   1133: getfield 454	com/aelitis/azureus/core/tag/impl/TagPropertyConstraintHandler$TagConstraint$ConstraintExprFunction:this$0	Lcom/aelitis/azureus/core/tag/impl/TagPropertyConstraintHandler$TagConstraint;
/*      */         //   1136: invokestatic 462	com/aelitis/azureus/core/tag/impl/TagPropertyConstraintHandler$TagConstraint:access$3000	(Lcom/aelitis/azureus/core/tag/impl/TagPropertyConstraintHandler$TagConstraint;)Lcom/aelitis/azureus/core/tag/Tag;
/*      */         //   1139: aload_1
/*      */         //   1140: invokeinterface 506 2 0
/*      */         //   1145: lstore 8
/*      */         //   1147: lload 8
/*      */         //   1149: lconst_0
/*      */         //   1150: lcmp
/*      */         //   1151: ifgt +22 -> 1173
/*      */         //   1154: iconst_0
/*      */         //   1155: invokestatic 476	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
/*      */         //   1158: astore 10
/*      */         //   1160: aload 6
/*      */         //   1162: ifnull +8 -> 1170
/*      */         //   1165: aload_2
/*      */         //   1166: iload_3
/*      */         //   1167: aload 6
/*      */         //   1169: aastore
/*      */         //   1170: aload 10
/*      */         //   1172: areturn
/*      */         //   1173: invokestatic 504	org/gudy/azureus2/core3/util/SystemTime:getCurrentTime	()J
/*      */         //   1176: lload 8
/*      */         //   1178: lsub
/*      */         //   1179: ldc2_w 258
/*      */         //   1182: ldiv
/*      */         //   1183: lstore 10
/*      */         //   1185: lload 10
/*      */         //   1187: lconst_0
/*      */         //   1188: lcmp
/*      */         //   1189: ifge +6 -> 1195
/*      */         //   1192: lconst_0
/*      */         //   1193: lstore 10
/*      */         //   1195: lload 10
/*      */         //   1197: invokestatic 477	java/lang/Long:valueOf	(J)Ljava/lang/Long;
/*      */         //   1200: astore 12
/*      */         //   1202: aload 6
/*      */         //   1204: ifnull +8 -> 1212
/*      */         //   1207: aload_2
/*      */         //   1208: iload_3
/*      */         //   1209: aload 6
/*      */         //   1211: aastore
/*      */         //   1212: aload 12
/*      */         //   1214: areturn
/*      */         //   1215: new 282	java/lang/StringBuilder
/*      */         //   1218: dup
/*      */         //   1219: invokespecial 491	java/lang/StringBuilder:<init>	()V
/*      */         //   1222: ldc 12
/*      */         //   1224: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */         //   1227: aload 5
/*      */         //   1229: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */         //   1232: invokevirtual 492	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */         //   1235: invokestatic 503	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */         //   1238: aload 6
/*      */         //   1240: astore 8
/*      */         //   1242: aload 6
/*      */         //   1244: ifnull +8 -> 1252
/*      */         //   1247: aload_2
/*      */         //   1248: iload_3
/*      */         //   1249: aload 6
/*      */         //   1251: aastore
/*      */         //   1252: aload 8
/*      */         //   1254: areturn
/*      */         //   1255: astore 7
/*      */         //   1257: new 282	java/lang/StringBuilder
/*      */         //   1260: dup
/*      */         //   1261: invokespecial 491	java/lang/StringBuilder:<init>	()V
/*      */         //   1264: ldc 13
/*      */         //   1266: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */         //   1269: aload 5
/*      */         //   1271: invokevirtual 494	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */         //   1274: invokevirtual 492	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */         //   1277: invokestatic 503	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */         //   1280: aload 6
/*      */         //   1282: astore 8
/*      */         //   1284: aload 6
/*      */         //   1286: ifnull +8 -> 1294
/*      */         //   1289: aload_2
/*      */         //   1290: iload_3
/*      */         //   1291: aload 6
/*      */         //   1293: aastore
/*      */         //   1294: aload 8
/*      */         //   1296: areturn
/*      */         //   1297: astore 13
/*      */         //   1299: aload 6
/*      */         //   1301: ifnull +8 -> 1309
/*      */         //   1304: aload_2
/*      */         //   1305: iload_3
/*      */         //   1306: aload 6
/*      */         //   1308: aastore
/*      */         //   1309: aload 13
/*      */         //   1311: athrow
/*      */         // Line number table:
/*      */         //   Java source line #1862	-> byte code offset #0
/*      */         //   Java source line #1864	-> byte code offset #5
/*      */         //   Java source line #1866	-> byte code offset #13
/*      */         //   Java source line #1869	-> byte code offset #19
/*      */         //   Java source line #1871	-> byte code offset #26
/*      */         //   Java source line #1874	-> byte code offset #32
/*      */         //   Java source line #1876	-> byte code offset #44
/*      */         //   Java source line #1878	-> byte code offset #54
/*      */         //   Java source line #1882	-> byte code offset #67
/*      */         //   Java source line #1885	-> byte code offset #77
/*      */         //   Java source line #2127	-> byte code offset #81
/*      */         //   Java source line #2131	-> byte code offset #86
/*      */         //   Java source line #1888	-> byte code offset #94
/*      */         //   Java source line #1890	-> byte code offset #115
/*      */         //   Java source line #1892	-> byte code offset #120
/*      */         //   Java source line #1894	-> byte code offset #143
/*      */         //   Java source line #2127	-> byte code offset #147
/*      */         //   Java source line #2131	-> byte code offset #152
/*      */         //   Java source line #1897	-> byte code offset #160
/*      */         //   Java source line #1899	-> byte code offset #240
/*      */         //   Java source line #1901	-> byte code offset #243
/*      */         //   Java source line #1903	-> byte code offset #256
/*      */         //   Java source line #1905	-> byte code offset #262
/*      */         //   Java source line #2127	-> byte code offset #269
/*      */         //   Java source line #2131	-> byte code offset #274
/*      */         //   Java source line #1909	-> byte code offset #282
/*      */         //   Java source line #2127	-> byte code offset #297
/*      */         //   Java source line #2131	-> byte code offset #302
/*      */         //   Java source line #1914	-> byte code offset #310
/*      */         //   Java source line #1918	-> byte code offset #313
/*      */         //   Java source line #1920	-> byte code offset #326
/*      */         //   Java source line #2127	-> byte code offset #341
/*      */         //   Java source line #2131	-> byte code offset #346
/*      */         //   Java source line #1924	-> byte code offset #354
/*      */         //   Java source line #1926	-> byte code offset #357
/*      */         //   Java source line #1928	-> byte code offset #372
/*      */         //   Java source line #1930	-> byte code offset #379
/*      */         //   Java source line #2127	-> byte code offset #385
/*      */         //   Java source line #2131	-> byte code offset #390
/*      */         //   Java source line #1933	-> byte code offset #398
/*      */         //   Java source line #2127	-> byte code offset #413
/*      */         //   Java source line #2131	-> byte code offset #418
/*      */         //   Java source line #1937	-> byte code offset #426
/*      */         //   Java source line #1939	-> byte code offset #429
/*      */         //   Java source line #2127	-> byte code offset #445
/*      */         //   Java source line #2131	-> byte code offset #450
/*      */         //   Java source line #1943	-> byte code offset #458
/*      */         //   Java source line #1945	-> byte code offset #461
/*      */         //   Java source line #2127	-> byte code offset #477
/*      */         //   Java source line #2131	-> byte code offset #482
/*      */         //   Java source line #1949	-> byte code offset #490
/*      */         //   Java source line #1951	-> byte code offset #493
/*      */         //   Java source line #1953	-> byte code offset #501
/*      */         //   Java source line #1955	-> byte code offset #512
/*      */         //   Java source line #1957	-> byte code offset #519
/*      */         //   Java source line #2127	-> byte code offset #527
/*      */         //   Java source line #2131	-> byte code offset #532
/*      */         //   Java source line #1960	-> byte code offset #540
/*      */         //   Java source line #2127	-> byte code offset #555
/*      */         //   Java source line #2131	-> byte code offset #560
/*      */         //   Java source line #1964	-> byte code offset #568
/*      */         //   Java source line #1966	-> byte code offset #571
/*      */         //   Java source line #1968	-> byte code offset #579
/*      */         //   Java source line #1970	-> byte code offset #584
/*      */         //   Java source line #1972	-> byte code offset #599
/*      */         //   Java source line #2127	-> byte code offset #605
/*      */         //   Java source line #2131	-> byte code offset #610
/*      */         //   Java source line #1975	-> byte code offset #618
/*      */         //   Java source line #2127	-> byte code offset #632
/*      */         //   Java source line #2131	-> byte code offset #637
/*      */         //   Java source line #1979	-> byte code offset #645
/*      */         //   Java source line #1981	-> byte code offset #648
/*      */         //   Java source line #1983	-> byte code offset #653
/*      */         //   Java source line #1985	-> byte code offset #662
/*      */         //   Java source line #1987	-> byte code offset #676
/*      */         //   Java source line #2127	-> byte code offset #688
/*      */         //   Java source line #2131	-> byte code offset #693
/*      */         //   Java source line #1991	-> byte code offset #701
/*      */         //   Java source line #1993	-> byte code offset #704
/*      */         //   Java source line #1995	-> byte code offset #709
/*      */         //   Java source line #1997	-> byte code offset #718
/*      */         //   Java source line #1999	-> byte code offset #732
/*      */         //   Java source line #2127	-> byte code offset #744
/*      */         //   Java source line #2131	-> byte code offset #749
/*      */         //   Java source line #2003	-> byte code offset #757
/*      */         //   Java source line #2005	-> byte code offset #760
/*      */         //   Java source line #2007	-> byte code offset #765
/*      */         //   Java source line #2009	-> byte code offset #774
/*      */         //   Java source line #2011	-> byte code offset #788
/*      */         //   Java source line #2127	-> byte code offset #800
/*      */         //   Java source line #2131	-> byte code offset #805
/*      */         //   Java source line #2015	-> byte code offset #813
/*      */         //   Java source line #2017	-> byte code offset #816
/*      */         //   Java source line #2127	-> byte code offset #834
/*      */         //   Java source line #2131	-> byte code offset #839
/*      */         //   Java source line #2021	-> byte code offset #847
/*      */         //   Java source line #2023	-> byte code offset #850
/*      */         //   Java source line #2025	-> byte code offset #858
/*      */         //   Java source line #2027	-> byte code offset #866
/*      */         //   Java source line #2029	-> byte code offset #881
/*      */         //   Java source line #2032	-> byte code offset #895
/*      */         //   Java source line #2127	-> byte code offset #906
/*      */         //   Java source line #2131	-> byte code offset #911
/*      */         //   Java source line #2036	-> byte code offset #919
/*      */         //   Java source line #2038	-> byte code offset #922
/*      */         //   Java source line #2040	-> byte code offset #930
/*      */         //   Java source line #2042	-> byte code offset #938
/*      */         //   Java source line #2044	-> byte code offset #953
/*      */         //   Java source line #2047	-> byte code offset #967
/*      */         //   Java source line #2127	-> byte code offset #978
/*      */         //   Java source line #2131	-> byte code offset #983
/*      */         //   Java source line #2051	-> byte code offset #991
/*      */         //   Java source line #2053	-> byte code offset #994
/*      */         //   Java source line #2055	-> byte code offset #1002
/*      */         //   Java source line #2056	-> byte code offset #1010
/*      */         //   Java source line #2058	-> byte code offset #1018
/*      */         //   Java source line #2060	-> byte code offset #1033
/*      */         //   Java source line #2061	-> byte code offset #1047
/*      */         //   Java source line #2066	-> byte code offset #1061
/*      */         //   Java source line #2068	-> byte code offset #1071
/*      */         //   Java source line #2072	-> byte code offset #1077
/*      */         //   Java source line #2074	-> byte code offset #1082
/*      */         //   Java source line #2076	-> byte code offset #1087
/*      */         //   Java source line #2080	-> byte code offset #1093
/*      */         //   Java source line #2084	-> byte code offset #1100
/*      */         //   Java source line #2088	-> byte code offset #1109
/*      */         //   Java source line #2127	-> byte code offset #1116
/*      */         //   Java source line #2131	-> byte code offset #1121
/*      */         //   Java source line #2092	-> byte code offset #1129
/*      */         //   Java source line #2094	-> byte code offset #1132
/*      */         //   Java source line #2096	-> byte code offset #1147
/*      */         //   Java source line #2098	-> byte code offset #1154
/*      */         //   Java source line #2127	-> byte code offset #1160
/*      */         //   Java source line #2131	-> byte code offset #1165
/*      */         //   Java source line #2101	-> byte code offset #1173
/*      */         //   Java source line #2103	-> byte code offset #1185
/*      */         //   Java source line #2105	-> byte code offset #1192
/*      */         //   Java source line #2108	-> byte code offset #1195
/*      */         //   Java source line #2127	-> byte code offset #1202
/*      */         //   Java source line #2131	-> byte code offset #1207
/*      */         //   Java source line #2113	-> byte code offset #1215
/*      */         //   Java source line #2115	-> byte code offset #1238
/*      */         //   Java source line #2127	-> byte code offset #1242
/*      */         //   Java source line #2131	-> byte code offset #1247
/*      */         //   Java source line #2119	-> byte code offset #1255
/*      */         //   Java source line #2121	-> byte code offset #1257
/*      */         //   Java source line #2123	-> byte code offset #1280
/*      */         //   Java source line #2127	-> byte code offset #1284
/*      */         //   Java source line #2131	-> byte code offset #1289
/*      */         //   Java source line #2127	-> byte code offset #1297
/*      */         //   Java source line #2131	-> byte code offset #1304
/*      */         // Local variable table:
/*      */         //   start	length	slot	name	signature
/*      */         //   0	1312	0	this	ConstraintExprFunction
/*      */         //   0	1312	1	dm	org.gudy.azureus2.core3.download.DownloadManager
/*      */         //   0	1312	2	args	Object[]
/*      */         //   0	1312	3	index	int
/*      */         //   3	17	4	arg	Object
/*      */         //   24	1246	5	str	String
/*      */         //   30	1277	6	result	Number
/*      */         //   79	13	7	localNumber1	Number
/*      */         //   113	48	7	kw	Integer
/*      */         //   1255	3	7	e	Throwable
/*      */         //   145	13	8	localNumber2	Number
/*      */         //   254	33	8	sr	int
/*      */         //   324	7	8	percent	int
/*      */         //   370	118	8	added	long
/*      */         //   443	45	8	localLong1	Long
/*      */         //   499	3	8	dms	DownloadManagerState
/*      */         //   577	42	8	resume_millis	long
/*      */         //   651	18	8	now	long
/*      */         //   707	18	8	now	long
/*      */         //   763	82	8	now	long
/*      */         //   832	13	8	localLong2	Long
/*      */         //   856	28	8	response	org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse
/*      */         //   928	28	8	response	org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse
/*      */         //   1000	50	8	response	org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse
/*      */         //   1145	150	8	tag_added	long
/*      */         //   1240	55	8	localNumber3	Number
/*      */         //   267	85	9	localObject1	Object
/*      */         //   510	34	9	timestamp	long
/*      */         //   864	33	9	seeds	int
/*      */         //   936	33	9	peers	int
/*      */         //   1008	93	9	seeds	int
/*      */         //   383	41	10	localObject2	Object
/*      */         //   582	39	10	now	long
/*      */         //   660	17	10	cal	java.util.GregorianCalendar
/*      */         //   716	17	10	cal	java.util.GregorianCalendar
/*      */         //   772	217	10	cal	Object
/*      */         //   1016	155	10	peers	int
/*      */         //   1158	13	10	localInteger1	Integer
/*      */         //   1183	13	10	age	long
/*      */         //   525	286	11	localObject3	Object
/*      */         //   1072	3	11	ratio	float
/*      */         //   1088	3	11	ratio	float
/*      */         //   1095	3	11	ratio	float
/*      */         //   1107	3	11	ratio	float
/*      */         //   603	610	12	localObject4	Object
/*      */         //   1297	13	13	localObject5	Object
/*      */         // Exception table:
/*      */         //   from	to	target	type
/*      */         //   32	81	1255	java/lang/Throwable
/*      */         //   94	147	1255	java/lang/Throwable
/*      */         //   160	269	1255	java/lang/Throwable
/*      */         //   282	297	1255	java/lang/Throwable
/*      */         //   310	341	1255	java/lang/Throwable
/*      */         //   354	385	1255	java/lang/Throwable
/*      */         //   398	413	1255	java/lang/Throwable
/*      */         //   426	445	1255	java/lang/Throwable
/*      */         //   458	477	1255	java/lang/Throwable
/*      */         //   490	527	1255	java/lang/Throwable
/*      */         //   540	555	1255	java/lang/Throwable
/*      */         //   568	605	1255	java/lang/Throwable
/*      */         //   618	632	1255	java/lang/Throwable
/*      */         //   645	688	1255	java/lang/Throwable
/*      */         //   701	744	1255	java/lang/Throwable
/*      */         //   757	800	1255	java/lang/Throwable
/*      */         //   813	834	1255	java/lang/Throwable
/*      */         //   847	906	1255	java/lang/Throwable
/*      */         //   919	978	1255	java/lang/Throwable
/*      */         //   991	1116	1255	java/lang/Throwable
/*      */         //   1129	1160	1255	java/lang/Throwable
/*      */         //   1173	1202	1255	java/lang/Throwable
/*      */         //   1215	1242	1255	java/lang/Throwable
/*      */         //   32	81	1297	finally
/*      */         //   94	147	1297	finally
/*      */         //   160	269	1297	finally
/*      */         //   282	297	1297	finally
/*      */         //   310	341	1297	finally
/*      */         //   354	385	1297	finally
/*      */         //   398	413	1297	finally
/*      */         //   426	445	1297	finally
/*      */         //   458	477	1297	finally
/*      */         //   490	527	1297	finally
/*      */         //   540	555	1297	finally
/*      */         //   568	605	1297	finally
/*      */         //   618	632	1297	finally
/*      */         //   645	688	1297	finally
/*      */         //   701	744	1297	finally
/*      */         //   757	800	1297	finally
/*      */         //   813	834	1297	finally
/*      */         //   847	906	1297	finally
/*      */         //   919	978	1297	finally
/*      */         //   991	1116	1297	finally
/*      */         //   1129	1160	1297	finally
/*      */         //   1173	1202	1297	finally
/*      */         //   1215	1242	1297	finally
/*      */         //   1255	1284	1297	finally
/*      */         //   1297	1299	1297	finally
/*      */       }
/*      */       
/* 2139 */       public String getString() { return this.func_name + "(" + this.params_expr.getString() + ")"; }
/*      */     }
/*      */     
/*      */     private static abstract interface ConstraintExpr { public abstract boolean eval(org.gudy.azureus2.core3.download.DownloadManager paramDownloadManager, List<Tag> paramList);
/*      */       
/*      */       public abstract String getString();
/*      */     }
/*      */   }
/*      */   
/* 2148 */   public static void main(String[] args) { TagPropertyConstraintHandler handler = new TagPropertyConstraintHandler();
/*      */     
/*      */ 
/* 2151 */     System.out.println(handler.compileConstraint("isGE( shareratio, 1.5)").getString());
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagPropertyConstraintHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */