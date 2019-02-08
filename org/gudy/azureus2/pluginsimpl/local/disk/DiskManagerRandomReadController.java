/*     */ package org.gudy.azureus2.pluginsimpl.local.disk;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPiece;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerEvent;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerRandomReadRequest;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiskManagerRandomReadController
/*     */ {
/*  59 */   private static Map<DownloadImpl, DiskManagerRandomReadController> controller_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private DownloadImpl download;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DiskManagerRandomReadRequest createRequest(DownloadImpl download, DiskManagerFileInfoImpl file, long file_offset, long length, boolean reverse_order, DiskManagerListener listener)
/*     */     throws DownloadException
/*     */   {
/*  72 */     if ((file_offset < 0L) || (file_offset >= file.getLength()))
/*     */     {
/*  74 */       throw new DownloadException("invalid file offset " + file_offset + ", file size=" + file.getLength());
/*     */     }
/*     */     
/*  77 */     if ((length <= 0L) || (file_offset + length > file.getLength()))
/*     */     {
/*  79 */       throw new DownloadException("invalid read length " + length + ", offset=" + file_offset + ", file size=" + file.getLength());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  84 */     synchronized (controller_map)
/*     */     {
/*  86 */       DiskManagerRandomReadController controller = (DiskManagerRandomReadController)controller_map.get(download);
/*     */       
/*  88 */       if (controller == null)
/*     */       {
/*  90 */         controller = new DiskManagerRandomReadController(download);
/*     */         
/*  92 */         controller_map.put(download, controller);
/*     */       }
/*     */       
/*  95 */       return controller.addRequest(file, file_offset, length, reverse_order, listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 101 */   private List<DiskManagerRandomReadRequestImpl> requests = new ArrayList();
/*     */   
/* 103 */   private AsyncDispatcher dispatcher = new AsyncDispatcher("dm_rand_reads");
/*     */   
/*     */   private boolean set_force_start;
/*     */   
/*     */   private TimerEventPeriodic timer_event;
/*     */   
/*     */   private volatile boolean busy;
/*     */   
/*     */   private volatile long last_busy_time;
/*     */   
/*     */ 
/*     */   private DiskManagerRandomReadController(DownloadImpl _download)
/*     */   {
/* 116 */     this.download = _download;
/*     */     
/* 118 */     this.timer_event = SimpleTimer.addPeriodicEvent("dmrr:timer", 5000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 128 */         if ((DiskManagerRandomReadController.this.busy) || (SystemTime.getMonotonousTime() - DiskManagerRandomReadController.this.last_busy_time < 5000L))
/*     */         {
/* 130 */           return;
/*     */         }
/*     */         
/* 133 */         synchronized (DiskManagerRandomReadController.controller_map)
/*     */         {
/* 135 */           synchronized (DiskManagerRandomReadController.this.requests)
/*     */           {
/* 137 */             if (DiskManagerRandomReadController.this.requests.size() > 0)
/*     */             {
/* 139 */               return;
/*     */             }
/*     */           }
/*     */           
/* 143 */           DiskManagerRandomReadController.controller_map.remove(DiskManagerRandomReadController.this.download);
/*     */           
/* 145 */           if (DiskManagerRandomReadController.this.set_force_start)
/*     */           {
/* 147 */             DiskManagerRandomReadController.this.download.setForceStart(false);
/*     */           }
/*     */         }
/*     */         
/* 151 */         DiskManagerRandomReadController.this.timer_event.cancel();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DiskManagerRandomReadRequest addRequest(DiskManagerFileInfoImpl file, long file_offset, long length, boolean reverse_order, DiskManagerListener listener)
/*     */   {
/* 165 */     DiskManagerRandomReadRequestImpl request = new DiskManagerRandomReadRequestImpl(file, file_offset, length, reverse_order, listener, null);
/*     */     
/* 167 */     long file_length = file.getLength();
/*     */     
/* 169 */     if (file_offset >= file_length)
/*     */     {
/* 171 */       Debug.out("Invalid request offset: " + file_offset + ", file length=" + file_length);
/*     */       
/* 173 */       return null;
/*     */     }
/*     */     
/* 176 */     if (file_offset + length > file_length)
/*     */     {
/* 178 */       Debug.out("Invalid request length: " + file_offset + "/" + length + ", file length=" + file_length);
/*     */       
/* 180 */       return null;
/*     */     }
/*     */     
/* 183 */     synchronized (this.requests)
/*     */     {
/* 185 */       this.requests.add(request);
/*     */     }
/*     */     
/* 188 */     this.dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */         try
/*     */         {
/* 195 */           DiskManagerRandomReadController.this.busy = true;
/*     */           
/* 197 */           DiskManagerRandomReadController.this.executeRequest();
/*     */         }
/*     */         finally
/*     */         {
/* 201 */           DiskManagerRandomReadController.this.busy = false;
/* 202 */           DiskManagerRandomReadController.this.last_busy_time = SystemTime.getMonotonousTime();
/*     */         }
/*     */         
/*     */       }
/* 206 */     });
/* 207 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */   private void executeRequest()
/*     */   {
/*     */     DiskManagerRandomReadRequestImpl request;
/*     */     
/* 215 */     synchronized (this.requests)
/*     */     {
/* 217 */       if (this.requests.isEmpty())
/*     */       {
/* 219 */         return;
/*     */       }
/*     */       
/* 222 */       request = (DiskManagerRandomReadRequestImpl)this.requests.remove(0);
/*     */     }
/*     */     
/* 225 */     if (request.isCancelled())
/*     */     {
/* 227 */       return;
/*     */     }
/*     */     
/* 230 */     DiskManagerFileInfoListener info_listener = null;
/*     */     
/* 232 */     DiskManagerFileInfo core_file = request.getFile().getCore();
/*     */     
/* 234 */     DownloadManager core_download = core_file.getDownloadManager();
/*     */     
/* 236 */     int prev_hint_piece = -1;
/* 237 */     int curr_hint_piece = -1;
/*     */     
/*     */     try
/*     */     {
/* 241 */       if (core_download.getTorrent() == null)
/*     */       {
/* 243 */         throw new DownloadException("Torrent invalid");
/*     */       }
/*     */       
/* 246 */       if (core_download.isDestroyed())
/*     */       {
/* 248 */         Debug.out("Download has been removed");
/*     */         
/* 250 */         throw new DownloadException("Download has been removed");
/*     */       }
/*     */       
/* 253 */       TOTorrentFile tf = core_file.getTorrentFile();
/*     */       
/* 255 */       TOTorrent torrent = tf.getTorrent();
/*     */       
/* 257 */       TOTorrentFile[] tfs = torrent.getFiles();
/*     */       
/* 259 */       long core_file_start_byte = 0L;
/*     */       
/* 261 */       for (int i = 0; i < core_file.getIndex(); i++)
/*     */       {
/* 263 */         core_file_start_byte += tfs[i].getLength();
/*     */       }
/*     */       
/* 266 */       long download_byte_start = core_file_start_byte + request.getOffset();
/* 267 */       long download_byte_end = download_byte_start + request.getLength();
/*     */       
/* 269 */       int piece_size = (int)tf.getTorrent().getPieceLength();
/*     */       
/* 271 */       if (core_file.getDownloaded() != core_file.getLength())
/*     */       {
/* 273 */         if (core_file.isSkipped())
/*     */         {
/* 275 */           core_file.setSkipped(false);
/*     */         }
/*     */         
/* 278 */         boolean force_start = this.download.isForceStart();
/*     */         
/* 280 */         if (!force_start)
/*     */         {
/* 282 */           this.download.setForceStart(true);
/*     */           
/* 284 */           this.set_force_start = true;
/*     */           
/* 286 */           final AESemaphore running_sem = new AESemaphore("rs");
/*     */           
/* 288 */           DownloadListener dl_listener = new DownloadListener()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public void stateChanged(Download download, int old_state, int new_state)
/*     */             {
/*     */ 
/*     */ 
/* 297 */               if ((new_state == 4) || (new_state == 5))
/*     */               {
/* 299 */                 running_sem.release();
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             public void positionChanged(Download download, int oldPosition, int newPosition) {}
/* 311 */           };
/* 312 */           this.download.addListener(dl_listener);
/*     */           try
/*     */           {
/* 315 */             if ((this.download.getState() != 4) && (this.download.getState() != 5))
/*     */             {
/* 317 */               if (!running_sem.reserve(10000L))
/*     */               {
/* 319 */                 throw new DownloadException("timeout waiting for download to start");
/*     */               }
/*     */             }
/*     */           }
/*     */           finally {
/* 324 */             this.download.removeListener(dl_listener);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 329 */       boolean is_reverse = request.isReverse();
/*     */       
/* 331 */       final AESemaphore wait_sem = new AESemaphore("rr:waiter");
/*     */       
/* 333 */       info_listener = new DiskManagerFileInfoListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void dataWritten(long offset, long length)
/*     */         {
/*     */ 
/*     */ 
/* 341 */           wait_sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void dataChecked(long offset, long length) {}
/* 351 */       };
/* 352 */       long start_time = SystemTime.getMonotonousTime();
/* 353 */       boolean has_started = false;
/*     */       
/* 355 */       core_file.addListener(info_listener);
/*     */       
/*     */ 
/*     */ 
/* 359 */       while (download_byte_start < download_byte_end)
/*     */       {
/* 361 */         if (request.isCancelled())
/*     */         {
/* 363 */           throw new Exception("request cancelled");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 368 */         long now = SystemTime.getMonotonousTime();
/*     */         
/* 370 */         int piece_start = (int)(download_byte_start / piece_size);
/* 371 */         int piece_start_offset = (int)(download_byte_start % piece_size);
/*     */         
/* 373 */         int piece_end = (int)((download_byte_end - 1L) / piece_size);
/* 374 */         int piece_end_offset = (int)((download_byte_end - 1L) % piece_size) + 1;
/*     */         
/*     */ 
/*     */ 
/* 378 */         DiskManagerPiece[] pieces = null;
/*     */         
/* 380 */         DiskManager disk_manager = core_download.getDiskManager();
/*     */         
/* 382 */         if (disk_manager != null)
/*     */         {
/* 384 */           pieces = disk_manager.getPieces();
/*     */         }
/*     */         
/*     */         long avail_start;
/*     */         
/*     */         long avail_end;
/* 390 */         if (pieces == null) {
/*     */           long avail_end;
/* 392 */           if (core_file.getDownloaded() == core_file.getLength())
/*     */           {
/* 394 */             long avail_start = download_byte_start;
/* 395 */             avail_end = download_byte_end;
/*     */           }
/*     */           else
/*     */           {
/* 399 */             if ((now - start_time < 10000L) && (!has_started))
/*     */             {
/* 401 */               wait_sem.reserve(250L);
/*     */               
/* 403 */               continue;
/*     */             }
/*     */             
/* 406 */             throw new Exception("download stopped");
/*     */           }
/*     */         }
/*     */         else {
/* 410 */           has_started = true;
/*     */           long avail_end;
/* 412 */           if (is_reverse)
/*     */           {
/* 414 */             long min_done = download_byte_end;
/*     */             
/* 416 */             for (int i = piece_end; i >= piece_start; i--)
/*     */             {
/* 418 */               int p_start = i == piece_start ? piece_start_offset : 0;
/* 419 */               int p_end = i == piece_end ? piece_end_offset : piece_size;
/*     */               
/* 421 */               DiskManagerPiece piece = pieces[i];
/*     */               
/* 423 */               boolean[] done = piece.getWritten();
/*     */               
/* 425 */               if (done == null)
/*     */               {
/* 427 */                 if (!piece.isDone())
/*     */                   break;
/* 429 */                 min_done = i * piece_size;
/*     */ 
/*     */ 
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 439 */                 int block_size = piece.getBlockSize(0);
/*     */                 
/* 441 */                 int first_block = p_start / block_size;
/* 442 */                 int last_block = (p_end - 1) / block_size;
/*     */                 
/* 444 */                 for (int j = last_block; j >= first_block; j--)
/*     */                 {
/* 446 */                   if (done[j] == 0)
/*     */                     break;
/* 448 */                   min_done = i * piece_size + j * block_size;
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 457 */             long avail_start = Math.max(download_byte_start, min_done);
/* 458 */             avail_end = download_byte_end;
/*     */           }
/*     */           else {
/* 461 */             long max_done = download_byte_start;
/*     */             
/* 463 */             for (int i = piece_start; i <= piece_end; i++)
/*     */             {
/* 465 */               int p_start = i == piece_start ? piece_start_offset : 0;
/* 466 */               int p_end = i == piece_end ? piece_end_offset : piece_size;
/*     */               
/* 468 */               DiskManagerPiece piece = pieces[i];
/*     */               
/* 470 */               boolean[] done = piece.getWritten();
/*     */               
/* 472 */               if (done == null)
/*     */               {
/* 474 */                 if (!piece.isDone())
/*     */                   break;
/* 476 */                 max_done = (i + 1) * piece_size;
/*     */ 
/*     */ 
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 486 */                 int block_size = piece.getBlockSize(0);
/*     */                 
/* 488 */                 int first_block = p_start / block_size;
/* 489 */                 int last_block = (p_end - 1) / block_size;
/*     */                 
/* 491 */                 for (int j = first_block; j <= last_block; j++)
/*     */                 {
/* 493 */                   if (done[j] == 0)
/*     */                     break;
/* 495 */                   max_done = i * piece_size + (j + 1) * block_size;
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 504 */             avail_start = download_byte_start;
/* 505 */             avail_end = Math.min(download_byte_end, max_done);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 511 */         int max_chunk = 131072;
/*     */         
/* 513 */         if (avail_end > avail_start)
/*     */         {
/* 515 */           long length = avail_end - avail_start;
/*     */           
/* 517 */           if (length > max_chunk)
/*     */           {
/* 519 */             if (is_reverse)
/*     */             {
/* 521 */               avail_start = avail_end - max_chunk;
/*     */             }
/*     */             else
/*     */             {
/* 525 */               avail_end = avail_start + max_chunk;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 531 */           long read_offset = avail_start - core_file_start_byte;
/* 532 */           int read_length = (int)(avail_end - avail_start);
/*     */           
/* 534 */           DirectByteBuffer buffer = core_file.read(read_offset, read_length);
/*     */           
/* 536 */           request.dataAvailable(buffer, read_offset, read_length);
/*     */           
/* 538 */           if (is_reverse)
/*     */           {
/* 540 */             download_byte_end = avail_start;
/*     */           }
/*     */           else
/*     */           {
/* 544 */             download_byte_start = avail_end;
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 550 */           PEPeerManager pm = core_download.getPeerManager();
/*     */           
/* 552 */           if (pm == null)
/*     */           {
/* 554 */             if ((now - start_time < 10000L) && (!has_started))
/*     */             {
/* 556 */               wait_sem.reserve(250L);
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 561 */               throw new Exception("download stopped");
/*     */             }
/*     */           }
/*     */           else {
/* 565 */             has_started = true;
/*     */             
/*     */ 
/* 568 */             PiecePicker picker = pm.getPiecePicker();
/*     */             
/* 570 */             picker.setReverseBlockOrder(is_reverse);
/*     */             
/*     */             int hint_length;
/*     */             int hint_piece;
/*     */             int hint_offset;
/*     */             int hint_length;
/* 576 */             if (piece_start == piece_end)
/*     */             {
/* 578 */               int hint_piece = piece_start;
/* 579 */               int hint_offset = piece_start_offset;
/* 580 */               hint_length = piece_end_offset - piece_start_offset;
/*     */             }
/*     */             else {
/*     */               int hint_length;
/* 584 */               if (is_reverse)
/*     */               {
/* 586 */                 int hint_piece = piece_end;
/* 587 */                 int hint_offset = 0;
/* 588 */                 hint_length = piece_end_offset;
/*     */               }
/*     */               else
/*     */               {
/* 592 */                 hint_piece = piece_start;
/* 593 */                 hint_offset = piece_start_offset;
/* 594 */                 hint_length = piece_size - piece_start_offset;
/*     */               }
/*     */             }
/*     */             
/* 598 */             if (curr_hint_piece == -1)
/*     */             {
/* 600 */               int[] existing = picker.getGlobalRequestHint();
/*     */               
/* 602 */               if (existing != null)
/*     */               {
/* 604 */                 curr_hint_piece = existing[0];
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 610 */             picker.setGlobalRequestHint(hint_piece, hint_offset, hint_length);
/*     */             
/* 612 */             if (hint_piece != curr_hint_piece)
/*     */             {
/* 614 */               prev_hint_piece = curr_hint_piece;
/*     */               
/* 616 */               curr_hint_piece = hint_piece;
/*     */             }
/*     */             
/* 619 */             if (prev_hint_piece != -1)
/*     */             {
/* 621 */               clearHint(pm, prev_hint_piece);
/*     */             }
/*     */             
/* 624 */             wait_sem.reserve(250L);
/*     */           }
/*     */         } } } catch (Throwable e) { PEPeerManager pm;
/*     */       PiecePicker picker;
/* 628 */       request.failed(e);
/*     */     } finally {
/*     */       PEPeerManager pm;
/*     */       PiecePicker picker;
/* 632 */       PEPeerManager pm = core_download.getPeerManager();
/*     */       
/* 634 */       if (pm != null)
/*     */       {
/* 636 */         PiecePicker picker = pm.getPiecePicker();
/*     */         
/* 638 */         if (picker != null)
/*     */         {
/* 640 */           picker.setReverseBlockOrder(false);
/*     */           
/* 642 */           picker.setGlobalRequestHint(-1, 0, 0);
/*     */           
/* 644 */           if (curr_hint_piece != -1)
/*     */           {
/* 646 */             clearHint(pm, curr_hint_piece);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 651 */       if (info_listener != null)
/*     */       {
/* 653 */         core_file.removeListener(info_listener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void clearHint(PEPeerManager pm, int hint_piece)
/*     */   {
/* 663 */     PEPiece piece = pm.getPiece(hint_piece);
/*     */     
/* 665 */     if ((piece != null) && (piece.getReservedBy() != null))
/*     */     {
/* 667 */       piece.setReservedBy(null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 672 */     List<PEPeer> peers = pm.getPeers();
/*     */     
/* 674 */     for (PEPeer peer : peers)
/*     */     {
/* 676 */       int[] res = peer.getReservedPieceNumbers();
/*     */       
/* 678 */       if (res != null)
/*     */       {
/* 680 */         for (int i : res)
/*     */         {
/* 682 */           if (i == hint_piece)
/*     */           {
/* 684 */             peer.removeReservedPieceNumber(hint_piece);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class DiskManagerRandomReadRequestImpl
/*     */     implements DiskManagerRandomReadRequest
/*     */   {
/*     */     private DiskManagerFileInfoImpl file;
/*     */     
/*     */     private long file_offset;
/*     */     
/*     */     private long length;
/*     */     
/*     */     private boolean reverse_order;
/*     */     
/*     */     private DiskManagerListener listener;
/*     */     
/*     */     private volatile boolean cancelled;
/*     */     
/*     */     private boolean failed;
/*     */     
/*     */ 
/*     */     private DiskManagerRandomReadRequestImpl(DiskManagerFileInfoImpl _file, long _file_offset, long _length, boolean _reverse_order, DiskManagerListener _listener)
/*     */     {
/* 713 */       this.file = _file;
/* 714 */       this.file_offset = _file_offset;
/* 715 */       this.length = _length;
/* 716 */       this.reverse_order = _reverse_order;
/* 717 */       this.listener = _listener;
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerFileInfoImpl getFile()
/*     */     {
/* 723 */       return this.file;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getOffset()
/*     */     {
/* 729 */       return this.file_offset;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getLength()
/*     */     {
/* 735 */       return this.length;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isReverse()
/*     */     {
/* 741 */       return this.reverse_order;
/*     */     }
/*     */     
/*     */ 
/*     */     private boolean isCancelled()
/*     */     {
/* 747 */       return this.cancelled;
/*     */     }
/*     */     
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 753 */       synchronized (DiskManagerRandomReadController.this.requests)
/*     */       {
/* 755 */         DiskManagerRandomReadController.this.requests.remove(this);
/*     */         
/* 757 */         this.cancelled = true;
/*     */       }
/*     */       
/* 760 */       failed(new Exception("request cancelled"));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void dataAvailable(DirectByteBuffer buffer, final long offset, int length)
/*     */     {
/* 769 */       final PooledByteBuffer p_buffer = new PooledByteBufferImpl(buffer);
/*     */       
/* 771 */       this.listener.eventOccurred(new DiskManagerEvent()
/*     */       {
/*     */ 
/*     */         public int getType()
/*     */         {
/*     */ 
/* 777 */           return 1;
/*     */         }
/*     */         
/*     */ 
/*     */         public long getOffset()
/*     */         {
/* 783 */           return offset;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getLength()
/*     */         {
/* 789 */           return p_buffer;
/*     */         }
/*     */         
/*     */ 
/*     */         public PooledByteBuffer getBuffer()
/*     */         {
/* 795 */           return this.val$p_buffer;
/*     */         }
/*     */         
/*     */ 
/*     */         public Throwable getFailure()
/*     */         {
/* 801 */           return null;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void failed(final Throwable e)
/*     */     {
/* 810 */       Debug.out(e);
/*     */       
/* 812 */       synchronized (DiskManagerRandomReadController.this.requests)
/*     */       {
/* 814 */         if (this.failed)
/*     */         {
/* 816 */           return;
/*     */         }
/*     */         
/* 819 */         this.failed = true;
/*     */       }
/*     */       
/* 822 */       this.listener.eventOccurred(new DiskManagerEvent()
/*     */       {
/*     */ 
/*     */         public int getType()
/*     */         {
/*     */ 
/* 828 */           return 2;
/*     */         }
/*     */         
/*     */ 
/*     */         public long getOffset()
/*     */         {
/* 834 */           return -1L;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getLength()
/*     */         {
/* 840 */           return -1;
/*     */         }
/*     */         
/*     */ 
/*     */         public PooledByteBuffer getBuffer()
/*     */         {
/* 846 */           return null;
/*     */         }
/*     */         
/*     */ 
/*     */         public Throwable getFailure()
/*     */         {
/* 852 */           return e;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerRandomReadController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */