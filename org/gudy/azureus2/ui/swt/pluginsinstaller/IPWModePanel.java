/*     */ package org.gudy.azureus2.ui.swt.pluginsinstaller;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class IPWModePanel
/*     */   extends AbstractWizardPanel<InstallPluginWizard>
/*     */ {
/*     */   private static final int MODE_FROM_LIST = 0;
/*     */   private static final int MODE_FROM_FILE = 1;
/*  49 */   private int mode = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPWModePanel(InstallPluginWizard wizard, IWizardPanel<InstallPluginWizard> previous)
/*     */   {
/*  56 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  64 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  66 */     ((InstallPluginWizard)this.wizard).setTitle(MessageText.getString("installPluginsWizard.mode.title"));
/*  67 */     ((InstallPluginWizard)this.wizard).setErrorMessage("");
/*     */     
/*  69 */     Composite rootPanel = ((InstallPluginWizard)this.wizard).getPanel();
/*  70 */     GridLayout layout = new GridLayout();
/*  71 */     layout.numColumns = 1;
/*  72 */     rootPanel.setLayout(layout);
/*     */     
/*  74 */     Composite panel = new Composite(rootPanel, 0);
/*  75 */     GridData gridData = new GridData(1812);
/*  76 */     Utils.setLayoutData(panel, gridData);
/*  77 */     layout = new GridLayout();
/*  78 */     layout.numColumns = 1;
/*  79 */     panel.setLayout(layout);
/*     */     
/*  81 */     Button bListMode = new Button(panel, 16);
/*  82 */     Messages.setLanguageText(bListMode, "installPluginsWizard.mode.list");
/*  83 */     bListMode.setData("mode", new Integer(0));
/*  84 */     if (((InstallPluginWizard)this.wizard).mode == 0) { bListMode.setSelection(true);
/*     */     }
/*  86 */     Button bFileMode = new Button(panel, 16);
/*  87 */     Messages.setLanguageText(bFileMode, "installPluginsWizard.mode.file");
/*  88 */     bFileMode.setData("mode", new Integer(1));
/*  89 */     if (((InstallPluginWizard)this.wizard).mode == 1) { bFileMode.setSelection(true);
/*     */     }
/*  91 */     Listener modeListener = new Listener() {
/*     */       public void handleEvent(Event e) {
/*  93 */         IPWModePanel.this.mode = ((Integer)e.widget.getData("mode")).intValue();
/*  94 */         ((InstallPluginWizard)IPWModePanel.this.wizard).mode = IPWModePanel.this.mode;
/*     */       }
/*     */       
/*  97 */     };
/*  98 */     bListMode.addListener(13, modeListener);
/*  99 */     bFileMode.addListener(13, modeListener);
/*     */     
/* 101 */     if (userMode < 2) {
/* 102 */       Group cWiki = new Group(panel, 64);
/* 103 */       cWiki.setText(MessageText.getString("installPluginsWizard.installMode.info.title"));
/* 104 */       gridData = new GridData(772);
/* 105 */       gridData.verticalIndent = 15;
/* 106 */       Utils.setLayoutData(cWiki, gridData);
/* 107 */       layout = new GridLayout();
/* 108 */       layout.numColumns = 4;
/* 109 */       layout.marginHeight = 0;
/* 110 */       cWiki.setLayout(layout);
/*     */       
/* 112 */       gridData = new GridData(772);
/* 113 */       gridData.horizontalIndent = 10;
/* 114 */       gridData.horizontalSpan = 4;
/* 115 */       Label label = new Label(cWiki, 64);
/* 116 */       Utils.setLayoutData(label, gridData);
/* 117 */       label.setText(MessageText.getString("installPluginsWizard.installMode.info.text"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public IWizardPanel<InstallPluginWizard> getNextPanel()
/*     */   {
/* 124 */     switch (this.mode) {
/*     */     case 0: 
/* 126 */       return new IPWListPanel((InstallPluginWizard)this.wizard, this);
/*     */     
/*     */     case 1: 
/* 129 */       return new IPWFilePanel((InstallPluginWizard)this.wizard, this);
/*     */     }
/*     */     
/* 132 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 138 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsinstaller/IPWModePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */