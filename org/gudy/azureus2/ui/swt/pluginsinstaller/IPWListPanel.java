/*     */ package org.gudy.azureus2.ui.swt.pluginsinstaller;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.html.HTMLUtils;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkArea;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread;
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
/*     */ public class IPWListPanel
/*     */   extends AbstractWizardPanel<InstallPluginWizard>
/*     */ {
/*     */   Table pluginList;
/*     */   LinkArea link_area;
/*     */   
/*     */   public IPWListPanel(InstallPluginWizard wizard, IWizardPanel<InstallPluginWizard> previous)
/*     */   {
/*  65 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  72 */     ((InstallPluginWizard)this.wizard).setTitle(MessageText.getString("installPluginsWizard.list.title"));
/*  73 */     ((InstallPluginWizard)this.wizard).setErrorMessage("");
/*     */     
/*  75 */     Composite rootPanel = ((InstallPluginWizard)this.wizard).getPanel();
/*  76 */     GridLayout layout = new GridLayout();
/*  77 */     layout.numColumns = 1;
/*  78 */     rootPanel.setLayout(layout);
/*     */     
/*  80 */     Composite panel = new Composite(rootPanel, 0);
/*  81 */     GridData gridData = new GridData(772);
/*  82 */     Utils.setLayoutData(panel, gridData);
/*  83 */     layout = new GridLayout();
/*  84 */     layout.numColumns = 1;
/*  85 */     panel.setLayout(layout);
/*     */     
/*  87 */     final Label lblStatus = new Label(panel, 0);
/*  88 */     GridData data = new GridData(768);
/*  89 */     Utils.setLayoutData(lblStatus, data);
/*  90 */     Messages.setLanguageText(lblStatus, "installPluginsWizard.list.loading");
/*     */     
/*  92 */     this.pluginList = new Table(panel, 68132);
/*  93 */     this.pluginList.setHeaderVisible(true);
/*  94 */     data = new GridData(768);
/*  95 */     data.heightHint = 120;
/*  96 */     Utils.setLayoutData(this.pluginList, data);
/*     */     
/*     */ 
/*  99 */     TableColumn tcName = new TableColumn(this.pluginList, 16384);
/* 100 */     Messages.setLanguageText(tcName, "installPluginsWizard.list.name");
/* 101 */     tcName.setWidth(Utils.adjustPXForDPI(200));
/*     */     
/* 103 */     TableColumn tcVersion = new TableColumn(this.pluginList, 16384);
/* 104 */     Messages.setLanguageText(tcVersion, "installPluginsWizard.list.version");
/* 105 */     tcVersion.setWidth(Utils.adjustPXForDPI(150));
/*     */     
/*     */ 
/* 108 */     Label lblDescription = new Label(panel, 0);
/* 109 */     Messages.setLanguageText(lblDescription, "installPluginsWizard.list.description");
/*     */     
/* 111 */     this.link_area = new LinkArea(panel);
/*     */     
/* 113 */     data = new GridData(768);
/* 114 */     data.heightHint = 100;
/* 115 */     this.link_area.getComponent().setLayoutData(data);
/*     */     
/* 117 */     CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.NEW_THREAD, new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*     */         final StandardPlugin[] plugins;
/*     */         try {
/* 121 */           plugins = ((InstallPluginWizard)IPWListPanel.this.wizard).getStandardPlugins(core);
/*     */           
/* 123 */           Arrays.sort(plugins, new Comparator()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public int compare(StandardPlugin o1, StandardPlugin o2)
/*     */             {
/*     */ 
/*     */ 
/* 132 */               return o1.getName().compareToIgnoreCase(o2.getName());
/*     */             }
/*     */           });
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 138 */           Debug.printStackTrace(e);
/* 139 */           ((InstallPluginWizard)IPWListPanel.this.wizard).getDisplay().asyncExec(new AERunnable() {
/*     */             public void runSupport() {
/* 141 */               IPWListPanel.this.link_area.addLine(Debug.getNestedExceptionMessage(e));
/*     */             }
/*     */             
/* 144 */           });
/* 145 */           return;
/*     */         }
/*     */         
/* 148 */         ((InstallPluginWizard)IPWListPanel.this.wizard).getDisplay().asyncExec(new AERunnable()
/*     */         {
/*     */           public void runSupport() {
/* 151 */             IPWListPanel.1.this.val$lblStatus.setText(((InstallPluginWizard)IPWListPanel.this.wizard).getListTitleText());
/*     */             
/* 153 */             List<InstallablePlugin> selected_plugins = ((InstallPluginWizard)IPWListPanel.this.wizard).getPluginList();
/*     */             
/* 155 */             for (int i = 0; i < plugins.length; i++) {
/* 156 */               StandardPlugin plugin = plugins[i];
/* 157 */               if (plugin.getAlreadyInstalledPlugin() == null) {
/* 158 */                 if ((IPWListPanel.this.pluginList == null) || (IPWListPanel.this.pluginList.isDisposed()))
/* 159 */                   return;
/* 160 */                 TableItem item = new TableItem(IPWListPanel.this.pluginList, 0);
/* 161 */                 item.setData(plugin);
/* 162 */                 item.setText(0, plugin.getName());
/* 163 */                 boolean selected = false;
/* 164 */                 for (int j = 0; j < selected_plugins.size(); j++) {
/* 165 */                   if (((InstallablePlugin)selected_plugins.get(j)).getId() == plugin.getId()) {
/* 166 */                     selected = true;
/*     */                   }
/*     */                 }
/* 169 */                 item.setChecked(selected);
/* 170 */                 item.setText(1, plugin.getVersion());
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 178 */             if ((plugins.length == 1) && (IPWListPanel.this.pluginList.getItemCount() > 0))
/*     */             {
/* 180 */               IPWListPanel.this.pluginList.select(0);
/*     */               
/* 182 */               IPWListPanel.this.loadPluginDetails(IPWListPanel.this.pluginList.getItem(0));
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 189 */     });
/* 190 */     this.pluginList.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 192 */         if (IPWListPanel.this.pluginList.getSelectionCount() > 0) {
/* 193 */           IPWListPanel.this.loadPluginDetails(IPWListPanel.this.pluginList.getSelection()[0]);
/*     */         }
/*     */         
/* 196 */         IPWListPanel.this.updateList();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadPluginDetails(final TableItem selected_item)
/*     */   {
/* 205 */     this.link_area.reset();
/*     */     
/* 207 */     this.link_area.addLine(MessageText.getString("installPluginsWizard.details.loading"));
/*     */     
/* 209 */     final StandardPlugin plugin = (StandardPlugin)selected_item.getData();
/*     */     
/*     */ 
/* 212 */     AEThread2 detailsLoader = new AEThread2("Detail Loader") {
/*     */       public void run() {
/* 214 */         final String description = HTMLUtils.convertListToString(HTMLUtils.convertHTMLToText(plugin.getDescription(), ""));
/* 215 */         ((InstallPluginWizard)IPWListPanel.this.wizard).getDisplay().asyncExec(new AERunnable() {
/*     */           public void runSupport() {
/* 217 */             if ((IPWListPanel.this.pluginList == null) || (IPWListPanel.this.pluginList.isDisposed()) || (IPWListPanel.this.pluginList.getSelectionCount() == 0))
/* 218 */               return;
/* 219 */             if (IPWListPanel.this.pluginList.getSelection()[0] != IPWListPanel.3.this.val$selected_item) {
/* 220 */               return;
/*     */             }
/* 222 */             IPWListPanel.this.link_area.reset();
/*     */             
/* 224 */             IPWListPanel.this.link_area.setRelativeURLBase(IPWListPanel.3.this.val$plugin.getRelativeURLBase());
/*     */             
/* 226 */             IPWListPanel.this.link_area.addLine(description);
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 231 */     };
/* 232 */     detailsLoader.start();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 238 */     return ((InstallPluginWizard)this.wizard).getPluginList().size() > 0;
/*     */   }
/*     */   
/*     */   public IWizardPanel<InstallPluginWizard> getNextPanel() {
/* 242 */     return new IPWInstallModePanel((InstallPluginWizard)this.wizard, this);
/*     */   }
/*     */   
/*     */   public void updateList() {
/* 246 */     ArrayList<InstallablePlugin> list = new ArrayList();
/* 247 */     TableItem[] items = this.pluginList.getItems();
/* 248 */     for (int i = 0; i < items.length; i++) {
/* 249 */       if (items[i].getChecked()) {
/* 250 */         list.add((InstallablePlugin)items[i].getData());
/*     */       }
/*     */     }
/* 253 */     ((InstallPluginWizard)this.wizard).setPluginList(list);
/* 254 */     ((InstallPluginWizard)this.wizard).setNextEnabled(isNextEnabled());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsinstaller/IPWListPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */