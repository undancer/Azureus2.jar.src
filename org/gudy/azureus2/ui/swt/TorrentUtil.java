/*      */ package org.gudy.azureus2.ui.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedLimitHandler;
/*      */ import com.aelitis.azureus.core.util.DNSUtils;
/*      */ import com.aelitis.azureus.core.util.HTTPUtils;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils.CreateChatCallback;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.ISelectedVuzeFileContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
/*      */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*      */ import com.aelitis.azureus.ui.swt.mdi.MultipleDocumentInterfaceSWT;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.DirectoryDialog;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MenuItem;
/*      */ import org.eclipse.swt.widgets.MessageBox;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerSource;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStub.DownloadStubEx;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*      */ import org.gudy.azureus2.plugins.ui.UIPluginView;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*      */ import org.gudy.azureus2.ui.swt.maketorrent.MultiTrackerEditor;
/*      */ import org.gudy.azureus2.ui.swt.maketorrent.TrackerEditorListener;
/*      */ import org.gudy.azureus2.ui.swt.maketorrent.WebSeedsEditor;
/*      */ import org.gudy.azureus2.ui.swt.maketorrent.WebSeedsEditorListener;
/*      */ import org.gudy.azureus2.ui.swt.minibar.DownloadBar;
/*      */ import org.gudy.azureus2.ui.swt.minibar.MiniBarManager;
/*      */ import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
/*      */ import org.gudy.azureus2.ui.swt.shells.AdvRenameWindow;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.views.FilesViewMenuUtil;
/*      */ import org.gudy.azureus2.ui.swt.views.ViewUtils;
/*      */ import org.gudy.azureus2.ui.swt.views.ViewUtils.SpeedAdapter;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*      */ 
/*      */ public class TorrentUtil
/*      */ {
/*      */   public static void fillTorrentMenu(final Menu menu, final DownloadManager[] dms, AzureusCore azureus_core, final Composite composite, boolean include_show_details, int selected_dl_types, final TableView tv)
/*      */   {
/*      */     boolean isSeedingView;
/*  114 */     switch (selected_dl_types) {
/*      */     case 1: 
/*  116 */       isSeedingView = false;
/*  117 */       break;
/*      */     case 2: 
/*  119 */       isSeedingView = true;
/*  120 */       break;
/*      */     case 0: 
/*  122 */       if (dms.length == 1)
/*  123 */         isSeedingView = dms[0].isDownloadComplete(false);
/*  124 */       break;
/*      */     }
/*      */     
/*      */     
/*      */ 
/*  129 */     final boolean isSeedingView = false;
/*      */     
/*      */ 
/*  132 */     boolean hasSelection = dms.length > 0;
/*  133 */     boolean isSingleSelection = dms.length == 1;
/*      */     
/*  135 */     boolean isTrackerOn = org.gudy.azureus2.core3.tracker.util.TRTrackerUtils.isTrackerEnabled();
/*  136 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/*      */ 
/*  139 */     boolean bChangeDir = hasSelection;
/*      */     boolean fileRescan;
/*      */     boolean fileMove;
/*      */     boolean manualUpdate;
/*      */     boolean barsOpened;
/*  144 */     boolean changeUrl = barsOpened = manualUpdate = fileMove = fileRescan = 1;
/*  145 */     boolean stop; boolean start; boolean recheck; boolean forceStartEnabled; boolean forceStart = forceStartEnabled = recheck = start = stop = 0;
/*      */     
/*  147 */     boolean canSetSuperSeed = false;
/*  148 */     boolean superSeedAllYes = true;
/*  149 */     boolean superSeedAllNo = true;
/*      */     
/*  151 */     boolean upSpeedDisabled = false;
/*  152 */     long totalUpSpeed = 0L;
/*  153 */     boolean upSpeedUnlimited = false;
/*  154 */     long upSpeedSetMax = 0L;
/*      */     
/*  156 */     boolean downSpeedDisabled = false;
/*  157 */     long totalDownSpeed = 0L;
/*  158 */     boolean downSpeedUnlimited = false;
/*  159 */     long downSpeedSetMax = 0L;
/*      */     
/*  161 */     boolean allScanSelected = true;
/*  162 */     boolean allScanNotSelected = true;
/*      */     
/*  164 */     boolean allStopped = true;
/*  165 */     boolean allResumeIncomplete = true;
/*      */     
/*  167 */     boolean hasClearableLinks = false;
/*  168 */     boolean hasRevertableFiles = false;
/*      */     
/*  170 */     if (hasSelection) {
/*  171 */       for (int i = 0; i < dms.length; i++) {
/*  172 */         DownloadManager dm = dms[i];
/*      */         try
/*      */         {
/*  175 */           int maxul = dm.getStats().getUploadRateLimitBytesPerSecond();
/*  176 */           if (maxul == 0) {
/*  177 */             upSpeedUnlimited = true;
/*      */           }
/*  179 */           else if (maxul > upSpeedSetMax) {
/*  180 */             upSpeedSetMax = maxul;
/*      */           }
/*      */           
/*  183 */           if (maxul == -1) {
/*  184 */             maxul = 0;
/*  185 */             upSpeedDisabled = true;
/*      */           }
/*  187 */           totalUpSpeed += maxul;
/*      */           
/*  189 */           int maxdl = dm.getStats().getDownloadRateLimitBytesPerSecond();
/*  190 */           if (maxdl == 0) {
/*  191 */             downSpeedUnlimited = true;
/*      */           }
/*  193 */           else if (maxdl > downSpeedSetMax) {
/*  194 */             downSpeedSetMax = maxdl;
/*      */           }
/*      */           
/*  197 */           if (maxdl == -1) {
/*  198 */             maxdl = 0;
/*  199 */             downSpeedDisabled = true;
/*      */           }
/*  201 */           totalDownSpeed += maxdl;
/*      */         }
/*      */         catch (Exception ex) {
/*  204 */           Debug.printStackTrace(ex);
/*      */         }
/*      */         
/*  207 */         if ((barsOpened) && (!DownloadBar.getManager().isOpen(dm))) {
/*  208 */           barsOpened = false;
/*      */         }
/*  210 */         stop = (stop) || (ManagerUtils.isStopable(dm));
/*      */         
/*  212 */         start = (start) || (ManagerUtils.isStartable(dm));
/*      */         
/*  214 */         recheck = (recheck) || (dm.canForceRecheck());
/*      */         
/*  216 */         forceStartEnabled = (forceStartEnabled) || (ManagerUtils.isForceStartable(dm));
/*      */         
/*      */ 
/*  219 */         forceStart = (forceStart) || (dm.isForceStart());
/*      */         
/*  221 */         boolean stopped = ManagerUtils.isStopped(dm);
/*      */         
/*  223 */         allStopped &= stopped;
/*      */         
/*  225 */         fileMove = (fileMove) && (dm.canMoveDataFiles());
/*      */         
/*  227 */         if (userMode < 2) {
/*  228 */           TRTrackerAnnouncer trackerClient = dm.getTrackerClient();
/*      */           
/*  230 */           if (trackerClient != null) {
/*  231 */             boolean update_state = SystemTime.getCurrentTime() / 1000L - trackerClient.getLastUpdateTime() >= 60L;
/*      */             
/*  233 */             manualUpdate &= update_state;
/*      */           }
/*      */         }
/*      */         
/*  237 */         int state = dm.getState();
/*  238 */         bChangeDir &= ((state == 100) || (state == 70) || (state == 75));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  247 */         if ((bChangeDir) && (dms.length == 1)) {
/*  248 */           bChangeDir = dm.isDataAlreadyAllocated();
/*  249 */           if ((bChangeDir) && (state == 100))
/*      */           {
/*  251 */             bChangeDir = !dm.filesExist(true);
/*      */           } else {
/*  253 */             bChangeDir = false;
/*      */           }
/*      */         }
/*      */         
/*  257 */         DownloadManagerState dm_state = dm.getDownloadState();
/*      */         
/*  259 */         boolean scan = dm_state.getFlag(2L);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  264 */         boolean incomplete = !dm.isDownloadComplete(true);
/*      */         
/*  266 */         allScanSelected = (incomplete) && (allScanSelected) && (scan);
/*  267 */         allScanNotSelected = (incomplete) && (allScanNotSelected) && (!scan);
/*      */         
/*  269 */         PEPeerManager pm = dm.getPeerManager();
/*      */         
/*  271 */         if (pm != null)
/*      */         {
/*  273 */           if (pm.canToggleSuperSeedMode())
/*      */           {
/*  275 */             canSetSuperSeed = true;
/*      */           }
/*      */           
/*  278 */           if (pm.isSuperSeedMode())
/*      */           {
/*  280 */             superSeedAllYes = false;
/*      */           }
/*      */           else
/*      */           {
/*  284 */             superSeedAllNo = false;
/*      */           }
/*      */         } else {
/*  287 */           superSeedAllYes = false;
/*  288 */           superSeedAllNo = false;
/*      */         }
/*      */         
/*  291 */         if (dm_state.isResumeDataComplete()) {
/*  292 */           allResumeIncomplete = false;
/*      */         }
/*      */         
/*  295 */         if ((stopped) && (!hasClearableLinks) && 
/*  296 */           (dm.getDiskManagerFileInfoSet().nbFiles() > 1) && 
/*  297 */           (dm_state.getFileLinks().hasLinks()))
/*      */         {
/*  299 */           hasClearableLinks = true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  304 */         if (dm_state.getFileLinks().size() > 0)
/*      */         {
/*  306 */           hasRevertableFiles = true;
/*      */         }
/*      */       }
/*      */       
/*  310 */       fileRescan = (allScanSelected) || (allScanNotSelected);
/*      */     }
/*      */     else {
/*  313 */       barsOpened = false;
/*  314 */       forceStart = false;
/*  315 */       forceStartEnabled = false;
/*      */       
/*  317 */       start = false;
/*  318 */       stop = false;
/*  319 */       fileMove = false;
/*  320 */       fileRescan = false;
/*  321 */       upSpeedDisabled = true;
/*  322 */       downSpeedDisabled = true;
/*  323 */       changeUrl = false;
/*  324 */       recheck = false;
/*  325 */       manualUpdate = false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  330 */     if (bChangeDir) {
/*  331 */       MenuItem menuItemChangeDir = new MenuItem(menu, 8);
/*  332 */       Messages.setLanguageText(menuItemChangeDir, "MyTorrentsView.menu.changeDirectory");
/*      */       
/*  334 */       menuItemChangeDir.addListener(13, new Listener() {
/*      */         public void handleEvent(Event e) {
/*  336 */           TorrentUtil.changeDirSelectedTorrents(this.val$dms, composite.getShell());
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  342 */     if (include_show_details) {
/*  343 */       MenuItem itemDetails = new MenuItem(menu, 8);
/*  344 */       Messages.setLanguageText(itemDetails, "MyTorrentsView.menu.showdetails");
/*  345 */       menu.setDefaultItem(itemDetails);
/*  346 */       Utils.setMenuItemImage(itemDetails, "details");
/*  347 */       itemDetails.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager dm) {
/*  349 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  350 */           if (uiFunctions != null) {
/*  351 */             uiFunctions.getMDI().showEntryByID("DMDetails", dm);
/*      */           }
/*      */           
/*      */         }
/*  355 */       });
/*  356 */       itemDetails.setEnabled(hasSelection);
/*      */     }
/*      */     
/*      */ 
/*  360 */     MenuItem itemBar = new MenuItem(menu, 32);
/*  361 */     Messages.setLanguageText(itemBar, "MyTorrentsView.menu.showdownloadbar");
/*  362 */     Utils.setMenuItemImage(itemBar, "downloadBar");
/*  363 */     itemBar.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  365 */         if (DownloadBar.getManager().isOpen(dm)) {
/*  366 */           DownloadBar.close(dm);
/*      */         } else {
/*  368 */           DownloadBar.open(dm, menu.getShell());
/*      */         }
/*      */       }
/*  371 */     });
/*  372 */     itemBar.setEnabled(hasSelection);
/*  373 */     itemBar.setSelection(barsOpened);
/*      */     
/*      */ 
/*  376 */     new MenuItem(menu, 2);
/*      */     
/*      */ 
/*  379 */     MenuItem itemOpen = new MenuItem(menu, 8);
/*  380 */     Messages.setLanguageText(itemOpen, "MyTorrentsView.menu.open");
/*  381 */     Utils.setMenuItemImage(itemOpen, "run");
/*  382 */     itemOpen.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/*  384 */         TorrentUtil.runDataSources(dms);
/*      */       }
/*  386 */     });
/*  387 */     itemOpen.setEnabled(hasSelection);
/*      */     
/*      */ 
/*  390 */     final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*  391 */     MenuItem itemExplore = new MenuItem(menu, 8);
/*  392 */     Messages.setLanguageText(itemExplore, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*      */     
/*  394 */     itemExplore.addListener(13, new ListenerDMTask(dms, false) {
/*      */       public void run(DownloadManager dm) {
/*  396 */         ManagerUtils.open(dm, use_open_containing_folder);
/*      */       }
/*  398 */     });
/*  399 */     itemExplore.setEnabled(hasSelection);
/*      */     
/*      */ 
/*      */ 
/*  403 */     Menu menuBrowse = new Menu(menu.getShell(), 4);
/*  404 */     MenuItem itemBrowse = new MenuItem(menu, 64);
/*  405 */     Messages.setLanguageText(itemBrowse, "MyTorrentsView.menu.browse");
/*  406 */     itemBrowse.setMenu(menuBrowse);
/*      */     
/*  408 */     MenuItem itemBrowsePublic = new MenuItem(menuBrowse, 8);
/*  409 */     itemBrowsePublic.setText(MessageText.getString("label.public") + "...");
/*  410 */     itemBrowsePublic.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  412 */         ManagerUtils.browse(dm, false, true);
/*      */       }
/*      */       
/*  415 */     });
/*  416 */     MenuItem itemBrowseAnon = new MenuItem(menuBrowse, 8);
/*  417 */     itemBrowseAnon.setText(MessageText.getString("label.anon") + "...");
/*  418 */     itemBrowseAnon.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  420 */         ManagerUtils.browse(dm, true, true);
/*      */       }
/*      */       
/*  423 */     });
/*  424 */     new MenuItem(menuBrowse, 2);
/*      */     
/*  426 */     MenuItem itemBrowseURL = new MenuItem(menuBrowse, 8);
/*  427 */     Messages.setLanguageText(itemBrowseURL, "label.copy.url.to.clip");
/*  428 */     itemBrowseURL.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  430 */         Utils.getOffOfSWTThread(new AERunnable()
/*      */         {
/*      */           public void runSupport()
/*      */           {
/*  434 */             String url = ManagerUtils.browse(TorrentUtil.8.this.val$dms[0], true, false);
/*  435 */             if (url != null) {
/*  436 */               ClipboardCopy.copyToClipBoard(url);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*  441 */     });
/*  442 */     itemBrowseURL.setEnabled(isSingleSelection);
/*      */     
/*  444 */     new MenuItem(menuBrowse, 2);
/*      */     
/*  446 */     MenuItem itemBrowseDir = new MenuItem(menuBrowse, 32);
/*  447 */     Messages.setLanguageText(itemBrowseDir, "library.launch.web.in.browser.dir.list");
/*  448 */     itemBrowseDir.setSelection(COConfigurationManager.getBooleanParameter("Library.LaunchWebsiteInBrowserDirList"));
/*  449 */     itemBrowseDir.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  451 */         COConfigurationManager.setParameter("Library.LaunchWebsiteInBrowserDirList", this.val$itemBrowseDir.getSelection());
/*      */       }
/*      */       
/*  454 */     });
/*  455 */     itemBrowse.setEnabled(hasSelection);
/*      */     
/*      */ 
/*      */ 
/*  459 */     MenuItem itemAdvanced = new MenuItem(menu, 64);
/*  460 */     Messages.setLanguageText(itemAdvanced, "MyTorrentsView.menu.advancedmenu");
/*  461 */     itemAdvanced.setEnabled(hasSelection);
/*      */     
/*  463 */     Menu menuAdvanced = new Menu(menu.getShell(), 4);
/*  464 */     itemAdvanced.setMenu(menuAdvanced);
/*      */     
/*      */ 
/*      */ 
/*  468 */     long kInB = DisplayFormatters.getKinB();
/*      */     
/*  470 */     long maxDownload = COConfigurationManager.getIntParameter("Max Download Speed KBs", 0) * kInB;
/*      */     
/*  472 */     long maxUpload = COConfigurationManager.getIntParameter("Max Upload Speed KBs", 0) * kInB;
/*      */     
/*      */ 
/*  475 */     ViewUtils.addSpeedMenu(menu.getShell(), menuAdvanced, true, true, true, hasSelection, downSpeedDisabled, downSpeedUnlimited, totalDownSpeed, downSpeedSetMax, maxDownload, upSpeedDisabled, upSpeedUnlimited, totalUpSpeed, upSpeedSetMax, maxUpload, dms.length, null, new ViewUtils.SpeedAdapter()
/*      */     {
/*      */ 
/*      */       public void setDownSpeed(final int speed)
/*      */       {
/*      */ 
/*  481 */         ListenerDMTask task = new ListenerDMTask(this.val$dms) {
/*      */           public void run(DownloadManager dm) {
/*  483 */             dm.getStats().setDownloadRateLimitBytesPerSecond(speed);
/*      */           }
/*  485 */         };
/*  486 */         task.go();
/*      */       }
/*      */       
/*      */       public void setUpSpeed(final int speed) {
/*  490 */         ListenerDMTask task = new ListenerDMTask(this.val$dms) {
/*      */           public void run(DownloadManager dm) {
/*  492 */             dm.getStats().setUploadRateLimitBytesPerSecond(speed);
/*      */           }
/*  494 */         };
/*  495 */         task.go();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  500 */     });
/*  501 */     final Menu speedLimitsMenu = new Menu(menuAdvanced.getShell(), 4);
/*      */     
/*  503 */     MenuItem speedLimitsMenuItem = new MenuItem(menuAdvanced, 64);
/*  504 */     Messages.setLanguageText(speedLimitsMenuItem, "MainWindow.menu.speed_limits");
/*      */     
/*  506 */     speedLimitsMenuItem.setMenu(speedLimitsMenu);
/*      */     
/*  508 */     MenuBuildUtils.addMaintenanceListenerForMenu(speedLimitsMenu, new MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */       public void buildMenu(Menu menu, MenuEvent menuEvent) {
/*  511 */         TorrentUtil.addSpeedLimitsMenu(this.val$dms, speedLimitsMenu);
/*      */       }
/*      */       
/*      */ 
/*  515 */     });
/*  516 */     Menu menuTracker = new Menu(menu.getShell(), 4);
/*  517 */     MenuItem itemTracker = new MenuItem(menuAdvanced, 64);
/*  518 */     Messages.setLanguageText(itemTracker, "MyTorrentsView.menu.tracker");
/*  519 */     itemTracker.setMenu(menuTracker);
/*  520 */     itemExplore.setEnabled(hasSelection);
/*  521 */     addTrackerTorrentMenu(menuTracker, dms, changeUrl, manualUpdate, allStopped, use_open_containing_folder);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  526 */     MenuItem itemFiles = new MenuItem(menuAdvanced, 64);
/*  527 */     Messages.setLanguageText(itemFiles, "ConfigView.section.files");
/*      */     
/*  529 */     Menu menuFiles = new Menu(composite.getShell(), 4);
/*  530 */     itemFiles.setMenu(menuFiles);
/*      */     
/*  532 */     MenuItem itemFileMoveData = new MenuItem(menuFiles, 8);
/*  533 */     Messages.setLanguageText(itemFileMoveData, "MyTorrentsView.menu.movedata");
/*  534 */     itemFileMoveData.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/*  536 */         TorrentUtil.moveDataFiles(composite.getShell(), dms);
/*      */       }
/*  538 */     });
/*  539 */     itemFileMoveData.setEnabled(fileMove);
/*      */     
/*  541 */     MenuItem itemFileMoveTorrent = new MenuItem(menuFiles, 8);
/*  542 */     Messages.setLanguageText(itemFileMoveTorrent, "MyTorrentsView.menu.movetorrent");
/*      */     
/*  544 */     itemFileMoveTorrent.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/*  546 */         TorrentUtil.moveTorrentFile(composite.getShell(), dms);
/*      */       }
/*  548 */     });
/*  549 */     itemFileMoveTorrent.setEnabled(fileMove);
/*      */     
/*  551 */     MenuItem itemCheckFilesExist = new MenuItem(menuFiles, 8);
/*  552 */     Messages.setLanguageText(itemCheckFilesExist, "MyTorrentsView.menu.checkfilesexist");
/*      */     
/*  554 */     itemCheckFilesExist.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  556 */         dm.filesExist(true);
/*      */       }
/*      */       
/*  559 */     });
/*  560 */     MenuItem itemLocateFiles = new MenuItem(menuFiles, 8);
/*  561 */     Messages.setLanguageText(itemLocateFiles, "MyTorrentsView.menu.locatefiles");
/*      */     
/*  563 */     itemLocateFiles.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/*  565 */         ManagerUtils.locateFiles(dms, menu.getShell());
/*      */       }
/*      */       
/*  568 */     });
/*  569 */     final MenuItem itemFileRescan = new MenuItem(menuFiles, 32);
/*  570 */     Messages.setLanguageText(itemFileRescan, "MyTorrentsView.menu.rescanfile");
/*  571 */     itemFileRescan.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  573 */         dm.getDownloadState().setFlag(2L, itemFileRescan.getSelection());
/*      */       }
/*      */       
/*      */ 
/*  577 */     });
/*  578 */     itemFileRescan.setSelection(allScanSelected);
/*  579 */     itemFileRescan.setEnabled(fileRescan);
/*      */     
/*      */ 
/*      */ 
/*  583 */     MenuItem itemRevertFiles = new MenuItem(menu, 8);
/*  584 */     Messages.setLanguageText(itemRevertFiles, "MyTorrentsView.menu.revertfiles");
/*  585 */     itemRevertFiles.addListener(13, new ListenerDMTask(dms)
/*      */     {
/*      */       public void run(DownloadManager[] dms) {
/*  588 */         FilesViewMenuUtil.revertFiles(tv, dms);
/*      */       }
/*      */       
/*  591 */     });
/*  592 */     itemRevertFiles.setEnabled(hasRevertableFiles);
/*      */     
/*      */ 
/*      */ 
/*  596 */     MenuItem itemClearLinks = new MenuItem(menuFiles, 8);
/*  597 */     Messages.setLanguageText(itemClearLinks, "FilesView.menu.clear.links");
/*  598 */     itemClearLinks.addListener(13, new ListenerDMTask(dms)
/*      */     {
/*      */       public void run(DownloadManager dm) {
/*  601 */         if ((ManagerUtils.isStopped(dm)) && (dm.getDownloadState().getFileLinks().hasLinks()))
/*      */         {
/*  603 */           DiskManagerFileInfoSet fis = dm.getDiskManagerFileInfoSet();
/*      */           
/*  605 */           if (fis.nbFiles() > 1)
/*      */           {
/*  607 */             DiskManagerFileInfo[] files = fis.getFiles();
/*      */             
/*  609 */             for (DiskManagerFileInfo file_info : files)
/*      */             {
/*  611 */               File file_link = file_info.getFile(true);
/*  612 */               File file_nolink = file_info.getFile(false);
/*      */               
/*  614 */               if (!file_nolink.getAbsolutePath().equals(file_link.getAbsolutePath()))
/*      */               {
/*  616 */                 file_info.setLink(null);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  623 */     });
/*  624 */     itemClearLinks.setEnabled(hasClearableLinks);
/*      */     
/*      */ 
/*      */ 
/*  628 */     MenuItem itemFileClearAlloc = new MenuItem(menuFiles, 8);
/*  629 */     Messages.setLanguageText(itemFileClearAlloc, "MyTorrentsView.menu.clear_alloc_data");
/*      */     
/*  631 */     itemFileClearAlloc.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  633 */         dm.setDataAlreadyAllocated(false);
/*      */       }
/*      */       
/*  636 */     });
/*  637 */     itemFileClearAlloc.setEnabled(allStopped);
/*      */     
/*      */ 
/*      */ 
/*  641 */     MenuItem itemFileClearResume = new MenuItem(menuFiles, 8);
/*  642 */     Messages.setLanguageText(itemFileClearResume, "MyTorrentsView.menu.clear_resume_data");
/*      */     
/*  644 */     itemFileClearResume.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  646 */         dm.getDownloadState().clearResumeData();
/*      */       }
/*  648 */     });
/*  649 */     itemFileClearResume.setEnabled(allStopped);
/*      */     
/*      */ 
/*      */ 
/*  653 */     MenuItem itemFileSetResumeComplete = new MenuItem(menuFiles, 8);
/*  654 */     Messages.setLanguageText(itemFileSetResumeComplete, "MyTorrentsView.menu.set.resume.complete");
/*      */     
/*  656 */     itemFileSetResumeComplete.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  658 */         TorrentUtils.setResumeDataCompletelyValid(dm.getDownloadState());
/*      */       }
/*  660 */     });
/*  661 */     itemFileSetResumeComplete.setEnabled((allStopped) && (allResumeIncomplete));
/*      */     
/*      */ 
/*      */ 
/*  665 */     final List<Download> ar_dms = new ArrayList();
/*      */     
/*  667 */     for (DownloadManager dm : dms)
/*      */     {
/*  669 */       Download stub = PluginCoreUtils.wrap(dm);
/*      */       
/*  671 */       if (stub.canStubbify())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  676 */         ar_dms.add(stub);
/*      */       }
/*      */     }
/*  679 */     MenuItem itemArchive = new MenuItem(menuAdvanced, 8);
/*  680 */     Messages.setLanguageText(itemArchive, "MyTorrentsView.menu.archive");
/*  681 */     Utils.setMenuItemImage(itemArchive, "archive");
/*  682 */     itemArchive.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/*  684 */         ManagerUtils.moveToArchive(ar_dms, null);
/*      */       }
/*      */       
/*  687 */     });
/*  688 */     itemArchive.setEnabled(ar_dms.size() > 0);
/*      */     
/*      */ 
/*      */ 
/*  692 */     MenuItem itemRename = new MenuItem(menuAdvanced, 4);
/*  693 */     Messages.setLanguageText(itemRename, "MyTorrentsView.menu.rename");
/*  694 */     itemRename.setEnabled(hasSelection);
/*  695 */     itemRename.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  697 */         for (DownloadManager dm : this.val$dms) {
/*  698 */           AdvRenameWindow window = new AdvRenameWindow();
/*  699 */           window.open(dm);
/*      */         }
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/*  706 */     if (ManagerUtils.canFindMoreLikeThis()) {
/*  707 */       MenuItem itemFindMore = new MenuItem(menuAdvanced, 8);
/*  708 */       Messages.setLanguageText(itemFindMore, "MyTorrentsView.menu.findmorelikethis");
/*      */       
/*  710 */       itemFindMore.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager[] dms) {
/*  712 */           ManagerUtils.findMoreLikeThis(dms[0], menu.getShell());
/*      */         }
/*  714 */       });
/*  715 */       itemFindMore.setSelection(isSingleSelection);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  720 */     Menu quickViewMenu = new Menu(menuAdvanced.getShell(), 4);
/*  721 */     MenuItem quickViewMenuItem = new MenuItem(menuAdvanced, 64);
/*  722 */     Messages.setLanguageText(quickViewMenuItem, "MainWindow.menu.quick_view");
/*      */     
/*  724 */     quickViewMenuItem.setMenu(quickViewMenu);
/*      */     
/*  726 */     MenuBuildUtils.addMaintenanceListenerForMenu(quickViewMenu, new MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */       public void buildMenu(Menu menu, MenuEvent menuEvent) {
/*  729 */         DownloadManager dm = this.val$dms[0];
/*      */         
/*  731 */         DiskManagerFileInfo[] files = dm.getDiskManagerFileInfoSet().getFiles();
/*      */         
/*  733 */         int added = 0;
/*      */         
/*  735 */         for (final DiskManagerFileInfo file : files)
/*      */         {
/*  737 */           if (Utils.isQuickViewSupported(file))
/*      */           {
/*  739 */             final MenuItem addItem = new MenuItem(menu, 32);
/*      */             
/*  741 */             addItem.setSelection(Utils.isQuickViewActive(file));
/*      */             
/*  743 */             addItem.setText(file.getTorrentFile().getRelativePath());
/*      */             
/*  745 */             addItem.addListener(13, new Listener() {
/*      */               public void handleEvent(Event arg) {
/*  747 */                 Utils.setQuickViewActive(file, addItem.getSelection());
/*      */               }
/*      */               
/*  750 */             });
/*  751 */             added++;
/*      */           }
/*      */         }
/*      */         
/*  755 */         if (added == 0)
/*      */         {
/*  757 */           MenuItem addItem = new MenuItem(menu, 8);
/*      */           
/*  759 */           addItem.setText(MessageText.getString("quick.view.no.files"));
/*      */           
/*  761 */           addItem.setEnabled(false);
/*      */         }
/*      */         
/*      */       }
/*  765 */     });
/*  766 */     quickViewMenuItem.setEnabled(isSingleSelection);
/*      */     
/*      */ 
/*      */ 
/*  770 */     MenuFactory.addAlertsMenu(menuAdvanced, true, dms);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  775 */     if (userMode > 0) {
/*  776 */       MenuItem itemExport = new MenuItem(menuAdvanced, 64);
/*  777 */       Messages.setLanguageText(itemExport, "MyTorrentsView.menu.exportmenu");
/*  778 */       Utils.setMenuItemImage(itemExport, "export");
/*  779 */       itemExport.setEnabled(hasSelection);
/*      */       
/*  781 */       Menu menuExport = new Menu(composite.getShell(), 4);
/*  782 */       itemExport.setMenu(menuExport);
/*      */       
/*      */ 
/*  785 */       final MenuItem itemExportXML = new MenuItem(menuExport, 8);
/*  786 */       Messages.setLanguageText(itemExportXML, "MyTorrentsView.menu.export");
/*  787 */       itemExportXML.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager[] dms) {
/*  789 */           DownloadManager dm = dms[0];
/*  790 */           if (dm != null) {
/*  791 */             new org.gudy.azureus2.ui.swt.exporttorrent.wizard.ExportTorrentWizard(itemExportXML.getDisplay(), dm);
/*      */           }
/*      */           
/*      */         }
/*  795 */       });
/*  796 */       MenuItem itemExportTorrent = new MenuItem(menuExport, 8);
/*  797 */       Messages.setLanguageText(itemExportTorrent, "MyTorrentsView.menu.exporttorrent");
/*      */       
/*  799 */       itemExportTorrent.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager[] dms) {
/*  801 */           TorrentUtil.exportTorrent(dms, composite.getShell());
/*      */         }
/*      */         
/*      */ 
/*  805 */       });
/*  806 */       MenuItem itemWebSeed = new MenuItem(menuExport, 8);
/*  807 */       Messages.setLanguageText(itemWebSeed, "MyTorrentsView.menu.exporthttpseeds");
/*      */       
/*  809 */       itemWebSeed.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager[] dms) {
/*  811 */           TorrentUtil.exportHTTPSeeds(dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  819 */     if (userMode > 0) {
/*  820 */       MenuItem itemExportXML = new MenuItem(menuAdvanced, 8);
/*  821 */       Messages.setLanguageText(itemExportXML, "label.options.and.info");
/*  822 */       itemExportXML.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager[] dms) {
/*  824 */           UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  825 */           if (uiFunctions != null) {
/*  826 */             uiFunctions.getMDI().showEntryByID("TorrentOptionsView", dms);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  835 */     if (userMode > 0) {
/*  836 */       MenuItem itemPeerSource = new MenuItem(menuAdvanced, 64);
/*  837 */       Messages.setLanguageText(itemPeerSource, "MyTorrentsView.menu.peersource");
/*      */       
/*  839 */       Menu menuPeerSource = new Menu(composite.getShell(), 4);
/*  840 */       itemPeerSource.setMenu(menuPeerSource);
/*      */       
/*  842 */       addPeerSourceSubMenu(dms, menuPeerSource);
/*      */     }
/*      */     
/*      */ 
/*  846 */     if (userMode > 0)
/*      */     {
/*  848 */       final MenuItem ipf_enable = new MenuItem(menuAdvanced, 32);
/*  849 */       Messages.setLanguageText(ipf_enable, "MyTorrentsView.menu.ipf_enable");
/*      */       
/*  851 */       ipf_enable.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager dm) {
/*  853 */           dm.getDownloadState().setFlag(256L, !ipf_enable.getSelection());
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*  858 */       });
/*  859 */       boolean bEnabled = IpFilterManagerFactory.getSingleton().getIPFilter().isEnabled();
/*      */       
/*  861 */       if (bEnabled) {
/*  862 */         boolean allChecked = true;
/*  863 */         boolean allUnchecked = true;
/*      */         
/*  865 */         for (int j = 0; j < dms.length; j++) {
/*  866 */           DownloadManager dm = dms[j];
/*      */           
/*  868 */           boolean b = dm.getDownloadState().getFlag(256L);
/*      */           
/*      */ 
/*  871 */           if (b) {
/*  872 */             allUnchecked = false;
/*      */           } else {
/*  874 */             allChecked = false;
/*      */           }
/*      */         }
/*      */         
/*      */         boolean bChecked;
/*      */         boolean bChecked;
/*  880 */         if (allUnchecked) {
/*  881 */           bChecked = true; } else { boolean bChecked;
/*  882 */           if (allChecked) {
/*  883 */             bChecked = false;
/*      */           } else {
/*  885 */             bChecked = false;
/*      */           }
/*      */         }
/*  888 */         ipf_enable.setSelection(bChecked);
/*      */       }
/*      */       
/*  891 */       ipf_enable.setEnabled(bEnabled);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  897 */     if (userMode > 1) {
/*  898 */       MenuItem itemNetworks = new MenuItem(menuAdvanced, 64);
/*  899 */       Messages.setLanguageText(itemNetworks, "MyTorrentsView.menu.networks");
/*      */       
/*  901 */       Menu menuNetworks = new Menu(composite.getShell(), 4);
/*  902 */       itemNetworks.setMenu(menuNetworks);
/*      */       
/*  904 */       addNetworksSubMenu(dms, menuNetworks);
/*      */     }
/*      */     
/*      */ 
/*  908 */     if ((userMode > 1) && (isSeedingView))
/*      */     {
/*  910 */       MenuItem itemSuperSeed = new MenuItem(menuAdvanced, 32);
/*      */       
/*  912 */       Messages.setLanguageText(itemSuperSeed, "ManagerItem.superseeding");
/*      */       
/*  914 */       boolean enabled = (canSetSuperSeed) && ((superSeedAllNo) || (superSeedAllYes));
/*      */       
/*  916 */       itemSuperSeed.setEnabled(enabled);
/*      */       
/*  918 */       final boolean selected = superSeedAllNo;
/*      */       
/*  920 */       if (enabled)
/*      */       {
/*  922 */         itemSuperSeed.setSelection(selected);
/*      */         
/*  924 */         itemSuperSeed.addListener(13, new ListenerDMTask(dms) {
/*      */           public void run(DownloadManager dm) {
/*  926 */             PEPeerManager pm = dm.getPeerManager();
/*      */             
/*  928 */             if (pm != null)
/*      */             {
/*  930 */               if ((pm.isSuperSeedMode() == selected) && (pm.canToggleSuperSeedMode()))
/*      */               {
/*      */ 
/*  933 */                 pm.setSuperSeedMode(!selected);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  942 */     if (userMode > 0)
/*      */     {
/*  944 */       boolean can_pause = false;
/*      */       
/*  946 */       for (int i = 0; i < dms.length; i++)
/*      */       {
/*  948 */         DownloadManager dm = dms[i];
/*      */         
/*  950 */         if (ManagerUtils.isPauseable(dm))
/*      */         {
/*  952 */           can_pause = true;
/*      */           
/*  954 */           break;
/*      */         }
/*      */       }
/*      */       
/*  958 */       MenuItem itemPauseFor = new MenuItem(menuAdvanced, 8);
/*      */       
/*  960 */       itemPauseFor.setEnabled(can_pause);
/*      */       
/*  962 */       Messages.setLanguageText(itemPauseFor, "MainWindow.menu.transfers.pausetransfersfor");
/*      */       
/*      */ 
/*  965 */       itemPauseFor.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/*  967 */           TorrentUtil.pauseDownloadsFor(this.val$dms);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*  973 */     MenuItem itemPositionManual = new MenuItem(menuAdvanced, 8);
/*  974 */     Messages.setLanguageText(itemPositionManual, "MyTorrentsView.menu.reposition.manual");
/*      */     
/*  976 */     itemPositionManual.addSelectionListener(new SelectionAdapter() {
/*      */       public void widgetSelected(SelectionEvent e) {
/*  978 */         TorrentUtil.repositionManual(this.val$tv, dms, composite.getShell(), isSeedingView);
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*  983 */     if ((userMode > 0) && (isTrackerOn))
/*      */     {
/*  985 */       MenuItem itemHost = new MenuItem(menu, 8);
/*  986 */       Messages.setLanguageText(itemHost, "MyTorrentsView.menu.host");
/*  987 */       Utils.setMenuItemImage(itemHost, "host");
/*  988 */       itemHost.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  990 */           TorrentUtil.hostTorrents(this.val$dms);
/*      */         }
/*      */         
/*      */ 
/*  994 */       });
/*  995 */       MenuItem itemPublish = new MenuItem(menu, 8);
/*  996 */       Messages.setLanguageText(itemPublish, "MyTorrentsView.menu.publish");
/*  997 */       Utils.setMenuItemImage(itemPublish, "publish");
/*  998 */       itemPublish.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1000 */           TorrentUtil.publishTorrents(this.val$dms);
/*      */         }
/*      */         
/* 1003 */       });
/* 1004 */       itemHost.setEnabled(hasSelection);
/* 1005 */       itemPublish.setEnabled(hasSelection);
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1068 */     Menu menuCategory = new Menu(composite.getShell(), 4);
/* 1069 */     MenuItem itemCategory = new MenuItem(menu, 64);
/* 1070 */     Messages.setLanguageText(itemCategory, "MyTorrentsView.menu.setCategory");
/*      */     
/* 1072 */     itemCategory.setMenu(menuCategory);
/* 1073 */     itemCategory.setEnabled(hasSelection);
/*      */     
/* 1075 */     addCategorySubMenu(dms, menuCategory, composite);
/*      */     
/*      */ 
/*      */ 
/* 1079 */     Menu menuTags = new Menu(composite.getShell(), 4);
/* 1080 */     MenuItem itemTags = new MenuItem(menu, 64);
/* 1081 */     Messages.setLanguageText(itemTags, "label.tags");
/* 1082 */     itemTags.setMenu(menuTags);
/* 1083 */     itemTags.setEnabled(hasSelection);
/*      */     
/* 1085 */     org.gudy.azureus2.ui.swt.views.utils.TagUIUtils.addLibraryViewTagsSubMenu(dms, menuTags, composite);
/*      */     
/*      */ 
/*      */ 
/* 1089 */     if (isSeedingView) {
/* 1090 */       MenuItem itemPersonalShare = new MenuItem(menu, 8);
/* 1091 */       Messages.setLanguageText(itemPersonalShare, "MyTorrentsView.menu.create_personal_share");
/*      */       
/* 1093 */       itemPersonalShare.addListener(13, new ListenerDMTask(dms, false) {
/*      */         public void run(DownloadManager dm) {
/* 1095 */           File file = dm.getSaveLocation();
/*      */           
/* 1097 */           Map<String, String> properties = new HashMap();
/*      */           
/* 1099 */           properties.put("personal", "true");
/*      */           
/* 1101 */           if (file.isFile())
/*      */           {
/* 1103 */             ShareUtils.shareFile(file.getAbsolutePath(), properties);
/*      */           }
/* 1105 */           else if (file.isDirectory())
/*      */           {
/* 1107 */             ShareUtils.shareDir(file.getAbsolutePath(), properties);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1130 */     new MenuItem(menu, 2);
/*      */     
/*      */ 
/* 1133 */     MenuItem itemQueue = new MenuItem(menu, 8);
/* 1134 */     Messages.setLanguageText(itemQueue, "MyTorrentsView.menu.queue");
/* 1135 */     Utils.setMenuItemImage(itemQueue, "start");
/* 1136 */     itemQueue.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1138 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1140 */             TorrentUtil.queueDataSources(TorrentUtil.37.this.val$dms, true);
/*      */           }
/*      */         });
/*      */       }
/* 1144 */     });
/* 1145 */     itemQueue.setEnabled(start);
/*      */     
/*      */ 
/* 1148 */     if (userMode > 0) {
/* 1149 */       final MenuItem itemForceStart = new MenuItem(menu, 32);
/* 1150 */       Messages.setLanguageText(itemForceStart, "MyTorrentsView.menu.forceStart");
/* 1151 */       Utils.setMenuItemImage(itemForceStart, "forcestart");
/* 1152 */       itemForceStart.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager dm) {
/* 1154 */           if (ManagerUtils.isForceStartable(dm)) {
/* 1155 */             dm.setForceStart(itemForceStart.getSelection());
/*      */           }
/*      */         }
/* 1158 */       });
/* 1159 */       itemForceStart.setSelection(forceStart);
/* 1160 */       itemForceStart.setEnabled(forceStartEnabled);
/*      */     }
/*      */     
/*      */ 
/* 1164 */     if (userMode > 0) {
/* 1165 */       MenuItem itemPause = new MenuItem(menu, 8);
/* 1166 */       Messages.setLanguageText(itemPause, "v3.MainWindow.button.pause");
/* 1167 */       Utils.setMenuItemImage(itemPause, "pause");
/* 1168 */       itemPause.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/* 1170 */           Utils.getOffOfSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/* 1172 */               TorrentUtil.pauseDataSources(TorrentUtil.39.this.val$dms);
/*      */             }
/*      */           });
/*      */         }
/* 1176 */       });
/* 1177 */       itemPause.setEnabled(stop);
/*      */     }
/*      */     
/*      */ 
/* 1181 */     MenuItem itemStop = new MenuItem(menu, 8);
/* 1182 */     Messages.setLanguageText(itemStop, "MyTorrentsView.menu.stop");
/* 1183 */     Utils.setMenuItemImage(itemStop, "stop");
/* 1184 */     itemStop.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1186 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1188 */             TorrentUtil.stopDataSources(TorrentUtil.40.this.val$dms);
/*      */           }
/*      */         });
/*      */       }
/* 1192 */     });
/* 1193 */     itemStop.setEnabled(stop);
/*      */     
/*      */ 
/* 1196 */     MenuItem itemRecheck = new MenuItem(menu, 8);
/* 1197 */     Messages.setLanguageText(itemRecheck, "MyTorrentsView.menu.recheck");
/* 1198 */     Utils.setMenuItemImage(itemRecheck, "recheck");
/* 1199 */     itemRecheck.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/* 1201 */         if (dm.canForceRecheck()) {
/* 1202 */           dm.forceRecheck();
/*      */         }
/*      */       }
/* 1205 */     });
/* 1206 */     itemRecheck.setEnabled(recheck);
/*      */     
/*      */ 
/* 1209 */     MenuItem itemRemove = new MenuItem(menu, 8);
/* 1210 */     Messages.setLanguageText(itemRemove, "menu.delete.options");
/* 1211 */     Utils.setMenuItemImage(itemRemove, "delete");
/* 1212 */     itemRemove.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1214 */         TorrentUtil.removeDownloads(this.val$dms, null, true);
/*      */       }
/* 1216 */     });
/* 1217 */     itemRemove.setEnabled(hasSelection);
/*      */   }
/*      */   
/*      */ 
/*      */   protected static void addNetworksSubMenu(DownloadManager[] dms, Menu menuNetworks)
/*      */   {
/* 1223 */     for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++) {
/* 1224 */       final String nn = AENetworkClassifier.AT_NETWORKS[i];
/* 1225 */       String msg_text = "ConfigView.section.connection.networks." + nn;
/* 1226 */       final MenuItem itemNetwork = new MenuItem(menuNetworks, 32);
/* 1227 */       itemNetwork.setData("network", nn);
/* 1228 */       Messages.setLanguageText(itemNetwork, msg_text);
/* 1229 */       itemNetwork.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager dm) {
/* 1231 */           dm.getDownloadState().setNetworkEnabled(nn, itemNetwork.getSelection());
/*      */         }
/*      */         
/* 1234 */       });
/* 1235 */       boolean bChecked = dms.length > 0;
/* 1236 */       if (bChecked)
/*      */       {
/* 1238 */         for (int j = 0; j < dms.length; j++) {
/* 1239 */           DownloadManager dm = dms[j];
/*      */           
/* 1241 */           if (!dm.getDownloadState().isNetworkEnabled(nn)) {
/* 1242 */             bChecked = false;
/* 1243 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1248 */       itemNetwork.setSelection(bChecked);
/*      */     }
/*      */   }
/*      */   
/*      */   protected static void addPeerSourceSubMenu(DownloadManager[] dms, Menu menuPeerSource)
/*      */   {
/* 1254 */     boolean hasSelection = dms.length > 0;
/* 1255 */     for (int i = 0; i < PEPeerSource.PS_SOURCES.length; i++)
/*      */     {
/* 1257 */       final String p = PEPeerSource.PS_SOURCES[i];
/* 1258 */       String msg_text = "ConfigView.section.connection.peersource." + p;
/* 1259 */       final MenuItem itemPS = new MenuItem(menuPeerSource, 32);
/* 1260 */       itemPS.setData("peerSource", p);
/* 1261 */       Messages.setLanguageText(itemPS, msg_text);
/* 1262 */       itemPS.addListener(13, new ListenerDMTask(dms) {
/*      */         public void run(DownloadManager dm) {
/* 1264 */           dm.getDownloadState().setPeerSourceEnabled(p, itemPS.getSelection());
/*      */         }
/* 1266 */       });
/* 1267 */       itemPS.setSelection(true);
/*      */       
/* 1269 */       boolean bChecked = hasSelection;
/* 1270 */       boolean bEnabled = !hasSelection;
/* 1271 */       if (bChecked) {
/* 1272 */         bEnabled = true;
/*      */         
/*      */ 
/* 1275 */         for (int j = 0; j < dms.length; j++) {
/* 1276 */           DownloadManager dm = dms[j];
/*      */           
/* 1278 */           if (!dm.getDownloadState().isPeerSourceEnabled(p)) {
/* 1279 */             bChecked = false;
/*      */           }
/* 1281 */           if (!dm.getDownloadState().isPeerSourcePermitted(p)) {
/* 1282 */             bEnabled = false;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1287 */       itemPS.setSelection(bChecked);
/* 1288 */       itemPS.setEnabled(bEnabled);
/*      */     }
/*      */   }
/*      */   
/*      */   protected static void exportHTTPSeeds(DownloadManager[] dms) {
/* 1293 */     String NL = "\r\n";
/* 1294 */     String data = "";
/*      */     
/* 1296 */     boolean http_enable = COConfigurationManager.getBooleanParameter("HTTP.Data.Listen.Port.Enable");
/*      */     
/*      */     String port;
/*      */     String port;
/* 1300 */     if (http_enable)
/*      */     {
/* 1302 */       int p = COConfigurationManager.getIntParameter("HTTP.Data.Listen.Port");
/* 1303 */       int o = COConfigurationManager.getIntParameter("HTTP.Data.Listen.Port.Override");
/*      */       String port;
/* 1305 */       if (o == 0)
/*      */       {
/* 1307 */         port = String.valueOf(p);
/*      */       }
/*      */       else
/*      */       {
/* 1311 */         port = String.valueOf(o);
/*      */       }
/*      */     }
/*      */     else {
/* 1315 */       data = "You need to enable the HTTP port or modify the URL(s) appropriately\r\n\r\n";
/*      */       
/*      */ 
/* 1318 */       port = "<port>";
/*      */     }
/*      */     
/* 1321 */     String ip = COConfigurationManager.getStringParameter("Tracker IP", "");
/*      */     
/*      */ 
/* 1324 */     if (ip.length() == 0)
/*      */     {
/* 1326 */       data = data + "You might need to modify the host address in the URL(s)\r\n\r\n";
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1331 */         InetAddress ia = NetworkAdmin.getSingleton().getDefaultPublicAddress();
/*      */         
/* 1333 */         if (ia != null)
/*      */         {
/* 1335 */           ip = org.gudy.azureus2.core3.util.IPToHostNameResolver.syncResolve(ia.getHostAddress(), 10000);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/*      */ 
/* 1342 */       if (ip.length() == 0)
/*      */       {
/* 1344 */         ip = "<host>";
/*      */       }
/*      */     }
/*      */     
/* 1348 */     String base = "http://" + UrlUtils.convertIPV6Host(ip) + ":" + port + "/";
/*      */     
/*      */ 
/* 1351 */     for (int i = 0; i < dms.length; i++)
/*      */     {
/* 1353 */       DownloadManager dm = dms[i];
/*      */       
/* 1355 */       if (dm != null)
/*      */       {
/*      */ 
/*      */ 
/* 1359 */         TOTorrent torrent = dm.getTorrent();
/*      */         
/* 1361 */         if (torrent != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1366 */           data = data + base + "webseed" + "\r\n";
/*      */           try
/*      */           {
/* 1369 */             data = data + base + "files/" + java.net.URLEncoder.encode(new String(torrent.getHash(), "ISO-8859-1"), "ISO-8859-1") + "/" + "\r\n" + "\r\n";
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1379 */     if (data.length() > 0) {
/* 1380 */       ClipboardCopy.copyToClipBoard(data);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static void exportTorrent(DownloadManager[] dms, Shell parentShell)
/*      */   {
/* 1387 */     File[] destinations = new File[dms.length];
/* 1388 */     if (dms.length == 1) {
/* 1389 */       FileDialog fd = new FileDialog(parentShell, 8192);
/* 1390 */       fd.setFileName(dms[0].getTorrentFileName());
/* 1391 */       String path = fd.open();
/* 1392 */       if (path == null) {
/* 1393 */         return;
/*      */       }
/* 1395 */       destinations[0] = new File(path);
/*      */     } else {
/* 1397 */       DirectoryDialog dd = new DirectoryDialog(parentShell, 8192);
/* 1398 */       String path = dd.open();
/* 1399 */       if (path == null) {
/* 1400 */         return;
/*      */       }
/* 1402 */       for (int i = 0; i < dms.length; i++) {
/* 1403 */         destinations[i] = new File(path, new File(dms[i].getTorrentFileName()).getName());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1408 */     int i = 0;
/*      */     try {
/* 1410 */       for (; i < dms.length; i++) {
/* 1411 */         File target = destinations[i];
/* 1412 */         if (target.exists()) {
/* 1413 */           MessageBox mb = new MessageBox(parentShell, 196);
/*      */           
/* 1415 */           mb.setText(MessageText.getString("exportTorrentWizard.process.outputfileexists.title"));
/* 1416 */           mb.setMessage(MessageText.getString("exportTorrentWizard.process.outputfileexists.message") + "\n" + destinations[i].getName());
/*      */           
/*      */ 
/* 1419 */           int result = mb.open();
/* 1420 */           if (result == 128) {
/* 1421 */             return;
/*      */           }
/*      */           
/* 1424 */           if (!target.delete()) {
/* 1425 */             throw new Exception("Failed to delete file");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1431 */         TorrentUtils.copyToFile(dms[i].getDownloadState().getTorrent(), target);
/*      */         
/*      */ 
/* 1434 */         TOTorrent dest = TOTorrentFactory.deserialiseFromBEncodedFile(target);
/* 1435 */         dest.removeAdditionalProperties();
/* 1436 */         dest.serialiseToBEncodedFile(target);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1440 */       Logger.log(new LogAlert(dms[i], false, "Torrent export failed", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected static void pauseDownloadsFor(DownloadManager[] dms)
/*      */   {
/* 1447 */     List<DownloadManager> dms_to_pause = new ArrayList();
/*      */     
/* 1449 */     for (int i = 0; i < dms.length; i++)
/*      */     {
/* 1451 */       DownloadManager dm = dms[i];
/*      */       
/* 1453 */       if (ManagerUtils.isPauseable(dm))
/*      */       {
/* 1455 */         dms_to_pause.add(dm);
/*      */       }
/*      */     }
/*      */     
/* 1459 */     if (dms_to_pause.size() == 0)
/*      */     {
/* 1461 */       return;
/*      */     }
/*      */     
/* 1464 */     String text = MessageText.getString("dialog.pause.for.period.text");
/*      */     
/* 1466 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("dialog.pause.for.period.title", "!" + text + "!");
/*      */     
/*      */ 
/* 1469 */     int def = COConfigurationManager.getIntParameter("pause.for.period.default", 10);
/*      */     
/*      */ 
/* 1472 */     entryWindow.setPreenteredText(String.valueOf(def), false);
/*      */     
/* 1474 */     entryWindow.prompt(new UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 1476 */         if (!entryWindow.hasSubmittedInput())
/*      */         {
/* 1478 */           return;
/*      */         }
/*      */         
/* 1481 */         String sReturn = entryWindow.getSubmittedInput();
/*      */         
/* 1483 */         if (sReturn == null)
/*      */         {
/* 1485 */           return;
/*      */         }
/*      */         
/* 1488 */         int mins = -1;
/*      */         
/*      */         try
/*      */         {
/* 1492 */           mins = Integer.valueOf(sReturn).intValue();
/*      */         }
/*      */         catch (NumberFormatException er) {}
/*      */         
/*      */ 
/*      */ 
/* 1498 */         if (mins <= 0)
/*      */         {
/* 1500 */           MessageBox mb = new MessageBox(Utils.findAnyShell(), 33);
/*      */           
/*      */ 
/* 1503 */           mb.setText(MessageText.getString("MyTorrentsView.dialog.NumberError.title"));
/* 1504 */           mb.setMessage(MessageText.getString("MyTorrentsView.dialog.NumberError.text"));
/*      */           
/* 1506 */           mb.open();
/*      */           
/* 1508 */           return;
/*      */         }
/*      */         
/* 1511 */         COConfigurationManager.setParameter("pause.for.period.default", mins);
/*      */         
/*      */ 
/* 1514 */         ManagerUtils.asyncPauseForPeriod(this.val$dms_to_pause, mins * 60);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected static void addSpeedLimitsMenu(DownloadManager[] dms, Menu menu)
/*      */   {
/* 1521 */     AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
/*      */     
/* 1523 */     SpeedLimitHandler slh = SpeedLimitHandler.getSingleton(azureus_core);
/*      */     
/* 1525 */     boolean all_have_limit = true;
/*      */     
/* 1527 */     Set<String> common_profiles = new HashSet();
/*      */     
/* 1529 */     final List<byte[]> dm_hashes = new ArrayList();
/*      */     
/* 1531 */     for (int i = 0; i < dms.length; i++) {
/* 1532 */       DownloadManager dm = dms[i];
/*      */       
/* 1534 */       int maxul = dm.getStats().getUploadRateLimitBytesPerSecond();
/* 1535 */       int maxdl = dm.getStats().getDownloadRateLimitBytesPerSecond();
/*      */       
/* 1537 */       if ((maxul == 0) && (maxdl == 0))
/*      */       {
/* 1539 */         all_have_limit = false;
/*      */       }
/*      */       
/* 1542 */       TOTorrent t = dm.getTorrent();
/*      */       
/* 1544 */       if (t == null)
/*      */       {
/* 1546 */         common_profiles.clear();
/*      */       }
/*      */       else {
/*      */         try
/*      */         {
/* 1551 */           byte[] hash = t.getHash();
/*      */           
/* 1553 */           dm_hashes.add(hash);
/*      */           
/* 1555 */           List<String> profs = slh.getProfilesForDownload(hash);
/*      */           
/* 1557 */           if (i == 0)
/*      */           {
/* 1559 */             common_profiles.addAll(profs);
/*      */           }
/*      */           else
/*      */           {
/* 1563 */             common_profiles.retainAll(profs);
/*      */           }
/*      */         }
/*      */         catch (TOTorrentException e) {
/* 1567 */           Debug.out(e);
/*      */           
/* 1569 */           common_profiles.clear();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1574 */     List<String> profiles = slh.getProfileNames();
/*      */     
/*      */ 
/*      */ 
/* 1578 */     Menu add_to_prof_menu = new Menu(menu.getShell(), 4);
/*      */     
/* 1580 */     MenuItem add_to_prof_item = new MenuItem(menu, 64);
/* 1581 */     add_to_prof_item.setMenu(add_to_prof_menu);
/*      */     
/* 1583 */     Messages.setLanguageText(add_to_prof_item, "MyTorrentsView.menu.sl_add_to_prof");
/*      */     
/*      */ 
/* 1586 */     if (!all_have_limit)
/*      */     {
/* 1588 */       add_to_prof_item.setEnabled(false);
/*      */     }
/*      */     else
/*      */     {
/* 1592 */       for (final String p : profiles)
/*      */       {
/* 1594 */         MenuItem addItem = new MenuItem(add_to_prof_menu, 8);
/* 1595 */         addItem.setText(p);
/*      */         
/* 1597 */         addItem.addListener(13, new Listener() {
/*      */           public void handleEvent(Event arg0) {
/* 1599 */             this.val$slh.addDownloadsToProfile(p, dm_hashes);
/*      */             
/* 1601 */             MenuFactory.showText("MainWindow.menu.speed_limits.info.title", MessageText.getString("MainWindow.menu.speed_limits.info.prof", new String[] { p }), this.val$slh.getProfile(p));
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1613 */     Menu remove_from_prof_menu = new Menu(menu.getShell(), 4);
/*      */     
/* 1615 */     MenuItem remove_from_prof_item = new MenuItem(menu, 64);
/* 1616 */     remove_from_prof_item.setMenu(remove_from_prof_menu);
/*      */     
/* 1618 */     Messages.setLanguageText(remove_from_prof_item, "MyTorrentsView.menu.sl_remove_from_prof");
/*      */     
/*      */ 
/* 1621 */     if (common_profiles.isEmpty())
/*      */     {
/* 1623 */       remove_from_prof_item.setEnabled(false);
/*      */     }
/*      */     else
/*      */     {
/* 1627 */       for (final String p : common_profiles)
/*      */       {
/* 1629 */         MenuItem addItem = new MenuItem(remove_from_prof_menu, 8);
/* 1630 */         addItem.setText(p);
/*      */         
/* 1632 */         addItem.addListener(13, new Listener() {
/*      */           public void handleEvent(Event arg0) {
/* 1634 */             this.val$slh.removeDownloadsFromProfile(p, dm_hashes);
/*      */             
/* 1636 */             MenuFactory.showText("MainWindow.menu.speed_limits.info.title", MessageText.getString("MainWindow.menu.speed_limits.info.prof", new String[] { p }), this.val$slh.getProfile(p));
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static void addTrackerTorrentMenu(final Menu menuTracker, final DownloadManager[] dms, boolean changeUrl, boolean manualUpdate, boolean allStopped, final boolean use_open_containing_folder)
/*      */   {
/* 1650 */     boolean hasSelection = dms.length > 0;
/*      */     
/* 1652 */     MenuItem itemChangeTracker = new MenuItem(menuTracker, 8);
/* 1653 */     Messages.setLanguageText(itemChangeTracker, "MyTorrentsView.menu.changeTracker");
/*      */     
/* 1655 */     Utils.setMenuItemImage(itemChangeTracker, "add_tracker");
/* 1656 */     itemChangeTracker.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/* 1658 */         if (dms.length > 0) {
/* 1659 */           new TrackerChangerWindow(dms);
/*      */         }
/*      */       }
/* 1662 */     });
/* 1663 */     itemChangeTracker.setEnabled(changeUrl);
/*      */     
/*      */ 
/*      */ 
/* 1667 */     MenuItem itemEditTracker = new MenuItem(menuTracker, 8);
/* 1668 */     Messages.setLanguageText(itemEditTracker, "MyTorrentsView.menu.editTracker");
/* 1669 */     Utils.setMenuItemImage(itemEditTracker, "edit_trackers");
/* 1670 */     itemEditTracker.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/* 1672 */         Map<String, List<DownloadManager>> same_map = new HashMap();
/*      */         
/* 1674 */         for (DownloadManager dm : dms)
/*      */         {
/* 1676 */           TOTorrent torrent = dm.getTorrent();
/*      */           
/* 1678 */           if (torrent != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1683 */             List<List<String>> group = TorrentUtils.announceGroupsToList(torrent);
/*      */             
/* 1685 */             String str = "";
/*      */             
/* 1687 */             for (List<String> l : group) {
/* 1688 */               str = str + "[[";
/*      */               
/* 1690 */               for (String s : l) {
/* 1691 */                 str = str + s + ", ";
/*      */               }
/*      */             }
/*      */             
/* 1695 */             List<DownloadManager> dl = (List)same_map.get(str);
/*      */             
/* 1697 */             if (dl == null)
/*      */             {
/* 1699 */               dl = new ArrayList();
/*      */               
/* 1701 */               same_map.put(str, dl);
/*      */             }
/*      */             
/* 1704 */             dl.add(dm);
/*      */           }
/*      */         }
/* 1707 */         for (final List<DownloadManager> set : same_map.values())
/*      */         {
/* 1709 */           TOTorrent torrent = ((DownloadManager)set.get(0)).getTorrent();
/*      */           
/* 1711 */           List<List<String>> group = TorrentUtils.announceGroupsToList(torrent);
/*      */           
/* 1713 */           new MultiTrackerEditor(null, null, group, new TrackerEditorListener()
/*      */           {
/*      */             public void trackersChanged(String str, String str2, List<List<String>> group)
/*      */             {
/* 1717 */               for (DownloadManager dm : set)
/*      */               {
/* 1719 */                 TOTorrent torrent = dm.getTorrent();
/*      */                 
/* 1721 */                 TorrentUtils.listToAnnounceGroups(group, torrent);
/*      */                 try
/*      */                 {
/* 1724 */                   TorrentUtils.writeToFile(torrent);
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/* 1728 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */                 
/* 1731 */                 if (dm.getTrackerClient() != null)
/*      */                 {
/* 1733 */                   dm.getTrackerClient().resetTrackerUrl(true); } } } }, true, true);
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 1741 */     });
/* 1742 */     itemEditTracker.setEnabled(hasSelection);
/*      */     
/*      */ 
/*      */ 
/* 1746 */     MenuItem itemEditTrackerMerged = new MenuItem(menuTracker, 8);
/* 1747 */     Messages.setLanguageText(itemEditTrackerMerged, "MyTorrentsView.menu.editTrackerMerge");
/*      */     
/* 1749 */     itemEditTrackerMerged.addListener(13, new ListenerDMTask(dms)
/*      */     {
/*      */       public void run(final DownloadManager[] dms) {
/* 1752 */         List<List<String>> merged_trackers = new ArrayList();
/*      */         
/* 1754 */         Set<String> added = new HashSet();
/*      */         
/* 1756 */         for (DownloadManager dm : dms)
/*      */         {
/* 1758 */           TOTorrent torrent = dm.getTorrent();
/*      */           
/* 1760 */           if (torrent != null)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1765 */             List<List<String>> group = TorrentUtils.announceGroupsToList(torrent);
/*      */             
/* 1767 */             for (List<String> set : group)
/*      */             {
/* 1769 */               List<String> rem = new ArrayList();
/*      */               
/* 1771 */               for (String url_str : set) {
/*      */                 try
/*      */                 {
/* 1774 */                   URL url = new URL(url_str);
/*      */                   
/* 1776 */                   if (!TorrentUtils.isDecentralised(url))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 1781 */                     if (!added.contains(url_str))
/*      */                     {
/* 1783 */                       added.add(url_str);
/*      */                       
/* 1785 */                       rem.add(url_str);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               
/* 1792 */               if (rem.size() > 0)
/*      */               {
/* 1794 */                 merged_trackers.add(rem);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1799 */         new MultiTrackerEditor(null, null, merged_trackers, new TrackerEditorListener()
/*      */         {
/*      */           public void trackersChanged(String str, String str2, List<List<String>> group)
/*      */           {
/* 1803 */             for (DownloadManager dm : dms)
/*      */             {
/* 1805 */               TOTorrent torrent = dm.getTorrent();
/*      */               
/* 1807 */               TorrentUtils.listToAnnounceGroups(group, torrent);
/*      */               try
/*      */               {
/* 1810 */                 TorrentUtils.writeToFile(torrent);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1814 */                 Debug.printStackTrace(e);
/*      */               }
/*      */               
/* 1817 */               if (dm.getTrackerClient() != null)
/*      */               {
/* 1819 */                 dm.getTrackerClient().resetTrackerUrl(true); } } } }, true, true);
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1826 */     });
/* 1827 */     itemEditTrackerMerged.setEnabled(dms.length > 1);
/*      */     
/*      */ 
/*      */ 
/* 1831 */     MenuItem itemEditWebSeeds = new MenuItem(menuTracker, 8);
/* 1832 */     Messages.setLanguageText(itemEditWebSeeds, "MyTorrentsView.menu.editWebSeeds");
/*      */     
/* 1834 */     itemEditWebSeeds.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(final DownloadManager[] dms) {
/* 1836 */         final TOTorrent torrent = dms[0].getTorrent();
/*      */         
/* 1838 */         if (torrent == null)
/*      */         {
/* 1840 */           return;
/*      */         }
/*      */         
/* 1843 */         List getright = getURLList(torrent, "url-list");
/* 1844 */         List webseeds = getURLList(torrent, "httpseeds");
/*      */         
/* 1846 */         Map ws = new HashMap();
/*      */         
/* 1848 */         ws.put("getright", getright);
/* 1849 */         ws.put("webseeds", webseeds);
/*      */         
/* 1851 */         ws = BDecoder.decodeStrings(ws);
/*      */         
/* 1853 */         new WebSeedsEditor(null, ws, new WebSeedsEditorListener()
/*      */         {
/*      */           public void webSeedsChanged(String oldName, String newName, Map ws)
/*      */           {
/*      */             try {
/* 1858 */               ws = BDecoder.decode(BEncoder.encode(ws));
/*      */               
/* 1860 */               List getright = (List)ws.get("getright");
/*      */               
/* 1862 */               if ((getright == null) || (getright.size() == 0))
/*      */               {
/* 1864 */                 torrent.removeAdditionalProperty("url-list");
/*      */               }
/*      */               else
/*      */               {
/* 1868 */                 torrent.setAdditionalListProperty("url-list", getright);
/*      */               }
/*      */               
/* 1871 */               List webseeds = (List)ws.get("webseeds");
/*      */               
/* 1873 */               if ((webseeds == null) || (webseeds.size() == 0))
/*      */               {
/* 1875 */                 torrent.removeAdditionalProperty("httpseeds");
/*      */               }
/*      */               else
/*      */               {
/* 1879 */                 torrent.setAdditionalListProperty("httpseeds", webseeds);
/*      */               }
/*      */               
/* 1882 */               PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(ExternalSeedPlugin.class);
/*      */               
/*      */ 
/* 1885 */               if (pi != null)
/*      */               {
/* 1887 */                 ExternalSeedPlugin ext_seed_plugin = (ExternalSeedPlugin)pi.getPlugin();
/*      */                 
/* 1889 */                 ext_seed_plugin.downloadChanged(PluginCoreUtils.wrap(dms[0]));
/*      */               }
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1894 */               Debug.printStackTrace(e); } } }, true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       protected List getURLList(TOTorrent torrent, String key)
/*      */       {
/* 1902 */         Object obj = torrent.getAdditionalProperty(key);
/*      */         
/* 1904 */         if ((obj instanceof byte[]))
/*      */         {
/* 1906 */           List l = new ArrayList();
/*      */           
/* 1908 */           l.add(obj);
/*      */           
/* 1910 */           return l;
/*      */         }
/* 1912 */         if ((obj instanceof List))
/*      */         {
/* 1914 */           return (List)obj;
/*      */         }
/*      */         
/*      */ 
/* 1918 */         return new ArrayList();
/*      */       }
/*      */       
/*      */ 
/* 1922 */     });
/* 1923 */     itemEditWebSeeds.setEnabled(dms.length == 1);
/*      */     
/*      */ 
/*      */ 
/* 1927 */     MenuItem itemManualUpdate = new MenuItem(menuTracker, 8);
/* 1928 */     Messages.setLanguageText(itemManualUpdate, "GeneralView.label.trackerurlupdate");
/*      */     
/*      */ 
/* 1931 */     itemManualUpdate.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager dm) {
/* 1933 */         dm.requestTrackerAnnounce(false);
/*      */       }
/* 1935 */     });
/* 1936 */     itemManualUpdate.setEnabled(manualUpdate);
/*      */     
/* 1938 */     boolean scrape_enabled = COConfigurationManager.getBooleanParameter("Tracker Client Scrape Enable");
/*      */     
/* 1940 */     boolean scrape_stopped = COConfigurationManager.getBooleanParameter("Tracker Client Scrape Stopped Enable");
/*      */     
/* 1942 */     boolean manualScrape = (!scrape_enabled) || ((!scrape_stopped) && (allStopped));
/*      */     
/*      */ 
/* 1945 */     MenuItem itemManualScrape = new MenuItem(menuTracker, 8);
/* 1946 */     Messages.setLanguageText(itemManualScrape, "GeneralView.label.trackerscrapeupdate");
/*      */     
/*      */ 
/* 1949 */     itemManualScrape.addListener(13, new ListenerDMTask(dms, true, true) {
/*      */       public void run(DownloadManager dm) {
/* 1951 */         dm.requestTrackerScrape(true);
/*      */       }
/* 1953 */     });
/* 1954 */     itemManualScrape.setEnabled(manualScrape);
/*      */     
/*      */ 
/*      */ 
/* 1958 */     MenuItem itemTorrentDL = new MenuItem(menuTracker, 8);
/* 1959 */     Messages.setLanguageText(itemTorrentDL, "MyTorrentsView.menu.torrent.dl");
/* 1960 */     itemTorrentDL.addListener(13, new ListenerDMTask(dms, false)
/*      */     {
/*      */ 
/*      */       public void run(DownloadManager dm)
/*      */       {
/* 1965 */         TOTorrent torrent = dm.getTorrent();
/*      */         
/* 1967 */         String link = null;
/*      */         String content;
/* 1969 */         String content; if (torrent == null)
/*      */         {
/* 1971 */           content = "Torrent not available";
/*      */         }
/*      */         else
/*      */         {
/* 1975 */           link = TorrentUtils.getObtainedFrom(torrent);
/*      */           
/* 1977 */           if (link != null) {
/*      */             try
/*      */             {
/* 1980 */               new URL(link);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1984 */               link = null;
/*      */             }
/*      */           }
/*      */           String content;
/* 1988 */           if (link != null) {
/*      */             String content;
/* 1990 */             if (link.toLowerCase().startsWith("magnet:"))
/*      */             {
/* 1992 */               link = UrlUtils.getMagnetURI(dm);
/*      */               
/* 1994 */               content = "Torrent's magnet link:\r\n\r\n\t" + link;
/*      */             }
/*      */             else
/*      */             {
/* 1998 */               content = "Torrent was obtained from\r\n\r\n\t" + link;
/*      */             }
/*      */           } else {
/*      */             String content;
/* 2002 */             if (TorrentUtils.isReallyPrivate(torrent))
/*      */             {
/* 2004 */               content = "Origin of torrent unknown and it is private so a magnet URI can't be used - sorry!";
/*      */             }
/*      */             else
/*      */             {
/* 2008 */               link = UrlUtils.getMagnetURI(dm);
/*      */               
/* 2010 */               content = "Origin unavailable but magnet URI may work:\r\n\r\n\t" + link;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 2016 */         if (link != null)
/*      */         {
/* 2018 */           ClipboardCopy.copyToClipBoard(link);
/*      */           
/* 2020 */           content = content + "\r\n\r\nLink copied to clipboard";
/*      */         }
/*      */         
/* 2023 */         TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("MyTorrentsView.menu.torrent.dl") + ": " + dm.getDisplayName(), null, content, false);
/*      */       }
/*      */       
/*      */ 
/* 2027 */     });
/* 2028 */     itemTorrentDL.setEnabled(dms.length == 1);
/*      */     
/*      */ 
/*      */ 
/* 2032 */     MenuItem itemTorrentSource = new MenuItem(menuTracker, 8);
/* 2033 */     Messages.setLanguageText(itemTorrentSource, "MyTorrentsView.menu.torrent.set.source");
/* 2034 */     itemTorrentSource.addListener(13, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2037 */         final TOTorrent torrent = this.val$dms[0].getTorrent();
/* 2038 */         if (torrent == null) {
/* 2039 */           return;
/*      */         }
/* 2041 */         String msg_key_prefix = "MyTorrentsView.menu.edit_source.";
/* 2042 */         SimpleTextEntryWindow text_entry = new SimpleTextEntryWindow();
/*      */         
/* 2044 */         text_entry.setParentShell(menuTracker.getShell());
/* 2045 */         text_entry.setTitle(msg_key_prefix + "title");
/* 2046 */         text_entry.setMessage(msg_key_prefix + "message");
/* 2047 */         text_entry.setPreenteredText(TorrentUtils.getObtainedFrom(torrent), false);
/* 2048 */         text_entry.setWidthHint(500);
/* 2049 */         text_entry.prompt(new UIInputReceiverListener() {
/*      */           public void UIInputReceiverClosed(UIInputReceiver text_entry) {
/* 2051 */             if (text_entry.hasSubmittedInput()) {
/* 2052 */               TorrentUtils.setObtainedFrom(torrent, text_entry.getSubmittedInput());
/*      */               try {
/* 2054 */                 TorrentUtils.writeToFile(torrent);
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/* 2061 */     });
/* 2062 */     itemTorrentSource.setEnabled(dms.length == 1);
/*      */     
/*      */ 
/* 2065 */     MenuItem itemTorrentThumb = new MenuItem(menuTracker, 8);
/* 2066 */     Messages.setLanguageText(itemTorrentThumb, "MyTorrentsView.menu.torrent.set.thumb");
/* 2067 */     itemTorrentThumb.addListener(13, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2070 */         FileDialog fDialog = new FileDialog(this.val$menuTracker.getShell(), 4098);
/*      */         
/* 2072 */         fDialog.setText(MessageText.getString("MainWindow.dialog.choose.thumb"));
/* 2073 */         String path = fDialog.open();
/* 2074 */         if (path == null) {
/* 2075 */           return;
/*      */         }
/* 2077 */         File file = new File(path);
/*      */         try
/*      */         {
/* 2080 */           byte[] thumbnail = FileUtil.readFileAsByteArray(file);
/*      */           
/* 2082 */           String name = file.getName();
/*      */           
/* 2084 */           int pos = name.lastIndexOf(".");
/*      */           
/*      */           String ext;
/*      */           String ext;
/* 2088 */           if (pos != -1)
/*      */           {
/* 2090 */             ext = name.substring(pos + 1);
/*      */           }
/*      */           else
/*      */           {
/* 2094 */             ext = "";
/*      */           }
/*      */           
/* 2097 */           String type = HTTPUtils.guessContentTypeFromFileType(ext);
/*      */           
/* 2099 */           for (DownloadManager dm : dms) {
/*      */             try
/*      */             {
/* 2102 */               TOTorrent torrent = dm.getTorrent();
/*      */               
/* 2104 */               PlatformTorrentUtils.setContentThumbnail(torrent, thumbnail, type);
/*      */ 
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 2112 */           Debug.out(e);
/*      */         }
/*      */         
/*      */       }
/* 2116 */     });
/* 2117 */     itemTorrentThumb.setEnabled(hasSelection);
/*      */     
/*      */ 
/*      */ 
/* 2121 */     MenuItem itemTorrentExplore = new MenuItem(menuTracker, 8);
/* 2122 */     Messages.setLanguageText(itemTorrentExplore, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*      */     
/* 2124 */     itemTorrentExplore.addListener(13, new ListenerDMTask(dms, false) {
/*      */       public void run(DownloadManager dm) {
/* 2126 */         ManagerUtils.open(new File(dm.getTorrentFileName()), use_open_containing_folder);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected static void moveTorrentFile(Shell shell, DownloadManager[] dms)
/*      */   {
/* 2133 */     if ((dms != null) && (dms.length > 0))
/*      */     {
/* 2135 */       DirectoryDialog dd = new DirectoryDialog(shell);
/* 2136 */       String filter_path = TorrentOpener.getFilterPathTorrent();
/*      */       
/*      */ 
/*      */ 
/* 2140 */       if ((filter_path == null) || (filter_path.trim().length() == 0)) {
/* 2141 */         filter_path = new File(dms[0].getTorrentFileName()).getParent();
/*      */       }
/*      */       
/* 2144 */       dd.setFilterPath(filter_path);
/*      */       
/* 2146 */       dd.setText(MessageText.getString("MyTorrentsView.menu.movedata.dialog"));
/*      */       
/* 2148 */       String path = dd.open();
/*      */       
/* 2150 */       if (path != null)
/*      */       {
/* 2152 */         File target = new File(path);
/*      */         
/* 2154 */         TorrentOpener.setFilterPathTorrent(target.toString());
/*      */         
/* 2156 */         for (int i = 0; i < dms.length; i++) {
/*      */           try
/*      */           {
/* 2159 */             dms[i].moveTorrentFile(target);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2163 */             Logger.log(new LogAlert(dms[i], true, "Download torrent move operation failed", e));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected static void moveDataFiles(Shell shell, DownloadManager[] dms)
/*      */   {
/* 2172 */     if ((dms != null) && (dms.length > 0))
/*      */     {
/* 2174 */       DirectoryDialog dd = new DirectoryDialog(shell);
/*      */       
/* 2176 */       String filter_path = TorrentOpener.getFilterPathData();
/*      */       
/*      */ 
/*      */ 
/* 2180 */       if ((filter_path == null) || (filter_path.trim().length() == 0)) {
/* 2181 */         filter_path = new File(dms[0].getTorrentFileName()).getParent();
/*      */       }
/*      */       
/* 2184 */       dd.setFilterPath(filter_path);
/*      */       
/* 2186 */       dd.setText(MessageText.getString("MyTorrentsView.menu.movedata.dialog"));
/*      */       
/* 2188 */       String path = dd.open();
/*      */       
/* 2190 */       if (path != null)
/*      */       {
/* 2192 */         TorrentOpener.setFilterPathData(path);
/*      */         
/* 2194 */         File target = new File(path);
/*      */         
/* 2196 */         for (int i = 0; i < dms.length; i++) {
/*      */           try
/*      */           {
/* 2199 */             dms[i].moveDataFilesLive(target);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2203 */             Logger.log(new LogAlert(dms[i], true, "Download data move operation failed", e));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void repositionManual(final TableView tv, final DownloadManager[] dms, final Shell shell, boolean isSeedingView)
/*      */   {
/* 2214 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("MyTorrentsView.dialog.setPosition.title", "MyTorrentsView.dialog.setPosition.text");
/*      */     
/*      */ 
/* 2217 */     entryWindow.prompt(new UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 2219 */         if (!entryWindow.hasSubmittedInput()) {
/* 2220 */           return;
/*      */         }
/* 2222 */         String sReturn = entryWindow.getSubmittedInput();
/*      */         
/* 2224 */         if (sReturn == null) {
/* 2225 */           return;
/*      */         }
/* 2227 */         int newPosition = -1;
/*      */         try {
/* 2229 */           newPosition = Integer.valueOf(sReturn).intValue();
/*      */         }
/*      */         catch (NumberFormatException er) {}
/*      */         
/*      */ 
/* 2234 */         AzureusCore azureus_core = AzureusCoreFactory.getSingleton();
/* 2235 */         if (azureus_core == null) {
/* 2236 */           return;
/*      */         }
/* 2238 */         int size = azureus_core.getGlobalManager().downloadManagerCount(this.val$isSeedingView);
/*      */         
/* 2240 */         if (newPosition > size) {
/* 2241 */           newPosition = size;
/*      */         }
/* 2243 */         if (newPosition <= 0) {
/* 2244 */           MessageBox mb = new MessageBox(shell, 33);
/* 2245 */           mb.setText(MessageText.getString("MyTorrentsView.dialog.NumberError.title"));
/* 2246 */           mb.setMessage(MessageText.getString("MyTorrentsView.dialog.NumberError.text"));
/*      */           
/* 2248 */           mb.open();
/* 2249 */           return;
/*      */         }
/*      */         
/* 2252 */         TorrentUtil.moveSelectedTorrentsTo(tv, dms, newPosition);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   protected static void addCategorySubMenu(DownloadManager[] dms, Menu menuCategory, final Composite composite)
/*      */   {
/* 2259 */     MenuItem[] items = menuCategory.getItems();
/*      */     
/* 2261 */     for (int i = 0; i < items.length; i++) {
/* 2262 */       items[i].dispose();
/*      */     }
/*      */     
/* 2265 */     Category[] categories = CategoryManager.getCategories();
/* 2266 */     Arrays.sort(categories);
/*      */     
/*      */ 
/* 2269 */     boolean allow_category_selection = categories.length > 0;
/* 2270 */     if (allow_category_selection) {
/* 2271 */       boolean user_category_found = false;
/* 2272 */       for (i = 0; i < categories.length; i++) {
/* 2273 */         if (categories[i].getType() == 0) {
/* 2274 */           user_category_found = true;
/* 2275 */           break;
/*      */         }
/*      */       }
/*      */       
/* 2279 */       allow_category_selection = user_category_found;
/*      */     }
/*      */     
/* 2282 */     if (allow_category_selection) {
/* 2283 */       final Category catUncat = CategoryManager.getCategory(2);
/* 2284 */       if (catUncat != null) {
/* 2285 */         MenuItem itemCategory = new MenuItem(menuCategory, 8);
/* 2286 */         Messages.setLanguageText(itemCategory, catUncat.getName());
/* 2287 */         itemCategory.addListener(13, new ListenerDMTask(dms) {
/*      */           public void run(DownloadManager dm) {
/* 2289 */             dm.getDownloadState().setCategory(catUncat);
/*      */           }
/*      */           
/* 2292 */         });
/* 2293 */         new MenuItem(menuCategory, 2);
/*      */       }
/*      */       
/* 2296 */       for (i = 0; i < categories.length; i++) {
/* 2297 */         final Category category = categories[i];
/* 2298 */         if (category.getType() == 0) {
/* 2299 */           MenuItem itemCategory = new MenuItem(menuCategory, 8);
/* 2300 */           itemCategory.setText(category.getName());
/* 2301 */           itemCategory.addListener(13, new ListenerDMTask(dms) {
/*      */             public void run(DownloadManager dm) {
/* 2303 */               dm.getDownloadState().setCategory(category);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/* 2309 */       new MenuItem(menuCategory, 2);
/*      */     }
/*      */     
/* 2312 */     MenuItem itemAddCategory = new MenuItem(menuCategory, 8);
/* 2313 */     Messages.setLanguageText(itemAddCategory, "MyTorrentsView.menu.setCategory.add");
/*      */     
/*      */ 
/* 2316 */     itemAddCategory.addListener(13, new ListenerDMTask(dms) {
/*      */       public void run(DownloadManager[] dms) {
/* 2318 */         CategoryAdderWindow adderWindow = new CategoryAdderWindow(composite.getDisplay());
/*      */         
/* 2320 */         Category newCategory = adderWindow.getNewCategory();
/* 2321 */         if (newCategory != null) {
/* 2322 */           TorrentUtil.assignToCategory(dms, newCategory);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static void moveSelectedTorrentsTo(TableView tv, DownloadManager[] dms, int iNewPos)
/*      */   {
/* 2330 */     if ((dms == null) || (dms.length == 0)) {
/* 2331 */       return;
/*      */     }
/*      */     
/* 2334 */     TableColumnCore sortColumn = tv == null ? null : tv.getSortColumn();
/* 2335 */     boolean isSortAscending = sortColumn == null ? true : sortColumn.isSortAscending();
/*      */     
/*      */ 
/* 2338 */     for (int i = 0; i < dms.length; i++) {
/* 2339 */       DownloadManager dm = dms[i];
/* 2340 */       int iOldPos = dm.getPosition();
/*      */       
/* 2342 */       dm.getGlobalManager().moveTo(dm, iNewPos);
/* 2343 */       if (isSortAscending) {
/* 2344 */         if (iOldPos > iNewPos) {
/* 2345 */           iNewPos++;
/*      */         }
/* 2347 */       } else if (iOldPos < iNewPos) {
/* 2348 */         iNewPos--;
/*      */       }
/*      */     }
/*      */     
/* 2352 */     if (tv != null) {
/* 2353 */       boolean bForceSort = sortColumn.getName().equals("#");
/* 2354 */       tv.columnInvalidate("#");
/* 2355 */       tv.refreshTable(bForceSort);
/*      */     }
/*      */   }
/*      */   
/*      */   protected static void changeDirSelectedTorrents(DownloadManager[] dms, Shell shell)
/*      */   {
/* 2361 */     if (dms.length <= 0) {
/* 2362 */       return;
/*      */     }
/* 2364 */     String sDefPath = COConfigurationManager.getStringParameter("Default save path");
/*      */     
/* 2366 */     if (sDefPath.length() > 0) {
/* 2367 */       File f = new File(sDefPath);
/*      */       
/* 2369 */       if (!f.exists()) {
/* 2370 */         FileUtil.mkdirs(f);
/*      */       }
/*      */     }
/*      */     
/* 2374 */     DirectoryDialog dDialog = new DirectoryDialog(shell, 131072);
/* 2375 */     dDialog.setFilterPath(sDefPath);
/* 2376 */     dDialog.setMessage(MessageText.getString("MainWindow.dialog.choose.savepath"));
/* 2377 */     String sSavePath = dDialog.open();
/* 2378 */     if (sSavePath != null) {
/* 2379 */       File fSavePath = new File(sSavePath);
/* 2380 */       for (int i = 0; i < dms.length; i++) {
/* 2381 */         DownloadManager dm = dms[i];
/*      */         
/* 2383 */         String displayName = dm.getDisplayName();
/*      */         
/* 2385 */         int state = dm.getState();
/* 2386 */         if ((state != 100) && 
/* 2387 */           (!dm.filesExist(true))) {
/* 2388 */           state = 100;
/*      */         }
/*      */         
/*      */ 
/* 2392 */         if (state == 100)
/*      */         {
/* 2394 */           File oldSaveLocation = dm.getSaveLocation();
/* 2395 */           dm.setTorrentSaveDir(sSavePath);
/*      */           
/*      */ 
/* 2398 */           boolean found = dm.filesExist(true);
/* 2399 */           if ((!found) && (dm.getTorrent() != null) && (!dm.getTorrent().isSimpleTorrent()))
/*      */           {
/* 2401 */             String parentPath = fSavePath.getParent();
/* 2402 */             if (parentPath != null) {
/* 2403 */               dm.setTorrentSaveDir(parentPath);
/* 2404 */               found = dm.filesExist(true);
/* 2405 */               if (!found) {
/* 2406 */                 dm.setTorrentSaveDir(parentPath, fSavePath.getName());
/*      */                 
/* 2408 */                 found = dm.filesExist(true);
/* 2409 */                 if (!found) {
/* 2410 */                   dm.setTorrentSaveDir(sSavePath, dm.getDisplayName());
/*      */                   
/* 2412 */                   found = dm.filesExist(true);
/* 2413 */                   if (!found) {
/* 2414 */                     dm.setTorrentSaveDir(sSavePath);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 2421 */           if (found) {
/* 2422 */             dm.stopIt(70, false, false);
/*      */             
/* 2424 */             ManagerUtils.queue(dm, shell);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void runDataSources(Object[] datasources)
/*      */   {
/* 2435 */     for (int i = datasources.length - 1; i >= 0; i--) {
/* 2436 */       Object ds = PluginCoreUtils.convert(datasources[i], true);
/* 2437 */       if ((ds instanceof DownloadManager)) {
/* 2438 */         DownloadManager dm = (DownloadManager)ds;
/* 2439 */         ManagerUtils.run(dm);
/* 2440 */       } else if ((ds instanceof DiskManagerFileInfo)) {
/* 2441 */         DiskManagerFileInfo info = (DiskManagerFileInfo)ds;
/* 2442 */         Utils.launch(info);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void hostTorrents(Object[] download_managers) {
/* 2448 */     ListenerDMTask task = new ListenerDMTask(toDMS(download_managers), true, true)
/*      */     {
/*      */       public void run(DownloadManager dm) {
/* 2451 */         ManagerUtils.host(AzureusCoreFactory.getSingleton(), dm);
/*      */       }
/* 2453 */     };
/* 2454 */     task.go();
/* 2455 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 2456 */     if (uiFunctions != null) {
/* 2457 */       uiFunctions.getMDI().showEntryByID("MyTrackerView");
/*      */     }
/*      */   }
/*      */   
/*      */   public static void publishTorrents(Object[] download_managers)
/*      */   {
/* 2463 */     ListenerDMTask task = new ListenerDMTask(toDMS(download_managers), true, true)
/*      */     {
/*      */       public void run(DownloadManager dm) {
/* 2466 */         ManagerUtils.publish(AzureusCoreFactory.getSingleton(), dm);
/*      */       }
/* 2468 */     };
/* 2469 */     task.go();
/* 2470 */     UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 2471 */     if (uiFunctions != null) {
/* 2472 */       uiFunctions.getMDI().showEntryByID("MyTrackerView");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void removeDataSources(Object[] datasources)
/*      */   {
/* 2483 */     DownloadManager[] dms = toDMS(datasources);
/* 2484 */     removeDownloads(dms, null);
/* 2485 */     DiskManagerFileInfo[] fileInfos = toDMFI(datasources);
/* 2486 */     if (fileInfos.length > 0) {
/* 2487 */       FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_DELETE, Arrays.asList(fileInfos));
/*      */     }
/*      */     
/* 2490 */     DownloadStub.DownloadStubEx[] stubs = toDownloadStubs(datasources);
/* 2491 */     if (stubs.length > 0) {
/* 2492 */       removeDownloadStubs(stubs, null, false);
/*      */     }
/*      */   }
/*      */   
/*      */   public static boolean shouldStopGroup(Object[] datasources) {
/* 2497 */     DownloadManager[] dms = toDMS(datasources);
/* 2498 */     DiskManagerFileInfo[] dmfi = toDMFI(datasources);
/* 2499 */     if ((dms.length == 0) && (dmfi.length == 0)) {
/* 2500 */       return true;
/*      */     }
/* 2502 */     for (DownloadManager dm : dms) {
/* 2503 */       int state = dm.getState();
/* 2504 */       boolean stopped = (state == 70) || (state == 65);
/*      */       
/* 2506 */       if (!stopped) {
/* 2507 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 2511 */     for (DiskManagerFileInfo fileInfo : dmfi) {
/* 2512 */       if (!fileInfo.isSkipped()) {
/* 2513 */         return true;
/*      */       }
/*      */     }
/* 2516 */     return false;
/*      */   }
/*      */   
/*      */   public static void stopOrStartDataSources(Object[] datasources) {
/* 2520 */     DownloadManager[] dms = toDMS(datasources);
/* 2521 */     DiskManagerFileInfo[] dmfi = toDMFI(datasources);
/* 2522 */     if ((dms.length == 0) && (dmfi.length == 0)) {
/* 2523 */       return;
/*      */     }
/* 2525 */     boolean doStop = shouldStopGroup(datasources);
/* 2526 */     if (doStop) {
/* 2527 */       stopDataSources(datasources);
/*      */     } else {
/* 2529 */       queueDataSources(datasources, true);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void stopDataSources(Object[] datasources) {
/* 2534 */     DownloadManager[] dms = toDMS(datasources);
/* 2535 */     for (DownloadManager dm : dms) {
/* 2536 */       ManagerUtils.stop(dm, null);
/*      */     }
/* 2538 */     DiskManagerFileInfo[] fileInfos = toDMFI(datasources);
/* 2539 */     if (fileInfos.length > 0) {
/* 2540 */       FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_SKIPPED, Arrays.asList(fileInfos));
/*      */     }
/*      */   }
/*      */   
/*      */   public static void pauseDataSources(Object[] datasources)
/*      */   {
/* 2546 */     DownloadManager[] dms = toDMS(datasources);
/* 2547 */     for (DownloadManager dm : dms) {
/* 2548 */       ManagerUtils.pause(dm, null);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void queueDataSources(Object[] datasources, boolean startStoppedParents)
/*      */   {
/* 2554 */     DownloadManager[] dms = toDMS(datasources);
/* 2555 */     for (DownloadManager dm : dms) {
/* 2556 */       ManagerUtils.queue(dm, null);
/*      */     }
/* 2558 */     DiskManagerFileInfo[] fileInfos = toDMFI(datasources);
/* 2559 */     if (fileInfos.length > 0) {
/* 2560 */       FilesViewMenuUtil.changePriority(FilesViewMenuUtil.PRIORITY_NORMAL, Arrays.asList(fileInfos));
/*      */       
/*      */ 
/* 2563 */       if (startStoppedParents) {
/* 2564 */         for (DiskManagerFileInfo fileInfo : fileInfos) {
/* 2565 */           if (fileInfo.getDownloadManager().getState() == 70) {
/* 2566 */             ManagerUtils.queue(fileInfo.getDownloadManager(), null);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void resumeTorrents(Object[] download_managers) {
/* 2574 */     ListenerDMTask task = new ListenerDMTask(toDMS(download_managers)) {
/*      */       public void run(DownloadManager dm) {
/* 2576 */         ManagerUtils.start(dm);
/*      */       }
/* 2578 */     };
/* 2579 */     task.go();
/*      */   }
/*      */   
/*      */ 
/*      */   public static void assignToCategory(Object[] download_managers, final Category category)
/*      */   {
/* 2585 */     ListenerDMTask task = new ListenerDMTask(toDMS(download_managers)) {
/*      */       public void run(DownloadManager dm) {
/* 2587 */         dm.getDownloadState().setCategory(category);
/*      */       }
/* 2589 */     };
/* 2590 */     task.go();
/*      */   }
/*      */   
/*      */   public static void promptUserForComment(DownloadManager[] dms) {
/* 2594 */     if (dms.length == 0) {
/* 2595 */       return;
/*      */     }
/* 2597 */     DownloadManager dm = dms[0];
/*      */     
/*      */ 
/* 2600 */     String suggested = dm.getDownloadState().getUserComment();
/* 2601 */     String msg_key_prefix = "MyTorrentsView.menu.edit_comment.enter.";
/* 2602 */     SimpleTextEntryWindow text_entry = new SimpleTextEntryWindow();
/* 2603 */     text_entry.setTitle(msg_key_prefix + "title");
/* 2604 */     text_entry.setMessage(msg_key_prefix + "message");
/* 2605 */     text_entry.setPreenteredText(suggested, false);
/* 2606 */     text_entry.setMultiLine(true);
/* 2607 */     text_entry.prompt(new UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver text_entry) {
/* 2609 */         if (text_entry.hasSubmittedInput()) {
/* 2610 */           String value = text_entry.getSubmittedInput();
/* 2611 */           final String value_to_set = value.length() == 0 ? null : value;
/* 2612 */           ListenerDMTask task = new ListenerDMTask(this.val$dms) {
/*      */             public void run(DownloadManager dm) {
/* 2614 */               dm.getDownloadState().setUserComment(value_to_set);
/*      */             }
/* 2616 */           };
/* 2617 */           task.go();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void promptUserForDescription(DownloadManager[] dms)
/*      */   {
/* 2625 */     if (dms.length == 0) {
/* 2626 */       return;
/*      */     }
/* 2628 */     DownloadManager dm = dms[0];
/*      */     
/* 2630 */     String desc = null;
/*      */     try
/*      */     {
/* 2633 */       desc = PlatformTorrentUtils.getContentDescription(dm.getTorrent());
/*      */       
/* 2635 */       if (desc == null) {
/* 2636 */         desc = "";
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2641 */     String msg_key_prefix = "MyTorrentsView.menu.edit_description.enter.";
/* 2642 */     SimpleTextEntryWindow text_entry = new SimpleTextEntryWindow();
/* 2643 */     text_entry.setTitle(msg_key_prefix + "title");
/* 2644 */     text_entry.setMessage(msg_key_prefix + "message");
/* 2645 */     text_entry.setPreenteredText(desc, false);
/* 2646 */     text_entry.setMultiLine(true);
/* 2647 */     text_entry.setWidthHint(500);
/* 2648 */     text_entry.setLineHeight(16);
/* 2649 */     text_entry.prompt(new UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver text_entry) {
/* 2651 */         if (text_entry.hasSubmittedInput()) {
/* 2652 */           String value = text_entry.getSubmittedInput();
/* 2653 */           final String value_to_set = value.length() == 0 ? null : value;
/* 2654 */           ListenerDMTask task = new ListenerDMTask(this.val$dms) {
/*      */             public void run(DownloadManager dm) {
/* 2656 */               PlatformTorrentUtils.setContentDescription(dm.getTorrent(), value_to_set);
/*      */             }
/* 2658 */           };
/* 2659 */           task.go();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static DownloadManager[] toDMS(Object[] objects) {
/* 2666 */     int count = 0;
/* 2667 */     DownloadManager[] result = new DownloadManager[objects.length];
/* 2668 */     for (Object object : objects) {
/* 2669 */       if ((object instanceof DownloadManager)) {
/* 2670 */         DownloadManager dm = (DownloadManager)object;
/* 2671 */         result[(count++)] = dm;
/* 2672 */       } else if ((object instanceof SelectedContent)) {
/* 2673 */         SelectedContent sc = (SelectedContent)object;
/* 2674 */         if ((sc.getFileIndex() == -1) && (sc.getDownloadManager() != null)) {
/* 2675 */           result[(count++)] = sc.getDownloadManager();
/*      */         }
/*      */       }
/*      */     }
/* 2679 */     DownloadManager[] resultTrim = new DownloadManager[count];
/* 2680 */     System.arraycopy(result, 0, resultTrim, 0, count);
/* 2681 */     return resultTrim;
/*      */   }
/*      */   
/*      */   private static DownloadStub.DownloadStubEx[] toDownloadStubs(Object[] objects) {
/* 2685 */     List<DownloadStub.DownloadStubEx> result = new ArrayList(objects.length);
/* 2686 */     for (Object o : objects) {
/* 2687 */       if ((o instanceof DownloadStub.DownloadStubEx)) {
/* 2688 */         result.add((DownloadStub.DownloadStubEx)o);
/*      */       }
/*      */     }
/* 2691 */     return (DownloadStub.DownloadStubEx[])result.toArray(new DownloadStub.DownloadStubEx[result.size()]);
/*      */   }
/*      */   
/*      */   private static DiskManagerFileInfo[] toDMFI(Object[] objects) {
/* 2695 */     int count = 0;
/* 2696 */     DiskManagerFileInfo[] result = new DiskManagerFileInfo[objects.length];
/* 2697 */     for (Object object : objects) {
/* 2698 */       if ((object instanceof DiskManagerFileInfo)) {
/* 2699 */         DiskManagerFileInfo fileInfo = (DiskManagerFileInfo)object;
/* 2700 */         result[(count++)] = fileInfo;
/* 2701 */       } else if ((object instanceof SelectedContent)) {
/* 2702 */         SelectedContent sc = (SelectedContent)object;
/* 2703 */         int fileIndex = sc.getFileIndex();
/* 2704 */         if ((fileIndex >= 0) && (sc.getDownloadManager() != null)) {
/* 2705 */           DownloadManager dm = sc.getDownloadManager();
/* 2706 */           if (dm != null) {
/* 2707 */             DiskManagerFileInfo[] infos = dm.getDiskManagerFileInfo();
/* 2708 */             if (fileIndex < infos.length) {
/* 2709 */               result[(count++)] = infos[fileIndex];
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2715 */     DiskManagerFileInfo[] resultTrim = new DiskManagerFileInfo[count];
/* 2716 */     System.arraycopy(result, 0, resultTrim, 0, count);
/* 2717 */     return resultTrim;
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
/*      */   public static boolean isFileTorrent(String originatingLocation, File torrentFile, String torrentName, boolean warnOnError)
/*      */   {
/* 2730 */     String sFirstChunk = null;
/*      */     try {
/* 2732 */       sFirstChunk = FileUtil.readFileAsString(torrentFile, 16384).toLowerCase();
/*      */       try
/*      */       {
/* 2735 */         if (!sFirstChunk.startsWith("d")) {
/* 2736 */           sFirstChunk = FileUtil.readGZippedFileAsString(torrentFile, 16384).toLowerCase();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     catch (IOException e) {
/* 2742 */       Debug.out("warning", e);
/*      */     }
/* 2744 */     if (sFirstChunk == null) {
/* 2745 */       sFirstChunk = "";
/*      */     }
/*      */     
/* 2748 */     if (!sFirstChunk.startsWith("d"))
/*      */     {
/* 2750 */       boolean isHTML = sFirstChunk.contains("<html");
/*      */       
/* 2752 */       String retry_url = UrlUtils.parseTextForMagnets(torrentName);
/* 2753 */       if (retry_url == null) {
/* 2754 */         retry_url = UrlUtils.parseTextForMagnets(sFirstChunk);
/*      */       }
/*      */       
/* 2757 */       if (retry_url != null) {
/* 2758 */         TorrentOpener.openTorrent(retry_url);
/* 2759 */         return false;
/*      */       }
/*      */       
/* 2762 */       if (warnOnError)
/*      */       {
/*      */ 
/* 2765 */         String chat_key = null;
/* 2766 */         String chat_net = null;
/*      */         
/* 2768 */         if ((originatingLocation != null) && (originatingLocation.toLowerCase(java.util.Locale.US).startsWith("http"))) {
/*      */           try
/*      */           {
/* 2771 */             URL url = new URL(originatingLocation);
/*      */             
/* 2773 */             String host = url.getHost();
/*      */             
/* 2775 */             String interesting = DNSUtils.getInterestingHostSuffix(host);
/*      */             
/* 2777 */             if (interesting != null)
/*      */             {
/* 2779 */               String net = AENetworkClassifier.categoriseAddress(host);
/*      */               
/* 2781 */               if (((net == "Public") && (BuddyPluginUtils.isBetaChatAvailable())) || ((net == "I2P") && (BuddyPluginUtils.isBetaChatAnonAvailable())))
/*      */               {
/*      */ 
/* 2784 */                 chat_key = "Torrent Error: " + interesting;
/* 2785 */                 chat_net = net;
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         String[] buttons;
/*      */         String[] buttons;
/* 2793 */         if (chat_key == null)
/*      */         {
/* 2795 */           buttons = new String[] { MessageText.getString("Button.ok") };
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 2801 */           buttons = new String[] { MessageText.getString("label.chat"), MessageText.getString("Button.ok") };
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2808 */         MessageBoxShell boxShell = new MessageBoxShell(MessageText.getString("OpenTorrentWindow.mb.notTorrent.title"), MessageText.getString("OpenTorrentWindow.mb.notTorrent.text", new String[] { torrentName, isHTML ? "" : MessageText.getString("OpenTorrentWindow.mb.notTorrent.cannot.display") }), buttons, buttons.length - 1);
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
/* 2819 */         if (isHTML) {
/* 2820 */           boxShell.setHtml(sFirstChunk);
/*      */         }
/*      */         
/* 2823 */         String f_chat_key = chat_key;
/* 2824 */         final String f_chat_net = chat_net;
/*      */         
/* 2826 */         boxShell.open(new UserPrompterResultListener()
/*      */         {
/*      */ 
/*      */           public void prompterClosed(int result)
/*      */           {
/* 2831 */             if ((this.val$f_chat_key != null) && (result == 0)) {
/* 2832 */               BuddyPluginUtils.createBetaChat(f_chat_net, this.val$f_chat_key, new BuddyPluginUtils.CreateChatCallback()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void complete(BuddyPluginBeta.ChatInstance chat)
/*      */                 {
/*      */ 
/*      */ 
/* 2841 */                   if (chat != null)
/*      */                   {
/* 2843 */                     chat.setInteresting(true);
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 2852 */       return false;
/*      */     }
/*      */     
/* 2855 */     return true;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Map<String, Long> calculateToolbarStates(ISelectedContent[] currentContent, String viewID_unused)
/*      */   {
/* 2878 */     Map<String, Long> mapNewToolbarStates = new HashMap();
/*      */     
/* 2880 */     String[] itemsNeedingSelection = new String[0];
/*      */     
/* 2882 */     String[] itemsNeedingRealDMSelection = { "remove", "top", "bottom", "transcode", "startstop" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2890 */     String[] itemsRequiring1DMwithHash = { "details", "comment", "up", "down" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2897 */     String[] itemsRequiring1DMSelection = new String[0];
/*      */     
/* 2899 */     int numSelection = currentContent.length;
/* 2900 */     boolean hasSelection = numSelection > 0;
/* 2901 */     boolean has1Selection = numSelection == 1;
/*      */     
/* 2903 */     for (int i = 0; i < itemsNeedingSelection.length; i++) {
/* 2904 */       String itemID = itemsNeedingSelection[i];
/* 2905 */       mapNewToolbarStates.put(itemID, Long.valueOf(hasSelection ? 1L : 0L));
/*      */     }
/*      */     
/*      */ 
/* 2909 */     TableView tv = SelectedContentManager.getCurrentlySelectedTableView();
/*      */     
/* 2911 */     boolean hasRealDM = tv != null;
/*      */     
/*      */ 
/* 2914 */     if ((!hasRealDM) && (numSelection > 0)) {
/* 2915 */       hasRealDM = true;
/* 2916 */       for (int i = 0; i < currentContent.length; i++) {
/* 2917 */         ISelectedContent content = currentContent[i];
/* 2918 */         DownloadManager dm = content.getDownloadManager();
/* 2919 */         if (dm == null) {
/* 2920 */           hasRealDM = false;
/* 2921 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 2925 */     if (!hasRealDM) {
/* 2926 */       MultipleDocumentInterfaceSWT mdi = UIFunctionsManagerSWT.getUIFunctionsSWT().getMDISWT();
/* 2927 */       if (mdi != null) {
/* 2928 */         MdiEntrySWT entry = mdi.getCurrentEntrySWT();
/* 2929 */         if (entry != null) {
/* 2930 */           if ((entry.getDatasource() instanceof DownloadManager)) {
/* 2931 */             hasRealDM = true;
/* 2932 */           } else if (((entry instanceof UIPluginView)) && ((entry.getDataSource() instanceof DownloadManager)))
/*      */           {
/* 2934 */             hasRealDM = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2940 */     boolean canStart = false;
/* 2941 */     boolean canStop = false;
/* 2942 */     boolean canRemoveFileInfo = false;
/* 2943 */     boolean canRunFileInfo = false;
/* 2944 */     boolean hasDM = false;
/*      */     
/* 2946 */     if ((currentContent.length > 0) && (hasRealDM))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2951 */       boolean canMoveUp = false;
/* 2952 */       boolean canMoveDown = false;
/* 2953 */       boolean canDownload = false;
/* 2954 */       GlobalManager gm = null;
/* 2955 */       for (int i = 0; i < currentContent.length; i++) {
/* 2956 */         ISelectedContent content = currentContent[i];
/* 2957 */         DownloadManager dm = content.getDownloadManager();
/* 2958 */         if (dm == null) {
/* 2959 */           if ((!canDownload) && (content.getDownloadInfo() != null)) {
/* 2960 */             canDownload = true;
/*      */           }
/*      */         }
/*      */         else {
/* 2964 */           if (gm == null) {
/* 2965 */             gm = dm.getGlobalManager();
/*      */           }
/*      */           
/* 2968 */           int fileIndex = content.getFileIndex();
/* 2969 */           if (fileIndex == -1) {
/* 2970 */             if ((!canMoveUp) && (gm.isMoveableUp(dm))) {
/* 2971 */               canMoveUp = true;
/*      */             }
/* 2973 */             if ((!canMoveDown) && (gm.isMoveableDown(dm))) {
/* 2974 */               canMoveDown = true;
/*      */             }
/*      */             
/* 2977 */             hasDM = true;
/* 2978 */             if ((!canStart) && (ManagerUtils.isStartable(dm))) {
/* 2979 */               canStart = true;
/*      */             }
/* 2981 */             if ((!canStop) && (ManagerUtils.isStopable(dm))) {
/* 2982 */               canStop = true;
/*      */             }
/*      */           } else {
/* 2985 */             DiskManagerFileInfoSet fileInfos = dm.getDiskManagerFileInfoSet();
/* 2986 */             if (fileIndex < fileInfos.nbFiles()) {
/* 2987 */               DiskManagerFileInfo fileInfo = fileInfos.getFiles()[fileIndex];
/* 2988 */               if ((!canStart) && (fileInfo.isSkipped())) {
/* 2989 */                 canStart = true;
/*      */               }
/*      */               
/* 2992 */               if ((!canStop) && (!fileInfo.isSkipped())) {
/* 2993 */                 canStop = true;
/*      */               }
/*      */               
/* 2996 */               if ((!canRemoveFileInfo) && (!fileInfo.isSkipped())) {
/* 2997 */                 int storageType = fileInfo.getStorageType();
/* 2998 */                 if ((storageType == 1) || (storageType == 2))
/*      */                 {
/* 3000 */                   canRemoveFileInfo = true;
/*      */                 }
/*      */               }
/*      */               
/* 3004 */               if ((!canRunFileInfo) && (fileInfo.getAccessMode() == 1) && (fileInfo.getDownloaded() == fileInfo.getLength()) && (fileInfo.getFile(true).exists()))
/*      */               {
/*      */ 
/*      */ 
/* 3008 */                 canRunFileInfo = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3014 */       boolean canRemove = (hasDM) || (canRemoveFileInfo);
/*      */       
/* 3016 */       mapNewToolbarStates.put("remove", Long.valueOf(canRemove ? 1L : 0L));
/*      */       
/*      */ 
/* 3019 */       mapNewToolbarStates.put("download", Long.valueOf(canDownload ? 1L : 0L));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3025 */       if (currentContent.length == 1) {
/* 3026 */         mapNewToolbarStates.put("up", Long.valueOf(canMoveUp ? 1L : 0L));
/*      */         
/* 3028 */         mapNewToolbarStates.put("down", Long.valueOf(canMoveDown ? 1L : 0L));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3033 */     boolean canRun = (has1Selection) && (((hasDM) && (!canRunFileInfo)) || ((!hasDM) && (canRunFileInfo)));
/*      */     
/* 3035 */     if (canRun) {
/* 3036 */       ISelectedContent content = currentContent[0];
/* 3037 */       DownloadManager dm = content.getDownloadManager();
/*      */       
/* 3039 */       if (dm == null) {
/* 3040 */         canRun = false;
/*      */       } else {
/* 3042 */         TOTorrent torrent = dm.getTorrent();
/*      */         
/* 3044 */         if (torrent == null)
/*      */         {
/* 3046 */           canRun = false;
/*      */         }
/* 3048 */         else if ((!dm.getAssumedComplete()) && (torrent.isSimpleTorrent()))
/*      */         {
/* 3050 */           canRun = false;
/*      */         }
/*      */       }
/*      */     }
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
/* 3064 */     mapNewToolbarStates.put("run", Long.valueOf(canRun ? 1L : 0L));
/*      */     
/* 3066 */     mapNewToolbarStates.put("start", Long.valueOf(canStart ? 1L : 0L));
/* 3067 */     mapNewToolbarStates.put("stop", Long.valueOf(canStop ? 1L : 0L));
/* 3068 */     mapNewToolbarStates.put("startstop", Long.valueOf((canStart) || (canStop) ? 1L : 0L));
/*      */     
/*      */ 
/* 3071 */     for (int i = 0; i < itemsNeedingRealDMSelection.length; i++) {
/* 3072 */       String itemID = itemsNeedingRealDMSelection[i];
/* 3073 */       if (!mapNewToolbarStates.containsKey(itemID)) {
/* 3074 */         mapNewToolbarStates.put(itemID, Long.valueOf((hasSelection) && (hasDM) && (hasRealDM) ? 1L : 0L));
/*      */       }
/*      */     }
/*      */     
/* 3078 */     for (int i = 0; i < itemsRequiring1DMSelection.length; i++) {
/* 3079 */       String itemID = itemsRequiring1DMSelection[i];
/* 3080 */       if (!mapNewToolbarStates.containsKey(itemID)) {
/* 3081 */         mapNewToolbarStates.put(itemID, Long.valueOf((has1Selection) && (hasDM) ? 1L : 0L));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3086 */     for (int i = 0; i < itemsRequiring1DMwithHash.length; i++) {
/* 3087 */       String itemID = itemsRequiring1DMwithHash[i];
/* 3088 */       if (!mapNewToolbarStates.containsKey(itemID)) {
/* 3089 */         mapNewToolbarStates.put(itemID, Long.valueOf(hasDM ? 1L : 0L));
/*      */       }
/*      */     }
/*      */     
/* 3093 */     mapNewToolbarStates.put("download", Long.valueOf((has1Selection) && (!(currentContent[0] instanceof ISelectedVuzeFileContent)) && (currentContent[0].getDownloadManager() == null) && ((currentContent[0].getHash() != null) || (currentContent[0].getDownloadInfo() != null)) ? 1L : 0L));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3101 */     if (tv != null) {
/* 3102 */       TableColumn tc = tv.getTableColumn("#");
/* 3103 */       if ((tc != null) && (!tc.isVisible())) {
/* 3104 */         mapNewToolbarStates.put("up", Long.valueOf(0L));
/* 3105 */         mapNewToolbarStates.put("down", Long.valueOf(0L));
/*      */       }
/*      */     }
/*      */     
/* 3109 */     return mapNewToolbarStates;
/*      */   }
/*      */   
/*      */   public static void removeDownloads(DownloadManager[] dms, AERunnable deleteFailed)
/*      */   {
/* 3114 */     removeDownloads(dms, deleteFailed, false);
/*      */   }
/*      */   
/*      */ 
/*      */   public static void removeDownloads(DownloadManager[] dms, final AERunnable deleteFailed, final boolean forcePrompt)
/*      */   {
/* 3120 */     TorrentUtils.runTorrentDelete(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 3126 */         TorrentUtil.removeDownloadsSupport(this.val$dms, deleteFailed, forcePrompt);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   private static void removeDownloadsSupport(DownloadManager[] dms, final AERunnable deleteFailed, boolean forcePrompt)
/*      */   {
/* 3133 */     if (dms == null) {
/* 3134 */       return;
/*      */     }
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
/* 3146 */     boolean can_archive = false;
/*      */     
/* 3148 */     for (int i = 0; i < dms.length; i++) {
/* 3149 */       DownloadManager dm = dms[i];
/* 3150 */       if (dm != null)
/*      */       {
/*      */ 
/* 3153 */         if (PluginCoreUtils.wrap(dm).canStubbify())
/* 3154 */           can_archive = true;
/*      */       }
/*      */     }
/* 3157 */     for (int i = 0; i < dms.length; i++) {
/* 3158 */       DownloadManager dm = dms[i];
/* 3159 */       if (dm != null)
/*      */       {
/*      */ 
/*      */ 
/* 3163 */         boolean deleteTorrent = COConfigurationManager.getBooleanParameter("def.deletetorrent");
/*      */         
/* 3165 */         int confirm = COConfigurationManager.getIntParameter("tb.confirm.delete.content");
/* 3166 */         boolean doPrompt = confirm == 0 | forcePrompt;
/*      */         
/* 3168 */         if (doPrompt) {
/* 3169 */           String title = MessageText.getString("deletedata.title");
/* 3170 */           String text = MessageText.getString("v3.deleteContent.message", new String[] { dm.getDisplayName() });
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 3175 */           if (can_archive) {
/* 3176 */             text = text + "\n\n" + MessageText.getString("v3.deleteContent.or.archive");
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 3182 */           String[] buttons = { MessageText.getString("Button.cancel"), MessageText.getString("Button.deleteContent.fromComputer"), MessageText.getString("Button.deleteContent.fromLibrary") };
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
/* 3194 */           int defaultButtonPos = 2;
/*      */           
/* 3196 */           final MessageBoxShell mb = new MessageBoxShell(title, text, buttons, defaultButtonPos);
/*      */           
/* 3198 */           int numLeft = dms.length - i;
/* 3199 */           if (numLeft > 1) {
/* 3200 */             mb.setRemember("na", false, MessageText.getString("v3.deleteContent.applyToAll", new String[] { "" + numLeft }));
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 3205 */             mb.setRememberOnlyIfButton(-3);
/*      */           }
/* 3207 */           mb.setRelatedObject(dm);
/* 3208 */           mb.setLeftImage("image.trash");
/* 3209 */           mb.addCheckBox("deletecontent.also.deletetorrent", 2, deleteTorrent);
/*      */           
/* 3211 */           final int index = i;
/*      */           
/* 3213 */           TorrentUtils.startTorrentDelete();
/*      */           
/* 3215 */           final boolean[] endDone = { false };
/*      */           try
/*      */           {
/* 3218 */             mb.open(new UserPrompterResultListener()
/*      */             {
/*      */               public void prompterClosed(int result)
/*      */               {
/*      */                 try {
/* 3223 */                   ImageLoader.getInstance().releaseImage("image.trash");
/*      */                   
/* 3225 */                   TorrentUtil.removeDownloadsPrompterClosed(this.val$dms, index, deleteFailed, result, mb.isRemembered(), mb.getCheckBoxEnabled());
/*      */ 
/*      */                 }
/*      */                 finally
/*      */                 {
/* 3230 */                   synchronized (endDone)
/*      */                   {
/* 3232 */                     if (endDone[0] == 0)
/*      */                     {
/* 3234 */                       TorrentUtils.endTorrentDelete();
/*      */                       
/* 3236 */                       endDone[0] = true;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (Throwable e) {
/* 3244 */             Debug.out(e);
/*      */             
/* 3246 */             synchronized (endDone)
/*      */             {
/* 3248 */               if (endDone[0] == 0)
/*      */               {
/* 3250 */                 TorrentUtils.endTorrentDelete();
/*      */                 
/* 3252 */                 endDone[0] = true;
/*      */               }
/*      */             }
/*      */           }
/* 3256 */           return;
/*      */         }
/* 3258 */         boolean deleteData = confirm == 1;
/* 3259 */         removeDownloadsPrompterClosed(dms, i, deleteFailed, deleteData ? 1 : 2, true, deleteTorrent);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void removeDownloadsPrompterClosed(DownloadManager[] dms, final int index, final AERunnable deleteFailed, final int result, final boolean doAll, final boolean deleteTorrent)
/*      */   {
/* 3269 */     TorrentUtils.runTorrentDelete(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 3275 */         TorrentUtil.removeDownloadsPrompterClosedSupport(this.val$dms, index, deleteFailed, result, doAll, deleteTorrent);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private static void removeDownloadsPrompterClosedSupport(DownloadManager[] dms, int index, AERunnable deleteFailed, int result, boolean doAll, boolean deleteTorrent)
/*      */   {
/* 3283 */     if (result == -1)
/*      */     {
/*      */ 
/* 3286 */       return;
/*      */     }
/* 3288 */     if (doAll) {
/* 3289 */       if ((result == 1) || (result == 2))
/*      */       {
/* 3291 */         for (int i = index; i < dms.length; i++) {
/* 3292 */           DownloadManager dm = dms[i];
/* 3293 */           boolean deleteData = result != 2;
/*      */           
/*      */ 
/* 3296 */           ManagerUtils.asyncStopDelete(dm, 70, deleteTorrent, deleteData, deleteFailed);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 3301 */       if ((result == 1) || (result == 2)) {
/* 3302 */         DownloadManager dm = dms[index];
/* 3303 */         boolean deleteData = result != 2;
/*      */         
/*      */ 
/*      */ 
/* 3307 */         ManagerUtils.asyncStopDelete(dm, 70, deleteTorrent, deleteData, null);
/*      */       }
/*      */       
/*      */ 
/* 3311 */       dms[index] = null;
/* 3312 */       if (index != dms.length - 1) {
/* 3313 */         removeDownloads(dms, deleteFailed, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void removeDownloadStubs(DownloadStub.DownloadStubEx[] dms, final AERunnable deleteFailed, boolean forcePrompt)
/*      */   {
/* 3325 */     if (dms == null)
/*      */     {
/* 3327 */       return;
/*      */     }
/*      */     
/* 3330 */     for (int i = 0; i < dms.length; i++)
/*      */     {
/* 3332 */       DownloadStub.DownloadStubEx dm = dms[i];
/*      */       
/* 3334 */       boolean deleteTorrent = COConfigurationManager.getBooleanParameter("def.deletetorrent");
/*      */       
/* 3336 */       int confirm = COConfigurationManager.getIntParameter("tb.confirm.delete.content");
/* 3337 */       boolean doPrompt = confirm == 0 | forcePrompt;
/*      */       
/* 3339 */       if (doPrompt) {
/* 3340 */         String title = MessageText.getString("deletedata.title");
/* 3341 */         String text = MessageText.getString("v3.deleteContent.message", new String[] { dm.getName() });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3349 */         String[] buttons = { MessageText.getString("Button.cancel"), MessageText.getString("Button.deleteContent.fromComputer"), MessageText.getString("Button.deleteContent.fromLibrary") };
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
/* 3361 */         int defaultButtonPos = 2;
/*      */         
/* 3363 */         final MessageBoxShell mb = new MessageBoxShell(title, text, buttons, defaultButtonPos);
/*      */         
/* 3365 */         int numLeft = dms.length - i;
/* 3366 */         if (numLeft > 1) {
/* 3367 */           mb.setRemember("na", false, MessageText.getString("v3.deleteContent.applyToAll", new String[] { "" + numLeft }));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 3372 */           mb.setRememberOnlyIfButton(-3);
/*      */         }
/* 3374 */         mb.setRelatedObject(dm);
/* 3375 */         mb.setLeftImage("image.trash");
/* 3376 */         mb.addCheckBox("deletecontent.also.deletetorrent", 2, deleteTorrent);
/*      */         
/* 3378 */         final int index = i;
/*      */         
/* 3380 */         mb.open(new UserPrompterResultListener()
/*      */         {
/*      */           public void prompterClosed(int result) {
/* 3383 */             ImageLoader.getInstance().releaseImage("image.trash");
/*      */             
/* 3385 */             TorrentUtil.removeDownloadStubsPrompterClosed(this.val$dms, index, deleteFailed, result, mb.isRemembered(), mb.getCheckBoxEnabled());
/*      */           }
/*      */           
/* 3388 */         });
/* 3389 */         return;
/*      */       }
/* 3391 */       boolean deleteData = confirm == 1;
/* 3392 */       removeDownloadStubsPrompterClosed(dms, i, deleteFailed, deleteData ? 1 : 2, true, deleteTorrent);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void removeDownloadStubsPrompterClosed(DownloadStub.DownloadStubEx[] dms, int index, AERunnable deleteFailed, int result, boolean doAll, boolean deleteTorrent)
/*      */   {
/* 3401 */     if (result == -1)
/*      */     {
/*      */ 
/* 3404 */       return;
/*      */     }
/* 3406 */     if (doAll) {
/* 3407 */       if ((result == 1) || (result == 2))
/*      */       {
/* 3409 */         for (int i = index; i < dms.length; i++) {
/* 3410 */           DownloadStub.DownloadStubEx dm = dms[i];
/* 3411 */           boolean deleteData = result != 2;
/*      */           
/*      */           try
/*      */           {
/* 3415 */             dm.remove(deleteTorrent, deleteData);
/*      */           } catch (Throwable e) {
/* 3417 */             if (deleteFailed != null) {
/* 3418 */               deleteFailed.runSupport();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/* 3424 */       if ((result == 1) || (result == 2)) {
/* 3425 */         DownloadStub.DownloadStubEx dm = dms[index];
/* 3426 */         boolean deleteData = result != 2;
/*      */         
/*      */         try
/*      */         {
/* 3430 */           dm.remove(deleteTorrent, deleteData);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3437 */       dms[index] = null;
/* 3438 */       if (index != dms.length - 1) {
/* 3439 */         removeDownloadStubs(dms, deleteFailed, true);
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/TorrentUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */