/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationDefaults;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationParameterNotFoundException;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeListener;
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
/*     */ public class ConfigSectionTransferAutoSpeedBeta
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private Group commentGroup;
/*     */   private Group uploadCapGroup;
/*     */   private Group dhtGroup;
/*     */   private IntParameter adjustmentInterval;
/*     */   private BooleanParameter skipAfterAdjustment;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  68 */     return "transfer.select";
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
/*  79 */     return "transfer.select.v2";
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
/* 100 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/* 109 */     Composite cSection = new Composite(parent, 0);
/*     */     
/* 111 */     GridData gridData = new GridData(272);
/* 112 */     cSection.setLayoutData(gridData);
/* 113 */     GridLayout subPanel = new GridLayout();
/* 114 */     subPanel.numColumns = 3;
/* 115 */     cSection.setLayout(subPanel);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */     this.commentGroup = new Group(cSection, 0);
/* 123 */     Messages.setLanguageText(this.commentGroup, "ConfigTransferAutoSpeed.add.comment.to.log.group");
/* 124 */     GridLayout commentLayout = new GridLayout();
/* 125 */     commentLayout.numColumns = 3;
/* 126 */     this.commentGroup.setLayout(commentLayout);
/* 127 */     gridData = new GridData(768);
/* 128 */     this.commentGroup.setLayoutData(gridData);
/*     */     
/*     */ 
/* 131 */     Label commentLabel = new Label(this.commentGroup, 0);
/* 132 */     Messages.setLanguageText(commentLabel, "ConfigTransferAutoSpeed.add.comment.to.log");
/* 133 */     gridData = new GridData();
/* 134 */     gridData.horizontalSpan = 1;
/* 135 */     commentLabel.setLayoutData(gridData);
/*     */     
/*     */ 
/* 138 */     final Text commentBox = new Text(this.commentGroup, 2048);
/* 139 */     gridData = new GridData(768);
/* 140 */     gridData.horizontalSpan = 1;
/* 141 */     commentBox.setText("");
/* 142 */     commentBox.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 146 */     Button commentButton = new Button(this.commentGroup, 8);
/*     */     
/* 148 */     gridData = new GridData();
/* 149 */     gridData.horizontalSpan = 1;
/* 150 */     commentButton.setLayoutData(gridData);
/* 151 */     Messages.setLanguageText(commentButton, "ConfigTransferAutoSpeed.log.button");
/* 152 */     commentButton.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 155 */         AEDiagnosticsLogger dLog = AEDiagnostics.getLogger("AutoSpeed");
/* 156 */         String comment = commentBox.getText();
/* 157 */         if ((comment != null) && 
/* 158 */           (comment.length() > 0)) {
/* 159 */           dLog.log("user-comment:" + comment);
/* 160 */           commentBox.setText("");
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 166 */     });
/* 167 */     Label commentSpacer = new Label(cSection, 0);
/* 168 */     gridData = new GridData();
/* 169 */     gridData.horizontalSpan = 3;
/* 170 */     commentSpacer.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */     this.uploadCapGroup = new Group(cSection, 0);
/* 178 */     Messages.setLanguageText(this.uploadCapGroup, "ConfigTransferAutoSpeed.upload.capacity.usage");
/*     */     
/* 180 */     GridLayout uCapLayout = new GridLayout();
/* 181 */     uCapLayout.numColumns = 2;
/* 182 */     this.uploadCapGroup.setLayout(uCapLayout);
/*     */     
/* 184 */     gridData = new GridData(768);
/* 185 */     gridData.horizontalSpan = 3;
/* 186 */     this.uploadCapGroup.setLayoutData(gridData);
/*     */     
/*     */ 
/* 189 */     Label upCapModeLbl = new Label(this.uploadCapGroup, 0);
/* 190 */     gridData = new GridData();
/* 191 */     upCapModeLbl.setLayoutData(gridData);
/* 192 */     Messages.setLanguageText(upCapModeLbl, "ConfigTransferAutoSpeed.mode");
/*     */     
/*     */ 
/* 195 */     Label ucSetLbl = new Label(this.uploadCapGroup, 0);
/* 196 */     gridData = new GridData();
/* 197 */     gridData.horizontalSpan = 2;
/* 198 */     Messages.setLanguageText(ucSetLbl, "ConfigTransferAutoSpeed.capacity.used");
/*     */     
/* 200 */     Label dlModeLbl = new Label(this.uploadCapGroup, 0);
/* 201 */     gridData = new GridData();
/* 202 */     Messages.setLanguageText(dlModeLbl, "ConfigTransferAutoSpeed.while.downloading");
/*     */     
/*     */ 
/* 205 */     String[] downloadModeNames = { " 80%", " 70%", " 60%", " 50%" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 212 */     int[] downloadModeValues = { 80, 70, 60, 50 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 219 */     new IntListParameter(this.uploadCapGroup, "SpeedLimitMonitor.setting.upload.used.download.mode", downloadModeNames, downloadModeValues);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 225 */     Label cSpacer = new Label(cSection, 0);
/* 226 */     gridData = new GridData();
/* 227 */     gridData.horizontalSpan = 4;
/* 228 */     cSpacer.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 234 */     this.dhtGroup = new Group(cSection, 0);
/* 235 */     Messages.setLanguageText(this.dhtGroup, "ConfigTransferAutoSpeed.data.update.frequency");
/* 236 */     this.dhtGroup.setLayout(subPanel);
/*     */     
/* 238 */     gridData = new GridData(768);
/* 239 */     gridData.horizontalSpan = 3;
/* 240 */     this.dhtGroup.setLayoutData(gridData);
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
/*     */ 
/*     */ 
/*     */ 
/* 300 */     Label iCount = new Label(this.dhtGroup, 0);
/* 301 */     gridData = new GridData();
/* 302 */     gridData.horizontalSpan = 2;
/* 303 */     gridData.horizontalAlignment = 1;
/* 304 */     iCount.setLayoutData(gridData);
/*     */     
/* 306 */     Messages.setLanguageText(iCount, "ConfigTransferAutoSpeed.adjustment.interval");
/*     */     
/* 308 */     this.adjustmentInterval = new IntParameter(this.dhtGroup, "SpeedManagerAlgorithmProviderV2.intervals.between.adjust");
/* 309 */     gridData = new GridData();
/* 310 */     this.adjustmentInterval.setLayoutData(gridData);
/*     */     
/*     */ 
/* 313 */     cSpacer = new Label(cSection, 0);
/* 314 */     gridData = new GridData();
/* 315 */     gridData.horizontalSpan = 1;
/* 316 */     cSpacer.setLayoutData(gridData);
/*     */     
/*     */ 
/* 319 */     Label skip = new Label(this.dhtGroup, 0);
/* 320 */     gridData = new GridData();
/* 321 */     gridData.horizontalSpan = 2;
/* 322 */     gridData.horizontalAlignment = 1;
/* 323 */     skip.setLayoutData(gridData);
/*     */     
/* 325 */     Messages.setLanguageText(skip, "ConfigTransferAutoSpeed.skip.after.adjust");
/*     */     
/* 327 */     this.skipAfterAdjustment = new BooleanParameter(this.dhtGroup, "SpeedManagerAlgorithmProviderV2.setting.wait.after.adjust");
/* 328 */     gridData = new GridData();
/* 329 */     this.skipAfterAdjustment.setLayoutData(gridData);
/*     */     
/*     */ 
/* 332 */     cSpacer = new Label(cSection, 0);
/* 333 */     gridData = new GridData();
/* 334 */     gridData.horizontalSpan = 3;
/* 335 */     cSpacer.setLayoutData(gridData);
/*     */     
/* 337 */     return cSection;
/*     */   }
/*     */   
/*     */   void enableGroups(String strategyListValue)
/*     */   {
/* 342 */     if (strategyListValue == null) {
/* 343 */       return;
/*     */     }
/*     */     
/*     */ 
/* 347 */     boolean isBothEnabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/* 348 */     boolean isSeedingEnabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Seeding Enabled");
/* 349 */     long version = COConfigurationManager.getLongParameter("Auto Upload Speed Version");
/*     */     
/* 351 */     boolean isV2Enabled = false;
/* 352 */     if (((isBothEnabled) || (isSeedingEnabled)) && (version == 2L)) {
/* 353 */       isV2Enabled = true;
/*     */     }
/*     */     
/* 356 */     if (this.commentGroup != null) {
/* 357 */       if (isV2Enabled)
/*     */       {
/* 359 */         this.commentGroup.setEnabled(true);
/* 360 */         this.commentGroup.setVisible(true);
/*     */       }
/*     */       else {
/* 363 */         this.commentGroup.setEnabled(false);
/* 364 */         this.commentGroup.setVisible(false);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static class GroupModeChangeListener
/*     */     implements ParameterChangeListener
/*     */   {
/*     */     public void parameterChanged(Parameter p, boolean caused_internally) {}
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
/*     */     public void booleanParameterChanging(Parameter p, boolean toValue) {}
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
/*     */     public void floatParameterChanging(Parameter owner, double toValue) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class RestoreDefaultsListener
/*     */     implements Listener
/*     */   {
/*     */     public void handleEvent(Event event)
/*     */     {
/* 410 */       ConfigurationDefaults configDefs = ConfigurationDefaults.getInstance();
/*     */       try
/*     */       {
/* 413 */         long downMax = configDefs.getLongParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit");
/* 414 */         String downConf = configDefs.getStringParameter("SpeedLimitMonitor.setting.download.limit.conf");
/* 415 */         long upMax = configDefs.getLongParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit");
/* 416 */         String upConf = configDefs.getStringParameter("SpeedLimitMonitor.setting.upload.limit.conf");
/*     */         
/* 418 */         COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", downMax);
/* 419 */         COConfigurationManager.setParameter("SpeedLimitMonitor.setting.download.limit.conf", downConf);
/* 420 */         COConfigurationManager.setParameter("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", upMax);
/* 421 */         COConfigurationManager.setParameter("SpeedLimitMonitor.setting.upload.limit.conf", upConf);
/*     */       }
/*     */       catch (ConfigurationParameterNotFoundException cpnfe) {}
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTransferAutoSpeedBeta.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */