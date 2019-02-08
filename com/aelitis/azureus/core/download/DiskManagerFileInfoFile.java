/*     */ package com.aelitis.azureus.core.download;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.QTFastStartRAF;
/*     */ import java.io.File;
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
/*     */ public class DiskManagerFileInfoFile
/*     */   implements DiskManagerFileInfo
/*     */ {
/*     */   private byte[] hash;
/*     */   private File file;
/*     */   
/*     */   public DiskManagerFileInfoFile(File _file)
/*     */   {
/*  52 */     this.file = _file;
/*     */     try
/*     */     {
/*  55 */       this.hash = new SHA1Simple().calculateHash(this.file.getAbsolutePath().getBytes("UTF-8"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  59 */       Debug.out(e);
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
/*     */   public int getNumericPriorty()
/*     */   {
/*  72 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriority()
/*     */   {
/*  78 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/*  85 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSkipped(boolean b)
/*     */   {
/*  92 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDeleted(boolean b) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setLink(File link_destination)
/*     */   {
/* 105 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 111 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 117 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 123 */     return getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 129 */     return this.file.length();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 135 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 142 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 148 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 154 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceSize()
/*     */   {
/* 160 */     return 32768L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumPieces()
/*     */   {
/* 166 */     long piece_size = getPieceSize();
/*     */     
/* 168 */     return (int)((getLength() + piece_size - 1L) / piece_size);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPriority()
/*     */   {
/* 174 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 180 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 186 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getDownloadHash()
/*     */   {
/* 192 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 200 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskManagerChannel createChannel()
/*     */     throws DownloadException
/*     */   {
/* 208 */     return new channel();
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
/* 220 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */   protected class channel
/*     */     implements DiskManagerChannel
/*     */   {
/*     */     private volatile boolean channel_destroyed;
/*     */     private volatile long channel_position;
/*     */     
/*     */     protected channel() {}
/*     */     
/*     */     public DiskManagerRequest createRequest()
/*     */     {
/* 233 */       return new request();
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerFileInfo getFile()
/*     */     {
/* 239 */       return DiskManagerFileInfoFile.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getPosition()
/*     */     {
/* 245 */       return this.channel_position;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDestroyed()
/*     */     {
/* 251 */       return this.channel_destroyed;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 257 */     public void destroy() { this.channel_destroyed = true; }
/*     */     
/*     */     protected class request implements DiskManagerRequest {
/*     */       private long offset;
/*     */       private long length;
/*     */       private long position;
/*     */       private int max_read_chunk;
/*     */       private volatile boolean cancelled;
/*     */       private String user_agent;
/*     */       private CopyOnWriteList<DiskManagerListener> listeners;
/*     */       
/*     */       protected request() {
/* 269 */         this.max_read_chunk = 131072;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 275 */         this.listeners = new CopyOnWriteList();
/*     */       }
/*     */       
/*     */ 
/*     */       public void setType(int type)
/*     */       {
/* 281 */         if (type != 1)
/*     */         {
/* 283 */           throw new RuntimeException("Not supported");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setOffset(long _offset)
/*     */       {
/* 291 */         this.offset = _offset;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setLength(long _length)
/*     */       {
/* 298 */         if (_length < 0L)
/*     */         {
/* 300 */           throw new RuntimeException("Illegal argument");
/*     */         }
/*     */         
/* 303 */         this.length = _length;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setMaximumReadChunkSize(int size)
/*     */       {
/* 310 */         this.max_read_chunk = size;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setUserAgent(String agent)
/*     */       {
/* 317 */         this.user_agent = agent;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public long getAvailableBytes()
/*     */       {
/* 324 */         return getRemaining();
/*     */       }
/*     */       
/*     */ 
/*     */       public long getRemaining()
/*     */       {
/* 330 */         return this.offset + this.length - this.position;
/*     */       }
/*     */       
/*     */ 
/*     */       public void run()
/*     */       {
/* 336 */         QTFastStartRAF raf = null;
/*     */         
/* 338 */         String name = DiskManagerFileInfoFile.this.file.getName();
/*     */         
/* 340 */         int dot_pos = name.lastIndexOf('.');
/*     */         
/* 342 */         String ext = dot_pos < 0 ? "" : name.substring(dot_pos + 1);
/*     */         try
/*     */         {
/* 345 */           raf = new QTFastStartRAF(DiskManagerFileInfoFile.this.file, (this.user_agent != null) && (QTFastStartRAF.isSupportedExtension(ext)));
/*     */           
/* 347 */           raf.seek(this.offset);
/*     */           
/* 349 */           byte[] buffer = new byte[this.max_read_chunk];
/*     */           
/* 351 */           long rem = this.length;
/* 352 */           long pos = this.offset;
/*     */           
/* 354 */           while (rem > 0L)
/*     */           {
/* 356 */             if (this.cancelled)
/*     */             {
/* 358 */               throw new Exception("Cancelled");
/*     */             }
/* 360 */             if (DiskManagerFileInfoFile.channel.this.channel_destroyed)
/*     */             {
/* 362 */               throw new Exception("Destroyed");
/*     */             }
/*     */             
/* 365 */             int chunk = (int)Math.min(rem, this.max_read_chunk);
/*     */             
/* 367 */             int len = raf.read(buffer, 0, chunk);
/*     */             
/* 369 */             sendEvent(new event(new PooledByteBufferImpl(buffer, 0, len), pos, len));
/*     */             
/* 371 */             rem -= len;
/* 372 */             pos += len;
/*     */           }
/*     */           return;
/*     */         } catch (Throwable e) {
/* 376 */           sendEvent(new event(e));
/*     */         }
/*     */         finally
/*     */         {
/* 380 */           if (raf != null) {
/*     */             try
/*     */             {
/* 383 */               raf.close();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 387 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void cancel()
/*     */       {
/* 396 */         this.cancelled = true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       protected void sendEvent(event ev)
/*     */       {
/* 403 */         for (DiskManagerListener l : this.listeners)
/*     */         {
/* 405 */           l.eventOccurred(ev);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void addListener(DiskManagerListener listener)
/*     */       {
/* 413 */         this.listeners.add(listener);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeListener(DiskManagerListener listener)
/*     */       {
/* 420 */         this.listeners.remove(listener);
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
/* 437 */           this.event_type = 2;
/* 438 */           this.error = _error;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         protected event(PooledByteBuffer _buffer, long _offset, int _length)
/*     */         {
/* 447 */           this.event_type = 1;
/* 448 */           this.buffer = _buffer;
/* 449 */           this.event_offset = _offset;
/* 450 */           this.event_length = _length;
/*     */           
/* 452 */           DiskManagerFileInfoFile.channel.this.channel_position = (_offset + _length - 1L);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getType()
/*     */         {
/* 458 */           return this.event_type;
/*     */         }
/*     */         
/*     */ 
/*     */         public DiskManagerRequest getRequest()
/*     */         {
/* 464 */           return DiskManagerFileInfoFile.channel.request.this;
/*     */         }
/*     */         
/*     */ 
/*     */         public long getOffset()
/*     */         {
/* 470 */           return this.event_offset;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getLength()
/*     */         {
/* 476 */           return this.event_length;
/*     */         }
/*     */         
/*     */ 
/*     */         public PooledByteBuffer getBuffer()
/*     */         {
/* 482 */           return this.buffer;
/*     */         }
/*     */         
/*     */ 
/*     */         public Throwable getFailure()
/*     */         {
/* 488 */           return this.error;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/DiskManagerFileInfoFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */