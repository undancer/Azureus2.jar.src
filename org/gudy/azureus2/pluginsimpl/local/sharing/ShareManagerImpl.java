/*      */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentProgressListener;
/*      */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareItem;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareManager;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareManagerListener;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDeletionVetoException;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDir;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceDirContents;
/*      */ import org.gudy.azureus2.plugins.sharing.ShareResourceFile;
/*      */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentException;
/*      */ import org.gudy.azureus2.plugins.torrent.TorrentManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*      */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentImpl;
/*      */ 
/*      */ public class ShareManagerImpl implements ShareManager, TOTorrentProgressListener, ParameterListener, AEDiagnosticsEvidenceGenerator
/*      */ {
/*   55 */   private static final LogIDs LOGID = LogIDs.PLUGIN;
/*      */   
/*      */   public static final String TORRENT_STORE = "shares";
/*      */   
/*      */   public static final String TORRENT_SUBSTORE = "cache";
/*      */   public static final int MAX_FILES_PER_DIR = 1000;
/*      */   public static final int MAX_DIRS = 1000;
/*      */   protected static ShareManagerImpl singleton;
/*   63 */   private static AEMonitor class_mon = new AEMonitor("ShareManager:class");
/*      */   private static boolean persistent_shares;
/*      */   
/*      */   static
/*      */   {
/*   68 */     COConfigurationManager.addAndFireParameterListener("Sharing Is Persistent", new ParameterListener()
/*      */     {
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*   74 */         ShareManagerImpl.access$002(COConfigurationManager.getBooleanParameter("Sharing Is Persistent"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*   79 */   protected AEMonitor this_mon = new AEMonitor("ShareManager");
/*      */   protected TOTorrentCreator to_creator;
/*      */   private volatile boolean initialised;
/*      */   private volatile boolean initialising;
/*      */   private File share_dir;
/*      */   private URL[] announce_urls;
/*      */   private ShareConfigImpl config;
/*      */   
/*      */   public static ShareManagerImpl getSingleton() throws ShareException {
/*      */     try {
/*   89 */       class_mon.enter();
/*      */       
/*   91 */       if (singleton == null)
/*      */       {
/*   93 */         singleton = new ShareManagerImpl();
/*      */       }
/*      */       
/*   96 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*  100 */       class_mon.exit();
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
/*  113 */   private Map<String, ShareResourceImpl> shares = new HashMap();
/*      */   
/*      */   private shareScanner current_scanner;
/*      */   
/*      */   private boolean scanning;
/*  118 */   private List<ShareManagerListener> listeners = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */   protected ShareManagerImpl()
/*      */     throws ShareException
/*      */   {
/*  125 */     COConfigurationManager.addListener(new COConfigurationListener()
/*      */     {
/*      */ 
/*      */       public void configurationSaved()
/*      */       {
/*      */ 
/*  131 */         ShareManagerImpl.this.announce_urls = null;
/*      */       }
/*      */       
/*  134 */     });
/*  135 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */   }
/*      */   
/*      */   public void initialise()
/*      */     throws ShareException
/*      */   {
/*      */     try
/*      */     {
/*  143 */       this.this_mon.enter();
/*      */       
/*  145 */       if (!this.initialised) {
/*      */         try
/*      */         {
/*  148 */           this.initialising = true;
/*      */           
/*  150 */           this.initialised = true;
/*      */           
/*  152 */           this.share_dir = FileUtil.getUserFile("shares");
/*      */           
/*  154 */           FileUtil.mkdirs(this.share_dir);
/*      */           
/*  156 */           this.config = new ShareConfigImpl();
/*      */           try
/*      */           {
/*  159 */             this.config.suspendSaving();
/*      */             
/*  161 */             this.config.loadConfig(this);
/*      */           } finally { Iterator<ShareResourceImpl> it;
/*      */             ShareResourceImpl resource;
/*      */             int i;
/*  165 */             Iterator<ShareResourceImpl> it = this.shares.values().iterator();
/*      */             
/*  167 */             while (it.hasNext())
/*      */             {
/*  169 */               ShareResourceImpl resource = (ShareResourceImpl)it.next();
/*      */               
/*  171 */               if (resource.getType() == 3)
/*      */               {
/*  173 */                 for (int i = 0; i < this.listeners.size(); i++)
/*      */                 {
/*      */                   try
/*      */                   {
/*  177 */                     ((ShareManagerListener)this.listeners.get(i)).resourceAdded(resource);
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/*  181 */                     Debug.printStackTrace(e);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*  187 */             this.config.resumeSaving();
/*      */           }
/*      */           
/*  190 */           readAZConfig();
/*      */         }
/*      */         finally
/*      */         {
/*  194 */           this.initialising = false;
/*      */           
/*  196 */           new AEThread2("ShareManager:initScan", true)
/*      */           {
/*      */             public void run()
/*      */             {
/*      */               try
/*      */               {
/*  202 */                 ShareManagerImpl.this.scanShares();
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  206 */                 Debug.printStackTrace(e);
/*      */               }
/*      */             }
/*      */           }.start();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  214 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isInitialising()
/*      */   {
/*  221 */     return this.initialising;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void readAZConfig()
/*      */   {
/*  227 */     COConfigurationManager.addParameterListener("Sharing Rescan Enable", this);
/*      */     
/*  229 */     readAZConfigSupport();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void parameterChanged(String name)
/*      */   {
/*  236 */     readAZConfigSupport();
/*      */   }
/*      */   
/*      */   protected void readAZConfigSupport()
/*      */   {
/*      */     try
/*      */     {
/*  243 */       this.this_mon.enter();
/*      */       
/*  245 */       boolean scan_enabled = COConfigurationManager.getBooleanParameter("Sharing Rescan Enable");
/*      */       
/*  247 */       if (!scan_enabled)
/*      */       {
/*  249 */         this.current_scanner = null;
/*      */       }
/*  251 */       else if (this.current_scanner == null)
/*      */       {
/*  253 */         this.current_scanner = new shareScanner();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  258 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected ShareConfigImpl getShareConfig()
/*      */   {
/*  265 */     return this.config;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkConsistency()
/*      */     throws ShareException
/*      */   {
/*  275 */     Iterator<ShareResourceImpl> it = new HashSet(this.shares.values()).iterator();
/*      */     
/*  277 */     while (it.hasNext())
/*      */     {
/*  279 */       ShareResourceImpl resource = (ShareResourceImpl)it.next();
/*      */       try
/*      */       {
/*  282 */         resource.checkConsistency();
/*      */       }
/*      */       catch (ShareException e)
/*      */       {
/*  286 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void deserialiseResource(Map map)
/*      */   {
/*      */     try
/*      */     {
/*  296 */       ShareResourceImpl new_resource = null;
/*      */       
/*  298 */       int type = ((Long)map.get("type")).intValue();
/*      */       
/*  300 */       if ((type == 1) || (type == 2))
/*      */       {
/*      */ 
/*  303 */         new_resource = ShareResourceFileOrDirImpl.deserialiseResource(this, map, type);
/*      */       }
/*      */       else
/*      */       {
/*  307 */         new_resource = ShareResourceDirContentsImpl.deserialiseResource(this, map);
/*      */       }
/*      */       
/*  310 */       if (new_resource != null)
/*      */       {
/*  312 */         ShareResourceImpl old_resource = (ShareResourceImpl)this.shares.get(new_resource.getName());
/*      */         
/*  314 */         if (old_resource != null)
/*      */         {
/*  316 */           old_resource.delete(true);
/*      */         }
/*      */         
/*  319 */         this.shares.put(new_resource.getName(), new_resource);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  324 */         if (type != 3)
/*      */         {
/*  326 */           for (int i = 0; i < this.listeners.size(); i++)
/*      */           {
/*      */             try
/*      */             {
/*  330 */               ((ShareManagerListener)this.listeners.get(i)).resourceAdded(new_resource);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  334 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  341 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getNewTorrentLocation()
/*      */     throws ShareException
/*      */   {
/*  350 */     for (int i = 1; i <= 1000; i++)
/*      */     {
/*  352 */       String cache_dir_str = this.share_dir + File.separator + "cache" + i;
/*      */       
/*  354 */       File cache_dir = new File(cache_dir_str);
/*      */       
/*  356 */       if (!cache_dir.exists())
/*      */       {
/*  358 */         FileUtil.mkdirs(cache_dir);
/*      */       }
/*      */       
/*  361 */       if (cache_dir.listFiles().length < 1000)
/*      */       {
/*  363 */         for (int j = 0; j < 1000; j++)
/*      */         {
/*  365 */           long file = RandomUtils.nextAbsoluteLong();
/*      */           
/*  367 */           File file_name = new File(cache_dir_str + File.separator + file + ".torrent");
/*      */           
/*  369 */           if (!file_name.exists())
/*      */           {
/*      */ 
/*      */ 
/*  373 */             return "cache" + i + File.separator + file + ".torrent";
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  379 */     throw new ShareException("ShareManager: Failed to allocate cache file");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void writeTorrent(ShareItemImpl item)
/*      */     throws ShareException
/*      */   {
/*      */     try
/*      */     {
/*  389 */       item.getTorrent().writeToFile(getTorrentFile(item));
/*      */     }
/*      */     catch (TorrentException e)
/*      */     {
/*  393 */       throw new ShareException("ShareManager: Torrent write fails", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void readTorrent(ShareItemImpl item)
/*      */     throws ShareException
/*      */   {
/*      */     try
/*      */     {
/*  404 */       TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedFile(getTorrentFile(item));
/*      */       
/*  406 */       item.setTorrent(new TorrentImpl(torrent));
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*  410 */       throw new ShareException("ShareManager: Torrent read fails", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void deleteTorrent(ShareItemImpl item)
/*      */   {
/*  418 */     File torrent_file = getTorrentFile(item);
/*      */     
/*  420 */     torrent_file.delete();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean torrentExists(ShareItemImpl item)
/*      */   {
/*  427 */     return getTorrentFile(item).exists();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected File getTorrentFile(ShareItemImpl item)
/*      */   {
/*  434 */     return new File(this.share_dir + File.separator + item.getTorrentLocation());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected URL[] getAnnounceURLs()
/*      */     throws ShareException
/*      */   {
/*  442 */     if (this.announce_urls == null)
/*      */     {
/*  444 */       String protocol = COConfigurationManager.getStringParameter("Sharing Protocol");
/*      */       
/*  446 */       if (protocol.equalsIgnoreCase("DHT"))
/*      */       {
/*  448 */         this.announce_urls = new URL[] { TorrentUtils.getDecentralisedEmptyURL() };
/*      */       }
/*      */       else
/*      */       {
/*  452 */         URL[][] tracker_url_sets = TRTrackerUtils.getAnnounceURLs();
/*      */         
/*  454 */         if (tracker_url_sets.length == 0)
/*      */         {
/*  456 */           throw new ShareException("ShareManager: Tracker must be configured");
/*      */         }
/*      */         
/*  459 */         for (int i = 0; i < tracker_url_sets.length; i++)
/*      */         {
/*  461 */           URL[] tracker_urls = tracker_url_sets[i];
/*      */           
/*  463 */           if (tracker_urls[0].getProtocol().equalsIgnoreCase(protocol))
/*      */           {
/*  465 */             this.announce_urls = tracker_urls;
/*      */             
/*  467 */             break;
/*      */           }
/*      */         }
/*      */         
/*  471 */         if (this.announce_urls == null)
/*      */         {
/*  473 */           throw new ShareException("ShareManager: Tracker must be configured for protocol '" + protocol + "'");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  478 */     return this.announce_urls;
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean getAddHashes()
/*      */   {
/*  484 */     return COConfigurationManager.getBooleanParameter("Sharing Add Hashes");
/*      */   }
/*      */   
/*      */ 
/*      */   public ShareResource[] getShares()
/*      */   {
/*  490 */     ShareResource[] res = new ShareResource[this.shares.size()];
/*      */     
/*  492 */     this.shares.values().toArray(res);
/*      */     
/*  494 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected ShareResourceImpl getResource(File file)
/*      */     throws ShareException
/*      */   {
/*      */     try
/*      */     {
/*  504 */       return (ShareResourceImpl)this.shares.get(file.getCanonicalFile().toString());
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*  508 */       throw new ShareException("getCanonicalFile fails", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public ShareResource getShare(File file_or_dir)
/*      */   {
/*      */     try
/*      */     {
/*  517 */       return getResource(file_or_dir);
/*      */     }
/*      */     catch (ShareException e) {}
/*      */     
/*  521 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean getBooleanProperty(Map<String, String> properties, String name)
/*      */   {
/*  530 */     if (properties == null)
/*      */     {
/*  532 */       return false;
/*      */     }
/*      */     
/*  535 */     String value = (String)properties.get(name);
/*      */     
/*  537 */     if (value == null)
/*      */     {
/*  539 */       return false;
/*      */     }
/*      */     
/*  542 */     return value.equalsIgnoreCase("true");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceFile addFile(File file)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  551 */     return addFile(file, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceFile addFile(File file, Map<String, String> properties)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  561 */     return addFile(null, file, getBooleanProperty(properties, "personal"), properties);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ShareResourceFile addFile(ShareResourceDirContentsImpl parent, File file, boolean personal, Map<String, String> properties)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  573 */     if (Logger.isEnabled()) {
/*  574 */       Logger.log(new LogEvent(LOGID, "ShareManager: addFile '" + file.toString() + "'"));
/*      */     }
/*      */     try
/*      */     {
/*  578 */       return (ShareResourceFile)addFileOrDir(parent, file, 1, personal, properties);
/*      */     }
/*      */     catch (ShareException e)
/*      */     {
/*  582 */       reportError(e);
/*      */       
/*  584 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceFile getFile(File file)
/*      */     throws ShareException
/*      */   {
/*  594 */     return ShareResourceFileImpl.getResource(this, file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceDir addDir(File dir)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  603 */     return addDir(dir, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceDir addDir(File dir, Map<String, String> properties)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  613 */     return addDir(null, dir, getBooleanProperty(properties, "personal"), properties);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceDir addDir(ShareResourceDirContentsImpl parent, File dir, boolean personal, Map<String, String> properties)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  625 */     if (Logger.isEnabled()) {
/*  626 */       Logger.log(new LogEvent(LOGID, "ShareManager: addDir '" + dir.toString() + "'"));
/*      */     }
/*      */     try
/*      */     {
/*  630 */       this.this_mon.enter();
/*      */       
/*  632 */       return (ShareResourceDir)addFileOrDir(parent, dir, 2, personal, properties);
/*      */     }
/*      */     catch (ShareException e)
/*      */     {
/*  636 */       reportError(e);
/*      */       
/*  638 */       throw e;
/*      */     }
/*      */     finally
/*      */     {
/*  642 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceDir getDir(File file)
/*      */     throws ShareException
/*      */   {
/*  652 */     return ShareResourceDirImpl.getResource(this, file);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ShareResource addFileOrDir(ShareResourceDirContentsImpl parent, File file, int type, boolean personal, Map<String, String> properties)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  665 */     properties = setPropertyDefaults(properties);
/*      */     try
/*      */     {
/*  668 */       this.this_mon.enter();
/*      */       
/*  670 */       String name = file.getCanonicalFile().toString();
/*      */       
/*  672 */       ShareResourceImpl old_resource = (ShareResourceImpl)this.shares.get(name);
/*      */       
/*  674 */       boolean modified = old_resource != null;
/*      */       
/*  676 */       if (modified)
/*      */       {
/*  678 */         if (old_resource.isPersistent())
/*      */         {
/*  680 */           return old_resource;
/*      */         }
/*      */         
/*  683 */         old_resource.delete(true, false);
/*      */       }
/*      */       
/*      */       Object new_resource;
/*      */       Object new_resource;
/*  688 */       if (type == 1)
/*      */       {
/*  690 */         reportCurrentTask("Adding file '" + name + "'");
/*      */         
/*  692 */         new_resource = new ShareResourceFileImpl(this, parent, file, personal, properties);
/*      */       }
/*      */       else
/*      */       {
/*  696 */         reportCurrentTask("Adding dir '" + name + "'");
/*      */         
/*  698 */         new_resource = new ShareResourceDirImpl(this, parent, file, personal, properties);
/*      */       }
/*      */       
/*  701 */       this.shares.put(name, new_resource);
/*      */       
/*  703 */       this.config.saveConfig();
/*      */       
/*  705 */       for (int i = 0; i < this.listeners.size(); i++)
/*      */       {
/*      */         try
/*      */         {
/*  709 */           if (modified)
/*      */           {
/*  711 */             ((ShareManagerListener)this.listeners.get(i)).resourceModified(old_resource, (ShareResource)new_resource);
/*      */           }
/*      */           else
/*      */           {
/*  715 */             ((ShareManagerListener)this.listeners.get(i)).resourceAdded((ShareResource)new_resource);
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {
/*  719 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  723 */       return (int)new_resource;
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*  727 */       throw new ShareException("getCanoncialFile fails", e);
/*      */     }
/*      */     finally
/*      */     {
/*  731 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceDirContents addDirContents(File dir, boolean recursive)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  742 */     return addDirContents(dir, recursive, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ShareResourceDirContents addDirContents(File dir, boolean recursive, Map<String, String> properties)
/*      */     throws ShareException, ShareResourceDeletionVetoException
/*      */   {
/*  753 */     if (Logger.isEnabled()) {
/*  754 */       Logger.log(new LogEvent(LOGID, "ShareManager: addDirContents '" + dir.toString() + "'"));
/*      */     }
/*      */     
/*  757 */     properties = setPropertyDefaults(properties);
/*      */     try
/*      */     {
/*  760 */       this.this_mon.enter();
/*      */       
/*  762 */       String name = dir.getCanonicalFile().toString();
/*      */       
/*  764 */       reportCurrentTask("Adding dir contents '" + name + "', recursive = " + recursive);
/*      */       
/*  766 */       ShareResource old_resource = (ShareResource)this.shares.get(name);
/*      */       
/*  768 */       if (old_resource != null)
/*      */       {
/*  770 */         if ((old_resource.isPersistent()) && ((old_resource instanceof ShareResourceDirContents)))
/*      */         {
/*  772 */           return (ShareResourceDirContents)old_resource;
/*      */         }
/*      */         
/*  775 */         old_resource.delete(true);
/*      */       }
/*      */       
/*  778 */       ShareResourceDirContentsImpl new_resource = new ShareResourceDirContentsImpl(this, dir, recursive, getBooleanProperty(properties, "personal"), properties, true);
/*      */       
/*  780 */       this.shares.put(name, new_resource);
/*      */       
/*  782 */       this.config.saveConfig();
/*      */       
/*  784 */       for (int i = 0; i < this.listeners.size(); i++)
/*      */       {
/*      */         try
/*      */         {
/*  788 */           ((ShareManagerListener)this.listeners.get(i)).resourceAdded(new_resource);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  792 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */       
/*  796 */       return new_resource;
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*  800 */       reportError(e);
/*      */       
/*  802 */       throw new ShareException("getCanoncialFile fails", e);
/*      */     }
/*      */     catch (ShareException e)
/*      */     {
/*  806 */       reportError(e);
/*      */       
/*  808 */       throw e;
/*      */     }
/*      */     finally
/*      */     {
/*  812 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void delete(ShareResourceImpl resource, boolean fire_listeners)
/*      */     throws ShareException
/*      */   {
/*  823 */     if (Logger.isEnabled()) {
/*  824 */       Logger.log(new LogEvent(LOGID, "ShareManager: resource '" + resource.getName() + "' deleted"));
/*      */     }
/*      */     try
/*      */     {
/*  828 */       this.this_mon.enter();
/*      */       
/*  830 */       this.shares.remove(resource.getName());
/*      */       
/*  832 */       resource.deleteInternal();
/*      */       
/*  834 */       this.config.saveConfig();
/*      */       
/*  836 */       if (fire_listeners)
/*      */       {
/*  838 */         for (int i = 0; i < this.listeners.size(); i++)
/*      */         {
/*      */           try
/*      */           {
/*  842 */             ((ShareManagerListener)this.listeners.get(i)).resourceDeleted(resource);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  846 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  852 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void scanShares()
/*      */     throws ShareException
/*      */   {
/*      */     try
/*      */     {
/*  862 */       this.this_mon.enter();
/*      */       
/*  864 */       if (this.scanning) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  869 */       this.scanning = true;
/*      */     }
/*      */     finally
/*      */     {
/*  873 */       this.this_mon.exit();
/*      */     }
/*      */     try
/*      */     {
/*  877 */       if (Logger.isEnabled()) {
/*  878 */         Logger.log(new LogEvent(LOGID, "ShareManager: scanning resources for changes"));
/*      */       }
/*      */       
/*  881 */       checkConsistency();
/*      */     }
/*      */     finally
/*      */     {
/*      */       try {
/*  886 */         this.this_mon.enter();
/*      */         
/*  888 */         this.scanning = false;
/*      */       }
/*      */       finally
/*      */       {
/*  892 */         this.this_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setTorrentCreator(TOTorrentCreator _to_creator)
/*      */   {
/*  904 */     this.to_creator = _to_creator;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private Map<String, String> setPropertyDefaults(Map<String, String> properties)
/*      */   {
/*  911 */     if (persistent_shares)
/*      */     {
/*  913 */       if (properties == null)
/*      */       {
/*  915 */         properties = new HashMap();
/*      */       }
/*      */       
/*  918 */       if (!properties.containsKey("persistent"))
/*      */       {
/*  920 */         properties.put("persistent", persistent_shares ? "true" : "false");
/*      */       }
/*      */     }
/*      */     
/*  924 */     return properties;
/*      */   }
/*      */   
/*      */ 
/*      */   public void cancelOperation()
/*      */   {
/*  930 */     TOTorrentCreator temp = this.to_creator;
/*      */     
/*  932 */     if (temp != null)
/*      */     {
/*  934 */       temp.cancel();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void reportProgress(int percent_complete)
/*      */   {
/*  942 */     for (int i = 0; i < this.listeners.size(); i++)
/*      */     {
/*      */       try
/*      */       {
/*  946 */         ((ShareManagerListener)this.listeners.get(i)).reportProgress(percent_complete);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  950 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void reportCurrentTask(String task_description)
/*      */   {
/*  959 */     for (int i = 0; i < this.listeners.size(); i++)
/*      */     {
/*      */       try
/*      */       {
/*  963 */         ((ShareManagerListener)this.listeners.get(i)).reportCurrentTask(task_description);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  967 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void reportError(Throwable e)
/*      */   {
/*  976 */     String message = e.getMessage();
/*      */     
/*  978 */     if (message != null)
/*      */     {
/*  980 */       reportCurrentTask(Debug.getNestedExceptionMessage(e));
/*      */     }
/*      */     else
/*      */     {
/*  984 */       reportCurrentTask(e.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(ShareManagerListener l)
/*      */   {
/*  992 */     this.listeners.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(ShareManagerListener l)
/*      */   {
/*  999 */     this.listeners.remove(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1006 */     writer.println("Shares");
/*      */     try
/*      */     {
/* 1009 */       writer.indent();
/*      */       
/* 1011 */       ShareResource[] shares = getShares();
/*      */       
/* 1013 */       HashSet share_map = new HashSet();
/*      */       
/* 1015 */       for (int i = 0; i < shares.length; i++)
/*      */       {
/* 1017 */         ShareResource share = shares[i];
/*      */         
/* 1019 */         if ((share instanceof ShareResourceDirContents))
/*      */         {
/* 1021 */           share_map.add(share);
/*      */         }
/* 1023 */         else if (share.getParent() == null)
/*      */         {
/*      */ 
/*      */ 
/* 1027 */           writer.println(getDebugName(share));
/*      */         }
/*      */       }
/*      */       
/* 1031 */       Iterator it = share_map.iterator();
/*      */       
/*      */ 
/*      */ 
/* 1035 */       if (!AzureusCoreFactory.isCoreAvailable())
/*      */       {
/*      */ 
/* 1038 */         writer.println("No Core");
/*      */       }
/*      */       else
/*      */       {
/* 1042 */         TorrentManager tm = PluginInitializer.getDefaultInterface().getTorrentManager();
/*      */         
/* 1044 */         TorrentAttribute category_attribute = tm.getAttribute("Category");
/* 1045 */         TorrentAttribute props_attribute = tm.getAttribute("ShareProperties");
/*      */         
/* 1047 */         while (it.hasNext())
/*      */         {
/* 1049 */           ShareResourceDirContents root = (ShareResourceDirContents)it.next();
/*      */           
/* 1051 */           String cat = root.getAttribute(category_attribute);
/* 1052 */           String props = root.getAttribute(props_attribute);
/*      */           
/* 1054 */           String extra = ",cat=" + cat;
/*      */           
/* 1056 */           extra = extra + (props == null ? "" : new StringBuilder().append(",props=").append(props).toString());
/*      */           
/* 1058 */           extra = extra + ",rec=" + root.isRecursive();
/*      */           
/* 1060 */           writer.println(root.getName() + extra);
/*      */           
/* 1062 */           generate(writer, root);
/*      */         }
/*      */       }
/*      */     } finally {
/* 1066 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void generate(IndentWriter writer, ShareResourceDirContents node)
/*      */   {
/*      */     try
/*      */     {
/* 1076 */       writer.indent();
/*      */       
/* 1078 */       ShareResource[] kids = node.getChildren();
/*      */       
/* 1080 */       for (int i = 0; i < kids.length; i++)
/*      */       {
/* 1082 */         ShareResource kid = kids[i];
/*      */         
/* 1084 */         writer.println(getDebugName(kid));
/*      */         
/* 1086 */         if ((kid instanceof ShareResourceDirContents))
/*      */         {
/* 1088 */           generate(writer, (ShareResourceDirContents)kid);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1093 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getDebugName(ShareResource _share)
/*      */   {
/* 1101 */     Torrent torrent = null;
/*      */     try
/*      */     {
/* 1104 */       if ((_share instanceof ShareResourceFile))
/*      */       {
/* 1106 */         ShareResourceFile share = (ShareResourceFile)_share;
/*      */         
/* 1108 */         torrent = share.getItem().getTorrent();
/*      */       }
/* 1110 */       else if ((_share instanceof ShareResourceDir))
/*      */       {
/* 1112 */         ShareResourceDir share = (ShareResourceDir)_share;
/*      */         
/* 1114 */         torrent = share.getItem().getTorrent();
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 1119 */     if (torrent == null)
/*      */     {
/* 1121 */       return Debug.secretFileName(_share.getName());
/*      */     }
/*      */     
/*      */ 
/* 1125 */     return Debug.secretFileName(torrent.getName()) + "/" + ByteFormatter.encodeString(torrent.getHash());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected class shareScanner
/*      */   {
/*      */     protected shareScanner()
/*      */     {
/* 1136 */       ShareManagerImpl.this.current_scanner = this;
/*      */       
/* 1138 */       new AEThread2("ShareManager::scanner", true)
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/* 1143 */           while (ShareManagerImpl.this.current_scanner == ShareManagerImpl.shareScanner.this)
/*      */           {
/*      */             try
/*      */             {
/* 1147 */               int scan_period = COConfigurationManager.getIntParameter("Sharing Rescan Period");
/*      */               
/* 1149 */               if (scan_period < 1)
/*      */               {
/* 1151 */                 scan_period = 1;
/*      */               }
/*      */               
/* 1154 */               Thread.sleep(scan_period * 1000);
/*      */               
/* 1156 */               if (ShareManagerImpl.this.current_scanner == ShareManagerImpl.shareScanner.this)
/*      */               {
/* 1158 */                 ShareManagerImpl.this.scanShares();
/*      */               }
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1163 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }.start();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */