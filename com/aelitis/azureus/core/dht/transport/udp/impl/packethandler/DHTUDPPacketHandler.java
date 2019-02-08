/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketReply;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketRequest;
/*     */ import com.aelitis.azureus.core.util.DNSUtils;
/*     */ import com.aelitis.azureus.core.util.DNSUtils.DNSUtilsIntf;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerRequest;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReceiver;
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTUDPPacketHandler
/*     */   implements DHTUDPPacketHandlerStub
/*     */ {
/*     */   private final DHTUDPPacketHandlerFactory factory;
/*     */   final int network;
/*     */   private final PRUDPPacketHandler packet_handler;
/*     */   private final DHTUDPRequestHandler request_handler;
/*     */   private final DHTUDPPacketHandlerStats stats;
/*  64 */   private boolean test_network_alive = true;
/*     */   
/*     */   private static final int BLOOM_FILTER_SIZE = 10000;
/*     */   
/*     */   private static final int BLOOM_ROTATION_PERIOD = 180000;
/*     */   
/*     */   private BloomFilter bloom1;
/*     */   
/*     */   private BloomFilter bloom2;
/*     */   
/*     */   private long last_bloom_rotation_time;
/*     */   
/*     */   private boolean destroyed;
/*     */   
/*     */ 
/*     */   protected DHTUDPPacketHandler(DHTUDPPacketHandlerFactory _factory, int _network, PRUDPPacketHandler _packet_handler, DHTUDPRequestHandler _request_handler)
/*     */   {
/*  81 */     this.factory = _factory;
/*  82 */     this.network = _network;
/*  83 */     this.packet_handler = _packet_handler;
/*  84 */     this.request_handler = _request_handler;
/*     */     
/*  86 */     this.bloom1 = BloomFilterFactory.createAddOnly(10000);
/*  87 */     this.bloom2 = BloomFilterFactory.createAddOnly(10000);
/*     */     
/*  89 */     this.stats = new DHTUDPPacketHandlerStats(this.packet_handler);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDestroyed()
/*     */   {
/*  95 */     return this.destroyed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void testNetworkAlive(boolean alive)
/*     */   {
/* 102 */     this.test_network_alive = alive;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTUDPRequestHandler getRequestHandler()
/*     */   {
/* 108 */     return this.request_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   public PRUDPPacketHandler getPacketHandler()
/*     */   {
/* 114 */     return this.packet_handler;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getNetwork()
/*     */   {
/* 120 */     return this.network;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateBloom(InetSocketAddress destination_address)
/*     */   {
/* 129 */     if (!destination_address.isUnresolved())
/*     */     {
/* 131 */       long diff = SystemTime.getCurrentTime() - this.last_bloom_rotation_time;
/*     */       
/* 133 */       if ((diff < 0L) || (diff > 180000L))
/*     */       {
/*     */ 
/*     */ 
/* 137 */         this.bloom1 = this.bloom2;
/*     */         
/* 139 */         this.bloom2 = BloomFilterFactory.createAddOnly(10000);
/*     */         
/* 141 */         this.last_bloom_rotation_time = SystemTime.getCurrentTime();
/*     */       }
/*     */       
/* 144 */       byte[] address_bytes = destination_address.getAddress().getAddress();
/*     */       
/* 146 */       this.bloom1.add(address_bytes);
/* 147 */       this.bloom2.add(address_bytes);
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
/*     */   public void sendAndReceive(DHTUDPPacketRequest request, InetSocketAddress destination_address, final DHTUDPPacketReceiver receiver, long timeout, int priority)
/*     */     throws DHTUDPPacketHandlerException
/*     */   {
/* 161 */     if (this.destroyed) {
/* 162 */       throw new DHTUDPPacketHandlerException("packet handler is destroyed");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 167 */     destination_address = AddressUtils.adjustDHTAddress(destination_address, true);
/*     */     try
/*     */     {
/* 170 */       request.setNetwork(this.network);
/*     */       
/* 172 */       if (this.test_network_alive)
/*     */       {
/* 174 */         if ((destination_address.isUnresolved()) && (destination_address.getHostName().equals("dht6.vuze.com")))
/*     */         {
/* 176 */           tunnelIPv6SeedRequest(request, destination_address, receiver);
/*     */         }
/*     */         else
/*     */         {
/* 180 */           updateBloom(destination_address);
/*     */           
/* 182 */           this.packet_handler.sendAndReceive(request, destination_address, new PRUDPPacketReceiver()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             public void packetReceived(PRUDPPacketHandlerRequest request, PRUDPPacket packet, InetSocketAddress from_address)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 193 */               DHTUDPPacketReply reply = (DHTUDPPacketReply)packet;
/*     */               
/* 195 */               DHTUDPPacketHandler.this.stats.packetReceived(reply.getSerialisedSize());
/*     */               
/* 197 */               if (reply.getNetwork() == DHTUDPPacketHandler.this.network)
/*     */               {
/* 199 */                 receiver.packetReceived(reply, from_address, request.getElapsedTime());
/*     */               }
/*     */               else
/*     */               {
/* 203 */                 Debug.out("Non-matching network reply received: expected=" + DHTUDPPacketHandler.this.network + ", actual=" + reply.getNetwork());
/*     */                 
/* 205 */                 receiver.error(new DHTUDPPacketHandlerException(new Exception("Non-matching network reply received")));
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 213 */             public void error(PRUDPPacketHandlerException e) { receiver.error(new DHTUDPPacketHandlerException(e)); } }, timeout, priority);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 221 */         receiver.error(new DHTUDPPacketHandlerException(new Exception("Test network disabled")));
/*     */       }
/*     */     }
/*     */     catch (PRUDPPacketHandlerException e)
/*     */     {
/* 226 */       throw new DHTUDPPacketHandlerException(e);
/*     */     }
/*     */     finally
/*     */     {
/* 230 */       this.stats.packetSent(request.getSerialisedSize());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send(DHTUDPPacketRequest request, InetSocketAddress destination_address)
/*     */     throws DHTUDPPacketHandlerException
/*     */   {
/* 242 */     if (this.destroyed) {
/* 243 */       throw new DHTUDPPacketHandlerException("packet handler is destroyed");
/*     */     }
/*     */     
/* 246 */     destination_address = AddressUtils.adjustDHTAddress(destination_address, true);
/*     */     
/* 248 */     updateBloom(destination_address);
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 254 */       request.setNetwork(this.network);
/*     */       
/* 256 */       if (this.test_network_alive)
/*     */       {
/* 258 */         this.packet_handler.send(request, destination_address);
/*     */       }
/*     */     }
/*     */     catch (PRUDPPacketHandlerException e)
/*     */     {
/* 263 */       throw new DHTUDPPacketHandlerException(e);
/*     */     }
/*     */     finally
/*     */     {
/* 267 */       this.stats.packetSent(request.getSerialisedSize());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send(DHTUDPPacketReply reply, InetSocketAddress destination_address)
/*     */     throws DHTUDPPacketHandlerException
/*     */   {
/* 278 */     if (this.destroyed) {
/* 279 */       throw new DHTUDPPacketHandlerException("packet handler is destroyed");
/*     */     }
/*     */     
/* 282 */     destination_address = AddressUtils.adjustDHTAddress(destination_address, true);
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 287 */       reply.setNetwork(this.network);
/*     */       
/*     */ 
/*     */ 
/* 291 */       if (this.test_network_alive)
/*     */       {
/* 293 */         this.packet_handler.send(reply, destination_address);
/*     */       }
/*     */     }
/*     */     catch (PRUDPPacketHandlerException e)
/*     */     {
/* 298 */       throw new DHTUDPPacketHandlerException(e);
/*     */     }
/*     */     finally
/*     */     {
/* 302 */       this.stats.packetSent(reply.getSerialisedSize());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void receive(DHTUDPPacketRequest request)
/*     */   {
/* 310 */     if (this.destroyed) {
/* 311 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 316 */     if (this.test_network_alive)
/*     */     {
/* 318 */       request.setAddress(AddressUtils.adjustDHTAddress(request.getAddress(), false));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 323 */       byte[] bloom_key = request.getAddress().getAddress().getAddress();
/*     */       
/* 325 */       boolean alien = !this.bloom1.contains(bloom_key);
/*     */       
/* 327 */       if (alien)
/*     */       {
/*     */ 
/*     */ 
/* 331 */         this.bloom1.add(bloom_key);
/* 332 */         this.bloom2.add(bloom_key);
/*     */       }
/*     */       
/* 335 */       this.stats.packetReceived(request.getSerialisedSize());
/*     */       
/* 337 */       this.request_handler.process(request, alien);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDelays(int send_delay, int receive_delay, int queued_request_timeout)
/*     */   {
/* 349 */     this.packet_handler.setDelays(send_delay, receive_delay, queued_request_timeout);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 355 */     this.factory.destroy(this);
/*     */     
/* 357 */     this.destroyed = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public DHTUDPPacketHandlerStats getStats()
/*     */   {
/* 363 */     return this.stats;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void tunnelIPv6SeedRequest(DHTUDPPacketRequest request, InetSocketAddress destination_address, DHTUDPPacketReceiver receiver)
/*     */     throws DHTUDPPacketHandlerException
/*     */   {
/* 375 */     if (this.destroyed) {
/* 376 */       throw new DHTUDPPacketHandlerException("packet handler is destroyed");
/*     */     }
/*     */     
/* 379 */     if (request.getAction() != 1028)
/*     */     {
/* 381 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 385 */       long start = SystemTime.getMonotonousTime();
/*     */       
/* 387 */       ByteArrayOutputStream baos_req = new ByteArrayOutputStream();
/*     */       
/* 389 */       DataOutputStream dos = new DataOutputStream(baos_req);
/*     */       
/* 391 */       request.serialise(dos);
/*     */       
/* 393 */       dos.close();
/*     */       
/* 395 */       byte[] request_bytes = baos_req.toByteArray();
/*     */       
/* 397 */       String host = "dht6tunnel.vuze.com";
/*     */       
/* 399 */       DNSUtils.DNSUtilsIntf dns_utils = DNSUtils.getSingleton();
/*     */       
/* 401 */       if (dns_utils != null) {
/*     */         try
/*     */         {
/* 404 */           host = dns_utils.getIPV6ByName(host).getHostAddress();
/*     */           
/* 406 */           host = UrlUtils.convertIPV6Host(host);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/* 412 */       URL url = new URL("http://" + host + "/dht?port=" + this.packet_handler.getPort() + "&request=" + Base32.encode(request_bytes));
/*     */       
/* 414 */       HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/*     */       
/* 416 */       connection.setConnectTimeout(10000);
/* 417 */       connection.setReadTimeout(20000);
/*     */       
/* 419 */       InputStream is = connection.getInputStream();
/*     */       
/* 421 */       ByteArrayOutputStream baos_rep = new ByteArrayOutputStream(1000);
/*     */       
/* 423 */       byte[] buffer = new byte[' '];
/*     */       
/*     */       for (;;)
/*     */       {
/* 427 */         int len = is.read(buffer);
/*     */         
/* 429 */         if (len <= 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 434 */         baos_rep.write(buffer, 0, len);
/*     */       }
/*     */       
/* 437 */       byte[] reply_bytes = baos_rep.toByteArray();
/*     */       
/* 439 */       if (reply_bytes.length > 0)
/*     */       {
/* 441 */         DHTUDPPacketReply reply = (DHTUDPPacketReply)PRUDPPacketReply.deserialiseReply(this.packet_handler, destination_address, new DataInputStream(new ByteArrayInputStream(reply_bytes)));
/*     */         
/*     */ 
/*     */ 
/* 445 */         receiver.packetReceived(reply, destination_address, SystemTime.getMonotonousTime() - start);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 449 */       throw new DHTUDPPacketHandlerException("Tunnel failed", e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/packethandler/DHTUDPPacketHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */