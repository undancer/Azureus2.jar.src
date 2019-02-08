/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.dnd.DropTargetEvent;
/*     */ import org.eclipse.swt.dnd.FileTransfer;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenFileOptions;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.xml.rss.RSSUtils;
/*     */ import org.gudy.azureus2.ui.swt.FileDownloadWindow;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*     */ import org.gudy.azureus2.ui.swt.URLTransfer.URLType;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TorrentOpener
/*     */ {
/*     */   public static void openTorrent(String torrentFile)
/*     */   {
/*  92 */     openTorrent(torrentFile, new HashMap());
/*     */   }
/*     */   
/*     */   public static void openTorrent(String torrentFile, final Map<String, Object> options) {
/*  96 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  98 */         UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*  99 */         if (uif != null) {
/* 100 */           uif.openTorrentOpenOptions(null, null, new String[] { this.val$torrentFile }, options);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void openTorrentsForTracking(final String path, String[] fileNames)
/*     */   {
/* 111 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(final AzureusCore core) {
/* 113 */         Display display = SWTThread.getInstance().getDisplay();
/* 114 */         if ((display == null) || (display.isDisposed()) || (core == null)) {
/* 115 */           return;
/*     */         }
/* 117 */         new AEThread2("TorrentOpener")
/*     */         {
/*     */           public void run() {
/* 120 */             for (int i = 0; i < TorrentOpener.2.this.val$fileNames.length; i++) {
/*     */               try
/*     */               {
/* 123 */                 TOTorrent t = TorrentUtils.readFromFile(new File(TorrentOpener.2.this.val$path, TorrentOpener.2.this.val$fileNames[i]), true);
/*     */                 
/*     */ 
/* 126 */                 core.getTrackerHost().hostTorrent(t, true, true);
/*     */               }
/*     */               catch (Throwable e) {
/* 129 */                 Logger.log(new LogAlert(false, "Torrent open fails for '" + TorrentOpener.2.this.val$path + File.separator + TorrentOpener.2.this.val$fileNames[i] + "'", e));
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void openTorrentTrackingOnly()
/*     */   {
/* 143 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 145 */         Shell shell = Utils.findAnyShell();
/* 146 */         if (shell == null) {
/* 147 */           return;
/*     */         }
/* 149 */         FileDialog fDialog = new FileDialog(shell, 4098);
/* 150 */         fDialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/* 151 */         fDialog.setFilterExtensions(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*     */         
/* 153 */         fDialog.setFilterNames(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/* 154 */         fDialog.setText(MessageText.getString("MainWindow.dialog.choose.file"));
/* 155 */         String path = TorrentOpener.setFilterPathTorrent(fDialog.open());
/* 156 */         if (path == null) {
/* 157 */           return;
/*     */         }
/* 159 */         TorrentOpener.openTorrentsForTracking(path, fDialog.getFileNames());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void openTorrentSimple() {
/* 165 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 167 */         Shell shell = Utils.findAnyShell();
/* 168 */         if (shell == null) {
/* 169 */           return;
/*     */         }
/* 171 */         FileDialog fDialog = new FileDialog(shell, 4098);
/* 172 */         fDialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/* 173 */         fDialog.setFilterExtensions(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*     */         
/*     */ 
/*     */ 
/* 177 */         fDialog.setFilterNames(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*     */         
/*     */ 
/*     */ 
/* 181 */         fDialog.setText(MessageText.getString("MainWindow.dialog.choose.file"));
/* 182 */         String path = TorrentOpener.setFilterPathTorrent(fDialog.open());
/* 183 */         if (path == null) {
/* 184 */           return;
/*     */         }
/* 186 */         UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentOpenOptions(shell, path, fDialog.getFileNames(), false, false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void openDroppedTorrents(DropTargetEvent event, boolean deprecated_sharing_param)
/*     */   {
/* 193 */     Object data = event.data;
/*     */     
/* 195 */     if (data == null) {
/* 196 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 203 */     if ((data instanceof String)) {
/* 204 */       if (((String)data).contains("azcdid=" + RandomUtils.INSTANCE_ID)) {
/* 205 */         event.detail = 0;
/*     */       }
/*     */     }
/* 208 */     else if ((data instanceof URLTransfer.URLType))
/*     */     {
/* 210 */       String link = ((URLTransfer.URLType)data).linkURL;
/*     */       
/* 212 */       if ((link != null) && (link.contains("azcdid=" + RandomUtils.INSTANCE_ID))) {
/* 213 */         event.detail = 0;
/* 214 */         return;
/*     */       }
/*     */     }
/*     */     
/* 218 */     if (((event.data instanceof String[])) || ((event.data instanceof String))) {
/* 219 */       String[] sourceNames = { (event.data instanceof String[]) ? (String[])event.data : (String)event.data };
/*     */       
/*     */ 
/* 222 */       if (event.detail == 0) {
/* 223 */         return;
/*     */       }
/* 225 */       for (int i = 0; i < sourceNames.length; i++) {
/* 226 */         final File source = new File(sourceNames[i]);
/* 227 */         String sURL = UrlUtils.parseTextForURL(sourceNames[i], true);
/*     */         
/* 229 */         if ((sURL != null) && (!source.exists())) {
/* 230 */           UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 231 */           if (uif != null) {
/* 232 */             uif.openTorrentOpenOptions(null, null, new String[] { sURL }, false, false);
/*     */           }
/*     */         }
/* 235 */         else if (source.isFile())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 240 */           new AEThread2("asyncOpen", true)
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/* 245 */               String filename = source.getAbsolutePath();
/*     */               
/* 247 */               VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*     */               
/* 249 */               if (vfh.loadAndHandleVuzeFile(filename, 0) != null)
/*     */               {
/* 251 */                 return;
/*     */               }
/*     */               
/*     */ 
/* 255 */               UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 256 */               if (uif != null) {
/* 257 */                 uif.openTorrentOpenOptions(null, null, new String[] { filename }, false, false);
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           }.start();
/*     */         }
/* 264 */         else if (source.isDirectory())
/*     */         {
/* 266 */           String dir_name = source.getAbsolutePath();
/*     */           
/* 268 */           UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 269 */           if (uif != null) {
/* 270 */             uif.openTorrentOpenOptions(null, dir_name, null, false, false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 275 */     else if ((event.data instanceof URLTransfer.URLType)) {
/* 276 */       UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 277 */       if (uif != null) {
/* 278 */         uif.openTorrentOpenOptions(null, null, new String[] { ((URLTransfer.URLType)event.data).linkURL }, false, false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getFilterPathData()
/*     */   {
/* 287 */     String before = COConfigurationManager.getStringParameter("previous.filter.dir.data");
/* 288 */     if ((before != null) && (before.length() > 0)) {
/* 289 */       return before;
/*     */     }
/*     */     try
/*     */     {
/* 293 */       return COConfigurationManager.getDirectoryParameter("Default save path");
/*     */     }
/*     */     catch (IOException e) {}
/* 296 */     return "";
/*     */   }
/*     */   
/*     */   public static String getFilterPathTorrent()
/*     */   {
/* 301 */     String before = COConfigurationManager.getStringParameter("previous.filter.dir.torrent");
/* 302 */     if ((before != null) && (before.length() > 0)) {
/* 303 */       return before;
/*     */     }
/* 305 */     return COConfigurationManager.getStringParameter("General_sDefaultTorrent_Directory");
/*     */   }
/*     */   
/*     */   public static String setFilterPathData(String path) {
/* 309 */     if ((path != null) && (path.length() > 0)) {
/* 310 */       File test = new File(path);
/* 311 */       if (!test.isDirectory()) test = test.getParentFile();
/* 312 */       String now = "";
/* 313 */       if (test != null) now = test.getAbsolutePath();
/* 314 */       String before = COConfigurationManager.getStringParameter("previous.filter.dir.data");
/* 315 */       if ((before == null) || (before.length() == 0) || (!before.equals(now))) {
/* 316 */         COConfigurationManager.setParameter("previous.filter.dir.data", now);
/* 317 */         COConfigurationManager.save();
/*     */       }
/*     */     }
/* 320 */     return path;
/*     */   }
/*     */   
/*     */   public static String setFilterPathTorrent(String path) {
/* 324 */     if ((path != null) && (path.length() > 0)) {
/* 325 */       File test = new File(path);
/* 326 */       if (!test.isDirectory()) test = test.getParentFile();
/* 327 */       String now = "";
/* 328 */       if (test != null) now = test.getAbsolutePath();
/* 329 */       String before = COConfigurationManager.getStringParameter("previous.filter.dir.torrent");
/* 330 */       if ((before == null) || (before.length() == 0) || (!before.equals(now))) {
/* 331 */         COConfigurationManager.setParameter("previous.filter.dir.torrent", now);
/* 332 */         COConfigurationManager.save();
/*     */       }
/* 334 */       return now;
/*     */     }
/* 336 */     return path;
/*     */   }
/*     */   
/*     */   public static boolean doesDropHaveTorrents(DropTargetEvent event)
/*     */   {
/* 341 */     boolean isTorrent = false;
/* 342 */     if ((event.data == null) && (event.currentDataType != null)) {
/* 343 */       FileTransfer ft = FileTransfer.getInstance();
/* 344 */       if (ft.isSupportedType(event.currentDataType)) {
/* 345 */         Object data = ft.nativeToJava(event.currentDataType);
/* 346 */         String[] fileList = (data instanceof String) ? new String[] { (String)data } : (String[])data;
/*     */         
/*     */ 
/* 349 */         if (fileList != null) {
/* 350 */           for (String file : fileList) {
/* 351 */             if ((file.endsWith(".torrent")) || (file.endsWith(".vuze"))) {
/* 352 */               isTorrent = true;
/* 353 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       } else {
/* 358 */         Object object = URLTransfer.getInstance().nativeToJava(event.currentDataType);
/*     */         
/* 360 */         if ((object instanceof URLTransfer.URLType))
/*     */         {
/* 362 */           URLTransfer.URLType xfer = (URLTransfer.URLType)object;
/*     */           
/* 364 */           String link = xfer.linkURL;
/*     */           
/* 366 */           if ((link == null) || (!link.contains("azcdid=" + RandomUtils.INSTANCE_ID)))
/*     */           {
/* 368 */             isTorrent = true;
/*     */           }
/*     */         }
/*     */       }
/* 372 */     } else if ((!(event.data instanceof String)) || (!((String)event.data).contains("azcdid=" + RandomUtils.INSTANCE_ID)))
/*     */     {
/*     */ 
/*     */ 
/* 376 */       if (((event.data instanceof String[])) || ((event.data instanceof String))) {
/* 377 */         String[] sourceNames = { (event.data instanceof String[]) ? (String[])event.data : (String)event.data };
/*     */         
/*     */ 
/*     */ 
/* 381 */         for (String name : sourceNames) {
/* 382 */           String sURL = UrlUtils.parseTextForURL(name, true);
/* 383 */           if (sURL != null) {
/* 384 */             isTorrent = true;
/* 385 */             break;
/*     */           }
/*     */         }
/* 388 */       } else if ((event.data instanceof URLTransfer.URLType))
/*     */       {
/* 390 */         URLTransfer.URLType xfer = (URLTransfer.URLType)event.data;
/*     */         
/* 392 */         String link = xfer.linkURL;
/*     */         
/* 394 */         if ((link == null) || (!link.contains("azcdid=" + RandomUtils.INSTANCE_ID)))
/*     */         {
/* 396 */           isTorrent = true; }
/*     */       }
/*     */     }
/* 399 */     return isTorrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final boolean addTorrent(TorrentOpenOptions torrentOptions)
/*     */   {
/*     */     try
/*     */     {
/* 412 */       if (torrentOptions.getTorrent() == null) {
/* 413 */         return false;
/*     */       }
/*     */       
/* 416 */       final DownloadManagerInitialisationAdapter dmia = new DownloadManagerInitialisationAdapter()
/*     */       {
/*     */ 
/*     */         public int getActions()
/*     */         {
/* 421 */           return 1;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void initialised(DownloadManager dm, boolean for_seeding)
/*     */         {
/* 429 */           DiskManagerFileInfoSet file_info_set = dm.getDiskManagerFileInfoSet();
/*     */           
/* 431 */           DiskManagerFileInfo[] fileInfos = file_info_set.getFiles();
/*     */           
/* 433 */           boolean reorder_mode = COConfigurationManager.getBooleanParameter("Enable reorder storage mode");
/* 434 */           int reorder_mode_min_mb = COConfigurationManager.getIntParameter("Reorder storage mode min MB");
/*     */           try
/*     */           {
/* 437 */             dm.getDownloadState().suppressStateSave(true);
/*     */             
/* 439 */             boolean[] toSkip = new boolean[fileInfos.length];
/* 440 */             boolean[] toCompact = new boolean[fileInfos.length];
/* 441 */             boolean[] toReorderCompact = new boolean[fileInfos.length];
/*     */             
/* 443 */             int[] priorities = null;
/*     */             
/* 445 */             int comp_num = 0;
/* 446 */             int reorder_comp_num = 0;
/*     */             
/* 448 */             TorrentOpenFileOptions[] files = this.val$torrentOptions.getFiles();
/*     */             
/* 450 */             for (int iIndex = 0; iIndex < fileInfos.length; iIndex++) {
/* 451 */               DiskManagerFileInfo fileInfo = fileInfos[iIndex];
/* 452 */               if ((iIndex >= 0) && (iIndex < files.length) && (files[iIndex].lSize == fileInfo.getLength()))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/* 457 */                 File fDest = fileInfo.getFile(true);
/* 458 */                 if (files[iIndex].isLinked())
/*     */                 {
/* 460 */                   fDest = files[iIndex].getDestFileFullName();
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/* 465 */                   dm.getDownloadState().setFileLink(iIndex, fileInfo.getFile(false), fDest);
/*     */                 }
/*     */                 
/*     */ 
/* 469 */                 if (files[iIndex].isToDownload())
/*     */                 {
/* 471 */                   int priority = files[iIndex].getPriority();
/*     */                   
/* 473 */                   if (priority != 0)
/*     */                   {
/* 475 */                     if (priorities == null)
/*     */                     {
/* 477 */                       priorities = new int[fileInfos.length];
/*     */                     }
/*     */                     
/* 480 */                     priorities[iIndex] = priority;
/*     */                   }
/*     */                 } else {
/* 483 */                   toSkip[iIndex] = true;
/*     */                   
/* 485 */                   if (!fDest.exists())
/*     */                   {
/* 487 */                     if ((reorder_mode) && (fileInfo.getLength() / 1048576L >= reorder_mode_min_mb))
/*     */                     {
/*     */ 
/* 490 */                       toReorderCompact[iIndex] = true;
/*     */                       
/* 492 */                       reorder_comp_num++;
/*     */                     }
/*     */                     else
/*     */                     {
/* 496 */                       toCompact[iIndex] = true;
/*     */                       
/* 498 */                       comp_num++;
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 508 */             if (files.length == 1)
/*     */             {
/* 510 */               TorrentOpenFileOptions file = files[0];
/*     */               
/* 512 */               if (file.isManualRename())
/*     */               {
/* 514 */                 String fileRename = file.getDestFileName();
/*     */                 
/* 516 */                 if ((fileRename != null) && (fileRename.length() > 0))
/*     */                 {
/* 518 */                   dm.getDownloadState().setDisplayName(fileRename);
/*     */                 }
/*     */               }
/*     */             }
/*     */             else {
/* 523 */               String folderRename = this.val$torrentOptions.getManualRename();
/*     */               
/* 525 */               if ((folderRename != null) && (folderRename.length() > 0))
/*     */               {
/*     */ 
/* 528 */                 dm.getDownloadState().setDisplayName(folderRename);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 533 */             if (comp_num > 0)
/*     */             {
/* 535 */               file_info_set.setStorageTypes(toCompact, 2);
/*     */             }
/*     */             
/*     */ 
/* 539 */             if (reorder_comp_num > 0)
/*     */             {
/* 541 */               file_info_set.setStorageTypes(toReorderCompact, 4);
/*     */             }
/*     */             
/*     */ 
/* 545 */             file_info_set.setSkipped(toSkip, true);
/*     */             
/* 547 */             if (priorities != null)
/*     */             {
/* 549 */               file_info_set.setPriority(priorities);
/*     */             }
/*     */             
/* 552 */             int maxUp = this.val$torrentOptions.getMaxUploadSpeed();
/*     */             
/* 554 */             int kInB = DisplayFormatters.getKinB();
/*     */             
/* 556 */             if (maxUp > 0) {
/* 557 */               dm.getStats().setUploadRateLimitBytesPerSecond(maxUp * kInB);
/*     */             }
/*     */             
/* 560 */             int maxDown = this.val$torrentOptions.getMaxDownloadSpeed();
/*     */             
/* 562 */             if (maxDown > 0) {
/* 563 */               dm.getStats().setDownloadRateLimitBytesPerSecond(maxDown * kInB);
/*     */             }
/*     */             
/* 566 */             DownloadManagerState dm_state = dm.getDownloadState();
/*     */             
/* 568 */             if (this.val$torrentOptions.disableIPFilter)
/*     */             {
/* 570 */               dm_state.setFlag(256L, true);
/*     */             }
/*     */             
/*     */ 
/* 574 */             if (this.val$torrentOptions.peerSource != null) {
/* 575 */               for (String peerSource : this.val$torrentOptions.peerSource.keySet()) {
/* 576 */                 boolean enable = ((Boolean)this.val$torrentOptions.peerSource.get(peerSource)).booleanValue();
/* 577 */                 dm_state.setPeerSourceEnabled(peerSource, enable);
/*     */               }
/*     */             }
/*     */             
/* 581 */             Map<String, Boolean> enabledNetworks = this.val$torrentOptions.getEnabledNetworks();
/*     */             
/* 583 */             if (enabledNetworks != null)
/*     */             {
/* 585 */               if (!dm_state.getFlag(4096L))
/*     */               {
/* 587 */                 for (String net : enabledNetworks.keySet()) {
/* 588 */                   boolean enable = ((Boolean)enabledNetworks.get(net)).booleanValue();
/* 589 */                   dm_state.setNetworkEnabled(net, enable);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 594 */             List<Tag> initialTags = this.val$torrentOptions.getInitialTags();
/*     */             
/* 596 */             for (Tag t : initialTags)
/*     */             {
/* 598 */               t.addTaggable(dm);
/*     */             }
/*     */             
/* 601 */             List<List<String>> trackers = this.val$torrentOptions.getTrackers(true);
/*     */             
/* 603 */             if (trackers != null)
/*     */             {
/* 605 */               TOTorrent torrent = dm.getTorrent();
/*     */               
/* 607 */               TorrentUtils.listToAnnounceGroups(trackers, torrent);
/*     */               
/*     */               try
/*     */               {
/* 611 */                 TorrentUtils.writeToFile(torrent);
/*     */               }
/*     */               catch (Throwable e2)
/*     */               {
/* 615 */                 Debug.printStackTrace(e2);
/*     */               }
/*     */             }
/*     */           }
/*     */           finally {
/* 620 */             dm.getDownloadState().suppressStateSave(false);
/*     */           }
/*     */           
/*     */         }
/* 624 */       };
/* 625 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */         public void azureusCoreRunning(AzureusCore core) {
/* 627 */           TOTorrent torrent = this.val$torrentOptions.getTorrent();
/* 628 */           byte[] hash = null;
/*     */           try {
/* 630 */             hash = torrent.getHash();
/*     */           }
/*     */           catch (TOTorrentException e1) {}
/*     */           
/* 634 */           int iStartState = this.val$torrentOptions.getStartMode() == 1 ? 70 : 75;
/*     */           
/*     */ 
/* 637 */           GlobalManager gm = core.getGlobalManager();
/*     */           
/* 639 */           DownloadManager dm = gm.addDownloadManager(this.val$torrentOptions.sFileName, hash, this.val$torrentOptions.getParentDir(), this.val$torrentOptions.getSubDir(), iStartState, true, this.val$torrentOptions.getStartMode() == 3, dmia);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 648 */           if (dm == null) {
/* 649 */             return;
/*     */           }
/*     */           
/* 652 */           if (this.val$torrentOptions.iQueueLocation == 0) {
/* 653 */             gm.moveTop(new DownloadManager[] { dm });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 658 */           if (this.val$torrentOptions.getStartMode() == 2) {
/* 659 */             dm.setForceStart(true);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 666 */       UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 667 */       if (uif != null) {
/* 668 */         uif.showErrorMessage("OpenTorrentWindow.mb.openError", Debug.getStackTrace(e), new String[] { torrentOptions.sOriginatingLocation, e.getMessage() });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 674 */       return false;
/*     */     }
/* 676 */     return true;
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
/*     */   public static boolean mergeFileIntoTorrentInfo(String sFileName, String sOriginatingLocation, TorrentOpenOptions torrentOptions)
/*     */   {
/* 691 */     TOTorrent torrent = null;
/*     */     
/* 693 */     boolean bDeleteFileOnCancel = false;
/*     */     
/*     */     File torrentFile;
/*     */     try
/*     */     {
/* 698 */       if (sFileName.startsWith("file://localhost/")) {
/* 699 */         sFileName = UrlUtils.decode(sFileName.substring(16));
/*     */       }
/*     */       
/* 702 */       File fOriginal = new File(sFileName);
/*     */       
/* 704 */       if ((!fOriginal.isFile()) || (!fOriginal.exists())) {
/* 705 */         UIFunctionsManager.getUIFunctions().showErrorMessage("OpenTorrentWindow.mb.openError", fOriginal.toString(), new String[] { UrlUtils.decode(sOriginatingLocation), "Not a File" });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 711 */         return false;
/*     */       }
/*     */       
/* 714 */       if (fOriginal.length() > 67108864L) {
/* 715 */         UIFunctionsManager.getUIFunctions().showErrorMessage("OpenTorrentWindow.mb.openError", fOriginal.toString(), new String[] { UrlUtils.decode(sOriginatingLocation), "Too large to be a torrent" });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 721 */         return false;
/*     */       }
/*     */       
/* 724 */       torrentFile = TorrentUtils.copyTorrentFileToSaveDir(fOriginal, true);
/* 725 */       bDeleteFileOnCancel = !fOriginal.equals(torrentFile);
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (IOException e1)
/*     */     {
/*     */ 
/*     */ 
/* 734 */       Debug.out(e1);
/*     */       
/* 736 */       torrentFile = new File(sFileName);
/*     */     }
/*     */     
/* 739 */     VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*     */     
/* 741 */     VuzeFile vf = vfh.loadVuzeFile(torrentFile);
/*     */     
/* 743 */     if (vf != null)
/*     */     {
/* 745 */       vfh.handleFiles(new VuzeFile[] { vf }, 0);
/*     */       
/*     */ 
/*     */ 
/* 749 */       return false;
/*     */     }
/*     */     
/* 752 */     if (RSSUtils.isRSSFeed(torrentFile))
/*     */     {
/* 754 */       boolean done = false;
/*     */       try
/*     */       {
/* 757 */         URL url = new URL(sOriginatingLocation);
/*     */         
/* 759 */         UIManager ui_manager = StaticUtilities.getUIManager(10000L);
/*     */         
/* 761 */         if (ui_manager != null)
/*     */         {
/* 763 */           String details = MessageText.getString("subscription.request.add.message", new String[] { sOriginatingLocation });
/*     */           
/*     */ 
/*     */ 
/* 767 */           long res = ui_manager.showMessageBox("subscription.request.add.title", "!" + details + "!", 12L);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 772 */           if (res == 4L)
/*     */           {
/* 774 */             SubscriptionManager sm = PluginInitializer.getDefaultInterface().getUtilities().getSubscriptionManager();
/*     */             
/* 776 */             sm.requestSubscription(url);
/*     */             
/* 778 */             done = true;
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 783 */         Debug.out(e);
/*     */       }
/*     */       
/*     */ 
/* 787 */       if (done) {
/* 788 */         if (bDeleteFileOnCancel) {
/* 789 */           torrentFile.delete();
/*     */         }
/* 791 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 795 */     if (!TorrentUtil.isFileTorrent(sOriginatingLocation, torrentFile, torrentFile.getName(), !torrentOptions.getHideErrors())) {
/* 796 */       if (bDeleteFileOnCancel) {
/* 797 */         torrentFile.delete();
/*     */       }
/* 799 */       return false;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 804 */       torrent = TorrentUtils.readFromFile(torrentFile, false);
/*     */     }
/*     */     catch (TOTorrentException e) {
/* 807 */       UIFunctionsManager.getUIFunctions().showErrorMessage("OpenTorrentWindow.mb.openError", Debug.getStackTrace(e), new String[] { sOriginatingLocation, e.getMessage() });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 814 */       if (bDeleteFileOnCancel) {
/* 815 */         torrentFile.delete();
/*     */       }
/* 817 */       return false;
/*     */     }
/*     */     
/* 820 */     torrentOptions.bDeleteFileOnCancel = bDeleteFileOnCancel;
/* 821 */     torrentOptions.sFileName = torrentFile.getAbsolutePath();
/* 822 */     torrentOptions.setTorrent(torrent);
/* 823 */     torrentOptions.sOriginatingLocation = sOriginatingLocation;
/*     */     
/* 825 */     return torrentOptions.getTorrent() != null;
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
/*     */   public static void openTorrentsFromStrings(TorrentOpenOptions optionsToClone, Shell parent, String pathPrefix, String[] torrents, String referrer, TorrentDownloaderCallBackInterface listener, boolean forceTorrentOptionsWindow)
/*     */   {
/* 843 */     if ((torrents == null) || (torrents.length == 0)) {
/* 844 */       if (pathPrefix == null) {
/* 845 */         return;
/*     */       }
/* 847 */       File path = new File(pathPrefix);
/* 848 */       if (!path.isDirectory()) {
/* 849 */         return;
/*     */       }
/*     */       
/* 852 */       List<String> newTorrents = new ArrayList();
/* 853 */       File[] listFiles = path.listFiles();
/* 854 */       for (File file : listFiles) {
/*     */         try {
/* 856 */           if ((file.isFile()) && (TorrentUtils.isTorrentFile(file.getAbsolutePath()))) {
/* 857 */             newTorrents.add(file.getName());
/*     */           }
/*     */         }
/*     */         catch (FileNotFoundException e) {}catch (IOException e) {}
/*     */       }
/*     */       
/*     */ 
/* 864 */       if (newTorrents.size() == 0) {
/* 865 */         return;
/*     */       }
/*     */       
/* 868 */       torrents = (String[])newTorrents.toArray(new String[0]);
/*     */     }
/*     */     
/*     */ 
/* 872 */     final VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/* 873 */     List<VuzeFile> vuze_files = new ArrayList();
/*     */     
/* 875 */     for (String line : torrents) {
/* 876 */       line = line.trim();
/* 877 */       if ((line.startsWith("\"")) && (line.endsWith("\""))) {
/* 878 */         if (line.length() < 3) {
/* 879 */           line = "";
/*     */         } else {
/* 881 */           line = line.substring(1, line.length() - 2);
/*     */         }
/*     */       }
/*     */       
/* 885 */       TorrentOpenOptions torrentOptions = optionsToClone == null ? new TorrentOpenOptions() : new TorrentOpenOptions(optionsToClone);
/*     */       
/*     */ 
/* 888 */       File file = pathPrefix == null ? new File(line) : new File(pathPrefix, line);
/*     */       
/* 890 */       if (file.exists())
/*     */       {
/*     */         try {
/* 893 */           VuzeFile vf = vfh.loadVuzeFile(file);
/*     */           
/* 895 */           if (vf != null) {
/* 896 */             vuze_files.add(vf);
/* 897 */             continue;
/*     */           }
/*     */         } catch (Throwable e) {
/* 900 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/* 903 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */         
/* 905 */         if (mergeFileIntoTorrentInfo(file.getAbsolutePath(), null, torrentOptions))
/*     */         {
/* 907 */           uif.addTorrentWithOptions(forceTorrentOptionsWindow, torrentOptions);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 912 */         final String url = UrlUtils.parseTextForURL(line, true);
/* 913 */         if (url != null)
/*     */         {
/*     */ 
/* 916 */           if (url.endsWith(".vuze")) {
/* 917 */             new AEThread2("VuzeLoader") {
/*     */               public void run() {
/*     */                 try {
/* 920 */                   VuzeFile vf = vfh.loadVuzeFile(url);
/* 921 */                   if (vf != null) {
/* 922 */                     vfh.handleFiles(new VuzeFile[] { vf }, 0);
/*     */                   }
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 927 */                   Debug.printStackTrace(e);
/*     */                 }
/*     */                 
/*     */               }
/*     */               
/*     */ 
/*     */             }.start();
/*     */           } else {
/* 935 */             new FileDownloadWindow(parent, url, referrer, null, torrentOptions, listener);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 940 */     if (vuze_files.size() > 0) {
/* 941 */       VuzeFile[] vfs = new VuzeFile[vuze_files.size()];
/* 942 */       vuze_files.toArray(vfs);
/* 943 */       vfh.handleFiles(vfs, 0);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/TorrentOpener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */