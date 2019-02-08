/*     */ package org.gudy.azureus2.plugins;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginManagerDefaultsImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginManagerImpl;
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
/*     */ public abstract class PluginManager
/*     */ {
/*     */   public static final int UI_NONE = 0;
/*     */   public static final int UI_SWT = 1;
/*     */   public static final String PR_MULTI_INSTANCE = "MULTI_INSTANCE";
/*     */   public static final String PR_USER_DIRECTORY = "USER_DIR";
/*     */   public static final String PR_APP_DIRECTORY = "APP_DIR";
/*     */   public static final String PR_DOC_DIRECTORY = "DOC_DIR";
/*     */   public static final String PR_DISABLE_NATIVE_SUPPORT = "DISABLE_NATIVE";
/*     */   public static final String CA_QUIT_VUZE = "QuitVuze";
/*     */   public static final String CA_SLEEP = "Sleep";
/*     */   public static final String CA_HIBERNATE = "Hibernate";
/*     */   public static final String CA_SHUTDOWN = "Shutdown";
/*     */   
/*     */   public static PluginManagerDefaults getDefaults()
/*     */   {
/* 101 */     return PluginManagerDefaultsImpl.getSingleton();
/*     */   }
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
/*     */   public static PluginManager startAzureus(int ui_type, Properties properties)
/*     */   {
/* 116 */     return PluginManagerImpl.startAzureus(ui_type, properties);
/*     */   }
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
/*     */   public static void stopAzureus()
/*     */     throws PluginException
/*     */   {}
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
/*     */   public static void restartAzureus()
/*     */     throws PluginException
/*     */   {}
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
/*     */   public static void registerPlugin(Class plugin_class)
/*     */   {
/* 160 */     PluginManagerImpl.registerPlugin(plugin_class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerPlugin(Plugin plugin, String id)
/*     */   {
/* 168 */     PluginManagerImpl.registerPlugin(plugin, id, plugin.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerPlugin(Plugin plugin, String id, String config_key)
/*     */   {
/* 177 */     PluginManagerImpl.registerPlugin(plugin, id, config_key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface getPluginInterfaceByID(String paramString, boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface getPluginInterfaceByClass(String paramString, boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface getPluginInterfaceByClass(Class paramClass, boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface[] getPluginInterfaces();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface getDefaultPluginInterface();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface[] getPlugins();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInterface[] getPlugins(boolean paramBoolean);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void firePluginEvent(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PluginInstaller getPluginInstaller();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void refreshPluginList()
/*     */   {
/* 254 */     refreshPluginList(true);
/*     */   }
/*     */   
/*     */   public abstract void refreshPluginList(boolean paramBoolean);
/*     */   
/*     */   public abstract boolean isSilentRestartEnabled();
/*     */   
/*     */   public abstract boolean isInitialized();
/*     */   
/*     */   public abstract void executeCloseAction(String paramString)
/*     */     throws PluginException;
/*     */   
/*     */   public abstract PluginInterface getPluginInterfaceByID(String paramString);
/*     */   
/*     */   public abstract PluginInterface getPluginInterfaceByClass(String paramString);
/*     */   
/*     */   public abstract PluginInterface getPluginInterfaceByClass(Class paramClass);
/*     */   
/*     */   public abstract List<PluginInterface> getPluginsWithMethod(String paramString, Class<?>[] paramArrayOfClass);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/PluginManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */