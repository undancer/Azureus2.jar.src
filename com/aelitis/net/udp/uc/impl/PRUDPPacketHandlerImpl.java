/*      */ package com.aelitis.net.udp.uc.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener;
/*      */ import com.aelitis.azureus.core.util.AEPriorityMixin;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacket;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketHandlerException;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketReceiver;
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*      */ import com.aelitis.net.udp.uc.PRUDPPrimordialHandler;
/*      */ import com.aelitis.net.udp.uc.PRUDPRequestHandler;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.net.BindException;
/*      */ import java.net.DatagramPacket;
/*      */ import java.net.DatagramSocket;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.PasswordAuthentication;
/*      */ import java.net.SocketTimeoutException;
/*      */ import java.nio.channels.UnsupportedAddressTypeException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor2;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Average;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ 
/*      */ public class PRUDPPacketHandlerImpl implements PRUDPPacketHandler
/*      */ {
/*   54 */   private static final LogIDs LOGID = LogIDs.NET;
/*      */   
/*   56 */   private boolean TRACE_REQUESTS = false;
/*      */   private static int MAX_PACKET_SIZE;
/*      */   private static final long MAX_SEND_QUEUE_DATA_SIZE = 2097152L;
/*      */   private static final long MAX_RECV_QUEUE_DATA_SIZE = 1048576L;
/*      */   
/*   61 */   static { COConfigurationManager.addAndFireParameterListener("network.udp.mtu.size", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameter_name)
/*      */       {
/*      */ 
/*      */ 
/*   69 */         PRUDPPacketHandlerImpl.access$002(COConfigurationManager.getIntParameter(parameter_name));
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   79 */     });
/*   80 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Enable.Proxy", "Enable.SOCKS" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameter_name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   91 */         boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/*   92 */         boolean enable_socks = COConfigurationManager.getBooleanParameter("Enable.SOCKS");
/*      */         
/*   94 */         PRUDPPacketHandlerImpl.access$102((enable_proxy) && (enable_socks));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private static boolean use_socks;
/*      */   private int port;
/*      */   private DatagramSocket socket;
/*  103 */   private CopyOnWriteList<PRUDPPrimordialHandler> primordial_handlers = new CopyOnWriteList();
/*      */   
/*      */   private PRUDPRequestHandler request_handler;
/*  106 */   private PRUDPPacketHandlerStatsImpl stats = new PRUDPPacketHandlerStatsImpl(this);
/*      */   
/*      */ 
/*  109 */   private Map requests = new org.gudy.azureus2.core3.util.LightHashMap();
/*  110 */   private AEMonitor2 requests_mon = new AEMonitor2("PRUDPPH:req");
/*      */   
/*      */ 
/*  113 */   private AEMonitor2 send_queue_mon = new AEMonitor2("PRUDPPH:sd");
/*      */   private long send_queue_data_size;
/*  115 */   private final List[] send_queues = { new LinkedList(), new LinkedList(), new LinkedList() };
/*  116 */   private AESemaphore send_queue_sem = new AESemaphore("PRUDPPH:sq");
/*      */   
/*      */   private AEThread send_thread;
/*  119 */   private AEMonitor recv_queue_mon = new AEMonitor("PRUDPPH:rq");
/*      */   private long recv_queue_data_size;
/*  121 */   private List recv_queue = new ArrayList();
/*  122 */   private AESemaphore recv_queue_sem = new AESemaphore("PRUDPPH:rq");
/*      */   
/*      */   private AEThread recv_thread;
/*  125 */   private int send_delay = 0;
/*  126 */   private int receive_delay = 0;
/*  127 */   private int queued_request_timeout = 0;
/*      */   
/*      */   private long total_requests_received;
/*      */   private long total_requests_processed;
/*      */   private long total_replies;
/*      */   private long last_error_report;
/*  133 */   private Average request_receive_average = Average.getInstance(1000, 10);
/*      */   
/*  135 */   private AEMonitor bind_address_mon = new AEMonitor("PRUDPPH:bind");
/*      */   
/*      */   private InetAddress default_bind_ip;
/*      */   
/*      */   private InetAddress explicit_bind_ip;
/*      */   
/*      */   private volatile InetAddress current_bind_ip;
/*      */   private volatile InetAddress target_bind_ip;
/*      */   private volatile boolean failed;
/*      */   private volatile boolean destroyed;
/*  145 */   private AESemaphore destroy_sem = new AESemaphore("PRUDPPacketHandler:destroy");
/*      */   
/*      */ 
/*      */   private Throwable init_error;
/*      */   
/*      */ 
/*      */   private PRUDPPacketHandlerImpl altProtocolDelegate;
/*      */   
/*      */ 
/*      */   private final PacketTransformer packet_transformer;
/*      */   
/*      */ 
/*      */   protected PRUDPPacketHandlerImpl(int _port, InetAddress _bind_ip, PacketTransformer _packet_transformer)
/*      */   {
/*  159 */     this.port = _port;
/*  160 */     this.explicit_bind_ip = _bind_ip;
/*  161 */     this.packet_transformer = _packet_transformer;
/*      */     
/*  163 */     this.default_bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*      */     
/*  165 */     calcBind();
/*      */     
/*  167 */     final AESemaphore init_sem = new AESemaphore("PRUDPPacketHandler:init");
/*      */     
/*  169 */     new AEThread2("PRUDPPacketReciever:" + this.port, true)
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  174 */         PRUDPPacketHandlerImpl.this.receiveLoop(init_sem);
/*      */       }
/*      */       
/*      */ 
/*  178 */     }.start();
/*  179 */     final TimerEventPeriodic[] f_ev = { null };
/*      */     
/*  181 */     TimerEventPeriodic ev = SimpleTimer.addPeriodicEvent("PRUDP:timeouts", 5000L, new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  191 */         if ((PRUDPPacketHandlerImpl.this.destroyed) && (f_ev[0] != null))
/*      */         {
/*  193 */           f_ev[0].cancel();
/*      */         }
/*  195 */         PRUDPPacketHandlerImpl.this.checkTimeouts();
/*      */       }
/*      */       
/*  198 */     });
/*  199 */     f_ev[0] = ev;
/*      */     
/*  201 */     init_sem.reserve();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasPrimordialHandler()
/*      */   {
/*  207 */     synchronized (this.primordial_handlers)
/*      */     {
/*  209 */       return this.primordial_handlers.size() > 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPrimordialHandler(PRUDPPrimordialHandler handler)
/*      */   {
/*  217 */     synchronized (this.primordial_handlers)
/*      */     {
/*  219 */       if (this.primordial_handlers.contains(handler))
/*      */       {
/*  221 */         Debug.out("Primordial handler already added!"); return;
/*      */       }
/*      */       
/*      */       int priority;
/*      */       
/*      */       int priority;
/*      */       
/*  228 */       if ((handler instanceof AEPriorityMixin))
/*      */       {
/*  230 */         priority = ((AEPriorityMixin)handler).getPriority();
/*      */       }
/*      */       else
/*      */       {
/*  234 */         priority = 2;
/*      */       }
/*      */       
/*  237 */       List<PRUDPPrimordialHandler> existing = this.primordial_handlers.getList();
/*      */       
/*  239 */       int insert_at = -1;
/*      */       
/*  241 */       for (int i = 0; i < existing.size(); i++)
/*      */       {
/*  243 */         PRUDPPrimordialHandler e = (PRUDPPrimordialHandler)existing.get(i);
/*      */         
/*      */         int existing_priority;
/*      */         int existing_priority;
/*  247 */         if ((e instanceof AEPriorityMixin))
/*      */         {
/*  249 */           existing_priority = ((AEPriorityMixin)e).getPriority();
/*      */         }
/*      */         else
/*      */         {
/*  253 */           existing_priority = 2;
/*      */         }
/*      */         
/*  256 */         if (existing_priority < priority)
/*      */         {
/*  258 */           insert_at = i;
/*      */           
/*  260 */           break;
/*      */         }
/*      */       }
/*      */       
/*  264 */       if (insert_at >= 0)
/*      */       {
/*  266 */         this.primordial_handlers.add(insert_at, handler);
/*      */       }
/*      */       else
/*      */       {
/*  270 */         this.primordial_handlers.add(handler);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removePrimordialHandler(PRUDPPrimordialHandler handler)
/*      */   {
/*  281 */     synchronized (this.primordial_handlers)
/*      */     {
/*  283 */       if (!this.primordial_handlers.contains(handler))
/*      */       {
/*  285 */         Debug.out("Primordial handler not found!");
/*      */         
/*  287 */         return;
/*      */       }
/*      */       
/*  290 */       this.primordial_handlers.remove(handler);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRequestHandler(PRUDPRequestHandler _request_handler)
/*      */   {
/*  300 */     if (this.request_handler != null)
/*      */     {
/*  302 */       if (_request_handler != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  308 */         throw new RuntimeException("Multiple handlers per endpoint not supported");
/*      */       }
/*      */     }
/*      */     
/*  312 */     this.request_handler = _request_handler;
/*      */     
/*  314 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/*  316 */     if (delegate != null)
/*      */     {
/*  318 */       delegate.setRequestHandler(_request_handler);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public PRUDPRequestHandler getRequestHandler()
/*      */   {
/*  325 */     return this.request_handler;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  331 */     if ((this.port == 0) && (this.socket != null))
/*      */     {
/*  333 */       return this.socket.getLocalPort();
/*      */     }
/*      */     
/*  336 */     return this.port;
/*      */   }
/*      */   
/*      */ 
/*      */   public InetAddress getBindIP()
/*      */   {
/*  342 */     return this.current_bind_ip;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setDefaultBindAddress(InetAddress address)
/*      */   {
/*      */     try
/*      */     {
/*  350 */       this.bind_address_mon.enter();
/*      */       
/*  352 */       this.default_bind_ip = address;
/*      */       
/*  354 */       calcBind();
/*      */     }
/*      */     finally
/*      */     {
/*  358 */       this.bind_address_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void setExplicitBindAddress(InetAddress address)
/*      */   {
/*      */     try
/*      */     {
/*  367 */       this.bind_address_mon.enter();
/*      */       
/*  369 */       this.explicit_bind_ip = address;
/*      */       
/*  371 */       calcBind();
/*      */     }
/*      */     finally
/*      */     {
/*  375 */       this.bind_address_mon.exit();
/*      */     }
/*      */     
/*  378 */     int loops = 0;
/*      */     for (;;) {
/*  380 */       if ((this.current_bind_ip != this.target_bind_ip) && (!this.failed) && (!this.destroyed))
/*      */       {
/*  382 */         if (loops >= 100)
/*      */         {
/*  384 */           Debug.out("Giving up on wait for bind ip change to take effect");
/*      */         }
/*      */         else
/*      */         {
/*      */           try
/*      */           {
/*  390 */             Thread.sleep(50L);
/*      */             
/*  392 */             loops++;
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void calcBind()
/*      */   {
/*  404 */     if (this.explicit_bind_ip != null)
/*      */     {
/*  406 */       if (this.altProtocolDelegate != null)
/*      */       {
/*  408 */         this.altProtocolDelegate.destroy();
/*  409 */         this.altProtocolDelegate = null;
/*      */       }
/*      */       
/*  412 */       this.target_bind_ip = this.explicit_bind_ip;
/*      */     }
/*      */     else
/*      */     {
/*  416 */       InetAddress altAddress = null;
/*  417 */       NetworkAdmin adm = NetworkAdmin.getSingleton();
/*      */       try
/*      */       {
/*  420 */         if (((this.default_bind_ip instanceof java.net.Inet6Address)) && (!this.default_bind_ip.isAnyLocalAddress()) && (adm.hasIPV4Potential())) {
/*  421 */           altAddress = adm.getSingleHomedServiceBindAddress(1);
/*  422 */         } else if (((this.default_bind_ip instanceof java.net.Inet4Address)) && (adm.hasIPV6Potential())) {
/*  423 */           altAddress = adm.getSingleHomedServiceBindAddress(2);
/*      */         }
/*      */       }
/*      */       catch (UnsupportedAddressTypeException e) {}
/*      */       
/*  428 */       if ((this.altProtocolDelegate != null) && (!this.altProtocolDelegate.explicit_bind_ip.equals(altAddress)))
/*      */       {
/*  430 */         this.altProtocolDelegate.destroy();
/*  431 */         this.altProtocolDelegate = null;
/*      */       }
/*      */       
/*  434 */       if ((altAddress != null) && (this.altProtocolDelegate == null))
/*      */       {
/*  436 */         this.altProtocolDelegate = new PRUDPPacketHandlerImpl(this.port, altAddress, this.packet_transformer);
/*  437 */         this.altProtocolDelegate.stats = this.stats;
/*  438 */         this.altProtocolDelegate.primordial_handlers = this.primordial_handlers;
/*  439 */         this.altProtocolDelegate.request_handler = this.request_handler;
/*      */       }
/*      */       
/*      */ 
/*  443 */       this.target_bind_ip = this.default_bind_ip;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void receiveLoop(AESemaphore init_sem)
/*      */   {
/*  451 */     long last_socket_close_time = 0L;
/*      */     
/*  453 */     NetworkAdminPropertyChangeListener prop_listener = new NetworkAdminPropertyChangeListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void propertyChanged(String property)
/*      */       {
/*      */ 
/*  460 */         if (property == "Default Bind IP")
/*      */         {
/*  462 */           PRUDPPacketHandlerImpl.this.setDefaultBindAddress(NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress());
/*      */         }
/*      */         
/*      */       }
/*  466 */     };
/*  467 */     NetworkAdmin.getSingleton().addPropertyChangeListener(prop_listener);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  472 */       while ((!this.failed) && (!this.destroyed))
/*      */       {
/*  474 */         if (this.socket != null) {
/*      */           try
/*      */           {
/*  477 */             this.socket.close();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  481 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/*  485 */         InetSocketAddress address = null;
/*  486 */         DatagramSocket new_socket = null;
/*      */         try
/*      */         {
/*  489 */           if (this.target_bind_ip == null)
/*      */           {
/*  491 */             address = new InetSocketAddress("127.0.0.1", this.port);
/*      */             
/*  493 */             new_socket = new DatagramSocket(this.port);
/*      */           }
/*      */           else
/*      */           {
/*  497 */             address = new InetSocketAddress(this.target_bind_ip, this.port);
/*      */             
/*  499 */             new_socket = new DatagramSocket(address);
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (BindException e)
/*      */         {
/*  506 */           boolean rebind_worked = false;
/*      */           
/*  508 */           int delay = 25;
/*      */           
/*  510 */           for (int i = 0; (i < 16) && (!this.failed) && (!this.destroyed); i++) {
/*      */             try
/*      */             {
/*  513 */               Thread.sleep(delay);
/*      */               
/*  515 */               delay *= 2;
/*      */               
/*  517 */               if (delay > 1000)
/*      */               {
/*  519 */                 delay = 1000;
/*      */               }
/*      */               
/*  522 */               if (this.target_bind_ip == null)
/*      */               {
/*  524 */                 address = new InetSocketAddress("127.0.0.1", this.port);
/*      */                 
/*  526 */                 new_socket = new DatagramSocket(this.port);
/*      */               }
/*      */               else
/*      */               {
/*  530 */                 address = new InetSocketAddress(this.target_bind_ip, this.port);
/*      */                 
/*  532 */                 new_socket = new DatagramSocket(address);
/*      */               }
/*      */               
/*  535 */               if (Logger.isEnabled()) {
/*  536 */                 Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: rebind to " + this.target_bind_ip + " worked (tries=" + (i + 1) + ") after getting " + Debug.getNestedExceptionMessage(e)));
/*      */               }
/*  538 */               rebind_worked = true;
/*      */             }
/*      */             catch (Throwable f) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  547 */           if (!rebind_worked)
/*      */           {
/*  549 */             if (Logger.isEnabled()) {
/*  550 */               Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: bind failed with " + Debug.getNestedExceptionMessage(e)));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  557 */             if (this.target_bind_ip.isAnyLocalAddress())
/*      */             {
/*  559 */               InetAddress guess = NetworkAdmin.getSingleton().guessRoutableBindAddress();
/*      */               
/*  561 */               if (guess != null)
/*      */               {
/*  563 */                 if (Logger.isEnabled()) {
/*  564 */                   Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: retrying with bind IP guess of " + guess));
/*      */                 }
/*      */                 try
/*      */                 {
/*  568 */                   InetSocketAddress guess_address = new InetSocketAddress(guess, this.port);
/*      */                   
/*  570 */                   new_socket = new DatagramSocket(guess_address);
/*      */                   
/*  572 */                   this.target_bind_ip = guess;
/*  573 */                   address = guess_address;
/*      */                   
/*  575 */                   if (Logger.isEnabled()) {
/*  576 */                     Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: Switched to explicit bind ip " + this.target_bind_ip + " after initial bind failure with wildcard (" + e.getMessage() + ")"));
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable f) {
/*  580 */                   throw e;
/*      */                 }
/*      */               }
/*      */               else {
/*  584 */                 throw e;
/*      */               }
/*      */             }
/*      */             else {
/*  588 */               throw e;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  593 */         new_socket.setReuseAddress(true);
/*      */         
/*      */ 
/*      */ 
/*  597 */         new_socket.setSoTimeout(1000);
/*      */         
/*      */ 
/*      */ 
/*  601 */         this.socket = new_socket;
/*      */         
/*  603 */         this.current_bind_ip = this.target_bind_ip;
/*      */         
/*  605 */         init_sem.release();
/*      */         
/*  607 */         if (Logger.isEnabled()) {
/*  608 */           Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: receiver established on port " + this.port + (this.current_bind_ip == null ? "" : new StringBuilder().append(", bound to ").append(this.current_bind_ip).toString())));
/*      */         }
/*      */         
/*  611 */         byte[] buffer = null;
/*      */         
/*  613 */         long successful_accepts = 0L;
/*  614 */         long failed_accepts = 0L;
/*      */         
/*  616 */         while ((!this.failed) && (!this.destroyed))
/*      */         {
/*  618 */           if (this.current_bind_ip != this.target_bind_ip) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*      */           try
/*      */           {
/*  625 */             if (buffer == null)
/*      */             {
/*  627 */               buffer = new byte[MAX_PACKET_SIZE];
/*      */             }
/*      */             
/*  630 */             DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
/*      */             
/*  632 */             receiveFromSocket(packet);
/*      */             
/*  634 */             if (packet.getLength() > MAX_PACKET_SIZE)
/*      */             {
/*  636 */               if (MAX_PACKET_SIZE < 8192)
/*      */               {
/*  638 */                 Debug.out("UDP Packet truncated: received length=" + packet.getLength() + ", current max=" + MAX_PACKET_SIZE);
/*      */                 
/*  640 */                 MAX_PACKET_SIZE = Math.min(packet.getLength() + 256, 8192);
/*      */                 
/*  642 */                 buffer = null;
/*      */                 
/*  644 */                 continue;
/*      */               }
/*      */             }
/*      */             
/*  648 */             long receive_time = SystemTime.getCurrentTime();
/*      */             
/*  650 */             successful_accepts += 1L;
/*      */             
/*  652 */             failed_accepts = 0L;
/*      */             
/*  654 */             for (PRUDPPrimordialHandler prim_hand : this.primordial_handlers)
/*      */             {
/*  656 */               if (prim_hand.packetReceived(packet))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  661 */                 buffer = null;
/*      */                 
/*  663 */                 this.stats.primordialPacketReceived(packet.getLength());
/*      */                 
/*  665 */                 break;
/*      */               }
/*      */             }
/*      */             
/*  669 */             if (buffer != null)
/*      */             {
/*  671 */               process(packet, receive_time);
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           catch (SocketTimeoutException e) {}catch (Throwable e)
/*      */           {
/*      */ 
/*  680 */             String message = e.getMessage();
/*      */             
/*  682 */             if ((this.socket.isClosed()) || ((message != null) && (message.toLowerCase().contains("socket closed"))))
/*      */             {
/*      */ 
/*      */ 
/*  686 */               long now = SystemTime.getCurrentTime();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  691 */               if (now - last_socket_close_time < 500L)
/*      */               {
/*  693 */                 Thread.sleep(250L);
/*      */               }
/*      */               
/*  696 */               last_socket_close_time = now;
/*      */               
/*  698 */               if (Logger.isEnabled()) {
/*  699 */                 Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: recycled UDP port " + this.port + " after close: ok=" + successful_accepts));
/*      */               }
/*      */               
/*  702 */               break;
/*      */             }
/*      */             
/*  705 */             failed_accepts += 1L;
/*      */             
/*  707 */             if (Logger.isEnabled()) {
/*  708 */               Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: receive failed on port " + this.port + ": ok=" + successful_accepts + ", fails=" + failed_accepts, e));
/*      */             }
/*      */             
/*      */ 
/*  712 */             if (((failed_accepts > 100L) && (successful_accepts == 0L)) || (failed_accepts > 1000L))
/*      */             {
/*  714 */               Logger.logTextResource(new LogAlert(false, 3, "Network.alert.acceptfail"), new String[] { "" + this.port, "UDP" });
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
/*  729 */               this.init_error = e;
/*      */               
/*  731 */               this.failed = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/*      */       PRUDPPacketHandlerImpl delegate;
/*  738 */       this.init_error = e;
/*      */       
/*  740 */       if ((!(e instanceof BindException)) || (!Constants.isWindowsVistaOrHigher))
/*      */       {
/*  742 */         Logger.logTextResource(new LogAlert(false, 3, "Tracker.alert.listenfail"), new String[] { "UDP:" + this.port });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  747 */       Logger.log(new LogEvent(LOGID, "PRUDPPacketReceiver: DatagramSocket bind failed on port " + this.port, e));
/*      */     }
/*      */     finally
/*      */     {
/*      */       PRUDPPacketHandlerImpl delegate;
/*  752 */       init_sem.release();
/*      */       
/*  754 */       this.destroy_sem.releaseForever();
/*      */       
/*  756 */       if (this.socket != null) {
/*      */         try
/*      */         {
/*  759 */           this.socket.close();
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  763 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  768 */       PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */       
/*  770 */       if (delegate != null)
/*      */       {
/*  772 */         delegate.destroy();
/*      */       }
/*      */       
/*  775 */       NetworkAdmin.getSingleton().removePropertyChangeListener(prop_listener);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkTimeouts()
/*      */   {
/*  782 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  784 */     List timed_out = new ArrayList();
/*      */     try
/*      */     {
/*  787 */       this.requests_mon.enter();
/*      */       
/*  789 */       Iterator it = this.requests.values().iterator();
/*      */       
/*  791 */       while (it.hasNext())
/*      */       {
/*  793 */         PRUDPPacketHandlerRequestImpl request = (PRUDPPacketHandlerRequestImpl)it.next();
/*      */         
/*  795 */         long sent_time = request.getSendTime();
/*      */         
/*  797 */         if ((sent_time != 0L) && (now - sent_time >= request.getTimeout()))
/*      */         {
/*      */ 
/*  800 */           it.remove();
/*      */           
/*  802 */           this.stats.requestTimedOut();
/*      */           
/*  804 */           timed_out.add(request);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  809 */       this.requests_mon.exit();
/*      */     }
/*      */     
/*  812 */     for (int i = 0; i < timed_out.size(); i++)
/*      */     {
/*  814 */       PRUDPPacketHandlerRequestImpl request = (PRUDPPacketHandlerRequestImpl)timed_out.get(i);
/*      */       
/*  816 */       if ((this.TRACE_REQUESTS) && 
/*  817 */         (Logger.isEnabled())) {
/*  818 */         Logger.log(new LogEvent(LOGID, 3, "PRUDPPacketHandler: request timeout"));
/*      */       }
/*      */       
/*      */ 
/*      */       try
/*      */       {
/*  824 */         request.setException(new PRUDPPacketHandlerException("timed out"));
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  828 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
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
/*      */   protected void process(DatagramPacket dg_packet, long receive_time)
/*      */   {
/*      */     try
/*      */     {
/*  845 */       byte[] packet_data = dg_packet.getData();
/*  846 */       int packet_len = dg_packet.getLength();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  854 */       this.stats.packetReceived(packet_len);
/*      */       
/*  856 */       InetSocketAddress originator = (InetSocketAddress)dg_packet.getSocketAddress();
/*      */       PRUDPPacket packet;
/*  858 */       boolean request_packet; PRUDPPacket packet; if ((packet_data[0] & 0x80) == 0)
/*      */       {
/*  860 */         boolean request_packet = false;
/*      */         
/*  862 */         packet = com.aelitis.net.udp.uc.PRUDPPacketReply.deserialiseReply(this, originator, new DataInputStream(new ByteArrayInputStream(packet_data, 0, packet_len)));
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  868 */         request_packet = true;
/*      */         
/*  870 */         PRUDPPacketRequest request = PRUDPPacketRequest.deserialiseRequest(this, new DataInputStream(new ByteArrayInputStream(packet_data, 0, packet_len)));
/*      */         
/*      */ 
/*      */ 
/*  874 */         request.setReceiveTime(receive_time);
/*      */         
/*  876 */         packet = request;
/*      */       }
/*      */       
/*  879 */       packet.setSerialisedSize(packet_len);
/*      */       
/*  881 */       packet.setAddress(originator);
/*      */       
/*  883 */       if (request_packet)
/*      */       {
/*  885 */         this.total_requests_received += 1L;
/*      */         
/*      */ 
/*      */ 
/*  889 */         if (this.TRACE_REQUESTS) {
/*  890 */           Logger.log(new LogEvent(LOGID, "PRUDPPacketHandler: request packet received: " + packet.getString()));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  895 */         if (this.receive_delay > 0)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*  901 */             this.recv_queue_mon.enter();
/*      */             
/*  903 */             if (this.recv_queue_data_size > 1048576L)
/*      */             {
/*  905 */               long now = SystemTime.getCurrentTime();
/*      */               
/*  907 */               if (now - this.last_error_report > 30000L)
/*      */               {
/*  909 */                 this.last_error_report = now;
/*      */                 
/*  911 */                 Debug.out("Receive queue size limit exceeded (1048576), dropping request packet [" + this.total_requests_received + "/" + this.total_requests_processed + ":" + this.total_replies + "]");
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*  916 */             else if (this.receive_delay * this.recv_queue.size() > this.queued_request_timeout)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*  921 */               long now = SystemTime.getCurrentTime();
/*      */               
/*  923 */               if (now - this.last_error_report > 30000L)
/*      */               {
/*  925 */                 this.last_error_report = now;
/*      */                 
/*  927 */                 Debug.out("Receive queue entry limit exceeded (" + this.recv_queue.size() + "), dropping request packet [" + this.total_requests_received + "/" + this.total_requests_processed + ":" + this.total_replies + "]");
/*      */               }
/*      */               
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*  934 */               this.recv_queue.add(new Object[] { packet, new Integer(dg_packet.getLength()) });
/*      */               
/*  936 */               this.recv_queue_data_size += dg_packet.getLength();
/*      */               
/*  938 */               this.recv_queue_sem.release();
/*      */               
/*  940 */               if (this.recv_thread == null)
/*      */               {
/*  942 */                 this.recv_thread = new AEThread("PRUDPPacketHandler:receiver")
/*      */                 {
/*      */ 
/*      */                   public void runSupport()
/*      */                   {
/*      */                     try
/*      */                     {
/*      */                       for (;;)
/*      */                       {
/*  951 */                         PRUDPPacketHandlerImpl.this.recv_queue_sem.reserve();
/*      */                         
/*      */                         Object[] data;
/*      */                         try
/*      */                         {
/*  956 */                           PRUDPPacketHandlerImpl.this.recv_queue_mon.enter();
/*      */                           
/*  958 */                           data = (Object[])PRUDPPacketHandlerImpl.this.recv_queue.remove(0);
/*      */                           
/*  960 */                           PRUDPPacketHandlerImpl.access$608(PRUDPPacketHandlerImpl.this);
/*      */                           
/*  962 */                           PRUDPPacketHandlerImpl.access$722(PRUDPPacketHandlerImpl.this, ((Integer)data[1]).intValue());
/*      */                           
/*  964 */                           PRUDPPacketHandlerImpl.this.request_receive_average.addValue(1L);
/*      */                         }
/*      */                         finally
/*      */                         {
/*  968 */                           PRUDPPacketHandlerImpl.this.recv_queue_mon.exit();
/*      */                         }
/*      */                         
/*  971 */                         PRUDPPacketRequest p = (PRUDPPacketRequest)data[0];
/*      */                         
/*  973 */                         PRUDPRequestHandler handler = PRUDPPacketHandlerImpl.this.request_handler;
/*      */                         
/*  975 */                         if (handler != null)
/*      */                         {
/*  977 */                           handler.process(p);
/*      */                           
/*  979 */                           if (PRUDPPacketHandlerImpl.this.receive_delay > 0)
/*      */                           {
/*  981 */                             int max_req_per_sec = 1000 / PRUDPPacketHandlerImpl.this.receive_delay;
/*      */                             
/*  983 */                             long request_per_sec = PRUDPPacketHandlerImpl.this.request_receive_average.getAverage();
/*      */                             
/*      */ 
/*      */ 
/*  987 */                             if (request_per_sec > max_req_per_sec)
/*      */                             {
/*  989 */                               Thread.sleep(PRUDPPacketHandlerImpl.this.receive_delay);
/*      */                             }
/*      */                             else
/*      */                             {
/*  993 */                               long delay = PRUDPPacketHandlerImpl.this.receive_delay * request_per_sec / max_req_per_sec;
/*      */                               
/*  995 */                               if (delay >= 5L)
/*      */                               {
/*  997 */                                 Thread.sleep(delay);
/*      */                               }
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 1006 */                       Debug.printStackTrace(e);
/*      */                     }
/*      */                     
/*      */                   }
/*      */                   
/* 1011 */                 };
/* 1012 */                 this.recv_thread.setDaemon(true);
/*      */                 
/* 1014 */                 this.recv_thread.start();
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 1019 */             this.recv_queue_mon.exit();
/*      */           }
/*      */         }
/*      */         else {
/* 1023 */           PRUDPRequestHandler handler = this.request_handler;
/*      */           
/* 1025 */           if (handler != null)
/*      */           {
/* 1027 */             handler.process((PRUDPPacketRequest)packet);
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1033 */         this.total_replies += 1L;
/*      */         
/* 1035 */         if (this.TRACE_REQUESTS) {
/* 1036 */           Logger.log(new LogEvent(LOGID, "PRUDPPacketHandler: reply packet received: " + packet.getString()));
/*      */         }
/*      */         
/*      */ 
/*      */         PRUDPPacketHandlerRequestImpl request;
/*      */         
/*      */         try
/*      */         {
/* 1044 */           this.requests_mon.enter();
/*      */           PRUDPPacketHandlerRequestImpl request;
/* 1046 */           if (packet.hasContinuation())
/*      */           {
/*      */ 
/*      */ 
/* 1050 */             request = (PRUDPPacketHandlerRequestImpl)this.requests.get(new Integer(packet.getTransactionId()));
/*      */           }
/*      */           else
/*      */           {
/* 1054 */             request = (PRUDPPacketHandlerRequestImpl)this.requests.remove(new Integer(packet.getTransactionId()));
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 1059 */           this.requests_mon.exit();
/*      */         }
/*      */         
/* 1062 */         if (request == null)
/*      */         {
/* 1064 */           if (this.TRACE_REQUESTS) {
/* 1065 */             Logger.log(new LogEvent(LOGID, 3, "PRUDPPacketReceiver: unmatched reply received, discarding:" + packet.getString()));
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1072 */           request.setReply(packet, (InetSocketAddress)dg_packet.getSocketAddress(), receive_time);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1079 */       if (!(e instanceof IOException))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1085 */         Logger.log(new LogEvent(LOGID, "", e));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PRUDPPacket sendAndReceive(PRUDPPacket request_packet, InetSocketAddress destination_address)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1097 */     return sendAndReceive(null, request_packet, destination_address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PRUDPPacket sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1108 */     return sendAndReceive(auth, request_packet, destination_address, 30000L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PRUDPPacket sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address, long timeout)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1120 */     PRUDPPacketHandlerRequestImpl request = sendAndReceive(auth, request_packet, destination_address, null, timeout, 1);
/*      */     
/*      */ 
/* 1123 */     return request.getReply();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PRUDPPacket sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address, long timeout, int priority)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1136 */     PRUDPPacketHandlerRequestImpl request = sendAndReceive(auth, request_packet, destination_address, null, timeout, priority);
/*      */     
/*      */ 
/* 1139 */     return request.getReply();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendAndReceive(PRUDPPacket request_packet, InetSocketAddress destination_address, PRUDPPacketReceiver receiver, long timeout, int priority)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1152 */     sendAndReceive(null, request_packet, destination_address, receiver, timeout, priority);
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
/*      */   public void send(PRUDPPacket request_packet, InetSocketAddress destination_address)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1492 */     if ((this.socket == null) || (this.socket.isClosed()))
/*      */     {
/* 1494 */       if (this.init_error != null)
/*      */       {
/* 1496 */         throw new PRUDPPacketHandlerException("Transport unavailable", this.init_error);
/*      */       }
/*      */       
/* 1499 */       throw new PRUDPPacketHandlerException("Transport unavailable");
/*      */     }
/*      */     
/* 1502 */     checkTargetAddress(destination_address);
/*      */     
/* 1504 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/* 1506 */     if ((delegate != null) && (destination_address.getAddress().getClass().isInstance(delegate.explicit_bind_ip)))
/*      */     {
/* 1508 */       delegate.send(request_packet, destination_address);
/*      */       
/* 1510 */       return;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1515 */       MyByteArrayOutputStream baos = new MyByteArrayOutputStream(MAX_PACKET_SIZE, null);
/*      */       
/* 1517 */       DataOutputStream os = new DataOutputStream(baos);
/*      */       
/* 1519 */       request_packet.serialise(os);
/*      */       
/* 1521 */       byte[] _buffer = baos.getBuffer();
/* 1522 */       int _length = baos.size();
/*      */       
/* 1524 */       request_packet.setSerialisedSize(_length);
/*      */       
/* 1526 */       DatagramPacket dg_packet = new DatagramPacket(_buffer, _length, destination_address);
/*      */       
/*      */ 
/*      */ 
/* 1530 */       if (this.TRACE_REQUESTS) {
/* 1531 */         Logger.log(new LogEvent(LOGID, "PRUDPPacketHandler: reply packet sent: " + request_packet.getString()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1536 */       sendToSocket(dg_packet);
/*      */       
/* 1538 */       this.stats.packetSent(_length);
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1544 */       if (!(e instanceof java.net.NoRouteToHostException))
/*      */       {
/*      */ 
/*      */ 
/* 1548 */         e.printStackTrace();
/*      */       }
/*      */       
/* 1551 */       Logger.log(new LogEvent(LOGID, 3, "PRUDPPacketHandler: send to " + destination_address + " failed: " + Debug.getNestedExceptionMessage(e)));
/*      */       
/* 1553 */       throw new PRUDPPacketHandlerException("PRUDPPacketHandler:send failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkTargetAddress(InetSocketAddress address)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1563 */     if (address.getPort() == 0)
/*      */     {
/* 1565 */       throw new PRUDPPacketHandlerException("Invalid port - 0");
/*      */     }
/*      */     
/* 1568 */     if (address.getAddress() == null)
/*      */     {
/* 1570 */       throw new PRUDPPacketHandlerException("Unresolved host '" + address.getHostName() + "'");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDelays(int _send_delay, int _receive_delay, int _queued_request_timeout)
/*      */   {
/* 1580 */     this.send_delay = _send_delay;
/* 1581 */     this.receive_delay = _receive_delay;
/*      */     
/*      */ 
/*      */ 
/* 1585 */     this.queued_request_timeout = (_queued_request_timeout - 5000);
/*      */     
/* 1587 */     if (this.queued_request_timeout < 5000)
/*      */     {
/* 1589 */       this.queued_request_timeout = 5000;
/*      */     }
/*      */     
/* 1592 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/* 1594 */     if (delegate != null)
/*      */     {
/* 1596 */       delegate.setDelays(_send_delay, _receive_delay, _queued_request_timeout);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSendQueueLength()
/*      */   {
/* 1603 */     int res = 0;
/* 1604 */     for (int i = 0; i < this.send_queues.length; i++) {
/* 1605 */       res += this.send_queues[i].size();
/*      */     }
/*      */     
/* 1608 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/* 1610 */     if (delegate != null)
/*      */     {
/* 1612 */       res = (int)(res + delegate.getSendQueueLength());
/*      */     }
/*      */     
/* 1615 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getReceiveQueueLength()
/*      */   {
/* 1621 */     long size = this.recv_queue.size();
/*      */     
/* 1623 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/* 1625 */     if (delegate != null)
/*      */     {
/* 1627 */       size += delegate.getReceiveQueueLength();
/*      */     }
/*      */     
/* 1630 */     return size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void primordialSend(byte[] buffer, InetSocketAddress target)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1640 */     if ((this.socket == null) || (this.socket.isClosed()))
/*      */     {
/* 1642 */       if (this.init_error != null)
/*      */       {
/* 1644 */         throw new PRUDPPacketHandlerException("Transport unavailable", this.init_error);
/*      */       }
/*      */       
/* 1647 */       throw new PRUDPPacketHandlerException("Transport unavailable");
/*      */     }
/*      */     
/* 1650 */     checkTargetAddress(target);
/*      */     
/* 1652 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/* 1654 */     if ((delegate != null) && (target.getAddress().getClass().isInstance(delegate.explicit_bind_ip)))
/*      */     {
/*      */ 
/* 1657 */       delegate.primordialSend(buffer, target);
/*      */       
/* 1659 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1663 */       DatagramPacket dg_packet = new DatagramPacket(buffer, buffer.length, target);
/*      */       
/*      */ 
/*      */ 
/* 1667 */       if (this.TRACE_REQUESTS) {
/* 1668 */         Logger.log(new LogEvent(LOGID, "PRUDPPacketHandler: reply packet sent: " + buffer.length + " to " + target));
/*      */       }
/*      */       
/*      */ 
/* 1672 */       sendToSocket(dg_packet);
/*      */       
/* 1674 */       this.stats.primordialPacketSent(buffer.length);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1678 */       throw new PRUDPPacketHandlerException(e.getMessage());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void sendToSocket(DatagramPacket p)
/*      */     throws IOException
/*      */   {
/* 1688 */     if (this.packet_transformer != null)
/*      */     {
/* 1690 */       this.packet_transformer.transformSend(p);
/*      */     }
/*      */     
/* 1693 */     this.socket.send(p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void receiveFromSocket(DatagramPacket p)
/*      */     throws IOException
/*      */   {
/* 1702 */     this.socket.receive(p);
/*      */     
/* 1704 */     if (this.packet_transformer != null)
/*      */     {
/* 1706 */       this.packet_transformer.transformReceive(p);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.net.udp.uc.PRUDPPacketHandlerStats getStats()
/*      */   {
/* 1713 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 1719 */     this.destroyed = true;
/*      */     
/* 1721 */     PRUDPPacketHandlerImpl delegate = this.altProtocolDelegate;
/*      */     
/* 1723 */     if (delegate != null)
/*      */     {
/* 1725 */       delegate.destroy();
/*      */     }
/*      */     
/* 1728 */     this.destroy_sem.reserve();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PRUDPPacketHandler openSession(InetSocketAddress target)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/* 1738 */     if (use_socks)
/*      */     {
/* 1740 */       return new PRUDPPacketHandlerSocks(target);
/*      */     }
/*      */     
/*      */ 
/* 1744 */     return this;
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public PRUDPPacketHandlerRequestImpl sendAndReceive(PasswordAuthentication auth, PRUDPPacket request_packet, InetSocketAddress destination_address, PRUDPPacketReceiver receiver, long timeout, int priority)
/*      */     throws PRUDPPacketHandlerException
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 945	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:socket	Ljava/net/DatagramSocket;
/*      */     //   4: ifnonnull +36 -> 40
/*      */     //   7: aload_0
/*      */     //   8: getfield 944	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:init_error	Ljava/lang/Throwable;
/*      */     //   11: ifnull +18 -> 29
/*      */     //   14: new 531	com/aelitis/net/udp/uc/PRUDPPacketHandlerException
/*      */     //   17: dup
/*      */     //   18: ldc_w 518
/*      */     //   21: aload_0
/*      */     //   22: getfield 944	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:init_error	Ljava/lang/Throwable;
/*      */     //   25: invokespecial 990	com/aelitis/net/udp/uc/PRUDPPacketHandlerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   28: athrow
/*      */     //   29: new 531	com/aelitis/net/udp/uc/PRUDPPacketHandlerException
/*      */     //   32: dup
/*      */     //   33: ldc_w 518
/*      */     //   36: invokespecial 989	com/aelitis/net/udp/uc/PRUDPPacketHandlerException:<init>	(Ljava/lang/String;)V
/*      */     //   39: athrow
/*      */     //   40: aload_0
/*      */     //   41: aload_3
/*      */     //   42: invokevirtual 1003	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:checkTargetAddress	(Ljava/net/InetSocketAddress;)V
/*      */     //   45: aload_0
/*      */     //   46: getfield 941	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:altProtocolDelegate	Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl;
/*      */     //   49: astore 8
/*      */     //   51: aload 8
/*      */     //   53: ifnull +36 -> 89
/*      */     //   56: aload_3
/*      */     //   57: invokevirtual 1083	java/net/InetSocketAddress:getAddress	()Ljava/net/InetAddress;
/*      */     //   60: invokevirtual 1046	java/lang/Object:getClass	()Ljava/lang/Class;
/*      */     //   63: aload 8
/*      */     //   65: getfield 948	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:explicit_bind_ip	Ljava/net/InetAddress;
/*      */     //   68: invokevirtual 1041	java/lang/Class:isInstance	(Ljava/lang/Object;)Z
/*      */     //   71: ifeq +18 -> 89
/*      */     //   74: aload 8
/*      */     //   76: aload_1
/*      */     //   77: aload_2
/*      */     //   78: aload_3
/*      */     //   79: aload 4
/*      */     //   81: lload 5
/*      */     //   83: iload 7
/*      */     //   85: invokevirtual 1009	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:sendAndReceive	(Ljava/net/PasswordAuthentication;Lcom/aelitis/net/udp/uc/PRUDPPacket;Ljava/net/InetSocketAddress;Lcom/aelitis/net/udp/uc/PRUDPPacketReceiver;JI)Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerRequestImpl;
/*      */     //   88: areturn
/*      */     //   89: new 545	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream
/*      */     //   92: dup
/*      */     //   93: getstatic 924	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:MAX_PACKET_SIZE	I
/*      */     //   96: aconst_null
/*      */     //   97: invokespecial 1021	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:<init>	(ILcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$1;)V
/*      */     //   100: astore 9
/*      */     //   102: new 552	java/io/DataOutputStream
/*      */     //   105: dup
/*      */     //   106: aload 9
/*      */     //   108: invokespecial 1039	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
/*      */     //   111: astore 10
/*      */     //   113: aload_2
/*      */     //   114: aload 10
/*      */     //   116: invokevirtual 986	com/aelitis/net/udp/uc/PRUDPPacket:serialise	(Ljava/io/DataOutputStream;)V
/*      */     //   119: aload 9
/*      */     //   121: invokestatic 1022	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:access$1200	(Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream;)[B
/*      */     //   124: astore 11
/*      */     //   126: aload 9
/*      */     //   128: invokevirtual 1018	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:size	()I
/*      */     //   131: istore 12
/*      */     //   133: aload_2
/*      */     //   134: iload 12
/*      */     //   136: invokevirtual 985	com/aelitis/net/udp/uc/PRUDPPacket:setSerialisedSize	(I)V
/*      */     //   139: aload_1
/*      */     //   140: ifnull +187 -> 327
/*      */     //   143: new 597	org/gudy/azureus2/core3/util/SHA1Hasher
/*      */     //   146: dup
/*      */     //   147: invokespecial 1117	org/gudy/azureus2/core3/util/SHA1Hasher:<init>	()V
/*      */     //   150: astore 13
/*      */     //   152: aload_1
/*      */     //   153: invokevirtual 1086	java/net/PasswordAuthentication:getUserName	()Ljava/lang/String;
/*      */     //   156: astore 14
/*      */     //   158: new 561	java/lang/String
/*      */     //   161: dup
/*      */     //   162: aload_1
/*      */     //   163: invokevirtual 1085	java/net/PasswordAuthentication:getPassword	()[C
/*      */     //   166: invokespecial 1051	java/lang/String:<init>	([C)V
/*      */     //   169: astore 15
/*      */     //   171: aload 14
/*      */     //   173: ldc_w 504
/*      */     //   176: invokevirtual 1053	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   179: ifeq +13 -> 192
/*      */     //   182: aload 15
/*      */     //   184: invokestatic 1124	org/gudy/bouncycastle/util/encoders/Base64:decode	(Ljava/lang/String;)[B
/*      */     //   187: astore 16
/*      */     //   189: goto +15 -> 204
/*      */     //   192: aload 13
/*      */     //   194: aload 15
/*      */     //   196: invokevirtual 1049	java/lang/String:getBytes	()[B
/*      */     //   199: invokevirtual 1120	org/gudy/azureus2/core3/util/SHA1Hasher:calculateHash	([B)[B
/*      */     //   202: astore 16
/*      */     //   204: bipush 8
/*      */     //   206: newarray <illegal type>
/*      */     //   208: astore 17
/*      */     //   210: aload 17
/*      */     //   212: iconst_0
/*      */     //   213: invokestatic 1088	java/util/Arrays:fill	([BB)V
/*      */     //   216: iconst_0
/*      */     //   217: istore 18
/*      */     //   219: iload 18
/*      */     //   221: aload 17
/*      */     //   223: arraylength
/*      */     //   224: if_icmpge +32 -> 256
/*      */     //   227: iload 18
/*      */     //   229: aload 14
/*      */     //   231: invokevirtual 1048	java/lang/String:length	()I
/*      */     //   234: if_icmpge +22 -> 256
/*      */     //   237: aload 17
/*      */     //   239: iload 18
/*      */     //   241: aload 14
/*      */     //   243: iload 18
/*      */     //   245: invokevirtual 1050	java/lang/String:charAt	(I)C
/*      */     //   248: i2b
/*      */     //   249: bastore
/*      */     //   250: iinc 18 1
/*      */     //   253: goto -34 -> 219
/*      */     //   256: new 597	org/gudy/azureus2/core3/util/SHA1Hasher
/*      */     //   259: dup
/*      */     //   260: invokespecial 1117	org/gudy/azureus2/core3/util/SHA1Hasher:<init>	()V
/*      */     //   263: astore 13
/*      */     //   265: aload 13
/*      */     //   267: aload 11
/*      */     //   269: iconst_0
/*      */     //   270: iload 12
/*      */     //   272: invokevirtual 1121	org/gudy/azureus2/core3/util/SHA1Hasher:update	([BII)V
/*      */     //   275: aload 13
/*      */     //   277: aload 17
/*      */     //   279: invokevirtual 1119	org/gudy/azureus2/core3/util/SHA1Hasher:update	([B)V
/*      */     //   282: aload 13
/*      */     //   284: aload 16
/*      */     //   286: invokevirtual 1119	org/gudy/azureus2/core3/util/SHA1Hasher:update	([B)V
/*      */     //   289: aload 13
/*      */     //   291: invokevirtual 1118	org/gudy/azureus2/core3/util/SHA1Hasher:getDigest	()[B
/*      */     //   294: astore 18
/*      */     //   296: aload 9
/*      */     //   298: aload 17
/*      */     //   300: invokevirtual 1019	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:write	([B)V
/*      */     //   303: aload 9
/*      */     //   305: aload 18
/*      */     //   307: iconst_0
/*      */     //   308: bipush 8
/*      */     //   310: invokevirtual 1020	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:write	([BII)V
/*      */     //   313: aload 9
/*      */     //   315: invokestatic 1022	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:access$1200	(Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream;)[B
/*      */     //   318: astore 11
/*      */     //   320: aload 9
/*      */     //   322: invokevirtual 1018	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$MyByteArrayOutputStream:size	()I
/*      */     //   325: istore 12
/*      */     //   327: new 567	java/net/DatagramPacket
/*      */     //   330: dup
/*      */     //   331: aload 11
/*      */     //   333: iload 12
/*      */     //   335: aload_3
/*      */     //   336: invokespecial 1068	java/net/DatagramPacket:<init>	([BILjava/net/SocketAddress;)V
/*      */     //   339: astore 13
/*      */     //   341: new 547	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerRequestImpl
/*      */     //   344: dup
/*      */     //   345: aload 4
/*      */     //   347: lload 5
/*      */     //   349: invokespecial 1028	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerRequestImpl:<init>	(Lcom/aelitis/net/udp/uc/PRUDPPacketReceiver;J)V
/*      */     //   352: astore 14
/*      */     //   354: aload_0
/*      */     //   355: getfield 956	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   358: invokevirtual 1102	org/gudy/azureus2/core3/util/AEMonitor2:enter	()V
/*      */     //   361: aload_0
/*      */     //   362: getfield 952	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests	Ljava/util/Map;
/*      */     //   365: new 556	java/lang/Integer
/*      */     //   368: dup
/*      */     //   369: aload_2
/*      */     //   370: invokevirtual 983	com/aelitis/net/udp/uc/PRUDPPacket:getTransactionId	()I
/*      */     //   373: invokespecial 1042	java/lang/Integer:<init>	(I)V
/*      */     //   376: aload 14
/*      */     //   378: invokeinterface 1140 3 0
/*      */     //   383: pop
/*      */     //   384: aload_0
/*      */     //   385: getfield 956	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   388: invokevirtual 1103	org/gudy/azureus2/core3/util/AEMonitor2:exit	()V
/*      */     //   391: goto +15 -> 406
/*      */     //   394: astore 19
/*      */     //   396: aload_0
/*      */     //   397: getfield 956	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   400: invokevirtual 1103	org/gudy/azureus2/core3/util/AEMonitor2:exit	()V
/*      */     //   403: aload 19
/*      */     //   405: athrow
/*      */     //   406: aload_0
/*      */     //   407: getfield 928	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_delay	I
/*      */     //   410: ifle +330 -> 740
/*      */     //   413: iload 7
/*      */     //   415: bipush 99
/*      */     //   417: if_icmpeq +323 -> 740
/*      */     //   420: aload_0
/*      */     //   421: getfield 957	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   424: invokevirtual 1102	org/gudy/azureus2/core3/util/AEMonitor2:enter	()V
/*      */     //   427: aload_0
/*      */     //   428: getfield 931	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_data_size	J
/*      */     //   431: ldc2_w 496
/*      */     //   434: lcmp
/*      */     //   435: ifle +89 -> 524
/*      */     //   438: aload 14
/*      */     //   440: invokevirtual 1025	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerRequestImpl:sent	()V
/*      */     //   443: aload_0
/*      */     //   444: aload 13
/*      */     //   446: invokespecial 1001	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:sendToSocket	(Ljava/net/DatagramPacket;)V
/*      */     //   449: aload_0
/*      */     //   450: getfield 943	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:stats	Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerStatsImpl;
/*      */     //   453: iload 12
/*      */     //   455: invokevirtual 1033	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerStatsImpl:packetSent	(I)V
/*      */     //   458: aload_0
/*      */     //   459: getfield 935	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:TRACE_REQUESTS	Z
/*      */     //   462: ifeq +49 -> 511
/*      */     //   465: new 586	org/gudy/azureus2/core3/logging/LogEvent
/*      */     //   468: dup
/*      */     //   469: getstatic 953	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:LOGID	Lorg/gudy/azureus2/core3/logging/LogIDs;
/*      */     //   472: new 562	java/lang/StringBuilder
/*      */     //   475: dup
/*      */     //   476: invokespecial 1055	java/lang/StringBuilder:<init>	()V
/*      */     //   479: ldc_w 511
/*      */     //   482: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   485: aload_3
/*      */     //   486: invokevirtual 1059	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   489: ldc_w 503
/*      */     //   492: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   495: aload_2
/*      */     //   496: invokevirtual 987	com/aelitis/net/udp/uc/PRUDPPacket:getString	()Ljava/lang/String;
/*      */     //   499: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   502: invokevirtual 1056	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   505: invokespecial 1094	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;Ljava/lang/String;)V
/*      */     //   508: invokestatic 1097	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*      */     //   511: aload_0
/*      */     //   512: getfield 928	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_delay	I
/*      */     //   515: iconst_4
/*      */     //   516: imul
/*      */     //   517: i2l
/*      */     //   518: invokestatic 1061	java/lang/Thread:sleep	(J)V
/*      */     //   521: goto +194 -> 715
/*      */     //   524: aload_0
/*      */     //   525: dup
/*      */     //   526: getfield 931	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_data_size	J
/*      */     //   529: aload 13
/*      */     //   531: invokevirtual 1065	java/net/DatagramPacket:getLength	()I
/*      */     //   534: i2l
/*      */     //   535: ladd
/*      */     //   536: putfield 931	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_data_size	J
/*      */     //   539: aload_0
/*      */     //   540: getfield 951	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queues	[Ljava/util/List;
/*      */     //   543: iload 7
/*      */     //   545: aaload
/*      */     //   546: iconst_2
/*      */     //   547: anewarray 559	java/lang/Object
/*      */     //   550: dup
/*      */     //   551: iconst_0
/*      */     //   552: aload 13
/*      */     //   554: aastore
/*      */     //   555: dup
/*      */     //   556: iconst_1
/*      */     //   557: aload 14
/*      */     //   559: aastore
/*      */     //   560: invokeinterface 1136 2 0
/*      */     //   565: pop
/*      */     //   566: aload_0
/*      */     //   567: getfield 935	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:TRACE_REQUESTS	Z
/*      */     //   570: ifeq +101 -> 671
/*      */     //   573: ldc 1
/*      */     //   575: astore 15
/*      */     //   577: iconst_0
/*      */     //   578: istore 16
/*      */     //   580: iload 16
/*      */     //   582: aload_0
/*      */     //   583: getfield 951	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queues	[Ljava/util/List;
/*      */     //   586: arraylength
/*      */     //   587: if_icmpge +57 -> 644
/*      */     //   590: new 562	java/lang/StringBuilder
/*      */     //   593: dup
/*      */     //   594: invokespecial 1055	java/lang/StringBuilder:<init>	()V
/*      */     //   597: aload 15
/*      */     //   599: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   602: iload 16
/*      */     //   604: ifne +8 -> 612
/*      */     //   607: ldc 1
/*      */     //   609: goto +6 -> 615
/*      */     //   612: ldc_w 501
/*      */     //   615: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   618: aload_0
/*      */     //   619: getfield 951	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queues	[Ljava/util/List;
/*      */     //   622: iload 16
/*      */     //   624: aaload
/*      */     //   625: invokeinterface 1134 1 0
/*      */     //   630: invokevirtual 1057	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   633: invokevirtual 1056	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   636: astore 15
/*      */     //   638: iinc 16 1
/*      */     //   641: goto -61 -> 580
/*      */     //   644: getstatic 964	java/lang/System:out	Ljava/io/PrintStream;
/*      */     //   647: new 562	java/lang/StringBuilder
/*      */     //   650: dup
/*      */     //   651: invokespecial 1055	java/lang/StringBuilder:<init>	()V
/*      */     //   654: ldc_w 523
/*      */     //   657: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   660: aload 15
/*      */     //   662: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   665: invokevirtual 1056	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   668: invokevirtual 1040	java/io/PrintStream:println	(Ljava/lang/String;)V
/*      */     //   671: aload_0
/*      */     //   672: getfield 960	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_sem	Lorg/gudy/azureus2/core3/util/AESemaphore;
/*      */     //   675: invokevirtual 1105	org/gudy/azureus2/core3/util/AESemaphore:release	()V
/*      */     //   678: aload_0
/*      */     //   679: getfield 962	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_thread	Lorg/gudy/azureus2/core3/util/AEThread;
/*      */     //   682: ifnonnull +33 -> 715
/*      */     //   685: aload_0
/*      */     //   686: new 544	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$7
/*      */     //   689: dup
/*      */     //   690: aload_0
/*      */     //   691: ldc_w 516
/*      */     //   694: invokespecial 1017	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl$7:<init>	(Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl;Ljava/lang/String;)V
/*      */     //   697: putfield 962	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_thread	Lorg/gudy/azureus2/core3/util/AEThread;
/*      */     //   700: aload_0
/*      */     //   701: getfield 962	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_thread	Lorg/gudy/azureus2/core3/util/AEThread;
/*      */     //   704: iconst_1
/*      */     //   705: invokevirtual 1110	org/gudy/azureus2/core3/util/AEThread:setDaemon	(Z)V
/*      */     //   708: aload_0
/*      */     //   709: getfield 962	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_thread	Lorg/gudy/azureus2/core3/util/AEThread;
/*      */     //   712: invokevirtual 1109	org/gudy/azureus2/core3/util/AEThread:start	()V
/*      */     //   715: aload_0
/*      */     //   716: getfield 957	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   719: invokevirtual 1103	org/gudy/azureus2/core3/util/AEMonitor2:exit	()V
/*      */     //   722: goto +15 -> 737
/*      */     //   725: astore 20
/*      */     //   727: aload_0
/*      */     //   728: getfield 957	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:send_queue_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   731: invokevirtual 1103	org/gudy/azureus2/core3/util/AEMonitor2:exit	()V
/*      */     //   734: aload 20
/*      */     //   736: athrow
/*      */     //   737: goto +92 -> 829
/*      */     //   740: aload 14
/*      */     //   742: invokevirtual 1025	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerRequestImpl:sent	()V
/*      */     //   745: aload 13
/*      */     //   747: ifnonnull +14 -> 761
/*      */     //   750: new 558	java/lang/NullPointerException
/*      */     //   753: dup
/*      */     //   754: ldc_w 520
/*      */     //   757: invokespecial 1044	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
/*      */     //   760: athrow
/*      */     //   761: aload_0
/*      */     //   762: aload 13
/*      */     //   764: invokespecial 1001	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:sendToSocket	(Ljava/net/DatagramPacket;)V
/*      */     //   767: aload_0
/*      */     //   768: getfield 943	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:stats	Lcom/aelitis/net/udp/uc/impl/PRUDPPacketHandlerStatsImpl;
/*      */     //   771: iload 12
/*      */     //   773: invokevirtual 1033	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerStatsImpl:packetSent	(I)V
/*      */     //   776: aload_0
/*      */     //   777: getfield 935	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:TRACE_REQUESTS	Z
/*      */     //   780: ifeq +49 -> 829
/*      */     //   783: new 586	org/gudy/azureus2/core3/logging/LogEvent
/*      */     //   786: dup
/*      */     //   787: getstatic 953	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:LOGID	Lorg/gudy/azureus2/core3/logging/LogIDs;
/*      */     //   790: new 562	java/lang/StringBuilder
/*      */     //   793: dup
/*      */     //   794: invokespecial 1055	java/lang/StringBuilder:<init>	()V
/*      */     //   797: ldc_w 511
/*      */     //   800: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   803: aload_3
/*      */     //   804: invokevirtual 1059	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   807: ldc_w 503
/*      */     //   810: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   813: aload_2
/*      */     //   814: invokevirtual 987	com/aelitis/net/udp/uc/PRUDPPacket:getString	()Ljava/lang/String;
/*      */     //   817: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   820: invokevirtual 1056	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   823: invokespecial 1094	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;Ljava/lang/String;)V
/*      */     //   826: invokestatic 1097	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*      */     //   829: aload 14
/*      */     //   831: areturn
/*      */     //   832: astore 15
/*      */     //   834: aload_0
/*      */     //   835: getfield 956	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   838: invokevirtual 1102	org/gudy/azureus2/core3/util/AEMonitor2:enter	()V
/*      */     //   841: aload_0
/*      */     //   842: getfield 952	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests	Ljava/util/Map;
/*      */     //   845: new 556	java/lang/Integer
/*      */     //   848: dup
/*      */     //   849: aload_2
/*      */     //   850: invokevirtual 983	com/aelitis/net/udp/uc/PRUDPPacket:getTransactionId	()I
/*      */     //   853: invokespecial 1042	java/lang/Integer:<init>	(I)V
/*      */     //   856: invokeinterface 1139 2 0
/*      */     //   861: pop
/*      */     //   862: aload_0
/*      */     //   863: getfield 956	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   866: invokevirtual 1103	org/gudy/azureus2/core3/util/AEMonitor2:exit	()V
/*      */     //   869: goto +15 -> 884
/*      */     //   872: astore 21
/*      */     //   874: aload_0
/*      */     //   875: getfield 956	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:requests_mon	Lorg/gudy/azureus2/core3/util/AEMonitor2;
/*      */     //   878: invokevirtual 1103	org/gudy/azureus2/core3/util/AEMonitor2:exit	()V
/*      */     //   881: aload 21
/*      */     //   883: athrow
/*      */     //   884: aload 15
/*      */     //   886: athrow
/*      */     //   887: astore 9
/*      */     //   889: aload 9
/*      */     //   891: instanceof 558
/*      */     //   894: ifeq +8 -> 902
/*      */     //   897: aload 9
/*      */     //   899: invokestatic 1113	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   902: aload 9
/*      */     //   904: invokestatic 1115	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*      */     //   907: astore 10
/*      */     //   909: new 586	org/gudy/azureus2/core3/logging/LogEvent
/*      */     //   912: dup
/*      */     //   913: getstatic 953	com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl:LOGID	Lorg/gudy/azureus2/core3/logging/LogIDs;
/*      */     //   916: iconst_3
/*      */     //   917: new 562	java/lang/StringBuilder
/*      */     //   920: dup
/*      */     //   921: invokespecial 1055	java/lang/StringBuilder:<init>	()V
/*      */     //   924: ldc_w 513
/*      */     //   927: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   930: aload_3
/*      */     //   931: invokevirtual 1059	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   934: ldc_w 498
/*      */     //   937: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   940: aload 10
/*      */     //   942: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   945: invokevirtual 1056	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   948: invokespecial 1093	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;ILjava/lang/String;)V
/*      */     //   951: invokestatic 1097	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*      */     //   954: aload 10
/*      */     //   956: ldc_w 507
/*      */     //   959: invokevirtual 1052	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
/*      */     //   962: ifeq +44 -> 1006
/*      */     //   965: new 562	java/lang/StringBuilder
/*      */     //   968: dup
/*      */     //   969: invokespecial 1055	java/lang/StringBuilder:<init>	()V
/*      */     //   972: ldc_w 522
/*      */     //   975: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   978: aload_2
/*      */     //   979: invokevirtual 987	com/aelitis/net/udp/uc/PRUDPPacket:getString	()Ljava/lang/String;
/*      */     //   982: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   985: ldc_w 502
/*      */     //   988: invokevirtual 1060	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   991: aload_1
/*      */     //   992: invokevirtual 1059	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*      */     //   995: invokevirtual 1056	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   998: invokestatic 1112	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */     //   1001: aload 9
/*      */     //   1003: invokestatic 1113	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/Throwable;)V
/*      */     //   1006: new 531	com/aelitis/net/udp/uc/PRUDPPacketHandlerException
/*      */     //   1009: dup
/*      */     //   1010: ldc_w 515
/*      */     //   1013: aload 9
/*      */     //   1015: invokespecial 990	com/aelitis/net/udp/uc/PRUDPPacketHandlerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1018: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1166	-> byte code offset #0
/*      */     //   Java source line #1168	-> byte code offset #7
/*      */     //   Java source line #1170	-> byte code offset #14
/*      */     //   Java source line #1173	-> byte code offset #29
/*      */     //   Java source line #1176	-> byte code offset #40
/*      */     //   Java source line #1178	-> byte code offset #45
/*      */     //   Java source line #1180	-> byte code offset #51
/*      */     //   Java source line #1182	-> byte code offset #74
/*      */     //   Java source line #1186	-> byte code offset #89
/*      */     //   Java source line #1188	-> byte code offset #102
/*      */     //   Java source line #1190	-> byte code offset #113
/*      */     //   Java source line #1192	-> byte code offset #119
/*      */     //   Java source line #1193	-> byte code offset #126
/*      */     //   Java source line #1195	-> byte code offset #133
/*      */     //   Java source line #1197	-> byte code offset #139
/*      */     //   Java source line #1203	-> byte code offset #143
/*      */     //   Java source line #1205	-> byte code offset #152
/*      */     //   Java source line #1206	-> byte code offset #158
/*      */     //   Java source line #1210	-> byte code offset #171
/*      */     //   Java source line #1212	-> byte code offset #182
/*      */     //   Java source line #1216	-> byte code offset #192
/*      */     //   Java source line #1219	-> byte code offset #204
/*      */     //   Java source line #1221	-> byte code offset #210
/*      */     //   Java source line #1223	-> byte code offset #216
/*      */     //   Java source line #1225	-> byte code offset #237
/*      */     //   Java source line #1223	-> byte code offset #250
/*      */     //   Java source line #1228	-> byte code offset #256
/*      */     //   Java source line #1230	-> byte code offset #265
/*      */     //   Java source line #1231	-> byte code offset #275
/*      */     //   Java source line #1232	-> byte code offset #282
/*      */     //   Java source line #1234	-> byte code offset #289
/*      */     //   Java source line #1238	-> byte code offset #296
/*      */     //   Java source line #1239	-> byte code offset #303
/*      */     //   Java source line #1241	-> byte code offset #313
/*      */     //   Java source line #1242	-> byte code offset #320
/*      */     //   Java source line #1245	-> byte code offset #327
/*      */     //   Java source line #1247	-> byte code offset #341
/*      */     //   Java source line #1250	-> byte code offset #354
/*      */     //   Java source line #1252	-> byte code offset #361
/*      */     //   Java source line #1256	-> byte code offset #384
/*      */     //   Java source line #1257	-> byte code offset #391
/*      */     //   Java source line #1256	-> byte code offset #394
/*      */     //   Java source line #1262	-> byte code offset #406
/*      */     //   Java source line #1265	-> byte code offset #420
/*      */     //   Java source line #1267	-> byte code offset #427
/*      */     //   Java source line #1269	-> byte code offset #438
/*      */     //   Java source line #1273	-> byte code offset #443
/*      */     //   Java source line #1275	-> byte code offset #449
/*      */     //   Java source line #1277	-> byte code offset #458
/*      */     //   Java source line #1278	-> byte code offset #465
/*      */     //   Java source line #1284	-> byte code offset #511
/*      */     //   Java source line #1288	-> byte code offset #524
/*      */     //   Java source line #1290	-> byte code offset #539
/*      */     //   Java source line #1292	-> byte code offset #566
/*      */     //   Java source line #1294	-> byte code offset #573
/*      */     //   Java source line #1296	-> byte code offset #577
/*      */     //   Java source line #1297	-> byte code offset #590
/*      */     //   Java source line #1296	-> byte code offset #638
/*      */     //   Java source line #1299	-> byte code offset #644
/*      */     //   Java source line #1302	-> byte code offset #671
/*      */     //   Java source line #1304	-> byte code offset #678
/*      */     //   Java source line #1306	-> byte code offset #685
/*      */     //   Java source line #1408	-> byte code offset #700
/*      */     //   Java source line #1410	-> byte code offset #708
/*      */     //   Java source line #1415	-> byte code offset #715
/*      */     //   Java source line #1416	-> byte code offset #722
/*      */     //   Java source line #1415	-> byte code offset #725
/*      */     //   Java source line #1419	-> byte code offset #740
/*      */     //   Java source line #1421	-> byte code offset #745
/*      */     //   Java source line #1423	-> byte code offset #750
/*      */     //   Java source line #1426	-> byte code offset #761
/*      */     //   Java source line #1430	-> byte code offset #767
/*      */     //   Java source line #1432	-> byte code offset #776
/*      */     //   Java source line #1433	-> byte code offset #783
/*      */     //   Java source line #1441	-> byte code offset #829
/*      */     //   Java source line #1443	-> byte code offset #832
/*      */     //   Java source line #1448	-> byte code offset #834
/*      */     //   Java source line #1450	-> byte code offset #841
/*      */     //   Java source line #1454	-> byte code offset #862
/*      */     //   Java source line #1455	-> byte code offset #869
/*      */     //   Java source line #1454	-> byte code offset #872
/*      */     //   Java source line #1457	-> byte code offset #884
/*      */     //   Java source line #1459	-> byte code offset #887
/*      */     //   Java source line #1464	-> byte code offset #889
/*      */     //   Java source line #1466	-> byte code offset #897
/*      */     //   Java source line #1469	-> byte code offset #902
/*      */     //   Java source line #1471	-> byte code offset #909
/*      */     //   Java source line #1474	-> byte code offset #954
/*      */     //   Java source line #1476	-> byte code offset #965
/*      */     //   Java source line #1478	-> byte code offset #1001
/*      */     //   Java source line #1481	-> byte code offset #1006
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	1019	0	this	PRUDPPacketHandlerImpl
/*      */     //   0	1019	1	auth	PasswordAuthentication
/*      */     //   0	1019	2	request_packet	PRUDPPacket
/*      */     //   0	1019	3	destination_address	InetSocketAddress
/*      */     //   0	1019	4	receiver	PRUDPPacketReceiver
/*      */     //   0	1019	5	timeout	long
/*      */     //   0	1019	7	priority	int
/*      */     //   49	26	8	delegate	PRUDPPacketHandlerImpl
/*      */     //   100	221	9	baos	MyByteArrayOutputStream
/*      */     //   887	127	9	e	Throwable
/*      */     //   111	4	10	os	DataOutputStream
/*      */     //   907	48	10	msg	String
/*      */     //   124	208	11	_buffer	byte[]
/*      */     //   131	641	12	_length	int
/*      */     //   150	140	13	hasher	org.gudy.azureus2.core3.util.SHA1Hasher
/*      */     //   339	424	13	dg_packet	DatagramPacket
/*      */     //   156	86	14	user_name	String
/*      */     //   352	478	14	request	PRUDPPacketHandlerRequestImpl
/*      */     //   169	26	15	password	String
/*      */     //   575	86	15	str	String
/*      */     //   832	53	15	e	Throwable
/*      */     //   187	3	16	sha1_password	byte[]
/*      */     //   202	83	16	sha1_password	byte[]
/*      */     //   578	61	16	i	int
/*      */     //   208	91	17	user_bytes	byte[]
/*      */     //   217	34	18	i	int
/*      */     //   294	12	18	overall_hash	byte[]
/*      */     //   394	10	19	localObject1	Object
/*      */     //   725	10	20	localObject2	Object
/*      */     //   872	10	21	localObject3	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   354	384	394	finally
/*      */     //   394	396	394	finally
/*      */     //   420	715	725	finally
/*      */     //   725	727	725	finally
/*      */     //   406	831	832	java/lang/Throwable
/*      */     //   834	862	872	finally
/*      */     //   872	874	872	finally
/*      */     //   89	831	887	java/lang/Throwable
/*      */     //   832	887	887	java/lang/Throwable
/*      */   }
/*      */   
/*      */   public void closeSession()
/*      */     throws PRUDPPacketHandlerException
/*      */   {}
/*      */   
/*      */   private static class MyByteArrayOutputStream
/*      */     extends ByteArrayOutputStream
/*      */   {
/*      */     private MyByteArrayOutputStream(int size)
/*      */     {
/* 1763 */       super();
/*      */     }
/*      */     
/*      */ 
/*      */     private byte[] getBuffer()
/*      */     {
/* 1769 */       return this.buf;
/*      */     }
/*      */   }
/*      */   
/*      */   protected static abstract interface PacketTransformer
/*      */   {
/*      */     public abstract void transformSend(DatagramPacket paramDatagramPacket);
/*      */     
/*      */     public abstract void transformReceive(DatagramPacket paramDatagramPacket);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */