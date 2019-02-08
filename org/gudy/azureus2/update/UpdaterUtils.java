/*     */ package org.gudy.azureus2.update;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintWriter;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class UpdaterUtils
/*     */ {
/*  39 */   public static String AZUPDATER_PLUGIN_ID = "azupdater";
/*  40 */   public static String AZUPDATERPATCHER_PLUGIN_ID = "azupdaterpatcher";
/*     */   
/*  42 */   protected static String AZUPNPAV_PLUGIN_ID = "azupnpav";
/*  43 */   protected static String AEFEATMAN_PLUGIN_ID = "aefeatman_v";
/*     */   
/*     */ 
/*     */   public static boolean disableNativeCode(String version)
/*     */   {
/*     */     try
/*     */     {
/*  50 */       File plugin_dir = null;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  55 */       File shared_plugin_dir = FileUtil.getApplicationFile("plugins");
/*     */       
/*  57 */       File shared_updater_plugin = new File(shared_plugin_dir, AZUPDATER_PLUGIN_ID);
/*     */       
/*  59 */       if (shared_updater_plugin.exists())
/*     */       {
/*  61 */         plugin_dir = shared_updater_plugin;
/*     */       }
/*     */       
/*  64 */       if (plugin_dir == null)
/*     */       {
/*  66 */         return false;
/*     */       }
/*     */       
/*  69 */       return new File(plugin_dir, "disnat" + version).exists();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  73 */       e.printStackTrace();
/*     */     }
/*     */     
/*  76 */     return false;
/*     */   }
/*     */   
/*     */   public static void checkBootstrapPlugins()
/*     */   {
/*     */     try
/*     */     {
/*  83 */       File target_props = getPropsIfNotPresent(AZUPDATER_PLUGIN_ID, true);
/*     */       
/*  85 */       if (target_props != null)
/*     */       {
/*  87 */         writePluginProperties(target_props, new String[] { "plugin.class=org.gudy.azureus2.update.UpdaterUpdateChecker;org.gudy.azureus2.update.UpdaterPatcher", "plugin.name=Azureus Update Support;Azureus Updater Support Patcher" });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */       target_props = getPropsIfNotPresent(AZUPNPAV_PLUGIN_ID, false);
/*     */       
/*  99 */       if (target_props != null)
/*     */       {
/* 101 */         writePluginProperties(target_props, new String[] { "plugin.class=com.aelitis.azureus.plugins.upnpmediaserver.UPnPMediaServer", "plugin.name=UPnP Media Server", "plugin.id=azupnpav" });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 111 */       target_props = getPropsIfNotPresent(AEFEATMAN_PLUGIN_ID, false);
/*     */       
/* 113 */       if (target_props != null)
/*     */       {
/* 115 */         writePluginProperties(target_props, new String[] { "plugin.class=com.aelitis.azureus.plugins.featman.FeatManPlugin", "plugin.name=Vuze Feature Manager", "plugin.id=aefeatman_v" });
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 125 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean ensurePluginPresent(String id, String cla, String name)
/*     */   {
/* 135 */     File target_props = getPropsIfNotPresent(id, false);
/*     */     
/* 137 */     if (target_props != null)
/*     */     {
/* 139 */       writePluginProperties(target_props, new String[] { "plugin.class=" + cla, "plugin.name=" + name, "plugin.id=" + id });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 146 */       return true;
/*     */     }
/*     */     
/* 149 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static void writePluginProperties(File target, String[] lines)
/*     */   {
/*     */     try
/*     */     {
/* 158 */       PrintWriter pw = null;
/*     */       try
/*     */       {
/* 161 */         target.getParentFile().mkdirs();
/*     */         
/* 163 */         pw = new PrintWriter(new FileWriter(target));
/*     */         
/* 165 */         for (int i = 0; i < lines.length; i++)
/*     */         {
/* 167 */           pw.println(lines[i]);
/*     */         }
/*     */         
/* 170 */         pw.println("plugin.install_if_missing=yes");
/*     */       }
/*     */       finally
/*     */       {
/* 174 */         if (pw != null)
/*     */         {
/* 176 */           pw.close();
/*     */         }
/*     */       }
/*     */       
/* 180 */       if (!target.exists())
/*     */       {
/* 182 */         throw new Exception("Failed to write '" + target.toString() + "'");
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 186 */       Logger.log(new LogAlert(false, "Plugin bootstrap: initialisation error for " + target, e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static File getPropsIfNotPresent(String id, boolean use_shared)
/*     */   {
/* 198 */     File user_plugin_dir = FileUtil.getUserFile("plugins");
/*     */     
/* 200 */     File user_plugin = new File(user_plugin_dir, id);
/*     */     
/* 202 */     File user_props = new File(user_plugin, "plugin.properties");
/*     */     
/* 204 */     if (user_props.exists())
/*     */     {
/* 206 */       return null;
/*     */     }
/*     */     
/* 209 */     File shared_plugin_dir = FileUtil.getApplicationFile("plugins");
/*     */     
/* 211 */     File shared_plugin = new File(shared_plugin_dir, id);
/*     */     
/* 213 */     File shared_props = new File(shared_plugin, "plugin.properties");
/*     */     
/* 215 */     if (shared_props.exists())
/*     */     {
/* 217 */       return null;
/*     */     }
/*     */     
/* 220 */     if (use_shared)
/*     */     {
/* 222 */       FileUtil.mkdirs(shared_plugin);
/*     */       
/* 224 */       return shared_props;
/*     */     }
/*     */     
/*     */ 
/* 228 */     FileUtil.mkdirs(user_plugin);
/*     */     
/* 230 */     return user_props;
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getUpdaterPluginVersion()
/*     */   {
/*     */     try
/*     */     {
/* 238 */       PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID(AZUPDATER_PLUGIN_ID, false);
/* 239 */       if (pi != null) {
/* 240 */         String version = pi.getPluginVersion();
/* 241 */         if (version != null) {
/* 242 */           return version;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {}
/* 247 */     return "0";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/update/UpdaterUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */