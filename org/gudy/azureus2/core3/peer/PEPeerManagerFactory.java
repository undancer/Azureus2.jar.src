/*    */ package org.gudy.azureus2.core3.peer;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManager;
/*    */ import org.gudy.azureus2.core3.peer.impl.PEPeerControlFactory;
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
/*    */ 
/*    */ public class PEPeerManagerFactory
/*    */ {
/*    */   public static PEPeerManager create(byte[] peer_id, PEPeerManagerAdapter adapter, DiskManager diskManager)
/*    */   {
/* 42 */     return create(peer_id, adapter, diskManager, 0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static PEPeerManager create(byte[] peer_id, PEPeerManagerAdapter adapter, DiskManager diskManager, int id)
/*    */   {
/* 52 */     return PEPeerControlFactory.create(peer_id, adapter, diskManager, id);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/PEPeerManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */