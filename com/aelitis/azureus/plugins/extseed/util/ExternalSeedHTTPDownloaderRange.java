/*     */ package com.aelitis.azureus.plugins.extseed.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.Proxy;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExternalSeedHTTPDownloaderRange
/*     */   implements ExternalSeedHTTPDownloader, SEPasswordListener
/*     */ {
/*     */   public static final String NL = "\r\n";
/*     */   private final URL very_original_url;
/*     */   private String user_agent;
/*     */   private URL redirected_url;
/*     */   private int consec_redirect_fails;
/*     */   private int last_response;
/*     */   private int last_response_retry_after_secs;
/*     */   
/*     */   public ExternalSeedHTTPDownloaderRange(URL _url, String _user_agent)
/*     */   {
/*  76 */     this.very_original_url = _url;
/*  77 */     this.user_agent = _user_agent;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/*  83 */     return this.very_original_url;
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
/*  94 */     download(new String[0], new String[0], length, listener, con_fail_is_perm_fail);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadRange(long offset, int length, ExternalSeedHTTPDownloaderListener listener, boolean con_fail_is_perm_fail)
/*     */     throws ExternalSeedException
/*     */   {
/* 106 */     download(new String[] { "Range" }, new String[] { "bytes=" + offset + "-" + (offset + length - 1L) }, length, listener, con_fail_is_perm_fail);
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
/*     */   public void download(String[] prop_names, String[] prop_values, int length, ExternalSeedHTTPDownloaderListener listener, boolean con_fail_is_perm_fail)
/*     */     throws ExternalSeedException
/*     */   {
/* 122 */     boolean connected = false;
/*     */     
/* 124 */     InputStream is = null;
/*     */     
/* 126 */     String outcome = "";
/*     */     
/* 128 */     AEProxyFactory.PluginProxy plugin_proxy = null;
/*     */     
/* 130 */     boolean proxy_ok = false;
/*     */     try
/*     */     {
/* 133 */       SESecurityManager.setThreadPasswordHandler(this);
/*     */       
/* 135 */       if (NetworkAdmin.getSingleton().hasMissingForcedBind())
/*     */       {
/* 137 */         throw new ExternalSeedException("Forced bind address is missing");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 145 */       Set<String> redirect_urls = new HashSet();
/*     */       HttpURLConnection connection;
/*     */       int response;
/*     */       label612:
/*     */       for (;;) {
/* 150 */         URL original_url = this.redirected_url == null ? this.very_original_url : this.redirected_url;
/* 151 */         URL current_url = original_url;
/*     */         
/* 153 */         if (plugin_proxy != null)
/*     */         {
/* 155 */           plugin_proxy.setOK(true);
/*     */           
/* 157 */           plugin_proxy = null;
/*     */         }
/*     */         
/* 160 */         Proxy current_proxy = null;
/*     */         
/* 162 */         if (AENetworkClassifier.categoriseAddress(original_url.getHost()) != "Public")
/*     */         {
/* 164 */           plugin_proxy = AEProxyFactory.getPluginProxy("webseed", original_url);
/*     */           
/* 166 */           if (plugin_proxy != null)
/*     */           {
/* 168 */             current_url = plugin_proxy.getURL();
/*     */             
/* 170 */             current_proxy = plugin_proxy.getProxy();
/*     */           }
/*     */         }
/*     */         
/* 174 */         for (int ssl_loop = 0;; ssl_loop++) { if (ssl_loop >= 2)
/*     */             break label612;
/*     */           try { HttpURLConnection connection;
/* 177 */             if (current_proxy == null)
/*     */             {
/* 179 */               connection = (HttpURLConnection)current_url.openConnection();
/*     */             }
/*     */             else
/*     */             {
/* 183 */               connection = (HttpURLConnection)current_url.openConnection(current_proxy);
/*     */             }
/*     */             
/* 186 */             if ((connection instanceof HttpsURLConnection))
/*     */             {
/* 188 */               HttpsURLConnection ssl_con = (HttpsURLConnection)connection;
/*     */               
/*     */ 
/*     */ 
/* 192 */               ssl_con.setHostnameVerifier(new HostnameVerifier()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public boolean verify(String host, SSLSession session)
/*     */                 {
/*     */ 
/*     */ 
/* 200 */                   return true;
/*     */                 }
/*     */                 
/* 203 */               });
/* 204 */               TrustManager[] tms_delegate = SESecurityManager.getAllTrustingTrustManager();
/*     */               
/* 206 */               SSLContext sc = SSLContext.getInstance("SSL");
/*     */               
/* 208 */               sc.init(null, tms_delegate, RandomUtils.SECURE_RANDOM);
/*     */               
/* 210 */               SSLSocketFactory factory = sc.getSocketFactory();
/*     */               
/* 212 */               ssl_con.setSSLSocketFactory(factory);
/*     */             }
/*     */             
/* 215 */             connection.setRequestProperty("Connection", "Keep-Alive");
/* 216 */             connection.setRequestProperty("User-Agent", this.user_agent);
/*     */             
/* 218 */             for (int i = 0; i < prop_names.length; i++)
/*     */             {
/* 220 */               connection.setRequestProperty(prop_names[i], prop_values[i]);
/*     */             }
/*     */             
/* 223 */             if (plugin_proxy != null)
/*     */             {
/* 225 */               connection.setRequestProperty("HOST", plugin_proxy.getURLHostRewrite() + (original_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(original_url.getPort()).toString()));
/*     */             }
/*     */             
/* 228 */             int time_remaining = listener.getPermittedTime();
/*     */             
/* 230 */             if (time_remaining > 0)
/*     */             {
/* 232 */               connection.setConnectTimeout(time_remaining);
/*     */             }
/*     */             
/* 235 */             connection.connect();
/*     */             
/* 237 */             time_remaining = listener.getPermittedTime();
/*     */             
/* 239 */             if (time_remaining < 0)
/*     */             {
/* 241 */               throw new IOException("Timeout during connect");
/*     */             }
/*     */             
/* 244 */             connection.setReadTimeout(time_remaining);
/*     */             
/* 246 */             connected = true;
/*     */             
/* 248 */             response = connection.getResponseCode();
/*     */             
/* 250 */             if ((response == 202) || (response == 200) || (response == 206))
/*     */             {
/*     */ 
/*     */ 
/* 254 */               if (this.redirected_url != null)
/*     */               {
/* 256 */                 this.consec_redirect_fails = 0;
/*     */               }
/*     */               
/*     */               break label615;
/*     */             }
/* 261 */             if ((response == 302) || (response == 301))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 266 */               String move_to = connection.getHeaderField("location");
/*     */               
/* 268 */               if (move_to != null)
/*     */               {
/* 270 */                 if ((redirect_urls.contains(move_to)) || (redirect_urls.size() > 32))
/*     */                 {
/* 272 */                   throw new ExternalSeedException("redirect loop");
/*     */                 }
/*     */                 
/* 275 */                 redirect_urls.add(move_to);
/*     */                 
/* 277 */                 this.redirected_url = new URL(move_to);
/*     */                 
/* 279 */                 break;
/*     */               }
/*     */             }
/*     */             
/* 283 */             if (this.redirected_url == null) {
/*     */               break label615;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 290 */             this.consec_redirect_fails += 1;
/*     */             
/* 292 */             this.redirected_url = null;
/*     */           }
/*     */           catch (SSLException e)
/*     */           {
/* 296 */             if ((ssl_loop != 0) || 
/*     */             
/* 298 */               (SESecurityManager.installServerCertificates(current_url) == null))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 306 */               throw e;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       label615:
/*     */       
/* 315 */       if (plugin_proxy == null)
/*     */       {
/* 317 */         URL final_url = connection.getURL();
/*     */         
/* 319 */         if ((this.consec_redirect_fails < 10) && (!this.very_original_url.toExternalForm().equals(final_url.toExternalForm())))
/*     */         {
/* 321 */           this.redirected_url = final_url;
/*     */         }
/*     */       }
/*     */       
/* 325 */       this.last_response = response;
/*     */       
/* 327 */       this.last_response_retry_after_secs = -1;
/*     */       
/* 329 */       if (response == 503)
/*     */       {
/*     */ 
/*     */ 
/* 333 */         long retry_after_date = connection.getHeaderFieldDate("Retry-After", -1L);
/*     */         
/* 335 */         if (retry_after_date <= -1L)
/*     */         {
/* 337 */           this.last_response_retry_after_secs = connection.getHeaderFieldInt("Retry-After", -1);
/*     */         }
/*     */         else
/*     */         {
/* 341 */           this.last_response_retry_after_secs = ((int)((retry_after_date - System.currentTimeMillis()) / 1000L));
/*     */           
/* 343 */           if (this.last_response_retry_after_secs < 0)
/*     */           {
/* 345 */             this.last_response_retry_after_secs = -1;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 350 */       is = connection.getInputStream();
/*     */       
/* 352 */       proxy_ok = true;
/*     */       
/* 354 */       if ((response == 202) || (response == 200) || (response == 206))
/*     */       {
/*     */ 
/*     */ 
/* 358 */         int pos = 0;
/*     */         
/* 360 */         byte[] buffer = null;
/* 361 */         int buffer_pos = 0;
/* 362 */         int buffer_len = 0;
/*     */         
/* 364 */         while (pos < length)
/*     */         {
/* 366 */           if (buffer == null)
/*     */           {
/* 368 */             buffer = listener.getBuffer();
/* 369 */             buffer_pos = listener.getBufferPosition();
/* 370 */             buffer_len = listener.getBufferLength();
/*     */           }
/*     */           
/* 373 */           listener.setBufferPosition(buffer_pos);
/*     */           
/* 375 */           int to_read = buffer_len - buffer_pos;
/*     */           
/* 377 */           int permitted = listener.getPermittedBytes();
/*     */           
/* 379 */           if (permitted < to_read)
/*     */           {
/* 381 */             to_read = permitted;
/*     */           }
/*     */           
/* 384 */           int len = is.read(buffer, buffer_pos, to_read);
/*     */           
/* 386 */           if (len < 0) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 391 */           listener.reportBytesRead(len);
/*     */           
/* 393 */           pos += len;
/*     */           
/* 395 */           buffer_pos += len;
/*     */           
/* 397 */           if (buffer_pos == buffer_len)
/*     */           {
/* 399 */             listener.done();
/*     */             
/* 401 */             buffer = null;
/* 402 */             buffer_pos = 0;
/*     */           }
/*     */         }
/*     */         
/* 406 */         if (pos != length)
/*     */         {
/*     */           String log_str;
/*     */           String log_str;
/* 410 */           if (buffer == null)
/*     */           {
/* 412 */             log_str = "No buffer assigned";
/*     */           }
/*     */           else
/*     */           {
/* 416 */             log_str = new String(buffer, 0, length);
/*     */             
/* 418 */             if (log_str.length() > 64)
/*     */             {
/* 420 */               log_str = log_str.substring(0, 64);
/*     */             }
/*     */           }
/*     */           
/* 424 */           outcome = "Connection failed: data too short - " + length + "/" + pos + " [" + log_str + "]";
/*     */           
/* 426 */           throw new ExternalSeedException(outcome);
/*     */         }
/*     */         
/* 429 */         outcome = "read " + pos + " bytes";
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 435 */         outcome = "Connection failed: " + connection.getResponseMessage();
/*     */         
/* 437 */         ExternalSeedException error = new ExternalSeedException(outcome);
/*     */         
/* 439 */         error.setPermanentFailure(true);
/*     */         
/* 441 */         throw error;
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 445 */       if ((con_fail_is_perm_fail) && (!connected))
/*     */       {
/* 447 */         outcome = "Connection failed: " + e.getMessage();
/*     */         
/* 449 */         ExternalSeedException error = new ExternalSeedException(outcome);
/*     */         
/* 451 */         error.setPermanentFailure(true);
/*     */         
/* 453 */         throw error;
/*     */       }
/*     */       
/*     */ 
/* 457 */       outcome = "Connection failed: " + Debug.getNestedExceptionMessage(e);
/*     */       
/* 459 */       if (this.last_response_retry_after_secs >= 0)
/*     */       {
/* 461 */         outcome = outcome + ", Retry-After: " + this.last_response_retry_after_secs + " seconds";
/*     */       }
/*     */       
/* 464 */       ExternalSeedException excep = new ExternalSeedException(outcome, e);
/*     */       
/* 466 */       if ((e instanceof FileNotFoundException))
/*     */       {
/* 468 */         excep.setPermanentFailure(true);
/*     */       }
/*     */       
/* 471 */       throw excep;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 475 */       if ((e instanceof ExternalSeedException))
/*     */       {
/* 477 */         throw ((ExternalSeedException)e);
/*     */       }
/*     */       
/* 480 */       outcome = "Connection failed: " + Debug.getNestedExceptionMessage(e);
/*     */       
/* 482 */       throw new ExternalSeedException("Connection failed", e);
/*     */     }
/*     */     finally
/*     */     {
/* 486 */       SESecurityManager.unsetThreadPasswordHandler();
/*     */       
/*     */ 
/*     */ 
/* 490 */       if (is != null) {
/*     */         try
/*     */         {
/* 493 */           is.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 500 */       if (plugin_proxy != null)
/*     */       {
/* 502 */         plugin_proxy.setOK(proxy_ok);
/*     */       }
/*     */     }
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
/* 515 */     downloadSocket(new String[0], new String[0], length, listener, con_fail_is_perm_fail);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadSocket(String[] prop_names, String[] prop_values, int length, ExternalSeedHTTPDownloaderListener listener, boolean con_fail_is_perm_fail)
/*     */     throws ExternalSeedException
/*     */   {
/* 528 */     Socket socket = null;
/*     */     
/* 530 */     boolean connected = false;
/*     */     
/* 532 */     AEProxyFactory.PluginProxy plugin_proxy = null;
/* 533 */     boolean proxy_ok = false;
/*     */     try
/*     */     {
/* 536 */       String output_header = "GET " + this.very_original_url.getPath() + "?" + this.very_original_url.getQuery() + " HTTP/1.1" + "\r\n" + "Host: " + this.very_original_url.getHost() + (this.very_original_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(this.very_original_url.getPort()).toString()) + "\r\n" + "Accept: */*" + "\r\n" + "Connection: Close" + "\r\n" + "User-Agent: " + this.user_agent + "\r\n";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 543 */       for (int i = 0; i < prop_names.length; i++)
/*     */       {
/* 545 */         output_header = output_header + prop_names[i] + ":" + prop_values[i] + "\r\n";
/*     */       }
/*     */       
/* 548 */       output_header = output_header + "\r\n";
/*     */       
/* 550 */       int time_remaining = listener.getPermittedTime();
/*     */       
/* 552 */       URL original_url = this.very_original_url;
/* 553 */       URL current_url = original_url;
/*     */       
/* 555 */       Proxy current_proxy = null;
/*     */       
/* 557 */       if (AENetworkClassifier.categoriseAddress(this.very_original_url.getHost()) != "Public")
/*     */       {
/* 559 */         plugin_proxy = AEProxyFactory.getPluginProxy("webseed", original_url);
/*     */         
/* 561 */         if (plugin_proxy != null)
/*     */         {
/* 563 */           current_url = plugin_proxy.getURL();
/*     */           
/* 565 */           current_proxy = plugin_proxy.getProxy();
/*     */         }
/*     */       }
/*     */       
/* 569 */       if (time_remaining > 0)
/*     */       {
/* 571 */         if (current_proxy == null)
/*     */         {
/* 573 */           socket = new Socket();
/*     */         }
/*     */         else
/*     */         {
/* 577 */           socket = new Socket(current_proxy);
/*     */         }
/*     */         
/* 580 */         socket.connect(new InetSocketAddress(current_url.getHost(), current_url.getPort() == -1 ? current_url.getDefaultPort() : current_url.getPort()), time_remaining);
/*     */ 
/*     */ 
/*     */       }
/* 584 */       else if (current_proxy == null)
/*     */       {
/* 586 */         socket = new Socket(current_url.getHost(), current_url.getPort() == -1 ? current_url.getDefaultPort() : current_url.getPort());
/*     */       }
/*     */       else
/*     */       {
/* 590 */         socket = new Socket(current_proxy);
/*     */         
/* 592 */         socket.connect(new InetSocketAddress(current_url.getHost(), current_url.getPort() == -1 ? current_url.getDefaultPort() : current_url.getPort()));
/*     */       }
/*     */       
/*     */ 
/* 596 */       connected = true;
/*     */       
/* 598 */       proxy_ok = true;
/*     */       
/* 600 */       time_remaining = listener.getPermittedTime();
/*     */       
/* 602 */       if (time_remaining < 0)
/*     */       {
/* 604 */         throw new IOException("Timeout during connect");
/*     */       }
/* 606 */       if (time_remaining > 0)
/*     */       {
/* 608 */         socket.setSoTimeout(time_remaining);
/*     */       }
/*     */       
/* 611 */       OutputStream os = socket.getOutputStream();
/*     */       
/* 613 */       os.write(output_header.getBytes("ISO-8859-1"));
/*     */       
/* 615 */       os.flush();
/*     */       
/* 617 */       InputStream is = socket.getInputStream();
/*     */       try
/*     */       {
/* 620 */         String input_header = "";
/*     */         
/*     */         for (;;)
/*     */         {
/* 624 */           byte[] buffer = new byte[1];
/*     */           
/* 626 */           int len = is.read(buffer);
/*     */           
/* 628 */           if (len < 0)
/*     */           {
/* 630 */             throw new IOException("input too short reading header");
/*     */           }
/*     */           
/* 633 */           input_header = input_header + (char)buffer[0];
/*     */           
/* 635 */           if (input_header.endsWith("\r\n\r\n")) {
/*     */             break;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 643 */         int line_end = input_header.indexOf("\r\n");
/*     */         
/* 645 */         if (line_end == -1)
/*     */         {
/* 647 */           throw new IOException("header too short");
/*     */         }
/*     */         
/* 650 */         String first_line = input_header.substring(0, line_end);
/*     */         
/* 652 */         StringTokenizer tok = new StringTokenizer(first_line, " ");
/*     */         
/* 654 */         tok.nextToken();
/*     */         
/* 656 */         int response = Integer.parseInt(tok.nextToken());
/*     */         
/* 658 */         this.last_response = response;
/*     */         
/* 660 */         this.last_response_retry_after_secs = -1;
/*     */         
/* 662 */         String response_str = tok.nextToken();
/*     */         
/* 664 */         if ((response == 202) || (response == 200) || (response == 206))
/*     */         {
/*     */ 
/*     */ 
/* 668 */           byte[] buffer = null;
/* 669 */           int buffer_pos = 0;
/* 670 */           int buffer_len = 0;
/*     */           
/* 672 */           int pos = 0;
/*     */           
/* 674 */           while (pos < length)
/*     */           {
/* 676 */             if (buffer == null)
/*     */             {
/* 678 */               buffer = listener.getBuffer();
/* 679 */               buffer_pos = listener.getBufferPosition();
/* 680 */               buffer_len = listener.getBufferLength();
/*     */             }
/*     */             
/* 683 */             int to_read = buffer_len - buffer_pos;
/*     */             
/* 685 */             int permitted = listener.getPermittedBytes();
/*     */             
/* 687 */             if (permitted < to_read)
/*     */             {
/* 689 */               to_read = permitted;
/*     */             }
/*     */             
/* 692 */             int len = is.read(buffer, buffer_pos, to_read);
/*     */             
/* 694 */             if (len < 0) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 699 */             listener.reportBytesRead(len);
/*     */             
/* 701 */             pos += len;
/*     */             
/* 703 */             buffer_pos += len;
/*     */             
/* 705 */             if (buffer_pos == buffer_len)
/*     */             {
/* 707 */               listener.done();
/*     */               
/* 709 */               buffer = null;
/* 710 */               buffer_pos = 0;
/*     */             }
/*     */           }
/*     */           
/* 714 */           if (pos != length)
/*     */           {
/*     */             String log_str;
/*     */             String log_str;
/* 718 */             if (buffer == null)
/*     */             {
/* 720 */               log_str = "No buffer assigned";
/*     */             }
/*     */             else
/*     */             {
/* 724 */               log_str = new String(buffer, 0, buffer_pos > 64 ? 64 : buffer_pos);
/*     */             }
/*     */             
/* 727 */             throw new ExternalSeedException("Connection failed: data too short - " + length + "/" + pos + " [last=" + log_str + "]");
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 732 */           if (response == 503)
/*     */           {
/*     */ 
/*     */ 
/* 736 */             String data_str = "";
/*     */             
/*     */             for (;;)
/*     */             {
/* 740 */               byte[] buffer = new byte[1];
/*     */               
/* 742 */               int len = is.read(buffer);
/*     */               
/* 744 */               if (len < 0) {
/*     */                 break;
/*     */               }
/*     */               
/*     */ 
/* 749 */               data_str = data_str + (char)buffer[0];
/*     */             }
/*     */             
/* 752 */             this.last_response_retry_after_secs = Integer.parseInt(data_str);
/*     */             
/*     */ 
/*     */ 
/* 756 */             throw new IOException("Server overloaded");
/*     */           }
/*     */           
/*     */ 
/* 760 */           ExternalSeedException error = new ExternalSeedException("Connection failed: " + response_str);
/*     */           
/* 762 */           error.setPermanentFailure(true);
/*     */           
/* 764 */           throw error;
/*     */         }
/*     */       }
/*     */       finally {
/* 768 */         is.close();
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 773 */       if ((con_fail_is_perm_fail) && (!connected))
/*     */       {
/* 775 */         ExternalSeedException error = new ExternalSeedException("Connection failed: " + e.getMessage());
/*     */         
/* 777 */         error.setPermanentFailure(true);
/*     */         
/* 779 */         throw error;
/*     */       }
/*     */       
/*     */ 
/* 783 */       String outcome = "Connection failed: " + Debug.getNestedExceptionMessage(e);
/*     */       
/* 785 */       if (this.last_response_retry_after_secs >= 0)
/*     */       {
/* 787 */         outcome = outcome + ", Retry-After: " + this.last_response_retry_after_secs + " seconds";
/*     */       }
/*     */       
/* 790 */       throw new ExternalSeedException(outcome, e);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 794 */       if ((e instanceof ExternalSeedException))
/*     */       {
/* 796 */         throw ((ExternalSeedException)e);
/*     */       }
/*     */       
/* 799 */       throw new ExternalSeedException("Connection failed", e);
/*     */     }
/*     */     finally
/*     */     {
/* 803 */       if (socket != null) {
/*     */         try
/*     */         {
/* 806 */           socket.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 812 */       if (plugin_proxy != null)
/*     */       {
/* 814 */         plugin_proxy.setOK(proxy_ok);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deactivate() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*     */   {
/* 829 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAuthenticationOutcome(String realm, URL tracker, boolean success) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearPasswords() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLastResponse()
/*     */   {
/* 848 */     return this.last_response;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLast503RetrySecs()
/*     */   {
/* 854 */     return this.last_response_retry_after_secs;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 862 */       String url_str = "";
/*     */       
/* 864 */       ExternalSeedHTTPDownloader downloader = new ExternalSeedHTTPDownloaderRange(new URL(url_str), "Azureus");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 870 */       downloader.downloadRange(0L, 1, new ExternalSeedHTTPDownloaderListener()
/*     */       {
/*     */         private int position;
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public byte[] getBuffer()
/*     */           throws ExternalSeedException
/*     */         {
/* 881 */           return new byte['Ѐ'];
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void setBufferPosition(int _position)
/*     */         {
/* 888 */           this.position = _position;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getBufferPosition()
/*     */         {
/* 894 */           return this.position;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getBufferLength()
/*     */         {
/* 900 */           return 1024;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public int getPermittedBytes()
/*     */           throws ExternalSeedException
/*     */         {
/* 908 */           return 1024;
/*     */         }
/*     */         
/*     */ 
/*     */         public int getPermittedTime()
/*     */         {
/* 914 */           return Integer.MAX_VALUE;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void reportBytesRead(int num)
/*     */         {
/* 921 */           System.out.println("read " + num);
/*     */         }
/*     */         
/*     */ 
/*     */         public boolean isCancelled()
/*     */         {
/* 927 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 933 */         public void done() { System.out.println("done"); } }, true);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 940 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/util/ExternalSeedHTTPDownloaderRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */