/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.HTTPUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.AsyncController;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;
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
/*     */ public class TrackerWebPageResponseImpl
/*     */   implements TrackerWebPageResponse
/*     */ {
/*     */   private static final String NL = "\r\n";
/*  57 */   private ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
/*     */   
/*  59 */   private String content_type = "text/html";
/*     */   
/*  61 */   private int reply_status = 200;
/*     */   
/*  63 */   private Map<String, Object> header_map = new LinkedHashMap();
/*     */   
/*     */   private TrackerWebPageRequestImpl request;
/*     */   
/*     */   private boolean raw_output;
/*     */   private boolean is_async;
/*  69 */   private int explicit_gzip = 0;
/*     */   
/*     */   private boolean is_gzipped;
/*     */   
/*     */ 
/*     */   protected TrackerWebPageResponseImpl(TrackerWebPageRequestImpl _request)
/*     */   {
/*  76 */     this.request = _request;
/*     */     
/*  78 */     String formatted_date_now = TimeFormatter.getHTTPDate(SystemTime.getCurrentTime());
/*     */     
/*  80 */     setHeader("Last-Modified", formatted_date_now);
/*     */     
/*  82 */     setHeader("Expires", formatted_date_now);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLastModified(long time)
/*     */   {
/*  89 */     String formatted_date = TimeFormatter.getHTTPDate(time);
/*     */     
/*  91 */     setHeader("Last-Modified", formatted_date);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExpires(long time)
/*     */   {
/*  98 */     String formatted_date = TimeFormatter.getHTTPDate(time);
/*     */     
/* 100 */     setHeader("Expires", formatted_date);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setContentType(String type)
/*     */   {
/* 107 */     this.content_type = type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReplyStatus(int status)
/*     */   {
/* 114 */     this.reply_status = status;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHeader(String name, String value)
/*     */   {
/* 122 */     if (name.equalsIgnoreCase("set-cookie"))
/*     */     {
/* 124 */       Iterator<String> it = this.header_map.keySet().iterator();
/*     */       
/* 126 */       while (it.hasNext())
/*     */       {
/* 128 */         String key = (String)it.next();
/*     */         
/* 130 */         if (key.equalsIgnoreCase(name))
/*     */         {
/* 132 */           Object existing = this.header_map.get(key);
/*     */           
/* 134 */           if ((existing instanceof String))
/*     */           {
/* 136 */             String old = (String)existing;
/*     */             
/* 138 */             List l = new ArrayList(3);
/*     */             
/* 140 */             l.add(old);
/* 141 */             l.add(value);
/*     */             
/* 143 */             this.header_map.put(name, l);
/*     */           }
/*     */           else
/*     */           {
/* 147 */             List l = (List)existing;
/*     */             
/* 149 */             if (!l.contains(value))
/*     */             {
/* 151 */               l.add(value);
/*     */             }
/*     */           }
/*     */           
/* 155 */           return;
/*     */         }
/*     */       }
/*     */       
/* 159 */       this.header_map.put(name, value);
/*     */     }
/*     */     else
/*     */     {
/* 163 */       addHeader(name, value, true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setGZIP(boolean gzip)
/*     */   {
/* 171 */     this.explicit_gzip = (gzip ? 1 : 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String addHeader(String name, String value, boolean replace)
/*     */   {
/* 180 */     Iterator<String> it = this.header_map.keySet().iterator();
/*     */     
/* 182 */     while (it.hasNext())
/*     */     {
/* 184 */       String key = (String)it.next();
/*     */       
/* 186 */       if (key.equalsIgnoreCase(name))
/*     */       {
/* 188 */         if (replace)
/*     */         {
/* 190 */           it.remove();
/*     */         }
/*     */         else
/*     */         {
/* 194 */           Object existing = this.header_map.get(key);
/*     */           
/* 196 */           if ((existing instanceof String))
/*     */           {
/* 198 */             return (String)existing;
/*     */           }
/* 200 */           if ((existing instanceof List))
/*     */           {
/* 202 */             List<String> l = (List)existing;
/*     */             
/* 204 */             if (l.size() > 0)
/*     */             {
/* 206 */               return (String)l.get(0);
/*     */             }
/*     */           }
/*     */           
/* 210 */           return null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 215 */     this.header_map.put(name, value);
/*     */     
/* 217 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */   public OutputStream getOutputStream()
/*     */   {
/* 223 */     return this.baos;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public OutputStream getRawOutputStream()
/*     */     throws IOException
/*     */   {
/* 231 */     this.raw_output = true;
/*     */     
/* 233 */     return this.request.getOutputStream();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isActive()
/*     */   {
/* 239 */     return this.request.isActive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void complete()
/*     */     throws IOException
/*     */   {
/* 247 */     if ((this.is_async) || (this.raw_output))
/*     */     {
/* 249 */       return;
/*     */     }
/*     */     
/* 252 */     byte[] reply_bytes = this.baos.toByteArray();
/*     */     
/*     */ 
/*     */ 
/* 256 */     String status_string = "BAD";
/*     */     
/*     */ 
/*     */ 
/* 260 */     if (this.reply_status == 200)
/*     */     {
/* 262 */       status_string = "OK";
/*     */     }
/* 264 */     else if (this.reply_status == 204)
/*     */     {
/* 266 */       status_string = "No Content";
/*     */     }
/* 268 */     else if (this.reply_status == 206)
/*     */     {
/* 270 */       status_string = "Partial Content";
/*     */     }
/* 272 */     else if (this.reply_status == 401)
/*     */     {
/* 274 */       status_string = "Unauthorized";
/*     */     }
/* 276 */     else if (this.reply_status == 404)
/*     */     {
/* 278 */       status_string = "Not Found";
/*     */     }
/* 280 */     else if (this.reply_status == 501)
/*     */     {
/* 282 */       status_string = "Not Implemented";
/*     */     }
/*     */     
/* 285 */     String reply_header = "HTTP/1.1 " + this.reply_status + " " + status_string + "\r\n";
/*     */     
/*     */ 
/*     */ 
/* 289 */     addHeader("Server", "Azureus 5.7.6.0", false);
/*     */     
/* 291 */     if (this.request.canKeepAlive())
/*     */     {
/* 293 */       String applied_value = addHeader("Connection", "keep-alive", false);
/*     */       
/* 295 */       if (applied_value.equalsIgnoreCase("keep-alive"))
/*     */       {
/* 297 */         this.request.setKeepAlive(true);
/*     */       }
/*     */     }
/*     */     else {
/* 301 */       addHeader("Connection", "close", true);
/*     */     }
/*     */     
/* 304 */     addHeader("Content-Type", this.content_type, false);
/*     */     
/* 306 */     boolean do_gzip = false;
/*     */     
/* 308 */     if ((this.explicit_gzip == 1) && (!this.is_gzipped))
/*     */     {
/* 310 */       Map headers = this.request.getHeaders();
/*     */       
/* 312 */       String accept_encoding = (String)headers.get("accept-encoding");
/*     */       
/* 314 */       if (HTTPUtils.canGZIP(accept_encoding))
/*     */       {
/* 316 */         this.is_gzipped = (do_gzip = 1);
/*     */         
/* 318 */         this.header_map.put("Content-Encoding", "gzip");
/*     */       }
/*     */     }
/*     */     
/* 322 */     Iterator<String> it = this.header_map.keySet().iterator();
/*     */     String name;
/* 324 */     while (it.hasNext())
/*     */     {
/* 326 */       name = (String)it.next();
/* 327 */       Object value = this.header_map.get(name);
/*     */       
/* 329 */       if ((value instanceof String))
/*     */       {
/* 331 */         reply_header = reply_header + name + ": " + value + "\r\n";
/*     */       }
/*     */       else
/*     */       {
/* 335 */         List<String> l = (List)value;
/*     */         
/* 337 */         for (String v : l)
/*     */         {
/* 339 */           reply_header = reply_header + name + ": " + v + "\r\n";
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 344 */     if (do_gzip)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 349 */       if (reply_bytes.length < 524288)
/*     */       {
/* 351 */         ByteArrayOutputStream temp = new ByteArrayOutputStream(reply_bytes.length);
/*     */         
/* 353 */         GZIPOutputStream gzos = new GZIPOutputStream(temp);
/*     */         
/* 355 */         gzos.write(reply_bytes);
/*     */         
/* 357 */         gzos.finish();
/*     */         
/* 359 */         reply_bytes = temp.toByteArray();
/*     */       }
/*     */       else {
/* 362 */         File post_file = AETemporaryFileHandler.createTempFile();
/*     */         
/* 364 */         post_file.deleteOnExit();
/*     */         
/* 366 */         FileOutputStream fos = new FileOutputStream(post_file);
/* 367 */         GZIPOutputStream gzos = new GZIPOutputStream(fos);
/*     */         
/* 369 */         gzos.write(reply_bytes);
/*     */         
/* 371 */         gzos.close();
/*     */         
/* 373 */         FileInputStream fis = new FileInputStream(post_file);
/*     */         
/* 375 */         reply_header = reply_header + "Content-Length: " + post_file.length() + "\r\n" + "\r\n";
/*     */         
/*     */ 
/*     */ 
/* 379 */         OutputStream os = this.request.getOutputStream();
/*     */         
/* 381 */         os.write(reply_header.getBytes());
/*     */         
/* 383 */         byte[] buffer = new byte['䀀'];
/*     */         for (;;) {
/* 385 */           int read = fis.read(buffer);
/* 386 */           if (read == -1) {
/*     */             break;
/*     */           }
/* 389 */           os.write(buffer, 0, read);
/*     */         }
/*     */         
/* 392 */         os.flush();
/* 393 */         fis.close();
/* 394 */         post_file.delete();
/*     */         
/* 396 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 401 */     reply_header = reply_header + "Content-Length: " + reply_bytes.length + "\r\n" + "\r\n";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 407 */     OutputStream os = this.request.getOutputStream();
/*     */     
/* 409 */     os.write(reply_header.getBytes());
/*     */     
/* 411 */     os.write(reply_bytes);
/*     */     
/* 413 */     os.flush();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean useFile(String root_dir, String relative_url)
/*     */     throws IOException
/*     */   {
/* 423 */     String target = root_dir + relative_url.replace('/', File.separatorChar);
/*     */     
/* 425 */     File canonical_file = new File(target).getCanonicalFile();
/*     */     
/*     */ 
/*     */ 
/* 429 */     if (!canonical_file.toString().toLowerCase().startsWith(root_dir.toLowerCase()))
/*     */     {
/* 431 */       return false;
/*     */     }
/*     */     
/* 434 */     if (canonical_file.isDirectory())
/*     */     {
/* 436 */       return false;
/*     */     }
/*     */     
/* 439 */     if (canonical_file.canRead())
/*     */     {
/* 441 */       String str = canonical_file.toString().toLowerCase();
/*     */       
/* 443 */       int pos = str.lastIndexOf(".");
/*     */       
/* 445 */       if (pos == -1)
/*     */       {
/* 447 */         return false;
/*     */       }
/*     */       
/* 450 */       String file_type = str.substring(pos + 1);
/*     */       
/* 452 */       FileInputStream fis = null;
/*     */       try
/*     */       {
/* 455 */         fis = new FileInputStream(canonical_file);
/*     */         
/* 457 */         useStream(file_type, fis);
/*     */         
/* 459 */         return true;
/*     */       }
/*     */       finally
/*     */       {
/* 463 */         if (fis != null)
/*     */         {
/* 465 */           fis.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 470 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void useStream(String file_type, InputStream input_stream)
/*     */     throws IOException
/*     */   {
/* 480 */     OutputStream os = getOutputStream();
/*     */     
/* 482 */     String response_type = HTTPUtils.guessContentTypeFromFileType(file_type);
/*     */     
/* 484 */     if ((this.explicit_gzip != 2) && (HTTPUtils.useCompressionForFileType(response_type)))
/*     */     {
/* 486 */       Map headers = this.request.getHeaders();
/*     */       
/* 488 */       String accept_encoding = (String)headers.get("accept-encoding");
/*     */       
/* 490 */       if (HTTPUtils.canGZIP(accept_encoding))
/*     */       {
/* 492 */         this.is_gzipped = true;
/*     */         
/* 494 */         os = new GZIPOutputStream(os);
/*     */         
/* 496 */         this.header_map.put("Content-Encoding", "gzip");
/*     */       }
/*     */     }
/*     */     
/* 500 */     setContentType(response_type);
/*     */     
/* 502 */     byte[] buffer = new byte['က'];
/*     */     
/*     */     for (;;)
/*     */     {
/* 506 */       int len = input_stream.read(buffer);
/*     */       
/* 508 */       if (len <= 0) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/* 513 */       os.write(buffer, 0, len);
/*     */     }
/*     */     
/* 516 */     if ((os instanceof GZIPOutputStream))
/*     */     {
/* 518 */       ((GZIPOutputStream)os).finish();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeTorrent(TrackerTorrent tracker_torrent)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 530 */       TRHostTorrent host_torrent = ((TrackerTorrentImpl)tracker_torrent).getHostTorrent();
/*     */       
/* 532 */       TOTorrent torrent = host_torrent.getTorrent();
/*     */       
/*     */ 
/*     */ 
/* 536 */       TOTorrent torrent_to_send = TOTorrentFactory.deserialiseFromMap(torrent.serialiseToMap());
/*     */       
/*     */ 
/*     */ 
/* 540 */       torrent_to_send.removeAdditionalProperties();
/*     */       
/* 542 */       if (!TorrentUtils.isDecentralised(torrent_to_send))
/*     */       {
/* 544 */         URL[][] url_sets = TRTrackerUtils.getAnnounceURLs();
/*     */         
/*     */ 
/*     */ 
/* 548 */         if ((host_torrent.getStatus() != 3) && (url_sets.length > 0))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 553 */           if (COConfigurationManager.getBooleanParameter("Tracker Host Add Our Announce URLs"))
/*     */           {
/* 555 */             String protocol = torrent_to_send.getAnnounceURL().getProtocol();
/*     */             
/* 557 */             for (int i = 0; i < url_sets.length; i++)
/*     */             {
/* 559 */               URL[] urls = url_sets[i];
/*     */               
/* 561 */               if (urls[0].getProtocol().equalsIgnoreCase(protocol))
/*     */               {
/* 563 */                 torrent_to_send.setAnnounceURL(urls[0]);
/*     */                 
/* 565 */                 torrent_to_send.getAnnounceURLGroup().setAnnounceURLSets(new TOTorrentAnnounceURLSet[0]);
/*     */                 
/* 567 */                 for (int j = 1; j < urls.length; j++)
/*     */                 {
/* 569 */                   TorrentUtils.announceGroupsInsertLast(torrent_to_send, new URL[] { urls[j] });
/*     */                 }
/*     */                 
/* 572 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 579 */       this.baos.write(BEncoder.encode(torrent_to_send.serialiseToMap()));
/*     */       
/* 581 */       setContentType("application/x-bittorrent");
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 585 */       Debug.printStackTrace(e);
/*     */       
/* 587 */       throw new IOException(e.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAsynchronous(boolean a)
/*     */     throws IOException
/*     */   {
/* 597 */     AsyncController async_control = this.request.getAsyncController();
/*     */     
/* 599 */     if (async_control == null)
/*     */     {
/* 601 */       throw new IOException("Request is not non-blocking");
/*     */     }
/*     */     
/* 604 */     if (a)
/*     */     {
/* 606 */       this.is_async = true;
/*     */       
/* 608 */       async_control.setAsyncStart();
/*     */     }
/*     */     else
/*     */     {
/* 612 */       this.is_async = false;
/*     */       
/* 614 */       complete();
/*     */       
/* 616 */       async_control.setAsyncComplete();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAsynchronous()
/*     */   {
/* 623 */     return this.is_async;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerWebPageResponseImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */