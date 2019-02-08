/*    */ package org.gudy.azureus2.ui.swt.exporttorrent.wizard;
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
/*    */ 
/*    */ public class ExportTorrentWizardFinishPanel
/*    */   extends AbstractWizardPanel
/*    */ {
/*    */   public ExportTorrentWizardFinishPanel(ExportTorrentWizard wizard, IWizardPanel previous)
/*    */   {
/* 50 */     super(wizard, previous);
/*    */   }
/*    */   
/*    */   public void show() {
/* 54 */     this.wizard.setTitle(MessageText.getString("exportTorrentWizard.finish.title"));
/* 55 */     Composite rootPanel = this.wizard.getPanel();
/* 56 */     GridLayout layout = new GridLayout();
/* 57 */     layout.numColumns = 1;
/* 58 */     rootPanel.setLayout(layout);
/*    */     
/* 60 */     Composite panel = new Composite(rootPanel, 0);
/* 61 */     GridData gridData = new GridData(772);
/* 62 */     Utils.setLayoutData(panel, gridData);
/* 63 */     layout = new GridLayout();
/* 64 */     layout.numColumns = 3;
/* 65 */     panel.setLayout(layout);
/*    */     
/* 67 */     Label label = new Label(panel, 64);
/* 68 */     gridData = new GridData();
/* 69 */     gridData.horizontalSpan = 3;
/* 70 */     gridData.widthHint = 380;
/* 71 */     Utils.setLayoutData(label, gridData);
/* 72 */     Messages.setLanguageText(label, "exportTorrentWizard.finish.message");
/*    */   }
/*    */   
/*    */   public boolean isPreviousEnabled() {
/* 76 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public void finish()
/*    */   {
/* 82 */     this.wizard.switchToClose();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/exporttorrent/wizard/ExportTorrentWizardFinishPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */