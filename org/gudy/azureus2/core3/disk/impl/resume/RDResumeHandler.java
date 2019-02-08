/*      */ package org.gudy.azureus2.core3.disk.impl.resume;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequestListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener;
/*      */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*      */ import org.gudy.azureus2.core3.disk.impl.DiskManagerImpl;
/*      */ import org.gudy.azureus2.core3.disk.impl.DiskManagerRecheckInstance;
/*      */ import org.gudy.azureus2.core3.disk.impl.DiskManagerRecheckScheduler;
/*      */ import org.gudy.azureus2.core3.disk.impl.access.DMChecker;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*      */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class RDResumeHandler
/*      */ {
/*   60 */   private static final LogIDs LOGID = LogIDs.DISK;
/*      */   private static final boolean TEST_RECHECK_FAILURE_HANDLING = false;
/*      */   private static final byte PIECE_NOT_DONE = 0;
/*      */   private static final byte PIECE_DONE = 1;
/*      */   private static final byte PIECE_RECHECK_REQUIRED = 2;
/*      */   private static final byte PIECE_STARTED = 3;
/*      */   private static boolean use_fast_resume;
/*      */   private static boolean use_fast_resume_recheck_all;
/*      */   final DiskManagerImpl disk_manager;
/*      */   final DMChecker checker;
/*      */   private volatile boolean started;
/*      */   private volatile boolean stopped;
/*      */   private volatile boolean stopped_for_close;
/*      */   private volatile boolean check_in_progress;
/*      */   private volatile boolean check_resume_was_valid;
/*      */   private volatile boolean check_is_full_check;
/*      */   private volatile boolean check_interrupted;
/*      */   private volatile int check_position;
/*      */   
/*      */   static {
/*   80 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Use Resume", "On Resume Recheck All" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String str)
/*      */       {
/*      */ 
/*      */ 
/*   89 */         RDResumeHandler.access$002(COConfigurationManager.getBooleanParameter("Use Resume"));
/*   90 */         RDResumeHandler.access$102(COConfigurationManager.getBooleanParameter("On Resume Recheck All"));
/*      */       }
/*      */     });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public RDResumeHandler(DiskManagerImpl _disk_manager, DMChecker _writer_and_checker)
/*      */   {
/*  114 */     this.disk_manager = _disk_manager;
/*  115 */     this.checker = _writer_and_checker;
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */   {
/*  121 */     if (this.started)
/*      */     {
/*  123 */       Debug.out("RDResumeHandler: reuse not supported");
/*      */     }
/*      */     
/*  126 */     this.started = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void stop(boolean closing)
/*      */   {
/*  133 */     this.stopped_for_close |= closing;
/*      */     
/*  135 */     if (this.check_in_progress)
/*      */     {
/*  137 */       this.check_interrupted = true;
/*      */     }
/*      */     
/*  140 */     this.stopped = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void checkAllPieces(boolean newfiles)
/*      */   {
/*  149 */     DiskManagerRecheckInstance recheck_inst = this.disk_manager.getRecheckScheduler().register(this.disk_manager, false);
/*      */     
/*  151 */     int overall_piece_size = this.disk_manager.getPieceLength();
/*      */     
/*  153 */     final AESemaphore run_sem = new AESemaphore("RDResumeHandler::checkAllPieces:runsem", overall_piece_size > 33554432 ? 1 : 2);
/*      */     
/*  155 */     final List<DiskManagerCheckRequest> failed_pieces = new ArrayList();
/*      */     try
/*      */     {
/*  158 */       boolean resume_data_complete = false;
/*      */       try
/*      */       {
/*  161 */         this.check_in_progress = true;
/*      */         
/*  163 */         boolean resumeEnabled = use_fast_resume;
/*      */         
/*      */ 
/*      */ 
/*  167 */         if (newfiles)
/*      */         {
/*  169 */           resumeEnabled = false;
/*      */         }
/*      */         
/*      */ 
/*  173 */         final AESemaphore pending_checks_sem = new AESemaphore("RD:PendingChecks");
/*  174 */         int pending_check_num = 0;
/*      */         
/*  176 */         DiskManagerPiece[] pieces = this.disk_manager.getPieces();
/*      */         
/*      */ 
/*      */ 
/*  180 */         DiskManagerFileInfo[] files = this.disk_manager.getFiles();
/*  181 */         Map file_sizes = new HashMap();
/*      */         
/*  183 */         for (int i = 0; i < files.length; i++) {
/*      */           try {
/*  185 */             Long len = new Long(((DiskManagerFileInfoImpl)files[i]).getCacheFile().getLength());
/*  186 */             file_sizes.put(files[i], len);
/*      */           } catch (CacheFileManagerException e) {
/*  188 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  194 */         if (resumeEnabled)
/*      */         {
/*  196 */           boolean resumeValid = false;
/*      */           
/*  198 */           byte[] resume_pieces = null;
/*      */           
/*  200 */           Map partialPieces = null;
/*      */           
/*  202 */           Map resume_data = getResumeData();
/*      */           
/*  204 */           if (resume_data != null)
/*      */           {
/*      */             try
/*      */             {
/*  208 */               resume_pieces = (byte[])resume_data.get("resume data");
/*      */               
/*  210 */               if (resume_pieces != null)
/*      */               {
/*  212 */                 if (resume_pieces.length != pieces.length)
/*      */                 {
/*  214 */                   Debug.out("Resume data array length mismatch: " + resume_pieces.length + "/" + pieces.length);
/*      */                   
/*  216 */                   resume_pieces = null;
/*      */                 }
/*      */               }
/*      */               
/*  220 */               partialPieces = (Map)resume_data.get("blocks");
/*      */               
/*  222 */               resumeValid = ((Long)resume_data.get("valid")).intValue() == 1;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  227 */               if (isTorrentResumeDataComplete(this.disk_manager.getDownloadManager().getDownloadState(), resume_data))
/*      */               {
/*  229 */                 resume_data_complete = true;
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  236 */                 resume_data.put("valid", new Long(0L));
/*      */                 
/*  238 */                 saveResumeData(resume_data);
/*      */               }
/*      */             }
/*      */             catch (Exception ignore) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  247 */           if (resume_pieces == null)
/*      */           {
/*  249 */             this.check_is_full_check = true;
/*      */             
/*  251 */             resumeValid = false;
/*      */             
/*  253 */             resume_pieces = new byte[pieces.length];
/*      */             
/*  255 */             Arrays.fill(resume_pieces, (byte)2);
/*      */           }
/*      */           
/*  258 */           this.check_resume_was_valid = resumeValid;
/*      */           
/*  260 */           boolean recheck_all = use_fast_resume_recheck_all;
/*      */           
/*  262 */           if (!recheck_all)
/*      */           {
/*      */ 
/*      */ 
/*  266 */             long total_not_done = 0L;
/*      */             
/*  268 */             int piece_size = this.disk_manager.getPieceLength();
/*      */             
/*  270 */             for (int i = 0; i < pieces.length; i++)
/*      */             {
/*  272 */               if (resume_pieces[i] != 1)
/*      */               {
/*  274 */                 total_not_done += piece_size;
/*      */               }
/*      */             }
/*      */             
/*  278 */             if (total_not_done < 67108864L)
/*      */             {
/*  280 */               recheck_all = true;
/*      */             }
/*      */           }
/*      */           
/*  284 */           if (Logger.isEnabled())
/*      */           {
/*  286 */             int total_not_done = 0;
/*  287 */             int total_done = 0;
/*  288 */             int total_started = 0;
/*  289 */             int total_recheck = 0;
/*      */             
/*  291 */             for (int i = 0; i < pieces.length; i++)
/*      */             {
/*  293 */               byte piece_state = resume_pieces[i];
/*      */               
/*  295 */               if (piece_state == 0) {
/*  296 */                 total_not_done++;
/*  297 */               } else if (piece_state == 1) {
/*  298 */                 total_done++;
/*  299 */               } else if (piece_state == 3) {
/*  300 */                 total_started++;
/*      */               } else {
/*  302 */                 total_recheck++;
/*      */               }
/*      */             }
/*      */             
/*  306 */             String str = "valid=" + resumeValid + ",not done=" + total_not_done + ",done=" + total_done + ",started=" + total_started + ",recheck=" + total_recheck + ",rc all=" + recheck_all + ",full=" + this.check_is_full_check;
/*      */             
/*      */ 
/*      */ 
/*  310 */             Logger.log(new LogEvent(this.disk_manager, LOGID, str));
/*      */           }
/*      */           
/*  313 */           for (int i = 0; i < pieces.length; i++)
/*      */           {
/*  315 */             this.check_position = i;
/*      */             
/*  317 */             DiskManagerPiece dm_piece = pieces[i];
/*      */             
/*  319 */             this.disk_manager.setPercentDone((i + 1) * 1000 / this.disk_manager.getNbPieces());
/*      */             
/*  321 */             boolean pieceCannotExist = false;
/*      */             
/*  323 */             byte piece_state = resume_pieces[i];
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  328 */             if ((piece_state == 1) || (!resumeValid) || (recheck_all))
/*      */             {
/*      */ 
/*      */ 
/*  332 */               DMPieceList list = this.disk_manager.getPieceList(i);
/*      */               
/*  334 */               for (int j = 0; j < list.size(); j++)
/*      */               {
/*  336 */                 DMPieceMapEntry entry = list.get(j);
/*      */                 
/*  338 */                 Long file_size = (Long)file_sizes.get(entry.getFile());
/*      */                 
/*  340 */                 if (file_size == null)
/*      */                 {
/*  342 */                   piece_state = 0;
/*  343 */                   pieceCannotExist = true;
/*      */                   
/*  345 */                   if (!Logger.isEnabled()) break;
/*  346 */                   Logger.log(new LogEvent(this.disk_manager, LOGID, 1, "Piece #" + i + ": file is missing, " + "fails re-check.")); break;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  353 */                 long expected_size = entry.getOffset() + entry.getLength();
/*      */                 
/*  355 */                 if (file_size.longValue() < expected_size)
/*      */                 {
/*  357 */                   piece_state = 0;
/*  358 */                   pieceCannotExist = true;
/*      */                   
/*  360 */                   if (!Logger.isEnabled()) break;
/*  361 */                   Logger.log(new LogEvent(this.disk_manager, LOGID, 1, "Piece #" + i + ": file is too small, fails re-check. File size = " + file_size + ", piece needs " + expected_size)); break;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  371 */             if (piece_state == 1)
/*      */             {
/*  373 */               dm_piece.setDone(true);
/*      */             }
/*  375 */             else if ((piece_state != 0) || (recheck_all))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  384 */               if (pieceCannotExist)
/*      */               {
/*  386 */                 dm_piece.setDone(false);
/*  387 */               } else if ((piece_state == 2) || (!resumeValid))
/*      */               {
/*  389 */                 run_sem.reserve();
/*      */                 
/*  391 */                 while (!this.stopped)
/*      */                 {
/*  393 */                   if (recheck_inst.getPermission()) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*  399 */                 if (this.stopped) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 try
/*      */                 {
/*  406 */                   DiskManagerCheckRequest request = this.disk_manager.createCheckRequest(i, null);
/*      */                   
/*  408 */                   request.setLowPriority(true);
/*      */                   
/*  410 */                   this.checker.enqueueCheckRequest(request, new DiskManagerCheckRequestListener()
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void checkCompleted(DiskManagerCheckRequest request, boolean passed)
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  426 */                       if (!passed)
/*      */                       {
/*  428 */                         synchronized (failed_pieces)
/*      */                         {
/*  430 */                           failed_pieces.add(request);
/*      */                         }
/*      */                       }
/*      */                       
/*  434 */                       complete();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                     public void checkCancelled(DiskManagerCheckRequest request)
/*      */                     {
/*  441 */                       complete();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */                     public void checkFailed(DiskManagerCheckRequest request, Throwable cause)
/*      */                     {
/*  449 */                       complete();
/*      */                     }
/*      */                     
/*      */ 
/*      */                     protected void complete()
/*      */                     {
/*  455 */                       run_sem.release();
/*      */                       
/*  457 */                       pending_checks_sem.release();
/*      */                     }
/*      */                     
/*  460 */                   });
/*  461 */                   pending_check_num++;
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  465 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  472 */           while (pending_check_num > 0)
/*      */           {
/*  474 */             pending_checks_sem.reserve();
/*      */             
/*  476 */             pending_check_num--;
/*      */           }
/*      */           
/*  479 */           if (partialPieces != null)
/*      */           {
/*  481 */             Iterator iter = partialPieces.entrySet().iterator();
/*      */             
/*  483 */             while (iter.hasNext())
/*      */             {
/*  485 */               Map.Entry key = (Map.Entry)iter.next();
/*      */               
/*  487 */               int pieceNumber = Integer.parseInt((String)key.getKey());
/*      */               
/*  489 */               DiskManagerPiece dm_piece = pieces[pieceNumber];
/*      */               
/*  491 */               if (!dm_piece.isDone())
/*      */               {
/*  493 */                 List blocks = (List)partialPieces.get(key.getKey());
/*      */                 
/*  495 */                 Iterator iterBlock = blocks.iterator();
/*      */                 
/*  497 */                 while (iterBlock.hasNext())
/*      */                 {
/*  499 */                   dm_piece.setWritten(((Long)iterBlock.next()).intValue());
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  508 */           for (int i = 0; i < pieces.length; i++)
/*      */           {
/*  510 */             this.check_position = i;
/*      */             
/*  512 */             this.disk_manager.setPercentDone((i + 1) * 1000 / this.disk_manager.getNbPieces());
/*      */             
/*  514 */             boolean pieceCannotExist = false;
/*      */             
/*      */ 
/*  517 */             DMPieceList list = this.disk_manager.getPieceList(i);
/*      */             
/*  519 */             for (int j = 0; j < list.size(); j++) {
/*  520 */               DMPieceMapEntry entry = list.get(j);
/*      */               
/*  522 */               Long file_size = (Long)file_sizes.get(entry.getFile());
/*  523 */               if (file_size == null) {
/*  524 */                 pieceCannotExist = true;
/*  525 */                 break;
/*      */               }
/*      */               
/*  528 */               long expected_size = entry.getOffset() + entry.getLength();
/*  529 */               if (file_size.longValue() < expected_size) {
/*  530 */                 pieceCannotExist = true;
/*  531 */                 break;
/*      */               }
/*      */             }
/*      */             
/*  535 */             if (pieceCannotExist)
/*      */             {
/*  537 */               this.disk_manager.getPiece(i).setDone(false);
/*      */             }
/*      */             else
/*      */             {
/*  541 */               run_sem.reserve();
/*      */               
/*  543 */               while (!this.stopped)
/*      */               {
/*  545 */                 if (recheck_inst.getPermission()) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  551 */               if (this.stopped) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */               try
/*      */               {
/*  558 */                 DiskManagerCheckRequest request = this.disk_manager.createCheckRequest(i, null);
/*      */                 
/*  560 */                 request.setLowPriority(true);
/*      */                 
/*  562 */                 this.checker.enqueueCheckRequest(request, new DiskManagerCheckRequestListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void checkCompleted(DiskManagerCheckRequest request, boolean passed)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  578 */                     if (!passed)
/*      */                     {
/*  580 */                       synchronized (failed_pieces)
/*      */                       {
/*  582 */                         failed_pieces.add(request);
/*      */                       }
/*      */                     }
/*      */                     
/*  586 */                     complete();
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */                   public void checkCancelled(DiskManagerCheckRequest request)
/*      */                   {
/*  593 */                     complete();
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void checkFailed(DiskManagerCheckRequest request, Throwable cause)
/*      */                   {
/*  601 */                     complete();
/*      */                   }
/*      */                   
/*      */ 
/*      */                   protected void complete()
/*      */                   {
/*  607 */                     run_sem.release();
/*      */                     
/*  609 */                     pending_checks_sem.release();
/*      */                   }
/*      */                   
/*  612 */                 });
/*  613 */                 pending_check_num++;
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  617 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }
/*  621 */           while (pending_check_num > 0)
/*      */           {
/*  623 */             pending_checks_sem.reserve();
/*      */             
/*  625 */             pending_check_num--;
/*      */           }
/*      */         }
/*      */         
/*  629 */         if (failed_pieces.size() > 0)
/*      */         {
/*  631 */           byte[][] piece_hashes = this.disk_manager.getTorrent().getPieces();
/*      */           
/*  633 */           hash_map = new ByteArrayHashMap();
/*      */           
/*  635 */           for (int i = 0; i < piece_hashes.length; i++)
/*      */           {
/*  637 */             hash_map.put(piece_hashes[i], Integer.valueOf(i));
/*      */           }
/*      */           
/*  640 */           for (DiskManagerCheckRequest request : failed_pieces)
/*      */           {
/*  642 */             while (!this.stopped)
/*      */             {
/*  644 */               if (recheck_inst.getPermission()) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  650 */             if (this.stopped) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/*  655 */             byte[] hash = request.getHash();
/*      */             
/*  657 */             if (hash != null)
/*      */             {
/*  659 */               final Integer target_index = (Integer)hash_map.get(hash);
/*      */               
/*  661 */               int current_index = request.getPieceNumber();
/*      */               
/*  663 */               int piece_size = this.disk_manager.getPieceLength(current_index);
/*      */               
/*  665 */               if ((target_index != null) && (target_index.intValue() != current_index) && (this.disk_manager.getPieceLength(target_index.intValue()) == piece_size) && (!this.disk_manager.isDone(target_index.intValue())))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  670 */                 final AESemaphore sem = new AESemaphore("PieceReorder");
/*      */                 
/*  672 */                 this.disk_manager.enqueueReadRequest(this.disk_manager.createReadRequest(current_index, 0, piece_size), new DiskManagerReadRequestListener()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public void readCompleted(DiskManagerReadRequest request, DirectByteBuffer data)
/*      */                   {
/*      */ 
/*      */                     try
/*      */                     {
/*      */ 
/*  682 */                       RDResumeHandler.this.disk_manager.enqueueWriteRequest(RDResumeHandler.this.disk_manager.createWriteRequest(target_index.intValue(), 0, data, null), new DiskManagerWriteRequestListener()
/*      */                       {
/*      */ 
/*      */                         public void writeCompleted(DiskManagerWriteRequest request)
/*      */                         {
/*      */ 
/*      */                           try
/*      */                           {
/*      */ 
/*  691 */                             DiskManagerCheckRequest check_request = RDResumeHandler.this.disk_manager.createCheckRequest(RDResumeHandler.4.this.val$target_index.intValue(), null);
/*      */                             
/*  693 */                             check_request.setLowPriority(true);
/*      */                             
/*  695 */                             RDResumeHandler.this.checker.enqueueCheckRequest(check_request, new DiskManagerCheckRequestListener()
/*      */                             {
/*      */ 
/*      */ 
/*      */ 
/*      */                               public void checkCompleted(DiskManagerCheckRequest request, boolean passed)
/*      */                               {
/*      */ 
/*      */ 
/*  704 */                                 RDResumeHandler.4.this.val$sem.release();
/*      */                               }
/*      */                               
/*      */ 
/*      */ 
/*      */                               public void checkCancelled(DiskManagerCheckRequest request)
/*      */                               {
/*  711 */                                 RDResumeHandler.4.this.val$sem.release();
/*      */                               }
/*      */                               
/*      */ 
/*      */ 
/*      */ 
/*      */                               public void checkFailed(DiskManagerCheckRequest request, Throwable cause)
/*      */                               {
/*  719 */                                 RDResumeHandler.4.this.val$sem.release();
/*      */                               }
/*      */                             });
/*      */                           }
/*      */                           catch (Throwable e) {
/*  724 */                             RDResumeHandler.4.this.val$sem.release();
/*      */                           }
/*      */                         }
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void writeFailed(DiskManagerWriteRequest request, Throwable cause)
/*      */                         {
/*  733 */                           RDResumeHandler.4.this.val$sem.release();
/*      */                         }
/*      */                       });
/*      */                     }
/*      */                     catch (Throwable e) {
/*  738 */                       sem.release();
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void readFailed(DiskManagerReadRequest request, Throwable cause)
/*      */                   {
/*  747 */                     sem.release();
/*      */                   }
/*      */                   
/*      */ 
/*      */                   public int getPriority()
/*      */                   {
/*  753 */                     return -1;
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                   public void requestExecuted(long bytes) {}
/*  762 */                 });
/*  763 */                 sem.reserve();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       } finally {
/*      */         ByteArrayHashMap<Integer> hash_map;
/*  770 */         this.check_in_progress = false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  775 */       if ((!this.stopped) && (!resume_data_complete)) {
/*      */         try
/*      */         {
/*  778 */           saveResumeData(true);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  782 */           Debug.out("Failed to dump initial resume data to disk");
/*      */           
/*  784 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  791 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  795 */       recheck_inst.unregister();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void saveResumeData(boolean interim_save)
/*      */     throws Exception
/*      */   {
/*  807 */     if ((this.check_in_progress) && (interim_save))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  817 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  827 */     DiskManagerFileInfo[] files = this.disk_manager.getFiles();
/*      */     
/*  829 */     if (!use_fast_resume)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  834 */       for (int i = 0; i < files.length; i++)
/*      */       {
/*  836 */         files[i].flushCache();
/*      */       }
/*      */       
/*  839 */       return;
/*      */     }
/*      */     
/*  842 */     boolean was_complete = isTorrentResumeDataComplete(this.disk_manager.getDownloadManager().getDownloadState());
/*      */     
/*  844 */     DiskManagerPiece[] pieces = this.disk_manager.getPieces();
/*      */     
/*      */ 
/*      */ 
/*  848 */     byte[] resume_pieces = new byte[pieces.length];
/*      */     
/*  850 */     for (int i = 0; i < resume_pieces.length; i++)
/*      */     {
/*  852 */       DiskManagerPiece piece = pieces[i];
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  857 */       if ((this.stopped_for_close) && (this.check_interrupted) && (this.check_is_full_check) && (i >= this.check_position))
/*      */       {
/*  859 */         resume_pieces[i] = 2;
/*      */       }
/*  861 */       else if (piece.isDone())
/*      */       {
/*  863 */         resume_pieces[i] = 1;
/*      */       }
/*  865 */       else if (piece.getNbWritten() > 0)
/*      */       {
/*  867 */         resume_pieces[i] = 3;
/*      */       }
/*      */       else
/*      */       {
/*  871 */         resume_pieces[i] = 0;
/*      */       }
/*      */     }
/*      */     
/*  875 */     Map resume_data = new HashMap();
/*      */     
/*  877 */     resume_data.put("resume data", resume_pieces);
/*      */     
/*  879 */     Map partialPieces = new HashMap();
/*      */     
/*  881 */     for (int i = 0; i < pieces.length; i++)
/*      */     {
/*  883 */       DiskManagerPiece piece = pieces[i];
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  888 */       boolean[] written = piece.getWritten();
/*      */       
/*  890 */       if ((!piece.isDone()) && (piece.getNbWritten() > 0) && (written != null))
/*      */       {
/*  892 */         boolean all_written = true;
/*      */         
/*  894 */         for (int j = 0; j < written.length; j++)
/*      */         {
/*  896 */           if (written[j] == 0)
/*      */           {
/*  898 */             all_written = false;
/*      */             
/*  900 */             break;
/*      */           }
/*      */         }
/*      */         
/*  904 */         if (all_written)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  909 */           resume_pieces[i] = 2;
/*      */         }
/*      */         else
/*      */         {
/*  913 */           List blocks = new ArrayList();
/*      */           
/*  915 */           for (int j = 0; j < written.length; j++)
/*      */           {
/*  917 */             if (written[j] != 0)
/*      */             {
/*  919 */               blocks.add(new Long(j));
/*      */             }
/*      */           }
/*      */           
/*  923 */           partialPieces.put("" + i, blocks);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  928 */     resume_data.put("blocks", partialPieces);
/*      */     
/*      */     long lValid;
/*      */     long lValid;
/*  932 */     if (this.check_interrupted)
/*      */     {
/*      */ 
/*      */ 
/*  936 */       lValid = this.check_resume_was_valid ? 1L : 0L;
/*      */     } else { long lValid;
/*  938 */       if (interim_save)
/*      */       {
/*      */ 
/*      */ 
/*  942 */         lValid = 0L;
/*      */       }
/*      */       else
/*      */       {
/*  946 */         lValid = 1L;
/*      */       }
/*      */     }
/*  949 */     resume_data.put("valid", new Long(lValid));
/*      */     
/*  951 */     for (int i = 0; i < files.length; i++)
/*      */     {
/*  953 */       files[i].flushCache();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  958 */     boolean is_complete = isTorrentResumeDataComplete(this.disk_manager.getDownloadManager().getDownloadState(), resume_data);
/*      */     
/*  960 */     if ((!was_complete) || (!is_complete))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  966 */       saveResumeData(resume_data);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected Map getResumeData()
/*      */   {
/*  973 */     return getResumeData(this.disk_manager.getDownloadManager());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static Map getResumeData(DownloadManager download_manager)
/*      */   {
/*  980 */     return getResumeData(download_manager.getDownloadState());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static Map getResumeData(DownloadManagerState download_manager_state)
/*      */   {
/*  987 */     Map resume_map = download_manager_state.getResumeData();
/*      */     
/*  989 */     if (resume_map != null)
/*      */     {
/*  991 */       Map resume_data = (Map)resume_map.get("data");
/*      */       
/*  993 */       return resume_data;
/*      */     }
/*      */     
/*      */ 
/*  997 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void saveResumeData(Map resume_data)
/*      */   {
/* 1005 */     saveResumeData(this.disk_manager.getDownloadManager().getDownloadState(), resume_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void saveResumeData(DownloadManagerState download_manager_state, Map resume_data)
/*      */   {
/* 1013 */     Map resume_map = new HashMap();
/*      */     
/* 1015 */     resume_map.put("data", resume_data);
/*      */     
/* 1017 */     download_manager_state.setResumeData(resume_map);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setTorrentResumeDataComplete(DownloadManagerState download_manager_state)
/*      */   {
/* 1025 */     TOTorrent torrent = download_manager_state.getTorrent();
/*      */     
/* 1027 */     int piece_count = torrent.getNumberOfPieces();
/*      */     
/* 1029 */     byte[] resume_pieces = new byte[piece_count];
/*      */     
/* 1031 */     Arrays.fill(resume_pieces, (byte)1);
/*      */     
/* 1033 */     Map resume_data = new HashMap();
/*      */     
/* 1035 */     resume_data.put("resume data", resume_pieces);
/*      */     
/* 1037 */     Map partialPieces = new HashMap();
/*      */     
/* 1039 */     resume_data.put("blocks", partialPieces);
/*      */     
/* 1041 */     resume_data.put("valid", new Long(1L));
/*      */     
/* 1043 */     saveResumeData(download_manager_state, resume_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static int clearResumeDataSupport(DownloadManager download_manager, DiskManagerFileInfo file, boolean recheck, boolean onlyClearUnsharedFirstLast)
/*      */   {
/* 1053 */     DownloadManagerState download_manager_state = download_manager.getDownloadState();
/*      */     
/* 1055 */     Map resume_data = getResumeData(download_manager);
/*      */     
/* 1057 */     if (resume_data == null)
/*      */     {
/* 1059 */       return 0;
/*      */     }
/*      */     
/* 1062 */     int pieces_cleared = 0;
/*      */     
/*      */ 
/*      */ 
/* 1066 */     byte[] resume_pieces = (byte[])resume_data.get("resume data");
/*      */     
/* 1068 */     int firstPiece = file.getFirstPieceNumber();
/* 1069 */     int lastPiece = file.getLastPieceNumber();
/*      */     
/* 1071 */     if (onlyClearUnsharedFirstLast) {
/* 1072 */       DiskManagerFileInfo[] files = download_manager.getDiskManagerFileInfo();
/* 1073 */       boolean firstPieceShared = false;
/* 1074 */       boolean lastPieceShared = false;
/*      */       
/* 1076 */       int firstFile = findFirstFileWithPieceN(firstPiece, files);
/*      */       
/* 1078 */       for (int i = firstFile; i < files.length; i++)
/*      */       {
/* 1080 */         DiskManagerFileInfo currentFile = files[i];
/* 1081 */         if (currentFile.getLastPieceNumber() >= firstPiece)
/*      */         {
/* 1083 */           if (currentFile.getIndex() != file.getIndex())
/*      */           {
/* 1085 */             if (currentFile.getFirstPieceNumber() > lastPiece)
/*      */               break;
/* 1087 */             if ((currentFile.getFirstPieceNumber() <= firstPiece) && (firstPiece <= currentFile.getLastPieceNumber()))
/* 1088 */               firstPieceShared |= !currentFile.isSkipped();
/* 1089 */             if ((currentFile.getFirstPieceNumber() <= lastPiece) && (lastPiece <= currentFile.getLastPieceNumber()))
/* 1090 */               lastPieceShared |= !currentFile.isSkipped();
/*      */           } }
/*      */       }
/* 1093 */       if (firstPieceShared) {
/* 1094 */         firstPiece++;
/*      */       }
/* 1096 */       if (lastPieceShared) {
/* 1097 */         lastPiece--;
/*      */       }
/*      */     }
/* 1100 */     if (resume_pieces != null)
/*      */     {
/* 1102 */       for (int i = firstPiece; i <= lastPiece; i++)
/*      */       {
/* 1104 */         if (i >= resume_pieces.length) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1109 */         if (resume_pieces[i] == 1)
/*      */         {
/* 1111 */           pieces_cleared++;
/*      */         }
/*      */         
/* 1114 */         resume_pieces[i] = (recheck ? 2 : 0);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1119 */     Map partial_pieces = (Map)resume_data.get("blocks");
/*      */     
/* 1121 */     if (partial_pieces != null)
/*      */     {
/* 1123 */       Iterator iter = partial_pieces.keySet().iterator();
/*      */       
/* 1125 */       while (iter.hasNext())
/*      */       {
/* 1127 */         int piece_number = Integer.parseInt((String)iter.next());
/*      */         
/* 1129 */         if ((piece_number >= firstPiece) && (piece_number <= lastPiece))
/*      */         {
/* 1131 */           iter.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1140 */     resume_data.put("valid", new Long(1L));
/*      */     
/* 1142 */     saveResumeData(download_manager_state, resume_data);
/*      */     
/* 1144 */     return pieces_cleared;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static int findFirstFileWithPieceN(int firstPiece, DiskManagerFileInfo[] files)
/*      */   {
/* 1154 */     int start = 0;
/* 1155 */     int end = files.length - 1;
/* 1156 */     int pivot = 0;
/*      */     
/* 1158 */     while (start <= end) {
/* 1159 */       pivot = start + end >>> 1;
/* 1160 */       int midVal = files[pivot].getLastPieceNumber();
/*      */       
/* 1162 */       if (midVal < firstPiece) {
/* 1163 */         start = pivot + 1;
/* 1164 */       } else if (midVal > firstPiece) {
/* 1165 */         end = pivot - 1;
/*      */       }
/*      */       else {
/* 1168 */         while ((pivot > 0) && (files[(pivot - 1)].getLastPieceNumber() == firstPiece)) {
/* 1169 */           pivot--;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1174 */     return pivot;
/*      */   }
/*      */   
/*      */   public static boolean fileMustExist(DownloadManager download_manager, DiskManagerFileInfo file)
/*      */   {
/* 1179 */     Map resumeData = getResumeData(download_manager);
/*      */     
/* 1181 */     byte[] resumePieces = resumeData != null ? (byte[])resumeData.get("resume data") : null;
/*      */     
/* 1183 */     boolean sharesAnyNeededPieces = false;
/*      */     
/* 1185 */     DiskManagerFileInfo[] files = download_manager.getDiskManagerFileInfo();
/* 1186 */     int firstPiece = file.getFirstPieceNumber();
/* 1187 */     int lastPiece = file.getLastPieceNumber();
/*      */     
/* 1189 */     int firstFile = findFirstFileWithPieceN(firstPiece, files);
/*      */     
/*      */ 
/* 1192 */     for (int i = firstFile; (i < files.length) && (!sharesAnyNeededPieces); i++)
/*      */     {
/* 1194 */       DiskManagerFileInfo currentFile = files[i];
/* 1195 */       if (currentFile.getLastPieceNumber() >= firstPiece)
/*      */       {
/* 1197 */         if ((currentFile.getIndex() == file.getIndex()) && (resumePieces != null) && (file.getStorageType() != 2) && (file.getStorageType() != 4))
/* 1198 */           for (int j = firstPiece; (j <= lastPiece) && (!sharesAnyNeededPieces); j++)
/* 1199 */             sharesAnyNeededPieces |= resumePieces[j] != 0;
/* 1200 */         if (currentFile.getFirstPieceNumber() > lastPiece)
/*      */           break;
/* 1202 */         if ((currentFile.getFirstPieceNumber() <= firstPiece) && (firstPiece <= currentFile.getLastPieceNumber()))
/* 1203 */           sharesAnyNeededPieces |= !currentFile.isSkipped();
/* 1204 */         if ((currentFile.getFirstPieceNumber() <= lastPiece) && (lastPiece <= currentFile.getLastPieceNumber()))
/* 1205 */           sharesAnyNeededPieces |= !currentFile.isSkipped();
/*      */       }
/*      */     }
/* 1208 */     return sharesAnyNeededPieces;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int storageTypeChanged(DownloadManager download_manager, DiskManagerFileInfo file)
/*      */   {
/* 1216 */     return clearResumeDataSupport(download_manager, file, false, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void clearResumeData(DownloadManager download_manager, DiskManagerFileInfo file)
/*      */   {
/* 1224 */     clearResumeDataSupport(download_manager, file, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void recheckFile(DownloadManager download_manager, DiskManagerFileInfo file)
/*      */   {
/* 1232 */     clearResumeDataSupport(download_manager, file, true, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setTorrentResumeDataNearlyComplete(DownloadManagerState download_manager_state)
/*      */   {
/* 1241 */     TOTorrent torrent = download_manager_state.getTorrent();
/*      */     
/* 1243 */     long piece_count = torrent.getNumberOfPieces();
/*      */     
/* 1245 */     byte[] resume_pieces = new byte[(int)piece_count];
/*      */     
/* 1247 */     Arrays.fill(resume_pieces, (byte)1);
/*      */     
/*      */ 
/*      */ 
/* 1251 */     for (int i = 0; i < 3; i++)
/*      */     {
/* 1253 */       int piece_num = (int)(Math.random() * piece_count);
/*      */       
/* 1255 */       resume_pieces[piece_num] = 2;
/*      */     }
/*      */     
/* 1258 */     Map resumeMap = new HashMap();
/*      */     
/* 1260 */     resumeMap.put("resume data", resume_pieces);
/*      */     
/* 1262 */     Map partialPieces = new HashMap();
/*      */     
/* 1264 */     resumeMap.put("blocks", partialPieces);
/*      */     
/* 1266 */     resumeMap.put("valid", new Long(0L));
/*      */     
/* 1268 */     saveResumeData(download_manager_state, resumeMap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isTorrentResumeDataComplete(DownloadManagerState dms)
/*      */   {
/* 1277 */     Map resume_data = getResumeData(dms);
/*      */     
/* 1279 */     return isTorrentResumeDataComplete(dms, resume_data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static boolean isTorrentResumeDataComplete(DownloadManagerState download_manager_state, Map resume_data)
/*      */   {
/*      */     try
/*      */     {
/* 1288 */       int piece_count = download_manager_state.getTorrent().getNumberOfPieces();
/*      */       
/* 1290 */       if (resume_data != null)
/*      */       {
/* 1292 */         byte[] pieces = (byte[])resume_data.get("resume data");
/* 1293 */         Map blocks = (Map)resume_data.get("blocks");
/* 1294 */         boolean valid = ((Long)resume_data.get("valid")).intValue() == 1;
/*      */         
/*      */ 
/*      */ 
/* 1298 */         if ((blocks == null) || (blocks.size() > 0))
/*      */         {
/* 1300 */           return false;
/*      */         }
/*      */         
/* 1303 */         if ((valid) && (pieces != null) && (pieces.length == piece_count))
/*      */         {
/* 1305 */           for (int i = 0; i < pieces.length; i++)
/*      */           {
/* 1307 */             if (pieces[i] != 1)
/*      */             {
/*      */ 
/*      */ 
/* 1311 */               return false;
/*      */             }
/*      */           }
/*      */           
/* 1315 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1320 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/* 1323 */     return false;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/resume/RDResumeHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */