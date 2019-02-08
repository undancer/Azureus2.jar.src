/*    */ package org.gudy.azureus2.ui.swt.pluginsinstaller;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.layout.GridLayout;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*    */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*    */ public class IPWFinishPanel
/*    */   extends AbstractWizardPanel<InstallPluginWizard>
/*    */ {
/*    */   public IPWFinishPanel(InstallPluginWizard wizard, IWizardPanel<InstallPluginWizard> previous)
/*    */   {
/* 43 */     super(wizard, previous);
/*    */   }
/*    */   
/*    */ 
/*    */   public void show()
/*    */   {
/* 49 */     ((InstallPluginWizard)this.wizard).setTitle(MessageText.getString("installPluginsWizard.finish.title"));
/* 50 */     ((InstallPluginWizard)this.wizard).setErrorMessage("");
/*    */     
/* 52 */     Composite rootPanel = ((InstallPluginWizard)this.wizard).getPanel();
/* 53 */     GridLayout layout = new GridLayout();
/* 54 */     layout.numColumns = 1;
/* 55 */     rootPanel.setLayout(layout);
/*    */     
/* 57 */     Composite panel = new Composite(rootPanel, 0);
/* 58 */     GridData gridData = new GridData(772);
/* 59 */     panel.setLayoutData(gridData);
/* 60 */     layout = new GridLayout();
/* 61 */     layout.numColumns = 1;
/* 62 */     panel.setLayout(layout);
/*    */     
/* 64 */     Label lblExplanation = new Label(panel, 64);
/* 65 */     GridData data = new GridData(1808);
/* 66 */     lblExplanation.setLayoutData(data);
/* 67 */     Messages.setLanguageText(lblExplanation, "installPluginsWizard.finish.explanation");
/*    */   }
/*    */   
/*    */   public void finish() {
/* 71 */     ((InstallPluginWizard)this.wizard).performInstall();
/* 72 */     ((InstallPluginWizard)this.wizard).switchToClose();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsinstaller/IPWFinishPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */