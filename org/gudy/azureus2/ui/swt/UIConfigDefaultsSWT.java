/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UIConfigDefaultsSWT
/*     */ {
/*     */   public static void initialize()
/*     */   {
/*  38 */     ConfigurationDefaults def = ConfigurationDefaults.getInstance();
/*  39 */     def.addParameter("useCustomTab", true);
/*  40 */     def.addParameter("GUI Refresh", 500);
/*  41 */     def.addParameter("Graphics Update", 4);
/*  42 */     def.addParameter("ReOrder Delay", 4);
/*  43 */     def.addParameter("Refresh When Inactive", 2);
/*  44 */     def.addParameter("Send Version Info", true);
/*  45 */     def.addParameter("Show Download Basket", false);
/*  46 */     def.addParameter("config.style.refreshMT", 0);
/*  47 */     def.addParameter("Open Details", false);
/*  48 */     def.addParameter("Open Seeding Details", false);
/*  49 */     def.addParameter("IconBar.enabled", true);
/*     */     
/*  51 */     def.addParameter("DefaultDir.BestGuess", true);
/*  52 */     def.addParameter("DefaultDir.BestGuess.Default", "");
/*  53 */     def.addParameter("DefaultDir.AutoUpdate", true);
/*  54 */     def.addParameter("DefaultDir.AutoSave.AutoRename", true);
/*  55 */     def.addParameter("GUI_SWT_bFancyTab", true);
/*  56 */     def.addParameter("Colors.progressBar.override", false);
/*  57 */     def.addParameter("GUI_SWT_DisableAlertSliding", false);
/*  58 */     def.addParameter("NameColumn.showProgramIcon", !Constants.isWindowsVista);
/*  59 */     def.addParameter("RankColumn.showUpDownIcon.big", true);
/*  60 */     def.addParameter("RankColumn.showUpDownIcon.small", false);
/*  61 */     def.addParameter("SeedsColumn.showNetworkIcon", true);
/*  62 */     def.addParameter("PeersColumn.showNetworkIcon", true);
/*     */     
/*  64 */     def.addParameter("DND Always In Incomplete", false);
/*     */     
/*  66 */     def.addParameter("Message Popup Autoclose in Seconds", 15);
/*     */     
/*     */ 
/*     */ 
/*  70 */     def.addParameter("Reduce Auto Activate Window", false);
/*     */     
/*  72 */     def.addParameter("MyTorrents.SplitAt", 30);
/*     */     
/*  74 */     def.addParameter("Wizard Completed", false);
/*  75 */     def.addParameter("SpeedTest Completed", false);
/*  76 */     def.addParameter("Color Scheme.red", 0);
/*  77 */     def.addParameter("Color Scheme.green", 128);
/*  78 */     def.addParameter("Color Scheme.blue", 255);
/*  79 */     def.addParameter("Show Splash", true);
/*  80 */     def.addParameter("window.maximized", true);
/*  81 */     def.addParameter("window.rectangle", "");
/*  82 */     def.addParameter("Start Minimized", false);
/*  83 */     def.addParameter("Open Transfer Bar On Start", false);
/*  84 */     def.addParameter("Transfer Bar Show Icon Area", true);
/*     */     
/*  86 */     def.addParameter("Stats Graph Dividers", false);
/*     */     
/*  88 */     def.addParameter("Open Bar Incomplete", false);
/*  89 */     def.addParameter("Open Bar Complete", false);
/*     */     
/*  91 */     def.addParameter("Close To Tray", true);
/*  92 */     def.addParameter("Minimize To Tray", false);
/*     */     
/*  94 */     def.addParameter("Status Area Show SR", true);
/*  95 */     def.addParameter("Status Area Show NAT", true);
/*  96 */     def.addParameter("Status Area Show DDB", true);
/*  97 */     def.addParameter("Status Area Show IPF", true);
/*  98 */     def.addParameter("Status Area Show RIP", true);
/*     */     
/* 100 */     def.addParameter("status.rategraphs", Utils.getUserMode() > 0);
/*     */     
/* 102 */     def.addParameter("GUI_SWT_share_count_at_close", 0);
/*     */     
/* 104 */     def.addParameter("GUI_SWT_bOldSpeedMenu", false);
/*     */     
/* 106 */     def.addParameter("ui.toolbar.uiswitcher", false);
/* 107 */     def.addParameter("ui.systray.tooltip.enable", false);
/* 108 */     def.addParameter("ui.systray.tooltip.next.eta.enable", false);
/*     */     
/* 110 */     def.addParameter("Remember transfer bar location", true);
/*     */     
/* 112 */     if (COConfigurationManager.getBooleanParameter("Open Bar"))
/*     */     {
/* 114 */       COConfigurationManager.setParameter("Open Bar Incomplete", true);
/* 115 */       COConfigurationManager.setParameter("Open Bar Complete", true);
/*     */       
/* 117 */       COConfigurationManager.setParameter("Open Bar", false);
/*     */     }
/*     */     
/* 120 */     def.addParameter("suppress_file_download_dialog", false);
/* 121 */     def.addParameter("Suppress Sharing Dialog", false);
/* 122 */     def.addParameter("auto_remove_inactive_items", false);
/* 123 */     def.addParameter("show_torrents_menu", true);
/* 124 */     def.addParameter("mainwindow.search.history.enabled", true);
/*     */     
/* 126 */     def.addParameter("swt.forceMozilla", false);
/* 127 */     def.addParameter("swt.xulRunner.path", "");
/*     */     
/* 129 */     String xulPath = COConfigurationManager.getStringParameter("swt.xulRunner.path");
/* 130 */     if (!xulPath.equals("")) {
/* 131 */       System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulPath);
/*     */     }
/*     */     
/* 134 */     def.addParameter("MyTorrentsView.table.style", 0);
/*     */     
/* 136 */     def.addParameter("v3.topbar.height", 60);
/* 137 */     def.addParameter("v3.topbar.show.plugin", false);
/* 138 */     def.addParameter("pluginbar.visible", false);
/* 139 */     def.addParameter("ui.toolbar.uiswitcher", false);
/* 140 */     def.addParameter("Table.extendedErase", false);
/* 141 */     def.addParameter("Table.useTree", false);
/*     */     
/* 143 */     if ("az2".equalsIgnoreCase(COConfigurationManager.getStringParameter("ui", "az3"))) {
/* 144 */       def.addParameter("v3.Show Welcome", false);
/*     */       
/* 146 */       def.addParameter("list.dm.dblclick", "1");
/* 147 */       def.addParameter("Library.viewmode", 1);
/* 148 */       def.addParameter("LibraryDLDL.viewmode", 1);
/* 149 */       def.addParameter("LibraryCD.viewmode", 1);
/*     */     }
/*     */     
/*     */ 
/* 153 */     def.addParameter("browser.external.id", "system");
/* 154 */     def.addParameter("browser.external.search", false);
/* 155 */     def.addParameter("browser.internal.disable", false);
/* 156 */     def.addParameter("browser.internal.proxy.id", "none");
/*     */     
/* 158 */     def.addParameter("Bar Transparency", 0);
/*     */     
/* 160 */     def.addParameter("Low Resource Silent Update Restart Enabled", true);
/*     */     
/* 162 */     def.addParameter("Library.ShowCatButtons", true);
/* 163 */     def.addParameter("Library.ShowTagButtons", false);
/* 164 */     def.addParameter("Library.ShowTagButtons.CompOnly", false);
/* 165 */     def.addParameter("open.torrent.window.rename.on.tlf.change", true);
/*     */     
/* 167 */     def.addParameter("Library.LaunchWebsiteInBrowser", true);
/* 168 */     def.addParameter("Library.LaunchWebsiteInBrowserAnon", false);
/* 169 */     def.addParameter("Library.LaunchWebsiteInBrowserDirList", false);
/*     */     
/* 171 */     def.addParameter("ui.scaled.graphics.binary.based", false);
/*     */     
/* 173 */     def.addParameter("Search Subs Row Height", 20);
/* 174 */     def.addParameter("Search View Is Web View", true);
/* 175 */     def.addParameter("Search View Switch Hidden", false);
/*     */     
/* 177 */     def.addParameter("tag.add.customize.default.checked", true);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/UIConfigDefaultsSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */