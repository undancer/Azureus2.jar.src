/*     */ package org.gudy.azureus2.ui.swt.speedtest;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpeedTestFinishPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   SpeedManager speedManager;
/*     */   TransferStatsView.limitToTextHelper helper;
/*     */   
/*     */   public SpeedTestFinishPanel(Wizard wizard, IWizardPanel previousPanel)
/*     */   {
/*  49 */     super(wizard, previousPanel);
/*     */     
/*  51 */     this.speedManager = AzureusCoreFactory.getSingleton().getSpeedManager();
/*  52 */     this.helper = new TransferStatsView.limitToTextHelper();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  61 */     String title = MessageText.getString("SpeedTestWizard.finish.panel.title");
/*  62 */     this.wizard.setTitle(title);
/*     */     
/*  64 */     Composite rootPanel = this.wizard.getPanel();
/*  65 */     GridLayout layout = new GridLayout();
/*  66 */     layout.numColumns = 1;
/*  67 */     rootPanel.setLayout(layout);
/*     */     
/*  69 */     Composite panel = new Composite(rootPanel, 0);
/*  70 */     GridData gridData = new GridData(772);
/*  71 */     Utils.setLayoutData(panel, gridData);
/*  72 */     layout = new GridLayout();
/*  73 */     layout.numColumns = 3;
/*  74 */     layout.makeColumnsEqualWidth = true;
/*  75 */     panel.setLayout(layout);
/*     */     
/*  77 */     Label label = new Label(panel, 64);
/*  78 */     gridData = new GridData();
/*  79 */     gridData.horizontalSpan = 3;
/*  80 */     gridData.widthHint = 380;
/*  81 */     Utils.setLayoutData(label, gridData);
/*  82 */     Messages.setLanguageText(label, "SpeedTestWizard.finish.panel.click.close");
/*     */     
/*     */ 
/*  85 */     SpeedManagerLimitEstimate upEst = this.speedManager.getEstimatedUploadCapacityBytesPerSec();
/*  86 */     int maxUploadKbs = upEst.getBytesPerSec() / 1024;
/*  87 */     SpeedManagerLimitEstimate downEst = this.speedManager.getEstimatedDownloadCapacityBytesPerSec();
/*  88 */     int maxDownloadKbs = downEst.getBytesPerSec() / 1024;
/*     */     
/*     */ 
/*  91 */     boolean autoSpeedEnabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Enabled");
/*  92 */     boolean autoSpeedSeedingEnabled = COConfigurationManager.getBooleanParameter("Auto Upload Speed Seeding Enabled");
/*     */     
/*     */ 
/*  95 */     Label s2 = new Label(panel, 0);
/*  96 */     gridData = new GridData();
/*  97 */     gridData.horizontalSpan = 3;
/*  98 */     s2.setLayoutData(gridData);
/*     */     
/* 100 */     String autoSpeed = MessageText.getString("SpeedTestWizard.finish.panel.auto.speed");
/* 101 */     createStatusLine(panel, autoSpeed, autoSpeedEnabled);
/*     */     
/* 103 */     String autoSpeedWhileSeeding = MessageText.getString("SpeedTestWizard.finish.panel.auto.speed.seeding");
/* 104 */     createStatusLine(panel, autoSpeedWhileSeeding, autoSpeedSeedingEnabled);
/*     */     
/*     */ 
/* 107 */     Label s1 = new Label(panel, 0);
/* 108 */     gridData = new GridData();
/* 109 */     gridData.horizontalSpan = 3;
/* 110 */     s1.setLayoutData(gridData);
/*     */     
/*     */ 
/* 113 */     createHeaderLine(panel);
/*     */     
/* 115 */     String maxUploadLbl = MessageText.getString("SpeedView.stats.estupcap");
/* 116 */     createDataLine(panel, maxUploadLbl, maxUploadKbs, upEst);
/*     */     
/* 118 */     String maxDownloadLbl = MessageText.getString("SpeedView.stats.estdowncap");
/* 119 */     createDataLine(panel, maxDownloadLbl, maxDownloadKbs, downEst);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createHeaderLine(Composite panel)
/*     */   {
/* 127 */     Label c1 = new Label(panel, 0);
/* 128 */     GridData gridData = new GridData();
/* 129 */     gridData.horizontalSpan = 1;
/* 130 */     c1.setLayoutData(gridData);
/* 131 */     c1.setText(" ");
/*     */     
/*     */ 
/* 134 */     Label c2 = new Label(panel, 0);
/* 135 */     gridData = new GridData();
/* 136 */     gridData.horizontalSpan = 1;
/* 137 */     gridData.horizontalAlignment = 2;
/* 138 */     c2.setLayoutData(gridData);
/* 139 */     c2.setText(MessageText.getString("SpeedTestWizard.set.upload.bytes.per.sec"));
/*     */     
/*     */ 
/* 142 */     Label c3 = new Label(panel, 0);
/* 143 */     gridData = new GridData();
/* 144 */     gridData.horizontalSpan = 1;
/* 145 */     gridData.horizontalAlignment = 1;
/* 146 */     c3.setLayoutData(gridData);
/* 147 */     c3.setText(MessageText.getString("SpeedTestWizard.set.upload.bits.per.sec"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createStatusLine(Composite panel, String label, boolean enabled)
/*     */   {
/* 158 */     Label r3c1 = new Label(panel, 0);
/* 159 */     GridData gridData = new GridData();
/* 160 */     gridData.horizontalSpan = 1;
/* 161 */     gridData.horizontalAlignment = 3;
/* 162 */     r3c1.setLayoutData(gridData);
/* 163 */     r3c1.setText(label);
/*     */     
/* 165 */     Label c3 = new Label(panel, 0);
/* 166 */     gridData = new GridData();
/* 167 */     gridData.horizontalSpan = 1;
/* 168 */     gridData.horizontalAlignment = 2;
/* 169 */     c3.setLayoutData(gridData);
/* 170 */     if (enabled) {
/* 171 */       c3.setText(MessageText.getString("SpeedTestWizard.finish.panel.enabled", "enabled"));
/*     */     } else {
/* 173 */       c3.setText(MessageText.getString("SpeedTestWizard.finish.panel.disabled", "disabled"));
/*     */     }
/*     */     
/* 176 */     Label c2 = new Label(panel, 0);
/* 177 */     gridData = new GridData();
/* 178 */     gridData.horizontalSpan = 1;
/* 179 */     gridData.horizontalAlignment = 1;
/* 180 */     c2.setLayoutData(gridData);
/* 181 */     String maxUploadBitsSec = "       ";
/* 182 */     c2.setText(maxUploadBitsSec);
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
/*     */   private void createDataLine(Composite panel, String label, int maxKbps, SpeedManagerLimitEstimate estimate)
/*     */   {
/* 195 */     Label c1 = new Label(panel, 0);
/* 196 */     GridData gridData = new GridData();
/* 197 */     gridData.horizontalSpan = 1;
/* 198 */     gridData.horizontalAlignment = 3;
/* 199 */     c1.setLayoutData(gridData);
/* 200 */     c1.setText(label + "  ");
/*     */     
/* 202 */     Label c2 = new Label(panel, 0);
/* 203 */     gridData = new GridData();
/* 204 */     gridData.horizontalSpan = 1;
/* 205 */     gridData.horizontalAlignment = 2;
/* 206 */     c2.setLayoutData(gridData);
/* 207 */     String estString = this.helper.getLimitText(estimate);
/* 208 */     c2.setText(estString);
/*     */     
/* 210 */     Label c3 = new Label(panel, 0);
/* 211 */     gridData = new GridData();
/* 212 */     gridData.horizontalSpan = 1;
/* 213 */     gridData.horizontalAlignment = 1;
/* 214 */     c3.setLayoutData(gridData);
/*     */     String maxBitsPerSec;
/*     */     String maxBitsPerSec;
/* 217 */     if (maxKbps == 0) {
/* 218 */       maxBitsPerSec = MessageText.getString("ConfigView.unlimited");
/*     */     } else {
/* 220 */       maxBitsPerSec = DisplayFormatters.formatByteCountToBitsPerSec(maxKbps * 1024);
/*     */     }
/*     */     
/* 223 */     c3.setText(maxBitsPerSec);
/*     */   }
/*     */   
/*     */   public boolean isPreviousEnabled()
/*     */   {
/* 228 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/speedtest/SpeedTestFinishPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */