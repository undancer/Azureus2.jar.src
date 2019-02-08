/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ public class ConfigureWizard
/*     */   extends Wizard
/*     */ {
/*     */   public static final int WIZARD_MODE_FULL = 0;
/*     */   public static final int WIZARD_MODE_SPEED_TEST_AUTO = 1;
/*     */   public static final int WIZARD_MODE_SPEED_TEST_MANUAL = 2;
/*     */   private int wizard_mode;
/*     */   private int connectionUploadLimit;
/*     */   private boolean uploadLimitManual;
/*     */   private int uploadLimit;
/*     */   int maxActiveTorrents;
/*     */   int maxDownloads;
/*  56 */   int serverTCPListenPort = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/*  57 */   int serverUDPListenPort = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/*     */   
/*     */   private String _dataPath;
/*     */   
/*     */   private boolean _dataPathChanged;
/*     */   String torrentPath;
/*  63 */   boolean completed = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConfigureWizard(boolean _modal, int _wizard_mode)
/*     */   {
/*  71 */     super("configureWizard.title", _modal);
/*     */     
/*  73 */     this.wizard_mode = _wizard_mode;
/*     */     
/*  75 */     IWizardPanel panel = this.wizard_mode == 0 ? new LanguagePanel(this, null) : new TransferPanel2(this, null);
/*     */     try {
/*  77 */       this.torrentPath = COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory");
/*     */     } catch (Exception e) {
/*  79 */       this.torrentPath = "";
/*     */     }
/*     */     
/*  82 */     this._dataPath = COConfigurationManager.getStringParameter("Default save path");
/*     */     
/*  84 */     setFirstPanel(panel);
/*     */   }
/*     */   
/*     */   public void onClose() {
/*     */     try {
/*  89 */       if ((!this.completed) && (this.wizard_mode != 1) && (!COConfigurationManager.getBooleanParameter("Wizard Completed")))
/*     */       {
/*     */ 
/*     */ 
/*  93 */         MessageBoxShell mb = new MessageBoxShell(MessageText.getString("wizard.close.confirmation"), MessageText.getString("wizard.close.message"), new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 100 */         mb.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int result) {
/* 102 */             if (result == 1) {
/* 103 */               COConfigurationManager.setParameter("Wizard Completed", true);
/* 104 */               COConfigurationManager.save();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 111 */       e.printStackTrace();
/*     */     }
/*     */     
/* 114 */     super.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getDataPath()
/*     */   {
/* 120 */     return this._dataPath;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setDataPath(String s)
/*     */   {
/* 127 */     this._dataPath = s;
/* 128 */     this._dataPathChanged = true;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean hasDataPathChanged()
/*     */   {
/* 134 */     return this._dataPathChanged;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setConnectionUploadLimit(int rate, boolean is_manual)
/*     */   {
/* 142 */     this.connectionUploadLimit = rate;
/*     */     
/* 144 */     if (this.connectionUploadLimit != 0)
/*     */     {
/* 146 */       this.uploadLimitManual = is_manual;
/*     */       
/* 148 */       this.uploadLimit = (this.connectionUploadLimit / 5 * 4);
/*     */       
/* 150 */       int kInB = DisplayFormatters.getKinB();
/*     */       
/* 152 */       this.uploadLimit = (this.uploadLimit / kInB * kInB);
/*     */       
/* 154 */       if (this.uploadLimit < 5 * kInB)
/*     */       {
/* 156 */         this.uploadLimit = (5 * kInB);
/*     */       }
/*     */       
/* 159 */       int nbMaxActive = (int)(Math.pow(this.uploadLimit / kInB, 0.34D) * 0.92D);
/* 160 */       int nbMaxUploads = (int)(Math.pow(this.uploadLimit / kInB, 0.25D) * 1.68D);
/* 161 */       int nbMaxDownloads = nbMaxActive * 4 / 5;
/*     */       
/* 163 */       if (nbMaxDownloads == 0) {
/* 164 */         nbMaxDownloads = 1;
/*     */       }
/*     */       
/* 167 */       if (nbMaxUploads > 50) {
/* 168 */         nbMaxUploads = 50;
/*     */       }
/*     */       
/* 171 */       this.maxActiveTorrents = nbMaxActive;
/* 172 */       this.maxDownloads = nbMaxDownloads;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 178 */       this.uploadLimitManual = false;
/* 179 */       this.uploadLimit = 0;
/* 180 */       this.maxActiveTorrents = 0;
/* 181 */       this.maxDownloads = 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getConnectionUploadLimit()
/*     */   {
/* 188 */     return this.connectionUploadLimit;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getUploadLimit()
/*     */   {
/* 194 */     return this.uploadLimit;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isUploadLimitManual()
/*     */   {
/* 200 */     return this.uploadLimitManual;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getWizardMode()
/*     */   {
/* 206 */     return this.wizard_mode;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/ConfigureWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */