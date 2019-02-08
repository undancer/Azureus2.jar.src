/*     */ package com.aelitis.azureus.ui.swt.views.skin;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.download.DownloadManagerEnhancer;
/*     */ import com.aelitis.azureus.core.download.EnhancedDownloadManager;
/*     */ import com.aelitis.azureus.core.download.StreamManager;
/*     */ import com.aelitis.azureus.core.download.StreamManagerDownload;
/*     */ import com.aelitis.azureus.core.download.StreamManagerDownloadListener;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.core.util.LaunchManager;
/*     */ import com.aelitis.azureus.core.util.LaunchManager.LaunchAction;
/*     */ import com.aelitis.azureus.core.util.LaunchManager.LaunchTarget;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.DownloadUrlInfo;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.player.PlayerInstallWindow;
/*     */ import com.aelitis.azureus.ui.swt.player.PlayerInstaller;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.utils.TorrentUIUtilsV3;
/*     */ import com.aelitis.azureus.util.DataSourceUtils;
/*     */ import com.aelitis.azureus.util.PlayUtils;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.ForceRecheckListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TorrentListViewsUtils
/*     */ {
/*     */   private static StreamManagerDownload current_stream;
/*     */   private static TextViewerWindow stream_viewer;
/*     */   private static boolean emp_installing;
/*     */   
/*     */   public static void playOrStreamDataSource(Object ds, boolean launch_already_checked)
/*     */   {
/*  81 */     String referal = "unknown";
/*  82 */     if ((ds instanceof VuzeActivitiesEntry)) {
/*  83 */       referal = "playdashboardactivity";
/*  84 */     } else if ((ds instanceof DownloadManager)) {
/*  85 */       referal = "playdownloadmanager";
/*  86 */     } else if ((ds instanceof ISelectedContent)) {
/*  87 */       referal = "selectedcontent";
/*     */     }
/*  89 */     playOrStreamDataSource(ds, referal, launch_already_checked, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void playOrStreamDataSource(Object ds, String referal, boolean launch_already_checked, boolean complete_only)
/*     */   {
/*  95 */     org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = DataSourceUtils.getFileInfo(ds);
/*  96 */     if (fileInfo != null) {
/*  97 */       playOrStream(fileInfo.getDownloadManager(), fileInfo.getIndex(), complete_only, launch_already_checked, referal);
/*     */     }
/*     */     else
/*     */     {
/* 101 */       DownloadManager dm = DataSourceUtils.getDM(ds);
/* 102 */       if (dm == null) {
/* 103 */         downloadDataSource(ds, true, referal);
/*     */       } else {
/* 105 */         playOrStream(dm, -1, complete_only, launch_already_checked, referal);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void downloadDataSource(Object ds, boolean playNow, String referal)
/*     */   {
/* 112 */     TOTorrent torrent = DataSourceUtils.getTorrent(ds);
/*     */     
/* 114 */     if (torrent != null) {
/*     */       try
/*     */       {
/* 117 */         Map torrent_map = torrent.serialiseToMap();
/*     */         
/* 119 */         torrent_map.remove("info");
/*     */         
/* 121 */         VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(torrent_map);
/*     */         
/* 123 */         if (vf != null)
/*     */         {
/* 125 */           VuzeFileHandler.getSingleton().handleFiles(new VuzeFile[] { vf }, 0);
/*     */           
/* 127 */           return;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 136 */     if ((torrent != null) && (!DataSourceUtils.isPlatformContent(ds))) {
/* 137 */       TorrentUIUtilsV3.addTorrentToGM(torrent);
/*     */     } else {
/* 139 */       DownloadUrlInfo dlInfo = DataSourceUtils.getDownloadInfo(ds);
/* 140 */       if (dlInfo != null) {
/* 141 */         TorrentUIUtilsV3.loadTorrent(dlInfo, playNow, false, true);
/* 142 */         return;
/*     */       }
/*     */       
/* 145 */       String hash = DataSourceUtils.getHash(ds);
/* 146 */       if (hash != null) {
/* 147 */         dlInfo = new DownloadUrlInfo(UrlUtils.parseTextForMagnets(hash));
/* 148 */         dlInfo.setReferer(referal);
/* 149 */         TorrentUIUtilsV3.loadTorrent(dlInfo, playNow, false, true);
/* 150 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void playOrStream(DownloadManager dm, int file_index, boolean complete_only, boolean launch_already_checked)
/*     */   {
/* 159 */     playOrStream(dm, file_index, complete_only, launch_already_checked, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void playOrStream(DownloadManager dm, final int file_index, final boolean complete_only, boolean launch_already_checked, final String referal)
/*     */   {
/* 167 */     if (dm == null) {
/* 168 */       return;
/*     */     }
/*     */     
/* 171 */     if (launch_already_checked)
/*     */     {
/* 173 */       _playOrStream(dm, file_index, complete_only, referal);
/*     */     }
/*     */     else
/*     */     {
/* 177 */       LaunchManager launch_manager = LaunchManager.getManager();
/*     */       
/* 179 */       LaunchManager.LaunchTarget target = launch_manager.createTarget(dm);
/*     */       
/* 181 */       launch_manager.launchRequest(target, new LaunchManager.LaunchAction()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void actionAllowed()
/*     */         {
/*     */ 
/* 188 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 194 */               TorrentListViewsUtils._playOrStream(TorrentListViewsUtils.1.this.val$dm, TorrentListViewsUtils.1.this.val$file_index, TorrentListViewsUtils.1.this.val$complete_only, TorrentListViewsUtils.1.this.val$referal);
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void actionDenied(Throwable reason)
/*     */         {
/* 203 */           Debug.out("Launch request denied", reason);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void _playOrStream(final DownloadManager dm, int file_index, final boolean complete_only, final String referal)
/*     */   {
/* 212 */     if (dm == null) {
/* 213 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 220 */     TOTorrent torrent = dm.getTorrent();
/* 221 */     if (torrent == null) {
/* 222 */       return;
/*     */     }
/*     */     
/* 225 */     if (file_index == -1) {
/* 226 */       final int[] playableFileIndexes = PlayUtils.getExternallyPlayableFileIndexes(PluginCoreUtils.wrap(dm), complete_only);
/*     */       
/* 228 */       if (playableFileIndexes.length == 1)
/*     */       {
/* 230 */         int open_result = openInEMP(dm, file_index, complete_only, referal);
/*     */         
/* 232 */         if (open_result == 0) {
/* 233 */           PlatformTorrentUtils.setHasBeenOpened(dm, true);
/*     */         }
/* 235 */       } else if (playableFileIndexes.length > 1) {
/* 236 */         VuzeMessageBox mb = new VuzeMessageBox(MessageText.getString("ConfigView.option.dm.dblclick.play"), null, new String[] { MessageText.getString("iconBar.play"), MessageText.getString("Button.cancel") }, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 241 */         final Map<Integer, Integer> mapPositionToFileInfo = new HashMap();
/* 242 */         int[] selectedIndex = { 0 };
/*     */         
/*     */ 
/*     */ 
/* 246 */         mb.setSubTitle(MessageText.getString("play.select.content"));
/* 247 */         mb.setListener(new VuzeMessageBoxListener()
/*     */         {
/*     */           public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 250 */             SWTSkin skin = soExtra.getSkin();
/*     */             
/* 252 */             Composite composite = soExtra.getComposite();
/* 253 */             final Table table = new Table(composite, 66308);
/*     */             
/* 255 */             table.setBackground(composite.getBackground());
/* 256 */             table.addSelectionListener(new SelectionListener()
/*     */             {
/*     */               public void widgetSelected(SelectionEvent e) {
/* 259 */                 TorrentListViewsUtils.2.this.val$selectedIndex[0] = table.getSelectionIndex();
/*     */               }
/*     */               
/*     */ 
/*     */               public void widgetDefaultSelected(SelectionEvent e) {}
/* 264 */             });
/* 265 */             FormData formData = Utils.getFilledFormData();
/* 266 */             formData.bottom.offset = -20;
/* 267 */             Utils.setLayoutData(table, formData);
/* 268 */             table.setHeaderVisible(false);
/* 269 */             table.addListener(41, new Listener() {
/*     */               public void handleEvent(Event event) {
/* 271 */                 int w = table.getClientArea().width - 5;
/* 272 */                 if (w == 0) {
/* 273 */                   return;
/*     */                 }
/* 275 */                 if (event.width < w) {
/* 276 */                   event.width = w;
/*     */                 }
/*     */                 
/*     */               }
/* 280 */             });
/* 281 */             String prefix = dm.getSaveLocation().toString();
/* 282 */             int i = 0;
/* 283 */             org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] fileInfos = dm.getDiskManagerFileInfoSet().getFiles();
/* 284 */             for (int fileIndex : playableFileIndexes) {
/* 285 */               if ((fileIndex >= 0) && (fileIndex < fileInfos.length))
/*     */               {
/*     */ 
/* 288 */                 File f = fileInfos[fileIndex].getFile(true);
/* 289 */                 String path = f.getParent();
/* 290 */                 if (path.startsWith(prefix)) {
/* 291 */                   path = path.length() > prefix.length() ? path.substring(prefix.length() + 1) : "";
/*     */                 }
/* 293 */                 String s = f.getName();
/* 294 */                 if (path.length() > 0) {
/* 295 */                   s = s + " in " + path;
/*     */                 }
/* 297 */                 TableItem item = new TableItem(table, 0);
/* 298 */                 item.setText(s);
/* 299 */                 mapPositionToFileInfo.put(Integer.valueOf(i++), Integer.valueOf(fileIndex));
/*     */               }
/*     */             }
/* 302 */             Image alphaImage = Utils.createAlphaImage(table.getDisplay(), 1, 25, (byte)-1);
/* 303 */             TableItem item = table.getItem(0);
/* 304 */             item.setImage(alphaImage);
/* 305 */             item.setImage((Image)null);
/* 306 */             alphaImage.dispose();
/*     */             
/*     */ 
/* 309 */             table.setSelection(0);
/*     */           }
/* 311 */         });
/* 312 */         mb.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int result) {
/* 314 */             if ((result != 0) || (this.val$selectedIndex[0] < 0)) {
/* 315 */               return;
/*     */             }
/* 317 */             Integer file_index = (Integer)mapPositionToFileInfo.get(Integer.valueOf(this.val$selectedIndex[0]));
/*     */             
/* 319 */             if (file_index != null) {
/* 320 */               int open_result = TorrentListViewsUtils.openInEMP(dm, file_index.intValue(), complete_only, referal);
/*     */               
/* 322 */               if (open_result == 0) {
/* 323 */                 PlatformTorrentUtils.setHasBeenOpened(dm, true);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/* 329 */       return;
/*     */     }
/*     */     
/* 332 */     if (PlayUtils.canUseEMP(torrent, file_index, complete_only)) {
/* 333 */       debug("Can use EMP");
/*     */       
/* 335 */       int open_result = openInEMP(dm, file_index, complete_only, referal);
/*     */       
/* 337 */       if (open_result == 0) {
/* 338 */         PlatformTorrentUtils.setHasBeenOpened(dm, true);
/* 339 */         return; }
/* 340 */       if (open_result == 2) {
/* 341 */         debug("Open in EMP abandoned");
/* 342 */         return;
/*     */       }
/* 344 */       debug("Open EMP Failed");
/*     */     }
/*     */     else
/*     */     {
/* 348 */       debug("Can't use EMP.");
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
/*     */   private static void debug(String string)
/*     */   {
/* 363 */     if (Constants.isCVSVersion()) {
/* 364 */       System.out.println(string);
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
/*     */   private static int installEMP(String name, final Runnable target)
/*     */   {
/* 382 */     synchronized (TorrentListViewsUtils.class)
/*     */     {
/* 384 */       if (emp_installing)
/*     */       {
/* 386 */         Debug.out("EMP is already being installed, secondary launch for " + name + " ignored");
/*     */         
/* 388 */         return 2;
/*     */       }
/*     */       
/* 391 */       emp_installing = true;
/*     */     }
/*     */     
/* 394 */     boolean running = false;
/*     */     try
/*     */     {
/* 397 */       final PlayerInstaller installer = new PlayerInstaller();
/*     */       
/* 399 */       window = new PlayerInstallWindow(installer);
/*     */       
/* 401 */       window.open();
/*     */       
/* 403 */       AEThread2 installerThread = new AEThread2("player installer", true)
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try {
/* 408 */             if (installer.install()) {
/* 409 */               Utils.execSWTThread(new AERunnable()
/*     */               {
/*     */                 public void runSupport() {
/* 412 */                   TorrentListViewsUtils.4.this.val$target.run();
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 419 */             synchronized (TorrentListViewsUtils.class)
/*     */             {
/* 421 */               TorrentListViewsUtils.access$202(false);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 426 */       };
/* 427 */       installerThread.start();
/*     */       
/* 429 */       running = true;
/*     */       
/* 431 */       return 0;
/*     */     }
/*     */     catch (Throwable e) {
/*     */       PlayerInstallWindow window;
/* 435 */       Debug.out(e);
/*     */       
/* 437 */       return 1;
/*     */     }
/*     */     finally
/*     */     {
/* 441 */       if (!running)
/*     */       {
/* 443 */         synchronized (TorrentListViewsUtils.class)
/*     */         {
/* 445 */           emp_installing = false;
/*     */         }
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
/*     */   private static int openInEMP(final DownloadManager dm, int _file_index, final boolean complete_only, final String referal)
/*     */   {
/*     */     try
/*     */     {
/* 466 */       int file_index = -1;
/*     */       
/* 468 */       if (_file_index == -1)
/*     */       {
/* 470 */         EnhancedDownloadManager edm = DownloadManagerEnhancer.getSingleton().getEnhancedDownload(dm);
/*     */         
/* 472 */         if (edm != null) {
/* 473 */           file_index = edm.getPrimaryFileIndex();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 478 */         file_index = _file_index;
/*     */       }
/*     */       
/* 481 */       if (file_index == -1)
/*     */       {
/* 483 */         return 1;
/*     */       }
/*     */       
/* 486 */       final int f_file_index = file_index;
/*     */       
/* 488 */       org.gudy.azureus2.plugins.disk.DiskManagerFileInfo file = PluginCoreUtils.wrap(dm).getDiskManagerFileInfo()[file_index];
/*     */       
/*     */       URL url;
/*     */       final URL url;
/* 492 */       if ((!complete_only) && (file.getDownloaded() != file.getLength()))
/*     */       {
/* 494 */         url = PlayUtils.getMediaServerContentURL(file);
/*     */       }
/*     */       else
/*     */       {
/* 498 */         url = null;
/*     */       }
/*     */       
/* 501 */       if (url != null)
/*     */       {
/* 503 */         if (PlayUtils.isStreamPermitted())
/*     */         {
/* 505 */           boolean show_debug_window = false;
/*     */           
/* 507 */           new AEThread2("stream:async")
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/* 512 */               StreamManager sm = StreamManager.getSingleton();
/*     */               
/* 514 */               synchronized (TorrentListViewsUtils.class)
/*     */               {
/* 516 */                 if ((TorrentListViewsUtils.current_stream != null) && (!TorrentListViewsUtils.current_stream.isCancelled()))
/*     */                 {
/* 518 */                   if (TorrentListViewsUtils.current_stream.getURL().equals(url))
/*     */                   {
/* 520 */                     TorrentListViewsUtils.current_stream.setPreviewMode(!TorrentListViewsUtils.current_stream.getPreviewMode());
/*     */                     
/* 522 */                     return;
/*     */                   }
/*     */                   
/* 525 */                   TorrentListViewsUtils.current_stream.cancel();
/*     */                   
/* 527 */                   TorrentListViewsUtils.access$302(null);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 567 */                 TorrentListViewsUtils.access$302(sm.stream(dm, f_file_index, url, false, new StreamManagerDownloadListener()
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/* 572 */                   private long last_log = 0L;
/*     */                   
/*     */ 
/*     */ 
/*     */                   public void updateActivity(String str)
/*     */                   {
/* 578 */                     append("Activity: " + str);
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                   public void updateStats(int secs_until_playable, int buffer_secs, long buffer_bytes, int target_secs)
/*     */                   {
/* 588 */                     long now = SystemTime.getMonotonousTime();
/*     */                     
/* 590 */                     if (now - this.last_log >= 1000L)
/*     */                     {
/* 592 */                       this.last_log = now;
/*     */                       
/* 594 */                       append("stats: play in " + secs_until_playable + " sec, buffer=" + DisplayFormatters.formatByteCountToKiBEtc(buffer_bytes) + "/" + buffer_secs + " sec - target=" + target_secs + " sec");
/*     */                     }
/*     */                   }
/*     */                   
/*     */ 
/*     */                   public void ready()
/*     */                   {
/* 601 */                     append("ready");
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */                   public void failed(Throwable error)
/*     */                   {
/* 608 */                     append("failed: " + Debug.getNestedExceptionMessage(error));
/*     */                     
/* 610 */                     Debug.out(error);
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */                   private void append(final String str)
/*     */                   {
/* 617 */                     if (TorrentListViewsUtils.stream_viewer != null)
/*     */                     {
/* 619 */                       Utils.execSWTThread(new Runnable()
/*     */                       {
/*     */ 
/*     */                         public void run()
/*     */                         {
/*     */ 
/* 625 */                           if ((TorrentListViewsUtils.stream_viewer != null) && (!TorrentListViewsUtils.stream_viewer.isDisposed()))
/*     */                           {
/* 627 */                             TorrentListViewsUtils.stream_viewer.append(str + "\r\n");
/*     */                           }
/*     */                         }
/*     */                       });
/*     */                     }
/*     */                   }
/*     */                 }));
/*     */               }
/*     */             }
/*     */           }.start();
/*     */         }
/*     */         else
/*     */         {
/* 640 */           FeatureManagerUI.openStreamPlusWindow(referal);
/*     */         }
/*     */         
/*     */ 
/* 644 */         return 0;
/*     */       }
/*     */       
/*     */ 
/* 648 */       synchronized (TorrentListViewsUtils.class)
/*     */       {
/* 650 */         if ((current_stream != null) && (!current_stream.isCancelled()))
/*     */         {
/* 652 */           current_stream.cancel();
/*     */           
/* 654 */           current_stream = null;
/*     */         }
/*     */       }
/*     */       
/* 658 */       Class epwClass = null;
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 663 */         PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azemp", false);
/*     */         
/*     */ 
/* 666 */         if (pi == null)
/*     */         {
/* 668 */           installEMP(dm.getDisplayName(), new Runnable() { public void run() { TorrentListViewsUtils.openInEMP(this.val$dm, f_file_index, complete_only, referal); }
/*     */           }); }
/* 670 */         if (!pi.getPluginState().isOperational())
/*     */         {
/* 672 */           return 1;
/*     */         }
/*     */         
/* 675 */         epwClass = pi.getPlugin().getClass().getClassLoader().loadClass("com.azureus.plugins.azemp.ui.swt.emp.EmbeddedPlayerWindowSWT");
/*     */       }
/*     */       catch (ClassNotFoundException e1)
/*     */       {
/* 679 */         return 1;
/*     */       }
/*     */       
/*     */       try
/*     */       {
/* 684 */         Method method = epwClass.getMethod("openWindow", new Class[] { File.class, String.class });
/*     */         
/*     */ 
/*     */ 
/* 688 */         File f = file.getFile(true);
/*     */         
/* 690 */         method.invoke(null, new Object[] { f, f.getName() });
/*     */         
/*     */ 
/*     */ 
/* 694 */         return 0;
/*     */       }
/*     */       catch (Throwable e) {
/* 697 */         debug("file/name open method missing");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 703 */         Method method = epwClass.getMethod("openWindow", new Class[] { DownloadManager.class });
/*     */         
/*     */ 
/*     */ 
/* 707 */         method.invoke(null, new Object[] { dm });
/*     */         
/*     */ 
/*     */ 
/* 711 */         return 0;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 720 */       return 1;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 713 */       e.printStackTrace();
/* 714 */       if ((e.getMessage() == null) || (!e.getMessage().toLowerCase().endsWith("only")))
/*     */       {
/* 716 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int openInEMP(String name, final URL url)
/*     */   {
/* 728 */     Class epwClass = null;
/*     */     
/*     */     try
/*     */     {
/* 732 */       PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID("azemp", false);
/*     */       
/*     */ 
/* 735 */       if (pi == null)
/*     */       {
/* 737 */         installEMP(name, new Runnable() { public void run() { TorrentListViewsUtils.openInEMP(this.val$name, url); }
/*     */         }); }
/* 739 */       if (!pi.getPluginState().isOperational())
/*     */       {
/* 741 */         return 1;
/*     */       }
/*     */       
/* 744 */       epwClass = pi.getPlugin().getClass().getClassLoader().loadClass("com.azureus.plugins.azemp.ui.swt.emp.EmbeddedPlayerWindowSWT");
/*     */     }
/*     */     catch (ClassNotFoundException e1)
/*     */     {
/* 748 */       return 1;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 753 */       Method method = epwClass.getMethod("openWindow", new Class[] { URL.class, String.class });
/*     */       
/*     */ 
/*     */ 
/* 757 */       method.invoke(null, new Object[] { url, name });
/*     */       
/* 759 */       return 0;
/*     */     }
/*     */     catch (Throwable e) {
/* 762 */       debug("URL/name open method missing");
/*     */     }
/* 764 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void handleNoFileExists(DownloadManager dm)
/*     */   {
/* 774 */     UIFunctionsSWT functionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 775 */     if (functionsSWT == null) {
/* 776 */       return;
/*     */     }
/* 778 */     ManagerUtils.start(dm);
/*     */     
/* 780 */     String sPrefix = "v3.mb.PlayFileNotFound.";
/* 781 */     MessageBoxShell mb = new MessageBoxShell(MessageText.getString(sPrefix + "title"), MessageText.getString(sPrefix + "text", new String[] { dm.getDisplayName() }), new String[] { MessageText.getString(sPrefix + "button.remove"), MessageText.getString(sPrefix + "button.redownload"), MessageText.getString("Button.cancel") }, 2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 790 */     mb.setRelatedObject(dm);
/* 791 */     mb.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int i) {
/* 793 */         if (i == 0) {
/* 794 */           ManagerUtils.asyncStopDelete(this.val$dm, 70, true, false, null);
/*     */         }
/* 796 */         else if (i == 1) {
/* 797 */           this.val$dm.forceRecheck(new ForceRecheckListener() {
/*     */             public void forceRecheckComplete(DownloadManager dm) {
/* 799 */               ManagerUtils.start(dm);
/*     */             }
/*     */           });
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
/*     */   public static void playOrStream(DownloadManager dm, int file_index)
/*     */   {
/* 816 */     playOrStream(dm, file_index, true, false, null);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/TorrentListViewsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */