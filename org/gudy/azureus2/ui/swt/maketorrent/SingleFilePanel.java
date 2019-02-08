/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
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
/*     */ public class SingleFilePanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */ {
/*     */   private Text file;
/*     */   
/*     */   public SingleFilePanel(NewTorrentWizard wizard, AbstractWizardPanel<NewTorrentWizard> previous)
/*     */   {
/*  51 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  60 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.singlefile"));
/*  61 */     ((NewTorrentWizard)this.wizard).setCurrentInfo(MessageText.getString("wizard.choosefile"));
/*  62 */     Composite panel = ((NewTorrentWizard)this.wizard).getPanel();
/*  63 */     GridLayout layout = new GridLayout();
/*  64 */     layout.numColumns = 3;
/*  65 */     panel.setLayout(layout);
/*     */     
/*  67 */     Label label = new Label(panel, 0);
/*  68 */     Messages.setLanguageText(label, "wizard.file");
/*     */     
/*  70 */     this.file = new Text(panel, 2048);
/*  71 */     this.file.addModifyListener(new ModifyListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void modifyText(ModifyEvent arg0)
/*     */       {
/*     */ 
/*  78 */         String fName = SingleFilePanel.this.file.getText();
/*  79 */         ((NewTorrentWizard)SingleFilePanel.this.wizard).singlePath = fName;
/*  80 */         String error = "";
/*  81 */         if (!fName.equals("")) {
/*  82 */           File f = new File(SingleFilePanel.this.file.getText());
/*  83 */           if ((!f.exists()) || (f.isDirectory())) {
/*  84 */             error = MessageText.getString("wizard.invalidfile");
/*     */           } else {
/*  86 */             String parent = f.getParent();
/*     */             
/*  88 */             if (parent != null)
/*     */             {
/*  90 */               ((NewTorrentWizard)SingleFilePanel.this.wizard).setDefaultOpenDir(parent);
/*     */             }
/*     */           }
/*     */         }
/*  94 */         ((NewTorrentWizard)SingleFilePanel.this.wizard).setErrorMessage(error);
/*  95 */         ((NewTorrentWizard)SingleFilePanel.this.wizard).setNextEnabled((!((NewTorrentWizard)SingleFilePanel.this.wizard).singlePath.equals("")) && (error.equals("")));
/*     */       }
/*  97 */     });
/*  98 */     this.file.setText(((NewTorrentWizard)this.wizard).singlePath);
/*  99 */     GridData gridData = new GridData(768);
/* 100 */     this.file.setLayoutData(gridData);
/*     */     
/* 102 */     Button browse = new Button(panel, 8);
/* 103 */     browse.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/* 110 */         FileDialog fd = new FileDialog(((NewTorrentWizard)SingleFilePanel.this.wizard).getWizardWindow());
/* 111 */         if ((((NewTorrentWizard)SingleFilePanel.this.wizard).getErrorMessage().equals("")) && (!((NewTorrentWizard)SingleFilePanel.this.wizard).singlePath.equals(""))) {
/* 112 */           fd.setFileName(((NewTorrentWizard)SingleFilePanel.this.wizard).singlePath);
/*     */         } else {
/* 114 */           String def = ((NewTorrentWizard)SingleFilePanel.this.wizard).getDefaultOpenDir();
/*     */           
/* 116 */           if ((def.length() > 0) && (new File(def).isDirectory()))
/*     */           {
/* 118 */             fd.setFilterPath(def);
/*     */           }
/*     */         }
/* 121 */         String f = fd.open();
/* 122 */         if (f != null) {
/* 123 */           SingleFilePanel.this.file.setText(f);
/*     */           
/* 125 */           File ff = new File(f);
/*     */           
/* 127 */           String parent = ff.getParent();
/*     */           
/* 129 */           if (parent != null)
/*     */           {
/* 131 */             ((NewTorrentWizard)SingleFilePanel.this.wizard).setDefaultOpenDir(parent);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 136 */     });
/* 137 */     Messages.setLanguageText(browse, "wizard.browse");
/*     */     
/* 139 */     label = new Label(panel, 0);
/* 140 */     gridData = new GridData(768);
/* 141 */     gridData.horizontalSpan = 3;
/* 142 */     label.setLayoutData(gridData);
/* 143 */     label.setText("\n");
/*     */     
/* 145 */     label = new Label(panel, 0);
/* 146 */     gridData = new GridData(768);
/* 147 */     gridData.horizontalSpan = 3;
/* 148 */     label.setLayoutData(gridData);
/* 149 */     label.setForeground(Colors.blue);
/* 150 */     Messages.setLanguageText(label, "wizard.hint.file");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IWizardPanel<NewTorrentWizard> getNextPanel()
/*     */   {
/* 160 */     return new SavePathPanel((NewTorrentWizard)this.wizard, this);
/*     */   }
/*     */   
/*     */   public void setFilename(String filename) {
/* 164 */     this.file.setText(filename);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/SingleFilePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */