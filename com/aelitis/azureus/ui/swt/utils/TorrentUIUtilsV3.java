/*     */ package com.aelitis.azureus.ui.swt.utils;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfoContentNetwork;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.browser.listener.DownloadUrlInfoSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.TorrentListViewsUtils;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.DataSourceUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import com.aelitis.azureus.util.UrlFilter;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*     */ import org.gudy.azureus2.core3.util.AERunnableObject;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.swt.FileDownloadWindow;
/*     */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TorrentUIUtilsV3
/*     */ {
/*     */   private static final String MSG_ALREADY_EXISTS = "OpenTorrentWindow.mb.alreadyExists";
/*     */   private static final String MSG_ALREADY_EXISTS_NAME = "OpenTorrentWindow.mb.alreadyExists.default.name";
/*  75 */   private static final Pattern hashPattern = Pattern.compile("download/([A-Z0-9]{32})\\.torrent");
/*     */   
/*     */ 
/*     */   static ImageLoader imageLoaderThumb;
/*     */   
/*     */ 
/*     */   public static void loadTorrent(DownloadUrlInfo dlInfo, final boolean playNow, final boolean playPrepare, final boolean bringToFront)
/*     */   {
/*  83 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  85 */         TorrentUIUtilsV3._loadTorrent(core, this.val$dlInfo, playNow, playPrepare, bringToFront);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void _loadTorrent(final AzureusCore core, DownloadUrlInfo dlInfo, final boolean playNow, final boolean playPrepare, boolean bringToFront)
/*     */   {
/*  95 */     if ((dlInfo instanceof DownloadUrlInfoSWT)) {
/*  96 */       DownloadUrlInfoSWT dlInfoSWT = (DownloadUrlInfoSWT)dlInfo;
/*  97 */       dlInfoSWT.invoke(playNow ? "play" : "download");
/*  98 */       return;
/*     */     }
/*     */     
/* 101 */     String url = dlInfo.getDownloadURL();
/*     */     try {
/* 103 */       Matcher m = hashPattern.matcher(url);
/* 104 */       if (m.find()) {
/* 105 */         String hash = m.group(1);
/* 106 */         GlobalManager gm = core.getGlobalManager();
/* 107 */         final DownloadManager dm = gm.getDownloadManager(new HashWrapper(Base32.decode(hash)));
/*     */         
/* 109 */         if (dm != null) {
/* 110 */           if ((playNow) || (playPrepare)) {
/* 111 */             new AEThread2("playExisting", true)
/*     */             {
/*     */               public void run() {
/* 114 */                 if (playNow) {
/* 115 */                   Debug.outNoStack("loadTorrent already exists.. playing", false);
/*     */                   
/*     */ 
/* 118 */                   TorrentListViewsUtils.playOrStream(dm, -1);
/*     */                 } else {
/* 120 */                   Debug.outNoStack("loadTorrent already exists.. preparing", false);
/*     */                   
/*     */ 
/* 123 */                   PlayUtils.prepareForPlay(dm);
/*     */                 }
/*     */                 
/*     */               }
/*     */             }.start();
/*     */           } else {
/* 129 */             new MessageBoxShell(32, "OpenTorrentWindow.mb.alreadyExists", new String[] { " ", dm.getDisplayName(), MessageText.getString("OpenTorrentWindow.mb.alreadyExists.default.name") }).open(null);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */           return;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 141 */       if (UrlFilter.getInstance().urlCanRPC(url)) {
/* 142 */         ContentNetwork cn = null;
/* 143 */         if ((dlInfo instanceof DownloadUrlInfoContentNetwork)) {
/* 144 */           cn = ((DownloadUrlInfoContentNetwork)dlInfo).getContentNetwork();
/*     */         }
/* 146 */         if (cn == null) {
/* 147 */           cn = ConstantsVuze.getDefaultContentNetwork();
/*     */         }
/* 149 */         url = cn.appendURLSuffix(url, false, true);
/*     */       }
/*     */       
/* 152 */       UIFunctionsSWT uiFunctions = (UIFunctionsSWT)UIFunctionsManager.getUIFunctions();
/* 153 */       if (uiFunctions != null)
/*     */       {
/* 155 */         if (bringToFront) {
/* 156 */           uiFunctions.bringToFront();
/*     */         }
/*     */         
/*     */ 
/* 160 */         Shell shell = uiFunctions.getMainShell();
/* 161 */         if (shell != null) {
/* 162 */           new FileDownloadWindow(shell, url, dlInfo.getReferer(), dlInfo.getRequestProperties(), null, new TorrentDownloaderCallBackInterface()
/*     */           {
/*     */ 
/*     */             public void TorrentDownloaderEvent(int state, TorrentDownloader inf)
/*     */             {
/*     */ 
/* 168 */               if (state == 3)
/*     */               {
/* 170 */                 File file = inf.getFile();
/* 171 */                 file.deleteOnExit();
/*     */                 
/*     */ 
/* 174 */                 if (!TorrentUtil.isFileTorrent(this.val$dlInfo.getDownloadURL(), file, file.getName(), true)) {
/*     */                   return;
/*     */                 }
/*     */                 TOTorrent torrent;
/*     */                 try
/*     */                 {
/* 180 */                   torrent = TorrentUtils.readFromFile(file, false);
/*     */                 } catch (TOTorrentException e) {
/* 182 */                   Debug.out(e);
/* 183 */                   return;
/*     */                 }
/*     */                 
/* 186 */                 if ((playNow) && (!com.aelitis.azureus.core.torrent.PlatformTorrentUtils.isPlatformTracker(torrent)))
/*     */                 {
/* 188 */                   Debug.out("stopped loading torrent because it's not in whitelist"); return;
/*     */                 }
/*     */                 
/*     */                 HashWrapper hw;
/*     */                 try
/*     */                 {
/* 194 */                   hw = torrent.getHashWrapper();
/*     */                 } catch (TOTorrentException e1) {
/* 196 */                   Debug.out(e1);
/* 197 */                   return;
/*     */                 }
/*     */                 
/* 200 */                 GlobalManager gm = core.getGlobalManager();
/*     */                 
/* 202 */                 if ((playNow) || (playPrepare)) {
/* 203 */                   DownloadManager existingDM = gm.getDownloadManager(hw);
/* 204 */                   if (existingDM != null) {
/* 205 */                     if (playNow) {
/* 206 */                       TorrentListViewsUtils.playOrStream(existingDM, -1);
/*     */                     } else {
/* 208 */                       PlayUtils.prepareForPlay(existingDM);
/*     */                     }
/* 210 */                     return;
/*     */                   }
/*     */                 }
/*     */                 
/* 214 */                 final HashWrapper fhw = hw;
/*     */                 
/* 216 */                 GlobalManagerListener l = new GlobalManagerAdapter()
/*     */                 {
/*     */                   public void downloadManagerAdded(DownloadManager dm) {
/*     */                     try {
/* 220 */                       TorrentUIUtilsV3.3.this.val$core.getGlobalManager().removeListener(this);
/*     */                       
/* 222 */                       TorrentUIUtilsV3.handleDMAdded(dm, TorrentUIUtilsV3.3.this.val$playNow, TorrentUIUtilsV3.3.this.val$playPrepare, fhw);
/*     */                     } catch (Exception e) {
/* 224 */                       Debug.out(e);
/*     */                     }
/*     */                     
/*     */                   }
/* 228 */                 };
/* 229 */                 gm.addListener(l, false);
/*     */                 
/* 231 */                 TorrentOpener.openTorrent(file.getAbsolutePath());
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 238 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void handleDMAdded(final DownloadManager dm, final boolean playNow, final boolean playPrepare, final HashWrapper fhw)
/*     */   {
/* 244 */     new AEThread2("playDM", true) {
/*     */       public void run() {
/*     */         try {
/* 247 */           HashWrapper hw = dm.getTorrent().getHashWrapper();
/* 248 */           if (!hw.equals(fhw)) {
/* 249 */             return;
/*     */           }
/*     */           
/* 252 */           if ((playNow) || (playPrepare)) {
/* 253 */             if (playNow) {
/* 254 */               TorrentListViewsUtils.playOrStream(dm, -1);
/*     */             } else {
/* 256 */               PlayUtils.prepareForPlay(dm);
/*     */             }
/*     */           }
/*     */         } catch (Exception e) {
/* 260 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addTorrentToGM(TOTorrent torrent)
/*     */   {
/* 274 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/*     */         try {
/* 279 */           File tempTorrentFile = File.createTempFile("AZU", ".torrent");
/* 280 */           tempTorrentFile.deleteOnExit();
/* 281 */           String filename = tempTorrentFile.getAbsolutePath();
/* 282 */           this.val$torrent.serialiseToBEncodedFile(tempTorrentFile);
/*     */           
/* 284 */           String savePath = COConfigurationManager.getStringParameter("Default save path");
/* 285 */           if ((savePath == null) || (savePath.length() == 0)) {
/* 286 */             savePath = ".";
/*     */           }
/*     */           
/* 289 */           core.getGlobalManager().addDownloadManager(filename, savePath);
/*     */         } catch (Throwable t) {
/* 291 */           Debug.out(t);
/*     */         }
/*     */       }
/*     */     });
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
/*     */   public static Image[] getContentImage(Object datasource, boolean big, ContentImageLoadedListener l)
/*     */   {
/* 311 */     if (l == null) {
/* 312 */       return null;
/*     */     }
/* 314 */     TOTorrent torrent = DataSourceUtils.getTorrent(datasource);
/* 315 */     if (torrent == null) {
/* 316 */       l.contentImageLoaded(null, true);
/* 317 */       return null;
/*     */     }
/*     */     
/* 320 */     if (imageLoaderThumb == null) {
/* 321 */       imageLoaderThumb = new ImageLoader(null, null);
/*     */     }
/*     */     
/* 324 */     String thumbnailUrl = com.aelitis.azureus.core.torrent.PlatformTorrentUtils.getContentThumbnailUrl(torrent);
/*     */     
/*     */ 
/* 327 */     if ((thumbnailUrl != null) && (imageLoaderThumb.imageExists(thumbnailUrl)))
/*     */     {
/* 329 */       Image image = imageLoaderThumb.getImage(thumbnailUrl);
/* 330 */       l.contentImageLoaded(image, true);
/* 331 */       return new Image[] { image };
/*     */     }
/*     */     
/* 334 */     String hash = null;
/*     */     try {
/* 336 */       hash = torrent.getHashWrapper().toBase32String();
/*     */     }
/*     */     catch (TOTorrentException e) {}
/* 339 */     if (hash == null) {
/* 340 */       l.contentImageLoaded(null, true);
/* 341 */       return null;
/*     */     }
/*     */     
/* 344 */     int thumbnailVersion = com.aelitis.azureus.core.util.PlatformTorrentUtils.getContentVersion(torrent);
/*     */     
/*     */ 
/*     */ 
/* 348 */     String id = "Thumbnail." + hash + "." + torrent.getSize() + "." + thumbnailVersion;
/*     */     
/* 350 */     Image image = imageLoaderThumb.imageAdded(id) ? imageLoaderThumb.getImage(id) : null;
/*     */     
/* 352 */     if ((image != null) && (!image.isDisposed())) {
/* 353 */       l.contentImageLoaded(image, true);
/* 354 */       return new Image[] { image };
/*     */     }
/*     */     
/* 357 */     byte[] imageBytes = com.aelitis.azureus.core.torrent.PlatformTorrentUtils.getContentThumbnail(torrent);
/*     */     
/* 359 */     if (imageBytes != null) {
/* 360 */       image = (Image)Utils.execSWTThreadWithObject("thumbcreator", new AERunnableObject()
/*     */       {
/*     */         public Object runSupport() {
/*     */           try {
/* 364 */             ByteArrayInputStream bis = new ByteArrayInputStream(this.val$imageBytes);
/* 365 */             return new Image(Display.getDefault(), bis);
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/*     */ 
/* 371 */           return null; } }, 500L);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 390 */     if ((image == null) || (image.isDisposed()))
/*     */     {
/* 392 */       DownloadManager dm = DataSourceUtils.getDM(datasource);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 397 */       String path = null;
/* 398 */       if (dm == null) {
/* 399 */         TOTorrentFile[] files = torrent.getFiles();
/* 400 */         if (files.length > 0) {
/* 401 */           path = files[0].getRelativePath();
/*     */         }
/*     */       } else {
/* 404 */         DiskManagerFileInfo primaryFile = dm.getDownloadState().getPrimaryFile();
/* 405 */         path = primaryFile == null ? null : primaryFile.getFile(true).getName();
/*     */       }
/* 407 */       if (path != null) {
/* 408 */         image = ImageRepository.getPathIcon(path, big, false);
/*     */         
/* 410 */         if ((image != null) && (!torrent.isSimpleTorrent())) {
/* 411 */           Image[] images = { image, ImageRepository.getPathIcon(new File(path).getParent(), false, false) };
/*     */           
/*     */ 
/*     */ 
/* 415 */           return images;
/*     */         }
/*     */       }
/*     */       
/* 419 */       if (image == null) {
/* 420 */         imageLoaderThumb.addImageNoDipose(id, ImageLoader.noImage);
/*     */       } else {
/* 422 */         imageLoaderThumb.addImageNoDipose(id, image);
/*     */       }
/*     */     }
/*     */     else {
/* 426 */       imageLoaderThumb.addImage(id, image);
/*     */     }
/*     */     
/* 429 */     l.contentImageLoaded(image, true);
/* 430 */     return new Image[] { image };
/*     */   }
/*     */   
/*     */   public static void releaseContentImage(Object datasource) {
/* 434 */     if (imageLoaderThumb == null) {
/* 435 */       return;
/*     */     }
/*     */     
/* 438 */     TOTorrent torrent = DataSourceUtils.getTorrent(datasource);
/* 439 */     if (torrent == null) {
/* 440 */       return;
/*     */     }
/*     */     
/* 443 */     String thumbnailUrl = com.aelitis.azureus.core.torrent.PlatformTorrentUtils.getContentThumbnailUrl(torrent);
/*     */     
/* 445 */     if (thumbnailUrl != null) {
/* 446 */       imageLoaderThumb.releaseImage(thumbnailUrl);
/*     */     } else {
/* 448 */       String hash = null;
/*     */       try {
/* 450 */         hash = torrent.getHashWrapper().toBase32String();
/*     */       }
/*     */       catch (TOTorrentException e) {}
/* 453 */       if (hash == null) {
/* 454 */         return;
/*     */       }
/*     */       
/* 457 */       String id = "Thumbnail." + hash + "." + torrent.getSize();
/*     */       
/* 459 */       imageLoaderThumb.releaseImage(id);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface ContentImageLoadedListener
/*     */   {
/*     */     public abstract void contentImageLoaded(Image paramImage, boolean paramBoolean);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/TorrentUIUtilsV3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */