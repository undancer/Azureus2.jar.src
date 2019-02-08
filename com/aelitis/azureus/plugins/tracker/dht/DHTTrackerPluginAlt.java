/*     */ package com.aelitis.azureus.plugins.tracker.dht;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeContact;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPUtils;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
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
/*     */ public class DHTTrackerPluginAlt
/*     */ {
/*  57 */   private static final long startup_time = ;
/*     */   
/*     */   private static final int startup_grace = 60000;
/*     */   
/*     */   private static final int INITAL_DELAY = 5000;
/*     */   
/*     */   private static final int RPC_TIMEOUT = 15000;
/*     */   
/*     */   private static final int LOOKUP_TIMEOUT = 90000;
/*     */   
/*     */   private static final int LOOKUP_LINGER = 5000;
/*     */   
/*     */   private static final int CONC_LOOKUPS = 8;
/*     */   private static final int NUM_WANT = 32;
/*     */   private static final int NID_CLOSENESS_LIMIT = 10;
/*     */   private final int port;
/*  73 */   private final byte[] NID = new byte[20];
/*     */   
/*     */   private DatagramSocket current_server;
/*     */   
/*     */   private Throwable last_server_error;
/*  78 */   private ByteArrayHashMap<Object[]> tid_map = new ByteArrayHashMap();
/*     */   
/*     */   private TimerEventPeriodic timer_event;
/*     */   
/*  82 */   private AsyncDispatcher dispatcher = new AsyncDispatcher();
/*     */   
/*     */   private volatile long lookup_count;
/*     */   
/*     */   private volatile long hit_count;
/*     */   
/*     */   private volatile long packets_out;
/*     */   
/*     */   private volatile long packets_in;
/*     */   private volatile long bytes_out;
/*     */   private volatile long bytes_in;
/*     */   
/*     */   protected DHTTrackerPluginAlt(int _port)
/*     */   {
/*  96 */     this.port = _port;
/*     */     
/*     */ 
/*     */ 
/* 100 */     RandomUtils.nextBytes(this.NID);
/*     */   }
/*     */   
/*     */ 
/*     */   private DatagramSocket getServer()
/*     */   {
/* 106 */     synchronized (this)
/*     */     {
/* 108 */       if (this.current_server != null)
/*     */       {
/* 110 */         if (this.current_server.isClosed())
/*     */         {
/* 112 */           this.current_server = null;
/*     */         }
/*     */         else
/*     */         {
/* 116 */           return this.current_server;
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 121 */         final DatagramSocket server = new DatagramSocket(null);
/*     */         
/* 123 */         server.setReuseAddress(true);
/*     */         
/* 125 */         InetAddress bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */         
/* 127 */         if (bind_ip == null)
/*     */         {
/* 129 */           bind_ip = InetAddress.getByName("127.0.0.1");
/*     */         }
/*     */         
/* 132 */         server.bind(new InetSocketAddress(bind_ip, this.port));
/*     */         
/* 134 */         this.current_server = server;
/*     */         
/* 136 */         this.last_server_error = null;
/*     */         
/* 138 */         new AEThread2("DHTPluginAlt:server")
/*     */         {
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/*     */               for (;;)
/*     */               {
/* 146 */                 byte[] buffer = new byte['᐀'];
/*     */                 
/* 148 */                 DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
/*     */                 
/* 150 */                 server.receive(packet);
/*     */                 
/* 152 */                 DHTTrackerPluginAlt.access$008(DHTTrackerPluginAlt.this);
/*     */                 
/* 154 */                 DHTTrackerPluginAlt.access$114(DHTTrackerPluginAlt.this, packet.getLength());
/*     */                 
/* 156 */                 Map<String, Object> map = new BDecoder().decodeByteArray(packet.getData(), 0, packet.getLength(), false);
/*     */                 
/*     */ 
/*     */ 
/* 160 */                 byte[] tid = (byte[])map.get("t");
/*     */                 
/* 162 */                 if (tid != null)
/*     */                 {
/*     */                   Object[] task;
/*     */                   
/* 166 */                   synchronized (DHTTrackerPluginAlt.this.tid_map)
/*     */                   {
/* 168 */                     task = (Object[])DHTTrackerPluginAlt.this.tid_map.remove(tid);
/*     */                   }
/*     */                   
/* 171 */                   if (task != null)
/*     */                   {
/* 173 */                     DHTTrackerPluginAlt.GetPeersTask.access$300((DHTTrackerPluginAlt.GetPeersTask)task[0], (InetSocketAddress)packet.getSocketAddress(), tid, map);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {}finally
/*     */             {
/*     */               try
/*     */               {
/* 182 */                 server.close();
/*     */               }
/*     */               catch (Throwable f) {}
/*     */               
/*     */ 
/* 187 */               synchronized (DHTTrackerPluginAlt.this)
/*     */               {
/* 189 */                 if (DHTTrackerPluginAlt.this.current_server == server)
/*     */                 {
/* 191 */                   DHTTrackerPluginAlt.this.current_server = null;
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */           }
/* 197 */         }.start();
/* 198 */         return server;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 202 */         this.last_server_error = e;
/*     */         
/* 204 */         return null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void get(final byte[] hash, final boolean no_seeds, final LookupListener listener)
/*     */   {
/* 215 */     SimpleTimer.addEvent("altlookup.delay", SystemTime.getCurrentTime() + 5000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 224 */         if (listener.isComplete())
/*     */         {
/* 226 */           return;
/*     */         }
/*     */         
/* 229 */         if (DHTTrackerPluginAlt.this.dispatcher.getQueueSize() > 100)
/*     */         {
/* 231 */           return;
/*     */         }
/*     */         
/* 234 */         DHTTrackerPluginAlt.this.dispatcher.dispatch(new AERunnable()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/* 241 */             DHTTrackerPluginAlt.this.getSupport(DHTTrackerPluginAlt.2.this.val$hash, DHTTrackerPluginAlt.2.this.val$no_seeds, DHTTrackerPluginAlt.2.this.val$listener);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void getSupport(byte[] hash, boolean no_seeds, LookupListener listener)
/*     */   {
/*     */     List<DHTTransportAlternativeContact> contacts;
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/* 258 */       if (listener.isComplete())
/*     */       {
/* 260 */         return;
/*     */       }
/*     */       
/* 263 */       contacts = DHTUDPUtils.getAlternativeContacts(1, 16);
/*     */       
/* 265 */       if (contacts.size() != 0)
/*     */         break label61;
/* 267 */       long now = SystemTime.getMonotonousTime();
/*     */       
/* 269 */       if (now - startup_time >= 60000L)
/*     */         break;
/*     */       try {
/* 272 */         Thread.sleep(5000L);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 281 */     return;
/*     */     
/*     */ 
/*     */ 
/*     */     label61:
/*     */     
/*     */ 
/*     */ 
/* 289 */     DatagramSocket server = getServer();
/*     */     
/* 291 */     if (server == null)
/*     */     {
/* 293 */       return;
/*     */     }
/*     */     
/* 296 */     this.lookup_count += 1L;
/*     */     
/* 298 */     new GetPeersTask(server, contacts, hash, no_seeds, listener, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] send(GetPeersTask task, DatagramSocket server, InetSocketAddress address, Map<String, Object> map)
/*     */     throws IOException
/*     */   {
/*     */     byte[] tid;
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/* 314 */       tid = new byte[4];
/*     */       
/* 316 */       RandomUtils.nextBytes(tid);
/*     */       
/* 318 */       synchronized (this.tid_map)
/*     */       {
/* 320 */         if (!this.tid_map.containsKey(tid))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 325 */           this.tid_map.put(tid, new Object[] { task, Long.valueOf(SystemTime.getMonotonousTime()) });
/*     */           
/* 327 */           if (this.timer_event == null)
/*     */           {
/* 329 */             this.timer_event = SimpleTimer.addPeriodicEvent("dhtalttimer", 2500L, new TimerEventPerformer()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               public void perform(TimerEvent event)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 339 */                 DHTTrackerPluginAlt.this.checkTimeouts();
/*     */                 
/* 341 */                 synchronized (DHTTrackerPluginAlt.this.tid_map)
/*     */                 {
/* 343 */                   if (DHTTrackerPluginAlt.this.tid_map.size() == 0)
/*     */                   {
/* 345 */                     DHTTrackerPluginAlt.this.timer_event.cancel();
/*     */                     
/* 347 */                     DHTTrackerPluginAlt.this.timer_event = null;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }); }
/*     */         }
/*     */       }
/*     */     }
/*     */     try {
/* 356 */       map.put("t", tid);
/*     */       
/*     */ 
/*     */ 
/* 360 */       byte[] data_out = BEncoder.encode(map);
/*     */       
/* 362 */       DatagramPacket packet = new DatagramPacket(data_out, data_out.length);
/*     */       
/* 364 */       packet.setSocketAddress(address);
/*     */       
/* 366 */       this.packets_out += 1L;
/* 367 */       this.bytes_out += data_out.length;
/*     */       
/* 369 */       server.send(packet);
/*     */       
/* 371 */       return tid;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */       try {
/* 376 */         server.close();
/*     */       }
/*     */       catch (Throwable f) {}
/*     */       
/*     */ 
/*     */ 
/* 382 */       synchronized (this.tid_map)
/*     */       {
/* 384 */         this.tid_map.remove(tid);
/*     */       }
/*     */       
/* 387 */       if ((e instanceof IOException))
/*     */       {
/* 389 */         throw ((IOException)e);
/*     */       }
/*     */       
/*     */ 
/* 393 */       throw new IOException(Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkTimeouts()
/*     */   {
/* 402 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 404 */     List<Object[]> timeouts = null;
/*     */     
/* 406 */     synchronized (this.tid_map)
/*     */     {
/* 408 */       Iterator<byte[]> it = this.tid_map.keys().iterator();
/*     */       
/* 410 */       while (it.hasNext())
/*     */       {
/* 412 */         byte[] key = (byte[])it.next();
/*     */         
/* 414 */         Object[] value = (Object[])this.tid_map.get(key);
/*     */         
/* 416 */         long time = ((Long)value[1]).longValue();
/*     */         
/* 418 */         if (now - time > 15000L)
/*     */         {
/* 420 */           this.tid_map.remove(key);
/*     */           
/* 422 */           if (timeouts == null)
/*     */           {
/* 424 */             timeouts = new ArrayList();
/*     */           }
/*     */           
/* 427 */           timeouts.add(new Object[] { key, value[0] });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 432 */     if (timeouts != null)
/*     */     {
/* 434 */       for (Object[] entry : timeouts) {
/*     */         try
/*     */         {
/* 437 */           ((GetPeersTask)entry[1]).handleTimeout((byte[])entry[0]);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 441 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 450 */     return "lookups=" + this.lookup_count + ", hits=" + this.hit_count + ", out=" + this.packets_out + "/" + DisplayFormatters.formatByteCountToKiBEtc(this.bytes_out) + ", in=" + this.packets_in + "/" + DisplayFormatters.formatByteCountToKiBEtc(this.bytes_in) + (this.last_server_error == null ? "" : new StringBuilder().append(", error=").append(Debug.getNestedExceptionMessage(this.last_server_error)).toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private class GetPeersTask
/*     */   {
/* 459 */     private long start_time = SystemTime.getMonotonousTime();
/*     */     
/*     */     private DatagramSocket server;
/*     */     
/*     */     private byte[] torrent_hash;
/*     */     
/*     */     private boolean no_seeds;
/*     */     private DHTTrackerPluginAlt.LookupListener listener;
/*     */     private List<DHTTransportAlternativeContact> initial_contacts;
/* 468 */     private ByteArrayHashMap<InetSocketAddress> active_queries = new ByteArrayHashMap();
/*     */     
/* 470 */     private Set<InetSocketAddress> queried_nodes = new HashSet();
/*     */     
/* 472 */     Comparator<byte[]> comparator = new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public int compare(byte[] o1, byte[] o2)
/*     */       {
/*     */ 
/*     */ 
/* 480 */         for (int i = 0; i < o1.length; i++)
/*     */         {
/* 482 */           byte b1 = o1[i];
/* 483 */           byte b2 = o2[i];
/*     */           
/* 485 */           if (b1 != b2)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 490 */             byte t = DHTTrackerPluginAlt.GetPeersTask.this.torrent_hash[i];
/*     */             
/* 492 */             int d1 = (b1 ^ t) & 0xFF;
/* 493 */             int d2 = (b2 ^ t) & 0xFF;
/*     */             
/* 495 */             if (d1 != d2)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 500 */               if (d1 < d2)
/*     */               {
/* 502 */                 return -1;
/*     */               }
/*     */               
/*     */ 
/* 506 */               return 1;
/*     */             }
/*     */           }
/*     */         }
/* 510 */         return 0;
/*     */       }
/*     */     };
/*     */     
/* 514 */     private TreeMap<byte[], InetSocketAddress> to_query = new TreeMap(this.comparator);
/*     */     
/* 516 */     private TreeMap<byte[], InetSocketAddress> heard_from = new TreeMap(new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int compare(byte[] o1, byte[] o2)
/*     */       {
/*     */ 
/*     */ 
/* 525 */         return -DHTTrackerPluginAlt.GetPeersTask.this.comparator.compare(o1, o2);
/*     */       }
/* 516 */     });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private long found_peer_time;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 531 */     private Set<InetSocketAddress> found_peers = new HashSet();
/*     */     
/*     */ 
/*     */     private int query_count;
/*     */     
/*     */ 
/*     */     private int timeout_count;
/*     */     
/*     */     private int reply_count;
/*     */     
/*     */     private boolean completed;
/*     */     
/*     */     private boolean failed;
/*     */     
/*     */ 
/*     */     private GetPeersTask(List<DHTTransportAlternativeContact> _server, byte[] _contacts, boolean _torrent_hash, DHTTrackerPluginAlt.LookupListener _no_seeds)
/*     */     {
/* 548 */       this.server = _server;
/* 549 */       this.torrent_hash = _torrent_hash;
/* 550 */       this.no_seeds = _no_seeds;
/* 551 */       this.listener = _listener;
/*     */       
/* 553 */       this.initial_contacts = _contacts;
/*     */       
/* 555 */       tryQuery();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void search(InetSocketAddress address)
/*     */       throws IOException
/*     */     {
/* 564 */       if (this.queried_nodes.contains(address))
/*     */       {
/* 566 */         return;
/*     */       }
/*     */       
/* 569 */       this.queried_nodes.add(address);
/*     */       
/* 571 */       Map<String, Object> map = new HashMap();
/*     */       
/* 573 */       map.put("q", "get_peers");
/* 574 */       map.put("y", "q");
/*     */       
/* 576 */       Map<String, Object> args = new HashMap();
/*     */       
/* 578 */       map.put("a", args);
/*     */       
/* 580 */       args.put("id", DHTTrackerPluginAlt.this.NID);
/*     */       
/* 582 */       args.put("info_hash", this.torrent_hash);
/*     */       
/* 584 */       args.put("noseed", new Long(this.no_seeds ? 1L : 0L));
/*     */       
/* 586 */       byte[] tid = DHTTrackerPluginAlt.this.send(this, this.server, address, map);
/*     */       
/* 588 */       this.query_count += 1;
/*     */       
/* 590 */       this.active_queries.put(tid, address);
/*     */     }
/*     */     
/*     */ 
/*     */     private void tryQuery()
/*     */     {
/* 596 */       if (this.listener.isComplete())
/*     */       {
/* 598 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 602 */         synchronized (this)
/*     */         {
/* 604 */           if ((this.failed) || (this.active_queries.size() >= 8))
/*     */           {
/* 606 */             return;
/*     */           }
/*     */           
/* 609 */           long now = SystemTime.getMonotonousTime();
/*     */           
/* 611 */           if (now - this.start_time > 90000L)
/*     */           {
/* 613 */             return;
/*     */           }
/*     */           
/* 616 */           if (this.found_peer_time > 0L)
/*     */           {
/* 618 */             if (this.found_peers.size() > 32)
/*     */             {
/* 620 */               setCompleted();
/*     */               
/* 622 */               return;
/*     */             }
/*     */             
/* 625 */             if (now - this.found_peer_time > 5000L)
/*     */             {
/* 627 */               setCompleted();
/*     */               
/* 629 */               return;
/*     */             }
/*     */           }
/*     */           try
/*     */           {
/*     */             byte[] limit_nid;
/*     */             byte[] limit_nid;
/* 636 */             if (this.heard_from.size() >= 10)
/*     */             {
/* 638 */               limit_nid = (byte[])this.heard_from.keySet().iterator().next();
/*     */             }
/*     */             else
/*     */             {
/* 642 */               limit_nid = null;
/*     */             }
/*     */             
/* 645 */             Iterator<Map.Entry<byte[], InetSocketAddress>> query_it = this.to_query.entrySet().iterator();
/*     */             
/* 647 */             while (query_it.hasNext())
/*     */             {
/* 649 */               Map.Entry<byte[], InetSocketAddress> entry = (Map.Entry)query_it.next();
/*     */               
/* 651 */               query_it.remove();
/*     */               
/* 653 */               byte[] nid = (byte[])entry.getKey();
/*     */               
/* 655 */               if ((limit_nid == null) || (this.comparator.compare(limit_nid, nid) > 0))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 664 */                 InetSocketAddress address = (InetSocketAddress)entry.getValue();
/*     */                 
/* 666 */                 search(address);
/*     */                 
/* 668 */                 if (this.active_queries.size() >= 8)
/*     */                 {
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
/* 708 */                   if (this.active_queries.size() == 0)
/*     */                   {
/* 710 */                     setCompleted();
/*     */                   }
/*     */                   return;
/*     */                 }
/*     */               }
/*     */             }
/* 674 */             if (this.heard_from.size() < 10)
/*     */             {
/* 676 */               Iterator<DHTTransportAlternativeContact> contact_it = this.initial_contacts.iterator();
/*     */               
/* 678 */               while (contact_it.hasNext())
/*     */               {
/* 680 */                 DHTTransportAlternativeContact contact = (DHTTransportAlternativeContact)contact_it.next();
/*     */                 
/* 682 */                 contact_it.remove();
/*     */                 
/* 684 */                 Map<String, Object> properties = contact.getProperties();
/*     */                 
/* 686 */                 byte[] _a = (byte[])properties.get("a");
/* 687 */                 Long _p = (Long)properties.get("p");
/*     */                 
/* 689 */                 if ((_a != null) && (_p != null)) {
/*     */                   try
/*     */                   {
/* 692 */                     InetSocketAddress address = new InetSocketAddress(InetAddress.getByAddress(_a), _p.intValue());
/*     */                     
/* 694 */                     search(address);
/*     */                     
/* 696 */                     if (this.active_queries.size() >= 8)
/*     */                     {
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
/* 708 */                       if (this.active_queries.size() == 0)
/*     */                       {
/* 710 */                         setCompleted();
/*     */                       }
/*     */                       return;
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 708 */             if (this.active_queries.size() == 0)
/*     */             {
/* 710 */               setCompleted(); }
/*     */           }
/*     */         }
/*     */       } catch (Throwable e) {
/* 714 */         e = 
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 723 */           e;
/* 716 */         synchronized (this)
/*     */         {
/* 718 */           setFailed();
/*     */         }
/*     */       }
/*     */       finally {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void handleTimeout(byte[] tid)
/*     */     {
/* 730 */       synchronized (this)
/*     */       {
/* 732 */         this.active_queries.remove(tid);
/*     */         
/* 734 */         this.timeout_count += 1;
/*     */       }
/*     */       
/* 737 */       tryQuery();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private void handleReply(InetSocketAddress from, byte[] tid, Map<String, Object> map)
/*     */       throws IOException
/*     */     {
/* 748 */       Map<String, Object> reply = (Map)map.get("r");
/*     */       
/* 750 */       synchronized (this)
/*     */       {
/* 752 */         this.active_queries.remove(tid);
/*     */         
/* 754 */         this.reply_count += 1;
/*     */         
/* 756 */         if (reply == null)
/*     */         {
/*     */ 
/*     */ 
/* 760 */           return;
/*     */         }
/*     */         
/* 763 */         this.heard_from.put((byte[])reply.get("id"), from);
/*     */         
/* 765 */         if (this.heard_from.size() > 10)
/*     */         {
/* 767 */           Iterator<byte[]> it = this.heard_from.keySet().iterator();
/*     */           
/* 769 */           it.next();
/*     */           
/* 771 */           it.remove();
/*     */         }
/*     */       }
/*     */       
/* 775 */       ArrayList<byte[]> values = (ArrayList)reply.get("values");
/*     */       
/* 777 */       if (values != null)
/*     */       {
/* 779 */         synchronized (this)
/*     */         {
/* 781 */           if (this.found_peer_time == 0L)
/*     */           {
/* 783 */             this.found_peer_time = SystemTime.getMonotonousTime();
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 788 */         for (byte[] value : values) {
/*     */           try
/*     */           {
/* 791 */             ByteBuffer bb = ByteBuffer.wrap(value);
/*     */             
/* 793 */             byte[] address = new byte[value.length - 2];
/*     */             
/* 795 */             bb.get(address);
/*     */             
/* 797 */             int port = bb.getShort() & 0xFFFF;
/*     */             
/* 799 */             InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(address), port);
/*     */             
/* 801 */             synchronized (this)
/*     */             {
/* 803 */               if (this.found_peers.contains(addr)) {
/*     */                 continue;
/*     */               }
/*     */               
/*     */ 
/* 808 */               this.found_peers.add(addr);
/*     */             }
/*     */             
/* 811 */             DHTTrackerPluginAlt.access$1408(DHTTrackerPluginAlt.this);
/*     */             
/* 813 */             this.listener.foundPeer(addr);
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 820 */       byte[] nodes = (byte[])reply.get("nodes");
/* 821 */       byte[] nodes6 = (byte[])reply.get("nodes6");
/*     */       
/* 823 */       if (nodes != null)
/*     */       {
/* 825 */         int entry_size = 26;
/*     */         
/* 827 */         for (int i = 0; i < nodes.length; i += entry_size)
/*     */         {
/* 829 */           ByteBuffer bb = ByteBuffer.wrap(nodes, i, entry_size);
/*     */           
/* 831 */           byte[] nid = new byte[20];
/*     */           
/* 833 */           bb.get(nid);
/*     */           
/* 835 */           byte[] address = new byte[4];
/*     */           
/* 837 */           bb.get(address);
/*     */           
/* 839 */           int port = bb.getShort() & 0xFFFF;
/*     */           try
/*     */           {
/* 842 */             InetSocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(address), port);
/*     */             
/* 844 */             synchronized (this)
/*     */             {
/* 846 */               if (!this.queried_nodes.contains(addr))
/*     */               {
/* 848 */                 this.to_query.put(nid, addr);
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       
/* 856 */       tryQuery();
/*     */     }
/*     */     
/*     */ 
/*     */     private void setCompleted()
/*     */     {
/* 862 */       if (!this.completed)
/*     */       {
/* 864 */         this.completed = true;
/*     */         
/* 866 */         this.listener.completed();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     private void setFailed()
/*     */     {
/* 873 */       this.failed = true;
/*     */       
/* 875 */       setCompleted();
/*     */     }
/*     */     
/*     */ 
/*     */     private void log()
/*     */     {
/* 881 */       System.out.println(ByteFormatter.encodeString(this.torrent_hash) + ": send=" + this.query_count + ", recv=" + this.reply_count + ", t/o=" + this.timeout_count + ", elapsed=" + (SystemTime.getMonotonousTime() - this.start_time) + ", toq=" + this.to_query.size() + ", found=" + this.found_peers.size());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 890 */       synchronized (this)
/*     */       {
/* 892 */         for (byte[] nid : this.heard_from.keySet())
/*     */         {
/* 894 */           System.out.println("    " + ByteFormatter.encodeString(nid));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected static abstract interface LookupListener
/*     */   {
/*     */     public abstract void foundPeer(InetSocketAddress paramInetSocketAddress);
/*     */     
/*     */     public abstract boolean isComplete();
/*     */     
/*     */     public abstract void completed();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/tracker/dht/DHTTrackerPluginAlt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */