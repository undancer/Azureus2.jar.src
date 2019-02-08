/*     */ package org.gudy.azureus2.core3.torrent.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
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
/*     */ public class TorrentOpenFileOptions
/*     */ {
/*     */   public final String orgFullName;
/*     */   public final String orgFileName;
/*     */   public final long lSize;
/*     */   private boolean toDownload;
/*     */   private int priority;
/*     */   private String destFileName;
/*     */   private String destPathName;
/*     */   private boolean didManualRename;
/*     */   private final int iIndex;
/*     */   public boolean isValid;
/*     */   public final TorrentOpenOptions parent;
/*     */   
/*     */   public TorrentOpenFileOptions(TorrentOpenOptions parent, int iIndex, String orgFullName, String orgFileName, long lSize, boolean wanted)
/*     */   {
/*  75 */     this.parent = parent;
/*  76 */     this.iIndex = iIndex;
/*  77 */     this.orgFullName = orgFullName;
/*  78 */     this.orgFileName = orgFileName;
/*     */     
/*  80 */     this.lSize = lSize;
/*     */     
/*  82 */     setToDownload(wanted);
/*     */     
/*  84 */     this.isValid = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/*  90 */     return this.iIndex;
/*     */   }
/*     */   
/*     */   public void setFullDestName(String newFullName)
/*     */   {
/*  95 */     if (newFullName == null)
/*     */     {
/*  97 */       setDestPathName(null);
/*  98 */       setDestFileName(null, true);
/*  99 */       return;
/*     */     }
/*     */     
/* 102 */     File newPath = new File(newFullName);
/* 103 */     setDestPathName(newPath.getParent());
/* 104 */     setDestFileName(newPath.getName(), true);
/*     */   }
/*     */   
/*     */   public void setDestPathName(String newPath)
/*     */   {
/* 109 */     if (this.parent.getTorrent().isSimpleTorrent()) {
/* 110 */       this.parent.setParentDir(newPath);
/*     */     } else {
/* 112 */       this.destPathName = newPath;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDestFileName(String newFileName, boolean manualRename) {
/* 117 */     if (this.orgFileName.equals(newFileName)) {
/* 118 */       this.destFileName = null;
/* 119 */       this.didManualRename = false;
/*     */     } else {
/* 121 */       this.destFileName = newFileName;
/* 122 */       this.didManualRename = manualRename;
/*     */     }
/*     */   }
/*     */   
/*     */   public String getDestPathName() {
/* 127 */     if (this.destPathName != null) {
/* 128 */       return this.destPathName;
/*     */     }
/* 130 */     if (this.parent.getTorrent().isSimpleTorrent()) {
/* 131 */       return this.parent.getParentDir();
/*     */     }
/* 133 */     return new File(this.parent.getDataDir(), this.orgFullName).getParent();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isManualRename()
/*     */   {
/* 139 */     return this.didManualRename;
/*     */   }
/*     */   
/*     */   public String getDestFileName() {
/* 143 */     return this.destFileName == null ? this.orgFileName : this.destFileName;
/*     */   }
/*     */   
/*     */   public File getDestFileFullName() {
/* 147 */     String path = getDestPathName();
/* 148 */     String file = getDestFileName();
/* 149 */     return new File(path, file);
/*     */   }
/*     */   
/*     */   public boolean okToDisable() {
/* 153 */     return this.parent.okToDisableAll();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getInitialLink()
/*     */   {
/* 159 */     return this.parent.getInitialLinkage(this.iIndex);
/*     */   }
/*     */   
/*     */   public boolean isLinked()
/*     */   {
/* 164 */     return (this.destFileName != null) || (this.destPathName != null);
/*     */   }
/*     */   
/*     */   public boolean isToDownload() {
/* 168 */     return this.toDownload;
/*     */   }
/*     */   
/*     */   public void setToDownload(boolean toDownload) {
/* 172 */     this.toDownload = toDownload;
/* 173 */     this.parent.fileDownloadStateChanged(this, toDownload);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 179 */     return this.priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPriority(int _priority)
/*     */   {
/* 186 */     this.priority = _priority;
/* 187 */     this.parent.filePriorityStateChanged(this, _priority);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/impl/TorrentOpenFileOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */