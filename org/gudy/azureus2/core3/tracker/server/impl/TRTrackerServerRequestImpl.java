/*    */ package org.gudy.azureus2.core3.tracker.server.impl;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerPeer;
/*    */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequest;
/*    */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
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
/*    */ 
/*    */ 
/*    */ public class TRTrackerServerRequestImpl
/*    */   implements TRTrackerServerRequest
/*    */ {
/*    */   protected final TRTrackerServerImpl server;
/*    */   protected final TRTrackerServerPeer peer;
/*    */   protected final TRTrackerServerTorrent torrent;
/*    */   protected final int type;
/*    */   protected final String request;
/*    */   protected final Map response;
/*    */   
/*    */   public TRTrackerServerRequestImpl(TRTrackerServerImpl _server, TRTrackerServerPeer _peer, TRTrackerServerTorrent _torrent, int _type, String _request, Map _response)
/*    */   {
/* 54 */     this.server = _server;
/* 55 */     this.peer = _peer;
/* 56 */     this.torrent = _torrent;
/* 57 */     this.type = _type;
/* 58 */     this.request = _request;
/* 59 */     this.response = _response;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getType()
/*    */   {
/* 65 */     return this.type;
/*    */   }
/*    */   
/*    */ 
/*    */   public TRTrackerServerPeer getPeer()
/*    */   {
/* 71 */     return this.peer;
/*    */   }
/*    */   
/*    */ 
/*    */   public TRTrackerServerTorrent getTorrent()
/*    */   {
/* 77 */     return this.torrent;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getRequest()
/*    */   {
/* 83 */     return this.request;
/*    */   }
/*    */   
/*    */ 
/*    */   public Map getResponse()
/*    */   {
/* 89 */     return this.response;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/server/impl/TRTrackerServerRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */