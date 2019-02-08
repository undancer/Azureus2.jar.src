/*    */ package org.gudy.azureus2.ui.swt.pluginsuninstaller;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.eclipse.swt.widgets.Display;
/*    */ import org.gudy.azureus2.core3.logging.LogAlert;
/*    */ import org.gudy.azureus2.core3.logging.Logger;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.PluginManager;
/*    */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*    */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UnInstallPluginWizard
/*    */   extends Wizard
/*    */ {
/* 39 */   List plugins = new ArrayList();
/*    */   
/*    */ 
/*    */   public UnInstallPluginWizard(Display display)
/*    */   {
/* 44 */     super("uninstallPluginsWizard.title");
/*    */     
/* 46 */     UIPWListPanel list_panel = new UIPWListPanel(this, null);
/*    */     
/* 48 */     setFirstPanel(list_panel);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void onClose()
/*    */   {
/* 55 */     super.onClose();
/*    */   }
/*    */   
/*    */   public void setPluginList(List _plugins) {
/* 59 */     this.plugins = _plugins;
/*    */   }
/*    */   
/*    */ 
/*    */   public List getPluginList()
/*    */   {
/* 65 */     return this.plugins;
/*    */   }
/*    */   
/*    */   public void performUnInstall()
/*    */   {
/* 70 */     PluginInterface[] ps = new PluginInterface[this.plugins.size()];
/*    */     
/* 72 */     this.plugins.toArray(ps);
/*    */     
/* 74 */     if (ps.length > 0)
/*    */     {
/*    */       try
/*    */       {
/* 78 */         ps[0].getPluginManager().getPluginInstaller().uninstall(ps);
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 82 */         Debug.printStackTrace(e);
/*    */         
/* 84 */         Logger.log(new LogAlert(true, "Failed to initialise installer", e));
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsuninstaller/UnInstallPluginWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */