/*    */ package org.gudy.azureus2.platform;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import org.gudy.azureus2.platform.unix.PlatformManagerUnixPlugin;
/*    */ import org.gudy.azureus2.plugins.Plugin;
/*    */ import org.gudy.azureus2.plugins.PluginException;
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*    */ import org.gudy.azureus2.plugins.update.UpdateChecker;
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
/*    */ public class PlatformManagerPluginDelegate
/*    */   implements Plugin, UpdatableComponent
/*    */ {
/*    */   public static void load(PluginInterface plugin_interface)
/*    */   {
/* 45 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Platform-Specific Support");
/*    */   }
/*    */   
/*    */   public void initialize(PluginInterface pluginInterface)
/*    */     throws PluginException
/*    */   {
/* 51 */     PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*    */     
/* 53 */     int platformType = platform.getPlatformType();
/* 54 */     if (platformType == 1) {
/* 55 */       org.gudy.azureus2.platform.win32.PlatformManagerUpdateChecker plugin = new org.gudy.azureus2.platform.win32.PlatformManagerUpdateChecker();
/* 56 */       plugin.initialize(pluginInterface);
/* 57 */     } else if (platformType == 3) {
/* 58 */       org.gudy.azureus2.platform.macosx.PlatformManagerUpdateChecker plugin = new org.gudy.azureus2.platform.macosx.PlatformManagerUpdateChecker();
/* 59 */       plugin.initialize(pluginInterface);
/* 60 */     } else if (platformType == 4) {
/* 61 */       PlatformManagerUnixPlugin plugin = new PlatformManagerUnixPlugin();
/* 62 */       plugin.initialize(pluginInterface);
/*    */     } else {
/* 64 */       Properties pluginProperties = pluginInterface.getPluginProperties();
/* 65 */       pluginProperties.setProperty("plugin.name", "Platform-Specific Support");
/* 66 */       pluginProperties.setProperty("plugin.version", "1.0");
/* 67 */       pluginProperties.setProperty("plugin.version.info", "Not required for this platform");
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getName()
/*    */   {
/* 75 */     return "Mixin only";
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public int getMaximumCheckTime()
/*    */   {
/* 82 */     return 0;
/*    */   }
/*    */   
/*    */   public void checkForUpdate(UpdateChecker checker) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/PlatformManagerPluginDelegate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */