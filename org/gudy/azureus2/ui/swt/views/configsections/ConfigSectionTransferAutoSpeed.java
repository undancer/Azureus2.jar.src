/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.FloatParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
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
/*     */ public class ConfigSectionTransferAutoSpeed
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.transfer.autospeed.";
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  46 */     return "transfer.select";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  50 */     return "transfer.autospeed";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  60 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  67 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  69 */     GridData gridData = new GridData(272);
/*  70 */     cSection.setLayoutData(gridData);
/*  71 */     GridLayout advanced_layout = new GridLayout();
/*  72 */     advanced_layout.numColumns = 2;
/*  73 */     cSection.setLayout(advanced_layout);
/*     */     
/*  75 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/*  78 */     Label linfo = new Label(cSection, 64);
/*  79 */     Messages.setLanguageText(linfo, "ConfigView.section.transfer.autospeed.info");
/*  80 */     gridData = new GridData(768);
/*  81 */     gridData.horizontalSpan = 2;
/*  82 */     linfo.setLayoutData(gridData);
/*     */     
/*  84 */     gridData = new GridData();
/*  85 */     gridData.horizontalSpan = 2;
/*  86 */     new LinkLabel(cSection, gridData, "ConfigView.label.please.visit.here", "http://wiki.vuze.com/w/Auto_Speed");
/*     */     
/*     */ 
/*     */ 
/*  90 */     String[] units = { DisplayFormatters.getRateUnit(1) };
/*     */     
/*     */ 
/*     */ 
/*  94 */     Label llmux = new Label(cSection, 0);
/*  95 */     Messages.setLanguageText(llmux, "ConfigView.section.transfer.autospeed.minupload", units);
/*  96 */     IntParameter min_upload = new IntParameter(cSection, "AutoSpeed Min Upload KBs");
/*     */     
/*  98 */     gridData = new GridData();
/*  99 */     min_upload.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 103 */     Label llmdx = new Label(cSection, 0);
/* 104 */     Messages.setLanguageText(llmdx, "ConfigView.section.transfer.autospeed.maxupload", units);
/* 105 */     IntParameter max_upload = new IntParameter(cSection, "AutoSpeed Max Upload KBs");
/*     */     
/* 107 */     gridData = new GridData();
/* 108 */     max_upload.setLayoutData(gridData);
/*     */     
/*     */ 
/* 111 */     if (userMode > 0)
/*     */     {
/* 113 */       BooleanParameter enable_down_adj = new BooleanParameter(cSection, "AutoSpeed Download Adj Enable", "ConfigView.section.transfer.autospeed.enabledownadj");
/*     */       
/*     */ 
/* 116 */       gridData = new GridData();
/* 117 */       gridData.horizontalSpan = 2;
/* 118 */       enable_down_adj.setLayoutData(gridData);
/*     */       
/*     */ 
/* 121 */       Label label = new Label(cSection, 0);
/* 122 */       Messages.setLanguageText(label, "ConfigView.section.transfer.autospeed.downadjratio");
/*     */       
/* 124 */       FloatParameter down_adj = new FloatParameter(cSection, "AutoSpeed Download Adj Ratio", 0.0F, Float.MAX_VALUE, false, 2);
/* 125 */       gridData = new GridData();
/* 126 */       down_adj.setLayoutData(gridData);
/*     */       
/*     */ 
/* 129 */       enable_down_adj.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { down_adj.getControl() }));
/*     */     }
/*     */     
/*     */ 
/* 133 */     if (userMode > 1)
/*     */     {
/*     */ 
/*     */ 
/* 137 */       Label label = new Label(cSection, 0);
/* 138 */       Messages.setLanguageText(label, "ConfigView.section.transfer.autospeed.maxinc", units);
/*     */       
/* 140 */       final IntParameter max_increase = new IntParameter(cSection, "AutoSpeed Max Increment KBs");
/*     */       
/* 142 */       gridData = new GridData();
/* 143 */       max_increase.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 147 */       label = new Label(cSection, 0);
/* 148 */       Messages.setLanguageText(label, "ConfigView.section.transfer.autospeed.maxdec", units);
/*     */       
/* 150 */       final IntParameter max_decrease = new IntParameter(cSection, "AutoSpeed Max Decrement KBs");
/*     */       
/* 152 */       gridData = new GridData();
/* 153 */       max_decrease.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 158 */       label = new Label(cSection, 0);
/* 159 */       Messages.setLanguageText(label, "ConfigView.section.transfer.autospeed.chokeping");
/*     */       
/* 161 */       final IntParameter choke_ping = new IntParameter(cSection, "AutoSpeed Choking Ping Millis");
/*     */       
/* 163 */       gridData = new GridData();
/* 164 */       choke_ping.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 168 */       label = new Label(cSection, 0);
/* 169 */       Messages.setLanguageText(label, "ConfigView.section.transfer.autospeed.forcemin", units);
/*     */       
/* 171 */       final IntParameter forced_min = new IntParameter(cSection, "AutoSpeed Forced Min KBs");
/*     */       
/* 173 */       gridData = new GridData();
/* 174 */       forced_min.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 178 */       label = new Label(cSection, 0);
/* 179 */       Messages.setLanguageText(label, "ConfigView.section.transfer.autospeed.latencyfactor");
/*     */       
/* 181 */       final IntParameter latency_factor = new IntParameter(cSection, "AutoSpeed Latency Factor", 1, Integer.MAX_VALUE);
/*     */       
/* 183 */       gridData = new GridData();
/* 184 */       latency_factor.setLayoutData(gridData);
/*     */       
/* 186 */       Label reset_label = new Label(cSection, 0);
/* 187 */       Messages.setLanguageText(reset_label, "ConfigView.section.transfer.autospeed.reset");
/*     */       
/* 189 */       Button reset_button = new Button(cSection, 8);
/*     */       
/* 191 */       Messages.setLanguageText(reset_button, "ConfigView.section.transfer.autospeed.reset.button");
/*     */       
/* 193 */       reset_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 199 */           max_increase.resetToDefault();
/* 200 */           max_decrease.resetToDefault();
/* 201 */           choke_ping.resetToDefault();
/* 202 */           latency_factor.resetToDefault();
/* 203 */           forced_min.resetToDefault();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 208 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTransferAutoSpeed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */