/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.RadioParameter;
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
/*     */ public class ConfigSectionSeedingAutoStarting
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  42 */     return "queue.seeding";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  46 */     return "queue.seeding.autoStarting";
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
/*  65 */     Composite gQR = new Composite(parent, 0);
/*     */     
/*  67 */     GridLayout layout = new GridLayout();
/*  68 */     layout.numColumns = 1;
/*  69 */     layout.marginHeight = 0;
/*  70 */     gQR.setLayout(layout);
/*  71 */     GridData gridData = new GridData(272);
/*  72 */     gQR.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  78 */     Composite cRankType = new Group(gQR, 0);
/*  79 */     layout = new GridLayout();
/*  80 */     layout.numColumns = 2;
/*  81 */     layout.verticalSpacing = 2;
/*  82 */     cRankType.setLayout(layout);
/*  83 */     gridData = new GridData(256);
/*  84 */     cRankType.setLayoutData(gridData);
/*  85 */     Messages.setLanguageText(cRankType, "ConfigView.label.seeding.rankType");
/*     */     
/*     */ 
/*  88 */     RadioParameter rparamPeerSeed = new RadioParameter(cRankType, "StartStopManager_iRankType", 1);
/*     */     
/*     */ 
/*  91 */     Messages.setLanguageText(rparamPeerSeed.getControl(), "ConfigView.label.seeding.rankType.peerSeed");
/*  92 */     gridData = new GridData(2);
/*  93 */     rparamPeerSeed.setLayoutData(gridData);
/*     */     
/*  95 */     new Label(cRankType, 0);
/*     */     
/*     */ 
/*     */ 
/*  99 */     RadioParameter rparamSeedCount = new RadioParameter(cRankType, "StartStopManager_iRankType", 2);
/*     */     
/*     */ 
/* 102 */     Messages.setLanguageText(rparamSeedCount.getControl(), "ConfigView.label.seeding.rankType.seed");
/* 103 */     gridData = new GridData(2);
/* 104 */     rparamSeedCount.setLayoutData(gridData);
/*     */     
/* 106 */     Group gSeedCount = new Group(cRankType, 0);
/* 107 */     layout = new GridLayout();
/* 108 */     layout.marginHeight = 2;
/* 109 */     layout.marginWidth = 2;
/* 110 */     layout.numColumns = 3;
/* 111 */     gSeedCount.setLayout(layout);
/* 112 */     gridData = new GridData(256);
/* 113 */     gridData.verticalSpan = 1;
/* 114 */     gSeedCount.setLayoutData(gridData);
/* 115 */     Messages.setLanguageText(gSeedCount, "ConfigView.label.seeding.rankType.seed.options");
/*     */     
/* 117 */     Label label = new Label(gSeedCount, 0);
/* 118 */     Messages.setLanguageText(label, "ConfigView.label.seeding.rankType.seed.fallback");
/*     */     
/* 120 */     gridData = new GridData(2);
/* 121 */     IntParameter intParamFallBack = new IntParameter(gSeedCount, "StartStopManager_iRankTypeSeedFallback", 0, Integer.MAX_VALUE);
/* 122 */     intParamFallBack.setLayoutData(gridData);
/*     */     
/* 124 */     Label labelFallBackSeeds = new Label(gSeedCount, 0);
/* 125 */     label.setLayoutData(new GridData(2));
/* 126 */     Messages.setLanguageText(labelFallBackSeeds, "ConfigView.label.seeds");
/*     */     
/* 128 */     Control[] controlsSeedCount = { gSeedCount };
/* 129 */     rparamSeedCount.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controlsSeedCount));
/*     */     
/*     */ 
/*     */ 
/* 133 */     RadioParameter rparamPeer = new RadioParameter(cRankType, "StartStopManager_iRankType", 4);
/*     */     
/*     */ 
/* 136 */     Messages.setLanguageText(rparamPeer.getControl(), "ConfigView.label.seeding.rankType.peer");
/* 137 */     gridData = new GridData(2);
/* 138 */     rparamPeer.setLayoutData(gridData);
/*     */     
/* 140 */     new Label(cRankType, 0);
/*     */     
/*     */ 
/* 143 */     RadioParameter rparamTimed = new RadioParameter(cRankType, "StartStopManager_iRankType", 3);
/*     */     
/*     */ 
/* 146 */     Messages.setLanguageText(rparamTimed.getControl(), "ConfigView.label.seeding.rankType.timedRotation");
/* 147 */     gridData = new GridData(2);
/* 148 */     rparamTimed.setLayoutData(gridData);
/*     */     
/* 150 */     Group gTimed = new Group(cRankType, 0);
/* 151 */     layout = new GridLayout();
/* 152 */     layout.marginHeight = 2;
/* 153 */     layout.marginWidth = 2;
/* 154 */     layout.numColumns = 2;
/* 155 */     gTimed.setLayout(layout);
/* 156 */     gridData = new GridData(256);
/* 157 */     gridData.verticalSpan = 1;
/* 158 */     gTimed.setLayoutData(gridData);
/* 159 */     Messages.setLanguageText(gTimed, "ConfigView.label.seeding.rankType.timed.options");
/*     */     
/* 161 */     label = new Label(gTimed, 0);
/* 162 */     Messages.setLanguageText(label, "ConfigView.label.seeding.rankType.timed.minTimeWithPeers");
/*     */     
/* 164 */     gridData = new GridData(2);
/* 165 */     IntParameter intParamTimedPeersMinTime = new IntParameter(gTimed, "StartStopManager_iTimed_MinSeedingTimeWithPeers", 0, Integer.MAX_VALUE);
/* 166 */     intParamTimedPeersMinTime.setLayoutData(gridData);
/*     */     
/* 168 */     Control[] controlsTimed = { gTimed };
/* 169 */     rparamTimed.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controlsTimed));
/*     */     
/*     */ 
/*     */ 
/* 173 */     RadioParameter rparamNone = new RadioParameter(cRankType, "StartStopManager_iRankType", 0);
/*     */     
/*     */ 
/* 176 */     Messages.setLanguageText(rparamNone.getControl(), "ConfigView.label.seeding.rankType.none");
/* 177 */     gridData = new GridData(2);
/* 178 */     rparamNone.setLayoutData(gridData);
/*     */     
/* 180 */     new Label(cRankType, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 185 */     Composite cNoTimeNone = new Composite(gQR, 0);
/* 186 */     layout = new GridLayout();
/* 187 */     layout.numColumns = 2;
/* 188 */     cNoTimeNone.setLayout(layout);
/* 189 */     gridData = new GridData();
/* 190 */     layout.marginHeight = 0;
/* 191 */     layout.marginWidth = 0;
/* 192 */     cNoTimeNone.setLayoutData(gridData);
/*     */     
/* 194 */     gridData = new GridData();
/* 195 */     gridData.horizontalSpan = 2;
/* 196 */     new BooleanParameter(cNoTimeNone, "StartStopManager_bPreferLargerSwarms", "ConfigView.label.seeding.preferLargerSwarms").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */     label = new Label(cNoTimeNone, 0);
/* 203 */     Messages.setLanguageText(label, "ConfigView.label.minPeersToBoostNoSeeds");
/* 204 */     String[] boostQRPeersLabels = new String[9];
/* 205 */     int[] boostQRPeersValues = new int[9];
/* 206 */     String peers = MessageText.getString("ConfigView.text.peers");
/* 207 */     for (int i = 0; i < boostQRPeersValues.length; i++) {
/* 208 */       boostQRPeersLabels[i] = (i + 1 + " " + peers);
/* 209 */       boostQRPeersValues[i] = (i + 1);
/*     */     }
/* 211 */     gridData = new GridData();
/* 212 */     new IntListParameter(cNoTimeNone, "StartStopManager_iMinPeersToBoostNoSeeds", boostQRPeersLabels, boostQRPeersValues);
/*     */     
/* 214 */     Control[] controlsNoTimeNone = { cNoTimeNone };
/* 215 */     rparamPeerSeed.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controlsNoTimeNone));
/* 216 */     rparamSeedCount.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controlsNoTimeNone));
/*     */     
/* 218 */     int iRankType = COConfigurationManager.getIntParameter("StartStopManager_iRankType");
/* 219 */     boolean enable = (iRankType == 1) || (iRankType == 2);
/*     */     
/* 221 */     controlsSetEnabled(controlsNoTimeNone, enable);
/*     */     
/*     */ 
/* 224 */     new BooleanParameter(gQR, "StartStopManager_bAutoStart0Peers", "ConfigView.label.seeding.autoStart0Peers");
/*     */     
/*     */ 
/* 227 */     return gQR;
/*     */   }
/*     */   
/* 230 */   private void controlsSetEnabled(Control[] controls, boolean bEnabled) { for (int i = 0; i < controls.length; i++) {
/* 231 */       if ((controls[i] instanceof Composite))
/* 232 */         controlsSetEnabled(((Composite)controls[i]).getChildren(), bEnabled);
/* 233 */       controls[i].setEnabled(bEnabled);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/ConfigSectionSeedingAutoStarting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */