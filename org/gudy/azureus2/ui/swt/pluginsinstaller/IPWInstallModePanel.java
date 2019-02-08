/*     */ package org.gudy.azureus2.ui.swt.pluginsinstaller;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
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
/*     */ public class IPWInstallModePanel
/*     */   extends AbstractWizardPanel<InstallPluginWizard>
/*     */ {
/*     */   private static final int MODE_USER = 0;
/*     */   private static final int MODE_SHARED = 1;
/*     */   
/*     */   public IPWInstallModePanel(InstallPluginWizard wizard, IWizardPanel<InstallPluginWizard> previous)
/*     */   {
/*  49 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  56 */     ((InstallPluginWizard)this.wizard).setTitle(MessageText.getString("installPluginsWizard.installMode.title"));
/*  57 */     ((InstallPluginWizard)this.wizard).setErrorMessage("");
/*     */     
/*  59 */     Composite rootPanel = ((InstallPluginWizard)this.wizard).getPanel();
/*  60 */     GridLayout layout = new GridLayout();
/*  61 */     layout.numColumns = 1;
/*  62 */     rootPanel.setLayout(layout);
/*     */     
/*  64 */     Composite panel = new Composite(rootPanel, 0);
/*  65 */     GridData gridData = new GridData(772);
/*  66 */     panel.setLayoutData(gridData);
/*  67 */     layout = new GridLayout();
/*  68 */     layout.numColumns = 1;
/*  69 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  73 */     ((InstallPluginWizard)this.wizard).shared = false;
/*     */     
/*  75 */     Button bSharedMode = new Button(panel, 16);
/*  76 */     Messages.setLanguageText(bSharedMode, "installPluginsWizard.installMode.shared");
/*  77 */     bSharedMode.setData("mode", new Integer(1));
/*  78 */     bSharedMode.setSelection(((InstallPluginWizard)this.wizard).shared);
/*  79 */     GridData data = new GridData(1040);
/*  80 */     data.verticalAlignment = 8;
/*  81 */     bSharedMode.setLayoutData(data);
/*     */     
/*     */ 
/*  84 */     Button bUserMode = new Button(panel, 16);
/*  85 */     Messages.setLanguageText(bUserMode, "installPluginsWizard.installMode.user");
/*  86 */     bUserMode.setData("mode", new Integer(0));
/*  87 */     bUserMode.setSelection(!((InstallPluginWizard)this.wizard).shared);
/*  88 */     data = new GridData(1040);
/*  89 */     data.verticalAlignment = 2;
/*  90 */     bUserMode.setLayoutData(data);
/*     */     
/*     */ 
/*  93 */     Listener modeListener = new Listener() {
/*     */       public void handleEvent(Event e) {
/*  95 */         ((InstallPluginWizard)IPWInstallModePanel.this.wizard).shared = (((Integer)e.widget.getData("mode")).intValue() == 1);
/*     */       }
/*     */       
/*  98 */     };
/*  99 */     bUserMode.addListener(13, modeListener);
/* 100 */     bSharedMode.addListener(13, modeListener);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishEnabled()
/*     */   {
/* 106 */     return true;
/*     */   }
/*     */   
/*     */   public IWizardPanel<InstallPluginWizard> getFinishPanel() {
/* 110 */     return new IPWFinishPanel((InstallPluginWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsinstaller/IPWInstallModePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */