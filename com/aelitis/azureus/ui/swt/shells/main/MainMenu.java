/*     */ package com.aelitis.azureus.ui.swt.shells.main;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import com.aelitis.azureus.ui.swt.feature.FeatureManagerUI;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinUtils;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_PlusFTUX;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinViewManager;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.ToolBarView;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.sidebar.SideBar;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.ContentNetworkUtils;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarItem;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
/*     */ import org.gudy.azureus2.ui.swt.KeyBindings;
/*     */ import org.gudy.azureus2.ui.swt.KeyBindings.KeyBindingInfo;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainMenu;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMenuConstants;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.MenuFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.PluginsMenuHelper;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UIToolBarManagerImpl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   implements IMainMenu, IMenuConstants
/*     */ {
/*     */   private static final String PREFIX_V2 = "MainWindow.menu";
/*     */   private static final String PREFIX_V3 = "v3.MainWindow.menu";
/*     */   private Menu menuBar;
/*     */   
/*     */   public MainMenu(SWTSkin skin, Shell shell)
/*     */   {
/*  74 */     if (null == skin) {
/*  75 */       System.err.println("MainMenu: The parameter [SWTSkin skin] can not be null");
/*  76 */       return;
/*     */     }
/*     */     
/*  79 */     buildMenu(shell);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void buildMenu(Shell parent)
/*     */   {
/*  86 */     this.menuBar = new Menu(parent, 2);
/*  87 */     parent.setMenuBar(this.menuBar);
/*     */     
/*  89 */     addFileMenu();
/*     */     
/*  91 */     addSimpleViewMenu();
/*     */     
/*  93 */     addCommunityMenu();
/*     */     
/*  95 */     addToolsMenu();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 100 */     if (COConfigurationManager.getBooleanParameter("show_torrents_menu")) {
/* 101 */       addTorrentMenu();
/*     */     }
/*     */     
/* 104 */     if (!Constants.isWindows) {
/* 105 */       addWindowMenu();
/*     */     }
/*     */     
/*     */ 
/* 109 */     if (Constants.isCVSVersion()) {
/* 110 */       final Menu menuDebug = org.gudy.azureus2.ui.swt.mainwindow.DebugMenuHelper.createDebugMenuItem(this.menuBar);
/* 111 */       menuDebug.addMenuListener(new MenuListener()
/*     */       {
/*     */         public void menuShown(MenuEvent e) {
/* 114 */           MenuItem[] items = menuDebug.getItems();
/* 115 */           Utils.disposeSWTObjects(items);
/*     */           
/* 117 */           DebugMenuHelper.createDebugMenuItem(menuDebug);
/* 118 */           MenuFactory.addSeparatorMenuItem(menuDebug);
/* 119 */           MenuItem menuItem = new MenuItem(menuDebug, 8);
/* 120 */           menuItem.setText("Log Views");
/* 121 */           menuItem.setEnabled(false);
/* 122 */           PluginsMenuHelper.getInstance().buildPluginLogsMenu(menuDebug);
/*     */         }
/*     */         
/*     */ 
/*     */         public void menuHidden(MenuEvent e) {}
/*     */       });
/*     */     }
/*     */     
/* 130 */     addV3HelpMenu();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */     MenuFactory.updateEnabledStates(this.menuBar);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void addFileMenu()
/*     */   {
/* 143 */     MenuItem fileItem = MenuFactory.createFileMenuItem(this.menuBar);
/* 144 */     final Menu fileMenu = fileItem.getMenu();
/* 145 */     builFileMenu(fileMenu);
/*     */     
/* 147 */     fileMenu.addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 149 */         MenuItem[] menuItems = fileMenu.getItems();
/* 150 */         for (int i = 0; i < menuItems.length; i++) {
/* 151 */           menuItems[i].dispose();
/*     */         }
/*     */         
/* 154 */         MainMenu.this.builFileMenu(fileMenu);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void builFileMenu(Menu fileMenu)
/*     */   {
/* 165 */     MenuItem openMenuItem = MenuFactory.createOpenMenuItem(fileMenu);
/* 166 */     Menu openSubMenu = openMenuItem.getMenu();
/* 167 */     MenuFactory.addOpenTorrentMenuItem(openSubMenu);
/* 168 */     MenuFactory.addOpenURIMenuItem(openSubMenu);
/* 169 */     MenuFactory.addOpenTorrentForTrackingMenuItem(openSubMenu);
/* 170 */     MenuFactory.addOpenVuzeFileMenuItem(openSubMenu);
/*     */     
/* 172 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/* 174 */     if (userMode > 0) {
/* 175 */       Menu shareSubMenu = MenuFactory.createShareMenuItem(fileMenu).getMenu();
/* 176 */       MenuFactory.addShareFileMenuItem(shareSubMenu);
/* 177 */       MenuFactory.addShareFolderMenuItem(shareSubMenu);
/* 178 */       MenuFactory.addShareFolderContentMenuItem(shareSubMenu);
/* 179 */       MenuFactory.addShareFolderContentRecursiveMenuItem(shareSubMenu);
/*     */     }
/*     */     
/* 182 */     MenuFactory.addCreateMenuItem(fileMenu);
/*     */     
/* 184 */     if (FeatureManagerUI.enabled) {
/* 185 */       MenuFactory.addSeparatorMenuItem(fileMenu);
/* 186 */       MenuFactory.addMenuItem(fileMenu, "menu.plus", new Listener() {
/*     */         public void handleEvent(Event event) {
/* 188 */           SBC_PlusFTUX.setSourceRef("menu-file");
/*     */           
/* 190 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 191 */           mdi.showEntryByID("Plus");
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 196 */     MenuFactory.addSeparatorMenuItem(fileMenu);
/* 197 */     MenuFactory.addCloseWindowMenuItem(fileMenu);
/* 198 */     MenuFactory.addCloseDetailsMenuItem(fileMenu);
/* 199 */     MenuFactory.addCloseDownloadBarsToMenu(fileMenu);
/*     */     
/* 201 */     MenuFactory.addSeparatorMenuItem(fileMenu);
/* 202 */     MenuFactory.createTransfersMenuItem(fileMenu);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 207 */     if (!Utils.isCarbon) {
/* 208 */       MenuFactory.addSeparatorMenuItem(fileMenu);
/* 209 */       MenuFactory.addRestartMenuItem(fileMenu);
/*     */     }
/* 211 */     if (!Constants.isOSX) {
/* 212 */       MenuFactory.addExitMenuItem(fileMenu);
/*     */     }
/*     */   }
/*     */   
/*     */   private void addSimpleViewMenu() {
/*     */     try {
/* 218 */       MenuItem viewItem = MenuFactory.createViewMenuItem(this.menuBar);
/* 219 */       final Menu viewMenu = viewItem.getMenu();
/*     */       
/* 221 */       viewMenu.addListener(22, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 223 */           Utils.disposeSWTObjects(viewMenu.getItems());
/* 224 */           MainMenu.this.buildSimpleViewMenu(viewMenu, -1);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/* 229 */       });
/* 230 */       final KeyBindings.KeyBindingInfo binding_info = KeyBindings.getKeyBindingInfo("v3.MainWindow.menu.view.pluginbar");
/*     */       
/* 232 */       if (binding_info != null) {
/* 233 */         Display.getDefault().addFilter(1, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 235 */             if (event.keyCode == binding_info.accelerator) {
/* 236 */               Utils.disposeSWTObjects(viewMenu.getItems());
/* 237 */               MainMenu.this.buildSimpleViewMenu(viewMenu, event.keyCode);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     } catch (Exception e) {
/* 243 */       Debug.out("Error creating View Menu", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void buildSimpleViewMenu(final Menu viewMenu, int accelerator)
/*     */   {
/*     */     try
/*     */     {
/* 255 */       MenuFactory.addMenuItem(viewMenu, 32, "v3.MainWindow.menu.view.sidebar", new Listener()
/*     */       {
/*     */         public void handleEvent(Event event) {
/* 258 */           SideBar sidebar = (SideBar)SkinViewManager.getByClass(SideBar.class);
/* 259 */           if (sidebar != null) {
/* 260 */             sidebar.flipSideBarVisibility();
/*     */           }
/*     */         }
/*     */       });
/*     */       
/* 265 */       if (COConfigurationManager.getIntParameter("User Mode") > 1)
/*     */       {
/* 267 */         SWTSkin skin = SWTSkinFactory.getInstance();
/*     */         
/* 269 */         SWTSkinObject plugin_bar = skin.getSkinObject("pluginbar");
/*     */         
/* 271 */         if (plugin_bar != null)
/*     */         {
/* 273 */           MenuItem mi = createViewMenuItem(skin, viewMenu, "v3.MainWindow.menu.view.pluginbar", "pluginbar.visible", "pluginbar", true, -1);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 279 */           if ((accelerator != -1) && (mi.getAccelerator() == accelerator))
/*     */           {
/* 281 */             Listener[] listeners = mi.getListeners(13);
/*     */             
/* 283 */             for (Listener l : listeners) {
/*     */               try
/*     */               {
/* 286 */                 l.handleEvent(null);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 297 */       MenuItem itemStatusBar = MenuFactory.createTopLevelMenuItem(viewMenu, "v3.MainWindow.menu.view.statusbar");
/*     */       
/* 299 */       itemStatusBar.setText(itemStatusBar.getText());
/* 300 */       Menu menuStatusBar = itemStatusBar.getMenu();
/*     */       
/* 302 */       String[] statusAreaLangs = { "ConfigView.section.style.status.show_sr", "ConfigView.section.style.status.show_nat", "ConfigView.section.style.status.show_ddb", "ConfigView.section.style.status.show_ipf" };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 308 */       String[] statusAreaConfig = { "Status Area Show SR", "Status Area Show NAT", "Status Area Show DDB", "Status Area Show IPF" };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 315 */       for (int i = 0; i < statusAreaConfig.length; i++) {
/* 316 */         final String configID = statusAreaConfig[i];
/* 317 */         String langID = statusAreaLangs[i];
/*     */         
/* 319 */         final MenuItem item = new MenuItem(menuStatusBar, 32);
/* 320 */         Messages.setLanguageText(item, langID);
/* 321 */         item.addListener(13, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 323 */             COConfigurationManager.setParameter(configID, !COConfigurationManager.getBooleanParameter(configID));
/*     */           }
/*     */           
/* 326 */         });
/* 327 */         menuStatusBar.addListener(22, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 329 */             item.setSelection(COConfigurationManager.getBooleanParameter(configID));
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 336 */       if (Constants.isWindows) {
/* 337 */         MenuFactory.addSeparatorMenuItem(viewMenu);
/*     */       }
/*     */       
/* 340 */       boolean needsSep = false;
/* 341 */       boolean enabled = COConfigurationManager.getBooleanParameter("Beta Programme Enabled");
/* 342 */       if (enabled) {
/* 343 */         MenuFactory.addMenuItem(viewMenu, 32, "MainWindow.menu.view.beta", new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 346 */             MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 347 */             if (mdi != null) {
/* 348 */               mdi.showEntryByID("BetaProgramme");
/*     */             }
/*     */           }
/* 351 */         });
/* 352 */         needsSep = true;
/*     */       }
/*     */       
/* 355 */       if ((Constants.isWindows) && (FeatureAvailability.isGamesEnabled())) {
/* 356 */         MenuFactory.addMenuItem(viewMenu, "v3.MainWindow.menu.games", new Listener() {
/*     */           public void handleEvent(Event event) {
/* 358 */             MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 359 */             mdi.showEntryByID("Games");
/*     */           }
/* 361 */         });
/* 362 */         needsSep = true;
/*     */       }
/*     */       
/*     */ 
/* 366 */       if (needsSep) {
/* 367 */         MenuFactory.addSeparatorMenuItem(viewMenu);
/*     */       }
/*     */       
/* 370 */       needsSep = PluginsMenuHelper.getInstance().buildViewMenu(viewMenu, viewMenu.getShell());
/*     */       
/* 372 */       if (COConfigurationManager.getBooleanParameter("Library.EnableSimpleView"))
/*     */       {
/* 374 */         if (needsSep) {
/* 375 */           MenuFactory.addSeparatorMenuItem(viewMenu);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 382 */         int simple_advanced_menu_type = Constants.isLinux ? 32 : 16;
/*     */         
/* 384 */         MenuFactory.addMenuItem(viewMenu, simple_advanced_menu_type, "v3.MainWindow.menu.view.asSimpleList", new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 387 */             UIToolBarManager tb = UIToolBarManagerImpl.getInstance();
/* 388 */             if (tb != null) {
/* 389 */               UIToolBarItem item = tb.getToolBarItem("modeBig");
/* 390 */               if (item != null) {
/* 391 */                 item.triggerToolBarItem(0L, SelectedContentManager.convertSelectedContentToObject(null));
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           }
/* 397 */         });
/* 398 */         MenuFactory.addMenuItem(viewMenu, simple_advanced_menu_type, "v3.MainWindow.menu.view.asAdvancedList", new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 401 */             UIToolBarManager tb = UIToolBarManagerImpl.getInstance();
/* 402 */             if (tb != null) {
/* 403 */               UIToolBarItem item = tb.getToolBarItem("modeSmall");
/* 404 */               if (item != null) {
/* 405 */                 item.triggerToolBarItem(0L, SelectedContentManager.convertSelectedContentToObject(null));
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 414 */       viewMenu.addMenuListener(new MenuListener()
/*     */       {
/*     */         public void menuShown(MenuEvent e)
/*     */         {
/* 418 */           MenuItem sidebarMenuItem = MenuFactory.findMenuItem(viewMenu, "v3.MainWindow.menu.view.sidebar");
/*     */           
/* 420 */           if (sidebarMenuItem != null) {
/* 421 */             MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 422 */             if (mdi != null) {
/* 423 */               sidebarMenuItem.setSelection(mdi.isVisible());
/*     */             }
/*     */           }
/*     */           
/* 427 */           MenuItem itemShowText = MenuFactory.findMenuItem(viewMenu, "v3.MainWindow.menu.view.toolbartext");
/*     */           
/* 429 */           if (itemShowText != null) {
/* 430 */             ToolBarView tb = (ToolBarView)SkinViewManager.getByClass(ToolBarView.class);
/* 431 */             if (tb != null) {
/* 432 */               itemShowText.setSelection(tb.getShowText());
/*     */             }
/*     */           }
/*     */           
/* 436 */           if (COConfigurationManager.getBooleanParameter("Library.EnableSimpleView"))
/*     */           {
/* 438 */             MenuItem itemShowAsSimple = MenuFactory.findMenuItem(viewMenu, "v3.MainWindow.menu.view.asSimpleList");
/*     */             
/* 440 */             if (itemShowAsSimple != null) {
/* 441 */               UIToolBarManager tb = UIToolBarManagerImpl.getInstance();
/* 442 */               if (tb != null) {
/* 443 */                 UIToolBarItem item = tb.getToolBarItem("modeBig");
/* 444 */                 long state = item == null ? 0L : item.getState();
/* 445 */                 itemShowAsSimple.setEnabled((state & 1L) > 0L);
/* 446 */                 itemShowAsSimple.setSelection((state & 0x2) > 0L);
/*     */               }
/*     */             }
/* 449 */             MenuItem itemShowAsAdv = MenuFactory.findMenuItem(viewMenu, "v3.MainWindow.menu.view.asAdvancedList");
/*     */             
/* 451 */             if (itemShowAsAdv != null) {
/* 452 */               UIToolBarManager tb = UIToolBarManagerImpl.getInstance();
/* 453 */               if (tb != null) {
/* 454 */                 UIToolBarItem item = tb.getToolBarItem("modeSmall");
/* 455 */                 long state = item == null ? 0L : item.getState();
/* 456 */                 itemShowAsAdv.setEnabled((state & 1L) > 0L);
/* 457 */                 itemShowAsAdv.setSelection((state & 0x2) > 0L);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         public void menuHidden(MenuEvent e) {}
/*     */       });
/*     */     }
/*     */     catch (Exception e) {
/* 467 */       Debug.out("Error creating View Menu", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void addToolsMenu()
/*     */   {
/* 475 */     MenuItem toolsItem = MenuFactory.createToolsMenuItem(this.menuBar);
/* 476 */     Menu toolsMenu = toolsItem.getMenu();
/*     */     
/* 478 */     MenuFactory.addMyTrackerMenuItem(toolsMenu);
/* 479 */     MenuFactory.addMySharesMenuItem(toolsMenu);
/* 480 */     MenuFactory.addConsoleMenuItem(toolsMenu);
/* 481 */     MenuFactory.addStatisticsMenuItem(toolsMenu);
/* 482 */     MenuFactory.addSpeedLimitsToMenu(toolsMenu);
/*     */     
/* 484 */     MenuFactory.addTransferBarToMenu(toolsMenu);
/* 485 */     MenuFactory.addAllPeersMenuItem(toolsMenu);
/* 486 */     MenuFactory.addClientStatsMenuItem(toolsMenu);
/* 487 */     MenuFactory.addBlockedIPsMenuItem(toolsMenu);
/*     */     
/* 489 */     MenuFactory.addSeparatorMenuItem(toolsMenu);
/* 490 */     MenuFactory.createPluginsMenuItem(toolsMenu, true);
/*     */     
/* 492 */     MenuFactory.addPairingMenuItem(toolsMenu);
/*     */     
/* 494 */     MenuFactory.addOptionsMenuItem(toolsMenu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addV3HelpMenu()
/*     */   {
/* 502 */     MenuItem helpItem = MenuFactory.createHelpMenuItem(this.menuBar);
/* 503 */     Menu helpMenu = helpItem.getMenu();
/*     */     
/* 505 */     if (!Constants.isOSX)
/*     */     {
/*     */ 
/*     */ 
/* 509 */       MenuFactory.addAboutMenuItem(helpMenu);
/* 510 */       MenuFactory.addSeparatorMenuItem(helpMenu);
/*     */     }
/*     */     
/* 513 */     MenuFactory.addMenuItem(helpMenu, "v3.MainWindow.menu.getting_started", new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 516 */         MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 517 */         if (mdi != null) {
/* 518 */           mdi.showEntryByID("Welcome");
/*     */         }
/*     */         
/*     */       }
/* 522 */     });
/* 523 */     MenuFactory.addHelpSupportMenuItem(helpMenu, ContentNetworkUtils.getUrl(ConstantsVuze.getDefaultContentNetwork(), 16));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 528 */     MenuFactory.addHealthMenuItem(helpMenu);
/*     */     
/* 530 */     MenuFactory.addReleaseNotesMenuItem(helpMenu);
/*     */     
/* 532 */     if (!SystemProperties.isJavaWebStartInstance()) {
/* 533 */       MenuFactory.addSeparatorMenuItem(helpMenu);
/* 534 */       MenuFactory.addCheckUpdateMenuItem(helpMenu);
/* 535 */       MenuFactory.addBetaMenuItem(helpMenu);
/* 536 */       MenuFactory.addVoteMenuItem(helpMenu);
/*     */     }
/*     */     
/* 539 */     if (FeatureManagerUI.enabled) {
/* 540 */       MenuFactory.addMenuItem(helpMenu, "menu.register", new Listener() {
/*     */         public void handleEvent(Event event) {
/* 542 */           FeatureManagerUI.openLicenceEntryWindow(false, null);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 547 */     MenuFactory.addDonationMenuItem(helpMenu);
/*     */     
/* 549 */     MenuFactory.addSeparatorMenuItem(helpMenu);
/* 550 */     MenuFactory.addConfigWizardMenuItem(helpMenu);
/* 551 */     MenuFactory.addNatTestMenuItem(helpMenu);
/* 552 */     MenuFactory.addNetStatusMenuItem(helpMenu);
/* 553 */     MenuFactory.addSpeedTestMenuItem(helpMenu);
/* 554 */     MenuFactory.addAdvancedHelpMenuItem(helpMenu);
/*     */     
/* 556 */     MenuFactory.addSeparatorMenuItem(helpMenu);
/* 557 */     MenuFactory.addDebugHelpMenuItem(helpMenu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addWindowMenu()
/*     */   {
/* 565 */     MenuItem menu_window = MenuFactory.createWindowMenuItem(this.menuBar);
/* 566 */     Menu windowMenu = menu_window.getMenu();
/*     */     
/* 568 */     MenuFactory.addMinimizeWindowMenuItem(windowMenu);
/* 569 */     MenuFactory.addZoomWindowMenuItem(windowMenu);
/* 570 */     MenuFactory.addSeparatorMenuItem(windowMenu);
/* 571 */     MenuFactory.addBringAllToFrontMenuItem(windowMenu);
/*     */     
/* 573 */     MenuFactory.addSeparatorMenuItem(windowMenu);
/* 574 */     MenuFactory.appendWindowMenuItems(windowMenu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void addTorrentMenu()
/*     */   {
/* 581 */     MenuFactory.createTorrentMenuItem(this.menuBar);
/*     */   }
/*     */   
/*     */   public Menu getMenu(String id) {
/* 585 */     if ("menu.bar".equals(id)) {
/* 586 */       return this.menuBar;
/*     */     }
/* 588 */     return MenuFactory.findMenu(this.menuBar, id);
/*     */   }
/*     */   
/*     */   private void addCommunityMenu() {
/* 592 */     MenuItem item = MenuFactory.createTopLevelMenuItem(this.menuBar, "MainWindow.menu.community");
/*     */     
/* 594 */     final Menu communityMenu = item.getMenu();
/*     */     
/* 596 */     communityMenu.addListener(22, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 603 */         Utils.disposeSWTObjects(communityMenu.getItems());
/*     */         
/* 605 */         MenuFactory.addMenuItem(communityMenu, "MainWindow.menu.community.forums", new Listener()
/*     */         {
/*     */           public void handleEvent(Event e) {
/* 608 */             Utils.launch(ContentNetworkUtils.getUrl(ConstantsVuze.getDefaultContentNetwork(), 20));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 613 */         });
/* 614 */         MenuFactory.addMenuItem(communityMenu, "MainWindow.menu.community.wiki", new Listener()
/*     */         {
/*     */           public void handleEvent(Event e) {
/* 617 */             Utils.launch(ContentNetworkUtils.getUrl(ConstantsVuze.getDefaultContentNetwork(), 21));
/*     */ 
/*     */           }
/*     */           
/*     */ 
/* 622 */         });
/* 623 */         MenuBuildUtils.addChatMenu(communityMenu, "MainWindow.menu.community.chat", "General: Help");
/*     */         
/* 625 */         MenuFactory.addMenuItem(communityMenu, "MainWindow.menu.community.blog", new Listener()
/*     */         {
/*     */           public void handleEvent(Event e) {
/* 628 */             Utils.launch(ContentNetworkUtils.getUrl(ConstantsVuze.getDefaultContentNetwork(), 19));
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public Menu getMenuBar()
/*     */   {
/* 645 */     return this.menuBar;
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
/*     */   public static MenuItem createViewMenuItem(SWTSkin skin, Menu viewMenu, String textID, final String configID, final String viewID, final boolean fast, int menuIndex)
/*     */   {
/* 658 */     if (!ConfigurationDefaults.getInstance().doesParameterDefaultExist(configID)) {
/* 659 */       COConfigurationManager.setBooleanDefault(configID, true);
/*     */     }
/*     */     
/* 662 */     MenuItem item = MenuFactory.addMenuItem(viewMenu, 32, menuIndex, textID, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 665 */         SWTSkinObject skinObject = this.val$skin.getSkinObject(viewID);
/* 666 */         if (skinObject != null) {
/* 667 */           boolean newVisibility = !skinObject.isVisible();
/*     */           
/* 669 */           SWTSkinUtils.setVisibility(this.val$skin, configID, viewID, newVisibility, true, fast);
/*     */         }
/*     */         
/*     */       }
/* 673 */     });
/* 674 */     SWTSkinUtils.setVisibility(skin, configID, viewID, COConfigurationManager.getBooleanParameter(configID), false, true);
/*     */     
/*     */ 
/* 677 */     MenuItem itemViewPluginBar = item;
/* 678 */     final ParameterListener listener = new ParameterListener() {
/*     */       public void parameterChanged(String parameterName) {
/* 680 */         this.val$itemViewPluginBar.setSelection(COConfigurationManager.getBooleanParameter(parameterName));
/*     */       }
/*     */       
/* 683 */     };
/* 684 */     COConfigurationManager.addAndFireParameterListener(configID, listener);
/* 685 */     item.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 687 */         COConfigurationManager.removeParameterListener(this.val$configID, listener);
/*     */       }
/*     */       
/* 690 */     });
/* 691 */     return item;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setVisibility(SWTSkin skin, String configID, String viewID, boolean visible)
/*     */   {
/* 697 */     SWTSkinUtils.setVisibility(skin, configID, viewID, visible, true, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public static void setVisibility(SWTSkin skin, String configID, String viewID, boolean visible, boolean save)
/*     */   {
/* 703 */     SWTSkinUtils.setVisibility(skin, configID, viewID, visible, save, false);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/main/MainMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */