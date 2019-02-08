/*      */ package com.aelitis.azureus.core.networkmanager.impl.udp;
/*      */ 
/*      */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
/*      */ import java.io.IOException;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*      */ import org.gudy.bouncycastle.crypto.engines.RC4Engine;
/*      */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UDPConnectionSet
/*      */ {
/*   45 */   private static final LogIDs LOGID = LogIDs.NET;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final boolean DEBUG_SEQUENCES = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*   56 */   private static final byte[] KEYA_IV = "UDPDriverKeyA".getBytes();
/*   57 */   private static final byte[] KEYB_IV = "UDPDriverKeyB".getBytes();
/*   58 */   private static final byte[] KEYC_IV = "UDPDriverKeyC".getBytes();
/*   59 */   private static final byte[] KEYD_IV = "UDPDriverKeyD".getBytes();
/*      */   
/*      */   private static final int MIN_MSS = 256;
/*      */   
/*      */   private static final int MAX_HEADER = 128;
/*      */   
/*      */   public static final int MIN_WRITE_PAYLOAD = 128;
/*      */   
/*      */   public static final int MAX_BUFFERED_PAYLOAD = 512;
/*      */   
/*      */   private final UDPConnectionManager manager;
/*      */   
/*      */   private final UDPSelector selector;
/*      */   
/*      */   private final int local_port;
/*      */   
/*      */   private final InetSocketAddress remote_address;
/*      */   
/*      */   private boolean outgoing;
/*      */   
/*      */   private final String connection_key;
/*      */   
/*      */   private Random random;
/*      */   private UDPConnection lead_connection;
/*      */   private RC4Engine header_cipher_out;
/*      */   private RC4Engine header_cipher_in;
/*      */   private SequenceGenerator in_seq_generator;
/*      */   private SequenceGenerator out_seq_generator;
/*      */   private volatile boolean crypto_done;
/*      */   private volatile boolean failed;
/*   89 */   private final Map connections = new HashMap();
/*      */   
/*   91 */   private final LinkedList connection_writers = new LinkedList();
/*      */   
/*      */   private long total_tick_count;
/*      */   
/*      */   private static final int STATS_LOG_TIMER = 60000;
/*   96 */   private static final int STATS_LOG_TICKS = Math.max(1, 2400);
/*   97 */   private int stats_log_ticks = STATS_LOG_TICKS;
/*      */   
/*      */ 
/*      */   private static final int IDLE_TIMER = 10000;
/*      */   
/*  102 */   private static final int IDLE_TICKS = Math.max(1, 400);
/*  103 */   private int idle_ticks = 0;
/*      */   
/*      */   private static final int TIMER_BASE_DEFAULT = 300;
/*      */   
/*      */   private static final int TIMER_BASE_MIN = 100;
/*      */   private static final int TIMER_BASE_MAX = 15000;
/*  109 */   private int current_timer_base = 300;
/*  110 */   private int old_timer_base = this.current_timer_base;
/*      */   
/*      */   private boolean timer_is_adjusting;
/*      */   
/*      */   private int stats_packets_unique_sent;
/*      */   
/*      */   private int stats_packets_resent_via_timer;
/*      */   
/*      */   private int stats_packets_unique_received;
/*      */   
/*      */   private int stats_packets_duplicates;
/*      */   
/*      */   private static final int STATS_RESET_TIMER = 30000;
/*  123 */   private long stats_reset_time = SystemTime.getCurrentTime();
/*      */   
/*      */ 
/*      */ 
/*  127 */   private int total_packets_sent = 0;
/*  128 */   private int total_data_sent = 0;
/*  129 */   private int total_data_resent = 0;
/*  130 */   private int total_protocol_sent = 0;
/*  131 */   private int total_protocol_resent = 0;
/*      */   
/*  133 */   private int total_packets_unique_sent = 0;
/*  134 */   private int total_packets_received = 0;
/*  135 */   private int total_packets_unique_received = 0;
/*  136 */   private int total_packets_duplicates = 0;
/*  137 */   private int total_packets_out_of_order = 0;
/*  138 */   private int total_packets_resent_via_timer = 0;
/*  139 */   private int total_packets_resent_via_ack = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  147 */   private int retransmit_ticks = 0;
/*      */   
/*      */   private UDPPacket current_retransmit_target;
/*      */   
/*      */   private static final int RETRANSMIT_COUNT_LIMIT = 5;
/*      */   
/*      */   private static final int MIN_RETRANSMIT_TIMER = 100;
/*  154 */   private static final int MIN_RETRANSMIT_TICKS = Math.max(1, 4);
/*      */   private static final int MAX_RETRANSMIT_TIMER = 20000;
/*  156 */   private static final int MAX_RETRANSMIT_TICKS = Math.max(1, 800);
/*      */   
/*      */   private static final int MAX_TRANSMIT_UNACK_DATA_PACKETS = 10;
/*      */   
/*      */   private static final int MAX_TRANSMIT_UNACK_PACKETS = 14;
/*  161 */   private final List transmit_unack_packets = new ArrayList();
/*      */   
/*      */   private static final int MAX_CONTIGUOUS_RETRANS_FOR_ACK = 3;
/*      */   
/*      */   private static final int MIN_KEEPALIVE_TIMER = 10000;
/*  166 */   private static final int MIN_KEEPALIVE_TICKS = Math.max(1, 400);
/*      */   private static final int MAX_KEEPALIVE_TIMER = 20000;
/*  168 */   private static final int MAX_KEEPALIVE_TICKS = Math.max(1, 800);
/*      */   
/*      */ 
/*      */   private int keep_alive_ticks;
/*      */   
/*  173 */   private int receive_last_inorder_sequence = -1;
/*  174 */   private int receive_last_inorder_alt_sequence = -1;
/*      */   
/*  176 */   private int receive_their_last_inorder_sequence = -1;
/*      */   
/*      */   private static final int RECEIVE_UNACK_IN_SEQUENCE_LIMIT = 3;
/*  179 */   private long current_receive_unack_in_sequence_count = 0L;
/*  180 */   private long sent_receive_unack_in_sequence_count = 0L;
/*      */   
/*      */   private static final int RECEIVE_OUT_OF_ORDER_ACK_LIMIT = 3;
/*  183 */   private long current_receive_out_of_order_count = 0L;
/*  184 */   private long sent_receive_out_of_order_count = 0L;
/*      */   
/*      */   private static final int RECEIVE_DONE_SEQ_MAX = 128;
/*      */   
/*  188 */   private final LinkedList receive_done_sequences = new LinkedList();
/*      */   
/*      */   private static final int RECEIVE_OUT_OF_ORDER_PACKETS_MAX = 64;
/*  191 */   private final List receive_out_of_order_packets = new LinkedList();
/*      */   
/*  193 */   private int explicitack_ticks = 0;
/*      */   
/*  195 */   private static final int MAX_SEQ_MEMORY = Math.max(64, 14);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected UDPConnectionSet(UDPConnectionManager _manager, String _connection_key, UDPSelector _selector, int _local_port, InetSocketAddress _remote_address)
/*      */   {
/*  205 */     this.manager = _manager;
/*  206 */     this.connection_key = _connection_key;
/*  207 */     this.selector = _selector;
/*  208 */     this.local_port = _local_port;
/*  209 */     this.remote_address = _remote_address;
/*      */   }
/*      */   
/*      */ 
/*      */   protected UDPSelector getSelector()
/*      */   {
/*  215 */     return this.selector;
/*      */   }
/*      */   
/*      */ 
/*      */   protected InetSocketAddress getRemoteAddress()
/*      */   {
/*  221 */     return this.remote_address;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getKey()
/*      */   {
/*  227 */     return this.connection_key;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void add(UDPConnection connection)
/*      */     throws IOException
/*      */   {
/*  236 */     UDPConnection old_connection = null;
/*      */     
/*  238 */     synchronized (this.connections)
/*      */     {
/*  240 */       if (this.failed)
/*      */       {
/*  242 */         throw new IOException("Connection set has failed");
/*      */       }
/*      */       
/*  245 */       old_connection = (UDPConnection)this.connections.put(new Integer(connection.getID()), connection);
/*      */       
/*  247 */       if ((this.connections.size() == 1) && (this.lead_connection == null))
/*      */       {
/*  249 */         this.lead_connection = connection;
/*      */         
/*  251 */         this.outgoing = true;
/*      */       }
/*      */     }
/*      */     
/*  255 */     if (old_connection != null)
/*      */     {
/*  257 */       Debug.out("Duplicate connection");
/*      */       
/*  259 */       old_connection.close("Duplication connection");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean remove(UDPConnection connection)
/*      */   {
/*  267 */     synchronized (this.connections)
/*      */     {
/*  269 */       this.connections.remove(new Integer(connection.getID()));
/*      */       
/*  271 */       return this.connections.size() == 0;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void poll()
/*      */   {
/*  278 */     synchronized (this.connections)
/*      */     {
/*  280 */       Iterator it = this.connections.values().iterator();
/*      */       
/*  282 */       while (it.hasNext())
/*      */       {
/*  284 */         ((UDPConnection)it.next()).poll();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setSecret(UDPConnection connection, byte[] session_secret)
/*      */   {
/*      */     try
/*      */     {
/*  295 */       if (connection == this.lead_connection)
/*      */       {
/*  297 */         if (this.manager.trace()) {
/*  298 */           trace("crypto done");
/*      */         }
/*      */         
/*  301 */         SHA1Hasher hasher = new SHA1Hasher();
/*      */         
/*  303 */         hasher.update(KEYA_IV);
/*  304 */         hasher.update(session_secret);
/*      */         
/*  306 */         byte[] a_key = hasher.getDigest();
/*      */         
/*  308 */         hasher = new SHA1Hasher();
/*      */         
/*  310 */         hasher.update(KEYB_IV);
/*  311 */         hasher.update(session_secret);
/*      */         
/*  313 */         byte[] b_key = hasher.getDigest();
/*      */         
/*  315 */         hasher = new SHA1Hasher();
/*      */         
/*  317 */         hasher.update(KEYC_IV);
/*  318 */         hasher.update(session_secret);
/*      */         
/*  320 */         byte[] c_key = hasher.getDigest();
/*      */         
/*  322 */         hasher = new SHA1Hasher();
/*      */         
/*  324 */         hasher.update(KEYD_IV);
/*  325 */         hasher.update(session_secret);
/*      */         
/*  327 */         byte[] d_key = hasher.getDigest();
/*      */         
/*      */ 
/*      */ 
/*  331 */         RC4Engine rc4_engine_a = getCipher(a_key);
/*  332 */         RC4Engine rc4_engine_b = getCipher(b_key);
/*  333 */         RC4Engine rc4_engine_c = getCipher(c_key);
/*  334 */         RC4Engine rc4_engine_d = getCipher(d_key);
/*      */         
/*      */ 
/*  337 */         if (this.lead_connection.isIncoming())
/*      */         {
/*  339 */           this.header_cipher_out = rc4_engine_a;
/*  340 */           this.header_cipher_in = rc4_engine_b;
/*      */           
/*  342 */           this.out_seq_generator = new SequenceGenerator(new Random(bytesToLong(d_key)), rc4_engine_c, false);
/*  343 */           this.in_seq_generator = new SequenceGenerator(new Random(bytesToLong(c_key)), rc4_engine_d, true);
/*      */           
/*  345 */           this.random = new Random(bytesToLong(d_key, 8));
/*      */         }
/*      */         else
/*      */         {
/*  349 */           this.header_cipher_out = rc4_engine_b;
/*  350 */           this.header_cipher_in = rc4_engine_a;
/*      */           
/*  352 */           this.in_seq_generator = new SequenceGenerator(new Random(bytesToLong(d_key)), rc4_engine_c, true);
/*  353 */           this.out_seq_generator = new SequenceGenerator(new Random(bytesToLong(c_key)), rc4_engine_d, false);
/*      */           
/*  355 */           this.random = new Random(bytesToLong(c_key, 8));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  362 */         this.out_seq_generator.getNextSequenceNumber();
/*      */         
/*  364 */         int[] initial_in_seqs = this.in_seq_generator.getNextSequenceNumber();
/*      */         
/*  366 */         this.receive_last_inorder_alt_sequence = initial_in_seqs[3];
/*      */         
/*  368 */         this.crypto_done = true;
/*      */       }
/*  370 */       else if (!this.crypto_done)
/*      */       {
/*  372 */         Debug.out("Secondary setSecret but crypto not done");
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  376 */       Debug.printStackTrace(e);
/*      */       
/*  378 */       connection.close("Crypto problems: " + Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private RC4Engine getCipher(byte[] key)
/*      */   {
/*  386 */     SecretKeySpec secret_key_spec = new SecretKeySpec(key, "RC4");
/*      */     
/*  388 */     RC4Engine rc4_engine = new RC4Engine();
/*      */     
/*  390 */     CipherParameters params_a = new KeyParameter(secret_key_spec.getEncoded());
/*      */     
/*      */ 
/*      */ 
/*  394 */     rc4_engine.init(true, params_a);
/*      */     
/*      */ 
/*      */ 
/*  398 */     byte[] temp = new byte['Ѐ'];
/*      */     
/*  400 */     rc4_engine.processBytes(temp, 0, temp.length, temp, 0);
/*      */     
/*  402 */     return rc4_engine;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void sendTimerBase()
/*      */   {
/*  410 */     if (!this.outgoing)
/*      */     {
/*  412 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  423 */     synchronized (this)
/*      */     {
/*  425 */       if (this.timer_is_adjusting)
/*      */       {
/*  427 */         return;
/*      */       }
/*      */       
/*      */ 
/*  431 */       if (this.stats_packets_unique_sent > 2)
/*      */       {
/*  433 */         int new_timer_base = this.current_timer_base;
/*      */         
/*  435 */         if (this.stats_packets_resent_via_timer > 0)
/*      */         {
/*  437 */           float resend_ratio = this.stats_packets_resent_via_timer / this.stats_packets_unique_sent;
/*      */           
/*      */ 
/*      */ 
/*  441 */           if (resend_ratio >= 0.25D)
/*      */           {
/*  443 */             new_timer_base = (int)(this.current_timer_base * (resend_ratio + 1.0F));
/*      */             
/*      */ 
/*      */ 
/*  447 */             new_timer_base = new_timer_base / 10 * 10;
/*      */             
/*  449 */             new_timer_base = Math.min(15000, new_timer_base);
/*      */             
/*  451 */             if (new_timer_base != this.current_timer_base)
/*      */             {
/*  453 */               if (this.manager.trace())
/*      */               {
/*  455 */                 trace("Increasing timer base from " + this.current_timer_base + " to " + new_timer_base + " due to resends (ratio=" + resend_ratio + ")");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  461 */         if ((new_timer_base == this.current_timer_base) && (this.stats_packets_unique_received > 2))
/*      */         {
/*  463 */           float duplicate_ratio = this.stats_packets_duplicates / this.stats_packets_unique_received;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  468 */           duplicate_ratio /= 2.0F;
/*      */           
/*      */ 
/*      */ 
/*  472 */           if (duplicate_ratio >= 0.25D)
/*      */           {
/*  474 */             new_timer_base = (int)(this.current_timer_base * (duplicate_ratio + 1.0F));
/*      */             
/*  476 */             new_timer_base = new_timer_base / 10 * 10;
/*      */             
/*  478 */             new_timer_base = Math.min(15000, new_timer_base);
/*      */             
/*  480 */             if (new_timer_base != this.current_timer_base)
/*      */             {
/*  482 */               if (this.manager.trace())
/*      */               {
/*  484 */                 trace("Increasing timer base from " + this.current_timer_base + " to " + new_timer_base + " due to duplicates (ratio=" + duplicate_ratio + ")");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  490 */         if ((new_timer_base == this.current_timer_base) && (this.stats_packets_unique_received > 2))
/*      */         {
/*      */ 
/*      */ 
/*  494 */           if ((this.stats_packets_resent_via_timer == 0) && (this.stats_packets_duplicates == 0))
/*      */           {
/*  496 */             new_timer_base = this.current_timer_base - this.current_timer_base / 10;
/*      */             
/*  498 */             new_timer_base = new_timer_base / 10 * 10;
/*      */             
/*  500 */             new_timer_base = Math.max(new_timer_base, 100);
/*      */             
/*  502 */             if (new_timer_base != this.current_timer_base)
/*      */             {
/*  504 */               if (this.manager.trace())
/*      */               {
/*  506 */                 trace("Decreasing timer base from " + this.current_timer_base + " to " + new_timer_base);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  512 */         boolean reset_stats = false;
/*      */         
/*  514 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  516 */         if (new_timer_base == this.current_timer_base)
/*      */         {
/*  518 */           if ((now < this.stats_reset_time) || (now - this.stats_reset_time > 30000L))
/*      */           {
/*  520 */             reset_stats = true;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  525 */           this.timer_is_adjusting = true;
/*      */           
/*  527 */           this.old_timer_base = this.current_timer_base;
/*  528 */           this.current_timer_base = new_timer_base;
/*      */           
/*  530 */           reset_stats = true;
/*      */         }
/*      */         
/*  533 */         if (reset_stats)
/*      */         {
/*  535 */           resetTimerStats();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void resetTimerStats()
/*      */   {
/*  544 */     this.stats_reset_time = SystemTime.getCurrentTime();
/*      */     
/*  546 */     this.stats_packets_unique_sent = 0;
/*  547 */     this.stats_packets_resent_via_timer = 0;
/*  548 */     this.stats_packets_duplicates = 0;
/*  549 */     this.stats_packets_unique_received = 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void receiveTimerBase(int theirs)
/*      */   {
/*  556 */     synchronized (this)
/*      */     {
/*  558 */       if (theirs != this.current_timer_base)
/*      */       {
/*  560 */         if (this.manager.trace())
/*      */         {
/*  562 */           trace("Received timer base: current=" + this.current_timer_base + ",theirs=" + theirs + "(adj=" + this.timer_is_adjusting + ")");
/*      */         }
/*      */       }
/*      */       
/*  566 */       if (this.outgoing)
/*      */       {
/*  568 */         if (theirs == this.current_timer_base)
/*      */         {
/*  570 */           if (this.timer_is_adjusting)
/*      */           {
/*  572 */             this.timer_is_adjusting = false;
/*      */             
/*  574 */             resetTimerStats();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       else {
/*  581 */         this.current_timer_base = theirs;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void timerTick()
/*      */     throws IOException
/*      */   {
/*  591 */     boolean retrans_expired = false;
/*  592 */     boolean ack_expired = false;
/*  593 */     boolean keep_alive_expired = false;
/*      */     
/*  595 */     synchronized (this)
/*      */     {
/*  597 */       if (this.connections.size() == 0)
/*      */       {
/*  599 */         this.idle_ticks += 1;
/*      */       }
/*      */       else
/*      */       {
/*  603 */         this.idle_ticks = 0;
/*      */       }
/*      */       
/*  606 */       this.total_tick_count += 1L;
/*      */       
/*  608 */       if (this.retransmit_ticks > 0)
/*      */       {
/*  610 */         this.retransmit_ticks -= 1;
/*      */         
/*  612 */         if (this.retransmit_ticks == 0)
/*      */         {
/*  614 */           retrans_expired = true;
/*      */         }
/*      */       }
/*      */       
/*  618 */       if (this.explicitack_ticks > 0)
/*      */       {
/*  620 */         this.explicitack_ticks -= 1;
/*      */         
/*  622 */         if (this.explicitack_ticks == 0)
/*      */         {
/*  624 */           ack_expired = true;
/*      */         }
/*      */       }
/*      */       
/*  628 */       if (this.keep_alive_ticks > 0)
/*      */       {
/*  630 */         this.keep_alive_ticks -= 1;
/*      */         
/*  632 */         if (this.keep_alive_ticks == 0)
/*      */         {
/*  634 */           keep_alive_expired = true;
/*      */         }
/*      */       }
/*      */       
/*  638 */       this.stats_log_ticks -= 1;
/*      */       
/*  640 */       if (this.stats_log_ticks == 0)
/*      */       {
/*  642 */         logStats();
/*      */         
/*  644 */         this.stats_log_ticks = STATS_LOG_TICKS;
/*      */       }
/*      */     }
/*      */     
/*  648 */     if (retrans_expired)
/*      */     {
/*  650 */       retransmitExpired();
/*      */     }
/*      */     
/*  653 */     if (ack_expired)
/*      */     {
/*  655 */       sendAckCommand(true);
/*      */     }
/*      */     
/*  658 */     if (keep_alive_expired)
/*      */     {
/*  660 */       sendStatsRequest();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getRetransmitTicks()
/*      */   {
/*      */     int timer_to_use;
/*      */     
/*  669 */     synchronized (this) {
/*      */       int timer_to_use;
/*  671 */       if (this.timer_is_adjusting) {
/*      */         int timer_to_use;
/*  673 */         if (this.current_timer_base > this.old_timer_base)
/*      */         {
/*  675 */           timer_to_use = this.current_timer_base;
/*      */         }
/*      */         else
/*      */         {
/*  679 */           timer_to_use = this.old_timer_base;
/*      */         }
/*      */       }
/*      */       else {
/*  683 */         timer_to_use = this.current_timer_base;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  690 */     int timer = timer_to_use * 5 / 3;
/*      */     
/*  692 */     return Math.max(1, timer / 25);
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getExplicitAckTicks()
/*      */   {
/*      */     int timer_to_use;
/*      */     
/*  700 */     synchronized (this) {
/*      */       int timer_to_use;
/*  702 */       if (this.timer_is_adjusting) {
/*      */         int timer_to_use;
/*  704 */         if (this.current_timer_base > this.old_timer_base)
/*      */         {
/*  706 */           timer_to_use = this.old_timer_base;
/*      */         }
/*      */         else
/*      */         {
/*  710 */           timer_to_use = this.current_timer_base;
/*      */         }
/*      */       }
/*      */       else {
/*  714 */         timer_to_use = this.current_timer_base;
/*      */       }
/*      */     }
/*      */     
/*  718 */     return Math.max(1, timer_to_use / 25);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void startKeepAliveTimer()
/*      */   {
/*  724 */     this.keep_alive_ticks = (MIN_KEEPALIVE_TICKS + this.random.nextInt(MAX_KEEPALIVE_TICKS - MIN_KEEPALIVE_TICKS));
/*      */   }
/*      */   
/*      */ 
/*      */   protected void stopKeepAliveTimer()
/*      */   {
/*  730 */     this.keep_alive_ticks = 0;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean idleLimitExceeded()
/*      */   {
/*  736 */     if (this.idle_ticks > IDLE_TICKS + RandomUtils.nextInt(2000) / 25)
/*      */     {
/*  738 */       synchronized (this.connections)
/*      */       {
/*  740 */         if (this.connections.size() == 0)
/*      */         {
/*  742 */           this.failed = true;
/*      */           
/*  744 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  749 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected UDPPacket getRetransmitPacket()
/*      */   {
/*  755 */     Iterator it = this.transmit_unack_packets.iterator();
/*      */     
/*  757 */     while (it.hasNext())
/*      */     {
/*  759 */       UDPPacket p = (UDPPacket)it.next();
/*      */       
/*  761 */       if (!p.hasBeenReceived())
/*      */       {
/*  763 */         boolean auto_retrans = p.isAutoRetransmit();
/*      */         
/*      */ 
/*      */ 
/*  767 */         if ((auto_retrans) || (it.hasNext()))
/*      */         {
/*  769 */           return p;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  774 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int getRetransmitTicks(int resend_count)
/*      */   {
/*  781 */     int ticks = getRetransmitTicks();
/*      */     
/*      */     int res;
/*      */     int res;
/*  785 */     if (resend_count == 0)
/*      */     {
/*  787 */       res = ticks;
/*      */     }
/*      */     else
/*      */     {
/*  791 */       res = ticks + (MAX_RETRANSMIT_TICKS - ticks) * resend_count / 4;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  796 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void retransmitExpired()
/*      */     throws IOException
/*      */   {
/*  804 */     UDPPacket packet_to_send = null;
/*      */     
/*  806 */     synchronized (this)
/*      */     {
/*  808 */       packet_to_send = getRetransmitPacket();
/*      */       
/*  810 */       if (packet_to_send != null)
/*      */       {
/*  812 */         this.stats_packets_resent_via_timer += 1;
/*  813 */         this.total_packets_resent_via_timer += 1;
/*      */         
/*  815 */         packet_to_send.resent();
/*      */       }
/*      */     }
/*      */     
/*  819 */     if (packet_to_send != null)
/*      */     {
/*  821 */       if (this.manager.trace()) {
/*  822 */         trace("Retransmit: " + packet_to_send.getString());
/*      */       }
/*      */       
/*  825 */       send(packet_to_send);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean remoteLastInSequence(int alt_sequence)
/*      */   {
/*  836 */     synchronized (this)
/*      */     {
/*  838 */       for (int i = 0; i < this.transmit_unack_packets.size(); i++)
/*      */       {
/*  840 */         UDPPacket packet = (UDPPacket)this.transmit_unack_packets.get(i);
/*      */         
/*  842 */         if (packet.getAlternativeSequence() == alt_sequence)
/*      */         {
/*  844 */           this.receive_their_last_inorder_sequence = packet.getSequence();
/*      */           
/*  846 */           for (int j = 0; j <= i; j++)
/*      */           {
/*  848 */             this.transmit_unack_packets.remove(0);
/*      */           }
/*      */           
/*  851 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  856 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   protected synchronized void dumpState()
/*      */   {
/*  862 */     if (this.manager.trace()) {
/*  863 */       String str = "State:";
/*      */       
/*  865 */       String unack = "";
/*      */       
/*  867 */       for (int i = 0; i < this.transmit_unack_packets.size(); i++)
/*      */       {
/*  869 */         UDPPacket packet = (UDPPacket)this.transmit_unack_packets.get(i);
/*      */         
/*  871 */         unack = unack + (i == 0 ? "" : ",") + packet.getString();
/*      */       }
/*      */       
/*  874 */       str = str + "unack=" + unack + ",last_in_order=" + this.receive_last_inorder_sequence + ",current_in_seq=" + this.current_receive_unack_in_sequence_count + ",sent_in_seq=" + this.sent_receive_unack_in_sequence_count + ",current_oo=" + this.current_receive_out_of_order_count + ",sent_oo=" + this.sent_receive_out_of_order_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  891 */       String oo = "";
/*      */       
/*  893 */       for (int i = 0; i < this.receive_out_of_order_packets.size(); i++)
/*      */       {
/*  895 */         Object[] entry = (Object[])this.receive_out_of_order_packets.get(i);
/*      */         
/*  897 */         oo = oo + (i == 0 ? "" : ",") + entry[0] + "/" + entry[1] + "/" + (entry[2] == null ? "null" : String.valueOf(((ByteBuffer)entry[2]).remaining()));
/*      */       }
/*      */       
/*  900 */       str = str + ",oo=" + oo;
/*      */       
/*  902 */       str = str + ",sent_data=" + this.total_data_sent + "/" + this.total_data_resent + ",sent_prot=" + this.total_protocol_sent + "/" + this.total_protocol_resent;
/*      */       
/*  904 */       trace(str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void send(UDPPacket packet)
/*      */     throws IOException
/*      */   {
/*  917 */     if (this.failed)
/*      */     {
/*  919 */       throw new IOException("Connection set has failed");
/*      */     }
/*      */     
/*  922 */     byte[] payload = packet.getBuffer();
/*      */     
/*  924 */     if (this.manager.trace()) {
/*  925 */       trace(packet.getConnection(), "Write: " + packet.getString());
/*      */     }
/*      */     
/*  928 */     synchronized (this)
/*      */     {
/*  930 */       this.total_packets_sent += 1;
/*      */       
/*  932 */       int resend_count = packet.getResendCount();
/*      */       
/*  934 */       if (resend_count > 5)
/*      */       {
/*  936 */         throw new IOException("Packet resend limit exceeded");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  941 */       long unackin = packet.getUnAckInSequenceCount();
/*      */       
/*  943 */       if (unackin > this.sent_receive_unack_in_sequence_count)
/*      */       {
/*  945 */         this.sent_receive_unack_in_sequence_count = unackin;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  950 */       UDPPacket retransmit_target = getRetransmitPacket();
/*      */       
/*  952 */       if (retransmit_target == null)
/*      */       {
/*      */ 
/*      */ 
/*  956 */         this.retransmit_ticks = 0;
/*      */       }
/*  958 */       else if ((retransmit_target != this.current_retransmit_target) || (retransmit_target == packet))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  963 */         this.retransmit_ticks = getRetransmitTicks(resend_count);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  969 */       else if (this.retransmit_ticks == 0)
/*      */       {
/*  971 */         this.retransmit_ticks = getRetransmitTicks(resend_count);
/*      */       }
/*      */       
/*      */ 
/*  975 */       this.current_retransmit_target = retransmit_target;
/*      */       
/*      */ 
/*      */ 
/*  979 */       if (packet.getAlternativeSequence() != -1)
/*      */       {
/*  981 */         byte[] alt = intToBytes(this.receive_last_inorder_alt_sequence);
/*      */         
/*  983 */         payload[0] = alt[0];
/*  984 */         payload[1] = alt[1];
/*  985 */         payload[8] = alt[2];
/*  986 */         payload[9] = alt[3];
/*      */       }
/*      */       
/*  989 */       int send_count = packet.sent(this.total_tick_count);
/*      */       
/*  991 */       if (send_count == 1)
/*      */       {
/*  993 */         if (packet.getCommand() == 1)
/*      */         {
/*  995 */           this.total_data_sent += 1;
/*      */         }
/*      */         else
/*      */         {
/*  999 */           this.total_protocol_sent += 1;
/*      */         }
/*      */         
/*      */       }
/* 1003 */       else if (packet.getCommand() == 1)
/*      */       {
/* 1005 */         this.total_data_resent += 1;
/*      */       }
/*      */       else
/*      */       {
/* 1009 */         this.total_protocol_resent += 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1014 */     this.manager.send(this.local_port, this.remote_address, payload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void receive(byte[] initial_data, int initial_data_length)
/*      */     throws IOException
/*      */   {
/* 1024 */     if (this.failed)
/*      */     {
/* 1026 */       throw new IOException("Connection set has failed");
/*      */     }
/*      */     
/* 1029 */     dumpState();
/*      */     
/* 1031 */     if (this.manager.trace()) {
/* 1032 */       trace("Read: total=" + initial_data_length);
/*      */     }
/*      */     
/* 1035 */     synchronized (this)
/*      */     {
/* 1037 */       this.total_packets_received += 1;
/*      */     }
/*      */     
/* 1040 */     ByteBuffer initial_buffer = ByteBuffer.wrap(initial_data);
/*      */     
/* 1042 */     initial_buffer.limit(initial_data_length);
/*      */     
/* 1044 */     if (!this.crypto_done)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1052 */       initial_buffer.position(4);
/*      */       
/* 1054 */       Integer pseudo_seq = new Integer(initial_buffer.getInt());
/*      */       
/* 1056 */       initial_buffer.position(0);
/*      */       
/* 1058 */       if (!this.receive_done_sequences.contains(pseudo_seq))
/*      */       {
/* 1060 */         this.receive_done_sequences.addFirst(pseudo_seq);
/*      */         
/* 1062 */         if (this.receive_done_sequences.size() > 128)
/*      */         {
/* 1064 */           this.receive_done_sequences.removeLast();
/*      */         }
/*      */       }
/*      */       
/* 1068 */       if (this.outgoing)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1073 */         remoteLastInSequence(-1);
/*      */       }
/*      */       
/* 1076 */       receiveCrypto(initial_buffer);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/* 1082 */       byte[] alt_seq = new byte[4];
/*      */       
/* 1084 */       alt_seq[0] = initial_data[0];
/* 1085 */       alt_seq[1] = initial_data[1];
/* 1086 */       alt_seq[2] = initial_data[8];
/* 1087 */       alt_seq[3] = initial_data[9];
/*      */       
/* 1089 */       int alt = bytesToInt(alt_seq, 0);
/*      */       
/* 1091 */       boolean write_select = remoteLastInSequence(alt);
/*      */       
/* 1093 */       boolean lazy_ack_found = false;
/*      */       try
/*      */       {
/* 1096 */         initial_buffer.getInt();
/*      */         
/* 1098 */         Integer seq2 = new Integer(initial_buffer.getInt());
/*      */         
/* 1100 */         initial_buffer.getInt();
/*      */         boolean send_ack;
/*      */         long unack_diff;
/*      */         long oos_diff;
/* 1104 */         long unack_diff; Iterator it; UDPConnection c; if (this.receive_done_sequences.contains(seq2))
/*      */         {
/* 1106 */           if (this.manager.trace()) {
/* 1107 */             trace("Duplicate processed packet: " + seq2);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1113 */           UDPPacket packet_to_send = null;
/*      */           
/* 1115 */           synchronized (this)
/*      */           {
/* 1117 */             this.stats_packets_duplicates += 1;
/* 1118 */             this.total_packets_duplicates += 1;
/*      */             
/* 1120 */             if (this.transmit_unack_packets.size() == 1)
/*      */             {
/* 1122 */               UDPPacket packet = (UDPPacket)this.transmit_unack_packets.get(0);
/*      */               
/* 1124 */               if (!packet.isAutoRetransmit())
/*      */               {
/* 1126 */                 if (this.total_tick_count - packet.getSendTickCount() >= MIN_RETRANSMIT_TICKS)
/*      */                 {
/* 1128 */                   if (this.manager.trace()) {
/* 1129 */                     trace("Retrans non-auto-retrans packet");
/*      */                   }
/*      */                   
/* 1132 */                   packet_to_send = packet;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1138 */           if (packet_to_send != null)
/*      */           {
/* 1140 */             send(packet_to_send); } } else { boolean send_ack;
/*      */           long unack_diff;
/*      */           long oos_diff;
/*      */           long unack_diff;
/*      */           Iterator it;
/*      */           UDPConnection c;
/* 1146 */           if (!this.out_seq_generator.isValidAlterativeSequence(alt))
/*      */           {
/* 1148 */             if (this.manager.trace()) {
/* 1149 */               trace("Received invalid alternative sequence " + alt + " - dropping packet");
/*      */             }
/*      */             
/*      */           }
/*      */           else
/*      */           {
/* 1155 */             boolean oop = false;
/*      */             
/* 1157 */             for (int i = 0; i < this.receive_out_of_order_packets.size(); i++)
/*      */             {
/* 1159 */               Object[] entry = (Object[])this.receive_out_of_order_packets.get(i);
/*      */               
/* 1161 */               Integer oop_seq = (Integer)entry[0];
/*      */               
/* 1163 */               ByteBuffer oop_buffer = (ByteBuffer)entry[2];
/*      */               
/* 1165 */               if (oop_seq.equals(seq2))
/*      */               {
/* 1167 */                 synchronized (this)
/*      */                 {
/* 1169 */                   if (oop_buffer != null)
/*      */                   {
/* 1171 */                     this.stats_packets_duplicates += 1;
/* 1172 */                     this.total_packets_duplicates += 1;
/*      */                     
/* 1174 */                     if (this.manager.trace())
/* 1175 */                       trace("Duplicate out-of-order packet: " + seq2);
/*      */                     boolean send_ack;
/*      */                     long unack_diff;
/*      */                     long oos_diff;
/*      */                     long unack_diff;
/*      */                     Iterator it;
/*      */                     UDPConnection c; return; } this.stats_packets_unique_received += 1;
/* 1182 */                   this.total_packets_unique_received += 1;
/*      */                   
/* 1184 */                   if (this.manager.trace()) {
/* 1185 */                     trace("Out-of-order packet entry data matched for seq " + seq2);
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 1190 */                   entry[2] = initial_buffer;
/*      */                   
/* 1192 */                   oop = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1199 */             if (!oop)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1204 */               boolean added = false;
/*      */               
/* 1206 */               while (this.receive_out_of_order_packets.size() < 64)
/*      */               {
/* 1208 */                 int[] seq_in = this.in_seq_generator.getNextSequenceNumber();
/*      */                 
/* 1210 */                 if (seq2.intValue() == seq_in[1])
/*      */                 {
/* 1212 */                   synchronized (this)
/*      */                   {
/* 1214 */                     this.stats_packets_unique_received += 1;
/* 1215 */                     this.total_packets_unique_received += 1;
/*      */                   }
/*      */                   
/* 1218 */                   if (this.receive_out_of_order_packets.size() != 0)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1224 */                     if (this.manager.trace()) {
/* 1225 */                       trace("Out-of-order packet entry adding for seq " + seq_in[1]);
/*      */                     }
/*      */                   }
/*      */                   
/* 1229 */                   this.receive_out_of_order_packets.add(new Object[] { seq2, new Integer(seq_in[3]), initial_buffer });
/*      */                   
/* 1231 */                   added = true;
/*      */                   
/* 1233 */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 1237 */                 if (this.manager.trace()) {
/* 1238 */                   trace("Out-of-order packet: adding spacer for seq " + seq_in[1]);
/*      */                 }
/*      */                 
/* 1241 */                 this.receive_out_of_order_packets.add(new Object[] { new Integer(seq_in[1]), new Integer(seq_in[3]), null });
/*      */               }
/*      */               
/*      */ 
/* 1245 */               if (!added)
/*      */               {
/*      */ 
/*      */ 
/* 1249 */                 if (this.manager.trace())
/* 1250 */                   trace("Out-of-order packet dropped as too many pending");
/*      */                 boolean send_ack;
/*      */                 long unack_diff;
/*      */                 long oos_diff;
/*      */                 long unack_diff;
/*      */                 Iterator it;
/*      */                 UDPConnection c;
/*      */                 return; } } boolean this_is_oop = true;
/*      */             
/*      */ 
/*      */ 
/* 1261 */             Iterator it = this.receive_out_of_order_packets.iterator();
/*      */             
/* 1263 */             while (it.hasNext())
/*      */             {
/* 1265 */               Object[] entry = (Object[])it.next();
/*      */               
/* 1267 */               ByteBuffer buffer = (ByteBuffer)entry[2];
/*      */               
/* 1269 */               if (buffer == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1274 */               it.remove();
/*      */               
/* 1276 */               byte[] data = buffer.array();
/*      */               
/* 1278 */               if (buffer == initial_buffer)
/*      */               {
/* 1280 */                 this_is_oop = false;
/*      */               }
/*      */               
/* 1283 */               synchronized (this)
/*      */               {
/* 1285 */                 this.current_receive_unack_in_sequence_count += 1L;
/*      */               }
/*      */               
/* 1288 */               Integer seq = (Integer)entry[0];
/*      */               
/* 1290 */               this.receive_last_inorder_sequence = seq.intValue();
/* 1291 */               this.receive_last_inorder_alt_sequence = ((Integer)entry[1]).intValue();
/*      */               
/* 1293 */               if (!this.receive_done_sequences.contains(seq))
/*      */               {
/* 1295 */                 this.receive_done_sequences.addFirst(seq);
/*      */                 
/* 1297 */                 if (this.receive_done_sequences.size() > 128)
/*      */                 {
/* 1299 */                   this.receive_done_sequences.removeLast();
/*      */                 }
/*      */               }
/*      */               
/* 1303 */               this.header_cipher_in.processBytes(data, 12, 2, data, 12);
/*      */               
/* 1305 */               int header_len = buffer.getShort() & 0xFFFF;
/*      */               
/* 1307 */               if (header_len > data.length)
/*      */               {
/* 1309 */                 if (this.manager.trace())
/* 1310 */                   trace("Header length too large");
/*      */                 boolean send_ack;
/*      */                 long unack_diff;
/*      */                 long oos_diff;
/*      */                 long unack_diff;
/*      */                 Iterator it;
/*      */                 UDPConnection c; return; } this.header_cipher_in.processBytes(data, 14, header_len - 14, data, 14);
/*      */               
/* 1318 */               SHA1Hasher hasher = new SHA1Hasher();
/*      */               
/* 1320 */               hasher.update(data, 4, 4);
/* 1321 */               hasher.update(data, 12, header_len - 4 - 12);
/*      */               
/* 1323 */               byte[] hash = hasher.getDigest();
/*      */               
/* 1325 */               for (int i = 0; i < 4;)
/*      */               {
/* 1327 */                 if (hash[i] != data[(header_len - 4 + i)])
/*      */                 {
/* 1329 */                   if (this.manager.trace()) {
/* 1330 */                     trace("hash incorrect");
/*      */                   }
/*      */                   boolean send_ack;
/*      */                   long unack_diff;
/*      */                   long oos_diff;
/*      */                   long unack_diff;
/*      */                   Iterator it;
/*      */                   UDPConnection c;
/*      */                   return;
/*      */                 }
/* 1325 */                 i++;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1337 */               byte version = buffer.get();
/*      */               
/* 1339 */               if (version != 1) {}
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1346 */               byte flags = buffer.get();
/*      */               
/* 1348 */               if ((flags & 0x1) != 0)
/*      */               {
/* 1350 */                 lazy_ack_found = true;
/*      */               }
/*      */               
/* 1353 */               int their_timer_base = (buffer.getShort() & 0xFFFF) * 10;
/*      */               
/* 1355 */               receiveTimerBase(their_timer_base);
/*      */               
/* 1357 */               byte command = buffer.get();
/*      */               
/* 1359 */               if (command == 1)
/*      */               {
/* 1361 */                 receiveDataCommand(seq.intValue(), buffer, header_len);
/*      */               }
/* 1363 */               else if (command == 2)
/*      */               {
/* 1365 */                 receiveAckCommand(buffer);
/*      */               }
/* 1367 */               else if (command == 3)
/*      */               {
/* 1369 */                 receiveCloseCommand(buffer);
/*      */               }
/* 1371 */               else if (command == 4)
/*      */               {
/* 1373 */                 receiveStatsRequest(buffer);
/*      */               }
/* 1375 */               else if (command == 5)
/*      */               {
/* 1377 */                 receiveStatsReply(buffer);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1385 */             if (this_is_oop)
/*      */             {
/* 1387 */               synchronized (this)
/*      */               {
/* 1389 */                 this.current_receive_out_of_order_count += 1L;
/* 1390 */                 this.total_packets_out_of_order += 1; } } } } } finally { boolean send_ack;
/*      */         long unack_diff;
/*      */         long oos_diff;
/*      */         long unack_diff;
/*      */         Iterator it;
/* 1395 */         UDPConnection c; boolean send_ack = false;
/*      */         
/* 1397 */         synchronized (this)
/*      */         {
/* 1399 */           long unack_diff = this.current_receive_unack_in_sequence_count - this.sent_receive_unack_in_sequence_count;
/* 1400 */           long oos_diff = this.current_receive_out_of_order_count - this.sent_receive_out_of_order_count;
/*      */           
/* 1402 */           if ((unack_diff > 3L) || (oos_diff > 3L))
/*      */           {
/*      */ 
/* 1405 */             send_ack = true;
/*      */           }
/*      */         }
/*      */         
/* 1409 */         if (send_ack)
/*      */         {
/* 1411 */           sendAckCommand(false);
/*      */         }
/*      */         
/* 1414 */         synchronized (this)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1421 */           long unack_diff = this.current_receive_unack_in_sequence_count - this.sent_receive_unack_in_sequence_count;
/*      */           
/* 1423 */           if ((unack_diff == 1L) && (lazy_ack_found) && (this.receive_out_of_order_packets.size() == 0))
/*      */           {
/* 1425 */             if (this.manager.trace()) {
/* 1426 */               trace("Not starting ack timer, only lazy ack received");
/*      */             }
/*      */             
/* 1429 */             startKeepAliveTimer();
/*      */           }
/*      */           else
/*      */           {
/* 1433 */             stopKeepAliveTimer();
/*      */             
/* 1435 */             if ((unack_diff > 0L) || (this.receive_out_of_order_packets.size() > 0))
/*      */             {
/* 1437 */               if (this.explicitack_ticks == 0)
/*      */               {
/* 1439 */                 this.explicitack_ticks = getExplicitAckTicks();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1445 */         if (write_select)
/*      */         {
/* 1447 */           synchronized (this.connection_writers)
/*      */           {
/* 1449 */             Iterator it = this.connection_writers.iterator();
/*      */             
/* 1451 */             while (it.hasNext())
/*      */             {
/* 1453 */               UDPConnection c = (UDPConnection)it.next();
/*      */               
/* 1455 */               if (c.isConnected())
/*      */               {
/*      */ 
/*      */ 
/* 1459 */                 c.sent();
/*      */               }
/*      */               else
/*      */               {
/* 1463 */                 it.remove();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
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
/*      */   protected int sendCrypto(ByteBuffer[] buffers, int offset, int length)
/*      */     throws IOException
/*      */   {
/* 1483 */     int payload_to_send = 0;
/*      */     
/* 1485 */     for (int i = offset; i < offset + length; i++)
/*      */     {
/* 1487 */       payload_to_send += buffers[i].remaining();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1492 */     byte[] packet_bytes = new byte[payload_to_send];
/*      */     
/* 1494 */     ByteBuffer packet_buffer = ByteBuffer.wrap(packet_bytes);
/*      */     
/* 1496 */     for (int i = offset; i < offset + length; i++)
/*      */     {
/* 1498 */       packet_buffer.put(buffers[i]);
/*      */     }
/*      */     
/* 1501 */     UDPPacket packet_to_send = new UDPPacket(this.lead_connection, new int[] { -1, -1, -1, -1 }, (byte)0, packet_bytes, 0L);
/*      */     
/* 1503 */     synchronized (this)
/*      */     {
/* 1505 */       this.stats_packets_unique_sent += 1;
/* 1506 */       this.total_packets_unique_sent += 1;
/*      */       
/* 1508 */       this.transmit_unack_packets.add(packet_to_send);
/*      */     }
/*      */     
/* 1511 */     if (this.manager.trace()) {
/* 1512 */       trace("sendCrypto: seq=" + packet_to_send.getSequence() + ", len=" + payload_to_send);
/*      */     }
/*      */     
/* 1515 */     send(packet_to_send);
/*      */     
/* 1517 */     return payload_to_send;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveCrypto(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1526 */     boolean new_connection = false;
/*      */     
/* 1528 */     UDPConnection connection = null;
/*      */     
/* 1530 */     synchronized (this.connections)
/*      */     {
/* 1532 */       if (this.failed)
/*      */       {
/* 1534 */         throw new IOException("Connection set has failed");
/*      */       }
/*      */       
/* 1537 */       if (this.connections.size() == 0)
/*      */       {
/*      */ 
/*      */ 
/* 1541 */         connection = new UDPConnection(this, -1);
/*      */         
/* 1543 */         this.connections.put(new Integer(connection.getID()), connection);
/*      */         
/* 1545 */         this.lead_connection = connection;
/*      */         
/* 1547 */         new_connection = true;
/*      */       }
/*      */       else
/*      */       {
/* 1551 */         connection = this.lead_connection;
/*      */       }
/*      */     }
/*      */     
/* 1555 */     if (new_connection)
/*      */     {
/* 1557 */       this.manager.accept(this.local_port, this.remote_address, connection);
/*      */     }
/*      */     
/* 1560 */     if (this.manager.trace()) {
/* 1561 */       trace(connection, "readCrypto: rem=" + buffer.remaining());
/*      */     }
/*      */     
/* 1564 */     connection.receive(buffer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int sendDataCommand(UDPConnection connection, ByteBuffer[] buffers, int offset, int length)
/*      */     throws IOException
/*      */   {
/* 1577 */     int payload_to_send = 0;
/*      */     
/* 1579 */     for (int i = offset; i < offset + length; i++)
/*      */     {
/* 1581 */       payload_to_send += buffers[i].remaining();
/*      */     }
/*      */     
/* 1584 */     byte[] header = new byte['Ā'];
/*      */     
/* 1586 */     ByteBuffer header_buffer = ByteBuffer.wrap(header);
/*      */     
/*      */     UDPPacket packet_to_send;
/*      */     
/* 1590 */     synchronized (this)
/*      */     {
/* 1592 */       long unack_in_sequence_count = this.current_receive_unack_in_sequence_count;
/*      */       
/* 1594 */       int[] sequence_numbers = writeHeaderStart(header_buffer, (byte)1, (byte)0);
/*      */       
/* 1596 */       header_buffer.putInt(connection.getID());
/*      */       
/* 1598 */       int header_size = writeHeaderEnd(header_buffer, false);
/*      */       
/* 1600 */       int mss = connection.getTransport().getMss();
/*      */       
/*      */ 
/*      */ 
/* 1604 */       if (mss < 256)
/*      */       {
/* 1606 */         mss = 256;
/*      */       }
/*      */       
/* 1609 */       if (payload_to_send > mss - header_size)
/*      */       {
/* 1611 */         payload_to_send = mss - header_size;
/*      */       }
/*      */       
/* 1614 */       if (payload_to_send < 0)
/*      */       {
/* 1616 */         payload_to_send = 0;
/*      */       }
/*      */       
/* 1619 */       byte[] packet_bytes = new byte[header_size + payload_to_send];
/*      */       
/* 1621 */       ByteBuffer packet_buffer = ByteBuffer.wrap(packet_bytes);
/*      */       
/* 1623 */       packet_buffer.put(header, 0, header_size);
/*      */       
/* 1625 */       int rem = payload_to_send;
/*      */       
/* 1627 */       for (int i = offset; i < offset + length; i++)
/*      */       {
/* 1629 */         ByteBuffer buffer = buffers[i];
/*      */         
/* 1631 */         int limit = buffer.limit();
/*      */         try
/*      */         {
/* 1634 */           if (buffer.remaining() > rem)
/*      */           {
/* 1636 */             buffer.limit(buffer.position() + rem);
/*      */           }
/*      */           
/* 1639 */           rem -= buffer.remaining();
/*      */           
/* 1641 */           packet_buffer.put(buffer);
/*      */         }
/*      */         finally
/*      */         {
/* 1645 */           buffer.limit(limit);
/*      */         }
/*      */         
/* 1648 */         if (rem == 0) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1654 */       packet_to_send = new UDPPacket(connection, sequence_numbers, (byte)1, packet_bytes, unack_in_sequence_count);
/*      */       
/* 1656 */       this.transmit_unack_packets.add(packet_to_send);
/*      */     }
/*      */     
/* 1659 */     if (this.manager.trace()) {
/* 1660 */       trace(connection, "sendData: seq=" + packet_to_send.getSequence() + ",data=" + payload_to_send);
/*      */     }
/*      */     
/* 1663 */     send(packet_to_send);
/*      */     
/* 1665 */     return payload_to_send;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveDataCommand(int sequence, ByteBuffer buffer, int header_length)
/*      */     throws IOException
/*      */   {
/* 1676 */     int connection_id = buffer.getInt();
/*      */     
/* 1678 */     UDPConnection connection = null;
/* 1679 */     boolean new_connection = false;
/*      */     
/* 1681 */     synchronized (this.connections)
/*      */     {
/* 1683 */       if (this.failed)
/*      */       {
/* 1685 */         throw new IOException("Connection set has failed");
/*      */       }
/*      */       
/* 1688 */       connection = (UDPConnection)this.connections.get(new Integer(connection_id));
/*      */       
/* 1690 */       if (connection == null)
/*      */       {
/* 1692 */         connection = (UDPConnection)this.connections.remove(new Integer(-1));
/*      */         
/* 1694 */         if (connection != null)
/*      */         {
/* 1696 */           connection.setID(connection_id);
/*      */           
/* 1698 */           this.connections.put(new Integer(connection_id), connection);
/*      */         }
/*      */       }
/*      */       
/* 1702 */       if (connection == null)
/*      */       {
/* 1704 */         if (this.connections.size() == 128)
/*      */         {
/* 1706 */           throw new IOException("Connection limit reached");
/*      */         }
/*      */         
/* 1709 */         connection = new UDPConnection(this, connection_id);
/*      */         
/* 1711 */         this.connections.put(new Integer(connection.getID()), connection);
/*      */         
/* 1713 */         new_connection = true;
/*      */       }
/*      */     }
/*      */     
/* 1717 */     buffer.position(header_length);
/*      */     
/* 1719 */     if (new_connection)
/*      */     {
/* 1721 */       this.manager.accept(this.local_port, this.remote_address, connection);
/*      */     }
/*      */     
/* 1724 */     if (this.manager.trace()) {
/* 1725 */       trace(connection, "receiveData: seq=" + sequence + ",data=" + buffer.remaining());
/*      */     }
/*      */     
/* 1728 */     connection.receive(buffer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendAckCommand(boolean timer_expired)
/*      */     throws IOException
/*      */   {
/* 1739 */     UDPPacket packet_to_send = null;
/*      */     
/* 1741 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/* 1745 */       Iterator it = this.transmit_unack_packets.iterator();
/*      */       
/* 1747 */       while (it.hasNext())
/*      */       {
/* 1749 */         UDPPacket packet = (UDPPacket)it.next();
/*      */         
/* 1751 */         if (packet.getCommand() == 2)
/*      */         {
/* 1753 */           if (this.total_tick_count - packet.getSendTickCount() >= getExplicitAckTicks())
/*      */           {
/* 1755 */             if (this.manager.trace()) {
/* 1756 */               trace(packet.getConnection(), "retransAck:" + packet.getString());
/*      */             }
/*      */             
/* 1759 */             packet_to_send = packet;
/*      */             
/* 1761 */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1767 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1772 */       if (packet_to_send == null)
/*      */       {
/* 1774 */         byte[] header_bytes = new byte['Ȅ'];
/*      */         
/* 1776 */         ByteBuffer header = ByteBuffer.wrap(header_bytes);
/*      */         
/* 1778 */         long unack_in_sequence_count = this.current_receive_unack_in_sequence_count;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1783 */         boolean no_retrans = (this.transmit_unack_packets.size() == 0) && (timer_expired) && (this.receive_out_of_order_packets.size() == 0);
/*      */         
/* 1785 */         int[] sequences = writeHeaderStart(header, (byte)2, (byte)(no_retrans ? 1 : 0));
/*      */         
/* 1787 */         it = this.receive_out_of_order_packets.iterator();
/*      */         
/* 1789 */         String oos_str = "";
/*      */         
/* 1791 */         int count = 0;
/*      */         
/* 1793 */         while ((it.hasNext()) && (count < 3))
/*      */         {
/* 1795 */           Object[] entry = (Object[])it.next();
/*      */           
/* 1797 */           if (entry[2] != null)
/*      */           {
/* 1799 */             int out_of_order_seq = ((Integer)entry[0]).intValue();
/* 1800 */             int out_of_rep_seq = ((Integer)entry[1]).intValue();
/*      */             
/* 1802 */             oos_str = oos_str + (oos_str.length() == 0 ? "" : ",") + out_of_order_seq + "/" + out_of_rep_seq;
/*      */             
/* 1804 */             header.putInt(out_of_order_seq);
/*      */             
/* 1806 */             count++;
/*      */           }
/*      */         }
/*      */         
/* 1810 */         header.putInt(-1);
/*      */         
/* 1812 */         if (count == 0)
/*      */         {
/* 1814 */           this.sent_receive_out_of_order_count = this.current_receive_out_of_order_count;
/*      */         }
/*      */         else
/*      */         {
/* 1818 */           this.sent_receive_out_of_order_count += count;
/*      */           
/* 1820 */           if (this.sent_receive_out_of_order_count > this.current_receive_out_of_order_count)
/*      */           {
/* 1822 */             this.sent_receive_out_of_order_count = this.current_receive_out_of_order_count;
/*      */           }
/*      */         }
/*      */         
/* 1826 */         int size = writeHeaderEnd(header, true);
/*      */         
/* 1828 */         byte[] packet_bytes = new byte[size];
/*      */         
/* 1830 */         System.arraycopy(header_bytes, 0, packet_bytes, 0, size);
/*      */         
/* 1832 */         packet_to_send = new UDPPacket(this.lead_connection, sequences, (byte)2, packet_bytes, unack_in_sequence_count);
/*      */         
/* 1834 */         if (no_retrans)
/*      */         {
/* 1836 */           packet_to_send.setAutoRetransmit(false);
/*      */           
/* 1838 */           startKeepAliveTimer();
/*      */         }
/*      */         
/* 1841 */         this.transmit_unack_packets.add(packet_to_send);
/*      */         
/* 1843 */         if (this.manager.trace()) {
/* 1844 */           trace(this.lead_connection, "sendAck: in_seq=" + this.receive_last_inorder_sequence + ",out_of_seq=" + oos_str);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1855 */     send(packet_to_send);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveAckCommand(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1864 */     List resend_list = new ArrayList();
/*      */     
/* 1866 */     String oos_str = "";
/*      */     
/* 1868 */     synchronized (this)
/*      */     {
/* 1870 */       Iterator it = this.transmit_unack_packets.iterator();
/*      */       
/* 1872 */       while (resend_list.size() < 3)
/*      */       {
/* 1874 */         int out_of_order_seq = buffer.getInt();
/*      */         
/* 1876 */         if (out_of_order_seq == -1) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1882 */         if (this.manager.trace()) {
/* 1883 */           oos_str = oos_str + (oos_str.length() == 0 ? "" : ",") + out_of_order_seq;
/*      */         }
/*      */         
/* 1886 */         while ((it.hasNext()) && (resend_list.size() < 3))
/*      */         {
/* 1888 */           UDPPacket packet = (UDPPacket)it.next();
/*      */           
/* 1890 */           if (packet.getSequence() == out_of_order_seq)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1895 */             packet.setHasBeenReceived();
/*      */             
/* 1897 */             break;
/*      */           }
/*      */           
/* 1900 */           if (this.total_tick_count - packet.getSendTickCount() >= MIN_RETRANSMIT_TICKS)
/*      */           {
/* 1902 */             if (!resend_list.contains(packet))
/*      */             {
/* 1904 */               resend_list.add(packet);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1911 */       this.total_packets_resent_via_ack += resend_list.size();
/*      */     }
/*      */     
/* 1914 */     if (this.manager.trace()) {
/* 1915 */       trace("receiveAck: in_seq=" + this.receive_their_last_inorder_sequence + ",out_of_seq=" + oos_str);
/*      */     }
/*      */     
/* 1918 */     for (int i = 0; i < resend_list.size(); i++)
/*      */     {
/* 1920 */       send((UDPPacket)resend_list.get(i));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendStatsRequest()
/*      */     throws IOException
/*      */   {
/* 1930 */     UDPPacket packet_to_send = null;
/*      */     
/* 1932 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/* 1936 */       Iterator it = this.transmit_unack_packets.iterator();
/*      */       
/* 1938 */       while (it.hasNext())
/*      */       {
/* 1940 */         UDPPacket packet = (UDPPacket)it.next();
/*      */         
/* 1942 */         if (packet.getCommand() == 4)
/*      */         {
/* 1944 */           if (this.total_tick_count - packet.getSendTickCount() >= MIN_RETRANSMIT_TICKS)
/*      */           {
/* 1946 */             if (this.manager.trace()) {
/* 1947 */               trace(packet.getConnection(), "retransStatsRequest:" + packet.getString());
/*      */             }
/*      */             
/* 1950 */             packet_to_send = packet;
/*      */             
/* 1952 */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1958 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1963 */       if (packet_to_send == null)
/*      */       {
/* 1965 */         byte[] header_bytes = new byte['Ā'];
/*      */         
/* 1967 */         ByteBuffer header = ByteBuffer.wrap(header_bytes);
/*      */         
/* 1969 */         long unack_in_sequence_count = this.current_receive_unack_in_sequence_count;
/*      */         
/* 1971 */         int[] sequences = writeHeaderStart(header, (byte)4, (byte)0);
/*      */         
/* 1973 */         int size = writeHeaderEnd(header, true);
/*      */         
/* 1975 */         byte[] packet_bytes = new byte[size];
/*      */         
/* 1977 */         System.arraycopy(header_bytes, 0, packet_bytes, 0, size);
/*      */         
/* 1979 */         packet_to_send = new UDPPacket(this.lead_connection, sequences, (byte)4, packet_bytes, unack_in_sequence_count);
/*      */         
/* 1981 */         this.transmit_unack_packets.add(packet_to_send);
/*      */         
/* 1983 */         if (this.manager.trace()) {
/* 1984 */           trace(this.lead_connection, "sendStatsRequest");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1989 */     send(packet_to_send);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveStatsRequest(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 1998 */     UDPPacket packet_to_send = null;
/*      */     
/* 2000 */     if (this.manager.trace()) {
/* 2001 */       trace("ReceiveStatsRequest");
/*      */     }
/*      */     
/* 2004 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/* 2008 */       Iterator it = this.transmit_unack_packets.iterator();
/*      */       
/* 2010 */       while (it.hasNext())
/*      */       {
/* 2012 */         UDPPacket packet = (UDPPacket)it.next();
/*      */         
/* 2014 */         if (packet.getCommand() == 5)
/*      */         {
/* 2016 */           if (this.total_tick_count - packet.getSendTickCount() >= MIN_RETRANSMIT_TICKS)
/*      */           {
/* 2018 */             if (this.manager.trace()) {
/* 2019 */               trace(packet.getConnection(), "retransStatsReply:" + packet.getString());
/*      */             }
/*      */             
/* 2022 */             packet_to_send = packet;
/*      */             
/* 2024 */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 2030 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2035 */       if (packet_to_send == null)
/*      */       {
/* 2037 */         byte[] header_bytes = new byte['Ā'];
/*      */         
/* 2039 */         ByteBuffer header = ByteBuffer.wrap(header_bytes);
/*      */         
/* 2041 */         long unack_in_sequence_count = this.current_receive_unack_in_sequence_count;
/*      */         
/* 2043 */         boolean no_retrans = (this.transmit_unack_packets.size() == 0) && (this.receive_out_of_order_packets.size() == 0);
/*      */         
/* 2045 */         int[] sequences = writeHeaderStart(header, (byte)5, (byte)(no_retrans ? 1 : 0));
/*      */         
/* 2047 */         int size = writeHeaderEnd(header, true);
/*      */         
/* 2049 */         byte[] packet_bytes = new byte[size];
/*      */         
/* 2051 */         System.arraycopy(header_bytes, 0, packet_bytes, 0, size);
/*      */         
/* 2053 */         packet_to_send = new UDPPacket(this.lead_connection, sequences, (byte)5, packet_bytes, unack_in_sequence_count);
/*      */         
/* 2055 */         if (no_retrans)
/*      */         {
/* 2057 */           packet_to_send.setAutoRetransmit(false);
/*      */         }
/*      */         
/* 2060 */         this.transmit_unack_packets.add(packet_to_send);
/*      */         
/* 2062 */         if (this.manager.trace()) {
/* 2063 */           trace(this.lead_connection, "sendStatsReply");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2068 */     send(packet_to_send);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveStatsReply(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 2077 */     if (this.manager.trace()) {
/* 2078 */       trace("receiveStatsReply");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendCloseCommand(UDPConnection connection)
/*      */     throws IOException
/*      */   {
/* 2089 */     if (this.crypto_done)
/*      */     {
/*      */       UDPPacket packet_to_send;
/*      */       
/* 2093 */       synchronized (this)
/*      */       {
/* 2095 */         byte[] header_bytes = new byte['Ā'];
/*      */         
/* 2097 */         ByteBuffer header = ByteBuffer.wrap(header_bytes);
/*      */         
/* 2099 */         long unack_in_sequence_count = this.current_receive_unack_in_sequence_count;
/*      */         
/* 2101 */         int[] sequences = writeHeaderStart(header, (byte)3, (byte)0);
/*      */         
/* 2103 */         header.putInt(connection.getID());
/*      */         
/* 2105 */         int size = writeHeaderEnd(header, true);
/*      */         
/* 2107 */         byte[] packet_bytes = new byte[size];
/*      */         
/* 2109 */         System.arraycopy(header_bytes, 0, packet_bytes, 0, size);
/*      */         
/* 2111 */         if (this.manager.trace()) {
/* 2112 */           trace(connection, "sendClose");
/*      */         }
/*      */         
/* 2115 */         packet_to_send = new UDPPacket(this.lead_connection, sequences, (byte)3, packet_bytes, unack_in_sequence_count);
/*      */         
/* 2117 */         this.transmit_unack_packets.add(packet_to_send);
/*      */       }
/*      */       
/* 2120 */       send(packet_to_send);
/*      */     }
/*      */     else
/*      */     {
/* 2124 */       IOException failure = new IOException("Connection failed during setup phase");
/*      */       
/* 2126 */       failed(failure);
/*      */       
/* 2128 */       throw failure;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void receiveCloseCommand(ByteBuffer buffer)
/*      */     throws IOException
/*      */   {
/* 2138 */     int connection_id = buffer.getInt();
/*      */     
/* 2140 */     UDPConnection connection = null;
/*      */     
/* 2142 */     synchronized (this.connections)
/*      */     {
/* 2144 */       if (this.failed)
/*      */       {
/* 2146 */         throw new IOException("Connection set has failed");
/*      */       }
/*      */       
/* 2149 */       connection = (UDPConnection)this.connections.get(new Integer(connection_id));
/*      */     }
/*      */     
/* 2152 */     if (this.manager.trace()) {
/* 2153 */       trace("receiveClose: con=" + (connection == null ? "<null>" : new StringBuilder().append("").append(connection.getID()).toString()));
/*      */     }
/*      */     
/* 2156 */     if (connection != null)
/*      */     {
/* 2158 */       connection.close("Remote has closed the connection");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int[] writeHeaderStart(ByteBuffer buffer, byte command, byte flags)
/*      */     throws IOException
/*      */   {
/* 2190 */     sendTimerBase();
/*      */     
/* 2192 */     this.stats_packets_unique_sent += 1;
/* 2193 */     this.total_packets_unique_sent += 1;
/*      */     
/* 2195 */     int[] sequence_numbers = this.out_seq_generator.getNextSequenceNumber();
/*      */     
/* 2197 */     int seq = sequence_numbers[1];
/*      */     
/* 2199 */     buffer.putInt(sequence_numbers[0]);
/* 2200 */     buffer.putInt(seq);
/* 2201 */     buffer.putInt(sequence_numbers[2]);
/*      */     
/*      */ 
/*      */ 
/* 2205 */     buffer.putShort((short)0);
/*      */     
/* 2207 */     buffer.put((byte)1);
/* 2208 */     buffer.put(flags);
/* 2209 */     buffer.putShort((short)(this.current_timer_base / 10));
/* 2210 */     buffer.put(command);
/*      */     
/* 2212 */     return sequence_numbers;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int writeHeaderEnd(ByteBuffer buffer, boolean randomise_size)
/*      */     throws IOException
/*      */   {
/* 2222 */     if (randomise_size)
/*      */     {
/* 2224 */       int pad = this.random.nextInt(8);
/*      */       
/* 2226 */       for (int i = 0; i < pad; i++)
/*      */       {
/* 2228 */         buffer.put((byte)0);
/*      */       }
/*      */     }
/*      */     
/* 2232 */     short total_length = (short)buffer.position();
/*      */     
/* 2234 */     buffer.position(12);
/*      */     
/* 2236 */     buffer.putShort((short)(total_length + 4));
/*      */     
/*      */ 
/*      */ 
/* 2240 */     byte[] buffer_bytes = buffer.array();
/*      */     
/* 2242 */     SHA1Hasher hasher = new SHA1Hasher();
/*      */     
/* 2244 */     hasher.update(buffer_bytes, 4, 4);
/* 2245 */     hasher.update(buffer_bytes, 12, total_length - 12);
/*      */     
/* 2247 */     byte[] hash = hasher.getDigest();
/*      */     
/* 2249 */     buffer.position(total_length);
/*      */     
/* 2251 */     buffer.put(hash, 0, 4);
/*      */     
/* 2253 */     total_length = (short)(total_length + 4);
/*      */     
/*      */ 
/*      */ 
/* 2257 */     this.header_cipher_out.processBytes(buffer_bytes, 12, total_length - 12, buffer_bytes, 12);
/*      */     
/* 2259 */     if (total_length > 128)
/*      */     {
/* 2261 */       Debug.out("MAX_HEADER exceeded!!!!");
/*      */       
/* 2263 */       throw new IOException("MAX_HEADER exceeded");
/*      */     }
/*      */     
/* 2266 */     return total_length;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int write(UDPConnection connection, ByteBuffer[] buffers, int offset, int length)
/*      */     throws IOException
/*      */   {
/* 2278 */     if (!canWrite(connection))
/*      */     {
/* 2280 */       return 0;
/*      */     }
/*      */     
/* 2283 */     synchronized (this.connection_writers)
/*      */     {
/* 2285 */       int size = this.connection_writers.size();
/*      */       
/* 2287 */       if (size == 0)
/*      */       {
/* 2289 */         this.connection_writers.add(connection);
/*      */       }
/* 2291 */       else if ((this.connection_writers.size() != 1) || (this.connection_writers.get(0) != connection))
/*      */       {
/*      */ 
/*      */ 
/* 2295 */         this.connection_writers.remove(connection);
/*      */         
/* 2297 */         this.connection_writers.addLast(connection);
/*      */       }
/*      */     }
/*      */     
/* 2301 */     if (this.total_packets_sent == 0)
/*      */     {
/* 2303 */       return sendCrypto(buffers, offset, length);
/*      */     }
/*      */     
/*      */ 
/* 2307 */     return sendDataCommand(connection, buffers, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean canWrite(UDPConnection connection)
/*      */   {
/* 2315 */     if (!this.crypto_done)
/*      */     {
/* 2317 */       if (connection != this.lead_connection)
/*      */       {
/* 2319 */         return false;
/*      */       }
/*      */       
/* 2322 */       if (this.total_packets_sent > 0)
/*      */       {
/* 2324 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 2328 */     boolean space = this.transmit_unack_packets.size() < 10;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2341 */     return space;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void close(UDPConnection connection, String reason)
/*      */   {
/* 2349 */     if (this.manager.trace()) {
/* 2350 */       trace(connection, "close: " + reason);
/*      */     }
/*      */     
/*      */     boolean found;
/*      */     
/* 2355 */     synchronized (this.connections)
/*      */     {
/* 2357 */       found = this.connections.containsValue(connection);
/*      */     }
/*      */     
/* 2360 */     if (found) {
/*      */       try
/*      */       {
/* 2363 */         sendCloseCommand(connection);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2367 */         failed(e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2373 */     connection.poll();
/*      */     
/* 2375 */     this.manager.remove(this, connection);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void failed(UDPConnection connection, Throwable reason)
/*      */   {
/* 2383 */     if (this.manager.trace()) {
/* 2384 */       trace(connection, "Failed: " + Debug.getNestedExceptionMessage(reason));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2389 */     connection.poll();
/*      */     
/* 2391 */     this.manager.remove(this, connection);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void failed(Throwable e)
/*      */   {
/* 2398 */     List conns = null;
/*      */     
/* 2400 */     synchronized (this.connections)
/*      */     {
/* 2402 */       if (!this.failed)
/*      */       {
/* 2404 */         if (this.manager.trace()) {
/* 2405 */           trace("Connection set failed: " + Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */         
/* 2408 */         this.failed = true;
/*      */         
/* 2410 */         conns = new ArrayList(this.connections.values());
/*      */       }
/*      */     }
/*      */     
/* 2414 */     if (conns != null)
/*      */     {
/* 2416 */       for (int i = 0; i < conns.size(); i++) {
/*      */         try
/*      */         {
/* 2419 */           ((UDPConnection)conns.get(i)).failed(e);
/*      */         }
/*      */         catch (Throwable f)
/*      */         {
/* 2423 */           Debug.printStackTrace(f);
/*      */         }
/*      */       }
/*      */       
/* 2427 */       this.manager.failed(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean hasFailed()
/*      */   {
/* 2434 */     return this.failed;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void removed()
/*      */   {
/* 2440 */     logStats();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static void forDocumentation()
/*      */   {
/* 2448 */     PRUDPPacketReply.registerDecoders(new HashMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int cipherInt(RC4Engine cipher, int i)
/*      */   {
/* 2458 */     byte[] bytes = intToBytes(i);
/*      */     
/* 2460 */     cipher.processBytes(bytes, 0, bytes.length, bytes, 0);
/*      */     
/* 2462 */     return bytesToInt(bytes, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int bytesToInt(byte[] bytes, int offset)
/*      */   {
/* 2470 */     int res = bytes[(offset++)] << 24 & 0xFF000000 | bytes[(offset++)] << 16 & 0xFF0000 | bytes[(offset++)] << 8 & 0xFF00 | bytes[(offset++)] & 0xFF;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2475 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected byte[] intToBytes(int i)
/*      */   {
/* 2482 */     byte[] res = { (byte)(i >> 24), (byte)(i >> 16), (byte)(i >> 8), (byte)i };
/*      */     
/* 2484 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected long bytesToLong(byte[] bytes)
/*      */   {
/* 2491 */     return bytesToLong(bytes, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long bytesToLong(byte[] bytes, int offset)
/*      */   {
/* 2499 */     long i1 = bytes[(offset++)] << 24 & 0xFF000000 | bytes[(offset++)] << 16 & 0xFF0000 | bytes[(offset++)] << 8 & 0xFF00 | bytes[(offset++)] & 0xFF;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2504 */     long i2 = bytes[(offset++)] << 24 & 0xFF000000 | bytes[(offset++)] << 16 & 0xFF0000 | bytes[(offset++)] << 8 & 0xFF00 | bytes[(offset++)] & 0xFF;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2509 */     long res = i1 << 32 | i2;
/*      */     
/* 2511 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getName()
/*      */   {
/* 2517 */     return "loc=" + this.local_port + " - " + this.remote_address;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void logStats()
/*      */   {
/* 2523 */     if (Logger.isEnabled())
/*      */     {
/* 2525 */       synchronized (this) {
/* 2526 */         String str = "sent: tot=" + this.total_packets_sent + ",uni=" + this.total_packets_unique_sent + ",ds=" + this.total_data_sent + ",dr=" + this.total_data_resent + ",ps=" + this.total_protocol_sent + ",pr=" + this.total_protocol_resent + ",rt=" + this.total_packets_resent_via_timer + ",ra=" + this.total_packets_resent_via_ack;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 2531 */         str = str + " recv: tot=" + this.total_packets_received + ",uni=" + this.total_packets_unique_received + ",du=" + this.total_packets_duplicates + ",oo=" + this.total_packets_out_of_order;
/*      */         
/*      */ 
/* 2534 */         str = str + " timer=" + this.current_timer_base + ",adj=" + this.timer_is_adjusting;
/*      */         
/* 2536 */         Logger.log(new LogEvent(LOGID, "UDP " + getName() + " - " + str));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void trace(String str)
/*      */   {
/* 2545 */     if (this.manager.trace())
/*      */     {
/* 2547 */       this.manager.trace("UDP " + getName() + ": " + str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void trace(UDPConnection connection, String str)
/*      */   {
/* 2556 */     if (this.manager.trace())
/*      */     {
/* 2558 */       this.manager.trace("UDP " + getName() + " (" + connection.getID() + "): " + str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class SequenceGenerator
/*      */   {
/*      */     private final Random generator;
/*      */     
/*      */     private final RC4Engine cipher;
/*      */     
/*      */     private final boolean in;
/*      */     private final int[] seq_memory;
/*      */     private final int[] alt_seq_memory;
/*      */     private int seq_memory_pos;
/* 2573 */     private int debug_seq_in_next = UDPConnectionSet.this.outgoing ? 0 : 1000000;
/* 2574 */     private int debug_seq_out_next = UDPConnectionSet.this.outgoing ? 1000000 : 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected SequenceGenerator(Random _generator, RC4Engine _cipher, boolean _in)
/*      */     {
/* 2582 */       this.generator = _generator;
/* 2583 */       this.cipher = _cipher;
/* 2584 */       this.in = _in;
/*      */       
/* 2586 */       this.seq_memory = new int[UDPConnectionSet.MAX_SEQ_MEMORY];
/* 2587 */       this.alt_seq_memory = new int[UDPConnectionSet.MAX_SEQ_MEMORY];
/*      */       
/* 2589 */       Arrays.fill(this.seq_memory, -1);
/* 2590 */       Arrays.fill(this.alt_seq_memory, -1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected synchronized int[] getNextSequenceNumber()
/*      */     {
/* 2608 */       int mask = 63488;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       for (;;)
/*      */       {
/* 2637 */         int seq1 = this.generator.nextInt();
/* 2638 */         int seq2 = this.generator.nextInt();
/* 2639 */         int seq3 = this.generator.nextInt();
/* 2640 */         int seq4 = this.generator.nextInt();
/*      */         
/* 2642 */         seq1 = UDPConnectionSet.this.cipherInt(this.cipher, seq1);
/* 2643 */         seq2 = UDPConnectionSet.this.cipherInt(this.cipher, seq2);
/* 2644 */         seq3 = UDPConnectionSet.this.cipherInt(this.cipher, seq3);
/* 2645 */         seq4 = UDPConnectionSet.this.cipherInt(this.cipher, seq4);
/*      */         
/*      */ 
/* 2648 */         if (((seq1 & 0xF800) != 0) && (seq2 != -1) && ((seq3 & 0xF800) != 0))
/*      */         {
/* 2650 */           if (((seq4 & 0xFFFF0000) != 0) && ((seq4 & 0xFFFF) != 0))
/*      */           {
/* 2652 */             boolean bad = false;
/*      */             
/* 2654 */             for (int i = 0; i < UDPConnectionSet.MAX_SEQ_MEMORY; i++)
/*      */             {
/* 2656 */               if ((this.seq_memory[i] == seq2) || (this.alt_seq_memory[i] == seq4))
/*      */               {
/* 2658 */                 bad = true;
/*      */                 
/* 2660 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 2664 */             if (!bad)
/*      */             {
/* 2666 */               this.seq_memory[this.seq_memory_pos] = seq2;
/*      */               
/* 2668 */               this.alt_seq_memory[(this.seq_memory_pos++)] = seq4;
/*      */               
/* 2670 */               if (this.seq_memory_pos == UDPConnectionSet.MAX_SEQ_MEMORY)
/*      */               {
/* 2672 */                 this.seq_memory_pos = 0;
/*      */               }
/*      */               
/* 2675 */               return new int[] { seq1, seq2, seq3, seq4 };
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected boolean isValidAlterativeSequence(int seq)
/*      */     {
/* 2686 */       for (int i = 0; i < UDPConnectionSet.MAX_SEQ_MEMORY; i++)
/*      */       {
/* 2688 */         if (this.alt_seq_memory[i] == seq)
/*      */         {
/* 2690 */           return true;
/*      */         }
/*      */       }
/*      */       
/* 2694 */       return false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/udp/UDPConnectionSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */