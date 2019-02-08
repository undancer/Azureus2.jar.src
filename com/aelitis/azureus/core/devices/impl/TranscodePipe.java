/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Average;
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
/*     */ public abstract class TranscodePipe
/*     */ {
/*  42 */   private final int BUFFER_SIZE = 131072;
/*  43 */   private final int BUFFER_CACHE_SIZE = 393216;
/*     */   
/*     */   protected volatile boolean paused;
/*     */   
/*     */   protected volatile boolean destroyed;
/*     */   
/*     */   protected volatile int bytes_available;
/*     */   protected volatile int max_bytes_per_sec;
/*  51 */   protected List<Socket> sockets = new ArrayList();
/*     */   
/*     */   private ServerSocket server_socket;
/*     */   
/*     */   private AEThread2 refiller;
/*  56 */   private LinkedList<bufferCache> buffer_cache = new LinkedList();
/*     */   
/*     */   private int buffer_cache_size;
/*  59 */   private Average connection_speed = Average.getInstance(1000, 10);
/*  60 */   private Average write_speed = Average.getInstance(1000, 10);
/*     */   
/*     */ 
/*     */   private errorListener error_listener;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TranscodePipe(errorListener _error_listener)
/*     */     throws IOException
/*     */   {
/*  70 */     this.error_listener = _error_listener;
/*     */     
/*  72 */     this.server_socket = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
/*     */     
/*  74 */     new AEThread2("TranscodePipe", true)
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         for (;;) {
/*  79 */           if (!TranscodePipe.this.destroyed) {
/*     */             try
/*     */             {
/*  82 */               final Socket socket = TranscodePipe.this.server_socket.accept();
/*     */               
/*  84 */               TranscodePipe.this.connection_speed.addValue(1L);
/*     */               
/*  86 */               new AEThread2("TranscodePipe", true)
/*     */               {
/*     */ 
/*     */                 public void run()
/*     */                 {
/*  91 */                   TranscodePipe.this.handleSocket(socket);
/*     */                 }
/*     */               }.start();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/*  97 */               if (!TranscodePipe.this.destroyed)
/*     */               {
/*  99 */                 TranscodePipe.this.destroy();
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getConnectionRate()
/*     */   {
/* 112 */     return this.connection_speed.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getWriteSpeed()
/*     */   {
/* 118 */     return this.write_speed.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void handleSocket(Socket paramSocket);
/*     */   
/*     */ 
/*     */ 
/*     */   protected void handlePipe(final InputStream is, final OutputStream os)
/*     */   {
/* 130 */     new AEThread2("TranscodePipe:c", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 135 */         int BUFFER_SIZE = 131072;
/*     */         
/* 137 */         byte[] buffer = new byte[131072];
/*     */         for (;;) {
/* 139 */           if (!TranscodePipe.this.destroyed) {
/*     */             try
/*     */             {
/*     */               int limit;
/*     */               int limit;
/* 144 */               if (TranscodePipe.this.paused)
/*     */               {
/* 146 */                 Thread.sleep(250L);
/*     */                 
/* 148 */                 limit = 1;
/*     */ 
/*     */ 
/*     */               }
/* 152 */               else if (TranscodePipe.this.max_bytes_per_sec > 0)
/*     */               {
/* 154 */                 int limit = TranscodePipe.this.bytes_available;
/*     */                 
/* 156 */                 if (limit <= 0)
/*     */                 {
/* 158 */                   Thread.sleep(25L);
/*     */                   
/* 160 */                   continue;
/*     */                 }
/*     */                 
/* 163 */                 limit = Math.min(131072, limit);
/*     */               }
/*     */               else
/*     */               {
/* 167 */                 limit = 131072;
/*     */               }
/*     */               
/*     */ 
/* 171 */               int len = is.read(buffer, 0, limit);
/*     */               
/* 173 */               if (len > 0)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 178 */                 if (TranscodePipe.this.max_bytes_per_sec > 0)
/*     */                 {
/* 180 */                   TranscodePipe.this.bytes_available -= len;
/*     */                 }
/*     */                 
/* 183 */                 os.write(buffer, 0, len);
/*     */                 
/* 185 */                 TranscodePipe.this.write_speed.addValue(len);
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */         
/*     */         try
/*     */         {
/* 194 */           os.flush();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */         try
/*     */         {
/* 200 */           is.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */         try
/*     */         {
/* 206 */           os.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected RandomAccessFile reserveRAF()
/*     */     throws IOException
/*     */   {
/* 219 */     throw new IOException("Not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void releaseRAF(RandomAccessFile raf) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleRAF(final OutputStream os, final long position, long length)
/*     */   {
/* 234 */     new AEThread2("TranscodePipe:c", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 239 */         RandomAccessFile raf = null;
/*     */         try
/*     */         {
/* 242 */           raf = TranscodePipe.this.reserveRAF();
/*     */           
/* 244 */           long pos = position;
/*     */           
/* 246 */           long rem = os;
/*     */           
/* 248 */           while ((!TranscodePipe.this.destroyed) && (rem > 0L))
/*     */           {
/*     */             int limit;
/*     */             int limit;
/* 252 */             if (TranscodePipe.this.paused)
/*     */             {
/* 254 */               Thread.sleep(250L);
/*     */               
/* 256 */               limit = 1;
/*     */             }
/*     */             else
/*     */             {
/* 260 */               if (TranscodePipe.this.max_bytes_per_sec > 0)
/*     */               {
/* 262 */                 int limit = TranscodePipe.this.bytes_available;
/*     */                 
/* 264 */                 if (limit <= 0)
/*     */                 {
/* 266 */                   Thread.sleep(25L);
/*     */                   
/* 268 */                   continue;
/*     */                 }
/*     */                 
/* 271 */                 limit = Math.min(131072, limit);
/*     */               }
/*     */               else
/*     */               {
/* 275 */                 limit = 131072;
/*     */               }
/*     */               
/* 278 */               limit = (int)Math.min(rem, limit);
/*     */             }
/*     */             
/* 281 */             int read_length = 0;
/*     */             
/* 283 */             int buffer_start = 0;
/* 284 */             byte[] buffer = null;
/*     */             
/* 286 */             synchronized (TranscodePipe.this)
/*     */             {
/* 288 */               int c_num = 0;
/*     */               
/* 290 */               Iterator<TranscodePipe.bufferCache> it = TranscodePipe.this.buffer_cache.iterator();
/*     */               
/* 292 */               while (it.hasNext())
/*     */               {
/* 294 */                 TranscodePipe.bufferCache b = (TranscodePipe.bufferCache)it.next();
/*     */                 
/* 296 */                 long rel_offset = pos - TranscodePipe.bufferCache.access$400(b);
/*     */                 
/* 298 */                 if (rel_offset >= 0L)
/*     */                 {
/* 300 */                   byte[] data = TranscodePipe.bufferCache.access$500(b);
/*     */                   
/* 302 */                   long avail = data.length - rel_offset;
/*     */                   
/* 304 */                   if (avail > 0L)
/*     */                   {
/* 306 */                     read_length = (int)Math.min(avail, limit);
/*     */                     
/* 308 */                     buffer = data;
/* 309 */                     buffer_start = (int)rel_offset;
/*     */                     
/*     */ 
/*     */ 
/* 313 */                     if (c_num <= 0)
/*     */                       break;
/* 315 */                     it.remove();
/*     */                     
/* 317 */                     TranscodePipe.this.buffer_cache.addFirst(b); break;
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 324 */                 c_num++;
/*     */               }
/*     */               
/* 327 */               if (buffer == null)
/*     */               {
/* 329 */                 buffer = new byte[limit];
/*     */                 
/* 331 */                 raf.seek(pos);
/*     */                 
/* 333 */                 read_length = raf.read(buffer);
/*     */                 
/* 335 */                 if (read_length != limit)
/*     */                 {
/* 337 */                   Debug.out("eh?");
/*     */                   
/* 339 */                   throw new IOException("Inconsistent");
/*     */                 }
/*     */                 
/* 342 */                 TranscodePipe.bufferCache b = new TranscodePipe.bufferCache(pos, buffer);
/*     */                 
/*     */ 
/*     */ 
/* 346 */                 TranscodePipe.this.buffer_cache.addFirst(b);
/*     */                 
/* 348 */                 TranscodePipe.access$612(TranscodePipe.this, limit);
/*     */                 
/* 350 */                 while (TranscodePipe.this.buffer_cache_size > 393216)
/*     */                 {
/* 352 */                   b = (TranscodePipe.bufferCache)TranscodePipe.this.buffer_cache.removeLast();
/*     */                   
/* 354 */                   TranscodePipe.access$620(TranscodePipe.this, TranscodePipe.bufferCache.access$500(b).length);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 359 */             if (read_length <= 0) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 364 */             rem -= read_length;
/* 365 */             pos += read_length;
/*     */             
/* 367 */             if (TranscodePipe.this.max_bytes_per_sec > 0)
/*     */             {
/* 369 */               TranscodePipe.this.bytes_available -= read_length;
/*     */             }
/*     */             
/* 372 */             this.val$os.write(buffer, buffer_start, read_length);
/*     */             
/* 374 */             TranscodePipe.this.write_speed.addValue(read_length);
/*     */           }
/*     */           
/* 377 */           this.val$os.flush();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 381 */           if (raf != null) {
/*     */             try
/*     */             {
/* 384 */               synchronized (TranscodePipe.this)
/*     */               {
/* 386 */                 raf.seek(0L);
/*     */                 
/* 388 */                 raf.read(new byte[1]);
/*     */               }
/*     */             }
/*     */             catch (Throwable f) {
/* 392 */               TranscodePipe.this.reportError(e);
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/*     */           try {
/* 398 */             this.val$os.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 403 */           if (raf != null)
/*     */           {
/* 405 */             TranscodePipe.this.releaseRAF(raf);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void pause()
/*     */   {
/* 415 */     this.paused = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resume()
/*     */   {
/* 421 */     this.paused = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaxBytesPerSecond(int max)
/*     */   {
/* 428 */     if (max == this.max_bytes_per_sec)
/*     */     {
/* 430 */       return;
/*     */     }
/*     */     
/* 433 */     this.max_bytes_per_sec = max;
/*     */     
/* 435 */     synchronized (this)
/*     */     {
/* 437 */       if (this.refiller == null)
/*     */       {
/* 439 */         this.refiller = new AEThread2("refiller", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 445 */             int count = 0;
/*     */             for (;;) {
/* 447 */               if (!TranscodePipe.this.destroyed)
/*     */               {
/* 449 */                 if (TranscodePipe.this.max_bytes_per_sec == 0)
/*     */                 {
/* 451 */                   synchronized (TranscodePipe.this)
/*     */                   {
/* 453 */                     if (TranscodePipe.this.max_bytes_per_sec == 0)
/*     */                     {
/* 455 */                       TranscodePipe.this.refiller = null;
/*     */                       
/* 457 */                       return;
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 
/* 462 */                 count++;
/*     */                 
/* 464 */                 TranscodePipe.this.bytes_available += TranscodePipe.this.max_bytes_per_sec / 10;
/*     */                 
/* 466 */                 if (count % 10 == 0)
/*     */                 {
/* 468 */                   TranscodePipe.this.bytes_available += TranscodePipe.this.max_bytes_per_sec % 10;
/*     */                 }
/*     */                 try
/*     */                 {
/* 472 */                   Thread.sleep(100L);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 476 */                   Debug.printStackTrace(e);
/*     */                 }
/*     */                 
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 483 */         };
/* 484 */         this.refiller.start();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getPort()
/*     */   {
/* 492 */     return this.server_socket.getLocalPort();
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean destroy()
/*     */   {
/* 498 */     synchronized (this)
/*     */     {
/* 500 */       if (this.destroyed)
/*     */       {
/* 502 */         return false;
/*     */       }
/*     */       
/* 505 */       this.destroyed = true;
/*     */     }
/*     */     
/* 508 */     for (Socket s : this.sockets) {
/*     */       try
/*     */       {
/* 511 */         s.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 517 */     this.sockets.clear();
/*     */     try
/*     */     {
/* 520 */       this.server_socket.close();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 524 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 527 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reportError(Throwable error)
/*     */   {
/* 534 */     if (this.error_listener != null)
/*     */     {
/* 536 */       this.error_listener.error(error);
/*     */     }
/*     */     else
/*     */     {
/* 540 */       Debug.out(error);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class bufferCache
/*     */   {
/*     */     private long offset;
/*     */     
/*     */     private byte[] data;
/*     */     
/*     */ 
/*     */     protected bufferCache(long _offset, byte[] _data)
/*     */     {
/* 555 */       this.offset = _offset;
/* 556 */       this.data = _data;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static abstract interface errorListener
/*     */   {
/*     */     public abstract void error(Throwable paramThrowable);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodePipe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */