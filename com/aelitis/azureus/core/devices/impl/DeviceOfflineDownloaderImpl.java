/*      */ package com.aelitis.azureus.core.devices.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.devices.DeviceManager;
/*      */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*      */ import com.aelitis.azureus.core.devices.DeviceOfflineDownload;
/*      */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloaderListener;
/*      */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloaderManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.util.DownloadUtils;
/*      */ import com.aelitis.net.upnp.UPnPDevice;
/*      */ import com.aelitis.net.upnp.UPnPException;
/*      */ import com.aelitis.net.upnp.UPnPRootDevice;
/*      */ import com.aelitis.net.upnp.UPnPService;
/*      */ import com.aelitis.net.upnp.services.UPnPOfflineDownloader;
/*      */ import java.net.InetAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Pattern;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FrequencyLimitedDispatcher;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ 
/*      */ public class DeviceOfflineDownloaderImpl extends DeviceUPnPImpl implements com.aelitis.azureus.core.devices.DeviceOfflineDownloader
/*      */ {
/*      */   public static final int UPDATE_MILLIS = 30000;
/*      */   public static final int UPDATE_TICKS = 6;
/*      */   public static final int UPDATE_SPACE_MILLIS = 180000;
/*      */   public static final int UPDATE_SPACE_TICKS = 36;
/*   68 */   public static final String client_id = ByteFormatter.encodeString(CryptoManagerFactory.getSingleton().getSecureID());
/*      */   
/*   70 */   private static final Object ERROR_KEY_OD = new Object();
/*      */   
/*      */   private volatile UPnPOfflineDownloader service;
/*      */   
/*      */   private volatile String service_ip;
/*      */   private volatile String manufacturer;
/*   76 */   private long start_time = SystemTime.getMonotonousTime();
/*      */   
/*   78 */   private volatile boolean update_space_outstanding = true;
/*   79 */   private volatile long space_on_device = -1L;
/*      */   
/*      */   private volatile boolean closing;
/*      */   
/*   83 */   private AsyncDispatcher dispatcher = new AsyncDispatcher();
/*      */   
/*   85 */   final FrequencyLimitedDispatcher freq_lim_updater = new FrequencyLimitedDispatcher(new AERunnable()
/*      */   {
/*      */ 
/*      */ 
/*      */     public void runSupport()
/*      */     {
/*      */ 
/*   92 */       DeviceOfflineDownloaderImpl.this.updateDownloads();
/*      */     }
/*   85 */   }, 5000);
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
/*   97 */   private boolean start_of_day = true;
/*   98 */   private int consec_errors = 0;
/*   99 */   private int consec_success = 0;
/*      */   
/*  101 */   private Map<String, OfflineDownload> offline_downloads = new HashMap();
/*  102 */   private Map<String, TransferableDownload> transferable = new LinkedHashMap();
/*      */   
/*      */   private TransferableDownload current_transfer;
/*      */   
/*      */   private boolean is_transferring;
/*  107 */   private CopyOnWriteList<DeviceOfflineDownloaderListener> listeners = new CopyOnWriteList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceOfflineDownloaderImpl(DeviceManagerImpl _manager, UPnPDevice _device, UPnPOfflineDownloader _service)
/*      */   {
/*  115 */     super(_manager, _device, 5);
/*      */     
/*  117 */     setService(_service);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DeviceOfflineDownloaderImpl(DeviceManagerImpl _manager, Map _map)
/*      */     throws java.io.IOException
/*      */   {
/*  127 */     super(_manager, _map);
/*      */     
/*  129 */     this.manufacturer = getPersistentStringProperty("od_manufacturer", "?");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*      */   {
/*  137 */     if (!super.updateFrom(_other, _is_alive))
/*      */     {
/*  139 */       return false;
/*      */     }
/*      */     
/*  142 */     if (!(_other instanceof DeviceOfflineDownloaderImpl))
/*      */     {
/*  144 */       Debug.out("Inconsistent");
/*      */       
/*  146 */       return false;
/*      */     }
/*      */     
/*  149 */     DeviceOfflineDownloaderImpl other = (DeviceOfflineDownloaderImpl)_other;
/*      */     
/*  151 */     if ((this.service == null) && (other.service != null))
/*      */     {
/*  153 */       setService(other.service);
/*      */       
/*  155 */       updateDownloads();
/*      */     }
/*      */     
/*  158 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setService(UPnPOfflineDownloader _service)
/*      */   {
/*  165 */     this.service = _service;
/*      */     
/*  167 */     UPnPRootDevice root = this.service.getGenericService().getDevice().getRootDevice();
/*      */     
/*  169 */     this.service_ip = root.getLocation().getHost();
/*      */     try
/*      */     {
/*  172 */       this.service_ip = InetAddress.getByName(this.service_ip).getHostAddress();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  176 */       Debug.out(e);
/*      */     }
/*      */     
/*  179 */     Map cache = root.getDiscoveryCache();
/*      */     
/*  181 */     if (cache != null)
/*      */     {
/*  183 */       setPersistentMapProperty("od_upnp_cache", cache);
/*      */     }
/*      */     
/*  186 */     this.manufacturer = root.getDevice().getManufacturer();
/*      */     
/*  188 */     setPersistentStringProperty("od_manufacturer", this.manufacturer);
/*      */     
/*  190 */     updateDownloads();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void UPnPInitialised()
/*      */   {
/*  196 */     super.UPnPInitialised();
/*      */     
/*  198 */     if (this.service == null)
/*      */     {
/*  200 */       Map cache = getPersistentMapProperty("od_upnp_cache", null);
/*      */       
/*  202 */       if (cache != null)
/*      */       {
/*  204 */         getUPnPDeviceManager().injectDiscoveryCache(cache);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateStatus(int tick_count)
/*      */   {
/*  213 */     super.updateStatus(tick_count);
/*      */     
/*  215 */     this.update_space_outstanding |= tick_count % 36 == 0;
/*      */     
/*  217 */     if (tick_count % 6 == 0)
/*      */     {
/*  219 */       updateDownloads();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkConfig()
/*      */   {
/*  226 */     this.freq_lim_updater.dispatch();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateDownloads()
/*      */   {
/*  232 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/*  238 */         if (DeviceOfflineDownloaderImpl.this.dispatcher.getQueueSize() == 0)
/*      */         {
/*  240 */           DeviceOfflineDownloaderImpl.this.updateDownloadsSupport();
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void updateDownloadsSupport()
/*      */   {
/*  249 */     AzureusCore core = getManager().getAzureusCore();
/*      */     
/*  251 */     if ((core == null) || (this.closing))
/*      */     {
/*      */ 
/*      */ 
/*  255 */       return;
/*      */     }
/*      */     
/*  258 */     boolean warn_if_dead = SystemTime.getMonotonousTime() - this.start_time > 180000L;
/*      */     
/*  260 */     if ((!isAlive()) || (this.service == null))
/*      */     {
/*      */ 
/*      */ 
/*  264 */       if (warn_if_dead)
/*      */       {
/*  266 */         setError(ERROR_KEY_OD, MessageText.getString("device.od.error.notfound"));
/*      */       }
/*      */       
/*  269 */       return;
/*      */     }
/*      */     
/*  272 */     String error_status = null;
/*  273 */     boolean force_status = false;
/*      */     
/*  275 */     Map<String, DownloadManager> new_offline_downloads = new HashMap();
/*  276 */     Map<String, TransferableDownload> new_transferables = new HashMap();
/*      */     try
/*      */     {
/*  279 */       if (this.update_space_outstanding) {
/*      */         try
/*      */         {
/*  282 */           this.space_on_device = this.service.getFreeSpace(client_id);
/*      */           
/*  284 */           this.update_space_outstanding = false;
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  288 */           error_status = MessageText.getString("device.od.error.opfailexcep", new String[] { "GetFreeSpace", Debug.getNestedExceptionMessage(e) });
/*      */           
/*  290 */           log("Failed to get free space", e);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  295 */       if (this.space_on_device == 0L)
/*      */       {
/*  297 */         error_status = MessageText.getString("device.od.error.nospace");
/*  298 */         force_status = true;
/*      */       }
/*      */       
/*  301 */       Map<String, byte[]> old_cache = getPersistentMapProperty("od_state_cache", new HashMap());
/*      */       
/*  303 */       Map<String, byte[]> new_cache = new HashMap();
/*      */       
/*  305 */       GlobalManager gm = core.getGlobalManager();
/*      */       
/*  307 */       if (this.start_of_day)
/*      */       {
/*  309 */         this.start_of_day = false;
/*      */         
/*  311 */         Map<String, Map> xfer_cache = getPersistentMapProperty("od_xfer_cache", new HashMap());
/*      */         
/*  313 */         if (xfer_cache.size() > 0)
/*      */         {
/*  315 */           List<DownloadManager> initial_downloads = gm.getDownloadManagers();
/*      */           
/*  317 */           for (DownloadManager download : initial_downloads)
/*      */           {
/*  319 */             if (download.isForceStart())
/*      */             {
/*  321 */               TOTorrent torrent = download.getTorrent();
/*      */               
/*  323 */               if (torrent != null)
/*      */               {
/*      */ 
/*      */                 try
/*      */                 {
/*      */ 
/*  329 */                   byte[] hash = torrent.getHash();
/*      */                   
/*  331 */                   String hash_str = ByteFormatter.encodeString(hash);
/*      */                   
/*  333 */                   Map m = (Map)xfer_cache.get(hash_str);
/*      */                   
/*  335 */                   if (m != null)
/*      */                   {
/*  337 */                     if (m.containsKey("f"))
/*      */                     {
/*  339 */                       log(download, "Resetting force-start");
/*      */                       
/*  341 */                       download.setForceStart(false);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {
/*  346 */                   Debug.printStackTrace(e);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  352 */         gm.addListener(new GlobalManagerAdapter()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void downloadManagerAdded(DownloadManager dm)
/*      */           {
/*      */ 
/*  359 */             DeviceOfflineDownloaderImpl.this.freq_lim_updater.dispatch();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  366 */           public void downloadManagerRemoved(DownloadManager dm) { DeviceOfflineDownloaderImpl.this.freq_lim_updater.dispatch(); } }, false);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  372 */       DeviceManager manager = getManager();
/*      */       
/*  374 */       DeviceOfflineDownloaderManager dodm = manager.getOfflineDownlaoderManager();
/*      */       
/*      */       List<DownloadManager> downloads;
/*      */       List<DownloadManager> downloads;
/*  378 */       if ((dodm.isOfflineDownloadingEnabled()) && (isEnabled()))
/*      */       {
/*  380 */         List<DownloadManager> initial_downloads = gm.getDownloadManagers();
/*      */         
/*  382 */         List<DownloadManager> relevant_downloads = new ArrayList(initial_downloads.size());
/*      */         
/*      */ 
/*      */ 
/*  386 */         for (DownloadManager download : initial_downloads)
/*      */         {
/*  388 */           int state = download.getState();
/*      */           
/*  390 */           if ((state != 60) && 
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  398 */             ((state != 70) || 
/*      */             
/*      */ 
/*      */ 
/*  402 */             (download.isPaused())) && 
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  410 */             (!download.isDownloadComplete(false)))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*  415 */             relevant_downloads.add(download);
/*      */           }
/*      */         }
/*  418 */         downloads = new ArrayList(relevant_downloads.size());
/*      */         
/*  420 */         if (dodm.getOfflineDownloadingIsAuto())
/*      */         {
/*  422 */           boolean include_private = dodm.getOfflineDownloadingIncludePrivate();
/*      */           
/*  424 */           if (include_private)
/*      */           {
/*  426 */             downloads.addAll(relevant_downloads);
/*      */           }
/*      */           else
/*      */           {
/*  430 */             for (DownloadManager download : relevant_downloads)
/*      */             {
/*  432 */               TOTorrent torrent = download.getTorrent();
/*      */               
/*  434 */               if (!TorrentUtils.isReallyPrivate(torrent))
/*      */               {
/*  436 */                 downloads.add(download);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  444 */           for (DownloadManager download : relevant_downloads)
/*      */           {
/*  446 */             if (dodm.isManualDownload(PluginCoreUtils.wrap(download)))
/*      */             {
/*  448 */               downloads.add(download);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  454 */         downloads = new ArrayList();
/*      */       }
/*      */       
/*  457 */       Map<DownloadManager, byte[]> download_map = new HashMap();
/*      */       
/*  459 */       for (DownloadManager download : downloads)
/*      */       {
/*  461 */         TOTorrent torrent = download.getTorrent();
/*      */         
/*  463 */         if (torrent != null)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*  469 */             byte[] hash = torrent.getHash();
/*      */             
/*  471 */             String hash_str = ByteFormatter.encodeString(hash);
/*      */             
/*  473 */             DiskManager disk = download.getDiskManager();
/*      */             
/*  475 */             if (disk == null)
/*      */             {
/*  477 */               byte[] existing = (byte[])old_cache.get(hash_str);
/*      */               
/*  479 */               if (existing != null)
/*      */               {
/*  481 */                 new_cache.put(hash_str, existing);
/*      */                 
/*  483 */                 download_map.put(download, existing);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*  489 */                 DiskManagerFileInfo[] files = download.getDiskManagerFileInfo();
/*      */                 
/*  491 */                 byte[] needed = new byte[(torrent.getNumberOfPieces() + 7) / 8];
/*      */                 
/*  493 */                 int hits = 0;
/*      */                 
/*  495 */                 for (DiskManagerFileInfo file : files)
/*      */                 {
/*  497 */                   if (!file.isSkipped())
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/*  502 */                     int first_piece = file.getFirstPieceNumber();
/*  503 */                     int last_piece = first_piece + file.getNbPieces() - 1;
/*      */                     
/*  505 */                     int needed_pos = first_piece / 8;
/*  506 */                     int current_byte = 0;
/*      */                     
/*  508 */                     for (int pos = first_piece; pos <= last_piece; pos++)
/*      */                     {
/*  510 */                       current_byte <<= 1;
/*      */                       
/*  512 */                       current_byte++;
/*      */                       
/*  514 */                       hits++;
/*      */                       
/*  516 */                       if (pos % 8 == 7)
/*      */                       {
/*  518 */                         int tmp1021_1018 = (needed_pos++); byte[] tmp1021_1014 = needed;tmp1021_1014[tmp1021_1018] = ((byte)(tmp1021_1014[tmp1021_1018] | (byte)current_byte));
/*      */                         
/*  520 */                         current_byte = 0;
/*      */                       }
/*      */                     }
/*      */                     
/*  524 */                     if (current_byte != 0)
/*      */                     {
/*  526 */                       int tmp1050_1047 = (needed_pos++); byte[] tmp1050_1043 = needed;tmp1050_1043[tmp1050_1047] = ((byte)(tmp1050_1043[tmp1050_1047] | (byte)(current_byte << 8 - last_piece % 8)));
/*      */                     }
/*      */                   }
/*      */                 }
/*  530 */                 if (hits > 0)
/*      */                 {
/*  532 */                   new_cache.put(hash_str, needed);
/*      */                   
/*  534 */                   download_map.put(download, needed);
/*      */                 }
/*      */               }
/*      */             }
/*      */             else {
/*  539 */               DiskManagerPiece[] pieces = disk.getPieces();
/*      */               
/*  541 */               byte[] needed = new byte[(pieces.length + 7) / 8];
/*      */               
/*  543 */               int needed_pos = 0;
/*  544 */               int current_byte = 0;
/*  545 */               int pos = 0;
/*      */               
/*  547 */               int hits = 0;
/*      */               
/*  549 */               for (DiskManagerPiece piece : pieces)
/*      */               {
/*  551 */                 current_byte <<= 1;
/*      */                 
/*  553 */                 if ((piece.isNeeded()) && (!piece.isDone()))
/*      */                 {
/*  555 */                   current_byte++;
/*      */                   
/*  557 */                   hits++;
/*      */                 }
/*      */                 
/*  560 */                 if (pos % 8 == 7)
/*      */                 {
/*  562 */                   needed[(needed_pos++)] = ((byte)current_byte);
/*      */                   
/*  564 */                   current_byte = 0;
/*      */                 }
/*  566 */                 pos++;
/*      */               }
/*      */               
/*  569 */               if (pos % 8 != 0)
/*      */               {
/*  571 */                 needed[(needed_pos++)] = ((byte)(current_byte << 8 - pos % 8));
/*      */               }
/*      */               
/*  574 */               if (hits > 0)
/*      */               {
/*  576 */                 new_cache.put(hash_str, needed);
/*      */                 
/*  578 */                 download_map.put(download, needed);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  583 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  589 */       setPersistentMapProperty("od_state_cache", new_cache);
/*      */       
/*      */ 
/*      */ 
/*  593 */       List<Map.Entry<DownloadManager, byte[]>> entries = new ArrayList(download_map.entrySet());
/*      */       
/*  595 */       Collections.sort(entries, new java.util.Comparator()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public int compare(Map.Entry<DownloadManager, byte[]> o1, Map.Entry<DownloadManager, byte[]> o2)
/*      */         {
/*      */ 
/*      */ 
/*  604 */           return ((DownloadManager)o1.getKey()).getPosition() - ((DownloadManager)o2.getKey()).getPosition();
/*      */         }
/*      */         
/*  607 */       });
/*  608 */       String download_hashes = "";
/*      */       
/*  610 */       Iterator<Map.Entry<DownloadManager, byte[]>> it = entries.iterator();
/*      */       
/*  612 */       while (it.hasNext())
/*      */       {
/*  614 */         Map.Entry<DownloadManager, byte[]> entry = (Map.Entry)it.next();
/*      */         
/*  616 */         DownloadManager download = (DownloadManager)entry.getKey();
/*      */         try
/*      */         {
/*  619 */           String hash = ByteFormatter.encodeString(download.getTorrent().getHash());
/*      */           
/*  621 */           download_hashes = download_hashes + (download_hashes.length() == 0 ? "" : ",") + hash;
/*      */           
/*  623 */           new_offline_downloads.put(hash, download);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  627 */           log(download, "Failed to get download hash", e);
/*      */           
/*  629 */           it.remove();
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/*  634 */         String[] set_dl_results = this.service.setDownloads(client_id, download_hashes);
/*      */         
/*  636 */         String set_dl_result = set_dl_results[0].trim();
/*  637 */         String set_dl_status = set_dl_results[1];
/*      */         
/*  639 */         if (!set_dl_status.equals("OK"))
/*      */         {
/*  641 */           error_status = MessageText.getString("device.od.error.opfailstatus", new String[] { "SetDownloads", set_dl_status });
/*      */           
/*  643 */           throw new Exception("Failing result returned: " + set_dl_status);
/*      */         }
/*      */         
/*  646 */         String[] bits = Constants.PAT_SPLIT_COMMA.split(set_dl_result);
/*      */         
/*  648 */         int num_bits = set_dl_result.length() == 0 ? 0 : bits.length;
/*      */         
/*  650 */         if (num_bits != entries.size())
/*      */         {
/*  652 */           log("SetDownloads returned an invalid number of results (hashes=" + new_offline_downloads.size() + ",result=" + set_dl_result + ")");
/*      */         }
/*      */         else
/*      */         {
/*  656 */           it = entries.iterator();
/*      */           
/*  658 */           int pos = 0;
/*      */           
/*  660 */           while (it.hasNext())
/*      */           {
/*  662 */             Map.Entry<DownloadManager, byte[]> entry = (Map.Entry)it.next();
/*      */             
/*  664 */             DownloadManager download = (DownloadManager)entry.getKey();
/*      */             try
/*      */             {
/*  667 */               TOTorrent torrent = download.getTorrent();
/*      */               
/*  669 */               String hash_str = ByteFormatter.encodeString(torrent.getHash());
/*      */               
/*  671 */               int status = Integer.parseInt(bits[(pos++)]);
/*      */               
/*  673 */               boolean do_update = false;
/*      */               
/*  675 */               if (status == 0)
/*      */               {
/*  677 */                 do_update = true;
/*      */               }
/*  679 */               else if (status == 1)
/*      */               {
/*      */ 
/*      */ 
/*      */                 try
/*      */                 {
/*      */ 
/*  686 */                   if (com.aelitis.azureus.core.torrent.PlatformTorrentUtils.isContent(torrent, true))
/*      */                   {
/*  688 */                     String ext = DownloadUtils.getTrackerExtensions(PluginCoreUtils.wrap(download));
/*      */                     
/*  690 */                     if ((ext != null) && (ext.length() > 0))
/*      */                     {
/*      */                       try
/*      */                       {
/*  694 */                         if (ext.startsWith("&"))
/*      */                         {
/*  696 */                           ext = ext.substring(1);
/*      */                         }
/*      */                         
/*  699 */                         torrent = org.gudy.azureus2.core3.torrent.TOTorrentFactory.deserialiseFromMap(torrent.serialiseToMap());
/*      */                         
/*  701 */                         torrent.setAnnounceURL(appendToURL(torrent.getAnnounceURL(), ext));
/*      */                         
/*  703 */                         TOTorrentAnnounceURLSet[] sets = torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*      */                         
/*  705 */                         for (TOTorrentAnnounceURLSet set : sets)
/*      */                         {
/*  707 */                           URL[] urls = set.getAnnounceURLs();
/*      */                           
/*  709 */                           for (int i = 0; i < urls.length; i++)
/*      */                           {
/*  711 */                             urls[i] = appendToURL(urls[i], ext);
/*      */                           }
/*      */                         }
/*      */                         
/*  715 */                         torrent.getAnnounceURLGroup().setAnnounceURLSets(sets);
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/*  719 */                         log("Torrent modification failed", e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                   
/*  724 */                   String add_result = addTorrent(hash_str, ByteFormatter.encodeStringFully(BEncoder.encode(torrent.serialiseToMap())));
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*  729 */                   log(download, "AddDownload succeeded");
/*      */                   
/*  731 */                   if (add_result.equals("OK"))
/*      */                   {
/*  733 */                     do_update = true;
/*      */                   }
/*      */                   else
/*      */                   {
/*  737 */                     error_status = MessageText.getString("device.od.error.opfailstatus", new String[] { "AddDownload", add_result });
/*      */                     
/*  739 */                     throw new Exception("Failed to add download: " + add_result);
/*      */                   }
/*      */                   
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  745 */                   error_status = MessageText.getString("device.od.error.opfailexcep", new String[] { "AddDownload", Debug.getNestedExceptionMessage(e) });
/*      */                   
/*  747 */                   log(download, "Failed to add download", e);
/*      */                 }
/*      */               }
/*      */               else {
/*  751 */                 error_status = MessageText.getString("device.od.error.opfailstatus", new String[] { "SetDownloads", String.valueOf(status) });
/*      */                 
/*  753 */                 log(download, "SetDownloads: error status returned - " + status);
/*      */               }
/*      */               
/*  756 */               if (do_update) {
/*      */                 try
/*      */                 {
/*  759 */                   byte[] required_map = (byte[])entry.getValue();
/*      */                   
/*  761 */                   String required_bitfield = ByteFormatter.encodeStringFully(required_map);
/*      */                   
/*  763 */                   String[] update_results = this.service.updateDownload(client_id, hash_str, required_bitfield);
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  769 */                   String have_bitfield = update_results[0];
/*  770 */                   String update_status = update_results[1];
/*      */                   
/*  772 */                   if (!update_status.equals("OK"))
/*      */                   {
/*  774 */                     error_status = MessageText.getString("device.od.error.opfailstatus", new String[] { "UpdateDownload", update_status });
/*      */                     
/*  776 */                     throw new Exception("UpdateDownload: Failing result returned: " + update_status);
/*      */                   }
/*      */                   
/*  779 */                   int useful_piece_count = 0;
/*      */                   
/*  781 */                   if (have_bitfield.length() > 0)
/*      */                   {
/*  783 */                     byte[] have_map = ByteFormatter.decodeString(have_bitfield);
/*      */                     
/*  785 */                     if (have_map.length != required_map.length)
/*      */                     {
/*  787 */                       throw new Exception("UpdateDownload: Returned bitmap length invalid");
/*      */                     }
/*      */                     
/*  790 */                     for (int i = 0; i < required_map.length; i++)
/*      */                     {
/*  792 */                       int x = required_map[i] & have_map[i] & 0xFF;
/*      */                       
/*  794 */                       if (x != 0)
/*      */                       {
/*  796 */                         for (int j = 0; j < 8; j++)
/*      */                         {
/*  798 */                           if ((x & 0x1) != 0)
/*      */                           {
/*  800 */                             useful_piece_count++;
/*      */                           }
/*      */                           
/*  803 */                           x >>= 1;
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/*  808 */                     if (useful_piece_count > 0)
/*      */                     {
/*  810 */                       long piece_size = torrent.getPieceLength();
/*      */                       
/*  812 */                       new_transferables.put(hash_str, new TransferableDownload(download, hash_str, have_map, useful_piece_count * piece_size));
/*      */                     }
/*      */                   }
/*      */                   
/*  816 */                   if (useful_piece_count > 0)
/*      */                   {
/*  818 */                     log(download, "They have " + useful_piece_count + " pieces that we don't");
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e)
/*      */                 {
/*  823 */                   error_status = MessageText.getString("device.od.error.opfailexcep", new String[] { "UpdateDownload", Debug.getNestedExceptionMessage(e) });
/*      */                   
/*  825 */                   log(download, "UpdateDownload failed", e);
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/*  830 */               log(download, "Processing failed", e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  837 */         error_status = MessageText.getString("device.od.error.opfailexcep", new String[] { "SetDownloads", Debug.getNestedExceptionMessage(e) });
/*      */         
/*  839 */         log("SetDownloads failed", e); } } finally { List<OfflineDownload> new_ods;
/*      */       List<OfflineDownload> del_ods;
/*      */       List<OfflineDownload> cha_ods;
/*      */       Iterator i$;
/*  843 */       Map.Entry<String, DownloadManager> entry; String key; OfflineDownload new_od; Iterator<Map.Entry<String, OfflineDownload>> it; Map.Entry<String, OfflineDownload> entry; String key; OfflineDownload od; TransferableDownload new_td; TransferableDownload existing_td; Iterator i$; OfflineDownload od; Iterator i$; DeviceOfflineDownloaderListener listener; Iterator i$; OfflineDownload od; Iterator i$; DeviceOfflineDownloaderListener listener; Iterator i$; OfflineDownload od; Iterator i$; DeviceOfflineDownloaderListener listener; updateTransferable(new_transferables);
/*      */       
/*  845 */       List<OfflineDownload> new_ods = new ArrayList();
/*  846 */       List<OfflineDownload> del_ods = new ArrayList();
/*  847 */       List<OfflineDownload> cha_ods = new ArrayList();
/*      */       
/*  849 */       synchronized (this.offline_downloads)
/*      */       {
/*  851 */         for (Map.Entry<String, DownloadManager> entry : new_offline_downloads.entrySet())
/*      */         {
/*  853 */           String key = (String)entry.getKey();
/*      */           
/*  855 */           if (!this.offline_downloads.containsKey(key))
/*      */           {
/*  857 */             OfflineDownload new_od = new OfflineDownload((DownloadManager)entry.getValue());
/*      */             
/*  859 */             this.offline_downloads.put(key, new_od);
/*      */             
/*  861 */             new_ods.add(new_od);
/*      */           }
/*      */         }
/*      */         
/*  865 */         Iterator<Map.Entry<String, OfflineDownload>> it = this.offline_downloads.entrySet().iterator();
/*      */         
/*  867 */         while (it.hasNext())
/*      */         {
/*  869 */           Map.Entry<String, OfflineDownload> entry = (Map.Entry)it.next();
/*      */           
/*  871 */           String key = (String)entry.getKey();
/*  872 */           OfflineDownload od = (OfflineDownload)entry.getValue();
/*      */           
/*  874 */           if (new_offline_downloads.containsKey(key))
/*      */           {
/*  876 */             TransferableDownload new_td = (TransferableDownload)this.transferable.get(key);
/*      */             
/*  878 */             TransferableDownload existing_td = od.getTransferable();
/*      */             
/*  880 */             if (new_td != existing_td)
/*      */             {
/*  882 */               if (!new_ods.contains(od))
/*      */               {
/*  884 */                 cha_ods.add(od);
/*      */               }
/*      */               
/*  887 */               od.setTransferable(new_td);
/*      */             }
/*      */           }
/*      */           else {
/*  891 */             it.remove();
/*      */             
/*  893 */             del_ods.add(od);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  898 */       for (Iterator i$ = new_ods.iterator(); i$.hasNext();) { od = (OfflineDownload)i$.next();
/*      */         
/*  900 */         for (DeviceOfflineDownloaderListener listener : this.listeners) {
/*      */           try
/*      */           {
/*  903 */             listener.downloadAdded(od);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  907 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       OfflineDownload od;
/*  912 */       for (Iterator i$ = cha_ods.iterator(); i$.hasNext();) { od = (OfflineDownload)i$.next();
/*      */         
/*  914 */         for (DeviceOfflineDownloaderListener listener : this.listeners) {
/*      */           try
/*      */           {
/*  917 */             listener.downloadChanged(od);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  921 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       OfflineDownload od;
/*  926 */       for (Iterator i$ = del_ods.iterator(); i$.hasNext();) { od = (OfflineDownload)i$.next();
/*      */         
/*  928 */         for (DeviceOfflineDownloaderListener listener : this.listeners) {
/*      */           try
/*      */           {
/*  931 */             listener.downloadRemoved(od);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  935 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       OfflineDownload od;
/*  940 */       updateError(error_status, force_status);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String addTorrent(String hash_str, String torrent_data)
/*      */     throws UPnPException
/*      */   {
/*  951 */     int chunk_size = 40960;
/*      */     
/*  953 */     int length = torrent_data.length();
/*      */     
/*  955 */     if (length < chunk_size)
/*      */     {
/*  957 */       return this.service.addDownload(client_id, hash_str, torrent_data);
/*      */     }
/*      */     
/*      */ 
/*  961 */     String status = "";
/*      */     
/*  963 */     int rem = length;
/*      */     
/*  965 */     for (int i = 0; i < length; i += chunk_size)
/*      */     {
/*  967 */       int size = Math.min(rem, chunk_size);
/*      */       
/*  969 */       status = this.service.addDownloadChunked(client_id, hash_str, torrent_data.substring(i, i + size), i, length);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  976 */       rem -= size;
/*      */     }
/*      */     
/*  979 */     return status;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateError(String str, boolean force)
/*      */   {
/*  988 */     if (str == null)
/*      */     {
/*  990 */       setError(ERROR_KEY_OD, null);
/*      */       
/*  992 */       this.consec_errors = 0;
/*      */       
/*  994 */       this.consec_success += 1;
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 1003 */         if (!this.service.getGenericService().isConnectable())
/*      */         {
/* 1005 */           str = MessageText.getString("device.od.error.notfound");
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1009 */         Debug.out(e);
/*      */       }
/*      */       
/* 1012 */       this.consec_errors += 1;
/*      */       
/* 1014 */       this.consec_success = 0;
/*      */       
/* 1016 */       if ((this.consec_errors > 2) || (force))
/*      */       {
/* 1018 */         setError(ERROR_KEY_OD, str);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected URL appendToURL(URL url, String ext)
/*      */     throws MalformedURLException
/*      */   {
/* 1030 */     String url_str = url.toExternalForm();
/*      */     
/* 1032 */     if (url_str.indexOf('?') == -1)
/*      */     {
/* 1034 */       url_str = url_str + "?" + ext;
/*      */     }
/*      */     else
/*      */     {
/* 1038 */       url_str = url_str + "&" + ext;
/*      */     }
/*      */     
/* 1041 */     return new URL(url_str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateTransferable(Map<String, TransferableDownload> map)
/*      */   {
/* 1050 */     Iterator<Map.Entry<String, TransferableDownload>> it = this.transferable.entrySet().iterator();
/*      */     
/* 1052 */     while (it.hasNext())
/*      */     {
/* 1054 */       Map.Entry<String, TransferableDownload> entry = (Map.Entry)it.next();
/*      */       
/* 1056 */       if (!map.containsKey(entry.getKey()))
/*      */       {
/* 1058 */         TransferableDownload existing = (TransferableDownload)entry.getValue();
/*      */         
/* 1060 */         if (existing == this.current_transfer)
/*      */         {
/* 1062 */           this.current_transfer.deactivate();
/*      */           
/* 1064 */           this.current_transfer = null;
/*      */         }
/*      */         
/* 1067 */         it.remove();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1073 */     for (TransferableDownload td : map.values())
/*      */     {
/* 1075 */       String hash = td.getHash();
/*      */       
/* 1077 */       if (!this.transferable.containsKey(hash))
/*      */       {
/* 1079 */         this.transferable.put(hash, td);
/*      */       }
/*      */     }
/*      */     
/* 1083 */     if (this.transferable.size() == 0)
/*      */     {
/* 1085 */       if (this.is_transferring)
/*      */       {
/* 1087 */         this.is_transferring = false;
/*      */         
/* 1089 */         setBusy(false);
/*      */       }
/*      */       
/* 1092 */       return;
/*      */     }
/*      */     
/* 1095 */     if (!this.is_transferring)
/*      */     {
/* 1097 */       this.is_transferring = true;
/*      */       
/* 1099 */       setBusy(true);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1104 */     if ((this.current_transfer != null) && (this.transferable.size() > 0))
/*      */     {
/*      */ 
/*      */ 
/* 1108 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 1110 */       long runtime = now - this.current_transfer.getStartTime();
/*      */       
/* 1112 */       if (runtime >= 30000L)
/*      */       {
/* 1114 */         boolean rotate = false;
/*      */         
/* 1116 */         PEPeerManager pm = this.current_transfer.getDownload().getPeerManager();
/*      */         
/* 1118 */         if (pm == null)
/*      */         {
/* 1120 */           rotate = true;
/*      */ 
/*      */ 
/*      */         }
/* 1124 */         else if (runtime > 180000L)
/*      */         {
/* 1126 */           List<PEPeer> peers = pm.getPeers(this.service_ip);
/*      */           
/* 1128 */           if (peers.size() == 0)
/*      */           {
/* 1130 */             rotate = true;
/*      */           }
/*      */           else
/*      */           {
/* 1134 */             PEPeer peer = (PEPeer)peers.get(0);
/*      */             
/* 1136 */             if (peer.getStats().getDataReceiveRate() < 1024L)
/*      */             {
/* 1138 */               rotate = true;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/* 1144 */         if (rotate)
/*      */         {
/* 1146 */           this.current_transfer.deactivate();
/*      */           
/* 1148 */           this.current_transfer = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1153 */     if (this.current_transfer == null)
/*      */     {
/* 1155 */       Iterator<TransferableDownload> it2 = this.transferable.values().iterator();
/*      */       
/* 1157 */       this.current_transfer = ((TransferableDownload)it2.next());
/*      */       
/* 1159 */       it2.remove();
/*      */       
/* 1161 */       this.transferable.put(this.current_transfer.getHash(), this.current_transfer);
/*      */     }
/*      */     
/* 1164 */     if (!this.current_transfer.isActive())
/*      */     {
/* 1166 */       this.current_transfer.activate();
/*      */     }
/*      */     
/* 1169 */     if (this.current_transfer.isForced())
/*      */     {
/* 1171 */       Map<String, Map> xfer_cache = new HashMap();
/*      */       
/* 1173 */       Map m = new HashMap();
/*      */       
/* 1175 */       m.put("f", new Long(1L));
/*      */       
/* 1177 */       xfer_cache.put(this.current_transfer.getHash(), m);
/*      */       
/* 1179 */       setPersistentMapProperty("od_xfer_cache", xfer_cache);
/*      */     }
/*      */     
/* 1182 */     DownloadManager download = this.current_transfer.getDownload();
/*      */     
/* 1184 */     int data_port = this.current_transfer.getDataPort();
/*      */     
/* 1186 */     if (data_port <= 0) {
/*      */       try
/*      */       {
/* 1189 */         String[] start_results = this.service.startDownload(client_id, this.current_transfer.getHash());
/*      */         
/* 1191 */         String start_status = start_results[1];
/*      */         
/* 1193 */         if (!start_status.equals("OK"))
/*      */         {
/* 1195 */           throw new Exception("Failing result returned: " + start_status);
/*      */         }
/*      */         
/* 1198 */         data_port = Integer.parseInt(start_results[0]);
/*      */         
/* 1200 */         log(download, "StartDownload succeeded - data port=" + data_port);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1204 */         log(download, "StartDownload failed", e);
/*      */       }
/*      */     }
/*      */     
/* 1208 */     if (data_port > 0)
/*      */     {
/* 1210 */       this.current_transfer.setDataPort(data_port);
/*      */     }
/*      */     
/* 1213 */     final TransferableDownload transfer = this.current_transfer;
/*      */     
/* 1215 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/* 1218 */       private final int[] count = { 0 };
/*      */       
/*      */ 
/*      */       public void runSupport()
/*      */       {
/* 1223 */         this.count[0] += 1;
/*      */         
/* 1225 */         if ((DeviceOfflineDownloaderImpl.this.current_transfer != transfer) || (!transfer.isActive()))
/*      */         {
/* 1227 */           return;
/*      */         }
/*      */         
/* 1230 */         PEPeerManager pm = transfer.getDownload().getPeerManager();
/*      */         
/* 1232 */         if (pm == null)
/*      */         {
/* 1234 */           return;
/*      */         }
/*      */         
/* 1237 */         List<PEPeer> peers = pm.getPeers(DeviceOfflineDownloaderImpl.this.service_ip);
/*      */         
/* 1239 */         if (peers.size() > 0)
/*      */         {
/* 1241 */           return;
/*      */         }
/*      */         
/* 1244 */         Map user_data = new LightHashMap();
/*      */         
/* 1246 */         user_data.put(org.gudy.azureus2.plugins.peers.Peer.PR_PRIORITY_CONNECTION, Boolean.TRUE);
/*      */         
/* 1248 */         pm.addPeer(DeviceOfflineDownloaderImpl.this.service_ip, transfer.getDataPort(), 0, false, user_data);
/*      */         
/* 1250 */         if (this.count[0] < 3)
/*      */         {
/* 1252 */           final AERunnable target = this;
/*      */           
/* 1254 */           SimpleTimer.addEvent("OD:retry", SystemTime.getCurrentTime() + 5000L, new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/*      */ 
/*      */ 
/* 1263 */               DeviceOfflineDownloaderImpl.this.dispatcher.dispatch(target);
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected void close()
/*      */   {
/* 1274 */     super.close();
/*      */     
/* 1276 */     final AESemaphore sem = new AESemaphore("DOD:closer");
/*      */     
/* 1278 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */         try
/*      */         {
/* 1285 */           DeviceOfflineDownloaderImpl.this.closing = true;
/*      */           
/* 1287 */           if (DeviceOfflineDownloaderImpl.this.service != null) {
/*      */             try
/*      */             {
/* 1290 */               DeviceOfflineDownloaderImpl.this.service.activate(DeviceOfflineDownloaderImpl.client_id);
/*      */ 
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 1298 */           sem.release();
/*      */         }
/*      */         
/*      */       }
/* 1302 */     });
/* 1303 */     sem.reserve(250L);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isEnabled()
/*      */   {
/* 1309 */     return getPersistentBooleanProperty("od_enabled", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setEnabled(boolean b)
/*      */   {
/* 1316 */     setPersistentBooleanProperty("od_enabled", b);
/*      */     
/* 1318 */     if (b)
/*      */     {
/* 1320 */       this.freq_lim_updater.dispatch();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isAlive()
/*      */   {
/* 1328 */     if (super.isAlive())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1333 */       return this.service.getGenericService().isConnectable();
/*      */     }
/*      */     
/* 1336 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasShownFTUX()
/*      */   {
/* 1342 */     return getPersistentBooleanProperty("od_shown_ftux", false);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setShownFTUX()
/*      */   {
/* 1348 */     setPersistentBooleanProperty("od_shown_ftux", true);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getManufacturer()
/*      */   {
/* 1354 */     return this.manufacturer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getSpaceAvailable(boolean force)
/*      */     throws DeviceManagerException
/*      */   {
/* 1363 */     if ((this.space_on_device >= 0L) && (!force))
/*      */     {
/* 1365 */       return this.space_on_device;
/*      */     }
/*      */     
/* 1368 */     if (this.service == null)
/*      */     {
/* 1370 */       throw new DeviceManagerException("Device is not online");
/*      */     }
/*      */     try
/*      */     {
/* 1374 */       this.space_on_device = this.service.getFreeSpace(client_id);
/*      */       
/* 1376 */       this.update_space_outstanding = false;
/*      */       
/* 1378 */       return this.space_on_device;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1382 */       throw new DeviceManagerException("Failed to read available space", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTransferingCount()
/*      */   {
/* 1389 */     return this.transferable.size();
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public DeviceOfflineDownload[] getDownloads()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 956	com/aelitis/azureus/core/devices/impl/DeviceOfflineDownloaderImpl:offline_downloads	Ljava/util/Map;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 956	com/aelitis/azureus/core/devices/impl/DeviceOfflineDownloaderImpl:offline_downloads	Ljava/util/Map;
/*      */     //   11: invokeinterface 1112 1 0
/*      */     //   16: aload_0
/*      */     //   17: getfield 956	com/aelitis/azureus/core/devices/impl/DeviceOfflineDownloaderImpl:offline_downloads	Ljava/util/Map;
/*      */     //   20: invokeinterface 1110 1 0
/*      */     //   25: anewarray 518	com/aelitis/azureus/core/devices/DeviceOfflineDownload
/*      */     //   28: invokeinterface 1100 2 0
/*      */     //   33: checkcast 509	[Lcom/aelitis/azureus/core/devices/DeviceOfflineDownload;
/*      */     //   36: aload_1
/*      */     //   37: monitorexit
/*      */     //   38: areturn
/*      */     //   39: astore_2
/*      */     //   40: aload_1
/*      */     //   41: monitorexit
/*      */     //   42: aload_2
/*      */     //   43: athrow
/*      */     // Line number table:
/*      */     //   Java source line #1395	-> byte code offset #0
/*      */     //   Java source line #1397	-> byte code offset #7
/*      */     //   Java source line #1398	-> byte code offset #39
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	44	0	this	DeviceOfflineDownloaderImpl
/*      */     //   5	36	1	Ljava/lang/Object;	Object
/*      */     //   39	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   7	38	39	finally
/*      */     //   39	42	39	finally
/*      */   }
/*      */   
/*      */   public void addListener(DeviceOfflineDownloaderListener listener)
/*      */   {
/* 1405 */     this.listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DeviceOfflineDownloaderListener listener)
/*      */   {
/* 1412 */     this.listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void getDisplayProperties(List<String[]> dp)
/*      */   {
/* 1419 */     super.getDisplayProperties(dp);
/*      */     
/* 1421 */     String space_str = "";
/*      */     
/* 1423 */     if (this.space_on_device >= 0L)
/*      */     {
/* 1425 */       space_str = DisplayFormatters.formatByteCountToKiBEtc(this.space_on_device);
/*      */     }
/*      */     
/* 1428 */     addDP(dp, "azbuddy.enabled", isEnabled());
/* 1429 */     addDP(dp, "device.od.space", space_str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(DownloadManager download, String str)
/*      */   {
/* 1437 */     log(download.getDisplayName() + ": " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(DownloadManager download, String str, Throwable e)
/*      */   {
/* 1446 */     log(download.getDisplayName() + ": " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1453 */     super.log("OfflineDownloader: " + str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1461 */     super.log("OfflineDownloader: " + str, e);
/*      */   }
/*      */   
/*      */ 
/*      */   protected static class OfflineDownload
/*      */     implements DeviceOfflineDownload
/*      */   {
/*      */     private DownloadManager core_download;
/*      */     
/*      */     private Download download;
/*      */     
/*      */     private DeviceOfflineDownloaderImpl.TransferableDownload transferable;
/*      */     
/*      */ 
/*      */     protected OfflineDownload(DownloadManager _core_download)
/*      */     {
/* 1477 */       this.core_download = _core_download;
/* 1478 */       this.download = PluginCoreUtils.wrap(this.core_download);
/*      */     }
/*      */     
/*      */ 
/*      */     public Download getDownload()
/*      */     {
/* 1484 */       return this.download;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean isTransfering()
/*      */     {
/* 1490 */       return this.transferable != null;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getCurrentTransferSize()
/*      */     {
/* 1496 */       DeviceOfflineDownloaderImpl.TransferableDownload t = this.transferable;
/*      */       
/* 1498 */       if (t == null)
/*      */       {
/* 1500 */         return 0L;
/*      */       }
/*      */       
/* 1503 */       return t.getCurrentTransferSize();
/*      */     }
/*      */     
/*      */ 
/*      */     public long getRemaining()
/*      */     {
/* 1509 */       DeviceOfflineDownloaderImpl.TransferableDownload t = this.transferable;
/*      */       
/* 1511 */       if (t == null)
/*      */       {
/* 1513 */         return 0L;
/*      */       }
/*      */       
/* 1516 */       return t.getRemaining();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setTransferable(DeviceOfflineDownloaderImpl.TransferableDownload td)
/*      */     {
/* 1523 */       this.transferable = td;
/*      */     }
/*      */     
/*      */ 
/*      */     protected DeviceOfflineDownloaderImpl.TransferableDownload getTransferable()
/*      */     {
/* 1529 */       return this.transferable;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class TransferableDownload
/*      */   {
/*      */     private DownloadManager download;
/*      */     
/*      */     private String hash_str;
/*      */     
/*      */     private byte[] have_map;
/*      */     
/*      */     private boolean active;
/*      */     
/*      */     private long start_time;
/*      */     
/*      */     private boolean forced;
/*      */     
/*      */     private int data_port;
/*      */     
/*      */     private long transfer_size;
/*      */     
/*      */     private volatile long last_calc;
/*      */     
/*      */     private volatile long last_calc_time;
/*      */     
/*      */     protected TransferableDownload(DownloadManager _download, String _hash_str, byte[] _have_map, long _transfer_size_estimate)
/*      */     {
/* 1558 */       this.download = _download;
/* 1559 */       this.hash_str = _hash_str;
/* 1560 */       this.have_map = _have_map;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1566 */       this.transfer_size = _transfer_size_estimate;
/*      */       
/* 1568 */       this.last_calc = this.transfer_size;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long calcDiff()
/*      */     {
/* 1574 */       long now = SystemTime.getMonotonousTime();
/*      */       
/* 1576 */       if (now - this.last_calc_time < 2000L)
/*      */       {
/* 1578 */         return this.last_calc;
/*      */       }
/*      */       
/* 1581 */       DiskManager disk = this.download.getDiskManager();
/*      */       
/* 1583 */       if (disk == null)
/*      */       {
/* 1585 */         return this.last_calc;
/*      */       }
/*      */       
/* 1588 */       DiskManagerPiece[] pieces = disk.getPieces();
/*      */       
/* 1590 */       int pos = 0;
/* 1591 */       int current = 0;
/*      */       
/* 1593 */       long remaining = 0L;
/*      */       
/* 1595 */       for (int i = 0; i < pieces.length; i++)
/*      */       {
/* 1597 */         if (i % 8 == 0)
/*      */         {
/* 1599 */           current = this.have_map[(pos++)] & 0xFF;
/*      */         }
/*      */         
/* 1602 */         if ((current & 0x80) != 0)
/*      */         {
/* 1604 */           DiskManagerPiece piece = pieces[i];
/*      */           
/* 1606 */           boolean[] written = piece.getWritten();
/*      */           
/* 1608 */           if (written == null)
/*      */           {
/* 1610 */             if (!piece.isDone())
/*      */             {
/* 1612 */               remaining += piece.getLength();
/*      */             }
/*      */           }
/*      */           else {
/* 1616 */             for (int j = 0; j < written.length; j++)
/*      */             {
/* 1618 */               if (written[j] == 0)
/*      */               {
/* 1620 */                 remaining += piece.getBlockSize(j);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1626 */         current <<= 1;
/*      */       }
/*      */       
/* 1629 */       this.last_calc = remaining;
/* 1630 */       this.last_calc_time = now;
/*      */       
/* 1632 */       return this.last_calc;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getCurrentTransferSize()
/*      */     {
/* 1638 */       return this.transfer_size;
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getRemaining()
/*      */     {
/* 1644 */       return calcDiff();
/*      */     }
/*      */     
/*      */ 
/*      */     protected long getStartTime()
/*      */     {
/* 1650 */       return this.start_time;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isForced()
/*      */     {
/* 1656 */       return this.forced;
/*      */     }
/*      */     
/*      */ 
/*      */     protected boolean isActive()
/*      */     {
/* 1662 */       return this.active;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getDataPort()
/*      */     {
/* 1668 */       return this.data_port;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setDataPort(int dp)
/*      */     {
/* 1675 */       this.data_port = dp;
/*      */     }
/*      */     
/*      */ 
/*      */     protected void activate()
/*      */     {
/* 1681 */       this.active = true;
/* 1682 */       this.start_time = SystemTime.getMonotonousTime();
/*      */       
/* 1684 */       if (this.download.isForceStart())
/*      */       {
/* 1686 */         DeviceOfflineDownloaderImpl.this.log(this.download, "Activating for transfer");
/*      */       }
/*      */       else
/*      */       {
/* 1690 */         DeviceOfflineDownloaderImpl.this.log(this.download, "Activating for transfer; setting force-start");
/*      */         
/* 1692 */         this.forced = true;
/*      */         
/* 1694 */         this.download.setForceStart(true);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void deactivate()
/*      */     {
/* 1701 */       this.active = false;
/*      */       
/* 1703 */       if (this.forced)
/*      */       {
/* 1705 */         DeviceOfflineDownloaderImpl.this.log(this.download, "Deactivating for transfer; resetting force-start");
/*      */         
/* 1707 */         this.download.setForceStart(false);
/*      */       }
/*      */       else
/*      */       {
/* 1711 */         DeviceOfflineDownloaderImpl.this.log(this.download, "Deactivating for transfer");
/*      */       }
/*      */       
/* 1714 */       this.data_port = 0;
/*      */     }
/*      */     
/*      */ 
/*      */     protected DownloadManager getDownload()
/*      */     {
/* 1720 */       return this.download;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getHash()
/*      */     {
/* 1726 */       return this.hash_str;
/*      */     }
/*      */     
/*      */ 
/*      */     protected byte[] getHaveMap()
/*      */     {
/* 1732 */       return this.have_map;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceOfflineDownloaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */