/*     */ package org.gudy.azureus2.ui.swt.speedtest;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.views.stats.TransferStatsView.limitToTextHelper;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
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
/*     */ public class SpeedTestSetLimitPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   private int measuredUploadKbps;
/*     */   private int measuredDownloadKbps;
/*     */   private boolean downloadTestRan;
/*  50 */   private boolean uploadTestRan = true;
/*     */   
/*     */   private boolean downloadHitLimit;
/*     */   
/*     */   private boolean uploadHitLimit;
/*     */   private Button apply;
/*     */   private Combo downConfLevelCombo;
/*     */   private Combo upConfLevelCombo;
/*     */   private SpeedManager speedManager;
/*     */   private TransferStatsView.limitToTextHelper helper;
/*     */   
/*     */   public SpeedTestSetLimitPanel(Wizard wizard, IWizardPanel previousPanel, int upload, long maxup, int download, long maxdown)
/*     */   {
/*  63 */     super(wizard, previousPanel);
/*     */     
/*  65 */     this.downloadHitLimit = (download > maxdown - 20480L);
/*  66 */     this.uploadHitLimit = (upload > maxup - 20480L);
/*     */     
/*  68 */     this.measuredUploadKbps = (upload / 1024);
/*  69 */     if (this.measuredUploadKbps < 5) {
/*  70 */       this.uploadTestRan = false;
/*     */     }
/*     */     
/*     */ 
/*  74 */     this.measuredDownloadKbps = (download / 1024);
/*  75 */     if (this.measuredDownloadKbps < 5) {
/*  76 */       this.downloadTestRan = false;
/*     */     }
/*     */     
/*  79 */     this.speedManager = AzureusCoreFactory.getSingleton().getSpeedManager();
/*  80 */     this.helper = new TransferStatsView.limitToTextHelper();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  90 */     this.wizard.setTitle(MessageText.getString("SpeedTestWizard.set.upload.title"));
/*  91 */     this.wizard.setCurrentInfo(MessageText.getString("SpeedTestWizard.set.upload.hint"));
/*     */     
/*  93 */     Composite rootPanel = this.wizard.getPanel();
/*  94 */     GridLayout layout = new GridLayout();
/*  95 */     layout.numColumns = 1;
/*  96 */     rootPanel.setLayout(layout);
/*     */     
/*  98 */     Composite panel = new Composite(rootPanel, 0);
/*  99 */     GridData gridData = new GridData(768);
/* 100 */     Utils.setLayoutData(panel, gridData);
/*     */     
/* 102 */     layout = new GridLayout();
/* 103 */     layout.numColumns = 4;
/* 104 */     panel.setLayout(layout);
/*     */     
/* 106 */     Label explain = new Label(panel, 64);
/* 107 */     gridData = new GridData(768);
/* 108 */     gridData.horizontalSpan = 4;
/* 109 */     Utils.setLayoutData(explain, gridData);
/* 110 */     Messages.setLanguageText(explain, "SpeedTestWizard.set.upload.panel.explain");
/*     */     
/*     */ 
/* 113 */     Label spacer = new Label(panel, 0);
/* 114 */     gridData = new GridData();
/* 115 */     gridData.horizontalSpan = 4;
/* 116 */     Utils.setLayoutData(spacer, gridData);
/*     */     
/* 118 */     Label spacer1 = new Label(panel, 0);
/* 119 */     gridData = new GridData();
/* 120 */     spacer1.setLayoutData(gridData);
/*     */     
/* 122 */     Label bytesCol = new Label(panel, 0);
/* 123 */     gridData = new GridData();
/* 124 */     gridData.widthHint = 80;
/* 125 */     Utils.setLayoutData(bytesCol, gridData);
/* 126 */     Messages.setLanguageText(bytesCol, "SpeedTestWizard.set.upload.bytes.per.sec");
/*     */     
/* 128 */     Label bitsCol = new Label(panel, 0);
/* 129 */     gridData = new GridData();
/* 130 */     gridData.widthHint = 80;
/* 131 */     Utils.setLayoutData(bitsCol, gridData);
/* 132 */     Messages.setLanguageText(bitsCol, "SpeedTestWizard.set.upload.bits.per.sec");
/*     */     
/* 134 */     Label confLevel = new Label(panel, 0);
/* 135 */     gridData = new GridData();
/* 136 */     gridData.widthHint = 80;
/* 137 */     Utils.setLayoutData(confLevel, gridData);
/* 138 */     Messages.setLanguageText(confLevel, "SpeedTestWizard.set.limit.conf.level");
/*     */     
/*     */ 
/* 141 */     Label ul = new Label(panel, 0);
/* 142 */     gridData = new GridData();
/* 143 */     Utils.setLayoutData(ul, gridData);
/* 144 */     Messages.setLanguageText(ul, "SpeedView.stats.estupcap", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 149 */     final Text uploadLimitSetting = new Text(panel, 2048);
/* 150 */     gridData = new GridData(1);
/* 151 */     gridData.widthHint = 80;
/* 152 */     Utils.setLayoutData(uploadLimitSetting, gridData);
/*     */     
/* 154 */     int uploadCapacity = determineRateSettingEx(this.measuredUploadKbps, this.uploadTestRan, true);
/*     */     
/*     */ 
/* 157 */     if (uploadCapacity < 20) {
/* 158 */       uploadCapacity = 20;
/*     */     }
/* 160 */     uploadLimitSetting.setText("" + uploadCapacity);
/* 161 */     uploadLimitSetting.addListener(25, new NumberListener(uploadLimitSetting));
/*     */     
/*     */ 
/*     */ 
/* 165 */     Label echo = new Label(panel, 0);
/* 166 */     gridData = new GridData();
/* 167 */     gridData.horizontalSpan = 1;
/* 168 */     gridData.widthHint = 80;
/* 169 */     Utils.setLayoutData(echo, gridData);
/* 170 */     echo.setText(DisplayFormatters.formatByteCountToBitsPerSec(uploadCapacity * 1024));
/*     */     
/*     */ 
/*     */ 
/* 174 */     uploadLimitSetting.addListener(24, new ByteConversionListener(echo, uploadLimitSetting));
/*     */     
/*     */ 
/* 177 */     String[] confName = this.helper.getSettableTypes();
/* 178 */     final String[] confValue = this.helper.getSettableTypes();
/*     */     
/*     */ 
/* 181 */     int uploadDropIndex = setDefaultConfidenceLevelEx(this.measuredUploadKbps, this.uploadTestRan, true, confValue);
/* 182 */     this.upConfLevelCombo = new Combo(panel, 8);
/* 183 */     addDropElements(this.upConfLevelCombo, confName);
/* 184 */     this.upConfLevelCombo.select(uploadDropIndex);
/*     */     
/*     */ 
/*     */ 
/* 188 */     Label dl = new Label(panel, 0);
/* 189 */     gridData = new GridData();
/* 190 */     Utils.setLayoutData(dl, gridData);
/* 191 */     Messages.setLanguageText(dl, "SpeedView.stats.estdowncap", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 196 */     final Text downloadLimitSetting = new Text(panel, 2048);
/* 197 */     gridData = new GridData(1);
/* 198 */     gridData.widthHint = 80;
/* 199 */     Utils.setLayoutData(downloadLimitSetting, gridData);
/*     */     
/* 201 */     int bestDownloadSetting = determineRateSettingEx(this.measuredDownloadKbps, this.downloadTestRan, false);
/*     */     
/* 203 */     downloadLimitSetting.setText("" + bestDownloadSetting);
/* 204 */     downloadLimitSetting.addListener(25, new NumberListener(downloadLimitSetting));
/*     */     
/*     */ 
/* 207 */     Label downEcho = new Label(panel, 0);
/* 208 */     gridData = new GridData();
/* 209 */     gridData.horizontalSpan = 1;
/* 210 */     gridData.widthHint = 80;
/* 211 */     Utils.setLayoutData(downEcho, gridData);
/* 212 */     downEcho.setText(DisplayFormatters.formatByteCountToBitsPerSec(bestDownloadSetting * 1024));
/*     */     
/*     */ 
/* 215 */     downloadLimitSetting.addListener(24, new ByteConversionListener(downEcho, downloadLimitSetting));
/* 216 */     int downIndex = setDefaultConfidenceLevelEx(this.measuredDownloadKbps, this.downloadTestRan, false, confValue);
/*     */     
/* 218 */     this.downConfLevelCombo = new Combo(panel, 8);
/* 219 */     addDropElements(this.downConfLevelCombo, confName);
/* 220 */     this.downConfLevelCombo.select(downIndex);
/*     */     
/*     */ 
/* 223 */     Label c1 = new Label(panel, 0);
/* 224 */     gridData = new GridData();
/* 225 */     gridData.horizontalSpan = 1;
/* 226 */     gridData.widthHint = 80;
/* 227 */     c1.setLayoutData(gridData);
/*     */     
/* 229 */     SpeedManager sm = AzureusCoreFactory.getSingleton().getSpeedManager();
/*     */     
/* 231 */     if (this.uploadTestRan)
/*     */     {
/*     */ 
/* 234 */       sm.setEstimatedUploadCapacityBytesPerSec(this.measuredUploadKbps * 1024, this.uploadHitLimit ? 0.0F : 0.0F);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 240 */     if (this.downloadTestRan)
/*     */     {
/* 242 */       sm.setEstimatedDownloadCapacityBytesPerSec(this.measuredDownloadKbps * 1024, this.downloadHitLimit ? 0.8F : 0.9F);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 248 */     this.apply = new Button(panel, 8);
/* 249 */     Messages.setLanguageText(this.apply, "SpeedTestWizard.set.upload.button.apply");
/* 250 */     gridData = new GridData();
/* 251 */     gridData.widthHint = 70;
/* 252 */     Utils.setLayoutData(this.apply, gridData);
/* 253 */     this.apply.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event)
/*     */       {
/* 257 */         int uploadLimitKBPS = Integer.parseInt(uploadLimitSetting.getText());
/* 258 */         int downlaodLimitKBPS = Integer.parseInt(downloadLimitSetting.getText());
/*     */         
/* 260 */         if (uploadLimitKBPS < 20) {
/* 261 */           uploadLimitKBPS = 20;
/*     */         }
/*     */         
/*     */ 
/* 265 */         if (downlaodLimitKBPS < uploadLimitKBPS) {
/* 266 */           downlaodLimitKBPS = uploadLimitKBPS;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 271 */         COConfigurationManager.setParameter("Auto Upload Speed Enabled", false);
/* 272 */         COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", false);
/*     */         
/*     */ 
/*     */ 
/* 276 */         COConfigurationManager.setParameter("AutoSpeed Max Upload KBs", uploadLimitKBPS);
/* 277 */         COConfigurationManager.setParameter("Max Upload Speed KBs", uploadLimitKBPS);
/* 278 */         COConfigurationManager.setParameter("Max Upload Speed Seeding KBs", uploadLimitKBPS);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 283 */         if (SpeedTestSetLimitPanel.this.downloadTestRan) {
/* 284 */           int dIndex = SpeedTestSetLimitPanel.this.downConfLevelCombo.getSelectionIndex();
/* 285 */           float downEstType = SpeedTestSetLimitPanel.this.helper.textToType(confValue[dIndex]);
/* 286 */           SpeedTestSetLimitPanel.this.speedManager.setEstimatedUploadCapacityBytesPerSec(downlaodLimitKBPS, downEstType);
/*     */         }
/* 288 */         if (SpeedTestSetLimitPanel.this.uploadTestRan) {
/* 289 */           int uIndex = SpeedTestSetLimitPanel.this.upConfLevelCombo.getSelectionIndex();
/* 290 */           float upEstType = SpeedTestSetLimitPanel.this.helper.textToType(confValue[uIndex]);
/* 291 */           SpeedTestSetLimitPanel.this.speedManager.setEstimatedUploadCapacityBytesPerSec(uploadLimitKBPS, upEstType);
/*     */         }
/*     */         
/* 294 */         SpeedTestSetLimitPanel.this.wizard.setFinishEnabled(true);
/* 295 */         SpeedTestSetLimitPanel.this.wizard.setPreviousEnabled(false);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 300 */     });
/* 301 */     Label c3 = new Label(panel, 0);
/* 302 */     gridData = new GridData();
/* 303 */     gridData.horizontalSpan = 1;
/* 304 */     c3.setLayoutData(gridData);
/*     */     
/*     */ 
/* 307 */     Label spacer2 = new Label(panel, 0);
/* 308 */     gridData = new GridData();
/* 309 */     gridData.horizontalSpan = 3;
/* 310 */     spacer2.setLayoutData(gridData);
/*     */     
/*     */ 
/* 313 */     Composite resultsPanel = new Composite(rootPanel, 0);
/* 314 */     gridData = new GridData(776);
/* 315 */     Utils.setLayoutData(resultsPanel, gridData);
/*     */     
/* 317 */     layout = new GridLayout();
/* 318 */     layout.numColumns = 5;
/* 319 */     layout.makeColumnsEqualWidth = true;
/* 320 */     resultsPanel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 324 */     NetworkAdminSpeedTesterResult result = SpeedTestData.getInstance().getLastResult();
/* 325 */     if (result.hadError())
/*     */     {
/* 327 */       String error = result.getLastError();
/* 328 */       createResultLabels(resultsPanel, true);
/* 329 */       createErrorDesc(resultsPanel, error);
/* 330 */       createTestDesc(resultsPanel);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 335 */       int upload = result.getUploadSpeed();
/* 336 */       int download = result.getDownloadSpeed();
/*     */       
/* 338 */       createResultLabels(resultsPanel, false);
/* 339 */       createResultData(resultsPanel, MessageText.getString("GeneralView.label.uploadspeed"), upload);
/* 340 */       createResultData(resultsPanel, MessageText.getString("GeneralView.label.downloadspeed"), download);
/* 341 */       createTestDesc(resultsPanel);
/*     */     }
/*     */   }
/*     */   
/*     */   private void addDropElements(Combo combo, String[] elements)
/*     */   {
/* 347 */     if (elements == null) {
/* 348 */       return;
/*     */     }
/*     */     
/* 351 */     int n = elements.length;
/* 352 */     for (int i = 0; i < n; i++) {
/* 353 */       combo.add(elements[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int setDefaultConfidenceLevelEx(int transferRateKBPS, boolean testRan, boolean isUpload, String[] values)
/*     */   {
/*     */     SpeedManagerLimitEstimate est;
/*     */     
/*     */ 
/*     */ 
/*     */     SpeedManagerLimitEstimate est;
/*     */     
/*     */ 
/* 369 */     if (isUpload) {
/* 370 */       est = this.speedManager.getEstimatedUploadCapacityBytesPerSec();
/*     */     } else {
/* 372 */       est = this.speedManager.getEstimatedDownloadCapacityBytesPerSec();
/*     */     }
/* 374 */     float originalEstType = est.getEstimateType();
/*     */     float retValType;
/*     */     float retValType;
/* 377 */     if (originalEstType == 1.0F) {
/* 378 */       retValType = originalEstType; } else { float retValType;
/* 379 */       if (!testRan)
/*     */       {
/* 381 */         retValType = originalEstType; } else { float retValType;
/* 382 */         if (isUpload)
/*     */         {
/* 384 */           retValType = 0.0F; } else { float retValType;
/* 385 */           if ((transferRateKBPS < 550) && (transferRateKBPS > 450)) {
/* 386 */             retValType = 0.0F;
/*     */           }
/*     */           else
/* 389 */             retValType = 0.9F;
/*     */         }
/*     */       } }
/* 392 */     String cType = this.helper.typeToText(retValType);
/*     */     
/*     */ 
/* 395 */     if (cType == null) {
/* 396 */       return -1;
/*     */     }
/*     */     
/* 399 */     for (int i = 0; i < values.length; i++) {
/* 400 */       if (cType.equalsIgnoreCase(values[i])) {
/* 401 */         return i;
/*     */       }
/*     */     }
/*     */     
/* 405 */     return -1;
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
/*     */   private void createResultLabels(Composite panel, boolean hadError)
/*     */   {
/* 419 */     Label c1 = new Label(panel, 0);
/* 420 */     GridData gridData = new GridData();
/* 421 */     gridData.horizontalSpan = 1;
/* 422 */     c1.setLayoutData(gridData);
/*     */     
/*     */ 
/* 425 */     Label c2 = new Label(panel, 0);
/* 426 */     gridData = new GridData();
/* 427 */     gridData.horizontalSpan = 1;
/* 428 */     gridData.horizontalAlignment = 3;
/* 429 */     c2.setLayoutData(gridData);
/* 430 */     c2.setText(MessageText.getString("SpeedTestWizard.set.upload.result"));
/*     */     
/*     */ 
/*     */ 
/* 434 */     Label c3 = new Label(panel, 0);
/* 435 */     gridData = new GridData();
/* 436 */     gridData.horizontalSpan = 1;
/* 437 */     gridData.horizontalAlignment = 2;
/* 438 */     c3.setLayoutData(gridData);
/* 439 */     if (!hadError) {
/* 440 */       c3.setText(MessageText.getString("SpeedTestWizard.set.upload.bytes.per.sec"));
/*     */     }
/*     */     
/*     */ 
/* 444 */     Label c4 = new Label(panel, 0);
/* 445 */     gridData = new GridData();
/* 446 */     gridData.horizontalSpan = 1;
/* 447 */     gridData.horizontalAlignment = 2;
/* 448 */     c4.setLayoutData(gridData);
/* 449 */     if (!hadError) {
/* 450 */       c4.setText(MessageText.getString("SpeedTestWizard.set.upload.bits.per.sec"));
/*     */     }
/*     */     
/*     */ 
/* 454 */     Label c5 = new Label(panel, 0);
/* 455 */     gridData = new GridData();
/* 456 */     gridData.horizontalSpan = 1;
/* 457 */     c5.setLayoutData(gridData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createResultData(Composite panel, String label, int rate)
/*     */   {
/* 465 */     Label c1 = new Label(panel, 0);
/* 466 */     GridData gridData = new GridData();
/* 467 */     gridData.horizontalSpan = 1;
/* 468 */     c1.setLayoutData(gridData);
/*     */     
/*     */ 
/* 471 */     Label c2 = new Label(panel, 0);
/* 472 */     gridData = new GridData();
/* 473 */     gridData.horizontalSpan = 1;
/* 474 */     gridData.horizontalAlignment = 3;
/* 475 */     c2.setLayoutData(gridData);
/* 476 */     c2.setText(label);
/*     */     
/*     */ 
/*     */ 
/* 480 */     Label c3 = new Label(panel, 0);
/* 481 */     gridData = new GridData();
/* 482 */     gridData.horizontalSpan = 1;
/* 483 */     gridData.horizontalAlignment = 2;
/* 484 */     c3.setLayoutData(gridData);
/* 485 */     c3.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(rate));
/*     */     
/*     */ 
/* 488 */     Label c4 = new Label(panel, 0);
/* 489 */     gridData = new GridData();
/* 490 */     gridData.horizontalSpan = 1;
/* 491 */     gridData.horizontalAlignment = 2;
/* 492 */     c4.setLayoutData(gridData);
/* 493 */     c4.setText(DisplayFormatters.formatByteCountToBitsPerSec(rate));
/*     */     
/*     */ 
/* 496 */     Label c5 = new Label(panel, 0);
/* 497 */     gridData = new GridData();
/* 498 */     gridData.horizontalSpan = 1;
/* 499 */     c5.setLayoutData(gridData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void createTestDesc(Composite panel) {}
/*     */   
/*     */ 
/*     */ 
/*     */   private void createErrorDesc(Composite panel, String error) {}
/*     */   
/*     */ 
/*     */   public int determineRateSettingEx(int measuredRate, boolean testRan, boolean isUpload)
/*     */   {
/* 513 */     int retVal = measuredRate;
/*     */     
/*     */     SpeedManagerLimitEstimate est;
/*     */     SpeedManagerLimitEstimate est;
/* 517 */     if (isUpload) {
/* 518 */       est = this.speedManager.getEstimatedUploadCapacityBytesPerSec();
/*     */     } else {
/* 520 */       est = this.speedManager.getEstimatedDownloadCapacityBytesPerSec();
/*     */     }
/*     */     
/*     */ 
/* 524 */     if (!testRan) {
/* 525 */       retVal = est.getBytesPerSec() / 1024;
/*     */     }
/*     */     
/*     */ 
/* 529 */     if (est.getEstimateType() == 1.0F) {
/* 530 */       retVal = est.getBytesPerSec() / 1024;
/*     */     }
/*     */     
/* 533 */     return retVal;
/*     */   }
/*     */   
/*     */   public void finish()
/*     */   {
/* 538 */     this.wizard.switchToClose();
/*     */   }
/*     */   
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 543 */     return new SpeedTestFinishPanel(this.wizard, this);
/*     */   }
/*     */   
/*     */   public boolean isFinishEnabled() {
/* 547 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isNextEnabled()
/*     */   {
/* 552 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   static class ByteConversionListener
/*     */     implements Listener
/*     */   {
/*     */     final Label echoLbl;
/*     */     final Text setting;
/*     */     
/*     */     public ByteConversionListener(Label _echoLbl, Text _setting)
/*     */     {
/* 564 */       this.echoLbl = _echoLbl;
/* 565 */       this.setting = _setting;
/*     */     }
/*     */     
/*     */     public void handleEvent(Event e) {
/* 569 */       String newVal = this.setting.getText();
/*     */       try {
/* 571 */         int newValInt = Integer.parseInt(newVal);
/* 572 */         if (this.echoLbl != null) {
/* 573 */           this.echoLbl.setText(DisplayFormatters.formatByteCountToBitsPerSec(newValInt * 1024));
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {}
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static class NumberListener
/*     */     implements Listener
/*     */   {
/*     */     final Text setting;
/*     */     
/*     */     public NumberListener(Text _setting)
/*     */     {
/* 588 */       this.setting = _setting;
/*     */     }
/*     */     
/*     */     public void handleEvent(Event e) {
/* 592 */       String text = e.text;
/* 593 */       char[] chars = new char[text.length()];
/* 594 */       text.getChars(0, chars.length, chars, 0);
/* 595 */       for (int i = 0; i < chars.length; i++) {
/* 596 */         if (('0' > chars[i]) || (chars[i] > '9')) {
/* 597 */           e.doit = false;
/* 598 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/speedtest/SpeedTestSetLimitPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */