/*    */ package com.aelitis.azureus.core.peermanager.uploadslots;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.unchoker.UnchokerUtil;
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*    */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*    */ public class UploadSession
/*    */ {
/*    */   protected static final int TYPE_DOWNLOAD = 0;
/*    */   protected static final int TYPE_SEED = 1;
/*    */   private final PEPeer peer;
/*    */   private final int session_type;
/*    */   
/*    */   protected UploadSession(PEPeer _peer, int _session_type)
/*    */   {
/* 43 */     this.peer = _peer;
/* 44 */     this.session_type = _session_type;
/*    */   }
/*    */   
/*    */   protected int getSessionType() {
/* 48 */     return this.session_type;
/*    */   }
/*    */   
/*    */   protected void start() {
/* 52 */     UnchokerUtil.performChokeUnchoke(null, this.peer);
/*    */   }
/*    */   
/*    */   protected void stop()
/*    */   {
/* 57 */     UnchokerUtil.performChokeUnchoke(this.peer, null);
/*    */   }
/*    */   
/*    */   protected boolean isSameSession(UploadSession session)
/*    */   {
/* 62 */     if (session == null) return false;
/* 63 */     return this.peer == session.peer;
/*    */   }
/*    */   
/*    */   protected String getStatsTrace()
/*    */   {
/* 68 */     String n = this.peer.getManager().getDisplayName();
/* 69 */     String t = this.session_type == 0 ? "DOWNLOADING" : "SEEDING";
/* 70 */     String p = " : [" + this.peer.getClient() + "] " + this.peer.getIp() + " :" + this.peer.getPort();
/* 71 */     String s = " || (D: " + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.peer.getStats().getDataReceiveRate()) + ") (U: " + DisplayFormatters.formatByteCountToKiBEtcPerSec(this.peer.getStats().getDataSendRate()) + ")";
/*    */     
/* 73 */     return "[" + n + "] " + t + p + s;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/UploadSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */