/*     */ package org.gudy.azureus2.pluginsimpl.local.peers;
/*     */ 
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerStats;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
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
/*     */ public class PeerManagerStatsImpl
/*     */   implements PeerManagerStats
/*     */ {
/*     */   protected PEPeerManager manager;
/*     */   protected PEPeerManagerStats stats;
/*     */   
/*     */   protected PeerManagerStatsImpl(PEPeerManager _manager)
/*     */   {
/*  42 */     this.manager = _manager;
/*  43 */     this.stats = this.manager.getStats();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectedSeeds()
/*     */   {
/*  49 */     return this.manager.getNbSeeds();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getConnectedLeechers()
/*     */   {
/*  55 */     return this.manager.getNbPeers();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/*  61 */     return this.stats.getTotalDataBytesReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploaded()
/*     */   {
/*  67 */     return this.stats.getTotalDataBytesSent();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloadAverage()
/*     */   {
/*  73 */     return this.stats.getDataReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getUploadAverage()
/*     */   {
/*  79 */     return this.stats.getDataSendRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDiscarded()
/*     */   {
/*  85 */     return this.stats.getTotalDiscarded();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getHashFailBytes()
/*     */   {
/*  91 */     return this.stats.getTotalHashFailBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToReceive()
/*     */   {
/*  97 */     return this.stats.getPermittedBytesToReceive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void permittedReceiveBytesUsed(int bytes)
/*     */   {
/* 104 */     this.stats.permittedReceiveBytesUsed(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToSend()
/*     */   {
/* 110 */     return this.stats.getPermittedBytesToSend();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void permittedSendBytesUsed(int bytes)
/*     */   {
/* 117 */     this.stats.permittedSendBytesUsed(bytes);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/peers/PeerManagerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */