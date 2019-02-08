/*     */ package com.aelitis.azureus.ui.swt.player;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
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
/*     */ public class PlayerInstaller
/*     */ {
/*     */   private PlayerInstallerListener listener;
/*     */   private PluginInstaller installer;
/*     */   private volatile UpdateCheckInstance instance;
/*     */   private boolean cancelled;
/*     */   
/*     */   public void setListener(PlayerInstallerListener listener)
/*     */   {
/*  50 */     this.listener = listener;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/*  56 */     UpdateCheckInstance to_cancel = null;
/*     */     
/*  58 */     synchronized (this)
/*     */     {
/*  60 */       this.cancelled = true;
/*     */       
/*  62 */       to_cancel = this.instance;
/*     */     }
/*     */     
/*  65 */     if (to_cancel != null)
/*     */     {
/*  67 */       to_cancel.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean install()
/*     */   {
/*     */     try
/*     */     {
/*  75 */       this.installer = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInstaller();
/*     */       
/*  77 */       StandardPlugin sp = this.installer.getStandardPlugin("azemp");
/*     */       
/*  79 */       Map<Integer, Object> properties = new HashMap();
/*     */       
/*  81 */       properties.put(Integer.valueOf(1), Integer.valueOf(3));
/*     */       
/*  83 */       properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*     */       
/*  85 */       final AESemaphore sem = new AESemaphore("emp install");
/*  86 */       final boolean[] result = new boolean[1];
/*     */       
/*  88 */       this.instance = this.installer.install(new InstallablePlugin[] { sp }, false, properties, new PluginInstallationListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void completed()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*  98 */           result[0] = true;
/*  99 */           if (PlayerInstaller.this.listener != null) {
/* 100 */             PlayerInstaller.this.listener.finished();
/*     */           }
/* 102 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */         public void cancelled()
/*     */         {
/* 108 */           result[0] = false;
/* 109 */           if (PlayerInstaller.this.listener != null) {
/* 110 */             PlayerInstaller.this.listener.finished();
/*     */           }
/* 112 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void failed(PluginException e)
/*     */         {
/* 119 */           result[0] = false;
/* 120 */           if (PlayerInstaller.this.listener != null) {
/* 121 */             PlayerInstaller.this.listener.finished();
/*     */           }
/* 123 */           sem.release();
/*     */         }
/*     */       });
/*     */       
/*     */       boolean kill_it;
/*     */       
/* 129 */       synchronized (this)
/*     */       {
/* 131 */         kill_it = this.cancelled;
/*     */       }
/*     */       
/* 134 */       if (kill_it)
/*     */       {
/* 136 */         this.instance.cancel();
/*     */         
/* 138 */         return false;
/*     */       }
/*     */       
/* 141 */       this.instance.addListener(new UpdateCheckInstanceListener()
/*     */       {
/*     */         public void cancelled(UpdateCheckInstance instance) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void complete(UpdateCheckInstance instance)
/*     */         {
/* 154 */           Update[] updates = instance.getUpdates();
/*     */           
/* 156 */           for (Update update : updates)
/*     */           {
/* 158 */             ResourceDownloader[] rds = update.getDownloaders();
/*     */             
/* 160 */             for (ResourceDownloader rd : rds)
/*     */             {
/* 162 */               rd.addListener(new ResourceDownloaderAdapter()
/*     */               {
/*     */                 public void reportActivity(ResourceDownloader downloader, String activity) {}
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
/*     */                 public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */                 {
/* 178 */                   if (PlayerInstaller.this.listener != null) {
/* 179 */                     PlayerInstaller.this.listener.progress(percentage);
/*     */                   }
/*     */                   
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/* 187 */       });
/* 188 */       sem.reserve();
/*     */       
/* 190 */       return result[0];
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 196 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/player/PlayerInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */