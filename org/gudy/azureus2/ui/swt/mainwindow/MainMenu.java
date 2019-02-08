/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MainMenu
/*     */   implements IMainMenu
/*     */ {
/*     */   private Menu menuBar;
/*     */   
/*     */   public MainMenu(Shell shell)
/*     */   {
/*  49 */     createMenus(shell);
/*     */   }
/*     */   
/*     */   public void linkMenuBar(Shell parent) {
/*  53 */     parent.setMenuBar(this.menuBar);
/*     */   }
/*     */   
/*     */ 
/*     */   private void createMenus(Shell parent)
/*     */   {
/*  59 */     this.menuBar = new Menu(parent, 2);
/*  60 */     parent.setMenuBar(this.menuBar);
/*     */     
/*  62 */     addFileMenu(parent);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */     if (Constants.isOSX) {
/*  70 */       addViewMenu(parent);
/*  71 */       addTransferMenu(parent);
/*  72 */       addTorrentMenu(parent);
/*     */     } else {
/*  74 */       addTransferMenu(parent);
/*  75 */       addTorrentMenu(parent);
/*  76 */       addViewMenu(parent);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  81 */       addToolsMenu(parent);
/*     */     }
/*     */     
/*  84 */     addPluginsMenu(parent);
/*     */     
/*     */ 
/*  87 */     if (Constants.isOSX) {
/*  88 */       addWindowMenu(parent);
/*     */     }
/*     */     
/*     */ 
/*  92 */     if (Constants.isCVSVersion()) {
/*  93 */       DebugMenuHelper.createDebugMenuItem(this.menuBar);
/*     */     }
/*     */     
/*  96 */     addV2HelpMenu(parent);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 104 */     MenuFactory.updateEnabledStates(this.menuBar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addWindowMenu(Shell parent)
/*     */   {
/* 113 */     MenuItem menu_window = MenuFactory.createWindowMenuItem(this.menuBar);
/* 114 */     Menu windowMenu = menu_window.getMenu();
/*     */     
/* 116 */     MenuFactory.addMinimizeWindowMenuItem(windowMenu);
/* 117 */     MenuFactory.addZoomWindowMenuItem(windowMenu);
/*     */     
/* 119 */     MenuFactory.addSeparatorMenuItem(windowMenu);
/* 120 */     MenuFactory.addBlockedIPsMenuItem(windowMenu);
/*     */     
/* 122 */     MenuFactory.addSeparatorMenuItem(windowMenu);
/* 123 */     MenuFactory.addBringAllToFrontMenuItem(windowMenu);
/*     */     
/* 125 */     MenuFactory.addSeparatorMenuItem(windowMenu);
/* 126 */     MenuFactory.appendWindowMenuItems(windowMenu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addFileMenu(Shell parent)
/*     */   {
/* 134 */     MenuItem fileItem = MenuFactory.createFileMenuItem(this.menuBar);
/* 135 */     Menu fileMenu = fileItem.getMenu();
/*     */     
/* 137 */     MenuFactory.addCreateMenuItem(fileMenu);
/*     */     
/* 139 */     MenuItem openMenuItem = MenuFactory.createOpenMenuItem(fileMenu);
/*     */     
/* 141 */     Menu openSubMenu = openMenuItem.getMenu();
/* 142 */     MenuFactory.addOpenTorrentMenuItem(openSubMenu);
/* 143 */     MenuFactory.addOpenURIMenuItem(openSubMenu);
/* 144 */     MenuFactory.addOpenTorrentForTrackingMenuItem(openSubMenu);
/* 145 */     MenuFactory.addOpenVuzeFileMenuItem(openSubMenu);
/*     */     
/* 147 */     MenuItem shareMenuItem = MenuFactory.createShareMenuItem(fileMenu);
/*     */     
/* 149 */     Menu shareSubMenu = shareMenuItem.getMenu();
/* 150 */     MenuFactory.addShareFileMenuItem(shareSubMenu);
/* 151 */     MenuFactory.addShareFolderMenuItem(shareSubMenu);
/* 152 */     MenuFactory.addShareFolderContentMenuItem(shareSubMenu);
/* 153 */     MenuFactory.addShareFolderContentRecursiveMenuItem(shareSubMenu);
/*     */     
/* 155 */     MenuFactory.addSearchMenuItem(fileMenu);
/*     */     
/* 157 */     MenuFactory.addSeparatorMenuItem(fileMenu);
/* 158 */     MenuFactory.addImportMenuItem(fileMenu);
/* 159 */     MenuFactory.addExportMenuItem(fileMenu);
/*     */     
/* 161 */     MenuFactory.addSeparatorMenuItem(fileMenu);
/* 162 */     MenuFactory.addCloseWindowMenuItem(fileMenu);
/* 163 */     MenuFactory.addCloseTabMenuItem(fileMenu);
/* 164 */     MenuFactory.addCloseDetailsMenuItem(fileMenu);
/* 165 */     MenuFactory.addCloseDownloadBarsToMenu(fileMenu);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */     if (!Utils.isCarbon) {
/* 172 */       MenuFactory.addSeparatorMenuItem(fileMenu);
/* 173 */       MenuFactory.addRestartMenuItem(fileMenu);
/*     */     }
/* 175 */     if (!Constants.isOSX) {
/* 176 */       MenuFactory.addExitMenuItem(fileMenu);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addTransferMenu(Shell parent)
/*     */   {
/* 186 */     MenuFactory.createTransfersMenuItem(this.menuBar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void addViewMenu(Shell parent)
/*     */   {
/*     */     try
/*     */     {
/* 195 */       MenuItem viewItem = MenuFactory.createViewMenuItem(this.menuBar);
/* 196 */       final Menu viewMenu = viewItem.getMenu();
/*     */       
/* 198 */       viewMenu.addListener(22, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 200 */           Utils.disposeSWTObjects(viewMenu.getItems());
/* 201 */           MainMenu.this.buildSimpleViewMenu(viewMenu);
/*     */         }
/*     */       });
/*     */     } catch (Exception e) {
/* 205 */       Debug.out("Error creating View Menu", e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void buildSimpleViewMenu(Menu viewMenu) {
/*     */     try {
/* 211 */       boolean enabled = COConfigurationManager.getBooleanParameter("Beta Programme Enabled");
/* 212 */       if (enabled) {
/* 213 */         MenuFactory.addMenuItem(viewMenu, 32, "MainWindow.menu.view.beta", new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 216 */             MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 217 */             MdiEntry entry = mdi.createEntryFromSkinRef(null, "BetaProgramme", "main.area.beta", "{Sidebar.beta.title}", null, null, true, "");
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 222 */             entry.setImageLeftID("image.sidebar.beta");
/* 223 */             mdi.showEntry(entry);
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 228 */       MenuFactory.addMyTorrentsMenuItem(viewMenu);
/* 229 */       MenuFactory.addMyTrackerMenuItem(viewMenu);
/* 230 */       MenuFactory.addMySharesMenuItem(viewMenu);
/* 231 */       MenuFactory.addViewToolbarMenuItem(viewMenu);
/* 232 */       MenuFactory.addTransferBarToMenu(viewMenu);
/* 233 */       MenuFactory.addAllPeersMenuItem(viewMenu);
/* 234 */       MenuFactory.addClientStatsMenuItem(viewMenu);
/* 235 */       MenuFactory.addPairingMenuItem(viewMenu);
/*     */       
/*     */ 
/* 238 */       MenuFactory.addSubscriptionMenuItem(viewMenu);
/*     */       
/* 240 */       if ((PluginsMenuHelper.getInstance().buildViewMenu(viewMenu, viewMenu.getShell())) && (Constants.isOSX)) {
/* 241 */         MenuFactory.addSeparatorMenuItem(viewMenu);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 248 */       if (Constants.isOSX) {
/* 249 */         MenuFactory.addConsoleMenuItem(viewMenu);
/* 250 */         MenuFactory.addStatisticsMenuItem(viewMenu);
/* 251 */         MenuFactory.addSpeedLimitsToMenu(viewMenu);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 255 */       Debug.out("Error creating View Menu", e);
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
/*     */   private void addTorrentMenu(Shell parent)
/*     */   {
/* 269 */     if (COConfigurationManager.getBooleanParameter("show_torrents_menu")) {
/* 270 */       MenuFactory.createTorrentMenuItem(this.menuBar);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addToolsMenu(Shell parent)
/*     */   {
/* 280 */     MenuItem item = MenuFactory.createToolsMenuItem(this.menuBar);
/* 281 */     Menu toolsMenu = item.getMenu();
/*     */     
/* 283 */     MenuFactory.addBlockedIPsMenuItem(toolsMenu);
/* 284 */     MenuFactory.addConsoleMenuItem(toolsMenu);
/* 285 */     MenuFactory.addStatisticsMenuItem(toolsMenu);
/* 286 */     MenuFactory.addSpeedLimitsToMenu(toolsMenu);
/* 287 */     MenuFactory.addNatTestMenuItem(toolsMenu);
/* 288 */     MenuFactory.addSpeedTestMenuItem(toolsMenu);
/*     */     
/* 290 */     MenuFactory.addSeparatorMenuItem(toolsMenu);
/* 291 */     MenuFactory.addConfigWizardMenuItem(toolsMenu);
/* 292 */     MenuFactory.addOptionsMenuItem(toolsMenu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addPluginsMenu(Shell parent)
/*     */   {
/* 304 */     MenuFactory.createPluginsMenuItem(this.menuBar, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addV2HelpMenu(Shell parent)
/*     */   {
/* 312 */     MenuItem helpItem = MenuFactory.createHelpMenuItem(this.menuBar);
/*     */     
/* 314 */     Menu helpMenu = helpItem.getMenu();
/*     */     
/* 316 */     if (!Constants.isOSX) {
/* 317 */       MenuFactory.addAboutMenuItem(helpMenu);
/* 318 */       MenuFactory.addSeparatorMenuItem(helpMenu);
/*     */     }
/*     */     
/* 321 */     MenuFactory.addHealthMenuItem(helpMenu);
/* 322 */     MenuFactory.addReleaseNotesMenuItem(helpMenu);
/* 323 */     MenuFactory.addWhatsNewMenuItem(helpMenu);
/*     */     
/* 325 */     MenuFactory.addWikiMenuItem(helpMenu);
/* 326 */     MenuFactory.addGetPluginsMenuItem(helpMenu);
/*     */     
/* 328 */     MenuFactory.addSeparatorMenuItem(helpMenu);
/*     */     
/* 330 */     if (!SystemProperties.isJavaWebStartInstance()) {
/* 331 */       MenuFactory.addCheckUpdateMenuItem(helpMenu);
/* 332 */       MenuFactory.addBetaMenuItem(helpMenu);
/* 333 */       MenuFactory.addVoteMenuItem(helpMenu);
/*     */     }
/* 335 */     MenuFactory.addDonationMenuItem(helpMenu);
/*     */     
/* 337 */     MenuFactory.addSeparatorMenuItem(helpMenu);
/* 338 */     MenuFactory.addAdvancedHelpMenuItem(helpMenu);
/* 339 */     MenuFactory.addDebugHelpMenuItem(helpMenu);
/*     */   }
/*     */   
/*     */   public Menu getMenu(String id)
/*     */   {
/* 344 */     if ("menu.bar".equals(id)) {
/* 345 */       return this.menuBar;
/*     */     }
/* 347 */     return MenuFactory.findMenu(this.menuBar, id);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/MainMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */