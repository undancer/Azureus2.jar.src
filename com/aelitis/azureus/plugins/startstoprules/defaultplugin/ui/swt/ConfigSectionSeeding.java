/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
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
/*     */ public class ConfigSectionSeeding
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  42 */     return "queue";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  46 */     return "queue.seeding";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  56 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  65 */     Composite cSeeding = new Composite(parent, 0);
/*     */     
/*  67 */     GridLayout layout = new GridLayout();
/*  68 */     layout.numColumns = 2;
/*  69 */     layout.marginHeight = 0;
/*  70 */     cSeeding.setLayout(layout);
/*  71 */     GridData gridData = new GridData(272);
/*  72 */     Utils.setLayoutData(cSeeding, gridData);
/*     */     
/*     */ 
/*     */ 
/*  76 */     Label label = new Label(cSeeding, 0);
/*  77 */     Messages.setLanguageText(label, "ConfigView.label.minSeedingTime");
/*  78 */     gridData = new GridData();
/*  79 */     new IntParameter(cSeeding, "StartStopManager_iMinSeedingTime", 0, Integer.MAX_VALUE).setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*  83 */     gridData = new GridData();
/*  84 */     gridData.horizontalSpan = 2;
/*  85 */     BooleanParameter dontStartMore = new BooleanParameter(cSeeding, "StartStopManager_bStartNoMoreSeedsWhenUpLimitMet", "ConfigView.label.bStartNoMoreSeedsWhenUpLimitMet");
/*     */     
/*     */ 
/*  88 */     dontStartMore.setLayoutData(gridData);
/*     */     
/*     */ 
/*  91 */     Composite cDontStartOptions = new Composite(cSeeding, 0);
/*  92 */     layout = new GridLayout();
/*  93 */     layout.numColumns = 3;
/*  94 */     layout.marginWidth = 0;
/*  95 */     layout.marginHeight = 0;
/*  96 */     cDontStartOptions.setLayout(layout);
/*  97 */     gridData = new GridData();
/*  98 */     gridData.horizontalIndent = 15;
/*  99 */     gridData.horizontalSpan = 2;
/* 100 */     Utils.setLayoutData(cDontStartOptions, gridData);
/*     */     
/* 102 */     label = new Label(cDontStartOptions, 0);
/* 103 */     ImageLoader.getInstance().setLabelImage(label, "subitem");
/* 104 */     gridData = new GridData(2);
/* 105 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 107 */     Label xlabel = new Label(cDontStartOptions, 0);
/* 108 */     Messages.setLanguageText(xlabel, "ConfigView.label.bStartNoMoreSeedsWhenUpLimitMetSlack");
/* 109 */     gridData = new GridData();
/* 110 */     IntParameter slack = new IntParameter(cDontStartOptions, "StartStopManager_bStartNoMoreSeedsWhenUpLimitMetSlack", 0, Integer.MAX_VALUE);
/* 111 */     slack.setLayoutData(gridData);
/*     */     
/* 113 */     label = new Label(cDontStartOptions, 0);
/* 114 */     ImageLoader.getInstance().setLabelImage(label, "subitem");
/* 115 */     gridData = new GridData(2);
/* 116 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 118 */     gridData = new GridData();
/* 119 */     gridData.horizontalSpan = 2;
/* 120 */     BooleanParameter slackIsPercent = new BooleanParameter(cDontStartOptions, "StartStopManager_bStartNoMoreSeedsWhenUpLimitMetPercent", "ConfigView.label.bStartNoMoreSeedsWhenUpLimitMetPercent");
/*     */     
/* 122 */     slackIsPercent.setLayoutData(gridData);
/*     */     
/* 124 */     dontStartMore.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(slack, slackIsPercent));
/* 125 */     dontStartMore.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(xlabel));
/*     */     
/*     */ 
/*     */ 
/* 129 */     gridData = new GridData();
/* 130 */     gridData.horizontalSpan = 2;
/* 131 */     new BooleanParameter(cSeeding, "Disconnect Seed", "ConfigView.label.disconnetseed").setLayoutData(gridData);
/*     */     
/*     */ 
/* 134 */     gridData = new GridData();
/* 135 */     gridData.horizontalSpan = 2;
/* 136 */     new BooleanParameter(cSeeding, "Use Super Seeding", "ConfigView.label.userSuperSeeding").setLayoutData(gridData);
/*     */     
/*     */ 
/* 139 */     gridData = new GridData();
/* 140 */     gridData.horizontalSpan = 2;
/* 141 */     new BooleanParameter(cSeeding, "StartStopManager_bAutoReposition", "ConfigView.label.seeding.autoReposition").setLayoutData(gridData);
/*     */     
/*     */ 
/* 144 */     label = new Label(cSeeding, 0);
/* 145 */     Messages.setLanguageText(label, "ConfigView.label.seeding.addForSeedingDLCopyCount");
/* 146 */     gridData = new GridData();
/* 147 */     new IntParameter(cSeeding, "StartStopManager_iAddForSeedingDLCopyCount", 0, Integer.MAX_VALUE).setLayoutData(gridData);
/*     */     
/* 149 */     label = new Label(cSeeding, 0);
/* 150 */     Messages.setLanguageText(label, "ConfigView.label.seeding.numPeersAsFullCopy");
/*     */     
/* 152 */     Composite cArea = new Composite(cSeeding, 0);
/* 153 */     layout = new GridLayout();
/* 154 */     layout.marginHeight = 0;
/* 155 */     layout.marginWidth = 0;
/* 156 */     layout.numColumns = 2;
/* 157 */     cArea.setLayout(layout);
/* 158 */     gridData = new GridData();
/* 159 */     Utils.setLayoutData(cArea, gridData);
/*     */     
/* 161 */     gridData = new GridData();
/* 162 */     final IntParameter paramFakeFullCopy = new IntParameter(cArea, "StartStopManager_iNumPeersAsFullCopy", 0, Integer.MAX_VALUE);
/* 163 */     paramFakeFullCopy.setLayoutData(gridData);
/*     */     
/* 165 */     label = new Label(cArea, 0);
/* 166 */     Messages.setLanguageText(label, "ConfigView.label.peers");
/*     */     
/*     */ 
/* 169 */     final Composite cFullCopyOptionsArea = new Composite(cSeeding, 0);
/* 170 */     layout = new GridLayout();
/* 171 */     layout.numColumns = 4;
/* 172 */     layout.marginWidth = 0;
/* 173 */     layout.marginHeight = 0;
/* 174 */     cFullCopyOptionsArea.setLayout(layout);
/* 175 */     gridData = new GridData();
/* 176 */     gridData.horizontalIndent = 15;
/* 177 */     gridData.horizontalSpan = 2;
/* 178 */     Utils.setLayoutData(cFullCopyOptionsArea, gridData);
/*     */     
/* 180 */     label = new Label(cFullCopyOptionsArea, 0);
/* 181 */     ImageLoader.getInstance().setLabelImage(label, "subitem");
/* 182 */     gridData = new GridData(2);
/* 183 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 185 */     label = new Label(cFullCopyOptionsArea, 0);
/* 186 */     Messages.setLanguageText(label, "ConfigView.label.seeding.fakeFullCopySeedStart");
/*     */     
/* 188 */     gridData = new GridData();
/* 189 */     new IntParameter(cFullCopyOptionsArea, "StartStopManager_iFakeFullCopySeedStart", 0, Integer.MAX_VALUE).setLayoutData(gridData);
/* 190 */     label = new Label(cFullCopyOptionsArea, 0);
/* 191 */     Messages.setLanguageText(label, "ConfigView.label.seeds");
/*     */     
/*     */ 
/* 194 */     int iNumPeersAsFullCopy = COConfigurationManager.getIntParameter("StartStopManager_iNumPeersAsFullCopy");
/* 195 */     controlsSetEnabled(cFullCopyOptionsArea.getChildren(), iNumPeersAsFullCopy != 0);
/*     */     
/* 197 */     paramFakeFullCopy.addChangeListener(new ParameterChangeAdapter() {
/*     */       public void parameterChanged(Parameter p, boolean caused_internally) {
/* 199 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/*     */             try {
/* 202 */               int value = ConfigSectionSeeding.1.this.val$paramFakeFullCopy.getValue();
/* 203 */               boolean enabled = value != 0;
/* 204 */               if (ConfigSectionSeeding.1.this.val$cFullCopyOptionsArea.getEnabled() != enabled) {
/* 205 */                 ConfigSectionSeeding.1.this.val$cFullCopyOptionsArea.setEnabled(enabled);
/* 206 */                 ConfigSectionSeeding.this.controlsSetEnabled(ConfigSectionSeeding.1.this.val$cFullCopyOptionsArea.getChildren(), enabled);
/*     */               }
/*     */             }
/*     */             catch (Exception e) {}
/*     */           }
/*     */         });
/*     */       }
/* 213 */     });
/* 214 */     paramFakeFullCopy.getControl().addListener(24, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {}
/*     */ 
/* 218 */     });
/* 219 */     return cSeeding;
/*     */   }
/*     */   
/*     */   private void controlsSetEnabled(Control[] controls, boolean bEnabled) {
/* 223 */     for (int i = 0; i < controls.length; i++) {
/* 224 */       if ((controls[i] instanceof Composite))
/* 225 */         controlsSetEnabled(((Composite)controls[i]).getChildren(), bEnabled);
/* 226 */       controls[i].setEnabled(bEnabled);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/ConfigSectionSeeding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */