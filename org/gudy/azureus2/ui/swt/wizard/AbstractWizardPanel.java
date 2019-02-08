/*    */ package org.gudy.azureus2.ui.swt.wizard;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractWizardPanel<W extends Wizard>
/*    */   implements IWizardPanel<W>
/*    */ {
/*    */   protected IWizardPanel<W> previousPanel;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected W wizard;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AbstractWizardPanel(W wizard, IWizardPanel<W> previousPanel)
/*    */   {
/* 34 */     this.previousPanel = previousPanel;
/* 35 */     this.wizard = wizard;
/*    */   }
/*    */   
/*    */   public boolean isPreviousEnabled() {
/* 39 */     return this.previousPanel != null;
/*    */   }
/*    */   
/*    */   public boolean isNextEnabled() {
/* 43 */     return false;
/*    */   }
/*    */   
/*    */   public boolean isFinishEnabled() {
/* 47 */     return false;
/*    */   }
/*    */   
/*    */   public IWizardPanel<W> getPreviousPanel() {
/* 51 */     return this.previousPanel;
/*    */   }
/*    */   
/*    */   public IWizardPanel<W> getNextPanel() {
/* 55 */     return null;
/*    */   }
/*    */   
/*    */   public IWizardPanel<W> getFinishPanel() {
/* 59 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isFinishSelectionOK()
/*    */   {
/* 65 */     return true;
/*    */   }
/*    */   
/*    */   public void cancelled() {}
/*    */   
/*    */   public void finish() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/wizard/AbstractWizardPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */