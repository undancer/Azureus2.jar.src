/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.TransportStartpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoder;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportCryptoManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportCryptoManager.HandshakeListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportImpl;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory.PluginProxy;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class TCPTransportImpl
/*     */   extends TransportImpl
/*     */   implements Transport
/*     */ {
/*  47 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */ 
/*     */   private final ProtocolEndpointTCP protocol_endpoint;
/*     */   
/*     */ 
/*  53 */   private TCPConnectionManager.ConnectListener connect_request_key = null;
/*  54 */   private String description = "<disconnected>";
/*     */   
/*     */   private final boolean is_inbound_connection;
/*  57 */   private int transport_mode = 0;
/*     */   
/*  59 */   public volatile boolean has_been_closed = false;
/*     */   
/*     */ 
/*     */   private boolean connect_with_crypto;
/*     */   
/*     */ 
/*     */   private byte[][] shared_secrets;
/*     */   
/*     */ 
/*     */   private int fallback_count;
/*     */   
/*     */   private final boolean fallback_allowed;
/*     */   
/*     */   private boolean is_socks;
/*     */   
/*     */   private volatile AEProxyFactory.PluginProxy plugin_proxy;
/*     */   
/*     */ 
/*     */   public TCPTransportImpl(ProtocolEndpointTCP endpoint, boolean use_crypto, boolean allow_fallback, byte[][] _shared_secrets)
/*     */   {
/*  79 */     this.protocol_endpoint = endpoint;
/*  80 */     this.is_inbound_connection = false;
/*  81 */     this.connect_with_crypto = use_crypto;
/*  82 */     this.shared_secrets = _shared_secrets;
/*  83 */     this.fallback_allowed = allow_fallback;
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
/*     */   public TCPTransportImpl(ProtocolEndpointTCP endpoint, TransportHelperFilter filter)
/*     */   {
/*  98 */     this.protocol_endpoint = endpoint;
/*     */     
/* 100 */     setFilter(filter);
/*     */     
/* 102 */     this.is_inbound_connection = true;
/* 103 */     this.connect_with_crypto = false;
/* 104 */     this.fallback_allowed = false;
/*     */     
/* 106 */     InetSocketAddress address = endpoint.getAddress();
/*     */     
/* 108 */     this.description = ((this.is_inbound_connection ? "R" : "L") + ": " + AddressUtils.getHostNameNoResolve(address) + ": " + address.getPort());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SocketChannel getSocketChannel()
/*     */   {
/* 117 */     TransportHelperFilter filter = getFilter();
/* 118 */     if (filter == null) {
/* 119 */       return null;
/*     */     }
/*     */     
/* 122 */     TCPTransportHelper helper = (TCPTransportHelper)filter.getHelper();
/* 123 */     if (helper == null) {
/* 124 */       return null;
/*     */     }
/*     */     
/* 127 */     return helper.getSocketChannel();
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportEndpointTCP getTransportEndpoint()
/*     */   {
/* 133 */     return new TransportEndpointTCP(this.protocol_endpoint, getSocketChannel());
/*     */   }
/*     */   
/*     */ 
/*     */   public TransportStartpoint getTransportStartpoint()
/*     */   {
/* 139 */     return new TransportStartpointTCP(getTransportEndpoint());
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMssSize()
/*     */   {
/* 145 */     return TCPNetworkManager.getTcpMssSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTCP()
/*     */   {
/* 151 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSOCKS()
/*     */   {
/* 157 */     return this.is_socks;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getProtocol()
/*     */   {
/* 163 */     if (this.is_socks)
/*     */     {
/* 165 */       return "TCP (SOCKS)";
/*     */     }
/*     */     
/*     */ 
/* 169 */     return "TCP";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 177 */     return this.description;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectOutbound(final ByteBuffer initial_data, final Transport.ConnectListener listener, final int priority)
/*     */   {
/* 189 */     if (!TCPNetworkManager.TCP_OUTGOING_ENABLED)
/*     */     {
/* 191 */       listener.connectFailure(new Throwable("Outbound TCP connections disabled"));
/*     */       
/* 193 */       return;
/*     */     }
/*     */     
/* 196 */     if (this.has_been_closed)
/*     */     {
/* 198 */       listener.connectFailure(new Throwable("Connection already closed"));
/*     */       
/* 200 */       return;
/*     */     }
/*     */     
/* 203 */     if (getFilter() != null) {
/* 204 */       Debug.out("socket_channel != null");
/* 205 */       listener.connectSuccess(this, initial_data);
/* 206 */       return;
/*     */     }
/*     */     
/* 209 */     final InetSocketAddress address = this.protocol_endpoint.getAddress();
/*     */     
/* 211 */     if (!address.equals(ProxyLoginHandler.DEFAULT_SOCKS_SERVER_ADDRESS))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 216 */       if (address.isUnresolved())
/*     */       {
/* 218 */         String host = address.getHostName();
/*     */         
/* 220 */         if (AENetworkClassifier.categoriseAddress(host) != "Public")
/*     */         {
/* 222 */           Map<String, Object> opts = new HashMap();
/*     */           
/* 224 */           Object peer_nets = listener.getConnectionProperty("peer_networks");
/*     */           
/* 226 */           if (peer_nets != null)
/*     */           {
/* 228 */             opts.put("peer_networks", peer_nets);
/*     */           }
/*     */           
/* 231 */           AEProxyFactory.PluginProxy pp = this.plugin_proxy;
/*     */           
/* 233 */           this.plugin_proxy = null;
/*     */           
/* 235 */           if (pp != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 240 */             pp.setOK(true);
/*     */           }
/*     */           
/* 243 */           this.plugin_proxy = AEProxyFactory.getPluginProxy("outbound connection", host, address.getPort(), opts);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 248 */       if (this.plugin_proxy == null)
/*     */       {
/* 250 */         this.is_socks = COConfigurationManager.getBooleanParameter("Proxy.Data.Enable");
/*     */       }
/*     */     }
/*     */     
/* 254 */     final TCPTransportImpl transport_instance = this;
/*     */     
/*     */ 
/* 257 */     TCPConnectionManager.ConnectListener connect_listener = new TCPConnectionManager.ConnectListener()
/*     */     {
/*     */       public int connectAttemptStarted(int default_connect_timeout) {
/* 260 */         return listener.connectAttemptStarted(default_connect_timeout);
/*     */       }
/*     */       
/*     */       public void connectSuccess(final SocketChannel channel) {
/* 264 */         if (channel == null) {
/* 265 */           String msg = "connectSuccess:: given channel == null";
/* 266 */           Debug.out(msg);
/* 267 */           TCPTransportImpl.this.setConnectResult(false);
/* 268 */           listener.connectFailure(new Exception(msg));
/* 269 */           return;
/*     */         }
/*     */         
/* 272 */         if (TCPTransportImpl.this.has_been_closed) {
/* 273 */           TCPNetworkManager.getSingleton().getConnectDisconnectManager().closeConnection(channel);
/*     */           
/* 275 */           TCPTransportImpl.this.setConnectResult(false);
/*     */           
/* 277 */           listener.connectFailure(new Throwable("Connection has been closed"));
/*     */           
/* 279 */           return;
/*     */         }
/*     */         
/* 282 */         TCPTransportImpl.this.connect_request_key = null;
/* 283 */         TCPTransportImpl.this.description = ((TCPTransportImpl.this.is_inbound_connection ? "R" : "L") + ": " + channel.socket().getInetAddress().getHostAddress() + ": " + channel.socket().getPort());
/*     */         
/* 285 */         AEProxyFactory.PluginProxy pp = TCPTransportImpl.this.plugin_proxy;
/*     */         
/* 287 */         if (TCPTransportImpl.this.is_socks) {
/* 288 */           if (Logger.isEnabled()) {
/* 289 */             Logger.log(new LogEvent(TCPTransportImpl.LOGID, "Socket connection established to proxy server [" + TCPTransportImpl.this.description + "], login initiated..."));
/*     */           }
/*     */           
/*     */ 
/* 293 */           TCPTransportImpl.this.setFilter(TCPTransportHelperFilterFactory.createTransparentFilter(channel));
/*     */           
/* 295 */           new ProxyLoginHandler(transport_instance, address, new ProxyLoginHandler.ProxyListener() {
/*     */             public void connectSuccess() {
/* 297 */               if (Logger.isEnabled())
/* 298 */                 Logger.log(new LogEvent(TCPTransportImpl.LOGID, "Proxy [" + TCPTransportImpl.this.description + "] login successful."));
/* 299 */               TCPTransportImpl.this.handleCrypto(TCPTransportImpl.1.this.val$address, channel, TCPTransportImpl.1.this.val$initial_data, TCPTransportImpl.1.this.val$priority, TCPTransportImpl.1.this.val$listener);
/*     */             }
/*     */             
/*     */             public void connectFailure(Throwable failure_msg) {
/* 303 */               TCPTransportImpl.this.close("Proxy login failed");
/* 304 */               TCPTransportImpl.1.this.val$listener.connectFailure(failure_msg);
/*     */             }
/*     */           });
/* 307 */         } else if (pp != null)
/*     */         {
/* 309 */           if (Logger.isEnabled()) {
/* 310 */             Logger.log(new LogEvent(TCPTransportImpl.LOGID, "Socket connection established via plugin proxy [" + TCPTransportImpl.this.description + "], login initiated..."));
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 315 */           TCPTransportImpl.this.setFilter(TCPTransportHelperFilterFactory.createTransparentFilter(channel));
/*     */           
/* 317 */           String pp_host = pp.getHost();
/*     */           
/*     */           InetSocketAddress ia_address;
/*     */           InetSocketAddress ia_address;
/* 321 */           if (AENetworkClassifier.categoriseAddress(pp_host) == "Public")
/*     */           {
/* 323 */             ia_address = new InetSocketAddress(pp.getHost(), pp.getPort());
/*     */           }
/*     */           else
/*     */           {
/* 327 */             ia_address = InetSocketAddress.createUnresolved(pp_host, pp.getPort());
/*     */           }
/*     */           
/* 330 */           new ProxyLoginHandler(transport_instance, ia_address, new ProxyLoginHandler.ProxyListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void connectSuccess()
/*     */             {
/*     */ 
/*     */ 
/* 338 */               if (Logger.isEnabled()) {
/* 339 */                 Logger.log(new LogEvent(TCPTransportImpl.LOGID, "Proxy [" + TCPTransportImpl.this.description + "] login successful."));
/*     */               }
/* 341 */               TCPTransportImpl.this.setConnectResult(true);
/*     */               
/* 343 */               TCPTransportImpl.this.handleCrypto(TCPTransportImpl.1.this.val$address, channel, TCPTransportImpl.1.this.val$initial_data, TCPTransportImpl.1.this.val$priority, TCPTransportImpl.1.this.val$listener);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             public void connectFailure(Throwable failure_msg)
/*     */             {
/* 350 */               TCPTransportImpl.this.setConnectResult(false);
/*     */               
/* 352 */               TCPTransportImpl.this.close("Proxy login failed");
/*     */               
/* 354 */               TCPTransportImpl.1.this.val$listener.connectFailure(failure_msg); } }, "V4a", "", "");
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 360 */           TCPTransportImpl.this.handleCrypto(address, channel, initial_data, priority, listener);
/*     */         }
/*     */       }
/*     */       
/*     */       public void connectFailure(Throwable failure_msg) {
/* 365 */         TCPTransportImpl.this.connect_request_key = null;
/* 366 */         TCPTransportImpl.this.setConnectResult(false);
/* 367 */         listener.connectFailure(failure_msg);
/*     */       }
/*     */       
/* 370 */     };
/* 371 */     this.connect_request_key = connect_listener;
/*     */     
/*     */ 
/*     */ 
/* 375 */     AEProxyFactory.PluginProxy pp = this.plugin_proxy;
/*     */     InetSocketAddress to_connect;
/* 377 */     InetSocketAddress to_connect; if (this.is_socks)
/*     */     {
/* 379 */       to_connect = ProxyLoginHandler.getProxyAddress(address);
/*     */     } else { InetSocketAddress to_connect;
/* 381 */       if (pp != null)
/*     */       {
/* 383 */         to_connect = (InetSocketAddress)pp.getProxy().address();
/*     */       }
/*     */       else
/*     */       {
/* 387 */         to_connect = address;
/*     */       }
/*     */     }
/* 390 */     TCPNetworkManager.getSingleton().getConnectDisconnectManager().requestNewConnection(to_connect, connect_listener, priority);
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
/*     */   protected void handleCrypto(InetSocketAddress address, final SocketChannel channel, final ByteBuffer initial_data, final int priority, final Transport.ConnectListener listener)
/*     */   {
/* 404 */     if (this.connect_with_crypto)
/*     */     {
/*     */ 
/* 407 */       final TransportHelper helper = new TCPTransportHelper(channel);
/* 408 */       TransportCryptoManager.getSingleton().manageCrypto(helper, this.shared_secrets, false, initial_data, new TransportCryptoManager.HandshakeListener()
/*     */       {
/*     */         public void handshakeSuccess(ProtocolDecoder decoder, ByteBuffer remaining_initial_data) {
/* 411 */           TransportHelperFilter filter = decoder.getFilter();
/* 412 */           TCPTransportImpl.this.setFilter(filter);
/* 413 */           if (Logger.isEnabled()) {
/* 414 */             Logger.log(new LogEvent(TCPTransportImpl.LOGID, "Outgoing TCP stream to " + channel.socket().getRemoteSocketAddress() + " established, type = " + filter.getName(false)));
/*     */           }
/*     */           
/* 417 */           TCPTransportImpl.this.connectedOutbound(remaining_initial_data, listener);
/*     */         }
/*     */         
/*     */         public void handshakeFailure(Throwable failure_msg) {
/* 421 */           if ((TCPTransportImpl.this.fallback_allowed) && (NetworkManager.OUTGOING_HANDSHAKE_FALLBACK_ALLOWED) && (!TCPTransportImpl.this.has_been_closed)) {
/* 422 */             if (Logger.isEnabled()) Logger.log(new LogEvent(TCPTransportImpl.LOGID, TCPTransportImpl.this.description + " | crypto handshake failure [" + failure_msg.getMessage() + "], attempting non-crypto fallback."));
/* 423 */             TCPTransportImpl.this.connect_with_crypto = false;
/* 424 */             TCPTransportImpl.access$908(TCPTransportImpl.this);
/* 425 */             TCPTransportImpl.this.close(helper, "Handshake failure and retry");
/* 426 */             TCPTransportImpl.this.has_been_closed = false;
/* 427 */             if (initial_data != null)
/*     */             {
/* 429 */               initial_data.position(0);
/*     */             }
/* 431 */             TCPTransportImpl.this.connectOutbound(initial_data, listener, priority);
/*     */           }
/*     */           else {
/* 434 */             TCPTransportImpl.this.close(helper, "Handshake failure");
/* 435 */             listener.connectFailure(failure_msg);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void gotSecret(byte[] session_secret) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public int getMaximumPlainHeaderLength()
/*     */         {
/* 448 */           throw new RuntimeException();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public int matchPlainHeader(ByteBuffer buffer)
/*     */         {
/* 455 */           throw new RuntimeException();
/*     */         }
/*     */         
/*     */ 
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/* 463 */       setFilter(TCPTransportHelperFilterFactory.createTransparentFilter(channel));
/*     */       
/* 465 */       if (Logger.isEnabled()) {
/* 466 */         Logger.log(new LogEvent(LOGID, "Outgoing TCP stream to " + channel.socket().getRemoteSocketAddress() + " established, type = " + getFilter().getName(false) + ", fallback = " + (this.fallback_count == 0 ? "no" : "yes")));
/*     */       }
/*     */       
/* 469 */       connectedOutbound(initial_data, listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setTransportBuffersSize(int size_in_bytes)
/*     */   {
/* 477 */     if (getFilter() == null) {
/* 478 */       Debug.out("socket_channel == null");
/* 479 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 483 */       SocketChannel channel = getSocketChannel();
/*     */       
/* 485 */       channel.socket().setSendBufferSize(size_in_bytes);
/* 486 */       channel.socket().setReceiveBufferSize(size_in_bytes);
/*     */       
/* 488 */       int snd_real = channel.socket().getSendBufferSize();
/* 489 */       int rcv_real = channel.socket().getReceiveBufferSize();
/*     */       
/* 491 */       if (Logger.isEnabled()) {
/* 492 */         Logger.log(new LogEvent(LOGID, "Setting new transport [" + this.description + "] buffer sizes: SND=" + size_in_bytes + " [" + snd_real + "] , RCV=" + size_in_bytes + " [" + rcv_real + "]"));
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 497 */       Debug.out(t);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTransportMode(int mode)
/*     */   {
/* 507 */     if (mode == this.transport_mode) { return;
/*     */     }
/* 509 */     switch (mode) {
/*     */     case 0: 
/* 511 */       setTransportBuffersSize(8192);
/* 512 */       break;
/*     */     
/*     */     case 1: 
/* 515 */       setTransportBuffersSize(65536);
/* 516 */       break;
/*     */     
/*     */     case 2: 
/* 519 */       setTransportBuffersSize(524288);
/* 520 */       break;
/*     */     
/*     */     default: 
/* 523 */       Debug.out("invalid transport mode given: " + mode);
/*     */     }
/*     */     
/* 526 */     this.transport_mode = mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void connectedOutbound(ByteBuffer remaining_initial_data, Transport.ConnectListener listener)
/*     */   {
/* 534 */     if (this.has_been_closed)
/*     */     {
/* 536 */       TransportHelperFilter filter = getFilter();
/*     */       
/* 538 */       if (filter != null)
/*     */       {
/* 540 */         filter.getHelper().close("Connection closed");
/*     */         
/* 542 */         setFilter(null);
/*     */       }
/*     */       
/* 545 */       listener.connectFailure(new Throwable("Connection closed"));
/*     */     }
/*     */     else
/*     */     {
/* 549 */       connectedOutbound();
/*     */       
/* 551 */       listener.connectSuccess(this, remaining_initial_data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getTransportMode()
/*     */   {
/* 559 */     return this.transport_mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void close(TransportHelper helper, String reason)
/*     */   {
/* 566 */     helper.close(reason);
/*     */     
/* 568 */     close(reason);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setConnectResult(boolean ok)
/*     */   {
/* 575 */     AEProxyFactory.PluginProxy pp = this.plugin_proxy;
/* 576 */     if (pp != null) {
/* 577 */       this.plugin_proxy = null;
/* 578 */       pp.setOK(ok);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void close(String reason)
/*     */   {
/* 586 */     this.has_been_closed = true;
/* 587 */     setConnectResult(false);
/* 588 */     if (this.connect_request_key != null) {
/* 589 */       TCPNetworkManager.getSingleton().getConnectDisconnectManager().cancelRequest(this.connect_request_key);
/*     */     }
/*     */     
/* 592 */     readyForRead(false);
/* 593 */     readyForWrite(false);
/*     */     
/* 595 */     TransportHelperFilter filter = getFilter();
/*     */     
/* 597 */     if (filter != null)
/*     */     {
/* 599 */       filter.getHelper().close(reason);
/*     */       
/* 601 */       setFilter(null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 607 */     setReadyForRead();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TCPTransportImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */