/*    */ package com.aelitis.azureus.core.peermanager.uploadslots;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.unchoker.UnchokerUtil;
/*    */ import java.util.ArrayList;
/*    */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*    */ public class SeedingRanker
/*    */ {
/*    */   private PEPeerTransport getNextOptimisticPeerExec(ArrayList all_peers)
/*    */   {
/* 44 */     if (all_peers.isEmpty()) {
/* 45 */       Debug.out("all_peers.isEmpty()");
/* 46 */       return null;
/*    */     }
/*    */     
/* 49 */     int pos = RandomUtils.nextInt(all_peers.size());
/*    */     
/* 51 */     for (int i = 0; i < all_peers.size(); i++)
/*    */     {
/* 53 */       PEPeerTransport peer = (PEPeerTransport)all_peers.get(pos);
/*    */       
/* 55 */       if ((peer.isChokedByMe()) && (UnchokerUtil.isUnchokable(peer, true)))
/*    */       {
/* 57 */         return peer;
/*    */       }
/*    */       
/* 60 */       pos++;
/*    */       
/* 62 */       if (pos >= all_peers.size()) {
/* 63 */         pos = 0;
/*    */       }
/*    */     }
/*    */     
/* 67 */     Debug.out("no optimistic-able seeding peers found");
/* 68 */     return null;
/*    */   }
/*    */   
/*    */   public PEPeerTransport getNextOptimisticPeer(ArrayList all_peers)
/*    */   {
/* 73 */     PEPeerTransport picked = getNextOptimisticPeerExec(all_peers);
/*    */     
/*    */ 
/*    */ 
/* 77 */     return picked;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/SeedingRanker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */