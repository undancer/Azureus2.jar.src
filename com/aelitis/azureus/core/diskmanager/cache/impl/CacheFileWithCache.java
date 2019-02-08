/*      */ package com.aelitis.azureus.core.diskmanager.cache.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Average;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CacheFileWithCache
/*      */   implements CacheFile
/*      */ {
/*      */   private static final byte SS_CACHE = 3;
/*   49 */   private static final LogIDs LOGID = LogIDs.CACHE;
/*      */   
/*   51 */   protected static final Comparator comparator = new Comparator()
/*      */   {
/*      */ 
/*      */ 
/*      */     public int compare(Object _o1, Object _o2)
/*      */     {
/*      */ 
/*      */ 
/*   59 */       if (_o1 == _o2)
/*      */       {
/*   61 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*   65 */       CacheEntry o1 = (CacheEntry)_o1;
/*   66 */       CacheEntry o2 = (CacheEntry)_o2;
/*      */       
/*   68 */       long offset1 = o1.getFilePosition();
/*   69 */       int length1 = o1.getLength();
/*      */       
/*   71 */       long offset2 = o2.getFilePosition();
/*   72 */       int length2 = o2.getLength();
/*      */       
/*   74 */       if ((offset1 + length1 > offset2) && (offset2 + length2 > offset1) && (length1 != 0) && (length2 != 0))
/*      */       {
/*      */ 
/*   77 */         Debug.out("Overlapping cache entries - " + o1.getString() + "/" + o2.getString());
/*      */       }
/*      */       
/*   80 */       return offset1 - offset2 < 0L ? -1 : 1;
/*      */     }
/*      */   };
/*      */   
/*   84 */   protected static boolean TRACE = false;
/*      */   protected static final boolean TRACE_CACHE_CONTENTS = false;
/*      */   protected static final int READAHEAD_LOW_LIMIT = 65536;
/*      */   protected static final int READAHEAD_HIGH_LIMIT = 262144;
/*      */   
/*   89 */   static { TRACE = COConfigurationManager.getBooleanParameter("diskmanager.perf.cache.trace");
/*      */     
/*   91 */     if (TRACE)
/*      */     {
/*   93 */       System.out.println("**** Disk Cache tracing enabled ****");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static final int READAHEAD_HISTORY = 32;
/*      */   
/*      */   protected final CacheFileManagerImpl manager;
/*      */   
/*      */   protected final FMFile file;
/*      */   
/*  104 */   protected int access_mode = 1;
/*      */   
/*      */   protected TOTorrentFile torrent_file;
/*      */   protected TOTorrent torrent;
/*      */   protected long file_offset_in_torrent;
/*      */   protected long[] read_history;
/*  110 */   protected int read_history_next = 0;
/*      */   
/*  112 */   protected final TreeSet cache = new TreeSet(comparator);
/*      */   
/*  114 */   protected int current_read_ahead_size = 0;
/*      */   
/*      */   protected static final int READ_AHEAD_STATS_WAIT_TICKS = 10;
/*      */   
/*  118 */   protected int read_ahead_stats_wait = 10;
/*      */   
/*  120 */   protected final Average read_ahead_made_average = Average.getInstance(1000, 5);
/*  121 */   protected final Average read_ahead_used_average = Average.getInstance(1000, 5);
/*      */   
/*      */   protected long read_ahead_bytes_made;
/*      */   
/*      */   protected long last_read_ahead_bytes_made;
/*      */   protected long read_ahead_bytes_used;
/*      */   protected long last_read_ahead_bytes_used;
/*  128 */   protected int piece_size = 0;
/*  129 */   protected int piece_offset = 0;
/*      */   
/*  131 */   protected final AEMonitor this_mon = new AEMonitor("CacheFile");
/*      */   
/*      */ 
/*      */   protected volatile CacheFileManagerException pending_exception;
/*      */   
/*      */ 
/*      */   private long bytes_written;
/*      */   
/*      */   private long bytes_read;
/*      */   
/*      */ 
/*      */   protected CacheFileWithCache(CacheFileManagerImpl _manager, FMFile _file, TOTorrentFile _torrent_file)
/*      */   {
/*  144 */     this.manager = _manager;
/*  145 */     this.file = _file;
/*      */     
/*  147 */     if (_torrent_file != null)
/*      */     {
/*  149 */       this.torrent_file = _torrent_file;
/*      */       
/*  151 */       this.torrent = this.torrent_file.getTorrent();
/*      */       
/*  153 */       this.piece_size = ((int)this.torrent.getPieceLength());
/*      */       
/*  155 */       for (int i = 0; i < this.torrent.getFiles().length; i++)
/*      */       {
/*  157 */         TOTorrentFile f = this.torrent.getFiles()[i];
/*      */         
/*  159 */         if (f == this.torrent_file) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*  164 */         this.file_offset_in_torrent += f.getLength();
/*      */       }
/*      */       
/*  167 */       this.piece_offset = (this.piece_size - (int)(this.file_offset_in_torrent % this.piece_size));
/*      */       
/*  169 */       if (this.piece_offset == this.piece_size)
/*      */       {
/*  171 */         this.piece_offset = 0;
/*      */       }
/*      */       
/*  174 */       this.current_read_ahead_size = Math.min(65536, this.piece_size);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TOTorrentFile getTorrentFile()
/*      */   {
/*  181 */     return this.torrent_file;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateStats()
/*      */   {
/*  187 */     long made = this.read_ahead_bytes_made;
/*  188 */     long used = this.read_ahead_bytes_used;
/*      */     
/*  190 */     long made_diff = made - this.last_read_ahead_bytes_made;
/*  191 */     long used_diff = used - this.last_read_ahead_bytes_used;
/*      */     
/*  193 */     this.read_ahead_made_average.addValue(made_diff);
/*  194 */     this.read_ahead_used_average.addValue(used_diff);
/*      */     
/*  196 */     this.last_read_ahead_bytes_made = made;
/*  197 */     this.last_read_ahead_bytes_used = used;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  202 */     if (--this.read_ahead_stats_wait == 0)
/*      */     {
/*  204 */       this.read_ahead_stats_wait = 10;
/*      */       
/*      */ 
/*      */ 
/*  208 */       double made_average = this.read_ahead_made_average.getAverage();
/*  209 */       double used_average = this.read_ahead_used_average.getAverage();
/*      */       
/*      */ 
/*      */ 
/*  213 */       double ratio = used_average * 100.0D / made_average;
/*      */       
/*  215 */       if (ratio > 0.75D)
/*      */       {
/*  217 */         this.current_read_ahead_size += 16384;
/*      */         
/*      */ 
/*      */ 
/*  221 */         this.current_read_ahead_size = Math.min(this.current_read_ahead_size, this.piece_size);
/*      */         
/*      */ 
/*      */ 
/*  225 */         this.current_read_ahead_size = Math.min(this.current_read_ahead_size, 262144);
/*      */         
/*      */ 
/*      */ 
/*  229 */         this.current_read_ahead_size = Math.min(this.current_read_ahead_size, (int)(this.manager.getCacheSize() / 16L));
/*      */       }
/*  231 */       else if (ratio < 0.5D)
/*      */       {
/*  233 */         this.current_read_ahead_size -= 16384;
/*      */         
/*      */ 
/*      */ 
/*  237 */         this.current_read_ahead_size = Math.max(this.current_read_ahead_size, 65536);
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
/*      */ 
/*      */   protected void readCache(DirectByteBuffer file_buffer, long file_position, boolean recursive, boolean disable_read_cache)
/*      */     throws CacheFileManagerException
/*      */   {
/*  253 */     checkPendingException();
/*      */     
/*  255 */     int file_buffer_position = file_buffer.position((byte)3);
/*  256 */     int file_buffer_limit = file_buffer.limit((byte)3);
/*      */     
/*  258 */     int read_length = file_buffer_limit - file_buffer_position;
/*      */     try
/*      */     {
/*  261 */       if (this.manager.isCacheEnabled())
/*      */       {
/*  263 */         if (TRACE) {
/*  264 */           Logger.log(new LogEvent(this.torrent, LOGID, "readCache: " + getName() + ", " + file_position + " - " + (file_position + read_length - 1L) + ":" + file_buffer_position + "/" + file_buffer_limit));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  269 */         if (read_length == 0)
/*      */         {
/*  271 */           return;
/*      */         }
/*      */         
/*  274 */         long writing_file_position = file_position;
/*  275 */         int writing_left = read_length;
/*      */         
/*  277 */         boolean ok = true;
/*  278 */         int used_entries = 0;
/*  279 */         long used_read_ahead = 0L;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  288 */           this.this_mon.enter();
/*      */           
/*      */ 
/*  291 */           if (this.read_history == null)
/*      */           {
/*  293 */             this.read_history = new long[32];
/*  294 */             Arrays.fill(this.read_history, -1L);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  299 */           this.read_history[(this.read_history_next++)] = (file_position + read_length);
/*      */           
/*  301 */           if (this.read_history_next == 32)
/*      */           {
/*  303 */             this.read_history_next = 0;
/*      */           }
/*      */           
/*  306 */           Iterator it = this.cache.iterator();
/*      */           
/*  308 */           while ((ok) && (writing_left > 0) && (it.hasNext()))
/*      */           {
/*  310 */             CacheEntry entry = (CacheEntry)it.next();
/*      */             
/*  312 */             long entry_file_position = entry.getFilePosition();
/*  313 */             int entry_length = entry.getLength();
/*      */             
/*  315 */             if (entry_file_position > writing_file_position)
/*      */             {
/*      */ 
/*      */ 
/*  319 */               ok = false;
/*      */               
/*  321 */               break;
/*      */             }
/*  323 */             if (entry_file_position + entry_length > writing_file_position)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  330 */               int skip = (int)(writing_file_position - entry_file_position);
/*      */               
/*  332 */               int available = entry_length - skip;
/*      */               
/*  334 */               if (available > writing_left)
/*      */               {
/*  336 */                 available = writing_left;
/*      */               }
/*      */               
/*  339 */               DirectByteBuffer entry_buffer = entry.getBuffer();
/*      */               
/*  341 */               int entry_buffer_position = entry_buffer.position((byte)3);
/*  342 */               int entry_buffer_limit = entry_buffer.limit((byte)3);
/*      */               
/*      */               try
/*      */               {
/*  346 */                 entry_buffer.limit((byte)3, entry_buffer_position + skip + available);
/*      */                 
/*  348 */                 entry_buffer.position((byte)3, entry_buffer_position + skip);
/*      */                 
/*  350 */                 if (TRACE) {
/*  351 */                   Logger.log(new LogEvent(this.torrent, LOGID, "cacheRead: using " + entry.getString() + "[" + entry_buffer.position((byte)3) + "/" + entry_buffer.limit((byte)3) + "]" + "to write to [" + file_buffer.position((byte)3) + "/" + file_buffer.limit((byte)3) + "]"));
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  358 */                 used_entries++;
/*      */                 
/*  360 */                 file_buffer.put((byte)3, entry_buffer);
/*      */                 
/*  362 */                 this.manager.cacheEntryUsed(entry);
/*      */               }
/*      */               finally
/*      */               {
/*  366 */                 entry_buffer.limit((byte)3, entry_buffer_limit);
/*      */                 
/*  368 */                 entry_buffer.position((byte)3, entry_buffer_position);
/*      */               }
/*      */               
/*  371 */               writing_file_position += available;
/*  372 */               writing_left -= available;
/*      */               
/*  374 */               if (entry.getType() == 1)
/*      */               {
/*  376 */                 used_read_ahead += available;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  383 */           if (ok)
/*      */           {
/*  385 */             this.read_ahead_bytes_used += used_read_ahead;
/*      */           }
/*      */           
/*  388 */           this.this_mon.exit();
/*      */         }
/*      */         
/*  391 */         if ((ok) && (writing_left == 0))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  396 */           if (!recursive)
/*      */           {
/*  398 */             this.manager.cacheBytesRead(read_length);
/*      */             
/*  400 */             this.bytes_read += read_length;
/*      */           }
/*      */           
/*  403 */           if (TRACE) {
/*  404 */             Logger.log(new LogEvent(this.torrent, LOGID, "cacheRead: cache use ok [entries = " + used_entries + "]"));
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  409 */           if (TRACE) {
/*  410 */             Logger.log(new LogEvent(this.torrent, LOGID, "cacheRead: cache use fails, reverting to plain read"));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  415 */           file_buffer.position((byte)3, file_buffer_position);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  422 */           for (int i = 0; i < 2; i++) {
/*      */             try
/*      */             {
/*  425 */               boolean do_read_ahead = (i == 0) && (!recursive) && (!disable_read_cache) && (this.read_history != null) && (this.manager.isReadCacheEnabled()) && (read_length < this.current_read_ahead_size) && (file_position + this.current_read_ahead_size <= this.file.getLength());
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  434 */               if (do_read_ahead)
/*      */               {
/*      */ 
/*      */ 
/*  438 */                 do_read_ahead = false;
/*      */                 
/*  440 */                 for (int j = 0; j < 32; j++)
/*      */                 {
/*  442 */                   if (this.read_history[j] == file_position)
/*      */                   {
/*  444 */                     do_read_ahead = true;
/*      */                     
/*  446 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  451 */               int actual_read_ahead = this.current_read_ahead_size;
/*      */               
/*  453 */               if (do_read_ahead)
/*      */               {
/*      */ 
/*      */ 
/*  457 */                 int request_piece_offset = (int)((file_position - this.piece_offset) % this.piece_size);
/*      */                 
/*  459 */                 if (request_piece_offset < 0)
/*      */                 {
/*  461 */                   request_piece_offset += this.piece_size;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  466 */                 int data_left = this.piece_size - request_piece_offset;
/*      */                 
/*  468 */                 if (data_left < actual_read_ahead)
/*      */                 {
/*  470 */                   actual_read_ahead = data_left;
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  475 */                   if (actual_read_ahead <= read_length)
/*      */                   {
/*  477 */                     do_read_ahead = false;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  483 */               if (do_read_ahead)
/*      */               {
/*  485 */                 if (TRACE) {
/*  486 */                   Logger.log(new LogEvent(this.torrent, LOGID, "\tperforming read-ahead"));
/*      */                 }
/*      */                 
/*  489 */                 DirectByteBuffer cache_buffer = DirectByteBufferPool.getBuffer((byte)5, actual_read_ahead);
/*      */                 
/*      */ 
/*  492 */                 boolean buffer_cached = false;
/*      */                 
/*      */ 
/*      */ 
/*      */                 try
/*      */                 {
/*  498 */                   CacheEntry entry = this.manager.allocateCacheSpace(1, this, cache_buffer, file_position, actual_read_ahead);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  504 */                   entry.setClean();
/*      */                   
/*      */                   try
/*      */                   {
/*  508 */                     this.this_mon.enter();
/*      */                     
/*      */ 
/*      */ 
/*  512 */                     flushCache(file_position, actual_read_ahead, true, -1L, 0L, -1L);
/*      */                     
/*  514 */                     getFMFile().read(cache_buffer, file_position);
/*      */                     
/*  516 */                     this.read_ahead_bytes_made += actual_read_ahead;
/*      */                     
/*  518 */                     this.manager.fileBytesRead(actual_read_ahead);
/*      */                     
/*  520 */                     this.bytes_read += actual_read_ahead;
/*      */                     
/*  522 */                     cache_buffer.position((byte)3, 0);
/*      */                     
/*  524 */                     this.cache.add(entry);
/*      */                     
/*  526 */                     this.manager.addCacheSpace(entry);
/*      */                   }
/*      */                   finally
/*      */                   {
/*  530 */                     this.this_mon.exit();
/*      */                   }
/*      */                   
/*  533 */                   buffer_cached = true;
/*      */                 }
/*      */                 finally
/*      */                 {
/*  537 */                   if (!buffer_cached)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*  542 */                     cache_buffer.returnToPool();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  550 */                 readCache(file_buffer, file_position, true, disable_read_cache);
/*      */               }
/*      */               else
/*      */               {
/*  554 */                 if (TRACE) {
/*  555 */                   Logger.log(new LogEvent(this.torrent, LOGID, "\tnot performing read-ahead"));
/*      */                 }
/*      */                 
/*      */                 try
/*      */                 {
/*  560 */                   this.this_mon.enter();
/*      */                   
/*  562 */                   flushCache(file_position, read_length, true, -1L, 0L, -1L);
/*      */                   
/*  564 */                   getFMFile().read(file_buffer, file_position);
/*      */                 }
/*      */                 finally
/*      */                 {
/*  568 */                   this.this_mon.exit();
/*      */                 }
/*      */                 
/*  571 */                 this.manager.fileBytesRead(read_length);
/*      */                 
/*  573 */                 this.bytes_read += read_length;
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*      */             catch (CacheFileManagerException e)
/*      */             {
/*  580 */               if (i == 1)
/*      */               {
/*  582 */                 throw e;
/*      */               }
/*      */             }
/*      */             catch (FMFileManagerException e)
/*      */             {
/*  587 */               if (i == 1)
/*      */               {
/*  589 */                 this.manager.rethrow(this, e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*      */         try {
/*  597 */           getFMFile().read(file_buffer, file_position);
/*      */           
/*  599 */           this.manager.fileBytesRead(read_length);
/*      */           
/*  601 */           this.bytes_read += read_length;
/*      */         }
/*      */         catch (FMFileManagerException e)
/*      */         {
/*  605 */           this.manager.rethrow(this, e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {}
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeCache(DirectByteBuffer file_buffer, long file_position, boolean buffer_handed_over)
/*      */     throws CacheFileManagerException
/*      */   {
/*  644 */     checkPendingException();
/*      */     
/*  646 */     boolean buffer_cached = false;
/*  647 */     boolean failed = false;
/*      */     try
/*      */     {
/*  650 */       int file_buffer_position = file_buffer.position((byte)3);
/*  651 */       int file_buffer_limit = file_buffer.limit((byte)3);
/*      */       
/*  653 */       int write_length = file_buffer_limit - file_buffer_position;
/*      */       
/*  655 */       if (write_length == 0) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  682 */       if (this.manager.isWriteCacheEnabled())
/*      */       {
/*  684 */         if (TRACE) {
/*  685 */           Logger.log(new LogEvent(this.torrent, LOGID, "writeCache: " + getName() + ", " + file_position + " - " + (file_position + write_length - 1L) + ":" + file_buffer_position + "/" + file_buffer_limit));
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
/*  696 */         if ((!buffer_handed_over) && (write_length < this.piece_size))
/*      */         {
/*      */ 
/*  699 */           if (TRACE) {
/*  700 */             Logger.log(new LogEvent(this.torrent, LOGID, "    making copy of non-handedover buffer"));
/*      */           }
/*      */           
/*  703 */           DirectByteBuffer cache_buffer = DirectByteBufferPool.getBuffer((byte)10, write_length);
/*      */           
/*  705 */           cache_buffer.put((byte)3, file_buffer);
/*      */           
/*  707 */           cache_buffer.position((byte)3, 0);
/*      */           
/*      */ 
/*      */ 
/*  711 */           file_buffer = cache_buffer;
/*      */           
/*  713 */           file_buffer_position = 0;
/*  714 */           file_buffer_limit = write_length;
/*      */           
/*  716 */           buffer_handed_over = true;
/*      */         }
/*      */         
/*  719 */         if (buffer_handed_over)
/*      */         {
/*      */ 
/*      */ 
/*  723 */           CacheEntry entry = this.manager.allocateCacheSpace(0, this, file_buffer, file_position, write_length);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  732 */             this.this_mon.enter();
/*      */             
/*  734 */             if (this.access_mode != 2)
/*      */             {
/*  736 */               throw new CacheFileManagerException(this, "Write failed - cache file is read only");
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  746 */             flushCache(file_position, write_length, true, -1L, 0L, -1L);
/*      */             
/*  748 */             this.cache.add(entry);
/*      */             
/*  750 */             this.manager.addCacheSpace(entry);
/*      */           }
/*      */           finally
/*      */           {
/*  754 */             this.this_mon.exit();
/*      */           }
/*      */           
/*  757 */           this.manager.cacheBytesWritten(write_length);
/*      */           
/*  759 */           this.bytes_written += write_length;
/*      */           
/*  761 */           buffer_cached = true;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*  770 */             this.this_mon.enter();
/*      */             
/*  772 */             flushCache(file_position, write_length, true, -1L, 0L, -1L);
/*      */             
/*  774 */             getFMFile().write(file_buffer, file_position);
/*      */           }
/*      */           finally
/*      */           {
/*  778 */             this.this_mon.exit();
/*      */           }
/*      */           
/*  781 */           this.manager.fileBytesWritten(write_length);
/*      */           
/*  783 */           this.bytes_written += write_length;
/*      */         }
/*      */       }
/*      */       else {
/*  787 */         getFMFile().write(file_buffer, file_position);
/*      */         
/*  789 */         this.manager.fileBytesWritten(write_length);
/*      */         
/*  791 */         this.bytes_written += write_length;
/*      */       }
/*      */     }
/*      */     catch (CacheFileManagerException e)
/*      */     {
/*  796 */       failed = true;
/*      */       
/*  798 */       throw e;
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  802 */       failed = true;
/*      */       
/*  804 */       this.manager.rethrow(this, e);
/*      */     }
/*      */     finally
/*      */     {
/*  808 */       if (buffer_handed_over)
/*      */       {
/*  810 */         if ((!failed) && (!buffer_cached))
/*      */         {
/*  812 */           file_buffer.returnToPool();
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
/*      */ 
/*      */   protected void flushCache(long file_position, long length, boolean release_entries, long minimum_to_release, long oldest_dirty_time, long min_chunk_size)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  831 */       flushCacheSupport(file_position, length, release_entries, minimum_to_release, oldest_dirty_time, min_chunk_size);
/*      */     }
/*      */     catch (CacheFileManagerException e)
/*      */     {
/*  835 */       if (!release_entries)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  840 */         flushCacheSupport(0L, -1L, true, -1L, 0L, -1L);
/*      */       }
/*      */       
/*  843 */       throw e;
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
/*      */ 
/*      */   protected void flushCacheSupport(long file_position, long length, boolean release_entries, long minimum_to_release, long oldest_dirty_time, long min_chunk_size)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  860 */       this.this_mon.enter();
/*      */       
/*  862 */       if (this.cache.size() == 0) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  867 */       Iterator it = this.cache.iterator();
/*      */       
/*  869 */       Throwable last_failure = null;
/*      */       
/*  871 */       long entry_total_released = 0L;
/*      */       
/*  873 */       List multi_block_entries = new ArrayList();
/*  874 */       long multi_block_start = -1L;
/*  875 */       long multi_block_next = -1L;
/*      */       for (;;) {
/*  877 */         if (it.hasNext())
/*      */         {
/*  879 */           CacheEntry entry = (CacheEntry)it.next();
/*      */           
/*  881 */           long entry_file_position = entry.getFilePosition();
/*  882 */           int entry_length = entry.getLength();
/*      */           
/*  884 */           if (entry_file_position + entry_length <= file_position) {
/*      */             continue;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  890 */           if ((length == -1L) || (file_position + length > entry_file_position))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  902 */             boolean dirty = entry.isDirty();
/*      */             
/*      */             try
/*      */             {
/*  906 */               if ((dirty) && ((oldest_dirty_time == 0L) || (entry.getLastUsed() < oldest_dirty_time)))
/*      */               {
/*      */ 
/*      */ 
/*  910 */                 if (multi_block_start == -1L)
/*      */                 {
/*      */ 
/*      */ 
/*  914 */                   multi_block_start = entry_file_position;
/*      */                   
/*  916 */                   multi_block_next = entry_file_position + entry_length;
/*      */                   
/*  918 */                   multi_block_entries.add(entry);
/*      */                 }
/*  920 */                 else if (multi_block_next == entry_file_position)
/*      */                 {
/*      */ 
/*      */ 
/*  924 */                   multi_block_next = entry_file_position + entry_length;
/*      */                   
/*  926 */                   multi_block_entries.add(entry);
/*      */ 
/*      */ 
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/*      */ 
/*      */ 
/*  935 */                   boolean skip_chunk = false;
/*      */                   
/*  937 */                   if (min_chunk_size != -1L)
/*      */                   {
/*  939 */                     if (release_entries)
/*      */                     {
/*  941 */                       Debug.out("CacheFile: can't use min chunk with release option");
/*      */                     }
/*      */                     else {
/*  944 */                       skip_chunk = multi_block_next - multi_block_start < min_chunk_size;
/*      */                     }
/*      */                   }
/*      */                   
/*  948 */                   List f_multi_block_entries = multi_block_entries;
/*  949 */                   long f_multi_block_start = multi_block_start;
/*  950 */                   long f_multi_block_next = multi_block_next;
/*      */                   
/*  952 */                   multi_block_start = entry_file_position;
/*      */                   
/*  954 */                   multi_block_next = entry_file_position + entry_length;
/*      */                   
/*  956 */                   multi_block_entries = new ArrayList();
/*      */                   
/*  958 */                   multi_block_entries.add(entry);
/*      */                   
/*  960 */                   if (skip_chunk) {
/*  961 */                     if (TRACE) {
/*  962 */                       Logger.log(new LogEvent(this.torrent, LOGID, "flushCache: skipping " + multi_block_entries.size() + " entries, [" + multi_block_start + "," + multi_block_next + "] as too small"));
/*      */                     }
/*      */                     
/*      */ 
/*      */                   }
/*      */                   else {
/*  968 */                     multiBlockFlush(f_multi_block_entries, f_multi_block_start, f_multi_block_next, release_entries);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  982 */               if (release_entries)
/*      */               {
/*  984 */                 it.remove();
/*      */                 
/*      */ 
/*      */ 
/*  988 */                 if (!dirty)
/*      */                 {
/*  990 */                   this.manager.releaseCacheSpace(entry);
/*      */                 }
/*      */                 
/*  993 */                 entry_total_released += entry.getLength();
/*      */                 
/*  995 */                 if ((minimum_to_release != -1L) && (entry_total_released > minimum_to_release)) {}
/*      */               }
/*      */               else
/*      */               {
/*      */                 continue;
/*      */               }
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  978 */               last_failure = e;
/*      */               
/*      */ 
/*      */ 
/*  982 */               if (release_entries)
/*      */               {
/*  984 */                 it.remove();
/*      */                 
/*      */ 
/*      */ 
/*  988 */                 if (!dirty)
/*      */                 {
/*  990 */                   this.manager.releaseCacheSpace(entry);
/*      */                 }
/*      */                 
/*  993 */                 entry_total_released += entry.getLength();
/*      */                 
/*  995 */                 if ((minimum_to_release == -1L) || (entry_total_released <= minimum_to_release)) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/*  982 */               if (release_entries)
/*      */               {
/*  984 */                 it.remove();
/*      */                 
/*      */ 
/*      */ 
/*  988 */                 if (!dirty)
/*      */                 {
/*  990 */                   this.manager.releaseCacheSpace(entry);
/*      */                 }
/*      */                 
/*  993 */                 entry_total_released += entry.getLength();
/*      */                 
/*  995 */                 if ((minimum_to_release != -1L) && (entry_total_released > minimum_to_release)) {
/*      */                   break label586;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       label586:
/* 1005 */       if (multi_block_start != -1L)
/*      */       {
/* 1007 */         boolean skip_chunk = false;
/*      */         
/* 1009 */         if (min_chunk_size != -1L)
/*      */         {
/* 1011 */           if (release_entries)
/*      */           {
/* 1013 */             Debug.out("CacheFile: can't use min chunk with release option");
/*      */           }
/*      */           else {
/* 1016 */             skip_chunk = multi_block_next - multi_block_start < min_chunk_size;
/*      */           }
/*      */         }
/*      */         
/* 1020 */         if (skip_chunk)
/*      */         {
/* 1022 */           if (TRACE) {
/* 1023 */             Logger.log(new LogEvent(this.torrent, LOGID, "flushCache: skipping " + multi_block_entries.size() + " entries, [" + multi_block_start + "," + multi_block_next + "] as too small"));
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1031 */           multiBlockFlush(multi_block_entries, multi_block_start, multi_block_next, release_entries);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1039 */       if (last_failure != null)
/*      */       {
/* 1041 */         if ((last_failure instanceof CacheFileManagerException))
/*      */         {
/* 1043 */           throw ((CacheFileManagerException)last_failure);
/*      */         }
/*      */         
/* 1046 */         throw new CacheFileManagerException(this, "cache flush failed", last_failure);
/*      */       }
/*      */     }
/*      */     finally {
/* 1050 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void multiBlockFlush(List multi_block_entries, long multi_block_start, long multi_block_next, boolean release_entries)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1063 */     boolean write_ok = false;
/*      */     try
/*      */     {
/* 1066 */       if (TRACE) {
/* 1067 */         Logger.log(new LogEvent(this.torrent, LOGID, "multiBlockFlush: writing " + multi_block_entries.size() + " entries, [" + multi_block_start + "," + multi_block_next + "," + release_entries + "]"));
/*      */       }
/*      */       
/*      */ 
/* 1071 */       DirectByteBuffer[] buffers = new DirectByteBuffer[multi_block_entries.size()];
/*      */       
/* 1073 */       long expected_per_entry_write = 0L;
/*      */       
/* 1075 */       for (int i = 0; i < buffers.length; i++)
/*      */       {
/* 1077 */         CacheEntry entry = (CacheEntry)multi_block_entries.get(i);
/*      */         
/*      */ 
/*      */ 
/* 1081 */         DirectByteBuffer buffer = entry.getBuffer();
/*      */         
/* 1083 */         if (buffer.limit((byte)3) - buffer.position((byte)3) != entry.getLength())
/*      */         {
/* 1085 */           throw new CacheFileManagerException(this, "flush: inconsistent entry length, position wrong");
/*      */         }
/*      */         
/* 1088 */         expected_per_entry_write += entry.getLength();
/*      */         
/* 1090 */         buffers[i] = buffer;
/*      */       }
/*      */       
/* 1093 */       long expected_overall_write = multi_block_next - multi_block_start;
/*      */       
/* 1095 */       if (expected_per_entry_write != expected_overall_write)
/*      */       {
/* 1097 */         throw new CacheFileManagerException(this, "flush: inconsistent write length, entrys = " + expected_per_entry_write + " overall = " + expected_overall_write);
/*      */       }
/*      */       
/*      */ 
/* 1101 */       getFMFile().write(buffers, multi_block_start);
/*      */       
/* 1103 */       this.manager.fileBytesWritten(expected_overall_write);
/*      */       
/*      */ 
/*      */ 
/* 1107 */       write_ok = true;
/*      */     } catch (FMFileManagerException e) {
/*      */       int i;
/*      */       CacheEntry entry;
/* 1111 */       throw new CacheFileManagerException(this, "flush fails", e);
/*      */     }
/*      */     finally
/*      */     {
/* 1115 */       for (int i = 0; i < multi_block_entries.size(); i++)
/*      */       {
/* 1117 */         CacheEntry entry = (CacheEntry)multi_block_entries.get(i);
/*      */         
/* 1119 */         if (release_entries)
/*      */         {
/* 1121 */           this.manager.releaseCacheSpace(entry);
/*      */         }
/*      */         else
/*      */         {
/* 1125 */           entry.resetBufferPosition();
/*      */           
/* 1127 */           if (write_ok)
/*      */           {
/* 1129 */             entry.setClean();
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
/*      */   protected void flushCache(long file_start_position, boolean release_entries, long minumum_to_release)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1144 */     if (this.manager.isCacheEnabled())
/*      */     {
/* 1146 */       if (TRACE) {
/* 1147 */         Logger.log(new LogEvent(this.torrent, LOGID, "flushCache: " + getName() + ", rel = " + release_entries + ", min = " + minumum_to_release));
/*      */       }
/*      */       
/* 1150 */       flushCache(file_start_position, -1L, release_entries, minumum_to_release, 0L, -1L);
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
/*      */ 
/*      */ 
/*      */   protected void flushCachePublic(boolean release_entries, long minumum_to_release)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1166 */     checkPendingException();
/*      */     
/* 1168 */     flushCache(0L, release_entries, minumum_to_release);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void flushOldDirtyData(long oldest_dirty_time, long min_chunk_size)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1178 */     if (this.manager.isCacheEnabled())
/*      */     {
/* 1180 */       if (TRACE) {
/* 1181 */         Logger.log(new LogEvent(this.torrent, LOGID, "flushOldDirtyData: " + getName()));
/*      */       }
/*      */       
/* 1184 */       flushCache(0L, -1L, false, -1L, oldest_dirty_time, min_chunk_size);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void flushOldDirtyData(long oldest_dirty_time)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1194 */     flushOldDirtyData(oldest_dirty_time, -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void getBytesInCache(boolean[] toModify, long[] absoluteOffsets, long[] lengths)
/*      */   {
/* 1204 */     long baseOffset = this.file_offset_in_torrent;
/*      */     
/* 1206 */     int i = 0;
/*      */     
/* 1208 */     long first = absoluteOffsets[0];
/* 1209 */     long last = absoluteOffsets[(absoluteOffsets.length - 1)] + lengths[(lengths.length - 1)];
/*      */     
/*      */ 
/* 1212 */     long lastEnd = Math.max(absoluteOffsets[0], baseOffset);
/* 1213 */     while (absoluteOffsets[i] + lengths[i] < baseOffset) {
/* 1214 */       i++;
/*      */     }
/* 1216 */     boolean doSkipping = true;
/*      */     
/* 1218 */     if (!this.this_mon.enter(250))
/*      */     {
/* 1220 */       Debug.outNoStack("Failed to lock stats, abandoning");
/*      */       
/* 1222 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1226 */       Iterator it = this.cache.subSet(new CacheEntry(first - 1L - baseOffset), new CacheEntry(last - baseOffset)).iterator();
/*      */       
/*      */ 
/* 1229 */       while (it.hasNext())
/*      */       {
/* 1231 */         CacheEntry entry = (CacheEntry)it.next();
/* 1232 */         long startPos = entry.getFilePosition() + baseOffset;
/* 1233 */         long endPos = startPos + entry.getLength();
/*      */         
/* 1235 */         if (startPos >= first)
/*      */         {
/*      */ 
/*      */ 
/* 1239 */           if (doSkipping) {
/* 1240 */             while ((i < absoluteOffsets.length) && (absoluteOffsets[i] < startPos))
/*      */             {
/* 1242 */               toModify[i] = false;
/* 1243 */               i++;
/*      */             }
/*      */           }
/* 1246 */           if (i >= absoluteOffsets.length) {
/*      */             break;
/*      */           }
/* 1249 */           doSkipping = false;
/*      */           
/* 1251 */           if ((startPos >= absoluteOffsets[i]) && (endPos >= absoluteOffsets[i] + lengths[i]))
/*      */           {
/* 1253 */             i++;
/* 1254 */             doSkipping = true;
/* 1255 */           } else if (startPos >= lastEnd)
/*      */           {
/* 1257 */             doSkipping = true;
/* 1258 */           } else if (startPos >= absoluteOffsets[i] + lengths[i])
/*      */           {
/* 1260 */             i++;
/* 1261 */             doSkipping = true;
/*      */           }
/*      */           
/* 1264 */           if (endPos > last) {
/*      */             break;
/*      */           }
/* 1267 */           lastEnd = endPos;
/*      */         }
/*      */       }
/*      */     } finally {
/* 1271 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1274 */     if (doSkipping) {
/* 1275 */       while (i < absoluteOffsets.length)
/*      */       {
/* 1277 */         if ((absoluteOffsets[i] + lengths[i] < baseOffset) || (absoluteOffsets[i] > baseOffset + this.torrent_file.getLength()))
/*      */         {
/* 1279 */           i++;
/*      */         }
/*      */         else {
/* 1282 */           toModify[i] = false;
/* 1283 */           i++;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkPendingException()
/*      */     throws CacheFileManagerException
/*      */   {
/* 1295 */     if (this.pending_exception != null)
/*      */     {
/* 1297 */       throw this.pending_exception;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setPendingException(CacheFileManagerException e)
/*      */   {
/* 1305 */     this.pending_exception = e;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getName()
/*      */   {
/* 1311 */     return this.file.getName();
/*      */   }
/*      */   
/*      */ 
/*      */   protected FMFile getFMFile()
/*      */   {
/* 1317 */     return this.file;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean exists()
/*      */   {
/* 1325 */     return this.file.exists();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void moveFile(File new_file)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1335 */       flushCachePublic(true, -1L);
/*      */       
/* 1337 */       this.file.moveFile(new_file);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1341 */       this.manager.rethrow(this, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void renameFile(String new_name)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1352 */       flushCachePublic(true, -1L);
/*      */       
/* 1354 */       this.file.renameFile(new_name);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1358 */       this.manager.rethrow(this, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAccessMode(int mode)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1369 */       this.this_mon.enter();
/*      */       
/* 1371 */       if (this.access_mode != mode)
/*      */       {
/* 1373 */         flushCachePublic(false, -1L);
/*      */       }
/*      */       
/* 1376 */       this.file.setAccessMode(mode == 1 ? 1 : 2);
/*      */       
/* 1378 */       this.access_mode = mode;
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1382 */       this.manager.rethrow(this, e);
/*      */     }
/*      */     finally
/*      */     {
/* 1386 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAccessMode()
/*      */   {
/* 1393 */     return this.access_mode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setStorageType(int type)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1403 */       this.this_mon.enter();
/*      */       
/* 1405 */       if (getStorageType() != type)
/*      */       {
/* 1407 */         flushCachePublic(false, -1L);
/*      */       }
/*      */       
/* 1410 */       this.file.setStorageType(CacheFileManagerImpl.convertCacheToFileType(type));
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1414 */       this.manager.rethrow(this, e);
/*      */     }
/*      */     finally
/*      */     {
/* 1418 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getStorageType()
/*      */   {
/* 1425 */     return CacheFileManagerImpl.convertFileToCacheType(this.file.getStorageType());
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
/*      */   public long getLength()
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1443 */       if (this.manager.isCacheEnabled()) {
/*      */         try
/*      */         {
/* 1446 */           this.this_mon.enter();
/*      */           
/* 1448 */           long physical_size = this.file.exists() ? this.file.getLength() : 0L;
/*      */           
/* 1450 */           Iterator it = this.cache.iterator();
/*      */           
/*      */           CacheEntry entry;
/*      */           
/* 1454 */           while (it.hasNext())
/*      */           {
/* 1456 */             entry = (CacheEntry)it.next();
/*      */             
/* 1458 */             if (!it.hasNext())
/*      */             {
/* 1460 */               long entry_file_position = entry.getFilePosition();
/* 1461 */               int entry_length = entry.getLength();
/*      */               
/* 1463 */               long logical_size = entry_file_position + entry_length;
/*      */               
/* 1465 */               if (logical_size > physical_size)
/*      */               {
/* 1467 */                 physical_size = logical_size;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1472 */           return physical_size;
/*      */         }
/*      */         finally
/*      */         {
/* 1476 */           this.this_mon.exit();
/*      */         }
/*      */       }
/*      */       
/* 1480 */       return this.file.exists() ? this.file.getLength() : 0L;
/*      */ 
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1485 */       this.manager.rethrow(this, e);
/*      */     }
/* 1487 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long compareLength(long compare_to)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1501 */       long physical_length = this.file.exists() ? this.file.getLength() : 0L;
/*      */       
/* 1503 */       long res = physical_length - compare_to;
/*      */       
/* 1505 */       if (res >= 0L)
/*      */       {
/* 1507 */         return res;
/*      */       }
/*      */       
/* 1510 */       return getLength() - compare_to;
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1514 */       this.manager.rethrow(this, e);
/*      */     }
/* 1516 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLength(long length)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1530 */       flushCachePublic(true, -1L);
/*      */       
/* 1532 */       this.file.setLength(length);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1536 */       this.manager.rethrow(this, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPieceComplete(int piece_number, DirectByteBuffer piece_data)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1548 */       this.file.setPieceComplete(piece_number, piece_data);
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1552 */       this.manager.rethrow(this, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void read(DirectByteBuffer[] buffers, long position, short policy)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1564 */     for (int i = 0; i < buffers.length; i++)
/*      */     {
/* 1566 */       DirectByteBuffer buffer = buffers[i];
/*      */       
/* 1568 */       int len = buffer.remaining((byte)3);
/*      */       try
/*      */       {
/* 1571 */         read(buffer, position, policy);
/*      */         
/* 1573 */         position += len;
/*      */       }
/*      */       catch (CacheFileManagerException e)
/*      */       {
/* 1577 */         throw new CacheFileManagerException(this, e.getMessage(), e, i);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void read(DirectByteBuffer buffer, long position, short policy)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1590 */     boolean read_cache = (policy & 0x1) != 0;
/* 1591 */     boolean flush = (policy & 0x2) != 0;
/*      */     
/* 1593 */     if (flush)
/*      */     {
/* 1595 */       int file_buffer_position = buffer.position((byte)3);
/* 1596 */       int file_buffer_limit = buffer.limit((byte)3);
/*      */       
/* 1598 */       int read_length = file_buffer_limit - file_buffer_position;
/*      */       
/* 1600 */       flushCache(position, read_length, false, -1L, 0L, -1L);
/*      */     }
/*      */     
/* 1603 */     readCache(buffer, position, false, !read_cache);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(DirectByteBuffer buffer, long position)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1613 */     writeCache(buffer, position, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void write(DirectByteBuffer[] buffers, long position)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1623 */     for (int i = 0; i < buffers.length; i++)
/*      */     {
/* 1625 */       DirectByteBuffer buffer = buffers[i];
/*      */       
/* 1627 */       int len = buffer.remaining((byte)3);
/*      */       try
/*      */       {
/* 1630 */         write(buffer, position);
/*      */         
/* 1632 */         position += len;
/*      */       }
/*      */       catch (CacheFileManagerException e)
/*      */       {
/* 1636 */         throw new CacheFileManagerException(this, e.getMessage(), e, i);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeAndHandoverBuffer(DirectByteBuffer buffer, long position)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1648 */     writeCache(buffer, position, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeAndHandoverBuffers(DirectByteBuffer[] buffers, long position)
/*      */     throws CacheFileManagerException
/*      */   {
/* 1658 */     for (int i = 0; i < buffers.length; i++)
/*      */     {
/* 1660 */       DirectByteBuffer buffer = buffers[i];
/*      */       
/* 1662 */       int len = buffer.remaining((byte)3);
/*      */       try
/*      */       {
/* 1665 */         writeAndHandoverBuffer(buffer, position);
/*      */         
/* 1667 */         position += len;
/*      */       }
/*      */       catch (CacheFileManagerException e)
/*      */       {
/* 1671 */         throw new CacheFileManagerException(this, e.getMessage(), e, i);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void flushCache()
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1682 */       flushCachePublic(false, -1L);
/*      */       
/* 1684 */       this.file.flush();
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1688 */       this.manager.rethrow(this, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clearCache()
/*      */     throws CacheFileManagerException
/*      */   {
/* 1697 */     flushCachePublic(true, -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void close()
/*      */     throws CacheFileManagerException
/*      */   {
/* 1707 */     boolean fm_file_closed = false;
/*      */     try
/*      */     {
/* 1710 */       flushCachePublic(true, -1L);
/*      */       
/* 1712 */       this.file.close();
/*      */       
/* 1714 */       fm_file_closed = true;
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1718 */       this.manager.rethrow(this, e);
/*      */     }
/*      */     finally
/*      */     {
/* 1722 */       if (!fm_file_closed) {
/*      */         try
/*      */         {
/* 1725 */           this.file.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1735 */       this.manager.closeFile(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOpen()
/*      */   {
/* 1742 */     return this.file.isOpen();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSessionBytesRead()
/*      */   {
/* 1748 */     return this.bytes_read;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSessionBytesWritten()
/*      */   {
/* 1754 */     return this.bytes_written;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void delete()
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/* 1764 */       this.file.delete();
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/* 1768 */       this.manager.rethrow(this, e);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/impl/CacheFileWithCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */