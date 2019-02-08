/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeListener;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.views.stats.TransferStatsView.limitToTextHelper;
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
/*     */ public class ConfigSectionTransferAutoSpeedSelect
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.transfer.autospeed.";
/*     */   StringListParameter versionList;
/*     */   BooleanParameter enableAutoSpeed;
/*     */   BooleanParameter enableAutoSpeedWhileSeeding;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  71 */     return "transfer";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  82 */     return "transfer.select";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int maxUserMode()
/*     */   {
/* 103 */     return 0;
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
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/* 120 */     Composite cSection = new Composite(parent, 0);
/*     */     
/* 122 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 123 */       cSection.setLayout(new FillLayout());
/* 124 */       Label lblNotAvail = new Label(cSection, 64);
/* 125 */       Messages.setLanguageText(lblNotAvail, "core.not.available");
/* 126 */       return cSection;
/*     */     }
/*     */     
/* 129 */     GridData gridData = new GridData(272);
/* 130 */     Utils.setLayoutData(cSection, gridData);
/* 131 */     GridLayout subPanel = new GridLayout();
/* 132 */     subPanel.numColumns = 3;
/* 133 */     cSection.setLayout(subPanel);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */     Group modeGroup = new Group(cSection, 0);
/* 143 */     Messages.setLanguageText(modeGroup, "ConfigTransferAutoSpeed.algorithm.selector");
/* 144 */     GridLayout modeLayout = new GridLayout();
/* 145 */     modeLayout.numColumns = 3;
/* 146 */     modeGroup.setLayout(modeLayout);
/*     */     
/* 148 */     gridData = new GridData(768);
/* 149 */     Utils.setLayoutData(modeGroup, gridData);
/*     */     
/*     */ 
/* 152 */     Label label = new Label(modeGroup, 0);
/* 153 */     Messages.setLanguageText(label, "ConfigTransferAutoSpeed.algorithm");
/* 154 */     gridData = new GridData();
/* 155 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 157 */     String AutoSpeedClassic = MessageText.getString("ConfigTransferAutoSpeed.auto.speed.classic");
/* 158 */     String AutoSpeedBeta = MessageText.getString("ConfigTransferAutoSpeed.auto.speed.beta");
/* 159 */     String AutoSpeedNeural = MessageText.getString("ConfigTransferAutoSpeed.auto.speed.neural");
/*     */     
/* 161 */     String[] modeNames = { AutoSpeedClassic, AutoSpeedBeta, AutoSpeedNeural };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */     String[] modes = { "1", "2", "3" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */     this.versionList = new StringListParameter(modeGroup, "Auto_Upload_Speed_Version_String", modeNames, modes);
/* 178 */     long verNum = COConfigurationManager.getLongParameter("Auto Upload Speed Version");
/* 179 */     if (verNum == 1L)
/*     */     {
/* 181 */       this.versionList.setValue(modes[0]);
/* 182 */     } else if (verNum == 2L)
/*     */     {
/* 184 */       this.versionList.setValue(modes[1]);
/* 185 */     } else if (verNum == 3L)
/*     */     {
/* 187 */       this.versionList.setValue(modes[2]);
/*     */     }
/*     */     else {
/* 190 */       this.versionList.setValue(modes[0]);
/*     */     }
/*     */     
/*     */ 
/* 194 */     this.versionList.addChangeListener(new ConvertToLongChangeListener());
/*     */     
/*     */ 
/*     */ 
/* 198 */     Label spacer = new Label(modeGroup, 0);
/* 199 */     gridData = new GridData();
/* 200 */     gridData.horizontalSpan = 3;
/* 201 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/*     */ 
/* 204 */     gridData = new GridData();
/* 205 */     gridData.horizontalIndent = 20;
/* 206 */     gridData.horizontalSpan = 2;
/* 207 */     this.enableAutoSpeed = new BooleanParameter(modeGroup, "Auto Upload Speed Enabled", "ConfigView.section.transfer.autospeed.enableauto");
/*     */     
/* 209 */     this.enableAutoSpeed.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 213 */     spacer = new Label(modeGroup, 0);
/*     */     
/*     */ 
/* 216 */     this.enableAutoSpeedWhileSeeding = new BooleanParameter(modeGroup, "Auto Upload Speed Seeding Enabled", "ConfigView.section.transfer.autospeed.enableautoseeding");
/*     */     
/* 218 */     gridData = new GridData();
/* 219 */     gridData.horizontalIndent = 20;
/* 220 */     gridData.horizontalSpan = 2;
/* 221 */     this.enableAutoSpeedWhileSeeding.setLayoutData(gridData);
/*     */     
/* 223 */     this.enableAutoSpeed.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(this.enableAutoSpeedWhileSeeding.getControls(), true));
/*     */     
/*     */ 
/*     */ 
/* 227 */     spacer = new Label(modeGroup, 0);
/*     */     
/* 229 */     spacer = new Label(cSection, 0);
/* 230 */     gridData = new GridData();
/* 231 */     gridData.horizontalSpan = 3;
/* 232 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/*     */ 
/*     */ 
/* 236 */     Group networkGroup = new Group(cSection, 0);
/*     */     
/*     */ 
/* 239 */     Messages.setLanguageText(networkGroup, "ConfigView.section.transfer.autospeed.networks");
/* 240 */     GridLayout networksLayout = new GridLayout();
/* 241 */     networksLayout.numColumns = 5;
/* 242 */     networkGroup.setLayout(networksLayout);
/*     */     
/* 244 */     gridData = new GridData(768);
/* 245 */     networkGroup.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 249 */     label = new Label(networkGroup, 0);
/* 250 */     Messages.setLanguageText(label, "SpeedView.stats.asn");
/*     */     
/* 252 */     final Label asn_label = new Label(networkGroup, 0);
/* 253 */     gridData = new GridData();
/* 254 */     gridData.horizontalSpan = 4;
/* 255 */     gridData.grabExcessHorizontalSpace = true;
/* 256 */     asn_label.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 260 */     label = new Label(networkGroup, 0);
/* 261 */     Messages.setLanguageText(label, "SpeedView.stats.estupcap");
/* 262 */     gridData = new GridData();
/* 263 */     gridData.horizontalIndent = 20;
/* 264 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 266 */     final Label up_cap = new Label(networkGroup, 0);
/* 267 */     gridData = new GridData(768);
/* 268 */     gridData.horizontalSpan = 4;
/* 269 */     Utils.setLayoutData(up_cap, gridData);
/*     */     
/*     */ 
/*     */ 
/* 273 */     label = new Label(networkGroup, 0);
/* 274 */     Messages.setLanguageText(label, "SpeedView.stats.estdowncap");
/* 275 */     gridData = new GridData();
/* 276 */     gridData.horizontalIndent = 20;
/* 277 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 279 */     final Label down_cap = new Label(networkGroup, 0);
/* 280 */     gridData = new GridData(768);
/* 281 */     gridData.horizontalSpan = 4;
/* 282 */     Utils.setLayoutData(down_cap, gridData);
/*     */     
/*     */ 
/* 285 */     final SpeedManager sm = AzureusCoreFactory.getSingleton().getSpeedManager();
/*     */     
/* 287 */     final TransferStatsView.limitToTextHelper limit_to_text = new TransferStatsView.limitToTextHelper();
/*     */     
/* 289 */     asn_label.setText(sm.getASN());
/* 290 */     up_cap.setText(limit_to_text.getLimitText(sm.getEstimatedUploadCapacityBytesPerSec()));
/* 291 */     down_cap.setText(limit_to_text.getLimitText(sm.getEstimatedDownloadCapacityBytesPerSec()));
/*     */     
/*     */ 
/*     */ 
/* 295 */     spacer = new Label(networkGroup, 0);
/* 296 */     gridData = new GridData();
/* 297 */     gridData.horizontalSpan = 5;
/* 298 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/*     */ 
/*     */ 
/* 302 */     Label info_label = new Label(networkGroup, 64);
/* 303 */     Messages.setLanguageText(info_label, "ConfigView.section.transfer.autospeed.network.info", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */     
/*     */ 
/* 306 */     Utils.setLayoutData(info_label, Utils.getWrappableLabelGridData(5, 0));
/*     */     
/*     */ 
/*     */ 
/* 310 */     label = new Label(networkGroup, 0);
/* 311 */     Messages.setLanguageText(label, "SpeedView.stats.estupcap");
/* 312 */     gridData = new GridData();
/* 313 */     gridData.horizontalIndent = 20;
/* 314 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 316 */     String co_up = "AutoSpeed Network Upload Speed (temp)";
/* 317 */     String co_up_type = "AutoSpeed Network Upload Speed Type (temp)";
/*     */     
/* 319 */     SpeedManagerLimitEstimate up_lim = sm.getEstimatedUploadCapacityBytesPerSec();
/*     */     
/* 321 */     COConfigurationManager.setParameter(co_up, up_lim.getBytesPerSec() / 1024);
/* 322 */     COConfigurationManager.setParameter(co_up_type, limit_to_text.getSettableType(up_lim));
/*     */     
/* 324 */     final IntParameter max_upload = new IntParameter(networkGroup, co_up);
/*     */     
/* 326 */     final Label upload_bits = new Label(networkGroup, 0);
/* 327 */     gridData = new GridData();
/* 328 */     Utils.setLayoutData(upload_bits, gridData);
/* 329 */     upload_bits.setText(getMBitLimit(limit_to_text, up_lim.getBytesPerSec() / 1024 * 1024));
/*     */     
/* 331 */     final StringListParameter max_upload_type = new StringListParameter(networkGroup, co_up_type, limit_to_text.getSettableTypes(), limit_to_text.getSettableTypes());
/*     */     
/*     */ 
/* 334 */     max_upload_type.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 342 */         if (max_upload_type.isDisposed())
/*     */         {
/* 344 */           return;
/*     */         }
/*     */         
/* 347 */         float type = limit_to_text.textToType(max_upload_type.getValue());
/*     */         
/* 349 */         SpeedManagerLimitEstimate existing = sm.getEstimatedUploadCapacityBytesPerSec();
/*     */         
/* 351 */         if (existing.getEstimateType() != type)
/*     */         {
/* 353 */           sm.setEstimatedUploadCapacityBytesPerSec(existing.getBytesPerSec(), type);
/*     */         }
/*     */         
/*     */       }
/* 357 */     });
/* 358 */     max_upload.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 366 */         if (max_upload.isDisposed())
/*     */         {
/* 368 */           return;
/*     */         }
/*     */         
/* 371 */         int value = max_upload.getValue() * 1024;
/*     */         
/* 373 */         SpeedManagerLimitEstimate existing = sm.getEstimatedUploadCapacityBytesPerSec();
/*     */         
/* 375 */         if (existing.getBytesPerSec() != value)
/*     */         {
/* 377 */           sm.setEstimatedUploadCapacityBytesPerSec(value, existing.getEstimateType());
/*     */         }
/*     */         
/*     */       }
/* 381 */     });
/* 382 */     label = new Label(networkGroup, 0);
/*     */     
/*     */ 
/*     */ 
/* 386 */     label = new Label(networkGroup, 0);
/* 387 */     Messages.setLanguageText(label, "SpeedView.stats.estdowncap");
/* 388 */     gridData = new GridData();
/* 389 */     gridData.horizontalIndent = 20;
/* 390 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 392 */     SpeedManagerLimitEstimate down_lim = sm.getEstimatedDownloadCapacityBytesPerSec();
/*     */     
/* 394 */     String co_down = "AutoSpeed Network Download Speed (temp)";
/* 395 */     String co_down_type = "AutoSpeed Network Download Speed Type (temp)";
/*     */     
/* 397 */     COConfigurationManager.setParameter(co_down, down_lim.getBytesPerSec() / 1024);
/* 398 */     COConfigurationManager.setParameter(co_down_type, limit_to_text.getSettableType(down_lim));
/*     */     
/* 400 */     final IntParameter max_download = new IntParameter(networkGroup, co_down);
/*     */     
/* 402 */     final Label download_bits = new Label(networkGroup, 0);
/* 403 */     gridData = new GridData();
/* 404 */     Utils.setLayoutData(download_bits, gridData);
/* 405 */     download_bits.setText(getMBitLimit(limit_to_text, down_lim.getBytesPerSec() / 1024 * 1024));
/*     */     
/* 407 */     final StringListParameter max_download_type = new StringListParameter(networkGroup, co_down_type, limit_to_text.getSettableTypes(), limit_to_text.getSettableTypes());
/*     */     
/*     */ 
/* 410 */     max_download_type.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 418 */         if (max_download_type.isDisposed())
/*     */         {
/* 420 */           return;
/*     */         }
/*     */         
/* 423 */         float type = limit_to_text.textToType(max_download_type.getValue());
/*     */         
/* 425 */         SpeedManagerLimitEstimate existing = sm.getEstimatedDownloadCapacityBytesPerSec();
/*     */         
/* 427 */         if (existing.getEstimateType() != type)
/*     */         {
/* 429 */           sm.setEstimatedDownloadCapacityBytesPerSec(existing.getBytesPerSec(), type);
/*     */         }
/*     */         
/*     */       }
/* 433 */     });
/* 434 */     max_download.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 442 */         if (max_download.isDisposed())
/*     */         {
/* 444 */           return;
/*     */         }
/*     */         
/* 447 */         int value = max_download.getValue() * 1024;
/*     */         
/* 449 */         SpeedManagerLimitEstimate existing = sm.getEstimatedDownloadCapacityBytesPerSec();
/*     */         
/* 451 */         if (existing.getBytesPerSec() != value)
/*     */         {
/* 453 */           sm.setEstimatedDownloadCapacityBytesPerSec(value, existing.getEstimateType());
/*     */         }
/*     */         
/*     */       }
/* 457 */     });
/* 458 */     label = new Label(networkGroup, 0);
/*     */     
/*     */ 
/*     */ 
/* 462 */     Label reset_label = new Label(networkGroup, 0);
/* 463 */     Messages.setLanguageText(reset_label, "ConfigView.section.transfer.autospeed.resetnetwork");
/*     */     
/* 465 */     Button reset_button = new Button(networkGroup, 8);
/*     */     
/* 467 */     Messages.setLanguageText(reset_button, "ConfigView.section.transfer.autospeed.reset.button");
/*     */     
/* 469 */     reset_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 475 */         sm.reset();
/*     */       }
/*     */       
/* 478 */     });
/* 479 */     sm.addListener(new SpeedManagerListener()
/*     */     {
/*     */ 
/* 482 */       private final SpeedManagerListener listener = this;
/*     */       
/*     */ 
/*     */ 
/*     */       public void propertyChanged(final int property)
/*     */       {
/* 488 */         Utils.execSWTThread(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 494 */             if (ConfigSectionTransferAutoSpeedSelect.6.this.val$asn_label.isDisposed())
/*     */             {
/* 496 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$sm.removeListener(ConfigSectionTransferAutoSpeedSelect.6.this.listener);
/*     */ 
/*     */ 
/*     */             }
/* 500 */             else if (property == 1)
/*     */             {
/* 502 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$asn_label.setText(ConfigSectionTransferAutoSpeedSelect.6.this.val$sm.getASN());
/*     */             }
/* 504 */             else if (property == 2)
/*     */             {
/* 506 */               SpeedManagerLimitEstimate limit = ConfigSectionTransferAutoSpeedSelect.6.this.val$sm.getEstimatedUploadCapacityBytesPerSec();
/*     */               
/* 508 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$up_cap.setText(ConfigSectionTransferAutoSpeedSelect.6.this.val$limit_to_text.getLimitText(limit));
/*     */               
/* 510 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$upload_bits.setText(ConfigSectionTransferAutoSpeedSelect.this.getMBitLimit(ConfigSectionTransferAutoSpeedSelect.6.this.val$limit_to_text, limit.getBytesPerSec()));
/*     */               
/* 512 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$max_upload.setValue(limit.getBytesPerSec() / 1024);
/*     */               
/* 514 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$max_upload_type.setValue(ConfigSectionTransferAutoSpeedSelect.6.this.val$limit_to_text.getSettableType(limit));
/*     */             }
/* 516 */             else if (property == 3)
/*     */             {
/* 518 */               SpeedManagerLimitEstimate limit = ConfigSectionTransferAutoSpeedSelect.6.this.val$sm.getEstimatedDownloadCapacityBytesPerSec();
/*     */               
/* 520 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$down_cap.setText(ConfigSectionTransferAutoSpeedSelect.6.this.val$limit_to_text.getLimitText(limit));
/*     */               
/* 522 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$download_bits.setText(ConfigSectionTransferAutoSpeedSelect.this.getMBitLimit(ConfigSectionTransferAutoSpeedSelect.6.this.val$limit_to_text, limit.getBytesPerSec()));
/*     */               
/* 524 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$max_download.setValue(limit.getBytesPerSec() / 1024);
/*     */               
/* 526 */               ConfigSectionTransferAutoSpeedSelect.6.this.val$max_download_type.setValue(ConfigSectionTransferAutoSpeedSelect.6.this.val$limit_to_text.getSettableType(limit));
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 539 */     });
/* 540 */     spacer = new Label(cSection, 0);
/* 541 */     gridData = new GridData();
/* 542 */     gridData.horizontalSpan = 3;
/* 543 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/* 545 */     BooleanParameter debug_au = new BooleanParameter(cSection, "Auto Upload Speed Debug Enabled", "ConfigView.section.transfer.autospeed.enabledebug");
/*     */     
/*     */ 
/* 548 */     gridData = new GridData();
/* 549 */     gridData.horizontalSpan = 3;
/* 550 */     debug_au.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 554 */     spacer = new Label(cSection, 0);
/* 555 */     gridData = new GridData();
/* 556 */     gridData.horizontalSpan = 3;
/* 557 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 562 */     Group azWiki = new Group(cSection, 64);
/* 563 */     gridData = new GridData();
/* 564 */     Utils.setLayoutData(azWiki, gridData);
/* 565 */     GridLayout layout = new GridLayout();
/* 566 */     layout.numColumns = 1;
/* 567 */     layout.marginHeight = 1;
/* 568 */     layout.marginWidth = 20;
/* 569 */     azWiki.setLayout(layout);
/*     */     
/* 571 */     azWiki.setText(MessageText.getString("Utils.link.visit"));
/*     */     
/* 573 */     Label linkLabel = new Label(azWiki, 0);
/* 574 */     linkLabel.setText(Constants.APP_NAME + " Wiki AutoSpeed (beta)");
/* 575 */     linkLabel.setData("http://wiki.vuze.com/w/Auto_Speed");
/* 576 */     linkLabel.setCursor(linkLabel.getDisplay().getSystemCursor(21));
/* 577 */     linkLabel.setForeground(Colors.blue);
/* 578 */     gridData = new GridData();
/* 579 */     Utils.setLayoutData(linkLabel, gridData);
/* 580 */     linkLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 582 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/* 585 */       public void mouseUp(MouseEvent arg0) { Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/* 587 */     });
/* 588 */     ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     
/* 590 */     return cSection;
/*     */   }
/*     */   
/*     */   static class ConvertToLongChangeListener implements ParameterChangeListener
/*     */   {
/*     */     public void parameterChanged(Parameter p, boolean caused_internally)
/*     */     {
/*     */       try
/*     */       {
/* 599 */         String str = COConfigurationManager.getStringParameter("Auto_Upload_Speed_Version_String");
/* 600 */         long asLong = Long.parseLong(str);
/* 601 */         COConfigurationManager.setParameter("Auto Upload Speed Version", asLong);
/*     */       }
/*     */       catch (Throwable t) {
/* 604 */         COConfigurationManager.setParameter("Auto Upload Speed Version", 1);
/*     */       }
/*     */     }
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
/*     */     public void intParameterChanging(Parameter p, int toValue) {}
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
/*     */     public void booleanParameterChanging(Parameter p, boolean toValue) {}
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
/*     */     public void stringParameterChanging(Parameter p, String toValue) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void floatParameterChanging(Parameter owner, double toValue) {}
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
/*     */   protected String getMBitLimit(TransferStatsView.limitToTextHelper helper, long value)
/*     */   {
/* 662 */     return "(" + (value == 0L ? helper.getUnlimited() : DisplayFormatters.formatByteCountToBitsPerSec(value)) + ")";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTransferAutoSpeedSelect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */