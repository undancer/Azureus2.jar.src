/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.devices.Device;
/*      */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*      */ import com.aelitis.azureus.core.devices.TranscodeException;
/*      */ import com.aelitis.azureus.core.devices.TranscodeManager;
/*      */ import com.aelitis.azureus.core.devices.TranscodeManagerListener;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*      */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*      */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagDownload;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureListener;
/*      */ import com.aelitis.azureus.core.tag.TagFeatureTranscode;
/*      */ import com.aelitis.azureus.core.tag.TagListener;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryListener;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.category.CategoryManagerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.plugins.PluginEvent;
/*      */ import org.gudy.azureus2.plugins.PluginEventListener;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginListener;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.PluginState;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TranscodeManagerImpl
/*      */   implements TranscodeManager
/*      */ {
/*      */   private DeviceManagerImpl device_manager;
/*      */   private AzureusCore azureus_core;
/*      */   private volatile TranscodeProviderVuze vuzexcode_provider;
/*   68 */   private CopyOnWriteList<TranscodeManagerListener> listeners = new CopyOnWriteList();
/*      */   
/*   70 */   private TranscodeQueueImpl queue = new TranscodeQueueImpl(this);
/*      */   
/*   72 */   private AESemaphore init_sem = new AESemaphore("TM:init");
/*      */   
/*      */   private boolean hooked_categories;
/*      */   
/*   76 */   private Map<Category, Object[]> category_map = new HashMap();
/*      */   
/*      */   private CategoryListener category_listener;
/*      */   
/*      */   private GlobalManagerListener category_dl_listener;
/*      */   private TorrentAttribute category_ta;
/*      */   private boolean hooked_tags;
/*   83 */   private Map<Tag, Object[]> tag_map = new HashMap();
/*      */   
/*      */   private TagListener tag_listener;
/*      */   
/*      */   private TorrentAttribute tag_ta;
/*      */   
/*      */ 
/*      */   protected TranscodeManagerImpl(DeviceManagerImpl _dm)
/*      */   {
/*   92 */     this.device_manager = _dm;
/*      */     
/*   94 */     this.azureus_core = AzureusCoreFactory.getSingleton();
/*      */     
/*   96 */     PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */     
/*   98 */     this.category_ta = default_pi.getTorrentManager().getPluginAttribute("xcode.cat.done");
/*   99 */     this.tag_ta = default_pi.getTorrentManager().getPluginAttribute("xcode.tag.done");
/*      */     
/*  101 */     final AESemaphore plugin_sem = new AESemaphore("TM:plugin");
/*      */     
/*  103 */     default_pi.addListener(new PluginListener()
/*      */     {
/*      */ 
/*      */       public void initializationComplete()
/*      */       {
/*      */         try
/*      */         {
/*  110 */           PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*      */           
/*  112 */           default_pi.addEventListener(new PluginEventListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void handleEvent(PluginEvent ev)
/*      */             {
/*      */ 
/*  119 */               int type = ev.getType();
/*      */               
/*  121 */               if (type == 8)
/*      */               {
/*  123 */                 TranscodeManagerImpl.this.pluginAdded((PluginInterface)ev.getValue());
/*      */               }
/*  125 */               if (type == 9)
/*      */               {
/*  127 */                 TranscodeManagerImpl.this.pluginRemoved((PluginInterface)ev.getValue());
/*      */               }
/*      */               
/*      */             }
/*  131 */           });
/*  132 */           PluginInterface[] plugins = default_pi.getPluginManager().getPlugins();
/*      */           
/*  134 */           for (PluginInterface pi : plugins)
/*      */           {
/*  136 */             if (pi.getPluginState().isOperational())
/*      */             {
/*  138 */               TranscodeManagerImpl.this.pluginAdded(pi);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  143 */           plugin_sem.releaseForever();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void closedownInitiated()
/*      */       {
/*  150 */         plugin_sem.releaseForever();
/*      */         
/*      */ 
/*      */ 
/*  154 */         TranscodeManagerImpl.this.init_sem.releaseForever();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void closedownComplete() {}
/*      */     });
/*      */     
/*      */ 
/*  163 */     if (!plugin_sem.reserve(30000L))
/*      */     {
/*  165 */       Debug.out("Timeout waiting for init");
/*      */       
/*  167 */       AEDiagnostics.dumpThreads();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void initialise()
/*      */   {
/*  174 */     this.queue.initialise();
/*      */     
/*  176 */     this.init_sem.releaseForever();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void pluginAdded(PluginInterface pi)
/*      */   {
/*  183 */     if (pi.getPluginState().isBuiltIn())
/*      */     {
/*  185 */       return;
/*      */     }
/*      */     
/*  188 */     String plugin_id = pi.getPluginID();
/*      */     TranscodeProviderVuze provider;
/*  190 */     if (plugin_id.equals("vuzexcode"))
/*      */     {
/*  192 */       boolean added = false;
/*  193 */       boolean updated = false;
/*      */       
/*  195 */       provider = null;
/*      */       
/*  197 */       synchronized (this)
/*      */       {
/*  199 */         if (this.vuzexcode_provider == null)
/*      */         {
/*  201 */           this.vuzexcode_provider = new TranscodeProviderVuze(this, pi);
/*      */           
/*  203 */           added = true;
/*      */         }
/*  205 */         else if (pi != this.vuzexcode_provider.getPluginInterface())
/*      */         {
/*  207 */           this.vuzexcode_provider.update(pi);
/*      */           
/*  209 */           updated = true;
/*      */         }
/*      */         
/*  212 */         provider = this.vuzexcode_provider;
/*      */       }
/*      */       
/*  215 */       if (added)
/*      */       {
/*  217 */         for (TranscodeManagerListener listener : this.listeners) {
/*      */           try
/*      */           {
/*  220 */             listener.providerAdded(provider);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  224 */             Debug.out(e);
/*      */           }
/*      */         }
/*  227 */       } else if (updated)
/*      */       {
/*  229 */         for (TranscodeManagerListener listener : this.listeners) {
/*      */           try
/*      */           {
/*  232 */             listener.providerUpdated(provider);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  236 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void pluginRemoved(PluginInterface pi)
/*      */   {
/*  247 */     String plugin_id = pi.getPluginID();
/*      */     TranscodeProviderVuze provider;
/*  249 */     if (plugin_id.equals("vuzexcode"))
/*      */     {
/*  251 */       provider = null;
/*      */       
/*  253 */       synchronized (this)
/*      */       {
/*  255 */         if (this.vuzexcode_provider != null)
/*      */         {
/*  257 */           provider = this.vuzexcode_provider;
/*      */           
/*  259 */           this.vuzexcode_provider.destroy();
/*      */           
/*  261 */           this.vuzexcode_provider = null;
/*      */         }
/*      */       }
/*      */       
/*  265 */       if (provider != null)
/*      */       {
/*  267 */         for (TranscodeManagerListener listener : this.listeners) {
/*      */           try
/*      */           {
/*  270 */             listener.providerRemoved(provider);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  274 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateStatus(int tick_count)
/*      */   {
/*  285 */     if (this.queue != null)
/*      */     {
/*  287 */       this.queue.updateStatus(tick_count);
/*      */       
/*  289 */       if (!this.hooked_categories)
/*      */       {
/*  291 */         this.hooked_categories = true;
/*      */         
/*  293 */         CategoryManager.addCategoryManagerListener(new CategoryManagerListener()
/*      */         {
/*      */           public void categoryAdded(Category category) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void categoryRemoved(Category category) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void categoryChanged(Category category)
/*      */           {
/*  312 */             TranscodeManagerImpl.this.checkCategories();
/*      */           }
/*      */           
/*  315 */         });
/*  316 */         checkCategories();
/*      */       }
/*      */       
/*  319 */       if (!this.hooked_tags)
/*      */       {
/*  321 */         this.hooked_tags = true;
/*      */         
/*  323 */         TagManagerFactory.getTagManager().addTagFeatureListener(8, new TagFeatureListener()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void tagFeatureChanged(Tag tag, int feature)
/*      */           {
/*      */ 
/*      */ 
/*  332 */             TranscodeManagerImpl.this.checkTags();
/*      */           }
/*      */           
/*      */ 
/*  336 */         });
/*  337 */         checkTags();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkCategories()
/*      */   {
/*  345 */     Category[] cats = CategoryManager.getCategories();
/*      */     
/*  347 */     Map<Category, Object[]> active_map = new HashMap();
/*      */     
/*  349 */     for (Category cat : cats)
/*      */     {
/*  351 */       String target = cat.getStringAttribute("at_att");
/*      */       
/*  353 */       if (target != null)
/*      */       {
/*  355 */         String device_id = null;
/*      */         
/*  357 */         if (target.endsWith("/blank"))
/*      */         {
/*  359 */           device_id = target.substring(0, target.length() - 6);
/*      */         }
/*      */         
/*  362 */         DeviceMediaRenderer target_dmr = null;
/*  363 */         TranscodeProfile target_profile = null;
/*      */         
/*  365 */         for (DeviceImpl device : this.device_manager.getDevices())
/*      */         {
/*  367 */           if ((device instanceof DeviceMediaRenderer))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  372 */             DeviceMediaRenderer dmr = (DeviceMediaRenderer)device;
/*      */             
/*  374 */             if (device_id != null)
/*      */             {
/*  376 */               if (device.getID().equals(device_id))
/*      */               {
/*  378 */                 target_dmr = dmr;
/*  379 */                 target_profile = device.getBlankProfile();
/*      */                 
/*  381 */                 break;
/*      */               }
/*      */             }
/*      */             else {
/*  385 */               TranscodeProfile[] profs = device.getTranscodeProfiles();
/*      */               
/*  387 */               for (TranscodeProfile prof : profs)
/*      */               {
/*  389 */                 if (prof.getUID().equals(target))
/*      */                 {
/*  391 */                   target_dmr = dmr;
/*  392 */                   target_profile = prof;
/*      */                   
/*  394 */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  400 */         if (target_dmr != null)
/*      */         {
/*  402 */           active_map.put(cat, new Object[] { target_dmr, target_profile });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  407 */     Map<Category, Object[]> to_process = new HashMap();
/*      */     
/*  409 */     synchronized (this.category_map)
/*      */     {
/*  411 */       if (this.category_listener == null)
/*      */       {
/*  413 */         this.category_listener = new CategoryListener()
/*      */         {
/*      */           public void downloadManagerAdded(Category cat, org.gudy.azureus2.core3.download.DownloadManager manager)
/*      */           {
/*      */             Object[] details;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  423 */             synchronized (TranscodeManagerImpl.this.category_map)
/*      */             {
/*  425 */               details = (Object[])TranscodeManagerImpl.this.category_map.get(cat);
/*      */             }
/*      */             
/*  428 */             if (details != null)
/*      */             {
/*  430 */               TranscodeManagerImpl.this.processCategory(cat, details, manager);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           public void downloadManagerRemoved(Category cat, org.gudy.azureus2.core3.download.DownloadManager removed) {}
/*      */         };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  443 */       Iterator<Category> it = this.category_map.keySet().iterator();
/*      */       
/*  445 */       while (it.hasNext())
/*      */       {
/*  447 */         Category c = (Category)it.next();
/*      */         
/*  449 */         if (!active_map.containsKey(c))
/*      */         {
/*  451 */           c.removeCategoryListener(this.category_listener);
/*      */           
/*  453 */           it.remove();
/*      */         }
/*      */       }
/*      */       
/*  457 */       for (final Category c : active_map.keySet())
/*      */       {
/*  459 */         if (!this.category_map.containsKey(c))
/*      */         {
/*  461 */           to_process.put(c, active_map.get(c));
/*      */           
/*  463 */           c.addCategoryListener(this.category_listener);
/*      */           
/*  465 */           this.category_map.put(c, active_map.get(c));
/*      */           
/*  467 */           if (c.getType() == 2)
/*      */           {
/*  469 */             if (this.category_dl_listener == null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  475 */               this.category_dl_listener = new GlobalManagerAdapter()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void downloadManagerAdded(final org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */                 {
/*      */ 
/*  482 */                   new DelayedEvent("TM:cat-check", 10000L, new AERunnable()
/*      */                   {
/*      */ 
/*      */ 
/*      */                     public void runSupport()
/*      */                     {
/*      */ 
/*      */ 
/*  490 */                       Category dm_c = dm.getDownloadState().getCategory();
/*      */                       
/*  492 */                       if ((dm_c == null) || (dm_c == TranscodeManagerImpl.5.this.val$c))
/*      */                       {
/*      */                         Object[] details;
/*      */                         
/*      */ 
/*      */ 
/*  498 */                         synchronized (TranscodeManagerImpl.this.category_map)
/*      */                         {
/*  500 */                           details = (Object[])TranscodeManagerImpl.this.category_map.get(TranscodeManagerImpl.5.this.val$c);
/*      */                         }
/*      */                         
/*  503 */                         if (details != null)
/*      */                         {
/*  505 */                           TranscodeManagerImpl.this.processCategory(TranscodeManagerImpl.5.this.val$c, details, dm);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   });
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 public void downloadManagerRemoved(org.gudy.azureus2.core3.download.DownloadManager dm) {}
/*  518 */               };
/*  519 */               this.azureus_core.getGlobalManager().addListener(this.category_dl_listener, false);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     List<org.gudy.azureus2.core3.download.DownloadManager> downloads;
/*  526 */     if (to_process.size() > 0)
/*      */     {
/*  528 */       downloads = this.azureus_core.getGlobalManager().getDownloadManagers();
/*      */       
/*  530 */       for (Map.Entry<Category, Object[]> entry : to_process.entrySet())
/*      */       {
/*  532 */         c = (Category)entry.getKey();
/*  533 */         details = (Object[])entry.getValue();
/*      */         
/*  535 */         List<org.gudy.azureus2.core3.download.DownloadManager> list = c.getDownloadManagers(downloads);
/*      */         
/*  537 */         for (org.gudy.azureus2.core3.download.DownloadManager dm : list)
/*      */         {
/*  539 */           processCategory(c, details, dm);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     Category c;
/*      */     
/*      */     Object[] details;
/*      */   }
/*      */   
/*      */   private void processCategory(Category cat, Object[] details, org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */   {
/*  551 */     Download download = PluginCoreUtils.wrap(dm);
/*      */     
/*  553 */     if (download == null)
/*      */     {
/*  555 */       return;
/*      */     }
/*      */     
/*  558 */     if (download.getFlag(16L))
/*      */     {
/*  560 */       return;
/*      */     }
/*      */     
/*  563 */     String str = download.getAttribute(this.category_ta);
/*      */     
/*  565 */     String cat_name = cat.getName();
/*      */     
/*  567 */     if (cat.getType() == 2)
/*      */     {
/*  569 */       cat_name = "<none>";
/*      */     }
/*      */     
/*  572 */     String cat_tag = cat_name + ";";
/*      */     
/*  574 */     if ((str != null) && (str.contains(cat_tag)))
/*      */     {
/*  576 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  580 */       DeviceMediaRenderer device = (DeviceMediaRenderer)details[0];
/*  581 */       TranscodeProfile profile = (TranscodeProfile)details[1];
/*      */       
/*  583 */       log("Category " + cat_name + " - adding " + download.getName() + " to " + device.getName() + "/" + profile.getName());
/*      */       
/*  585 */       DiskManagerFileInfo[] dm_files = download.getDiskManagerFileInfo();
/*      */       
/*  587 */       int num_added = 0;
/*      */       
/*  589 */       for (DiskManagerFileInfo dm_file : dm_files)
/*      */       {
/*      */ 
/*      */ 
/*  593 */         if (num_added > 64) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  600 */         if ((dm_files.length == 1) || (dm_file.getLength() >= 131072L)) {
/*      */           try
/*      */           {
/*  603 */             this.queue.add(device, profile, dm_file, false);
/*      */             
/*  605 */             num_added++;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  609 */             log("    add failed", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  615 */       download.setAttribute(this.category_ta, str + cat_tag);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkTags()
/*      */   {
/*  624 */     TagManager tm = TagManagerFactory.getTagManager();
/*      */     
/*  626 */     Map<Tag, Object[]> active_map = new HashMap();
/*      */     
/*  628 */     for (TagType tt : tm.getTagTypes())
/*      */     {
/*  630 */       if (tt.hasTagTypeFeature(8L))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  636 */         for (Tag tag : tt.getTags())
/*      */         {
/*  638 */           TagFeatureTranscode tfx = (TagFeatureTranscode)tag;
/*      */           
/*  640 */           if (tfx.supportsTagTranscode())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  645 */             String[] target_details = tfx.getTagTranscodeTarget();
/*      */             
/*  647 */             if (target_details != null)
/*      */             {
/*  649 */               String target = target_details[0];
/*      */               
/*  651 */               String device_id = null;
/*      */               
/*  653 */               if (target.endsWith("/blank"))
/*      */               {
/*  655 */                 device_id = target.substring(0, target.length() - 6);
/*      */               }
/*      */               
/*  658 */               DeviceMediaRenderer target_dmr = null;
/*  659 */               TranscodeProfile target_profile = null;
/*      */               
/*  661 */               for (DeviceImpl device : this.device_manager.getDevices())
/*      */               {
/*  663 */                 if ((device instanceof DeviceMediaRenderer))
/*      */                 {
/*      */ 
/*      */ 
/*      */ 
/*  668 */                   DeviceMediaRenderer dmr = (DeviceMediaRenderer)device;
/*      */                   
/*  670 */                   if (device_id != null)
/*      */                   {
/*  672 */                     if (device.getID().equals(device_id))
/*      */                     {
/*  674 */                       target_dmr = dmr;
/*  675 */                       target_profile = device.getBlankProfile();
/*      */                       
/*  677 */                       break;
/*      */                     }
/*      */                   }
/*      */                   else {
/*  681 */                     TranscodeProfile[] profs = device.getTranscodeProfiles();
/*      */                     
/*  683 */                     for (TranscodeProfile prof : profs)
/*      */                     {
/*  685 */                       if (prof.getUID().equals(target))
/*      */                       {
/*  687 */                         target_dmr = dmr;
/*  688 */                         target_profile = prof;
/*      */                         
/*  690 */                         break;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*  696 */               if (target_dmr != null)
/*      */               {
/*  698 */                 active_map.put(tag, new Object[] { target_dmr, target_profile }); }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  704 */     Map<Tag, Object[]> to_process = new HashMap();
/*      */     
/*  706 */     synchronized (this.tag_map)
/*      */     {
/*  708 */       if (this.tag_listener == null)
/*      */       {
/*  710 */         this.tag_listener = new TagListener()
/*      */         {
/*      */           public void taggableAdded(Tag tag, Taggable tagged)
/*      */           {
/*      */             Object[] details;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  720 */             synchronized (TranscodeManagerImpl.this.tag_map)
/*      */             {
/*  722 */               details = (Object[])TranscodeManagerImpl.this.tag_map.get(tag);
/*      */             }
/*      */             
/*  725 */             if (details != null)
/*      */             {
/*  727 */               TranscodeManagerImpl.this.processTag(tag, details, (org.gudy.azureus2.core3.download.DownloadManager)tagged);
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void taggableSync(Tag tag) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void taggableRemoved(Tag tag, Taggable tagged) {}
/*      */         };
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  748 */       Iterator<Tag> it = this.tag_map.keySet().iterator();
/*      */       
/*  750 */       while (it.hasNext())
/*      */       {
/*  752 */         Tag t = (Tag)it.next();
/*      */         
/*  754 */         if (!active_map.containsKey(t))
/*      */         {
/*  756 */           t.removeTagListener(this.tag_listener);
/*      */           
/*  758 */           it.remove();
/*      */         }
/*      */       }
/*      */       
/*  762 */       for (Tag tag : active_map.keySet())
/*      */       {
/*  764 */         if (!this.tag_map.containsKey(tag))
/*      */         {
/*  766 */           to_process.put(tag, active_map.get(tag));
/*      */           
/*  768 */           tag.addTagListener(this.tag_listener, false);
/*      */           
/*  770 */           this.tag_map.put(tag, active_map.get(tag));
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  775 */     if (to_process.size() > 0)
/*      */     {
/*  777 */       for (Map.Entry<Tag, Object[]> entry : to_process.entrySet())
/*      */       {
/*  779 */         tag = (Tag)entry.getKey();
/*  780 */         details = (Object[])entry.getValue();
/*      */         
/*  782 */         Set<org.gudy.azureus2.core3.download.DownloadManager> list = ((TagDownload)tag).getTaggedDownloads();
/*      */         
/*  784 */         for (org.gudy.azureus2.core3.download.DownloadManager dm : list)
/*      */         {
/*  786 */           processTag(tag, details, dm);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     Tag tag;
/*      */     
/*      */     Object[] details;
/*      */   }
/*      */   
/*      */ 
/*      */   private void processTag(Tag tag, Object[] details, org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */   {
/*  800 */     Download download = PluginCoreUtils.wrap(dm);
/*      */     
/*  802 */     if (download == null)
/*      */     {
/*  804 */       return;
/*      */     }
/*      */     
/*  807 */     if (download.getFlag(16L))
/*      */     {
/*  809 */       return;
/*      */     }
/*      */     
/*  812 */     String str = download.getAttribute(this.tag_ta);
/*      */     
/*  814 */     String tag_name = tag.getTagName(true);
/*      */     
/*  816 */     String tag_tag = tag.getTagType().getTagType() + "." + tag.getTagID() + ";";
/*      */     
/*  818 */     if ((str != null) && (str.contains(tag_tag)))
/*      */     {
/*  820 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  824 */       DeviceMediaRenderer device = (DeviceMediaRenderer)details[0];
/*  825 */       TranscodeProfile profile = (TranscodeProfile)details[1];
/*      */       
/*  827 */       log("Tag " + tag_name + " - adding " + download.getName() + " to " + device.getName() + "/" + profile.getName());
/*      */       
/*  829 */       DiskManagerFileInfo[] dm_files = download.getDiskManagerFileInfo();
/*      */       
/*  831 */       int num_added = 0;
/*      */       
/*  833 */       for (DiskManagerFileInfo dm_file : dm_files)
/*      */       {
/*      */ 
/*      */ 
/*  837 */         if (num_added > 64) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  844 */         if ((dm_files.length == 1) || (dm_file.getLength() >= 131072L)) {
/*      */           try
/*      */           {
/*  847 */             this.queue.add(device, profile, dm_file, false);
/*      */             
/*  849 */             num_added++;
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  853 */             log("    add failed", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  859 */       download.setAttribute(this.tag_ta, str + tag_tag);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeProvider[] getProviders()
/*      */   {
/*  866 */     TranscodeProviderVuze vp = this.vuzexcode_provider;
/*      */     
/*  868 */     if (vp == null)
/*      */     {
/*  870 */       return new TranscodeProvider[0];
/*      */     }
/*      */     
/*  873 */     return new TranscodeProvider[] { vp };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TranscodeProvider getProvider(int p_id)
/*      */     throws TranscodeException
/*      */   {
/*  882 */     TranscodeProviderVuze vp = this.vuzexcode_provider;
/*      */     
/*  884 */     if ((p_id == 1) && (vp != null))
/*      */     {
/*  886 */       return vp;
/*      */     }
/*      */     
/*  889 */     throw new TranscodeException("Transcode provider not registered");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected TranscodeProfile getProfileFromUID(String uid)
/*      */   {
/*  896 */     for (TranscodeProvider provider : getProviders())
/*      */     {
/*  898 */       TranscodeProfile profile = provider.getProfile(uid);
/*      */       
/*  900 */       if (profile != null)
/*      */       {
/*  902 */         return profile;
/*      */       }
/*      */     }
/*      */     
/*  906 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public TranscodeQueueImpl getQueue()
/*      */   {
/*  912 */     if (!this.init_sem.reserve(30000L))
/*      */     {
/*  914 */       Debug.out("Timeout waiting for init");
/*      */       
/*  916 */       AEDiagnostics.dumpThreads();
/*      */     }
/*      */     
/*  919 */     return this.queue;
/*      */   }
/*      */   
/*      */ 
/*      */   protected DeviceManagerImpl getManager()
/*      */   {
/*  925 */     return this.device_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TranscodeTarget lookupTarget(String target_id)
/*      */     throws TranscodeException
/*      */   {
/*  934 */     Device device = this.device_manager.getDevice(target_id);
/*      */     
/*  936 */     if ((device instanceof TranscodeTarget))
/*      */     {
/*  938 */       return (TranscodeTarget)device;
/*      */     }
/*      */     
/*  941 */     throw new TranscodeException("Transcode target with id " + target_id + " not found");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DiskManagerFileInfo lookupFile(byte[] hash, int index)
/*      */     throws TranscodeException
/*      */   {
/*      */     try
/*      */     {
/*  952 */       Download download = PluginInitializer.getDefaultInterface().getDownloadManager().getDownload(hash);
/*      */       
/*  954 */       if (download == null)
/*      */       {
/*  956 */         throw new TranscodeException("Download with hash " + ByteFormatter.encodeString(hash) + " not found");
/*      */       }
/*      */       
/*  959 */       return download.getDiskManagerFileInfo()[index];
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  963 */       throw new TranscodeException("Download with hash " + ByteFormatter.encodeString(hash) + " not found", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void close()
/*      */   {
/*  971 */     this.queue.close();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(TranscodeManagerListener listener)
/*      */   {
/*  978 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TranscodeManagerListener listener)
/*      */   {
/*  985 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/*  992 */     this.device_manager.log("Trans: " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1000 */     this.device_manager.log("Trans: " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1007 */     writer.println("Transcode Manager: vuze provider=" + this.vuzexcode_provider);
/*      */     
/* 1009 */     this.queue.generate(writer);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */