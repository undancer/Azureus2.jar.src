/*     */ package org.gudy.azureus2.core3.disk.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManager;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerFactory;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileOwner;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory.LazyMovingImmediateAverageAdapter;
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory.LazyMovingImmediateAverageState;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*     */ import org.gudy.azureus2.core3.util.StringInterner;
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
/*     */   implements DiskManagerFileInfo, CacheFileOwner
/*     */ {
/*     */   private String root_dir;
/*     */   private final File relative_file;
/*     */   final int file_index;
/*     */   private CacheFile cache_file;
/*     */   private String extension;
/*     */   private long downloaded;
/*     */   final DiskManagerHelper diskManager;
/*     */   final TOTorrentFile torrent_file;
/*  67 */   private int priority = 0;
/*     */   
/*  69 */   protected boolean skipped_internal = false;
/*     */   
/*     */ 
/*     */   private volatile CopyOnWriteList<DiskManagerFileInfoListener> listeners;
/*     */   
/*     */   private volatile AverageFactory.LazyMovingImmediateAverageState read_average_state;
/*     */   
/*     */   private volatile AverageFactory.LazyMovingImmediateAverageState write_average_state;
/*     */   
/*     */   private volatile AverageFactory.LazyMovingImmediateAverageState eta_average_state;
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfoImpl(DiskManagerHelper _disk_manager, String _root_dir, File _relative_file, int _file_index, TOTorrentFile _torrent_file, int _storage_type)
/*     */     throws CacheFileManagerException
/*     */   {
/*  84 */     this.diskManager = _disk_manager;
/*  85 */     this.torrent_file = _torrent_file;
/*     */     
/*  87 */     this.root_dir = (_root_dir + File.separator);
/*  88 */     this.relative_file = _relative_file;
/*     */     
/*  90 */     this.file_index = _file_index;
/*     */     
/*  92 */     int cache_st = DiskManagerUtil.convertDMStorageTypeToCache(_storage_type);
/*     */     
/*  94 */     this.cache_file = CacheFileManagerFactory.getSingleton().createFile(this, new File(this.root_dir + this.relative_file.toString()), cache_st);
/*     */     
/*  96 */     if ((cache_st == 2) || (cache_st == 4))
/*     */     {
/*  98 */       this.skipped_internal = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getCacheFileOwnerName()
/*     */   {
/* 105 */     return this.diskManager.getInternalName();
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentFile getCacheFileTorrentFile()
/*     */   {
/* 111 */     return this.torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getCacheFileControlFileDir()
/*     */   {
/* 117 */     return this.diskManager.getDownloadState().getStateFile();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCacheMode()
/*     */   {
/* 123 */     return this.diskManager.getCacheMode();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void flushCache()
/*     */     throws Exception
/*     */   {
/* 131 */     this.cache_file.flushCache();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveFile(String new_root_dir, File new_absolute_file, boolean link_only)
/*     */     throws CacheFileManagerException
/*     */   {
/* 142 */     if (!link_only)
/*     */     {
/* 144 */       this.cache_file.moveFile(new_absolute_file);
/*     */     }
/*     */     
/* 147 */     this.root_dir = (new_root_dir + File.separator);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void renameFile(String new_name)
/*     */     throws CacheFileManagerException
/*     */   {
/* 156 */     this.cache_file.renameFile(new_name);
/*     */   }
/*     */   
/*     */ 
/*     */   public CacheFile getCacheFile()
/*     */   {
/* 162 */     return this.cache_file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAccessMode(int mode)
/*     */     throws CacheFileManagerException
/*     */   {
/* 171 */     int old_mode = this.cache_file.getAccessMode();
/*     */     
/* 173 */     this.cache_file.setAccessMode(mode == 1 ? 1 : 2);
/*     */     
/* 175 */     if (old_mode != mode)
/*     */     {
/* 177 */       this.diskManager.accessModeChanged(this, old_mode, mode);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getAccessMode()
/*     */   {
/* 184 */     int mode = this.cache_file.getAccessMode();
/*     */     
/* 186 */     return mode == 1 ? 1 : 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDownloaded()
/*     */   {
/* 193 */     return this.downloaded;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getExtension()
/*     */   {
/* 200 */     return this.extension;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile(boolean follow_link)
/*     */   {
/* 210 */     if (follow_link)
/*     */     {
/* 212 */       File res = getLink();
/*     */       
/* 214 */       if (res != null)
/*     */       {
/* 216 */         return res;
/*     */       }
/*     */     }
/*     */     
/* 220 */     return new File(this.root_dir + this.relative_file.toString());
/*     */   }
/*     */   
/*     */ 
/*     */   public TOTorrentFile getTorrentFile()
/*     */   {
/* 226 */     return this.torrent_file;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setLink(File link_destination)
/*     */   {
/* 233 */     Debug.out("setLink: download must be stopped");
/*     */     
/* 235 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean setLinkAtomic(File link_destination)
/*     */   {
/* 242 */     Debug.out("setLink: download must be stopped");
/*     */     
/* 244 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public File getLink()
/*     */   {
/* 250 */     return this.diskManager.getDownloadState().getFileLink(this.file_index, getFile(false));
/*     */   }
/*     */   
/*     */   public boolean setStorageType(int type) {
/* 254 */     DiskManagerFileInfoSet set = this.diskManager.getFileSet();
/* 255 */     boolean[] toSet = new boolean[set.nbFiles()];
/* 256 */     toSet[this.file_index] = true;
/* 257 */     return set.setStorageTypes(toSet, type)[this.file_index];
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStorageType()
/*     */   {
/* 263 */     return DiskManagerUtil.convertDMStorageTypeFromString(this.diskManager.getStorageType(this.file_index));
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isLinked()
/*     */   {
/* 269 */     return getLink() != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getFirstPieceNumber()
/*     */   {
/* 276 */     return this.torrent_file.getFirstPieceNumber();
/*     */   }
/*     */   
/*     */   public int getLastPieceNumber()
/*     */   {
/* 281 */     return this.torrent_file.getLastPieceNumber();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 288 */     return this.torrent_file.getLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 294 */     return this.file_index;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNbPieces()
/*     */   {
/* 300 */     return this.torrent_file.getNumberOfPieces();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDownloaded(long l)
/*     */   {
/* 308 */     this.downloaded = l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExtension(String string)
/*     */   {
/* 315 */     this.extension = StringInterner.intern(string);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 322 */     return this.priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPriority(int b)
/*     */   {
/* 329 */     this.priority = b;
/* 330 */     this.diskManager.priorityChanged(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSkipped()
/*     */   {
/* 337 */     return this.skipped_internal;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSkipped(boolean _skipped)
/*     */   {
/* 345 */     int existing_st = getStorageType();
/*     */     
/*     */ 
/*     */ 
/* 349 */     if ((!_skipped) && (existing_st == 2) && 
/* 350 */       (!setStorageType(1))) {
/* 351 */       return;
/*     */     }
/*     */     
/*     */ 
/* 355 */     if ((!_skipped) && (existing_st == 4) && 
/* 356 */       (!setStorageType(3))) {
/* 357 */       return;
/*     */     }
/*     */     
/*     */ 
/* 361 */     setSkippedInternal(_skipped);
/* 362 */     this.diskManager.skippedFileSetChanged(this);
/* 363 */     if (!_skipped)
/*     */     {
/* 365 */       boolean[] toCheck = new boolean[this.diskManager.getFileSet().nbFiles()];
/* 366 */       toCheck[this.file_index] = true;
/* 367 */       DiskManagerUtil.doFileExistenceChecks(this.diskManager.getFileSet(), toCheck, this.diskManager.getDownloadState().getDownloadManager(), true);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setSkippedInternal(boolean _skipped)
/*     */   {
/* 375 */     this.skipped_internal = _skipped;
/*     */     
/* 377 */     DownloadManager dm = getDownloadManager();
/*     */     
/* 379 */     if ((dm != null) && (!dm.isDestroyed()))
/*     */     {
/* 381 */       DownloadManagerState dm_state = this.diskManager.getDownloadState();
/*     */       
/* 383 */       String dnd_sf = dm_state.getAttribute("dnd_sf");
/*     */       
/* 385 */       if (dnd_sf != null)
/*     */       {
/* 387 */         File link = getLink();
/*     */         
/* 389 */         File file = getFile(false);
/*     */         
/* 391 */         if (_skipped)
/*     */         {
/* 393 */           if ((link == null) || (link.equals(file)))
/*     */           {
/* 395 */             File parent = file.getParentFile();
/*     */             
/* 397 */             if (parent != null)
/*     */             {
/* 399 */               File new_parent = new File(parent, dnd_sf);
/*     */               
/*     */ 
/*     */ 
/* 403 */               String prefix = dm_state.getAttribute("dnd_pfx");
/*     */               
/* 405 */               String file_name = file.getName();
/*     */               
/* 407 */               if ((prefix != null) && (!file_name.startsWith(prefix)))
/*     */               {
/* 409 */                 file_name = prefix + file_name;
/*     */               }
/*     */               
/* 412 */               File new_file = new File(new_parent, file_name);
/*     */               
/* 414 */               if (!new_file.exists())
/*     */               {
/* 416 */                 if (!new_parent.exists())
/*     */                 {
/* 418 */                   new_parent.mkdirs();
/*     */                 }
/*     */                 
/* 421 */                 if (new_parent.canWrite())
/*     */                 {
/*     */                   boolean ok;
/*     */                   try
/*     */                   {
/* 426 */                     dm_state.setFileLink(this.file_index, file, new_file);
/*     */                     
/* 428 */                     this.cache_file.moveFile(new_file);
/*     */                     
/* 430 */                     ok = true;
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 434 */                     ok = false;
/*     */                     
/* 436 */                     Debug.out(e);
/*     */                   }
/*     */                   
/* 439 */                   if (!ok)
/*     */                   {
/* 441 */                     dm_state.setFileLink(this.file_index, file, link);
/*     */                   }
/*     */                   
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 449 */         else if ((link != null) && (!file.exists()))
/*     */         {
/* 451 */           File parent = file.getParentFile();
/*     */           
/* 453 */           if ((parent != null) && (parent.canWrite()))
/*     */           {
/* 455 */             File new_parent = parent.getName().equals(dnd_sf) ? parent : new File(parent, dnd_sf);
/*     */             
/*     */ 
/*     */ 
/* 459 */             File new_file = new File(new_parent, link.getName());
/*     */             
/* 461 */             if (new_file.equals(link))
/*     */             {
/*     */               boolean ok;
/*     */               try
/*     */               {
/* 466 */                 String file_name = file.getName();
/*     */                 
/* 468 */                 String prefix = dm_state.getAttribute("dnd_pfx");
/*     */                 
/* 470 */                 boolean prefix_removed = false;
/*     */                 
/* 472 */                 if ((prefix != null) && (file_name.startsWith(prefix)))
/*     */                 {
/* 474 */                   file_name = file_name.substring(prefix.length());
/*     */                   
/* 476 */                   prefix_removed = true;
/*     */                 }
/*     */                 
/* 479 */                 String incomp_ext = dm_state.getAttribute("incompfilesuffix");
/*     */                 
/* 481 */                 if ((incomp_ext != null) && (incomp_ext.length() > 0) && (getDownloaded() != getLength()))
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 486 */                   if (prefix == null)
/*     */                   {
/* 488 */                     prefix = "";
/*     */                   }
/*     */                   
/* 491 */                   File new_link = new File(file.getParentFile(), prefix + file_name + incomp_ext);
/*     */                   
/* 493 */                   dm_state.setFileLink(this.file_index, file, new_link);
/*     */                   
/* 495 */                   this.cache_file.moveFile(new_link);
/*     */                 }
/* 497 */                 else if (prefix_removed)
/*     */                 {
/* 499 */                   File new_link = new File(file.getParentFile(), file_name);
/*     */                   
/* 501 */                   dm_state.setFileLink(this.file_index, file, new_link);
/*     */                   
/* 503 */                   this.cache_file.moveFile(new_link);
/*     */                 }
/*     */                 else
/*     */                 {
/* 507 */                   dm_state.setFileLink(this.file_index, file, null);
/*     */                   
/* 509 */                   this.cache_file.moveFile(file);
/*     */                 }
/*     */                 
/* 512 */                 File[] files = new_parent.listFiles();
/*     */                 
/* 514 */                 if ((files != null) && (files.length == 0))
/*     */                 {
/* 516 */                   new_parent.delete();
/*     */                 }
/*     */                 
/* 519 */                 ok = true;
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 523 */                 ok = false;
/*     */                 
/* 525 */                 Debug.out(e);
/*     */               }
/*     */               
/* 528 */               if (!ok)
/*     */               {
/* 530 */                 dm_state.setFileLink(this.file_index, file, link);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskManager getDiskManager()
/*     */   {
/* 543 */     return this.diskManager;
/*     */   }
/*     */   
/*     */   public DownloadManager getDownloadManager()
/*     */   {
/* 548 */     DownloadManagerState state = this.diskManager.getDownloadState();
/*     */     
/* 550 */     if (state == null) {
/* 551 */       return null;
/*     */     }
/*     */     
/* 554 */     return state.getDownloadManager();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dataWritten(long offset, long size)
/*     */   {
/* 562 */     CopyOnWriteList<DiskManagerFileInfoListener> l_ref = this.listeners;
/*     */     
/* 564 */     if (l_ref != null)
/*     */     {
/* 566 */       for (DiskManagerFileInfoListener listener : l_ref) {
/*     */         try
/*     */         {
/* 569 */           listener.dataWritten(offset, size);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 573 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dataChecked(long offset, long size)
/*     */   {
/* 584 */     CopyOnWriteList<DiskManagerFileInfoListener> l_ref = this.listeners;
/*     */     
/* 586 */     if (l_ref != null)
/*     */     {
/* 588 */       for (DiskManagerFileInfoListener listener : l_ref) {
/*     */         try
/*     */         {
/* 591 */           listener.dataChecked(offset, size);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 595 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DirectByteBuffer read(long offset, int length)
/*     */     throws IOException
/*     */   {
/* 608 */     DirectByteBuffer buffer = DirectByteBufferPool.getBuffer(, length);
/*     */     
/*     */     try
/*     */     {
/* 612 */       this.cache_file.read(buffer, offset, (short)1);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 616 */       buffer.returnToPool();
/*     */       
/* 618 */       Debug.printStackTrace(e);
/*     */       
/* 620 */       throw new IOException(e.getMessage());
/*     */     }
/*     */     
/* 623 */     return buffer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 630 */   private static final AverageFactory.LazyMovingImmediateAverageAdapter<DiskManagerFileInfoImpl> read_adapter = new AverageFactory.LazyMovingImmediateAverageAdapter()
/*     */   {
/*     */ 
/*     */ 
/*     */     public AverageFactory.LazyMovingImmediateAverageState getCurrent(DiskManagerFileInfoImpl instance)
/*     */     {
/*     */ 
/* 637 */       return instance.read_average_state;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setCurrent(DiskManagerFileInfoImpl instance, AverageFactory.LazyMovingImmediateAverageState average)
/*     */     {
/* 645 */       instance.read_average_state = average;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getValue(DiskManagerFileInfoImpl instance)
/*     */     {
/* 652 */       return instance.cache_file.getSessionBytesRead();
/*     */     }
/*     */   };
/*     */   
/* 656 */   private static final AverageFactory.LazyMovingImmediateAverageAdapter<DiskManagerFileInfoImpl> write_adapter = new AverageFactory.LazyMovingImmediateAverageAdapter()
/*     */   {
/*     */ 
/*     */ 
/*     */     public AverageFactory.LazyMovingImmediateAverageState getCurrent(DiskManagerFileInfoImpl instance)
/*     */     {
/*     */ 
/* 663 */       return instance.write_average_state;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setCurrent(DiskManagerFileInfoImpl instance, AverageFactory.LazyMovingImmediateAverageState average)
/*     */     {
/* 671 */       instance.write_average_state = average;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getValue(DiskManagerFileInfoImpl instance)
/*     */     {
/* 678 */       return instance.cache_file.getSessionBytesWritten();
/*     */     }
/*     */   };
/*     */   
/* 682 */   private static final AverageFactory.LazyMovingImmediateAverageAdapter<DiskManagerFileInfoImpl> eta_adapter = new AverageFactory.LazyMovingImmediateAverageAdapter()
/*     */   {
/*     */ 
/*     */ 
/*     */     public AverageFactory.LazyMovingImmediateAverageState getCurrent(DiskManagerFileInfoImpl instance)
/*     */     {
/*     */ 
/* 689 */       return instance.eta_average_state;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setCurrent(DiskManagerFileInfoImpl instance, AverageFactory.LazyMovingImmediateAverageState average)
/*     */     {
/* 697 */       instance.eta_average_state = average;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long getValue(DiskManagerFileInfoImpl instance)
/*     */     {
/* 704 */       return instance.cache_file.getSessionBytesWritten();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */   public int getReadBytesPerSecond()
/*     */   {
/* 711 */     return (int)AverageFactory.LazyMovingImmediateAverage(10, 1, read_adapter, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getWriteBytesPerSecond()
/*     */   {
/* 717 */     return (int)AverageFactory.LazyMovingImmediateAverage(10, 1, write_adapter, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getETA()
/*     */   {
/* 723 */     if (isSkipped())
/*     */     {
/* 725 */       return -1L;
/*     */     }
/*     */     
/* 728 */     long rem = getLength() - getDownloaded();
/*     */     
/* 730 */     if (rem == 0L)
/*     */     {
/* 732 */       return -1L;
/*     */     }
/*     */     
/* 735 */     long speed = AverageFactory.LazySmoothMovingImmediateAverage(eta_adapter, this);
/*     */     
/* 737 */     if (speed == 0L)
/*     */     {
/* 739 */       return 1827387392L;
/*     */     }
/*     */     
/*     */ 
/* 743 */     long eta = rem / speed;
/*     */     
/* 745 */     if (eta == 0L)
/*     */     {
/* 747 */       return 1L;
/*     */     }
/*     */     
/*     */ 
/* 751 */     return eta;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(final DiskManagerFileInfoListener listener)
/*     */   {
/* 766 */     synchronized (this)
/*     */     {
/* 768 */       if (this.listeners == null)
/*     */       {
/* 770 */         this.listeners = new CopyOnWriteList();
/*     */       }
/*     */     }
/*     */     
/* 774 */     if (!this.listeners.addIfNotPresent(listener))
/*     */     {
/* 776 */       return;
/*     */     }
/*     */     
/* 779 */     new Runnable()
/*     */     {
/*     */       private long file_start;
/*     */       
/*     */       private long file_end;
/* 784 */       private long current_write_start = -1L;
/* 785 */       private long current_write_end = -1L;
/* 786 */       private long current_check_start = -1L;
/* 787 */       private long current_check_end = -1L;
/*     */       
/*     */ 
/*     */       public void run()
/*     */       {
/* 792 */         TOTorrentFile[] tfs = DiskManagerFileInfoImpl.this.torrent_file.getTorrent().getFiles();
/*     */         
/* 794 */         long torrent_offset = 0L;
/*     */         
/* 796 */         for (int i = 0; i < DiskManagerFileInfoImpl.this.file_index; i++)
/*     */         {
/* 798 */           torrent_offset += tfs[i].getLength();
/*     */         }
/*     */         
/* 801 */         this.file_start = torrent_offset;
/* 802 */         this.file_end = (this.file_start + DiskManagerFileInfoImpl.this.torrent_file.getLength());
/*     */         
/* 804 */         DiskManagerPiece[] pieces = DiskManagerFileInfoImpl.this.diskManager.getPieces();
/*     */         
/* 806 */         int first_piece = DiskManagerFileInfoImpl.this.getFirstPieceNumber();
/* 807 */         int last_piece = DiskManagerFileInfoImpl.this.getLastPieceNumber();
/* 808 */         long piece_size = DiskManagerFileInfoImpl.this.torrent_file.getTorrent().getPieceLength();
/*     */         
/* 810 */         for (int i = first_piece; i <= last_piece; i++)
/*     */         {
/* 812 */           long piece_offset = piece_size * i;
/*     */           
/* 814 */           DiskManagerPiece piece = pieces[i];
/*     */           
/* 816 */           if (piece.isDone())
/*     */           {
/* 818 */             long bit_start = piece_offset;
/* 819 */             long bit_end = bit_start + piece.getLength();
/*     */             
/* 821 */             bitWritten(bit_start, bit_end, true);
/*     */           }
/*     */           else
/*     */           {
/* 825 */             int block_offset = 0;
/*     */             
/* 827 */             for (int j = 0; j < piece.getNbBlocks(); j++)
/*     */             {
/* 829 */               int block_size = piece.getBlockSize(j);
/*     */               
/* 831 */               if (piece.isWritten(j))
/*     */               {
/* 833 */                 long bit_start = piece_offset + block_offset;
/* 834 */                 long bit_end = bit_start + block_size;
/*     */                 
/* 836 */                 bitWritten(bit_start, bit_end, false);
/*     */               }
/*     */               
/* 839 */               block_offset += block_size;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 844 */         bitWritten(-1L, -1L, false);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       protected void bitWritten(long bit_start, long bit_end, boolean checked)
/*     */       {
/* 853 */         if (this.current_write_start == -1L)
/*     */         {
/* 855 */           this.current_write_start = bit_start;
/* 856 */           this.current_write_end = bit_end;
/*     */         }
/* 858 */         else if (this.current_write_end == bit_start)
/*     */         {
/* 860 */           this.current_write_end = bit_end;
/*     */         }
/*     */         else
/*     */         {
/* 864 */           if (this.current_write_start < this.file_start)
/*     */           {
/* 866 */             this.current_write_start = this.file_start;
/*     */           }
/*     */           
/* 869 */           if (this.current_write_end > this.file_end)
/*     */           {
/* 871 */             this.current_write_end = this.file_end;
/*     */           }
/*     */           
/* 874 */           if (this.current_write_start < this.current_write_end) {
/*     */             try
/*     */             {
/* 877 */               listener.dataWritten(this.current_write_start - this.file_start, this.current_write_end - this.current_write_start);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 881 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */           
/* 885 */           this.current_write_start = bit_start;
/* 886 */           this.current_write_end = bit_end;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 891 */         if ((checked) && (this.current_check_start == -1L))
/*     */         {
/* 893 */           this.current_check_start = bit_start;
/* 894 */           this.current_check_end = bit_end;
/*     */         }
/* 896 */         else if ((checked) && (this.current_check_end == bit_start))
/*     */         {
/* 898 */           this.current_check_end = bit_end;
/*     */         }
/*     */         else
/*     */         {
/* 902 */           if (this.current_check_start < this.file_start)
/*     */           {
/* 904 */             this.current_check_start = this.file_start;
/*     */           }
/*     */           
/* 907 */           if (this.current_check_end > this.file_end)
/*     */           {
/* 909 */             this.current_check_end = this.file_end;
/*     */           }
/*     */           
/* 912 */           if (this.current_check_start < this.current_check_end) {
/*     */             try
/*     */             {
/* 915 */               listener.dataChecked(this.current_check_start - this.file_start, this.current_check_end - this.current_check_start);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 919 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */           
/* 923 */           if (checked) {
/* 924 */             this.current_check_start = bit_start;
/* 925 */             this.current_check_end = bit_end;
/*     */           } else {
/* 927 */             this.current_check_start = -1L;
/* 928 */             this.current_check_end = -1L;
/*     */           }
/*     */         }
/*     */       }
/*     */     }.run();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(DiskManagerFileInfoListener listener)
/*     */   {
/* 940 */     synchronized (this)
/*     */     {
/* 942 */       if (this.listeners != null)
/*     */       {
/* 944 */         this.listeners.remove(listener);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */