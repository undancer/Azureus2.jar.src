/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
/*     */ import java.io.File;
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
/*     */ public class FilePanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   public FilePanel(ConfigureWizard wizard, IWizardPanel previous)
/*     */   {
/*  45 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  51 */     this.wizard.setTitle(MessageText.getString("configureWizard.file.title"));
/*     */     
/*  53 */     Composite rootPanel = this.wizard.getPanel();
/*  54 */     GridLayout layout = new GridLayout();
/*  55 */     layout.numColumns = 1;
/*  56 */     rootPanel.setLayout(layout);
/*     */     
/*  58 */     Composite panel = new Composite(rootPanel, 0);
/*  59 */     GridData gridData = new GridData(1808);
/*  60 */     Utils.setLayoutData(panel, gridData);
/*  61 */     layout = new GridLayout();
/*  62 */     layout.numColumns = 3;
/*  63 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  68 */     Label label = new Label(panel, 64);
/*  69 */     gridData = new GridData(768);
/*  70 */     gridData.horizontalSpan = 3;
/*  71 */     Utils.setLayoutData(label, gridData);
/*  72 */     Messages.setLanguageText(label, "configureWizard.file.message3");
/*     */     
/*  74 */     label = new Label(panel, 0);
/*  75 */     Utils.setLayoutData(label, new GridData());
/*  76 */     Messages.setLanguageText(label, "configureWizard.file.path");
/*     */     
/*  78 */     final Text textPath = new Text(panel, 2048);
/*  79 */     gridData = new GridData(768);
/*  80 */     gridData.widthHint = 100;
/*  81 */     Utils.setLayoutData(textPath, gridData);
/*  82 */     textPath.setText(((ConfigureWizard)this.wizard).getDataPath());
/*     */     
/*  84 */     Button browse = new Button(panel, 8);
/*  85 */     Messages.setLanguageText(browse, "configureWizard.file.browse");
/*  86 */     Utils.setLayoutData(browse, new GridData());
/*  87 */     browse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/*  89 */         DirectoryDialog dd = new DirectoryDialog(FilePanel.this.wizard.getWizardWindow());
/*  90 */         dd.setFilterPath(textPath.getText());
/*  91 */         String path = dd.open();
/*  92 */         if (path != null) {
/*  93 */           textPath.setText(path);
/*     */         }
/*     */         
/*     */       }
/*  97 */     });
/*  98 */     textPath.addListener(24, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 100 */         String path = textPath.getText();
/* 101 */         ((ConfigureWizard)FilePanel.this.wizard).setDataPath(path);
/*     */         try {
/* 103 */           File f = new File(path);
/* 104 */           if ((f.exists()) && (f.isDirectory())) {
/* 105 */             FilePanel.this.wizard.setErrorMessage("");
/* 106 */             FilePanel.this.wizard.setFinishEnabled(true);
/*     */           } else {
/* 108 */             FilePanel.this.wizard.setErrorMessage(MessageText.getString("configureWizard.file.invalidPath"));
/* 109 */             FilePanel.this.wizard.setFinishEnabled(false);
/*     */           }
/*     */         } catch (Exception e) {
/* 112 */           FilePanel.this.wizard.setErrorMessage(MessageText.getString("configureWizard.file.invalidPath"));
/* 113 */           FilePanel.this.wizard.setFinishEnabled(false);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 122 */     });
/* 123 */     Label label = new Label(panel, 64);
/* 124 */     gridData = new GridData(768);
/* 125 */     gridData.horizontalSpan = 3;
/* 126 */     Utils.setLayoutData(label, gridData);
/* 127 */     Messages.setLanguageText(label, "configureWizard.file.message1");
/*     */     
/* 129 */     label = new Label(panel, 0);
/* 130 */     Utils.setLayoutData(label, new GridData());
/* 131 */     Messages.setLanguageText(label, "configureWizard.file.path");
/*     */     
/* 133 */     final Text textPath = new Text(panel, 2048);
/* 134 */     gridData = new GridData(768);
/* 135 */     gridData.widthHint = 100;
/* 136 */     Utils.setLayoutData(textPath, gridData);
/* 137 */     textPath.setText(((ConfigureWizard)this.wizard).torrentPath);
/*     */     
/* 139 */     Button browse = new Button(panel, 8);
/* 140 */     Messages.setLanguageText(browse, "configureWizard.file.browse");
/* 141 */     Utils.setLayoutData(browse, new GridData());
/* 142 */     browse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 144 */         DirectoryDialog dd = new DirectoryDialog(FilePanel.this.wizard.getWizardWindow());
/* 145 */         dd.setFilterPath(textPath.getText());
/* 146 */         String path = dd.open();
/* 147 */         if (path != null) {
/* 148 */           textPath.setText(path);
/*     */         }
/*     */         
/*     */       }
/* 152 */     });
/* 153 */     textPath.addListener(24, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 155 */         String path = textPath.getText();
/* 156 */         ((ConfigureWizard)FilePanel.this.wizard).torrentPath = path;
/*     */         try {
/* 158 */           File f = new File(path);
/* 159 */           if ((f.exists()) && (f.isDirectory())) {
/* 160 */             FilePanel.this.wizard.setErrorMessage("");
/* 161 */             FilePanel.this.wizard.setFinishEnabled(true);
/*     */           } else {
/* 163 */             FilePanel.this.wizard.setErrorMessage(MessageText.getString("configureWizard.file.invalidPath"));
/* 164 */             FilePanel.this.wizard.setFinishEnabled(false);
/*     */           }
/*     */         } catch (Exception e) {
/* 167 */           FilePanel.this.wizard.setErrorMessage(MessageText.getString("configureWizard.file.invalidPath"));
/* 168 */           FilePanel.this.wizard.setFinishEnabled(false);
/*     */         }
/*     */         
/*     */       }
/* 172 */     });
/* 173 */     textPath.setText(((ConfigureWizard)this.wizard).torrentPath);
/*     */   }
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
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 202 */     return new FinishPanel((ConfigureWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/FilePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */