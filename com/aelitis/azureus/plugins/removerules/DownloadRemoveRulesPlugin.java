/*     */ package com.aelitis.azureus.plugins.removerules;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManager;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*     */ import org.gudy.azureus2.plugins.download.DownloadTrackerListener;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
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
/*     */ public class DownloadRemoveRulesPlugin
/*     */   implements Plugin, DownloadManagerListener
/*     */ {
/*     */   public static final int INITIAL_DELAY = 60000;
/*     */   public static final int DELAYED_REMOVAL_PERIOD = 60000;
/*     */   public static final int AELITIS_BIG_TORRENT_SEED_LIMIT = 10000;
/*     */   public static final int AELITIS_SMALL_TORRENT_SEED_LIMIT = 1000;
/*     */   public static final int MAX_SEED_TO_PEER_RATIO = 10;
/*     */   public static final String UPDATE_TRACKER = "tracker.update.vuze.com";
/*     */   protected PluginInterface plugin_interface;
/*     */   protected boolean closing;
/*  56 */   protected Map dm_listener_map = new HashMap(10);
/*  57 */   protected List monitored_downloads = new ArrayList();
/*     */   
/*     */   protected LoggerChannel log;
/*     */   
/*     */   protected BooleanParameter remove_unauthorised;
/*     */   
/*     */   protected BooleanParameter remove_unauthorised_seeding_only;
/*     */   
/*     */   protected BooleanParameter remove_unauthorised_data;
/*     */   
/*     */   protected BooleanParameter remove_update_torrents;
/*     */   
/*     */   public static void load(PluginInterface plugin_interface)
/*     */   {
/*  71 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  72 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Download Remove Rules");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */   {
/*  79 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  81 */     this.log = this.plugin_interface.getLogger().getChannel("DLRemRules");
/*     */     
/*  83 */     BasicPluginConfigModel config = this.plugin_interface.getUIManager().createBasicPluginConfigModel("torrents", "download.removerules.name");
/*     */     
/*  85 */     config.addLabelParameter2("download.removerules.unauthorised.info");
/*     */     
/*  87 */     this.remove_unauthorised = config.addBooleanParameter2("download.removerules.unauthorised", "download.removerules.unauthorised", false);
/*     */     
/*     */ 
/*  90 */     this.remove_unauthorised_seeding_only = config.addBooleanParameter2("download.removerules.unauthorised.seedingonly", "download.removerules.unauthorised.seedingonly", true);
/*     */     
/*     */ 
/*  93 */     this.remove_unauthorised_data = config.addBooleanParameter2("download.removerules.unauthorised.data", "download.removerules.unauthorised.data", false);
/*     */     
/*     */ 
/*  96 */     this.remove_unauthorised.addEnabledOnSelection(this.remove_unauthorised_seeding_only);
/*  97 */     this.remove_unauthorised.addEnabledOnSelection(this.remove_unauthorised_data);
/*     */     
/*  99 */     this.remove_update_torrents = config.addBooleanParameter2("download.removerules.updatetorrents", "download.removerules.updatetorrents", true);
/*     */     
/*     */ 
/* 102 */     new DelayedEvent("DownloadRemovalRules", 60000L, new AERunnable()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */ 
/* 110 */         DownloadRemoveRulesPlugin.this.plugin_interface.getDownloadManager().addListener(DownloadRemoveRulesPlugin.this);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadAdded(final Download download)
/*     */   {
/* 122 */     if (!download.isPersistent())
/*     */     {
/* 124 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 129 */     if (download.getFlag(16L))
/*     */     {
/* 131 */       DiskManagerFileInfo[] files = download.getDiskManagerFileInfo();
/*     */       
/* 133 */       if (files.length == 1)
/*     */       {
/* 135 */         DiskManagerFileInfo file = files[0];
/*     */         
/*     */ 
/*     */ 
/* 139 */         if ((file.getDownloaded() == file.getLength()) && (!file.getFile().exists()))
/*     */         {
/*     */ 
/* 142 */           this.log.log("Removing low-noise download '" + download.getName() + " as data missing");
/*     */           
/* 144 */           removeDownload(download, false);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 149 */     DownloadTrackerListener listener = new DownloadTrackerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void scrapeResult(DownloadScrapeResult response)
/*     */       {
/*     */ 
/* 156 */         if (DownloadRemoveRulesPlugin.this.closing)
/*     */         {
/* 158 */           return;
/*     */         }
/*     */         
/* 161 */         DownloadRemoveRulesPlugin.this.handleScrape(download, response);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void announceResult(DownloadAnnounceResult response)
/*     */       {
/* 168 */         if (DownloadRemoveRulesPlugin.this.closing)
/*     */         {
/* 170 */           return;
/*     */         }
/*     */         
/* 173 */         DownloadRemoveRulesPlugin.this.handleAnnounce(download, response);
/*     */       }
/*     */       
/* 176 */     };
/* 177 */     this.monitored_downloads.add(download);
/*     */     
/* 179 */     this.dm_listener_map.put(download, listener);
/*     */     
/* 181 */     download.addTrackerListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleScrape(Download download, DownloadScrapeResult response)
/*     */   {
/* 189 */     String status = response.getStatus();
/*     */     
/* 191 */     if (status == null)
/*     */     {
/* 193 */       status = "";
/*     */     }
/*     */     
/* 196 */     handleAnnounceScrapeStatus(download, status);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleAnnounce(Download download, DownloadAnnounceResult response)
/*     */   {
/* 204 */     String reason = "";
/*     */     
/* 206 */     if (response.getResponseType() == 2)
/*     */     {
/* 208 */       reason = response.getError();
/*     */       
/* 210 */       if (reason == null)
/*     */       {
/* 212 */         reason = "";
/*     */       }
/*     */     }
/*     */     
/* 216 */     handleAnnounceScrapeStatus(download, reason);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void handleAnnounceScrapeStatus(Download download, String status)
/*     */   {
/* 224 */     if (!this.monitored_downloads.contains(download))
/*     */     {
/* 226 */       return;
/*     */     }
/*     */     
/* 229 */     status = status.toLowerCase();
/*     */     
/* 231 */     boolean download_completed = download.isComplete();
/*     */     
/* 233 */     if ((status.contains("not authori")) || (status.toLowerCase().contains("unauthori")))
/*     */     {
/*     */ 
/* 236 */       if ((this.remove_unauthorised.getValue()) && ((!this.remove_unauthorised_seeding_only.getValue()) || (download_completed)))
/*     */       {
/*     */ 
/*     */ 
/* 240 */         this.log.log(download.getTorrent(), 1, "Download '" + download.getName() + "' is unauthorised and removal triggered");
/*     */         
/*     */ 
/* 243 */         removeDownload(download, this.remove_unauthorised_data.getValue());
/*     */         
/* 245 */         return;
/*     */       }
/*     */     }
/*     */     
/* 249 */     Torrent torrent = download.getTorrent();
/*     */     
/* 251 */     if ((torrent != null) && (torrent.getAnnounceURL() != null))
/*     */     {
/* 253 */       String url_string = torrent.getAnnounceURL().toString().toLowerCase();
/*     */       
/* 255 */       if (url_string.contains("tracker.update.vuze.com"))
/*     */       {
/*     */ 
/*     */ 
/* 259 */         if (((download_completed) && (status.contains("too many seeds"))) || (status.contains("too many peers")))
/*     */         {
/*     */ 
/*     */ 
/* 263 */           this.log.log(download.getTorrent(), 1, "Download '" + download.getName() + "' being removed on instruction from the tracker");
/*     */           
/*     */ 
/*     */ 
/* 267 */           removeDownloadDelayed(download, false);
/*     */         }
/* 269 */         else if ((download_completed) && (this.remove_update_torrents.getValue()))
/*     */         {
/* 271 */           long seeds = download.getLastScrapeResult().getSeedCount();
/* 272 */           long peers = download.getLastScrapeResult().getNonSeedCount();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 277 */           if (seeds / (peers == 0L ? 1L : peers) > 10L)
/*     */           {
/* 279 */             this.log.log(download.getTorrent(), 1, "Download '" + download.getName() + "' being removed to reduce swarm size");
/*     */             
/*     */ 
/*     */ 
/* 283 */             removeDownloadDelayed(download, false);
/*     */           }
/*     */           else
/*     */           {
/* 287 */             long creation_time = download.getCreationTime();
/*     */             
/* 289 */             long running_mins = (SystemTime.getCurrentTime() - creation_time) / 60000L;
/*     */             
/* 291 */             if (running_mins > 15L)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 296 */               boolean big_torrent = torrent.getSize() > 1048576L;
/*     */               
/* 298 */               if (((seeds > 10000L) && (big_torrent)) || ((seeds > 1000L) && (!big_torrent)))
/*     */               {
/*     */ 
/*     */ 
/* 302 */                 this.log.log("Download '" + download.getName() + "' being removed to reduce swarm size");
/*     */                 
/* 304 */                 removeDownloadDelayed(download, false);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeDownloadDelayed(final Download download, final boolean remove_data)
/*     */   {
/* 318 */     this.monitored_downloads.remove(download);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 323 */     this.plugin_interface.getUtilities().createThread("delayedRemoval", new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/* 331 */           Thread.sleep(60000L);
/*     */           
/* 333 */           DownloadRemoveRulesPlugin.this.removeDownload(download, remove_data);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 337 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeDownload(Download download, final boolean remove_data)
/*     */   {
/* 348 */     this.monitored_downloads.remove(download);
/*     */     
/* 350 */     if (download.getState() == 7)
/*     */     {
/*     */       try {
/* 353 */         download.remove(false, remove_data);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 357 */         this.log.logAlert("Automatic removal of download '" + download.getName() + "' failed", e);
/*     */       }
/*     */     }
/*     */     else {
/* 361 */       download.addListener(new DownloadListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void stateChanged(Download download, int old_state, int new_state)
/*     */         {
/*     */ 
/*     */ 
/* 370 */           DownloadRemoveRulesPlugin.this.log.log(download.getTorrent(), 1, "download state changed to '" + new_state + "'");
/*     */           
/*     */ 
/* 373 */           if (new_state == 7) {
/*     */             try
/*     */             {
/* 376 */               download.remove(false, remove_data);
/*     */               
/* 378 */               String msg = DownloadRemoveRulesPlugin.this.plugin_interface.getUtilities().getLocaleUtilities().getLocalisedMessageText("download.removerules.removed.ok", new String[] { download.getName() });
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 383 */               if (download.getFlag(16L)) {
/* 384 */                 DownloadRemoveRulesPlugin.this.log.log(download.getTorrent(), 1, msg);
/*     */               }
/*     */               else {
/* 387 */                 DownloadRemoveRulesPlugin.this.log.logAlert(1, msg);
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 391 */               DownloadRemoveRulesPlugin.this.log.logAlert("Automatic removal of download '" + download.getName() + "' failed", e);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */         public void positionChanged(Download download, int oldPosition, int newPosition) {}
/*     */       });
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 406 */         download.stop();
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 410 */         this.log.logAlert("Automatic removal of download '" + download.getName() + "' failed", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void downloadRemoved(Download download)
/*     */   {
/* 419 */     this.monitored_downloads.remove(download);
/*     */     
/* 421 */     DownloadTrackerListener listener = (DownloadTrackerListener)this.dm_listener_map.remove(download);
/*     */     
/* 423 */     if (listener != null)
/*     */     {
/* 425 */       download.removeTrackerListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroyInitiated()
/*     */   {
/* 432 */     this.closing = true;
/*     */   }
/*     */   
/*     */   public void destroyed() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/removerules/DownloadRemoveRulesPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */