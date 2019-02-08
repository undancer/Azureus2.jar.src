/*    */ package org.gudy.azureus2.core3.peer.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManager;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManagerAdapter;
/*    */ import org.gudy.azureus2.core3.peer.impl.control.PEPeerControlImpl;
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
/*    */ public class PEPeerControlFactory
/*    */ {
/*    */   public static PEPeerControl create(byte[] peer_id, PEPeerManagerAdapter adapter, DiskManager diskManager)
/*    */   {
/* 43 */     return create(peer_id, adapter, diskManager, 0);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static PEPeerControl create(byte[] peer_id, PEPeerManagerAdapter adapter, DiskManager diskManager, int id)
/*    */   {
/* 53 */     return new PEPeerControlImpl(peer_id, adapter, diskManager, id);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPeerControlFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */