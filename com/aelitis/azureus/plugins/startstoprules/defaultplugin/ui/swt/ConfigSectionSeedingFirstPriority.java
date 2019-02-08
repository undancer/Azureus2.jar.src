/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
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
/*     */ public class ConfigSectionSeedingFirstPriority
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  45 */     return "queue.seeding";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  49 */     return "queue.seeding.firstPriority";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  59 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  69 */     Composite cFirstPriorityArea = new Composite(parent, 0);
/*     */     
/*  71 */     GridLayout layout = new GridLayout();
/*  72 */     layout.numColumns = 2;
/*  73 */     layout.marginHeight = 0;
/*  74 */     cFirstPriorityArea.setLayout(layout);
/*  75 */     GridData gridData = new GridData(272);
/*  76 */     Utils.setLayoutData(cFirstPriorityArea, gridData);
/*     */     
/*     */ 
/*  79 */     Label label = new Label(cFirstPriorityArea, 64);
/*  80 */     gridData = new GridData(768);
/*  81 */     gridData.horizontalSpan = 2;
/*  82 */     gridData.widthHint = 300;
/*  83 */     Utils.setLayoutData(label, gridData);
/*  84 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.info");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */     Composite cFP = new Group(cFirstPriorityArea, 0);
/*  91 */     layout = new GridLayout();
/*  92 */     layout.numColumns = 2;
/*  93 */     layout.verticalSpacing = 6;
/*  94 */     cFP.setLayout(layout);
/*  95 */     gridData = new GridData(256);
/*  96 */     Utils.setLayoutData(cFP, gridData);
/*  97 */     Messages.setLanguageText(cFP, "ConfigView.label.seeding.firstPriority.FP");
/*     */     
/*     */ 
/* 100 */     Composite cArea = new Composite(cFP, 0);
/* 101 */     layout = new GridLayout();
/* 102 */     layout.marginHeight = 0;
/* 103 */     layout.marginWidth = 0;
/* 104 */     layout.numColumns = 3;
/* 105 */     cArea.setLayout(layout);
/* 106 */     gridData = new GridData(272);
/* 107 */     gridData.horizontalSpan = 3;
/* 108 */     Utils.setLayoutData(cArea, gridData);
/* 109 */     label = new Label(cArea, 0);
/* 110 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority");
/* 111 */     String[] fpLabels = { MessageText.getString("ConfigView.text.all"), MessageText.getString("ConfigView.text.any") };
/*     */     
/* 113 */     int[] fpValues = { 0, 1 };
/*     */     
/* 115 */     new IntListParameter(cArea, "StartStopManager_iFirstPriority_Type", fpLabels, fpValues);
/*     */     
/* 117 */     label = new Label(cArea, 0);
/* 118 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.following");
/*     */     
/*     */ 
/* 121 */     label = new Label(cFP, 0);
/* 122 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.shareRatio");
/* 123 */     String[] minQueueLabels = new String[55];
/* 124 */     int[] minQueueValues = new int[55];
/* 125 */     minQueueLabels[0] = "1:2 (0.5)";
/* 126 */     minQueueValues[0] = 500;
/* 127 */     minQueueLabels[1] = "3:4 (0.75)";
/* 128 */     minQueueValues[1] = 750;
/* 129 */     minQueueLabels[2] = "1:1";
/* 130 */     minQueueValues[2] = 1000;
/* 131 */     minQueueLabels[3] = "5:4 (1.25)";
/* 132 */     minQueueValues[3] = 1250;
/* 133 */     minQueueLabels[4] = "3:2 (1.5)";
/* 134 */     minQueueValues[4] = 1500;
/* 135 */     minQueueLabels[5] = "7:4 (1.75)";
/* 136 */     minQueueValues[5] = 1750;
/* 137 */     for (int i = 6; i < minQueueLabels.length; i++) {
/* 138 */       minQueueLabels[i] = (i - 4 + ":1");
/* 139 */       minQueueValues[i] = ((i - 4) * 1000);
/*     */     }
/* 141 */     new IntListParameter(cFP, "StartStopManager_iFirstPriority_ShareRatio", minQueueLabels, minQueueValues);
/*     */     
/*     */ 
/* 144 */     String sMinutes = MessageText.getString("ConfigView.text.minutes");
/* 145 */     String sHours = MessageText.getString("ConfigView.text.hours");
/*     */     
/*     */ 
/* 148 */     label = new Label(cFP, 0);
/* 149 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.DLMinutes");
/*     */     
/* 151 */     String[] dlTimeLabels = new String[15];
/* 152 */     int[] dlTimeValues = new int[15];
/* 153 */     dlTimeLabels[0] = MessageText.getString("ConfigView.text.ignore");
/* 154 */     dlTimeValues[0] = 0;
/* 155 */     for (int i = 1; i < dlTimeValues.length; i++) {
/* 156 */       dlTimeLabels[i] = ("<= " + (i + 2) + " " + sHours);
/* 157 */       dlTimeValues[i] = ((i + 2) * 60);
/*     */     }
/* 159 */     new IntListParameter(cFP, "StartStopManager_iFirstPriority_DLMinutes", dlTimeLabels, dlTimeValues);
/*     */     
/*     */ 
/* 162 */     label = new Label(cFirstPriorityArea, 64);
/*     */     
/*     */ 
/* 165 */     label = new Label(cFP, 0);
/* 166 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.seedingMinutes");
/*     */     
/* 168 */     String[] seedTimeLabels = new String[15];
/* 169 */     int[] seedTimeValues = new int[15];
/* 170 */     seedTimeLabels[0] = MessageText.getString("ConfigView.text.ignore");
/* 171 */     seedTimeValues[0] = 0;
/* 172 */     seedTimeLabels[1] = ("<= 90 " + sMinutes);
/* 173 */     seedTimeValues[1] = 90;
/* 174 */     for (int i = 2; i < seedTimeValues.length; i++) {
/* 175 */       seedTimeLabels[i] = ("<= " + i + " " + sHours);
/* 176 */       seedTimeValues[i] = (i * 60);
/*     */     }
/* 178 */     new IntListParameter(cFP, "StartStopManager_iFirstPriority_SeedingMinutes", seedTimeLabels, seedTimeValues);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 185 */     Composite cIgnoreFP = new Group(cFirstPriorityArea, 0);
/* 186 */     layout = new GridLayout();
/* 187 */     layout.numColumns = 2;
/* 188 */     layout.verticalSpacing = 6;
/* 189 */     cIgnoreFP.setLayout(layout);
/* 190 */     gridData = new GridData(272);
/* 191 */     Utils.setLayoutData(cIgnoreFP, gridData);
/* 192 */     Messages.setLanguageText(cIgnoreFP, "ConfigView.label.seeding.firstPriority.ignore");
/*     */     
/*     */ 
/* 195 */     label = new Label(cIgnoreFP, 0);
/* 196 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.ignoreSPRatio");
/* 197 */     String[] ignoreSPRatioLabels = new String[15];
/* 198 */     int[] ignoreSPRatioValues = new int[15];
/* 199 */     ignoreSPRatioLabels[0] = MessageText.getString("ConfigView.text.ignore");
/* 200 */     ignoreSPRatioValues[0] = 0;
/* 201 */     for (int i = 1; i < ignoreSPRatioLabels.length; i++) {
/* 202 */       ignoreSPRatioLabels[i] = (i * 10 + " " + ":1");
/* 203 */       ignoreSPRatioValues[i] = (i * 10);
/*     */     }
/* 205 */     new IntListParameter(cIgnoreFP, "StartStopManager_iFirstPriority_ignoreSPRatio", 0, ignoreSPRatioLabels, ignoreSPRatioValues);
/*     */     
/*     */ 
/*     */ 
/* 209 */     new BooleanParameter(cIgnoreFP, "StartStopManager_bFirstPriority_ignore0Peer", "ConfigView.label.seeding.firstPriority.ignore0Peer");
/*     */     
/*     */ 
/*     */ 
/* 213 */     label = new Label(cIgnoreFP, 0);
/*     */     
/*     */ 
/* 216 */     label = new Label(cIgnoreFP, 0);
/* 217 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.ignoreIdleHours");
/*     */     
/* 219 */     int[] availIdleHours = { 2, 3, 4, 5, 6, 7, 8, 12, 18, 24, 48, 72, 168 };
/* 220 */     String[] ignoreIdleHoursLabels = new String[availIdleHours.length + 1];
/* 221 */     int[] ignoreIdleHoursValues = new int[availIdleHours.length + 1];
/* 222 */     ignoreIdleHoursLabels[0] = MessageText.getString("ConfigView.text.ignore");
/* 223 */     ignoreIdleHoursValues[0] = 0;
/* 224 */     for (int i = 0; i < availIdleHours.length; i++) {
/* 225 */       ignoreIdleHoursLabels[(i + 1)] = (availIdleHours[i] + " " + sHours);
/* 226 */       ignoreIdleHoursValues[(i + 1)] = availIdleHours[i];
/*     */     }
/* 228 */     new IntListParameter(cIgnoreFP, "StartStopManager_iFirstPriority_ignoreIdleHours", 0, ignoreIdleHoursLabels, ignoreIdleHoursValues);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 233 */     Composite cArea1 = new Composite(cIgnoreFP, 0);
/* 234 */     layout = new GridLayout();
/* 235 */     layout.marginHeight = 0;
/* 236 */     layout.marginWidth = 0;
/* 237 */     layout.numColumns = 2;
/* 238 */     cArea1.setLayout(layout);
/* 239 */     gridData = new GridData(272);
/* 240 */     gridData.horizontalSpan = 2;
/* 241 */     Utils.setLayoutData(cArea1, gridData);
/* 242 */     label = new Label(cArea1, 0);
/* 243 */     Messages.setLanguageText(label, "ConfigView.label.seeding.firstPriority.ignore.info");
/*     */     
/*     */ 
/* 246 */     return cFirstPriorityArea;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/ConfigSectionSeedingFirstPriority.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */