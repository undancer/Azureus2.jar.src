/*    */ package org.gudy.azureus2.ui.swt.importtorrent.wizard;
/*    */ 
/*    */ import org.eclipse.swt.layout.GridData;
/*    */ import org.eclipse.swt.layout.GridLayout;
/*    */ import org.eclipse.swt.widgets.Composite;
/*    */ import org.eclipse.swt.widgets.Label;
/*    */ import org.gudy.azureus2.core3.internat.MessageText;
/*    */ import org.gudy.azureus2.ui.swt.Messages;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ImportTorrentWizardFinishPanel
/*    */   extends AbstractWizardPanel
/*    */ {
/*    */   public ImportTorrentWizardFinishPanel(Wizard wizard, IWizardPanel previous)
/*    */   {
/* 49 */     super(wizard, previous);
/*    */   }
/*    */   
/*    */   public void show() {
/* 53 */     this.wizard.setTitle(MessageText.getString("importTorrentWizard.finish.title"));
/* 54 */     Composite rootPanel = this.wizard.getPanel();
/* 55 */     GridLayout layout = new GridLayout();
/* 56 */     layout.numColumns = 1;
/* 57 */     rootPanel.setLayout(layout);
/*    */     
/* 59 */     Composite panel = new Composite(rootPanel, 0);
/* 60 */     GridData gridData = new GridData(772);
/* 61 */     Utils.setLayoutData(panel, gridData);
/* 62 */     layout = new GridLayout();
/* 63 */     layout.numColumns = 3;
/* 64 */     panel.setLayout(layout);
/*    */     
/* 66 */     Label label = new Label(panel, 64);
/* 67 */     gridData = new GridData();
/* 68 */     gridData.horizontalSpan = 3;
/* 69 */     gridData.widthHint = 380;
/* 70 */     Utils.setLayoutData(label, gridData);
/* 71 */     Messages.setLanguageText(label, "importTorrentWizard.finish.message");
/*    */   }
/*    */   
/*    */   public boolean isPreviousEnabled()
/*    */   {
/* 76 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public void finish()
/*    */   {
/* 82 */     this.wizard.switchToClose();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/importtorrent/wizard/ImportTorrentWizardFinishPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */