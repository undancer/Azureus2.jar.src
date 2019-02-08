/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector.SelectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelectorFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.IncomingConnectionManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.IncomingConnectionManager.MatchListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoder;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportCryptoManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportCryptoManager.HandshakeListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper.AppliedPortMapping;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ServerSocketChannel;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class IncomingSocketChannelManager
/*     */ {
/*  66 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*     */   private final String port_config_key;
/*     */   
/*     */   private final String port_enable_config_key;
/*     */   
/*     */   private int tcp_listen_port;
/*  73 */   private int so_rcvbuf_size = COConfigurationManager.getIntParameter("network.tcp.socket.SO_RCVBUF");
/*     */   
/*  75 */   private InetAddress[] default_bind_addresses = NetworkAdmin.getSingleton().getMultiHomedServiceBindAddresses(true);
/*     */   
/*     */   private InetAddress explicit_bind_address;
/*     */   private boolean explicit_bind_address_set;
/*  79 */   private VirtualServerChannelSelector[] serverSelectors = new VirtualServerChannelSelector[0];
/*  80 */   private int[] listenFailCounts = new int[0];
/*     */   
/*  82 */   final IncomingConnectionManager incoming_manager = IncomingConnectionManager.getSingleton();
/*     */   
/*  84 */   protected final AEMonitor this_mon = new AEMonitor("IncomingSocketChannelManager");
/*     */   
/*     */   private long last_non_local_connection_time;
/*     */   
/*  88 */   private final AEProxyAddressMapper proxy_address_mapper = AEProxyFactory.getAddressMapper();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IncomingSocketChannelManager(String _port_config_key, String _port_enable_config_key)
/*     */   {
/*  95 */     this.port_config_key = _port_config_key;
/*  96 */     this.port_enable_config_key = _port_enable_config_key;
/*     */     
/*  98 */     this.tcp_listen_port = COConfigurationManager.getIntParameter(this.port_config_key);
/*     */     
/*     */ 
/* 101 */     COConfigurationManager.addParameterListener(this.port_config_key, new ParameterListener() {
/*     */       public void parameterChanged(String parameterName) {
/* 103 */         int port = COConfigurationManager.getIntParameter(IncomingSocketChannelManager.this.port_config_key);
/* 104 */         if (port != IncomingSocketChannelManager.this.tcp_listen_port) {
/* 105 */           IncomingSocketChannelManager.this.tcp_listen_port = port;
/* 106 */           IncomingSocketChannelManager.this.restart();
/*     */         }
/*     */         
/*     */       }
/* 110 */     });
/* 111 */     COConfigurationManager.addParameterListener(this.port_enable_config_key, new ParameterListener() {
/*     */       public void parameterChanged(String parameterName) {
/* 113 */         IncomingSocketChannelManager.this.restart();
/*     */       }
/*     */       
/*     */ 
/* 117 */     });
/* 118 */     COConfigurationManager.addParameterListener("network.tcp.socket.SO_RCVBUF", new ParameterListener() {
/*     */       public void parameterChanged(String parameterName) {
/* 120 */         int size = COConfigurationManager.getIntParameter("network.tcp.socket.SO_RCVBUF");
/* 121 */         if (size != IncomingSocketChannelManager.this.so_rcvbuf_size) {
/* 122 */           IncomingSocketChannelManager.this.so_rcvbuf_size = size;
/* 123 */           IncomingSocketChannelManager.this.restart();
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 129 */     });
/* 130 */     NetworkAdmin.getSingleton().addPropertyChangeListener(new NetworkAdminPropertyChangeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void propertyChanged(String property)
/*     */       {
/*     */ 
/* 137 */         if (property == "Default Bind IP")
/*     */         {
/* 139 */           InetAddress[] addresses = NetworkAdmin.getSingleton().getMultiHomedServiceBindAddresses(true);
/*     */           
/* 141 */           if (!Arrays.equals(addresses, IncomingSocketChannelManager.this.default_bind_addresses))
/*     */           {
/* 143 */             IncomingSocketChannelManager.this.default_bind_addresses = addresses;
/*     */             
/* 145 */             IncomingSocketChannelManager.this.restart();
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 152 */     });
/* 153 */     start();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 160 */     SimpleTimer.addPeriodicEvent("IncomingSocketChannelManager:concheck", 60000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */ 
/* 166 */         COConfigurationManager.setParameter("network.tcp.port." + IncomingSocketChannelManager.this.tcp_listen_port + ".last.nonlocal.incoming", IncomingSocketChannelManager.this.last_non_local_connection_time);
/*     */         
/* 168 */         for (int i = 0; i < IncomingSocketChannelManager.this.serverSelectors.length; i++)
/*     */         {
/* 170 */           VirtualServerChannelSelector server_selector = IncomingSocketChannelManager.this.serverSelectors[i];
/*     */           
/* 172 */           if ((server_selector != null) && (server_selector.isRunning()))
/*     */           {
/* 174 */             long accept_idle = SystemTime.getCurrentTime() - server_selector.getTimeOfLastAccept();
/* 175 */             if (accept_idle > 600000L)
/*     */             {
/*     */ 
/* 178 */               InetAddress inet_address = server_selector.getBoundToAddress();
/*     */               try
/*     */               {
/* 181 */                 if (inet_address == null)
/* 182 */                   inet_address = InetAddress.getByName("127.0.0.1");
/* 183 */                 Socket sock = new Socket(inet_address, IncomingSocketChannelManager.this.tcp_listen_port, inet_address, 0);
/* 184 */                 sock.close();
/* 185 */                 IncomingSocketChannelManager.this.listenFailCounts[i] = 0;
/*     */               }
/*     */               catch (Throwable t)
/*     */               {
/*     */                 try
/*     */                 {
/* 191 */                   Socket sock = new Socket(InetAddress.getByName("127.0.0.1"), IncomingSocketChannelManager.this.tcp_listen_port);
/* 192 */                   sock.close();
/* 193 */                   IncomingSocketChannelManager.this.listenFailCounts[i] = 0;
/*     */                 }
/*     */                 catch (Throwable x) {
/* 196 */                   IncomingSocketChannelManager.this.listenFailCounts[i] += 1;
/* 197 */                   Debug.out(new Date() + ": listen port on [" + inet_address + ": " + IncomingSocketChannelManager.this.tcp_listen_port + "] seems CLOSED [" + IncomingSocketChannelManager.this.listenFailCounts[i] + "x]");
/* 198 */                   if (IncomingSocketChannelManager.this.listenFailCounts[i] > 4)
/*     */                   {
/* 200 */                     String error = t.getMessage() == null ? "<null>" : t.getMessage();
/* 201 */                     String msg = "Listen server socket on [" + inet_address + ": " + IncomingSocketChannelManager.this.tcp_listen_port + "] does not appear to be accepting inbound connections.\n[" + error + "]\nAuto-repairing listen service....\n";
/* 202 */                     Logger.log(new LogAlert(false, 1, msg));
/* 203 */                     IncomingSocketChannelManager.this.restart();
/* 204 */                     IncomingSocketChannelManager.this.listenFailCounts[i] = 0;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/* 210 */               IncomingSocketChannelManager.this.listenFailCounts[i] = 0;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 221 */     return COConfigurationManager.getBooleanParameter(this.port_enable_config_key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTCPListeningPortNumber()
/*     */   {
/* 230 */     return this.tcp_listen_port;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setExplicitBindAddress(InetAddress address)
/*     */   {
/* 236 */     this.explicit_bind_address = address;
/* 237 */     this.explicit_bind_address_set = true;
/*     */     
/* 239 */     restart();
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearExplicitBindAddress()
/*     */   {
/* 245 */     this.explicit_bind_address = null;
/* 246 */     this.explicit_bind_address_set = false;
/*     */     
/* 248 */     restart();
/*     */   }
/*     */   
/*     */ 
/*     */   protected InetAddress[] getEffectiveBindAddresses()
/*     */   {
/* 254 */     if (this.explicit_bind_address_set)
/*     */     {
/* 256 */       return new InetAddress[] { this.explicit_bind_address };
/*     */     }
/*     */     
/*     */ 
/* 260 */     return this.default_bind_addresses;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isEffectiveBindAddress(InetAddress address)
/*     */   {
/* 268 */     InetAddress[] effective = getEffectiveBindAddresses();
/*     */     
/* 270 */     return Arrays.asList(effective).contains(address);
/*     */   }
/*     */   
/*     */   private final class TcpSelectListener
/*     */     implements VirtualServerChannelSelector.SelectListener
/*     */   {
/*     */     private TcpSelectListener() {}
/*     */     
/*     */     public void newConnectionAccepted(final ServerSocketChannel server, SocketChannel channel)
/*     */     {
/* 280 */       InetAddress remote_ia = channel.socket().getInetAddress();
/*     */       
/* 282 */       if ((!remote_ia.isLoopbackAddress()) && (!remote_ia.isLinkLocalAddress()) && (!remote_ia.isSiteLocalAddress()))
/*     */       {
/* 284 */         IncomingSocketChannelManager.this.last_non_local_connection_time = SystemTime.getCurrentTime();
/*     */       }
/*     */       
/*     */ 
/* 288 */       final TCPTransportHelper helper = new TCPTransportHelper(channel);
/*     */       
/* 290 */       TransportCryptoManager.getSingleton().manageCrypto(helper, (byte[][])null, true, null, new TransportCryptoManager.HandshakeListener() {
/*     */         public void handshakeSuccess(ProtocolDecoder decoder, ByteBuffer remaining_initial_data) {
/* 292 */           IncomingSocketChannelManager.this.process(server.socket().getLocalPort(), decoder.getFilter());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void handshakeFailure(Throwable failure_msg)
/*     */         {
/* 300 */           if (Logger.isEnabled()) { Logger.log(new LogEvent(IncomingSocketChannelManager.LOGID, "incoming crypto handshake failure: " + Debug.getNestedExceptionMessage(failure_msg)));
/*     */           }
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
/* 323 */           helper.close("Handshake failure: " + Debug.getNestedExceptionMessage(failure_msg));
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
/* 335 */           return IncomingSocketChannelManager.this.incoming_manager.getMaxMinMatchBufferSize();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public int matchPlainHeader(ByteBuffer buffer)
/*     */         {
/* 342 */           Object[] match_data = IncomingSocketChannelManager.this.incoming_manager.checkForMatch(helper, server.socket().getLocalPort(), buffer, true);
/*     */           
/* 344 */           if (match_data == null)
/*     */           {
/* 346 */             return 1;
/*     */           }
/*     */           
/*     */ 
/* 350 */           IncomingConnectionManager.MatchListener match = (IncomingConnectionManager.MatchListener)match_data[0];
/*     */           
/* 352 */           if (match.autoCryptoFallback())
/*     */           {
/* 354 */             return 3;
/*     */           }
/*     */           
/*     */ 
/* 358 */           return 2;
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 368 */   private final VirtualServerChannelSelector.SelectListener selectListener = new TcpSelectListener(null);
/*     */   
/*     */   private void start()
/*     */   {
/*     */     try
/*     */     {
/* 374 */       this.this_mon.enter();
/*     */       
/* 376 */       if ((this.tcp_listen_port < 0) || (this.tcp_listen_port > 65535) || (this.tcp_listen_port == Constants.INSTANCE_PORT))
/*     */       {
/* 378 */         String msg = "Invalid incoming TCP listen port configured, " + this.tcp_listen_port + ". Port reset to default. Please check your config!";
/* 379 */         Debug.out(msg);
/* 380 */         Logger.log(new LogAlert(false, 3, msg));
/* 381 */         this.tcp_listen_port = RandomUtils.generateRandomNetworkListenPort();
/* 382 */         COConfigurationManager.setParameter(this.port_config_key, this.tcp_listen_port);
/*     */       }
/*     */       
/* 385 */       if (COConfigurationManager.getBooleanParameter(this.port_enable_config_key))
/*     */       {
/* 387 */         this.last_non_local_connection_time = COConfigurationManager.getLongParameter("network.tcp.port." + this.tcp_listen_port + ".last.nonlocal.incoming", 0L);
/*     */         
/* 389 */         if (this.last_non_local_connection_time > SystemTime.getCurrentTime())
/*     */         {
/* 391 */           this.last_non_local_connection_time = SystemTime.getCurrentTime();
/*     */         }
/*     */         
/* 394 */         if (this.serverSelectors.length == 0)
/*     */         {
/*     */ 
/* 397 */           InetAddress[] bindAddresses = getEffectiveBindAddresses();
/*     */           
/* 399 */           List tempSelectors = new ArrayList(bindAddresses.length);
/*     */           
/*     */ 
/* 402 */           this.listenFailCounts = new int[bindAddresses.length];
/* 403 */           for (int i = 0; i < bindAddresses.length; i++)
/*     */           {
/* 405 */             InetAddress bindAddress = bindAddresses[i];
/*     */             
/* 407 */             if ((NetworkAdmin.getSingleton().hasIPV6Potential(true)) || (!(bindAddress instanceof Inet6Address))) {
/*     */               InetSocketAddress address;
/*     */               InetSocketAddress address;
/* 410 */               if (bindAddress != null) {
/* 411 */                 address = new InetSocketAddress(bindAddress, this.tcp_listen_port);
/*     */               } else {
/* 413 */                 address = new InetSocketAddress(this.tcp_listen_port);
/*     */               }
/*     */               VirtualServerChannelSelector serverSelector;
/*     */               VirtualServerChannelSelector serverSelector;
/* 417 */               if (bindAddresses.length == 1) {
/* 418 */                 serverSelector = VirtualServerChannelSelectorFactory.createBlocking(address, this.so_rcvbuf_size, this.selectListener);
/*     */               } else
/* 420 */                 serverSelector = VirtualServerChannelSelectorFactory.createNonBlocking(address, this.so_rcvbuf_size, this.selectListener);
/* 421 */               serverSelector.start();
/*     */               
/* 423 */               tempSelectors.add(serverSelector);
/*     */             }
/*     */           }
/* 426 */           if (tempSelectors.size() == 0) {
/* 427 */             Logger.log(new LogAlert(true, 1, MessageText.getString("network.bindError")));
/*     */           }
/* 429 */           this.serverSelectors = ((VirtualServerChannelSelector[])tempSelectors.toArray(new VirtualServerChannelSelector[tempSelectors.size()]));
/*     */         }
/*     */       }
/*     */       else {
/* 433 */         Logger.log(new LogEvent(LOGID, "Not starting TCP listener on port " + this.tcp_listen_port + " as protocol disabled"));
/*     */       }
/*     */     }
/*     */     finally {
/* 437 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void process(int local_port, TransportHelperFilter filter)
/*     */   {
/* 449 */     SocketChannel channel = ((TCPTransportHelper)filter.getHelper()).getSocketChannel();
/*     */     
/* 451 */     Socket socket = channel.socket();
/*     */     
/*     */     try
/*     */     {
/* 455 */       int so_sndbuf_size = COConfigurationManager.getIntParameter("network.tcp.socket.SO_SNDBUF");
/* 456 */       if (so_sndbuf_size > 0) { socket.setSendBufferSize(so_sndbuf_size);
/*     */       }
/* 458 */       String ip_tos = COConfigurationManager.getStringParameter("network.tcp.socket.IPDiffServ");
/* 459 */       if (ip_tos.length() > 0) socket.setTrafficClass(Integer.decode(ip_tos).intValue());
/*     */     }
/*     */     catch (Throwable t) {
/* 462 */       t.printStackTrace();
/*     */     }
/*     */     
/* 465 */     AEProxyAddressMapper.AppliedPortMapping applied_mapping = this.proxy_address_mapper.applyPortMapping(socket.getInetAddress(), socket.getPort());
/*     */     
/* 467 */     InetSocketAddress tcp_address = applied_mapping.getAddress();
/*     */     
/* 469 */     ConnectionEndpoint co_ep = new ConnectionEndpoint(tcp_address);
/*     */     
/* 471 */     Map<String, Object> properties = applied_mapping.getProperties();
/*     */     
/* 473 */     if (properties != null)
/*     */     {
/* 475 */       co_ep.addProperties(properties);
/*     */     }
/*     */     
/* 478 */     ProtocolEndpointTCP pe_tcp = (ProtocolEndpointTCP)ProtocolEndpointFactory.createEndpoint(1, co_ep, tcp_address);
/*     */     
/* 480 */     Transport transport = new TCPTransportImpl(pe_tcp, filter);
/*     */     
/* 482 */     this.incoming_manager.addConnection(local_port, filter, transport);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getLastNonLocalConnectionTime()
/*     */   {
/* 489 */     return this.last_non_local_connection_time;
/*     */   }
/*     */   
/*     */   private void restart() {
/*     */     try {
/* 494 */       this.this_mon.enter();
/*     */       
/* 496 */       for (int i = 0; i < this.serverSelectors.length; i++)
/* 497 */         this.serverSelectors[i].stop();
/* 498 */       this.serverSelectors = new VirtualServerChannelSelector[0];
/*     */     }
/*     */     finally {
/* 501 */       this.this_mon.exit();
/*     */     }
/*     */     try {
/* 504 */       Thread.sleep(1000L); } catch (Throwable t) { t.printStackTrace();
/*     */     }
/* 506 */     start();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/IncomingSocketChannelManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */