/*      */ package org.gudy.azureus2.pluginsimpl.local.download;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateFactory;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerDefaultPaths;
/*      */ import org.gudy.azureus2.core3.download.impl.DownloadManagerMoveHandler;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadRemovalVetoException;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerDownloadWillBeRemovedListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.BDecoder;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteArrayHashMap;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadEventNotifier;
/*      */ import org.gudy.azureus2.plugins.download.DownloadException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStub;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStubEvent;
/*      */ import org.gudy.azureus2.plugins.download.DownloadStubListener;
/*      */ import org.gudy.azureus2.plugins.download.DownloadWillBeAddedListener;
/*      */ import org.gudy.azureus2.plugins.download.savelocation.DefaultSaveLocationManager;
/*      */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationManager;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DownloadManagerImpl
/*      */   implements org.gudy.azureus2.plugins.download.DownloadManager, DownloadManagerInitialisationAdapter
/*      */ {
/*      */   protected static DownloadManagerImpl singleton;
/*   74 */   protected static AEMonitor class_mon = new AEMonitor("DownloadManager:class");
/*      */   private final GlobalManager global_manager;
/*      */   private final DownloadManagerStats stats;
/*      */   private final DownloadEventNotifierImpl global_dl_notifier;
/*      */   private final TagManager tag_manager;
/*      */   
/*      */   public static DownloadManagerImpl getSingleton(AzureusCore azureus_core) {
/*   81 */     try { class_mon.enter();
/*      */       
/*   83 */       if (singleton == null)
/*      */       {
/*   85 */         singleton = new DownloadManagerImpl(azureus_core);
/*      */       }
/*      */       
/*   88 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*   92 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  102 */   private List<DownloadManagerListener> listeners = new ArrayList();
/*  103 */   private CopyOnWriteList<DownloadWillBeAddedListener> dwba_listeners = new CopyOnWriteList();
/*  104 */   private AEMonitor listeners_mon = new AEMonitor("DownloadManager:L");
/*      */   
/*  106 */   private List<Download> downloads = new ArrayList();
/*  107 */   private Map<org.gudy.azureus2.core3.download.DownloadManager, DownloadImpl> pending_dls = new IdentityHashMap();
/*  108 */   private Map<org.gudy.azureus2.core3.download.DownloadManager, DownloadImpl> download_map = new IdentityHashMap();
/*      */   
/*      */   private static final String STUB_CONFIG_FILE = "dlarchive.config";
/*      */   
/*      */ 
/*      */   protected DownloadManagerImpl(AzureusCore _azureus_core)
/*      */   {
/*  115 */     this.global_manager = _azureus_core.getGlobalManager();
/*      */     
/*  117 */     this.stats = new DownloadManagerStatsImpl(this.global_manager);
/*      */     
/*  119 */     this.global_dl_notifier = new DownloadEventNotifierImpl(this);
/*      */     
/*  121 */     this.tag_manager = TagManagerFactory.getTagManager();
/*      */     
/*  123 */     readStubConfig();
/*      */     
/*  125 */     this.global_manager.addListener(new GlobalManagerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void downloadManagerAdded(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */       {
/*      */ 
/*  132 */         DownloadManagerImpl.this.addDownloadManager(dm);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void downloadManagerRemoved(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */       {
/*  139 */         List<DownloadManagerListener> listeners_ref = null;
/*  140 */         DownloadImpl dl = null;
/*      */         try
/*      */         {
/*  143 */           DownloadManagerImpl.this.listeners_mon.enter();
/*      */           
/*  145 */           dl = (DownloadImpl)DownloadManagerImpl.this.download_map.get(dm);
/*      */           
/*  147 */           if (dl == null)
/*      */           {
/*  149 */             System.out.println("DownloadManager:unknown manager removed");
/*      */           }
/*      */           else
/*      */           {
/*  153 */             DownloadManagerImpl.this.downloads.remove(dl);
/*      */             
/*  155 */             DownloadManagerImpl.this.download_map.remove(dm);
/*      */             
/*  157 */             DownloadManagerImpl.this.pending_dls.remove(dm);
/*      */             
/*  159 */             dl.destroy();
/*      */             
/*  161 */             listeners_ref = DownloadManagerImpl.this.listeners;
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/*  166 */           DownloadManagerImpl.this.listeners_mon.exit();
/*      */         }
/*      */         
/*  169 */         if (dl != null)
/*      */         {
/*  171 */           for (int i = 0; i < listeners_ref.size(); i++) {
/*      */             try
/*      */             {
/*  174 */               ((DownloadManagerListener)listeners_ref.get(i)).downloadRemoved(dl);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  178 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void destroyInitiated() {}
/*      */       
/*      */ 
/*      */ 
/*      */       public void destroyed()
/*      */       {
/*  192 */         synchronized (DownloadManagerImpl.this.download_stubs)
/*      */         {
/*  194 */           if (DownloadManagerImpl.this.dirty_stubs)
/*      */           {
/*  196 */             DownloadManagerImpl.this.writeStubConfig();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void seedingStatusChanged(boolean seeding_only_mode, boolean b) {}
/*  206 */     });
/*  207 */     this.global_manager.addDownloadWillBeRemovedListener(new GlobalManagerDownloadWillBeRemovedListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void downloadWillBeRemoved(org.gudy.azureus2.core3.download.DownloadManager dm, boolean remove_torrent, boolean remove_data)
/*      */         throws GlobalManagerDownloadRemovalVetoException
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  218 */         DownloadImpl download = (DownloadImpl)DownloadManagerImpl.this.download_map.get(dm);
/*      */         
/*  220 */         if (download != null) {
/*      */           try
/*      */           {
/*  223 */             download.isRemovable();
/*      */           }
/*      */           catch (DownloadRemovalVetoException e)
/*      */           {
/*  227 */             throw new GlobalManagerDownloadRemovalVetoException(e.getMessage(), e.isSilent());
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDownload(File fileName)
/*      */   {
/*  238 */     UIManagerImpl.fireEvent(null, 2, fileName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addDownload(URL url)
/*      */   {
/*  245 */     addDownload(url, null, true, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDownload(URL url, boolean auto_download)
/*      */     throws DownloadException
/*      */   {
/*  255 */     addDownload(url, null, auto_download, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDownload(URL url, URL referrer)
/*      */   {
/*  263 */     addDownload(url, referrer, true, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDownload(URL url, Map request_properties)
/*      */   {
/*  271 */     addDownload(url, null, true, request_properties);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDownload(URL url, URL referrer, boolean auto_download, Map request_properties)
/*      */   {
/*  281 */     UIManagerImpl.fireEvent(null, 3, new Object[] { url, referrer, Boolean.valueOf(auto_download), request_properties });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addDownloadManager(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */   {
/*  290 */     List<DownloadManagerListener> listeners_ref = null;
/*  291 */     DownloadImpl dl = null;
/*      */     try
/*      */     {
/*  294 */       this.listeners_mon.enter();
/*      */       
/*  296 */       if (this.download_map.get(dm) == null)
/*      */       {
/*  298 */         dl = (DownloadImpl)this.pending_dls.remove(dm);
/*      */         
/*  300 */         if (dl == null)
/*      */         {
/*  302 */           dl = new DownloadImpl(this, dm);
/*      */         }
/*      */         
/*  305 */         this.downloads.add(dl);
/*      */         
/*  307 */         this.download_map.put(dm, dl);
/*      */         
/*  309 */         listeners_ref = this.listeners;
/*      */       }
/*      */     }
/*      */     finally {
/*  313 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  316 */     if (dl != null)
/*      */     {
/*  318 */       for (int i = 0; i < listeners_ref.size(); i++) {
/*      */         try
/*      */         {
/*  321 */           ((DownloadManagerListener)listeners_ref.get(i)).downloadAdded(dl);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  325 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download addDownload(Torrent torrent)
/*      */     throws DownloadException
/*      */   {
/*  337 */     return addDownload(torrent, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download addDownload(Torrent torrent, File torrent_file, File data_location)
/*      */     throws DownloadException
/*      */   {
/*  348 */     return addDownload(torrent, torrent_file, data_location, getInitialState());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download addDownload(Torrent torrent, File torrent_file, File data_location, int initial_state)
/*      */     throws DownloadException
/*      */   {
/*  360 */     if (torrent_file == null)
/*      */     {
/*  362 */       String torrent_dir = null;
/*      */       
/*  364 */       if (COConfigurationManager.getBooleanParameter("Save Torrent Files"))
/*      */       {
/*      */         try
/*      */         {
/*  368 */           torrent_dir = COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory");
/*      */         }
/*      */         catch (Exception egnore) {}
/*      */       }
/*      */       
/*  373 */       if ((torrent_dir == null) || (torrent_dir.length() == 0))
/*      */       {
/*  375 */         throw new DownloadException("DownloadManager::addDownload: default torrent save directory must be configured");
/*      */       }
/*      */       
/*  378 */       torrent_file = new File(torrent_dir + File.separator + torrent.getName() + ".torrent");
/*      */       try
/*      */       {
/*  381 */         torrent.writeToFile(torrent_file);
/*      */       }
/*      */       catch (TorrentException e)
/*      */       {
/*  385 */         throw new DownloadException("DownloadManager::addDownload: failed to write torrent to '" + torrent_file.toString() + "'", e);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  390 */       if (!torrent_file.exists()) {
/*  391 */         throw new DownloadException("DownloadManager::addDownload: torrent file does not exist - " + torrent_file.toString());
/*      */       }
/*  393 */       if (!torrent_file.isFile()) {
/*  394 */         throw new DownloadException("DownloadManager::addDownload: torrent filepath given is not a file - " + torrent_file.toString());
/*      */       }
/*      */     }
/*      */     
/*  398 */     if (data_location == null)
/*      */     {
/*  400 */       String data_dir = COConfigurationManager.getStringParameter("Default save path");
/*      */       
/*  402 */       if ((data_dir == null) || (data_dir.length() == 0))
/*      */       {
/*  404 */         throw new DownloadException("DownloadManager::addDownload: default data save directory must be configured");
/*      */       }
/*      */       
/*  407 */       data_location = new File(data_dir);
/*      */       
/*  409 */       FileUtil.mkdirs(data_location);
/*      */     }
/*      */     
/*  412 */     byte[] hash = null;
/*      */     try {
/*  414 */       hash = torrent.getHash();
/*      */     }
/*      */     catch (Exception e) {}
/*  417 */     boolean for_seeding = torrent.isComplete();
/*      */     
/*  419 */     org.gudy.azureus2.core3.download.DownloadManager dm = this.global_manager.addDownloadManager(torrent_file.toString(), hash, data_location.toString(), initial_state, true, for_seeding, null);
/*      */     
/*      */ 
/*      */ 
/*  423 */     if (dm == null)
/*      */     {
/*  425 */       throw new DownloadException("DownloadManager::addDownload - failed, download may already in the process of being added");
/*      */     }
/*      */     
/*  428 */     addDownloadManager(dm);
/*      */     
/*  430 */     return getDownload(dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download addDownloadStopped(Torrent torrent, File torrent_location, File data_location)
/*      */     throws DownloadException
/*      */   {
/*  441 */     return addDownload(torrent, torrent_location, data_location, 70);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download addNonPersistentDownload(Torrent torrent, File torrent_file, File data_location)
/*      */     throws DownloadException
/*      */   {
/*  453 */     byte[] hash = null;
/*      */     try {
/*  455 */       hash = torrent.getHash();
/*      */     }
/*      */     catch (Exception e) {}
/*  458 */     org.gudy.azureus2.core3.download.DownloadManager dm = this.global_manager.addDownloadManager(torrent_file.toString(), hash, data_location.toString(), getInitialState(), false);
/*      */     
/*      */ 
/*      */ 
/*  462 */     if (dm == null)
/*      */     {
/*  464 */       throw new DownloadException("DownloadManager::addDownload - failed");
/*      */     }
/*      */     
/*  467 */     addDownloadManager(dm);
/*      */     
/*  469 */     return getDownload(dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download addNonPersistentDownloadStopped(Torrent torrent, File torrent_file, File data_location)
/*      */     throws DownloadException
/*      */   {
/*  481 */     byte[] hash = null;
/*      */     try {
/*  483 */       hash = torrent.getHash();
/*      */     }
/*      */     catch (Exception e) {}
/*  486 */     org.gudy.azureus2.core3.download.DownloadManager dm = this.global_manager.addDownloadManager(torrent_file.toString(), hash, data_location.toString(), 70, false);
/*      */     
/*      */ 
/*      */ 
/*  490 */     if (dm == null)
/*      */     {
/*  492 */       throw new DownloadException("DownloadManager::addDownload - failed");
/*      */     }
/*      */     
/*  495 */     addDownloadManager(dm);
/*      */     
/*  497 */     return getDownload(dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clearNonPersistentDownloadState(byte[] hash)
/*      */   {
/*  504 */     this.global_manager.clearNonPersistentDownloadState(hash);
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getInitialState()
/*      */   {
/*  510 */     boolean default_start_stopped = COConfigurationManager.getBooleanParameter("Default Start Torrents Stopped");
/*      */     
/*  512 */     return default_start_stopped ? 70 : 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DownloadImpl getDownload(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */     throws DownloadException
/*      */   {
/*  521 */     DownloadImpl dl = (DownloadImpl)this.download_map.get(dm);
/*      */     
/*  523 */     if (dl == null)
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*  528 */         this.listeners_mon.enter();
/*      */         
/*  530 */         dl = (DownloadImpl)this.download_map.get(dm);
/*      */         
/*  532 */         if (dl != null)
/*      */         {
/*  534 */           return dl;
/*      */         }
/*      */         
/*  537 */         dl = (DownloadImpl)this.pending_dls.get(dm);
/*      */       }
/*      */       finally
/*      */       {
/*  541 */         this.listeners_mon.exit();
/*      */       }
/*      */       
/*  544 */       if (dl != null)
/*      */       {
/*  546 */         long now = SystemTime.getMonotonousTime();
/*      */         
/*      */ 
/*      */ 
/*      */         for (;;)
/*      */         {
/*  552 */           DownloadImpl dl2 = (DownloadImpl)this.download_map.get(dm);
/*      */           
/*  554 */           if (dl2 != null)
/*      */           {
/*  556 */             return dl2;
/*      */           }
/*      */           
/*  559 */           if (SystemTime.getMonotonousTime() - now > 5000L) {
/*      */             break;
/*      */           }
/*      */           
/*      */           try
/*      */           {
/*  565 */             Thread.sleep(100L);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/*      */ 
/*  571 */         return dl;
/*      */       }
/*      */       
/*  574 */       throw new DownloadException("DownloadManager::getDownload: download not found");
/*      */     }
/*      */     
/*  577 */     return dl;
/*      */   }
/*      */   
/*      */   public static DownloadImpl[] getDownloadStatic(org.gudy.azureus2.core3.download.DownloadManager[] dm) {
/*  581 */     ArrayList res = new ArrayList(dm.length);
/*  582 */     for (int i = 0; i < dm.length; i++) {
/*  583 */       try { res.add(getDownloadStatic(dm[i]));
/*      */       } catch (DownloadException de) {}
/*      */     }
/*  586 */     return (DownloadImpl[])res.toArray(new DownloadImpl[res.size()]);
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
/*      */   public static DownloadImpl getDownloadStatic(org.gudy.azureus2.core3.download.DownloadManager dm)
/*      */     throws DownloadException
/*      */   {
/*  602 */     if (singleton != null)
/*      */     {
/*  604 */       return singleton.getDownload(dm);
/*      */     }
/*      */     
/*  607 */     throw new DownloadException("DownloadManager not initialised");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Download getDownloadStatic(DiskManager dm)
/*      */     throws DownloadException
/*      */   {
/*  616 */     if (singleton != null)
/*      */     {
/*  618 */       return singleton.getDownload(dm);
/*      */     }
/*      */     
/*  621 */     throw new DownloadException("DownloadManager not initialised");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download getDownload(DiskManager dm)
/*      */     throws DownloadException
/*      */   {
/*  630 */     List<org.gudy.azureus2.core3.download.DownloadManager> dls = this.global_manager.getDownloadManagers();
/*      */     
/*  632 */     for (int i = 0; i < dls.size(); i++)
/*      */     {
/*  634 */       org.gudy.azureus2.core3.download.DownloadManager man = (org.gudy.azureus2.core3.download.DownloadManager)dls.get(i);
/*      */       
/*  636 */       if (man.getDiskManager() == dm)
/*      */       {
/*  638 */         return getDownload(man.getTorrent());
/*      */       }
/*      */     }
/*      */     
/*  642 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Download getDownload(TOTorrent torrent)
/*      */     throws DownloadException
/*      */   {
/*  651 */     if (torrent != null)
/*      */     {
/*  653 */       for (int i = 0; i < this.downloads.size(); i++)
/*      */       {
/*  655 */         Download dl = (Download)this.downloads.get(i);
/*      */         
/*  657 */         TorrentImpl t = (TorrentImpl)dl.getTorrent();
/*      */         
/*      */ 
/*      */ 
/*  661 */         if (t != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  666 */           if (t.getTorrent().hasSameHashAs(torrent))
/*      */           {
/*  668 */             return dl;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  673 */     throw new DownloadException("DownloadManager::getDownload: download not found");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Download getDownloadStatic(TOTorrent torrent)
/*      */     throws DownloadException
/*      */   {
/*  682 */     if (singleton != null)
/*      */     {
/*  684 */       return singleton.getDownload(torrent);
/*      */     }
/*      */     
/*  687 */     throw new DownloadException("DownloadManager not initialised");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Download getDownload(Torrent _torrent)
/*      */   {
/*  694 */     TorrentImpl torrent = (TorrentImpl)_torrent;
/*      */     try
/*      */     {
/*  697 */       return getDownload(torrent.getTorrent());
/*      */     }
/*      */     catch (DownloadException e) {}
/*      */     
/*      */ 
/*  702 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Download getDownload(byte[] hash)
/*      */   {
/*  709 */     org.gudy.azureus2.core3.download.DownloadManager manager = this.global_manager.getDownloadManager(new HashWrapper(hash));
/*  710 */     if (manager != null) {
/*      */       try {
/*  712 */         return getDownload(manager);
/*      */       }
/*      */       catch (DownloadException e) {}
/*      */     }
/*      */     
/*  717 */     List dls = this.global_manager.getDownloadManagers();
/*      */     
/*  719 */     for (int i = 0; i < dls.size(); i++)
/*      */     {
/*  721 */       org.gudy.azureus2.core3.download.DownloadManager man = (org.gudy.azureus2.core3.download.DownloadManager)dls.get(i);
/*      */       
/*      */ 
/*      */ 
/*  725 */       TOTorrent torrent = man.getTorrent();
/*      */       
/*  727 */       if (torrent != null) {
/*      */         try
/*      */         {
/*  730 */           if (Arrays.equals(torrent.getHash(), hash))
/*      */           {
/*  732 */             return getDownload(torrent);
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (DownloadException e) {}catch (TOTorrentException e)
/*      */         {
/*      */ 
/*  740 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  745 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Download[] getDownloads()
/*      */   {
/*  754 */     List<org.gudy.azureus2.core3.download.DownloadManager> dms = this.global_manager.getDownloadManagers();
/*      */     
/*      */     Set<Download> res_l;
/*      */     try
/*      */     {
/*  759 */       this.listeners_mon.enter();
/*      */       
/*  761 */       res_l = new LinkedHashSet(this.downloads.size());
/*      */       
/*  763 */       for (int i = 0; i < dms.size(); i++)
/*      */       {
/*  765 */         DownloadImpl dl = (DownloadImpl)this.download_map.get(dms.get(i));
/*      */         
/*  767 */         if (dl != null)
/*      */         {
/*  769 */           res_l.add(dl);
/*      */         }
/*      */       }
/*      */       
/*  773 */       if (res_l.size() < this.downloads.size())
/*      */       {
/*      */ 
/*      */ 
/*  777 */         for (int i = 0; i < this.downloads.size(); i++)
/*      */         {
/*  779 */           Download download = (Download)this.downloads.get(i);
/*      */           
/*  781 */           if (!res_l.contains(download))
/*      */           {
/*  783 */             res_l.add(download);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  789 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  792 */     Download[] res = new Download[res_l.size()];
/*      */     
/*  794 */     res_l.toArray(res);
/*      */     
/*  796 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public Download[] getDownloads(boolean bSorted)
/*      */   {
/*  802 */     if (bSorted)
/*      */     {
/*  804 */       return getDownloads();
/*      */     }
/*      */     try
/*      */     {
/*  808 */       this.listeners_mon.enter();
/*      */       
/*  810 */       Download[] res = new Download[this.downloads.size()];
/*      */       
/*  812 */       this.downloads.toArray(res);
/*      */       
/*  814 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  818 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void pauseDownloads()
/*      */   {
/*  825 */     this.global_manager.pauseDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canPauseDownloads()
/*      */   {
/*  831 */     return this.global_manager.canPauseDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   public void resumeDownloads()
/*      */   {
/*  837 */     this.global_manager.resumeDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canResumeDownloads()
/*      */   {
/*  843 */     return this.global_manager.canResumeDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   public void startAllDownloads()
/*      */   {
/*  849 */     this.global_manager.startAllDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   public void stopAllDownloads()
/*      */   {
/*  855 */     this.global_manager.stopAllDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadManagerStats getStats()
/*      */   {
/*  861 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSeedingOnly()
/*      */   {
/*  867 */     return this.global_manager.isSeedingOnly();
/*      */   }
/*      */   
/*  870 */   public void addListener(DownloadManagerListener l) { addListener(l, true); }
/*      */   
/*      */   public void addListener(DownloadManagerListener l, boolean notify_of_current_downloads) {
/*  873 */     List<Download> downloads_copy = null;
/*      */     try
/*      */     {
/*  876 */       this.listeners_mon.enter();
/*  877 */       List<DownloadManagerListener> new_listeners = new ArrayList(this.listeners);
/*  878 */       new_listeners.add(l);
/*  879 */       this.listeners = new_listeners;
/*  880 */       if (notify_of_current_downloads) {
/*  881 */         downloads_copy = new ArrayList(this.downloads);
/*      */         
/*  883 */         Collections.shuffle(downloads_copy);
/*      */       }
/*      */     }
/*      */     finally {
/*  887 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  890 */     if (downloads_copy != null)
/*  891 */       for (int i = 0; i < downloads_copy.size(); i++) {
/*  892 */         try { l.downloadAdded((Download)downloads_copy.get(i));
/*  893 */         } catch (Throwable e) { Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */   }
/*      */   
/*  898 */   public void removeListener(DownloadManagerListener l) { removeListener(l, false); }
/*      */   
/*      */   public void removeListener(DownloadManagerListener l, boolean notify_of_current_downloads) {
/*  901 */     List<Download> downloads_copy = null;
/*      */     try
/*      */     {
/*  904 */       this.listeners_mon.enter();
/*  905 */       List<DownloadManagerListener> new_listeners = new ArrayList(this.listeners);
/*  906 */       new_listeners.remove(l);
/*  907 */       this.listeners = new_listeners;
/*  908 */       if (notify_of_current_downloads) {
/*  909 */         downloads_copy = new ArrayList(this.downloads);
/*      */       }
/*      */     }
/*      */     finally {
/*  913 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  916 */     if (downloads_copy != null) {
/*  917 */       for (int i = 0; i < downloads_copy.size(); i++) {
/*  918 */         try { l.downloadRemoved((Download)downloads_copy.get(i));
/*  919 */         } catch (Throwable e) { Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialised(org.gudy.azureus2.core3.download.DownloadManager manager, boolean for_seeding)
/*      */   {
/*      */     DownloadImpl dl;
/*      */     
/*      */     try
/*      */     {
/*  933 */       this.listeners_mon.enter();
/*      */       
/*  935 */       dl = new DownloadImpl(this, manager);
/*      */       
/*  937 */       this.pending_dls.put(manager, dl);
/*      */     }
/*      */     finally
/*      */     {
/*  941 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/*  944 */     Object it = this.dwba_listeners.iterator();
/*      */     
/*  946 */     while (((Iterator)it).hasNext()) {
/*      */       try
/*      */       {
/*  949 */         ((DownloadWillBeAddedListener)((Iterator)it).next()).initialised(dl);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  953 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getActions()
/*      */   {
/*  964 */     if (this.dwba_listeners.size() > 0)
/*      */     {
/*  966 */       return 1;
/*      */     }
/*      */     
/*  969 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addDownloadWillBeAddedListener(DownloadWillBeAddedListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  977 */       this.listeners_mon.enter();
/*      */       
/*  979 */       this.dwba_listeners.add(listener);
/*      */       
/*  981 */       if (this.dwba_listeners.size() == 1)
/*      */       {
/*  983 */         this.global_manager.addDownloadManagerInitialisationAdapter(this);
/*      */       }
/*      */     }
/*      */     finally {
/*  987 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeDownloadWillBeAddedListener(DownloadWillBeAddedListener listener)
/*      */   {
/*      */     try
/*      */     {
/*  996 */       this.listeners_mon.enter();
/*      */       
/*  998 */       this.dwba_listeners.remove(listener);
/*      */       
/* 1000 */       if (this.dwba_listeners.size() == 0)
/*      */       {
/* 1002 */         this.global_manager.removeDownloadManagerInitialisationAdapter(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1006 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addExternalDownload(Download download)
/*      */   {
/* 1014 */     List<DownloadManagerListener> listeners_ref = null;
/*      */     try
/*      */     {
/* 1017 */       this.listeners_mon.enter();
/*      */       
/* 1019 */       if (this.downloads.contains(download)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 1024 */       this.downloads.add(download);
/*      */       
/* 1026 */       listeners_ref = this.listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 1030 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/* 1033 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 1036 */         ((DownloadManagerListener)listeners_ref.get(i)).downloadAdded(download);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1040 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeExternalDownload(Download download)
/*      */   {
/* 1049 */     List<DownloadManagerListener> listeners_ref = null;
/*      */     try
/*      */     {
/* 1052 */       this.listeners_mon.enter();
/*      */       
/* 1054 */       if (!this.downloads.contains(download)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 1059 */       this.downloads.remove(download);
/*      */       
/* 1061 */       listeners_ref = this.listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 1065 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/* 1068 */     for (int i = 0; i < listeners_ref.size(); i++) {
/*      */       try
/*      */       {
/* 1071 */         ((DownloadManagerListener)listeners_ref.get(i)).downloadRemoved(download);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1075 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public DownloadEventNotifier getGlobalDownloadEventNotifier() {
/* 1081 */     return this.global_dl_notifier;
/*      */   }
/*      */   
/*      */   public void setSaveLocationManager(SaveLocationManager manager) {
/* 1085 */     if (manager == null) manager = getDefaultSaveLocationManager();
/* 1086 */     DownloadManagerMoveHandler.CURRENT_HANDLER = manager;
/*      */   }
/*      */   
/*      */   public SaveLocationManager getSaveLocationManager() {
/* 1090 */     return DownloadManagerMoveHandler.CURRENT_HANDLER;
/*      */   }
/*      */   
/*      */   public DefaultSaveLocationManager getDefaultSaveLocationManager() {
/* 1094 */     return DownloadManagerDefaultPaths.DEFAULT_HANDLER;
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
/* 1106 */   private static final File ARCHIVE_DIR = FileUtil.getUserFile("dlarchive");
/*      */   
/* 1108 */   static { if (!ARCHIVE_DIR.exists())
/*      */     {
/* 1110 */       FileUtil.mkdirs(ARCHIVE_DIR);
/*      */     }
/*      */   }
/*      */   
/* 1114 */   private List<DownloadStubImpl> download_stubs = new ArrayList();
/* 1115 */   private ByteArrayHashMap<DownloadStubImpl> download_stub_map = new ByteArrayHashMap();
/*      */   
/* 1117 */   private CopyOnWriteList<DownloadStubListener> download_stub_listeners = new CopyOnWriteList();
/*      */   
/* 1119 */   private FrequencyLimitedDispatcher dirty_stub_dispatcher = new FrequencyLimitedDispatcher(new AERunnable()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void runSupport()
/*      */     {
/*      */ 
/* 1126 */       synchronized (DownloadManagerImpl.this.download_stubs)
/*      */       {
/* 1128 */         DownloadManagerImpl.this.writeStubConfig();
/*      */       }
/*      */     }
/* 1119 */   }, 10000);
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
/* 1134 */   private boolean dirty_stubs = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private void readStubConfig()
/*      */   {
/* 1140 */     if (FileUtil.resilientConfigFileExists("dlarchive.config"))
/*      */     {
/* 1142 */       Map map = FileUtil.readResilientConfigFile("dlarchive.config");
/*      */       
/* 1144 */       List<Map> list = (List)map.get("stubs");
/*      */       
/* 1146 */       if (list != null)
/*      */       {
/* 1148 */         for (Map m : list)
/*      */         {
/* 1150 */           DownloadStubImpl stub = new DownloadStubImpl(this, m);
/*      */           
/* 1152 */           this.download_stubs.add(stub);
/*      */           
/* 1154 */           this.download_stub_map.put(stub.getTorrentHash(), stub);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void writeStubConfig()
/*      */   {
/* 1163 */     if (this.download_stubs.size() == 0)
/*      */     {
/* 1165 */       FileUtil.deleteResilientConfigFile("dlarchive.config");
/*      */     }
/*      */     else
/*      */     {
/* 1169 */       Map map = new HashMap();
/*      */       
/* 1171 */       List list = new ArrayList(this.download_stubs.size());
/*      */       
/* 1173 */       map.put("stubs", list);
/*      */       
/* 1175 */       for (DownloadStubImpl stub : this.download_stubs)
/*      */       {
/* 1177 */         list.add(stub.exportToMap());
/*      */       }
/*      */       
/* 1180 */       FileUtil.writeResilientConfigFile("dlarchive.config", map);
/*      */     }
/*      */     
/* 1183 */     this.dirty_stubs = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canStubbify(DownloadImpl download)
/*      */   {
/* 1190 */     if (download.getState() != 7)
/*      */     {
/* 1192 */       return false;
/*      */     }
/*      */     
/* 1195 */     if (!download.isPersistent())
/*      */     {
/* 1197 */       return false;
/*      */     }
/*      */     
/* 1200 */     if (download.getTorrent() == null)
/*      */     {
/* 1202 */       return false;
/*      */     }
/*      */     
/* 1205 */     if ((download.getFlag(16L)) || (download.getFlag(512L)))
/*      */     {
/* 1207 */       return false;
/*      */     }
/*      */     
/* 1210 */     if (!download.isComplete(false))
/*      */     {
/* 1212 */       return false;
/*      */     }
/*      */     
/* 1215 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DownloadStub stubbify(DownloadImpl download)
/*      */     throws DownloadException, DownloadRemovalVetoException
/*      */   {
/* 1224 */     if (!canStubbify(download))
/*      */     {
/* 1226 */       throw new DownloadException("Download not in stubbifiable state");
/*      */     }
/*      */     
/* 1229 */     org.gudy.azureus2.core3.download.DownloadManager core_dm = PluginCoreUtils.unwrap(download);
/*      */     
/* 1231 */     Map<String, Object> gm_data = this.global_manager.exportDownloadStateToMap(core_dm);
/*      */     
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1237 */       gm_data = BDecoder.decode(BEncoder.encode(gm_data));
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1241 */       Debug.out(e);
/*      */     }
/*      */     
/*      */ 
/* 1245 */     String[] manual_tags = null;
/*      */     
/* 1247 */     if (this.tag_manager.isEnabled())
/*      */     {
/* 1249 */       List<Tag> tag_list = this.tag_manager.getTagType(3).getTagsForTaggable(core_dm);
/*      */       
/* 1251 */       if ((tag_list != null) && (tag_list.size() > 0))
/*      */       {
/*      */ 
/*      */ 
/* 1255 */         String restored_tag_name = MessageText.getString("label.restored");
/*      */         
/* 1257 */         tag_list = new ArrayList(tag_list);
/*      */         
/* 1259 */         Iterator<Tag> it = tag_list.iterator();
/*      */         
/* 1261 */         while (it.hasNext())
/*      */         {
/* 1263 */           Tag t = (Tag)it.next();
/*      */           
/* 1265 */           if (t.isTagAuto()[0] != 0)
/*      */           {
/* 1267 */             it.remove();
/*      */           }
/* 1269 */           else if (t.getTagName(true).equals(restored_tag_name))
/*      */           {
/* 1271 */             it.remove();
/*      */           }
/*      */         }
/*      */         
/* 1275 */         if (tag_list.size() > 0)
/*      */         {
/* 1277 */           manual_tags = new String[tag_list.size()];
/*      */           
/* 1279 */           for (int i = 0; i < manual_tags.length; i++)
/*      */           {
/* 1281 */             manual_tags[i] = ((Tag)tag_list.get(i)).getTagName(true);
/*      */           }
/*      */           
/* 1284 */           Arrays.sort(manual_tags);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1289 */     DownloadStubImpl stub = new DownloadStubImpl(this, download, manual_tags, gm_data);
/*      */     try
/*      */     {
/* 1292 */       informAdded(stub, true);
/*      */     }
/*      */     finally
/*      */     {
/* 1296 */       stub.setStubbified();
/*      */     }
/*      */     
/* 1299 */     boolean added = false;
/*      */     try
/*      */     {
/* 1302 */       core_dm.getDownloadState().exportState(ARCHIVE_DIR);
/*      */       
/* 1304 */       download.remove(false, false);
/*      */       
/* 1306 */       synchronized (this.download_stubs)
/*      */       {
/* 1308 */         this.download_stubs.add(stub);
/*      */         
/* 1310 */         this.download_stub_map.put(stub.getTorrentHash(), stub);
/*      */         
/* 1312 */         writeStubConfig();
/*      */       }
/*      */       
/* 1315 */       added = true;
/*      */       
/* 1317 */       informAdded(stub, false);
/*      */     }
/*      */     finally
/*      */     {
/* 1321 */       if (!added)
/*      */       {
/*      */ 
/*      */ 
/* 1325 */         informRemoved(stub, true);
/*      */       }
/*      */     }
/*      */     
/* 1329 */     return stub;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Download destubbify(DownloadStubImpl stub)
/*      */     throws DownloadException
/*      */   {
/* 1338 */     boolean removed = false;
/*      */     
/* 1340 */     informRemoved(stub, true);
/*      */     try
/*      */     {
/* 1343 */       byte[] torrent_hash = stub.getTorrentHash();
/*      */       try
/*      */       {
/* 1346 */         DownloadManagerStateFactory.importDownloadState(ARCHIVE_DIR, torrent_hash);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1350 */         throw new DownloadException("Failed to import download state", e);
/*      */       }
/*      */       
/* 1353 */       org.gudy.azureus2.core3.download.DownloadManager core_dm = this.global_manager.importDownloadStateFromMap(stub.getGMMap());
/*      */       
/* 1355 */       if (core_dm == null)
/*      */       {
/*      */         try {
/* 1358 */           DownloadManagerStateFactory.deleteDownloadState(torrent_hash);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1362 */           Debug.out(e);
/*      */         }
/*      */         
/* 1365 */         throw new DownloadException("Failed to add download");
/*      */       }
/*      */       
/*      */       try
/*      */       {
/* 1370 */         DownloadManagerStateFactory.deleteDownloadState(ARCHIVE_DIR, torrent_hash);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1374 */         Debug.out(e);
/*      */       }
/*      */       
/* 1377 */       synchronized (this.download_stubs)
/*      */       {
/* 1379 */         this.download_stubs.remove(stub);
/*      */         
/* 1381 */         this.download_stub_map.remove(stub.getTorrentHash());
/*      */         
/* 1383 */         writeStubConfig();
/*      */       }
/*      */       
/* 1386 */       String[] manual_tags = stub.getManualTags();
/*      */       Object tt;
/* 1388 */       if ((manual_tags != null) && (this.tag_manager.isEnabled()))
/*      */       {
/* 1390 */         tt = this.tag_manager.getTagType(3);
/*      */         
/* 1392 */         for (String name : manual_tags)
/*      */         {
/* 1394 */           Tag tag = ((TagType)tt).getTag(name, true);
/*      */           
/* 1396 */           if (tag == null) {
/*      */             try
/*      */             {
/* 1399 */               tag = ((TagType)tt).createTag(name, true);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1403 */               Debug.out(e);
/*      */             }
/*      */           }
/*      */           
/* 1407 */           if (tag != null)
/*      */           {
/* 1409 */             tag.addTaggable(core_dm);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1414 */       removed = true;
/*      */       
/* 1416 */       informRemoved(stub, false);
/*      */       
/* 1418 */       return PluginCoreUtils.wrap(core_dm);
/*      */     }
/*      */     finally
/*      */     {
/* 1422 */       if (!removed)
/*      */       {
/*      */ 
/*      */ 
/* 1426 */         informAdded(stub, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void remove(DownloadStubImpl stub)
/*      */   {
/* 1435 */     boolean removed = false;
/*      */     
/* 1437 */     informRemoved(stub, true);
/*      */     try
/*      */     {
/*      */       try {
/* 1441 */         DownloadManagerStateFactory.deleteDownloadState(ARCHIVE_DIR, stub.getTorrentHash());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1445 */         Debug.out(e);
/*      */       }
/*      */       
/* 1448 */       synchronized (this.download_stubs)
/*      */       {
/* 1450 */         this.download_stubs.remove(stub);
/*      */         
/* 1452 */         this.download_stub_map.remove(stub.getTorrentHash());
/*      */         
/* 1454 */         writeStubConfig();
/*      */       }
/*      */       
/* 1457 */       removed = true;
/*      */       
/* 1459 */       informRemoved(stub, false);
/*      */     }
/*      */     finally
/*      */     {
/* 1463 */       if (!removed)
/*      */       {
/* 1465 */         informAdded(stub, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static TOTorrent getStubTorrent(byte[] hash)
/*      */   {
/* 1474 */     File torrent_file = new File(ARCHIVE_DIR, ByteFormatter.encodeString(hash) + ".dat");
/*      */     
/* 1476 */     if (torrent_file.exists()) {
/*      */       try
/*      */       {
/* 1479 */         return TOTorrentFactory.deserialiseFromBEncodedFile(torrent_file);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1483 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1487 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected TOTorrent getTorrent(DownloadStubImpl stub)
/*      */   {
/* 1493 */     File torrent_file = new File(ARCHIVE_DIR, ByteFormatter.encodeString(stub.getTorrentHash()) + ".dat");
/*      */     
/* 1495 */     if (torrent_file.exists()) {
/*      */       try
/*      */       {
/* 1498 */         return TOTorrentFactory.deserialiseFromBEncodedFile(torrent_file);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1502 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1506 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updated(DownloadStubImpl stub)
/*      */   {
/* 1513 */     synchronized (this.download_stubs)
/*      */     {
/* 1515 */       this.dirty_stubs = true;
/*      */     }
/*      */     
/* 1518 */     this.dirty_stub_dispatcher.dispatch();
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
/* 1549 */   private Set<DownloadStub> informing_of_add = new HashSet();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void informAdded(DownloadStub stub, final boolean preparing)
/*      */   {
/* 1556 */     synchronized (this.informing_of_add)
/*      */     {
/* 1558 */       if (this.informing_of_add.contains(stub))
/*      */       {
/* 1560 */         Debug.out("Already informing of addition, ignoring");
/*      */         
/* 1562 */         return;
/*      */       }
/*      */       
/* 1565 */       this.informing_of_add.add(stub);
/*      */     }
/*      */     try
/*      */     {
/* 1569 */       list = new ArrayList();
/*      */       
/* 1571 */       list.add(stub);
/*      */       
/* 1573 */       for (DownloadStubListener l : this.download_stub_listeners) {
/*      */         try
/*      */         {
/* 1576 */           l.downloadStubEventOccurred(new DownloadStubEvent()
/*      */           {
/*      */ 
/*      */             public int getEventType()
/*      */             {
/*      */ 
/* 1582 */               return preparing ? 3 : 1;
/*      */             }
/*      */             
/*      */ 
/*      */             public List<DownloadStub> getDownloadStubs()
/*      */             {
/* 1588 */               return list;
/*      */             }
/*      */           });
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1594 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1599 */       synchronized (this.informing_of_add) {
/*      */         final List<DownloadStub> list;
/* 1601 */         this.informing_of_add.remove(stub);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void informRemoved(DownloadStub stub, final boolean preparing)
/*      */   {
/* 1611 */     final List<DownloadStub> list = new ArrayList();
/*      */     
/* 1613 */     list.add(stub);
/*      */     
/* 1615 */     for (DownloadStubListener l : this.download_stub_listeners) {
/*      */       try
/*      */       {
/* 1618 */         l.downloadStubEventOccurred(new DownloadStubEvent()
/*      */         {
/*      */ 
/*      */           public int getEventType()
/*      */           {
/*      */ 
/* 1624 */             return preparing ? 4 : 2;
/*      */           }
/*      */           
/*      */ 
/*      */           public List<DownloadStub> getDownloadStubs()
/*      */           {
/* 1630 */             return list;
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1636 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDownloadStubListener(DownloadStubListener l, boolean inform_of_current)
/*      */   {
/* 1646 */     this.download_stub_listeners.add(l);
/*      */     
/* 1648 */     if (inform_of_current)
/*      */     {
/*      */       final List<DownloadStub> existing;
/*      */       
/* 1652 */       synchronized (this.download_stubs)
/*      */       {
/* 1654 */         existing = new ArrayList(this.download_stubs);
/*      */       }
/*      */       try
/*      */       {
/* 1658 */         l.downloadStubEventOccurred(new DownloadStubEvent()
/*      */         {
/*      */ 
/*      */           public int getEventType()
/*      */           {
/*      */ 
/* 1664 */             return 1;
/*      */           }
/*      */           
/*      */ 
/*      */           public List<DownloadStub> getDownloadStubs()
/*      */           {
/* 1670 */             return existing;
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1676 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeDownloadStubListener(DownloadStubListener l)
/*      */   {
/* 1685 */     this.download_stub_listeners.remove(l);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public DownloadStub[] getDownloadStubs()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 795	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stubs	Ljava/util/List;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 795	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stubs	Ljava/util/List;
/*      */     //   11: aload_0
/*      */     //   12: getfield 795	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stubs	Ljava/util/List;
/*      */     //   15: invokeinterface 931 1 0
/*      */     //   20: anewarray 437	org/gudy/azureus2/plugins/download/DownloadStub
/*      */     //   23: invokeinterface 937 2 0
/*      */     //   28: checkcast 382	[Lorg/gudy/azureus2/plugins/download/DownloadStub;
/*      */     //   31: aload_1
/*      */     //   32: monitorexit
/*      */     //   33: areturn
/*      */     //   34: astore_2
/*      */     //   35: aload_1
/*      */     //   36: monitorexit
/*      */     //   37: aload_2
/*      */     //   38: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1524	-> byte code offset #0
/*      */     //   Java source line #1526	-> byte code offset #7
/*      */     //   Java source line #1527	-> byte code offset #34
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	39	0	this	DownloadManagerImpl
/*      */     //   5	31	1	Ljava/lang/Object;	Object
/*      */     //   34	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	33	34	finally
/*      */     //   34	37	34	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public int getDownloadStubCount()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 795	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stubs	Ljava/util/List;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 795	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stubs	Ljava/util/List;
/*      */     //   11: invokeinterface 931 1 0
/*      */     //   16: aload_1
/*      */     //   17: monitorexit
/*      */     //   18: ireturn
/*      */     //   19: astore_2
/*      */     //   20: aload_1
/*      */     //   21: monitorexit
/*      */     //   22: aload_2
/*      */     //   23: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1533	-> byte code offset #0
/*      */     //   Java source line #1535	-> byte code offset #7
/*      */     //   Java source line #1536	-> byte code offset #19
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	24	0	this	DownloadManagerImpl
/*      */     //   5	16	1	Ljava/lang/Object;	Object
/*      */     //   19	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	18	19	finally
/*      */     //   19	22	19	finally
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public DownloadStub lookupDownloadStub(byte[] hash)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 795	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stubs	Ljava/util/List;
/*      */     //   4: dup
/*      */     //   5: astore_2
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 804	org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl:download_stub_map	Lorg/gudy/azureus2/core3/util/ByteArrayHashMap;
/*      */     //   11: aload_1
/*      */     //   12: invokevirtual 855	org/gudy/azureus2/core3/util/ByteArrayHashMap:get	([B)Ljava/lang/Object;
/*      */     //   15: checkcast 437	org/gudy/azureus2/plugins/download/DownloadStub
/*      */     //   18: aload_2
/*      */     //   19: monitorexit
/*      */     //   20: areturn
/*      */     //   21: astore_3
/*      */     //   22: aload_2
/*      */     //   23: monitorexit
/*      */     //   24: aload_3
/*      */     //   25: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1543	-> byte code offset #0
/*      */     //   Java source line #1545	-> byte code offset #7
/*      */     //   Java source line #1546	-> byte code offset #21
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	26	0	this	DownloadManagerImpl
/*      */     //   0	26	1	hash	byte[]
/*      */     //   5	18	2	Ljava/lang/Object;	Object
/*      */     //   21	4	3	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	20	21	finally
/*      */     //   21	24	21	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/download/DownloadManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */