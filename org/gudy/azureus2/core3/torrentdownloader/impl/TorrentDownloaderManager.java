/*     */ package org.gudy.azureus2.core3.torrentdownloader.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
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
/*     */ public class TorrentDownloaderManager
/*     */   implements TorrentDownloaderCallBackInterface
/*     */ {
/*  36 */   private static TorrentDownloaderManager man = null;
/*     */   
/*  38 */   private boolean logged = false;
/*  39 */   private boolean autostart = false;
/*  40 */   private GlobalManager gm = null;
/*     */   
/*     */   private String downloaddir;
/*  43 */   private final ArrayList running = new ArrayList();
/*  44 */   private final ArrayList queued = new ArrayList();
/*  45 */   private final ArrayList errors = new ArrayList();
/*     */   
/*     */   public TorrentDownloaderManager() {
/*     */     try {
/*  49 */       this.downloaddir = COConfigurationManager.getDirectoryParameter("Default save path");
/*     */     }
/*     */     catch (Exception e) {
/*  52 */       this.downloaddir = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public static TorrentDownloaderManager getInstance() {
/*  57 */     if (man == null)
/*  58 */       man = new TorrentDownloaderManager();
/*  59 */     return man;
/*     */   }
/*     */   
/*     */   public void init(GlobalManager _gm, boolean _logged, boolean _autostart, String _downloaddir) {
/*  63 */     this.gm = _gm;
/*  64 */     this.logged = _logged;
/*  65 */     this.autostart = _autostart;
/*  66 */     if (_downloaddir != null)
/*  67 */       this.downloaddir = _downloaddir;
/*     */   }
/*     */   
/*     */   public TorrentDownloader add(TorrentDownloader dl) {
/*  71 */     if (dl.getDownloadState() == 4) {
/*  72 */       this.errors.add(dl);
/*  73 */     } else if ((this.running.contains(dl)) || (this.queued.contains(dl))) {
/*  74 */       ((TorrentDownloaderImpl)dl).setDownloadState(5);
/*  75 */       ((TorrentDownloaderImpl)dl).notifyListener();
/*  76 */       this.errors.add(dl);
/*  77 */     } else if (this.autostart) {
/*  78 */       dl.start();
/*     */     } else {
/*  80 */       this.queued.add(dl); }
/*  81 */     return dl;
/*     */   }
/*     */   
/*     */   public TorrentDownloader download(String url, String fileordir, boolean logged) {
/*  85 */     return add(TorrentDownloaderFactory.create(this, url, null, fileordir, logged));
/*     */   }
/*     */   
/*     */   public TorrentDownloader download(String url, boolean logged) {
/*  89 */     return add(TorrentDownloaderFactory.create(this, url, null, null, logged));
/*     */   }
/*     */   
/*     */   public TorrentDownloader download(String url, String fileordir) {
/*  93 */     return add(TorrentDownloaderFactory.create(this, url, null, fileordir, this.logged));
/*     */   }
/*     */   
/*     */   public TorrentDownloader download(String url) {
/*  97 */     return add(TorrentDownloaderFactory.create(this, url, this.logged));
/*     */   }
/*     */   
/*     */   public void TorrentDownloaderEvent(int state, TorrentDownloader inf) {
/* 101 */     switch (state) {
/*     */     case 1: 
/* 103 */       if (this.queued.contains(inf))
/* 104 */         this.queued.remove(inf);
/* 105 */       if (!this.running.contains(inf))
/* 106 */         this.running.add(inf);
/*     */       break;
/*     */     case 3: 
/* 109 */       remove(inf);
/* 110 */       if ((this.gm != null) && (this.downloaddir != null)) {
/* 111 */         this.gm.addDownloadManager(inf.getFile().getAbsolutePath(), this.downloaddir);
/*     */       }
/*     */       break;
/*     */     case 4: 
/* 115 */       remove(inf);
/* 116 */       this.errors.add(inf);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove(TorrentDownloader inf)
/*     */   {
/* 125 */     if (this.running.contains(inf))
/* 126 */       this.running.remove(inf);
/* 127 */     if (this.queued.contains(inf)) {
/* 128 */       this.queued.remove(inf);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrentdownloader/impl/TorrentDownloaderManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */