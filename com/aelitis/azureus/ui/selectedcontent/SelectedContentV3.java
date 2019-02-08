/*     */ package com.aelitis.azureus.ui.selectedcontent;
/*     */ 
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
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
/*     */ 
/*     */ public class SelectedContentV3
/*     */   implements ISelectedContent
/*     */ {
/*     */   private final SelectedContent content;
/*     */   private boolean isPlatformContent;
/*     */   private boolean canPlay;
/*     */   private String thumbURL;
/*     */   private byte[] imageBytes;
/*     */   private DownloadUrlInfo downloadInfo;
/*     */   
/*     */   public SelectedContentV3(SelectedContent content)
/*     */   {
/*  56 */     this.content = content;
/*  57 */     setDownloadManager(content.getDownloadManager());
/*     */   }
/*     */   
/*     */   public SelectedContentV3() {
/*  61 */     this.content = new SelectedContent();
/*     */   }
/*     */   
/*     */   public SelectedContentV3(String hash, String displayName, boolean isPlatformContent, boolean canPlay)
/*     */   {
/*  66 */     this.isPlatformContent = isPlatformContent;
/*  67 */     this.canPlay = canPlay;
/*  68 */     this.content = new SelectedContent(hash, displayName);
/*     */   }
/*     */   
/*     */   public SelectedContentV3(DownloadManager dm) throws Exception {
/*  72 */     this.content = new SelectedContent();
/*  73 */     setDownloadManager(dm);
/*     */   }
/*     */   
/*     */   public String getDisplayName()
/*     */   {
/*  78 */     return this.content.getDisplayName();
/*     */   }
/*     */   
/*     */   public DownloadManager getDownloadManager()
/*     */   {
/*  83 */     return this.content.getDownloadManager();
/*     */   }
/*     */   
/*     */   public int getFileIndex() {
/*  87 */     return this.content.getFileIndex();
/*     */   }
/*     */   
/*  90 */   public TOTorrent getTorrent() { return this.content.getTorrent(); }
/*     */   
/*     */ 
/*     */   public String getHash()
/*     */   {
/*  95 */     return this.content.getHash();
/*     */   }
/*     */   
/*     */   public void setDisplayName(String displayName)
/*     */   {
/* 100 */     this.content.setDisplayName(displayName);
/*     */   }
/*     */   
/*     */   public void setDownloadManager(DownloadManager dm)
/*     */   {
/* 105 */     this.content.setDownloadManager(dm);
/* 106 */     if (dm != null) {
/* 107 */       setTorrent(dm.getTorrent());
/*     */       
/* 109 */       setDisplayName(PlatformTorrentUtils.getContentTitle2(dm));
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTorrent(TOTorrent torrent) {
/* 114 */     this.content.setTorrent(torrent);
/*     */     
/* 116 */     if (torrent != null)
/*     */     {
/*     */       try {
/* 119 */         setHash(torrent.getHashWrapper().toBase32String());
/*     */       } catch (Exception e) {
/* 121 */         setHash(null);
/*     */       }
/* 123 */       setPlatformContent(PlatformTorrentUtils.isContent(torrent, true));
/* 124 */       setDisplayName(PlatformTorrentUtils.getContentTitle(torrent));
/* 125 */       setCanPlay(PlayUtils.canUseEMP(torrent, -1));
/* 126 */       setImageBytes(PlatformTorrentUtils.getContentThumbnail(torrent));
/*     */     }
/*     */   }
/*     */   
/*     */   public void setHash(String hash)
/*     */   {
/* 132 */     this.content.setHash(hash);
/*     */   }
/*     */   
/*     */   public void setHash(String hash, boolean isPlatformContent) {
/* 136 */     this.content.setHash(hash);
/* 137 */     setPlatformContent(isPlatformContent);
/*     */   }
/*     */   
/*     */   public boolean isPlatformContent() {
/* 141 */     return this.isPlatformContent;
/*     */   }
/*     */   
/*     */   public void setPlatformContent(boolean isPlatformContent) {
/* 145 */     this.isPlatformContent = isPlatformContent;
/*     */   }
/*     */   
/*     */   public boolean canPlay() {
/* 149 */     return this.canPlay;
/*     */   }
/*     */   
/*     */   public void setCanPlay(boolean canPlay) {
/* 153 */     this.canPlay = canPlay;
/*     */   }
/*     */   
/*     */   public String getThumbURL() {
/* 157 */     return this.thumbURL;
/*     */   }
/*     */   
/*     */   public void setThumbURL(String thumbURL) {
/* 161 */     this.thumbURL = thumbURL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImageBytes(byte[] imageBytes)
/*     */   {
/* 168 */     this.imageBytes = imageBytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] getImageBytes()
/*     */   {
/* 175 */     return this.imageBytes;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadUrlInfo getDownloadInfo()
/*     */   {
/* 181 */     return this.downloadInfo;
/*     */   }
/*     */   
/*     */   public void setDownloadInfo(DownloadUrlInfo info)
/*     */   {
/* 186 */     this.downloadInfo = info;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean sameAs(ISelectedContent _other)
/*     */   {
/* 193 */     if (_other == this)
/*     */     {
/* 195 */       return true;
/*     */     }
/*     */     
/* 198 */     if ((_other instanceof SelectedContentV3))
/*     */     {
/* 200 */       SelectedContentV3 other = (SelectedContentV3)_other;
/*     */       
/* 202 */       if (!this.content.sameAs(other.content))
/*     */       {
/* 204 */         return false;
/*     */       }
/*     */       
/* 207 */       if ((this.isPlatformContent != other.isPlatformContent) || (this.canPlay != other.canPlay))
/*     */       {
/*     */ 
/* 210 */         return false;
/*     */       }
/*     */       
/* 213 */       if (this.thumbURL != other.thumbURL)
/*     */       {
/* 215 */         if ((this.thumbURL == null) || (other.thumbURL == null) || (!this.thumbURL.equals(other.thumbURL)))
/*     */         {
/*     */ 
/*     */ 
/* 219 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 223 */       if (this.imageBytes != other.imageBytes)
/*     */       {
/* 225 */         return false;
/*     */       }
/*     */       
/* 228 */       if (this.downloadInfo != other.downloadInfo)
/*     */       {
/* 230 */         if ((this.downloadInfo == null) || (other.downloadInfo == null) || (!this.downloadInfo.sameAs(other.downloadInfo)))
/*     */         {
/*     */ 
/*     */ 
/* 234 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 238 */       return true;
/*     */     }
/*     */     
/* 241 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/selectedcontent/SelectedContentV3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */