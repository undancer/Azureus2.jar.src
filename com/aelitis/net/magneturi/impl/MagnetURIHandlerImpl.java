/*      */ package com.aelitis.net.magneturi.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.HTTPUtils;
/*      */ import com.aelitis.azureus.core.util.png.PNG;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandler;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandler.ResourceProvider;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandlerListener;
/*      */ import com.aelitis.net.magneturi.MagnetURIHandlerProgressListener;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.ServerSocket;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketException;
/*      */ import java.net.URL;
/*      */ import java.net.URLEncoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.bouncycastle.util.encoders.Base64;
/*      */ 
/*      */ public class MagnetURIHandlerImpl
/*      */   extends MagnetURIHandler
/*      */ {
/*   50 */   private static final LogIDs LOGID = LogIDs.NET;
/*      */   
/*      */ 
/*      */   private static MagnetURIHandlerImpl singleton;
/*      */   
/*   55 */   private static AEMonitor class_mon = new AEMonitor("MagnetURLHandler:class");
/*      */   
/*      */   private static final int DOWNLOAD_TIMEOUT = -1;
/*      */   
/*      */   protected static final String NL = "\r\n";
/*      */   private static final boolean DEBUG = false;
/*      */   private int port;
/*      */   
/*      */   public static MagnetURIHandler getSingleton()
/*      */   {
/*      */     try
/*      */     {
/*   67 */       class_mon.enter();
/*      */       
/*   69 */       if (singleton == null)
/*      */       {
/*   71 */         singleton = new MagnetURIHandlerImpl();
/*      */       }
/*      */       
/*   74 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*   78 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*   84 */   private CopyOnWriteList<MagnetURIHandlerListener> listeners = new CopyOnWriteList();
/*      */   
/*   86 */   private Map info_map = new HashMap();
/*      */   
/*   88 */   private Map<String, MagnetURIHandler.ResourceProvider> resources = new HashMap();
/*      */   
/*      */ 
/*      */   protected MagnetURIHandlerImpl()
/*      */   {
/*   93 */     ServerSocket socket = null;
/*      */     
/*   95 */     for (int i = 45100; i <= 45199; i++)
/*      */     {
/*      */       try
/*      */       {
/*   99 */         socket = new ServerSocket(i, 50, InetAddress.getByName("127.0.0.1"));
/*      */         
/*  101 */         this.port = i;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  110 */     COConfigurationManager.setIntDefault("magnet.uri.port", this.port);
/*      */     
/*  112 */     COConfigurationManager.registerExportedParameter("magnet.port", "magnet.uri.port");
/*      */     
/*  114 */     if (socket == null)
/*      */     {
/*      */ 
/*  117 */       if (Logger.isEnabled()) {
/*  118 */         Logger.log(new LogEvent(LOGID, 3, "MagnetURI: no free sockets, giving up"));
/*      */       }
/*      */     }
/*      */     else {
/*  122 */       if (Logger.isEnabled()) {
/*  123 */         Logger.log(new LogEvent(LOGID, "MagnetURI: bound on " + socket.getLocalPort()));
/*      */       }
/*      */       
/*  126 */       final ServerSocket f_socket = socket;
/*      */       
/*  128 */       Thread t = new AEThread("MagnetURIHandler")
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */ 
/*  134 */           int errors = 0;
/*  135 */           int ok = 0;
/*      */           
/*      */           try
/*      */           {
/*      */             for (;;)
/*      */             {
/*  141 */               final Socket sck = f_socket.accept();
/*      */               
/*  143 */               ok++;
/*      */               
/*  145 */               errors = 0;
/*      */               
/*  147 */               new AEThread2("MagnetURIHandler:processor", true)
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*  152 */                   boolean close_socket = true;
/*      */                   try
/*      */                   {
/*  155 */                     String address = sck.getInetAddress().getHostAddress();
/*      */                     
/*  157 */                     if ((address.equals("localhost")) || (address.equals("127.0.0.1")))
/*      */                     {
/*  159 */                       BufferedReader br = new BufferedReader(new InputStreamReader(sck.getInputStream(), "UTF8"));
/*      */                       
/*  161 */                       String line = br.readLine();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  184 */                       if (line != null)
/*      */                       {
/*  186 */                         if (line.toUpperCase().startsWith("GET "))
/*      */                         {
/*  188 */                           Logger.log(new LogEvent(MagnetURIHandlerImpl.LOGID, "MagnetURIHandler: processing '" + line + "'"));
/*      */                           
/*      */ 
/*  191 */                           line = line.substring(4);
/*      */                           
/*  193 */                           int pos = line.lastIndexOf(' ');
/*      */                           
/*  195 */                           line = line.substring(0, pos);
/*      */                           
/*  197 */                           close_socket = MagnetURIHandlerImpl.this.process(line, br, sck.getOutputStream());
/*      */                         }
/*      */                         else
/*      */                         {
/*  201 */                           Logger.log(new LogEvent(MagnetURIHandlerImpl.LOGID, 1, "MagnetURIHandler: invalid command - '" + line + "'"));
/*      */                         }
/*      */                         
/*      */ 
/*      */                       }
/*      */                       else {
/*  207 */                         Logger.log(new LogEvent(MagnetURIHandlerImpl.LOGID, 1, "MagnetURIHandler: connect from '" + address + "': no data read"));
/*      */                       }
/*      */                       
/*      */ 
/*      */                     }
/*      */                     else
/*      */                     {
/*      */ 
/*  215 */                       Logger.log(new LogEvent(MagnetURIHandlerImpl.LOGID, 1, "MagnetURIHandler: connect from invalid address '" + address + "'"));
/*      */                     }
/*      */                     return;
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*  221 */                     if ((!(e instanceof IOException)) && (!(e instanceof SocketException)))
/*      */                     {
/*  223 */                       Debug.printStackTrace(e);
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/*      */                     try
/*      */                     {
/*  230 */                       if (close_socket)
/*      */                       {
/*  232 */                         sck.close();
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     catch (Throwable e) {}
/*      */                   }
/*      */                 }
/*      */               }.start();
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  244 */             Debug.printStackTrace(e);
/*      */             
/*  246 */             errors++;
/*      */             
/*  248 */             if (errors > 100)
/*      */             {
/*  250 */               if (Logger.isEnabled()) {
/*  251 */                 Logger.log(new LogEvent(MagnetURIHandlerImpl.LOGID, "MagnetURIHandler: bailing out, too many socket errors"));
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*  261 */       };
/*  262 */       t.setDaemon(true);
/*      */       
/*  264 */       t.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void process(final String get, final InputStream is, final OutputStream os)
/*      */     throws IOException
/*      */   {
/*  276 */     new AEThread2("MagnetProcessor", true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  281 */         boolean close = false;
/*      */         try
/*      */         {
/*  284 */           close = MagnetURIHandlerImpl.this.process(get, new BufferedReader(new InputStreamReader(is)), os); return;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  288 */           Debug.out("Magnet processing failed", e);
/*      */         }
/*      */         finally
/*      */         {
/*  292 */           if (close) {
/*      */             try
/*      */             {
/*  295 */               is.close();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  299 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */           try
/*      */           {
/*  304 */             os.flush();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  308 */             Debug.out(e);
/*      */           }
/*      */           
/*  311 */           if (close) {
/*      */             try
/*      */             {
/*  314 */               os.close();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  318 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }.start();
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
/*      */   protected boolean process(String get, BufferedReader is, OutputStream os)
/*      */     throws IOException
/*      */   {
/*  338 */     Map<String, String> original_params = new HashMap();
/*  339 */     Map<String, String> lc_params = new HashMap();
/*      */     
/*  341 */     List<String> source_params = new ArrayList();
/*      */     
/*  343 */     int pos = get.indexOf('?');
/*      */     
/*      */     String arg_str;
/*      */     String arg_str;
/*  347 */     if (pos == -1)
/*      */     {
/*  349 */       arg_str = "";
/*      */     }
/*      */     else
/*      */     {
/*  353 */       arg_str = get.substring(pos + 1);
/*      */       
/*  355 */       pos = arg_str.lastIndexOf(' ');
/*      */       
/*  357 */       if (pos >= 0)
/*      */       {
/*  359 */         arg_str = arg_str.substring(0, pos).trim();
/*      */       }
/*      */       
/*  362 */       StringTokenizer tok = new StringTokenizer(arg_str, "&");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  368 */       while (tok.hasMoreTokens())
/*      */       {
/*  370 */         String arg = tok.nextToken();
/*      */         
/*  372 */         pos = arg.indexOf('=');
/*      */         
/*  374 */         if (pos == -1)
/*      */         {
/*  376 */           String lhs = arg.trim();
/*      */           
/*  378 */           original_params.put(lhs, "");
/*      */           
/*  380 */           lc_params.put(lhs.toLowerCase(MessageText.LOCALE_ENGLISH), "");
/*      */         }
/*      */         else
/*      */         {
/*      */           try {
/*  385 */             String lhs = arg.substring(0, pos).trim();
/*  386 */             String lc_lhs = lhs.toLowerCase(MessageText.LOCALE_ENGLISH);
/*      */             
/*  388 */             String rhs = UrlUtils.decode(arg.substring(pos + 1).trim());
/*      */             
/*  390 */             if (lc_lhs.equals("xt"))
/*      */             {
/*  392 */               if (rhs.toLowerCase(MessageText.LOCALE_ENGLISH).startsWith("urn:btih:"))
/*      */               {
/*  394 */                 original_params.put(lhs, rhs);
/*      */                 
/*  396 */                 lc_params.put(lhs, rhs);
/*      */               }
/*      */               else
/*      */               {
/*  400 */                 String existing = (String)lc_params.get("xt");
/*      */                 
/*  402 */                 if ((existing == null) || ((!existing.toLowerCase(MessageText.LOCALE_ENGLISH).startsWith("urn:btih:")) && (rhs.startsWith("urn:sha1:"))))
/*      */                 {
/*      */ 
/*  405 */                   original_params.put(lhs, rhs);
/*      */                   
/*  407 */                   lc_params.put(lhs, rhs);
/*      */                 }
/*      */               }
/*      */             }
/*      */             else {
/*  412 */               original_params.put(lhs, rhs);
/*      */               
/*  414 */               lc_params.put(lc_lhs, rhs);
/*      */               
/*  416 */               if (lc_lhs.equals("xsource"))
/*      */               {
/*  418 */                 source_params.add(rhs);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  423 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  430 */     if (get.startsWith("/magnet10/badge.img"))
/*      */     {
/*  432 */       for (MagnetURIHandlerListener listener : this.listeners)
/*      */       {
/*  434 */         byte[] data = listener.badge();
/*      */         
/*  436 */         if (data != null)
/*      */         {
/*  438 */           writeReply(os, "image/gif", data);
/*      */           
/*  440 */           return true;
/*      */         }
/*      */       }
/*      */       
/*  444 */       writeNotFound(os);
/*      */       
/*  446 */       return true;
/*      */     }
/*  448 */     if (get.startsWith("/magnet10/canHandle.img?"))
/*      */     {
/*  450 */       String urn = (String)lc_params.get("xt");
/*      */       
/*  452 */       if ((urn != null) && (urn.toLowerCase(MessageText.LOCALE_ENGLISH).startsWith("urn:btih:")))
/*      */       {
/*  454 */         for (MagnetURIHandlerListener listener : this.listeners)
/*      */         {
/*  456 */           byte[] data = listener.badge();
/*      */           
/*  458 */           if (data != null)
/*      */           {
/*  460 */             writeReply(os, "image/gif", data);
/*      */             
/*  462 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  467 */       writeNotFound(os);
/*      */       
/*  469 */       return true;
/*      */     }
/*  471 */     if (get.startsWith("/azversion"))
/*      */     {
/*  473 */       writeReply(os, "text/plain", "5.7.6.0");
/*      */       
/*  475 */       return true;
/*      */     }
/*  477 */     if ((get.startsWith("/magnet10/options.js?")) || (get.startsWith("/magnet10/default.js?")))
/*      */     {
/*      */ 
/*  480 */       String resp = "";
/*      */       
/*  482 */       resp = resp + getJS("magnetOptionsPreamble");
/*      */       
/*  484 */       resp = resp + getJSS("<a href=\\\"http://127.0.0.1:\"+(45100+magnetCurrentSlot)+\"/select/?\"+magnetQueryString+\"\\\" target=\\\"_blank\\\">");
/*  485 */       resp = resp + getJSS("<img src=\\\"http://127.0.0.1:\"+(45100+magnetCurrentSlot)+\"/magnet10/badge.img\\\">");
/*  486 */       resp = resp + getJSS("Download with Azureus");
/*  487 */       resp = resp + getJSS("</a>");
/*      */       
/*  489 */       resp = resp + getJS("magnetOptionsPostamble");
/*      */       
/*  491 */       resp = resp + "magnetOptionsPollSuccesses++";
/*      */       
/*  493 */       writeReply(os, "application/x-javascript", resp);
/*      */       
/*  495 */       return true;
/*      */     }
/*  497 */     if (get.startsWith("/magnet10/pause"))
/*      */     {
/*      */       try {
/*  500 */         Thread.sleep(250L);
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*  505 */       writeNotFound(os);
/*      */       
/*  507 */       return true;
/*      */     }
/*  509 */     if (get.startsWith("/select/"))
/*      */     {
/*  511 */       String fail_reason = "";
/*      */       
/*  513 */       boolean ok = false;
/*      */       
/*  515 */       String urn = (String)lc_params.get("xt");
/*      */       
/*  517 */       if (urn == null)
/*      */       {
/*  519 */         fail_reason = "xt missing";
/*      */       }
/*      */       else
/*      */       {
/*  523 */         String lc_urn = urn.toLowerCase(MessageText.LOCALE_ENGLISH);
/*      */         
/*      */         try
/*      */         {
/*      */           URL url;
/*      */           URL url;
/*  529 */           if ((lc_urn.startsWith("http:")) || (lc_urn.startsWith("https:")))
/*      */           {
/*  531 */             url = new URL(urn);
/*      */           }
/*      */           else
/*      */           {
/*  535 */             url = new URL("magnet:?xt=" + urn);
/*      */           }
/*      */           
/*  538 */           for (MagnetURIHandlerListener listener : this.listeners)
/*      */           {
/*  540 */             if (listener.download(url))
/*      */             {
/*  542 */               ok = true;
/*      */               
/*  544 */               break;
/*      */             }
/*      */           }
/*      */           
/*  548 */           if (!ok)
/*      */           {
/*  550 */             fail_reason = "No listeners accepted the operation";
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  554 */           Debug.printStackTrace(e);
/*      */           
/*  556 */           fail_reason = Debug.getNestedExceptionMessage(e);
/*      */         }
/*      */       }
/*      */       
/*  560 */       if (ok)
/*      */       {
/*  562 */         if ("image".equalsIgnoreCase((String)lc_params.get("result")))
/*      */         {
/*  564 */           for (MagnetURIHandlerListener listener : this.listeners)
/*      */           {
/*  566 */             byte[] data = listener.badge();
/*      */             
/*  568 */             if (data != null)
/*      */             {
/*  570 */               writeReply(os, "image/gif", data);
/*      */               
/*  572 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  577 */         writeReply(os, "text/plain", "Download initiated");
/*      */       }
/*      */       else
/*      */       {
/*  581 */         writeReply(os, "text/plain", "Download initiation failed: " + fail_reason);
/*      */       }
/*      */     }
/*  584 */     else if (get.startsWith("/download/"))
/*      */     {
/*  586 */       final PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
/*      */       try
/*      */       {
/*  589 */         pw.print("HTTP/1.0 200 OK\r\n");
/*      */         
/*  591 */         pw.flush();
/*      */         
/*  593 */         String urn = (String)lc_params.get("xt");
/*      */         
/*  595 */         if ((urn == null) || ((!urn.toLowerCase(MessageText.LOCALE_ENGLISH).startsWith("urn:sha1:")) && (!urn.toLowerCase(MessageText.LOCALE_ENGLISH).startsWith("urn:btih:")))) {
/*  596 */           if (Logger.isEnabled()) {
/*  597 */             Logger.log(new LogEvent(LOGID, 1, "MagnetURIHandler: invalid command - '" + get + "'"));
/*      */           }
/*      */           
/*  600 */           throw new IOException("Invalid magnet URI - no urn:sha1 or urn:btih argument supplied.");
/*      */         }
/*      */         
/*  603 */         String encoded = urn.substring(9);
/*      */         
/*  605 */         List<InetSocketAddress> sources = new ArrayList();
/*      */         
/*  607 */         for (int i = 0; i < source_params.size(); i++)
/*      */         {
/*  609 */           String source = (String)source_params.get(i);
/*      */           
/*  611 */           int p = source.indexOf(':');
/*      */           
/*  613 */           if (p != -1) {
/*      */             try
/*      */             {
/*  616 */               String host = source.substring(0, p);
/*  617 */               int port = Integer.parseInt(source.substring(p + 1));
/*      */               
/*      */ 
/*      */ 
/*  621 */               if (host.startsWith("/"))
/*      */               {
/*  623 */                 host = host.substring(1);
/*      */               }
/*      */               
/*  626 */               InetSocketAddress sa = new InetSocketAddress(host, port);
/*      */               
/*  628 */               sources.add(sa);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  632 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  637 */         final InetSocketAddress[] s = (InetSocketAddress[])sources.toArray(new InetSocketAddress[sources.size()]);
/*      */         
/*  639 */         if (Logger.isEnabled()) {
/*  640 */           Logger.log(new LogEvent(LOGID, "MagnetURIHandler: download of '" + encoded + "' starts (initial sources=" + s.length + ")"));
/*      */         }
/*      */         
/*  643 */         byte[] _sha1 = UrlUtils.decodeSHA1Hash(encoded);
/*      */         
/*  645 */         if (_sha1 == null)
/*      */         {
/*  647 */           _sha1 = new byte[20];
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  652 */         final byte[] sha1 = _sha1;
/*      */         
/*  654 */         byte[] data = null;
/*      */         
/*  656 */         String verbose_str = (String)lc_params.get("verbose");
/*      */         
/*  658 */         final boolean verbose = (verbose_str != null) && (verbose_str.equalsIgnoreCase("true"));
/*      */         
/*  660 */         final boolean[] cancel = { false };
/*      */         
/*  662 */         TimerEventPeriodic keep_alive = SimpleTimer.addPeriodicEvent("MURI:keepalive", 5000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  672 */             pw.print("X-KeepAlive: YEAH!\r\n");
/*      */             
/*  674 */             boolean failed = pw.checkError();
/*      */             
/*  676 */             if (failed)
/*      */             {
/*  678 */               synchronized (cancel)
/*      */               {
/*  680 */                 cancel[0] = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */         try
/*      */         {
/*  687 */           final String f_arg_str = arg_str;
/*      */           
/*  689 */           final byte[][] f_data = { null };
/*  690 */           final Throwable[] f_error = { null };
/*      */           
/*  692 */           final AESemaphore wait_sem = new AESemaphore("download-waiter");
/*      */           
/*  694 */           List<Runnable> tasks = new ArrayList();
/*      */           
/*  696 */           for (final MagnetURIHandlerListener listener : this.listeners)
/*      */           {
/*  698 */             tasks.add(new Runnable()
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*      */                 try
/*      */                 {
/*  705 */                   byte[] data = listener.download(new MagnetURIHandlerProgressListener()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void reportSize(long size)
/*      */                     {
/*      */ 
/*      */ 
/*  713 */                       MagnetURIHandlerImpl.4.this.val$pw.print("X-Report: " + MagnetURIHandlerImpl.this.getMessageText("torrent_size", String.valueOf(size)) + "\r\n");
/*      */                       
/*  715 */                       MagnetURIHandlerImpl.4.this.val$pw.flush();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                     public void reportActivity(String str)
/*      */                     {
/*  722 */                       MagnetURIHandlerImpl.4.this.val$pw.print("X-Report: " + str + "\r\n");
/*      */                       
/*  724 */                       MagnetURIHandlerImpl.4.this.val$pw.flush();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */                     public void reportCompleteness(int percent)
/*      */                     {
/*  731 */                       MagnetURIHandlerImpl.4.this.val$pw.print("X-Report: " + MagnetURIHandlerImpl.this.getMessageText("percent", String.valueOf(percent)) + "\r\n");
/*      */                       
/*  733 */                       MagnetURIHandlerImpl.4.this.val$pw.flush();
/*      */                     }
/*      */                     
/*      */ 
/*      */                     public boolean verbose()
/*      */                     {
/*  739 */                       return MagnetURIHandlerImpl.4.this.val$verbose;
/*      */                     }
/*      */                     
/*      */ 
/*      */                     public boolean cancelled()
/*      */                     {
/*  745 */                       synchronized (MagnetURIHandlerImpl.4.this.val$cancel)
/*      */                       {
/*  747 */                         if (MagnetURIHandlerImpl.4.this.val$cancel[0] != 0)
/*      */                         {
/*  749 */                           return true;
/*      */                         }
/*      */                       }
/*      */                       
/*  753 */                       synchronized (MagnetURIHandlerImpl.4.this.val$f_data)
/*      */                       {
/*  755 */                         return MagnetURIHandlerImpl.4.this.val$f_data[0] != null; } } }, sha1, f_arg_str, s, -1L);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  764 */                   synchronized (f_data)
/*      */                   {
/*  766 */                     if (data != null)
/*      */                     {
/*  768 */                       if (f_data[0] == null)
/*      */                       {
/*  770 */                         f_data[0] = data;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/*  776 */                   synchronized (f_data)
/*      */                   {
/*  778 */                     f_error[0] = e;
/*      */                   }
/*      */                 }
/*      */                 finally
/*      */                 {
/*  783 */                   wait_sem.release();
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */           
/*  789 */           if (tasks.size() > 0)
/*      */           {
/*  791 */             if (tasks.size() == 1)
/*      */             {
/*  793 */               ((Runnable)tasks.get(0)).run();
/*      */             }
/*      */             else
/*      */             {
/*  797 */               for (final Runnable task : tasks)
/*      */               {
/*  799 */                 new AEThread2("MUH:dasync")
/*      */                 {
/*      */ 
/*      */                   public void run()
/*      */                   {
/*  804 */                     task.run();
/*      */                   }
/*      */                 }.start();
/*      */               }
/*      */               
/*  809 */               for (int i = 0; i < tasks.size(); i++)
/*      */               {
/*  811 */                 wait_sem.reserve();
/*      */               }
/*      */             }
/*      */             
/*  815 */             synchronized (f_data)
/*      */             {
/*  817 */               data = f_data[0];
/*      */               
/*  819 */               if (data == null)
/*      */               {
/*  821 */                 if (f_error[0] != null)
/*      */                 {
/*  823 */                   throw f_error[0];
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  831 */           keep_alive.cancel();
/*      */         }
/*      */         
/*  834 */         if (Logger.isEnabled()) {
/*  835 */           Logger.log(new LogEvent(LOGID, "MagnetURIHandler: download of '" + encoded + "' completes, data " + (data == null ? "not found" : new StringBuilder().append("found, length = ").append(data.length).toString())));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  841 */         if (data != null)
/*      */         {
/*  843 */           pw.print("Content-Length: " + data.length + "\r\n" + "\r\n");
/*      */           
/*  845 */           pw.flush();
/*      */           
/*  847 */           os.write(data);
/*      */           
/*  849 */           os.flush();
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  856 */           pw.print("X-Report: error: " + getMessageText("no_sources") + "\r\n");
/*      */           
/*  858 */           pw.flush();
/*      */           
/*      */ 
/*      */ 
/*  862 */           return !lc_params.containsKey("pause_on_error");
/*      */         }
/*      */         
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  868 */         pw.print("X-Report: error: " + getMessageText("error", Debug.getNestedExceptionMessage(e)) + "\r\n");
/*      */         
/*  870 */         pw.flush();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  876 */         return !lc_params.containsKey("pause_on_error");
/*      */       }
/*  878 */     } else { if (get.startsWith("/getinfo?"))
/*      */       {
/*  880 */         String name = (String)lc_params.get("name");
/*      */         
/*  882 */         if (name != null)
/*      */         {
/*  884 */           Integer info = (Integer)this.info_map.get(name);
/*      */           
/*  886 */           int value = Integer.MIN_VALUE;
/*      */           
/*  888 */           if (info != null)
/*      */           {
/*  890 */             value = info.intValue();
/*      */           }
/*      */           else
/*      */           {
/*  894 */             for (MagnetURIHandlerListener listener : this.listeners)
/*      */             {
/*      */ 
/*      */ 
/*  898 */               HashMap paramsCopy = new HashMap();
/*      */               
/*  900 */               paramsCopy.putAll(original_params);
/*      */               
/*  902 */               value = listener.get(name, paramsCopy);
/*      */               
/*  904 */               if (value != Integer.MIN_VALUE) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  911 */           if (value == Integer.MIN_VALUE)
/*      */           {
/*      */ 
/*      */ 
/*  915 */             String def_str = (String)lc_params.get("default");
/*      */             
/*  917 */             if (def_str != null) {
/*      */               try
/*      */               {
/*  920 */                 value = Integer.parseInt(def_str);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  924 */                 Debug.printStackTrace(e);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  931 */             String max_str = (String)lc_params.get("max");
/*      */             
/*  933 */             if (max_str != null) {
/*      */               try
/*      */               {
/*  936 */                 int max = Integer.parseInt(max_str);
/*      */                 
/*  938 */                 if (value > max)
/*      */                 {
/*  940 */                   value = max;
/*      */                 }
/*      */               }
/*      */               catch (Throwable e) {
/*  944 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  949 */           if (value != Integer.MIN_VALUE)
/*      */           {
/*  951 */             if (value < 0)
/*      */             {
/*  953 */               value = 0;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  958 */             if (value > 1048576)
/*      */             {
/*  960 */               value = 1048576;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  966 */             int width = value;
/*  967 */             int height = 1;
/*      */             
/*      */ 
/*      */ 
/*  971 */             String div_mod = (String)lc_params.get("divmod");
/*      */             
/*  973 */             if (div_mod != null)
/*      */             {
/*  975 */               int n = Integer.parseInt(div_mod);
/*      */               
/*  977 */               width = value / n + 1;
/*  978 */               height = value % n + 1;
/*      */             }
/*      */             else
/*      */             {
/*  982 */               String div = (String)lc_params.get("div");
/*      */               
/*  984 */               if (div != null)
/*      */               {
/*  986 */                 width = value / Integer.parseInt(div);
/*      */               }
/*      */               else
/*      */               {
/*  990 */                 String mod = (String)lc_params.get("mod");
/*      */                 
/*  992 */                 if (mod != null)
/*      */                 {
/*  994 */                   width = value % Integer.parseInt(mod);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  999 */             String img_type = (String)lc_params.get("img_type");
/*      */             
/* 1001 */             if ((img_type != null) && (img_type.equals("png")))
/*      */             {
/* 1003 */               byte[] data = PNG.getPNGBytesForSize(width, height);
/*      */               
/* 1005 */               writeReply(os, "image/png", data);
/*      */             }
/*      */             else
/*      */             {
/* 1009 */               ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */               
/* 1011 */               writeImage(baos, width, height);
/*      */               
/* 1013 */               byte[] data = baos.toByteArray();
/*      */               
/* 1015 */               writeReply(os, "image/bmp", data);
/*      */             }
/*      */             
/* 1018 */             return true;
/*      */           }
/*      */         }
/*      */         
/* 1022 */         writeNotFound(os);
/*      */         
/* 1024 */         return true;
/*      */       }
/* 1026 */       if (get.startsWith("/setinfo?"))
/*      */       {
/* 1028 */         String name = (String)lc_params.get("name");
/*      */         
/* 1030 */         if (name != null)
/*      */         {
/* 1032 */           boolean result = false;
/*      */           
/* 1034 */           for (MagnetURIHandlerListener listener : this.listeners)
/*      */           {
/*      */ 
/*      */ 
/* 1038 */             HashMap paramsCopy = new HashMap();
/*      */             
/* 1040 */             paramsCopy.putAll(original_params);
/*      */             
/* 1042 */             result = listener.set(name, paramsCopy);
/*      */             
/* 1044 */             if (result) {
/*      */               break;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1050 */           int width = result ? 20 : 10;
/* 1051 */           int height = result ? 20 : 10;
/*      */           
/* 1053 */           String img_type = (String)lc_params.get("img_type");
/*      */           
/* 1055 */           if ((img_type != null) && (img_type.equals("png")))
/*      */           {
/* 1057 */             byte[] data = PNG.getPNGBytesForSize(width, height);
/*      */             
/* 1059 */             writeReply(os, "image/png", data);
/*      */           }
/*      */           else
/*      */           {
/* 1063 */             ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */             
/* 1065 */             writeImage(baos, width, height);
/*      */             
/* 1067 */             byte[] data = baos.toByteArray();
/*      */             
/* 1069 */             writeReply(os, "image/bmp", data);
/*      */           }
/*      */           
/* 1072 */           return true;
/*      */         }
/* 1074 */       } else if (get.equals("/browserheaders.js"))
/*      */       {
/* 1076 */         String headers_str = "";
/*      */         
/*      */         for (;;)
/*      */         {
/* 1080 */           String header = is.readLine();
/*      */           
/* 1082 */           if (header == null) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1087 */           header = header.trim();
/*      */           
/* 1089 */           if (header.length() == 0) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/* 1094 */           headers_str = headers_str + (headers_str.length() == 0 ? "" : "\n") + header;
/*      */         }
/*      */         
/* 1097 */         String script = "var headers = \"" + new String(Base64.encode(headers_str.getBytes("UTF-8"))) + "\";";
/*      */         
/*      */ 
/* 1100 */         writeReply(os, "application/x-javascript", script);
/*      */       }
/* 1102 */       else if (get.startsWith("/resource."))
/*      */       {
/* 1104 */         String rid = (String)lc_params.get("rid");
/*      */         
/*      */         MagnetURIHandler.ResourceProvider provider;
/*      */         
/* 1108 */         synchronized (this.resources)
/*      */         {
/* 1110 */           provider = (MagnetURIHandler.ResourceProvider)this.resources.get(rid);
/*      */         }
/*      */         
/* 1113 */         if (provider != null)
/*      */         {
/* 1115 */           byte[] data = provider.getData();
/*      */           
/* 1117 */           if (data != null)
/*      */           {
/* 1119 */             writeReply(os, HTTPUtils.guessContentTypeFromFileType(provider.getFileType()), data);
/*      */           }
/*      */           else
/*      */           {
/* 1123 */             writeNotFound(os);
/*      */           }
/*      */         }
/*      */         else {
/* 1127 */           writeNotFound(os);
/*      */         }
/*      */       }
/*      */     }
/* 1131 */     return true;
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
/*      */   private void writeImage(OutputStream os, int width, int height)
/*      */   {
/* 1146 */     int rowWidth = width / 8;
/* 1147 */     if (rowWidth % 4 != 0) {
/* 1148 */       rowWidth = (rowWidth / 4 + 1) * 4;
/*      */     }
/* 1150 */     int imageSize = rowWidth * height;
/* 1151 */     int fileSize = 54 + imageSize;
/*      */     try {
/* 1153 */       os.write(new byte[] { 66, 77 });
/*      */       
/*      */ 
/*      */ 
/* 1157 */       write4Bytes(os, fileSize);
/* 1158 */       write4Bytes(os, 0L);
/* 1159 */       write4Bytes(os, 54L);
/*      */       
/* 1161 */       write4Bytes(os, 40L);
/* 1162 */       write4Bytes(os, width);
/* 1163 */       write4Bytes(os, height);
/* 1164 */       write4Bytes(os, 65537L);
/* 1165 */       write4Bytes(os, 0L);
/* 1166 */       write4Bytes(os, imageSize);
/* 1167 */       write4Bytes(os, 0L);
/* 1168 */       write4Bytes(os, 0L);
/* 1169 */       write4Bytes(os, 0L);
/* 1170 */       write4Bytes(os, 0L);
/*      */       
/* 1172 */       byte[] data = new byte[imageSize];
/* 1173 */       os.write(data);
/*      */     }
/*      */     catch (IOException e) {
/* 1176 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */   private void write4Bytes(OutputStream os, long l) {
/*      */     try {
/* 1182 */       os.write((int)(l & 0xFF));
/* 1183 */       os.write((int)(l >> 8 & 0xFF));
/* 1184 */       os.write((int)(l >> 16 & 0xFF));
/* 1185 */       os.write((int)(l >> 24 & 0xFF));
/*      */     } catch (IOException e) {
/* 1187 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getMessageText(String resource)
/*      */   {
/* 1195 */     return MessageText.getString("MagnetURLHandler.report." + resource);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getMessageText(String resource, String param)
/*      */   {
/* 1203 */     if (resource.equals("error"))
/*      */     {
/*      */ 
/*      */ 
/* 1207 */       return param;
/*      */     }
/*      */     
/* 1210 */     return MessageText.getString("MagnetURLHandler.report." + resource, new String[] { param });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getJS(String s)
/*      */   {
/* 1217 */     return "document.write(" + s + ");" + "\r\n";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getJSS(String s)
/*      */   {
/* 1224 */     return "document.write(\"" + s + "\");" + "\r\n";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeReply(OutputStream os, String content_type, String content)
/*      */     throws IOException
/*      */   {
/* 1235 */     writeReply(os, content_type, content.getBytes());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeReply(OutputStream os, String content_type, byte[] content)
/*      */     throws IOException
/*      */   {
/* 1246 */     PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
/*      */     
/* 1248 */     pw.print("HTTP/1.1 200 OK\r\n");
/* 1249 */     pw.print("Cache-Control: no-cache\r\n");
/* 1250 */     pw.print("Pragma: no-cache\r\n");
/* 1251 */     pw.print("Content-Type: " + content_type + "\r\n");
/* 1252 */     pw.print("Content-Length: " + content.length + "\r\n" + "\r\n");
/*      */     
/* 1254 */     pw.flush();
/*      */     
/* 1256 */     os.write(content);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void writeNotFound(OutputStream os)
/*      */     throws IOException
/*      */   {
/* 1266 */     PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
/*      */     
/* 1268 */     pw.print("HTTP/1.0 404 Not Found\r\n\r\n");
/*      */     
/* 1270 */     pw.flush();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/* 1276 */     return this.port;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addInfo(String name, int info)
/*      */   {
/* 1284 */     this.info_map.put(name, new Integer(info));
/*      */     
/* 1286 */     Logger.log(new LogEvent(LOGID, 0, "MagnetURIHandler: global info registered: " + name + " -> " + info));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(MagnetURIHandlerListener l)
/*      */   {
/* 1293 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(MagnetURIHandlerListener l)
/*      */   {
/* 1300 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1307 */     new MagnetURIHandlerImpl();
/*      */     try
/*      */     {
/* 1310 */       Thread.sleep(1000000L);
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public URL registerResource(MagnetURIHandler.ResourceProvider provider)
/*      */   {
/*      */     try
/*      */     {
/* 1321 */       String rid = URLEncoder.encode(provider.getUID(), "UTF-8");
/*      */       
/* 1323 */       synchronized (this.resources)
/*      */       {
/* 1325 */         this.resources.put(rid, provider);
/*      */       }
/*      */       
/* 1328 */       return new URL("http://127.0.0.1:" + this.port + "/resource." + provider.getFileType() + "?rid=" + rid);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1332 */       Debug.out(e);
/*      */     }
/* 1334 */     return null;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/magneturi/impl/MagnetURIHandlerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */