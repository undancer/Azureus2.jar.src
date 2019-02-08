/*     */ package org.gudy.azureus2.core3.disk;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.LinkFileMap;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerImpl;
/*     */ import org.gudy.azureus2.core3.disk.impl.DiskManagerUtil;
/*     */ import org.gudy.azureus2.core3.disk.impl.resume.RDResumeHandler;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*     */ public class DiskManagerFactory
/*     */ {
/*     */   public static DiskManager create(TOTorrent torrent, DownloadManager manager)
/*     */   {
/*  48 */     DiskManagerImpl dm = new DiskManagerImpl(torrent, manager);
/*     */     
/*  50 */     if (dm.getState() != 10)
/*     */     {
/*  52 */       dm.start();
/*     */     }
/*     */     
/*  55 */     return dm;
/*     */   }
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
/*     */   public static void setResumeDataCompletelyValid(DownloadManagerState download_manager_state)
/*     */   {
/*  78 */     RDResumeHandler.setTorrentResumeDataComplete(download_manager_state);
/*     */   }
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
/*     */   public static void setTorrentResumeDataNearlyComplete(DownloadManagerState dms)
/*     */   {
/*  94 */     RDResumeHandler.setTorrentResumeDataNearlyComplete(dms);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean isTorrentResumeDataComplete(DownloadManagerState dms)
/*     */   {
/* 101 */     return RDResumeHandler.isTorrentResumeDataComplete(dms);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void deleteDataFiles(TOTorrent torrent, String torrent_save_dir, String torrent_save_file, boolean force_no_recycle)
/*     */   {
/* 111 */     DiskManagerImpl.deleteDataFiles(torrent, torrent_save_dir, torrent_save_file, force_no_recycle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DiskManagerFileInfoSet getFileInfoSkeleton(DownloadManager download_manager, DiskManagerListener listener)
/*     */   {
/* 119 */     return DiskManagerUtil.getFileInfoSkeleton(download_manager, listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setFileLinks(DownloadManager download_manager, LinkFileMap links)
/*     */   {
/* 127 */     DiskManagerImpl.setFileLinks(download_manager, links);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void clearResumeData(DownloadManager download_manager, DiskManagerFileInfo file)
/*     */   {
/* 135 */     RDResumeHandler.clearResumeData(download_manager, file);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void recheckFile(DownloadManager download_manager, DiskManagerFileInfo file)
/*     */   {
/* 143 */     RDResumeHandler.recheckFile(download_manager, file);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */