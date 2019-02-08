/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.GenericActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*     */ public class ConfigSectionTransfer
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  57 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  61 */     return "transfer";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  68 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  69 */     imageLoader.releaseImage("subitem");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  73 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  82 */     Composite cSection = new Composite(parent, 0);
/*  83 */     GridData gridData = new GridData(272);
/*     */     
/*  85 */     Utils.setLayoutData(cSection, gridData);
/*  86 */     GridLayout layout = new GridLayout();
/*  87 */     layout.numColumns = 2;
/*  88 */     layout.marginHeight = 0;
/*  89 */     cSection.setLayout(layout);
/*     */     
/*  91 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/*  94 */     final int[] manual_max_download_speed = { COConfigurationManager.getIntParameter("Max Download Speed KBs") };
/*     */     
/*     */ 
/*     */ 
/*  98 */     gridData = new GridData();
/*  99 */     Label label = new Label(cSection, 0);
/* 100 */     Utils.setLayoutData(label, gridData);
/* 101 */     Messages.setLanguageText(label, "ConfigView.label.maxuploadspeed");
/*     */     
/* 103 */     gridData = new GridData();
/* 104 */     final IntParameter paramMaxUploadSpeed = new IntParameter(cSection, "Max Upload Speed KBs", 0, Integer.MAX_VALUE);
/*     */     
/* 106 */     paramMaxUploadSpeed.setLayoutData(gridData);
/*     */     
/*     */ 
/* 109 */     Composite cMaxUploadSpeedOptionsArea = new Composite(cSection, 0);
/* 110 */     layout = new GridLayout();
/* 111 */     layout.numColumns = 3;
/* 112 */     layout.marginWidth = 0;
/* 113 */     layout.marginHeight = 0;
/* 114 */     cMaxUploadSpeedOptionsArea.setLayout(layout);
/* 115 */     gridData = new GridData();
/* 116 */     gridData.horizontalIndent = 15;
/* 117 */     gridData.horizontalSpan = 2;
/* 118 */     Utils.setLayoutData(cMaxUploadSpeedOptionsArea, gridData);
/*     */     
/* 120 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 121 */     Image img = imageLoader.getImage("subitem");
/*     */     
/* 123 */     label = new Label(cMaxUploadSpeedOptionsArea, 0);
/* 124 */     img.setBackground(label.getBackground());
/* 125 */     gridData = new GridData(2);
/* 126 */     Utils.setLayoutData(label, gridData);
/* 127 */     label.setImage(img);
/*     */     
/* 129 */     gridData = new GridData();
/* 130 */     final BooleanParameter enable_seeding_rate = new BooleanParameter(cMaxUploadSpeedOptionsArea, "enable.seedingonly.upload.rate", "ConfigView.label.maxuploadspeedseeding");
/*     */     
/*     */ 
/* 133 */     enable_seeding_rate.setLayoutData(gridData);
/*     */     
/* 135 */     gridData = new GridData();
/* 136 */     final IntParameter paramMaxUploadSpeedSeeding = new IntParameter(cMaxUploadSpeedOptionsArea, "Max Upload Speed Seeding KBs", 0, Integer.MAX_VALUE);
/*     */     
/* 138 */     paramMaxUploadSpeedSeeding.setLayoutData(gridData);
/* 139 */     enable_seeding_rate.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(paramMaxUploadSpeedSeeding.getControl()));
/*     */     
/*     */ 
/*     */ 
/* 143 */     if (userMode < 2)
/*     */     {
/*     */ 
/* 146 */       Composite cWiki = new Composite(cSection, 15);
/* 147 */       gridData = new GridData(272);
/*     */       
/* 149 */       gridData.horizontalSpan = 2;
/* 150 */       Utils.setLayoutData(cWiki, gridData);
/* 151 */       layout = new GridLayout();
/* 152 */       layout.numColumns = 4;
/* 153 */       layout.marginHeight = 0;
/* 154 */       cWiki.setLayout(layout);
/*     */       
/* 156 */       gridData = new GridData();
/* 157 */       gridData.horizontalIndent = 6;
/* 158 */       gridData.horizontalSpan = 2;
/* 159 */       label = new Label(cWiki, 0);
/* 160 */       Utils.setLayoutData(label, gridData);
/* 161 */       label.setText(MessageText.getString("Utils.link.visit") + ":");
/*     */       
/* 163 */       gridData = new GridData();
/* 164 */       gridData.horizontalIndent = 10;
/* 165 */       gridData.horizontalSpan = 2;
/* 166 */       new LinkLabel(cWiki, gridData, "ConfigView.section.transfer.speeds.wiki", "http://wiki.vuze.com/w/Good_settings");
/*     */     }
/*     */     
/*     */ 
/* 170 */     if (userMode > 1)
/*     */     {
/* 172 */       gridData = new GridData();
/* 173 */       label = new Label(cSection, 0);
/* 174 */       Utils.setLayoutData(label, gridData);
/* 175 */       Messages.setLanguageText(label, "ConfigView.label.maxuploadswhenbusymin");
/*     */       
/* 177 */       gridData = new GridData();
/* 178 */       new IntParameter(cSection, "max.uploads.when.busy.inc.min.secs", 0, Integer.MAX_VALUE).setLayoutData(gridData);
/*     */     }
/*     */     
/*     */ 
/* 182 */     gridData = new GridData();
/* 183 */     label = new Label(cSection, 0);
/* 184 */     Utils.setLayoutData(label, gridData);
/* 185 */     Messages.setLanguageText(label, "ConfigView.label.maxdownloadspeed");
/*     */     
/* 187 */     gridData = new GridData();
/* 188 */     final IntParameter paramMaxDownSpeed = new IntParameter(cSection, "Max Download Speed KBs", 0, Integer.MAX_VALUE);
/*     */     
/* 190 */     paramMaxDownSpeed.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 194 */     Listener l = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 197 */         boolean disableAuto = false;
/* 198 */         boolean disableAutoSeeding = false;
/*     */         
/* 200 */         if (enable_seeding_rate.isSelected().booleanValue())
/*     */         {
/* 202 */           disableAutoSeeding = event.widget == paramMaxUploadSpeedSeeding.getControl();
/* 203 */           disableAuto = (event.widget == paramMaxDownSpeed.getControl()) || (event.widget == paramMaxUploadSpeed.getControl());
/*     */         }
/*     */         else {
/* 206 */           disableAuto = true;
/* 207 */           disableAutoSeeding = true;
/*     */         }
/*     */         
/*     */ 
/* 211 */         if (disableAuto)
/* 212 */           COConfigurationManager.setParameter("Auto Upload Speed Enabled", false);
/* 213 */         if (disableAutoSeeding) {
/* 214 */           COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", false);
/*     */         }
/*     */       }
/* 217 */     };
/* 218 */     paramMaxDownSpeed.getControl().addListener(13, l);
/* 219 */     paramMaxUploadSpeed.getControl().addListener(13, l);
/* 220 */     paramMaxUploadSpeedSeeding.getControl().addListener(13, l);
/*     */     
/*     */ 
/* 223 */     paramMaxUploadSpeed.addChangeListener(new ParameterChangeAdapter() {
/* 224 */       ParameterChangeAdapter me = this;
/*     */       
/*     */       public void parameterChanged(Parameter p, boolean internal) {
/* 227 */         CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 230 */             if (ConfigSectionTransfer.2.this.val$paramMaxUploadSpeed.isDisposed()) {
/* 231 */               ConfigSectionTransfer.2.this.val$paramMaxUploadSpeed.removeChangeListener(ConfigSectionTransfer.2.this.me);
/* 232 */               return;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 238 */             if (TransferSpeedValidator.isAutoSpeedActive(core.getGlobalManager()))
/*     */             {
/* 240 */               return;
/*     */             }
/*     */             
/* 243 */             int up_val = ConfigSectionTransfer.2.this.val$paramMaxUploadSpeed.getValue();
/* 244 */             int down_val = ConfigSectionTransfer.2.this.val$paramMaxDownSpeed.getValue();
/*     */             
/* 246 */             if ((up_val != 0) && (up_val < 5))
/*     */             {
/*     */ 
/* 249 */               if ((down_val == 0) || (down_val > up_val * 2))
/*     */               {
/* 251 */                 ConfigSectionTransfer.2.this.val$paramMaxDownSpeed.setValue(up_val * 2);
/*     */               }
/*     */               
/*     */             }
/* 255 */             else if (down_val != ConfigSectionTransfer.2.this.val$manual_max_download_speed[0])
/*     */             {
/* 257 */               ConfigSectionTransfer.2.this.val$paramMaxDownSpeed.setValue(ConfigSectionTransfer.2.this.val$manual_max_download_speed[0]);
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */ 
/*     */         });
/*     */       }
/* 265 */     });
/* 266 */     paramMaxDownSpeed.addChangeListener(new ParameterChangeAdapter() {
/* 267 */       ParameterChangeAdapter me = this;
/*     */       
/*     */       public void parameterChanged(Parameter p, boolean internal) {
/* 270 */         CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 273 */             if (ConfigSectionTransfer.3.this.val$paramMaxDownSpeed.isDisposed()) {
/* 274 */               ConfigSectionTransfer.3.this.val$paramMaxDownSpeed.removeChangeListener(ConfigSectionTransfer.3.this.me);
/* 275 */               return;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 281 */             if (TransferSpeedValidator.isAutoSpeedActive(core.getGlobalManager()))
/*     */             {
/* 283 */               return;
/*     */             }
/*     */             
/* 286 */             int up_val = ConfigSectionTransfer.3.this.val$paramMaxUploadSpeed.getValue();
/* 287 */             int down_val = ConfigSectionTransfer.3.this.val$paramMaxDownSpeed.getValue();
/*     */             
/* 289 */             ConfigSectionTransfer.3.this.val$manual_max_download_speed[0] = down_val;
/*     */             
/* 291 */             if (up_val < 5)
/*     */             {
/* 293 */               if ((up_val != 0) && (up_val < down_val * 2))
/*     */               {
/* 295 */                 ConfigSectionTransfer.3.this.val$paramMaxUploadSpeed.setValue((down_val + 1) / 2);
/*     */               }
/* 297 */               else if (down_val == 0)
/*     */               {
/* 299 */                 ConfigSectionTransfer.3.this.val$paramMaxUploadSpeed.setValue(0);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */     
/* 307 */     if (userMode > 0)
/*     */     {
/*     */ 
/*     */ 
/* 311 */       BooleanParameter bias_upload = new BooleanParameter(cSection, "Bias Upload Enable", "ConfigView.label.xfer.bias_up");
/*     */       
/*     */ 
/*     */ 
/* 315 */       gridData = new GridData();
/* 316 */       gridData.horizontalSpan = 2;
/* 317 */       bias_upload.setLayoutData(gridData);
/*     */       
/*     */ 
/* 320 */       Composite bias_slack_area = new Composite(cSection, 0);
/* 321 */       layout = new GridLayout();
/* 322 */       layout.numColumns = 3;
/* 323 */       layout.marginWidth = 0;
/* 324 */       layout.marginHeight = 0;
/* 325 */       bias_slack_area.setLayout(layout);
/* 326 */       gridData = new GridData();
/* 327 */       gridData.horizontalIndent = 15;
/* 328 */       gridData.horizontalSpan = 2;
/* 329 */       Utils.setLayoutData(bias_slack_area, gridData);
/*     */       
/* 331 */       label = new Label(bias_slack_area, 0);
/* 332 */       gridData = new GridData(2);
/* 333 */       Utils.setLayoutData(label, gridData);
/* 334 */       label.setImage(img);
/*     */       
/* 336 */       label = new Label(bias_slack_area, 0);
/* 337 */       Messages.setLanguageText(label, "ConfigView.label.xfer.bias_slack");
/*     */       
/* 339 */       IntParameter bias_slack = new IntParameter(bias_slack_area, "Bias Upload Slack KBs", 1, Integer.MAX_VALUE);
/*     */       
/*     */ 
/*     */ 
/* 343 */       Composite bias_unlimited_area = new Composite(cSection, 0);
/* 344 */       layout = new GridLayout();
/* 345 */       layout.numColumns = 2;
/* 346 */       layout.marginWidth = 0;
/* 347 */       layout.marginHeight = 0;
/* 348 */       bias_unlimited_area.setLayout(layout);
/* 349 */       gridData = new GridData();
/* 350 */       gridData.horizontalIndent = 15;
/* 351 */       gridData.horizontalSpan = 2;
/* 352 */       Utils.setLayoutData(bias_unlimited_area, gridData);
/*     */       
/* 354 */       label = new Label(bias_unlimited_area, 0);
/* 355 */       gridData = new GridData(2);
/* 356 */       Utils.setLayoutData(label, gridData);
/* 357 */       label.setImage(img);
/*     */       
/*     */ 
/* 360 */       BooleanParameter bias_no_limit = new BooleanParameter(bias_unlimited_area, "Bias Upload Handle No Limit", "ConfigView.label.xfer.bias_no_limit");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 365 */       bias_upload.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Parameter[] { bias_slack, bias_no_limit }));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 370 */     if (userMode > 0)
/*     */     {
/*     */ 
/*     */ 
/* 374 */       Group auto_group = new Group(cSection, 0);
/*     */       
/* 376 */       Messages.setLanguageText(auto_group, "group.auto");
/*     */       
/* 378 */       GridLayout auto_layout = new GridLayout();
/*     */       
/* 380 */       auto_layout.numColumns = 2;
/*     */       
/* 382 */       auto_group.setLayout(auto_layout);
/*     */       
/* 384 */       gridData = new GridData(768);
/* 385 */       gridData.horizontalSpan = 2;
/* 386 */       Utils.setLayoutData(auto_group, gridData);
/*     */       
/* 388 */       BooleanParameter auto_adjust = new BooleanParameter(auto_group, "Auto Adjust Transfer Defaults", "ConfigView.label.autoadjust");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 393 */       gridData = new GridData();
/* 394 */       gridData.horizontalSpan = 2;
/*     */       
/* 396 */       auto_adjust.setLayoutData(gridData);
/*     */       
/*     */ 
/* 399 */       gridData = new GridData();
/* 400 */       label = new Label(auto_group, 0);
/* 401 */       Utils.setLayoutData(label, gridData);
/* 402 */       Messages.setLanguageText(label, "ConfigView.label.maxuploads");
/*     */       
/* 404 */       gridData = new GridData();
/* 405 */       IntParameter paramMaxUploads = new IntParameter(auto_group, "Max Uploads", 2, Integer.MAX_VALUE);
/*     */       
/* 407 */       paramMaxUploads.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 411 */       Composite cMaxUploadsOptionsArea = new Composite(auto_group, 0);
/* 412 */       layout = new GridLayout();
/* 413 */       layout.numColumns = 3;
/* 414 */       layout.marginWidth = 0;
/* 415 */       layout.marginHeight = 0;
/* 416 */       cMaxUploadsOptionsArea.setLayout(layout);
/* 417 */       gridData = new GridData();
/* 418 */       gridData.horizontalIndent = 15;
/* 419 */       gridData.horizontalSpan = 2;
/* 420 */       Utils.setLayoutData(cMaxUploadsOptionsArea, gridData);
/* 421 */       label = new Label(cMaxUploadsOptionsArea, 0);
/* 422 */       img.setBackground(label.getBackground());
/* 423 */       gridData = new GridData(2);
/* 424 */       Utils.setLayoutData(label, gridData);
/* 425 */       label.setImage(img);
/*     */       
/* 427 */       gridData = new GridData();
/* 428 */       BooleanParameter enable_seeding_uploads = new BooleanParameter(cMaxUploadsOptionsArea, "enable.seedingonly.maxuploads", "ConfigView.label.maxuploadsseeding");
/*     */       
/*     */ 
/* 431 */       enable_seeding_uploads.setLayoutData(gridData);
/*     */       
/* 433 */       gridData = new GridData();
/* 434 */       final IntParameter paramMaxUploadsSeeding = new IntParameter(cMaxUploadsOptionsArea, "Max Uploads Seeding", 2, Integer.MAX_VALUE);
/*     */       
/* 436 */       paramMaxUploadsSeeding.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 442 */       gridData = new GridData();
/* 443 */       label = new Label(auto_group, 0);
/* 444 */       Utils.setLayoutData(label, gridData);
/* 445 */       Messages.setLanguageText(label, "ConfigView.label.max_peers_per_torrent");
/*     */       
/* 447 */       gridData = new GridData();
/* 448 */       IntParameter paramMaxClients = new IntParameter(auto_group, "Max.Peer.Connections.Per.Torrent");
/*     */       
/* 450 */       paramMaxClients.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 457 */       Composite cMaxPeersOptionsArea = new Composite(auto_group, 0);
/* 458 */       layout = new GridLayout();
/* 459 */       layout.numColumns = 3;
/* 460 */       layout.marginWidth = 0;
/* 461 */       layout.marginHeight = 0;
/* 462 */       cMaxPeersOptionsArea.setLayout(layout);
/* 463 */       gridData = new GridData();
/* 464 */       gridData.horizontalIndent = 15;
/* 465 */       gridData.horizontalSpan = 2;
/* 466 */       Utils.setLayoutData(cMaxPeersOptionsArea, gridData);
/* 467 */       label = new Label(cMaxPeersOptionsArea, 0);
/* 468 */       img.setBackground(label.getBackground());
/* 469 */       gridData = new GridData(2);
/* 470 */       Utils.setLayoutData(label, gridData);
/* 471 */       label.setImage(img);
/*     */       
/* 473 */       gridData = new GridData();
/* 474 */       BooleanParameter enable_max_peers_seeding = new BooleanParameter(cMaxPeersOptionsArea, "Max.Peer.Connections.Per.Torrent.When.Seeding.Enable", "ConfigView.label.maxuploadsseeding");
/*     */       
/*     */ 
/* 477 */       enable_max_peers_seeding.setLayoutData(gridData);
/*     */       
/* 479 */       gridData = new GridData();
/* 480 */       final IntParameter paramMaxPeersSeeding = new IntParameter(cMaxPeersOptionsArea, "Max.Peer.Connections.Per.Torrent.When.Seeding", 0, Integer.MAX_VALUE);
/*     */       
/* 482 */       paramMaxPeersSeeding.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 486 */       gridData = new GridData();
/* 487 */       label = new Label(auto_group, 0);
/* 488 */       Utils.setLayoutData(label, gridData);
/* 489 */       Messages.setLanguageText(label, "ConfigView.label.max_peers_total");
/*     */       
/* 491 */       gridData = new GridData();
/* 492 */       IntParameter paramMaxClientsTotal = new IntParameter(auto_group, "Max.Peer.Connections.Total");
/*     */       
/* 494 */       paramMaxClientsTotal.setLayoutData(gridData);
/*     */       
/* 496 */       gridData = new GridData();
/* 497 */       label = new Label(auto_group, 0);
/* 498 */       Utils.setLayoutData(label, gridData);
/* 499 */       Messages.setLanguageText(label, "ConfigView.label.maxseedspertorrent");
/*     */       
/* 501 */       gridData = new GridData();
/* 502 */       IntParameter max_seeds_per_torrent = new IntParameter(auto_group, "Max Seeds Per Torrent");
/* 503 */       max_seeds_per_torrent.setLayoutData(gridData);
/*     */       
/* 505 */       final Parameter[] parameters = { paramMaxUploads, enable_seeding_uploads, paramMaxUploadsSeeding, paramMaxClients, enable_max_peers_seeding, paramMaxPeersSeeding, paramMaxClientsTotal, max_seeds_per_torrent };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 511 */       IAdditionalActionPerformer f_enabler = new GenericActionPerformer(new Control[0])
/*     */       {
/*     */ 
/*     */         public void performAction()
/*     */         {
/*     */ 
/* 517 */           boolean auto = COConfigurationManager.getBooleanParameter("Auto Adjust Transfer Defaults");
/*     */           
/* 519 */           for (Parameter p : parameters)
/*     */           {
/* 521 */             Control[] c = p.getControls();
/*     */             
/* 523 */             for (Control x : c)
/*     */             {
/* 525 */               x.setEnabled(!auto);
/*     */             }
/*     */           }
/*     */           
/* 529 */           if (!auto)
/*     */           {
/* 531 */             paramMaxUploadsSeeding.getControl().setEnabled(COConfigurationManager.getBooleanParameter("enable.seedingonly.maxuploads"));
/*     */             
/* 533 */             paramMaxPeersSeeding.getControl().setEnabled(COConfigurationManager.getBooleanParameter("Max.Peer.Connections.Per.Torrent.When.Seeding.Enable"));
/*     */           }
/*     */           
/*     */         }
/* 537 */       };
/* 538 */       f_enabler.performAction();
/*     */       
/* 540 */       enable_seeding_uploads.setAdditionalActionPerformer(f_enabler);
/* 541 */       enable_max_peers_seeding.setAdditionalActionPerformer(f_enabler);
/* 542 */       auto_adjust.setAdditionalActionPerformer(f_enabler);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 547 */       Group npp_group = new Group(cSection, 0);
/*     */       
/* 549 */       Messages.setLanguageText(npp_group, "label.non.public.peers");
/*     */       
/* 551 */       GridLayout npp_layout = new GridLayout();
/*     */       
/* 553 */       npp_layout.numColumns = 2;
/*     */       
/* 555 */       npp_group.setLayout(npp_layout);
/*     */       
/* 557 */       gridData = new GridData(768);
/* 558 */       gridData.horizontalSpan = 2;
/* 559 */       Utils.setLayoutData(npp_group, gridData);
/*     */       
/* 561 */       label = new Label(npp_group, 0);
/* 562 */       Messages.setLanguageText(label, "ConfigView.label.npp.slots");
/*     */       
/* 564 */       IntParameter npp_upload_slots = new IntParameter(npp_group, "Non-Public Peer Extra Slots Per Torrent", 0, Integer.MAX_VALUE);
/*     */       
/* 566 */       label = new Label(npp_group, 0);
/* 567 */       Messages.setLanguageText(label, "ConfigView.label.npp.connections");
/*     */       
/* 569 */       IntParameter npp_connections = new IntParameter(npp_group, "Non-Public Peer Extra Connections Per Torrent", 0, Integer.MAX_VALUE);
/*     */       
/*     */ 
/*     */ 
/* 573 */       gridData = new GridData();
/* 574 */       gridData.horizontalSpan = 2;
/* 575 */       BooleanParameter useReqLimiting = new BooleanParameter(cSection, "Use Request Limiting", "ConfigView.label.userequestlimiting");
/*     */       
/* 577 */       useReqLimiting.setLayoutData(gridData);
/*     */       
/* 579 */       gridData = new GridData();
/* 580 */       gridData.horizontalSpan = 2;
/* 581 */       BooleanParameter useReqLimitingPrios = new BooleanParameter(cSection, "Use Request Limiting Priorities", "ConfigView.label.userequestlimitingpriorities");
/*     */       
/* 583 */       useReqLimitingPrios.setLayoutData(gridData);
/* 584 */       useReqLimiting.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(useReqLimitingPrios.getControl()));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 589 */       gridData = new GridData();
/* 590 */       gridData.horizontalSpan = 2;
/* 591 */       BooleanParameter upIncludesProt = new BooleanParameter(cSection, "Up Rate Limits Include Protocol", "ConfigView.label.up.includes.prot");
/*     */       
/* 593 */       upIncludesProt.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 597 */       gridData = new GridData();
/* 598 */       gridData.horizontalSpan = 2;
/* 599 */       BooleanParameter downIncludesProt = new BooleanParameter(cSection, "Down Rate Limits Include Protocol", "ConfigView.label.down.includes.prot");
/*     */       
/* 601 */       downIncludesProt.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 605 */       gridData = new GridData();
/* 606 */       gridData.horizontalSpan = 2;
/* 607 */       BooleanParameter allowSameIP = new BooleanParameter(cSection, "Allow Same IP Peers", "ConfigView.label.allowsameip");
/*     */       
/* 609 */       allowSameIP.setLayoutData(gridData);
/*     */       
/*     */ 
/* 612 */       gridData = new GridData();
/* 613 */       gridData.horizontalSpan = 2;
/* 614 */       BooleanParameter lazybf = new BooleanParameter(cSection, "Use Lazy Bitfield", "ConfigView.label.lazybitfield");
/*     */       
/* 616 */       lazybf.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 620 */       if (userMode > 1) {
/* 621 */         gridData = new GridData();
/* 622 */         gridData.horizontalSpan = 2;
/* 623 */         BooleanParameter hap = new BooleanParameter(cSection, "peercontrol.hide.piece", "ConfigView.label.hap");
/*     */         
/* 625 */         hap.setLayoutData(gridData);
/*     */         
/* 627 */         gridData = new GridData();
/* 628 */         gridData.horizontalSpan = 2;
/* 629 */         BooleanParameter hapds = new BooleanParameter(cSection, "peercontrol.hide.piece.ds", "ConfigView.label.hapds");
/*     */         
/* 631 */         hapds.setLayoutData(gridData);
/*     */         
/* 633 */         hap.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(hapds.getControl()));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 639 */       gridData = new GridData();
/* 640 */       gridData.horizontalSpan = 2;
/* 641 */       BooleanParameter firstPiece = new BooleanParameter(cSection, "Prioritize First Piece", "ConfigView.label.prioritizefirstpiece");
/*     */       
/*     */ 
/* 644 */       firstPiece.setLayoutData(gridData);
/*     */       
/*     */ 
/* 647 */       gridData = new GridData();
/* 648 */       gridData.horizontalSpan = 2;
/* 649 */       BooleanParameter mostCompletedFiles = new BooleanParameter(cSection, "Prioritize Most Completed Files", "ConfigView.label.prioritizemostcompletedfiles");
/*     */       
/*     */ 
/* 652 */       mostCompletedFiles.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 656 */       Composite cMiniArea = new Composite(cSection, 0);
/* 657 */       layout = new GridLayout();
/* 658 */       layout.numColumns = 2;
/* 659 */       layout.marginHeight = 0;
/* 660 */       layout.marginWidth = 0;
/* 661 */       cMiniArea.setLayout(layout);
/* 662 */       gridData = new GridData(768);
/* 663 */       gridData.horizontalSpan = 2;
/* 664 */       Utils.setLayoutData(cMiniArea, gridData);
/*     */       
/* 666 */       gridData = new GridData();
/* 667 */       label = new Label(cMiniArea, 0);
/* 668 */       Utils.setLayoutData(label, gridData);
/* 669 */       Messages.setLanguageText(label, "ConfigView.label.transfer.ignorepeerports");
/*     */       
/*     */ 
/* 672 */       gridData = new GridData();
/* 673 */       gridData.widthHint = 125;
/* 674 */       StringParameter ignore_ports = new StringParameter(cMiniArea, "Ignore.peer.ports", "0");
/*     */       
/* 676 */       ignore_ports.setLayoutData(gridData);
/*     */     }
/*     */     
/* 679 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTransfer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */