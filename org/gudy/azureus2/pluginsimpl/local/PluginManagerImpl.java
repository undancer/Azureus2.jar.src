/*     */ package org.gudy.azureus2.pluginsimpl.local;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.pluginsimpl.local.installer.PluginInstallerImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ipc.IPCInterfaceImpl;
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
/*     */ public class PluginManagerImpl
/*     */   extends PluginManager
/*     */ {
/*  45 */   protected static boolean running = false;
/*     */   
/*     */   private static final boolean GET_PI_METHODS_OPERATIONAL_FLAG_DEFAULT = true;
/*     */   
/*     */   protected static PluginManagerImpl singleton;
/*  50 */   protected static AEMonitor class_mon = new AEMonitor("PluginManager");
/*     */   
/*     */   protected static AzureusCore azureus_core;
/*     */   protected PluginInitializer pi;
/*     */   
/*     */   protected static PluginManagerImpl getSingleton(PluginInitializer pi)
/*     */   {
/*     */     try
/*     */     {
/*  59 */       class_mon.enter();
/*     */       
/*  61 */       if (singleton == null)
/*     */       {
/*  63 */         singleton = new PluginManagerImpl(pi);
/*     */       }
/*     */       
/*  66 */       return singleton;
/*     */     }
/*     */     finally
/*     */     {
/*  70 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static PluginManager startAzureus(int ui_type, Properties properties)
/*     */   {
/*     */     try
/*     */     {
/*  80 */       class_mon.enter();
/*     */       
/*  82 */       if (running)
/*     */       {
/*  84 */         throw new RuntimeException(Constants.APP_NAME + " is already running");
/*     */       }
/*     */       
/*  87 */       running = true;
/*     */     }
/*     */     finally
/*     */     {
/*  91 */       class_mon.exit();
/*     */     }
/*     */     
/*  94 */     String config_dir = (String)properties.get("USER_DIR");
/*     */     
/*  96 */     if (config_dir != null)
/*     */     {
/*  98 */       System.setProperty("azureus.config.path", config_dir);
/*     */     }
/*     */     
/* 101 */     String user_dir = (String)properties.get("APP_DIR");
/*     */     
/* 103 */     if (user_dir != null)
/*     */     {
/* 105 */       System.setProperty("azureus.install.path", user_dir);
/* 106 */       System.setProperty("user.dir", user_dir);
/*     */     }
/*     */     
/* 109 */     String doc_dir = (String)properties.get("DOC_DIR");
/*     */     
/* 111 */     if (doc_dir != null)
/*     */     {
/* 113 */       System.setProperty("azureus.doc.path", doc_dir);
/*     */     }
/*     */     
/*     */ 
/* 117 */     String disable_native = (String)properties.get("DISABLE_NATIVE");
/*     */     
/* 119 */     if ((disable_native != null) && (disable_native.equalsIgnoreCase("true")))
/*     */     {
/* 121 */       System.setProperty("azureus.platform.manager.disable", "true");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 128 */     if (ui_type == 0)
/*     */     {
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 135 */         azureus_core = AzureusCoreFactory.create();
/*     */         
/* 137 */         azureus_core.start();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 141 */         Debug.printStackTrace(e);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 146 */         throw new RuntimeException(Constants.APP_NAME + " failed to start", e);
/*     */       }
/* 148 */     } else if (ui_type == 1)
/*     */     {
/* 150 */       String mi = (String)properties.get("MULTI_INSTANCE");
/*     */       
/* 152 */       if ((mi != null) && (mi.equalsIgnoreCase("true")))
/*     */       {
/* 154 */         System.setProperty("MULTI_INSTANCE", "true");
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 160 */         Class.forName("org.gudy.azureus2.ui.swt.Main").getMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { new String[0] });
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/* 166 */         throw new RuntimeException("Main method invocation failed", e);
/*     */       }
/*     */     }
/*     */     
/* 170 */     if (azureus_core == null)
/*     */     {
/* 172 */       throw new RuntimeException(Constants.APP_NAME + " core failed to initialise");
/*     */     }
/*     */     
/* 175 */     return azureus_core.getPluginManager();
/*     */   }
/*     */   
/*     */ 
/*     */   public static void stopAzureus()
/*     */     throws PluginException
/*     */   {
/*     */     try
/*     */     {
/* 184 */       class_mon.enter();
/*     */       
/* 186 */       if (!running)
/*     */       {
/* 188 */         throw new RuntimeException(Constants.APP_NAME + " is not running");
/*     */       }
/*     */       try
/*     */       {
/* 192 */         azureus_core.requestStop();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 196 */         throw new PluginException("PluginManager: " + Constants.APP_NAME + " close action failed", e);
/*     */       }
/*     */       
/* 199 */       running = false;
/*     */     }
/*     */     finally
/*     */     {
/* 203 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void restartAzureus()
/*     */     throws PluginException
/*     */   {
/* 212 */     if (!running)
/*     */     {
/* 214 */       throw new RuntimeException(Constants.APP_NAME + " is not running");
/*     */     }
/*     */     try
/*     */     {
/* 218 */       azureus_core.requestRestart();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 222 */       throw new PluginException("PluginManager: " + Constants.APP_NAME + " restart action failed", e);
/*     */     }
/*     */     
/* 225 */     running = false;
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
/*     */   public static void setStartDetails(AzureusCore _core)
/*     */   {
/* 238 */     azureus_core = _core;
/*     */     
/* 240 */     running = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void registerPlugin(Class plugin_class)
/*     */   {
/* 247 */     PluginInitializer.queueRegistration(plugin_class);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerPlugin(Plugin plugin, String id, String config_key)
/*     */   {
/* 256 */     PluginInitializer.queueRegistration(plugin, id, config_key);
/*     */   }
/*     */   
/*     */   public PluginInterface getPluginInterfaceByID(String id) {
/* 260 */     return getPluginInterfaceByID(id, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PluginInterface getPluginInterfaceByID(String id, boolean operational)
/*     */   {
/* 268 */     PluginInterface[] p = getPluginInterfaces();
/*     */     
/* 270 */     for (int i = 0; i < p.length; i++)
/*     */     {
/* 272 */       if (p[i].getPluginID().equalsIgnoreCase(id))
/*     */       {
/* 274 */         if ((operational) && (!p[i].getPluginState().isOperational())) { return null;
/*     */         }
/* 276 */         return p[i];
/*     */       }
/*     */     }
/*     */     
/* 280 */     return null;
/*     */   }
/*     */   
/*     */   public PluginInterface getPluginInterfaceByClass(Class c) {
/* 284 */     return getPluginInterfaceByClass(c, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PluginInterface getPluginInterfaceByClass(Class c, boolean operational)
/*     */   {
/* 292 */     PluginInterface[] p = getPluginInterfaces();
/*     */     
/* 294 */     for (int i = 0; i < p.length; i++)
/*     */     {
/* 296 */       if (p[i].getPlugin().getClass().equals(c))
/*     */       {
/* 298 */         if ((operational) && (!p[i].getPluginState().isOperational())) { return null;
/*     */         }
/* 300 */         return p[i];
/*     */       }
/*     */     }
/*     */     
/* 304 */     return null;
/*     */   }
/*     */   
/*     */   public PluginInterface getPluginInterfaceByClass(String class_name) {
/* 308 */     return getPluginInterfaceByClass(class_name, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PluginInterface getPluginInterfaceByClass(String class_name, boolean operational)
/*     */   {
/* 316 */     PluginInterface[] p = getPluginInterfaces();
/*     */     
/* 318 */     for (int i = 0; i < p.length; i++)
/*     */     {
/* 320 */       if (p[i].getPlugin().getClass().getName().equals(class_name))
/*     */       {
/* 322 */         if ((operational) && (!p[i].getPluginState().isOperational())) { return null;
/*     */         }
/* 324 */         return p[i];
/*     */       }
/*     */     }
/*     */     
/* 328 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface[] getPluginInterfaces()
/*     */   {
/* 334 */     List l = PluginInitializer.getPluginInterfaces();
/*     */     
/* 336 */     PluginInterface[] res = new PluginInterface[l.size()];
/*     */     
/* 338 */     l.toArray(res);
/*     */     
/* 340 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getDefaultPluginInterface()
/*     */   {
/* 346 */     return PluginInitializer.getDefaultInterface();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PluginManagerImpl(PluginInitializer _pi)
/*     */   {
/* 355 */     this.pi = _pi;
/*     */     
/*     */ 
/*     */ 
/* 359 */     getPluginInstaller();
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface[] getPlugins()
/*     */   {
/* 365 */     return this.pi.getPlugins();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PluginInterface[] getPlugins(boolean expect_partial_result)
/*     */   {
/* 372 */     return this.pi.getPlugins(expect_partial_result);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void firePluginEvent(int ev)
/*     */   {
/* 379 */     PluginInitializer.fireEvent(ev);
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInstaller getPluginInstaller()
/*     */   {
/* 385 */     return PluginInstallerImpl.getSingleton(this);
/*     */   }
/*     */   
/*     */   public void refreshPluginList(boolean initialise) {
/* 389 */     List loadedPlugins = this.pi.loadPlugins(this.pi.getAzureusCore(), true, true, false, initialise);
/* 390 */     for (Iterator iter = loadedPlugins.iterator(); iter.hasNext();) {
/* 391 */       PluginInterfaceImpl plugin = (PluginInterfaceImpl)iter.next();
/*     */       
/*     */ 
/*     */ 
/* 395 */       if (!plugin.getPluginState().isOperational()) {
/*     */         try {
/* 397 */           this.pi.reloadPlugin(plugin, false, initialise);
/*     */         }
/*     */         catch (PluginException e) {
/* 400 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSilentRestartEnabled()
/*     */   {
/* 409 */     PluginInterface[] pis = this.pi.getPlugins();
/*     */     
/* 411 */     for (int i = 0; i < pis.length; i++)
/*     */     {
/* 413 */       if (pis[i].getPluginProperties().getProperty("plugin.silentrestart.disabled", "").equalsIgnoreCase("true"))
/*     */       {
/* 415 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 419 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInitialized()
/*     */   {
/* 425 */     return this.pi.isInitialized();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void executeCloseAction(String action)
/*     */     throws PluginException
/*     */   {
/* 435 */     if (azureus_core == null)
/*     */     {
/* 437 */       throw new PluginException(Constants.APP_NAME + " is not running");
/*     */     }
/*     */     try
/*     */     {
/* 441 */       azureus_core.executeCloseAction(action, "plugin requested");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 445 */       throw new PluginException("PluginManager: " + Constants.APP_NAME + " restart action failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<PluginInterface> getPluginsWithMethod(String name, Class<?>[] parameters)
/*     */   {
/* 454 */     List<PluginInterface> result = new ArrayList();
/*     */     
/* 456 */     List<PluginInterfaceImpl> pis = PluginInitializer.getPluginInterfaces();
/*     */     
/* 458 */     for (PluginInterfaceImpl pi : pis)
/*     */     {
/* 460 */       if (pi.getIPC().canInvoke(name, parameters))
/*     */       {
/* 462 */         result.add(pi);
/*     */       }
/*     */     }
/*     */     
/* 466 */     return result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */