/*      */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector;
/*      */ import com.aelitis.azureus.core.networkmanager.VirtualChannelSelector.VirtualSelectorListener;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.proxy.AEProxyFactory;
/*      */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*      */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.NoRouteToHostException;
/*      */ import java.net.Socket;
/*      */ import java.net.SocketAddress;
/*      */ import java.net.SocketException;
/*      */ import java.net.SocketTimeoutException;
/*      */ import java.nio.channels.SocketChannel;
/*      */ import java.nio.channels.UnresolvedAddressException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*      */ public class TCPConnectionManager
/*      */ {
/*   53 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*      */   
/*   55 */   private static int CONNECT_SELECT_LOOP_TIME = 100;
/*   56 */   private static int CONNECT_SELECT_LOOP_MIN_TIME = 0;
/*      */   
/*   58 */   private static int MIN_SIMULTANIOUS_CONNECT_ATTEMPTS = 3;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   64 */   public static int MAX_SIMULTANIOUS_CONNECT_ATTEMPTS = COConfigurationManager.getIntParameter("network.max.simultaneous.connect.attempts");
/*      */   
/*   66 */   static { if (MAX_SIMULTANIOUS_CONNECT_ATTEMPTS < 1) {
/*   67 */       MAX_SIMULTANIOUS_CONNECT_ATTEMPTS = 1;
/*   68 */       COConfigurationManager.setParameter("network.max.simultaneous.connect.attempts", 1);
/*      */     }
/*      */     
/*   71 */     MIN_SIMULTANIOUS_CONNECT_ATTEMPTS = MAX_SIMULTANIOUS_CONNECT_ATTEMPTS - 2;
/*      */     
/*   73 */     if (MIN_SIMULTANIOUS_CONNECT_ATTEMPTS < 1) {
/*   74 */       MIN_SIMULTANIOUS_CONNECT_ATTEMPTS = 1;
/*      */     }
/*      */     
/*   77 */     COConfigurationManager.addParameterListener("network.max.simultaneous.connect.attempts", new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/*   79 */         TCPConnectionManager.MAX_SIMULTANIOUS_CONNECT_ATTEMPTS = COConfigurationManager.getIntParameter("network.max.simultaneous.connect.attempts");
/*   80 */         TCPConnectionManager.access$002(TCPConnectionManager.MAX_SIMULTANIOUS_CONNECT_ATTEMPTS - 2);
/*   81 */         if (TCPConnectionManager.MIN_SIMULTANIOUS_CONNECT_ATTEMPTS < 1) {
/*   82 */           TCPConnectionManager.access$002(1);
/*      */         }
/*      */         
/*      */       }
/*   86 */     });
/*   87 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.tcp.max.connections.outstanding" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   97 */         TCPConnectionManager.access$102(COConfigurationManager.getIntParameter("network.tcp.max.connections.outstanding"));
/*      */       }
/*      */       
/*  100 */     });
/*  101 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.tcp.connect.select.time", "network.tcp.connect.select.min.time" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  112 */         TCPConnectionManager.access$202(COConfigurationManager.getIntParameter("network.tcp.connect.select.time"));
/*  113 */         TCPConnectionManager.access$302(COConfigurationManager.getIntParameter("network.tcp.connect.select.min.time"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static int max_outbound_connections;
/*      */   private int rcv_size;
/*      */   private int snd_size;
/*      */   private String ip_tos;
/*      */   public TCPConnectionManager()
/*      */   {
/*  124 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.tcp.socket.SO_RCVBUF", "network.tcp.socket.SO_SNDBUF", "network.tcp.socket.IPDiffServ", "network.bind.local.port" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  137 */         TCPConnectionManager.this.rcv_size = COConfigurationManager.getIntParameter("network.tcp.socket.SO_RCVBUF");
/*      */         
/*  139 */         TCPConnectionManager.this.snd_size = COConfigurationManager.getIntParameter("network.tcp.socket.SO_SNDBUF");
/*      */         
/*  141 */         TCPConnectionManager.this.ip_tos = COConfigurationManager.getStringParameter("network.tcp.socket.IPDiffServ");
/*      */         
/*  143 */         TCPConnectionManager.this.local_bind_port = COConfigurationManager.getIntParameter("network.bind.local.port");
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  151 */     });
/*  152 */     this.connect_selector = new VirtualChannelSelector("Connect/Disconnect Manager", 8, true);
/*      */     
/*      */ 
/*      */ 
/*  156 */     this.new_requests = new TreeSet(new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(TCPConnectionManager.ConnectionRequest r1, TCPConnectionManager.ConnectionRequest r2)
/*      */       {
/*      */ 
/*      */ 
/*  165 */         if (r1 == r2)
/*      */         {
/*  167 */           return 0;
/*      */         }
/*      */         
/*  170 */         int res = TCPConnectionManager.ConnectionRequest.access$800(r1) - TCPConnectionManager.ConnectionRequest.access$800(r2);
/*      */         
/*  172 */         if (res == 0)
/*      */         {
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
/*  190 */           res = TCPConnectionManager.ConnectionRequest.access$900(r1) - TCPConnectionManager.ConnectionRequest.access$900(r2);
/*      */           
/*  192 */           if (res == 0)
/*      */           {
/*  194 */             long l = TCPConnectionManager.ConnectionRequest.access$1000(r1) - TCPConnectionManager.ConnectionRequest.access$1000(r2);
/*      */             
/*  196 */             if (l < 0L)
/*      */             {
/*  198 */               res = -1;
/*      */             }
/*  200 */             else if (l > 0L)
/*      */             {
/*  202 */               res = 1;
/*      */             }
/*      */             else
/*      */             {
/*  206 */               Debug.out("arghhh, borkage");
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  211 */         return res;
/*      */       }
/*      */       
/*  214 */     });
/*  215 */     this.canceled_requests = new ArrayList();
/*      */     
/*  217 */     this.new_canceled_mon = new AEMonitor("ConnectDisconnectManager:NCM");
/*      */     
/*  219 */     this.pending_attempts = new HashMap();
/*      */     
/*  221 */     this.pending_closes = new LinkedList();
/*      */     
/*  223 */     this.delayed_closes = new HashMap();
/*      */     
/*  225 */     this.pending_closes_mon = new AEMonitor("ConnectDisconnectManager:PC");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  233 */     Set<String> types = new HashSet();
/*      */     
/*  235 */     types.add("net.tcp.outbound.connect.queue.length");
/*  236 */     types.add("net.tcp.outbound.cancel.queue.length");
/*  237 */     types.add("net.tcp.outbound.close.queue.length");
/*  238 */     types.add("net.tcp.outbound.pending.queue.length");
/*      */     
/*  240 */     AzureusCoreStats.registerProvider(types, new AzureusCoreStatsProvider()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void updateStats(Set<String> types, Map<String, Object> values)
/*      */       {
/*      */ 
/*      */ 
/*  249 */         if (types.contains("net.tcp.outbound.connect.queue.length"))
/*      */         {
/*  251 */           values.put("net.tcp.outbound.connect.queue.length", new Long(TCPConnectionManager.this.new_requests.size()));
/*      */         }
/*      */         
/*  254 */         if (types.contains("net.tcp.outbound.cancel.queue.length"))
/*      */         {
/*  256 */           values.put("net.tcp.outbound.cancel.queue.length", new Long(TCPConnectionManager.this.canceled_requests.size()));
/*      */         }
/*      */         
/*  259 */         if (types.contains("net.tcp.outbound.close.queue.length"))
/*      */         {
/*  261 */           values.put("net.tcp.outbound.close.queue.length", new Long(TCPConnectionManager.this.pending_closes.size()));
/*      */         }
/*      */         
/*  264 */         if (types.contains("net.tcp.outbound.pending.queue.length"))
/*      */         {
/*  266 */           values.put("net.tcp.outbound.pending.queue.length", new Long(TCPConnectionManager.this.pending_attempts.size()));
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  271 */     });
/*  272 */     new AEThread2("ConnectDisconnectManager", true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */         for (;;)
/*      */         {
/*  279 */           TCPConnectionManager.this.addNewOutboundRequests();
/*      */           
/*  281 */           TCPConnectionManager.this.runSelect();
/*      */           
/*  283 */           TCPConnectionManager.this.doClosings();
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxOutboundPermitted()
/*      */   {
/*  292 */     return Math.max(max_outbound_connections - this.new_requests.size(), 0);
/*      */   }
/*      */   
/*      */ 
/*      */   private void addNewOutboundRequests()
/*      */   {
/*  298 */     while (this.pending_attempts.size() < MIN_SIMULTANIOUS_CONNECT_ATTEMPTS)
/*      */     {
/*  300 */       ConnectionRequest cr = null;
/*      */       try
/*      */       {
/*  303 */         this.new_canceled_mon.enter();
/*      */         
/*  305 */         if (this.new_requests.isEmpty())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  315 */           this.new_canceled_mon.exit(); break;
/*      */         }
/*  307 */         Iterator<ConnectionRequest> it = this.new_requests.iterator();
/*      */         
/*  309 */         cr = (ConnectionRequest)it.next();
/*      */         
/*  311 */         it.remove();
/*      */       }
/*      */       finally
/*      */       {
/*  315 */         this.new_canceled_mon.exit();
/*      */       }
/*      */       
/*  318 */       if (cr != null)
/*      */       {
/*  320 */         addNewRequest(cr);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addNewRequest(final ConnectionRequest request)
/*      */   {
/*  331 */     request.setConnectTimeout(request.listener.connectAttemptStarted(ConnectionRequest.access$1800(request)));
/*      */     
/*      */ 
/*  334 */     boolean ipv6problem = false;
/*  335 */     boolean bind_failed = false;
/*      */     try
/*      */     {
/*  338 */       request.channel = SocketChannel.open();
/*      */       
/*  340 */       InetAddress bindIP = null;
/*      */       try
/*      */       {
/*  343 */         if (this.rcv_size > 0) {
/*  344 */           if (Logger.isEnabled()) {
/*  345 */             Logger.log(new LogEvent(LOGID, "Setting socket receive buffer size for outgoing connection [" + request.address + "] to: " + this.rcv_size));
/*      */           }
/*      */           
/*  348 */           request.channel.socket().setReceiveBufferSize(this.rcv_size);
/*      */         }
/*      */         
/*  351 */         if (this.snd_size > 0) {
/*  352 */           if (Logger.isEnabled()) {
/*  353 */             Logger.log(new LogEvent(LOGID, "Setting socket send buffer size for outgoing connection [" + request.address + "] to: " + this.snd_size));
/*      */           }
/*      */           
/*  356 */           request.channel.socket().setSendBufferSize(this.snd_size);
/*      */         }
/*      */         
/*  359 */         if (this.ip_tos.length() > 0) {
/*  360 */           if (Logger.isEnabled()) {
/*  361 */             Logger.log(new LogEvent(LOGID, "Setting socket TOS field for outgoing connection [" + request.address + "] to: " + this.ip_tos));
/*      */           }
/*      */           
/*  364 */           request.channel.socket().setTrafficClass(Integer.decode(this.ip_tos).intValue());
/*      */         }
/*      */         
/*  367 */         if (this.local_bind_port > 0) {
/*  368 */           request.channel.socket().setReuseAddress(true);
/*      */         }
/*      */         try
/*      */         {
/*  372 */           bindIP = NetworkAdmin.getSingleton().getMultiHomedOutgoingRoundRobinBindAddress(request.address.getAddress());
/*  373 */           if (bindIP != null)
/*      */           {
/*      */ 
/*      */ 
/*  377 */             if ((bindIP.isAnyLocalAddress()) || (!AEProxyFactory.isPluginProxy(request.address))) {
/*  378 */               if (Logger.isEnabled()) Logger.log(new LogEvent(LOGID, "Binding outgoing connection [" + request.address + "] to local IP address: " + bindIP + ":" + this.local_bind_port));
/*  379 */               request.channel.socket().bind(new InetSocketAddress(bindIP, this.local_bind_port));
/*      */             }
/*      */           }
/*  382 */           else if (this.local_bind_port > 0) {
/*  383 */             if (Logger.isEnabled()) Logger.log(new LogEvent(LOGID, "Binding outgoing connection [" + request.address + "] to local port #: " + this.local_bind_port));
/*  384 */             request.channel.socket().bind(new InetSocketAddress(this.local_bind_port));
/*      */           }
/*      */         }
/*      */         catch (SocketException e) {
/*  388 */           bind_failed = true;
/*      */           
/*  390 */           String msg = e.getMessage().toLowerCase();
/*      */           
/*  392 */           if (((msg.contains("address family not supported by protocol family")) || (msg.contains("protocol family unavailable"))) && (!NetworkAdmin.getSingleton().hasIPV6Potential(true)))
/*      */           {
/*      */ 
/*  395 */             ipv6problem = true;
/*      */           }
/*      */           
/*      */ 
/*  399 */           throw e;
/*      */         }
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  404 */         if ((bind_failed) && (NetworkAdmin.getSingleton().mustBind()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  409 */           throw t;
/*      */         }
/*  411 */         if (!ipv6problem)
/*      */         {
/*      */ 
/*      */ 
/*  415 */           String msg = "Error while processing advanced socket options (rcv=" + this.rcv_size + ", snd=" + this.snd_size + ", tos=" + this.ip_tos + ", port=" + this.local_bind_port + ", bind=" + bindIP + ")";
/*      */           
/*  417 */           Logger.log(new LogAlert(false, msg, t));
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  422 */           throw t;
/*      */         }
/*      */       }
/*      */       
/*  426 */       request.channel.configureBlocking(false);
/*  427 */       request.connect_start_time = SystemTime.getMonotonousTime();
/*      */       
/*  429 */       if (request.channel.connect(request.address))
/*      */       {
/*  431 */         finishConnect(request);
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */         try
/*      */         {
/*  438 */           this.new_canceled_mon.enter();
/*      */           
/*  440 */           this.pending_attempts.put(request, null);
/*      */         }
/*      */         finally
/*      */         {
/*  444 */           this.new_canceled_mon.exit();
/*      */         }
/*      */         
/*  447 */         this.connect_selector.register(request.channel, new VirtualChannelSelector.VirtualSelectorListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public boolean selectSuccess(VirtualChannelSelector selector, SocketChannel sc, Object attachment)
/*      */           {
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*  458 */               TCPConnectionManager.this.new_canceled_mon.enter();
/*      */               
/*  460 */               TCPConnectionManager.this.pending_attempts.remove(request);
/*      */             }
/*      */             finally
/*      */             {
/*  464 */               TCPConnectionManager.this.new_canceled_mon.exit();
/*      */             }
/*      */             
/*  467 */             TCPConnectionManager.this.finishConnect(request);
/*      */             
/*  469 */             return true;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void selectFailure(VirtualChannelSelector selector, SocketChannel sc, Object attachment, Throwable msg)
/*      */           {
/*      */             try
/*      */             {
/*  480 */               TCPConnectionManager.this.new_canceled_mon.enter();
/*      */               
/*  482 */               TCPConnectionManager.this.pending_attempts.remove(request);
/*      */             }
/*      */             finally
/*      */             {
/*  486 */               TCPConnectionManager.this.new_canceled_mon.exit();
/*      */             }
/*      */             
/*  489 */             TCPConnectionManager.this.closeConnection(TCPConnectionManager.ConnectionRequest.access$2100(request));
/*      */             
/*  491 */             TCPConnectionManager.ConnectionRequest.access$1900(request).connectFailure(msg); } }, null);
/*      */       }
/*      */       
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  497 */       String full = request.address.toString();
/*  498 */       String hostname = request.address.getHostName();
/*  499 */       int port = request.address.getPort();
/*  500 */       boolean unresolved = request.address.isUnresolved();
/*  501 */       InetAddress inet_address = request.address.getAddress();
/*  502 */       String full_sub = inet_address == null ? request.address.toString() : inet_address.toString();
/*  503 */       String host_address = inet_address == null ? request.address.toString() : inet_address.getHostAddress();
/*      */       
/*  505 */       String msg = "ConnectDisconnectManager::address exception: full=" + full + ", hostname=" + hostname + ", port=" + port + ", unresolved=" + unresolved + ", full_sub=" + full_sub + ", host_address=" + host_address;
/*      */       
/*  507 */       if (request.channel != null) {
/*  508 */         String channel = request.channel.toString();
/*  509 */         Socket socket = request.channel.socket();
/*  510 */         String socket_string = socket.toString();
/*  511 */         InetAddress local_address = socket.getLocalAddress();
/*  512 */         String local_address_string = local_address == null ? "<null>" : local_address.toString();
/*  513 */         int local_port = socket.getLocalPort();
/*  514 */         SocketAddress ra = socket.getRemoteSocketAddress();
/*      */         String remote_address;
/*  516 */         String remote_address; if (ra != null) remote_address = ra.toString(); else
/*  517 */           remote_address = "<null>";
/*  518 */         int remote_port = socket.getPort();
/*      */         
/*  520 */         msg = msg + "\n channel=" + channel + ", socket=" + socket_string + ", local_address=" + local_address_string + ", local_port=" + local_port + ", remote_address=" + remote_address + ", remote_port=" + remote_port;
/*      */       }
/*      */       else {
/*  523 */         msg = msg + "\n channel=<null>";
/*      */       }
/*      */       
/*  526 */       if ((ipv6problem) || ((t instanceof UnresolvedAddressException)) || ((t instanceof NoRouteToHostException)))
/*      */       {
/*  528 */         Logger.log(new LogEvent(LOGID, 1, msg));
/*      */       }
/*      */       else
/*      */       {
/*  532 */         Logger.log(new LogEvent(LOGID, 3, msg, t));
/*      */       }
/*      */       
/*  535 */       if (request.channel != null)
/*      */       {
/*  537 */         closeConnection(request.channel);
/*      */       }
/*      */       
/*  540 */       request.listener.connectFailure(t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void finishConnect(ConnectionRequest request)
/*      */   {
/*      */     try
/*      */     {
/*  552 */       if (request.channel.finishConnect())
/*      */       {
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
/*  566 */         boolean canceled = false;
/*      */         try {
/*  568 */           this.new_canceled_mon.enter();
/*      */           
/*  570 */           canceled = this.canceled_requests.contains(request.listener);
/*      */         }
/*      */         finally
/*      */         {
/*  574 */           this.new_canceled_mon.exit();
/*      */         }
/*      */         
/*  577 */         if (canceled)
/*      */         {
/*  579 */           closeConnection(request.channel);
/*      */         }
/*      */         else
/*      */         {
/*  583 */           this.connect_selector.cancel(request.channel);
/*      */           
/*  585 */           request.listener.connectSuccess(request.channel);
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/*  591 */         Debug.out("finishConnect() failed");
/*      */         
/*  593 */         request.listener.connectFailure(new Throwable("finishConnect() failed"));
/*      */         
/*  595 */         closeConnection(request.channel);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  610 */       request.listener.connectFailure(t);
/*      */       
/*  612 */       closeConnection(request.channel);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void runSelect()
/*      */   {
/*      */     try
/*      */     {
/*  621 */       this.new_canceled_mon.enter();
/*      */       
/*  623 */       for (Iterator<ConnectListener> can_it = this.canceled_requests.iterator(); can_it.hasNext();)
/*      */       {
/*  625 */         key = (ConnectListener)can_it.next();
/*      */         
/*  627 */         for (pen_it = this.pending_attempts.keySet().iterator(); pen_it.hasNext();)
/*      */         {
/*  629 */           ConnectionRequest request = (ConnectionRequest)pen_it.next();
/*      */           
/*  631 */           if (request.listener == key)
/*      */           {
/*  633 */             this.connect_selector.cancel(request.channel);
/*      */             
/*  635 */             closeConnection(request.channel);
/*      */             
/*  637 */             pen_it.remove();
/*      */             
/*  639 */             break;
/*      */           }
/*      */         } }
/*      */       ConnectListener key;
/*      */       Iterator<ConnectionRequest> pen_it;
/*  644 */       this.canceled_requests.clear();
/*      */     }
/*      */     finally
/*      */     {
/*  648 */       this.new_canceled_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  654 */       if (CONNECT_SELECT_LOOP_MIN_TIME > 0)
/*      */       {
/*  656 */         long start = SystemTime.getHighPrecisionCounter();
/*      */         
/*  658 */         this.connect_selector.select(CONNECT_SELECT_LOOP_TIME);
/*      */         
/*  660 */         long duration = SystemTime.getHighPrecisionCounter() - start;
/*      */         
/*  662 */         duration /= 1000000L;
/*      */         
/*  664 */         long sleep = CONNECT_SELECT_LOOP_MIN_TIME - duration;
/*      */         
/*  666 */         if (sleep > 0L) {
/*      */           try
/*      */           {
/*  669 */             Thread.sleep(sleep);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */       else {
/*  675 */         this.connect_selector.select(CONNECT_SELECT_LOOP_TIME);
/*      */       }
/*      */     }
/*      */     catch (Throwable t) {
/*  679 */       Debug.out("connnectSelectLoop() EXCEPTION: ", t);
/*      */     }
/*      */     
/*      */ 
/*  683 */     int num_stalled_requests = 0;
/*      */     
/*  685 */     long now = SystemTime.getMonotonousTime();
/*      */     
/*  687 */     List<ConnectionRequest> timeouts = null;
/*      */     try {
/*  689 */       this.new_canceled_mon.enter();
/*      */       
/*  691 */       for (i = this.pending_attempts.keySet().iterator(); ((Iterator)i).hasNext();)
/*      */       {
/*  693 */         ConnectionRequest request = (ConnectionRequest)((Iterator)i).next();
/*      */         
/*  695 */         long waiting_time = now - request.connect_start_time;
/*      */         
/*  697 */         if (waiting_time > request.connect_timeout)
/*      */         {
/*  699 */           ((Iterator)i).remove();
/*      */           
/*  701 */           SocketChannel channel = request.channel;
/*      */           
/*  703 */           this.connect_selector.cancel(channel);
/*      */           
/*  705 */           closeConnection(channel);
/*      */           
/*  707 */           if (timeouts == null)
/*      */           {
/*  709 */             timeouts = new ArrayList();
/*      */           }
/*      */           
/*  712 */           timeouts.add(request);
/*      */         }
/*  714 */         else if (waiting_time >= 3000L)
/*      */         {
/*  716 */           num_stalled_requests++;
/*      */         }
/*  718 */         else if (waiting_time < 0L)
/*      */         {
/*  720 */           request.connect_start_time = now;
/*      */         }
/*      */       }
/*      */     } finally {
/*      */       Object i;
/*  725 */       this.new_canceled_mon.exit();
/*      */     }
/*      */     
/*  728 */     if (timeouts != null)
/*      */     {
/*  730 */       for (ConnectionRequest request : timeouts)
/*      */       {
/*  732 */         InetSocketAddress sock_address = request.address;
/*      */         
/*  734 */         InetAddress a = sock_address.getAddress();
/*      */         
/*      */         String target;
/*      */         String target;
/*  738 */         if (a != null)
/*      */         {
/*  740 */           target = a.getHostAddress() + ":" + sock_address.getPort();
/*      */         }
/*      */         else
/*      */         {
/*  744 */           target = sock_address.toString();
/*      */         }
/*      */         
/*  747 */         request.listener.connectFailure(new SocketTimeoutException("Connection attempt to " + target + " aborted: timed out after " + request.connect_timeout / 1000 + "sec"));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  752 */     if ((num_stalled_requests == this.pending_attempts.size()) && (this.pending_attempts.size() < MAX_SIMULTANIOUS_CONNECT_ATTEMPTS))
/*      */     {
/*  754 */       ConnectionRequest cr = null;
/*      */       try
/*      */       {
/*  757 */         this.new_canceled_mon.enter();
/*      */         
/*  759 */         if (!this.new_requests.isEmpty())
/*      */         {
/*  761 */           Iterator<ConnectionRequest> it = this.new_requests.iterator();
/*      */           
/*  763 */           cr = (ConnectionRequest)it.next();
/*      */           
/*  765 */           it.remove();
/*      */         }
/*      */       } finally {
/*  768 */         this.new_canceled_mon.exit();
/*      */       }
/*      */       
/*  771 */       if (cr != null) {
/*  772 */         addNewRequest(cr);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void doClosings()
/*      */   {
/*      */     try {
/*  780 */       this.pending_closes_mon.enter();
/*      */       
/*  782 */       long now = SystemTime.getMonotonousTime();
/*      */       
/*  784 */       if (this.delayed_closes.size() > 0)
/*      */       {
/*  786 */         Iterator<Map.Entry<SocketChannel, Long>> it = this.delayed_closes.entrySet().iterator();
/*      */         
/*  788 */         while (it.hasNext())
/*      */         {
/*  790 */           Map.Entry<SocketChannel, Long> entry = (Map.Entry)it.next();
/*      */           
/*  792 */           long wait = ((Long)entry.getValue()).longValue() - now;
/*      */           
/*  794 */           if ((wait < 0L) || (wait > 60000L))
/*      */           {
/*  796 */             this.pending_closes.addLast(entry.getKey());
/*      */             
/*  798 */             it.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  803 */       while (!this.pending_closes.isEmpty())
/*      */       {
/*  805 */         SocketChannel channel = (SocketChannel)this.pending_closes.removeFirst();
/*      */         
/*  807 */         if (channel != null)
/*      */         {
/*  809 */           this.connect_selector.cancel(channel);
/*      */           try
/*      */           {
/*  812 */             channel.close();
/*      */ 
/*      */           }
/*      */           catch (Throwable t) {}
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  822 */       this.pending_closes_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private int local_bind_port;
/*      */   private static final int CONNECT_ATTEMPT_TIMEOUT = 15000;
/*      */   private static final int CONNECT_ATTEMPT_STALL_TIME = 3000;
/*      */   private static final boolean SHOW_CONNECT_STATS = false;
/*      */   private final VirtualChannelSelector connect_selector;
/*      */   public void requestNewConnection(InetSocketAddress address, ConnectListener listener, int priority)
/*      */   {
/*  833 */     requestNewConnection(address, listener, 15000, priority);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestNewConnection(InetSocketAddress address, ConnectListener listener, int connect_timeout, int priority)
/*      */   {
/*  843 */     if (address.getPort() == 0)
/*      */     {
/*      */       try {
/*  846 */         listener.connectFailure(new Exception("Invalid port, connection to " + address + " abandoned"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  850 */         Debug.out(e);
/*      */       }
/*      */       
/*  853 */       return;
/*      */     }
/*      */     
/*  856 */     List<ConnectionRequest> kicked = null;
/*  857 */     boolean duplicate = false;
/*      */     try
/*      */     {
/*  860 */       this.new_canceled_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  866 */       ConnectionRequest cr = new ConnectionRequest(this.connection_request_id_next++, address, listener, connect_timeout, priority, null);
/*      */       
/*      */ 
/*      */ 
/*  870 */       if (this.new_requests.contains(cr))
/*      */       {
/*  872 */         duplicate = true;
/*      */       }
/*      */       else
/*      */       {
/*  876 */         this.new_requests.add(cr);
/*      */         
/*  878 */         if (this.new_requests.size() >= max_outbound_connections)
/*      */         {
/*  880 */           if (!this.max_conn_exceeded_logged)
/*      */           {
/*  882 */             this.max_conn_exceeded_logged = true;
/*      */             
/*  884 */             Debug.out("TCPConnectionManager: max outbound connection limit reached (" + max_outbound_connections + ")");
/*      */           }
/*      */         }
/*      */         
/*  888 */         if (priority == 1)
/*      */         {
/*  890 */           for (pen_it = this.pending_attempts.keySet().iterator(); pen_it.hasNext();)
/*      */           {
/*  892 */             ConnectionRequest request = (ConnectionRequest)pen_it.next();
/*      */             
/*  894 */             if (request.priority == 4)
/*      */             {
/*  896 */               if (!this.canceled_requests.contains(request.listener))
/*      */               {
/*  898 */                 this.canceled_requests.add(request.listener);
/*      */                 
/*  900 */                 if (kicked == null)
/*      */                 {
/*  902 */                   kicked = new ArrayList();
/*      */                 }
/*      */                 
/*  905 */                 kicked.add(request);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/*      */       Iterator<ConnectionRequest> pen_it;
/*  913 */       this.new_canceled_mon.exit();
/*      */     }
/*      */     
/*  916 */     if (duplicate) {
/*      */       try
/*      */       {
/*  919 */         listener.connectFailure(new Exception("Connection request already queued for " + address));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  923 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  927 */     if (kicked != null)
/*      */     {
/*  929 */       for (int i = 0; i < kicked.size(); i++)
/*      */         try
/*      */         {
/*  932 */           ((ConnectionRequest)kicked.get(i)).listener.connectFailure(new Exception("Low priority connection request abandoned in favour of high priority"));
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  937 */           Debug.printStackTrace(e);
/*      */         } }
/*      */   }
/*      */   
/*      */   private long connection_request_id_next;
/*      */   private final Set<ConnectionRequest> new_requests;
/*      */   private final List<ConnectListener> canceled_requests;
/*      */   private final AEMonitor new_canceled_mon;
/*      */   private final Map<ConnectionRequest, Object> pending_attempts;
/*      */   private final LinkedList<SocketChannel> pending_closes;
/*      */   private final Map<SocketChannel, Long> delayed_closes;
/*      */   private final AEMonitor pending_closes_mon;
/*      */   private boolean max_conn_exceeded_logged;
/*      */   public void closeConnection(SocketChannel channel) {
/*  951 */     closeConnection(channel, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void closeConnection(SocketChannel channel, int delay)
/*      */   {
/*      */     try
/*      */     {
/*  960 */       this.pending_closes_mon.enter();
/*      */       
/*  962 */       if (delay == 0)
/*      */       {
/*  964 */         if (!this.delayed_closes.containsKey(channel))
/*      */         {
/*  966 */           if (!this.pending_closes.contains(channel))
/*      */           {
/*  968 */             this.pending_closes.addLast(channel);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  973 */         this.delayed_closes.put(channel, new Long(SystemTime.getMonotonousTime() + delay));
/*      */       }
/*      */     }
/*      */     finally {
/*  977 */       this.pending_closes_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void cancelRequest(ConnectListener listener_key)
/*      */   {
/*      */     try
/*      */     {
/*  988 */       this.new_canceled_mon.enter();
/*      */       
/*      */ 
/*  991 */       for (Iterator<ConnectionRequest> i = this.new_requests.iterator(); i.hasNext();) {
/*  992 */         ConnectionRequest request = (ConnectionRequest)i.next();
/*  993 */         if (request.listener == listener_key) {
/*  994 */           i.remove(); return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  999 */       this.canceled_requests.add(listener_key);
/*      */     }
/*      */     finally {
/* 1002 */       this.new_canceled_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public static abstract interface ConnectListener { public abstract int connectAttemptStarted(int paramInt);
/*      */     
/*      */     public abstract void connectSuccess(SocketChannel paramSocketChannel);
/*      */     
/*      */     public abstract void connectFailure(Throwable paramThrowable);
/*      */   }
/*      */   
/*      */   private static class ConnectionRequest { private final InetSocketAddress address;
/*      */     private final TCPConnectionManager.ConnectListener listener;
/*      */     private final long request_start_time;
/*      */     private long connect_start_time;
/*      */     private int connect_timeout;
/*      */     private SocketChannel channel;
/*      */     private final short rand;
/*      */     private final int priority;
/*      */     private final long id;
/*      */     
/* 1023 */     private ConnectionRequest(long _id, InetSocketAddress _address, TCPConnectionManager.ConnectListener _listener, int _connect_timeout, int _priority) { this.id = _id;
/* 1024 */       this.address = _address;
/* 1025 */       this.listener = _listener;
/* 1026 */       this.connect_timeout = _connect_timeout;
/* 1027 */       this.request_start_time = SystemTime.getMonotonousTime();
/* 1028 */       this.rand = ((short)RandomUtils.nextInt(32767));
/* 1029 */       this.priority = _priority;
/*      */     }
/*      */     
/*      */ 
/*      */     private int getConnectTimeout()
/*      */     {
/* 1035 */       return this.connect_timeout;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setConnectTimeout(int _ct)
/*      */     {
/* 1042 */       this.connect_timeout = _ct;
/*      */     }
/*      */     
/*      */ 
/*      */     private long getID()
/*      */     {
/* 1048 */       return this.id;
/*      */     }
/*      */     
/*      */ 
/*      */     private int getPriority()
/*      */     {
/* 1054 */       return this.priority;
/*      */     }
/*      */     
/*      */ 
/*      */     private short getRandom()
/*      */     {
/* 1060 */       return this.rand;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/TCPConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */