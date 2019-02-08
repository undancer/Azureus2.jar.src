/*    */ package org.gudy.azureus2.core3.peer.impl.control;
/*    */ 
/*    */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
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
/*    */ public class SuperSeedPeer
/*    */   implements Comparable
/*    */ {
/*    */   public final PEPeerTransport peer;
/*    */   
/*    */   public SuperSeedPeer(PEPeerTransport peer)
/*    */   {
/* 35 */     this.peer = peer;
/*    */   }
/*    */   
/*    */   public int compareTo(Object obj) {
/* 39 */     SuperSeedPeer otherPeer = (SuperSeedPeer)obj;
/* 40 */     return this.peer.getUploadHint() - otherPeer.peer.getUploadHint();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/control/SuperSeedPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */