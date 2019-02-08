/*     */ package com.aelitis.azureus.ui.selectedcontent;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Base32;
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
/*     */ public class SelectedContent
/*     */   implements ISelectedContent
/*     */ {
/*     */   private String hash;
/*     */   private DownloadManager dm;
/*  44 */   private int file_index = -1;
/*     */   
/*     */ 
/*     */   private TOTorrent torrent;
/*     */   
/*     */ 
/*     */   private String displayName;
/*     */   
/*     */ 
/*     */   private DownloadUrlInfo downloadInfo;
/*     */   
/*     */ 
/*     */   public SelectedContent(DownloadManager dm)
/*     */   {
/*  58 */     setDownloadManager(dm);
/*     */   }
/*     */   
/*     */   public SelectedContent(DownloadManager dm, int _file_index) {
/*  62 */     setDownloadManager(dm);
/*  63 */     this.file_index = _file_index;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public SelectedContent(String hash, String displayName)
/*     */   {
/*  70 */     this.hash = hash;
/*  71 */     this.displayName = displayName;
/*     */   }
/*     */   
/*     */ 
/*     */   public SelectedContent() {}
/*     */   
/*     */   public String getHash()
/*     */   {
/*  79 */     return this.hash;
/*     */   }
/*     */   
/*     */   public void setHash(String hash)
/*     */   {
/*  84 */     this.hash = hash;
/*     */   }
/*     */   
/*     */   public DownloadManager getDownloadManager()
/*     */   {
/*  89 */     if ((this.dm == null) && (this.hash != null)) {
/*     */       try {
/*  91 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*  92 */         return gm.getDownloadManager(new HashWrapper(Base32.decode(this.hash)));
/*     */       }
/*     */       catch (Exception ignore) {}
/*     */     }
/*     */     
/*  97 */     return this.dm;
/*     */   }
/*     */   
/*     */   public void setDownloadManager(DownloadManager _dm)
/*     */   {
/* 102 */     this.dm = _dm;
/* 103 */     if (this.dm != null) {
/* 104 */       setTorrent(this.dm.getTorrent());
/* 105 */       setDisplayName(this.dm.getDisplayName());
/*     */     }
/*     */   }
/*     */   
/*     */   public int getFileIndex() {
/* 110 */     return this.file_index;
/*     */   }
/*     */   
/*     */   public TOTorrent getTorrent() {
/* 114 */     return this.torrent;
/*     */   }
/*     */   
/*     */   public void setTorrent(TOTorrent _torrent) {
/* 118 */     this.torrent = _torrent;
/*     */     
/* 120 */     if (this.torrent != null) {
/*     */       try {
/* 122 */         this.hash = this.torrent.getHashWrapper().toBase32String();
/*     */       } catch (Exception e) {
/* 124 */         this.hash = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String getDisplayName()
/*     */   {
/* 131 */     return this.displayName;
/*     */   }
/*     */   
/*     */   public void setDisplayName(String displayName)
/*     */   {
/* 136 */     this.displayName = displayName;
/*     */   }
/*     */   
/*     */   public DownloadUrlInfo getDownloadInfo()
/*     */   {
/* 141 */     return this.downloadInfo;
/*     */   }
/*     */   
/*     */   public void setDownloadInfo(DownloadUrlInfo info)
/*     */   {
/* 146 */     this.downloadInfo = info;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean sameAs(ISelectedContent _other)
/*     */   {
/* 153 */     if ((_other instanceof SelectedContent))
/*     */     {
/* 155 */       SelectedContent other = (SelectedContent)_other;
/*     */       
/* 157 */       if (this.hash != other.hash)
/*     */       {
/* 159 */         if ((this.hash == null) || (other.hash == null) || (!this.hash.equals(other.hash)))
/*     */         {
/*     */ 
/*     */ 
/* 163 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 167 */       if ((this.dm != other.dm) || (this.torrent != other.torrent) || (this.file_index != other.file_index))
/*     */       {
/*     */ 
/*     */ 
/* 171 */         return false;
/*     */       }
/*     */       
/* 174 */       if (this.displayName != other.displayName)
/*     */       {
/* 176 */         if ((this.displayName == null) || (other.displayName == null) || (!this.displayName.equals(other.displayName)))
/*     */         {
/*     */ 
/*     */ 
/* 180 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 184 */       if (this.downloadInfo != other.downloadInfo)
/*     */       {
/* 186 */         if ((this.downloadInfo == null) || (other.downloadInfo == null) || (!this.downloadInfo.sameAs(other.downloadInfo)))
/*     */         {
/*     */ 
/*     */ 
/* 190 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 194 */       return true;
/*     */     }
/*     */     
/* 197 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/selectedcontent/SelectedContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */