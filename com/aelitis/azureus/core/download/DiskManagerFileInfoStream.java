/*     */ package com.aelitis.azureus.core.download;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerEvent;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerRandomReadRequest;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerRequest;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.PooledByteBufferImpl;
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
/*     */ public class DiskManagerFileInfoStream
/*     */   implements DiskManagerFileInfo
/*     */ {
/*     */   private StreamFactory stream_factory;
/*     */   private File save_to;
/*     */   private byte[] hash;
/*     */   private context current_context;
/*  53 */   private Object lock = this;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerFileInfoStream(StreamFactory _stream_factory, File _save_to)
/*     */   {
/*  60 */     this.stream_factory = _stream_factory;
/*  61 */     this.save_to = _save_to;
/*     */     try
/*     */     {
/*  64 */       this.hash = new SHA1Simple().calculateHash(_save_to.getAbsolutePath().getBytes("UTF-8"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  68 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isComplete()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 160	com/aelitis/azureus/core/download/DiskManagerFileInfoStream:lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 159	com/aelitis/azureus/core/download/DiskManagerFileInfoStream:save_to	Ljava/io/File;
/*     */     //   11: invokevirtual 166	java/io/File:exists	()Z
/*     */     //   14: aload_1
/*     */     //   15: monitorexit
/*     */     //   16: ireturn
/*     */     //   17: astore_2
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: aload_2
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #75	-> byte code offset #0
/*     */     //   Java source line #77	-> byte code offset #7
/*     */     //   Java source line #78	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	DiskManagerFileInfoStream
/*     */     //   5	14	1	Ljava/lang/Object;	Object
/*     */     //   17	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/*  84 */     synchronized (this.lock)
/*     */     {
/*  86 */       if (this.current_context != null)
/*     */       {
/*  88 */         this.current_context.destroy(new Exception("Reset"));
/*     */       }
/*     */       
/*  91 */       this.save_to.delete();
/*     */     }
/*     */   }
/*     */   
/*     */   public void setPriority(boolean b) {}
/*     */   
/*     */   public static abstract interface StreamFactory { public abstract StreamDetails getStream(Object paramObject) throws IOException;
/*     */     
/*     */     public abstract void destroyed(Object paramObject);
/*     */     
/*     */     public static abstract interface StreamDetails { public abstract InputStream getStream();
/*     */       
/*     */       public abstract boolean hasFailed(); } }
/*     */   
/* 105 */   public void setSkipped(boolean b) { throw new RuntimeException("Not supported"); }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getNumericPriority()
/*     */   {
/* 111 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getNumericPriorty()
/*     */   {
/* 118 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/* 125 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDeleted(boolean b) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLink(File link_destination)
/*     */   {
/* 137 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 143 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 149 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 155 */     return getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 161 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 167 */     return this.save_to;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 174 */     return this.save_to;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 180 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 186 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceSize()
/*     */   {
/* 192 */     return 32768L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumPieces()
/*     */   {
/* 198 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPriority()
/*     */   {
/* 204 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 210 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 216 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getDownloadHash()
/*     */     throws DownloadException
/*     */   {
/* 224 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 232 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public DiskManagerChannel createChannel()
/*     */     throws DownloadException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 160	com/aelitis/azureus/core/download/DiskManagerFileInfoStream:lock	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 158	com/aelitis/azureus/core/download/DiskManagerFileInfoStream:current_context	Lcom/aelitis/azureus/core/download/DiskManagerFileInfoStream$context;
/*     */     //   11: ifnonnull +15 -> 26
/*     */     //   14: aload_0
/*     */     //   15: new 99	com/aelitis/azureus/core/download/DiskManagerFileInfoStream$context
/*     */     //   18: dup
/*     */     //   19: aload_0
/*     */     //   20: invokespecial 162	com/aelitis/azureus/core/download/DiskManagerFileInfoStream$context:<init>	(Lcom/aelitis/azureus/core/download/DiskManagerFileInfoStream;)V
/*     */     //   23: putfield 158	com/aelitis/azureus/core/download/DiskManagerFileInfoStream:current_context	Lcom/aelitis/azureus/core/download/DiskManagerFileInfoStream$context;
/*     */     //   26: aload_0
/*     */     //   27: getfield 158	com/aelitis/azureus/core/download/DiskManagerFileInfoStream:current_context	Lcom/aelitis/azureus/core/download/DiskManagerFileInfoStream$context;
/*     */     //   30: invokevirtual 163	com/aelitis/azureus/core/download/DiskManagerFileInfoStream$context:createChannel	()Lcom/aelitis/azureus/core/download/DiskManagerFileInfoStream$context$channel;
/*     */     //   33: aload_1
/*     */     //   34: monitorexit
/*     */     //   35: areturn
/*     */     //   36: astore_2
/*     */     //   37: aload_1
/*     */     //   38: monitorexit
/*     */     //   39: aload_2
/*     */     //   40: athrow
/*     */     //   41: astore_1
/*     */     //   42: new 110	org/gudy/azureus2/plugins/download/DownloadException
/*     */     //   45: dup
/*     */     //   46: ldc 1
/*     */     //   48: aload_1
/*     */     //   49: invokespecial 176	org/gudy/azureus2/plugins/download/DownloadException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   52: athrow
/*     */     // Line number table:
/*     */     //   Java source line #241	-> byte code offset #0
/*     */     //   Java source line #243	-> byte code offset #7
/*     */     //   Java source line #245	-> byte code offset #14
/*     */     //   Java source line #248	-> byte code offset #26
/*     */     //   Java source line #249	-> byte code offset #36
/*     */     //   Java source line #250	-> byte code offset #41
/*     */     //   Java source line #252	-> byte code offset #42
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	53	0	this	DiskManagerFileInfoStream
/*     */     //   41	8	1	e	Throwable
/*     */     //   36	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	35	36	finally
/*     */     //   36	39	36	finally
/*     */     //   0	35	41	java/lang/Throwable
/*     */     //   36	41	41	java/lang/Throwable
/*     */   }
/*     */   
/*     */   public DiskManagerRandomReadRequest createRandomReadRequest(long file_offset, long length, boolean reverse_order, DiskManagerListener listener)
/*     */     throws DownloadException
/*     */   {
/* 265 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void destroyed(context c)
/*     */   {
/* 272 */     synchronized (this.lock)
/*     */     {
/* 274 */       if (this.current_context == c)
/*     */       {
/* 276 */         this.current_context = null;
/*     */       }
/*     */     }
/*     */     
/* 280 */     this.stream_factory.destroyed(c);
/*     */   }
/*     */   
/*     */ 
/*     */   protected class context
/*     */   {
/*     */     private RandomAccessFile raf;
/*     */     
/*     */     private DiskManagerFileInfoStream.StreamFactory.StreamDetails stream_details;
/*     */     
/*     */     private boolean stream_got_eof;
/* 291 */     private List<channel> channels = new ArrayList();
/*     */     
/* 293 */     private List<AESemaphore> waiters = new ArrayList();
/*     */     
/*     */     private boolean context_destroyed;
/*     */     
/*     */ 
/*     */     protected context()
/*     */       throws Exception
/*     */     {
/*     */       final File temp_file;
/* 302 */       if (DiskManagerFileInfoStream.this.save_to.exists())
/*     */       {
/* 304 */         this.raf = new RandomAccessFile(DiskManagerFileInfoStream.this.save_to, "r");
/*     */         
/* 306 */         this.stream_got_eof = true;
/*     */       }
/*     */       else
/*     */       {
/* 310 */         temp_file = new File(DiskManagerFileInfoStream.this.save_to.getAbsolutePath() + "._tmp_");
/*     */         
/* 312 */         this.raf = new RandomAccessFile(temp_file, "rw");
/*     */         
/* 314 */         this.stream_details = DiskManagerFileInfoStream.this.stream_factory.getStream(this);
/*     */         
/* 316 */         final InputStream stream = this.stream_details.getStream();
/*     */         
/* 318 */         new AEThread2("DMS:reader", true)
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/* 323 */             int BUFF_SIZE = 131072;
/*     */             
/* 325 */             byte[] buffer = new byte[131072];
/*     */             try
/*     */             {
/*     */               for (;;)
/*     */               {
/* 330 */                 int len = stream.read(buffer);
/*     */                 
/* 332 */                 if (len <= 0)
/*     */                 {
/* 334 */                   if (DiskManagerFileInfoStream.context.this.stream_details.hasFailed())
/*     */                   {
/* 336 */                     throw new IOException("Stream failed");
/*     */                   }
/*     */                   
/* 339 */                   synchronized (DiskManagerFileInfoStream.this.lock)
/*     */                   {
/* 341 */                     DiskManagerFileInfoStream.context.this.stream_got_eof = true;
/*     */                   }
/*     */                   
/* 344 */                   break;
/*     */                 }
/*     */                 
/* 347 */                 synchronized (DiskManagerFileInfoStream.this.lock)
/*     */                 {
/* 349 */                   DiskManagerFileInfoStream.context.this.raf.seek(DiskManagerFileInfoStream.context.this.raf.length());
/*     */                   
/* 351 */                   DiskManagerFileInfoStream.context.this.raf.write(buffer, 0, len);
/*     */                   
/* 353 */                   for (AESemaphore waiter : DiskManagerFileInfoStream.context.this.waiters)
/*     */                   {
/* 355 */                     waiter.release();
/*     */                   }
/*     */                 }
/*     */               }
/*     */             } catch (Throwable e) {
/*     */               Throwable failed;
/* 361 */               DiskManagerFileInfoStream.context.this.destroy(e);
/*     */             }
/*     */             finally {
/*     */               try {
/*     */                 Throwable failed;
/* 366 */                 stream.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */               
/*     */ 
/*     */ 
/* 372 */               Throwable failed = null;
/*     */               
/* 374 */               synchronized (DiskManagerFileInfoStream.this.lock)
/*     */               {
/* 376 */                 DiskManagerFileInfoStream.context.this.stream_details = null;
/*     */                 
/* 378 */                 if (DiskManagerFileInfoStream.context.this.stream_got_eof) {
/*     */                   try
/*     */                   {
/* 381 */                     DiskManagerFileInfoStream.context.this.raf.close();
/*     */                     
/* 383 */                     DiskManagerFileInfoStream.this.save_to.delete();
/*     */                     
/* 385 */                     temp_file.renameTo(DiskManagerFileInfoStream.this.save_to);
/*     */                     
/* 387 */                     DiskManagerFileInfoStream.context.this.raf = new RandomAccessFile(DiskManagerFileInfoStream.this.save_to, "r");
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 391 */                     failed = e;
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 396 */               if (failed != null)
/*     */               {
/* 398 */                 DiskManagerFileInfoStream.context.this.destroy(failed);
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected int read(byte[] buffer, long offset, int length)
/*     */       throws IOException
/*     */     {
/*     */       AESemaphore sem;
/*     */       
/*     */ 
/*     */ 
/* 416 */       synchronized (DiskManagerFileInfoStream.this.lock)
/*     */       {
/* 418 */         if (this.raf.length() > offset)
/*     */         {
/* 420 */           this.raf.seek(offset);
/*     */           
/* 422 */           return this.raf.read(buffer, 0, length);
/*     */         }
/*     */         
/* 425 */         if (this.stream_details == null)
/*     */         {
/* 427 */           if (this.stream_got_eof)
/*     */           {
/* 429 */             return -1;
/*     */           }
/*     */           
/* 432 */           throw new IOException("Premature end of stream (read)");
/*     */         }
/*     */         
/* 435 */         sem = new AESemaphore("DMS:block");
/*     */         
/* 437 */         this.waiters.add(sem);
/*     */       }
/*     */       try
/*     */       {
/* 441 */         sem.reserve(1000L);
/*     */       }
/*     */       finally
/*     */       {
/* 445 */         synchronized (DiskManagerFileInfoStream.this.lock)
/*     */         {
/* 447 */           this.waiters.remove(sem);
/*     */         }
/*     */       }
/*     */       
/* 451 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */     protected channel createChannel()
/*     */     {
/* 457 */       synchronized (DiskManagerFileInfoStream.this.lock)
/*     */       {
/* 459 */         channel c = new channel();
/*     */         
/* 461 */         this.channels.add(c);
/*     */         
/* 463 */         return c;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void removeChannel(channel c)
/*     */     {
/* 471 */       synchronized (DiskManagerFileInfoStream.this.lock)
/*     */       {
/* 473 */         this.channels.remove(c);
/*     */         
/* 475 */         if ((this.channels.size() == 0) && (DiskManagerFileInfoStream.this.save_to.exists()))
/*     */         {
/* 477 */           destroy(null);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void destroy(Throwable error)
/*     */     {
/* 486 */       if (error != null)
/*     */       {
/* 488 */         Debug.out(error);
/*     */       }
/*     */       
/* 491 */       synchronized (DiskManagerFileInfoStream.this.lock)
/*     */       {
/* 493 */         if (this.context_destroyed)
/*     */         {
/* 495 */           return;
/*     */         }
/*     */         
/* 498 */         this.context_destroyed = true;
/*     */         
/* 500 */         if (this.channels != null)
/*     */         {
/* 502 */           List<channel> channels_copy = new ArrayList(this.channels);
/*     */           
/* 504 */           for (channel c : channels_copy)
/*     */           {
/* 506 */             c.destroy();
/*     */           }
/*     */         }
/*     */         
/* 510 */         if (this.raf != null)
/*     */         {
/*     */           try {
/* 513 */             this.raf.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 518 */           this.raf = null;
/*     */         }
/*     */         
/* 521 */         if (this.stream_details != null)
/*     */         {
/*     */           try {
/* 524 */             this.stream_details.getStream().close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/*     */ 
/* 530 */           this.stream_details = null;
/*     */         }
/*     */         
/* 533 */         if (error != null)
/*     */         {
/* 535 */           DiskManagerFileInfoStream.this.save_to.delete();
/*     */         }
/*     */       }
/*     */       
/* 539 */       DiskManagerFileInfoStream.this.destroyed(this);
/*     */     }
/*     */     
/*     */     protected class channel
/*     */       implements DiskManagerChannel
/*     */     {
/*     */       private volatile boolean channel_destroyed;
/*     */       private volatile long channel_position;
/*     */       
/*     */       protected channel() {}
/*     */       
/*     */       public DiskManagerRequest createRequest()
/*     */       {
/* 552 */         return new request();
/*     */       }
/*     */       
/*     */ 
/*     */       public DiskManagerFileInfo getFile()
/*     */       {
/* 558 */         return DiskManagerFileInfoStream.this;
/*     */       }
/*     */       
/*     */ 
/*     */       public long getPosition()
/*     */       {
/* 564 */         return this.channel_position;
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean isDestroyed()
/*     */       {
/* 570 */         return this.channel_destroyed;
/*     */       }
/*     */       
/*     */ 
/*     */       public void destroy()
/*     */       {
/* 576 */         this.channel_destroyed = true;
/*     */         
/* 578 */         DiskManagerFileInfoStream.context.this.removeChannel(this);
/*     */       }
/*     */       
/*     */       protected class request implements DiskManagerRequest {
/*     */         private long offset;
/*     */         private long length;
/*     */         private long position;
/*     */         private int max_read_chunk;
/*     */         private volatile boolean request_cancelled;
/*     */         private CopyOnWriteList<DiskManagerListener> listeners;
/*     */         
/*     */         protected request() {
/* 590 */           this.max_read_chunk = 131072;
/*     */           
/*     */ 
/*     */ 
/* 594 */           this.listeners = new CopyOnWriteList();
/*     */         }
/*     */         
/*     */ 
/*     */         public void setType(int type)
/*     */         {
/* 600 */           if (type != 1)
/*     */           {
/* 602 */             throw new RuntimeException("Not supported");
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void setOffset(long _offset)
/*     */         {
/* 610 */           this.offset = _offset;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void setLength(long _length)
/*     */         {
/* 619 */           this.length = (_length == -1L ? Long.MAX_VALUE : _length);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void setMaximumReadChunkSize(int size)
/*     */         {
/* 626 */           if (size > 16384)
/*     */           {
/* 628 */             this.max_read_chunk = size;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */         public long getAvailableBytes()
/*     */         {
/* 635 */           return getRemaining();
/*     */         }
/*     */         
/*     */ 
/*     */         public long getRemaining()
/*     */         {
/* 641 */           return this.length == Long.MAX_VALUE ? this.length : this.offset + this.length - this.position;
/*     */         }
/*     */         
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/* 648 */             byte[] buffer = new byte[this.max_read_chunk];
/*     */             
/* 650 */             long rem = this.length;
/* 651 */             long pos = this.offset;
/*     */             
/* 653 */             while (rem > 0L)
/*     */             {
/* 655 */               if (this.request_cancelled)
/*     */               {
/* 657 */                 throw new Exception("Cancelled");
/*     */               }
/* 659 */               if (DiskManagerFileInfoStream.context.channel.this.channel_destroyed)
/*     */               {
/* 661 */                 throw new Exception("Destroyed");
/*     */               }
/*     */               
/* 664 */               int chunk = (int)Math.min(rem, this.max_read_chunk);
/*     */               
/* 666 */               int len = DiskManagerFileInfoStream.context.this.read(buffer, pos, chunk);
/*     */               
/* 668 */               if (len == -1)
/*     */               {
/* 670 */                 if (this.length == Long.MAX_VALUE) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 676 */                 throw new Exception("Premature end of stream (complete)");
/*     */               }
/* 678 */               if (len == 0)
/*     */               {
/* 680 */                 sendEvent(new event(pos));
/*     */               }
/*     */               else
/*     */               {
/* 684 */                 sendEvent(new event(new PooledByteBufferImpl(buffer, 0, len), pos, len));
/*     */                 
/* 686 */                 rem -= len;
/* 687 */                 pos += len;
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 692 */             sendEvent(new event(e));
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */         public void cancel()
/*     */         {
/* 699 */           this.request_cancelled = true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void setUserAgent(String agent) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         protected void sendEvent(event ev)
/*     */         {
/* 712 */           for (DiskManagerListener l : this.listeners)
/*     */           {
/* 714 */             l.eventOccurred(ev);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void addListener(DiskManagerListener listener)
/*     */         {
/* 722 */           this.listeners.add(listener);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void removeListener(DiskManagerListener listener)
/*     */         {
/* 729 */           this.listeners.remove(listener);
/*     */         }
/*     */         
/*     */ 
/*     */         protected class event
/*     */           implements DiskManagerEvent
/*     */         {
/*     */           private int event_type;
/*     */           
/*     */           private Throwable error;
/*     */           
/*     */           private PooledByteBuffer buffer;
/*     */           private long event_offset;
/*     */           private int event_length;
/*     */           
/*     */           protected event(Throwable _error)
/*     */           {
/* 746 */             this.event_type = 2;
/* 747 */             this.error = _error;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           protected event(long _offset)
/*     */           {
/* 754 */             this.event_type = 3;
/*     */             
/* 756 */             this.event_offset = _offset;
/*     */             
/* 758 */             DiskManagerFileInfoStream.context.channel.this.channel_position = _offset;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           protected event(PooledByteBuffer _buffer, long _offset, int _length)
/*     */           {
/* 767 */             this.event_type = 1;
/* 768 */             this.buffer = _buffer;
/* 769 */             this.event_offset = _offset;
/* 770 */             this.event_length = _length;
/*     */             
/* 772 */             DiskManagerFileInfoStream.context.channel.this.channel_position = (_offset + _length - 1L);
/*     */           }
/*     */           
/*     */ 
/*     */           public int getType()
/*     */           {
/* 778 */             return this.event_type;
/*     */           }
/*     */           
/*     */ 
/*     */           public DiskManagerRequest getRequest()
/*     */           {
/* 784 */             return DiskManagerFileInfoStream.context.channel.request.this;
/*     */           }
/*     */           
/*     */ 
/*     */           public long getOffset()
/*     */           {
/* 790 */             return this.event_offset;
/*     */           }
/*     */           
/*     */ 
/*     */           public int getLength()
/*     */           {
/* 796 */             return this.event_length;
/*     */           }
/*     */           
/*     */ 
/*     */           public PooledByteBuffer getBuffer()
/*     */           {
/* 802 */             return this.buffer;
/*     */           }
/*     */           
/*     */ 
/*     */           public Throwable getFailure()
/*     */           {
/* 808 */             return this.error;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/DiskManagerFileInfoStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */