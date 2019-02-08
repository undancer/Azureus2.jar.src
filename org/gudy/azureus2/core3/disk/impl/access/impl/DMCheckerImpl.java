/*     */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerHelper;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerRecheckInstance;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerRecheckScheduler;
/*     */ import org.gudy.azureus2.core3.disk.impl.access.DMChecker;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.ConcurrentHasher;
/*     */ import org.gudy.azureus2.core3.util.ConcurrentHasherRequest;
/*     */ import org.gudy.azureus2.core3.util.ConcurrentHasherRequestListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class DMCheckerImpl
/*     */   implements DMChecker
/*     */ {
/*  47 */   protected static final LogIDs LOGID = LogIDs.DISK;
/*     */   
/*     */   private static boolean flush_pieces;
/*     */   
/*     */   private static boolean checking_read_priority;
/*  52 */   static final AEMonitor class_mon = new AEMonitor("DMChecker:class");
/*  53 */   static final List async_check_queue = new ArrayList();
/*  54 */   static final AESemaphore async_check_queue_sem = new AESemaphore("DMChecker::asyncCheck");
/*     */   
/*  56 */   private static final boolean fully_async = COConfigurationManager.getBooleanParameter("diskmanager.perf.checking.fully.async");
/*     */   protected final DiskManagerHelper disk_manager;
/*     */   
/*  59 */   static { if (fully_async)
/*     */     {
/*  61 */       new AEThread2("DMCheckerImpl:asyncCheckScheduler", true)
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */           for (;;)
/*     */           {
/*  68 */             DMCheckerImpl.async_check_queue_sem.reserve();
/*     */             
/*     */             Object[] entry;
/*     */             try
/*     */             {
/*  73 */               DMCheckerImpl.class_mon.enter();
/*     */               
/*  75 */               entry = (Object[])DMCheckerImpl.async_check_queue.remove(0);
/*     */               
/*  77 */               int queue_size = DMCheckerImpl.async_check_queue.size();
/*     */               
/*  79 */               if ((queue_size % 100 == 0) && (queue_size > 0))
/*     */               {
/*  81 */                 System.out.println("async check queue size=" + DMCheckerImpl.async_check_queue.size());
/*     */               }
/*     */             }
/*     */             finally
/*     */             {
/*  86 */               DMCheckerImpl.class_mon.exit();
/*     */             }
/*     */             
/*  89 */             ((DMCheckerImpl)entry[0]).enqueueCheckRequest((DiskManagerCheckRequest)entry[1], (DiskManagerCheckRequestListener)entry[2], DMCheckerImpl.flush_pieces);
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */     ParameterListener param_listener = new ParameterListener()
/*     */     {
/*     */ 
/*     */       public void parameterChanged(String str)
/*     */       {
/* 106 */         DMCheckerImpl.access$002(COConfigurationManager.getBooleanParameter("diskmanager.perf.cache.flushpieces"));
/* 107 */         DMCheckerImpl.access$102(COConfigurationManager.getBooleanParameter("diskmanager.perf.checking.read.priority"));
/*     */       }
/*     */       
/* 110 */     };
/* 111 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "diskmanager.perf.cache.flushpieces", "diskmanager.perf.checking.read.priority" }, param_listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int async_checks;
/*     */   
/*     */ 
/*     */ 
/* 121 */   protected final AESemaphore async_check_sem = new AESemaphore("DMChecker::asyncCheck");
/*     */   
/*     */   protected int async_reads;
/* 124 */   protected final AESemaphore async_read_sem = new AESemaphore("DMChecker::asyncRead");
/*     */   
/*     */   private boolean started;
/*     */   
/*     */   protected volatile boolean stopped;
/*     */   
/*     */   private volatile boolean complete_recheck_in_progress;
/*     */   
/*     */   private volatile int complete_recheck_progress;
/* 133 */   private boolean checking_enabled = true;
/*     */   
/* 135 */   protected final AEMonitor this_mon = new AEMonitor("DMChecker");
/*     */   
/*     */ 
/*     */ 
/*     */   public DMCheckerImpl(DiskManagerHelper _disk_manager)
/*     */   {
/* 141 */     this.disk_manager = _disk_manager;
/*     */   }
/*     */   
/*     */   public void start()
/*     */   {
/*     */     try
/*     */     {
/* 148 */       this.this_mon.enter();
/*     */       
/* 150 */       if (this.started)
/*     */       {
/* 152 */         throw new RuntimeException("DMChecker: start while started");
/*     */       }
/*     */       
/* 155 */       if (this.stopped)
/*     */       {
/* 157 */         throw new RuntimeException("DMChecker: start after stopped");
/*     */       }
/*     */       
/* 160 */       this.started = true;
/*     */     }
/*     */     finally
/*     */     {
/* 164 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */     int read_wait;
/*     */     int check_wait;
/*     */     try
/*     */     {
/* 175 */       this.this_mon.enter();
/*     */       
/* 177 */       if ((this.stopped) || (!this.started)) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 185 */       this.stopped = true;
/*     */       
/* 187 */       read_wait = this.async_reads;
/* 188 */       check_wait = this.async_checks;
/*     */     }
/*     */     finally
/*     */     {
/* 192 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 195 */     long log_time = SystemTime.getCurrentTime();
/*     */     
/*     */ 
/*     */ 
/* 199 */     for (int i = 0; i < read_wait; i++)
/*     */     {
/* 201 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 203 */       if (now < log_time)
/*     */       {
/* 205 */         log_time = now;
/*     */ 
/*     */ 
/*     */       }
/* 209 */       else if (now - log_time > 1000L)
/*     */       {
/* 211 */         log_time = now;
/*     */         
/* 213 */         if (Logger.isEnabled())
/*     */         {
/* 215 */           Logger.log(new LogEvent(this.disk_manager, LOGID, "Waiting for check-reads to complete - " + (read_wait - i) + " remaining"));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 220 */       this.async_read_sem.reserve();
/*     */     }
/*     */     
/* 223 */     log_time = SystemTime.getCurrentTime();
/*     */     
/*     */ 
/*     */ 
/* 227 */     for (int i = 0; i < check_wait; i++)
/*     */     {
/* 229 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 231 */       if (now < log_time)
/*     */       {
/* 233 */         log_time = now;
/*     */ 
/*     */ 
/*     */       }
/* 237 */       else if (now - log_time > 1000L)
/*     */       {
/* 239 */         log_time = now;
/*     */         
/* 241 */         if (Logger.isEnabled())
/*     */         {
/* 243 */           Logger.log(new LogEvent(this.disk_manager, LOGID, "Waiting for checks to complete - " + (read_wait - i) + " remaining"));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 248 */       this.async_check_sem.reserve();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCompleteRecheckStatus()
/*     */   {
/* 255 */     if (this.complete_recheck_in_progress)
/*     */     {
/* 257 */       return this.complete_recheck_progress;
/*     */     }
/*     */     
/*     */ 
/* 261 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setCheckingEnabled(boolean enabled)
/*     */   {
/* 269 */     this.checking_enabled = enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerCheckRequest createCheckRequest(int pieceNumber, Object user_data)
/*     */   {
/* 277 */     return new DiskManagerCheckRequestImpl(pieceNumber, user_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueCompleteRecheckRequest(final DiskManagerCheckRequest request, final DiskManagerCheckRequestListener listener)
/*     */   {
/* 285 */     if (!this.checking_enabled)
/*     */     {
/* 287 */       listener.checkCompleted(request, true);
/*     */       
/* 289 */       return;
/*     */     }
/*     */     
/* 292 */     this.complete_recheck_progress = 0;
/* 293 */     this.complete_recheck_in_progress = true;
/*     */     
/* 295 */     new AEThread2("DMChecker::completeRecheck", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 300 */         DiskManagerRecheckInstance recheck_inst = DMCheckerImpl.this.disk_manager.getRecheckScheduler().register(DMCheckerImpl.this.disk_manager, true);
/*     */         try
/*     */         {
/* 303 */           final AESemaphore sem = new AESemaphore("DMChecker::completeRecheck");
/*     */           
/* 305 */           int checks_submitted = 0;
/*     */           
/* 307 */           final AESemaphore run_sem = new AESemaphore("DMChecker::completeRecheck:runsem", 2);
/*     */           
/* 309 */           int nbPieces = DMCheckerImpl.this.disk_manager.getNbPieces();
/*     */           
/* 311 */           for (int i = 0; i < nbPieces; i++)
/*     */           {
/* 313 */             DMCheckerImpl.this.complete_recheck_progress = (1000 * i / nbPieces);
/*     */             
/* 315 */             DiskManagerPiece dm_piece = DMCheckerImpl.this.disk_manager.getPiece(i);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 320 */             if ((dm_piece.isDone()) || (!dm_piece.isSkipped()))
/*     */             {
/* 322 */               run_sem.reserve();
/*     */               
/* 324 */               while (!DMCheckerImpl.this.stopped)
/*     */               {
/* 326 */                 if (recheck_inst.getPermission()) {
/*     */                   break;
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/* 332 */               if (DMCheckerImpl.this.stopped) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 337 */               DiskManagerCheckRequest this_request = DMCheckerImpl.this.createCheckRequest(i, request.getUserData());
/*     */               
/* 339 */               DMCheckerImpl.this.enqueueCheckRequest(this_request, new DiskManagerCheckRequestListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void checkCompleted(DiskManagerCheckRequest request, boolean passed)
/*     */                 {
/*     */ 
/*     */                   try
/*     */                   {
/*     */ 
/* 349 */                     DMCheckerImpl.3.this.val$listener.checkCompleted(request, passed);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 353 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                   finally
/*     */                   {
/* 357 */                     complete();
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */                 public void checkCancelled(DiskManagerCheckRequest request)
/*     */                 {
/*     */                   try
/*     */                   {
/* 366 */                     DMCheckerImpl.3.this.val$listener.checkCancelled(request);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 370 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                   finally
/*     */                   {
/* 374 */                     complete();
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */                 public void checkFailed(DiskManagerCheckRequest request, Throwable cause)
/*     */                 {
/*     */                   try
/*     */                   {
/* 384 */                     DMCheckerImpl.3.this.val$listener.checkFailed(request, cause);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 388 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                   finally
/*     */                   {
/* 392 */                     complete();
/*     */                   }
/*     */                 }
/*     */                 
/*     */                 protected void complete()
/*     */                 {
/* 398 */                   run_sem.release();
/*     */                   
/* 400 */                   sem.release(); } }, false);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 405 */               checks_submitted++;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 411 */           for (int i = 0; i < checks_submitted; i++)
/*     */           {
/* 413 */             sem.reserve();
/*     */           }
/*     */         }
/*     */         finally {
/* 417 */           DMCheckerImpl.this.complete_recheck_in_progress = false;
/*     */           
/* 419 */           recheck_inst.unregister();
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueCheckRequest(DiskManagerCheckRequest request, DiskManagerCheckRequestListener listener)
/*     */   {
/* 430 */     if (fully_async)
/*     */     {
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 437 */         class_mon.enter();
/*     */         
/* 439 */         async_check_queue.add(new Object[] { this, request, listener });
/*     */         
/* 441 */         if (async_check_queue.size() % 100 == 0)
/*     */         {
/* 443 */           System.out.println("async check queue size=" + async_check_queue.size());
/*     */         }
/*     */       }
/*     */       finally {
/* 447 */         class_mon.exit();
/*     */       }
/*     */       
/* 450 */       async_check_queue_sem.release();
/*     */     }
/*     */     else
/*     */     {
/* 454 */       enqueueCheckRequest(request, listener, flush_pieces);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasOutstandingCheckRequestForPiece(int piece_number)
/*     */   {
/* 462 */     if (fully_async) {
/*     */       try
/*     */       {
/* 465 */         class_mon.enter();
/*     */         
/* 467 */         for (int i = 0; i < async_check_queue.size(); i++)
/*     */         {
/* 469 */           Object[] entry = (Object[])async_check_queue.get(i);
/*     */           
/* 471 */           if (entry[0] == this)
/*     */           {
/* 473 */             DiskManagerCheckRequest request = (DiskManagerCheckRequest)entry[1];
/*     */             
/* 475 */             if (request.getPieceNumber() == piece_number)
/*     */             {
/* 477 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 483 */         class_mon.exit();
/*     */       }
/*     */     }
/*     */     
/* 487 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void enqueueCheckRequest(DiskManagerCheckRequest request, final DiskManagerCheckRequestListener listener, boolean read_flush)
/*     */   {
/* 499 */     request.requestStarts();
/*     */     
/* 501 */     enqueueCheckRequestSupport(request, new DiskManagerCheckRequestListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void checkCompleted(DiskManagerCheckRequest request, boolean passed)
/*     */       {
/*     */ 
/*     */ 
/* 510 */         request.requestEnds(true);
/*     */         try
/*     */         {
/* 513 */           int piece_number = request.getPieceNumber();
/*     */           
/* 515 */           DiskManagerPiece piece = DMCheckerImpl.this.disk_manager.getPiece(request.getPieceNumber());
/*     */           
/* 517 */           piece.setDone(passed);
/*     */           
/* 519 */           if (passed)
/*     */           {
/* 521 */             DMPieceList piece_list = DMCheckerImpl.this.disk_manager.getPieceList(piece_number);
/*     */             
/* 523 */             for (int i = 0; i < piece_list.size(); i++)
/*     */             {
/* 525 */               DMPieceMapEntry piece_entry = piece_list.get(i);
/*     */               
/* 527 */               piece_entry.getFile().dataChecked(piece_entry.getOffset(), piece_entry.getLength());
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/* 532 */           listener.checkCompleted(request, passed);
/*     */           
/* 534 */           if (Logger.isEnabled()) {
/* 535 */             if (passed)
/*     */             {
/* 537 */               Logger.log(new LogEvent(DMCheckerImpl.this.disk_manager, DMCheckerImpl.LOGID, 0, "Piece " + request.getPieceNumber() + " passed hash check."));
/*     */             }
/*     */             else {
/* 540 */               Logger.log(new LogEvent(DMCheckerImpl.this.disk_manager, DMCheckerImpl.LOGID, 1, "Piece " + request.getPieceNumber() + " failed hash check."));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void checkCancelled(DiskManagerCheckRequest request)
/*     */       {
/* 552 */         request.requestEnds(false);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 559 */         listener.checkCancelled(request);
/*     */         
/* 561 */         if (Logger.isEnabled()) {
/* 562 */           Logger.log(new LogEvent(DMCheckerImpl.this.disk_manager, DMCheckerImpl.LOGID, 1, "Piece " + request.getPieceNumber() + " hash check cancelled."));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void checkFailed(DiskManagerCheckRequest request, Throwable cause)
/*     */       {
/* 572 */         request.requestEnds(false);
/*     */         try
/*     */         {
/* 575 */           DMCheckerImpl.this.disk_manager.getPiece(request.getPieceNumber()).setDone(false);
/*     */         }
/*     */         finally
/*     */         {
/* 579 */           listener.checkFailed(request, cause);
/*     */           
/* 581 */           if (Logger.isEnabled())
/* 582 */             Logger.log(new LogEvent(DMCheckerImpl.this.disk_manager, DMCheckerImpl.LOGID, 1, "Piece " + request.getPieceNumber() + " failed hash check - " + Debug.getNestedExceptionMessage(cause))); } } }, read_flush);
/*     */   }
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
/*     */   protected void enqueueCheckRequestSupport(final DiskManagerCheckRequest request, final DiskManagerCheckRequestListener listener, boolean read_flush)
/*     */   {
/* 597 */     if (!this.checking_enabled)
/*     */     {
/* 599 */       listener.checkCompleted(request, true);
/*     */       
/* 601 */       return;
/*     */     }
/*     */     
/* 604 */     final int pieceNumber = request.getPieceNumber();
/*     */     
/*     */     try
/*     */     {
/* 608 */       final byte[] required_hash = this.disk_manager.getPieceHash(pieceNumber);
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
/* 619 */       final DMPieceList pieceList = this.disk_manager.getPieceList(pieceNumber);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 627 */         boolean all_compact = pieceList.size() > 0;
/*     */         
/* 629 */         for (int i = 0; i < pieceList.size(); i++)
/*     */         {
/* 631 */           DMPieceMapEntry piece_entry = pieceList.get(i);
/*     */           
/* 633 */           DiskManagerFileInfoImpl file_info = piece_entry.getFile();
/*     */           
/* 635 */           CacheFile cache_file = file_info.getCacheFile();
/*     */           
/* 637 */           if (cache_file.compareLength(piece_entry.getOffset()) < 0L)
/*     */           {
/* 639 */             listener.checkCompleted(request, false);
/*     */             
/* 641 */             return;
/*     */           }
/*     */           
/* 644 */           if (all_compact)
/*     */           {
/* 646 */             int st = cache_file.getStorageType();
/*     */             
/* 648 */             if (((st != 2) && (st != 4)) || (file_info.getNbPieces() <= 2))
/*     */             {
/* 650 */               all_compact = false;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 655 */         if (all_compact)
/*     */         {
/*     */ 
/*     */ 
/* 659 */           listener.checkCompleted(request, false);
/*     */           
/* 661 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/* 669 */         listener.checkCancelled(request);
/*     */         
/* 671 */         return;
/*     */       }
/*     */       
/* 674 */       int this_piece_length = this.disk_manager.getPieceLength(pieceNumber);
/*     */       
/* 676 */       DiskManagerReadRequest read_request = this.disk_manager.createReadRequest(pieceNumber, 0, this_piece_length);
/*     */       try
/*     */       {
/* 679 */         this.this_mon.enter();
/*     */         
/* 681 */         if (this.stopped)
/*     */         {
/* 683 */           listener.checkCancelled(request); return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 688 */         this.async_reads += 1;
/*     */       }
/*     */       finally
/*     */       {
/* 692 */         this.this_mon.exit();
/*     */       }
/*     */       
/* 695 */       read_request.setFlush(read_flush);
/*     */       
/* 697 */       read_request.setUseCache(!request.isAdHoc());
/*     */       
/* 699 */       this.disk_manager.enqueueReadRequest(read_request, new DiskManagerReadRequestListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void readCompleted(DiskManagerReadRequest read_request, DirectByteBuffer buffer)
/*     */         {
/*     */ 
/*     */ 
/* 708 */           complete();
/*     */           try
/*     */           {
/* 711 */             DMCheckerImpl.this.this_mon.enter();
/*     */             
/* 713 */             if (DMCheckerImpl.this.stopped)
/*     */             {
/* 715 */               buffer.returnToPool();
/*     */               
/* 717 */               listener.checkCancelled(request); return;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 722 */             DMCheckerImpl.this.async_checks += 1;
/*     */           }
/*     */           finally
/*     */           {
/* 726 */             DMCheckerImpl.this.this_mon.exit();
/*     */           }
/*     */           
/* 729 */           if (buffer.getFlag((byte)1)) {
/*     */             try
/*     */             {
/* 732 */               buffer.returnToPool();
/*     */               
/* 734 */               listener.checkCompleted(request, false);
/*     */             }
/*     */             finally
/*     */             {
/*     */               try {
/* 739 */                 DMCheckerImpl.this.this_mon.enter();
/*     */                 
/* 741 */                 DMCheckerImpl.this.async_checks -= 1;
/*     */                 
/* 743 */                 if (DMCheckerImpl.this.stopped)
/*     */                 {
/* 745 */                   DMCheckerImpl.this.async_check_sem.release();
/*     */                 }
/*     */               }
/*     */               finally {
/* 749 */                 DMCheckerImpl.this.this_mon.exit();
/*     */               }
/*     */             }
/*     */           } else {
/*     */             try {
/* 754 */               final DirectByteBuffer f_buffer = buffer;
/*     */               
/* 756 */               ConcurrentHasher.getSingleton().addRequest(buffer.getBuffer((byte)8), new ConcurrentHasherRequestListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void complete(ConcurrentHasherRequest hash_request)
/*     */                 {
/*     */ 
/*     */ 
/* 764 */                   int async_result = 3;
/*     */                   
/*     */                   try
/*     */                   {
/* 768 */                     byte[] actual_hash = hash_request.getResult();
/*     */                     
/* 770 */                     if (actual_hash != null)
/*     */                     {
/* 772 */                       DMCheckerImpl.5.this.val$request.setHash(actual_hash);
/*     */                       
/* 774 */                       async_result = 1;
/*     */                       
/* 776 */                       for (int i = 0; i < actual_hash.length; i++)
/*     */                       {
/* 778 */                         if (actual_hash[i] != DMCheckerImpl.5.this.val$required_hash[i])
/*     */                         {
/* 780 */                           async_result = 2;
/*     */                           
/* 782 */                           break;
/*     */                         } }
/*     */                     }
/*     */                   } finally { try { int i;
/*     */                       DMPieceMapEntry piece_entry;
/*     */                       DiskManagerFileInfoImpl file_info;
/*     */                       CacheFile cache_file;
/* 789 */                       if (async_result == 1) {
/*     */                         try
/*     */                         {
/* 792 */                           for (int i = 0; i < DMCheckerImpl.5.this.val$pieceList.size(); i++)
/*     */                           {
/* 794 */                             DMPieceMapEntry piece_entry = DMCheckerImpl.5.this.val$pieceList.get(i);
/*     */                             
/* 796 */                             DiskManagerFileInfoImpl file_info = piece_entry.getFile();
/*     */                             
/*     */ 
/*     */ 
/* 800 */                             if ((file_info.getLength() > 0L) || (!file_info.isSkipped()))
/*     */                             {
/* 802 */                               CacheFile cache_file = file_info.getCacheFile();
/*     */                               
/* 804 */                               cache_file.setPieceComplete(DMCheckerImpl.5.this.val$pieceNumber, f_buffer);
/*     */                             }
/*     */                           }
/*     */                         }
/*     */                         catch (Throwable e) {
/* 809 */                           f_buffer.returnToPool();
/*     */                           
/* 811 */                           Debug.out(e);
/*     */                           
/* 813 */                           DMCheckerImpl.5.this.val$listener.checkFailed(DMCheckerImpl.5.this.val$request, e); return;
/*     */                         }
/*     */                       }
/*     */                       
/*     */ 
/*     */ 
/* 819 */                       f_buffer.returnToPool();
/*     */                       
/* 821 */                       if (async_result == 1)
/*     */                       {
/* 823 */                         DMCheckerImpl.5.this.val$listener.checkCompleted(DMCheckerImpl.5.this.val$request, true);
/*     */                       }
/* 825 */                       else if (async_result == 2)
/*     */                       {
/* 827 */                         DMCheckerImpl.5.this.val$listener.checkCompleted(DMCheckerImpl.5.this.val$request, false);
/*     */                       }
/*     */                       else
/*     */                       {
/* 831 */                         DMCheckerImpl.5.this.val$listener.checkCancelled(DMCheckerImpl.5.this.val$request);
/*     */                       }
/*     */                     }
/*     */                     finally
/*     */                     {
/*     */                       try {
/* 837 */                         DMCheckerImpl.this.this_mon.enter();
/*     */                         
/* 839 */                         DMCheckerImpl.this.async_checks -= 1;
/*     */                         
/* 841 */                         if (DMCheckerImpl.this.stopped)
/*     */                         {
/* 843 */                           DMCheckerImpl.this.async_check_sem.release();
/*     */                         }
/*     */                       }
/*     */                       finally {
/* 847 */                         DMCheckerImpl.this.this_mon.exit(); } } } } }, request.isLowPriority());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 859 */               Debug.printStackTrace(e);
/*     */               
/* 861 */               buffer.returnToPool();
/*     */               
/* 863 */               listener.checkFailed(request, e);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void readFailed(DiskManagerReadRequest read_request, Throwable cause)
/*     */         {
/* 873 */           complete();
/*     */           
/* 875 */           listener.checkFailed(request, cause);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getPriority()
/*     */         {
/* 881 */           return DMCheckerImpl.checking_read_priority ? 0 : -1;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void requestExecuted(long bytes) {}
/*     */         
/*     */ 
/*     */         protected void complete()
/*     */         {
/*     */           try
/*     */           {
/* 893 */             DMCheckerImpl.this.this_mon.enter();
/*     */             
/* 895 */             DMCheckerImpl.this.async_reads -= 1;
/*     */             
/* 897 */             if (DMCheckerImpl.this.stopped)
/*     */             {
/* 899 */               DMCheckerImpl.this.async_read_sem.release();
/*     */             }
/*     */           }
/*     */           finally {
/* 903 */             DMCheckerImpl.this.this_mon.exit();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 910 */       this.disk_manager.setFailed("Piece check error - " + Debug.getNestedExceptionMessage(e));
/*     */       
/* 912 */       Debug.printStackTrace(e);
/*     */       
/* 914 */       listener.checkFailed(request, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DMCheckerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */