/*      */ package org.gudy.azureus2.update;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.GeneralUtils;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctions.actionListener;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipInputStream;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*      */ import org.gudy.azureus2.core3.util.AEVerifier;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.platform.win32.access.AEWin32Access;
/*      */ import org.gudy.azureus2.platform.win32.access.AEWin32Manager;
/*      */ import org.gudy.azureus2.plugins.Plugin;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*      */ import org.gudy.azureus2.plugins.update.Update;
/*      */ import org.gudy.azureus2.plugins.update.UpdateCheckInstance;
/*      */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*      */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*      */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*      */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderDelayedFactory;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*      */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CoreUpdateChecker
/*      */   implements Plugin, UpdatableComponent
/*      */ {
/*      */   public static final String LATEST_VERSION_PROPERTY = "latest_version";
/*      */   public static final String MESSAGE_PROPERTY = "message";
/*      */   public static final int RD_GET_DETAILS_RETRIES = 3;
/*      */   public static final int RD_GET_MIRRORS_RETRIES = 3;
/*      */   public static final int RD_SIZE_RETRIES = 3;
/*      */   public static final int RD_SIZE_TIMEOUT = 10000;
/*      */   protected static CoreUpdateChecker singleton;
/*      */   protected PluginInterface plugin_interface;
/*      */   protected ResourceDownloaderFactory rdf;
/*      */   protected LoggerChannel log;
/*      */   protected ResourceDownloaderListener rd_logger;
/*   74 */   protected boolean first_check = true;
/*      */   
/*      */ 
/*      */   public static void doUsageStats()
/*      */   {
/*   79 */     singleton.doUsageStatsSupport();
/*      */   }
/*      */   
/*      */ 
/*      */   public CoreUpdateChecker()
/*      */   {
/*   85 */     singleton = this;
/*      */   }
/*      */   
/*      */   protected void doUsageStatsSupport()
/*      */   {
/*      */     try
/*      */     {
/*   92 */       Map decoded = VersionCheckClient.getSingleton().getVersionCheckInfo(this.first_check ? "us" : "up");
/*      */       
/*      */ 
/*   95 */       displayUserMessage(decoded);
/*      */     }
/*      */     finally
/*      */     {
/*   99 */       this.first_check = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initialize(PluginInterface _plugin_interface)
/*      */   {
/*  108 */     this.plugin_interface = _plugin_interface;
/*      */     
/*  110 */     this.plugin_interface.getPluginProperties().setProperty("plugin.name", "Core Updater");
/*      */     
/*  112 */     this.log = this.plugin_interface.getLogger().getChannel("CoreUpdater");
/*      */     
/*  114 */     this.rd_logger = new ResourceDownloaderAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void reportActivity(ResourceDownloader downloader, String activity)
/*      */       {
/*      */ 
/*      */ 
/*  122 */         CoreUpdateChecker.this.log.log(activity);
/*      */       }
/*      */       
/*  125 */     };
/*  126 */     Properties props = this.plugin_interface.getPluginProperties();
/*      */     
/*  128 */     props.setProperty("plugin.version", this.plugin_interface.getAzureusVersion());
/*      */     
/*  130 */     this.rdf = this.plugin_interface.getUtilities().getResourceDownloaderFactory();
/*      */     
/*  132 */     this.plugin_interface.getUpdateManager().registerUpdatableComponent(this, true);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  138 */     return "Azureus Core";
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaximumCheckTime()
/*      */   {
/*  144 */     return 30;
/*      */   }
/*      */   
/*      */ 
/*      */   public void checkForUpdate(final UpdateChecker checker)
/*      */   {
/*      */     try
/*      */     {
/*  152 */       String current_version = this.plugin_interface.getAzureusVersion();
/*      */       
/*  154 */       this.log.log("Update check starts: current = " + current_version);
/*      */       
/*  156 */       Map decoded = VersionCheckClient.getSingleton().getVersionCheckInfo(this.first_check ? "us" : "up");
/*      */       
/*      */ 
/*      */ 
/*  160 */       displayUserMessage(decoded);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  165 */       if (decoded.isEmpty()) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  170 */       byte[] b_version = (byte[])decoded.get("version");
/*      */       
/*  172 */       if (b_version != null)
/*      */       {
/*  174 */         String latest_version = new String(b_version);
/*      */         
/*  176 */         this.plugin_interface.getPluginProperties().setProperty("latest_version", latest_version);
/*      */       }
/*      */       else
/*      */       {
/*  180 */         throw new Exception("No version found in reply");
/*      */       }
/*      */       String latest_version;
/*  183 */       byte[] b_filename = (byte[])decoded.get("filename");
/*      */       String latest_file_name;
/*  185 */       if (b_filename != null)
/*      */       {
/*  187 */         latest_file_name = new String(b_filename);
/*      */       }
/*      */       else
/*      */       {
/*  191 */         throw new Exception("No update file details in reply");
/*      */       }
/*      */       
/*      */ 
/*      */       String latest_file_name;
/*      */       
/*      */ 
/*  198 */       String msg = "Core: latest_version = '" + latest_version + "', file = '" + latest_file_name + "'";
/*      */       
/*      */ 
/*      */ 
/*      */       URL full_download_url;
/*      */       
/*      */ 
/*  205 */       if (latest_file_name.startsWith("http"))
/*      */       {
/*      */         try {
/*  208 */           full_download_url = new URL(latest_file_name);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  212 */           URL full_download_url = null;
/*      */           
/*  214 */           this.log.log(e);
/*      */         }
/*      */         
/*  217 */         int pos = latest_file_name.lastIndexOf('/');
/*      */         
/*  219 */         latest_file_name = latest_file_name.substring(pos + 1);
/*      */       }
/*      */       else
/*      */       {
/*  223 */         full_download_url = null;
/*      */       }
/*      */       
/*  226 */       checker.reportProgress(msg);
/*      */       
/*  228 */       this.log.log(msg);
/*      */       
/*  230 */       if (!shouldUpdate(current_version, latest_version)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  235 */       final String f_latest_version = latest_version;
/*  236 */       final String f_latest_file_name = latest_file_name;
/*      */       
/*      */       ResourceDownloader top_downloader;
/*      */       ResourceDownloader top_downloader;
/*  240 */       if (full_download_url == null)
/*      */       {
/*      */ 
/*      */ 
/*  244 */         ResourceDownloader[] primary_mirrors = getPrimaryDownloaders(latest_file_name);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  251 */         ResourceDownloader random_primary_mirrors = this.rdf.getRandomDownloader(primary_mirrors);
/*      */         
/*  253 */         ResourceDownloader backup_downloader = this.rdf.create(new ResourceDownloaderDelayedFactory()
/*      */         {
/*      */ 
/*      */ 
/*      */           public ResourceDownloader create()
/*      */           {
/*      */ 
/*  260 */             ResourceDownloader[] backup_mirrors = CoreUpdateChecker.this.getBackupDownloaders(f_latest_file_name);
/*      */             
/*  262 */             return CoreUpdateChecker.this.rdf.getRandomDownloader(backup_mirrors);
/*      */           }
/*      */           
/*  265 */         });
/*  266 */         top_downloader = this.rdf.getAlternateDownloader(new ResourceDownloader[] { random_primary_mirrors, backup_downloader });
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  276 */         ResourceDownloader full_rd = this.rdf.create(full_download_url);
/*  277 */         ResourceDownloader full_ap_rd = this.rdf.createWithAutoPluginProxy(full_download_url);
/*      */         
/*  279 */         full_rd = this.rdf.getSuffixBasedDownloader(full_rd);
/*      */         
/*  281 */         ResourceDownloader primary_downloader = this.rdf.create(new ResourceDownloaderDelayedFactory()
/*      */         {
/*      */ 
/*      */ 
/*      */           public ResourceDownloader create()
/*      */           {
/*      */ 
/*  288 */             ResourceDownloader[] primary_mirrors = CoreUpdateChecker.this.getPrimaryDownloaders(f_latest_file_name);
/*      */             
/*  290 */             return CoreUpdateChecker.this.rdf.getRandomDownloader(primary_mirrors);
/*      */           }
/*      */           
/*  293 */         });
/*  294 */         ResourceDownloader backup_downloader = this.rdf.create(new ResourceDownloaderDelayedFactory()
/*      */         {
/*      */ 
/*      */ 
/*      */           public ResourceDownloader create()
/*      */           {
/*      */ 
/*  301 */             ResourceDownloader[] backup_mirrors = CoreUpdateChecker.this.getBackupDownloaders(f_latest_file_name);
/*      */             
/*  303 */             return CoreUpdateChecker.this.rdf.getRandomDownloader(backup_mirrors);
/*      */           }
/*      */           
/*      */ 
/*  307 */         });
/*  308 */         top_downloader = this.rdf.getAlternateDownloader(new ResourceDownloader[] { full_rd, primary_downloader, backup_downloader, full_ap_rd });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  319 */       top_downloader.addListener(this.rd_logger);
/*      */       
/*      */ 
/*      */ 
/*  323 */       top_downloader.getSize();
/*      */       
/*      */ 
/*  326 */       byte[] info_b = (byte[])decoded.get("info");
/*      */       
/*  328 */       String info = null;
/*      */       
/*  330 */       if (info_b != null) {
/*      */         try
/*      */         {
/*  333 */           info = new String(info_b, "UTF-8");
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  337 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  341 */       byte[] info_url_bytes = (byte[])decoded.get("info_url");
/*      */       
/*  343 */       String info_url = null;
/*      */       
/*  345 */       if (info_url_bytes != null) {
/*      */         try
/*      */         {
/*  348 */           info_url = new String(info_url_bytes);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  352 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */       
/*  356 */       if ((info != null) || (info_url != null))
/*      */       {
/*      */         String check;
/*      */         String check;
/*  360 */         if (info == null)
/*      */         {
/*  362 */           check = info_url;
/*      */         } else { String check;
/*  364 */           if (info_url == null)
/*      */           {
/*  366 */             check = info;
/*      */           }
/*      */           else
/*      */           {
/*  370 */             check = info + "|" + info_url;
/*      */           }
/*      */         }
/*  373 */         byte[] sig = (byte[])decoded.get("info_sig");
/*      */         
/*  375 */         boolean ok = false;
/*      */         
/*  377 */         if (sig == null)
/*      */         {
/*  379 */           org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "info signature check failed - missing signature"));
/*      */         }
/*      */         else {
/*      */           try
/*      */           {
/*  384 */             AEVerifier.verifyData(check, sig);
/*      */             
/*  386 */             ok = true;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  390 */             org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "info signature check failed", e));
/*      */           }
/*      */         }
/*      */         
/*  394 */         if (!ok)
/*      */         {
/*  396 */           info = null;
/*  397 */           info_url = null;
/*      */         }
/*      */       }
/*      */       
/*      */       String[] desc;
/*      */       String[] desc;
/*  403 */       if (info == null)
/*      */       {
/*  405 */         desc = new String[] { "Core Azureus Version" };
/*      */       }
/*      */       else
/*      */       {
/*  409 */         desc = new String[] { "Core Azureus Version", info };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  414 */       final Update update = checker.addUpdate("Core Azureus Version", desc, current_version, latest_version, top_downloader, 2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  423 */       if (info_url != null)
/*      */       {
/*  425 */         update.setDescriptionURL(info_url);
/*      */       }
/*      */       
/*  428 */       top_downloader.addListener(new ResourceDownloaderAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean completed(ResourceDownloader downloader, InputStream data)
/*      */         {
/*      */ 
/*      */ 
/*  436 */           CoreUpdateChecker.this.installUpdate(checker, update, downloader, f_latest_file_name, f_latest_version, data);
/*      */           
/*  438 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*      */         {
/*  448 */           update.complete(false);
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (Throwable e) {
/*  453 */       this.log.log(e);
/*      */       
/*  455 */       Debug.printStackTrace(e);
/*      */       
/*  457 */       checker.reportProgress("Failed to check for core update: " + Debug.getNestedExceptionMessage(e));
/*      */       
/*  459 */       checker.failed();
/*      */     }
/*      */     finally
/*      */     {
/*  463 */       checker.completed();
/*      */       
/*  465 */       this.first_check = false;
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
/*      */ 
/*      */ 
/*      */   private void displayUserMessage(Map reply)
/*      */   {
/*      */     try
/*      */     {
/*  483 */       Iterator it = reply.keySet().iterator();
/*      */       
/*  485 */       while (it.hasNext())
/*      */       {
/*  487 */         String key = (String)it.next();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  492 */         if ((!key.startsWith("message_sig")) && (key.startsWith("message")))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  497 */           byte[] message_bytes = (byte[])reply.get(key);
/*      */           
/*  499 */           if ((message_bytes != null) && (message_bytes.length > 0))
/*      */           {
/*      */             String message;
/*      */             try
/*      */             {
/*  504 */               message = new String(message_bytes, "UTF-8");
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  508 */               message = new String(message_bytes);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  513 */             int pos = key.indexOf('_');
/*      */             String sig_key;
/*  515 */             String sig_key; if (pos == -1)
/*      */             {
/*  517 */               sig_key = "message_sig";
/*      */             }
/*      */             else
/*      */             {
/*  521 */               sig_key = "message_sig" + key.substring(pos);
/*      */             }
/*      */             
/*  524 */             String last_message_key = "CoreUpdateChecker.last" + key;
/*      */             
/*  526 */             String last = COConfigurationManager.getStringParameter(last_message_key, "");
/*      */             
/*  528 */             if (!message.equals(last))
/*      */             {
/*  530 */               boolean repeatable = false;
/*      */               
/*  532 */               byte[] signature = (byte[])reply.get(sig_key);
/*      */               
/*  534 */               if (signature == null)
/*      */               {
/*  536 */                 org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "Signature missing from message"));
/*      */                 
/*  538 */                 return;
/*      */               }
/*      */               try
/*      */               {
/*  542 */                 AEVerifier.verifyData(message, signature);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  546 */                 org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "Message signature check failed", e));
/*      */                 
/*  548 */                 return;
/*      */               }
/*      */               
/*  551 */               boolean completed = false;
/*      */               
/*  553 */               if ((message.startsWith("x:")) || (message.startsWith("y:")))
/*      */               {
/*      */ 
/*      */ 
/*  557 */                 repeatable = message.startsWith("y:");
/*      */                 try
/*      */                 {
/*  560 */                   URL jar_url = new URL(message.substring(2));
/*      */                   
/*  562 */                   if (!repeatable)
/*      */                   {
/*  564 */                     org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "Patch application requsted: url=" + jar_url));
/*      */                   }
/*      */                   
/*  567 */                   File temp_dir = AETemporaryFileHandler.createTempDir();
/*      */                   
/*  569 */                   File jar_file = new File(temp_dir, "patch.jar");
/*      */                   
/*  571 */                   InputStream is = this.rdf.create(jar_url).download();
/*      */                   try
/*      */                   {
/*  574 */                     FileUtil.copyFile(is, jar_file);
/*      */                     
/*  576 */                     is = null;
/*      */                     
/*  578 */                     AEVerifier.verifyData(jar_file);
/*      */                     
/*  580 */                     ClassLoader cl = CoreUpdateChecker.class.getClassLoader();
/*      */                     
/*  582 */                     if ((cl instanceof URLClassLoader))
/*      */                     {
/*  584 */                       URL[] old = ((URLClassLoader)cl).getURLs();
/*      */                       
/*  586 */                       URL[] new_urls = new URL[old.length + 1];
/*      */                       
/*  588 */                       System.arraycopy(old, 0, new_urls, 1, old.length);
/*      */                       
/*  590 */                       new_urls[0] = jar_file.toURL();
/*      */                       
/*  592 */                       cl = new URLClassLoader(new_urls, cl);
/*      */                     }
/*      */                     else
/*      */                     {
/*  596 */                       cl = new URLClassLoader(new URL[] { jar_file.toURL() }, cl);
/*      */                     }
/*      */                     
/*  599 */                     Class cla = cl.loadClass("org.gudy.azureus2.update.version.Patch");
/*      */                     
/*  601 */                     cla.newInstance();
/*      */                     
/*  603 */                     completed = true;
/*      */                   }
/*      */                   finally
/*      */                   {
/*  607 */                     if (is != null)
/*      */                     {
/*  609 */                       is.close();
/*      */                     }
/*      */                     
/*  612 */                     jar_file.delete();
/*      */                     
/*  614 */                     temp_dir.delete();
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/*  618 */                   if (!repeatable)
/*      */                   {
/*  620 */                     org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "Patch application failed", e));
/*      */                   }
/*      */                 }
/*  623 */               } else if ((message.startsWith("u:")) && (message.length() > 4)) {
/*      */                 try {
/*  625 */                   String type = message.substring(2, 3);
/*  626 */                   String url = message.substring(4);
/*  627 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*  628 */                   if (uif != null) {
/*  629 */                     uif.viewURL(url, null, 0.9D, 0.9D, true, type.equals("1"));
/*      */                   }
/*      */                 } catch (Throwable t) {
/*  632 */                   org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "URL message failed", t));
/*      */                 }
/*      */                 
/*  635 */                 completed = true;
/*      */               }
/*      */               else {
/*  638 */                 int alert_type = 1;
/*      */                 
/*  640 */                 String alert_text = message;
/*      */                 
/*  642 */                 boolean force = false;
/*      */                 
/*  644 */                 if (alert_text.startsWith("f:"))
/*      */                 {
/*  646 */                   force = true;
/*      */                   
/*  648 */                   alert_text = alert_text.substring(2);
/*      */                 }
/*      */                 
/*  651 */                 if (alert_text.startsWith("i:"))
/*      */                 {
/*  653 */                   alert_type = 0;
/*      */                   
/*  655 */                   alert_text = alert_text.substring(2);
/*      */                 }
/*      */                 
/*  658 */                 this.plugin_interface.getPluginProperties().setProperty("message", alert_text);
/*      */                 
/*  660 */                 if (force)
/*      */                 {
/*  662 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */                   
/*  664 */                   if (uif != null) {
/*      */                     try
/*      */                     {
/*  667 */                       uif.forceNotify(0, null, alert_text, null, null, 0);
/*      */                       
/*  669 */                       completed = true;
/*      */                     }
/*      */                     catch (Throwable e) {}
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*  677 */                 if (!completed)
/*      */                 {
/*  679 */                   org.gudy.azureus2.core3.logging.Logger.log(new LogAlert(false, alert_type, alert_text, 0));
/*      */                 }
/*      */                 
/*  682 */                 completed = true;
/*      */               }
/*      */               
/*  685 */               if (completed)
/*      */               {
/*  687 */                 if (!repeatable)
/*      */                 {
/*  689 */                   COConfigurationManager.setParameter(last_message_key, message);
/*      */                   
/*  691 */                   COConfigurationManager.save();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } catch (Throwable e) {
/*  699 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected ResourceDownloader[] getPrimaryDownloaders(String latest_file_name)
/*      */   {
/*  707 */     this.log.log("Downloading primary mirrors");
/*      */     
/*  709 */     List res = new ArrayList();
/*      */     try
/*      */     {
/*  712 */       if (latest_file_name == null)
/*      */       {
/*      */ 
/*      */ 
/*  716 */         res.add(new URL("http://plugins.vuze.com/Azureus2.jar"));
/*      */       }
/*      */       else
/*      */       {
/*  720 */         URL mirrors_url = new URL("http://prdownloads.sourceforge.net/azureus/" + latest_file_name + "?download");
/*      */         
/*  722 */         ResourceDownloader rd = this.rdf.create(mirrors_url);
/*      */         
/*  724 */         rd = this.rdf.getRetryDownloader(rd, 3);
/*      */         
/*  726 */         rd.addListener(this.rd_logger);
/*      */         
/*  728 */         String page = FileUtil.readInputStreamAsString(rd.download(), 65535);
/*      */         
/*  730 */         String pattern = "/azureus/" + latest_file_name + "?use_mirror=";
/*      */         
/*  732 */         int position = page.indexOf(pattern);
/*      */         
/*  734 */         while (position > 0)
/*      */         {
/*  736 */           int end = page.indexOf(">", position);
/*      */           
/*  738 */           if (end < 0)
/*      */           {
/*  740 */             position = -1;
/*      */           }
/*      */           else
/*      */           {
/*  744 */             String mirror = page.substring(position, end);
/*      */             
/*  746 */             if (mirror.endsWith("\""))
/*      */             {
/*  748 */               mirror = mirror.substring(0, mirror.length() - 1);
/*      */             }
/*      */             try
/*      */             {
/*  752 */               res.add(new URL("http://prdownloads.sourceforge.net" + mirror));
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  756 */               this.log.log("Invalid URL read:" + mirror, e);
/*      */             }
/*      */             
/*  759 */             position = page.indexOf(pattern, position + 1);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  765 */       this.log.log("Failed to read primary mirror list", e);
/*      */     }
/*      */     
/*  768 */     ResourceDownloader[] dls = new ResourceDownloader[res.size()];
/*      */     
/*  770 */     for (int i = 0; i < res.size(); i++)
/*      */     {
/*  772 */       URL url = (URL)res.get(i);
/*      */       
/*  774 */       this.log.log("    Primary mirror:" + url.toString());
/*      */       
/*  776 */       ResourceDownloader dl = this.rdf.create(url);
/*      */       
/*  778 */       dl = this.rdf.getMetaRefreshDownloader(dl);
/*      */       
/*      */ 
/*      */ 
/*  782 */       dl = this.rdf.getSuffixBasedDownloader(dl);
/*      */       
/*  784 */       dls[i] = dl;
/*      */     }
/*      */     
/*  787 */     return dls;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected ResourceDownloader[] getBackupDownloaders(String latest_file_name)
/*      */   {
/*  794 */     List res = new ArrayList();
/*      */     try
/*      */     {
/*  797 */       if (latest_file_name != null)
/*      */       {
/*  799 */         this.log.log("Downloading backup mirrors");
/*      */         
/*  801 */         URL mirrors_url = new URL("http://plugins.vuze.com/mirrors.php");
/*      */         
/*  803 */         ResourceDownloader rd = this.rdf.create(mirrors_url);
/*      */         
/*  805 */         rd = this.rdf.getRetryDownloader(rd, 3);
/*      */         
/*  807 */         rd.addListener(this.rd_logger);
/*      */         
/*  809 */         BufferedInputStream data = new BufferedInputStream(rd.download());
/*      */         
/*  811 */         Map decoded = BDecoder.decode(data);
/*      */         
/*  813 */         data.close();
/*      */         
/*  815 */         List mirrors = (List)decoded.get("mirrors");
/*      */         
/*  817 */         for (int i = 0; i < mirrors.size(); i++)
/*      */         {
/*  819 */           String mirror = new String((byte[])mirrors.get(i));
/*      */           
/*      */           try
/*      */           {
/*  823 */             res.add(new URL(mirror + latest_file_name));
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  828 */             this.log.log("Invalid URL read:" + mirror, e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  834 */       this.log.log("Failed to read backup mirror list", e);
/*      */     }
/*      */     
/*  837 */     ResourceDownloader[] dls = new ResourceDownloader[res.size()];
/*      */     
/*  839 */     for (int i = 0; i < res.size(); i++)
/*      */     {
/*  841 */       URL url = (URL)res.get(i);
/*      */       
/*  843 */       this.log.log("    Backup mirror:" + url.toString());
/*      */       
/*  845 */       ResourceDownloader dl = this.rdf.create(url);
/*      */       
/*      */ 
/*      */ 
/*  849 */       dl = this.rdf.getSuffixBasedDownloader(dl);
/*      */       
/*  851 */       dls[i] = dl;
/*      */     }
/*      */     
/*  854 */     return dls;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void installUpdate(UpdateChecker checker, Update update, ResourceDownloader rd, String filename, String version, InputStream data)
/*      */   {
/*      */     try
/*      */     {
/*  867 */       data = update.verifyData(data, true);
/*      */       
/*  869 */       rd.reportActivity("Data verified successfully");
/*      */       
/*  871 */       if (filename.toLowerCase().endsWith(".zip.torrent"))
/*      */       {
/*  873 */         handleZIPUpdate(checker, data);
/*      */       }
/*      */       else
/*      */       {
/*  877 */         String temp_jar_name = "Azureus2_" + version + ".jar";
/*  878 */         String target_jar_name = "Azureus2.jar";
/*      */         
/*  880 */         UpdateInstaller installer = checker.createInstaller();
/*      */         
/*  882 */         installer.addResource(temp_jar_name, data);
/*      */         
/*  884 */         if (Constants.isOSX)
/*      */         {
/*  886 */           installer.addMoveAction(temp_jar_name, installer.getInstallDir() + "/" + SystemProperties.getApplicationName() + ".app/Contents/Resources/Java/" + target_jar_name);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  891 */           installer.addMoveAction(temp_jar_name, installer.getInstallDir() + File.separator + target_jar_name);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  897 */       update.complete(true); return;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  901 */       update.complete(false);
/*      */       
/*  903 */       rd.reportActivity("Update install failed:" + e.getMessage());
/*      */     }
/*      */     finally
/*      */     {
/*  907 */       if (data != null) {
/*      */         try {
/*  909 */           data.close();
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
/*      */   protected void handleZIPUpdate(UpdateChecker checker, InputStream data)
/*      */     throws Exception
/*      */   {
/*  923 */     ZipInputStream zip = null;
/*      */     
/*  925 */     Properties update_properties = new Properties();
/*      */     
/*  927 */     File temp_dir = AETemporaryFileHandler.createTempDir();
/*      */     
/*  929 */     File update_file = null;
/*      */     try
/*      */     {
/*  932 */       zip = new ZipInputStream(data);
/*      */       
/*  934 */       ZipEntry entry = null;
/*      */       
/*  936 */       while ((entry = zip.getNextEntry()) != null)
/*      */       {
/*  938 */         String name = entry.getName().trim();
/*      */         
/*  940 */         if ((!name.equals("azureus.sig")) && (!name.endsWith("/")) && (name.length() != 0))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  945 */           if (name.equals("update.properties"))
/*      */           {
/*  947 */             update_properties.load(zip);
/*      */           }
/*      */           else
/*      */           {
/*  951 */             if (update_file != null)
/*      */             {
/*  953 */               throw new Exception("Multiple update files are not supported");
/*      */             }
/*      */             
/*  956 */             update_file = new File(temp_dir, name);
/*      */             
/*  958 */             FileUtil.copyFile(zip, update_file, false);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  963 */       if (zip != null) {
/*      */         try
/*      */         {
/*  966 */           zip.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  974 */       if (update_properties != null) {
/*      */         break label188;
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  963 */       if (zip != null) {
/*      */         try
/*      */         {
/*  966 */           zip.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  976 */     throw new Exception("Update properties missing");
/*      */     
/*      */     label188:
/*  979 */     if (update_file == null)
/*      */     {
/*  981 */       throw new Exception("Update file missing");
/*      */     }
/*      */     
/*  984 */     String info_url = update_properties.getProperty("info.url");
/*      */     
/*  986 */     if (info_url == null)
/*      */     {
/*  988 */       throw new Exception("Update property 'info.url' missing");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  994 */     String s_args = update_properties.getProperty("launch.args", "").trim();
/*      */     
/*      */     String[] args;
/*      */     final String[] args;
/*  998 */     if (s_args.length() > 0)
/*      */     {
/* 1000 */       args = GeneralUtils.splitQuotedTokens(s_args);
/*      */     }
/*      */     else
/*      */     {
/* 1004 */       args = new String[0];
/*      */     }
/*      */     
/* 1007 */     UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */     
/* 1009 */     if (uif == null)
/*      */     {
/* 1011 */       throw new Exception("Update can't proceed - UI functions unavailable");
/*      */     }
/*      */     
/* 1014 */     checker.getCheckInstance().setProperty(4, Boolean.valueOf(true));
/*      */     
/* 1016 */     final File f_update_file = update_file;
/*      */     
/* 1018 */     boolean silent = update_properties.getProperty("launch.silent", "false").equals("true");
/*      */     
/* 1020 */     if (silent)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1025 */       if (Constants.isOSX)
/*      */       {
/* 1027 */         String app_name = SystemProperties.getApplicationName();
/*      */         
/* 1029 */         if ((!app_name.equals("Vuze")) && (!app_name.equals("Azureus")))
/*      */         {
/* 1031 */           UIManager ui_manager = StaticUtilities.getUIManager(120000L);
/*      */           
/* 1033 */           String details = MessageText.getString("update.fail.app.changed", new String[] { app_name });
/*      */           
/*      */ 
/*      */ 
/* 1037 */           ui_manager.showMessageBox("update.fail.app.changed.title", "!" + details + "!", 1L);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1042 */           return;
/*      */         }
/*      */       }
/*      */       
/* 1046 */       uif.performAction(2, Boolean.valueOf(!FileUtil.canReallyWriteToAppDirectory()), new UIFunctions.actionListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void actionComplete(Object result)
/*      */         {
/*      */ 
/*      */ 
/* 1055 */           if (((Boolean)result).booleanValue())
/*      */           {
/* 1057 */             CoreUpdateChecker.this.launchUpdate(f_update_file, args);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     else {
/* 1063 */       uif.performAction(1, info_url, new UIFunctions.actionListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void actionComplete(Object result)
/*      */         {
/*      */ 
/*      */ 
/* 1072 */           if (((Boolean)result).booleanValue())
/*      */           {
/* 1074 */             CoreUpdateChecker.this.launchUpdate(f_update_file, args);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void launchUpdate(File file, String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 1090 */       if (file.getName().endsWith(".exe"))
/*      */       {
/*      */         try {
/* 1093 */           AEWin32Access accessor = AEWin32Manager.getAccessor(true);
/*      */           
/*      */ 
/*      */ 
/* 1097 */           String s_args = null;
/*      */           
/* 1099 */           if (args.length > 0)
/*      */           {
/* 1101 */             s_args = "";
/*      */             
/* 1103 */             for (String s : args)
/*      */             {
/* 1105 */               s_args = s_args + (s_args.length() == 0 ? "" : " ") + s;
/*      */             }
/*      */           }
/*      */           
/* 1109 */           accessor.shellExecute(null, file.getAbsolutePath(), s_args, SystemProperties.getApplicationPath(), 1);
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*      */ 
/*      */ 
/* 1118 */           org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "AEWin32Access failed", e));
/*      */           
/* 1120 */           if (args.length > 0)
/*      */           {
/* 1122 */             String[] s_args = new String[args.length + 1];
/*      */             
/* 1124 */             s_args[0] = file.getAbsolutePath();
/*      */             
/* 1126 */             System.arraycopy(args, 0, s_args, 1, args.length);
/*      */             
/* 1128 */             Runtime.getRuntime().exec(s_args);
/*      */           }
/*      */           else
/*      */           {
/* 1132 */             Runtime.getRuntime().exec(new String[] { file.getAbsolutePath() });
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1138 */         File dir = file.getParentFile();
/*      */         
/* 1140 */         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
/*      */         
/* 1142 */         Throwable unzip_error = null;
/*      */         
/* 1144 */         String chmod_command = findCommand("chmod");
/*      */         try
/*      */         {
/*      */           for (;;)
/*      */           {
/* 1149 */             ZipEntry entry = zis.getNextEntry();
/*      */             
/* 1151 */             if (entry != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 1156 */               if (entry.isDirectory()) {
/*      */                 continue;
/*      */               }
/*      */               
/*      */ 
/* 1161 */               String name = entry.getName();
/*      */               
/* 1163 */               FileOutputStream entry_os = null;
/* 1164 */               File entry_file = null;
/*      */               
/* 1166 */               if (!name.endsWith("/"))
/*      */               {
/* 1168 */                 entry_file = new File(dir, name.replace('/', File.separatorChar));
/*      */                 
/* 1170 */                 entry_file.getParentFile().mkdirs();
/*      */                 
/* 1172 */                 entry_os = new FileOutputStream(entry_file);
/*      */               }
/*      */               try
/*      */               {
/* 1176 */                 byte[] buffer = new byte[65536];
/*      */                 
/*      */                 for (;;)
/*      */                 {
/* 1180 */                   int len = zis.read(buffer);
/*      */                   
/* 1182 */                   if (len <= 0) {
/*      */                     break;
/*      */                   }
/*      */                   
/*      */ 
/* 1187 */                   if (entry_os != null)
/*      */                   {
/* 1189 */                     entry_os.write(buffer, 0, len);
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/* 1194 */                 if (entry_os != null)
/*      */                 {
/* 1196 */                   entry_os.close();
/*      */                   
/* 1198 */                   if ((name.endsWith(".jnilib")) || (name.endsWith("JavaApplicationStub"))) {
/*      */                     try
/*      */                     {
/* 1201 */                       String[] to_run = { chmod_command, "a+x", entry_file.getAbsolutePath() };
/*      */                       
/* 1203 */                       runCommand(to_run, true);
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 1207 */                       unzip_error = e;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 String[] to_run;
/*      */               }
/*      */               finally
/*      */               {
/* 1194 */                 if (entry_os != null)
/*      */                 {
/* 1196 */                   entry_os.close();
/*      */                   
/* 1198 */                   if ((name.endsWith(".jnilib")) || (name.endsWith("JavaApplicationStub"))) {
/*      */                     try
/*      */                     {
/* 1201 */                       to_run = new String[] { chmod_command, "a+x", entry_file.getAbsolutePath() };
/*      */                       
/* 1203 */                       runCommand(to_run, true);
/*      */                     }
/*      */                     catch (Throwable e)
/*      */                     {
/* 1207 */                       unzip_error = e;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         } finally {
/* 1215 */           zis.close();
/*      */         }
/*      */         
/* 1218 */         if (unzip_error != null)
/*      */         {
/* 1220 */           throw unzip_error;
/*      */         }
/*      */         
/* 1223 */         File[] files = dir.listFiles();
/*      */         
/* 1225 */         boolean launched = false;
/*      */         
/* 1227 */         for (File f : files)
/*      */         {
/* 1229 */           if (f.getName().endsWith(".app"))
/*      */           {
/*      */             String[] to_run;
/*      */             
/*      */ 
/*      */             String[] to_run;
/*      */             
/*      */ 
/* 1237 */             if ((args.length == 0) || (!Constants.isOSX_10_6_OrHigher))
/*      */             {
/* 1239 */               to_run = new String[] { "/bin/sh", "-c", "open \"" + f.getAbsolutePath() + "\"" };
/*      */             }
/*      */             else
/*      */             {
/* 1243 */               to_run = new String[3 + args.length];
/*      */               
/* 1245 */               to_run[0] = findCommand("open");
/* 1246 */               to_run[1] = f.getAbsolutePath();
/* 1247 */               to_run[2] = "--args";
/*      */               
/* 1249 */               System.arraycopy(args, 0, to_run, 3, args.length);
/*      */             }
/*      */             
/* 1252 */             runCommand(to_run, false);
/*      */             
/* 1254 */             launched = true;
/*      */           }
/*      */         }
/*      */         
/* 1258 */         if (!launched)
/*      */         {
/* 1260 */           throw new Exception("No .app files found in '" + dir + "'");
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1265 */       org.gudy.azureus2.core3.logging.Logger.log(new LogEvent(LogIDs.LOGGER, "Failed to launch update '" + file + "'", e));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String findCommand(String name)
/*      */   {
/* 1273 */     String[] locations = { "/bin", "/usr/bin" };
/*      */     
/* 1275 */     for (String s : locations)
/*      */     {
/* 1277 */       File f = new File(s, name);
/*      */       
/* 1279 */       if ((f.exists()) && (f.canRead()))
/*      */       {
/* 1281 */         return f.getAbsolutePath();
/*      */       }
/*      */     }
/*      */     
/* 1285 */     return name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static void runCommand(String[] command, boolean wait)
/*      */     throws Throwable
/*      */   {
/*      */     try
/*      */     {
/* 1296 */       String str = "";
/*      */       
/* 1298 */       for (String s : command)
/*      */       {
/* 1300 */         str = str + (str.length() == 0 ? "" : " ") + s;
/*      */       }
/*      */       
/* 1303 */       System.out.println("running " + str);
/*      */       
/* 1305 */       Process proc = Runtime.getRuntime().exec(command);
/*      */       
/* 1307 */       if (wait)
/*      */       {
/* 1309 */         proc.waitFor();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1313 */       System.err.println(e);
/*      */       
/* 1315 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static boolean shouldUpdate(String current_version, String latest_version)
/*      */   {
/* 1324 */     String current_base = Constants.getBaseVersion(current_version);
/* 1325 */     int current_inc = Constants.getIncrementalBuild(current_version);
/*      */     
/* 1327 */     String latest_base = Constants.getBaseVersion(latest_version);
/* 1328 */     int latest_inc = Constants.getIncrementalBuild(latest_version);
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
/* 1341 */     int major_comp = Constants.compareVersions(current_base, latest_base);
/*      */     
/* 1343 */     if ((major_comp < 0) && (latest_inc >= 0))
/*      */     {
/* 1345 */       return true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1350 */     return (major_comp == 0) && (current_inc > 0) && (latest_inc > 0) && (latest_inc > current_inc);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1357 */     String[][] tests = { { "2.4.0.0", "2.4.0.2", "true" }, { "2.4.0.1_CVS", "2.4.0.2", "true" }, { "2.4.0.1_B12", "2.4.0.2", "true" }, { "2.4.0.1_B12", "2.4.0.1_B34", "true" }, { "2.4.0.1_B12", "2.4.0.1_B6", "false" }, { "2.4.0.0", "2.4.0.1_CVS", "false" }, { "2.4.0.0", "2.4.0.1_B12", "true" }, { "2.4.0.0", "2.4.0.0", "false" }, { "2.4.0.1_CVS", "2.4.0.1_CVS", "false" }, { "2.4.0.1_B2", "2.4.0.1_B2", "false" }, { "2.4.0.1_CVS", "2.4.0.1_B2", "false" }, { "2.4.0.1_B2", "2.4.0.1_CVS", "false" } };
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
/* 1373 */     for (int i = 0; i < tests.length; i++)
/*      */     {
/* 1375 */       System.out.println(shouldUpdate(tests[i][0], tests[i][1]) + " / " + tests[i][2]);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/update/CoreUpdateChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */