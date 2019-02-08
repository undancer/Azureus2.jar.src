/*     */ package com.aelitis.azureus.core.download;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.QTFastStartRAF;
/*     */ import com.aelitis.azureus.core.util.QTFastStartRAF.FileAccessor;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class DiskManagerFileInfoDelegate
/*     */   implements DiskManagerFileInfo
/*     */ {
/*     */   private DiskManagerFileInfo delegate;
/*     */   private byte[] hash;
/*     */   
/*     */   public DiskManagerFileInfoDelegate(DiskManagerFileInfo _delegate)
/*     */     throws DownloadException
/*     */   {
/*  56 */     this.delegate = _delegate;
/*     */     
/*  58 */     if (this.delegate.getDownload() == null)
/*     */     {
/*  60 */       throw new DownloadException("Not supported");
/*     */     }
/*     */     
/*  63 */     byte[] delegate_hash = this.delegate.getDownloadHash();
/*     */     
/*  65 */     this.hash = ((byte[])delegate_hash.clone()); int 
/*     */     
/*  67 */       tmp57_56 = 0; byte[] tmp57_53 = this.hash;tmp57_53[tmp57_56] = ((byte)(tmp57_53[tmp57_56] ^ 0x1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPriority(boolean b)
/*     */   {
/*  74 */     this.delegate.setPriority(b);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriorty()
/*     */   {
/*  80 */     return this.delegate.getNumericPriority();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriority()
/*     */   {
/*  86 */     return this.delegate.getNumericPriority();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/*  93 */     this.delegate.setNumericPriority(priority);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSkipped(boolean b)
/*     */   {
/* 100 */     this.delegate.setSkipped(b);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDeleted(boolean b)
/*     */   {
/* 106 */     this.delegate.setDeleted(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLink(File link_destination)
/*     */   {
/* 113 */     this.delegate.setLink(link_destination);
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 119 */     return this.delegate.getLink();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 125 */     return this.delegate.getAccessMode();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 131 */     return this.delegate.getDownloaded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 137 */     return this.delegate.getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 143 */     return this.delegate.getFile();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 150 */     return this.delegate.getFile(follow_link);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 156 */     return this.delegate.getIndex();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 162 */     return this.delegate.getFirstPieceNumber();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceSize()
/*     */   {
/* 168 */     return this.delegate.getPieceSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumPieces()
/*     */   {
/* 174 */     return this.delegate.getNumPieces();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPriority()
/*     */   {
/* 180 */     return this.delegate.isPriority();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 186 */     return this.delegate.isSkipped();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 192 */     return this.delegate.isDeleted();
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getDownloadHash()
/*     */   {
/* 198 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 206 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskManagerChannel createChannel()
/*     */     throws DownloadException
/*     */   {
/* 214 */     return new channel(null);
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
/* 226 */     return this.delegate.createRandomReadRequest(file_offset, length, reverse_order, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class channel
/*     */     implements DiskManagerChannel
/*     */   {
/*     */     private DiskManagerChannel delegate_channel;
/*     */     
/*     */     private volatile boolean channel_destroyed;
/*     */     
/*     */ 
/*     */     private channel()
/*     */       throws DownloadException
/*     */     {
/* 242 */       this.delegate_channel = DiskManagerFileInfoDelegate.this.delegate.createChannel();
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerRequest createRequest()
/*     */     {
/* 248 */       return new request(null);
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerFileInfo getFile()
/*     */     {
/* 254 */       return DiskManagerFileInfoDelegate.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getPosition()
/*     */     {
/* 260 */       return this.delegate_channel.getPosition();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDestroyed()
/*     */     {
/* 266 */       return this.delegate_channel.isDestroyed();
/*     */     }
/*     */     
/*     */ 
/*     */     public void destroy()
/*     */     {
/* 272 */       this.delegate_channel.destroy();
/*     */     }
/*     */     
/*     */ 
/*     */     protected class request
/*     */       implements DiskManagerRequest
/*     */     {
/*     */       private DiskManagerRequest delegate_request;
/*     */       
/*     */       private volatile boolean using_delegate;
/*     */       
/*     */       private long offset;
/*     */       
/*     */       private long length;
/*     */       
/*     */       private volatile long position;
/*     */       private String user_agent;
/* 289 */       private int max_read_chunk = 131072;
/*     */       
/*     */ 
/*     */       private volatile boolean cancelled;
/*     */       
/* 294 */       private CopyOnWriteList<DiskManagerListener> listeners = new CopyOnWriteList();
/*     */       
/*     */ 
/*     */       private request()
/*     */       {
/* 299 */         this.delegate_request = DiskManagerFileInfoDelegate.channel.this.delegate_channel.createRequest();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setType(int type)
/*     */       {
/* 306 */         if (type != 1)
/*     */         {
/* 308 */           throw new RuntimeException("Not supported");
/*     */         }
/*     */         
/* 311 */         this.delegate_request.setType(type);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setOffset(long _offset)
/*     */       {
/* 318 */         this.offset = _offset;
/*     */         
/* 320 */         this.delegate_request.setOffset(this.offset);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setLength(long _length)
/*     */       {
/* 327 */         if (_length < 0L)
/*     */         {
/* 329 */           throw new RuntimeException("Illegal argument");
/*     */         }
/*     */         
/* 332 */         this.length = _length;
/*     */         
/* 334 */         this.delegate_request.setLength(this.length);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setMaximumReadChunkSize(int size)
/*     */       {
/* 341 */         this.max_read_chunk = size;
/*     */         
/* 343 */         this.delegate_request.setMaximumReadChunkSize(size);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setUserAgent(String agent)
/*     */       {
/* 350 */         this.user_agent = agent;
/*     */         
/* 352 */         this.delegate_request.setUserAgent(agent);
/*     */       }
/*     */       
/*     */ 
/*     */       public long getAvailableBytes()
/*     */       {
/* 358 */         if (this.using_delegate)
/*     */         {
/* 360 */           return this.delegate_request.getAvailableBytes();
/*     */         }
/*     */         
/* 363 */         return getRemaining();
/*     */       }
/*     */       
/*     */ 
/*     */       public long getRemaining()
/*     */       {
/* 369 */         if (this.using_delegate)
/*     */         {
/* 371 */           return this.delegate_request.getRemaining();
/*     */         }
/*     */         
/* 374 */         return this.offset + this.length - this.position;
/*     */       }
/*     */       
/*     */ 
/*     */       public void run()
/*     */       {
/* 380 */         boolean for_stream = this.user_agent != null;
/*     */         
/* 382 */         if (for_stream)
/*     */         {
/* 384 */           File file = DiskManagerFileInfoDelegate.this.delegate.getFile();
/*     */           
/* 386 */           String name = file.getName();
/*     */           
/* 388 */           int dot_pos = name.lastIndexOf('.');
/*     */           
/* 390 */           String ext = dot_pos < 0 ? "" : name.substring(dot_pos + 1);
/*     */           
/* 392 */           for_stream = QTFastStartRAF.isSupportedExtension(ext);
/*     */         }
/*     */         
/* 395 */         if (for_stream)
/*     */         {
/* 397 */           QTFastStartRAF raf = null;
/*     */           try
/*     */           {
/* 400 */             raf = new QTFastStartRAF(DiskManagerFileInfoDelegate.channel.this.getAccessor(this.max_read_chunk, this.user_agent), true);
/*     */             
/* 402 */             raf.seek(this.offset);
/*     */             
/* 404 */             byte[] buffer = new byte[this.max_read_chunk];
/*     */             
/* 406 */             long rem = this.length;
/* 407 */             long pos = this.offset;
/*     */             
/* 409 */             while (rem > 0L)
/*     */             {
/* 411 */               if (this.cancelled)
/*     */               {
/* 413 */                 throw new Exception("Cancelled");
/*     */               }
/* 415 */               if (DiskManagerFileInfoDelegate.channel.this.channel_destroyed)
/*     */               {
/* 417 */                 throw new Exception("Destroyed");
/*     */               }
/*     */               
/* 420 */               int chunk = (int)Math.min(rem, this.max_read_chunk);
/*     */               
/* 422 */               int len = raf.read(buffer, 0, chunk);
/*     */               
/* 424 */               sendEvent(new event(new PooledByteBufferImpl(buffer, 0, len), pos, len));
/*     */               
/* 426 */               rem -= len;
/* 427 */               pos += len;
/*     */               
/* 429 */               this.position += len;
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 433 */             sendEvent(new event(e));
/*     */           }
/*     */           finally
/*     */           {
/* 437 */             if (raf != null) {
/*     */               try
/*     */               {
/* 440 */                 raf.close();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 444 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 450 */         this.using_delegate = true;
/*     */         
/* 452 */         this.delegate_request.addListener(new DiskManagerListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void eventOccurred(DiskManagerEvent event)
/*     */           {
/*     */ 
/* 459 */             DiskManagerFileInfoDelegate.channel.request.this.sendEvent(event);
/*     */           }
/*     */           
/* 462 */         });
/* 463 */         this.delegate_request.run();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void cancel()
/*     */       {
/* 470 */         this.cancelled = true;
/*     */         
/* 472 */         this.delegate_request.cancel();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       protected void sendEvent(DiskManagerEvent ev)
/*     */       {
/* 479 */         for (DiskManagerListener l : this.listeners)
/*     */         {
/* 481 */           l.eventOccurred(ev);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void addListener(DiskManagerListener listener)
/*     */       {
/* 489 */         this.listeners.add(listener);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeListener(DiskManagerListener listener)
/*     */       {
/* 496 */         this.listeners.remove(listener);
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
/* 513 */           this.event_type = 2;
/* 514 */           this.error = _error;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         protected event(PooledByteBuffer _buffer, long _offset, int _length)
/*     */         {
/* 523 */           this.event_type = 1;
/* 524 */           this.buffer = _buffer;
/* 525 */           this.event_offset = _offset;
/* 526 */           this.event_length = _length;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getType()
/*     */         {
/* 532 */           return this.event_type;
/*     */         }
/*     */         
/*     */ 
/*     */         public long getOffset()
/*     */         {
/* 538 */           return this.event_offset;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getLength()
/*     */         {
/* 544 */           return this.event_length;
/*     */         }
/*     */         
/*     */ 
/*     */         public PooledByteBuffer getBuffer()
/*     */         {
/* 550 */           return this.buffer;
/*     */         }
/*     */         
/*     */ 
/*     */         public Throwable getFailure()
/*     */         {
/* 556 */           return this.error;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private QTFastStartRAF.FileAccessor getAccessor(final int max_req_size, final String user_agent)
/*     */     {
/* 566 */       new QTFastStartRAF.FileAccessor()
/*     */       {
/*     */         private long seek_position;
/*     */         
/*     */         private DiskManagerRequest current_request;
/*     */         private volatile boolean closed;
/*     */         
/*     */         public String getName()
/*     */         {
/*     */           try
/*     */           {
/* 577 */             return DiskManagerFileInfoDelegate.this.delegate.getDownload().getName() + "/" + DiskManagerFileInfoDelegate.this.delegate.getFile().getName();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 581 */             Debug.out(e);
/*     */           }
/* 583 */           return DiskManagerFileInfoDelegate.this.delegate.getFile().getAbsolutePath();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public long getFilePointer()
/*     */           throws IOException
/*     */         {
/* 592 */           return this.seek_position;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void seek(long pos)
/*     */           throws IOException
/*     */         {
/* 601 */           this.seek_position = pos;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void skipBytes(int num)
/*     */           throws IOException
/*     */         {
/* 610 */           this.seek_position += num;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public long length()
/*     */           throws IOException
/*     */         {
/* 618 */           return DiskManagerFileInfoDelegate.this.getLength();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public int read(final byte[] buffer, final int pos, final int len)
/*     */           throws IOException
/*     */         {
/* 629 */           synchronized (this)
/*     */           {
/* 631 */             if (this.closed)
/*     */             {
/* 633 */               throw new IOException("closed");
/*     */             }
/*     */             
/* 636 */             this.current_request = DiskManagerFileInfoDelegate.channel.this.delegate_channel.createRequest();
/*     */           }
/*     */           
/* 639 */           this.current_request.setType(1);
/* 640 */           this.current_request.setOffset(this.seek_position);
/* 641 */           this.current_request.setLength(len);
/*     */           
/* 643 */           this.current_request.setMaximumReadChunkSize(max_req_size);
/*     */           
/* 645 */           if (user_agent != null)
/*     */           {
/* 647 */             this.current_request.setUserAgent(user_agent);
/*     */           }
/*     */           
/* 650 */           final AESemaphore sem = new AESemaphore("waiter");
/* 651 */           final Throwable[] error = { null };
/*     */           
/* 653 */           this.current_request.addListener(new DiskManagerListener()
/*     */           {
/*     */ 
/* 656 */             private int write_pos = pos;
/* 657 */             private int rem = len;
/*     */             
/*     */ 
/*     */ 
/*     */             public void eventOccurred(DiskManagerEvent event)
/*     */             {
/* 663 */               int type = event.getType();
/*     */               
/* 665 */               if (type == 1)
/*     */               {
/* 667 */                 PooledByteBuffer p_buffer = event.getBuffer();
/*     */                 try
/*     */                 {
/* 670 */                   ByteBuffer bb = p_buffer.toByteBuffer();
/*     */                   
/* 672 */                   bb.position(0);
/*     */                   
/* 674 */                   int read = bb.remaining();
/*     */                   
/* 676 */                   bb.get(buffer, this.write_pos, read);
/*     */                   
/* 678 */                   this.write_pos += read;
/* 679 */                   this.rem -= read;
/*     */                   
/* 681 */                   if (this.rem == 0)
/*     */                   {
/* 683 */                     sem.release();
/*     */                   }
/*     */                 }
/*     */                 finally {
/* 687 */                   p_buffer.returnToPool();
/*     */                 }
/* 689 */               } else if (type == 2)
/*     */               {
/* 691 */                 error[0] = event.getFailure();
/*     */                 
/* 693 */                 sem.release();
/*     */               }
/*     */               
/*     */             }
/* 697 */           });
/* 698 */           this.current_request.run();
/*     */           
/*     */           do
/*     */           {
/* 702 */             if (sem.reserve(1000L))
/*     */             {
/* 704 */               if (error[0] != null)
/*     */               {
/* 706 */                 throw new IOException(Debug.getNestedExceptionMessage(error[0]));
/*     */               }
/*     */               
/* 709 */               this.seek_position += len;
/*     */               
/* 711 */               return len;
/*     */             }
/*     */             
/*     */           }
/* 715 */           while (!this.closed);
/*     */           
/* 717 */           throw new IOException("Closed");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public int readInt()
/*     */           throws IOException
/*     */         {
/* 728 */           byte[] readBuffer = new byte[4];
/*     */           
/* 730 */           readFully(readBuffer);
/*     */           
/* 732 */           return (readBuffer[0] << 24) + ((readBuffer[1] & 0xFF) << 16) + ((readBuffer[2] & 0xFF) << 8) + ((readBuffer[3] & 0xFF) << 0);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public long readLong()
/*     */           throws IOException
/*     */         {
/* 743 */           byte[] readBuffer = new byte[8];
/*     */           
/* 745 */           readFully(readBuffer);
/*     */           
/* 747 */           return (readBuffer[0] << 56) + ((readBuffer[1] & 0xFF) << 48) + ((readBuffer[2] & 0xFF) << 40) + ((readBuffer[3] & 0xFF) << 32) + ((readBuffer[4] & 0xFF) << 24) + ((readBuffer[5] & 0xFF) << 16) + ((readBuffer[6] & 0xFF) << 8) + ((readBuffer[7] & 0xFF) << 0);
/*     */         }
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
/*     */         public void readFully(byte[] buffer)
/*     */           throws IOException
/*     */         {
/* 763 */           read(buffer, 0, buffer.length);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void close()
/*     */           throws IOException
/*     */         {
/* 771 */           synchronized (this)
/*     */           {
/* 773 */             this.closed = true;
/*     */             
/* 775 */             if (this.current_request != null)
/*     */             {
/* 777 */               this.current_request.cancel();
/*     */             }
/*     */           }
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/DiskManagerFileInfoDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */