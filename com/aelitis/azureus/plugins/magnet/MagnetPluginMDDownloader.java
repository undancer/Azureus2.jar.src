/*     */ package com.aelitis.azureus.plugins.magnet;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerChannel;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerEvent;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerListener;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerRequest;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadManagerListener;
/*     */ import org.gudy.azureus2.plugins.download.DownloadPeerListener;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.peers.PeerEvent;
/*     */ import org.gudy.azureus2.plugins.peers.PeerListener2;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerEvent;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerListener2;
/*     */ import org.gudy.azureus2.plugins.utils.PooledByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class MagnetPluginMDDownloader
/*     */ {
/*  78 */   private static final Set<String> active_set = new HashSet();
/*     */   
/*     */   private final PluginInterface plugin_interface;
/*     */   
/*     */   private final MagnetPlugin plugin;
/*     */   
/*     */   private final byte[] hash;
/*     */   private final Set<String> networks;
/*     */   private final InetSocketAddress[] addresses;
/*     */   private final String args;
/*     */   private volatile boolean started;
/*     */   private volatile boolean cancelled;
/*     */   private volatile boolean completed;
/*  91 */   private List<DiskManagerRequest> requests = new ArrayList();
/*     */   
/*  93 */   private AESemaphore running_sem = new AESemaphore("MPMDD:run");
/*  94 */   private AESemaphore complete_sem = new AESemaphore("MPMDD:comp");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MagnetPluginMDDownloader(MagnetPlugin _plugin, PluginInterface _plugin_interface, byte[] _hash, Set<String> _networks, InetSocketAddress[] _addresses, String _args)
/*     */   {
/* 105 */     this.plugin = _plugin;
/* 106 */     this.plugin_interface = _plugin_interface;
/* 107 */     this.hash = _hash;
/* 108 */     this.networks = _networks;
/* 109 */     this.addresses = _addresses;
/* 110 */     this.args = _args;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void start(final DownloadListener listener)
/*     */   {
/* 117 */     synchronized (this)
/*     */     {
/* 119 */       if (this.started)
/*     */       {
/* 121 */         listener.failed(new Exception("Already started"));
/*     */         
/* 123 */         return;
/*     */       }
/*     */       
/* 126 */       if ((this.cancelled) || (this.completed))
/*     */       {
/* 128 */         listener.failed(new Exception("Already cancelled/completed"));
/*     */         
/* 130 */         return;
/*     */       }
/*     */       
/* 133 */       this.started = true;
/*     */       
/* 135 */       new AEThread2("MagnetPluginMDDownloader")
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/* 140 */           MagnetPluginMDDownloader.this.startSupport(listener);
/*     */         }
/*     */       }.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void cancel()
/*     */   {
/* 149 */     cancelSupport(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void cancelSupport(boolean internal)
/*     */   {
/* 156 */     boolean wait_for_complete = !internal;
/*     */     
/*     */     try
/*     */     {
/*     */       List<DiskManagerRequest> to_cancel;
/* 161 */       synchronized (this)
/*     */       {
/* 163 */         if (!this.started)
/*     */         {
/* 165 */           Debug.out("Not started!");
/*     */           
/* 167 */           wait_for_complete = false;
/*     */         }
/*     */         
/* 170 */         if ((this.cancelled) || (this.completed)) {
/*     */           return;
/*     */         }
/*     */         
/*     */ 
/* 175 */         this.cancelled = true;
/*     */         
/* 177 */         to_cancel = new ArrayList(this.requests);
/*     */         
/* 179 */         this.requests.clear();
/*     */       }
/*     */       
/* 182 */       for (DiskManagerRequest request : to_cancel)
/*     */       {
/* 184 */         request.cancel();
/*     */       }
/*     */     }
/*     */     finally {
/* 188 */       this.running_sem.releaseForever();
/*     */       
/* 190 */       if (wait_for_complete)
/*     */       {
/* 192 */         this.complete_sem.reserve();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void startSupport(final DownloadListener listener)
/*     */   {
/* 201 */     String hash_str = ByteFormatter.encodeString(this.hash);
/*     */     
/* 203 */     File tmp_dir = null;
/* 204 */     File data_file = null;
/* 205 */     File torrent_file = null;
/*     */     
/* 207 */     org.gudy.azureus2.plugins.download.DownloadManager download_manager = this.plugin_interface.getDownloadManager();
/*     */     
/* 209 */     Download download = null;
/*     */     
/* 211 */     final Throwable[] error = { null };
/*     */     
/* 213 */     final ByteArrayOutputStream result = new ByteArrayOutputStream(32768);
/*     */     
/* 215 */     TOTorrentAnnounceURLSet[] url_sets = null;
/*     */     try
/*     */     {
/* 218 */       synchronized (active_set)
/*     */       {
/* 220 */         if (active_set.contains(hash_str))
/*     */         {
/* 222 */           throw new Exception("Download already active for hash " + hash_str);
/*     */         }
/*     */         
/* 225 */         active_set.add(hash_str);
/*     */       }
/*     */       
/* 228 */       Download existing_download = download_manager.getDownload(this.hash);
/*     */       
/* 230 */       if (existing_download != null)
/*     */       {
/* 232 */         throw new Exception("download already exists");
/*     */       }
/*     */       
/* 235 */       tmp_dir = AETemporaryFileHandler.createTempDir();
/*     */       
/* 237 */       int rand = RandomUtils.generateRandomIntUpto(10000);
/*     */       
/* 239 */       data_file = new File(tmp_dir, hash_str + "_" + rand + ".torrent");
/* 240 */       torrent_file = new File(tmp_dir, hash_str + "_" + rand + ".metatorrent");
/*     */       
/* 242 */       RandomAccessFile raf = new RandomAccessFile(data_file, "rw");
/*     */       try
/*     */       {
/* 245 */         byte[] buffer = new byte[524288];
/*     */         
/* 247 */         Arrays.fill(buffer, (byte)-1);
/*     */         
/* 249 */         for (long i = 0L; i < 67108864L; i += buffer.length)
/*     */         {
/* 251 */           raf.write(buffer);
/*     */         }
/*     */       }
/*     */       finally {
/* 255 */         raf.close();
/*     */       }
/*     */       
/* 258 */       URL announce_url = TorrentUtils.getDecentralisedURL(this.hash);
/*     */       
/* 260 */       TOTorrentCreator creator = TOTorrentFactory.createFromFileOrDirWithFixedPieceLength(data_file, announce_url, 16384L);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 266 */       TOTorrent meta_torrent = creator.create();
/*     */       
/* 268 */       String[] bits = this.args.split("&");
/*     */       
/* 270 */       List<String> trackers = new ArrayList();
/*     */       
/* 272 */       String name = "magnet:" + Base32.encode(this.hash);
/*     */       
/* 274 */       Map<String, String> magnet_args = new HashMap();
/*     */       
/* 276 */       for (String bit : bits)
/*     */       {
/* 278 */         String[] x = bit.split("=");
/*     */         
/* 280 */         if (x.length == 2)
/*     */         {
/* 282 */           String lhs = x[0].toLowerCase();
/* 283 */           String rhs = UrlUtils.decode(x[1]);
/*     */           
/* 285 */           magnet_args.put(lhs, rhs);
/*     */           
/* 287 */           if (lhs.equals("tr"))
/*     */           {
/* 289 */             String tracker = rhs;
/*     */             
/* 291 */             trackers.add(tracker);
/*     */           }
/* 293 */           else if (lhs.equals("dn"))
/*     */           {
/* 295 */             name = rhs;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 300 */       if (trackers.size() > 0)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 305 */         trackers.add(0, announce_url.toExternalForm());
/*     */         
/* 307 */         TOTorrentAnnounceURLGroup ag = meta_torrent.getAnnounceURLGroup();
/*     */         
/* 309 */         List<TOTorrentAnnounceURLSet> sets = new ArrayList();
/*     */         
/* 311 */         for (String tracker : trackers) {
/*     */           try
/*     */           {
/* 314 */             URL tracker_url = new URL(tracker);
/*     */             
/* 316 */             sets.add(ag.createAnnounceURLSet(new URL[] { tracker_url }));
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 320 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/* 324 */         if (sets.size() > 0)
/*     */         {
/* 326 */           url_sets = (TOTorrentAnnounceURLSet[])sets.toArray(new TOTorrentAnnounceURLSet[sets.size()]);
/*     */           
/* 328 */           ag.setAnnounceURLSets(url_sets);
/*     */         }
/*     */       }
/*     */       
/* 332 */       if (!data_file.delete())
/*     */       {
/* 334 */         throw new Exception("Failed to delete " + data_file);
/*     */       }
/*     */       
/* 337 */       meta_torrent.setHashOverride(this.hash);
/*     */       
/* 339 */       TorrentUtils.setFlag(meta_torrent, 2, true);
/*     */       
/* 341 */       TorrentUtils.setFlag(meta_torrent, 1, true);
/*     */       
/* 343 */       meta_torrent.serialiseToBEncodedFile(torrent_file);
/*     */       
/* 345 */       download_manager.clearNonPersistentDownloadState(this.hash);
/*     */       
/* 347 */       download = download_manager.addNonPersistentDownloadStopped(PluginCoreUtils.wrap(meta_torrent), torrent_file, data_file);
/*     */       
/* 349 */       String display_name = MessageText.getString("MagnetPlugin.use.md.download.name", new String[] { name });
/*     */       
/* 351 */       DownloadManagerState state = PluginCoreUtils.unwrap(download).getDownloadState();
/*     */       
/* 353 */       state.setDisplayName(display_name + ".torrent");
/*     */       
/* 355 */       if ((this.networks.size() == 0) || ((this.networks.size() == 1) && (this.networks.contains("Public"))))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 362 */         for (String network : AENetworkClassifier.AT_NETWORKS)
/*     */         {
/* 364 */           state.setNetworkEnabled(network, true);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 369 */         for (String network : this.networks)
/*     */         {
/* 371 */           state.setNetworkEnabled(network, true);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 376 */         if (!this.networks.contains("Public"))
/*     */         {
/* 378 */           state.setNetworkEnabled("Public", false);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 385 */       if (!this.plugin.isNetworkEnabled("Public"))
/*     */       {
/* 387 */         state.setNetworkEnabled("Public", false);
/*     */       }
/*     */       
/* 390 */       final List<InetSocketAddress> peers_to_inject = new ArrayList();
/*     */       
/* 392 */       if ((this.addresses != null) && (this.addresses.length > 0))
/*     */       {
/* 394 */         String[] enabled_nets = state.getNetworks();
/*     */         
/* 396 */         for (InetSocketAddress address : this.addresses)
/*     */         {
/* 398 */           String host = AddressUtils.getHostAddress(address);
/*     */           
/* 400 */           String net = AENetworkClassifier.categoriseAddress(host);
/*     */           
/* 402 */           for (String n : enabled_nets)
/*     */           {
/* 404 */             if (n == net)
/*     */             {
/* 406 */               peers_to_inject.add(address);
/*     */               
/* 408 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 414 */       final Set<String> peer_networks = new HashSet();
/*     */       
/* 416 */       final List<Map<String, Object>> peers_for_cache = new ArrayList();
/*     */       
/* 418 */       download.addPeerListener(new DownloadPeerListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void peerManagerAdded(final Download download, final PeerManager peer_manager)
/*     */         {
/*     */ 
/*     */ 
/* 426 */           if ((MagnetPluginMDDownloader.this.cancelled) || (MagnetPluginMDDownloader.this.completed))
/*     */           {
/* 428 */             download.removePeerListener(this);
/*     */             
/* 430 */             return;
/*     */           }
/*     */           
/* 433 */           final PEPeerManager pm = PluginCoreUtils.unwrap(peer_manager);
/*     */           
/* 435 */           peer_manager.addListener(new PeerManagerListener2()
/*     */           {
/*     */ 
/* 438 */             private PeerManagerListener2 pm_listener = this;
/*     */             
/*     */ 
/*     */             private int md_size;
/*     */             
/*     */ 
/*     */             public void eventOccurred(PeerManagerEvent event)
/*     */             {
/* 446 */               if ((MagnetPluginMDDownloader.this.cancelled) || (MagnetPluginMDDownloader.this.completed))
/*     */               {
/* 448 */                 peer_manager.removeListener(this);
/*     */                 
/* 450 */                 return;
/*     */               }
/*     */               
/* 453 */               if (event.getType() != 1)
/*     */               {
/* 455 */                 return;
/*     */               }
/*     */               
/* 458 */               final Peer peer = event.getPeer();
/*     */               try
/*     */               {
/* 461 */                 String peer_ip = peer.getIp();
/*     */                 
/* 463 */                 String network = AENetworkClassifier.categoriseAddress(peer_ip);
/*     */                 
/* 465 */                 synchronized (MagnetPluginMDDownloader.2.this.val$peer_networks)
/*     */                 {
/* 467 */                   MagnetPluginMDDownloader.2.this.val$peer_networks.add(network);
/*     */                   
/* 469 */                   Map<String, Object> map = new HashMap();
/*     */                   
/* 471 */                   MagnetPluginMDDownloader.2.this.val$peers_for_cache.add(map);
/*     */                   
/* 473 */                   map.put("ip", peer_ip.getBytes("UTF-8"));
/*     */                   
/* 475 */                   map.put("port", new Long(peer.getPort()));
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 479 */                 Debug.out(e);
/*     */               }
/*     */               
/* 482 */               peer.addListener(new PeerListener2()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void eventOccurred(PeerEvent event)
/*     */                 {
/*     */ 
/* 489 */                   if ((MagnetPluginMDDownloader.this.cancelled) || (MagnetPluginMDDownloader.this.completed) || (MagnetPluginMDDownloader.2.1.this.md_size > 0))
/*     */                   {
/* 491 */                     peer.removeListener(this);
/*     */                     
/* 493 */                     return;
/*     */                   }
/*     */                   
/* 496 */                   if (event.getType() != 1)
/*     */                   {
/* 498 */                     return;
/*     */                   }
/*     */                   
/* 501 */                   if (((Integer)event.getData()).intValue() != 30)
/*     */                   {
/* 503 */                     return;
/*     */                   }
/*     */                   
/* 506 */                   synchronized (MagnetPluginMDDownloader.2.1.this.pm_listener)
/*     */                   {
/* 508 */                     if (MagnetPluginMDDownloader.2.1.this.md_size > 0)
/*     */                     {
/* 510 */                       return;
/*     */                     }
/*     */                     
/* 513 */                     MagnetPluginMDDownloader.2.1.this.md_size = MagnetPluginMDDownloader.2.1.this.val$pm.getTorrentInfoDictSize();
/*     */                     
/* 515 */                     if (MagnetPluginMDDownloader.2.1.this.md_size > 0)
/*     */                     {
/* 517 */                       MagnetPluginMDDownloader.2.1.this.val$peer_manager.removeListener(MagnetPluginMDDownloader.2.1.this.pm_listener);
/*     */                     }
/*     */                     else
/*     */                     {
/* 521 */                       return;
/*     */                     }
/*     */                   }
/*     */                   
/* 525 */                   MagnetPluginMDDownloader.2.this.val$listener.reportProgress(0, MagnetPluginMDDownloader.2.1.this.md_size);
/*     */                   
/* 527 */                   new AEThread2("")
/*     */                   {
/*     */ 
/*     */                     public void run()
/*     */                     {
/* 532 */                       DiskManagerChannel channel = null;
/*     */                       try
/*     */                       {
/* 535 */                         channel = MagnetPluginMDDownloader.2.1.this.val$download.getDiskManagerFileInfo()[0].createChannel();
/*     */                         
/* 537 */                         final DiskManagerRequest request = channel.createRequest();
/*     */                         
/* 539 */                         request.setType(1);
/* 540 */                         request.setOffset(0L);
/* 541 */                         request.setLength(MagnetPluginMDDownloader.2.1.this.md_size);
/*     */                         
/* 543 */                         request.setMaximumReadChunkSize(16384);
/*     */                         
/* 545 */                         request.addListener(new DiskManagerListener()
/*     */                         {
/*     */ 
/*     */ 
/*     */                           public void eventOccurred(DiskManagerEvent event)
/*     */                           {
/*     */ 
/* 552 */                             int type = event.getType();
/*     */                             
/* 554 */                             if (type == 2)
/*     */                             {
/* 556 */                               MagnetPluginMDDownloader.2.this.val$error[0] = event.getFailure();
/*     */                               
/* 558 */                               MagnetPluginMDDownloader.this.running_sem.releaseForever();
/*     */                             }
/* 560 */                             else if (type == 1)
/*     */                             {
/* 562 */                               PooledByteBuffer buffer = null;
/*     */                               try
/*     */                               {
/* 565 */                                 buffer = event.getBuffer();
/*     */                                 
/* 567 */                                 byte[] bytes = buffer.toByteArray();
/*     */                                 
/*     */                                 int dl_size;
/*     */                                 
/* 571 */                                 synchronized (MagnetPluginMDDownloader.this)
/*     */                                 {
/* 573 */                                   MagnetPluginMDDownloader.2.this.val$result.write(bytes);
/*     */                                   
/* 575 */                                   dl_size = MagnetPluginMDDownloader.2.this.val$result.size();
/*     */                                   
/* 577 */                                   if (dl_size == MagnetPluginMDDownloader.2.1.this.md_size)
/*     */                                   {
/* 579 */                                     MagnetPluginMDDownloader.this.completed = true;
/*     */                                     
/* 581 */                                     MagnetPluginMDDownloader.2.this.val$listener.reportProgress(MagnetPluginMDDownloader.2.1.this.md_size, MagnetPluginMDDownloader.2.1.this.md_size);
/*     */                                     
/* 583 */                                     MagnetPluginMDDownloader.this.running_sem.releaseForever();
/*     */                                   }
/*     */                                 }
/*     */                                 
/* 587 */                                 if (!MagnetPluginMDDownloader.this.completed)
/*     */                                 {
/* 589 */                                   MagnetPluginMDDownloader.2.this.val$listener.reportProgress(dl_size, MagnetPluginMDDownloader.2.1.this.md_size);
/*     */                                 }
/*     */                               }
/*     */                               catch (Throwable e)
/*     */                               {
/* 594 */                                 MagnetPluginMDDownloader.2.this.val$error[0] = e;
/*     */                                 
/* 596 */                                 request.cancel();
/*     */                                 
/* 598 */                                 MagnetPluginMDDownloader.this.running_sem.releaseForever();
/*     */                               }
/*     */                               finally
/*     */                               {
/* 602 */                                 if (buffer != null)
/*     */                                 {
/* 604 */                                   buffer.returnToPool();
/*     */                                 }
/*     */                               }
/* 607 */                             } else if (type != 3) {}
/*     */                           }
/*     */                         });
/*     */                         
/*     */ 
/*     */ 
/*     */ 
/* 614 */                         synchronized (MagnetPluginMDDownloader.this)
/*     */                         {
/* 616 */                           if (MagnetPluginMDDownloader.this.cancelled) {
/*     */                             return;
/*     */                           }
/*     */                           
/*     */ 
/* 621 */                           MagnetPluginMDDownloader.this.requests.add(request);
/*     */                         }
/*     */                         
/* 624 */                         request.run();
/*     */                         
/* 626 */                         synchronized (MagnetPluginMDDownloader.this)
/*     */                         {
/* 628 */                           MagnetPluginMDDownloader.this.requests.remove(request);
/*     */                         }
/*     */                       }
/*     */                       catch (Throwable e) {
/* 632 */                         MagnetPluginMDDownloader.2.this.val$error[0] = e;
/*     */                         
/* 634 */                         MagnetPluginMDDownloader.this.running_sem.releaseForever();
/*     */                       }
/*     */                       finally
/*     */                       {
/* 638 */                         if (channel != null)
/*     */                         {
/* 640 */                           channel.destroy();
/*     */                         }
/*     */                       }
/*     */                     }
/*     */                   }.start();
/*     */                 }
/*     */               });
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void peerManagerRemoved(Download download, PeerManager peer_manager) {}
/* 658 */       });
/* 659 */       final Download f_download = download;
/*     */       
/* 661 */       DownloadManagerListener dl_listener = new DownloadManagerListener()
/*     */       {
/*     */ 
/* 664 */         private Object lock = this;
/*     */         
/*     */         private TimerEventPeriodic timer_event;
/*     */         
/*     */         private boolean removed;
/*     */         
/*     */ 
/*     */         public void downloadAdded(final Download download)
/*     */         {
/* 673 */           if (download == f_download)
/*     */           {
/* 675 */             synchronized (this.lock)
/*     */             {
/* 677 */               if (!this.removed)
/*     */               {
/* 679 */                 if (this.timer_event == null)
/*     */                 {
/* 681 */                   this.timer_event = SimpleTimer.addPeriodicEvent("announcer", 30000L, new TimerEventPerformer()
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/*     */                     public void perform(TimerEvent event)
/*     */                     {
/*     */ 
/*     */ 
/*     */ 
/* 691 */                       synchronized (MagnetPluginMDDownloader.3.this.lock)
/*     */                       {
/* 693 */                         if (MagnetPluginMDDownloader.3.this.removed)
/*     */                         {
/* 695 */                           return;
/*     */                         }
/*     */                         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 702 */                         if (MagnetPluginMDDownloader.this.running_sem.isReleasedForever())
/*     */                         {
/* 704 */                           if (MagnetPluginMDDownloader.3.this.timer_event != null)
/*     */                           {
/* 706 */                             MagnetPluginMDDownloader.3.this.timer_event.cancel();
/*     */                             
/* 708 */                             MagnetPluginMDDownloader.3.this.timer_event = null;
/*     */                           }
/*     */                           
/* 711 */                           return;
/*     */                         }
/*     */                       }
/*     */                       
/* 715 */                       download.requestTrackerAnnounce(true);
/*     */                       
/* 717 */                       MagnetPluginMDDownloader.3.this.injectPeers(download);
/*     */                     }
/*     */                   });
/*     */                 }
/*     */                 
/* 722 */                 if (peers_to_inject.size() > 0)
/*     */                 {
/* 724 */                   SimpleTimer.addEvent("injecter", SystemTime.getOffsetTime(5000L), new TimerEventPerformer()
/*     */                   {
/*     */ 
/*     */                     public void perform(TimerEvent event)
/*     */                     {
/* 729 */                       MagnetPluginMDDownloader.3.this.injectPeers(download);
/*     */                     }
/*     */                   });
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         private void injectPeers(Download download)
/*     */         {
/* 742 */           PeerManager pm = download.getPeerManager();
/*     */           
/* 744 */           if (pm != null)
/*     */           {
/* 746 */             for (InetSocketAddress address : peers_to_inject)
/*     */             {
/* 748 */               pm.addPeer(AddressUtils.getHostAddress(address), address.getPort());
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void downloadRemoved(Download dl)
/*     */         {
/* 759 */           if (dl == f_download)
/*     */           {
/* 761 */             synchronized (this.lock)
/*     */             {
/* 763 */               this.removed = true;
/*     */               
/* 765 */               if (this.timer_event != null)
/*     */               {
/* 767 */                 this.timer_event.cancel();
/*     */                 
/* 769 */                 this.timer_event = null;
/*     */               }
/*     */             }
/*     */             
/* 773 */             if ((!MagnetPluginMDDownloader.this.cancelled) && (!MagnetPluginMDDownloader.this.completed))
/*     */             {
/* 775 */               error[0] = new Exception("Download manually removed");
/*     */               
/* 777 */               MagnetPluginMDDownloader.this.running_sem.releaseForever();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 782 */       };
/* 783 */       download_manager.addListener(dl_listener, true);
/*     */       try
/*     */       {
/* 786 */         download.moveTo(1);
/*     */         
/* 788 */         download.setForceStart(true);
/*     */         
/* 790 */         download.setFlag(4L, true);
/*     */         
/* 792 */         this.running_sem.reserve();
/*     */       }
/*     */       finally
/*     */       {
/* 796 */         download_manager.removeListener(dl_listener);
/*     */       }
/*     */       
/* 799 */       if (this.completed)
/*     */       {
/* 801 */         byte[] bytes = result.toByteArray();
/*     */         
/* 803 */         Map info = BDecoder.decode(bytes);
/*     */         
/* 805 */         Map map = new HashMap();
/*     */         
/* 807 */         map.put("info", info);
/*     */         
/* 809 */         TOTorrent torrent = TOTorrentFactory.deserialiseFromMap(map);
/*     */         
/* 811 */         byte[] final_hash = torrent.getHash();
/*     */         
/* 813 */         if (!Arrays.equals(this.hash, final_hash))
/*     */         {
/* 815 */           throw new Exception("Metadata torrent hash mismatch: expected=" + ByteFormatter.encodeString(this.hash) + ", actual=" + ByteFormatter.encodeString(final_hash));
/*     */         }
/*     */         
/* 818 */         if (url_sets != null)
/*     */         {
/*     */ 
/*     */ 
/* 822 */           List<TOTorrentAnnounceURLSet> updated = new ArrayList();
/*     */           
/* 824 */           for (TOTorrentAnnounceURLSet set : url_sets)
/*     */           {
/* 826 */             if (!TorrentUtils.isDecentralised(set.getAnnounceURLs()[0]))
/*     */             {
/* 828 */               updated.add(set);
/*     */             }
/*     */           }
/*     */           
/* 832 */           if (updated.size() == 0)
/*     */           {
/* 834 */             url_sets = null;
/*     */           }
/*     */           else
/*     */           {
/* 838 */             url_sets = (TOTorrentAnnounceURLSet[])updated.toArray(new TOTorrentAnnounceURLSet[updated.size()]);
/*     */           }
/*     */         }
/*     */         
/* 842 */         if (url_sets != null)
/*     */         {
/* 844 */           torrent.setAnnounceURL(url_sets[0].getAnnounceURLs()[0]);
/*     */           
/* 846 */           torrent.getAnnounceURLGroup().setAnnounceURLSets(url_sets);
/*     */         }
/*     */         else
/*     */         {
/* 850 */           torrent.setAnnounceURL(TorrentUtils.getDecentralisedURL(this.hash));
/*     */         }
/*     */         
/* 853 */         if (peers_for_cache.size() > 0)
/*     */         {
/* 855 */           Map<String, List<Map<String, Object>>> peer_cache = new HashMap();
/*     */           
/* 857 */           peer_cache.put("tracker_peers", peers_for_cache);
/*     */           
/* 859 */           TorrentUtils.setPeerCache(torrent, peer_cache);
/*     */         }
/*     */         try
/*     */         {
/* 863 */           String dn = (String)magnet_args.get("dn");
/*     */           
/* 865 */           if (dn != null)
/*     */           {
/* 867 */             PlatformTorrentUtils.setContentTitle(torrent, dn);
/*     */           }
/*     */           
/* 870 */           String pfi_str = (String)magnet_args.get("pfi");
/*     */           
/* 872 */           if (pfi_str != null)
/*     */           {
/* 874 */             PlatformTorrentUtils.setContentPrimaryFileIndex(torrent, Integer.parseInt(pfi_str));
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */         
/* 879 */         listener.complete(torrent, peer_networks);
/*     */       }
/*     */       else
/*     */       {
/* 883 */         if (this.cancelled)
/*     */         {
/* 885 */           throw new Exception("Download cancelled");
/*     */         }
/*     */         
/*     */ 
/* 889 */         cancelSupport(true);
/*     */         try
/*     */         {
/* 892 */           if (error[0] != null)
/*     */           {
/* 894 */             throw error[0];
/*     */           }
/*     */           
/*     */ 
/* 898 */           throw new Exception("Download terminated prematurely");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 902 */           listener.failed(e);
/*     */           
/* 904 */           Debug.out(e);
/*     */           
/* 906 */           throw e;
/*     */         }
/*     */       }
/*     */     } catch (Throwable e) { List<DiskManagerRequest> to_cancel;
/*     */       Iterator i$;
/*     */       DiskManagerRequest request;
/* 912 */       boolean was_cancelled = this.cancelled;
/*     */       
/* 914 */       cancelSupport(true);
/*     */       
/* 916 */       if (!was_cancelled)
/*     */       {
/* 918 */         listener.failed(e);
/*     */         
/* 920 */         Debug.out(e);
/*     */       }
/*     */     } finally { try { List<DiskManagerRequest> to_cancel;
/*     */         Iterator i$;
/*     */         DiskManagerRequest request;
/* 925 */         if (download != null)
/*     */         {
/*     */           try {
/* 928 */             download.stop();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */           try
/*     */           {
/* 934 */             download.remove();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 938 */             Debug.out(e);
/*     */           }
/*     */         }
/*     */         
/*     */         List<DiskManagerRequest> to_cancel;
/*     */         
/* 944 */         synchronized (this)
/*     */         {
/* 946 */           to_cancel = new ArrayList(this.requests);
/*     */           
/* 948 */           this.requests.clear();
/*     */         }
/*     */         
/* 951 */         for (DiskManagerRequest request : to_cancel)
/*     */         {
/* 953 */           request.cancel();
/*     */         }
/*     */         
/* 956 */         if (torrent_file != null)
/*     */         {
/* 958 */           torrent_file.delete();
/*     */         }
/*     */         
/* 961 */         if (data_file != null)
/*     */         {
/* 963 */           data_file.delete();
/*     */         }
/*     */         
/* 966 */         if (tmp_dir != null)
/*     */         {
/* 968 */           tmp_dir.delete();
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 972 */         Debug.out(e);
/*     */       }
/*     */       finally
/*     */       {
/* 976 */         synchronized (active_set)
/*     */         {
/* 978 */           active_set.remove(hash_str);
/*     */         }
/*     */         
/* 981 */         this.complete_sem.releaseForever();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected static abstract interface DownloadListener
/*     */   {
/*     */     public abstract void reportProgress(int paramInt1, int paramInt2);
/*     */     
/*     */     public abstract void complete(TOTorrent paramTOTorrent, Set<String> paramSet);
/*     */     
/*     */     public abstract void failed(Throwable paramThrowable);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/magnet/MagnetPluginMDDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */