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
/*     */ public class ImportTorrentWizardOutputPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*  51 */   protected boolean file_valid = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImportTorrentWizardOutputPanel(Wizard wizard, IWizardPanel previous)
/*     */   {
/*  58 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  64 */     this.wizard.setTitle(MessageText.getString("importTorrentWizard.torrentfile.title"));
/*  65 */     Composite rootPanel = this.wizard.getPanel();
/*  66 */     GridLayout layout = new GridLayout();
/*  67 */     layout.numColumns = 1;
/*  68 */     rootPanel.setLayout(layout);
/*     */     
/*  70 */     Composite panel = new Composite(rootPanel, 0);
/*  71 */     GridData gridData = new GridData(772);
/*  72 */     Utils.setLayoutData(panel, gridData);
/*  73 */     layout = new GridLayout();
/*  74 */     layout.numColumns = 3;
/*  75 */     panel.setLayout(layout);
/*     */     
/*  77 */     Label label = new Label(panel, 64);
/*  78 */     gridData = new GridData();
/*  79 */     gridData.horizontalSpan = 3;
/*  80 */     gridData.widthHint = 380;
/*  81 */     Utils.setLayoutData(label, gridData);
/*  82 */     Messages.setLanguageText(label, "importTorrentWizard.torrentfile.message");
/*     */     
/*  84 */     label = new Label(panel, 0);
/*  85 */     Messages.setLanguageText(label, "importTorrentWizard.torrentfile.path");
/*     */     
/*  87 */     final Text textPath = new Text(panel, 2048);
/*  88 */     gridData = new GridData(768);
/*  89 */     Utils.setLayoutData(textPath, gridData);
/*  90 */     textPath.setText(((ImportTorrentWizard)this.wizard).getTorrentFile());
/*     */     
/*  92 */     Button browse = new Button(panel, 8);
/*  93 */     Messages.setLanguageText(browse, "importTorrentWizard.torrentfile.browse");
/*  94 */     browse.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*  98 */         FileDialog fd = new FileDialog(ImportTorrentWizardOutputPanel.this.wizard.getWizardWindow(), 8192);
/*     */         
/* 100 */         fd.setFileName(textPath.getText());
/*     */         
/* 102 */         fd.setFilterExtensions(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*     */         
/* 104 */         String path = fd.open();
/*     */         
/* 106 */         if (path != null)
/*     */         {
/* 108 */           textPath.setText(path);
/*     */         }
/*     */         
/*     */       }
/* 112 */     });
/* 113 */     textPath.addListener(24, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 116 */         String path = textPath.getText();
/*     */         
/* 118 */         ImportTorrentWizardOutputPanel.this.pathSet(path);
/*     */       }
/*     */       
/* 121 */     });
/* 122 */     textPath.setText(((ImportTorrentWizard)this.wizard).getTorrentFile());
/*     */     
/* 124 */     textPath.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void pathSet(String path)
/*     */   {
/* 131 */     ((ImportTorrentWizard)this.wizard).setTorrentFile(path);
/*     */     
/* 133 */     this.file_valid = false;
/*     */     
/*     */     try
/*     */     {
/* 137 */       File f = new File(path);
/*     */       
/* 139 */       if (f.exists())
/*     */       {
/* 141 */         if (f.isFile()) {
/* 142 */           this.wizard.setErrorMessage("");
/*     */           
/* 144 */           this.file_valid = true;
/*     */         }
/*     */         else {
/* 147 */           this.wizard.setErrorMessage(MessageText.getString("importTorrentWizard.torrentfile.invalidPath"));
/*     */         }
/*     */       }
/*     */       else {
/* 151 */         this.wizard.setErrorMessage("");
/*     */         
/* 153 */         this.file_valid = true;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 157 */       this.wizard.setErrorMessage(MessageText.getString("importTorrentWizard.torrentfile.invalidPath"));
/*     */     }
/*     */     
/* 160 */     this.wizard.setFinishEnabled(this.file_valid);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishEnabled()
/*     */   {
/* 166 */     return this.file_valid;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishSelectionOK()
/*     */   {
/* 172 */     return ((ImportTorrentWizard)this.wizard).performImport();
/*     */   }
/*     */   
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 177 */     return new ImportTorrentWizardFinishPanel((ImportTorrentWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/importtorrent/wizard/ImportTorrentWizardOutputPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */