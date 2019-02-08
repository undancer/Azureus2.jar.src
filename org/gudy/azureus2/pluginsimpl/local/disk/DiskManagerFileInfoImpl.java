/*     */ package org.gudy.azureus2.pluginsimpl.local.disk;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerRandomReadRequest;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
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
/*     */ public class DiskManagerFileInfoImpl
/*     */   implements org.gudy.azureus2.plugins.disk.DiskManagerFileInfo
/*     */ {
/*     */   protected DownloadImpl download;
/*     */   protected org.gudy.azureus2.core3.disk.DiskManagerFileInfo core;
/*     */   
/*     */   public DiskManagerFileInfoImpl(DownloadImpl _download, org.gudy.azureus2.core3.disk.DiskManagerFileInfo coreFileInfo)
/*     */   {
/*  53 */     this.core = coreFileInfo;
/*  54 */     this.download = _download;
/*     */   }
/*     */   
/*     */   public void setPriority(boolean b) {
/*  58 */     this.core.setPriority(b ? 1 : 0);
/*     */   }
/*     */   
/*     */   public void setSkipped(boolean b) {
/*  62 */     this.core.setSkipped(b);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumericPriorty()
/*     */   {
/*  68 */     return this.core.getPriority();
/*     */   }
/*     */   
/*     */   public int getNumericPriority() {
/*  72 */     return this.core.getPriority();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNumericPriority(int priority)
/*     */   {
/*  79 */     this.core.setPriority(priority);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDeleted(boolean b)
/*     */   {
/*  85 */     int st = this.core.getStorageType();
/*     */     
/*     */ 
/*     */ 
/*  89 */     if (b) {
/*     */       int target_st;
/*  91 */       if (st == 1)
/*     */       {
/*  93 */         target_st = 2;
/*     */       } else { int target_st;
/*  95 */         if (st == 3)
/*     */         {
/*  97 */           target_st = 4;
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       int target_st;
/*     */       
/*     */ 
/* 106 */       if (st == 2)
/*     */       {
/* 108 */         target_st = 1;
/*     */       } else { int target_st;
/* 110 */         if (st == 4)
/*     */         {
/* 112 */           target_st = 3;
/*     */         } else {
/*     */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     int target_st;
/* 120 */     this.core.setStorageType(target_st);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDeleted()
/*     */   {
/* 127 */     int st = this.core.getStorageType();
/*     */     
/* 129 */     return (st == 2) || (st == 4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLink(File link_destination)
/*     */   {
/* 136 */     this.core.setLink(link_destination);
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 142 */     return this.core.getLink();
/*     */   }
/*     */   
/*     */   public int getAccessMode()
/*     */   {
/* 147 */     return this.core.getAccessMode();
/*     */   }
/*     */   
/*     */   public long getDownloaded() {
/* 151 */     return this.core.getDownloaded();
/*     */   }
/*     */   
/*     */   public long getLength() {
/* 155 */     return this.core.getLength();
/*     */   }
/*     */   
/* 158 */   public File getFile() { return this.core.getFile(false); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 165 */     return this.core.getFile(follow_link);
/*     */   }
/*     */   
/*     */   public int getFirstPieceNumber() {
/* 169 */     return this.core.getFirstPieceNumber();
/*     */   }
/*     */   
/*     */   public long getPieceSize() {
/*     */     try {
/* 174 */       return getDownload().getTorrent().getPieceSize();
/*     */     }
/*     */     catch (Throwable e) {
/* 177 */       Debug.printStackTrace(e);
/*     */     }
/* 179 */     return 0L;
/*     */   }
/*     */   
/*     */   public int getNumPieces() {
/* 183 */     return this.core.getNbPieces();
/*     */   }
/*     */   
/*     */   public boolean isPriority() {
/* 187 */     return this.core.getPriority() != 0;
/*     */   }
/*     */   
/*     */   public boolean isSkipped() {
/* 191 */     return this.core.isSkipped();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 197 */     return this.core.getIndex();
/*     */   }
/*     */   
/*     */   public byte[] getDownloadHash()
/*     */     throws DownloadException
/*     */   {
/* 203 */     return getDownload().getTorrent().getHash();
/*     */   }
/*     */   
/*     */   public Download getDownload()
/*     */     throws DownloadException
/*     */   {
/* 209 */     if (this.download != null)
/*     */     {
/* 211 */       return this.download;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 216 */     return DownloadManagerImpl.getDownloadStatic(this.core.getDownloadManager());
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerChannel createChannel()
/*     */     throws DownloadException
/*     */   {
/* 223 */     return new DiskManagerChannelImpl(this.download, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerRandomReadRequest createRandomReadRequest(long file_offset, long length, boolean reverse_order, DiskManagerListener listener)
/*     */     throws DownloadException
/*     */   {
/* 235 */     return DiskManagerRandomReadController.createRequest(this.download, this, file_offset, length, reverse_order, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public org.gudy.azureus2.core3.disk.DiskManagerFileInfo getCore()
/*     */   {
/* 243 */     return this.core;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/disk/DiskManagerFileInfoImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */