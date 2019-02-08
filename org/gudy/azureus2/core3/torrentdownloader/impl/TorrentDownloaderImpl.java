/*      */ package org.gudy.azureus2.core3.torrentdownloader.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.HttpURLConnection;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Proxy;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.URLDecoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import java.util.zip.InflaterInputStream;
/*      */ import javax.net.ssl.HostnameVerifier;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSession;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import javax.net.ssl.TrustManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
/*      */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.core3.util.protocol.magnet.MagnetConnection;
/*      */ import org.gudy.azureus2.core3.util.protocol.magnet.MagnetConnection2;
/*      */ import org.gudy.azureus2.plugins.clientid.ClientIDGenerator;
/*      */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.xml.rss.RSSUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TorrentDownloaderImpl
/*      */   extends AEThread
/*      */   implements TorrentDownloader
/*      */ {
/*      */   private String original_url;
/*      */   private String url_str;
/*      */   private Proxy proxy;
/*      */   private String referrer;
/*      */   private Map request_properties;
/*      */   private String file_str;
/*      */   private URL url;
/*      */   private URLConnection con;
/*   78 */   private String error = "Ok";
/*   79 */   private String status = "";
/*      */   private TorrentDownloaderCallBackInterface iface;
/*   81 */   private int state = -1;
/*   82 */   private int percentDone = 0;
/*   83 */   private int readTotal = 0;
/*   84 */   private boolean cancel = false;
/*      */   private String filename;
/*   86 */   private String directoryname; private File file = null;
/*   87 */   private final byte[] buf = new byte['ϼ'];
/*   88 */   private int bufBytes = 0;
/*   89 */   private boolean deleteFileOnCancel = true;
/*   90 */   private boolean ignoreReponseCode = false;
/*      */   
/*      */ 
/*   93 */   final AEMonitor this_mon = new AEMonitor("TorrentDownloader");
/*      */   private int errCode;
/*      */   
/*      */   public TorrentDownloaderImpl() {
/*   97 */     super("Torrent Downloader");
/*   98 */     setDaemon(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void init(TorrentDownloaderCallBackInterface _iface, String _url, Proxy _proxy, String _referrer, Map _request_properties, String _file)
/*      */   {
/*  110 */     this.iface = _iface;
/*      */     
/*  112 */     this.original_url = _url;
/*      */     
/*      */ 
/*  115 */     _url = _url.replace('\\', '/');
/*      */     
/*      */ 
/*  118 */     _url = _url.replaceAll(" ", "%20");
/*      */     
/*  120 */     setName("TorrentDownloader: " + _url);
/*      */     
/*  122 */     this.url_str = _url;
/*  123 */     this.proxy = _proxy;
/*  124 */     this.referrer = _referrer;
/*  125 */     this.request_properties = _request_properties;
/*  126 */     this.file_str = _file;
/*      */     
/*  128 */     if ((this.referrer == null) || (this.referrer.length() == 0))
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/*  133 */         this.referrer = this.url_str;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */   }
/*      */   
/*      */   public void notifyListener()
/*      */   {
/*  141 */     if (this.iface != null) {
/*  142 */       this.iface.TorrentDownloaderEvent(this.state, this);
/*  143 */     } else if (this.state == 4)
/*  144 */       System.err.println(this.error);
/*      */   }
/*      */   
/*      */   private void cleanUpFile() {
/*  148 */     if ((this.file != null) && (this.file.exists()))
/*  149 */       this.file.delete();
/*      */   }
/*      */   
/*      */   private void error(int errCode, String err) {
/*      */     try {
/*  154 */       this.this_mon.enter();
/*      */       
/*  156 */       this.state = 4;
/*  157 */       setError(errCode, err);
/*  158 */       cleanUpFile();
/*  159 */       notifyListener();
/*      */     }
/*      */     finally {
/*  162 */       this.this_mon.exit();
/*      */       
/*  164 */       closeConnection();
/*      */     }
/*      */   }
/*      */   
/*      */   public void runSupport()
/*      */   {
/*      */     try
/*      */     {
/*  172 */       new URL(this.url_str);
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */ 
/*  178 */       String magnet_uri = UrlUtils.normaliseMagnetURI(this.url_str);
/*      */       
/*  180 */       if (magnet_uri != null)
/*      */       {
/*  182 */         this.url_str = magnet_uri;
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  187 */       this.url = AddressUtils.adjustURL(new URL(this.url_str));
/*      */       
/*  189 */       String protocol = this.url.getProtocol().toLowerCase(Locale.US);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  194 */       if ((protocol.equals("magnet")) || (protocol.equals("maggot")) || (protocol.equals("dht")))
/*      */       {
/*  196 */         this.url = AddressUtils.adjustURL(new URL(this.url_str + (this.url_str.contains("?") ? "&" : "?") + "pause_on_error=true"));
/*      */       }
/*      */       
/*  199 */       Set<String> redirect_urls = new HashSet();
/*      */       
/*  201 */       boolean follow_redirect = true;
/*      */       
/*  203 */       URL current_url = this.url;
/*  204 */       Proxy current_proxy = this.proxy;
/*      */       
/*  206 */       AEProxyFactory.PluginProxy current_plugin_proxy = AEProxyFactory.getPluginProxy(current_proxy);
/*      */       
/*      */       label1299:
/*      */       
/*  210 */       while (follow_redirect)
/*      */       {
/*  212 */         follow_redirect = false;
/*      */         
/*  214 */         boolean dh_hack = false;
/*  215 */         boolean internal_error_hack = false;
/*      */         
/*  217 */         for (int connect_loop = 0;; connect_loop++) { if (connect_loop >= 3)
/*      */             break label1299;
/*  219 */           protocol = current_url.getProtocol().toLowerCase(Locale.US);
/*      */           
/*      */           try
/*      */           {
/*  223 */             if (protocol.equals("https"))
/*      */             {
/*      */               HttpsURLConnection ssl_con;
/*      */               
/*      */               HttpsURLConnection ssl_con;
/*      */               
/*  229 */               if (current_proxy == null)
/*      */               {
/*  231 */                 ssl_con = (HttpsURLConnection)current_url.openConnection();
/*      */               }
/*      */               else
/*      */               {
/*  235 */                 ssl_con = (HttpsURLConnection)current_url.openConnection(current_proxy);
/*      */               }
/*      */               
/*  238 */               if (!internal_error_hack)
/*      */               {
/*      */ 
/*  241 */                 ssl_con.setHostnameVerifier(new HostnameVerifier()
/*      */                 {
/*      */ 
/*      */ 
/*      */                   public boolean verify(String host, SSLSession session)
/*      */                   {
/*      */ 
/*      */ 
/*  249 */                     return true;
/*      */                   }
/*      */                 });
/*      */               }
/*      */               
/*  254 */               if (dh_hack)
/*      */               {
/*  256 */                 UrlUtils.DHHackIt(ssl_con);
/*      */               }
/*      */               
/*  259 */               if (connect_loop > 0)
/*      */               {
/*      */ 
/*      */ 
/*  263 */                 TrustManager[] trustAllCerts = SESecurityManager.getAllTrustingTrustManager();
/*      */                 try
/*      */                 {
/*  266 */                   SSLContext sc = SSLContext.getInstance("SSL");
/*      */                   
/*  268 */                   sc.init(null, trustAllCerts, RandomUtils.SECURE_RANDOM);
/*      */                   
/*  270 */                   SSLSocketFactory factory = sc.getSocketFactory();
/*      */                   
/*  272 */                   ssl_con.setSSLSocketFactory(factory);
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               
/*      */ 
/*  278 */               if (internal_error_hack)
/*      */               {
/*  280 */                 if (current_plugin_proxy != null)
/*      */                 {
/*  282 */                   String host = current_plugin_proxy.getURLHostRewrite();
/*      */                   
/*  284 */                   UrlUtils.HTTPSURLConnectionSNIHack(host, ssl_con);
/*      */                 }
/*      */               }
/*      */               
/*  288 */               this.con = ssl_con;
/*      */ 
/*      */ 
/*      */             }
/*  292 */             else if (current_proxy == null)
/*      */             {
/*  294 */               this.con = current_url.openConnection();
/*      */             }
/*      */             else
/*      */             {
/*  298 */               this.con = current_url.openConnection(current_proxy);
/*      */             }
/*      */             
/*      */ 
/*  302 */             if ((this.con instanceof HttpURLConnection))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  307 */               ((HttpURLConnection)this.con).setInstanceFollowRedirects(this.proxy == null);
/*      */             }
/*      */             
/*  310 */             Properties props = new Properties();
/*      */             
/*  312 */             ClientIDManagerImpl.getSingleton().getGenerator().generateHTTPProperties(null, props);
/*      */             
/*  314 */             String ua = props.getProperty("User-Agent");
/*      */             
/*  316 */             this.con.setRequestProperty("User-Agent", ua);
/*      */             
/*  318 */             if ((this.referrer != null) && (this.referrer.length() > 0))
/*      */             {
/*  320 */               this.con.setRequestProperty("Referer", this.referrer);
/*      */             }
/*      */             
/*  323 */             if (this.request_properties != null)
/*      */             {
/*  325 */               Iterator it = this.request_properties.entrySet().iterator();
/*      */               
/*  327 */               while (it.hasNext())
/*      */               {
/*  329 */                 Map.Entry entry = (Map.Entry)it.next();
/*      */                 
/*  331 */                 String key = (String)entry.getKey();
/*  332 */                 String value = (String)entry.getValue();
/*      */                 
/*      */ 
/*      */ 
/*  336 */                 if (!key.equalsIgnoreCase("Accept-Encoding"))
/*      */                 {
/*  338 */                   this.con.setRequestProperty(key, value);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  343 */             this.con.connect();
/*      */             
/*  345 */             String magnetURI = this.con.getHeaderField("Magnet-Uri");
/*      */             
/*  347 */             if (magnetURI != null)
/*      */             {
/*  349 */               closeConnection();
/*      */               
/*  351 */               this.url_str = magnetURI;
/*      */               
/*  353 */               runSupport();
/*      */               
/*  355 */               return;
/*      */             }
/*      */             
/*  358 */             int response = (this.con instanceof HttpURLConnection) ? ((HttpURLConnection)this.con).getResponseCode() : 200;
/*      */             
/*  360 */             if ((response == 302) || (response == 301))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  365 */               String move_to = this.con.getHeaderField("location");
/*      */               
/*  367 */               if (move_to != null)
/*      */               {
/*  369 */                 if ((!redirect_urls.contains(move_to)) && (redirect_urls.size() > 32)) {
/*      */                   break label1299;
/*      */                 }
/*      */                 
/*      */ 
/*  374 */                 redirect_urls.add(move_to);
/*      */                 
/*      */ 
/*      */                 try
/*      */                 {
/*  379 */                   URL move_to_url = new URL(move_to);
/*      */                   
/*  381 */                   boolean follow = false;
/*      */                   
/*  383 */                   if (current_plugin_proxy != null)
/*      */                   {
/*  385 */                     AEProxyFactory.PluginProxy child = current_plugin_proxy.getChildProxy("redirect", move_to_url);
/*      */                     
/*  387 */                     if (child != null)
/*      */                     {
/*      */ 
/*      */ 
/*  391 */                       this.request_properties.put("HOST", child.getURLHostRewrite() + (move_to_url.getPort() == -1 ? "" : new StringBuilder().append(":").append(move_to_url.getPort()).toString()));
/*      */                       
/*  393 */                       current_proxy = child.getProxy();
/*  394 */                       move_to_url = child.getURL();
/*      */                       
/*  396 */                       follow = true;
/*      */                     }
/*      */                   }
/*      */                   
/*  400 */                   String original_protocol = current_url.getProtocol().toLowerCase();
/*  401 */                   String new_protocol = move_to_url.getProtocol().toLowerCase();
/*      */                   
/*  403 */                   if ((follow) || (!original_protocol.equals(new_protocol)))
/*      */                   {
/*  405 */                     current_url = move_to_url;
/*      */                     try
/*      */                     {
/*  408 */                       List<String> cookies_list = (List)this.con.getHeaderFields().get("Set-cookie");
/*      */                       
/*  410 */                       List<String> cookies_set = new ArrayList();
/*      */                       
/*  412 */                       if (cookies_list != null)
/*      */                       {
/*  414 */                         for (int i = 0; i < cookies_list.size(); i++)
/*      */                         {
/*  416 */                           String[] cookie_bits = ((String)cookies_list.get(i)).split(";");
/*      */                           
/*  418 */                           if (cookie_bits.length > 0)
/*      */                           {
/*  420 */                             cookies_set.add(cookie_bits[0]);
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                       
/*  425 */                       if (cookies_set.size() > 0)
/*      */                       {
/*  427 */                         String new_cookies = "";
/*      */                         
/*  429 */                         Object obj = this.request_properties.get("Cookie");
/*      */                         
/*  431 */                         if ((obj instanceof String))
/*      */                         {
/*  433 */                           new_cookies = (String)obj;
/*      */                         }
/*      */                         
/*  436 */                         for (String s : cookies_set)
/*      */                         {
/*  438 */                           new_cookies = new_cookies + (new_cookies.length() == 0 ? "" : "; ") + s;
/*      */                         }
/*      */                         
/*  441 */                         this.request_properties.put("Cookie", new_cookies);
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {
/*  445 */                       Debug.out(e);
/*      */                     }
/*      */                     
/*  448 */                     follow_redirect = true;
/*      */                     
/*  450 */                     break;
/*      */                   }
/*      */                   
/*      */ 
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/*      */           catch (SSLException e)
/*      */           {
/*  463 */             if (connect_loop < 3)
/*      */             {
/*  465 */               String msg = Debug.getNestedExceptionMessage(e);
/*      */               
/*  467 */               boolean try_again = false;
/*      */               
/*  469 */               if (msg.contains("DH keypair"))
/*      */               {
/*  471 */                 if (!dh_hack)
/*      */                 {
/*  473 */                   dh_hack = true;
/*      */                   
/*  475 */                   try_again = true;
/*      */                 }
/*  477 */               } else if (msg.contains("internal_error"))
/*      */               {
/*  479 */                 if (!internal_error_hack)
/*      */                 {
/*  481 */                   internal_error_hack = true;
/*      */                   
/*  483 */                   try_again = true;
/*      */                 }
/*      */               }
/*      */               
/*  487 */               if (current_plugin_proxy == null)
/*      */               {
/*  489 */                 if (SESecurityManager.installServerCertificates(this.url) != null)
/*      */                 {
/*      */ 
/*      */ 
/*  493 */                   try_again = true;
/*      */                 }
/*      */               }
/*      */               
/*  497 */               if ((this.url != current_url) && (current_plugin_proxy == null) && (SESecurityManager.installServerCertificates(current_url) != null))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  503 */                 try_again = true;
/*      */               }
/*      */               
/*  506 */               if (try_again) {}
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  512 */               throw e;
/*      */             }
/*      */           }
/*      */           catch (IOException e) {
/*  516 */             if (connect_loop == 0)
/*      */             {
/*  518 */               URL retry_url = UrlUtils.getIPV4Fallback(this.url);
/*      */               
/*  520 */               if (retry_url != null)
/*      */               {
/*  522 */                 this.url = retry_url;
/*      */               }
/*      */               else
/*      */               {
/*  526 */                 throw e;
/*      */               }
/*      */             }
/*      */             
/*  530 */             if ((e instanceof UnknownHostException))
/*      */             {
/*  532 */               throw e;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  538 */       int response = (this.con instanceof HttpURLConnection) ? ((HttpURLConnection)this.con).getResponseCode() : 200;
/*  539 */       if ((!this.ignoreReponseCode) && 
/*  540 */         (response != 202) && (response != 200)) {
/*  541 */         error(response, Integer.toString(response) + ": " + ((HttpURLConnection)this.con).getResponseMessage());
/*  542 */         return;
/*      */       }
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
/*  556 */       this.filename = this.con.getHeaderField("Content-Disposition");
/*      */       
/*  558 */       if ((this.filename != null) && (this.filename.toLowerCase().matches(".*attachment.*")))
/*      */       {
/*  560 */         while (this.filename.toLowerCase().charAt(0) != 'a')
/*      */         {
/*  562 */           this.filename = this.filename.substring(1);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  568 */       Pattern p = Pattern.compile(".*filename=\\\"(.*)\\\"");
/*      */       
/*  570 */       Matcher m = null;
/*      */       
/*  572 */       if ((this.filename != null) && ((m = p.matcher(this.filename)) != null) && (m.matches()))
/*      */       {
/*  574 */         this.filename = m.group(1).trim();
/*      */       }
/*  576 */       else if ((this.filename == null) || (!this.filename.toLowerCase().startsWith("attachment")) || (this.filename.indexOf('=') == -1))
/*      */       {
/*      */ 
/*      */ 
/*  580 */         String tmp = this.url.getFile();
/*      */         
/*  582 */         if ((tmp.length() == 0) || (tmp.equals("/")))
/*      */         {
/*  584 */           this.filename = this.url.getHost();
/*      */         }
/*  586 */         else if (tmp.startsWith("?"))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  592 */           String query = tmp.toUpperCase();
/*      */           
/*  594 */           int pos = query.indexOf("XT=URN:SHA1:");
/*      */           
/*  596 */           if (pos == -1)
/*      */           {
/*  598 */             pos = query.indexOf("XT=URN:BTIH:");
/*      */           }
/*      */           
/*  601 */           if (pos != -1)
/*      */           {
/*  603 */             pos += 12;
/*      */             
/*  605 */             int p2 = query.indexOf("&", pos);
/*      */             
/*  607 */             if (p2 == -1)
/*      */             {
/*  609 */               this.filename = query.substring(pos);
/*      */             }
/*      */             else
/*      */             {
/*  613 */               this.filename = query.substring(pos, p2);
/*      */             }
/*      */           }
/*      */           else {
/*  617 */             this.filename = ("Torrent" + (Math.random() * 9.223372036854776E18D));
/*      */           }
/*      */           
/*      */ 
/*  621 */           this.filename += ".tmp";
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  626 */           while (tmp.endsWith("/"))
/*      */           {
/*  628 */             tmp = tmp.substring(0, tmp.length() - 1);
/*      */           }
/*      */           
/*  631 */           if (tmp.lastIndexOf('/') != -1)
/*      */           {
/*  633 */             tmp = tmp.substring(tmp.lastIndexOf('/') + 1);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  638 */           int param_pos = tmp.indexOf('?');
/*      */           
/*  640 */           if (param_pos != -1) {
/*  641 */             tmp = tmp.substring(0, param_pos);
/*      */           }
/*      */           
/*  644 */           this.filename = URLDecoder.decode(tmp, "UTF8");
/*      */           
/*  646 */           if (this.filename.length() == 0)
/*      */           {
/*  648 */             this.filename = ("Torrent" + (Math.random() * 9.223372036854776E18D));
/*      */           }
/*      */         }
/*      */       } else {
/*  652 */         this.filename = this.filename.substring(this.filename.indexOf('=') + 1);
/*  653 */         if ((this.filename.startsWith("\"")) && (this.filename.endsWith("\""))) {
/*  654 */           this.filename = this.filename.substring(1, this.filename.lastIndexOf('"'));
/*      */         }
/*  656 */         this.filename = URLDecoder.decode(this.filename, "UTF8");
/*      */         
/*      */ 
/*      */ 
/*  660 */         File temp = new File(this.filename);
/*  661 */         this.filename = temp.getName();
/*      */       }
/*      */       
/*  664 */       this.filename = FileUtil.convertOSSpecificChars(this.filename, false);
/*      */       
/*  666 */       this.directoryname = COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory");
/*  667 */       boolean useTorrentSave = COConfigurationManager.getBooleanParameter("Save Torrent Files");
/*      */       
/*  669 */       if (this.file_str != null)
/*      */       {
/*  671 */         File temp = new File(this.file_str);
/*      */         
/*      */ 
/*  674 */         if ((!useTorrentSave) || (this.directoryname.length() == 0))
/*      */         {
/*  676 */           if (temp.isDirectory())
/*      */           {
/*  678 */             this.directoryname = temp.getCanonicalPath();
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  683 */             this.directoryname = temp.getCanonicalFile().getParent();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  688 */         if (!temp.isDirectory())
/*      */         {
/*  690 */           this.filename = temp.getName();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  695 */       this.state = 0;
/*  696 */       notifyListener();
/*      */     } catch (MalformedURLException e) {
/*  698 */       error(0, "Exception while parsing URL '" + this.url_str + "':" + e.getMessage());
/*      */     } catch (UnknownHostException e) {
/*  700 */       error(0, "Exception while initializing download of '" + this.url + "': Unknown Host '" + e.getMessage() + "'");
/*      */     } catch (IOException ioe) {
/*  702 */       error(0, "I/O Exception while initializing download of '" + this.url + "':" + ioe.toString());
/*      */     } catch (Throwable e) {
/*  704 */       error(0, "Exception while initializing download of '" + this.url + "':" + e.toString());
/*      */     }
/*      */     
/*  707 */     if (this.state == 4)
/*      */     {
/*  709 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  713 */       final boolean[] status_reader_run = { true };
/*      */       
/*  715 */       this.state = 1;
/*      */       
/*  717 */       notifyListener();
/*      */       
/*  719 */       this.state = 2;
/*      */       
/*  721 */       notifyListener();
/*      */       
/*  723 */       if ((this.con instanceof HttpURLConnection))
/*      */       {
/*  725 */         Thread status_reader = new AEThread("TorrentDownloader:statusreader")
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  731 */             HttpURLConnection http_con = (HttpURLConnection)TorrentDownloaderImpl.this.con;
/*      */             
/*  733 */             boolean changed_status = false;
/*  734 */             String last_status = "";
/*      */             
/*  736 */             boolean sleep = false;
/*      */             
/*  738 */             long last_progress_update = SystemTime.getMonotonousTime();
/*      */             try
/*      */             {
/*      */               for (;;)
/*      */               {
/*  743 */                 if (sleep)
/*      */                 {
/*  745 */                   Thread.sleep(50L);
/*      */                   
/*  747 */                   sleep = false;
/*      */                 }
/*      */                 try
/*      */                 {
/*  751 */                   TorrentDownloaderImpl.this.this_mon.enter();
/*      */                   
/*  753 */                   if (status_reader_run[0] == 0)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  759 */                     TorrentDownloaderImpl.this.this_mon.exit(); break; } } finally { TorrentDownloaderImpl.this.this_mon.exit();
/*      */                 }
/*      */                 
/*  762 */                 String s = http_con.getResponseMessage();
/*      */                 
/*  764 */                 if (s.equals(last_status))
/*      */                 {
/*  766 */                   sleep = true;
/*      */                 }
/*      */                 else
/*      */                 {
/*  770 */                   last_status = s;
/*      */                   
/*  772 */                   String lc_s = s.toLowerCase();
/*      */                   
/*  774 */                   if (!lc_s.startsWith("error:"))
/*      */                   {
/*  776 */                     if (s.toLowerCase().contains("alive"))
/*      */                     {
/*  778 */                       if (TorrentDownloaderImpl.this.percentDone < 10)
/*      */                       {
/*  780 */                         TorrentDownloaderImpl.access$108(TorrentDownloaderImpl.this);
/*      */                       }
/*      */                     }
/*      */                     
/*  784 */                     boolean progress_update = false;
/*      */                     
/*  786 */                     int pos = s.indexOf('%');
/*      */                     
/*  788 */                     if (pos != -1)
/*      */                     {
/*      */ 
/*      */ 
/*  792 */                       for (int i = pos - 1; i >= 0; i--)
/*      */                       {
/*  794 */                         char c = s.charAt(i);
/*      */                         
/*  796 */                         if ((!Character.isDigit(c)) && (c != ' '))
/*      */                         {
/*  798 */                           i++;
/*      */                           
/*  800 */                           break;
/*      */                         }
/*      */                       }
/*      */                       try
/*      */                       {
/*  805 */                         TorrentDownloaderImpl.this.percentDone = Integer.parseInt(s.substring(i, pos).trim());
/*      */                         
/*  807 */                         progress_update = true;
/*      */                       }
/*      */                       catch (Throwable e) {}
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*  814 */                     if (lc_s.startsWith("received"))
/*      */                     {
/*  816 */                       progress_update = true;
/*      */                     }
/*      */                     
/*  819 */                     if (progress_update)
/*      */                     {
/*  821 */                       long now = SystemTime.getMonotonousTime();
/*      */                       
/*  823 */                       if (now - last_progress_update < 250L) {
/*      */                         continue;
/*      */                       }
/*      */                       
/*      */ 
/*  828 */                       last_progress_update = now;
/*      */                     }
/*      */                     
/*  831 */                     TorrentDownloaderImpl.this.setStatus(s);
/*      */                   }
/*      */                   else {
/*  834 */                     TorrentDownloaderImpl.this.error(http_con.getResponseCode(), s.substring(6));
/*      */                   }
/*      */                   
/*  837 */                   changed_status = true;
/*      */                 }
/*      */               }
/*      */               
/*      */               return;
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  845 */               if (changed_status)
/*      */               {
/*  847 */                 TorrentDownloaderImpl.this.setStatus("");
/*      */               }
/*      */             }
/*      */           }
/*  851 */         };
/*  852 */         status_reader.setDaemon(true);
/*      */         
/*  854 */         status_reader.start();
/*      */       }
/*      */       
/*  857 */       InputStream in = null;
/*  858 */       FileOutputStream fileout = null;
/*      */       try
/*      */       {
/*      */         try {
/*  862 */           in = this.con.getInputStream();
/*      */         }
/*      */         catch (FileNotFoundException e) {
/*  865 */           if (this.ignoreReponseCode)
/*      */           {
/*  867 */             if ((this.con instanceof HttpURLConnection)) {
/*  868 */               in = ((HttpURLConnection)this.con).getErrorStream();
/*      */             } else {
/*  870 */               in = null;
/*      */             }
/*      */           }
/*      */           else {
/*  874 */             throw e;
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*      */           try {
/*  880 */             this.this_mon.enter();
/*      */             
/*  882 */             status_reader_run[0] = false;
/*      */           }
/*      */           finally
/*      */           {
/*  886 */             this.this_mon.exit();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  892 */         String encoding = this.con.getHeaderField("content-encoding");
/*      */         
/*  894 */         if (encoding != null)
/*      */         {
/*  896 */           if (encoding.equalsIgnoreCase("gzip"))
/*      */           {
/*  898 */             in = new GZIPInputStream(in);
/*      */           }
/*  900 */           else if (encoding.equalsIgnoreCase("deflate"))
/*      */           {
/*  902 */             in = new InflaterInputStream(in);
/*      */           }
/*      */         }
/*      */         
/*  906 */         if (this.state != 4)
/*      */         {
/*  908 */           this.file = new File(this.directoryname, this.filename);
/*      */           
/*  910 */           boolean useTempFile = this.file.exists();
/*  911 */           if (!useTempFile) {
/*      */             try {
/*  913 */               this.file.createNewFile();
/*  914 */               useTempFile = !this.file.exists();
/*      */             } catch (Throwable t) {
/*  916 */               useTempFile = true;
/*      */             }
/*      */           }
/*      */           
/*  920 */           if (useTempFile) {
/*  921 */             this.file = File.createTempFile("AZU", ".torrent", new File(this.directoryname));
/*      */             
/*  923 */             this.file.createNewFile();
/*      */           }
/*      */           
/*  926 */           fileout = new FileOutputStream(this.file, false);
/*      */           
/*  928 */           this.bufBytes = 0;
/*      */           
/*  930 */           int size = (int)UrlUtils.getContentLength(this.con);
/*      */           
/*  932 */           this.percentDone = -1;
/*      */           do
/*      */           {
/*  935 */             if (this.cancel) {
/*      */               break;
/*      */             }
/*      */             try
/*      */             {
/*  940 */               this.bufBytes = in.read(this.buf);
/*      */               
/*  942 */               this.readTotal += this.bufBytes;
/*      */               
/*  944 */               if (size > 0) {
/*  945 */                 this.percentDone = (100 * this.readTotal / size);
/*      */               }
/*      */               
/*  948 */               notifyListener();
/*      */             }
/*      */             catch (IOException e) {}
/*      */             
/*      */ 
/*  953 */             if (this.bufBytes > 0) {
/*  954 */               fileout.write(this.buf, 0, this.bufBytes);
/*      */             }
/*  956 */           } while (this.bufBytes > 0);
/*      */           
/*  958 */           in.close();
/*      */           
/*  960 */           fileout.flush();
/*      */           
/*  962 */           fileout.close();
/*      */           
/*  964 */           if (this.cancel) {
/*  965 */             this.state = 6;
/*  966 */             if (this.deleteFileOnCancel) {
/*  967 */               cleanUpFile();
/*      */             }
/*      */           } else {
/*  970 */             if (this.readTotal <= 0) {
/*  971 */               error(0, "No data contained in '" + this.url.toString() + "'"); return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*  979 */               if (!this.filename.toLowerCase().endsWith(".torrent"))
/*      */               {
/*  981 */                 TOTorrent torrent = TorrentUtils.readFromFile(this.file, false);
/*      */                 
/*  983 */                 String name = TorrentUtils.getLocalisedName(torrent) + ".torrent";
/*      */                 
/*  985 */                 File new_file = new File(this.directoryname, name);
/*      */                 
/*  987 */                 if (this.file.renameTo(new_file))
/*      */                 {
/*  989 */                   this.filename = name;
/*      */                   
/*  991 */                   this.file = new_file;
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  996 */               boolean is_vuze_file = false;
/*      */               try
/*      */               {
/*  999 */                 if (this.filename.toLowerCase().endsWith(".vuze"))
/*      */                 {
/* 1001 */                   is_vuze_file = true;
/*      */ 
/*      */ 
/*      */                 }
/* 1005 */                 else if (VuzeFileHandler.getSingleton().loadVuzeFile(this.file) != null)
/*      */                 {
/* 1007 */                   is_vuze_file = true;
/*      */                   
/* 1009 */                   String name = this.filename + ".vuze";
/*      */                   
/* 1011 */                   File new_file = new File(this.directoryname, name);
/*      */                   
/* 1013 */                   if (this.file.renameTo(new_file))
/*      */                   {
/* 1015 */                     this.filename = name;
/*      */                     
/* 1017 */                     this.file = new_file;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Throwable f) {}
/*      */               
/*      */ 
/* 1024 */               if (!is_vuze_file)
/*      */               {
/* 1026 */                 if (!RSSUtils.isRSSFeed(this.file))
/*      */                 {
/* 1028 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1035 */             if (this.proxy == null)
/*      */             {
/* 1037 */               TorrentUtils.setObtainedFrom(this.file, this.original_url);
/*      */             }
/*      */             
/* 1040 */             this.state = 3;
/*      */           }
/* 1042 */           notifyListener();
/*      */         }
/*      */       }
/*      */       finally {
/* 1046 */         if (in != null) {
/*      */           try {
/* 1048 */             in.close();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/* 1052 */         if (fileout != null)
/*      */           try {
/* 1054 */             fileout.close();
/*      */           } catch (Throwable e) {}
/*      */       }
/*      */       String url_log_string;
/*      */       String log_msg;
/*      */       return;
/*      */     } catch (Throwable e) {
/* 1061 */       url_log_string = this.url_str.toString().replaceAll("\\Q&pause_on_error=true\\E", "");
/*      */       
/* 1063 */       log_msg = MessageText.getString("torrentdownload.error.dl_fail", new String[] { url_log_string, this.file == null ? this.filename : this.file.getAbsolutePath(), e.getMessage() });
/*      */       
/*      */ 
/*      */ 
/* 1067 */       if (!this.cancel)
/*      */       {
/* 1069 */         Debug.out(log_msg);
/*      */       }
/*      */       
/* 1072 */       error(0, log_msg);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean equals(Object obj)
/*      */   {
/* 1079 */     if (this == obj)
/*      */     {
/* 1081 */       return true;
/*      */     }
/*      */     
/* 1084 */     if ((obj instanceof TorrentDownloaderImpl))
/*      */     {
/* 1086 */       TorrentDownloaderImpl other = (TorrentDownloaderImpl)obj;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1091 */       if (other.original_url.equals(this.original_url))
/*      */       {
/* 1093 */         File other_file = other.getFile();
/* 1094 */         File this_file = this.file;
/*      */         
/* 1096 */         if (other_file == this_file)
/*      */         {
/* 1098 */           return true;
/*      */         }
/*      */         
/* 1101 */         if ((other_file == null) || (this_file == null))
/*      */         {
/* 1103 */           return false;
/*      */         }
/*      */         
/* 1106 */         return other_file.getAbsolutePath().equals(this_file.getAbsolutePath());
/*      */       }
/*      */       
/*      */ 
/* 1110 */       return false;
/*      */     }
/*      */     
/* 1113 */     return false;
/*      */   }
/*      */   
/*      */   public int hashCode()
/*      */   {
/* 1118 */     return this.original_url.hashCode();
/*      */   }
/*      */   
/*      */   public String getError()
/*      */   {
/* 1123 */     return this.error;
/*      */   }
/*      */   
/*      */   public void setError(int errCode, String err) {
/* 1127 */     this.error = err;
/* 1128 */     this.errCode = errCode;
/*      */   }
/*      */   
/*      */   public int getErrorCode() {
/* 1132 */     return this.errCode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setStatus(String str)
/*      */   {
/* 1139 */     this.status = str;
/* 1140 */     notifyListener();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStatus()
/*      */   {
/* 1146 */     return this.status;
/*      */   }
/*      */   
/*      */   public File getFile() {
/* 1150 */     if ((!isAlive()) || (this.file == null))
/* 1151 */       this.file = new File(this.directoryname, this.filename);
/* 1152 */     return this.file;
/*      */   }
/*      */   
/*      */   public int getPercentDone() {
/* 1156 */     return this.percentDone;
/*      */   }
/*      */   
/*      */   public int getDownloadState() {
/* 1160 */     return this.state;
/*      */   }
/*      */   
/*      */   public void setDownloadState(int state) {
/* 1164 */     this.state = state;
/*      */   }
/*      */   
/*      */   public String getURL() {
/* 1168 */     return this.url.toString();
/*      */   }
/*      */   
/*      */   public void cancel() {
/* 1172 */     this.cancel = true;
/* 1173 */     closeConnection();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void closeConnection()
/*      */   {
/* 1179 */     if ((this.con instanceof MagnetConnection)) {
/* 1180 */       ((MagnetConnection)this.con).disconnect();
/*      */     }
/* 1182 */     else if ((this.con instanceof MagnetConnection2)) {
/* 1183 */       ((MagnetConnection2)this.con).disconnect();
/*      */     }
/* 1185 */     else if ((this.con instanceof HttpURLConnection)) {
/* 1186 */       ((HttpURLConnection)this.con).disconnect();
/*      */     }
/*      */   }
/*      */   
/*      */   public void setDownloadPath(String path, String file) {
/* 1191 */     if (!isAlive()) {
/* 1192 */       if (path != null)
/* 1193 */         this.directoryname = path;
/* 1194 */       if (file != null) {
/* 1195 */         this.filename = file;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTotalRead()
/*      */   {
/* 1203 */     return this.readTotal;
/*      */   }
/*      */   
/*      */   public byte[] getLastReadBytes() {
/* 1207 */     if (this.bufBytes <= 0) {
/* 1208 */       return new byte[0];
/*      */     }
/* 1210 */     byte[] bytes = new byte[this.bufBytes];
/* 1211 */     System.arraycopy(this.buf, 0, bytes, 0, this.bufBytes);
/* 1212 */     return bytes;
/*      */   }
/*      */   
/*      */   public int getLastReadCount() {
/* 1216 */     return this.bufBytes;
/*      */   }
/*      */   
/*      */   public void setDeleteFileOnCancel(boolean deleteFileOnCancel) {
/* 1220 */     this.deleteFileOnCancel = deleteFileOnCancel;
/*      */   }
/*      */   
/*      */   public boolean getDeleteFileOnCancel() {
/* 1224 */     return this.deleteFileOnCancel;
/*      */   }
/*      */   
/*      */   public boolean isIgnoreReponseCode() {
/* 1228 */     return this.ignoreReponseCode;
/*      */   }
/*      */   
/*      */   public void setIgnoreReponseCode(boolean ignoreReponseCode) {
/* 1232 */     this.ignoreReponseCode = ignoreReponseCode;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrentdownloader/impl/TorrentDownloaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */