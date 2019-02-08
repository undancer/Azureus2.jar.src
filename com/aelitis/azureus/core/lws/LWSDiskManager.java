/*     */ package com.aelitis.azureus.core.lws;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessController;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManager.OperationStatus;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerHelper;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerPieceImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerRecheckScheduler;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerUtil;
/*     */ import org.gudy.azureus2.core3.disk.impl.access.DMAccessFactory;
/*     */ import org.gudy.azureus2.core3.disk.impl.access.DMChecker;
/*     */ import org.gudy.azureus2.core3.disk.impl.access.DMReader;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapper;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapperFactory;
/*     */ import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapperFile;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public class LWSDiskManager
/*     */   implements DiskManagerHelper
/*     */ {
/*  69 */   private static final sePiece piece = new sePiece();
/*     */   
/*     */   private final LightWeightSeed lws;
/*     */   
/*     */   private final DiskAccessController disk_access_controller;
/*     */   
/*     */   private final File save_file;
/*     */   private DMReader reader;
/*     */   private DMChecker checker_use_accessor;
/*     */   private DMPieceMapper piece_mapper;
/*     */   private DMPieceMap piece_map_use_accessor;
/*     */   private final sePiece[] pieces;
/*     */   private DiskManagerFileInfoImpl[] files;
/*     */   private String internal_name;
/*     */   private final DownloadManagerState download_state;
/*     */   private boolean started;
/*  85 */   private int state = 1;
/*  86 */   private String error_message = "";
/*  87 */   private int error_type = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LWSDiskManager(LightWeightSeed _lws, File _save_file)
/*     */   {
/*  94 */     this.lws = _lws;
/*  95 */     this.save_file = _save_file;
/*     */     
/*  97 */     this.disk_access_controller = DiskManagerImpl.getDefaultDiskAccessController();
/*     */     
/*  99 */     this.download_state = new LWSDiskManagerState();
/*     */     
/* 101 */     TOTorrent torrent = this.lws.getTOTorrent(false);
/*     */     
/* 103 */     this.pieces = new sePiece[torrent.getNumberOfPieces()];
/*     */     
/* 105 */     for (int i = 0; i < this.pieces.length; i++)
/*     */     {
/* 107 */       this.pieces[i] = piece;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 114 */     return this.lws.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getCacheMode()
/*     */   {
/* 121 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] getReadStats()
/*     */   {
/* 127 */     if (this.reader == null)
/*     */     {
/* 129 */       return new long[] { 0L, 0L };
/*     */     }
/*     */     
/* 132 */     return this.reader.getStats();
/*     */   }
/*     */   
/*     */   public void start()
/*     */   {
/*     */     try
/*     */     {
/* 139 */       TOTorrent torrent = this.lws.getTOTorrent(false);
/*     */       
/* 141 */       this.internal_name = ByteFormatter.nicePrint(torrent.getHash(), true);
/*     */       
/* 143 */       LocaleUtilDecoder locale_decoder = LocaleTorrentUtil.getTorrentEncoding(torrent);
/*     */       
/* 145 */       this.piece_mapper = DMPieceMapperFactory.create(torrent);
/*     */       
/* 147 */       this.piece_mapper.construct(locale_decoder, this.save_file.getName());
/*     */       
/* 149 */       this.files = getFileInfo(this.piece_mapper.getFiles(), this.save_file);
/*     */       
/* 151 */       this.reader = DMAccessFactory.createReader(this);
/*     */       
/* 153 */       this.reader.start();
/*     */       
/* 155 */       if (this.state != 10)
/*     */       {
/* 157 */         this.started = true;
/*     */         
/* 159 */         this.state = 4;
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 163 */       setFailed("start failed - " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DiskManagerFileInfoImpl[] getFileInfo(DMPieceMapperFile[] pm_files, File save_location)
/*     */   {
/* 172 */     boolean ok = false;
/*     */     
/* 174 */     DiskManagerFileInfoImpl[] local_files = new DiskManagerFileInfoImpl[pm_files.length];
/*     */     
/*     */     try
/*     */     {
/* 178 */       TOTorrent torrent = this.lws.getTOTorrent(false);
/*     */       
/* 180 */       if (torrent.isSimpleTorrent())
/*     */       {
/* 182 */         save_location = save_location.getParentFile();
/*     */       }
/*     */       
/* 185 */       for (i = 0; i < pm_files.length; i++)
/*     */       {
/* 187 */         DMPieceMapperFile pm_info = pm_files[i];
/*     */         
/* 189 */         File relative_file = pm_info.getDataFile();
/*     */         
/* 191 */         long target_length = pm_info.getLength();
/*     */         
/* 193 */         DiskManagerFileInfoImpl file_info = new DiskManagerFileInfoImpl(this, save_location.toString(), relative_file, i, pm_info.getTorrentFile(), 1);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */         local_files[i] = file_info;
/*     */         
/* 204 */         CacheFile cache_file = file_info.getCacheFile();
/* 205 */         File data_file = file_info.getFile(true);
/*     */         
/* 207 */         if (!cache_file.exists())
/*     */         {
/* 209 */           throw new Exception("File '" + data_file + "' doesn't exist");
/*     */         }
/*     */         
/* 212 */         if (cache_file.getLength() != target_length)
/*     */         {
/* 214 */           throw new Exception("File '" + data_file + "' doesn't exist");
/*     */         }
/*     */         
/*     */ 
/* 218 */         pm_info.setFileInfo(file_info);
/*     */       }
/*     */       
/* 221 */       ok = true;
/*     */       int i;
/* 223 */       return local_files;
/*     */     }
/*     */     catch (Throwable e) {
/*     */       int i;
/* 227 */       setFailed("getFiles failed - " + Debug.getNestedExceptionMessage(e));
/*     */       int i;
/* 229 */       return null;
/*     */     }
/*     */     finally
/*     */     {
/* 233 */       if (!ok)
/*     */       {
/* 235 */         for (int i = 0; i < local_files.length; i++)
/*     */         {
/* 237 */           if (local_files[i] != null)
/*     */           {
/* 239 */             local_files[i].close();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieceDone(DiskManagerPieceImpl dmPiece, boolean done) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean stop(boolean closing)
/*     */   {
/* 257 */     this.started = false;
/*     */     
/* 259 */     if (this.reader != null)
/*     */     {
/* 261 */       this.reader.stop();
/*     */       
/* 263 */       this.reader = null;
/*     */     }
/*     */     
/* 266 */     if (this.files != null)
/*     */     {
/* 268 */       for (int i = 0; i < this.files.length; i++) {
/*     */         try
/*     */         {
/* 271 */           this.files[i].getCacheFile().close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 275 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 280 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStopped()
/*     */   {
/* 286 */     return !this.started;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean filesExist()
/*     */   {
/* 292 */     throw new RuntimeException("filesExist not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerWriteRequest createWriteRequest(int pieceNumber, int offset, DirectByteBuffer data, Object user_data)
/*     */   {
/* 303 */     throw new RuntimeException("createWriteRequest not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueWriteRequest(DiskManagerWriteRequest request, DiskManagerWriteRequestListener listener)
/*     */   {
/* 311 */     throw new RuntimeException("enqueueWriteRequest not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasOutstandingWriteRequestForPiece(int piece_number)
/*     */   {
/* 318 */     throw new RuntimeException("hasOutstandingWriteRequestForPiece not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasOutstandingReadRequestForPiece(int piece_number)
/*     */   {
/* 325 */     throw new RuntimeException("hasOutstandingReadRequestForPiece not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasOutstandingCheckRequestForPiece(int piece_number)
/*     */   {
/* 332 */     throw new RuntimeException("hasOutstandingCheckRequestForPiece not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DirectByteBuffer readBlock(int pieceNumber, int offset, int length)
/*     */   {
/* 341 */     return this.reader.readBlock(pieceNumber, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerReadRequest createReadRequest(int pieceNumber, int offset, int length)
/*     */   {
/* 350 */     return this.reader.createReadRequest(pieceNumber, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueReadRequest(DiskManagerReadRequest request, DiskManagerReadRequestListener listener)
/*     */   {
/* 358 */     this.reader.readBlock(request, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerCheckRequest createCheckRequest(int pieceNumber, Object user_data)
/*     */   {
/* 366 */     DMChecker checker = getChecker();
/*     */     
/* 368 */     return checker.createCheckRequest(pieceNumber, user_data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueCheckRequest(DiskManagerCheckRequest request, DiskManagerCheckRequestListener listener)
/*     */   {
/* 376 */     DMChecker checker = getChecker();
/*     */     
/* 378 */     checker.enqueueCheckRequest(request, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enqueueCompleteRecheckRequest(DiskManagerCheckRequest request, DiskManagerCheckRequestListener listener)
/*     */   {
/* 386 */     throw new RuntimeException("enqueueCompleteRecheckRequest not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPieceCheckingEnabled(boolean enabled) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveResumeData(boolean interim_save) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerPiece[] getPieces()
/*     */   {
/* 404 */     return this.pieces;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskManagerPiece getPiece(int index)
/*     */   {
/* 411 */     return this.pieces[index];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInteresting(int piece_num)
/*     */   {
/* 418 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isDone(int piece_num)
/*     */   {
/* 425 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNbPieces()
/*     */   {
/* 431 */     return this.pieces.length;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfo[] getFiles()
/*     */   {
/* 437 */     return this.files;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfoSet getFileSet()
/*     */   {
/* 443 */     throw new RuntimeException("getFileSet not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getState()
/*     */   {
/* 449 */     return this.state;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalLength()
/*     */   {
/* 455 */     return this.piece_mapper.getTotalLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPieceLength()
/*     */   {
/* 461 */     return this.piece_mapper.getPieceLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getPieceLength(int piece_number)
/*     */   {
/* 468 */     if (piece_number == this.pieces.length - 1)
/*     */     {
/* 470 */       return this.piece_mapper.getLastPieceLength();
/*     */     }
/*     */     
/*     */ 
/* 474 */     return this.piece_mapper.getPieceLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getLastPieceLength()
/*     */   {
/* 481 */     return this.piece_mapper.getLastPieceLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRemaining()
/*     */   {
/* 487 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRemainingExcludingDND()
/*     */   {
/* 493 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentDone()
/*     */   {
/* 499 */     return 100;
/*     */   }
/*     */   
/*     */   public int getPercentDoneExcludingDND()
/*     */   {
/* 504 */     return 1000;
/*     */   }
/*     */   
/*     */   public long getSizeExcludingDND() {
/* 508 */     return getTotalLength();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getErrorMessage()
/*     */   {
/* 514 */     return this.error_message;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getErrorType()
/*     */   {
/* 520 */     return this.error_type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadEnded(DiskManager.OperationStatus op_status) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveDataFiles(File new_parent_dir, String new_name, DiskManager.OperationStatus op_status)
/*     */   {
/* 534 */     throw new RuntimeException("moveDataFiles not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCompleteRecheckStatus()
/*     */   {
/* 540 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMoveProgress()
/*     */   {
/* 546 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean checkBlockConsistencyForWrite(String originator, int pieceNumber, int offset, DirectByteBuffer data)
/*     */   {
/* 556 */     long pos = pieceNumber * this.piece_mapper.getPieceLength() + offset + data.remaining((byte)1);
/*     */     
/* 558 */     return pos <= this.piece_mapper.getTotalLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean checkBlockConsistencyForRead(String originator, boolean peer_request, int pieceNumber, int offset, int length)
/*     */   {
/* 569 */     return DiskManagerUtil.checkBlockConsistencyForRead(this, originator, peer_request, pieceNumber, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean checkBlockConsistencyForHint(String originator, int pieceNumber, int offset, int length)
/*     */   {
/* 579 */     return DiskManagerUtil.checkBlockConsistencyForHint(this, originator, pieceNumber, offset, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(DiskManagerListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(DiskManagerListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasListener(DiskManagerListener l)
/*     */   {
/* 599 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void saveState() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public DiskAccessController getDiskAccessController()
/*     */   {
/* 610 */     return this.disk_access_controller;
/*     */   }
/*     */   
/*     */ 
/*     */   public DMPieceMap getPieceMap()
/*     */   {
/* 616 */     DMPieceMap map = this.piece_map_use_accessor;
/*     */     
/* 618 */     if (map == null)
/*     */     {
/* 620 */       this.piece_map_use_accessor = (map = this.piece_mapper.getPieceMap());
/*     */     }
/*     */     
/* 623 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DMPieceList getPieceList(int piece_number)
/*     */   {
/* 630 */     DMPieceMap map = getPieceMap();
/*     */     
/* 632 */     return map.getPieceList(piece_number);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected DMChecker getChecker()
/*     */   {
/* 639 */     DMChecker checker = this.checker_use_accessor;
/*     */     
/* 641 */     if (checker == null)
/*     */     {
/* 643 */       checker = this.checker_use_accessor = DMAccessFactory.createChecker(this);
/*     */     }
/*     */     
/* 646 */     return checker;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] getPieceHash(int piece_number)
/*     */     throws TOTorrentException
/*     */   {
/* 655 */     return this.lws.getTorrent().getPieces()[piece_number];
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerRecheckScheduler getRecheckScheduler()
/*     */   {
/* 661 */     throw new RuntimeException("getPieceHash not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadRemoved() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void setFailed(String reason)
/*     */   {
/* 673 */     this.started = false;
/*     */     
/* 675 */     this.state = 10;
/*     */     
/* 677 */     this.error_message = reason;
/* 678 */     this.error_type = 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setFailed(DiskManagerFileInfo file, String reason)
/*     */   {
/* 686 */     this.started = false;
/*     */     
/* 688 */     this.state = 10;
/*     */     
/* 690 */     this.error_message = reason;
/* 691 */     this.error_type = 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAllocated()
/*     */   {
/* 697 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAllocated(long num) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPercentDone(int num) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TOTorrent getTorrent()
/*     */   {
/* 715 */     return this.lws.getTOTorrent(false);
/*     */   }
/*     */   
/*     */ 
/*     */   public String[] getStorageTypes()
/*     */   {
/* 721 */     throw new RuntimeException("getStorageTypes not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getStorageType(int fileIndex)
/*     */   {
/* 728 */     throw new RuntimeException("getStorageType not implemented");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void accessModeChanged(DiskManagerFileInfoImpl file, int old_mode, int new_mode) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void skippedFileSetChanged(DiskManagerFileInfo file) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void priorityChanged(DiskManagerFileInfo file) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getSaveLocation()
/*     */   {
/* 754 */     return this.save_file;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInternalName()
/*     */   {
/* 760 */     return this.internal_name;
/*     */   }
/*     */   
/*     */ 
/*     */   public DownloadManagerState getDownloadState()
/*     */   {
/* 766 */     return this.download_state;
/*     */   }
/*     */   
/*     */   public long getPriorityChangeMarker()
/*     */   {
/* 771 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void generateEvidence(IndentWriter writer) {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class sePiece
/*     */     implements DiskManagerPiece
/*     */   {
/* 784 */     public void clearChecking() { throw new RuntimeException("clearChecking not implemented"); }
/* 785 */     public boolean isNeedsCheck() { throw new RuntimeException("isNeedsCheck not implemented"); }
/* 786 */     public boolean spansFiles() { throw new RuntimeException("spansfiles not implemented"); }
/* 787 */     public int getLength() { throw new RuntimeException("getLength not implemented"); }
/* 788 */     public int getNbBlocks() { throw new RuntimeException("getNbBlocks not implemented"); }
/* 789 */     public int getPieceNumber() { throw new RuntimeException("getPieceNumber not implemented"); }
/* 790 */     public int getBlockSize(int b) { throw new RuntimeException("getBlockSize not implemented"); }
/* 791 */     public boolean isWritten() { throw new RuntimeException("isWritten not implemented"); }
/* 792 */     public int getNbWritten() { throw new RuntimeException("getNbWritten not implemented"); }
/* 793 */     public boolean[] getWritten() { throw new RuntimeException("getWritten not implemented"); }
/* 794 */     public void reDownloadBlock(int blockNumber) { throw new RuntimeException("reDownloadBlock not implemented"); }
/* 795 */     public void reset() { throw new RuntimeException("reset not implemented"); }
/* 796 */     public boolean isDownloadable() { return false; }
/* 797 */     public void setDownloadable() { throw new RuntimeException("setRequestable not implemented"); }
/* 798 */     public DiskManager getManager() { throw new RuntimeException("getManager not implemented"); }
/* 799 */     public boolean calcNeeded() { throw new RuntimeException("calcNeeded not implemented"); }
/* 800 */     public void clearNeeded() { throw new RuntimeException("clearNeeded not implemented"); }
/* 801 */     public boolean isNeeded() { throw new RuntimeException("isNeeded not implemented"); }
/* 802 */     public void setNeeded() { throw new RuntimeException("setNeeded not implemented"); }
/* 803 */     public void setNeeded(boolean b) { throw new RuntimeException("setNeeded not implemented"); }
/* 804 */     public void setWritten(int b) { throw new RuntimeException("setWritten not implemented"); }
/* 805 */     public boolean isWritten(int blockNumber) { throw new RuntimeException("isWritten not implemented"); }
/* 806 */     public boolean calcChecking() { throw new RuntimeException("calcChecking not implemented"); }
/* 807 */     public boolean isChecking() { return false; }
/* 808 */     public void setChecking() { throw new RuntimeException("setChecking not implemented"); }
/* 809 */     public void setChecking(boolean b) { throw new RuntimeException("setChecking not implemented"); }
/* 810 */     public boolean calcDone() { throw new RuntimeException("calcDone not implemented"); }
/* 811 */     public boolean isDone() { return true; }
/* 812 */     public boolean isInteresting() { return false; }
/* 813 */     public boolean isSkipped() { return false; }
/* 814 */     public String getString() { return ""; }
/* 815 */     public short getReadCount() { return 0; }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setReadCount(short c) {}
/*     */     
/*     */ 
/*     */     public void setDone(boolean b)
/*     */     {
/* 824 */       if (!b)
/*     */       {
/* 826 */         Debug.out("Piece failed recheck");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/lws/LWSDiskManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */