/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper.selectListener;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper.AppliedPortMapping;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class TCPTransportHelper
/*     */   implements TransportHelper
/*     */ {
/*     */   public static final int READ_TIMEOUT = 10000;
/*     */   public static final int CONNECT_TIMEOUT = 20000;
/*  50 */   private static final AEProxyAddressMapper proxy_address_mapper = ;
/*     */   
/*     */   public static final int MAX_PARTIAL_WRITE_RETAIN = 64;
/*     */   
/*  54 */   private long remainingBytesToScatter = 0L;
/*     */   
/*  56 */   private static boolean enable_efficient_io = !Constants.JAVA_VERSION.startsWith("1.4");
/*     */   
/*     */   private final SocketChannel channel;
/*     */   
/*     */   private ByteBuffer delayed_write;
/*     */   
/*     */   private Map user_data;
/*     */   
/*     */   private boolean trace;
/*     */   
/*     */   private volatile InetSocketAddress tcp_address;
/*     */   private volatile boolean closed;
/*     */   
/*     */   public TCPTransportHelper(SocketChannel _channel)
/*     */   {
/*  71 */     this.channel = _channel;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getAddress()
/*     */   {
/*  77 */     if (this.tcp_address != null)
/*     */     {
/*  79 */       return this.tcp_address;
/*     */     }
/*     */     
/*  82 */     Socket socket = this.channel.socket();
/*     */     
/*  84 */     AEProxyAddressMapper.AppliedPortMapping applied_mapping = proxy_address_mapper.applyPortMapping(socket.getInetAddress(), socket.getPort());
/*     */     
/*  86 */     InetSocketAddress tcp_address = applied_mapping.getAddress();
/*     */     
/*  88 */     return tcp_address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName(boolean verbose)
/*     */   {
/*  96 */     if (verbose)
/*     */     {
/*  98 */       return "TCP";
/*     */     }
/*     */     
/*     */ 
/* 102 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean minimiseOverheads()
/*     */   {
/* 109 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectTimeout()
/*     */   {
/* 115 */     return 20000;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getReadTimeout()
/*     */   {
/* 121 */     return 10000;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean delayWrite(ByteBuffer buffer)
/*     */   {
/* 128 */     if (this.delayed_write != null)
/*     */     {
/* 130 */       Debug.out("secondary delayed write");
/*     */       
/* 132 */       return false;
/*     */     }
/*     */     
/* 135 */     this.delayed_write = buffer;
/*     */     
/* 137 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasDelayedWrite()
/*     */   {
/* 143 */     return this.delayed_write != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int write(ByteBuffer buffer, boolean partial_write)
/*     */     throws IOException
/*     */   {
/* 153 */     if (this.channel == null)
/*     */     {
/* 155 */       Debug.out("channel == null");
/*     */       
/* 157 */       return 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 162 */     if ((partial_write) && (this.delayed_write == null))
/*     */     {
/* 164 */       if (buffer.remaining() < 64)
/*     */       {
/* 166 */         ByteBuffer copy = ByteBuffer.allocate(buffer.remaining());
/*     */         
/* 168 */         copy.put(buffer);
/*     */         
/* 170 */         copy.position(0);
/*     */         
/* 172 */         this.delayed_write = copy;
/*     */         
/* 174 */         return copy.remaining();
/*     */       }
/*     */     }
/*     */     
/* 178 */     long written = 0L;
/*     */     
/* 180 */     if (this.delayed_write != null)
/*     */     {
/*     */ 
/*     */ 
/* 184 */       ByteBuffer[] buffers = { this.delayed_write, buffer };
/*     */       
/* 186 */       int delay_remaining = this.delayed_write.remaining();
/*     */       
/* 188 */       this.delayed_write = null;
/*     */       
/* 190 */       written = write(buffers, 0, 2);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 195 */       if (buffers[0].hasRemaining())
/*     */       {
/* 197 */         this.delayed_write = buffers[0];
/*     */         
/* 199 */         written = 0L;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 204 */         written -= delay_remaining;
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 210 */       written = channelWrite(buffer);
/*     */     }
/*     */     
/* 213 */     if (this.trace)
/*     */     {
/* 215 */       TimeFormatter.milliTrace("tcp: write " + written);
/*     */     }
/*     */     
/* 218 */     return (int)written;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long write(ByteBuffer[] buffers, int array_offset, int length)
/*     */     throws IOException
/*     */   {
/* 229 */     if (this.channel == null)
/*     */     {
/* 231 */       Debug.out("channel == null");
/*     */       
/* 233 */       return 0L;
/*     */     }
/*     */     
/* 236 */     long written_sofar = 0L;
/*     */     
/* 238 */     if (this.delayed_write != null)
/*     */     {
/* 240 */       ByteBuffer[] buffers2 = new ByteBuffer[length + 1];
/*     */       
/* 242 */       buffers2[0] = this.delayed_write;
/*     */       
/* 244 */       int pos = 1;
/*     */       
/* 246 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 248 */         buffers2[(pos++)] = buffers[i];
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 253 */       int delay_remaining = this.delayed_write.remaining();
/*     */       
/* 255 */       this.delayed_write = null;
/*     */       
/* 257 */       written_sofar = write(buffers2, 0, buffers2.length);
/*     */       
/* 259 */       if (buffers2[0].hasRemaining())
/*     */       {
/* 261 */         this.delayed_write = buffers2[0];
/*     */         
/* 263 */         written_sofar = 0L;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 269 */         written_sofar -= delay_remaining;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 275 */     else if ((enable_efficient_io) && (this.remainingBytesToScatter < 1L))
/*     */     {
/*     */       try {
/* 278 */         written_sofar = this.channel.write(buffers, array_offset, length);
/*     */ 
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/*     */ 
/* 284 */         String msg = ioe.getMessage();
/* 285 */         if ((msg != null) && (msg.equals("A non-blocking socket operation could not be completed immediately"))) {
/* 286 */           enable_efficient_io = false;
/* 287 */           Logger.log(new LogAlert(false, 1, "WARNING: Multi-buffer socket write failed; switching to single-buffer mode.\nUpgrade to JRE 1.5 (5.0) series to fix this problem!"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 292 */         throw ioe;
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 298 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 300 */         int data_length = buffers[i].remaining();
/*     */         
/* 302 */         int written = channelWrite(buffers[i]);
/*     */         
/* 304 */         written_sofar += written;
/*     */         
/* 306 */         if (written < data_length) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 315 */     if (this.trace) {
/* 316 */       TimeFormatter.milliTrace("tcp: write " + written_sofar);
/*     */     }
/*     */     
/* 319 */     return written_sofar;
/*     */   }
/*     */   
/* 322 */   private static final Random rnd = new Random();
/*     */   
/*     */   private int channelWrite(ByteBuffer buf) throws IOException
/*     */   {
/* 326 */     int written = 0;
/* 327 */     while ((this.remainingBytesToScatter > 0L) && (buf.remaining() > 0))
/*     */     {
/* 329 */       int currentWritten = this.channel.write((ByteBuffer)buf.slice().limit(Math.min(50 + rnd.nextInt(100), buf.remaining())));
/* 330 */       if (currentWritten == 0)
/*     */         break;
/* 332 */       buf.position(buf.position() + currentWritten);
/* 333 */       this.remainingBytesToScatter -= currentWritten;
/* 334 */       if (this.remainingBytesToScatter <= 0L)
/*     */       {
/* 336 */         this.remainingBytesToScatter = 0L;
/*     */         try
/*     */         {
/* 339 */           this.channel.socket().setTcpNoDelay(false);
/*     */         }
/*     */         catch (SocketException e) {
/* 342 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/* 345 */       written += currentWritten;
/*     */     }
/*     */     
/* 348 */     if (buf.remaining() > 0) {
/* 349 */       written += this.channel.write(buf);
/*     */     }
/* 351 */     return written;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int read(ByteBuffer buffer)
/*     */     throws IOException
/*     */   {
/* 360 */     if (this.channel == null)
/*     */     {
/* 362 */       Debug.out("channel == null");
/*     */       
/* 364 */       return 0;
/*     */     }
/*     */     
/* 367 */     int res = this.channel.read(buffer);
/*     */     
/* 369 */     if (this.trace) {
/* 370 */       TimeFormatter.milliTrace("tcp: read " + res);
/*     */     }
/*     */     
/* 373 */     return res;
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
/* 384 */     if (this.channel == null) {
/* 385 */       Debug.out("channel == null");
/* 386 */       return 0L;
/*     */     }
/*     */     
/* 389 */     if (buffers == null) {
/* 390 */       Debug.out("read: buffers == null");
/* 391 */       return 0L;
/*     */     }
/*     */     
/* 394 */     long bytes_read = 0L;
/*     */     
/* 396 */     if (enable_efficient_io) {
/*     */       try {
/* 398 */         bytes_read = this.channel.read(buffers, array_offset, length);
/*     */ 
/*     */       }
/*     */       catch (IOException ioe)
/*     */       {
/* 403 */         String msg = ioe.getMessage();
/* 404 */         if ((msg != null) && (msg.equals("A non-blocking socket operation could not be completed immediately"))) {
/* 405 */           enable_efficient_io = false;
/* 406 */           Logger.log(new LogAlert(false, 1, "WARNING: Multi-buffer socket read failed; switching to single-buffer mode.\nUpgrade to JRE 1.5 (5.0) series to fix this problem!"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 411 */         throw ioe;
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/* 416 */       for (int i = array_offset; i < array_offset + length; i++)
/*     */       {
/* 418 */         int data_length = buffers[i].remaining();
/*     */         
/* 420 */         int read = this.channel.read(buffers[i]);
/*     */         
/* 422 */         bytes_read += read;
/*     */         
/* 424 */         if (read < data_length) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 431 */     if (bytes_read < 0L)
/*     */     {
/* 433 */       throw new IOException("end of stream on socket read");
/*     */     }
/*     */     
/* 436 */     if (this.trace) {
/* 437 */       TimeFormatter.milliTrace("tcp: read " + bytes_read);
/*     */     }
/*     */     
/* 440 */     return bytes_read;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerForReadSelects(final TransportHelper.selectListener listener, Object attachment)
/*     */   {
/* 448 */     TCPNetworkManager.getSingleton().getReadSelector().register(this.channel, new VirtualChannelSelector.VirtualSelectorListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 458 */         return listener.selectSuccess(TCPTransportHelper.this, attachment);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 468 */       public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg) { listener.selectFailure(TCPTransportHelper.this, attachment, msg); } }, attachment);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerForWriteSelects(final TransportHelper.selectListener listener, Object attachment)
/*     */   {
/* 479 */     TCPNetworkManager.getSingleton().getWriteSelector().register(this.channel, new VirtualChannelSelector.VirtualSelectorListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 489 */         if (TCPTransportHelper.this.trace) {
/* 490 */           TimeFormatter.milliTrace("tcp: write select");
/*     */         }
/*     */         
/* 493 */         return listener.selectSuccess(TCPTransportHelper.this, attachment);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 503 */       public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg) { listener.selectFailure(TCPTransportHelper.this, attachment, msg); } }, attachment);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cancelReadSelects()
/*     */   {
/* 512 */     TCPNetworkManager.getSingleton().getReadSelector().cancel(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancelWriteSelects()
/*     */   {
/* 518 */     if (this.trace) {
/* 519 */       TimeFormatter.milliTrace("tcp: cancel write selects");
/*     */     }
/*     */     
/* 522 */     TCPNetworkManager.getSingleton().getWriteSelector().cancel(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void resumeReadSelects()
/*     */   {
/* 528 */     TCPNetworkManager.getSingleton().getReadSelector().resumeSelects(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void resumeWriteSelects()
/*     */   {
/* 534 */     if (this.trace) {
/* 535 */       TimeFormatter.milliTrace("tcp: resume write selects");
/*     */     }
/*     */     
/* 538 */     TCPNetworkManager.getSingleton().getWriteSelector().resumeSelects(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void pauseReadSelects()
/*     */   {
/* 544 */     TCPNetworkManager.getSingleton().getReadSelector().pauseSelects(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void pauseWriteSelects()
/*     */   {
/* 550 */     if (this.trace) {
/* 551 */       TimeFormatter.milliTrace("tcp: pause write selects");
/*     */     }
/*     */     
/* 554 */     TCPNetworkManager.getSingleton().getWriteSelector().pauseSelects(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 560 */     return this.closed;
/*     */   }
/*     */   
/*     */ 
/*     */   public void close(String reason)
/*     */   {
/* 566 */     this.closed = true;
/*     */     
/* 568 */     TCPNetworkManager.getSingleton().getReadSelector().cancel(this.channel);
/* 569 */     TCPNetworkManager.getSingleton().getWriteSelector().cancel(this.channel);
/* 570 */     TCPNetworkManager.getSingleton().getConnectDisconnectManager().closeConnection(this.channel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void failed(Throwable reason)
/*     */   {
/* 577 */     close(Debug.getNestedExceptionMessage(reason));
/*     */   }
/*     */   
/* 580 */   public SocketChannel getSocketChannel() { return this.channel; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void setUserData(Object key, Object data)
/*     */   {
/* 587 */     if (this.user_data == null)
/*     */     {
/* 589 */       this.user_data = new HashMap();
/*     */     }
/*     */     
/* 592 */     this.user_data.put(key, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public synchronized Object getUserData(Object key)
/*     */   {
/* 599 */     if (this.user_data == null)
/*     */     {
/* 601 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 605 */     return this.user_data.get(key);
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
/*     */   public void setTrace(boolean on)
/*     */   {
/* 645 */     this.trace = on;
/*     */   }
/*     */   
/*     */   public void setScatteringMode(long forBytes) {
/* 649 */     if (forBytes > 0L)
/*     */     {
/* 651 */       if (this.remainingBytesToScatter == 0L)
/*     */         try
/*     */         {
/* 654 */           this.channel.socket().setTcpNoDelay(true);
/*     */         }
/*     */         catch (SocketException e) {
/* 657 */           Debug.printStackTrace(e);
/*     */         }
/* 659 */       this.remainingBytesToScatter = forBytes;
/*     */     }
/*     */     else {
/* 662 */       if (this.remainingBytesToScatter > 0L)
/*     */         try
/*     */         {
/* 665 */           this.channel.socket().setTcpNoDelay(false);
/*     */         }
/*     */         catch (SocketException e) {
/* 668 */           Debug.printStackTrace(e);
/*     */         }
/* 670 */       this.remainingBytesToScatter = 0L;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TCPTransportHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */