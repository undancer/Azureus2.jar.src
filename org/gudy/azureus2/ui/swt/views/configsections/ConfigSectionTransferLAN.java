/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
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
/*     */ public class ConfigSectionTransferLAN
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.transfer.lan.";
/*     */   private static final int REQUIRED_MODE = 2;
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  45 */     return 2;
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  50 */     return "transfer";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  54 */     return "transfer.lan";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  66 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  68 */     GridData gridData = new GridData(272);
/*  69 */     cSection.setLayoutData(gridData);
/*  70 */     GridLayout advanced_layout = new GridLayout();
/*  71 */     advanced_layout.numColumns = 2;
/*  72 */     cSection.setLayout(advanced_layout);
/*     */     
/*  74 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  75 */     if (userMode < 2) {
/*  76 */       Label label = new Label(cSection, 64);
/*  77 */       gridData = new GridData();
/*  78 */       gridData.horizontalSpan = 2;
/*  79 */       label.setLayoutData(gridData);
/*     */       
/*  81 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*  86 */       if (2 < modeKeys.length) {
/*  87 */         param1 = MessageText.getString(modeKeys[2]);
/*     */       } else
/*  89 */         param1 = String.valueOf(2);
/*     */       String param2;
/*  91 */       String param2; if (userMode < modeKeys.length) {
/*  92 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/*  94 */         param2 = String.valueOf(userMode);
/*     */       }
/*  96 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/*  99 */       return cSection;
/*     */     }
/*     */     
/*     */ 
/* 103 */     BooleanParameter enable_lan = new BooleanParameter(cSection, "LAN Speed Enabled", "ConfigView.section.transfer.lan.enable");
/*     */     
/*     */ 
/* 106 */     gridData = new GridData();
/* 107 */     gridData.horizontalSpan = 2;
/* 108 */     enable_lan.setLayoutData(gridData);
/*     */     
/* 110 */     IntParameter lan_max_upload = new IntParameter(cSection, "Max LAN Upload Speed KBs");
/* 111 */     gridData = new GridData();
/* 112 */     lan_max_upload.setLayoutData(gridData);
/* 113 */     Label llmux = new Label(cSection, 0);
/* 114 */     Messages.setLanguageText(llmux, "ConfigView.section.transfer.lan.uploadrate");
/*     */     
/*     */ 
/* 117 */     IntParameter lan_max_download = new IntParameter(cSection, "Max LAN Download Speed KBs");
/* 118 */     gridData = new GridData();
/* 119 */     lan_max_download.setLayoutData(gridData);
/* 120 */     Label llmdx = new Label(cSection, 0);
/* 121 */     Messages.setLanguageText(llmdx, "ConfigView.section.transfer.lan.downloadrate");
/*     */     
/* 123 */     enable_lan.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Parameter[] { lan_max_upload, lan_max_download }));
/*     */     
/* 125 */     enable_lan.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { llmux, llmdx }));
/*     */     
/*     */ 
/*     */ 
/* 129 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTransferLAN.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */