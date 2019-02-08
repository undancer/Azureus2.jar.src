/*     */ package com.aelitis.azureus.plugins.net.netstatus;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection.ConnectionListener;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*     */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManager;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
/*     */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistrationAdapter;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTBitfield;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHandshake;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHave;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageEncoder;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ public class NetStatusProtocolTesterBT
/*     */ {
/*  60 */   private static Random random = RandomUtils.SECURE_RANDOM;
/*     */   
/*     */   private NetStatusProtocolTester tester;
/*     */   
/*     */   private boolean test_initiator;
/*  65 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */   private byte[] my_hash;
/*     */   
/*     */   private byte[] peer_id;
/*     */   
/*     */   private PeerManagerRegistration pm_reg;
/*     */   
/*  73 */   private long start_time = SystemTime.getCurrentTime();
/*     */   
/*  75 */   private List sessions = new ArrayList();
/*     */   
/*     */   private int session_id_next;
/*  78 */   private int outbound_attempts = 0;
/*  79 */   private int outbound_connects = 0;
/*  80 */   private int inbound_connects = 0;
/*     */   
/*     */   private boolean outbound_connections_complete;
/*  83 */   private AESemaphore completion_sem = new AESemaphore("Completion");
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean destroyed;
/*     */   
/*     */ 
/*     */ 
/*     */   protected NetStatusProtocolTesterBT(NetStatusProtocolTester _tester, boolean _test_initiator)
/*     */   {
/*  93 */     this.tester = _tester;
/*  94 */     this.test_initiator = _test_initiator;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void start()
/*     */   {
/* 100 */     this.my_hash = new byte[20];
/*     */     
/* 102 */     random.nextBytes(this.my_hash);
/*     */     
/* 104 */     this.peer_id = new byte[20];
/*     */     
/* 106 */     random.nextBytes(this.peer_id);
/*     */     
/*     */ 
/* 109 */     this.pm_reg = PeerManager.getSingleton().registerLegacyManager(new HashWrapper(this.my_hash), new PeerManagerRegistrationAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public byte[][] getSecrets()
/*     */       {
/*     */ 
/* 116 */         return new byte[][] { NetStatusProtocolTesterBT.this.my_hash };
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean manualRoute(NetworkConnection connection)
/*     */       {
/* 123 */         NetStatusProtocolTesterBT.this.log("Got incoming connection from " + connection.getEndpoint().getNotionalAddress());
/*     */         
/* 125 */         new NetStatusProtocolTesterBT.Session(NetStatusProtocolTesterBT.this, connection, null);
/*     */         
/* 127 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean isPeerSourceEnabled(String peer_source)
/*     */       {
/* 134 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean activateRequest(InetSocketAddress remote_address)
/*     */       {
/* 141 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void deactivateRequest(InetSocketAddress remote_address) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public String getDescription()
/*     */       {
/* 153 */         return "NetStatusPlugin - router";
/*     */       }
/*     */       
/*     */ 
/* 157 */     });
/* 158 */     log("Incoming routing established for " + ByteFormatter.encodeString(this.my_hash));
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte[] getServerHash()
/*     */   {
/* 164 */     return this.my_hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getStartTime(long now)
/*     */   {
/* 171 */     if (now < this.start_time)
/*     */     {
/* 173 */       this.start_time = now;
/*     */     }
/*     */     
/* 176 */     return this.start_time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void testOutbound(InetSocketAddress address, byte[] their_hash, boolean use_crypto)
/*     */   {
/* 187 */     if (NetworkManager.getCryptoRequired(0))
/*     */     {
/* 189 */       use_crypto = true;
/*     */     }
/*     */     
/* 192 */     log("Making outbound connection to " + address);
/*     */     
/* 194 */     synchronized (this)
/*     */     {
/* 196 */       this.outbound_attempts += 1;
/*     */     }
/*     */     
/* 199 */     boolean allow_fallback = false;
/*     */     
/* 201 */     ProtocolEndpoint pe = ProtocolEndpointFactory.createEndpoint(1, address);
/*     */     
/* 203 */     ConnectionEndpoint connection_endpoint = new ConnectionEndpoint(address);
/*     */     
/* 205 */     connection_endpoint.addProtocol(pe);
/*     */     
/* 207 */     NetworkConnection connection = NetworkManager.getSingleton().createConnection(connection_endpoint, new BTMessageEncoder(), new BTMessageDecoder(), use_crypto, allow_fallback, new byte[][] { their_hash });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */     new Session(connection, their_hash);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 222 */     List to_close = new ArrayList();
/*     */     
/* 224 */     synchronized (this.sessions)
/*     */     {
/* 226 */       if (this.destroyed)
/*     */       {
/* 228 */         return;
/*     */       }
/*     */       
/* 231 */       this.destroyed = true;
/*     */       
/* 233 */       to_close.addAll(this.sessions);
/*     */       
/* 235 */       this.sessions.clear();
/*     */     }
/*     */     
/* 238 */     for (int i = 0; i < to_close.size(); i++)
/*     */     {
/* 240 */       Session session = (Session)to_close.get(i);
/*     */       
/* 242 */       session.close();
/*     */     }
/*     */     
/* 245 */     this.pm_reg.unregister();
/*     */     
/* 247 */     checkCompletion();
/*     */     
/* 249 */     log("Incoming routing destroyed for " + ByteFormatter.encodeString(this.my_hash));
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isDestroyed()
/*     */   {
/* 255 */     return this.destroyed;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setOutboundConnectionsComplete()
/*     */   {
/* 261 */     synchronized (this.sessions)
/*     */     {
/* 263 */       this.outbound_connections_complete = true;
/*     */     }
/*     */     
/* 266 */     checkCompletion();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void checkCompletion()
/*     */   {
/* 272 */     boolean inform = false;
/*     */     
/* 274 */     synchronized (this.sessions)
/*     */     {
/* 276 */       if (this.completion_sem.isReleasedForever())
/*     */       {
/* 278 */         return;
/*     */       }
/*     */       
/* 281 */       if ((this.destroyed) || ((this.outbound_connections_complete) && (this.sessions.size() == 0)))
/*     */       {
/*     */ 
/* 284 */         inform = true;
/*     */         
/* 286 */         this.completion_sem.releaseForever();
/*     */       }
/*     */     }
/*     */     
/* 290 */     if (inform)
/*     */     {
/* 292 */       Iterator it = this.listeners.iterator();
/*     */       
/* 294 */       while (it.hasNext()) {
/*     */         try
/*     */         {
/* 297 */           ((NetStatusProtocolTesterListener)it.next()).complete(this);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 301 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean waitForCompletion(long max_millis)
/*     */   {
/* 311 */     if (max_millis == 0L)
/*     */     {
/* 313 */       this.completion_sem.reserve();
/*     */       
/* 315 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 319 */     return this.completion_sem.reserve(max_millis);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(NetStatusProtocolTesterListener l)
/*     */   {
/* 327 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(NetStatusProtocolTesterListener l)
/*     */   {
/* 334 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getOutboundConnects()
/*     */   {
/* 340 */     return this.outbound_connects;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInboundConnects()
/*     */   {
/* 346 */     return this.inbound_connects;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatus()
/*     */   {
/* 352 */     return "sessions=" + this.sessions.size() + ", out_attempts=" + this.outbound_attempts + ", out_connect=" + this.outbound_connects + ", in_connect=" + this.inbound_connects;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 362 */     log(str, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, boolean detailed)
/*     */   {
/* 370 */     Iterator it = this.listeners.iterator();
/*     */     
/* 372 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 375 */         ((NetStatusProtocolTesterListener)it.next()).log(str, detailed);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 379 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 383 */     this.tester.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void logError(String str)
/*     */   {
/* 390 */     Iterator it = this.listeners.iterator();
/*     */     
/* 392 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 395 */         ((NetStatusProtocolTesterListener)it.next()).logError(str);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 399 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 403 */     this.tester.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void logError(String str, Throwable e)
/*     */   {
/* 411 */     Iterator it = this.listeners.iterator();
/*     */     
/* 413 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 416 */         ((NetStatusProtocolTesterListener)it.next()).logError(str, e);
/*     */       }
/*     */       catch (Throwable f)
/*     */       {
/* 420 */         Debug.printStackTrace(f);
/*     */       }
/*     */     }
/*     */     
/* 424 */     this.tester.log(str, e);
/*     */   }
/*     */   
/*     */ 
/*     */   public class Session
/*     */   {
/*     */     private NetworkConnection connection;
/*     */     
/*     */     private int session_id;
/*     */     
/*     */     private boolean initiator;
/*     */     
/*     */     private byte[] info_hash;
/*     */     private boolean handshake_sent;
/*     */     private boolean handshake_received;
/*     */     private boolean bitfield_sent;
/*     */     private boolean bitfield_received;
/*     */     private int num_pieces;
/*     */     private boolean is_seed;
/* 443 */     private Set missing_pieces = new HashSet();
/*     */     
/*     */     private boolean connected;
/*     */     
/*     */     private boolean closing;
/*     */     
/*     */     private boolean closed;
/*     */     
/*     */ 
/*     */     protected Session(NetworkConnection _connection, byte[] _info_hash)
/*     */     {
/* 454 */       this.connection = _connection;
/* 455 */       this.info_hash = _info_hash;
/*     */       
/* 457 */       this.initiator = (this.info_hash != null);
/*     */       
/* 459 */       synchronized (NetStatusProtocolTesterBT.this.sessions)
/*     */       {
/* 461 */         NetStatusProtocolTesterBT.access$208(NetStatusProtocolTesterBT.this);
/*     */         
/* 463 */         this.session_id = NetStatusProtocolTesterBT.this.session_id_next;
/*     */         
/* 465 */         if (NetStatusProtocolTesterBT.this.destroyed)
/*     */         {
/* 467 */           log("Already destroyed");
/*     */           
/* 469 */           close();
/*     */           
/* 471 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 478 */         if ((!NetStatusProtocolTesterBT.this.test_initiator) && (!this.initiator))
/*     */         {
/* 480 */           int responder_sessions = 0;
/*     */           
/* 482 */           for (int i = 0; i < NetStatusProtocolTesterBT.this.sessions.size(); i++)
/*     */           {
/* 484 */             Session existing_session = (Session)NetStatusProtocolTesterBT.this.sessions.get(i);
/*     */             
/* 486 */             if (!existing_session.isInitiator())
/*     */             {
/* 488 */               responder_sessions++;
/*     */             }
/*     */           }
/*     */           
/* 492 */           if (responder_sessions >= 2)
/*     */           {
/* 494 */             log("Too many responder sessions");
/*     */             
/* 496 */             close();
/*     */             
/* 498 */             return;
/*     */           }
/*     */         }
/*     */         
/* 502 */         NetStatusProtocolTesterBT.this.sessions.add(this);
/*     */         
/* 504 */         this.is_seed = ((this.initiator) && (NetStatusProtocolTesterBT.this.sessions.size() % 2 == 0));
/*     */       }
/*     */       
/*     */ 
/* 508 */       Iterator it = NetStatusProtocolTesterBT.this.listeners.iterator();
/*     */       
/* 510 */       while (it.hasNext()) {
/*     */         try
/*     */         {
/* 513 */           ((NetStatusProtocolTesterListener)it.next()).sessionAdded(this);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 517 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 521 */       this.connection.connect(3, new NetworkConnection.ConnectionListener()
/*     */       {
/*     */ 
/*     */ 
/* 525 */         final String type = NetStatusProtocolTesterBT.Session.this.initiator ? "Outbound" : "Inbound";
/*     */         
/*     */ 
/*     */ 
/*     */         public int connectStarted(int default_connect_timeout)
/*     */         {
/* 531 */           NetStatusProtocolTesterBT.Session.this.log(this.type + " connect start", true);
/*     */           
/* 533 */           return default_connect_timeout;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public final void connectSuccess(ByteBuffer remaining_initial_data)
/*     */         {
/* 540 */           NetStatusProtocolTesterBT.Session.this.log(this.type + " connect success", true);
/*     */           
/* 542 */           NetStatusProtocolTesterBT.Session.this.connected = true;
/*     */           
/* 544 */           synchronized (NetStatusProtocolTesterBT.this)
/*     */           {
/* 546 */             if (NetStatusProtocolTesterBT.Session.this.initiator)
/*     */             {
/* 548 */               NetStatusProtocolTesterBT.access$808(NetStatusProtocolTesterBT.this);
/*     */             }
/*     */             else
/*     */             {
/* 552 */               NetStatusProtocolTesterBT.access$908(NetStatusProtocolTesterBT.this);
/*     */             }
/*     */           }
/*     */           
/* 556 */           NetStatusProtocolTesterBT.Session.this.connected();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public final void connectFailure(Throwable e)
/*     */         {
/* 563 */           if (!NetStatusProtocolTesterBT.Session.this.closing)
/*     */           {
/* 565 */             NetStatusProtocolTesterBT.Session.this.logError(this.type + " connection fail", e);
/*     */           }
/*     */           
/* 568 */           NetStatusProtocolTesterBT.Session.this.close();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public final void exceptionThrown(Throwable e)
/*     */         {
/* 575 */           if (!NetStatusProtocolTesterBT.Session.this.closing)
/*     */           {
/* 577 */             NetStatusProtocolTesterBT.Session.this.logError(this.type + " connection fail", e);
/*     */           }
/*     */           
/* 580 */           NetStatusProtocolTesterBT.Session.this.close();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public Object getConnectionProperty(String property_name)
/*     */         {
/* 587 */           return null;
/*     */         }
/*     */         
/*     */ 
/*     */         public String getDescription()
/*     */         {
/* 593 */           return "NetStatusPlugin - " + this.type;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isInitiator()
/*     */     {
/* 601 */       return this.initiator;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isConnected()
/*     */     {
/* 607 */       return this.connected;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isSeed()
/*     */     {
/* 613 */       return this.is_seed;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isOK()
/*     */     {
/* 619 */       return this.bitfield_received;
/*     */     }
/*     */     
/*     */ 
/*     */     protected void connected()
/*     */     {
/* 625 */       this.connection.getIncomingMessageQueue().registerQueueListener(new IncomingMessageQueue.MessageQueueListener()
/*     */       {
/*     */ 
/*     */         public boolean messageReceived(Message message)
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 634 */             String message_id = message.getID();
/*     */             
/* 636 */             NetStatusProtocolTesterBT.Session.this.log("Incoming message received: " + message.getID(), true);
/*     */             BTHave have;
/* 638 */             if (message_id.equals("BT_HANDSHAKE"))
/*     */             {
/* 640 */               NetStatusProtocolTesterBT.Session.this.handshake_received = true;
/*     */               
/* 642 */               BTHandshake handshake = (BTHandshake)message;
/*     */               
/* 644 */               NetStatusProtocolTesterBT.Session.this.info_hash = handshake.getDataHash();
/*     */               
/* 646 */               NetStatusProtocolTesterBT.Session.this.num_pieces = (500 + (NetStatusProtocolTesterBT.Session.this.info_hash[0] & 0xFF));
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 651 */               if (NetStatusProtocolTesterBT.Session.this.num_pieces % 8 == 0)
/*     */               {
/* 653 */                 NetStatusProtocolTesterBT.Session.access$1310(NetStatusProtocolTesterBT.Session.this);
/*     */               }
/*     */               
/* 656 */               if (!NetStatusProtocolTesterBT.Session.this.is_seed)
/*     */               {
/* 658 */                 int missing = NetStatusProtocolTesterBT.random.nextInt(NetStatusProtocolTesterBT.Session.this.num_pieces / 2) + 5;
/*     */                 
/* 660 */                 for (int i = 0; i < missing; i++)
/*     */                 {
/* 662 */                   NetStatusProtocolTesterBT.Session.this.missing_pieces.add(new Integer(NetStatusProtocolTesterBT.random.nextInt(NetStatusProtocolTesterBT.Session.this.num_pieces)));
/*     */                 }
/*     */               }
/*     */               
/* 666 */               NetStatusProtocolTesterBT.Session.this.sendHandshake();
/*     */               
/* 668 */               NetStatusProtocolTesterBT.Session.this.sendBitfield();
/*     */               
/* 670 */               NetStatusProtocolTesterBT.Session.this.connection.getIncomingMessageQueue().getDecoder().resumeDecoding();
/*     */             }
/* 672 */             else if (message_id.equals("BT_BITFIELD"))
/*     */             {
/* 674 */               NetStatusProtocolTesterBT.Session.this.bitfield_received = true;
/*     */               
/* 676 */               BTBitfield bitfield = (BTBitfield)message;
/*     */               
/* 678 */               ByteBuffer bb = bitfield.getBitfield().getBuffer((byte)0);
/*     */               
/* 680 */               byte[] contents = new byte[bb.remaining()];
/*     */               
/* 682 */               bb.get(contents);
/*     */             }
/* 684 */             else if (message_id.equals("BT_HAVE"))
/*     */             {
/* 686 */               have = (BTHave)message;
/*     */               
/* 688 */               if (have.getPieceNumber() == NetStatusProtocolTesterBT.Session.this.num_pieces)
/*     */               {
/* 690 */                 synchronized (NetStatusProtocolTesterBT.this.sessions)
/*     */                 {
/* 692 */                   NetStatusProtocolTesterBT.Session.this.closing = true;
/*     */                 }
/*     */                 
/* 695 */                 NetStatusProtocolTesterBT.Session.this.close();
/*     */               }
/*     */             }
/*     */             
/* 699 */             return 1;
/*     */           }
/*     */           finally
/*     */           {
/* 703 */             message.destroy();
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void protocolBytesReceived(int byte_count) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void dataBytesReceived(int byte_count) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public boolean isPriority()
/*     */         {
/* 724 */           return true;
/*     */         }
/*     */         
/* 727 */       });
/* 728 */       this.connection.getOutgoingMessageQueue().registerQueueListener(new OutgoingMessageQueue.MessageQueueListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public final boolean messageAdded(Message message)
/*     */         {
/*     */ 
/* 735 */           return true;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void messageQueued(Message message) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void messageRemoved(Message message) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void messageSent(Message message)
/*     */         {
/* 755 */           NetStatusProtocolTesterBT.Session.this.log("Outgoing message sent: " + message.getID(), true);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void protocolBytesSent(int byte_count) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public final void dataBytesSent(int byte_count) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void flush() {}
/* 775 */       });
/* 776 */       this.connection.startMessageProcessing();
/*     */       
/* 778 */       if (this.initiator)
/*     */       {
/* 780 */         sendHandshake();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected void sendHandshake()
/*     */     {
/* 787 */       if (!this.handshake_sent)
/*     */       {
/* 789 */         this.handshake_sent = true;
/*     */         
/* 791 */         this.connection.getOutgoingMessageQueue().addMessage(new BTHandshake(this.info_hash, NetStatusProtocolTesterBT.this.peer_id, 0, (byte)1), false);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void sendHave(int piece_number)
/*     */     {
/* 801 */       BTHave message = new BTHave(piece_number, (byte)1);
/*     */       
/* 803 */       OutgoingMessageQueue out_q = this.connection.getOutgoingMessageQueue();
/*     */       
/* 805 */       out_q.addMessage(message, false);
/*     */       
/* 807 */       out_q.flush();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void sendBitfield()
/*     */     {
/* 813 */       if (!this.bitfield_sent)
/*     */       {
/* 815 */         this.bitfield_sent = true;
/*     */         
/* 817 */         byte[] bits = new byte[(this.num_pieces + 7) / 8];
/*     */         
/* 819 */         int pos = 0;
/*     */         
/* 821 */         int i = 0;
/* 822 */         int bToSend = 0;
/* 824 */         for (; 
/* 824 */             i < this.num_pieces; i++)
/*     */         {
/* 826 */           if (i % 8 == 0)
/*     */           {
/* 828 */             bToSend = 0;
/*     */           }
/*     */           
/* 831 */           bToSend <<= 1;
/*     */           
/* 833 */           boolean has_piece = !this.missing_pieces.contains(new Integer(i));
/*     */           
/* 835 */           if (has_piece)
/*     */           {
/* 837 */             bToSend++;
/*     */           }
/*     */           
/* 840 */           if (i % 8 == 7)
/*     */           {
/* 842 */             bits[(pos++)] = ((byte)bToSend);
/*     */           }
/*     */         }
/*     */         
/* 846 */         if (i % 8 != 0)
/*     */         {
/* 848 */           bToSend <<= 8 - i % 8;
/*     */           
/* 850 */           bits[(pos++)] = ((byte)bToSend);
/*     */         }
/*     */         
/* 853 */         DirectByteBuffer buffer = new DirectByteBuffer(ByteBuffer.wrap(bits));
/*     */         
/* 855 */         this.connection.getOutgoingMessageQueue().addMessage(new BTBitfield(buffer, (byte)1), false);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void close()
/*     */     {
/* 864 */       synchronized (NetStatusProtocolTesterBT.this.sessions)
/*     */       {
/* 866 */         NetStatusProtocolTesterBT.this.sessions.remove(this);
/*     */         
/* 868 */         if (!this.closing)
/*     */         {
/* 870 */           this.closing = true;
/*     */         }
/*     */         else
/*     */         {
/* 874 */           this.closed = true;
/*     */         }
/*     */       }
/*     */       
/* 878 */       if (this.closed)
/*     */       {
/* 880 */         log("Closing connection", true);
/*     */         
/* 882 */         this.connection.close(null);
/*     */       }
/*     */       else
/*     */       {
/* 886 */         sendHave(this.num_pieces);
/*     */         
/* 888 */         new DelayedEvent("NetStatus:delayClose", 5000L, new AERunnable()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/*     */ 
/* 896 */             if (!NetStatusProtocolTesterBT.Session.this.closed)
/*     */             {
/* 898 */               NetStatusProtocolTesterBT.Session.this.close();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 904 */       NetStatusProtocolTesterBT.this.checkCompletion();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getProtocolString()
/*     */     {
/* 910 */       String str = "";
/*     */       
/* 912 */       if (this.connected)
/*     */       {
/* 914 */         str = "connected";
/*     */         
/* 916 */         str = str + addSent("hand", this.handshake_sent);
/* 917 */         str = str + addRecv("hand", this.handshake_received);
/* 918 */         str = str + addSent("bitf", this.bitfield_sent);
/* 919 */         str = str + addRecv("bitf", this.bitfield_received);
/*     */       }
/*     */       else
/*     */       {
/* 923 */         str = "not connected";
/*     */       }
/*     */       
/* 926 */       return str;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected String addSent(String str, boolean ok)
/*     */     {
/* 934 */       if (ok)
/*     */       {
/* 936 */         return ", " + str + " sent";
/*     */       }
/*     */       
/* 939 */       return ", " + str + " !sent";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected String addRecv(String str, boolean ok)
/*     */     {
/* 948 */       if (ok)
/*     */       {
/* 950 */         return ", " + str + " recv";
/*     */       }
/*     */       
/* 953 */       return ", " + str + " !recv";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected String getLogPrefix()
/*     */     {
/* 960 */       return "(" + (this.initiator ? "L" : "R") + (this.is_seed ? "S" : "L") + " " + this.session_id + ") ";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void log(String str)
/*     */     {
/* 967 */       NetStatusProtocolTesterBT.this.log(getLogPrefix() + str);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void log(String str, boolean is_detailed)
/*     */     {
/* 975 */       NetStatusProtocolTesterBT.this.log(getLogPrefix() + str, is_detailed);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void logError(String str)
/*     */     {
/* 982 */       NetStatusProtocolTesterBT.this.logError(getLogPrefix() + str);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected void logError(String str, Throwable e)
/*     */     {
/* 990 */       NetStatusProtocolTesterBT.this.logError(getLogPrefix() + str, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/netstatus/NetStatusProtocolTesterBT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */