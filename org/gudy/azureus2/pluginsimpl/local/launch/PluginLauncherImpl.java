/*     */ package org.gudy.azureus2.pluginsimpl.local.launch;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.launcher.Launcher;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.plugins.LaunchablePlugin;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannelListener;
/*     */ import org.gudy.azureus2.pluginsimpl.PluginUtils;
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
/*     */ public class PluginLauncherImpl
/*     */ {
/*  42 */   private static Map preloaded_plugins = new HashMap();
/*     */   
/*     */ 
/*     */   private static void main(String[] args)
/*     */   {
/*  47 */     launch(args);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void launch(String[] args)
/*     */   {
/*  54 */     if (Launcher.checkAndLaunch(PluginLauncherImpl.class, args)) {
/*  55 */       return;
/*     */     }
/*     */     
/*  58 */     COConfigurationManager.preInitialise();
/*     */     
/*  60 */     final LoggerChannelListener listener = new LoggerChannelListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void messageLogged(int type, String content)
/*     */       {
/*     */ 
/*     */ 
/*  68 */         log(content, false);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void messageLogged(String str, Throwable error)
/*     */       {
/*  76 */         log(str, true);
/*     */         
/*  78 */         StringWriter sw = new StringWriter();
/*     */         
/*  80 */         PrintWriter pw = new PrintWriter(sw);
/*     */         
/*  82 */         error.printStackTrace(pw);
/*     */         
/*  84 */         pw.flush();
/*     */         
/*  86 */         log(sw.toString(), true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       protected synchronized void log(String str, boolean stdout)
/*     */       {
/*  94 */         File log_file = PluginLauncherImpl.getApplicationFile("launch.log");
/*     */         
/*  96 */         PrintWriter pw = null;
/*     */         try
/*     */         {
/*  99 */           pw = new PrintWriter(new FileWriter(log_file, true));
/*     */           
/* 101 */           if (str.endsWith("\n"))
/*     */           {
/* 103 */             if (stdout) {
/* 104 */               System.err.print("PluginLauncher: " + str);
/*     */             }
/*     */             
/* 107 */             pw.print(str);
/*     */           }
/*     */           else
/*     */           {
/* 111 */             if (stdout) {
/* 112 */               System.err.println("PluginLauncher: " + str);
/*     */             }
/*     */             
/* 115 */             pw.println(str);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Throwable e) {}finally
/*     */         {
/* 122 */           if (pw != null)
/*     */           {
/* 124 */             pw.close();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 129 */     };
/* 130 */     LaunchablePlugin[] launchables = findLaunchablePlugins(listener);
/*     */     
/* 132 */     if (launchables.length == 0)
/*     */     {
/* 134 */       listener.messageLogged(3, "No launchable plugins found");
/*     */       
/* 136 */       return;
/*     */     }
/* 138 */     if (launchables.length > 1)
/*     */     {
/* 140 */       listener.messageLogged(3, "Multiple launchable plugins found, running first");
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 146 */       SystemProperties.setApplicationEntryPoint("org.gudy.azureus2.plugins.PluginLauncher");
/*     */       
/* 148 */       launchables[0].setDefaults(args);
/*     */       
/*     */ 
/*     */ 
/* 152 */       if (PluginSingleInstanceHandler.process(listener, args))
/*     */       {
/* 154 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 159 */       Thread core_thread = new Thread("PluginLauncher")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 168 */             Thread.sleep(500L);
/*     */             
/* 170 */             AzureusCore azureus_core = AzureusCoreFactory.create();
/*     */             
/* 172 */             azureus_core.start();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 176 */             listener.messageLogged("PluginLauncher: launch fails", e);
/*     */           }
/*     */           
/*     */         }
/* 180 */       };
/* 181 */       core_thread.setDaemon(true);
/*     */       
/* 183 */       core_thread.start();
/*     */       
/* 185 */       boolean restart = false;
/*     */       
/* 187 */       boolean process_succeeded = false;
/*     */       try
/*     */       {
/* 190 */         restart = launchables[0].process();
/*     */         
/* 192 */         process_succeeded = true;
/*     */       }
/*     */       finally
/*     */       {
/*     */         try {
/* 197 */           if (restart)
/*     */           {
/* 199 */             AzureusCoreFactory.getSingleton().restart();
/*     */           }
/*     */           else
/*     */           {
/* 203 */             AzureusCoreFactory.getSingleton().stop();
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 209 */           if (process_succeeded)
/*     */           {
/* 211 */             throw e;
/*     */           }
/*     */         }
/*     */       }
/*     */       return;
/*     */     }
/*     */     catch (Throwable e) {
/* 218 */       listener.messageLogged("PluginLauncher: launch fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static LaunchablePlugin[] findLaunchablePlugins(LoggerChannelListener listener)
/*     */   {
/* 229 */     List res = new ArrayList();
/*     */     
/* 231 */     File app_dir = getApplicationFile("plugins");
/*     */     
/* 233 */     if ((!app_dir.exists()) && (app_dir.isDirectory()))
/*     */     {
/* 235 */       listener.messageLogged(3, "Application dir '" + app_dir + "' not found");
/*     */       
/* 237 */       return new LaunchablePlugin[0];
/*     */     }
/*     */     
/* 240 */     File[] plugins = app_dir.listFiles();
/*     */     
/* 242 */     if ((plugins == null) || (plugins.length == 0))
/*     */     {
/* 244 */       listener.messageLogged(3, "Application dir '" + app_dir + "' empty");
/*     */       
/* 246 */       return new LaunchablePlugin[0];
/*     */     }
/*     */     
/* 249 */     for (int i = 0; i < plugins.length; i++)
/*     */     {
/* 251 */       File plugin_dir = plugins[i];
/*     */       
/* 253 */       if (plugin_dir.isDirectory())
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/*     */ 
/* 260 */           ClassLoader classLoader = PluginLauncherImpl.class.getClassLoader();
/*     */           
/* 262 */           ClassLoader root_cl = classLoader;
/*     */           
/* 264 */           File[] contents = plugin_dir.listFiles();
/*     */           
/* 266 */           if ((contents == null) || (contents.length != 0))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 273 */             String[] plugin_version = { null };
/* 274 */             String[] plugin_id = { null };
/*     */             
/* 276 */             contents = getHighestJarVersions(contents, plugin_version, plugin_id, true);
/*     */             
/* 278 */             for (int j = 0; j < contents.length; j++)
/*     */             {
/* 280 */               classLoader = addFileToClassPath(root_cl, classLoader, contents[j]);
/*     */             }
/*     */             
/* 283 */             Properties props = new Properties();
/*     */             
/* 285 */             File properties_file = new File(plugin_dir, "plugin.properties");
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 290 */             if (properties_file.exists())
/*     */             {
/* 292 */               FileInputStream fis = null;
/*     */               try
/*     */               {
/* 295 */                 fis = new FileInputStream(properties_file);
/*     */                 
/* 297 */                 props.load(fis);
/*     */               }
/*     */               finally
/*     */               {
/* 301 */                 if (fis != null)
/*     */                 {
/* 303 */                   fis.close();
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/* 308 */             else if ((classLoader instanceof URLClassLoader))
/*     */             {
/* 310 */               URLClassLoader current = (URLClassLoader)classLoader;
/*     */               
/* 312 */               URL url = current.findResource("plugin.properties");
/*     */               
/* 314 */               if (url != null)
/*     */               {
/* 316 */                 props.load(url.openStream());
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 321 */             String plugin_class = (String)props.get("plugin.class");
/*     */             
/*     */ 
/*     */ 
/* 325 */             if ((plugin_class == null) || (plugin_class.indexOf(';') == -1))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 330 */               Class c = classLoader.loadClass(plugin_class);
/*     */               
/* 332 */               Plugin plugin = (Plugin)c.newInstance();
/*     */               
/* 334 */               if ((plugin instanceof LaunchablePlugin))
/*     */               {
/* 336 */                 preloaded_plugins.put(plugin_class, plugin);
/*     */                 
/* 338 */                 res.add(plugin);
/*     */               }
/*     */             }
/*     */           }
/* 342 */         } catch (Throwable e) { listener.messageLogged("Load of plugin in '" + plugin_dir + "' fails", e);
/*     */         }
/*     */       }
/*     */     }
/* 346 */     LaunchablePlugin[] x = new LaunchablePlugin[res.size()];
/*     */     
/* 348 */     res.toArray(x);
/*     */     
/* 350 */     return x;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Plugin getPreloadedPlugin(String cla)
/*     */   {
/* 357 */     return (Plugin)preloaded_plugins.get(cla);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static File getApplicationFile(String filename)
/*     */   {
/* 364 */     return FileUtil.getApplicationFile(filename);
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
/*     */   public static File[] getHighestJarVersions(File[] files, String[] version_out, String[] id_out, boolean discard_non_versioned_when_versioned_found)
/*     */   {
/* 377 */     List res = new ArrayList();
/* 378 */     Map version_map = new HashMap();
/*     */     
/* 380 */     for (int i = 0; i < files.length; i++)
/*     */     {
/* 382 */       File f = files[i];
/*     */       
/* 384 */       String name = f.getName().toLowerCase();
/*     */       
/* 386 */       if (name.endsWith(".jar"))
/*     */       {
/* 388 */         int cvs_pos = name.lastIndexOf("_cvs");
/*     */         
/*     */         int sep_pos;
/*     */         int sep_pos;
/* 392 */         if (cvs_pos <= 0) {
/* 393 */           sep_pos = name.lastIndexOf("_");
/*     */         } else {
/* 395 */           sep_pos = name.lastIndexOf("_", cvs_pos - 1);
/*     */         }
/* 397 */         if ((sep_pos == -1) || (sep_pos == name.length() - 1) || (!Character.isDigit(name.charAt(sep_pos + 1))))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 403 */           res.add(f);
/*     */         }
/*     */         else
/*     */         {
/* 407 */           String prefix = name.substring(0, sep_pos);
/*     */           
/* 409 */           String version = name.substring(sep_pos + 1, cvs_pos <= 0 ? name.length() - 4 : cvs_pos);
/*     */           
/* 411 */           String prev_version = (String)version_map.get(prefix);
/*     */           
/* 413 */           if (prev_version == null)
/*     */           {
/* 415 */             version_map.put(prefix, version);
/*     */ 
/*     */ 
/*     */           }
/* 419 */           else if (PluginUtils.comparePluginVersions(prev_version, version) < 0)
/*     */           {
/* 421 */             version_map.put(prefix, version);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 432 */     if ((version_map.size() > 0) && (discard_non_versioned_when_versioned_found))
/*     */     {
/* 434 */       res.clear();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 441 */     if (version_map.containsKey("azrating"))
/*     */     {
/* 443 */       version_map.remove("rating");
/*     */     }
/*     */     
/* 446 */     Iterator it = version_map.keySet().iterator();
/*     */     
/* 448 */     while (it.hasNext())
/*     */     {
/* 450 */       String prefix = (String)it.next();
/* 451 */       String version = (String)version_map.get(prefix);
/*     */       
/* 453 */       String target = prefix + "_" + version;
/*     */       
/* 455 */       version_out[0] = version;
/* 456 */       id_out[0] = prefix;
/*     */       
/* 458 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 460 */         File f = files[i];
/*     */         
/* 462 */         String lc_name = f.getName().toLowerCase();
/*     */         
/* 464 */         if ((lc_name.equals(target + ".jar")) || (lc_name.equals(target + "_cvs.jar")))
/*     */         {
/*     */ 
/* 467 */           res.add(f);
/*     */           
/* 469 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 476 */     File[] res_array = new File[res.size()];
/*     */     
/* 478 */     res.toArray(res_array);
/*     */     
/* 480 */     return res_array;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassLoader addFileToClassPath(ClassLoader root, ClassLoader classLoader, File f)
/*     */   {
/* 489 */     if ((f.exists()) && (!f.isDirectory()) && (f.getName().endsWith(".jar")))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 495 */         classLoader = extendClassLoader(root, classLoader, f.toURL());
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */ 
/* 501 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 505 */     return classLoader;
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
/*     */   public static ClassLoader extendClassLoader(ClassLoader root, ClassLoader classLoader, URL url)
/*     */   {
/* 519 */     if ((classLoader instanceof URLClassLoader))
/*     */     {
/* 521 */       URL[] old = ((URLClassLoader)classLoader).getURLs();
/*     */       
/* 523 */       URL[] new_urls = new URL[old.length + 1];
/*     */       
/* 525 */       System.arraycopy(old, 0, new_urls, 1, old.length);
/*     */       
/* 527 */       new_urls[0] = url;
/*     */       
/* 529 */       classLoader = new URLClassLoader(new_urls, classLoader == root ? classLoader : classLoader.getParent());
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 536 */       classLoader = new URLClassLoader(new URL[] { url }, classLoader);
/*     */     }
/*     */     
/* 539 */     return classLoader;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/launch/PluginLauncherImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */