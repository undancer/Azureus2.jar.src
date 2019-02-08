/*    */ package org.gudy.azureus2.core3.tracker.host.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*    */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
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
/*    */ public class TRHostPeerHostImpl
/*    */   implements TRHostPeer
/*    */ {
/*    */   protected final TRTrackerServerPeer peer;
/*    */   
/*    */   protected TRHostPeerHostImpl(TRTrackerServerPeer _peer)
/*    */   {
/* 42 */     this.peer = _peer;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSeed()
/*    */   {
/* 48 */     return getAmountLeft() == 0L;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getUploaded()
/*    */   {
/* 54 */     return this.peer.getUploaded();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getDownloaded()
/*    */   {
/* 60 */     return this.peer.getDownloaded();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getAmountLeft()
/*    */   {
/* 66 */     return this.peer.getAmountLeft();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIP()
/*    */   {
/* 72 */     return this.peer.getIP();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIPRaw()
/*    */   {
/* 78 */     return this.peer.getIPRaw();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 84 */     return this.peer.getTCPPort();
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getPeerID()
/*    */   {
/* 90 */     return this.peer.getPeerID();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostPeerHostImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */