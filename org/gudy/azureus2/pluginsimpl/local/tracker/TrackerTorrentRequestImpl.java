/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostPeer;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRequest;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerPeer;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TrackerTorrentRequestImpl
/*     */   implements TrackerTorrentRequest
/*     */ {
/*     */   protected TRHostTorrentRequest req;
/*     */   
/*     */   protected TrackerTorrentRequestImpl(TRHostTorrentRequest _req)
/*     */   {
/*  46 */     this.req = _req;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRequestType()
/*     */   {
/*  52 */     if (this.req.getRequestType() == 1)
/*     */     {
/*  54 */       return 1;
/*     */     }
/*  56 */     if (this.req.getRequestType() == 2)
/*     */     {
/*  58 */       return 2;
/*     */     }
/*     */     
/*     */ 
/*  62 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerTorrent getTorrent()
/*     */   {
/*  69 */     TRHostTorrent torrent = this.req.getTorrent();
/*     */     
/*  71 */     if (torrent == null)
/*     */     {
/*  73 */       return null;
/*     */     }
/*     */     
/*  76 */     return new TrackerTorrentImpl(torrent);
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerPeer getPeer()
/*     */   {
/*  82 */     TRHostPeer peer = this.req.getPeer();
/*     */     
/*  84 */     if (peer == null)
/*     */     {
/*  86 */       return null;
/*     */     }
/*     */     
/*  89 */     return new TrackerPeerImpl(peer);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getRequest()
/*     */   {
/*  95 */     return this.req.getRequest();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getResponse()
/*     */   {
/* 101 */     return this.req.getResponse();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerTorrentRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */