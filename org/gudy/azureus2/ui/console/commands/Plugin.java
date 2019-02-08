/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeSet;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoader;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderFactory;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.gudy.azureus2.ui.console.util.TextWrap;
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
/*     */ public class Plugin
/*     */   extends IConsoleCommand
/*     */ {
/*     */   public Plugin()
/*     */   {
/*  53 */     super("plugin");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/*  58 */     return "plugin [various options]\t\tRun with no parameter for more help.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  62 */     out.println("> -----");
/*  63 */     out.println("Subcommands:");
/*  64 */     out.println("install\t[pluginid]\tLists plugins available to install or installs a given plugin");
/*  65 */     out.println("location\t\tLists where plugins are being loaded from");
/*  66 */     out.println("list\t\t\tList all running plugins");
/*  67 */     out.println("listall\t\t\tList all plugins - running or not");
/*  68 */     out.println("status pluginid\t\tPrints the status of a given plugin");
/*  69 */     out.println("startup pluginid on|off\tEnables or disables the plugin running at startup");
/*  70 */     out.println("uninstall pluginid\t\tUninstalls a plugin");
/*  71 */     out.println("update\t\tUpdates all plugins with outstanding updates");
/*  72 */     out.println("> -----");
/*     */   }
/*     */   
/*     */   public void execute(String commandName, final ConsoleInput ci, List args) {
/*  76 */     if (args.isEmpty()) {
/*  77 */       printHelpExtra(ci.out, args);
/*  78 */       return;
/*     */     }
/*     */     
/*  81 */     String subcmd = (String)args.get(0);
/*  82 */     if (!Arrays.asList(new String[] { "location", "list", "listall", "status", "startup", "install", "uninstall", "update" }).contains(subcmd))
/*     */     {
/*     */ 
/*  85 */       ci.out.println("Invalid subcommand: " + subcmd);
/*  86 */       ci.out.println();
/*  87 */       return;
/*     */     }
/*     */     
/*  90 */     PluginManager plugin_manager = ci.getCore().getPluginManager();
/*     */     
/*  92 */     if ((subcmd.equals("list")) || (subcmd.equals("listall"))) {
/*  93 */       boolean all_plugins = subcmd.equals("listall");
/*  94 */       ci.out.println("> -----");
/*  95 */       PluginInterface[] plugins = plugin_manager.getPluginInterfaces();
/*  96 */       TreeSet plugin_ids = new TreeSet(String.CASE_INSENSITIVE_ORDER);
/*  97 */       for (int i = 0; i < plugins.length; i++)
/*  98 */         if ((all_plugins) || (plugins[i].getPluginState().isOperational())) {
/*  99 */           String plugin_id = plugins[i].getPluginID();
/* 100 */           plugin_ids.add(plugin_id);
/*     */         }
/* 102 */       TextWrap.printList(plugin_ids.iterator(), ci.out, "   ");
/* 103 */       ci.out.println("> -----");
/* 104 */       return;
/*     */     }
/*     */     
/* 107 */     if (subcmd.equals("location"))
/*     */     {
/* 109 */       File fUserPluginDir = FileUtil.getUserFile("plugins");
/* 110 */       String sep = File.separator;
/*     */       
/*     */       String sUserPluginDir;
/*     */       try
/*     */       {
/* 115 */         sUserPluginDir = fUserPluginDir.getCanonicalPath();
/*     */       } catch (Throwable e) {
/* 117 */         sUserPluginDir = fUserPluginDir.toString();
/*     */       }
/*     */       
/* 120 */       if (!sUserPluginDir.endsWith(sep)) {
/* 121 */         sUserPluginDir = sUserPluginDir + sep;
/*     */       }
/*     */       
/* 124 */       File fAppPluginDir = FileUtil.getApplicationFile("plugins");
/*     */       
/*     */       String sAppPluginDir;
/*     */       try
/*     */       {
/* 129 */         sAppPluginDir = fAppPluginDir.getCanonicalPath();
/*     */       } catch (Throwable e) {
/* 131 */         sAppPluginDir = fAppPluginDir.toString();
/*     */       }
/*     */       
/* 134 */       if (!sAppPluginDir.endsWith(sep)) {
/* 135 */         sAppPluginDir = sAppPluginDir + sep;
/*     */       }
/*     */       
/* 138 */       ci.out.println("Shared plugin location:");
/* 139 */       ci.out.println("  " + sAppPluginDir);
/* 140 */       ci.out.println("User plugin location:");
/* 141 */       ci.out.println("  " + sUserPluginDir);
/* 142 */       ci.out.println();
/* 143 */       return;
/*     */     }
/*     */     
/* 146 */     if (subcmd.equals("update"))
/*     */     {
/* 148 */       if (args.size() != 1)
/*     */       {
/* 150 */         ci.out.println("Usage: update");
/*     */         
/* 152 */         return;
/*     */       }
/*     */       
/* 155 */       UpdateManager update_manager = plugin_manager.getDefaultPluginInterface().getUpdateManager();
/*     */       
/* 157 */       UpdateCheckInstance checker = update_manager.createUpdateCheckInstance();
/*     */       
/* 159 */       checker.addListener(new UpdateCheckInstanceListener()
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
/*     */ 
/*     */         public void complete(UpdateCheckInstance instance)
/*     */         {
/* 173 */           Update[] updates = instance.getUpdates();
/*     */           
/*     */           try
/*     */           {
/* 177 */             for (Update update : updates)
/*     */             {
/* 179 */               ci.out.println("Updating " + update.getName());
/*     */               
/* 181 */               for (ResourceDownloader rd : update.getDownloaders())
/*     */               {
/* 183 */                 rd.addListener(new ResourceDownloaderAdapter()
/*     */                 {
/*     */ 
/*     */ 
/*     */                   public void reportActivity(ResourceDownloader downloader, String activity)
/*     */                   {
/*     */ 
/*     */ 
/* 191 */                     Plugin.1.this.val$ci.out.println("\t" + activity);
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */                   public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */                   {
/* 199 */                     Plugin.1.this.val$ci.out.println("\t" + percentage + "%");
/*     */                   }
/*     */                   
/*     */ 
/* 203 */                 });
/* 204 */                 rd.download();
/*     */               }
/*     */             }
/*     */             
/* 208 */             boolean restart_required = false;
/*     */             
/* 210 */             for (int i = 0; i < updates.length; i++)
/*     */             {
/* 212 */               if (updates[i].getRestartRequired() == 2)
/*     */               {
/* 214 */                 restart_required = true;
/*     */               }
/*     */             }
/*     */             
/* 218 */             if (restart_required)
/*     */             {
/* 220 */               ci.out.println("**** Restart required to complete update ****");
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 224 */             ci.out.println("Plugin update failed: " + Debug.getNestedExceptionMessage(e));
/*     */           }
/*     */           
/*     */         }
/* 228 */       });
/* 229 */       checker.start();
/*     */       
/* 231 */       return;
/*     */     }
/*     */     
/* 234 */     if (subcmd.equals("install"))
/*     */     {
/* 236 */       if (args.size() == 1)
/*     */       {
/* 238 */         ci.out.println("Contacting plugin repository for list of available plugins...");
/*     */         try
/*     */         {
/* 241 */           SFPluginDetails[] plugins = SFPluginDetailsLoaderFactory.getSingleton().getPluginDetails();
/*     */           
/* 243 */           for (SFPluginDetails p : plugins)
/*     */           {
/* 245 */             String category = p.getCategory();
/*     */             
/* 247 */             if ((category == null) || (
/*     */             
/* 249 */               (!category.equalsIgnoreCase("hidden")) && (!category.equalsIgnoreCase("core"))))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */               String id = p.getId();
/*     */               
/* 257 */               if (plugin_manager.getPluginInterfaceByID(id, false) == null)
/*     */               {
/* 259 */                 String desc = p.getDescription();
/*     */                 
/* 261 */                 int pos = desc.indexOf("<br");
/*     */                 
/* 263 */                 if (pos > 0)
/*     */                 {
/* 265 */                   desc = desc.substring(0, pos);
/*     */                 }
/*     */                 
/* 268 */                 ci.out.println("\t" + id + ": \t\t" + desc);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 274 */           ci.out.println("Failed to list plugins: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */       }
/*     */       else {
/* 278 */         String target_id = (String)args.get(1);
/*     */         
/* 280 */         if (plugin_manager.getPluginInterfaceByID(target_id, false) != null)
/*     */         {
/* 282 */           ci.out.println("Plugin '" + target_id + "' already installed");
/*     */           
/* 284 */           return;
/*     */         }
/*     */         
/* 287 */         final PluginInstaller installer = plugin_manager.getPluginInstaller();
/*     */         try
/*     */         {
/* 290 */           final StandardPlugin sp = installer.getStandardPlugin(target_id);
/*     */           
/* 292 */           if (sp == null)
/*     */           {
/* 294 */             ci.out.println("Plugin '" + target_id + "' is unknown");
/*     */             
/* 296 */             return;
/*     */           }
/*     */           
/* 299 */           new AEThread2("Plugin Installer")
/*     */           {
/*     */             public void run()
/*     */             {
/*     */               try
/*     */               {
/* 305 */                 Map<Integer, Object> properties = new HashMap();
/*     */                 
/* 307 */                 properties.put(Integer.valueOf(1), Integer.valueOf(3));
/*     */                 
/* 309 */                 properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*     */                 
/* 311 */                 final AESemaphore sem = new AESemaphore("plugin-install");
/*     */                 
/* 313 */                 final boolean[] restart_required = { false };
/*     */                 
/* 315 */                 UpdateCheckInstance instance = installer.install(new InstallablePlugin[] { sp }, false, properties, new PluginInstallationListener()
/*     */                 {
/*     */ 
/*     */ 
/*     */ 
/*     */                   public void completed()
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/* 325 */                     Plugin.2.this.val$ci.out.println("Installation complete");
/*     */                     
/* 327 */                     sem.release();
/*     */                   }
/*     */                   
/*     */ 
/*     */                   public void cancelled()
/*     */                   {
/* 333 */                     Plugin.2.this.val$ci.out.println("Installation cancelled");
/*     */                     
/* 335 */                     sem.release();
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */                   public void failed(PluginException e)
/*     */                   {
/* 342 */                     Plugin.2.this.val$ci.out.println("Installation failed: " + Debug.getNestedExceptionMessage(e));
/*     */                     
/* 344 */                     sem.release();
/*     */                   }
/*     */                   
/* 347 */                 });
/* 348 */                 instance.addListener(new UpdateCheckInstanceListener()
/*     */                 {
/*     */ 
/*     */ 
/*     */                   public void cancelled(UpdateCheckInstance instance)
/*     */                   {
/*     */ 
/* 355 */                     Plugin.2.this.val$ci.out.println("Installation cancelled");
/*     */                   }
/*     */                   
/*     */ 
/*     */ 
/*     */                   public void complete(UpdateCheckInstance instance)
/*     */                   {
/* 362 */                     Update[] updates = instance.getUpdates();
/*     */                     
/* 364 */                     for (Update update : updates)
/*     */                     {
/* 366 */                       ResourceDownloader[] rds = update.getDownloaders();
/*     */                       
/* 368 */                       for (ResourceDownloader rd : rds)
/*     */                       {
/* 370 */                         rd.addListener(new ResourceDownloaderAdapter()
/*     */                         {
/*     */ 
/*     */ 
/*     */                           public void reportActivity(ResourceDownloader downloader, String activity)
/*     */                           {
/*     */ 
/*     */ 
/* 378 */                             Plugin.2.this.val$ci.out.println("\t" + activity);
/*     */                           }
/*     */                           
/*     */ 
/*     */ 
/*     */ 
/*     */                           public void reportPercentComplete(ResourceDownloader downloader, int percentage)
/*     */                           {
/* 386 */                             Plugin.2.this.val$ci.out.println("\t" + percentage + "%");
/*     */                           }
/*     */                         });
/*     */                         
/*     */ 
/*     */ 
/*     */                         try
/*     */                         {
/* 394 */                           rd.download();
/*     */                         }
/*     */                         catch (Throwable e) {}
/*     */                       }
/*     */                       
/*     */ 
/* 400 */                       if (update.getRestartRequired() != 1)
/*     */                       {
/* 402 */                         restart_required[0] = true;
/*     */                       }
/*     */                       
/*     */                     }
/*     */                   }
/* 407 */                 });
/* 408 */                 sem.reserve();
/*     */                 
/* 410 */                 if (restart_required[0] != 0)
/*     */                 {
/* 412 */                   ci.out.println("**** Restart required to complete installation ****");
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 416 */                 ci.out.println("Install failed: " + Debug.getNestedExceptionMessage(e));
/*     */               }
/*     */             }
/*     */           }.start();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 423 */           ci.out.println("Install failed: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */       }
/* 426 */       return;
/*     */     }
/*     */     
/*     */ 
/* 430 */     if (args.size() == 1) {
/* 431 */       ci.out.println("No plugin ID given.");
/* 432 */       ci.out.println();
/* 433 */       return;
/*     */     }
/*     */     
/* 436 */     String plugin_id = (String)args.get(1);
/* 437 */     PluginInterface plugin = plugin_manager.getPluginInterfaceByID(plugin_id, false);
/* 438 */     if (plugin == null) {
/* 439 */       ci.out.println("Invalid plugin ID: " + plugin_id);
/* 440 */       ci.out.println();
/* 441 */       return;
/*     */     }
/*     */     
/* 444 */     if (subcmd.equals("status")) {
/* 445 */       ci.out.println("ID     : " + plugin.getPluginID());
/* 446 */       ci.out.println("Name   : " + plugin.getPluginName());
/* 447 */       ci.out.println("Version: " + plugin.getPluginVersion());
/* 448 */       ci.out.println("Running: " + plugin.getPluginState().isOperational());
/* 449 */       ci.out.println("Runs at startup: " + plugin.getPluginState().isLoadedAtStartup());
/* 450 */       if (!plugin.getPluginState().isBuiltIn()) {
/* 451 */         ci.out.println("Location: " + plugin.getPluginDirectoryName());
/*     */       }
/* 453 */       ci.out.println();
/* 454 */       return;
/*     */     }
/*     */     
/* 457 */     if (subcmd.equals("startup")) {
/* 458 */       if (args.size() == 2) {
/* 459 */         ci.out.println("Need to pass either \"on\" or \"off\"");
/* 460 */         ci.out.println();
/* 461 */         return;
/*     */       }
/* 463 */       String enabled_mode = (String)args.get(2);
/* 464 */       if (enabled_mode.equals("on")) {
/* 465 */         plugin.getPluginState().setLoadedAtStartup(true);
/*     */       }
/* 467 */       else if (enabled_mode.equals("off")) {
/* 468 */         plugin.getPluginState().setLoadedAtStartup(false);
/*     */       }
/*     */       else {
/* 471 */         ci.out.println("Need to pass either \"on\" or \"off\"");
/* 472 */         ci.out.println();
/* 473 */         return;
/*     */       }
/* 475 */       ci.out.println("Done.");
/* 476 */       ci.out.println();
/* 477 */       return;
/*     */     }
/*     */     
/* 480 */     if (subcmd.equals("uninstall"))
/*     */     {
/* 482 */       PluginInterface pi = plugin_manager.getPluginInterfaceByID(plugin_id, false);
/*     */       
/* 484 */       if (pi == null)
/*     */       {
/* 486 */         ci.out.println("Plugin '" + plugin_id + "' is not installed");
/*     */         
/* 488 */         return;
/*     */       }
/*     */       
/* 491 */       PluginInstaller installer = plugin_manager.getPluginInstaller();
/*     */       try
/*     */       {
/* 494 */         StandardPlugin sp = installer.getStandardPlugin(plugin_id);
/*     */         
/* 496 */         if (sp == null)
/*     */         {
/* 498 */           ci.out.println("Plugin '" + plugin_id + "' is not a standard plugin");
/*     */           
/* 500 */           return;
/*     */         }
/*     */         
/* 503 */         PluginInstaller uninstaller = plugin_manager.getPluginInstaller();
/*     */         
/* 505 */         Map<Integer, Object> properties = new HashMap();
/*     */         
/* 507 */         final AESemaphore sem = new AESemaphore("plugin-uninstall");
/*     */         
/* 509 */         UpdateCheckInstance instance = uninstaller.uninstall(new PluginInterface[] { pi }, new PluginInstallationListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void completed()
/*     */           {
/*     */ 
/*     */ 
/* 517 */             ci.out.println("Uninstallation complete");
/*     */             
/* 519 */             sem.release();
/*     */           }
/*     */           
/*     */ 
/*     */           public void cancelled()
/*     */           {
/* 525 */             ci.out.println("Uninstallation cancelled");
/*     */             
/* 527 */             sem.release();
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void failed(PluginException e)
/*     */           {
/* 534 */             ci.out.println("Uninstallation failed: " + Debug.getNestedExceptionMessage(e));
/*     */             
/* 536 */             sem.release(); } }, properties);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 541 */         instance.addListener(new UpdateCheckInstanceListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void cancelled(UpdateCheckInstance instance)
/*     */           {
/*     */ 
/* 548 */             ci.out.println("InsUninstallationtallation cancelled");
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void complete(UpdateCheckInstance instance)
/*     */           {
/* 555 */             Update[] updates = instance.getUpdates();
/*     */             
/* 557 */             for (Update update : updates)
/*     */             {
/* 559 */               ResourceDownloader[] rds = update.getDownloaders();
/*     */               
/* 561 */               for (ResourceDownloader rd : rds) {
/*     */                 try
/*     */                 {
/* 564 */                   rd.download();
/*     */ 
/*     */                 }
/*     */                 catch (Throwable e) {}
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 572 */         });
/* 573 */         sem.reserve();
/*     */         
/* 575 */         Object obj = properties.get(Integer.valueOf(5));
/*     */         
/* 577 */         if (((obj instanceof Boolean)) && (((Boolean)obj).booleanValue()))
/*     */         {
/* 579 */           ci.out.println("**** Restart required to complete uninstallation ****");
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 583 */         ci.out.println("Uninstall failed: " + Debug.getNestedExceptionMessage(e));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Plugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */