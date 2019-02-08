/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.EventWaiter;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
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
/*     */ public abstract class TransportImpl
/*     */   implements Transport
/*     */ {
/*     */   private TransportHelperFilter filter;
/*  41 */   private static final TransportStats stats = (TransportStats)null;
/*     */   
/*  43 */   private ByteBuffer data_already_read = null;
/*     */   
/*     */   private volatile EventWaiter read_waiter;
/*     */   private volatile EventWaiter write_waiter;
/*  47 */   private volatile boolean is_ready_for_write = false;
/*  48 */   private volatile boolean is_ready_for_read = false;
/*  49 */   private Throwable write_select_failure = null;
/*  50 */   private Throwable read_select_failure = null;
/*     */   
/*  52 */   private long last_ready_for_read = SystemTime.getSteppedMonotonousTime();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean trace;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TransportStartpoint getTransportStartpoint()
/*     */   {
/*  64 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFilter(TransportHelperFilter _filter)
/*     */   {
/*  71 */     this.filter = _filter;
/*     */     
/*  73 */     if ((this.trace) && (_filter != null))
/*     */     {
/*  75 */       _filter.setTrace(true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportHelperFilter getFilter()
/*     */   {
/*  82 */     return this.filter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAlreadyRead(ByteBuffer bytes_already_read)
/*     */   {
/*  89 */     if ((bytes_already_read != null) && (bytes_already_read.hasRemaining()))
/*     */     {
/*  91 */       if (this.data_already_read != null) {
/*  92 */         ByteBuffer new_bb = ByteBuffer.allocate(this.data_already_read.remaining() + bytes_already_read.remaining());
/*  93 */         new_bb.put(bytes_already_read);
/*  94 */         new_bb.put(this.data_already_read);
/*  95 */         new_bb.position(0);
/*  96 */         this.data_already_read = new_bb;
/*     */       }
/*     */       else {
/*  99 */         this.data_already_read = bytes_already_read;
/*     */       }
/* 101 */       this.is_ready_for_read = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getEncryption(boolean verbose)
/*     */   {
/* 109 */     return this.filter == null ? "" : this.filter.getName(verbose);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getProtocol()
/*     */   {
/* 117 */     String s = getEncryption(false);
/*     */     
/* 119 */     int pos = s.indexOf('(');
/*     */     
/* 121 */     if (pos != -1)
/*     */     {
/* 123 */       s = s.substring(pos + 1);
/*     */       
/* 125 */       pos = s.indexOf(')');
/*     */       
/* 127 */       if (pos > 0)
/*     */       {
/* 129 */         return s.substring(0, pos);
/*     */       }
/*     */     }
/*     */     
/* 133 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEncrypted()
/*     */   {
/* 139 */     return this.filter == null ? false : this.filter.isEncrypted();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSOCKS()
/*     */   {
/* 145 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isReadyForWrite(EventWaiter waiter)
/*     */   {
/* 158 */     if (waiter != null)
/*     */     {
/* 160 */       this.write_waiter = waiter;
/*     */     }
/*     */     
/* 163 */     return this.is_ready_for_write;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean readyForWrite(boolean ready)
/*     */   {
/* 170 */     if (this.trace) {
/* 171 */       TimeFormatter.milliTrace("trans: readyForWrite -> " + ready);
/*     */     }
/* 173 */     if (ready) {
/* 174 */       boolean progress = !this.is_ready_for_write;
/* 175 */       this.is_ready_for_write = true;
/* 176 */       EventWaiter ww = this.write_waiter;
/* 177 */       if (ww != null) {
/* 178 */         ww.eventOccurred();
/*     */       }
/* 180 */       return progress;
/*     */     }
/* 182 */     this.is_ready_for_write = false;
/* 183 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeFailed(Throwable msg)
/*     */   {
/* 191 */     msg.fillInStackTrace();
/*     */     
/* 193 */     this.write_select_failure = msg;
/*     */     
/* 195 */     this.is_ready_for_write = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long isReadyForRead(EventWaiter waiter)
/*     */   {
/* 208 */     if (waiter != null) {
/* 209 */       this.read_waiter = waiter;
/*     */     }
/*     */     
/* 212 */     boolean ready = (this.is_ready_for_read) || (this.data_already_read != null) || ((this.filter != null) && (this.filter.hasBufferedRead()));
/*     */     
/*     */ 
/*     */ 
/* 216 */     long now = SystemTime.getSteppedMonotonousTime();
/*     */     
/* 218 */     if (ready)
/*     */     {
/* 220 */       this.last_ready_for_read = now;
/*     */       
/* 222 */       return 0L;
/*     */     }
/*     */     
/* 225 */     long diff = now - this.last_ready_for_read + 1L;
/*     */     
/* 227 */     return diff;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean readyForRead(boolean ready)
/*     */   {
/* 234 */     if (ready) {
/* 235 */       boolean progress = !this.is_ready_for_read;
/* 236 */       this.is_ready_for_read = true;
/* 237 */       EventWaiter rw = this.read_waiter;
/* 238 */       if (rw != null) {
/* 239 */         rw.eventOccurred();
/*     */       }
/* 241 */       return progress;
/*     */     }
/* 243 */     this.is_ready_for_read = false;
/* 244 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReadyForRead()
/*     */   {
/* 251 */     readyForRead(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void readFailed(Throwable msg)
/*     */   {
/* 258 */     msg.fillInStackTrace();
/*     */     
/* 260 */     this.read_select_failure = msg;
/*     */     
/* 262 */     this.is_ready_for_read = true;
/*     */   }
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
/*     */   public long write(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/* 283 */     if (this.write_select_failure != null)
/*     */     {
/* 285 */       throw new IOException("write_select_failure: " + this.write_select_failure.getMessage());
/*     */     }
/*     */     
/* 288 */     if (this.filter == null) { return 0L;
/*     */     }
/* 290 */     long written = this.filter.write(buffers, array_offset, length);
/*     */     
/* 292 */     if (stats != null) { stats.bytesWritten((int)written);
/*     */     }
/* 294 */     if (written < 1L) { requestWriteSelect();
/*     */     }
/* 296 */     return written;
/*     */   }
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
/*     */   public long read(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/* 317 */     if (this.read_select_failure != null)
/*     */     {
/* 319 */       throw new IOException("read_select_failure: " + this.read_select_failure.getMessage());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 324 */     if (this.data_already_read != null)
/*     */     {
/* 326 */       int inserted = 0;
/*     */       
/* 328 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 330 */         ByteBuffer bb = buffers[i];
/*     */         
/* 332 */         int orig_limit = this.data_already_read.limit();
/*     */         
/* 334 */         if (this.data_already_read.remaining() > bb.remaining())
/*     */         {
/* 336 */           this.data_already_read.limit(this.data_already_read.position() + bb.remaining());
/*     */         }
/*     */         
/* 339 */         inserted += this.data_already_read.remaining();
/*     */         
/* 341 */         bb.put(this.data_already_read);
/*     */         
/* 343 */         this.data_already_read.limit(orig_limit);
/*     */         
/* 345 */         if (!this.data_already_read.hasRemaining())
/*     */         {
/* 347 */           this.data_already_read = null;
/*     */           
/* 349 */           break;
/*     */         }
/*     */       }
/*     */       
/* 353 */       if (!buffers[(array_offset + length - 1)].hasRemaining())
/*     */       {
/* 355 */         return inserted;
/*     */       }
/*     */     }
/*     */     
/* 359 */     if (this.filter == null)
/*     */     {
/* 361 */       throw new IOException("Transport not ready");
/*     */     }
/*     */     
/* 364 */     long bytes_read = this.filter.read(buffers, array_offset, length);
/*     */     
/* 366 */     if (stats != null) { stats.bytesRead((int)bytes_read);
/*     */     }
/* 368 */     if (bytes_read == 0L)
/*     */     {
/* 370 */       requestReadSelect();
/*     */     }
/*     */     
/* 373 */     return bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */   private void requestWriteSelect()
/*     */   {
/* 379 */     this.is_ready_for_write = false;
/*     */     
/* 381 */     if (this.filter != null)
/*     */     {
/* 383 */       this.filter.getHelper().resumeWriteSelects();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void requestReadSelect()
/*     */   {
/* 391 */     this.is_ready_for_read = false;
/*     */     
/* 393 */     if (this.filter != null)
/*     */     {
/* 395 */       this.filter.getHelper().resumeReadSelects();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void connectedInbound()
/*     */   {
/* 402 */     registerSelectHandling();
/*     */   }
/*     */   
/*     */ 
/*     */   public void connectedOutbound()
/*     */   {
/* 408 */     registerSelectHandling();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void bindConnection(NetworkConnection connection) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void unbindConnection(NetworkConnection connection) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void registerSelectHandling()
/*     */   {
/* 426 */     TransportHelperFilter filter = getFilter();
/*     */     
/* 428 */     if (filter == null) {
/* 429 */       Debug.out("ERROR: registerSelectHandling():: filter == null");
/* 430 */       return;
/*     */     }
/*     */     
/* 433 */     TransportHelper helper = filter.getHelper();
/*     */     
/*     */ 
/*     */ 
/* 437 */     helper.registerForReadSelects(new TransportHelper.selectListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean selectSuccess(TransportHelper helper, Object attachment)
/*     */       {
/*     */ 
/*     */ 
/* 445 */         return TransportImpl.this.readyForRead(true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 454 */       public void selectFailure(TransportHelper helper, Object attachment, Throwable msg) { TransportImpl.this.readFailed(msg); } }, null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 459 */     helper.registerForWriteSelects(new TransportHelper.selectListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean selectSuccess(TransportHelper helper, Object attachment)
/*     */       {
/*     */ 
/*     */ 
/* 467 */         return TransportImpl.this.readyForWrite(true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 476 */       public void selectFailure(TransportHelper helper, Object attachment, Throwable msg) { TransportImpl.this.writeFailed(msg); } }, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTrace(boolean on)
/*     */   {
/* 487 */     this.trace = on;
/*     */     
/* 489 */     TransportHelperFilter filter = getFilter();
/*     */     
/* 491 */     if (filter != null)
/*     */     {
/* 493 */       filter.setTrace(on);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */