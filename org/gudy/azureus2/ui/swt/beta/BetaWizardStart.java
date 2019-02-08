/*     */ package org.gudy.azureus2.ui.swt.beta;
/*     */ 
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
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
/*     */ 
/*     */ 
/*     */ public class BetaWizardStart
/*     */   extends AbstractWizardPanel<BetaWizard>
/*     */ {
/*     */   protected BetaWizardStart(BetaWizard wizard)
/*     */   {
/*  48 */     super(wizard, null);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  54 */     ((BetaWizard)this.wizard).setTitle(MessageText.getString("beta.wizard.intro.title"));
/*  55 */     ((BetaWizard)this.wizard).setCurrentInfo("");
/*  56 */     ((BetaWizard)this.wizard).setPreviousEnabled(false);
/*  57 */     ((BetaWizard)this.wizard).setNextEnabled(false);
/*  58 */     ((BetaWizard)this.wizard).setFinishEnabled(true);
/*     */     
/*  60 */     Composite rootPanel = ((BetaWizard)this.wizard).getPanel();
/*  61 */     GridLayout layout = new GridLayout();
/*  62 */     layout.numColumns = 1;
/*  63 */     rootPanel.setLayout(layout);
/*     */     
/*     */ 
/*  66 */     Label info_label = new Label(rootPanel, 64);
/*  67 */     GridData gridData = new GridData(768);
/*  68 */     Utils.setLayoutData(info_label, gridData);
/*  69 */     info_label.setText(MessageText.getString("beta.wizard.info"));
/*     */     
/*  71 */     LinkLabel link = new LinkLabel(rootPanel, "beta.wizard.link", MessageText.getString("beta.wizard.link.url"));
/*  72 */     Label link_label = link.getlabel();
/*     */     
/*  74 */     gridData = new GridData(768);
/*  75 */     gridData.verticalIndent = 10;
/*  76 */     Utils.setLayoutData(link_label, gridData);
/*     */     
/*  78 */     Composite gRadio = new Composite(rootPanel, 0);
/*  79 */     gridData = new GridData(768);
/*  80 */     gridData.verticalIndent = 10;
/*  81 */     Utils.setLayoutData(gRadio, gridData);
/*  82 */     layout = new GridLayout();
/*  83 */     layout.numColumns = 1;
/*  84 */     gRadio.setLayout(layout);
/*     */     
/*     */ 
/*  87 */     Button off_button = new Button(gRadio, 16);
/*  88 */     Messages.setLanguageText(off_button, "beta.wizard.off");
/*  89 */     final Button on_button = new Button(gRadio, 16);
/*  90 */     Messages.setLanguageText(on_button, "beta.wizard.on");
/*     */     
/*  92 */     SelectionAdapter l = new SelectionAdapter()
/*     */     {
/*     */ 
/*     */       public void widgetSelected(SelectionEvent arg0)
/*     */       {
/*     */ 
/*  98 */         ((BetaWizard)BetaWizardStart.this.wizard).setBetaEnabled(on_button.getSelection());
/*     */       }
/* 100 */     };
/* 101 */     off_button.addSelectionListener(l);
/* 102 */     on_button.addSelectionListener(l);
/*     */     
/* 104 */     on_button.setSelection(((BetaWizard)this.wizard).getBetaEnabled());
/* 105 */     off_button.setSelection(!((BetaWizard)this.wizard).getBetaEnabled());
/*     */     
/* 107 */     LinkLabel forum = new LinkLabel(rootPanel, "beta.wizard.forum", MessageText.getString("beta.wizard.forum.url"));
/* 108 */     Label forum_label = link.getlabel();
/*     */     
/* 110 */     gridData = new GridData(768);
/* 111 */     gridData.verticalIndent = 10;
/* 112 */     Utils.setLayoutData(forum_label, gridData);
/*     */     
/* 114 */     Label version_label = new Label(rootPanel, 64);
/* 115 */     gridData = new GridData(768);
/* 116 */     gridData.verticalIndent = 10;
/* 117 */     Utils.setLayoutData(version_label, gridData);
/* 118 */     version_label.setText(MessageText.getString("beta.wizard.version", new String[] { "5.7.6.0" }));
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel getFinishPanel()
/*     */   {
/* 124 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public void finish()
/*     */   {
/* 130 */     ((BetaWizard)this.wizard).finish();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/beta/BetaWizardStart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */