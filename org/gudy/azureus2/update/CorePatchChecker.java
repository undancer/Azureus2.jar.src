/*     */ package org.gudy.azureus2.update;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateListener;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CorePatchChecker
/*     */   implements Plugin, UpdatableComponent, UpdateCheckInstanceListener
/*     */ {
/*  43 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   public static final boolean TESTING = false;
/*     */   
/*     */   protected PluginInterface plugin_interface;
/*  48 */   private Map<UpdateCheckInstance, Update> my_updates = new HashMap(1);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */     throws PluginException
/*     */   {
/*  56 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  58 */     this.plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  59 */     this.plugin_interface.getPluginProperties().setProperty("plugin.name", "Core Patcher (level=" + CorePatchLevel.getCurrentPatchLevel() + ")");
/*     */     
/*  61 */     if (!Constants.isCVSVersion())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  68 */       this.plugin_interface.getUpdateManager().registerUpdatableComponent(this, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  75 */     return "Core Patch Checker";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMaximumCheckTime()
/*     */   {
/*  82 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkForUpdate(UpdateChecker checker)
/*     */   {
/*     */     try
/*     */     {
/*  90 */       UpdateCheckInstance inst = checker.getCheckInstance();
/*     */       
/*  92 */       inst.addListener(this);
/*     */       
/*  94 */       this.my_updates.put(inst, checker.addUpdate("Core Patch Checker", new String[0], "", "", new ResourceDownloader[0], 3));
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 101 */       checker.completed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancelled(UpdateCheckInstance instance)
/*     */   {
/* 109 */     Update update = (Update)this.my_updates.remove(instance);
/*     */     
/* 111 */     if (update != null)
/*     */     {
/* 113 */       update.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void complete(final UpdateCheckInstance instance)
/*     */   {
/* 121 */     Update my_update = (Update)this.my_updates.remove(instance);
/*     */     
/* 123 */     if (my_update != null)
/*     */     {
/* 125 */       my_update.complete(true);
/*     */     }
/*     */     
/* 128 */     Update[] updates = instance.getUpdates();
/*     */     
/* 130 */     final PluginInterface updater_plugin = this.plugin_interface.getPluginManager().getPluginInterfaceByClass(UpdaterUpdateChecker.class);
/*     */     
/* 132 */     for (int i = 0; i < updates.length; i++)
/*     */     {
/* 134 */       Update update = updates[i];
/*     */       
/* 136 */       Object user_object = update.getUserObject();
/*     */       
/* 138 */       if ((user_object != null) && (user_object == updater_plugin))
/*     */       {
/*     */ 
/*     */ 
/* 142 */         if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 143 */           org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LOGID, "Core Patcher: updater update found"));
/*     */         }
/* 145 */         update.setRestartRequired(3);
/*     */         
/* 147 */         update.addListener(new UpdateListener() {
/*     */           public void complete(Update update) {
/* 149 */             if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 150 */               org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(CorePatchChecker.LOGID, "Core Patcher: updater update complete"));
/*     */             }
/*     */             
/* 153 */             CorePatchChecker.this.patch(instance, update, updater_plugin);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void cancelled(Update update) {}
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void patch(UpdateCheckInstance instance, Update updater_update, PluginInterface updater_plugin)
/*     */   {
/*     */     try
/*     */     {
/* 175 */       ResourceDownloader rd_log = updater_update.getDownloaders()[0];
/*     */       
/* 177 */       File[] files = new File(updater_plugin.getPluginDirectoryName()).listFiles();
/*     */       
/* 179 */       if (files == null)
/*     */       {
/* 181 */         if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 182 */           org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LOGID, "Core Patcher: no files in plugin dir!!!"));
/*     */         }
/*     */         
/* 185 */         return;
/*     */       }
/*     */       
/* 188 */       String patch_prefix = "Azureus2_" + Constants.getBaseVersion() + "_P";
/*     */       
/* 190 */       int highest_p = -1;
/* 191 */       File highest_p_file = null;
/*     */       
/* 193 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 195 */         String name = files[i].getName();
/*     */         
/* 197 */         if ((name.startsWith(patch_prefix)) && (name.endsWith(".pat")))
/*     */         {
/* 199 */           if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 200 */             org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LOGID, "Core Patcher: found patch file '" + name + "'"));
/*     */           }
/*     */           try
/*     */           {
/* 204 */             int this_p = Integer.parseInt(name.substring(patch_prefix.length(), name.indexOf(".pat")));
/*     */             
/* 206 */             if (this_p > highest_p)
/*     */             {
/* 208 */               highest_p = this_p;
/*     */               
/* 210 */               highest_p_file = files[i];
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 214 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 219 */       if (CorePatchLevel.getCurrentPatchLevel() >= highest_p)
/*     */       {
/* 221 */         if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 222 */           org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LOGID, "Core Patcher: no applicable patch found (highest = " + highest_p + ")"));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 228 */         if (updater_update.getRestartRequired() == 3)
/*     */         {
/* 230 */           updater_update.setRestartRequired(1);
/*     */         }
/*     */       }
/*     */       else {
/* 234 */         rd_log.reportActivity("Applying patch '" + highest_p_file.getName() + "'");
/*     */         
/* 236 */         if (org.gudy.azureus2.core3.logging.Logger.isEnabled()) {
/* 237 */           org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LOGID, "Core Patcher: applying patch '" + highest_p_file.toString() + "'"));
/*     */         }
/*     */         
/* 240 */         InputStream pis = new FileInputStream(highest_p_file);
/*     */         try
/*     */         {
/* 243 */           patchAzureus2(instance, pis, "P" + highest_p, this.plugin_interface.getLogger().getChannel("CorePatcher"));
/*     */           
/* 245 */           org.gudy.azureus2.core3.logging.Logger.log(new LogAlert(false, 0, "Patch " + highest_p_file.getName() + " ready to be applied"));
/*     */           
/*     */ 
/* 248 */           String done_file = highest_p_file.toString();
/*     */           
/* 250 */           done_file = done_file.substring(0, done_file.length() - 1) + "x";
/*     */           
/* 252 */           highest_p_file.renameTo(new File(done_file));
/*     */           
/*     */ 
/*     */ 
/* 256 */           updater_update.setRestartRequired(2);
/*     */         } finally {
/*     */           try {
/* 259 */             pis.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 269 */       return;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 266 */       Debug.printStackTrace(e);
/* 267 */       org.gudy.azureus2.core3.logging.Logger.log(new LogAlert(false, "Core Patcher failed", e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void patchAzureus2(UpdateCheckInstance instance, InputStream pis, String resource_tag, LoggerChannel log)
/*     */     throws Exception
/*     */   {
/* 280 */     OutputStream os = null;
/* 281 */     InputStream is = null;
/*     */     try
/*     */     {
/* 284 */       String resource_name = "Azureus2_" + resource_tag + ".jar";
/*     */       
/* 286 */       UpdateInstaller installer = instance.createInstaller();
/*     */       
/* 288 */       File tmp = AETemporaryFileHandler.createTempFile();
/*     */       
/* 290 */       os = new FileOutputStream(tmp);
/*     */       
/*     */       String az2_jar;
/*     */       
/* 294 */       if (Constants.isOSX)
/*     */       {
/* 296 */         az2_jar = installer.getInstallDir() + "/" + SystemProperties.getApplicationName() + ".app/Contents/Resources/Java/";
/*     */       }
/*     */       else
/*     */       {
/* 300 */         az2_jar = installer.getInstallDir() + File.separator;
/*     */       }
/*     */       
/* 303 */       String az2_jar = az2_jar + "Azureus2.jar";
/*     */       
/* 305 */       is = new FileInputStream(az2_jar);
/*     */       
/* 307 */       new UpdateJarPatcher(is, pis, os, log);
/*     */       
/* 309 */       is.close();
/*     */       
/* 311 */       is = null;
/*     */       
/* 313 */       pis.close();
/*     */       
/* 315 */       pis = null;
/*     */       
/* 317 */       os.close();
/*     */       
/* 319 */       os = null;
/*     */       
/* 321 */       installer.addResource(resource_name, new FileInputStream(tmp));
/*     */       
/*     */ 
/* 324 */       tmp.delete();
/*     */       
/* 326 */       installer.addMoveAction(resource_name, az2_jar); return;
/*     */     }
/*     */     finally
/*     */     {
/* 330 */       if (is != null) {
/*     */         try {
/* 332 */           is.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/* 336 */       if (os != null) {
/*     */         try {
/* 338 */           os.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/* 342 */       if (pis != null) {
/*     */         try {
/* 344 */           pis.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/update/CorePatchChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */