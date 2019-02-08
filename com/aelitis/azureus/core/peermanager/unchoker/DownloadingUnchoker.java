/*     */ package com.aelitis.azureus.core.peermanager.unchoker;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
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
/*     */ public class DownloadingUnchoker
/*     */   implements Unchoker
/*     */ {
/*  34 */   private ArrayList<PEPeer> chokes = new ArrayList();
/*  35 */   private ArrayList<PEPeer> unchokes = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSeedingUnchoker()
/*     */   {
/*  45 */     return false;
/*     */   }
/*     */   
/*     */   public ArrayList<PEPeer> getImmediateUnchokes(int max_to_unchoke, ArrayList<PEPeer> all_peers) {
/*  49 */     ArrayList<PEPeer> to_unchoke = new ArrayList();
/*     */     
/*     */ 
/*  52 */     int num_unchoked = 0;
/*  53 */     for (int i = 0; i < all_peers.size(); i++) {
/*  54 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*  55 */       if (!peer.isChokedByMe()) { num_unchoked++;
/*     */       }
/*     */     }
/*     */     
/*  59 */     int needed = max_to_unchoke - num_unchoked;
/*  60 */     if (needed > 0) {
/*  61 */       for (int i = 0; i < needed; i++) {
/*  62 */         PEPeer peer = UnchokerUtil.getNextOptimisticPeer(all_peers, true, true);
/*  63 */         if (peer == null) break;
/*  64 */         to_unchoke.add(peer);
/*  65 */         peer.setOptimisticUnchoke(true);
/*     */       }
/*     */     }
/*     */     
/*  69 */     return to_unchoke;
/*     */   }
/*     */   
/*     */ 
/*     */   public void calculateUnchokes(int max_to_unchoke, ArrayList<PEPeer> all_peers, boolean force_refresh, boolean check_priority_connections, boolean do_high_latency_peers)
/*     */   {
/*  75 */     int max_optimistic = (max_to_unchoke - 1) / 10 + 1;
/*     */     
/*  77 */     ArrayList<PEPeer> optimistic_unchokes = new ArrayList();
/*  78 */     ArrayList<PEPeer> best_peers = new ArrayList();
/*  79 */     long[] bests = new long[max_to_unchoke];
/*     */     
/*     */ 
/*     */ 
/*  83 */     for (int i = 0; i < all_peers.size(); i++) {
/*  84 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*     */       
/*  86 */       if (!peer.isChokedByMe()) {
/*  87 */         if (UnchokerUtil.isUnchokable(peer, true)) {
/*  88 */           this.unchokes.add(peer);
/*  89 */           if (peer.isOptimisticUnchoke()) {
/*  90 */             optimistic_unchokes.add(peer);
/*     */           }
/*     */         }
/*     */         else {
/*  94 */           this.chokes.add(peer);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 100 */     if (!force_refresh) {
/* 101 */       for (int i = 0; i < optimistic_unchokes.size(); i++) {
/* 102 */         PEPeer peer = (PEPeer)optimistic_unchokes.get(i);
/*     */         
/* 104 */         if (i < max_optimistic) {
/* 105 */           best_peers.add(peer);
/*     */         }
/*     */         else {
/* 108 */           peer.setOptimisticUnchoke(false);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 115 */     int start_pos = best_peers.size();
/* 116 */     for (int i = 0; i < all_peers.size(); i++) {
/* 117 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*     */       
/* 119 */       if ((peer.isInteresting()) && (UnchokerUtil.isUnchokable(peer, false)) && (!best_peers.contains(peer))) {
/* 120 */         long rate = peer.getStats().getSmoothDataReceiveRate();
/* 121 */         if (rate > 256L) {
/* 122 */           UnchokerUtil.updateLargestValueFirstSort(rate, bests, peer, best_peers, start_pos);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 129 */     if (best_peers.size() < max_to_unchoke) {
/* 130 */       start_pos = best_peers.size();
/*     */       
/*     */ 
/* 133 */       for (int i = 0; i < all_peers.size(); i++) {
/* 134 */         PEPeer peer = (PEPeer)all_peers.get(i);
/*     */         
/* 136 */         if ((peer.isInteresting()) && (UnchokerUtil.isUnchokable(peer, false)) && (!best_peers.contains(peer))) {
/* 137 */           long uploaded_ratio = peer.getStats().getTotalDataBytesSent() / (peer.getStats().getTotalDataBytesReceived() + 16383L);
/*     */           
/* 139 */           if (uploaded_ratio < 3L) {
/* 140 */             UnchokerUtil.updateLargestValueFirstSort(peer.getStats().getTotalDataBytesReceived(), bests, peer, best_peers, start_pos);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 147 */     if (force_refresh)
/*     */     {
/* 149 */       while (best_peers.size() > max_to_unchoke - max_optimistic) {
/* 150 */         best_peers.remove(best_peers.size() - 1);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 156 */     while (best_peers.size() < max_to_unchoke) {
/* 157 */       PEPeer peer = UnchokerUtil.getNextOptimisticPeer(all_peers, true, true);
/* 158 */       if (peer == null)
/*     */         break;
/* 160 */       if (!best_peers.contains(peer)) {
/* 161 */         best_peers.add(peer);
/* 162 */         peer.setOptimisticUnchoke(true);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 168 */         peer.sendUnChoke();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 175 */     for (Iterator<PEPeer> it = this.unchokes.iterator(); it.hasNext();) {
/* 176 */       PEPeer peer = (PEPeer)it.next();
/*     */       
/* 178 */       if (!best_peers.contains(peer)) {
/* 179 */         if (best_peers.size() < max_to_unchoke) {
/* 180 */           best_peers.add(peer);
/*     */         }
/*     */         else {
/* 183 */           this.chokes.add(peer);
/* 184 */           it.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 190 */     for (int i = 0; i < best_peers.size(); i++) {
/* 191 */       PEPeer peer = (PEPeer)best_peers.get(i);
/*     */       
/* 193 */       if (!this.unchokes.contains(peer)) {
/* 194 */         this.unchokes.add(peer);
/*     */       }
/*     */     }
/*     */     
/* 198 */     if (do_high_latency_peers)
/*     */     {
/* 200 */       UnchokerUtil.doHighLatencyPeers(this.chokes, this.unchokes, true);
/*     */     }
/*     */   }
/*     */   
/*     */   public ArrayList<PEPeer> getChokes()
/*     */   {
/* 206 */     ArrayList<PEPeer> to_choke = this.chokes;
/* 207 */     this.chokes = new ArrayList();
/* 208 */     return to_choke;
/*     */   }
/*     */   
/*     */   public ArrayList<PEPeer> getUnchokes()
/*     */   {
/* 213 */     ArrayList<PEPeer> to_unchoke = this.unchokes;
/* 214 */     this.unchokes = new ArrayList();
/* 215 */     return to_unchoke;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/unchoker/DownloadingUnchoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */