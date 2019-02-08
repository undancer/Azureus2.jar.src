/*      */ package com.aelitis.azureus.core.util.http;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.Proxy;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import java.util.zip.InflaterInputStream;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import javax.net.ssl.TrustManager;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.ThreadPool;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HTTPAuthHelper
/*      */ {
/*      */   public static final boolean TRACE = false;
/*      */   public static final int MAX_PROCESSORS = 32;
/*      */   public static final int CONNECT_TIMEOUT = 30000;
/*      */   public static final int READ_TIMEOUT = 30000;
/*      */   private final HTTPAuthHelper parent;
/*   51 */   private final Map children = new HashMap();
/*      */   
/*      */   private final URL delegate_to;
/*      */   
/*      */   final String delegate_to_host;
/*      */   
/*      */   final int delegate_to_port;
/*      */   final boolean delegate_is_https;
/*      */   private Proxy delegate_to_proxy;
/*   60 */   private final CopyOnWriteList listeners = new CopyOnWriteList();
/*      */   
/*      */   private int port;
/*      */   
/*      */   private ServerSocket server_socket;
/*      */   
/*      */   private boolean http_only_detected;
/*      */   
/*   68 */   private final Map cookie_names_set = new HashMap();
/*      */   
/*   70 */   final ThreadPool thread_pool = new ThreadPool("HTTPSniffer", 32, true);
/*      */   
/*   72 */   final List processors = new ArrayList();
/*      */   
/*      */ 
/*      */   private volatile boolean destroyed;
/*      */   
/*      */ 
/*      */ 
/*      */   public HTTPAuthHelper(URL url)
/*      */     throws Exception
/*      */   {
/*   82 */     this(null, url);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private HTTPAuthHelper(HTTPAuthHelper _parent, URL _delegate_to)
/*      */     throws Exception
/*      */   {
/*   92 */     this.parent = _parent;
/*   93 */     this.delegate_to = _delegate_to;
/*      */     
/*   95 */     this.delegate_to_host = this.delegate_to.getHost();
/*   96 */     this.delegate_is_https = this.delegate_to.getProtocol().toLowerCase().equals("https");
/*   97 */     this.delegate_to_port = (this.delegate_to.getPort() == -1 ? this.delegate_to.getDefaultPort() : this.delegate_to.getPort());
/*      */     
/*   99 */     this.server_socket = new ServerSocket();
/*      */     
/*  101 */     this.server_socket.setReuseAddress(true);
/*      */     
/*  103 */     this.server_socket.bind(new InetSocketAddress("127.0.0.1", 0));
/*      */     
/*  105 */     this.port = this.server_socket.getLocalPort();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setProxy(Proxy _proxy)
/*      */   {
/*  112 */     this.delegate_to_proxy = _proxy;
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */   {
/*  118 */     new AEThread2("HTTPSniffingProxy: " + this.delegate_to_host + ":" + this.delegate_to_port + "/" + this.delegate_is_https + "/" + this.port, true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  126 */           while (!HTTPAuthHelper.this.destroyed)
/*      */           {
/*  128 */             Socket socket = HTTPAuthHelper.this.server_socket.accept();
/*      */             
/*  130 */             socket.setSoTimeout(30000);
/*      */             
/*  132 */             synchronized (HTTPAuthHelper.this)
/*      */             {
/*  134 */               if (HTTPAuthHelper.this.processors.size() >= 32)
/*      */               {
/*      */                 try {
/*  137 */                   Debug.out("Too many processors");
/*      */                   
/*  139 */                   socket.close();
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               else
/*      */               {
/*  145 */                 HTTPAuthHelper.processor proc = new HTTPAuthHelper.processor(HTTPAuthHelper.this, socket, null);
/*      */                 
/*  147 */                 HTTPAuthHelper.this.processors.add(proc);
/*      */                 
/*  149 */                 HTTPAuthHelper.processor.access$300(proc);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  155 */           if (!HTTPAuthHelper.this.destroyed)
/*      */           {
/*  157 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  167 */     return this.port;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean wasHTTPOnlyCookieDetected()
/*      */   {
/*  173 */     return this.http_only_detected;
/*      */   }
/*      */   
/*      */ 
/*      */   private void setHTTPOnlyCookieDetected()
/*      */   {
/*  179 */     this.http_only_detected = true;
/*      */     
/*  181 */     if (this.parent != null)
/*      */     {
/*  183 */       this.parent.setHTTPOnlyCookieDetected();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String getKey(URL url)
/*      */   {
/*  191 */     int child_port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
/*      */     
/*  193 */     String key = url.getProtocol() + ":" + url.getHost() + ":" + child_port;
/*      */     
/*  195 */     return key;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private HTTPAuthHelper getChild(String url_str, boolean optional)
/*      */     throws Exception
/*      */   {
/*  205 */     if (this.parent != null)
/*      */     {
/*  207 */       return this.parent.getChild(url_str, optional);
/*      */     }
/*      */     
/*  210 */     String lc_url_str = url_str.toLowerCase();
/*      */     
/*  212 */     if ((lc_url_str.startsWith("http://")) || (lc_url_str.startsWith("https://")))
/*      */     {
/*  214 */       URL child_url = new URL(url_str);
/*      */       
/*  216 */       String child_key = getKey(child_url);
/*      */       
/*  218 */       if (child_key.equals(getKey(this.delegate_to)))
/*      */       {
/*  220 */         return this;
/*      */       }
/*      */       
/*  223 */       synchronized (this)
/*      */       {
/*  225 */         if (this.destroyed)
/*      */         {
/*  227 */           throw new Exception("Destroyed");
/*      */         }
/*      */         
/*  230 */         HTTPAuthHelper child = (HTTPAuthHelper)this.children.get(child_key);
/*      */         
/*  232 */         if (optional)
/*      */         {
/*      */ 
/*      */ 
/*  236 */           String base_host = this.delegate_to.getHost();
/*  237 */           String child_host = child_url.getHost();
/*      */           
/*  239 */           int base_pos = base_host.lastIndexOf('.');
/*  240 */           base_pos = base_host.lastIndexOf('.', base_pos - 1);
/*      */           
/*  242 */           int child_pos = child_host.lastIndexOf('.');
/*  243 */           child_pos = child_host.lastIndexOf('.', child_pos - 1);
/*      */           
/*  245 */           String base_dom = base_host.substring(base_pos, base_host.length());
/*  246 */           String child_dom = child_host.substring(child_pos, child_host.length());
/*      */           
/*  248 */           if (base_dom.equals(child_dom))
/*      */           {
/*  250 */             optional = false;
/*      */           }
/*      */         }
/*      */         
/*  254 */         if ((child == null) && (!optional))
/*      */         {
/*  256 */           child = new HTTPAuthHelper(this, new URL(url_str));
/*      */           
/*  258 */           this.children.put(child_key, child);
/*      */           
/*  260 */           child.start();
/*      */         }
/*      */         
/*  263 */         return child;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  268 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addSetCookieName(String name, String value)
/*      */   {
/*  277 */     if (this.parent != null)
/*      */     {
/*  279 */       this.parent.addSetCookieName(name, value);
/*      */     }
/*      */     else
/*      */     {
/*      */       boolean new_entry;
/*      */       
/*  285 */       synchronized (this.cookie_names_set)
/*      */       {
/*  287 */         trace("SetCookieName: " + name);
/*      */         
/*  289 */         String old_value = (String)this.cookie_names_set.put(name, value);
/*      */         
/*  291 */         new_entry = (old_value == null) || (!old_value.equals(value));
/*      */       }
/*      */       
/*  294 */       if (new_entry)
/*      */       {
/*  296 */         Iterator it = this.listeners.iterator();
/*      */         
/*  298 */         while (it.hasNext()) {
/*      */           try
/*      */           {
/*  301 */             ((HTTPAuthHelperListener)it.next()).cookieFound(this, name, value);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  305 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean hasSetCookieName(String name)
/*      */   {
/*  316 */     if (this.parent != null)
/*      */     {
/*  318 */       return this.parent.hasSetCookieName(name);
/*      */     }
/*      */     
/*      */ 
/*  322 */     synchronized (this.cookie_names_set)
/*      */     {
/*  324 */       trace("GetCookieName: " + name);
/*      */       
/*  326 */       return this.cookie_names_set.containsKey(name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(HTTPAuthHelperListener listener)
/*      */   {
/*  335 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/*      */     List chidren_to_destroy;
/*      */     
/*      */     List processors_to_destroy;
/*  344 */     synchronized (this)
/*      */     {
/*  346 */       if (this.destroyed)
/*      */       {
/*  348 */         return;
/*      */       }
/*      */       
/*  351 */       this.destroyed = true;
/*      */       
/*  353 */       chidren_to_destroy = new ArrayList(this.children.values());
/*      */       
/*  355 */       this.children.clear();
/*      */       
/*  357 */       processors_to_destroy = new ArrayList(this.processors);
/*      */       
/*  359 */       this.processors.clear();
/*      */       try
/*      */       {
/*  362 */         this.server_socket.close();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*  368 */     for (int i = 0; i < chidren_to_destroy.size(); i++) {
/*      */       try
/*      */       {
/*  371 */         ((HTTPAuthHelper)chidren_to_destroy.get(i)).destroy();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*  377 */     for (int i = 0; i < processors_to_destroy.size(); i++) {
/*      */       try
/*      */       {
/*  380 */         ((processor)processors_to_destroy.get(i)).destroy();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void trace(String str) {}
/*      */   
/*      */ 
/*      */   private class processor
/*      */   {
/*      */     private static final String NL = "\r\n";
/*      */     
/*      */     final Socket socket_in;
/*      */     
/*      */     private Socket socket_out;
/*      */     private volatile boolean destroyed;
/*      */     
/*      */     private processor(Socket _socket)
/*      */     {
/*  401 */       this.socket_in = _socket;
/*      */     }
/*      */     
/*      */ 
/*      */     private void start()
/*      */     {
/*  407 */       HTTPAuthHelper.this.thread_pool.run(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/*  414 */             HTTPAuthHelper.processor.this.sniff();
/*      */           }
/*      */           finally
/*      */           {
/*  418 */             synchronized (HTTPAuthHelper.this)
/*      */             {
/*  420 */               HTTPAuthHelper.this.processors.remove(HTTPAuthHelper.processor.this);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */     private void sniff()
/*      */     {
/*      */       try
/*      */       {
/*  431 */         InputStream is = this.socket_in.getInputStream();
/*      */         
/*  433 */         String request_header = readHeader(is);
/*      */         
/*  435 */         connectToDelegate();
/*      */         
/*  437 */         process(request_header);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  441 */         if (!(e instanceof IOException))
/*      */         {
/*  443 */           Debug.out(e);
/*      */         }
/*      */         
/*  446 */         destroy();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void connectToDelegate()
/*      */       throws IOException
/*      */     {
/*      */       try
/*      */       {
/*  456 */         if (HTTPAuthHelper.this.delegate_is_https)
/*      */         {
/*  458 */           TrustManager[] trustAllCerts = SESecurityManager.getAllTrustingTrustManager();
/*      */           
/*  460 */           SSLContext sc = SSLContext.getInstance("SSL");
/*      */           
/*  462 */           sc.init(null, trustAllCerts, RandomUtils.SECURE_RANDOM);
/*      */           
/*  464 */           SSLSocketFactory factory = sc.getSocketFactory();
/*      */           try
/*      */           {
/*  467 */             if (HTTPAuthHelper.this.delegate_to_proxy == null)
/*      */             {
/*  469 */               this.socket_out = factory.createSocket();
/*      */               
/*  471 */               this.socket_out.connect(new InetSocketAddress(HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port), 30000);
/*      */             }
/*      */             else
/*      */             {
/*  475 */               Socket plain_socket = new Socket(HTTPAuthHelper.this.delegate_to_proxy);
/*      */               
/*  477 */               plain_socket.connect(new InetSocketAddress(HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port), 30000);
/*      */               
/*  479 */               this.socket_out = factory.createSocket(plain_socket, HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port, true);
/*      */             }
/*      */           }
/*      */           catch (SSLException ssl_excep)
/*      */           {
/*  484 */             if (this.socket_out != null) {
/*      */               try
/*      */               {
/*  487 */                 this.socket_out.close();
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */             
/*      */ 
/*  493 */             factory = SESecurityManager.installServerCertificates("AZ-sniffer:" + HTTPAuthHelper.this.delegate_to_host + ":" + HTTPAuthHelper.this.port, HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port);
/*      */             
/*  495 */             if (HTTPAuthHelper.this.delegate_to_proxy == null)
/*      */             {
/*  497 */               this.socket_out = factory.createSocket();
/*      */               
/*  499 */               this.socket_out.connect(new InetSocketAddress(HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port), 30000);
/*      */             }
/*      */             else
/*      */             {
/*  503 */               Socket plain_socket = new Socket(HTTPAuthHelper.this.delegate_to_proxy);
/*      */               
/*  505 */               plain_socket.connect(new InetSocketAddress(HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port), 30000);
/*      */               
/*  507 */               this.socket_out = factory.createSocket(plain_socket, HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port, true);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  512 */           if (HTTPAuthHelper.this.delegate_to_proxy == null)
/*      */           {
/*  514 */             this.socket_out = new Socket();
/*      */           }
/*      */           else
/*      */           {
/*  518 */             this.socket_out = new Socket(HTTPAuthHelper.this.delegate_to_proxy);
/*      */           }
/*      */           
/*  521 */           this.socket_out.connect(new InetSocketAddress(HTTPAuthHelper.this.delegate_to_host, HTTPAuthHelper.this.delegate_to_port), 30000);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  525 */         if ((e instanceof IOException))
/*      */         {
/*  527 */           throw ((IOException)e);
/*      */         }
/*      */         
/*  530 */         throw new IOException(e.toString());
/*      */       }
/*      */       finally
/*      */       {
/*  534 */         if (this.socket_out != null)
/*      */         {
/*  536 */           synchronized (this)
/*      */           {
/*  538 */             if (this.destroyed)
/*      */             {
/*      */               try {
/*  541 */                 this.socket_out.close();
/*      */ 
/*      */               }
/*      */               catch (Throwable e) {}finally
/*      */               {
/*      */ 
/*  547 */                 this.socket_out = null;
/*      */               }
/*      */               
/*  550 */               throw new IOException("destroyed");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void process(String request_header)
/*      */       throws Exception
/*      */     {
/*  563 */       final OutputStream target_os = this.socket_out.getOutputStream();
/*      */       
/*  565 */       String[] request_lines = splitHeader(request_header);
/*      */       
/*  567 */       String target_url = request_lines[0];
/*      */       
/*  569 */       int space_pos = target_url.indexOf(' ');
/*      */       
/*  571 */       if (space_pos == -1)
/*      */       {
/*  573 */         System.out.println("eh?");
/*      */       }
/*  575 */       target_url = target_url.substring(space_pos).trim();
/*      */       
/*  577 */       space_pos = target_url.indexOf(' ');
/*      */       
/*  579 */       target_url = target_url.substring(0, space_pos).trim();
/*      */       
/*  581 */       HTTPAuthHelper.this.trace("Page request for " + target_url);
/*      */       
/*  583 */       List cookies_to_remove = new ArrayList();
/*      */       
/*  585 */       for (int i = 0; i < request_lines.length; i++)
/*      */       {
/*  587 */         String line_out = request_lines[i];
/*      */         
/*  589 */         String line_in = line_out.trim().toLowerCase();
/*      */         
/*  591 */         String[] bits = line_in.split(":");
/*      */         
/*  593 */         if (bits.length >= 2)
/*      */         {
/*  595 */           String lhs = bits[0].trim();
/*      */           
/*  597 */           if (lhs.equals("host"))
/*      */           {
/*      */             String port_str;
/*      */             String port_str;
/*  601 */             if ((HTTPAuthHelper.this.delegate_to_port == 80) || (HTTPAuthHelper.this.delegate_to_port == 443))
/*      */             {
/*  603 */               port_str = "";
/*      */             }
/*      */             else
/*      */             {
/*  607 */               port_str = ":" + HTTPAuthHelper.this.delegate_to_port;
/*      */             }
/*      */             
/*  610 */             line_out = "Host: " + HTTPAuthHelper.this.delegate_to_host + port_str;
/*      */           }
/*  612 */           else if (lhs.equals("connection"))
/*      */           {
/*  614 */             line_out = "Connection: close";
/*      */           }
/*  616 */           else if (lhs.equals("referer"))
/*      */           {
/*  618 */             String page = line_out.substring(line_out.indexOf(':') + 1).trim();
/*      */             
/*  620 */             page = page.substring(page.indexOf("://") + 3);
/*      */             
/*  622 */             int pos = page.indexOf('/');
/*      */             
/*  624 */             if (pos >= 0)
/*      */             {
/*  626 */               page = page.substring(pos);
/*      */             }
/*      */             else
/*      */             {
/*  630 */               page = "/";
/*      */             }
/*      */             
/*      */             String port_str;
/*      */             String port_str;
/*  635 */             if ((HTTPAuthHelper.this.delegate_to_port == 80) || (HTTPAuthHelper.this.delegate_to_port == 443))
/*      */             {
/*  637 */               port_str = "";
/*      */             }
/*      */             else
/*      */             {
/*  641 */               port_str = ":" + HTTPAuthHelper.this.delegate_to_port;
/*      */             }
/*      */             
/*  644 */             line_out = "Referer: http" + (HTTPAuthHelper.this.delegate_is_https ? "s" : "") + "://" + HTTPAuthHelper.this.delegate_to_host + port_str + page;
/*      */           }
/*  646 */           else if (lhs.equals("cookie"))
/*      */           {
/*  648 */             String cookies_str = line_out.substring(line_out.indexOf(':') + 1).trim();
/*      */             
/*  650 */             String[] cookies = cookies_str.split(";");
/*      */             
/*  652 */             String cookies_out = "";
/*      */             
/*  654 */             for (int j = 0; j < cookies.length; j++)
/*      */             {
/*  656 */               String cookie = cookies[j];
/*      */               
/*  658 */               String name = cookie.split("=")[0].trim();
/*      */               
/*  660 */               if (HTTPAuthHelper.this.hasSetCookieName(name))
/*      */               {
/*  662 */                 cookies_out = cookies_out + (cookies_out.length() == 0 ? "" : "; ") + cookie;
/*      */               }
/*      */               else
/*      */               {
/*  666 */                 cookies_to_remove.add(name);
/*      */               }
/*      */             }
/*      */             
/*  670 */             if (cookies_out.length() > 0)
/*      */             {
/*  672 */               line_out = "Cookie: " + cookies_out;
/*      */             }
/*      */             else
/*      */             {
/*  676 */               line_out = null;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  681 */         if (line_out != null)
/*      */         {
/*  683 */           HTTPAuthHelper.this.trace("-> " + line_out);
/*      */           
/*  685 */           target_os.write((line_out + "\r\n").getBytes());
/*      */         }
/*      */       }
/*      */       
/*  689 */       target_os.write("\r\n".getBytes());
/*      */       
/*  691 */       target_os.flush();
/*      */       
/*  693 */       new AEThread2("HTTPSniffingProxy:proc:2", true)
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try
/*      */           {
/*  699 */             InputStream source_is = HTTPAuthHelper.processor.this.socket_in.getInputStream();
/*      */             
/*  701 */             byte[] buffer = new byte['紀'];
/*      */             
/*  703 */             while (!HTTPAuthHelper.processor.this.destroyed)
/*      */             {
/*  705 */               int len = source_is.read(buffer);
/*      */               
/*  707 */               if (len <= 0) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*  712 */               target_os.write(buffer, 0, len);
/*      */               
/*  714 */               HTTPAuthHelper.this.trace("POST:" + new String(buffer, 0, len));
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*  721 */       }.start();
/*  722 */       InputStream target_is = this.socket_out.getInputStream();
/*      */       
/*  724 */       OutputStream source_os = this.socket_in.getOutputStream();
/*      */       
/*  726 */       String reply_header = readHeader(target_is);
/*      */       
/*  728 */       String[] reply_lines = splitHeader(reply_header);
/*      */       
/*  730 */       String content_type = null;
/*  731 */       String content_charset = "ISO-8859-1";
/*      */       
/*  733 */       for (int i = 0; i < reply_lines.length; i++)
/*      */       {
/*  735 */         String line_in = reply_lines[i].trim().toLowerCase();
/*      */         
/*  737 */         String[] bits = line_in.split(":");
/*      */         
/*  739 */         if (bits.length >= 2)
/*      */         {
/*  741 */           String lhs = bits[0].trim();
/*      */           
/*  743 */           if (lhs.equals("content-type"))
/*      */           {
/*  745 */             String rhs = reply_lines[i].substring(line_in.indexOf(':') + 1).trim();
/*      */             
/*  747 */             String[] x = rhs.split(";");
/*      */             
/*  749 */             content_type = x[0];
/*      */             
/*  751 */             if (x.length > 1)
/*      */             {
/*  753 */               int pos = rhs.toLowerCase().indexOf("charset");
/*      */               
/*  755 */               if (pos >= 0)
/*      */               {
/*  757 */                 String cc = rhs.substring(pos + 1);
/*      */                 
/*  759 */                 pos = cc.indexOf('=');
/*      */                 
/*  761 */                 if (pos != -1)
/*      */                 {
/*  763 */                   cc = cc.substring(pos + 1).trim();
/*      */                   
/*  765 */                   if (Charset.isSupported(cc))
/*      */                   {
/*  767 */                     content_charset = cc;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  776 */       boolean rewrite = false;
/*  777 */       boolean chunked = false;
/*  778 */       String content_encoding = null;
/*      */       
/*  780 */       if (content_type == null)
/*      */       {
/*  782 */         rewrite = true;
/*      */       }
/*      */       else
/*      */       {
/*  786 */         content_type = content_type.toLowerCase();
/*      */         
/*  788 */         if (content_type.contains("text/"))
/*      */         {
/*  790 */           rewrite = true;
/*      */         }
/*      */       }
/*      */       
/*  794 */       for (int i = 0; i < reply_lines.length; i++)
/*      */       {
/*  796 */         String line_out = reply_lines[i];
/*      */         
/*  798 */         String line_in = line_out.trim().toLowerCase();
/*      */         
/*  800 */         String[] bits = line_in.split(":");
/*      */         
/*  802 */         if (bits.length >= 2)
/*      */         {
/*  804 */           String lhs = bits[0].trim();
/*      */           
/*  806 */           if (lhs.equals("set-cookie"))
/*      */           {
/*  808 */             String cookies_in = line_out.substring(line_out.indexOf(':') + 1);
/*      */             
/*      */             String[] cookies;
/*      */             String[] cookies;
/*  812 */             if (!cookies_in.toLowerCase().contains("expires"))
/*      */             {
/*  814 */               cookies = cookies_in.split(",");
/*      */             }
/*      */             else
/*      */             {
/*  818 */               cookies = new String[] { cookies_in };
/*      */             }
/*      */             
/*  821 */             String cookies_out = "";
/*      */             
/*  823 */             for (int c = 0; c < cookies.length; c++)
/*      */             {
/*  825 */               String cookie = cookies[c];
/*      */               
/*  827 */               String[] x = cookie.split(";");
/*      */               
/*  829 */               String modified_cookie = "";
/*      */               
/*  831 */               for (int j = 0; j < x.length; j++)
/*      */               {
/*  833 */                 String entry = x[j].trim();
/*      */                 
/*  835 */                 if (entry.equalsIgnoreCase("httponly"))
/*      */                 {
/*  837 */                   HTTPAuthHelper.this.setHTTPOnlyCookieDetected();
/*      */                 }
/*  839 */                 else if (!entry.equalsIgnoreCase("secure"))
/*      */                 {
/*  841 */                   if (!entry.toLowerCase().startsWith("domain"))
/*      */                   {
/*      */ 
/*      */ 
/*  845 */                     if (!entry.toLowerCase().startsWith("expires"))
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  851 */                       if (j == 0)
/*      */                       {
/*  853 */                         int pos = entry.indexOf('=');
/*      */                         
/*  855 */                         String name = entry.substring(0, pos).trim();
/*  856 */                         String value = entry.substring(pos + 1).trim();
/*      */                         
/*  858 */                         HTTPAuthHelper.this.addSetCookieName(name, value);
/*      */                       }
/*      */                       
/*  861 */                       modified_cookie = modified_cookie + (modified_cookie.length() == 0 ? "" : "; ") + entry;
/*      */                     } }
/*      */                 }
/*      */               }
/*  865 */               cookies_out = cookies_out + (c == 0 ? "" : ", ") + modified_cookie;
/*      */             }
/*      */             
/*  868 */             line_out = "Set-Cookie: " + cookies_out;
/*      */           }
/*  870 */           else if (lhs.equals("set-cookie2"))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  876 */             String cookies_in = line_out.substring(line_out.indexOf(':') + 1);
/*      */             
/*  878 */             String[] cookies = cookies_in.split(",");
/*      */             
/*  880 */             String cookies_out = "";
/*      */             
/*  882 */             for (int c = 0; c < cookies.length; c++)
/*      */             {
/*  884 */               String cookie = cookies[c];
/*      */               
/*  886 */               String[] x = cookie.split(";");
/*      */               
/*  888 */               String modified_cookie = "";
/*      */               
/*  890 */               for (int j = 0; j < x.length; j++)
/*      */               {
/*  892 */                 String entry = x[j].trim();
/*      */                 
/*  894 */                 if (!entry.equalsIgnoreCase("secure"))
/*      */                 {
/*  896 */                   if (!entry.equalsIgnoreCase("discard"))
/*      */                   {
/*  898 */                     if (!entry.toLowerCase().startsWith("domain"))
/*      */                     {
/*  900 */                       if (!entry.toLowerCase().startsWith("port"))
/*      */                       {
/*      */ 
/*      */ 
/*  904 */                         if (j == 0)
/*      */                         {
/*  906 */                           int pos = entry.indexOf('=');
/*      */                           
/*  908 */                           String name = entry.substring(0, pos).trim();
/*      */                           
/*  910 */                           String value = entry.substring(pos + 1).trim();
/*      */                           
/*  912 */                           HTTPAuthHelper.this.addSetCookieName(name, value);
/*      */                         }
/*      */                         
/*  915 */                         modified_cookie = modified_cookie + (modified_cookie.length() == 0 ? "" : "; ") + entry;
/*      */                       } } }
/*      */                 }
/*      */               }
/*  919 */               cookies_out = cookies_out + (c == 0 ? "" : ", ") + modified_cookie + "; Discard";
/*      */             }
/*      */             
/*  922 */             line_out = "Set-Cookie2: " + cookies_out;
/*      */           }
/*  924 */           else if (lhs.equals("connection"))
/*      */           {
/*  926 */             line_out = "Connection: close";
/*      */           }
/*  928 */           else if (lhs.equals("location"))
/*      */           {
/*  930 */             String page = line_out.substring(line_out.indexOf(':') + 1).trim();
/*      */             
/*  932 */             String child_url = page.trim();
/*      */             
/*  934 */             HTTPAuthHelper child = HTTPAuthHelper.this.getChild(child_url, false);
/*      */             
/*  936 */             int pos = page.indexOf("://");
/*      */             
/*  938 */             if (pos >= 0)
/*      */             {
/*      */ 
/*      */ 
/*  942 */               page = page.substring(pos + 3);
/*      */               
/*  944 */               pos = page.indexOf('/');
/*      */               
/*  946 */               if (pos >= 0)
/*      */               {
/*  948 */                 page = page.substring(pos);
/*      */               }
/*      */               else
/*      */               {
/*  952 */                 page = "/";
/*      */ 
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*  958 */             else if (!page.startsWith("/"))
/*      */             {
/*  960 */               String temp = target_url;
/*      */               
/*  962 */               int marker = temp.indexOf("://");
/*      */               
/*  964 */               if (marker != -1)
/*      */               {
/*      */ 
/*      */ 
/*  968 */                 temp = temp.substring(marker + 3);
/*      */                 
/*  970 */                 marker = temp.indexOf("/");
/*      */                 
/*  972 */                 if (marker == -1)
/*      */                 {
/*  974 */                   temp = "/";
/*      */                 }
/*      */                 else
/*      */                 {
/*  978 */                   temp = temp.substring(marker);
/*      */                 }
/*      */                 
/*      */               }
/*  982 */               else if (!temp.startsWith("/"))
/*      */               {
/*  984 */                 temp = "/" + temp;
/*      */               }
/*      */               
/*      */ 
/*  988 */               marker = temp.lastIndexOf("/");
/*      */               
/*  990 */               if (marker >= 0)
/*      */               {
/*  992 */                 temp = temp.substring(0, marker + 1);
/*      */               }
/*      */               
/*  995 */               page = temp + page;
/*      */             }
/*      */             
/*      */ 
/*  999 */             line_out = "Location: http://127.0.0.1:" + child.getPort() + page;
/*      */           }
/* 1001 */           else if (lhs.equals("content-encoding"))
/*      */           {
/* 1003 */             if (rewrite)
/*      */             {
/* 1005 */               String encoding = bits[1].trim();
/*      */               
/* 1007 */               if ((encoding.equalsIgnoreCase("gzip")) || (encoding.equalsIgnoreCase("deflate")))
/*      */               {
/*      */ 
/* 1010 */                 content_encoding = encoding;
/*      */                 
/* 1012 */                 line_out = null;
/*      */               }
/*      */             }
/* 1015 */           } else if (lhs.equals("content-length"))
/*      */           {
/* 1017 */             if (rewrite)
/*      */             {
/* 1019 */               line_out = null;
/*      */             }
/* 1021 */           } else if (lhs.equals("transfer-encoding"))
/*      */           {
/* 1023 */             if (bits[1].contains("chunked"))
/*      */             {
/* 1025 */               chunked = true;
/*      */               
/* 1027 */               if (rewrite)
/*      */               {
/* 1029 */                 line_out = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1035 */         if (line_out != null)
/*      */         {
/* 1037 */           HTTPAuthHelper.this.trace("<- " + line_out);
/*      */           
/* 1039 */           source_os.write((line_out + "\r\n").getBytes());
/*      */         }
/*      */       }
/*      */       
/* 1043 */       for (int i = 0; i < cookies_to_remove.size(); i++)
/*      */       {
/* 1045 */         String name = (String)cookies_to_remove.get(i);
/*      */         
/* 1047 */         if (!HTTPAuthHelper.this.hasSetCookieName(name))
/*      */         {
/* 1049 */           String remove_str = "Set-Cookie: " + name + "=X; expires=Sun, 01 Jan 2000 01:00:00 GMT";
/*      */           
/* 1051 */           HTTPAuthHelper.this.trace("<- (cookie removal) " + remove_str);
/*      */           
/* 1053 */           source_os.write((remove_str + "\r\n").getBytes());
/*      */           
/* 1055 */           remove_str = "Set-Cookie2: " + name + "=X; Max-Age=0; Version=1";
/*      */           
/* 1057 */           HTTPAuthHelper.this.trace("<- (cookie removal) " + remove_str);
/*      */           
/* 1059 */           source_os.write((remove_str + "\r\n").getBytes());
/*      */         }
/*      */       }
/*      */       
/* 1063 */       byte[] buffer = new byte['紀'];
/*      */       
/*      */ 
/* 1066 */       if (rewrite)
/*      */       {
/* 1068 */         StringBuffer sb = new StringBuffer();
/*      */         
/* 1070 */         if (chunked)
/*      */         {
/*      */ 
/*      */           for (;;)
/*      */           {
/*      */ 
/* 1076 */             int len = target_is.read(buffer);
/*      */             
/* 1078 */             if (len <= 0) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/* 1083 */             sb.append(new String(buffer, 0, len, "ISO-8859-1"));
/*      */           }
/*      */           
/* 1086 */           StringBuilder sb_dechunked = new StringBuilder(sb.length());
/*      */           
/* 1088 */           String chunk = "";
/*      */           
/* 1090 */           int total_length = 0;
/*      */           
/* 1092 */           int sb_pos = 0;
/*      */           
/* 1094 */           while (sb_pos < sb.length())
/*      */           {
/* 1096 */             chunk = chunk + sb.charAt(sb_pos++);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1102 */             if ((chunk.endsWith("\r\n")) && (chunk.length() > 2))
/*      */             {
/* 1104 */               int semi_pos = chunk.indexOf(';');
/*      */               
/* 1106 */               if (semi_pos != -1)
/*      */               {
/* 1108 */                 chunk = chunk.substring(0, semi_pos);
/*      */               }
/*      */               
/* 1111 */               chunk = chunk.trim();
/*      */               
/* 1113 */               int chunk_length = Integer.parseInt(chunk, 16);
/*      */               
/* 1115 */               if (chunk_length <= 0) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1120 */               total_length += chunk_length;
/*      */               
/* 1122 */               if (total_length > 2097152)
/*      */               {
/* 1124 */                 throw new IOException("Chunk size " + chunk_length + " too large");
/*      */               }
/*      */               
/*      */ 
/* 1128 */               char[] chunk_buffer = new char[chunk_length];
/*      */               
/* 1130 */               sb.getChars(sb_pos, sb_pos + chunk_length, chunk_buffer, 0);
/*      */               
/* 1132 */               sb_dechunked.append(chunk_buffer);
/*      */               
/* 1134 */               sb_pos += chunk_length;
/*      */               
/* 1136 */               chunk = "";
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1142 */           target_is = new ByteArrayInputStream(sb_dechunked.toString().getBytes("ISO-8859-1"));
/*      */         }
/*      */         
/* 1145 */         if (content_encoding != null)
/*      */         {
/* 1147 */           if (content_encoding.equalsIgnoreCase("gzip"))
/*      */           {
/* 1149 */             target_is = new GZIPInputStream(target_is);
/*      */           }
/* 1151 */           else if (content_encoding.equalsIgnoreCase("deflate"))
/*      */           {
/* 1153 */             target_is = new InflaterInputStream(target_is);
/*      */           }
/*      */         }
/*      */         
/* 1157 */         sb.setLength(0);
/*      */         
/* 1159 */         while (!this.destroyed)
/*      */         {
/* 1161 */           int len = target_is.read(buffer);
/*      */           
/* 1163 */           if (len <= 0) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1168 */           sb.append(new String(buffer, 0, len, content_charset));
/*      */         }
/*      */         
/* 1171 */         String str = sb.toString();
/* 1172 */         String lc_str = str.toLowerCase();
/*      */         
/* 1174 */         StringBuffer result = null;
/* 1175 */         int str_pos = 0;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         for (;;)
/*      */         {
/* 1183 */           int url_start = str.length() - str_pos >= 10 ? lc_str.indexOf("http", str_pos) : -1;
/*      */           
/* 1185 */           if (url_start == -1) {
/*      */             break;
/*      */           }
/*      */           
/*      */           int match_pos;
/*      */           
/*      */           int match_pos;
/* 1192 */           if (lc_str.charAt(url_start + 4) == 's')
/*      */           {
/* 1194 */             match_pos = url_start + 5;
/*      */           }
/*      */           else
/*      */           {
/* 1198 */             match_pos = url_start + 4;
/*      */           }
/*      */           
/* 1201 */           if (lc_str.substring(match_pos, match_pos + 3).equals("://"))
/*      */           {
/* 1203 */             int url_end = -1;
/*      */             
/* 1205 */             for (int i = match_pos + 3;; i++)
/*      */             {
/* 1207 */               char c = lc_str.charAt(i);
/*      */               
/* 1209 */               if (c == '/')
/*      */               {
/* 1211 */                 url_end = i + 1;
/*      */                 
/* 1213 */                 break;
/*      */               }
/* 1215 */               if ((c != '.') && (c != '-') && (c != ':'))
/*      */               {
/* 1217 */                 if ((c < '0') || (c > '9'))
/*      */                 {
/* 1219 */                   if ((c < 'a') || (c > 'z'))
/*      */                   {
/*      */ 
/*      */ 
/* 1223 */                     url_end = i;
/*      */                     
/* 1225 */                     break;
/*      */                   } }
/*      */               }
/* 1228 */               if (i == lc_str.length() - 1)
/*      */               {
/* 1230 */                 url_end = i;
/*      */               }
/*      */             }
/*      */             
/* 1234 */             if (url_end <= url_start)
/*      */               break;
/* 1236 */             String url_str = str.substring(url_start, url_end);
/*      */             
/* 1238 */             boolean appended = false;
/*      */             
/*      */ 
/*      */             try
/*      */             {
/* 1243 */               URL url = new URL(url_str);
/*      */               
/* 1245 */               if (url.getHost().length() > 0)
/*      */               {
/* 1247 */                 boolean existing_only = true;
/*      */                 
/*      */ 
/*      */ 
/* 1251 */                 for (int i = url_start - 1; (i >= 0) && (url_start - i < 512); i--)
/*      */                 {
/* 1253 */                   if (lc_str.charAt(i) == '<')
/*      */                   {
/* 1255 */                     String prefix = lc_str.substring(i, url_start);
/*      */                     
/* 1257 */                     if (prefix.contains("form"))
/*      */                     {
/* 1259 */                       existing_only = false; break;
/*      */                     }
/* 1261 */                     if ((!prefix.contains("meta")) || (!prefix.contains("http-equiv"))) {
/*      */                       break;
/*      */                     }
/* 1264 */                     existing_only = false; break;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 1271 */                 HTTPAuthHelper child = HTTPAuthHelper.this.getChild(url_str, existing_only);
/*      */                 
/* 1273 */                 if (child != null)
/*      */                 {
/* 1275 */                   String replacement = "http://127.0.0.1:" + child.getPort();
/*      */                   
/* 1277 */                   if (url_str.endsWith("/"))
/*      */                   {
/* 1279 */                     replacement = replacement + "/";
/*      */                   }
/*      */                   
/* 1282 */                   if (result == null)
/*      */                   {
/* 1284 */                     result = new StringBuffer(str.length());
/*      */                     
/* 1286 */                     if (url_start > 0)
/*      */                     {
/* 1288 */                       result.append(str.subSequence(0, url_start));
/*      */                     }
/* 1290 */                   } else if (url_start > str_pos)
/*      */                   {
/* 1292 */                     result.append(str.subSequence(str_pos, url_start));
/*      */                   }
/*      */                   
/* 1295 */                   HTTPAuthHelper.this.trace("Replacing " + url_str + " with " + replacement);
/*      */                   
/* 1297 */                   result.append(replacement);
/*      */                   
/* 1299 */                   appended = true;
/*      */                 }
/*      */                 else
/*      */                 {
/* 1303 */                   HTTPAuthHelper.this.trace("    No child for " + url_str);
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */ 
/* 1310 */             if ((result != null) && (!appended))
/*      */             {
/* 1312 */               result.append(str.subSequence(str_pos, url_end));
/*      */             }
/*      */             
/* 1315 */             str_pos = url_end;
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*      */ 
/* 1323 */             if (result != null)
/*      */             {
/* 1325 */               result.append(str.subSequence(str_pos, match_pos));
/*      */             }
/*      */             
/* 1328 */             str_pos = match_pos;
/*      */           }
/*      */         }
/*      */         
/* 1332 */         if (result != null)
/*      */         {
/* 1334 */           if (str_pos < str.length())
/*      */           {
/* 1336 */             result.append(str.subSequence(str_pos, str.length()));
/*      */           }
/*      */           
/* 1339 */           sb = result;
/*      */         }
/*      */         
/* 1342 */         source_os.write(("Content-Length: " + sb.length() + "\r\n").getBytes());
/*      */         
/* 1344 */         source_os.write("\r\n".getBytes());
/*      */         
/* 1346 */         source_os.write(sb.toString().getBytes(content_charset));
/*      */       }
/*      */       else
/*      */       {
/* 1350 */         source_os.write("\r\n".getBytes());
/*      */         
/* 1352 */         while (!this.destroyed)
/*      */         {
/* 1354 */           int len = target_is.read(buffer);
/*      */           
/* 1356 */           if (len <= 0) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1361 */           source_os.write(buffer, 0, len);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private String readHeader(InputStream is)
/*      */       throws IOException
/*      */     {
/* 1372 */       String header = "";
/*      */       
/* 1374 */       byte[] buffer = new byte[1];
/*      */       
/* 1376 */       boolean found = false;
/*      */       
/*      */ 
/*      */ 
/* 1380 */       while (is.read(buffer) == 1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1385 */         header = header + (char)buffer[0];
/*      */         
/* 1387 */         if (header.endsWith("\r\n\r\n"))
/*      */         {
/* 1389 */           found = true;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1395 */       if (!found)
/*      */       {
/* 1397 */         throw new IOException("End of stream reading header");
/*      */       }
/*      */       
/* 1400 */       return header;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private String[] splitHeader(String str)
/*      */     {
/* 1407 */       String[] bits = str.split("\r\n");
/*      */       
/* 1409 */       return bits;
/*      */     }
/*      */     
/*      */ 
/*      */     private void destroy()
/*      */     {
/* 1415 */       synchronized (this)
/*      */       {
/* 1417 */         if (this.destroyed)
/*      */         {
/* 1419 */           return;
/*      */         }
/*      */         
/* 1422 */         this.destroyed = true;
/*      */       }
/*      */       
/* 1425 */       if (this.socket_out != null) {
/*      */         try
/*      */         {
/* 1428 */           this.socket_out.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1435 */         this.socket_in.close();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 1457 */       HTTPAuthHelper proxy = new HTTPAuthHelper(new URL("https://client.vuze.com/"));
/*      */       
/* 1459 */       proxy.start();
/*      */       
/* 1461 */       System.out.println("port=" + proxy.getPort());
/*      */       
/*      */       for (;;)
/*      */       {
/* 1465 */         Thread.sleep(1000L);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1469 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/http/HTTPAuthHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */