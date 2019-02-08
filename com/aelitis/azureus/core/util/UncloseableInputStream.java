/*     */ package com.aelitis.azureus.core.util;
/*     */ 
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
/*     */ 
/*     */ public class UncloseableInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final InputStream is;
/*     */   private boolean closed;
/*     */   
/*     */   public UncloseableInputStream(InputStream _is)
/*     */   {
/*  38 */     this.is = _is;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  46 */     if (this.closed) {
/*  47 */       throw new IOException("Stream Closed");
/*     */     }
/*     */     
/*  50 */     return this.is.read();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/*  59 */     if (this.closed) {
/*  60 */       throw new IOException("Stream Closed");
/*     */     }
/*     */     
/*  63 */     return this.is.read(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  74 */     if (this.closed) {
/*  75 */       throw new IOException("Stream Closed");
/*     */     }
/*     */     
/*  78 */     return this.is.read(b, off, len);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  86 */     this.closed = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/*  95 */     if (this.closed) {
/*  96 */       throw new IOException("Stream Closed");
/*     */     }
/*     */     
/*  99 */     return this.is.skip(n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 107 */     if (this.closed) {
/* 108 */       throw new IOException("Stream Closed");
/*     */     }
/*     */     
/* 111 */     return this.is.available();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void mark(int readlimit)
/*     */   {
/* 118 */     this.is.mark(readlimit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */     throws IOException
/*     */   {
/* 126 */     if (this.closed) {
/* 127 */       throw new IOException("Stream Closed");
/*     */     }
/*     */     
/* 130 */     this.is.reset();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 136 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 142 */     return this.closed;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/UncloseableInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */