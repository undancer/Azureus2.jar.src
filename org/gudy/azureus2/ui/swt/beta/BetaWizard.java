/*     */ package org.gudy.azureus2.ui.swt.beta;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ import org.gudy.azureus2.ui.swt.update.UpdateMonitor;
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
/*     */ public class BetaWizard
/*     */   extends Wizard
/*     */ {
/*  39 */   private boolean beta_enabled = COConfigurationManager.getBooleanParameter("Beta Programme Enabled");
/*     */   
/*  41 */   private boolean beta_was_enabled = this.beta_enabled;
/*     */   
/*     */   private boolean finished;
/*     */   
/*     */ 
/*     */   public BetaWizard()
/*     */   {
/*  48 */     super("beta.wizard.title", false);
/*     */     
/*  50 */     BetaWizardStart panel = new BetaWizardStart(this);
/*     */     
/*  52 */     setFirstPanel(panel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onClose()
/*     */   {
/*  58 */     super.onClose();
/*     */     
/*  60 */     if (this.finished)
/*     */     {
/*  62 */       COConfigurationManager.setParameter("Beta Programme Enabled", this.beta_enabled);
/*     */       
/*  64 */       if ((!this.beta_enabled) && (Constants.IS_CVS_VERSION))
/*     */       {
/*  66 */         MessageBoxShell mb = new MessageBoxShell(34, MessageText.getString("beta.wizard.disable.title"), MessageText.getString("beta.wizard.disable.text"));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*  71 */         mb.open(null);
/*     */       }
/*  73 */       else if ((this.beta_enabled) && (!this.beta_was_enabled))
/*     */       {
/*  75 */         UpdateMonitor.getSingleton(AzureusCoreFactory.getSingleton()).performCheck(true, false, false, new UpdateCheckInstanceListener()
/*     */         {
/*     */           public void cancelled(UpdateCheckInstance instance) {}
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void complete(UpdateCheckInstance instance) {}
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean getBetaEnabled()
/*     */   {
/*  98 */     return this.beta_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setBetaEnabled(boolean b)
/*     */   {
/* 105 */     this.beta_enabled = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public void finish()
/*     */   {
/* 111 */     this.finished = true;
/*     */     
/* 113 */     close();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/beta/BetaWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */