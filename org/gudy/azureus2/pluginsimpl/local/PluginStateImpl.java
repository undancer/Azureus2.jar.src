/*     */ package org.gudy.azureus2.pluginsimpl.local;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.UnloadablePlugin;
/*     */ import org.gudy.azureus2.pluginsimpl.local.installer.PluginInstallerImpl;
/*     */ import org.gudy.azureus2.update.UpdaterUtils;
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
/*     */ public class PluginStateImpl
/*     */   implements PluginState
/*     */ {
/*     */   private PluginInterfaceImpl pi;
/*     */   private PluginInitializer initialiser;
/*     */   private boolean disabled;
/*     */   boolean operational;
/*     */   boolean failed;
/*     */   
/*     */   public PluginStateImpl(PluginInterfaceImpl pi, PluginInitializer initialiser)
/*     */   {
/*  45 */     this.pi = pi;
/*  46 */     this.initialiser = initialiser;
/*     */   }
/*     */   
/*     */   public void setLoadedAtStartup(boolean load_at_startup) {
/*  50 */     String param_name = "PluginInfo." + this.pi.getPluginID() + ".enabled";
/*  51 */     COConfigurationManager.setParameter(param_name, load_at_startup);
/*     */   }
/*     */   
/*     */   public boolean isLoadedAtStartup() {
/*  55 */     String param_name = "PluginInfo." + this.pi.getPluginID() + ".enabled";
/*  56 */     if (!COConfigurationManager.hasParameter(param_name, false)) {
/*  57 */       return true;
/*     */     }
/*  59 */     return COConfigurationManager.getBooleanParameter(param_name);
/*     */   }
/*     */   
/*     */   public boolean hasFailed() {
/*  63 */     return this.failed;
/*     */   }
/*     */   
/*     */   public void setDisabled(boolean _disabled) {
/*  67 */     this.disabled = _disabled;
/*     */   }
/*     */   
/*     */   public boolean isDisabled() {
/*  71 */     return this.disabled;
/*     */   }
/*     */   
/*     */   public boolean isBuiltIn() {
/*  75 */     String dir = this.pi.getPluginDirectoryName();
/*  76 */     if (dir == null) {
/*  77 */       return PluginInitializer.isLoadingBuiltin();
/*     */     }
/*  79 */     return (dir.length() == 0) || (this.pi.getPluginID().equals(UpdaterUtils.AZUPDATER_PLUGIN_ID)) || (this.pi.getPluginID().equals(UpdaterUtils.AZUPDATERPATCHER_PLUGIN_ID));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isMandatory()
/*     */   {
/*  86 */     if ((this.pi.getPluginID().equals(UpdaterUtils.AZUPDATER_PLUGIN_ID)) || (this.pi.getPluginID().equals(UpdaterUtils.AZUPDATERPATCHER_PLUGIN_ID)))
/*     */     {
/*     */ 
/*  89 */       return true;
/*     */     }
/*     */     
/*  92 */     String mand = this.pi.getPluginProperties().getProperty("plugin.mandatory");
/*  93 */     return (mand != null) && (mand.trim().toLowerCase().equals("true"));
/*     */   }
/*     */   
/*     */   void setOperational(boolean b, boolean reloading) {
/*  97 */     this.operational = b;
/*     */     
/*  99 */     if (!reloading)
/*     */     {
/* 101 */       this.initialiser.fireOperational(this.pi, this.operational);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isOperational() {
/* 106 */     return this.operational;
/*     */   }
/*     */   
/*     */   public boolean isShared() {
/* 110 */     String shared_dir = FileUtil.getApplicationFile("plugins").toString();
/* 111 */     String plugin_dir = this.pi.getPluginDirectoryName();
/* 112 */     return plugin_dir.startsWith(shared_dir);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInitialisationComplete()
/*     */   {
/* 118 */     return this.initialiser.isInitialisationComplete();
/*     */   }
/*     */   
/*     */   public void reload()
/*     */     throws PluginException
/*     */   {
/* 124 */     if ((isUnloadable()) || (isOperational())) unload(true);
/* 125 */     this.initialiser.reloadPlugin(this.pi);
/*     */   }
/*     */   
/*     */   public void uninstall() throws PluginException {
/* 129 */     PluginInstallerImpl.getSingleton(this.pi.getPluginManager()).uninstall(this.pi);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUnloaded()
/*     */   {
/* 135 */     return this.pi.class_loader == null;
/*     */   }
/*     */   
/*     */   public void unload() throws PluginException {
/* 139 */     unload(false);
/*     */   }
/*     */   
/*     */   protected void unload(boolean for_reload) throws PluginException {
/* 143 */     if (!isUnloadable()) {
/* 144 */       throw new PluginException("Plugin isn't unloadable");
/*     */     }
/*     */     
/* 147 */     String dir = this.pi.getPluginDirectoryName();
/*     */     
/*     */ 
/* 150 */     if ((dir == null) || (dir.length() == 0)) {
/*     */       try {
/* 152 */         ((UnloadablePlugin)this.pi.getPlugin()).unload();
/*     */       } catch (Throwable e) {
/* 154 */         Debug.out("Plugin unload operation failed", e);
/*     */       }
/* 156 */       this.initialiser.unloadPlugin(this.pi);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 161 */       List pis = new ArrayList(PluginInitializer.getPluginInterfaces());
/* 162 */       for (int i = 0; i < pis.size(); i++) {
/* 163 */         PluginInterfaceImpl pi = (PluginInterfaceImpl)pis.get(i);
/* 164 */         String other_dir = pi.getPluginDirectoryName();
/* 165 */         if ((other_dir != null) && (other_dir.length() != 0) && 
/* 166 */           (dir.equals(other_dir))) {
/*     */           try {
/* 168 */             ((UnloadablePlugin)pi.getPlugin()).unload();
/*     */           } catch (Throwable e) {
/* 170 */             Debug.out("Plugin unload operation failed", e);
/*     */           }
/* 172 */           this.initialiser.unloadPlugin(pi);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 177 */     for (int i = 0; i < this.pi.children.size(); i++) {
/* 178 */       ((PluginStateImpl)((PluginInterface)this.pi.children.get(i)).getPluginState()).unload(for_reload);
/*     */     }
/*     */     
/* 181 */     setOperational(false, for_reload);
/* 182 */     this.pi.destroy();
/*     */   }
/*     */   
/*     */   public boolean isUnloadable() {
/* 186 */     String dir = this.pi.getPluginDirectoryName();
/*     */     
/*     */ 
/* 189 */     boolean disable_unload = this.pi.getPluginProperties().getProperty("plugin.unload.disabled", "").equalsIgnoreCase("true");
/* 190 */     if (disable_unload) { return false;
/*     */     }
/*     */     
/* 193 */     if ((dir == null) || (dir.length() == 0)) {
/* 194 */       return this.pi.getPlugin() instanceof UnloadablePlugin;
/*     */     }
/*     */     
/* 197 */     List pis = PluginInitializer.getPluginInterfaces();
/* 198 */     for (int i = 0; i < pis.size(); i++) {
/* 199 */       PluginInterface pi = (PluginInterface)pis.get(i);
/* 200 */       String other_dir = pi.getPluginDirectoryName();
/* 201 */       if ((other_dir != null) && (other_dir.length() != 0) && 
/* 202 */         (dir.equals(other_dir)) && 
/* 203 */         (!(pi.getPlugin() instanceof UnloadablePlugin))) {
/* 204 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 209 */     for (int i = 0; i < this.pi.children.size(); i++) {
/* 210 */       if (!((PluginInterface)this.pi.children.get(i)).getPluginState().isUnloadable()) {
/* 211 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 215 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/PluginStateImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */