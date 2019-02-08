/*     */ package com.aelitis.net.udp.mc.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.NetUtils;
/*     */ import com.aelitis.net.udp.mc.MCGroup;
/*     */ import com.aelitis.net.udp.mc.MCGroupAdapter;
/*     */ import com.aelitis.net.udp.mc.MCGroupException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.Inet6Address;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MulticastSocket;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.SocketException;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
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
/*     */ 
/*     */ 
/*     */ public class MCGroupImpl
/*     */   implements MCGroup
/*     */ {
/*     */   private static final int TTL = 4;
/*     */   private static final int PACKET_SIZE = 8192;
/*     */   private static boolean overall_suspended;
/*  58 */   private static Map<String, MCGroupImpl> singletons = new HashMap();
/*     */   
/*  60 */   private static AEMonitor class_mon = new AEMonitor("MCGroup:class");
/*     */   
/*  62 */   private static AsyncDispatcher async_dispatcher = new AsyncDispatcher();
/*     */   
/*     */   private MCGroupAdapter adapter;
/*     */   private String group_address_str;
/*     */   private int group_port;
/*     */   private int control_port;
/*     */   protected InetSocketAddress group_address;
/*     */   private String[] selected_interfaces;
/*     */   
/*     */   public static MCGroupImpl getSingleton(MCGroupAdapter adapter, String group_address, int group_port, int control_port, String[] interfaces)
/*     */     throws MCGroupException
/*     */   {
/*     */     try
/*     */     {
/*  76 */       class_mon.enter();
/*     */       
/*  78 */       String key = group_address + ":" + group_port + ":" + control_port;
/*     */       
/*  80 */       MCGroupImpl singleton = (MCGroupImpl)singletons.get(key);
/*     */       int last_allocated;
/*  82 */       if (singleton == null)
/*     */       {
/*  84 */         if (control_port == 0)
/*     */         {
/*  86 */           last_allocated = COConfigurationManager.getIntParameter("mcgroup.ports." + key, 0);
/*     */           
/*  88 */           if (last_allocated != 0) {
/*     */             try
/*     */             {
/*  91 */               DatagramSocket test_socket = new DatagramSocket(null);
/*     */               
/*  93 */               test_socket.setReuseAddress(false);
/*     */               
/*  95 */               test_socket.bind(new InetSocketAddress(last_allocated));
/*     */               
/*  97 */               test_socket.close();
/*     */               
/*  99 */               control_port = last_allocated;
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 103 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 108 */         singleton = new MCGroupImpl(adapter, group_address, group_port, control_port, interfaces, overall_suspended);
/*     */         
/* 110 */         if (control_port == 0)
/*     */         {
/* 112 */           control_port = singleton.getControlPort();
/*     */           
/* 114 */           COConfigurationManager.setParameter("mcgroup.ports." + key, control_port);
/*     */         }
/*     */         
/* 117 */         singletons.put(key, singleton);
/*     */       }
/*     */       
/* 120 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/* 124 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setSuspended(boolean suspended)
/*     */   {
/*     */     try
/*     */     {
/* 133 */       class_mon.enter();
/*     */       
/* 135 */       if (overall_suspended == suspended) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 140 */       overall_suspended = suspended;
/*     */       
/* 142 */       for (MCGroupImpl group : singletons.values())
/*     */       {
/* 144 */         group.setInstanceSuspended(overall_suspended);
/*     */       }
/*     */     }
/*     */     finally {
/* 148 */       class_mon.exit();
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
/*     */ 
/* 161 */   private boolean ttl_problem_reported = true;
/* 162 */   private boolean sso_problem_reported = true;
/*     */   
/* 164 */   protected AEMonitor this_mon = new AEMonitor("MCGroup");
/*     */   
/* 166 */   private Map<String, Set<InetAddress>> current_registrations = new HashMap();
/*     */   
/*     */   private volatile boolean instance_suspended;
/* 169 */   private List<Object[]> suspended_threads = new ArrayList();
/*     */   
/* 171 */   private Map<String, MulticastSocket> socket_cache = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private MCGroupImpl(MCGroupAdapter _adapter, String _group_address, int _group_port, int _control_port, String[] _interfaces, boolean _is_suspended)
/*     */     throws MCGroupException
/*     */   {
/* 184 */     this.adapter = _adapter;
/*     */     
/* 186 */     this.group_address_str = _group_address;
/* 187 */     this.group_port = _group_port;
/* 188 */     this.control_port = _control_port;
/* 189 */     this.selected_interfaces = _interfaces;
/*     */     
/* 191 */     this.instance_suspended = _is_suspended;
/*     */     try
/*     */     {
/* 194 */       InetAddress ia = HostNameToIPResolver.syncResolve(this.group_address_str);
/*     */       
/* 196 */       this.group_address = new InetSocketAddress(ia, 0);
/*     */       
/* 198 */       processNetworkInterfaces(true);
/*     */       
/* 200 */       SimpleTimer.addPeriodicEvent("MCGroup:refresher", 60000L, new TimerEventPerformer()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void perform(TimerEvent event)
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 210 */             MCGroupImpl.this.processNetworkInterfaces(false);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 214 */             MCGroupImpl.this.adapter.log(e);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 221 */       throw new MCGroupException("Failed to initialise MCGroup", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void setInstanceSuspended(boolean _suspended)
/*     */   {
/*     */     try
/*     */     {
/* 230 */       this.this_mon.enter();
/*     */       
/* 232 */       if (this.instance_suspended == _suspended) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 237 */       this.instance_suspended = _suspended;
/*     */       
/* 239 */       if (!this.instance_suspended)
/*     */       {
/* 241 */         List<Object[]> states = new ArrayList(this.suspended_threads);
/*     */         
/* 243 */         this.suspended_threads.clear();
/*     */         
/* 245 */         for (final Object[] state : states)
/*     */         {
/* 247 */           new AEThread2((String)state[0], true)
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/* 252 */               MCGroupImpl.this.handleSocket((NetworkInterface)state[1], (InetAddress)state[2], (DatagramSocket)state[3], ((Boolean)state[4]).booleanValue());
/*     */             }
/*     */           }.start();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 259 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 262 */     if (!_suspended)
/*     */     {
/* 264 */       async_dispatcher.dispatch(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */           try
/*     */           {
/* 271 */             MCGroupImpl.this.processNetworkInterfaces(false);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 275 */             MCGroupImpl.this.adapter.log(e);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void processNetworkInterfaces(boolean start_of_day)
/*     */     throws SocketException
/*     */   {
/* 288 */     Map<String, Set<InetAddress>> new_registrations = new HashMap();
/*     */     
/* 290 */     Map<String, NetworkInterface> changed_interfaces = new HashMap();
/*     */     try
/*     */     {
/* 293 */       this.this_mon.enter();
/*     */       
/* 295 */       if (this.instance_suspended) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 300 */       List<NetworkInterface> x = NetUtils.getNetworkInterfaces();
/*     */       
/* 302 */       for (final NetworkInterface network_interface : x)
/*     */       {
/* 304 */         if (!interfaceSelected(network_interface))
/*     */         {
/* 306 */           if (start_of_day)
/*     */           {
/* 308 */             this.adapter.trace("ignoring interface " + network_interface.getName() + ":" + network_interface.getDisplayName() + ", not selected");
/*     */           }
/*     */           
/*     */         }
/*     */         else
/*     */         {
/* 314 */           String ni_name = network_interface.getName();
/*     */           
/* 316 */           Set<InetAddress> old_address_set = (Set)this.current_registrations.get(ni_name);
/*     */           
/* 318 */           if (old_address_set == null)
/*     */           {
/* 320 */             old_address_set = new HashSet();
/*     */           }
/*     */           
/* 323 */           Set<InetAddress> new_address_set = new HashSet();
/*     */           
/* 325 */           new_registrations.put(ni_name, new_address_set);
/*     */           
/* 327 */           Enumeration<InetAddress> ni_addresses = network_interface.getInetAddresses();
/*     */           
/* 329 */           while (ni_addresses.hasMoreElements())
/*     */           {
/* 331 */             final InetAddress ni_address = (InetAddress)ni_addresses.nextElement();
/*     */             
/* 333 */             new_address_set.add(ni_address);
/*     */             
/* 335 */             if (!old_address_set.contains(ni_address))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 344 */               if (ni_address.isLoopbackAddress())
/*     */               {
/* 346 */                 if (start_of_day)
/*     */                 {
/* 348 */                   this.adapter.trace("ignoring loopback address " + ni_address + ", interface " + network_interface.getName());
/*     */ 
/*     */                 }
/*     */                 
/*     */ 
/*     */               }
/* 354 */               else if ((ni_address instanceof Inet6Address))
/*     */               {
/* 356 */                 if (start_of_day)
/*     */                 {
/* 358 */                   this.adapter.trace("ignoring IPv6 address " + ni_address + ", interface " + network_interface.getName());
/*     */                 }
/*     */                 
/*     */               }
/*     */               else
/*     */               {
/* 364 */                 if (!start_of_day)
/*     */                 {
/* 366 */                   if (!changed_interfaces.containsKey(ni_name))
/*     */                   {
/* 368 */                     changed_interfaces.put(ni_name, network_interface);
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/*     */                 try
/*     */                 {
/* 375 */                   final MulticastSocket mc_sock = new MulticastSocket(this.group_port);
/*     */                   
/* 377 */                   mc_sock.setReuseAddress(true);
/*     */                   
/*     */ 
/*     */                   try
/*     */                   {
/* 382 */                     mc_sock.setTimeToLive(4);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 386 */                     if (!this.ttl_problem_reported)
/*     */                     {
/* 388 */                       this.ttl_problem_reported = true;
/*     */                       
/* 390 */                       this.adapter.log(e);
/*     */                     }
/*     */                   }
/*     */                   
/* 394 */                   String addresses_string = "";
/*     */                   
/* 396 */                   Enumeration<InetAddress> it = network_interface.getInetAddresses();
/*     */                   
/* 398 */                   while (it.hasMoreElements())
/*     */                   {
/* 400 */                     InetAddress addr = (InetAddress)it.nextElement();
/*     */                     
/* 402 */                     addresses_string = addresses_string + (addresses_string.length() == 0 ? "" : ",") + addr;
/*     */                   }
/*     */                   
/* 405 */                   this.adapter.trace("group = " + this.group_address + "/" + network_interface.getName() + ":" + network_interface.getDisplayName() + "-" + addresses_string + ": started");
/*     */                   
/*     */ 
/*     */ 
/* 409 */                   mc_sock.joinGroup(this.group_address, network_interface);
/*     */                   
/* 411 */                   mc_sock.setNetworkInterface(network_interface);
/*     */                   
/*     */ 
/*     */ 
/* 415 */                   mc_sock.setLoopbackMode(false);
/*     */                   
/* 417 */                   Runtime.getRuntime().addShutdownHook(new AEThread("MCGroup:VMShutdown")
/*     */                   {
/*     */ 
/*     */                     public void runSupport()
/*     */                     {
/*     */                       try
/*     */                       {
/* 424 */                         mc_sock.leaveGroup(MCGroupImpl.this.group_address, network_interface);
/*     */                       }
/*     */                       catch (Throwable e)
/*     */                       {
/* 428 */                         MCGroupImpl.this.adapter.log(e);
/*     */                       }
/*     */                       
/*     */                     }
/* 432 */                   });
/* 433 */                   new AEThread2("MCGroup:MCListener", true)
/*     */                   {
/*     */ 
/*     */                     public void run()
/*     */                     {
/* 438 */                       MCGroupImpl.this.handleSocket(network_interface, ni_address, mc_sock, true);
/*     */                     }
/*     */                   }.start();
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 444 */                   this.adapter.log(e);
/*     */                 }
/*     */                 
/*     */ 
/*     */                 try
/*     */                 {
/* 450 */                   final DatagramSocket control_socket = new DatagramSocket(null);
/*     */                   
/* 452 */                   control_socket.setReuseAddress(true);
/*     */                   
/* 454 */                   control_socket.bind(new InetSocketAddress(ni_address, this.control_port));
/*     */                   
/* 456 */                   if (this.control_port == 0)
/*     */                   {
/* 458 */                     this.control_port = control_socket.getLocalPort();
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/* 463 */                   new AEThread2("MCGroup:CtrlListener", true)
/*     */                   {
/*     */ 
/*     */                     public void run()
/*     */                     {
/* 468 */                       MCGroupImpl.this.handleSocket(network_interface, ni_address, control_socket, false);
/*     */                     }
/*     */                   }.start();
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 474 */                   this.adapter.log(e);
/*     */                 }
/*     */               } }
/*     */           }
/*     */         } }
/*     */     } finally {
/* 480 */       this.current_registrations = new_registrations;
/*     */       
/* 482 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 485 */     for (NetworkInterface ni : changed_interfaces.values()) {
/*     */       try
/*     */       {
/* 488 */         this.adapter.interfaceChanged(ni);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 492 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getControlPort()
/*     */   {
/* 500 */     return this.control_port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean interfaceSelected(NetworkInterface ni)
/*     */   {
/* 507 */     if ((this.selected_interfaces != null) && (this.selected_interfaces.length > 0))
/*     */     {
/* 509 */       boolean ok = false;
/*     */       
/* 511 */       for (int i = 0; i < this.selected_interfaces.length; i++)
/*     */       {
/* 513 */         if (ni.getName().equalsIgnoreCase(this.selected_interfaces[i]))
/*     */         {
/* 515 */           ok = true;
/*     */           
/* 517 */           break;
/*     */         }
/*     */       }
/*     */       
/* 521 */       return ok;
/*     */     }
/*     */     
/*     */ 
/* 525 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean validNetworkAddress(NetworkInterface network_interface, InetAddress ni_address)
/*     */   {
/* 534 */     String ni_name = network_interface.getName();
/*     */     try
/*     */     {
/* 537 */       this.this_mon.enter();
/*     */       
/* 539 */       Set<InetAddress> set = (Set)this.current_registrations.get(ni_name);
/*     */       boolean bool;
/* 541 */       if (set == null)
/*     */       {
/* 543 */         return false;
/*     */       }
/*     */       
/* 546 */       return set.contains(ni_address);
/*     */     }
/*     */     finally
/*     */     {
/* 550 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendToGroup(final byte[] data)
/*     */   {
/* 559 */     if (this.instance_suspended)
/*     */     {
/* 561 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 566 */     async_dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 572 */         MCGroupImpl.this.sendToGroupSupport(data);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void sendToGroupSupport(byte[] data)
/*     */   {
/*     */     try
/*     */     {
/* 582 */       List<NetworkInterface> x = NetUtils.getNetworkInterfaces();
/*     */       
/* 584 */       for (NetworkInterface network_interface : x)
/*     */       {
/* 586 */         if (interfaceSelected(network_interface))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 591 */           Enumeration<InetAddress> ni_addresses = network_interface.getInetAddresses();
/*     */           
/* 593 */           String socket_key = null;
/*     */           
/* 595 */           while (ni_addresses.hasMoreElements())
/*     */           {
/* 597 */             InetAddress ni_address = (InetAddress)ni_addresses.nextElement();
/*     */             
/* 599 */             if ((!(ni_address instanceof Inet6Address)) && (!ni_address.isLoopbackAddress()))
/*     */             {
/* 601 */               socket_key = ni_address.toString();
/*     */               
/* 603 */               break;
/*     */             }
/*     */           }
/*     */           
/* 607 */           if (socket_key != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 612 */             socket_key = socket_key + ":" + this.control_port;
/*     */             
/*     */             try
/*     */             {
/* 616 */               synchronized (this.socket_cache)
/*     */               {
/* 618 */                 MulticastSocket mc_sock = (MulticastSocket)this.socket_cache.get(socket_key);
/*     */                 
/* 620 */                 if (mc_sock == null)
/*     */                 {
/* 622 */                   mc_sock = new MulticastSocket(null);
/*     */                   
/* 624 */                   mc_sock.setReuseAddress(true);
/*     */                   try
/*     */                   {
/* 627 */                     mc_sock.setTimeToLive(4);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 631 */                     if (!this.ttl_problem_reported)
/*     */                     {
/* 633 */                       this.ttl_problem_reported = true;
/*     */                       
/* 635 */                       this.adapter.log(e);
/*     */                     }
/*     */                   }
/*     */                   
/* 639 */                   mc_sock.bind(new InetSocketAddress(this.control_port));
/*     */                   
/* 641 */                   mc_sock.setNetworkInterface(network_interface);
/*     */                   
/* 643 */                   this.socket_cache.put(socket_key, mc_sock);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 648 */                 DatagramPacket packet = new DatagramPacket(data, data.length, this.group_address.getAddress(), this.group_port);
/*     */                 try
/*     */                 {
/* 651 */                   mc_sock.send(packet);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */                   try {
/* 656 */                     mc_sock.close();
/*     */                   }
/*     */                   catch (Throwable f) {}
/*     */                   
/*     */ 
/*     */ 
/* 662 */                   this.socket_cache.remove(socket_key);
/*     */                   
/* 664 */                   throw e;
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 670 */               if (!this.sso_problem_reported)
/*     */               {
/* 672 */                 this.sso_problem_reported = true;
/*     */                 
/* 674 */                 this.adapter.log(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */   public void sendToGroup(final String param_data)
/*     */   {
/* 686 */     if (this.instance_suspended)
/*     */     {
/* 688 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 693 */     async_dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 699 */         MCGroupImpl.this.sendToGroupSupport(param_data);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void sendToGroupSupport(String param_data)
/*     */   {
/*     */     try
/*     */     {
/* 709 */       List<NetworkInterface> x = NetUtils.getNetworkInterfaces();
/*     */       
/* 711 */       for (NetworkInterface network_interface : x)
/*     */       {
/* 713 */         if (interfaceSelected(network_interface))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 718 */           Enumeration<InetAddress> ni_addresses = network_interface.getInetAddresses();
/*     */           
/* 720 */           InetAddress an_address = null;
/*     */           
/* 722 */           while (ni_addresses.hasMoreElements())
/*     */           {
/* 724 */             InetAddress ni_address = (InetAddress)ni_addresses.nextElement();
/*     */             
/* 726 */             if ((!(ni_address instanceof Inet6Address)) && (!ni_address.isLoopbackAddress()))
/*     */             {
/* 728 */               an_address = ni_address;
/*     */               
/* 730 */               break;
/*     */             }
/*     */           }
/*     */           
/* 734 */           if (an_address != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 739 */             String socket_key = an_address.toString() + ":" + this.control_port;
/*     */             
/*     */             try
/*     */             {
/* 743 */               synchronized (this.socket_cache)
/*     */               {
/* 745 */                 MulticastSocket mc_sock = (MulticastSocket)this.socket_cache.get(socket_key);
/*     */                 
/* 747 */                 if (mc_sock == null)
/*     */                 {
/* 749 */                   mc_sock = new MulticastSocket(null);
/*     */                   
/* 751 */                   mc_sock.setReuseAddress(true);
/*     */                   try
/*     */                   {
/* 754 */                     mc_sock.setTimeToLive(4);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 758 */                     if (!this.ttl_problem_reported)
/*     */                     {
/* 760 */                       this.ttl_problem_reported = true;
/*     */                       
/* 762 */                       this.adapter.log(e);
/*     */                     }
/*     */                   }
/*     */                   
/* 766 */                   mc_sock.bind(new InetSocketAddress(this.control_port));
/*     */                   
/* 768 */                   mc_sock.setNetworkInterface(network_interface);
/*     */                   
/* 770 */                   this.socket_cache.put(socket_key, mc_sock);
/*     */                 }
/*     */                 
/* 773 */                 byte[] data = param_data.replaceAll("%AZINTERFACE%", an_address.getHostAddress()).getBytes();
/*     */                 
/*     */ 
/*     */ 
/* 777 */                 DatagramPacket packet = new DatagramPacket(data, data.length, this.group_address.getAddress(), this.group_port);
/*     */                 try
/*     */                 {
/* 780 */                   mc_sock.send(packet);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/*     */                   try {
/* 785 */                     mc_sock.close();
/*     */                   }
/*     */                   catch (Throwable f) {}
/*     */                   
/*     */ 
/*     */ 
/* 791 */                   this.socket_cache.remove(socket_key);
/*     */                   
/* 793 */                   throw e;
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 798 */               if (!this.sso_problem_reported)
/*     */               {
/* 800 */                 this.sso_problem_reported = true;
/*     */                 
/* 802 */                 this.adapter.log(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleSocket(NetworkInterface network_interface, InetAddress local_address, DatagramSocket socket, boolean log_on_stop)
/*     */   {
/* 817 */     long successful_accepts = 0L;
/* 818 */     long failed_accepts = 0L;
/*     */     
/* 820 */     int port = socket.getLocalPort();
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 826 */       socket.setSoTimeout(30000);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/* 834 */       if (this.instance_suspended) {
/*     */         try
/*     */         {
/* 837 */           this.this_mon.enter();
/*     */           
/* 839 */           if (this.instance_suspended)
/*     */           {
/* 841 */             this.suspended_threads.add(new Object[] { Thread.currentThread().getName(), network_interface, local_address, socket, Boolean.valueOf(log_on_stop) }); return;
/*     */           }
/*     */           
/*     */         }
/*     */         finally
/*     */         {
/* 847 */           this.this_mon.exit();
/*     */         }
/*     */       }
/* 850 */       if (!validNetworkAddress(network_interface, local_address))
/*     */       {
/* 852 */         if (log_on_stop)
/*     */         {
/* 854 */           this.adapter.trace("group = " + this.group_address + "/" + network_interface.getName() + ":" + network_interface.getDisplayName() + " - " + local_address + ": stopped");
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 860 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 864 */         byte[] buf = new byte[' '];
/*     */         
/* 866 */         DatagramPacket packet = new DatagramPacket(buf, buf.length);
/*     */         
/* 868 */         socket.receive(packet);
/*     */         
/* 870 */         successful_accepts += 1L;
/*     */         
/* 872 */         failed_accepts = 0L;
/*     */         
/* 874 */         receivePacket(network_interface, local_address, packet);
/*     */ 
/*     */       }
/*     */       catch (SocketTimeoutException e) {}catch (Throwable e)
/*     */       {
/*     */ 
/* 880 */         failed_accepts += 1L;
/*     */         
/* 882 */         this.adapter.trace("MCGroup: receive failed on port " + port + ":" + e.getMessage());
/*     */         
/* 884 */         if (((failed_accepts > 100L) && (successful_accepts == 0L)) || (failed_accepts > 1000L))
/*     */         {
/* 886 */           this.adapter.trace("    too many failures, abandoning");
/*     */           
/* 888 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void receivePacket(NetworkInterface network_interface, InetAddress local_address, DatagramPacket packet)
/*     */   {
/* 900 */     if (this.instance_suspended)
/*     */     {
/* 902 */       return;
/*     */     }
/*     */     
/* 905 */     byte[] data = packet.getData();
/* 906 */     int len = packet.getLength();
/*     */     
/*     */ 
/*     */ 
/* 910 */     this.adapter.received(network_interface, local_address, (InetSocketAddress)packet.getSocketAddress(), data, len);
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
/*     */   public void sendToMember(InetSocketAddress address, byte[] data)
/*     */     throws MCGroupException
/*     */   {
/* 925 */     if (this.instance_suspended)
/*     */     {
/* 927 */       return;
/*     */     }
/*     */     
/* 930 */     DatagramSocket reply_socket = null;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 935 */       reply_socket = new DatagramSocket(null);
/*     */       
/* 937 */       reply_socket.setReuseAddress(true);
/*     */       
/* 939 */       reply_socket.bind(new InetSocketAddress(this.group_port));
/*     */       
/* 941 */       DatagramPacket reply_packet = new DatagramPacket(data, data.length, address);
/*     */       
/* 943 */       reply_socket.send(reply_packet); return;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 947 */       throw new MCGroupException("sendToMember failed", e);
/*     */     }
/*     */     finally
/*     */     {
/* 951 */       if (reply_socket != null) {
/*     */         try
/*     */         {
/* 954 */           reply_socket.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/mc/impl/MCGroupImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */