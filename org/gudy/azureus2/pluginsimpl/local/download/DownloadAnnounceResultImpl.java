/*     */ package org.gudy.azureus2.pluginsimpl.local.download;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResultPeer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DownloadAnnounceResultImpl
/*     */   implements DownloadAnnounceResult
/*     */ {
/*     */   protected Download download;
/*     */   protected TRTrackerAnnouncerResponse response;
/*     */   
/*     */   public DownloadAnnounceResultImpl(Download _download, TRTrackerAnnouncerResponse _response)
/*     */   {
/*  54 */     this.download = _download;
/*  55 */     this.response = _response;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setContent(TRTrackerAnnouncerResponse _response)
/*     */   {
/*  64 */     this.response = _response;
/*     */   }
/*     */   
/*     */ 
/*     */   public Download getDownload()
/*     */   {
/*  70 */     return this.download;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseType()
/*     */   {
/*  76 */     if (this.response == null)
/*     */     {
/*  78 */       return 2;
/*     */     }
/*     */     
/*  81 */     int status = this.response.getStatus();
/*     */     
/*  83 */     if (status == 2)
/*     */     {
/*  85 */       return 1;
/*     */     }
/*     */     
/*  88 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getReportedPeerCount()
/*     */   {
/*  95 */     return (this.response == null) || (this.response.getPeers() == null) ? 0 : this.response.getPeers().length;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/* 101 */     PeerManager pm = this.download.getPeerManager();
/*     */     
/* 103 */     if (pm != null)
/*     */     {
/* 105 */       return pm.getStats().getConnectedSeeds();
/*     */     }
/*     */     
/* 108 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNonSeedCount()
/*     */   {
/* 114 */     PeerManager pm = this.download.getPeerManager();
/*     */     
/* 116 */     if (pm != null)
/*     */     {
/* 118 */       return pm.getStats().getConnectedLeechers();
/*     */     }
/*     */     
/* 121 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getError()
/*     */   {
/* 127 */     return this.response == null ? "No Response" : this.response.getAdditionalInfo();
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getURL()
/*     */   {
/* 133 */     return this.response == null ? null : this.response.getURL();
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadAnnounceResultPeer[] getPeers()
/*     */   {
/* 139 */     if (this.response == null)
/*     */     {
/* 141 */       return new DownloadAnnounceResultPeer[0];
/*     */     }
/*     */     
/* 144 */     return this.response.getPeers();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTimeToWait()
/*     */   {
/* 150 */     if (this.response == null)
/*     */     {
/* 152 */       return -1L;
/*     */     }
/*     */     
/* 155 */     return this.response.getTimeToWait();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getExtensions()
/*     */   {
/* 161 */     if (this.response == null)
/*     */     {
/* 163 */       return null;
/*     */     }
/*     */     
/* 166 */     return this.response.getExtensions();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadAnnounceResultImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */