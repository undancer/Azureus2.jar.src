/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class ByteCountedInputStream
/*     */   extends FilterInputStream
/*     */ {
/*     */   private long position;
/*     */   private long mark;
/*     */   
/*     */   public ByteCountedInputStream(InputStream is)
/*     */   {
/*  38 */     super(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  46 */     int read = this.in.read();
/*     */     
/*  48 */     this.position += read;
/*     */     
/*  50 */     return read;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/*  58 */     int read = read(b, 0, b.length);
/*     */     
/*  60 */     this.position += read;
/*     */     
/*  62 */     return read;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  70 */     int read = this.in.read(b, off, len);
/*     */     
/*  72 */     this.position += read;
/*     */     
/*  74 */     return read;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/*  82 */     long skipped = this.in.skip(n);
/*     */     
/*  84 */     this.position += skipped;
/*     */     
/*  86 */     return skipped;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void mark(int readlimit)
/*     */   {
/*  92 */     this.in.mark(readlimit);
/*     */     
/*  94 */     this.mark = this.position;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */     throws IOException
/*     */   {
/* 102 */     this.in.reset();
/*     */     
/* 104 */     this.position = this.mark;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPosition()
/*     */   {
/* 110 */     return this.position;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/ByteCountedInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */