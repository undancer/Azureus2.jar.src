/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelector;
/*     */ import com.aelitis.azureus.core.proxy.AEProxySelectorFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Proxy.Type;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxyLoginHandler
/*     */ {
/*     */   private static final int READ_DONE = 0;
/*     */   private static final int READ_NOT_DONE = 1;
/*     */   private static final int READ_NO_PROGRESS = 2;
/*     */   public static InetSocketAddress DEFAULT_SOCKS_SERVER_ADDRESS;
/*     */   private static String default_socks_version;
/*     */   private static String default_socks_user;
/*     */   private static String default_socks_password;
/*     */   
/*     */   static
/*     */   {
/*  58 */     COConfigurationManager.addListener(new COConfigurationListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void configurationSaved() {}
/*     */ 
/*     */ 
/*     */ 
/*  67 */     });
/*  68 */     readConfig();
/*     */   }
/*     */   
/*     */ 
/*     */   static void readConfig()
/*     */   {
/*  74 */     boolean socks_same = COConfigurationManager.getBooleanParameter("Proxy.Data.Same");
/*  75 */     String socks_host = COConfigurationManager.getStringParameter(socks_same ? "Proxy.Host" : "Proxy.Data.Host");
/*  76 */     int socks_port = 0;
/*     */     try {
/*  78 */       String socks_port_str = COConfigurationManager.getStringParameter(socks_same ? "Proxy.Port" : "Proxy.Data.Port");
/*     */       
/*  80 */       socks_port_str = socks_port_str.trim();
/*     */       
/*  82 */       if (socks_port_str.length() > 0)
/*     */       {
/*  84 */         socks_port = Integer.parseInt(COConfigurationManager.getStringParameter(socks_same ? "Proxy.Port" : "Proxy.Data.Port")); }
/*     */     } catch (Throwable e) {
/*  86 */       Debug.printStackTrace(e);
/*     */     }
/*  88 */     DEFAULT_SOCKS_SERVER_ADDRESS = new InetSocketAddress(socks_host, socks_port);
/*     */     
/*  90 */     default_socks_version = COConfigurationManager.getStringParameter("Proxy.Data.SOCKS.version");
/*  91 */     default_socks_user = COConfigurationManager.getStringParameter(socks_same ? "Proxy.Username" : "Proxy.Data.Username");
/*  92 */     if (default_socks_user.trim().equalsIgnoreCase("<none>")) {
/*  93 */       default_socks_user = "";
/*     */     }
/*  95 */     default_socks_password = COConfigurationManager.getStringParameter(socks_same ? "Proxy.Password" : "Proxy.Data.Password");
/*     */   }
/*     */   
/*     */ 
/*  99 */   private static final AEProxySelector proxy_selector = AEProxySelectorFactory.getSelector();
/*     */   
/*     */   private final TCPTransportImpl proxy_connection;
/*     */   
/*     */   private final InetSocketAddress remote_address;
/*     */   private final ProxyListener proxy_listener;
/*     */   private final String mapped_ip;
/* 106 */   private int socks5_handshake_phase = 0;
/*     */   
/*     */   private int socks5_address_length;
/* 109 */   private long read_start_time = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */   private final String socks_version;
/*     */   
/*     */ 
/*     */ 
/*     */   private final String socks_user;
/*     */   
/*     */ 
/*     */ 
/*     */   private final String socks_password;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProxyLoginHandler(TCPTransportImpl proxy_connection, InetSocketAddress remote_address, ProxyListener listener)
/*     */   {
/* 128 */     this(proxy_connection, remote_address, listener, default_socks_version, default_socks_user, default_socks_password);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ProxyLoginHandler(TCPTransportImpl _proxy_connection, InetSocketAddress _remote_address, ProxyListener _listener, String _socks_version, String _socks_user, String _socks_password)
/*     */   {
/* 140 */     this.proxy_connection = _proxy_connection;
/* 141 */     this.remote_address = _remote_address;
/* 142 */     this.proxy_listener = _listener;
/* 143 */     this.socks_version = _socks_version;
/* 144 */     this.socks_user = _socks_user;
/* 145 */     this.socks_password = _socks_password;
/*     */     
/* 147 */     if ((this.remote_address.isUnresolved()) || (this.remote_address.getAddress() == null))
/*     */     {
/* 149 */       this.mapped_ip = AEProxyFactory.getAddressMapper().internalise(this.remote_address.getHostName());
/*     */     }
/*     */     else
/*     */     {
/* 153 */       this.mapped_ip = AddressUtils.getHostNameNoResolve(this.remote_address);
/*     */     }
/*     */     
/* 156 */     if (this.socks_version.equals("V4")) {
/*     */       try {
/* 158 */         doSocks4Login(createSocks4Message());
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 162 */         this.proxy_listener.connectFailure(t);
/*     */       }
/*     */       
/* 165 */     } else if (this.socks_version.equals("V4a")) {
/*     */       try {
/* 167 */         doSocks4Login(createSocks4aMessage());
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 171 */         this.proxy_listener.connectFailure(t);
/*     */       }
/*     */       
/*     */     } else {
/* 175 */       doSocks5Login();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static InetSocketAddress getProxyAddress(InetSocketAddress target)
/*     */   {
/* 184 */     Proxy p = proxy_selector.getSOCKSProxy(DEFAULT_SOCKS_SERVER_ADDRESS, target);
/*     */     
/*     */ 
/*     */ 
/* 188 */     if (p.type() == Proxy.Type.SOCKS)
/*     */     {
/* 190 */       SocketAddress sa = p.address();
/*     */       
/* 192 */       if ((sa instanceof InetSocketAddress))
/*     */       {
/* 194 */         return (InetSocketAddress)sa;
/*     */       }
/*     */     }
/*     */     
/* 198 */     return DEFAULT_SOCKS_SERVER_ADDRESS;
/*     */   }
/*     */   
/*     */   private void doSocks4Login(final ByteBuffer[] data) {
/*     */     try {
/* 203 */       sendMessage(data[0]);
/*     */       
/*     */ 
/* 206 */       TCPNetworkManager.getSingleton().getReadSelector().register(this.proxy_connection.getSocketChannel(), new VirtualChannelSelector.VirtualSelectorListener() {
/*     */         public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment) {
/*     */           try {
/* 209 */             int result = ProxyLoginHandler.this.readMessage(data[1]);
/*     */             
/* 211 */             if (result == 0) {
/* 212 */               TCPNetworkManager.getSingleton().getReadSelector().cancel(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/* 213 */               ProxyLoginHandler.this.parseSocks4Reply(data[1]);
/* 214 */               ProxyLoginHandler.this.proxy_listener.connectSuccess();
/*     */             }
/*     */             else {
/* 217 */               TCPNetworkManager.getSingleton().getReadSelector().resumeSelects(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/*     */             }
/*     */             
/* 220 */             return result != 2;
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 224 */             TCPNetworkManager.getSingleton().getReadSelector().cancel(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/* 225 */             ProxyLoginHandler.this.proxy_listener.connectFailure(t); }
/* 226 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */         public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */         {
/* 232 */           TCPNetworkManager.getSingleton().getReadSelector().cancel(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/* 233 */           ProxyLoginHandler.this.proxy_listener.connectFailure(msg); } }, null);
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 239 */       SocketChannel chan = this.proxy_connection.getSocketChannel();
/* 240 */       if (chan != null) {
/* 241 */         TCPNetworkManager.getSingleton().getReadSelector().cancel(chan);
/*     */       }
/* 243 */       this.proxy_listener.connectFailure(t);
/*     */     }
/*     */   }
/*     */   
/*     */   private void doSocks5Login()
/*     */   {
/*     */     try
/*     */     {
/* 251 */       final ArrayList data = new ArrayList(2);
/*     */       
/* 253 */       ByteBuffer[] header = createSocks5Message();
/* 254 */       data.add(header[0]);
/* 255 */       data.add(header[1]);
/*     */       
/* 257 */       sendMessage((ByteBuffer)data.get(0));
/*     */       
/*     */ 
/* 260 */       TCPNetworkManager.getSingleton().getReadSelector().register(this.proxy_connection.getSocketChannel(), new VirtualChannelSelector.VirtualSelectorListener() {
/*     */         public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment) {
/*     */           try {
/* 263 */             int result = ProxyLoginHandler.this.readMessage((ByteBuffer)data.get(1));
/*     */             
/* 265 */             if (result == 0) {
/* 266 */               boolean done = ProxyLoginHandler.this.parseSocks5Reply((ByteBuffer)data.get(1));
/*     */               
/* 268 */               if (done) {
/* 269 */                 TCPNetworkManager.getSingleton().getReadSelector().cancel(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/* 270 */                 ProxyLoginHandler.this.proxy_listener.connectSuccess();
/*     */               }
/*     */               else {
/* 273 */                 ByteBuffer[] raw = ProxyLoginHandler.this.createSocks5Message();
/* 274 */                 data.set(0, raw[0]);
/* 275 */                 data.set(1, raw[1]);
/*     */                 
/* 277 */                 if (raw[0] != null) ProxyLoginHandler.this.sendMessage(raw[0]);
/* 278 */                 TCPNetworkManager.getSingleton().getReadSelector().resumeSelects(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/*     */               }
/*     */             }
/*     */             else {
/* 282 */               TCPNetworkManager.getSingleton().getReadSelector().resumeSelects(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/*     */             }
/*     */             
/* 285 */             return result != 2;
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/* 289 */             TCPNetworkManager.getSingleton().getReadSelector().cancel(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/* 290 */             ProxyLoginHandler.this.proxy_listener.connectFailure(t); }
/* 291 */           return false;
/*     */         }
/*     */         
/*     */ 
/*     */         public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*     */         {
/* 297 */           TCPNetworkManager.getSingleton().getReadSelector().cancel(ProxyLoginHandler.this.proxy_connection.getSocketChannel());
/* 298 */           ProxyLoginHandler.this.proxy_listener.connectFailure(msg); } }, null);
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 304 */       SocketChannel chan = this.proxy_connection.getSocketChannel();
/* 305 */       if (chan != null) {
/* 306 */         TCPNetworkManager.getSingleton().getReadSelector().cancel(chan);
/*     */       }
/* 308 */       this.proxy_listener.connectFailure(t);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void parseSocks4Reply(ByteBuffer reply)
/*     */     throws IOException
/*     */   {
/* 316 */     byte ver = reply.get();
/* 317 */     byte resp = reply.get();
/*     */     
/* 319 */     if ((ver != 0) || (resp != 90)) {
/* 320 */       throw new IOException("SOCKS 4(a): connection declined [" + ver + "/" + resp + "]");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void sendMessage(ByteBuffer msg)
/*     */     throws IOException
/*     */   {
/* 329 */     long start_time = SystemTime.getCurrentTime();
/*     */     
/* 331 */     while (msg.hasRemaining()) {
/* 332 */       if (this.proxy_connection.write(new ByteBuffer[] { msg }, 0, 1) < 1L) {
/* 333 */         if (SystemTime.getCurrentTime() - start_time > 30000L) {
/* 334 */           String error = "proxy handshake message send timed out after 30sec";
/*     */           
/* 336 */           throw new IOException(error);
/*     */         }
/*     */         try {
/* 339 */           Thread.sleep(10L); } catch (Throwable t) {} t.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private int readMessage(ByteBuffer msg)
/*     */     throws IOException
/*     */   {
/* 347 */     if (this.read_start_time == 0L) { this.read_start_time = SystemTime.getCurrentTime();
/*     */     }
/* 349 */     long bytes_read = this.proxy_connection.read(new ByteBuffer[] { msg }, 0, 1);
/*     */     
/* 351 */     if (!msg.hasRemaining()) {
/* 352 */       msg.position(0);
/* 353 */       this.read_start_time = 0L;
/* 354 */       return 0;
/*     */     }
/*     */     
/* 357 */     if (SystemTime.getCurrentTime() - this.read_start_time > 30000L) {
/* 358 */       String error = "proxy message read timed out after 30sec";
/*     */       
/* 360 */       throw new IOException(error);
/*     */     }
/*     */     
/* 363 */     return bytes_read == 0L ? 2 : 1;
/*     */   }
/*     */   
/*     */ 
/*     */   private ByteBuffer[] createSocks4Message()
/*     */     throws Exception
/*     */   {
/* 370 */     ByteBuffer handshake = ByteBuffer.allocate(256 + this.mapped_ip.length());
/*     */     
/* 372 */     handshake.put((byte)4);
/* 373 */     handshake.put((byte)1);
/* 374 */     handshake.putShort((short)this.remote_address.getPort());
/*     */     
/*     */ 
/*     */ 
/* 378 */     InetAddress ia = this.remote_address.getAddress();
/*     */     
/*     */     String host_str;
/*     */     String host_str;
/* 382 */     if (ia == null)
/*     */     {
/*     */ 
/*     */ 
/* 386 */       host_str = this.remote_address.getHostName();
/*     */     }
/*     */     else
/*     */     {
/* 390 */       host_str = ia.getHostAddress();
/*     */     }
/*     */     
/* 393 */     InetAddress address = HostNameToIPResolver.syncResolve(host_str);
/*     */     
/* 395 */     if (address == null)
/*     */     {
/* 397 */       throw new Exception("Unresolved host: " + this.remote_address);
/*     */     }
/*     */     
/* 400 */     byte[] ip_bytes = address.getAddress();
/*     */     
/* 402 */     if (ip_bytes.length != 4)
/*     */     {
/* 404 */       throw new Exception("Unsupported IPv6 address: " + this.remote_address);
/*     */     }
/*     */     
/* 407 */     handshake.put(ip_bytes[0]);
/* 408 */     handshake.put(ip_bytes[1]);
/* 409 */     handshake.put(ip_bytes[2]);
/* 410 */     handshake.put(ip_bytes[3]);
/*     */     
/* 412 */     if (this.socks_user.length() > 0)
/*     */     {
/* 414 */       handshake.put(this.socks_user.getBytes());
/*     */     }
/*     */     
/* 417 */     handshake.put((byte)0);
/*     */     
/* 419 */     handshake.flip();
/*     */     
/* 421 */     return new ByteBuffer[] { handshake, ByteBuffer.allocate(8) };
/*     */   }
/*     */   
/*     */ 
/*     */   private ByteBuffer[] createSocks4aMessage()
/*     */   {
/* 427 */     ByteBuffer handshake = ByteBuffer.allocate(256 + this.mapped_ip.length());
/*     */     
/* 429 */     handshake.put((byte)4);
/* 430 */     handshake.put((byte)1);
/* 431 */     handshake.putShort((short)this.remote_address.getPort());
/* 432 */     handshake.put((byte)0);
/* 433 */     handshake.put((byte)0);
/* 434 */     handshake.put((byte)0);
/* 435 */     handshake.put((byte)1);
/*     */     
/* 437 */     if (this.socks_user.length() > 0) {
/* 438 */       handshake.put(this.socks_user.getBytes());
/*     */     }
/*     */     
/* 441 */     handshake.put((byte)0);
/* 442 */     handshake.put(this.mapped_ip.getBytes());
/* 443 */     handshake.put((byte)0);
/*     */     
/* 445 */     handshake.flip();
/*     */     
/* 447 */     return new ByteBuffer[] { handshake, ByteBuffer.allocate(8) };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ByteBuffer[] createSocks5Message()
/*     */   {
/* 454 */     ByteBuffer handshake = ByteBuffer.allocate(256 + this.mapped_ip.length());
/*     */     
/* 456 */     if (this.socks5_handshake_phase == 0)
/*     */     {
/*     */ 
/* 459 */       handshake.put((byte)5);
/* 460 */       handshake.put((byte)2);
/* 461 */       handshake.put((byte)0);
/* 462 */       handshake.put((byte)2);
/*     */       
/* 464 */       handshake.flip();
/* 465 */       this.socks5_handshake_phase = 1;
/*     */       
/* 467 */       return new ByteBuffer[] { handshake, ByteBuffer.allocate(2) };
/*     */     }
/*     */     
/* 470 */     if (this.socks5_handshake_phase == 1)
/*     */     {
/*     */ 
/* 473 */       handshake.put((byte)1);
/* 474 */       handshake.put((byte)this.socks_user.length());
/* 475 */       handshake.put(this.socks_user.getBytes());
/* 476 */       handshake.put((byte)this.socks_password.length());
/* 477 */       handshake.put(this.socks_password.getBytes());
/*     */       
/* 479 */       handshake.flip();
/* 480 */       this.socks5_handshake_phase = 2;
/*     */       
/* 482 */       return new ByteBuffer[] { handshake, ByteBuffer.allocate(2) };
/*     */     }
/*     */     
/* 485 */     if (this.socks5_handshake_phase == 2)
/*     */     {
/*     */ 
/* 488 */       handshake.put((byte)5);
/* 489 */       handshake.put((byte)1);
/* 490 */       handshake.put((byte)0);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 495 */         byte[] ip_bytes = HostNameToIPResolver.syncResolve(this.mapped_ip).getAddress();
/*     */         
/* 497 */         handshake.put((byte)1);
/*     */         
/* 499 */         handshake.put(ip_bytes[0]);
/* 500 */         handshake.put(ip_bytes[1]);
/* 501 */         handshake.put(ip_bytes[2]);
/* 502 */         handshake.put(ip_bytes[3]);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 506 */         handshake.put((byte)3);
/* 507 */         handshake.put((byte)this.mapped_ip.length());
/* 508 */         handshake.put(this.mapped_ip.getBytes());
/*     */       }
/*     */       
/* 511 */       handshake.putShort((short)this.remote_address.getPort());
/*     */       
/* 513 */       handshake.flip();
/* 514 */       this.socks5_handshake_phase = 3;
/*     */       
/* 516 */       return new ByteBuffer[] { handshake, ByteBuffer.allocate(5) };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 523 */     this.socks5_handshake_phase = 4;
/* 524 */     return new ByteBuffer[] { null, ByteBuffer.allocate(this.socks5_address_length) };
/*     */   }
/*     */   
/*     */   private boolean parseSocks5Reply(ByteBuffer reply)
/*     */     throws IOException
/*     */   {
/* 530 */     if (this.socks5_handshake_phase == 1)
/*     */     {
/*     */ 
/* 533 */       reply.get();
/* 534 */       byte method = reply.get();
/*     */       
/* 536 */       if ((method != 0) && (method != 2)) {
/* 537 */         throw new IOException("SOCKS 5: no valid method [" + method + "]");
/*     */       }
/*     */       
/*     */ 
/* 541 */       if (method == 0) {
/* 542 */         this.socks5_handshake_phase = 2;
/*     */       }
/*     */       
/* 545 */       return false;
/*     */     }
/*     */     
/* 548 */     if (this.socks5_handshake_phase == 2)
/*     */     {
/*     */ 
/* 551 */       reply.get();
/* 552 */       byte status = reply.get();
/*     */       
/* 554 */       if (status != 0) {
/* 555 */         throw new IOException("SOCKS 5: authentication fails [status=" + status + "]");
/*     */       }
/*     */       
/* 558 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 562 */     if (this.socks5_handshake_phase == 3)
/*     */     {
/*     */ 
/* 565 */       reply.get();
/* 566 */       byte rep = reply.get();
/*     */       
/* 568 */       if (rep != 0) {
/* 569 */         String[] error_msgs = { "", "General SOCKS server failure", "connection not allowed by ruleset", "Network unreachable", "Host unreachable", "Connection refused (authentication failure?)", "TTL expired (can mean authentication failure)", "Command not supported", "Address type not supported" };
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 579 */         String error_msg = rep < error_msgs.length ? error_msgs[rep] : "Unknown error";
/* 580 */         throw new IOException("SOCKS request failure [" + error_msg + "/" + rep + "]");
/*     */       }
/*     */       
/* 583 */       reply.get();
/* 584 */       byte atype = reply.get();
/* 585 */       byte first_address_byte = reply.get();
/*     */       
/* 587 */       if (atype == 1) {
/* 588 */         this.socks5_address_length = 3;
/*     */       }
/* 590 */       else if (atype == 3) {
/* 591 */         this.socks5_address_length = first_address_byte;
/*     */       }
/*     */       else {
/* 594 */         this.socks5_address_length = 15;
/*     */       }
/*     */       
/* 597 */       this.socks5_address_length += 2;
/* 598 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 605 */     return true;
/*     */   }
/*     */   
/*     */   public static abstract interface ProxyListener
/*     */   {
/*     */     public abstract void connectSuccess();
/*     */     
/*     */     public abstract void connectFailure(Throwable paramThrowable);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/ProxyLoginHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */