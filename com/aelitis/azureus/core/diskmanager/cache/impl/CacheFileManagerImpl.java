/*      */ package com.aelitis.azureus.core.diskmanager.cache.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManager;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerStats;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileOwner;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFile;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManager;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerException;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileManagerFactory;
/*      */ import com.aelitis.azureus.core.diskmanager.file.FMFileOwner;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.WeakHashMap;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ 
/*      */ 
/*      */ public class CacheFileManagerImpl
/*      */   implements CacheFileManager, AEDiagnosticsEvidenceGenerator
/*      */ {
/*   46 */   private static final LogIDs LOGID = LogIDs.CACHE;
/*      */   
/*      */   public static final boolean DEBUG = false;
/*      */   
/*      */   public static final int CACHE_CLEANER_TICKS = 60;
/*      */   
/*      */   public static final int STATS_UPDATE_FREQUENCY = 1000;
/*      */   
/*      */   public static final long DIRTY_CACHE_WRITE_MAX_AGE = 120000L;
/*      */   protected boolean cache_enabled;
/*      */   protected boolean cache_read_enabled;
/*      */   protected boolean cache_write_enabled;
/*      */   protected long cache_size;
/*      */   protected long cache_files_not_smaller_than;
/*      */   protected long cache_minimum_free_size;
/*      */   protected long cache_space_free;
/*      */   
/*      */   protected static int convertCacheToFileType(int cache_type)
/*      */   {
/*   65 */     if (cache_type == 1)
/*      */     {
/*   67 */       return 1;
/*      */     }
/*   69 */     if (cache_type == 2)
/*      */     {
/*   71 */       return 2;
/*      */     }
/*   73 */     if (cache_type == 3)
/*      */     {
/*   75 */       return 3;
/*      */     }
/*      */     
/*      */ 
/*   79 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static int convertFileToCacheType(int file_type)
/*      */   {
/*   87 */     if (file_type == 1)
/*      */     {
/*   89 */       return 1;
/*      */     }
/*   91 */     if (file_type == 2)
/*      */     {
/*   93 */       return 2;
/*      */     }
/*   95 */     if (file_type == 3)
/*      */     {
/*   97 */       return 3;
/*      */     }
/*      */     
/*      */ 
/*  101 */     return 4;
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
/*  115 */   private long cache_file_id_next = 0L;
/*      */   
/*      */ 
/*      */   protected final FMFileManager file_manager;
/*      */   
/*      */ 
/*  121 */   protected WeakHashMap cache_files = new WeakHashMap();
/*  122 */   protected WeakHashMap updated_cache_files = null;
/*      */   
/*      */ 
/*      */ 
/*  126 */   protected final LinkedHashMap cache_entries = new LinkedHashMap(1024, 0.75F, true);
/*      */   
/*      */ 
/*      */   protected CacheFileManagerStatsImpl stats;
/*      */   
/*  131 */   protected final Map torrent_to_cache_file_map = new LightHashMap();
/*      */   
/*      */   protected long cache_bytes_written;
/*      */   
/*      */   protected long cache_bytes_read;
/*      */   
/*      */   protected long file_bytes_written;
/*      */   protected long file_bytes_read;
/*      */   protected long cache_read_count;
/*      */   protected long cache_write_count;
/*      */   protected long file_read_count;
/*      */   protected long file_write_count;
/*  143 */   protected final AEMonitor this_mon = new AEMonitor("CacheFileManager");
/*      */   
/*  145 */   private long cleaner_ticks = 60L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public CacheFileManagerImpl()
/*      */   {
/*  152 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  154 */     this.file_manager = FMFileManagerFactory.getSingleton();
/*      */     
/*  156 */     boolean enabled = COConfigurationManager.getBooleanParameter("diskmanager.perf.cache.enable");
/*      */     
/*  158 */     boolean enable_read = COConfigurationManager.getBooleanParameter("diskmanager.perf.cache.enable.read");
/*      */     
/*  160 */     boolean enable_write = COConfigurationManager.getBooleanParameter("diskmanager.perf.cache.enable.write");
/*      */     
/*      */ 
/*      */ 
/*  164 */     long size = 1048576L * COConfigurationManager.getIntParameter("diskmanager.perf.cache.size");
/*      */     
/*      */ 
/*      */ 
/*  168 */     int not_smaller_than = 1024 * COConfigurationManager.getIntParameter("notsmallerthan");
/*      */     
/*  170 */     if (size <= 0L)
/*      */     {
/*  172 */       Debug.out("Invalid cache size parameter (" + size + "), caching disabled");
/*      */       
/*  174 */       enabled = false;
/*      */     }
/*      */     
/*  177 */     initialise(enabled, enable_read, enable_write, size, not_smaller_than);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void initialise(boolean enabled, boolean enable_read, boolean enable_write, long size, long not_smaller_than)
/*      */   {
/*  188 */     this.cache_enabled = ((enabled) && ((enable_read) || (enable_write)));
/*      */     
/*  190 */     this.cache_read_enabled = ((enabled) && (enable_read));
/*      */     
/*  192 */     this.cache_write_enabled = ((enabled) && (enable_write));
/*      */     
/*  194 */     this.cache_size = size;
/*      */     
/*  196 */     this.cache_files_not_smaller_than = not_smaller_than;
/*      */     
/*  198 */     this.cache_minimum_free_size = (this.cache_size / 4L);
/*      */     
/*  200 */     this.cache_space_free = this.cache_size;
/*      */     
/*  202 */     this.stats = new CacheFileManagerStatsImpl(this);
/*      */     
/*      */ 
/*  205 */     cacheStatsAndCleaner();
/*      */     
/*      */ 
/*  208 */     if (Logger.isEnabled()) {
/*  209 */       Logger.log(new LogEvent(LOGID, "DiskCache: enabled = " + this.cache_enabled + ", read = " + this.cache_read_enabled + ", write = " + this.cache_write_enabled + ", size = " + this.cache_size + " B"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isWriteCacheEnabled()
/*      */   {
/*  217 */     return this.cache_write_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isReadCacheEnabled()
/*      */   {
/*  223 */     return this.cache_read_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public CacheFile createFile(final CacheFileOwner owner, File file, int type)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     final long my_id;
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  238 */       this.this_mon.enter();
/*      */       
/*  240 */       my_id = this.cache_file_id_next++;
/*      */     }
/*      */     finally
/*      */     {
/*  244 */       this.this_mon.exit();
/*      */     }
/*      */     
/*  247 */     int fm_type = convertCacheToFileType(type);
/*      */     try
/*      */     {
/*  250 */       FMFile fm_file = this.file_manager.createFile(new FMFileOwner()
/*      */       {
/*      */ 
/*      */ 
/*      */         public String getName()
/*      */         {
/*      */ 
/*  257 */           return owner.getCacheFileOwnerName() + "[" + my_id + "]";
/*      */         }
/*      */         
/*      */         public TOTorrentFile getTorrentFile()
/*      */         {
/*  262 */           return owner.getCacheFileTorrentFile();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  267 */         public File getControlFileDir() { return owner.getCacheFileControlFileDir(); } }, file, fm_type);
/*      */       
/*      */ 
/*      */ 
/*  271 */       TOTorrentFile tf = owner.getCacheFileTorrentFile();
/*      */       
/*      */ 
/*      */ 
/*  275 */       int cache_mode = owner.getCacheMode();
/*      */       CacheFile cf;
/*  277 */       CacheFile cf; if (cache_mode == 3)
/*      */       {
/*  279 */         cf = new CacheFileWithoutCacheMT(this, fm_file, tf);
/*      */       } else { CacheFile cf;
/*  281 */         if (((tf != null) && (tf.getLength() < this.cache_files_not_smaller_than)) || (!this.cache_enabled) || (cache_mode == 2))
/*      */         {
/*  283 */           cf = new CacheFileWithoutCache(this, fm_file, tf);
/*      */         }
/*      */         else
/*      */         {
/*  287 */           cf = new CacheFileWithCache(this, fm_file, tf);
/*      */           try
/*      */           {
/*  290 */             this.this_mon.enter();
/*      */             
/*  292 */             if (this.updated_cache_files == null)
/*      */             {
/*  294 */               this.updated_cache_files = new WeakHashMap(this.cache_files);
/*      */             }
/*      */             
/*      */ 
/*  298 */             this.updated_cache_files.put(cf, null);
/*      */             
/*  300 */             if (tf != null)
/*      */             {
/*  302 */               this.torrent_to_cache_file_map.put(tf, cf);
/*      */             }
/*      */           }
/*      */           finally {
/*  306 */             this.this_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*  310 */       return cf;
/*      */     }
/*      */     catch (FMFileManagerException e)
/*      */     {
/*  314 */       rethrow(null, e);
/*      */     }
/*  316 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public CacheFileManagerStats getStats()
/*      */   {
/*  323 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isCacheEnabled()
/*      */   {
/*  329 */     return this.cache_enabled;
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
/*      */   protected CacheEntry allocateCacheSpace(int entry_type, CacheFileWithCache file, DirectByteBuffer buffer, long file_position, int length)
/*      */     throws CacheFileManagerException
/*      */   {
/*  360 */     boolean ok = false;
/*  361 */     boolean log = false;
/*      */     
/*  363 */     while (!ok)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  368 */       CacheEntry oldest_entry = null;
/*      */       try
/*      */       {
/*  371 */         this.this_mon.enter();
/*      */         
/*  373 */         if ((length < this.cache_space_free) || (this.cache_space_free == this.cache_size))
/*      */         {
/*  375 */           ok = true;
/*      */         }
/*      */         else
/*      */         {
/*  379 */           oldest_entry = (CacheEntry)this.cache_entries.keySet().iterator().next();
/*      */         }
/*      */       }
/*      */       finally {
/*  383 */         this.this_mon.exit();
/*      */       }
/*      */       
/*  386 */       if (!ok)
/*      */       {
/*  388 */         log = true;
/*      */         
/*  390 */         long old_free = this.cache_space_free;
/*      */         
/*  392 */         CacheFileWithCache oldest_file = oldest_entry.getFile();
/*      */         
/*      */         try
/*      */         {
/*  396 */           oldest_file.flushCache(oldest_entry.getFilePosition(), true, this.cache_minimum_free_size);
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (CacheFileManagerException e)
/*      */         {
/*      */ 
/*      */ 
/*  404 */           if (oldest_file != file)
/*      */           {
/*  406 */             oldest_file.setPendingException(e);
/*      */           }
/*      */           else
/*      */           {
/*  410 */             throw e;
/*      */           }
/*      */         }
/*      */         
/*  414 */         long flushed = this.cache_space_free - old_free;
/*      */         
/*  416 */         if (Logger.isEnabled()) {
/*  417 */           TOTorrentFile tf = file.getTorrentFile();
/*  418 */           TOTorrent torrent = tf == null ? null : tf.getTorrent();
/*  419 */           Logger.log(new LogEvent(torrent, LOGID, "DiskCache: cache full, flushed " + flushed + " from " + oldest_file.getName()));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  424 */         if (flushed == 0L) {
/*      */           try
/*      */           {
/*  427 */             this.this_mon.enter();
/*      */             
/*  429 */             if ((this.cache_entries.size() > 0) && ((CacheEntry)this.cache_entries.keySet().iterator().next() == oldest_entry))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  435 */               throw new CacheFileManagerException(null, "Cache inconsistent: 0 flushed");
/*      */             }
/*      */           }
/*      */           finally {
/*  439 */             this.this_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  445 */     CacheEntry entry = new CacheEntry(entry_type, file, buffer, file_position, length);
/*      */     
/*  447 */     if ((log) && (Logger.isEnabled())) {
/*  448 */       TOTorrentFile tf = file.getTorrentFile();
/*  449 */       TOTorrent torrent = tf == null ? null : tf.getTorrent();
/*      */       
/*  451 */       Logger.log(new LogEvent(torrent, LOGID, "DiskCache: cr=" + this.cache_bytes_read + ",cw=" + this.cache_bytes_written + ",fr=" + this.file_bytes_read + ",fw=" + this.file_bytes_written));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  456 */     return entry;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void cacheStatsAndCleaner()
/*      */   {
/*  462 */     SimpleTimer.addPeriodicEvent("CacheFile:stats+cleaner", 1000L, new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent ev)
/*      */       {
/*      */ 
/*      */ 
/*  471 */         CacheFileManagerImpl.this.stats.update();
/*      */         
/*      */ 
/*      */ 
/*  475 */         Iterator cf_it = CacheFileManagerImpl.this.cache_files.keySet().iterator();
/*      */         
/*  477 */         while (cf_it.hasNext())
/*      */         {
/*  479 */           ((CacheFileWithCache)cf_it.next()).updateStats();
/*      */         }
/*      */         
/*  482 */         if (CacheFileManagerImpl.access$006(CacheFileManagerImpl.this) == 0L)
/*      */         {
/*  484 */           CacheFileManagerImpl.this.cleaner_ticks = 60L;
/*      */           
/*  486 */           Set dirty_files = new HashSet();
/*      */           
/*  488 */           long oldest = SystemTime.getCurrentTime() - 120000L;
/*      */           try
/*      */           {
/*  491 */             CacheFileManagerImpl.this.this_mon.enter();
/*      */             
/*  493 */             if (CacheFileManagerImpl.this.updated_cache_files != null)
/*      */             {
/*  495 */               CacheFileManagerImpl.this.cache_files = CacheFileManagerImpl.this.updated_cache_files;
/*      */               
/*  497 */               CacheFileManagerImpl.this.updated_cache_files = null;
/*      */             }
/*      */             
/*  500 */             if (CacheFileManagerImpl.this.cache_entries.size() > 0)
/*      */             {
/*  502 */               Iterator it = CacheFileManagerImpl.this.cache_entries.keySet().iterator();
/*      */               
/*  504 */               while (it.hasNext())
/*      */               {
/*  506 */                 CacheEntry entry = (CacheEntry)it.next();
/*      */                 
/*      */ 
/*      */ 
/*  510 */                 if (entry.isDirty())
/*      */                 {
/*  512 */                   dirty_files.add(entry.getFile());
/*      */                 }
/*      */                 
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/*  521 */             CacheFileManagerImpl.this.this_mon.exit();
/*      */           }
/*      */           
/*  524 */           Iterator it = dirty_files.iterator();
/*      */           
/*  526 */           while (it.hasNext())
/*      */           {
/*  528 */             CacheFileWithCache file = (CacheFileWithCache)it.next();
/*      */             
/*      */             try
/*      */             {
/*  532 */               TOTorrentFile tf = file.getTorrentFile();
/*      */               
/*  534 */               long min_flush_size = -1L;
/*      */               
/*  536 */               if (tf != null)
/*      */               {
/*  538 */                 min_flush_size = tf.getTorrent().getPieceLength();
/*      */               }
/*      */               
/*      */ 
/*  542 */               file.flushOldDirtyData(oldest, min_flush_size);
/*      */             }
/*      */             catch (CacheFileManagerException e)
/*      */             {
/*  546 */               file.setPendingException(e);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  551 */               Debug.printStackTrace(e);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  555 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
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
/*      */   protected void addCacheSpace(CacheEntry new_entry)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  576 */       this.this_mon.enter();
/*      */       
/*  578 */       this.cache_space_free -= new_entry.getLength();
/*      */       
/*      */ 
/*      */ 
/*  582 */       this.cache_entries.put(new_entry, new_entry);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  630 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void cacheEntryUsed(CacheEntry entry)
/*      */     throws CacheFileManagerException
/*      */   {
/*      */     try
/*      */     {
/*  641 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  645 */       if (this.cache_entries.get(entry) == null)
/*      */       {
/*  647 */         Debug.out("Cache inconsistency: entry missing on usage");
/*      */         
/*  649 */         throw new CacheFileManagerException(null, "Cache inconsistency: entry missing on usage");
/*      */       }
/*      */       
/*      */ 
/*  653 */       entry.used();
/*      */     }
/*      */     finally
/*      */     {
/*  657 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void releaseCacheSpace(CacheEntry entry)
/*      */     throws CacheFileManagerException
/*      */   {
/*  667 */     entry.getBuffer().returnToPool();
/*      */     try
/*      */     {
/*  670 */       this.this_mon.enter();
/*      */       
/*  672 */       this.cache_space_free += entry.getLength();
/*      */       
/*  674 */       if (this.cache_entries.remove(entry) == null)
/*      */       {
/*  676 */         Debug.out("Cache inconsistency: entry missing on removal");
/*      */         
/*  678 */         throw new CacheFileManagerException(null, "Cache inconsistency: entry missing on removal");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  698 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getCacheSize()
/*      */   {
/*  705 */     return this.cache_size;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getCacheUsed()
/*      */   {
/*  711 */     long free = this.cache_space_free;
/*      */     
/*  713 */     if (free < 0L)
/*      */     {
/*  715 */       free = 0L;
/*      */     }
/*      */     
/*  718 */     return this.cache_size - free;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void cacheBytesWritten(long num)
/*      */   {
/*      */     try
/*      */     {
/*  726 */       this.this_mon.enter();
/*      */       
/*  728 */       this.cache_bytes_written += num;
/*      */       
/*  730 */       this.cache_write_count += 1L;
/*      */     }
/*      */     finally
/*      */     {
/*  734 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void cacheBytesRead(int num)
/*      */   {
/*      */     try
/*      */     {
/*  743 */       this.this_mon.enter();
/*      */       
/*  745 */       this.cache_bytes_read += num;
/*      */       
/*  747 */       this.cache_read_count += 1L;
/*      */     }
/*      */     finally
/*      */     {
/*  751 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void fileBytesWritten(long num)
/*      */   {
/*      */     try
/*      */     {
/*  760 */       this.this_mon.enter();
/*      */       
/*  762 */       this.file_bytes_written += num;
/*      */       
/*  764 */       this.file_write_count += 1L;
/*      */     }
/*      */     finally
/*      */     {
/*  768 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void fileBytesRead(int num)
/*      */   {
/*      */     try
/*      */     {
/*  777 */       this.this_mon.enter();
/*      */       
/*  779 */       this.file_bytes_read += num;
/*      */       
/*  781 */       this.file_read_count += 1L;
/*      */     }
/*      */     finally {
/*  784 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getBytesWrittenToCache()
/*      */   {
/*  791 */     return this.cache_bytes_written;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getBytesWrittenToFile()
/*      */   {
/*  797 */     return this.file_bytes_written;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getBytesReadFromCache()
/*      */   {
/*  803 */     return this.cache_bytes_read;
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getBytesReadFromFile()
/*      */   {
/*  809 */     return this.file_bytes_read;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCacheReadCount()
/*      */   {
/*  815 */     return this.cache_read_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCacheWriteCount()
/*      */   {
/*  821 */     return this.cache_write_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getFileReadCount()
/*      */   {
/*  827 */     return this.file_read_count;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getFileWriteCount()
/*      */   {
/*  833 */     return this.file_write_count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void closeFile(CacheFileWithCache file)
/*      */   {
/*  840 */     TOTorrentFile tf = file.getTorrentFile();
/*      */     
/*  842 */     if ((tf != null) && (this.torrent_to_cache_file_map.get(tf) != null)) {
/*      */       try
/*      */       {
/*  845 */         this.this_mon.enter();
/*      */         
/*  847 */         this.torrent_to_cache_file_map.remove(tf);
/*      */       }
/*      */       finally {
/*  850 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean[] getBytesInCache(TOTorrent torrent, long[] absoluteOffsets, long[] lengths)
/*      */   {
/*  858 */     if (absoluteOffsets.length != lengths.length)
/*  859 */       throw new IllegalArgumentException("Offsets/Lengths mismatch");
/*  860 */     long prevEnding = 0L;
/*  861 */     for (int i = 0; i < lengths.length; i++)
/*      */     {
/*  863 */       if ((absoluteOffsets[i] < prevEnding) || (lengths[i] <= 0L))
/*  864 */         throw new IllegalArgumentException("Offsets/Lengths are not in ascending order");
/*  865 */       prevEnding = absoluteOffsets[i] + lengths[i];
/*      */     }
/*      */     
/*      */ 
/*  869 */     TOTorrentFile[] files = torrent.getFiles();
/*  870 */     long[] fileOffsets = new long[files.length];
/*      */     
/*  872 */     boolean[] results = new boolean[absoluteOffsets.length];
/*  873 */     Arrays.fill(results, true);
/*      */     
/*  875 */     long first = absoluteOffsets[0];
/*  876 */     long last = absoluteOffsets[(absoluteOffsets.length - 1)] + lengths[(lengths.length - 1)];
/*  877 */     long fileOffset = 0L;
/*  878 */     int firstFile = -1;
/*  879 */     boolean lockAcquired = false;
/*      */     
/*  881 */     Map localCacheMap = new LightHashMap();
/*      */     try
/*      */     {
/*  884 */       for (int i = 0; i < files.length; i++)
/*      */       {
/*  886 */         TOTorrentFile tf = files[i];
/*  887 */         long length = tf.getLength();
/*  888 */         fileOffsets[i] = fileOffset;
/*  889 */         if ((firstFile == -1) && (fileOffset <= first) && (first < fileOffset + length))
/*      */         {
/*  891 */           firstFile = i;
/*  892 */           this.this_mon.enter();
/*  893 */           lockAcquired = true;
/*      */         }
/*      */         
/*  896 */         if (fileOffset > last) {
/*      */           break;
/*      */         }
/*  899 */         if (lockAcquired)
/*      */         {
/*  901 */           CacheFileWithCache cache_file = (CacheFileWithCache)this.torrent_to_cache_file_map.get(tf);
/*  902 */           localCacheMap.put(tf, cache_file);
/*      */         }
/*      */         
/*  905 */         fileOffset += length;
/*      */       }
/*      */     } finally {
/*  908 */       if (lockAcquired) {
/*  909 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  914 */     for (int i = firstFile; (-1 < i) && (i < files.length); i++) {
/*  915 */       TOTorrentFile tf = files[i];
/*  916 */       CacheFileWithCache cache_file = (CacheFileWithCache)localCacheMap.get(tf);
/*      */       
/*  918 */       long length = tf.getLength();
/*      */       
/*  920 */       fileOffset = fileOffsets[i];
/*  921 */       if (fileOffset > last) {
/*      */         break;
/*      */       }
/*  924 */       if (cache_file != null) {
/*  925 */         cache_file.getBytesInCache(results, absoluteOffsets, lengths);
/*      */       } else {
/*  927 */         for (int j = 0; j < results.length; j++)
/*  928 */           if (((absoluteOffsets[j] < fileOffset + length) && (absoluteOffsets[j] > fileOffset)) || ((absoluteOffsets[j] + lengths[j] < fileOffset + length) && (absoluteOffsets[j] + lengths[j] > fileOffset)))
/*  929 */             results[j] = false;
/*      */       }
/*      */     }
/*  932 */     if (!lockAcquired) {
/*  933 */       Arrays.fill(results, false);
/*      */     }
/*      */     
/*  936 */     return results;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void rethrow(CacheFile file, FMFileManagerException e)
/*      */     throws CacheFileManagerException
/*      */   {
/*  946 */     Throwable cause = e.getCause();
/*      */     
/*  948 */     if (cause != null)
/*      */     {
/*  950 */       throw new CacheFileManagerException(file, e.getMessage(), cause);
/*      */     }
/*      */     
/*  953 */     throw new CacheFileManagerException(file, e.getMessage(), e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/*  960 */     writer.println("Cache Manager");
/*      */     try
/*      */     {
/*  963 */       writer.indent();
/*      */       
/*      */ 
/*      */       Iterator it;
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  971 */         this.this_mon.enter();
/*      */         
/*  973 */         it = new ArrayList(this.cache_entries.keySet()).iterator();
/*      */       }
/*      */       finally
/*      */       {
/*  977 */         this.this_mon.exit();
/*      */       }
/*      */       
/*  980 */       writer.println("Entries = " + this.cache_entries.size());
/*      */       
/*  982 */       Object files = new HashSet();
/*      */       
/*  984 */       while (it.hasNext())
/*      */       {
/*  986 */         CacheEntry entry = (CacheEntry)it.next();
/*      */         
/*  988 */         CacheFileWithCache file = entry.getFile();
/*      */         
/*  990 */         if (!((Set)files).contains(file))
/*      */         {
/*  992 */           ((Set)files).add(file);
/*      */           
/*  994 */           TOTorrentFile torrentFile = file.getTorrentFile();
/*  995 */           String fileLength = "";
/*      */           try {
/*  997 */             fileLength = "" + file.getLength();
/*      */           } catch (Exception e) {
/*  999 */             if (torrentFile != null)
/* 1000 */               fileLength = "" + torrentFile.getLength();
/*      */           }
/* 1002 */           String hash = "<unknown>";
/*      */           try
/*      */           {
/* 1005 */             if (torrentFile != null) {
/* 1006 */               hash = ByteFormatter.encodeString(torrentFile.getTorrent().getHash());
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/* 1011 */           String name = file.getName();
/*      */           
/* 1013 */           writer.println("File: " + Debug.secretFileName(name) + ", size " + fileLength + ", torrent " + hash + ", access = " + file.getAccessMode());
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1020 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFileLinks(TOTorrent torrent, LinkFileMap links)
/*      */   {
/* 1029 */     this.file_manager.setFileLinks(torrent, links);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/impl/CacheFileManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */