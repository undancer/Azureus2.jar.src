/*      */ package org.gudy.azureus2.pluginsimpl.update;
/*      */ 
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import org.gudy.azureus2.core3.html.HTMLUtils;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.PluginConfig;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginException;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*      */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*      */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.components.UITextArea;
/*      */ import org.gudy.azureus2.plugins.ui.model.BasicPluginViewModel;
/*      */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*      */ import org.gudy.azureus2.plugins.update.Update;
/*      */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*      */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*      */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManagerListener;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoader;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderFactory;
/*      */ 
/*      */ public class PluginUpdatePlugin implements org.gudy.azureus2.plugins.Plugin
/*      */ {
/*      */   private static final String PLUGIN_CONFIGSECTION_ID = "plugins.update";
/*      */   private static final String PLUGIN_RESOURCE_ID = "ConfigView.section.plugins.update";
/*      */   public static final int RD_SIZE_RETRIES = 3;
/*      */   public static final int RD_SIZE_TIMEOUT = 10000;
/*      */   private PluginInterface plugin_interface;
/*      */   private LoggerChannel log;
/*      */   private boolean loader_listener_added;
/*   69 */   private String last_id_info = "";
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*   75 */     this.plugin_interface = _plugin_interface;
/*      */     
/*   77 */     this.plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*   78 */     this.plugin_interface.getPluginProperties().setProperty("plugin.name", "Plugin Updater");
/*      */     
/*   80 */     this.log = this.plugin_interface.getLogger().getChannel("Plugin Update");
/*      */     
/*   82 */     this.log.setDiagnostic();
/*      */     
/*   84 */     this.log.setForce(true);
/*      */     
/*   86 */     UIManager ui_manager = this.plugin_interface.getUIManager();
/*      */     
/*   88 */     final BasicPluginViewModel model = ui_manager.createBasicPluginViewModel("ConfigView.section.plugins.update");
/*      */     
/*      */ 
/*   91 */     final PluginConfig plugin_config = this.plugin_interface.getPluginconfig();
/*      */     
/*   93 */     boolean enabled = plugin_config.getPluginBooleanParameter("enable.update", true);
/*      */     
/*   95 */     model.setConfigSectionID("plugins.update");
/*   96 */     model.getStatus().setText(enabled ? "Running" : "Optional checks disabled");
/*   97 */     model.getActivity().setVisible(false);
/*   98 */     model.getProgress().setVisible(false);
/*      */     
/*  100 */     this.log.addListener(new LoggerChannelListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void messageLogged(int type, String message)
/*      */       {
/*      */ 
/*      */ 
/*  108 */         model.getLogArea().appendText(message + "\n");
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void messageLogged(String str, Throwable error)
/*      */       {
/*  116 */         model.getLogArea().appendText(error.toString() + "\n");
/*      */       }
/*      */       
/*  119 */     });
/*  120 */     org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel config = ui_manager.createBasicPluginConfigModel("plugins", "plugins.update");
/*      */     
/*  122 */     config.addBooleanParameter2("enable.update", "Plugin.pluginupdate.enablecheck", true);
/*      */     
/*  124 */     this.plugin_interface.addEventListener(new org.gudy.azureus2.plugins.PluginEventListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void handleEvent(PluginEvent ev)
/*      */       {
/*      */ 
/*  131 */         if (ev.getType() == 7)
/*      */         {
/*  133 */           PluginUpdatePlugin.this.plugin_interface.removeEventListener(this);
/*      */           
/*  135 */           PluginUpdatePlugin.this.initComplete(plugin_config);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void initComplete(final PluginConfig plugin_config)
/*      */   {
/*  145 */     UpdateManager update_manager = this.plugin_interface.getUpdateManager();
/*      */     
/*  147 */     update_manager.addListener(new UpdateManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void checkInstanceCreated(UpdateCheckInstance inst)
/*      */       {
/*      */ 
/*  154 */         SFPluginDetailsLoaderFactory.getSingleton().reset();
/*      */       }
/*      */       
/*      */ 
/*  158 */     });
/*  159 */     final PluginManager plugin_manager = this.plugin_interface.getPluginManager();
/*      */     
/*  161 */     PluginInterface[] plugins = plugin_manager.getPlugins();
/*      */     
/*  163 */     int mandatory_count = 0;
/*  164 */     int non_mandatory_count = 0;
/*      */     
/*  166 */     for (int i = 0; i < plugins.length; i++)
/*      */     {
/*  168 */       PluginInterface pi = plugins[i];
/*      */       
/*  170 */       boolean pi_mandatory = pi.getPluginState().isMandatory();
/*      */       
/*  172 */       if (pi_mandatory)
/*      */       {
/*  174 */         mandatory_count++;
/*      */       }
/*      */       else
/*      */       {
/*  178 */         non_mandatory_count++;
/*      */       }
/*      */     }
/*      */     
/*  182 */     final int f_non_mandatory_count = non_mandatory_count;
/*  183 */     final int f_mandatory_count = mandatory_count;
/*      */     
/*  185 */     update_manager.registerUpdatableComponent(new UpdatableComponent()
/*      */     {
/*      */ 
/*      */       public String getName()
/*      */       {
/*      */ 
/*  191 */         return "Non-mandatory plugins";
/*      */       }
/*      */       
/*      */ 
/*      */       public int getMaximumCheckTime()
/*      */       {
/*  197 */         return f_non_mandatory_count * 30;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void checkForUpdate(UpdateChecker checker)
/*      */       {
/*  204 */         if (PluginUpdatePlugin.this.checkForUpdateSupport(checker, null, false) == 0)
/*      */         {
/*  206 */           VersionCheckClient vc = VersionCheckClient.getSingleton();
/*      */           
/*  208 */           String[] rps = vc.getRecommendedPlugins();
/*      */           
/*  210 */           boolean found_one = false;
/*      */           
/*  212 */           for (int i = 0; i < rps.length; i++)
/*      */           {
/*  214 */             String rp_id = rps[i];
/*      */             
/*  216 */             if (plugin_manager.getPluginInterfaceByID(rp_id, false) == null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  223 */               final String config_key = "recommended.processed." + rp_id;
/*      */               
/*  225 */               if (!plugin_config.getPluginBooleanParameter(config_key, false)) {
/*      */                 try
/*      */                 {
/*  228 */                   final PluginInstaller installer = PluginUpdatePlugin.this.plugin_interface.getPluginManager().getPluginInstaller();
/*      */                   
/*  230 */                   StandardPlugin[] sps = installer.getStandardPlugins();
/*      */                   
/*  232 */                   for (int j = 0; j < sps.length; j++)
/*      */                   {
/*  234 */                     final StandardPlugin sp = sps[j];
/*      */                     
/*  236 */                     if (sp.getId().equals(rp_id))
/*      */                     {
/*  238 */                       found_one = true;
/*      */                       
/*  240 */                       checker.getCheckInstance().addListener(new org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener()
/*      */                       {
/*      */                         public void cancelled(UpdateCheckInstance instance) {}
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void complete(UpdateCheckInstance instance)
/*      */                         {
/*  253 */                           if (instance.getUpdates().length == 0)
/*      */                           {
/*  255 */                             PluginUpdatePlugin.this.installRecommendedPlugin(installer, sp);
/*      */                             
/*  257 */                             PluginUpdatePlugin.4.this.val$plugin_config.setPluginParameter(config_key, true);
/*      */                           }
/*      */                           
/*      */                         }
/*  261 */                       });
/*  262 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*  271 */               if (found_one) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  277 */           if (!found_one)
/*      */           {
/*  279 */             java.util.Set<String> auto_install = vc.getAutoInstallPluginIDs();
/*      */             
/*  281 */             final List<String> to_do = new ArrayList();
/*      */             
/*  283 */             for (String pid : auto_install)
/*      */             {
/*  285 */               if (plugin_manager.getPluginInterfaceByID(pid, false) == null)
/*      */               {
/*  287 */                 to_do.add(pid);
/*      */               }
/*      */             }
/*      */             
/*  291 */             if (to_do.size() > 0)
/*      */             {
/*  293 */               new AEThread2("pup:autoinst")
/*      */               {
/*      */                 public void run()
/*      */                 {
/*      */                   try
/*      */                   {
/*  299 */                     Thread.sleep(120000L);
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*  303 */                     Debug.out(e);
/*      */                     
/*  305 */                     return;
/*      */                   }
/*      */                   
/*  308 */                   UpdateManager update_manager = PluginUpdatePlugin.this.plugin_interface.getUpdateManager();
/*      */                   
/*  310 */                   final List<UpdateCheckInstance> l_instances = new ArrayList();
/*      */                   
/*  312 */                   update_manager.addListener(new UpdateManagerListener()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void checkInstanceCreated(UpdateCheckInstance instance)
/*      */                     {
/*      */ 
/*  319 */                       synchronized (l_instances)
/*      */                       {
/*  321 */                         l_instances.add(instance);
/*      */                       }
/*      */                       
/*      */                     }
/*  325 */                   });
/*  326 */                   UpdateCheckInstance[] instances = update_manager.getCheckInstances();
/*      */                   
/*  328 */                   l_instances.addAll(java.util.Arrays.asList(instances));
/*      */                   
/*  330 */                   long start = SystemTime.getMonotonousTime();
/*      */                   
/*      */ 
/*      */ 
/*  334 */                   while (SystemTime.getMonotonousTime() - start < 300000L)
/*      */                   {
/*      */ 
/*      */                     try
/*      */                     {
/*      */ 
/*  340 */                       Thread.sleep(5000L);
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/*  344 */                       Debug.out(e);
/*      */                       
/*  346 */                       return;
/*      */                     }
/*      */                     
/*  349 */                     if (l_instances.size() > 0)
/*      */                     {
/*  351 */                       boolean all_done = true;
/*      */                       
/*  353 */                       for (UpdateCheckInstance instance : l_instances)
/*      */                       {
/*  355 */                         if (!instance.isCompleteOrCancelled())
/*      */                         {
/*  357 */                           all_done = false;
/*      */                           
/*  359 */                           break;
/*      */                         }
/*      */                       }
/*      */                       
/*  363 */                       if (all_done) {
/*      */                         break;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*  370 */                   if (update_manager.getInstallers().length > 0)
/*      */                   {
/*  372 */                     return;
/*      */                   }
/*      */                   
/*  375 */                   PluginInstaller installer = PluginUpdatePlugin.this.plugin_interface.getPluginManager().getPluginInstaller();
/*      */                   
/*  377 */                   List<InstallablePlugin> sps = new ArrayList();
/*      */                   
/*  379 */                   for (String pid : to_do) {
/*      */                     try
/*      */                     {
/*  382 */                       StandardPlugin sp = installer.getStandardPlugin(pid);
/*      */                       
/*  384 */                       if (sp != null)
/*      */                       {
/*  386 */                         PluginUpdatePlugin.this.log.log("Auto-installing " + pid);
/*      */                         
/*  388 */                         sps.add(sp);
/*      */                       }
/*      */                       else
/*      */                       {
/*  392 */                         PluginUpdatePlugin.this.log.log("Standard plugin '" + pid + "' missing");
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {
/*  396 */                       PluginUpdatePlugin.this.log.log("Standard plugin '" + pid + "' missing", e);
/*      */                     }
/*      */                   }
/*      */                   
/*  400 */                   if (sps.size() > 0)
/*      */                   {
/*  402 */                     Map<Integer, Object> properties = new HashMap();
/*      */                     
/*  404 */                     properties.put(Integer.valueOf(1), Integer.valueOf(3));
/*      */                     
/*  406 */                     properties.put(Integer.valueOf(3), Boolean.valueOf(true));
/*      */                     try
/*      */                     {
/*  409 */                       installer.install((InstallablePlugin[])sps.toArray(new InstallablePlugin[sps.size()]), false, properties, new org.gudy.azureus2.plugins.installer.PluginInstallationListener()
/*      */                       {
/*      */                         public void completed() {}
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void cancelled() {}
/*      */                         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void failed(PluginException e) {}
/*      */                       });
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/*  435 */                       PluginUpdatePlugin.this.log.log("Auto install failed", e);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }.start();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  293 */     }, false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  447 */     update_manager.registerUpdatableComponent(new UpdatableComponent()
/*      */     {
/*      */ 
/*      */       public String getName()
/*      */       {
/*      */ 
/*  453 */         return "Mandatory plugins";
/*      */       }
/*      */       
/*      */ 
/*      */       public int getMaximumCheckTime()
/*      */       {
/*  459 */         return f_mandatory_count * 30;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  466 */       public void checkForUpdate(UpdateChecker checker) { PluginUpdatePlugin.this.checkForUpdateSupport(checker, null, true); } }, true);
/*      */     
/*      */ 
/*      */ 
/*  470 */     update_manager.addListener(new UpdateManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void checkInstanceCreated(UpdateCheckInstance instance)
/*      */       {
/*      */ 
/*  477 */         PluginUpdatePlugin.this.log.log(1, "**** Update check starts ****");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void installRecommendedPlugin(PluginInstaller installer, StandardPlugin plugin)
/*      */   {
/*      */     try
/*      */     {
/*  488 */       installer.requestInstall(MessageText.getString("plugin.installer.recommended.plugin"), plugin);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  492 */       this.log.log(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public UpdatableComponent getCustomUpdateableComponent(final String id, final boolean mandatory)
/*      */   {
/*  501 */     new UpdatableComponent()
/*      */     {
/*      */ 
/*      */       public String getName()
/*      */       {
/*      */ 
/*  507 */         return "Installation of '" + id + "'";
/*      */       }
/*      */       
/*      */ 
/*      */       public int getMaximumCheckTime()
/*      */       {
/*  513 */         return 30;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void checkForUpdate(UpdateChecker checker)
/*      */       {
/*  520 */         PluginUpdatePlugin.this.checkForUpdateSupport(checker, new String[] { id }, mandatory);
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int checkForUpdateSupport(UpdateChecker checker, String[] ids_to_check, boolean mandatory)
/*      */   {
/*  531 */     int num_updates_found = 0;
/*      */     try
/*      */     {
/*  534 */       if ((!mandatory) && (ids_to_check == null) && (!this.plugin_interface.getPluginconfig().getPluginBooleanParameter("enable.update", true)))
/*      */       {
/*      */ 
/*      */ 
/*  538 */         return num_updates_found;
/*      */       }
/*      */       
/*  541 */       PluginInterface[] plugins = this.plugin_interface.getPluginManager().getPlugins();
/*      */       
/*  543 */       List plugins_to_check = new ArrayList();
/*  544 */       List plugins_to_check_ids = new ArrayList();
/*  545 */       Map plugins_to_check_names = new HashMap();
/*      */       
/*  547 */       for (int i = 0; i < plugins.length; i++)
/*      */       {
/*  549 */         PluginInterface pi = plugins[i];
/*      */         
/*  551 */         if ((!pi.getPluginState().isDisabled()) || 
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  556 */           (pi.getPluginState().hasFailed()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  562 */           String mand = pi.getPluginProperties().getProperty("plugin.mandatory");
/*      */           
/*  564 */           boolean pi_mandatory = (mand != null) && (mand.trim().toLowerCase().equals("true"));
/*      */           
/*  566 */           if (pi_mandatory == mandatory)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  571 */             String id = pi.getPluginID();
/*  572 */             String version = pi.getPluginVersion();
/*  573 */             String name = pi.getPluginName();
/*      */             
/*  575 */             if (ids_to_check != null)
/*      */             {
/*  577 */               boolean id_selected = false;
/*      */               
/*  579 */               for (int j = 0; j < ids_to_check.length; j++)
/*      */               {
/*  581 */                 if (ids_to_check[j].equals(id))
/*      */                 {
/*  583 */                   id_selected = true;
/*      */                   
/*  585 */                   break;
/*      */                 }
/*      */               }
/*      */               
/*  589 */               if (!id_selected) {}
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  595 */               if (version != null)
/*      */               {
/*  597 */                 if (plugins_to_check_ids.contains(id))
/*      */                 {
/*  599 */                   String s = (String)plugins_to_check_names.get(id);
/*      */                   
/*  601 */                   if (!name.equals(id))
/*      */                   {
/*  603 */                     plugins_to_check_names.put(id, s + "," + name);
/*      */                   }
/*      */                 } else {
/*  606 */                   plugins_to_check_ids.add(id);
/*      */                   
/*  608 */                   plugins_to_check.add(pi);
/*      */                   
/*  610 */                   plugins_to_check_names.put(id, name.equals(id) ? "" : name);
/*      */                 }
/*      */               }
/*      */               
/*  614 */               String location = pi.getPluginDirectoryName();
/*      */               
/*  616 */               this.log.log(1, (mandatory ? "*" : "-") + pi.getPluginName() + ", id = " + id + (version == null ? "" : new StringBuilder().append(", version = ").append(pi.getPluginVersion()).toString()) + (location == null ? "" : new StringBuilder().append(", loc = ").append(location).toString()));
/*      */             }
/*      */           } } }
/*  619 */       SFPluginDetailsLoader loader = SFPluginDetailsLoaderFactory.getSingleton();
/*      */       
/*  621 */       if (!this.loader_listener_added)
/*      */       {
/*  623 */         this.loader_listener_added = true;
/*      */         
/*  625 */         loader.addListener(new org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void log(String str)
/*      */           {
/*      */ 
/*  632 */             PluginUpdatePlugin.this.log.log(1, "[" + str + "]");
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*  638 */       String[] ids = loader.getPluginIDs();
/*      */       
/*  640 */       String id_info = "";
/*      */       
/*  642 */       for (int i = 0; i < ids.length; i++)
/*      */       {
/*  644 */         String id = ids[i];
/*      */         
/*  646 */         SFPluginDetails details = loader.getPluginDetails(id);
/*      */         
/*  648 */         id_info = id_info + (i == 0 ? "" : ",") + ids[i] + "=" + details.getVersion() + "/" + details.getCVSVersion();
/*      */       }
/*      */       
/*  651 */       if (!id_info.equals(this.last_id_info))
/*      */       {
/*  653 */         this.last_id_info = id_info;
/*      */         
/*  655 */         this.log.log(1, "Downloaded plugin info = " + id_info);
/*      */       }
/*      */       
/*  658 */       for (int i = 0; i < plugins_to_check.size(); i++)
/*      */       {
/*  660 */         if (checker.getCheckInstance().isCancelled())
/*      */         {
/*  662 */           throw new Exception("Update check cancelled");
/*      */         }
/*      */         
/*  665 */         PluginInterface pi_being_checked = (PluginInterface)plugins_to_check.get(i);
/*  666 */         String plugin_id = pi_being_checked.getPluginID();
/*      */         
/*  668 */         boolean found = false;
/*      */         
/*  670 */         for (int j = 0; j < ids.length; j++)
/*      */         {
/*  672 */           if (ids[j].equalsIgnoreCase(plugin_id))
/*      */           {
/*  674 */             found = true;
/*      */             
/*  676 */             break;
/*      */           }
/*      */         }
/*      */         
/*  680 */         if (!found)
/*      */         {
/*  682 */           if (!pi_being_checked.getPluginState().isBuiltIn())
/*      */           {
/*  684 */             this.log.log(1, "Skipping " + plugin_id + " as not listed on web site");
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/*  690 */           String plugin_names = (String)plugins_to_check_names.get(plugin_id);
/*      */           
/*      */ 
/*  693 */           this.log.log(1, "Checking " + plugin_id);
/*      */           try
/*      */           {
/*  696 */             checker.reportProgress("Loading details for " + plugin_id + "/" + pi_being_checked.getPluginName());
/*      */             
/*  698 */             SFPluginDetails details = loader.getPluginDetails(plugin_id);
/*      */             
/*  700 */             if (plugin_names.length() == 0)
/*      */             {
/*  702 */               plugin_names = details.getName();
/*      */             }
/*      */             
/*  705 */             boolean az_cvs = this.plugin_interface.getUtilities().isCVSVersion();
/*      */             
/*  707 */             String pi_version_info = pi_being_checked.getPluginProperties().getProperty("plugin.version.info");
/*      */             
/*  709 */             String az_plugin_version = pi_being_checked.getPluginVersion();
/*      */             
/*  711 */             String sf_plugin_version = details.getVersion();
/*      */             
/*  713 */             String sf_comp_version = sf_plugin_version;
/*      */             
/*  715 */             if (az_cvs)
/*      */             {
/*  717 */               String sf_cvs_version = details.getCVSVersion();
/*      */               
/*  719 */               if (sf_cvs_version.length() > 0)
/*      */               {
/*      */ 
/*      */ 
/*  723 */                 sf_plugin_version = sf_cvs_version;
/*      */                 
/*  725 */                 sf_comp_version = sf_plugin_version.substring(0, sf_plugin_version.length() - 4);
/*      */               }
/*      */             }
/*      */             
/*  729 */             if ((sf_comp_version.length() == 0) || (!Character.isDigit(sf_comp_version.charAt(0))))
/*      */             {
/*      */ 
/*  732 */               this.log.log(1, "Skipping " + plugin_id + " as no valid version to check");
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  739 */               int comp = org.gudy.azureus2.pluginsimpl.PluginUtils.comparePluginVersions(az_plugin_version, sf_comp_version);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  744 */               this.log.log(1, "    Current: " + az_plugin_version + ((comp == 0) && (sf_plugin_version.endsWith("_CVS")) ? "_CVS" : "") + ", Latest: " + sf_plugin_version + (pi_version_info == null ? "" : new StringBuilder().append(" [").append(pi_version_info).append("]").toString()));
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  749 */               checker.reportProgress("    current=" + az_plugin_version + ((comp == 0) && (sf_plugin_version.endsWith("_CVS")) ? "_CVS" : "") + ", latest=" + sf_plugin_version);
/*      */               
/*  751 */               if ((comp < 0) && (!(pi_being_checked.getPlugin() instanceof UpdatableComponent)))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  756 */                 String sf_plugin_download = details.getDownloadURL();
/*      */                 
/*  758 */                 if (az_cvs)
/*      */                 {
/*  760 */                   String sf_cvs_version = details.getCVSVersion();
/*      */                   
/*  762 */                   if (sf_cvs_version.length() > 0)
/*      */                   {
/*  764 */                     sf_plugin_download = details.getCVSDownloadURL();
/*      */                   }
/*      */                 }
/*      */                 
/*  768 */                 this.log.log(1, "    Description:");
/*      */                 
/*  770 */                 List update_desc = new ArrayList();
/*      */                 
/*  772 */                 List desc_lines = HTMLUtils.convertHTMLToText("", details.getDescription());
/*      */                 
/*  774 */                 logMultiLine("        ", desc_lines);
/*      */                 
/*  776 */                 update_desc.addAll(desc_lines);
/*      */                 
/*  778 */                 this.log.log(1, "    Comment:");
/*      */                 
/*  780 */                 List comment_lines = HTMLUtils.convertHTMLToText("    ", details.getComment());
/*      */                 
/*  782 */                 logMultiLine("    ", comment_lines);
/*      */                 
/*  784 */                 update_desc.addAll(comment_lines);
/*      */                 
/*  786 */                 String msg = "A newer version (version " + sf_plugin_version + ") of plugin '" + plugin_id + "' " + (plugin_names.length() == 0 ? "" : new StringBuilder().append("(").append(plugin_names).append(") ").toString()) + "is available. ";
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*  791 */                 this.log.log(1, "");
/*      */                 
/*  793 */                 this.log.log(1, "        " + msg + "Download from " + sf_plugin_download);
/*      */                 
/*      */ 
/*  796 */                 ResourceDownloaderFactory rdf = this.plugin_interface.getUtilities().getResourceDownloaderFactory();
/*      */                 
/*  798 */                 ResourceDownloader direct_rdl = rdf.create(new URL(sf_plugin_download));
/*  799 */                 ResourceDownloader direct_ap_rdl = rdf.createWithAutoPluginProxy(new URL(sf_plugin_download));
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  805 */                 String torrent_download = "http://cf1.vuze.com/torrent/torrents/";
/*      */                 
/*  807 */                 int slash_pos = sf_plugin_download.lastIndexOf("/");
/*      */                 
/*  809 */                 if (slash_pos == -1)
/*      */                 {
/*  811 */                   torrent_download = torrent_download + sf_plugin_download;
/*      */                 }
/*      */                 else
/*      */                 {
/*  815 */                   torrent_download = torrent_download + sf_plugin_download.substring(slash_pos + 1);
/*      */                 }
/*      */                 
/*  818 */                 torrent_download = torrent_download + ".torrent";
/*      */                 
/*  820 */                 ResourceDownloader torrent_rdl = rdf.create(new URL(torrent_download));
/*      */                 
/*      */ 
/*  823 */                 torrent_rdl = rdf.getSuffixBasedDownloader(torrent_rdl);
/*      */                 
/*      */ 
/*      */ 
/*  827 */                 ResourceDownloader alternate_rdl = rdf.getAlternateDownloader(new ResourceDownloader[] { torrent_rdl, direct_rdl, direct_ap_rdl });
/*      */                 
/*      */ 
/*      */ 
/*  831 */                 rdf.getTimeoutDownloader(rdf.getRetryDownloader(alternate_rdl, 3), 10000).getSize();
/*      */                 
/*  833 */                 String[] update_d = new String[update_desc.size()];
/*      */                 
/*  835 */                 update_desc.toArray(update_d);
/*      */                 
/*  837 */                 num_updates_found++;
/*      */                 
/*      */ 
/*      */ 
/*  841 */                 boolean plugin_unloadable = true;
/*      */                 
/*  843 */                 for (int j = 0; j < plugins.length; j++)
/*      */                 {
/*  845 */                   PluginInterface pi = plugins[j];
/*      */                   
/*  847 */                   if (pi.getPluginID().equals(plugin_id))
/*      */                   {
/*  849 */                     plugin_unloadable &= pi.getPluginState().isUnloadable();
/*      */                   }
/*      */                 }
/*      */                 
/*  853 */                 if (plugin_unloadable)
/*      */                 {
/*  855 */                   checker.reportProgress("Plugin is unloadable");
/*      */                 }
/*      */                 
/*  858 */                 Update update = addUpdate(pi_being_checked, checker, plugin_id + "/" + plugin_names, update_d, az_plugin_version, sf_plugin_version, alternate_rdl, sf_plugin_download.toLowerCase().endsWith(".jar"), plugin_unloadable ? 1 : 2, true);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  871 */                 update.setRelativeURLBase(details.getRelativeURLBase());
/*  872 */                 update.setDescriptionURL(details.getInfoURL());
/*      */               }
/*      */             }
/*      */           } catch (Throwable e) {
/*  876 */             checker.reportProgress("Failed to load details for plugin '" + plugin_id + "': " + Debug.getNestedExceptionMessage(e));
/*      */             
/*  878 */             this.log.log("    Plugin check failed", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/*  883 */       if (!"Update check cancelled".equals(e.getMessage())) {
/*  884 */         this.log.log("Failed to load plugin details", e);
/*      */       }
/*      */       
/*  887 */       checker.reportProgress("Failed to load plugin details: " + Debug.getNestedExceptionMessage(e));
/*      */       
/*  889 */       checker.failed();
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*  895 */       checker.completed();
/*      */     }
/*      */     
/*  898 */     return num_updates_found;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Update addUpdate(final PluginInterface pi_for_update, final UpdateChecker checker, String update_name, String[] update_details, final String old_version, final String new_version, ResourceDownloader resource_downloader, final boolean is_jar, final int restart_type, final boolean verify)
/*      */   {
/*  914 */     final Update update = checker.addUpdate(update_name, update_details, old_version, new_version, resource_downloader, restart_type);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  922 */     update.setUserObject(pi_for_update);
/*      */     
/*  924 */     resource_downloader.addListener(new org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public boolean completed(final ResourceDownloader downloader, InputStream data)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  935 */         LoggerChannelListener list = new LoggerChannelListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void messageLogged(int type, String content)
/*      */           {
/*      */ 
/*      */ 
/*  943 */             downloader.reportActivity(content);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void messageLogged(String str, Throwable error)
/*      */           {
/*  951 */             downloader.reportActivity(str);
/*      */           }
/*      */         };
/*      */         
/*      */         try
/*      */         {
/*  957 */           PluginUpdatePlugin.this.log.addListener(list);
/*      */           
/*  959 */           PluginUpdatePlugin.this.installUpdate(checker, update, pi_for_update, restart_type == 1, is_jar, old_version, new_version, data, verify);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  970 */           return true;
/*      */         }
/*      */         finally {
/*  973 */           PluginUpdatePlugin.this.log.removeListener(list);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*      */       {
/*  982 */         if (!downloader.isCancelled())
/*      */         {
/*  984 */           Debug.out(downloader.getName() + " failed", e);
/*      */         }
/*      */         
/*  987 */         update.complete(false);
/*      */       }
/*      */       
/*  990 */     });
/*  991 */     return update;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void installUpdate(UpdateChecker checker, Update update, PluginInterface plugin, boolean unloadable, boolean is_jar, String old_version, String new_version, InputStream data, boolean verify)
/*      */   {
/* 1007 */     this.log.log(1, "Installing plugin '" + update.getName() + "', version " + new_version);
/*      */     
/*      */ 
/* 1010 */     String target_version = new_version.endsWith("_CVS") ? new_version.substring(0, new_version.length() - 4) : new_version;
/*      */     
/* 1012 */     UpdateInstaller installer = null;
/*      */     
/* 1014 */     boolean update_successful = false;
/*      */     
/*      */     try
/*      */     {
/* 1018 */       data = update.verifyData(data, verify);
/*      */       
/* 1020 */       this.log.log("    Data verification stage complete");
/*      */       
/* 1022 */       boolean update_txt_found = false;
/*      */       
/* 1024 */       String plugin_dir_name = plugin.getPluginDirectoryName();
/*      */       
/* 1026 */       if ((plugin_dir_name == null) || (plugin_dir_name.length() == 0))
/*      */       {
/*      */ 
/*      */ 
/* 1030 */         this.log.log(1, "    This is a built-in plugin, updating core");
/*      */         
/* 1032 */         org.gudy.azureus2.update.CorePatchChecker.patchAzureus2(update.getCheckInstance(), data, plugin.getPluginID() + "_" + new_version, this.log);
/*      */         
/*      */ 
/*      */ 
/* 1036 */         update.setRestartRequired(2);
/*      */       }
/*      */       else
/*      */       {
/* 1040 */         File plugin_dir = new File(plugin_dir_name);
/* 1041 */         File user_dir = new File(this.plugin_interface.getUtilities().getAzureusUserDir());
/* 1042 */         File prog_dir = new File(this.plugin_interface.getUtilities().getAzureusProgramDir());
/*      */         
/* 1044 */         Map<String, List<String[]>> install_properties = new HashMap();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1053 */         boolean force_indirect_install = false;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1059 */         if (Constants.isWindowsVistaOrHigher)
/*      */         {
/*      */ 
/*      */ 
/* 1063 */           File test_file = new File(plugin_dir, "_aztest45.dll");
/*      */           
/* 1065 */           boolean ok = false;
/*      */           try
/*      */           {
/* 1068 */             if (test_file.exists())
/*      */             {
/* 1070 */               test_file.delete();
/*      */             }
/*      */             
/* 1073 */             FileOutputStream os = new FileOutputStream(test_file);
/*      */             try
/*      */             {
/* 1076 */               os.write(32);
/*      */             }
/*      */             finally
/*      */             {
/* 1080 */               os.close();
/*      */             }
/*      */             
/* 1083 */             ok = test_file.delete();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/* 1087 */           if (!ok)
/*      */           {
/* 1089 */             this.log.log("Can't write directly to the plugin directroy, installing indirectly");
/*      */             
/* 1091 */             force_indirect_install = true;
/*      */           }
/*      */         }
/*      */         
/*      */         File target_plugin_dir;
/*      */         
/*      */         File target_user_dir;
/*      */         File target_prog_dir;
/* 1099 */         if (force_indirect_install)
/*      */         {
/* 1101 */           File temp_dir = org.gudy.azureus2.core3.util.AETemporaryFileHandler.createTempDir();
/*      */           
/* 1103 */           File target_plugin_dir = new File(temp_dir, "plugin");
/* 1104 */           File target_user_dir = new File(temp_dir, "user");
/* 1105 */           File target_prog_dir = new File(temp_dir, "prog");
/*      */           
/* 1107 */           target_plugin_dir.mkdirs();
/* 1108 */           target_user_dir.mkdirs();
/* 1109 */           target_prog_dir.mkdirs();
/*      */           
/* 1111 */           installer = update.getCheckInstance().createInstaller();
/*      */           
/* 1113 */           update.setRestartRequired(2);
/*      */         }
/*      */         else
/*      */         {
/* 1117 */           target_plugin_dir = plugin_dir;
/* 1118 */           target_user_dir = user_dir;
/* 1119 */           target_prog_dir = prog_dir;
/*      */         }
/*      */         
/* 1122 */         File target_jar_zip = new File(target_plugin_dir, plugin.getPluginID() + "_" + target_version + (is_jar ? ".jar" : ".zip"));
/*      */         
/*      */ 
/* 1125 */         FileUtil.copyFile(data, new FileOutputStream(target_jar_zip));
/*      */         
/* 1127 */         if (!is_jar)
/*      */         {
/* 1129 */           ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(target_jar_zip)));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1136 */           String common_prefix = null;
/*      */           
/* 1138 */           String selected_platform = null;
/* 1139 */           List selected_sub_platforms = new ArrayList();
/*      */           try
/*      */           {
/*      */             for (;;)
/*      */             {
/* 1144 */               ZipEntry entry = zis.getNextEntry();
/*      */               
/* 1146 */               if (entry == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/* 1151 */               String name = entry.getName();
/*      */               
/*      */ 
/* 1154 */               if (name.equals("plugin_install.properties"))
/*      */               {
/* 1156 */                 ByteArrayOutputStream baos = new ByteArrayOutputStream(32768);
/*      */                 
/* 1158 */                 byte[] buffer = new byte[65536];
/*      */                 
/*      */                 for (;;)
/*      */                 {
/* 1162 */                   int len = zis.read(buffer);
/*      */                   
/* 1164 */                   if (len <= 0) {
/*      */                     break;
/*      */                   }
/*      */                   
/*      */ 
/* 1169 */                   baos.write(buffer, 0, len);
/*      */                 }
/*      */                 try
/*      */                 {
/* 1173 */                   LineNumberReader lnr = new LineNumberReader(new java.io.InputStreamReader(new java.io.ByteArrayInputStream(baos.toByteArray()), "UTF-8"));
/*      */                   
/*      */                   for (;;)
/*      */                   {
/* 1177 */                     String line = lnr.readLine();
/*      */                     
/* 1179 */                     if (line == null) {
/*      */                       break;
/*      */                     }
/*      */                     
/*      */ 
/* 1184 */                     line = line.trim();
/*      */                     
/* 1186 */                     if (line.endsWith("defer_install"))
/*      */                     {
/* 1188 */                       force_indirect_install = true;
/*      */                     }
/*      */                     else
/*      */                     {
/* 1192 */                       String[] command = line.split(",");
/*      */                       
/* 1194 */                       if (command.length > 1)
/*      */                       {
/* 1196 */                         List<String[]> commands = (List)install_properties.get(command[0]);
/*      */                         
/* 1198 */                         if (commands == null)
/*      */                         {
/* 1200 */                           commands = new ArrayList();
/*      */                           
/* 1202 */                           install_properties.put(command[0], commands);
/*      */                         }
/*      */                         
/* 1205 */                         commands.add(command);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/* 1211 */                   Debug.out(e);
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 1216 */                 if ((!name.equals("azureus.sig")) && (!name.endsWith("/")))
/*      */                 {
/* 1218 */                   if (common_prefix == null)
/*      */                   {
/* 1220 */                     common_prefix = name;
/*      */                   }
/*      */                   else {
/* 1223 */                     int len = 0;
/*      */                     
/* 1225 */                     for (int i = 0; i < Math.min(common_prefix.length(), name.length()); i++)
/*      */                     {
/* 1227 */                       if (common_prefix.charAt(i) != name.charAt(i))
/*      */                         break;
/* 1229 */                       len++;
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1237 */                     common_prefix = common_prefix.substring(0, len);
/*      */                   }
/*      */                   
/* 1240 */                   int plat_pos = name.indexOf("platform/");
/*      */                   
/* 1242 */                   if (plat_pos != -1)
/*      */                   {
/* 1244 */                     plat_pos += 9;
/*      */                     
/* 1246 */                     int plat_end_pos = name.indexOf("/", plat_pos);
/*      */                     
/* 1248 */                     if (plat_end_pos != -1)
/*      */                     {
/* 1250 */                       String platform = name.substring(plat_pos, plat_end_pos);
/* 1251 */                       String sub_platform = null;
/*      */                       
/* 1253 */                       int sub_plat_pos = platform.indexOf("_");
/*      */                       
/* 1255 */                       if (sub_plat_pos != -1)
/*      */                       {
/* 1257 */                         sub_platform = platform.substring(sub_plat_pos + 1);
/*      */                         
/* 1259 */                         platform = platform.substring(0, sub_plat_pos);
/*      */                       }
/*      */                       
/* 1262 */                       if (((Constants.isWindows) && (platform.equalsIgnoreCase("windows"))) || ((Constants.isLinux) && (platform.equalsIgnoreCase("linux"))) || ((Constants.isUnix) && (platform.equalsIgnoreCase("unix"))) || ((Constants.isFreeBSD) && (platform.equalsIgnoreCase("freebsd"))) || ((Constants.isSolaris) && (platform.equalsIgnoreCase("solaris"))) || ((Constants.isOSX) && (platform.equalsIgnoreCase("osx"))))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1270 */                         selected_platform = platform;
/*      */                         
/* 1272 */                         if (sub_platform != null)
/*      */                         {
/* 1274 */                           if (!selected_sub_platforms.contains(sub_platform))
/*      */                           {
/* 1276 */                             selected_sub_platforms.add(sub_platform);
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/* 1284 */                 byte[] buffer = new byte[65536];
/*      */                 
/*      */                 for (;;)
/*      */                 {
/* 1288 */                   int len = zis.read(buffer);
/*      */                   
/* 1290 */                   if (len <= 0) {
/*      */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 1298 */             zis.close();
/*      */           }
/*      */           
/* 1301 */           if (selected_platform != null)
/*      */           {
/* 1303 */             String[] options = new String[selected_sub_platforms.size()];
/*      */             
/* 1305 */             selected_sub_platforms.toArray(options);
/*      */             
/* 1307 */             if (options.length == 1)
/*      */             {
/* 1309 */               selected_platform = selected_platform + "_" + options[0];
/*      */               
/* 1311 */               this.log.log(1, "platform is '" + selected_platform + "'");
/*      */ 
/*      */             }
/* 1314 */             else if (options.length > 1)
/*      */             {
/* 1316 */               String selected_sub_platform = (String)update.getDecision(0, "Select Platform", "Multiple platform options exist for this plugin, please select required one", options);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1323 */               if (selected_sub_platform == null)
/*      */               {
/* 1325 */                 throw new Exception("Valid sub-platform selection not selected");
/*      */               }
/*      */               
/*      */ 
/* 1329 */               selected_platform = selected_platform + "_" + selected_sub_platform;
/*      */               
/* 1331 */               this.log.log(1, "platform is '" + selected_platform + "'");
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1337 */           if (common_prefix != null)
/*      */           {
/* 1339 */             int pos = common_prefix.lastIndexOf("/");
/*      */             
/* 1341 */             if (pos == -1)
/*      */             {
/* 1343 */               common_prefix = "";
/*      */             }
/*      */             else {
/* 1346 */               common_prefix = common_prefix.substring(0, pos + 1);
/*      */             }
/*      */             
/* 1349 */             zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(target_jar_zip)));
/*      */             
/*      */             try
/*      */             {
/*      */               for (;;)
/*      */               {
/* 1355 */                 ZipEntry entry = zis.getNextEntry();
/*      */                 
/* 1357 */                 if (entry == null) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 1362 */                 String name = entry.getName();
/*      */                 
/* 1364 */                 OutputStream entry_os = null;
/*      */                 
/* 1366 */                 File origin = null;
/* 1367 */                 File initial_target = null;
/* 1368 */                 File final_target = null;
/* 1369 */                 boolean is_plugin_properties = false;
/*      */                 try
/*      */                 {
/* 1372 */                   if ((name.length() >= common_prefix.length()) && (!name.equals("azureus.sig")) && (!name.endsWith("/")))
/*      */                   {
/*      */ 
/* 1375 */                     boolean skip_file = false;
/*      */                     
/* 1377 */                     String file_name = entry.getName().substring(common_prefix.length());
/*      */                     
/* 1379 */                     if (selected_platform != null)
/*      */                     {
/* 1381 */                       if (file_name.contains("platform/"))
/*      */                       {
/* 1383 */                         String bit_to_remove = "platform/" + selected_platform;
/*      */                         
/* 1385 */                         int pp = file_name.indexOf(bit_to_remove);
/*      */                         
/* 1387 */                         if (pp != -1)
/*      */                         {
/* 1389 */                           file_name = file_name.substring(0, pp) + file_name.substring(pp + bit_to_remove.length() + 1);
/*      */ 
/*      */                         }
/*      */                         else
/*      */                         {
/*      */ 
/* 1395 */                           skip_file = true;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/*      */                     File install_root;
/*      */                     File origin_root;
/*      */                     File install_root;
/* 1403 */                     if (file_name.startsWith("shared/lib"))
/*      */                     {
/*      */ 
/*      */ 
/* 1407 */                       update.setRestartRequired(2);
/*      */                       
/*      */ 
/*      */ 
/*      */ 
/* 1412 */                       unloadable = false;
/*      */                       File install_root;
/* 1414 */                       if (plugin.getPluginState().isShared())
/*      */                       {
/* 1416 */                         File origin_root = prog_dir;
/* 1417 */                         install_root = target_prog_dir;
/*      */                       }
/*      */                       else
/*      */                       {
/* 1421 */                         File origin_root = user_dir;
/* 1422 */                         install_root = target_user_dir;
/*      */                       }
/*      */                     }
/*      */                     else {
/* 1426 */                       origin_root = plugin_dir;
/* 1427 */                       install_root = target_plugin_dir;
/*      */                     }
/*      */                     
/* 1430 */                     origin = new File(origin_root, file_name);
/* 1431 */                     initial_target = new File(install_root, file_name);
/*      */                     
/* 1433 */                     final_target = initial_target;
/*      */                     
/* 1435 */                     if (origin.exists())
/*      */                     {
/* 1437 */                       if ((file_name.indexOf('/') == -1) && ((file_name.toLowerCase(MessageText.LOCALE_ENGLISH).endsWith(".properties")) || (file_name.toLowerCase(MessageText.LOCALE_ENGLISH).endsWith(".config"))))
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1444 */                         is_plugin_properties = file_name.toLowerCase(MessageText.LOCALE_ENGLISH).equals("plugin.properties");
/*      */                         
/* 1446 */                         String old_file_name = file_name;
/*      */                         
/* 1448 */                         file_name = file_name + "_" + target_version;
/*      */                         
/* 1450 */                         final_target = new File(install_root, file_name);
/*      */                         
/* 1452 */                         this.log.log(1, "saving new file '" + old_file_name + "'as '" + file_name + "'");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                       }
/* 1459 */                       else if (isVersioned(file_name))
/*      */                       {
/* 1461 */                         this.log.log(1, "Version '" + file_name + "' already present, skipping");
/*      */                         
/*      */ 
/* 1464 */                         skip_file = true;
/*      */                       }
/*      */                       else
/*      */                       {
/* 1468 */                         this.log.log(1, "overwriting '" + file_name + "'");
/*      */                         
/*      */ 
/* 1471 */                         File backup = new File(origin.getParentFile(), origin.getName() + ".bak");
/*      */                         
/*      */ 
/*      */ 
/* 1475 */                         if (force_indirect_install)
/*      */                         {
/* 1477 */                           if (backup.exists())
/*      */                           {
/* 1479 */                             installer.addRemoveAction(backup.getAbsolutePath());
/*      */                           }
/*      */                           
/* 1482 */                           installer.addMoveAction(origin.getAbsolutePath(), backup.getAbsolutePath());
/*      */                         }
/*      */                         else
/*      */                         {
/* 1486 */                           if (backup.exists())
/*      */                           {
/* 1488 */                             backup.delete();
/*      */                           }
/*      */                           
/* 1491 */                           if (!initial_target.renameTo(backup))
/*      */                           {
/* 1493 */                             this.log.log(1, "    failed to backup '" + file_name + "', deferring until restart");
/*      */                             
/*      */ 
/* 1496 */                             if (installer == null)
/*      */                             {
/* 1498 */                               update.setRestartRequired(2);
/*      */                               
/* 1500 */                               installer = update.getCheckInstance().createInstaller();
/*      */                             }
/*      */                             
/* 1503 */                             File tmp = new File(initial_target.getParentFile(), initial_target.getName() + ".tmp");
/*      */                             
/* 1505 */                             tmp.delete();
/*      */                             
/* 1507 */                             installer.addMoveAction(tmp.getAbsolutePath(), initial_target.getAbsolutePath());
/*      */                             
/* 1509 */                             final_target = tmp;
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/*      */ 
/* 1516 */                     if (!skip_file)
/*      */                     {
/* 1518 */                       FileUtil.mkdirs(final_target.getParentFile());
/*      */                       
/* 1520 */                       entry_os = new FileOutputStream(final_target);
/*      */                     }
/*      */                   }
/*      */                   
/* 1524 */                   byte[] buffer = new byte[65536];
/*      */                   
/*      */                   for (;;)
/*      */                   {
/* 1528 */                     int len = zis.read(buffer);
/*      */                     
/* 1530 */                     if (len <= 0) {
/*      */                       break;
/*      */                     }
/*      */                     
/*      */ 
/* 1535 */                     if (entry_os != null)
/*      */                     {
/* 1537 */                       entry_os.write(buffer, 0, len);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 finally {
/* 1542 */                   if (entry_os == null) {}
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 1548 */                 if (is_plugin_properties)
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1564 */                   Properties old_props = new Properties();
/* 1565 */                   Properties new_props = new Properties();
/*      */                   
/* 1567 */                   List props_to_delete = new ArrayList();
/* 1568 */                   Map props_to_replace = new HashMap();
/* 1569 */                   Map props_to_insert = new HashMap();
/*      */                   try
/*      */                   {
/* 1572 */                     FileInputStream fis = new FileInputStream(origin);
/*      */                     try
/*      */                     {
/* 1575 */                       old_props.load(fis);
/*      */                     }
/*      */                     catch (Throwable e) {}finally {}
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1585 */                     fis = new FileInputStream(final_target);
/*      */                     try
/*      */                     {
/* 1588 */                       new_props.load(fis);
/*      */ 
/*      */ 
/*      */                     }
/*      */                     catch (Throwable e) {}finally {}
/*      */ 
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*      */ 
/* 1598 */                     Debug.printStackTrace(e);
/*      */                   }
/*      */                   
/* 1601 */                   new_props.put("plugin.version", target_version);
/*      */                   
/* 1603 */                   String[] prop_names = { "plugin.name", "plugin.names", "plugin.class", "plugin.classes", "plugin.version", "plugin.langfile" };
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1610 */                   for (int z = 0; z < prop_names.length; z++)
/*      */                   {
/* 1612 */                     String prop_name = prop_names[z];
/*      */                     
/* 1614 */                     String old_name = old_props.getProperty(prop_name);
/* 1615 */                     String new_name = new_props.getProperty(prop_name);
/*      */                     
/* 1617 */                     if (new_name != null)
/*      */                     {
/* 1619 */                       if (prop_name.equals("plugin.name")) {
/* 1620 */                         props_to_delete.add("plugin.names");
/* 1621 */                       } else if (prop_name.equals("plugin.names")) {
/* 1622 */                         props_to_delete.add("plugin.name");
/* 1623 */                       } else if (prop_name.equals("plugin.class")) {
/* 1624 */                         props_to_delete.add("plugin.classes");
/* 1625 */                       } else if (prop_name.equals("plugin.classes")) {
/* 1626 */                         props_to_delete.add("plugin.class");
/*      */                       }
/*      */                       
/* 1629 */                       if (old_name == null)
/*      */                       {
/* 1631 */                         props_to_insert.put(prop_name, new_name);
/*      */                       }
/* 1633 */                       else if (!new_name.equals(old_name))
/*      */                       {
/* 1635 */                         props_to_replace.put(prop_name, new_name);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/*      */                   File tmp_file;
/*      */                   File tmp_file;
/* 1642 */                   if (force_indirect_install)
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 1647 */                     tmp_file = initial_target;
/*      */                   }
/*      */                   else
/*      */                   {
/* 1651 */                     tmp_file = new File(initial_target.getParentFile(), initial_target.getName() + ".tmp");
/*      */                   }
/*      */                   
/* 1654 */                   LineNumberReader lnr = null;
/*      */                   
/* 1656 */                   PrintWriter tmp = null;
/*      */                   try
/*      */                   {
/* 1659 */                     lnr = new LineNumberReader(new FileReader(origin));
/*      */                     
/* 1661 */                     tmp = new PrintWriter(new java.io.FileWriter(tmp_file));
/*      */                     
/* 1663 */                     Iterator it = props_to_insert.keySet().iterator();
/*      */                     
/* 1665 */                     while (it.hasNext())
/*      */                     {
/* 1667 */                       String pn = (String)it.next();
/*      */                       
/* 1669 */                       String pv = (String)props_to_insert.get(pn);
/*      */                       
/* 1671 */                       this.log.log("    Inserting property:" + pn + "=" + pv);
/*      */                       
/* 1673 */                       tmp.println(pn + "=" + pv);
/*      */                     }
/*      */                     
/*      */                     for (;;)
/*      */                     {
/* 1678 */                       String line = lnr.readLine();
/*      */                       
/* 1680 */                       if (line == null) {
/*      */                         break;
/*      */                       }
/*      */                       
/*      */ 
/* 1685 */                       int ep = line.indexOf('=');
/*      */                       
/* 1687 */                       if (ep != -1)
/*      */                       {
/* 1689 */                         String pn = line.substring(0, ep).trim();
/*      */                         
/* 1691 */                         if (props_to_delete.contains(pn))
/*      */                         {
/* 1693 */                           this.log.log("    Deleting property:" + pn);
/*      */                         }
/*      */                         else
/*      */                         {
/* 1697 */                           String rv = (String)props_to_replace.get(pn);
/*      */                           
/* 1699 */                           if (rv != null)
/*      */                           {
/* 1701 */                             this.log.log("    Replacing property:" + pn + " with " + rv);
/*      */                             
/* 1703 */                             tmp.println(pn + "=" + rv);
/*      */                           }
/*      */                           else
/*      */                           {
/* 1707 */                             tmp.println(line);
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                       else {
/* 1712 */                         tmp.println(line);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   finally {
/* 1717 */                     lnr.close();
/*      */                     
/* 1719 */                     if (tmp == null) {}
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/* 1725 */                   File bak_file = new File(origin.getParentFile(), origin.getName() + ".bak");
/*      */                   
/* 1727 */                   if (force_indirect_install)
/*      */                   {
/* 1729 */                     if (bak_file.exists())
/*      */                     {
/* 1731 */                       installer.addRemoveAction(bak_file.getAbsolutePath());
/*      */                     }
/*      */                     
/* 1734 */                     installer.addMoveAction(origin.getAbsolutePath(), bak_file.getAbsolutePath());
/*      */                   }
/*      */                   else
/*      */                   {
/* 1738 */                     if (bak_file.exists())
/*      */                     {
/* 1740 */                       bak_file.delete();
/*      */                     }
/*      */                     
/* 1743 */                     if (!initial_target.renameTo(bak_file))
/*      */                     {
/* 1745 */                       throw new IOException("Failed to rename '" + initial_target.toString() + "' to '" + bak_file.toString() + "'");
/*      */                     }
/*      */                     
/* 1748 */                     if (!tmp_file.renameTo(initial_target))
/*      */                     {
/* 1750 */                       bak_file.renameTo(initial_target);
/*      */                       
/* 1752 */                       throw new IOException("Failed to rename '" + tmp_file.toString() + "' to '" + initial_target.toString() + "'");
/*      */                     }
/*      */                     
/* 1755 */                     bak_file.delete();
/*      */                   }
/*      */                 }
/* 1758 */                 else if ((final_target != null) && (final_target.getName().equalsIgnoreCase("update.txt")))
/*      */                 {
/* 1760 */                   update_txt_found = true;
/*      */                   
/* 1762 */                   LineNumberReader lnr = null;
/*      */                   try
/*      */                   {
/* 1765 */                     lnr = new LineNumberReader(new FileReader(final_target));
/*      */                     
/*      */                     for (;;)
/*      */                     {
/* 1769 */                       String line = lnr.readLine();
/*      */                       
/* 1771 */                       if (line == null) {
/*      */                         break;
/*      */                       }
/*      */                       
/*      */ 
/* 1776 */                       this.log.log(1, line);
/*      */                     }
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 1781 */                     Debug.printStackTrace(e);
/*      */                   }
/*      */                   finally
/*      */                   {
/* 1785 */                     if (lnr == null) {}
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 1794 */               zis.close();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1799 */         if (unloadable)
/*      */         {
/*      */ 
/*      */ 
/* 1803 */           String plugin_id = plugin.getPluginID();
/*      */           
/* 1805 */           PluginInterface[] plugins = plugin.getPluginManager().getPlugins();
/*      */           
/* 1807 */           boolean plugin_unloadable = true;
/*      */           
/* 1809 */           for (int j = 0; j < plugins.length; j++)
/*      */           {
/* 1811 */             PluginInterface pi = plugins[j];
/*      */             
/* 1813 */             if (pi.getPluginID().equals(plugin_id))
/*      */             {
/* 1815 */               plugin_unloadable &= pi.getPluginState().isUnloadable();
/*      */             }
/*      */           }
/*      */           
/* 1819 */           if (!plugin_unloadable)
/*      */           {
/* 1821 */             this.log.log("Switching unloadability for " + plugin_id + " as changed during update");
/*      */             
/* 1823 */             update.setRestartRequired(2);
/*      */             
/* 1825 */             unloadable = false;
/*      */           }
/*      */         }
/*      */         
/* 1829 */         if (force_indirect_install)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1834 */           boolean defer_restart = false;
/*      */           
/* 1836 */           if (addInstallationActions(installer, install_properties, "%plugin%", target_plugin_dir, plugin_dir))
/*      */           {
/* 1838 */             defer_restart = true;
/*      */           }
/*      */           
/* 1841 */           if (addInstallationActions(installer, install_properties, "%app%", target_prog_dir, prog_dir))
/*      */           {
/* 1843 */             defer_restart = true;
/*      */           }
/*      */           
/* 1846 */           if (addInstallationActions(installer, install_properties, "%user%", target_user_dir, user_dir))
/*      */           {
/* 1848 */             defer_restart = true;
/*      */           }
/*      */           
/* 1851 */           if ((defer_restart) && (update.getRestartRequired() == 2))
/*      */           {
/* 1853 */             this.log.log("Deferring restart for '" + plugin.getPluginID() + "'");
/*      */             
/* 1855 */             update.setRestartRequired(1);
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1862 */           boolean defer_restart = false;
/*      */           
/* 1864 */           if (applyInstallProperties(install_properties, "%plugin%", plugin_dir))
/*      */           {
/* 1866 */             defer_restart = true;
/*      */           }
/*      */           
/* 1869 */           if (applyInstallProperties(install_properties, "%app%", prog_dir))
/*      */           {
/* 1871 */             defer_restart = true;
/*      */           }
/*      */           
/* 1874 */           if (applyInstallProperties(install_properties, "%user%", user_dir))
/*      */           {
/* 1876 */             defer_restart = true;
/*      */           }
/*      */           
/* 1879 */           if (unloadable)
/*      */           {
/* 1881 */             this.log.log("Plugin initialising, please wait... ");
/*      */             
/* 1883 */             plugin.getPluginState().reload();
/*      */             
/* 1885 */             this.log.log("... initialisation complete.");
/*      */ 
/*      */ 
/*      */           }
/* 1889 */           else if ((defer_restart) && (update.getRestartRequired() == 2))
/*      */           {
/* 1891 */             this.log.log("Deferring restart for '" + plugin.getPluginID() + "'");
/*      */             
/* 1893 */             update.setRestartRequired(1);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1899 */       Boolean b_disable = (Boolean)update.getCheckInstance().getProperty(3);
/*      */       
/* 1901 */       if ((update_txt_found) || (b_disable == null) || (!b_disable.booleanValue()))
/*      */       {
/* 1903 */         String msg = MessageText.getString("plugin.update.ok", new String[] { new_version, update.getName() });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1908 */         if (update_txt_found)
/*      */         {
/* 1910 */           msg = msg + " - " + MessageText.getString("plugin.update.ok.msg");
/*      */         }
/*      */         
/* 1913 */         this.log.logAlertRepeatable(update_txt_found ? 2 : 1, msg);
/*      */       }
/*      */       try
/*      */       {
/* 1917 */         String plugin_id = plugin.getPluginID();
/*      */         
/* 1919 */         org.gudy.azureus2.pluginsimpl.local.PluginInitializer.fireEvent(checker.getCheckInstance().getType() == 1 ? 10 : 11, plugin_id);
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/* 1925 */         Debug.out(e);
/*      */       }
/* 1927 */       update_successful = true;
/*      */       String msg;
/*      */       return;
/*      */     } catch (Throwable e) {
/* 1931 */       msg = MessageText.getString("plugin.update.fail", new String[] { new_version, update.getName(), e.getMessage() });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1936 */       this.log.logAlertRepeatable(3, msg);
/*      */     }
/*      */     finally
/*      */     {
/* 1940 */       update.complete(update_successful);
/*      */       
/* 1942 */       if (data != null) {
/*      */         try
/*      */         {
/* 1945 */           data.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean addInstallationActions(UpdateInstaller installer, Map<String, List<String[]>> install_properties, String prefix, File from_file, File to_file)
/*      */     throws org.gudy.azureus2.plugins.update.UpdateException
/*      */   {
/* 1963 */     boolean defer_restart = false;
/*      */     
/* 1965 */     if (from_file.isDirectory())
/*      */     {
/* 1967 */       File[] files = from_file.listFiles();
/*      */       
/* 1969 */       if (files != null)
/*      */       {
/* 1971 */         for (int i = 0; i < files.length; i++)
/*      */         {
/* 1973 */           if (addInstallationActions(installer, install_properties, prefix + "/" + files[i].getName(), files[i], new File(to_file, files[i].getName())))
/*      */           {
/* 1975 */             defer_restart = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1981 */       installer.addMoveAction(from_file.getAbsolutePath(), to_file.getAbsolutePath());
/*      */       
/* 1983 */       List<String[]> commands = (List)install_properties.get(prefix);
/*      */       
/* 1985 */       if (commands != null)
/*      */       {
/* 1987 */         for (String[] command : commands)
/*      */         {
/* 1989 */           String cmd = command[1];
/*      */           
/* 1991 */           if (cmd.equals("chmod"))
/*      */           {
/* 1993 */             if (!Constants.isWindows)
/*      */             {
/* 1995 */               this.log.log("Applying " + cmd + " " + command[2] + " to " + to_file);
/*      */               
/* 1997 */               installer.addChangeRightsAction(command[2], to_file.getAbsolutePath());
/*      */             }
/* 1999 */           } else if (cmd.equals("rm"))
/*      */           {
/* 2001 */             this.log.log("Deleting " + to_file);
/*      */             
/* 2003 */             installer.addRemoveAction(to_file.getAbsolutePath());
/*      */           }
/* 2005 */           else if (cmd.equals("defer_restart"))
/*      */           {
/* 2007 */             defer_restart = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2013 */     return defer_restart;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean applyInstallProperties(Map<String, List<String[]>> install_properties, String prefix, File to_file)
/*      */   {
/* 2022 */     boolean defer_restart = false;
/*      */     
/* 2024 */     if (to_file.isDirectory())
/*      */     {
/* 2026 */       File[] files = to_file.listFiles();
/*      */       
/* 2028 */       if (files != null)
/*      */       {
/* 2030 */         for (int i = 0; i < files.length; i++)
/*      */         {
/* 2032 */           File file = files[i];
/*      */           
/* 2034 */           String file_name = file.getName();
/*      */           
/* 2036 */           if ((!file_name.equals(".")) && (!file_name.equals("..")))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2041 */             String new_prefix = prefix + "/" + file_name;
/*      */             
/* 2043 */             boolean match = false;
/*      */             
/* 2045 */             for (String s : install_properties.keySet())
/*      */             {
/* 2047 */               if (s.startsWith(new_prefix))
/*      */               {
/* 2049 */                 match = true;
/*      */                 
/* 2051 */                 break;
/*      */               }
/*      */             }
/*      */             
/* 2055 */             if (match)
/*      */             {
/* 2057 */               if (applyInstallProperties(install_properties, new_prefix, files[i]))
/*      */               {
/* 2059 */                 defer_restart = true;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/* 2066 */       List<String[]> commands = (List)install_properties.get(prefix);
/*      */       
/* 2068 */       if (commands != null)
/*      */       {
/* 2070 */         for (String[] command : commands)
/*      */         {
/* 2072 */           String cmd = command[1];
/*      */           
/* 2074 */           if (cmd.equals("chmod"))
/*      */           {
/* 2076 */             if (!Constants.isWindows)
/*      */             {
/* 2078 */               runCommand(new String[] { "chmod", command[2], to_file.getAbsolutePath().replaceAll(" ", "\\ ") });
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/* 2085 */           else if (cmd.equals("rm"))
/*      */           {
/* 2087 */             this.log.log("Deleting " + to_file);
/*      */             
/* 2089 */             to_file.delete();
/*      */           }
/* 2091 */           else if (cmd.equals("defer_restart"))
/*      */           {
/* 2093 */             defer_restart = true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2099 */     return defer_restart;
/*      */   }
/*      */   
/*      */ 
/*      */   private void runCommand(String[] command)
/*      */   {
/*      */     try
/*      */     {
/* 2107 */       command[0] = findCommand(command[0]);
/*      */       
/* 2109 */       String str = "";
/*      */       
/* 2111 */       for (String s : command)
/*      */       {
/* 2113 */         str = str + " " + s;
/*      */       }
/*      */       
/* 2116 */       this.log.log("Executing" + str);
/*      */       
/* 2118 */       Runtime.getRuntime().exec(command).waitFor();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2122 */       this.log.log("Failed to execute command", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private String findCommand(String name)
/*      */   {
/* 2130 */     String[] locations = { "/bin", "/usr/bin" };
/*      */     
/* 2132 */     for (String s : locations)
/*      */     {
/* 2134 */       File f = new File(s, name);
/*      */       
/* 2136 */       if ((f.exists()) && (f.canRead()))
/*      */       {
/* 2138 */         return f.getAbsolutePath();
/*      */       }
/*      */     }
/*      */     
/* 2142 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isVersioned(String name)
/*      */   {
/* 2152 */     int pos = name.lastIndexOf('_');
/*      */     
/* 2154 */     if ((pos == -1) || (name.endsWith("_")))
/*      */     {
/* 2156 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2161 */     String rem = name.substring(pos + 1);
/*      */     
/* 2163 */     pos = rem.lastIndexOf('.');
/*      */     
/*      */ 
/*      */ 
/* 2167 */     if (pos != -1)
/*      */     {
/* 2169 */       rem = rem.substring(0, pos);
/*      */     }
/*      */     
/* 2172 */     for (int i = 0; i < rem.length(); i++)
/*      */     {
/* 2174 */       char c = rem.charAt(i);
/*      */       
/* 2176 */       if ((c != '.') && (!Character.isDigit(c)))
/*      */       {
/* 2178 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 2182 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void logMultiLine(String indent, List lines)
/*      */   {
/* 2190 */     for (int i = 0; i < lines.size(); i++)
/*      */     {
/* 2192 */       this.log.log(1, indent + (String)lines.get(i));
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/update/PluginUpdatePlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */