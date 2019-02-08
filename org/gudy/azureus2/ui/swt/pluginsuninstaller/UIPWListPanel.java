/*     */ package org.gudy.azureus2.ui.swt.pluginsuninstaller;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UIPWListPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   Table pluginList;
/*     */   
/*     */   public UIPWListPanel(Wizard wizard, IWizardPanel previous)
/*     */   {
/*  67 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  74 */     CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  76 */         UIPWListPanel.this._show(core);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void _show(AzureusCore core) {
/*  82 */     this.wizard.setTitle(MessageText.getString("uninstallPluginsWizard.list.title"));
/*  83 */     this.wizard.setErrorMessage("");
/*     */     
/*  85 */     Composite rootPanel = this.wizard.getPanel();
/*  86 */     GridLayout layout = new GridLayout();
/*  87 */     layout.numColumns = 1;
/*  88 */     rootPanel.setLayout(layout);
/*     */     
/*  90 */     Composite panel = new Composite(rootPanel, 0);
/*  91 */     GridData gridData = new GridData(772);
/*  92 */     Utils.setLayoutData(panel, gridData);
/*  93 */     layout = new GridLayout();
/*  94 */     layout.numColumns = 1;
/*  95 */     panel.setLayout(layout);
/*     */     
/*  97 */     Label lblStatus = new Label(panel, 0);
/*  98 */     Messages.setLanguageText(lblStatus, "uninstallPluginsWizard.list.loaded");
/*     */     
/* 100 */     this.pluginList = new Table(panel, 68132);
/* 101 */     this.pluginList.setHeaderVisible(true);
/* 102 */     GridData data = new GridData(768);
/* 103 */     data.heightHint = 200;
/* 104 */     Utils.setLayoutData(this.pluginList, data);
/*     */     
/*     */ 
/* 107 */     TableColumn tcName = new TableColumn(this.pluginList, 16384);
/* 108 */     Messages.setLanguageText(tcName, "installPluginsWizard.list.name");
/* 109 */     tcName.setWidth(Utils.adjustPXForDPI(200));
/*     */     
/* 111 */     TableColumn tcVersion = new TableColumn(this.pluginList, 16384);
/* 112 */     Messages.setLanguageText(tcVersion, "installPluginsWizard.list.version");
/* 113 */     tcVersion.setWidth(Utils.adjustPXForDPI(150));
/*     */     
/* 115 */     PluginInterface[] plugins = new PluginInterface[0];
/*     */     try {
/* 117 */       plugins = core.getPluginManager().getPluginInterfaces();
/*     */       
/* 119 */       Arrays.sort(plugins, new Comparator()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public int compare(Object o1, Object o2)
/*     */         {
/*     */ 
/*     */ 
/* 128 */           return ((PluginInterface)o1).getPluginName().compareTo(((PluginInterface)o2).getPluginName());
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Exception e) {
/* 133 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 138 */     Map pid_map = new HashMap();
/*     */     
/* 140 */     for (int i = 0; i < plugins.length; i++)
/*     */     {
/* 142 */       PluginInterface plugin = plugins[i];
/*     */       
/* 144 */       String pid = plugin.getPluginID();
/*     */       
/* 146 */       ArrayList pis = (ArrayList)pid_map.get(pid);
/*     */       
/* 148 */       if (pis == null)
/*     */       {
/* 150 */         pis = new ArrayList();
/*     */         
/* 152 */         pid_map.put(pid, pis);
/*     */       }
/*     */       
/* 155 */       pis.add(plugin);
/*     */     }
/*     */     
/* 158 */     ArrayList[] pid_list = new ArrayList[pid_map.size()];
/*     */     
/* 160 */     pid_map.values().toArray(pid_list);
/*     */     
/* 162 */     Arrays.sort(pid_list, new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/*     */ 
/*     */ 
/* 171 */         ArrayList l1 = (ArrayList)o1;
/* 172 */         ArrayList l2 = (ArrayList)o2;
/* 173 */         return ((PluginInterface)l1.get(0)).getPluginName().compareToIgnoreCase(((PluginInterface)l2.get(0)).getPluginName());
/*     */       }
/*     */     });
/*     */     
/* 177 */     for (int i = 0; i < pid_list.length; i++)
/*     */     {
/* 179 */       ArrayList pis = pid_list[i];
/*     */       
/* 181 */       boolean skip = false;
/*     */       
/* 183 */       String display_name = "";
/*     */       
/* 185 */       for (int j = 0; j < pis.size(); j++)
/*     */       {
/* 187 */         PluginInterface pi = (PluginInterface)pis.get(j);
/*     */         
/* 189 */         if ((pi.getPluginState().isMandatory()) || (pi.getPluginState().isBuiltIn()))
/*     */         {
/* 191 */           skip = true;
/*     */           
/* 193 */           break;
/*     */         }
/*     */         
/* 196 */         display_name = display_name + (j == 0 ? "" : ",") + pi.getPluginName();
/*     */       }
/*     */       
/* 199 */       if (!skip)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 204 */         PluginInterface plugin = (PluginInterface)pis.get(0);
/*     */         
/* 206 */         List selected_plugins = ((UnInstallPluginWizard)this.wizard).getPluginList();
/*     */         
/* 208 */         TableItem item = new TableItem(this.pluginList, 0);
/* 209 */         item.setData(plugin);
/* 210 */         item.setText(0, display_name);
/* 211 */         item.setChecked(selected_plugins.contains(plugin));
/* 212 */         String version = plugin.getPluginVersion();
/* 213 */         if (version == null) version = MessageText.getString("installPluginsWizard.list.nullversion");
/* 214 */         item.setText(1, version);
/*     */       }
/*     */     }
/* 217 */     this.pluginList.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 219 */         UIPWListPanel.this.updateList();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishEnabled()
/*     */   {
/* 227 */     return ((UnInstallPluginWizard)this.wizard).getPluginList().size() > 0;
/*     */   }
/*     */   
/*     */   public IWizardPanel getFinishPanel() {
/* 231 */     return new UIPWFinishPanel(this.wizard, this);
/*     */   }
/*     */   
/*     */   public void updateList() {
/* 235 */     ArrayList list = new ArrayList();
/* 236 */     TableItem[] items = this.pluginList.getItems();
/* 237 */     for (int i = 0; i < items.length; i++) {
/* 238 */       if (items[i].getChecked())
/* 239 */         list.add(items[i].getData());
/*     */     }
/* 241 */     ((UnInstallPluginWizard)this.wizard).setPluginList(list);
/* 242 */     ((UnInstallPluginWizard)this.wizard).setFinishEnabled(isFinishEnabled());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsuninstaller/UIPWListPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */