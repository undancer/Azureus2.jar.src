/*      */ package org.gudy.azureus2.core3.tracker.server.impl.tcp;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.net.URLDecoder;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*      */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerImpl;
/*      */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerPeerImpl;
/*      */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerProcessor.lightweightPeer;
/*      */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerTorrentImpl;
/*      */ import org.gudy.azureus2.core3.util.AsyncController;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*      */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.bouncycastle.util.encoders.Base64;
/*      */ 
/*      */ public abstract class TRTrackerServerProcessorTCP extends org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerProcessor
/*      */ {
/*      */   protected static final int SOCKET_TIMEOUT = 5000;
/*      */   protected static final char CR = '\r';
/*      */   protected static final char FF = '\n';
/*      */   protected static final String NL = "\r\n";
/*   51 */   private static final String lc_azureus_name = "Azureus".toLowerCase();
/*      */   
/*   53 */   protected static final byte[] HTTP_RESPONSE_START = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nServer: Azureus 5.7.6.0\r\nConnection: close\r\nContent-Length: ".getBytes();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   60 */   protected static final byte[] HTTP_RESPONSE_XML_START = "HTTP/1.1 200 OK\r\nContent-Type: text/xml; charset=\"utf-8\"\r\nServer: Azureus 5.7.6.0\r\nConnection: close\r\nContent-Length: ".getBytes();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   67 */   protected static final byte[] HTTP_RESPONSE_END_GZIP = "\r\nContent-Encoding: gzip\r\n\r\n".getBytes();
/*   68 */   protected static final byte[] HTTP_RESPONSE_END_NOGZIP = "\r\n\r\n".getBytes();
/*      */   private static String MSG_CLIENT_NOT_SUPPORTED;
/*      */   private final TRTrackerServerTCP server;
/*      */   private final String server_url;
/*      */   
/*   73 */   static { MessageText.addAndFireListener(new MessageText.MessageTextListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void localeChanged(Locale old_locale, Locale new_locale)
/*      */       {
/*      */ 
/*      */ 
/*   81 */         TRTrackerServerProcessorTCP.access$002(MessageText.getString("tracker.msg.client.not.supported"));
/*      */       }
/*      */     }); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   90 */   private boolean disable_timeouts = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TRTrackerServerProcessorTCP(TRTrackerServerTCP _server)
/*      */   {
/*   97 */     this.server = _server;
/*      */     
/*   99 */     this.server_url = ((this.server.isSSL() ? "https" : "http") + "://" + UrlUtils.convertIPV6Host(this.server.getHost()) + ":" + this.server.getPort());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean areTimeoutsDisabled()
/*      */   {
/*  105 */     return this.disable_timeouts;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTimeoutsDisabled(boolean d)
/*      */   {
/*  112 */     this.disable_timeouts = d;
/*      */   }
/*      */   
/*      */ 
/*      */   protected TRTrackerServerTCP getServer()
/*      */   {
/*  118 */     return this.server;
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
/*      */   protected boolean processRequest(String input_header, String lowercase_input_header, String url_path, InetSocketAddress local_address, InetSocketAddress remote_address, boolean announce_and_scrape_only, boolean keep_alive, InputStream is, OutputStream os, AsyncController async)
/*      */     throws IOException
/*      */   {
/*  136 */     String str = url_path;
/*      */     
/*  138 */     int request_type = -1;
/*      */     
/*  140 */     boolean compact_enabled = this.server.isCompactEnabled();
/*      */     try
/*      */     {
/*  143 */       Map root = null;
/*      */       
/*  145 */       TRTrackerServerTorrentImpl specific_torrent = null;
/*      */       
/*  147 */       boolean gzip_reply = false;
/*      */       
/*  149 */       boolean xml_output = false;
/*      */       try
/*      */       {
/*  152 */         List<String> banned = TRTrackerServerImpl.banned_clients;
/*      */         String user_agent;
/*  154 */         Iterator i$; if (!banned.isEmpty())
/*      */         {
/*  156 */           int ua_pos = lowercase_input_header.indexOf("user-agent");
/*      */           
/*  158 */           if (ua_pos != -1)
/*      */           {
/*  160 */             user_agent = lowercase_input_header.substring(ua_pos + 10, lowercase_input_header.indexOf("\n", ua_pos)).trim().substring(1).trim();
/*      */             
/*  162 */             for (i$ = banned.iterator(); i$.hasNext();) { b = (String)i$.next();
/*      */               
/*  164 */               if (user_agent.contains(b))
/*      */               {
/*  166 */                 throw new Exception(MSG_CLIENT_NOT_SUPPORTED); }
/*      */             }
/*      */           }
/*      */         }
/*      */         String b;
/*      */         String redirect;
/*  172 */         if (str.startsWith("/announce?"))
/*      */         {
/*  174 */           request_type = 1;
/*      */           
/*  176 */           str = str.substring(10);
/*      */         }
/*  178 */         else if (str.startsWith("/scrape?"))
/*      */         {
/*  180 */           request_type = 2;
/*      */           
/*  182 */           str = str.substring(8);
/*      */         }
/*  184 */         else if (str.equals("/scrape"))
/*      */         {
/*  186 */           request_type = 3;
/*      */           
/*  188 */           str = "";
/*      */         }
/*  190 */         else if (str.startsWith("/query?"))
/*      */         {
/*  192 */           request_type = 4;
/*      */           
/*  194 */           str = str.substring(7);
/*      */         }
/*      */         else
/*      */         {
/*  198 */           redirect = TRTrackerServerImpl.redirect_on_not_found;
/*      */           String user;
/*  200 */           if (announce_and_scrape_only)
/*      */           {
/*  202 */             if (redirect.length() == 0)
/*      */             {
/*  204 */               throw new Exception("Tracker only supports announce and scrape functions");
/*      */             }
/*      */           }
/*      */           else {
/*  208 */             setTaskState("external request");
/*      */             
/*  210 */             this.disable_timeouts = true;
/*      */             
/*      */ 
/*      */ 
/*  214 */             user = doAuthentication(remote_address, url_path, input_header, os, false);
/*      */             
/*  216 */             if (user == null)
/*      */             {
/*  218 */               return 0;
/*      */             }
/*      */             
/*  221 */             boolean[] ka = { keep_alive };
/*      */             
/*  223 */             if (handleExternalRequest(local_address, remote_address, user, str, input_header, is, os, async, ka))
/*      */             {
/*  225 */               return ka[0];
/*      */             }
/*      */           }
/*      */           
/*  229 */           if (redirect.length() > 0)
/*      */           {
/*  231 */             os.write(("HTTP/1.1 301 Moved Permanently\r\nLocation: " + redirect + "\r\n" + "Connection: close" + "\r\n" + "Content-Length: 0" + "\r\n" + "\r\n").getBytes());
/*      */           }
/*      */           else
/*      */           {
/*  235 */             os.write("HTTP/1.1 404 Not Found\r\nConnection: close\r\nContent-Length: 0\r\n\r\n".getBytes());
/*      */           }
/*      */           
/*  238 */           os.flush();
/*      */           
/*  240 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  247 */         if (doAuthentication(remote_address, url_path, input_header, os, true) == null)
/*      */         {
/*  249 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*  253 */         int enc_pos = lowercase_input_header.indexOf("accept-encoding:");
/*      */         
/*  255 */         if (enc_pos != -1)
/*      */         {
/*  257 */           int e_pos = input_header.indexOf("\r\n", enc_pos);
/*      */           
/*  259 */           if (e_pos != -1)
/*      */           {
/*      */ 
/*      */ 
/*  263 */             if (enc_pos > 0)
/*      */             {
/*  265 */               char c = lowercase_input_header.charAt(enc_pos - 1);
/*      */               
/*  267 */               if ((c != '\n') && (c != ' '))
/*      */               {
/*  269 */                 enc_pos = -1;
/*      */               }
/*      */             }
/*      */             
/*  273 */             if (enc_pos != -1)
/*      */             {
/*  275 */               String accept_encoding = lowercase_input_header.substring(enc_pos + 16, e_pos);
/*      */               
/*  277 */               gzip_reply = com.aelitis.azureus.core.util.HTTPUtils.canGZIP(accept_encoding);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  282 */         setTaskState("decoding announce/scrape");
/*      */         
/*  284 */         int pos = 0;
/*      */         
/*  286 */         byte[] hash = null;
/*  287 */         List hash_list = null;
/*  288 */         String link = null;
/*      */         
/*  290 */         HashWrapper peer_id = null;
/*  291 */         int tcp_port = 0;
/*  292 */         String event = null;
/*      */         
/*  294 */         long uploaded = 0L;
/*  295 */         long downloaded = 0L;
/*  296 */         long left = 0L;
/*  297 */         int num_want = -1;
/*  298 */         boolean no_peer_id = false;
/*  299 */         byte compact_mode = 0;
/*  300 */         String key = null;
/*  301 */         byte crypto_level = 0;
/*  302 */         int crypto_port = 0;
/*  303 */         int udp_port = 0;
/*  304 */         int http_port = 0;
/*  305 */         int az_ver = 0;
/*  306 */         boolean stop_to_queue = false;
/*  307 */         String scrape_flags = null;
/*  308 */         int up_speed = 0;
/*  309 */         boolean hide = false;
/*      */         
/*  311 */         DHTNetworkPosition network_position = null;
/*      */         
/*  313 */         String real_ip_address = remote_address.getAddress().getHostAddress();
/*  314 */         String client_ip_address = real_ip_address;
/*      */         
/*  316 */         while (pos < str.length())
/*      */         {
/*  318 */           int p1 = str.indexOf('&', pos);
/*      */           
/*      */           String token;
/*      */           String token;
/*  322 */           if (p1 == -1)
/*      */           {
/*  324 */             token = str.substring(pos);
/*      */           }
/*      */           else
/*      */           {
/*  328 */             token = str.substring(pos, p1);
/*      */             
/*  330 */             pos = p1 + 1;
/*      */           }
/*      */           
/*  333 */           int p2 = token.indexOf('=');
/*      */           
/*  335 */           if (p2 == -1)
/*      */           {
/*  337 */             throw new Exception("format invalid");
/*      */           }
/*      */           
/*  340 */           String lhs = token.substring(0, p2).toLowerCase();
/*  341 */           String rhs = URLDecoder.decode(token.substring(p2 + 1), "ISO-8859-1");
/*      */           
/*      */ 
/*      */ 
/*  345 */           if (lhs.equals("info_hash"))
/*      */           {
/*  347 */             byte[] b = rhs.getBytes("ISO-8859-1");
/*      */             
/*  349 */             if (hash == null)
/*      */             {
/*  351 */               hash = b;
/*      */             }
/*      */             else
/*      */             {
/*  355 */               if (hash_list == null)
/*      */               {
/*  357 */                 hash_list = new ArrayList();
/*      */                 
/*  359 */                 hash_list.add(hash);
/*      */               }
/*      */               
/*  362 */               hash_list.add(b);
/*      */             }
/*      */           }
/*  365 */           else if (lhs.equals("peer_id"))
/*      */           {
/*  367 */             peer_id = new HashWrapper(rhs.getBytes("ISO-8859-1"));
/*      */           }
/*  369 */           else if (lhs.equals("no_peer_id"))
/*      */           {
/*  371 */             no_peer_id = rhs.equals("1");
/*      */           }
/*  373 */           else if (lhs.equals("compact"))
/*      */           {
/*  375 */             if (compact_enabled)
/*      */             {
/*  377 */               if ((rhs.equals("1")) && (compact_mode == 0))
/*      */               {
/*  379 */                 compact_mode = 1;
/*      */               }
/*      */             }
/*  382 */           } else if (lhs.equals("key"))
/*      */           {
/*  384 */             if (this.server.isKeyEnabled())
/*      */             {
/*  386 */               key = rhs;
/*      */             }
/*      */           }
/*  389 */           else if (lhs.equals("port"))
/*      */           {
/*  391 */             tcp_port = Integer.parseInt(rhs);
/*      */           }
/*  393 */           else if (lhs.equals("event"))
/*      */           {
/*  395 */             event = rhs;
/*      */           }
/*  397 */           else if (lhs.equals("ip"))
/*      */           {
/*      */ 
/*      */ 
/*  401 */             if (!HostNameToIPResolver.isNonDNSName(rhs))
/*      */             {
/*  403 */               for (int i = 0; i < rhs.length(); i++)
/*      */               {
/*  405 */                 char c = rhs.charAt(i);
/*      */                 
/*  407 */                 if ((c != '.') && (c != ':') && (!Character.isDigit(c)))
/*      */                 {
/*  409 */                   throw new Exception("IP override address must be resolved by the client");
/*      */                 }
/*      */               }
/*      */               try
/*      */               {
/*  414 */                 rhs = HostNameToIPResolver.syncResolve(rhs).getHostAddress();
/*      */               }
/*      */               catch (UnknownHostException e)
/*      */               {
/*  418 */                 throw new Exception("IP override address must be resolved by the client");
/*      */               }
/*      */             }
/*      */             
/*  422 */             client_ip_address = rhs;
/*      */           }
/*  424 */           else if (lhs.equals("uploaded"))
/*      */           {
/*  426 */             uploaded = Long.parseLong(rhs);
/*      */           }
/*  428 */           else if (lhs.equals("downloaded"))
/*      */           {
/*  430 */             downloaded = Long.parseLong(rhs);
/*      */           }
/*  432 */           else if (lhs.equals("left"))
/*      */           {
/*  434 */             left = Long.parseLong(rhs);
/*      */           }
/*  436 */           else if (lhs.equals("numwant"))
/*      */           {
/*  438 */             num_want = Integer.parseInt(rhs);
/*      */           }
/*  440 */           else if (lhs.equals("azudp"))
/*      */           {
/*  442 */             udp_port = Integer.parseInt(rhs);
/*      */             
/*      */ 
/*      */ 
/*  446 */             if (compact_enabled)
/*      */             {
/*  448 */               compact_mode = 2;
/*      */             }
/*      */           }
/*  451 */           else if (lhs.equals("azhttp"))
/*      */           {
/*  453 */             http_port = Integer.parseInt(rhs);
/*      */           }
/*  455 */           else if (lhs.equals("azver"))
/*      */           {
/*  457 */             az_ver = Integer.parseInt(rhs);
/*      */           }
/*  459 */           else if (lhs.equals("supportcrypto"))
/*      */           {
/*  461 */             if (crypto_level == 0)
/*      */             {
/*  463 */               crypto_level = 1;
/*      */             }
/*      */           }
/*  466 */           else if (lhs.equals("requirecrypto"))
/*      */           {
/*  468 */             crypto_level = 2;
/*      */           }
/*  470 */           else if (lhs.equals("cryptoport"))
/*      */           {
/*  472 */             crypto_port = Integer.parseInt(rhs);
/*      */           }
/*  474 */           else if (lhs.equals("azq"))
/*      */           {
/*  476 */             stop_to_queue = true;
/*      */           }
/*  478 */           else if (lhs.equals("azsf"))
/*      */           {
/*  480 */             scrape_flags = rhs;
/*      */           }
/*  482 */           else if (lhs.equals("link"))
/*      */           {
/*  484 */             link = rhs;
/*      */           }
/*  486 */           else if (lhs.equals("outform"))
/*      */           {
/*  488 */             if (rhs.equals("xml"))
/*      */             {
/*  490 */               xml_output = true;
/*      */             }
/*      */           }
/*  493 */           else if (lhs.equals("hide"))
/*      */           {
/*  495 */             hide = Integer.parseInt(rhs) == 1;
/*      */           }
/*  497 */           else if (TRTrackerServerImpl.supportsExtensions())
/*      */           {
/*  499 */             if (lhs.equals("aznp")) {
/*      */               try
/*      */               {
/*  502 */                 network_position = DHTNetworkPositionManager.deserialisePosition(remote_address.getAddress(), org.gudy.azureus2.core3.util.Base32.decode(rhs));
/*      */ 
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*  507 */             else if (lhs.equals("azup"))
/*      */             {
/*  509 */               up_speed = Integer.parseInt(rhs);
/*      */             }
/*      */           }
/*      */           
/*  513 */           if (p1 == -1) {
/*      */             break;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  524 */         if (hide)
/*      */         {
/*  526 */           tcp_port = 0;
/*  527 */           crypto_port = 0;
/*  528 */           http_port = 0;
/*  529 */           udp_port = 0;
/*      */         }
/*      */         
/*  532 */         if (crypto_level == 2)
/*      */         {
/*  534 */           if (crypto_port != 0)
/*      */           {
/*  536 */             tcp_port = crypto_port;
/*      */           }
/*      */         }
/*      */         
/*  540 */         byte[][] hashes = (byte[][])null;
/*      */         
/*  542 */         if (hash_list != null)
/*      */         {
/*  544 */           hashes = new byte[hash_list.size()][];
/*      */           
/*  546 */           hash_list.toArray(hashes);
/*      */         }
/*  548 */         else if (hash != null)
/*      */         {
/*  550 */           hashes = new byte[][] { hash };
/*      */         }
/*      */         
/*  553 */         if (compact_enabled)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  558 */           if (xml_output)
/*      */           {
/*  560 */             compact_mode = 16;
/*      */           }
/*  562 */           else if (az_ver >= 2)
/*      */           {
/*  564 */             compact_mode = 3;
/*      */           }
/*      */         }
/*      */         
/*  568 */         Map[] root_out = new Map[1];
/*  569 */         TRTrackerServerPeerImpl[] peer_out = new TRTrackerServerPeerImpl[1];
/*      */         
/*  571 */         specific_torrent = processTrackerRequest(this.server, str, root_out, peer_out, request_type, hashes, link, scrape_flags, peer_id, no_peer_id, compact_mode, key, event, stop_to_queue, tcp_port & 0xFFFF, udp_port & 0xFFFF, http_port & 0xFFFF, real_ip_address, client_ip_address, downloaded, uploaded, left, num_want, crypto_level, (byte)az_ver, up_speed, network_position);
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
/*  589 */         root = root_out[0];
/*      */         
/*  591 */         if (request_type == 2)
/*      */         {
/*      */ 
/*      */ 
/*  595 */           if (lowercase_input_header.contains(lc_azureus_name))
/*      */           {
/*  597 */             root.put("aztracker", new Long(1L));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  603 */         if (root.get("_data") == null)
/*      */         {
/*  605 */           org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer post_process_peer = peer_out[0];
/*      */           
/*  607 */           if (post_process_peer == null)
/*      */           {
/*  609 */             post_process_peer = new TRTrackerServerProcessor.lightweightPeer(client_ip_address, tcp_port, peer_id);
/*      */           }
/*      */           
/*  612 */           this.server.postProcess(post_process_peer, specific_torrent, request_type, str, root);
/*      */         }
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  617 */         String warning_message = null;
/*      */         
/*  619 */         Map error_entries = null;
/*      */         
/*  621 */         if ((e instanceof TRTrackerServerException))
/*      */         {
/*  623 */           TRTrackerServerException tr_excep = (TRTrackerServerException)e;
/*      */           
/*  625 */           int reason = tr_excep.getResponseCode();
/*      */           
/*  627 */           error_entries = tr_excep.getErrorEntries();
/*      */           
/*  629 */           if (reason != -1)
/*      */           {
/*  631 */             String resp = "HTTP/1.1 " + reason + " " + tr_excep.getResponseText() + "\r\n";
/*      */             
/*  633 */             Map headers = tr_excep.getResponseHeaders();
/*      */             
/*  635 */             Iterator it = headers.entrySet().iterator();
/*      */             String key;
/*  637 */             while (it.hasNext())
/*      */             {
/*  639 */               Map.Entry entry = (Map.Entry)it.next();
/*      */               
/*  641 */               key = (String)entry.getKey();
/*  642 */               String value = (String)entry.getValue();
/*      */               
/*  644 */               if (key.equalsIgnoreCase("connection"))
/*      */               {
/*  646 */                 if (!value.equalsIgnoreCase("close"))
/*      */                 {
/*  648 */                   Debug.out("Ignoring 'Connection' header");
/*      */                   
/*  650 */                   continue;
/*      */                 }
/*      */               }
/*  653 */               resp = resp + key + ": " + value + "\r\n";
/*      */             }
/*      */             
/*  656 */             resp = resp + "Connection: close\r\n";
/*      */             
/*  658 */             byte[] payload = null;
/*      */             
/*  660 */             if (error_entries != null)
/*      */             {
/*  662 */               payload = BEncoder.encode(error_entries);
/*      */               
/*  664 */               resp = resp + "Content-Length: " + payload.length + "\r\n";
/*      */             }
/*      */             else {
/*  667 */               resp = resp + "Content-Length: 0\r\n";
/*      */             }
/*      */             
/*  670 */             resp = resp + "\r\n";
/*      */             
/*  672 */             os.write(resp.getBytes());
/*      */             
/*  674 */             if (payload != null)
/*      */             {
/*  676 */               os.write(payload);
/*      */             }
/*      */             
/*  679 */             os.flush();
/*      */             
/*  681 */             return 0;
/*      */           }
/*      */           
/*  684 */           if (tr_excep.isUserMessage())
/*      */           {
/*  686 */             warning_message = tr_excep.getMessage();
/*      */           }
/*  688 */         } else if ((e instanceof NullPointerException))
/*      */         {
/*  690 */           e.printStackTrace();
/*      */         }
/*      */         
/*  693 */         String message = e.getMessage();
/*      */         
/*      */ 
/*      */ 
/*  697 */         if ((message == null) || (message.length() == 0))
/*      */         {
/*      */ 
/*      */ 
/*  701 */           message = e.toString();
/*      */         }
/*      */         
/*  704 */         root = new HashMap();
/*      */         
/*  706 */         root.put("failure reason", message);
/*      */         
/*  708 */         if (warning_message != null)
/*      */         {
/*  710 */           root.put("warning message", warning_message);
/*      */         }
/*      */         
/*  713 */         if (error_entries != null)
/*      */         {
/*  715 */           root.putAll(error_entries);
/*      */         }
/*      */       }
/*      */       
/*  719 */       setTaskState("writing response");
/*      */       
/*      */       byte[] header_start;
/*      */       byte[] data;
/*      */       byte[] header_start;
/*  724 */       if (xml_output)
/*      */       {
/*  726 */         StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/*      */         
/*  728 */         xml.append("<RESULT>");
/*      */         
/*  730 */         if (specific_torrent != null)
/*      */         {
/*  732 */           xml.append("<BTIH>");
/*  733 */           xml.append(ByteFormatter.encodeString(specific_torrent.getHash().getBytes()));
/*  734 */           xml.append("</BTIH>");
/*      */           
/*  736 */           xml.append(BEncoder.encodeToXML(root, true));
/*      */         }
/*      */         
/*  739 */         xml.append("</RESULT>");
/*      */         
/*  741 */         byte[] data = xml.toString().getBytes("UTF-8");
/*      */         
/*  743 */         header_start = HTTP_RESPONSE_XML_START;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  748 */         data = (byte[])root.get("_data");
/*      */         
/*  750 */         if (data == null)
/*      */         {
/*  752 */           data = BEncoder.encode(root);
/*      */           
/*  754 */           if (data.length > 1000000)
/*      */           {
/*  756 */             File dump = new File("bdecoder.dump");
/*      */             
/*  758 */             synchronized (TRTrackerServerProcessorTCP.class)
/*      */             {
/*      */               try {
/*  761 */                 Debug.out("Output is too large, saving diagnostics to " + dump.toString());
/*      */                 
/*  763 */                 PrintWriter pw = new PrintWriter(new FileWriter(dump));
/*      */                 
/*  765 */                 org.gudy.azureus2.core3.util.BDecoder.print(pw, root);
/*      */                 
/*  767 */                 pw.close();
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  775 */           root.put("_data", data);
/*      */         }
/*      */         
/*  778 */         header_start = HTTP_RESPONSE_START;
/*      */       }
/*      */       
/*  781 */       if (gzip_reply)
/*      */       {
/*  783 */         byte[] gzip_data = (byte[])root.get("_gzipdata");
/*      */         
/*  785 */         if (gzip_data == null)
/*      */         {
/*  787 */           ByteArrayOutputStream tos = new ByteArrayOutputStream(data.length);
/*      */           
/*  789 */           GZIPOutputStream gos = new GZIPOutputStream(tos);
/*      */           
/*  791 */           gos.write(data);
/*      */           
/*  793 */           gos.close();
/*      */           
/*  795 */           gzip_data = tos.toByteArray();
/*      */           
/*  797 */           root.put("_gzipdata", gzip_data);
/*      */         }
/*      */         
/*  800 */         data = gzip_data;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  807 */       setTaskState("writing header");
/*      */       
/*  809 */       os.write(header_start);
/*      */       
/*  811 */       byte[] length_bytes = String.valueOf(data.length).getBytes();
/*      */       
/*  813 */       os.write(length_bytes);
/*      */       
/*  815 */       int header_len = header_start.length + length_bytes.length;
/*      */       
/*  817 */       setTaskState("writing content");
/*      */       
/*  819 */       if (gzip_reply)
/*      */       {
/*  821 */         os.write(HTTP_RESPONSE_END_GZIP);
/*      */         
/*  823 */         header_len += HTTP_RESPONSE_END_GZIP.length;
/*      */       }
/*      */       else {
/*  826 */         os.write(HTTP_RESPONSE_END_NOGZIP);
/*      */         
/*  828 */         header_len += HTTP_RESPONSE_END_NOGZIP.length;
/*      */       }
/*      */       
/*  831 */       os.write(data);
/*      */       
/*  833 */       this.server.updateStats(request_type, specific_torrent, input_header.length(), header_len + data.length);
/*      */     }
/*      */     finally
/*      */     {
/*  837 */       setTaskState("final os flush");
/*      */       
/*  839 */       os.flush();
/*      */     }
/*      */     
/*  842 */     return false;
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
/*      */   protected String doAuthentication(InetSocketAddress remote_ip, String url_path, String header, OutputStream os, boolean tracker)
/*      */     throws IOException
/*      */   {
/*  857 */     boolean apply_web_password = (!tracker) && (this.server.isWebPasswordEnabled());
/*  858 */     boolean apply_torrent_password = (tracker) && (this.server.isTrackerPasswordEnabled());
/*      */     
/*  860 */     if ((apply_web_password) && (this.server.isWebPasswordHTTPSOnly()) && (!this.server.isSSL()))
/*      */     {
/*      */ 
/*      */ 
/*  864 */       os.write("HTTP/1.1 403 BAD\r\n\r\nAccess Denied\r\n".getBytes());
/*      */       
/*  866 */       os.flush();
/*      */       
/*  868 */       return null;
/*      */     }
/*  870 */     if ((apply_torrent_password) || (apply_web_password))
/*      */     {
/*      */ 
/*  873 */       int x = header.indexOf("Authorization:");
/*      */       
/*  875 */       if (x == -1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  880 */         if (this.server.hasExternalAuthorisation()) {
/*      */           try
/*      */           {
/*  883 */             String resource_str = (this.server.isSSL() ? "https" : "http") + "://" + UrlUtils.convertIPV6Host(this.server.getHost()) + ":" + this.server.getPort() + url_path;
/*      */             
/*      */ 
/*      */ 
/*  887 */             URL resource = new URL(resource_str);
/*      */             
/*  889 */             if (this.server.performExternalAuthorisation(remote_ip, header, resource, "", ""))
/*      */             {
/*  891 */               return "";
/*      */             }
/*      */           }
/*      */           catch (MalformedURLException e) {
/*  895 */             Debug.printStackTrace(e);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  902 */         int p1 = header.indexOf(' ', x);
/*  903 */         int p2 = header.indexOf(' ', p1 + 1);
/*      */         
/*  905 */         String body = header.substring(p2, header.indexOf('\r', p2)).trim();
/*      */         
/*  907 */         String decoded = new String(Base64.decode(body));
/*      */         
/*      */ 
/*      */ 
/*  911 */         int cp = decoded.indexOf(':');
/*      */         
/*  913 */         String user = decoded.substring(0, cp);
/*  914 */         String pw = decoded.substring(cp + 1);
/*      */         
/*  916 */         boolean auth_failed = false;
/*      */         
/*  918 */         if (this.server.hasExternalAuthorisation())
/*      */         {
/*      */           try {
/*  921 */             String resource_str = (this.server.isSSL() ? "https" : "http") + "://" + UrlUtils.convertIPV6Host(this.server.getHost()) + ":" + this.server.getPort() + url_path;
/*      */             
/*      */ 
/*      */ 
/*  925 */             URL resource = new URL(resource_str);
/*      */             
/*  927 */             if (this.server.performExternalAuthorisation(remote_ip, header, resource, user, pw))
/*      */             {
/*  929 */               return user;
/*      */             }
/*      */           }
/*      */           catch (MalformedURLException e) {
/*  933 */             Debug.printStackTrace(e);
/*      */           }
/*      */           
/*  936 */           auth_failed = true;
/*      */         }
/*      */         
/*  939 */         if ((this.server.hasInternalAuthorisation()) && (!auth_failed))
/*      */         {
/*      */           try
/*      */           {
/*  943 */             SHA1Hasher hasher = new SHA1Hasher();
/*      */             
/*  945 */             byte[] password = pw.getBytes();
/*      */             
/*      */             byte[] encoded;
/*      */             byte[] encoded;
/*  949 */             if (password.length > 0)
/*      */             {
/*  951 */               encoded = hasher.calculateHash(password);
/*      */             }
/*      */             else
/*      */             {
/*  955 */               encoded = new byte[0];
/*      */             }
/*      */             
/*  958 */             if (user.equals("<internal>"))
/*      */             {
/*  960 */               byte[] internal_pw = Base64.decode(pw);
/*      */               
/*  962 */               if (Arrays.equals(internal_pw, this.server.getPassword()))
/*      */               {
/*  964 */                 return user;
/*      */               }
/*  966 */             } else if ((user.equalsIgnoreCase(this.server.getUsername())) && (Arrays.equals(encoded, this.server.getPassword())))
/*      */             {
/*      */ 
/*  969 */               return user;
/*      */             }
/*      */           }
/*      */           catch (Exception e) {
/*  973 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  978 */       os.write(("HTTP/1.1 401 Not Authorized\r\nWWW-Authenticate: Basic realm=\"" + this.server.getName() + "\"" + "\r\n" + "Content-Length: 15" + "\r\n" + "\r\n" + "Access Denied" + "\r\n").getBytes());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  984 */       os.flush();
/*      */       
/*  986 */       return null;
/*      */     }
/*      */     
/*      */ 
/*  990 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean isActive()
/*      */   {
/*  997 */     return true;
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
/*      */   protected boolean handleExternalRequest(InetSocketAddress local_address, InetSocketAddress remote_address, String user, String url, String header, InputStream is, OutputStream os, AsyncController async, boolean[] keep_alive)
/*      */     throws IOException
/*      */   {
/* 1014 */     URL absolute_url = new URL(this.server_url + (url.startsWith("/") ? url : new StringBuilder().append("/").append(url).toString()));
/*      */     
/* 1016 */     return this.server.handleExternalRequest(this, local_address, remote_address, user, url, absolute_url, header, is, os, async, keep_alive);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/tcp/TRTrackerServerProcessorTCP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */