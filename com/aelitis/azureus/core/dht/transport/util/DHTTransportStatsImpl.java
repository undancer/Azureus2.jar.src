/*     */ package com.aelitis.azureus.core.dht.transport.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketRequest;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.Arrays;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
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
/*     */ public abstract class DHTTransportStatsImpl
/*     */   implements DHTTransportStats
/*     */ {
/*     */   private static final int RTT_HISTORY = 50;
/*     */   private final byte protocol_version;
/*  48 */   private long[] pings = new long[4];
/*  49 */   private long[] find_nodes = new long[4];
/*  50 */   private long[] find_values = new long[4];
/*  51 */   private long[] stores = new long[4];
/*  52 */   private final long[] stats = new long[4];
/*  53 */   private long[] data = new long[4];
/*  54 */   private long[] key_blocks = new long[4];
/*  55 */   private long[] store_queries = new long[4];
/*     */   
/*  57 */   private long[] aliens = new long[7];
/*     */   
/*     */   private long incoming_requests;
/*     */   
/*     */   private long outgoing_requests;
/*     */   
/*     */   private long incoming_version_requests;
/*     */   private final long[] incoming_request_versions;
/*     */   private long outgoing_version_requests;
/*     */   private final long[] outgoing_request_versions;
/*     */   private static final int SKEW_VALUE_MAX = 256;
/*  68 */   private final int[] skew_values = new int['Ā'];
/*  69 */   private int skew_pos = 0;
/*     */   
/*     */   private long last_skew_average;
/*     */   private long last_skew_average_time;
/*  73 */   private final BloomFilter skew_originator_bloom = BloomFilterFactory.createRotating(BloomFilterFactory.createAddOnly(1024), 2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  78 */   private final int[] rtt_history = new int[50];
/*     */   
/*     */   private int rtt_history_pos;
/*     */   
/*     */ 
/*     */   protected DHTTransportStatsImpl(byte _protocol_version)
/*     */   {
/*  85 */     this.protocol_version = _protocol_version;
/*     */     
/*  87 */     this.incoming_request_versions = new long[this.protocol_version + 1];
/*  88 */     this.outgoing_request_versions = new long[this.protocol_version + 1];
/*     */     
/*  90 */     Arrays.fill(this.skew_values, Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */ 
/*     */   protected byte getProtocolVersion()
/*     */   {
/*  96 */     return this.protocol_version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void receivedRTT(int rtt)
/*     */   {
/* 103 */     if ((rtt <= 0) || (rtt > 120000))
/*     */     {
/* 105 */       return;
/*     */     }
/*     */     
/* 108 */     synchronized (this.rtt_history)
/*     */     {
/* 110 */       this.rtt_history[(this.rtt_history_pos++ % 50)] = rtt;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int[] getRTTHistory()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 253	com/aelitis/azureus/core/dht/transport/util/DHTTransportStatsImpl:rtt_history	[I
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 253	com/aelitis/azureus/core/dht/transport/util/DHTTransportStatsImpl:rtt_history	[I
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: areturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #117	-> byte code offset #0
/*     */     //   Java source line #121	-> byte code offset #7
/*     */     //   Java source line #122	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	DHTTransportStatsImpl
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   public void add(DHTTransportStatsImpl other)
/*     */   {
/* 129 */     add(this.pings, other.pings);
/* 130 */     add(this.find_nodes, other.find_nodes);
/* 131 */     add(this.find_values, other.find_values);
/* 132 */     add(this.stores, other.stores);
/* 133 */     add(this.stats, other.stats);
/* 134 */     add(this.data, other.data);
/* 135 */     add(this.key_blocks, other.key_blocks);
/* 136 */     add(this.store_queries, other.store_queries);
/* 137 */     add(this.aliens, other.aliens);
/*     */     
/* 139 */     this.incoming_requests += other.incoming_requests;
/* 140 */     this.outgoing_requests += other.outgoing_requests;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void add(long[] a, long[] b)
/*     */   {
/* 148 */     for (int i = 0; i < a.length; i++) {
/* 149 */       a[i] += b[i];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void snapshotSupport(DHTTransportStatsImpl clone)
/*     */   {
/* 157 */     clone.pings = ((long[])this.pings.clone());
/* 158 */     clone.find_nodes = ((long[])this.find_nodes.clone());
/* 159 */     clone.find_values = ((long[])this.find_values.clone());
/* 160 */     clone.stores = ((long[])this.stores.clone());
/* 161 */     clone.data = ((long[])this.data.clone());
/* 162 */     clone.key_blocks = ((long[])this.key_blocks.clone());
/* 163 */     clone.store_queries = ((long[])this.store_queries.clone());
/* 164 */     clone.aliens = ((long[])this.aliens.clone());
/*     */     
/* 166 */     clone.incoming_requests = this.incoming_requests;
/* 167 */     clone.outgoing_requests = this.outgoing_requests;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pingSent(DHTUDPPacketRequest request)
/*     */   {
/* 175 */     this.pings[0] += 1L;
/*     */     
/* 177 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */ 
/*     */   public void pingOK()
/*     */   {
/* 183 */     this.pings[1] += 1L;
/*     */   }
/*     */   
/*     */   public void pingFailed()
/*     */   {
/* 188 */     this.pings[2] += 1L;
/*     */   }
/*     */   
/*     */   public void pingReceived()
/*     */   {
/* 193 */     this.pings[3] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getPings()
/*     */   {
/* 199 */     return this.pings;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void keyBlockSent(DHTUDPPacketRequest request)
/*     */   {
/* 208 */     this.key_blocks[0] += 1L;
/*     */     
/* 210 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */   public void keyBlockOK()
/*     */   {
/* 215 */     this.key_blocks[1] += 1L;
/*     */   }
/*     */   
/*     */   public void keyBlockFailed()
/*     */   {
/* 220 */     this.key_blocks[2] += 1L;
/*     */   }
/*     */   
/*     */   public void keyBlockReceived()
/*     */   {
/* 225 */     this.key_blocks[3] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getKeyBlocks()
/*     */   {
/* 231 */     return this.key_blocks;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void queryStoreSent(DHTUDPPacketRequest request)
/*     */   {
/* 240 */     this.store_queries[0] += 1L;
/*     */     
/* 242 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */ 
/*     */   public void queryStoreOK()
/*     */   {
/* 248 */     this.store_queries[1] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public void queryStoreFailed()
/*     */   {
/* 254 */     this.store_queries[2] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public void queryStoreReceived()
/*     */   {
/* 260 */     this.store_queries[3] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getQueryStores()
/*     */   {
/* 266 */     return this.store_queries;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void findNodeSent(DHTUDPPacketRequest request)
/*     */   {
/* 275 */     this.find_nodes[0] += 1L;
/*     */     
/* 277 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */   public void findNodeOK()
/*     */   {
/* 282 */     this.find_nodes[1] += 1L;
/*     */   }
/*     */   
/*     */   public void findNodeFailed()
/*     */   {
/* 287 */     this.find_nodes[2] += 1L;
/*     */   }
/*     */   
/*     */   public void findNodeReceived()
/*     */   {
/* 292 */     this.find_nodes[3] += 1L;
/*     */   }
/*     */   
/*     */   public long[] getFindNodes()
/*     */   {
/* 297 */     return this.find_nodes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void findValueSent(DHTUDPPacketRequest request)
/*     */   {
/* 306 */     this.find_values[0] += 1L;
/*     */     
/* 308 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */   public void findValueOK()
/*     */   {
/* 313 */     this.find_values[1] += 1L;
/*     */   }
/*     */   
/*     */   public void findValueFailed()
/*     */   {
/* 318 */     this.find_values[2] += 1L;
/*     */   }
/*     */   
/*     */   public void findValueReceived()
/*     */   {
/* 323 */     this.find_values[3] += 1L;
/*     */   }
/*     */   
/*     */   public long[] getFindValues()
/*     */   {
/* 328 */     return this.find_values;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void storeSent(DHTUDPPacketRequest request)
/*     */   {
/* 337 */     this.stores[0] += 1L;
/*     */     
/* 339 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */ 
/*     */   public void storeOK()
/*     */   {
/* 345 */     this.stores[1] += 1L;
/*     */   }
/*     */   
/*     */   public void storeFailed()
/*     */   {
/* 350 */     this.stores[2] += 1L;
/*     */   }
/*     */   
/*     */   public void storeReceived()
/*     */   {
/* 355 */     this.stores[3] += 1L;
/*     */   }
/*     */   
/*     */   public long[] getStores()
/*     */   {
/* 360 */     return this.stores;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void statsSent(DHTUDPPacketRequest request)
/*     */   {
/* 368 */     this.stats[0] += 1L;
/*     */     
/* 370 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */ 
/*     */   public void statsOK()
/*     */   {
/* 376 */     this.stats[1] += 1L;
/*     */   }
/*     */   
/*     */   public void statsFailed()
/*     */   {
/* 381 */     this.stats[2] += 1L;
/*     */   }
/*     */   
/*     */   public void statsReceived()
/*     */   {
/* 386 */     this.stats[3] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dataSent(DHTUDPPacketRequest request)
/*     */   {
/* 395 */     this.data[0] += 1L;
/*     */     
/* 397 */     outgoingRequestSent(request);
/*     */   }
/*     */   
/*     */ 
/*     */   public void dataOK()
/*     */   {
/* 403 */     this.data[1] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public void dataFailed()
/*     */   {
/* 409 */     this.data[2] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public void dataReceived()
/*     */   {
/* 415 */     this.data[3] += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getData()
/*     */   {
/* 421 */     return this.data;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void outgoingRequestSent(DHTUDPPacketRequest request)
/*     */   {
/* 428 */     this.outgoing_requests += 1L;
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
/*     */   public void incomingRequestReceived(DHTUDPPacketRequest request, boolean alien)
/*     */   {
/* 476 */     this.incoming_requests += 1L;
/*     */     
/* 478 */     if ((alien) && (request != null))
/*     */     {
/*     */ 
/*     */ 
/* 482 */       int type = request.getAction();
/*     */       
/* 484 */       if (type == 1028)
/*     */       {
/* 486 */         this.aliens[0] += 1L;
/*     */       }
/* 488 */       else if (type == 1030)
/*     */       {
/* 490 */         this.aliens[1] += 1L;
/*     */       }
/* 492 */       else if (type == 1024)
/*     */       {
/* 494 */         this.aliens[2] += 1L;
/*     */       }
/* 496 */       else if (type == 1034)
/*     */       {
/* 498 */         this.aliens[3] += 1L;
/*     */       }
/* 500 */       else if (type == 1026)
/*     */       {
/* 502 */         this.aliens[4] += 1L;
/*     */       }
/* 504 */       else if (type == 1036)
/*     */       {
/* 506 */         this.aliens[5] += 1L;
/*     */       }
/* 508 */       else if (type == 1038)
/*     */       {
/* 510 */         this.aliens[6] += 1L;
/*     */       }
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
/*     */   public long[] getAliens()
/*     */   {
/* 558 */     return this.aliens;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getIncomingRequests()
/*     */   {
/* 564 */     return this.incoming_requests;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordSkew(InetSocketAddress originator_address, long skew)
/*     */   {
/* 572 */     byte[] bytes = AddressUtils.getAddressBytes(originator_address);
/*     */     
/* 574 */     if (this.skew_originator_bloom.contains(bytes))
/*     */     {
/*     */ 
/*     */ 
/* 578 */       return;
/*     */     }
/*     */     
/* 581 */     this.skew_originator_bloom.add(bytes);
/*     */     
/*     */ 
/*     */ 
/* 585 */     int i_skew = skew < 2147483647L ? (int)skew : 2147483646;
/*     */     
/*     */ 
/*     */ 
/* 589 */     int pos = this.skew_pos;
/*     */     
/* 591 */     this.skew_values[(pos++)] = i_skew;
/*     */     
/* 593 */     if (pos == 256)
/*     */     {
/* 595 */       pos = 0;
/*     */     }
/*     */     
/* 598 */     this.skew_pos = pos;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSkewAverage()
/*     */   {
/* 604 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 606 */     if ((now < this.last_skew_average_time) || (now - this.last_skew_average_time > 30000L))
/*     */     {
/*     */ 
/* 609 */       int[] values = (int[])this.skew_values.clone();
/*     */       
/* 611 */       int pos = this.skew_pos;
/*     */       
/*     */       int num_values;
/*     */       int num_values;
/* 615 */       if (values[pos] == Integer.MAX_VALUE)
/*     */       {
/* 617 */         num_values = pos;
/*     */       }
/*     */       else {
/* 620 */         num_values = 256;
/*     */       }
/*     */       
/* 623 */       Arrays.sort(values, 0, num_values);
/*     */       
/*     */ 
/*     */ 
/* 627 */       int start = num_values / 3;
/* 628 */       int end = 2 * num_values / 3;
/*     */       
/* 630 */       int entries = end - start;
/*     */       
/* 632 */       if (entries < 5)
/*     */       {
/* 634 */         this.last_skew_average = 0L;
/*     */       }
/*     */       else
/*     */       {
/* 638 */         long total = 0L;
/*     */         
/* 640 */         for (int i = start; i < end; i++)
/*     */         {
/* 642 */           total += values[i];
/*     */         }
/*     */         
/* 645 */         this.last_skew_average = (total / entries);
/*     */       }
/*     */       
/* 648 */       this.last_skew_average_time = now;
/*     */     }
/*     */     
/* 651 */     return this.last_skew_average;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 657 */     return "ping:" + getString(this.pings) + "," + "store:" + getString(this.stores) + "," + "node:" + getString(this.find_nodes) + "," + "value:" + getString(this.find_values) + "," + "stats:" + getString(this.stats) + "," + "data:" + getString(this.data) + "," + "kb:" + getString(this.key_blocks) + "," + "incoming:" + this.incoming_requests + "," + "alien:" + getString(this.aliens);
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
/*     */ 
/*     */   protected String getString(long[] x)
/*     */   {
/* 672 */     String str = "";
/*     */     
/* 674 */     for (int i = 0; i < x.length; i++)
/*     */     {
/* 676 */       str = str + (i == 0 ? "" : ",") + x[i];
/*     */     }
/*     */     
/* 679 */     return str;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/util/DHTTransportStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */