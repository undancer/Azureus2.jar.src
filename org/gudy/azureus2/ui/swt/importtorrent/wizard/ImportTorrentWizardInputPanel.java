/*     */ package org.gudy.azureus2.ui.swt.importtorrent.wizard;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ImportTorrentWizardInputPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*  52 */   protected boolean file_valid = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImportTorrentWizardInputPanel(Wizard wizard, IWizardPanel previous)
/*     */   {
/*  59 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  66 */     this.wizard.setTitle(MessageText.getString("importTorrentWizard.importfile.title"));
/*     */     
/*  68 */     Composite rootPanel = this.wizard.getPanel();
/*  69 */     GridLayout layout = new GridLayout();
/*  70 */     layout.numColumns = 1;
/*  71 */     rootPanel.setLayout(layout);
/*     */     
/*  73 */     Composite panel = new Composite(rootPanel, 0);
/*  74 */     GridData gridData = new GridData(772);
/*  75 */     Utils.setLayoutData(panel, gridData);
/*  76 */     layout = new GridLayout();
/*  77 */     layout.numColumns = 3;
/*  78 */     panel.setLayout(layout);
/*     */     
/*  80 */     Label label = new Label(panel, 64);
/*  81 */     gridData = new GridData();
/*  82 */     gridData.horizontalSpan = 3;
/*  83 */     gridData.widthHint = 380;
/*  84 */     Utils.setLayoutData(label, gridData);
/*  85 */     Messages.setLanguageText(label, "importTorrentWizard.importfile.message");
/*     */     
/*  87 */     label = new Label(panel, 0);
/*  88 */     Messages.setLanguageText(label, "importTorrentWizard.importfile.path");
/*     */     
/*  90 */     final Text textPath = new Text(panel, 2048);
/*  91 */     gridData = new GridData(768);
/*  92 */     Utils.setLayoutData(textPath, gridData);
/*  93 */     textPath.setText("");
/*     */     
/*  95 */     Button browse = new Button(panel, 8);
/*  96 */     Messages.setLanguageText(browse, "importTorrentWizard.importfile.browse");
/*  97 */     browse.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {
/* 100 */         FileDialog fd = new FileDialog(ImportTorrentWizardInputPanel.this.wizard.getWizardWindow());
/*     */         
/* 102 */         fd.setFileName(textPath.getText());
/*     */         
/* 104 */         fd.setFilterExtensions(new String[] { "*.xml", Constants.FILE_WILDCARD });
/*     */         
/* 106 */         String path = fd.open();
/*     */         
/* 108 */         if (path != null)
/*     */         {
/* 110 */           textPath.setText(path);
/*     */         }
/*     */         
/*     */       }
/* 114 */     });
/* 115 */     textPath.addListener(24, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event)
/*     */       {
/* 119 */         String path = textPath.getText();
/*     */         
/* 121 */         ((ImportTorrentWizard)ImportTorrentWizardInputPanel.this.wizard).setImportFile(path);
/*     */         
/* 123 */         ImportTorrentWizardInputPanel.this.file_valid = false;
/*     */         
/*     */         try
/*     */         {
/* 127 */           File f = new File(path);
/*     */           
/* 129 */           if (f.exists())
/*     */           {
/* 131 */             if (f.isFile())
/*     */             {
/* 133 */               ImportTorrentWizardInputPanel.this.file_valid = true;
/*     */               
/* 135 */               ImportTorrentWizardInputPanel.this.wizard.setErrorMessage("");
/*     */             }
/*     */             else {
/* 138 */               ImportTorrentWizardInputPanel.this.wizard.setErrorMessage(MessageText.getString("importTorrentWizard.importfile.invalidPath"));
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e) {
/* 143 */           ImportTorrentWizardInputPanel.this.wizard.setErrorMessage(MessageText.getString("importTorrentWizard.importfile.invalidPath"));
/*     */         }
/*     */         
/* 146 */         ImportTorrentWizardInputPanel.this.wizard.setNextEnabled(ImportTorrentWizardInputPanel.this.file_valid);
/*     */       }
/*     */       
/* 149 */     });
/* 150 */     textPath.setText(((ImportTorrentWizard)this.wizard).getImportFile());
/*     */     
/* 152 */     textPath.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel getNextPanel()
/*     */   {
/* 158 */     return new ImportTorrentWizardOutputPanel(this.wizard, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 164 */     return this.file_valid;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/importtorrent/wizard/ImportTorrentWizardInputPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */