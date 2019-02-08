/*     */ package org.gudy.azureus2.core3.global.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostFactory;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentFinder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ class GlobalManagerHostSupport
/*     */   implements TRHostTorrentFinder
/*     */ {
/*     */   protected final GlobalManager gm;
/*     */   protected final TRHost host;
/*     */   
/*     */   protected GlobalManagerHostSupport(GlobalManager _gm)
/*     */   {
/*  45 */     this.gm = _gm;
/*     */     
/*  47 */     this.host = TRHostFactory.getSingleton();
/*     */     
/*  49 */     this.host.initialise(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TOTorrent lookupTorrent(byte[] hash)
/*     */   {
/*  56 */     DownloadManager dm = this.gm.getDownloadManager(new HashWrapper(hash));
/*     */     
/*  58 */     if (dm != null)
/*     */     {
/*  60 */       TOTorrent torrent = dm.getTorrent();
/*     */       
/*  62 */       if (torrent != null)
/*     */       {
/*  64 */         return torrent;
/*     */       }
/*     */     }
/*     */     
/*  68 */     TOTorrent torrent = DownloadManagerImpl.getStubTorrent(hash);
/*     */     
/*  70 */     return torrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void torrentRemoved(String torrent_file_str, TOTorrent torrent)
/*     */   {
/*  78 */     TRHostTorrent host_torrent = this.host.getHostTorrent(torrent);
/*     */     
/*  80 */     if (host_torrent != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  91 */       File torrent_file = new File(torrent_file_str);
/*     */       
/*  93 */       if (torrent_file.exists()) {
/*     */         try
/*     */         {
/*  96 */           TorrentUtils.writeToFile(host_torrent.getTorrent(), torrent_file, false);
/*     */           
/*  98 */           host_torrent.setPassive(true);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 102 */           Debug.out("Failed to make torrent '" + torrent_file_str + "' passive: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void torrentAdded(String torrent_file_str, TOTorrent torrent)
/*     */   {
/* 113 */     TRHostTorrent host_torrent = this.host.getHostTorrent(torrent);
/*     */     
/* 115 */     if (host_torrent != null)
/*     */     {
/* 117 */       if (host_torrent.getTorrent() != torrent)
/*     */       {
/* 119 */         host_torrent.setTorrent(torrent);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 127 */     this.host.close();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/impl/GlobalManagerHostSupport.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */