/*    */ package com.aelitis.azureus.plugins.extseed;
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
/*    */ public class ExternalSeedManualPeer
/*    */ {
/*    */   private ExternalSeedPeer peer;
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
/*    */   protected ExternalSeedManualPeer(ExternalSeedPeer _peer)
/*    */   {
/* 32 */     this.peer = _peer;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIP()
/*    */   {
/* 38 */     return this.peer.getIp();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] read(int piece_number, int offset, int length, int timeout)
/*    */     throws ExternalSeedException
/*    */   {
/* 50 */     return this.peer.getReader().read(piece_number, offset, length, timeout);
/*    */   }
/*    */   
/*    */ 
/*    */   public ExternalSeedPeer getDelegate()
/*    */   {
/* 56 */     return this.peer;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/extseed/ExternalSeedManualPeer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */