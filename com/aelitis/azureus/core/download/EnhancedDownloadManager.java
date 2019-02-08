/*      */ package com.aelitis.azureus.core.download;
/*      */ 
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PieceRTAProvider;
/*      */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.core.util.average.Average;
/*      */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*      */ import com.aelitis.azureus.util.ConstantsVuze;
/*      */ import java.io.PrintStream;
/*      */ import java.util.List;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerAdapter;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.RealTimeInfo;
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
/*      */ 
/*      */ 
/*      */ public class EnhancedDownloadManager
/*      */ {
/*   52 */   public static int DEFAULT_MINIMUM_INITIAL_BUFFER_SECS_FOR_ETA = 30;
/*      */   public static int MINIMUM_INITIAL_BUFFER_SECS;
/*      */   public static final int REACTIVATE_PROVIDER_PERIOD = 5000;
/*      */   public static final int REACTIVATE_PROVIDER_PERIOD_TICKS = 5;
/*      */   public static final int LOG_PROG_STATS_PERIOD = 10000;
/*      */   public static final int LOG_PROG_STATS_TICKS = 10;
/*      */   
/*   59 */   static { COConfigurationManager.addAndFireParameterListeners(new String[] { "filechannel.rt.buffer.millis" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   69 */         int channel_buffer_millis = COConfigurationManager.getIntParameter("filechannel.rt.buffer.millis");
/*      */         
/*   71 */         EnhancedDownloadManager.MINIMUM_INITIAL_BUFFER_SECS = 2 * channel_buffer_millis / 1000;
/*      */       }
/*      */     }); }
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int content_stream_bps_min_increase_ratio = 5;
/*      */   
/*      */ 
/*      */   private static final int content_stream_bps_max_increase_ratio = 8;
/*      */   
/*      */ 
/*      */   private DownloadManagerEnhancer enhancer;
/*      */   
/*      */ 
/*      */   private DownloadManager download_manager;
/*      */   
/*      */ 
/*      */   private boolean explicit_progressive;
/*      */   
/*      */ 
/*      */   private volatile PiecePicker current_piece_pickler;
/*      */   
/*      */ 
/*   95 */   private volatile boolean progressive_active = false;
/*      */   
/*      */   private long content_min_delivery_bps;
/*      */   
/*      */   private int minimum_initial_buffer_secs_for_eta;
/*      */   
/*  101 */   private bufferETAProvider buffer_provider = new bufferETAProvider();
/*      */   
/*      */ 
/*      */   private progressiveStats progressive_stats;
/*      */   
/*      */ 
/*      */   private boolean marked_active;
/*      */   
/*      */ 
/*      */   private boolean destroyed;
/*      */   
/*      */ 
/*      */   private DownloadManagerListener dmListener;
/*      */   
/*      */ 
/*      */   private EnhancedDownloadManagerFile[] enhanced_files;
/*      */   
/*      */ 
/*      */ 
/*      */   protected EnhancedDownloadManager(DownloadManagerEnhancer _enhancer, DownloadManager _download_manager)
/*      */   {
/*  122 */     this.enhancer = _enhancer;
/*  123 */     this.download_manager = _download_manager;
/*      */     
/*  125 */     DiskManagerFileInfo[] files = this.download_manager.getDiskManagerFileInfo();
/*      */     
/*  127 */     this.minimum_initial_buffer_secs_for_eta = DEFAULT_MINIMUM_INITIAL_BUFFER_SECS_FOR_ETA;
/*      */     
/*  129 */     this.enhanced_files = new EnhancedDownloadManagerFile[files.length];
/*      */     
/*  131 */     long offset = 0L;
/*      */     
/*  133 */     for (int i = 0; i < files.length; i++)
/*      */     {
/*  135 */       DiskManagerFileInfo f = files[i];
/*      */       
/*  137 */       this.enhanced_files[i] = new EnhancedDownloadManagerFile(f, offset);
/*      */       
/*  139 */       offset += f.getLength();
/*      */     }
/*      */     
/*      */ 
/*  143 */     int primary_index = getPrimaryFileIndex();
/*      */     
/*  145 */     EnhancedDownloadManagerFile primary_file = this.enhanced_files[primary_index];
/*      */     
/*  147 */     this.progressive_stats = createProgressiveStats(this.download_manager, primary_file);
/*      */     
/*  149 */     this.download_manager.addPeerListener(new DownloadManagerPeerListener()
/*      */     {
/*      */       public void peerManagerWillBeAdded(PEPeerManager peer_manager) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void peerManagerAdded(PEPeerManager manager)
/*      */       {
/*  162 */         synchronized (EnhancedDownloadManager.this)
/*      */         {
/*  164 */           EnhancedDownloadManager.this.current_piece_pickler = manager.getPiecePicker();
/*      */           
/*  166 */           if ((EnhancedDownloadManager.this.progressive_active) && (EnhancedDownloadManager.this.current_piece_pickler != null))
/*      */           {
/*  168 */             EnhancedDownloadManager.this.buffer_provider.activate(EnhancedDownloadManager.this.current_piece_pickler);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void peerManagerRemoved(PEPeerManager manager)
/*      */       {
/*  177 */         synchronized (EnhancedDownloadManager.this)
/*      */         {
/*  179 */           if (EnhancedDownloadManager.this.progressive_active)
/*      */           {
/*  181 */             EnhancedDownloadManager.this.setProgressiveMode(false);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void peerAdded(PEPeer peer) {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void peerRemoved(PEPeer peer) {}
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPrimaryFileIndex()
/*      */   {
/*  201 */     DiskManagerFileInfo info = this.download_manager.getDownloadState().getPrimaryFile();
/*      */     
/*  203 */     if (info == null) {
/*  204 */       return -1;
/*      */     }
/*  206 */     return info.getIndex();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setExplicitProgressive(int min_initial_buffer_secs, long min_bps, int file_index)
/*      */   {
/*  215 */     if ((file_index >= 0) && (file_index < this.enhanced_files.length))
/*      */     {
/*  217 */       this.explicit_progressive = true;
/*      */       
/*  219 */       this.minimum_initial_buffer_secs_for_eta = min_initial_buffer_secs;
/*      */       
/*  221 */       this.content_min_delivery_bps = min_bps;
/*      */       
/*  223 */       EnhancedDownloadManagerFile primary_file = this.enhanced_files[file_index];
/*      */       
/*  225 */       this.progressive_stats = createProgressiveStats(this.download_manager, primary_file);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  232 */     return this.download_manager.getDisplayName();
/*      */   }
/*      */   
/*      */ 
/*      */   public byte[] getHash()
/*      */   {
/*  238 */     TOTorrent t = this.download_manager.getTorrent();
/*      */     
/*  240 */     if (t == null)
/*      */     {
/*  242 */       return null;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  247 */       return t.getHash();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  251 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPlatform()
/*      */   {
/*  258 */     TOTorrent torrent = this.download_manager.getTorrent();
/*      */     
/*  260 */     if (torrent != null)
/*      */     {
/*  262 */       return PlatformTorrentUtils.isContent(torrent, true);
/*      */     }
/*      */     
/*  265 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public EnhancedDownloadManagerFile[] getFiles()
/*      */   {
/*  271 */     return this.enhanced_files;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getTargetSpeed()
/*      */   {
/*  277 */     long target_speed = this.progressive_active ? this.progressive_stats.getStreamBytesPerSecondMax() : this.content_min_delivery_bps;
/*      */     
/*  279 */     if (target_speed < this.content_min_delivery_bps)
/*      */     {
/*  281 */       target_speed = this.content_min_delivery_bps;
/*      */     }
/*      */     
/*  284 */     return target_speed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean updateStats(int tick_count)
/*      */   {
/*  291 */     return updateProgressiveStats(tick_count);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsProgressiveMode()
/*      */   {
/*  299 */     TOTorrent torrent = this.download_manager.getTorrent();
/*      */     
/*  301 */     if (torrent == null)
/*      */     {
/*  303 */       return false;
/*      */     }
/*      */     
/*  306 */     return (this.enhancer.isProgressiveAvailable()) && ((PlatformTorrentUtils.isContentProgressive(torrent)) || (this.explicit_progressive));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void prepareForProgressiveMode(boolean active)
/*      */   {
/*  314 */     this.enhancer.prepareForProgressiveMode(this.download_manager, active);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean setProgressiveMode(boolean active)
/*      */   {
/*  321 */     return setProgressiveMode(active, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean setProgressiveMode(boolean active, boolean switching_progressive_downloads)
/*      */   {
/*  329 */     TOTorrent torrent = this.download_manager.getTorrent();
/*      */     
/*  331 */     DiskManagerFileInfo primaryFile = this.download_manager.getDownloadState().getPrimaryFile();
/*      */     
/*  333 */     if ((torrent == null) || (primaryFile == null))
/*      */     {
/*  335 */       return false;
/*      */     }
/*      */     
/*  338 */     EnhancedDownloadManagerFile enhanced_file = this.enhanced_files[primaryFile.getIndex()];
/*      */     
/*  340 */     synchronized (this)
/*      */     {
/*  342 */       if (this.progressive_active == active)
/*      */       {
/*  344 */         return true;
/*      */       }
/*      */       
/*  347 */       if ((active) && (!supportsProgressiveMode()))
/*      */       {
/*  349 */         Debug.out("Attempt to set progress mode on non-progressible content - " + getName());
/*      */         
/*  351 */         return false;
/*      */       }
/*      */       
/*  354 */       log("Progressive mode changed to " + active);
/*      */       
/*  356 */       GlobalManager gm = this.download_manager.getGlobalManager();
/*  357 */       if (active) {
/*  358 */         if (this.dmListener == null) {
/*  359 */           this.dmListener = new DownloadManagerAdapter() {
/*      */             public void downloadComplete(DownloadManager manager) {
/*  361 */               EnhancedDownloadManager.this.enhancer.resume();
/*      */             }
/*      */           };
/*      */         }
/*  365 */         this.download_manager.addListener(this.dmListener);
/*      */         
/*      */ 
/*      */ 
/*  369 */         Object[] dms = gm.getDownloadManagers().toArray();
/*  370 */         for (int i = 0; i < dms.length; i++) {
/*  371 */           DownloadManager dmCheck = (DownloadManager)dms[i];
/*  372 */           if (!dmCheck.equals(this.download_manager))
/*      */           {
/*      */ 
/*      */ 
/*  376 */             if (!dmCheck.isDownloadComplete(false)) {
/*  377 */               int state = dmCheck.getState();
/*  378 */               if ((state == 50) || (state == 75))
/*      */               {
/*  380 */                 this.enhancer.pause(dmCheck);
/*      */               }
/*  382 */               EnhancedDownloadManager edmCheck = this.enhancer.getEnhancedDownload(dmCheck);
/*  383 */               if ((edmCheck != null) && (edmCheck.getProgressiveMode()))
/*  384 */                 edmCheck.setProgressiveMode(false, true);
/*      */             }
/*      */           }
/*      */         }
/*  388 */         if (this.download_manager.isPaused()) {
/*  389 */           this.enhancer.resume(this.download_manager);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  394 */         if (this.download_manager.getState() == 70) {
/*  395 */           this.download_manager.setStateWaiting();
/*      */         }
/*      */         
/*  398 */         if (this.download_manager.getPosition() != 1) {
/*  399 */           this.download_manager.getGlobalManager().moveTo(this.download_manager, 1);
/*      */         }
/*      */       } else {
/*  402 */         this.download_manager.removeListener(this.dmListener);
/*  403 */         if (!switching_progressive_downloads) {
/*  404 */           this.enhancer.resume();
/*      */         }
/*      */       }
/*      */       
/*  408 */       this.progressive_active = active;
/*      */       
/*  410 */       if (this.progressive_active)
/*      */       {
/*  412 */         this.enhancer.progressiveActivated();
/*      */       }
/*      */       
/*  415 */       if (this.current_piece_pickler != null)
/*      */       {
/*  417 */         if (this.progressive_active)
/*      */         {
/*  419 */           this.buffer_provider.activate(this.current_piece_pickler);
/*      */           
/*  421 */           this.progressive_stats.update(0);
/*      */         }
/*      */         else
/*      */         {
/*  425 */           this.buffer_provider.deactivate(this.current_piece_pickler);
/*      */           
/*  427 */           this.progressive_stats = createProgressiveStats(this.download_manager, enhanced_file);
/*      */         }
/*      */       }
/*      */       else {
/*  431 */         this.progressive_stats = createProgressiveStats(this.download_manager, enhanced_file);
/*      */       }
/*      */       
/*  434 */       if (!switching_progressive_downloads)
/*      */       {
/*  436 */         if (active)
/*      */         {
/*  438 */           RealTimeInfo.setProgressiveActive(this.progressive_stats.getStreamBytesPerSecondMax());
/*      */         }
/*      */         else
/*      */         {
/*  442 */           RealTimeInfo.setProgressiveInactive();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  447 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getProgressiveMode()
/*      */   {
/*  454 */     return this.progressive_active;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getProgressivePlayETA()
/*      */   {
/*  460 */     progressiveStats stats = getProgressiveStats();
/*      */     
/*  462 */     if (stats == null)
/*      */     {
/*  464 */       return Long.MAX_VALUE;
/*      */     }
/*      */     
/*  467 */     long eta = stats.getETA();
/*      */     
/*  469 */     return eta;
/*      */   }
/*      */   
/*      */ 
/*      */   protected progressiveStats getProgressiveStats()
/*      */   {
/*  475 */     synchronized (this)
/*      */     {
/*  477 */       if (this.progressive_stats == null)
/*      */       {
/*  479 */         return null;
/*      */       }
/*      */       
/*  482 */       return this.progressive_stats.getCopy();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected progressiveStats createProgressiveStats(DownloadManager dm, EnhancedDownloadManagerFile file)
/*      */   {
/*  491 */     return new progressiveStatsCommon(dm, file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean updateProgressiveStats(int tick_count)
/*      */   {
/*  498 */     if (!this.progressive_active)
/*      */     {
/*  500 */       return false;
/*      */     }
/*      */     
/*  503 */     synchronized (this)
/*      */     {
/*  505 */       if ((!this.progressive_active) || (this.progressive_stats == null))
/*      */       {
/*  507 */         return false;
/*      */       }
/*      */       
/*  510 */       if (tick_count % 5 == 0)
/*      */       {
/*  512 */         PiecePicker piece_picker = this.current_piece_pickler;
/*      */         
/*  514 */         if (piece_picker != null)
/*      */         {
/*  516 */           this.buffer_provider.checkActivation(piece_picker);
/*      */         }
/*      */       }
/*      */       
/*  520 */       this.progressive_stats.update(tick_count);
/*      */       
/*  522 */       long current_max = this.progressive_stats.getStreamBytesPerSecondMax();
/*      */       
/*  524 */       if (RealTimeInfo.getProgressiveActiveBytesPerSec() != current_max)
/*      */       {
/*  526 */         RealTimeInfo.setProgressiveActive(current_max);
/*      */       }
/*      */     }
/*      */     
/*  530 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setRTA(boolean active)
/*      */   {
/*  537 */     synchronized (this)
/*      */     {
/*  539 */       if ((this.marked_active) && (!active))
/*      */       {
/*  541 */         this.marked_active = false;
/*      */         
/*  543 */         RealTimeInfo.removeRealTimeTask();
/*      */       }
/*      */       
/*  546 */       if (this.destroyed)
/*      */       {
/*  548 */         return;
/*      */       }
/*      */       
/*  551 */       if ((!this.marked_active) && (active))
/*      */       {
/*  553 */         this.marked_active = true;
/*      */         
/*  555 */         RealTimeInfo.addRealTimeTask();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getContiguousAvailableBytes(int file_index, long file_start_offset, long stop_counting_after)
/*      */   {
/*  566 */     if ((file_index < 0) || (file_index >= this.enhanced_files.length))
/*      */     {
/*  568 */       return -1L;
/*      */     }
/*      */     
/*  571 */     EnhancedDownloadManagerFile efile = this.enhanced_files[file_index];
/*      */     
/*  573 */     DiskManagerFileInfo file = efile.getFile();
/*      */     
/*  575 */     DiskManager dm = this.download_manager.getDiskManager();
/*      */     
/*  577 */     if (dm == null)
/*      */     {
/*  579 */       if (file.getDownloaded() == file.getLength())
/*      */       {
/*  581 */         return file.getLength() - file_start_offset;
/*      */       }
/*      */       
/*  584 */       return -1L;
/*      */     }
/*      */     
/*  587 */     int piece_size = dm.getPieceLength();
/*      */     
/*  589 */     long start_index = efile.getByteOffestInTorrent() + file_start_offset;
/*      */     
/*      */ 
/*  592 */     int first_piece_index = (int)(start_index / piece_size);
/*  593 */     int first_piece_offset = (int)(start_index % piece_size);
/*  594 */     int last_piece_index = file.getLastPieceNumber();
/*      */     
/*  596 */     DiskManagerPiece[] pieces = dm.getPieces();
/*      */     
/*  598 */     DiskManagerPiece first_piece = pieces[first_piece_index];
/*      */     
/*  600 */     long available = 0L;
/*      */     
/*  602 */     if (!first_piece.isDone())
/*      */     {
/*  604 */       boolean[] blocks = first_piece.getWritten();
/*      */       
/*  606 */       if (blocks == null)
/*      */       {
/*  608 */         if (first_piece.isDone())
/*      */         {
/*  610 */           available = first_piece.getLength() - first_piece_offset;
/*      */         }
/*      */       }
/*      */       else {
/*  614 */         int piece_offset = 0;
/*      */         
/*  616 */         for (int j = 0; j < blocks.length; j++)
/*      */         {
/*  618 */           if (blocks[j] == 0)
/*      */             break;
/*  620 */           int block_size = first_piece.getBlockSize(j);
/*      */           
/*  622 */           piece_offset += block_size;
/*      */           
/*  624 */           if (available == 0L)
/*      */           {
/*  626 */             if (piece_offset > first_piece_offset)
/*      */             {
/*  628 */               available = piece_offset - first_piece_offset;
/*      */             }
/*      */           }
/*      */           else {
/*  632 */             available += block_size;
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */     }
/*      */     else
/*      */     {
/*  642 */       available = first_piece.getLength() - first_piece_offset;
/*      */       
/*  644 */       for (int i = first_piece_index + 1; i <= last_piece_index; i++)
/*      */       {
/*  646 */         if ((stop_counting_after > 0L) && (available >= stop_counting_after)) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*  651 */         DiskManagerPiece piece = pieces[i];
/*      */         
/*  653 */         if (piece.isDone())
/*      */         {
/*  655 */           available += piece.getLength();
/*      */         }
/*      */         else
/*      */         {
/*  659 */           boolean[] blocks = piece.getWritten();
/*      */           
/*  661 */           if (blocks == null)
/*      */           {
/*  663 */             if (!piece.isDone())
/*      */               break;
/*  665 */             available += piece.getLength(); break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  673 */           for (int j = 0; j < blocks.length; j++)
/*      */           {
/*  675 */             if (blocks[j] == 0)
/*      */               break;
/*  677 */             available += piece.getBlockSize(j);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  686 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  691 */     long max_available = file.getLength() - file_start_offset;
/*      */     
/*  693 */     if (available > max_available)
/*      */     {
/*  695 */       available = max_available;
/*      */     }
/*      */     
/*  698 */     return available;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadManager getDownloadManager()
/*      */   {
/*  705 */     return this.download_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void destroy()
/*      */   {
/*  711 */     synchronized (this)
/*      */     {
/*  713 */       setRTA(false);
/*      */       
/*  715 */       this.destroyed = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/*  723 */     log(str, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, boolean to_file)
/*      */   {
/*  731 */     log(this.download_manager, str, to_file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(DownloadManager dm, String str, boolean to_file)
/*      */   {
/*  740 */     str = dm.toString() + ": " + str;
/*      */     
/*  742 */     if (to_file)
/*      */     {
/*  744 */       AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("v3.Stream");
/*      */       
/*  746 */       diag_logger.log(str);
/*      */     }
/*      */     
/*  749 */     if (ConstantsVuze.DIAG_TO_STDOUT)
/*      */     {
/*  751 */       System.out.println(Thread.currentThread().getName() + "|" + System.currentTimeMillis() + "] " + str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected class bufferETAProvider
/*      */     implements PieceRTAProvider
/*      */   {
/*  760 */     private boolean is_buffering = true;
/*      */     
/*      */     private long[] piece_rtas;
/*      */     
/*      */     private long last_buffer_size;
/*      */     
/*      */     private long last_buffer_size_time;
/*      */     private boolean active;
/*      */     private long last_recalc;
/*      */     
/*      */     protected bufferETAProvider() {}
/*      */     
/*      */     protected void activate(PiecePicker picker)
/*      */     {
/*  774 */       synchronized (EnhancedDownloadManager.this)
/*      */       {
/*  776 */         if (!this.active)
/*      */         {
/*  778 */           EnhancedDownloadManager.this.log("Activating RTA provider");
/*      */           
/*  780 */           this.active = true;
/*      */           
/*  782 */           picker.addRTAProvider(this);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void deactivate(PiecePicker picker)
/*      */     {
/*  791 */       synchronized (EnhancedDownloadManager.this)
/*      */       {
/*  793 */         if (this.active)
/*      */         {
/*  795 */           EnhancedDownloadManager.this.log("Deactivating RTA provider");
/*      */           
/*  797 */           picker.removeRTAProvider(this);
/*      */         }
/*      */         
/*  800 */         this.piece_rtas = null;
/*      */         
/*  802 */         this.active = false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void checkActivation(PiecePicker picker)
/*      */     {
/*  812 */       if (EnhancedDownloadManager.this.getProgressivePlayETA() > 0L)
/*      */       {
/*  814 */         synchronized (EnhancedDownloadManager.this)
/*      */         {
/*  816 */           if (this.piece_rtas == null)
/*      */           {
/*  818 */             activate(picker);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public long[] updateRTAs(PiecePicker picker)
/*      */     {
/*  828 */       long mono_now = SystemTime.getMonotonousTime();
/*      */       
/*  830 */       if (mono_now - this.last_recalc < 500L)
/*      */       {
/*  832 */         return this.piece_rtas;
/*      */       }
/*      */       
/*  835 */       this.last_recalc = mono_now;
/*      */       
/*  837 */       DiskManager disk_manager = EnhancedDownloadManager.this.download_manager.getDiskManager();
/*      */       
/*  839 */       EnhancedDownloadManager.progressiveStats stats = EnhancedDownloadManager.this.progressive_stats;
/*      */       
/*  841 */       if ((disk_manager == null) || (stats == null) || (stats.getFile().isComplete()))
/*      */       {
/*  843 */         deactivate(picker);
/*      */         
/*  845 */         return null;
/*      */       }
/*      */       
/*  848 */       EnhancedDownloadManagerFile file = stats.getFile();
/*      */       
/*  850 */       long abs_provider_pos = stats.getCurrentProviderPosition(true);
/*  851 */       long rel_provider_pos = stats.getCurrentProviderPosition(false);
/*      */       
/*  853 */       long buffer_bytes = stats.getBufferBytes();
/*      */       
/*  855 */       boolean buffering = EnhancedDownloadManager.this.getProgressivePlayETA() >= 0L;
/*      */       
/*  857 */       if (buffering)
/*      */       {
/*  859 */         long buffer_size = EnhancedDownloadManager.this.getContiguousAvailableBytes(file.getIndex(), rel_provider_pos, buffer_bytes);
/*      */         
/*  861 */         if (buffer_size == buffer_bytes)
/*      */         {
/*  863 */           buffering = false;
/*      */         }
/*      */       }
/*      */       
/*  867 */       if (buffering != this.is_buffering)
/*      */       {
/*  869 */         if (buffering)
/*      */         {
/*  871 */           EnhancedDownloadManager.this.log("Switching to buffer mode");
/*      */         }
/*      */         else
/*      */         {
/*  875 */           EnhancedDownloadManager.this.log("Switching to RTA mode");
/*      */         }
/*      */         
/*  878 */         this.is_buffering = buffering;
/*      */       }
/*      */       
/*  881 */       long piece_size = disk_manager.getPieceLength();
/*      */       
/*  883 */       int start_piece = (int)(abs_provider_pos / piece_size);
/*      */       
/*  885 */       int end_piece = file.getFile().getLastPieceNumber();
/*      */       
/*  887 */       this.piece_rtas = new long[picker.getNumberOfPieces()];
/*      */       
/*  889 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  891 */       if (this.is_buffering)
/*      */       {
/*  893 */         for (int i = start_piece; i <= end_piece; i++)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  898 */           this.piece_rtas[i] = (now + i * 60000);
/*      */         }
/*      */         
/*  901 */         long buffer_size = EnhancedDownloadManager.this.getContiguousAvailableBytes(file.getIndex(), rel_provider_pos, 0L);
/*      */         
/*  903 */         if (this.last_buffer_size != buffer_size)
/*      */         {
/*  905 */           this.last_buffer_size = buffer_size;
/*      */           
/*  907 */           this.last_buffer_size_time = now;
/*      */ 
/*      */ 
/*      */         }
/*  911 */         else if (now < this.last_buffer_size_time)
/*      */         {
/*  913 */           this.last_buffer_size_time = now;
/*      */         }
/*      */         else
/*      */         {
/*  917 */           long stalled_for = now - this.last_buffer_size_time;
/*      */           
/*  919 */           long dl_speed = EnhancedDownloadManager.this.progressive_stats.getDownloadBytesPerSecond();
/*      */           
/*  921 */           if (dl_speed > 0L)
/*      */           {
/*  923 */             long block_time = 16384000L / dl_speed;
/*      */             
/*  925 */             if (stalled_for > Math.max(5000L, 5L * block_time))
/*      */             {
/*  927 */               long target_rta = now + block_time;
/*      */               
/*  929 */               int blocked_piece_index = (int)((abs_provider_pos + buffer_size) / disk_manager.getPieceLength());
/*      */               
/*  931 */               DiskManagerPiece[] pieces = disk_manager.getPieces();
/*      */               
/*  933 */               if (blocked_piece_index < pieces.length)
/*      */               {
/*  935 */                 if (pieces[blocked_piece_index].isDone())
/*      */                 {
/*  937 */                   blocked_piece_index++;
/*      */                   
/*  939 */                   if (blocked_piece_index < pieces.length)
/*      */                   {
/*  941 */                     if (pieces[blocked_piece_index].isDone())
/*      */                     {
/*  943 */                       blocked_piece_index = -1;
/*      */                     }
/*      */                   }
/*      */                   else {
/*  947 */                     blocked_piece_index = -1;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  952 */               if (blocked_piece_index >= 0)
/*      */               {
/*  954 */                 this.piece_rtas[blocked_piece_index] = target_rta;
/*      */                 
/*  956 */                 EnhancedDownloadManager.this.log("Buffer provider: reprioritising lagging piece " + blocked_piece_index + " with rta " + block_time);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  964 */         long bytes_offset = 0L;
/*      */         
/*  966 */         long max_bps = stats.getStreamBytesPerSecondMax();
/*      */         
/*  968 */         for (int i = start_piece; i <= end_piece; i++)
/*      */         {
/*  970 */           this.piece_rtas[i] = (now + 1000L * (bytes_offset / max_bps));
/*      */           
/*  972 */           bytes_offset += piece_size;
/*      */           
/*  974 */           if (bytes_offset > buffer_bytes) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  981 */       return this.piece_rtas;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCurrentPosition()
/*      */     {
/*  987 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getStartTime()
/*      */     {
/*  993 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getStartPosition()
/*      */     {
/*  999 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getBlockingPosition()
/*      */     {
/* 1005 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setBufferMillis(long millis, long delay_millis) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getUserAgent()
/*      */     {
/* 1018 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static abstract class progressiveStats
/*      */     implements Cloneable
/*      */   {
/*      */     protected abstract EnhancedDownloadManagerFile getFile();
/*      */     
/*      */ 
/*      */ 
/*      */     protected abstract boolean isProviderActive();
/*      */     
/*      */ 
/*      */     protected abstract long getCurrentProviderPosition(boolean paramBoolean);
/*      */     
/*      */ 
/*      */     protected abstract long getStreamBytesPerSecondMax();
/*      */     
/*      */ 
/*      */     protected abstract long getStreamBytesPerSecondMin();
/*      */     
/*      */ 
/*      */     protected abstract long getDownloadBytesPerSecond();
/*      */     
/*      */ 
/*      */     protected abstract long getETA();
/*      */     
/*      */ 
/*      */     public abstract long getBufferBytes();
/*      */     
/*      */ 
/*      */     protected abstract long getSecondsToDownload();
/*      */     
/*      */ 
/*      */     protected abstract long getSecondsToWatch();
/*      */     
/*      */ 
/*      */     protected abstract void update(int paramInt);
/*      */     
/*      */ 
/*      */     protected progressiveStats getCopy()
/*      */     {
/*      */       try
/*      */       {
/* 1065 */         return (progressiveStats)clone();
/*      */       }
/*      */       catch (CloneNotSupportedException e)
/*      */       {
/* 1069 */         Debug.printStackTrace(e);
/*      */       }
/* 1071 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected String formatBytes(long l)
/*      */     {
/* 1079 */       return DisplayFormatters.formatByteCountToKiBEtc(l);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected String formatSpeed(long l)
/*      */     {
/* 1086 */       return DisplayFormatters.formatByteCountToKiBEtcPerSec(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class progressiveStatsCommon
/*      */     extends EnhancedDownloadManager.progressiveStats
/*      */   {
/*      */     private EnhancedDownloadManagerFile primary_file;
/*      */     
/*      */     private PieceRTAProvider current_provider;
/*      */     
/*      */     private String current_user_agent;
/*      */     
/*      */     private long content_stream_bps_min;
/*      */     
/*      */     private long content_stream_bps_max;
/*      */     
/* 1104 */     private Average capped_download_rate_average = AverageFactory.MovingImmediateAverage(10);
/* 1105 */     private Average discard_rate_average = AverageFactory.MovingImmediateAverage(10);
/* 1106 */     private long last_discard_bytes = EnhancedDownloadManager.this.download_manager.getStats().getDiscarded();
/*      */     
/*      */     private long actual_bytes_to_download;
/*      */     
/*      */     private long weighted_bytes_to_download;
/*      */     private long provider_life_secs;
/*      */     private long provider_initial_position;
/*      */     private long provider_byte_position;
/* 1114 */     private long provider_last_byte_position = -1L;
/*      */     private long provider_blocking_byte_position;
/* 1116 */     private Average provider_speed_average = AverageFactory.MovingImmediateAverage(10);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected progressiveStatsCommon(DownloadManager _dm, EnhancedDownloadManagerFile _primary_file)
/*      */     {
/* 1123 */       this.primary_file = _primary_file;
/*      */       
/* 1125 */       TOTorrent torrent = EnhancedDownloadManager.this.download_manager.getTorrent();
/*      */       
/* 1127 */       this.content_stream_bps_min = (EnhancedDownloadManager.this.explicit_progressive ? EnhancedDownloadManager.this.content_min_delivery_bps : PlatformTorrentUtils.getContentStreamSpeedBps(torrent));
/*      */       
/* 1129 */       if (this.content_stream_bps_min == 0L)
/*      */       {
/*      */ 
/*      */ 
/* 1133 */         long size = torrent.getSize();
/*      */         
/* 1135 */         if (size < 209715200L)
/*      */         {
/* 1137 */           this.content_stream_bps_min = 30720L;
/*      */         }
/* 1139 */         else if (size < 1048576000L)
/*      */         {
/* 1141 */           this.content_stream_bps_min = 204800L;
/*      */         }
/*      */         else
/*      */         {
/* 1145 */           this.content_stream_bps_min = 409600L;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1151 */       this.content_stream_bps_min += this.content_stream_bps_min / 5L;
/*      */       
/* 1153 */       this.content_stream_bps_max = (this.content_stream_bps_min + this.content_stream_bps_min / 8L);
/*      */       
/* 1155 */       EnhancedDownloadManager.this.setRTA(false);
/*      */       
/* 1157 */       EnhancedDownloadManager.this.log(EnhancedDownloadManager.this.download_manager, "content_stream_bps=" + getStreamBytesPerSecondMin() + ",primary=" + this.primary_file.getFile().getIndex(), true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected void updateCurrentProvider(PieceRTAProvider provider)
/*      */     {
/* 1168 */       long file_start = this.primary_file.getByteOffestInTorrent();
/*      */       
/* 1170 */       if ((this.current_provider != provider) || (provider == null))
/*      */       {
/* 1172 */         this.current_provider = provider;
/* 1173 */         this.current_user_agent = null;
/*      */         
/* 1175 */         this.provider_speed_average = AverageFactory.MovingImmediateAverage(10);
/*      */         
/* 1177 */         if (this.current_provider == null)
/*      */         {
/* 1179 */           this.provider_life_secs = 0L;
/* 1180 */           this.provider_initial_position = file_start;
/* 1181 */           this.provider_byte_position = file_start;
/* 1182 */           this.provider_blocking_byte_position = -1L;
/* 1183 */           this.provider_last_byte_position = -1L;
/*      */         }
/*      */         else
/*      */         {
/* 1187 */           this.provider_initial_position = Math.max(file_start, this.current_provider.getStartPosition());
/*      */           
/* 1189 */           this.provider_byte_position = this.provider_initial_position;
/* 1190 */           this.provider_last_byte_position = this.provider_initial_position;
/*      */           
/* 1192 */           this.provider_blocking_byte_position = this.current_provider.getBlockingPosition();
/*      */           
/* 1194 */           this.provider_life_secs = ((SystemTime.getCurrentTime() - this.current_provider.getStartTime()) / 1000L);
/*      */           
/* 1196 */           if (this.provider_life_secs < 0L)
/*      */           {
/* 1198 */             this.provider_life_secs = 0L;
/*      */           }
/*      */         }
/*      */         
/* 1202 */         EnhancedDownloadManager.this.setRTA(this.current_provider != null);
/*      */       }
/*      */       else
/*      */       {
/* 1206 */         this.provider_life_secs += 1L;
/*      */         
/* 1208 */         if (this.current_user_agent == null)
/*      */         {
/* 1210 */           this.current_user_agent = this.current_provider.getUserAgent();
/*      */           
/* 1212 */           if (this.current_user_agent != null)
/*      */           {
/* 1214 */             EnhancedDownloadManager.this.log("Provider user agent = " + this.current_user_agent);
/*      */           }
/*      */         }
/*      */         
/* 1218 */         this.provider_byte_position = Math.max(file_start, this.current_provider.getCurrentPosition());
/* 1219 */         this.provider_blocking_byte_position = this.current_provider.getBlockingPosition();
/*      */         
/* 1221 */         long bytes_read = this.provider_byte_position - this.provider_last_byte_position;
/*      */         
/* 1223 */         this.provider_speed_average.update(bytes_read);
/*      */         
/* 1225 */         this.provider_last_byte_position = this.provider_byte_position;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isProviderActive()
/*      */     {
/* 1232 */       return this.current_provider != null;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getInitialProviderPosition()
/*      */     {
/* 1238 */       return this.provider_initial_position;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected long getCurrentProviderPosition(boolean absolute)
/*      */     {
/* 1245 */       long res = this.provider_byte_position;
/*      */       
/* 1247 */       if (absolute)
/*      */       {
/* 1249 */         if (res == 0L)
/*      */         {
/* 1251 */           res = this.primary_file.getByteOffestInTorrent();
/*      */         }
/*      */       }
/*      */       else {
/* 1255 */         res -= this.primary_file.getByteOffestInTorrent();
/*      */         
/* 1257 */         if (res < 0L)
/*      */         {
/* 1259 */           res = 0L;
/*      */         }
/*      */       }
/*      */       
/* 1263 */       return res;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getProviderLifeSecs()
/*      */     {
/* 1269 */       return this.provider_life_secs;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void update(int tick_count)
/*      */     {
/* 1276 */       long download_rate = EnhancedDownloadManager.this.download_manager.getStats().getDataReceiveRate();
/*      */       
/* 1278 */       this.capped_download_rate_average.update(download_rate);
/*      */       
/* 1280 */       long discards = EnhancedDownloadManager.this.download_manager.getStats().getDiscarded();
/*      */       
/* 1282 */       this.discard_rate_average.update(discards - this.last_discard_bytes);
/*      */       
/* 1284 */       this.last_discard_bytes = discards;
/*      */       
/* 1286 */       DiskManager disk_manager = EnhancedDownloadManager.this.download_manager.getDiskManager();
/*      */       
/* 1288 */       PiecePicker picker = EnhancedDownloadManager.this.current_piece_pickler;
/*      */       
/* 1290 */       if ((getStreamBytesPerSecondMin() > 0L) && (disk_manager != null) && (picker != null))
/*      */       {
/* 1292 */         List providers = picker.getRTAProviders();
/*      */         
/* 1294 */         long max_cp = 0L;
/*      */         
/* 1296 */         PieceRTAProvider best_provider = null;
/*      */         
/* 1298 */         for (int i = 0; i < providers.size(); i++)
/*      */         {
/* 1300 */           PieceRTAProvider provider = (PieceRTAProvider)providers.get(i);
/*      */           
/* 1302 */           if (provider.getStartTime() > 0L)
/*      */           {
/* 1304 */             long cp = provider.getCurrentPosition();
/*      */             
/* 1306 */             if (cp >= max_cp)
/*      */             {
/* 1308 */               best_provider = provider;
/*      */               
/* 1310 */               max_cp = cp;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1315 */         updateCurrentProvider(best_provider);
/*      */         
/* 1317 */         if (best_provider != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1322 */           long relative_pos = getCurrentProviderPosition(false);
/*      */           
/* 1324 */           long buffer_bytes = EnhancedDownloadManager.this.getContiguousAvailableBytes(this.primary_file.getIndex(), relative_pos, getStreamBytesPerSecondMin() * 60L);
/*      */           
/* 1326 */           long buffer_secs = buffer_bytes / getStreamBytesPerSecondMin();
/*      */           
/*      */ 
/*      */ 
/* 1330 */           buffer_secs = Math.max(10L, buffer_secs);
/*      */           
/* 1332 */           best_provider.setBufferMillis(15000L, buffer_secs * 1000L);
/*      */         }
/*      */         
/* 1335 */         DiskManagerPiece[] pieces = disk_manager.getPieces();
/*      */         
/* 1337 */         this.actual_bytes_to_download = 0L;
/* 1338 */         this.weighted_bytes_to_download = 0L;
/*      */         
/* 1340 */         int first_incomplete_piece = -1;
/*      */         
/* 1342 */         int piece_size = disk_manager.getPieceLength();
/*      */         
/* 1344 */         int last_piece_number = this.primary_file.getFile().getLastPieceNumber();
/*      */         
/* 1346 */         for (int i = (int)(this.provider_byte_position / piece_size); i <= last_piece_number; i++)
/*      */         {
/* 1348 */           DiskManagerPiece piece = pieces[i];
/*      */           
/* 1350 */           if (!piece.isDone())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1355 */             if (first_incomplete_piece == -1)
/*      */             {
/* 1357 */               first_incomplete_piece = i;
/*      */             }
/*      */             
/* 1360 */             boolean[] blocks = piece.getWritten();
/*      */             
/* 1362 */             int bytes_this_piece = 0;
/*      */             
/* 1364 */             if (blocks == null)
/*      */             {
/* 1366 */               bytes_this_piece = piece.getLength();
/*      */             }
/*      */             else {
/* 1369 */               for (int j = 0; j < blocks.length; j++)
/*      */               {
/* 1371 */                 if (blocks[j] == 0)
/*      */                 {
/* 1373 */                   bytes_this_piece += piece.getBlockSize(j);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 1378 */             if (bytes_this_piece > 0)
/*      */             {
/* 1380 */               this.actual_bytes_to_download += bytes_this_piece;
/*      */               
/* 1382 */               int diff = i - first_incomplete_piece;
/*      */               
/* 1384 */               if (diff == 0)
/*      */               {
/* 1386 */                 this.weighted_bytes_to_download += bytes_this_piece;
/*      */               }
/*      */               else
/*      */               {
/* 1390 */                 int weighted_bytes_done = piece.getLength() - bytes_this_piece;
/*      */                 
/* 1392 */                 weighted_bytes_done = weighted_bytes_done * (pieces.length - i) / (pieces.length - first_incomplete_piece);
/*      */                 
/* 1394 */                 this.weighted_bytes_to_download += piece.getLength() - weighted_bytes_done;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1400 */       EnhancedDownloadManager.this.log(getString(), tick_count % 10 == 0);
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getETA()
/*      */     {
/* 1406 */       DiskManagerFileInfo file = this.primary_file.getFile();
/*      */       
/* 1408 */       if (file.getLength() == file.getDownloaded())
/*      */       {
/* 1410 */         return 0L;
/*      */       }
/*      */       
/* 1413 */       long download_rate = getDownloadBytesPerSecond();
/*      */       
/* 1415 */       if (download_rate <= 0L)
/*      */       {
/* 1417 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/* 1420 */       long buffer_bytes = getBufferBytes();
/*      */       
/* 1422 */       long buffer_done = EnhancedDownloadManager.this.getContiguousAvailableBytes(file.getIndex(), getCurrentProviderPosition(false), buffer_bytes);
/*      */       
/* 1424 */       long rem_buffer = buffer_bytes - buffer_done;
/*      */       
/* 1426 */       long rem_secs = rem_buffer <= 0L ? 0L : rem_buffer / download_rate;
/*      */       
/* 1428 */       long secs_to_download = getSecondsToDownload();
/*      */       
/* 1430 */       long secs_to_watch = getSecondsToWatch();
/*      */       
/* 1432 */       long eta = secs_to_download - secs_to_watch;
/*      */       
/* 1434 */       if ((rem_secs > eta) && (rem_secs > 0L))
/*      */       {
/* 1436 */         eta = rem_secs;
/*      */       }
/*      */       
/* 1439 */       return eta;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getStreamBytesPerSecondMax()
/*      */     {
/* 1445 */       return this.content_stream_bps_max;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getStreamBytesPerSecondMin()
/*      */     {
/* 1451 */       return this.content_stream_bps_min;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getBufferBytes()
/*      */     {
/* 1457 */       long min_dl = EnhancedDownloadManager.this.minimum_initial_buffer_secs_for_eta * getStreamBytesPerSecondMax();
/*      */       
/* 1459 */       return min_dl;
/*      */     }
/*      */     
/*      */ 
/*      */     protected EnhancedDownloadManagerFile getFile()
/*      */     {
/* 1465 */       return this.primary_file;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getDownloadBytesPerSecond()
/*      */     {
/* 1471 */       long original = this.capped_download_rate_average.getAverage();
/*      */       
/* 1473 */       long current = original;
/*      */       
/* 1475 */       int dl_limit = EnhancedDownloadManager.this.download_manager.getStats().getDownloadRateLimitBytesPerSecond();
/*      */       
/* 1477 */       if (dl_limit > 0)
/*      */       {
/* 1479 */         current = Math.min(current, dl_limit);
/*      */       }
/*      */       
/* 1482 */       int global_limit = TransferSpeedValidator.getGlobalDownloadRateLimitBytesPerSecond();
/*      */       
/* 1484 */       if (global_limit > 0)
/*      */       {
/* 1486 */         current = Math.min(current, global_limit);
/*      */       }
/*      */       
/* 1489 */       return current;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getSecondsToDownload()
/*      */     {
/* 1495 */       long download_rate = getDownloadBytesPerSecond();
/*      */       
/* 1497 */       if (download_rate == 0L)
/*      */       {
/* 1499 */         return Long.MAX_VALUE;
/*      */       }
/*      */       
/* 1502 */       return this.weighted_bytes_to_download / download_rate;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getSecondsToWatch()
/*      */     {
/* 1508 */       return (this.primary_file.getLength() - getCurrentProviderPosition(false)) / getStreamBytesPerSecondMin();
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 1514 */       long dl_rate = getDownloadBytesPerSecond();
/*      */       
/* 1516 */       long buffer_bytes = getBufferBytes();
/*      */       
/* 1518 */       long buffer_done = EnhancedDownloadManager.this.getContiguousAvailableBytes(this.primary_file.getIndex(), getCurrentProviderPosition(false), buffer_bytes);
/*      */       
/* 1520 */       return "play_eta=" + getETA() + "/d=" + getSecondsToDownload() + "/w=" + getSecondsToWatch() + ", dl_rate=" + formatSpeed(dl_rate) + ", download_rem=" + formatBytes(this.weighted_bytes_to_download) + "/" + formatBytes(this.actual_bytes_to_download) + ", discard_rate=" + formatSpeed(this.discard_rate_average.getAverage()) + ", buffer: " + buffer_bytes + "/" + buffer_done + ", prov: byte=" + formatBytes(this.provider_byte_position) + " secs=" + this.provider_byte_position / getStreamBytesPerSecondMin() + " speed=" + formatSpeed(this.provider_speed_average.getAverage()) + " block= " + formatBytes(this.provider_blocking_byte_position);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/EnhancedDownloadManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */