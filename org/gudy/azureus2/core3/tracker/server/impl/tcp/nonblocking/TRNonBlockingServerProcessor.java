/*     */ package org.gudy.azureus2.core3.tracker.server.impl.tcp.nonblocking;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerProcessorTCP;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerTCP;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ 
/*     */ 
/*     */ public abstract class TRNonBlockingServerProcessor
/*     */   extends TRTrackerServerProcessorTCP
/*     */ {
/*     */   private static final int MAX_POST = 262144;
/*     */   private static final int READ_BUFFER_INITIAL = 1024;
/*     */   private static final int READ_BUFFER_INCREMENT = 1024;
/*     */   private static final int READ_BUFFER_LIMIT = 32768;
/*     */   private final SocketChannel socket_channel;
/*     */   private VirtualChannelSelector.VirtualSelectorListener read_listener;
/*     */   private VirtualChannelSelector.VirtualSelectorListener write_listener;
/*     */   private long start_time;
/*     */   private ByteBuffer read_buffer;
/*     */   private ByteBuffer post_data_buffer;
/*     */   private String request_header;
/*     */   private String lc_request_header;
/*     */   private ByteBuffer write_buffer;
/*     */   private boolean keep_alive;
/*     */   
/*     */   protected TRNonBlockingServerProcessor(TRTrackerServerTCP _server, SocketChannel _socket)
/*     */   {
/*  76 */     super(_server);
/*     */     
/*  78 */     this.socket_channel = _socket;
/*     */     
/*  80 */     this.start_time = SystemTime.getCurrentTime();
/*     */     
/*  82 */     this.read_buffer = ByteBuffer.allocate(1024);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setReadListener(VirtualChannelSelector.VirtualSelectorListener rl)
/*     */   {
/*  91 */     this.read_listener = rl;
/*     */   }
/*     */   
/*     */ 
/*     */   protected VirtualChannelSelector.VirtualSelectorListener getReadListener()
/*     */   {
/*  97 */     return this.read_listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setWriteListener(VirtualChannelSelector.VirtualSelectorListener wl)
/*     */   {
/* 104 */     this.write_listener = wl;
/*     */   }
/*     */   
/*     */ 
/*     */   protected VirtualChannelSelector.VirtualSelectorListener getWriteListener()
/*     */   {
/* 110 */     return this.write_listener;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int processRead()
/*     */   {
/* 121 */     if (this.post_data_buffer != null) {
/*     */       try
/*     */       {
/* 124 */         int len = this.socket_channel.read(this.post_data_buffer);
/*     */         
/* 126 */         if (len < 0)
/*     */         {
/* 128 */           return -1;
/*     */         }
/*     */         
/* 131 */         if (this.post_data_buffer.remaining() == 0)
/*     */         {
/* 133 */           this.post_data_buffer.flip();
/*     */           
/* 135 */           getServer().runProcessor(this);
/*     */           
/* 137 */           return 0;
/*     */         }
/*     */         
/*     */ 
/* 141 */         return 1;
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 145 */         return -1;
/*     */       }
/*     */     }
/*     */     
/* 149 */     if (this.read_buffer.remaining() == 0)
/*     */     {
/* 151 */       int capacity = this.read_buffer.capacity();
/*     */       
/* 153 */       if (capacity == 32768)
/*     */       {
/* 155 */         return -1;
/*     */       }
/*     */       
/*     */ 
/* 159 */       this.read_buffer.position(0);
/*     */       
/* 161 */       byte[] data = new byte[capacity];
/*     */       
/* 163 */       this.read_buffer.get(data);
/*     */       
/* 165 */       this.read_buffer = ByteBuffer.allocate(capacity + 1024);
/*     */       
/* 167 */       this.read_buffer.put(data);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 172 */       int len = this.socket_channel.read(this.read_buffer);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 177 */       if (len < 0)
/*     */       {
/* 179 */         return -1;
/*     */       }
/* 181 */       if (len == 0)
/*     */       {
/* 183 */         return 2;
/*     */       }
/*     */       
/* 186 */       byte[] data = this.read_buffer.array();
/*     */       
/* 188 */       int array_offset = this.read_buffer.arrayOffset();
/* 189 */       int array_position = array_offset + this.read_buffer.position();
/*     */       
/* 191 */       for (int i = array_offset; i <= array_position - 4; i++)
/*     */       {
/* 193 */         if ((data[i] == 13) && (data[(i + 1)] == 10) && (data[(i + 2)] == 13) && (data[(i + 3)] == 10))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 198 */           int header_end = i + 4;
/* 199 */           int header_length = header_end - array_offset;
/*     */           
/* 201 */           this.request_header = new String(data, array_offset, header_length);
/* 202 */           this.lc_request_header = this.request_header.toLowerCase();
/*     */           
/* 204 */           int rem = array_position - header_end;
/*     */           
/* 206 */           if (rem == 0)
/*     */           {
/* 208 */             this.read_buffer = ByteBuffer.allocate(1024);
/*     */           }
/*     */           else
/*     */           {
/* 212 */             this.read_buffer = ByteBuffer.allocate(rem + 1024);
/*     */             
/* 214 */             this.read_buffer.put(data, header_end, rem);
/*     */           }
/*     */           
/* 217 */           this.post_data_buffer = null;
/*     */           
/* 219 */           int pos1 = this.lc_request_header.indexOf("content-length");
/*     */           
/* 221 */           if (pos1 == -1)
/*     */           {
/* 223 */             if ((this.lc_request_header.contains("transfer-encoding")) && (this.lc_request_header.contains("chunked")))
/*     */             {
/*     */ 
/* 226 */               Debug.out("Chunked transfer-encoding not supported!!!!");
/*     */             }
/*     */           }
/*     */           else {
/* 230 */             int pos2 = this.lc_request_header.indexOf("\r\n", pos1);
/*     */             
/*     */             String entry;
/*     */             String entry;
/* 234 */             if (pos2 == -1)
/*     */             {
/* 236 */               entry = this.lc_request_header.substring(pos1);
/*     */             }
/*     */             else
/*     */             {
/* 240 */               entry = this.lc_request_header.substring(pos1, pos2);
/*     */             }
/*     */             
/* 243 */             int pos = entry.indexOf(':');
/*     */             
/* 245 */             if (pos != -1)
/*     */             {
/* 247 */               int content_length = 0;
/*     */               try
/*     */               {
/* 250 */                 content_length = Integer.parseInt(entry.substring(pos + 1).trim());
/*     */               }
/*     */               catch (Throwable e) {}
/*     */               
/*     */ 
/* 255 */               if (content_length > 0)
/*     */               {
/* 257 */                 if (content_length > 262144)
/*     */                 {
/* 259 */                   throw new IOException("content-length too large, max=262144");
/*     */                 }
/*     */                 
/* 262 */                 this.post_data_buffer = ByteBuffer.allocate(content_length);
/*     */                 
/* 264 */                 int buffer_position = this.read_buffer.position();
/*     */                 
/* 266 */                 if (buffer_position > 0)
/*     */                 {
/* 268 */                   byte[] already_read = new byte[Math.min(buffer_position, content_length)];
/*     */                   
/* 270 */                   this.read_buffer.flip();
/*     */                   
/* 272 */                   this.read_buffer.get(already_read);
/*     */                   
/* 274 */                   byte[] xrem = new byte[this.read_buffer.remaining()];
/*     */                   
/* 276 */                   this.read_buffer.get(xrem);
/*     */                   
/* 278 */                   this.read_buffer = ByteBuffer.allocate(xrem.length + 1024);
/*     */                   
/* 280 */                   this.read_buffer.put(xrem);
/*     */                   
/* 282 */                   this.post_data_buffer.put(already_read);
/*     */                   
/* 284 */                   if (this.post_data_buffer.remaining() == 0)
/*     */                   {
/* 286 */                     getServer().runProcessor(this);
/*     */                     
/* 288 */                     return 0;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 295 */           if (this.post_data_buffer == null)
/*     */           {
/*     */ 
/*     */ 
/* 299 */             getServer().runProcessor(this);
/*     */             
/* 301 */             return 0;
/*     */           }
/*     */           
/*     */ 
/* 305 */           return 1;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 310 */       return 1;
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 314 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int processWrite()
/*     */   {
/* 326 */     if (this.write_buffer == null)
/*     */     {
/* 328 */       return -1;
/*     */     }
/*     */     
/* 331 */     if (!this.write_buffer.hasRemaining())
/*     */     {
/* 333 */       writeComplete();
/*     */       
/* 335 */       return 0;
/*     */     }
/*     */     try
/*     */     {
/* 339 */       int written = this.socket_channel.write(this.write_buffer);
/*     */       
/* 341 */       if (written == 0)
/*     */       {
/* 343 */         return 2;
/*     */       }
/*     */       
/* 346 */       if (this.write_buffer.hasRemaining())
/*     */       {
/* 348 */         return 1;
/*     */       }
/*     */       
/* 351 */       writeComplete();
/*     */       
/* 353 */       return 0;
/*     */     }
/*     */     catch (IOException e) {}
/*     */     
/* 357 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void runSupport()
/*     */   {
/* 364 */     boolean async = false;
/*     */     try
/*     */     {
/* 367 */       String url = this.request_header.substring(this.request_header.indexOf(' ')).trim();
/*     */       
/* 369 */       int pos = url.indexOf(" ");
/*     */       
/* 371 */       url = url.substring(0, pos);
/*     */       
/* 373 */       final AESemaphore[] went_async = { null };
/* 374 */       final ByteArrayOutputStream[] async_stream = { null };
/*     */       
/* 376 */       AsyncController async_control = new AsyncController()
/*     */       {
/*     */ 
/*     */         public void setAsyncStart()
/*     */         {
/*     */ 
/* 382 */           went_async[0] = new AESemaphore("async");
/*     */         }
/*     */         
/*     */ 
/*     */         public void setAsyncComplete()
/*     */         {
/* 388 */           went_async[0].reserve();
/*     */           
/* 390 */           TRNonBlockingServerProcessor.this.asyncProcessComplete(async_stream[0]);
/*     */         }
/*     */       };
/*     */       try
/*     */       {
/* 395 */         ByteArrayOutputStream response = process(this.request_header, this.lc_request_header, url, (InetSocketAddress)this.socket_channel.socket().getRemoteSocketAddress(), getServer().getRestrictNonBlocking(), new ByteArrayInputStream(new byte[0]), async_control);
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
/* 408 */         if (response == null)
/*     */         {
/* 410 */           async = true;
/*     */         }
/* 412 */         else if (went_async[0] != null)
/*     */         {
/* 414 */           async_stream[0] = response;
/*     */           
/* 416 */           async = true;
/*     */         }
/*     */         else
/*     */         {
/* 420 */           this.write_buffer = ByteBuffer.wrap(response.toByteArray());
/*     */         }
/*     */       }
/*     */       finally {
/* 424 */         if (went_async[0] != null)
/*     */         {
/* 426 */           went_async[0].release();
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */     }
/*     */     catch (Throwable e) {}finally
/*     */     {
/* 434 */       if (!async)
/*     */       {
/* 436 */         ((TRNonBlockingServer)getServer()).readyToWrite(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract ByteArrayOutputStream process(String paramString1, String paramString2, String paramString3, InetSocketAddress paramInetSocketAddress, boolean paramBoolean, InputStream paramInputStream, AsyncController paramAsyncController)
/*     */     throws IOException;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void asyncProcessComplete(ByteArrayOutputStream response)
/*     */   {
/* 457 */     this.write_buffer = ByteBuffer.wrap(response.toByteArray());
/*     */     
/* 459 */     ((TRNonBlockingServer)getServer()).readyToWrite(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected SocketChannel getSocketChannel()
/*     */   {
/* 465 */     return this.socket_channel;
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getPostData()
/*     */   {
/* 471 */     ByteBuffer result = this.post_data_buffer;
/*     */     
/* 473 */     if (result == null)
/*     */     {
/* 475 */       return null;
/*     */     }
/*     */     
/* 478 */     this.post_data_buffer = null;
/*     */     
/* 480 */     return result.array();
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getStartTime()
/*     */   {
/* 486 */     return this.start_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getKeepAlive()
/*     */   {
/* 492 */     return this.keep_alive;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setKeepAlive(boolean k)
/*     */   {
/* 499 */     this.keep_alive = k;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isActive()
/*     */   {
/* 505 */     return !this.socket_channel.socket().isClosed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void interruptTask() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void failed() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeComplete()
/*     */   {
/* 523 */     if (this.keep_alive)
/*     */     {
/*     */ 
/*     */ 
/* 527 */       this.start_time = SystemTime.getCurrentTime();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void completed() {}
/*     */   
/*     */   protected void closed() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/nonblocking/TRNonBlockingServerProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */