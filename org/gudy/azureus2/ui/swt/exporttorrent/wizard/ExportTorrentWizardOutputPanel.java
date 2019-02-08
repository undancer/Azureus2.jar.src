/*     */ package org.gudy.azureus2.ui.swt.exporttorrent.wizard;
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
/*     */ public class ExportTorrentWizardOutputPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*  52 */   protected boolean file_valid = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExportTorrentWizardOutputPanel(Wizard wizard, IWizardPanel previous)
/*     */   {
/*  59 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  65 */     this.wizard.setTitle(MessageText.getString("exportTorrentWizard.exportfile.title"));
/*  66 */     Composite rootPanel = this.wizard.getPanel();
/*  67 */     GridLayout layout = new GridLayout();
/*  68 */     layout.numColumns = 1;
/*  69 */     rootPanel.setLayout(layout);
/*     */     
/*  71 */     Composite panel = new Composite(rootPanel, 0);
/*  72 */     GridData gridData = new GridData(772);
/*  73 */     Utils.setLayoutData(panel, gridData);
/*  74 */     layout = new GridLayout();
/*  75 */     layout.numColumns = 3;
/*  76 */     panel.setLayout(layout);
/*     */     
/*  78 */     Label label = new Label(panel, 64);
/*  79 */     gridData = new GridData();
/*  80 */     gridData.horizontalSpan = 3;
/*  81 */     gridData.widthHint = 380;
/*  82 */     Utils.setLayoutData(label, gridData);
/*  83 */     Messages.setLanguageText(label, "exportTorrentWizard.exportfile.message");
/*     */     
/*  85 */     label = new Label(panel, 0);
/*  86 */     Messages.setLanguageText(label, "exportTorrentWizard.exportfile.path");
/*     */     
/*  88 */     final Text textPath = new Text(panel, 2048);
/*  89 */     gridData = new GridData(768);
/*  90 */     Utils.setLayoutData(textPath, gridData);
/*  91 */     textPath.setText(((ExportTorrentWizard)this.wizard).getExportFile());
/*     */     
/*  93 */     Button browse = new Button(panel, 8);
/*  94 */     Messages.setLanguageText(browse, "exportTorrentWizard.exportfile.browse");
/*  95 */     browse.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*  99 */         FileDialog fd = new FileDialog(ExportTorrentWizardOutputPanel.this.wizard.getWizardWindow(), 8192);
/*     */         
/* 101 */         fd.setFileName(textPath.getText());
/*     */         
/* 103 */         fd.setFilterExtensions(new String[] { "*.xml", Constants.FILE_WILDCARD });
/*     */         
/* 105 */         String path = fd.open();
/*     */         
/* 107 */         if (path != null)
/*     */         {
/* 109 */           textPath.setText(path);
/*     */         }
/*     */         
/*     */       }
/* 113 */     });
/* 114 */     textPath.addListener(24, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 117 */         String path = textPath.getText();
/*     */         
/* 119 */         ExportTorrentWizardOutputPanel.this.pathSet(path);
/*     */       }
/*     */       
/* 122 */     });
/* 123 */     textPath.setText(((ExportTorrentWizard)this.wizard).getExportFile());
/*     */     
/* 125 */     textPath.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void pathSet(String path)
/*     */   {
/* 132 */     ((ExportTorrentWizard)this.wizard).setExportFile(path);
/*     */     
/* 134 */     this.file_valid = false;
/*     */     
/*     */     try
/*     */     {
/* 138 */       File f = new File(path);
/*     */       
/* 140 */       if (f.exists())
/*     */       {
/* 142 */         if (f.isFile()) {
/* 143 */           this.wizard.setErrorMessage("");
/*     */           
/* 145 */           this.file_valid = true;
/*     */         }
/*     */         else {
/* 148 */           this.wizard.setErrorMessage(MessageText.getString("exportTorrentWizard.exportfile.invalidPath"));
/*     */         }
/*     */       }
/*     */       else {
/* 152 */         this.wizard.setErrorMessage("");
/*     */         
/* 154 */         this.file_valid = true;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 158 */       this.wizard.setErrorMessage(MessageText.getString("exportTorrentWizard.exportfile.invalidPath"));
/*     */     }
/*     */     
/* 161 */     this.wizard.setFinishEnabled(this.file_valid);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishEnabled()
/*     */   {
/* 167 */     return this.file_valid;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishSelectionOK()
/*     */   {
/* 173 */     return ((ExportTorrentWizard)this.wizard).performExport();
/*     */   }
/*     */   
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 178 */     return new ExportTorrentWizardFinishPanel((ExportTorrentWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/exporttorrent/wizard/ExportTorrentWizardOutputPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */