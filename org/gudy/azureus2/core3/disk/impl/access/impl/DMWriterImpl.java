/*     */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessController;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequest;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequestListener;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerException;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerHelper;
/*     */ import org.gudy.azureus2.core3.disk.impl.access.DMWriter;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*     */ 
/*     */ 
/*     */ public class DMWriterImpl
/*     */   implements DMWriter
/*     */ {
/*  48 */   static final LogIDs LOGID = LogIDs.DISK;
/*     */   
/*     */   private static final int MIN_ZERO_BLOCK = 1048576;
/*     */   
/*     */   final DiskManagerHelper disk_manager;
/*     */   
/*     */   final DiskAccessController disk_access;
/*     */   private int async_writes;
/*  56 */   final Set write_requests = new HashSet();
/*  57 */   final AESemaphore async_write_sem = new AESemaphore("DMWriter::asyncWrite");
/*     */   
/*     */   private boolean started;
/*     */   
/*     */   private volatile boolean stopped;
/*     */   
/*     */   private final int pieceLength;
/*     */   
/*     */   private final long totalLength;
/*     */   
/*     */   private boolean complete_recheck_in_progress;
/*  68 */   final AEMonitor this_mon = new AEMonitor("DMWriter");
/*     */   
/*     */ 
/*     */ 
/*     */   public DMWriterImpl(DiskManagerHelper _disk_manager)
/*     */   {
/*  74 */     this.disk_manager = _disk_manager;
/*  75 */     this.disk_access = this.disk_manager.getDiskAccessController();
/*     */     
/*  77 */     this.pieceLength = this.disk_manager.getPieceLength();
/*  78 */     this.totalLength = this.disk_manager.getTotalLength();
/*     */   }
/*     */   
/*     */   public void start()
/*     */   {
/*     */     try
/*     */     {
/*  85 */       this.this_mon.enter();
/*     */       
/*  87 */       if (this.started)
/*     */       {
/*  89 */         throw new RuntimeException("DMWWriter: start while started");
/*     */       }
/*     */       
/*  92 */       if (this.stopped)
/*     */       {
/*  94 */         throw new RuntimeException("DMWWriter: start after stopped");
/*     */       }
/*     */       
/*  97 */       this.started = true;
/*     */     }
/*     */     finally
/*     */     {
/* 101 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */     int write_wait;
/*     */     try
/*     */     {
/* 111 */       this.this_mon.enter();
/*     */       
/* 113 */       if ((this.stopped) || (!this.started)) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 118 */       this.stopped = true;
/*     */       
/* 120 */       write_wait = this.async_writes;
/*     */     }
/*     */     finally
/*     */     {
/* 124 */       this.this_mon.exit();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 129 */     long log_time = SystemTime.getCurrentTime();
/*     */     
/* 131 */     for (int i = 0; i < write_wait; i++)
/*     */     {
/* 133 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 135 */       if (now < log_time)
/*     */       {
/* 137 */         log_time = now;
/*     */ 
/*     */ 
/*     */       }
/* 141 */       else if (now - log_time > 1000L)
/*     */       {
/* 143 */         log_time = now;
/*     */         
/* 145 */         if (Logger.isEnabled())
/*     */         {
/* 147 */           Logger.log(new LogEvent(this.disk_manager, LOGID, "Waiting for writes to complete - " + (write_wait - i) + " remaining"));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 152 */       this.async_write_sem.reserve();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isChecking()
/*     */   {
/* 159 */     return this.complete_recheck_in_progress;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean zeroFile(DiskManagerFileInfoImpl file, long length)
/*     */     throws DiskManagerException
/*     */   {
/* 167 */     CacheFile cache_file = file.getCacheFile();
/*     */     try
/*     */     {
/* 170 */       if (length == 0L)
/*     */       {
/* 172 */         cache_file.setLength(0L);
/*     */       }
/*     */       else {
/* 175 */         int buffer_size = this.pieceLength < 1048576 ? 1048576 : this.pieceLength;
/*     */         
/* 177 */         buffer_size = (buffer_size + 1023) / 1024 * 1024;
/*     */         
/* 179 */         DirectByteBuffer buffer = DirectByteBufferPool.getBuffer((byte)7, buffer_size);
/*     */         
/* 181 */         long remainder = length;
/* 182 */         long written = 0L;
/*     */         try
/*     */         {
/* 185 */           byte[] blanks = new byte['Ѐ'];
/*     */           
/* 187 */           for (int i = 0; i < buffer_size / 1024; i++)
/*     */           {
/* 189 */             buffer.put((byte)8, blanks);
/*     */           }
/*     */           
/* 192 */           buffer.position((byte)8, 0);
/*     */           
/* 194 */           while ((remainder > 0L) && (!this.stopped))
/*     */           {
/* 196 */             int write_size = buffer_size;
/*     */             
/* 198 */             if (remainder < write_size)
/*     */             {
/* 200 */               write_size = (int)remainder;
/*     */               
/* 202 */               buffer.limit((byte)8, write_size);
/*     */             }
/*     */             
/* 205 */             final AESemaphore sem = new AESemaphore("DMW&C:zeroFile");
/* 206 */             final Throwable[] op_failed = { null };
/*     */             
/* 208 */             this.disk_access.queueWriteRequest(cache_file, written, buffer, false, new DiskAccessRequestListener()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               public void requestComplete(DiskAccessRequest request)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 219 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void requestCancelled(DiskAccessRequest request)
/*     */               {
/* 226 */                 op_failed[0] = new Throwable("Request cancelled");
/*     */                 
/* 228 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void requestFailed(DiskAccessRequest request, Throwable cause)
/*     */               {
/* 236 */                 op_failed[0] = cause;
/*     */                 
/* 238 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */               public int getPriority()
/*     */               {
/* 244 */                 return -1;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               public void requestExecuted(long bytes) {}
/* 252 */             });
/* 253 */             sem.reserve();
/*     */             
/* 255 */             if (op_failed[0] != null)
/*     */             {
/* 257 */               throw op_failed[0];
/*     */             }
/*     */             
/* 260 */             buffer.position((byte)8, 0);
/*     */             
/* 262 */             written += write_size;
/* 263 */             remainder -= write_size;
/*     */             
/* 265 */             this.disk_manager.setAllocated(this.disk_manager.getAllocated() + write_size);
/*     */             
/* 267 */             this.disk_manager.setPercentDone((int)(this.disk_manager.getAllocated() * 1000L / this.totalLength));
/*     */           }
/*     */         }
/*     */         finally {
/* 271 */           buffer.returnToPool();
/*     */         }
/*     */         
/* 274 */         cache_file.flushCache();
/*     */       }
/*     */       
/* 277 */       if (this.stopped)
/*     */       {
/* 279 */         return false;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 283 */       Debug.printStackTrace(e);
/*     */       
/* 285 */       throw new DiskManagerException(e);
/*     */     }
/*     */     
/* 288 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerWriteRequest createWriteRequest(int pieceNumber, int offset, DirectByteBuffer buffer, Object user_data)
/*     */   {
/* 300 */     return new DiskManagerWriteRequestImpl(pieceNumber, offset, buffer, user_data);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasOutstandingWriteRequestForPiece(int piece_number)
/*     */   {
/*     */     try
/*     */     {
/* 308 */       this.this_mon.enter();
/*     */       
/* 310 */       Iterator it = this.write_requests.iterator();
/*     */       DiskManagerWriteRequest request;
/* 312 */       while (it.hasNext())
/*     */       {
/* 314 */         request = (DiskManagerWriteRequest)it.next();
/*     */         
/* 316 */         if (request.getPieceNumber() == piece_number)
/*     */         {
/* 318 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 322 */       return 0;
/*     */     }
/*     */     finally
/*     */     {
/* 326 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeBlock(final DiskManagerWriteRequest request, final DiskManagerWriteRequestListener _listener)
/*     */   {
/* 336 */     request.requestStarts();
/*     */     
/* 338 */     final DiskManagerWriteRequestListener listener = new DiskManagerWriteRequestListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void writeCompleted(DiskManagerWriteRequest request)
/*     */       {
/*     */ 
/* 345 */         request.requestEnds(true);
/*     */         
/* 347 */         _listener.writeCompleted(request);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void writeFailed(DiskManagerWriteRequest request, Throwable cause)
/*     */       {
/* 355 */         request.requestEnds(false);
/*     */         
/* 357 */         _listener.writeFailed(request, cause);
/*     */       }
/*     */     };
/*     */     try
/*     */     {
/* 362 */       int pieceNumber = request.getPieceNumber();
/* 363 */       DirectByteBuffer buffer = request.getBuffer();
/* 364 */       int offset = request.getOffset();
/*     */       
/*     */ 
/*     */ 
/* 368 */       final DiskManagerPiece dmPiece = this.disk_manager.getPieces()[pieceNumber];
/*     */       
/* 370 */       if (dmPiece.isDone())
/*     */       {
/*     */ 
/*     */ 
/* 374 */         buffer.returnToPool();
/*     */         
/* 376 */         listener.writeCompleted(request);
/*     */       }
/*     */       else
/*     */       {
/* 380 */         int buffer_position = buffer.position((byte)8);
/* 381 */         int buffer_limit = buffer.limit((byte)8);
/*     */         
/*     */ 
/*     */ 
/* 385 */         int previousFilesLength = 0;
/*     */         
/* 387 */         int currentFile = 0;
/*     */         
/* 389 */         DMPieceList pieceList = this.disk_manager.getPieceList(pieceNumber);
/*     */         
/* 391 */         DMPieceMapEntry current_piece = pieceList.get(currentFile);
/*     */         
/* 393 */         long fileOffset = current_piece.getOffset();
/*     */         
/* 395 */         while (previousFilesLength + current_piece.getLength() < offset)
/*     */         {
/* 397 */           previousFilesLength += current_piece.getLength();
/*     */           
/* 399 */           currentFile++;
/*     */           
/* 401 */           fileOffset = 0L;
/*     */           
/* 403 */           current_piece = pieceList.get(currentFile);
/*     */         }
/*     */         
/* 406 */         List chunks = new ArrayList();
/*     */         
/*     */ 
/*     */ 
/* 410 */         while (buffer_position < buffer_limit)
/*     */         {
/* 412 */           current_piece = pieceList.get(currentFile);
/*     */           
/* 414 */           long file_limit = buffer_position + (current_piece.getFile().getLength() - current_piece.getOffset() - (offset - previousFilesLength));
/*     */           
/*     */ 
/*     */ 
/* 418 */           if (file_limit > buffer_limit)
/*     */           {
/* 420 */             file_limit = buffer_limit;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 425 */           if (file_limit > buffer_position)
/*     */           {
/* 427 */             long file_pos = fileOffset + (offset - previousFilesLength);
/*     */             
/* 429 */             chunks.add(new Object[] { current_piece.getFile(), new Long(file_pos), new Integer((int)file_limit) });
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 434 */             buffer_position = (int)file_limit;
/*     */           }
/*     */           
/* 437 */           currentFile++;
/*     */           
/* 439 */           fileOffset = 0L;
/*     */           
/* 441 */           previousFilesLength = offset;
/*     */         }
/*     */         
/*     */ 
/* 445 */         DiskManagerWriteRequestListener l = new DiskManagerWriteRequestListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void writeCompleted(DiskManagerWriteRequest request)
/*     */           {
/*     */ 
/* 452 */             complete();
/*     */             
/* 454 */             listener.writeCompleted(request);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           public void writeFailed(DiskManagerWriteRequest request, Throwable cause)
/*     */           {
/* 462 */             complete();
/*     */             
/* 464 */             if (dmPiece.isDone())
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 472 */               if (Logger.isEnabled())
/*     */               {
/* 474 */                 Logger.log(new LogEvent(DMWriterImpl.this.disk_manager, DMWriterImpl.LOGID, "Piece " + dmPiece.getPieceNumber() + " write failed but already marked as done"));
/*     */               }
/*     */               
/* 477 */               listener.writeCompleted(request);
/*     */             }
/*     */             else
/*     */             {
/* 481 */               DMWriterImpl.this.disk_manager.setFailed("Disk write error - " + Debug.getNestedExceptionMessage(cause));
/*     */               
/* 483 */               Debug.printStackTrace(cause);
/*     */               
/* 485 */               listener.writeFailed(request, cause);
/*     */             }
/*     */           }
/*     */           
/*     */           protected void complete()
/*     */           {
/*     */             try
/*     */             {
/* 493 */               DMWriterImpl.this.this_mon.enter();
/*     */               
/* 495 */               DMWriterImpl.access$010(DMWriterImpl.this);
/*     */               
/* 497 */               if (!DMWriterImpl.this.write_requests.remove(request))
/*     */               {
/* 499 */                 Debug.out("request not found");
/*     */               }
/*     */               
/* 502 */               if (DMWriterImpl.this.stopped)
/*     */               {
/* 504 */                 DMWriterImpl.this.async_write_sem.release();
/*     */               }
/*     */             }
/*     */             finally {
/* 508 */               DMWriterImpl.this.this_mon.exit();
/*     */             }
/*     */           }
/*     */         };
/*     */         try
/*     */         {
/* 514 */           this.this_mon.enter();
/*     */           
/* 516 */           if (this.stopped)
/*     */           {
/* 518 */             buffer.returnToPool();
/*     */             
/* 520 */             listener.writeFailed(request, new Exception("Disk writer has been stopped")); return;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 526 */           this.async_writes += 1;
/*     */           
/* 528 */           this.write_requests.add(request);
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 533 */           this.this_mon.exit();
/*     */         }
/*     */         
/* 536 */         new requestDispatcher(request, l, buffer, chunks);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 540 */       request.getBuffer().returnToPool();
/*     */       
/* 542 */       this.disk_manager.setFailed("Disk write error - " + Debug.getNestedExceptionMessage(e));
/*     */       
/* 544 */       Debug.printStackTrace(e);
/*     */       
/* 546 */       listener.writeFailed(request, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class requestDispatcher
/*     */     implements DiskAccessRequestListener
/*     */   {
/*     */     private final DiskManagerWriteRequest request;
/*     */     
/*     */     private final DiskManagerWriteRequestListener listener;
/*     */     
/*     */     private final DirectByteBuffer buffer;
/*     */     
/*     */     private final List chunks;
/*     */     
/*     */     private int chunk_index;
/*     */     
/*     */ 
/*     */     protected requestDispatcher(DiskManagerWriteRequest _request, DiskManagerWriteRequestListener _listener, DirectByteBuffer _buffer, List _chunks)
/*     */     {
/* 568 */       this.request = _request;
/* 569 */       this.listener = _listener;
/* 570 */       this.buffer = _buffer;
/* 571 */       this.chunks = _chunks;
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
/* 588 */       dispatch();
/*     */     }
/*     */     
/*     */     protected void dispatch()
/*     */     {
/*     */       try
/*     */       {
/* 595 */         if (this.chunk_index == this.chunks.size())
/*     */         {
/* 597 */           this.listener.writeCompleted(this.request);
/*     */ 
/*     */ 
/*     */         }
/* 601 */         else if ((this.chunk_index == 1) && (this.chunks.size() > 32))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 606 */           for (int i = 1; i < this.chunks.size(); i++)
/*     */           {
/* 608 */             final AESemaphore sem = new AESemaphore("DMW&C:dispatch:asyncReq");
/* 609 */             final Throwable[] error = { null };
/*     */             
/* 611 */             doRequest(new DiskAccessRequestListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void requestComplete(DiskAccessRequest request)
/*     */               {
/*     */ 
/* 618 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void requestCancelled(DiskAccessRequest request)
/*     */               {
/* 625 */                 Debug.out("shouldn't get here");
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void requestFailed(DiskAccessRequest request, Throwable cause)
/*     */               {
/* 633 */                 error[0] = cause;
/*     */                 
/* 635 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */               public int getPriority()
/*     */               {
/* 641 */                 return -1;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               public void requestExecuted(long bytes) {}
/* 649 */             });
/* 650 */             sem.reserve();
/*     */             
/* 652 */             if (error[0] != null)
/*     */             {
/* 654 */               throw error[0];
/*     */             }
/*     */           }
/*     */           
/* 658 */           this.listener.writeCompleted(this.request);
/*     */         }
/*     */         else
/*     */         {
/* 662 */           doRequest(this);
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 667 */         failed(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void doRequest(final DiskAccessRequestListener l)
/*     */       throws CacheFileManagerException
/*     */     {
/* 677 */       Object[] stuff = (Object[])this.chunks.get(this.chunk_index++);
/*     */       
/* 679 */       final DiskManagerFileInfoImpl file = (DiskManagerFileInfoImpl)stuff[0];
/*     */       
/* 681 */       this.buffer.limit((byte)7, ((Integer)stuff[2]).intValue());
/*     */       
/* 683 */       if (file.getAccessMode() == 1)
/*     */       {
/* 685 */         if (Logger.isEnabled()) {
/* 686 */           Logger.log(new LogEvent(DMWriterImpl.this.disk_manager, DMWriterImpl.LOGID, "Changing " + file.getFile(true).getName() + " to read/write"));
/*     */         }
/*     */         
/*     */ 
/* 690 */         file.setAccessMode(2);
/*     */       }
/*     */       
/* 693 */       boolean handover_buffer = this.chunk_index == this.chunks.size();
/*     */       
/* 695 */       DiskAccessRequestListener delegate_listener = new DiskAccessRequestListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void requestComplete(DiskAccessRequest request)
/*     */         {
/*     */ 
/* 702 */           l.requestComplete(request);
/*     */           
/* 704 */           file.dataWritten(request.getOffset(), request.getSize());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void requestCancelled(DiskAccessRequest request)
/*     */         {
/* 711 */           l.requestCancelled(request);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void requestFailed(DiskAccessRequest request, Throwable cause)
/*     */         {
/* 719 */           l.requestFailed(request, cause);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getPriority()
/*     */         {
/* 725 */           return -1;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void requestExecuted(long bytes) {}
/* 733 */       };
/* 734 */       DMWriterImpl.this.disk_access.queueWriteRequest(file.getCacheFile(), ((Long)stuff[1]).longValue(), this.buffer, handover_buffer, delegate_listener);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void requestComplete(DiskAccessRequest request)
/*     */     {
/* 746 */       dispatch();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void requestCancelled(DiskAccessRequest request)
/*     */     {
/* 755 */       Debug.out("shouldn't get here");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void requestFailed(DiskAccessRequest request, Throwable cause)
/*     */     {
/* 763 */       failed(cause);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getPriority()
/*     */     {
/* 770 */       return -1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void requestExecuted(long bytes) {}
/*     */     
/*     */ 
/*     */ 
/*     */     protected void failed(Throwable cause)
/*     */     {
/* 782 */       this.buffer.returnToPool();
/*     */       
/* 784 */       this.listener.writeFailed(this.request, cause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DMWriterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */