/*      */ package org.gudy.azureus2.core3.util;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.Socket;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.URLDecoder;
/*      */ import java.net.URLEncoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.net.ssl.HttpsURLConnection;
/*      */ import javax.net.ssl.SSLContext;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLSocket;
/*      */ import javax.net.ssl.SSLSocketFactory;
/*      */ import javax.net.ssl.TrustManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLList;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAnnounceURLListSet;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourceuploader.ResourceUploader;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.bouncycastle.util.encoders.Base64;
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
/*      */ public class UrlUtils
/*      */ {
/*   63 */   private static final ThreadPool connect_pool = new ThreadPool("URLConnectWithTimeout", 8, true);
/*      */   
/*      */   static {
/*   66 */     connect_pool.setWarnWhenFull();
/*      */   }
/*      */   
/*   69 */   private static Pattern patMagnetHashFinder = Pattern.compile("xt=urn:(?:btih|sha1):([^&]+)");
/*      */   
/*   71 */   private static final String[] prefixes = { "http://", "https://", "ftp://", "dht://", "magnet:?", "magnet://?", "maggot://" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int MAGNETURL_STARTS_AT = 3;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   82 */   private static final Object[] XMLescapes = { { "&", "&amp;" }, { ">", "&gt;" }, { "<", "&lt;" }, { "\"", "&quot;" }, { "'", "&apos;" } };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, String> decodeArgs(String args)
/*      */   {
/*   94 */     Map<String, String> result = new HashMap();
/*      */     
/*   96 */     String[] bits = (args.startsWith("?") ? args.substring(1) : args).split("&");
/*      */     
/*   98 */     for (String bit : bits)
/*      */     {
/*  100 */       String[] temp = bit.split("=", 2);
/*      */       
/*  102 */       if (temp.length == 2)
/*      */       {
/*  104 */         String lhs = temp[0].toLowerCase(Locale.US);
/*      */         
/*  106 */         String rhs = decode(temp[1]);
/*      */         
/*  108 */         result.put(lhs, rhs);
/*      */       }
/*      */       else
/*      */       {
/*  112 */         result.put("", decode(temp[0]));
/*      */       }
/*      */     }
/*      */     
/*  116 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(byte[] hash)
/*      */   {
/*  123 */     return "magnet:?xt=urn:btih:" + Base32.encode(hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(byte[] hash, String name, String[] networks)
/*      */   {
/*  132 */     String magnet_uri = getMagnetURI(hash);
/*      */     
/*  134 */     magnet_uri = magnet_uri + encodeName(name);
/*      */     
/*  136 */     magnet_uri = magnet_uri + encodeNetworks(networks);
/*      */     
/*  138 */     return magnet_uri;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String encodeName(String name)
/*      */   {
/*  145 */     if (name == null)
/*      */     {
/*  147 */       return "";
/*      */     }
/*      */     
/*      */ 
/*  151 */     return "&dn=" + encode(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static String encodeNetworks(String[] networks)
/*      */   {
/*  159 */     String net_str = "";
/*      */     
/*  161 */     if ((networks != null) && (networks.length > 0))
/*      */     {
/*  163 */       for (String net : networks)
/*      */       {
/*  165 */         if ((net == "Public") && (networks.length == 1)) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*  170 */         net_str = net_str + "&net=" + net;
/*      */       }
/*      */     }
/*      */     
/*  174 */     return net_str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static byte[] extractHash(String magnet_uri)
/*      */   {
/*  181 */     magnet_uri = magnet_uri.toLowerCase(Locale.US);
/*      */     
/*  183 */     int pos = magnet_uri.indexOf("btih:");
/*      */     
/*  185 */     if (pos > 0)
/*      */     {
/*  187 */       magnet_uri = magnet_uri.substring(pos + 5);
/*      */       
/*  189 */       pos = magnet_uri.indexOf('&');
/*      */       
/*  191 */       if (pos != -1)
/*      */       {
/*  193 */         magnet_uri = magnet_uri.substring(0, pos);
/*      */       }
/*      */       
/*  196 */       return decodeSHA1Hash(magnet_uri);
/*      */     }
/*      */     
/*  199 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Set<String> extractNetworks(String[] magnet_uri)
/*      */   {
/*  206 */     String magnet_uri_in = magnet_uri[0];
/*      */     
/*  208 */     Set<String> result = new HashSet();
/*      */     
/*  210 */     int pos = magnet_uri_in.indexOf('?');
/*      */     
/*  212 */     if (pos != -1)
/*      */     {
/*  214 */       String magnet_uri_out = magnet_uri_in.substring(0, pos + 1);
/*      */       
/*  216 */       String[] bits = magnet_uri_in.substring(pos + 1).split("&");
/*      */       
/*  218 */       for (String bit : bits)
/*      */       {
/*  220 */         String[] temp = bit.split("=", 2);
/*      */         
/*  222 */         boolean remove = false;
/*      */         
/*  224 */         if (temp.length == 2)
/*      */         {
/*  226 */           String lhs = temp[0];
/*      */           
/*  228 */           if (lhs.equalsIgnoreCase("net"))
/*      */           {
/*  230 */             String rhs = decode(temp[1]);
/*      */             
/*  232 */             result.add(AENetworkClassifier.internalise(rhs));
/*      */             
/*  234 */             remove = true;
/*      */           }
/*      */         }
/*      */         
/*  238 */         if (!remove)
/*      */         {
/*  240 */           if (!magnet_uri_out.endsWith("?"))
/*      */           {
/*  242 */             magnet_uri_out = magnet_uri_out + "&";
/*      */           }
/*      */           
/*  245 */           magnet_uri_out = magnet_uri_out + bit;
/*      */         }
/*      */       }
/*      */       
/*  249 */       if (result.size() > 0)
/*      */       {
/*  251 */         magnet_uri[0] = magnet_uri_out;
/*      */       }
/*      */     }
/*      */     
/*  255 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(Download download)
/*      */   {
/*  262 */     return getMagnetURI(PluginCoreUtils.unwrap(download));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(Download download, int max_name_len)
/*      */   {
/*  270 */     return getMagnetURI(PluginCoreUtils.unwrap(download), max_name_len);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(DownloadManager dm)
/*      */   {
/*  277 */     return getMagnetURI(dm, Integer.MAX_VALUE);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(DownloadManager dm, int max_name_len)
/*      */   {
/*  285 */     if (dm == null)
/*      */     {
/*  287 */       return null;
/*      */     }
/*      */     
/*  290 */     TOTorrent to_torrent = dm.getTorrent();
/*      */     
/*  292 */     if (to_torrent == null)
/*      */     {
/*  294 */       return null;
/*      */     }
/*      */     
/*  297 */     String name = dm.getDisplayName();
/*      */     
/*  299 */     if (name.length() > max_name_len)
/*      */     {
/*  301 */       name = name.substring(0, max_name_len - 3) + "...";
/*      */     }
/*      */     
/*  304 */     String magnet_uri = getMagnetURI(name, PluginCoreUtils.wrap(to_torrent));
/*      */     
/*  306 */     String[] networks = dm.getDownloadState().getNetworks();
/*      */     
/*  308 */     magnet_uri = magnet_uri + encodeNetworks(networks);
/*      */     
/*  310 */     return magnet_uri;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getMagnetURI(String name, Torrent torrent)
/*      */   {
/*  318 */     String magnet_str = getMagnetURI(torrent.getHash());
/*      */     
/*  320 */     magnet_str = magnet_str + encodeName(name);
/*      */     
/*  322 */     List<String> tracker_urls = new ArrayList();
/*      */     
/*  324 */     URL announce_url = torrent.getAnnounceURL();
/*      */     
/*  326 */     if (announce_url != null)
/*      */     {
/*  328 */       if (!TorrentUtils.isDecentralised(announce_url))
/*      */       {
/*  330 */         tracker_urls.add(announce_url.toExternalForm());
/*      */       }
/*      */     }
/*      */     
/*  334 */     TorrentAnnounceURLList list = torrent.getAnnounceURLList();
/*      */     
/*  336 */     TorrentAnnounceURLListSet[] sets = list.getSets();
/*      */     
/*  338 */     for (TorrentAnnounceURLListSet set : sets)
/*      */     {
/*  340 */       URL[] set_urls = set.getURLs();
/*      */       
/*  342 */       if (set_urls.length > 0)
/*      */       {
/*  344 */         URL set_url = set_urls[0];
/*      */         
/*  346 */         if (!TorrentUtils.isDecentralised(set_url))
/*      */         {
/*  348 */           String str = set_url.toExternalForm();
/*      */           
/*  350 */           if (!tracker_urls.contains(str))
/*      */           {
/*  352 */             tracker_urls.add(str);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  358 */     for (String str : tracker_urls)
/*      */     {
/*  360 */       magnet_str = magnet_str + "&tr=" + encode(str);
/*      */     }
/*      */     
/*  363 */     List<String> ws_urls = new ArrayList();
/*      */     
/*  365 */     Object obj = torrent.getAdditionalProperty("url-list");
/*      */     
/*  367 */     if ((obj instanceof byte[])) {
/*      */       try
/*      */       {
/*  370 */         ws_urls.add(new URL(new String((byte[])obj, "UTF-8")).toExternalForm());
/*      */ 
/*      */       }
/*      */       catch (Throwable e) {}
/*  374 */     } else if ((obj instanceof List))
/*      */     {
/*  376 */       for (Object o : (List)obj) {
/*      */         try
/*      */         {
/*  379 */           if ((o instanceof byte[])) {
/*  380 */             ws_urls.add(new URL(new String((byte[])o, "UTF-8")).toExternalForm());
/*  381 */           } else if ((o instanceof String)) {
/*  382 */             ws_urls.add(new URL((String)o).toExternalForm());
/*      */           }
/*      */           
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*  388 */     } else if ((obj instanceof String)) {
/*      */       try {
/*  390 */         ws_urls.add(new URL((String)obj).toExternalForm());
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*  395 */     for (String str : ws_urls)
/*      */     {
/*  397 */       magnet_str = magnet_str + "&ws=" + encode(str);
/*      */     }
/*      */     
/*  400 */     return magnet_str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String normaliseMagnetURI(String base_hash)
/*      */   {
/*  412 */     byte[] hash = decodeSHA1Hash(base_hash);
/*      */     
/*  414 */     if (hash != null)
/*      */     {
/*  416 */       return getMagnetURI(hash);
/*      */     }
/*      */     
/*  419 */     return null;
/*      */   }
/*      */   
/*      */   public static byte[] getHashFromMagnetURI(String magnetURI) {
/*  423 */     if (magnetURI == null) {
/*  424 */       return null;
/*      */     }
/*  426 */     Matcher matcher = patMagnetHashFinder.matcher(magnetURI);
/*  427 */     if (matcher.find()) {
/*  428 */       return decodeSHA1Hash(matcher.group(1));
/*      */     }
/*  430 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static byte[] decodeSHA1Hash(String str)
/*      */   {
/*  438 */     if (str == null)
/*      */     {
/*  440 */       return null;
/*      */     }
/*      */     
/*  443 */     str = str.trim();
/*      */     
/*  445 */     byte[] hash = null;
/*      */     try
/*      */     {
/*  448 */       if (str.length() == 40)
/*      */       {
/*  450 */         hash = ByteFormatter.decodeString(str);
/*      */       }
/*  452 */       else if (str.length() == 32)
/*      */       {
/*  454 */         hash = Base32.decode(str);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  459 */     if (hash != null)
/*      */     {
/*  461 */       if (hash.length != 20)
/*      */       {
/*  463 */         hash = null;
/*      */       }
/*      */     }
/*      */     
/*  467 */     return hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isURL(String sURL)
/*      */   {
/*  478 */     return parseTextForURL(sURL, true) != null;
/*      */   }
/*      */   
/*      */   public static boolean isURL(String sURL, boolean bGuess) {
/*  482 */     return parseTextForURL(sURL, true, bGuess) != null;
/*      */   }
/*      */   
/*      */   public static String parseTextForURL(String text, boolean accept_magnets) {
/*  486 */     return parseTextForURL(text, accept_magnets, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getURL(String text)
/*      */   {
/*  493 */     return parseTextForURL(text, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean isInternalProtocol(String url)
/*      */   {
/*  500 */     url = url.toLowerCase();
/*      */     
/*  502 */     return (url.startsWith("magnet:")) || (url.startsWith("chat:")) || (url.startsWith("azplug:")) || (url.startsWith("vuze:")) || (url.startsWith("tor:"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String parseTextForURL(String text, boolean accept_magnets, boolean guess)
/*      */   {
/*  513 */     if ((text == null) || (text.length() < 5)) {
/*  514 */       return null;
/*      */     }
/*      */     
/*  517 */     text = text.trim();
/*      */     
/*  519 */     if (text.startsWith("azplug:"))
/*      */     {
/*  521 */       return text;
/*      */     }
/*      */     
/*  524 */     if (text.startsWith("chat:"))
/*      */     {
/*  526 */       return "azplug:?id=azbuddy&arg=" + encode(text);
/*      */     }
/*      */     
/*  529 */     if (text.startsWith("tor:"))
/*      */     {
/*  531 */       String href = parseTextForURL(text.substring(4), false, false);
/*  532 */       if (href != null) {
/*  533 */         return "tor:" + href;
/*      */       }
/*      */     }
/*      */     
/*  537 */     String href = parseHTMLforURL(text);
/*  538 */     if (href != null) {
/*  539 */       return href;
/*      */     }
/*      */     try
/*      */     {
/*  543 */       text = text.trim();
/*  544 */       text = decodeIfNeeded(text);
/*      */     }
/*      */     catch (Exception e) {}
/*      */     
/*      */     String textLower;
/*      */     
/*      */     try
/*      */     {
/*  552 */       textLower = text.toLowerCase();
/*      */     } catch (Throwable e) {
/*  554 */       textLower = text;
/*      */     }
/*  556 */     int max = accept_magnets ? prefixes.length : 3;
/*  557 */     int end = -1;
/*  558 */     int start = textLower.length();
/*  559 */     String strURL = null;
/*  560 */     for (int i = 0; i < max; i++) {
/*  561 */       int testBegin = textLower.indexOf(prefixes[i]);
/*  562 */       if ((testBegin >= 0) && (testBegin < start)) {
/*  563 */         end = text.indexOf("\n", testBegin + prefixes[i].length());
/*  564 */         String strURLTest = end >= 0 ? text.substring(testBegin, end - 1) : text.substring(testBegin);
/*      */         try
/*      */         {
/*  567 */           URL parsedURL = new URL(strURLTest);
/*  568 */           strURL = parsedURL.toExternalForm();
/*      */         } catch (MalformedURLException e1) {
/*  570 */           e1.printStackTrace();
/*  571 */           if (i >= 3) {
/*  572 */             strURL = strURLTest;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  577 */     if (strURL != null) {
/*  578 */       return strURL;
/*      */     }
/*      */     
/*  581 */     if (new File(text).exists()) {
/*  582 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  588 */       URL u = new URL("http://" + text);
/*      */       
/*  590 */       String host = u.getHost();
/*      */       
/*  592 */       if ((host != null) && (AENetworkClassifier.categoriseAddress(host) != "Public"))
/*      */       {
/*  594 */         return u.toExternalForm();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  599 */     if ((accept_magnets) && ((text.startsWith("bc://")) || (text.startsWith("bctp://"))))
/*      */     {
/*  601 */       return parseTextForMagnets(text);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  606 */     String text_prefix = text;
/*  607 */     String text_suffix = "";
/*      */     
/*  609 */     int a_pos = text_prefix.indexOf('?');
/*  610 */     if (a_pos == -1) {
/*  611 */       a_pos = text_prefix.indexOf('&');
/*      */     }
/*  613 */     if (a_pos != -1) {
/*  614 */       String args = text_prefix.substring(a_pos + 1).trim();
/*  615 */       if (args.contains("=")) {
/*  616 */         int s_pos = args.indexOf(' ');
/*  617 */         if (s_pos != -1) {
/*  618 */           args = args.substring(0, s_pos);
/*      */         }
/*  620 */         text_prefix = text_prefix.substring(0, a_pos);
/*  621 */         text_suffix = "&" + args;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  626 */     if (accept_magnets)
/*      */     {
/*  628 */       if (text_prefix.matches("^[a-fA-F0-9]{40}$"))
/*      */       {
/*      */ 
/*  631 */         byte[] infohash = ByteFormatter.decodeString(text_prefix.toUpperCase());
/*      */         
/*  633 */         return "magnet:?xt=urn:btih:" + Base32.encode(infohash) + text_suffix;
/*      */       }
/*      */       
/*  636 */       String temp_text = text_prefix.replaceAll("\\s+", "");
/*      */       
/*  638 */       if (temp_text.matches("^[a-fA-F0-9]{40}$"))
/*      */       {
/*      */ 
/*  641 */         byte[] infohash = ByteFormatter.decodeString(temp_text.toUpperCase());
/*      */         
/*  643 */         return "magnet:?xt=urn:btih:" + Base32.encode(infohash) + text_suffix;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  648 */     if ((accept_magnets) && (text_prefix.matches("^[a-zA-Z2-7]{32}$"))) {
/*  649 */       return "magnet:?xt=urn:btih:" + text_prefix + text_suffix;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  655 */     if ((accept_magnets) && (guess)) {
/*  656 */       Pattern pattern = Pattern.compile("[^a-zA-Z2-7][a-zA-Z2-7]{32}[^a-zA-Z2-7]");
/*  657 */       Matcher matcher = pattern.matcher(text);
/*  658 */       if (matcher.find()) {
/*  659 */         String hash = text.substring(matcher.start() + 1, matcher.start() + 33);
/*  660 */         return "magnet:?xt=urn:btih:" + hash;
/*      */       }
/*      */       
/*  663 */       pattern = Pattern.compile("[^a-fA-F0-9][a-fA-F0-9]{40}[^a-fA-F0-9]");
/*  664 */       matcher = pattern.matcher(text);
/*  665 */       if (matcher.find()) {
/*  666 */         String hash = text.substring(matcher.start() + 1, matcher.start() + 41);
/*      */         
/*  668 */         byte[] infohash = ByteFormatter.decodeString(hash.toUpperCase());
/*      */         
/*  670 */         return "magnet:?xt=urn:btih:" + Base32.encode(infohash);
/*      */       }
/*      */     }
/*      */     
/*  674 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String parseTextForMagnets(String text)
/*      */   {
/*  681 */     if ((text.startsWith("magnet:")) || (text.startsWith("maggot:"))) {
/*  682 */       return text;
/*      */     }
/*      */     
/*      */ 
/*  686 */     if (text.matches("^[a-fA-F0-9]{40}$"))
/*      */     {
/*  688 */       byte[] infohash = ByteFormatter.decodeString(text.toUpperCase());
/*      */       
/*  690 */       return "magnet:?xt=urn:btih:" + Base32.encode(infohash);
/*      */     }
/*      */     
/*  693 */     String temp_text = text.replaceAll("\\s+", "");
/*  694 */     if (temp_text.matches("^[a-fA-F0-9]{40}$"))
/*      */     {
/*  696 */       byte[] infohash = ByteFormatter.decodeString(temp_text.toUpperCase());
/*      */       
/*  698 */       return "magnet:?xt=urn:btih:" + Base32.encode(infohash);
/*      */     }
/*      */     
/*      */ 
/*  702 */     if (text.matches("^[a-zA-Z2-7]{32}$")) {
/*  703 */       return "magnet:?xt=urn:btih:" + text;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  709 */     Pattern pattern = Pattern.compile("magnet:\\?[a-z%0-9=_:&.]+", 2);
/*  710 */     Matcher matcher = pattern.matcher(text);
/*  711 */     if (matcher.find()) {
/*  712 */       return matcher.group();
/*      */     }
/*      */     
/*  715 */     pattern = Pattern.compile("maggot://[a-z0-9]+:[a-z0-9]", 2);
/*  716 */     matcher = pattern.matcher(text);
/*  717 */     if (matcher.find()) {
/*  718 */       return matcher.group();
/*      */     }
/*      */     
/*  721 */     pattern = Pattern.compile("bc://bt/([a-z0-9=\\+/]+)", 2);
/*  722 */     matcher = pattern.matcher(text.replaceAll(" ", "+"));
/*  723 */     if (matcher.find()) {
/*  724 */       String base64 = matcher.group(1);
/*  725 */       byte[] decode = Base64.decode(base64);
/*  726 */       if ((decode != null) && (decode.length > 0)) {
/*      */         try
/*      */         {
/*  729 */           String decodeString = new String(decode, "utf8");
/*  730 */           pattern = Pattern.compile("AA.*/(.*)/ZZ", 2);
/*  731 */           matcher = pattern.matcher(decodeString);
/*  732 */           if (matcher.find()) {
/*  733 */             String hash = matcher.group(1);
/*  734 */             String magnet = parseTextForMagnets(hash);
/*  735 */             if (magnet != null) {
/*  736 */               pattern = Pattern.compile("AA/(.*)/[0-9]+", 2);
/*  737 */               matcher = pattern.matcher(decodeString);
/*  738 */               if (matcher.find()) {
/*  739 */                 String name = matcher.group(1);
/*  740 */                 return magnet + "&dn=" + encode(name);
/*      */               }
/*  742 */               return magnet;
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (UnsupportedEncodingException e) {}
/*      */       }
/*      */     }
/*      */     
/*  750 */     pattern = Pattern.compile("bctp://task/(.*)", 2);
/*  751 */     matcher = pattern.matcher(text);
/*  752 */     if (matcher.find())
/*      */     {
/*  754 */       String decodeString = matcher.group(1);
/*  755 */       String magnet = parseTextForMagnets(decodeString);
/*  756 */       if (magnet != null) {
/*  757 */         pattern = Pattern.compile("(.*)/[0-9]+", 2);
/*  758 */         matcher = pattern.matcher(decodeString);
/*  759 */         if (matcher.find()) {
/*  760 */           String name = matcher.group(1);
/*  761 */           return magnet + "&dn=" + encode(name);
/*      */         }
/*  763 */         return magnet;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  769 */     text = "!" + text + "!";
/*  770 */     pattern = Pattern.compile("[^a-zA-Z2-7][a-zA-Z2-7]{32}[^a-zA-Z2-7]");
/*  771 */     matcher = pattern.matcher(text);
/*  772 */     if (matcher.find()) {
/*  773 */       String hash = text.substring(matcher.start() + 1, matcher.start() + 33);
/*  774 */       return "magnet:?xt=urn:btih:" + hash;
/*      */     }
/*      */     
/*  777 */     pattern = Pattern.compile("[^a-fA-F0-9][a-fA-F0-9]{40}[^a-fA-F0-9]");
/*  778 */     matcher = pattern.matcher(text);
/*  779 */     if (matcher.find()) {
/*  780 */       String hash = text.substring(matcher.start() + 1, matcher.start() + 41);
/*      */       
/*  782 */       byte[] infohash = ByteFormatter.decodeString(hash.toUpperCase());
/*      */       
/*  784 */       return "magnet:?xt=urn:btih:" + Base32.encode(infohash);
/*      */     }
/*      */     
/*      */ 
/*  788 */     return null;
/*      */   }
/*      */   
/*      */   private static String parseHTMLforURL(String text) {
/*  792 */     if (text == null) {
/*  793 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  801 */     Pattern pat = Pattern.compile("<.*a\\s++.*href=\"?([^\\'\"\\s>]++).*", 2);
/*      */     
/*  803 */     Matcher m = pat.matcher(text);
/*  804 */     if (m.find()) {
/*  805 */       String sURL = m.group(1);
/*      */       try {
/*  807 */         sURL = decodeIfNeeded(sURL);
/*      */       }
/*      */       catch (Exception e) {}
/*      */       
/*      */ 
/*  812 */       return sURL;
/*      */     }
/*      */     
/*  815 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String encode(String s)
/*      */   {
/*  826 */     if (s == null) {
/*  827 */       return "";
/*      */     }
/*      */     try {
/*  830 */       return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
/*      */     } catch (UnsupportedEncodingException e) {}
/*  832 */     return URLEncoder.encode(s).replaceAll("\\+", "%20");
/*      */   }
/*      */   
/*      */   public static String decode(String s)
/*      */   {
/*  837 */     if (s == null) {
/*  838 */       return "";
/*      */     }
/*      */     try
/*      */     {
/*  842 */       return URLDecoder.decode(s, "UTF-8");
/*      */ 
/*      */     }
/*      */     catch (IllegalArgumentException e)
/*      */     {
/*      */ 
/*  848 */       int pos = s.lastIndexOf("%");
/*      */       
/*  850 */       if (pos >= s.length() - 2)
/*      */       {
/*  852 */         return URLDecoder.decode(s.substring(0, pos), "UTF-8");
/*      */       }
/*      */       
/*  855 */       throw e;
/*      */     }
/*      */     catch (UnsupportedEncodingException e) {}
/*      */     
/*  859 */     return URLDecoder.decode(s);
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
/*      */   public static String decodeIfNeeded(String s)
/*      */   {
/*  874 */     if (s == null)
/*      */     {
/*  876 */       return "";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  885 */       int q_pos = s.indexOf('?');
/*  886 */       int a_pos = s.indexOf('&');
/*      */       
/*  888 */       if ((q_pos == -1) && (a_pos == -1))
/*      */       {
/*  890 */         return decode(s);
/*      */       }
/*      */       
/*  893 */       int start = Math.min(q_pos, a_pos);
/*      */       
/*  895 */       return decode(s.substring(0, start)) + s.substring(start);
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  899 */     return s;
/*      */   }
/*      */   
/*      */   public static String escapeXML(String s)
/*      */   {
/*  904 */     if (s == null) {
/*  905 */       return "";
/*      */     }
/*  907 */     String ret = s;
/*  908 */     for (int i = 0; i < XMLescapes.length; i++) {
/*  909 */       String[] escapeEntry = (String[])XMLescapes[i];
/*  910 */       ret = ret.replaceAll(escapeEntry[0], escapeEntry[1]);
/*      */     }
/*  912 */     return ret;
/*      */   }
/*      */   
/*      */   public static String unescapeXML(String s) {
/*  916 */     if (s == null) {
/*  917 */       return "";
/*      */     }
/*  919 */     String ret = s;
/*  920 */     for (int i = 0; i < XMLescapes.length; i++) {
/*  921 */       String[] escapeEntry = (String[])XMLescapes[i];
/*  922 */       ret = ret.replaceAll(escapeEntry[1], escapeEntry[0]);
/*      */     }
/*  924 */     return ret;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String convertIPV6Host(String host)
/*      */   {
/*  931 */     if (host.indexOf(':') != -1)
/*      */     {
/*  933 */       int zone_index = host.indexOf('%');
/*      */       
/*  935 */       if (zone_index != -1)
/*      */       {
/*  937 */         host = host.substring(0, zone_index) + encode(host.substring(zone_index));
/*      */       }
/*      */       
/*  940 */       return "[" + host + "]";
/*      */     }
/*      */     
/*  943 */     return host;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String expandIPV6Host(String host)
/*      */   {
/*  950 */     if (host.indexOf(':') != -1) {
/*      */       try
/*      */       {
/*  953 */         return InetAddress.getByAddress(InetAddress.getByName(host).getAddress()).getHostAddress();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  957 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  961 */     return host;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void connectWithTimeout(URLConnection connection, long connect_timeout)
/*      */     throws IOException
/*      */   {
/*  971 */     connectWithTimeouts(connection, connect_timeout, -1L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void connectWithTimeouts(URLConnection connection, long connect_timeout, long read_timeout)
/*      */     throws IOException
/*      */   {
/*  982 */     if (connect_timeout != -1L)
/*      */     {
/*  984 */       connection.setConnectTimeout((int)connect_timeout);
/*      */     }
/*      */     
/*  987 */     if (read_timeout != -1L)
/*      */     {
/*  989 */       connection.setReadTimeout((int)read_timeout);
/*      */     }
/*      */     
/*  992 */     connection.connect();
/*      */   }
/*      */   
/*  995 */   private static String last_headers = COConfigurationManager.getStringParameter("metasearch.web.last.headers", null);
/*      */   
/*      */ 
/*      */ 
/*      */   private static final String default_headers = "QWNjZXB0OiB0ZXh0L2h0bWwsYXBwbGljYXRpb24veGh0bWwreG1sLGFwcGxpY2F0aW9uL3htbDtxPTAuOSwqLyo7cT0wLjgKQWNjZXB0LUNoYXJzZXQ6IElTTy04ODU5LTEsdXRmLTg7cT0wLjcsKjtxPTAuMwpBY2NlcHQtRW5jb2Rpbmc6IGd6aXAsZGVmbGF0ZQpBY2NlcHQtTGFuZ3VhZ2U6IGVuLVVTLGVuO3E9MC44CkNhY2hlLUNvbnRyb2w6IG1heC1hZ2U9MApDb25uZWN0aW9uOiBrZWVwLWFsaXZlClVzZXItQWdlbnQ6IE1vemlsbGEvNS4wIChXaW5kb3dzIE5UIDYuMTsgV09XNjQpIEFwcGxlV2ViS2l0LzUzNi4xMSAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS8yMC4wLjExMzIuNDcgU2FmYXJpLzUzNi4xMQ==";
/*      */   
/*      */ 
/*      */ 
/*      */   public static void setBrowserHeaders(ResourceDownloader rd, String referer)
/*      */   {
/* 1005 */     setBrowserHeaders(rd, null, referer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setBrowserHeaders(ResourceDownloader rd, String encoded_headers, String referer)
/*      */   {
/* 1014 */     String headers_to_use = getBrowserHeadersToUse(encoded_headers);
/*      */     try
/*      */     {
/* 1017 */       String header_string = new String(Base64.decode(headers_to_use), "UTF-8");
/*      */       
/* 1019 */       String[] headers = header_string.split("\n");
/*      */       
/* 1021 */       for (int i = 0; i < headers.length; i++)
/*      */       {
/* 1023 */         String header = headers[i];
/*      */         
/* 1025 */         int pos = header.indexOf(':');
/*      */         
/* 1027 */         if (pos != -1)
/*      */         {
/* 1029 */           String lhs = header.substring(0, pos).trim();
/* 1030 */           String rhs = header.substring(pos + 1).trim();
/*      */           
/* 1032 */           if ((!lhs.equalsIgnoreCase("Host")) && (!lhs.equalsIgnoreCase("Referer")))
/*      */           {
/*      */ 
/* 1035 */             rd.setProperty("URL_" + lhs, rhs);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1040 */       if ((referer != null) && (referer.length() > 0))
/*      */       {
/* 1042 */         rd.setProperty("URL_Referer", referer);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setBrowserHeaders(ResourceUploader ru, String encoded_headers, String referer)
/*      */   {
/* 1054 */     String headers_to_use = getBrowserHeadersToUse(encoded_headers);
/*      */     try
/*      */     {
/* 1057 */       String header_string = new String(Base64.decode(headers_to_use), "UTF-8");
/*      */       
/* 1059 */       String[] headers = header_string.split("\n");
/*      */       
/* 1061 */       for (int i = 0; i < headers.length; i++)
/*      */       {
/* 1063 */         String header = headers[i];
/*      */         
/* 1065 */         int pos = header.indexOf(':');
/*      */         
/* 1067 */         if (pos != -1)
/*      */         {
/* 1069 */           String lhs = header.substring(0, pos).trim();
/* 1070 */           String rhs = header.substring(pos + 1).trim();
/*      */           
/* 1072 */           if ((!lhs.equalsIgnoreCase("Host")) && (!lhs.equalsIgnoreCase("Referer")))
/*      */           {
/*      */ 
/* 1075 */             ru.setProperty("URL_" + lhs, rhs);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1080 */       if ((referer != null) && (referer.length() > 0))
/*      */       {
/* 1082 */         ru.setProperty("URL_Referer", referer);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setBrowserHeaders(URLConnection connection, String referer)
/*      */   {
/* 1093 */     setBrowserHeaders(connection, null, referer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void setBrowserHeaders(URLConnection connection, String encoded_headers, String referer)
/*      */   {
/* 1102 */     String headers_to_use = getBrowserHeadersToUse(encoded_headers);
/*      */     
/*      */     try
/*      */     {
/* 1106 */       String header_string = new String(Base64.decode(headers_to_use), "UTF-8");
/*      */       
/* 1108 */       String[] headers = header_string.split("\n");
/*      */       
/* 1110 */       for (int i = 0; i < headers.length; i++)
/*      */       {
/* 1112 */         String header = headers[i];
/*      */         
/* 1114 */         int pos = header.indexOf(':');
/*      */         
/* 1116 */         if (pos != -1)
/*      */         {
/* 1118 */           String lhs = header.substring(0, pos).trim();
/* 1119 */           String rhs = header.substring(pos + 1).trim();
/*      */           
/* 1121 */           if ((!lhs.equalsIgnoreCase("Host")) && (!lhs.equalsIgnoreCase("Referer")))
/*      */           {
/*      */ 
/* 1124 */             connection.setRequestProperty(lhs, rhs);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1129 */       if ((referer != null) && (referer.length() > 0))
/*      */       {
/* 1131 */         connection.setRequestProperty("Referer", referer);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Map getBrowserHeaders(String referer)
/*      */   {
/* 1141 */     String headers_to_use = getBrowserHeadersToUse(null);
/*      */     
/* 1143 */     Map result = new HashMap();
/*      */     
/*      */     try
/*      */     {
/* 1147 */       String header_string = new String(Base64.decode(headers_to_use), "UTF-8");
/*      */       
/* 1149 */       String[] headers = header_string.split("\n");
/*      */       
/* 1151 */       for (int i = 0; i < headers.length; i++)
/*      */       {
/* 1153 */         String header = headers[i];
/*      */         
/* 1155 */         int pos = header.indexOf(':');
/*      */         
/* 1157 */         if (pos != -1)
/*      */         {
/* 1159 */           String lhs = header.substring(0, pos).trim();
/* 1160 */           String rhs = header.substring(pos + 1).trim();
/*      */           
/* 1162 */           if ((!lhs.equalsIgnoreCase("Host")) && (!lhs.equalsIgnoreCase("Referer")))
/*      */           {
/*      */ 
/* 1165 */             result.put(lhs, rhs);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1170 */       if ((referer != null) && (referer.length() > 0))
/*      */       {
/* 1172 */         result.put("Referer", referer);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1177 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String getBrowserHeadersToUse(String encoded_headers)
/*      */   {
/* 1184 */     String headers_to_use = encoded_headers;
/*      */     
/* 1186 */     synchronized (UrlUtils.class)
/*      */     {
/* 1188 */       if (headers_to_use == null)
/*      */       {
/* 1190 */         if (last_headers != null)
/*      */         {
/* 1192 */           headers_to_use = last_headers;
/*      */         }
/*      */         else
/*      */         {
/* 1196 */           headers_to_use = "QWNjZXB0OiB0ZXh0L2h0bWwsYXBwbGljYXRpb24veGh0bWwreG1sLGFwcGxpY2F0aW9uL3htbDtxPTAuOSwqLyo7cT0wLjgKQWNjZXB0LUNoYXJzZXQ6IElTTy04ODU5LTEsdXRmLTg7cT0wLjcsKjtxPTAuMwpBY2NlcHQtRW5jb2Rpbmc6IGd6aXAsZGVmbGF0ZQpBY2NlcHQtTGFuZ3VhZ2U6IGVuLVVTLGVuO3E9MC44CkNhY2hlLUNvbnRyb2w6IG1heC1hZ2U9MApDb25uZWN0aW9uOiBrZWVwLWFsaXZlClVzZXItQWdlbnQ6IE1vemlsbGEvNS4wIChXaW5kb3dzIE5UIDYuMTsgV09XNjQpIEFwcGxlV2ViS2l0LzUzNi4xMSAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS8yMC4wLjExMzIuNDcgU2FmYXJpLzUzNi4xMQ==";
/*      */         }
/*      */       }
/*      */       else {
/* 1200 */         if ((last_headers == null) || (!headers_to_use.equals(last_headers)))
/*      */         {
/* 1202 */           COConfigurationManager.setParameter("metasearch.web.last.headers", headers_to_use);
/*      */         }
/*      */         
/* 1205 */         last_headers = headers_to_use;
/*      */       }
/*      */     }
/*      */     
/* 1209 */     return headers_to_use;
/*      */   }
/*      */   
/*      */   public static boolean queryHasParameter(String query_string, String param_name, boolean case_sensitive) {
/* 1213 */     if (!case_sensitive) {
/* 1214 */       query_string = query_string.toLowerCase();
/* 1215 */       param_name = param_name.toLowerCase();
/*      */     }
/* 1217 */     if (query_string.charAt(0) == '?') {
/* 1218 */       query_string = '&' + query_string.substring(1);
/*      */     }
/* 1220 */     else if (query_string.charAt(0) != '&') {
/* 1221 */       query_string = '&' + query_string;
/*      */     }
/*      */     
/* 1224 */     return query_string.contains("&" + param_name + "=");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean containsPasskey(URL url)
/*      */   {
/* 1231 */     if (url == null)
/*      */     {
/* 1233 */       return false;
/*      */     }
/*      */     
/* 1236 */     String url_str = url.toExternalForm();
/*      */     
/* 1238 */     return url_str.matches(".*[0-9a-z]{20,40}.*");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static URL setPort(URL u, int port)
/*      */   {
/* 1246 */     if (port == -1) {
/* 1247 */       port = u.getDefaultPort();
/*      */     }
/* 1249 */     StringBuilder result = new StringBuilder();
/* 1250 */     result.append(u.getProtocol());
/* 1251 */     result.append(":");
/* 1252 */     String authority = u.getAuthority();
/* 1253 */     if ((authority != null) && (authority.length() > 0)) {
/* 1254 */       result.append("//");
/* 1255 */       int pos = authority.indexOf('@');
/* 1256 */       if (pos != -1) {
/* 1257 */         result.append(authority.substring(0, pos + 1));
/* 1258 */         authority = authority.substring(pos + 1);
/*      */       }
/* 1260 */       pos = authority.lastIndexOf(':');
/* 1261 */       if (pos == -1) {
/* 1262 */         if (port > 0) {
/* 1263 */           result.append(authority).append(":").append(port);
/*      */         } else {
/* 1265 */           result.append(authority);
/*      */         }
/*      */       }
/* 1268 */       else if (port > 0) {
/* 1269 */         result.append(authority.substring(0, pos + 1)).append(port);
/*      */       } else {
/* 1271 */         result.append(authority.substring(0, pos));
/*      */       }
/*      */     }
/*      */     
/* 1275 */     if (u.getPath() != null) {
/* 1276 */       result.append(u.getPath());
/*      */     }
/* 1278 */     if (u.getQuery() != null) {
/* 1279 */       result.append('?');
/* 1280 */       result.append(u.getQuery());
/*      */     }
/* 1282 */     if (u.getRef() != null) {
/* 1283 */       result.append("#");
/* 1284 */       result.append(u.getRef());
/*      */     }
/*      */     try {
/* 1287 */       return new URL(result.toString());
/*      */     } catch (Throwable e) {
/* 1289 */       Debug.out(e); }
/* 1290 */     return u;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static URL setHost(URL u, String host)
/*      */   {
/* 1299 */     StringBuilder result = new StringBuilder();
/* 1300 */     result.append(u.getProtocol());
/* 1301 */     result.append(":");
/* 1302 */     String authority = u.getAuthority();
/* 1303 */     if ((authority != null) && (authority.length() > 0)) {
/* 1304 */       result.append("//");
/* 1305 */       int pos = authority.indexOf('@');
/* 1306 */       if (pos != -1) {
/* 1307 */         result.append(authority.substring(0, pos + 1));
/* 1308 */         authority = authority.substring(pos + 1);
/*      */       }
/* 1310 */       pos = authority.lastIndexOf(':');
/* 1311 */       if (pos == -1) {
/* 1312 */         result.append(host);
/*      */       } else {
/* 1314 */         result.append(host).append(authority.substring(pos));
/*      */       }
/*      */     }
/* 1317 */     if (u.getPath() != null) {
/* 1318 */       result.append(u.getPath());
/*      */     }
/* 1320 */     if (u.getQuery() != null) {
/* 1321 */       result.append('?');
/* 1322 */       result.append(u.getQuery());
/*      */     }
/* 1324 */     if (u.getRef() != null) {
/* 1325 */       result.append("#");
/* 1326 */       result.append(u.getRef());
/*      */     }
/*      */     try {
/* 1329 */       return new URL(result.toString());
/*      */     } catch (Throwable e) {
/* 1331 */       Debug.out(e); }
/* 1332 */     return u;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static URL setProtocol(URL u, String protocol)
/*      */   {
/* 1341 */     String str = u.toExternalForm();
/*      */     
/* 1343 */     int pos = str.indexOf(":");
/*      */     try
/*      */     {
/* 1346 */       return new URL(protocol + str.substring(pos));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1350 */       Debug.out(e);
/*      */     }
/* 1352 */     return u;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static URL getBaseURL(URL u)
/*      */   {
/* 1360 */     StringBuilder result = new StringBuilder();
/* 1361 */     result.append(u.getProtocol());
/* 1362 */     result.append(":");
/* 1363 */     String authority = u.getAuthority();
/* 1364 */     if ((authority != null) && (authority.length() > 0)) {
/* 1365 */       result.append("//");
/* 1366 */       int pos = authority.indexOf('@');
/* 1367 */       if (pos != -1) {
/* 1368 */         result.append(authority.substring(0, pos + 1));
/* 1369 */         authority = authority.substring(pos + 1);
/*      */       }
/* 1371 */       pos = authority.lastIndexOf(':');
/* 1372 */       int port = u.getPort();
/* 1373 */       if (port == -1) {
/* 1374 */         port = u.getDefaultPort();
/*      */       }
/* 1376 */       if (pos == -1) {
/* 1377 */         result.append(authority).append(":").append(port);
/*      */       } else {
/* 1379 */         result.append(authority.substring(0, pos + 1)).append(port);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1384 */       return new URL(result.toString());
/*      */     } catch (Throwable e) {
/* 1386 */       Debug.out(e); }
/* 1387 */     return u;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String getCanonicalString(URL url)
/*      */   {
/* 1395 */     String protocol = url.getProtocol();
/*      */     
/* 1397 */     if (!protocol.equals(protocol.toLowerCase(Locale.US)))
/*      */     {
/* 1399 */       protocol = protocol.toLowerCase(Locale.US);
/*      */       
/* 1401 */       url = setProtocol(url, protocol);
/*      */     }
/*      */     
/* 1404 */     int port = url.getPort();
/*      */     
/* 1406 */     if ((protocol.equals("http")) || (protocol.equals("https")))
/*      */     {
/* 1408 */       if (port == url.getDefaultPort())
/*      */       {
/* 1410 */         url = setPort(url, 0);
/*      */       }
/*      */       
/*      */     }
/* 1414 */     else if (port == -1)
/*      */     {
/* 1416 */       url = setPort(url, url.getDefaultPort());
/*      */     }
/*      */     
/*      */ 
/* 1420 */     return url.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static URL getIPV4Fallback(URL url)
/*      */   {
/*      */     try
/*      */     {
/* 1434 */       InetAddress[] addresses = AddressUtils.getAllByName(url.getHost());
/*      */       
/* 1436 */       if (addresses.length > 0)
/*      */       {
/* 1438 */         InetAddress ipv4 = null;
/* 1439 */         InetAddress ipv6 = null;
/*      */         
/* 1441 */         for (InetAddress a : addresses)
/*      */         {
/* 1443 */           if ((a instanceof Inet4Address))
/*      */           {
/* 1445 */             ipv4 = a;
/*      */           }
/*      */           else
/*      */           {
/* 1449 */             ipv6 = a;
/*      */           }
/*      */         }
/*      */         
/* 1453 */         if ((ipv4 != null) && (ipv6 != null))
/*      */         {
/* 1455 */           return setHost(url, ipv4.getHostAddress());
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable f) {}
/*      */     
/*      */ 
/*      */ 
/* 1463 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static long getContentLength(URLConnection con)
/*      */   {
/* 1470 */     long res = con.getContentLength();
/*      */     
/* 1472 */     if (res == -1L) {
/*      */       try
/*      */       {
/* 1475 */         String str = con.getHeaderField("content-length");
/*      */         
/* 1477 */         if (str != null)
/*      */         {
/* 1479 */           res = Long.parseLong(str);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 1486 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean SSLSocketSNIHack(String host_name, SSLSocket socket)
/*      */   {
/*      */     try
/*      */     {
/* 1499 */       Object sni_host_name = Class.forName("javax.net.ssl.SNIHostName").getConstructor(new Class[] { String.class }).newInstance(new Object[] { host_name });
/*      */       
/* 1501 */       List<Object> sni_host_names = new ArrayList(1);
/*      */       
/* 1503 */       sni_host_names.add(sni_host_name);
/*      */       
/* 1505 */       Object ssl_parameters = SSLSocket.class.getMethod("getSSLParameters", new Class[0]).invoke(socket, new Object[0]);
/*      */       
/* 1507 */       Class ssl_parameters_class = Class.forName("javax.net.ssl.SSLParameters");
/*      */       
/* 1509 */       ssl_parameters_class.getMethod("setServerNames", new Class[] { List.class }).invoke(ssl_parameters, new Object[] { sni_host_names });
/*      */       
/* 1511 */       socket.getClass().getMethod("setSSLParameters", new Class[] { ssl_parameters_class }).invoke(socket, new Object[] { ssl_parameters });
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
/* 1523 */       return true;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1527 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static SSLSocketFactory DHHackIt(SSLSocketFactory factory)
/*      */   {
/* 1535 */     SSLSocketFactory hack = new SSLSocketFactory()
/*      */     {
/*      */       public Socket createSocket()
/*      */         throws IOException
/*      */       {
/* 1540 */         Socket result = this.val$factory.createSocket();
/*      */         
/* 1542 */         hack(result);
/*      */         
/* 1544 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
/*      */         throws IOException
/*      */       {
/* 1553 */         Socket result = this.val$factory.createSocket(address, port, localAddress, localPort);
/*      */         
/* 1555 */         hack(result);
/*      */         
/* 1557 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */       public Socket createSocket(InetAddress host, int port)
/*      */         throws IOException
/*      */       {
/* 1564 */         Socket result = this.val$factory.createSocket(host, port);
/*      */         
/* 1566 */         hack(result);
/*      */         
/* 1568 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public Socket createSocket(Socket s, String host, int port, boolean autoClose)
/*      */         throws IOException
/*      */       {
/* 1577 */         Socket result = this.val$factory.createSocket(s, host, port, autoClose);
/*      */         
/* 1579 */         hack(result);
/*      */         
/* 1581 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public Socket createSocket(String host, int port)
/*      */         throws IOException, UnknownHostException
/*      */       {
/* 1589 */         Socket result = this.val$factory.createSocket(host, port);
/*      */         
/* 1591 */         hack(result);
/*      */         
/* 1593 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
/*      */         throws IOException, UnknownHostException
/*      */       {
/* 1603 */         Socket result = this.val$factory.createSocket(host, port, localHost, localPort);
/*      */         
/* 1605 */         hack(result);
/*      */         
/* 1607 */         return result;
/*      */       }
/*      */       
/*      */       public String[] getDefaultCipherSuites() {
/* 1611 */         String[] result = this.val$factory.getDefaultCipherSuites();
/*      */         
/* 1613 */         result = hack(result);
/*      */         
/* 1615 */         return result;
/*      */       }
/*      */       
/*      */       public String[] getSupportedCipherSuites() {
/* 1619 */         String[] result = this.val$factory.getSupportedCipherSuites();
/*      */         
/* 1621 */         result = hack(result);
/*      */         
/* 1623 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void hack(Socket socket)
/*      */       {
/* 1630 */         SSLSocket ssl_socket = (SSLSocket)socket;
/*      */         
/* 1632 */         ssl_socket.setEnabledCipherSuites(hack(ssl_socket.getEnabledCipherSuites()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private String[] hack(String[] cs)
/*      */       {
/* 1639 */         List<String> new_cs = new ArrayList();
/*      */         
/* 1641 */         for (String x : cs)
/*      */         {
/* 1643 */           if ((!x.contains("_DH_")) && (!x.contains("_DHE_")))
/*      */           {
/*      */ 
/*      */ 
/* 1647 */             new_cs.add(x);
/*      */           }
/*      */         }
/*      */         
/* 1651 */         return (String[])new_cs.toArray(new String[new_cs.size()]);
/*      */       }
/*      */       
/* 1654 */     };
/* 1655 */     return hack;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void HTTPSURLConnectionSNIHack(final String host_name, HttpsURLConnection con)
/*      */   {
/* 1663 */     SSLSocketFactory factory = con.getSSLSocketFactory();
/*      */     
/* 1665 */     SSLSocketFactory hack = new SSLSocketFactory()
/*      */     {
/*      */       public Socket createSocket()
/*      */         throws IOException
/*      */       {
/* 1670 */         Socket result = this.val$factory.createSocket();
/*      */         
/* 1672 */         hack(result);
/*      */         
/* 1674 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
/*      */         throws IOException
/*      */       {
/* 1683 */         Socket result = this.val$factory.createSocket(address, port, localAddress, localPort);
/*      */         
/* 1685 */         hack(result);
/*      */         
/* 1687 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */       public Socket createSocket(InetAddress host, int port)
/*      */         throws IOException
/*      */       {
/* 1694 */         Socket result = this.val$factory.createSocket(host, port);
/*      */         
/* 1696 */         hack(result);
/*      */         
/* 1698 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public Socket createSocket(Socket s, String host, int port, boolean autoClose)
/*      */         throws IOException
/*      */       {
/* 1707 */         Socket result = this.val$factory.createSocket(s, host, port, autoClose);
/*      */         
/* 1709 */         hack(result);
/*      */         
/* 1711 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public Socket createSocket(String host, int port)
/*      */         throws IOException, UnknownHostException
/*      */       {
/* 1719 */         Socket result = this.val$factory.createSocket(host, port);
/*      */         
/* 1721 */         hack(result);
/*      */         
/* 1723 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
/*      */         throws IOException, UnknownHostException
/*      */       {
/* 1733 */         Socket result = this.val$factory.createSocket(host, port, localHost, localPort);
/*      */         
/* 1735 */         hack(result);
/*      */         
/* 1737 */         return result;
/*      */       }
/*      */       
/*      */       public String[] getDefaultCipherSuites() {
/* 1741 */         String[] result = this.val$factory.getDefaultCipherSuites();
/*      */         
/* 1743 */         result = hack(result);
/*      */         
/* 1745 */         return result;
/*      */       }
/*      */       
/*      */       public String[] getSupportedCipherSuites() {
/* 1749 */         String[] result = this.val$factory.getSupportedCipherSuites();
/*      */         
/* 1751 */         result = hack(result);
/*      */         
/* 1753 */         return result;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private void hack(Socket socket)
/*      */       {
/* 1760 */         SSLSocket ssl_socket = (SSLSocket)socket;
/*      */         
/* 1762 */         UrlUtils.SSLSocketSNIHack(host_name, ssl_socket);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       private String[] hack(String[] cs)
/*      */       {
/* 1769 */         return cs;
/*      */       }
/*      */       
/* 1772 */     };
/* 1773 */     con.setSSLSocketFactory(hack);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void DHHackIt(HttpsURLConnection ssl_con)
/*      */   {
/* 1780 */     SSLSocketFactory factory = ssl_con.getSSLSocketFactory();
/*      */     
/* 1782 */     SSLSocketFactory hack = DHHackIt(factory);
/*      */     
/* 1784 */     ssl_con.setSSLSocketFactory(hack);
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
/*      */   public static Socket connectSocketAndWrite(boolean is_ssl, String target_host, int target_port, byte[] bytes, int connect_timeout, int read_timeout)
/*      */     throws Exception
/*      */   {
/* 1802 */     boolean is_java_17_plus = Constants.isJava7OrHigher;
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1807 */       return connectSocketAndWrite(is_ssl, target_host, target_port, bytes, connect_timeout, read_timeout, false);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1811 */       if (is_java_17_plus)
/*      */       {
/* 1813 */         throw e;
/*      */       }
/*      */     }
/* 1816 */     return connectSocketAndWrite(is_ssl, target_host, target_port, bytes, connect_timeout, read_timeout, true);
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
/*      */   public static Socket connectSocketAndWrite(boolean is_ssl, String target_host, int target_port, byte[] bytes, int connect_timeout, int read_timeout, boolean unconnected_socket_hack)
/*      */     throws Exception
/*      */   {
/* 1832 */     boolean cert_hack = false;
/* 1833 */     boolean dh_hack = false;
/* 1834 */     boolean internal_error_hack = false;
/*      */     
/* 1836 */     boolean hacks_to_do = true;
/* 1837 */     Exception last_error = null;
/*      */     
/* 1839 */     while (hacks_to_do)
/*      */     {
/* 1841 */       hacks_to_do = false;
/*      */       
/* 1843 */       Socket target = null;
/*      */       
/* 1845 */       boolean ok = false;
/*      */       
/*      */       try
/*      */       {
/* 1849 */         InetSocketAddress targetSockAddress = new InetSocketAddress(InetAddress.getByName(target_host), target_port);
/*      */         
/* 1851 */         InetAddress bindIP = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress((targetSockAddress.getAddress() instanceof Inet6Address) ? 2 : 1);
/*      */         TrustManager[] tms_delegate;
/* 1853 */         if (is_ssl)
/*      */         {
/* 1855 */           tms_delegate = SESecurityManager.getAllTrustingTrustManager();
/*      */           
/* 1857 */           SSLContext sc = SSLContext.getInstance("SSL");
/*      */           
/* 1859 */           sc.init(null, tms_delegate, RandomUtils.SECURE_RANDOM);
/*      */           
/* 1861 */           SSLSocketFactory factory = sc.getSocketFactory();
/*      */           
/* 1863 */           if (dh_hack)
/*      */           {
/* 1865 */             factory = DHHackIt(factory);
/*      */           }
/*      */           else {
/* 1868 */             factory = DHHackIt(factory);
/*      */           }
/*      */           
/*      */ 
/* 1872 */           if (unconnected_socket_hack)
/*      */           {
/* 1874 */             if (bindIP == null)
/*      */             {
/* 1876 */               target = factory.createSocket(targetSockAddress.getAddress(), targetSockAddress.getPort());
/*      */             }
/*      */             else
/*      */             {
/* 1880 */               target = factory.createSocket(targetSockAddress.getAddress(), targetSockAddress.getPort(), bindIP, 0);
/*      */             }
/*      */           }
/*      */           else {
/* 1884 */             target = factory.createSocket();
/*      */           }
/*      */           
/*      */         }
/* 1888 */         else if (unconnected_socket_hack)
/*      */         {
/* 1890 */           if (bindIP == null)
/*      */           {
/* 1892 */             target = new Socket(targetSockAddress.getAddress(), targetSockAddress.getPort());
/*      */           }
/*      */           else
/*      */           {
/* 1896 */             target = new Socket(targetSockAddress.getAddress(), targetSockAddress.getPort(), bindIP, 0);
/*      */           }
/*      */         }
/*      */         else {
/* 1900 */           target = new Socket();
/*      */         }
/*      */         
/*      */ 
/* 1904 */         if (internal_error_hack)
/*      */         {
/* 1906 */           SSLSocketSNIHack(target_host, (SSLSocket)target);
/*      */         }
/*      */         
/* 1909 */         target.setSoTimeout(read_timeout);
/*      */         
/* 1911 */         if (!unconnected_socket_hack)
/*      */         {
/* 1913 */           if (bindIP != null)
/*      */           {
/* 1915 */             target.bind(new InetSocketAddress(bindIP, 0));
/*      */           }
/*      */           
/* 1918 */           target.connect(targetSockAddress, connect_timeout);
/*      */         }
/*      */         
/* 1921 */         target.getOutputStream().write(bytes);
/*      */         
/* 1923 */         ok = true;
/*      */         
/* 1925 */         return target;
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1929 */         last_error = e;
/*      */         
/* 1931 */         if ((e instanceof SSLException))
/*      */         {
/* 1933 */           String msg = Debug.getNestedExceptionMessage(e);
/*      */           
/* 1935 */           if (msg.contains("DH keypair"))
/*      */           {
/* 1937 */             if (!dh_hack)
/*      */             {
/* 1939 */               dh_hack = true;
/*      */               
/* 1941 */               hacks_to_do = true;
/*      */             }
/* 1943 */           } else if (msg.contains("internal_error"))
/*      */           {
/* 1945 */             if (!internal_error_hack)
/*      */             {
/* 1947 */               internal_error_hack = true;
/*      */               
/* 1949 */               hacks_to_do = true;
/*      */             }
/*      */           }
/*      */           
/* 1953 */           if (!cert_hack)
/*      */           {
/* 1955 */             cert_hack = true;
/*      */             
/* 1957 */             SESecurityManager.installServerCertificates(new URL("https://" + target_host + ":" + target_port + "/"));
/*      */             
/* 1959 */             hacks_to_do = true;
/*      */           }
/*      */         }
/*      */         
/* 1963 */         if (!hacks_to_do)
/*      */         {
/* 1965 */           throw e;
/*      */         }
/*      */       }
/*      */       finally {
/* 1969 */         if (!ok)
/*      */         {
/* 1971 */           if (target != null)
/*      */           {
/* 1973 */             target.close();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1979 */     throw last_error;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1987 */     System.out.println(URLEncoder.encode("http://a.b.c/fred?a=10&b=20"));
/*      */     
/* 1989 */     byte[] infohash = ByteFormatter.decodeString("1234567890123456789012345678901234567890");
/* 1990 */     String[] test = { "http://moo.com", "http%3A%2F/moo%2Ecom", "magnet:?moo", "magnet%3A%3Fxt=urn:btih:26", "magnet%3A//%3Fmooo", "magnet:?xt=urn:btih:" + Base32.encode(infohash), "aaaaaaaaaabbbbbbbbbbccccccccccdddddddddd", "magnet:?dn=OpenOffice.org_2.0.3_Win32Intel_install.exe&xt=urn:sha1:PEMIGLKMNFI4HZ4CCHZNPKZJNMAAORKN&xt=urn:tree:tiger:JMIJVWHCQUX47YYH7O4XIBCORNU2KYKHBBC6DHA&xt=urn:ed2k:1c0804541f34b6583a383bb8f2cec682&xl=96793015&xs=http://mirror.switch.ch/ftp/mirror/OpenOffice/stable/2.0.3/OOo_2.0.3_Win32Intel_install.exe%3Fa%3D10%26b%3D20" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2000 */     for (int i = 0; i < test.length; i++) {
/* 2001 */       System.out.println(test[i]);
/* 2002 */       System.out.println("URLDecoder.decode: -> " + URLDecoder.decode(test[i]));
/* 2003 */       System.out.println("decode:            -> " + decode(test[i]));
/* 2004 */       System.out.println("decodeIf:          -> " + decodeIfNeeded(test[i]));
/* 2005 */       System.out.println("isURL:             -> " + isURL(test[i]));
/* 2006 */       System.out.println("parse:             -> " + parseTextForURL(test[i], true));
/*      */     }
/*      */     
/* 2009 */     String[] testEncode = { "a b" };
/*      */     
/*      */ 
/* 2012 */     for (int i = 0; i < testEncode.length; i++) {
/* 2013 */       String txt = testEncode[i];
/*      */       try {
/* 2015 */         System.out.println("URLEncoder.encode: " + txt + " -> " + URLEncoder.encode(txt, "UTF8"));
/*      */       }
/*      */       catch (UnsupportedEncodingException e) {}
/*      */       
/* 2019 */       System.out.println("URLEncoder.encode: " + txt + " -> " + URLEncoder.encode(txt));
/*      */       
/* 2021 */       System.out.println("encode: " + txt + " -> " + encode(txt));
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/UrlUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */