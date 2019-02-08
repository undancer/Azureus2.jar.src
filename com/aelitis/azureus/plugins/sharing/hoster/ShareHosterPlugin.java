/*     */ package com.aelitis.azureus.plugins.sharing.hoster;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.Plugin;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginListener;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadAttributeListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeRemovedListener;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareManagerListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDeletionVetoException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceEvent;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceWillBeDeletedListener;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrentWillBeRemovedListener;
/*     */ import org.gudy.azureus2.plugins.utils.DelayedTask;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*     */ 
/*     */ public class ShareHosterPlugin implements Plugin, PluginListener, ShareManagerListener
/*     */ {
/*     */   protected PluginInterface plugin_interface;
/*     */   protected LoggerChannel log;
/*     */   protected Tracker tracker;
/*     */   protected ShareManager share_manager;
/*     */   protected org.gudy.azureus2.plugins.download.DownloadManager download_manager;
/*  59 */   protected Map resource_dl_map = new HashMap();
/*  60 */   protected Map resource_tt_map = new HashMap();
/*     */   
/*     */   protected Download download_being_removed;
/*     */   
/*     */   protected TrackerTorrent torrent_being_removed;
/*     */   
/*     */ 
/*     */   public static void load(PluginInterface plugin_interface)
/*     */   {
/*  69 */     plugin_interface.getPluginProperties().setProperty("plugin.version", "1.0");
/*  70 */     plugin_interface.getPluginProperties().setProperty("plugin.name", "Share Hoster");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(PluginInterface _plugin_interface)
/*     */   {
/*  77 */     this.plugin_interface = _plugin_interface;
/*     */     
/*  79 */     this.log = this.plugin_interface.getLogger().getChannel("ShareHosterPlugin");
/*     */     
/*  81 */     this.log.log(1, "ShareHosterPlugin: initialisation starts");
/*     */     
/*  83 */     this.plugin_interface.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void initializationComplete()
/*     */   {
/*  89 */     DelayedTask dt = this.plugin_interface.getUtilities().createDelayedTask(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*  94 */         ShareHosterPlugin.this.initialise();
/*     */       }
/*     */       
/*  97 */     });
/*  98 */     dt.queue();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initialise()
/*     */   {
/* 104 */     this.log.log(1, "ShareHosterPlugin: initialisation complete");
/*     */     
/* 106 */     Thread.currentThread().setPriority(1);
/*     */     try
/*     */     {
/* 109 */       this.tracker = this.plugin_interface.getTracker();
/*     */       
/* 111 */       this.download_manager = this.plugin_interface.getDownloadManager();
/*     */       
/* 113 */       this.share_manager = this.plugin_interface.getShareManager();
/*     */       
/* 115 */       this.share_manager.addListener(this);
/*     */       
/* 117 */       this.share_manager.initialise();
/*     */     }
/*     */     catch (ShareException e)
/*     */     {
/* 121 */       Debug.printStackTrace(e);
/*     */       
/* 123 */       this.log.log(e);
/*     */     }
/*     */     finally
/*     */     {
/* 127 */       this.plugin_interface.getPluginManager().firePluginEvent(5);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void closedownInitiated() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void closedownComplete() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void resourceAdded(final ShareResource resource)
/*     */   {
/* 145 */     this.log.log(1, "Resource added:".concat(resource.getName()));
/*     */     
/*     */     try
/*     */     {
/* 149 */       resource.addDeletionListener(new ShareResourceWillBeDeletedListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void resourceWillBeDeleted(ShareResource resource)
/*     */           throws ShareResourceDeletionVetoException
/*     */         {
/*     */ 
/*     */ 
/* 158 */           ShareHosterPlugin.this.canResourceBeDeleted(resource);
/*     */         }
/*     */         
/* 161 */       });
/* 162 */       Download new_download = null;
/*     */       
/* 164 */       int type = resource.getType();
/*     */       
/* 166 */       if (type == 1)
/*     */       {
/* 168 */         ShareResourceFile file_resource = (ShareResourceFile)resource;
/*     */         
/* 170 */         ShareItem item = file_resource.getItem();
/*     */         
/* 172 */         Torrent torrent = item.getTorrent();
/*     */         
/* 174 */         Download download = this.download_manager.getDownload(torrent);
/*     */         
/* 176 */         if (download == null)
/*     */         {
/* 178 */           new_download = addDownload(resource, torrent, item.getTorrentFile(), file_resource.getFile());
/*     */         }
/* 180 */       } else if (type == 2)
/*     */       {
/* 182 */         ShareResourceDir dir_resource = (ShareResourceDir)resource;
/*     */         
/* 184 */         ShareItem item = dir_resource.getItem();
/*     */         
/* 186 */         Torrent torrent = item.getTorrent();
/*     */         
/* 188 */         Download download = this.download_manager.getDownload(torrent);
/*     */         
/* 190 */         if (download == null)
/*     */         {
/* 192 */           new_download = addDownload(resource, torrent, item.getTorrentFile(), dir_resource.getDir());
/*     */         }
/*     */       }
/*     */       
/* 196 */       if (new_download != null)
/*     */       {
/* 198 */         final Download f_new_download = new_download;
/*     */         
/* 200 */         this.resource_dl_map.put(resource, new_download);
/*     */         
/* 202 */         resource.addChangeListener(new ShareResourceListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void shareResourceChanged(ShareResource resource, ShareResourceEvent event)
/*     */           {
/*     */ 
/*     */ 
/* 210 */             if (event.getType() == 1)
/*     */             {
/* 212 */               TorrentAttribute attribute = (TorrentAttribute)event.getData();
/*     */               
/*     */ 
/*     */ 
/* 216 */               f_new_download.setAttribute(attribute, resource.getAttribute(attribute));
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */ 
/* 222 */         });
/* 223 */         TorrentAttribute[] attributes = resource.getAttributes();
/*     */         
/* 225 */         for (int i = 0; i < attributes.length; i++)
/*     */         {
/* 227 */           TorrentAttribute ta = attributes[i];
/*     */           
/* 229 */           new_download.setAttribute(ta, resource.getAttribute(ta));
/*     */         }
/*     */         
/* 232 */         new_download.addAttributeListener(new DownloadAttributeListener()
/*     */         {
/*     */ 
/* 235 */           public void attributeEventOccurred(Download d, TorrentAttribute attr, int event_type) { resource.setAttribute(attr, d.getAttribute(attr)); } }, this.plugin_interface.getTorrentManager().getAttribute("Category"), 1);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 242 */         boolean persistent = resource.isPersistent();
/*     */         
/* 244 */         Torrent dl_torrent = new_download.getTorrent();
/*     */         
/* 246 */         if (dl_torrent != null)
/*     */         {
/* 248 */           TrackerTorrent tt = this.tracker.host(dl_torrent, persistent);
/*     */           
/* 250 */           if (!persistent)
/*     */           {
/* 252 */             tt.addRemovalListener(new TrackerTorrentWillBeRemovedListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void torrentWillBeRemoved(TrackerTorrent tt)
/*     */                 throws TrackerTorrentRemovalVetoException
/*     */               {
/*     */ 
/*     */ 
/* 261 */                 if (tt != ShareHosterPlugin.this.torrent_being_removed)
/*     */                 {
/* 263 */                   throw new TrackerTorrentRemovalVetoException(MessageText.getString("plugin.sharing.torrent.remove.veto"));
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */           
/*     */ 
/* 270 */           this.resource_tt_map.put(resource, tt);
/*     */         }
/*     */         
/* 273 */         if (!persistent)
/*     */         {
/* 275 */           new_download.addDownloadWillBeRemovedListener(new DownloadWillBeRemovedListener()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void downloadWillBeRemoved(Download dl)
/*     */               throws DownloadRemovalVetoException
/*     */             {
/*     */ 
/*     */ 
/* 284 */               if (dl != ShareHosterPlugin.this.download_being_removed)
/*     */               {
/* 286 */                 throw new DownloadRemovalVetoException(MessageText.getString("plugin.sharing.download.remove.veto"));
/*     */               }
/*     */               
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 296 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Download addDownload(ShareResource resource, final Torrent torrent, File torrent_file, File data_file)
/*     */     throws DownloadException
/*     */   {
/* 309 */     Map<String, String> properties = resource.getProperties();
/*     */     
/* 311 */     final List<String> networks = new ArrayList();
/* 312 */     List<Tag> tags = new ArrayList();
/*     */     TagManager tm;
/* 314 */     if (properties != null)
/*     */     {
/* 316 */       String nets = (String)properties.get("networks");
/*     */       
/* 318 */       if (nets != null)
/*     */       {
/* 320 */         String[] bits = nets.split(",");
/*     */         
/* 322 */         for (String bit : bits)
/*     */         {
/* 324 */           bit = AENetworkClassifier.internalise(bit.trim());
/*     */           
/* 326 */           if (bit != null)
/*     */           {
/* 328 */             networks.add(bit);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 333 */       String tags_str = (String)properties.get("tags");
/*     */       
/* 335 */       if (tags_str != null)
/*     */       {
/* 337 */         String[] bits = tags_str.split(",");
/*     */         
/* 339 */         tm = TagManagerFactory.getTagManager();
/*     */         
/* 341 */         for (String bit : bits) {
/*     */           try
/*     */           {
/* 344 */             long tag_uid = Long.parseLong(bit.trim());
/*     */             
/* 346 */             Tag tag = tm.lookupTagByUID(tag_uid);
/*     */             
/* 348 */             if (tag != null)
/*     */             {
/* 350 */               tags.add(tag);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 354 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 360 */     DownloadWillBeAddedListener dwbal = null;
/*     */     
/* 362 */     if (networks.size() > 0)
/*     */     {
/* 364 */       dwbal = new DownloadWillBeAddedListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void initialised(Download download)
/*     */         {
/*     */ 
/* 371 */           if (Arrays.equals(download.getTorrentHash(), torrent.getHash()))
/*     */           {
/* 373 */             PluginCoreUtils.unwrap(download).getDownloadState().setNetworks((String[])networks.toArray(new String[networks.size()]));
/*     */           }
/*     */           
/*     */         }
/* 377 */       };
/* 378 */       this.download_manager.addDownloadWillBeAddedListener(dwbal);
/*     */     }
/*     */     try
/*     */     {
/*     */       Download download;
/*     */       Download download;
/* 384 */       if (resource.isPersistent())
/*     */       {
/* 386 */         DownloadStub stub = this.download_manager.lookupDownloadStub(torrent.getHash());
/*     */         
/* 388 */         if (stub != null)
/*     */         {
/*     */ 
/*     */ 
/* 392 */           return null;
/*     */         }
/*     */         try
/*     */         {
/* 396 */           torrent.setComplete(data_file);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 400 */           Debug.out(e);
/*     */         }
/*     */         
/* 403 */         download = this.download_manager.addDownload(torrent, torrent_file, data_file);
/*     */       }
/*     */       else
/*     */       {
/* 407 */         download = this.download_manager.addNonPersistentDownload(torrent, torrent_file, data_file);
/*     */       }
/*     */       org.gudy.azureus2.core3.download.DownloadManager dm;
/* 410 */       if (tags.size() > 0)
/*     */       {
/* 412 */         dm = PluginCoreUtils.unwrap(download);
/*     */         
/* 414 */         for (Tag tag : tags)
/*     */         {
/* 416 */           tag.addTaggable(dm);
/*     */         }
/*     */       }
/*     */       
/* 420 */       return download;
/*     */     }
/*     */     finally
/*     */     {
/* 424 */       if (dwbal != null)
/*     */       {
/* 426 */         this.download_manager.removeDownloadWillBeAddedListener(dwbal);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void canResourceBeDeleted(ShareResource resource)
/*     */     throws ShareResourceDeletionVetoException
/*     */   {
/* 437 */     Download dl = (Download)this.resource_dl_map.get(resource);
/*     */     
/* 439 */     if (dl != null) {
/*     */       try
/*     */       {
/* 442 */         this.download_being_removed = dl;
/*     */         
/* 444 */         dl.canBeRemoved();
/*     */       }
/*     */       catch (DownloadRemovalVetoException e)
/*     */       {
/* 448 */         throw new ShareResourceDeletionVetoException(e.getMessage());
/*     */       }
/*     */       finally
/*     */       {
/* 452 */         this.download_being_removed = null;
/*     */       }
/*     */     }
/*     */     
/* 456 */     TrackerTorrent tt = (TrackerTorrent)this.resource_tt_map.get(resource);
/*     */     
/* 458 */     if (tt != null) {
/*     */       try
/*     */       {
/* 461 */         this.torrent_being_removed = tt;
/*     */         
/* 463 */         tt.canBeRemoved();
/*     */       }
/*     */       catch (TrackerTorrentRemovalVetoException e)
/*     */       {
/* 467 */         throw new ShareResourceDeletionVetoException(e.getMessage());
/*     */       }
/*     */       finally
/*     */       {
/* 471 */         this.torrent_being_removed = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void resourceModified(ShareResource old_resource, ShareResource new_resource)
/*     */   {
/* 481 */     this.log.log(1, "Resource modified:".concat(old_resource.getName()));
/*     */     
/* 483 */     resourceDeleted(old_resource);
/*     */     
/* 485 */     resourceAdded(new_resource);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void resourceDeleted(ShareResource resource)
/*     */   {
/* 492 */     this.log.log(1, "Resource deleted:".concat(resource.getName()));
/*     */     
/* 494 */     Download dl = (Download)this.resource_dl_map.get(resource);
/*     */     
/* 496 */     if (dl != null)
/*     */     {
/*     */       try {
/* 499 */         this.download_being_removed = dl;
/*     */         
/*     */ 
/*     */ 
/*     */         try
/*     */         {
/* 505 */           dl.stop();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 512 */         dl.remove();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 516 */         Debug.printStackTrace(e);
/*     */       }
/*     */       finally
/*     */       {
/* 520 */         this.download_being_removed = null;
/*     */       }
/*     */       
/* 523 */       this.resource_dl_map.remove(resource);
/*     */     }
/*     */     
/* 526 */     TrackerTorrent tt = (TrackerTorrent)this.resource_tt_map.get(resource);
/*     */     
/* 528 */     if (tt != null)
/*     */     {
/*     */       try {
/* 531 */         this.torrent_being_removed = tt;
/*     */         
/* 533 */         tt.remove();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 537 */         Debug.printStackTrace(e);
/*     */       }
/*     */       finally
/*     */       {
/* 541 */         this.torrent_being_removed = null;
/*     */       }
/*     */       
/* 544 */       this.resource_tt_map.remove(resource);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportProgress(int percent_complete) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reportCurrentTask(String task_description)
/*     */   {
/* 558 */     this.log.log(1, "Current Task:".concat(task_description));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/sharing/hoster/ShareHosterPlugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */