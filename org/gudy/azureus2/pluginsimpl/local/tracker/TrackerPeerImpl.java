/*    */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*    */ import org.gudy.azureus2.plugins.tracker.TrackerPeer;
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
/*    */ public class TrackerPeerImpl
/*    */   implements TrackerPeer
/*    */ {
/*    */   protected TRHostPeer peer;
/*    */   
/*    */   protected TrackerPeerImpl(TRHostPeer _peer)
/*    */   {
/* 43 */     this.peer = _peer;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isSeed()
/*    */   {
/* 49 */     return this.peer.isSeed();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getAmountLeft()
/*    */   {
/* 55 */     return this.peer.getAmountLeft();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getDownloaded()
/*    */   {
/* 61 */     return this.peer.getDownloaded();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getUploaded()
/*    */   {
/* 67 */     return this.peer.getUploaded();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIP()
/*    */   {
/* 73 */     return this.peer.getIP();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getIPRaw()
/*    */   {
/* 79 */     return this.peer.getIPRaw();
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 85 */     return this.peer.getPort();
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getPeerID()
/*    */   {
/* 91 */     return this.peer.getPeerID();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerPeerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */