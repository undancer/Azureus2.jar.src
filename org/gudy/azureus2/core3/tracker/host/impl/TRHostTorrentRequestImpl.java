/*    */ package org.gudy.azureus2.core3.tracker.host.impl;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*    */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRequest;
/*    */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequest;
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
/*    */ public class TRHostTorrentRequestImpl
/*    */   implements TRHostTorrentRequest
/*    */ {
/*    */   protected final TRHostTorrent torrent;
/*    */   protected final TRHostPeer peer;
/*    */   protected final TRTrackerServerRequest request;
/*    */   
/*    */   protected TRHostTorrentRequestImpl(TRHostTorrent _torrent, TRHostPeer _peer, TRTrackerServerRequest _request)
/*    */   {
/* 49 */     this.torrent = _torrent;
/* 50 */     this.peer = _peer;
/* 51 */     this.request = _request;
/*    */   }
/*    */   
/*    */ 
/*    */   public TRHostPeer getPeer()
/*    */   {
/* 57 */     return this.peer;
/*    */   }
/*    */   
/*    */ 
/*    */   public TRHostTorrent getTorrent()
/*    */   {
/* 63 */     return this.torrent;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getRequestType()
/*    */   {
/* 69 */     if (this.request.getType() == 1)
/*    */     {
/* 71 */       return 1;
/*    */     }
/* 73 */     if (this.request.getType() == 2)
/*    */     {
/* 75 */       return 2;
/*    */     }
/*    */     
/*    */ 
/* 79 */     return 3;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getRequest()
/*    */   {
/* 86 */     return this.request.getRequest();
/*    */   }
/*    */   
/*    */ 
/*    */   public Map getResponse()
/*    */   {
/* 92 */     return this.request.getResponse();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */