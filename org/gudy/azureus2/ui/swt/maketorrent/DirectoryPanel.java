/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*     */ public class DirectoryPanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */ {
/*     */   private Text file;
/*     */   
/*     */   public DirectoryPanel(NewTorrentWizard wizard, IWizardPanel<NewTorrentWizard> previous)
/*     */   {
/*  52 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  61 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.directory"));
/*  62 */     ((NewTorrentWizard)this.wizard).setCurrentInfo(MessageText.getString("wizard.choosedirectory"));
/*  63 */     Composite panel = ((NewTorrentWizard)this.wizard).getPanel();
/*  64 */     GridLayout layout = new GridLayout();
/*  65 */     layout.numColumns = 3;
/*  66 */     panel.setLayout(layout);
/*  67 */     Label label = new Label(panel, 0);
/*  68 */     Messages.setLanguageText(label, "wizard.directory");
/*  69 */     this.file = new Text(panel, 2048);
/*  70 */     this.file.addModifyListener(new ModifyListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void modifyText(ModifyEvent arg0)
/*     */       {
/*     */ 
/*  77 */         String fName = DirectoryPanel.this.file.getText();
/*  78 */         ((NewTorrentWizard)DirectoryPanel.this.wizard).directoryPath = fName;
/*  79 */         String error = "";
/*  80 */         if (!fName.equals("")) {
/*  81 */           File f = new File(DirectoryPanel.this.file.getText());
/*  82 */           if ((!f.exists()) || (!f.isDirectory())) {
/*  83 */             error = MessageText.getString("wizard.invaliddirectory");
/*     */           } else {
/*  85 */             String parent = f.getParent();
/*     */             
/*  87 */             if (parent != null)
/*     */             {
/*  89 */               ((NewTorrentWizard)DirectoryPanel.this.wizard).setDefaultOpenDir(parent);
/*     */             }
/*     */           }
/*     */         }
/*  93 */         ((NewTorrentWizard)DirectoryPanel.this.wizard).setErrorMessage(error);
/*  94 */         ((NewTorrentWizard)DirectoryPanel.this.wizard).setNextEnabled((!((NewTorrentWizard)DirectoryPanel.this.wizard).directoryPath.equals("")) && (error.equals("")));
/*     */       }
/*  96 */     });
/*  97 */     this.file.setText(((NewTorrentWizard)this.wizard).directoryPath);
/*  98 */     GridData gridData = new GridData(768);
/*  99 */     this.file.setLayoutData(gridData);
/* 100 */     Button browse = new Button(panel, 8);
/* 101 */     browse.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/* 108 */         DirectoryDialog fd = new DirectoryDialog(((NewTorrentWizard)DirectoryPanel.this.wizard).getWizardWindow());
/* 109 */         if ((((NewTorrentWizard)DirectoryPanel.this.wizard).getErrorMessage().equals("")) && (!((NewTorrentWizard)DirectoryPanel.this.wizard).directoryPath.equals(""))) {
/* 110 */           fd.setFilterPath(((NewTorrentWizard)DirectoryPanel.this.wizard).directoryPath);
/*     */         } else {
/* 112 */           String def = ((NewTorrentWizard)DirectoryPanel.this.wizard).getDefaultOpenDir();
/*     */           
/* 114 */           if (def.length() > 0)
/*     */           {
/* 116 */             fd.setFilterPath(def);
/*     */           }
/*     */         }
/* 119 */         String f = fd.open();
/* 120 */         if (f != null) {
/* 121 */           DirectoryPanel.this.file.setText(f);
/*     */           
/* 123 */           File ff = new File(f);
/*     */           
/* 125 */           String parent = ff.getParent();
/*     */           
/* 127 */           if (parent != null)
/*     */           {
/* 129 */             ((NewTorrentWizard)DirectoryPanel.this.wizard).setDefaultOpenDir(parent);
/*     */           }
/*     */         }
/*     */       }
/* 133 */     });
/* 134 */     Messages.setLanguageText(browse, "wizard.browse");
/*     */     
/* 136 */     label = new Label(panel, 0);
/* 137 */     gridData = new GridData(768);
/* 138 */     gridData.horizontalSpan = 3;
/* 139 */     label.setLayoutData(gridData);
/* 140 */     label.setText("\n");
/*     */     
/* 142 */     label = new Label(panel, 0);
/* 143 */     gridData = new GridData(768);
/* 144 */     gridData.horizontalSpan = 3;
/* 145 */     label.setLayoutData(gridData);
/* 146 */     label.setForeground(Colors.blue);
/* 147 */     Messages.setLanguageText(label, "wizard.hint.directory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IWizardPanel<NewTorrentWizard> getNextPanel()
/*     */   {
/* 157 */     return new SavePathPanel((NewTorrentWizard)this.wizard, this);
/*     */   }
/*     */   
/*     */   public void setFilename(String filename) {
/* 161 */     this.file.setText(filename);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/DirectoryPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */