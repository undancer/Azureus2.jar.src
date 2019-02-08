/*     */ package com.aelitis.azureus.core.peermanager.unchoker;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ public class UnchokerUtil
/*     */ {
/*     */   public static boolean isUnchokable(PEPeer peer, boolean allow_snubbed)
/*     */   {
/*  45 */     return (peer.getPeerState() == 30) && (!peer.isSeed()) && (!peer.isRelativeSeed()) && (peer.isInterested()) && (!peer.isUploadDisabled()) && ((!peer.isSnubbed()) || (allow_snubbed));
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
/*     */   public static void updateLargestValueFirstSort(long new_value, long[] values, PEPeer new_item, ArrayList items, int start_pos)
/*     */   {
/*  64 */     items.ensureCapacity(values.length);
/*  65 */     for (int i = start_pos; i < values.length; i++) {
/*  66 */       if (new_value >= values[i])
/*     */       {
/*     */ 
/*     */ 
/*  70 */         for (int j = values.length - 2; j >= i; j--) {
/*  71 */           values[(j + 1)] = values[j];
/*     */         }
/*     */         
/*  74 */         if (items.size() == values.length) {
/*  75 */           items.remove(values.length - 1);
/*     */         }
/*     */         
/*  78 */         values[i] = new_value;
/*  79 */         items.add(i, new_item);
/*     */         
/*  81 */         return;
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
/*     */   public static PEPeer getNextOptimisticPeer(ArrayList<PEPeer> all_peers, boolean factor_reciprocated, boolean allow_snubbed)
/*     */   {
/*  96 */     ArrayList<PEPeer> peers = getNextOptimisticPeers(all_peers, factor_reciprocated, allow_snubbed, 1);
/*     */     
/*  98 */     if (peers != null)
/*     */     {
/* 100 */       return (PEPeerTransport)peers.get(0);
/*     */     }
/*     */     
/* 103 */     return null;
/*     */   }
/*     */   
/*     */   public static ArrayList<PEPeer> getNextOptimisticPeers(ArrayList<PEPeer> all_peers, boolean factor_reciprocated, boolean allow_snubbed, int num_needed)
/*     */   {
/* 108 */     ArrayList<PEPeer> optimistics = new ArrayList();
/* 109 */     for (int i = 0; i < all_peers.size(); i++) {
/* 110 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*     */       
/* 112 */       if ((isUnchokable(peer, false)) && (peer.isChokedByMe())) {
/* 113 */         optimistics.add(peer);
/*     */       }
/*     */     }
/*     */     
/* 117 */     if ((optimistics.isEmpty()) && (allow_snubbed)) {
/* 118 */       for (int i = 0; i < all_peers.size(); i++) {
/* 119 */         PEPeer peer = (PEPeer)all_peers.get(i);
/*     */         
/* 121 */         if ((isUnchokable(peer, true)) && (peer.isChokedByMe())) {
/* 122 */           optimistics.add(peer);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 127 */     if (optimistics.isEmpty()) { return null;
/*     */     }
/*     */     
/*     */ 
/* 131 */     ArrayList<PEPeer> result = new ArrayList(optimistics.size());
/*     */     
/* 133 */     if (factor_reciprocated)
/*     */     {
/* 135 */       ArrayList<PEPeerTransport> ratioed_peers = new ArrayList(optimistics.size());
/* 136 */       long[] ratios = new long[optimistics.size()];
/* 137 */       Arrays.fill(ratios, Long.MIN_VALUE);
/*     */       
/*     */ 
/* 140 */       for (int i = 0; i < optimistics.size(); i++) {
/* 141 */         PEPeer peer = (PEPeer)optimistics.get(i);
/*     */         
/*     */ 
/* 144 */         long score = peer.getStats().getTotalDataBytesSent() - peer.getStats().getTotalDataBytesReceived();
/*     */         
/* 146 */         updateLargestValueFirstSort(score, ratios, peer, ratioed_peers, 0);
/*     */       }
/*     */       
/* 149 */       for (int i = 0; (i < num_needed) && (ratioed_peers.size() > 0); i++)
/*     */       {
/* 151 */         double factor = 1.0D / (0.8D + 0.2D * Math.pow(RandomUtils.nextFloat(), -1.0D));
/*     */         
/* 153 */         int pos = (int)(factor * ratioed_peers.size());
/*     */         
/* 155 */         result.add(ratioed_peers.remove(pos));
/*     */       }
/*     */     }
/*     */     else {
/* 159 */       for (int i = 0; (i < num_needed) && (optimistics.size() > 0); i++)
/*     */       {
/* 161 */         int rand_pos = new Random().nextInt(optimistics.size());
/*     */         
/* 163 */         result.add(optimistics.remove(rand_pos));
/*     */       }
/*     */     }
/*     */     
/* 167 */     return result;
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
/*     */   public static void performChokes(ArrayList<PEPeer> peers_to_choke, ArrayList<PEPeer> peers_to_unchoke)
/*     */   {
/* 185 */     if (peers_to_choke != null) {
/* 186 */       for (int i = 0; i < peers_to_choke.size(); i++) {
/* 187 */         PEPeerTransport peer = (PEPeerTransport)peers_to_choke.get(i);
/*     */         
/* 189 */         if (!peer.isChokedByMe()) {
/* 190 */           peer.sendChoke();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 196 */     if (peers_to_unchoke != null) {
/* 197 */       for (int i = 0; i < peers_to_unchoke.size(); i++) {
/* 198 */         PEPeer peer = (PEPeer)peers_to_unchoke.get(i);
/*     */         
/* 200 */         if (peer.isChokedByMe()) {
/* 201 */           peer.sendUnChoke();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void performChokeUnchoke(PEPeer to_choke, PEPeer to_unchoke)
/*     */   {
/* 209 */     if ((to_choke != null) && (!to_choke.isChokedByMe())) {
/* 210 */       to_choke.sendChoke();
/*     */     }
/*     */     
/* 213 */     if ((to_unchoke != null) && (to_unchoke.isChokedByMe())) {
/* 214 */       to_unchoke.sendUnChoke();
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
/*     */   public static void doHighLatencyPeers(ArrayList<PEPeer> peers_to_choke, ArrayList<PEPeer> peers_to_unchoke, boolean allow_snubbed)
/*     */   {
/* 227 */     if (peers_to_choke.size() == 0)
/*     */     {
/* 229 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 234 */     Iterator<PEPeer> choke_it = peers_to_choke.iterator();
/*     */     
/* 236 */     int to_remove = 0;
/*     */     
/* 238 */     while (choke_it.hasNext())
/*     */     {
/* 240 */       PEPeer peer = (PEPeer)choke_it.next();
/*     */       
/* 242 */       if (AENetworkClassifier.categoriseAddress(peer.getIp()) != "Public")
/*     */       {
/* 244 */         if (isUnchokable(peer, allow_snubbed))
/*     */         {
/*     */ 
/*     */ 
/* 248 */           choke_it.remove();
/*     */           
/* 250 */           to_remove++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */     if (to_remove > 0)
/*     */     {
/* 265 */       ListIterator<PEPeer> unchoke_it = peers_to_unchoke.listIterator(peers_to_unchoke.size());
/*     */       
/*     */ 
/*     */ 
/* 269 */       while (unchoke_it.hasPrevious())
/*     */       {
/* 271 */         PEPeer peer = (PEPeer)unchoke_it.previous();
/*     */         
/* 273 */         if (AENetworkClassifier.categoriseAddress(peer.getIp()) != "Public")
/*     */         {
/*     */ 
/*     */ 
/* 277 */           unchoke_it.remove();
/*     */           
/* 279 */           to_remove--;
/*     */           
/* 281 */           if (to_remove == 0)
/*     */           {
/* 283 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 288 */       if (to_remove > 0)
/*     */       {
/* 290 */         unchoke_it = peers_to_unchoke.listIterator(peers_to_unchoke.size());
/*     */         
/* 292 */         while (unchoke_it.hasPrevious())
/*     */         {
/* 294 */           PEPeer peer = (PEPeer)unchoke_it.previous();
/*     */           
/* 296 */           unchoke_it.remove();
/*     */           
/* 298 */           to_remove--;
/*     */           
/* 300 */           if (to_remove == 0)
/*     */           {
/* 302 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/unchoker/UnchokerUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */