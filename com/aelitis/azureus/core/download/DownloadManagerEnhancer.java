/*     */ package com.aelitis.azureus.core.download;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.core.tag.TagTypeAdapter;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.util.ExternalStimulusHandler;
/*     */ import com.aelitis.azureus.util.ExternalStimulusListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerChannelImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerChannelImpl.channelCreateListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DownloadManagerEnhancer
/*     */ {
/*     */   public static final int TICK_PERIOD = 1000;
/*  54 */   private static TagManager tag_manager = ;
/*     */   
/*     */   private static DownloadManagerEnhancer singleton;
/*     */   
/*     */   private AzureusCore core;
/*     */   
/*     */   public static synchronized DownloadManagerEnhancer initialise(AzureusCore core)
/*     */   {
/*  62 */     if (singleton == null)
/*     */     {
/*  64 */       singleton = new DownloadManagerEnhancer(core);
/*     */     }
/*     */     
/*  67 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */   public static synchronized DownloadManagerEnhancer getSingleton()
/*     */   {
/*  73 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  78 */   private Map<DownloadManager, EnhancedDownloadManager> download_map = new IdentityHashMap();
/*     */   
/*  80 */   private Set<HashWrapper> pause_set = new HashSet();
/*     */   
/*     */   private boolean progressive_enabled;
/*     */   
/*     */   private long progressive_active_counter;
/*     */   
/*     */   private TimerEventPeriodic pa_timer;
/*     */   
/*     */ 
/*     */   protected DownloadManagerEnhancer(AzureusCore _core)
/*     */   {
/*  91 */     this.core = _core;
/*     */     
/*  93 */     boolean tag_all = initAutoTag();
/*     */     
/*  95 */     this.core.getGlobalManager().addListener(new GlobalManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void downloadManagerAdded(DownloadManager dm)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 105 */         DownloadManagerEnhancer.this.handleAutoTag(dm);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void downloadManagerRemoved(DownloadManager dm)
/*     */       {
/*     */         EnhancedDownloadManager edm;
/*     */         
/* 114 */         synchronized (DownloadManagerEnhancer.this.download_map)
/*     */         {
/* 116 */           edm = (EnhancedDownloadManager)DownloadManagerEnhancer.this.download_map.remove(dm);
/*     */         }
/*     */         
/* 119 */         if (edm != null)
/*     */         {
/* 121 */           edm.destroy();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 130 */       public void destroyInitiated() { DownloadManagerEnhancer.this.resume(); } public void destroyed() {} public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {} }, tag_all);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 145 */     ExternalStimulusHandler.addListener(new ExternalStimulusListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean receive(String name, Map values)
/*     */       {
/*     */ 
/*     */ 
/* 153 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */       public int query(String name, Map values)
/*     */       {
/*     */         byte[] b_hash;
/*     */         
/* 161 */         if (name.equals("az3.downloadmanager.stream.eta"))
/*     */         {
/* 163 */           Object hash = values.get("hash");
/*     */           
/* 165 */           b_hash = null;
/*     */           
/* 167 */           if ((hash instanceof String))
/*     */           {
/* 169 */             String hash_str = (String)hash;
/*     */             
/* 171 */             if (hash_str.length() == 32)
/*     */             {
/* 173 */               b_hash = Base32.decode(hash_str);
/*     */             }
/*     */             else
/*     */             {
/* 177 */               b_hash = ByteFormatter.decodeString(hash_str);
/*     */             }
/*     */           }
/*     */           
/* 181 */           if (b_hash != null)
/*     */           {
/*     */ 
/*     */ 
/* 185 */             DownloadManagerEnhancer.this.getEnhancedDownload(b_hash);
/*     */           }
/*     */           
/*     */           List<EnhancedDownloadManager> edms_copy;
/*     */           
/* 190 */           synchronized (DownloadManagerEnhancer.this.download_map)
/*     */           {
/* 192 */             edms_copy = new ArrayList(DownloadManagerEnhancer.this.download_map.values());
/*     */           }
/*     */           
/* 195 */           for (EnhancedDownloadManager edm : edms_copy)
/*     */           {
/* 197 */             if (b_hash != null)
/*     */             {
/* 199 */               byte[] d_hash = edm.getHash();
/*     */               
/* 201 */               if ((d_hash != null) && (Arrays.equals(b_hash, d_hash)))
/*     */               {
/*     */ 
/*     */ 
/* 205 */                 if (edm.getDownloadManager().isDownloadComplete(false))
/*     */                 {
/* 207 */                   return 0;
/*     */                 }
/*     */                 
/* 210 */                 if (!edm.supportsProgressiveMode())
/*     */                 {
/* 212 */                   return Integer.MIN_VALUE;
/*     */                 }
/*     */                 
/* 215 */                 if (!edm.getProgressiveMode())
/*     */                 {
/* 217 */                   edm.setProgressiveMode(true);
/*     */                 }
/*     */                 
/* 220 */                 long eta = edm.getProgressivePlayETA();
/*     */                 
/* 222 */                 if (eta > 2147483647L)
/*     */                 {
/* 224 */                   return Integer.MAX_VALUE;
/*     */                 }
/*     */                 
/* 227 */                 return (int)eta;
/*     */               }
/*     */               
/*     */             }
/* 231 */             else if (edm.getProgressiveMode())
/*     */             {
/* 233 */               long eta = edm.getProgressivePlayETA();
/*     */               
/* 235 */               if (eta > 2147483647L)
/*     */               {
/* 237 */                 return Integer.MAX_VALUE;
/*     */               }
/*     */               
/* 240 */               return (int)eta;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 246 */         return Integer.MIN_VALUE;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 251 */     });
/* 252 */     DiskManagerChannelImpl.addListener(new DiskManagerChannelImpl.channelCreateListener()
/*     */     {
/*     */ 
/*     */       public void channelCreated(DiskManagerChannel channel)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/* 260 */           EnhancedDownloadManager edm = DownloadManagerEnhancer.this.getEnhancedDownload(PluginCoreUtils.unwrap(channel.getFile().getDownload()));
/*     */           
/*     */ 
/*     */ 
/* 264 */           if (edm == null)
/*     */           {
/* 266 */             return;
/*     */           }
/*     */           
/* 269 */           if (edm.getDownloadManager().isDownloadComplete(true))
/*     */           {
/* 271 */             return;
/*     */           }
/*     */           
/* 274 */           if (!edm.getProgressiveMode())
/*     */           {
/* 276 */             if (edm.supportsProgressiveMode())
/*     */             {
/* 278 */               Debug.out("Enabling progressive mode for '" + edm.getName() + "' due to external stream");
/*     */               
/* 280 */               edm.setProgressiveMode(true);
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 285 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void progressiveActivated()
/*     */   {
/* 294 */     synchronized (this)
/*     */     {
/* 296 */       this.progressive_active_counter += 1L;
/*     */       
/* 298 */       if (this.pa_timer == null)
/*     */       {
/* 300 */         this.pa_timer = SimpleTimer.addPeriodicEvent("DownloadManagerEnhancer:speedChecker", 1000L, new TimerEventPerformer()
/*     */         {
/*     */           private int tick_count;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 308 */           private long last_inactive_marker = 0L;
/*     */           
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/* 314 */             this.tick_count += 1;
/*     */             
/*     */             long current_marker;
/*     */             
/* 318 */             synchronized (DownloadManagerEnhancer.this)
/*     */             {
/* 320 */               current_marker = DownloadManagerEnhancer.this.progressive_active_counter;
/*     */               
/* 322 */               if (this.last_inactive_marker == current_marker)
/*     */               {
/* 324 */                 DownloadManagerEnhancer.this.pa_timer.cancel();
/*     */                 
/* 326 */                 DownloadManagerEnhancer.this.pa_timer = null;
/*     */                 
/* 328 */                 return;
/*     */               }
/*     */             }
/*     */             
/* 332 */             List downloads = DownloadManagerEnhancer.this.core.getGlobalManager().getDownloadManagers();
/*     */             
/* 334 */             boolean is_active = false;
/*     */             
/* 336 */             for (int i = 0; i < downloads.size(); i++)
/*     */             {
/* 338 */               DownloadManager download = (DownloadManager)downloads.get(i);
/*     */               
/* 340 */               EnhancedDownloadManager edm = DownloadManagerEnhancer.this.getEnhancedDownload(download);
/*     */               
/* 342 */               if (edm != null)
/*     */               {
/* 344 */                 if (edm.updateStats(this.tick_count))
/*     */                 {
/* 346 */                   is_active = true;
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 351 */             if (!is_active)
/*     */             {
/* 353 */               this.last_inactive_marker = current_marker;
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected AzureusCore getCore()
/*     */   {
/* 364 */     return this.core;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void pause(DownloadManager dm)
/*     */   {
/* 371 */     TOTorrent torrent = dm.getTorrent();
/*     */     
/* 373 */     if (torrent == null)
/*     */     {
/* 375 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 379 */       HashWrapper hw = torrent.getHashWrapper();
/*     */       
/* 381 */       synchronized (this.pause_set)
/*     */       {
/* 383 */         if (this.pause_set.contains(hw))
/*     */         {
/* 385 */           return;
/*     */         }
/*     */         
/* 388 */         this.pause_set.add(hw);
/*     */       }
/*     */       
/* 391 */       dm.pause();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 395 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void resume(DownloadManager dm)
/*     */   {
/* 403 */     TOTorrent torrent = dm.getTorrent();
/*     */     
/* 405 */     if (torrent == null)
/*     */     {
/* 407 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 411 */       HashWrapper hw = torrent.getHashWrapper();
/*     */       
/* 413 */       synchronized (this.pause_set)
/*     */       {
/* 415 */         if (!this.pause_set.remove(hw))
/*     */         {
/* 417 */           return;
/*     */         }
/*     */       }
/*     */       
/* 421 */       dm.resume();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 425 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resume()
/*     */   {
/*     */     Set<HashWrapper> copy;
/*     */     
/* 434 */     synchronized (this.pause_set)
/*     */     {
/* 436 */       copy = new HashSet(this.pause_set);
/*     */       
/* 438 */       this.pause_set.clear();
/*     */     }
/*     */     
/* 441 */     GlobalManager gm = this.core.getGlobalManager();
/*     */     
/* 443 */     for (HashWrapper hw : copy)
/*     */     {
/* 445 */       DownloadManager dm = gm.getDownloadManager(hw);
/*     */       
/* 447 */       if (dm != null)
/*     */       {
/* 449 */         dm.resume();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void prepareForProgressiveMode(DownloadManager dm, boolean active)
/*     */   {
/* 459 */     if (active)
/*     */     {
/* 461 */       GlobalManager gm = this.core.getGlobalManager();
/*     */       
/* 463 */       List<DownloadManager> dms = gm.getDownloadManagers();
/*     */       
/* 465 */       for (DownloadManager this_dm : dms)
/*     */       {
/* 467 */         if (this_dm != dm)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 472 */           if (!this_dm.isDownloadComplete(false))
/*     */           {
/* 474 */             int state = this_dm.getState();
/*     */             
/* 476 */             if ((state == 50) || (state == 75))
/*     */             {
/*     */ 
/* 479 */               pause(this_dm);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 484 */       if (dm.isPaused())
/*     */       {
/* 486 */         dm.resume();
/*     */       }
/*     */     }
/*     */     else {
/* 490 */       resume();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public EnhancedDownloadManager getEnhancedDownload(byte[] hash)
/*     */   {
/* 498 */     DownloadManager dm = this.core.getGlobalManager().getDownloadManager(new HashWrapper(hash));
/*     */     
/* 500 */     if (dm == null)
/*     */     {
/* 502 */       return null;
/*     */     }
/*     */     
/* 505 */     return getEnhancedDownload(dm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public EnhancedDownloadManager getEnhancedDownload(DownloadManager manager)
/*     */   {
/* 512 */     TOTorrent torrent = manager.getTorrent();
/*     */     
/* 514 */     if (torrent == null)
/*     */     {
/* 516 */       return null;
/*     */     }
/*     */     
/* 519 */     DownloadManager dm2 = manager.getGlobalManager().getDownloadManager(torrent);
/*     */     
/* 521 */     if (dm2 != manager)
/*     */     {
/* 523 */       return null;
/*     */     }
/*     */     
/* 526 */     synchronized (this.download_map)
/*     */     {
/* 528 */       EnhancedDownloadManager res = (EnhancedDownloadManager)this.download_map.get(manager);
/*     */       
/* 530 */       if (res == null)
/*     */       {
/* 532 */         res = new EnhancedDownloadManager(this, manager);
/*     */         
/* 534 */         this.download_map.put(manager, res);
/*     */       }
/*     */       
/* 537 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isProgressiveAvailable()
/*     */   {
/* 544 */     if (this.progressive_enabled)
/*     */     {
/* 546 */       return true;
/*     */     }
/*     */     
/* 549 */     PluginInterface ms_pi = this.core.getPluginManager().getPluginInterfaceByID("azupnpav", true);
/*     */     
/* 551 */     if (ms_pi != null)
/*     */     {
/* 553 */       this.progressive_enabled = true;
/*     */     }
/*     */     
/* 556 */     return this.progressive_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DownloadManager findDownloadManager(String hash)
/*     */   {
/*     */     Iterator<DownloadManager> iter;
/*     */     
/*     */ 
/* 566 */     synchronized (this.download_map)
/*     */     {
/* 568 */       for (iter = this.download_map.keySet().iterator(); iter.hasNext();) {
/* 569 */         DownloadManager dm = (DownloadManager)iter.next();
/*     */         
/* 571 */         TOTorrent torrent = dm.getTorrent();
/* 572 */         if (PlatformTorrentUtils.isContent(torrent, true)) {
/* 573 */           String thisHash = PlatformTorrentUtils.getContentHash(torrent);
/* 574 */           if (hash.equals(thisHash)) {
/* 575 */             return dm;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 580 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean initAutoTag()
/*     */   {
/* 586 */     if (!tag_manager.isEnabled())
/*     */     {
/* 588 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 593 */     final String[] tag_ids = { "tag.type.man.vhdn", "tag.type.man.featcon" };
/*     */     
/* 595 */     final TagType tt = tag_manager.getTagType(3);
/*     */     
/* 597 */     tt.addTagTypeListener(new TagTypeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void tagRemoved(Tag tag)
/*     */       {
/*     */ 
/* 604 */         String name = tag.getTagName(false);
/*     */         
/* 606 */         for (String t : tag_ids)
/*     */         {
/* 608 */           if (t.equals(name))
/*     */           {
/* 610 */             COConfigurationManager.setParameter(name + ".enabled", false); } } } }, false);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 617 */     for (final String id : tag_ids)
/*     */     {
/* 619 */       COConfigurationManager.addParameterListener(id + ".enabled", new ParameterListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(String name)
/*     */         {
/*     */ 
/*     */ 
/* 627 */           if (COConfigurationManager.getBooleanParameter(name))
/*     */           {
/* 629 */             DownloadManagerEnhancer.this.handleAutoTag(DownloadManagerEnhancer.this.core.getGlobalManager().getDownloadManagers());
/*     */           }
/*     */           else
/*     */           {
/* 633 */             Tag tag = tt.getTag(id, false);
/*     */             
/* 635 */             if (tag != null)
/*     */             {
/* 637 */               tag.removeTag();
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 644 */     boolean run_all = COConfigurationManager.getBooleanParameter("dme.autotag.init_pending", true);
/*     */     
/* 646 */     if (run_all)
/*     */     {
/* 648 */       COConfigurationManager.setParameter("dme.autotag.init_pending", false);
/*     */     }
/*     */     
/* 651 */     return run_all;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void handleAutoTag(List<DownloadManager> dms)
/*     */   {
/* 658 */     for (DownloadManager dm : dms)
/*     */     {
/* 660 */       handleAutoTag(dm);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void handleAutoTag(DownloadManager dm)
/*     */   {
/* 668 */     if (!tag_manager.isEnabled())
/*     */     {
/* 670 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 674 */       TOTorrent torrent = dm.getTorrent();
/*     */       
/* 676 */       if (torrent != null)
/*     */       {
/* 678 */         boolean is_vhdn = PlatformTorrentUtils.getContentNetworkID(torrent) == 3L;
/*     */         
/* 680 */         String content_type = PlatformTorrentUtils.getContentType(torrent);
/*     */         
/* 682 */         if ((content_type != null) && (!content_type.toLowerCase().contains("vhdn")))
/*     */         {
/*     */ 
/*     */ 
/* 686 */           is_vhdn = false;
/*     */         }
/*     */         
/* 689 */         if (is_vhdn)
/*     */         {
/* 691 */           handleAutoTag(dm, "tag.type.man.vhdn", "image.sidebar.tag.vhdn");
/*     */         }
/*     */         
/* 694 */         if (PlatformTorrentUtils.isFeaturedContent(torrent))
/*     */         {
/* 696 */           handleAutoTag(dm, "tag.type.man.featcon", "image.sidebar.tag.featcon");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 701 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleAutoTag(DownloadManager dm, String tag_id, String img_id)
/*     */   {
/* 711 */     if (!tag_manager.isEnabled())
/*     */     {
/* 713 */       return;
/*     */     }
/*     */     
/* 716 */     TagType tt = tag_manager.getTagType(3);
/*     */     
/* 718 */     Tag t = tt.getTag(tag_id, false);
/*     */     
/* 720 */     if (t == null)
/*     */     {
/* 722 */       if (COConfigurationManager.getBooleanParameter(tag_id + ".enabled", true)) {
/*     */         try
/*     */         {
/* 725 */           t = tt.createTag(tag_id, false);
/*     */           
/* 727 */           t.setImageID(img_id);
/*     */           
/* 729 */           t.setColor(new int[] { 0, 74, 156 });
/*     */           
/* 731 */           t.setPublic(false);
/*     */           
/* 733 */           t.setCanBePublic(false);
/*     */           
/* 735 */           tt.addTag(t);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 739 */           Debug.out(e);
/*     */         }
/*     */         
/*     */       }
/*     */     }
/* 744 */     else if (t.canBePublic())
/*     */     {
/* 746 */       t.setCanBePublic(false);
/*     */     }
/*     */     
/*     */ 
/* 750 */     if (t != null)
/*     */     {
/* 752 */       if (!t.hasTaggable(dm))
/*     */       {
/* 754 */         t.addTaggable(dm);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/DownloadManagerEnhancer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */