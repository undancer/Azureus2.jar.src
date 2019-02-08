/*     */ package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.HTTPUtils;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Arrays;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderCancelledException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResourceDownloaderTorrentImpl
/*     */   extends ResourceDownloaderBaseImpl
/*     */   implements ResourceDownloaderListener
/*     */ {
/*     */   public static final int MAX_FOLLOWS = 1;
/*     */   protected ResourceDownloaderBaseImpl delegate;
/*     */   protected boolean persistent;
/*     */   protected File download_dir;
/*  53 */   protected long size = -2L;
/*     */   
/*     */ 
/*     */ 
/*  57 */   protected TOTorrent[] torrent_holder = new TOTorrent[1];
/*     */   
/*     */   protected org.gudy.azureus2.plugins.download.DownloadManager download_manager;
/*     */   
/*     */   protected Download download;
/*     */   
/*     */   protected boolean cancelled;
/*     */   protected boolean completed;
/*     */   protected ResourceDownloader current_downloader;
/*     */   protected Object result;
/*  67 */   protected AESemaphore done_sem = new AESemaphore("RDTorrent");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderTorrentImpl(ResourceDownloaderBaseImpl _parent, ResourceDownloader _delegate, boolean _persistent, File _download_dir)
/*     */   {
/*  76 */     super(_parent);
/*     */     
/*  78 */     this.persistent = _persistent;
/*  79 */     this.download_dir = _download_dir;
/*  80 */     this.delegate = ((ResourceDownloaderBaseImpl)_delegate);
/*     */     
/*  82 */     this.delegate.setParent(this);
/*     */     
/*  84 */     this.download_manager = PluginInitializer.getDefaultInterface().getDownloadManager();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  90 */     return this.delegate.getName() + ": torrent";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getSize()
/*     */     throws ResourceDownloaderException
/*     */   {
/*  98 */     if (this.size == -2L) {
/*     */       try
/*     */       {
/* 101 */         this.size = getSizeSupport();
/*     */       }
/*     */       finally
/*     */       {
/* 105 */         if (this.size == -2L)
/*     */         {
/* 107 */           this.size = -1L;
/*     */         }
/*     */         
/* 110 */         setSize(this.size);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 115 */     return this.size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSize(long l)
/*     */   {
/* 122 */     this.size = l;
/*     */     
/* 124 */     if (this.size >= 0L)
/*     */     {
/* 126 */       this.delegate.setSize(this.size);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(String name, Object value)
/*     */     throws ResourceDownloaderException
/*     */   {
/* 137 */     setPropertySupport(name, value);
/*     */     
/* 139 */     this.delegate.setProperty(name, value);
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getSizeSupport()
/*     */     throws ResourceDownloaderException
/*     */   {
/*     */     try
/*     */     {
/* 148 */       if (this.torrent_holder[0] == null)
/*     */       {
/* 150 */         ResourceDownloader x = this.delegate.getClone(this);
/*     */         
/* 152 */         addReportListener(x);
/*     */         
/* 154 */         InputStream is = x.download();
/*     */         try
/*     */         {
/* 157 */           this.torrent_holder[0] = TOTorrentFactory.deserialiseFromBEncodedInputStream(is);
/*     */           
/*     */ 
/*     */           try
/*     */           {
/* 162 */             is.close();
/*     */           }
/*     */           catch (IOException e) {}
/*     */           
/*     */ 
/*     */ 
/* 168 */           if (this.torrent_holder[0].isSimpleTorrent()) {
/*     */             break label90;
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/*     */           try
/*     */           {
/* 162 */             is.close();
/*     */           }
/*     */           catch (IOException e) {}
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 170 */         throw new ResourceDownloaderException(this, "Only simple torrents supported");
/*     */       }
/*     */       try
/*     */       {
/*     */         label90:
/* 175 */         String file_str = new String(this.torrent_holder[0].getName());
/*     */         
/* 177 */         int pos = file_str.lastIndexOf(".");
/*     */         
/*     */         String file_type;
/*     */         String file_type;
/* 181 */         if (pos != -1)
/*     */         {
/* 183 */           file_type = file_str.substring(pos + 1);
/*     */         }
/*     */         else
/*     */         {
/* 187 */           file_type = null;
/*     */         }
/*     */         
/* 190 */         setProperty("ContentType", HTTPUtils.guessContentTypeFromFileType(file_type));
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 195 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 198 */       return this.torrent_holder[0].getSize();
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 202 */       throw new ResourceDownloaderException(this, "Torrent deserialisation failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setSizeAndTorrent(long _size, TOTorrent[] _torrent_holder)
/*     */   {
/* 211 */     this.size = _size;
/* 212 */     this.torrent_holder = _torrent_holder;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ResourceDownloaderBaseImpl getClone(ResourceDownloaderBaseImpl parent)
/*     */   {
/* 219 */     ResourceDownloaderTorrentImpl c = new ResourceDownloaderTorrentImpl(parent, this.delegate.getClone(this), this.persistent, this.download_dir);
/*     */     
/* 221 */     c.setSizeAndTorrent(this.size, this.torrent_holder);
/*     */     
/* 223 */     c.setProperties(this);
/*     */     
/* 225 */     return c;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream download()
/*     */     throws ResourceDownloaderException
/*     */   {
/* 233 */     asyncDownload();
/*     */     
/* 235 */     this.done_sem.reserve();
/*     */     
/* 237 */     if ((this.result instanceof InputStream))
/*     */     {
/* 239 */       return (InputStream)this.result;
/*     */     }
/*     */     
/* 242 */     throw ((ResourceDownloaderException)this.result);
/*     */   }
/*     */   
/*     */   public void asyncDownload()
/*     */   {
/*     */     try
/*     */     {
/* 249 */       this.this_mon.enter();
/*     */       
/* 251 */       if (this.cancelled)
/*     */       {
/* 253 */         this.done_sem.release();
/*     */         
/* 255 */         informFailed((ResourceDownloaderException)this.result);
/*     */ 
/*     */ 
/*     */       }
/* 259 */       else if (this.torrent_holder[0] == null)
/*     */       {
/* 261 */         this.current_downloader = this.delegate.getClone(this);
/*     */         
/* 263 */         informActivity(getLogIndent() + "Downloading: " + getName());
/*     */         
/* 265 */         this.current_downloader.addListener(this);
/*     */         
/* 267 */         this.current_downloader.asyncDownload();
/*     */       }
/*     */       else
/*     */       {
/* 271 */         downloadTorrent();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 276 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void downloadTorrent()
/*     */   {
/*     */     try
/*     */     {
/* 284 */       String name = new String(this.torrent_holder[0].getName(), "UTF8");
/*     */       
/* 286 */       informActivity(getLogIndent() + "Downloading: " + name);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 292 */       final File torrent_file = AETemporaryFileHandler.createSemiTempFile();
/*     */       
/* 294 */       if ((this.download_dir != null) && (!this.download_dir.exists()))
/*     */       {
/* 296 */         FileUtil.mkdirs(this.download_dir);
/*     */       }
/*     */       
/* 299 */       final File data_dir = this.download_dir == null ? torrent_file.getParentFile() : this.download_dir;
/*     */       
/* 301 */       final TOTorrent torrent = this.torrent_holder[0];
/*     */       
/* 303 */       TorrentUtils.setFlag(torrent, 1, true);
/*     */       
/* 305 */       boolean anon = isAnonymous();
/*     */       
/* 307 */       if (anon)
/*     */       {
/*     */ 
/*     */ 
/* 311 */         TorrentUtils.announceGroupsInsertFirst(torrent, "http://crs2nugpvoqygnpabqbopwyjqettwszth6ubr2fh7whstlos3a6q.b32.i2p:17979/announce");
/*     */       }
/*     */       
/* 314 */       torrent.serialiseToBEncodedFile(torrent_file);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 319 */         Download existing = this.download_manager.getDownload(torrent.getHash());
/*     */         
/* 321 */         if (existing != null)
/*     */         {
/* 323 */           int existing_state = existing.getState();
/*     */           
/* 325 */           if ((existing_state == 8) || (existing_state == 7))
/*     */           {
/* 327 */             informActivity(getLogIndent() + "Deleting existing stopped/error state download for " + name);
/*     */             
/* 329 */             existing.remove(true, true);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/* 335 */       DownloadWillBeAddedListener dwbal = null;
/*     */       try
/*     */       {
/* 338 */         Torrent t = new TorrentImpl(torrent);
/*     */         
/* 340 */         if (anon)
/*     */         {
/* 342 */           dwbal = new DownloadWillBeAddedListener()
/*     */           {
/*     */ 
/*     */             public void initialised(Download download)
/*     */             {
/*     */ 
/*     */               try
/*     */               {
/* 350 */                 if (Arrays.equals(download.getTorrentHash(), torrent.getHash()))
/*     */                 {
/* 352 */                   PluginCoreUtils.unwrap(download).getDownloadState().setNetworks(AENetworkClassifier.AT_NON_PUBLIC);
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 356 */                 Debug.out(e);
/*     */               }
/*     */               
/*     */             }
/* 360 */           };
/* 361 */           this.download_manager.addDownloadWillBeAddedListener(dwbal);
/*     */         }
/*     */         
/* 364 */         if (this.persistent)
/*     */         {
/* 366 */           this.download = this.download_manager.addDownload(t, torrent_file, data_dir);
/*     */         }
/*     */         else
/*     */         {
/* 370 */           this.download = this.download_manager.addNonPersistentDownload(t, torrent_file, data_dir);
/*     */         }
/*     */       }
/*     */       finally {
/* 374 */         if (dwbal != null)
/*     */         {
/* 376 */           this.download_manager.removeDownloadWillBeAddedListener(dwbal);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 381 */       this.download.moveTo(1);
/*     */       
/* 383 */       this.download.setForceStart(true);
/*     */       
/*     */ 
/*     */ 
/* 387 */       this.download.setFlag(4L, true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 394 */       this.download_manager.addListener(new DownloadManagerListener()
/*     */       {
/*     */         public void downloadAdded(Download download) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void downloadRemoved(Download _download)
/*     */         {
/* 407 */           if (ResourceDownloaderTorrentImpl.this.download == _download)
/*     */           {
/* 409 */             ResourceDownloaderTorrentImpl.this.downloadRemoved(torrent_file, data_dir);
/*     */           }
/*     */           
/*     */         }
/* 413 */       });
/* 414 */       this.download.addListener(new DownloadListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void stateChanged(final Download download, int old_state, int new_state)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 425 */           if (new_state == 5)
/*     */           {
/* 427 */             download.removeListener(this);
/*     */             
/* 429 */             PluginInitializer.getDefaultInterface().getUtilities().createThread("resource complete event dispatcher", new Runnable()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 436 */                 ResourceDownloaderTorrentImpl.this.downloadSucceeded(download, ResourceDownloaderTorrentImpl.3.this.val$torrent_file, ResourceDownloaderTorrentImpl.3.this.val$data_dir);
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void positionChanged(Download download, int oldPosition, int newPosition) {}
/* 451 */       });
/* 452 */       Thread t = new AEThread("RDTorrent percentage checker")
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 458 */           int last_percentage = 0;
/*     */           
/* 460 */           while (ResourceDownloaderTorrentImpl.this.result == null)
/*     */           {
/* 462 */             int this_percentage = ResourceDownloaderTorrentImpl.this.download.getStats().getDownloadCompleted(false) / 10;
/*     */             
/* 464 */             long total = torrent.getSize();
/*     */             
/* 466 */             if (this_percentage != last_percentage)
/*     */             {
/* 468 */               ResourceDownloaderTorrentImpl.this.reportPercentComplete(ResourceDownloaderTorrentImpl.this, this_percentage);
/*     */               
/* 470 */               last_percentage = this_percentage;
/*     */             }
/*     */             try
/*     */             {
/* 474 */               Thread.sleep(1000L);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 478 */               Debug.printStackTrace(e);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 483 */       };
/* 484 */       t.setDaemon(true);
/*     */       
/* 486 */       t.start();
/*     */       
/*     */ 
/*     */ 
/* 490 */       if (this.download.getState() == 5)
/*     */       {
/* 492 */         downloadSucceeded(this.download, torrent_file, data_dir);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 496 */       failed(this, new ResourceDownloaderException(this, "Torrent download failed", e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void downloadSucceeded(Download download, File torrent_file, File data_dir)
/*     */   {
/* 506 */     synchronized (this)
/*     */     {
/* 508 */       if (this.completed)
/*     */       {
/* 510 */         return;
/*     */       }
/*     */       
/* 513 */       this.completed = true;
/*     */     }
/*     */     
/* 516 */     reportActivity("Torrent download complete");
/*     */     
/*     */ 
/*     */ 
/* 520 */     File target_file = new File(data_dir, new String(this.torrent_holder[0].getFiles()[0].getPathComponents()[0]));
/*     */     
/*     */ 
/* 523 */     if (!target_file.exists())
/*     */     {
/* 525 */       File actual_target_file = new File(download.getSavePath());
/*     */       try
/*     */       {
/* 528 */         if ((this.download_dir != null) && (actual_target_file.exists()))
/*     */         {
/* 530 */           FileUtil.copyFile(actual_target_file, target_file);
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 535 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 538 */       target_file = actual_target_file;
/*     */     }
/*     */     try
/*     */     {
/* 542 */       if (!target_file.exists())
/*     */       {
/* 544 */         throw new Exception("File '" + target_file.toString() + "' not found");
/*     */       }
/*     */       
/* 547 */       Object data = new FileInputStream(target_file);
/*     */       
/* 549 */       informComplete((InputStream)data);
/*     */       
/* 551 */       this.result = data;
/*     */       
/* 553 */       this.done_sem.release();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 557 */       Debug.printStackTrace(e);
/*     */       
/* 559 */       failed(this, new ResourceDownloaderException(this, "Failed to read downloaded torrent data: " + e.getMessage(), e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void downloadRemoved(File torrent_file, File data_dir)
/*     */   {
/* 568 */     reportActivity("Torrent removed");
/*     */     
/* 570 */     if (!(this.result instanceof InputStream))
/*     */     {
/* 572 */       failed(this, new ResourceDownloaderException(this, "Download did not complete"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 579 */     setCancelled();
/*     */     try
/*     */     {
/* 582 */       this.this_mon.enter();
/*     */       
/* 584 */       this.result = new ResourceDownloaderCancelledException(this);
/*     */       
/* 586 */       this.cancelled = true;
/*     */       
/* 588 */       informFailed((ResourceDownloaderException)this.result);
/*     */       
/* 590 */       this.done_sem.release();
/*     */       
/* 592 */       if (this.current_downloader != null)
/*     */       {
/* 594 */         this.current_downloader.cancel();
/*     */       }
/*     */     }
/*     */     finally {
/* 598 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */   {
/*     */     try
/*     */     {
/* 608 */       this.torrent_holder[0] = TOTorrentFactory.deserialiseFromBEncodedInputStream(data);
/*     */       
/* 610 */       if (this.torrent_holder[0].isSimpleTorrent())
/*     */       {
/* 612 */         downloadTorrent();
/*     */       }
/*     */       else
/*     */       {
/* 616 */         failed(this, new ResourceDownloaderException(this, "Only simple torrents supported"));
/*     */       }
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
/* 632 */       return true;
/*     */     }
/*     */     catch (TOTorrentException e)
/*     */     {
/* 621 */       failed(downloader, new ResourceDownloaderException(this, "Torrent deserialisation failed", e));
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 626 */         data.close();
/*     */       }
/*     */       catch (IOException e) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */   {
/* 640 */     this.result = e;
/*     */     
/* 642 */     this.done_sem.release();
/*     */     
/* 644 */     informFailed(e);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */   {
/* 652 */     if (downloader == this)
/*     */     {
/* 654 */       informPercentDone(percentage);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/resourcedownloader/ResourceDownloaderTorrentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */