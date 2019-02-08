/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
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
/*     */ public class ConfigSectionFileTorrentsDecoding
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final int REQUIRED_MODE = 2;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  45 */     return "torrents";
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  49 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  57 */     return "torrent.decoding";
/*     */   }
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
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  71 */     Composite cSection = new Composite(parent, 0);
/*  72 */     GridData gridData = new GridData(272);
/*     */     
/*  74 */     cSection.setLayoutData(gridData);
/*  75 */     GridLayout layout = new GridLayout();
/*  76 */     layout.numColumns = 2;
/*  77 */     cSection.setLayout(layout);
/*     */     
/*  79 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  80 */     if (userMode < 2) {
/*  81 */       Label label = new Label(cSection, 64);
/*  82 */       gridData = new GridData();
/*  83 */       label.setLayoutData(gridData);
/*     */       
/*  85 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*     */       
/*  91 */       if (2 < modeKeys.length) {
/*  92 */         param1 = MessageText.getString(modeKeys[2]);
/*     */       } else
/*  94 */         param1 = String.valueOf(2);
/*     */       String param2;
/*  96 */       String param2; if (userMode < modeKeys.length) {
/*  97 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/*  99 */         param2 = String.valueOf(userMode);
/*     */       }
/* 101 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/* 104 */       return cSection;
/*     */     }
/*     */     
/*     */ 
/* 108 */     Label label = new Label(cSection, 0);
/* 109 */     Messages.setLanguageText(label, "ConfigView.section.file.decoder.label");
/*     */     
/* 111 */     LocaleUtilDecoder[] decoders = LocaleUtil.getSingleton().getDecoders();
/*     */     
/* 113 */     String[] decoderLabels = new String[decoders.length + 1];
/* 114 */     String[] decoderValues = new String[decoders.length + 1];
/*     */     
/* 116 */     decoderLabels[0] = MessageText.getString("ConfigView.section.file.decoder.nodecoder");
/* 117 */     decoderValues[0] = "";
/*     */     
/* 119 */     for (int i = 1; i <= decoders.length; i++) {
/* 120 */       decoderLabels[i] = (decoderValues[i] = decoders[(i - 1)].getName());
/*     */     }
/* 122 */     new StringListParameter(cSection, "File.Decoder.Default", "", decoderLabels, decoderValues);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 127 */     gridData = new GridData();
/* 128 */     gridData.horizontalSpan = 2;
/* 129 */     new BooleanParameter(cSection, "File.Decoder.Prompt", "ConfigView.section.file.decoder.prompt").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 134 */     gridData = new GridData();
/* 135 */     gridData.horizontalSpan = 2;
/* 136 */     new BooleanParameter(cSection, "File.Decoder.ShowLax", "ConfigView.section.file.decoder.showlax").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 141 */     gridData = new GridData();
/* 142 */     gridData.horizontalSpan = 2;
/* 143 */     new BooleanParameter(cSection, "File.Decoder.ShowAll", "ConfigView.section.file.decoder.showall").setLayoutData(gridData);
/*     */     
/*     */ 
/* 146 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionFileTorrentsDecoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */