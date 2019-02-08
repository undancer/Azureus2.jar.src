/*    */ package org.gudy.azureus2.ui.swt.pluginsuninstaller;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.layout.GridLayout;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*    */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*    */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UIPWFinishPanel
/*    */   extends AbstractWizardPanel
/*    */ {
/*    */   public UIPWFinishPanel(Wizard wizard, IWizardPanel previous)
/*    */   {
/* 44 */     super(wizard, previous);
/*    */   }
/*    */   
/*    */ 
/*    */   public void show()
/*    */   {
/* 50 */     this.wizard.setTitle(MessageText.getString("uninstallPluginsWizard.finish.title"));
/* 51 */     this.wizard.setErrorMessage("");
/*    */     
/* 53 */     Composite rootPanel = this.wizard.getPanel();
/* 54 */     GridLayout layout = new GridLayout();
/* 55 */     layout.numColumns = 1;
/* 56 */     rootPanel.setLayout(layout);
/*    */     
/* 58 */     Composite panel = new Composite(rootPanel, 0);
/* 59 */     GridData gridData = new GridData(772);
/* 60 */     panel.setLayoutData(gridData);
/* 61 */     layout = new GridLayout();
/* 62 */     layout.numColumns = 1;
/* 63 */     panel.setLayout(layout);
/*    */     
/* 65 */     Label lblExplanation = new Label(panel, 64);
/* 66 */     GridData data = new GridData(1808);
/* 67 */     lblExplanation.setLayoutData(data);
/* 68 */     Messages.setLanguageText(lblExplanation, "uninstallPluginsWizard.finish.explanation");
/*    */   }
/*    */   
/*    */   public void finish() {
/* 72 */     ((UnInstallPluginWizard)this.wizard).performUnInstall();
/* 73 */     this.wizard.switchToClose();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsuninstaller/UIPWFinishPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */