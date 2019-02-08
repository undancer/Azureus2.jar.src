/*     */ package org.gudy.azureus2.ui.swt.ipchecker;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerService;
/*     */ import org.gudy.azureus2.core3.ipchecker.extipchecker.ExternalIPCheckerServiceListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
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
/*     */ public class ProgressPanel
/*     */   extends AbstractWizardPanel
/*     */   implements ExternalIPCheckerServiceListener
/*     */ {
/*     */   Text tasks;
/*     */   Display display;
/*     */   
/*     */   public ProgressPanel(IpCheckerWizard wizard, IWizardPanel previousPanel)
/*     */   {
/*  48 */     super(wizard, previousPanel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  54 */     this.display = this.wizard.getDisplay();
/*  55 */     this.wizard.setTitle(MessageText.getString("ipCheckerWizard.progresstitle"));
/*  56 */     this.wizard.setCurrentInfo("");
/*  57 */     Composite rootPanel = this.wizard.getPanel();
/*  58 */     GridLayout layout = new GridLayout();
/*  59 */     layout.numColumns = 1;
/*  60 */     rootPanel.setLayout(layout);
/*     */     
/*  62 */     Composite panel = new Composite(rootPanel, 0);
/*  63 */     GridData gridData = new GridData(772);
/*  64 */     Utils.setLayoutData(panel, gridData);
/*  65 */     layout = new GridLayout();
/*  66 */     layout.numColumns = 1;
/*  67 */     panel.setLayout(layout);
/*     */     
/*  69 */     this.tasks = new Text(panel, 2058);
/*  70 */     this.tasks.setBackground(this.display.getSystemColor(1));
/*  71 */     gridData = new GridData(1808);
/*  72 */     gridData.heightHint = 120;
/*  73 */     Utils.setLayoutData(this.tasks, gridData);
/*     */   }
/*     */   
/*     */   public void finish() {
/*  77 */     ((IpCheckerWizard)this.wizard).selectedService.addListener(this);
/*  78 */     ((IpCheckerWizard)this.wizard).selectedService.initiateCheck(10000L);
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkComplete(ExternalIPCheckerService service, String ip)
/*     */   {
/*  84 */     reportProgress(service, MessageText.getString("ipCheckerWizard.checkComplete") + ip);
/*  85 */     IpSetterCallBack callBack = ((IpCheckerWizard)this.wizard).callBack;
/*  86 */     if (callBack != null) {
/*  87 */       callBack.setIp(ip);
/*     */     }
/*  89 */     this.wizard.switchToClose();
/*     */   }
/*     */   
/*     */   public void checkFailed(ExternalIPCheckerService service, String reason)
/*     */   {
/*  94 */     reportProgress(service, MessageText.getString("ipCheckerWizard.checkFailed") + reason);
/*  95 */     this.wizard.switchToClose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reportProgress(final ExternalIPCheckerService service, final String message)
/*     */   {
/* 102 */     if ((this.display == null) || (this.display.isDisposed()))
/* 103 */       return;
/* 104 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 106 */         if ((ProgressPanel.this.tasks != null) && (!ProgressPanel.this.tasks.isDisposed())) {
/* 107 */           ProgressPanel.this.tasks.append(service.getName() + " : " + message + Text.DELIMITER);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ipchecker/ProgressPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */