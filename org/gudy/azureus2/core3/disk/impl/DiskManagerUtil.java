/*      */ package org.gudy.azureus2.core3.disk.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManager;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerFactory;
/*      */ import com.aelitis.azureus.core.diskmanager.cache.CacheFileOwner;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.impl.resume.RDResumeHandler;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerException;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerStatsImpl;
/*      */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.StringInterner;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DiskManagerUtil
/*      */ {
/*   58 */   private static final LogIDs LOGID = LogIDs.DISK;
/*      */   
/*      */   protected static int max_read_block_size;
/*      */   
/*      */   static
/*      */   {
/*   64 */     ParameterListener param_listener = new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String str)
/*      */       {
/*   69 */         DiskManagerUtil.max_read_block_size = COConfigurationManager.getIntParameter("BT Request Max Block Size");
/*      */       }
/*      */       
/*   72 */     };
/*   73 */     COConfigurationManager.addAndFireParameterListener("BT Request Max Block Size", param_listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean checkBlockConsistencyForHint(DiskManager dm, String originator, int pieceNumber, int offset, int length)
/*      */   {
/*   84 */     if (length <= 0) {
/*   85 */       if (Logger.isEnabled()) {
/*   86 */         Logger.log(new LogEvent(dm, LOGID, 3, "Hint invalid: " + originator + " length=" + length + " <= 0"));
/*      */       }
/*   88 */       return false;
/*      */     }
/*   90 */     if (pieceNumber < 0) {
/*   91 */       if (Logger.isEnabled()) {
/*   92 */         Logger.log(new LogEvent(dm, LOGID, 3, "Hint invalid: " + originator + " pieceNumber=" + pieceNumber + " < 0"));
/*      */       }
/*   94 */       return false;
/*      */     }
/*   96 */     if (pieceNumber >= dm.getNbPieces()) {
/*   97 */       if (Logger.isEnabled()) {
/*   98 */         Logger.log(new LogEvent(dm, LOGID, 3, "Hint invalid: " + originator + " pieceNumber=" + pieceNumber + " >= this.nbPieces=" + dm.getNbPieces()));
/*      */       }
/*      */       
/*  101 */       return false;
/*      */     }
/*  103 */     int pLength = dm.getPieceLength(pieceNumber);
/*      */     
/*  105 */     if (offset < 0) {
/*  106 */       if (Logger.isEnabled()) {
/*  107 */         Logger.log(new LogEvent(dm, LOGID, 3, "Hint invalid: " + originator + " offset=" + offset + " < 0"));
/*      */       }
/*  109 */       return false;
/*      */     }
/*  111 */     if (offset > pLength) {
/*  112 */       if (Logger.isEnabled()) {
/*  113 */         Logger.log(new LogEvent(dm, LOGID, 3, "Hint invalid: " + originator + " offset=" + offset + " > pLength=" + pLength));
/*      */       }
/*  115 */       return false;
/*      */     }
/*  117 */     if (offset + length > pLength) {
/*  118 */       if (Logger.isEnabled()) {
/*  119 */         Logger.log(new LogEvent(dm, LOGID, 3, "Hint invalid: " + originator + " offset=" + offset + " + length=" + length + " > pLength=" + pLength));
/*      */       }
/*      */       
/*  122 */       return false;
/*      */     }
/*      */     
/*  125 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean checkBlockConsistencyForRead(DiskManager dm, String originator, boolean peer_request, int pieceNumber, int offset, int length)
/*      */   {
/*  137 */     if (!checkBlockConsistencyForHint(dm, originator, pieceNumber, offset, length))
/*      */     {
/*  139 */       return false;
/*      */     }
/*      */     
/*  142 */     if ((length > max_read_block_size) && (peer_request)) {
/*  143 */       if (Logger.isEnabled()) {
/*  144 */         Logger.log(new LogEvent(dm, LOGID, 3, "Read invalid: " + originator + " length=" + length + " > " + max_read_block_size));
/*      */       }
/*  146 */       return false;
/*      */     }
/*      */     
/*  149 */     if (!dm.getPiece(pieceNumber).isDone()) {
/*  150 */       Logger.log(new LogEvent(dm, LOGID, 3, "Read invalid: " + originator + " piece #" + pieceNumber + " not done"));
/*      */       
/*  152 */       return false;
/*      */     }
/*      */     
/*  155 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void doFileExistenceChecks(DiskManagerFileInfoSet fileSet, boolean[] toCheck, DownloadManager dm, boolean allowAlloction)
/*      */   {
/*  165 */     DiskManagerFileInfo[] files = fileSet.getFiles();
/*      */     
/*  167 */     int lastPieceScanned = -1;
/*  168 */     int windowStart = -1;
/*  169 */     int windowEnd = -1;
/*      */     
/*  171 */     String[] types = DiskManagerImpl.getStorageTypes(dm);
/*      */     
/*      */ 
/*  174 */     for (int i = 0; i < files.length; i++)
/*      */     {
/*  176 */       int firstPiece = files[i].getFirstPieceNumber();
/*  177 */       int lastPiece = files[i].getLastPieceNumber();
/*      */       
/*  179 */       if (toCheck[i] != 0)
/*      */       {
/*  181 */         if (lastPieceScanned < firstPiece)
/*      */         {
/*  183 */           windowStart = firstPiece;
/*  184 */           while ((i > 0) && (files[(i - 1)].getLastPieceNumber() >= windowStart)) {
/*  185 */             i--;
/*      */           }
/*      */         }
/*  188 */         if (windowEnd < lastPiece) {
/*  189 */           windowEnd = lastPiece;
/*      */         }
/*      */       }
/*  192 */       if (((windowStart <= firstPiece) && (firstPiece <= windowEnd)) || ((windowStart <= lastPiece) && (lastPiece <= windowEnd)))
/*      */       {
/*  194 */         File currentFile = files[i].getFile(true);
/*  195 */         if (!RDResumeHandler.fileMustExist(dm, files[i]))
/*      */         {
/*  197 */           int st = convertDMStorageTypeFromString(types[i]);
/*  198 */           if ((st == 2) || (st == 4)) {
/*  199 */             currentFile.delete();
/*      */           }
/*  201 */         } else if ((allowAlloction) && (!currentFile.exists()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  206 */           dm.setDataAlreadyAllocated(false);
/*      */         }
/*  208 */         lastPieceScanned = lastPiece;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  215 */   static final AEMonitor cache_read_mon = new AEMonitor("DiskManager:cacheRead");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean setFileLink(DownloadManager download_manager, DiskManagerFileInfo[] info, DiskManagerFileInfo file_info, File from_file, File to_link)
/*      */   {
/*  225 */     if (to_link != null)
/*      */     {
/*  227 */       File existing_file = file_info.getFile(true);
/*      */       
/*  229 */       if (to_link.equals(existing_file))
/*      */       {
/*      */ 
/*      */ 
/*  233 */         return true;
/*      */       }
/*      */       
/*  236 */       for (int i = 0; i < info.length; i++)
/*      */       {
/*  238 */         if (to_link.equals(info[i].getFile(true)))
/*      */         {
/*  240 */           Logger.log(new LogAlert(download_manager, true, 3, "Attempt to link to existing file '" + info[i].getFile(true) + "'"));
/*      */           
/*      */ 
/*      */ 
/*  244 */           return false;
/*      */         }
/*      */       }
/*      */       
/*  248 */       if (to_link.exists())
/*      */       {
/*  250 */         if (!existing_file.exists())
/*      */         {
/*      */ 
/*      */ 
/*  254 */           download_manager.recheckFile(file_info);
/*      */         }
/*      */         else
/*      */         {
/*  258 */           Object skip_delete = download_manager.getUserData("set_link_dont_delete_existing");
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  263 */           if (((skip_delete instanceof Boolean)) && (((Boolean)skip_delete).booleanValue()))
/*      */           {
/*  265 */             download_manager.recheckFile(file_info);
/*      */ 
/*      */ 
/*      */           }
/*  269 */           else if (FileUtil.deleteWithRecycle(existing_file, download_manager.getDownloadState().getFlag(16L)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  275 */             download_manager.recheckFile(file_info);
/*      */           }
/*      */           else
/*      */           {
/*  279 */             Logger.log(new LogAlert(download_manager, true, 3, "Failed to delete '" + existing_file.toString() + "'"));
/*      */             
/*      */ 
/*  282 */             return false;
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*  288 */       else if (existing_file.exists())
/*      */       {
/*  290 */         if (!FileUtil.renameFile(existing_file, to_link))
/*      */         {
/*  292 */           Logger.log(new LogAlert(download_manager, true, 3, "Failed to rename '" + existing_file.toString() + "'"));
/*      */           
/*      */ 
/*  295 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  301 */     DownloadManagerState state = download_manager.getDownloadState();
/*      */     
/*  303 */     state.setFileLink(file_info.getIndex(), from_file, to_link);
/*      */     
/*  305 */     state.save();
/*      */     
/*  307 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DiskManagerFileInfoSet getFileInfoSkeleton(final DownloadManager download_manager, final DiskManagerListener listener)
/*      */   {
/*  325 */     TOTorrent torrent = download_manager.getTorrent();
/*      */     
/*  327 */     if (torrent == null)
/*      */     {
/*  329 */       return new DiskManagerFileInfoSetImpl(new DiskManagerFileInfoImpl[0], null);
/*      */     }
/*      */     
/*  332 */     String tempRootDir = download_manager.getAbsoluteSaveLocation().getParent();
/*      */     
/*  334 */     if (tempRootDir == null) {
/*  335 */       tempRootDir = download_manager.getAbsoluteSaveLocation().getPath();
/*      */     }
/*      */     
/*  338 */     if (!torrent.isSimpleTorrent()) {
/*  339 */       tempRootDir = tempRootDir + File.separator + download_manager.getAbsoluteSaveLocation().getName();
/*      */     }
/*      */     
/*  342 */     tempRootDir = tempRootDir + File.separator;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  347 */     final boolean[] loading = { true };
/*      */     try
/*      */     {
/*  350 */       final String root_dir = StringInterner.intern(tempRootDir);
/*      */       try
/*      */       {
/*  353 */         final LocaleUtilDecoder locale_decoder = LocaleTorrentUtil.getTorrentEncoding(torrent);
/*      */         
/*  355 */         TOTorrentFile[] torrent_files = torrent.getFiles();
/*      */         
/*  357 */         final FileSkeleton[] res = new FileSkeleton[torrent_files.length];
/*      */         
/*  359 */         final String incomplete_suffix = download_manager.getDownloadState().getAttribute("incompfilesuffix");
/*      */         
/*  361 */         final DiskManagerFileInfoSet fileSetSkeleton = new DiskManagerFileInfoSet()
/*      */         {
/*      */           public DiskManagerFileInfo[] getFiles() {
/*  364 */             return this.val$res;
/*      */           }
/*      */           
/*      */           public int nbFiles() {
/*  368 */             return this.val$res.length;
/*      */           }
/*      */           
/*      */           public void setPriority(int[] toChange) {
/*  372 */             if (toChange.length != this.val$res.length) {
/*  373 */               throw new IllegalArgumentException("array length mismatches the number of files");
/*      */             }
/*  375 */             for (int i = 0; i < this.val$res.length; i++) {
/*  376 */               this.val$res[i].priority = toChange[i];
/*      */             }
/*  378 */             if (loading[0] == 0)
/*      */             {
/*  380 */               DiskManagerImpl.storeFilePriorities(download_manager, this.val$res);
/*      */             }
/*      */             
/*  383 */             for (int i = 0; i < this.val$res.length; i++)
/*  384 */               if (toChange[i] != 0)
/*  385 */                 listener.filePriorityChanged(this.val$res[i]);
/*      */           }
/*      */           
/*      */           public void setSkipped(boolean[] toChange, boolean setSkipped) {
/*  389 */             if (toChange.length != this.val$res.length) {
/*  390 */               throw new IllegalArgumentException("array length mismatches the number of files");
/*      */             }
/*  392 */             if (!setSkipped) {
/*  393 */               String[] types = DiskManagerImpl.getStorageTypes(download_manager);
/*      */               
/*  395 */               boolean[] toLinear = new boolean[toChange.length];
/*  396 */               boolean[] toReorder = new boolean[toChange.length];
/*      */               
/*  398 */               int num_linear = 0;
/*  399 */               int num_reorder = 0;
/*      */               
/*  401 */               for (int i = 0; i < toChange.length; i++)
/*      */               {
/*  403 */                 if (toChange[i] != 0)
/*      */                 {
/*  405 */                   int old_type = DiskManagerUtil.convertDMStorageTypeFromString(types[i]);
/*      */                   
/*  407 */                   if (old_type == 2)
/*      */                   {
/*  409 */                     toLinear[i] = true;
/*      */                     
/*  411 */                     num_linear++;
/*      */                   }
/*  413 */                   else if (old_type == 4)
/*      */                   {
/*  415 */                     toReorder[i] = true;
/*      */                     
/*  417 */                     num_reorder++;
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*  422 */               if (num_linear > 0)
/*      */               {
/*  424 */                 if (!Arrays.equals(toLinear, setStorageTypes(toLinear, 1)))
/*      */                 {
/*  426 */                   return;
/*      */                 }
/*      */               }
/*      */               
/*  430 */               if (num_reorder > 0)
/*      */               {
/*  432 */                 if (!Arrays.equals(toReorder, setStorageTypes(toReorder, 3)))
/*      */                 {
/*  434 */                   return;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  439 */             File[] to_link = new File[this.val$res.length];
/*      */             
/*  441 */             for (int i = 0; i < this.val$res.length; i++) {
/*  442 */               if (toChange[i] != 0) {
/*  443 */                 to_link[i] = this.val$res[i].setSkippedInternal(setSkipped);
/*      */               }
/*      */             }
/*      */             
/*  447 */             if (loading[0] == 0)
/*      */             {
/*  449 */               DiskManagerImpl.storeFilePriorities(download_manager, this.val$res);
/*      */             }
/*      */             
/*  452 */             List<Integer> from_indexes = new ArrayList();
/*  453 */             List<File> from_links = new ArrayList();
/*  454 */             List<File> to_links = new ArrayList();
/*      */             
/*  456 */             for (int i = 0; i < this.val$res.length; i++) {
/*  457 */               if (to_link[i] != null) {
/*  458 */                 from_indexes.add(Integer.valueOf(i));
/*  459 */                 from_links.add(this.val$res[i].getFile(false));
/*  460 */                 to_links.add(to_link[i]);
/*      */               }
/*      */             }
/*      */             
/*  464 */             if (from_links.size() > 0) {
/*  465 */               download_manager.getDownloadState().setFileLinks(from_indexes, from_links, to_links);
/*      */             }
/*      */             
/*  468 */             if (!setSkipped) {
/*  469 */               DiskManagerUtil.doFileExistenceChecks(this, toChange, download_manager, true);
/*      */             }
/*      */             
/*  472 */             for (int i = 0; i < this.val$res.length; i++) {
/*  473 */               if (toChange[i] != 0) {
/*  474 */                 listener.filePriorityChanged(this.val$res[i]);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */           public boolean[] setStorageTypes(boolean[] toChange, int newStorageType) {
/*  480 */             if (toChange.length != this.val$res.length) {
/*  481 */               throw new IllegalArgumentException("array length mismatches the number of files");
/*      */             }
/*  483 */             String[] types = DiskManagerImpl.getStorageTypes(download_manager);
/*  484 */             boolean[] modified = new boolean[this.val$res.length];
/*  485 */             boolean[] toSkip = new boolean[this.val$res.length];
/*  486 */             int toSkipCount = 0;
/*  487 */             DownloadManagerState dmState = download_manager.getDownloadState();
/*      */             try
/*      */             {
/*  490 */               dmState.suppressStateSave(true);
/*      */               
/*  492 */               for (int i = 0; i < this.val$res.length; i++)
/*      */               {
/*  494 */                 if (toChange[i] != 0)
/*      */                 {
/*      */ 
/*      */ 
/*  498 */                   final int idx = i;
/*      */                   
/*  500 */                   int old_type = DiskManagerUtil.convertDMStorageTypeFromString(types[i]);
/*      */                   
/*      */ 
/*      */ 
/*  504 */                   if (newStorageType == old_type)
/*      */                   {
/*  506 */                     modified[i] = true;
/*      */                   }
/*      */                   else
/*      */                   {
/*      */                     try {
/*  511 */                       File target_file = this.val$res[i].getFile(true);
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  519 */                       if (target_file.exists())
/*      */                       {
/*  521 */                         CacheFile cache_file = CacheFileManagerFactory.getSingleton().createFile(new CacheFileOwner()
/*      */                         {
/*      */ 
/*      */ 
/*      */                           public String getCacheFileOwnerName()
/*      */                           {
/*      */ 
/*  528 */                             return DiskManagerUtil.2.this.val$download_manager.getInternalName();
/*      */                           }
/*      */                           
/*      */ 
/*      */                           public TOTorrentFile getCacheFileTorrentFile()
/*      */                           {
/*  534 */                             return DiskManagerUtil.2.this.val$res[idx].getTorrentFile();
/*      */                           }
/*      */                           
/*      */ 
/*      */                           public File getCacheFileControlFileDir()
/*      */                           {
/*  540 */                             return DiskManagerUtil.2.this.val$download_manager.getDownloadState().getStateFile();
/*      */                           }
/*      */                           
/*      */ 
/*      */ 
/*  545 */                           public int getCacheMode() { return 1; } }, target_file, DiskManagerUtil.convertDMStorageTypeToCache(newStorageType));
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  551 */                         cache_file.getLength();
/*      */                         
/*  553 */                         cache_file.close();
/*      */                       }
/*      */                       
/*  556 */                       toSkip[i] = (((newStorageType == 2) || (newStorageType == 4)) && (!this.val$res[i].isSkipped()) ? 1 : false);
/*      */                       
/*  558 */                       if (toSkip[i] != 0)
/*      */                       {
/*  560 */                         toSkipCount++;
/*      */                       }
/*      */                       
/*  563 */                       modified[i] = true;
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/*  567 */                       Debug.printStackTrace(e);
/*      */                       
/*  569 */                       Logger.log(new LogAlert(download_manager, true, 3, "Failed to change storage type for '" + this.val$res[i].getFile(true) + "': " + Debug.getNestedExceptionMessage(e)));
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  577 */                       RDResumeHandler.recheckFile(download_manager, this.val$res[i]);
/*      */                     }
/*      */                     
/*      */ 
/*  581 */                     types[i] = DiskManagerUtil.convertDMStorageTypeToString(newStorageType);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  589 */               dmState.setListAttribute("storetypes", types);
/*      */               
/*  591 */               if (toSkipCount > 0)
/*      */               {
/*  593 */                 setSkipped(toSkip, true);
/*      */               }
/*      */               
/*  596 */               for (int i = 0; i < this.val$res.length; i++)
/*      */               {
/*  598 */                 if (toChange[i] != 0)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  603 */                   int cleared = RDResumeHandler.storageTypeChanged(download_manager, this.val$res[i]);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  610 */                   if (cleared > 0)
/*      */                   {
/*  612 */                     this.val$res[i].downloaded -= cleared * this.val$res[i].getTorrentFile().getTorrent().getPieceLength();
/*  613 */                     if (this.val$res[i].downloaded < 0L) this.val$res[i].downloaded = 0L;
/*      */                   }
/*      */                 }
/*      */               }
/*  617 */               DiskManagerImpl.storeFileDownloaded(download_manager, this.val$res, true);
/*      */               
/*  619 */               DiskManagerUtil.doFileExistenceChecks(this, toChange, download_manager, (newStorageType == 1) || (newStorageType == 3));
/*      */             }
/*      */             finally {
/*  622 */               dmState.suppressStateSave(false);
/*  623 */               dmState.save();
/*      */             }
/*      */             
/*  626 */             return modified;
/*      */           }
/*      */         };
/*      */         
/*  630 */         for (int i = 0; i < res.length; i++)
/*      */         {
/*  632 */           final TOTorrentFile torrent_file = torrent_files[i];
/*      */           
/*  634 */           final int file_index = i;
/*      */           
/*  636 */           FileSkeleton info = new FileSkeleton()
/*      */           {
/*      */             private volatile CacheFile read_cache_file;
/*      */             
/*  640 */             private WeakReference dataFile = new WeakReference(null);
/*      */             
/*      */ 
/*      */             public void setPriority(int b)
/*      */             {
/*  645 */               this.priority = b;
/*      */               
/*  647 */               DiskManagerImpl.storeFilePriorities(this.val$download_manager, res);
/*      */               
/*  649 */               listener.filePriorityChanged(this);
/*      */             }
/*      */             
/*      */ 
/*      */             public void setSkipped(boolean _skipped)
/*      */             {
/*  655 */               if ((!_skipped) && (getStorageType() == 2) && 
/*  656 */                 (!setStorageType(1))) {
/*  657 */                 return;
/*      */               }
/*      */               
/*      */ 
/*  661 */               if ((!_skipped) && (getStorageType() == 4) && 
/*  662 */                 (!setStorageType(3))) {
/*  663 */                 return;
/*      */               }
/*      */               
/*      */ 
/*  667 */               File to_link = setSkippedInternal(_skipped);
/*      */               
/*  669 */               DiskManagerImpl.storeFilePriorities(this.val$download_manager, res);
/*      */               
/*  671 */               if (to_link != null)
/*      */               {
/*  673 */                 this.val$download_manager.getDownloadState().setFileLink(file_index, getFile(false), to_link);
/*      */               }
/*      */               
/*  676 */               if (!_skipped) {
/*  677 */                 boolean[] toCheck = new boolean[fileSetSkeleton.nbFiles()];
/*  678 */                 toCheck[file_index] = true;
/*  679 */                 DiskManagerUtil.doFileExistenceChecks(fileSetSkeleton, toCheck, this.val$download_manager, true);
/*      */               }
/*      */               
/*  682 */               listener.filePriorityChanged(this);
/*      */             }
/*      */             
/*      */ 
/*      */             public int getAccessMode()
/*      */             {
/*  688 */               return 1;
/*      */             }
/*      */             
/*      */ 
/*      */             public long getDownloaded()
/*      */             {
/*  694 */               return this.downloaded;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void setDownloaded(long l)
/*      */             {
/*  701 */               this.downloaded = l;
/*      */             }
/*      */             
/*      */ 
/*      */             public String getExtension()
/*      */             {
/*  707 */               String ext = lazyGetFile().getName();
/*      */               
/*  709 */               if ((incomplete_suffix != null) && (ext.endsWith(incomplete_suffix)))
/*      */               {
/*  711 */                 ext = ext.substring(0, ext.length() - incomplete_suffix.length());
/*      */               }
/*      */               
/*  714 */               int separator = ext.lastIndexOf(".");
/*  715 */               if (separator == -1)
/*  716 */                 separator = 0;
/*  717 */               return ext.substring(separator);
/*      */             }
/*      */             
/*      */ 
/*      */             public int getFirstPieceNumber()
/*      */             {
/*  723 */               return torrent_file.getFirstPieceNumber();
/*      */             }
/*      */             
/*      */ 
/*      */             public int getLastPieceNumber()
/*      */             {
/*  729 */               return torrent_file.getLastPieceNumber();
/*      */             }
/*      */             
/*      */ 
/*      */             public long getLength()
/*      */             {
/*  735 */               return torrent_file.getLength();
/*      */             }
/*      */             
/*      */ 
/*      */             public int getIndex()
/*      */             {
/*  741 */               return file_index;
/*      */             }
/*      */             
/*      */ 
/*      */             public int getNbPieces()
/*      */             {
/*  747 */               return torrent_file.getNumberOfPieces();
/*      */             }
/*      */             
/*      */ 
/*      */             public int getPriority()
/*      */             {
/*  753 */               return this.priority;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             protected File setSkippedInternal(boolean _skipped)
/*      */             {
/*  762 */               this.skipped_internal = _skipped;
/*      */               
/*  764 */               if (!this.val$download_manager.isDestroyed())
/*      */               {
/*  766 */                 DownloadManagerState dm_state = this.val$download_manager.getDownloadState();
/*      */                 
/*  768 */                 String dnd_sf = dm_state.getAttribute("dnd_sf");
/*      */                 
/*  770 */                 if (dnd_sf != null)
/*      */                 {
/*  772 */                   File link = getLink();
/*      */                   
/*  774 */                   File file = getFile(false);
/*      */                   
/*  776 */                   if (_skipped)
/*      */                   {
/*  778 */                     if ((link == null) || (link.equals(file)))
/*      */                     {
/*  780 */                       File parent = file.getParentFile();
/*      */                       
/*  782 */                       if (parent != null)
/*      */                       {
/*  784 */                         String prefix = dm_state.getAttribute("dnd_pfx");
/*      */                         
/*  786 */                         String file_name = file.getName();
/*      */                         
/*  788 */                         if ((prefix != null) && (!file_name.startsWith(prefix)))
/*      */                         {
/*  790 */                           file_name = prefix + file_name;
/*      */                         }
/*      */                         
/*  793 */                         File new_parent = new File(parent, dnd_sf);
/*      */                         
/*  795 */                         File new_file = new File(new_parent, file_name);
/*      */                         
/*  797 */                         if (!new_file.exists())
/*      */                         {
/*  799 */                           if (!new_parent.exists())
/*      */                           {
/*  801 */                             new_parent.mkdirs();
/*      */                           }
/*      */                           
/*  804 */                           if (new_parent.canWrite())
/*      */                           {
/*      */                             boolean ok;
/*      */                             boolean ok;
/*  808 */                             if (file.exists())
/*      */                             {
/*  810 */                               ok = FileUtil.renameFile(file, new_file);
/*      */                             }
/*      */                             else
/*      */                             {
/*  814 */                               ok = true;
/*      */                             }
/*      */                             
/*  817 */                             if (ok)
/*      */                             {
/*  819 */                               return new_file;
/*      */                             }
/*      */                             
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*  827 */                   else if ((link != null) && (!file.exists()))
/*      */                   {
/*  829 */                     File parent = file.getParentFile();
/*      */                     
/*  831 */                     if ((parent != null) && (parent.canWrite()))
/*      */                     {
/*  833 */                       File new_parent = parent.getName().equals(dnd_sf) ? parent : new File(parent, dnd_sf);
/*      */                       
/*      */ 
/*      */ 
/*  837 */                       File new_file = new File(new_parent, link.getName());
/*      */                       
/*  839 */                       if (new_file.equals(link))
/*      */                       {
/*      */ 
/*      */ 
/*  843 */                         String incomp_ext = dm_state.getAttribute("incompfilesuffix");
/*      */                         
/*  845 */                         String file_name = file.getName();
/*      */                         
/*  847 */                         String prefix = dm_state.getAttribute("dnd_pfx");
/*      */                         
/*  849 */                         boolean prefix_removed = false;
/*      */                         
/*  851 */                         if ((prefix != null) && (file_name.startsWith(prefix)))
/*      */                         {
/*  853 */                           file_name = file_name.substring(prefix.length());
/*      */                           
/*  855 */                           prefix_removed = true;
/*      */                         }
/*      */                         
/*  858 */                         if ((incomp_ext != null) && (incomp_ext.length() > 0) && (getDownloaded() != getLength()))
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*  863 */                           if (prefix == null)
/*      */                           {
/*  865 */                             prefix = "";
/*      */                           }
/*      */                           
/*  868 */                           file = new File(file.getParentFile(), prefix + file_name + incomp_ext);
/*      */                         }
/*  870 */                         else if (prefix_removed)
/*      */                         {
/*  872 */                           file = new File(file.getParentFile(), file_name); }
/*      */                         boolean ok;
/*      */                         boolean ok;
/*  875 */                         if (new_file.exists())
/*      */                         {
/*  877 */                           ok = FileUtil.renameFile(new_file, file);
/*      */                         }
/*      */                         else
/*      */                         {
/*  881 */                           ok = true;
/*      */                         }
/*      */                         
/*  884 */                         if (ok)
/*      */                         {
/*  886 */                           File[] files = new_parent.listFiles();
/*      */                           
/*  888 */                           if ((files != null) && (files.length == 0))
/*      */                           {
/*  890 */                             new_parent.delete();
/*      */                           }
/*      */                           
/*  893 */                           return file;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  902 */               return null;
/*      */             }
/*      */             
/*      */ 
/*      */             public boolean isSkipped()
/*      */             {
/*  908 */               return this.skipped_internal;
/*      */             }
/*      */             
/*      */ 
/*      */             public DiskManager getDiskManager()
/*      */             {
/*  914 */               return null;
/*      */             }
/*      */             
/*      */ 
/*      */             public DownloadManager getDownloadManager()
/*      */             {
/*  920 */               return this.val$download_manager;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public File getFile(boolean follow_link)
/*      */             {
/*  927 */               if (follow_link)
/*      */               {
/*  929 */                 File link = getLink();
/*      */                 
/*  931 */                 if (link != null)
/*      */                 {
/*  933 */                   return link;
/*      */                 }
/*      */               }
/*  936 */               return lazyGetFile();
/*      */             }
/*      */             
/*      */             private File lazyGetFile()
/*      */             {
/*  941 */               File toReturn = (File)this.dataFile.get();
/*  942 */               if (toReturn != null) {
/*  943 */                 return toReturn;
/*      */               }
/*  945 */               TOTorrent tor = this.val$download_manager.getTorrent();
/*      */               
/*  947 */               String path_str = root_dir;
/*  948 */               File simpleFile = null;
/*      */               
/*      */ 
/*      */ 
/*  952 */               if ((tor == null) || (tor.isSimpleTorrent()))
/*      */               {
/*      */ 
/*      */ 
/*  956 */                 simpleFile = this.val$download_manager.getAbsoluteSaveLocation();
/*      */               }
/*      */               else {
/*  959 */                 byte[][] path_comps = torrent_file.getPathComponents();
/*      */                 
/*  961 */                 for (int j = 0; j < path_comps.length; j++)
/*      */                 {
/*      */ 
/*      */                   try
/*      */                   {
/*  966 */                     comp = locale_decoder.decodeString(path_comps[j]);
/*      */                   }
/*      */                   catch (UnsupportedEncodingException e) {
/*  969 */                     Debug.printStackTrace(e);
/*  970 */                     comp = "undecodableFileName" + file_index;
/*      */                   }
/*      */                   
/*  973 */                   String comp = FileUtil.convertOSSpecificChars(comp, j != path_comps.length - 1);
/*      */                   
/*  975 */                   path_str = path_str + (j == 0 ? "" : File.separator) + comp;
/*      */                 }
/*      */               }
/*      */               
/*  979 */               this.dataFile = new WeakReference(toReturn = simpleFile != null ? simpleFile : new File(path_str));
/*      */               
/*      */ 
/*  982 */               return toReturn;
/*      */             }
/*      */             
/*      */ 
/*      */             public TOTorrentFile getTorrentFile()
/*      */             {
/*  988 */               return torrent_file;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public boolean setLink(File link_destination)
/*      */             {
/*  999 */               if (this.val$download_manager.getTorrent().isSimpleTorrent()) {
/*      */                 try {
/* 1001 */                   this.val$download_manager.moveDataFiles(link_destination.getParentFile(), link_destination.getName());
/* 1002 */                   return true;
/*      */                 }
/*      */                 catch (DownloadManagerException e)
/*      */                 {
/* 1006 */                   return false;
/*      */                 }
/*      */               }
/* 1009 */               return setLinkAtomic(link_destination);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public boolean setLinkAtomic(File link_destination)
/*      */             {
/* 1016 */               return DiskManagerUtil.setFileLink(this.val$download_manager, res, this, lazyGetFile(), link_destination);
/*      */             }
/*      */             
/*      */ 
/*      */             public File getLink()
/*      */             {
/* 1022 */               return this.val$download_manager.getDownloadState().getFileLink(file_index, lazyGetFile());
/*      */             }
/*      */             
/*      */             public boolean setStorageType(int type) {
/* 1026 */               boolean[] change = new boolean[res.length];
/* 1027 */               change[file_index] = true;
/* 1028 */               return fileSetSkeleton.setStorageTypes(change, type)[file_index];
/*      */             }
/*      */             
/*      */ 
/*      */             public int getStorageType()
/*      */             {
/* 1034 */               return DiskManagerUtil.convertDMStorageTypeFromString(DiskManagerImpl.getStorageType(this.val$download_manager, file_index));
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             public void flushCache() {}
/*      */             
/*      */ 
/*      */ 
/*      */             public DirectByteBuffer read(long offset, int length)
/*      */               throws IOException
/*      */             {
/*      */               CacheFile temp;
/*      */               
/*      */ 
/*      */               try
/*      */               {
/* 1052 */                 DiskManagerUtil.cache_read_mon.enter();
/*      */                 
/* 1054 */                 if (this.read_cache_file == null) {
/*      */                   try
/*      */                   {
/* 1057 */                     int type = DiskManagerUtil.convertDMStorageTypeFromString(DiskManagerImpl.getStorageType(this.val$download_manager, file_index));
/*      */                     
/* 1059 */                     this.read_cache_file = CacheFileManagerFactory.getSingleton().createFile(new CacheFileOwner()
/*      */                     {
/*      */ 
/*      */ 
/*      */                       public String getCacheFileOwnerName()
/*      */                       {
/*      */ 
/* 1066 */                         return DiskManagerUtil.3.this.val$download_manager.getInternalName();
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public TOTorrentFile getCacheFileTorrentFile()
/*      */                       {
/* 1072 */                         return DiskManagerUtil.3.this.val$torrent_file;
/*      */                       }
/*      */                       
/*      */ 
/*      */                       public File getCacheFileControlFileDir()
/*      */                       {
/* 1078 */                         return DiskManagerUtil.3.this.val$download_manager.getDownloadState().getStateFile();
/*      */                       }
/*      */                       
/*      */ 
/*      */ 
/* 1083 */                       public int getCacheMode() { return 1; } }, getFile(true), DiskManagerUtil.convertDMStorageTypeToCache(type));
/*      */ 
/*      */ 
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*      */ 
/*      */ 
/* 1091 */                     Debug.printStackTrace(e);
/*      */                     
/* 1093 */                     throw new IOException(e.getMessage());
/*      */                   }
/*      */                 }
/*      */                 
/* 1097 */                 temp = this.read_cache_file;
/*      */               }
/*      */               finally
/*      */               {
/* 1101 */                 DiskManagerUtil.cache_read_mon.exit();
/*      */               }
/*      */               
/* 1104 */               DirectByteBuffer buffer = DirectByteBufferPool.getBuffer((byte)6, length);
/*      */               
/*      */               try
/*      */               {
/* 1108 */                 temp.read(buffer, offset, (short)1);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1112 */                 buffer.returnToPool();
/*      */                 
/* 1114 */                 Debug.printStackTrace(e);
/*      */                 
/* 1116 */                 throw new IOException(e.getMessage());
/*      */               }
/*      */               
/* 1119 */               return buffer;
/*      */             }
/*      */             
/*      */ 
/*      */             public int getReadBytesPerSecond()
/*      */             {
/* 1125 */               CacheFile temp = this.read_cache_file;
/*      */               
/* 1127 */               if (temp == null)
/*      */               {
/* 1129 */                 return 0;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 1134 */               return 0;
/*      */             }
/*      */             
/*      */ 
/*      */             public int getWriteBytesPerSecond()
/*      */             {
/* 1140 */               return 0;
/*      */             }
/*      */             
/*      */ 
/*      */             public long getETA()
/*      */             {
/* 1146 */               return -1L;
/*      */             }
/*      */             
/*      */ 
/*      */             public void close()
/*      */             {
/*      */               CacheFile temp;
/*      */               try
/*      */               {
/* 1155 */                 DiskManagerUtil.cache_read_mon.enter();
/*      */                 
/* 1157 */                 temp = this.read_cache_file;
/*      */                 
/* 1159 */                 this.read_cache_file = null;
/*      */               }
/*      */               finally
/*      */               {
/* 1163 */                 DiskManagerUtil.cache_read_mon.exit();
/*      */               }
/*      */               
/* 1166 */               if (temp != null) {
/*      */                 try
/*      */                 {
/* 1169 */                   temp.close();
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1173 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void addListener(DiskManagerFileInfoListener listener)
/*      */             {
/* 1182 */               if (getDownloaded() == getLength()) {
/*      */                 try
/*      */                 {
/* 1185 */                   listener.dataWritten(0L, getLength());
/*      */                   
/* 1187 */                   listener.dataChecked(0L, getLength());
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1191 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void removeListener(DiskManagerFileInfoListener listener) {}
/* 1202 */           };
/* 1203 */           res[i] = info;
/*      */         }
/*      */         
/* 1206 */         loadFilePriorities(download_manager, fileSetSkeleton);
/*      */         
/* 1208 */         loadFileDownloaded(download_manager, res);
/*      */         
/* 1210 */         return fileSetSkeleton;
/*      */       }
/*      */       finally
/*      */       {
/* 1214 */         loading[0] = false;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1221 */       return new DiskManagerFileInfoSetImpl(new DiskManagerFileInfoImpl[0], null);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1219 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int convertDMStorageTypeFromString(String str)
/*      */   {
/* 1230 */     char c = str.charAt(0);
/*      */     
/* 1232 */     switch (c) {
/*      */     case 'L': 
/* 1234 */       return 1;
/*      */     
/*      */     case 'C': 
/* 1237 */       return 2;
/*      */     
/*      */     case 'R': 
/* 1240 */       return 3;
/*      */     
/*      */     case 'X': 
/* 1243 */       return 4;
/*      */     }
/*      */     
/*      */     
/* 1247 */     Debug.out("eh?");
/*      */     
/* 1249 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String convertDMStorageTypeToString(int dm_type)
/*      */   {
/* 1256 */     switch (dm_type) {
/*      */     case 1: 
/* 1258 */       return "L";
/*      */     
/*      */     case 2: 
/* 1261 */       return "C";
/*      */     
/*      */     case 3: 
/* 1264 */       return "R";
/*      */     
/*      */     case 4: 
/* 1267 */       return "X";
/*      */     }
/*      */     
/*      */     
/* 1271 */     Debug.out("eh?");
/*      */     
/* 1273 */     return "?";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static String convertCacheStorageTypeToString(int cache_type)
/*      */   {
/* 1280 */     switch (cache_type) {
/*      */     case 1: 
/* 1282 */       return "L";
/*      */     
/*      */     case 2: 
/* 1285 */       return "C";
/*      */     
/*      */     case 3: 
/* 1288 */       return "R";
/*      */     
/*      */     case 4: 
/* 1291 */       return "X";
/*      */     }
/*      */     
/*      */     
/* 1295 */     Debug.out("eh?");
/*      */     
/* 1297 */     return "?";
/*      */   }
/*      */   
/*      */ 
/*      */   public static int convertDMStorageTypeToCache(int dm_type)
/*      */   {
/* 1303 */     switch (dm_type) {
/*      */     case 1: 
/* 1305 */       return 1;
/*      */     
/*      */     case 2: 
/* 1308 */       return 2;
/*      */     
/*      */     case 3: 
/* 1311 */       return 3;
/*      */     
/*      */     case 4: 
/* 1314 */       return 4;
/*      */     }
/*      */     
/*      */     
/* 1318 */     Debug.out("eh?");
/*      */     
/* 1320 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void storeFilePriorities(DownloadManager download_manager, DiskManagerFileInfo[] files)
/*      */   {
/* 1328 */     if (files == null) { return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1335 */     List file_priorities = new ArrayList(files.length);
/* 1336 */     for (int i = 0; i < files.length; i++) {
/* 1337 */       DiskManagerFileInfo file = files[i];
/* 1338 */       if (file == null) return;
/* 1339 */       boolean skipped = file.isSkipped();
/* 1340 */       int priority = file.getPriority();
/*      */       int value;
/* 1342 */       int value; if (skipped) {
/* 1343 */         value = 0; } else { int value;
/* 1344 */         if (priority > 0) {
/* 1345 */           value = priority;
/*      */         } else {
/* 1347 */           value = priority - 1;
/* 1348 */           if (value > 0)
/* 1349 */             value = Integer.MIN_VALUE;
/*      */         }
/*      */       }
/* 1352 */       file_priorities.add(i, Long.valueOf(value));
/*      */     }
/*      */     
/* 1355 */     download_manager.setData("file_priorities", file_priorities);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1360 */     if ((files.length > 0) && (!(files[0] instanceof DiskManagerFileInfoImpl)))
/*      */     {
/* 1362 */       long skipped_file_set_size = 0L;
/* 1363 */       long skipped_but_downloaded = 0L;
/*      */       
/* 1365 */       for (int i = 0; i < files.length; i++)
/*      */       {
/* 1367 */         DiskManagerFileInfo file = files[i];
/*      */         
/* 1369 */         if (file.isSkipped())
/*      */         {
/* 1371 */           skipped_file_set_size += file.getLength();
/* 1372 */           skipped_but_downloaded += file.getDownloaded();
/*      */         }
/*      */       }
/*      */       
/* 1376 */       DownloadManagerStats stats = download_manager.getStats();
/* 1377 */       if ((stats instanceof DownloadManagerStatsImpl)) {
/* 1378 */         ((DownloadManagerStatsImpl)stats).setSkippedFileStats(skipped_file_set_size, skipped_but_downloaded);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   static void loadFilePriorities(DownloadManager download_manager, DiskManagerFileInfoSet fileSet)
/*      */   {
/*      */     try
/*      */     {
/* 1390 */       DiskManagerFileInfo[] files = fileSet.getFiles();
/*      */       
/* 1392 */       if (files == null) return;
/* 1393 */       List file_priorities = (List)download_manager.getData("file_priorities");
/* 1394 */       if (file_priorities == null) { return;
/*      */       }
/* 1396 */       boolean[] toSkip = new boolean[files.length];
/* 1397 */       int[] prio = new int[files.length];
/*      */       
/* 1399 */       for (int i = 0; i < files.length; i++) {
/* 1400 */         DiskManagerFileInfo file = files[i];
/* 1401 */         if (file == null) return;
/*      */         try {
/* 1403 */           int priority = ((Long)file_priorities.get(i)).intValue();
/* 1404 */           if (priority == 0) {
/* 1405 */             toSkip[i] = true;
/*      */           } else {
/* 1407 */             if (priority < 0) {
/* 1408 */               priority++;
/*      */             }
/* 1410 */             prio[i] = priority;
/*      */           }
/*      */         } catch (Throwable t2) {
/* 1413 */           Debug.printStackTrace(t2);
/*      */         }
/*      */       }
/*      */       
/* 1417 */       fileSet.setPriority(prio);
/* 1418 */       fileSet.setSkipped(toSkip, true);
/*      */     }
/*      */     catch (Throwable t) {
/* 1421 */       Debug.printStackTrace(t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static void loadFileDownloaded(DownloadManager download_manager, DiskManagerFileInfoHelper[] files)
/*      */   {
/* 1429 */     DownloadManagerState state = download_manager.getDownloadState();
/*      */     
/* 1431 */     Map details = state.getMapAttribute("filedownloaded");
/*      */     
/* 1433 */     if (details == null)
/*      */     {
/* 1435 */       return;
/*      */     }
/*      */     
/* 1438 */     List downloaded = (List)details.get("downloaded");
/*      */     
/* 1440 */     if (downloaded == null)
/*      */     {
/* 1442 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 1446 */       for (int i = 0; i < files.length; i++)
/*      */       {
/* 1448 */         files[i].setDownloaded(((Long)downloaded.get(i)).longValue());
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1452 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract class FileSkeleton
/*      */     implements DiskManagerFileInfoHelper
/*      */   {
/*      */     protected int priority;
/*      */     protected boolean skipped_internal;
/*      */     protected long downloaded;
/*      */     
/*      */     protected abstract File setSkippedInternal(boolean paramBoolean);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */