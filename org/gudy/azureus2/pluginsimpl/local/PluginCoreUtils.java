/*     */ package org.gudy.azureus2.pluginsimpl.local;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.network.Connection;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.disk.DiskManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.network.ConnectionImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.peers.PeerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.peers.PeerManagerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.tracker.TrackerTorrentImpl;
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
/*     */ public class PluginCoreUtils
/*     */ {
/*     */   public static Torrent wrap(TOTorrent t)
/*     */   {
/*  65 */     return new TorrentImpl(t);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static TOTorrent unwrap(Torrent t)
/*     */   {
/*  72 */     return ((TorrentImpl)t).getTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.plugins.disk.DiskManager wrap(org.gudy.azureus2.core3.disk.DiskManager dm)
/*     */   {
/*  79 */     return new DiskManagerImpl(dm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.core3.disk.DiskManager unwrap(org.gudy.azureus2.plugins.disk.DiskManager dm)
/*     */   {
/*  86 */     return ((DiskManagerImpl)dm).getDiskmanager();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Download wrap(DownloadManager dm)
/*     */   {
/*     */     try
/*     */     {
/* 100 */       return DownloadManagerImpl.getDownloadStatic(dm);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 106 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static NetworkConnection unwrap(Connection connection)
/*     */   {
/* 114 */     if ((connection instanceof ConnectionImpl))
/*     */     {
/* 116 */       return ((ConnectionImpl)connection).getCoreConnection();
/*     */     }
/*     */     
/* 119 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Connection wrap(NetworkConnection connection)
/*     */   {
/* 126 */     return new ConnectionImpl(connection, connection.isIncoming());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.plugins.disk.DiskManagerFileInfo wrap(org.gudy.azureus2.core3.disk.DiskManagerFileInfo info)
/*     */     throws DownloadException
/*     */   {
/* 135 */     if (info == null)
/*     */     {
/* 137 */       return null;
/*     */     }
/*     */     
/* 140 */     return new DiskManagerFileInfoImpl(DownloadManagerImpl.getDownloadStatic(info.getDownloadManager()), info);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.gudy.azureus2.core3.disk.DiskManagerFileInfo unwrap(org.gudy.azureus2.plugins.disk.DiskManagerFileInfo info)
/*     */     throws DownloadException
/*     */   {
/* 149 */     if ((info instanceof DiskManagerFileInfoImpl))
/*     */     {
/* 151 */       return ((DiskManagerFileInfoImpl)info).getCore();
/*     */     }
/*     */     
/* 154 */     if (info == null)
/*     */     {
/* 156 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 160 */       Download dl = info.getDownload();
/*     */       
/* 162 */       if (dl != null)
/*     */       {
/* 164 */         DownloadManager dm = unwrap(dl);
/*     */         
/* 166 */         return dm.getDiskManagerFileInfo()[info.getIndex()];
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 173 */     new org.gudy.azureus2.core3.disk.DiskManagerFileInfo()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void setPriority(int b)
/*     */       {
/*     */ 
/* 180 */         this.val$info.setNumericPriority(b);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setSkipped(boolean b)
/*     */       {
/* 187 */         this.val$info.setSkipped(b);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean setLink(File link_destination)
/*     */       {
/* 194 */         this.val$info.setLink(link_destination);
/*     */         
/* 196 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean setLinkAtomic(File link_destination)
/*     */       {
/* 203 */         this.val$info.setLink(link_destination);
/*     */         
/* 205 */         return true;
/*     */       }
/*     */       
/*     */ 
/*     */       public File getLink()
/*     */       {
/* 211 */         return this.val$info.getLink();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean setStorageType(int type)
/*     */       {
/* 218 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getStorageType()
/*     */       {
/* 224 */         return 1;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getAccessMode()
/*     */       {
/* 230 */         return this.val$info.getAccessMode();
/*     */       }
/*     */       
/*     */ 
/*     */       public long getDownloaded()
/*     */       {
/* 236 */         return this.val$info.getDownloaded();
/*     */       }
/*     */       
/*     */ 
/*     */       public String getExtension()
/*     */       {
/* 242 */         return "";
/*     */       }
/*     */       
/*     */ 
/*     */       public int getFirstPieceNumber()
/*     */       {
/* 248 */         return this.val$info.getFirstPieceNumber();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getLastPieceNumber()
/*     */       {
/* 254 */         return (int)((this.val$info.getLength() + this.val$info.getPieceSize() - 1L) / this.val$info.getPieceSize());
/*     */       }
/*     */       
/*     */ 
/*     */       public long getLength()
/*     */       {
/* 260 */         return this.val$info.getLength();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getNbPieces()
/*     */       {
/* 266 */         return this.val$info.getNumPieces();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPriority()
/*     */       {
/* 272 */         return this.val$info.getNumericPriorty();
/*     */       }
/*     */       
/*     */ 
/*     */       public boolean isSkipped()
/*     */       {
/* 278 */         return this.val$info.isSkipped();
/*     */       }
/*     */       
/*     */ 
/*     */       public int getIndex()
/*     */       {
/* 284 */         return this.val$info.getIndex();
/*     */       }
/*     */       
/*     */ 
/*     */       public DownloadManager getDownloadManager()
/*     */       {
/* 290 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */       public org.gudy.azureus2.core3.disk.DiskManager getDiskManager()
/*     */       {
/* 296 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public File getFile(boolean follow_link)
/*     */       {
/* 303 */         if (follow_link)
/*     */         {
/* 305 */           return this.val$info.getLink();
/*     */         }
/*     */         
/*     */ 
/* 309 */         return this.val$info.getFile();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public TOTorrentFile getTorrentFile()
/*     */       {
/* 316 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public DirectByteBuffer read(long offset, int length)
/*     */         throws IOException
/*     */       {
/* 326 */         throw new IOException("unsupported");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void flushCache()
/*     */         throws Exception
/*     */       {}
/*     */       
/*     */ 
/*     */ 
/*     */       public int getReadBytesPerSecond()
/*     */       {
/* 339 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getWriteBytesPerSecond()
/*     */       {
/* 345 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */       public long getETA()
/*     */       {
/* 351 */         return -1L;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void close() {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void addListener(DiskManagerFileInfoListener listener) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void removeListener(DiskManagerFileInfoListener listener) {}
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Object convert(Object datasource, boolean toCore)
/*     */   {
/* 378 */     if ((datasource instanceof Object[])) {
/* 379 */       Object[] array = (Object[])datasource;
/* 380 */       Object[] newArray = new Object[array.length];
/* 381 */       for (int i = 0; i < array.length; i++) {
/* 382 */         Object o = array[i];
/* 383 */         newArray[i] = convert(o, toCore);
/*     */       }
/* 385 */       return newArray;
/*     */     }
/*     */     try
/*     */     {
/* 389 */       if (toCore) {
/* 390 */         if ((datasource instanceof DownloadManager)) {
/* 391 */           return datasource;
/*     */         }
/* 393 */         if ((datasource instanceof DownloadImpl)) {
/* 394 */           return ((DownloadImpl)datasource).getDownload();
/*     */         }
/*     */         
/* 397 */         if ((datasource instanceof org.gudy.azureus2.core3.disk.DiskManager)) {
/* 398 */           return datasource;
/*     */         }
/* 400 */         if ((datasource instanceof DiskManagerImpl)) {
/* 401 */           return ((DiskManagerImpl)datasource).getDiskmanager();
/*     */         }
/*     */         
/* 404 */         if ((datasource instanceof PEPeerManager)) {
/* 405 */           return datasource;
/*     */         }
/* 407 */         if ((datasource instanceof PeerManagerImpl)) {
/* 408 */           return ((PeerManagerImpl)datasource).getDelegate();
/*     */         }
/*     */         
/* 411 */         if ((datasource instanceof PEPeer)) {
/* 412 */           return datasource;
/*     */         }
/* 414 */         if ((datasource instanceof PeerImpl)) {
/* 415 */           return ((PeerImpl)datasource).getPEPeer();
/*     */         }
/*     */         
/* 418 */         if ((datasource instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 419 */           return datasource;
/*     */         }
/* 421 */         if ((datasource instanceof DiskManagerFileInfoImpl)) {
/* 422 */           return ((DiskManagerFileInfoImpl)datasource).getCore();
/*     */         }
/*     */         
/* 425 */         if ((datasource instanceof TRHostTorrent)) {
/* 426 */           return datasource;
/*     */         }
/* 428 */         if ((datasource instanceof TrackerTorrentImpl)) {
/* 429 */           ((TrackerTorrentImpl)datasource).getHostTorrent();
/*     */         }
/*     */       } else {
/* 432 */         if ((datasource instanceof DownloadManager)) {
/* 433 */           return wrap((DownloadManager)datasource);
/*     */         }
/* 435 */         if ((datasource instanceof DownloadImpl)) {
/* 436 */           return datasource;
/*     */         }
/*     */         
/* 439 */         if ((datasource instanceof org.gudy.azureus2.core3.disk.DiskManager)) {
/* 440 */           return wrap((org.gudy.azureus2.core3.disk.DiskManager)datasource);
/*     */         }
/* 442 */         if ((datasource instanceof DiskManagerImpl)) {
/* 443 */           return datasource;
/*     */         }
/*     */         
/* 446 */         if ((datasource instanceof PEPeerManager)) {
/* 447 */           return wrap((PEPeerManager)datasource);
/*     */         }
/* 449 */         if ((datasource instanceof PeerManagerImpl)) {
/* 450 */           return datasource;
/*     */         }
/*     */         
/* 453 */         if ((datasource instanceof PEPeer)) {
/* 454 */           return PeerManagerImpl.getPeerForPEPeer((PEPeer)datasource);
/*     */         }
/* 456 */         if ((datasource instanceof Peer)) {
/* 457 */           return datasource;
/*     */         }
/*     */         
/* 460 */         if ((datasource instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 461 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)datasource;
/* 462 */           if (fileInfo != null) {
/*     */             try {
/* 464 */               return new DiskManagerFileInfoImpl(DownloadManagerImpl.getDownloadStatic(fileInfo.getDownloadManager()), fileInfo);
/*     */             }
/*     */             catch (DownloadException e) {}
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 471 */         if ((datasource instanceof DiskManagerFileInfoImpl)) {
/* 472 */           return datasource;
/*     */         }
/*     */         
/* 475 */         if ((datasource instanceof TRHostTorrent)) {
/* 476 */           TRHostTorrent item = (TRHostTorrent)datasource;
/* 477 */           return new TrackerTorrentImpl(item);
/*     */         }
/* 479 */         if ((datasource instanceof TrackerTorrentImpl)) {
/* 480 */           return datasource;
/*     */         }
/*     */       }
/*     */     } catch (Throwable t) {
/* 484 */       Debug.out(t);
/*     */     }
/*     */     
/* 487 */     return datasource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DownloadManager unwrapIfPossible(Download dm)
/*     */   {
/* 496 */     if ((dm instanceof DownloadImpl))
/*     */     {
/* 498 */       return ((DownloadImpl)dm).getDownload();
/*     */     }
/*     */     
/*     */ 
/* 502 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DownloadManager unwrap(Download dm)
/*     */   {
/* 510 */     if ((dm instanceof DownloadImpl))
/*     */     {
/* 512 */       return ((DownloadImpl)dm).getDownload();
/*     */     }
/*     */     
/*     */ 
/* 516 */     Debug.out("Can't unwrap " + dm);
/*     */     
/* 518 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeerManager wrap(PEPeerManager pm)
/*     */   {
/* 526 */     return PeerManagerImpl.getPeerManager(pm);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static PEPeerManager unwrap(PeerManager pm)
/*     */   {
/* 533 */     return ((PeerManagerImpl)pm).getDelegate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static TRTrackerServerTorrent unwrap(TrackerTorrent torrent)
/*     */   {
/* 540 */     return ((TrackerTorrentImpl)torrent).getHostTorrent().getTrackerTorrent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static PEPeer unwrap(Peer peer)
/*     */   {
/* 547 */     return ((PeerImpl)peer).getDelegate();
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isInitialisationComplete()
/*     */   {
/* 553 */     return PluginInitializer.getDefaultInterface().getPluginState().isInitialisationComplete();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginCoreUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */