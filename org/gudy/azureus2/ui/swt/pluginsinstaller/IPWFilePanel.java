/*     */ package org.gudy.azureus2.ui.swt.pluginsinstaller;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ListenerNeedingCoreRunning;
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
/*     */ public class IPWFilePanel
/*     */   extends AbstractWizardPanel<InstallPluginWizard>
/*     */ {
/*     */   Text txtFile;
/*  49 */   boolean valid = false;
/*     */   
/*     */ 
/*     */ 
/*     */   public IPWFilePanel(InstallPluginWizard wizard, IWizardPanel<InstallPluginWizard> previous)
/*     */   {
/*  55 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */   public void show() {
/*  59 */     ((InstallPluginWizard)this.wizard).setTitle(MessageText.getString("installPluginsWizard.file.title"));
/*  60 */     ((InstallPluginWizard)this.wizard).setErrorMessage("");
/*     */     
/*  62 */     Composite rootPanel = ((InstallPluginWizard)this.wizard).getPanel();
/*  63 */     GridLayout layout = new GridLayout();
/*  64 */     layout.numColumns = 1;
/*  65 */     rootPanel.setLayout(layout);
/*     */     
/*  67 */     Composite panel = new Composite(rootPanel, 0);
/*  68 */     GridData gridData = new GridData(772);
/*  69 */     panel.setLayoutData(gridData);
/*  70 */     layout = new GridLayout();
/*  71 */     layout.numColumns = 3;
/*  72 */     panel.setLayout(layout);
/*     */     
/*  74 */     Label label = new Label(panel, 0);
/*  75 */     Messages.setLanguageText(label, "installPluginsWizard.file.file");
/*     */     
/*  77 */     this.txtFile = new Text(panel, 2048);
/*  78 */     GridData data = new GridData(768);
/*  79 */     this.txtFile.setLayoutData(data);
/*  80 */     this.txtFile.addListener(24, new ListenerNeedingCoreRunning() {
/*     */       public void handleEvent(AzureusCore core, Event event) {
/*  82 */         IPWFilePanel.this.checkValidFile(core);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  87 */     });
/*  88 */     Button btnBrowse = new Button(panel, 8);
/*  89 */     Messages.setLanguageText(btnBrowse, "installPluginsWizard.file.browse");
/*  90 */     btnBrowse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  92 */         FileDialog fd = new FileDialog(((InstallPluginWizard)IPWFilePanel.this.wizard).getWizardWindow());
/*  93 */         fd.setFilterExtensions(new String[] { "*.zip;*.jar;*.vuze" });
/*  94 */         fd.setFilterNames(new String[] { "Azureus Plugins" });
/*  95 */         String fileName = fd.open();
/*  96 */         if (fileName != null) IPWFilePanel.this.txtFile.setText(fileName);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void checkValidFile(AzureusCore core)
/*     */   {
/* 103 */     String fileName = this.txtFile.getText();
/* 104 */     String error_message = null;
/*     */     try {
/* 106 */       File f = new File(fileName);
/* 107 */       if ((f.isFile()) && ((f.getName().endsWith(".jar")) || (f.getName().endsWith(".zip")) || (f.getName().endsWith(".vuze")))) {
/* 108 */         ((InstallPluginWizard)this.wizard).setErrorMessage("");
/* 109 */         ((InstallPluginWizard)this.wizard).setNextEnabled(true);
/* 110 */         List<InstallablePlugin> list = new ArrayList();
/* 111 */         InstallablePlugin plugin = core.getPluginManager().getPluginInstaller().installFromFile(f);
/* 112 */         list.add(plugin);
/* 113 */         ((InstallPluginWizard)this.wizard).plugins = list;
/* 114 */         this.valid = true;
/* 115 */         return;
/*     */       }
/*     */     } catch (PluginException e) {
/* 118 */       error_message = e.getMessage();
/* 119 */       Debug.printStackTrace(e);
/*     */     } catch (Exception e) {
/* 121 */       error_message = null;
/* 122 */       Debug.printStackTrace(e);
/*     */     }
/* 124 */     this.valid = false;
/* 125 */     if (!fileName.equals("")) { String error_message_full;
/*     */       String error_message_full;
/* 127 */       if (new File(fileName).isFile()) {
/* 128 */         error_message_full = MessageText.getString("installPluginsWizard.file.invalidfile");
/*     */       } else {
/* 130 */         error_message_full = MessageText.getString("installPluginsWizard.file.no_such_file");
/*     */       }
/* 132 */       if (error_message != null) {
/* 133 */         error_message_full = error_message_full + " (" + error_message + ")";
/*     */       }
/* 135 */       ((InstallPluginWizard)this.wizard).setErrorMessage(error_message_full);
/* 136 */       ((InstallPluginWizard)this.wizard).setNextEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 143 */     return this.valid;
/*     */   }
/*     */   
/*     */   public IWizardPanel<InstallPluginWizard> getNextPanel() {
/* 147 */     return new IPWInstallModePanel((InstallPluginWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsinstaller/IPWFilePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */