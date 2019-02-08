/*    */ package org.gudy.azureus2.update;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.UnloadablePlugin;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdaterUpdateChecker
/*    */   implements UnloadablePlugin
/*    */ {
/*    */   public static String getPluginID()
/*    */   {
/* 41 */     return UpdaterUtils.AZUPDATER_PLUGIN_ID;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void initialize(PluginInterface pi)
/*    */   {
/* 48 */     Properties props = pi.getPluginProperties();
/*    */     
/* 50 */     props.setProperty("plugin.mandatory", "true");
/*    */     
/* 52 */     if (pi.getPluginVersion() == null)
/*    */     {
/* 54 */       props.setProperty("plugin.version", "1.0");
/*    */     }
/*    */     
/* 57 */     props.setProperty("plugin.id", "azupdater");
/*    */   }
/*    */   
/*    */   public void unload() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/update/UpdaterUpdateChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */