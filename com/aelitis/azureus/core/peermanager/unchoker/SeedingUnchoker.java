/*     */ package com.aelitis.azureus.core.peermanager.unchoker;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class SeedingUnchoker
/*     */   implements Unchoker
/*     */ {
/*     */   private static int priority_unchoke_retention_count;
/*     */   
/*  38 */   static { COConfigurationManager.addAndFireParameterListener("Non-Public Peer Extra Slots Per Torrent", new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  46 */         SeedingUnchoker.access$002(COConfigurationManager.getIntParameter("Non-Public Peer Extra Slots Per Torrent"));
/*     */       }
/*     */     }); }
/*     */   
/*  50 */   private ArrayList<PEPeer> chokes = new ArrayList();
/*  51 */   private ArrayList<PEPeer> unchokes = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSeedingUnchoker()
/*     */   {
/*  61 */     return true;
/*     */   }
/*     */   
/*     */   public ArrayList<PEPeer> getImmediateUnchokes(int max_to_unchoke, ArrayList<PEPeer> all_peers)
/*     */   {
/*  66 */     int peer_count = all_peers.size();
/*     */     
/*  68 */     if (max_to_unchoke > peer_count)
/*     */     {
/*  70 */       max_to_unchoke = peer_count;
/*     */     }
/*     */     
/*     */ 
/*  74 */     int num_unchoked = 0;
/*  75 */     for (int i = 0; i < all_peers.size(); i++) {
/*  76 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*  77 */       if (!peer.isChokedByMe()) { num_unchoked++;
/*     */       }
/*     */     }
/*     */     
/*  81 */     int needed = max_to_unchoke - num_unchoked;
/*     */     
/*  83 */     if (needed > 0)
/*     */     {
/*  85 */       ArrayList<PEPeer> to_unchoke = UnchokerUtil.getNextOptimisticPeers(all_peers, false, false, needed);
/*     */       
/*  87 */       if (to_unchoke == null)
/*     */       {
/*  89 */         return new ArrayList(0);
/*     */       }
/*     */       
/*  92 */       for (int i = 0; i < to_unchoke.size(); i++)
/*     */       {
/*  94 */         ((PEPeer)to_unchoke.get(i)).setOptimisticUnchoke(true);
/*     */       }
/*     */       
/*  97 */       return to_unchoke;
/*     */     }
/*     */     
/*     */ 
/* 101 */     return new ArrayList(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void calculateUnchokes(int max_to_unchoke, ArrayList<PEPeer> all_peers, boolean force_refresh, boolean check_priority_connections, boolean do_high_latency_peers)
/*     */   {
/* 109 */     int max_optimistic = (max_to_unchoke - 1) / 5 + 1;
/*     */     
/*     */ 
/* 112 */     for (int i = 0; i < all_peers.size(); i++) {
/* 113 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*     */       
/* 115 */       if (!peer.isChokedByMe()) {
/* 116 */         if (UnchokerUtil.isUnchokable(peer, false)) {
/* 117 */           this.unchokes.add(peer);
/*     */         }
/*     */         else {
/* 120 */           this.chokes.add(peer);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 127 */     while (this.unchokes.size() > max_to_unchoke) {
/* 128 */       this.chokes.add(this.unchokes.remove(this.unchokes.size() - 1));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 133 */     if (force_refresh)
/*     */     {
/*     */ 
/* 136 */       ArrayList<PEPeer> peers_ordered_by_rate = new ArrayList();
/* 137 */       ArrayList<PEPeer> peers_ordered_by_uploaded = new ArrayList();
/*     */       
/* 139 */       long[] rates = new long[this.unchokes.size()];
/* 140 */       long[] uploaded = new long[rates.length];
/*     */       
/*     */ 
/* 143 */       for (int i = 0; i < this.unchokes.size(); i++) {
/* 144 */         PEPeer peer = (PEPeer)this.unchokes.get(i);
/*     */         
/* 146 */         long rate = peer.getStats().getDataSendRate();
/* 147 */         if (rate > 256L)
/*     */         {
/* 149 */           UnchokerUtil.updateLargestValueFirstSort(rate, rates, peer, peers_ordered_by_rate, 0);
/*     */           
/*     */ 
/* 152 */           UnchokerUtil.updateLargestValueFirstSort(peer.getStats().getTotalDataBytesSent(), uploaded, peer, peers_ordered_by_uploaded, 0);
/*     */         }
/*     */       }
/*     */       
/* 156 */       Collections.reverse(peers_ordered_by_rate);
/*     */       
/* 158 */       ArrayList<PEPeer> peers_ordered_by_rank = new ArrayList();
/* 159 */       long[] ranks = new long[peers_ordered_by_rate.size()];
/* 160 */       Arrays.fill(ranks, Long.MIN_VALUE);
/*     */       
/*     */ 
/* 163 */       for (int i = 0; i < this.unchokes.size(); i++) {
/* 164 */         PEPeer peer = (PEPeer)this.unchokes.get(i);
/*     */         
/*     */ 
/* 167 */         long rate_factor = peers_ordered_by_rate.indexOf(peer);
/* 168 */         long uploaded_factor = peers_ordered_by_uploaded.indexOf(peer);
/*     */         
/* 170 */         if (rate_factor != -1L)
/*     */         {
/* 172 */           long rank_factor = rate_factor + uploaded_factor;
/*     */           
/* 174 */           UnchokerUtil.updateLargestValueFirstSort(rank_factor, ranks, peer, peers_ordered_by_rank, 0);
/*     */         }
/*     */       }
/*     */       
/* 178 */       while (peers_ordered_by_rank.size() > max_to_unchoke - max_optimistic) {
/* 179 */         peers_ordered_by_rank.remove(peers_ordered_by_rank.size() - 1);
/*     */       }
/*     */       
/*     */ 
/* 183 */       ArrayList<PEPeer> to_unchoke = new ArrayList();
/* 184 */       for (Iterator<PEPeer> it = this.unchokes.iterator(); it.hasNext();) {
/* 185 */         PEPeer peer = (PEPeer)it.next();
/*     */         
/* 187 */         peer.setOptimisticUnchoke(false);
/*     */         
/* 189 */         if (!peers_ordered_by_rank.contains(peer))
/*     */         {
/* 191 */           PEPeer optimistic_peer = UnchokerUtil.getNextOptimisticPeer(all_peers, false, false);
/*     */           
/* 193 */           if (optimistic_peer != null) {
/* 194 */             this.chokes.add(peer);
/* 195 */             it.remove();
/*     */             
/* 197 */             to_unchoke.add(optimistic_peer);
/* 198 */             optimistic_peer.setOptimisticUnchoke(true);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 203 */       for (int i = 0; i < to_unchoke.size(); i++) {
/* 204 */         this.unchokes.add(to_unchoke.get(i));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 209 */     if (check_priority_connections)
/*     */     {
/* 211 */       setPriorityUnchokes(max_to_unchoke - max_optimistic, all_peers);
/*     */     }
/*     */     
/* 214 */     if (do_high_latency_peers)
/*     */     {
/* 216 */       UnchokerUtil.doHighLatencyPeers(this.chokes, this.unchokes, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setPriorityUnchokes(int max_priority, ArrayList<PEPeer> all_peers)
/*     */   {
/* 225 */     if (this.unchokes.isEmpty()) { return;
/*     */     }
/* 227 */     ArrayList<PEPeer> priority_peers = new ArrayList();
/*     */     
/* 229 */     for (int i = 0; i < all_peers.size(); i++)
/*     */     {
/* 231 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*     */       
/* 233 */       if ((peer.isPriorityConnection()) && (UnchokerUtil.isUnchokable(peer, true)))
/*     */       {
/* 235 */         priority_peers.add(peer);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 241 */     Collections.shuffle(priority_peers);
/*     */     
/* 243 */     int num_unchoked = 0;
/*     */     
/* 245 */     int num_non_priority_to_retain = priority_unchoke_retention_count;
/*     */     
/* 247 */     int max = max_priority > this.unchokes.size() ? this.unchokes.size() : max_priority;
/*     */     
/* 249 */     while ((num_unchoked < max) && (!priority_peers.isEmpty()))
/*     */     {
/* 251 */       PEPeer peer = (PEPeer)this.unchokes.remove(0);
/*     */       
/* 253 */       if (priority_peers.remove(peer))
/*     */       {
/* 255 */         this.unchokes.add(peer);
/*     */       }
/*     */       else
/*     */       {
/* 259 */         if (num_non_priority_to_retain-- > 0)
/*     */         {
/*     */ 
/*     */ 
/* 263 */           this.unchokes.add(peer);
/*     */         }
/*     */         
/* 266 */         PEPeer buddy = (PEPeer)priority_peers.remove(priority_peers.size() - 1);
/*     */         
/* 268 */         this.chokes.remove(buddy);
/*     */         
/* 270 */         this.unchokes.add(buddy);
/*     */       }
/*     */       
/* 273 */       num_unchoked++;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ArrayList<PEPeer> getChokes()
/*     */   {
/* 282 */     ArrayList<PEPeer> to_choke = this.chokes;
/* 283 */     this.chokes = new ArrayList();
/* 284 */     return to_choke;
/*     */   }
/*     */   
/*     */   public ArrayList<PEPeer> getUnchokes()
/*     */   {
/* 289 */     ArrayList<PEPeer> to_unchoke = this.unchokes;
/* 290 */     this.unchokes = new ArrayList();
/* 291 */     return to_unchoke;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/unchoker/SeedingUnchoker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */