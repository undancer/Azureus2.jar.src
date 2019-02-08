/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
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
/*     */ public class ConfigSectionSeedingIgnore
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  39 */     return "queue.seeding";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  43 */     return "queue.seeding.ignore";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  53 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  62 */     Composite cIgnoreRules = new Composite(parent, 0);
/*     */     
/*  64 */     GridLayout layout = new GridLayout();
/*  65 */     layout.numColumns = 3;
/*  66 */     layout.marginHeight = 0;
/*  67 */     cIgnoreRules.setLayout(layout);
/*     */     
/*  69 */     Label label = new Label(cIgnoreRules, 64);
/*  70 */     GridData gridData = new GridData(768);
/*  71 */     gridData.horizontalSpan = 3;
/*  72 */     gridData.widthHint = 300;
/*  73 */     Utils.setLayoutData(label, gridData);
/*  74 */     Messages.setLanguageText(label, "ConfigView.label.autoSeedingIgnoreInfo");
/*     */     
/*  76 */     Composite cIgnore = new Group(cIgnoreRules, 0);
/*  77 */     layout = new GridLayout();
/*  78 */     layout.numColumns = 3;
/*  79 */     layout.verticalSpacing = 6;
/*  80 */     cIgnore.setLayout(layout);
/*  81 */     gridData = new GridData(256);
/*  82 */     Utils.setLayoutData(cIgnore, gridData);
/*  83 */     Messages.setLanguageText(cIgnore, "ConfigView.label.seeding.ignore");
/*     */     
/*     */ 
/*  86 */     label = new Label(cIgnore, 0);
/*  87 */     Messages.setLanguageText(label, "ConfigView.label.ignoreSeeds");
/*  88 */     gridData = new GridData();
/*  89 */     new IntParameter(cIgnore, "StartStopManager_iIgnoreSeedCount", 0, 9999).setLayoutData(gridData);
/*  90 */     label = new Label(cIgnore, 0);
/*  91 */     Messages.setLanguageText(label, "ConfigView.label.seeds");
/*     */     
/*  93 */     label = new Label(cIgnore, 64);
/*  94 */     Messages.setLanguageText(label, "ConfigView.label.seeding.ignoreRatioPeers");
/*  95 */     gridData = new GridData();
/*  96 */     new IntParameter(cIgnore, "Stop Peers Ratio", 0, 9999).setLayoutData(gridData);
/*  97 */     label = new Label(cIgnore, 0);
/*  98 */     Messages.setLanguageText(label, "ConfigView.label.peers");
/*     */     
/* 100 */     Composite cArea = new Composite(cIgnore, 0);
/* 101 */     layout = new GridLayout();
/* 102 */     layout.numColumns = 4;
/* 103 */     layout.marginWidth = 0;
/* 104 */     layout.marginHeight = 0;
/* 105 */     cArea.setLayout(layout);
/* 106 */     gridData = new GridData();
/* 107 */     gridData.horizontalIndent = 15;
/* 108 */     gridData.horizontalSpan = 3;
/* 109 */     Utils.setLayoutData(cArea, gridData);
/*     */     
/* 111 */     label = new Label(cArea, 0);
/* 112 */     ImageLoader.getInstance().setLabelImage(label, "subitem");
/* 113 */     gridData = new GridData(2);
/* 114 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 116 */     label = new Label(cArea, 0);
/* 117 */     Messages.setLanguageText(label, "ConfigView.label.seeding.fakeFullCopySeedStart");
/*     */     
/* 119 */     gridData = new GridData();
/* 120 */     new IntParameter(cArea, "StartStopManager_iIgnoreRatioPeersSeedStart", 0, 9999).setLayoutData(gridData);
/* 121 */     label = new Label(cArea, 0);
/* 122 */     Messages.setLanguageText(label, "ConfigView.label.seeds");
/*     */     
/*     */ 
/* 125 */     label = new Label(cIgnore, 0);
/* 126 */     Messages.setLanguageText(label, "ConfigView.label.seeding.ignoreShareRatio");
/* 127 */     gridData = new GridData();
/* 128 */     gridData.widthHint = 50;
/* 129 */     new FloatParameter(cIgnore, "Stop Ratio", 1.0F, -1.0F, true, 1).setLayoutData(gridData);
/* 130 */     label = new Label(cIgnore, 0);
/* 131 */     label.setText(":1");
/*     */     
/* 133 */     cArea = new Composite(cIgnore, 0);
/* 134 */     layout = new GridLayout();
/* 135 */     layout.numColumns = 4;
/* 136 */     layout.marginWidth = 0;
/* 137 */     layout.marginHeight = 0;
/* 138 */     cArea.setLayout(layout);
/* 139 */     gridData = new GridData();
/* 140 */     gridData.horizontalIndent = 15;
/* 141 */     gridData.horizontalSpan = 3;
/* 142 */     Utils.setLayoutData(cArea, gridData);
/*     */     
/* 144 */     label = new Label(cArea, 0);
/* 145 */     ImageLoader.getInstance().setLabelImage(label, "subitem");
/* 146 */     gridData = new GridData(2);
/* 147 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 149 */     label = new Label(cArea, 0);
/* 150 */     Messages.setLanguageText(label, "ConfigView.label.seeding.fakeFullCopySeedStart");
/*     */     
/* 152 */     gridData = new GridData();
/* 153 */     new IntParameter(cArea, "StartStopManager_iIgnoreShareRatioSeedStart", 0, 9999).setLayoutData(gridData);
/* 154 */     label = new Label(cArea, 0);
/* 155 */     Messages.setLanguageText(label, "ConfigView.label.seeds");
/*     */     
/*     */ 
/* 158 */     gridData = new GridData();
/* 159 */     gridData.horizontalSpan = 3;
/* 160 */     new BooleanParameter(cIgnore, "StartStopManager_bIgnore0Peers", "ConfigView.label.seeding.ignore0Peers").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 164 */     return cIgnoreRules;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/ConfigSectionSeedingIgnore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */