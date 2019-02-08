/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class DeleteFileOnCloseInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private InputStream in;
/*     */   private final File file;
/*     */   private boolean closed;
/*     */   private long pos;
/*     */   private long mark;
/*     */   
/*     */   public DeleteFileOnCloseInputStream(File _file)
/*     */     throws IOException
/*     */   {
/*  48 */     this.file = _file;
/*  49 */     this.in = new BufferedInputStream(new FileInputStream(this.file), 131072);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  57 */     if (this.closed)
/*     */     {
/*  59 */       return;
/*     */     }
/*     */     
/*  62 */     this.closed = true;
/*     */     try
/*     */     {
/*  65 */       this.in.close();
/*     */     }
/*     */     finally
/*     */     {
/*  69 */       if (!this.file.delete())
/*     */       {
/*  71 */         Debug.out("Failed to delete file '" + this.file + "'");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  81 */     int result = this.in.read();
/*     */     
/*  83 */     this.pos += 1L;
/*     */     
/*  85 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/*  95 */     int res = read(b, 0, b.length);
/*     */     
/*  97 */     if (res > 0)
/*     */     {
/*  99 */       this.pos += res;
/*     */     }
/*     */     
/* 102 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 114 */     int res = this.in.read(b, off, len);
/*     */     
/* 116 */     if (res > 0)
/*     */     {
/* 118 */       this.pos += res;
/*     */     }
/*     */     
/* 121 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long skip(long n)
/*     */     throws IOException
/*     */   {
/* 131 */     long res = this.in.skip(n);
/*     */     
/* 133 */     this.pos += res;
/*     */     
/* 135 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/* 144 */     return this.in.available();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void mark(int readlimit)
/*     */   {
/* 151 */     this.mark = this.pos;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */     throws IOException
/*     */   {
/* 159 */     this.in.close();
/*     */     
/* 161 */     this.in = new FileInputStream(this.file);
/*     */     
/* 163 */     this.in.skip(this.mark);
/*     */     
/* 165 */     this.pos = this.mark;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean markSupported()
/*     */   {
/* 171 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 180 */     return this.file;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/DeleteFileOnCloseInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */