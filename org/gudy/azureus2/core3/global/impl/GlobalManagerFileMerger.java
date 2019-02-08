/*      */ package org.gudy.azureus2.core3.global.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*      */ import com.aelitis.azureus.core.util.IdentityHashSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerListenerAdapter;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginAdapter;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*      */ public class GlobalManagerFileMerger
/*      */ {
/*      */   private static final boolean TRACE = false;
/*      */   private static final int MIN_PIECES = 5;
/*      */   private static final int HASH_FAILS_BEFORE_QUIT = 3;
/*      */   private static final int TIMER_PERIOD = 5000;
/*      */   private static final int FORCE_PIECE_TIMER_PERIOD = 15000;
/*      */   private static final int FORCE_PIECE_TIMER_TICKS = 3;
/*      */   private static final int SYNC_TIMER_PERIOD = 60000;
/*      */   private static final int SYNC_TIMER_TICKS = 12;
/*   80 */   private static final Object merged_data_lock = new Object();
/*      */   
/*      */   private final GlobalManagerImpl gm;
/*      */   
/*      */   private boolean initialised;
/*      */   
/*      */   private boolean enabled;
/*      */   private boolean enabled_extended;
/*   88 */   final Map<HashWrapper, DownloadManager> dm_map = new HashMap();
/*      */   
/*   90 */   final List<SameSizeFiles> sames = new ArrayList();
/*      */   
/*   92 */   final AsyncDispatcher read_write_dispatcher = new AsyncDispatcher("GMFM");
/*      */   
/*      */ 
/*      */   private TimerEventPeriodic timer_event;
/*      */   
/*      */ 
/*      */   protected GlobalManagerFileMerger(GlobalManagerImpl _gm)
/*      */   {
/*  100 */     this.gm = _gm;
/*      */     
/*  102 */     PluginInitializer.getDefaultInterface().addListener(new PluginAdapter()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */ 
/*  108 */         new DelayedEvent("GMFM:delay", 30000L, new AERunnable()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*      */ 
/*  117 */             GlobalManagerFileMerger.this.initialise();
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private void initialise()
/*      */   {
/*  127 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Merge Same Size Files", "Merge Same Size Files Extended" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String _name)
/*      */       {
/*      */ 
/*      */ 
/*  135 */         GlobalManagerFileMerger.this.enabled = COConfigurationManager.getBooleanParameter("Merge Same Size Files");
/*  136 */         GlobalManagerFileMerger.this.enabled_extended = COConfigurationManager.getBooleanParameter("Merge Same Size Files Extended");
/*      */         
/*  138 */         if (GlobalManagerFileMerger.this.initialised)
/*      */         {
/*  140 */           GlobalManagerFileMerger.this.syncFileSets();
/*      */         }
/*      */         
/*      */       }
/*  144 */     });
/*  145 */     this.gm.addListener(new GlobalManagerAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void downloadManagerAdded(DownloadManager dm)
/*      */       {
/*      */ 
/*  152 */         GlobalManagerFileMerger.this.syncFileSets();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  159 */       public void downloadManagerRemoved(DownloadManager dm) { GlobalManagerFileMerger.this.syncFileSets(); } }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  164 */     syncFileSets();
/*      */     
/*  166 */     this.initialised = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String isSwarmMerging(DownloadManager dm)
/*      */   {
/*  173 */     synchronized (this.dm_map)
/*      */     {
/*  175 */       if (this.sames.size() > 0)
/*      */       {
/*  177 */         StringBuffer result = null;
/*      */         
/*  179 */         for (SameSizeFiles s : this.sames)
/*      */         {
/*  181 */           if (s.hasDownloadManager(dm))
/*      */           {
/*  183 */             String info = s.getInfo();
/*      */             
/*  185 */             if (result == null)
/*      */             {
/*  187 */               result = new StringBuffer(1024);
/*      */             }
/*      */             else
/*      */             {
/*  191 */               result.append("\n");
/*      */             }
/*      */             
/*  194 */             result.append(info);
/*      */           }
/*      */         }
/*      */         
/*  198 */         return result == null ? null : result.toString();
/*      */       }
/*      */     }
/*      */     
/*  202 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private void syncFileSets()
/*      */   {
/*  208 */     List<DownloadManager> dms = this.gm.getDownloadManagers();
/*      */     
/*  210 */     synchronized (this.dm_map)
/*      */     {
/*  212 */       boolean changed = false;
/*      */       
/*  214 */       Set<HashWrapper> existing_dm_hashes = new HashSet(this.dm_map.keySet());
/*      */       
/*  216 */       if (this.enabled)
/*      */       {
/*  218 */         for (DownloadManager dm : dms)
/*      */         {
/*  220 */           if (dm.isPersistent())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  225 */             DownloadManagerState state = dm.getDownloadState();
/*      */             
/*  227 */             if ((!state.getFlag(16L)) && (!state.getFlag(512L)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  233 */               if ((this.enabled_extended) || (!dm.isDownloadComplete(false)))
/*      */               {
/*  235 */                 TOTorrent torrent = dm.getTorrent();
/*      */                 
/*  237 */                 if (torrent != null) {
/*      */                   try
/*      */                   {
/*  240 */                     HashWrapper hw = torrent.getHashWrapper();
/*      */                     
/*  242 */                     if (this.dm_map.containsKey(hw))
/*      */                     {
/*  244 */                       existing_dm_hashes.remove(hw);
/*      */                     }
/*      */                     else
/*      */                     {
/*  248 */                       this.dm_map.put(hw, dm);
/*      */                       
/*  250 */                       changed = true;
/*      */                     }
/*      */                   } catch (Throwable e) {}
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  259 */       if (existing_dm_hashes.size() > 0)
/*      */       {
/*  261 */         changed = true;
/*      */         
/*  263 */         for (HashWrapper hw : existing_dm_hashes)
/*      */         {
/*  265 */           this.dm_map.remove(hw);
/*      */         }
/*      */       }
/*      */       
/*  269 */       if (changed)
/*      */       {
/*  271 */         List<Set<DiskManagerFileInfo>> interesting = new LinkedList();
/*      */         
/*  273 */         Map<Long, Set<DiskManagerFileInfo>> size_map = new HashMap();
/*      */         
/*  275 */         for (DownloadManager dm : this.dm_map.values())
/*      */         {
/*  277 */           TOTorrent torrent = dm.getTorrent();
/*      */           
/*  279 */           if (torrent != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  284 */             DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/*      */             
/*  286 */             for (DiskManagerFileInfo file : files)
/*      */             {
/*      */ 
/*      */ 
/*  290 */               if (file.getNbPieces() >= 5)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  295 */                 long len = file.getLength();
/*      */                 
/*  297 */                 Set<DiskManagerFileInfo> set = (Set)size_map.get(Long.valueOf(len));
/*      */                 
/*  299 */                 if (set == null)
/*      */                 {
/*  301 */                   set = new HashSet();
/*      */                   
/*  303 */                   size_map.put(Long.valueOf(len), set);
/*      */                 }
/*      */                 
/*  306 */                 boolean same_dm = false;
/*      */                 
/*  308 */                 for (DiskManagerFileInfo existing : set)
/*      */                 {
/*  310 */                   if (existing.getDownloadManager() == dm)
/*      */                   {
/*  312 */                     same_dm = true;
/*      */                     
/*  314 */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/*  318 */                 if (!same_dm)
/*      */                 {
/*  320 */                   set.add(file);
/*      */                   
/*  322 */                   if (set.size() == 2)
/*      */                   {
/*  324 */                     interesting.add(set);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  332 */         Iterator<Set<DiskManagerFileInfo>> interesting_it = interesting.iterator();
/*      */         
/*  334 */         while (interesting_it.hasNext())
/*      */         {
/*  336 */           Set<DiskManagerFileInfo> set = (Set)interesting_it.next();
/*      */           
/*  338 */           boolean all_done = true;
/*      */           
/*  340 */           for (DiskManagerFileInfo file : set)
/*      */           {
/*  342 */             if (file.getDownloaded() != file.getLength())
/*      */             {
/*  344 */               all_done = false;
/*      */               
/*  346 */               break;
/*      */             }
/*      */           }
/*      */           
/*  350 */           if (all_done)
/*      */           {
/*  352 */             interesting_it.remove();
/*      */           }
/*      */         }
/*      */         
/*  356 */         List<SameSizeFiles> sames_copy = new LinkedList(this.sames);
/*      */         
/*  358 */         for (Set<DiskManagerFileInfo> set : interesting)
/*      */         {
/*  360 */           boolean found = false;
/*      */           
/*  362 */           Iterator<SameSizeFiles> sames_it = sames_copy.iterator();
/*      */           
/*  364 */           while (sames_it.hasNext())
/*      */           {
/*  366 */             SameSizeFiles same = (SameSizeFiles)sames_it.next();
/*      */             
/*  368 */             if (same.sameAs(set))
/*      */             {
/*  370 */               found = true;
/*      */               
/*  372 */               sames_it.remove();
/*      */               
/*  374 */               break;
/*      */             }
/*      */           }
/*      */           
/*  378 */           if (!found)
/*      */           {
/*  380 */             this.sames.add(new SameSizeFiles(set, null));
/*      */           }
/*      */         }
/*      */         
/*  384 */         for (SameSizeFiles dead : sames_copy)
/*      */         {
/*  386 */           dead.destroy();
/*      */           
/*  388 */           this.sames.remove(dead);
/*      */         }
/*      */         
/*  391 */         if (this.sames.size() > 0)
/*      */         {
/*  393 */           if (this.timer_event == null)
/*      */           {
/*  395 */             this.timer_event = SimpleTimer.addPeriodicEvent("GMFM:sync", 5000L, new TimerEventPerformer()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  401 */               private int tick_count = 0;
/*      */               
/*      */ 
/*      */ 
/*      */               public void perform(TimerEvent event)
/*      */               {
/*  407 */                 this.tick_count += 1;
/*      */                 
/*  409 */                 synchronized (GlobalManagerFileMerger.this.dm_map)
/*      */                 {
/*  411 */                   for (GlobalManagerFileMerger.SameSizeFiles s : GlobalManagerFileMerger.this.sames)
/*      */                   {
/*  413 */                     GlobalManagerFileMerger.SameSizeFiles.access$1000(s, this.tick_count);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*  421 */         else if (this.timer_event != null)
/*      */         {
/*  423 */           this.timer_event.cancel();
/*      */           
/*  425 */           this.timer_event = null;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static abstract interface DownloadManagerPeerListenerEx extends DownloadManagerPeerListener {
/*      */     public abstract void sync();
/*      */   }
/*      */   
/*      */   private class SameSizeFiles {
/*      */     private final Set<DiskManagerFileInfo> files;
/*      */     private final Set<SameSizeFileWrapper> file_wrappers;
/*  438 */     private final Set<DownloadManager> dm_set = new IdentityHashSet();
/*      */     
/*      */ 
/*      */     private boolean completion_logged;
/*      */     
/*      */     private volatile boolean dl_has_restarted;
/*      */     
/*      */     private volatile boolean destroyed;
/*      */     
/*      */ 
/*      */     private SameSizeFiles()
/*      */     {
/*  450 */       this.files = _files;
/*      */       
/*  452 */       this.file_wrappers = new HashSet();
/*      */       
/*  454 */       for (final DiskManagerFileInfo file : this.files)
/*      */       {
/*  456 */         final SameSizeFileWrapper file_wrapper = new SameSizeFileWrapper(file, null);
/*      */         
/*  458 */         DownloadManager dm = file_wrapper.getDownloadManager();
/*      */         
/*  460 */         this.dm_set.add(dm);
/*      */         
/*  462 */         this.file_wrappers.add(file_wrapper);
/*      */         
/*  464 */         GlobalManagerFileMerger.DownloadManagerPeerListenerEx dmpl = new GlobalManagerFileMerger.DownloadManagerPeerListenerEx()
/*      */         {
/*      */ 
/*  467 */           final AsyncDispatcher dispatcher = new AsyncDispatcher("GMFM:serial");
/*      */           
/*  469 */           final Object lock = this;
/*      */           
/*      */           private DiskManager current_disk_manager;
/*      */           
/*      */           private boolean pm_removed;
/*      */           
/*  475 */           final DiskManagerFileInfoListener file_listener = new DiskManagerFileInfoListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void dataWritten(long offset, long length)
/*      */             {
/*      */ 
/*      */ 
/*  483 */               if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */               {
/*  485 */                 GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.removeListener(this);
/*      */                 
/*  487 */                 return;
/*      */               }
/*      */               
/*  490 */               GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.access$1400(GlobalManagerFileMerger.SameSizeFiles.1.this.val$file_wrapper, offset, length);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void dataChecked(long offset, long length)
/*      */             {
/*  498 */               if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */               {
/*  500 */                 GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.removeListener(this);
/*      */                 
/*  502 */                 return;
/*      */               }
/*      */             }
/*      */           };
/*      */           
/*      */ 
/*      */           public void sync()
/*      */           {
/*  510 */             if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */             {
/*  512 */               return;
/*      */             }
/*      */             
/*  515 */             this.dispatcher.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*  521 */                 if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */                 {
/*  523 */                   return;
/*      */                 }
/*      */                 
/*  526 */                 synchronized (GlobalManagerFileMerger.SameSizeFiles.1.this.lock)
/*      */                 {
/*  528 */                   if (GlobalManagerFileMerger.SameSizeFiles.1.this.current_disk_manager != null)
/*      */                   {
/*  530 */                     GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.removeListener(GlobalManagerFileMerger.SameSizeFiles.1.this.file_listener);
/*      */                   }
/*      */                   else
/*      */                   {
/*  534 */                     return;
/*      */                   }
/*      */                 }
/*      */                 
/*  538 */                 GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.addListener(GlobalManagerFileMerger.SameSizeFiles.1.this.file_listener);
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void peerManagerAdded(final PEPeerManager manager)
/*      */           {
/*  547 */             if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */             {
/*  549 */               return;
/*      */             }
/*      */             
/*  552 */             this.dispatcher.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*  558 */                 if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */                 {
/*  560 */                   return;
/*      */                 }
/*      */                 
/*  563 */                 if (GlobalManagerFileMerger.SameSizeFiles.1.this.pm_removed)
/*      */                 {
/*  565 */                   GlobalManagerFileMerger.SameSizeFiles.this.dl_has_restarted = true;
/*      */                 }
/*      */                 
/*  568 */                 manager.addListener(new PEPeerManagerListenerAdapter()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void pieceCorrupted(PEPeerManager manager, int piece_number)
/*      */                   {
/*      */ 
/*      */ 
/*  576 */                     if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed)
/*      */                     {
/*  578 */                       manager.removeListener(this);
/*      */                       
/*  580 */                       return;
/*      */                     }
/*      */                     
/*  583 */                     GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.access$1800(GlobalManagerFileMerger.SameSizeFiles.1.this.val$file_wrapper, piece_number);
/*      */                   }
/*      */                 });
/*      */                 
/*  587 */                 synchronized (GlobalManagerFileMerger.SameSizeFiles.1.this.lock)
/*      */                 {
/*  589 */                   if (GlobalManagerFileMerger.SameSizeFiles.1.this.current_disk_manager != null)
/*      */                   {
/*  591 */                     GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.removeListener(GlobalManagerFileMerger.SameSizeFiles.1.this.file_listener);
/*      */                   }
/*      */                   
/*  594 */                   GlobalManagerFileMerger.SameSizeFiles.1.this.current_disk_manager = manager.getDiskManager();
/*      */                   
/*  596 */                   if (GlobalManagerFileMerger.SameSizeFiles.1.this.current_disk_manager == null)
/*      */                   {
/*  598 */                     return;
/*      */                   }
/*      */                 }
/*      */                 
/*  602 */                 GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.addListener(GlobalManagerFileMerger.SameSizeFiles.1.this.file_listener);
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void peerManagerRemoved(PEPeerManager manager)
/*      */           {
/*  611 */             this.dispatcher.dispatch(new AERunnable()
/*      */             {
/*      */ 
/*      */               public void runSupport()
/*      */               {
/*      */ 
/*  617 */                 synchronized (GlobalManagerFileMerger.SameSizeFiles.1.this.lock)
/*      */                 {
/*  619 */                   GlobalManagerFileMerger.SameSizeFiles.1.this.pm_removed = true;
/*      */                   
/*  621 */                   if (GlobalManagerFileMerger.SameSizeFiles.1.this.current_disk_manager != null)
/*      */                   {
/*  623 */                     GlobalManagerFileMerger.SameSizeFiles.1.this.val$file.removeListener(GlobalManagerFileMerger.SameSizeFiles.1.this.file_listener);
/*      */                     
/*  625 */                     GlobalManagerFileMerger.SameSizeFiles.1.this.current_disk_manager = null;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void peerAdded(PEPeer peer) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void peerRemoved(PEPeer peer) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void peerManagerWillBeAdded(PEPeerManager manager) {}
/*  650 */         };
/*  651 */         dm.setUserData(this, dmpl);
/*      */         
/*  653 */         dm.addPeerListener(dmpl);
/*      */       }
/*      */       
/*  656 */       this.dl_has_restarted = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean hasDownloadManager(DownloadManager dm)
/*      */     {
/*  665 */       return this.dm_set.contains(dm);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void sync(int tick_count)
/*      */     {
/*  672 */       if (GlobalManagerFileMerger.this.read_write_dispatcher.getQueueSize() > 0)
/*      */       {
/*      */ 
/*      */ 
/*  676 */         return;
/*      */       }
/*      */       
/*  679 */       boolean do_sync = tick_count % 12 == 0;
/*  680 */       boolean do_force = tick_count % 3 == 0;
/*      */       
/*  682 */       if (this.dl_has_restarted)
/*      */       {
/*  684 */         this.dl_has_restarted = false;
/*      */         
/*  686 */         do_sync = true;
/*      */       }
/*      */       
/*  689 */       if ((!do_sync) && (!do_force))
/*      */       {
/*  691 */         return;
/*      */       }
/*      */       
/*  694 */       Set<DiskManagerFileInfo> active = new HashSet();
/*      */       
/*  696 */       int num_incomplete = 0;
/*      */       
/*  698 */       for (DiskManagerFileInfo file : this.files)
/*      */       {
/*  700 */         if (!file.isSkipped())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  705 */           int dl_state = file.getDownloadManager().getState();
/*      */           
/*  707 */           if ((dl_state == 50) || (dl_state == 60))
/*      */           {
/*  709 */             active.add(file);
/*      */             
/*  711 */             if (file.getLength() != file.getDownloaded())
/*      */             {
/*  713 */               num_incomplete++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  718 */       if ((num_incomplete > 0) && (active.size() > 1))
/*      */       {
/*  720 */         boolean rta_active = false;
/*      */         
/*  722 */         for (DiskManagerFileInfo file : active)
/*      */         {
/*  724 */           DownloadManager dm = file.getDownloadManager();
/*      */           
/*  726 */           if (do_sync)
/*      */           {
/*  728 */             GlobalManagerFileMerger.DownloadManagerPeerListenerEx dmpl = (GlobalManagerFileMerger.DownloadManagerPeerListenerEx)dm.getUserData(this);
/*      */             
/*  730 */             if (dmpl != null)
/*      */             {
/*  732 */               dmpl.sync();
/*      */             }
/*      */           }
/*      */           
/*  736 */           PEPeerManager pm = dm.getPeerManager();
/*      */           
/*  738 */           if (pm != null)
/*      */           {
/*  740 */             if (pm.getPiecePicker().getRTAProviders().size() > 0)
/*      */             {
/*  742 */               rta_active = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  747 */         if (rta_active)
/*      */         {
/*  749 */           do_force = false;
/*      */         }
/*      */         
/*  752 */         if (do_force)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*  758 */             for (SameSizeFileWrapper ss_file : this.file_wrappers)
/*      */             {
/*  760 */               DiskManagerFileInfo file = ss_file.getFile();
/*      */               
/*  762 */               if (active.contains(file))
/*      */               {
/*  764 */                 DiskManager dm = ss_file.getDiskManager();
/*  765 */                 PEPeerManager pm = ss_file.getPeerManager();
/*      */                 
/*  767 */                 if (dm != null)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  772 */                   DiskManagerPiece[] pieces = dm.getPieces();
/*      */                   
/*  774 */                   int first_piece = file.getFirstPieceNumber();
/*  775 */                   int last_piece = file.getLastPieceNumber();
/*      */                   
/*  777 */                   long file_length = file.getLength();
/*      */                   
/*  779 */                   long piece_size = dm.getPieceLength();
/*      */                   
/*  781 */                   long file_start_offset = ss_file.getFileByteOffset();
/*      */                   
/*  783 */                   boolean force_done = false;
/*      */                   
/*  785 */                   int[] availability = pm.getAvailability();
/*      */                   long start_in_file;
/*  787 */                   long end_in_file_exclusive; for (int i = first_piece; (i <= last_piece) && (!force_done); i++)
/*      */                   {
/*  789 */                     DiskManagerPiece piece = pieces[i];
/*      */                     
/*  791 */                     if ((piece.isInteresting()) && (availability[i] == 0))
/*      */                     {
/*  793 */                       start_in_file = piece_size * i - file_start_offset;
/*  794 */                       end_in_file_exclusive = start_in_file + piece.getLength();
/*      */                       
/*  796 */                       if (start_in_file < 0L)
/*      */                       {
/*  798 */                         start_in_file = 0L;
/*      */                       }
/*      */                       
/*      */ 
/*  802 */                       if (end_in_file_exclusive > file_length)
/*      */                       {
/*  804 */                         end_in_file_exclusive = file_length;
/*      */                       }
/*      */                       
/*  807 */                       for (SameSizeFileWrapper o_ss_file : this.file_wrappers)
/*      */                       {
/*  809 */                         if ((ss_file != o_ss_file) && (active.contains(o_ss_file.getFile())))
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*  814 */                           if (o_ss_file.forceRange(i, start_in_file, end_in_file_exclusive))
/*      */                           {
/*  816 */                             force_done = true;
/*      */                             
/*  818 */                             break;
/*      */                           } }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           } catch (Throwable e) {
/*  827 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  832 */       if (!do_sync)
/*      */       {
/*  834 */         return;
/*      */       }
/*      */       
/*  837 */       if (!this.completion_logged)
/*      */       {
/*  839 */         boolean all_done = true;
/*  840 */         long total_merged = 0L;
/*      */         
/*  842 */         for (SameSizeFileWrapper ssf : this.file_wrappers)
/*      */         {
/*  844 */           if (!ssf.isSkipped())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  849 */             total_merged += ssf.getMergedByteCount();
/*      */             
/*  851 */             if (!ssf.isComplete())
/*      */             {
/*  853 */               all_done = false;
/*      */             }
/*      */           }
/*      */         }
/*  857 */         if (all_done)
/*      */         {
/*  859 */           this.completion_logged = true;
/*      */           
/*  861 */           if (total_merged > 0L)
/*      */           {
/*  863 */             String msg = "Successfully merged files:\n";
/*      */             
/*  865 */             for (SameSizeFileWrapper file : this.file_wrappers)
/*      */             {
/*  867 */               long merged = file.getMergedByteCount();
/*      */               
/*  869 */               if (merged > 0L)
/*      */               {
/*  871 */                 DownloadManager dm = file.getDownloadManager();
/*      */                 
/*  873 */                 msg = msg + dm.getDisplayName();
/*      */                 
/*  875 */                 if (!dm.getTorrent().isSimpleTorrent())
/*      */                 {
/*  877 */                   msg = msg + " - " + file.getFile().getTorrentFile().getRelativePath();
/*      */                 }
/*      */                 
/*  880 */                 msg = msg + ": " + DisplayFormatters.formatByteCountToKiBEtc(merged) + "\n";
/*      */               }
/*      */             }
/*      */             
/*  884 */             msg = msg + "\nTotal: " + DisplayFormatters.formatByteCountToKiBEtc(total_merged);
/*      */             
/*  886 */             Logger.log(new LogAlert(true, 0, msg));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean sameAs(Set<DiskManagerFileInfo> _others)
/*      */     {
/*  900 */       return this.files.equals(_others);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void abandon(SameSizeFileWrapper failed)
/*      */     {
/*  907 */       destroy();
/*      */       
/*  909 */       String msg = "Abandoned attempt to merge files:\n";
/*      */       
/*  911 */       for (SameSizeFileWrapper file : this.file_wrappers)
/*      */       {
/*  913 */         msg = msg + file.getDownloadManager().getDisplayName() + " - " + file.getFile().getTorrentFile().getRelativePath() + "\n";
/*      */       }
/*  915 */       msg = msg + "\nToo many hash fails in " + failed.getDownloadManager().getDisplayName();
/*      */       
/*  917 */       Logger.log(new LogEvent(LogIDs.CORE, msg));
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
/*      */     private String getInfo()
/*      */     {
/*  931 */       StringBuilder msg = new StringBuilder(1024);
/*      */       
/*  933 */       long size = -1L;
/*      */       
/*  935 */       for (SameSizeFileWrapper file : this.file_wrappers)
/*      */       {
/*  937 */         DiskManagerFileInfo f = file.getFile();
/*      */         
/*  939 */         if (size == -1L)
/*      */         {
/*  941 */           size = f.getLength();
/*      */         }
/*      */         
/*  944 */         msg.append("    ");
/*  945 */         msg.append(file.getDownloadManager().getDisplayName());
/*  946 */         msg.append(": ");
/*  947 */         msg.append(f.getTorrentFile().getRelativePath());
/*  948 */         msg.append("\n");
/*      */       }
/*      */       
/*  951 */       return "Size: " + DisplayFormatters.formatByteCountToKiBEtc(size) + "\n" + msg.toString();
/*      */     }
/*      */     
/*      */ 
/*      */     private void destroy()
/*      */     {
/*  957 */       this.destroyed = true;
/*      */       
/*  959 */       for (DiskManagerFileInfo file : this.files)
/*      */       {
/*  961 */         DownloadManager dm = file.getDownloadManager();
/*      */         
/*  963 */         GlobalManagerFileMerger.DownloadManagerPeerListenerEx dmpl = (GlobalManagerFileMerger.DownloadManagerPeerListenerEx)dm.getUserData(this);
/*      */         
/*  965 */         if (dmpl != null)
/*      */         {
/*  967 */           dm.removePeerListener(dmpl);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private String getString()
/*      */     {
/*  977 */       String str = "";
/*      */       
/*  979 */       long size = -1L;
/*      */       
/*  981 */       for (DiskManagerFileInfo file : this.files)
/*      */       {
/*  983 */         size = file.getLength();
/*      */         
/*  985 */         str = str + (str.length() == 0 ? "" : ", ") + file.getTorrentFile().getRelativePath();
/*      */       }
/*      */       
/*  988 */       str = str + " - length " + size;
/*      */       
/*  990 */       return str;
/*      */     }
/*      */     
/*      */ 
/*      */     private class SameSizeFileWrapper
/*      */     {
/*      */       private final DiskManagerFileInfo file;
/*      */       
/*      */       private final DownloadManager download_manager;
/*      */       
/*      */       private final long file_byte_offset;
/*      */       
/*      */       private final String id;
/*      */       
/*      */       private long merged_byte_counnt;
/*      */       
/*      */       private final boolean[] modified_pieces;
/*      */       
/*      */       private int pieces_completed;
/*      */       
/*      */       private int pieces_corrupted;
/*      */       
/* 1012 */       private int forced_start_piece = 0;
/* 1013 */       private int forced_end_piece = -1;
/*      */       
/*      */ 
/*      */ 
/*      */       private SameSizeFileWrapper(DiskManagerFileInfo _file)
/*      */       {
/* 1019 */         this.file = _file;
/*      */         
/* 1021 */         this.modified_pieces = new boolean[this.file.getNbPieces()];
/*      */         
/* 1023 */         this.download_manager = this.file.getDownloadManager();
/*      */         
/* 1025 */         int file_index = this.file.getIndex();
/*      */         
/* 1027 */         long fbo = 0L;
/*      */         
/* 1029 */         if (file_index > 0)
/*      */         {
/* 1031 */           DiskManagerFileInfo[] f = this.download_manager.getDiskManagerFileInfoSet().getFiles();
/*      */           
/* 1033 */           for (int i = 0; i < file_index; i++)
/*      */           {
/* 1035 */             fbo += f[i].getLength();
/*      */           }
/*      */         }
/*      */         
/*      */         String _id;
/*      */         try
/*      */         {
/* 1042 */           _id = Base32.encode(this.download_manager.getTorrent().getHash()) + "/" + this.file.getIndex();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1046 */           _id = this.download_manager.getDisplayName() + "/" + this.file.getIndex();
/*      */         }
/*      */         
/* 1049 */         this.id = _id;
/*      */         
/* 1051 */         this.file_byte_offset = fbo;
/*      */       }
/*      */       
/*      */ 
/*      */       private DiskManagerFileInfo getFile()
/*      */       {
/* 1057 */         return this.file;
/*      */       }
/*      */       
/*      */ 
/*      */       private boolean isSkipped()
/*      */       {
/* 1063 */         return this.file.isSkipped();
/*      */       }
/*      */       
/*      */ 
/*      */       private boolean isComplete()
/*      */       {
/* 1069 */         return this.file.getLength() == this.file.getDownloaded();
/*      */       }
/*      */       
/*      */ 
/*      */       private DownloadManager getDownloadManager()
/*      */       {
/* 1075 */         return this.download_manager;
/*      */       }
/*      */       
/*      */ 
/*      */       private DiskManager getDiskManager()
/*      */       {
/* 1081 */         return this.file.getDiskManager();
/*      */       }
/*      */       
/*      */ 
/*      */       private PEPeerManager getPeerManager()
/*      */       {
/* 1087 */         return this.download_manager.getPeerManager();
/*      */       }
/*      */       
/*      */ 
/*      */       private long getFileByteOffset()
/*      */       {
/* 1093 */         return this.file_byte_offset;
/*      */       }
/*      */       
/*      */ 
/*      */       private String getID()
/*      */       {
/* 1099 */         return this.id;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private void dataWritten(long offset, long length)
/*      */       {
/* 1109 */         final DiskManager disk_manager = getDiskManager();
/* 1110 */         PEPeerManager peer_manager = getPeerManager();
/*      */         
/* 1112 */         if ((disk_manager == null) || (peer_manager == null))
/*      */         {
/* 1114 */           return;
/*      */         }
/*      */         
/* 1117 */         DiskManagerPiece[] pieces = disk_manager.getPieces();
/*      */         
/* 1119 */         final long piece_length = disk_manager.getPieceLength();
/*      */         
/* 1121 */         long written_start = this.file_byte_offset + offset;
/* 1122 */         long written_end_inclusive = written_start + length - 1L;
/*      */         
/* 1124 */         int first_piece_num = (int)(written_start / piece_length);
/* 1125 */         int last_piece_num = (int)(written_end_inclusive / piece_length);
/*      */         
/* 1127 */         DiskManagerPiece first_piece = pieces[first_piece_num];
/* 1128 */         DiskManagerPiece last_piece = pieces[last_piece_num];
/*      */         
/* 1130 */         int first_block = (int)(written_start % piece_length) / 16384;
/* 1131 */         int last_block = (int)(written_end_inclusive % piece_length) / 16384;
/*      */         
/* 1133 */         if (first_block > 0) {
/* 1134 */           boolean[] written = first_piece.getWritten();
/* 1135 */           if ((first_piece.isDone()) || ((written != null) && (written[(first_block - 1)] != 0))) {
/* 1136 */             first_block--;
/*      */           }
/*      */         }
/* 1139 */         else if (first_piece_num > 0) {
/* 1140 */           DiskManagerPiece prev_piece = pieces[(first_piece_num - 1)];
/* 1141 */           boolean[] written = prev_piece.getWritten();
/* 1142 */           int nb = prev_piece.getNbBlocks();
/*      */           
/* 1144 */           if ((prev_piece.isDone()) || ((written != null) && (written[(nb - 1)] != 0))) {
/* 1145 */             first_piece_num--;
/* 1146 */             first_block = nb - 1;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1151 */         if (last_block < last_piece.getNbBlocks() - 1) {
/* 1152 */           boolean[] written = last_piece.getWritten();
/* 1153 */           if ((last_piece.isDone()) || ((written != null) && (written[(last_block + 1)] != 0))) {
/* 1154 */             last_block++;
/*      */           }
/*      */         }
/* 1157 */         else if (last_piece_num < pieces.length - 1) {
/* 1158 */           DiskManagerPiece next_piece = pieces[(last_piece_num + 1)];
/* 1159 */           boolean[] written = next_piece.getWritten();
/*      */           
/* 1161 */           if ((next_piece.isDone()) || ((written != null) && (written[0] != 0))) {
/* 1162 */             last_piece_num++;
/* 1163 */             last_block = 0;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1171 */         final long avail_start = first_piece_num * piece_length + first_block * 16384;
/* 1172 */         long avail_end_inclusive = last_piece_num * piece_length + last_block * 16384 + pieces[last_piece_num].getBlockSize(last_block) - 1L;
/*      */         
/*      */ 
/*      */ 
/* 1176 */         for (final SameSizeFileWrapper other_file : GlobalManagerFileMerger.SameSizeFiles.this.file_wrappers)
/*      */         {
/* 1178 */           if ((other_file != this) && (!other_file.isSkipped()) && (!other_file.isComplete()))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1183 */             final DiskManager other_disk_manager = other_file.getDiskManager();
/* 1184 */             PEPeerManager other_peer_manager = other_file.getPeerManager();
/*      */             
/* 1186 */             if ((other_disk_manager != null) && (other_peer_manager != null))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1191 */               GlobalManagerFileMerger.this.read_write_dispatcher.dispatch(new AERunnable()
/*      */               {
/*      */ 
/*      */                 public void runSupport()
/*      */                 {
/*      */ 
/* 1197 */                   if (other_file.isComplete())
/*      */                   {
/* 1199 */                     return;
/*      */                   }
/*      */                   
/* 1202 */                   DiskManagerPiece[] other_pieces = other_disk_manager.getPieces();
/*      */                   
/* 1204 */                   long other_piece_length = other_disk_manager.getPieceLength();
/*      */                   
/* 1206 */                   long skew = GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.this.file_byte_offset - other_file.getFileByteOffset();
/*      */                   
/* 1208 */                   if (skew % 16384L == 0L)
/*      */                   {
/*      */ 
/*      */ 
/* 1212 */                     for (long block_start = avail_start; block_start <= piece_length; block_start += 16384L)
/*      */                     {
/* 1214 */                       if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed) {
/*      */                         break;
/*      */                       }
/*      */                       
/*      */ 
/* 1219 */                       int origin_piece_num = (int)(block_start / disk_manager);
/* 1220 */                       int origin_block_num = (int)(block_start % disk_manager / 16384L);
/*      */                       
/* 1222 */                       long target_offset = block_start - skew;
/*      */                       
/* 1224 */                       int target_piece_num = (int)(target_offset / other_piece_length);
/* 1225 */                       int target_block_num = (int)(target_offset % other_piece_length / 16384L);
/*      */                       
/* 1227 */                       DiskManagerPiece origin_piece = this.val$pieces[origin_piece_num];
/* 1228 */                       DiskManagerPiece target_piece = other_pieces[target_piece_num];
/*      */                       
/* 1230 */                       boolean[] written = target_piece.getWritten();
/*      */                       
/* 1232 */                       if ((!target_piece.isDone()) && ((written == null) || (written[target_block_num] == 0)))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1238 */                         if (origin_piece.getBlockSize(origin_block_num) == target_piece.getBlockSize(target_block_num))
/*      */                         {
/* 1240 */                           DirectByteBuffer buffer = this.val$disk_manager.readBlock(origin_piece_num, origin_block_num * 16384, origin_piece.getBlockSize(origin_block_num));
/*      */                           
/* 1242 */                           if (buffer != null)
/*      */                           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1248 */                             written = target_piece.getWritten();
/*      */                             
/* 1250 */                             if ((!target_piece.isDone()) && ((written == null) || (written[target_block_num] == 0)))
/*      */                             {
/*      */ 
/*      */                               try
/*      */                               {
/*      */ 
/*      */ 
/* 1257 */                                 boolean completed_piece = target_piece.getNbWritten() == target_piece.getNbBlocks() - 1;
/*      */                                 
/*      */ 
/*      */ 
/* 1261 */                                 if (other_file.writeBlock(target_piece_num, target_block_num, buffer))
/*      */                                 {
/* 1263 */                                   buffer = null;
/*      */                                   
/* 1265 */                                   if (completed_piece)
/*      */                                   {
/* 1267 */                                     GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.access$3008(GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.this);
/*      */                                     
/* 1269 */                                     if (GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.this.pieces_completed < 5) {
/*      */                                       try
/*      */                                       {
/* 1272 */                                         Thread.sleep(500L);
/*      */ 
/*      */                                       }
/*      */                                       catch (Throwable e) {}
/*      */                                     }
/*      */                                     
/*      */                                   }
/*      */                                   
/*      */ 
/*      */                                 }
/*      */                                 else
/*      */                                 {
/*      */ 
/* 1285 */                                   if (buffer == null)
/*      */                                     break;
/* 1287 */                                   buffer.returnToPool(); break;
/*      */                                 }
/*      */                               }
/*      */                               finally
/*      */                               {
/* 1285 */                                 if (buffer != null)
/*      */                                 {
/* 1287 */                                   buffer.returnToPool();
/*      */                                 }
/*      */                               }
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   else {
/* 1297 */                     DirectByteBuffer prev_block = null;
/* 1298 */                     int prev_block_pn = 0;
/* 1299 */                     int prev_block_bn = 0;
/*      */                     try
/*      */                     {
/* 1302 */                       for (long block_start = avail_start; block_start <= piece_length; block_start += 16384L)
/*      */                       {
/* 1304 */                         if (GlobalManagerFileMerger.SameSizeFiles.this.destroyed) {
/*      */                           break;
/*      */                         }
/*      */                         
/*      */ 
/* 1309 */                         long origin_start = block_start;
/*      */                         
/* 1311 */                         long target_offset = origin_start - skew;
/*      */                         
/* 1313 */                         target_offset = (target_offset + 16384L - 1L) / 16384L * 16384L;
/*      */                         
/* 1315 */                         long origin_offset = target_offset + skew;
/*      */                         
/* 1317 */                         int target_piece_num = (int)(target_offset / other_piece_length);
/* 1318 */                         int target_block_num = (int)(target_offset % other_piece_length / 16384L);
/*      */                         
/* 1320 */                         DiskManagerPiece target_piece = other_pieces[target_piece_num];
/*      */                         
/* 1322 */                         boolean[] target_written = target_piece.getWritten();
/*      */                         
/* 1324 */                         if ((!target_piece.isDone()) && ((target_written == null) || ((target_block_num < target_written.length) && (target_written[target_block_num] == 0))))
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1330 */                           int target_block_size = target_piece.getBlockSize(target_block_num);
/*      */                           
/* 1332 */                           if ((origin_offset >= GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.this.file_byte_offset) && (origin_offset + target_block_size <= piece_length + 1L))
/*      */                           {
/*      */ 
/* 1335 */                             int origin1_piece_number = (int)(origin_start / disk_manager);
/* 1336 */                             int origin1_block_num = (int)(origin_start % disk_manager / 16384L);
/*      */                             
/* 1338 */                             DiskManagerPiece origin1_piece = this.val$pieces[origin1_piece_number];
/*      */                             
/* 1340 */                             if (origin1_piece.isWritten(origin1_block_num))
/*      */                             {
/*      */ 
/*      */ 
/*      */ 
/* 1345 */                               DirectByteBuffer read_block1 = null;
/* 1346 */                               DirectByteBuffer read_block2 = null;
/* 1347 */                               DirectByteBuffer write_block = null;
/*      */                               try
/*      */                               {
/* 1350 */                                 if ((prev_block != null) && (prev_block_pn == origin1_piece_number) && (prev_block_bn == origin1_block_num))
/*      */                                 {
/*      */ 
/*      */ 
/* 1354 */                                   read_block1 = prev_block;
/* 1355 */                                   prev_block = null;
/*      */                                 }
/*      */                                 else
/*      */                                 {
/* 1359 */                                   read_block1 = this.val$disk_manager.readBlock(origin1_piece_number, origin1_block_num * 16384, origin1_piece.getBlockSize(origin1_block_num));
/*      */                                   
/* 1361 */                                   if (read_block1 == null)
/*      */                                   {
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1483 */                                     if (read_block1 != null)
/*      */                                     {
/* 1485 */                                       read_block1.returnToPool();
/*      */                                     }
/*      */                                     
/* 1488 */                                     if (read_block2 != null)
/*      */                                     {
/* 1490 */                                       read_block2.returnToPool();
/*      */                                     }
/*      */                                     
/* 1493 */                                     if (write_block == null)
/*      */                                       continue;
/* 1495 */                                     write_block.returnToPool(); continue;
/*      */                                   }
/*      */                                 }
/* 1367 */                                 write_block = DirectByteBufferPool.getBuffer((byte)1, target_block_size);
/*      */                                 
/* 1369 */                                 byte SS = 1;
/*      */                                 
/* 1371 */                                 int delta = (int)(origin_offset - origin_start);
/*      */                                 
/* 1373 */                                 read_block1.position((byte)1, delta);
/*      */                                 
/*      */ 
/*      */ 
/*      */ 
/* 1378 */                                 int rb1_rem = read_block1.remaining((byte)1);
/*      */                                 
/* 1380 */                                 if (rb1_rem > target_block_size)
/*      */                                 {
/* 1382 */                                   read_block1.limit((byte)1, delta + target_block_size);
/*      */                                 }
/*      */                                 
/* 1385 */                                 write_block.limit((byte)1, read_block1.remaining((byte)1));
/*      */                                 
/* 1387 */                                 write_block.put((byte)1, read_block1);
/*      */                                 
/* 1389 */                                 write_block.limit((byte)1, target_block_size);
/*      */                                 
/* 1391 */                                 read_block1.returnToPool();
/*      */                                 
/* 1393 */                                 read_block1 = null;
/*      */                                 
/* 1395 */                                 if (write_block.hasRemaining((byte)1))
/*      */                                 {
/* 1397 */                                   int origin2_piece_number = origin1_piece_number;
/* 1398 */                                   int origin2_block_num = origin1_block_num + 1;
/*      */                                   
/* 1400 */                                   if (origin2_block_num >= origin1_piece.getNbBlocks())
/*      */                                   {
/* 1402 */                                     origin2_piece_number++;
/*      */                                     
/* 1404 */                                     origin2_block_num = 0;
/*      */                                   }
/*      */                                   
/* 1407 */                                   if (origin2_piece_number < this.val$pieces.length)
/*      */                                   {
/* 1409 */                                     DiskManagerPiece origin2_piece = this.val$pieces[origin2_piece_number];
/*      */                                     
/* 1411 */                                     if (!origin2_piece.isWritten(origin2_block_num))
/*      */                                     {
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
/* 1483 */                                       if (read_block1 != null)
/*      */                                       {
/* 1485 */                                         read_block1.returnToPool();
/*      */                                       }
/*      */                                       
/* 1488 */                                       if (read_block2 != null)
/*      */                                       {
/* 1490 */                                         read_block2.returnToPool();
/*      */                                       }
/*      */                                       
/* 1493 */                                       if (write_block == null)
/*      */                                         continue;
/* 1495 */                                       write_block.returnToPool(); continue;
/*      */                                     }
/* 1416 */                                     read_block2 = this.val$disk_manager.readBlock(origin2_piece_number, origin2_block_num * 16384, origin2_piece.getBlockSize(origin2_block_num));
/*      */                                     
/* 1418 */                                     if (read_block2 == null)
/*      */                                     {
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
/* 1483 */                                       if (read_block1 != null)
/*      */                                       {
/* 1485 */                                         read_block1.returnToPool();
/*      */                                       }
/*      */                                       
/* 1488 */                                       if (read_block2 != null)
/*      */                                       {
/* 1490 */                                         read_block2.returnToPool();
/*      */                                       }
/*      */                                       
/* 1493 */                                       if (write_block == null)
/*      */                                         continue;
/* 1495 */                                       write_block.returnToPool(); continue;
/*      */                                     }
/* 1423 */                                     read_block2.limit((byte)1, write_block.remaining((byte)1));
/*      */                                     
/* 1425 */                                     write_block.put((byte)1, read_block2);
/*      */                                     
/* 1427 */                                     read_block2.position((byte)1, 0);
/* 1428 */                                     read_block2.limit((byte)1, read_block2.capacity((byte)1));
/*      */                                     
/* 1430 */                                     prev_block = read_block2;
/* 1431 */                                     prev_block_pn = origin2_piece_number;
/* 1432 */                                     prev_block_bn = origin2_block_num;
/*      */                                     
/* 1434 */                                     read_block2 = null;
/*      */                                   }
/*      */                                 }
/*      */                                 
/* 1438 */                                 if (write_block.hasRemaining((byte)1))
/*      */                                 {
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1483 */                                   if (read_block1 != null)
/*      */                                   {
/* 1485 */                                     read_block1.returnToPool();
/*      */                                   }
/*      */                                   
/* 1488 */                                   if (read_block2 != null)
/*      */                                   {
/* 1490 */                                     read_block2.returnToPool();
/*      */                                   }
/*      */                                   
/* 1493 */                                   if (write_block != null)
/*      */                                   {
/* 1495 */                                     write_block.returnToPool();
/*      */                                   }
/*      */                                 }
/*      */                                 else
/*      */                                 {
/* 1444 */                                   write_block.flip((byte)1);
/*      */                                   
/* 1446 */                                   target_written = target_piece.getWritten();
/*      */                                   
/* 1448 */                                   if ((!target_piece.isDone()) && ((target_written == null) || (target_written[target_block_num] == 0)))
/*      */                                   {
/*      */ 
/*      */ 
/*      */ 
/* 1453 */                                     boolean completed_piece = target_piece.getNbWritten() == target_piece.getNbBlocks() - 1;
/*      */                                     
/*      */ 
/*      */ 
/* 1457 */                                     if (other_file.writeBlock(target_piece_num, target_block_num, write_block))
/*      */                                     {
/* 1459 */                                       write_block = null;
/*      */                                       
/* 1461 */                                       if (completed_piece)
/*      */                                       {
/* 1463 */                                         GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.access$3008(GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.this);
/*      */                                         
/* 1465 */                                         if (GlobalManagerFileMerger.SameSizeFiles.SameSizeFileWrapper.this.pieces_completed < 5) {
/*      */                                           try
/*      */                                           {
/* 1468 */                                             Thread.sleep(500L);
/*      */ 
/*      */ 
/*      */                                           }
/*      */                                           catch (Throwable e) {}
/*      */ 
/*      */                                         }
/*      */                                         
/*      */                                       }
/*      */                                       
/*      */ 
/*      */                                     }
/*      */                                     else
/*      */                                     {
/*      */ 
/* 1483 */                                       if (read_block1 != null)
/*      */                                       {
/* 1485 */                                         read_block1.returnToPool();
/*      */                                       }
/*      */                                       
/* 1488 */                                       if (read_block2 != null)
/*      */                                       {
/* 1490 */                                         read_block2.returnToPool();
/*      */                                       }
/*      */                                       
/* 1493 */                                       if (write_block == null)
/*      */                                         break;
/* 1495 */                                       write_block.returnToPool(); break;
/*      */                                     }
/*      */                                   }
/*      */                                 }
/*      */                               }
/*      */                               finally
/*      */                               {
/* 1483 */                                 if (read_block1 != null)
/*      */                                 {
/* 1485 */                                   read_block1.returnToPool();
/*      */                                 }
/*      */                                 
/* 1488 */                                 if (read_block2 != null)
/*      */                                 {
/* 1490 */                                   read_block2.returnToPool();
/*      */                                 }
/*      */                                 
/*      */                               }
/*      */                               
/*      */                             }
/*      */                             
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     finally
/*      */                     {
/* 1503 */                       if (prev_block != null)
/*      */                       {
/* 1505 */                         prev_block.returnToPool();
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private boolean writeBlock(int piece_number, int block_number, DirectByteBuffer buffer)
/*      */       {
/* 1520 */         PEPeerManager pm = getPeerManager();
/*      */         
/* 1522 */         if (pm == null)
/*      */         {
/* 1524 */           return false;
/*      */         }
/*      */         
/* 1527 */         this.modified_pieces[(piece_number - this.file.getFirstPieceNumber())] = true;
/*      */         
/* 1529 */         int length = buffer.remaining((byte)1);
/*      */         
/* 1531 */         synchronized (GlobalManagerFileMerger.merged_data_lock)
/*      */         {
/* 1533 */           DownloadManagerState dms = this.download_manager.getDownloadState();
/*      */           
/* 1535 */           long merged = dms.getLongAttribute("mergedata");
/*      */           
/* 1537 */           merged += length;
/*      */           
/* 1539 */           dms.setLongAttribute("mergedata", merged);
/*      */         }
/*      */         
/* 1542 */         this.merged_byte_counnt += length;
/*      */         
/* 1544 */         pm.writeBlock(piece_number, block_number * 16384, buffer, "block-xfer from " + getID(), true);
/*      */         
/* 1546 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void pieceCorrupt(int piece_number)
/*      */       {
/* 1553 */         int first_piece = this.file.getFirstPieceNumber();
/*      */         
/* 1555 */         if ((piece_number >= first_piece) && (piece_number <= this.file.getLastPieceNumber()))
/*      */         {
/* 1557 */           if (this.modified_pieces[(piece_number - first_piece)] != 0)
/*      */           {
/* 1559 */             this.pieces_corrupted += 1;
/*      */             
/* 1561 */             if (this.pieces_corrupted >= 3)
/*      */             {
/* 1563 */               GlobalManagerFileMerger.SameSizeFiles.this.abandon(this);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       private long getMergedByteCount()
/*      */       {
/* 1572 */         return this.merged_byte_counnt;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       private boolean forceRange(int for_piece, long start_in_file, long end_in_file_exclusive)
/*      */       {
/* 1581 */         DiskManager dm = getDiskManager();
/* 1582 */         PEPeerManager pm = getPeerManager();
/*      */         
/* 1584 */         if ((dm == null) || (pm == null))
/*      */         {
/* 1586 */           return false;
/*      */         }
/*      */         
/* 1589 */         int[] availability = pm.getAvailability();
/*      */         
/* 1591 */         long start_in_torrent = start_in_file + this.file_byte_offset;
/* 1592 */         long end_in_torrent_inclusive = end_in_file_exclusive + this.file_byte_offset - 1L;
/*      */         
/* 1594 */         int piece_size = dm.getPieceLength();
/*      */         
/* 1596 */         int first_piece = (int)(start_in_torrent / piece_size);
/* 1597 */         int last_piece = (int)(end_in_torrent_inclusive / piece_size);
/*      */         
/* 1599 */         DiskManagerPiece[] pieces = dm.getPieces();
/*      */         
/* 1601 */         boolean forceable = false;
/*      */         
/* 1603 */         for (int i = first_piece; i <= last_piece; i++)
/*      */         {
/* 1605 */           DiskManagerPiece piece = pieces[i];
/*      */           
/* 1607 */           if (!piece.isDone())
/*      */           {
/* 1609 */             if ((availability[piece.getPieceNumber()] > 0) && (piece.isInteresting()))
/*      */             {
/*      */ 
/* 1612 */               forceable = true;
/*      */               
/* 1614 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1619 */         if (forceable)
/*      */         {
/* 1621 */           if ((this.forced_start_piece != first_piece) || (this.forced_end_piece != last_piece))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1627 */             PiecePicker pp = pm.getPiecePicker();
/*      */             
/* 1629 */             if ((this.forced_start_piece != first_piece) || (this.forced_end_piece != last_piece))
/*      */             {
/* 1631 */               for (int i = this.forced_start_piece; i <= this.forced_end_piece; i++)
/*      */               {
/* 1633 */                 DiskManagerPiece piece = pieces[i];
/*      */                 
/* 1635 */                 pp.setForcePiece(piece.getPieceNumber(), false);
/*      */               }
/*      */             }
/*      */             
/* 1639 */             this.forced_start_piece = first_piece;
/* 1640 */             this.forced_end_piece = last_piece;
/*      */             
/* 1642 */             for (int i = first_piece; i <= last_piece; i++)
/*      */             {
/* 1644 */               DiskManagerPiece piece = pieces[i];
/*      */               
/* 1646 */               if (!piece.isDone())
/*      */               {
/* 1648 */                 pp.setForcePiece(i, true);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1655 */           return true;
/*      */         }
/*      */         
/*      */ 
/* 1659 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/impl/GlobalManagerFileMerger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */