/*    */ package com.aelitis.azureus.core.peermanager.uploadslots;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.unchoker.UnchokerUtil;
/*    */ import java.util.ArrayList;
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DownloadingRanker
/*    */ {
/*    */   public PEPeer getNextOptimisticPeer(ArrayList<PEPeer> all_peers)
/*    */   {
/* 43 */     return UnchokerUtil.getNextOptimisticPeer(all_peers, true, true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ArrayList<PEPeer> rankPeers(int max_to_unchoke, ArrayList<PEPeer> all_peers)
/*    */   {
/* 51 */     ArrayList<PEPeer> best_peers = new ArrayList();
/* 52 */     long[] bests = new long[max_to_unchoke];
/*    */     
/*    */ 
/*    */ 
/* 56 */     for (int i = 0; i < all_peers.size(); i++) {
/* 57 */       PEPeer peer = (PEPeer)all_peers.get(i);
/*    */       
/* 59 */       if ((peer.isInteresting()) && (UnchokerUtil.isUnchokable(peer, false))) {
/* 60 */         long rate = peer.getStats().getSmoothDataReceiveRate();
/* 61 */         if (rate > 256L) {
/* 62 */           UnchokerUtil.updateLargestValueFirstSort(rate, bests, peer, best_peers, 0);
/*    */         }
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 69 */     if (best_peers.size() < max_to_unchoke) {
/* 70 */       int start_pos = best_peers.size();
/*    */       
/*    */ 
/* 73 */       for (int i = 0; i < all_peers.size(); i++) {
/* 74 */         PEPeer peer = (PEPeer)all_peers.get(i);
/*    */         
/* 76 */         if ((peer.isInteresting()) && (UnchokerUtil.isUnchokable(peer, false)) && (!best_peers.contains(peer))) {
/* 77 */           long uploaded_ratio = peer.getStats().getTotalDataBytesSent() / (peer.getStats().getTotalDataBytesReceived() + 16383L);
/*    */           
/* 79 */           if (uploaded_ratio < 3L) {
/* 80 */             UnchokerUtil.updateLargestValueFirstSort(peer.getStats().getTotalDataBytesReceived(), bests, peer, best_peers, start_pos);
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 87 */     return best_peers;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/DownloadingRanker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */