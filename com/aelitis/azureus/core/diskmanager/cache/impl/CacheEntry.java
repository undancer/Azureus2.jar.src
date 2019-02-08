/*     */ package com.aelitis.azureus.core.diskmanager.cache.impl;
/*     */ 
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
/*     */ public class CacheEntry
/*     */ {
/*     */   protected static final int CT_DATA_WRITE = 0;
/*     */   protected static final int CT_READ_AHEAD = 1;
/*     */   protected CacheFileWithCache file;
/*     */   protected DirectByteBuffer buffer;
/*     */   protected final long offset;
/*     */   protected int size;
/*     */   protected int buffer_pos;
/*     */   protected int buffer_limit;
/*     */   protected boolean dirty;
/*     */   protected long last_used;
/*     */   protected int entry_type;
/*     */   protected int usage_count;
/*     */   
/*     */   CacheEntry(long offset)
/*     */   {
/*  57 */     this.offset = offset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CacheEntry(int _entry_type, CacheFileWithCache _file, DirectByteBuffer _buffer, long _offset, int _size)
/*     */   {
/*  68 */     this.entry_type = _entry_type;
/*  69 */     this.file = _file;
/*  70 */     this.buffer = _buffer;
/*  71 */     this.offset = _offset;
/*  72 */     this.size = _size;
/*     */     
/*  74 */     this.buffer_pos = this.buffer.position((byte)3);
/*  75 */     this.buffer_limit = this.buffer.limit((byte)3);
/*     */     
/*  77 */     if (this.size != this.buffer_limit - this.buffer_pos)
/*     */     {
/*  79 */       Debug.out("CacheEntry: initial size incorrect - size =" + this.size + ", pos = " + this.buffer_pos + ", lim = " + this.buffer_limit);
/*     */     }
/*     */     
/*  82 */     this.dirty = true;
/*  83 */     this.last_used = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public CacheFileWithCache getFile()
/*     */   {
/*  89 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFilePosition()
/*     */   {
/*  95 */     return this.offset;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLength()
/*     */   {
/* 101 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer getBuffer()
/*     */   {
/* 107 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDirty()
/*     */   {
/* 113 */     return this.dirty;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setClean()
/*     */   {
/* 119 */     this.dirty = false;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resetBufferPosition()
/*     */   {
/* 125 */     this.buffer.position((byte)3, this.buffer_pos);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void used()
/*     */   {
/* 131 */     this.last_used = SystemTime.getCurrentTime();
/*     */     
/* 133 */     this.usage_count += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getLastUsed()
/*     */   {
/* 139 */     return this.last_used;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getUsageCount()
/*     */   {
/* 145 */     return this.usage_count;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getType()
/*     */   {
/* 151 */     return this.entry_type;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 157 */     return "[" + this.offset + " - " + (this.offset + this.size - 1L) + ":" + this.buffer.position((byte)3) + "/" + this.buffer.limit((byte)3) + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/cache/impl/CacheEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */