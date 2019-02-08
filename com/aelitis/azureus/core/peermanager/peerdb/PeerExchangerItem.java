/*     */ package com.aelitis.azureus.core.peermanager.peerdb;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.LightHashMap;
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
/*     */ public class PeerExchangerItem
/*     */ {
/*     */   public static final int MAX_PEERS_PER_VOLLEY = 50;
/*     */   private static final int MAX_KNOWN_PER_PEER = 500;
/*     */   private final PeerDatabase parent_db;
/*     */   private final PeerItem base_peer;
/*     */   private final String network;
/*  41 */   private final LinkedHashSet<PeerItem> connections_added = new LinkedHashSet();
/*  42 */   private final LinkedHashSet<PeerItem> connections_dropped = new LinkedHashSet();
/*  43 */   private final Map<PeerItem, Object> connected_peers = new LightHashMap();
/*  44 */   private final AEMonitor peers_mon = new AEMonitor("PeerConnectionItem");
/*  45 */   private boolean maintain_peers_state = true;
/*     */   private final Helper helper;
/*     */   
/*     */   protected PeerExchangerItem(PeerDatabase parent_db, PeerItem peer, Helper helper)
/*     */   {
/*  50 */     this.parent_db = parent_db;
/*  51 */     this.base_peer = peer;
/*  52 */     this.helper = helper;
/*     */     
/*  54 */     this.network = peer.getNetwork();
/*     */   }
/*     */   
/*     */ 
/*  58 */   protected PeerItem getBasePeer() { return this.base_peer; }
/*     */   
/*  60 */   protected Helper getHelper() { return this.helper; }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addConnectedPeer(PeerItem peer)
/*     */   {
/*     */     try
/*     */     {
/*  68 */       this.peers_mon.enter();
/*  69 */       if (!this.maintain_peers_state)
/*     */         return;
/*  71 */       int max_cache_size = PeerUtils.MAX_CONNECTIONS_PER_TORRENT;
/*  72 */       if ((max_cache_size < 1) || (max_cache_size > 500)) { max_cache_size = 500;
/*     */       }
/*  74 */       if (this.connected_peers.size() < max_cache_size) {
/*  75 */         this.connected_peers.put(peer, null);
/*     */       }
/*     */     } finally {
/*  78 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void dropConnectedPeer(PeerItem peer)
/*     */   {
/*     */     try
/*     */     {
/*  87 */       this.peers_mon.enter();
/*     */       
/*  89 */       this.connected_peers.remove(peer);
/*     */     } finally {
/*  91 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyAdded(PeerItem peer_connection)
/*     */   {
/*     */     try {
/*  98 */       this.peers_mon.enter();
/*  99 */       if (!this.maintain_peers_state)
/*     */         return;
/* 101 */       if (!this.connections_dropped.contains(peer_connection)) {
/* 102 */         if (!this.connections_added.contains(peer_connection)) {
/* 103 */           this.connections_added.add(peer_connection);
/*     */         }
/*     */       }
/*     */       else {
/* 107 */         this.connections_dropped.remove(peer_connection);
/*     */       }
/*     */     } finally {
/* 110 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void notifyDropped(PeerItem peer_connection) {
/* 115 */     try { this.peers_mon.enter();
/* 116 */       if (!this.maintain_peers_state)
/*     */         return;
/* 118 */       if (!this.connections_added.contains(peer_connection)) {
/* 119 */         if (!this.connections_dropped.contains(peer_connection)) {
/* 120 */           this.connections_dropped.add(peer_connection);
/*     */         }
/*     */       }
/*     */       else {
/* 124 */         this.connections_added.remove(peer_connection);
/*     */       }
/*     */     } finally {
/* 127 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void seedStatusChanged()
/*     */   {
/* 133 */     this.parent_db.seedStatusChanged(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PeerItem[] getNewlyAddedPeerConnections()
/*     */   {
/*     */     try
/*     */     {
/* 142 */       this.peers_mon.enter();
/* 143 */       if (this.connections_added.isEmpty()) { return null;
/*     */       }
/* 145 */       int num_to_send = this.connections_added.size() > 50 ? 50 : this.connections_added.size();
/*     */       
/* 147 */       PeerItem[] peers = new PeerItem[num_to_send];
/*     */       
/* 149 */       Iterator<PeerItem> it = this.connections_added.iterator();
/*     */       
/* 151 */       for (int i = 0; i < num_to_send; i++) {
/* 152 */         peers[i] = ((PeerItem)it.next());
/* 153 */         it.remove();
/*     */       }
/*     */       
/* 156 */       return peers;
/*     */     } finally {
/* 158 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public PeerItem[] getNewlyAddedPeerConnections(String network)
/*     */   {
/*     */     try
/*     */     {
/* 166 */       this.peers_mon.enter();
/*     */       
/* 168 */       if (this.connections_added.isEmpty()) { return null;
/*     */       }
/* 170 */       int num_to_send = this.connections_added.size() > 50 ? 50 : this.connections_added.size();
/*     */       
/* 172 */       List<PeerItem> peers = new ArrayList(num_to_send);
/*     */       
/* 174 */       Iterator<PeerItem> it = this.connections_added.iterator();
/*     */       PeerItem peer;
/* 176 */       while ((peers.size() < num_to_send) && (it.hasNext()))
/*     */       {
/* 178 */         peer = (PeerItem)it.next();
/*     */         
/* 180 */         if (peer.getNetwork() == network)
/*     */         {
/* 182 */           peers.add(peer);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 190 */         it.remove();
/*     */       }
/*     */       
/* 193 */       if (peers.size() == 0)
/*     */       {
/* 195 */         return null;
/*     */       }
/*     */       
/* 198 */       return (PeerItem[])peers.toArray(new PeerItem[peers.size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 202 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public PeerItem[] getNewlyDroppedPeerConnections()
/*     */   {
/*     */     try
/*     */     {
/* 211 */       this.peers_mon.enter();
/* 212 */       if (this.connections_dropped.isEmpty()) { return null;
/*     */       }
/* 214 */       int num_to_send = this.connections_dropped.size() > 50 ? 50 : this.connections_dropped.size();
/*     */       
/* 216 */       PeerItem[] peers = new PeerItem[num_to_send];
/*     */       
/* 218 */       Iterator<PeerItem> it = this.connections_dropped.iterator();
/*     */       
/* 220 */       for (int i = 0; i < num_to_send; i++) {
/* 221 */         peers[i] = ((PeerItem)it.next());
/* 222 */         it.remove();
/*     */       }
/* 224 */       return peers;
/*     */     } finally {
/* 226 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public PeerItem[] getNewlyDroppedPeerConnections(String network)
/*     */   {
/*     */     try
/*     */     {
/* 234 */       this.peers_mon.enter();
/*     */       
/* 236 */       if (this.connections_dropped.isEmpty()) { return null;
/*     */       }
/* 238 */       int num_to_send = this.connections_dropped.size() > 50 ? 50 : this.connections_dropped.size();
/*     */       
/* 240 */       List<PeerItem> peers = new ArrayList(num_to_send);
/*     */       
/* 242 */       Iterator<PeerItem> it = this.connections_dropped.iterator();
/*     */       PeerItem peer;
/* 244 */       while ((peers.size() < num_to_send) && (it.hasNext()))
/*     */       {
/* 246 */         peer = (PeerItem)it.next();
/*     */         
/* 248 */         if (peer.getNetwork() == network)
/*     */         {
/* 250 */           peers.add(peer);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 255 */         it.remove();
/*     */       }
/*     */       
/* 258 */       if (peers.size() == 0)
/*     */       {
/* 260 */         return null;
/*     */       }
/*     */       
/* 263 */       return (PeerItem[])peers.toArray(new PeerItem[peers.size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 267 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void disableStateMaintenance()
/*     */   {
/*     */     try
/*     */     {
/* 278 */       this.peers_mon.enter();
/*     */       
/* 280 */       this.maintain_peers_state = false;
/*     */       
/* 282 */       this.connections_added.clear();
/*     */       
/* 284 */       this.connections_dropped.clear();
/*     */       
/* 286 */       this.connected_peers.clear();
/*     */     }
/*     */     finally {
/* 289 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void enableStateMaintenance()
/*     */   {
/*     */     try
/*     */     {
/* 297 */       this.peers_mon.enter();
/*     */       
/* 299 */       if (this.maintain_peers_state) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 304 */       this.maintain_peers_state = true;
/*     */     }
/*     */     finally
/*     */     {
/* 308 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean isConnectedToPeer(PeerItem peer) {
/*     */     try {
/* 314 */       this.peers_mon.enter();
/*     */       
/* 316 */       return this.connected_peers.containsKey(peer);
/*     */     } finally {
/* 318 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected PeerItem[] getConnectedPeers() {
/* 323 */     try { this.peers_mon.enter();
/*     */       
/* 325 */       PeerItem[] peers = new PeerItem[this.connected_peers.size()];
/* 326 */       this.connected_peers.keySet().toArray(peers);
/* 327 */       return peers;
/*     */     } finally {
/* 329 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 336 */     this.parent_db.deregisterPeerConnection(this.base_peer);
/*     */     try {
/* 338 */       this.peers_mon.enter();
/* 339 */       this.connections_added.clear();
/* 340 */       this.connections_dropped.clear();
/* 341 */       this.connected_peers.clear();
/*     */     } finally {
/* 343 */       this.peers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface Helper
/*     */   {
/*     */     public abstract boolean isSeed();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/peerdb/PeerExchangerItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */