/*     */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*     */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.Transport.ConnectListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.IncomingConnectionManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ProtocolDecoder;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportCryptoManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportCryptoManager.HandshakeListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelperFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class UDPConnectionManager
/*     */   implements NetworkGlueListener
/*     */ {
/*  52 */   private static final LogIDs LOGID = LogIDs.NET;
/*     */   
/*     */   private static final boolean LOOPBACK = false;
/*     */   private static final boolean FORCE_LOG = false;
/*  56 */   private static boolean LOG = false;
/*     */   private static int max_outbound_connections;
/*     */   
/*     */   static {
/*  60 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Logging Enable UDP Transport", "network.udp.max.connections.outstanding" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*  71 */         UDPConnectionManager.access$002(COConfigurationManager.getBooleanParameter("Logging Enable UDP Transport"));
/*     */         
/*  73 */         UDPConnectionManager.access$102(COConfigurationManager.getIntParameter("network.udp.max.connections.outstanding", 2048));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public static final int TIMER_TICK_MILLIS = 25;
/*     */   
/*     */   public static final int THREAD_LINGER_ON_IDLE_PERIOD = 30000;
/*     */   public static final int DEAD_KEY_RETENTION_PERIOD = 30000;
/*     */   public static final int STATS_TIME = 60000;
/*     */   public static final int STATS_TICKS = 2400;
/*  85 */   private final Map connection_sets = new HashMap();
/*  86 */   private final Map recently_dead_keys = new HashMap();
/*     */   
/*     */ 
/*     */   private int next_connection_id;
/*     */   
/*  91 */   final IncomingConnectionManager incoming_manager = IncomingConnectionManager.getSingleton();
/*     */   
/*     */   private final NetworkGlue network_glue;
/*     */   
/*     */   private UDPSelector selector;
/*     */   
/*     */   private ProtocolTimer protocol_timer;
/*     */   private long idle_start;
/*     */   private static final int BLOOM_RECREATE = 30000;
/*     */   private static final int BLOOM_INCREASE = 1000;
/* 101 */   private BloomFilter incoming_bloom = BloomFilterFactory.createAddRemove4Bit(1000);
/* 102 */   private long incoming_bloom_create_time = SystemTime.getCurrentTime();
/*     */   
/*     */ 
/*     */   private long last_incoming;
/*     */   
/*     */ 
/*     */   private int rate_limit_discard_packets;
/*     */   
/*     */   private int rate_limit_discard_bytes;
/*     */   
/*     */   private int setup_discard_packets;
/*     */   
/*     */   private int setup_discard_bytes;
/*     */   
/*     */   private volatile int outbound_connection_count;
/*     */   
/*     */   private boolean max_conn_exceeded_logged;
/*     */   
/*     */ 
/*     */   protected UDPConnectionManager()
/*     */   {
/* 123 */     this.network_glue = new NetworkGlueUDP(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connectOutbound(final UDPTransport udp_transport, final InetSocketAddress address, byte[][] shared_secrets, ByteBuffer initial_data, final Transport.ConnectListener listener)
/*     */   {
/* 135 */     UDPTransportHelper helper = null;
/*     */     try
/*     */     {
/* 138 */       if (address.isUnresolved())
/*     */       {
/* 140 */         listener.connectFailure(new UnknownHostException(address.getHostName()));
/*     */         
/* 142 */         return;
/*     */       }
/*     */       
/* 145 */       int time = listener.connectAttemptStarted(-1);
/*     */       
/* 147 */       if (time != -1)
/*     */       {
/* 149 */         Debug.out("UDP connect time override not supported");
/*     */       }
/*     */       
/* 152 */       helper = new UDPTransportHelper(this, address, udp_transport);
/*     */       
/* 154 */       final UDPTransportHelper f_helper = helper;
/*     */       
/* 156 */       synchronized (this)
/*     */       {
/* 158 */         this.outbound_connection_count += 1;
/*     */         
/* 160 */         if (this.outbound_connection_count >= max_outbound_connections)
/*     */         {
/* 162 */           if (!this.max_conn_exceeded_logged)
/*     */           {
/* 164 */             this.max_conn_exceeded_logged = true;
/*     */             
/* 166 */             Debug.out("UDPConnectionManager: max outbound connection limit reached (" + max_outbound_connections + ")");
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 172 */         TransportCryptoManager.getSingleton().manageCrypto(helper, shared_secrets, false, initial_data, new TransportCryptoManager.HandshakeListener()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void handshakeSuccess(ProtocolDecoder decoder, ByteBuffer remaining_initial_data)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 184 */             synchronized (UDPConnectionManager.this)
/*     */             {
/* 186 */               if (UDPConnectionManager.this.outbound_connection_count > 0)
/*     */               {
/* 188 */                 UDPConnectionManager.access$210(UDPConnectionManager.this);
/*     */               }
/*     */             }
/*     */             
/* 192 */             TransportHelperFilter filter = decoder.getFilter();
/*     */             try
/*     */             {
/* 195 */               udp_transport.setFilter(filter);
/*     */               
/* 197 */               if (udp_transport.isClosed())
/*     */               {
/* 199 */                 udp_transport.close("Already closed");
/*     */                 
/* 201 */                 listener.connectFailure(new Exception("Connection already closed"));
/*     */               }
/*     */               else
/*     */               {
/* 205 */                 if (Logger.isEnabled())
/*     */                 {
/* 207 */                   Logger.log(new LogEvent(UDPConnectionManager.LOGID, "Outgoing UDP stream to " + address + " established, type = " + filter.getName(false)));
/*     */                 }
/*     */                 
/* 210 */                 udp_transport.connectedOutbound();
/*     */                 
/* 212 */                 listener.connectSuccess(udp_transport, remaining_initial_data);
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 216 */               Debug.printStackTrace(e);
/*     */               
/* 218 */               udp_transport.close(Debug.getNestedExceptionMessageAndStack(e));
/*     */               
/* 220 */               listener.connectFailure(e);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void handshakeFailure(Throwable failure_msg)
/*     */           {
/* 228 */             synchronized (UDPConnectionManager.this)
/*     */             {
/* 230 */               if (UDPConnectionManager.this.outbound_connection_count > 0)
/*     */               {
/* 232 */                 UDPConnectionManager.access$210(UDPConnectionManager.this);
/*     */               }
/*     */             }
/*     */             
/* 236 */             f_helper.close(Debug.getNestedExceptionMessageAndStack(failure_msg));
/*     */             
/* 238 */             listener.connectFailure(failure_msg);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void gotSecret(byte[] session_secret)
/*     */           {
/* 245 */             f_helper.getConnection().setSecret(session_secret);
/*     */           }
/*     */           
/*     */ 
/*     */           public int getMaximumPlainHeaderLength()
/*     */           {
/* 251 */             throw new RuntimeException();
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public int matchPlainHeader(ByteBuffer buffer)
/*     */           {
/* 258 */             throw new RuntimeException();
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 264 */         synchronized (this)
/*     */         {
/* 266 */           if (this.outbound_connection_count > 0)
/*     */           {
/* 268 */             this.outbound_connection_count -= 1;
/*     */           }
/*     */         }
/*     */         
/* 272 */         throw e;
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 277 */       Debug.printStackTrace(e);
/*     */       
/* 279 */       if (helper != null)
/*     */       {
/* 281 */         helper.close(Debug.getNestedExceptionMessage(e));
/*     */       }
/*     */       
/* 284 */       listener.connectFailure(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxOutboundPermitted()
/*     */   {
/* 291 */     return Math.max(max_outbound_connections - this.outbound_connection_count, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UDPSelector checkThreadCreation()
/*     */   {
/* 299 */     if (this.selector == null)
/*     */     {
/* 301 */       if (Logger.isEnabled()) {
/* 302 */         Logger.log(new LogEvent(LOGID, "UDPConnectionManager: activating"));
/*     */       }
/*     */       
/* 305 */       this.idle_start = SystemTime.getMonotonousTime();
/*     */       
/* 307 */       this.selector = new UDPSelector(this);
/*     */       
/* 309 */       this.protocol_timer = new ProtocolTimer();
/*     */     }
/*     */     
/* 312 */     return this.selector;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void checkThreadDeath(boolean connections_running)
/*     */   {
/* 321 */     if (connections_running)
/*     */     {
/* 323 */       this.idle_start = 0L;
/*     */     }
/*     */     else
/*     */     {
/* 327 */       long now = SystemTime.getMonotonousTime();
/*     */       
/* 329 */       if (this.idle_start == 0L)
/*     */       {
/* 331 */         this.idle_start = now;
/*     */       }
/* 333 */       else if (now - this.idle_start > 30000L)
/*     */       {
/* 335 */         if (Logger.isEnabled()) {
/* 336 */           Logger.log(new LogEvent(LOGID, "UDPConnectionManager: deactivating"));
/*     */         }
/*     */         
/* 339 */         this.selector.destroy();
/*     */         
/* 341 */         this.selector = null;
/*     */         
/* 343 */         this.protocol_timer.destroy();
/*     */         
/* 345 */         this.protocol_timer = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void poll()
/*     */   {
/* 353 */     synchronized (this.connection_sets)
/*     */     {
/* 355 */       Iterator it = this.connection_sets.values().iterator();
/*     */       
/* 357 */       while (it.hasNext())
/*     */       {
/* 359 */         ((UDPConnectionSet)it.next()).poll();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void remove(UDPConnectionSet set, UDPConnection connection)
/*     */   {
/* 369 */     synchronized (this.connection_sets)
/*     */     {
/* 371 */       if (set.remove(connection))
/*     */       {
/* 373 */         String key = set.getKey();
/*     */         
/* 375 */         if (set.hasFailed())
/*     */         {
/* 377 */           if (this.connection_sets.remove(key) != null)
/*     */           {
/* 379 */             set.removed();
/*     */             
/* 381 */             this.recently_dead_keys.put(key, new Long(SystemTime.getCurrentTime()));
/*     */             
/* 383 */             if (Logger.isEnabled())
/*     */             {
/* 385 */               Logger.log(new LogEvent(LOGID, "Connection set " + key + " failed"));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void failed(UDPConnectionSet set)
/*     */   {
/* 397 */     synchronized (this.connection_sets)
/*     */     {
/* 399 */       String key = set.getKey();
/*     */       
/* 401 */       if (this.connection_sets.remove(key) != null)
/*     */       {
/* 403 */         set.removed();
/*     */         
/* 405 */         this.recently_dead_keys.put(key, new Long(SystemTime.getCurrentTime()));
/*     */         
/* 407 */         if (Logger.isEnabled())
/*     */         {
/* 409 */           Logger.log(new LogEvent(LOGID, "Connection set " + key + " failed"));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UDPConnection registerOutgoing(UDPTransportHelper helper)
/*     */     throws IOException
/*     */   {
/* 421 */     int local_port = UDPNetworkManager.getSingleton().getUDPListeningPortNumber();
/*     */     
/* 423 */     InetSocketAddress address = helper.getAddress();
/*     */     
/* 425 */     String key = local_port + ":" + address.getAddress().getHostAddress() + ":" + address.getPort();
/*     */     
/* 427 */     synchronized (this.connection_sets)
/*     */     {
/* 429 */       UDPSelector current_selector = checkThreadCreation();
/*     */       
/* 431 */       UDPConnectionSet connection_set = (UDPConnectionSet)this.connection_sets.get(key);
/*     */       
/* 433 */       if (connection_set == null)
/*     */       {
/* 435 */         timeoutDeadKeys();
/*     */         
/* 437 */         connection_set = new UDPConnectionSet(this, key, current_selector, local_port, address);
/*     */         
/* 439 */         if (Logger.isEnabled())
/*     */         {
/* 441 */           Logger.log(new LogEvent(LOGID, "Created new set - " + connection_set.getName() + ", outgoing"));
/*     */         }
/*     */         
/* 444 */         this.connection_sets.put(key, connection_set);
/*     */       }
/*     */       
/* 447 */       UDPConnection connection = new UDPConnection(connection_set, allocationConnectionID(), helper);
/*     */       
/* 449 */       connection_set.add(connection);
/*     */       
/* 451 */       return connection;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void receive(int local_port, InetSocketAddress remote_address, byte[] data, int data_length)
/*     */   {
/* 462 */     String key = local_port + ":" + remote_address.getAddress().getHostAddress() + ":" + remote_address.getPort();
/*     */     
/*     */     UDPConnectionSet connection_set;
/*     */     
/* 466 */     synchronized (this.connection_sets)
/*     */     {
/* 468 */       UDPSelector current_selector = checkThreadCreation();
/*     */       
/* 470 */       connection_set = (UDPConnectionSet)this.connection_sets.get(key);
/*     */       
/* 472 */       if (connection_set == null)
/*     */       {
/* 474 */         timeoutDeadKeys();
/*     */         
/*     */ 
/*     */ 
/* 478 */         if ((data_length >= UDPNetworkManager.MIN_INCOMING_INITIAL_PACKET_SIZE) && (data_length <= UDPNetworkManager.MAX_INCOMING_INITIAL_PACKET_SIZE))
/*     */         {
/*     */ 
/* 481 */           if (!rateLimitIncoming(remote_address))
/*     */           {
/* 483 */             this.rate_limit_discard_packets += 1;
/* 484 */             this.rate_limit_discard_bytes += data_length;
/*     */             
/* 486 */             return;
/*     */           }
/*     */           
/* 489 */           connection_set = new UDPConnectionSet(this, key, current_selector, local_port, remote_address);
/*     */           
/* 491 */           if (Logger.isEnabled())
/*     */           {
/* 493 */             Logger.log(new LogEvent(LOGID, "Created new set - " + connection_set.getName() + ", incoming"));
/*     */           }
/*     */           
/* 496 */           this.connection_sets.put(key, connection_set);
/*     */         }
/*     */         else
/*     */         {
/* 500 */           if (this.recently_dead_keys.get(key) == null) {}
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 507 */           this.setup_discard_packets += 1;
/* 508 */           this.setup_discard_bytes += data_length;
/*     */           
/* 510 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 518 */       connection_set.receive(data, data_length);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 522 */       connection_set.failed(e);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 526 */       Debug.printStackTrace(e);
/*     */       
/* 528 */       connection_set.failed(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean rateLimitIncoming(InetSocketAddress s_address)
/*     */   {
/* 536 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 538 */     byte[] address = s_address.getAddress().getAddress();
/*     */     
/*     */     long delay;
/*     */     
/* 542 */     synchronized (this)
/*     */     {
/* 544 */       int hit_count = this.incoming_bloom.add(address);
/*     */       
/*     */ 
/*     */ 
/* 548 */       if (this.incoming_bloom.getSize() / this.incoming_bloom.getEntryCount() < 10)
/*     */       {
/* 550 */         this.incoming_bloom = BloomFilterFactory.createAddRemove4Bit(this.incoming_bloom.getSize() + 1000);
/*     */         
/* 552 */         this.incoming_bloom_create_time = now;
/*     */         
/* 554 */         Logger.log(new LogEvent(LOGID, "UDP connnection bloom: size increased to " + this.incoming_bloom.getSize()));
/*     */       }
/* 556 */       else if ((now < this.incoming_bloom_create_time) || (now - this.incoming_bloom_create_time > 30000L))
/*     */       {
/* 558 */         this.incoming_bloom = BloomFilterFactory.createAddRemove4Bit(this.incoming_bloom.getSize());
/*     */         
/* 560 */         this.incoming_bloom_create_time = now;
/*     */       }
/*     */       
/* 563 */       if (hit_count >= 15)
/*     */       {
/* 565 */         Logger.log(new LogEvent(LOGID, "UDP incoming: too many recent connection attempts from " + s_address));
/*     */         
/* 567 */         return false;
/*     */       }
/*     */       
/* 570 */       long since_last = now - this.last_incoming;
/*     */       
/* 572 */       delay = 100L - since_last;
/*     */       
/* 574 */       this.last_incoming = now;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 579 */     if ((delay > 0L) && (delay < 100L)) {
/*     */       try
/*     */       {
/* 582 */         Thread.sleep(delay);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 588 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int send(int local_port, InetSocketAddress remote_address, byte[] data)
/*     */     throws IOException
/*     */   {
/* 599 */     return this.network_glue.send(local_port, remote_address, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void accept(final int local_port, final InetSocketAddress remote_address, final UDPConnection connection)
/*     */   {
/* 608 */     final UDPTransportHelper helper = new UDPTransportHelper(this, remote_address, connection);
/*     */     try
/*     */     {
/* 611 */       connection.setTransport(helper);
/*     */       
/* 613 */       TransportCryptoManager.getSingleton().manageCrypto(helper, (byte[][])null, true, null, new TransportCryptoManager.HandshakeListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void handshakeSuccess(ProtocolDecoder decoder, ByteBuffer remaining_initial_data)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 625 */           TransportHelperFilter filter = decoder.getFilter();
/*     */           
/* 627 */           ConnectionEndpoint co_ep = new ConnectionEndpoint(remote_address);
/*     */           
/* 629 */           ProtocolEndpointUDP pe_udp = (ProtocolEndpointUDP)ProtocolEndpointFactory.createEndpoint(2, co_ep, remote_address);
/*     */           
/* 631 */           UDPTransport transport = new UDPTransport(pe_udp, filter);
/*     */           
/* 633 */           helper.setTransport(transport);
/*     */           
/* 635 */           UDPConnectionManager.this.incoming_manager.addConnection(local_port, filter, transport);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void handshakeFailure(Throwable failure_msg)
/*     */         {
/* 642 */           if (Logger.isEnabled()) {
/* 643 */             Logger.log(new LogEvent(UDPConnectionManager.LOGID, "incoming crypto handshake failure: " + Debug.getNestedExceptionMessage(failure_msg)));
/*     */           }
/*     */           
/* 646 */           connection.close("handshake failure: " + Debug.getNestedExceptionMessage(failure_msg));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void gotSecret(byte[] session_secret)
/*     */         {
/* 653 */           helper.getConnection().setSecret(session_secret);
/*     */         }
/*     */         
/*     */ 
/*     */         public int getMaximumPlainHeaderLength()
/*     */         {
/* 659 */           return UDPConnectionManager.this.incoming_manager.getMaxMinMatchBufferSize();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public int matchPlainHeader(ByteBuffer buffer)
/*     */         {
/* 666 */           Object[] match_data = UDPConnectionManager.this.incoming_manager.checkForMatch(helper, local_port, buffer, true);
/*     */           
/* 668 */           if (match_data == null)
/*     */           {
/* 670 */             return 1;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 676 */           return 2;
/*     */         }
/*     */         
/*     */       });
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 683 */       Debug.printStackTrace(e);
/*     */       
/* 685 */       helper.close(Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized int allocationConnectionID()
/*     */   {
/* 692 */     int id = this.next_connection_id++;
/*     */     
/* 694 */     if (id < 0)
/*     */     {
/* 696 */       id = 0;
/* 697 */       this.next_connection_id = 1;
/*     */     }
/*     */     
/* 700 */     return id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void timeoutDeadKeys()
/*     */   {
/* 706 */     Iterator it = this.recently_dead_keys.values().iterator();
/*     */     
/* 708 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 710 */     while (it.hasNext())
/*     */     {
/* 712 */       long dead_time = ((Long)it.next()).longValue();
/*     */       
/* 714 */       if ((dead_time > now) || (now - dead_time > 30000L))
/*     */       {
/* 716 */         it.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class ProtocolTimer
/*     */   {
/*     */     private volatile boolean destroyed;
/*     */     
/*     */ 
/*     */     protected ProtocolTimer()
/*     */     {
/* 729 */       new AEThread2("UDPConnectionManager:timer", true)
/*     */       {
/*     */         private int tick_count;
/*     */         
/*     */ 
/*     */         public void run()
/*     */         {
/* 736 */           Thread.currentThread().setPriority(6);
/*     */           
/* 738 */           while (!UDPConnectionManager.ProtocolTimer.this.destroyed)
/*     */           {
/*     */             try {
/* 741 */               Thread.sleep(25L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */             
/*     */ 
/*     */ 
/* 747 */             this.tick_count += 1;
/*     */             
/* 749 */             if (this.tick_count % 2400 == 0)
/*     */             {
/* 751 */               UDPConnectionManager.this.logStats();
/*     */             }
/*     */             
/* 754 */             List failed_sets = null;
/*     */             
/* 756 */             synchronized (UDPConnectionManager.this.connection_sets)
/*     */             {
/* 758 */               int cs_size = UDPConnectionManager.this.connection_sets.size();
/*     */               
/* 760 */               UDPConnectionManager.this.checkThreadDeath(cs_size > 0);
/*     */               
/* 762 */               if (cs_size > 0)
/*     */               {
/* 764 */                 Iterator it = UDPConnectionManager.this.connection_sets.values().iterator();
/*     */                 
/* 766 */                 while (it.hasNext())
/*     */                 {
/* 768 */                   UDPConnectionSet set = (UDPConnectionSet)it.next();
/*     */                   try
/*     */                   {
/* 771 */                     set.timerTick();
/*     */                     
/* 773 */                     if (set.idleLimitExceeded())
/*     */                     {
/* 775 */                       if (Logger.isEnabled())
/*     */                       {
/* 777 */                         Logger.log(new LogEvent(UDPConnectionManager.LOGID, "Idle limit exceeded for " + set.getName() + ", removing"));
/*     */                       }
/*     */                       
/* 780 */                       UDPConnectionManager.this.recently_dead_keys.put(set.getKey(), new Long(SystemTime.getCurrentTime()));
/*     */                       
/* 782 */                       it.remove();
/*     */                       
/* 784 */                       set.removed();
/*     */                     }
/*     */                   }
/*     */                   catch (Throwable e) {
/* 788 */                     if (failed_sets == null)
/*     */                     {
/* 790 */                       failed_sets = new ArrayList();
/*     */                     }
/*     */                     
/* 793 */                     failed_sets.add(new Object[] { set, e });
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 799 */             if (failed_sets != null)
/*     */             {
/* 801 */               for (int i = 0; i < failed_sets.size(); i++)
/*     */               {
/* 803 */                 Object[] entry = (Object[])failed_sets.get(i);
/*     */                 
/* 805 */                 ((UDPConnectionSet)entry[0]).failed((Throwable)entry[1]);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 810 */           UDPConnectionManager.this.logStats();
/*     */         }
/*     */       }.start();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void destroy()
/*     */     {
/* 818 */       this.destroyed = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void logStats()
/*     */   {
/* 825 */     if (Logger.isEnabled())
/*     */     {
/* 827 */       long[] nw_stats = this.network_glue.getStats();
/*     */       
/* 829 */       String str = "UDPConnection stats: sent=" + nw_stats[0] + "/" + nw_stats[1] + ",received=" + nw_stats[2] + "/" + nw_stats[3];
/*     */       
/* 831 */       str = str + ", setup discards=" + this.setup_discard_packets + "/" + this.setup_discard_bytes;
/* 832 */       str = str + ", rate discards=" + this.rate_limit_discard_packets + "/" + this.rate_limit_discard_bytes;
/*     */       
/* 834 */       Logger.log(new LogEvent(LOGID, str));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean trace()
/*     */   {
/* 841 */     return LOG;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void trace(String str)
/*     */   {
/* 848 */     if (LOG)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 855 */       if (Logger.isEnabled())
/*     */       {
/* 857 */         Logger.log(new LogEvent(LOGID, str));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */