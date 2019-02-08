/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TranscodeJobOutputLeecher
/*     */   implements DiskManagerFileInfo
/*     */ {
/*     */   private TranscodeJobImpl job;
/*     */   private TranscodeFileImpl file;
/*     */   private File save_to;
/*     */   private byte[] hash;
/*     */   
/*     */   public TranscodeJobOutputLeecher(TranscodeJobImpl _job, TranscodeFileImpl _file)
/*     */     throws TranscodeException
/*     */   {
/*  59 */     this.job = _job;
/*  60 */     this.file = _file;
/*     */     
/*  62 */     this.save_to = this.file.getCacheFile();
/*     */     try
/*     */     {
/*  65 */       this.hash = new SHA1Simple().calculateHash(this.save_to.getAbsolutePath().getBytes("UTF-8"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  69 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPriority(boolean b) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getNumericPriorty()
/*     */   {
/*  83 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriority()
/*     */   {
/*  89 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/*  96 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSkipped(boolean b)
/*     */   {
/* 103 */     throw new RuntimeException("Not supported");
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
/* 115 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 121 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 127 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 133 */     return getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 139 */     if (this.file.isComplete()) {
/*     */       try
/*     */       {
/* 142 */         return this.file.getTargetFile().getLength();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 146 */         return -1L;
/*     */       }
/*     */     }
/*     */     
/* 150 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 157 */     return this.save_to;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 164 */     return this.save_to;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 170 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 176 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceSize()
/*     */   {
/* 182 */     return 32768L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumPieces()
/*     */   {
/* 188 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPriority()
/*     */   {
/* 194 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 200 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 206 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getDownloadHash()
/*     */     throws DownloadException
/*     */   {
/* 214 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 222 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskManagerChannel createChannel()
/*     */     throws DownloadException
/*     */   {
/* 230 */     return new Channel();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerRandomReadRequest createRandomReadRequest(long file_offset, long length, boolean reverse_order, DiskManagerListener listener)
/*     */     throws DownloadException
/*     */   {
/* 242 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   protected class Channel
/*     */     implements DiskManagerChannel
/*     */   {
/*     */     private volatile boolean channel_destroyed;
/*     */     private volatile long channel_position;
/*     */     private RandomAccessFile raf;
/*     */     
/*     */     protected Channel() {}
/*     */     
/*     */     public DiskManagerRequest createRequest()
/*     */     {
/* 257 */       return new request();
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerFileInfo getFile()
/*     */     {
/* 263 */       return TranscodeJobOutputLeecher.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getPosition()
/*     */     {
/* 269 */       return this.channel_position;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDestroyed()
/*     */     {
/* 275 */       return this.channel_destroyed;
/*     */     }
/*     */     
/*     */ 
/*     */     public void destroy()
/*     */     {
/* 281 */       synchronized (this)
/*     */       {
/* 283 */         this.channel_destroyed = true;
/*     */         
/* 285 */         if (this.raf != null)
/*     */         {
/*     */           try {
/* 288 */             this.raf.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/* 293 */           this.raf = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected int read(byte[] buffer, long offset, int length)
/*     */       throws IOException
/*     */     {
/* 306 */       synchronized (this)
/*     */       {
/* 308 */         if (this.channel_destroyed)
/*     */         {
/* 310 */           throw new IOException("Channel destroyed");
/*     */         }
/*     */         
/* 313 */         if (this.raf == null)
/*     */         {
/* 315 */           if (TranscodeJobOutputLeecher.this.save_to.exists())
/*     */           {
/* 317 */             this.raf = new RandomAccessFile(TranscodeJobOutputLeecher.this.save_to, "r");
/*     */           }
/*     */           else
/*     */           {
/* 321 */             int state = TranscodeJobOutputLeecher.this.job.getState();
/*     */             
/* 323 */             if (state == 7)
/*     */             {
/* 325 */               throw new IOException("Job has been removed");
/*     */             }
/* 327 */             if ((state == 5) || (state == 4))
/*     */             {
/*     */ 
/* 330 */               throw new IOException("Job has failed or been cancelled");
/*     */             }
/* 332 */             if (state == 3)
/*     */             {
/* 334 */               throw new IOException("Job is complete but file missing");
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 341 */         if (this.raf != null)
/*     */         {
/* 343 */           if (this.raf.length() > offset)
/*     */           {
/* 345 */             this.raf.seek(offset);
/*     */             
/* 347 */             return this.raf.read(buffer, 0, length);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 353 */           if (TranscodeJobOutputLeecher.this.file.isComplete())
/*     */           {
/* 355 */             return -1;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 362 */         Thread.sleep(500L);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 366 */         throw new IOException("Interrupted");
/*     */       }
/*     */       
/* 369 */       return 0;
/*     */     }
/*     */     
/*     */     protected class request implements DiskManagerRequest {
/*     */       private long offset;
/*     */       private long length;
/*     */       private long position;
/*     */       private int max_read_chunk;
/*     */       private volatile boolean request_cancelled;
/*     */       private CopyOnWriteList<DiskManagerListener> listeners;
/*     */       
/*     */       protected request() {
/* 381 */         this.max_read_chunk = 131072;
/*     */         
/*     */ 
/*     */ 
/* 385 */         this.listeners = new CopyOnWriteList();
/*     */       }
/*     */       
/*     */ 
/*     */       public void setType(int type)
/*     */       {
/* 391 */         if (type != 1)
/*     */         {
/* 393 */           throw new RuntimeException("Not supported");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setOffset(long _offset)
/*     */       {
/* 401 */         this.offset = _offset;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setLength(long _length)
/*     */       {
/* 410 */         this.length = (_length == -1L ? Long.MAX_VALUE : _length);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setMaximumReadChunkSize(int size)
/*     */       {
/* 417 */         if (size > 16384)
/*     */         {
/* 419 */           this.max_read_chunk = size;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public long getAvailableBytes()
/*     */       {
/* 426 */         return getRemaining();
/*     */       }
/*     */       
/*     */ 
/*     */       public long getRemaining()
/*     */       {
/* 432 */         return this.length == Long.MAX_VALUE ? this.length : this.offset + this.length - this.position;
/*     */       }
/*     */       
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 439 */           byte[] buffer = new byte[this.max_read_chunk];
/*     */           
/* 441 */           long rem = this.length;
/* 442 */           long pos = this.offset;
/*     */           
/* 444 */           while (rem > 0L)
/*     */           {
/* 446 */             if (this.request_cancelled)
/*     */             {
/* 448 */               throw new Exception("Cancelled");
/*     */             }
/* 450 */             if (TranscodeJobOutputLeecher.Channel.this.channel_destroyed)
/*     */             {
/* 452 */               throw new Exception("Destroyed");
/*     */             }
/*     */             
/* 455 */             int chunk = (int)Math.min(rem, this.max_read_chunk);
/*     */             
/* 457 */             int len = TranscodeJobOutputLeecher.Channel.this.read(buffer, pos, chunk);
/*     */             
/* 459 */             if (len == -1)
/*     */             {
/* 461 */               if (this.length == Long.MAX_VALUE) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 467 */               throw new Exception("Premature end of stream (complete)");
/*     */             }
/* 469 */             if (len == 0)
/*     */             {
/* 471 */               sendEvent(new event(pos));
/*     */             }
/*     */             else
/*     */             {
/* 475 */               sendEvent(new event(new PooledByteBufferImpl(buffer, 0, len), pos, len));
/*     */               
/* 477 */               rem -= len;
/* 478 */               pos += len;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 483 */           sendEvent(new event(e));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void cancel()
/*     */       {
/* 490 */         this.request_cancelled = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setUserAgent(String agent) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       protected void sendEvent(event ev)
/*     */       {
/* 503 */         for (DiskManagerListener l : this.listeners)
/*     */         {
/* 505 */           l.eventOccurred(ev);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void addListener(DiskManagerListener listener)
/*     */       {
/* 513 */         this.listeners.add(listener);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeListener(DiskManagerListener listener)
/*     */       {
/* 520 */         this.listeners.remove(listener);
/*     */       }
/*     */       
/*     */ 
/*     */       protected class event
/*     */         implements DiskManagerEvent
/*     */       {
/*     */         private int event_type;
/*     */         
/*     */         private Throwable error;
/*     */         
/*     */         private PooledByteBuffer buffer;
/*     */         private long event_offset;
/*     */         private int event_length;
/*     */         
/*     */         protected event(Throwable _error)
/*     */         {
/* 537 */           this.event_type = 2;
/* 538 */           this.error = _error;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         protected event(long _offset)
/*     */         {
/* 545 */           this.event_type = 3;
/*     */           
/* 547 */           this.event_offset = _offset;
/*     */           
/* 549 */           TranscodeJobOutputLeecher.Channel.this.channel_position = _offset;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         protected event(PooledByteBuffer _buffer, long _offset, int _length)
/*     */         {
/* 558 */           this.event_type = 1;
/* 559 */           this.buffer = _buffer;
/* 560 */           this.event_offset = _offset;
/* 561 */           this.event_length = _length;
/*     */           
/* 563 */           TranscodeJobOutputLeecher.Channel.this.channel_position = (_offset + _length - 1L);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getType()
/*     */         {
/* 569 */           return this.event_type;
/*     */         }
/*     */         
/*     */ 
/*     */         public DiskManagerRequest getRequest()
/*     */         {
/* 575 */           return TranscodeJobOutputLeecher.Channel.request.this;
/*     */         }
/*     */         
/*     */ 
/*     */         public long getOffset()
/*     */         {
/* 581 */           return this.event_offset;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getLength()
/*     */         {
/* 587 */           return this.event_length;
/*     */         }
/*     */         
/*     */ 
/*     */         public PooledByteBuffer getBuffer()
/*     */         {
/* 593 */           return this.buffer;
/*     */         }
/*     */         
/*     */ 
/*     */         public Throwable getFailure()
/*     */         {
/* 599 */           return this.error;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeJobOutputLeecher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */