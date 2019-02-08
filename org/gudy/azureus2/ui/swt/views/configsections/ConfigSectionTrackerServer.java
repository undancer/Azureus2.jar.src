/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.auth.CertificateCreatorWindow;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.GenericActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.PasswordParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.ipchecker.IpCheckerWizard;
/*     */ import org.gudy.azureus2.ui.swt.ipchecker.IpSetterCallBack;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*     */ public class ConfigSectionTrackerServer
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.";
/*     */   private static final int REQUIRED_MODE = 1;
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  55 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  63 */     return "tracker";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  67 */     return "tracker.server";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  83 */     Composite gMainTab = new Composite(parent, 0);
/*     */     
/*  85 */     GridData gridData = new GridData(272);
/*  86 */     gMainTab.setLayoutData(gridData);
/*  87 */     GridLayout layout = new GridLayout();
/*  88 */     layout.numColumns = 4;
/*  89 */     gMainTab.setLayout(layout);
/*     */     
/*  91 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  93 */     if (userMode < 1) {
/*  94 */       Label label = new Label(gMainTab, 64);
/*  95 */       gridData = new GridData();
/*  96 */       label.setLayoutData(gridData);
/*     */       
/*  98 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/* 103 */       if (1 < modeKeys.length) {
/* 104 */         param1 = MessageText.getString(modeKeys[1]);
/*     */       } else
/* 106 */         param1 = String.valueOf(1);
/*     */       String param2;
/* 108 */       String param2; if (userMode < modeKeys.length) {
/* 109 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/* 111 */         param2 = String.valueOf(userMode);
/*     */       }
/* 113 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/* 116 */       return gMainTab;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 123 */     if (userMode > 0)
/*     */     {
/* 125 */       Label label = new Label(gMainTab, 0);
/* 126 */       Messages.setLanguageText(label, "ConfigView.section.tracker.ip");
/*     */       
/* 128 */       final StringParameter tracker_ip = new StringParameter(gMainTab, "Tracker IP", "");
/*     */       
/* 130 */       gridData = new GridData();
/* 131 */       gridData.widthHint = 120;
/* 132 */       tracker_ip.setLayoutData(gridData);
/*     */       
/* 134 */       Button check_button = new Button(gMainTab, 8);
/* 135 */       gridData = new GridData();
/* 136 */       gridData.horizontalSpan = 2;
/* 137 */       check_button.setLayoutData(gridData);
/*     */       
/* 139 */       Messages.setLanguageText(check_button, "ConfigView.section.tracker.checkip");
/*     */       
/* 141 */       final Display display = gMainTab.getDisplay();
/*     */       
/* 143 */       check_button.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 145 */           IpCheckerWizard wizard = new IpCheckerWizard();
/* 146 */           wizard.setIpSetterCallBack(new IpSetterCallBack() {
/*     */             public void setIp(final String ip) {
/* 148 */               if ((ConfigSectionTrackerServer.1.this.val$display == null) || (ConfigSectionTrackerServer.1.this.val$display.isDisposed()))
/* 149 */                 return;
/* 150 */               ConfigSectionTrackerServer.1.this.val$display.asyncExec(new AERunnable() {
/*     */                 public void runSupport() {
/* 152 */                   if (ConfigSectionTrackerServer.1.this.val$tracker_ip != null) {
/* 153 */                     ConfigSectionTrackerServer.1.this.val$tracker_ip.setValue(ip);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */ 
/*     */               });
/*     */             }
/*     */           });
/*     */         }
/* 163 */       });
/* 164 */       final BooleanParameter nonsslEnable = new BooleanParameter(gMainTab, "Tracker Port Enable", "ConfigView.section.tracker.port");
/*     */       
/*     */ 
/*     */ 
/* 168 */       IntParameter tracker_port = new IntParameter(gMainTab, "Tracker Port", 0, 65535);
/*     */       
/* 170 */       gridData = new GridData();
/* 171 */       tracker_port.setLayoutData(gridData);
/*     */       
/* 173 */       StringParameter tracker_port_backup = new StringParameter(gMainTab, "Tracker Port Backups", "");
/*     */       
/* 175 */       gridData = new GridData();
/* 176 */       gridData.widthHint = 100;
/* 177 */       tracker_port_backup.setLayoutData(gridData);
/*     */       
/* 179 */       Label tracker_port_backup_label = new Label(gMainTab, 0);
/* 180 */       Messages.setLanguageText(tracker_port_backup_label, "ConfigView.section.tracker.portbackup");
/*     */       
/* 182 */       Control[] non_ssl_controls = new Control[3];
/* 183 */       non_ssl_controls[0] = tracker_port.getControl();
/* 184 */       non_ssl_controls[1] = tracker_port_backup.getControl();
/* 185 */       non_ssl_controls[2] = tracker_port_backup_label;
/*     */       
/* 187 */       nonsslEnable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(non_ssl_controls));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 192 */       final BooleanParameter sslEnable = new BooleanParameter(gMainTab, "Tracker Port SSL Enable", "ConfigView.section.tracker.sslport");
/*     */       
/*     */ 
/*     */ 
/* 196 */       IntParameter tracker_port_ssl = new IntParameter(gMainTab, "Tracker Port SSL", 0, 65535);
/*     */       
/* 198 */       gridData = new GridData();
/* 199 */       tracker_port_ssl.setLayoutData(gridData);
/*     */       
/* 201 */       StringParameter tracker_port_ssl_backup = new StringParameter(gMainTab, "Tracker Port SSL Backups", "");
/*     */       
/* 203 */       gridData = new GridData();
/* 204 */       gridData.widthHint = 100;
/* 205 */       tracker_port_ssl_backup.setLayoutData(gridData);
/*     */       
/* 207 */       Label tracker_port_ssl_backup_label = new Label(gMainTab, 0);
/* 208 */       Messages.setLanguageText(tracker_port_ssl_backup_label, "ConfigView.section.tracker.portbackup");
/*     */       
/*     */ 
/*     */ 
/* 212 */       Label cert_label = new Label(gMainTab, 0);
/* 213 */       Messages.setLanguageText(cert_label, "ConfigView.section.tracker.createcert");
/*     */       
/* 215 */       Button cert_button = new Button(gMainTab, 8);
/*     */       
/* 217 */       Messages.setLanguageText(cert_button, "ConfigView.section.tracker.createbutton");
/*     */       
/* 219 */       cert_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 225 */           new CertificateCreatorWindow();
/*     */         }
/*     */         
/* 228 */       });
/* 229 */       Label ssl_faq_label = new Label(gMainTab, 0);
/* 230 */       gridData = new GridData();
/* 231 */       gridData.horizontalSpan = 2;
/* 232 */       ssl_faq_label.setLayoutData(gridData);
/* 233 */       Messages.setLanguageText(ssl_faq_label, "ConfigView.section.tracker.sslport.info");
/* 234 */       String linkFAQ = "http://plugins.vuze.com/faq.php#19";
/* 235 */       ssl_faq_label.setCursor(ssl_faq_label.getDisplay().getSystemCursor(21));
/* 236 */       ssl_faq_label.setData("http://plugins.vuze.com/faq.php#19");
/* 237 */       ssl_faq_label.setForeground(Colors.blue);
/* 238 */       ssl_faq_label.addMouseListener(new MouseAdapter() {
/*     */         public void mouseDoubleClick(MouseEvent arg0) {
/* 240 */           Utils.launch("http://plugins.vuze.com/faq.php#19");
/*     */         }
/*     */         
/* 243 */         public void mouseDown(MouseEvent arg0) { Utils.launch("http://plugins.vuze.com/faq.php#19");
/*     */         }
/* 245 */       });
/* 246 */       ClipboardCopy.addCopyToClipMenu(ssl_faq_label);
/*     */       
/* 248 */       Control[] ssl_controls = { tracker_port_ssl.getControl(), tracker_port_ssl_backup.getControl(), tracker_port_ssl_backup_label, ssl_faq_label, cert_label, cert_button };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 257 */       sslEnable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(ssl_controls));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 262 */       gridData = new GridData();
/* 263 */       gridData.horizontalSpan = 1;
/* 264 */       new BooleanParameter(gMainTab, "Tracker Public Enable", "ConfigView.section.tracker.publicenable").setLayoutData(gridData);
/*     */       
/*     */ 
/* 267 */       label = new Label(gMainTab, 0);
/* 268 */       Messages.setLanguageText(label, "ConfigView.section.tracker.publicenable.info");
/* 269 */       gridData = new GridData();
/* 270 */       gridData.horizontalSpan = 3;
/* 271 */       label.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 275 */       BooleanParameter forcePortDetails = new BooleanParameter(gMainTab, "Tracker Port Force External", "ConfigView.section.tracker.forceport");
/*     */       
/*     */ 
/*     */ 
/* 279 */       label = new Label(gMainTab, 0);
/* 280 */       gridData = new GridData();
/* 281 */       gridData.horizontalSpan = 3;
/* 282 */       label.setLayoutData(gridData);
/*     */       
/*     */ 
/* 285 */       Control[] f_controls = new Control[1];
/* 286 */       f_controls[0] = forcePortDetails.getControl();
/*     */       
/* 288 */       IAdditionalActionPerformer f_enabler = new GenericActionPerformer(f_controls)
/*     */       {
/*     */         public void performAction()
/*     */         {
/* 292 */           boolean selected = (nonsslEnable.isSelected().booleanValue()) || (sslEnable.isSelected().booleanValue());
/*     */           
/*     */ 
/* 295 */           this.controls[0].setEnabled(selected);
/*     */         }
/*     */         
/* 298 */       };
/* 299 */       nonsslEnable.setAdditionalActionPerformer(f_enabler);
/* 300 */       sslEnable.setAdditionalActionPerformer(f_enabler);
/*     */       
/*     */ 
/*     */ 
/* 304 */       BooleanParameter hostAddURLs = new BooleanParameter(gMainTab, "Tracker Host Add Our Announce URLs", "ConfigView.section.tracker.host.addurls");
/*     */       
/*     */ 
/*     */ 
/* 308 */       gridData = new GridData();
/* 309 */       gridData.horizontalSpan = 2;
/* 310 */       hostAddURLs.setLayoutData(gridData);
/*     */       
/* 312 */       label = new Label(gMainTab, 0);
/* 313 */       gridData = new GridData();
/* 314 */       gridData.horizontalSpan = 2;
/* 315 */       label.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 319 */       gridData = new GridData();
/* 320 */       gridData.horizontalSpan = 1;
/* 321 */       final BooleanParameter passwordEnableWeb = new BooleanParameter(gMainTab, "Tracker Password Enable Web", "ConfigView.section.tracker.passwordenableweb");
/*     */       
/*     */ 
/* 324 */       passwordEnableWeb.setLayoutData(gridData);
/*     */       
/* 326 */       gridData = new GridData();
/* 327 */       gridData.horizontalSpan = 3;
/* 328 */       BooleanParameter passwordWebHTTPSOnly = new BooleanParameter(gMainTab, "Tracker Password Web HTTPS Only", "ConfigView.section.tracker.passwordwebhttpsonly");
/*     */       
/*     */ 
/* 331 */       passwordWebHTTPSOnly.setLayoutData(gridData);
/*     */       
/* 333 */       IAdditionalActionPerformer web_https_enabler = new GenericActionPerformer(passwordWebHTTPSOnly.getControls())
/*     */       {
/*     */ 
/*     */         public void performAction()
/*     */         {
/* 338 */           boolean selected = (passwordEnableWeb.isSelected().booleanValue()) && (sslEnable.isSelected().booleanValue());
/*     */           
/*     */ 
/* 341 */           for (int i = 0; i < this.controls.length; i++)
/*     */           {
/* 343 */             this.controls[i].setEnabled(selected);
/*     */           }
/*     */           
/*     */         }
/* 347 */       };
/* 348 */       passwordEnableWeb.setAdditionalActionPerformer(web_https_enabler);
/* 349 */       sslEnable.setAdditionalActionPerformer(web_https_enabler);
/*     */       
/*     */ 
/*     */ 
/* 353 */       final BooleanParameter passwordEnableTorrent = new BooleanParameter(gMainTab, "Tracker Password Enable Torrent", "ConfigView.section.tracker.passwordenabletorrent");
/*     */       
/*     */ 
/*     */ 
/* 357 */       label = new Label(gMainTab, 0);
/* 358 */       Messages.setLanguageText(label, "ConfigView.section.tracker.passwordenabletorrent.info");
/* 359 */       gridData = new GridData();
/* 360 */       gridData.horizontalSpan = 3;
/* 361 */       label.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 365 */       label = new Label(gMainTab, 0);
/* 366 */       Messages.setLanguageText(label, "ConfigView.section.tracker.username");
/*     */       
/* 368 */       StringParameter tracker_username = new StringParameter(gMainTab, "Tracker Username", "");
/*     */       
/* 370 */       gridData = new GridData();
/* 371 */       gridData.widthHint = 100;
/* 372 */       tracker_username.setLayoutData(gridData);
/*     */       
/* 374 */       label = new Label(gMainTab, 0);
/* 375 */       label = new Label(gMainTab, 0);
/*     */       
/*     */ 
/* 378 */       label = new Label(gMainTab, 0);
/* 379 */       Messages.setLanguageText(label, "ConfigView.section.tracker.password");
/*     */       
/* 381 */       PasswordParameter tracker_password = new PasswordParameter(gMainTab, "Tracker Password");
/*     */       
/* 383 */       gridData = new GridData();
/* 384 */       gridData.widthHint = 100;
/* 385 */       tracker_password.setLayoutData(gridData);
/*     */       
/* 387 */       label = new Label(gMainTab, 0);
/* 388 */       label = new Label(gMainTab, 0);
/*     */       
/* 390 */       Control[] x_controls = new Control[2];
/* 391 */       x_controls[0] = tracker_username.getControl();
/* 392 */       x_controls[1] = tracker_password.getControl();
/*     */       
/* 394 */       IAdditionalActionPerformer enabler = new GenericActionPerformer(x_controls)
/*     */       {
/*     */ 
/*     */         public void performAction()
/*     */         {
/* 399 */           boolean selected = (passwordEnableWeb.isSelected().booleanValue()) || (passwordEnableTorrent.isSelected().booleanValue());
/*     */           
/*     */ 
/* 402 */           for (int i = 0; i < this.controls.length; i++)
/*     */           {
/* 404 */             this.controls[i].setEnabled(selected);
/*     */           }
/*     */           
/*     */         }
/* 408 */       };
/* 409 */       passwordEnableWeb.setAdditionalActionPerformer(enabler);
/* 410 */       passwordEnableTorrent.setAdditionalActionPerformer(enabler);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 416 */       Group gPollStuff = new Group(gMainTab, 0);
/* 417 */       Messages.setLanguageText(gPollStuff, "ConfigView.section.tracker.pollinterval");
/* 418 */       gridData = new GridData(272);
/* 419 */       gridData.horizontalSpan = 4;
/* 420 */       gPollStuff.setLayoutData(gridData);
/* 421 */       layout = new GridLayout();
/* 422 */       layout.numColumns = 4;
/* 423 */       gPollStuff.setLayout(layout);
/*     */       
/* 425 */       label = new Label(gPollStuff, 0);
/* 426 */       Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalmin");
/* 427 */       gridData = new GridData();
/* 428 */       label.setLayoutData(gridData);
/*     */       
/* 430 */       IntParameter pollIntervalMin = new IntParameter(gPollStuff, "Tracker Poll Interval Min");
/*     */       
/* 432 */       gridData = new GridData();
/* 433 */       pollIntervalMin.setLayoutData(gridData);
/*     */       
/* 435 */       label = new Label(gPollStuff, 0);
/* 436 */       Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalmax");
/* 437 */       gridData = new GridData();
/* 438 */       label.setLayoutData(gridData);
/*     */       
/* 440 */       IntParameter pollIntervalMax = new IntParameter(gPollStuff, "Tracker Poll Interval Max");
/*     */       
/* 442 */       gridData = new GridData();
/* 443 */       pollIntervalMax.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 447 */       label = new Label(gPollStuff, 0);
/* 448 */       Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalincby");
/* 449 */       gridData = new GridData();
/* 450 */       label.setLayoutData(gridData);
/*     */       
/* 452 */       IntParameter pollIntervalIncBy = new IntParameter(gPollStuff, "Tracker Poll Inc By");
/*     */       
/* 454 */       gridData = new GridData();
/* 455 */       pollIntervalIncBy.setLayoutData(gridData);
/*     */       
/* 457 */       label = new Label(gPollStuff, 0);
/* 458 */       Messages.setLanguageText(label, "ConfigView.section.tracker.pollintervalincper");
/* 459 */       gridData = new GridData();
/* 460 */       label.setLayoutData(gridData);
/*     */       
/* 462 */       IntParameter pollIntervalIncPer = new IntParameter(gPollStuff, "Tracker Poll Inc Per");
/*     */       
/* 464 */       gridData = new GridData();
/* 465 */       pollIntervalIncPer.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 470 */       Group gScrapeCache = new Group(gMainTab, 0);
/* 471 */       Messages.setLanguageText(gScrapeCache, "ConfigView.section.tracker.scrapeandcache");
/* 472 */       gridData = new GridData(272);
/* 473 */       gridData.horizontalSpan = 4;
/* 474 */       gScrapeCache.setLayoutData(gridData);
/* 475 */       layout = new GridLayout();
/* 476 */       layout.numColumns = 4;
/* 477 */       gScrapeCache.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/* 481 */       label = new Label(gScrapeCache, 0);
/* 482 */       Messages.setLanguageText(label, "ConfigView.section.tracker.announcescrapepercentage");
/*     */       
/* 484 */       IntParameter scrapeannouncepercentage = new IntParameter(gScrapeCache, "Tracker Scrape Retry Percentage");
/*     */       
/* 486 */       gridData = new GridData();
/* 487 */       scrapeannouncepercentage.setLayoutData(gridData);
/*     */       
/* 489 */       label = new Label(gScrapeCache, 0);
/* 490 */       Messages.setLanguageText(label, "ConfigView.section.tracker.scrapecacheperiod");
/* 491 */       gridData = new GridData();
/* 492 */       label.setLayoutData(gridData);
/*     */       
/* 494 */       IntParameter scrapeCachePeriod = new IntParameter(gScrapeCache, "Tracker Scrape Cache");
/*     */       
/* 496 */       gridData = new GridData();
/* 497 */       scrapeCachePeriod.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 502 */       label = new Label(gScrapeCache, 0);
/* 503 */       Messages.setLanguageText(label, "ConfigView.section.tracker.announcecacheminpeers");
/*     */       
/* 505 */       IntParameter announceCacheMinPeers = new IntParameter(gScrapeCache, "Tracker Announce Cache Min Peers");
/*     */       
/* 507 */       gridData = new GridData();
/* 508 */       announceCacheMinPeers.setLayoutData(gridData);
/*     */       
/* 510 */       label = new Label(gScrapeCache, 0);
/* 511 */       Messages.setLanguageText(label, "ConfigView.section.tracker.announcecacheperiod");
/* 512 */       gridData = new GridData();
/* 513 */       label.setLayoutData(gridData);
/*     */       
/* 515 */       IntParameter announceCachePeriod = new IntParameter(gScrapeCache, "Tracker Announce Cache");
/*     */       
/* 517 */       gridData = new GridData();
/* 518 */       announceCachePeriod.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 524 */       label = new Label(gMainTab, 0);
/* 525 */       Messages.setLanguageText(label, "ConfigView.section.tracker.maxpeersreturned");
/* 526 */       gridData = new GridData();
/* 527 */       label.setLayoutData(gridData);
/*     */       
/* 529 */       IntParameter maxPeersReturned = new IntParameter(gMainTab, "Tracker Max Peers Returned");
/*     */       
/* 531 */       gridData = new GridData();
/* 532 */       maxPeersReturned.setLayoutData(gridData);
/*     */       
/* 534 */       label = new Label(gMainTab, 0);
/* 535 */       label = new Label(gMainTab, 0);
/*     */       
/*     */ 
/*     */ 
/* 539 */       label = new Label(gMainTab, 0);
/* 540 */       Messages.setLanguageText(label, "ConfigView.section.tracker.seedretention");
/* 541 */       gridData = new GridData();
/* 542 */       label.setLayoutData(gridData);
/*     */       
/* 544 */       IntParameter seedRetentionLimit = new IntParameter(gMainTab, "Tracker Max Seeds Retained");
/*     */       
/* 546 */       gridData = new GridData();
/* 547 */       seedRetentionLimit.setLayoutData(gridData);
/*     */       
/* 549 */       label = new Label(gMainTab, 0);
/* 550 */       Messages.setLanguageText(label, "ConfigView.section.tracker.seedretention.info");
/* 551 */       gridData = new GridData();
/* 552 */       gridData.horizontalSpan = 2;
/* 553 */       label.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 557 */       gridData = new GridData();
/* 558 */       gridData.horizontalSpan = 2;
/* 559 */       new BooleanParameter(gMainTab, "Tracker NAT Check Enable", "ConfigView.section.tracker.natcheckenable").setLayoutData(gridData);
/*     */       
/*     */ 
/* 562 */       Composite gNATDetails = new Composite(gMainTab, 0);
/* 563 */       gridData = new GridData(272);
/* 564 */       gridData.horizontalSpan = 2;
/* 565 */       gNATDetails.setLayoutData(gridData);
/* 566 */       layout = new GridLayout();
/* 567 */       layout.numColumns = 2;
/* 568 */       layout.marginHeight = 0;
/* 569 */       layout.marginWidth = 0;
/* 570 */       gNATDetails.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/* 574 */       label = new Label(gNATDetails, 0);
/* 575 */       Messages.setLanguageText(label, "ConfigView.section.tracker.natchecktimeout");
/* 576 */       gridData = new GridData();
/* 577 */       label.setLayoutData(gridData);
/*     */       
/* 579 */       IntParameter NATTimeout = new IntParameter(gNATDetails, "Tracker NAT Check Timeout");
/*     */       
/* 581 */       gridData = new GridData();
/* 582 */       NATTimeout.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 587 */       gridData = new GridData();
/* 588 */       gridData.horizontalSpan = 4;
/*     */       
/* 590 */       new BooleanParameter(gMainTab, "Tracker Send Peer IDs", "ConfigView.section.tracker.sendpeerids").setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 595 */       gridData = new GridData();
/* 596 */       gridData.horizontalSpan = 4;
/*     */       
/* 598 */       BooleanParameter enable_udp = new BooleanParameter(gMainTab, "Tracker Port UDP Enable", "ConfigView.section.tracker.enableudp");
/*     */       
/*     */ 
/*     */ 
/* 602 */       enable_udp.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 606 */       Label udp_version_label = new Label(gMainTab, 0);
/* 607 */       Messages.setLanguageText(udp_version_label, "ConfigView.section.tracker.udpversion");
/* 608 */       gridData = new GridData();
/* 609 */       IntParameter udp_version = new IntParameter(gMainTab, "Tracker Port UDP Version");
/* 610 */       udp_version.setLayoutData(gridData);
/* 611 */       label = new Label(gMainTab, 0);
/* 612 */       label = new Label(gMainTab, 0);
/*     */       
/* 614 */       enable_udp.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { udp_version_label, udp_version.getControl() }));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 619 */       gridData = new GridData();
/* 620 */       gridData.horizontalSpan = 4;
/*     */       
/* 622 */       new BooleanParameter(gMainTab, "Tracker Compact Enable", "ConfigView.section.tracker.enablecompact").setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 627 */       gridData = new GridData();
/* 628 */       gridData.horizontalSpan = 4;
/* 629 */       BooleanParameter log_enable = new BooleanParameter(gMainTab, "Tracker Log Enable", "ConfigView.section.tracker.logenable");
/*     */       
/*     */ 
/* 632 */       log_enable.setLayoutData(gridData);
/*     */       
/* 634 */       if (userMode > 1)
/*     */       {
/*     */ 
/*     */ 
/* 638 */         gridData = new GridData();
/* 639 */         gridData.horizontalSpan = 4;
/*     */         
/* 641 */         new BooleanParameter(gMainTab, "Tracker Key Enable Server", "ConfigView.section.tracker.enablekey").setLayoutData(gridData);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 646 */         Label banned_peers_label = new Label(gMainTab, 0);
/* 647 */         Messages.setLanguageText(banned_peers_label, "ConfigView.section.tracker.banned.clients");
/*     */         
/* 649 */         gridData = new GridData(768);
/* 650 */         gridData.horizontalSpan = 3;
/*     */         
/* 652 */         new StringParameter(gMainTab, "Tracker Banned Clients", "").setLayoutData(gridData);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 657 */         Group networks_group = new Group(gMainTab, 0);
/* 658 */         Messages.setLanguageText(networks_group, "ConfigView.section.tracker.server.group.networks");
/* 659 */         GridData networks_layout = new GridData(272);
/* 660 */         networks_layout.horizontalSpan = 4;
/* 661 */         networks_group.setLayoutData(networks_layout);
/* 662 */         layout = new GridLayout();
/* 663 */         layout.numColumns = 2;
/* 664 */         networks_group.setLayout(layout);
/*     */         
/* 666 */         label = new Label(networks_group, 0);
/* 667 */         Messages.setLanguageText(label, "ConfigView.section.tracker.server.group.networks.info");
/* 668 */         GridData grid_data = new GridData();
/* 669 */         grid_data.horizontalSpan = 2;
/* 670 */         label.setLayoutData(grid_data);
/*     */         
/* 672 */         for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*     */         {
/* 674 */           String nn = AENetworkClassifier.AT_NETWORKS[i];
/*     */           
/* 676 */           String config_name = "Tracker Network Selection Default." + nn;
/* 677 */           String msg_text = "ConfigView.section.connection.networks." + nn;
/*     */           
/* 679 */           BooleanParameter network = new BooleanParameter(networks_group, config_name, msg_text);
/*     */           
/* 681 */           grid_data = new GridData();
/* 682 */           grid_data.horizontalSpan = 2;
/* 683 */           network.setLayoutData(grid_data);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 688 */         Group gProcessing = new Group(gMainTab, 0);
/* 689 */         Messages.setLanguageText(gProcessing, "ConfigView.section.tracker.processinglimits");
/* 690 */         gridData = new GridData(272);
/* 691 */         gridData.horizontalSpan = 4;
/* 692 */         gProcessing.setLayoutData(gridData);
/* 693 */         layout = new GridLayout();
/* 694 */         layout.numColumns = 3;
/* 695 */         gProcessing.setLayout(layout);
/*     */         
/*     */ 
/*     */ 
/* 699 */         label = new Label(gProcessing, 0);
/* 700 */         Messages.setLanguageText(label, "ConfigView.section.tracker.maxgettime");
/* 701 */         gridData = new GridData();
/* 702 */         label.setLayoutData(gridData);
/*     */         
/* 704 */         IntParameter maxGetTime = new IntParameter(gProcessing, "Tracker Max GET Time");
/*     */         
/* 706 */         gridData = new GridData();
/* 707 */         maxGetTime.setLayoutData(gridData);
/*     */         
/* 709 */         label = new Label(gProcessing, 0);
/* 710 */         Messages.setLanguageText(label, "ConfigView.section.tracker.maxgettime.info");
/*     */         
/*     */ 
/*     */ 
/* 714 */         label = new Label(gProcessing, 0);
/* 715 */         Messages.setLanguageText(label, "ConfigView.section.tracker.maxposttimemultiplier");
/* 716 */         gridData = new GridData();
/* 717 */         label.setLayoutData(gridData);
/*     */         
/* 719 */         IntParameter maxPostTimeMultiplier = new IntParameter(gProcessing, "Tracker Max POST Time Multiplier");
/*     */         
/* 721 */         gridData = new GridData();
/* 722 */         maxPostTimeMultiplier.setLayoutData(gridData);
/*     */         
/* 724 */         label = new Label(gProcessing, 0);
/* 725 */         Messages.setLanguageText(label, "ConfigView.section.tracker.maxposttimemultiplier.info");
/*     */         
/*     */ 
/*     */ 
/* 729 */         label = new Label(gProcessing, 0);
/* 730 */         Messages.setLanguageText(label, "ConfigView.section.tracker.maxthreads");
/* 731 */         gridData = new GridData();
/* 732 */         label.setLayoutData(gridData);
/*     */         
/* 734 */         IntParameter maxThreadsTime = new IntParameter(gProcessing, "Tracker Max Threads");
/* 735 */         maxThreadsTime.setMinimumValue(1);
/* 736 */         maxThreadsTime.setMaximumValue(4096);
/* 737 */         gridData = new GridData();
/* 738 */         maxThreadsTime.setLayoutData(gridData);
/*     */         
/* 740 */         label = new Label(gProcessing, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 745 */         Group gNBTracker = new Group(gMainTab, 0);
/* 746 */         Messages.setLanguageText(gNBTracker, "ConfigView.section.tracker.nonblocking");
/* 747 */         gridData = new GridData(272);
/* 748 */         gridData.horizontalSpan = 4;
/* 749 */         gNBTracker.setLayoutData(gridData);
/* 750 */         layout = new GridLayout();
/* 751 */         layout.numColumns = 3;
/* 752 */         gNBTracker.setLayout(layout);
/*     */         
/*     */ 
/*     */ 
/* 756 */         gridData = new GridData();
/* 757 */         gridData.horizontalSpan = 3;
/*     */         
/* 759 */         BooleanParameter nb_enable = new BooleanParameter(gNBTracker, "Tracker TCP NonBlocking", "ConfigView.section.tracker.tcpnonblocking");
/*     */         
/*     */ 
/* 762 */         nb_enable.setLayoutData(gridData);
/*     */         
/*     */ 
/*     */ 
/* 766 */         label = new Label(gNBTracker, 0);
/* 767 */         Messages.setLanguageText(label, "ConfigView.section.tracker.nonblockingconcmax");
/* 768 */         gridData = new GridData();
/* 769 */         label.setLayoutData(gridData);
/*     */         
/* 771 */         IntParameter maxConcConn = new IntParameter(gNBTracker, "Tracker TCP NonBlocking Conc Max");
/* 772 */         gridData = new GridData();
/* 773 */         maxConcConn.setLayoutData(gridData);
/*     */         
/* 775 */         label = new Label(gNBTracker, 0);
/*     */         
/* 777 */         nb_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(maxConcConn.getControls()));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 782 */     return gMainTab;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTrackerServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */