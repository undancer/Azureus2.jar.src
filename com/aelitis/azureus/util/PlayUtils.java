/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.download.DownloadManagerEnhancer;
/*     */ import com.aelitis.azureus.core.download.EnhancedDownloadManager;
/*     */ import com.aelitis.azureus.core.download.StreamManager;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentV3;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
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
/*     */ public class PlayUtils
/*     */ {
/*     */   public static final boolean COMPLETE_PLAY_ONLY = true;
/*     */   public static final int fileSizeThreshold = 90;
/*     */   public static final String playableFileExtensions = ".avi .flv .flc .mp4 .divx .h264 .mkv .mov .mp2 .m4v .mp3 .aac, .mts, .m2ts";
/*  76 */   private static volatile String actualPlayableFileExtensions = ".avi .flv .flc .mp4 .divx .h264 .mkv .mov .mp2 .m4v .mp3 .aac, .mts, .m2ts";
/*     */   
/*     */ 
/*     */   private static Boolean hasQuickTime;
/*     */   
/*     */ 
/*     */   public static boolean prepareForPlay(org.gudy.azureus2.core3.download.DownloadManager dm)
/*     */   {
/*  84 */     EnhancedDownloadManager edm = DownloadManagerEnhancer.getSingleton().getEnhancedDownload(dm);
/*     */     
/*     */ 
/*  87 */     if (edm != null)
/*     */     {
/*  89 */       edm.setProgressiveMode(true);
/*     */       
/*  91 */       return true;
/*     */     }
/*     */     
/*  94 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean canProgressiveOrIsComplete(TOTorrent torrent) {
/*  98 */     if (torrent == null) {
/*  99 */       return false;
/*     */     }
/*     */     try {
/* 102 */       DownloadManagerEnhancer enhancer = DownloadManagerEnhancer.getSingleton();
/* 103 */       EnhancedDownloadManager edm = DownloadManagerEnhancer.getSingleton().getEnhancedDownload(torrent.getHash());
/*     */       
/*     */ 
/* 106 */       if (edm == null) {
/* 107 */         return (enhancer.isProgressiveAvailable()) && (PlatformTorrentUtils.isContentProgressive(torrent));
/*     */       }
/*     */       
/*     */ 
/* 111 */       boolean complete = edm.getDownloadManager().isDownloadComplete(false);
/* 112 */       if (complete) {
/* 113 */         return true;
/*     */       }
/*     */       
/*     */ 
/* 117 */       if (!edm.supportsProgressiveMode()) {
/* 118 */         return false;
/*     */       }
/*     */     } catch (TOTorrentException e) {
/* 121 */       return false;
/*     */     }
/*     */     
/* 124 */     return true;
/*     */   }
/*     */   
/*     */   public static boolean canUseEMP(org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file) {
/* 128 */     return isExternallyPlayable(file);
/*     */   }
/*     */   
/*     */   public static boolean canUseEMP(TOTorrent torrent, int file_index)
/*     */   {
/* 133 */     return canUseEMP(torrent, file_index, true);
/*     */   }
/*     */   
/*     */   public static boolean canUseEMP(TOTorrent torrent, int file_index, boolean complete_only) {
/* 137 */     if (torrent == null) {
/* 138 */       return false;
/*     */     }
/*     */     
/* 141 */     if (canPlayViaExternalEMP(torrent, file_index, complete_only)) {
/* 142 */       return true;
/*     */     }
/*     */     
/* 145 */     return false;
/*     */   }
/*     */   
/*     */   private static boolean canPlay(org.gudy.azureus2.core3.download.DownloadManager dm, int file_index) {
/* 149 */     if (dm == null) {
/* 150 */       return false;
/*     */     }
/* 152 */     TOTorrent torrent = dm.getTorrent();
/* 153 */     return canUseEMP(torrent, file_index);
/*     */   }
/*     */   
/*     */   private static boolean canPlay(TOTorrent torrent, int file_index) {
/* 157 */     if (!PlatformTorrentUtils.isContent(torrent, false)) {
/* 158 */       return false;
/*     */     }
/*     */     
/* 161 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 162 */       return false;
/*     */     }
/*     */     
/* 165 */     GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 166 */     org.gudy.azureus2.core3.download.DownloadManager dm = gm.getDownloadManager(torrent);
/*     */     
/*     */ 
/* 169 */     if (dm != null) {
/* 170 */       return (dm.getAssumedComplete()) || (canUseEMP(torrent, file_index));
/*     */     }
/* 172 */     return canUseEMP(torrent, file_index);
/*     */   }
/*     */   
/*     */ 
/* 176 */   private static ThreadLocal<int[]> tls_non_block_indicator = new ThreadLocal()
/*     */   {
/*     */ 
/*     */     public int[] initialValue()
/*     */     {
/*     */ 
/* 182 */       return new int[1];
/*     */     }
/*     */   };
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
/*     */   public static boolean canPlayDS(Object ds, int file_index, boolean block_for_accuracy)
/*     */   {
/* 200 */     if (ds == null) {
/* 201 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 205 */       if (!block_for_accuracy)
/*     */       {
/* 207 */         ((int[])tls_non_block_indicator.get())[0] += 1;
/*     */       }
/*     */       boolean bool1;
/* 210 */       if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 211 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo fi = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/* 212 */         bool1 = canPlayDS(fi.getDownloadManager(), fi.getIndex(), block_for_accuracy);
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
/* 236 */         if (!block_for_accuracy)
/*     */         {
/* 238 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool1;
/*     */       }
/* 215 */       org.gudy.azureus2.core3.download.DownloadManager dm = DataSourceUtils.getDM(ds);
/* 216 */       if (dm != null) {
/* 217 */         bool1 = canPlay(dm, file_index);
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
/* 236 */         if (!block_for_accuracy)
/*     */         {
/* 238 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool1;
/*     */       }
/* 219 */       TOTorrent torrent = DataSourceUtils.getTorrent(ds);
/* 220 */       boolean bool2; if (torrent != null) {
/* 221 */         bool2 = canPlay(torrent, file_index);
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
/* 236 */         if (!block_for_accuracy)
/*     */         {
/* 238 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool2;
/*     */       }
/* 223 */       if ((ds instanceof VuzeActivitiesEntry)) {
/* 224 */         bool2 = ((VuzeActivitiesEntry)ds).isPlayable(block_for_accuracy);
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
/* 236 */         if (!block_for_accuracy)
/*     */         {
/* 238 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool2;
/*     */       }
/*     */       SelectedContentV3 sel;
/* 227 */       if ((ds instanceof SelectedContentV3)) {
/* 228 */         sel = (SelectedContentV3)ds;
/* 229 */         boolean bool4 = sel.canPlay();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */         if (!block_for_accuracy)
/*     */         {
/* 238 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool4;
/*     */       }
/* 232 */       boolean bool3 = false;
/*     */       
/*     */ 
/*     */ 
/* 236 */       if (!block_for_accuracy)
/*     */       {
/* 238 */         ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool3;
/*     */     }
/*     */     finally
/*     */     {
/* 236 */       if (!block_for_accuracy)
/*     */       {
/* 238 */         ((int[])tls_non_block_indicator.get())[0] -= 1;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isStreamPermitted()
/*     */   {
/* 248 */     FeatureManager fm = PluginInitializer.getDefaultInterface().getUtilities().getFeatureManager();
/*     */     
/* 250 */     return fm.isFeatureInstalled("core");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean canStream(org.gudy.azureus2.core3.download.DownloadManager dm, int file_index)
/*     */   {
/* 258 */     if (dm == null)
/*     */     {
/* 260 */       return false;
/*     */     }
/*     */     
/*     */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo file;
/*     */     
/* 265 */     if (file_index == -1)
/*     */     {
/* 267 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo file = dm.getDownloadState().getPrimaryFile();
/* 268 */       if (file == null) {
/* 269 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/* 270 */         if (files.length == 0) {
/* 271 */           return false;
/*     */         }
/* 273 */         file = files[0];
/*     */       }
/*     */       
/* 276 */       file_index = file.getIndex();
/*     */     }
/*     */     else
/*     */     {
/* 280 */       file = dm.getDiskManagerFileInfoSet().getFiles()[file_index];
/*     */     }
/*     */     
/* 283 */     if (file.getDownloaded() == file.getLength())
/*     */     {
/* 285 */       return false;
/*     */     }
/*     */     
/* 288 */     if (!StreamManager.getSingleton().isStreamingUsable())
/*     */     {
/* 290 */       return false;
/*     */     }
/*     */     
/* 293 */     TOTorrent torrent = dm.getTorrent();
/*     */     
/* 295 */     return canUseEMP(torrent, file_index, false);
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
/*     */   public static boolean canStreamDS(Object ds, int file_index, boolean block_for_accuracy)
/*     */   {
/* 312 */     if (ds == null)
/*     */     {
/* 314 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 318 */       if (!block_for_accuracy)
/*     */       {
/* 320 */         ((int[])tls_non_block_indicator.get())[0] += 1;
/*     */       }
/*     */       
/* 323 */       if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo)) {
/* 324 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo fi = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/* 325 */         bool = canStreamDS(fi.getDownloadManager(), fi.getIndex(), block_for_accuracy);
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
/* 339 */         if (!block_for_accuracy)
/*     */         {
/* 341 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool;
/*     */       }
/* 328 */       org.gudy.azureus2.core3.download.DownloadManager dm = DataSourceUtils.getDM(ds);
/*     */       
/* 330 */       if (dm != null)
/*     */       {
/* 332 */         bool = canStream(dm, file_index);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 339 */         if (!block_for_accuracy)
/*     */         {
/* 341 */           ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool;
/*     */       }
/* 335 */       boolean bool = false;
/*     */       
/*     */ 
/*     */ 
/* 339 */       if (!block_for_accuracy)
/*     */       {
/* 341 */         ((int[])tls_non_block_indicator.get())[0] -= 1; } return bool;
/*     */     }
/*     */     finally
/*     */     {
/* 339 */       if (!block_for_accuracy)
/*     */       {
/* 341 */         ((int[])tls_non_block_indicator.get())[0] -= 1;
/*     */       }
/*     */     }
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
/*     */   public static URL getMediaServerContentURL(org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file)
/*     */   {
/* 433 */     PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/* 434 */     PluginInterface pi = pm.getPluginInterfaceByID("azupnpav", false);
/*     */     
/* 436 */     if (pi == null) {
/* 437 */       Logger.log(new LogEvent(LogIDs.UI3, "Media server plugin not found"));
/* 438 */       return null;
/*     */     }
/*     */     
/* 441 */     if (!pi.getPluginState().isOperational()) {
/* 442 */       Logger.log(new LogEvent(LogIDs.UI3, "Media server plugin not operational"));
/* 443 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 447 */       if (hasQuickTime == null)
/*     */       {
/* 449 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */         
/* 451 */         if (uif != null)
/*     */         {
/* 453 */           hasQuickTime = Boolean.valueOf(uif.isProgramInstalled(".qtl", "Quicktime"));
/*     */           try
/*     */           {
/* 456 */             pi.getIPC().invoke("setQuickTimeAvailable", new Object[] { hasQuickTime });
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 460 */             Logger.log(new LogEvent(LogIDs.UI3, 1, "IPC to media server plugin failed", e));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 466 */       boolean use_peek = ((int[])tls_non_block_indicator.get())[0] > 0;
/*     */       
/*     */ 
/*     */ 
/* 470 */       if (use_peek) { if (pi.getIPC().canInvoke("peekContentURL", new Object[] { file }))
/*     */         {
/* 472 */           Object url = pi.getIPC().invoke("peekContentURL", new Object[] { file });
/*     */           break label243;
/*     */         }
/*     */       }
/* 476 */       Object url = pi.getIPC().invoke("getContentURL", new Object[] { file });
/*     */       
/*     */       label243:
/* 479 */       if ((url instanceof String)) {
/* 480 */         return new URL((String)url);
/*     */       }
/*     */     } catch (Throwable e) {
/* 483 */       Logger.log(new LogEvent(LogIDs.UI3, 1, "IPC to media server plugin failed", e));
/*     */     }
/*     */     
/*     */ 
/* 487 */     return null;
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
/* 520 */   private static AtomicInteger dm_uid = new AtomicInteger();
/*     */   
/* 522 */   private static Map<String, Object[]> ext_play_cache = new LinkedHashMap(100, 0.75F, true)
/*     */   {
/*     */ 
/*     */ 
/*     */     protected boolean removeEldestEntry(Map.Entry<String, Object[]> eldest)
/*     */     {
/*     */ 
/* 529 */       return size() > 100;
/*     */     }
/*     */   };
/*     */   
/*     */   public static boolean isExternallyPlayable(Download d, int file_index, boolean complete_only)
/*     */   {
/* 535 */     if (d == null)
/*     */     {
/* 537 */       return false;
/*     */     }
/*     */     
/* 540 */     boolean use_cache = d.getState() != 4;
/*     */     
/* 542 */     String cache_key = null;
/* 543 */     long now = 0L;
/*     */     
/* 545 */     if (use_cache)
/*     */     {
/* 547 */       Integer uid = (Integer)d.getUserData(PlayUtils.class);
/*     */       
/* 549 */       if (uid == null)
/*     */       {
/* 551 */         uid = Integer.valueOf(dm_uid.getAndIncrement());
/*     */         
/* 553 */         d.setUserData(PlayUtils.class, uid);
/*     */       }
/*     */       
/* 556 */       cache_key = uid + "/" + file_index + "/" + complete_only;
/*     */       
/*     */       Object[] cached;
/*     */       
/* 560 */       synchronized (ext_play_cache)
/*     */       {
/* 562 */         cached = (Object[])ext_play_cache.get(cache_key);
/*     */       }
/*     */       
/* 565 */       now = SystemTime.getMonotonousTime();
/*     */       
/* 567 */       if (cached != null)
/*     */       {
/* 569 */         if (now - ((Long)cached[0]).longValue() < 60000L)
/*     */         {
/* 571 */           return ((Boolean)cached[1]).booleanValue();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 576 */     boolean result = isExternallyPlayableSupport(d, file_index, complete_only);
/*     */     
/* 578 */     if (use_cache)
/*     */     {
/* 580 */       synchronized (ext_play_cache)
/*     */       {
/* 582 */         ext_play_cache.put(cache_key, new Object[] { Long.valueOf(now), Boolean.valueOf(result) });
/*     */       }
/*     */     }
/*     */     
/* 586 */     return result;
/*     */   }
/*     */   
/*     */   private static boolean isExternallyPlayableSupport(Download d, int file_index, boolean complete_only)
/*     */   {
/* 591 */     int primary_file_index = -1;
/*     */     
/* 593 */     if (file_index == -1)
/*     */     {
/*     */ 
/* 596 */       org.gudy.azureus2.core3.download.DownloadManager dm = PluginCoreUtils.unwrap(d);
/*     */       
/* 598 */       if (dm == null)
/*     */       {
/* 600 */         return false;
/*     */       }
/*     */       
/* 603 */       org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = null;
/*     */       try {
/* 605 */         file = PluginCoreUtils.wrap(dm.getDownloadState().getPrimaryFile());
/*     */       } catch (DownloadException e) {
/* 607 */         return false;
/*     */       }
/*     */       
/* 610 */       if (file == null)
/*     */       {
/* 612 */         return false;
/*     */       }
/*     */       
/* 615 */       if (file.getDownloaded() != file.getLength())
/*     */       {
/* 617 */         if ((complete_only) || (getMediaServerContentURL(file) == null))
/*     */         {
/* 619 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 623 */       primary_file_index = file.getIndex();
/*     */     }
/*     */     else
/*     */     {
/* 627 */       org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = d.getDiskManagerFileInfo(file_index);
/*     */       
/* 629 */       if (file.getDownloaded() != file.getLength())
/*     */       {
/* 631 */         if ((complete_only) || (getMediaServerContentURL(file) == null))
/*     */         {
/* 633 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 637 */       primary_file_index = file_index;
/*     */     }
/*     */     
/* 640 */     if (primary_file_index == -1)
/*     */     {
/* 642 */       return false;
/*     */     }
/*     */     
/* 645 */     return isExternallyPlayable(d.getDiskManagerFileInfo()[primary_file_index]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int[] getExternallyPlayableFileIndexes(Download d, boolean complete_only)
/*     */   {
/* 653 */     org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[] fileInfos = d.getDiskManagerFileInfo();
/* 654 */     int count = d.getDiskManagerFileCount();
/* 655 */     int[] playableIndexes = new int[0];
/* 656 */     for (int i = 0; i < count; i++) {
/* 657 */       org.gudy.azureus2.plugins.disk.DiskManagerFileInfo fileInfo = d.getDiskManagerFileInfo(i);
/* 658 */       if ((!complete_only) || (fileInfo.getLength() == fileInfo.getDownloaded()))
/*     */       {
/*     */ 
/* 661 */         if (isExternallyPlayable(fileInfo)) {
/* 662 */           int[] newPlayableIndexes = new int[playableIndexes.length + 1];
/* 663 */           System.arraycopy(playableIndexes, 0, newPlayableIndexes, 0, playableIndexes.length);
/*     */           
/* 665 */           newPlayableIndexes[playableIndexes.length] = i;
/* 666 */           playableIndexes = newPlayableIndexes;
/*     */         } }
/*     */     }
/* 669 */     return playableIndexes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean isExternallyPlayable(org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file)
/*     */   {
/* 676 */     String name = file.getFile(true).getName();
/*     */     try
/*     */     {
/* 679 */       Download dl = file.getDownload();
/*     */       
/* 681 */       if (dl != null)
/*     */       {
/* 683 */         String is = PluginCoreUtils.unwrap(dl).getDownloadState().getAttribute("incompfilesuffix");
/*     */         
/* 685 */         if ((is != null) && (name.endsWith(is)))
/*     */         {
/* 687 */           name = name.substring(0, name.length() - is.length());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 693 */     int extIndex = name.lastIndexOf(".");
/*     */     
/* 695 */     if (extIndex > -1)
/*     */     {
/* 697 */       String ext = name.substring(extIndex);
/*     */       
/* 699 */       if (ext == null)
/*     */       {
/* 701 */         return false;
/*     */       }
/*     */       
/* 704 */       ext = ext.toLowerCase();
/*     */       
/* 706 */       if (getPlayableFileExtensions().contains(ext))
/*     */       {
/* 708 */         return true;
/*     */       }
/*     */     }
/*     */     
/* 712 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean isExternallyPlayable(TOTorrent torrent, int file_index, boolean complete_only) {
/* 716 */     if (torrent == null) {
/* 717 */       return false;
/*     */     }
/*     */     try {
/* 720 */       Download download = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getDownloadManager().getDownload(torrent.getHash());
/* 721 */       if (download != null) {
/* 722 */         return isExternallyPlayable(download, file_index, complete_only);
/*     */       }
/*     */     } catch (Exception e) {
/* 725 */       e.printStackTrace();
/*     */     }
/*     */     
/* 728 */     return false;
/*     */   }
/*     */   
/*     */   private static final boolean canPlayViaExternalEMP(TOTorrent torrent, int file_index, boolean complete_only) {
/* 732 */     if (torrent == null) {
/* 733 */       return false;
/*     */     }
/*     */     
/* 736 */     return isExternallyPlayable(torrent, file_index, complete_only);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getPlayableFileExtensions()
/*     */   {
/* 742 */     return actualPlayableFileExtensions;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setPlayableFileExtensions(String str)
/*     */   {
/* 754 */     actualPlayableFileExtensions = str;
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static int getPrimaryFileIndex(Download dl)
/*     */   {
/* 762 */     EnhancedDownloadManager edm = DownloadManagerEnhancer.getSingleton().getEnhancedDownload(PluginCoreUtils.unwrap(dl));
/*     */     
/* 764 */     if (edm == null)
/*     */     {
/* 766 */       return -1;
/*     */     }
/*     */     
/* 769 */     return edm.getPrimaryFileIndex();
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isEMPAvailable()
/*     */   {
/* 775 */     PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azemp");
/*     */     
/* 777 */     if ((pi == null) || (pi.getPluginState().isDisabled()))
/*     */     {
/* 779 */       return false;
/*     */     }
/*     */     
/* 782 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean playURL(URL url, String name)
/*     */   {
/*     */     try
/*     */     {
/* 791 */       PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azemp");
/*     */       
/* 793 */       if ((pi == null) || (pi.getPluginState().isDisabled()))
/*     */       {
/* 795 */         return false;
/*     */       }
/*     */       
/* 798 */       Class<?> ewp_class = pi.getPlugin().getClass().getClassLoader().loadClass("com.azureus.plugins.azemp.ui.swt.emp.EmbeddedPlayerWindowSWT");
/*     */       
/* 800 */       if (ewp_class != null)
/*     */       {
/* 802 */         Method ow = ewp_class.getMethod("openWindow", new Class[] { URL.class, String.class });
/*     */         
/* 804 */         if (ow != null)
/*     */         {
/* 806 */           ow.invoke(null, new Object[] { url, name });
/*     */           
/* 808 */           return true;
/*     */         }
/*     */       }
/*     */       
/* 812 */       return false;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 816 */       Debug.out(e);
/*     */     }
/* 818 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/PlayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */