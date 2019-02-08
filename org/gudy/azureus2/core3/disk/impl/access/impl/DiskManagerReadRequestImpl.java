/*     */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*     */ 
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiskManagerReadRequestImpl
/*     */   extends DiskManagerRequestImpl
/*     */   implements DiskManagerReadRequest
/*     */ {
/*     */   private static final int EXPIRATION_TIME = 60000;
/*     */   private final int pieceNumber;
/*     */   private final int offset;
/*     */   private final int length;
/*     */   private final int hashcode;
/*     */   private long timeCreated;
/*     */   private long timeSent;
/*     */   private boolean flush;
/*     */   private boolean cancelled;
/*  56 */   private boolean use_cache = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean latency_test;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerReadRequestImpl(int _pieceNumber, int _offset, int _length)
/*     */   {
/*  68 */     this.pieceNumber = _pieceNumber;
/*  69 */     this.offset = _offset;
/*  70 */     this.length = _length;
/*     */     
/*  72 */     this.timeCreated = SystemTime.getCurrentTime();
/*     */     
/*  74 */     this.hashcode = (this.pieceNumber + this.offset + this.length);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getName()
/*     */   {
/*  80 */     return "Read: " + this.pieceNumber + ",off=" + this.offset + ",len=" + this.length + ",fl=" + this.flush + ",uc=" + this.use_cache;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isExpired()
/*     */   {
/*  89 */     long now = SystemTime.getCurrentTime();
/*  90 */     if (now >= this.timeCreated) {
/*  91 */       return now - this.timeCreated > 60000L;
/*     */     }
/*  93 */     this.timeCreated = now;
/*  94 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resetTime(long now)
/*     */   {
/* 104 */     this.timeCreated = now;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPieceNumber()
/*     */   {
/* 110 */     return this.pieceNumber;
/*     */   }
/*     */   
/*     */   public int getOffset()
/*     */   {
/* 115 */     return this.offset;
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/* 120 */     return this.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFlush(boolean _flush)
/*     */   {
/* 127 */     this.flush = _flush;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getFlush()
/*     */   {
/* 133 */     return this.flush;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUseCache(boolean cache)
/*     */   {
/* 140 */     this.use_cache = cache;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getUseCache()
/*     */   {
/* 146 */     return this.use_cache;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 152 */     this.cancelled = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 158 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 168 */     if (!(o instanceof DiskManagerReadRequestImpl))
/* 169 */       return false;
/* 170 */     DiskManagerReadRequestImpl otherRequest = (DiskManagerReadRequestImpl)o;
/* 171 */     if (otherRequest.pieceNumber != this.pieceNumber)
/* 172 */       return false;
/* 173 */     if (otherRequest.offset != this.offset)
/* 174 */       return false;
/* 175 */     if (otherRequest.length != this.length) {
/* 176 */       return false;
/*     */     }
/* 178 */     return true;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 182 */     return this.hashcode;
/*     */   }
/*     */   
/*     */   public long getTimeCreated(long now)
/*     */   {
/* 187 */     if (this.timeCreated > now)
/* 188 */       this.timeCreated = now;
/* 189 */     return this.timeCreated;
/*     */   }
/*     */   
/*     */   public void setTimeSent(long time) {
/* 193 */     this.timeSent = time;
/*     */   }
/*     */   
/*     */   public long getTimeSent() {
/* 197 */     return this.timeSent;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLatencyTest()
/*     */   {
/* 203 */     this.latency_test = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLatencyTest()
/*     */   {
/* 209 */     return this.latency_test;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DiskManagerReadRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */