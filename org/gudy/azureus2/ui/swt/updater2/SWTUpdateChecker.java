/*     */ package org.gudy.azureus2.ui.swt.updater2;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
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
/*     */ public class SWTUpdateChecker
/*     */   implements UpdatableComponent
/*     */ {
/*  58 */   private static final LogIDs LOGID = LogIDs.GUI;
/*     */   
/*  60 */   private static final String OSX_APP = "/" + SystemProperties.getApplicationName() + ".app";
/*     */   
/*     */ 
/*     */   public static void initialize()
/*     */   {
/*  65 */     PluginInitializer.getDefaultInterface().getUpdateManager().registerUpdatableComponent(new SWTUpdateChecker(), true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkForUpdate(final UpdateChecker checker)
/*     */   {
/*     */     try
/*     */     {
/*  73 */       SWTVersionGetter versionGetter = new SWTVersionGetter(checker);
/*     */       
/*  75 */       boolean update_required = (System.getProperty("azureus.skipSWTcheck") == null) && (versionGetter.needsUpdate());
/*     */       
/*  77 */       if (update_required)
/*     */       {
/*  79 */         int update_prevented_version = COConfigurationManager.getIntParameter("swt.update.prevented.version", -1);
/*     */         try
/*     */         {
/*  82 */           URL swt_url = SWT.class.getClassLoader().getResource("org/eclipse/swt/SWT.class");
/*     */           
/*  84 */           if (swt_url != null)
/*     */           {
/*  86 */             String url_str = swt_url.toExternalForm();
/*     */             
/*  88 */             if (url_str.startsWith("jar:file:"))
/*     */             {
/*  90 */               File jar_file = FileUtil.getJarFileFromURL(url_str);
/*     */               
/*     */               String expected_location;
/*     */               String expected_location;
/*  94 */               if (Constants.isOSX)
/*     */               {
/*  96 */                 expected_location = checker.getCheckInstance().getManager().getInstallDir() + OSX_APP + "/Contents/Resources/Java";
/*     */               }
/*     */               else
/*     */               {
/* 100 */                 expected_location = checker.getCheckInstance().getManager().getInstallDir();
/*     */               }
/*     */               
/* 103 */               File expected_dir = new File(expected_location);
/*     */               
/* 105 */               File jar_file_dir = jar_file.getParentFile();
/*     */               
/*     */ 
/*     */ 
/* 109 */               if ((expected_dir.exists()) && (jar_file_dir.exists()))
/*     */               {
/* 111 */                 expected_dir = expected_dir.getCanonicalFile();
/* 112 */                 jar_file_dir = jar_file_dir.getCanonicalFile();
/*     */                 
/* 114 */                 if (expected_dir.equals(jar_file_dir))
/*     */                 {
/*     */ 
/*     */ 
/* 118 */                   if (update_prevented_version != -1)
/*     */                   {
/* 120 */                     update_prevented_version = -1;
/*     */                     
/* 122 */                     COConfigurationManager.setParameter("swt.update.prevented.version", update_prevented_version);
/*     */                   }
/*     */                   
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/* 129 */                   String alert = MessageText.getString("swt.alert.cant.update", new String[] { String.valueOf(versionGetter.getCurrentVersion()), String.valueOf(versionGetter.getLatestVersion()), jar_file_dir.toString(), expected_dir.toString() });
/*     */                   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 138 */                   checker.reportProgress(alert);
/*     */                   
/* 140 */                   long last_prompt = COConfigurationManager.getLongParameter("swt.update.prevented.version.time", 0L);
/* 141 */                   long now = SystemTime.getCurrentTime();
/*     */                   
/* 143 */                   boolean force = (now < last_prompt) || (now - last_prompt > 604800000L);
/*     */                   
/* 145 */                   if (!checker.getCheckInstance().isAutomatic())
/*     */                   {
/* 147 */                     force = true;
/*     */                   }
/*     */                   
/* 150 */                   if ((force) || (update_prevented_version != versionGetter.getCurrentVersion()))
/*     */                   {
/* 152 */                     Logger.log(new LogAlert(true, 3, alert));
/*     */                     
/* 154 */                     update_prevented_version = versionGetter.getCurrentVersion();
/*     */                     
/* 156 */                     COConfigurationManager.setParameter("swt.update.prevented.version", update_prevented_version);
/* 157 */                     COConfigurationManager.setParameter("swt.update.prevented.version.time", now);
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 165 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/* 168 */         if (update_prevented_version == versionGetter.getCurrentVersion())
/*     */         {
/* 170 */           Logger.log(new LogEvent(LOGID, 3, "SWT update aborted due to previously reported issues regarding its install location"));
/*     */           
/* 172 */           checker.failed();
/*     */           
/* 174 */           checker.getCheckInstance().cancel(); return;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 179 */         String[] mirrors = versionGetter.getMirrors();
/*     */         
/* 181 */         ResourceDownloader swtDownloader = null;
/*     */         
/* 183 */         ResourceDownloaderFactory factory = ResourceDownloaderFactoryImpl.getSingleton();
/* 184 */         List<ResourceDownloader> downloaders = new ArrayList();
/*     */         
/* 186 */         for (int i = 0; i < mirrors.length; i++) {
/*     */           try {
/* 188 */             downloaders.add(factory.getSuffixBasedDownloader(factory.create(new URL(mirrors[i]))));
/*     */           }
/*     */           catch (MalformedURLException e) {
/* 191 */             if (Logger.isEnabled()) {
/* 192 */               Logger.log(new LogEvent(LOGID, 1, "Cannot use URL " + mirrors[i] + " (not valid)"));
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 197 */         for (int i = 0; i < mirrors.length; i++) {
/*     */           try {
/* 199 */             downloaders.add(factory.getSuffixBasedDownloader(factory.createWithAutoPluginProxy(new URL(mirrors[i]))));
/*     */           }
/*     */           catch (MalformedURLException e) {}
/*     */         }
/*     */         
/* 204 */         ResourceDownloader[] resourceDownloaders = (ResourceDownloader[])downloaders.toArray(new ResourceDownloader[downloaders.size()]);
/*     */         
/*     */ 
/*     */ 
/* 208 */         swtDownloader = factory.getAlternateDownloader(resourceDownloaders);
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 213 */           swtDownloader.getSize();
/*     */         }
/*     */         catch (ResourceDownloaderException e)
/*     */         {
/* 217 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/* 220 */         String extra = "";
/*     */         
/* 222 */         if ((Constants.isWindows) && (Constants.is64Bit))
/*     */         {
/* 224 */           extra = " (64-bit)";
/*     */         }
/*     */         
/* 227 */         final Update update = checker.addUpdate("SWT Library for " + versionGetter.getPlatform() + extra, new String[] { "SWT is the graphical library used by " + Constants.APP_NAME }, "" + versionGetter.getCurrentVersion(), "" + versionGetter.getLatestVersion(), swtDownloader, 2);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */         update.setDescriptionURL(versionGetter.getInfoURL());
/*     */         
/* 238 */         swtDownloader.addListener(new ResourceDownloaderAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */           {
/*     */ 
/*     */ 
/* 247 */             return SWTUpdateChecker.this.processData(checker, update, downloader, data);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */           {
/* 255 */             Debug.out(downloader.getName() + " failed", e);
/*     */             
/* 257 */             update.complete(false);
/*     */           }
/*     */         });
/*     */       }
/*     */     } catch (Throwable e) {
/* 262 */       Logger.log(new LogAlert(false, "SWT Version check failed", e));
/*     */       
/*     */ 
/* 265 */       checker.failed();
/*     */     }
/*     */     finally
/*     */     {
/* 269 */       checker.completed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean processData(UpdateChecker checker, Update update, ResourceDownloader rd, InputStream data)
/*     */   {
/* 281 */     ZipInputStream zip = null;
/*     */     try
/*     */     {
/* 284 */       data = update.verifyData(data, true);
/*     */       
/* 286 */       rd.reportActivity("Data verified successfully");
/*     */       
/* 288 */       UpdateInstaller installer = checker.createInstaller();
/*     */       
/* 290 */       zip = new ZipInputStream(data);
/*     */       
/* 292 */       ZipEntry entry = null;
/*     */       
/* 294 */       while ((entry = zip.getNextEntry()) != null)
/*     */       {
/* 296 */         String name = entry.getName();
/*     */         
/*     */ 
/*     */ 
/* 300 */         if (name.endsWith(".jar"))
/*     */         {
/* 302 */           installer.addResource(name, zip, false);
/*     */           
/* 304 */           if (Constants.isOSX)
/*     */           {
/* 306 */             installer.addMoveAction(name, installer.getInstallDir() + OSX_APP + "/Contents/Resources/Java/" + name);
/*     */           }
/*     */           else
/*     */           {
/* 310 */             installer.addMoveAction(name, installer.getInstallDir() + File.separator + name);
/*     */           }
/* 312 */         } else if ((name.endsWith(".jnilib")) && (Constants.isOSX))
/*     */         {
/*     */ 
/*     */ 
/* 316 */           installer.addResource(name, zip, false);
/*     */           
/* 318 */           installer.addMoveAction(name, installer.getInstallDir() + OSX_APP + "/Contents/Resources/Java/dll/" + name);
/*     */         }
/* 320 */         else if (name.equals("java_swt"))
/*     */         {
/*     */ 
/*     */ 
/* 324 */           installer.addResource(name, zip, false);
/*     */           
/* 326 */           installer.addMoveAction(name, installer.getInstallDir() + OSX_APP + "/Contents/MacOS/" + name);
/*     */           
/* 328 */           installer.addChangeRightsAction("755", installer.getInstallDir() + OSX_APP + "/Contents/MacOS/" + name);
/*     */         }
/* 330 */         else if ((name.endsWith(".dll")) || (name.endsWith(".so")) || (name.contains(".so.")))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 335 */           installer.addResource(name, zip, false);
/*     */           
/* 337 */           installer.addMoveAction(name, installer.getInstallDir() + File.separator + name);
/*     */         }
/* 339 */         else if ((!name.equals("javaw.exe.manifest")) && (!name.equals("azureus.sig")))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 344 */           Debug.outNoStack("SWTUpdate: ignoring zip entry '" + name + "'");
/*     */         }
/*     */       }
/*     */       
/* 348 */       update.complete(true);
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
/* 368 */       return true;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 352 */       update.complete(false);
/*     */       
/* 354 */       Logger.log(new LogAlert(false, "SWT Update failed", e));
/*     */       
/* 356 */       return 0;
/*     */     } finally {
/* 358 */       if (zip != null) {
/*     */         try
/*     */         {
/* 361 */           zip.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 374 */     return "SWT library";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumCheckTime()
/*     */   {
/* 380 */     return 30;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/updater2/SWTUpdateChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */