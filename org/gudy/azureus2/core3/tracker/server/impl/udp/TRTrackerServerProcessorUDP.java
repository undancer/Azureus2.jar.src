/*     */ package org.gudy.azureus2.core3.tracker.server.impl.udp;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyAnnounce;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyAnnounce2;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyConnect;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyError;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyScrape;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketReplyScrape2;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestAnnounce;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestAnnounce2;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketRequestScrape;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPPacketTracker;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.udp.PRUDPTrackerCodecs;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerPeerImpl;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerProcessor;
/*     */ import org.gudy.azureus2.core3.tracker.server.impl.TRTrackerServerTorrentImpl;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ public class TRTrackerServerProcessorUDP extends TRTrackerServerProcessor
/*     */ {
/*  50 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*     */   
/*     */   public static final long CONNECTION_ID_LIFETIME = 180000L;
/*     */   
/*     */   private final TRTrackerServerUDP server;
/*     */   
/*     */   private final DatagramSocket socket;
/*     */   
/*     */   private final DatagramPacket request_dg;
/*  59 */   private static final Map<Long, connectionData> connection_id_map = new LinkedHashMap();
/*  60 */   private static final Map<String, List<connectionData>> connection_ip_map = new HashMap();
/*     */   
/*     */   private static long last_timeout_check;
/*  63 */   private static final SecureRandom random = RandomUtils.SECURE_RANDOM;
/*  64 */   private static final AEMonitor random_mon = new AEMonitor("TRTrackerServerUDP:rand");
/*     */   
/*     */   static {
/*  67 */     PRUDPTrackerCodecs.registerCodecs();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TRTrackerServerProcessorUDP(TRTrackerServerUDP _server, DatagramSocket _socket, DatagramPacket _packet)
/*     */   {
/*  76 */     this.server = _server;
/*  77 */     this.socket = _socket;
/*  78 */     this.request_dg = _packet;
/*     */   }
/*     */   
/*     */ 
/*     */   public void runSupport()
/*     */   {
/*  84 */     byte[] input_buffer = new byte[this.request_dg.getLength()];
/*     */     
/*  86 */     System.arraycopy(this.request_dg.getData(), 0, input_buffer, 0, input_buffer.length);
/*     */     
/*  88 */     int packet_data_length = input_buffer.length;
/*     */     
/*  90 */     String auth_user = null;
/*  91 */     byte[] auth_user_bytes = null;
/*  92 */     byte[] auth_hash = null;
/*     */     
/*     */ 
/*  95 */     if (this.server.isTrackerPasswordEnabled())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 100 */       if (input_buffer.length < 17)
/*     */       {
/* 102 */         Logger.log(new LogEvent(LOGID, 1, "TRTrackerServerProcessorUDP: packet received but authorisation missing"));
/*     */         
/*     */ 
/*     */ 
/* 106 */         return;
/*     */       }
/*     */       
/* 109 */       packet_data_length -= 16;
/*     */       
/* 111 */       auth_user_bytes = new byte[8];
/*     */       
/* 113 */       auth_hash = new byte[8];
/*     */       
/* 115 */       System.arraycopy(input_buffer, packet_data_length, auth_user_bytes, 0, 8);
/*     */       
/* 117 */       int user_len = 0;
/*     */       
/* 119 */       while ((user_len < 8) && (auth_user_bytes[user_len] != 0))
/*     */       {
/* 121 */         user_len++;
/*     */       }
/*     */       
/* 124 */       auth_user = new String(auth_user_bytes, 0, user_len);
/*     */       
/* 126 */       System.arraycopy(input_buffer, packet_data_length + 8, auth_hash, 0, 8);
/*     */     }
/*     */     
/* 129 */     DataInputStream is = new DataInputStream(new ByteArrayInputStream(input_buffer, 0, packet_data_length));
/*     */     try
/*     */     {
/* 132 */       String client_ip_address = this.request_dg.getAddress().getHostAddress();
/*     */       
/* 134 */       PRUDPPacketRequest request = PRUDPPacketRequest.deserialiseRequest(null, is);
/*     */       
/* 136 */       Logger.log(new LogEvent(LOGID, "TRTrackerServerProcessorUDP: packet received: " + request.getString()));
/*     */       
/*     */ 
/*     */ 
/* 140 */       PRUDPPacket reply = null;
/* 141 */       TRTrackerServerTorrentImpl torrent = null;
/*     */       
/* 143 */       if (auth_user_bytes != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 152 */         byte[] sha1_pw = null;
/*     */         
/* 154 */         if (this.server.hasExternalAuthorisation())
/*     */         {
/*     */           try {
/* 157 */             URL resource = new URL("udp://" + this.server.getHost() + ":" + this.server.getPort() + "/");
/*     */             
/* 159 */             sha1_pw = this.server.performExternalAuthorisation(resource, auth_user);
/*     */           }
/*     */           catch (MalformedURLException e)
/*     */           {
/* 163 */             Debug.printStackTrace(e);
/*     */           }
/*     */           
/*     */ 
/* 167 */           if (sha1_pw == null)
/*     */           {
/* 169 */             Logger.log(new LogEvent(LOGID, 3, "TRTrackerServerProcessorUDP: auth fails for user '" + auth_user + "'"));
/*     */             
/*     */ 
/*     */ 
/* 173 */             reply = new PRUDPPacketReplyError(request.getTransactionId(), "Access Denied");
/*     */           }
/*     */         }
/*     */         else {
/* 177 */           sha1_pw = this.server.getPassword();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 182 */         if (reply == null)
/*     */         {
/* 184 */           SHA1Hasher hasher = new SHA1Hasher();
/*     */           
/* 186 */           hasher.update(input_buffer, 0, packet_data_length);
/* 187 */           hasher.update(auth_user_bytes);
/* 188 */           hasher.update(sha1_pw);
/*     */           
/* 190 */           byte[] digest = hasher.getDigest();
/*     */           
/* 192 */           for (int i = 0; i < auth_hash.length; i++)
/*     */           {
/* 194 */             if (auth_hash[i] != digest[i])
/*     */             {
/* 196 */               Logger.log(new LogEvent(LOGID, 3, "TRTrackerServerProcessorUDP: auth fails for user '" + auth_user + "'"));
/*     */               
/*     */ 
/*     */ 
/* 200 */               reply = new PRUDPPacketReplyError(request.getTransactionId(), "Access Denied");
/*     */               
/* 202 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 208 */       int request_type = -1;
/*     */       
/* 210 */       if (reply == null)
/*     */       {
/* 212 */         if (this.server.isEnabled())
/*     */         {
/*     */           try {
/* 215 */             int type = request.getAction();
/*     */             
/* 217 */             if (type == 0)
/*     */             {
/* 219 */               reply = handleConnect(client_ip_address, request);
/*     */             }
/* 221 */             else if (type == 1)
/*     */             {
/* 223 */               Object[] x = handleAnnounceAndScrape(client_ip_address, request, 1);
/*     */               
/* 225 */               if (x == null)
/*     */               {
/* 227 */                 throw new Exception("Connection ID mismatch");
/*     */               }
/*     */               
/* 230 */               reply = (PRUDPPacket)x[0];
/* 231 */               torrent = (TRTrackerServerTorrentImpl)x[1];
/*     */               
/* 233 */               request_type = 1;
/*     */             }
/* 235 */             else if (type == 2)
/*     */             {
/* 237 */               Object[] x = handleAnnounceAndScrape(client_ip_address, request, 2);
/*     */               
/* 239 */               if (x == null)
/*     */               {
/* 241 */                 throw new Exception("Connection ID mismatch");
/*     */               }
/*     */               
/* 244 */               reply = (PRUDPPacket)x[0];
/* 245 */               torrent = (TRTrackerServerTorrentImpl)x[1];
/*     */               
/* 247 */               request_type = 2;
/*     */             }
/*     */             else
/*     */             {
/* 251 */               reply = new PRUDPPacketReplyError(request.getTransactionId(), "unsupported action");
/*     */             }
/*     */             
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 257 */             String error = e.getMessage();
/*     */             
/* 259 */             if (error == null)
/*     */             {
/* 261 */               error = e.toString();
/*     */             }
/*     */             
/* 264 */             reply = new PRUDPPacketReplyError(request.getTransactionId(), error);
/*     */           }
/*     */         }
/*     */         else {
/* 268 */           System.out.println("UDP Tracker: replying 'disabled' to " + client_ip_address);
/*     */           
/* 270 */           reply = new PRUDPPacketReplyError(request.getTransactionId(), "UDP Tracker disabled");
/*     */         }
/*     */       }
/*     */       
/* 274 */       if (reply != null)
/*     */       {
/* 276 */         InetAddress address = this.request_dg.getAddress();
/*     */         
/* 278 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */         
/* 280 */         DataOutputStream os = new DataOutputStream(baos);
/*     */         
/* 282 */         reply.serialise(os);
/*     */         
/* 284 */         byte[] output_buffer = baos.toByteArray();
/*     */         
/* 286 */         DatagramPacket reply_packet = new DatagramPacket(output_buffer, output_buffer.length, address, this.request_dg.getPort());
/*     */         
/* 288 */         this.socket.send(reply_packet);
/*     */         
/* 290 */         this.server.updateStats(request_type, torrent, input_buffer.length, output_buffer.length);
/*     */       }
/*     */       return;
/*     */     }
/*     */     catch (Throwable e) {
/* 295 */       Logger.log(new LogEvent(LOGID, "TRTrackerServerProcessorUDP: processing fails", e));
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 300 */         is.close();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
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
/*     */   protected long allocateConnectionId(String client_address)
/*     */   {
/*     */     try
/*     */     {
/* 318 */       random_mon.enter();
/*     */       
/* 320 */       long id = random.nextLong();
/*     */       
/* 322 */       Long new_key = new Long(id);
/*     */       
/* 324 */       connectionData new_data = new connectionData(client_address, id, null);
/*     */       
/*     */ 
/*     */ 
/* 328 */       if (new_data.getTime() - last_timeout_check > 500L)
/*     */       {
/* 330 */         last_timeout_check = new_data.getTime();
/*     */         
/* 332 */         Iterator<Long> it = connection_id_map.keySet().iterator();
/*     */         
/* 334 */         while (it.hasNext())
/*     */         {
/* 336 */           Long key = (Long)it.next();
/*     */           
/* 338 */           connectionData data = (connectionData)connection_id_map.get(key);
/*     */           
/* 340 */           if (new_data.getTime() - data.getTime() <= 180000L) {
/*     */             break;
/*     */           }
/*     */           
/* 344 */           it.remove();
/*     */           
/* 346 */           List<connectionData> cds = (List)connection_ip_map.get(client_address);
/*     */           
/* 348 */           if (cds != null)
/*     */           {
/* 350 */             Iterator<connectionData> it2 = cds.iterator();
/*     */             
/* 352 */             while (it2.hasNext())
/*     */             {
/* 354 */               if (((connectionData)it2.next()).getID() == key.longValue())
/*     */               {
/* 356 */                 it2.remove();
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 362 */             if (cds.size() == 0)
/*     */             {
/* 364 */               connection_ip_map.remove(client_address);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 376 */       List<connectionData> cds = (List)connection_ip_map.get(client_address);
/*     */       
/* 378 */       if (cds == null)
/*     */       {
/* 380 */         cds = new ArrayList();
/*     */         
/* 382 */         connection_ip_map.put(client_address, cds);
/*     */       }
/*     */       
/* 385 */       cds.add(new_data);
/*     */       connectionData dead;
/* 387 */       if (cds.size() > 512)
/*     */       {
/* 389 */         dead = (connectionData)cds.remove(0);
/*     */         
/* 391 */         connection_id_map.remove(Long.valueOf(dead.getID()));
/*     */       }
/*     */       
/* 394 */       connection_id_map.put(new_key, new_data);
/*     */       
/*     */ 
/*     */ 
/* 398 */       return id;
/*     */     }
/*     */     finally
/*     */     {
/* 402 */       random_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean checkConnectionId(String client_address, long id)
/*     */   {
/*     */     try
/*     */     {
/* 412 */       random_mon.enter();
/*     */       
/* 414 */       Long key = new Long(id);
/*     */       
/* 416 */       connectionData data = (connectionData)connection_id_map.get(key);
/*     */       boolean bool1;
/* 418 */       if (data == null)
/*     */       {
/*     */ 
/*     */ 
/* 422 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 426 */       if (SystemTime.getMonotonousTime() - data.getTime() > 180000L)
/*     */       {
/* 428 */         return false;
/*     */       }
/*     */       
/*     */ 
/* 432 */       boolean ok = data.getAddress().equals(client_address);
/*     */       
/*     */ 
/*     */ 
/* 436 */       return ok;
/*     */     }
/*     */     finally
/*     */     {
/* 440 */       random_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PRUDPPacket handleConnect(String client_ip_address, PRUDPPacketRequest request)
/*     */   {
/* 449 */     long conn_id = allocateConnectionId(client_ip_address);
/*     */     
/* 451 */     PRUDPPacket reply = new PRUDPPacketReplyConnect(request.getTransactionId(), conn_id);
/*     */     
/* 453 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object[] handleAnnounceAndScrape(String client_ip_address, PRUDPPacketRequest request, int request_type)
/*     */     throws Exception
/*     */   {
/* 466 */     if (!checkConnectionId(client_ip_address, request.getConnectionId()))
/*     */     {
/* 468 */       return null;
/*     */     }
/*     */     
/* 471 */     List hashbytes = new ArrayList();
/* 472 */     HashWrapper peer_id = null;
/* 473 */     int port = 0;
/* 474 */     String event = null;
/*     */     
/* 476 */     long uploaded = 0L;
/* 477 */     long downloaded = 0L;
/* 478 */     long left = 0L;
/* 479 */     int num_want = -1;
/*     */     
/* 481 */     String key = null;
/*     */     
/* 483 */     if (request_type == 1)
/*     */     {
/* 485 */       if (PRUDPPacketTracker.VERSION == 1) {
/* 486 */         PRUDPPacketRequestAnnounce announce = (PRUDPPacketRequestAnnounce)request;
/*     */         
/* 488 */         hashbytes.add(announce.getHash());
/*     */         
/* 490 */         peer_id = new HashWrapper(announce.getPeerId());
/*     */         
/* 492 */         port = announce.getPort();
/*     */         
/* 494 */         int i_event = announce.getEvent();
/*     */         
/* 496 */         switch (i_event)
/*     */         {
/*     */         case 2: 
/* 499 */           event = "started";
/* 500 */           break;
/*     */         
/*     */ 
/*     */         case 3: 
/* 504 */           event = "stopped";
/* 505 */           break;
/*     */         
/*     */ 
/*     */         case 1: 
/* 509 */           event = "completed";
/*     */         }
/*     */         
/*     */         
/*     */ 
/* 514 */         uploaded = announce.getUploaded();
/*     */         
/* 516 */         downloaded = announce.getDownloaded();
/*     */         
/* 518 */         left = announce.getLeft();
/*     */         
/* 520 */         num_want = announce.getNumWant();
/*     */         
/* 522 */         int i_ip = announce.getIPAddress();
/*     */         
/* 524 */         if (i_ip != 0)
/*     */         {
/* 526 */           client_ip_address = PRHelpers.intToAddress(i_ip);
/*     */         }
/*     */       }
/*     */       else {
/* 530 */         PRUDPPacketRequestAnnounce2 announce = (PRUDPPacketRequestAnnounce2)request;
/*     */         
/* 532 */         hashbytes.add(announce.getHash());
/*     */         
/* 534 */         peer_id = new HashWrapper(announce.getPeerId());
/*     */         
/* 536 */         port = announce.getPort();
/*     */         
/* 538 */         int i_event = announce.getEvent();
/*     */         
/* 540 */         switch (i_event)
/*     */         {
/*     */         case 2: 
/* 543 */           event = "started";
/* 544 */           break;
/*     */         
/*     */ 
/*     */         case 3: 
/* 548 */           event = "stopped";
/* 549 */           break;
/*     */         
/*     */ 
/*     */         case 1: 
/* 553 */           event = "completed";
/*     */         }
/*     */         
/*     */         
/*     */ 
/* 558 */         uploaded = announce.getUploaded();
/*     */         
/* 560 */         downloaded = announce.getDownloaded();
/*     */         
/* 562 */         left = announce.getLeft();
/*     */         
/* 564 */         num_want = announce.getNumWant();
/*     */         
/* 566 */         int i_ip = announce.getIPAddress();
/*     */         
/* 568 */         if (i_ip != 0)
/*     */         {
/* 570 */           client_ip_address = PRHelpers.intToAddress(i_ip);
/*     */         }
/*     */         
/* 573 */         key = "" + announce.getKey();
/*     */       }
/*     */     }
/*     */     else {
/* 577 */       PRUDPPacketRequestScrape scrape = (PRUDPPacketRequestScrape)request;
/*     */       
/* 579 */       hashbytes.addAll(scrape.getHashes());
/*     */     }
/*     */     
/* 582 */     Map[] root_out = new Map[1];
/* 583 */     TRTrackerServerPeerImpl[] peer_out = new TRTrackerServerPeerImpl[1];
/*     */     
/* 585 */     TRTrackerServerTorrentImpl torrent = processTrackerRequest(this.server, "", root_out, peer_out, request_type, (byte[][])hashbytes.toArray(new byte[0][0]), null, null, peer_id, false, (byte)0, key, event, false, port, 0, 0, client_ip_address, client_ip_address, downloaded, uploaded, left, num_want, (byte)0, (byte)1, 0, null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 602 */     Map root = root_out[0];
/*     */     
/* 604 */     if (request_type == 1)
/*     */     {
/* 606 */       if (PRUDPPacketTracker.VERSION == 1) {
/* 607 */         PRUDPPacketReplyAnnounce reply = new PRUDPPacketReplyAnnounce(request.getTransactionId());
/*     */         
/* 609 */         reply.setInterval(((Long)root.get("interval")).intValue());
/*     */         
/* 611 */         List peers = (List)root.get("peers");
/*     */         
/* 613 */         int[] addresses = new int[peers.size()];
/* 614 */         short[] ports = new short[addresses.length];
/*     */         
/* 616 */         for (int i = 0; i < addresses.length; i++)
/*     */         {
/* 618 */           Map peer = (Map)peers.get(i);
/*     */           
/* 620 */           addresses[i] = PRHelpers.addressToInt(new String((byte[])(byte[])peer.get("ip")));
/*     */           
/* 622 */           ports[i] = ((Long)peer.get("port")).shortValue();
/*     */         }
/*     */         
/* 625 */         reply.setPeers(addresses, ports);
/*     */         
/* 627 */         return new Object[] { reply, torrent };
/*     */       }
/*     */       
/*     */ 
/* 631 */       PRUDPPacketReplyAnnounce2 reply = new PRUDPPacketReplyAnnounce2(request.getTransactionId());
/*     */       
/* 633 */       reply.setInterval(((Long)root.get("interval")).intValue());
/*     */       
/* 635 */       boolean local_scrape = client_ip_address.equals("127.0.0.1");
/*     */       
/* 637 */       Map scrape_details = torrent.exportScrapeToMap("", client_ip_address, !local_scrape);
/*     */       
/* 639 */       int seeders = ((Long)scrape_details.get("complete")).intValue();
/* 640 */       int leechers = ((Long)scrape_details.get("incomplete")).intValue();
/*     */       
/* 642 */       reply.setLeechersSeeders(leechers, seeders);
/*     */       
/* 644 */       List peers = (List)root.get("peers");
/*     */       
/* 646 */       int[] addresses = new int[peers.size()];
/* 647 */       short[] ports = new short[addresses.length];
/*     */       
/* 649 */       for (int i = 0; i < addresses.length; i++)
/*     */       {
/* 651 */         Map peer = (Map)peers.get(i);
/*     */         
/* 653 */         addresses[i] = PRHelpers.addressToInt(new String((byte[])(byte[])peer.get("ip")));
/*     */         
/* 655 */         ports[i] = ((Long)peer.get("port")).shortValue();
/*     */       }
/*     */       
/* 658 */       reply.setPeers(addresses, ports);
/*     */       
/* 660 */       return new Object[] { reply, torrent };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 665 */     if (PRUDPPacketTracker.VERSION == 1)
/*     */     {
/* 667 */       PRUDPPacketReplyScrape reply = new PRUDPPacketReplyScrape(request.getTransactionId());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 678 */       Map files = (Map)root.get("files");
/*     */       
/* 680 */       byte[][] hashes = new byte[files.size()][];
/* 681 */       int[] s_complete = new int[hashes.length];
/* 682 */       int[] s_downloaded = new int[hashes.length];
/* 683 */       int[] s_incomplete = new int[hashes.length];
/*     */       
/* 685 */       Iterator it = files.keySet().iterator();
/*     */       
/* 687 */       int pos = 0;
/*     */       
/* 689 */       while (it.hasNext())
/*     */       {
/* 691 */         String hash_str = (String)it.next();
/*     */         
/* 693 */         hashes[pos] = hash_str.getBytes("ISO-8859-1");
/*     */         
/* 695 */         Map details = (Map)files.get(hash_str);
/*     */         
/* 697 */         s_complete[pos] = ((Long)details.get("complete")).intValue();
/* 698 */         s_incomplete[pos] = ((Long)details.get("incomplete")).intValue();
/* 699 */         s_downloaded[pos] = ((Long)details.get("downloaded")).intValue();
/*     */         
/* 701 */         pos++;
/*     */       }
/*     */       
/* 704 */       reply.setDetails(hashes, s_complete, s_downloaded, s_incomplete);
/*     */       
/* 706 */       return new Object[] { reply, torrent };
/*     */     }
/*     */     
/*     */ 
/* 710 */     PRUDPPacketReplyScrape2 reply = new PRUDPPacketReplyScrape2(request.getTransactionId());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 721 */     Map files = (Map)root.get("files");
/*     */     
/* 723 */     int[] s_complete = new int[files.size()];
/* 724 */     int[] s_downloaded = new int[s_complete.length];
/* 725 */     int[] s_incomplete = new int[s_complete.length];
/*     */     
/* 727 */     Iterator it = files.keySet().iterator();
/*     */     
/* 729 */     int pos = 0;
/*     */     
/* 731 */     while (it.hasNext())
/*     */     {
/* 733 */       String hash_str = (String)it.next();
/*     */       
/* 735 */       Map details = (Map)files.get(hash_str);
/*     */       
/* 737 */       s_complete[pos] = ((Long)details.get("complete")).intValue();
/* 738 */       s_incomplete[pos] = ((Long)details.get("incomplete")).intValue();
/* 739 */       s_downloaded[pos] = ((Long)details.get("downloaded")).intValue();
/*     */       
/* 741 */       pos++;
/*     */     }
/*     */     
/* 744 */     reply.setDetails(s_complete, s_downloaded, s_incomplete);
/*     */     
/* 746 */     return new Object[] { reply, torrent };
/*     */   }
/*     */   
/*     */ 
/*     */   public void interruptTask() {}
/*     */   
/*     */ 
/*     */   protected static class connectionData
/*     */   {
/*     */     private final String address;
/*     */     
/*     */     private final long id;
/*     */     
/*     */     private final long time;
/*     */     
/*     */     private connectionData(String _address, long _id)
/*     */     {
/* 763 */       this.address = _address;
/* 764 */       this.id = _id;
/*     */       
/* 766 */       this.time = SystemTime.getMonotonousTime();
/*     */     }
/*     */     
/*     */ 
/*     */     private String getAddress()
/*     */     {
/* 772 */       return this.address;
/*     */     }
/*     */     
/*     */ 
/*     */     private long getID()
/*     */     {
/* 778 */       return this.id;
/*     */     }
/*     */     
/*     */ 
/*     */     private long getTime()
/*     */     {
/* 784 */       return this.time;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/udp/TRTrackerServerProcessorUDP.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */