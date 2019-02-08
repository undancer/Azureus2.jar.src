/*      */ package org.gudy.azureus2.core3.disk.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessController;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManager;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManager;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerFactory;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager.GettingThere;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager.OperationStatus;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequestListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener;
/*      */ import org.gudy.azureus2.core3.disk.impl.access.DMAccessFactory;
/*      */ import org.gudy.azureus2.core3.disk.impl.access.DMChecker;
/*      */ import org.gudy.azureus2.core3.disk.impl.access.DMReader;
/*      */ import org.gudy.azureus2.core3.disk.impl.access.DMWriter;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapper;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapperFile;
/*      */ import org.gudy.azureus2.core3.disk.impl.resume.RDResumeHandler;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerException;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerMoveHandler;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerStatsImpl;
/*      */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtilEncodingException;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.LogRelation;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.ThreadPool;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*      */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*      */ 
/*      */ public class DiskManagerImpl extends LogRelation implements DiskManagerHelper
/*      */ {
/*      */   private static final int DM_FREE_PIECELIST_TIMEOUT = 120000;
/*   91 */   private static final LogIDs LOGID = LogIDs.DISK;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final DiskAccessController disk_access_controller;
/*      */   
/*      */ 
/*      */ 
/*      */   private static boolean reorder_storage_mode;
/*      */   
/*      */ 
/*      */ 
/*      */   private static int reorder_storage_mode_min_mb;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final DiskManagerRecheckScheduler recheck_scheduler;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final DiskManagerAllocationScheduler allocation_scheduler;
/*      */   
/*      */ 
/*      */   private static final ThreadPool start_pool;
/*      */   
/*      */ 
/*      */ 
/*      */   public static DiskAccessController getDefaultDiskAccessController()
/*      */   {
/*  120 */     return disk_access_controller;
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*   96 */     int max_read_threads = COConfigurationManager.getIntParameter("diskmanager.perf.read.maxthreads");
/*   97 */     int max_read_mb = COConfigurationManager.getIntParameter("diskmanager.perf.read.maxmb");
/*   98 */     int max_write_threads = COConfigurationManager.getIntParameter("diskmanager.perf.write.maxthreads");
/*   99 */     int max_write_mb = COConfigurationManager.getIntParameter("diskmanager.perf.write.maxmb");
/*      */     
/*  101 */     disk_access_controller = com.aelitis.azureus.core.diskmanager.access.DiskAccessControllerFactory.create("core", max_read_threads, max_read_mb, max_write_threads, max_write_mb);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  107 */     if (Logger.isEnabled()) {
/*  108 */       Logger.log(new LogEvent(LOGID, "Disk access controller params: " + max_read_threads + "/" + max_read_mb + "/" + max_write_threads + "/" + max_write_mb));
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
/*  127 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Enable reorder storage mode", "Reorder storage mode min MB" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  137 */         DiskManagerImpl.access$002(COConfigurationManager.getBooleanParameter("Enable reorder storage mode"));
/*  138 */         DiskManagerImpl.access$102(COConfigurationManager.getIntParameter("Reorder storage mode min MB"));
/*      */       }
/*      */       
/*      */ 
/*  142 */     });
/*  143 */     recheck_scheduler = new DiskManagerRecheckScheduler();
/*  144 */     allocation_scheduler = new DiskManagerAllocationScheduler();
/*      */     
/*  146 */     start_pool = new ThreadPool("DiskManager:start", 64, true);
/*      */     
/*      */ 
/*  149 */     start_pool.setThreadPriority(1);
/*      */   }
/*      */   
/*  152 */   private boolean used = false;
/*      */   
/*  154 */   private boolean started = false;
/*  155 */   final AESemaphore started_sem = new AESemaphore("DiskManager::started");
/*      */   
/*      */   private boolean starting;
/*      */   
/*      */   private boolean stopping;
/*      */   private int state_set_via_method;
/*  161 */   private String errorMessage = "";
/*  162 */   private int errorType = 0;
/*      */   
/*      */   private int pieceLength;
/*      */   
/*      */   private int lastPieceLength;
/*      */   
/*      */   private int nbPieces;
/*      */   
/*      */   private long totalLength;
/*      */   
/*      */   private int percentDone;
/*      */   
/*      */   private long allocated;
/*      */   
/*      */   private long remaining;
/*      */   
/*      */   private final TOTorrent torrent;
/*      */   
/*      */   private DMReader reader;
/*      */   
/*      */   private DMChecker checker;
/*      */   
/*      */   private DMWriter writer;
/*      */   
/*      */   private RDResumeHandler resume_handler;
/*      */   private DMPieceMapper piece_mapper;
/*      */   private DiskManagerPieceImpl[] pieces;
/*      */   private DMPieceMap piece_map_use_accessor;
/*      */   private long piece_map_use_accessor_time;
/*      */   private DiskManagerFileInfoImpl[] files;
/*      */   private DiskManagerFileInfoSet fileset;
/*      */   protected final DownloadManager download_manager;
/*  194 */   private boolean alreadyMoved = false;
/*      */   
/*  196 */   private boolean skipped_file_set_changed = true;
/*      */   
/*      */   private long skipped_file_set_size;
/*      */   private long skipped_but_downloaded;
/*  200 */   private final AtomicLong priority_change_marker = new AtomicLong(RandomUtils.nextLong());
/*      */   
/*      */ 
/*      */   private boolean checking_enabled;
/*      */   
/*      */ 
/*      */   private volatile boolean move_in_progress;
/*      */   
/*      */ 
/*      */   private volatile int move_progress;
/*      */   
/*      */   private static final int LDT_STATECHANGED = 1;
/*      */   
/*      */   private static final int LDT_PRIOCHANGED = 2;
/*      */   
/*      */   private static final int LDT_PIECE_DONE_CHANGED = 3;
/*      */   
/*      */   private static final int LDT_ACCESS_MODE_CHANGED = 4;
/*      */   
/*  219 */   protected static final ListenerManager<DiskManagerListener> listeners_aggregator = ListenerManager.createAsyncManager("DiskM:ListenAggregatorDispatcher", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(DiskManagerListener listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  229 */       if (type == 1)
/*      */       {
/*  231 */         int[] params = (int[])value;
/*      */         
/*  233 */         listener.stateChanged(params[0], params[1]);
/*      */       }
/*  235 */       else if (type == 2)
/*      */       {
/*  237 */         listener.filePriorityChanged((DiskManagerFileInfo)value);
/*      */       }
/*  239 */       else if (type == 3)
/*      */       {
/*  241 */         listener.pieceDoneChanged((DiskManagerPiece)value);
/*      */       }
/*  243 */       else if (type == 4)
/*      */       {
/*  245 */         Object[] o = (Object[])value;
/*      */         
/*  247 */         listener.fileAccessModeChanged((DiskManagerFileInfo)o[0], ((Integer)o[1]).intValue(), ((Integer)o[2]).intValue());
/*      */       }
/*      */     }
/*  219 */   });
/*      */   private final ListenerManager<DiskManagerListener> listeners;
/*      */   final AEMonitor start_stop_mon;
/*      */   private final AEMonitor file_piece_mon;
/*      */   
/*      */   public DiskManagerImpl(TOTorrent _torrent, DownloadManager _dmanager)
/*      */   {
/*  202 */     if (this.priority_change_marker.get() == 0L) {
/*  203 */       this.priority_change_marker.incrementAndGet();
/*      */     }
/*      */     
/*      */ 
/*  207 */     this.checking_enabled = true;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  255 */     this.listeners = ListenerManager.createManager("DiskM:ListenDispatcher", new ListenerManagerDispatcher()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dispatch(DiskManagerListener listener, int type, Object value)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  265 */         DiskManagerImpl.listeners_aggregator.dispatch(listener, type, value);
/*      */       }
/*      */       
/*  268 */     });
/*  269 */     this.start_stop_mon = new AEMonitor("DiskManager:startStop");
/*  270 */     this.file_piece_mon = new AEMonitor("DiskManager:filePiece");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  278 */     this.torrent = _torrent;
/*  279 */     this.download_manager = _dmanager;
/*      */     
/*  281 */     this.pieces = new DiskManagerPieceImpl[0];
/*      */     
/*  283 */     setState(1);
/*      */     
/*  285 */     this.percentDone = 0;
/*  286 */     this.errorType = 0;
/*      */     
/*  288 */     if (this.torrent == null)
/*      */     {
/*  290 */       this.errorMessage = "Torrent not available";
/*      */       
/*  292 */       setState(10);
/*      */       
/*  294 */       return;
/*      */     }
/*      */     
/*  297 */     LocaleUtilDecoder locale_decoder = null;
/*      */     try
/*      */     {
/*  300 */       locale_decoder = LocaleTorrentUtil.getTorrentEncoding(this.torrent);
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*  304 */       Debug.printStackTrace(e);
/*      */       
/*  306 */       this.errorMessage = TorrentUtils.exceptionToText(e);
/*      */       
/*  308 */       setState(10);
/*      */       
/*  310 */       return;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  314 */       Debug.printStackTrace(e);
/*      */       
/*  316 */       this.errorMessage = ("Initialisation failed - " + Debug.getNestedExceptionMessage(e));
/*      */       
/*  318 */       setState(10);
/*      */       
/*  320 */       return;
/*      */     }
/*      */     
/*  323 */     this.piece_mapper = org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapperFactory.create(this.torrent);
/*      */     try
/*      */     {
/*  326 */       this.piece_mapper.construct(locale_decoder, this.download_manager.getAbsoluteSaveLocation().getName());
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  330 */       Debug.printStackTrace(e);
/*      */       
/*  332 */       this.errorMessage = ("Failed to build piece map - " + Debug.getNestedExceptionMessage(e));
/*      */       
/*  334 */       setState(10);
/*      */       
/*  336 */       return;
/*      */     }
/*      */     
/*  339 */     this.totalLength = this.piece_mapper.getTotalLength();
/*  340 */     this.remaining = this.totalLength;
/*      */     
/*  342 */     this.nbPieces = this.torrent.getNumberOfPieces();
/*      */     
/*  344 */     this.pieceLength = ((int)this.torrent.getPieceLength());
/*  345 */     this.lastPieceLength = this.piece_mapper.getLastPieceLength();
/*      */     
/*  347 */     this.pieces = new DiskManagerPieceImpl[this.nbPieces];
/*      */     
/*  349 */     for (int i = 0; i < this.nbPieces; i++)
/*      */     {
/*  351 */       this.pieces[i] = new DiskManagerPieceImpl(this, i, i == this.nbPieces - 1 ? this.lastPieceLength : this.pieceLength);
/*      */     }
/*      */     
/*  354 */     this.reader = DMAccessFactory.createReader(this);
/*      */     
/*  356 */     this.checker = DMAccessFactory.createChecker(this);
/*      */     
/*  358 */     this.writer = DMAccessFactory.createWriter(this);
/*      */     
/*  360 */     this.resume_handler = new RDResumeHandler(this, this.checker);
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */   {
/*      */     try
/*      */     {
/*  368 */       if (this.move_in_progress)
/*      */       {
/*  370 */         Debug.out("start called while move in progress!");
/*      */       }
/*      */       
/*  373 */       this.start_stop_mon.enter();
/*      */       
/*  375 */       if (this.used)
/*      */       {
/*  377 */         Debug.out("DiskManager reuse not supported!!!!");
/*      */       }
/*      */       
/*  380 */       this.used = true;
/*      */       
/*  382 */       if (getState() == 10)
/*      */       {
/*  384 */         Debug.out("starting a faulty disk manager");
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  389 */         this.started = true;
/*  390 */         this.starting = true;
/*      */         
/*  392 */         start_pool.run(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/*  404 */                 DiskManagerImpl.this.start_stop_mon.enter();
/*      */                 
/*  406 */                 if (DiskManagerImpl.this.stopping)
/*      */                 {
/*  408 */                   throw new Exception("Stopped during startup");
/*      */                 }
/*      */               }
/*      */               finally {
/*  412 */                 DiskManagerImpl.this.start_stop_mon.exit();
/*      */               }
/*      */               
/*  415 */               DiskManagerImpl.this.startSupport();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  419 */               DiskManagerImpl.this.errorMessage = (Debug.getNestedExceptionMessage(e) + " (start)");
/*      */               
/*  421 */               Debug.printStackTrace(e);
/*      */               
/*  423 */               DiskManagerImpl.this.setState(10);
/*      */             }
/*      */             finally
/*      */             {
/*  427 */               DiskManagerImpl.this.started_sem.release();
/*      */             }
/*      */             
/*      */             boolean stop_required;
/*      */             try
/*      */             {
/*  433 */               DiskManagerImpl.this.start_stop_mon.enter();
/*      */               
/*  435 */               stop_required = (DiskManagerImpl.this.getState() == 10) || (DiskManagerImpl.this.stopping);
/*      */               
/*  437 */               DiskManagerImpl.this.starting = false;
/*      */             }
/*      */             finally
/*      */             {
/*  441 */               DiskManagerImpl.this.start_stop_mon.exit();
/*      */             }
/*      */             
/*  444 */             if (stop_required)
/*      */             {
/*  446 */               DiskManagerImpl.this.stop(false);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     finally {
/*  453 */       this.start_stop_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void startSupport()
/*      */   {
/*  461 */     boolean files_exist = false;
/*      */     
/*  463 */     if (this.download_manager.isPersistent())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  468 */       File[] move_to_dirs = DownloadManagerMoveHandler.getRelatedDirs(this.download_manager);
/*      */       
/*  470 */       for (int i = 0; i < move_to_dirs.length; i++) {
/*  471 */         String move_to_dir = move_to_dirs[i].getAbsolutePath();
/*  472 */         if (filesExist(move_to_dir)) {
/*  473 */           this.alreadyMoved = (files_exist = 1);
/*  474 */           this.download_manager.setTorrentSaveDir(move_to_dir);
/*  475 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  480 */     this.reader.start();
/*      */     
/*  482 */     this.checker.start();
/*      */     
/*  484 */     this.writer.start();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  489 */     if ((!this.alreadyMoved) && (!this.download_manager.isDataAlreadyAllocated()))
/*      */     {
/*      */ 
/*  492 */       if (!files_exist) files_exist = filesExist();
/*  493 */       if (!files_exist) {
/*  494 */         SaveLocationChange transfer = DownloadManagerMoveHandler.onInitialisation(this.download_manager);
/*      */         
/*  496 */         if (transfer != null) {
/*  497 */           if ((transfer.download_location != null) || (transfer.download_name != null)) {
/*  498 */             File dl_location = transfer.download_location;
/*  499 */             if (dl_location == null) dl_location = this.download_manager.getAbsoluteSaveLocation().getParentFile();
/*  500 */             if (transfer.download_name == null) {
/*  501 */               this.download_manager.setTorrentSaveDir(dl_location.getAbsolutePath());
/*      */             }
/*      */             else {
/*  504 */               this.download_manager.setTorrentSaveDir(dl_location.getAbsolutePath(), transfer.download_name);
/*      */             }
/*      */           }
/*  507 */           if ((transfer.torrent_location != null) || (transfer.torrent_name != null)) {
/*  508 */             try { this.download_manager.setTorrentFile(transfer.torrent_location, transfer.torrent_name);
/*  509 */             } catch (DownloadManagerException e) { Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  517 */     int[] alloc_result = allocateFiles();
/*      */     
/*  519 */     int newFiles = alloc_result[0];
/*  520 */     int notNeededFiles = alloc_result[1];
/*      */     
/*  522 */     if (getState() == 10)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  527 */       return;
/*      */     }
/*      */     
/*  530 */     if (getState() == 10)
/*      */     {
/*      */ 
/*      */ 
/*  534 */       return;
/*      */     }
/*      */     
/*  537 */     setState(3);
/*      */     
/*  539 */     this.resume_handler.start();
/*      */     
/*  541 */     if (this.checking_enabled)
/*      */     {
/*  543 */       if (newFiles == 0)
/*      */       {
/*  545 */         this.resume_handler.checkAllPieces(false);
/*      */         
/*      */ 
/*      */ 
/*  549 */         if (getRemainingExcludingDND() == 0L)
/*      */         {
/*  551 */           checkFreePieceList(true);
/*      */         }
/*  553 */       } else if (newFiles + notNeededFiles != this.files.length)
/*      */       {
/*      */ 
/*      */ 
/*  557 */         this.resume_handler.checkAllPieces(true);
/*      */       }
/*      */     }
/*      */     
/*  561 */     if (getState() == 10)
/*      */     {
/*  563 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  573 */     this.skipped_file_set_changed = true;
/*      */     
/*  575 */     setState(4);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean stop(boolean closing)
/*      */   {
/*      */     try
/*      */     {
/*  583 */       if (this.move_in_progress)
/*      */       {
/*  585 */         Debug.out("stop called while move in progress!");
/*      */       }
/*      */       
/*  588 */       this.start_stop_mon.enter();
/*      */       boolean bool;
/*  590 */       if (!this.started)
/*      */       {
/*  592 */         return false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  599 */       if (this.starting)
/*      */       {
/*  601 */         this.stopping = true;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  607 */         this.checker.stop();
/*      */         
/*  609 */         this.writer.stop();
/*      */         
/*  611 */         this.reader.stop();
/*      */         
/*  613 */         this.resume_handler.stop(closing);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  618 */         saveState(false);
/*      */         
/*  620 */         return true;
/*      */       }
/*      */       
/*  623 */       this.started = false;
/*      */       
/*  625 */       this.stopping = false;
/*      */     }
/*      */     finally
/*      */     {
/*  629 */       this.start_stop_mon.exit();
/*      */     }
/*      */     
/*  632 */     this.started_sem.reserve();
/*      */     
/*  634 */     this.checker.stop();
/*      */     
/*  636 */     this.writer.stop();
/*      */     
/*  638 */     this.reader.stop();
/*      */     
/*  640 */     this.resume_handler.stop(closing);
/*      */     
/*  642 */     if (this.files != null)
/*      */     {
/*  644 */       for (int i = 0; i < this.files.length; i++) {
/*      */         try
/*      */         {
/*  647 */           if (this.files[i] != null)
/*      */           {
/*  649 */             this.files[i].getCacheFile().close();
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  653 */           setFailed("File close fails: " + Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  658 */     if (getState() == 4)
/*      */     {
/*      */       try
/*      */       {
/*  662 */         saveResumeData(false);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  666 */         setFailed("Resume data save fails: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     
/*  670 */     saveState();
/*      */     
/*      */ 
/*  673 */     this.listeners.clear();
/*      */     
/*  675 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStopped()
/*      */   {
/*  681 */     if (this.move_in_progress)
/*      */     {
/*  683 */       Debug.out("isStopped called while move in progress!");
/*      */     }
/*      */     try
/*      */     {
/*  687 */       this.start_stop_mon.enter();
/*      */       
/*  689 */       return (!this.started) && (!this.starting) && (!this.stopping);
/*      */     }
/*      */     finally
/*      */     {
/*  693 */       this.start_stop_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean filesExist()
/*      */   {
/*  700 */     return filesExist(this.download_manager.getAbsoluteSaveLocation().getParent());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean filesExist(String root_dir)
/*      */   {
/*  707 */     if (!this.torrent.isSimpleTorrent())
/*      */     {
/*  709 */       root_dir = root_dir + File.separator + this.download_manager.getAbsoluteSaveLocation().getName();
/*      */     }
/*      */     
/*  712 */     if (!root_dir.endsWith(File.separator))
/*      */     {
/*  714 */       root_dir = root_dir + File.separator;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  719 */     DMPieceMapperFile[] pm_files = this.piece_mapper.getFiles();
/*      */     
/*  721 */     String[] storage_types = getStorageTypes();
/*      */     
/*  723 */     DownloadManagerState state = this.download_manager.getDownloadState();
/*      */     
/*  725 */     for (int i = 0; i < pm_files.length; i++)
/*      */     {
/*  727 */       DMPieceMapperFile pm_info = pm_files[i];
/*      */       
/*  729 */       File relative_file = pm_info.getDataFile();
/*      */       
/*  731 */       long target_length = pm_info.getLength();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  740 */       DiskManagerFileInfoImpl file_info = pm_info.getFileInfo();
/*      */       
/*  742 */       boolean close_it = false;
/*      */       try
/*      */       {
/*  745 */         if (file_info == null)
/*      */         {
/*  747 */           int storage_type = DiskManagerUtil.convertDMStorageTypeFromString(storage_types[i]);
/*      */           
/*  749 */           file_info = createFileInfo(state, pm_info, i, root_dir, relative_file, storage_type);
/*      */           
/*  751 */           close_it = true;
/*      */         }
/*      */         try
/*      */         {
/*  755 */           CacheFile cache_file = file_info.getCacheFile();
/*  756 */           File data_file = file_info.getFile(true);
/*      */           boolean bool1;
/*  758 */           if (!cache_file.exists())
/*      */           {
/*      */ 
/*      */ 
/*  762 */             File current = data_file;
/*      */             File parent;
/*  764 */             while (!current.exists())
/*      */             {
/*  766 */               parent = current.getParentFile();
/*      */               
/*  768 */               if (parent == null) {
/*      */                 break;
/*      */               }
/*      */               
/*  772 */               if (!parent.exists())
/*      */               {
/*  774 */                 current = parent;
/*      */               }
/*      */               else
/*      */               {
/*  778 */                 if (parent.isDirectory())
/*      */                 {
/*  780 */                   this.errorMessage = (current.toString() + " not found.");
/*      */                 }
/*      */                 else
/*      */                 {
/*  784 */                   this.errorMessage = (parent.toString() + " is not a directory.");
/*      */                 }
/*      */                 
/*  787 */                 return false;
/*      */               }
/*      */             }
/*      */             
/*  791 */             this.errorMessage = (data_file.toString() + " not found.");
/*      */             
/*  793 */             return 0;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  799 */           long existing_length = file_info.getCacheFile().getLength();
/*      */           
/*  801 */           if (existing_length > target_length)
/*      */           {
/*  803 */             if (COConfigurationManager.getBooleanParameter("File.truncate.if.too.large"))
/*      */             {
/*  805 */               file_info.setAccessMode(2);
/*      */               
/*  807 */               file_info.getCacheFile().setLength(target_length);
/*      */               
/*  809 */               Debug.out("Existing data file length too large [" + existing_length + ">" + target_length + "]: " + data_file.getAbsolutePath() + ", truncating");
/*      */             }
/*      */             else
/*      */             {
/*  813 */               this.errorMessage = ("Existing data file length too large [" + existing_length + ">" + target_length + "]: " + data_file.getAbsolutePath());
/*      */               
/*  815 */               return false;
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  820 */           if (close_it)
/*      */           {
/*  822 */             file_info.getCacheFile().close();
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  827 */         this.errorMessage = (Debug.getNestedExceptionMessage(e) + " (filesExist:" + relative_file.toString() + ")");
/*      */         
/*  829 */         return false;
/*      */       }
/*      */     }
/*      */     
/*  833 */     return true;
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
/*      */   private DiskManagerFileInfoImpl createFileInfo(DownloadManagerState state, DMPieceMapperFile pm_info, int file_index, String root_dir, File relative_file, int storage_type)
/*      */     throws Exception
/*      */   {
/*      */     try
/*      */     {
/*  849 */       return new DiskManagerFileInfoImpl(this, root_dir, relative_file, file_index, pm_info.getTorrentFile(), storage_type);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (CacheFileManagerException e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  865 */       if (Debug.getNestedExceptionMessage(e).contains("volume label syntax is incorrect"))
/*      */       {
/*  867 */         File target_file = new File(root_dir + relative_file.toString());
/*      */         
/*  869 */         File actual_file = state.getFileLink(file_index, target_file);
/*      */         
/*  871 */         if (actual_file == null)
/*      */         {
/*  873 */           actual_file = target_file;
/*      */         }
/*      */         
/*  876 */         File temp = actual_file;
/*      */         
/*  878 */         Stack<String> comps = new Stack();
/*      */         
/*  880 */         boolean fixed = false;
/*      */         
/*  882 */         while (temp != null)
/*      */         {
/*  884 */           if (temp.exists()) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*  889 */           String old_name = temp.getName();
/*  890 */           String new_name = "";
/*      */           
/*  892 */           char[] chars = old_name.toCharArray();
/*      */           
/*  894 */           for (char c : chars)
/*      */           {
/*  896 */             int i_c = c;
/*      */             
/*  898 */             if ((i_c >= 0) && (i_c < 32))
/*      */             {
/*  900 */               new_name = new_name + "_";
/*      */             }
/*      */             else
/*      */             {
/*  904 */               new_name = new_name + c;
/*      */             }
/*      */           }
/*      */           
/*  908 */           comps.push(new_name);
/*      */           
/*  910 */           if (!old_name.equals(new_name))
/*      */           {
/*  912 */             fixed = true;
/*      */           }
/*      */           
/*  915 */           temp = temp.getParentFile();
/*      */         }
/*      */         
/*  918 */         if (fixed)
/*      */         {
/*  920 */           while (!comps.isEmpty())
/*      */           {
/*  922 */             String comp = (String)comps.pop();
/*      */             
/*  924 */             if (comps.isEmpty())
/*      */             {
/*  926 */               String prefix = Base32.encode(new SHA1Simple().calculateHash(relative_file.toString().getBytes("UTF-8"))).substring(0, 4);
/*      */               
/*  928 */               comp = prefix + "_" + comp;
/*      */             }
/*      */             
/*  931 */             temp = new File(temp, comp);
/*      */           }
/*      */           
/*  934 */           Debug.outNoStack("Fixing unsupported file path: " + actual_file.getAbsolutePath() + " -> " + temp.getAbsolutePath());
/*      */           
/*  936 */           state.setFileLink(file_index, target_file, temp);
/*      */           
/*  938 */           return new DiskManagerFileInfoImpl(this, root_dir, relative_file, file_index, pm_info.getTorrentFile(), storage_type);
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
/*  949 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private int[] allocateFiles()
/*      */   {
/*  956 */     int[] fail_result = { -1, -1 };
/*      */     
/*  958 */     Set file_set = new java.util.HashSet();
/*      */     
/*  960 */     DMPieceMapperFile[] pm_files = this.piece_mapper.getFiles();
/*      */     
/*  962 */     DiskManagerFileInfoImpl[] allocated_files = new DiskManagerFileInfoImpl[pm_files.length];
/*      */     
/*  964 */     DownloadManagerState state = this.download_manager.getDownloadState();
/*      */     try
/*      */     {
/*  967 */       allocation_scheduler.register(this);
/*      */       
/*  969 */       setState(2);
/*      */       
/*  971 */       this.allocated = 0L;
/*      */       
/*  973 */       int numNewFiles = 0;
/*  974 */       int notRequiredFiles = 0;
/*      */       
/*  976 */       String root_dir = this.download_manager.getAbsoluteSaveLocation().getParent();
/*      */       
/*  978 */       if (!this.torrent.isSimpleTorrent())
/*      */       {
/*  980 */         root_dir = root_dir + File.separator + this.download_manager.getAbsoluteSaveLocation().getName();
/*      */       }
/*      */       
/*  983 */       root_dir = root_dir + File.separator;
/*      */       
/*  985 */       String[] storage_types = getStorageTypes();
/*      */       
/*  987 */       String incomplete_suffix = state.getAttribute("incompfilesuffix");
/*      */       
/*  989 */       for (int i = 0; i < pm_files.length; i++)
/*      */       {
/*  991 */         if (this.stopping)
/*      */         {
/*  993 */           this.errorMessage = "File allocation interrupted - download is stopping";
/*      */           
/*  995 */           setState(10);
/*      */           int i;
/*  997 */           return fail_result;
/*      */         }
/*      */         
/* 1000 */         DMPieceMapperFile pm_info = pm_files[i];
/*      */         
/* 1002 */         long target_length = pm_info.getLength();
/*      */         
/* 1004 */         File relative_data_file = pm_info.getDataFile();
/*      */         
/*      */         DiskManagerFileInfoImpl fileInfo;
/*      */         try
/*      */         {
/* 1009 */           int storage_type = DiskManagerUtil.convertDMStorageTypeFromString(storage_types[i]);
/*      */           
/* 1011 */           fileInfo = createFileInfo(state, pm_info, i, root_dir, relative_data_file, storage_type);
/*      */           
/* 1013 */           allocated_files[i] = fileInfo;
/*      */           
/* 1015 */           pm_info.setFileInfo(fileInfo);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 1019 */           this.errorMessage = (Debug.getNestedExceptionMessage(e) + " (allocateFiles:" + relative_data_file.toString() + ")");
/*      */           
/* 1021 */           setState(10);
/*      */           int i;
/* 1023 */           return fail_result;
/*      */         }
/*      */         
/* 1026 */         CacheFile cache_file = fileInfo.getCacheFile();
/* 1027 */         File data_file = fileInfo.getFile(true);
/*      */         
/* 1029 */         String file_key = data_file.getAbsolutePath();
/*      */         
/* 1031 */         if (Constants.isWindows)
/*      */         {
/* 1033 */           file_key = file_key.toLowerCase();
/*      */         }
/*      */         
/* 1036 */         if (file_set.contains(file_key))
/*      */         {
/* 1038 */           this.errorMessage = ("File occurs more than once in download: " + data_file.toString() + ".\nRename one of the files in Files view via the right-click menu.");
/*      */           
/* 1040 */           setState(10);
/*      */           int i;
/* 1042 */           return fail_result;
/*      */         }
/*      */         
/* 1045 */         file_set.add(file_key);
/*      */         
/* 1047 */         String ext = data_file.getName();
/*      */         
/* 1049 */         if ((incomplete_suffix != null) && (ext.endsWith(incomplete_suffix)))
/*      */         {
/* 1051 */           ext = ext.substring(0, ext.length() - incomplete_suffix.length());
/*      */         }
/*      */         
/* 1054 */         int separator = ext.lastIndexOf(".");
/*      */         
/* 1056 */         if (separator == -1)
/*      */         {
/* 1058 */           separator = 0;
/*      */         }
/*      */         
/* 1061 */         fileInfo.setExtension(ext.substring(separator));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1067 */         String extensions = COConfigurationManager.getStringParameter("priorityExtensions", "");
/*      */         
/* 1069 */         if (!extensions.equals("")) {
/* 1070 */           boolean bIgnoreCase = COConfigurationManager.getBooleanParameter("priorityExtensionsIgnoreCase");
/* 1071 */           StringTokenizer st = new StringTokenizer(extensions, ";");
/* 1072 */           while (st.hasMoreTokens()) {
/* 1073 */             String extension = st.nextToken();
/* 1074 */             extension = extension.trim();
/* 1075 */             if (!extension.startsWith("."))
/* 1076 */               extension = "." + extension;
/* 1077 */             boolean bHighPriority = bIgnoreCase ? fileInfo.getExtension().equalsIgnoreCase(extension) : fileInfo.getExtension().equals(extension);
/*      */             
/*      */ 
/* 1080 */             if (bHighPriority) {
/* 1081 */               fileInfo.setPriority(1);
/*      */             }
/*      */           }
/*      */         }
/* 1085 */         fileInfo.setDownloaded(0L);
/*      */         
/* 1087 */         int st = cache_file.getStorageType();
/*      */         
/* 1089 */         boolean compact = (st == 2) || (st == 4);
/*      */         
/* 1091 */         boolean mustExistOrAllocate = (!compact) || (RDResumeHandler.fileMustExist(this.download_manager, fileInfo));
/*      */         
/*      */ 
/*      */ 
/* 1095 */         if ((!mustExistOrAllocate) && (cache_file.exists()))
/*      */         {
/* 1097 */           data_file.delete();
/*      */         }
/*      */         
/* 1100 */         if (cache_file.exists())
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/* 1106 */             long existing_length = fileInfo.getCacheFile().getLength();
/*      */             int[] arrayOfInt5;
/* 1108 */             if (existing_length > target_length)
/*      */             {
/* 1110 */               if (COConfigurationManager.getBooleanParameter("File.truncate.if.too.large"))
/*      */               {
/* 1112 */                 fileInfo.setAccessMode(2);
/*      */                 
/* 1114 */                 cache_file.setLength(target_length);
/*      */                 
/* 1116 */                 fileInfo.setAccessMode(1);
/*      */                 
/* 1118 */                 Debug.out("Existing data file length too large [" + existing_length + ">" + target_length + "]: " + data_file.getAbsolutePath() + ", truncating");
/*      */               }
/*      */               else
/*      */               {
/* 1122 */                 this.errorMessage = ("Existing data file length too large [" + existing_length + ">" + target_length + "]: " + data_file.getAbsolutePath());
/*      */                 
/* 1124 */                 setState(10);
/*      */                 int i;
/* 1126 */                 return fail_result;
/*      */               }
/* 1128 */             } else if (existing_length < target_length)
/*      */             {
/* 1130 */               if (!compact)
/*      */               {
/*      */ 
/* 1133 */                 if (!allocateFile(fileInfo, data_file, existing_length, target_length))
/*      */                 {
/*      */                   int i;
/*      */                   
/* 1137 */                   return fail_result;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1143 */             fileAllocFailed(data_file, target_length, false, e);
/*      */             
/* 1145 */             setState(10);
/*      */             int i;
/* 1147 */             return fail_result;
/*      */           }
/*      */           
/* 1150 */           this.allocated += target_length;
/*      */         }
/* 1152 */         else if (mustExistOrAllocate)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1157 */           if (this.download_manager.isDataAlreadyAllocated())
/*      */           {
/* 1159 */             this.errorMessage = ("Data file missing: " + data_file.getAbsolutePath());
/*      */             
/* 1161 */             setState(10);
/*      */             int i;
/* 1163 */             return fail_result;
/*      */           }
/*      */           
/*      */ 
/*      */           try
/*      */           {
/* 1169 */             if (!allocateFile(fileInfo, data_file, -1L, target_length))
/*      */             {
/*      */ 
/*      */ 
/* 1173 */               return fail_result;
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*      */             int i;
/* 1178 */             fileAllocFailed(data_file, target_length, true, e);
/*      */             
/* 1180 */             setState(10);
/*      */             int i;
/* 1182 */             return fail_result;
/*      */           }
/*      */           
/* 1185 */           numNewFiles++;
/*      */         }
/*      */         else
/*      */         {
/* 1189 */           notRequiredFiles++;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1196 */       this.files = allocated_files;
/* 1197 */       this.fileset = new DiskManagerFileInfoSetImpl(this.files, this);
/*      */       
/* 1199 */       loadFilePriorities();
/*      */       
/* 1201 */       this.download_manager.setDataAlreadyAllocated(true);
/*      */       int i;
/* 1203 */       return new int[] { numNewFiles, notRequiredFiles };
/*      */     }
/*      */     finally
/*      */     {
/* 1207 */       allocation_scheduler.unregister(this);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1212 */       if (this.files == null)
/*      */       {
/* 1214 */         for (int i = 0; i < allocated_files.length; i++)
/*      */         {
/* 1216 */           if (allocated_files[i] != null) {
/*      */             try
/*      */             {
/* 1219 */               allocated_files[i].getCacheFile().close();
/*      */             }
/*      */             catch (Throwable e) {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean allocateFile(DiskManagerFileInfoImpl fileInfo, File data_file, long existing_length, long target_length)
/*      */     throws Throwable
/*      */   {
/* 1238 */     while (this.started)
/*      */     {
/* 1240 */       if (allocation_scheduler.getPermission(this)) {
/*      */         break;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1246 */     if (!this.started)
/*      */     {
/*      */ 
/*      */ 
/* 1250 */       return false;
/*      */     }
/*      */     
/* 1253 */     fileInfo.setAccessMode(2);
/*      */     
/* 1255 */     if (COConfigurationManager.getBooleanParameter("Enable incremental file creation"))
/*      */     {
/*      */ 
/*      */ 
/* 1259 */       if (existing_length < 0L)
/*      */       {
/*      */ 
/*      */ 
/* 1263 */         fileInfo.getCacheFile().setLength(0L);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/* 1269 */     else if ((target_length > 0L) && (!Constants.isWindows) && (COConfigurationManager.getBooleanParameter("XFS Allocation")))
/*      */     {
/*      */ 
/*      */ 
/* 1273 */       fileInfo.getCacheFile().setLength(target_length);
/*      */       
/*      */       long resvp_len;
/*      */       long resvp_start;
/*      */       long resvp_len;
/* 1278 */       if (existing_length > 0L)
/*      */       {
/* 1280 */         long resvp_start = existing_length;
/* 1281 */         resvp_len = target_length - existing_length;
/*      */       } else {
/* 1283 */         resvp_start = 0L;
/* 1284 */         resvp_len = target_length;
/*      */       }
/*      */       
/* 1287 */       String[] cmd = { "/usr/sbin/xfs_io", "-c", "resvsp " + resvp_start + " " + resvp_len, data_file.getAbsolutePath() };
/*      */       
/* 1289 */       ByteArrayOutputStream os = new ByteArrayOutputStream();
/* 1290 */       byte[] buffer = new byte['Ѐ'];
/*      */       try {
/* 1292 */         Process p = Runtime.getRuntime().exec(cmd);
/* 1293 */         for (int count = p.getErrorStream().read(buffer); count > 0; count = p.getErrorStream().read(buffer)) {
/* 1294 */           os.write(buffer, 0, count);
/*      */         }
/* 1296 */         os.close();
/* 1297 */         p.waitFor();
/*      */       } catch (IOException e) {
/* 1299 */         String message = MessageText.getString("xfs.allocation.xfs_io.not.found", new String[] { e.getMessage() });
/* 1300 */         Logger.log(new LogAlert(this, false, 3, message));
/*      */       }
/* 1302 */       if (os.size() > 0) {
/* 1303 */         String message = os.toString().trim();
/* 1304 */         if (message.endsWith("is not on an XFS filesystem")) {
/* 1305 */           Logger.log(new LogEvent(this, LogIDs.DISK, "XFS file allocation impossible because \"" + data_file.getAbsolutePath() + "\" is not on an XFS filesystem. Original error reported by xfs_io : \"" + message + "\""));
/*      */         }
/*      */         else {
/* 1308 */           throw new Exception(message);
/*      */         }
/*      */       }
/*      */       
/* 1312 */       this.allocated += target_length;
/*      */     }
/* 1314 */     else if (COConfigurationManager.getBooleanParameter("Zero New"))
/*      */     {
/* 1316 */       boolean successfulAlloc = false;
/*      */       try
/*      */       {
/* 1319 */         successfulAlloc = this.writer.zeroFile(fileInfo, target_length);
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1324 */         fileAllocFailed(data_file, target_length, existing_length == -1L, e);
/*      */         
/* 1326 */         throw e;
/*      */       }
/*      */       finally
/*      */       {
/* 1330 */         if (!successfulAlloc)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 1335 */             fileInfo.getCacheFile().close();
/*      */             
/* 1337 */             fileInfo.getCacheFile().delete();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/*      */ 
/*      */ 
/* 1343 */           setState(10);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 1353 */       fileInfo.getCacheFile().setLength(target_length);
/*      */       
/* 1355 */       this.allocated += target_length;
/*      */     }
/*      */     
/*      */ 
/* 1359 */     fileInfo.setAccessMode(1);
/*      */     
/* 1361 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void fileAllocFailed(File file, long length, boolean is_new, Throwable e)
/*      */   {
/* 1371 */     this.errorMessage = (Debug.getNestedExceptionMessage(e) + " (allocateFiles " + (is_new ? "new" : "existing") + ":" + file.toString() + ")");
/*      */     
/* 1373 */     if (this.errorMessage.contains("not enough space"))
/*      */     {
/* 1375 */       this.errorType = 2;
/*      */       
/* 1377 */       if (length >= 4294967296L)
/*      */       {
/*      */ 
/*      */ 
/* 1381 */         this.errorMessage = MessageText.getString("DiskManager.error.nospace_fat32");
/*      */       }
/*      */       else
/*      */       {
/* 1385 */         this.errorMessage = MessageText.getString("DiskManager.error.nospace");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public DiskAccessController getDiskAccessController()
/*      */   {
/* 1393 */     return disk_access_controller;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enqueueReadRequest(DiskManagerReadRequest request, DiskManagerReadRequestListener listener)
/*      */   {
/* 1401 */     this.reader.readBlock(request, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasOutstandingReadRequestForPiece(int piece_number)
/*      */   {
/* 1408 */     return this.reader.hasOutstandingReadRequestForPiece(piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPieces()
/*      */   {
/* 1414 */     return this.nbPieces;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDone()
/*      */   {
/* 1420 */     return this.percentDone;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPercentDone(int num)
/*      */   {
/* 1427 */     this.percentDone = num;
/*      */   }
/*      */   
/*      */   public long getRemaining()
/*      */   {
/* 1432 */     return this.remaining;
/*      */   }
/*      */   
/*      */ 
/*      */   private void fixupSkippedCalculation()
/*      */   {
/* 1438 */     if (this.skipped_file_set_changed)
/*      */     {
/* 1440 */       DiskManagerFileInfoImpl[] current_files = this.files;
/*      */       
/* 1442 */       if (current_files != null)
/*      */       {
/* 1444 */         this.skipped_file_set_changed = false;
/*      */         try
/*      */         {
/* 1447 */           this.file_piece_mon.enter();
/*      */           
/* 1449 */           long skipped = 0L;
/* 1450 */           long downloaded = 0L;
/*      */           
/* 1452 */           for (int i = 0; i < current_files.length; i++)
/*      */           {
/* 1454 */             DiskManagerFileInfoImpl file = current_files[i];
/*      */             
/* 1456 */             if (file.isSkipped())
/*      */             {
/* 1458 */               skipped += file.getLength();
/* 1459 */               downloaded += file.getDownloaded();
/*      */             }
/*      */           }
/*      */           
/* 1463 */           this.skipped_file_set_size = skipped;
/* 1464 */           this.skipped_but_downloaded = downloaded;
/*      */         }
/*      */         finally {
/* 1467 */           this.file_piece_mon.exit();
/*      */         }
/*      */         
/* 1470 */         DownloadManagerStats stats = this.download_manager.getStats();
/*      */         
/* 1472 */         if ((stats instanceof DownloadManagerStatsImpl)) {
/* 1473 */           ((DownloadManagerStatsImpl)stats).setSkippedFileStats(this.skipped_file_set_size, this.skipped_but_downloaded);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getRemainingExcludingDND()
/*      */   {
/* 1482 */     fixupSkippedCalculation();
/*      */     
/* 1484 */     long rem = this.remaining - (this.skipped_file_set_size - this.skipped_but_downloaded);
/*      */     
/* 1486 */     if (rem < 0L)
/*      */     {
/* 1488 */       rem = 0L;
/*      */     }
/*      */     
/* 1491 */     return rem;
/*      */   }
/*      */   
/*      */   public long getSizeExcludingDND() {
/* 1495 */     fixupSkippedCalculation();
/*      */     
/* 1497 */     return this.totalLength - this.skipped_file_set_size;
/*      */   }
/*      */   
/*      */   public int getPercentDoneExcludingDND() {
/* 1501 */     long sizeExcludingDND = getSizeExcludingDND();
/* 1502 */     if (sizeExcludingDND <= 0L) {
/* 1503 */       return 0;
/*      */     }
/* 1505 */     float pct = (float)(sizeExcludingDND - getRemainingExcludingDND()) / (float)sizeExcludingDND;
/* 1506 */     return (int)(1000.0F * pct);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getAllocated()
/*      */   {
/* 1512 */     return this.allocated;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAllocated(long num)
/*      */   {
/* 1519 */     this.allocated = num;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPieceDone(DiskManagerPieceImpl dmPiece, boolean done)
/*      */   {
/* 1531 */     int piece_number = dmPiece.getPieceNumber();
/* 1532 */     int piece_length = dmPiece.getLength();
/*      */     try
/*      */     {
/* 1535 */       this.file_piece_mon.enter();
/*      */       
/* 1537 */       if (dmPiece.isDone() != done)
/*      */       {
/* 1539 */         dmPiece.setDoneSupport(done);
/*      */         
/* 1541 */         if (done) {
/* 1542 */           this.remaining -= piece_length;
/*      */         } else {
/* 1544 */           this.remaining += piece_length;
/*      */         }
/* 1546 */         DMPieceList piece_list = getPieceList(piece_number);
/*      */         
/* 1548 */         for (int i = 0; i < piece_list.size(); i++)
/*      */         {
/*      */ 
/* 1551 */           DMPieceMapEntry piece_map_entry = piece_list.get(i);
/*      */           
/* 1553 */           DiskManagerFileInfoImpl this_file = piece_map_entry.getFile();
/*      */           
/* 1555 */           long file_length = this_file.getLength();
/*      */           
/* 1557 */           long file_done = this_file.getDownloaded();
/*      */           
/* 1559 */           long file_done_before = file_done;
/*      */           
/* 1561 */           if (done) {
/* 1562 */             file_done += piece_map_entry.getLength();
/*      */           } else {
/* 1564 */             file_done -= piece_map_entry.getLength();
/*      */           }
/* 1566 */           if (file_done < 0L)
/*      */           {
/* 1568 */             Debug.out("piece map entry length negative");
/*      */             
/* 1570 */             file_done = 0L;
/*      */           }
/* 1572 */           else if (file_done > file_length)
/*      */           {
/* 1574 */             Debug.out("piece map entry length too large");
/*      */             
/* 1576 */             file_done = file_length;
/*      */           }
/*      */           
/* 1579 */           if (this_file.isSkipped())
/*      */           {
/* 1581 */             this.skipped_but_downloaded += file_done - file_done_before;
/*      */           }
/*      */           
/* 1584 */           this_file.setDownloaded(file_done);
/*      */           
/*      */ 
/*      */ 
/* 1588 */           if (file_done == file_length) {
/*      */             try
/*      */             {
/* 1591 */               DownloadManagerState state = this.download_manager.getDownloadState();
/*      */               
/*      */               try
/*      */               {
/* 1595 */                 String suffix = state.getAttribute("incompfilesuffix");
/*      */                 
/* 1597 */                 if ((suffix != null) && (suffix.length() > 0))
/*      */                 {
/* 1599 */                   String prefix = state.getAttribute("dnd_pfx");
/*      */                   
/* 1601 */                   if (prefix == null)
/*      */                   {
/* 1603 */                     prefix = "";
/*      */                   }
/*      */                   
/* 1606 */                   File base_file = this_file.getFile(false);
/*      */                   
/* 1608 */                   int file_index = this_file.getIndex();
/*      */                   
/* 1610 */                   File link = state.getFileLink(file_index, base_file);
/*      */                   
/* 1612 */                   if (link != null)
/*      */                   {
/* 1614 */                     String name = link.getName();
/*      */                     
/* 1616 */                     if ((name.endsWith(suffix)) && (name.length() > suffix.length()))
/*      */                     {
/* 1618 */                       String new_name = name.substring(0, name.length() - suffix.length());
/*      */                       
/* 1620 */                       if (!this_file.isSkipped())
/*      */                       {
/*      */ 
/*      */ 
/* 1624 */                         if ((prefix.length() > 0) && (new_name.startsWith(prefix)))
/*      */                         {
/* 1626 */                           new_name = new_name.substring(prefix.length());
/*      */                         }
/*      */                       }
/*      */                       
/* 1630 */                       File new_file = new File(link.getParentFile(), new_name);
/*      */                       
/* 1632 */                       if (!new_file.exists())
/*      */                       {
/* 1634 */                         this_file.renameFile(new_name);
/*      */                         
/* 1636 */                         if (base_file.equals(new_file))
/*      */                         {
/* 1638 */                           state.setFileLink(file_index, base_file, null);
/*      */                         }
/*      */                         else
/*      */                         {
/* 1642 */                           state.setFileLink(file_index, base_file, new_file);
/*      */ 
/*      */                         }
/*      */                         
/*      */ 
/*      */                       }
/*      */                       
/*      */ 
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                   }
/* 1655 */                   else if (this_file.getTorrentFile().getTorrent().isSimpleTorrent())
/*      */                   {
/* 1657 */                     File save_location = this.download_manager.getSaveLocation();
/*      */                     
/* 1659 */                     String name = save_location.getName();
/*      */                     
/* 1661 */                     if ((name.endsWith(suffix)) && (name.length() > suffix.length()))
/*      */                     {
/* 1663 */                       String new_name = name.substring(0, name.length() - suffix.length());
/*      */                       
/* 1665 */                       if (!this_file.isSkipped())
/*      */                       {
/*      */ 
/*      */ 
/* 1669 */                         if ((prefix.length() > 0) && (new_name.startsWith(prefix)))
/*      */                         {
/* 1671 */                           new_name = new_name.substring(prefix.length());
/*      */                         }
/*      */                       }
/*      */                       
/* 1675 */                       File new_file = new File(save_location.getParentFile(), new_name);
/*      */                       
/* 1677 */                       if (!new_file.exists())
/*      */                       {
/* 1679 */                         this_file.renameFile(new_name);
/*      */                         
/* 1681 */                         if (save_location.equals(new_file))
/*      */                         {
/* 1683 */                           state.setFileLink(0, save_location, null);
/*      */                         }
/*      */                         else
/*      */                         {
/* 1687 */                           state.setFileLink(0, save_location, new_file);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               finally
/*      */               {
/* 1696 */                 if (this_file.getAccessMode() == 2)
/*      */                 {
/* 1698 */                   this_file.setAccessMode(1);
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/* 1703 */                 if (getState() == 4)
/*      */                 {
/* 1705 */                   state.setLongParameter("stats.download.file.completed.time", SystemTime.getCurrentTime());
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 1710 */               setFailed("Disk access error - " + Debug.getNestedExceptionMessage(e));
/*      */               
/* 1712 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1722 */         if (getState() == 4)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1727 */           this.listeners.dispatch(3, dmPiece);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1732 */       this.file_piece_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void accessModeChanged(DiskManagerFileInfoImpl file, int old_mode, int new_mode)
/*      */   {
/* 1743 */     this.listeners.dispatch(4, new Object[] { file, new Integer(old_mode), new Integer(new_mode) });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DiskManagerPiece[] getPieces()
/*      */   {
/* 1750 */     return this.pieces;
/*      */   }
/*      */   
/*      */   public DiskManagerPiece getPiece(int PieceNumber)
/*      */   {
/* 1755 */     return this.pieces[PieceNumber];
/*      */   }
/*      */   
/*      */   public int getPieceLength() {
/* 1759 */     return this.pieceLength;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPieceLength(int piece_number)
/*      */   {
/* 1766 */     if (piece_number == this.nbPieces - 1)
/*      */     {
/* 1768 */       return this.lastPieceLength;
/*      */     }
/*      */     
/*      */ 
/* 1772 */     return this.pieceLength;
/*      */   }
/*      */   
/*      */   public long getTotalLength()
/*      */   {
/* 1777 */     return this.totalLength;
/*      */   }
/*      */   
/*      */   public int getLastPieceLength() {
/* 1781 */     return this.lastPieceLength;
/*      */   }
/*      */   
/*      */   public int getState() {
/* 1785 */     return this.state_set_via_method;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setState(int _state)
/*      */   {
/* 1794 */     if (this.state_set_via_method == 10)
/*      */     {
/* 1796 */       if (_state != 10)
/*      */       {
/* 1798 */         Debug.out("DiskManager: attempt to move from faulty state to " + _state);
/*      */       }
/*      */       
/* 1801 */       return;
/*      */     }
/*      */     
/* 1804 */     if (this.state_set_via_method != _state)
/*      */     {
/* 1806 */       int[] params = { this.state_set_via_method, _state };
/*      */       
/* 1808 */       this.state_set_via_method = _state;
/*      */       
/* 1810 */       if (_state == 10)
/*      */       {
/* 1812 */         if (this.errorType == 0)
/*      */         {
/* 1814 */           this.errorType = 1;
/*      */         }
/*      */       }
/*      */       
/* 1818 */       this.listeners.dispatch(1, params);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DiskManagerFileInfo[] getFiles()
/*      */   {
/* 1826 */     return this.files;
/*      */   }
/*      */   
/*      */   public DiskManagerFileInfoSet getFileSet() {
/* 1830 */     return this.fileset;
/*      */   }
/*      */   
/*      */   public String getErrorMessage() {
/* 1834 */     return this.errorMessage;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getErrorType()
/*      */   {
/* 1840 */     return this.errorType;
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
/*      */   public void setFailed(final String reason)
/*      */   {
/* 1853 */     new AEThread("DiskManager:setFailed")
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/* 1858 */         DiskManagerImpl.this.errorMessage = reason;
/*      */         
/* 1860 */         Logger.log(new LogAlert(DiskManagerImpl.this, false, 3, DiskManagerImpl.this.errorMessage));
/*      */         
/*      */ 
/*      */ 
/* 1864 */         DiskManagerImpl.this.setState(10);
/*      */         
/* 1866 */         DiskManagerImpl.this.stop(false);
/*      */       }
/*      */     }.start();
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
/*      */   public void setFailed(final DiskManagerFileInfo file, final String reason)
/*      */   {
/* 1882 */     new AEThread("DiskManager:setFailed")
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/* 1887 */         DiskManagerImpl.this.errorMessage = reason;
/*      */         
/* 1889 */         Logger.log(new LogAlert(DiskManagerImpl.this, false, 3, DiskManagerImpl.this.errorMessage));
/*      */         
/*      */ 
/*      */ 
/* 1893 */         DiskManagerImpl.this.setState(10);
/*      */         
/* 1895 */         DiskManagerImpl.this.stop(false);
/*      */         
/* 1897 */         RDResumeHandler.recheckFile(DiskManagerImpl.this.download_manager, file);
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCacheMode()
/*      */   {
/* 1905 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public long[] getReadStats()
/*      */   {
/* 1911 */     if (this.reader == null)
/*      */     {
/* 1913 */       return new long[] { 0L, 0L };
/*      */     }
/*      */     
/* 1916 */     return this.reader.getStats();
/*      */   }
/*      */   
/*      */ 
/*      */   public DMPieceMap getPieceMap()
/*      */   {
/* 1922 */     DMPieceMap map = this.piece_map_use_accessor;
/*      */     
/* 1924 */     if (map == null)
/*      */     {
/*      */ 
/*      */ 
/* 1928 */       this.piece_map_use_accessor = (map = this.piece_mapper.getPieceMap());
/*      */     }
/*      */     
/* 1931 */     this.piece_map_use_accessor_time = SystemTime.getCurrentTime();
/*      */     
/* 1933 */     return map;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DMPieceList getPieceList(int piece_number)
/*      */   {
/* 1940 */     DMPieceMap map = getPieceMap();
/*      */     
/* 1942 */     return map.getPieceList(piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void checkFreePieceList(boolean force_discard)
/*      */   {
/* 1949 */     if (this.piece_map_use_accessor == null)
/*      */     {
/* 1951 */       return;
/*      */     }
/*      */     
/* 1954 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1956 */     if (!force_discard)
/*      */     {
/* 1958 */       if (now < this.piece_map_use_accessor_time)
/*      */       {
/* 1960 */         this.piece_map_use_accessor_time = now;
/*      */         
/* 1962 */         return;
/*      */       }
/* 1964 */       if (now - this.piece_map_use_accessor_time < 120000L)
/*      */       {
/* 1966 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1972 */     this.piece_map_use_accessor = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] getPieceHash(int piece_number)
/*      */     throws TOTorrentException
/*      */   {
/* 1981 */     return this.torrent.getPieces()[piece_number];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DiskManagerReadRequest createReadRequest(int pieceNumber, int offset, int length)
/*      */   {
/* 1990 */     return this.reader.createReadRequest(pieceNumber, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public DiskManagerCheckRequest createCheckRequest(int pieceNumber, Object user_data)
/*      */   {
/* 1998 */     return this.checker.createCheckRequest(pieceNumber, user_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasOutstandingCheckRequestForPiece(int piece_number)
/*      */   {
/* 2005 */     return this.checker.hasOutstandingCheckRequestForPiece(piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enqueueCompleteRecheckRequest(DiskManagerCheckRequest request, DiskManagerCheckRequestListener listener)
/*      */   {
/* 2014 */     this.checker.enqueueCompleteRecheckRequest(request, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enqueueCheckRequest(DiskManagerCheckRequest request, DiskManagerCheckRequestListener listener)
/*      */   {
/* 2022 */     this.checker.enqueueCheckRequest(request, listener);
/*      */   }
/*      */   
/*      */   public int getCompleteRecheckStatus()
/*      */   {
/* 2027 */     return this.checker.getCompleteRecheckStatus();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMoveProgress()
/*      */   {
/* 2033 */     if (this.move_in_progress)
/*      */     {
/* 2035 */       return this.move_progress;
/*      */     }
/*      */     
/* 2038 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPieceCheckingEnabled(boolean enabled)
/*      */   {
/* 2045 */     this.checking_enabled = enabled;
/*      */     
/* 2047 */     this.checker.setCheckingEnabled(enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DirectByteBuffer readBlock(int pieceNumber, int offset, int length)
/*      */   {
/* 2056 */     return this.reader.readBlock(pieceNumber, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DiskManagerWriteRequest createWriteRequest(int pieceNumber, int offset, DirectByteBuffer data, Object user_data)
/*      */   {
/* 2066 */     return this.writer.createWriteRequest(pieceNumber, offset, data, user_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enqueueWriteRequest(DiskManagerWriteRequest request, DiskManagerWriteRequestListener listener)
/*      */   {
/* 2074 */     this.writer.writeBlock(request, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasOutstandingWriteRequestForPiece(int piece_number)
/*      */   {
/* 2081 */     return this.writer.hasOutstandingWriteRequestForPiece(piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean checkBlockConsistencyForWrite(String originator, int pieceNumber, int offset, DirectByteBuffer data)
/*      */   {
/* 2091 */     if (pieceNumber < 0) {
/* 2092 */       if (Logger.isEnabled()) {
/* 2093 */         Logger.log(new LogEvent(this, LOGID, 3, "Write invalid: " + originator + " pieceNumber=" + pieceNumber + " < 0"));
/*      */       }
/* 2095 */       return false;
/*      */     }
/* 2097 */     if (pieceNumber >= this.nbPieces) {
/* 2098 */       if (Logger.isEnabled()) {
/* 2099 */         Logger.log(new LogEvent(this, LOGID, 3, "Write invalid: " + originator + " pieceNumber=" + pieceNumber + " >= this.nbPieces=" + this.nbPieces));
/*      */       }
/*      */       
/* 2102 */       return false;
/*      */     }
/* 2104 */     int length = this.pieceLength;
/* 2105 */     if (pieceNumber == this.nbPieces - 1) {
/* 2106 */       length = this.lastPieceLength;
/*      */     }
/* 2108 */     if (offset < 0) {
/* 2109 */       if (Logger.isEnabled()) {
/* 2110 */         Logger.log(new LogEvent(this, LOGID, 3, "Write invalid: " + originator + " offset=" + offset + " < 0"));
/*      */       }
/* 2112 */       return false;
/*      */     }
/* 2114 */     if (offset > length) {
/* 2115 */       if (Logger.isEnabled()) {
/* 2116 */         Logger.log(new LogEvent(this, LOGID, 3, "Write invalid: " + originator + " offset=" + offset + " > length=" + length));
/*      */       }
/* 2118 */       return false;
/*      */     }
/* 2120 */     int size = data.remaining((byte)8);
/* 2121 */     if (size <= 0) {
/* 2122 */       if (Logger.isEnabled()) {
/* 2123 */         Logger.log(new LogEvent(this, LOGID, 3, "Write invalid: " + originator + " size=" + size + " <= 0"));
/*      */       }
/* 2125 */       return false;
/*      */     }
/* 2127 */     if (offset + size > length) {
/* 2128 */       if (Logger.isEnabled()) {
/* 2129 */         Logger.log(new LogEvent(this, LOGID, 3, "Write invalid: " + originator + " offset=" + offset + " + size=" + size + " > length=" + length));
/*      */       }
/*      */       
/* 2132 */       return false;
/*      */     }
/* 2134 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean checkBlockConsistencyForRead(String originator, boolean peer_request, int pieceNumber, int offset, int length)
/*      */   {
/* 2145 */     return DiskManagerUtil.checkBlockConsistencyForRead(this, originator, peer_request, pieceNumber, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean checkBlockConsistencyForHint(String originator, int pieceNumber, int offset, int length)
/*      */   {
/* 2155 */     return DiskManagerUtil.checkBlockConsistencyForHint(this, originator, pieceNumber, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void saveResumeData(boolean interim_save)
/*      */     throws Exception
/*      */   {
/* 2164 */     this.resume_handler.saveResumeData(interim_save);
/*      */   }
/*      */   
/*      */   public void downloadEnded(DiskManager.OperationStatus op_status) {
/* 2168 */     moveDownloadFilesWhenEndedOrRemoved(false, true, op_status);
/*      */   }
/*      */   
/*      */   public void downloadRemoved() {
/* 2172 */     moveDownloadFilesWhenEndedOrRemoved(true, true, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean moveDownloadFilesWhenEndedOrRemoved(boolean removing, boolean torrent_file_exists, final DiskManager.OperationStatus op_status)
/*      */   {
/*      */     try
/*      */     {
/* 2182 */       this.start_stop_mon.enter();
/*      */       
/* 2184 */       boolean ending = !removing;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2192 */       if (ending) {
/* 2193 */         if (this.alreadyMoved) return false;
/* 2194 */         this.alreadyMoved = true;
/*      */       }
/*      */       SaveLocationChange move_details;
/*      */       SaveLocationChange move_details;
/* 2198 */       if (removing) {
/* 2199 */         move_details = DownloadManagerMoveHandler.onRemoval(this.download_manager);
/*      */       }
/*      */       else {
/* 2202 */         DownloadManagerMoveHandler.onCompletion(this.download_manager, new org.gudy.azureus2.core3.download.impl.DownloadManagerMoveHandler.MoveCallback()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void perform(SaveLocationChange move_details)
/*      */           {
/*      */ 
/*      */ 
/* 2210 */             DiskManagerImpl.this.moveFiles(move_details, true, op_status);
/*      */           }
/*      */           
/* 2213 */         });
/* 2214 */         move_details = null;
/*      */       }
/*      */       
/* 2217 */       if (move_details != null)
/*      */       {
/* 2219 */         moveFiles(move_details, true, op_status);
/*      */       }
/*      */       
/* 2222 */       return 1;
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 2227 */       this.start_stop_mon.exit();
/*      */       
/* 2229 */       if (!removing) {
/*      */         try {
/* 2231 */           saveResumeData(false);
/*      */         } catch (Throwable e) {
/* 2233 */           setFailed("Resume data save fails: " + Debug.getNestedExceptionMessage(e));
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
/*      */   public void moveDataFiles(File new_parent_dir, String new_name, DiskManager.OperationStatus op_status)
/*      */   {
/* 2246 */     SaveLocationChange loc_change = new SaveLocationChange();
/*      */     
/* 2248 */     loc_change.download_location = new_parent_dir;
/* 2249 */     loc_change.download_name = new_name;
/*      */     
/* 2251 */     moveFiles(loc_change, false, op_status);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void moveFiles(SaveLocationChange loc_change, boolean change_to_read_only, DiskManager.OperationStatus op_status)
/*      */   {
/* 2260 */     boolean move_files = false;
/* 2261 */     if (loc_change.hasDownloadChange()) {
/* 2262 */       move_files = !isFileDestinationIsItself(loc_change);
/*      */     }
/*      */     try
/*      */     {
/* 2266 */       this.start_stop_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2272 */       boolean files_moved = true;
/* 2273 */       if (move_files) {
/*      */         try {
/* 2275 */           this.move_progress = 0;
/* 2276 */           this.move_in_progress = true;
/*      */           
/* 2278 */           files_moved = moveDataFiles0(loc_change, change_to_read_only, op_status);
/*      */         }
/*      */         finally
/*      */         {
/* 2282 */           this.move_in_progress = false;
/*      */         }
/*      */       }
/*      */       
/* 2286 */       if ((loc_change.hasTorrentChange()) && ((files_moved) || (!move_files))) {
/* 2287 */         moveTorrentFile(loc_change);
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 2291 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/* 2295 */       this.start_stop_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private void logMoveFileError(String destination_path, String message)
/*      */   {
/* 2301 */     Logger.log(new LogEvent(this, LOGID, 3, message));
/* 2302 */     Logger.logTextResource(new LogAlert(this, true, 3, "DiskManager.alert.movefilefails"), new String[] { destination_path, message });
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean isFileDestinationIsItself(SaveLocationChange loc_change)
/*      */   {
/* 2308 */     File old_location = this.download_manager.getAbsoluteSaveLocation();
/* 2309 */     File new_location = loc_change.normaliseDownloadLocation(old_location);
/*      */     try {
/* 2311 */       old_location = old_location.getCanonicalFile();
/* 2312 */       new_location = new_location.getCanonicalFile();
/* 2313 */       if (old_location.equals(new_location)) return true;
/* 2314 */       if ((!this.download_manager.getTorrent().isSimpleTorrent()) && (FileUtil.isAncestorOf(new_location, old_location))) {
/* 2315 */         String msg = "Target is sub-directory of files";
/* 2316 */         logMoveFileError(new_location.toString(), msg);
/* 2317 */         return true;
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 2321 */       Debug.out(e);
/*      */     }
/* 2323 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean moveDataFiles0(SaveLocationChange loc_change, boolean change_to_read_only, DiskManager.OperationStatus op_status)
/*      */     throws Exception
/*      */   {
/*      */     try
/*      */     {
/* 2334 */       this.file_piece_mon.enter();
/*      */     }
/*      */     finally
/*      */     {
/* 2338 */       this.file_piece_mon.exit();
/*      */     }
/*      */     
/* 2341 */     File move_to_dir_name = loc_change.download_location;
/* 2342 */     if (move_to_dir_name == null) { move_to_dir_name = this.download_manager.getAbsoluteSaveLocation().getParentFile();
/*      */     }
/* 2344 */     String move_to_dir = move_to_dir_name.toString();
/* 2345 */     String new_name = loc_change.download_name;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2359 */     if (this.files == null) { return false;
/*      */     }
/* 2361 */     if (isFileDestinationIsItself(loc_change)) { return false;
/*      */     }
/* 2363 */     final boolean[] got_there = { false };
/*      */     
/* 2365 */     if (op_status != null)
/*      */     {
/* 2367 */       op_status.gonnaTakeAWhile(new DiskManager.GettingThere()
/*      */       {
/*      */         /* Error */
/*      */         public boolean hasGotThere()
/*      */         {
/*      */           // Byte code:
/*      */           //   0: aload_0
/*      */           //   1: getfield 32	org/gudy/azureus2/core3/disk/impl/DiskManagerImpl$8:val$got_there	[Z
/*      */           //   4: dup
/*      */           //   5: astore_1
/*      */           //   6: monitorenter
/*      */           //   7: aload_0
/*      */           //   8: getfield 32	org/gudy/azureus2/core3/disk/impl/DiskManagerImpl$8:val$got_there	[Z
/*      */           //   11: iconst_0
/*      */           //   12: baload
/*      */           //   13: aload_1
/*      */           //   14: monitorexit
/*      */           //   15: ireturn
/*      */           //   16: astore_2
/*      */           //   17: aload_1
/*      */           //   18: monitorexit
/*      */           //   19: aload_2
/*      */           //   20: athrow
/*      */           // Line number table:
/*      */           //   Java source line #2373	-> byte code offset #0
/*      */           //   Java source line #2375	-> byte code offset #7
/*      */           //   Java source line #2376	-> byte code offset #16
/*      */           // Local variable table:
/*      */           //   start	length	slot	name	signature
/*      */           //   0	21	0	this	8
/*      */           //   5	13	1	Ljava/lang/Object;	Object
/*      */           //   16	4	2	localObject1	Object
/*      */           // Exception table:
/*      */           //   from	to	target	type
/*      */           //   7	15	16	finally
/*      */           //   16	19	16	finally
/*      */         }
/*      */       });
/*      */     }
/*      */     try
/*      */     {
/* 2382 */       boolean simple_torrent = this.download_manager.getTorrent().isSimpleTorrent();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2388 */       File save_location = this.download_manager.getAbsoluteSaveLocation();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2393 */       String move_from_name = save_location.getName();
/* 2394 */       String move_from_dir = save_location.getParentFile().getCanonicalFile().getPath();
/*      */       
/* 2396 */       final File[] new_files = new File[this.files.length];
/*      */       
/* 2398 */       File[] old_files = new File[this.files.length];
/* 2399 */       boolean[] link_only = new boolean[this.files.length];
/*      */       
/* 2401 */       long total_bytes = 0L;
/*      */       
/* 2403 */       final long[] file_lengths_to_move = new long[this.files.length];
/*      */       
/* 2405 */       for (int i = 0; i < this.files.length; i++)
/*      */       {
/* 2407 */         File old_file = this.files[i].getFile(false);
/*      */         
/* 2409 */         File linked_file = FMFileManagerFactory.getSingleton().getFileLink(this.torrent, i, old_file);
/*      */         
/* 2411 */         if (!linked_file.equals(old_file))
/*      */         {
/* 2413 */           if (simple_torrent)
/*      */           {
/*      */ 
/*      */ 
/* 2417 */             if (linked_file.getParentFile().getCanonicalPath().equals(save_location.getParentFile().getCanonicalPath()))
/*      */             {
/* 2419 */               old_file = linked_file;
/*      */             }
/*      */             else
/*      */             {
/* 2423 */               link_only[i] = true;
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/* 2430 */           else if (linked_file.getCanonicalPath().startsWith(save_location.getCanonicalPath()))
/*      */           {
/* 2432 */             old_file = linked_file;
/*      */           }
/*      */           else
/*      */           {
/* 2436 */             link_only[i] = true;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2461 */         old_files[i] = old_file;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2470 */         String old_parent_path = old_file.getCanonicalFile().getParent();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         String sub_path;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2481 */         if (old_parent_path.startsWith(move_from_dir))
/*      */         {
/* 2483 */           sub_path = old_parent_path.substring(move_from_dir.length());
/*      */         }
/*      */         else
/*      */         {
/* 2487 */           logMoveFileError(move_to_dir, "Could not determine relative path for file - " + old_parent_path);
/*      */           
/* 2489 */           throw new IOException("relative path assertion failed: move_from_dir=\"" + move_from_dir + "\", old_parent_path=\"" + old_parent_path + "\"");
/*      */         }
/*      */         
/*      */         String sub_path;
/*      */         
/* 2494 */         if (sub_path.startsWith(File.separator))
/*      */         {
/* 2496 */           sub_path = sub_path.substring(1);
/*      */         }
/*      */         
/*      */         File new_file;
/*      */         
/*      */         String new_path;
/*      */         File new_file;
/* 2503 */         if (new_name == null)
/*      */         {
/* 2505 */           new_file = new File(new File(move_to_dir, sub_path), old_file.getName());
/*      */         }
/*      */         else
/*      */         {
/*      */           File new_file;
/*      */           
/* 2511 */           if (simple_torrent)
/*      */           {
/* 2513 */             new_file = new File(new File(move_to_dir, sub_path), new_name);
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 2519 */             int pos = sub_path.indexOf(File.separator);
/*      */             String new_path;
/* 2521 */             if (pos == -1) {
/* 2522 */               new_path = new_name;
/*      */             }
/*      */             else
/*      */             {
/* 2526 */               String sub_sub_path = sub_path.substring(pos);
/* 2527 */               String expected_old_name = sub_path.substring(0, pos);
/* 2528 */               new_path = new_name + sub_sub_path;
/* 2529 */               boolean assert_expected_old_name = expected_old_name.equals(save_location.getName());
/* 2530 */               if (!assert_expected_old_name) {
/* 2531 */                 Debug.out("Assertion check for renaming file in multi-name torrent " + (assert_expected_old_name ? "passed" : "failed") + "\n" + "  Old parent path: " + old_parent_path + "\n" + "  Subpath: " + sub_path + "\n" + "  Sub-subpath: " + sub_sub_path + "\n" + "  Expected old name: " + expected_old_name + "\n" + "  Torrent pre-move name: " + save_location.getName() + "\n" + "  New torrent name: " + new_name + "\n" + "  Old file: " + old_file + "\n" + "  Linked file: " + linked_file + "\n" + "\n" + "  Move-to-dir: " + move_to_dir + "\n" + "  New path: " + new_path + "\n" + "  Old file [name]: " + old_file.getName() + "\n");
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2549 */             new_file = new File(new File(move_to_dir, new_path), old_file.getName());
/*      */           }
/*      */         }
/*      */         
/* 2553 */         new_files[i] = new_file;
/*      */         
/* 2555 */         if (link_only[i] == 0)
/*      */         {
/* 2557 */           total_bytes += (file_lengths_to_move[i] = old_file.length());
/*      */           
/* 2559 */           if (new_file.exists())
/*      */           {
/* 2561 */             String msg = "" + linked_file.getName() + " already exists in MoveTo destination dir";
/*      */             
/* 2563 */             Logger.log(new LogEvent(this, LOGID, 3, msg));
/*      */             
/* 2565 */             Logger.logTextResource(new LogAlert(this, true, 3, "DiskManager.alert.movefileexists"), new String[] { old_file.getName() });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2570 */             Debug.out(msg);
/*      */             
/* 2572 */             return 0;
/*      */           }
/*      */           
/* 2575 */           FileUtil.mkdirs(new_file.getParentFile());
/*      */         }
/*      */       }
/*      */       
/* 2579 */       String abs_path = move_to_dir_name.getAbsolutePath();
/*      */       
/* 2581 */       String _average_config_key = null;
/*      */       try
/*      */       {
/* 2584 */         _average_config_key = "dm.move.target.abps." + Base32.encode(abs_path.getBytes("UTF-8"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2588 */         Debug.out(e);
/*      */       }
/*      */       
/* 2591 */       final String average_config_key = _average_config_key;
/*      */       
/*      */ 
/*      */ 
/* 2595 */       if (total_bytes == 0L)
/*      */       {
/* 2597 */         total_bytes = 1L;
/*      */       }
/*      */       
/* 2600 */       long done_bytes = 0L;
/*      */       
/* 2602 */       final Object progress_lock = new Object();
/* 2603 */       final int[] current_file_index = { 0 };
/* 2604 */       final long[] current_file_bs = { 0L };
/* 2605 */       final long f_total_bytes = total_bytes;
/*      */       
/* 2607 */       final long[] last_progress_bytes = { 0L };
/* 2608 */       final long[] last_progress_update = { SystemTime.getMonotonousTime() };
/*      */       
/* 2610 */       TimerEventPeriodic timer_event1 = SimpleTimer.addPeriodicEvent("MoveFile:speedster", 1000L, new TimerEventPerformer()
/*      */       {
/*      */         private final long start_time;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         private long last_update_processed;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         private long estimated_speed;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/* 2638 */           synchronized (progress_lock)
/*      */           {
/* 2640 */             int file_index = current_file_index[0];
/*      */             
/* 2642 */             if (file_index >= new_files.length)
/*      */             {
/* 2644 */               return;
/*      */             }
/*      */             
/* 2647 */             long now = SystemTime.getMonotonousTime();
/*      */             
/* 2649 */             long last_update = last_progress_update[0];
/* 2650 */             long bytes_moved = last_progress_bytes[0];
/*      */             
/* 2652 */             if (last_update != this.last_update_processed)
/*      */             {
/* 2654 */               this.last_update_processed = last_update;
/*      */               
/* 2656 */               if (bytes_moved > 10485760L)
/*      */               {
/*      */ 
/*      */ 
/* 2660 */                 long elapsed = now - this.start_time;
/*      */                 
/* 2662 */                 this.estimated_speed = (bytes_moved * 1000L / elapsed);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2668 */             long secs_since_last_update = (now - last_update) / 1000L;
/*      */             
/* 2670 */             if (secs_since_last_update > 2L)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 2675 */               long file_start_overall = current_file_bs[0];
/* 2676 */               long file_end_overall = file_start_overall + file_lengths_to_move[file_index];
/* 2677 */               long bytes_of_file_remaining = file_end_overall - bytes_moved;
/*      */               
/* 2679 */               long pretend_bytes = 0L;
/*      */               
/* 2681 */               long current_speed = this.estimated_speed;
/* 2682 */               long current_remaining = bytes_of_file_remaining;
/* 2683 */               long current_added = 0L;
/*      */               
/* 2685 */               int percentage_to_slow_at = 80;
/*      */               
/*      */ 
/*      */ 
/* 2689 */               for (int i = 0; i < secs_since_last_update; i++)
/*      */               {
/* 2691 */                 current_added += current_speed;
/* 2692 */                 pretend_bytes += current_speed;
/*      */                 
/*      */ 
/*      */ 
/* 2696 */                 if (current_added > percentage_to_slow_at * current_remaining / 100L)
/*      */                 {
/* 2698 */                   percentage_to_slow_at = 50;
/*      */                   
/* 2700 */                   current_speed /= 2L;
/*      */                   
/* 2702 */                   current_remaining = bytes_of_file_remaining - pretend_bytes;
/*      */                   
/* 2704 */                   current_added = 0L;
/*      */                   
/* 2706 */                   if (current_speed < 1024L)
/*      */                   {
/* 2708 */                     current_speed = 1024L;
/*      */                   }
/*      */                 }
/*      */                 
/* 2712 */                 if (pretend_bytes >= bytes_of_file_remaining)
/*      */                 {
/* 2714 */                   pretend_bytes = bytes_of_file_remaining;
/*      */                   
/* 2716 */                   break;
/*      */                 }
/*      */               }
/*      */               
/* 2720 */               long pretend_bytes_moved = bytes_moved + pretend_bytes;
/*      */               
/* 2722 */               DiskManagerImpl.this.move_progress = ((int)(1000L * pretend_bytes_moved / f_total_bytes));
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/* 2729 */       });
/* 2730 */       TimerEventPeriodic timer_event2 = SimpleTimer.addPeriodicEvent("MoveFile:observer", 500L, new TimerEventPerformer()
/*      */       {
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */           int index;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           File file;
/*      */           
/*      */ 
/*      */ 
/* 2743 */           synchronized (progress_lock)
/*      */           {
/* 2745 */             index = current_file_index[0];
/*      */             
/* 2747 */             if (index >= new_files.length)
/*      */             {
/* 2749 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 2754 */             file = new_files[index];
/*      */           }
/*      */           
/* 2757 */           long file_length = file.length();
/*      */           
/* 2759 */           synchronized (progress_lock)
/*      */           {
/* 2761 */             if (index == current_file_index[0])
/*      */             {
/* 2763 */               long done_bytes = current_file_bs[0] + file_length;
/*      */               
/* 2765 */               DiskManagerImpl.this.move_progress = ((int)(1000L * done_bytes / f_total_bytes));
/*      */               
/* 2767 */               last_progress_update[0] = done_bytes;
/* 2768 */               this.val$last_progress_update[0] = SystemTime.getMonotonousTime();
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 2773 */       });
/* 2774 */       long start = SystemTime.getMonotonousTime();
/*      */       
/*      */       String new_root_dir;
/*      */       String old_root_dir;
/*      */       String new_root_dir;
/* 2779 */       if (simple_torrent)
/*      */       {
/* 2781 */         String old_root_dir = move_from_dir;
/* 2782 */         new_root_dir = move_to_dir;
/*      */       }
/*      */       else
/*      */       {
/* 2786 */         old_root_dir = move_from_dir + File.separator + move_from_name;
/* 2787 */         new_root_dir = move_to_dir + File.separator + (new_name == null ? move_from_name : new_name);
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 2792 */         for (int i = 0; i < this.files.length; i++)
/*      */         {
/* 2794 */           File new_file = new_files[i];
/*      */           
/*      */           try
/*      */           {
/* 2798 */             long initial_done_bytes = done_bytes;
/*      */             
/* 2800 */             this.files[i].moveFile(new_root_dir, new_file, link_only[i]);
/*      */             
/* 2802 */             synchronized (progress_lock)
/*      */             {
/* 2804 */               current_file_index[0] = (i + 1);
/*      */               
/* 2806 */               done_bytes = initial_done_bytes + file_lengths_to_move[i];
/*      */               
/* 2808 */               current_file_bs[0] = done_bytes;
/*      */               
/* 2810 */               this.move_progress = ((int)(1000L * done_bytes / total_bytes));
/*      */               
/* 2812 */               last_progress_bytes[0] = done_bytes;
/* 2813 */               last_progress_update[0] = SystemTime.getMonotonousTime();
/*      */             }
/*      */             
/* 2816 */             if (change_to_read_only)
/*      */             {
/* 2818 */               this.files[i].setAccessMode(1);
/*      */             }
/*      */           }
/*      */           catch (CacheFileManagerException e)
/*      */           {
/* 2823 */             String msg = "Failed to move " + old_files[i].toString() + " to destination " + new_root_dir + ": " + new_file + "/" + link_only[i];
/*      */             
/* 2825 */             Logger.log(new LogEvent(this, LOGID, 3, msg));
/*      */             
/* 2827 */             Logger.logTextResource(new LogAlert(this, true, 3, "DiskManager.alert.movefilefails"), new String[] { old_files[i].toString(), Debug.getNestedExceptionMessage(e) });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2834 */             for (int j = 0; j < i; j++) {
/*      */               try
/*      */               {
/* 2837 */                 this.files[j].moveFile(old_root_dir, old_files[j], link_only[j]);
/*      */               }
/*      */               catch (CacheFileManagerException f)
/*      */               {
/* 2841 */                 Logger.logTextResource(new LogAlert(this, true, 3, "DiskManager.alert.movefilerecoveryfails"), new String[] { old_files[j].toString(), Debug.getNestedExceptionMessage(f) });
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2850 */             j = 0;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2855 */             timer_event1.cancel();
/* 2856 */             timer_event2.cancel();
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2896 */             return j;
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2855 */         timer_event1.cancel();
/* 2856 */         timer_event2.cancel();
/*      */       }
/*      */       
/* 2859 */       long elapsed_secs = (SystemTime.getMonotonousTime() - start) / 1000L;
/*      */       long bps;
/* 2861 */       if ((total_bytes > 10485760L) && (elapsed_secs > 10L))
/*      */       {
/* 2863 */         bps = total_bytes / elapsed_secs;
/*      */         
/* 2865 */         if (average_config_key != null)
/*      */         {
/* 2867 */           COConfigurationManager.setParameter(average_config_key, bps);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2873 */       if (save_location.isDirectory())
/*      */       {
/* 2875 */         TorrentUtils.recursiveEmptyDirDelete(save_location, false);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2880 */       if (new_name == null)
/*      */       {
/* 2882 */         this.download_manager.setTorrentSaveDir(move_to_dir);
/*      */       }
/*      */       else
/*      */       {
/* 2886 */         this.download_manager.setTorrentSaveDir(move_to_dir, new_name);
/*      */       }
/*      */       
/* 2889 */       return 1;
/*      */     }
/*      */     finally
/*      */     {
/* 2893 */       synchronized (got_there)
/*      */       {
/* 2895 */         got_there[0] = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void moveTorrentFile(SaveLocationChange loc_change) {
/* 2901 */     if (!loc_change.hasTorrentChange()) { return;
/*      */     }
/* 2903 */     File old_torrent_file = new File(this.download_manager.getTorrentFileName());
/* 2904 */     File new_torrent_file = loc_change.normaliseTorrentLocation(old_torrent_file);
/*      */     
/* 2906 */     if (!old_torrent_file.exists())
/*      */     {
/* 2908 */       if (Logger.isEnabled())
/* 2909 */         Logger.log(new LogEvent(this, LOGID, 1, "Torrent file '" + old_torrent_file.getPath() + "' has been deleted, move operation ignored"));
/* 2910 */       return;
/*      */     }
/*      */     try {
/* 2913 */       this.download_manager.setTorrentFile(loc_change.torrent_location, loc_change.torrent_name);
/*      */     } catch (DownloadManagerException e) {
/* 2915 */       String msg = "Failed to move " + old_torrent_file.toString() + " to " + new_torrent_file.toString();
/*      */       
/* 2917 */       if (Logger.isEnabled()) {
/* 2918 */         Logger.log(new LogEvent(this, LOGID, 3, msg));
/*      */       }
/* 2920 */       Logger.logTextResource(new LogAlert(this, true, 3, "DiskManager.alert.movefilefails"), new String[] { old_torrent_file.toString(), new_torrent_file.toString() });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2925 */       Debug.out(msg);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TOTorrent getTorrent()
/*      */   {
/* 2932 */     return this.torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(DiskManagerListener l)
/*      */   {
/* 2940 */     this.listeners.addListener(l);
/*      */     
/* 2942 */     int[] params = { getState(), getState() };
/*      */     
/* 2944 */     this.listeners.dispatch(l, 1, params);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DiskManagerListener l)
/*      */   {
/* 2951 */     this.listeners.removeListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasListener(DiskManagerListener l)
/*      */   {
/* 2958 */     return this.listeners.hasListener(l);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void deleteDataFiles(TOTorrent torrent, String torrent_save_dir, String torrent_save_file, boolean force_no_recycle)
/*      */   {
/* 2976 */     if ((torrent == null) || (torrent_save_file == null))
/*      */     {
/* 2978 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2982 */       if (torrent.isSimpleTorrent())
/*      */       {
/* 2984 */         File target = new File(torrent_save_dir, torrent_save_file);
/*      */         
/* 2986 */         target = FMFileManagerFactory.getSingleton().getFileLink(torrent, 0, target.getCanonicalFile());
/*      */         
/* 2988 */         FileUtil.deleteWithRecycle(target, force_no_recycle);
/*      */       }
/*      */       else
/*      */       {
/* 2992 */         PlatformManager mgr = org.gudy.azureus2.platform.PlatformManagerFactory.getPlatformManager();
/* 2993 */         if ((Constants.isOSX) && (torrent_save_file.length() > 0) && (COConfigurationManager.getBooleanParameter("Move Deleted Data To Recycle Bin")) && (!force_no_recycle) && (mgr.hasCapability(org.gudy.azureus2.platform.PlatformManagerCapabilities.RecoverableFileDelete)))
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 3001 */             String dir = torrent_save_dir + File.separatorChar + torrent_save_file + File.separatorChar;
/*      */             
/*      */ 
/*      */ 
/* 3005 */             int numDataFiles = countDataFiles(torrent, torrent_save_dir, torrent_save_file);
/* 3006 */             if (countFiles(new File(dir), numDataFiles) == numDataFiles)
/*      */             {
/* 3008 */               mgr.performRecoverableFileDelete(dir);
/*      */             }
/*      */             else
/*      */             {
/* 3012 */               deleteDataFileContents(torrent, torrent_save_dir, torrent_save_file, force_no_recycle);
/*      */             }
/*      */           }
/*      */           catch (PlatformManagerException ex)
/*      */           {
/* 3017 */             deleteDataFileContents(torrent, torrent_save_dir, torrent_save_file, force_no_recycle);
/*      */           }
/*      */           
/*      */         } else {
/* 3021 */           deleteDataFileContents(torrent, torrent_save_dir, torrent_save_file, force_no_recycle);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3027 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int countFiles(File f, int stopAfterCount)
/*      */   {
/* 3036 */     if (f.isFile())
/*      */     {
/* 3038 */       return 1;
/*      */     }
/*      */     
/* 3041 */     int res = 0;
/*      */     
/* 3043 */     File[] files = f.listFiles();
/*      */     
/* 3045 */     if (files != null)
/*      */     {
/* 3047 */       for (int i = 0; i < files.length; i++)
/*      */       {
/* 3049 */         res += countFiles(files[i], stopAfterCount);
/*      */         
/* 3051 */         if (res > stopAfterCount) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3057 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int countDataFiles(TOTorrent torrent, String torrent_save_dir, String torrent_save_file)
/*      */   {
/*      */     try
/*      */     {
/* 3068 */       int res = 0;
/*      */       
/* 3070 */       LocaleUtilDecoder locale_decoder = LocaleTorrentUtil.getTorrentEncoding(torrent);
/*      */       
/* 3072 */       TOTorrentFile[] files = torrent.getFiles();
/*      */       
/* 3074 */       for (int i = 0; i < files.length; i++)
/*      */       {
/* 3076 */         byte[][] path_comps = files[i].getPathComponents();
/*      */         
/* 3078 */         String path_str = torrent_save_dir + File.separator + torrent_save_file + File.separator;
/*      */         
/* 3080 */         for (int j = 0; j < path_comps.length; j++)
/*      */         {
/* 3082 */           String comp = locale_decoder.decodeString(path_comps[j]);
/*      */           
/* 3084 */           comp = FileUtil.convertOSSpecificChars(comp, j != path_comps.length - 1);
/*      */           
/* 3086 */           path_str = path_str + (j == 0 ? "" : File.separator) + comp;
/*      */         }
/*      */         
/* 3089 */         File file = new File(path_str).getCanonicalFile();
/*      */         
/* 3091 */         File linked_file = FMFileManagerFactory.getSingleton().getFileLink(torrent, i, file);
/*      */         
/* 3093 */         boolean skip = false;
/*      */         
/* 3095 */         if (linked_file != file)
/*      */         {
/* 3097 */           if (!linked_file.getCanonicalPath().startsWith(new File(torrent_save_dir).getCanonicalPath()))
/*      */           {
/* 3099 */             skip = true;
/*      */           }
/*      */         }
/*      */         
/* 3103 */         if ((!skip) && (file.exists()) && (!file.isDirectory()))
/*      */         {
/* 3105 */           res++;
/*      */         }
/*      */       }
/*      */       
/* 3109 */       return res;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3113 */       Debug.printStackTrace(e);
/*      */     }
/* 3115 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void deleteDataFileContents(TOTorrent torrent, String torrent_save_dir, String torrent_save_file, boolean force_no_recycle)
/*      */     throws TOTorrentException, UnsupportedEncodingException, LocaleUtilEncodingException
/*      */   {
/* 3128 */     LocaleUtilDecoder locale_decoder = LocaleTorrentUtil.getTorrentEncoding(torrent);
/*      */     
/* 3130 */     TOTorrentFile[] files = torrent.getFiles();
/*      */     
/* 3132 */     String root_path = torrent_save_dir + File.separator + torrent_save_file + File.separator;
/*      */     
/* 3134 */     boolean delete_if_not_in_dir = COConfigurationManager.getBooleanParameter("File.delete.include_files_outside_save_dir");
/*      */     
/*      */ 
/*      */ 
/* 3138 */     for (int i = 0; i < files.length; i++)
/*      */     {
/* 3140 */       byte[][] path_comps = files[i].getPathComponents();
/*      */       
/* 3142 */       String path_str = root_path;
/*      */       
/* 3144 */       for (int j = 0; j < path_comps.length; j++)
/*      */       {
/*      */         try
/*      */         {
/* 3148 */           String comp = locale_decoder.decodeString(path_comps[j]);
/*      */           
/* 3150 */           comp = FileUtil.convertOSSpecificChars(comp, j != path_comps.length - 1);
/*      */           
/* 3152 */           path_str = path_str + (j == 0 ? "" : File.separator) + comp;
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 3156 */           Debug.out("file - unsupported encoding!!!!");
/*      */         }
/*      */       }
/*      */       
/* 3160 */       File file = new File(path_str);
/*      */       
/* 3162 */       File linked_file = FMFileManagerFactory.getSingleton().getFileLink(torrent, i, file);
/*      */       
/*      */       boolean delete;
/*      */       boolean delete;
/* 3166 */       if (linked_file == file)
/*      */       {
/* 3168 */         delete = true;
/*      */       } else {
/*      */         try
/*      */         {
/*      */           boolean delete;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 3177 */           if ((delete_if_not_in_dir) || (linked_file.getCanonicalPath().startsWith(new File(root_path).getCanonicalPath())))
/*      */           {
/* 3179 */             file = linked_file;
/*      */             
/* 3181 */             delete = true;
/*      */           }
/*      */           else
/*      */           {
/* 3185 */             delete = false;
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/* 3189 */           Debug.printStackTrace(e);
/*      */           
/* 3191 */           delete = false;
/*      */         }
/*      */       }
/*      */       
/* 3195 */       if ((delete) && (file.exists()) && (!file.isDirectory())) {
/*      */         try
/*      */         {
/* 3198 */           FileUtil.deleteWithRecycle(file, force_no_recycle);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 3202 */           Debug.out(e.toString());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3207 */     TorrentUtils.recursiveEmptyDirDelete(new File(torrent_save_dir, torrent_save_file));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void skippedFileSetChanged(DiskManagerFileInfo file)
/*      */   {
/* 3214 */     this.skipped_file_set_changed = true;
/* 3215 */     if (this.priority_change_marker.incrementAndGet() == 0L) {
/* 3216 */       this.priority_change_marker.incrementAndGet();
/*      */     }
/* 3218 */     this.listeners.dispatch(2, file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void priorityChanged(DiskManagerFileInfo file)
/*      */   {
/* 3225 */     if (this.priority_change_marker.incrementAndGet() == 0L) {
/* 3226 */       this.priority_change_marker.incrementAndGet();
/*      */     }
/* 3228 */     this.listeners.dispatch(2, file);
/*      */   }
/*      */   
/*      */ 
/*      */   private void loadFilePriorities()
/*      */   {
/* 3234 */     DiskManagerUtil.loadFilePriorities(this.download_manager, this.fileset);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void storeFilePriorities()
/*      */   {
/* 3240 */     storeFilePriorities(this.download_manager, this.files);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void storeFilePriorities(DownloadManager download_manager, DiskManagerFileInfo[] files)
/*      */   {
/* 3248 */     DiskManagerUtil.storeFilePriorities(download_manager, files);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void storeFileDownloaded(DownloadManager download_manager, DiskManagerFileInfo[] files, boolean persist)
/*      */   {
/* 3257 */     DownloadManagerState state = download_manager.getDownloadState();
/*      */     
/* 3259 */     Map details = new java.util.HashMap();
/*      */     
/* 3261 */     List downloaded = new java.util.ArrayList();
/*      */     
/* 3263 */     details.put("downloaded", downloaded);
/*      */     
/* 3265 */     for (int i = 0; i < files.length; i++)
/*      */     {
/* 3267 */       downloaded.add(new Long(files[i].getDownloaded()));
/*      */     }
/*      */     
/* 3270 */     state.setMapAttribute("filedownloaded", details);
/*      */     
/* 3272 */     if (persist)
/*      */     {
/* 3274 */       state.save();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void saveState()
/*      */   {
/* 3281 */     saveState(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void saveState(boolean persist)
/*      */   {
/* 3288 */     if (this.files != null)
/*      */     {
/* 3290 */       storeFileDownloaded(this.download_manager, this.files, persist);
/*      */       
/* 3292 */       storeFilePriorities();
/*      */     }
/*      */     
/* 3295 */     checkFreePieceList(false);
/*      */   }
/*      */   
/*      */   public DownloadManager getDownloadManager() {
/* 3299 */     return this.download_manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getInternalName()
/*      */   {
/* 3305 */     return this.download_manager.getInternalName();
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadManagerState getDownloadState()
/*      */   {
/* 3311 */     return this.download_manager.getDownloadState();
/*      */   }
/*      */   
/*      */ 
/*      */   public File getSaveLocation()
/*      */   {
/* 3317 */     return this.download_manager.getSaveLocation();
/*      */   }
/*      */   
/*      */ 
/*      */   public String[] getStorageTypes()
/*      */   {
/* 3323 */     return getStorageTypes(this.download_manager);
/*      */   }
/*      */   
/*      */   public String getStorageType(int fileIndex) {
/* 3327 */     return getStorageType(this.download_manager, fileIndex);
/*      */   }
/*      */   
/*      */ 
/*      */   public static String[] getStorageTypes(DownloadManager download_manager)
/*      */   {
/* 3333 */     DownloadManagerState state = download_manager.getDownloadState();
/* 3334 */     String[] types = state.getListAttribute("storetypes");
/* 3335 */     if ((types == null) || (types.length == 0)) {
/* 3336 */       TOTorrentFile[] files = download_manager.getTorrent().getFiles();
/* 3337 */       types = new String[download_manager.getTorrent().getFiles().length];
/*      */       
/* 3339 */       if (reorder_storage_mode)
/*      */       {
/* 3341 */         int existing = state.getIntAttribute("reordermb");
/*      */         
/* 3343 */         if (existing < 0)
/*      */         {
/* 3345 */           existing = reorder_storage_mode_min_mb;
/*      */           
/* 3347 */           state.setIntAttribute("reordermb", existing);
/*      */         }
/*      */         
/* 3350 */         for (int i = 0; i < types.length; i++)
/*      */         {
/* 3352 */           if (files[i].getLength() / 1048576L >= existing)
/*      */           {
/* 3354 */             types[i] = "R";
/*      */           }
/*      */           else
/*      */           {
/* 3358 */             types[i] = "L";
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 3363 */         for (int i = 0; i < types.length; i++)
/*      */         {
/* 3365 */           types[i] = "L";
/*      */         }
/*      */       }
/*      */       
/* 3369 */       state.setListAttribute("storetypes", types);
/*      */     }
/*      */     
/* 3372 */     return types;
/*      */   }
/*      */   
/*      */ 
/*      */   public static String getStorageType(DownloadManager download_manager, int fileIndex)
/*      */   {
/* 3378 */     DownloadManagerState state = download_manager.getDownloadState();
/* 3379 */     String type = state.getListAttribute("storetypes", fileIndex);
/*      */     
/* 3381 */     if (type != null)
/*      */     {
/* 3383 */       return type;
/*      */     }
/*      */     
/* 3386 */     return getStorageTypes(download_manager)[fileIndex];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setFileLinks(DownloadManager download_manager, LinkFileMap links)
/*      */   {
/*      */     try
/*      */     {
/* 3395 */       com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerFactory.getSingleton().setFileLinks(download_manager.getTorrent(), links);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 3399 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 3407 */     return "TorrentDM: '" + this.download_manager.getDisplayName() + "'";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/* 3415 */     return new Object[] { this.download_manager, this.torrent };
/*      */   }
/*      */   
/*      */ 
/*      */   public DiskManagerRecheckScheduler getRecheckScheduler()
/*      */   {
/* 3421 */     return recheck_scheduler;
/*      */   }
/*      */   
/*      */   public boolean isInteresting(int pieceNumber)
/*      */   {
/* 3426 */     return this.pieces[pieceNumber].isInteresting();
/*      */   }
/*      */   
/*      */   public boolean isDone(int pieceNumber)
/*      */   {
/* 3431 */     return this.pieces[pieceNumber].isDone();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getPriorityChangeMarker()
/*      */   {
/* 3437 */     return this.priority_change_marker.get();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 3444 */     writer.println("Disk Manager");
/*      */     try
/*      */     {
/* 3447 */       writer.indent();
/*      */       
/* 3449 */       writer.println("percent_done=" + this.percentDone + ",allocated=" + this.allocated + ",remaining=" + this.remaining);
/* 3450 */       writer.println("skipped_file_set_size=" + this.skipped_file_set_size + ",skipped_but_downloaded=" + this.skipped_but_downloaded);
/* 3451 */       writer.println("already_moved=" + this.alreadyMoved);
/*      */     }
/*      */     finally {
/* 3454 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */