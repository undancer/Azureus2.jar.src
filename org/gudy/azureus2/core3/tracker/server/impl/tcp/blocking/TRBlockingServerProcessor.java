/*     */ package org.gudy.azureus2.core3.tracker.server.impl.tcp.blocking;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerProcessorTCP;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.tcp.TRTrackerServerTCP;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
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
/*     */ public class TRBlockingServerProcessor
/*     */   extends TRTrackerServerProcessorTCP
/*     */ {
/*     */   protected static final int KEEP_ALIVE_SOCKET_TIMEOUT = 30000;
/*  52 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */   protected final Socket socket;
/*     */   
/*  56 */   protected int timeout_ticks = 1;
/*     */   
/*     */ 
/*     */   protected String current_request;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRBlockingServerProcessor(TRTrackerServerTCP _server, Socket _socket)
/*     */   {
/*  65 */     super(_server);
/*     */     
/*  67 */     this.socket = _socket;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void runSupport()
/*     */   {
/*  75 */     boolean keep_alive = getServer().isKeepAliveEnabled();
/*     */     try
/*     */     {
/*  78 */       InputStream is = new BufferedInputStream(this.socket.getInputStream());
/*     */       
/*     */       for (;;)
/*     */       {
/*  82 */         setTaskState("entry");
/*     */         try
/*     */         {
/*  85 */           if (keep_alive)
/*     */           {
/*  87 */             this.socket.setSoTimeout(30000);
/*     */             
/*  89 */             setTimeoutsDisabled(true);
/*     */           }
/*     */           else
/*     */           {
/*  93 */             this.socket.setSoTimeout(5000);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*     */ 
/* 100 */         setTaskState("reading header");
/*     */         
/*     */         try
/*     */         {
/* 104 */           byte[] buffer = new byte['䀀'];
/* 105 */           int header_pos = 0;
/*     */           
/* 107 */           while (header_pos < buffer.length)
/*     */           {
/* 109 */             int len = is.read(buffer, header_pos, 1);
/*     */             
/* 111 */             if (len != 1)
/*     */             {
/* 113 */               throw new Exception("Premature end of stream reading header");
/*     */             }
/*     */             
/* 116 */             header_pos++;
/*     */             
/* 118 */             if ((header_pos >= 4) && (buffer[(header_pos - 4)] == 13) && (buffer[(header_pos - 3)] == 10) && (buffer[(header_pos - 2)] == 13) && (buffer[(header_pos - 1)] == 10)) {
/*     */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 128 */           String header = new String(buffer, 0, header_pos, "ISO-8859-1");
/*     */           
/* 130 */           if (Logger.isEnabled())
/*     */           {
/* 132 */             String log_str = header;
/*     */             
/* 134 */             int pos = log_str.indexOf("\r\n");
/*     */             
/* 136 */             if (pos != -1)
/*     */             {
/* 138 */               log_str = log_str.substring(0, pos);
/*     */             }
/*     */             
/* 141 */             Logger.log(new LogEvent(LOGID, "Tracker Server: received header '" + log_str + "' from " + this.socket.getRemoteSocketAddress()));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 147 */           InputStream post_is = null;
/* 148 */           File post_file = null;
/*     */           
/*     */ 
/*     */ 
/* 152 */           boolean head = false;
/*     */           
/*     */           int url_start;
/*     */           String lowercase_header;
/*     */           int url_start;
/* 157 */           if (header.startsWith("GET "))
/*     */           {
/* 159 */             this.timeout_ticks = 1;
/*     */             
/* 161 */             String lowercase_header = header.toLowerCase();
/* 162 */             url_start = 4;
/*     */           }
/* 164 */           else if (header.startsWith("HEAD "))
/*     */           {
/* 166 */             this.timeout_ticks = 1;
/*     */             
/* 168 */             String lowercase_header = header.toLowerCase();
/* 169 */             int url_start = 5;
/*     */             
/* 171 */             head = true;
/*     */           }
/* 173 */           else if (header.startsWith("POST "))
/*     */           {
/* 175 */             this.timeout_ticks = TRTrackerServerTCP.PROCESSING_POST_MULTIPLIER;
/*     */             
/* 177 */             if (this.timeout_ticks == 0)
/*     */             {
/* 179 */               setTimeoutsDisabled(true);
/*     */             }
/*     */             
/* 182 */             setTaskState("reading content");
/*     */             
/* 184 */             String lowercase_header = header.toLowerCase();
/* 185 */             int url_start = 5;
/*     */             
/* 187 */             String cl_str = getHeaderField(header, lowercase_header, "content-length:");
/*     */             
/* 189 */             boolean chunk_read = false;
/* 190 */             if (cl_str == null)
/*     */             {
/* 192 */               String transfer_encoding_str = getHeaderField(header, lowercase_header, "transfer-encoding: ");
/*     */               
/* 194 */               chunk_read = (transfer_encoding_str != null) && (transfer_encoding_str.equalsIgnoreCase("chunked"));
/*     */               
/*     */ 
/* 197 */               cl_str = "0";
/*     */             }
/*     */             
/* 200 */             int content_length = Integer.parseInt(cl_str);
/*     */             
/* 202 */             ByteArrayOutputStream baos = null;
/* 203 */             FileOutputStream fos = null;
/*     */             try
/*     */             {
/*     */               OutputStream data_os;
/*     */               OutputStream data_os;
/* 208 */               if (content_length <= 262144)
/*     */               {
/* 210 */                 baos = new ByteArrayOutputStream();
/*     */                 
/* 212 */                 data_os = baos;
/*     */               }
/*     */               else
/*     */               {
/* 216 */                 post_file = AETemporaryFileHandler.createTempFile();
/*     */                 
/* 218 */                 post_file.deleteOnExit();
/*     */                 
/* 220 */                 fos = new FileOutputStream(post_file);
/*     */                 
/* 222 */                 data_os = fos;
/*     */               }
/* 224 */               if (chunk_read) {
/*     */                 for (;;)
/*     */                 {
/* 227 */                   int chunkSize = -1;
/*     */                   for (;;) {
/* 229 */                     int val = is.read();
/* 230 */                     if (val == -1) {
/* 231 */                       throw new TRTrackerServerException("premature end of input stream (chunksize)");
/*     */                     }
/* 233 */                     if (val == 10) {
/*     */                       break;
/*     */                     }
/* 236 */                     if (val != 13) {
/* 237 */                       if (chunkSize == -1) {
/* 238 */                         chunkSize = 0;
/*     */                       } else {
/* 240 */                         chunkSize <<= 4;
/*     */                       }
/* 242 */                       chunkSize += Character.digit(val, 16);
/*     */                     }
/*     */                   }
/* 245 */                   if (chunkSize == -1) {
/* 246 */                     throw new TRTrackerServerException("invalid chunk size");
/*     */                   }
/* 248 */                   if (chunkSize == 0)
/*     */                   {
/* 250 */                     boolean bad = (is.read() == -1) || (is.read() == -1);
/* 251 */                     if (!bad) break;
/* 252 */                     throw new TRTrackerServerException("premature end of input stream (NoTerminatingChunk)");
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/* 257 */                   while (chunkSize > 0) {
/* 258 */                     int len = is.read(buffer, 0, Math.min(chunkSize, buffer.length));
/* 259 */                     if (len < 0) {
/* 260 */                       throw new TRTrackerServerException("premature end of input stream");
/*     */                     }
/* 262 */                     data_os.write(buffer, 0, len);
/* 263 */                     chunkSize -= len;
/*     */                   }
/*     */                   
/* 266 */                   boolean bad = (is.read() == -1) || (is.read() == -1);
/* 267 */                   if (bad) {
/* 268 */                     throw new TRTrackerServerException("premature end of input stream (NoChunkEndMarker)");
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/* 273 */               while (content_length > 0)
/*     */               {
/* 275 */                 int len = is.read(buffer, 0, Math.min(content_length, buffer.length));
/*     */                 
/* 277 */                 if (len < 0)
/*     */                 {
/* 279 */                   throw new TRTrackerServerException("premature end of input stream");
/*     */                 }
/*     */                 
/* 282 */                 data_os.write(buffer, 0, len);
/*     */                 
/* 284 */                 content_length -= len;
/*     */               }
/*     */               
/* 287 */               if (baos != null)
/*     */               {
/* 289 */                 post_is = new ByteArrayInputStream(baos.toByteArray());
/*     */               }
/*     */               else
/*     */               {
/* 293 */                 fos.close();
/*     */                 
/* 295 */                 fos = null;
/*     */                 
/* 297 */                 post_is = new BufferedInputStream(new FileInputStream(post_file), 262144);
/*     */               }
/*     */               
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/* 304 */               if (baos != null) {
/*     */                 try {
/* 306 */                   baos.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/* 310 */               if (fos != null) {
/*     */                 try {
/* 312 */                   fos.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/*     */ 
/* 318 */               if ((post_is == null) && (post_file != null)) {
/* 319 */                 post_file.delete();
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/* 324 */             int pos = header.indexOf(' ');
/*     */             
/* 326 */             if (pos == -1)
/*     */             {
/* 328 */               throw new TRTrackerServerException("header doesn't have space in right place");
/*     */             }
/*     */             
/* 331 */             this.timeout_ticks = 1;
/*     */             
/* 333 */             lowercase_header = header.toLowerCase();
/* 334 */             url_start = pos + 1;
/*     */           }
/*     */           
/* 337 */           setTaskState("processing request");
/*     */           
/* 339 */           this.current_request = header;
/*     */           try
/*     */           {
/* 342 */             if (post_is == null)
/*     */             {
/*     */ 
/*     */ 
/* 346 */               post_is = new ByteArrayInputStream(new byte[0]);
/*     */             }
/*     */             
/* 349 */             int url_end = header.indexOf(" ", url_start);
/*     */             
/* 351 */             if (url_end == -1)
/*     */             {
/* 353 */               throw new TRTrackerServerException("header doesn't have space in right place");
/*     */             }
/*     */             
/* 356 */             String url = header.substring(url_start, url_end).trim();
/*     */             
/* 358 */             int nl_pos = header.indexOf("\r\n", url_end);
/*     */             
/* 360 */             if (nl_pos == -1)
/*     */             {
/* 362 */               throw new TRTrackerServerException("header doesn't have nl in right place");
/*     */             }
/*     */             
/* 365 */             String http_ver = header.substring(url_end, nl_pos).trim();
/*     */             
/*     */ 
/* 368 */             String con_str = getHeaderField(header, lowercase_header, "connection:");
/*     */             
/* 370 */             if (con_str == null)
/*     */             {
/* 372 */               if (http_ver.equalsIgnoreCase("HTTP/1.0"))
/*     */               {
/* 374 */                 keep_alive = false;
/*     */               }
/* 376 */             } else if (con_str.equalsIgnoreCase("close"))
/*     */             {
/* 378 */               keep_alive = false;
/*     */             }
/*     */             
/* 381 */             if (head)
/*     */             {
/* 383 */               ByteArrayOutputStream head_response = new ByteArrayOutputStream(4096);
/*     */               
/* 385 */               if (!processRequest(header, lowercase_header, url, (InetSocketAddress)this.socket.getLocalSocketAddress(), (InetSocketAddress)this.socket.getRemoteSocketAddress(), false, keep_alive, post_is, head_response, null))
/*     */               {
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
/* 397 */                 keep_alive = false;
/*     */               }
/*     */               
/* 400 */               byte[] head_data = head_response.toByteArray();
/*     */               
/* 402 */               int header_length = head_data.length;
/*     */               
/* 404 */               for (int i = 3; i < head_data.length; i++)
/*     */               {
/* 406 */                 if ((head_data[(i - 3)] == 13) && (head_data[(i - 2)] == 10) && (head_data[(i - 1)] == 13) && (head_data[i] == 10))
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 411 */                   header_length = i + 1;
/*     */                   
/* 413 */                   break;
/*     */                 }
/*     */               }
/*     */               
/* 417 */               setTaskState("writing head response");
/*     */               
/* 419 */               this.socket.getOutputStream().write(head_data, 0, header_length);
/*     */               
/* 421 */               this.socket.getOutputStream().flush();
/*     */ 
/*     */ 
/*     */             }
/* 425 */             else if (!processRequest(header, lowercase_header, url, (InetSocketAddress)this.socket.getLocalSocketAddress(), (InetSocketAddress)this.socket.getRemoteSocketAddress(), false, keep_alive, post_is, this.socket.getOutputStream(), null))
/*     */             {
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
/* 437 */               keep_alive = false;
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 442 */             if (post_is != null)
/*     */             {
/* 444 */               post_is.close();
/*     */             }
/*     */             
/* 447 */             if (post_file != null)
/*     */             {
/* 449 */               post_file.delete();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 454 */           keep_alive = false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 459 */         if (!keep_alive) {
/*     */           break;
/*     */         }
/*     */       }
/*     */       
/*     */       return;
/*     */     }
/*     */     catch (Throwable e) {}finally
/*     */     {
/* 468 */       setTaskState("final socket close");
/*     */       try
/*     */       {
/* 471 */         this.socket.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
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
/*     */   protected String getHeaderField(String header, String lc_header, String field)
/*     */   {
/* 488 */     int start = lc_header.indexOf(field);
/*     */     
/* 490 */     if (start == -1)
/*     */     {
/* 492 */       return null;
/*     */     }
/*     */     
/* 495 */     int end = header.indexOf("\r\n", start);
/*     */     
/* 497 */     if (end == -1)
/*     */     {
/* 499 */       return null;
/*     */     }
/*     */     
/* 502 */     return header.substring(start + field.length(), end).trim();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isActive()
/*     */   {
/*     */     try
/*     */     {
/* 512 */       if (!this.socket.getKeepAlive())
/*     */       {
/* 514 */         this.socket.setKeepAlive(true);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/* 520 */     return !this.socket.isClosed();
/*     */   }
/*     */   
/*     */   public void interruptTask()
/*     */   {
/*     */     try
/*     */     {
/* 527 */       if (!areTimeoutsDisabled())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 533 */         this.timeout_ticks -= 1;
/*     */         
/* 535 */         if (this.timeout_ticks <= 0)
/*     */         {
/* 537 */           System.out.println("Tracker task interrupted in state '" + getTaskState() + "' : processing time limit exceeded for " + this.socket.getInetAddress());
/*     */           
/* 539 */           this.socket.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/blocking/TRBlockingServerProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */