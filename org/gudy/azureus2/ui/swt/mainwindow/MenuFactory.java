/*      */ package org.gudy.azureus2.ui.swt.mainwindow;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedLimitHandler;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.common.table.TableView;
/*      */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*      */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.net.URL;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.events.MenuEvent;
/*      */ import org.eclipse.swt.events.MenuListener;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.FileDialog;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.eclipse.swt.widgets.Menu;
/*      */ import org.eclipse.swt.widgets.MessageBox;
/*      */ import org.eclipse.swt.widgets.Shell;
/*      */ import org.eclipse.swt.widgets.Widget;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*      */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*      */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRow;
/*      */ import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
/*      */ import org.gudy.azureus2.plugins.update.Update;
/*      */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*      */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*      */ import org.gudy.azureus2.ui.swt.BlockedIpsWindow;
/*      */ import org.gudy.azureus2.ui.swt.KeyBindings;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuItemPluginMenuControllerImpl;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*      */ import org.gudy.azureus2.ui.swt.TextViewerWindow.TextViewerWindowListener;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.shell.ShellManager;
/*      */ import org.gudy.azureus2.ui.swt.config.wizard.ConfigureWizard;
/*      */ import org.gudy.azureus2.ui.swt.debug.UIDebugGenerator;
/*      */ import org.gudy.azureus2.ui.swt.help.AboutWindow;
/*      */ import org.gudy.azureus2.ui.swt.help.HealthHelpWindow;
/*      */ import org.gudy.azureus2.ui.swt.importtorrent.wizard.ImportTorrentWizard;
/*      */ import org.gudy.azureus2.ui.swt.minibar.AllTransfersBar;
/*      */ import org.gudy.azureus2.ui.swt.minibar.MiniBarManager;
/*      */ import org.gudy.azureus2.ui.swt.nat.NatTestWindow;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInputReceiver;
/*      */ import org.gudy.azureus2.ui.swt.pluginsinstaller.InstallPluginWizard;
/*      */ import org.gudy.azureus2.ui.swt.pluginsuninstaller.UnInstallPluginWizard;
/*      */ import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
/*      */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*      */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*      */ import org.gudy.azureus2.ui.swt.speedtest.SpeedTestWizard;
/*      */ import org.gudy.azureus2.ui.swt.update.UpdateMonitor;
/*      */ import org.gudy.azureus2.ui.swt.views.stats.StatsView;
/*      */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
/*      */ import org.gudy.azureus2.ui.swt.views.table.utils.TableContextMenuManager;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
/*      */ 
/*      */ public class MenuFactory implements IMenuConstants
/*      */ {
/*  108 */   private static boolean isAZ3 = "az3".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui"));
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createFileMenuItem(Menu menuParent) {
/*  111 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.file");
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createTransfersMenuItem(Menu menuParent) {
/*  115 */     org.eclipse.swt.widgets.MenuItem transferMenuItem = createTopLevelMenuItem(menuParent, "MainWindow.menu.transfers");
/*      */     
/*      */ 
/*  118 */     Menu transferMenu = transferMenuItem.getMenu();
/*      */     
/*  120 */     addStartAllMenuItem(transferMenu);
/*  121 */     addStopAllMenuItem(transferMenu);
/*      */     
/*  123 */     org.eclipse.swt.widgets.MenuItem itemPause = addPauseMenuItem(transferMenu);
/*  124 */     org.eclipse.swt.widgets.MenuItem itemPauseFor = addPauseForMenuItem(transferMenu);
/*  125 */     final org.eclipse.swt.widgets.MenuItem itemResume = addResumeMenuItem(transferMenu);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  131 */     transferMenu.addMenuListener(new MenuListener() {
/*      */       public void menuShown(MenuEvent menu) {
/*  133 */         if (!AzureusCoreFactory.isCoreRunning()) {
/*  134 */           this.val$itemPause.setEnabled(true);
/*  135 */           itemResume.setEnabled(true);
/*      */         } else {
/*  137 */           AzureusCore core = AzureusCoreFactory.getSingleton();
/*  138 */           this.val$itemPause.setEnabled(core.getGlobalManager().canPauseDownloads());
/*  139 */           itemResume.setEnabled(core.getGlobalManager().canResumeDownloads());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void menuHidden(MenuEvent menu)
/*      */       {
/*  146 */         this.val$itemPause.setEnabled(true);
/*  147 */         itemResume.setEnabled(true);
/*      */       }
/*      */       
/*  150 */     });
/*  151 */     return transferMenuItem;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createViewMenuItem(Menu menuParent) {
/*  155 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.view");
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createAdvancedMenuItem(Menu menuParent) {
/*  159 */     return createTopLevelMenuItem(menuParent, "v3.MainWindow.tab.advanced");
/*      */   }
/*      */   
/*      */   public static Menu createTorrentMenuItem(Menu menuParent) {
/*  163 */     Menu torrentMenu = createTopLevelMenuItem(menuParent, "MainWindow.menu.torrent").getMenu();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  171 */     MenuBuildUtils.addMaintenanceListenerForMenu(torrentMenu, new MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */       public void buildMenu(Menu menu, MenuEvent menuEvent) {
/*  174 */         MenuFactory.buildTorrentMenu(menu);
/*      */       }
/*  176 */     });
/*  177 */     return torrentMenu;
/*      */   }
/*      */   
/*      */   public static void buildTorrentMenu(Menu menu) {
/*  181 */     DownloadManager[] current_dls = (DownloadManager[])menu.getData("downloads");
/*      */     
/*  183 */     current_dls = ManagerUtils.cleanUp(current_dls);
/*      */     
/*  185 */     if (current_dls.length == 0) {
/*  186 */       return;
/*      */     }
/*      */     
/*      */ 
/*  190 */     if (AzureusCoreFactory.isCoreRunning()) {
/*  191 */       boolean is_detailed_view = ((Boolean)menu.getData("is_detailed_view")).booleanValue();
/*  192 */       TableViewSWT<?> tv = (TableViewSWT)menu.getData("TableView");
/*  193 */       AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */       
/*  195 */       org.gudy.azureus2.ui.swt.TorrentUtil.fillTorrentMenu(menu, current_dls, core, menu.getShell(), !is_detailed_view, 0, tv);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  201 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray(new String[] { "torrentmenu", "download_context" });
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  207 */     Object[] plugin_dls = org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl.getDownloadStatic(current_dls);
/*      */     
/*  209 */     if (menu_items.length > 0) {
/*  210 */       addSeparatorMenuItem(menu);
/*      */       
/*  212 */       MenuBuildUtils.addPluginMenuItems(menu_items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(plugin_dls));
/*      */     }
/*      */     
/*      */ 
/*  216 */     menu_items = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  223 */     String table_to_use = null;
/*  224 */     for (int i = 0; i < current_dls.length; i++) {
/*  225 */       String table_name = current_dls[i].isDownloadComplete(false) ? "MySeeders" : "MyTorrents";
/*      */       
/*      */ 
/*  228 */       if ((table_to_use == null) || (table_to_use.equals(table_name))) {
/*  229 */         table_to_use = table_name;
/*      */       } else {
/*  231 */         table_to_use = null;
/*  232 */         break;
/*      */       }
/*      */     }
/*      */     
/*  236 */     if (table_to_use != null) {
/*  237 */       menu_items = TableContextMenuManager.getInstance().getAllAsArray(table_to_use);
/*      */     }
/*      */     
/*      */ 
/*  241 */     if (menu_items != null) {
/*  242 */       addSeparatorMenuItem(menu);
/*      */       
/*  244 */       TableRow[] dls_as_rows = null;
/*  245 */       dls_as_rows = new TableRow[plugin_dls.length];
/*  246 */       for (int i = 0; i < plugin_dls.length; i++) {
/*  247 */         dls_as_rows[i] = wrapAsRow(plugin_dls[i], table_to_use);
/*      */       }
/*      */       
/*  250 */       MenuBuildUtils.addPluginMenuItems(menu_items, menu, true, true, new MenuBuildUtils.MenuItemPluginMenuControllerImpl(dls_as_rows));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem createToolsMenuItem(Menu menuParent)
/*      */   {
/*  257 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.tools");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem createPluginsMenuItem(Menu menuParent, final boolean includeGetPluginsMenu)
/*      */   {
/*  269 */     org.eclipse.swt.widgets.MenuItem pluginsMenuItem = createTopLevelMenuItem(menuParent, "MainWindow.menu.view.plugins");
/*      */     
/*  271 */     MenuBuildUtils.addMaintenanceListenerForMenu(pluginsMenuItem.getMenu(), new MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */       public void buildMenu(Menu menu, MenuEvent menuEvent) {
/*  274 */         PluginsMenuHelper.getInstance().buildPluginMenu(menu, this.val$menuParent.getShell(), includeGetPluginsMenu);
/*      */       }
/*      */       
/*      */ 
/*  278 */     });
/*  279 */     return pluginsMenuItem;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createWindowMenuItem(Menu menuParent) {
/*  283 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.window");
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createHelpMenuItem(Menu menuParent) {
/*  287 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.help");
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addCreateMenuItem(Menu menuParent) {
/*  291 */     org.eclipse.swt.widgets.MenuItem file_create = addMenuItem(menuParent, "MainWindow.menu.file.create", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  294 */         new org.gudy.azureus2.ui.swt.maketorrent.NewTorrentWizard(e.display);
/*      */       }
/*  296 */     });
/*  297 */     return file_create;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createOpenMenuItem(Menu menuParent) {
/*  301 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.file.open");
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addLogsViewMenuItem(Menu menuParent) {
/*  305 */     return createTopLevelMenuItem(menuParent, "MainWindow.menu.view.plugins.logViews");
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addOpenTorrentMenuItem(Menu menuParent) {
/*  309 */     addMenuItem(menuParent, "MainWindow.menu.file.open.torrent", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  311 */         UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentWindow();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*  317 */   public static org.eclipse.swt.widgets.MenuItem addOpenURIMenuItem(Menu menuParent) { addMenuItem(menuParent, "MainWindow.menu.file.open.uri", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  319 */         UIFunctionsManagerSWT.getUIFunctionsSWT().openTorrentWindow();
/*      */       }
/*      */     }); }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addOpenTorrentForTrackingMenuItem(Menu menuParent) {
/*  324 */     org.eclipse.swt.widgets.MenuItem file_new_torrent_for_tracking = addMenuItem(menuParent, "MainWindow.menu.file.open.torrentfortracking", new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event e) {}
/*      */ 
/*  329 */     });
/*  330 */     return file_new_torrent_for_tracking;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addSearchMenuItem(Menu menuParent) {
/*  334 */     org.eclipse.swt.widgets.MenuItem item = addMenuItem(menuParent, "Button.search", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  337 */         UIFunctionsManagerSWT.getUIFunctionsSWT().promptForSearch();
/*      */       }
/*  339 */     });
/*  340 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addOpenVuzeFileMenuItem(Menu menuParent)
/*      */   {
/*  345 */     addMenuItem(menuParent, "MainWindow.menu.file.open.vuze", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  347 */         Display display = this.val$menuParent.getDisplay();
/*      */         
/*  349 */         display.asyncExec(new AERunnable() {
/*      */           public void runSupport() {
/*  351 */             FileDialog dialog = new FileDialog(MenuFactory.9.this.val$menuParent.getShell(), 135168);
/*      */             
/*      */ 
/*  354 */             dialog.setFilterPath(TorrentOpener.getFilterPathData());
/*      */             
/*  356 */             dialog.setText(MessageText.getString("MainWindow.dialog.select.vuze.file"));
/*      */             
/*  358 */             dialog.setFilterExtensions(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  363 */             dialog.setFilterNames(new String[] { "*.vuze", "*.vuz", Constants.FILE_WILDCARD });
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  369 */             String path = TorrentOpener.setFilterPathData(dialog.open());
/*      */             
/*  371 */             if (path != null)
/*      */             {
/*  373 */               VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*      */               
/*  375 */               if (vfh.loadAndHandleVuzeFile(path, 0) == null)
/*      */               {
/*      */ 
/*  378 */                 TorrentOpener.openTorrent(path);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createShareMenuItem(Menu menuParent) {
/*  388 */     org.eclipse.swt.widgets.MenuItem file_share = createTopLevelMenuItem(menuParent, "MainWindow.menu.file.share");
/*  389 */     return file_share;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addShareFileMenuItem(Menu menuParent)
/*      */   {
/*  394 */     org.eclipse.swt.widgets.MenuItem file_share_file = addMenuItem(menuParent, "MainWindow.menu.file.share.file", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  397 */         ShareUtils.shareFile(this.val$menuParent.getShell());
/*      */       }
/*  399 */     });
/*  400 */     return file_share_file;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addShareFolderMenuItem(Menu menuParent) {
/*  404 */     org.eclipse.swt.widgets.MenuItem file_share_dir = addMenuItem(menuParent, "MainWindow.menu.file.share.dir", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  407 */         ShareUtils.shareDir(this.val$menuParent.getShell());
/*      */       }
/*  409 */     });
/*  410 */     return file_share_dir;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addShareFolderContentMenuItem(Menu menuParent) {
/*  414 */     org.eclipse.swt.widgets.MenuItem file_share_dircontents = addMenuItem(menuParent, "MainWindow.menu.file.share.dircontents", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  417 */         ShareUtils.shareDirContents(this.val$menuParent.getShell(), false);
/*      */       }
/*  419 */     });
/*  420 */     return file_share_dircontents;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addShareFolderContentRecursiveMenuItem(Menu menuParent)
/*      */   {
/*  425 */     org.eclipse.swt.widgets.MenuItem file_share_dircontents_rec = addMenuItem(menuParent, "MainWindow.menu.file.share.dircontentsrecursive", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  428 */         ShareUtils.shareDirContents(this.val$menuParent.getShell(), true);
/*      */       }
/*  430 */     });
/*  431 */     return file_share_dircontents_rec;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addImportMenuItem(Menu menuParent) {
/*  435 */     org.eclipse.swt.widgets.MenuItem file_import = addMenuItem(menuParent, "MainWindow.menu.file.import", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  438 */         new ImportTorrentWizard();
/*      */       }
/*  440 */     });
/*  441 */     return file_import;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addExportMenuItem(Menu menuParent) {
/*  445 */     org.eclipse.swt.widgets.MenuItem file_export = addMenuItem(menuParent, "MainWindow.menu.file.export", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  448 */         new org.gudy.azureus2.ui.swt.exporttorrent.wizard.ExportTorrentWizard();
/*      */       }
/*  450 */     });
/*  451 */     return file_export;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addCloseWindowMenuItem(Menu menuParent)
/*      */   {
/*  456 */     org.eclipse.swt.widgets.MenuItem closeWindow = addMenuItem(menuParent, "MainWindow.menu.file.closewindow", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/*  459 */         Shell shell = this.val$menuParent.getShell();
/*  460 */         if ((shell != null) && (!shell.isDisposed())) {
/*  461 */           this.val$menuParent.getShell().close();
/*      */         }
/*      */       }
/*  464 */     });
/*  465 */     return closeWindow;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addCloseTabMenuItem(Menu menu) {
/*  469 */     org.eclipse.swt.widgets.MenuItem menuItem = addMenuItem(menu, "MainWindow.menu.file.closetab", new Listener() {
/*      */       public void handleEvent(Event event) {
/*  471 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  472 */         if (mdi != null) {
/*  473 */           MdiEntry currentEntry = mdi.getCurrentEntry();
/*  474 */           if ((currentEntry != null) && (currentEntry.isCloseable())) {
/*  475 */             mdi.closeEntry(currentEntry.getId());
/*      */           }
/*      */         }
/*      */       }
/*  479 */     });
/*  480 */     menu.addMenuListener(new MenuListener() {
/*      */       public void menuShown(MenuEvent e) {
/*  482 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  483 */         if (mdi != null) {
/*  484 */           MdiEntry currentEntry = mdi.getCurrentEntry();
/*  485 */           if ((currentEntry != null) && (currentEntry.isCloseable())) {
/*  486 */             this.val$menuItem.setEnabled(true);
/*  487 */             return;
/*      */           }
/*      */         }
/*  490 */         this.val$menuItem.setEnabled(false);
/*      */       }
/*      */       
/*      */ 
/*      */       public void menuHidden(MenuEvent e) {}
/*  495 */     });
/*  496 */     return menuItem;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addCloseDetailsMenuItem(Menu menu) {
/*  500 */     org.eclipse.swt.widgets.MenuItem item = addMenuItem(menu, "MainWindow.menu.closealldetails", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  503 */         UIFunctionsManagerSWT.getUIFunctionsSWT().closeAllDetails();
/*      */       }
/*      */       
/*  506 */     });
/*  507 */     Listener enableHandler = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  509 */         if ((MenuFactory.isEnabledForCurrentMode(this.val$item)) && 
/*  510 */           (!this.val$item.isDisposed()) && (!event.widget.isDisposed())) {
/*  511 */           boolean hasDetails = UIFunctionsManagerSWT.getUIFunctionsSWT().hasDetailViews();
/*  512 */           this.val$item.setEnabled(hasDetails);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  517 */     };
/*  518 */     menu.addListener(22, enableHandler);
/*      */     
/*  520 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addCloseDownloadBarsToMenu(Menu menu) {
/*  524 */     org.eclipse.swt.widgets.MenuItem item = addMenuItem(menu, "MainWindow.menu.closealldownloadbars", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  527 */         MiniBarManager.getManager().closeAll();
/*      */       }
/*      */       
/*  530 */     });
/*  531 */     Listener enableHandler = new Listener() {
/*      */       public void handleEvent(Event event) {
/*  533 */         if (!this.val$item.isDisposed()) {
/*  534 */           this.val$item.setEnabled(!MiniBarManager.getManager().getShellManager().isEmpty());
/*      */         }
/*      */       }
/*  537 */     };
/*  538 */     menu.addListener(22, enableHandler);
/*      */     
/*  540 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addRestartMenuItem(Menu menuParent) {
/*  544 */     org.eclipse.swt.widgets.MenuItem file_restart = new org.eclipse.swt.widgets.MenuItem(menuParent, 0);
/*  545 */     Messages.setLanguageText(file_restart, "MainWindow.menu.file.restart");
/*      */     
/*  547 */     file_restart.addListener(13, new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/*  550 */         UIFunctionsManagerSWT.getUIFunctionsSWT().dispose(true, false);
/*      */       }
/*  552 */     });
/*  553 */     return file_restart;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addExitMenuItem(Menu menuParent) {
/*  557 */     org.eclipse.swt.widgets.MenuItem file_exit = new org.eclipse.swt.widgets.MenuItem(menuParent, 0);
/*  558 */     if ((!COConfigurationManager.getBooleanParameter("Enable System Tray")) || (!COConfigurationManager.getBooleanParameter("Close To Tray")))
/*      */     {
/*  560 */       KeyBindings.setAccelerator(file_exit, "MainWindow.menu.file.exit");
/*      */     }
/*  562 */     Messages.setLanguageText(file_exit, "MainWindow.menu.file.exit");
/*      */     
/*  564 */     file_exit.addListener(13, new Listener() {
/*      */       public void handleEvent(Event e) {
/*  566 */         UIFunctionsManagerSWT.getUIFunctionsSWT().dispose(false, false);
/*      */       }
/*      */       
/*      */ 
/*  570 */     });
/*  571 */     ParameterListener paramListener = new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/*  573 */         if ((COConfigurationManager.getBooleanParameter("Enable System Tray")) && (COConfigurationManager.getBooleanParameter("Close To Tray")))
/*      */         {
/*  575 */           KeyBindings.removeAccelerator(this.val$file_exit, "MainWindow.menu.file.exit");
/*      */         } else {
/*  577 */           KeyBindings.setAccelerator(this.val$file_exit, "MainWindow.menu.file.exit");
/*      */         }
/*      */       }
/*  580 */     };
/*  581 */     COConfigurationManager.addParameterListener("Enable System Tray", paramListener);
/*      */     
/*  583 */     COConfigurationManager.addParameterListener("Close To Tray", paramListener);
/*  584 */     return file_exit;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addStartAllMenuItem(Menu menu) {
/*  588 */     addMenuItem(menu, "MainWindow.menu.transfers.startalltransfers", new ListenerNeedingCoreRunning()
/*      */     {
/*      */       public void handleEvent(AzureusCore core, Event e) {
/*  591 */         core.getGlobalManager().startAllDownloads();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem addStopAllMenuItem(Menu menu)
/*      */   {
/*  602 */     addMenuItem(menu, "MainWindow.menu.transfers.stopalltransfers", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {}
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addPauseMenuItem(Menu menu)
/*      */   {
/*  610 */     addMenuItem(menu, "MainWindow.menu.transfers.pausetransfers", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {}
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addPauseForMenuItem(Menu menu)
/*      */   {
/*  618 */     addMenuItem(menu, "MainWindow.menu.transfers.pausetransfersfor", new ListenerNeedingCoreRunning()
/*      */     {
/*      */       public void handleEvent(AzureusCore core, Event event) {
/*  621 */         String text = MessageText.getString("dialog.pause.for.period.text");
/*      */         
/*  623 */         int rem = core.getGlobalManager().getPauseDownloadPeriodRemaining();
/*      */         
/*  625 */         if (rem > 0)
/*      */         {
/*  627 */           text = text + "\n\n" + MessageText.getString("dialog.pause.for.period.text2", new String[] { TimeFormatter.format2(rem, true) });
/*      */         }
/*      */         
/*  630 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("dialog.pause.for.period.title", "!" + text + "!");
/*      */         
/*      */ 
/*      */ 
/*  634 */         int def = COConfigurationManager.getIntParameter("pause.for.period.default", 10);
/*      */         
/*  636 */         entryWindow.setPreenteredText(String.valueOf(def), false);
/*      */         
/*  638 */         entryWindow.prompt(new UIInputReceiverListener() {
/*      */           public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/*  640 */             if (!entryWindow.hasSubmittedInput()) {
/*  641 */               return;
/*      */             }
/*  643 */             String sReturn = entryWindow.getSubmittedInput();
/*      */             
/*  645 */             if (sReturn == null) {
/*  646 */               return;
/*      */             }
/*  648 */             int mins = -1;
/*      */             try {
/*  650 */               mins = Integer.valueOf(sReturn).intValue();
/*      */             }
/*      */             catch (NumberFormatException er) {}
/*      */             
/*      */ 
/*  655 */             if (mins <= 0) {
/*  656 */               MessageBox mb = new MessageBox(MenuFactory.29.this.val$menu.getShell(), 33);
/*      */               
/*  658 */               mb.setText(MessageText.getString("MyTorrentsView.dialog.NumberError.title"));
/*  659 */               mb.setMessage(MessageText.getString("MyTorrentsView.dialog.NumberError.text"));
/*      */               
/*  661 */               mb.open();
/*  662 */               return;
/*      */             }
/*      */             
/*  665 */             COConfigurationManager.setParameter("pause.for.period.default", mins);
/*      */             
/*  667 */             ManagerUtils.asyncPauseForPeriod(mins * 60);
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*  674 */   public static org.eclipse.swt.widgets.MenuItem addResumeMenuItem(Menu menu) { addMenuItem(menu, "MainWindow.menu.transfers.resumetransfers", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {}
/*      */     }); }
/*      */   
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem addMyTorrentsMenuItem(Menu menu)
/*      */   {
/*  682 */     addMenuItem(menu, "MainWindow.menu.view.mytorrents", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  684 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  685 */         if (uiFunctions != null) {
/*  686 */           uiFunctions.getMDI().showEntryByID("Library");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addAllPeersMenuItem(Menu menu)
/*      */   {
/*  694 */     addMenuItem(menu, "MainWindow.menu.view.allpeers", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  696 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  697 */         if (uiFunctions != null) {
/*  698 */           uiFunctions.getMDI().showEntryByID("AllPeersView");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addClientStatsMenuItem(Menu menu)
/*      */   {
/*  706 */     addMenuItem(menu, "MainWindow.menu.view.clientstats", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  708 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  709 */         if (uiFunctions != null) {
/*  710 */           uiFunctions.getMDI().showEntryByID("ClientStatsView");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addDeviceManagerMenuItem(Menu menu)
/*      */   {
/*  718 */     addMenuItem(menu, "MainWindow.menu.view.devicemanager", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  720 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  721 */         mdi.showEntryByID("Devices");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addSubscriptionMenuItem(Menu menu) {
/*  727 */     addMenuItem(menu, "subscriptions.view.title", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  729 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  730 */         mdi.showEntryByID("Subscriptions");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addMyTrackerMenuItem(Menu menu) {
/*  736 */     addMenuItem(menu, "MainWindow.menu.view.mytracker", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  738 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*  739 */         mdi.showEntryByID("MyTrackerView");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addMySharesMenuItem(Menu menu) {
/*  745 */     addMenuItem(menu, "MainWindow.menu.view.myshares", new Listener() {
/*      */       public void handleEvent(Event e) {
/*  747 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*  748 */         if (uiFunctions != null) {
/*  749 */           uiFunctions.getMDI().showEntryByID("MySharesView");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addViewToolbarMenuItem(Menu menu)
/*      */   {
/*  757 */     org.eclipse.swt.widgets.MenuItem item = addMenuItem(menu, 32, "MainWindow.menu.view.iconbar", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/*  760 */         UIFunctionsSWT uiFunctions = MenuFactory.access$000();
/*  761 */         if (null != uiFunctions) {
/*  762 */           IMainWindow mainWindow = uiFunctions.getMainWindow();
/*  763 */           boolean isToolbarVisible = mainWindow.isVisible(2);
/*  764 */           mainWindow.setVisible(2, !isToolbarVisible);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  769 */     });
/*  770 */     ParameterListener listener = new ParameterListener() {
/*      */       public void parameterChanged(String parameterName) {
/*  772 */         this.val$item.setSelection(COConfigurationManager.getBooleanParameter(parameterName));
/*      */       }
/*      */       
/*  775 */     };
/*  776 */     COConfigurationManager.addAndFireParameterListener("IconBar.enabled", listener);
/*      */     
/*  778 */     item.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/*  780 */         COConfigurationManager.removeParameterListener("IconBar.enabled", this.val$listener);
/*      */       }
/*      */       
/*  783 */     });
/*  784 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addTransferBarToMenu(final Menu menu) {
/*  788 */     org.eclipse.swt.widgets.MenuItem item = addMenuItem(menu, 32, "MainWindow.menu.view.open_global_transfer_bar", new ListenerNeedingCoreRunning()
/*      */     {
/*      */       public void handleEvent(AzureusCore core, Event e) {
/*  791 */         if (AllTransfersBar.getManager().isOpen(core.getGlobalManager()))
/*      */         {
/*  793 */           AllTransfersBar.closeAllTransfersBar();
/*      */         } else {
/*  795 */           AllTransfersBar.open(this.val$menu.getShell());
/*      */         }
/*      */       }
/*  798 */     });
/*  799 */     item.setSelection(!MiniBarManager.getManager().getShellManager().isEmpty());
/*      */     
/*  801 */     menu.addListener(22, new Listener() {
/*      */       public void handleEvent(Event event) {
/*  803 */         if (this.val$item.isDisposed()) {
/*  804 */           menu.removeListener(22, this);
/*      */         } else {
/*  806 */           this.val$item.setSelection(!MiniBarManager.getManager().getShellManager().isEmpty());
/*      */         }
/*      */       }
/*  809 */     });
/*  810 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addSpeedLimitsToMenu(Menu menuParent) {
/*  814 */     org.eclipse.swt.widgets.MenuItem speedLimitsMenuItem = createTopLevelMenuItem(menuParent, "MainWindow.menu.speed_limits");
/*      */     
/*  816 */     MenuBuildUtils.addMaintenanceListenerForMenu(speedLimitsMenuItem.getMenu(), new MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */ 
/*      */       public void buildMenu(Menu menu, MenuEvent menuEvent)
/*      */       {
/*      */ 
/*  822 */         if (AzureusCoreFactory.isCoreRunning())
/*      */         {
/*  824 */           AzureusCore core = AzureusCoreFactory.getSingleton();
/*      */           
/*  826 */           final SpeedLimitHandler slh = SpeedLimitHandler.getSingleton(core);
/*      */           
/*  828 */           org.eclipse.swt.widgets.MenuItem viewCurrentItem = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  829 */           Messages.setLanguageText(viewCurrentItem, "MainWindow.menu.speed_limits.view_current");
/*      */           
/*  831 */           viewCurrentItem.addListener(13, new Listener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(Event arg0)
/*      */             {
/*      */ 
/*      */ 
/*  839 */               MenuFactory.showText("MainWindow.menu.speed_limits.info.title", "MainWindow.menu.speed_limits.info.curr", slh.getCurrent());
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  845 */           });
/*  846 */           List<String> profiles = slh.getProfileNames();
/*      */           
/*  848 */           Menu profiles_menu = new Menu(this.val$menuParent.getShell(), 4);
/*  849 */           org.eclipse.swt.widgets.MenuItem profiles_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*  850 */           profiles_item.setMenu(profiles_menu);
/*      */           
/*      */ 
/*  853 */           Messages.setLanguageText(profiles_item, "MainWindow.menu.speed_limits.profiles");
/*      */           
/*  855 */           if (profiles.size() == 0)
/*      */           {
/*  857 */             profiles_item.setEnabled(false);
/*      */           }
/*      */           else
/*      */           {
/*  861 */             for (final String p : profiles)
/*      */             {
/*  863 */               Menu profile_menu = new Menu(this.val$menuParent.getShell(), 4);
/*  864 */               org.eclipse.swt.widgets.MenuItem profile_item = new org.eclipse.swt.widgets.MenuItem(profiles_menu, 64);
/*  865 */               profile_item.setMenu(profile_menu);
/*  866 */               profile_item.setText(p);
/*      */               
/*  868 */               org.eclipse.swt.widgets.MenuItem loadItem = new org.eclipse.swt.widgets.MenuItem(profile_menu, 8);
/*  869 */               Messages.setLanguageText(loadItem, "MainWindow.menu.speed_limits.load");
/*      */               
/*  871 */               loadItem.addListener(13, new Listener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void handleEvent(Event arg0)
/*      */                 {
/*      */ 
/*      */ 
/*  879 */                   MenuFactory.showText("MainWindow.menu.speed_limits.info.title", MessageText.getString("MainWindow.menu.speed_limits.info.prof", new String[] { p }), slh.loadProfile(p));
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  885 */               });
/*  886 */               org.eclipse.swt.widgets.MenuItem viewItem = new org.eclipse.swt.widgets.MenuItem(profile_menu, 8);
/*  887 */               Messages.setLanguageText(viewItem, "MainWindow.menu.speed_limits.view");
/*      */               
/*  889 */               viewItem.addListener(13, new Listener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void handleEvent(Event arg0)
/*      */                 {
/*      */ 
/*      */ 
/*  897 */                   MenuFactory.showText("MainWindow.menu.speed_limits.info.title", MessageText.getString("MainWindow.menu.speed_limits.info.prof", new String[] { p }), slh.getProfile(p));
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  903 */               });
/*  904 */               MenuFactory.addSeparatorMenuItem(profile_menu);
/*      */               
/*  906 */               org.eclipse.swt.widgets.MenuItem deleteItem = new org.eclipse.swt.widgets.MenuItem(profile_menu, 8);
/*  907 */               Messages.setLanguageText(deleteItem, "MainWindow.menu.speed_limits.delete");
/*      */               
/*  909 */               deleteItem.addListener(13, new Listener()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void handleEvent(Event arg0)
/*      */                 {
/*      */ 
/*      */ 
/*  917 */                   slh.deleteProfile(p);
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */           
/*  923 */           org.eclipse.swt.widgets.MenuItem saveItem = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  924 */           Messages.setLanguageText(saveItem, "MainWindow.menu.speed_limits.save_current");
/*      */           
/*  926 */           saveItem.addListener(13, new Listener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(Event arg0)
/*      */             {
/*      */ 
/*      */ 
/*  934 */               UISWTInputReceiver entry = new SimpleTextEntryWindow();
/*      */               
/*  936 */               entry.allowEmptyInput(false);
/*  937 */               entry.setLocalisedTitle(MessageText.getString("MainWindow.menu.speed_limits.profile"));
/*  938 */               entry.prompt(new UIInputReceiverListener() {
/*      */                 public void UIInputReceiverClosed(UIInputReceiver entry) {
/*  940 */                   if (!entry.hasSubmittedInput())
/*      */                   {
/*  942 */                     return;
/*      */                   }
/*      */                   
/*  945 */                   String input = entry.getSubmittedInput().trim();
/*      */                   
/*  947 */                   if (input.length() > 0)
/*      */                   {
/*  949 */                     MenuFactory.showText("MainWindow.menu.speed_limits.info.title", MessageText.getString("MainWindow.menu.speed_limits.info.prof", new String[] { input }), MenuFactory.43.5.this.val$slh.saveProfile(input));
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */ 
/*      */               });
/*      */             }
/*      */             
/*  958 */           });
/*  959 */           MenuFactory.addSeparatorMenuItem(menu);
/*      */           
/*  961 */           org.eclipse.swt.widgets.MenuItem resetItem = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  962 */           Messages.setLanguageText(resetItem, "MainWindow.menu.speed_limits.reset");
/*      */           
/*  964 */           resetItem.addListener(13, new Listener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(Event arg0)
/*      */             {
/*      */ 
/*      */ 
/*  972 */               MenuFactory.showText("MainWindow.menu.speed_limits.info.title", "MainWindow.menu.speed_limits.info.curr", slh.reset());
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  978 */           });
/*  979 */           MenuFactory.addSeparatorMenuItem(menu);
/*      */           
/*  981 */           org.eclipse.swt.widgets.MenuItem scheduleItem = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*  982 */           Messages.setLanguageText(scheduleItem, "MainWindow.menu.speed_limits.schedule");
/*      */           
/*  984 */           scheduleItem.addListener(13, new Listener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(Event arg0)
/*      */             {
/*      */ 
/*      */ 
/*  992 */               Utils.execSWTThreadLater(1, new Runnable()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/*  999 */                   List<String> lines = MenuFactory.43.7.this.val$slh.getSchedule();
/*      */                   
/* 1001 */                   StringBuilder text = new StringBuilder(80 * lines.size());
/*      */                   
/* 1003 */                   for (String s : lines)
/*      */                   {
/* 1005 */                     if (text.length() > 0)
/*      */                     {
/* 1007 */                       text.append("\n");
/*      */                     }
/*      */                     
/* 1010 */                     text.append(s);
/*      */                   }
/*      */                   
/* 1013 */                   final TextViewerWindow viewer = new TextViewerWindow("MainWindow.menu.speed_limits.schedule.title", "MainWindow.menu.speed_limits.schedule.msg", text.toString(), false);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1019 */                   viewer.setEditable(true);
/*      */                   
/* 1021 */                   viewer.addListener(new TextViewerWindow.TextViewerWindowListener()
/*      */                   {
/*      */ 
/*      */                     public void closed()
/*      */                     {
/*      */ 
/* 1027 */                       String text = viewer.getText();
/*      */                       
/* 1029 */                       String[] lines = text.split("\n");
/*      */                       
/* 1031 */                       List<String> updated_lines = new java.util.ArrayList(Arrays.asList(lines));
/*      */                       
/* 1033 */                       List<String> result = MenuFactory.43.7.this.val$slh.setSchedule(updated_lines);
/*      */                       
/* 1035 */                       if ((result != null) && (result.size() > 0))
/*      */                       {
/* 1037 */                         MenuFactory.showText("MainWindow.menu.speed_limits.schedule.title", "MainWindow.menu.speed_limits.schedule.err", result);
/*      */                       }
/*      */                       
/*      */                     }
/*      */                     
/*      */ 
/*      */                   });
/*      */                 }
/*      */                 
/*      */               });
/*      */             }
/* 1048 */           });
/* 1049 */           MenuFactory.addSeparatorMenuItem(menu);
/*      */           
/* 1051 */           org.eclipse.swt.widgets.MenuItem helpItem = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/* 1052 */           Messages.setLanguageText(helpItem, "MainWindow.menu.speed_limits.wiki");
/*      */           
/* 1054 */           helpItem.addListener(13, new Listener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(Event arg0)
/*      */             {
/*      */ 
/*      */ 
/* 1062 */               Utils.launch(MessageText.getString("MainWindow.menu.speed_limits.wiki.url"));
/*      */             }
/*      */             
/*      */           });
/*      */         }
/*      */       }
/* 1068 */     });
/* 1069 */     return speedLimitsMenuItem;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addAdvancedHelpMenuItem(Menu menuParent) {
/* 1073 */     org.eclipse.swt.widgets.MenuItem advancedHelpMenuItem = createTopLevelMenuItem(menuParent, "MainWindow.menu.advanced_tools");
/*      */     
/* 1075 */     MenuBuildUtils.addMaintenanceListenerForMenu(advancedHelpMenuItem.getMenu(), new MenuBuildUtils.MenuBuilder()
/*      */     {
/*      */ 
/*      */       public void buildMenu(Menu menu, MenuEvent menuEvent)
/*      */       {
/*      */ 
/* 1081 */         org.eclipse.swt.widgets.MenuItem viewTorrent = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1083 */         Messages.setLanguageText(viewTorrent, "torrent.view.info");
/*      */         
/* 1085 */         viewTorrent.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg)
/*      */           {
/*      */ 
/*      */ 
/* 1093 */             Utils.execSWTThreadLater(1, new Runnable()
/*      */             {
/*      */ 
/*      */               public void run() {}
/*      */ 
/*      */ 
/*      */             });
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1105 */         });
/* 1106 */         org.eclipse.swt.widgets.MenuItem fixTorrent = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1108 */         Messages.setLanguageText(fixTorrent, "torrent.fix.corrupt");
/*      */         
/* 1110 */         fixTorrent.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg)
/*      */           {
/*      */ 
/*      */ 
/* 1118 */             Utils.execSWTThreadLater(1, new Runnable()
/*      */             {
/*      */ 
/*      */               public void run() {}
/*      */ 
/*      */ 
/*      */             });
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1130 */         });
/* 1131 */         org.eclipse.swt.widgets.MenuItem importXMLTorrent = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1133 */         Messages.setLanguageText(importXMLTorrent, "importTorrentWizard.title");
/*      */         
/* 1135 */         importXMLTorrent.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg)
/*      */           {
/*      */ 
/*      */ 
/* 1143 */             Utils.execSWTThreadLater(1, new Runnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/* 1150 */                 new ImportTorrentWizard();
/*      */               }
/*      */               
/*      */             });
/*      */           }
/* 1155 */         });
/* 1156 */         org.eclipse.swt.widgets.MenuItem bencodeToJSON = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1158 */         Messages.setLanguageText(bencodeToJSON, "menu.bencode.to.json");
/*      */         
/* 1160 */         bencodeToJSON.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg)
/*      */           {
/*      */ 
/*      */ 
/* 1168 */             Utils.execSWTThreadLater(1, new Runnable()
/*      */             {
/*      */ 
/*      */               public void run() {}
/*      */ 
/*      */ 
/*      */             });
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1180 */         });
/* 1181 */         org.eclipse.swt.widgets.MenuItem JSONToBencode = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1183 */         Messages.setLanguageText(JSONToBencode, "menu.json.to.bencode");
/*      */         
/* 1185 */         JSONToBencode.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg)
/*      */           {
/*      */ 
/*      */ 
/* 1193 */             Utils.execSWTThreadLater(1, new Runnable()
/*      */             {
/*      */ 
/*      */               public void run() {}
/*      */ 
/*      */ 
/*      */             });
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1205 */         });
/* 1206 */         org.eclipse.swt.widgets.MenuItem showChanges = new org.eclipse.swt.widgets.MenuItem(menu, 8);
/*      */         
/* 1208 */         Messages.setLanguageText(showChanges, "show.config.changes");
/*      */         
/* 1210 */         showChanges.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event arg)
/*      */           {
/*      */ 
/*      */ 
/* 1218 */             Utils.execSWTThreadLater(1, new Runnable()
/*      */             {
/*      */ 
/*      */               public void run() {}
/*      */ 
/*      */ 
/*      */             });
/*      */           }
/*      */           
/*      */ 
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 1232 */     });
/* 1233 */     return advancedHelpMenuItem;
/*      */   }
/*      */   
/*      */ 
/*      */   private static void BencodeToJSON()
/*      */   {
/* 1239 */     Shell shell = Utils.findAnyShell();
/*      */     
/* 1241 */     FileDialog dialog = new FileDialog(shell.getShell(), 135168);
/*      */     
/* 1243 */     dialog.setFilterExtensions(new String[] { "*.config", "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */     
/* 1245 */     dialog.setFilterNames(new String[] { "*.config", "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */     
/* 1247 */     dialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */     
/* 1249 */     dialog.setText(MessageText.getString("bencode.file.browse"));
/*      */     
/* 1251 */     String str = dialog.open();
/*      */     
/* 1253 */     if (str != null) {
/*      */       try
/*      */       {
/* 1256 */         BufferedInputStream bis = new BufferedInputStream(new java.io.FileInputStream(str));
/*      */         try
/*      */         {
/* 1259 */           Map map = BDecoder.decode(bis);
/*      */           
/* 1261 */           if (map == null)
/*      */           {
/* 1263 */             throw new Exception("BDecode failed");
/*      */           }
/*      */           
/* 1266 */           final String json = BEncoder.encodeToJSON(map);
/*      */           
/* 1268 */           Utils.execSWTThreadLater(1, new Runnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 1275 */               FileDialog dialog2 = new FileDialog(this.val$shell, 139264);
/*      */               
/* 1277 */               dialog2.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */               
/* 1279 */               dialog2.setFilterExtensions(new String[] { "*.json" });
/*      */               
/* 1281 */               String str2 = dialog2.open();
/*      */               
/* 1283 */               if (str2 != null)
/*      */               {
/* 1285 */                 if (!str2.toLowerCase(Locale.US).endsWith(".json"))
/*      */                 {
/* 1287 */                   str2 = str2 + ".json";
/*      */                 }
/*      */                 try
/*      */                 {
/* 1291 */                   if (!FileUtil.writeStringAsFile(new File(str2), json))
/*      */                   {
/* 1293 */                     throw new Exception("Failed to write output file");
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/* 1297 */                   MessageBoxShell mb = new MessageBoxShell(1, MessageText.getString("ConfigView.section.security.resetkey.error.title"), Debug.getNestedExceptionMessage(e));
/*      */                   
/* 1299 */                   mb.setParent(this.val$shell);
/*      */                   
/* 1301 */                   mb.open(null);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         finally {
/* 1308 */           bis.close();
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1312 */         MessageBoxShell mb = new MessageBoxShell(1, MessageText.getString("ConfigView.section.security.resetkey.error.title"), Debug.getNestedExceptionMessage(e));
/*      */         
/* 1314 */         mb.setParent(shell);
/*      */         
/* 1316 */         mb.open(null);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static void JSONToBencode()
/*      */   {
/* 1324 */     Shell shell = Utils.findAnyShell();
/*      */     
/* 1326 */     FileDialog dialog = new FileDialog(shell.getShell(), 135168);
/*      */     
/* 1328 */     dialog.setFilterExtensions(new String[] { "*.json", Constants.FILE_WILDCARD });
/*      */     
/* 1330 */     dialog.setFilterNames(new String[] { "*.json", Constants.FILE_WILDCARD });
/*      */     
/* 1332 */     dialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */     
/* 1334 */     dialog.setText(MessageText.getString("json.file.browse"));
/*      */     
/* 1336 */     String str = dialog.open();
/*      */     
/* 1338 */     if (str != null) {
/*      */       try
/*      */       {
/* 1341 */         String json = FileUtil.readFileAsString(new File(str), -1, "UTF-8");
/*      */         
/* 1343 */         if (json == null)
/*      */         {
/* 1345 */           throw new Exception("JSON decode failed");
/*      */         }
/*      */         
/* 1348 */         final Map map = BDecoder.decodeFromJSON(json);
/*      */         
/* 1350 */         Utils.execSWTThreadLater(1, new Runnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void run()
/*      */           {
/*      */ 
/* 1357 */             FileDialog dialog2 = new FileDialog(this.val$shell, 139264);
/*      */             
/* 1359 */             dialog2.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */             
/* 1361 */             dialog2.setFilterExtensions(new String[] { "*.config", "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */             
/* 1363 */             String str2 = dialog2.open();
/*      */             
/* 1365 */             if (str2 != null)
/*      */             {
/* 1367 */               if (!str2.contains("."))
/*      */               {
/* 1369 */                 str2 = str2 + ".config";
/*      */               }
/*      */               try
/*      */               {
/* 1373 */                 byte[] bytes = BEncoder.encode(map);
/*      */                 
/* 1375 */                 FileUtil.writeBytesAsFile(str2, bytes);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1379 */                 MessageBoxShell mb = new MessageBoxShell(1, MessageText.getString("ConfigView.section.security.resetkey.error.title"), Debug.getNestedExceptionMessage(e));
/*      */                 
/* 1381 */                 mb.setParent(this.val$shell);
/*      */                 
/* 1383 */                 mb.open(null);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1391 */         MessageBoxShell mb = new MessageBoxShell(1, MessageText.getString("ConfigView.section.security.resetkey.error.title"), Debug.getNestedExceptionMessage(e));
/*      */         
/* 1393 */         mb.setParent(shell);
/*      */         
/* 1395 */         mb.open(null);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static void handleTorrentView()
/*      */   {
/* 1404 */     Shell shell = Utils.findAnyShell();
/*      */     try
/*      */     {
/* 1407 */       FileDialog dialog = new FileDialog(shell.getShell(), 135168);
/*      */       
/* 1409 */       dialog.setFilterExtensions(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */       
/* 1411 */       dialog.setFilterNames(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */       
/* 1413 */       dialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */       
/* 1415 */       dialog.setText(MessageText.getString("torrent.fix.corrupt.browse"));
/*      */       
/* 1417 */       String str = dialog.open();
/*      */       
/* 1419 */       if (str != null)
/*      */       {
/* 1421 */         TorrentOpener.setFilterPathTorrent(str);
/*      */         
/* 1423 */         File file = new File(str);
/*      */         
/* 1425 */         StringBuilder content = new StringBuilder();
/*      */         
/* 1427 */         String NL = "\r\n";
/*      */         try
/*      */         {
/* 1430 */           TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedFile(file);
/*      */           
/* 1432 */           LocaleUtilDecoder locale_decoder = LocaleTorrentUtil.getTorrentEncoding(torrent);
/*      */           
/* 1434 */           content.append("Character Encoding:\t").append(locale_decoder.getName()).append(NL);
/*      */           
/* 1436 */           String display_name = locale_decoder.decodeString(torrent.getName());
/*      */           
/* 1438 */           content.append("Name:\t").append(display_name).append(NL);
/*      */           
/* 1440 */           byte[] hash = torrent.getHash();
/*      */           
/* 1442 */           content.append("Hash:\t").append(ByteFormatter.encodeString(hash)).append(NL);
/*      */           
/* 1444 */           content.append("Size:\t").append(DisplayFormatters.formatByteCountToKiBEtc(torrent.getSize())).append(", piece size=").append(DisplayFormatters.formatByteCountToKiBEtc(torrent.getPieceLength())).append(", piece count=").append(torrent.getPieces().length).append(NL);
/*      */           
/*      */ 
/*      */ 
/* 1448 */           if (torrent.getPrivate())
/*      */           {
/* 1450 */             content.append("Private Torrent").append(NL);
/*      */           }
/*      */           
/* 1453 */           URL announce_url = torrent.getAnnounceURL();
/*      */           
/* 1455 */           if (announce_url != null)
/*      */           {
/* 1457 */             content.append("Announce URL:\t").append(announce_url).append(NL);
/*      */           }
/*      */           
/* 1460 */           TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*      */           
/* 1462 */           if (sets.length > 0)
/*      */           {
/* 1464 */             content.append("Announce List").append(NL);
/*      */             
/* 1466 */             for (TOTorrentAnnounceURLSet set : sets)
/*      */             {
/* 1468 */               String x = "";
/*      */               
/* 1470 */               URL[] urls = set.getAnnounceURLs();
/*      */               
/* 1472 */               for (URL u : urls)
/*      */               {
/* 1474 */                 x = x + (x.length() == 0 ? "" : ", ") + u;
/*      */               }
/*      */               
/* 1477 */               content.append("\t").append(x).append(NL);
/*      */             }
/*      */           }
/*      */           
/* 1481 */           content.append("Magnet URI:\t").append(UrlUtils.getMagnetURI(display_name, org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils.wrap(torrent))).append(NL);
/*      */           
/* 1483 */           long c_date = torrent.getCreationDate();
/*      */           
/* 1485 */           if (c_date > 0L)
/*      */           {
/* 1487 */             content.append("Created On:\t").append(DisplayFormatters.formatDate(c_date * 1000L)).append(NL);
/*      */           }
/*      */           
/* 1490 */           byte[] created_by = torrent.getCreatedBy();
/*      */           
/* 1492 */           if (created_by != null)
/*      */           {
/* 1494 */             content.append("Created By:\t").append(locale_decoder.decodeString(created_by)).append(NL);
/*      */           }
/*      */           
/* 1497 */           byte[] comment = torrent.getComment();
/*      */           
/* 1499 */           if (comment != null)
/*      */           {
/* 1501 */             content.append("Comment:\t").append(locale_decoder.decodeString(comment)).append(NL);
/*      */           }
/*      */           
/* 1504 */           TOTorrentFile[] files = torrent.getFiles();
/*      */           
/* 1506 */           content.append("Files:\t").append(files.length).append(" - simple=").append(torrent.isSimpleTorrent()).append(NL);
/*      */           
/* 1508 */           for (TOTorrentFile tf : files)
/*      */           {
/* 1510 */             byte[][] comps = tf.getPathComponents();
/*      */             
/* 1512 */             String f_name = "";
/*      */             
/* 1514 */             for (byte[] comp : comps)
/*      */             {
/* 1516 */               f_name = f_name + (f_name.length() == 0 ? "" : File.separator) + locale_decoder.decodeString(comp);
/*      */             }
/*      */             
/* 1519 */             content.append("\t").append(f_name).append("\t\t").append(DisplayFormatters.formatByteCountToKiBEtc(tf.getLength())).append(NL);
/*      */           }
/*      */           
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1525 */           content.append(Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */         
/* 1528 */         new TextViewerWindow(MessageText.getString("MainWindow.menu.quick_view") + ": " + file.getName(), null, content.toString(), false);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1535 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void handleTorrentFixup()
/*      */   {
/* 1544 */     final Shell shell = Utils.findAnyShell();
/*      */     try
/*      */     {
/* 1547 */       FileDialog dialog = new FileDialog(shell.getShell(), 135168);
/*      */       
/* 1549 */       dialog.setFilterExtensions(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */       
/* 1551 */       dialog.setFilterNames(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*      */       
/* 1553 */       dialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */       
/* 1555 */       dialog.setText(MessageText.getString("torrent.fix.corrupt.browse"));
/*      */       
/* 1557 */       String str = dialog.open();
/*      */       
/* 1559 */       if (str != null)
/*      */       {
/* 1561 */         TorrentOpener.setFilterPathTorrent(str);
/*      */         
/* 1563 */         File file = new File(str);
/*      */         
/* 1565 */         byte[] bytes = FileUtil.readFileAsByteArray(file);
/*      */         
/* 1567 */         Map existing_map = BDecoder.decode(bytes);
/*      */         
/* 1569 */         Map existing_info = (Map)existing_map.get("info");
/*      */         
/* 1571 */         byte[] existing_info_encoded = BEncoder.encode(existing_info);
/*      */         
/* 1573 */         final TOTorrent t = TOTorrentFactory.deserialiseFromMap(existing_map);
/*      */         
/* 1575 */         byte[] old_hash = t.getHash();
/* 1576 */         byte[] new_hash = null;
/*      */         
/* 1578 */         for (int i = 0; i < bytes.length - 5; i++)
/*      */         {
/* 1580 */           if ((bytes[i] == 58) && (bytes[(i + 1)] == 105) && (bytes[(i + 2)] == 110) && (bytes[(i + 3)] == 102) && (bytes[(i + 4)] == 111))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1586 */             new_hash = new SHA1Simple().calculateHash(bytes, i + 5, existing_info_encoded.length);
/*      */             
/* 1588 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1592 */         if (new_hash != null)
/*      */         {
/* 1594 */           final byte[] f_new_hash = new_hash;
/*      */           
/* 1596 */           Utils.execSWTThreadLater(1, new Runnable()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 1603 */               String title = MessageText.getString("torrent.fix.corrupt.result.title");
/*      */               
/* 1605 */               if (Arrays.equals(this.val$old_hash, f_new_hash))
/*      */               {
/* 1607 */                 MessageBoxShell mb = new MessageBoxShell(32, title, MessageText.getString("torrent.fix.corrupt.result.nothing"));
/*      */                 
/*      */ 
/* 1610 */                 mb.setParent(shell);
/*      */                 
/* 1612 */                 mb.open(null);
/*      */               }
/*      */               else
/*      */               {
/* 1616 */                 MessageBoxShell mb = new MessageBoxShell(32, title, MessageText.getString("torrent.fix.corrupt.result.fixed", new String[] { ByteFormatter.encodeString(f_new_hash) }));
/*      */                 
/*      */ 
/* 1619 */                 mb.setParent(shell);
/*      */                 
/* 1621 */                 mb.open(null);
/*      */                 
/* 1623 */                 mb.waitUntilClosed();
/*      */                 try
/*      */                 {
/* 1626 */                   t.setHashOverride(f_new_hash);
/*      */                   
/* 1628 */                   Utils.execSWTThreadLater(1, new Runnable()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void run()
/*      */                     {
/*      */ 
/* 1635 */                       FileDialog dialog2 = new FileDialog(MenuFactory.47.this.val$shell, 139264);
/*      */                       
/* 1637 */                       dialog2.setFilterPath(TorrentOpener.getFilterPathTorrent());
/*      */                       
/* 1639 */                       dialog2.setFilterExtensions(new String[] { "*.torrent" });
/*      */                       
/* 1641 */                       String str2 = dialog2.open();
/*      */                       
/* 1643 */                       if (str2 != null)
/*      */                       {
/* 1645 */                         if ((!str2.toLowerCase(Locale.US).endsWith(".tor")) && (!str2.toLowerCase(Locale.US).endsWith(".torrent")))
/*      */                         {
/* 1647 */                           str2 = str2 + ".torrent";
/*      */                         }
/*      */                         try
/*      */                         {
/* 1651 */                           MenuFactory.47.this.val$t.serialiseToBEncodedFile(new File(str2));
/*      */                         }
/*      */                         catch (Throwable e)
/*      */                         {
/* 1655 */                           Debug.out(e);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/*      */                 catch (Throwable e) {
/* 1662 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1671 */       Debug.out(e);
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   private static void handleShowChanges()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: ldc 2
/*      */     //   2: astore_0
/*      */     //   3: new 879	java/io/StringWriter
/*      */     //   6: dup
/*      */     //   7: invokespecial 1411	java/io/StringWriter:<init>	()V
/*      */     //   10: astore_1
/*      */     //   11: aload_1
/*      */     //   12: ldc_w 778
/*      */     //   15: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   18: pop
/*      */     //   19: aload_1
/*      */     //   20: ldc_w 837
/*      */     //   23: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   26: pop
/*      */     //   27: new 922	org/gudy/azureus2/core3/util/IndentWriter
/*      */     //   30: dup
/*      */     //   31: new 878	java/io/PrintWriter
/*      */     //   34: dup
/*      */     //   35: aload_1
/*      */     //   36: invokespecial 1410	java/io/PrintWriter:<init>	(Ljava/io/Writer;)V
/*      */     //   39: invokespecial 1496	org/gudy/azureus2/core3/util/IndentWriter:<init>	(Ljava/io/PrintWriter;)V
/*      */     //   42: astore_2
/*      */     //   43: aload_2
/*      */     //   44: invokevirtual 1495	org/gudy/azureus2/core3/util/IndentWriter:indent	()V
/*      */     //   47: aload_2
/*      */     //   48: invokestatic 1469	org/gudy/azureus2/core3/config/COConfigurationManager:dumpConfigChanges	(Lorg/gudy/azureus2/core3/util/IndentWriter;)V
/*      */     //   51: aload_2
/*      */     //   52: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   55: aload_2
/*      */     //   56: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   59: goto +14 -> 73
/*      */     //   62: astore_3
/*      */     //   63: aload_2
/*      */     //   64: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   67: aload_2
/*      */     //   68: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   71: aload_3
/*      */     //   72: athrow
/*      */     //   73: invokestatic 1401	com/aelitis/azureus/core/AzureusCoreFactory:getSingleton	()Lcom/aelitis/azureus/core/AzureusCore;
/*      */     //   76: astore_3
/*      */     //   77: aload_1
/*      */     //   78: ldc_w 833
/*      */     //   81: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   84: pop
/*      */     //   85: aload_3
/*      */     //   86: invokeinterface 1645 1 0
/*      */     //   91: invokevirtual 1501	org/gudy/azureus2/plugins/PluginManager:getPlugins	()[Lorg/gudy/azureus2/plugins/PluginInterface;
/*      */     //   94: astore 4
/*      */     //   96: aload 4
/*      */     //   98: astore 5
/*      */     //   100: aload 5
/*      */     //   102: arraylength
/*      */     //   103: istore 6
/*      */     //   105: iconst_0
/*      */     //   106: istore 7
/*      */     //   108: iload 7
/*      */     //   110: iload 6
/*      */     //   112: if_icmpge +73 -> 185
/*      */     //   115: aload 5
/*      */     //   117: iload 7
/*      */     //   119: aaload
/*      */     //   120: astore 8
/*      */     //   122: aload 8
/*      */     //   124: invokeinterface 1682 1 0
/*      */     //   129: invokeinterface 1683 1 0
/*      */     //   134: ifeq +6 -> 140
/*      */     //   137: goto +42 -> 179
/*      */     //   140: aload_1
/*      */     //   141: ldc_w 776
/*      */     //   144: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   147: aload 8
/*      */     //   149: invokeinterface 1680 1 0
/*      */     //   154: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   157: ldc_w 783
/*      */     //   160: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   163: aload 8
/*      */     //   165: invokeinterface 1681 1 0
/*      */     //   170: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   173: ldc 2
/*      */     //   175: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   178: pop
/*      */     //   179: iinc 7 1
/*      */     //   182: goto -74 -> 108
/*      */     //   185: aload_3
/*      */     //   186: invokeinterface 1644 1 0
/*      */     //   191: invokeinterface 1660 1 0
/*      */     //   196: astore 5
/*      */     //   198: aload_1
/*      */     //   199: ldc_w 795
/*      */     //   202: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   205: aload 5
/*      */     //   207: invokeinterface 1648 1 0
/*      */     //   212: invokestatic 1423	java/lang/String:valueOf	(I)Ljava/lang/String;
/*      */     //   215: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   218: ldc 2
/*      */     //   220: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   223: pop
/*      */     //   224: new 922	org/gudy/azureus2/core3/util/IndentWriter
/*      */     //   227: dup
/*      */     //   228: new 878	java/io/PrintWriter
/*      */     //   231: dup
/*      */     //   232: aload_1
/*      */     //   233: invokespecial 1410	java/io/PrintWriter:<init>	(Ljava/io/Writer;)V
/*      */     //   236: invokespecial 1496	org/gudy/azureus2/core3/util/IndentWriter:<init>	(Ljava/io/PrintWriter;)V
/*      */     //   239: astore_2
/*      */     //   240: aload_2
/*      */     //   241: invokevirtual 1495	org/gudy/azureus2/core3/util/IndentWriter:indent	()V
/*      */     //   244: aload 5
/*      */     //   246: invokeinterface 1649 1 0
/*      */     //   251: astore 6
/*      */     //   253: aload 6
/*      */     //   255: invokeinterface 1646 1 0
/*      */     //   260: ifeq +121 -> 381
/*      */     //   263: aload 6
/*      */     //   265: invokeinterface 1647 1 0
/*      */     //   270: checkcast 903	org/gudy/azureus2/core3/download/DownloadManager
/*      */     //   273: astore 7
/*      */     //   275: aload 7
/*      */     //   277: invokeinterface 1657 1 0
/*      */     //   282: invokeinterface 1670 1 0
/*      */     //   287: astore 9
/*      */     //   289: aload 9
/*      */     //   291: invokestatic 1484	org/gudy/azureus2/core3/util/Base32:encode	([B)Ljava/lang/String;
/*      */     //   294: iconst_0
/*      */     //   295: bipush 16
/*      */     //   297: invokevirtual 1424	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   300: astore 8
/*      */     //   302: goto +10 -> 312
/*      */     //   305: astore 9
/*      */     //   307: ldc_w 784
/*      */     //   310: astore 8
/*      */     //   312: aload_2
/*      */     //   313: new 886	java/lang/StringBuilder
/*      */     //   316: dup
/*      */     //   317: invokespecial 1426	java/lang/StringBuilder:<init>	()V
/*      */     //   320: aload 8
/*      */     //   322: invokevirtual 1431	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   325: ldc_w 783
/*      */     //   328: invokevirtual 1431	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   331: aload 7
/*      */     //   333: invokestatic 1490	org/gudy/azureus2/core3/util/DisplayFormatters:formatDownloadStatus	(Lorg/gudy/azureus2/core3/download/DownloadManager;)Ljava/lang/String;
/*      */     //   336: invokevirtual 1431	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   339: invokevirtual 1427	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   342: invokevirtual 1497	org/gudy/azureus2/core3/util/IndentWriter:println	(Ljava/lang/String;)V
/*      */     //   345: aload_2
/*      */     //   346: invokevirtual 1495	org/gudy/azureus2/core3/util/IndentWriter:indent	()V
/*      */     //   349: aload 7
/*      */     //   351: invokeinterface 1656 1 0
/*      */     //   356: aload_2
/*      */     //   357: invokeinterface 1658 2 0
/*      */     //   362: aload_2
/*      */     //   363: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   366: goto +12 -> 378
/*      */     //   369: astore 10
/*      */     //   371: aload_2
/*      */     //   372: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   375: aload 10
/*      */     //   377: athrow
/*      */     //   378: goto -125 -> 253
/*      */     //   381: aload_2
/*      */     //   382: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   385: aload_2
/*      */     //   386: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   389: goto +16 -> 405
/*      */     //   392: astore 11
/*      */     //   394: aload_2
/*      */     //   395: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   398: aload_2
/*      */     //   399: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   402: aload 11
/*      */     //   404: athrow
/*      */     //   405: aload_1
/*      */     //   406: ldc_w 788
/*      */     //   409: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   412: pop
/*      */     //   413: invokestatic 1467	org/gudy/azureus2/core3/category/CategoryManager:getCategories	()[Lorg/gudy/azureus2/core3/category/Category;
/*      */     //   416: astore 6
/*      */     //   418: new 922	org/gudy/azureus2/core3/util/IndentWriter
/*      */     //   421: dup
/*      */     //   422: new 878	java/io/PrintWriter
/*      */     //   425: dup
/*      */     //   426: aload_1
/*      */     //   427: invokespecial 1410	java/io/PrintWriter:<init>	(Ljava/io/Writer;)V
/*      */     //   430: invokespecial 1496	org/gudy/azureus2/core3/util/IndentWriter:<init>	(Ljava/io/PrintWriter;)V
/*      */     //   433: astore_2
/*      */     //   434: aload_2
/*      */     //   435: invokevirtual 1495	org/gudy/azureus2/core3/util/IndentWriter:indent	()V
/*      */     //   438: aload 6
/*      */     //   440: astore 7
/*      */     //   442: aload 7
/*      */     //   444: arraylength
/*      */     //   445: istore 8
/*      */     //   447: iconst_0
/*      */     //   448: istore 9
/*      */     //   450: iload 9
/*      */     //   452: iload 8
/*      */     //   454: if_icmpge +55 -> 509
/*      */     //   457: aload 7
/*      */     //   459: iload 9
/*      */     //   461: aaload
/*      */     //   462: astore 10
/*      */     //   464: aload_2
/*      */     //   465: aload 10
/*      */     //   467: invokeinterface 1652 1 0
/*      */     //   472: invokevirtual 1497	org/gudy/azureus2/core3/util/IndentWriter:println	(Ljava/lang/String;)V
/*      */     //   475: aload_2
/*      */     //   476: invokevirtual 1495	org/gudy/azureus2/core3/util/IndentWriter:indent	()V
/*      */     //   479: aload 10
/*      */     //   481: aload_2
/*      */     //   482: invokeinterface 1653 2 0
/*      */     //   487: aload_2
/*      */     //   488: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   491: goto +12 -> 503
/*      */     //   494: astore 12
/*      */     //   496: aload_2
/*      */     //   497: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   500: aload 12
/*      */     //   502: athrow
/*      */     //   503: iinc 9 1
/*      */     //   506: goto -56 -> 450
/*      */     //   509: aload_2
/*      */     //   510: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   513: aload_2
/*      */     //   514: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   517: goto +16 -> 533
/*      */     //   520: astore 13
/*      */     //   522: aload_2
/*      */     //   523: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   526: aload_2
/*      */     //   527: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   530: aload 13
/*      */     //   532: athrow
/*      */     //   533: aload_1
/*      */     //   534: ldc_w 839
/*      */     //   537: invokevirtual 1413	java/io/StringWriter:append	(Ljava/lang/CharSequence;)Ljava/io/StringWriter;
/*      */     //   540: pop
/*      */     //   541: new 922	org/gudy/azureus2/core3/util/IndentWriter
/*      */     //   544: dup
/*      */     //   545: new 878	java/io/PrintWriter
/*      */     //   548: dup
/*      */     //   549: aload_1
/*      */     //   550: invokespecial 1410	java/io/PrintWriter:<init>	(Ljava/io/Writer;)V
/*      */     //   553: invokespecial 1496	org/gudy/azureus2/core3/util/IndentWriter:<init>	(Ljava/io/PrintWriter;)V
/*      */     //   556: astore_2
/*      */     //   557: aload_2
/*      */     //   558: invokevirtual 1495	org/gudy/azureus2/core3/util/IndentWriter:indent	()V
/*      */     //   561: aload_3
/*      */     //   562: invokestatic 1403	com/aelitis/azureus/core/speedmanager/SpeedLimitHandler:getSingleton	(Lcom/aelitis/azureus/core/AzureusCore;)Lcom/aelitis/azureus/core/speedmanager/SpeedLimitHandler;
/*      */     //   565: aload_2
/*      */     //   566: invokevirtual 1402	com/aelitis/azureus/core/speedmanager/SpeedLimitHandler:dump	(Lorg/gudy/azureus2/core3/util/IndentWriter;)V
/*      */     //   569: aload_2
/*      */     //   570: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   573: aload_2
/*      */     //   574: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   577: goto +16 -> 593
/*      */     //   580: astore 14
/*      */     //   582: aload_2
/*      */     //   583: invokevirtual 1494	org/gudy/azureus2/core3/util/IndentWriter:exdent	()V
/*      */     //   586: aload_2
/*      */     //   587: invokevirtual 1493	org/gudy/azureus2/core3/util/IndentWriter:close	()V
/*      */     //   590: aload 14
/*      */     //   592: athrow
/*      */     //   593: new 938	org/gudy/azureus2/ui/swt/TextViewerWindow
/*      */     //   596: dup
/*      */     //   597: ldc_w 841
/*      */     //   600: invokestatic 1474	org/gudy/azureus2/core3/internat/MessageText:getString	(Ljava/lang/String;)Ljava/lang/String;
/*      */     //   603: aconst_null
/*      */     //   604: aload_1
/*      */     //   605: invokevirtual 1412	java/io/StringWriter:toString	()Ljava/lang/String;
/*      */     //   608: iconst_0
/*      */     //   609: invokespecial 1512	org/gudy/azureus2/ui/swt/TextViewerWindow:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V
/*      */     //   612: pop
/*      */     //   613: return
/*      */     // Line number table:
/*      */     //   Java source line #1678	-> byte code offset #0
/*      */     //   Java source line #1680	-> byte code offset #3
/*      */     //   Java source line #1682	-> byte code offset #11
/*      */     //   Java source line #1684	-> byte code offset #19
/*      */     //   Java source line #1686	-> byte code offset #27
/*      */     //   Java source line #1688	-> byte code offset #43
/*      */     //   Java source line #1691	-> byte code offset #47
/*      */     //   Java source line #1695	-> byte code offset #51
/*      */     //   Java source line #1697	-> byte code offset #55
/*      */     //   Java source line #1698	-> byte code offset #59
/*      */     //   Java source line #1695	-> byte code offset #62
/*      */     //   Java source line #1697	-> byte code offset #67
/*      */     //   Java source line #1700	-> byte code offset #73
/*      */     //   Java source line #1702	-> byte code offset #77
/*      */     //   Java source line #1704	-> byte code offset #85
/*      */     //   Java source line #1706	-> byte code offset #96
/*      */     //   Java source line #1708	-> byte code offset #122
/*      */     //   Java source line #1710	-> byte code offset #137
/*      */     //   Java source line #1713	-> byte code offset #140
/*      */     //   Java source line #1706	-> byte code offset #179
/*      */     //   Java source line #1716	-> byte code offset #185
/*      */     //   Java source line #1718	-> byte code offset #198
/*      */     //   Java source line #1720	-> byte code offset #224
/*      */     //   Java source line #1722	-> byte code offset #240
/*      */     //   Java source line #1725	-> byte code offset #244
/*      */     //   Java source line #1730	-> byte code offset #275
/*      */     //   Java source line #1732	-> byte code offset #289
/*      */     //   Java source line #1737	-> byte code offset #302
/*      */     //   Java source line #1734	-> byte code offset #305
/*      */     //   Java source line #1736	-> byte code offset #307
/*      */     //   Java source line #1739	-> byte code offset #312
/*      */     //   Java source line #1741	-> byte code offset #345
/*      */     //   Java source line #1743	-> byte code offset #349
/*      */     //   Java source line #1749	-> byte code offset #362
/*      */     //   Java source line #1750	-> byte code offset #366
/*      */     //   Java source line #1749	-> byte code offset #369
/*      */     //   Java source line #1751	-> byte code offset #378
/*      */     //   Java source line #1754	-> byte code offset #381
/*      */     //   Java source line #1756	-> byte code offset #385
/*      */     //   Java source line #1757	-> byte code offset #389
/*      */     //   Java source line #1754	-> byte code offset #392
/*      */     //   Java source line #1756	-> byte code offset #398
/*      */     //   Java source line #1759	-> byte code offset #405
/*      */     //   Java source line #1761	-> byte code offset #413
/*      */     //   Java source line #1763	-> byte code offset #418
/*      */     //   Java source line #1765	-> byte code offset #434
/*      */     //   Java source line #1768	-> byte code offset #438
/*      */     //   Java source line #1770	-> byte code offset #464
/*      */     //   Java source line #1772	-> byte code offset #475
/*      */     //   Java source line #1775	-> byte code offset #479
/*      */     //   Java source line #1779	-> byte code offset #487
/*      */     //   Java source line #1780	-> byte code offset #491
/*      */     //   Java source line #1779	-> byte code offset #494
/*      */     //   Java source line #1768	-> byte code offset #503
/*      */     //   Java source line #1784	-> byte code offset #509
/*      */     //   Java source line #1786	-> byte code offset #513
/*      */     //   Java source line #1787	-> byte code offset #517
/*      */     //   Java source line #1784	-> byte code offset #520
/*      */     //   Java source line #1786	-> byte code offset #526
/*      */     //   Java source line #1789	-> byte code offset #533
/*      */     //   Java source line #1791	-> byte code offset #541
/*      */     //   Java source line #1793	-> byte code offset #557
/*      */     //   Java source line #1796	-> byte code offset #561
/*      */     //   Java source line #1800	-> byte code offset #569
/*      */     //   Java source line #1802	-> byte code offset #573
/*      */     //   Java source line #1803	-> byte code offset #577
/*      */     //   Java source line #1800	-> byte code offset #580
/*      */     //   Java source line #1802	-> byte code offset #586
/*      */     //   Java source line #1805	-> byte code offset #593
/*      */     //   Java source line #1809	-> byte code offset #613
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   2	2	0	NL	String
/*      */     //   10	595	1	content	java.io.StringWriter
/*      */     //   42	545	2	iw	org.gudy.azureus2.core3.util.IndentWriter
/*      */     //   62	10	3	localObject1	Object
/*      */     //   76	486	3	core	AzureusCore
/*      */     //   94	3	4	plugins	org.gudy.azureus2.plugins.PluginInterface[]
/*      */     //   98	18	5	arr$	org.gudy.azureus2.plugins.PluginInterface[]
/*      */     //   196	49	5	dms	List<DownloadManager>
/*      */     //   103	8	6	len$	int
/*      */     //   251	13	6	i$	Iterator
/*      */     //   416	23	6	cats	org.gudy.azureus2.core3.category.Category[]
/*      */     //   106	74	7	i$	int
/*      */     //   273	77	7	dm	DownloadManager
/*      */     //   440	18	7	arr$	org.gudy.azureus2.core3.category.Category[]
/*      */     //   120	44	8	pi	org.gudy.azureus2.plugins.PluginInterface
/*      */     //   300	21	8	hash_str	String
/*      */     //   445	8	8	len$	int
/*      */     //   287	3	9	hash	byte[]
/*      */     //   305	3	9	e	Throwable
/*      */     //   448	56	9	i$	int
/*      */     //   369	7	10	localObject2	Object
/*      */     //   462	18	10	cat	org.gudy.azureus2.core3.category.Category
/*      */     //   392	11	11	localObject3	Object
/*      */     //   494	7	12	localObject4	Object
/*      */     //   520	11	13	localObject5	Object
/*      */     //   580	11	14	localObject6	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   47	51	62	finally
/*      */     //   62	63	62	finally
/*      */     //   275	302	305	java/lang/Throwable
/*      */     //   369	371	369	finally
/*      */     //   244	381	392	finally
/*      */     //   392	394	392	finally
/*      */     //   479	487	494	finally
/*      */     //   494	496	494	finally
/*      */     //   438	509	520	finally
/*      */     //   520	522	520	finally
/*      */     //   561	569	580	finally
/*      */     //   580	582	580	finally
/*      */   }
/*      */   
/*      */   public static void showText(final String title, final String message, List<String> lines)
/*      */   {
/* 1818 */     Utils.execSWTThreadLater(1, new Runnable()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/* 1825 */         StringBuilder text = new StringBuilder(this.val$lines.size() * 80);
/*      */         
/* 1827 */         for (String s : this.val$lines)
/*      */         {
/* 1829 */           if (text.length() > 0) {
/* 1830 */             text.append("\n");
/*      */           }
/* 1832 */           text.append(s);
/*      */         }
/*      */         
/* 1835 */         TextViewerWindow viewer = new TextViewerWindow(title, message, text.toString(), false);
/*      */         
/* 1837 */         viewer.setEditable(false);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addBlockedIPsMenuItem(Menu menu) {
/* 1843 */     addMenuItem(menu, "MainWindow.menu.view.ipFilter", new ListenerNeedingCoreRunning() {
/*      */       public void handleEvent(AzureusCore core, Event e) {
/* 1845 */         BlockedIpsWindow.showBlockedIps(core, MenuFactory.access$000().getMainShell());
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addConsoleMenuItem(Menu menu)
/*      */   {
/* 1852 */     addMenuItem(menu, "MainWindow.menu.view.console", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1854 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1855 */         if (uiFunctions != null) {
/* 1856 */           uiFunctions.getMDI().showEntryByID("LoggerView");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addStatisticsMenuItem(Menu menu)
/*      */   {
/* 1864 */     addMenuItem(menu, "MainWindow.menu.view.stats", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1866 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1867 */         if (uiFunctions != null) {
/* 1868 */           MultipleDocumentInterface mdi = uiFunctions.getMDI();
/* 1869 */           mdi.showEntryByID(StatsView.VIEW_ID);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addNatTestMenuItem(Menu menu) {
/* 1876 */     addMenuItem(menu, "MainWindow.menu.tools.nattest", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1878 */         new NatTestWindow();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addNetStatusMenuItem(Menu menu) {
/* 1884 */     addMenuItem(menu, "MainWindow.menu.tools.netstat", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1886 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 1887 */         if (uiFunctions != null)
/*      */         {
/* 1889 */           PluginsMenuHelper.IViewInfo[] views = PluginsMenuHelper.getInstance().getPluginViewsInfo();
/*      */           
/* 1891 */           for (PluginsMenuHelper.IViewInfo view : views)
/*      */           {
/* 1893 */             String viewID = view.viewID;
/*      */             
/* 1895 */             if ((viewID != null) && (viewID.equals("aznetstatus")))
/*      */             {
/* 1897 */               view.openView(uiFunctions);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addSpeedTestMenuItem(Menu menu) {
/* 1906 */     addMenuItem(menu, "MainWindow.menu.tools.speedtest", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1908 */         CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*      */           public void azureusCoreRunning(AzureusCore core) {
/* 1910 */             new SpeedTestWizard();
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addConfigWizardMenuItem(Menu menu) {
/* 1918 */     addMenuItem(menu, "MainWindow.menu.file.configure", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1920 */         new ConfigureWizard(false, 0);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addOptionsMenuItem(Menu menu) {
/* 1926 */     addMenuItem(menu, "MainWindow.menu.view.configuration", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 1928 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 1929 */         if (uiFunctions != null) {
/* 1930 */           uiFunctions.getMDI().showEntryByID("ConfigView");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addMinimizeWindowMenuItem(Menu menu)
/*      */   {
/* 1938 */     Shell shell = menu.getShell();
/*      */     
/* 1940 */     final org.eclipse.swt.widgets.MenuItem item = addMenuItem(menu, "MainWindow.menu.window.minimize", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 1943 */         if ((null == this.val$shell) || (this.val$shell.isDisposed())) {
/* 1944 */           event.doit = false;
/* 1945 */           return;
/*      */         }
/* 1947 */         this.val$shell.setMinimized(true);
/*      */       }
/*      */       
/* 1950 */     });
/* 1951 */     Listener enableHandler = new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1953 */         if ((null == this.val$shell) || (this.val$shell.isDisposed()) || (item.isDisposed()))
/*      */         {
/* 1955 */           event.doit = false;
/* 1956 */           return;
/*      */         }
/*      */         
/* 1959 */         if ((this.val$shell.getStyle() & 0x80) != 0) {
/* 1960 */           item.setEnabled(!this.val$shell.getMinimized());
/*      */         } else {
/* 1962 */           item.setEnabled(false);
/*      */         }
/*      */         
/*      */       }
/* 1966 */     };
/* 1967 */     menu.addListener(22, enableHandler);
/* 1968 */     shell.addListener(15, enableHandler);
/* 1969 */     shell.addListener(19, enableHandler);
/* 1970 */     shell.addListener(20, enableHandler);
/*      */     
/* 1972 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addBringAllToFrontMenuItem(Menu menu) {
/* 1976 */     org.eclipse.swt.widgets.MenuItem item = addMenuItem(menu, "MainWindow.menu.window.alltofront", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 1979 */         Iterator<Shell> iter = ShellManager.sharedManager().getWindows();
/* 1980 */         while (iter.hasNext()) {
/* 1981 */           Shell shell = (Shell)iter.next();
/* 1982 */           if ((!shell.isDisposed()) && (!shell.getMinimized())) {
/* 1983 */             shell.open();
/*      */           }
/*      */         }
/*      */       }
/* 1987 */     });
/* 1988 */     Listener enableHandler = new Listener() {
/*      */       public void handleEvent(Event event) {
/* 1990 */         if (this.val$item.isDisposed()) {
/* 1991 */           return;
/*      */         }
/* 1993 */         Iterator<Shell> iter = ShellManager.sharedManager().getWindows();
/* 1994 */         boolean hasNonMaximizedShell = false;
/* 1995 */         while (iter.hasNext()) {
/* 1996 */           Shell shell = (Shell)iter.next();
/* 1997 */           if ((!shell.isDisposed()) && (!shell.getMinimized())) {
/* 1998 */             hasNonMaximizedShell = true;
/* 1999 */             break;
/*      */           }
/*      */         }
/* 2002 */         this.val$item.setEnabled(hasNonMaximizedShell);
/*      */       }
/*      */       
/* 2005 */     };
/* 2006 */     menu.addListener(22, enableHandler);
/* 2007 */     menu.getShell().addListener(15, enableHandler);
/*      */     
/* 2009 */     ShellManager.sharedManager().addWindowAddedListener(enableHandler);
/* 2010 */     ShellManager.sharedManager().addWindowRemovedListener(enableHandler);
/* 2011 */     item.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent event) {
/* 2013 */         ShellManager.sharedManager().removeWindowAddedListener(this.val$enableHandler);
/* 2014 */         ShellManager.sharedManager().removeWindowRemovedListener(this.val$enableHandler);
/*      */       }
/*      */       
/* 2017 */     });
/* 2018 */     return item;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void appendWindowMenuItems(Menu menuParent)
/*      */   {
/* 2027 */     final Shell shell = menuParent.getShell();
/* 2028 */     final int numTopItems = menuParent.getItemCount();
/* 2029 */     Listener rebuild = new Listener() {
/*      */       public void handleEvent(Event event) {
/*      */         try {
/* 2032 */           if ((this.val$menuParent.isDisposed()) || (shell.isDisposed())) {
/* 2033 */             return;
/*      */           }
/* 2035 */           int size = ShellManager.sharedManager().getSize();
/* 2036 */           if (size == this.val$menuParent.getItemCount() - numTopItems) {
/* 2037 */             for (int i = numTopItems; i < this.val$menuParent.getItemCount(); i++) {
/* 2038 */               org.eclipse.swt.widgets.MenuItem item = this.val$menuParent.getItem(i);
/* 2039 */               item.setSelection(item.getData() == shell);
/*      */             }
/* 2041 */             return;
/*      */           }
/*      */           
/* 2044 */           for (int i = numTopItems; i < this.val$menuParent.getItemCount();) {
/* 2045 */             this.val$menuParent.getItem(i).dispose();
/*      */           }
/* 2047 */           Iterator<Shell> iter = ShellManager.sharedManager().getWindows();
/* 2048 */           for (int i = 0; i < size; i++) {
/* 2049 */             final Shell sh = (Shell)iter.next();
/*      */             
/* 2051 */             if ((!sh.isDisposed()) && (sh.getText().length() != 0))
/*      */             {
/*      */ 
/* 2054 */               org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(this.val$menuParent, 32);
/*      */               
/* 2056 */               item.setText(sh.getText());
/* 2057 */               item.setSelection(shell == sh);
/* 2058 */               item.setData(sh);
/*      */               
/* 2060 */               item.addSelectionListener(new SelectionAdapter() {
/*      */                 public void widgetSelected(SelectionEvent event) {
/* 2062 */                   if ((event.widget.isDisposed()) || (sh.isDisposed())) {
/* 2063 */                     return;
/*      */                   }
/* 2065 */                   if (sh.getMinimized()) {
/* 2066 */                     sh.setMinimized(false);
/*      */                   }
/* 2068 */                   sh.open();
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/* 2073 */         } catch (Exception e) { Logger.log(new LogEvent(LogIDs.GUI, "rebuild menu error", e));
/*      */         }
/*      */         
/*      */       }
/* 2077 */     };
/* 2078 */     ShellManager.sharedManager().addWindowAddedListener(rebuild);
/* 2079 */     ShellManager.sharedManager().addWindowRemovedListener(rebuild);
/* 2080 */     shell.addListener(15, rebuild);
/* 2081 */     menuParent.addListener(22, rebuild);
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addZoomWindowMenuItem(Menu menuParent) {
/* 2085 */     Shell shell = menuParent.getShell();
/* 2086 */     final org.eclipse.swt.widgets.MenuItem item = addMenuItem(menuParent, "MainWindow.menu.window.zoom", new Listener()
/*      */     {
/*      */       public void handleEvent(Event event) {
/* 2089 */         if (this.val$shell.isDisposed()) {
/* 2090 */           event.doit = false;
/* 2091 */           return;
/*      */         }
/* 2093 */         this.val$shell.setMaximized(!this.val$shell.getMaximized());
/*      */       }
/*      */       
/* 2096 */     });
/* 2097 */     Listener enableHandler = new Listener() {
/*      */       public void handleEvent(Event event) {
/* 2099 */         if ((!this.val$shell.isDisposed()) && (!item.isDisposed())) {
/* 2100 */           if (!Constants.isOSX) {
/* 2101 */             if (this.val$shell.getMaximized()) {
/* 2102 */               Messages.setLanguageText(item, MessageText.resolveLocalizationKey("MainWindow.menu.window.zoom.restore"));
/*      */             }
/*      */             else
/*      */             {
/* 2106 */               Messages.setLanguageText(item, MessageText.resolveLocalizationKey("MainWindow.menu.window.zoom.maximize"));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 2112 */           if ((this.val$shell.getStyle() & 0x400) != 0) {
/* 2113 */             item.setEnabled(!this.val$shell.getMinimized());
/*      */           } else {
/* 2115 */             item.setEnabled(false);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 2120 */     };
/* 2121 */     menuParent.addListener(22, enableHandler);
/* 2122 */     shell.addListener(15, enableHandler);
/* 2123 */     shell.addListener(19, enableHandler);
/* 2124 */     shell.addListener(20, enableHandler);
/*      */     
/* 2126 */     return item;
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addAboutMenuItem(Menu menu) {
/* 2130 */     addMenuItem(menu, "MainWindow.menu.help.about", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {}
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addHealthMenuItem(Menu menu)
/*      */   {
/* 2138 */     addMenuItem(menu, "MyTorrentsView.menu.health", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2140 */         HealthHelpWindow.show(MenuFactory.access$600());
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addWhatsNewMenuItem(Menu menu) {
/* 2146 */     addMenuItem(menu, "MainWindow.menu.help.whatsnew", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2148 */         Utils.launch("http://plugins.vuze.com/changelog.php?version=5.7.6.0");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addWikiMenuItem(Menu menu)
/*      */   {
/* 2155 */     addMenuItem(menu, "MainWindow.menu.community.wiki", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2157 */         Utils.launch("http://wiki.vuze.com/w/");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addVoteMenuItem(Menu menu) {
/* 2163 */     addMenuItem(menu, "MainWindow.menu.vote", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2165 */         Utils.launch(MessageText.getString("vote.vuze.url"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addReleaseNotesMenuItem(Menu menu) {
/* 2171 */     addMenuItem(menu, "MainWindow.menu.help.releasenotes", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2173 */         new org.gudy.azureus2.ui.swt.welcome.WelcomeWindow(this.val$menu.getShell());
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addHelpSupportMenuItem(Menu menu, String support_url)
/*      */   {
/* 2180 */     addMenuItem(menu, "MainWindow.menu.help.support", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2182 */         Utils.launch(this.val$support_url);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addDonationMenuItem(Menu menu) {
/* 2188 */     addMenuItem(menu, "MainWindow.menu.help.donate", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2190 */         org.gudy.azureus2.ui.swt.donations.DonationWindow.open(true, "menu");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addGetPluginsMenuItem(Menu menu)
/*      */   {
/* 2197 */     addMenuItem(menu, "MainWindow.menu.help.plugins", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2199 */         Utils.launch("http://plugins.vuze.com/plugin_list.php");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem addDebugHelpMenuItem(Menu menu)
/*      */   {
/* 2207 */     addMenuItem(menu, "MainWindow.menu.help.debug", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2209 */         UIDebugGenerator.generate(Constants.APP_NAME + " " + "5.7.6.0", "Generated via Help Menu");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addCheckUpdateMenuItem(Menu menu)
/*      */   {
/* 2216 */     addMenuItem(menu, "MainWindow.menu.help.checkupdate", new ListenerNeedingCoreRunning() {
/*      */       public void handleEvent(final AzureusCore core, Event e) {
/* 2218 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 2219 */         if (uiFunctions != null) {
/* 2220 */           uiFunctions.bringToFront();
/*      */         }
/* 2222 */         Utils.getOffOfSWTThread(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 2228 */             UpdateMonitor.getSingleton(core).performCheck(true, false, false, new org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener()
/*      */             {
/*      */               public void cancelled(UpdateCheckInstance instance) {}
/*      */               
/*      */               public void complete(UpdateCheckInstance instance)
/*      */               {
/* 2234 */                 Update[] updates = instance.getUpdates();
/* 2235 */                 boolean hasUpdates = false;
/* 2236 */                 for (Update update : updates) {
/* 2237 */                   if (update.getDownloaders().length > 0) {
/* 2238 */                     hasUpdates = true;
/* 2239 */                     break;
/*      */                   }
/*      */                 }
/* 2242 */                 if (!hasUpdates)
/*      */                 {
/* 2244 */                   int build = Constants.getIncrementalBuild();
/*      */                   
/* 2246 */                   if ((COConfigurationManager.getBooleanParameter("Beta Programme Enabled")) && (build > 0))
/*      */                   {
/* 2248 */                     String build_str = "" + build;
/*      */                     
/* 2250 */                     if (build_str.length() == 1)
/*      */                     {
/* 2252 */                       build_str = "0" + build_str;
/*      */                     }
/*      */                     
/* 2255 */                     MessageBoxShell mb = new MessageBoxShell(34, "window.update.noupdates.beta", new String[] { "B" + build_str });
/*      */                     
/*      */ 
/*      */ 
/* 2259 */                     mb.open(null);
/*      */                   }
/*      */                   else
/*      */                   {
/* 2263 */                     MessageBoxShell mb = new MessageBoxShell(34, "window.update.noupdates", (String[])null);
/*      */                     
/*      */ 
/*      */ 
/* 2267 */                     mb.open(null);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static void addBetaMenuItem(Menu menuParent) {
/* 2279 */     org.eclipse.swt.widgets.MenuItem menuItem = addMenuItem(menuParent, "MainWindow.menu.beta", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/* 2282 */         new org.gudy.azureus2.ui.swt.beta.BetaWizard();
/*      */       }
/*      */       
/* 2285 */     });
/* 2286 */     COConfigurationManager.addAndFireParameterListener("Beta Programme Enabled", new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName) {
/* 2289 */         Utils.execSWTThread(new AERunnable() {
/*      */           public void runSupport() {
/* 2291 */             if (MenuFactory.77.this.val$menuItem.isDisposed()) {
/* 2292 */               return;
/*      */             }
/* 2294 */             boolean enabled = COConfigurationManager.getBooleanParameter("Beta Programme Enabled");
/* 2295 */             Messages.setLanguageText(MenuFactory.77.this.val$menuItem, MessageText.resolveLocalizationKey("MainWindow.menu.beta" + (enabled ? ".off" : ".on")));
/*      */           }
/*      */           
/*      */ 
/*      */         });
/*      */       }
/*      */       
/*      */ 
/* 2303 */     });
/* 2304 */     boolean enabled = COConfigurationManager.getBooleanParameter("Beta Programme Enabled");
/*      */     
/* 2306 */     if (enabled)
/*      */     {
/* 2308 */       addMenuItem(menuParent, "MainWindow.menu.report.beta.problem", new Listener() {
/*      */         public void handleEvent(Event e) {
/* 2310 */           Utils.launch(MessageText.getString("beta.bug.url"));
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addPluginInstallMenuItem(Menu menuParent) {
/* 2317 */     addMenuItem(menuParent, "MainWindow.menu.plugins.installPlugins", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/* 2320 */         new InstallPluginWizard();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem addPluginUnInstallMenuItem(Menu menuParent) {
/* 2326 */     addMenuItem(menuParent, "MainWindow.menu.plugins.uninstallPlugins", new Listener()
/*      */     {
/*      */       public void handleEvent(Event e) {
/* 2329 */         new UnInstallPluginWizard(MenuFactory.access$600());
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void addAlertsMenu(Menu menu, boolean createSubmenu, final DownloadManager[] dms)
/*      */   {
/* 2340 */     if (dms.length == 0) {
/*      */       return;
/*      */     }
/*      */     
/*      */ 
/*      */     Menu alert_menu;
/*      */     
/* 2347 */     if (createSubmenu) {
/* 2348 */       Menu alert_menu = new Menu(menu.getShell(), 4);
/*      */       
/* 2350 */       org.eclipse.swt.widgets.MenuItem alerts_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */       
/* 2352 */       Messages.setLanguageText(alerts_item, "ConfigView.section.interface.alerts");
/*      */       
/* 2354 */       alerts_item.setMenu(alert_menu);
/*      */     } else {
/* 2356 */       alert_menu = menu;
/*      */     }
/*      */     
/* 2359 */     String[][] alert_keys = { { "Play Download Finished", "playdownloadfinished" }, { "Play Download Finished Announcement", "playdownloadspeech" }, { "Popup Download Finished", "popupdownloadfinished" } };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2365 */     boolean[] all_enabled = new boolean[alert_keys.length];
/*      */     
/* 2367 */     Arrays.fill(all_enabled, true);
/*      */     
/* 2369 */     for (DownloadManager dm : dms)
/*      */     {
/* 2371 */       DownloadManagerState state = dm.getDownloadState();
/*      */       
/* 2373 */       Map map = state.getMapAttribute("df_alerts");
/*      */       
/* 2375 */       if (map == null)
/*      */       {
/* 2377 */         Arrays.fill(all_enabled, false);
/*      */       }
/*      */       else
/*      */       {
/* 2381 */         for (int i = 0; i < alert_keys.length; i++)
/*      */         {
/* 2383 */           if (!map.containsKey(alert_keys[i][0]))
/*      */           {
/* 2385 */             all_enabled[i] = false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2391 */     for (int i = 0; i < alert_keys.length; i++)
/*      */     {
/* 2393 */       final String[] entry = alert_keys[i];
/*      */       
/* 2395 */       if ((i != 1) || (Constants.isOSX))
/*      */       {
/* 2397 */         org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(alert_menu, 32);
/*      */         
/* 2399 */         item.setText(MessageText.getString("ConfigView.label." + entry[1]));
/*      */         
/* 2401 */         item.setSelection(all_enabled[i]);
/*      */         
/* 2403 */         item.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event event)
/*      */           {
/*      */ 
/*      */ 
/* 2411 */             boolean selected = this.val$item.getSelection();
/*      */             
/* 2413 */             for (DownloadManager dm : dms)
/*      */             {
/* 2415 */               DownloadManagerState state = dm.getDownloadState();
/*      */               
/* 2417 */               Map map = state.getMapAttribute("df_alerts");
/*      */               
/* 2419 */               if (map == null)
/*      */               {
/* 2421 */                 map = new HashMap();
/*      */               }
/*      */               else
/*      */               {
/* 2425 */                 map = new HashMap(map);
/*      */               }
/*      */               
/* 2428 */               if (selected)
/*      */               {
/* 2430 */                 map.put(entry[0], "");
/*      */               }
/*      */               else
/*      */               {
/* 2434 */                 map.remove(entry[0]);
/*      */               }
/*      */               
/* 2437 */               state.setMapAttribute("df_alerts", map);
/*      */             }
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
/*      */   public static void addAlertsMenu(Menu menu, DownloadManager dm, final DiskManagerFileInfo[] files)
/*      */   {
/* 2451 */     if (files.length == 0)
/*      */     {
/* 2453 */       return;
/*      */     }
/*      */     
/* 2456 */     String[][] alert_keys = { { "Play File Finished", "playfilefinished" }, { "Play File Finished Announcement", "playfilespeech" }, { "Popup File Finished", "popupfilefinished" } };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2463 */     Menu alert_menu = new Menu(menu.getShell(), 4);
/*      */     
/* 2465 */     org.eclipse.swt.widgets.MenuItem alerts_item = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*      */     
/* 2467 */     Messages.setLanguageText(alerts_item, "ConfigView.section.interface.alerts");
/*      */     
/* 2469 */     alerts_item.setMenu(alert_menu);
/*      */     
/* 2471 */     boolean[] all_enabled = new boolean[alert_keys.length];
/*      */     
/* 2473 */     DownloadManagerState state = dm.getDownloadState();
/*      */     
/* 2475 */     Map map = state.getMapAttribute("df_alerts");
/*      */     
/* 2477 */     if (map != null)
/*      */     {
/* 2479 */       Arrays.fill(all_enabled, true);
/*      */       
/* 2481 */       for (DiskManagerFileInfo file : files)
/*      */       {
/* 2483 */         for (int i = 0; i < alert_keys.length; i++)
/*      */         {
/* 2485 */           String key = String.valueOf(file.getIndex()) + "." + alert_keys[i][0];
/*      */           
/* 2487 */           if (!map.containsKey(key))
/*      */           {
/* 2489 */             all_enabled[i] = false;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2495 */     for (int i = 0; i < alert_keys.length; i++)
/*      */     {
/* 2497 */       final String[] entry = alert_keys[i];
/*      */       
/* 2499 */       if ((i != 1) || (Constants.isOSX))
/*      */       {
/* 2501 */         final org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(alert_menu, 32);
/*      */         
/* 2503 */         item.setText(MessageText.getString("ConfigView.label." + entry[1]));
/*      */         
/* 2505 */         item.setSelection(all_enabled[i]);
/*      */         
/* 2507 */         item.addListener(13, new Listener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void handleEvent(Event event)
/*      */           {
/*      */ 
/*      */ 
/* 2515 */             DownloadManagerState state = this.val$dm.getDownloadState();
/*      */             
/* 2517 */             Map map = state.getMapAttribute("df_alerts");
/*      */             
/* 2519 */             if (map == null)
/*      */             {
/* 2521 */               map = new HashMap();
/*      */             }
/*      */             else
/*      */             {
/* 2525 */               map = new HashMap(map);
/*      */             }
/*      */             
/* 2528 */             boolean selected = item.getSelection();
/*      */             
/* 2530 */             for (DiskManagerFileInfo file : files)
/*      */             {
/* 2532 */               String key = String.valueOf(file.getIndex()) + "." + entry[0];
/*      */               
/* 2534 */               if (selected)
/*      */               {
/* 2536 */                 map.put(key, "");
/*      */               }
/*      */               else
/*      */               {
/* 2540 */                 map.remove(key);
/*      */               }
/*      */             }
/*      */             
/* 2544 */             state.setMapAttribute("df_alerts", map);
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
/*      */ 
/*      */   public static final org.eclipse.swt.widgets.MenuItem addLabelMenuItem(Menu menu, String localizationKey)
/*      */   {
/* 2559 */     org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 2560 */     Messages.setLanguageText(item, localizationKey);
/* 2561 */     item.setEnabled(false);
/* 2562 */     return item;
/*      */   }
/*      */   
/*      */   public static void addPairingMenuItem(Menu menu) {
/* 2566 */     addMenuItem(menu, "MainWindow.menu.pairing", new Listener() {
/*      */       public void handleEvent(Event e) {
/* 2568 */         UIFunctionsSWT uiFunctionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 2569 */         if (uiFunctionsSWT != null) {
/* 2570 */           uiFunctionsSWT.openRemotePairingWindow();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem addSeparatorMenuItem(Menu menuParent)
/*      */   {
/* 2579 */     return new org.eclipse.swt.widgets.MenuItem(menuParent, 2);
/*      */   }
/*      */   
/*      */   public static org.eclipse.swt.widgets.MenuItem createTopLevelMenuItem(Menu menuParent, String localizationKey)
/*      */   {
/* 2584 */     Menu menu = new Menu(menuParent.getShell(), 4);
/* 2585 */     org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(menuParent, 64);
/* 2586 */     Messages.setLanguageText(menuItem, localizationKey);
/* 2587 */     menuItem.setMenu(menu);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2592 */     menu.setData("key.menu.id", localizationKey);
/* 2593 */     menuItem.setData("key.menu.id", localizationKey);
/*      */     
/* 2595 */     return menuItem;
/*      */   }
/*      */   
/*      */   public static final org.eclipse.swt.widgets.MenuItem addMenuItem(Menu menu, String localizationKey, Listener selListener)
/*      */   {
/* 2600 */     return addMenuItem(menu, localizationKey, selListener, 0);
/*      */   }
/*      */   
/*      */   public static final org.eclipse.swt.widgets.MenuItem addMenuItem(Menu menu, String localizationKey, Listener selListener, int style)
/*      */   {
/* 2605 */     org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(menu, style);
/* 2606 */     Messages.setLanguageText(menuItem, MessageText.resolveLocalizationKey(localizationKey));
/*      */     
/* 2608 */     KeyBindings.setAccelerator(menuItem, MessageText.resolveAcceleratorKey(localizationKey));
/*      */     
/* 2610 */     if (null != selListener) {
/* 2611 */       menuItem.addListener(13, selListener);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2617 */     menuItem.setData("key.menu.id", localizationKey);
/* 2618 */     return menuItem;
/*      */   }
/*      */   
/*      */   public static final org.eclipse.swt.widgets.MenuItem addMenuItem(Menu menu, int style, String localizationKey, Listener selListener)
/*      */   {
/* 2623 */     return addMenuItem(menu, style, -1, localizationKey, selListener);
/*      */   }
/*      */   
/*      */   public static final org.eclipse.swt.widgets.MenuItem addMenuItem(Menu menu, int style, int index, String localizationKey, Listener selListener)
/*      */   {
/* 2628 */     if ((index < 0) || (index > menu.getItemCount())) {
/* 2629 */       index = menu.getItemCount();
/*      */     }
/* 2631 */     org.eclipse.swt.widgets.MenuItem menuItem = new org.eclipse.swt.widgets.MenuItem(menu, style, index);
/* 2632 */     Messages.setLanguageText(menuItem, localizationKey);
/* 2633 */     KeyBindings.setAccelerator(menuItem, localizationKey);
/* 2634 */     menuItem.addListener(13, selListener);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2639 */     menuItem.setData("key.menu.id", localizationKey);
/* 2640 */     return menuItem;
/*      */   }
/*      */   
/*      */   private static UIFunctionsSWT getUIFunctionSWT() {
/* 2644 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 2645 */     if (null != uiFunctions) {
/* 2646 */       return uiFunctions;
/*      */     }
/* 2648 */     throw new IllegalStateException("No instance of UIFunctionsSWT found; the UIFunctionsManager might not have been initialized properly");
/*      */   }
/*      */   
/*      */   private static Display getDisplay()
/*      */   {
/* 2653 */     return SWTThread.getInstance().getDisplay();
/*      */   }
/*      */   
/*      */   public static void updateMenuText(Object menu) {
/* 2657 */     if (menu == null)
/* 2658 */       return;
/* 2659 */     if ((menu instanceof Menu)) {
/* 2660 */       org.eclipse.swt.widgets.MenuItem[] menus = ((Menu)menu).getItems();
/* 2661 */       for (int i = 0; i < menus.length; i++) {
/* 2662 */         updateMenuText(menus[i]);
/*      */       }
/* 2664 */     } else if ((menu instanceof org.eclipse.swt.widgets.MenuItem)) {
/* 2665 */       org.eclipse.swt.widgets.MenuItem item = (org.eclipse.swt.widgets.MenuItem)menu;
/* 2666 */       if ((item.getData("key.menu.id") instanceof String)) {
/* 2667 */         String localizationKey = (String)item.getData("key.menu.id");
/* 2668 */         item.setText(MessageText.getString(localizationKey));
/* 2669 */         KeyBindings.setAccelerator(item, MessageText.resolveAcceleratorKey(localizationKey));
/*      */         
/* 2671 */         updateMenuText(item.getMenu());
/*      */       } else {
/* 2673 */         Messages.updateLanguageForControl(item);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static void performOneTimeDisable(org.eclipse.swt.widgets.MenuItem item, boolean affectsChildMenuItems)
/*      */   {
/* 2680 */     item.setEnabled(false);
/* 2681 */     if (affectsChildMenuItems) {
/* 2682 */       Menu childMenu = item.getMenu();
/* 2683 */       if (childMenu == null) {
/* 2684 */         return;
/*      */       }
/* 2686 */       for (int i = 0; i < childMenu.getItemCount(); i++) {
/* 2687 */         childMenu.getItem(i).setEnabled(false);
/*      */       }
/*      */     }
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
/*      */   public static Menu findMenu(Menu menuToStartWith, String idToMatch)
/*      */   {
/* 2706 */     if ((null == menuToStartWith) || (menuToStartWith.isDisposed()) || (null == idToMatch) || (idToMatch.length() < 1))
/*      */     {
/* 2708 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2714 */     if (idToMatch.equals(getID(menuToStartWith))) {
/* 2715 */       return menuToStartWith;
/*      */     }
/*      */     
/* 2718 */     org.eclipse.swt.widgets.MenuItem[] items = menuToStartWith.getItems();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2723 */     for (int i = 0; i < items.length; i++) {
/* 2724 */       org.eclipse.swt.widgets.MenuItem item = items[i];
/* 2725 */       Menu menuToFind = findMenu(item.getMenu(), idToMatch);
/* 2726 */       if (null != menuToFind) {
/* 2727 */         return menuToFind;
/*      */       }
/*      */     }
/*      */     
/* 2731 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem findMenuItem(Menu menuToStartWith, String idToMatch)
/*      */   {
/* 2742 */     return findMenuItem(menuToStartWith, idToMatch, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static org.eclipse.swt.widgets.MenuItem findMenuItem(Menu menuToStartWith, String idToMatch, boolean deep)
/*      */   {
/* 2751 */     if ((null == menuToStartWith) || (menuToStartWith.isDisposed()) || (null == idToMatch) || (idToMatch.length() < 1))
/*      */     {
/* 2753 */       return null;
/*      */     }
/*      */     
/* 2756 */     org.eclipse.swt.widgets.MenuItem[] items = menuToStartWith.getItems();
/*      */     
/* 2758 */     for (int i = 0; i < items.length; i++) {
/* 2759 */       org.eclipse.swt.widgets.MenuItem item = items[i];
/* 2760 */       if (idToMatch.equals(getID(item))) {
/* 2761 */         return item;
/*      */       }
/*      */       
/* 2764 */       if (deep)
/*      */       {
/*      */ 
/*      */ 
/* 2768 */         org.eclipse.swt.widgets.MenuItem menuItemToFind = findMenuItem(item.getMenu(), idToMatch);
/* 2769 */         if (null != menuItemToFind) {
/* 2770 */           return menuItemToFind;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2775 */     return null;
/*      */   }
/*      */   
/*      */   private static String getID(Widget widget) {
/* 2779 */     if ((null != widget) && (!widget.isDisposed())) {
/* 2780 */       Object id = widget.getData("key.menu.id");
/* 2781 */       if (null != id) {
/* 2782 */         return id.toString();
/*      */       }
/*      */     }
/* 2785 */     return "";
/*      */   }
/*      */   
/*      */   public static void setEnablementKeys(Widget widget, int keys) {
/* 2789 */     if ((null != widget) && (!widget.isDisposed())) {
/* 2790 */       widget.setData("key.enablement", new Integer(keys));
/*      */     }
/*      */   }
/*      */   
/*      */   public static int getEnablementKeys(Widget widget) {
/* 2795 */     if ((null != widget) && (!widget.isDisposed())) {
/* 2796 */       Object keys = widget.getData("key.enablement");
/* 2797 */       if ((keys instanceof Integer)) {
/* 2798 */         return ((Integer)keys).intValue();
/*      */       }
/*      */     }
/* 2801 */     return -1;
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
/*      */   public static void updateEnabledStates(Menu menuToStartWith)
/*      */   {
/* 2818 */     if ((null == menuToStartWith) || (menuToStartWith.isDisposed())) {
/* 2819 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2826 */     if (!setEnablement(menuToStartWith)) {
/* 2827 */       return;
/*      */     }
/*      */     
/* 2830 */     org.eclipse.swt.widgets.MenuItem[] items = menuToStartWith.getItems();
/*      */     
/* 2832 */     for (int i = 0; i < items.length; i++) {
/* 2833 */       org.eclipse.swt.widgets.MenuItem item = items[i];
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2839 */       if (setEnablement(item))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2846 */         updateEnabledStates(item.getMenu());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean setEnablement(Widget widget)
/*      */   {
/* 2858 */     if ((null != widget) && (!widget.isDisposed())) {
/* 2859 */       boolean isEnabled = isEnabledForCurrentMode(widget);
/*      */       
/* 2861 */       if ((widget instanceof org.eclipse.swt.widgets.MenuItem)) {
/* 2862 */         ((org.eclipse.swt.widgets.MenuItem)widget).setEnabled(isEnabled);
/* 2863 */       } else if ((widget instanceof Menu)) {
/* 2864 */         ((Menu)widget).setEnabled(isEnabled);
/*      */       }
/* 2866 */       return isEnabled;
/*      */     }
/* 2868 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isEnabledForCurrentMode(Widget widget)
/*      */   {
/* 2878 */     int keys = getEnablementKeys(widget);
/* 2879 */     if (keys <= 0)
/* 2880 */       return true;
/* 2881 */     if (isAZ3) {
/* 2882 */       return (keys & 0x2) != 0;
/*      */     }
/* 2884 */     return (keys & 0x1) != 0;
/*      */   }
/*      */   
/*      */ 
/* 2888 */   private static final boolean DEBUG_SET_FOREGROUND = System.getProperty("debug.setforeground") != null;
/*      */   
/*      */   private static TableRow wrapAsRow(Object o, final String table_name) {
/* 2891 */     new TableRow() {
/* 2892 */       public Object getDataSource() { return this.val$o; }
/* 2893 */       public String getTableID() { return table_name; }
/*      */       
/*      */       private void notSupported() {
/* 2896 */         throw new RuntimeException("method is not supported - table row is a \"virtual\" one, only getDataSource and getTableID are supported.");
/*      */       }
/*      */       
/*      */       private void setForegroundDebug() {
/* 2900 */         if (MenuFactory.DEBUG_SET_FOREGROUND) {
/* 2901 */           Debug.out("setForeground on fake TableRow");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 2906 */       public TableView<?> getView() { return null; }
/* 2907 */       public int getIndex() { notSupported();return 0; }
/* 2908 */       public void setForeground(int red, int green, int blue) { setForegroundDebug();notSupported(); }
/* 2909 */       public void setForeground(int[] rgb) { setForegroundDebug();notSupported(); }
/* 2910 */       public void setForegroundToErrorColor() { setForegroundDebug();notSupported(); }
/* 2911 */       public boolean isValid() { notSupported();return false; }
/* 2912 */       public TableCell getTableCell(String sColumnName) { notSupported();return null; }
/* 2913 */       public boolean isSelected() { notSupported();return false; }
/* 2914 */       public void addMouseListener(TableRowMouseListener listener) { notSupported(); }
/* 2915 */       public void removeMouseListener(TableRowMouseListener listener) { notSupported(); }
/* 2916 */       public Object getData(String id) { return null; }
/*      */       
/*      */       public void setData(String id, Object data) {}
/*      */     };
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/MenuFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */