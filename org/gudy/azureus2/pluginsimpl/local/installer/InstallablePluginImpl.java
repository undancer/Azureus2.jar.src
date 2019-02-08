/*     */ package org.gudy.azureus2.pluginsimpl.local.installer;
/*     */ 
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin;
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
/*     */ public abstract class InstallablePluginImpl
/*     */   implements InstallablePlugin
/*     */ {
/*     */   private PluginInstallerImpl installer;
/*     */   
/*     */   protected InstallablePluginImpl(PluginInstallerImpl _installer)
/*     */   {
/*  48 */     this.installer = _installer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isAlreadyInstalled()
/*     */   {
/*  59 */     PluginInterface pi = getAlreadyInstalledPlugin();
/*     */     
/*  61 */     if (pi == null)
/*     */     {
/*  63 */       return false;
/*     */     }
/*     */     
/*  66 */     String version = getVersion();
/*     */     
/*  68 */     if ((version == null) || (version.length() == 0))
/*     */     {
/*  70 */       return false;
/*     */     }
/*     */     
/*  73 */     String existing_version = pi.getPluginVersion();
/*     */     
/*     */ 
/*     */ 
/*  77 */     if (existing_version == null)
/*     */     {
/*  79 */       return true;
/*     */     }
/*     */     
/*  82 */     return Constants.compareVersions(existing_version, version) >= 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInterface getAlreadyInstalledPlugin()
/*     */   {
/*  88 */     return this.installer.getAlreadyInstalledPlugin(getId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void install(boolean shared)
/*     */     throws PluginException
/*     */   {
/*  97 */     this.installer.install(this, shared);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void install(boolean shared, boolean low_noise, final boolean wait_until_done)
/*     */     throws PluginException
/*     */   {
/* 108 */     final AESemaphore sem = new AESemaphore("FPI");
/*     */     
/* 110 */     final PluginException[] error = { null };
/*     */     
/* 112 */     this.installer.install(new InstallablePlugin[] { this }, shared, low_noise, null, new PluginInstallationListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void completed()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 122 */         sem.release();
/*     */       }
/*     */       
/*     */ 
/*     */       public void cancelled()
/*     */       {
/* 128 */         failed(new PluginException("Install cancelled"));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void failed(PluginException e)
/*     */       {
/* 135 */         error[0] = e;
/*     */         
/* 137 */         sem.release();
/*     */         
/* 139 */         if (!wait_until_done)
/*     */         {
/* 141 */           Debug.out("Install failed", e);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 146 */     if (wait_until_done)
/*     */     {
/* 148 */       sem.reserve();
/*     */       
/* 150 */       if (error[0] != null)
/*     */       {
/* 152 */         throw error[0];
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void uninstall()
/*     */     throws PluginException
/*     */   {
/* 162 */     this.installer.uninstall(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public PluginInstaller getInstaller()
/*     */   {
/* 168 */     return this.installer;
/*     */   }
/*     */   
/*     */   public abstract void addUpdate(UpdateCheckInstance paramUpdateCheckInstance, PluginUpdatePlugin paramPluginUpdatePlugin, Plugin paramPlugin, PluginInterface paramPluginInterface);
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/installer/InstallablePluginImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */