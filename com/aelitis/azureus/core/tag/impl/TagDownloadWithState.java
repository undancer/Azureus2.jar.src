/*      */ package com.aelitis.azureus.core.tag.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagDownload;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureProperties.TagProperty;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureRateLimit;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.download.Download;
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
/*      */ public class TagDownloadWithState
/*      */   extends TagWithState
/*      */   implements TagDownload
/*      */ {
/*      */   private int upload_rate_limit;
/*      */   private int download_rate_limit;
/*   68 */   private int upload_rate = -1;
/*   69 */   private int download_rate = -1;
/*      */   
/*      */   private int aggregate_sr;
/*      */   
/*      */   private long session_up;
/*      */   
/*      */   private long session_down;
/*      */   
/*      */   private long last_rate_update;
/*   78 */   final Object UPLOAD_PRIORITY_ADDED_KEY = new Object();
/*      */   
/*      */   private int upload_priority;
/*      */   
/*      */   private int min_share_ratio;
/*      */   
/*      */   private int max_share_ratio;
/*      */   private int max_share_ratio_action;
/*      */   private int max_aggregate_share_ratio;
/*      */   private int max_aggregate_share_ratio_action;
/*      */   private boolean max_aggregate_share_ratio_priority;
/*      */   private boolean supports_xcode;
/*      */   private boolean supports_file_location;
/*   91 */   final Object rate_lock = new Object();
/*      */   
/*   93 */   private final LimitedRateGroup upload_limiter = new LimitedRateGroup()
/*      */   {
/*      */ 
/*      */     public String getName()
/*      */     {
/*      */ 
/*   99 */       String str = "tag_up: " + TagDownloadWithState.this.getTagName(true);
/*      */       
/*  101 */       if (TagDownloadWithState.this.upload_rate_limit < 0)
/*      */       {
/*  103 */         str = str + ": disabled";
/*      */       }
/*      */       
/*  106 */       return str;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRateLimitBytesPerSecond()
/*      */     {
/*  112 */       int res = TagDownloadWithState.this.upload_rate_limit;
/*      */       
/*  114 */       if (res < 0)
/*      */       {
/*  116 */         res = 0;
/*      */       }
/*      */       
/*  119 */       return res;
/*      */     }
/*      */     
/*      */     public boolean isDisabled()
/*      */     {
/*  124 */       return TagDownloadWithState.this.upload_rate_limit < 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public void updateBytesUsed(int used)
/*      */     {
/*  130 */       TagDownloadWithState.access$114(TagDownloadWithState.this, used);
/*      */     }
/*      */   };
/*      */   
/*  134 */   private final LimitedRateGroup download_limiter = new LimitedRateGroup()
/*      */   {
/*      */ 
/*      */     public String getName()
/*      */     {
/*      */ 
/*  140 */       String str = "tag_down: " + TagDownloadWithState.this.getTagName(true);
/*      */       
/*  142 */       if (TagDownloadWithState.this.download_rate_limit < 0)
/*      */       {
/*  144 */         str = str + ": disabled";
/*      */       }
/*      */       
/*  147 */       return str;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getRateLimitBytesPerSecond()
/*      */     {
/*  153 */       int res = TagDownloadWithState.this.download_rate_limit;
/*      */       
/*  155 */       if (res < 0)
/*      */       {
/*  157 */         res = 0;
/*      */       }
/*      */       
/*  160 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isDisabled()
/*      */     {
/*  166 */       return TagDownloadWithState.this.download_rate_limit < 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void updateBytesUsed(int used)
/*      */     {
/*  173 */       TagDownloadWithState.access$314(TagDownloadWithState.this, used);
/*      */     }
/*      */   };
/*      */   
/*      */   private boolean do_rates;
/*      */   
/*      */   private boolean do_up;
/*      */   
/*      */   private boolean do_down;
/*      */   private boolean do_bytes;
/*      */   private int run_states;
/*  184 */   private static final AsyncDispatcher rs_async = new AsyncDispatcher(2000);
/*      */   
/*  186 */   private final TagFeatureProperties.TagProperty[] tag_properties = { createTagProperty("trackers", 1), createTagProperty("untagged", 2), createTagProperty("tracker_templates", 1), createTagProperty("constraint", 1) };
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
/*      */   public TagDownloadWithState(TagTypeBase tt, int tag_id, String name, boolean do_rates, boolean do_up, boolean do_down, boolean do_bytes, int run_states)
/*      */   {
/*  205 */     super(tt, tag_id, name);
/*      */     
/*  207 */     init(do_rates, do_up, do_down, do_bytes, run_states);
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
/*      */   protected TagDownloadWithState(TagTypeBase tt, int tag_id, Map details, boolean do_rates, boolean do_up, boolean do_down, boolean do_bytes, int run_states)
/*      */   {
/*  221 */     super(tt, tag_id, details);
/*      */     
/*  223 */     init(do_rates, do_up, do_down, do_bytes, run_states);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void init(boolean _do_rates, boolean _do_up, boolean _do_down, boolean _do_bytes, int _run_states)
/*      */   {
/*  234 */     this.do_rates = _do_rates;
/*  235 */     this.do_up = _do_up;
/*  236 */     this.do_down = _do_down;
/*  237 */     this.do_bytes = _do_bytes;
/*      */     
/*  239 */     this.run_states = _run_states;
/*      */     
/*  241 */     if (this.do_up)
/*      */     {
/*  243 */       setRateLimit(readLongAttribute("rl.up", Long.valueOf(0L)).intValue(), true);
/*      */     }
/*      */     
/*  246 */     if (this.do_down)
/*      */     {
/*  248 */       setRateLimit(readLongAttribute("rl.down", Long.valueOf(0L)).intValue(), false);
/*      */     }
/*      */     
/*  251 */     this.upload_priority = readLongAttribute("rl.uppri", Long.valueOf(0L)).intValue();
/*      */     
/*  253 */     this.min_share_ratio = readLongAttribute("rl.minsr", Long.valueOf(0L)).intValue();
/*  254 */     this.max_share_ratio = readLongAttribute("rl.maxsr", Long.valueOf(0L)).intValue();
/*  255 */     this.max_share_ratio_action = readLongAttribute("rl.maxsr.a", Long.valueOf(0L)).intValue();
/*      */     
/*  257 */     this.max_aggregate_share_ratio = readLongAttribute("rl.maxaggsr", Long.valueOf(0L)).intValue();
/*  258 */     this.max_aggregate_share_ratio_action = readLongAttribute("rl.maxaggsr.a", Long.valueOf(1L)).intValue();
/*  259 */     this.max_aggregate_share_ratio_priority = readBooleanAttribute("rl.maxaggsr.p", Boolean.valueOf(true)).booleanValue();
/*      */     
/*  261 */     addTagListener(new TagListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void taggableAdded(Tag tag, Taggable tagged)
/*      */       {
/*      */ 
/*      */ 
/*  269 */         DownloadManager manager = (DownloadManager)tagged;
/*      */         
/*  271 */         TagDownloadWithState.this.setRateLimit(manager, true);
/*      */         
/*  273 */         if (TagDownloadWithState.this.upload_priority > 0)
/*      */         {
/*  275 */           manager.updateAutoUploadPriority(TagDownloadWithState.this.UPLOAD_PRIORITY_ADDED_KEY, true);
/*      */         }
/*      */         
/*  278 */         if (TagDownloadWithState.this.min_share_ratio > 0)
/*      */         {
/*  280 */           updateMinShareRatio(manager, TagDownloadWithState.this.min_share_ratio);
/*      */         }
/*      */         
/*  283 */         if (TagDownloadWithState.this.max_share_ratio > 0)
/*      */         {
/*  285 */           updateMaxShareRatio(manager, TagDownloadWithState.this.max_share_ratio);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void taggableSync(Tag tag) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void taggableRemoved(Tag tag, Taggable tagged)
/*      */       {
/*  300 */         DownloadManager manager = (DownloadManager)tagged;
/*      */         
/*  302 */         TagDownloadWithState.this.setRateLimit(manager, false);
/*      */         
/*  304 */         if (TagDownloadWithState.this.upload_priority > 0)
/*      */         {
/*  306 */           manager.updateAutoUploadPriority(TagDownloadWithState.this.UPLOAD_PRIORITY_ADDED_KEY, false);
/*      */         }
/*      */         
/*  309 */         if (TagDownloadWithState.this.min_share_ratio > 0)
/*      */         {
/*  311 */           updateMinShareRatio(manager, 0);
/*      */         }
/*      */         
/*  314 */         if (TagDownloadWithState.this.max_share_ratio > 0)
/*      */         {
/*  316 */           updateMaxShareRatio(manager, 0);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private void updateMinShareRatio(DownloadManager manager, int sr)
/*      */       {
/*  325 */         List<Tag> dm_tags = TagDownloadWithState.this.getTagType().getTagsForTaggable(manager);
/*      */         
/*  327 */         for (Tag t : dm_tags)
/*      */         {
/*  329 */           if (t != TagDownloadWithState.this)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  334 */             if ((t instanceof TagFeatureRateLimit))
/*      */             {
/*  336 */               int o_sr = ((TagFeatureRateLimit)t).getTagMinShareRatio();
/*      */               
/*  338 */               if (o_sr > sr)
/*      */               {
/*  340 */                 sr = o_sr;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  345 */         manager.getDownloadState().setIntParameter("sr.min", sr);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       private void updateMaxShareRatio(DownloadManager manager, int sr)
/*      */       {
/*  353 */         List<Tag> dm_tags = TagDownloadWithState.this.getTagType().getTagsForTaggable(manager);
/*      */         
/*  355 */         for (Tag t : dm_tags)
/*      */         {
/*  357 */           if (t != TagDownloadWithState.this)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  362 */             if ((t instanceof TagFeatureRateLimit))
/*      */             {
/*  364 */               int o_sr = ((TagFeatureRateLimit)t).getTagMaxShareRatio();
/*      */               
/*  366 */               if (o_sr > sr)
/*      */               {
/*  368 */                 sr = o_sr;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  373 */         manager.getDownloadState().setIntParameter("sr.max", sr); } }, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeTag()
/*      */   {
/*  383 */     for (DownloadManager dm : getTaggedDownloads())
/*      */     {
/*  385 */       setRateLimit(dm, false);
/*      */       
/*  387 */       if (this.upload_priority > 0)
/*      */       {
/*  389 */         dm.updateAutoUploadPriority(this.UPLOAD_PRIORITY_ADDED_KEY, false);
/*      */       }
/*      */     }
/*      */     
/*  393 */     super.removeTag();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addTaggable(Taggable t)
/*      */   {
/*  401 */     if ((t instanceof DownloadManager))
/*      */     {
/*  403 */       final DownloadManager dm = (DownloadManager)t;
/*      */       
/*  405 */       if (!dm.isDestroyed())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  416 */         super.addTaggable(t);
/*      */         
/*  418 */         int actions = getSupportedActions();
/*      */         
/*  420 */         if (actions != 0)
/*      */         {
/*  422 */           if ((isActionEnabled(2)) || (isActionEnabled(128)))
/*      */           {
/*      */ 
/*  425 */             int dm_state = dm.getState();
/*      */             
/*  427 */             if ((dm_state == 70) || (dm_state == 100))
/*      */             {
/*      */ 
/*  430 */               rs_async.dispatch(new AERunnable()
/*      */               {
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/*  436 */                   if (TagDownloadWithState.this.isActionEnabled(2))
/*      */                   {
/*  438 */                     dm.setStateQueued();
/*      */                   }
/*      */                   else
/*      */                   {
/*  442 */                     dm.resume();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*  448 */           else if ((isActionEnabled(4)) || (isActionEnabled(64)))
/*      */           {
/*      */ 
/*  451 */             int dm_state = dm.getState();
/*      */             
/*  453 */             if ((dm_state != 70) && (dm_state != 65) && (dm_state != 100))
/*      */             {
/*      */ 
/*      */ 
/*  457 */               rs_async.dispatch(new AERunnable()
/*      */               {
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/*  463 */                   if (TagDownloadWithState.this.isActionEnabled(4))
/*      */                   {
/*  465 */                     dm.stopIt(70, false, false);
/*      */                   }
/*      */                   else
/*      */                   {
/*  469 */                     dm.pause();
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  475 */                   TagDownloadWithState.this.checkMaximumTaggables();
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  482 */           if (isActionEnabled(8))
/*      */           {
/*  484 */             rs_async.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*  490 */                 dm.setForceStart(true);
/*      */               }
/*      */               
/*      */             });
/*  494 */           } else if (isActionEnabled(16))
/*      */           {
/*  496 */             rs_async.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*  502 */                 dm.setForceStart(false);
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*  507 */           if (isActionEnabled(32))
/*      */           {
/*  509 */             final String script = getActionScript();
/*      */             
/*  511 */             if (script.length() > 0)
/*      */             {
/*  513 */               rs_async.dispatch(new AERunnable()
/*      */               {
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/*  519 */                   TagManagerImpl.getSingleton().evalScript(TagDownloadWithState.this, script, dm, "execAssign");
/*      */                 }
/*      */                 
/*      */ 
/*      */               });
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  533 */       Debug.out("Invalid Taggable added: " + t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTaggableTypes()
/*      */   {
/*  540 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */   public Set<DownloadManager> getTaggedDownloads()
/*      */   {
/*  546 */     return (Set)getTagged();
/*      */   }
/*      */   
/*  549 */   private final DownloadManagerPeerListener peer_listener = new DownloadManagerPeerListener()
/*      */   {
/*      */     public void peerManagerWillBeAdded(PEPeerManager manager) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void peerManagerAdded(PEPeerManager manager) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void peerManagerRemoved(PEPeerManager manager) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void peerAdded(PEPeer peer)
/*      */     {
/*  575 */       synchronized (TagDownloadWithState.this.rate_lock)
/*      */       {
/*  577 */         if (TagDownloadWithState.this.upload_rate_limit < 0)
/*      */         {
/*  579 */           peer.setUploadDisabled(this, true);
/*      */         }
/*      */         
/*  582 */         if (TagDownloadWithState.this.download_rate_limit < 0)
/*      */         {
/*  584 */           peer.setDownloadDisabled(this, true);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void peerRemoved(PEPeer peer) {}
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setRateLimit(DownloadManager manager, boolean added)
/*      */   {
/*  601 */     synchronized (this.rate_lock)
/*      */     {
/*  603 */       if (added)
/*      */       {
/*  605 */         if (manager.getUserData(this.rate_lock) == null)
/*      */         {
/*  607 */           manager.setUserData(this.rate_lock, "");
/*      */           
/*  609 */           manager.addPeerListener(this.peer_listener, true);
/*      */           
/*  611 */           manager.addRateLimiter(this.upload_limiter, true);
/*      */           
/*  613 */           manager.addRateLimiter(this.download_limiter, false);
/*      */         }
/*      */         
/*      */       }
/*  617 */       else if (manager.getUserData(this.rate_lock) != null)
/*      */       {
/*  619 */         manager.setUserData(this.rate_lock, null);
/*      */         
/*  621 */         manager.removeRateLimiter(this.upload_limiter, true);
/*      */         
/*  623 */         manager.removeRateLimiter(this.download_limiter, false);
/*      */         
/*  625 */         manager.removePeerListener(this.peer_listener);
/*      */         
/*  627 */         PEPeerManager pm = manager.getPeerManager();
/*      */         
/*  629 */         if (pm != null)
/*      */         {
/*  631 */           List<PEPeer> peers = pm.getPeers();
/*      */           
/*  633 */           if ((this.upload_rate_limit < 0) || (this.download_rate_limit < 0))
/*      */           {
/*  635 */             for (PEPeer peer : peers)
/*      */             {
/*  637 */               if (this.upload_rate_limit < 0)
/*      */               {
/*  639 */                 peer.setUploadDisabled(this.peer_listener, false);
/*      */               }
/*      */               
/*  642 */               if (this.download_rate_limit < 0)
/*      */               {
/*  644 */                 peer.setDownloadDisabled(this.peer_listener, false);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void setRateLimit(int limit, boolean is_up)
/*      */   {
/*  659 */     if (limit < 0)
/*      */     {
/*  661 */       limit = -1;
/*      */     }
/*      */     
/*  664 */     synchronized (this.rate_lock)
/*      */     {
/*  666 */       if (is_up)
/*      */       {
/*  668 */         if (limit == this.upload_rate_limit)
/*      */         {
/*  670 */           return;
/*      */         }
/*      */         
/*  673 */         if ((limit < 0) || (this.upload_rate_limit < 0))
/*      */         {
/*  675 */           Set<DownloadManager> downloads = getTaggedDownloads();
/*      */           
/*  677 */           for (DownloadManager dm : downloads)
/*      */           {
/*  679 */             PEPeerManager pm = dm.getPeerManager();
/*      */             
/*  681 */             if (pm != null)
/*      */             {
/*  683 */               List<PEPeer> peers = pm.getPeers();
/*      */               
/*  685 */               for (PEPeer peer : peers)
/*      */               {
/*  687 */                 peer.setUploadDisabled(this.peer_listener, limit < 0);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  693 */         this.upload_rate_limit = limit;
/*      */       }
/*      */       else
/*      */       {
/*  697 */         if (limit == this.download_rate_limit)
/*      */         {
/*  699 */           return;
/*      */         }
/*      */         
/*  702 */         if ((limit < 0) || (this.download_rate_limit < 0))
/*      */         {
/*  704 */           Set<DownloadManager> downloads = getTaggedDownloads();
/*      */           
/*  706 */           for (DownloadManager dm : downloads)
/*      */           {
/*  708 */             PEPeerManager pm = dm.getPeerManager();
/*      */             
/*  710 */             if (pm != null)
/*      */             {
/*  712 */               List<PEPeer> peers = pm.getPeers();
/*      */               
/*  714 */               for (PEPeer peer : peers)
/*      */               {
/*  716 */                 peer.setDownloadDisabled(this.peer_listener, limit < 0);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  722 */         this.download_rate_limit = limit;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsTagRates()
/*      */   {
/*  730 */     return this.do_rates;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsTagUploadLimit()
/*      */   {
/*  736 */     return this.do_up;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsTagDownloadLimit()
/*      */   {
/*  742 */     return this.do_down;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagUploadLimit()
/*      */   {
/*  748 */     return this.upload_rate_limit;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagUploadLimit(int bps)
/*      */   {
/*  755 */     if (this.upload_rate_limit == bps)
/*      */     {
/*  757 */       return;
/*      */     }
/*      */     
/*  760 */     if (!this.do_up)
/*      */     {
/*  762 */       Debug.out("Not supported");
/*      */       
/*  764 */       return;
/*      */     }
/*      */     
/*  767 */     setRateLimit(bps, true);
/*      */     
/*  769 */     writeLongAttribute("rl.up", this.upload_rate_limit);
/*      */     
/*  771 */     getTagType().fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagCurrentUploadRate()
/*      */   {
/*  777 */     updateStuff();
/*      */     
/*  779 */     return this.upload_rate;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagDownloadLimit()
/*      */   {
/*  785 */     return this.download_rate_limit;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagDownloadLimit(int bps)
/*      */   {
/*  792 */     if (this.download_rate_limit == bps)
/*      */     {
/*  794 */       return;
/*      */     }
/*      */     
/*  797 */     if (!this.do_down)
/*      */     {
/*  799 */       Debug.out("Not supported");
/*      */       
/*  801 */       return;
/*      */     }
/*      */     
/*  804 */     setRateLimit(bps, false);
/*      */     
/*  806 */     writeLongAttribute("rl.down", this.download_rate_limit);
/*      */     
/*  808 */     getTagType().fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagCurrentDownloadRate()
/*      */   {
/*  814 */     updateStuff();
/*      */     
/*  816 */     return this.download_rate;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagUploadPriority()
/*      */   {
/*  822 */     return this.upload_priority;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long[] getTagSessionUploadTotalCurrent()
/*      */   {
/*  829 */     if ((this.do_bytes) && (this.do_up))
/*      */     {
/*  831 */       return new long[] { this.session_up };
/*      */     }
/*      */     
/*      */ 
/*  835 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long[] getTagSessionDownloadTotalCurrent()
/*      */   {
/*  843 */     if ((this.do_bytes) && (this.do_down))
/*      */     {
/*  845 */       return new long[] { this.session_down };
/*      */     }
/*      */     
/*      */ 
/*  849 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTagUploadPriority(int priority)
/*      */   {
/*  857 */     if (priority < 0)
/*      */     {
/*  859 */       priority = 0;
/*      */     }
/*      */     
/*  862 */     if (priority == this.upload_priority)
/*      */     {
/*  864 */       return;
/*      */     }
/*      */     
/*  867 */     int old_up = this.upload_priority;
/*      */     
/*  869 */     this.upload_priority = priority;
/*      */     
/*  871 */     writeLongAttribute("rl.uppri", priority);
/*      */     
/*  873 */     if ((old_up == 0) || (priority == 0))
/*      */     {
/*  875 */       Set<DownloadManager> dms = getTaggedDownloads();
/*      */       
/*  877 */       for (DownloadManager dm : dms)
/*      */       {
/*  879 */         dm.updateAutoUploadPriority(this.UPLOAD_PRIORITY_ADDED_KEY, priority > 0);
/*      */       }
/*      */     }
/*      */     
/*  883 */     getTagType().fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMinShareRatio()
/*      */   {
/*  889 */     return this.min_share_ratio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMinShareRatio(int sr)
/*      */   {
/*  896 */     if (sr < 0)
/*      */     {
/*  898 */       sr = 0;
/*      */     }
/*      */     
/*  901 */     if (sr == this.min_share_ratio)
/*      */     {
/*  903 */       return;
/*      */     }
/*      */     
/*  906 */     this.min_share_ratio = sr;
/*      */     
/*  908 */     writeLongAttribute("rl.minsr", sr);
/*      */     
/*  910 */     Set<DownloadManager> dms = getTaggedDownloads();
/*      */     
/*  912 */     for (DownloadManager dm : dms)
/*      */     {
/*  914 */       List<Tag> dm_tags = getTagType().getTagsForTaggable(dm);
/*      */       
/*  916 */       for (Tag t : dm_tags)
/*      */       {
/*  918 */         if (t != this)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  923 */           if ((t instanceof TagFeatureRateLimit))
/*      */           {
/*  925 */             int o_sr = ((TagFeatureRateLimit)t).getTagMinShareRatio();
/*      */             
/*  927 */             if (o_sr > sr)
/*      */             {
/*  929 */               sr = o_sr;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  934 */       dm.getDownloadState().setIntParameter("sr.min", sr);
/*      */     }
/*      */     
/*  937 */     getTagType().fireChanged(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxShareRatio()
/*      */   {
/*  943 */     return this.max_share_ratio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxShareRatio(int sr)
/*      */   {
/*  950 */     if (sr < 0)
/*      */     {
/*  952 */       sr = 0;
/*      */     }
/*      */     
/*  955 */     if (sr == this.max_share_ratio)
/*      */     {
/*  957 */       return;
/*      */     }
/*      */     
/*  960 */     this.max_share_ratio = sr;
/*      */     
/*  962 */     writeLongAttribute("rl.maxsr", sr);
/*      */     
/*  964 */     Set<DownloadManager> dms = getTaggedDownloads();
/*      */     
/*  966 */     for (DownloadManager dm : dms)
/*      */     {
/*  968 */       List<Tag> dm_tags = getTagType().getTagsForTaggable(dm);
/*      */       
/*  970 */       for (Tag t : dm_tags)
/*      */       {
/*  972 */         if (t != this)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  977 */           if ((t instanceof TagFeatureRateLimit))
/*      */           {
/*  979 */             int o_sr = ((TagFeatureRateLimit)t).getTagMaxShareRatio();
/*      */             
/*  981 */             if (o_sr > sr)
/*      */             {
/*  983 */               sr = o_sr;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  988 */       dm.getDownloadState().setIntParameter("sr.max", sr);
/*      */     }
/*      */     
/*  991 */     getTagType().fireChanged(this);
/*      */     
/*  993 */     checkIndividualShareRatio();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxShareRatioAction()
/*      */   {
/*  999 */     return this.max_share_ratio_action;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxShareRatioAction(int action)
/*      */   {
/* 1006 */     if (action == this.max_share_ratio_action)
/*      */     {
/* 1008 */       return;
/*      */     }
/*      */     
/* 1011 */     this.max_share_ratio_action = action;
/*      */     
/* 1013 */     writeLongAttribute("rl.maxsr.a", action);
/*      */     
/* 1015 */     getTagType().fireChanged(this);
/*      */     
/* 1017 */     checkIndividualShareRatio();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagAggregateShareRatio()
/*      */   {
/* 1023 */     updateStuff();
/*      */     
/* 1025 */     return this.aggregate_sr;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxAggregateShareRatio()
/*      */   {
/* 1031 */     return this.max_aggregate_share_ratio;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxAggregateShareRatio(int sr)
/*      */   {
/* 1038 */     if (sr < 0)
/*      */     {
/* 1040 */       sr = 0;
/*      */     }
/*      */     
/* 1043 */     if (sr == this.max_aggregate_share_ratio)
/*      */     {
/* 1045 */       return;
/*      */     }
/*      */     
/* 1048 */     this.max_aggregate_share_ratio = sr;
/*      */     
/* 1050 */     writeLongAttribute("rl.maxaggsr", sr);
/*      */     
/* 1052 */     getTagType().fireChanged(this);
/*      */     
/* 1054 */     checkAggregateShareRatio();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTagMaxAggregateShareRatioAction()
/*      */   {
/* 1060 */     return this.max_aggregate_share_ratio_action;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxAggregateShareRatioAction(int action)
/*      */   {
/* 1067 */     if (action == this.max_aggregate_share_ratio_action)
/*      */     {
/* 1069 */       return;
/*      */     }
/*      */     
/* 1072 */     this.max_aggregate_share_ratio_action = action;
/*      */     
/* 1074 */     writeLongAttribute("rl.maxaggsr.a", action);
/*      */     
/* 1076 */     getTagType().fireChanged(this);
/*      */     
/* 1078 */     checkAggregateShareRatio();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getTagMaxAggregateShareRatioHasPriority()
/*      */   {
/* 1084 */     return this.max_aggregate_share_ratio_priority;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTagMaxAggregateShareRatioHasPriority(boolean priority)
/*      */   {
/* 1091 */     if (priority == this.max_aggregate_share_ratio_priority)
/*      */     {
/* 1093 */       return;
/*      */     }
/*      */     
/* 1096 */     this.max_aggregate_share_ratio_priority = priority;
/*      */     
/* 1098 */     writeBooleanAttribute("rl.maxaggsr.p", Boolean.valueOf(priority));
/*      */     
/* 1100 */     getTagType().fireChanged(this);
/*      */     
/* 1102 */     checkIndividualShareRatio();
/*      */     
/* 1104 */     checkAggregateShareRatio();
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateStuff()
/*      */   {
/* 1110 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1112 */     if (now - this.last_rate_update > 2500L)
/*      */     {
/* 1114 */       int new_up = 0;
/* 1115 */       int new_down = 0;
/*      */       
/* 1117 */       long new_agg_up = 0L;
/* 1118 */       long new_agg_down = 0L;
/*      */       
/*      */ 
/* 1121 */       Set<DownloadManager> dms = getTaggedDownloads();
/*      */       
/* 1123 */       if (dms.size() == 0)
/*      */       {
/* 1125 */         new_up = -1;
/* 1126 */         new_down = -1;
/*      */       }
/*      */       else
/*      */       {
/* 1130 */         new_up = 0;
/* 1131 */         new_down = 0;
/*      */         
/* 1133 */         for (DownloadManager dm : dms)
/*      */         {
/* 1135 */           DownloadManagerStats stats = dm.getStats();
/*      */           
/* 1137 */           new_up = (int)(new_up + (stats.getDataSendRate() + stats.getProtocolSendRate()));
/* 1138 */           new_down = (int)(new_down + (stats.getDataReceiveRate() + stats.getProtocolReceiveRate()));
/*      */           
/* 1140 */           long downloaded = stats.getTotalGoodDataBytesReceived();
/* 1141 */           long uploaded = stats.getTotalDataBytesSent();
/*      */           
/* 1143 */           if (downloaded > 0L)
/*      */           {
/* 1145 */             new_agg_down += downloaded;
/*      */           }
/*      */           
/* 1148 */           if (uploaded > 0L)
/*      */           {
/* 1150 */             new_agg_up += uploaded;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1156 */       this.upload_rate = new_up;
/* 1157 */       this.download_rate = new_down;
/*      */       
/* 1159 */       this.aggregate_sr = (new_agg_down <= 0L ? 0 : (int)(1000L * new_agg_up / new_agg_down));
/*      */       
/*      */ 
/* 1162 */       this.last_rate_update = now;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkIndividualShareRatio()
/*      */   {
/* 1169 */     if (this.max_share_ratio <= 0)
/*      */     {
/*      */ 
/*      */ 
/* 1173 */       return;
/*      */     }
/*      */     
/* 1176 */     if (this.max_share_ratio_action == 0)
/*      */     {
/*      */ 
/*      */ 
/* 1180 */       return;
/*      */     }
/*      */     
/* 1183 */     if ((this.max_aggregate_share_ratio_priority) && (this.max_aggregate_share_ratio > 0))
/*      */     {
/* 1185 */       updateStuff();
/*      */       
/* 1187 */       if (this.aggregate_sr < this.max_aggregate_share_ratio)
/*      */       {
/*      */ 
/*      */ 
/* 1191 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1195 */     Set<DownloadManager> dms = getTaggedDownloads();
/*      */     
/* 1197 */     Set<DownloadManager> to_action = new HashSet();
/*      */     
/* 1199 */     for (DownloadManager dm : dms)
/*      */     {
/* 1201 */       if ((dm.isDownloadComplete(false)) && (!dm.isForceStart()))
/*      */       {
/* 1203 */         int state = dm.getState();
/*      */         
/* 1205 */         if ((state == 75) || (state == 60))
/*      */         {
/* 1207 */           int sr = dm.getStats().getShareRatio();
/*      */           
/* 1209 */           if (sr >= this.max_share_ratio)
/*      */           {
/* 1211 */             to_action.add(dm);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1217 */     if (to_action.size() > 0)
/*      */     {
/* 1219 */       performOperation(this.max_share_ratio_action == 1 ? 2 : 1, to_action);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean isAggregateShareRatioMet()
/*      */   {
/* 1229 */     if (this.max_aggregate_share_ratio == 0)
/*      */     {
/* 1231 */       return true;
/*      */     }
/*      */     
/* 1234 */     updateStuff();
/*      */     
/* 1236 */     return this.aggregate_sr >= this.max_aggregate_share_ratio;
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkAggregateShareRatio()
/*      */   {
/* 1242 */     if (this.max_aggregate_share_ratio > 0)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1248 */       if ((TorrentUtils.isTorrentDeleting()) || (TorrentUtils.getMillisecondsSinceLastTorrentDelete() < 10000L))
/*      */       {
/*      */ 
/* 1251 */         return;
/*      */       }
/*      */       
/* 1254 */       updateStuff();
/*      */       
/* 1256 */       if (this.aggregate_sr >= this.max_aggregate_share_ratio)
/*      */       {
/* 1258 */         Set<DownloadManager> dms = new HashSet(getTaggedDownloads());
/*      */         
/* 1260 */         Iterator<DownloadManager> it = dms.iterator();
/*      */         
/*      */ 
/*      */ 
/* 1264 */         while (it.hasNext())
/*      */         {
/* 1266 */           DownloadManager dm = (DownloadManager)it.next();
/*      */           
/* 1268 */           if ((dm.isForceStart()) || (!dm.isDownloadComplete(false)))
/*      */           {
/* 1270 */             it.remove();
/*      */           }
/*      */           else
/*      */           {
/* 1274 */             if ((!this.max_aggregate_share_ratio_priority) && (this.max_share_ratio > 0))
/*      */             {
/* 1276 */               int sr = dm.getStats().getShareRatio();
/*      */               
/* 1278 */               if (sr < this.max_share_ratio)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 1283 */                 it.remove();
/*      */                 
/* 1285 */                 continue;
/*      */               }
/*      */             }
/*      */             
/* 1289 */             List<Tag> all_tags = getTagType().getTagManager().getTagsForTaggable(dm);
/*      */             
/* 1291 */             for (Tag tag : all_tags)
/*      */             {
/* 1293 */               if ((tag != this) && ((tag instanceof TagDownloadWithState)))
/*      */               {
/* 1295 */                 TagDownloadWithState other_tag = (TagDownloadWithState)tag;
/*      */                 
/* 1297 */                 if (!other_tag.isAggregateShareRatioMet())
/*      */                 {
/* 1299 */                   it.remove();
/*      */                   
/* 1301 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1308 */         performOperation(this.max_aggregate_share_ratio_action == 1 ? 2 : 1, dms);
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1315 */         performOperation(this.max_aggregate_share_ratio_action == 1 ? 4 : 8);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sync()
/*      */   {
/* 1325 */     checkIndividualShareRatio();
/*      */     
/* 1327 */     checkAggregateShareRatio();
/*      */     
/* 1329 */     checkMaximumTaggables();
/*      */     
/* 1331 */     super.sync();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRunStateCapabilities()
/*      */   {
/* 1337 */     return this.run_states;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasRunStateCapability(int capability)
/*      */   {
/* 1344 */     return (this.run_states & capability) != 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean[] getPerformableOperations(int[] ops)
/*      */   {
/* 1351 */     boolean[] result = new boolean[ops.length];
/*      */     
/* 1353 */     Set<DownloadManager> dms = getTaggedDownloads();
/*      */     
/* 1355 */     for (DownloadManager dm : dms)
/*      */     {
/* 1357 */       int dm_state = dm.getState();
/*      */       
/* 1359 */       for (int i = 0; i < ops.length; i++)
/*      */       {
/* 1361 */         if (result[i] == 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1366 */           int op = ops[i];
/*      */           
/* 1368 */           if ((op & 0x8) != 0)
/*      */           {
/* 1370 */             if ((dm_state == 70) || (dm_state == 100))
/*      */             {
/*      */ 
/* 1373 */               result[i] = true;
/*      */             }
/*      */           }
/*      */           
/* 1377 */           if ((op & 0x1) != 0)
/*      */           {
/* 1379 */             if ((dm_state != 70) && (dm_state != 65) && (dm_state != 100))
/*      */             {
/*      */ 
/*      */ 
/* 1383 */               result[i] = true;
/*      */             }
/*      */           }
/*      */           
/* 1387 */           if ((op & 0x2) != 0)
/*      */           {
/* 1389 */             if ((dm_state != 70) && (dm_state != 65) && (dm_state != 100))
/*      */             {
/*      */ 
/*      */ 
/* 1393 */               if (!dm.isPaused())
/*      */               {
/* 1395 */                 result[i] = true;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1400 */           if ((op & 0x4) != 0)
/*      */           {
/* 1402 */             if (dm.isPaused())
/*      */             {
/* 1404 */               result[i] = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1410 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void performOperation(int op)
/*      */   {
/* 1417 */     Set<DownloadManager> dms = getTaggedDownloads();
/*      */     
/* 1419 */     performOperation(op, dms);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void performOperation(int op, Set<DownloadManager> dms)
/*      */   {
/* 1427 */     for (final DownloadManager dm : dms)
/*      */     {
/* 1429 */       int dm_state = dm.getState();
/*      */       
/* 1431 */       if (op == 8)
/*      */       {
/* 1433 */         if ((dm_state == 70) || (dm_state == 100))
/*      */         {
/*      */ 
/* 1436 */           rs_async.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 1442 */               dm.setStateQueued();
/*      */             }
/*      */           });
/*      */         }
/* 1446 */       } else if (op == 1)
/*      */       {
/* 1448 */         if ((dm_state != 70) && (dm_state != 65) && (dm_state != 100))
/*      */         {
/*      */ 
/*      */ 
/* 1452 */           rs_async.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 1458 */               dm.stopIt(70, false, false);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 1463 */               TagDownloadWithState.this.checkMaximumTaggables();
/*      */             }
/*      */           });
/*      */         }
/* 1467 */       } else if (op == 2)
/*      */       {
/* 1469 */         if ((dm_state != 70) && (dm_state != 65) && (dm_state != 100))
/*      */         {
/*      */ 
/*      */ 
/* 1473 */           rs_async.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 1479 */               dm.pause();
/*      */             }
/*      */           });
/*      */         }
/* 1483 */       } else if (op == 4)
/*      */       {
/* 1485 */         if (dm.isPaused())
/*      */         {
/* 1487 */           rs_async.dispatch(new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/* 1493 */               dm.resume();
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSupportedActions()
/*      */   {
/* 1504 */     if (getTagType().getTagType() == 3)
/*      */     {
/* 1506 */       return 254;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1514 */     if (getTagType().getTagType() == 2)
/*      */     {
/* 1516 */       return 32;
/*      */     }
/*      */     
/*      */ 
/* 1520 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setSupportsTagTranscode(boolean sup)
/*      */   {
/* 1528 */     this.supports_xcode = sup;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsTagTranscode()
/*      */   {
/* 1534 */     return this.supports_xcode;
/*      */   }
/*      */   
/*      */ 
/*      */   public String[] getTagTranscodeTarget()
/*      */   {
/* 1540 */     String temp = readStringAttribute("xcode.to", null);
/*      */     
/* 1542 */     if (temp == null)
/*      */     {
/* 1544 */       return null;
/*      */     }
/*      */     
/* 1547 */     String[] bits = temp.split("\n");
/*      */     
/* 1549 */     if (bits.length != 2)
/*      */     {
/* 1551 */       return null;
/*      */     }
/*      */     
/* 1554 */     return bits;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTagTranscodeTarget(String uid, String name)
/*      */   {
/* 1562 */     writeStringAttribute("xcode.to", uid + "\n" + name);
/*      */     
/* 1564 */     getTagType().fireChanged(this);
/*      */     
/* 1566 */     getManager().featureChanged(this, 8);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSupportsFileLocation(boolean sup)
/*      */   {
/* 1573 */     this.supports_file_location = sup;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsTagInitialSaveFolder()
/*      */   {
/* 1580 */     return this.supports_file_location;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsTagMoveOnComplete()
/*      */   {
/* 1587 */     return this.supports_file_location;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsTagCopyOnComplete()
/*      */   {
/* 1594 */     return this.supports_file_location;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TagFeatureProperties.TagProperty[] getSupportedProperties()
/*      */   {
/* 1601 */     return getTagType().isTagTypeAuto() ? new TagFeatureProperties.TagProperty[0] : this.tag_properties;
/*      */   }
/*      */   
/* 1604 */   private static final boolean[] AUTO_BOTH = { true, true };
/* 1605 */   private static final boolean[] AUTO_NONE = { false, false };
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean[] isTagAuto()
/*      */   {
/* 1611 */     TagFeatureProperties.TagProperty[] props = getSupportedProperties();
/*      */     
/* 1613 */     for (TagFeatureProperties.TagProperty prop : props)
/*      */     {
/* 1615 */       String name = prop.getName(false);
/*      */       
/* 1617 */       if (!name.equals("tracker_templates"))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1622 */         int type = prop.getType();
/*      */         
/* 1624 */         if (type == 2)
/*      */         {
/* 1626 */           Boolean b = prop.getBoolean();
/*      */           
/* 1628 */           if ((b != null) && (b.booleanValue()))
/*      */           {
/* 1630 */             return AUTO_BOTH;
/*      */           }
/* 1632 */         } else if (type == 3)
/*      */         {
/* 1634 */           Long l = prop.getLong();
/*      */           
/* 1636 */           if ((l != null) && (l.longValue() != Long.MIN_VALUE))
/*      */           {
/* 1638 */             return AUTO_BOTH;
/*      */           }
/* 1640 */         } else if (type == 1)
/*      */         {
/* 1642 */           String[] vals = prop.getStringList();
/*      */           
/* 1644 */           if ((vals != null) && (vals.length > 0))
/*      */           {
/* 1646 */             if ((name.equals("constraint")) && (vals.length > 1))
/*      */             {
/* 1648 */               String options = vals[1];
/*      */               
/* 1650 */               if (options != null)
/*      */               {
/* 1652 */                 if (options.contains("am=1;"))
/*      */                 {
/* 1654 */                   return new boolean[] { true, false };
/*      */                 }
/* 1656 */                 if (options.contains("am=2;"))
/*      */                 {
/* 1658 */                   return new boolean[] { false, true };
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1663 */             return AUTO_BOTH;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1668 */     return AUTO_NONE;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaximumTaggables()
/*      */   {
/* 1675 */     if (getTagType().getTagType() != 3)
/*      */     {
/* 1677 */       return -1;
/*      */     }
/*      */     
/* 1680 */     return super.getMaximumTaggables();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkMaximumTaggables()
/*      */   {
/* 1687 */     if (getTagType().getTagType() != 3)
/*      */     {
/* 1689 */       return;
/*      */     }
/*      */     
/* 1692 */     int max = getMaximumTaggables();
/*      */     
/* 1694 */     if (max <= 0)
/*      */     {
/* 1696 */       return;
/*      */     }
/*      */     
/* 1699 */     if (max == 999999)
/*      */     {
/* 1701 */       max = 0;
/*      */     }
/*      */     
/* 1704 */     int removal_strategy = getRemovalStrategy();
/*      */     
/* 1706 */     if (removal_strategy == 0)
/*      */     {
/* 1708 */       return;
/*      */     }
/*      */     
/* 1711 */     if (getTaggedCount() > max)
/*      */     {
/* 1713 */       Set<DownloadManager> dms = getTaggedDownloads();
/*      */       
/* 1715 */       List<DownloadManager> sorted_dms = new ArrayList(dms);
/*      */       
/* 1717 */       final int order = getOrdering();
/*      */       
/* 1719 */       Collections.sort(sorted_dms, new Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(DownloadManager dm1, DownloadManager dm2)
/*      */         {
/*      */ 
/*      */ 
/* 1728 */           if (order == 0)
/*      */           {
/* 1730 */             long t1 = dm1.getDownloadState().getLongParameter("stats.download.added.time");
/* 1731 */             long t2 = dm2.getDownloadState().getLongParameter("stats.download.added.time");
/*      */             
/* 1733 */             if (t1 < t2)
/*      */             {
/* 1735 */               return -1;
/*      */             }
/* 1737 */             if (t1 > t2)
/*      */             {
/* 1739 */               return 1;
/*      */             }
/*      */             
/*      */ 
/* 1743 */             return dm1.getInternalName().compareTo(dm2.getInternalName());
/*      */           }
/*      */           
/*      */ 
/* 1747 */           long t1 = TagDownloadWithState.this.getTaggableAddedTime(dm1);
/* 1748 */           long t2 = TagDownloadWithState.this.getTaggableAddedTime(dm2);
/*      */           
/* 1750 */           if (t1 < t2)
/*      */           {
/* 1752 */             return -1;
/*      */           }
/* 1754 */           if (t1 > t2)
/*      */           {
/* 1756 */             return 1;
/*      */           }
/*      */           
/*      */ 
/* 1760 */           return dm1.getInternalName().compareTo(dm2.getInternalName());
/*      */ 
/*      */         }
/*      */         
/*      */ 
/* 1765 */       });
/* 1766 */       Iterator<DownloadManager> it = sorted_dms.iterator();
/*      */       
/* 1768 */       while ((it.hasNext()) && (sorted_dms.size() > max))
/*      */       {
/* 1770 */         DownloadManager dm = (DownloadManager)it.next();
/*      */         
/* 1772 */         if (dm.isPersistent())
/*      */         {
/* 1774 */           it.remove();
/*      */           try
/*      */           {
/* 1777 */             if (removal_strategy == 1)
/*      */             {
/* 1779 */               Download download = PluginCoreUtils.wrap(dm);
/*      */               
/* 1781 */               if (download.canStubbify())
/*      */               {
/*      */ 
/*      */ 
/* 1785 */                 removeTaggable(dm);
/*      */                 
/* 1787 */                 download.stubbify();
/*      */               }
/* 1789 */             } else if (removal_strategy == 2)
/*      */             {
/* 1791 */               dm.getGlobalManager().removeDownloadManager(dm, false, false);
/*      */             }
/* 1793 */             else if (removal_strategy == 3)
/*      */             {
/* 1795 */               boolean reallyDeleteData = !dm.getDownloadState().getFlag(64L);
/*      */               
/* 1797 */               dm.getGlobalManager().removeDownloadManager(dm, true, reallyDeleteData);
/*      */             }
/* 1799 */             else if (removal_strategy == 4)
/*      */             {
/* 1801 */               String old_tag = getTagName(true) + "_";
/*      */               
/* 1803 */               if (Character.isUpperCase(old_tag.charAt(0)))
/*      */               {
/* 1805 */                 old_tag = old_tag + "Old";
/*      */               }
/*      */               else {
/* 1808 */                 old_tag = old_tag + "old";
/*      */               }
/*      */               
/* 1811 */               Tag ot = getTagType().getTag(old_tag, true);
/*      */               
/* 1813 */               if (ot == null)
/*      */               {
/* 1815 */                 ot = getTagType().createTag(old_tag, true);
/*      */               }
/*      */               
/* 1818 */               ot.addTaggable(dm);
/*      */               
/* 1820 */               removeTaggable(dm);
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1825 */             Debug.out(e);
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/* 1831 */           Logger.log(new LogAlert(false, 1, "Non-persistent downloads (e.g. shares) can't be automatically deleted or archived. Maximum entries not enforced for Tag '" + getTagName(true) + "'"));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/impl/TagDownloadWithState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */