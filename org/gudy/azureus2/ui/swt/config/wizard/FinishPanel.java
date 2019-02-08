/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*     */ public class FinishPanel
/*     */   extends AbstractWizardPanel<ConfigureWizard>
/*     */ {
/*     */   public FinishPanel(ConfigureWizard wizard, IWizardPanel previous)
/*     */   {
/*  51 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */   public void show() {
/*  55 */     ((ConfigureWizard)this.wizard).setTitle(MessageText.getString("configureWizard.finish.title"));
/*     */     
/*  57 */     Composite rootPanel = ((ConfigureWizard)this.wizard).getPanel();
/*  58 */     GridLayout layout = new GridLayout();
/*  59 */     layout.numColumns = 1;
/*  60 */     rootPanel.setLayout(layout);
/*     */     
/*  62 */     Composite panel = new Composite(rootPanel, 0);
/*  63 */     GridData gridData = new GridData(772);
/*  64 */     Utils.setLayoutData(panel, gridData);
/*  65 */     layout = new GridLayout();
/*  66 */     layout.numColumns = 3;
/*  67 */     panel.setLayout(layout);
/*     */     
/*  69 */     Label label = new Label(panel, 64);
/*  70 */     gridData = new GridData();
/*  71 */     gridData.horizontalSpan = 3;
/*  72 */     gridData.widthHint = 380;
/*  73 */     Utils.setLayoutData(label, gridData);
/*  74 */     Messages.setLanguageText(label, "configureWizard.finish.message");
/*     */   }
/*     */   
/*     */   public void finish()
/*     */   {
/*  79 */     ((ConfigureWizard)this.wizard).completed = true;
/*     */     
/*  81 */     int upLimit = ((ConfigureWizard)this.wizard).getUploadLimit();
/*  82 */     if (upLimit > 0) {
/*  83 */       COConfigurationManager.setParameter("Auto Upload Speed Enabled", false);
/*  84 */       COConfigurationManager.setParameter("Auto Upload Speed Seeding Enabled", false);
/*  85 */       COConfigurationManager.setParameter("Max Upload Speed KBs", upLimit / DisplayFormatters.getKinB());
/*  86 */       COConfigurationManager.setParameter("enable.seedingonly.upload.rate", false);
/*  87 */       COConfigurationManager.setParameter("max active torrents", ((ConfigureWizard)this.wizard).maxActiveTorrents);
/*  88 */       COConfigurationManager.setParameter("max downloads", ((ConfigureWizard)this.wizard).maxDownloads);
/*     */       try
/*     */       {
/*  91 */         SpeedManager sm = AzureusCoreFactory.getSingleton().getSpeedManager();
/*     */         
/*  93 */         boolean is_manual = ((ConfigureWizard)this.wizard).isUploadLimitManual();
/*     */         
/*  95 */         sm.setEstimatedUploadCapacityBytesPerSec(upLimit, is_manual ? 1.0F : 0.9F);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  99 */         Debug.out(e);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 104 */       COConfigurationManager.setParameter("Auto Adjust Transfer Defaults", false);
/* 105 */       COConfigurationManager.setParameter("Auto Adjust Transfer Defaults", true);
/*     */     }
/*     */     
/* 108 */     if (((ConfigureWizard)this.wizard).getWizardMode() != 0)
/*     */     {
/* 110 */       ((ConfigureWizard)this.wizard).close();
/*     */     }
/*     */     else {
/* 113 */       COConfigurationManager.setParameter("TCP.Listen.Port", ((ConfigureWizard)this.wizard).serverTCPListenPort);
/* 114 */       COConfigurationManager.setParameter("UDP.Listen.Port", ((ConfigureWizard)this.wizard).serverUDPListenPort);
/* 115 */       COConfigurationManager.setParameter("UDP.NonData.Listen.Port", ((ConfigureWizard)this.wizard).serverUDPListenPort);
/* 116 */       COConfigurationManager.setParameter("General_sDefaultTorrent_Directory", ((ConfigureWizard)this.wizard).torrentPath);
/*     */       
/* 118 */       if (((ConfigureWizard)this.wizard).hasDataPathChanged()) {
/* 119 */         COConfigurationManager.setParameter("Default save path", ((ConfigureWizard)this.wizard).getDataPath());
/*     */       }
/*     */       
/* 122 */       COConfigurationManager.setParameter("Wizard Completed", true);
/* 123 */       COConfigurationManager.save();
/* 124 */       ((ConfigureWizard)this.wizard).switchToClose();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isPreviousEnabled() {
/* 129 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/FinishPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */