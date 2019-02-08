/*     */ package com.aelitis.azureus.core.diskmanager.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ 
/*     */ 
/*     */ public class DiskAccessControllerInstance
/*     */ {
/*     */   private final int aggregation_request_limit;
/*     */   private final int aggregation_byte_limit;
/*     */   private final String name;
/*     */   final boolean enable_aggregation;
/*  49 */   final boolean invert_threads = !COConfigurationManager.getBooleanParameter("diskmanager.perf.queue.torrent.bias");
/*     */   
/*     */   final int max_threads;
/*     */   
/*     */   private int max_mb_queued;
/*     */   
/*     */   private final groupSemaphore max_mb_sem;
/*     */   
/*     */   private long request_bytes_queued;
/*     */   
/*     */   private long requests_queued;
/*     */   
/*     */   private long total_requests;
/*     */   
/*     */   private long total_single_requests_made;
/*     */   
/*     */   private long total_aggregated_requests_made;
/*     */   private long total_bytes;
/*     */   private long total_single_bytes;
/*     */   private long total_aggregated_bytes;
/*     */   private long io_time;
/*     */   private long io_count;
/*     */   private final requestDispatcher[] dispatchers;
/*  72 */   private long last_check = 0L;
/*     */   
/*  74 */   private final Map torrent_dispatcher_map = new HashMap();
/*     */   
/*     */   private static final int REQUEST_NUM_LOG_CHUNK = 100;
/*     */   
/*     */   private static final int REQUEST_BYTE_LOG_CHUNK = 1048576;
/*  79 */   private int next_request_num_log = 100;
/*  80 */   private long next_request_byte_log = 1048576L;
/*     */   
/*  82 */   static final ThreadLocal tls = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     public Object initialValue()
/*     */     {
/*     */ 
/*  88 */       return null;
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskAccessControllerInstance(String _name, boolean _enable_aggregation, int _aggregation_request_limit, int _aggregation_byte_limit, int _max_threads, int _max_mb)
/*     */   {
/* 101 */     this.name = _name;
/*     */     
/* 103 */     this.enable_aggregation = _enable_aggregation;
/* 104 */     this.aggregation_request_limit = _aggregation_request_limit;
/* 105 */     this.aggregation_byte_limit = _aggregation_byte_limit;
/*     */     
/* 107 */     this.max_mb_queued = _max_mb;
/*     */     
/* 109 */     this.max_mb_sem = new groupSemaphore(this.max_mb_queued);
/* 110 */     this.max_threads = _max_threads;
/*     */     
/* 112 */     this.dispatchers = new requestDispatcher[this.invert_threads ? 1 : this.max_threads];
/*     */     
/* 114 */     for (int i = 0; i < this.dispatchers.length; i++) {
/* 115 */       this.dispatchers[i] = new requestDispatcher(i);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getName()
/*     */   {
/* 122 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getBlockCount()
/*     */   {
/* 128 */     return this.max_mb_sem.getBlockCount();
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getQueueSize()
/*     */   {
/* 134 */     return this.requests_queued;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getQueuedBytes()
/*     */   {
/* 140 */     return this.request_bytes_queued;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTotalRequests()
/*     */   {
/* 146 */     return this.total_requests;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTotalSingleRequests()
/*     */   {
/* 152 */     return this.total_single_requests_made;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTotalAggregatedRequests()
/*     */   {
/* 158 */     return this.total_aggregated_requests_made;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytes()
/*     */   {
/* 164 */     return this.total_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalSingleBytes()
/*     */   {
/* 170 */     return this.total_single_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalAggregatedBytes()
/*     */   {
/* 176 */     return this.total_aggregated_bytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getIOTime()
/*     */   {
/* 182 */     return this.io_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getIOCount()
/*     */   {
/* 188 */     return this.io_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void queueRequest(DiskAccessRequestImpl request)
/*     */   {
/*     */     requestDispatcher dispatcher;
/*     */     
/*     */     requestDispatcher dispatcher;
/* 197 */     if (this.dispatchers.length == 1)
/*     */     {
/* 199 */       dispatcher = this.dispatchers[0];
/*     */     }
/*     */     else
/*     */     {
/* 203 */       synchronized (this.torrent_dispatcher_map)
/*     */       {
/* 205 */         long now = System.currentTimeMillis();
/*     */         
/* 207 */         boolean check = false;
/*     */         
/* 209 */         if ((now - this.last_check > 60000L) || (now < this.last_check))
/*     */         {
/* 211 */           check = true;
/* 212 */           this.last_check = now;
/*     */         }
/*     */         
/* 215 */         if (check)
/*     */         {
/* 217 */           Iterator it = this.torrent_dispatcher_map.values().iterator();
/*     */           
/* 219 */           while (it.hasNext())
/*     */           {
/* 221 */             requestDispatcher d = (requestDispatcher)it.next();
/*     */             
/* 223 */             long last_active = d.getLastRequestTime();
/*     */             
/* 225 */             if (now - last_active > 60000L)
/*     */             {
/* 227 */               it.remove();
/*     */             }
/* 229 */             else if (now < last_active)
/*     */             {
/* 231 */               d.setLastRequestTime(now);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 236 */         TOTorrent torrent = request.getFile().getTorrentFile().getTorrent();
/*     */         
/* 238 */         dispatcher = (requestDispatcher)this.torrent_dispatcher_map.get(torrent);
/*     */         
/* 240 */         if (dispatcher == null)
/*     */         {
/* 242 */           int min_index = 0;
/* 243 */           int min_size = Integer.MAX_VALUE;
/*     */           
/* 245 */           for (int i = 0; i < this.dispatchers.length; i++)
/*     */           {
/* 247 */             int size = this.dispatchers[i].size();
/*     */             
/* 249 */             if (size == 0)
/*     */             {
/* 251 */               min_index = i;
/*     */               
/* 253 */               break;
/*     */             }
/*     */             
/* 256 */             if (size < min_size)
/*     */             {
/* 258 */               min_size = size;
/* 259 */               min_index = i;
/*     */             }
/*     */           }
/*     */           
/* 263 */           dispatcher = this.dispatchers[min_index];
/*     */           
/* 265 */           this.torrent_dispatcher_map.put(torrent, dispatcher);
/*     */         }
/*     */         
/* 268 */         dispatcher.setLastRequestTime(now);
/*     */       }
/*     */     }
/*     */     
/* 272 */     dispatcher.queue(request);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void getSpaceAllowance(DiskAccessRequestImpl request)
/*     */   {
/*     */     int mb_diff;
/*     */     
/* 281 */     synchronized (this.torrent_dispatcher_map)
/*     */     {
/* 283 */       int old_mb = (int)(this.request_bytes_queued / 1048576L);
/*     */       
/* 285 */       this.request_bytes_queued += request.getSize();
/*     */       
/* 287 */       int new_mb = (int)(this.request_bytes_queued / 1048576L);
/*     */       
/* 289 */       mb_diff = new_mb - old_mb;
/*     */       
/* 291 */       if (mb_diff > this.max_mb_queued)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 296 */         this.max_mb_sem.releaseGroup(mb_diff - this.max_mb_queued);
/*     */         
/* 298 */         this.max_mb_queued = mb_diff;
/*     */       }
/*     */       
/* 301 */       this.requests_queued += 1L;
/*     */       
/* 303 */       if (this.requests_queued >= this.next_request_num_log)
/*     */       {
/*     */ 
/*     */ 
/* 307 */         this.next_request_num_log += 100;
/*     */       }
/*     */       
/* 310 */       if (this.request_bytes_queued >= this.next_request_byte_log)
/*     */       {
/*     */ 
/*     */ 
/* 314 */         this.next_request_byte_log += 1048576L;
/*     */       }
/*     */     }
/*     */     
/* 318 */     if (mb_diff > 0)
/*     */     {
/* 320 */       this.max_mb_sem.reserveGroup(mb_diff);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void releaseSpaceAllowance(DiskAccessRequestImpl request)
/*     */   {
/*     */     int mb_diff;
/*     */     
/* 330 */     synchronized (this.torrent_dispatcher_map)
/*     */     {
/* 332 */       int old_mb = (int)(this.request_bytes_queued / 1048576L);
/*     */       
/* 334 */       this.request_bytes_queued -= request.getSize();
/*     */       
/* 336 */       int new_mb = (int)(this.request_bytes_queued / 1048576L);
/*     */       
/* 338 */       mb_diff = old_mb - new_mb;
/*     */       
/* 340 */       this.requests_queued -= 1L;
/*     */     }
/*     */     
/* 343 */     if (mb_diff > 0)
/*     */     {
/* 345 */       this.max_mb_sem.releaseGroup(mb_diff);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 352 */     return this.name + ",agg=" + this.enable_aggregation + ",max_t=" + this.max_threads + ",max_mb=" + this.max_mb_queued + ",q_byte=" + DisplayFormatters.formatByteCountToKiBEtc(this.request_bytes_queued) + ",q_req=" + this.requests_queued + ",t_req=" + this.total_requests + ",t_byte=" + DisplayFormatters.formatByteCountToKiBEtc(this.total_bytes) + ",io=" + this.io_count;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected class requestDispatcher
/*     */   {
/*     */     private final int index;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 368 */     final AEThread2[] threads = new AEThread2[DiskAccessControllerInstance.this.invert_threads ? DiskAccessControllerInstance.this.max_threads : 1];
/*     */     
/*     */     private int active_threads;
/* 371 */     final LinkedList requests = new LinkedList();
/*     */     
/* 373 */     final Map request_map = new HashMap();
/*     */     
/*     */     private long last_request_map_tidy;
/* 376 */     final AESemaphore request_sem = new AESemaphore("DiskAccessControllerInstance:requestDispatcher:request");
/* 377 */     final AESemaphore schedule_sem = new AESemaphore("DiskAccessControllerInstance:requestDispatcher:schedule", 1);
/*     */     
/*     */ 
/*     */     private long last_request_time;
/*     */     
/*     */ 
/*     */ 
/*     */     protected requestDispatcher(int _index)
/*     */     {
/* 386 */       this.index = _index;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void queue(DiskAccessRequestImpl request)
/*     */     {
/* 393 */       if (DiskAccessControllerInstance.tls.get() != null)
/*     */       {
/*     */ 
/*     */ 
/* 397 */         synchronized (this.requests)
/*     */         {
/*     */ 
/*     */ 
/* 401 */           DiskAccessControllerInstance.access$008(DiskAccessControllerInstance.this);
/*     */           
/* 403 */           DiskAccessControllerInstance.access$108(DiskAccessControllerInstance.this);
/*     */           
/* 405 */           DiskAccessControllerInstance.access$214(DiskAccessControllerInstance.this, request.getSize());
/*     */           
/* 407 */           DiskAccessControllerInstance.access$314(DiskAccessControllerInstance.this, request.getSize());
/*     */         }
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 413 */           request.runRequest();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 424 */           DiskAccessControllerInstance.access$408(DiskAccessControllerInstance.this);
/*     */           
/* 426 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 431 */         DiskAccessControllerInstance.this.getSpaceAllowance(request);
/*     */         
/* 433 */         synchronized (this.requests)
/*     */         {
/* 435 */           DiskAccessControllerInstance.access$008(DiskAccessControllerInstance.this);
/*     */           
/* 437 */           DiskAccessControllerInstance.access$214(DiskAccessControllerInstance.this, request.getSize());
/*     */           
/* 439 */           boolean added = false;
/*     */           
/* 441 */           int priority = request.getPriority();
/*     */           int pos;
/* 443 */           Iterator it; if (priority >= 0)
/*     */           {
/* 445 */             pos = 0;
/*     */             
/* 447 */             for (it = this.requests.iterator(); it.hasNext();)
/*     */             {
/* 449 */               DiskAccessRequestImpl r = (DiskAccessRequestImpl)it.next();
/*     */               
/* 451 */               if (r.getPriority() < priority)
/*     */               {
/* 453 */                 this.requests.add(pos, request);
/*     */                 
/* 455 */                 added = true;
/*     */                 
/* 457 */                 break;
/*     */               }
/*     */               
/* 460 */               pos++;
/*     */             }
/*     */           }
/*     */           
/* 464 */           if (!added)
/*     */           {
/* 466 */             this.requests.add(request);
/*     */           }
/*     */           
/* 469 */           if (DiskAccessControllerInstance.this.enable_aggregation)
/*     */           {
/* 471 */             Map m = (Map)this.request_map.get(request.getFile());
/*     */             
/* 473 */             if (m == null)
/*     */             {
/* 475 */               m = new HashMap();
/*     */               
/* 477 */               this.request_map.put(request.getFile(), m);
/*     */             }
/*     */             
/* 480 */             m.put(new Long(request.getOffset()), request);
/*     */             
/* 482 */             long now = SystemTime.getCurrentTime();
/*     */             
/* 484 */             if ((now < this.last_request_map_tidy) || (now - this.last_request_map_tidy > 30000L))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 489 */               this.last_request_map_tidy = now;
/*     */               
/* 491 */               Iterator it = this.request_map.entrySet().iterator();
/*     */               
/* 493 */               while (it.hasNext())
/*     */               {
/* 495 */                 Map.Entry entry = (Map.Entry)it.next();
/*     */                 
/* 497 */                 if (((HashMap)entry.getValue()).size() == 0)
/*     */                 {
/* 499 */                   if (!((CacheFile)entry.getKey()).isOpen())
/*     */                   {
/* 501 */                     it.remove();
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 510 */           this.request_sem.release();
/*     */           
/* 512 */           requestQueued();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getLastRequestTime()
/*     */     {
/* 520 */       return this.last_request_time;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setLastRequestTime(long l)
/*     */     {
/* 527 */       this.last_request_time = l;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int size()
/*     */     {
/* 533 */       return this.requests.size();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void requestQueued()
/*     */     {
/* 541 */       if ((this.active_threads < this.threads.length) && ((this.active_threads == 0) || (this.requests.size() > 32)))
/*     */       {
/* 543 */         for (int i = 0; i < this.threads.length; i++)
/*     */         {
/* 545 */           if (this.threads[i] == null)
/*     */           {
/* 547 */             this.active_threads += 1;
/*     */             
/* 549 */             final int thread_index = i;
/*     */             
/* 551 */             this.threads[thread_index = new AEThread2("DiskAccessController:dispatch(" + DiskAccessControllerInstance.this.getName() + ")[" + this.index + "/" + thread_index + "]", true)
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 557 */                 DiskAccessControllerInstance.tls.set(this);
/*     */                 
/*     */                 for (;;)
/*     */                 {
/* 561 */                   DiskAccessRequestImpl request = null;
/* 562 */                   List aggregated = null;
/*     */                   try
/*     */                   {
/* 565 */                     if (DiskAccessControllerInstance.this.invert_threads)
/*     */                     {
/* 567 */                       DiskAccessControllerInstance.requestDispatcher.this.schedule_sem.reserve();
/*     */                     }
/*     */                     
/* 570 */                     if (DiskAccessControllerInstance.requestDispatcher.this.request_sem.reserve(30000L))
/*     */                     {
/* 572 */                       synchronized (DiskAccessControllerInstance.requestDispatcher.this.requests)
/*     */                       {
/* 574 */                         request = (DiskAccessRequestImpl)DiskAccessControllerInstance.requestDispatcher.this.requests.remove(0);
/*     */                         
/* 576 */                         if (DiskAccessControllerInstance.this.enable_aggregation)
/*     */                         {
/* 578 */                           CacheFile file = request.getFile();
/*     */                           
/* 580 */                           Map file_map = (Map)DiskAccessControllerInstance.requestDispatcher.this.request_map.get(file);
/*     */                           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 587 */                           if (file_map == null)
/*     */                           {
/* 589 */                             file_map = new HashMap();
/*     */                           }
/*     */                           
/* 592 */                           file_map.remove(new Long(request.getOffset()));
/*     */                           
/* 594 */                           if ((request.getPriority() < 0) && (!request.isCancelled()))
/*     */                           {
/* 596 */                             DiskAccessRequestImpl current = request;
/*     */                             
/* 598 */                             long aggregated_bytes = 0L;
/*     */                             try
/*     */                             {
/*     */                               for (;;)
/*     */                               {
/* 603 */                                 int current_size = current.getSize();
/*     */                                 
/* 605 */                                 long end = current.getOffset() + current_size;
/*     */                                 
/*     */ 
/*     */ 
/* 609 */                                 DiskAccessRequestImpl next = (DiskAccessRequestImpl)file_map.remove(new Long(end));
/*     */                                 
/* 611 */                                 if ((next == null) || (next.isCancelled()) || (!next.canBeAggregatedWith(request))) {
/*     */                                   break;
/*     */                                 }
/*     */                                 
/*     */ 
/*     */ 
/* 617 */                                 DiskAccessControllerInstance.requestDispatcher.this.requests.remove(next);
/*     */                                 
/* 619 */                                 if (!DiskAccessControllerInstance.requestDispatcher.this.request_sem.reserve(30000L))
/*     */                                 {
/*     */ 
/*     */ 
/* 623 */                                   Debug.out("shouldn't happen");
/*     */                                 }
/*     */                                 
/* 626 */                                 if (aggregated == null)
/*     */                                 {
/* 628 */                                   aggregated = new ArrayList(8);
/*     */                                   
/* 630 */                                   aggregated.add(current);
/*     */                                   
/* 632 */                                   aggregated_bytes += current_size;
/*     */                                 }
/*     */                                 
/* 635 */                                 aggregated.add(next);
/*     */                                 
/* 637 */                                 aggregated_bytes += next.getSize();
/*     */                                 
/* 639 */                                 if ((aggregated.size() > DiskAccessControllerInstance.this.aggregation_request_limit) || (aggregated_bytes >= DiskAccessControllerInstance.this.aggregation_byte_limit)) {
/*     */                                   break;
/*     */                                 }
/*     */                                 
/*     */ 
/* 644 */                                 current = next;
/*     */                               }
/*     */                             }
/*     */                             finally
/*     */                             {
/* 649 */                               if (aggregated != null)
/*     */                               {
/* 651 */                                 DiskAccessControllerInstance.access$708(DiskAccessControllerInstance.this);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                               }
/*     */                               else
/*     */                               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 663 */                                 DiskAccessControllerInstance.access$108(DiskAccessControllerInstance.this);
/*     */                               }
/*     */                             }
/*     */                           }
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   finally {
/* 672 */                     if (DiskAccessControllerInstance.this.invert_threads)
/*     */                     {
/* 674 */                       DiskAccessControllerInstance.requestDispatcher.this.schedule_sem.release();
/*     */                     }
/*     */                   }
/*     */                   
/*     */                   try
/*     */                   {
/* 680 */                     long io_start = SystemTime.getHighPrecisionCounter();
/*     */                     
/* 682 */                     if (aggregated != null)
/*     */                     {
/* 684 */                       DiskAccessRequestImpl[] requests = (DiskAccessRequestImpl[])aggregated.toArray(new DiskAccessRequestImpl[aggregated.size()]);
/*     */                       
/*     */                       try
/*     */                       {
/* 688 */                         DiskAccessRequestImpl.runAggregated(request, requests);
/*     */                       } finally { long io_end;
/*     */                         int i;
/*     */                         DiskAccessRequestImpl r;
/* 692 */                         long io_end = SystemTime.getHighPrecisionCounter();
/*     */                         
/* 694 */                         DiskAccessControllerInstance.access$814(DiskAccessControllerInstance.this, io_end - io_start);
/*     */                         
/* 696 */                         DiskAccessControllerInstance.access$408(DiskAccessControllerInstance.this);
/*     */                         
/* 698 */                         for (int i = 0; i < requests.length; i++)
/*     */                         {
/* 700 */                           DiskAccessRequestImpl r = requests[i];
/*     */                           
/* 702 */                           DiskAccessControllerInstance.access$914(DiskAccessControllerInstance.this, r.getSize());
/*     */                           
/* 704 */                           DiskAccessControllerInstance.this.releaseSpaceAllowance(r);
/*     */                         }
/*     */                       }
/* 707 */                     } else if (request != null)
/*     */                     {
/*     */                       try {
/* 710 */                         request.runRequest();
/*     */                       }
/*     */                       finally {
/*     */                         long io_end;
/* 714 */                         long io_end = SystemTime.getHighPrecisionCounter();
/*     */                         
/* 716 */                         DiskAccessControllerInstance.access$814(DiskAccessControllerInstance.this, io_end - io_start);
/*     */                         
/* 718 */                         DiskAccessControllerInstance.access$408(DiskAccessControllerInstance.this);
/*     */                         
/* 720 */                         DiskAccessControllerInstance.access$314(DiskAccessControllerInstance.this, request.getSize());
/*     */                         
/* 722 */                         DiskAccessControllerInstance.this.releaseSpaceAllowance(request);
/*     */                       }
/*     */                     }
/*     */                     else
/*     */                     {
/* 727 */                       synchronized (DiskAccessControllerInstance.requestDispatcher.this.requests)
/*     */                       {
/* 729 */                         if (DiskAccessControllerInstance.requestDispatcher.this.requests.size() == 0)
/*     */                         {
/* 731 */                           DiskAccessControllerInstance.requestDispatcher.this.threads[thread_index] = null;
/*     */                           
/* 733 */                           DiskAccessControllerInstance.requestDispatcher.access$1010(DiskAccessControllerInstance.requestDispatcher.this);
/*     */                           
/* 735 */                           break;
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {
/* 741 */                     Debug.printStackTrace(e);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */               }
/* 747 */             };
/* 748 */             this.threads[thread_index].start();
/*     */             
/* 750 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class groupSemaphore
/*     */   {
/*     */     private int value;
/*     */     
/* 762 */     private final List waiters = new LinkedList();
/*     */     
/*     */ 
/*     */     private long blocks;
/*     */     
/*     */ 
/*     */     protected groupSemaphore(int _value)
/*     */     {
/* 770 */       this.value = _value;
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getBlockCount()
/*     */     {
/* 776 */       return this.blocks;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void reserveGroup(int num)
/*     */     {
/*     */       mutableInteger wait;
/*     */       
/* 785 */       synchronized (this)
/*     */       {
/*     */ 
/*     */ 
/* 789 */         if ((num <= this.value) && (this.waiters.size() == 0))
/*     */         {
/* 791 */           this.value -= num;
/*     */           
/* 793 */           return;
/*     */         }
/*     */         
/*     */ 
/* 797 */         this.blocks += 1L;
/*     */         
/* 799 */         wait = new mutableInteger(num - this.value);
/*     */         
/* 801 */         this.value = 0;
/*     */         
/* 803 */         this.waiters.add(wait);
/*     */       }
/*     */       
/*     */ 
/* 807 */       wait.reserve();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void releaseGroup(int num)
/*     */     {
/* 814 */       synchronized (this)
/*     */       {
/* 816 */         if (this.waiters.size() == 0)
/*     */         {
/*     */ 
/*     */ 
/* 820 */           this.value += num;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 826 */           while (this.waiters.size() > 0)
/*     */           {
/* 828 */             mutableInteger wait = (mutableInteger)this.waiters.get(0);
/*     */             
/* 830 */             int wait_num = wait.getValue();
/*     */             
/* 832 */             if (wait_num <= num)
/*     */             {
/*     */ 
/*     */ 
/* 836 */               wait.release();
/*     */               
/* 838 */               this.waiters.remove(0);
/*     */               
/* 840 */               num -= wait_num;
/*     */             }
/*     */             else
/*     */             {
/* 844 */               wait.setValue(wait_num - num);
/*     */               
/* 846 */               num = 0;
/*     */               
/* 848 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 854 */           this.value = num;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected static class mutableInteger
/*     */     {
/*     */       private int i;
/*     */       
/*     */       private boolean released;
/*     */       
/*     */ 
/*     */       protected mutableInteger(int _i)
/*     */       {
/* 869 */         this.i = _i;
/*     */       }
/*     */       
/*     */ 
/*     */       protected int getValue()
/*     */       {
/* 875 */         return this.i;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       protected void setValue(int _i)
/*     */       {
/* 882 */         this.i = _i;
/*     */       }
/*     */       
/*     */ 
/*     */       protected void release()
/*     */       {
/* 888 */         synchronized (this)
/*     */         {
/* 890 */           this.released = true;
/*     */           
/* 892 */           notify();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       protected void reserve()
/*     */       {
/* 899 */         synchronized (this)
/*     */         {
/* 901 */           if (this.released)
/*     */           {
/* 903 */             return;
/*     */           }
/*     */           try
/*     */           {
/* 907 */             int spurious_count = 0;
/*     */             
/*     */             for (;;)
/*     */             {
/* 911 */               wait();
/*     */               
/* 913 */               if (this.released) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 919 */               spurious_count++;
/*     */               
/* 921 */               if (spurious_count > 1024)
/*     */               {
/* 923 */                 Debug.out("DAC::mutableInteger: spurious wakeup limit exceeded");
/*     */                 
/* 925 */                 throw new RuntimeException("die die die");
/*     */               }
/*     */               
/*     */ 
/* 929 */               Debug.out("DAC::mutableInteger: spurious wakeup, ignoring");
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           catch (InterruptedException e)
/*     */           {
/* 936 */             throw new RuntimeException("Semaphore: operation interrupted");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 947 */     groupSemaphore sem = new groupSemaphore(9);
/*     */     
/* 949 */     for (int i = 0; i < 10; i++)
/*     */     {
/* 951 */       new Thread()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 956 */           int count = 0;
/*     */           
/*     */           for (;;)
/*     */           {
/* 960 */             int group = RandomUtils.generateRandomIntUpto(10);
/*     */             
/* 962 */             System.out.println(Thread.currentThread().getName() + " reserving " + group);
/*     */             
/* 964 */             this.val$sem.reserveGroup(group);
/*     */             try
/*     */             {
/* 967 */               Thread.sleep(5 + RandomUtils.generateRandomIntUpto(5));
/*     */             }
/*     */             catch (Throwable e) {}
/*     */             
/*     */ 
/* 972 */             this.val$sem.releaseGroup(group);
/*     */             
/* 974 */             count++;
/*     */             
/* 976 */             if (count % 100 == 0)
/*     */             {
/* 978 */               System.out.println(Thread.currentThread().getName() + ": " + count + " ops");
/*     */             }
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/impl/DiskAccessControllerInstance.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */