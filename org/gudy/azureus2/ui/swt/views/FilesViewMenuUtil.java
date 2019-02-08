/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCoreOperation;
/*      */ import com.aelitis.azureus.core.AzureusCoreOperationTask;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap.Entry;
/*      */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*      */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.widgets.DirectoryDialog;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*      */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*      */ import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
/*      */ import org.gudy.azureus2.ui.swt.shells.AdvRenameWindow;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
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
/*      */ public class FilesViewMenuUtil
/*      */ {
/*   69 */   public static final Object PRIORITY_HIGH = new Object();
/*   70 */   public static final Object PRIORITY_NORMAL = new Object();
/*   71 */   public static final Object PRIORITY_LOW = new Object();
/*   72 */   public static final Object PRIORITY_NUMERIC = new Object();
/*   73 */   public static final Object PRIORITY_NUMERIC_AUTO = new Object();
/*   74 */   public static final Object PRIORITY_SKIPPED = new Object();
/*   75 */   public static final Object PRIORITY_DELETE = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void fillMenu(final TableView<?> tv, final Menu menu, DownloadManager[] manager_list, final org.gudy.azureus2.core3.disk.DiskManagerFileInfo[][] files_list)
/*      */   {
/*   84 */     Shell shell = menu.getShell();
/*      */     
/*   86 */     final List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> all_files = new ArrayList();
/*      */     
/*   88 */     for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files : files_list)
/*      */     {
/*   90 */       all_files.addAll(Arrays.asList(files));
/*      */     }
/*      */     
/*   93 */     boolean hasSelection = all_files.size() > 0;
/*      */     
/*   95 */     org.eclipse.swt.widgets.MenuItem itemOpen = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*   96 */     Messages.setLanguageText(itemOpen, "FilesView.menu.open");
/*   97 */     Utils.setMenuItemImage(itemOpen, "run");
/*      */     
/*   99 */     menu.setDefaultItem(itemOpen);
/*      */     
/*      */ 
/*  102 */     final boolean use_open_containing_folder = COConfigurationManager.getBooleanParameter("MyTorrentsView.menu.show_parent_folder_enabled");
/*  103 */     org.eclipse.swt.widgets.MenuItem itemExplore = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  104 */     Messages.setLanguageText(itemExplore, "MyTorrentsView.menu." + (use_open_containing_folder ? "open_parent_folder" : "explore"));
/*      */     
/*  106 */     itemExplore.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  108 */         for (int i = this.val$all_files.size() - 1; i >= 0; i--) {
/*  109 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(i);
/*  110 */           if (info != null) {
/*  111 */             ManagerUtils.open(info, use_open_containing_folder);
/*      */           }
/*      */         }
/*      */       }
/*  115 */     });
/*  116 */     itemExplore.setEnabled(hasSelection);
/*      */     
/*      */ 
/*      */ 
/*  120 */     Menu menuBrowse = new Menu(menu.getShell(), 4);
/*  121 */     org.eclipse.swt.widgets.MenuItem itemBrowse = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*  122 */     Messages.setLanguageText(itemBrowse, "MyTorrentsView.menu.browse");
/*  123 */     itemBrowse.setMenu(menuBrowse);
/*      */     
/*      */ 
/*  126 */     org.eclipse.swt.widgets.MenuItem itemBrowsePublic = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 8);
/*  127 */     itemBrowsePublic.setText(MessageText.getString("label.public") + "...");
/*  128 */     itemBrowsePublic.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  130 */         for (int i = this.val$all_files.size() - 1; i >= 0; i--) {
/*  131 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(i);
/*  132 */           if (info != null) {
/*  133 */             ManagerUtils.browse(info, false, true);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  138 */     });
/*  139 */     org.eclipse.swt.widgets.MenuItem itemBrowseAnon = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 8);
/*  140 */     itemBrowseAnon.setText(MessageText.getString("label.anon") + "...");
/*  141 */     itemBrowseAnon.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  143 */         for (int i = this.val$all_files.size() - 1; i >= 0; i--) {
/*  144 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(i);
/*  145 */           if (info != null) {
/*  146 */             ManagerUtils.browse(info, true, true);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  151 */     });
/*  152 */     new org.eclipse.swt.widgets.MenuItem(menuBrowse, 2);
/*      */     
/*  154 */     org.eclipse.swt.widgets.MenuItem itemBrowseURL = new org.eclipse.swt.widgets.MenuItem(menuBrowse, 8);
/*  155 */     Messages.setLanguageText(itemBrowseURL, "label.copy.url.to.clip");
/*  156 */     itemBrowseURL.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  158 */         Utils.getOffOfSWTThread(new AERunnable()
/*      */         {
/*      */           public void runSupport()
/*      */           {
/*  162 */             String url = ManagerUtils.browse((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)FilesViewMenuUtil.4.this.val$all_files.get(0), true, false);
/*  163 */             if (url != null) {
/*  164 */               ClipboardCopy.copyToClipBoard(url);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*  169 */     });
/*  170 */     itemBrowseURL.setEnabled(all_files.size() == 1);
/*      */     
/*  172 */     menuBrowse.setEnabled(hasSelection);
/*      */     
/*      */ 
/*      */ 
/*  176 */     org.eclipse.swt.widgets.MenuItem itemRenameOrRetarget = null;org.eclipse.swt.widgets.MenuItem itemRename = null;org.eclipse.swt.widgets.MenuItem itemRetarget = null;
/*      */     
/*      */ 
/*  179 */     itemRenameOrRetarget = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  180 */     Messages.setLanguageText(itemRenameOrRetarget, "FilesView.menu.rename");
/*  181 */     itemRenameOrRetarget.setData("rename", Boolean.valueOf(true));
/*  182 */     itemRenameOrRetarget.setData("retarget", Boolean.valueOf(true));
/*      */     
/*      */ 
/*  185 */     itemRename = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  186 */     Messages.setLanguageText(itemRename, "FilesView.menu.rename_only");
/*  187 */     itemRename.setData("rename", Boolean.valueOf(true));
/*  188 */     itemRename.setData("retarget", Boolean.valueOf(false));
/*      */     
/*      */ 
/*  191 */     itemRetarget = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  192 */     Messages.setLanguageText(itemRetarget, "FilesView.menu.retarget");
/*  193 */     itemRetarget.setData("rename", Boolean.valueOf(false));
/*  194 */     itemRetarget.setData("retarget", Boolean.valueOf(true));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  199 */     org.eclipse.swt.widgets.MenuItem itemRevertFiles = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  200 */     Messages.setLanguageText(itemRevertFiles, "MyTorrentsView.menu.revertfiles");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  205 */     org.eclipse.swt.widgets.MenuItem itemLocateFiles = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  206 */     Messages.setLanguageText(itemLocateFiles, "MyTorrentsView.menu.locatefiles");
/*      */     
/*      */ 
/*      */ 
/*  210 */     org.eclipse.swt.widgets.MenuItem itemfindMore = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  211 */     Messages.setLanguageText(itemfindMore, "MyTorrentsView.menu.findmorelikethis");
/*      */     
/*      */ 
/*  214 */     org.eclipse.swt.widgets.MenuItem itemClearLinks = null;
/*      */     
/*  216 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/*  218 */     if (userMode > 1)
/*      */     {
/*  220 */       itemClearLinks = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  221 */       Messages.setLanguageText(itemClearLinks, "FilesView.menu.clear.links");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  226 */     final org.eclipse.swt.widgets.MenuItem itemQuickView = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/*  227 */     Messages.setLanguageText(itemQuickView, "MainWindow.menu.quick_view");
/*      */     
/*  229 */     itemQuickView.setEnabled((all_files.size() == 1) && (Utils.isQuickViewSupported((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)all_files.get(0))));
/*  230 */     itemQuickView.setSelection((all_files.size() == 1) && (Utils.isQuickViewActive((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)all_files.get(0))));
/*      */     
/*  232 */     itemQuickView.addListener(13, new Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(Event arg)
/*      */       {
/*      */ 
/*      */ 
/*  240 */         Utils.setQuickViewActive((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(0), itemQuickView.getSelection());
/*      */       }
/*      */     });
/*      */     
/*      */ 
/*      */ 
/*  246 */     if (manager_list.length == 1)
/*      */     {
/*  248 */       MenuFactory.addAlertsMenu(menu, manager_list[0], files_list[0]);
/*      */     }
/*      */     
/*      */ 
/*  252 */     org.eclipse.swt.widgets.MenuItem itemPersonalShare = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  253 */     Messages.setLanguageText(itemPersonalShare, "MyTorrentsView.menu.create_personal_share");
/*      */     
/*      */ 
/*      */ 
/*  257 */     org.eclipse.swt.widgets.MenuItem itemPriority = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*  258 */     Messages.setLanguageText(itemPriority, "FilesView.menu.setpriority");
/*      */     
/*  260 */     Menu menuPriority = new Menu(shell, 4);
/*  261 */     itemPriority.setMenu(menuPriority);
/*      */     
/*  263 */     org.eclipse.swt.widgets.MenuItem itemHigh = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  264 */     itemHigh.setData("Priority", PRIORITY_HIGH);
/*  265 */     Messages.setLanguageText(itemHigh, "FilesView.menu.setpriority.high");
/*      */     
/*  267 */     org.eclipse.swt.widgets.MenuItem itemNormal = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  268 */     itemNormal.setData("Priority", PRIORITY_NORMAL);
/*  269 */     Messages.setLanguageText(itemNormal, "FilesView.menu.setpriority.normal");
/*      */     
/*  271 */     org.eclipse.swt.widgets.MenuItem itemLow = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  272 */     itemLow.setData("Priority", PRIORITY_LOW);
/*  273 */     Messages.setLanguageText(itemLow, "FileItem.low");
/*      */     
/*  275 */     org.eclipse.swt.widgets.MenuItem itemNumeric = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  276 */     itemNumeric.setData("Priority", PRIORITY_NUMERIC);
/*  277 */     Messages.setLanguageText(itemNumeric, "FilesView.menu.setpriority.numeric");
/*      */     
/*  279 */     org.eclipse.swt.widgets.MenuItem itemNumericAuto = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  280 */     itemNumericAuto.setData("Priority", PRIORITY_NUMERIC_AUTO);
/*  281 */     Messages.setLanguageText(itemNumericAuto, "FilesView.menu.setpriority.numeric.auto");
/*      */     
/*      */ 
/*  284 */     org.eclipse.swt.widgets.MenuItem itemSkipped = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  285 */     itemSkipped.setData("Priority", PRIORITY_SKIPPED);
/*  286 */     Messages.setLanguageText(itemSkipped, "FilesView.menu.setpriority.skipped");
/*      */     
/*  288 */     org.eclipse.swt.widgets.MenuItem itemDelete = new org.eclipse.swt.widgets.MenuItem(menuPriority, 64);
/*  289 */     itemDelete.setData("Priority", PRIORITY_DELETE);
/*  290 */     Messages.setLanguageText(itemDelete, "wizard.multitracker.delete");
/*      */     
/*  292 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*      */     
/*  294 */     if (!hasSelection) {
/*  295 */       itemOpen.setEnabled(false);
/*  296 */       itemPriority.setEnabled(false);
/*  297 */       itemRenameOrRetarget.setEnabled(false);
/*  298 */       itemRename.setEnabled(false);
/*  299 */       itemRetarget.setEnabled(false);
/*  300 */       itemLocateFiles.setEnabled(false);
/*  301 */       itemfindMore.setEnabled(false);
/*  302 */       if (itemClearLinks != null) {
/*  303 */         itemClearLinks.setEnabled(false);
/*      */       }
/*  305 */       itemPersonalShare.setEnabled(false);
/*      */       
/*  307 */       return;
/*      */     }
/*      */     
/*  310 */     boolean all_persistent = true;
/*      */     
/*  312 */     boolean open = true;
/*  313 */     boolean all_compact = true;
/*  314 */     boolean all_dnd_not_deleted = true;
/*  315 */     boolean all_high_pri = true;
/*  316 */     boolean all_normal_pri = true;
/*  317 */     boolean all_low_pri = true;
/*  318 */     boolean all_complete = true;
/*      */     
/*  320 */     boolean any_relocated = false;
/*      */     
/*  322 */     List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> files_with_links = new ArrayList();
/*      */     
/*  324 */     for (int j = 0; j < manager_list.length; j++)
/*      */     {
/*  326 */       DownloadManager manager = manager_list[j];
/*      */       
/*  328 */       int dm_file_count = manager.getNumFileInfos();
/*      */       
/*  330 */       if (!manager.isPersistent()) {
/*  331 */         all_persistent = false;
/*      */       }
/*  333 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] files = files_list[j];
/*      */       
/*  335 */       DownloadManagerState dm_state = manager.getDownloadState();
/*      */       
/*  337 */       int[] storage_types = manager.getStorageType(files);
/*      */       
/*  339 */       for (int i = 0; i < files.length; i++)
/*      */       {
/*  341 */         org.gudy.azureus2.core3.disk.DiskManagerFileInfo file_info = files[i];
/*      */         
/*  343 */         if ((open) && (file_info.getAccessMode() != 1))
/*      */         {
/*  345 */           open = false;
/*      */         }
/*      */         
/*  348 */         boolean isCompact = (storage_types[i] == 2) || (storage_types[i] == 4);
/*  349 */         if ((all_compact) && (!isCompact)) {
/*  350 */           all_compact = false;
/*      */         }
/*      */         
/*  353 */         if ((all_dnd_not_deleted) || (all_high_pri) || (all_normal_pri) || (all_low_pri)) {
/*  354 */           if (file_info.isSkipped()) {
/*  355 */             all_high_pri = all_normal_pri = all_low_pri = 0;
/*  356 */             if (isCompact) {
/*  357 */               all_dnd_not_deleted = false;
/*      */             }
/*      */           } else {
/*  360 */             all_dnd_not_deleted = false;
/*      */             
/*      */ 
/*  363 */             if ((all_high_pri) || (all_normal_pri) || (all_low_pri)) {
/*  364 */               int file_pri = file_info.getPriority();
/*  365 */               if (file_pri == 0) {
/*  366 */                 all_high_pri = all_low_pri = 0;
/*  367 */               } else if (file_pri == 1) {
/*  368 */                 all_normal_pri = all_low_pri = 0;
/*  369 */               } else if (file_pri == -1) {
/*  370 */                 all_normal_pri = all_high_pri = 0;
/*      */               } else {
/*  372 */                 all_low_pri = all_normal_pri = all_high_pri = 0;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  378 */         File file_link = file_info.getFile(true);
/*  379 */         File file_nolink = file_info.getFile(false);
/*      */         
/*  381 */         if ((file_info.getDownloaded() != file_info.getLength()) || (file_link.length() != file_info.getLength()))
/*      */         {
/*      */ 
/*  384 */           all_complete = false;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  389 */         if (dm_file_count > 1)
/*      */         {
/*      */ 
/*  392 */           if (!file_nolink.getAbsolutePath().equals(file_link.getAbsolutePath()))
/*      */           {
/*  394 */             files_with_links.add(file_info);
/*      */           }
/*      */         }
/*      */         
/*  398 */         File target = dm_state.getFileLink(file_info.getIndex(), file_nolink);
/*      */         
/*  400 */         if (target != null)
/*      */         {
/*  402 */           if (target != file_nolink)
/*      */           {
/*  404 */             if (!target.equals(file_nolink))
/*      */             {
/*  406 */               any_relocated = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  415 */     itemOpen.setEnabled(open);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  420 */     itemRenameOrRetarget.setEnabled(all_persistent);
/*  421 */     itemRename.setEnabled(all_persistent);
/*  422 */     itemRetarget.setEnabled(all_persistent);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  428 */     itemPersonalShare.setEnabled((all_complete) && (all_files.size() == 1));
/*      */     
/*  430 */     itemSkipped.setEnabled(!all_dnd_not_deleted);
/*      */     
/*  432 */     itemHigh.setEnabled(!all_high_pri);
/*      */     
/*  434 */     itemNormal.setEnabled(!all_normal_pri);
/*      */     
/*  436 */     itemLow.setEnabled(!all_low_pri);
/*      */     
/*  438 */     itemDelete.setEnabled(!all_compact);
/*      */     
/*  440 */     itemOpen.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  442 */         for (int i = 0; i < this.val$all_files.size(); i++) {
/*  443 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(i);
/*  444 */           if ((info != null) && (info.getAccessMode() == 1)) {
/*  445 */             Utils.launch(info);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  450 */     });
/*  451 */     Listener rename_listener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  453 */         boolean rename_it = ((Boolean)event.widget.getData("rename")).booleanValue();
/*  454 */         boolean retarget_it = ((Boolean)event.widget.getData("retarget")).booleanValue();
/*  455 */         FilesViewMenuUtil.rename(this.val$tv, all_files.toArray(new Object[all_files.size()]), rename_it, retarget_it);
/*      */       }
/*      */       
/*  458 */     };
/*  459 */     itemRenameOrRetarget.addListener(13, rename_listener);
/*  460 */     itemRename.addListener(13, rename_listener);
/*  461 */     itemRetarget.addListener(13, rename_listener);
/*      */     
/*      */ 
/*  464 */     itemLocateFiles.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  466 */         ManagerUtils.locateFiles(this.val$manager_list, files_list, menu.getShell());
/*      */       }
/*      */       
/*  469 */     });
/*  470 */     itemLocateFiles.setEnabled(true);
/*      */     
/*  472 */     if (ManagerUtils.canFindMoreLikeThis()) {
/*  473 */       itemfindMore.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  475 */           ManagerUtils.findMoreLikeThis((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(0), menu.getShell());
/*      */         }
/*      */         
/*  478 */       });
/*  479 */       itemfindMore.setEnabled(all_files.size() == 1);
/*      */     }
/*      */     
/*  482 */     itemRevertFiles.setEnabled(any_relocated);
/*  483 */     itemRevertFiles.addListener(13, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/*  486 */         FilesViewMenuUtil.revertFiles(this.val$tv, all_files);
/*      */       }
/*      */     });
/*      */     
/*  490 */     if (itemClearLinks != null)
/*      */     {
/*  492 */       itemClearLinks.setEnabled(files_with_links.size() > 0);
/*      */       
/*  494 */       itemClearLinks.addListener(13, new Listener()
/*      */       {
/*      */         public void handleEvent(Event event) {
/*  497 */           for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : this.val$files_with_links)
/*      */           {
/*  499 */             file.setLink(null);
/*      */           }
/*      */           
/*  502 */           FilesViewMenuUtil.invalidateRows(tv, this.val$files_with_links);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  507 */     itemPersonalShare.addListener(13, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  509 */         Map<String, String> properties = new HashMap();
/*      */         
/*  511 */         properties.put("personal", "true");
/*      */         
/*  513 */         for (int i = 0; i < this.val$all_files.size(); i++)
/*      */         {
/*  515 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo file_info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)this.val$all_files.get(i);
/*      */           
/*  517 */           File file = file_info.getFile(true);
/*      */           
/*  519 */           if (file.isFile())
/*      */           {
/*  521 */             ShareUtils.shareFile(file.getAbsolutePath(), properties);
/*      */           }
/*  523 */           else if (file.isDirectory())
/*      */           {
/*  525 */             ShareUtils.shareDir(file.getAbsolutePath(), properties);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*  531 */     });
/*  532 */     Listener priorityListener = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  534 */         final Object priority = event.widget.getData("Priority");
/*  535 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  537 */             FilesViewMenuUtil.changePriority(priority, FilesViewMenuUtil.13.this.val$all_files);
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*  542 */     };
/*  543 */     itemNumeric.addListener(13, priorityListener);
/*  544 */     itemNumericAuto.addListener(13, priorityListener);
/*  545 */     itemHigh.addListener(13, priorityListener);
/*  546 */     itemNormal.addListener(13, priorityListener);
/*  547 */     itemLow.addListener(13, priorityListener);
/*  548 */     itemSkipped.addListener(13, priorityListener);
/*  549 */     itemDelete.addListener(13, priorityListener);
/*      */     
/*  551 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray("file_context");
/*      */     
/*  553 */     if (menu_items.length > 0)
/*      */     {
/*  555 */       org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[] fileInfos = new org.gudy.azureus2.plugins.disk.DiskManagerFileInfo[all_files.size()];
/*  556 */       for (int i = 0; i < all_files.size(); i++) {
/*  557 */         fileInfos[i] = ((org.gudy.azureus2.plugins.disk.DiskManagerFileInfo)PluginCoreUtils.convert(all_files.get(i), false));
/*      */       }
/*      */       
/*  560 */       MenuBuildUtils.addPluginMenuItems(menu_items, menu, false, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(fileInfos));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void rename(final TableView tv, Object[] datasources, boolean rename_it, boolean retarget_it)
/*      */   {
/*  572 */     if (datasources.length == 0) {
/*  573 */       return;
/*      */     }
/*      */     
/*  576 */     String save_dir = null;
/*  577 */     if ((!rename_it) && (retarget_it))
/*      */     {
/*  579 */       String s = MessageText.getString("label.num_selected", new String[] { Integer.toString(datasources.length) });
/*      */       
/*      */ 
/*  582 */       save_dir = askForSaveDirectory((org.gudy.azureus2.core3.disk.DiskManagerFileInfo)datasources[0], s);
/*  583 */       if (save_dir == null) {
/*  584 */         return;
/*      */       }
/*      */     }
/*      */     
/*  588 */     final List<DownloadManager> pausedDownloads = new ArrayList(0);
/*      */     
/*  590 */     final AESemaphore task_sem = new AESemaphore("tasksem");
/*      */     
/*  592 */     List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> affected_files = new ArrayList();
/*      */     try
/*      */     {
/*  595 */       for (int i = 0; i < datasources.length; i++)
/*  596 */         if ((datasources[i] instanceof DownloadManager)) {
/*  597 */           AdvRenameWindow window = new AdvRenameWindow();
/*  598 */           window.open((DownloadManager)datasources[i]);
/*      */ 
/*      */         }
/*  601 */         else if ((datasources[i] instanceof org.gudy.azureus2.core3.disk.DiskManagerFileInfo))
/*      */         {
/*      */ 
/*  604 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)datasources[i];
/*  605 */           File existing_file = fileInfo.getFile(true);
/*  606 */           File f_target = null;
/*  607 */           if ((rename_it) && (retarget_it)) {
/*  608 */             String s_target = askForRetargetedFilename(fileInfo);
/*  609 */             if (s_target != null)
/*  610 */               f_target = new File(s_target);
/*  611 */           } else if (rename_it) {
/*  612 */             String s_target = askForRenameFilename(fileInfo);
/*  613 */             if (s_target != null) {
/*  614 */               f_target = new File(existing_file.getParentFile(), s_target);
/*      */             }
/*      */           } else {
/*  617 */             f_target = new File(save_dir, existing_file.getName());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  622 */           if (f_target == null) {
/*      */             return;
/*      */           }
/*      */           
/*  626 */           DownloadManager manager = fileInfo.getDownloadManager();
/*  627 */           if ((!pausedDownloads.contains(manager)) && 
/*  628 */             (manager.pause())) {
/*  629 */             pausedDownloads.add(manager);
/*      */           }
/*      */           
/*      */ 
/*  633 */           boolean dont_delete_existing = false;
/*      */           
/*  635 */           if (f_target.exists())
/*      */           {
/*      */ 
/*      */ 
/*  639 */             if (f_target.equals(existing_file)) {
/*      */               continue;
/*      */             }
/*      */             
/*  643 */             if (retarget_it)
/*      */             {
/*      */ 
/*      */ 
/*  647 */               if (!checkRetargetOK(fileInfo, f_target))
/*      */                 continue;
/*  649 */               dont_delete_existing = true;
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  655 */               if ((existing_file.exists()) && (!askCanOverwrite(existing_file))) {
/*      */                 continue;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  666 */           final File ff_target = f_target;
/*      */           
/*  668 */           final boolean f_dont_delete_existing = dont_delete_existing;
/*      */           
/*  670 */           affected_files.add(fileInfo);
/*      */           
/*  672 */           Utils.getOffOfSWTThread(new AERunnable() {
/*      */             public void runSupport() {
/*  674 */               FilesViewMenuUtil.moveFile(this.val$fileInfo.getDownloadManager(), this.val$fileInfo, ff_target, f_dont_delete_existing, new Runnable()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  684 */                   FilesViewMenuUtil.14.this.val$task_sem.release();
/*      */                 }
/*      */               });
/*      */             }
/*      */           });
/*      */         }
/*      */     } finally {
/*  691 */       if (affected_files.size() > 0)
/*      */       {
/*  693 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/*  695 */             for (int i = 0; i < this.val$affected_files.size(); i++) {
/*  696 */               task_sem.reserve();
/*      */             }
/*      */             
/*  699 */             for (DownloadManager manager : pausedDownloads) {
/*  700 */               manager.resume();
/*      */             }
/*      */             
/*  703 */             FilesViewMenuUtil.invalidateRows(tv, this.val$affected_files);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void invalidateRows(TableView tv, List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> files)
/*      */   {
/*  715 */     if (tv == null)
/*      */     {
/*  717 */       return;
/*      */     }
/*      */     
/*  720 */     Set<TableRowCore> done = new HashSet();
/*      */     
/*  722 */     for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : files)
/*      */     {
/*  724 */       TableRowCore row = tv.getRow(file);
/*      */       
/*  726 */       if (row == null)
/*      */       {
/*  728 */         row = tv.getRow(file.getDownloadManager());
/*      */         
/*  730 */         if (row != null)
/*      */         {
/*  732 */           TableRowCore[] subrows = row.getSubRowsWithNull();
/*      */           
/*  734 */           if (subrows != null)
/*      */           {
/*  736 */             for (TableRowCore sr : subrows)
/*      */             {
/*  738 */               if (sr.getDataSource(true) == file)
/*      */               {
/*  740 */                 row = sr;
/*      */                 
/*  742 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  749 */       if ((row != null) && (!done.contains(row)))
/*      */       {
/*  751 */         done.add(row);
/*      */         
/*  753 */         row.invalidate(true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void changePriority(Object type, List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> file_list)
/*      */   {
/*  760 */     if ((file_list == null) || (file_list.size() == 0)) {
/*  761 */       return;
/*      */     }
/*      */     
/*  764 */     if (type == PRIORITY_NUMERIC) {
/*  765 */       changePriorityManual(file_list);
/*  766 */       return; }
/*  767 */     if (type == PRIORITY_NUMERIC_AUTO) {
/*  768 */       changePriorityAuto(file_list);
/*  769 */       return;
/*      */     }
/*      */     
/*  772 */     Map<DownloadManager, ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo>> mapDMtoDMFI = new IdentityHashMap();
/*      */     
/*  774 */     for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : file_list)
/*      */     {
/*  776 */       DownloadManager dm = file.getDownloadManager();
/*  777 */       ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> listFileInfos = (ArrayList)mapDMtoDMFI.get(dm);
/*  778 */       if (listFileInfos == null) {
/*  779 */         listFileInfos = new ArrayList(1);
/*  780 */         mapDMtoDMFI.put(dm, listFileInfos);
/*      */       }
/*  782 */       listFileInfos.add(file);
/*      */     }
/*  784 */     boolean skipped = (type == PRIORITY_SKIPPED) || (type == PRIORITY_DELETE);
/*  785 */     boolean delete_action = type == PRIORITY_DELETE;
/*  786 */     for (DownloadManager dm : mapDMtoDMFI.keySet()) {
/*  787 */       ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> list = (ArrayList)mapDMtoDMFI.get(dm);
/*  788 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] fileInfos = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo[])list.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[0]);
/*      */       
/*  790 */       if (type == PRIORITY_NORMAL)
/*      */       {
/*  792 */         dm.setFilePriorities(fileInfos, 0);
/*      */       }
/*  794 */       else if (type == PRIORITY_HIGH)
/*      */       {
/*  796 */         dm.setFilePriorities(fileInfos, 1);
/*      */       }
/*  798 */       else if (type == PRIORITY_LOW)
/*      */       {
/*  800 */         dm.setFilePriorities(fileInfos, -1);
/*      */       }
/*      */       
/*  803 */       boolean paused = setSkipped(dm, fileInfos, skipped, delete_action);
/*      */       
/*  805 */       if (paused)
/*      */       {
/*  807 */         dm.resume();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void changePriorityManual(List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> file_list)
/*      */   {
/*  814 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("FilesView.dialog.priority.title", "FilesView.dialog.priority.text");
/*      */     
/*      */ 
/*  817 */     entryWindow.prompt(new UIInputReceiverListener() {
/*      */       public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/*  819 */         if (!entryWindow.hasSubmittedInput()) {
/*  820 */           return;
/*      */         }
/*  822 */         String sReturn = entryWindow.getSubmittedInput();
/*      */         
/*  824 */         if (sReturn == null) {
/*  825 */           return;
/*      */         }
/*  827 */         int priority = 0;
/*      */         try {
/*  829 */           priority = Integer.valueOf(sReturn).intValue();
/*      */         }
/*      */         catch (NumberFormatException er) {
/*  832 */           Debug.out("Invalid priority: " + sReturn);
/*      */           
/*  834 */           new MessageBoxShell(33, MessageText.getString("FilePriority.invalid.title"), MessageText.getString("FilePriority.invalid.text", new String[] { sReturn })).open(null);
/*      */           
/*      */ 
/*      */ 
/*  838 */           return;
/*      */         }
/*      */         
/*  841 */         Map<DownloadManager, ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo>> mapDMtoDMFI = new IdentityHashMap();
/*      */         
/*  843 */         for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : this.val$file_list) {
/*  844 */           DownloadManager dm = file.getDownloadManager();
/*  845 */           ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> listFileInfos = (ArrayList)mapDMtoDMFI.get(dm);
/*  846 */           if (listFileInfos == null) {
/*  847 */             listFileInfos = new ArrayList(1);
/*  848 */             mapDMtoDMFI.put(dm, listFileInfos);
/*      */           }
/*  850 */           listFileInfos.add(file);
/*      */           
/*  852 */           file.setPriority(priority);
/*      */         }
/*      */         
/*  855 */         for (DownloadManager dm : mapDMtoDMFI.keySet()) {
/*  856 */           ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> list = (ArrayList)mapDMtoDMFI.get(dm);
/*  857 */           org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] fileInfos = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo[])list.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[0]);
/*  858 */           boolean paused = FilesViewMenuUtil.setSkipped(dm, fileInfos, false, false);
/*      */           
/*  860 */           if (paused)
/*      */           {
/*  862 */             dm.resume();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void changePriorityAuto(List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> file_list)
/*      */   {
/*  873 */     int priority = 0;
/*      */     
/*  875 */     Map<DownloadManager, ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo>> mapDMtoDMFI = new IdentityHashMap();
/*      */     
/*  877 */     for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : file_list) {
/*  878 */       DownloadManager dm = file.getDownloadManager();
/*      */       
/*  880 */       ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> listFileInfos = (ArrayList)mapDMtoDMFI.get(dm);
/*  881 */       if (listFileInfos == null) {
/*  882 */         listFileInfos = new ArrayList(1);
/*  883 */         mapDMtoDMFI.put(dm, listFileInfos);
/*      */       }
/*  885 */       listFileInfos.add(file);
/*      */       
/*  887 */       file.setPriority(priority++);
/*      */     }
/*      */     
/*  890 */     for (Map.Entry<DownloadManager, ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo>> entry : mapDMtoDMFI.entrySet())
/*      */     {
/*  892 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] all_files = ((DownloadManager)entry.getKey()).getDiskManagerFileInfoSet().getFiles();
/*      */       
/*  894 */       ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> files = (ArrayList)entry.getValue();
/*      */       
/*  896 */       next_priority = 0;
/*      */       
/*  898 */       if (all_files.length != files.size())
/*      */       {
/*  900 */         Set<Integer> affected_indexes = new HashSet();
/*      */         
/*  902 */         for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : files)
/*      */         {
/*  904 */           affected_indexes.add(Integer.valueOf(file.getIndex()));
/*      */         }
/*      */         
/*  907 */         for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : all_files)
/*      */         {
/*  909 */           if ((!affected_indexes.contains(Integer.valueOf(file.getIndex()))) && (!file.isSkipped()))
/*      */           {
/*  911 */             next_priority = Math.max(next_priority, file.getPriority() + 1);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  916 */       next_priority += files.size();
/*      */       
/*  918 */       for (org.gudy.azureus2.core3.disk.DiskManagerFileInfo file : files)
/*      */       {
/*  920 */         file.setPriority(--next_priority);
/*      */       }
/*      */     }
/*      */     int next_priority;
/*  924 */     for (DownloadManager dm : mapDMtoDMFI.keySet()) {
/*  925 */       ArrayList<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> list = (ArrayList)mapDMtoDMFI.get(dm);
/*  926 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] fileInfos = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo[])list.toArray(new org.gudy.azureus2.core3.disk.DiskManagerFileInfo[0]);
/*  927 */       boolean paused = setSkipped(dm, fileInfos, false, false);
/*      */       
/*  929 */       if (paused)
/*      */       {
/*  931 */         dm.resume();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static String askForRenameFilename(org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo) {
/*  937 */     SimpleTextEntryWindow dialog = new SimpleTextEntryWindow("FilesView.rename.filename.title", "FilesView.rename.filename.text");
/*      */     
/*  939 */     String file_name = fileInfo.getFile(true).getName();
/*  940 */     dialog.setPreenteredText(file_name, false);
/*      */     
/*  942 */     int pos = file_name.lastIndexOf('.');
/*      */     
/*  944 */     if (pos > 0)
/*      */     {
/*  946 */       String suffix = fileInfo.getDownloadManager().getDownloadState().getAttribute("incompfilesuffix");
/*      */       
/*  948 */       if ((suffix != null) && (file_name.substring(pos).equals(suffix)))
/*      */       {
/*  950 */         pos--;
/*      */         
/*  952 */         while ((pos > 0) && (file_name.charAt(pos) != '.'))
/*      */         {
/*  954 */           pos--;
/*      */         }
/*      */       }
/*      */       
/*  958 */       if (pos > 0)
/*      */       {
/*  960 */         dialog.selectPreenteredTextRange(new int[] { 0, pos });
/*      */       }
/*      */     }
/*      */     
/*  964 */     dialog.allowEmptyInput(false);
/*  965 */     dialog.prompt();
/*  966 */     if (!dialog.hasSubmittedInput()) {
/*  967 */       return null;
/*      */     }
/*  969 */     return dialog.getSubmittedInput();
/*      */   }
/*      */   
/*      */   private static String askForRetargetedFilename(org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo) {
/*  973 */     FileDialog fDialog = new FileDialog(Utils.findAnyShell(), 131072);
/*  974 */     File existing_file = fileInfo.getFile(true);
/*  975 */     fDialog.setFilterPath(existing_file.getParent());
/*  976 */     fDialog.setFileName(existing_file.getName());
/*  977 */     fDialog.setText(MessageText.getString("FilesView.rename.choose.path"));
/*  978 */     return fDialog.open();
/*      */   }
/*      */   
/*      */ 
/*      */   private static String askForSaveDirectory(org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo, String message)
/*      */   {
/*  984 */     Shell anyShell = Utils.findAnyShell();
/*  985 */     Shell shell = new Shell(anyShell.getDisplay(), 8);
/*  986 */     shell.setSize(1, 1);
/*  987 */     shell.open();
/*      */     
/*  989 */     DirectoryDialog dDialog = new DirectoryDialog(shell, 139264);
/*      */     
/*  991 */     File current_dir = fileInfo.getFile(true).getParentFile();
/*  992 */     if (!current_dir.isDirectory()) {
/*  993 */       current_dir = fileInfo.getDownloadManager().getSaveLocation();
/*      */     }
/*  995 */     dDialog.setFilterPath(current_dir.getPath());
/*  996 */     dDialog.setText(MessageText.getString("FilesView.rename.choose.path.dir"));
/*  997 */     if (message != null) {
/*  998 */       dDialog.setMessage(message);
/*      */     }
/* 1000 */     String open = dDialog.open();
/* 1001 */     shell.close();
/* 1002 */     return open;
/*      */   }
/*      */   
/*      */   private static boolean askCanOverwrite(File file) {
/* 1006 */     MessageBoxShell mb = new MessageBoxShell(288, MessageText.getString("FilesView.rename.confirm.delete.title"), MessageText.getString("FilesView.rename.confirm.delete.text", new String[] { file.toString() }));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1012 */     mb.setDefaultButtonUsingStyle(32);
/* 1013 */     mb.setRememberOnlyIfButton(0);
/* 1014 */     mb.setRemember("FilesView.messagebox.rename.id", true, null);
/* 1015 */     mb.setLeftImage(8);
/* 1016 */     mb.open(null);
/* 1017 */     return mb.waitUntilClosed() == 32;
/*      */   }
/*      */   
/*      */   private static boolean checkRetargetOK(org.gudy.azureus2.core3.disk.DiskManagerFileInfo info, File target)
/*      */   {
/* 1022 */     if (!target.exists())
/*      */     {
/* 1024 */       return true;
/*      */     }
/*      */     
/* 1027 */     if (info.getTorrentFile().getLength() == target.length())
/*      */     {
/* 1029 */       return true;
/*      */     }
/*      */     
/* 1032 */     MessageBoxShell mb = new MessageBoxShell(288, MessageText.getString("FilesView.retarget.confirm.title"), MessageText.getString("FilesView.retarget.confirm.text"));
/*      */     
/*      */ 
/*      */ 
/* 1036 */     mb.setDefaultButtonUsingStyle(32);
/*      */     
/* 1038 */     mb.setLeftImage(8);
/*      */     
/* 1040 */     mb.open(null);
/*      */     
/* 1042 */     return mb.waitUntilClosed() == 32;
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
/*      */   private static void moveFile(final DownloadManager manager, org.gudy.azureus2.core3.disk.DiskManagerFileInfo fileInfo, final File target, boolean dont_delete_existing, final Runnable done)
/*      */   {
/* 1058 */     manager.setUserData("is_changing_links", Boolean.valueOf(true));
/*      */     
/* 1060 */     if (dont_delete_existing)
/*      */     {
/* 1062 */       manager.setUserData("set_link_dont_delete_existing", Boolean.valueOf(true));
/*      */     }
/*      */     
/*      */     try
/*      */     {
/* 1067 */       FileUtil.runAsTask(new AzureusCoreOperationTask() {
/*      */         public void run(AzureusCoreOperation operation) {
/* 1069 */           boolean went_async = false;
/*      */           
/*      */           try
/*      */           {
/* 1073 */             boolean ok = this.val$fileInfo.setLink(target);
/*      */             
/* 1075 */             if (!ok)
/*      */             {
/* 1077 */               new MessageBoxShell(33, MessageText.getString("FilesView.rename.failed.title"), MessageText.getString("FilesView.rename.failed.text")).open(new UserPrompterResultListener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void prompterClosed(int result)
/*      */                 {
/*      */ 
/* 1084 */                   if (FilesViewMenuUtil.17.this.val$done != null) {
/* 1085 */                     FilesViewMenuUtil.17.this.val$done.run();
/*      */                   }
/*      */                   
/*      */                 }
/* 1089 */               });
/* 1090 */               went_async = true;
/*      */             }
/*      */           } finally {
/* 1093 */             manager.setUserData("is_changing_links", Boolean.valueOf(false));
/* 1094 */             manager.setUserData("set_link_dont_delete_existing", null);
/*      */             
/* 1096 */             if (!went_async)
/*      */             {
/* 1098 */               if (done != null) {
/* 1099 */                 done.run();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     } catch (Throwable e) {
/* 1106 */       manager.setUserData("is_changing_links", Boolean.valueOf(false));
/* 1107 */       manager.setUserData("set_link_dont_delete_existing", null);
/*      */       
/* 1109 */       if (done != null) {
/* 1110 */         done.run();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static boolean setSkipped(DownloadManager manager, org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] infos, boolean skipped, boolean delete_action)
/*      */   {
/* 1121 */     if (!manager.isPersistent()) {
/* 1122 */       for (int i = 0; i < infos.length; i++) {
/* 1123 */         infos[i].setSkipped(skipped);
/*      */       }
/* 1125 */       return false;
/*      */     }
/* 1127 */     int[] existing_storage_types = manager.getStorageType(infos);
/* 1128 */     int nbFiles = manager.getDiskManagerFileInfoSet().nbFiles();
/* 1129 */     boolean[] setLinear = new boolean[nbFiles];
/* 1130 */     boolean[] setCompact = new boolean[nbFiles];
/* 1131 */     boolean[] setReorder = new boolean[nbFiles];
/* 1132 */     boolean[] setReorderCompact = new boolean[nbFiles];
/* 1133 */     int compactCount = 0;
/* 1134 */     int linearCount = 0;
/* 1135 */     int reorderCount = 0;
/* 1136 */     int reorderCompactCount = 0;
/*      */     
/* 1138 */     if (infos.length > 1) {}
/*      */     
/*      */ 
/*      */ 
/* 1142 */     File save_location = manager.getAbsoluteSaveLocation();
/* 1143 */     boolean root_exists = (save_location.isDirectory()) || ((infos.length <= 1) && (save_location.exists()));
/*      */     
/*      */ 
/* 1146 */     boolean type_has_been_changed = false;
/* 1147 */     boolean requires_pausing = false;
/*      */     
/* 1149 */     for (int i = 0; i < infos.length; i++) {
/* 1150 */       int existing_storage_type = existing_storage_types[i];
/*      */       int non_compact_target;
/*      */       int compact_target;
/* 1153 */       int non_compact_target; if ((existing_storage_type == 2) || (existing_storage_type == 1)) {
/* 1154 */         int compact_target = 2;
/* 1155 */         non_compact_target = 1;
/*      */       } else {
/* 1157 */         compact_target = 4;
/* 1158 */         non_compact_target = 3; }
/*      */       int new_storage_type;
/*      */       int new_storage_type;
/* 1161 */       if (skipped)
/*      */       {
/*      */ 
/*      */ 
/* 1165 */         File existing_file = infos[i].getFile(true);
/*      */         
/*      */         boolean perform_check;
/*      */         
/*      */         boolean perform_check;
/*      */         
/* 1171 */         if (root_exists) {
/* 1172 */           perform_check = true; } else { boolean perform_check;
/* 1173 */           if (FileUtil.isAncestorOf(save_location, existing_file)) {
/* 1174 */             perform_check = false;
/*      */           } else
/* 1176 */             perform_check = true;
/*      */         }
/*      */         int new_storage_type;
/* 1179 */         if ((perform_check) && (existing_file.exists())) { int new_storage_type;
/* 1180 */           if (delete_action) {
/* 1181 */             MessageBoxShell mb = new MessageBoxShell(288, MessageText.getString("FilesView.rename.confirm.delete.title"), MessageText.getString("FilesView.rename.confirm.delete.text", new String[] { existing_file.toString() }));
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1187 */             mb.setDefaultButtonUsingStyle(32);
/* 1188 */             mb.setRememberOnlyIfButton(0);
/* 1189 */             mb.setRemember("FilesView.messagebox.delete.id", false, null);
/* 1190 */             mb.setLeftImage(8);
/* 1191 */             mb.open(null);
/*      */             
/* 1193 */             boolean wants_to_delete = mb.waitUntilClosed() == 32;
/*      */             int new_storage_type;
/* 1195 */             if (wants_to_delete)
/*      */             {
/* 1197 */               new_storage_type = compact_target;
/*      */             }
/*      */             else
/*      */             {
/* 1201 */               new_storage_type = non_compact_target;
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/* 1208 */             new_storage_type = non_compact_target;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1213 */           new_storage_type = compact_target;
/*      */         }
/*      */       } else {
/* 1216 */         new_storage_type = non_compact_target;
/*      */       }
/*      */       
/* 1219 */       boolean has_changed = existing_storage_type != new_storage_type;
/*      */       
/* 1221 */       type_has_been_changed |= has_changed;
/*      */       
/* 1223 */       if (has_changed)
/*      */       {
/* 1225 */         requires_pausing |= ((new_storage_type == 2) || (new_storage_type == 4));
/*      */         
/* 1227 */         if (new_storage_type == 2) {
/* 1228 */           setCompact[infos[i].getIndex()] = true;
/* 1229 */           compactCount++;
/* 1230 */         } else if (new_storage_type == 1) {
/* 1231 */           setLinear[infos[i].getIndex()] = true;
/* 1232 */           linearCount++;
/* 1233 */         } else if (new_storage_type == 3) {
/* 1234 */           setReorder[infos[i].getIndex()] = true;
/* 1235 */           reorderCount++;
/* 1236 */         } else if (new_storage_type == 4) {
/* 1237 */           setReorderCompact[infos[i].getIndex()] = true;
/* 1238 */           reorderCompactCount++;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1243 */     boolean ok = true;
/* 1244 */     boolean paused = false;
/* 1245 */     if (type_has_been_changed) {
/* 1246 */       if (requires_pausing)
/* 1247 */         paused = manager.pause();
/* 1248 */       if (linearCount > 0) {
/* 1249 */         ok &= Arrays.equals(setLinear, manager.getDiskManagerFileInfoSet().setStorageTypes(setLinear, 1));
/*      */       }
/*      */       
/*      */ 
/* 1253 */       if (compactCount > 0) {
/* 1254 */         ok &= Arrays.equals(setCompact, manager.getDiskManagerFileInfoSet().setStorageTypes(setCompact, 2));
/*      */       }
/*      */       
/*      */ 
/* 1258 */       if (reorderCount > 0) {
/* 1259 */         ok &= Arrays.equals(setReorder, manager.getDiskManagerFileInfoSet().setStorageTypes(setReorder, 3));
/*      */       }
/*      */       
/*      */ 
/* 1263 */       if (reorderCompactCount > 0) {
/* 1264 */         ok &= Arrays.equals(setReorderCompact, manager.getDiskManagerFileInfoSet().setStorageTypes(setReorderCompact, 4));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1270 */     if (ok) {
/* 1271 */       for (int i = 0; i < infos.length; i++) {
/* 1272 */         infos[i].setSkipped(skipped);
/*      */       }
/*      */     }
/*      */     
/* 1276 */     return paused;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void revertFiles(TableView<?> tv, DownloadManager[] dms)
/*      */   {
/* 1284 */     List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> files = new ArrayList();
/*      */     
/* 1286 */     for (DownloadManager dm : dms)
/*      */     {
/* 1288 */       org.gudy.azureus2.core3.disk.DiskManagerFileInfo[] dm_files = dm.getDiskManagerFileInfoSet().getFiles();
/*      */       
/* 1290 */       LinkFileMap links = dm.getDownloadState().getFileLinks();
/*      */       
/* 1292 */       Iterator<LinkFileMap.Entry> it = links.entryIterator();
/*      */       
/* 1294 */       while (it.hasNext())
/*      */       {
/* 1296 */         LinkFileMap.Entry entry = (LinkFileMap.Entry)it.next();
/*      */         
/* 1298 */         if (entry.getToFile() != null)
/*      */         {
/* 1300 */           files.add(dm_files[entry.getIndex()]);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1305 */     if (files.size() > 0)
/*      */     {
/* 1307 */       revertFiles(tv, files);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void revertFiles(final TableView<?> tv, List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> files)
/*      */   {
/* 1316 */     final List<DownloadManager> paused = new ArrayList();
/*      */     
/* 1318 */     final AESemaphore task_sem = new AESemaphore("tasksem");
/*      */     
/* 1320 */     List<org.gudy.azureus2.core3.disk.DiskManagerFileInfo> affected_files = new ArrayList();
/*      */     try
/*      */     {
/* 1323 */       for (int i = 0; i < files.size(); i++)
/*      */       {
/* 1325 */         final org.gudy.azureus2.core3.disk.DiskManagerFileInfo file_info = (org.gudy.azureus2.core3.disk.DiskManagerFileInfo)files.get(i);
/*      */         
/* 1327 */         if (file_info != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1332 */           final File file_nolink = file_info.getFile(false);
/*      */           
/* 1334 */           DownloadManager manager = file_info.getDownloadManager();
/*      */           
/* 1336 */           File target = file_info.getDownloadManager().getDownloadState().getFileLink(file_info.getIndex(), file_nolink);
/*      */           
/* 1338 */           if (target != null)
/*      */           {
/* 1340 */             if (target != file_nolink)
/*      */             {
/* 1342 */               if (!target.equals(file_nolink))
/*      */               {
/* 1344 */                 if (!paused.contains(manager))
/*      */                 {
/* 1346 */                   if (manager.pause())
/*      */                   {
/* 1348 */                     paused.add(manager);
/*      */                   }
/*      */                 }
/*      */                 
/* 1352 */                 affected_files.add(file_info);
/*      */                 
/* 1354 */                 Utils.getOffOfSWTThread(new AERunnable() {
/*      */                   public void runSupport() {
/* 1356 */                     FilesViewMenuUtil.moveFile(this.val$manager, file_info, file_nolink, true, new Runnable()
/*      */                     {
/*      */ 
/*      */ 
/*      */ 
/*      */                       public void run()
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/* 1366 */                         FilesViewMenuUtil.18.this.val$task_sem.release();
/*      */                       }
/*      */                     });
/*      */                   }
/*      */                 });
/*      */               } } }
/*      */         }
/*      */       }
/*      */     } finally {
/* 1375 */       if (affected_files.size() > 0)
/*      */       {
/* 1377 */         Utils.getOffOfSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 1379 */             for (int i = 0; i < this.val$affected_files.size(); i++) {
/* 1380 */               task_sem.reserve();
/*      */             }
/* 1382 */             for (DownloadManager manager : paused) {
/* 1383 */               manager.resume();
/*      */             }
/*      */             
/* 1386 */             FilesViewMenuUtil.invalidateRows(tv, this.val$affected_files);
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/FilesViewMenuUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */