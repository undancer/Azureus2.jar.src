/*     */ package com.aelitis.azureus.core.diskmanager.cache.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerStats;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Average;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheFileManagerStatsImpl
/*     */   implements CacheFileManagerStats
/*     */ {
/*     */   protected final CacheFileManagerImpl manager;
/*  40 */   protected final Average cache_read_average = Average.getInstance(1000, 10);
/*  41 */   protected final Average cache_write_average = Average.getInstance(1000, 10);
/*  42 */   protected final Average file_read_average = Average.getInstance(1000, 10);
/*     */   
/*     */ 
/*     */ 
/*  46 */   protected final Average file_write_average = Average.getInstance(1000, 5);
/*     */   
/*     */   protected long last_cache_read;
/*     */   
/*     */   protected long last_cache_write;
/*     */   protected long last_file_read;
/*     */   protected long last_file_write;
/*  53 */   protected final AEMonitor this_mon = new AEMonitor("CacheFileManagerStats");
/*     */   
/*     */ 
/*     */ 
/*     */   protected CacheFileManagerStatsImpl(CacheFileManagerImpl _manager)
/*     */   {
/*  59 */     this.manager = _manager;
/*     */   }
/*     */   
/*     */   protected void update()
/*     */   {
/*     */     try
/*     */     {
/*  66 */       this.this_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*  70 */       long cache_read = this.manager.getBytesReadFromCache();
/*  71 */       long cache_read_diff = cache_read - this.last_cache_read;
/*     */       
/*  73 */       this.last_cache_read = cache_read;
/*     */       
/*  75 */       this.cache_read_average.addValue(cache_read_diff);
/*     */       
/*     */ 
/*     */ 
/*  79 */       long cache_write = this.manager.getBytesWrittenToCache();
/*  80 */       long cache_write_diff = cache_write - this.last_cache_write;
/*     */       
/*  82 */       this.last_cache_write = cache_write;
/*     */       
/*  84 */       this.cache_write_average.addValue(cache_write_diff);
/*     */       
/*     */ 
/*     */ 
/*  88 */       long file_read = this.manager.getBytesReadFromFile();
/*  89 */       long file_read_diff = file_read - this.last_file_read;
/*     */       
/*  91 */       this.last_file_read = file_read;
/*     */       
/*  93 */       this.file_read_average.addValue(file_read_diff);
/*     */       
/*     */ 
/*     */ 
/*  97 */       long file_write = this.manager.getBytesWrittenToFile();
/*  98 */       long file_write_diff = file_write - this.last_file_write;
/*     */       
/* 100 */       this.last_file_write = file_write;
/*     */       
/* 102 */       this.file_write_average.addValue(file_write_diff);
/*     */     }
/*     */     finally
/*     */     {
/* 106 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSize()
/*     */   {
/* 113 */     return this.manager.getCacheSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUsedSize()
/*     */   {
/* 119 */     return this.manager.getCacheUsed();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesWrittenToCache()
/*     */   {
/* 125 */     return this.manager.getBytesWrittenToCache();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesWrittenToFile()
/*     */   {
/* 131 */     return this.manager.getBytesWrittenToFile();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReadFromCache()
/*     */   {
/* 137 */     return this.manager.getBytesReadFromCache();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesReadFromFile()
/*     */   {
/* 143 */     return this.manager.getBytesReadFromFile();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesWrittenToCache()
/*     */   {
/* 149 */     return this.cache_write_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesWrittenToFile()
/*     */   {
/* 155 */     return this.file_write_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesReadFromCache()
/*     */   {
/* 161 */     return this.cache_read_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesReadFromFile()
/*     */   {
/* 167 */     return this.file_read_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCacheReadCount()
/*     */   {
/* 173 */     return this.manager.getCacheReadCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCacheWriteCount()
/*     */   {
/* 179 */     return this.manager.getCacheWriteCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFileReadCount()
/*     */   {
/* 185 */     return this.manager.getFileReadCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFileWriteCount()
/*     */   {
/* 191 */     return this.manager.getFileWriteCount();
/*     */   }
/*     */   
/*     */   public boolean[] getBytesInCache(TOTorrent torrent, long[] absoluteOffsets, long[] lengths)
/*     */   {
/* 196 */     return this.manager.getBytesInCache(torrent, absoluteOffsets, lengths);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/impl/CacheFileManagerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */