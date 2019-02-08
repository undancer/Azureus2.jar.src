/*      */ package org.gudy.azureus2.pluginsimpl.local.installer;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.InputStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginException;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.installer.FilePluginInstaller;
/*      */ import org.gudy.azureus2.plugins.installer.InstallablePlugin;
/*      */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*      */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*      */ import org.gudy.azureus2.plugins.installer.PluginInstallerListener;
/*      */ import org.gudy.azureus2.plugins.installer.StandardPlugin;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*      */ import org.gudy.azureus2.plugins.update.Update;
/*      */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*      */ import org.gudy.azureus2.plugins.update.UpdateCheckInstanceListener;
/*      */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*      */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*      */ import org.gudy.azureus2.plugins.update.UpdateListener;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.pluginsimpl.local.FailedPlugin;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.update.UpdateCheckInstanceImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.update.UpdateManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.update.PluginUpdatePlugin;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsException;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoader;
/*      */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderFactory;
/*      */ 
/*      */ public class PluginInstallerImpl
/*      */   implements PluginInstaller
/*      */ {
/*      */   protected static PluginInstallerImpl singleton;
/*      */   private PluginManager manager;
/*      */   
/*      */   public static synchronized PluginInstallerImpl getSingleton(PluginManager _manager)
/*      */   {
/*   74 */     if (singleton == null)
/*      */     {
/*   76 */       singleton = new PluginInstallerImpl(_manager);
/*      */     }
/*      */     
/*   79 */     return singleton;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*   84 */   private CopyOnWriteList<PluginInstallerListener> listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */   private AsyncDispatcher add_file_install_dispatcher;
/*      */   
/*      */ 
/*      */   protected PluginInstallerImpl(PluginManager _manager)
/*      */   {
/*   92 */     this.manager = _manager;
/*      */     
/*   94 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void process(VuzeFile[] files, int expected_types)
/*      */       {
/*      */ 
/*      */ 
/*  102 */         for (int i = 0; i < files.length; i++)
/*      */         {
/*  104 */           VuzeFile vf = files[i];
/*      */           
/*  106 */           VuzeFileComponent[] comps = vf.getComponents();
/*      */           
/*  108 */           for (int j = 0; j < comps.length; j++)
/*      */           {
/*  110 */             VuzeFileComponent comp = comps[j];
/*      */             
/*  112 */             if (comp.getType() == 8) {
/*      */               try
/*      */               {
/*  115 */                 Map content = comp.getContent();
/*      */                 
/*  117 */                 String id = new String((byte[])content.get("id"), "UTF-8");
/*  118 */                 String version = new String((byte[])content.get("version"), "UTF-8");
/*  119 */                 String suffix = ((Long)content.get("is_jar")).longValue() == 1L ? "jar" : "zip";
/*      */                 
/*  121 */                 byte[] plugin_file = (byte[])content.get("file");
/*      */                 
/*  123 */                 File temp_dir = AETemporaryFileHandler.createTempDir();
/*      */                 
/*  125 */                 File temp_file = new File(temp_dir, id + "_" + version + "." + suffix);
/*      */                 
/*  127 */                 FileUtil.copyFile(new ByteArrayInputStream(plugin_file), temp_file);
/*      */                 
/*  129 */                 FilePluginInstaller installer = PluginInstallerImpl.this.installFromFile(temp_file);
/*      */                 
/*  131 */                 PluginInstallerImpl.this.addFileInstallOperation(installer);
/*      */                 
/*  133 */                 comp.setProcessed();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  137 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addFileInstallOperation(final FilePluginInstaller installer)
/*      */   {
/*  150 */     synchronized (this)
/*      */     {
/*  152 */       if (this.add_file_install_dispatcher == null)
/*      */       {
/*  154 */         this.add_file_install_dispatcher = new AsyncDispatcher();
/*      */       }
/*      */       
/*  157 */       this.add_file_install_dispatcher.dispatch(new AERunnable()
/*      */       {
/*      */ 
/*      */         public void runSupport()
/*      */         {
/*      */           try
/*      */           {
/*  164 */             final AESemaphore done_sem = new AESemaphore("PluginInstall:fio");
/*      */             
/*  166 */             final UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */             
/*  168 */             new AEThread2("PluginInstall:fio", true)
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*  173 */                 if (PluginInstallerImpl.2.this.val$installer.isAlreadyInstalled())
/*      */                 {
/*  175 */                   String details = MessageText.getString("fileplugininstall.duplicate.desc", new String[] { PluginInstallerImpl.2.this.val$installer.getName(), PluginInstallerImpl.2.this.val$installer.getVersion() });
/*      */                   
/*      */ 
/*      */ 
/*  179 */                   ui_manager.showMessageBox("fileplugininstall.duplicate.title", "!" + details + "!", 1L);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  184 */                   done_sem.release();
/*      */                 }
/*      */                 else
/*      */                 {
/*  188 */                   String details = MessageText.getString("fileplugininstall.install.desc", new String[] { PluginInstallerImpl.2.this.val$installer.getName(), PluginInstallerImpl.2.this.val$installer.getVersion() });
/*      */                   
/*      */ 
/*      */ 
/*  192 */                   long res = ui_manager.showMessageBox("fileplugininstall.install.title", "!" + details + "!", 12L);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  197 */                   if (res == 4L)
/*      */                   {
/*      */                     try {
/*  200 */                       PluginInstallerImpl.this.install(new InstallablePlugin[] { PluginInstallerImpl.2.this.val$installer }, false, true, null, new PluginInstallationListener()
/*      */                       {
/*      */ 
/*      */ 
/*      */ 
/*      */                         public void completed()
/*      */                         {
/*      */ 
/*      */ 
/*      */ 
/*  210 */                           PluginInstallerImpl.2.1.this.val$done_sem.release();
/*      */                         }
/*      */                         
/*      */ 
/*      */                         public void cancelled()
/*      */                         {
/*  216 */                           PluginInstallerImpl.2.1.this.val$done_sem.release();
/*      */                         }
/*      */                         
/*      */ 
/*      */ 
/*      */                         public void failed(PluginException e)
/*      */                         {
/*  223 */                           PluginInstallerImpl.2.1.this.val$done_sem.release();
/*      */                           
/*  225 */                           Debug.out("Installation failed", e);
/*      */                         }
/*      */                       });
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/*  231 */                       Debug.printStackTrace(e);
/*      */                       
/*  233 */                       done_sem.release();
/*      */                     }
/*  235 */                   } else if (res == 8L)
/*      */                   {
/*  237 */                     done_sem.release();
/*      */                   }
/*      */                   else
/*      */                   {
/*  241 */                     Debug.out("Message box not handled");
/*      */                     
/*  243 */                     done_sem.release();
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }.start();
/*      */             
/*      */ 
/*  250 */             while (!done_sem.reserve(60000L))
/*      */             {
/*  252 */               if (PluginInstallerImpl.this.add_file_install_dispatcher.getQueueSize() > 0)
/*      */               {
/*  254 */                 Debug.out("File plugin install operation queued pending completion of previous");
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  260 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected PluginManager getPluginManager()
/*      */   {
/*  270 */     return this.manager;
/*      */   }
/*      */   
/*      */ 
/*      */   public StandardPlugin[] getStandardPlugins()
/*      */     throws PluginException
/*      */   {
/*      */     try
/*      */     {
/*  279 */       SFPluginDetailsLoader loader = SFPluginDetailsLoaderFactory.getSingleton();
/*      */       
/*  281 */       SFPluginDetails[] details = loader.getPluginDetails();
/*      */       
/*  283 */       List res = new ArrayList();
/*      */       
/*  285 */       for (int i = 0; i < details.length; i++)
/*      */       {
/*  287 */         SFPluginDetails detail = details[i];
/*      */         
/*  289 */         String name = detail.getId();
/*      */         
/*  291 */         String version = "";
/*      */         
/*  293 */         if (Constants.isCVSVersion())
/*      */         {
/*  295 */           version = detail.getCVSVersion();
/*      */         }
/*      */         
/*  298 */         if ((version == null) || (version.length() == 0) || (!Character.isDigit(version.charAt(0))))
/*      */         {
/*  300 */           version = detail.getVersion();
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  307 */           String non_cvs_version = detail.getVersion();
/*      */           
/*  309 */           if (version.equals(non_cvs_version + "_CVS"))
/*      */           {
/*  311 */             version = non_cvs_version;
/*      */           }
/*      */         }
/*      */         
/*  315 */         if ((!name.startsWith("azplatform")) && (!name.equals("azupdater")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  320 */           if ((version != null) && (version.length() != 0) && (Character.isDigit(version.charAt(0))))
/*      */           {
/*      */ 
/*      */ 
/*  324 */             if (!detail.getCategory().equalsIgnoreCase("hidden"))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  330 */               res.add(new StandardPluginImpl(this, details[i], version)); }
/*      */           }
/*      */         }
/*      */       }
/*  334 */       StandardPlugin[] res_a = new StandardPlugin[res.size()];
/*      */       
/*  336 */       res.toArray(res_a);
/*      */       
/*  338 */       return res_a;
/*      */     }
/*      */     catch (SFPluginDetailsException e)
/*      */     {
/*  342 */       throw new PluginException("Failed to load standard plugin details", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public StandardPlugin getStandardPlugin(String id)
/*      */     throws PluginException
/*      */   {
/*      */     try
/*      */     {
/*  353 */       SFPluginDetailsLoader loader = SFPluginDetailsLoaderFactory.getSingleton();
/*      */       
/*  355 */       SFPluginDetails[] details = loader.getPluginDetails();
/*      */       
/*      */ 
/*  358 */       for (int i = 0; i < details.length; i++)
/*      */       {
/*  360 */         SFPluginDetails detail = details[i];
/*      */         
/*  362 */         String name = detail.getId();
/*      */         
/*  364 */         if (name.equals(id))
/*      */         {
/*  366 */           String version = "";
/*      */           
/*  368 */           if (Constants.isCVSVersion())
/*      */           {
/*  370 */             version = detail.getCVSVersion();
/*      */           }
/*      */           
/*  373 */           if ((version == null) || (version.length() == 0) || (!Character.isDigit(version.charAt(0))))
/*      */           {
/*  375 */             version = detail.getVersion();
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  382 */             String non_cvs_version = detail.getVersion();
/*      */             
/*  384 */             if (version.equals(non_cvs_version + "_CVS"))
/*      */             {
/*  386 */               version = non_cvs_version;
/*      */             }
/*      */           }
/*      */           
/*  390 */           if ((!name.startsWith("azplatform")) && (!name.equals("azupdater")))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  395 */             if ((version != null) && (version.length() != 0) && (Character.isDigit(version.charAt(0))))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  401 */               return new StandardPluginImpl(this, details[i], version);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  406 */       return null;
/*      */     }
/*      */     catch (SFPluginDetailsException e)
/*      */     {
/*  410 */       throw new PluginException("Failed to load standard plugin details", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private File extractFromVuzeFile(File file)
/*      */     throws PluginException
/*      */   {
/*  420 */     VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(file);
/*      */     
/*  422 */     VuzeFileComponent[] comps = vf.getComponents();
/*      */     
/*  424 */     for (int j = 0; j < comps.length; j++)
/*      */     {
/*  426 */       VuzeFileComponent comp = comps[j];
/*      */       
/*  428 */       if (comp.getType() == 8) {
/*      */         try
/*      */         {
/*  431 */           Map content = comp.getContent();
/*      */           
/*  433 */           String id = new String((byte[])content.get("id"), "UTF-8");
/*  434 */           String version = new String((byte[])content.get("version"), "UTF-8");
/*  435 */           String suffix = ((Long)content.get("is_jar")).longValue() == 1L ? "jar" : "zip";
/*      */           
/*  437 */           byte[] plugin_file = (byte[])content.get("file");
/*      */           
/*  439 */           File temp_dir = AETemporaryFileHandler.createTempDir();
/*      */           
/*  441 */           File temp_file = new File(temp_dir, id + "_" + version + "." + suffix);
/*      */           
/*  443 */           FileUtil.copyFile(new ByteArrayInputStream(plugin_file), temp_file);
/*      */           
/*  445 */           return temp_file;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  449 */           throw new PluginException("Not a valid Vuze file", e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  454 */     return file;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public FilePluginInstaller installFromFile(File file)
/*      */     throws PluginException
/*      */   {
/*  463 */     if (file.getName().toLowerCase(Locale.US).endsWith(".vuze"))
/*      */     {
/*  465 */       file = extractFromVuzeFile(file);
/*      */     }
/*      */     
/*  468 */     return new FilePluginInstallerImpl(this, file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void install(InstallablePlugin installable_plugin, boolean shared)
/*      */     throws PluginException
/*      */   {
/*  478 */     install(new InstallablePlugin[] { installable_plugin }, shared);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void install(InstallablePlugin[] plugins, boolean shared)
/*      */     throws PluginException
/*      */   {
/*  488 */     install(plugins, shared, false, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UpdateCheckInstance install(InstallablePlugin[] plugins, boolean shared, Map<Integer, Object> properties, PluginInstallationListener listener)
/*      */     throws PluginException
/*      */   {
/*  500 */     return install(plugins, shared, false, properties, listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected UpdateCheckInstance install(InstallablePlugin[] plugins, boolean shared, boolean low_noise, Map<Integer, Object> properties, final PluginInstallationListener listener)
/*      */     throws PluginException
/*      */   {
/*  513 */     PluginInterface pup_pi = this.manager.getPluginInterfaceByClass(PluginUpdatePlugin.class);
/*      */     
/*  515 */     if (pup_pi == null)
/*      */     {
/*  517 */       throw new PluginException("Installation aborted, plugin-update plugin unavailable");
/*      */     }
/*      */     
/*  520 */     if (!pup_pi.getPluginState().isOperational())
/*      */     {
/*  522 */       throw new PluginException("Installation aborted, plugin-update plugin not operational");
/*      */     }
/*      */     
/*  525 */     PluginUpdatePlugin pup = (PluginUpdatePlugin)pup_pi.getPlugin();
/*      */     
/*  527 */     UpdateManagerImpl uman = (UpdateManagerImpl)this.manager.getDefaultPluginInterface().getUpdateManager();
/*      */     
/*  529 */     UpdateCheckInstanceImpl inst = uman.createEmptyUpdateCheckInstance(1, "update.instance.install", low_noise);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  535 */     if (properties != null)
/*      */     {
/*  537 */       for (Map.Entry<Integer, Object> entry : properties.entrySet())
/*      */       {
/*  539 */         inst.setProperty(((Integer)entry.getKey()).intValue(), entry.getValue());
/*      */       }
/*      */     }
/*      */     
/*  543 */     if (listener != null)
/*      */     {
/*  545 */       inst.addListener(new UpdateCheckInstanceListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void cancelled(UpdateCheckInstance instance)
/*      */         {
/*      */ 
/*  552 */           listener.cancelled();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void complete(UpdateCheckInstance instance)
/*      */         {
/*  559 */           final Update[] updates = instance.getUpdates();
/*      */           
/*  561 */           if (updates.length == 0)
/*      */           {
/*  563 */             listener.failed(new PluginException("No updates were added during check process"));
/*      */           }
/*      */           else
/*      */           {
/*  567 */             for (int i = 0; i < updates.length; i++)
/*      */             {
/*  569 */               updates[i].addListener(new UpdateListener()
/*      */               {
/*      */                 private boolean cancelled;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void cancelled(Update update)
/*      */                 {
/*  578 */                   this.cancelled = true;
/*      */                   
/*  580 */                   check();
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */                 public void complete(Update update)
/*      */                 {
/*  587 */                   check();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 protected void check()
/*      */                 {
/*  593 */                   Update failed_update = null;
/*      */                   
/*  595 */                   for (Update update : updates)
/*      */                   {
/*  597 */                     if ((!update.isCancelled()) && (!update.isComplete()))
/*      */                     {
/*  599 */                       return;
/*      */                     }
/*      */                     
/*  602 */                     if (!update.wasSuccessful())
/*      */                     {
/*  604 */                       failed_update = update;
/*      */                     }
/*      */                   }
/*      */                   
/*  608 */                   if (this.cancelled)
/*      */                   {
/*  610 */                     PluginInstallerImpl.3.this.val$listener.cancelled();
/*      */ 
/*      */ 
/*      */                   }
/*  614 */                   else if (failed_update == null)
/*      */                   {
/*      */ 
/*      */ 
/*  618 */                     PluginInitializer.waitForPluginEvents();
/*      */                     
/*  620 */                     PluginInstallerImpl.3.this.val$listener.completed();
/*      */                   }
/*      */                   else
/*      */                   {
/*  624 */                     PluginInstallerImpl.3.this.val$listener.failed(new PluginException("Install of " + failed_update.getName() + " failed"));
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  639 */       for (int i = 0; i < plugins.length; i++)
/*      */       {
/*  641 */         InstallablePlugin plugin = plugins[i];
/*      */         
/*  643 */         final String plugin_id = plugin.getId();
/*      */         
/*  645 */         PluginInterface existing_plugin_interface = this.manager.getPluginInterfaceByID(plugin_id, false);
/*      */         
/*  647 */         Plugin existing_plugin = null;
/*      */         
/*  649 */         if (existing_plugin_interface != null)
/*      */         {
/*  651 */           existing_plugin = existing_plugin_interface.getPlugin();
/*      */           
/*      */ 
/*      */ 
/*  655 */           String old_version = existing_plugin_interface.getPluginVersion();
/*      */           
/*  657 */           if (old_version != null)
/*      */           {
/*  659 */             int res = Constants.compareVersions(plugin.getVersion(), old_version);
/*      */             
/*  661 */             if (res < 0)
/*      */             {
/*  663 */               throw new PluginException("A higher version (" + old_version + ") of Plugin '" + plugin_id + "' is already installed");
/*      */             }
/*  665 */             if (res == 0)
/*      */             {
/*  667 */               throw new PluginException("Version (" + old_version + ") of Plugin '" + plugin_id + "' is already installed");
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */         String target_dir;
/*      */         
/*  674 */         if (shared)
/*      */         {
/*  676 */           target_dir = FileUtil.getApplicationFile("plugins").toString();
/*      */         }
/*      */         else
/*      */         {
/*  680 */           target_dir = FileUtil.getUserFile("plugins").toString();
/*      */         }
/*      */         
/*  683 */         String target_dir = target_dir + File.separator + plugin_id;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  688 */         new File(target_dir).mkdir();
/*      */         
/*  690 */         if (existing_plugin == null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  695 */           FailedPlugin dummy_plugin = new FailedPlugin(plugin_id, target_dir);
/*      */           
/*  697 */           PluginManager.registerPlugin(dummy_plugin, plugin_id);
/*      */           
/*  699 */           final PluginInterface dummy_plugin_interface = this.manager.getPluginInterfaceByID(plugin_id, false);
/*      */           
/*  701 */           ((InstallablePluginImpl)plugin).addUpdate(inst, pup, dummy_plugin, dummy_plugin_interface);
/*      */           
/*  703 */           inst.addListener(new UpdateCheckInstanceListener()
/*      */           {
/*      */ 
/*      */             public void cancelled(UpdateCheckInstance instance)
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/*  712 */                 dummy_plugin_interface.getPluginState().unload();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  716 */                 Debug.out("Failed to unload plugin", e);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             public void complete(UpdateCheckInstance instance)
/*      */             {
/*  724 */               PluginInterface pi = PluginInstallerImpl.this.manager.getPluginInterfaceByID(plugin_id, false);
/*      */               
/*  726 */               if ((pi != null) && ((pi.getPlugin() instanceof FailedPlugin))) {
/*      */                 try
/*      */                 {
/*  729 */                   pi.getPluginState().unload();
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  733 */                   Debug.out("Failed to unload plugin", e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */         else
/*      */         {
/*  741 */           ((InstallablePluginImpl)plugin).addUpdate(inst, pup, existing_plugin, existing_plugin_interface);
/*      */         }
/*      */       }
/*      */       
/*  745 */       inst.start();
/*      */       
/*  747 */       return inst;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  751 */       inst.cancel();
/*      */       
/*  753 */       if ((e instanceof PluginException))
/*      */       {
/*  755 */         throw ((PluginException)e);
/*      */       }
/*      */       
/*  758 */       throw new PluginException("Failed to create installer", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void uninstall(InstallablePlugin standard_plugin)
/*      */     throws PluginException
/*      */   {
/*  769 */     PluginInterface pi = standard_plugin.getAlreadyInstalledPlugin();
/*      */     
/*  771 */     if (pi == null)
/*      */     {
/*  773 */       throw new PluginException(" Plugin '" + standard_plugin.getId() + "' is not installed");
/*      */     }
/*      */     
/*  776 */     pi.getPluginState().uninstall();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void uninstall(PluginInterface pi)
/*      */     throws PluginException
/*      */   {
/*  785 */     uninstall(new PluginInterface[] { pi });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void uninstall(PluginInterface[] pis)
/*      */     throws PluginException
/*      */   {
/*  794 */     uninstall(pis, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void uninstall(PluginInterface[] pis, PluginInstallationListener listener_maybe_null)
/*      */     throws PluginException
/*      */   {
/*  804 */     uninstall(pis, listener_maybe_null, new HashMap());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public UpdateCheckInstance uninstall(PluginInterface[] pis, final PluginInstallationListener listener_maybe_null, final Map<Integer, Object> properties)
/*      */     throws PluginException
/*      */   {
/*  815 */     properties.put(Integer.valueOf(5), Boolean.valueOf(false));
/*      */     
/*  817 */     for (int i = 0; i < pis.length; i++)
/*      */     {
/*  819 */       PluginInterface pi = pis[i];
/*      */       
/*  821 */       if (pi.getPluginState().isMandatory())
/*      */       {
/*  823 */         throw new PluginException("Plugin '" + pi.getPluginID() + "' is mandatory, can't uninstall");
/*      */       }
/*      */       
/*  826 */       if (pi.getPluginState().isBuiltIn())
/*      */       {
/*  828 */         throw new PluginException("Plugin '" + pi.getPluginID() + "' is built-in, can't uninstall");
/*      */       }
/*      */       
/*  831 */       String plugin_dir = pi.getPluginDirectoryName();
/*      */       
/*  833 */       if ((plugin_dir == null) || (!new File(plugin_dir).exists()))
/*      */       {
/*  835 */         throw new PluginException("Plugin '" + pi.getPluginID() + "' is not loaded from the file system, can't uninstall");
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  840 */       UpdateManager uman = this.manager.getDefaultPluginInterface().getUpdateManager();
/*      */       
/*  842 */       UpdateCheckInstance inst = uman.createEmptyUpdateCheckInstance(3, "update.instance.uninstall");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  847 */       final int[] rds_added = { 0 };
/*      */       
/*  849 */       final AESemaphore rd_waiter_sem = new AESemaphore("uninst:rd:wait");
/*      */       
/*  851 */       for (int i = 0; i < pis.length; i++)
/*      */       {
/*  853 */         final PluginInterface pi = pis[i];
/*      */         
/*  855 */         final String plugin_dir = pi.getPluginDirectoryName();
/*      */         
/*  857 */         inst.addUpdatableComponent(new UpdatableComponent()
/*      */         {
/*      */ 
/*      */           public String getName()
/*      */           {
/*      */ 
/*  863 */             return pi.getPluginName();
/*      */           }
/*      */           
/*      */ 
/*      */           public int getMaximumCheckTime()
/*      */           {
/*  869 */             return 0;
/*      */           }
/*      */           
/*      */ 
/*      */           public void checkForUpdate(final UpdateChecker checker)
/*      */           {
/*      */             try
/*      */             {
/*  877 */               ResourceDownloader rd = PluginInstallerImpl.this.manager.getDefaultPluginInterface().getUtilities().getResourceDownloaderFactory().create(new File(plugin_dir));
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  882 */               String update_name = "";
/*      */               
/*  884 */               PluginInterface[] ifs = PluginInstallerImpl.this.manager.getPluginInterfaces();
/*      */               
/*  886 */               Arrays.sort(ifs, new Comparator()
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */                 public int compare(Object o1, Object o2)
/*      */                 {
/*      */ 
/*      */ 
/*  895 */                   return ((PluginInterface)o1).getPluginName().compareTo(((PluginInterface)o2).getPluginName());
/*      */                 }
/*      */               });
/*      */               
/*  899 */               for (int i = 0; i < ifs.length; i++)
/*      */               {
/*  901 */                 if (ifs[i].getPluginID().equals(pi.getPluginID()))
/*      */                 {
/*  903 */                   update_name = update_name + (update_name.length() == 0 ? "" : ",") + ifs[i].getPluginName();
/*      */                 }
/*      */               }
/*      */               
/*  907 */               boolean unloadable = pi.getPluginState().isUnloadable();
/*      */               
/*  909 */               if (!unloadable)
/*      */               {
/*  911 */                 properties.put(Integer.valueOf(5), Boolean.valueOf(true));
/*      */               }
/*      */               
/*  914 */               final Update update = checker.addUpdate(update_name, new String[] { "Uninstall: " + plugin_dir }, pi.getPluginVersion(), pi.getPluginVersion(), rd, unloadable ? 1 : 2);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  922 */               synchronized (rds_added)
/*      */               {
/*  924 */                 rds_added[0] += 1;
/*      */               }
/*      */               
/*  927 */               rd.addListener(new ResourceDownloaderAdapter()
/*      */               {
/*      */ 
/*      */                 public boolean completed(ResourceDownloader downloader, InputStream data)
/*      */                 {
/*      */ 
/*      */                   try
/*      */                   {
/*      */                     try
/*      */                     {
/*  937 */                       if (PluginInstallerImpl.5.this.val$pi.getPluginState().isUnloadable())
/*      */                       {
/*  939 */                         PluginInstallerImpl.5.this.val$pi.getPluginState().unload();
/*      */                         
/*  941 */                         if (!FileUtil.recursiveDelete(new File(PluginInstallerImpl.5.this.val$plugin_dir)))
/*      */                         {
/*  943 */                           update.setRestartRequired(2);
/*      */                           
/*  945 */                           PluginInstallerImpl.5.this.val$properties.put(Integer.valueOf(5), Boolean.valueOf(true));
/*      */                           
/*  947 */                           checker.reportProgress("Failed to remove plugin, restart will be required");
/*      */                         }
/*      */                       }
/*      */                       
/*  951 */                       UpdateInstaller installer = checker.createInstaller();
/*      */                       
/*  953 */                       installer.addRemoveAction(new File(PluginInstallerImpl.5.this.val$plugin_dir).getCanonicalPath());
/*      */                       
/*  955 */                       update.complete(true);
/*      */                       try
/*      */                       {
/*  958 */                         PluginInitializer.fireEvent(12, PluginInstallerImpl.5.this.val$pi.getPluginID());
/*      */ 
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/*      */ 
/*  964 */                         Debug.out(e);
/*      */                       }
/*      */                     }
/*      */                     catch (Throwable e) {
/*  968 */                       update.complete(false);
/*      */                       
/*  970 */                       Debug.printStackTrace(e);
/*      */                       
/*  972 */                       Logger.log(new LogAlert(true, "Plugin uninstall failed", e));
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/*      */ 
/*  978 */                     return 1;
/*      */                   }
/*      */                   finally
/*      */                   {
/*  982 */                     PluginInstallerImpl.5.this.val$rd_waiter_sem.release();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */                 public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*      */                 {
/*      */                   try
/*      */                   {
/*  992 */                     update.complete(false);
/*      */                     
/*  994 */                     if (!downloader.isCancelled())
/*      */                     {
/*  996 */                       Logger.log(new LogAlert(true, "Plugin uninstall failed", e));
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/* 1001 */                     PluginInstallerImpl.5.this.val$rd_waiter_sem.release();
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             finally {
/* 1007 */               checker.completed(); } } }, false);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1014 */       if (listener_maybe_null != null)
/*      */       {
/* 1016 */         inst.addListener(new UpdateCheckInstanceListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void cancelled(UpdateCheckInstance instance)
/*      */           {
/*      */ 
/* 1023 */             listener_maybe_null.cancelled();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void complete(UpdateCheckInstance instance)
/*      */           {
/* 1033 */             new AEThread2("Uninstall:async")
/*      */             {
/*      */               public void run()
/*      */               {
/*      */                 int wait_count;
/*      */                 
/*      */ 
/* 1040 */                 synchronized (PluginInstallerImpl.6.this.val$rds_added)
/*      */                 {
/* 1042 */                   wait_count = PluginInstallerImpl.6.this.val$rds_added[0];
/*      */                 }
/*      */                 
/* 1045 */                 for (int i = 0; i < wait_count; i++)
/*      */                 {
/* 1047 */                   PluginInstallerImpl.6.this.val$rd_waiter_sem.reserve();
/*      */                 }
/*      */                 
/* 1050 */                 PluginInstallerImpl.6.this.val$listener_maybe_null.completed();
/*      */               }
/*      */             }.start();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/* 1057 */       inst.start();
/*      */       
/* 1059 */       return inst;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */       PluginException pe;
/*      */       PluginException pe;
/* 1065 */       if ((e instanceof PluginException))
/*      */       {
/* 1067 */         pe = (PluginException)e;
/*      */       }
/*      */       else
/*      */       {
/* 1071 */         pe = new PluginException("Uninstall failed", e);
/*      */       }
/*      */       
/* 1074 */       if (listener_maybe_null != null)
/*      */       {
/* 1076 */         listener_maybe_null.failed(pe);
/*      */       }
/*      */       
/* 1079 */       throw pe;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected PluginInterface getAlreadyInstalledPlugin(String id)
/*      */   {
/* 1087 */     return getPluginManager().getPluginInterfaceByID(id, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestInstall(String reason, InstallablePlugin plugin)
/*      */     throws PluginException
/*      */   {
/* 1098 */     for (PluginInstallerListener listener : this.listeners) {
/*      */       try
/*      */       {
/* 1101 */         if (listener.installRequest(reason, plugin))
/*      */         {
/* 1103 */           return;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1107 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1111 */     throw new PluginException("No listeners registered to perform installation of '" + plugin.getName() + " (" + reason + ")");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(PluginInstallerListener l)
/*      */   {
/* 1118 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(PluginInstallerListener l)
/*      */   {
/* 1125 */     this.listeners.remove(l);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/installer/PluginInstallerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */