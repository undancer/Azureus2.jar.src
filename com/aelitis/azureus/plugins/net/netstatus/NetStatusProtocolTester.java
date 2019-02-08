/*     */ package com.aelitis.azureus.plugins.net.netstatus;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPluginContact;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabase;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseContact;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseKey;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferHandler;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseTransferType;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseValue;
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
/*     */ public class NetStatusProtocolTester
/*     */   implements DistributedDatabaseTransferHandler
/*     */ {
/*     */   private static final int REQUEST_HISTORY_MAX = 64;
/*     */   private static final int MAX_ACTIVE_TESTS = 3;
/*     */   private static final int MAX_TEST_TIME = 120000;
/*     */   private static final int TEST_TYPE_BT = 1;
/*     */   private static final int VERSION_INITIAL = 1;
/*     */   private static final int CURRENT_VERSION = 1;
/*     */   private static final int BT_MAX_SLAVES = 8;
/*     */   private NetStatusPlugin plugin;
/*     */   private PluginInterface plugin_interface;
/*     */   private DistributedDatabase ddb;
/*     */   private DHTPlugin dht_plugin;
/*  83 */   private testXferType transfer_type = new testXferType();
/*     */   
/*  85 */   private Map request_history = new LinkedHashMap(64, 0.75F, true)
/*     */   {
/*     */ 
/*     */ 
/*     */     protected boolean removeEldestEntry(Map.Entry eldest)
/*     */     {
/*     */ 
/*  92 */       return size() > 64;
/*     */     }
/*     */   };
/*     */   
/*  96 */   private List active_tests = new ArrayList();
/*     */   
/*  98 */   private TimerEventPeriodic timer_event = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetStatusProtocolTester(NetStatusPlugin _plugin, PluginInterface _plugin_interface)
/*     */   {
/* 105 */     this.plugin = _plugin;
/* 106 */     this.plugin_interface = _plugin_interface;
/*     */     try
/*     */     {
/* 109 */       PluginInterface dht_pi = this.plugin_interface.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */       
/* 111 */       if (dht_pi != null)
/*     */       {
/* 113 */         this.dht_plugin = ((DHTPlugin)dht_pi.getPlugin());
/*     */       }
/*     */       
/* 116 */       this.ddb = this.plugin_interface.getDistributedDatabase();
/*     */       
/* 118 */       if (this.ddb.isAvailable())
/*     */       {
/* 120 */         this.ddb.addTransferHandler(this.transfer_type, this);
/*     */         
/* 122 */         log("DDB transfer type registered");
/*     */       }
/*     */       else
/*     */       {
/* 126 */         log("DDB transfer type not registered, DDB unavailable");
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 130 */       log("DDB transfer type registration failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public NetStatusProtocolTesterBT runTest(NetStatusProtocolTesterListener listener)
/*     */   {
/* 138 */     return runTest("", listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetStatusProtocolTesterBT runTest(String test_address, final NetStatusProtocolTesterListener listener)
/*     */   {
/* 146 */     final NetStatusProtocolTesterBT bt_tester = new NetStatusProtocolTesterBT(this, true);
/*     */     
/* 148 */     bt_tester.addListener(listener);
/*     */     
/* 150 */     bt_tester.start();
/*     */     
/* 152 */     addToActive(bt_tester);
/*     */     try {
/*     */       DHT target_dht;
/* 155 */       if (test_address.length() == 0)
/*     */       {
/* 157 */         DHT[] dhts = this.dht_plugin.getDHTs();
/*     */         
/* 159 */         target_dht = null;
/*     */         
/* 161 */         int target_network = Constants.isCVSVersion() ? 1 : 0;
/*     */         
/* 163 */         for (int i = 0; i < dhts.length; i++)
/*     */         {
/* 165 */           if (dhts[i].getTransport().getNetwork() == target_network)
/*     */           {
/* 167 */             target_dht = dhts[i];
/*     */             
/* 169 */             break;
/*     */           }
/*     */         }
/*     */         
/* 173 */         if (target_dht == null)
/*     */         {
/* 175 */           listener.logError("Distributed database unavailable");
/*     */         }
/*     */         else
/*     */         {
/* 179 */           DHTTransportContact[] contacts = target_dht.getTransport().getReachableContacts();
/*     */           
/* 181 */           final List f_contacts = new ArrayList(Arrays.asList(contacts));
/*     */           
/* 183 */           final int[] ok = { 0 };
/*     */           
/* 185 */           final int num_threads = Math.min(8, contacts.length);
/*     */           
/* 187 */           listener.log("Searching " + contacts.length + " contacts for " + num_threads + " test targets", false);
/*     */           
/* 189 */           final AESemaphore sem = new AESemaphore("NetStatusProbe");
/*     */           
/* 191 */           for (int i = 0; i < num_threads; i++)
/*     */           {
/* 193 */             new AEThread2("NetStatusProbe", true)
/*     */             {
/*     */               public void run()
/*     */               {
/*     */                 try
/*     */                 {
/* 199 */                   while (!bt_tester.isDestroyed())
/*     */                   {
/* 201 */                     DHTTransportContact contact = null;
/*     */                     
/* 203 */                     synchronized (ok)
/*     */                     {
/* 205 */                       if ((ok[0] < num_threads) && (f_contacts.size() > 0))
/*     */                       {
/* 207 */                         contact = (DHTTransportContact)f_contacts.remove(0);
/*     */                       }
/*     */                     }
/*     */                     
/* 211 */                     if (contact == null) {
/*     */                       break;
/*     */                     }
/*     */                     
/*     */                     try
/*     */                     {
/* 217 */                       DistributedDatabaseContact ddb_contact = NetStatusProtocolTester.this.ddb.importContact(contact.getAddress());
/*     */                       
/* 219 */                       if (NetStatusProtocolTester.this.tryTest(bt_tester, ddb_contact))
/*     */                       {
/* 221 */                         synchronized (ok)
/*     */                         {
/* 223 */                           ok[0] += 1;
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                     catch (Throwable e) {
/* 228 */                       listener.logError("Contact import for " + contact.getName() + " failed", e);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */                 finally {
/* 233 */                   sem.release();
/*     */                 }
/*     */               }
/*     */             }.start();
/*     */           }
/*     */           
/* 239 */           for (int i = 0; i < num_threads; i++)
/*     */           {
/* 241 */             sem.reserve();
/*     */           }
/*     */           
/* 244 */           listener.log("Searching complete, " + ok[0] + " targets found", false);
/*     */         }
/*     */       }
/*     */       else {
/* 248 */         String[] bits = test_address.split(":");
/*     */         
/* 250 */         if (bits.length != 2)
/*     */         {
/* 252 */           log("Invalid address - use <host>:<port> ");
/*     */           
/* 254 */           return bt_tester;
/*     */         }
/*     */         
/* 257 */         InetSocketAddress address = new InetSocketAddress(bits[0].trim(), Integer.parseInt(bits[1].trim()));
/*     */         
/* 259 */         DistributedDatabaseContact contact = this.ddb.importContact(address);
/*     */         
/* 261 */         tryTest(bt_tester, contact);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 265 */       listener.logError("Test failed", e);
/*     */     }
/*     */     finally
/*     */     {
/* 269 */       bt_tester.addListener(new NetStatusProtocolTesterListener()
/*     */       {
/*     */         public void sessionAdded(NetStatusProtocolTesterBT.Session session) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void complete(NetStatusProtocolTesterBT tester)
/*     */         {
/* 282 */           NetStatusProtocolTester.this.removeFromActive(tester);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void log(String str, boolean detailed) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void logError(String str) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void logError(String str, Throwable e) {}
/* 305 */       });
/* 306 */       bt_tester.setOutboundConnectionsComplete();
/*     */       
/* 308 */       new DelayedEvent("NetStatus:killer", 10000L, new AERunnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*     */ 
/* 316 */           listener.log("Destroying tester", false);
/*     */           
/* 318 */           bt_tester.destroy();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 323 */     return bt_tester;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean tryTest(NetStatusProtocolTesterBT bt_tester, DistributedDatabaseContact contact)
/*     */   {
/* 331 */     boolean use_crypto = NetworkManager.getCryptoRequired(0);
/*     */     
/* 333 */     log("Trying test to " + contact.getName());
/*     */     
/* 335 */     Map request = new HashMap();
/*     */     
/* 337 */     request.put("v", new Long(1L));
/*     */     
/* 339 */     request.put("t", new Long(1L));
/*     */     
/* 341 */     request.put("h", bt_tester.getServerHash());
/*     */     
/* 343 */     request.put("c", new Long(use_crypto ? 1L : 0L));
/*     */     
/* 345 */     Map reply = sendRequest(contact, request);
/*     */     
/* 347 */     byte[] server_hash = reply == null ? null : (byte[])reply.get("h");
/*     */     
/* 349 */     if (server_hash != null)
/*     */     {
/* 351 */       log("    " + contact.getName() + " accepted test");
/*     */       
/* 353 */       bt_tester.testOutbound(adjustLoopback(contact.getAddress()), server_hash, use_crypto);
/*     */       
/* 355 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 359 */     log("    " + contact.getName() + " declined test");
/*     */     
/* 361 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InetSocketAddress adjustLoopback(InetSocketAddress address)
/*     */   {
/* 369 */     InetSocketAddress local = this.dht_plugin.getLocalAddress().getAddress();
/*     */     
/* 371 */     if (local.getAddress().getHostAddress().equals(address.getAddress().getHostAddress()))
/*     */     {
/* 373 */       return new InetSocketAddress("127.0.0.1", address.getPort());
/*     */     }
/*     */     
/*     */ 
/* 377 */     return address;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Map sendRequest(DistributedDatabaseContact contact, Map request)
/*     */   {
/*     */     try
/*     */     {
/* 387 */       log("Sending DDB request to " + contact.getName() + " - " + request);
/*     */       
/* 389 */       DistributedDatabaseKey key = this.ddb.createKey(BEncoder.encode(request));
/*     */       
/* 391 */       DistributedDatabaseValue value = contact.read(null, this.transfer_type, key, 10000L);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 398 */       if (value == null)
/*     */       {
/* 400 */         return null;
/*     */       }
/*     */       
/* 403 */       Map reply = BDecoder.decode((byte[])value.getValue(byte[].class));
/*     */       
/* 405 */       log("    received reply - " + reply);
/*     */       
/* 407 */       return reply;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 411 */       log("sendRequest failed", e);
/*     */     }
/* 413 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Map receiveRequest(InetSocketAddress originator, Map request)
/*     */   {
/* 422 */     Map reply = new HashMap();
/*     */     
/* 424 */     Long test_type = (Long)request.get("t");
/*     */     
/* 426 */     reply.put("v", new Long(1L));
/*     */     
/* 428 */     if (test_type != null)
/*     */     {
/* 430 */       if (test_type.intValue() == 1)
/*     */       {
/* 432 */         TCPNetworkManager tcp_man = TCPNetworkManager.getSingleton();
/*     */         
/* 434 */         InetSocketAddress adjusted_originator = adjustLoopback(originator);
/*     */         
/* 436 */         boolean test = adjusted_originator.getAddress().isLoopbackAddress();
/*     */         
/* 438 */         if ((test) || ((tcp_man.isTCPListenerEnabled()) && (tcp_man.getTCPListeningPortNumber() == this.ddb.getLocalContact().getAddress().getPort()) && (SystemTime.getCurrentTime() - tcp_man.getLastIncomingNonLocalConnectionTime() <= 86400000L)))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 443 */           byte[] their_hash = (byte[])request.get("h");
/*     */           
/* 445 */           if (their_hash != null)
/*     */           {
/*     */             NetStatusProtocolTesterBT bt_tester;
/*     */             
/* 449 */             synchronized (this.active_tests)
/*     */             {
/* 451 */               if (this.active_tests.size() > 3)
/*     */               {
/* 453 */                 log("Too many active tests");
/*     */                 
/* 455 */                 return reply;
/*     */               }
/*     */               
/*     */ 
/* 459 */               bt_tester = new NetStatusProtocolTesterBT(this, false);
/*     */               
/* 461 */               bt_tester.start();
/*     */               
/* 463 */               addToActive(bt_tester);
/*     */             }
/*     */             
/*     */ 
/* 467 */             Long l_crypto = (Long)request.get("c");
/*     */             
/* 469 */             boolean use_crypto = (l_crypto != null) && (l_crypto.longValue() == 1L);
/*     */             
/* 471 */             bt_tester.testOutbound(adjusted_originator, their_hash, use_crypto);
/*     */             
/* 473 */             reply.put("h", bt_tester.getServerHash());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 479 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addToActive(NetStatusProtocolTesterBT tester)
/*     */   {
/* 486 */     synchronized (this.active_tests)
/*     */     {
/* 488 */       this.active_tests.add(tester);
/*     */       
/* 490 */       if (this.timer_event == null)
/*     */       {
/* 492 */         this.timer_event = SimpleTimer.addPeriodicEvent("NetStatusProtocolTester:timer", 30000L, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 502 */             long now = SystemTime.getCurrentTime();
/*     */             
/* 504 */             List to_remove = new ArrayList();
/*     */             
/* 506 */             synchronized (NetStatusProtocolTester.this.active_tests)
/*     */             {
/* 508 */               for (int i = 0; i < NetStatusProtocolTester.this.active_tests.size(); i++)
/*     */               {
/* 510 */                 NetStatusProtocolTesterBT tester = (NetStatusProtocolTesterBT)NetStatusProtocolTester.this.active_tests.get(i);
/*     */                 
/* 512 */                 long start = tester.getStartTime(now);
/*     */                 
/* 514 */                 if (now - start > 120000L)
/*     */                 {
/* 516 */                   to_remove.add(tester);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 521 */             for (int i = 0; i < to_remove.size(); i++)
/*     */             {
/* 523 */               NetStatusProtocolTester.this.removeFromActive((NetStatusProtocolTesterBT)to_remove.get(i));
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeFromActive(NetStatusProtocolTesterBT tester)
/*     */   {
/* 535 */     tester.destroy();
/*     */     
/* 537 */     synchronized (this.active_tests)
/*     */     {
/* 539 */       this.active_tests.remove(tester);
/*     */       
/* 541 */       if (this.active_tests.size() == 0)
/*     */       {
/* 543 */         if (this.timer_event != null)
/*     */         {
/* 545 */           this.timer_event.cancel();
/*     */           
/* 547 */           this.timer_event = null;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributedDatabaseValue read(DistributedDatabaseContact contact, DistributedDatabaseTransferType type, DistributedDatabaseKey ddb_key)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 561 */     Object o_key = ddb_key.getKey();
/*     */     try
/*     */     {
/* 564 */       byte[] key = (byte[])o_key;
/*     */       
/* 566 */       HashWrapper hw = new HashWrapper(key);
/*     */       
/* 568 */       synchronized (this.request_history)
/*     */       {
/* 570 */         if (this.request_history.containsKey(hw))
/*     */         {
/* 572 */           return null;
/*     */         }
/*     */         
/* 575 */         this.request_history.put(hw, "");
/*     */       }
/*     */       
/* 578 */       Map request = BDecoder.decode((byte[])o_key);
/*     */       
/* 580 */       log("Received DDB request from " + contact.getName() + " - " + request);
/*     */       
/* 582 */       Map result = receiveRequest(contact.getAddress(), request);
/*     */       
/* 584 */       return this.ddb.createValue(BEncoder.encode(result));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 588 */       log("DDB read failed", e);
/*     */     }
/* 590 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DistributedDatabaseValue write(DistributedDatabaseContact contact, DistributedDatabaseTransferType type, DistributedDatabaseKey key, DistributedDatabaseValue value)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 603 */     throw new DistributedDatabaseException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void log(String str)
/*     */   {
/* 611 */     this.plugin.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void log(String str, Throwable e)
/*     */   {
/* 619 */     this.plugin.log(str, e);
/*     */   }
/*     */   
/*     */   protected static class testXferType
/*     */     implements DistributedDatabaseTransferType
/*     */   {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/netstatus/NetStatusProtocolTester.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */