/*     */ package com.aelitis.azureus.ui.swt.plugininstall;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SimplePluginInstaller
/*     */ {
/*     */   private String plugin_id;
/*     */   private UIFunctions.actionListener action_listener;
/*     */   private SimplePluginInstallerListener listener;
/*     */   private PluginInstaller installer;
/*     */   private volatile UpdateCheckInstance instance;
/*     */   private boolean completed;
/*     */   private boolean cancelled;
/*     */   
/*     */   public SimplePluginInstaller(String _plugin_id, String _resource_prefix, final UIFunctions.actionListener _action_listener)
/*     */   {
/*  60 */     this.plugin_id = _plugin_id;
/*     */     
/*  62 */     PluginInterface existing = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID(this.plugin_id);
/*     */     
/*  64 */     if (existing != null)
/*     */     {
/*  66 */       if (existing.getPluginState().isOperational())
/*     */       {
/*  68 */         _action_listener.actionComplete(Boolean.valueOf(true));
/*     */       }
/*     */       else
/*     */       {
/*  72 */         _action_listener.actionComplete(new Exception("Plugin is installed but not operational"));
/*     */       }
/*     */       
/*  75 */       return;
/*     */     }
/*     */     
/*  78 */     this.action_listener = new UIFunctions.actionListener()
/*     */     {
/*     */ 
/*  81 */       private boolean informed = false;
/*     */       
/*     */ 
/*     */ 
/*     */       public void actionComplete(Object result)
/*     */       {
/*  87 */         synchronized (this)
/*     */         {
/*  89 */           if (this.informed)
/*     */           {
/*  91 */             return;
/*     */           }
/*     */           
/*  94 */           this.informed = true;
/*     */         }
/*     */         
/*  97 */         _action_listener.actionComplete(result);
/*     */       }
/*     */       
/* 100 */     };
/* 101 */     SimplePluginInstallWindow window = new SimplePluginInstallWindow(this, _resource_prefix);
/*     */     
/* 103 */     window.open();
/*     */     
/* 105 */     AEThread2 installerThread = new AEThread2("plugin installer", true)
/*     */     {
/*     */       public void run()
/*     */       {
/* 109 */         SimplePluginInstaller.this.install();
/*     */       }
/*     */       
/* 112 */     };
/* 113 */     installerThread.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setListener(SimplePluginInstallerListener listener)
/*     */   {
/* 120 */     this.listener = listener;
/*     */   }
/*     */   
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 126 */     UpdateCheckInstance to_cancel = null;
/*     */     
/* 128 */     synchronized (this)
/*     */     {
/* 130 */       if (this.completed)
/*     */       {
/* 132 */         return;
/*     */       }
/*     */       
/* 135 */       this.cancelled = true;
/*     */       
/* 137 */       to_cancel = this.instance;
/*     */     }
/*     */     
/* 140 */     if (to_cancel != null)
/*     */     {
/* 142 */       to_cancel.cancel();
/*     */     }
/*     */     
/* 145 */     this.action_listener.actionComplete(new Exception("Cancelled"));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean install()
/*     */   {
/*     */     try
/*     */     {
/* 153 */       this.installer = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInstaller();
/*     */       
/* 155 */       StandardPlugin sp = this.installer.getStandardPlugin(this.plugin_id);
/*     */       
/* 157 */       if (sp == null)
/*     */       {
/* 159 */         throw new Exception("Unknown plugin");
/*     */       }
/*     */       
/* 162 */       Map<Integer, Object> properties = new HashMap();
/*     */       
/* 164 */       properties.put(Integer.valueOf(1), Integer.valueOf(3));
/*     */       
/* 166 */       properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*     */       
/* 168 */       final AESemaphore sem = new AESemaphore("plugin-install");
/*     */       
/* 170 */       final Object[] result = { null };
/*     */       
/* 172 */       this.instance = this.installer.install(new InstallablePlugin[] { sp }, false, properties, new PluginInstallationListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void completed()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 182 */           synchronized (SimplePluginInstaller.this)
/*     */           {
/* 184 */             SimplePluginInstaller.this.completed = true;
/*     */           }
/*     */           
/* 187 */           result[0] = Boolean.valueOf(true);
/*     */           
/* 189 */           if (SimplePluginInstaller.this.listener != null)
/*     */           {
/* 191 */             SimplePluginInstaller.this.listener.finished();
/*     */           }
/*     */           
/* 194 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */         public void cancelled()
/*     */         {
/* 200 */           result[0] = new Exception("Cancelled");
/*     */           
/* 202 */           if (SimplePluginInstaller.this.listener != null)
/*     */           {
/* 204 */             SimplePluginInstaller.this.listener.finished();
/*     */           }
/*     */           
/* 207 */           sem.release();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void failed(PluginException e)
/*     */         {
/* 214 */           result[0] = e;
/*     */           
/* 216 */           if (SimplePluginInstaller.this.listener != null)
/*     */           {
/* 218 */             SimplePluginInstaller.this.listener.finished();
/*     */           }
/*     */           
/* 221 */           sem.release();
/*     */         }
/*     */       });
/*     */       
/*     */       boolean kill_it;
/*     */       
/* 227 */       synchronized (this)
/*     */       {
/* 229 */         kill_it = this.cancelled;
/*     */       }
/*     */       
/* 232 */       if (kill_it)
/*     */       {
/* 234 */         this.instance.cancel();
/*     */         
/* 236 */         this.action_listener.actionComplete(new Exception("Cancelled"));
/*     */         
/* 238 */         return false;
/*     */       }
/*     */       
/* 241 */       this.instance.addListener(new UpdateCheckInstanceListener()
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
/* 254 */           Update[] updates = instance.getUpdates();
/*     */           
/* 256 */           for (Update update : updates)
/*     */           {
/* 258 */             ResourceDownloader[] rds = update.getDownloaders();
/*     */             
/* 260 */             for (ResourceDownloader rd : rds)
/*     */             {
/* 262 */               rd.addListener(new ResourceDownloaderAdapter()
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
/*     */                 public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */                 {
/* 277 */                   if (SimplePluginInstaller.this.listener != null)
/*     */                   {
/* 279 */                     SimplePluginInstaller.this.listener.progress(percentage);
/*     */                   }
/*     */                   
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }
/* 287 */       });
/* 288 */       sem.reserve();
/*     */       
/* 290 */       this.action_listener.actionComplete(result[0]);
/*     */       
/* 292 */       return result[0] instanceof Boolean;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 296 */       if (this.listener != null)
/*     */       {
/* 298 */         this.listener.finished();
/*     */       }
/*     */       
/* 301 */       this.action_listener.actionComplete(e);
/*     */     }
/*     */     
/* 304 */     return false;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/plugininstall/SimplePluginInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */