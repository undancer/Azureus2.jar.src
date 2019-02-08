/*     */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessController;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequest;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequestListener;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerHelper;
/*     */ import org.gudy.azureus2.core3.disk.impl.access.DMReader;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DMReaderImpl
/*     */   implements DMReader
/*     */ {
/*  49 */   private static final LogIDs LOGID = LogIDs.DISK;
/*     */   
/*     */   final DiskManagerHelper disk_manager;
/*     */   
/*     */   final DiskAccessController disk_access;
/*     */   private int async_reads;
/*  55 */   final Set read_requests = new HashSet();
/*  56 */   final AESemaphore async_read_sem = new AESemaphore("DMReader:asyncReads");
/*     */   
/*     */   private boolean started;
/*     */   
/*     */   private boolean stopped;
/*     */   
/*     */   private long total_read_ops;
/*     */   private long total_read_bytes;
/*  64 */   protected final AEMonitor this_mon = new AEMonitor("DMReader");
/*     */   
/*     */ 
/*     */ 
/*     */   public DMReaderImpl(DiskManagerHelper _disk_manager)
/*     */   {
/*  70 */     this.disk_manager = _disk_manager;
/*     */     
/*  72 */     this.disk_access = this.disk_manager.getDiskAccessController();
/*     */   }
/*     */   
/*     */   public void start()
/*     */   {
/*     */     try
/*     */     {
/*  79 */       this.this_mon.enter();
/*     */       
/*  81 */       if (this.started)
/*     */       {
/*  83 */         throw new RuntimeException("can't start twice");
/*     */       }
/*     */       
/*  86 */       if (this.stopped)
/*     */       {
/*  88 */         throw new RuntimeException("already been stopped");
/*     */       }
/*     */       
/*  91 */       this.started = true;
/*     */     }
/*     */     finally
/*     */     {
/*  95 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */   {
/*     */     int read_wait;
/*     */     try
/*     */     {
/* 105 */       this.this_mon.enter();
/*     */       
/* 107 */       if ((this.stopped) || (!this.started)) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 112 */       this.stopped = true;
/*     */       
/* 114 */       read_wait = this.async_reads;
/*     */     }
/*     */     finally
/*     */     {
/* 118 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 121 */     long log_time = SystemTime.getCurrentTime();
/*     */     
/* 123 */     for (int i = 0; i < read_wait; i++)
/*     */     {
/* 125 */       long now = SystemTime.getCurrentTime();
/*     */       
/* 127 */       if (now < log_time)
/*     */       {
/* 129 */         log_time = now;
/*     */ 
/*     */ 
/*     */       }
/* 133 */       else if (now - log_time > 1000L)
/*     */       {
/* 135 */         log_time = now;
/*     */         
/* 137 */         if (Logger.isEnabled())
/*     */         {
/* 139 */           Logger.log(new LogEvent(this.disk_manager, LOGID, "Waiting for reads to complete - " + (read_wait - i) + " remaining"));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 144 */       this.async_read_sem.reserve();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerReadRequest createReadRequest(int pieceNumber, int offset, int length)
/*     */   {
/* 154 */     return new DiskManagerReadRequestImpl(pieceNumber, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasOutstandingReadRequestForPiece(int piece_number)
/*     */   {
/*     */     try
/*     */     {
/* 162 */       this.this_mon.enter();
/*     */       
/* 164 */       Iterator it = this.read_requests.iterator();
/*     */       DiskManagerReadRequest request;
/* 166 */       while (it.hasNext())
/*     */       {
/* 168 */         request = (DiskManagerReadRequest)((Object[])(Object[])it.next())[0];
/*     */         
/* 170 */         if (request.getPieceNumber() == piece_number)
/*     */         {
/* 172 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 176 */       return 0;
/*     */     }
/*     */     finally
/*     */     {
/* 180 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getStats()
/*     */   {
/* 187 */     return new long[] { this.total_read_ops, this.total_read_bytes };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DirectByteBuffer readBlock(int pieceNumber, int offset, int length)
/*     */   {
/* 198 */     DiskManagerReadRequest request = createReadRequest(pieceNumber, offset, length);
/*     */     
/* 200 */     final AESemaphore sem = new AESemaphore("DMReader:readBlock");
/*     */     
/* 202 */     final DirectByteBuffer[] result = { null };
/*     */     
/* 204 */     readBlock(request, new DiskManagerReadRequestListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void readCompleted(DiskManagerReadRequest request, DirectByteBuffer data)
/*     */       {
/*     */ 
/*     */ 
/* 213 */         result[0] = data;
/*     */         
/* 215 */         sem.release();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void readFailed(DiskManagerReadRequest request, Throwable cause)
/*     */       {
/* 223 */         sem.release();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPriority()
/*     */       {
/* 229 */         return -1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void requestExecuted(long bytes) {}
/* 237 */     });
/* 238 */     sem.reserve();
/*     */     
/* 240 */     return result[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void readBlock(DiskManagerReadRequest request, final DiskManagerReadRequestListener _listener)
/*     */   {
/* 248 */     request.requestStarts();
/*     */     
/* 250 */     final DiskManagerReadRequestListener listener = new DiskManagerReadRequestListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void readCompleted(DiskManagerReadRequest request, DirectByteBuffer data)
/*     */       {
/*     */ 
/*     */ 
/* 258 */         request.requestEnds(true);
/*     */         
/* 260 */         _listener.readCompleted(request, data);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void readFailed(DiskManagerReadRequest request, Throwable cause)
/*     */       {
/* 268 */         request.requestEnds(false);
/*     */         
/* 270 */         _listener.readFailed(request, cause);
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPriority()
/*     */       {
/* 276 */         return _listener.getPriority();
/*     */       }
/*     */       
/*     */       public void requestExecuted(long bytes)
/*     */       {
/* 281 */         _listener.requestExecuted(bytes);
/*     */       }
/*     */       
/* 284 */     };
/* 285 */     DirectByteBuffer buffer = null;
/*     */     try
/*     */     {
/* 288 */       int length = request.getLength();
/*     */       
/* 290 */       buffer = DirectByteBufferPool.getBuffer((byte)6, length);
/*     */       
/* 292 */       if (buffer == null)
/*     */       {
/* 294 */         Debug.out("DiskManager::readBlock:: ByteBufferPool returned null buffer");
/*     */         
/* 296 */         listener.readFailed(request, new Exception("Out of memory"));
/*     */         
/* 298 */         return;
/*     */       }
/*     */       
/* 301 */       int pieceNumber = request.getPieceNumber();
/* 302 */       int offset = request.getOffset();
/*     */       
/* 304 */       DMPieceList pieceList = this.disk_manager.getPieceList(pieceNumber);
/*     */       
/*     */ 
/*     */ 
/* 308 */       if (pieceList.size() == 0)
/*     */       {
/* 310 */         Debug.out("no pieceList entries for " + pieceNumber);
/*     */         
/* 312 */         listener.readCompleted(request, buffer);
/*     */         
/* 314 */         return;
/*     */       }
/*     */       
/* 317 */       long previousFilesLength = 0L;
/*     */       
/* 319 */       int currentFile = 0;
/*     */       
/* 321 */       long fileOffset = pieceList.get(0).getOffset();
/*     */       
/* 323 */       while ((currentFile < pieceList.size()) && (pieceList.getCumulativeLengthToPiece(currentFile) < offset))
/*     */       {
/* 325 */         previousFilesLength = pieceList.getCumulativeLengthToPiece(currentFile);
/*     */         
/* 327 */         currentFile++;
/*     */         
/* 329 */         fileOffset = 0L;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 334 */       fileOffset += offset - previousFilesLength;
/*     */       
/* 336 */       List chunks = new ArrayList();
/*     */       
/* 338 */       int buffer_position = 0;
/*     */       
/* 340 */       while ((buffer_position < length) && (currentFile < pieceList.size()))
/*     */       {
/* 342 */         DMPieceMapEntry map_entry = pieceList.get(currentFile);
/*     */         
/* 344 */         int length_available = map_entry.getLength() - (int)(fileOffset - map_entry.getOffset());
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 349 */         int entry_read_limit = buffer_position + length_available;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 354 */         entry_read_limit = Math.min(length, entry_read_limit);
/*     */         
/*     */ 
/*     */ 
/* 358 */         chunks.add(new Object[] { map_entry.getFile().getCacheFile(), new Long(fileOffset), new Integer(entry_read_limit) });
/*     */         
/* 360 */         buffer_position = entry_read_limit;
/*     */         
/* 362 */         currentFile++;
/*     */         
/* 364 */         fileOffset = 0L;
/*     */       }
/*     */       
/* 367 */       if (chunks.size() == 0)
/*     */       {
/* 369 */         Debug.out("no chunk reads for " + pieceNumber);
/*     */         
/* 371 */         listener.readCompleted(request, buffer);
/*     */         
/* 373 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 382 */       final Object[] request_wrapper = { request };
/*     */       
/* 384 */       DiskManagerReadRequestListener l = new DiskManagerReadRequestListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void readCompleted(DiskManagerReadRequest request, DirectByteBuffer data)
/*     */         {
/*     */ 
/*     */ 
/* 392 */           complete();
/*     */           
/* 394 */           listener.readCompleted(request, data);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void readFailed(DiskManagerReadRequest request, Throwable cause)
/*     */         {
/* 402 */           complete();
/*     */           
/* 404 */           listener.readFailed(request, cause);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getPriority()
/*     */         {
/* 410 */           return _listener.getPriority();
/*     */         }
/*     */         
/*     */ 
/*     */         public void requestExecuted(long bytes)
/*     */         {
/* 416 */           _listener.requestExecuted(bytes);
/*     */         }
/*     */         
/*     */         protected void complete()
/*     */         {
/*     */           try
/*     */           {
/* 423 */             DMReaderImpl.this.this_mon.enter();
/*     */             
/* 425 */             DMReaderImpl.access$010(DMReaderImpl.this);
/*     */             
/* 427 */             if (!DMReaderImpl.this.read_requests.remove(request_wrapper))
/*     */             {
/* 429 */               Debug.out("request not found");
/*     */             }
/*     */             
/* 432 */             if (DMReaderImpl.this.stopped)
/*     */             {
/* 434 */               DMReaderImpl.this.async_read_sem.release();
/*     */             }
/*     */           }
/*     */           finally {
/* 438 */             DMReaderImpl.this.this_mon.exit();
/*     */           }
/*     */         }
/*     */       };
/*     */       try
/*     */       {
/* 444 */         this.this_mon.enter();
/*     */         
/* 446 */         if (this.stopped)
/*     */         {
/* 448 */           buffer.returnToPool();
/*     */           
/* 450 */           listener.readFailed(request, new Exception("Disk reader has been stopped")); return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 455 */         this.async_reads += 1;
/*     */         
/* 457 */         this.read_requests.add(request_wrapper);
/*     */       }
/*     */       finally
/*     */       {
/* 461 */         this.this_mon.exit();
/*     */       }
/*     */       
/* 464 */       new requestDispatcher(request, l, buffer, chunks);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 468 */       if (buffer != null)
/*     */       {
/* 470 */         buffer.returnToPool();
/*     */       }
/*     */       
/* 473 */       this.disk_manager.setFailed("Disk read error - " + Debug.getNestedExceptionMessage(e));
/*     */       
/* 475 */       Debug.printStackTrace(e);
/*     */       
/* 477 */       listener.readFailed(request, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class requestDispatcher
/*     */     implements DiskAccessRequestListener
/*     */   {
/*     */     private final DiskManagerReadRequest dm_request;
/*     */     
/*     */     final DiskManagerReadRequestListener listener;
/*     */     
/*     */     private final DirectByteBuffer buffer;
/*     */     
/*     */     private final List chunks;
/*     */     
/*     */     private final int buffer_length;
/*     */     
/*     */     private int chunk_index;
/*     */     
/*     */     private int chunk_limit;
/*     */     
/*     */ 
/*     */     protected requestDispatcher(DiskManagerReadRequest _request, DiskManagerReadRequestListener _listener, DirectByteBuffer _buffer, List _chunks)
/*     */     {
/* 502 */       this.dm_request = _request;
/* 503 */       this.listener = _listener;
/* 504 */       this.buffer = _buffer;
/* 505 */       this.chunks = _chunks;
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
/* 522 */       this.buffer_length = this.buffer.limit((byte)7);
/*     */       
/* 524 */       dispatch();
/*     */     }
/*     */     
/*     */     protected void dispatch()
/*     */     {
/*     */       try
/*     */       {
/* 531 */         if (this.chunk_index == this.chunks.size())
/*     */         {
/* 533 */           this.buffer.limit((byte)7, this.buffer_length);
/*     */           
/* 535 */           this.buffer.position((byte)7, 0);
/*     */           
/* 537 */           this.listener.readCompleted(this.dm_request, this.buffer);
/*     */ 
/*     */ 
/*     */         }
/* 541 */         else if ((this.chunk_index == 1) && (this.chunks.size() > 32))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 546 */           for (int i = 1; i < this.chunks.size(); i++)
/*     */           {
/* 548 */             final AESemaphore sem = new AESemaphore("DMR:dispatch:asyncReq");
/* 549 */             final Throwable[] error = { null };
/*     */             
/* 551 */             doRequest(new DiskAccessRequestListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void requestComplete(DiskAccessRequest request)
/*     */               {
/*     */ 
/* 558 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               public void requestCancelled(DiskAccessRequest request)
/*     */               {
/* 565 */                 Debug.out("shouldn't get here");
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */               public void requestFailed(DiskAccessRequest request, Throwable cause)
/*     */               {
/* 573 */                 error[0] = cause;
/*     */                 
/* 575 */                 sem.release();
/*     */               }
/*     */               
/*     */ 
/*     */               public int getPriority()
/*     */               {
/* 581 */                 return DMReaderImpl.requestDispatcher.this.listener.getPriority();
/*     */               }
/*     */               
/*     */ 
/*     */               public void requestExecuted(long bytes)
/*     */               {
/* 587 */                 if (bytes > 0L)
/*     */                 {
/* 589 */                   DMReaderImpl.access$214(DMReaderImpl.this, bytes);
/* 590 */                   DMReaderImpl.access$308(DMReaderImpl.this);
/*     */                 }
/*     */                 
/* 593 */                 DMReaderImpl.requestDispatcher.this.listener.requestExecuted(bytes);
/*     */               }
/*     */               
/* 596 */             });
/* 597 */             sem.reserve();
/*     */             
/* 599 */             if (error[0] != null)
/*     */             {
/* 601 */               throw error[0];
/*     */             }
/*     */           }
/*     */           
/* 605 */           this.buffer.limit((byte)7, this.buffer_length);
/*     */           
/* 607 */           this.buffer.position((byte)7, 0);
/*     */           
/* 609 */           this.listener.readCompleted(this.dm_request, this.buffer);
/*     */         }
/*     */         else {
/* 612 */           doRequest(this);
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 617 */         failed(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void doRequest(DiskAccessRequestListener l)
/*     */     {
/* 625 */       Object[] stuff = (Object[])this.chunks.get(this.chunk_index++);
/*     */       
/* 627 */       if (this.chunk_index > 0)
/*     */       {
/* 629 */         this.buffer.position((byte)7, this.chunk_limit);
/*     */       }
/*     */       
/* 632 */       this.chunk_limit = ((Integer)stuff[2]).intValue();
/*     */       
/* 634 */       this.buffer.limit((byte)7, this.chunk_limit);
/*     */       
/* 636 */       short cache_policy = this.dm_request.getUseCache() ? 1 : 0;
/*     */       
/* 638 */       if (this.dm_request.getFlush())
/*     */       {
/* 640 */         cache_policy = (short)(cache_policy | 0x2);
/*     */       }
/*     */       
/* 643 */       DMReaderImpl.this.disk_access.queueReadRequest((CacheFile)stuff[0], ((Long)stuff[1]).longValue(), this.buffer, cache_policy, l);
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
/* 655 */       dispatch();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void requestCancelled(DiskAccessRequest request)
/*     */     {
/* 664 */       Debug.out("shouldn't get here");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void requestFailed(DiskAccessRequest request, Throwable cause)
/*     */     {
/* 672 */       failed(cause);
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPriority()
/*     */     {
/* 678 */       return this.listener.getPriority();
/*     */     }
/*     */     
/*     */ 
/*     */     public void requestExecuted(long bytes)
/*     */     {
/* 684 */       if (bytes > 0L)
/*     */       {
/* 686 */         DMReaderImpl.access$214(DMReaderImpl.this, bytes);
/* 687 */         DMReaderImpl.access$308(DMReaderImpl.this);
/*     */       }
/*     */       
/* 690 */       this.listener.requestExecuted(bytes);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void failed(Throwable cause)
/*     */     {
/* 697 */       this.buffer.returnToPool();
/*     */       
/* 699 */       DMReaderImpl.this.disk_manager.setFailed("Disk read error - " + Debug.getNestedExceptionMessage(cause));
/*     */       
/* 701 */       Debug.printStackTrace(cause);
/*     */       
/* 703 */       this.listener.readFailed(this.dm_request, cause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DMReaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */