/*     */ package org.gudy.azureus2.platform.win32;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.gudy.azureus2.core3.html.HTMLUtils;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
/*     */ import org.gudy.azureus2.plugins.update.Update;
/*     */ import org.gudy.azureus2.plugins.update.UpdateChecker;
/*     */ import org.gudy.azureus2.plugins.update.UpdateInstaller;
/*     */ import org.gudy.azureus2.plugins.update.UpdateManager;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderAdapter;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetails;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoader;
/*     */ import org.gudy.azureus2.pluginsimpl.update.sf.SFPluginDetailsLoaderFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlatformManagerUpdateChecker
/*     */   implements Plugin, UpdatableComponent
/*     */ {
/*  47 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   public static final String UPDATE_NAME = "Platform-specific support";
/*     */   
/*     */   public static final int RD_SIZE_RETRIES = 3;
/*     */   
/*     */   public static final int RD_SIZE_TIMEOUT = 10000;
/*     */   
/*     */   protected PluginInterface plugin_interface;
/*     */   
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */   {
/*  59 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  61 */     this.plugin_interface.getPluginProperties().setProperty("plugin.name", "Platform-Specific Support");
/*     */     
/*  63 */     String version = "1.0";
/*     */     
/*  65 */     PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */     
/*  67 */     if (platform.getPlatformType() == 1)
/*     */     {
/*  69 */       if (platform.hasCapability(PlatformManagerCapabilities.GetVersion)) {
/*     */         try
/*     */         {
/*  72 */           version = platform.getVersion();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  76 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/*  80 */       this.plugin_interface.getUpdateManager().registerUpdatableComponent(this, false);
/*     */     }
/*     */     else
/*     */     {
/*  84 */       this.plugin_interface.getPluginProperties().setProperty("plugin.version.info", "Not required for this platform");
/*     */     }
/*     */     
/*     */ 
/*  88 */     this.plugin_interface.getPluginProperties().setProperty("plugin.version", version);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  94 */     return "Platform-specific support";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaximumCheckTime()
/*     */   {
/* 100 */     return 30;
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkForUpdate(final UpdateChecker checker)
/*     */   {
/*     */     try
/*     */     {
/* 108 */       SFPluginDetails sf_details = SFPluginDetailsLoaderFactory.getSingleton().getPluginDetails(this.plugin_interface.getPluginID());
/*     */       
/* 110 */       String current_version = this.plugin_interface.getPluginVersion();
/*     */       
/* 112 */       if (Logger.isEnabled()) {
/* 113 */         Logger.log(new LogEvent(LOGID, "PlatformManager:Win32 update check starts: current = " + current_version));
/*     */       }
/*     */       
/*     */ 
/* 117 */       boolean current_az_is_cvs = Constants.isCVSVersion();
/*     */       
/* 119 */       String sf_plugin_version = sf_details.getVersion();
/*     */       
/* 121 */       String sf_comp_version = sf_plugin_version;
/*     */       
/* 123 */       if (current_az_is_cvs)
/*     */       {
/* 125 */         String sf_cvs_version = sf_details.getCVSVersion();
/*     */         
/* 127 */         if (sf_cvs_version.length() > 0)
/*     */         {
/*     */ 
/*     */ 
/* 131 */           sf_plugin_version = sf_cvs_version;
/*     */           
/* 133 */           sf_comp_version = sf_plugin_version.substring(0, sf_plugin_version.length() - 4);
/*     */         }
/*     */       }
/*     */       
/* 137 */       String target_version = null;
/*     */       
/* 139 */       if ((sf_comp_version.length() == 0) || (!Character.isDigit(sf_comp_version.charAt(0))))
/*     */       {
/*     */ 
/* 142 */         if (Logger.isEnabled()) {
/* 143 */           Logger.log(new LogEvent(LOGID, 1, "PlatformManager:Win32 no valid version to check against (" + sf_comp_version + ")"));
/*     */         }
/*     */         
/*     */       }
/* 147 */       else if (Constants.compareVersions(current_version, sf_comp_version) < 0)
/*     */       {
/* 149 */         target_version = sf_comp_version;
/*     */       }
/*     */       
/* 152 */       checker.reportProgress("Win32: current = " + current_version + ", latest = " + sf_comp_version);
/*     */       
/* 154 */       if (Logger.isEnabled()) {
/* 155 */         Logger.log(new LogEvent(LOGID, "PlatformManager:Win32 update required = " + (target_version != null)));
/*     */       }
/*     */       
/*     */ 
/* 159 */       if (target_version != null)
/*     */       {
/* 161 */         String target_download = sf_details.getDownloadURL();
/*     */         
/* 163 */         if (current_az_is_cvs)
/*     */         {
/* 165 */           String sf_cvs_version = sf_details.getCVSVersion();
/*     */           
/* 167 */           if (sf_cvs_version.length() > 0)
/*     */           {
/* 169 */             target_download = sf_details.getCVSDownloadURL();
/*     */           }
/*     */         }
/*     */         
/* 173 */         ResourceDownloaderFactory rdf = ResourceDownloaderFactoryImpl.getSingleton();
/*     */         
/* 175 */         ResourceDownloader direct_rdl = rdf.create(new URL(target_download));
/*     */         
/* 177 */         String torrent_download = "http://cf1.vuze.com/torrent/torrents/";
/*     */         
/* 179 */         int slash_pos = target_download.lastIndexOf("/");
/*     */         
/* 181 */         if (slash_pos == -1)
/*     */         {
/* 183 */           torrent_download = torrent_download + target_download;
/*     */         }
/*     */         else
/*     */         {
/* 187 */           torrent_download = torrent_download + target_download.substring(slash_pos + 1);
/*     */         }
/*     */         
/* 190 */         torrent_download = torrent_download + ".torrent";
/*     */         
/* 192 */         ResourceDownloader torrent_rdl = rdf.create(new URL(torrent_download));
/*     */         
/* 194 */         torrent_rdl = rdf.getSuffixBasedDownloader(torrent_rdl);
/*     */         
/*     */ 
/*     */ 
/* 198 */         ResourceDownloader alternate_rdl = rdf.getAlternateDownloader(new ResourceDownloader[] { torrent_rdl, direct_rdl });
/*     */         
/*     */ 
/*     */ 
/* 202 */         rdf.getTimeoutDownloader(rdf.getRetryDownloader(alternate_rdl, 3), 10000).getSize();
/*     */         
/*     */ 
/* 205 */         List update_desc = new ArrayList();
/*     */         
/* 207 */         List desc_lines = HTMLUtils.convertHTMLToText("", sf_details.getDescription());
/*     */         
/* 209 */         update_desc.addAll(desc_lines);
/*     */         
/* 211 */         List comment_lines = HTMLUtils.convertHTMLToText("    ", sf_details.getComment());
/*     */         
/* 213 */         update_desc.addAll(comment_lines);
/*     */         
/* 215 */         String[] update_d = new String[update_desc.size()];
/*     */         
/* 217 */         update_desc.toArray(update_d);
/*     */         
/* 219 */         final Update update = checker.addUpdate("Platform-specific support", update_d, current_version, target_version, alternate_rdl, 2);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 228 */         update.setDescriptionURL(sf_details.getInfoURL());
/*     */         
/* 230 */         alternate_rdl.addListener(new ResourceDownloaderAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */           {
/*     */ 
/*     */ 
/* 238 */             PlatformManagerUpdateChecker.this.installUpdate(checker, update, downloader, data);
/*     */             
/* 240 */             return true;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */           {
/* 250 */             update.complete(false);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 256 */       Debug.printStackTrace(e);
/*     */       
/* 258 */       checker.reportProgress("Failed to load plugin details for the platform manager: " + Debug.getNestedExceptionMessage(e));
/*     */       
/* 260 */       checker.failed();
/*     */     }
/*     */     finally
/*     */     {
/* 264 */       checker.completed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void installUpdate(UpdateChecker checker, Update update, ResourceDownloader rd, InputStream data)
/*     */   {
/* 275 */     ZipInputStream zip = null;
/*     */     try
/*     */     {
/* 278 */       data = update.verifyData(data, true);
/*     */       
/* 280 */       rd.reportActivity("Data verified successfully");
/*     */       
/* 282 */       UpdateInstaller installer = checker.createInstaller();
/*     */       
/* 284 */       zip = new ZipInputStream(data);
/*     */       
/* 286 */       ZipEntry entry = null;
/*     */       
/* 288 */       while ((entry = zip.getNextEntry()) != null)
/*     */       {
/* 290 */         String name = entry.getName();
/*     */         
/* 292 */         if (name.toLowerCase().startsWith("windows/"))
/*     */         {
/*     */ 
/*     */ 
/* 296 */           name = name.substring(8);
/*     */           
/*     */ 
/*     */ 
/* 300 */           if (name.length() > 0)
/*     */           {
/* 302 */             rd.reportActivity("Adding update action for '" + name + "'");
/*     */             
/* 304 */             if (Logger.isEnabled()) {
/* 305 */               Logger.log(new LogEvent(LOGID, "PlatformManager:Win32 adding action for '" + name + "'"));
/*     */             }
/*     */             
/* 308 */             installer.addResource(name, zip, false);
/*     */             
/* 310 */             installer.addMoveAction(name, installer.getInstallDir() + File.separator + name);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 316 */       update.complete(true); return;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 320 */       update.complete(false);
/*     */       
/* 322 */       rd.reportActivity("Update install failed:" + e.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/* 326 */       if (zip != null) {
/*     */         try
/*     */         {
/* 329 */           zip.close();
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
/*     */   protected List splitMultiLine(String indent, String text)
/*     */   {
/* 342 */     int pos = 0;
/*     */     
/* 344 */     String lc_text = text.toLowerCase();
/*     */     
/* 346 */     List lines = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */     for (;;)
/*     */     {
/* 352 */       int p1 = lc_text.indexOf("<br>", pos);
/*     */       String line;
/* 354 */       String line; if (p1 == -1)
/*     */       {
/* 356 */         line = text.substring(pos);
/*     */       }
/*     */       else
/*     */       {
/* 360 */         line = text.substring(pos, p1);
/*     */         
/* 362 */         pos = p1 + 4;
/*     */       }
/*     */       
/* 365 */       lines.add(indent + line);
/*     */       
/* 367 */       if (p1 == -1) {
/*     */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 373 */     return lines;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/PlatformManagerUpdateChecker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */