/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper.selectListener;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ public class UDPTransportHelper
/*     */   implements TransportHelper
/*     */ {
/*     */   public static final int READ_TIMEOUT = 30000;
/*     */   public static final int CONNECT_TIMEOUT = 20000;
/*     */   private final UDPConnectionManager manager;
/*     */   private UDPSelector selector;
/*     */   private final InetSocketAddress address;
/*     */   private UDPTransport transport;
/*     */   private final boolean incoming;
/*     */   private UDPConnection connection;
/*     */   private TransportHelper.selectListener read_listener;
/*     */   private Object read_attachment;
/*     */   private boolean read_selects_paused;
/*     */   private TransportHelper.selectListener write_listener;
/*     */   private Object write_attachment;
/*  54 */   private boolean write_selects_paused = true;
/*     */   
/*     */ 
/*     */   private boolean closed;
/*     */   
/*     */ 
/*     */   private IOException failed;
/*     */   
/*     */ 
/*     */   private ByteBuffer[] pending_partial_writes;
/*     */   
/*     */ 
/*     */   private Map user_data;
/*     */   
/*     */ 
/*     */ 
/*     */   public UDPTransportHelper(UDPConnectionManager _manager, InetSocketAddress _address, UDPTransport _transport)
/*     */     throws IOException
/*     */   {
/*  73 */     this.manager = _manager;
/*  74 */     this.address = _address;
/*  75 */     this.transport = _transport;
/*     */     
/*  77 */     this.incoming = false;
/*     */     
/*  79 */     this.connection = this.manager.registerOutgoing(this);
/*     */     
/*  81 */     this.selector = this.connection.getSelector();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UDPTransportHelper(UDPConnectionManager _manager, InetSocketAddress _address, UDPConnection _connection)
/*     */   {
/*  93 */     this.manager = _manager;
/*  94 */     this.address = _address;
/*  95 */     this.connection = _connection;
/*     */     
/*  97 */     this.incoming = true;
/*     */     
/*  99 */     this.selector = this.connection.getSelector();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTransport(UDPTransport _transport)
/*     */   {
/* 106 */     this.transport = _transport;
/*     */   }
/*     */   
/*     */ 
/*     */   protected UDPTransport getTransport()
/*     */   {
/* 112 */     return this.transport;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getMss()
/*     */   {
/* 118 */     if (this.transport == null)
/*     */     {
/* 120 */       return UDPNetworkManager.getUdpMssSize();
/*     */     }
/*     */     
/* 123 */     return this.transport.getMssSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean minimiseOverheads()
/*     */   {
/* 129 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectTimeout()
/*     */   {
/* 135 */     return 20000;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getReadTimeout()
/*     */   {
/* 141 */     return 30000;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/* 147 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName(boolean verbose)
/*     */   {
/* 154 */     return "UDP";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isIncoming()
/*     */   {
/* 160 */     return this.incoming;
/*     */   }
/*     */   
/*     */ 
/*     */   protected UDPConnection getConnection()
/*     */   {
/* 166 */     return this.connection;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean delayWrite(ByteBuffer buffer)
/*     */   {
/* 173 */     if (this.pending_partial_writes == null)
/*     */     {
/* 175 */       this.pending_partial_writes = new ByteBuffer[] { buffer };
/*     */       
/* 177 */       return true;
/*     */     }
/*     */     
/* 180 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasDelayedWrite()
/*     */   {
/* 186 */     return this.pending_partial_writes != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int write(ByteBuffer buffer, boolean partial_write)
/*     */     throws IOException
/*     */   {
/* 196 */     synchronized (this)
/*     */     {
/* 198 */       if (this.failed != null)
/*     */       {
/* 200 */         throw this.failed;
/*     */       }
/*     */       
/* 203 */       if (this.closed)
/*     */       {
/* 205 */         throw new IOException("Transport closed");
/*     */       }
/*     */     }
/*     */     
/* 209 */     int buffer_rem = buffer.remaining();
/*     */     
/* 211 */     if ((partial_write) && (buffer_rem < 128))
/*     */     {
/* 213 */       if (this.pending_partial_writes == null)
/*     */       {
/* 215 */         this.pending_partial_writes = new ByteBuffer[1];
/*     */         
/* 217 */         ByteBuffer copy = ByteBuffer.allocate(buffer_rem);
/*     */         
/* 219 */         copy.put(buffer);
/*     */         
/* 221 */         copy.position(0);
/*     */         
/* 223 */         this.pending_partial_writes[0] = copy;
/*     */         
/* 225 */         return buffer_rem;
/*     */       }
/*     */       
/*     */ 
/* 229 */       int queued = 0;
/*     */       
/* 231 */       for (int i = 0; i < this.pending_partial_writes.length; i++)
/*     */       {
/* 233 */         queued += this.pending_partial_writes[i].remaining();
/*     */       }
/*     */       
/* 236 */       if (queued + buffer_rem <= 512)
/*     */       {
/* 238 */         ByteBuffer[] new_ppw = new ByteBuffer[this.pending_partial_writes.length + 1];
/*     */         
/* 240 */         System.arraycopy(this.pending_partial_writes, 0, new_ppw, 0, this.pending_partial_writes.length);
/*     */         
/* 242 */         ByteBuffer copy = ByteBuffer.allocate(buffer_rem);
/*     */         
/* 244 */         copy.put(buffer);
/*     */         
/* 246 */         copy.position(0);
/*     */         
/* 248 */         new_ppw[this.pending_partial_writes.length] = copy;
/*     */         
/* 250 */         this.pending_partial_writes = new_ppw;
/*     */         
/* 252 */         return buffer_rem;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 257 */     if (this.pending_partial_writes != null)
/*     */     {
/* 259 */       int ppw_len = this.pending_partial_writes.length;
/* 260 */       int ppw_rem = 0;
/*     */       
/* 262 */       ByteBuffer[] buffers2 = new ByteBuffer[ppw_len + 1];
/*     */       
/* 264 */       for (int i = 0; i < ppw_len; i++)
/*     */       {
/* 266 */         buffers2[i] = this.pending_partial_writes[i];
/*     */         
/* 268 */         ppw_rem += buffers2[i].remaining();
/*     */       }
/*     */       
/* 271 */       buffers2[ppw_len] = buffer;
/*     */       try
/*     */       {
/* 274 */         int written = this.connection.write(buffers2, 0, buffers2.length);
/*     */         int i;
/* 276 */         if (written >= ppw_rem) {
/*     */           int i;
/* 278 */           return written - ppw_rem;
/*     */         }
/*     */         
/*     */         int i;
/* 282 */         return 0;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 287 */         ppw_rem = 0;
/*     */         
/* 289 */         for (int i = 0; i < ppw_len; i++)
/*     */         {
/* 291 */           ppw_rem += buffers2[i].remaining();
/*     */         }
/*     */         
/* 294 */         if (ppw_rem == 0)
/*     */         {
/* 296 */           this.pending_partial_writes = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 301 */     return this.connection.write(new ByteBuffer[] { buffer }, 0, 1);
/*     */   }
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
/* 313 */     synchronized (this)
/*     */     {
/* 315 */       if (this.failed != null)
/*     */       {
/* 317 */         throw this.failed;
/*     */       }
/*     */       
/* 320 */       if (this.closed)
/*     */       {
/* 322 */         throw new IOException("Transport closed");
/*     */       }
/*     */     }
/*     */     
/* 326 */     if (this.pending_partial_writes != null)
/*     */     {
/* 328 */       int ppw_len = this.pending_partial_writes.length;
/* 329 */       int ppw_rem = 0;
/*     */       
/* 331 */       ByteBuffer[] buffers2 = new ByteBuffer[length + ppw_len];
/*     */       
/* 333 */       for (int i = 0; i < ppw_len; i++)
/*     */       {
/* 335 */         buffers2[i] = this.pending_partial_writes[i];
/*     */         
/* 337 */         ppw_rem += buffers2[i].remaining();
/*     */       }
/*     */       
/* 340 */       int pos = ppw_len;
/*     */       
/* 342 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 344 */         buffers2[(pos++)] = buffers[i];
/*     */       }
/*     */       try
/*     */       {
/* 348 */         int written = this.connection.write(buffers2, 0, buffers2.length);
/*     */         long l;
/* 350 */         if (written >= ppw_rem) {
/*     */           int i;
/* 352 */           return written - ppw_rem;
/*     */         }
/*     */         
/*     */         int i;
/* 356 */         return 0L;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/* 361 */         ppw_rem = 0;
/*     */         
/* 363 */         for (int i = 0; i < ppw_len; i++)
/*     */         {
/* 365 */           ppw_rem += buffers2[i].remaining();
/*     */         }
/*     */         
/* 368 */         if (ppw_rem == 0)
/*     */         {
/* 370 */           this.pending_partial_writes = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 375 */     return this.connection.write(buffers, array_offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 385 */     synchronized (this)
/*     */     {
/* 387 */       if (this.failed != null)
/*     */       {
/* 389 */         throw this.failed;
/*     */       }
/*     */       
/* 392 */       if (this.closed)
/*     */       {
/* 394 */         throw new IOException("Transport closed");
/*     */       }
/*     */     }
/*     */     
/* 398 */     return this.connection.read(buffer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long read(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/* 409 */     synchronized (this)
/*     */     {
/* 411 */       if (this.failed != null)
/*     */       {
/* 413 */         throw this.failed;
/*     */       }
/*     */       
/* 416 */       if (this.closed)
/*     */       {
/* 418 */         throw new IOException("Transport closed");
/*     */       }
/*     */     }
/*     */     
/* 422 */     long total = 0L;
/*     */     
/* 424 */     for (int i = array_offset; i < array_offset + length; i++)
/*     */     {
/* 426 */       ByteBuffer buffer = buffers[i];
/*     */       
/* 428 */       int max = buffer.remaining();
/*     */       
/* 430 */       int read = this.connection.read(buffer);
/*     */       
/* 432 */       total += read;
/*     */       
/* 434 */       if (read < max) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 440 */     return total;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void canRead()
/*     */   {
/* 446 */     fireReadSelect();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void canWrite()
/*     */   {
/* 452 */     fireWriteSelect();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void pauseReadSelects()
/*     */   {
/* 458 */     if (this.read_listener != null)
/*     */     {
/* 460 */       this.selector.cancel(this, this.read_listener);
/*     */     }
/*     */     
/* 463 */     this.read_selects_paused = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void pauseWriteSelects()
/*     */   {
/* 469 */     if (this.write_listener != null)
/*     */     {
/* 471 */       this.selector.cancel(this, this.write_listener);
/*     */     }
/*     */     
/* 474 */     this.write_selects_paused = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void resumeReadSelects()
/*     */   {
/* 480 */     this.read_selects_paused = false;
/*     */     
/* 482 */     fireReadSelect();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void resumeWriteSelects()
/*     */   {
/* 488 */     this.write_selects_paused = false;
/*     */     
/* 490 */     fireWriteSelect();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerForReadSelects(TransportHelper.selectListener listener, Object attachment)
/*     */   {
/* 498 */     synchronized (this)
/*     */     {
/* 500 */       this.read_listener = listener;
/* 501 */       this.read_attachment = attachment;
/*     */     }
/*     */     
/* 504 */     resumeReadSelects();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerForWriteSelects(TransportHelper.selectListener listener, Object attachment)
/*     */   {
/* 512 */     synchronized (this)
/*     */     {
/* 514 */       this.write_listener = listener;
/* 515 */       this.write_attachment = attachment;
/*     */     }
/*     */     
/* 518 */     resumeWriteSelects();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void cancelReadSelects()
/*     */   {
/* 524 */     this.selector.cancel(this, this.read_listener);
/*     */     
/* 526 */     this.read_selects_paused = true;
/* 527 */     this.read_listener = null;
/* 528 */     this.read_attachment = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void cancelWriteSelects()
/*     */   {
/* 534 */     this.selector.cancel(this, this.write_listener);
/*     */     
/* 536 */     this.write_selects_paused = true;
/* 537 */     this.write_listener = null;
/* 538 */     this.write_attachment = null;
/*     */   }
/*     */   
/*     */   protected void fireReadSelect()
/*     */   {
/*     */     try
/*     */     {
/* 545 */       synchronized (this)
/*     */       {
/* 547 */         if ((this.read_listener != null) && (!this.read_selects_paused))
/*     */         {
/* 549 */           if (this.failed != null)
/*     */           {
/* 551 */             this.selector.ready(this, this.read_listener, this.read_attachment, this.failed);
/*     */           }
/* 553 */           else if (this.closed)
/*     */           {
/* 555 */             this.selector.ready(this, this.read_listener, this.read_attachment, new Throwable("Transport closed"));
/*     */           }
/* 557 */           else if (this.connection.canRead())
/*     */           {
/* 559 */             this.selector.ready(this, this.read_listener, this.read_attachment);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */       boolean report;
/*     */       
/*     */ 
/*     */ 
/* 570 */       synchronized (this)
/*     */       {
/* 572 */         report = this.failed == null;
/*     */         
/* 574 */         if (report)
/*     */         {
/* 576 */           this.failed = e;
/*     */         }
/*     */       }
/*     */       
/* 580 */       if (report)
/*     */       {
/* 582 */         this.connection.failedSupport(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void fireWriteSelect()
/*     */   {
/*     */     try
/*     */     {
/* 591 */       synchronized (this)
/*     */       {
/* 593 */         if ((this.write_listener != null) && (!this.write_selects_paused))
/*     */         {
/* 595 */           if (this.failed != null)
/*     */           {
/* 597 */             this.write_selects_paused = true;
/*     */             
/* 599 */             this.selector.ready(this, this.write_listener, this.write_attachment, this.failed);
/*     */           }
/* 601 */           else if (this.closed)
/*     */           {
/* 603 */             this.write_selects_paused = true;
/*     */             
/* 605 */             this.selector.ready(this, this.write_listener, this.write_attachment, new Throwable("Transport closed"));
/*     */           }
/* 607 */           else if (this.connection.canWrite())
/*     */           {
/* 609 */             this.write_selects_paused = true;
/*     */             
/* 611 */             this.selector.ready(this, this.write_listener, this.write_attachment);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */       boolean report;
/*     */       
/*     */ 
/*     */ 
/* 622 */       synchronized (this)
/*     */       {
/* 624 */         report = this.failed == null;
/*     */         
/* 626 */         if (report)
/*     */         {
/* 628 */           this.failed = e;
/*     */         }
/*     */       }
/*     */       
/* 632 */       if (report)
/*     */       {
/* 634 */         this.connection.failedSupport(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void failed(Throwable reason)
/*     */   {
/* 643 */     synchronized (this)
/*     */     {
/* 645 */       if ((reason instanceof IOException))
/*     */       {
/* 647 */         this.failed = ((IOException)reason);
/*     */       }
/*     */       else
/*     */       {
/* 651 */         this.failed = new IOException(Debug.getNestedExceptionMessageAndStack(reason));
/*     */       }
/*     */       
/* 654 */       fireReadSelect();
/* 655 */       fireWriteSelect();
/*     */     }
/*     */     
/* 658 */     this.connection.failedSupport(reason);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean isClosed()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 235	com/aelitis/azureus/core/networkmanager/impl/udp/UDPTransportHelper:closed	Z
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #664	-> byte code offset #0
/*     */     //   Java source line #666	-> byte code offset #4
/*     */     //   Java source line #667	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	UDPTransportHelper
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   public void close(String reason)
/*     */   {
/* 674 */     synchronized (this)
/*     */     {
/* 676 */       this.closed = true;
/*     */       
/* 678 */       fireReadSelect();
/* 679 */       fireWriteSelect();
/*     */     }
/*     */     
/* 682 */     this.connection.closeSupport(reason);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void poll()
/*     */   {
/* 688 */     synchronized (this)
/*     */     {
/* 690 */       fireReadSelect();
/*     */       
/* 692 */       fireWriteSelect();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setUserData(Object key, Object data)
/*     */   {
/* 701 */     if (this.user_data == null)
/*     */     {
/* 703 */       this.user_data = new HashMap();
/*     */     }
/*     */     
/* 706 */     this.user_data.put(key, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized Object getUserData(Object key)
/*     */   {
/* 713 */     if (this.user_data == null)
/*     */     {
/* 715 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 719 */     return this.user_data.get(key);
/*     */   }
/*     */   
/*     */   public void setTrace(boolean on) {}
/*     */   
/*     */   public void setScatteringMode(long forBytes) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPTransportHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */