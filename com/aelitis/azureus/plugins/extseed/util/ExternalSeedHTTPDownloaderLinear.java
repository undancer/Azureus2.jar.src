/*     */ package com.aelitis.azureus.plugins.extseed.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*     */ 
/*     */ 
/*     */ public class ExternalSeedHTTPDownloaderLinear
/*     */   implements ExternalSeedHTTPDownloader
/*     */ {
/*     */   private final URL very_original_url;
/*     */   private final String user_agent;
/*     */   private int last_response;
/*     */   private int last_response_retry_after_secs;
/*     */   private Downloader downloader;
/*     */   
/*     */   public ExternalSeedHTTPDownloaderLinear(URL _url, String _user_agent)
/*     */   {
/*  66 */     this.very_original_url = _url;
/*  67 */     this.user_agent = _user_agent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadRange(long offset, int length, ExternalSeedHTTPDownloaderListener listener, boolean con_fail_is_perm_fail)
/*     */     throws ExternalSeedException
/*     */   {
/*     */     Request request;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  82 */     synchronized (this)
/*     */     {
/*  84 */       if (this.downloader == null)
/*     */       {
/*  86 */         this.downloader = new Downloader(listener, con_fail_is_perm_fail);
/*     */       }
/*     */       
/*  89 */       request = this.downloader.addRequest(offset, length, listener);
/*     */     }
/*     */     
/*     */     do
/*     */     {
/*  94 */       if (request.waitFor(1000))
/*     */       {
/*  96 */         return;
/*     */       }
/*     */       
/*  99 */     } while (!listener.isCancelled());
/*     */     
/* 101 */     throw new ExternalSeedException("request cancelled");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deactivate()
/*     */   {
/* 109 */     Downloader to_destroy = null;
/*     */     
/* 111 */     synchronized (this)
/*     */     {
/* 113 */       if (this.downloader != null)
/*     */       {
/* 115 */         to_destroy = this.downloader;
/*     */         
/* 117 */         this.downloader = null;
/*     */       }
/*     */     }
/*     */     
/* 121 */     if (to_destroy != null)
/*     */     {
/* 123 */       to_destroy.destroy(new ExternalSeedException("deactivated"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void destoyed(Downloader dead)
/*     */   {
/* 131 */     synchronized (this)
/*     */     {
/* 133 */       if (this.downloader == dead)
/*     */       {
/* 135 */         this.downloader = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void download(int length, ExternalSeedHTTPDownloaderListener listener, boolean con_fail_is_perm_fail)
/*     */     throws ExternalSeedException
/*     */   {
/* 148 */     throw new ExternalSeedException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadSocket(int length, ExternalSeedHTTPDownloaderListener listener, boolean con_fail_is_perm_fail)
/*     */     throws ExternalSeedException
/*     */   {
/* 159 */     throw new ExternalSeedException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLastResponse()
/*     */   {
/* 165 */     return this.last_response;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLast503RetrySecs()
/*     */   {
/* 171 */     return this.last_response_retry_after_secs;
/*     */   }
/*     */   
/*     */ 
/*     */   protected class Downloader
/*     */     implements SEPasswordListener
/*     */   {
/*     */     private ExternalSeedHTTPDownloaderListener listener;
/*     */     
/*     */     private boolean con_fail_is_perm_fail;
/*     */     
/*     */     private volatile boolean destroyed;
/* 183 */     private List<ExternalSeedHTTPDownloaderLinear.Request> requests = new ArrayList();
/*     */     
/* 185 */     private RandomAccessFile raf = null;
/* 186 */     private File scratch_file = null;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected Downloader(ExternalSeedHTTPDownloaderListener _listener, boolean _con_fail_is_perm_fail)
/*     */     {
/* 193 */       this.listener = _listener;
/* 194 */       this.con_fail_is_perm_fail = _con_fail_is_perm_fail;
/*     */       
/* 196 */       new AEThread2("ES:downloader", true)
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 201 */           ExternalSeedHTTPDownloaderLinear.Downloader.this.download();
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void download()
/*     */     {
/* 209 */       boolean connected = false;
/* 210 */       String outcome = "";
/*     */       
/* 212 */       AEProxyFactory.PluginProxy plugin_proxy = null;
/*     */       
/* 214 */       boolean proxy_ok = false;
/*     */       try
/*     */       {
/* 217 */         URL original_url = ExternalSeedHTTPDownloaderLinear.this.very_original_url;
/* 218 */         URL current_url = original_url;
/*     */         
/* 220 */         Proxy current_proxy = null;
/*     */         
/* 222 */         if (AENetworkClassifier.categoriseAddress(original_url.getHost()) != "Public")
/*     */         {
/* 224 */           plugin_proxy = AEProxyFactory.getPluginProxy("webseed", original_url);
/*     */           
/* 226 */           if (plugin_proxy != null)
/*     */           {
/* 228 */             current_url = plugin_proxy.getURL();
/*     */             
/* 230 */             current_proxy = plugin_proxy.getProxy();
/*     */           }
/*     */         }
/*     */         
/* 234 */         InputStream is = null;
/*     */         try
/*     */         {
/* 237 */           SESecurityManager.setThreadPasswordHandler(this);
/*     */           
/* 239 */           if (NetworkAdmin.getSingleton().hasMissingForcedBind())
/*     */           {
/* 241 */             throw new ExternalSeedException("Forced bind address is missing");
/*     */           }
/*     */           
/* 244 */           synchronized (this)
/*     */           {
/* 246 */             if (this.destroyed)
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
/* 436 */               SESecurityManager.unsetThreadPasswordHandler();
/*     */               
/*     */ 
/*     */ 
/* 440 */               if (is != null) {
/*     */                 try
/*     */                 {
/* 443 */                   is.close();
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               return;
/*     */             }
/* 251 */             this.scratch_file = AETemporaryFileHandler.createTempFile();
/*     */             
/* 253 */             this.raf = new RandomAccessFile(this.scratch_file, "rw");
/*     */           }
/*     */           
/*     */ 
/*     */           HttpURLConnection connection;
/*     */           
/*     */ 
/*     */           HttpURLConnection connection;
/*     */           
/* 262 */           if (current_proxy == null)
/*     */           {
/* 264 */             connection = (HttpURLConnection)current_url.openConnection();
/*     */           }
/*     */           else
/*     */           {
/* 268 */             connection = (HttpURLConnection)current_url.openConnection(current_proxy);
/*     */             
/* 270 */             connection.setRequestProperty("HOST", plugin_proxy.getURLHostRewrite() + (original_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(original_url.getPort()).toString()));
/*     */           }
/*     */           
/* 273 */           connection.setRequestProperty("Connection", "Keep-Alive");
/* 274 */           connection.setRequestProperty("User-Agent", ExternalSeedHTTPDownloaderLinear.this.user_agent);
/*     */           
/* 276 */           int time_remaining = this.listener.getPermittedTime();
/*     */           
/* 278 */           if (time_remaining > 0)
/*     */           {
/* 280 */             connection.setConnectTimeout(time_remaining);
/*     */           }
/*     */           
/* 283 */           connection.connect();
/*     */           
/* 285 */           proxy_ok = true;
/*     */           
/* 287 */           time_remaining = this.listener.getPermittedTime();
/*     */           
/* 289 */           if (time_remaining < 0)
/*     */           {
/* 291 */             throw new IOException("Timeout during connect");
/*     */           }
/*     */           
/* 294 */           connection.setReadTimeout(time_remaining);
/*     */           
/* 296 */           connected = true;
/*     */           
/* 298 */           int response = connection.getResponseCode();
/*     */           
/* 300 */           ExternalSeedHTTPDownloaderLinear.this.last_response = response;
/*     */           
/* 302 */           ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs = -1;
/*     */           
/* 304 */           if (response == 503)
/*     */           {
/*     */ 
/*     */ 
/* 308 */             long retry_after_date = connection.getHeaderFieldDate("Retry-After", -1L);
/*     */             
/* 310 */             if (retry_after_date <= -1L)
/*     */             {
/* 312 */               ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs = connection.getHeaderFieldInt("Retry-After", -1);
/*     */             }
/*     */             else
/*     */             {
/* 316 */               ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs = ((int)((retry_after_date - System.currentTimeMillis()) / 1000L));
/*     */               
/* 318 */               if (ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs < 0)
/*     */               {
/* 320 */                 ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs = -1;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 325 */           is = connection.getInputStream();
/*     */           
/* 327 */           if ((response == 202) || (response == 200) || (response == 206))
/*     */           {
/*     */ 
/*     */ 
/* 331 */             byte[] buffer = new byte[65536];
/*     */             
/* 333 */             int requests_outstanding = 1;
/*     */             
/* 335 */             while (!this.destroyed)
/*     */             {
/* 337 */               int permitted = this.listener.getPermittedBytes();
/*     */               
/*     */ 
/*     */ 
/* 341 */               if ((requests_outstanding == 0) || (permitted < 1))
/*     */               {
/* 343 */                 permitted = 1;
/*     */                 
/* 345 */                 Thread.sleep(100L);
/*     */               }
/*     */               
/* 348 */               int len = is.read(buffer, 0, Math.min(permitted, buffer.length));
/*     */               
/* 350 */               if (len <= 0) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 355 */               synchronized (this)
/*     */               {
/*     */                 try {
/* 358 */                   this.raf.write(buffer, 0, len);
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */ 
/* 364 */                   outcome = "Write failed: " + e.getMessage();
/*     */                   
/* 366 */                   ExternalSeedException error = new ExternalSeedException(outcome, e);
/*     */                   
/* 368 */                   error.setPermanentFailure(true);
/*     */                   
/* 370 */                   throw error;
/*     */                 }
/*     */               }
/*     */               
/* 374 */               requests_outstanding = checkRequests();
/*     */             }
/*     */             
/* 377 */             checkRequests();
/*     */           }
/*     */           else
/*     */           {
/* 381 */             outcome = "Connection failed: " + connection.getResponseMessage();
/*     */             
/* 383 */             ExternalSeedException error = new ExternalSeedException(outcome);
/*     */             
/* 385 */             error.setPermanentFailure(true);
/*     */             
/* 387 */             throw error;
/*     */           }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 436 */           SESecurityManager.unsetThreadPasswordHandler();
/*     */           
/*     */ 
/*     */ 
/* 440 */           if (is != null) {
/*     */             try
/*     */             {
/* 443 */               is.close();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           ExternalSeedException error;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           ExternalSeedException excep;
/*     */           
/*     */ 
/*     */ 
/* 460 */           if (plugin_proxy == null) {
/*     */             return;
/*     */           }
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/* 391 */           if ((this.con_fail_is_perm_fail) && (!connected))
/*     */           {
/* 393 */             outcome = "Connection failed: " + e.getMessage();
/*     */             
/* 395 */             error = new ExternalSeedException(outcome);
/*     */             
/* 397 */             error.setPermanentFailure(true);
/*     */             
/* 399 */             throw error;
/*     */           }
/*     */           
/*     */ 
/* 403 */           outcome = "Connection failed: " + Debug.getNestedExceptionMessage(e);
/*     */           
/* 405 */           if (ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs >= 0)
/*     */           {
/* 407 */             outcome = outcome + ", Retry-After: " + ExternalSeedHTTPDownloaderLinear.this.last_response_retry_after_secs + " seconds";
/*     */           }
/*     */           
/* 410 */           excep = new ExternalSeedException(outcome, e);
/*     */           
/* 412 */           if ((e instanceof FileNotFoundException))
/*     */           {
/* 414 */             excep.setPermanentFailure(true);
/*     */           }
/*     */           
/* 417 */           throw excep;
/*     */         }
/*     */         catch (ExternalSeedException e)
/*     */         {
/* 421 */           throw e;
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 425 */           if ((e instanceof ExternalSeedException))
/*     */           {
/* 427 */             throw ((ExternalSeedException)e);
/*     */           }
/*     */           
/* 430 */           outcome = "Connection failed: " + Debug.getNestedExceptionMessage(e);
/*     */           
/* 432 */           throw new ExternalSeedException("Connection failed", e);
/*     */         }
/*     */         finally
/*     */         {
/* 436 */           SESecurityManager.unsetThreadPasswordHandler();
/*     */           
/*     */ 
/*     */ 
/* 440 */           if (is != null) {
/*     */             try
/*     */             {
/* 443 */               is.close();
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
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
/*     */ 
/*     */ 
/*     */ 
/* 462 */         plugin_proxy.setOK(proxy_ok);
/*     */       }
/*     */       catch (ExternalSeedException e)
/*     */       {
/* 451 */         if ((!connected) && (this.con_fail_is_perm_fail))
/*     */         {
/* 453 */           e.setPermanentFailure(true);
/*     */         }
/*     */         
/* 456 */         destroy(e);
/*     */       }
/*     */       finally
/*     */       {
/* 460 */         if (plugin_proxy != null)
/*     */         {
/* 462 */           plugin_proxy.setOK(proxy_ok);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected ExternalSeedHTTPDownloaderLinear.Request addRequest(long offset, int length, ExternalSeedHTTPDownloaderListener listener)
/*     */       throws ExternalSeedException
/*     */     {
/*     */       ExternalSeedHTTPDownloaderLinear.Request request;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 480 */       synchronized (this)
/*     */       {
/* 482 */         if (this.destroyed)
/*     */         {
/* 484 */           throw new ExternalSeedException("downloader destroyed");
/*     */         }
/*     */         
/* 487 */         request = new ExternalSeedHTTPDownloaderLinear.Request(offset, length, listener);
/*     */         
/* 489 */         this.requests.add(request);
/*     */       }
/*     */       
/* 492 */       checkRequests();
/*     */       
/* 494 */       return request;
/*     */     }
/*     */     
/*     */     protected int checkRequests()
/*     */     {
/*     */       try
/*     */       {
/* 501 */         synchronized (this)
/*     */         {
/* 503 */           if (this.raf == null)
/*     */           {
/*     */ 
/*     */ 
/* 507 */             return this.requests.size();
/*     */           }
/*     */           
/* 510 */           long pos = this.raf.getFilePointer();
/*     */           
/* 512 */           Iterator<ExternalSeedHTTPDownloaderLinear.Request> it = this.requests.iterator();
/*     */           
/* 514 */           while (it.hasNext())
/*     */           {
/* 516 */             ExternalSeedHTTPDownloaderLinear.Request request = (ExternalSeedHTTPDownloaderLinear.Request)it.next();
/*     */             
/* 518 */             long end = request.getOffset() + request.getLength();
/*     */             
/* 520 */             if (pos >= end)
/*     */             {
/* 522 */               ExternalSeedHTTPDownloaderListener listener = request.getListener();
/*     */               try
/*     */               {
/* 525 */                 this.raf.seek(request.getOffset());
/*     */                 
/* 527 */                 int total = 0;
/*     */                 
/* 529 */                 while (total < request.getLength())
/*     */                 {
/* 531 */                   byte[] buffer = listener.getBuffer();
/* 532 */                   int buffer_position = listener.getBufferPosition();
/* 533 */                   int buffer_len = listener.getBufferLength();
/*     */                   
/* 535 */                   int space = buffer_len - buffer_position;
/*     */                   
/* 537 */                   if (this.raf.read(buffer, buffer_position, space) != space)
/*     */                   {
/* 539 */                     throw new IOException("Error reading scratch file");
/*     */                   }
/*     */                   
/* 542 */                   total += space;
/*     */                   
/* 544 */                   listener.reportBytesRead(space);
/*     */                   
/* 546 */                   listener.done();
/*     */                 }
/*     */               }
/*     */               finally {
/* 550 */                 this.raf.seek(pos);
/*     */               }
/*     */               
/* 553 */               request.complete();
/*     */               
/* 555 */               it.remove();
/*     */             }
/*     */           }
/*     */           
/* 559 */           return this.requests.size();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 567 */         return 0;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 563 */         Debug.out(e);
/*     */         
/* 565 */         destroy(new ExternalSeedException("read failed", e));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void destroy(ExternalSeedException error)
/*     */     {
/* 575 */       synchronized (this)
/*     */       {
/* 577 */         if (this.destroyed)
/*     */         {
/* 579 */           return;
/*     */         }
/*     */         
/* 582 */         this.destroyed = true;
/*     */         
/* 584 */         if (this.raf != null) {
/*     */           try
/*     */           {
/* 587 */             this.raf.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         
/*     */ 
/* 593 */         if (this.scratch_file != null)
/*     */         {
/* 595 */           this.scratch_file.delete();
/*     */         }
/*     */         
/* 598 */         for (ExternalSeedHTTPDownloaderLinear.Request r : this.requests)
/*     */         {
/* 600 */           r.destroy(error);
/*     */         }
/*     */         
/* 603 */         this.requests.clear();
/*     */       }
/*     */       
/* 606 */       ExternalSeedHTTPDownloaderLinear.this.destoyed(this);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*     */     {
/* 614 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setAuthenticationOutcome(String realm, URL tracker, boolean success) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public void clearPasswords() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class Request
/*     */   {
/*     */     private long offset;
/*     */     
/*     */ 
/*     */     private int length;
/*     */     
/*     */ 
/*     */     private ExternalSeedHTTPDownloaderListener listener;
/*     */     
/* 638 */     private AESemaphore sem = new AESemaphore("ES:wait");
/*     */     
/*     */ 
/*     */ 
/*     */     private volatile ExternalSeedException exception;
/*     */     
/*     */ 
/*     */ 
/*     */     protected Request(long _offset, int _length, ExternalSeedHTTPDownloaderListener _listener)
/*     */     {
/* 648 */       this.offset = _offset;
/* 649 */       this.length = _length;
/* 650 */       this.listener = _listener;
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getOffset()
/*     */     {
/* 656 */       return this.offset;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int getLength()
/*     */     {
/* 662 */       return this.length;
/*     */     }
/*     */     
/*     */ 
/*     */     protected ExternalSeedHTTPDownloaderListener getListener()
/*     */     {
/* 668 */       return this.listener;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void complete()
/*     */     {
/* 674 */       this.sem.release();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void destroy(ExternalSeedException e)
/*     */     {
/* 681 */       this.exception = e;
/*     */       
/* 683 */       this.sem.release();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean waitFor(int timeout)
/*     */       throws ExternalSeedException
/*     */     {
/* 692 */       if (!this.sem.reserve(timeout))
/*     */       {
/* 694 */         return false;
/*     */       }
/*     */       
/* 697 */       if (this.exception != null)
/*     */       {
/* 699 */         throw this.exception;
/*     */       }
/*     */       
/* 702 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/util/ExternalSeedHTTPDownloaderLinear.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */