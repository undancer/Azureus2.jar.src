/*    */ package org.gudy.azureus2.core3.tracker.host.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
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
/*    */ public class TRHostPeerPublishImpl
/*    */   implements TRHostPeer
/*    */ {
/*    */   protected final boolean seed;
/*    */   
/*    */   protected TRHostPeerPublishImpl(boolean _seed)
/*    */   {
/* 42 */     this.seed = _seed;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSeed()
/*    */   {
/* 48 */     return this.seed;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getUploaded()
/*    */   {
/* 54 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getDownloaded()
/*    */   {
/* 60 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getAmountLeft()
/*    */   {
/* 66 */     return 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getNumberOfPeers()
/*    */   {
/* 72 */     return 0;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIP()
/*    */   {
/* 78 */     return "";
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIPRaw()
/*    */   {
/* 84 */     return "";
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 90 */     return 0;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getPeerID()
/*    */   {
/* 96 */     return null;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostPeerPublishImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */