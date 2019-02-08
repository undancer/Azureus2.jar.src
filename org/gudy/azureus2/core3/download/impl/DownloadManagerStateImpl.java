/*      */ package org.gudy.azureus2.core3.download.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteMap;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap.Entry;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import org.gudy.azureus2.core3.category.Category;
/*      */ import org.gudy.azureus2.core3.category.CategoryManager;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFactory;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerException;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateEvent;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateListener;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerSource;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentFile;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentListener;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.StringInterner;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils.ExtendedTorrent;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils.torrentDelegate;
/*      */ 
/*      */ public class DownloadManagerStateImpl implements DownloadManagerState, ParameterListener
/*      */ {
/*      */   private static final int VER_INCOMING_PEER_SOURCE = 1;
/*      */   private static final int VER_CURRENT = 1;
/*   66 */   private static final LogIDs LOGID = LogIDs.DISK;
/*      */   
/*      */   private static final String RESUME_KEY = "resume";
/*      */   
/*      */   private static final String TRACKER_CACHE_KEY = "tracker_cache";
/*      */   private static final String ATTRIBUTE_KEY = "attributes";
/*      */   private static final String AZUREUS_PROPERTIES_KEY = "azureus_properties";
/*      */   private static final File ACTIVE_DIR;
/*   74 */   public static boolean SUPPRESS_FIXUP_ERRORS = false;
/*      */   private static final Random random;
/*      */   private static final Map default_parameters;
/*      */   private static final Map default_attributes; private static final AEMonitor class_mon;
/*   78 */   static { ACTIVE_DIR = FileUtil.getUserFile("active");
/*      */     
/*   80 */     if (!ACTIVE_DIR.exists())
/*      */     {
/*   82 */       FileUtil.mkdirs(ACTIVE_DIR);
/*      */     }
/*      */     
/*      */ 
/*   86 */     random = RandomUtils.SECURE_RANDOM;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   92 */     default_parameters = new HashMap();
/*      */     
/*   94 */     for (int i = 0; i < PARAMETERS.length; i++)
/*      */     {
/*   96 */       default_parameters.put(PARAMETERS[i][0], PARAMETERS[i][1]);
/*      */     }
/*      */     
/*   99 */     default_attributes = new HashMap();
/*      */     
/*  101 */     for (int i = 0; i < ATTRIBUTE_DEFAULTS.length; i++)
/*      */     {
/*  103 */       default_attributes.put(ATTRIBUTE_DEFAULTS[i][0], ATTRIBUTE_DEFAULTS[i][1]);
/*      */     }
/*      */     
/*      */ 
/*  107 */     TorrentUtils.registerMapFluff(new String[] { "tracker_cache", "resume" });
/*      */     
/*      */ 
/*  110 */     class_mon = new AEMonitor("DownloadManagerState:class");
/*      */     
/*  112 */     state_map = new HashMap();
/*      */     
/*      */ 
/*  115 */     ParameterListener listener = new ParameterListener()
/*      */     {
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */         List<DownloadManagerStateImpl> states;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  124 */         synchronized (DownloadManagerStateImpl.state_map)
/*      */         {
/*  126 */           states = new ArrayList(DownloadManagerStateImpl.state_map.values());
/*      */         }
/*      */         
/*  129 */         for (DownloadManagerStateImpl state : states) {
/*      */           try
/*      */           {
/*  132 */             state.parameterChanged(parameterName);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  136 */             Debug.out(e);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*  141 */     };
/*  142 */     COConfigurationManager.addParameterListener("Max.Peer.Connections.Per.Torrent.When.Seeding", listener);
/*  143 */     COConfigurationManager.addParameterListener("Max.Peer.Connections.Per.Torrent.When.Seeding.Enable", listener);
/*  144 */     COConfigurationManager.addParameterListener("Max.Peer.Connections.Per.Torrent", listener);
/*  145 */     COConfigurationManager.addParameterListener("Max Uploads", listener);
/*  146 */     COConfigurationManager.addParameterListener("Max Uploads Seeding", listener);
/*  147 */     COConfigurationManager.addParameterListener("Max Seeds Per Torrent", listener);
/*  148 */     COConfigurationManager.addParameterListener("enable.seedingonly.maxuploads", listener);
/*      */   }
/*      */   
/*      */   static final Map<HashWrapper, DownloadManagerStateImpl> state_map;
/*  152 */   private static final Map global_state_cache = new HashMap();
/*  153 */   private static final ArrayList global_state_cache_wrappers = new ArrayList();
/*      */   
/*  155 */   private static final CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> global_listeners_read_map_cow = new CopyOnWriteMap();
/*  156 */   private static final CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> global_listeners_write_map_cow = new CopyOnWriteMap();
/*      */   
/*      */ 
/*      */   private DownloadManagerImpl download_manager;
/*      */   
/*      */   private final TorrentUtils.ExtendedTorrent torrent;
/*      */   
/*      */   private boolean write_required;
/*      */   
/*      */   private Category category;
/*      */   
/*  167 */   private final CopyOnWriteList<DownloadManagerStateListener> listeners_cow = new CopyOnWriteList();
/*      */   
/*  169 */   private final CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> listeners_read_map_cow = new CopyOnWriteMap();
/*  170 */   private final CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> listeners_write_map_cow = new CopyOnWriteMap();
/*      */   
/*      */   private Map parameters;
/*      */   
/*      */   private Map attributes;
/*      */   
/*  176 */   private final AEMonitor this_mon = new AEMonitor("DownloadManagerState");
/*      */   
/*  178 */   private int supressWrites = 0;
/*      */   
/*  180 */   private static final ThreadLocal tls_wbr = new ThreadLocal()
/*      */   {
/*      */ 
/*      */     public Object initialValue()
/*      */     {
/*      */ 
/*  186 */       return new ArrayList(1);
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static DownloadManagerState getDownloadState(DownloadManagerImpl download_manager, TOTorrent original_torrent, TorrentUtils.ExtendedTorrent target_torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  198 */     byte[] hash = target_torrent.getHash();
/*      */     
/*  200 */     DownloadManagerStateImpl res = null;
/*      */     try
/*      */     {
/*  203 */       class_mon.enter();
/*      */       
/*  205 */       HashWrapper hash_wrapper = new HashWrapper(hash);
/*      */       
/*  207 */       res = (DownloadManagerStateImpl)state_map.get(hash_wrapper);
/*      */       
/*  209 */       if (res == null)
/*      */       {
/*  211 */         res = new DownloadManagerStateImpl(download_manager, target_torrent);
/*      */         
/*  213 */         state_map.put(hash_wrapper, res);
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  220 */         if ((res.getDownloadManager() == null) && (download_manager != null))
/*      */         {
/*  222 */           res.setDownloadManager(download_manager);
/*      */         }
/*      */         
/*  225 */         if (original_torrent != null)
/*      */         {
/*  227 */           res.mergeTorrentDetails(original_torrent);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/*  232 */       class_mon.exit();
/*      */     }
/*      */     
/*  235 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DownloadManagerState getDownloadState(TOTorrent original_torrent)
/*      */     throws TOTorrentException
/*      */   {
/*  245 */     byte[] torrent_hash = original_torrent.getHash();
/*      */     
/*      */ 
/*      */ 
/*  249 */     TorrentUtils.ExtendedTorrent saved_state = null;
/*      */     
/*  251 */     File saved_file = getStateFile(torrent_hash);
/*      */     
/*  253 */     if (saved_file.exists()) {
/*      */       try
/*      */       {
/*  256 */         saved_state = TorrentUtils.readDelegateFromFile(saved_file, false);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  260 */         Debug.out("Failed to load download state for " + saved_file, e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  266 */     if (saved_state == null)
/*      */     {
/*  268 */       TorrentUtils.copyToFile(original_torrent, saved_file);
/*      */       
/*  270 */       saved_state = TorrentUtils.readDelegateFromFile(saved_file, false);
/*      */     }
/*      */     
/*  273 */     return getDownloadState(null, original_torrent, saved_state);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DownloadManagerState getDownloadState(DownloadManagerImpl download_manager, String torrent_file, byte[] torrent_hash, boolean inactive)
/*      */     throws TOTorrentException
/*      */   {
/*  285 */     boolean discard_pieces = state_map.size() > 32;
/*      */     
/*      */ 
/*      */ 
/*  289 */     TOTorrent original_torrent = null;
/*  290 */     TorrentUtils.ExtendedTorrent saved_state = null;
/*      */     
/*      */ 
/*      */ 
/*  294 */     if (torrent_hash != null)
/*      */     {
/*  296 */       File saved_file = getStateFile(torrent_hash);
/*      */       
/*  298 */       if (saved_file.exists()) {
/*      */         try
/*      */         {
/*  301 */           Map cached_state = (Map)global_state_cache.remove(new HashWrapper(torrent_hash));
/*      */           
/*  303 */           if (cached_state != null)
/*      */           {
/*  305 */             CachedStateWrapper wrapper = new CachedStateWrapper(download_manager, torrent_file, torrent_hash, cached_state, inactive);
/*      */             
/*  307 */             global_state_cache_wrappers.add(wrapper);
/*      */             
/*  309 */             saved_state = wrapper;
/*      */           }
/*      */           else
/*      */           {
/*  313 */             saved_state = TorrentUtils.readDelegateFromFile(saved_file, discard_pieces);
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  318 */           Debug.out("Failed to load download state for " + saved_file);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  325 */     if (saved_state == null)
/*      */     {
/*  327 */       original_torrent = TorrentUtils.readDelegateFromFile(new File(torrent_file), discard_pieces);
/*      */       
/*  329 */       torrent_hash = original_torrent.getHash();
/*      */       
/*  331 */       File saved_file = getStateFile(torrent_hash);
/*      */       
/*  333 */       if (saved_file.exists()) {
/*      */         try
/*      */         {
/*  336 */           saved_state = TorrentUtils.readDelegateFromFile(saved_file, discard_pieces);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  340 */           Debug.out("Failed to load download state for " + saved_file);
/*      */         }
/*      */       }
/*      */       
/*  344 */       if (saved_state == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  351 */         TorrentUtils.copyToFile(original_torrent, saved_file);
/*      */         
/*  353 */         saved_state = TorrentUtils.readDelegateFromFile(saved_file, discard_pieces);
/*      */       }
/*      */     }
/*      */     
/*  357 */     DownloadManagerState res = getDownloadState(download_manager, original_torrent, saved_state);
/*      */     
/*  359 */     if (inactive)
/*      */     {
/*  361 */       res.setActive(false);
/*      */     }
/*      */     
/*  364 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static File getStateFile(byte[] torrent_hash)
/*      */   {
/*  371 */     return new File(ACTIVE_DIR, ByteFormatter.encodeString(torrent_hash) + ".dat");
/*      */   }
/*      */   
/*      */ 
/*      */   protected static File getGlobalStateFile()
/*      */   {
/*  377 */     return new File(ACTIVE_DIR, "cache.dat");
/*      */   }
/*      */   
/*      */ 
/*      */   public static void loadGlobalStateCache()
/*      */   {
/*  383 */     File file = getGlobalStateFile();
/*      */     
/*  385 */     if (!file.canRead())
/*      */     {
/*  387 */       return;
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  392 */       BufferedInputStream is = new BufferedInputStream(new java.util.zip.GZIPInputStream(new java.io.FileInputStream(file)));
/*      */       
/*      */       try
/*      */       {
/*  396 */         Map map = org.gudy.azureus2.core3.util.BDecoder.decode(is);
/*      */         
/*  398 */         List cache = (List)map.get("state");
/*      */         
/*  400 */         if (cache != null)
/*      */         {
/*  402 */           for (int i = 0; i < cache.size(); i++)
/*      */           {
/*  404 */             Map entry = (Map)cache.get(i);
/*      */             
/*  406 */             byte[] hash = (byte[])entry.get("hash");
/*      */             
/*  408 */             if (hash != null)
/*      */             {
/*  410 */               global_state_cache.put(new HashWrapper(hash), entry);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  415 */         is.close();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  419 */         Debug.printStackTrace(e);
/*      */       }
/*      */       finally {
/*      */         try {
/*  423 */           is.close();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       return;
/*      */     }
/*      */     catch (Throwable e) {
/*  430 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void saveGlobalStateCache()
/*      */   {
/*      */     try
/*      */     {
/*  438 */       class_mon.enter();
/*      */       
/*  440 */       Map map = new HashMap();
/*      */       
/*  442 */       List cache = new ArrayList();
/*      */       
/*  444 */       map.put("state", cache);
/*      */       
/*  446 */       Iterator it = state_map.values().iterator();
/*      */       
/*  448 */       while (it.hasNext())
/*      */       {
/*  450 */         DownloadManagerState dms = (DownloadManagerState)it.next();
/*      */         
/*  452 */         DownloadManager dm = dms.getDownloadManager();
/*      */         
/*  454 */         if ((dm != null) && (dm.isPersistent())) {
/*      */           try
/*      */           {
/*  457 */             Map state = CachedStateWrapper.export(dms);
/*      */             
/*  459 */             cache.add(state);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  463 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  468 */       GZIPOutputStream os = new GZIPOutputStream(new java.io.FileOutputStream(getGlobalStateFile()));
/*      */       
/*      */       try
/*      */       {
/*  472 */         os.write(BEncoder.encode(map));
/*      */         
/*  474 */         os.close();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  478 */         Debug.printStackTrace(e);
/*      */         try
/*      */         {
/*  481 */           os.close();
/*      */ 
/*      */         }
/*      */         catch (IOException f) {}
/*      */       }
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  489 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  493 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void discardGlobalStateCache()
/*      */   {
/*  501 */     getGlobalStateFile().delete();
/*      */     
/*  503 */     for (int i = 0; i < global_state_cache_wrappers.size(); i++)
/*      */     {
/*  505 */       ((CachedStateWrapper)global_state_cache_wrappers.get(i)).clearCache();
/*      */     }
/*      */     
/*  508 */     global_state_cache_wrappers.clear();
/*  509 */     global_state_cache_wrappers.trimToSize();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void importDownloadState(File source_dir, byte[] download_hash)
/*      */     throws DownloadManagerException
/*      */   {
/*  519 */     String hash_str = ByteFormatter.encodeString(download_hash);
/*      */     
/*  521 */     String state_file = hash_str + ".dat";
/*      */     
/*  523 */     File target_state_file = new File(ACTIVE_DIR, state_file);
/*  524 */     File source_state_file = new File(source_dir, state_file);
/*      */     
/*  526 */     if (!source_state_file.exists())
/*      */     {
/*  528 */       throw new DownloadManagerException("Source state file missing: " + source_state_file);
/*      */     }
/*      */     
/*  531 */     if (target_state_file.exists())
/*      */     {
/*  533 */       target_state_file.delete();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  538 */     if (!FileUtil.copyFile(source_state_file, target_state_file))
/*      */     {
/*  540 */       throw new DownloadManagerException("Failed to copy state file: " + source_state_file + " -> " + target_state_file);
/*      */     }
/*      */     
/*  543 */     File source_state_dir = new File(source_dir, hash_str);
/*      */     
/*  545 */     if (source_state_dir.exists()) {
/*      */       try
/*      */       {
/*  548 */         FileUtil.copyFileOrDirectory(source_state_dir, ACTIVE_DIR);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  552 */         target_state_file.delete();
/*      */         
/*  554 */         throw new DownloadManagerException("Failed to copy state dir: " + source_dir + " -> " + ACTIVE_DIR, e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void deleteDownloadState(byte[] download_hash)
/*      */     throws DownloadManagerException
/*      */   {
/*  565 */     deleteDownloadState(ACTIVE_DIR, download_hash);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void deleteDownloadState(File source_dir, byte[] download_hash)
/*      */     throws DownloadManagerException
/*      */   {
/*  575 */     String hash_str = ByteFormatter.encodeString(download_hash);
/*      */     
/*  577 */     String state_file = hash_str + ".dat";
/*      */     
/*  579 */     File target_state_file = new File(source_dir, state_file);
/*      */     
/*  581 */     if (target_state_file.exists())
/*      */     {
/*  583 */       if (!target_state_file.delete())
/*      */       {
/*  585 */         throw new DownloadManagerException("Failed to delete state file: " + target_state_file);
/*      */       }
/*      */     }
/*      */     
/*  589 */     File target_state_dir = new File(source_dir, hash_str);
/*      */     
/*  591 */     if (target_state_dir.exists())
/*      */     {
/*  593 */       if (!FileUtil.recursiveDelete(target_state_dir))
/*      */       {
/*  595 */         throw new DownloadManagerException("Failed to delete state dir: " + target_state_dir);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DownloadManagerStateImpl(DownloadManagerImpl _download_manager, TorrentUtils.ExtendedTorrent _torrent)
/*      */   {
/*  605 */     this.download_manager = _download_manager;
/*  606 */     this.torrent = _torrent;
/*      */     
/*  608 */     this.attributes = this.torrent.getAdditionalMapProperty("attributes");
/*      */     
/*  610 */     if (this.attributes == null)
/*      */     {
/*  612 */       this.attributes = new HashMap();
/*      */     }
/*      */     
/*  615 */     String cat_string = getStringAttribute("category");
/*      */     
/*  617 */     if (cat_string != null)
/*      */     {
/*  619 */       Category cat = CategoryManager.getCategory(cat_string);
/*      */       
/*  621 */       if (cat != null)
/*      */       {
/*  623 */         setCategory(cat);
/*      */       }
/*      */     }
/*      */     
/*  627 */     this.parameters = getMapAttribute("parameters");
/*      */     
/*  629 */     if (this.parameters == null)
/*      */     {
/*  631 */       this.parameters = new HashMap();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  636 */     int version = getIntAttribute("version");
/*      */     
/*  638 */     if (version < 1)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  643 */       if (getPeerSources().length > 0)
/*      */       {
/*  645 */         if (PEPeerSource.isPeerSourceEnabledByDefault("Incoming"))
/*      */         {
/*  647 */           setPeerSourceEnabled("Incoming", true);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else {
/*  653 */         setPeerSources(PEPeerSource.getDefaultEnabledPeerSources());
/*      */       }
/*      */     }
/*      */     
/*  657 */     long flags = getFlags();
/*      */     
/*  659 */     if ((flags & 0x100) != 0L) {
/*      */       try
/*      */       {
/*  662 */         IpFilterManagerFactory.getSingleton().getIPFilter().addExcludedHash(this.torrent.getHash());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  666 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/*  670 */     if (version < 1)
/*      */     {
/*  672 */       setIntAttribute("version", 1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void parameterChanged(String parameterName)
/*      */   {
/*  682 */     informWritten("parameters");
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadManager getDownloadManager()
/*      */   {
/*  688 */     return this.download_manager;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setDownloadManager(DownloadManagerImpl dm)
/*      */   {
/*  695 */     this.download_manager = dm;
/*      */   }
/*      */   
/*      */   public File getStateFile()
/*      */   {
/*      */     try
/*      */     {
/*  702 */       File parent = new File(ACTIVE_DIR, ByteFormatter.encodeString(this.torrent.getHash()) + File.separatorChar);
/*      */       
/*  704 */       return StringInterner.internFile(parent);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  708 */       Debug.printStackTrace(e);
/*      */     }
/*  710 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void clearTrackerResponseCache()
/*      */   {
/*  717 */     setTrackerResponseCache(new HashMap());
/*      */   }
/*      */   
/*      */   public Map getTrackerResponseCache()
/*      */   {
/*  722 */     Map tracker_response_cache = null;
/*      */     
/*  724 */     tracker_response_cache = this.torrent.getAdditionalMapProperty("tracker_cache");
/*      */     
/*  726 */     if (tracker_response_cache == null) {
/*  727 */       tracker_response_cache = new HashMap();
/*      */     }
/*  729 */     return tracker_response_cache;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTrackerResponseCache(Map value)
/*      */   {
/*      */     try
/*      */     {
/*  738 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  742 */       boolean changed = !BEncoder.mapsAreIdentical(value, getTrackerResponseCache());
/*      */       
/*  744 */       if (changed)
/*      */       {
/*  746 */         this.write_required = true;
/*      */         
/*  748 */         this.torrent.setAdditionalMapProperty("tracker_cache", value);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/*  753 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public Map getResumeData()
/*      */   {
/*      */     try
/*      */     {
/*  761 */       this.this_mon.enter();
/*      */       
/*  763 */       return this.torrent.getAdditionalMapProperty("resume");
/*      */     }
/*      */     finally
/*      */     {
/*  767 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearResumeData()
/*      */   {
/*  774 */     setResumeData(null);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setResumeData(Map data)
/*      */   {
/*      */     try
/*      */     {
/*  782 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*  786 */       if (data == null)
/*      */       {
/*  788 */         setLongAttribute("resumecomplete", 1L);
/*      */         
/*  790 */         this.torrent.removeAdditionalProperty("resume");
/*      */       }
/*      */       else
/*      */       {
/*  794 */         this.torrent.setAdditionalMapProperty("resume", data);
/*      */         
/*  796 */         boolean complete = DiskManagerFactory.isTorrentResumeDataComplete(this);
/*      */         
/*  798 */         setLongAttribute("resumecomplete", complete ? 2L : 1L);
/*      */       }
/*      */       
/*  801 */       this.write_required = true;
/*      */     }
/*      */     finally
/*      */     {
/*  805 */       this.this_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  810 */     save();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isResumeDataComplete()
/*      */   {
/*  818 */     long state = getLongAttribute("resumecomplete");
/*      */     
/*  820 */     if (state == 0L)
/*      */     {
/*      */ 
/*      */ 
/*  824 */       boolean complete = DiskManagerFactory.isTorrentResumeDataComplete(this);
/*      */       
/*  826 */       setLongAttribute("resumecomplete", complete ? 2L : 1L);
/*      */       
/*  828 */       return complete;
/*      */     }
/*      */     
/*      */ 
/*  832 */     return state == 2L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TOTorrent getTorrent()
/*      */   {
/*  839 */     return this.torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setActive(boolean active)
/*      */   {
/*  846 */     this.torrent.setDiscardFluff(!active);
/*      */   }
/*      */   
/*      */   public void discardFluff()
/*      */   {
/*  851 */     this.torrent.setDiscardFluff(true);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean exportState(File target_dir)
/*      */   {
/*      */     try
/*      */     {
/*  859 */       this.this_mon.enter();
/*      */       
/*  861 */       save(true);
/*      */       
/*  863 */       byte[] hash = this.torrent.getHash();
/*      */       
/*  865 */       hash_str = ByteFormatter.encodeString(hash);
/*      */       
/*  867 */       String state_file = hash_str + ".dat";
/*      */       
/*  869 */       File existing_state_file = new File(ACTIVE_DIR, state_file);
/*  870 */       File target_state_file = new File(target_dir, state_file);
/*      */       
/*  872 */       if (!FileUtil.copyFile(existing_state_file, target_state_file))
/*      */       {
/*  874 */         throw new IOException("Failed to copy state file");
/*      */       }
/*      */       
/*  877 */       File existing_state_dir = new File(ACTIVE_DIR, hash_str);
/*      */       
/*  879 */       if (existing_state_dir.exists())
/*      */       {
/*  881 */         FileUtil.copyFileOrDirectory(existing_state_dir, target_dir);
/*      */       }
/*      */       
/*  884 */       return true;
/*      */     }
/*      */     catch (Throwable e) {
/*      */       String hash_str;
/*  888 */       Debug.out(e);
/*      */       
/*  890 */       return 0;
/*      */     }
/*      */     finally
/*      */     {
/*  894 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void suppressStateSave(boolean suppress) {
/*  899 */     if (suppress) {
/*  900 */       this.supressWrites += 1;
/*  901 */     } else if (this.supressWrites > 0) {
/*  902 */       this.supressWrites -= 1;
/*      */     }
/*      */   }
/*      */   
/*      */   public void save()
/*      */   {
/*  908 */     save(false);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void save(boolean force)
/*      */   {
/*  914 */     if ((this.supressWrites > 0) && (!force)) {
/*      */       return;
/*      */     }
/*      */     
/*      */     boolean do_write;
/*      */     
/*      */     try
/*      */     {
/*  922 */       this.this_mon.enter();
/*      */       
/*  924 */       do_write = this.write_required;
/*      */       
/*  926 */       this.write_required = false;
/*      */     }
/*      */     finally
/*      */     {
/*  930 */       this.this_mon.exit();
/*      */     }
/*      */     
/*  933 */     if (do_write)
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/*  938 */         if (Logger.isEnabled()) {
/*  939 */           Logger.log(new LogEvent(this.torrent, LOGID, "Saving state for download '" + TorrentUtils.getLocalisedName(this.torrent) + "'"));
/*      */         }
/*      */         
/*  942 */         this.torrent.setAdditionalMapProperty("attributes", this.attributes);
/*      */         
/*  944 */         TorrentUtils.writeToFile(this.torrent, true);
/*      */       }
/*      */       catch (Throwable e) {
/*  947 */         Logger.log(new LogEvent(this.torrent, LOGID, "Saving state", e));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void delete()
/*      */   {
/*      */     try
/*      */     {
/*  959 */       class_mon.enter();
/*      */       
/*  961 */       HashWrapper wrapper = this.torrent.getHashWrapper();
/*      */       
/*  963 */       state_map.remove(wrapper);
/*      */       
/*  965 */       TorrentUtils.delete(this.torrent);
/*      */       
/*  967 */       File dir = new File(ACTIVE_DIR, ByteFormatter.encodeString(wrapper.getBytes()));
/*      */       
/*  969 */       if ((dir.exists()) && (dir.isDirectory()))
/*      */       {
/*  971 */         FileUtil.recursiveDelete(dir);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/*  975 */       Debug.printStackTrace(e);
/*      */     }
/*      */     finally
/*      */     {
/*  979 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void mergeTorrentDetails(TOTorrent other_torrent)
/*      */   {
/*      */     try
/*      */     {
/*  988 */       boolean write = TorrentUtils.mergeAnnounceURLs(other_torrent, this.torrent);
/*      */       
/*      */ 
/*      */ 
/*  992 */       if (write)
/*      */       {
/*  994 */         save();
/*      */         
/*  996 */         if (this.download_manager != null)
/*      */         {
/*  998 */           TRTrackerAnnouncer client = this.download_manager.getTrackerClient();
/*      */           
/* 1000 */           if (client != null)
/*      */           {
/*      */ 
/*      */ 
/* 1004 */             client.resetTrackerUrl(false);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1010 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFlag(long flag, boolean set)
/*      */   {
/* 1019 */     long old_value = getLongAttribute("flags");
/*      */     
/*      */     long new_value;
/*      */     long new_value;
/* 1023 */     if (set)
/*      */     {
/* 1025 */       new_value = old_value | flag;
/*      */     }
/*      */     else
/*      */     {
/* 1029 */       new_value = old_value & (flag ^ 0xFFFFFFFFFFFFFFFF);
/*      */     }
/*      */     
/* 1032 */     if (old_value != new_value)
/*      */     {
/* 1034 */       setLongAttribute("flags", new_value);
/*      */       
/* 1036 */       if ((old_value & 0x100) != (new_value & 0x100)) {
/*      */         try
/*      */         {
/* 1039 */           if ((new_value & 0x100) != 0L)
/*      */           {
/* 1041 */             IpFilterManagerFactory.getSingleton().getIPFilter().addExcludedHash(this.torrent.getHash());
/*      */           }
/*      */           else
/*      */           {
/* 1045 */             IpFilterManagerFactory.getSingleton().getIPFilter().removeExcludedHash(this.torrent.getHash());
/*      */           }
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1050 */           Debug.out(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getFlag(long flag)
/*      */   {
/* 1060 */     long value = getLongAttribute("flags");
/*      */     
/* 1062 */     return (value & flag) != 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getFlags()
/*      */   {
/* 1068 */     return getLongAttribute("flags");
/*      */   }
/*      */   
/*      */   public boolean parameterExists(String name) {
/* 1072 */     return this.parameters.containsKey(name);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setParameterDefault(String name)
/*      */   {
/*      */     try
/*      */     {
/* 1080 */       this.this_mon.enter();
/*      */       
/* 1082 */       Object value = this.parameters.get(name);
/*      */       
/* 1084 */       if (value == null) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1092 */       this.parameters = new LightHashMap(this.parameters);
/*      */       
/* 1094 */       this.parameters.remove(name);
/*      */     }
/*      */     finally
/*      */     {
/* 1098 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1101 */     setMapAttribute("parameters", this.parameters);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLongParameter(String name)
/*      */   {
/*      */     try
/*      */     {
/* 1109 */       this.this_mon.enter();
/*      */       
/* 1111 */       Object value = this.parameters.get(name);
/*      */       long rand;
/* 1113 */       if (value == null)
/*      */       {
/* 1115 */         value = default_parameters.get(name);
/*      */         
/* 1117 */         if (value == null)
/*      */         {
/* 1119 */           Debug.out("Unknown parameter '" + name + "' - must be defined in DownloadManagerState");
/*      */           
/* 1121 */           return 0L;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1130 */         if (name == "max.uploads.when.seeding.enabled")
/*      */         {
/* 1132 */           if (COConfigurationManager.getBooleanParameter("enable.seedingonly.maxuploads"))
/*      */           {
/* 1134 */             value = Boolean.TRUE;
/*      */           }
/*      */         }
/* 1137 */         else if (name == "max.uploads.when.seeding")
/*      */         {
/* 1139 */           int def = COConfigurationManager.getIntParameter("Max Uploads Seeding");
/*      */           
/* 1141 */           value = new Integer(def);
/*      */         }
/* 1143 */         else if (name == "max.uploads")
/*      */         {
/* 1145 */           int def = COConfigurationManager.getIntParameter("Max Uploads");
/*      */           
/* 1147 */           value = new Integer(def);
/*      */         }
/* 1149 */         else if (name == "max.peers")
/*      */         {
/* 1151 */           int def = COConfigurationManager.getIntParameter("Max.Peer.Connections.Per.Torrent");
/*      */           
/* 1153 */           value = new Integer(def);
/*      */         }
/* 1155 */         else if (name == "max.peers.when.seeding.enabled")
/*      */         {
/* 1157 */           if (COConfigurationManager.getBooleanParameter("Max.Peer.Connections.Per.Torrent.When.Seeding.Enable"))
/*      */           {
/* 1159 */             value = Boolean.TRUE;
/*      */           }
/*      */         }
/* 1162 */         else if (name == "max.peers.when.seeding")
/*      */         {
/* 1164 */           int def = COConfigurationManager.getIntParameter("Max.Peer.Connections.Per.Torrent.When.Seeding");
/*      */           
/* 1166 */           value = new Integer(def);
/*      */         }
/* 1168 */         else if (name == "max.seeds")
/*      */         {
/* 1170 */           value = new Integer(COConfigurationManager.getIntParameter("Max Seeds Per Torrent"));
/*      */         }
/* 1172 */         else if (name == "rand")
/*      */         {
/* 1174 */           rand = random.nextLong();
/*      */           
/* 1176 */           setLongParameter(name, rand);
/*      */           
/* 1178 */           value = new Long(rand);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1183 */       if ((value instanceof Boolean))
/*      */       {
/* 1185 */         return ((Boolean)value).booleanValue() ? 1 : 0;
/*      */       }
/* 1187 */       if ((value instanceof Integer))
/*      */       {
/* 1189 */         return ((Integer)value).longValue();
/*      */       }
/* 1191 */       if ((value instanceof Long))
/*      */       {
/* 1193 */         return ((Long)value).longValue();
/*      */       }
/*      */       
/* 1196 */       Debug.out("Invalid parameter value for '" + name + "' - " + value);
/*      */       
/* 1198 */       return 0L;
/*      */     }
/*      */     finally
/*      */     {
/* 1202 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLongParameter(String name, long value)
/*      */   {
/* 1211 */     Object default_value = default_parameters.get(name);
/*      */     
/* 1213 */     if (default_value == null)
/*      */     {
/* 1215 */       Debug.out("Unknown parameter '" + name + "' - must be defined in DownloadManagerState");
/*      */     }
/*      */     try
/*      */     {
/* 1219 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1224 */       this.parameters = new LightHashMap(this.parameters);
/*      */       
/* 1226 */       this.parameters.put(name, new Long(value));
/*      */       
/* 1228 */       setMapAttribute("parameters", this.parameters);
/*      */     }
/*      */     finally
/*      */     {
/* 1232 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getIntParameter(String name)
/*      */   {
/* 1240 */     return (int)getLongParameter(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setIntParameter(String name, int value)
/*      */   {
/* 1248 */     setLongParameter(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getBooleanParameter(String name)
/*      */   {
/* 1255 */     return getLongParameter(name) != 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBooleanParameter(String name, boolean value)
/*      */   {
/* 1263 */     setLongParameter(name, value ? 1L : 0L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setAttribute(String name, String value)
/*      */   {
/* 1272 */     if (name.equals("category"))
/*      */     {
/* 1274 */       if (value == null)
/*      */       {
/* 1276 */         setCategory(null);
/*      */       }
/*      */       else {
/* 1279 */         Category cat = CategoryManager.getCategory(value);
/*      */         
/* 1281 */         if (cat == null)
/*      */         {
/* 1283 */           cat = CategoryManager.createCategory(value);
/*      */         }
/*      */         
/*      */ 
/* 1287 */         setCategory(cat);
/*      */       }
/* 1289 */       return;
/*      */     }
/*      */     
/* 1292 */     if ((name.equals("relativepath")) && 
/* 1293 */       (value.length() > 0)) {
/* 1294 */       File relative_path_file = new File(value);
/* 1295 */       relative_path_file = DownloadManagerDefaultPaths.normaliseRelativePath(relative_path_file);
/* 1296 */       value = relative_path_file == null ? "" : relative_path_file.getPath();
/*      */     }
/*      */     
/*      */ 
/* 1300 */     setStringAttribute(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getAttribute(String name)
/*      */   {
/* 1307 */     if (name.equals("category"))
/*      */     {
/* 1309 */       Category cat = getCategory();
/*      */       
/* 1311 */       if (cat == null)
/*      */       {
/* 1313 */         return null;
/*      */       }
/*      */       
/* 1316 */       if (cat == CategoryManager.getCategory(2))
/*      */       {
/* 1318 */         return null;
/*      */       }
/*      */       
/* 1321 */       return cat.getName();
/*      */     }
/*      */     
/*      */ 
/* 1325 */     return getStringAttribute(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Category getCategory()
/*      */   {
/* 1333 */     return this.category;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCategory(Category cat)
/*      */   {
/* 1340 */     if (cat == this.category) {
/* 1341 */       return;
/*      */     }
/*      */     
/* 1344 */     if ((cat != null) && (cat.getType() != 0))
/*      */     {
/* 1346 */       cat = null;
/* 1347 */       if (cat == this.category) {
/* 1348 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1352 */     Category oldCategory = this.category == null ? CategoryManager.getCategory(2) : this.category;
/*      */     
/* 1354 */     this.category = cat;
/*      */     
/* 1356 */     if (oldCategory != null)
/*      */     {
/* 1358 */       oldCategory.removeManager(this);
/*      */     }
/*      */     
/* 1361 */     DownloadManager dm = getDownloadManager();
/*      */     
/* 1363 */     if ((dm != null) && (!dm.isDestroyed()))
/*      */     {
/* 1365 */       if (this.category != null)
/*      */       {
/* 1367 */         this.category.addManager(this);
/*      */       }
/*      */       else
/*      */       {
/* 1371 */         CategoryManager.getCategory(2).addManager(this);
/*      */       }
/*      */     }
/*      */     
/* 1375 */     if (this.category != null)
/*      */     {
/* 1377 */       setStringAttribute("category", this.category.getName());
/*      */     }
/*      */     else
/*      */     {
/* 1381 */       setStringAttribute("category", null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTrackerClientExtensions()
/*      */   {
/* 1388 */     return getStringAttribute("trackerclientextensions");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTrackerClientExtensions(String value)
/*      */   {
/* 1395 */     setStringAttribute("trackerclientextensions", value);
/*      */   }
/*      */   
/*      */   public String getDisplayName() {
/* 1399 */     return getStringAttribute("displayname");
/*      */   }
/*      */   
/*      */   public void setDisplayName(String value) {
/* 1403 */     setStringAttribute("displayname", value);
/*      */   }
/*      */   
/*      */   public String getUserComment() {
/* 1407 */     return getStringAttribute("comment");
/*      */   }
/*      */   
/*      */   public void setUserComment(String value) {
/* 1411 */     setStringAttribute("comment", value);
/*      */   }
/*      */   
/*      */   public String getRelativeSavePath() {
/* 1415 */     return getStringAttribute("relativepath");
/*      */   }
/*      */   
/*      */   public DiskManagerFileInfo getPrimaryFile() {
/* 1419 */     int primaryIndex = -1;
/* 1420 */     DiskManagerFileInfo[] fileInfo = this.download_manager.getDiskManagerFileInfoSet().getFiles();
/* 1421 */     if (hasAttribute("primaryfileidx")) {
/* 1422 */       primaryIndex = getIntAttribute("primaryfileidx");
/*      */     }
/*      */     
/* 1425 */     if ((primaryIndex < 0) || (primaryIndex >= fileInfo.length)) {
/* 1426 */       primaryIndex = -1;
/* 1427 */       if (fileInfo.length > 0) {
/* 1428 */         int idxBiggest = -1;
/* 1429 */         long lBiggest = -1L;
/* 1430 */         int numChecked = 0;
/* 1431 */         for (int i = 0; (i < fileInfo.length) && (numChecked < 10); i++) {
/* 1432 */           if (!fileInfo[i].isSkipped()) {
/* 1433 */             numChecked++;
/* 1434 */             if (fileInfo[i].getLength() > lBiggest) {
/* 1435 */               lBiggest = fileInfo[i].getLength();
/* 1436 */               idxBiggest = i;
/*      */             }
/*      */           }
/*      */         }
/* 1440 */         if (idxBiggest >= 0) {
/* 1441 */           primaryIndex = idxBiggest;
/*      */         }
/*      */       }
/* 1444 */       if (primaryIndex >= 0) {
/* 1445 */         setPrimaryFile(fileInfo[primaryIndex]);
/*      */       }
/*      */     }
/*      */     
/* 1449 */     if (primaryIndex >= 0) {
/* 1450 */       return fileInfo[primaryIndex];
/*      */     }
/* 1452 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPrimaryFile(DiskManagerFileInfo dmfi)
/*      */   {
/* 1459 */     setIntAttribute("primaryfileidx", dmfi.getIndex());
/*      */   }
/*      */   
/*      */ 
/*      */   public String[] getNetworks()
/*      */   {
/* 1465 */     List values = getListAttributeSupport("networks");
/*      */     
/* 1467 */     List res = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/* 1471 */     for (int i = 0; i < values.size(); i++)
/*      */     {
/* 1473 */       String nw = (String)values.get(i);
/*      */       
/* 1475 */       for (int j = 0; j < org.gudy.azureus2.core3.util.AENetworkClassifier.AT_NETWORKS.length; j++)
/*      */       {
/* 1477 */         String nn = org.gudy.azureus2.core3.util.AENetworkClassifier.AT_NETWORKS[j];
/*      */         
/* 1479 */         if (nn.equals(nw))
/*      */         {
/* 1481 */           res.add(nn);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1486 */     String[] x = new String[res.size()];
/*      */     
/* 1488 */     res.toArray(x);
/*      */     
/* 1490 */     return x;
/*      */   }
/*      */   
/*      */   public boolean isNetworkEnabled(String network)
/*      */   {
/* 1495 */     List values = getListAttributeSupport("networks");
/* 1496 */     return values.contains(network);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNetworks(String[] networks)
/*      */   {
/* 1503 */     if (networks == null)
/*      */     {
/* 1505 */       networks = new String[0];
/*      */     }
/*      */     
/* 1508 */     List l = new ArrayList();
/*      */     
/* 1510 */     java.util.Collections.addAll(l, networks);
/*      */     
/* 1512 */     setListAttribute("networks", l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setNetworkEnabled(String network, boolean enabled)
/*      */   {
/* 1519 */     List values = getListAttributeSupport("networks");
/* 1520 */     boolean alreadyEnabled = values.contains(network);
/* 1521 */     List l = new ArrayList();
/*      */     
/* 1523 */     if ((enabled) && (!alreadyEnabled)) {
/* 1524 */       for (int i = 0; i < values.size(); i++) {
/* 1525 */         l.add(values.get(i));
/*      */       }
/* 1527 */       l.add(network);
/* 1528 */       setListAttribute("networks", l);
/*      */     }
/* 1530 */     if ((!enabled) && (alreadyEnabled)) {
/* 1531 */       for (int i = 0; i < values.size(); i++) {
/* 1532 */         l.add(values.get(i));
/*      */       }
/* 1534 */       l.remove(network);
/* 1535 */       setListAttribute("networks", l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] getPeerSources()
/*      */   {
/* 1544 */     List values = getListAttributeSupport("peersources");
/*      */     
/* 1546 */     List res = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/* 1550 */     for (int i = 0; i < values.size(); i++)
/*      */     {
/* 1552 */       String ps = (String)values.get(i);
/*      */       
/* 1554 */       for (int j = 0; j < PEPeerSource.PS_SOURCES.length; j++)
/*      */       {
/* 1556 */         String x = PEPeerSource.PS_SOURCES[j];
/*      */         
/* 1558 */         if (x.equals(ps))
/*      */         {
/* 1560 */           res.add(x);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1565 */     String[] x = new String[res.size()];
/*      */     
/* 1567 */     res.toArray(x);
/*      */     
/* 1569 */     return x;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPeerSourceEnabled(String peerSource)
/*      */   {
/* 1576 */     List values = getListAttributeSupport("peersources");
/*      */     
/* 1578 */     return values.contains(peerSource);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isPeerSourcePermitted(String peerSource)
/*      */   {
/* 1587 */     if (peerSource.equals("DHT"))
/*      */     {
/* 1589 */       if ((TorrentUtils.getPrivate(this.torrent)) || (!TorrentUtils.getDHTBackupEnabled(this.torrent)))
/*      */       {
/*      */ 
/* 1592 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1598 */     if (peerSource.equals("PeerExchange"))
/*      */     {
/* 1600 */       if (TorrentUtils.getPrivate(this.torrent))
/*      */       {
/* 1602 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1606 */     List values = getListAttributeSupport("peersourcesdenied");
/*      */     
/* 1608 */     if (values != null)
/*      */     {
/* 1610 */       if (values.contains(peerSource))
/*      */       {
/* 1612 */         return false;
/*      */       }
/*      */     }
/*      */     
/* 1616 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPeerSourcePermitted(String peerSource, boolean enabled)
/*      */   {
/* 1624 */     if (!getFlag(32L))
/*      */     {
/* 1626 */       Logger.log(new LogEvent(this.torrent, LOGID, "Attempt to modify permitted peer sources denied as disabled '" + TorrentUtils.getLocalisedName(this.torrent) + "'"));
/*      */       
/*      */ 
/* 1629 */       return;
/*      */     }
/*      */     
/* 1632 */     if (!enabled)
/*      */     {
/* 1634 */       setPeerSourceEnabled(peerSource, false);
/*      */     }
/*      */     
/* 1637 */     List values = getListAttributeSupport("peersourcesdenied");
/*      */     
/* 1639 */     if (values == null)
/*      */     {
/* 1641 */       if (!enabled)
/*      */       {
/* 1643 */         values = new ArrayList();
/*      */         
/* 1645 */         values.add(peerSource);
/*      */         
/* 1647 */         setListAttribute("peersourcesdenied", values);
/*      */       }
/*      */     }
/*      */     else {
/* 1651 */       if (enabled)
/*      */       {
/* 1653 */         values.remove(peerSource);
/*      */ 
/*      */ 
/*      */       }
/* 1657 */       else if (!values.contains(peerSource))
/*      */       {
/* 1659 */         values.add(peerSource);
/*      */       }
/*      */       
/*      */ 
/* 1663 */       setListAttribute("peersourcesdenied", values);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPeerSources(String[] ps)
/*      */   {
/* 1671 */     if (ps == null)
/*      */     {
/* 1673 */       ps = new String[0];
/*      */     }
/*      */     
/* 1676 */     List l = new ArrayList();
/*      */     
/* 1678 */     for (int i = 0; i < ps.length; i++)
/*      */     {
/* 1680 */       String p = ps[i];
/*      */       
/* 1682 */       if (isPeerSourcePermitted(p))
/*      */       {
/* 1684 */         l.add(ps[i]);
/*      */       }
/*      */     }
/*      */     
/* 1688 */     setListAttribute("peersources", l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPeerSourceEnabled(String source, boolean enabled)
/*      */   {
/* 1696 */     if ((enabled) && (!isPeerSourcePermitted(source)))
/*      */     {
/* 1698 */       return;
/*      */     }
/*      */     
/* 1701 */     List values = getListAttributeSupport("peersources");
/*      */     
/* 1703 */     boolean alreadyEnabled = values.contains(source);
/*      */     
/* 1705 */     List l = new ArrayList();
/*      */     
/* 1707 */     if ((enabled) && (!alreadyEnabled)) {
/* 1708 */       for (int i = 0; i < values.size(); i++) {
/* 1709 */         l.add(values.get(i));
/*      */       }
/* 1711 */       l.add(source);
/* 1712 */       setListAttribute("peersources", l);
/*      */     }
/* 1714 */     if ((!enabled) && (alreadyEnabled)) {
/* 1715 */       for (int i = 0; i < values.size(); i++) {
/* 1716 */         l.add(values.get(i));
/*      */       }
/* 1718 */       l.remove(source);
/* 1719 */       setListAttribute("peersources", l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 1726 */   private volatile WeakReference<LinkFileMap> file_link_cache = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFileLink(int source_index, File link_source, File link_destination)
/*      */   {
/* 1734 */     LinkFileMap links = getFileLinks();
/*      */     
/* 1736 */     File existing = links.get(source_index, link_source);
/*      */     
/* 1738 */     if (link_destination == null)
/*      */     {
/* 1740 */       if (existing != null) {}
/*      */ 
/*      */ 
/*      */     }
/* 1744 */     else if ((existing != null) && (existing.equals(link_destination)))
/*      */     {
/* 1746 */       return;
/*      */     }
/*      */     
/* 1749 */     links.put(source_index, link_source, link_destination);
/*      */     
/* 1751 */     List list = new ArrayList();
/*      */     
/* 1753 */     Iterator<LinkFileMap.Entry> it = links.entryIterator();
/*      */     
/* 1755 */     while (it.hasNext())
/*      */     {
/* 1757 */       LinkFileMap.Entry entry = (LinkFileMap.Entry)it.next();
/*      */       
/* 1759 */       int index = entry.getIndex();
/* 1760 */       File source = entry.getFromFile();
/* 1761 */       File target = entry.getToFile();
/*      */       
/* 1763 */       String str = index + "\n" + source + "\n" + (target == null ? "" : target.toString());
/*      */       
/* 1765 */       list.add(str);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1770 */     synchronized (this)
/*      */     {
/* 1772 */       this.file_link_cache = new WeakReference(links);
/*      */     }
/*      */     
/* 1775 */     setListAttribute("filelinks2", list);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFileLinks(List<Integer> source_indexes, List<File> link_sources, List<File> link_destinations)
/*      */   {
/* 1784 */     LinkFileMap links = getFileLinks();
/*      */     
/* 1786 */     boolean changed = false;
/*      */     
/* 1788 */     for (int i = 0; i < link_sources.size(); i++)
/*      */     {
/* 1790 */       int source_index = ((Integer)source_indexes.get(i)).intValue();
/* 1791 */       File link_source = (File)link_sources.get(i);
/* 1792 */       File link_destination = (File)link_destinations.get(i);
/*      */       
/* 1794 */       File existing = links.get(source_index, link_source);
/*      */       
/* 1796 */       if (link_destination == null ? 
/*      */       
/* 1798 */         existing == null : 
/*      */         
/*      */ 
/*      */ 
/* 1802 */         (existing == null) || (!existing.equals(link_destination)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1807 */         links.put(source_index, link_source, link_destination);
/*      */         
/* 1809 */         changed = true;
/*      */       }
/*      */     }
/* 1812 */     if (!changed)
/*      */     {
/* 1814 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1819 */     List list = new ArrayList();
/*      */     
/* 1821 */     Iterator<LinkFileMap.Entry> it = links.entryIterator();
/*      */     
/* 1823 */     while (it.hasNext())
/*      */     {
/* 1825 */       LinkFileMap.Entry entry = (LinkFileMap.Entry)it.next();
/*      */       
/* 1827 */       int index = entry.getIndex();
/* 1828 */       File source = entry.getFromFile();
/* 1829 */       File target = entry.getToFile();
/*      */       
/* 1831 */       String str = index + "\n" + source + "\n" + (target == null ? "" : target.toString());
/*      */       
/* 1833 */       list.add(str);
/*      */     }
/*      */     
/* 1836 */     synchronized (this)
/*      */     {
/* 1838 */       this.file_link_cache = new WeakReference(links);
/*      */     }
/*      */     
/* 1841 */     setListAttribute("filelinks2", list);
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearFileLinks()
/*      */   {
/* 1847 */     LinkFileMap links = getFileLinks();
/*      */     
/* 1849 */     List list = new ArrayList();
/*      */     
/* 1851 */     Iterator<LinkFileMap.Entry> it = links.entryIterator();
/*      */     
/* 1853 */     boolean changed = false;
/*      */     
/* 1855 */     while (it.hasNext())
/*      */     {
/* 1857 */       LinkFileMap.Entry entry = (LinkFileMap.Entry)it.next();
/*      */       
/* 1859 */       int index = entry.getIndex();
/* 1860 */       File source = entry.getFromFile();
/* 1861 */       File target = entry.getToFile();
/*      */       
/* 1863 */       if (target != null)
/*      */       {
/* 1865 */         changed = true;
/*      */       }
/*      */       
/* 1868 */       String str = index + "\n" + source + "\n";
/*      */       
/* 1870 */       list.add(str);
/*      */     }
/*      */     
/* 1873 */     if (changed)
/*      */     {
/* 1875 */       synchronized (this)
/*      */       {
/* 1877 */         this.file_link_cache = null;
/*      */       }
/*      */       
/* 1880 */       setListAttribute("filelinks2", list);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public File getFileLink(int source_index, File link_source)
/*      */   {
/* 1891 */     LinkFileMap map = null;
/*      */     
/* 1893 */     WeakReference<LinkFileMap> ref = this.file_link_cache;
/*      */     
/* 1895 */     if (ref != null)
/*      */     {
/* 1897 */       map = (LinkFileMap)ref.get();
/*      */     }
/*      */     
/* 1900 */     if (map == null)
/*      */     {
/* 1902 */       map = getFileLinks();
/*      */       
/* 1904 */       synchronized (this)
/*      */       {
/* 1906 */         this.file_link_cache = new WeakReference(map);
/*      */       }
/*      */     }
/*      */     
/* 1910 */     File res = map.get(source_index, link_source);
/*      */     
/*      */ 
/*      */ 
/* 1914 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public LinkFileMap getFileLinks()
/*      */   {
/* 1920 */     LinkFileMap map = null;
/*      */     
/* 1922 */     WeakReference<LinkFileMap> ref = this.file_link_cache;
/*      */     
/* 1924 */     if (ref != null)
/*      */     {
/* 1926 */       map = (LinkFileMap)ref.get();
/*      */     }
/*      */     
/* 1929 */     if (map == null)
/*      */     {
/* 1931 */       map = getFileLinksSupport();
/*      */       
/* 1933 */       synchronized (this)
/*      */       {
/* 1935 */         this.file_link_cache = new WeakReference(map);
/*      */       }
/*      */     }
/*      */     
/* 1939 */     return map;
/*      */   }
/*      */   
/*      */ 
/*      */   private LinkFileMap getFileLinksSupport()
/*      */   {
/* 1945 */     LinkFileMap res = new LinkFileMap();
/*      */     
/* 1947 */     List new_values = getListAttributeSupport("filelinks2");
/*      */     
/* 1949 */     if (new_values.size() > 0)
/*      */     {
/* 1951 */       for (int i = 0; i < new_values.size(); i++)
/*      */       {
/* 1953 */         String entry = (String)new_values.get(i);
/*      */         
/* 1955 */         String[] bits = entry.split("\n");
/*      */         
/* 1957 */         if (bits.length >= 2) {
/*      */           try
/*      */           {
/* 1960 */             int index = Integer.parseInt(bits[0].trim());
/* 1961 */             File source = new File(bits[1].trim());
/* 1962 */             File target = bits.length < 3 ? null : new File(bits[2].trim());
/*      */             
/* 1964 */             if (index >= 0)
/*      */             {
/* 1966 */               res.put(index, source, target);
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/* 1972 */               res.putMigration(source, target);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/* 1976 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1982 */       List old_values = getListAttributeSupport("filelinks");
/*      */       
/* 1984 */       for (int i = 0; i < old_values.size(); i++)
/*      */       {
/* 1986 */         String entry = (String)old_values.get(i);
/*      */         
/* 1988 */         int sep = entry.indexOf("\n");
/*      */         
/* 1990 */         if (sep != -1)
/*      */         {
/* 1992 */           File target = sep == entry.length() - 1 ? null : new File(entry.substring(sep + 1));
/*      */           
/* 1994 */           res.putMigration(new File(entry.substring(0, sep)), target);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2001 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isOurContent()
/*      */   {
/* 2009 */     Map mapAttr = getMapAttribute("Plugin.azdirector.ContentMap");
/*      */     
/* 2011 */     return (mapAttr != null) && (mapAttr.containsKey("DIRECTOR PUBLISH"));
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
/*      */   protected void setStringAttribute(String attribute_name, String attribute_value)
/*      */   {
/* 2058 */     boolean changed = false;
/*      */     try
/*      */     {
/* 2061 */       this.this_mon.enter();
/*      */       
/* 2063 */       if (attribute_value == null)
/*      */       {
/* 2065 */         if (this.attributes.containsKey(attribute_name))
/*      */         {
/* 2067 */           this.attributes.remove(attribute_name);
/*      */           
/* 2069 */           this.write_required = (changed = 1);
/*      */         }
/*      */       }
/*      */       else {
/*      */         try {
/* 2074 */           byte[] existing_bytes = (byte[])this.attributes.get(attribute_name);
/*      */           
/* 2076 */           byte[] new_bytes = attribute_value.getBytes("UTF8");
/*      */           
/* 2078 */           if ((existing_bytes == null) || (!Arrays.equals(existing_bytes, new_bytes)))
/*      */           {
/*      */ 
/* 2081 */             this.attributes.put(attribute_name, new_bytes);
/*      */             
/* 2083 */             this.write_required = (changed = 1);
/*      */           }
/*      */         }
/*      */         catch (UnsupportedEncodingException e)
/*      */         {
/* 2088 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2093 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2096 */     if (changed)
/*      */     {
/* 2098 */       informWritten(attribute_name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getLongAttribute(String attribute_name)
/*      */   {
/* 2106 */     informWillRead(attribute_name);
/*      */     try
/*      */     {
/* 2109 */       this.this_mon.enter();
/*      */       
/* 2111 */       Long l = (Long)this.attributes.get(attribute_name);
/*      */       Object def;
/* 2113 */       if (l == null)
/*      */       {
/* 2115 */         def = default_attributes.get(attribute_name);
/*      */         boolean featured;
/* 2117 */         if (def != null) {
/*      */           long l1;
/* 2119 */           if ((def instanceof Long))
/*      */           {
/* 2121 */             return ((Long)def).longValue();
/*      */           }
/* 2123 */           if ((def instanceof Integer))
/*      */           {
/* 2125 */             return ((Integer)def).longValue();
/*      */           }
/*      */           
/*      */ 
/* 2129 */           Debug.out("unknown default type " + def);
/*      */         }
/* 2131 */         else if (attribute_name == "file.expand")
/*      */         {
/* 2133 */           featured = TorrentUtils.isFeaturedContent(this.torrent);
/*      */           
/* 2135 */           long res = featured ? 1L : 0L;
/*      */           
/* 2137 */           this.attributes.put(attribute_name, new Long(res));
/*      */           
/* 2139 */           this.write_required = true;
/*      */           
/* 2141 */           return res;
/*      */         }
/*      */         
/* 2144 */         return 0L;
/*      */       }
/*      */       
/* 2147 */       return l.longValue();
/*      */     }
/*      */     finally
/*      */     {
/* 2151 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLongAttribute(String attribute_name, long attribute_value)
/*      */   {
/* 2160 */     boolean changed = false;
/*      */     try
/*      */     {
/* 2163 */       this.this_mon.enter();
/*      */       
/* 2165 */       Long existing_value = (Long)this.attributes.get(attribute_name);
/*      */       
/* 2167 */       if ((existing_value == null) || (existing_value.longValue() != attribute_value))
/*      */       {
/*      */ 
/* 2170 */         this.attributes.put(attribute_name, new Long(attribute_value));
/*      */         
/* 2172 */         this.write_required = (changed = 1);
/*      */       }
/*      */     }
/*      */     finally {
/* 2176 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2179 */     if (changed)
/*      */     {
/* 2181 */       informWritten(attribute_name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setListAttribute(String name, String[] values)
/*      */   {
/* 2190 */     List list = values == null ? null : Arrays.asList((Object[])values.clone());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2200 */     setListAttribute(name, list);
/*      */   }
/*      */   
/*      */   public String getListAttribute(String name, int idx) {
/* 2204 */     if ((name.equals("networks")) || (name.equals("peersources"))) {
/* 2205 */       throw new UnsupportedOperationException("not supported right now, implement it yourself :P");
/*      */     }
/* 2207 */     informWillRead(name);
/*      */     try
/*      */     {
/* 2210 */       this.this_mon.enter();
/* 2211 */       List values = (List)this.attributes.get(name);
/* 2212 */       if ((values == null) || (idx >= values.size()) || (idx < 0))
/* 2213 */         return null;
/* 2214 */       Object o = values.get(idx);
/* 2215 */       byte[] bytes; if ((o instanceof byte[])) {
/* 2216 */         bytes = (byte[])o;
/* 2217 */         String s = null;
/*      */         try {
/* 2219 */           s = StringInterner.intern(new String(bytes, "UTF8"));
/*      */         } catch (UnsupportedEncodingException e) {
/* 2221 */           Debug.printStackTrace(e);
/*      */         }
/* 2223 */         if (s != null)
/* 2224 */           values.set(idx, s);
/* 2225 */         return s; }
/* 2226 */       if ((o instanceof String)) {
/* 2227 */         return (String)o;
/*      */       }
/*      */     } finally {
/* 2230 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2233 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String[] getListAttribute(String attribute_name)
/*      */   {
/* 2240 */     if (attribute_name == "networks")
/*      */     {
/* 2242 */       return getNetworks();
/*      */     }
/* 2244 */     if (attribute_name == "peersources")
/*      */     {
/* 2246 */       return getPeerSources();
/*      */     }
/*      */     
/*      */ 
/* 2250 */     List l = getListAttributeSupport(attribute_name);
/*      */     
/* 2252 */     if (l == null)
/*      */     {
/* 2254 */       return null;
/*      */     }
/*      */     
/* 2257 */     String[] res = new String[l.size()];
/*      */     try
/*      */     {
/* 2260 */       res = (String[])l.toArray(res);
/*      */     }
/*      */     catch (ArrayStoreException e) {
/* 2263 */       Debug.out("getListAttribute( " + attribute_name + ") - object isnt String - " + e);
/*      */       
/* 2265 */       return null;
/*      */     }
/*      */     
/*      */ 
/* 2269 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List getListAttributeSupport(String attribute_name)
/*      */   {
/* 2277 */     informWillRead(attribute_name);
/*      */     try
/*      */     {
/* 2280 */       this.this_mon.enter();
/*      */       
/* 2282 */       List values = (List)this.attributes.get(attribute_name);
/*      */       
/* 2284 */       List res = new ArrayList(values != null ? values.size() : 0);
/*      */       int i;
/* 2286 */       if (values != null)
/*      */       {
/* 2288 */         for (i = 0; i < values.size(); i++)
/*      */         {
/* 2290 */           Object o = values.get(i);
/*      */           
/* 2292 */           if ((o instanceof byte[]))
/*      */           {
/* 2294 */             byte[] bytes = (byte[])o;
/*      */             
/* 2296 */             String s = null;
/*      */             
/*      */             try
/*      */             {
/* 2300 */               s = StringInterner.intern(new String(bytes, "UTF8"));
/*      */             }
/*      */             catch (UnsupportedEncodingException e)
/*      */             {
/* 2304 */               Debug.printStackTrace(e);
/*      */             }
/*      */             
/* 2307 */             if (s != null)
/*      */             {
/* 2309 */               res.add(s);
/* 2310 */               values.set(i, s);
/*      */             }
/* 2312 */           } else if ((o instanceof String))
/*      */           {
/* 2314 */             res.add(o);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2319 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 2323 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setListAttribute(String attribute_name, List attribute_value)
/*      */   {
/* 2332 */     boolean changed = false;
/*      */     try
/*      */     {
/* 2335 */       this.this_mon.enter();
/*      */       
/* 2337 */       if (attribute_value == null)
/*      */       {
/* 2339 */         if (this.attributes.containsKey(attribute_name))
/*      */         {
/* 2341 */           this.attributes.remove(attribute_name);
/*      */           
/* 2343 */           this.write_required = (changed = 1);
/*      */         }
/*      */       }
/*      */       else {
/* 2347 */         List old_value = getListAttributeSupport(attribute_name);
/*      */         
/* 2349 */         if ((old_value == null) || (old_value.size() != attribute_value.size()))
/*      */         {
/* 2351 */           this.attributes.put(attribute_name, attribute_value);
/*      */           
/* 2353 */           this.write_required = (changed = 1);
/*      */         }
/*      */         else
/*      */         {
/* 2357 */           if (old_value == attribute_value)
/*      */           {
/* 2359 */             Debug.out("setListAttribute: should clone?");
/*      */           }
/*      */           
/* 2362 */           changed = !BEncoder.listsAreIdentical(old_value, attribute_value);
/*      */           
/* 2364 */           if (changed)
/*      */           {
/* 2366 */             this.write_required = true;
/*      */             
/* 2368 */             this.attributes.put(attribute_name, attribute_value);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2374 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2377 */     if (changed)
/*      */     {
/* 2379 */       informWritten(attribute_name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map getMapAttribute(String attribute_name)
/*      */   {
/* 2387 */     informWillRead(attribute_name);
/*      */     try
/*      */     {
/* 2390 */       this.this_mon.enter();
/*      */       
/* 2392 */       Map value = (Map)this.attributes.get(attribute_name);
/*      */       
/* 2394 */       return value;
/*      */     }
/*      */     finally
/*      */     {
/* 2398 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMapAttribute(String attribute_name, Map attribute_value)
/*      */   {
/* 2407 */     setMapAttribute(attribute_name, attribute_value, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setMapAttribute(String attribute_name, Map attribute_value, boolean disable_change_notification)
/*      */   {
/* 2416 */     boolean changed = false;
/*      */     try
/*      */     {
/* 2419 */       this.this_mon.enter();
/*      */       
/* 2421 */       if (attribute_value == null)
/*      */       {
/* 2423 */         if (this.attributes.containsKey(attribute_name))
/*      */         {
/* 2425 */           this.attributes.remove(attribute_name);
/*      */           
/* 2427 */           this.write_required = (changed = 1);
/*      */         }
/*      */       }
/*      */       else {
/* 2431 */         Map old_value = getMapAttribute(attribute_name);
/*      */         
/* 2433 */         if ((old_value == null) || (old_value.size() != attribute_value.size()))
/*      */         {
/* 2435 */           this.attributes.put(attribute_name, attribute_value);
/*      */           
/* 2437 */           this.write_required = (changed = 1);
/*      */         }
/*      */         else
/*      */         {
/* 2441 */           if (old_value == attribute_value)
/*      */           {
/* 2443 */             Debug.out("setMapAttribute: should clone?");
/*      */           }
/*      */           
/* 2446 */           changed = !BEncoder.mapsAreIdentical(old_value, attribute_value);
/*      */           
/* 2448 */           if (changed)
/*      */           {
/* 2450 */             this.write_required = true;
/*      */             
/* 2452 */             this.attributes.put(attribute_name, attribute_value);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 2458 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 2461 */     if ((changed) && (!disable_change_notification))
/*      */     {
/* 2463 */       informWritten(attribute_name);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean hasAttribute(String name)
/*      */   {
/*      */     try
/*      */     {
/* 2473 */       this.this_mon.enter();
/*      */       boolean bool;
/* 2475 */       if (this.attributes == null) { return false;
/*      */       }
/* 2477 */       return this.attributes.containsKey(name);
/*      */     }
/*      */     finally
/*      */     {
/* 2481 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setIntAttribute(String name, int value)
/*      */   {
/* 2492 */     setLongAttribute(name, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getIntAttribute(String name)
/*      */   {
/* 2499 */     return (int)getLongAttribute(name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setBooleanAttribute(String name, boolean value)
/*      */   {
/* 2507 */     setLongAttribute(name, value ? 1 : 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean getBooleanAttribute(String name)
/*      */   {
/* 2514 */     return getLongAttribute(name) != 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DownloadManagerState getDownloadState(DownloadManager dm)
/*      */   {
/* 2522 */     return new nullState(dm);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informWritten(final String attribute_name)
/*      */   {
/* 2532 */     for (DownloadManagerStateListener l : this.listeners_cow.getList()) {
/*      */       try
/*      */       {
/* 2535 */         l.stateChanged(this, new DownloadManagerStateEvent()
/*      */         {
/*      */ 
/*      */ 
/*      */           public int getType()
/*      */           {
/*      */ 
/* 2542 */             return 1;
/*      */           }
/*      */           
/*      */ 
/*      */           public Object getData()
/*      */           {
/* 2548 */             return attribute_name;
/*      */           }
/*      */         });
/*      */       }
/*      */       catch (Throwable e) {
/* 2553 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 2557 */     CopyOnWriteList<DownloadManagerStateAttributeListener> write_listeners = (CopyOnWriteList)global_listeners_write_map_cow.get(attribute_name);
/*      */     
/* 2559 */     if (write_listeners != null)
/*      */     {
/* 2561 */       for (DownloadManagerStateAttributeListener l : write_listeners.getList())
/*      */       {
/*      */         try
/*      */         {
/* 2565 */           l.attributeEventOccurred(this.download_manager, attribute_name, 1);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/* 2569 */           Debug.printStackTrace(t);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2574 */     write_listeners = (CopyOnWriteList)this.listeners_write_map_cow.get(attribute_name);
/*      */     
/* 2576 */     if (write_listeners != null)
/*      */     {
/* 2578 */       for (DownloadManagerStateAttributeListener l : write_listeners.getList())
/*      */       {
/*      */         try
/*      */         {
/* 2582 */           l.attributeEventOccurred(this.download_manager, attribute_name, 1);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/* 2586 */           Debug.printStackTrace(t);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informWillRead(final String attribute_name)
/*      */   {
/* 2599 */     List will_be_read_list = (List)tls_wbr.get();
/*      */     
/* 2601 */     if (!will_be_read_list.contains(attribute_name))
/*      */     {
/* 2603 */       will_be_read_list.add(attribute_name);
/*      */       
/*      */       try
/*      */       {
/* 2607 */         for (DownloadManagerStateListener l : this.listeners_cow.getList()) {
/*      */           try
/*      */           {
/* 2610 */             l.stateChanged(this, new DownloadManagerStateEvent()
/*      */             {
/*      */ 
/*      */ 
/*      */               public int getType()
/*      */               {
/*      */ 
/* 2617 */                 return 2;
/*      */               }
/*      */               
/*      */ 
/*      */               public Object getData()
/*      */               {
/* 2623 */                 return attribute_name;
/*      */               }
/*      */             });
/*      */           }
/*      */           catch (Throwable e) {
/* 2628 */             Debug.printStackTrace(e);
/*      */           }
/*      */         }
/*      */         
/* 2632 */         CopyOnWriteList<DownloadManagerStateAttributeListener> read_listeners = (CopyOnWriteList)global_listeners_read_map_cow.get(attribute_name);
/*      */         
/* 2634 */         if (read_listeners != null)
/*      */         {
/* 2636 */           for (DownloadManagerStateAttributeListener l : read_listeners.getList()) {
/*      */             try
/*      */             {
/* 2639 */               l.attributeEventOccurred(this.download_manager, attribute_name, 2);
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/* 2643 */               Debug.printStackTrace(t);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2648 */         read_listeners = (CopyOnWriteList)this.listeners_read_map_cow.get(attribute_name);
/*      */         
/* 2650 */         if (read_listeners != null)
/*      */         {
/* 2652 */           for (DownloadManagerStateAttributeListener l : read_listeners.getList()) {
/*      */             try
/*      */             {
/* 2655 */               l.attributeEventOccurred(this.download_manager, attribute_name, 2);
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/* 2659 */               Debug.printStackTrace(t);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 2665 */         will_be_read_list.remove(attribute_name);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DownloadManagerStateListener l)
/*      */   {
/* 2674 */     this.listeners_cow.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DownloadManagerStateListener l)
/*      */   {
/* 2681 */     this.listeners_cow.remove(l);
/*      */   }
/*      */   
/*      */   public void addListener(DownloadManagerStateAttributeListener l, String attribute, int event_type) {
/* 2685 */     CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> map_to_use = event_type == 2 ? this.listeners_read_map_cow : this.listeners_write_map_cow;
/* 2686 */     CopyOnWriteList<DownloadManagerStateAttributeListener> lst = (CopyOnWriteList)map_to_use.get(attribute);
/* 2687 */     if (lst == null) {
/* 2688 */       lst = new CopyOnWriteList();
/* 2689 */       map_to_use.put(attribute, lst);
/*      */     }
/* 2691 */     lst.add(l);
/*      */   }
/*      */   
/*      */   public void removeListener(DownloadManagerStateAttributeListener l, String attribute, int event_type) {
/* 2695 */     CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> map_to_use = event_type == 2 ? this.listeners_read_map_cow : this.listeners_write_map_cow;
/* 2696 */     CopyOnWriteList<DownloadManagerStateAttributeListener> lst = (CopyOnWriteList)map_to_use.get(attribute);
/* 2697 */     if (lst != null) { lst.remove(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void addGlobalListener(DownloadManagerStateAttributeListener l, String attribute, int event_type)
/*      */   {
/* 2704 */     CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> map_to_use = event_type == 2 ? global_listeners_read_map_cow : global_listeners_write_map_cow;
/* 2705 */     CopyOnWriteList<DownloadManagerStateAttributeListener> lst = (CopyOnWriteList)map_to_use.get(attribute);
/* 2706 */     if (lst == null) {
/* 2707 */       lst = new CopyOnWriteList();
/* 2708 */       map_to_use.put(attribute, lst);
/*      */     }
/* 2710 */     lst.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeGlobalListener(DownloadManagerStateAttributeListener l, String attribute, int event_type)
/*      */   {
/* 2717 */     CopyOnWriteMap<String, CopyOnWriteList<DownloadManagerStateAttributeListener>> map_to_use = event_type == 2 ? global_listeners_read_map_cow : global_listeners_write_map_cow;
/* 2718 */     CopyOnWriteList<DownloadManagerStateAttributeListener> lst = (CopyOnWriteList)map_to_use.get(attribute);
/* 2719 */     if (lst != null) { lst.remove(l);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 2726 */     writer.println("DownloadManagerState");
/*      */     try
/*      */     {
/* 2729 */       writer.indent();
/*      */       
/* 2731 */       writer.println("parameters=" + this.parameters);
/* 2732 */       writer.println("flags=" + getFlags());
/* 2733 */       DiskManagerFileInfo primaryFile = getPrimaryFile();
/* 2734 */       if (primaryFile != null) {
/* 2735 */         writer.println("primary file=" + Debug.secretFileName(primaryFile.getFile(true).getAbsolutePath()));
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2740 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void dump(IndentWriter writer)
/*      */   {
/* 2749 */     writer.println("attributes: " + this.parameters);
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   protected String getStringAttribute(String attribute_name)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: aload_1
/*      */     //   2: invokevirtual 1214	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:informWillRead	(Ljava/lang/String;)V
/*      */     //   5: aload_0
/*      */     //   6: getfield 1107	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   9: invokevirtual 1257	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */     //   12: aload_0
/*      */     //   13: getfield 1096	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:attributes	Ljava/util/Map;
/*      */     //   16: aload_1
/*      */     //   17: invokeinterface 1307 2 0
/*      */     //   22: instanceof 616
/*      */     //   25: ifne +14 -> 39
/*      */     //   28: aconst_null
/*      */     //   29: astore_2
/*      */     //   30: aload_0
/*      */     //   31: getfield 1107	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   34: invokevirtual 1258	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   37: aload_2
/*      */     //   38: areturn
/*      */     //   39: aload_0
/*      */     //   40: getfield 1096	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:attributes	Ljava/util/Map;
/*      */     //   43: aload_1
/*      */     //   44: invokeinterface 1307 2 0
/*      */     //   49: checkcast 616	[B
/*      */     //   52: checkcast 616	[B
/*      */     //   55: astore_2
/*      */     //   56: aload_2
/*      */     //   57: ifnonnull +14 -> 71
/*      */     //   60: aconst_null
/*      */     //   61: astore_3
/*      */     //   62: aload_0
/*      */     //   63: getfield 1107	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   66: invokevirtual 1258	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   69: aload_3
/*      */     //   70: areturn
/*      */     //   71: new 635	java/lang/String
/*      */     //   74: dup
/*      */     //   75: aload_2
/*      */     //   76: ldc_w 598
/*      */     //   79: invokespecial 1159	java/lang/String:<init>	([BLjava/lang/String;)V
/*      */     //   82: astore_3
/*      */     //   83: aload_0
/*      */     //   84: getfield 1107	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   87: invokevirtual 1258	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   90: aload_3
/*      */     //   91: areturn
/*      */     //   92: astore_3
/*      */     //   93: aload_3
/*      */     //   94: invokestatic 1267	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */     //   97: aconst_null
/*      */     //   98: astore 4
/*      */     //   100: aload_0
/*      */     //   101: getfield 1107	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   104: invokevirtual 1258	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   107: aload 4
/*      */     //   109: areturn
/*      */     //   110: astore 5
/*      */     //   112: aload_0
/*      */     //   113: getfield 1107	org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */     //   116: invokevirtual 1258	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */     //   119: aload 5
/*      */     //   121: athrow
/*      */     // Line number table:
/*      */     //   Java source line #2021	-> byte code offset #0
/*      */     //   Java source line #2024	-> byte code offset #5
/*      */     //   Java source line #2026	-> byte code offset #12
/*      */     //   Java source line #2028	-> byte code offset #28
/*      */     //   Java source line #2049	-> byte code offset #30
/*      */     //   Java source line #2031	-> byte code offset #39
/*      */     //   Java source line #2033	-> byte code offset #56
/*      */     //   Java source line #2035	-> byte code offset #60
/*      */     //   Java source line #2049	-> byte code offset #62
/*      */     //   Java source line #2039	-> byte code offset #71
/*      */     //   Java source line #2049	-> byte code offset #83
/*      */     //   Java source line #2041	-> byte code offset #92
/*      */     //   Java source line #2043	-> byte code offset #93
/*      */     //   Java source line #2045	-> byte code offset #97
/*      */     //   Java source line #2049	-> byte code offset #100
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	122	0	this	DownloadManagerStateImpl
/*      */     //   0	122	1	attribute_name	String
/*      */     //   29	9	2	str1	String
/*      */     //   55	21	2	bytes	byte[]
/*      */     //   61	30	3	str2	String
/*      */     //   92	2	3	e	UnsupportedEncodingException
/*      */     //   98	10	4	str3	String
/*      */     //   110	10	5	localObject	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   71	83	92	java/io/UnsupportedEncodingException
/*      */     //   5	30	110	finally
/*      */     //   39	62	110	finally
/*      */     //   71	83	110	finally
/*      */     //   92	100	110	finally
/*      */     //   110	112	110	finally
/*      */   }
/*      */   
/*      */   protected static class nullState
/*      */     implements DownloadManagerState
/*      */   {
/*      */     protected final DownloadManager download_manager;
/*      */     
/*      */     protected nullState(DownloadManager _dm)
/*      */     {
/* 2763 */       this.download_manager = _dm;
/*      */     }
/*      */     
/*      */ 
/*      */     public TOTorrent getTorrent()
/*      */     {
/* 2769 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public File getStateFile()
/*      */     {
/* 2775 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public DownloadManager getDownloadManager()
/*      */     {
/* 2781 */       return this.download_manager;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void clearResumeData() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public Map getResumeData()
/*      */     {
/* 2792 */       return new HashMap();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setResumeData(Map data) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean isResumeDataComplete()
/*      */     {
/* 2804 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void clearTrackerResponseCache() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public Map getTrackerResponseCache()
/*      */     {
/* 2815 */       return new HashMap();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setTrackerResponseCache(Map value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setFlag(long flag, boolean set) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean getFlag(long flag)
/*      */     {
/* 2835 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getFlags()
/*      */     {
/* 2841 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setParameterDefault(String name) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public long getLongParameter(String name)
/*      */     {
/* 2854 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setLongParameter(String name, long value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getIntParameter(String name)
/*      */     {
/* 2868 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setIntParameter(String name, int value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public boolean getBooleanParameter(String name)
/*      */     {
/* 2882 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setBooleanParameter(String name, boolean value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAttribute(String name, String value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getAttribute(String name)
/*      */     {
/* 2903 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public String getTrackerClientExtensions()
/*      */     {
/* 2909 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setTrackerClientExtensions(String value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setListAttribute(String name, String[] values) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String getListAttribute(String name, int idx)
/*      */     {
/* 2927 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String[] getListAttribute(String name)
/*      */     {
/* 2934 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setMapAttribute(String name, Map value) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public Map getMapAttribute(String name)
/*      */     {
/* 2948 */       return null;
/*      */     }
/*      */     
/* 2951 */     public boolean hasAttribute(String name) { return false; }
/* 2952 */     public int getIntAttribute(String name) { return 0; }
/* 2953 */     public long getLongAttribute(String name) { return 0L; }
/* 2954 */     public boolean getBooleanAttribute(String name) { return false; }
/*      */     
/*      */     public void setIntAttribute(String name, int value) {}
/*      */     
/*      */     public void setLongAttribute(String name, long value) {}
/*      */     
/*      */     public void setBooleanAttribute(String name, boolean value) {}
/*      */     
/* 2962 */     public Category getCategory() { return null; }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setCategory(Category cat) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String[] getNetworks()
/*      */     {
/* 2974 */       return new String[0];
/*      */     }
/*      */     
/*      */     public boolean isNetworkEnabled(String network)
/*      */     {
/* 2979 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setNetworks(String[] networks) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setNetworkEnabled(String network, boolean enabled) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public String[] getPeerSources()
/*      */     {
/* 2997 */       return new String[0];
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isPeerSourcePermitted(String peerSource)
/*      */     {
/* 3003 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setPeerSourcePermitted(String peerSource, boolean permitted) {}
/*      */     
/*      */ 
/*      */     public boolean isPeerSourceEnabled(String peerSource)
/*      */     {
/* 3012 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void suppressStateSave(boolean suppress) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setPeerSources(String[] networks) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setPeerSourceEnabled(String source, boolean enabled) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setFileLink(int source_index, File link_source, File link_destination) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setFileLinks(List<Integer> source_indexes, List<File> link_sources, List<File> link_destinations) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void clearFileLinks() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public File getFileLink(int source_index, File link_source)
/*      */     {
/* 3056 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public LinkFileMap getFileLinks()
/*      */     {
/* 3062 */       return new LinkFileMap();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setActive(boolean active) {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void discardFluff() {}
/*      */     
/*      */ 
/*      */     public boolean exportState(File target_dir)
/*      */     {
/* 3076 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void save() {}
/*      */     
/*      */ 
/*      */ 
/*      */     public void delete() {}
/*      */     
/*      */ 
/*      */     public void addListener(DownloadManagerStateListener l) {}
/*      */     
/*      */ 
/*      */     public void removeListener(DownloadManagerStateListener l) {}
/*      */     
/*      */ 
/*      */     public void addListener(DownloadManagerStateAttributeListener l, String attribute, int event_type) {}
/*      */     
/*      */ 
/*      */     public void removeListener(DownloadManagerStateAttributeListener l, String attribute, int event_type) {}
/*      */     
/*      */ 
/*      */     public void setDisplayName(String name) {}
/*      */     
/*      */ 
/* 3103 */     public String getDisplayName() { return null; }
/*      */     
/*      */     public void setUserComment(String name) {}
/* 3106 */     public String getUserComment() { return null; }
/*      */     
/*      */     public void setRelativeSavePath(String name) {}
/* 3109 */     public String getRelativeSavePath() { return null; }
/*      */     
/*      */     public boolean parameterExists(String name)
/*      */     {
/* 3113 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void generateEvidence(IndentWriter writer)
/*      */     {
/* 3120 */       writer.println("DownloadManagerState: broken torrent");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void dump(IndentWriter writer) {}
/*      */     
/*      */ 
/*      */     public boolean isOurContent()
/*      */     {
/* 3130 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public DiskManagerFileInfo getPrimaryFile()
/*      */     {
/* 3136 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public void setPrimaryFile(DiskManagerFileInfo dmfi) {}
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class CachedStateWrapper
/*      */     extends org.gudy.azureus2.core3.logging.LogRelation
/*      */     implements TorrentUtils.ExtendedTorrent
/*      */   {
/*      */     private final DownloadManagerImpl download_manager;
/*      */     
/*      */     private final String torrent_file;
/*      */     
/*      */     private HashWrapper torrent_hash_wrapper;
/*      */     
/*      */     private Map cache;
/*      */     
/*      */     private Map cache_attributes;
/*      */     
/*      */     private Map cache_azp;
/*      */     
/*      */     private volatile TorrentUtils.ExtendedTorrent delegate;
/*      */     
/*      */     private TOTorrentException fixup_failure;
/*      */     
/*      */     private boolean discard_pieces;
/*      */     
/*      */     private boolean logged_failure;
/*      */     
/*      */     private Boolean simple_torrent;
/*      */     
/*      */     private long size;
/*      */     
/*      */     private int file_count;
/*      */     
/*      */     private URL announce_url;
/*      */     
/*      */     private cacheGroup announce_group;
/*      */     
/*      */     private volatile boolean discard_fluff;
/*      */     
/*      */     protected CachedStateWrapper(DownloadManagerImpl _download_manager, String _torrent_file, byte[] _torrent_hash, Map _cache, boolean _force_piece_discard)
/*      */     {
/* 3182 */       this.download_manager = _download_manager;
/* 3183 */       this.torrent_file = _torrent_file;
/* 3184 */       this.torrent_hash_wrapper = new HashWrapper(_torrent_hash);
/* 3185 */       this.cache = _cache;
/*      */       
/* 3187 */       this.cache_attributes = ((Map)this.cache.get("attributes"));
/* 3188 */       this.cache_azp = ((Map)this.cache.get("azp"));
/*      */       
/* 3190 */       if (_force_piece_discard)
/*      */       {
/* 3192 */         this.discard_pieces = true;
/*      */       }
/*      */       else
/*      */       {
/* 3196 */         Long l_fp = (Long)this.cache.get("dp");
/*      */         
/* 3198 */         if (l_fp != null)
/*      */         {
/* 3200 */           this.discard_pieces = (l_fp.longValue() == 1L);
/*      */         }
/*      */       }
/*      */       
/* 3204 */       Long st = (Long)this.cache.get("simple");
/*      */       
/* 3206 */       if (st != null)
/*      */       {
/* 3208 */         this.simple_torrent = Boolean.valueOf(st.longValue() == 1L);
/*      */       }
/*      */       
/* 3211 */       Long fc = (Long)this.cache.get("fc");
/*      */       
/* 3213 */       if (fc != null)
/*      */       {
/* 3215 */         this.file_count = fc.intValue();
/*      */       }
/*      */       
/* 3218 */       Long l_size = (Long)this.cache.get("size");
/*      */       
/* 3220 */       if (l_size != null)
/*      */       {
/* 3222 */         this.size = l_size.longValue();
/*      */       }
/*      */       
/* 3225 */       byte[] au = (byte[])this.cache.get("au");
/*      */       
/* 3227 */       if (au != null) {
/*      */         try
/*      */         {
/* 3230 */           this.announce_url = StringInterner.internURL(new URL(new String(au, "UTF-8")));
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3237 */       List ag = (List)this.cache.get("ag");
/*      */       
/* 3239 */       if (ag != null) {
/*      */         try
/*      */         {
/* 3242 */           this.announce_group = importGroup(ag);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected static Map export(DownloadManagerState dms)
/*      */       throws TOTorrentException
/*      */     {
/* 3256 */       Map cache = new HashMap();
/*      */       
/* 3258 */       TOTorrent state = dms.getTorrent();
/*      */       
/* 3260 */       cache.put("hash", state.getHash());
/* 3261 */       cache.put("name", state.getName());
/* 3262 */       cache.put("utf8name", state.getUTF8Name() == null ? "" : state.getUTF8Name());
/* 3263 */       cache.put("comment", state.getComment());
/* 3264 */       cache.put("createdby", state.getCreatedBy());
/* 3265 */       cache.put("size", new Long(state.getSize()));
/*      */       
/* 3267 */       cache.put("encoding", state.getAdditionalStringProperty("encoding"));
/* 3268 */       cache.put("torrent filename", state.getAdditionalStringProperty("torrent filename"));
/*      */       
/* 3270 */       cache.put("attributes", state.getAdditionalMapProperty("attributes"));
/* 3271 */       cache.put("azp", state.getAdditionalMapProperty("azureus_properties"));
/*      */       try
/*      */       {
/* 3274 */         cache.put("au", state.getAnnounceURL().toExternalForm());
/* 3275 */         cache.put("ag", exportGroup(state.getAnnounceURLGroup()));
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/*      */ 
/* 3280 */       boolean discard_pieces = dms.isResumeDataComplete();
/*      */       
/* 3282 */       TOTorrent t = dms.getTorrent();
/*      */       
/* 3284 */       if ((t instanceof CachedStateWrapper))
/*      */       {
/*      */ 
/*      */ 
/* 3288 */         CachedStateWrapper csw = (CachedStateWrapper)t;
/*      */         
/* 3290 */         if (!discard_pieces)
/*      */         {
/*      */ 
/*      */ 
/* 3294 */           discard_pieces = csw.peekPieces() == null;
/*      */         }
/*      */         
/* 3297 */         Boolean simple_torrent = csw.simple_torrent;
/*      */         
/* 3299 */         if (simple_torrent != null)
/*      */         {
/* 3301 */           cache.put("simple", new Long(simple_torrent.booleanValue() ? 1L : 0L));
/*      */         }
/*      */         else
/*      */         {
/* 3305 */           Debug.out("Failed to cache simple state");
/*      */         }
/*      */         
/* 3308 */         int fc = csw.file_count;
/*      */         
/* 3310 */         if (fc > 0)
/*      */         {
/* 3312 */           cache.put("fc", new Long(fc));
/*      */         }
/*      */       }
/* 3315 */       else if ((t instanceof TorrentUtils.torrentDelegate))
/*      */       {
/*      */ 
/*      */ 
/* 3319 */         cache.put("simple", new Long(t.isSimpleTorrent() ? 1L : 0L));
/*      */         
/* 3321 */         cache.put("fc", Integer.valueOf(t.getFileCount()));
/*      */       }
/*      */       else
/*      */       {
/* 3325 */         Debug.out("Hmm, torrent isn't cache-state-wrapper, it is " + t);
/*      */       }
/*      */       
/*      */ 
/* 3329 */       cache.put("dp", new Long(discard_pieces ? 1L : 0L));
/*      */       
/* 3331 */       return cache;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected static List exportGroup(TOTorrentAnnounceURLGroup group)
/*      */     {
/* 3338 */       TOTorrentAnnounceURLSet[] sets = group.getAnnounceURLSets();
/*      */       
/* 3340 */       List result = new ArrayList();
/*      */       
/* 3342 */       for (int i = 0; i < sets.length; i++)
/*      */       {
/* 3344 */         TOTorrentAnnounceURLSet set = sets[i];
/*      */         
/* 3346 */         URL[] urls = set.getAnnounceURLs();
/*      */         
/* 3348 */         if (urls.length > 0)
/*      */         {
/* 3350 */           List s = new ArrayList(urls.length);
/*      */           
/* 3352 */           for (int j = 0; j < urls.length; j++)
/*      */           {
/* 3354 */             s.add(urls[j].toExternalForm());
/*      */           }
/*      */           
/* 3357 */           result.add(s);
/*      */         }
/*      */       }
/*      */       
/* 3361 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     protected cacheGroup importGroup(List l)
/*      */       throws Exception
/*      */     {
/* 3370 */       return new cacheGroup(l);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected class cacheGroup
/*      */       implements TOTorrentAnnounceURLGroup
/*      */     {
/*      */       private TOTorrentAnnounceURLSet[] sets;
/*      */       
/*      */ 
/*      */ 
/*      */       protected cacheGroup(List group)
/*      */         throws Exception
/*      */       {
/* 3385 */         this.sets = new TOTorrentAnnounceURLSet[group.size()];
/*      */         
/* 3387 */         for (int i = 0; i < this.sets.length; i++)
/*      */         {
/* 3389 */           List set = (List)group.get(i);
/*      */           
/* 3391 */           URL[] urls = new URL[set.size()];
/*      */           
/* 3393 */           for (int j = 0; j < urls.length; j++)
/*      */           {
/* 3395 */             urls[j] = StringInterner.internURL(new URL(new String((byte[])(byte[])set.get(j), "UTF-8")));
/*      */           }
/*      */           
/* 3398 */           this.sets[i] = new cacheSet(urls);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public TOTorrentAnnounceURLSet[] getAnnounceURLSets()
/*      */       {
/* 3405 */         if ((DownloadManagerStateImpl.CachedStateWrapper.this.announce_group == null) && (DownloadManagerStateImpl.CachedStateWrapper.this.fixup()))
/*      */         {
/* 3407 */           return DownloadManagerStateImpl.CachedStateWrapper.this.delegate.getAnnounceURLGroup().getAnnounceURLSets();
/*      */         }
/*      */         
/* 3410 */         return this.sets;
/*      */       }
/*      */       
/*      */       void fixGroup()
/*      */       {
/* 3415 */         TOTorrentAnnounceURLSet[] realSets = DownloadManagerStateImpl.CachedStateWrapper.this.delegate.getAnnounceURLGroup().getAnnounceURLSets();
/*      */         
/* 3417 */         if (realSets.length == this.sets.length)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3426 */           for (int i = 0; i < realSets.length; i++)
/*      */           {
/* 3428 */             if ((this.sets[i] instanceof cacheSet))
/*      */             {
/* 3430 */               ((cacheSet)this.sets[i]).delegateSet = realSets[i];
/*      */             }
/*      */           }
/*      */           
/* 3434 */           this.sets = null;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void setAnnounceURLSets(TOTorrentAnnounceURLSet[] toSet)
/*      */       {
/* 3442 */         if (DownloadManagerStateImpl.CachedStateWrapper.this.fixup())
/*      */         {
/* 3444 */           TOTorrentAnnounceURLSet[] modToSet = new TOTorrentAnnounceURLSet[toSet.length];
/*      */           
/* 3446 */           for (int i = 0; i < toSet.length; i++)
/*      */           {
/* 3448 */             TOTorrentAnnounceURLSet set = toSet[i];
/*      */             
/* 3450 */             if ((set instanceof cacheSet))
/*      */             {
/* 3452 */               modToSet[i] = ((cacheSet)set).delegateSet;
/*      */             }
/*      */             
/* 3455 */             if (modToSet[i] == null)
/*      */             {
/* 3457 */               modToSet[i] = set;
/*      */             }
/*      */           }
/*      */           
/* 3461 */           DownloadManagerStateImpl.CachedStateWrapper.this.delegate.getAnnounceURLGroup().setAnnounceURLSets(modToSet);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public TOTorrentAnnounceURLSet createAnnounceURLSet(URL[] urls)
/*      */       {
/* 3469 */         if (DownloadManagerStateImpl.CachedStateWrapper.this.fixup())
/*      */         {
/* 3471 */           return DownloadManagerStateImpl.CachedStateWrapper.this.delegate.getAnnounceURLGroup().createAnnounceURLSet(urls);
/*      */         }
/*      */         
/* 3474 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       protected class cacheSet
/*      */         implements TOTorrentAnnounceURLSet
/*      */       {
/*      */         private URL[] urls;
/*      */         private TOTorrentAnnounceURLSet delegateSet;
/*      */         
/*      */         public cacheSet(URL[] urls)
/*      */         {
/* 3486 */           this.urls = urls;
/*      */         }
/*      */         
/*      */ 
/*      */         public URL[] getAnnounceURLs()
/*      */         {
/* 3492 */           if ((DownloadManagerStateImpl.CachedStateWrapper.this.announce_group == null) && (DownloadManagerStateImpl.CachedStateWrapper.this.fixup()) && (this.delegateSet != null))
/*      */           {
/* 3494 */             return this.delegateSet.getAnnounceURLs();
/*      */           }
/*      */           
/* 3497 */           return this.urls;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void setAnnounceURLs(URL[] toSet)
/*      */         {
/* 3504 */           if ((DownloadManagerStateImpl.CachedStateWrapper.this.fixup()) && (this.delegateSet != null))
/*      */           {
/* 3506 */             this.delegateSet.setAnnounceURLs(toSet);
/*      */           }
/*      */           else
/*      */           {
/* 3510 */             this.urls = toSet;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void clearCache()
/*      */     {
/* 3519 */       this.cache = null;
/*      */     }
/*      */     
/*      */     protected boolean fixup()
/*      */     {
/*      */       try
/*      */       {
/* 3526 */         if (this.delegate == null)
/*      */         {
/* 3528 */           synchronized (this)
/*      */           {
/* 3530 */             if (this.delegate == null)
/*      */             {
/*      */ 
/*      */ 
/* 3534 */               if (this.fixup_failure != null)
/*      */               {
/* 3536 */                 throw this.fixup_failure;
/*      */               }
/*      */               
/* 3539 */               this.delegate = loadRealState();
/*      */               
/* 3541 */               if (this.discard_fluff)
/*      */               {
/* 3543 */                 this.delegate.setDiscardFluff(this.discard_fluff);
/*      */               }
/*      */               
/* 3546 */               if (this.cache != null)
/*      */               {
/* 3548 */                 Debug.out("Cache miss forced fixup");
/*      */               }
/*      */               
/* 3551 */               this.cache = null;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3557 */               if (this.cache_attributes != null)
/*      */               {
/* 3559 */                 this.delegate.setAdditionalMapProperty("attributes", this.cache_attributes);
/*      */                 
/* 3561 */                 this.cache_attributes = null;
/*      */               }
/*      */               
/* 3564 */               if (this.cache_azp != null)
/*      */               {
/* 3566 */                 this.delegate.setAdditionalMapProperty("azureus_properties", this.cache_azp);
/*      */                 
/* 3568 */                 this.cache_azp = null;
/*      */               }
/*      */               
/* 3571 */               this.announce_url = null;
/*      */               
/* 3573 */               if (this.announce_group != null)
/*      */               {
/* 3575 */                 this.announce_group.fixGroup();
/*      */                 
/* 3577 */                 this.announce_group = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 3583 */         return true;
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 3587 */         this.fixup_failure = e;
/*      */         
/* 3589 */         if (this.download_manager != null)
/*      */         {
/* 3591 */           this.download_manager.setTorrentInvalid(Debug.getNestedExceptionMessage(e));
/*      */ 
/*      */ 
/*      */         }
/* 3595 */         else if (!this.logged_failure)
/*      */         {
/* 3597 */           this.logged_failure = true;
/*      */           
/* 3599 */           Debug.out("Torrent can't be loaded: " + Debug.getNestedExceptionMessage(e));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 3604 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected TorrentUtils.ExtendedTorrent loadRealState()
/*      */       throws TOTorrentException
/*      */     {
/* 3614 */       if ((!DownloadManagerStateImpl.SUPPRESS_FIXUP_ERRORS) && (org.gudy.azureus2.core3.util.Constants.isCVSVersion()))
/*      */       {
/* 3616 */         if (!Thread.currentThread().isDaemon())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3622 */           Debug.outNoStack(Debug.getCompressedStackTrace(new Exception() { public String toString() { return "Premature fixup?"; } }, 2, 10, true), true);
/*      */         }
/*      */       }
/*      */       
/* 3626 */       File saved_file = DownloadManagerStateImpl.getStateFile(this.torrent_hash_wrapper.getBytes());
/*      */       
/* 3628 */       if (saved_file.exists())
/*      */       {
/*      */         try
/*      */         {
/* 3632 */           return TorrentUtils.readDelegateFromFile(saved_file, this.discard_pieces);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3636 */           Debug.out("Failed to load download state for " + saved_file);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3642 */       TOTorrent original_torrent = TorrentUtils.readFromFile(new File(this.torrent_file), true);
/*      */       
/* 3644 */       this.torrent_hash_wrapper = original_torrent.getHashWrapper();
/*      */       
/* 3646 */       saved_file = DownloadManagerStateImpl.getStateFile(this.torrent_hash_wrapper.getBytes());
/*      */       
/* 3648 */       if (saved_file.exists()) {
/*      */         try
/*      */         {
/* 3651 */           return TorrentUtils.readDelegateFromFile(saved_file, this.discard_pieces);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3655 */           Debug.out("Failed to load download state for " + saved_file);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3664 */       TorrentUtils.copyToFile(original_torrent, saved_file);
/*      */       
/* 3666 */       return TorrentUtils.readDelegateFromFile(saved_file, this.discard_pieces);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getName()
/*      */     {
/* 3673 */       Map c = this.cache;
/*      */       
/* 3675 */       if (c != null)
/*      */       {
/* 3677 */         byte[] name = (byte[])c.get("name");
/* 3678 */         if (name != null) {
/* 3679 */           return name;
/*      */         }
/*      */       }
/*      */       
/* 3683 */       if (fixup())
/*      */       {
/* 3685 */         return this.delegate.getName();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3690 */       return ("Error - " + Debug.getNestedExceptionMessage(this.fixup_failure)).getBytes();
/*      */     }
/*      */     
/*      */     public String getUTF8Name() {
/* 3694 */       Map c = this.cache;
/*      */       
/* 3696 */       if (c != null)
/*      */       {
/* 3698 */         byte[] name = (byte[])c.get("utf8name");
/* 3699 */         if (name != null) {
/*      */           String utf8name;
/*      */           try {
/* 3702 */             utf8name = new String(name, "utf8");
/*      */           } catch (UnsupportedEncodingException e) {
/* 3704 */             return null;
/*      */           }
/* 3706 */           if (utf8name.length() == 0) {
/* 3707 */             return null;
/*      */           }
/* 3709 */           return utf8name;
/*      */         }
/*      */       }
/*      */       
/* 3713 */       if (fixup()) {
/* 3714 */         return this.delegate.getUTF8Name();
/*      */       }
/* 3716 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isSimpleTorrent()
/*      */     {
/* 3722 */       if (this.simple_torrent != null)
/*      */       {
/* 3724 */         return this.simple_torrent.booleanValue();
/*      */       }
/*      */       
/* 3727 */       if (fixup())
/*      */       {
/* 3729 */         boolean st = this.delegate.isSimpleTorrent();
/*      */         
/* 3731 */         this.simple_torrent = Boolean.valueOf(st);
/*      */         
/* 3733 */         return st;
/*      */       }
/*      */       
/* 3736 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getComment()
/*      */     {
/* 3742 */       Map c = this.cache;
/*      */       
/* 3744 */       if (c != null)
/*      */       {
/* 3746 */         return (byte[])c.get("comment");
/*      */       }
/*      */       
/* 3749 */       if (fixup())
/*      */       {
/* 3751 */         return this.delegate.getComment();
/*      */       }
/*      */       
/* 3754 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setComment(String comment)
/*      */     {
/* 3761 */       if (fixup())
/*      */       {
/* 3763 */         this.delegate.setComment(comment);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCreationDate()
/*      */     {
/* 3770 */       if (fixup())
/*      */       {
/* 3772 */         return this.delegate.getCreationDate();
/*      */       }
/*      */       
/* 3775 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setCreationDate(long date)
/*      */     {
/* 3782 */       if (fixup())
/*      */       {
/* 3784 */         this.delegate.setCreationDate(date);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public byte[] getCreatedBy()
/*      */     {
/* 3791 */       Map c = this.cache;
/*      */       
/* 3793 */       if (c != null)
/*      */       {
/* 3795 */         return (byte[])c.get("createdby");
/*      */       }
/*      */       
/* 3798 */       if (fixup())
/*      */       {
/* 3800 */         return this.delegate.getCreatedBy();
/*      */       }
/*      */       
/* 3803 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setCreatedBy(byte[] cb)
/*      */     {
/* 3810 */       if (fixup())
/*      */       {
/* 3812 */         this.delegate.setCreatedBy(cb);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isCreated()
/*      */     {
/* 3819 */       if (fixup())
/*      */       {
/* 3821 */         return this.delegate.isCreated();
/*      */       }
/*      */       
/* 3824 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isDecentralised()
/*      */     {
/* 3830 */       return TorrentUtils.isDecentralised(getAnnounceURL());
/*      */     }
/*      */     
/*      */ 
/*      */     public URL getAnnounceURL()
/*      */     {
/* 3836 */       if (this.announce_url != null)
/*      */       {
/* 3838 */         return this.announce_url;
/*      */       }
/*      */       
/* 3841 */       if (fixup())
/*      */       {
/* 3843 */         return this.delegate.getAnnounceURL();
/*      */       }
/*      */       
/* 3846 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean setAnnounceURL(URL url)
/*      */     {
/* 3853 */       if (this.announce_url != null)
/*      */       {
/* 3855 */         if (this.announce_url.toExternalForm().equals(url.toExternalForm()))
/*      */         {
/* 3857 */           return false;
/*      */         }
/*      */       }
/*      */       
/* 3861 */       if (fixup())
/*      */       {
/* 3863 */         return this.delegate.setAnnounceURL(url);
/*      */       }
/*      */       
/*      */ 
/* 3867 */       this.announce_url = url;
/*      */       
/*      */ 
/* 3870 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */     public TOTorrentAnnounceURLGroup getAnnounceURLGroup()
/*      */     {
/* 3876 */       if (this.announce_group != null)
/*      */       {
/* 3878 */         return this.announce_group;
/*      */       }
/*      */       
/* 3881 */       if (fixup())
/*      */       {
/* 3883 */         return this.delegate.getAnnounceURLGroup();
/*      */       }
/*      */       
/* 3886 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[][] getPieces()
/*      */       throws TOTorrentException
/*      */     {
/* 3894 */       if (fixup())
/*      */       {
/* 3896 */         return this.delegate.getPieces();
/*      */       }
/*      */       
/* 3899 */       throw this.fixup_failure;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setPieces(byte[][] pieces)
/*      */       throws TOTorrentException
/*      */     {
/* 3909 */       if (fixup())
/*      */       {
/* 3911 */         this.delegate.setPieces(pieces);
/*      */         
/* 3913 */         return;
/*      */       }
/*      */       
/* 3916 */       throw this.fixup_failure;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[][] peekPieces()
/*      */       throws TOTorrentException
/*      */     {
/* 3924 */       if (fixup())
/*      */       {
/* 3926 */         return this.delegate.peekPieces();
/*      */       }
/*      */       
/* 3929 */       throw this.fixup_failure;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setDiscardFluff(boolean discard)
/*      */     {
/* 3936 */       this.discard_fluff = discard;
/*      */       
/* 3938 */       if (this.delegate != null)
/*      */       {
/* 3940 */         this.delegate.setDiscardFluff(this.discard_fluff);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public long getPieceLength()
/*      */     {
/* 3947 */       if (fixup())
/*      */       {
/* 3949 */         return this.delegate.getPieceLength();
/*      */       }
/*      */       
/* 3952 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getNumberOfPieces()
/*      */     {
/* 3958 */       if (fixup())
/*      */       {
/* 3960 */         return this.delegate.getNumberOfPieces();
/*      */       }
/*      */       
/* 3963 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getSize()
/*      */     {
/* 3969 */       if (this.size > 0L)
/*      */       {
/* 3971 */         return this.size;
/*      */       }
/*      */       
/* 3974 */       if (fixup())
/*      */       {
/* 3976 */         this.size = this.delegate.getSize();
/*      */         
/* 3978 */         return this.size;
/*      */       }
/*      */       
/* 3981 */       return 0L;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getFileCount()
/*      */     {
/* 3987 */       if (this.file_count == 0)
/*      */       {
/* 3989 */         if (fixup())
/*      */         {
/* 3991 */           this.file_count = this.delegate.getFileCount();
/*      */         }
/*      */       }
/*      */       
/* 3995 */       return this.file_count;
/*      */     }
/*      */     
/*      */ 
/*      */     public TOTorrentFile[] getFiles()
/*      */     {
/* 4001 */       if (fixup())
/*      */       {
/* 4003 */         return this.delegate.getFiles();
/*      */       }
/*      */       
/* 4006 */       return new TOTorrentFile[0];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public byte[] getHash()
/*      */       throws TOTorrentException
/*      */     {
/* 4016 */       return this.torrent_hash_wrapper.getBytes();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public HashWrapper getHashWrapper()
/*      */       throws TOTorrentException
/*      */     {
/* 4024 */       return this.torrent_hash_wrapper;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setHashOverride(byte[] hash)
/*      */       throws TOTorrentException
/*      */     {
/* 4033 */       throw new TOTorrentException("Not supported", 8);
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean hasSameHashAs(TOTorrent other)
/*      */     {
/*      */       try
/*      */       {
/* 4041 */         byte[] other_hash = other.getHash();
/*      */         
/* 4043 */         return Arrays.equals(getHash(), other_hash);
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 4047 */         Debug.printStackTrace(e);
/*      */       }
/* 4049 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean getPrivate()
/*      */     {
/* 4056 */       if (fixup())
/*      */       {
/* 4058 */         return this.delegate.getPrivate();
/*      */       }
/*      */       
/* 4061 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setPrivate(boolean _private)
/*      */       throws TOTorrentException
/*      */     {
/* 4070 */       if (fixup())
/*      */       {
/* 4072 */         this.delegate.setPrivate(_private);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalStringProperty(String name, String value)
/*      */     {
/* 4081 */       if (fixup())
/*      */       {
/* 4083 */         this.delegate.setAdditionalStringProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getAdditionalStringProperty(String name)
/*      */     {
/* 4091 */       Map c = this.cache;
/*      */       
/* 4093 */       if ((c != null) && ((name.equals("encoding")) || (name.equals("torrent filename"))))
/*      */       {
/* 4095 */         byte[] res = (byte[])c.get(name);
/*      */         
/* 4097 */         if (res == null)
/*      */         {
/* 4099 */           return null;
/*      */         }
/*      */         try
/*      */         {
/* 4103 */           return new String(res, "UTF8");
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 4107 */           Debug.printStackTrace(e);
/*      */           
/* 4109 */           return null;
/*      */         }
/*      */       }
/*      */       
/* 4113 */       if (fixup())
/*      */       {
/* 4115 */         return this.delegate.getAdditionalStringProperty(name);
/*      */       }
/*      */       
/* 4118 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalByteArrayProperty(String name, byte[] value)
/*      */     {
/* 4126 */       if (fixup())
/*      */       {
/* 4128 */         this.delegate.setAdditionalByteArrayProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public byte[] getAdditionalByteArrayProperty(String name)
/*      */     {
/* 4136 */       if (fixup())
/*      */       {
/* 4138 */         return this.delegate.getAdditionalByteArrayProperty(name);
/*      */       }
/*      */       
/* 4141 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalLongProperty(String name, Long value)
/*      */     {
/* 4149 */       if (fixup())
/*      */       {
/* 4151 */         this.delegate.setAdditionalLongProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Long getAdditionalLongProperty(String name)
/*      */     {
/* 4159 */       if (fixup())
/*      */       {
/* 4161 */         return this.delegate.getAdditionalLongProperty(name);
/*      */       }
/*      */       
/* 4164 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalListProperty(String name, List value)
/*      */     {
/* 4173 */       if (fixup())
/*      */       {
/* 4175 */         this.delegate.setAdditionalListProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public List getAdditionalListProperty(String name)
/*      */     {
/* 4183 */       if (fixup())
/*      */       {
/* 4185 */         return this.delegate.getAdditionalListProperty(name);
/*      */       }
/*      */       
/* 4188 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalMapProperty(String name, Map value)
/*      */     {
/* 4196 */       if (fixup())
/*      */       {
/* 4198 */         this.delegate.setAdditionalMapProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Map getAdditionalMapProperty(String name)
/*      */     {
/* 4206 */       Map c = this.cache_attributes;
/*      */       
/* 4208 */       if ((c != null) && (name.equals("attributes")))
/*      */       {
/* 4210 */         return c;
/*      */       }
/*      */       
/* 4213 */       c = this.cache_azp;
/*      */       
/* 4215 */       if ((c != null) && (name.equals("azureus_properties")))
/*      */       {
/* 4217 */         return c;
/*      */       }
/*      */       
/* 4220 */       if (fixup())
/*      */       {
/* 4222 */         return this.delegate.getAdditionalMapProperty(name);
/*      */       }
/*      */       
/* 4225 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object getAdditionalProperty(String name)
/*      */     {
/* 4232 */       if (fixup())
/*      */       {
/* 4234 */         return this.delegate.getAdditionalProperty(name);
/*      */       }
/*      */       
/* 4237 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setAdditionalProperty(String name, Object value)
/*      */     {
/* 4245 */       if (fixup())
/*      */       {
/* 4247 */         this.delegate.setAdditionalProperty(name, value);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeAdditionalProperty(String name)
/*      */     {
/* 4255 */       if (fixup())
/*      */       {
/* 4257 */         this.delegate.removeAdditionalProperty(name);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void removeAdditionalProperties()
/*      */     {
/* 4264 */       if (fixup())
/*      */       {
/* 4266 */         this.delegate.removeAdditionalProperties();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void serialiseToBEncodedFile(File file)
/*      */       throws TOTorrentException
/*      */     {
/* 4276 */       if (fixup())
/*      */       {
/* 4278 */         this.delegate.serialiseToBEncodedFile(file);
/*      */         
/* 4280 */         return;
/*      */       }
/*      */       
/* 4283 */       throw this.fixup_failure;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Map serialiseToMap()
/*      */       throws TOTorrentException
/*      */     {
/* 4291 */       if (fixup())
/*      */       {
/* 4293 */         return this.delegate.serialiseToMap();
/*      */       }
/*      */       
/* 4296 */       throw this.fixup_failure;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void serialiseToXMLFile(File file)
/*      */       throws TOTorrentException
/*      */     {
/* 4305 */       if (fixup())
/*      */       {
/* 4307 */         this.delegate.serialiseToXMLFile(file);
/*      */         
/* 4309 */         return;
/*      */       }
/*      */       
/* 4312 */       throw this.fixup_failure;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void addListener(TOTorrentListener l)
/*      */     {
/* 4319 */       if (fixup())
/*      */       {
/* 4321 */         this.delegate.addListener(l);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void removeListener(TOTorrentListener l)
/*      */     {
/* 4329 */       if (fixup())
/*      */       {
/* 4331 */         this.delegate.removeListener(l);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public AEMonitor getMonitor()
/*      */     {
/* 4338 */       if (fixup())
/*      */       {
/* 4340 */         return this.delegate.getMonitor();
/*      */       }
/*      */       
/* 4343 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */     public void print()
/*      */     {
/* 4349 */       if (fixup())
/*      */       {
/* 4351 */         this.delegate.print();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getRelationText()
/*      */     {
/* 4359 */       return "Torrent: '" + new String(getName()) + "'";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public Object[] getQueryableInterfaces()
/*      */     {
/*      */       try
/*      */       {
/* 4368 */         return new Object[] { com.aelitis.azureus.core.AzureusCoreFactory.getSingleton().getGlobalManager().getDownloadManager(this) };
/*      */       }
/*      */       catch (Exception e) {}
/*      */       
/*      */ 
/* 4373 */       return null;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerStateImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */