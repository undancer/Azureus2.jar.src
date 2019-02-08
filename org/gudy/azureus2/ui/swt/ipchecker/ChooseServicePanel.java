/*     */ package org.gudy.azureus2.ui.swt.ipchecker;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPChecker;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerFactory;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class ChooseServicePanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   private Combo servicesList;
/*     */   private ExternalIPCheckerService[] services;
/*     */   Label serviceDescription;
/*     */   Label serviceUrl;
/*     */   
/*     */   public ChooseServicePanel(IpCheckerWizard wizard, IWizardPanel previousPanel)
/*     */   {
/*  56 */     super(wizard, previousPanel);
/*     */   }
/*     */   
/*     */   public void show() {
/*  60 */     this.wizard.setTitle(MessageText.getString("ipCheckerWizard.service"));
/*  61 */     this.wizard.setCurrentInfo(MessageText.getString("ipCheckerWizard.chooseService"));
/*  62 */     Composite rootPanel = this.wizard.getPanel();
/*  63 */     GridLayout layout = new GridLayout();
/*  64 */     layout.numColumns = 2;
/*  65 */     rootPanel.setLayout(layout);
/*     */     
/*  67 */     Label label = new Label(rootPanel, 64);
/*  68 */     GridData gridData = new GridData();
/*  69 */     gridData.widthHint = 380;
/*  70 */     gridData.horizontalSpan = 2;
/*  71 */     Utils.setLayoutData(label, gridData);
/*  72 */     label.setText(MessageText.getString("ipCheckerWizard.explanations"));
/*     */     
/*  74 */     this.servicesList = new Combo(rootPanel, 8);
/*  75 */     gridData = new GridData(768);
/*  76 */     gridData.horizontalSpan = 2;
/*  77 */     Utils.setLayoutData(this.servicesList, gridData);
/*     */     
/*  79 */     this.services = ExternalIPCheckerFactory.create().getServices();
/*     */     
/*  81 */     for (int i = 0; i < this.services.length; i++) {
/*  82 */       this.servicesList.add(this.services[i].getName());
/*     */     }
/*     */     
/*  85 */     label = new Label(rootPanel, 0);
/*  86 */     label.setText(MessageText.getString("ipCheckerWizard.service.url"));
/*     */     
/*  88 */     Cursor handCursor = new Cursor(rootPanel.getDisplay(), 21);
/*     */     
/*  90 */     this.serviceUrl = new Label(rootPanel, 0);
/*  91 */     gridData = new GridData(768);
/*  92 */     Utils.setLayoutData(this.serviceUrl, gridData);
/*  93 */     this.serviceUrl.setForeground(Colors.blue);
/*  94 */     this.serviceUrl.setCursor(handCursor);
/*  95 */     this.serviceUrl.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/*  97 */         Utils.launch(((Label)arg0.widget).getText());
/*     */       }
/*     */       
/* 100 */       public void mouseDown(MouseEvent arg0) { Utils.launch(((Label)arg0.widget).getText());
/*     */       }
/*     */ 
/* 103 */     });
/* 104 */     this.servicesList.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 106 */         ChooseServicePanel.this.updateInfos();
/*     */       }
/* 108 */     });
/* 109 */     this.servicesList.select(0);
/*     */     
/* 111 */     label = new Label(rootPanel, 0);
/* 112 */     gridData = new GridData();
/* 113 */     gridData.heightHint = 50;
/* 114 */     gridData.verticalAlignment = 2;
/* 115 */     Utils.setLayoutData(label, gridData);
/* 116 */     label.setText(MessageText.getString("ipCheckerWizard.service.description"));
/*     */     
/* 118 */     this.serviceDescription = new Label(rootPanel, 64);
/* 119 */     gridData = new GridData(768);
/* 120 */     gridData.heightHint = 50;
/* 121 */     gridData.verticalAlignment = 2;
/* 122 */     Utils.setLayoutData(this.serviceDescription, gridData);
/*     */     
/* 124 */     updateInfos();
/*     */   }
/*     */   
/*     */   private void updateInfos()
/*     */   {
/* 129 */     int selection = this.servicesList.getSelectionIndex();
/* 130 */     this.serviceDescription.setText(this.services[selection].getDescription());
/* 131 */     this.serviceUrl.setText(this.services[selection].getURL());
/* 132 */     ((IpCheckerWizard)this.wizard).selectedService = this.services[selection];
/* 133 */     ((IpCheckerWizard)this.wizard).setFinishEnabled(this.services[selection].supportsCheck());
/*     */   }
/*     */   
/*     */   public boolean isFinishEnabled() {
/* 137 */     return true;
/*     */   }
/*     */   
/*     */   public IWizardPanel getFinishPanel() {
/* 141 */     return new ProgressPanel((IpCheckerWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ipchecker/ChooseServicePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */