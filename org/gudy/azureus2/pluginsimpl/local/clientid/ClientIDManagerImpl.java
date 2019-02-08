/*     */ package org.gudy.azureus2.pluginsimpl.local.clientid;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.util.NetUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.net.URLEncoder;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.channels.UnsupportedAddressTypeException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
/*     */ import org.gudy.azureus2.core3.util.ThreadPoolTask;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDException;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDGenerator;
/*     */ import org.gudy.azureus2.plugins.clientid.ClientIDManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClientIDManagerImpl
/*     */   implements ClientIDManager
/*     */ {
/*  51 */   private static final LogIDs LOGID = LogIDs.PLUGIN;
/*  52 */   protected static ClientIDManagerImpl singleton = new ClientIDManagerImpl();
/*     */   
/*     */   protected static final char CR = '\r';
/*     */   protected static final char FF = '\n';
/*     */   protected static final String NL = "\r\n";
/*     */   private static final int connect_timeout;
/*     */   private static final int read_timeout;
/*     */   
/*     */   static
/*     */   {
/*  62 */     String connect_timeout_str = System.getProperty("sun.net.client.defaultConnectTimeout");
/*  63 */     String read_timeout_str = System.getProperty("sun.net.client.defaultReadTimeout");
/*     */     
/*  65 */     int ct = 60000;
/*  66 */     int rt = 60000;
/*     */     try
/*     */     {
/*  69 */       ct = Integer.parseInt(connect_timeout_str);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */     try
/*     */     {
/*  75 */       rt = Integer.parseInt(read_timeout_str);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*  80 */     connect_timeout = ct;
/*  81 */     read_timeout = rt;
/*     */   }
/*     */   
/*     */ 
/*     */   public static ClientIDManagerImpl getSingleton()
/*     */   {
/*  87 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ClientIDManagerImpl()
/*     */   {
/*  95 */     this.filter_lock = new Object();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setGenerator(ClientIDGenerator _generator, boolean _use_filter)
/*     */   {
/* 103 */     this.generator = _generator;
/* 104 */     this.use_filter = _use_filter;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 111 */     if (!this.use_filter)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */       String http_proxy = System.getProperty("http.proxyHost");
/* 118 */       String socks_proxy = System.getProperty("socksProxyHost");
/*     */       
/* 120 */       NetworkAdmin network_admin = NetworkAdmin.getSingleton();
/*     */       
/* 122 */       if (network_admin.mustBind())
/*     */       {
/* 124 */         this.filter_override = true;
/*     */         
/* 126 */         this.use_filter = true;
/*     */       }
/*     */       else
/*     */       {
/* 130 */         InetAddress bindIP = network_admin.getSingleHomedServiceBindAddress();
/*     */         
/*     */ 
/* 133 */         if (((http_proxy == null) || (http_proxy.trim().length() == 0)) && ((socks_proxy == null) || (socks_proxy.trim().length() == 0)) && (bindIP != null) && (!bindIP.isAnyLocalAddress()))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 138 */           int ips = 0;
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 145 */             List<NetworkInterface> x = NetUtils.getNetworkInterfaces();
/*     */             
/* 147 */             for (NetworkInterface network_interface : x)
/*     */             {
/* 149 */               Enumeration<InetAddress> addresses = network_interface.getInetAddresses();
/*     */               
/* 151 */               while (addresses.hasMoreElements())
/*     */               {
/* 153 */                 InetAddress address = (InetAddress)addresses.nextElement();
/*     */                 
/* 155 */                 if (!address.isLoopbackAddress())
/*     */                 {
/* 157 */                   ips++;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 163 */             Logger.log(new LogEvent(LOGID, "", e));
/*     */           }
/*     */           
/* 166 */           if (ips > 1)
/*     */           {
/* 168 */             this.filter_override = true;
/*     */             
/* 170 */             this.use_filter = true;
/*     */             
/* 172 */             if (Logger.isEnabled()) {
/* 173 */               Logger.log(new LogEvent(LOGID, "ClientIDManager: overriding filter option to support local bind IP"));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 181 */     setupFilter(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setupFilter(boolean force)
/*     */   {
/* 188 */     synchronized (this.filter_lock)
/*     */     {
/* 190 */       if (!this.use_filter)
/*     */       {
/* 192 */         if (force)
/*     */         {
/* 194 */           this.use_filter = true;
/*     */         }
/*     */         else
/*     */         {
/* 198 */           return;
/*     */         }
/*     */       }
/*     */       
/* 202 */       if (this.filter_port != 0)
/*     */       {
/* 204 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 208 */         this.thread_pool = new ThreadPool("ClientIDManager", 32);
/*     */         
/* 210 */         int timeout = connect_timeout + read_timeout;
/*     */         
/* 212 */         this.thread_pool.setExecutionLimit(timeout);
/*     */         
/* 214 */         final ServerSocket ss = new ServerSocket(0, 1024, InetAddress.getByName("127.0.0.1"));
/*     */         
/* 216 */         this.filter_port = ss.getLocalPort();
/*     */         
/* 218 */         ss.setReuseAddress(true);
/*     */         
/* 220 */         new AEThread2("ClientIDManager::filterloop")
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/* 225 */             long failed_accepts = 0L;
/*     */             try
/*     */             {
/*     */               for (;;)
/*     */               {
/* 230 */                 Socket socket = ss.accept();
/*     */                 
/* 232 */                 failed_accepts = 0L;
/*     */                 
/* 234 */                 ClientIDManagerImpl.this.thread_pool.run(new ClientIDManagerImpl.httpFilter(ClientIDManagerImpl.this, socket));
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 238 */               failed_accepts += 1L;
/*     */               
/* 240 */               if (Logger.isEnabled()) {
/* 241 */                 Logger.log(new LogEvent(ClientIDManagerImpl.LOGID, "ClientIDManager: listener failed on port " + ClientIDManagerImpl.this.filter_port, e));
/*     */               }
/*     */               
/*     */ 
/* 245 */               if (failed_accepts > 10L)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 250 */                 Logger.logTextResource(new LogAlert(false, 3, "Network.alert.acceptfail"), new String[] { "" + ClientIDManagerImpl.this.filter_port, "TCP" });
/*     */                 
/*     */ 
/*     */ 
/* 254 */                 ClientIDManagerImpl.this.use_filter = false;
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 263 */         if (Logger.isEnabled()) {
/* 264 */           Logger.log(new LogEvent(LOGID, "ClientIDManager: listener established on port " + this.filter_port));
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 269 */         Logger.logTextResource(new LogAlert(false, 3, "Tracker.alert.listenfail"), new String[] { "" + this.filter_port });
/*     */         
/*     */ 
/*     */ 
/* 273 */         if (Logger.isEnabled()) {
/* 274 */           Logger.log(new LogEvent(LOGID, "ClientIDManager: listener failed on port " + this.filter_port, e));
/*     */         }
/*     */         
/* 277 */         this.use_filter = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ClientIDGenerator getGenerator()
/*     */   {
/* 285 */     return this.generator;
/*     */   }
/*     */   
/*     */   private ClientIDGenerator generator;
/*     */   private volatile boolean use_filter;
/*     */   private boolean filter_override;
/*     */   private ThreadPool thread_pool;
/*     */   private Object filter_lock;
/*     */   private int filter_port;
/*     */   public byte[] generatePeerID(byte[] hash, boolean for_tracker) throws ClientIDException {
/* 295 */     return this.generator.generatePeerID(hash, for_tracker);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getProperty(byte[] hash, String property_name)
/*     */   {
/* 303 */     return this.generator.getProperty(hash, property_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generateHTTPProperties(byte[] hash, Properties properties)
/*     */     throws ClientIDException
/*     */   {
/* 313 */     Boolean sni_hack = (Boolean)properties.get("SNI-Hack");
/*     */     
/* 315 */     if ((sni_hack != null) && (sni_hack.booleanValue()))
/*     */     {
/* 317 */       if (!this.use_filter)
/*     */       {
/* 319 */         setupFilter(true);
/*     */       }
/*     */     }
/*     */     
/* 323 */     boolean filter_it = this.use_filter;
/*     */     
/* 325 */     if (filter_it)
/*     */     {
/* 327 */       URL url = (URL)properties.get("URL");
/*     */       
/* 329 */       String protocol = url.getProtocol();
/* 330 */       String host = url.getHost();
/*     */       
/* 332 */       if ((host.equals("127.0.0.1")) || (protocol.equals("ws")) || (protocol.equals("wss")) || (AENetworkClassifier.categoriseAddress(host) != "Public"))
/*     */       {
/*     */ 
/*     */ 
/* 336 */         filter_it = false;
/*     */       }
/*     */       else
/*     */       {
/* 340 */         Proxy proxy = (Proxy)properties.get("Proxy");
/*     */         
/* 342 */         if ((proxy != null) && (proxy.type() == Proxy.Type.SOCKS))
/*     */         {
/* 344 */           InetSocketAddress address = (InetSocketAddress)proxy.address();
/*     */           
/* 346 */           if (address.getAddress().isLoopbackAddress())
/*     */           {
/* 348 */             filter_it = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 354 */     this.generator.generateHTTPProperties(hash, properties);
/*     */     
/* 356 */     if (filter_it)
/*     */     {
/* 358 */       URL url = (URL)properties.get("URL");
/*     */       
/* 360 */       boolean is_ssl = url.getProtocol().toLowerCase().equals("https");
/*     */       try
/*     */       {
/* 363 */         String url_str = url.toString();
/*     */         
/* 365 */         String target_host = url.getHost();
/* 366 */         int target_port = url.getPort();
/*     */         
/* 368 */         if (target_port == -1)
/*     */         {
/* 370 */           target_port = url.getDefaultPort();
/*     */         }
/*     */         
/*     */         String hash_str;
/*     */         String hash_str;
/* 375 */         if (hash == null) {
/* 376 */           hash_str = "";
/*     */         } else {
/* 378 */           hash_str = URLEncoder.encode(new String(hash, "ISO-8859-1"), "ISO-8859-1").replaceAll("\\+", "%20");
/*     */         }
/*     */         
/* 381 */         int host_pos = url_str.indexOf(target_host);
/*     */         
/* 383 */         String new_url = url_str.substring(0, host_pos) + "127.0.0.1:" + this.filter_port;
/*     */         
/* 385 */         if (is_ssl)
/*     */         {
/* 387 */           new_url = "http" + new_url.substring(new_url.indexOf(':'));
/*     */         }
/*     */         
/* 390 */         String rem = url_str.substring(host_pos + target_host.length());
/*     */         
/* 392 */         if (rem.charAt(0) == ':')
/*     */         {
/* 394 */           rem = rem.substring(("" + target_port).length() + 1);
/*     */         }
/*     */         
/* 397 */         int q_pos = rem.indexOf('?');
/*     */         
/* 399 */         String details = "cid=" + (is_ssl ? "." : "") + target_host + ":" + target_port + "+" + hash_str;
/*     */         
/* 401 */         if (q_pos == -1)
/*     */         {
/* 403 */           new_url = new_url + rem + "?" + details;
/*     */         }
/*     */         else {
/* 406 */           new_url = new_url + rem.substring(0, q_pos + 1) + details + "&" + rem.substring(q_pos + 1);
/*     */         }
/*     */         
/* 409 */         properties.put("URL", new URL(new_url));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 413 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class httpFilter
/*     */     extends ThreadPoolTask
/*     */   {
/*     */     private Socket socket;
/*     */     
/*     */ 
/*     */     protected httpFilter(Socket _socket)
/*     */     {
/* 428 */       this.socket = _socket;
/*     */     }
/*     */     
/*     */ 
/*     */     public void runSupport()
/*     */     {
/* 434 */       String report_error = null;
/* 435 */       int written = 0;
/*     */       
/* 437 */       boolean looks_like_tracker_request = false;
/*     */       
/*     */       try
/*     */       {
/* 441 */         setTaskState("reading header");
/*     */         
/* 443 */         InputStream is = this.socket.getInputStream();
/*     */         
/* 445 */         byte[] buffer = new byte['Ѐ'];
/*     */         
/* 447 */         String header = "";
/*     */         
/*     */         for (;;)
/*     */         {
/* 451 */           int len = is.read(buffer);
/*     */           
/* 453 */           if (len == -1) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 458 */           header = header + new String(buffer, 0, len, "ISO-8859-1");
/*     */           
/* 460 */           if ((header.endsWith("\r\n\r\n")) || (header.contains("\r\n\r\n"))) {
/*     */             break;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 467 */         List<String> lines = new ArrayList();
/*     */         
/* 469 */         int pos = 0;
/*     */         
/*     */         for (;;)
/*     */         {
/* 473 */           int p1 = header.indexOf("\r\n", pos);
/*     */           
/*     */           String line;
/*     */           
/* 477 */           if (p1 == -1)
/*     */           {
/* 479 */             line = header.substring(pos);
/*     */           }
/*     */           else
/*     */           {
/* 483 */             line = header.substring(pos, p1);
/*     */           }
/*     */           
/* 486 */           String line = line.trim();
/*     */           
/* 488 */           if (line.length() > 0)
/*     */           {
/* 490 */             lines.add(line);
/*     */           }
/*     */           
/* 493 */           if (p1 == -1) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 498 */           pos = p1 + 2;
/*     */         }
/*     */         
/*     */ 
/* 502 */         String[] lines_in = new String[lines.size()];
/*     */         
/* 504 */         lines.toArray(lines_in);
/*     */         
/* 506 */         String get = lines_in[0];
/*     */         
/* 508 */         int p1 = get.indexOf("?cid=");
/* 509 */         int p2 = get.indexOf("&", p1);
/*     */         
/* 511 */         if (p2 == -1)
/*     */         {
/* 513 */           p2 = get.indexOf(' ', p1);
/*     */         }
/*     */         
/* 516 */         String cid = get.substring(p1 + 5, p2);
/*     */         
/* 518 */         int p3 = cid.lastIndexOf(":");
/*     */         
/* 520 */         String target_host = cid.substring(0, p3);
/*     */         
/* 522 */         String[] port_hash = cid.substring(p3 + 1).split("\\+");
/*     */         
/* 524 */         int target_port = Integer.parseInt(port_hash[0]);
/*     */         
/* 526 */         String hash_str = port_hash.length == 1 ? "" : port_hash[1];
/*     */         
/* 528 */         byte[] hash = hash_str.length() == 0 ? null : URLDecoder.decode(port_hash[1], "ISO-8859-1").getBytes("ISO-8859-1");
/*     */         
/* 530 */         looks_like_tracker_request = hash != null;
/*     */         
/*     */         boolean is_ssl;
/*     */         
/* 534 */         if (target_host.startsWith("."))
/*     */         {
/* 536 */           boolean is_ssl = true;
/*     */           
/* 538 */           target_host = target_host.substring(1);
/*     */         }
/*     */         else
/*     */         {
/* 542 */           is_ssl = false;
/*     */         }
/*     */         
/*     */ 
/* 546 */         for (int i = 1; i < lines_in.length; i++)
/*     */         {
/* 548 */           String line = lines_in[i];
/*     */           
/* 550 */           if (line.toLowerCase().contains("host:"))
/*     */           {
/* 552 */             lines_in[i] = ("Host: " + target_host + ":" + target_port);
/*     */             
/* 554 */             break;
/*     */           }
/*     */         }
/*     */         
/* 558 */         get = get.substring(0, p1 + 1) + get.substring(p2 + 1);
/*     */         
/* 560 */         lines_in[0] = get;
/*     */         
/*     */         String[] lines_out;
/*     */         
/* 564 */         if (ClientIDManagerImpl.this.filter_override)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 569 */           String[] lines_out = lines_in;
/*     */           
/* 571 */           Properties p = new Properties();
/*     */           
/* 573 */           ClientIDManagerImpl.this.generator.generateHTTPProperties(hash, p);
/*     */           
/* 575 */           String agent = p.getProperty("User-Agent");
/*     */           
/* 577 */           if (agent != null)
/*     */           {
/* 579 */             for (int i = 0; i < lines_out.length; i++)
/*     */             {
/* 581 */               if (lines_out[i].toLowerCase().startsWith("user-agent"))
/*     */               {
/* 583 */                 lines_out[i] = ("User-Agent: " + agent);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 588 */           lines_out = ClientIDManagerImpl.this.generator.filterHTTP(hash, (String[])lines_out.clone());
/*     */         }
/*     */         else
/*     */         {
/* 592 */           lines_out = ClientIDManagerImpl.this.generator.filterHTTP(hash, lines_in);
/*     */         }
/*     */         
/* 595 */         String header_out = "";
/*     */         
/* 597 */         for (int i = 0; i < lines_out.length; i++)
/*     */         {
/* 599 */           header_out = header_out + lines_out[i] + "\r\n";
/*     */         }
/*     */         
/* 602 */         header_out = header_out + "\r\n";
/*     */         
/* 604 */         Socket target = UrlUtils.connectSocketAndWrite(is_ssl, target_host, target_port, header_out.getBytes("ISO-8859-1"), ClientIDManagerImpl.connect_timeout, ClientIDManagerImpl.read_timeout);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 614 */           target.getOutputStream().flush();
/*     */           
/* 616 */           InputStream target_is = target.getInputStream();
/*     */           
/*     */ 
/*     */ 
/* 620 */           String reply_header = "";
/*     */           
/* 622 */           byte[] temp = new byte[1];
/*     */           
/*     */           for (;;)
/*     */           {
/* 626 */             int len = target_is.read(temp);
/*     */             
/* 628 */             if (len != 1)
/*     */             {
/* 630 */               throw new ClientIDException("EOF while reading reply header");
/*     */             }
/*     */             
/* 633 */             reply_header = reply_header + new String(temp, "ISO-8859-1");
/*     */             
/* 635 */             if ((temp[0] == 10) && (reply_header.endsWith("\r\n\r\n"))) {
/*     */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 641 */           String[] reply_lines = reply_header.trim().split("\r\n");
/*     */           
/* 643 */           String line1 = reply_lines[0];
/*     */           
/* 645 */           line1 = line1.substring(line1.indexOf(' ') + 1).trim();
/*     */           
/* 647 */           if ((line1.startsWith("301")) || (line1.startsWith("302")))
/*     */           {
/* 649 */             for (int i = 1; i < reply_lines.length; i++)
/*     */             {
/* 651 */               String line = reply_lines[i];
/*     */               
/* 653 */               if (line.toLowerCase(Locale.US).startsWith("location:"))
/*     */               {
/* 655 */                 String redirect_url = line.substring(9).trim();
/*     */                 
/* 657 */                 String lc_redirect_url = redirect_url.toLowerCase(Locale.US);
/*     */                 
/* 659 */                 if ((!lc_redirect_url.startsWith("http:")) && (!lc_redirect_url.startsWith("https:")))
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 667 */                   String prefix = "http" + (is_ssl ? "s" : "") + "://" + target_host + ":" + target_port;
/*     */                   
/* 669 */                   if (redirect_url.startsWith("/"))
/*     */                   {
/* 671 */                     redirect_url = prefix + redirect_url;
/*     */                   }
/*     */                   else
/*     */                   {
/* 675 */                     String get_line = (String)lines.get(0);
/*     */                     
/* 677 */                     get_line = get_line.substring(get_line.indexOf(' ') + 1).trim();
/*     */                     
/* 679 */                     get_line = get_line.substring(0, get_line.indexOf(' ')).trim();
/*     */                     
/* 681 */                     int x_pos = get_line.indexOf('?');
/*     */                     
/* 683 */                     if (x_pos != -1)
/*     */                     {
/* 685 */                       get_line = get_line.substring(0, x_pos);
/*     */                     }
/*     */                     
/* 688 */                     x_pos = get_line.lastIndexOf('/');
/*     */                     
/* 690 */                     if (x_pos == -1)
/*     */                     {
/* 692 */                       redirect_url = prefix + "/" + redirect_url;
/*     */                     }
/*     */                     else
/*     */                     {
/* 696 */                       redirect_url = prefix + get_line.substring(0, x_pos + 1) + redirect_url;
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 
/* 701 */                 Properties http_properties = new Properties();
/*     */                 
/* 703 */                 http_properties.put("URL", new URL(redirect_url));
/*     */                 
/* 705 */                 ClientIDManagerImpl.this.generateHTTPProperties(hash, http_properties);
/*     */                 
/* 707 */                 URL updated = (URL)http_properties.get("URL");
/*     */                 
/* 709 */                 reply_lines[i] = ("Location: " + updated.toExternalForm());
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 714 */           OutputStream os = this.socket.getOutputStream();
/*     */           
/* 716 */           for (String str : reply_lines)
/*     */           {
/* 718 */             os.write((str + "\r\n").getBytes("ISO-8859-1"));
/*     */           }
/*     */           
/* 721 */           os.write("\r\n".getBytes("ISO-8859-1"));
/*     */           
/*     */           for (;;)
/*     */           {
/* 725 */             int len = target_is.read(buffer);
/*     */             
/* 727 */             if (len == -1) {
/*     */               break;
/*     */             }
/*     */             
/*     */ 
/* 732 */             os.write(buffer, 0, len);
/*     */             
/* 734 */             written += len;
/*     */           }
/*     */         }
/*     */         finally {
/* 738 */           target.close(); }
/*     */         Map failure;
/*     */         byte[] x;
/*     */         OutputStream os;
/*     */         String[] reply_lines; String[] arr$; int len$; int i$; String str; Map failure; byte[] x; OutputStream os; String[] reply_lines; String[] arr$; int len$; int i$; String str; Map failure; byte[] x; OutputStream os; String[] reply_lines; String[] arr$; int len$; int i$; String str; Map failure; byte[] x; OutputStream os; String[] reply_lines; String[] arr$; int len$; int i$; String str; Map failure; byte[] x; OutputStream os; String[] reply_lines; String[] arr$; int len$; int i$; String str; Map failure; byte[] x; OutputStream os; String[] reply_lines; String[] arr$; int len$; int i$; String str; Map failure; byte[] x; OutputStream os; String[] reply_lines; ???; ???; ???; String str; return; } catch (ClientIDException e) { report_error = e.getMessage();
/*     */       }
/*     */       catch (UnknownHostException e)
/*     */       {
/* 746 */         report_error = "Unknown host '" + e.getMessage() + "'";
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 750 */         report_error = e.getMessage();
/*     */ 
/*     */       }
/*     */       catch (UnsupportedAddressTypeException e)
/*     */       {
/*     */ 
/* 756 */         report_error = e.getMessage();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 760 */         Debug.printStackTrace(e);
/*     */       }
/*     */       finally
/*     */       {
/* 764 */         if ((report_error != null) && (written == 0) && (looks_like_tracker_request))
/*     */         {
/* 766 */           failure = new HashMap();
/*     */           
/* 768 */           failure.put("failure reason", report_error);
/*     */           try
/*     */           {
/* 771 */             x = BEncoder.encode(failure);
/*     */             
/* 773 */             os = this.socket.getOutputStream();
/*     */             
/* 775 */             reply_lines = new String[] { "HTTP/1.1 200 OK", "Content-Length: " + x.length, "Connection: close" };
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 782 */             for (str : reply_lines)
/*     */             {
/* 784 */               os.write((str + "\r\n").getBytes("ISO-8859-1"));
/*     */             }
/*     */             
/* 787 */             os.write("\r\n".getBytes("ISO-8859-1"));
/*     */             
/* 789 */             os.write(x);
/*     */           }
/*     */           catch (Throwable f) {}
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 798 */           this.socket.getOutputStream().flush();
/*     */           
/* 800 */           this.socket.close();
/*     */         }
/*     */         catch (Throwable f) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void interruptTask()
/*     */     {
/*     */       try
/*     */       {
/* 817 */         this.socket.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/clientid/ClientIDManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */