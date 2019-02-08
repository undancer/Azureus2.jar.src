/*     */ package org.gudy.azureus2.ui.swt.pluginsinstaller;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallerListener;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
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
/*     */ public class InstallPluginWizard
/*     */   extends Wizard
/*     */ {
/*     */   int mode;
/*     */   StandardPlugin[] standard_plugins;
/*  50 */   List<InstallablePlugin> plugins = new ArrayList();
/*  51 */   boolean shared = false;
/*     */   
/*     */ 
/*     */   String list_title_text;
/*     */   
/*     */ 
/*     */   public static void register(AzureusCore core, Display display)
/*     */   {
/*  59 */     core.getPluginManager().getPluginInstaller().addListener(new PluginInstallerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean installRequest(final String reason, final InstallablePlugin plugin)
/*     */         throws PluginException
/*     */       {
/*     */ 
/*     */ 
/*  69 */         if ((plugin instanceof StandardPlugin))
/*     */         {
/*  71 */           this.val$display.asyncExec(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/*  77 */               new InstallPluginWizard(reason, (StandardPlugin)plugin);
/*     */             }
/*     */             
/*  80 */           });
/*  81 */           return true;
/*     */         }
/*     */         
/*  84 */         return false;
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InstallPluginWizard()
/*     */   {
/*  93 */     super("installPluginsWizard.title");
/*     */     
/*  95 */     IPWModePanel mode_panel = new IPWModePanel(this, null);
/*     */     
/*  97 */     setFirstPanel(mode_panel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public InstallPluginWizard(String reason, StandardPlugin plugin)
/*     */   {
/* 105 */     super("installPluginsWizard.title");
/*     */     
/* 107 */     this.standard_plugins = new StandardPlugin[] { plugin };
/* 108 */     this.list_title_text = reason;
/*     */     
/* 110 */     this.plugins = new ArrayList();
/* 111 */     this.plugins.add(plugin);
/*     */     
/* 113 */     IPWListPanel list_panel = new IPWListPanel(this, null);
/*     */     
/* 115 */     setFirstPanel(list_panel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected StandardPlugin[] getStandardPlugins(AzureusCore core)
/*     */     throws PluginException
/*     */   {
/* 123 */     if (this.standard_plugins == null)
/*     */     {
/* 125 */       this.standard_plugins = core.getPluginManager().getPluginInstaller().getStandardPlugins();
/*     */     }
/*     */     
/* 128 */     return this.standard_plugins;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getListTitleText()
/*     */   {
/* 134 */     if (this.list_title_text == null)
/*     */     {
/* 136 */       this.list_title_text = MessageText.getString("installPluginsWizard.list.loaded");
/*     */     }
/*     */     
/* 139 */     return this.list_title_text;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onClose()
/*     */   {
/* 146 */     super.onClose();
/*     */   }
/*     */   
/*     */   public void setPluginList(List<InstallablePlugin> _plugins)
/*     */   {
/* 151 */     this.plugins = _plugins;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<InstallablePlugin> getPluginList()
/*     */   {
/* 157 */     return this.plugins;
/*     */   }
/*     */   
/*     */   public void performInstall()
/*     */   {
/* 162 */     InstallablePlugin[] ps = new InstallablePlugin[this.plugins.size()];
/*     */     
/* 164 */     this.plugins.toArray(ps);
/*     */     
/* 166 */     if (ps.length > 0)
/*     */     {
/*     */       try
/*     */       {
/* 170 */         ps[0].getInstaller().install(ps, this.shared);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 174 */         Debug.printStackTrace(e);
/*     */         
/* 176 */         Logger.log(new LogAlert(true, "Failed to initialise installer", e));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsinstaller/InstallPluginWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */