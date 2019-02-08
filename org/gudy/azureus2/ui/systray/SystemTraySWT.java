/*     */ package org.gudy.azureus2.ui.systray;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagDownload;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatableAlways;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Tray;
/*     */ import org.eclipse.swt.widgets.TrayItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.internat.MessageText.MessageTextListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*     */ import org.gudy.azureus2.ui.swt.Alerts;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SelectableSpeedMenu;
/*     */ import org.gudy.azureus2.ui.swt.views.utils.ManagerUtils;
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
/*     */ public class SystemTraySWT
/*     */   implements UIUpdatableAlways, MessageText.MessageTextListener
/*     */ {
/*     */   private static SystemTraySWT singleton;
/*     */   
/*     */   public static synchronized SystemTraySWT getTray()
/*     */   {
/*  78 */     if (singleton == null)
/*     */     {
/*  80 */       singleton = new SystemTraySWT();
/*     */     }
/*     */     
/*  83 */     return singleton;
/*     */   }
/*     */   
/*  86 */   protected static AzureusCore core = null;
/*     */   
/*     */   Display display;
/*     */   
/*     */   UIFunctionsSWT uiFunctions;
/*     */   
/*     */   Tray tray;
/*     */   
/*     */   TrayItem trayItem;
/*     */   
/*     */   Menu menu;
/*     */   
/*     */   Image imgAzureus;
/*     */   
/*     */   Image imgAzureusGray;
/*     */   Image imgAzureusWhite;
/* 102 */   protected GlobalManager gm = null;
/*     */   
/*     */   private String seedingKeyVal;
/*     */   
/*     */   private String downloadingKeyVal;
/*     */   private String etaKeyVal;
/*     */   private String dlAbbrKeyVal;
/*     */   private String ulAbbrKeyVal;
/*     */   private String alertsKeyVal;
/* 111 */   long interval = 0L;
/*     */   protected boolean enableTooltip;
/*     */   protected boolean enableTooltipNextETA;
/*     */   
/*     */   private SystemTraySWT()
/*     */   {
/* 117 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 119 */         SystemTraySWT.core = core;
/* 120 */         SystemTraySWT.this.gm = core.getGlobalManager();
/*     */       }
/*     */       
/* 123 */     });
/* 124 */     COConfigurationManager.addAndFireParameterListener("ui.systray.tooltip.enable", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/* 127 */         SystemTraySWT.this.enableTooltip = COConfigurationManager.getBooleanParameter(parameterName);
/* 128 */         if (SystemTraySWT.this.enableTooltip) {
/* 129 */           MessageText.addAndFireListener(SystemTraySWT.this);
/* 130 */           SystemTraySWT.this.interval = 0L;
/*     */         } else {
/* 132 */           MessageText.removeListener(SystemTraySWT.this);
/* 133 */           if ((SystemTraySWT.this.trayItem != null) && (!SystemTraySWT.this.trayItem.isDisposed())) {
/* 134 */             SystemTraySWT.this.trayItem.setToolTipText(null);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 139 */     });
/* 140 */     COConfigurationManager.addAndFireParameterListener("ui.systray.tooltip.next.eta.enable", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/* 144 */         SystemTraySWT.this.enableTooltipNextETA = COConfigurationManager.getBooleanParameter(parameterName);
/* 145 */         SystemTraySWT.this.interval = 0L;
/*     */       }
/*     */       
/* 148 */     });
/* 149 */     this.uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 150 */     this.display = SWTThread.getInstance().getDisplay();
/*     */     
/* 152 */     this.tray = this.display.getSystemTray();
/* 153 */     this.trayItem = new TrayItem(this.tray, 0);
/*     */     
/* 155 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 156 */     if (Constants.isOSX) {
/* 157 */       this.imgAzureusGray = imageLoader.getImage("azureus_grey");
/* 158 */       this.imgAzureusWhite = imageLoader.getImage("azureus_white");
/* 159 */       this.trayItem.setImage(this.imgAzureusGray);
/*     */     } else {
/* 161 */       this.imgAzureus = imageLoader.getImage("azureus");
/* 162 */       this.trayItem.setImage(this.imgAzureus);
/*     */     }
/*     */     
/* 165 */     this.trayItem.setVisible(true);
/*     */     
/* 167 */     this.menu = new Menu(this.uiFunctions.getMainShell(), 8);
/* 168 */     this.menu.addMenuListener(new MenuListener() {
/*     */       public void menuShown(MenuEvent _menu) {}
/*     */       
/*     */       public void menuHidden(MenuEvent _menu) {
/* 172 */         if (Constants.isOSX) {
/* 173 */           SystemTraySWT.this.trayItem.setImage(SystemTraySWT.this.imgAzureusGray);
/*     */         }
/*     */         
/*     */       }
/* 177 */     });
/* 178 */     MenuBuildUtils.addMaintenanceListenerForMenu(this.menu, new MenuBuildUtils.MenuBuilder() {
/*     */       public void buildMenu(Menu menu, MenuEvent menuEvent) {
/* 180 */         SystemTraySWT.this.fillMenu(menu);
/*     */       }
/*     */       
/* 183 */     });
/* 184 */     this.trayItem.addListener(14, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 186 */         SystemTraySWT.this.showMainWindow();
/*     */       }
/*     */       
/*     */ 
/* 190 */     });
/* 191 */     this.trayItem.addListener(13, new Listener() {
/* 192 */       long lastTime = 0L;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 200 */         if (Constants.isWindows) {
/* 201 */           long now = SystemTime.getCurrentTime();
/* 202 */           if (now - this.lastTime < 200L) {
/* 203 */             SystemTraySWT.this.showMainWindow();
/*     */           } else {
/* 205 */             this.lastTime = now;
/*     */           }
/* 207 */         } else if (Constants.isOSX) {
/* 208 */           SystemTraySWT.this.trayItem.setImage(SystemTraySWT.this.imgAzureusWhite);
/* 209 */           SystemTraySWT.this.menu.setVisible(true);
/*     */         }
/*     */         
/*     */       }
/* 213 */     });
/* 214 */     this.trayItem.addListener(35, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 216 */         SystemTraySWT.this.menu.setVisible(true);
/*     */       }
/*     */       
/* 219 */     });
/* 220 */     this.uiFunctions.getUIUpdater().addUpdater(this);
/*     */   }
/*     */   
/*     */   public void fillMenu(Menu menu)
/*     */   {
/* 225 */     org.eclipse.swt.widgets.MenuItem itemShow = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 226 */     Messages.setLanguageText(itemShow, "SystemTray.menu.show");
/*     */     
/* 228 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     
/* 230 */     org.eclipse.swt.widgets.MenuItem itemAddTorrent = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 231 */     Messages.setLanguageText(itemAddTorrent, "menu.open.torrent");
/*     */     
/*     */ 
/* 234 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     
/* 236 */     org.eclipse.swt.widgets.MenuItem itemCloseAll = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 237 */     Messages.setLanguageText(itemCloseAll, "SystemTray.menu.closealldownloadbars");
/*     */     
/*     */ 
/* 240 */     org.eclipse.swt.widgets.MenuItem itemShowGlobalTransferBar = new org.eclipse.swt.widgets.MenuItem(menu, 32);
/* 241 */     Messages.setLanguageText(itemShowGlobalTransferBar, "SystemTray.menu.open_global_transfer_bar");
/*     */     
/*     */ 
/* 244 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     
/*     */ 
/* 247 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] menu_items = MenuItemManager.getInstance().getAllAsArray("systray");
/* 248 */     if (menu_items.length > 0) {
/* 249 */       MenuBuildUtils.addPluginMenuItems(menu_items, menu, true, true, MenuBuildUtils.BASIC_MENU_ITEM_CONTROLLER);
/* 250 */       new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     }
/*     */     
/* 253 */     createUploadLimitMenu(menu);
/* 254 */     createDownloadLimitMenu(menu);
/*     */     
/* 256 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     
/* 258 */     org.eclipse.swt.widgets.MenuItem itemStartAll = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 259 */     Messages.setLanguageText(itemStartAll, "SystemTray.menu.startalltransfers");
/*     */     
/* 261 */     org.eclipse.swt.widgets.MenuItem itemStopAll = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 262 */     Messages.setLanguageText(itemStopAll, "SystemTray.menu.stopalltransfers");
/*     */     
/* 264 */     org.eclipse.swt.widgets.MenuItem itemPause = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 265 */     Messages.setLanguageText(itemPause, "SystemTray.menu.pausetransfers");
/*     */     
/* 267 */     org.eclipse.swt.widgets.MenuItem itemResume = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 268 */     Messages.setLanguageText(itemResume, "SystemTray.menu.resumetransfers");
/*     */     
/* 270 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     
/* 272 */     Menu optionsMenu = new Menu(menu.getShell(), 4);
/*     */     
/* 274 */     org.eclipse.swt.widgets.MenuItem optionsItem = new org.eclipse.swt.widgets.MenuItem(menu, 64);
/*     */     
/* 276 */     Messages.setLanguageText(optionsItem, "tray.options");
/*     */     
/* 278 */     optionsItem.setMenu(optionsMenu);
/*     */     
/* 280 */     final org.eclipse.swt.widgets.MenuItem itemShowToolTip = new org.eclipse.swt.widgets.MenuItem(optionsMenu, 32);
/* 281 */     Messages.setLanguageText(itemShowToolTip, "show.tooltip.label");
/*     */     
/* 283 */     org.eclipse.swt.widgets.MenuItem itemMoreOptions = new org.eclipse.swt.widgets.MenuItem(optionsMenu, 8);
/* 284 */     Messages.setLanguageText(itemMoreOptions, "label.more.dot");
/*     */     
/* 286 */     new org.eclipse.swt.widgets.MenuItem(menu, 2);
/*     */     
/* 288 */     org.eclipse.swt.widgets.MenuItem itemExit = new org.eclipse.swt.widgets.MenuItem(menu, 0);
/* 289 */     Messages.setLanguageText(itemExit, "SystemTray.menu.exit");
/*     */     
/* 291 */     itemShow.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 293 */         SystemTraySWT.this.showMainWindow();
/*     */       }
/*     */       
/* 296 */     });
/* 297 */     itemAddTorrent.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 299 */         SystemTraySWT.this.uiFunctions.openTorrentWindow();
/*     */       }
/*     */       
/* 302 */     });
/* 303 */     itemStartAll.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 305 */         if (SystemTraySWT.this.gm == null) {
/* 306 */           return;
/*     */         }
/* 308 */         SystemTraySWT.this.gm.startAllDownloads();
/*     */       }
/*     */       
/* 311 */     });
/* 312 */     itemStopAll.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0) {}
/*     */ 
/* 317 */     });
/* 318 */     itemPause.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0) {}
/*     */ 
/* 323 */     });
/* 324 */     itemResume.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 326 */         if (SystemTraySWT.this.gm == null) {
/* 327 */           return;
/*     */         }
/* 329 */         SystemTraySWT.this.gm.resumeDownloads();
/*     */       }
/*     */       
/* 332 */     });
/* 333 */     itemPause.setEnabled((this.gm != null) && (this.gm.canPauseDownloads()));
/* 334 */     itemResume.setEnabled((this.gm != null) && (this.gm.canResumeDownloads()));
/*     */     
/* 336 */     itemCloseAll.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 338 */         SystemTraySWT.this.uiFunctions.closeDownloadBars();
/*     */       }
/*     */       
/* 341 */     });
/* 342 */     itemShowGlobalTransferBar.setSelection(this.uiFunctions.isGlobalTransferBarShown());
/* 343 */     itemShowGlobalTransferBar.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 345 */         if (SystemTraySWT.this.uiFunctions.isGlobalTransferBarShown()) {
/* 346 */           SystemTraySWT.this.uiFunctions.closeGlobalTransferBar();
/*     */         }
/*     */         else {
/* 349 */           SystemTraySWT.this.uiFunctions.showGlobalTransferBar();
/*     */         }
/*     */         
/*     */       }
/* 353 */     });
/* 354 */     itemShowToolTip.setSelection(this.enableTooltip);
/* 355 */     itemShowToolTip.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 357 */         COConfigurationManager.setParameter("ui.systray.tooltip.enable", itemShowToolTip.getSelection());
/*     */       }
/*     */       
/* 360 */     });
/* 361 */     itemMoreOptions.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 363 */         UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */         
/* 365 */         if (uif != null) {
/* 366 */           uif.getMDI().showEntryByID("ConfigView", "style");
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 372 */     });
/* 373 */     itemMoreOptions.setEnabled(this.uiFunctions.getVisibilityState() != 1);
/*     */     
/* 375 */     itemExit.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 380 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*     */           public void runSupport() {
/* 382 */             SystemTraySWT.this.uiFunctions.dispose(false, false);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final void createUploadLimitMenu(Menu parent)
/*     */   {
/* 394 */     if (this.gm == null) {
/* 395 */       return;
/*     */     }
/* 397 */     org.eclipse.swt.widgets.MenuItem uploadSpeedItem = new org.eclipse.swt.widgets.MenuItem(parent, 64);
/* 398 */     uploadSpeedItem.setText(MessageText.getString("GeneralView.label.maxuploadspeed"));
/*     */     
/* 400 */     final Menu uploadSpeedMenu = new Menu(this.uiFunctions.getMainShell(), 4);
/*     */     
/*     */ 
/* 403 */     uploadSpeedMenu.addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 405 */         SelectableSpeedMenu.generateMenuItems(uploadSpeedMenu, SystemTraySWT.core, SystemTraySWT.this.gm, true);
/*     */       }
/*     */       
/* 408 */     });
/* 409 */     uploadSpeedItem.setMenu(uploadSpeedMenu);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final void createDownloadLimitMenu(Menu parent)
/*     */   {
/* 417 */     if (this.gm == null) {
/* 418 */       return;
/*     */     }
/* 420 */     org.eclipse.swt.widgets.MenuItem downloadSpeedItem = new org.eclipse.swt.widgets.MenuItem(parent, 64);
/* 421 */     downloadSpeedItem.setText(MessageText.getString("GeneralView.label.maxdownloadspeed"));
/*     */     
/* 423 */     final Menu downloadSpeedMenu = new Menu(this.uiFunctions.getMainShell(), 4);
/*     */     
/*     */ 
/* 426 */     downloadSpeedMenu.addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 428 */         SelectableSpeedMenu.generateMenuItems(downloadSpeedMenu, SystemTraySWT.core, SystemTraySWT.this.gm, false);
/*     */       }
/*     */       
/* 431 */     });
/* 432 */     downloadSpeedItem.setMenu(downloadSpeedMenu);
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 436 */     this.uiFunctions.getUIUpdater().removeUpdater(this);
/* 437 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 439 */         if ((SystemTraySWT.this.trayItem != null) && (!SystemTraySWT.this.trayItem.isDisposed())) {
/* 440 */           SystemTraySWT.this.trayItem.dispose();
/*     */         }
/*     */         
/* 443 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 444 */         if (Constants.isOSX) {
/* 445 */           imageLoader.releaseImage("azureus_grey");
/* 446 */           imageLoader.releaseImage("azureus_white");
/*     */         } else {
/* 448 */           imageLoader.releaseImage("azureus");
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 453 */     synchronized (SystemTraySWT.class)
/*     */     {
/* 455 */       singleton = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateUI() {
/* 460 */     updateUI(true);
/*     */   }
/*     */   
/*     */   public void updateUI(boolean is_visible)
/*     */   {
/* 465 */     if (this.interval++ % 10L > 0L) {
/* 466 */       return;
/*     */     }
/* 468 */     if (this.trayItem.isDisposed()) {
/* 469 */       this.uiFunctions.getUIUpdater().removeUpdater(this);
/* 470 */       return;
/*     */     }
/* 472 */     if ((core == null) || (!core.isStarted())) {
/* 473 */       return;
/*     */     }
/*     */     
/* 476 */     if (this.enableTooltip) {
/* 477 */       GlobalManagerStats stats = this.gm.getStats();
/*     */       
/* 479 */       StringBuilder toolTip = new StringBuilder();
/*     */       
/* 481 */       int seeding = 0;
/* 482 */       int downloading = 0;
/*     */       
/* 484 */       DownloadManager next_download = null;
/* 485 */       long next_download_eta = Long.MAX_VALUE;
/*     */       
/* 487 */       TagManager tm = TagManagerFactory.getTagManager();
/*     */       
/* 489 */       if ((tm != null) && (tm.isEnabled()))
/*     */       {
/* 491 */         TagType tt = tm.getTagType(2);
/*     */         
/* 493 */         if (tt != null)
/*     */         {
/* 495 */           TagDownload dl_tag = (TagDownload)tt.getTag(1);
/*     */           
/* 497 */           downloading = dl_tag.getTaggedCount();
/* 498 */           seeding = tt.getTag(2).getTaggedCount();
/*     */           
/* 500 */           if ((this.enableTooltipNextETA) && (downloading > 0))
/*     */           {
/* 502 */             for (DownloadManager dl : dl_tag.getTaggedDownloads())
/*     */             {
/* 504 */               DownloadManagerStats dl_stats = dl.getStats();
/*     */               
/* 506 */               long eta = dl_stats.getSmoothedETA();
/*     */               
/* 508 */               if (eta < next_download_eta)
/*     */               {
/* 510 */                 next_download_eta = eta;
/* 511 */                 next_download = dl;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
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
/* 532 */       String seeding_text = this.seedingKeyVal.replaceAll("%1", "" + seeding);
/* 533 */       String downloading_text = this.downloadingKeyVal.replaceAll("%1", "" + downloading);
/*     */       
/* 535 */       toolTip.append(seeding_text).append(downloading_text).append("\n");
/*     */       
/* 537 */       if (next_download != null)
/*     */       {
/* 539 */         String dl_name = next_download.getDisplayName();
/*     */         
/* 541 */         if (dl_name.length() > 80)
/*     */         {
/* 543 */           dl_name = dl_name.substring(0, 77) + "...";
/*     */         }
/*     */         
/* 546 */         dl_name = dl_name.replaceAll("&", "&&");
/*     */         
/* 548 */         toolTip.append("  ");
/* 549 */         toolTip.append(dl_name);
/* 550 */         toolTip.append(": ");
/* 551 */         toolTip.append(this.etaKeyVal);
/* 552 */         toolTip.append("=");
/* 553 */         toolTip.append(DisplayFormatters.formatETA(next_download_eta));
/* 554 */         toolTip.append("\n");
/*     */       }
/*     */       
/* 557 */       toolTip.append(this.dlAbbrKeyVal).append(" ");
/*     */       
/* 559 */       toolTip.append(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(stats.getDataReceiveRate(), stats.getProtocolReceiveRate()));
/*     */       
/*     */ 
/* 562 */       toolTip.append(", ").append(this.ulAbbrKeyVal).append(" ");
/* 563 */       toolTip.append(DisplayFormatters.formatDataProtByteCountToKiBEtcPerSec(stats.getDataSendRate(), stats.getProtocolSendRate()));
/*     */       
/*     */ 
/* 566 */       int alerts = Alerts.getUnviewedLogAlertCount();
/*     */       
/* 568 */       if (alerts > 0)
/*     */       {
/* 570 */         toolTip.append("\n");
/* 571 */         toolTip.append(this.alertsKeyVal.replaceAll("%1", "" + alerts));
/*     */       }
/*     */       
/* 574 */       this.trayItem.setToolTipText(toolTip.toString());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 580 */     this.trayItem.setVisible(true);
/*     */   }
/*     */   
/*     */   private void showMainWindow() {
/* 584 */     this.uiFunctions.bringToFront(false);
/*     */   }
/*     */   
/*     */   public void updateLanguage() {
/* 588 */     if (this.menu != null) {
/* 589 */       Messages.updateLanguageForControl(this.menu);
/*     */     }
/*     */     
/* 592 */     updateUI();
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 597 */     return "SystemTraySWT";
/*     */   }
/*     */   
/*     */   public void localeChanged(Locale oldLocale, Locale newLocale) {
/* 601 */     this.seedingKeyVal = MessageText.getString("SystemTray.tooltip.seeding");
/* 602 */     this.downloadingKeyVal = MessageText.getString("SystemTray.tooltip.downloading");
/* 603 */     if (!this.downloadingKeyVal.startsWith(" ")) {
/* 604 */       this.downloadingKeyVal = (" " + this.downloadingKeyVal);
/*     */     }
/* 606 */     this.etaKeyVal = MessageText.getString("TableColumn.header.eta");
/* 607 */     this.dlAbbrKeyVal = MessageText.getString("ConfigView.download.abbreviated");
/* 608 */     this.ulAbbrKeyVal = MessageText.getString("ConfigView.upload.abbreviated");
/*     */     
/* 610 */     this.alertsKeyVal = MessageText.getString("label.alertnum");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/systray/SystemTraySWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */