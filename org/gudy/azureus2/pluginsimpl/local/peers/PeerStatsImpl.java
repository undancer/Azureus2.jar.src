/*     */ package org.gudy.azureus2.pluginsimpl.local.peers;
/*     */ 
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerStats;
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
/*     */ 
/*     */ 
/*     */ public class PeerStatsImpl
/*     */   implements PeerStats
/*     */ {
/*     */   private PeerManagerImpl peer_manager;
/*     */   private PEPeerManager manager;
/*     */   private PEPeerStats delegate;
/*     */   private Peer owner;
/*     */   
/*     */   public PeerStatsImpl(PeerManagerImpl _peer_manager, Peer _owner, PEPeerStats _delegate)
/*     */   {
/*  49 */     this.peer_manager = _peer_manager;
/*  50 */     this.manager = this.peer_manager.getDelegate();
/*  51 */     this.delegate = _delegate;
/*  52 */     this.owner = _owner;
/*     */   }
/*     */   
/*     */ 
/*     */   public PEPeerStats getDelegate()
/*     */   {
/*  58 */     return this.delegate;
/*     */   }
/*     */   
/*     */   public int getDownloadAverage()
/*     */   {
/*  63 */     return (int)this.delegate.getDataReceiveRate();
/*     */   }
/*     */   
/*     */   public int getReception()
/*     */   {
/*  68 */     return (int)this.delegate.getSmoothDataReceiveRate();
/*     */   }
/*     */   
/*     */   public int getUploadAverage()
/*     */   {
/*  73 */     return (int)this.delegate.getDataSendRate();
/*     */   }
/*     */   
/*     */   public int getTotalAverage()
/*     */   {
/*  78 */     return (int)this.delegate.getEstimatedDownloadRateOfPeer();
/*     */   }
/*     */   
/*     */   public long getTotalDiscarded()
/*     */   {
/*  83 */     return this.delegate.getTotalBytesDiscarded();
/*     */   }
/*     */   
/*     */   public long getTotalSent()
/*     */   {
/*  88 */     return this.delegate.getTotalDataBytesSent();
/*     */   }
/*     */   
/*     */   public long getTotalReceived()
/*     */   {
/*  93 */     return this.delegate.getTotalDataBytesReceived();
/*     */   }
/*     */   
/*     */   public int getStatisticSentAverage()
/*     */   {
/*  98 */     return (int)this.delegate.getEstimatedUploadRateOfPeer();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToReceive()
/*     */   {
/* 104 */     return this.delegate.getPermittedBytesToReceive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void permittedReceiveBytesUsed(int bytes)
/*     */   {
/* 111 */     this.delegate.permittedReceiveBytesUsed(bytes);
/*     */     
/* 113 */     received(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToSend()
/*     */   {
/* 119 */     return this.delegate.getPermittedBytesToSend();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void permittedSendBytesUsed(int bytes)
/*     */   {
/* 126 */     this.delegate.permittedSendBytesUsed(bytes);
/*     */     
/* 128 */     sent(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void received(int bytes)
/*     */   {
/* 135 */     this.delegate.dataBytesReceived(bytes);
/*     */     
/* 137 */     this.manager.dataBytesReceived(this.delegate.getPeer(), bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void sent(int bytes)
/*     */   {
/* 144 */     this.delegate.dataBytesSent(bytes);
/*     */     
/* 146 */     this.manager.dataBytesSent(this.delegate.getPeer(), bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void discarded(int bytes)
/*     */   {
/* 153 */     this.delegate.bytesDiscarded(bytes);
/*     */     
/* 155 */     this.manager.discarded(this.delegate.getPeer(), bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeSinceConnectionEstablished()
/*     */   {
/* 161 */     return this.peer_manager.getTimeSinceConnectionEstablished(this.owner);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDownloadRateLimit()
/*     */   {
/* 167 */     return this.delegate.getDownloadRateLimitBytesPerSecond();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDownloadRateLimit(int bytes)
/*     */   {
/* 174 */     this.delegate.setDownloadRateLimitBytesPerSecond(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getUploadRateLimit()
/*     */   {
/* 180 */     return this.delegate.getUploadRateLimitBytesPerSecond();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setUploadRateLimit(int bytes)
/*     */   {
/* 187 */     this.delegate.setUploadRateLimitBytesPerSecond(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getOverallBytesRemaining()
/*     */   {
/* 193 */     return this.delegate.getPeer().getBytesRemaining();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/peers/PeerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */