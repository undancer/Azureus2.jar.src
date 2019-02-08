/*     */ package com.aelitis.azureus.core.peermanager.peerdb;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*     */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class PeerDatabase
/*     */ {
/*     */   private static final int STARTUP_MIN_REBUILD_WAIT_TIME = 10000;
/*     */   private static final int STARTUP_MILLIS = 120000;
/*     */   private static final int MIN_REBUILD_WAIT_TIME = 60000;
/*     */   private static final int MAX_DISCOVERED_PEERS = 500;
/*     */   private static final int BLOOM_ROTATION_PERIOD = 420000;
/*     */   private static final int BLOOM_FILTER_SIZE = 10000;
/*  43 */   private final long start_time = SystemTime.getMonotonousTime();
/*     */   
/*  45 */   private final HashMap peer_connections = new HashMap();
/*     */   
/*  47 */   private final TreeSet<PeerItem> discovered_peers = new TreeSet(new Comparator()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public int compare(PeerItem o1, PeerItem o2)
/*     */     {
/*     */ 
/*     */ 
/*  56 */       long res = o2.getPriority() - o1.getPriority();
/*     */       
/*  58 */       if (res == 0L)
/*     */       {
/*  60 */         res = o1.compareTo(o2);
/*     */       }
/*     */       
/*  63 */       return res > 0L ? 1 : res < 0L ? -1 : 0;
/*     */     }
/*  47 */   });
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
/*  67 */   private final TreeSet<PeerItem> discovered_peers_non_pub = new TreeSet(new Comparator()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public int compare(PeerItem o1, PeerItem o2)
/*     */     {
/*     */ 
/*     */ 
/*  76 */       long res = o2.getPriority() - o1.getPriority();
/*     */       
/*  78 */       if (res == 0L)
/*     */       {
/*  80 */         res = o1.compareTo(o2);
/*     */       }
/*     */       
/*  83 */       return res > 0L ? 1 : res < 0L ? -1 : 0;
/*     */     }
/*  67 */   });
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
/*  87 */   private final AEMonitor map_mon = new AEMonitor("PeerDatabase");
/*     */   
/*  89 */   private PeerItem[] cached_peer_popularities = null;
/*  90 */   private int popularity_pos = 0;
/*  91 */   private int popularity_pos_non_pub = 0;
/*  92 */   private long last_rebuild_time = -2147483648L;
/*  93 */   private long last_rotation_time = -2147483648L;
/*     */   
/*     */   private PeerItem self_peer;
/*     */   
/*  97 */   private BloomFilter filter_one = null;
/*  98 */   private BloomFilter filter_two = BloomFilterFactory.createAddOnly(10000);
/*     */   
/*     */ 
/*     */   private long pex_count_last_time;
/*     */   
/*     */ 
/*     */   private int pex_count_last;
/*     */   
/*     */ 
/*     */   private int pex_used_count;
/*     */   
/*     */ 
/*     */   private int total_peers_returned;
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerExchangerItem registerPeerConnection(PeerItem base_peer_item, PeerExchangerItem.Helper helper)
/*     */   {
/*     */     try
/*     */     {
/* 118 */       this.map_mon.enter();
/* 119 */       PeerExchangerItem new_connection = new PeerExchangerItem(this, base_peer_item, helper);
/*     */       
/*     */ 
/* 122 */       for (Iterator it = this.peer_connections.entrySet().iterator(); it.hasNext();) {
/* 123 */         Map.Entry entry = (Map.Entry)it.next();
/* 124 */         PeerItem old_key = (PeerItem)entry.getKey();
/* 125 */         PeerExchangerItem old_connection = (PeerExchangerItem)entry.getValue();
/*     */         
/* 127 */         if ((!old_connection.getHelper().isSeed()) || (!new_connection.getHelper().isSeed()))
/*     */         {
/*     */ 
/*     */ 
/* 131 */           old_connection.notifyAdded(base_peer_item);
/* 132 */           new_connection.notifyAdded(old_key);
/*     */         }
/*     */       }
/* 135 */       this.peer_connections.put(base_peer_item, new_connection);
/* 136 */       return new_connection;
/*     */     } finally {
/* 138 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void deregisterPeerConnection(PeerItem base_peer_key) {
/* 143 */     try { this.map_mon.enter();
/* 144 */       this.peer_connections.remove(base_peer_key);
/*     */       
/*     */ 
/* 147 */       for (it = this.peer_connections.values().iterator(); it.hasNext();) {
/* 148 */         PeerExchangerItem old_connection = (PeerExchangerItem)it.next();
/*     */         
/*     */ 
/* 151 */         old_connection.notifyDropped(base_peer_key);
/*     */       }
/*     */     } finally { Iterator it;
/* 154 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void seedStatusChanged(PeerExchangerItem item)
/*     */   {
/* 163 */     if (item.getHelper().isSeed()) {
/*     */       try
/*     */       {
/* 166 */         this.map_mon.enter();
/*     */         
/* 168 */         for (it = this.peer_connections.values().iterator(); it.hasNext();)
/*     */         {
/* 170 */           PeerExchangerItem connection = (PeerExchangerItem)it.next();
/*     */           
/* 172 */           if ((connection != item) && (connection.getHelper().isSeed()))
/*     */           {
/*     */ 
/*     */ 
/* 176 */             connection.notifyDropped(item.getBasePeer());
/*     */             
/* 178 */             item.notifyDropped(connection.getBasePeer());
/*     */           }
/*     */         }
/*     */       } finally {
/*     */         Iterator it;
/* 183 */         this.map_mon.exit();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDiscoveredPeer(PeerItem peer)
/*     */   {
/*     */     try
/*     */     {
/* 194 */       this.map_mon.enter();
/* 195 */       for (Iterator it = this.peer_connections.values().iterator(); it.hasNext();) {
/* 196 */         PeerExchangerItem connection = (PeerExchangerItem)it.next();
/* 197 */         if (connection.isConnectedToPeer(peer))
/*     */           return;
/*     */       }
/* 200 */       if (!this.discovered_peers.contains(peer)) {
/* 201 */         this.discovered_peers.add(peer);
/*     */         
/* 203 */         int max_cache_size = PeerUtils.MAX_CONNECTIONS_PER_TORRENT * 2;
/* 204 */         if ((max_cache_size < 1) || (max_cache_size > 500)) { max_cache_size = 500;
/*     */         }
/* 206 */         while (this.discovered_peers.size() > max_cache_size)
/*     */         {
/* 208 */           Iterator<PeerItem> it = this.discovered_peers.iterator();
/* 209 */           it.next();
/* 210 */           it.remove();
/*     */         }
/*     */         
/* 213 */         if (peer.getNetwork() != "Public")
/*     */         {
/* 215 */           this.discovered_peers_non_pub.add(peer);
/*     */           
/* 217 */           while (this.discovered_peers_non_pub.size() > max_cache_size)
/*     */           {
/* 219 */             Iterator<PeerItem> it = this.discovered_peers_non_pub.iterator();
/* 220 */             it.next();
/* 221 */             it.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally {
/* 226 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSelfPeer(PeerItem self)
/*     */   {
/* 234 */     this.self_peer = self;
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
/*     */   public PeerItem getSelfPeer()
/*     */   {
/* 252 */     return this.self_peer;
/*     */   }
/*     */   
/*     */   public PeerItem[] getDiscoveredPeers()
/*     */   {
/*     */     try
/*     */     {
/* 259 */       this.map_mon.enter();
/*     */       
/* 261 */       return (PeerItem[])this.discovered_peers.toArray(new PeerItem[this.discovered_peers.size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 265 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerItem[] getDiscoveredPeers(String address)
/*     */   {
/* 273 */     List<PeerItem> result = null;
/*     */     try
/*     */     {
/* 276 */       this.map_mon.enter();
/*     */       
/* 278 */       Iterator<PeerItem> it = this.discovered_peers.iterator();
/*     */       
/* 280 */       while (it.hasNext())
/*     */       {
/* 282 */         PeerItem peer = (PeerItem)it.next();
/*     */         
/* 284 */         if (peer.getIP().equals(address))
/*     */         {
/* 286 */           if (result == null)
/*     */           {
/* 288 */             result = new ArrayList();
/*     */           }
/*     */           
/* 291 */           result.add(peer);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 296 */       this.map_mon.exit();
/*     */     }
/*     */     
/* 299 */     if (result == null)
/*     */     {
/* 301 */       return new PeerItem[0];
/*     */     }
/*     */     
/*     */ 
/* 305 */     return (PeerItem[])result.toArray(new PeerItem[result.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getDiscoveredPeerCount()
/*     */   {
/*     */     try
/*     */     {
/* 314 */       this.map_mon.enter();
/*     */       
/* 316 */       return this.discovered_peers.size();
/*     */     }
/*     */     finally
/*     */     {
/* 320 */       this.map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeerItem getNextOptimisticConnectPeer(boolean non_public)
/*     */   {
/* 330 */     PeerItem item = getNextOptimisticConnectPeer(non_public, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 339 */     return item;
/*     */   }
/*     */   
/*     */   private PeerItem getNextOptimisticConnectPeer(boolean non_public, int recursion_count) {
/* 343 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 345 */     boolean starting_up = now - this.start_time <= 120000L;
/*     */     
/* 347 */     PeerItem peer = null;
/* 348 */     boolean discovered_peer = false;
/* 349 */     boolean tried_pex = false;
/*     */     
/* 351 */     if ((starting_up) && (this.total_peers_returned % 5 == 0))
/*     */     {
/*     */ 
/*     */ 
/* 355 */       peer = getPeerFromPEX(now, starting_up, non_public);
/*     */       
/* 357 */       tried_pex = true;
/*     */     }
/*     */     
/* 360 */     if (peer == null)
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 365 */         this.map_mon.enter();
/*     */         
/* 367 */         if (!this.discovered_peers.isEmpty())
/*     */         {
/* 369 */           if (non_public)
/*     */           {
/* 371 */             if (!this.discovered_peers_non_pub.isEmpty())
/*     */             {
/* 373 */               Iterator<PeerItem> it = this.discovered_peers_non_pub.iterator();
/*     */               
/* 375 */               peer = (PeerItem)it.next();
/*     */               
/* 377 */               it.remove();
/*     */               
/* 379 */               discovered_peer = true;
/*     */               
/* 381 */               this.discovered_peers.remove(peer);
/*     */             }
/*     */           }
/*     */           else {
/* 385 */             Iterator<PeerItem> it = this.discovered_peers.iterator();
/*     */             
/* 387 */             peer = (PeerItem)it.next();
/*     */             
/* 389 */             it.remove();
/*     */             
/* 391 */             discovered_peer = true;
/*     */             
/* 393 */             if (peer.getNetwork() != "Public")
/*     */             {
/* 395 */               this.discovered_peers_non_pub.remove(peer);
/*     */             }
/*     */           }
/*     */         }
/*     */       } finally {
/* 400 */         this.map_mon.exit();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 406 */     if ((peer == null) && (!tried_pex))
/*     */     {
/* 408 */       peer = getPeerFromPEX(now, starting_up, non_public);
/*     */     }
/*     */     
/*     */ 
/* 412 */     if (peer != null)
/*     */     {
/*     */ 
/* 415 */       long diff = now - this.last_rotation_time;
/*     */       
/* 417 */       if (diff > 420000L) {
/* 418 */         this.filter_one = this.filter_two;
/* 419 */         this.filter_two = BloomFilterFactory.createAddOnly(10000);
/* 420 */         this.last_rotation_time = now;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 425 */       boolean already_recorded = false;
/*     */       
/* 427 */       byte[] peer_serialisation = peer.getSerialization();
/*     */       
/* 429 */       if ((this.filter_one.contains(peer_serialisation)) && (recursion_count < 100))
/*     */       {
/*     */ 
/*     */ 
/* 433 */         PeerItem next_peer = getNextOptimisticConnectPeer(non_public, recursion_count + 1);
/*     */         
/* 435 */         if (next_peer != null)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 440 */           if (discovered_peer) {
/*     */             try {
/* 442 */               this.map_mon.enter();
/*     */               
/* 444 */               this.discovered_peers.add(peer);
/*     */               
/* 446 */               if (peer.getNetwork() != "Public")
/*     */               {
/* 448 */                 this.discovered_peers_non_pub.add(peer);
/*     */               }
/*     */             }
/*     */             finally {
/* 452 */               this.map_mon.exit();
/*     */             }
/*     */           }
/*     */           
/* 456 */           peer = next_peer;
/*     */           
/* 458 */           already_recorded = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 463 */       if (!already_recorded)
/*     */       {
/* 465 */         this.filter_one.add(peer_serialisation);
/* 466 */         this.filter_two.add(peer_serialisation);
/*     */       }
/*     */     }
/*     */     
/* 470 */     if ((recursion_count == 0) && (peer != null))
/*     */     {
/* 472 */       this.total_peers_returned += 1;
/*     */     }
/*     */     
/* 475 */     return peer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PeerItem getPeerFromPEX(long now, boolean starting_up, boolean non_public)
/*     */   {
/* 486 */     if ((this.cached_peer_popularities == null) || (this.popularity_pos == this.cached_peer_popularities.length)) {
/* 487 */       this.cached_peer_popularities = null;
/* 488 */       long time_since_rebuild = now - this.last_rebuild_time;
/*     */       
/* 490 */       if (time_since_rebuild > (starting_up ? 10000 : 60000)) {
/* 491 */         this.cached_peer_popularities = getExchangedPeersSortedByLeastPopularFirst();
/* 492 */         this.popularity_pos = 0;
/* 493 */         this.popularity_pos_non_pub = 0;
/* 494 */         this.last_rebuild_time = now;
/*     */       }
/*     */     }
/*     */     PeerItem peer;
/* 498 */     if ((this.cached_peer_popularities != null) && (this.cached_peer_popularities.length > 0))
/*     */     {
/* 500 */       if (non_public)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 507 */         PeerItem peer = null;
/*     */         
/* 509 */         while (this.popularity_pos_non_pub < this.cached_peer_popularities.length)
/*     */         {
/* 511 */           PeerItem temp = this.cached_peer_popularities[this.popularity_pos_non_pub];
/*     */           
/* 513 */           this.popularity_pos_non_pub += 1;
/*     */           
/* 515 */           if (temp.getNetwork() != "Public")
/*     */           {
/* 517 */             peer = temp;
/*     */             
/* 519 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 524 */       PeerItem peer = this.cached_peer_popularities[this.popularity_pos];
/*     */       
/* 526 */       this.popularity_pos += 1;
/*     */       
/* 528 */       if (peer.getNetwork() != "Public")
/*     */       {
/* 530 */         this.popularity_pos_non_pub = this.popularity_pos;
/*     */       }
/*     */       
/* 533 */       this.pex_used_count += 1;
/* 534 */       this.last_rebuild_time = now;
/*     */     }
/*     */     else
/*     */     {
/* 538 */       peer = null;
/*     */     }
/*     */     
/* 541 */     return peer;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getExchangedPeerCount()
/*     */   {
/* 547 */     long now = SystemTime.getMonotonousTime();
/*     */     
/* 549 */     if (now - this.pex_count_last_time >= 10000L)
/*     */     {
/* 551 */       PeerItem[] peers = getExchangedPeersSortedByLeastPopularFirst();
/*     */       
/* 553 */       this.pex_count_last = (peers == null ? 0 : peers.length);
/* 554 */       this.pex_count_last_time = now;
/*     */     }
/*     */     
/* 557 */     return Math.max(0, this.pex_count_last - this.popularity_pos);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getExchangedPeersUsed()
/*     */   {
/* 563 */     return this.pex_used_count;
/*     */   }
/*     */   
/*     */   private PeerItem[] getExchangedPeersSortedByLeastPopularFirst() {
/* 567 */     HashMap popularity_counts = new HashMap();
/*     */     try {
/* 569 */       this.map_mon.enter();
/*     */       
/* 571 */       for (it = this.peer_connections.values().iterator(); it.hasNext();) {
/* 572 */         PeerExchangerItem connection = (PeerExchangerItem)it.next();
/* 573 */         PeerItem[] peers = connection.getConnectedPeers();
/*     */         
/* 575 */         for (int i = 0; i < peers.length; i++) {
/* 576 */           PeerItem peer = peers[i];
/* 577 */           Integer count = (Integer)popularity_counts.get(peer);
/*     */           
/* 579 */           if (count == null) {
/* 580 */             count = new Integer(1);
/*     */           }
/*     */           else {
/* 583 */             count = new Integer(count.intValue() + 1);
/*     */           }
/*     */           
/* 586 */           popularity_counts.put(peer, count);
/*     */         }
/*     */       }
/*     */     } finally { Iterator it;
/* 590 */       this.map_mon.exit();
/*     */     }
/* 592 */     if (popularity_counts.isEmpty()) { return null;
/*     */     }
/*     */     
/* 595 */     Map.Entry[] sorted_entries = new Map.Entry[popularity_counts.size()];
/* 596 */     popularity_counts.entrySet().toArray(sorted_entries);
/*     */     
/* 598 */     Arrays.sort(sorted_entries, new Comparator() {
/*     */       public int compare(Object obj1, Object obj2) {
/* 600 */         Map.Entry en1 = (Map.Entry)obj1;
/* 601 */         Map.Entry en2 = (Map.Entry)obj2;
/* 602 */         return ((Integer)en1.getValue()).compareTo((Integer)en2.getValue());
/*     */       }
/*     */       
/* 605 */     });
/* 606 */     PeerItem[] sorted_peers = new PeerItem[sorted_entries.length];
/*     */     
/* 608 */     for (int i = 0; i < sorted_entries.length; i++) {
/* 609 */       Map.Entry entry = sorted_entries[i];
/* 610 */       sorted_peers[i] = ((PeerItem)entry.getKey());
/*     */     }
/*     */     
/* 613 */     return sorted_peers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getString()
/*     */   {
/* 622 */     return "pc=" + this.peer_connections.size() + ",dp=" + this.discovered_peers.size() + "/" + this.discovered_peers_non_pub.size();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/peerdb/PeerDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */