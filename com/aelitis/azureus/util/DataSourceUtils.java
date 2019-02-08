/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*     */ import com.aelitis.azureus.core.devices.TranscodeFile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataSourceUtils
/*     */ {
/*     */   public static org.gudy.azureus2.core3.disk.DiskManagerFileInfo getFileInfo(Object ds)
/*     */   {
/*     */     try
/*     */     {
/*  52 */       if ((ds instanceof org.gudy.azureus2.plugins.disk.DiskManagerFileInfo))
/*  53 */         return PluginCoreUtils.unwrap((org.gudy.azureus2.plugins.disk.DiskManagerFileInfo)ds);
/*  54 */       if ((ds instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo))
/*  55 */         return (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)ds;
/*  56 */       if (((ds instanceof ISelectedContent)) && (((ISelectedContent)ds).getFileIndex() >= 0))
/*     */       {
/*  58 */         ISelectedContent sc = (ISelectedContent)ds;
/*  59 */         int idx = sc.getFileIndex();
/*  60 */         DownloadManager dm = sc.getDownloadManager();
/*  61 */         return dm.getDiskManagerFileInfoSet().getFiles()[idx]; }
/*  62 */       if ((ds instanceof TranscodeJob)) {
/*  63 */         TranscodeJob tj = (TranscodeJob)ds;
/*     */         try {
/*  65 */           return PluginCoreUtils.unwrap(tj.getFile());
/*     */         }
/*     */         catch (DownloadException e) {}
/*  68 */       } else if ((ds instanceof TranscodeFile)) {
/*  69 */         TranscodeFile tf = (TranscodeFile)ds;
/*     */         try {
/*  71 */           org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = tf.getSourceFile();
/*  72 */           return PluginCoreUtils.unwrap(file);
/*     */         }
/*     */         catch (DownloadException e) {}
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  78 */       Debug.printStackTrace(e);
/*     */     }
/*  80 */     return null;
/*     */   }
/*     */   
/*     */   public static DownloadManager getDM(Object ds) {
/*     */     try {
/*  85 */       if ((ds instanceof DownloadManager))
/*  86 */         return (DownloadManager)ds;
/*  87 */       if ((ds instanceof VuzeActivitiesEntry)) {
/*  88 */         VuzeActivitiesEntry entry = (VuzeActivitiesEntry)ds;
/*  89 */         DownloadManager dm = entry.getDownloadManger();
/*  90 */         if (dm == null) {
/*  91 */           String assetHash = entry.getAssetHash();
/*  92 */           if ((assetHash != null) && (AzureusCoreFactory.isCoreRunning())) {
/*  93 */             GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/*  94 */             dm = gm.getDownloadManager(new HashWrapper(Base32.decode(assetHash)));
/*  95 */             entry.setDownloadManager(dm);
/*     */           }
/*     */         }
/*  98 */         return dm; }
/*  99 */       if (((ds instanceof TOTorrent)) && (AzureusCoreFactory.isCoreRunning())) {
/* 100 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 101 */         return gm.getDownloadManager((TOTorrent)ds); }
/* 102 */       if ((ds instanceof ISelectedContent))
/* 103 */         return getDM(((ISelectedContent)ds).getDownloadManager());
/* 104 */       if ((ds instanceof TranscodeJob)) {
/* 105 */         TranscodeJob tj = (TranscodeJob)ds;
/*     */         try {
/* 107 */           org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = tj.getFile();
/* 108 */           if (file != null) {
/* 109 */             Download download = tj.getFile().getDownload();
/* 110 */             if (download != null) {
/* 111 */               return PluginCoreUtils.unwrap(download);
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (DownloadException e) {}
/* 116 */       } else if ((ds instanceof TranscodeFile)) {
/* 117 */         TranscodeFile tf = (TranscodeFile)ds;
/*     */         try {
/* 119 */           org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = tf.getSourceFile();
/* 120 */           if (file != null) {
/* 121 */             Download download = file.getDownload();
/* 122 */             if (download != null) {
/* 123 */               return PluginCoreUtils.unwrap(download);
/*     */             }
/*     */           }
/*     */         } catch (DownloadException e) {}
/*     */       } else {
/* 128 */         if ((ds instanceof DeviceOfflineDownload))
/* 129 */           return PluginCoreUtils.unwrap(((DeviceOfflineDownload)ds).getDownload());
/* 130 */         if ((ds instanceof Download))
/* 131 */           return PluginCoreUtils.unwrap((Download)ds);
/* 132 */         if ((ds instanceof byte[])) {
/* 133 */           byte[] hash = (byte[])ds;
/* 134 */           GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 135 */           DownloadManager dm = gm.getDownloadManager(new HashWrapper(hash));
/* 136 */           if (dm != null)
/* 137 */             return dm;
/*     */         } else {
/* 139 */           if ((ds instanceof Object[])) {
/* 140 */             Object[] o = (Object[])ds;
/* 141 */             return getDM(o[0]); }
/* 142 */           if (((ds instanceof String)) && (AzureusCoreFactory.isCoreRunning())) {
/* 143 */             String hash = (String)ds;
/*     */             try {
/* 145 */               GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 146 */               DownloadManager dm = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/* 147 */               if (dm != null) {
/* 148 */                 return dm;
/*     */               }
/*     */             }
/*     */             catch (Exception e) {}
/*     */           }
/*     */         }
/*     */       }
/* 155 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = getFileInfo(ds);
/* 156 */       if (fileInfo != null) {
/* 157 */         return fileInfo.getDownloadManager();
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 162 */       Debug.printStackTrace(e);
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */   
/*     */   public static TOTorrent getTorrent(Object ds) {
/* 168 */     if ((ds instanceof TOTorrent)) {
/* 169 */       return (TOTorrent)ds;
/*     */     }
/*     */     
/* 172 */     if ((ds instanceof DownloadManager)) {
/* 173 */       TOTorrent torrent = ((DownloadManager)ds).getTorrent();
/* 174 */       if (torrent != null) {
/* 175 */         return torrent;
/*     */       }
/*     */     }
/* 178 */     if ((ds instanceof VuzeActivitiesEntry)) {
/* 179 */       TOTorrent torrent = ((VuzeActivitiesEntry)ds).getTorrent();
/* 180 */       if (torrent == null)
/*     */       {
/* 182 */         DownloadManager dm = getDM(ds);
/* 183 */         if (dm != null) {
/* 184 */           torrent = dm.getTorrent();
/*     */         }
/*     */       }
/* 187 */       return torrent;
/*     */     }
/*     */     
/* 190 */     if ((ds instanceof TranscodeFile)) {
/* 191 */       TranscodeFile tf = (TranscodeFile)ds;
/*     */       try {
/* 193 */         org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = tf.getSourceFile();
/* 194 */         if (file != null) {
/* 195 */           Download download = file.getDownload();
/* 196 */           if (download != null) {
/* 197 */             Torrent torrent = download.getTorrent();
/* 198 */             if (torrent != null) {
/* 199 */               return PluginCoreUtils.unwrap(torrent);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/* 207 */     if ((ds instanceof TranscodeJob)) {
/* 208 */       TranscodeJob tj = (TranscodeJob)ds;
/*     */       try {
/* 210 */         org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = tj.getFile();
/* 211 */         if (file != null) {
/* 212 */           Download download = tj.getFile().getDownload();
/*     */           
/* 214 */           if (download != null) {
/* 215 */             Torrent torrent = download.getTorrent();
/* 216 */             if (torrent != null) {
/* 217 */               return PluginCoreUtils.unwrap(torrent);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (DownloadException e) {}
/*     */     }
/*     */     
/* 225 */     if ((ds instanceof DeviceOfflineDownload)) {
/* 226 */       Torrent torrent = ((DeviceOfflineDownload)ds).getDownload().getTorrent();
/* 227 */       if (torrent != null) {
/* 228 */         return PluginCoreUtils.unwrap(torrent);
/*     */       }
/*     */     }
/*     */     
/* 232 */     if ((ds instanceof ISelectedContent)) {
/* 233 */       return ((ISelectedContent)ds).getTorrent();
/*     */     }
/*     */     
/* 236 */     if ((ds instanceof String)) {
/* 237 */       String hash = (String)ds;
/*     */       try {
/* 239 */         GlobalManager gm = AzureusCoreFactory.getSingleton().getGlobalManager();
/* 240 */         DownloadManager dm = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/* 241 */         if (dm != null) {
/* 242 */           return dm.getTorrent();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/* 249 */     DownloadManager dm = getDM(ds);
/* 250 */     if (dm != null) {
/* 251 */       return dm.getTorrent();
/*     */     }
/*     */     
/* 254 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isPlatformContent(Object ds)
/*     */   {
/* 263 */     TOTorrent torrent = getTorrent(ds);
/* 264 */     if (torrent != null) {
/* 265 */       return PlatformTorrentUtils.isContent(torrent, true);
/*     */     }
/* 267 */     if ((ds instanceof VuzeActivitiesEntry)) {
/* 268 */       return true;
/*     */     }
/*     */     
/* 271 */     return false;
/*     */   }
/*     */   
/*     */   public static String getHash(Object ds) {
/*     */     try {
/* 276 */       if ((ds instanceof VuzeActivitiesEntry)) {
/* 277 */         VuzeActivitiesEntry entry = (VuzeActivitiesEntry)ds;
/* 278 */         return entry.getAssetHash(); }
/* 279 */       if ((ds instanceof ISelectedContent))
/* 280 */         return ((ISelectedContent)ds).getHash();
/* 281 */       if ((ds instanceof byte[]))
/* 282 */         return Base32.encode((byte[])ds);
/* 283 */       if ((ds instanceof String))
/*     */       {
/* 285 */         return (String)ds;
/*     */       }
/*     */       
/* 288 */       TOTorrent torrent = getTorrent(ds);
/* 289 */       if (torrent != null) {
/* 290 */         return torrent.getHashWrapper().toBase32String();
/*     */       }
/*     */     } catch (Exception e) {
/* 293 */       Debug.printStackTrace(e);
/*     */     }
/* 295 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DownloadUrlInfo getDownloadInfo(Object ds)
/*     */   {
/* 304 */     if ((ds instanceof ISelectedContent)) {
/* 305 */       return ((ISelectedContent)ds).getDownloadInfo();
/*     */     }
/* 307 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/DataSourceUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */