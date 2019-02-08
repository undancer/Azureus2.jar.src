/*     */ package com.aelitis.azureus.core.download;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ public class DiskManagerFileInfoURL
/*     */   implements DiskManagerFileInfo, SEPasswordListener
/*     */ {
/*     */   private URL url;
/*     */   private byte[] hash;
/*     */   private File file;
/*  65 */   private Object lock = new Object();
/*     */   
/*     */   private URL redirected_url;
/*     */   
/*     */   private int consec_redirect_fails;
/*     */   
/*     */   private volatile boolean file_cached;
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfoURL(URL _url)
/*     */   {
/*  76 */     this.url = _url;
/*     */     
/*  78 */     String url_str = this.url.toExternalForm();
/*     */     
/*  80 */     String id_key = "azcdid=";
/*  81 */     String dn_key = "azcddn=";
/*     */     
/*  83 */     int id_pos = url_str.indexOf(id_key);
/*  84 */     int dn_pos = url_str.indexOf(dn_key);
/*     */     
/*  86 */     int min_pos = id_pos;
/*  87 */     if (min_pos == -1) {
/*  88 */       min_pos = dn_pos;
/*     */     }
/*  90 */     else if (dn_pos != -1) {
/*  91 */       min_pos = Math.min(min_pos, dn_pos);
/*     */     }
/*     */     
/*     */ 
/*  95 */     if (min_pos > 0) {
/*     */       try
/*     */       {
/*  98 */         this.url = new URL(url_str.substring(0, min_pos - 1));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 102 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 107 */       this.hash = new SHA1Simple().calculateHash(("DiskManagerFileInfoURL" + this.url.toExternalForm()).getBytes("UTF-8"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 111 */       Debug.out(e);
/*     */     }
/*     */     
/*     */     String file_name;
/*     */     
/* 116 */     if (dn_pos != -1)
/*     */     {
/* 118 */       String dn = url_str.substring(dn_pos + dn_key.length());
/*     */       
/* 120 */       dn_pos = dn.indexOf('&');
/*     */       
/* 122 */       if (dn_pos != -1)
/*     */       {
/* 124 */         dn = dn.substring(0, dn_pos);
/*     */       }
/*     */       
/* 127 */       file_name = UrlUtils.decode(dn);
/*     */     }
/*     */     else
/*     */     {
/* 131 */       String path = this.url.getPath();
/*     */       
/* 133 */       int pos = path.lastIndexOf("/");
/*     */       
/* 135 */       if (pos != -1)
/*     */       {
/* 137 */         path = path.substring(pos + 1);
/*     */       }
/*     */       
/* 140 */       path = path.trim();
/*     */       String file_name;
/* 142 */       if (url_str.length() > 0)
/*     */       {
/* 144 */         file_name = UrlUtils.decode(path);
/*     */       }
/*     */       else
/*     */       {
/* 148 */         file_name = Base32.encode(this.hash);
/*     */       }
/*     */     }
/*     */     
/* 152 */     String file_name = FileUtil.convertOSSpecificChars(file_name, false);
/*     */     try
/*     */     {
/* 155 */       this.file = new File(AETemporaryFileHandler.createTempDir(), file_name);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 159 */       file_name = file_name + ".tmp";
/*     */       
/* 161 */       this.file = new File(AETemporaryFileHandler.getTempDirectory(), file_name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 168 */     return this.url;
/*     */   }
/*     */   
/*     */ 
/*     */   public void download()
/*     */   {
/* 174 */     synchronized (this.lock)
/*     */     {
/* 176 */       if (this.file_cached)
/*     */       {
/* 178 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 182 */         channel chan = createChannel();
/*     */         
/* 184 */         DiskManagerFileInfoURL.channel.request req = chan.createRequest();
/*     */         
/* 186 */         req.setAll();
/*     */         
/* 188 */         final FileOutputStream fos = new FileOutputStream(this.file);
/*     */         
/* 190 */         boolean ok = false;
/*     */         try
/*     */         {
/* 193 */           req.addListener(new DiskManagerListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void eventOccurred(DiskManagerEvent event)
/*     */             {
/*     */ 
/* 200 */               if (event.getType() == 2)
/*     */               {
/* 202 */                 throw new RuntimeException(event.getFailure());
/*     */               }
/*     */               
/* 205 */               PooledByteBuffer buffer = event.getBuffer();
/*     */               
/* 207 */               if (buffer == null)
/*     */               {
/* 209 */                 throw new RuntimeException("eh?");
/*     */               }
/*     */               
/*     */               try
/*     */               {
/* 214 */                 fos.write(buffer.toByteArray());
/*     */               }
/*     */               catch (IOException e)
/*     */               {
/* 218 */                 throw new RuntimeException("Failed to write to " + DiskManagerFileInfoURL.this.file, e);
/*     */               }
/*     */               finally
/*     */               {
/* 222 */                 buffer.returnToPool();
/*     */               }
/*     */               
/*     */             }
/* 226 */           });
/* 227 */           req.run();
/*     */           
/* 229 */           ok = true;
/*     */         }
/*     */         finally
/*     */         {
/*     */           try {
/* 234 */             fos.close();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 238 */             Debug.out(e);
/*     */           }
/*     */           
/* 241 */           if (!ok)
/*     */           {
/* 243 */             this.file.delete();
/*     */           }
/*     */           else
/*     */           {
/* 247 */             this.file_cached = true;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 252 */         Debug.out("Failed to cache file from " + this.url, e);
/*     */       }
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
/*     */   public void setSkipped(boolean b)
/*     */   {
/* 267 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriority()
/*     */   {
/* 273 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriorty()
/*     */   {
/* 279 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/* 286 */     throw new RuntimeException("Not supported");
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
/* 298 */     throw new RuntimeException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 304 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 310 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 316 */     return getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 322 */     if (this.file_cached)
/*     */     {
/* 324 */       long len = this.file.length();
/*     */       
/* 326 */       if (len > 0L)
/*     */       {
/* 328 */         return len;
/*     */       }
/*     */     }
/*     */     
/* 332 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 338 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 345 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 351 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 357 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPieceSize()
/*     */   {
/* 363 */     return 32768L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumPieces()
/*     */   {
/* 369 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isPriority()
/*     */   {
/* 375 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 381 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 387 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getDownloadHash()
/*     */     throws DownloadException
/*     */   {
/* 395 */     return this.hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 403 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public channel createChannel()
/*     */     throws DownloadException
/*     */   {
/* 411 */     return new channel();
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
/* 423 */     throw new DownloadException("Not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*     */   {
/* 431 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setAuthenticationOutcome(String realm, URL tracker, boolean success) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearPasswords() {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected class channel
/*     */     implements DiskManagerChannel
/*     */   {
/*     */     private volatile boolean channel_destroyed;
/*     */     
/*     */     private volatile long channel_position;
/*     */     
/*     */ 
/*     */     protected channel() {}
/*     */     
/*     */ 
/*     */     public request createRequest()
/*     */     {
/* 457 */       return new request();
/*     */     }
/*     */     
/*     */ 
/*     */     public DiskManagerFileInfo getFile()
/*     */     {
/* 463 */       return DiskManagerFileInfoURL.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getPosition()
/*     */     {
/* 469 */       return this.channel_position;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDestroyed()
/*     */     {
/* 475 */       return this.channel_destroyed;
/*     */     }
/*     */     
/*     */ 
/*     */     public void destroy()
/*     */     {
/* 481 */       this.channel_destroyed = true;
/*     */     }
/*     */     
/*     */     protected class request implements DiskManagerRequest {
/*     */       private long offset;
/*     */       private long length;
/*     */       private boolean do_all_file;
/*     */       private long position;
/*     */       private int max_read_chunk;
/*     */       private volatile boolean request_cancelled;
/*     */       private CopyOnWriteList<DiskManagerListener> listeners;
/*     */       
/*     */       protected request() {
/* 494 */         this.max_read_chunk = 131072;
/*     */         
/*     */ 
/*     */ 
/* 498 */         this.listeners = new CopyOnWriteList();
/*     */       }
/*     */       
/*     */ 
/*     */       public void setType(int type)
/*     */       {
/* 504 */         if (type != 1)
/*     */         {
/* 506 */           throw new RuntimeException("Not supported");
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void setAll()
/*     */       {
/* 513 */         this.do_all_file = true;
/* 514 */         this.offset = 0L;
/* 515 */         setLength(-1L);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setOffset(long _offset)
/*     */       {
/* 522 */         this.offset = _offset;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void setLength(long _length)
/*     */       {
/* 531 */         this.length = (_length == -1L ? Long.MAX_VALUE : _length);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setMaximumReadChunkSize(int size)
/*     */       {
/* 538 */         if (size > 16384)
/*     */         {
/* 540 */           this.max_read_chunk = size;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public long getAvailableBytes()
/*     */       {
/* 547 */         return getRemaining();
/*     */       }
/*     */       
/*     */ 
/*     */       public long getRemaining()
/*     */       {
/* 553 */         return this.length == Long.MAX_VALUE ? this.length : this.offset + this.length - this.position;
/*     */       }
/*     */       
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 560 */           byte[] buffer = new byte[this.max_read_chunk];
/*     */           
/* 562 */           long rem = this.length;
/* 563 */           long pos = this.offset;
/*     */           
/* 565 */           InputStream is = null;
/*     */           try
/*     */           {
/* 568 */             SESecurityManager.setThreadPasswordHandler(DiskManagerFileInfoURL.this);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 575 */             Set<String> redirect_urls = new HashSet();
/*     */             HttpURLConnection connection;
/*     */             label435:
/*     */             for (;;)
/*     */             {
/* 580 */               URL target = DiskManagerFileInfoURL.this.redirected_url == null ? DiskManagerFileInfoURL.this.url : DiskManagerFileInfoURL.this.redirected_url;
/*     */               
/* 582 */               for (int ssl_loop = 0;; ssl_loop++) { if (ssl_loop >= 2)
/*     */                   break label435;
/*     */                 try {
/* 585 */                   connection = (HttpURLConnection)target.openConnection();
/*     */                   
/* 587 */                   if ((connection instanceof HttpsURLConnection))
/*     */                   {
/* 589 */                     HttpsURLConnection ssl_con = (HttpsURLConnection)connection;
/*     */                     
/*     */ 
/*     */ 
/* 593 */                     ssl_con.setHostnameVerifier(new HostnameVerifier()
/*     */                     {
/*     */ 
/*     */ 
/*     */                       public boolean verify(String host, SSLSession session)
/*     */                       {
/*     */ 
/*     */ 
/* 601 */                         return true;
/*     */                       }
/*     */                     });
/*     */                   }
/*     */                   
/* 606 */                   connection.setRequestProperty("Connection", "Keep-Alive");
/*     */                   
/* 608 */                   if (!this.do_all_file)
/*     */                   {
/* 610 */                     connection.setRequestProperty("Range", "bytes=" + this.offset + "-" + (this.offset + this.length - 1L));
/*     */                   }
/*     */                   
/* 613 */                   connection.setConnectTimeout(20000);
/*     */                   
/* 615 */                   connection.connect();
/*     */                   
/* 617 */                   connection.setReadTimeout(10000);
/*     */                   
/* 619 */                   int response = connection.getResponseCode();
/*     */                   
/* 621 */                   if ((response == 202) || (response == 200) || (response == 206))
/*     */                   {
/*     */ 
/*     */ 
/* 625 */                     if (DiskManagerFileInfoURL.this.redirected_url != null)
/*     */                     {
/* 627 */                       DiskManagerFileInfoURL.this.consec_redirect_fails = 0;
/*     */                     }
/*     */                     
/*     */                     break label438;
/*     */                   }
/* 632 */                   if ((response == 302) || (response == 301))
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/* 637 */                     String move_to = connection.getHeaderField("location");
/*     */                     
/* 639 */                     if (move_to != null)
/*     */                     {
/* 641 */                       if ((redirect_urls.contains(move_to)) || (redirect_urls.size() > 32))
/*     */                       {
/* 643 */                         throw new ExternalSeedException("redirect loop");
/*     */                       }
/*     */                       
/* 646 */                       redirect_urls.add(move_to);
/*     */                       
/* 648 */                       DiskManagerFileInfoURL.this.redirected_url = new URL(move_to);
/*     */                       
/* 650 */                       break;
/*     */                     }
/*     */                   }
/*     */                   
/* 654 */                   if (DiskManagerFileInfoURL.this.redirected_url == null) {
/*     */                     break label438;
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/* 661 */                   DiskManagerFileInfoURL.access$308(DiskManagerFileInfoURL.this);
/*     */                   
/* 663 */                   DiskManagerFileInfoURL.this.redirected_url = null;
/*     */                 }
/*     */                 catch (SSLException e)
/*     */                 {
/* 667 */                   if ((ssl_loop != 0) || 
/*     */                   
/* 669 */                     (SESecurityManager.installServerCertificates(target) == null))
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 677 */                     throw e;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */             label438:
/*     */             
/* 686 */             URL final_url = connection.getURL();
/*     */             
/* 688 */             if ((DiskManagerFileInfoURL.this.consec_redirect_fails < 10) && (!DiskManagerFileInfoURL.this.url.toExternalForm().equals(final_url.toExternalForm())))
/*     */             {
/* 690 */               DiskManagerFileInfoURL.this.redirected_url = final_url;
/*     */             }
/*     */             
/* 693 */             is = connection.getInputStream();
/*     */             
/* 695 */             while (rem > 0L)
/*     */             {
/* 697 */               if (this.request_cancelled)
/*     */               {
/* 699 */                 throw new Exception("Cancelled");
/*     */               }
/* 701 */               if (DiskManagerFileInfoURL.channel.this.channel_destroyed)
/*     */               {
/* 703 */                 throw new Exception("Destroyed");
/*     */               }
/*     */               
/* 706 */               int len = is.read(buffer);
/*     */               
/* 708 */               if (len == -1)
/*     */               {
/* 710 */                 if (this.length == Long.MAX_VALUE) {
/*     */                   break;
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 716 */                 throw new Exception("Premature end of stream (complete)");
/*     */               }
/* 718 */               if (len == 0)
/*     */               {
/* 720 */                 sendEvent(new event(pos));
/*     */               }
/*     */               else
/*     */               {
/* 724 */                 sendEvent(new event(new PooledByteBufferImpl(buffer, 0, len), pos, len));
/*     */                 
/* 726 */                 rem -= len;
/* 727 */                 pos += len;
/*     */               }
/*     */             }
/*     */           }
/*     */           finally {
/* 732 */             SESecurityManager.unsetThreadPasswordHandler();
/*     */             
/*     */ 
/*     */ 
/* 736 */             if (is != null) {
/*     */               try
/*     */               {
/* 739 */                 is.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 750 */           return;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 748 */           sendEvent(new event(e));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void cancel()
/*     */       {
/* 755 */         this.request_cancelled = true;
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
/* 768 */         for (DiskManagerListener l : this.listeners)
/*     */         {
/* 770 */           l.eventOccurred(ev);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void addListener(DiskManagerListener listener)
/*     */       {
/* 778 */         this.listeners.add(listener);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void removeListener(DiskManagerListener listener)
/*     */       {
/* 785 */         this.listeners.remove(listener);
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
/* 802 */           this.event_type = 2;
/* 803 */           this.error = _error;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         protected event(long _offset)
/*     */         {
/* 810 */           this.event_type = 3;
/*     */           
/* 812 */           this.event_offset = _offset;
/*     */           
/* 814 */           DiskManagerFileInfoURL.channel.this.channel_position = _offset;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         protected event(PooledByteBuffer _buffer, long _offset, int _length)
/*     */         {
/* 823 */           this.event_type = 1;
/* 824 */           this.buffer = _buffer;
/* 825 */           this.event_offset = _offset;
/* 826 */           this.event_length = _length;
/*     */           
/* 828 */           DiskManagerFileInfoURL.channel.this.channel_position = (_offset + _length - 1L);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getType()
/*     */         {
/* 834 */           return this.event_type;
/*     */         }
/*     */         
/*     */ 
/*     */         public DiskManagerRequest getRequest()
/*     */         {
/* 840 */           return DiskManagerFileInfoURL.channel.request.this;
/*     */         }
/*     */         
/*     */ 
/*     */         public long getOffset()
/*     */         {
/* 846 */           return this.event_offset;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getLength()
/*     */         {
/* 852 */           return this.event_length;
/*     */         }
/*     */         
/*     */ 
/*     */         public PooledByteBuffer getBuffer()
/*     */         {
/* 858 */           return this.buffer;
/*     */         }
/*     */         
/*     */ 
/*     */         public Throwable getFailure()
/*     */         {
/* 864 */           return this.error;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/download/DiskManagerFileInfoURL.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */