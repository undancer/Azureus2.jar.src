/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionQueue
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  46 */     return "root";
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  50 */     return 0;
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
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  63 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  65 */     GridData gridData = new GridData(272);
/*     */     
/*  67 */     Utils.setLayoutData(cSection, gridData);
/*  68 */     GridLayout layout = new GridLayout();
/*  69 */     layout.numColumns = 2;
/*  70 */     layout.marginHeight = 0;
/*  71 */     cSection.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  75 */     Label label = new Label(cSection, 0);
/*  76 */     Messages.setLanguageText(label, "ConfigView.label.maxdownloads");
/*  77 */     gridData = new GridData();
/*  78 */     final IntParameter maxDLs = new IntParameter(cSection, "max downloads", 0, Integer.MAX_VALUE);
/*  79 */     maxDLs.setLayoutData(gridData);
/*     */     
/*     */ 
/*  82 */     Composite cMaxDownloads = new Composite(cSection, 0);
/*  83 */     layout = new GridLayout();
/*  84 */     layout.numColumns = 3;
/*  85 */     layout.marginWidth = 0;
/*  86 */     layout.marginHeight = 0;
/*  87 */     cMaxDownloads.setLayout(layout);
/*  88 */     gridData = new GridData();
/*  89 */     gridData.horizontalIndent = 15;
/*  90 */     gridData.horizontalSpan = 2;
/*  91 */     Utils.setLayoutData(cMaxDownloads, gridData);
/*     */     
/*  93 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  94 */     label = new Label(cMaxDownloads, 0);
/*  95 */     imageLoader.setLabelImage(label, "subitem");
/*  96 */     gridData = new GridData(2);
/*  97 */     Utils.setLayoutData(label, gridData);
/*     */     
/*  99 */     label = new Label(cMaxDownloads, 0);
/* 100 */     Messages.setLanguageText(label, "ConfigView.label.ignoreChecking");
/*     */     
/* 102 */     gridData = new GridData();
/* 103 */     BooleanParameter ignoreChecking = new BooleanParameter(cMaxDownloads, "StartStopManager_bMaxDownloadIgnoreChecking");
/*     */     
/*     */ 
/* 106 */     ignoreChecking.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 110 */     label = new Label(cSection, 0);
/* 111 */     Messages.setLanguageText(label, "ConfigView.label.maxactivetorrents");
/* 112 */     gridData = new GridData();
/* 113 */     final IntParameter maxActiv = new IntParameter(cSection, "max active torrents", 0, Integer.MAX_VALUE);
/*     */     
/* 115 */     maxActiv.setLayoutData(gridData);
/*     */     
/* 117 */     Composite cMaxActiveOptionsArea = new Composite(cSection, 0);
/* 118 */     layout = new GridLayout();
/* 119 */     layout.numColumns = 3;
/* 120 */     layout.marginWidth = 0;
/* 121 */     layout.marginHeight = 0;
/* 122 */     cMaxActiveOptionsArea.setLayout(layout);
/* 123 */     gridData = new GridData();
/* 124 */     gridData.horizontalIndent = 15;
/* 125 */     gridData.horizontalSpan = 2;
/* 126 */     Utils.setLayoutData(cMaxActiveOptionsArea, gridData);
/*     */     
/* 128 */     label = new Label(cMaxActiveOptionsArea, 0);
/* 129 */     imageLoader.setLabelImage(label, "subitem");
/* 130 */     gridData = new GridData(2);
/* 131 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 133 */     gridData = new GridData();
/* 134 */     BooleanParameter maxActiveWhenSeedingEnabled = new BooleanParameter(cMaxActiveOptionsArea, "StartStopManager_bMaxActiveTorrentsWhenSeedingEnabled", "ConfigView.label.queue.maxactivetorrentswhenseeding");
/*     */     
/*     */ 
/*     */ 
/* 138 */     maxActiveWhenSeedingEnabled.setLayoutData(gridData);
/*     */     
/* 140 */     gridData = new GridData();
/*     */     
/* 142 */     IntParameter maxActivWhenSeeding = new IntParameter(cMaxActiveOptionsArea, "StartStopManager_iMaxActiveTorrentsWhenSeeding", 0, Integer.MAX_VALUE);
/*     */     
/* 144 */     maxActivWhenSeeding.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 148 */     label = new Label(cSection, 0);
/* 149 */     Messages.setLanguageText(label, "ConfigView.label.mindownloads");
/* 150 */     gridData = new GridData();
/* 151 */     final IntParameter minDLs = new IntParameter(cSection, "min downloads", 0, Integer.MAX_VALUE);
/* 152 */     minDLs.setLayoutData(gridData);
/* 153 */     minDLs.setMaximumValue(maxDLs.getValue() / 2);
/*     */     
/*     */ 
/*     */ 
/* 157 */     maxActiveWhenSeedingEnabled.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(maxActivWhenSeeding));
/*     */     
/*     */ 
/* 160 */     maxDLs.addChangeListener(new ParameterChangeAdapter() {
/*     */       public void parameterChanged(Parameter p, boolean caused_internally) {
/* 162 */         int iMaxDLs = maxDLs.getValue();
/* 163 */         minDLs.setMaximumValue(iMaxDLs / 2);
/*     */         
/* 165 */         int iMinDLs = minDLs.getValue();
/* 166 */         int iMaxActive = maxActiv.getValue();
/*     */         
/* 168 */         if (((iMaxDLs == 0) || (iMaxDLs > iMaxActive)) && (iMaxActive != 0)) {
/* 169 */           maxActiv.setValue(iMaxDLs);
/*     */         }
/*     */         
/*     */       }
/* 173 */     });
/* 174 */     maxActiv.addChangeListener(new ParameterChangeAdapter() {
/*     */       public void parameterChanged(Parameter p, boolean caused_internally) {
/* 176 */         int iMaxDLs = maxDLs.getValue();
/* 177 */         int iMaxActive = maxActiv.getValue();
/*     */         
/* 179 */         if (((iMaxDLs == 0) || (iMaxDLs > iMaxActive)) && (iMaxActive != 0)) {
/* 180 */           maxDLs.setValue(iMaxActive);
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 187 */     });
/* 188 */     ArrayList values = new ArrayList();
/* 189 */     int exp = 29;
/* 190 */     for (int val = 0; val <= 8388608;)
/*     */     {
/* 192 */       values.add(new Integer(val));
/* 193 */       if (val < 256) {
/* 194 */         val += 64;
/* 195 */       } else if (val < 1024) {
/* 196 */         val += 256;
/* 197 */       } else if (val < 16384) {
/* 198 */         val += 1024;
/*     */       } else
/* 200 */         val = (int)(Math.pow(2.0D, exp++ / 2) + (exp % 2 == 0 ? Math.pow(2.0D, (exp - 3) / 2) : 0.0D));
/*     */     }
/* 202 */     String[] activeDLLabels = new String[values.size()];
/* 203 */     int[] activeDLValues = new int[activeDLLabels.length];
/*     */     
/*     */ 
/* 206 */     label = new Label(cSection, 0);
/* 207 */     Messages.setLanguageText(label, "ConfigView.label.minSpeedForActiveDL");
/* 208 */     for (int i = 0; i < activeDLLabels.length; i++)
/*     */     {
/* 210 */       activeDLValues[i] = ((Integer)values.get(i)).intValue();
/* 211 */       activeDLLabels[i] = DisplayFormatters.formatByteCountToKiBEtcPerSec(activeDLValues[i], true);
/*     */     }
/*     */     
/*     */ 
/* 215 */     new IntListParameter(cSection, "StartStopManager_iMinSpeedForActiveDL", activeDLLabels, activeDLValues);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 220 */     label = new Label(cSection, 0);
/* 221 */     Messages.setLanguageText(label, "ConfigView.label.minSpeedForActiveSeeding");
/* 222 */     String[] activeSeedingLabels = new String[values.size() - 4];
/* 223 */     int[] activeSeedingValues = new int[activeSeedingLabels.length];
/* 224 */     System.arraycopy(activeDLLabels, 0, activeSeedingLabels, 0, activeSeedingLabels.length);
/* 225 */     System.arraycopy(activeDLValues, 0, activeSeedingValues, 0, activeSeedingValues.length);
/*     */     
/* 227 */     new IntListParameter(cSection, "StartStopManager_iMinSpeedForActiveSeeding", activeSeedingLabels, activeSeedingValues);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 233 */     Composite cMinSpeedActiveCDing = new Composite(cSection, 0);
/* 234 */     layout = new GridLayout();
/* 235 */     layout.numColumns = 3;
/* 236 */     layout.marginWidth = 0;
/* 237 */     layout.marginHeight = 0;
/* 238 */     cMinSpeedActiveCDing.setLayout(layout);
/* 239 */     gridData = new GridData();
/* 240 */     gridData.horizontalIndent = 15;
/* 241 */     gridData.horizontalSpan = 2;
/* 242 */     Utils.setLayoutData(cMinSpeedActiveCDing, gridData);
/*     */     
/* 244 */     label = new Label(cMinSpeedActiveCDing, 0);
/* 245 */     imageLoader.setLabelImage(label, "subitem");
/* 246 */     gridData = new GridData(2);
/* 247 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 249 */     label = new Label(cMinSpeedActiveCDing, 0);
/* 250 */     Messages.setLanguageText(label, "ConfigView.label.maxStalledSeeding");
/*     */     
/* 252 */     gridData = new GridData();
/* 253 */     IntParameter maxStalledSeeding = new IntParameter(cMinSpeedActiveCDing, "StartStopManager_iMaxStalledSeeding", 0, Integer.MAX_VALUE);
/*     */     
/* 255 */     maxStalledSeeding.setMinimumValue(0);
/* 256 */     maxStalledSeeding.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 261 */     gridData = new GridData();
/* 262 */     gridData.horizontalSpan = 2;
/* 263 */     new BooleanParameter(cSection, "StartStopManager_bStopOnceBandwidthMet", "ConfigView.label.queue.stoponcebandwidthmet").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 268 */     gridData = new GridData();
/* 269 */     gridData.horizontalSpan = 2;
/* 270 */     new BooleanParameter(cSection, "StartStopManager_bNewSeedsMoveTop", "ConfigView.label.queue.newseedsmovetop").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 275 */     gridData = new GridData();
/* 276 */     gridData.horizontalSpan = 2;
/* 277 */     new BooleanParameter(cSection, "StartStopManager_bRetainForceStartWhenComplete", "ConfigView.label.queue.retainforce").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 282 */     gridData = new GridData();
/* 283 */     gridData.horizontalSpan = 2;
/* 284 */     new BooleanParameter(cSection, "Alert on close", "ConfigView.label.showpopuponclose").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 289 */     gridData = new GridData();
/* 290 */     gridData.horizontalSpan = 2;
/* 291 */     new BooleanParameter(cSection, "StartStopManager_bDebugLog", "ConfigView.label.queue.debuglog").setLayoutData(gridData);
/*     */     
/*     */ 
/* 294 */     return cSection;
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/* 298 */     return "queue";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/ConfigSectionQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */