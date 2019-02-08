/*      */ package org.gudy.azureus2.core3.download.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreOperation;
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.peermanager.control.PeerControlSchedulerFactory;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSource;
/*      */ import com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap;
/*      */ import com.aelitis.azureus.core.util.LinkFileMap.Entry;
/*      */ import com.aelitis.azureus.core.util.PlatformTorrentUtils;
/*      */ import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
/*      */ import com.aelitis.azureus.plugins.tracker.dht.DHTTrackerPlugin;
/*      */ import com.aelitis.azureus.plugins.tracker.local.LocalTrackerPlugin;
/*      */ import java.io.File;
/*      */ import java.io.FileFilter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFactory;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerActivationListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerDiskListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerException;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerInitialisationAdapter;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPeerListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerPieceListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerTPSListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerTrackerListener;
/*      */ import org.gudy.azureus2.core3.download.ForceRecheckListener;
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.LocaleTorrentUtil;
/*      */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.logging.LogAlert;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPiece;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLGroup;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentAnnounceURLSet;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentListener;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory.DataProvider;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerListener;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraper;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.LightHashMap;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.ListenerManagerDispatcher;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.download.DownloadAnnounceResult;
/*      */ import org.gudy.azureus2.plugins.download.DownloadScrapeResult;
/*      */ import org.gudy.azureus2.plugins.download.savelocation.SaveLocationChange;
/*      */ import org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl;
/*      */ import org.gudy.azureus2.pluginsimpl.local.download.DownloadImpl;
/*      */ 
/*      */ public class DownloadManagerImpl extends org.gudy.azureus2.core3.logging.LogRelation implements DownloadManager, com.aelitis.azureus.core.tag.Taggable
/*      */ {
/*      */   private static final long SCRAPE_DELAY_ERROR_TORRENTS = 7200000L;
/*      */   private static final long SCRAPE_DELAY_STOPPED_TORRENTS = 3600000L;
/*      */   private static final long SCRAPE_INITDELAY_ERROR_TORRENTS = 600000L;
/*      */   private static final long SCRAPE_INITDELAY_STOPPED_TORRENTS = 180000L;
/*      */   private static int upload_when_busy_min_secs;
/*      */   private static int max_connections_npp_extra;
/*  110 */   private static final ClientIDManagerImpl client_id_manager = ;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final String CFG_MOVE_COMPLETED_TOP = "Newly Seeding Torrents Get First Priority";
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int LDT_STATECHANGED = 1;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int LDT_DOWNLOADCOMPLETE = 2;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int LDT_COMPLETIONCHANGED = 3;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int LDT_POSITIONCHANGED = 4;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int LDT_FILEPRIORITYCHANGED = 5;
/*      */   
/*      */ 
/*      */ 
/*  140 */   private final AEMonitor listeners_mon = new AEMonitor("DM:DownloadManager:L");
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
/*      */   static final ListenerManager<DownloadManagerListener> listeners_aggregator;
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
/*      */   static final CopyOnWriteList<DownloadManagerListener> global_dm_listeners;
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
/*      */   private static final DownloadManagerListener global_dm_listener;
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
/*      */   public static void addGlobalDownloadListener(DownloadManagerListener listener)
/*      */   {
/*  273 */     global_dm_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void removeGlobalDownloadListener(DownloadManagerListener listener)
/*      */   {
/*  280 */     global_dm_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*  284 */   private final ListenerManager<DownloadManagerListener> listeners = ListenerManager.createManager("DM:ListenDispatcher", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(DownloadManagerListener listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  294 */       DownloadManagerImpl.listeners_aggregator.dispatch(listener, type, value);
/*      */     }
/*  284 */   });
/*      */   private static final int LDT_TL_ANNOUNCERESULT = 1;
/*      */   private static final int LDT_TL_SCRAPERESULT = 2;
/*      */   final ListenerManager tracker_listeners;
/*      */   private static final int LDT_PE_PEER_ADDED = 1;
/*      */   private static final int LDT_PE_PEER_REMOVED = 2;
/*      */   private static final int LDT_PE_PM_ADDED = 5;
/*      */   private static final int LDT_PE_PM_REMOVED = 6;
/*      */   static final ListenerManager<DownloadManagerPeerListener> peer_listeners_aggregator;
/*      */   static final Object TPS_Key;
/*      */   public static volatile String dnd_subfolder;
/*      */   private final ListenerManager<DownloadManagerPeerListener> peer_listeners;
/*      */   final AEMonitor peer_listeners_mon;
/*      */   final Map<PEPeer, String> current_peers;
/*      */   private final Map<PEPeer, Long> current_peers_unmatched_removal;
/*      */   private static final int LDT_PE_PIECE_ADDED = 3;
/*      */   private static final int LDT_PE_PIECE_REMOVED = 4;
/*      */   
/*      */   static
/*      */   {
/*  113 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "max.uploads.when.busy.inc.min.secs" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  123 */         DownloadManagerImpl.access$002(COConfigurationManager.getIntParameter("max.uploads.when.busy.inc.min.secs"));
/*      */         
/*  125 */         DownloadManagerImpl.access$102(COConfigurationManager.getIntParameter("Non-Public Peer Extra Connections Per Torrent"));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  142 */     });
/*  143 */     listeners_aggregator = ListenerManager.createAsyncManager("DM:ListenAggregatorDispatcher", new ListenerManagerDispatcher()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dispatch(DownloadManagerListener listener, int type, Object _value)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  153 */         Object[] value = (Object[])_value;
/*      */         
/*  155 */         DownloadManagerImpl dm = (DownloadManagerImpl)value[0];
/*      */         
/*  157 */         if (type == 1)
/*      */         {
/*  159 */           listener.stateChanged(dm, ((Integer)value[1]).intValue());
/*      */         }
/*  161 */         else if (type == 2)
/*      */         {
/*  163 */           listener.downloadComplete(dm);
/*      */         }
/*  165 */         else if (type == 3)
/*      */         {
/*  167 */           listener.completionChanged(dm, ((Boolean)value[1]).booleanValue());
/*      */         }
/*  169 */         else if (type == 5)
/*      */         {
/*  171 */           listener.filePriorityChanged(dm, (DiskManagerFileInfo)value[1]);
/*      */         }
/*  173 */         else if (type == 4)
/*      */         {
/*  175 */           listener.positionChanged(dm, ((Integer)value[1]).intValue(), ((Integer)value[2]).intValue());
/*      */         }
/*      */         
/*      */       }
/*  179 */     });
/*  180 */     global_dm_listeners = new CopyOnWriteList();
/*      */     
/*  182 */     global_dm_listener = new DownloadManagerListener()
/*      */     {
/*      */       public void stateChanged(DownloadManager manager, int state)
/*      */       {
/*  186 */         for (DownloadManagerListener listener : DownloadManagerImpl.global_dm_listeners) {
/*      */           try
/*      */           {
/*  189 */             listener.stateChanged(manager, state);
/*      */           } catch (Throwable e) {
/*  191 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       public void positionChanged(DownloadManager download, int oldPosition, int newPosition)
/*      */       {
/*  198 */         for (DownloadManagerListener listener : DownloadManagerImpl.global_dm_listeners) {
/*      */           try
/*      */           {
/*  201 */             listener.positionChanged(download, oldPosition, newPosition);
/*      */           } catch (Throwable e) {
/*  203 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       public void filePriorityChanged(DownloadManager download, DiskManagerFileInfo file)
/*      */       {
/*  210 */         for (DownloadManagerListener listener : DownloadManagerImpl.global_dm_listeners) {
/*      */           try
/*      */           {
/*  213 */             listener.filePriorityChanged(download, file);
/*      */           } catch (Throwable e) {
/*  215 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       public void downloadComplete(DownloadManager manager) {
/*  221 */         for (DownloadManagerListener listener : DownloadManagerImpl.global_dm_listeners) {
/*      */           try
/*      */           {
/*  224 */             listener.downloadComplete(manager);
/*      */           } catch (Throwable e) {
/*  226 */             Debug.out(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       public void completionChanged(DownloadManager manager, boolean bCompleted) {
/*  232 */         DownloadManagerState dms = manager.getDownloadState();
/*      */         
/*  234 */         long time = dms.getLongAttribute("complt");
/*      */         
/*  236 */         if (time == -1L) {
/*  237 */           if (bCompleted) {
/*  238 */             dms.setLongAttribute("complt", SystemTime.getCurrentTime());
/*      */           }
/*  240 */         } else if (time > 0L) {
/*  241 */           if (!bCompleted) {
/*  242 */             dms.setLongAttribute("complt", -1L);
/*      */           }
/*      */         }
/*  245 */         else if (bCompleted)
/*      */         {
/*  247 */           long completedOn = dms.getLongParameter("stats.download.completed.time");
/*      */           
/*  249 */           if (completedOn > 0L)
/*      */           {
/*  251 */             dms.setLongAttribute("complt", completedOn);
/*      */           }
/*      */         } else {
/*  254 */           dms.setLongAttribute("complt", -1L);
/*      */         }
/*      */         
/*      */ 
/*  258 */         for (DownloadManagerListener listener : DownloadManagerImpl.global_dm_listeners) {
/*      */           try
/*      */           {
/*  261 */             listener.completionChanged(manager, bCompleted);
/*      */           } catch (Throwable e) {
/*  263 */             Debug.out(e);
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
/*      */           }
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
/*      */         }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  337 */     };
/*  338 */     peer_listeners_aggregator = ListenerManager.createAsyncManager("DM:PeerListenAggregatorDispatcher", new ListenerManagerDispatcher()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dispatch(DownloadManagerPeerListener listener, int type, Object value)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  348 */         if (type == 1)
/*      */         {
/*  350 */           listener.peerAdded((PEPeer)value);
/*      */         }
/*  352 */         else if (type == 2)
/*      */         {
/*  354 */           listener.peerRemoved((PEPeer)value);
/*      */         }
/*  356 */         else if (type == 5)
/*      */         {
/*  358 */           listener.peerManagerAdded((PEPeerManager)value);
/*      */         }
/*  360 */         else if (type == 6)
/*      */         {
/*  362 */           listener.peerManagerRemoved((PEPeerManager)value);
/*      */         }
/*      */         
/*      */       }
/*  366 */     });
/*  367 */     TPS_Key = new Object();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  372 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Enable Subfolder for DND Files", "Subfolder for DND Files" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*  380 */         boolean enable = COConfigurationManager.getBooleanParameter("Enable Subfolder for DND Files");
/*      */         
/*  382 */         if (enable)
/*      */         {
/*  384 */           String folder = COConfigurationManager.getStringParameter("Subfolder for DND Files").trim();
/*      */           
/*  386 */           if (folder.length() > 0)
/*      */           {
/*  388 */             folder = FileUtil.convertOSSpecificChars(folder, true).trim();
/*      */           }
/*      */           
/*  391 */           if (folder.length() > 0)
/*      */           {
/*  393 */             DownloadManagerImpl.dnd_subfolder = folder;
/*      */           }
/*      */           else
/*      */           {
/*  397 */             DownloadManagerImpl.dnd_subfolder = null;
/*      */           }
/*      */         }
/*      */         else {
/*  401 */           DownloadManagerImpl.dnd_subfolder = null;
/*      */         }
/*      */       }
/*      */     });
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
/*  436 */   static final ListenerManager piece_listeners_aggregator = ListenerManager.createAsyncManager("DM:PieceListenAggregatorDispatcher", new ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(Object _listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  446 */       DownloadManagerPieceListener listener = (DownloadManagerPieceListener)_listener;
/*      */       
/*  448 */       if (type == 3)
/*      */       {
/*  450 */         listener.pieceAdded((PEPiece)value);
/*      */       }
/*  452 */       else if (type == 4)
/*      */       {
/*  454 */         listener.pieceRemoved((PEPiece)value);
/*      */       }
/*      */     }
/*  436 */   });
/*      */   private final ListenerManager piece_listeners;
/*      */   private List<DownloadManagerTPSListener> tps_listeners;
/*      */   private final AEMonitor piece_listeners_mon;
/*      */   private final List current_pieces;
/*      */   final DownloadManagerController controller;
/*      */   private final DownloadManagerStatsImpl stats;
/*      */   protected final AEMonitor this_mon;
/*      */   private final boolean persistent;
/*      */   private boolean assumedComplete;
/*      */   private int last_informed_state;
/*      */   private boolean latest_informed_force_start;
/*      */   private long resume_time;
/*      */   final GlobalManager globalManager;
/*      */   private String torrentFileName;
/*      */   private boolean open_for_seeding;
/*      */   private String display_name;
/*      */   private String internal_name;
/*      */   private File torrent_save_location;
/*      */   private int position;
/*      */   private Object[] read_torrent_state;
/*      */   private DownloadManagerState download_manager_state;
/*      */   private TOTorrent torrent;
/*      */   private String torrent_comment;
/*      */   private String torrent_created_by;
/*      */   private TRTrackerAnnouncer tracker_client;
/*      */   private final TRTrackerAnnouncerListener tracker_client_listener;
/*      */   private final TRTrackerAnnouncerListener stopping_tracker_client_listener;
/*      */   private final CopyOnWriteList activation_listeners;
/*      */   private final long scrape_random_seed;
/*      */   private volatile Map<Object, Object> data;
/*      */   private boolean data_already_allocated;
/*      */   private long creation_time;
/*      */   private int iSeedingRank;
/*      */   private boolean dl_identity_obtained;
/*      */   private byte[] dl_identity;
/*      */   private int dl_identity_hashcode;
/*      */   private int max_uploads;
/*      */   private int max_connections;
/*      */   private int max_connections_when_seeding;
/*      */   private boolean max_connections_when_seeding_enabled;
/*      */   private int max_seed_connections;
/*      */   private int max_uploads_when_seeding;
/*      */   private boolean max_uploads_when_seeding_enabled;
/*      */   private int max_upload_when_busy_bps;
/*      */   private int current_upload_when_busy_bps;
/*      */   private long last_upload_when_busy_update;
/*      */   private long last_upload_when_busy_dec_time;
/*      */   private int upload_priority_manual;
/*      */   private int upload_priority_auto;
/*      */   private int crypto_level;
/*      */   private int message_mode;
/*      */   private volatile boolean removing;
/*      */   private volatile boolean destroyed;
/*      */   private File cached_save_location;
/*      */   private File cached_save_location_result;
/*      */   
/*      */   public DownloadManagerImpl(GlobalManager _gm, byte[] _torrent_hash, String _torrentFileName, String _torrent_save_dir, String _torrent_save_file, int _initialState, boolean _persistent, boolean _recovered, boolean _open_for_seeding, boolean _has_ever_been_started, List _file_priorities, DownloadManagerInitialisationAdapter _initialisation_adapter)
/*      */   {
/*  299 */     this.listeners.addListener(global_dm_listener);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  306 */     this.tracker_listeners = ListenerManager.createManager("DM:TrackerListenDispatcher", new ListenerManagerDispatcher()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dispatch(Object _listener, int type, Object value)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  316 */         DownloadManagerTrackerListener listener = (DownloadManagerTrackerListener)_listener;
/*      */         
/*  318 */         if (type == 1)
/*      */         {
/*  320 */           listener.announceResult((TRTrackerAnnouncerResponse)value);
/*      */         }
/*  322 */         else if (type == 2)
/*      */         {
/*  324 */           listener.scrapeResult((TRTrackerScraperResponse)value);
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
/*      */         }
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
/*  409 */     });
/*  410 */     this.peer_listeners = ListenerManager.createManager("DM:PeerListenDispatcher", new ListenerManagerDispatcher()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dispatch(DownloadManagerPeerListener listener, int type, Object value)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  420 */         DownloadManagerImpl.peer_listeners_aggregator.dispatch(listener, type, value);
/*      */       }
/*      */       
/*  423 */     });
/*  424 */     this.peer_listeners_mon = new AEMonitor("DM:DownloadManager:PeerL");
/*      */     
/*  426 */     this.current_peers = new IdentityHashMap();
/*  427 */     this.current_peers_unmatched_removal = new IdentityHashMap();
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
/*  459 */     this.piece_listeners = ListenerManager.createManager("DM:PieceListenDispatcher", new ListenerManagerDispatcher()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void dispatch(Object listener, int type, Object value)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  469 */         DownloadManagerImpl.piece_listeners_aggregator.dispatch(listener, type, value);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  474 */     });
/*  475 */     this.piece_listeners_mon = new AEMonitor("DM:DownloadManager:PeiceL");
/*      */     
/*  477 */     this.current_pieces = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  482 */     this.this_mon = new AEMonitor("DM:DownloadManager");
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
/*  505 */     this.last_informed_state = -1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  515 */     this.display_name = "";
/*  516 */     this.internal_name = "";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  524 */     this.position = -1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  534 */     this.tracker_client_listener = new TRTrackerAnnouncerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void receivedTrackerResponse(TRTrackerAnnouncerResponse response)
/*      */       {
/*      */ 
/*  541 */         PEPeerManager pm = DownloadManagerImpl.this.controller.getPeerManager();
/*      */         
/*  543 */         if (pm != null)
/*      */         {
/*  545 */           pm.processTrackerResponse(response);
/*      */         }
/*      */         
/*  548 */         DownloadManagerImpl.this.tracker_listeners.dispatch(1, response);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void urlChanged(TRTrackerAnnouncer announcer, URL old_url, URL new_url, boolean explicit)
/*      */       {
/*  558 */         if (explicit)
/*      */         {
/*      */ 
/*      */ 
/*  562 */           if (DownloadManagerImpl.this.torrent.getPrivate())
/*      */           {
/*      */             final List<PEPeer> peers;
/*      */             try
/*      */             {
/*  567 */               DownloadManagerImpl.this.peer_listeners_mon.enter();
/*      */               
/*  569 */               peers = new ArrayList(DownloadManagerImpl.this.current_peers.keySet());
/*      */             }
/*      */             finally
/*      */             {
/*  573 */               DownloadManagerImpl.this.peer_listeners_mon.exit();
/*      */             }
/*      */             
/*  576 */             new AEThread2("DM:torrentChangeFlusher", true)
/*      */             {
/*      */ 
/*      */               public void run()
/*      */               {
/*  581 */                 for (int i = 0; i < peers.size(); i++)
/*      */                 {
/*  583 */                   PEPeer peer = (PEPeer)peers.get(i);
/*      */                   
/*  585 */                   peer.getManager().removePeer(peer, "Private torrent: tracker changed");
/*      */                 }
/*      */               }
/*      */             }.start();
/*      */           }
/*      */           
/*  591 */           DownloadManagerImpl.this.requestTrackerAnnounce(true);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       public void urlRefresh()
/*      */       {
/*  598 */         DownloadManagerImpl.this.requestTrackerAnnounce(true);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  603 */     };
/*  604 */     this.stopping_tracker_client_listener = new TRTrackerAnnouncerListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void receivedTrackerResponse(TRTrackerAnnouncerResponse response)
/*      */       {
/*      */ 
/*  611 */         if (DownloadManagerImpl.this.tracker_client == null)
/*  612 */           response.setPeers(new TRTrackerAnnouncerResponsePeer[0]);
/*  613 */         DownloadManagerImpl.this.tracker_listeners.dispatch(1, response);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void urlChanged(TRTrackerAnnouncer announcer, URL old_url, URL new_url, boolean explicit) {}
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void urlRefresh() {}
/*  631 */     };
/*  632 */     this.activation_listeners = new CopyOnWriteList();
/*      */     
/*  634 */     this.scrape_random_seed = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*      */ 
/*  638 */     this.data_already_allocated = false;
/*      */     
/*  640 */     this.creation_time = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  648 */     this.max_uploads = 2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  653 */     this.max_uploads_when_seeding = 2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  663 */     this.crypto_level = 0;
/*  664 */     this.message_mode = -1;
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
/*  686 */     if ((_initialState != 0) && (_initialState != 70) && (_initialState != 75))
/*      */     {
/*      */ 
/*      */ 
/*  690 */       Debug.out("DownloadManagerImpl: Illegal start state, " + _initialState);
/*      */     }
/*      */     
/*  693 */     this.persistent = _persistent;
/*  694 */     this.globalManager = _gm;
/*  695 */     this.open_for_seeding = _open_for_seeding;
/*      */     
/*      */ 
/*      */ 
/*  699 */     if (_file_priorities != null)
/*      */     {
/*  701 */       setData("file_priorities", _file_priorities);
/*      */     }
/*      */     
/*  704 */     this.stats = new DownloadManagerStatsImpl(this);
/*      */     
/*  706 */     this.controller = new DownloadManagerController(this);
/*      */     
/*  708 */     this.torrentFileName = _torrentFileName;
/*      */     
/*  710 */     while (_torrent_save_dir.endsWith(File.separator))
/*      */     {
/*  712 */       _torrent_save_dir = _torrent_save_dir.substring(0, _torrent_save_dir.length() - 1);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  717 */     readTorrent(_torrent_save_dir, _torrent_save_file, _torrent_hash, (this.persistent) && (!_recovered), _open_for_seeding, _has_ever_been_started, _initialState);
/*      */     
/*      */ 
/*      */ 
/*  721 */     if (this.torrent != null)
/*      */     {
/*  723 */       if ((_open_for_seeding) && (!_recovered))
/*      */       {
/*  725 */         Map<Integer, File> linkage = TorrentUtils.getInitialLinkage(this.torrent);
/*      */         
/*  727 */         if (linkage.size() > 0)
/*      */         {
/*  729 */           DownloadManagerState dms = getDownloadState();
/*      */           
/*  731 */           DiskManagerFileInfo[] files = getDiskManagerFileInfoSet().getFiles();
/*      */           try
/*      */           {
/*  734 */             dms.suppressStateSave(true);
/*      */             
/*  736 */             for (Map.Entry<Integer, File> entry : linkage.entrySet())
/*      */             {
/*  738 */               int index = ((Integer)entry.getKey()).intValue();
/*  739 */               File target = (File)entry.getValue();
/*      */               
/*  741 */               dms.setFileLink(index, files[index].getFile(false), target);
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/*  746 */             dms.suppressStateSave(false);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  751 */       if (_initialisation_adapter != null) {
/*      */         try
/*      */         {
/*  754 */           _initialisation_adapter.initialised(this, this.open_for_seeding);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/*  758 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTaggableType()
/*      */   {
/*  767 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.core.tag.TaggableResolver getTaggableResolver()
/*      */   {
/*  773 */     return this.globalManager;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTaggableID()
/*      */   {
/*  779 */     return this.dl_identity == null ? null : Base32.encode(this.dl_identity);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void readTorrent(String torrent_save_dir, String torrent_save_file, byte[] torrent_hash, boolean new_torrent, boolean for_seeding, boolean has_ever_been_started, int initial_state)
/*      */   {
/*      */     try
/*      */     {
/*  793 */       this.display_name = this.torrentFileName;
/*  794 */       this.internal_name = "";
/*  795 */       this.torrent_comment = "";
/*  796 */       this.torrent_created_by = "";
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/*  803 */         this.download_manager_state = DownloadManagerStateImpl.getDownloadState(this, this.torrentFileName, torrent_hash, (initial_state == 70) || (initial_state == 75));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  811 */         readParameters();
/*      */         
/*      */ 
/*      */ 
/*  815 */         DownloadManagerStateAttributeListener attr_listener = new DownloadManagerStateAttributeListener()
/*      */         {
/*      */ 
/*  818 */           private final ThreadLocal<Boolean> links_changing = new ThreadLocal()
/*      */           {
/*      */             protected Boolean initialValue()
/*      */             {
/*  822 */               return Boolean.FALSE;
/*      */             }
/*      */           };
/*      */           
/*      */ 
/*      */ 
/*      */           public void attributeEventOccurred(DownloadManager dm, String attribute_name, int event_type)
/*      */           {
/*  830 */             if (attribute_name.equals("filelinks2"))
/*      */             {
/*  832 */               if (((Boolean)this.links_changing.get()).booleanValue())
/*      */               {
/*  834 */                 System.out.println("recursive!");
/*      */                 
/*  836 */                 return;
/*      */               }
/*      */               
/*  839 */               this.links_changing.set(Boolean.valueOf(true));
/*      */               
/*      */               try
/*      */               {
/*  843 */                 DownloadManagerImpl.this.setFileLinks();
/*      */               }
/*      */               finally
/*      */               {
/*  847 */                 this.links_changing.set(Boolean.valueOf(false));
/*      */               }
/*  849 */             } else if (attribute_name.equals("parameters"))
/*      */             {
/*  851 */               DownloadManagerImpl.this.readParameters();
/*      */             }
/*  853 */             else if (attribute_name.equals("networks"))
/*      */             {
/*  855 */               TRTrackerAnnouncer tc = DownloadManagerImpl.this.tracker_client;
/*      */               
/*  857 */               if (tc != null)
/*      */               {
/*  859 */                 tc.resetTrackerUrl(false);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*  864 */         };
/*  865 */         this.download_manager_state.addListener(attr_listener, "filelinks2", 1);
/*  866 */         this.download_manager_state.addListener(attr_listener, "parameters", 1);
/*  867 */         this.download_manager_state.addListener(attr_listener, "networks", 1);
/*      */         
/*  869 */         this.torrent = this.download_manager_state.getTorrent();
/*      */         
/*  871 */         setFileLinks();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  877 */         if (!this.dl_identity_obtained)
/*      */         {
/*      */ 
/*      */ 
/*  881 */           this.dl_identity = (torrent_hash == null ? this.torrent.getHash() : torrent_hash);
/*      */           
/*  883 */           this.dl_identity_hashcode = new String(this.dl_identity).hashCode();
/*      */         }
/*      */         
/*  886 */         if (!Arrays.equals(this.dl_identity, this.torrent.getHash()))
/*      */         {
/*  888 */           this.torrent = null;
/*      */           
/*      */ 
/*      */ 
/*  892 */           this.torrent_save_location = new File(torrent_save_dir, this.torrentFileName);
/*      */           
/*  894 */           throw new NoStackException("Download identity changed - please remove and re-add the download");
/*      */         }
/*      */         
/*  897 */         this.read_torrent_state = null;
/*      */         
/*  899 */         LocaleUtilDecoder locale_decoder = LocaleTorrentUtil.getTorrentEncoding(this.torrent);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  904 */         this.display_name = FileUtil.convertOSSpecificChars(TorrentUtils.getLocalisedName(this.torrent), false);
/*      */         
/*  906 */         byte[] hash = this.torrent.getHash();
/*      */         
/*  908 */         this.internal_name = ByteFormatter.nicePrint(hash, true);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  914 */         File save_dir_file = new File(torrent_save_dir);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  920 */         if (torrent_save_file == null)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*  925 */             if (save_dir_file.exists())
/*      */             {
/*  927 */               save_dir_file = save_dir_file.getCanonicalFile();
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {
/*  931 */             Debug.printStackTrace(e);
/*      */           }
/*      */           
/*  934 */           if (this.torrent.isSimpleTorrent())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  940 */             if (save_dir_file.exists())
/*      */             {
/*  942 */               if (save_dir_file.isDirectory())
/*      */               {
/*  944 */                 torrent_save_file = this.display_name;
/*      */               }
/*      */               else
/*      */               {
/*  948 */                 torrent_save_dir = save_dir_file.getParent().toString();
/*      */                 
/*  950 */                 torrent_save_file = save_dir_file.getName();
/*      */               }
/*      */               
/*      */             }
/*      */             else
/*      */             {
/*  956 */               if (save_dir_file.getParent() == null)
/*      */               {
/*  958 */                 throw new NoStackException("Data location '" + torrent_save_dir + "' is invalid");
/*      */               }
/*      */               
/*      */ 
/*  962 */               torrent_save_dir = save_dir_file.getParent().toString();
/*      */               
/*  964 */               torrent_save_file = save_dir_file.getName();
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  974 */             if (save_dir_file.exists())
/*      */             {
/*  976 */               if (!save_dir_file.isDirectory())
/*      */               {
/*  978 */                 throw new NoStackException("'" + torrent_save_dir + "' is not a directory");
/*      */               }
/*      */               
/*  981 */               if (save_dir_file.getName().equals(this.display_name)) {
/*  982 */                 torrent_save_dir = save_dir_file.getParent().toString();
/*      */               }
/*      */             }
/*      */             
/*  986 */             torrent_save_file = this.display_name;
/*      */           }
/*      */         }
/*      */         
/*  990 */         this.torrent_save_location = new File(torrent_save_dir, torrent_save_file);
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
/* 1004 */         if ((!new_torrent) && (!org.gudy.azureus2.core3.util.Constants.isWindows))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1009 */           if (has_ever_been_started)
/*      */           {
/* 1011 */             File linked_target = getSaveLocation();
/*      */             
/* 1013 */             if (!linked_target.exists())
/*      */             {
/* 1015 */               throw new NoStackException(MessageText.getString("DownloadManager.error.datamissing") + " " + Debug.secretFileName(linked_target.toString()));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1024 */         boolean low_noise = TorrentUtils.getFlag(this.torrent, 1);
/*      */         
/* 1026 */         if (low_noise)
/*      */         {
/* 1028 */           this.download_manager_state.setFlag(16L, true);
/*      */         }
/*      */         
/* 1031 */         boolean metadata_dl = TorrentUtils.getFlag(this.torrent, 2);
/*      */         
/* 1033 */         if (metadata_dl)
/*      */         {
/* 1035 */           this.download_manager_state.setFlag(512L, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1042 */         if (new_torrent)
/*      */         {
/* 1044 */           this.download_manager_state.setLongParameter("stats.download.added.time", SystemTime.getCurrentTime());
/*      */           
/* 1046 */           Map peer_cache = TorrentUtils.getPeerCache(this.torrent);
/*      */           
/* 1048 */           if (peer_cache != null) {
/*      */             try
/*      */             {
/* 1051 */               this.download_manager_state.setTrackerResponseCache(peer_cache);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1055 */               Debug.out(e);
/*      */               
/* 1057 */               this.download_manager_state.setTrackerResponseCache(new HashMap());
/*      */             }
/*      */             
/*      */           } else {
/* 1061 */             this.download_manager_state.setTrackerResponseCache(new HashMap());
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1067 */           if (for_seeding)
/*      */           {
/* 1069 */             DiskManagerFactory.setTorrentResumeDataNearlyComplete(this.download_manager_state);
/*      */             
/*      */ 
/* 1072 */             this.download_manager_state.setFlag(8L, true);
/*      */           }
/*      */           else
/*      */           {
/* 1076 */             this.download_manager_state.clearResumeData();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1081 */           if ((this.persistent) && (!for_seeding) && (!this.torrent.isSimpleTorrent()))
/*      */           {
/* 1083 */             String dnd_sf = dnd_subfolder;
/*      */             
/* 1085 */             if (dnd_sf != null)
/*      */             {
/* 1087 */               if (this.torrent.getFiles().length <= org.gudy.azureus2.core3.download.DownloadManagerStateFactory.MAX_FILES_FOR_INCOMPLETE_AND_DND_LINKAGE)
/*      */               {
/* 1089 */                 if (this.download_manager_state.getAttribute("dnd_sf") == null)
/*      */                 {
/* 1091 */                   this.download_manager_state.setAttribute("dnd_sf", dnd_sf);
/*      */                 }
/*      */                 
/* 1094 */                 boolean use_prefix = COConfigurationManager.getBooleanParameter("Use Incomplete File Prefix");
/*      */                 
/* 1096 */                 if (use_prefix)
/*      */                 {
/* 1098 */                   if (this.download_manager_state.getAttribute("dnd_pfx") == null)
/*      */                   {
/* 1100 */                     String prefix = Base32.encode(hash).substring(0, 12).toLowerCase(java.util.Locale.US) + "_";
/*      */                     
/* 1102 */                     this.download_manager_state.setAttribute("dnd_pfx", prefix);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 1110 */           long add_time = this.download_manager_state.getLongParameter("stats.download.added.time");
/*      */           
/* 1112 */           if (add_time == 0L)
/*      */           {
/*      */ 
/*      */             try
/*      */             {
/* 1117 */               add_time = new File(this.torrentFileName).lastModified();
/*      */             }
/*      */             catch (Throwable e) {}
/*      */             
/*      */ 
/* 1122 */             if (add_time == 0L)
/*      */             {
/* 1124 */               add_time = SystemTime.getCurrentTime();
/*      */             }
/*      */             
/* 1127 */             this.download_manager_state.setLongParameter("stats.download.added.time", add_time);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1134 */         this.torrent_comment = org.gudy.azureus2.core3.util.StringInterner.intern(locale_decoder.decodeString(this.torrent.getComment()));
/*      */         
/* 1136 */         if (this.torrent_comment == null)
/*      */         {
/* 1138 */           this.torrent_comment = "";
/*      */         }
/*      */         
/* 1141 */         this.torrent_created_by = locale_decoder.decodeString(this.torrent.getCreatedBy());
/*      */         
/* 1143 */         if (this.torrent_created_by == null)
/*      */         {
/* 1145 */           this.torrent_created_by = "";
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1150 */         if ((this.download_manager_state.isResumeDataComplete()) || (for_seeding))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1158 */           this.stats.setDownloadCompletedBytes(getSize());
/*      */           
/* 1160 */           setAssumedComplete(true);
/*      */         }
/*      */         else
/*      */         {
/* 1164 */           setAssumedComplete(false);
/*      */         }
/*      */         
/* 1167 */         if (this.download_manager_state.getDisplayName() == null)
/*      */         {
/* 1169 */           String title = PlatformTorrentUtils.getContentTitle(this.torrent);
/*      */           
/* 1171 */           if ((title != null) && (title.length() > 0))
/*      */           {
/* 1173 */             this.download_manager_state.setDisplayName(title);
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/* 1180 */         setFailed(TorrentUtils.exceptionToText(e));
/*      */       }
/*      */       catch (UnsupportedEncodingException e)
/*      */       {
/* 1184 */         Debug.printStackTrace(e);
/*      */         
/* 1186 */         setFailed(MessageText.getString("DownloadManager.error.unsupportedencoding"));
/*      */       }
/*      */       catch (NoStackException e)
/*      */       {
/* 1190 */         Debug.outNoStack(e.getMessage());
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1194 */         Debug.printStackTrace(e);
/*      */         
/* 1196 */         setFailed(e);
/*      */       }
/*      */       finally
/*      */       {
/* 1200 */         this.dl_identity_obtained = true;
/*      */       }
/*      */       
/* 1203 */       if (this.download_manager_state == null) {
/* 1204 */         this.read_torrent_state = new Object[] { torrent_save_dir, torrent_save_file, torrent_hash, Boolean.valueOf(new_torrent), Boolean.valueOf(for_seeding), Boolean.valueOf(has_ever_been_started), new Integer(initial_state) };
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1214 */         this.download_manager_state = DownloadManagerStateImpl.getDownloadState(this);
/*      */         
/*      */ 
/*      */ 
/* 1218 */         if (torrent_save_file == null)
/*      */         {
/* 1220 */           this.torrent_save_location = new File(torrent_save_dir);
/*      */         }
/*      */         else
/*      */         {
/* 1224 */           this.torrent_save_location = new File(torrent_save_dir, torrent_save_file);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1232 */         if (torrent_save_file == null)
/*      */         {
/* 1234 */           this.torrent_save_location = new File(torrent_save_dir);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1239 */         if ((this.torrent != null) && (!this.download_manager_state.hasAttribute("networks")))
/*      */         {
/* 1241 */           String[] networks = AENetworkClassifier.getNetworks(this.torrent, this.display_name);
/*      */           
/* 1243 */           this.download_manager_state.setNetworks(networks);
/*      */         } } } finally { boolean already_done;
/*      */       String cache;
/*      */       String key;
/*      */       String key;
/* 1248 */       if (this.torrent_save_location != null)
/*      */       {
/* 1250 */         boolean already_done = false;
/*      */         
/* 1252 */         String cache = this.download_manager_state.getAttribute("canosavedir");
/*      */         
/* 1254 */         if (cache != null)
/*      */         {
/* 1256 */           String key = this.torrent_save_location.getAbsolutePath() + "\n";
/*      */           
/* 1258 */           if (cache.startsWith(key))
/*      */           {
/* 1260 */             this.torrent_save_location = new File(cache.substring(key.length()));
/*      */             
/* 1262 */             already_done = true;
/*      */           }
/*      */         }
/*      */         
/* 1266 */         if (!already_done)
/*      */         {
/* 1268 */           String key = this.torrent_save_location.getAbsolutePath() + "\n";
/*      */           try
/*      */           {
/* 1271 */             this.torrent_save_location = this.torrent_save_location.getCanonicalFile();
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1275 */             this.torrent_save_location = this.torrent_save_location.getAbsoluteFile();
/*      */           }
/*      */           
/* 1278 */           this.download_manager_state.setAttribute("canosavedir", key + this.torrent_save_location.getAbsolutePath());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1284 */         getSaveLocation();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1291 */       this.controller.setInitialState(initial_state);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void readTorrent()
/*      */   {
/* 1298 */     if (this.read_torrent_state == null)
/*      */     {
/* 1300 */       return;
/*      */     }
/*      */     
/* 1303 */     readTorrent((String)this.read_torrent_state[0], (String)this.read_torrent_state[1], (byte[])this.read_torrent_state[2], ((Boolean)this.read_torrent_state[3]).booleanValue(), ((Boolean)this.read_torrent_state[4]).booleanValue(), ((Boolean)this.read_torrent_state[5]).booleanValue(), ((Integer)this.read_torrent_state[6]).intValue());
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
/*      */   protected void readParameters()
/*      */   {
/* 1317 */     this.max_connections = getDownloadState().getIntParameter("max.peers");
/* 1318 */     this.max_connections_when_seeding_enabled = getDownloadState().getBooleanParameter("max.peers.when.seeding.enabled");
/* 1319 */     this.max_connections_when_seeding = getDownloadState().getIntParameter("max.peers.when.seeding");
/* 1320 */     this.max_seed_connections = getDownloadState().getIntParameter("max.seeds");
/* 1321 */     this.max_uploads = getDownloadState().getIntParameter("max.uploads");
/* 1322 */     this.max_uploads_when_seeding_enabled = getDownloadState().getBooleanParameter("max.uploads.when.seeding.enabled");
/* 1323 */     this.max_uploads_when_seeding = getDownloadState().getIntParameter("max.uploads.when.seeding");
/* 1324 */     this.max_upload_when_busy_bps = (getDownloadState().getIntParameter("max.upload.when.busy") * 1024);
/*      */     
/* 1326 */     this.max_uploads = Math.max(this.max_uploads, 2);
/* 1327 */     this.max_uploads_when_seeding = Math.max(this.max_uploads_when_seeding, 2);
/*      */     
/* 1329 */     this.upload_priority_manual = getDownloadState().getIntParameter("up.pri");
/*      */   }
/*      */   
/*      */ 
/*      */   protected int[] getMaxConnections(boolean mixed)
/*      */   {
/* 1335 */     if ((mixed) && (this.max_connections > 0))
/*      */     {
/* 1337 */       return new int[] { this.max_connections, max_connections_npp_extra };
/*      */     }
/*      */     
/* 1340 */     return new int[] { this.max_connections, 0 };
/*      */   }
/*      */   
/*      */ 
/*      */   protected int[] getMaxConnectionsWhenSeeding(boolean mixed)
/*      */   {
/* 1346 */     if ((mixed) && (this.max_connections_when_seeding > 0))
/*      */     {
/* 1348 */       return new int[] { this.max_connections_when_seeding, max_connections_npp_extra };
/*      */     }
/*      */     
/* 1351 */     return new int[] { this.max_connections_when_seeding, 0 };
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isMaxConnectionsWhenSeedingEnabled()
/*      */   {
/* 1357 */     return this.max_connections_when_seeding_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int[] getMaxSeedConnections(boolean mixed)
/*      */   {
/* 1363 */     if ((mixed) && (this.max_seed_connections > 0))
/*      */     {
/* 1365 */       return new int[] { this.max_seed_connections, max_connections_npp_extra };
/*      */     }
/*      */     
/* 1368 */     return new int[] { this.max_seed_connections, 0 };
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean isMaxUploadsWhenSeedingEnabled()
/*      */   {
/* 1374 */     return this.max_uploads_when_seeding_enabled;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getMaxUploadsWhenSeeding()
/*      */   {
/* 1380 */     return this.max_uploads_when_seeding;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updateAutoUploadPriority(Object key, boolean inc)
/*      */   {
/*      */     try
/*      */     {
/* 1389 */       this.peer_listeners_mon.enter();
/*      */       
/* 1391 */       boolean key_exists = getUserData(key) != null;
/*      */       
/* 1393 */       if ((inc) && (!key_exists))
/*      */       {
/* 1395 */         this.upload_priority_auto += 1;
/*      */         
/* 1397 */         setUserData(key, "");
/*      */       }
/* 1399 */       else if ((!inc) && (key_exists))
/*      */       {
/* 1401 */         this.upload_priority_auto -= 1;
/*      */         
/* 1403 */         setUserData(key, null);
/*      */       }
/*      */     }
/*      */     finally {
/* 1407 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getEffectiveUploadPriority()
/*      */   {
/* 1414 */     return this.upload_priority_manual + this.upload_priority_auto;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxUploads()
/*      */   {
/* 1420 */     return this.max_uploads;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMaxUploads(int max)
/*      */   {
/* 1427 */     this.download_manager_state.setIntParameter("max.uploads", max);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setManualUploadPriority(int priority)
/*      */   {
/* 1434 */     this.download_manager_state.setIntParameter("up.pri", priority);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getEffectiveMaxUploads()
/*      */   {
/* 1440 */     if ((isMaxUploadsWhenSeedingEnabled()) && (getState() == 60))
/*      */     {
/* 1442 */       return getMaxUploadsWhenSeeding();
/*      */     }
/*      */     
/*      */ 
/* 1446 */     return this.max_uploads;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getEffectiveUploadRateLimitBytesPerSecond()
/*      */   {
/* 1453 */     int local_max_bps = this.stats.getUploadRateLimitBytesPerSecond();
/* 1454 */     int rate = local_max_bps;
/*      */     
/* 1456 */     if (this.max_upload_when_busy_bps != 0)
/*      */     {
/* 1458 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 1460 */       if ((now < this.last_upload_when_busy_update) || (now - this.last_upload_when_busy_update > 5000L))
/*      */       {
/* 1462 */         this.last_upload_when_busy_update = now;
/*      */         
/*      */ 
/*      */ 
/* 1466 */         String key = TransferSpeedValidator.getActiveUploadParameter(this.globalManager);
/*      */         
/* 1468 */         int global_limit_bps = COConfigurationManager.getIntParameter(key) * 1024;
/*      */         
/* 1470 */         if ((global_limit_bps > 0) && (this.max_upload_when_busy_bps < global_limit_bps))
/*      */         {
/*      */ 
/*      */ 
/* 1474 */           local_max_bps = local_max_bps == 0 ? global_limit_bps : local_max_bps;
/*      */           
/* 1476 */           GlobalManagerStats gm_stats = this.globalManager.getStats();
/*      */           
/* 1478 */           int actual = gm_stats.getDataSendRateNoLAN() + gm_stats.getProtocolSendRateNoLAN();
/*      */           
/* 1480 */           int move_by = (local_max_bps - this.max_upload_when_busy_bps) / 10;
/*      */           
/* 1482 */           if (move_by < 1024)
/*      */           {
/* 1484 */             move_by = 1024;
/*      */           }
/*      */           
/* 1487 */           if (global_limit_bps - actual <= 2048)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1492 */             if (this.current_upload_when_busy_bps == 0)
/*      */             {
/* 1494 */               this.current_upload_when_busy_bps = local_max_bps;
/*      */             }
/*      */             
/* 1497 */             int prev_upload_when_busy_bps = this.current_upload_when_busy_bps;
/*      */             
/* 1499 */             this.current_upload_when_busy_bps -= move_by;
/*      */             
/* 1501 */             if (this.current_upload_when_busy_bps < this.max_upload_when_busy_bps)
/*      */             {
/* 1503 */               this.current_upload_when_busy_bps = this.max_upload_when_busy_bps;
/*      */             }
/*      */             
/* 1506 */             if (this.current_upload_when_busy_bps < prev_upload_when_busy_bps)
/*      */             {
/* 1508 */               this.last_upload_when_busy_dec_time = now;
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */           }
/* 1514 */           else if (this.current_upload_when_busy_bps != 0)
/*      */           {
/*      */ 
/*      */ 
/* 1518 */             if ((upload_when_busy_min_secs == 0) || (now < this.last_upload_when_busy_dec_time) || (now - this.last_upload_when_busy_dec_time >= upload_when_busy_min_secs * 1000L))
/*      */             {
/*      */ 
/*      */ 
/* 1522 */               this.current_upload_when_busy_bps += move_by;
/*      */               
/* 1524 */               if (this.current_upload_when_busy_bps >= local_max_bps)
/*      */               {
/* 1526 */                 this.current_upload_when_busy_bps = 0;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1532 */           if (this.current_upload_when_busy_bps > 0)
/*      */           {
/* 1534 */             rate = this.current_upload_when_busy_bps;
/*      */           }
/*      */         }
/*      */         else {
/* 1538 */           this.current_upload_when_busy_bps = 0;
/*      */         }
/*      */         
/*      */       }
/* 1542 */       else if (this.current_upload_when_busy_bps > 0)
/*      */       {
/* 1544 */         rate = this.current_upload_when_busy_bps;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1549 */     return rate;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setFileLinks()
/*      */   {
/* 1557 */     this.cached_save_location = null;
/*      */     
/* 1559 */     DiskManagerFactory.setFileLinks(this, this.download_manager_state.getFileLinks());
/*      */     
/* 1561 */     this.controller.fileInfoChanged();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void clearFileLinks()
/*      */   {
/* 1567 */     this.download_manager_state.clearFileLinks();
/*      */   }
/*      */   
/*      */   private void updateFileLinks(File old_save_path, File new_save_path)
/*      */   {
/*      */     try
/*      */     {
/* 1574 */       old_save_path = old_save_path.getCanonicalFile();
/* 1575 */     } catch (IOException ioe) { old_save_path = old_save_path.getAbsoluteFile(); }
/* 1576 */     try { new_save_path = new_save_path.getCanonicalFile();
/* 1577 */     } catch (IOException ioe) { new_save_path = new_save_path.getAbsoluteFile();
/*      */     }
/* 1579 */     String old_path = old_save_path.getPath();
/* 1580 */     String new_path = new_save_path.getPath();
/*      */     
/*      */ 
/*      */ 
/* 1584 */     LinkFileMap links = this.download_manager_state.getFileLinks();
/*      */     
/* 1586 */     Iterator<LinkFileMap.Entry> it = links.entryIterator();
/*      */     
/* 1588 */     List<Integer> from_indexes = new ArrayList();
/* 1589 */     List<File> from_links = new ArrayList();
/* 1590 */     List<File> to_links = new ArrayList();
/*      */     
/* 1592 */     while (it.hasNext())
/*      */     {
/* 1594 */       LinkFileMap.Entry entry = (LinkFileMap.Entry)it.next();
/*      */       
/*      */       try
/*      */       {
/* 1598 */         File to = entry.getToFile();
/*      */         
/* 1600 */         if (to != null)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 1608 */             to = to.getCanonicalFile();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/* 1612 */           int file_index = entry.getIndex();
/* 1613 */           File from = entry.getFromFile();
/*      */           try
/*      */           {
/* 1616 */             from = from.getCanonicalFile();
/*      */           }
/*      */           catch (Throwable e) {}
/*      */           
/* 1620 */           String from_s = from.getAbsolutePath();
/* 1621 */           String to_s = to.getAbsolutePath();
/*      */           
/* 1623 */           updateFileLink(file_index, old_path, new_path, from_s, to_s, from_indexes, from_links, to_links);
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/* 1627 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1631 */     if (from_links.size() > 0)
/*      */     {
/* 1633 */       this.download_manager_state.setFileLinks(from_indexes, from_links, to_links);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateFileLink(int file_index, String old_path, String new_path, String from_loc, String to_loc, List<Integer> from_indexes, List<File> from_links, List<File> to_links)
/*      */   {
/* 1658 */     if (this.torrent.isSimpleTorrent())
/*      */     {
/* 1660 */       if (!old_path.equals(from_loc))
/*      */       {
/* 1662 */         throw new RuntimeException("assert failure: old_path=" + old_path + ", from_loc=" + from_loc);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1667 */       from_indexes.add(Integer.valueOf(0));
/* 1668 */       from_links.add(new File(old_path));
/* 1669 */       to_links.add(null);
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
/* 1682 */       int pos = new_path.lastIndexOf(File.separatorChar);
/* 1683 */       String new_path_parent; String new_path_parent; if (pos != -1) {
/* 1684 */         new_path_parent = new_path.substring(0, pos);
/*      */       } else {
/* 1686 */         Debug.out("new_path " + new_path + " missing file separator, not good");
/*      */         
/* 1688 */         new_path_parent = new_path;
/*      */       }
/*      */       
/*      */ 
/* 1692 */       pos = to_loc.lastIndexOf(File.separatorChar);
/* 1693 */       String to_loc_name; String to_loc_name; if (pos != -1) {
/* 1694 */         to_loc_name = to_loc.substring(pos + 1);
/*      */       } else {
/* 1696 */         Debug.out("to_loc " + to_loc + " missing file separator, not good");
/*      */         
/* 1698 */         to_loc_name = to_loc;
/*      */       }
/*      */       
/* 1701 */       String to_loc_to_use = new_path_parent + File.separatorChar + to_loc_name;
/*      */       
/*      */ 
/* 1704 */       from_indexes.add(Integer.valueOf(0));
/* 1705 */       from_links.add(new File(new_path));
/* 1706 */       to_links.add(new File(to_loc_to_use));
/*      */     }
/*      */     else
/*      */     {
/* 1710 */       String from_loc_to_use = FileUtil.translateMoveFilePath(old_path, new_path, from_loc);
/*      */       
/* 1712 */       if (from_loc_to_use == null)
/*      */       {
/* 1714 */         return;
/*      */       }
/*      */       
/* 1717 */       String to_loc_to_use = FileUtil.translateMoveFilePath(old_path, new_path, to_loc);
/*      */       
/* 1719 */       if (to_loc_to_use == null)
/*      */       {
/* 1721 */         to_loc_to_use = to_loc;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1726 */       from_indexes.add(Integer.valueOf(file_index));
/* 1727 */       from_links.add(new File(from_loc));
/* 1728 */       to_links.add(null);
/*      */       
/*      */ 
/*      */ 
/* 1732 */       from_indexes.add(Integer.valueOf(file_index));
/* 1733 */       from_links.add(new File(from_loc_to_use));
/* 1734 */       to_links.add(new File(to_loc_to_use));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public boolean filesExist()
/*      */   {
/* 1745 */     return filesExist(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean filesExist(boolean expected_to_be_allocated)
/*      */   {
/* 1752 */     return this.controller.filesExist(expected_to_be_allocated);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPersistent()
/*      */   {
/* 1759 */     return this.persistent;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDisplayName()
/*      */   {
/* 1765 */     DownloadManagerState dms = getDownloadState();
/* 1766 */     if (dms != null) {
/* 1767 */       String result = dms.getDisplayName();
/* 1768 */       if (result != null) return result;
/*      */     }
/* 1770 */     return this.display_name;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getInternalName()
/*      */   {
/* 1776 */     return this.internal_name;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getErrorDetails()
/*      */   {
/* 1782 */     return this.controller.getErrorDetail();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getErrorType()
/*      */   {
/* 1788 */     return this.controller.getErrorType();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSize()
/*      */   {
/* 1794 */     if (this.torrent != null)
/*      */     {
/* 1796 */       return this.torrent.getSize();
/*      */     }
/*      */     
/* 1799 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setFailed()
/*      */   {
/* 1805 */     setFailed((String)null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setFailed(Throwable e)
/*      */   {
/* 1812 */     setFailed(Debug.getNestedExceptionMessage(e));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setFailed(String str)
/*      */   {
/* 1819 */     this.controller.setFailed(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setTorrentInvalid(String str)
/*      */   {
/* 1826 */     setFailed(str);
/*      */     
/* 1828 */     this.torrent = null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void saveResumeData()
/*      */   {
/* 1835 */     if (getState() == 50) {
/*      */       try
/*      */       {
/* 1838 */         getDiskManager().saveResumeData(true);
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1842 */         setFailed("Resume data save fails: " + Debug.getNestedExceptionMessage(e));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1848 */     if (!this.assumedComplete)
/*      */     {
/* 1850 */       this.download_manager_state.save();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void saveDownload()
/*      */   {
/* 1857 */     DiskManager disk_manager = this.controller.getDiskManager();
/*      */     
/* 1859 */     if (disk_manager != null)
/*      */     {
/* 1861 */       disk_manager.saveState();
/*      */     }
/*      */     
/* 1864 */     this.download_manager_state.save();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void initialize()
/*      */   {
/* 1874 */     if (this.torrent == null)
/*      */     {
/*      */ 
/*      */ 
/* 1878 */       readTorrent();
/*      */     }
/*      */     
/* 1881 */     if (this.torrent == null)
/*      */     {
/* 1883 */       setFailed();
/*      */       
/* 1885 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1891 */     if ((this.assumedComplete) && (!filesExist(true)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1896 */       return;
/*      */     }
/*      */     
/* 1899 */     this.download_manager_state.setActive(true);
/*      */     try
/*      */     {
/*      */       try {
/* 1903 */         this.this_mon.enter();
/*      */         
/* 1905 */         if (this.tracker_client != null)
/*      */         {
/* 1907 */           Debug.out("DownloadManager: initialize called with tracker client still available");
/*      */           
/* 1909 */           this.tracker_client.destroy();
/*      */         }
/*      */         
/* 1912 */         this.tracker_client = org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory.create(this.torrent, new TRTrackerAnnouncerFactory.DataProvider()
/*      */         {
/*      */ 
/*      */ 
/*      */           public String[] getNetworks()
/*      */           {
/*      */ 
/*      */ 
/* 1920 */             return DownloadManagerImpl.this.download_manager_state.getNetworks();
/*      */           }
/*      */           
/* 1923 */         });
/* 1924 */         this.tracker_client.setTrackerResponseCache(this.download_manager_state.getTrackerResponseCache());
/*      */         
/* 1926 */         this.tracker_client.addListener(this.tracker_client_listener);
/*      */       }
/*      */       finally
/*      */       {
/* 1930 */         this.this_mon.exit();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1938 */         this.controller.initializeDiskManager(this.open_for_seeding);
/*      */ 
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*      */ 
/* 1945 */         this.open_for_seeding = false;
/*      */       }
/*      */     }
/*      */     catch (TRTrackerAnnouncerException e)
/*      */     {
/* 1950 */       setFailed(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setStateWaiting()
/*      */   {
/* 1958 */     checkResuming();
/*      */     
/* 1960 */     this.controller.setStateWaiting();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setStateFinishing()
/*      */   {
/* 1966 */     this.controller.setStateFinishing();
/*      */   }
/*      */   
/*      */ 
/*      */   public void setStateQueued()
/*      */   {
/* 1972 */     checkResuming();
/*      */     
/* 1974 */     this.controller.setStateQueued();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getState()
/*      */   {
/* 1980 */     return this.controller.getState();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSubState()
/*      */   {
/* 1986 */     return this.controller.getSubState();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canForceRecheck()
/*      */   {
/* 1992 */     if (getTorrent() == null)
/*      */     {
/*      */ 
/*      */ 
/* 1996 */       return false;
/*      */     }
/*      */     
/* 1999 */     return this.controller.canForceRecheck();
/*      */   }
/*      */   
/*      */ 
/*      */   public void forceRecheck()
/*      */   {
/* 2005 */     this.controller.forceRecheck(null);
/*      */   }
/*      */   
/*      */ 
/*      */   public void forceRecheck(ForceRecheckListener l)
/*      */   {
/* 2011 */     this.controller.forceRecheck(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPieceCheckingEnabled(boolean enabled)
/*      */   {
/* 2018 */     this.controller.setPieceCheckingEnabled(enabled);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void resetFile(DiskManagerFileInfo file)
/*      */   {
/* 2025 */     int state = getState();
/*      */     
/* 2027 */     if ((state == 70) || (state == 100))
/*      */     {
/*      */ 
/* 2030 */       DiskManagerFactory.clearResumeData(this, file);
/*      */     }
/*      */     else
/*      */     {
/* 2034 */       Debug.out("Download not stopped");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void recheckFile(DiskManagerFileInfo file)
/*      */   {
/* 2042 */     int state = getState();
/*      */     
/* 2044 */     if ((state == 70) || (state == 100))
/*      */     {
/*      */ 
/* 2047 */       DiskManagerFactory.recheckFile(this, file);
/*      */     }
/*      */     else
/*      */     {
/* 2051 */       Debug.out("Download not stopped");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void startDownload()
/*      */   {
/* 2058 */     this.message_mode = -1;
/*      */     
/* 2060 */     this.controller.startDownload(getTrackerClient());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stopIt(int state_after_stopping, boolean remove_torrent, boolean remove_data)
/*      */   {
/* 2069 */     stopIt(state_after_stopping, remove_torrent, remove_data, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void stopIt(int state_after_stopping, boolean remove_torrent, boolean remove_data, boolean for_removal)
/*      */   {
/* 2079 */     if (for_removal)
/*      */     {
/* 2081 */       this.removing = true;
/*      */     }
/*      */     try
/*      */     {
/* 2085 */       boolean closing = state_after_stopping == 71;
/* 2086 */       int curState = getState();
/* 2087 */       boolean alreadyStopped = (curState == 70) || (curState == 65) || (curState == 100);
/*      */       
/* 2089 */       boolean skipSetTimeStopped = (alreadyStopped) || ((closing) && (curState == 75));
/*      */       
/*      */ 
/* 2092 */       if (!skipSetTimeStopped) {
/* 2093 */         this.download_manager_state.setLongAttribute("timestopped", SystemTime.getCurrentTime());
/*      */       }
/*      */       
/*      */ 
/* 2097 */       this.controller.stopIt(state_after_stopping, remove_torrent, remove_data, for_removal);
/*      */     }
/*      */     finally
/*      */     {
/* 2101 */       this.download_manager_state.setActive(false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkResuming()
/*      */   {
/* 2108 */     this.globalManager.resumingDownload(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean pause()
/*      */   {
/* 2114 */     return this.globalManager.pauseDownload(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean pause(long _resume_time)
/*      */   {
/* 2125 */     this.resume_time = (-_resume_time);
/*      */     
/* 2127 */     return this.globalManager.pauseDownload(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public long getAutoResumeTime()
/*      */   {
/* 2133 */     return this.resume_time;
/*      */   }
/*      */   
/*      */   public boolean isPaused()
/*      */   {
/* 2138 */     return this.globalManager.isPaused(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public void resume()
/*      */   {
/* 2144 */     this.globalManager.resumeDownload(this);
/*      */   }
/*      */   
/*      */   public boolean getAssumedComplete() {
/* 2148 */     return this.assumedComplete;
/*      */   }
/*      */   
/*      */   public boolean requestAssumedCompleteMode() {
/* 2152 */     boolean bCompleteNoDND = this.controller.isDownloadComplete(false);
/*      */     
/* 2154 */     setAssumedComplete(bCompleteNoDND);
/* 2155 */     return bCompleteNoDND;
/*      */   }
/*      */   
/*      */   protected void setAssumedComplete(boolean _assumedComplete)
/*      */   {
/* 2160 */     if (_assumedComplete) {
/* 2161 */       long completedOn = this.download_manager_state.getLongParameter("stats.download.completed.time");
/* 2162 */       if (completedOn <= 0L) {
/* 2163 */         this.download_manager_state.setLongParameter("stats.download.completed.time", SystemTime.getCurrentTime());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2169 */     if (this.assumedComplete == _assumedComplete) {
/* 2170 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2176 */     this.assumedComplete = _assumedComplete;
/*      */     
/* 2178 */     if (!this.assumedComplete) {
/* 2179 */       this.controller.setStateDownloading();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2185 */     if (this.position != -1)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2192 */       DownloadManager[] dms = { this };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2197 */       this.position = (this.globalManager.getDownloadManagers().size() + 1);
/*      */       
/* 2199 */       if (COConfigurationManager.getBooleanParameter("Newly Seeding Torrents Get First Priority"))
/*      */       {
/* 2201 */         this.globalManager.moveTop(dms);
/*      */       }
/*      */       else
/*      */       {
/* 2205 */         this.globalManager.moveEnd(dms);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2210 */       this.globalManager.fixUpDownloadManagerPositions();
/*      */     }
/*      */     
/* 2213 */     this.listeners.dispatch(3, new Object[] { this, Boolean.valueOf(_assumedComplete) });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getNbSeeds()
/*      */   {
/* 2223 */     PEPeerManager peerManager = this.controller.getPeerManager();
/*      */     
/* 2225 */     if (peerManager != null)
/*      */     {
/* 2227 */       return peerManager.getNbSeeds();
/*      */     }
/*      */     
/* 2230 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPeers()
/*      */   {
/* 2236 */     PEPeerManager peerManager = this.controller.getPeerManager();
/*      */     
/* 2238 */     if (peerManager != null)
/*      */     {
/* 2240 */       return peerManager.getNbPeers();
/*      */     }
/*      */     
/* 2243 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTrackerStatus()
/*      */   {
/* 2251 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 2253 */     if (tc != null)
/*      */     {
/* 2255 */       return tc.getStatusString();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2260 */     if (this.torrent != null)
/*      */     {
/* 2262 */       TRTrackerScraperResponse response = getTrackerScrapeResponse();
/*      */       
/* 2264 */       if (response != null) {
/* 2265 */         return response.getStatusString();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2270 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerAnnouncer getTrackerClient()
/*      */   {
/* 2276 */     return this.tracker_client;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setAnnounceResult(DownloadAnnounceResult result)
/*      */   {
/* 2283 */     TRTrackerAnnouncer cl = getTrackerClient();
/*      */     
/* 2285 */     if (cl == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2290 */       return;
/*      */     }
/*      */     
/* 2293 */     cl.setAnnounceResult(result);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setScrapeResult(DownloadScrapeResult result)
/*      */   {
/* 2300 */     if ((this.torrent != null) && (result != null))
/*      */     {
/* 2302 */       TRTrackerScraper scraper = this.globalManager.getTrackerScraper();
/*      */       
/* 2304 */       TRTrackerScraperResponse current_resp = getTrackerScrapeResponse();
/*      */       
/*      */       URL target_url;
/*      */       URL target_url;
/* 2308 */       if (current_resp != null)
/*      */       {
/* 2310 */         target_url = current_resp.getURL();
/*      */       }
/*      */       else
/*      */       {
/* 2314 */         target_url = this.torrent.getAnnounceURL();
/*      */       }
/*      */       
/* 2317 */       scraper.setScrape(this.torrent, target_url, result);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPieces()
/*      */   {
/* 2324 */     if (this.torrent == null)
/*      */     {
/* 2326 */       return 0;
/*      */     }
/*      */     
/* 2329 */     return this.torrent.getNumberOfPieces();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getTrackerTime()
/*      */   {
/* 2336 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 2338 */     if (tc != null)
/*      */     {
/* 2340 */       return tc.getTimeUntilNextUpdate();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2345 */     if (this.torrent != null)
/*      */     {
/* 2347 */       TRTrackerScraperResponse response = getTrackerScrapeResponse();
/*      */       
/* 2349 */       if (response != null)
/*      */       {
/* 2351 */         if (response.getStatus() == 3)
/*      */         {
/* 2353 */           return -1;
/*      */         }
/*      */         
/* 2356 */         return (int)((response.getNextScrapeStartTime() - SystemTime.getCurrentTime()) / 1000L);
/*      */       }
/*      */     }
/*      */     
/* 2360 */     return 60;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TOTorrent getTorrent()
/*      */   {
/* 2367 */     return this.torrent;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public File getSaveLocation()
/*      */   {
/* 2378 */     File save_location = this.torrent_save_location;
/*      */     
/* 2380 */     if (save_location == this.cached_save_location)
/*      */     {
/* 2382 */       return this.cached_save_location_result;
/*      */     }
/*      */     
/*      */     File res;
/*      */     File res;
/* 2387 */     if ((this.torrent == null) || (this.torrent.isSimpleTorrent()))
/*      */     {
/* 2389 */       res = this.download_manager_state.getFileLink(0, save_location);
/*      */     }
/*      */     else
/*      */     {
/* 2393 */       res = save_location;
/*      */     }
/*      */     
/* 2396 */     if ((res == null) || (res.equals(save_location)))
/*      */     {
/* 2398 */       res = save_location;
/*      */     } else {
/*      */       try
/*      */       {
/* 2402 */         res = res.getCanonicalFile();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2406 */         res = res.getAbsoluteFile();
/*      */       }
/*      */     }
/*      */     
/* 2410 */     this.cached_save_location = save_location;
/* 2411 */     this.cached_save_location_result = res;
/*      */     
/* 2413 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public File getAbsoluteSaveLocation()
/*      */   {
/* 2419 */     return this.torrent_save_location;
/*      */   }
/*      */   
/*      */   public void setTorrentSaveDir(String new_dir) {
/* 2423 */     setTorrentSaveDir(new_dir, getAbsoluteSaveLocation().getName());
/*      */   }
/*      */   
/*      */   public void setTorrentSaveDir(String new_dir, String dl_name) {
/* 2427 */     File old_location = this.torrent_save_location;
/* 2428 */     File new_location = new File(new_dir, dl_name);
/*      */     
/* 2430 */     if (new_location.equals(old_location)) {
/* 2431 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2440 */     updateFileLinks(old_location, new_location);
/*      */     
/* 2442 */     this.torrent_save_location = new_location;
/*      */     
/* 2444 */     String key = this.torrent_save_location.getAbsolutePath() + "\n";
/*      */     try
/*      */     {
/* 2447 */       this.torrent_save_location = this.torrent_save_location.getCanonicalFile();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 2451 */       this.torrent_save_location = this.torrent_save_location.getAbsoluteFile();
/*      */     }
/*      */     
/* 2454 */     this.download_manager_state.setAttribute("canosavedir", key + this.torrent_save_location.getAbsolutePath());
/*      */     
/*      */ 
/*      */ 
/* 2458 */     Logger.log(new LogEvent(this, org.gudy.azureus2.core3.logging.LogIDs.CORE, "Torrent save directory changing from \"" + old_location.getPath() + "\" to \"" + new_location.getPath()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2464 */     this.controller.fileInfoChanged();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getPieceLength()
/*      */   {
/* 2470 */     if (this.torrent != null) {
/* 2471 */       return DisplayFormatters.formatByteCountToKiBEtc(this.torrent.getPieceLength());
/*      */     }
/*      */     
/* 2474 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTorrentFileName()
/*      */   {
/* 2480 */     return this.torrentFileName;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTorrentFileName(String string)
/*      */   {
/* 2487 */     this.torrentFileName = string;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTrackerScrapeResponse(TRTrackerScraperResponse response)
/*      */   {
/* 2499 */     Object[] res = getActiveScrapeResponse();
/*      */     
/* 2501 */     URL active_url = (URL)res[1];
/*      */     
/* 2503 */     if ((active_url != null) && (this.torrent != null))
/*      */     {
/* 2505 */       this.torrent.setAnnounceURL(active_url);
/*      */     }
/*      */     
/* 2508 */     if (response != null) {
/* 2509 */       int state = getState();
/* 2510 */       if ((state == 100) || (state == 70)) { long minNextScrape;
/*      */         long minNextScrape;
/* 2512 */         if (response.getStatus() == 0) {
/* 2513 */           minNextScrape = SystemTime.getCurrentTime() + (state == 100 ? 600000L : 180000L);
/*      */         }
/*      */         else
/*      */         {
/* 2517 */           minNextScrape = SystemTime.getCurrentTime() + (state == 100 ? 7200000L : 3600000L);
/*      */         }
/*      */         
/*      */ 
/* 2521 */         if (response.getNextScrapeStartTime() < minNextScrape) {
/* 2522 */           response.setNextScrapeStartTime(minNextScrape);
/*      */         }
/* 2524 */       } else if ((!response.isValid()) && (response.getStatus() == 0))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2531 */         int sr = getStats().getShareRatio();
/* 2532 */         long minNextScrape = SystemTime.getCurrentTime() + (sr > 10000 ? 10000 : sr + 1000) * 60;
/*      */         
/*      */ 
/* 2535 */         if (response.getNextScrapeStartTime() < minNextScrape) {
/* 2536 */           response.setNextScrapeStartTime(minNextScrape);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2543 */       if ((response.isValid()) && (response.getStatus() == 2))
/*      */       {
/* 2545 */         long cache = (response.getSeeds() & 0xFFFFFF) << 32 | response.getPeers() & 0xFFFFFF;
/*      */         
/* 2547 */         this.download_manager_state.setLongAttribute("scrapecache", cache);
/*      */       }
/*      */       
/* 2550 */       this.tracker_listeners.dispatch(2, response);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public TRTrackerScraperResponse getTrackerScrapeResponse()
/*      */   {
/* 2557 */     Object[] res = getActiveScrapeResponse();
/*      */     
/* 2559 */     return (TRTrackerScraperResponse)res[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object[] getActiveScrapeResponse()
/*      */   {
/* 2571 */     TRTrackerScraperResponse response = null;
/* 2572 */     URL active_url = null;
/*      */     
/* 2574 */     TRTrackerScraper scraper = this.globalManager.getTrackerScraper();
/*      */     
/* 2576 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 2578 */     if (tc != null)
/*      */     {
/* 2580 */       response = scraper.scrape(tc);
/*      */     }
/*      */     
/* 2583 */     if ((response == null) && (this.torrent != null))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2588 */       TRTrackerScraperResponse non_null_response = null;
/*      */       TOTorrentAnnounceURLSet[] sets;
/*      */       try
/*      */       {
/* 2592 */         sets = this.torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*      */       } catch (Exception e) {
/* 2594 */         return new Object[] { scraper.scrape(this.torrent), active_url };
/*      */       }
/*      */       
/* 2597 */       if (sets.length == 0)
/*      */       {
/* 2599 */         response = scraper.scrape(this.torrent);
/*      */       }
/*      */       else
/*      */       {
/* 2603 */         URL backup_url = null;
/* 2604 */         TRTrackerScraperResponse backup_response = null;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2611 */         Random scrape_random = new Random(this.scrape_random_seed);
/*      */         
/* 2613 */         for (int i = 0; (response == null) && (i < sets.length); i++)
/*      */         {
/* 2615 */           TOTorrentAnnounceURLSet set = sets[i];
/*      */           
/* 2617 */           URL[] urls = set.getAnnounceURLs();
/*      */           
/* 2619 */           List rand_urls = new ArrayList();
/*      */           
/* 2621 */           for (int j = 0; j < urls.length; j++)
/*      */           {
/* 2623 */             URL url = urls[j];
/*      */             
/* 2625 */             int pos = (int)(scrape_random.nextDouble() * (rand_urls.size() + 1));
/*      */             
/* 2627 */             rand_urls.add(pos, url);
/*      */           }
/*      */           
/* 2630 */           for (int j = 0; (response == null) && (j < rand_urls.size()); j++)
/*      */           {
/* 2632 */             URL url = (URL)rand_urls.get(j);
/*      */             
/* 2634 */             response = scraper.scrape(this.torrent, url);
/*      */             
/* 2636 */             if (response != null)
/*      */             {
/* 2638 */               int status = response.getStatus();
/*      */               
/*      */ 
/*      */ 
/* 2642 */               if (status == 2)
/*      */               {
/* 2644 */                 if (response.isDHTBackup())
/*      */                 {
/*      */ 
/*      */ 
/* 2648 */                   backup_url = url;
/* 2649 */                   backup_response = response;
/*      */                   
/* 2651 */                   response = null;
/*      */ 
/*      */                 }
/*      */                 else
/*      */                 {
/* 2656 */                   active_url = url;
/*      */                   
/* 2658 */                   break;
/*      */                 }
/*      */                 
/*      */               }
/*      */               else
/*      */               {
/* 2664 */                 if ((status == 0) || (status == 3)) {
/*      */                   break;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2673 */                 if ((!response.isValid()) || (status == 1))
/*      */                 {
/* 2675 */                   if (non_null_response == null)
/*      */                   {
/* 2677 */                     non_null_response = response;
/*      */                   }
/*      */                   
/* 2680 */                   response = null;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2686 */         if (response == null)
/*      */         {
/* 2688 */           if (backup_response != null)
/*      */           {
/* 2690 */             response = backup_response;
/* 2691 */             active_url = backup_url;
/*      */           }
/*      */           else
/*      */           {
/* 2695 */             response = non_null_response;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2701 */     return new Object[] { response, active_url };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<TRTrackerScraperResponse> getGoodTrackerScrapeResponses()
/*      */   {
/* 2708 */     List<TRTrackerScraperResponse> responses = new ArrayList();
/*      */     
/* 2710 */     if (this.torrent != null)
/*      */     {
/* 2712 */       TRTrackerScraper scraper = this.globalManager.getTrackerScraper();
/*      */       
/*      */       TOTorrentAnnounceURLSet[] sets;
/*      */       
/*      */       try
/*      */       {
/* 2718 */         sets = this.torrent.getAnnounceURLGroup().getAnnounceURLSets();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 2722 */         sets = new TOTorrentAnnounceURLSet[0];
/*      */       }
/*      */       
/* 2725 */       if (sets.length == 0)
/*      */       {
/* 2727 */         TRTrackerScraperResponse response = scraper.peekScrape(this.torrent, null);
/*      */         
/* 2729 */         if (response != null)
/*      */         {
/* 2731 */           int status = response.getStatus();
/*      */           
/* 2733 */           if (status == 2)
/*      */           {
/* 2735 */             responses.add(response);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 2740 */         for (int i = 0; i < sets.length; i++)
/*      */         {
/* 2742 */           TOTorrentAnnounceURLSet set = sets[i];
/*      */           
/* 2744 */           URL[] urls = set.getAnnounceURLs();
/*      */           
/* 2746 */           for (URL url : urls)
/*      */           {
/* 2748 */             TRTrackerScraperResponse response = scraper.peekScrape(this.torrent, url);
/*      */             
/* 2750 */             if (response != null)
/*      */             {
/* 2752 */               int status = response.getStatus();
/*      */               
/* 2754 */               if (status == 2)
/*      */               {
/*      */ 
/* 2757 */                 responses.add(response);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2765 */     return responses;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestTrackerAnnounce(boolean force)
/*      */   {
/* 2773 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 2775 */     if (tc != null)
/*      */     {
/* 2777 */       tc.update(force);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void requestTrackerScrape(boolean force)
/*      */   {
/* 2784 */     if (this.torrent != null)
/*      */     {
/* 2786 */       TRTrackerScraper scraper = this.globalManager.getTrackerScraper();
/*      */       
/* 2788 */       scraper.scrape(this.torrent, force);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setTrackerRefreshDelayOverrides(int percent)
/*      */   {
/* 2796 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 2798 */     if (tc != null)
/*      */     {
/* 2800 */       tc.setRefreshDelayOverrides(percent);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean activateRequest(int count)
/*      */   {
/* 2810 */     for (Iterator it = this.activation_listeners.iterator(); it.hasNext();)
/*      */     {
/* 2812 */       DownloadManagerActivationListener listener = (DownloadManagerActivationListener)it.next();
/*      */       
/*      */       try
/*      */       {
/* 2816 */         if (listener.activateRequest(count))
/*      */         {
/* 2818 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 2822 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 2826 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getActivationCount()
/*      */   {
/* 2832 */     return this.controller.getActivationCount();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTorrentComment()
/*      */   {
/* 2838 */     return this.torrent_comment;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTorrentCreatedBy()
/*      */   {
/* 2844 */     return this.torrent_created_by;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTorrentCreationDate()
/*      */   {
/* 2850 */     if (this.torrent == null) {
/* 2851 */       return 0L;
/*      */     }
/*      */     
/* 2854 */     return this.torrent.getCreationDate();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public GlobalManager getGlobalManager()
/*      */   {
/* 2861 */     return this.globalManager;
/*      */   }
/*      */   
/*      */ 
/*      */   public DiskManager getDiskManager()
/*      */   {
/* 2867 */     return this.controller.getDiskManager();
/*      */   }
/*      */   
/*      */   public DiskManagerFileInfoSet getDiskManagerFileInfoSet()
/*      */   {
/* 2872 */     return this.controller.getDiskManagerFileInfoSet();
/*      */   }
/*      */   
/*      */ 
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   public DiskManagerFileInfo[] getDiskManagerFileInfo()
/*      */   {
/* 2881 */     return this.controller.getDiskManagerFileInfo();
/*      */   }
/*      */   
/*      */   public int getNumFileInfos()
/*      */   {
/* 2886 */     return this.torrent == null ? 0 : this.torrent.getFileCount();
/*      */   }
/*      */   
/*      */ 
/*      */   public PEPeerManager getPeerManager()
/*      */   {
/* 2892 */     return this.controller.getPeerManager();
/*      */   }
/*      */   
/*      */   public boolean isDownloadComplete(boolean bIncludeDND) {
/* 2896 */     if (!bIncludeDND) {
/* 2897 */       return this.assumedComplete;
/*      */     }
/*      */     
/* 2900 */     return this.controller.isDownloadComplete(bIncludeDND);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DownloadManagerListener listener)
/*      */   {
/* 2907 */     addListener(listener, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(DownloadManagerListener listener, boolean triggerStateChange)
/*      */   {
/* 2915 */     if (listener == null) {
/* 2916 */       Debug.out("Warning: null listener");
/* 2917 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2921 */       this.listeners_mon.enter();
/*      */       
/* 2923 */       this.listeners.addListener(listener);
/*      */       
/* 2925 */       if (triggerStateChange)
/*      */       {
/* 2927 */         this.listeners.dispatch(listener, 1, new Object[] { this, new Integer(getState()) });
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */ 
/* 2935 */       Debug.out("adding listener", t);
/*      */     }
/*      */     finally
/*      */     {
/* 2939 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(DownloadManagerListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 2948 */       this.listeners_mon.enter();
/*      */       
/* 2950 */       this.listeners.removeListener(listener);
/*      */     }
/*      */     finally
/*      */     {
/* 2954 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informStateChanged()
/*      */   {
/*      */     try
/*      */     {
/* 2966 */       this.listeners_mon.enter();
/*      */       
/* 2968 */       int new_state = this.controller.getState();
/* 2969 */       boolean new_force_start = this.controller.isForceStart();
/*      */       
/* 2971 */       if ((new_state != this.last_informed_state) || (new_force_start != this.latest_informed_force_start))
/*      */       {
/*      */ 
/* 2974 */         this.last_informed_state = new_state;
/*      */         
/* 2976 */         this.latest_informed_force_start = new_force_start;
/*      */         
/* 2978 */         if (this.resume_time < 0L)
/*      */         {
/* 2980 */           if (new_state == 70)
/*      */           {
/* 2982 */             this.resume_time = (-this.resume_time);
/*      */           }
/*      */         }
/*      */         else {
/* 2986 */           this.resume_time = 0L;
/*      */         }
/*      */         
/*      */ 
/* 2990 */         this.listeners.dispatch(1, new Object[] { this, new Integer(new_state) });
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2995 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void informDownloadEnded()
/*      */   {
/*      */     try
/*      */     {
/* 3003 */       this.listeners_mon.enter();
/*      */       
/* 3005 */       this.listeners.dispatch(2, new Object[] { this });
/*      */     }
/*      */     finally
/*      */     {
/* 3009 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   void informPrioritiesChange(List files)
/*      */   {
/* 3016 */     this.controller.filePrioritiesChanged(files);
/*      */     try
/*      */     {
/* 3019 */       this.listeners_mon.enter();
/*      */       
/* 3021 */       for (int i = 0; i < files.size(); i++) {
/* 3022 */         this.listeners.dispatch(5, new Object[] { this, (DiskManagerFileInfo)files.get(i) });
/*      */       }
/*      */     }
/*      */     finally {
/* 3026 */       this.listeners_mon.exit();
/*      */     }
/*      */     
/* 3029 */     requestAssumedCompleteMode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informPriorityChange(DiskManagerFileInfo file)
/*      */   {
/* 3037 */     informPrioritiesChange(java.util.Collections.singletonList(file));
/*      */   }
/*      */   
/*      */ 
/*      */   protected void informPositionChanged(int new_position)
/*      */   {
/*      */     try
/*      */     {
/* 3045 */       this.listeners_mon.enter();
/*      */       
/* 3047 */       int old_position = this.position;
/*      */       
/* 3049 */       if (new_position != old_position)
/*      */       {
/* 3051 */         this.position = new_position;
/*      */         
/* 3053 */         this.listeners.dispatch(4, new Object[] { this, new Integer(old_position), new Integer(new_position) });
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 3058 */         if ((getState() == 60) || (getState() == 50)) {
/* 3059 */           PeerControlSchedulerFactory.updateScheduleOrdering();
/*      */         }
/*      */       }
/*      */     } finally {
/* 3063 */       this.listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPeerListener(DownloadManagerPeerListener listener)
/*      */   {
/* 3071 */     addPeerListener(listener, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPeerListener(DownloadManagerPeerListener listener, boolean bDispatchForExisting)
/*      */   {
/*      */     try
/*      */     {
/* 3080 */       this.peer_listeners_mon.enter();
/*      */       
/* 3082 */       this.peer_listeners.addListener(listener);
/*      */       
/* 3084 */       if (!bDispatchForExisting) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 3089 */       for (PEPeer peer : this.current_peers.keySet())
/*      */       {
/* 3091 */         this.peer_listeners.dispatch(listener, 1, peer);
/*      */       }
/*      */       
/* 3094 */       PEPeerManager temp = this.controller.getPeerManager();
/*      */       
/* 3096 */       if (temp != null)
/*      */       {
/* 3098 */         this.peer_listeners.dispatch(listener, 5, temp);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3103 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePeerListener(DownloadManagerPeerListener listener)
/*      */   {
/* 3111 */     this.peer_listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPieceListener(DownloadManagerPieceListener listener)
/*      */   {
/* 3118 */     addPieceListener(listener, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPieceListener(DownloadManagerPieceListener listener, boolean bDispatchForExisting)
/*      */   {
/*      */     try
/*      */     {
/* 3127 */       this.piece_listeners_mon.enter();
/*      */       
/* 3129 */       this.piece_listeners.addListener(listener);
/*      */       
/* 3131 */       if (!bDispatchForExisting) {
/*      */         return;
/*      */       }
/* 3134 */       for (int i = 0; i < this.current_pieces.size(); i++)
/*      */       {
/* 3136 */         this.piece_listeners.dispatch(listener, 3, this.current_pieces.get(i));
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3141 */       this.piece_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePieceListener(DownloadManagerPieceListener listener)
/*      */   {
/* 3149 */     this.piece_listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addPeer(PEPeer peer)
/*      */   {
/*      */     try
/*      */     {
/* 3159 */       this.peer_listeners_mon.enter();
/*      */       
/* 3161 */       if (this.current_peers_unmatched_removal.remove(peer) != null) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 3166 */       this.current_peers.put(peer, "");
/*      */       
/* 3168 */       this.peer_listeners.dispatch(1, peer);
/*      */     }
/*      */     finally
/*      */     {
/* 3172 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removePeer(PEPeer peer)
/*      */   {
/*      */     try
/*      */     {
/* 3181 */       this.peer_listeners_mon.enter();
/*      */       
/* 3183 */       if (this.current_peers.remove(peer) == null)
/*      */       {
/* 3185 */         long now = SystemTime.getMonotonousTime();
/*      */         
/* 3187 */         this.current_peers_unmatched_removal.put(peer, Long.valueOf(now));
/*      */         
/* 3189 */         if (this.current_peers_unmatched_removal.size() > 100)
/*      */         {
/* 3191 */           Iterator<Map.Entry<PEPeer, Long>> it = this.current_peers_unmatched_removal.entrySet().iterator();
/*      */           
/* 3193 */           while (it.hasNext())
/*      */           {
/* 3195 */             if (now - ((Long)((Map.Entry)it.next()).getValue()).longValue() > 10000L)
/*      */             {
/* 3197 */               Debug.out("Removing expired unmatched removal record");
/*      */               
/* 3199 */               it.remove();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3205 */       this.peer_listeners.dispatch(2, peer);
/*      */     }
/*      */     finally
/*      */     {
/* 3209 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3215 */     if (((peer.isSeed()) || (peer.isRelativeSeed())) && (isDownloadComplete(false)))
/*      */     {
/* 3217 */       TRTrackerAnnouncer announcer = this.tracker_client;
/*      */       
/* 3219 */       if (announcer != null)
/*      */       {
/* 3221 */         announcer.removeFromTrackerResponseCache(peer.getIp(), peer.getTCPListenPort());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public PEPeer[] getCurrentPeers()
/*      */   {
/*      */     try
/*      */     {
/* 3230 */       this.peer_listeners_mon.enter();
/*      */       
/* 3232 */       return (PEPeer[])this.current_peers.keySet().toArray(new PEPeer[this.current_peers.size()]);
/*      */     }
/*      */     finally
/*      */     {
/* 3236 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPiece(PEPiece piece)
/*      */   {
/*      */     try
/*      */     {
/* 3246 */       this.piece_listeners_mon.enter();
/*      */       
/* 3248 */       this.current_pieces.add(piece);
/*      */       
/* 3250 */       this.piece_listeners.dispatch(3, piece);
/*      */     }
/*      */     finally
/*      */     {
/* 3254 */       this.piece_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removePiece(PEPiece piece)
/*      */   {
/*      */     try
/*      */     {
/* 3263 */       this.piece_listeners_mon.enter();
/*      */       
/* 3265 */       this.current_pieces.remove(piece);
/*      */       
/* 3267 */       this.piece_listeners.dispatch(4, piece);
/*      */     }
/*      */     finally
/*      */     {
/* 3271 */       this.piece_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public PEPiece[] getCurrentPieces()
/*      */   {
/*      */     try
/*      */     {
/* 3279 */       this.piece_listeners_mon.enter();
/*      */       
/* 3281 */       return (PEPiece[])this.current_pieces.toArray(new PEPiece[this.current_pieces.size()]);
/*      */     }
/*      */     finally
/*      */     {
/* 3285 */       this.piece_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informWillBeStarted(PEPeerManager pm)
/*      */   {
/* 3297 */     List l = this.peer_listeners.getListenersCopy();
/*      */     
/* 3299 */     for (int i = 0; i < l.size(); i++) {
/*      */       try
/*      */       {
/* 3302 */         ((DownloadManagerPeerListener)l.get(i)).peerManagerWillBeAdded(pm);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3306 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void informStarted(PEPeerManager pm)
/*      */   {
/*      */     try
/*      */     {
/* 3316 */       this.peer_listeners_mon.enter();
/*      */       
/* 3318 */       this.peer_listeners.dispatch(5, pm);
/*      */     }
/*      */     finally {
/* 3321 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */     
/* 3324 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 3326 */     if (tc != null)
/*      */     {
/* 3328 */       tc.update(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void informStopped(PEPeerManager pm, boolean for_queue)
/*      */   {
/* 3337 */     if (pm != null) {
/*      */       try
/*      */       {
/* 3340 */         this.peer_listeners_mon.enter();
/*      */         
/* 3342 */         this.peer_listeners.dispatch(6, pm);
/*      */       }
/*      */       finally
/*      */       {
/* 3346 */         this.peer_listeners_mon.exit();
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 3351 */       this.this_mon.enter();
/*      */       
/* 3353 */       if (this.tracker_client != null)
/*      */       {
/* 3355 */         this.tracker_client.addListener(this.stopping_tracker_client_listener);
/*      */         
/* 3357 */         this.tracker_client.removeListener(this.tracker_client_listener);
/*      */         
/* 3359 */         this.download_manager_state.setTrackerResponseCache(this.tracker_client.getTrackerResponseCache());
/*      */         
/*      */ 
/* 3362 */         this.tracker_client.getLastResponse().setPeers(new TRTrackerAnnouncerResponsePeer[0]);
/*      */         
/*      */ 
/*      */ 
/* 3366 */         this.tracker_client.stop((for_queue) && (isDownloadComplete(false)));
/*      */         
/* 3368 */         this.tracker_client.destroy();
/*      */         
/* 3370 */         this.tracker_client = null;
/*      */       }
/*      */     }
/*      */     finally {
/* 3374 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public DownloadManagerStats getStats()
/*      */   {
/* 3381 */     return this.stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isForceStart()
/*      */   {
/* 3387 */     return this.controller.isForceStart();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setForceStart(boolean forceStart)
/*      */   {
/* 3394 */     if (forceStart)
/*      */     {
/* 3396 */       checkResuming();
/*      */     }
/*      */     
/* 3399 */     this.controller.setForceStart(forceStart);
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
/*      */   protected void downloadEnded(boolean never_downloaded)
/*      */   {
/* 3418 */     if (!never_downloaded)
/*      */     {
/* 3420 */       if (!COConfigurationManager.getBooleanParameter("StartStopManager_bRetainForceStartWhenComplete"))
/*      */       {
/* 3422 */         if (isForceStart())
/*      */         {
/* 3424 */           setForceStart(false);
/*      */         }
/*      */       }
/*      */       
/* 3428 */       setAssumedComplete(true);
/*      */       
/* 3430 */       informDownloadEnded();
/*      */     }
/*      */     
/* 3433 */     TRTrackerAnnouncer tc = this.tracker_client;
/*      */     
/* 3435 */     if (tc != null)
/*      */     {
/* 3437 */       DiskManager dm = getDiskManager();
/*      */       
/*      */ 
/*      */ 
/* 3441 */       if ((dm != null) && (dm.getRemaining() == 0L) && (!COConfigurationManager.getBooleanParameter("peercontrol.hide.piece")))
/*      */       {
/* 3443 */         tc.complete(never_downloaded);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDiskListener(DownloadManagerDiskListener listener)
/*      */   {
/* 3453 */     this.controller.addDiskListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeDiskListener(DownloadManagerDiskListener listener)
/*      */   {
/* 3460 */     this.controller.removeDiskListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addActivationListener(DownloadManagerActivationListener listener)
/*      */   {
/* 3467 */     this.activation_listeners.add(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeActivationListener(DownloadManagerActivationListener listener)
/*      */   {
/* 3474 */     this.activation_listeners.remove(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getHealthStatus()
/*      */   {
/* 3480 */     int state = getState();
/*      */     
/* 3482 */     PEPeerManager peerManager = this.controller.getPeerManager();
/*      */     
/* 3484 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 3486 */     if ((tc != null) && (peerManager != null) && ((state == 50) || (state == 60)))
/*      */     {
/* 3488 */       int nbSeeds = getNbSeeds();
/* 3489 */       int nbPeers = getNbPeers();
/* 3490 */       int nbRemotes = peerManager.getNbRemoteTCPConnections() + peerManager.getNbRemoteUTPConnections();
/*      */       
/* 3492 */       TRTrackerAnnouncerResponse announce_response = tc.getLastResponse();
/*      */       
/* 3494 */       int trackerStatus = announce_response.getStatus();
/*      */       
/* 3496 */       boolean isSeed = state == 60;
/*      */       
/* 3498 */       if (nbSeeds + nbPeers == 0)
/*      */       {
/* 3500 */         if (isSeed)
/*      */         {
/* 3502 */           return 2;
/*      */         }
/*      */         
/* 3505 */         return 5;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3513 */       if (!isSeed)
/*      */       {
/* 3515 */         if ((trackerStatus == 0) || (trackerStatus == 1))
/*      */         {
/*      */ 
/* 3518 */           return 2;
/*      */         }
/*      */       }
/*      */       
/* 3522 */       if (nbRemotes == 0)
/*      */       {
/* 3524 */         TRTrackerScraperResponse scrape_response = getTrackerScrapeResponse();
/*      */         
/* 3526 */         if ((scrape_response != null) && (scrape_response.isValid()))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 3531 */           if ((nbSeeds == scrape_response.getSeeds()) && (nbPeers == scrape_response.getPeers()))
/*      */           {
/*      */ 
/* 3534 */             return 4;
/*      */           }
/*      */         }
/*      */         
/* 3538 */         return 3;
/*      */       }
/*      */       
/* 3541 */       return 4;
/*      */     }
/* 3543 */     if (state == 100) {
/* 3544 */       return 6;
/*      */     }
/*      */     
/* 3547 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getNATStatus()
/*      */   {
/* 3554 */     int state = getState();
/*      */     
/* 3556 */     PEPeerManager peerManager = this.controller.getPeerManager();
/*      */     
/* 3558 */     TRTrackerAnnouncer tc = getTrackerClient();
/*      */     
/* 3560 */     if ((tc != null) && (peerManager != null) && ((state == 50) || (state == 60)))
/*      */     {
/* 3562 */       if ((peerManager.getNbRemoteTCPConnections() > 0) || (peerManager.getNbRemoteUTPConnections() > 0))
/*      */       {
/* 3564 */         return 1;
/*      */       }
/*      */       
/* 3567 */       long last_good_time = peerManager.getLastRemoteConnectionTime();
/*      */       
/* 3569 */       if (last_good_time > 0L)
/*      */       {
/*      */ 
/*      */ 
/* 3573 */         if (SystemTime.getCurrentTime() - last_good_time < 1800000L)
/*      */         {
/* 3575 */           return 1;
/*      */         }
/*      */         
/*      */ 
/* 3579 */         return 2;
/*      */       }
/*      */       
/*      */ 
/* 3583 */       TRTrackerAnnouncerResponse announce_response = tc.getLastResponse();
/*      */       
/* 3585 */       int trackerStatus = announce_response.getStatus();
/*      */       
/* 3587 */       if ((trackerStatus == 0) || (trackerStatus == 1))
/*      */       {
/*      */ 
/* 3590 */         return 0;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 3595 */       if (SystemTime.getCurrentTime() - peerManager.getTimeStarted(false) < 180000L)
/*      */       {
/* 3597 */         return 0;
/*      */       }
/*      */       
/* 3600 */       TRTrackerScraperResponse scrape_response = getTrackerScrapeResponse();
/*      */       
/* 3602 */       if ((scrape_response != null) && (scrape_response.isValid()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3607 */         if ((peerManager.getNbSeeds() == scrape_response.getSeeds()) && (peerManager.getNbPeers() == scrape_response.getPeers()))
/*      */         {
/*      */ 
/* 3610 */           return 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3615 */         if ((state == 60) && (scrape_response.getPeers() == 0))
/*      */         {
/* 3617 */           return 0;
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */       }
/* 3624 */       else if (state == 60)
/*      */       {
/* 3626 */         return 0;
/*      */       }
/*      */       
/*      */ 
/* 3630 */       return 3;
/*      */     }
/*      */     
/*      */ 
/* 3634 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPosition()
/*      */   {
/* 3641 */     return this.position;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPosition(int new_position)
/*      */   {
/* 3648 */     informPositionChanged(new_position);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addTrackerListener(DownloadManagerTrackerListener listener)
/*      */   {
/* 3655 */     this.tracker_listeners.addListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeTrackerListener(DownloadManagerTrackerListener listener)
/*      */   {
/* 3662 */     this.tracker_listeners.removeListener(listener);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void deleteDataFiles()
/*      */   {
/* 3668 */     DownloadManagerState state = getDownloadState();
/*      */     
/* 3670 */     DiskManagerFactory.deleteDataFiles(this.torrent, this.torrent_save_location.getParent(), this.torrent_save_location.getName(), (state.getFlag(16L)) || (state.getFlag(128L)));
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
/* 3681 */     state.setFlag(4L, true);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void deletePartialDataFiles()
/*      */   {
/* 3687 */     DiskManagerFileInfo[] files = getDiskManagerFileInfoSet().getFiles();
/*      */     
/* 3689 */     String abs_root = this.torrent_save_location.getAbsolutePath();
/*      */     
/* 3691 */     for (DiskManagerFileInfo file : files)
/*      */     {
/* 3693 */       if (file.isSkipped())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3700 */         if (file.getDownloaded() != file.getLength())
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3708 */           int storage_type = file.getStorageType();
/*      */           
/* 3710 */           if ((storage_type == 2) || (storage_type == 4))
/*      */           {
/* 3712 */             File f = file.getFile(true);
/*      */             
/* 3714 */             if (f.exists())
/*      */             {
/* 3716 */               if (f.delete())
/*      */               {
/* 3718 */                 File parent = f.getParentFile();
/*      */                 
/* 3720 */                 while (parent != null)
/*      */                 {
/* 3722 */                   if ((!parent.isDirectory()) || (parent.listFiles().length != 0))
/*      */                     break;
/* 3724 */                   if (!parent.getAbsolutePath().startsWith(abs_root))
/*      */                     break;
/* 3726 */                   if (!parent.delete())
/*      */                   {
/* 3728 */                     Debug.outNoStack("Failed to remove empty directory: " + parent);
/*      */                     
/* 3730 */                     break;
/*      */                   }
/*      */                   
/*      */ 
/* 3734 */                   parent = parent.getParentFile();
/*      */ 
/*      */ 
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 3748 */                 Debug.outNoStack("Failed to remove partial: " + f);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void deleteTorrentFile() {
/* 3758 */     if (this.torrentFileName != null)
/*      */     {
/* 3760 */       TorrentUtils.delete(new File(this.torrentFileName), getDownloadState().getFlag(16L));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DownloadManagerState getDownloadState()
/*      */   {
/* 3768 */     return this.download_manager_state;
/*      */   }
/*      */   
/* 3771 */   public Object getData(String key) { return getUserData(key); }
/*      */   
/*      */   public void setData(String key, Object value) {
/* 3774 */     setUserData(key, value);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getUserData(Object key)
/*      */   {
/* 3782 */     Map<Object, Object> data_ref = this.data;
/*      */     
/* 3784 */     if (data_ref == null)
/*      */     {
/* 3786 */       return null;
/*      */     }
/*      */     
/* 3789 */     return data_ref.get(key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUserData(Object key, Object value)
/*      */   {
/*      */     try
/*      */     {
/* 3802 */       this.peer_listeners_mon.enter();
/*      */       
/* 3804 */       Map<Object, Object> data_ref = this.data;
/*      */       
/* 3806 */       if ((data_ref == null) && (value == null)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 3811 */       if (value == null)
/*      */       {
/*      */ 
/*      */ 
/* 3815 */         if (data_ref.containsKey(key))
/*      */         {
/* 3817 */           if (data_ref.size() == 1)
/*      */           {
/* 3819 */             data_ref = null;
/*      */           }
/*      */           else
/*      */           {
/* 3823 */             data_ref = new LightHashMap(data_ref);
/*      */             
/* 3825 */             data_ref.remove(key);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/* 3833 */         if (data_ref == null)
/*      */         {
/* 3835 */           data_ref = new LightHashMap();
/*      */         }
/*      */         else
/*      */         {
/* 3839 */           data_ref = new LightHashMap(data_ref);
/*      */         }
/*      */         
/* 3842 */         data_ref.put(key, value);
/*      */       }
/*      */       
/* 3845 */       this.data = data_ref;
/*      */     }
/*      */     finally
/*      */     {
/* 3849 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/* 3853 */   private static Object TTP_KEY = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getTaggableTransientProperty(String key)
/*      */   {
/* 3860 */     synchronized (TTP_KEY)
/*      */     {
/* 3862 */       LightHashMap<String, Object> map = (LightHashMap)getUserData(TTP_KEY);
/*      */       
/* 3864 */       if (map == null)
/*      */       {
/* 3866 */         return null;
/*      */       }
/*      */       
/*      */ 
/* 3870 */       return map.get(key);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTaggableTransientProperty(String key, Object value)
/*      */   {
/* 3878 */     synchronized (TTP_KEY)
/*      */     {
/* 3880 */       LightHashMap<String, Object> map = (LightHashMap)getUserData(TTP_KEY);
/*      */       
/* 3882 */       if (map == null)
/*      */       {
/* 3884 */         if (value == null)
/*      */         {
/* 3886 */           return;
/*      */         }
/*      */         
/* 3889 */         map = new LightHashMap();
/*      */         
/* 3891 */         map.put(key, value);
/*      */         
/* 3893 */         setUserData(TTP_KEY, map);
/*      */ 
/*      */ 
/*      */       }
/* 3897 */       else if (value == null)
/*      */       {
/* 3899 */         map.remove(key);
/*      */         
/* 3901 */         if (map.size() == 0)
/*      */         {
/* 3903 */           setUserData(TTP_KEY, null);
/*      */         }
/*      */       }
/*      */       else {
/* 3907 */         map.put(key, value);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDataAlreadyAllocated()
/*      */   {
/* 3916 */     return this.data_already_allocated;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDataAlreadyAllocated(boolean already_allocated)
/*      */   {
/* 3923 */     this.data_already_allocated = already_allocated;
/*      */   }
/*      */   
/*      */   public void setSeedingRank(int rank) {
/* 3927 */     this.iSeedingRank = rank;
/*      */   }
/*      */   
/*      */   public int getSeedingRank() {
/* 3931 */     return this.iSeedingRank;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getCreationTime()
/*      */   {
/* 3937 */     return this.creation_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCreationTime(long t)
/*      */   {
/* 3944 */     this.creation_time = t;
/*      */   }
/*      */   
/*      */ 
/*      */   public String isSwarmMerging()
/*      */   {
/* 3950 */     return this.globalManager.isSwarmMerging(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getExtendedMessagingMode()
/*      */   {
/* 3956 */     if (this.message_mode == -1)
/*      */     {
/* 3958 */       byte[] hash = null;
/*      */       
/* 3960 */       if (this.torrent != null) {
/*      */         try
/*      */         {
/* 3963 */           hash = this.torrent.getHash();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/* 3969 */       this.message_mode = ((Integer)client_id_manager.getProperty(hash, "Messaging-Mode")).intValue();
/*      */     }
/*      */     
/* 3972 */     return this.message_mode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setCryptoLevel(int level)
/*      */   {
/* 3979 */     this.crypto_level = level;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getCryptoLevel()
/*      */   {
/* 3985 */     return this.crypto_level;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void moveDataFiles(File new_parent_dir)
/*      */     throws DownloadManagerException
/*      */   {
/* 3994 */     moveDataFiles(new_parent_dir, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void moveDataFilesLive(File new_parent_dir)
/*      */     throws DownloadManagerException
/*      */   {
/* 4003 */     moveDataFiles(new_parent_dir, null, true);
/*      */   }
/*      */   
/*      */   public void renameDownload(String new_name) throws DownloadManagerException {
/* 4007 */     moveDataFiles(null, new_name);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void moveDataFiles(File destination, String new_name)
/*      */     throws DownloadManagerException
/*      */   {
/* 4017 */     moveDataFiles(destination, new_name, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void moveDataFiles(final File destination, final String new_name, final boolean live)
/*      */     throws DownloadManagerException
/*      */   {
/* 4028 */     if ((destination == null) && (new_name == null)) {
/* 4029 */       throw new NullPointerException("destination and new name are both null");
/*      */     }
/*      */     
/* 4032 */     if (!canMoveDataFiles()) {
/* 4033 */       throw new DownloadManagerException("canMoveDataFiles is false!");
/*      */     }
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
/* 4046 */     SaveLocationChange slc = new SaveLocationChange();
/* 4047 */     slc.download_location = destination;
/* 4048 */     slc.download_name = new_name;
/*      */     
/* 4050 */     File current_location = getSaveLocation();
/* 4051 */     if (slc.normaliseDownloadLocation(current_location).equals(current_location)) {
/* 4052 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 4056 */       FileUtil.runAsTask(new com.aelitis.azureus.core.AzureusCoreOperationTask()
/*      */       {
/*      */ 
/*      */         public void run(AzureusCoreOperation operation)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 4064 */             if (live)
/*      */             {
/* 4066 */               DownloadManagerImpl.this.moveDataFilesSupport0(destination, new_name);
/*      */             }
/*      */             else
/*      */             {
/* 4070 */               DownloadManagerImpl.this.moveDataFilesSupport(destination, new_name);
/*      */             }
/*      */           }
/*      */           catch (DownloadManagerException e) {
/* 4074 */             throw new RuntimeException(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */     catch (RuntimeException e) {
/* 4080 */       Throwable cause = e.getCause();
/*      */       
/* 4082 */       if ((cause instanceof DownloadManagerException))
/*      */       {
/* 4084 */         throw ((DownloadManagerException)cause);
/*      */       }
/*      */       
/* 4087 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void moveDataFilesSupport(File new_parent_dir, String new_filename)
/*      */     throws DownloadManagerException
/*      */   {
/* 4098 */     boolean is_paused = pause();
/* 4099 */     try { moveDataFilesSupport0(new_parent_dir, new_filename);
/* 4100 */     } finally { if (is_paused) { resume();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void moveDataFilesSupport0(File new_parent_dir, String new_filename)
/*      */     throws DownloadManagerException
/*      */   {
/* 4110 */     if (!canMoveDataFiles()) {
/* 4111 */       throw new DownloadManagerException("canMoveDataFiles is false!");
/*      */     }
/*      */     
/* 4114 */     if (new_filename != null) { new_filename = FileUtil.convertOSSpecificChars(new_filename, false);
/*      */     }
/*      */     
/*      */ 
/* 4118 */     File old_file = getSaveLocation();
/*      */     try
/*      */     {
/* 4121 */       old_file = old_file.getCanonicalFile();
/* 4122 */       if (new_parent_dir != null) new_parent_dir = new_parent_dir.getCanonicalFile();
/*      */     }
/*      */     catch (Throwable e) {
/* 4125 */       Debug.printStackTrace(e);
/* 4126 */       throw new DownloadManagerException("Failed to get canonical paths", e);
/*      */     }
/*      */     
/* 4129 */     File current_save_location = old_file;
/* 4130 */     File new_save_location = new File(new_parent_dir == null ? old_file.getParentFile() : new_parent_dir, new_filename == null ? old_file.getName() : new_filename);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 4135 */     if (current_save_location.equals(new_save_location))
/*      */     {
/* 4137 */       return;
/*      */     }
/*      */     
/* 4140 */     DiskManager dm = getDiskManager();
/*      */     
/* 4142 */     if ((dm == null) || (dm.getFiles() == null))
/*      */     {
/* 4144 */       if (!old_file.exists())
/*      */       {
/*      */ 
/*      */ 
/* 4148 */         FileUtil.mkdirs(new_save_location.getParentFile());
/*      */         
/* 4150 */         setTorrentSaveDir(new_save_location.getParent().toString(), new_save_location.getName());
/*      */         
/* 4152 */         return;
/*      */       }
/*      */       try
/*      */       {
/* 4156 */         new_save_location = new_save_location.getCanonicalFile();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 4160 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/* 4163 */       if (!old_file.equals(new_save_location))
/*      */       {
/*      */ 
/*      */ 
/* 4167 */         if (this.torrent.isSimpleTorrent())
/*      */         {
/*      */ 
/* 4170 */           if (this.controller.getDiskManagerFileInfo()[0].setLinkAtomic(new_save_location))
/* 4171 */             setTorrentSaveDir(new_save_location.getParentFile().toString(), new_save_location.getName()); else {
/* 4172 */             throw new DownloadManagerException("rename operation failed");
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4189 */           if (FileUtil.isAncestorOf(old_file, new_save_location))
/*      */           {
/* 4191 */             Logger.logTextResource(new LogAlert(this, true, 3, "DiskManager.alert.movefilefails"), new String[] { old_file.toString(), "Target is sub-directory of files" });
/*      */             
/*      */ 
/*      */ 
/* 4195 */             throw new DownloadManagerException("rename operation failed");
/*      */           }
/*      */           
/*      */ 
/* 4199 */           final HashSet files_to_move = new HashSet();
/*      */           
/*      */ 
/* 4202 */           files_to_move.add(null);
/* 4203 */           DiskManagerFileInfo[] info_files = this.controller.getDiskManagerFileInfo();
/* 4204 */           for (int i = 0; i < info_files.length; i++) {
/* 4205 */             File f = info_files[i].getFile(true);
/* 4206 */             try { f = f.getCanonicalFile();
/* 4207 */             } catch (IOException ioe) { f = f.getAbsoluteFile(); }
/* 4208 */             boolean added_entry = files_to_move.add(f);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4217 */             while (added_entry) {
/* 4218 */               f = f.getParentFile();
/* 4219 */               added_entry = files_to_move.add(f);
/*      */             }
/*      */           }
/* 4222 */           FileFilter ff = new FileFilter() {
/* 4223 */             public boolean accept(File f) { return files_to_move.contains(f); }
/*      */           };
/*      */           
/* 4226 */           if (FileUtil.renameFile(old_file, new_save_location, false, ff))
/*      */           {
/* 4228 */             setTorrentSaveDir(new_save_location.getParentFile().toString(), new_save_location.getName());
/*      */           }
/*      */           else
/*      */           {
/* 4232 */             throw new DownloadManagerException("rename operation failed");
/*      */           }
/*      */           
/* 4235 */           if (current_save_location.isDirectory())
/*      */           {
/* 4237 */             TorrentUtils.recursiveEmptyDirDelete(current_save_location, false); }
/*      */         }
/*      */       }
/*      */     } else {
/* 4241 */       dm.moveDataFiles(new_save_location.getParentFile(), new_save_location.getName(), null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void copyDataFiles(File parent_dir)
/*      */     throws DownloadManagerException
/*      */   {
/* 4251 */     if (parent_dir.exists())
/*      */     {
/* 4253 */       if (!parent_dir.isDirectory())
/*      */       {
/* 4255 */         throw new DownloadManagerException("'" + parent_dir + "' is not a directory");
/*      */       }
/*      */       
/*      */     }
/* 4259 */     else if (!parent_dir.mkdirs())
/*      */     {
/* 4261 */       throw new DownloadManagerException("failed to create '" + parent_dir + "'");
/*      */     }
/*      */     
/*      */ 
/* 4265 */     DiskManagerFileInfo[] files = this.controller.getDiskManagerFileInfoSet().getFiles();
/*      */     
/* 4267 */     if (this.torrent.isSimpleTorrent())
/*      */     {
/* 4269 */       File file_from = files[0].getFile(true);
/*      */       try
/*      */       {
/* 4272 */         File file_to = new File(parent_dir, file_from.getName());
/*      */         
/* 4274 */         if (file_to.exists())
/*      */         {
/* 4276 */           if (file_to.length() != file_from.length())
/*      */           {
/* 4278 */             throw new Exception("target file '" + file_to + " already exists");
/*      */           }
/*      */         }
/*      */         else {
/* 4282 */           FileUtil.copyFileWithException(file_from, file_to);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 4286 */         throw new DownloadManagerException("copy of '" + file_from + "' failed", e);
/*      */       }
/*      */     }
/*      */     else {
/*      */       try {
/* 4291 */         File sl_file = getSaveLocation();
/*      */         
/* 4293 */         String save_location = sl_file.getCanonicalPath();
/*      */         
/* 4295 */         if (!save_location.endsWith(File.separator))
/*      */         {
/* 4297 */           save_location = save_location + File.separator;
/*      */         }
/*      */         
/* 4300 */         parent_dir = new File(parent_dir, sl_file.getName());
/*      */         
/* 4302 */         if (!parent_dir.isDirectory())
/*      */         {
/* 4304 */           parent_dir.mkdirs();
/*      */         }
/*      */         
/* 4307 */         for (DiskManagerFileInfo file : files)
/*      */         {
/* 4309 */           if ((!file.isSkipped()) && (file.getDownloaded() == file.getLength()))
/*      */           {
/* 4311 */             File file_from = file.getFile(true);
/*      */             try
/*      */             {
/* 4314 */               String file_path = file_from.getCanonicalPath();
/*      */               
/* 4316 */               if (file_path.startsWith(save_location))
/*      */               {
/* 4318 */                 File file_to = new File(parent_dir, file_path.substring(save_location.length()));
/*      */                 
/* 4320 */                 if (file_to.exists())
/*      */                 {
/* 4322 */                   if (file_to.length() != file_from.length())
/*      */                   {
/* 4324 */                     throw new Exception("target file '" + file_to + " already exists");
/*      */                   }
/*      */                 }
/*      */                 else {
/* 4328 */                   File parent = file_to.getParentFile();
/*      */                   
/* 4330 */                   if (!parent.exists())
/*      */                   {
/* 4332 */                     if (!parent.mkdirs())
/*      */                     {
/* 4334 */                       throw new Exception("Failed to make directory '" + parent + "'");
/*      */                     }
/*      */                   }
/*      */                   
/* 4338 */                   FileUtil.copyFileWithException(file_from, file_to);
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (Throwable e) {
/* 4343 */               throw new DownloadManagerException("copy of '" + file_from + "' failed", e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 4349 */         throw new DownloadManagerException("copy failed", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void moveTorrentFile(File new_parent_dir) throws DownloadManagerException {
/* 4355 */     moveTorrentFile(new_parent_dir, null);
/*      */   }
/*      */   
/*      */   public void moveTorrentFile(File new_parent_dir, String new_name) throws DownloadManagerException {
/* 4359 */     SaveLocationChange slc = new SaveLocationChange();
/* 4360 */     slc.torrent_location = new_parent_dir;
/* 4361 */     slc.torrent_name = new_name;
/*      */     
/* 4363 */     File torrent_file_now = new File(getTorrentFileName());
/* 4364 */     if (!slc.isDifferentTorrentLocation(torrent_file_now)) { return;
/*      */     }
/* 4366 */     boolean is_paused = pause();
/* 4367 */     try { moveTorrentFile0(new_parent_dir, new_name);
/* 4368 */     } finally { if (is_paused) { resume();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void moveTorrentFile0(File new_parent_dir, String new_name)
/*      */     throws DownloadManagerException
/*      */   {
/* 4379 */     if (!canMoveDataFiles())
/*      */     {
/* 4381 */       throw new DownloadManagerException("Cannot move torrent file");
/*      */     }
/*      */     
/* 4384 */     setTorrentFile(new_parent_dir, new_name);
/*      */   }
/*      */   
/*      */   public void setTorrentFile(File new_parent_dir, String new_name) throws DownloadManagerException
/*      */   {
/* 4389 */     File old_file = new File(getTorrentFileName());
/*      */     
/* 4391 */     if (!old_file.exists()) {
/* 4392 */       Debug.out("torrent file doesn't exist!");
/* 4393 */       return;
/*      */     }
/*      */     
/* 4396 */     if (new_parent_dir == null) new_parent_dir = old_file.getParentFile();
/* 4397 */     if (new_name == null) new_name = old_file.getName();
/* 4398 */     File new_file = new File(new_parent_dir, new_name);
/*      */     try
/*      */     {
/* 4401 */       old_file = old_file.getCanonicalFile();
/* 4402 */       new_file = new_file.getCanonicalFile();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 4406 */       Debug.printStackTrace(e);
/*      */       
/* 4408 */       throw new DownloadManagerException("Failed to get canonical paths", e);
/*      */     }
/*      */     
/*      */ 
/* 4412 */     if (new_file.equals(old_file)) { return;
/*      */     }
/* 4414 */     if (TorrentUtils.move(old_file, new_file)) {
/* 4415 */       setTorrentFileName(new_file.toString());
/*      */     }
/*      */     else {
/* 4418 */       throw new DownloadManagerException("rename operation failed");
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isInDefaultSaveDir() {
/* 4423 */     return DownloadManagerDefaultPaths.isInDefaultDownloadDir(this);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean seedPieceRecheck()
/*      */   {
/* 4429 */     PEPeerManager pm = this.controller.getPeerManager();
/*      */     
/* 4431 */     if (pm != null)
/*      */     {
/* 4433 */       return pm.seedPieceRecheck();
/*      */     }
/*      */     
/* 4436 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(LimitedRateGroup group, boolean upload)
/*      */   {
/* 4444 */     this.controller.addRateLimiter(group, upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public LimitedRateGroup[] getRateLimiters(boolean upload)
/*      */   {
/* 4451 */     return this.controller.getRateLimiters(upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(LimitedRateGroup group, boolean upload)
/*      */   {
/* 4459 */     this.controller.removeRateLimiter(group, upload);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTrackerError()
/*      */   {
/* 4465 */     TRTrackerAnnouncer announcer = getTrackerClient();
/*      */     
/* 4467 */     if (announcer != null)
/*      */     {
/* 4469 */       TRTrackerAnnouncerResponse resp = announcer.getLastResponse();
/*      */       
/* 4471 */       if (resp != null)
/*      */       {
/* 4473 */         if (resp.getStatus() == 1)
/*      */         {
/* 4475 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 4480 */       TRTrackerScraperResponse resp = getTrackerScrapeResponse();
/*      */       
/* 4482 */       if (resp != null)
/*      */       {
/* 4484 */         if (resp.getStatus() == 1)
/*      */         {
/* 4486 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4491 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isUnauthorisedOnTracker()
/*      */   {
/* 4497 */     TRTrackerAnnouncer announcer = getTrackerClient();
/*      */     
/* 4499 */     String status_str = null;
/*      */     
/* 4501 */     if (announcer != null)
/*      */     {
/* 4503 */       TRTrackerAnnouncerResponse resp = announcer.getLastResponse();
/*      */       
/* 4505 */       if (resp != null)
/*      */       {
/* 4507 */         if (resp.getStatus() == 1)
/*      */         {
/* 4509 */           status_str = resp.getStatusString();
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 4514 */       TRTrackerScraperResponse resp = getTrackerScrapeResponse();
/*      */       
/* 4516 */       if (resp != null)
/*      */       {
/* 4518 */         if (resp.getStatus() == 1)
/*      */         {
/* 4520 */           status_str = resp.getStatusString();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4525 */     if (status_str != null)
/*      */     {
/* 4527 */       status_str = status_str.toLowerCase();
/*      */       
/* 4529 */       if ((status_str.contains("not authorised")) || (status_str.contains("not authorized")))
/*      */       {
/*      */ 
/* 4532 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 4536 */     return false;
/*      */   }
/*      */   
/*      */   public List<TrackerPeerSource> getTrackerPeerSources()
/*      */   {
/*      */     try
/*      */     {
/* 4543 */       this.this_mon.enter();
/*      */       
/* 4545 */       Object[] tps_data = (Object[])getUserData(TPS_Key);
/*      */       
/*      */       TOTorrentListener tol;
/*      */       List<TrackerPeerSource> tps;
/* 4549 */       if (tps_data == null)
/*      */       {
/* 4551 */         List<TrackerPeerSource> tps = new ArrayList();
/*      */         
/* 4553 */         tol = new TOTorrentListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void torrentChanged(TOTorrent torrent, int type)
/*      */           {
/*      */ 
/*      */ 
/* 4561 */             if (type == 1)
/*      */             {
/* 4563 */               List<DownloadManagerTPSListener> to_inform = null;
/*      */               try
/*      */               {
/* 4566 */                 DownloadManagerImpl.this.this_mon.enter();
/*      */                 
/* 4568 */                 torrent.removeListener(this);
/*      */                 
/* 4570 */                 DownloadManagerImpl.this.setUserData(DownloadManagerImpl.TPS_Key, null);
/*      */                 
/* 4572 */                 if (DownloadManagerImpl.this.tps_listeners != null)
/*      */                 {
/* 4574 */                   to_inform = new ArrayList(DownloadManagerImpl.this.tps_listeners);
/*      */                 }
/*      */               }
/*      */               finally {
/* 4578 */                 DownloadManagerImpl.this.this_mon.exit();
/*      */               }
/*      */               
/* 4581 */               if (to_inform != null)
/*      */               {
/* 4583 */                 for (DownloadManagerTPSListener l : to_inform)
/*      */                 {
/*      */                   try
/*      */                   {
/* 4587 */                     l.trackerPeerSourcesChanged();
/*      */                   }
/*      */                   catch (Throwable e)
/*      */                   {
/* 4591 */                     Debug.out(e);
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 4598 */         };
/* 4599 */         setUserData(TPS_Key, new Object[] { tps, tol });
/*      */         
/* 4601 */         Download plugin_download = org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils.wrap(this);
/*      */         
/* 4603 */         if ((isDestroyed()) || (plugin_download == null))
/*      */         {
/* 4605 */           return tps;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 4610 */         final TOTorrent t = getTorrent();
/*      */         
/* 4612 */         if (t != null)
/*      */         {
/* 4614 */           t.addListener(tol);
/*      */           
/* 4616 */           TOTorrentAnnounceURLSet[] sets = t.getAnnounceURLGroup().getAnnounceURLSets();
/*      */           
/* 4618 */           if (sets.length == 0)
/*      */           {
/* 4620 */             sets = new TOTorrentAnnounceURLSet[] { t.getAnnounceURLGroup().createAnnounceURLSet(new URL[] { this.torrent.getAnnounceURL() }) };
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 4625 */           for (final TOTorrentAnnounceURLSet set : sets)
/*      */           {
/* 4627 */             final URL[] urls = set.getAnnounceURLs();
/*      */             
/* 4629 */             if ((urls.length != 0) && (!TorrentUtils.isDecentralised(urls[0])))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 4634 */               tps.add(new TrackerPeerSource()
/*      */               {
/*      */                 private TrackerPeerSource _delegate;
/*      */                 
/*      */                 private TRTrackerAnnouncer ta;
/*      */                 
/*      */                 private long ta_fixup;
/*      */                 
/*      */                 private long last_scrape_fixup_time;
/*      */                 
/*      */                 private Object[] last_scrape;
/*      */                 
/*      */                 private TrackerPeerSource fixup()
/*      */                 {
/* 4648 */                   long now = SystemTime.getMonotonousTime();
/*      */                   
/* 4650 */                   if (now - this.ta_fixup > 1000L)
/*      */                   {
/* 4652 */                     TRTrackerAnnouncer current_ta = DownloadManagerImpl.this.getTrackerClient();
/*      */                     
/* 4654 */                     if (current_ta == this.ta)
/*      */                     {
/* 4656 */                       if ((current_ta != null) && (this._delegate == null))
/*      */                       {
/* 4658 */                         this._delegate = current_ta.getTrackerPeerSource(set);
/*      */                       }
/*      */                     }
/*      */                     else {
/* 4662 */                       if (current_ta == null)
/*      */                       {
/* 4664 */                         this._delegate = null;
/*      */                       }
/*      */                       else
/*      */                       {
/* 4668 */                         this._delegate = current_ta.getTrackerPeerSource(set);
/*      */                       }
/*      */                       
/* 4671 */                       this.ta = current_ta;
/*      */                     }
/*      */                     
/* 4674 */                     this.ta_fixup = now;
/*      */                   }
/*      */                   
/* 4677 */                   return this._delegate;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 protected Object[] getScrape()
/*      */                 {
/* 4683 */                   long now = SystemTime.getMonotonousTime();
/*      */                   
/* 4685 */                   if ((now - this.last_scrape_fixup_time > 30000L) || (this.last_scrape == null))
/*      */                   {
/* 4687 */                     TRTrackerScraper scraper = DownloadManagerImpl.this.globalManager.getTrackerScraper();
/*      */                     
/* 4689 */                     int max_peers = -1;
/* 4690 */                     int max_seeds = -1;
/* 4691 */                     int max_comp = -1;
/* 4692 */                     int max_time = 0;
/* 4693 */                     int min_scrape = Integer.MAX_VALUE;
/*      */                     
/* 4695 */                     String status_str = null;
/*      */                     
/* 4697 */                     boolean found_usable = false;
/*      */                     
/* 4699 */                     for (URL u : urls)
/*      */                     {
/* 4701 */                       TRTrackerScraperResponse resp = scraper.peekScrape(DownloadManagerImpl.this.torrent, u);
/*      */                       
/* 4703 */                       if (resp != null)
/*      */                       {
/* 4705 */                         if (!resp.isDHTBackup())
/*      */                         {
/* 4707 */                           found_usable = true;
/*      */                           
/* 4709 */                           int peers = resp.getPeers();
/* 4710 */                           int seeds = resp.getSeeds();
/* 4711 */                           int comp = resp.getCompleted();
/*      */                           
/* 4713 */                           if (peers > max_peers)
/*      */                           {
/* 4715 */                             max_peers = peers;
/*      */                           }
/*      */                           
/* 4718 */                           if (seeds > max_seeds)
/*      */                           {
/* 4720 */                             max_seeds = seeds;
/*      */                           }
/*      */                           
/* 4723 */                           if (comp > max_comp)
/*      */                           {
/* 4725 */                             max_comp = comp;
/*      */                           }
/*      */                           
/* 4728 */                           if (resp.getStatus() != 0)
/*      */                           {
/* 4730 */                             status_str = resp.getStatusString();
/*      */                             
/* 4732 */                             int time = resp.getScrapeTime();
/*      */                             
/* 4734 */                             if (time > max_time)
/*      */                             {
/* 4736 */                               max_time = time;
/*      */                             }
/*      */                             
/* 4739 */                             long next_scrape = resp.getNextScrapeStartTime();
/*      */                             
/* 4741 */                             if (next_scrape > 0L)
/*      */                             {
/* 4743 */                               int ns = (int)(next_scrape / 1000L);
/*      */                               
/* 4745 */                               if (ns < min_scrape)
/*      */                               {
/* 4747 */                                 min_scrape = ns;
/*      */                               }
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/* 4757 */                     if ((found_usable) || (this.last_scrape == null))
/*      */                     {
/* 4759 */                       this.last_scrape = new Object[] { Integer.valueOf(max_seeds), Integer.valueOf(max_peers), Integer.valueOf(max_time), Integer.valueOf(min_scrape), Integer.valueOf(max_comp), status_str };
/*      */                     }
/*      */                     
/* 4762 */                     this.last_scrape_fixup_time = now;
/*      */                   }
/*      */                   
/* 4765 */                   return this.last_scrape;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getType()
/*      */                 {
/* 4771 */                   return 1;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public String getName()
/*      */                 {
/* 4777 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4779 */                   if (delegate == null)
/*      */                   {
/* 4781 */                     return urls[0].toExternalForm();
/*      */                   }
/*      */                   
/* 4784 */                   return delegate.getName();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getStatus()
/*      */                 {
/* 4790 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4792 */                   if (delegate == null)
/*      */                   {
/* 4794 */                     return 2;
/*      */                   }
/*      */                   
/* 4797 */                   return delegate.getStatus();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public String getStatusString()
/*      */                 {
/* 4803 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4805 */                   if (delegate == null)
/*      */                   {
/* 4807 */                     return (String)getScrape()[5];
/*      */                   }
/*      */                   
/* 4810 */                   return delegate.getStatusString();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getSeedCount()
/*      */                 {
/* 4816 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4818 */                   if (delegate == null)
/*      */                   {
/* 4820 */                     return ((Integer)getScrape()[0]).intValue();
/*      */                   }
/*      */                   
/* 4823 */                   int seeds = delegate.getSeedCount();
/*      */                   
/* 4825 */                   if (seeds < 0)
/*      */                   {
/* 4827 */                     seeds = ((Integer)getScrape()[0]).intValue();
/*      */                   }
/*      */                   
/* 4830 */                   return seeds;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getLeecherCount()
/*      */                 {
/* 4836 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4838 */                   if (delegate == null)
/*      */                   {
/* 4840 */                     return ((Integer)getScrape()[1]).intValue();
/*      */                   }
/*      */                   
/* 4843 */                   int leechers = delegate.getLeecherCount();
/*      */                   
/* 4845 */                   if (leechers < 0)
/*      */                   {
/* 4847 */                     leechers = ((Integer)getScrape()[1]).intValue();
/*      */                   }
/*      */                   
/* 4850 */                   return leechers;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getCompletedCount()
/*      */                 {
/* 4856 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4858 */                   if (delegate == null)
/*      */                   {
/* 4860 */                     return ((Integer)getScrape()[4]).intValue();
/*      */                   }
/*      */                   
/* 4863 */                   int comp = delegate.getCompletedCount();
/*      */                   
/* 4865 */                   if (comp < 0)
/*      */                   {
/* 4867 */                     comp = ((Integer)getScrape()[4]).intValue();
/*      */                   }
/*      */                   
/* 4870 */                   return comp;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getPeers()
/*      */                 {
/* 4876 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4878 */                   if (delegate == null)
/*      */                   {
/* 4880 */                     return -1;
/*      */                   }
/*      */                   
/* 4883 */                   return delegate.getPeers();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getInterval()
/*      */                 {
/* 4889 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4891 */                   if (delegate == null)
/*      */                   {
/* 4893 */                     Object[] si = getScrape();
/*      */                     
/* 4895 */                     int last = ((Integer)si[2]).intValue();
/* 4896 */                     int next = ((Integer)si[3]).intValue();
/*      */                     
/* 4898 */                     if ((last > 0) && (next < Integer.MAX_VALUE) && (last < next))
/*      */                     {
/* 4900 */                       return next - last;
/*      */                     }
/*      */                     
/* 4903 */                     return -1;
/*      */                   }
/*      */                   
/* 4906 */                   return delegate.getInterval();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getMinInterval()
/*      */                 {
/* 4912 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4914 */                   if (delegate == null)
/*      */                   {
/* 4916 */                     return -1;
/*      */                   }
/*      */                   
/* 4919 */                   return delegate.getMinInterval();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public boolean isUpdating()
/*      */                 {
/* 4925 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4927 */                   if (delegate == null)
/*      */                   {
/* 4929 */                     return false;
/*      */                   }
/*      */                   
/* 4932 */                   return delegate.isUpdating();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getLastUpdate()
/*      */                 {
/* 4938 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4940 */                   if (delegate == null)
/*      */                   {
/* 4942 */                     return ((Integer)getScrape()[2]).intValue();
/*      */                   }
/*      */                   
/* 4945 */                   return delegate.getLastUpdate();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public int getSecondsToUpdate()
/*      */                 {
/* 4951 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4953 */                   if (delegate == null)
/*      */                   {
/* 4955 */                     return -1;
/*      */                   }
/*      */                   
/* 4958 */                   return delegate.getSecondsToUpdate();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public boolean canManuallyUpdate()
/*      */                 {
/* 4964 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4966 */                   if (delegate == null)
/*      */                   {
/* 4968 */                     return false;
/*      */                   }
/*      */                   
/* 4971 */                   return delegate.canManuallyUpdate();
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public void manualUpdate()
/*      */                 {
/* 4977 */                   TrackerPeerSource delegate = fixup();
/*      */                   
/* 4979 */                   if (delegate != null)
/*      */                   {
/* 4981 */                     delegate.manualUpdate();
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public boolean canDelete()
/*      */                 {
/* 4988 */                   return true;
/*      */                 }
/*      */                 
/*      */ 
/*      */                 public void delete()
/*      */                 {
/* 4994 */                   List<List<String>> lists = TorrentUtils.announceGroupsToList(t);
/*      */                   
/* 4996 */                   List<String> rem = new ArrayList();
/*      */                   
/* 4998 */                   for (URL u : urls) {
/* 4999 */                     rem.add(u.toExternalForm());
/*      */                   }
/*      */                   
/* 5002 */                   lists = TorrentUtils.removeAnnounceURLs2(lists, rem, false);
/*      */                   
/* 5004 */                   TorrentUtils.listToAnnounceGroups(lists, t);
/*      */                 }
/*      */               });
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 5011 */           tps.add(new TrackerPeerSourceAdapter()
/*      */           {
/*      */             private TrackerPeerSource _delegate;
/*      */             
/*      */             private TRTrackerAnnouncer ta;
/*      */             
/*      */             private boolean enabled;
/*      */             
/*      */             private long ta_fixup;
/*      */             
/*      */             private TrackerPeerSource fixup()
/*      */             {
/* 5023 */               long now = SystemTime.getMonotonousTime();
/*      */               
/* 5025 */               if (now - this.ta_fixup > 1000L)
/*      */               {
/* 5027 */                 TRTrackerAnnouncer current_ta = DownloadManagerImpl.this.getTrackerClient();
/*      */                 
/* 5029 */                 if (current_ta == this.ta)
/*      */                 {
/* 5031 */                   if ((current_ta != null) && (this._delegate == null))
/*      */                   {
/* 5033 */                     this._delegate = current_ta.getCacheTrackerPeerSource();
/*      */                   }
/*      */                 }
/*      */                 else {
/* 5037 */                   if (current_ta == null)
/*      */                   {
/* 5039 */                     this._delegate = null;
/*      */                   }
/*      */                   else
/*      */                   {
/* 5043 */                     this._delegate = current_ta.getCacheTrackerPeerSource();
/*      */                   }
/*      */                   
/* 5046 */                   this.ta = current_ta;
/*      */                 }
/*      */                 
/* 5049 */                 this.enabled = DownloadManagerImpl.this.controller.isPeerSourceEnabled("Tracker");
/*      */                 
/* 5051 */                 this.ta_fixup = now;
/*      */               }
/*      */               
/* 5054 */               return this._delegate;
/*      */             }
/*      */             
/*      */ 
/*      */             public int getType()
/*      */             {
/* 5060 */               return 1;
/*      */             }
/*      */             
/*      */ 
/*      */             public String getName()
/*      */             {
/* 5066 */               TrackerPeerSource delegate = fixup();
/*      */               
/* 5068 */               if (delegate == null)
/*      */               {
/* 5070 */                 return MessageText.getString("tps.tracker.cache");
/*      */               }
/*      */               
/* 5073 */               return delegate.getName();
/*      */             }
/*      */             
/*      */ 
/*      */             public int getStatus()
/*      */             {
/* 5079 */               TrackerPeerSource delegate = fixup();
/*      */               
/* 5081 */               if (!this.enabled)
/*      */               {
/* 5083 */                 return 1;
/*      */               }
/*      */               
/* 5086 */               if (delegate == null)
/*      */               {
/* 5088 */                 return 2;
/*      */               }
/*      */               
/* 5091 */               return 5;
/*      */             }
/*      */             
/*      */ 
/*      */             public int getPeers()
/*      */             {
/* 5097 */               TrackerPeerSource delegate = fixup();
/*      */               
/* 5099 */               if ((delegate == null) || (!this.enabled))
/*      */               {
/* 5101 */                 return -1;
/*      */               }
/*      */               
/* 5104 */               return delegate.getPeers();
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */         try
/*      */         {
/* 5112 */           ExternalSeedPlugin esp = DownloadManagerController.getExternalSeedPlugin();
/*      */           
/* 5114 */           if (esp != null)
/*      */           {
/* 5116 */             tps.add(esp.getTrackerPeerSource(plugin_download));
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 5125 */           PluginInterface dht_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(DHTTrackerPlugin.class);
/*      */           
/* 5127 */           if (dht_pi != null)
/*      */           {
/* 5129 */             tps.add(((DHTTrackerPlugin)dht_pi.getPlugin()).getTrackerPeerSource(plugin_download));
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 5138 */           PluginInterface lt_pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByClass(LocalTrackerPlugin.class);
/*      */           
/* 5140 */           if (lt_pi != null)
/*      */           {
/* 5142 */             tps.add(((LocalTrackerPlugin)lt_pi.getPlugin()).getTrackerPeerSource(plugin_download));
/*      */           }
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 5154 */           tps.add(((DownloadImpl)plugin_download).getTrackerPeerSource());
/*      */         }
/*      */         catch (Throwable e) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5162 */         tps.add(new TrackerPeerSourceAdapter()
/*      */         {
/*      */           private PEPeerManager _pm;
/*      */           
/*      */           private TrackerPeerSource _delegate;
/*      */           
/*      */ 
/*      */           private TrackerPeerSource fixup()
/*      */           {
/* 5171 */             PEPeerManager pm = DownloadManagerImpl.this.getPeerManager();
/*      */             
/* 5173 */             if (pm == null)
/*      */             {
/* 5175 */               this._delegate = null;
/* 5176 */               this._pm = null;
/*      */             }
/* 5178 */             else if (pm != this._pm)
/*      */             {
/* 5180 */               this._pm = pm;
/*      */               
/* 5182 */               this._delegate = pm.getTrackerPeerSource();
/*      */             }
/*      */             
/* 5185 */             return this._delegate;
/*      */           }
/*      */           
/*      */ 
/*      */           public int getType()
/*      */           {
/* 5191 */             return 5;
/*      */           }
/*      */           
/*      */ 
/*      */           public int getStatus()
/*      */           {
/* 5197 */             TrackerPeerSource delegate = fixup();
/*      */             
/* 5199 */             if (delegate == null)
/*      */             {
/* 5201 */               return 2;
/*      */             }
/*      */             
/*      */ 
/* 5205 */             return delegate.getStatus();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public String getName()
/*      */           {
/* 5212 */             TrackerPeerSource delegate = fixup();
/*      */             
/* 5214 */             if (delegate == null)
/*      */             {
/* 5216 */               return "";
/*      */             }
/*      */             
/*      */ 
/* 5220 */             return delegate.getName();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public int getPeers()
/*      */           {
/* 5227 */             TrackerPeerSource delegate = fixup();
/*      */             
/* 5229 */             if (delegate == null)
/*      */             {
/* 5231 */               return -1;
/*      */             }
/*      */             
/*      */ 
/* 5235 */             return delegate.getPeers();
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 5241 */         });
/* 5242 */         tps.add(new TrackerPeerSourceAdapter()
/*      */         {
/*      */           private long fixup_time;
/*      */           
/*      */           private PEPeerManager _pm;
/*      */           
/*      */           private int tcp;
/*      */           
/*      */           private int udp;
/*      */           private int utp;
/*      */           private int total;
/*      */           private boolean enabled;
/*      */           
/*      */           private PEPeerManager fixup()
/*      */           {
/* 5257 */             long now = SystemTime.getMonotonousTime();
/*      */             
/* 5259 */             if (now - this.fixup_time > 1000L)
/*      */             {
/* 5261 */               PEPeerManager pm = this._pm = DownloadManagerImpl.this.getPeerManager();
/*      */               
/* 5263 */               if (pm != null)
/*      */               {
/* 5265 */                 this.tcp = pm.getNbRemoteTCPConnections();
/* 5266 */                 this.udp = pm.getNbRemoteUDPConnections();
/* 5267 */                 this.utp = pm.getNbRemoteUTPConnections();
/* 5268 */                 this.total = pm.getStats().getTotalIncomingConnections();
/*      */               }
/*      */               
/* 5271 */               this.enabled = DownloadManagerImpl.this.controller.isPeerSourceEnabled("Incoming");
/*      */               
/* 5273 */               this.fixup_time = now;
/*      */             }
/*      */             
/* 5276 */             return this._pm;
/*      */           }
/*      */           
/*      */ 
/*      */           public int getType()
/*      */           {
/* 5282 */             return 6;
/*      */           }
/*      */           
/*      */ 
/*      */           public int getStatus()
/*      */           {
/* 5288 */             PEPeerManager delegate = fixup();
/*      */             
/* 5290 */             if (delegate == null)
/*      */             {
/* 5292 */               return 2;
/*      */             }
/* 5294 */             if (!this.enabled)
/*      */             {
/* 5296 */               return 1;
/*      */             }
/*      */             
/*      */ 
/* 5300 */             return 5;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public String getName()
/*      */           {
/* 5307 */             PEPeerManager delegate = fixup();
/*      */             
/* 5309 */             if ((delegate == null) || (!this.enabled))
/*      */             {
/* 5311 */               return "";
/*      */             }
/*      */             
/*      */ 
/* 5315 */             return MessageText.getString("tps.incoming.details", new String[] { String.valueOf(this.tcp), String.valueOf(this.udp + this.utp), String.valueOf(this.total) });
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public int getPeers()
/*      */           {
/* 5325 */             PEPeerManager delegate = fixup();
/*      */             
/* 5327 */             if ((delegate == null) || (!this.enabled))
/*      */             {
/* 5329 */               return -1;
/*      */             }
/*      */             
/*      */ 
/* 5333 */             return this.tcp + this.udp;
/*      */           }
/*      */           
/*      */         });
/*      */       }
/*      */       else
/*      */       {
/* 5340 */         tps = (List)tps_data[0];
/*      */       }
/*      */       
/* 5343 */       return tps;
/*      */     }
/*      */     finally
/*      */     {
/* 5347 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void addTPSListener(DownloadManagerTPSListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 5356 */       this.this_mon.enter();
/*      */       
/* 5358 */       if (this.tps_listeners == null)
/*      */       {
/* 5360 */         this.tps_listeners = new ArrayList(1);
/*      */       }
/*      */       
/* 5363 */       this.tps_listeners.add(listener);
/*      */     }
/*      */     finally
/*      */     {
/* 5367 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeTPSListener(DownloadManagerTPSListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 5376 */       this.this_mon.enter();
/*      */       
/* 5378 */       if (this.tps_listeners != null)
/*      */       {
/* 5380 */         this.tps_listeners.remove(listener);
/*      */         
/* 5382 */         if (this.tps_listeners.size() == 0)
/*      */         {
/* 5384 */           this.tps_listeners = null;
/*      */           
/* 5386 */           Object[] tps_data = (Object[])getUserData(TPS_Key);
/*      */           
/* 5388 */           if (tps_data != null)
/*      */           {
/* 5390 */             TOTorrent t = getTorrent();
/*      */             
/* 5392 */             if (t != null)
/*      */             {
/* 5394 */               t.removeListener((TOTorrentListener)tps_data[1]);
/*      */             }
/*      */             
/* 5397 */             setUserData(TPS_Key, null);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 5403 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private byte[] getIdentity()
/*      */   {
/* 5410 */     return this.dl_identity;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean equals(Object obj)
/*      */   {
/* 5420 */     if (this == obj)
/*      */     {
/* 5422 */       return true;
/*      */     }
/*      */     
/* 5425 */     if ((obj instanceof DownloadManagerImpl))
/*      */     {
/* 5427 */       DownloadManagerImpl other = (DownloadManagerImpl)obj;
/*      */       
/* 5429 */       byte[] id1 = getIdentity();
/* 5430 */       byte[] id2 = other.getIdentity();
/*      */       
/* 5432 */       if ((id1 == null) || (id2 == null))
/*      */       {
/* 5434 */         return false;
/*      */       }
/*      */       
/*      */ 
/* 5438 */       return Arrays.equals(id1, id2);
/*      */     }
/*      */     
/* 5441 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 5448 */     return this.dl_identity_hashcode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 5456 */     return "TorrentDLM: '" + getDisplayName() + "'";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/* 5464 */     return new Object[] { this.tracker_client };
/*      */   }
/*      */   
/*      */   public String toString() {
/* 5468 */     String hash = "<unknown>";
/*      */     
/* 5470 */     if (this.torrent != null) {
/*      */       try {
/* 5472 */         hash = ByteFormatter.encodeString(this.torrent.getHash());
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/* 5478 */     String status = DisplayFormatters.formatDownloadStatus(this);
/* 5479 */     if (status.length() > 10) {
/* 5480 */       status = status.substring(0, 10);
/*      */     }
/* 5482 */     return "DownloadManagerImpl#" + getPosition() + (getAssumedComplete() ? "s" : "d") + "@" + Integer.toHexString(hashCode()) + "/" + status + "/" + hash;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static class NoStackException
/*      */     extends Exception
/*      */   {
/*      */     protected NoStackException(String str)
/*      */     {
/* 5497 */       super();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 5505 */     writer.println(toString());
/*      */     
/* 5507 */     PEPeerManager pm = getPeerManager();
/*      */     try
/*      */     {
/* 5510 */       writer.indent();
/*      */       
/* 5512 */       writer.println("Save Dir: " + Debug.secretFileName(getSaveLocation().toString()));
/*      */       
/*      */ 
/* 5515 */       if (this.current_peers.size() > 0) {
/* 5516 */         writer.println("# Peers: " + this.current_peers.size());
/*      */       }
/*      */       
/* 5519 */       if (this.current_pieces.size() > 0) {
/* 5520 */         writer.println("# Pieces: " + this.current_pieces.size());
/*      */       }
/*      */       
/* 5523 */       writer.println("Listeners: DownloadManager=" + this.listeners.size() + "; Disk=" + this.controller.getDiskListenerCount() + "; Peer=" + this.peer_listeners.size() + "; Tracker=" + this.tracker_listeners.size());
/*      */       
/*      */ 
/*      */ 
/* 5527 */       writer.println("SR: " + this.iSeedingRank);
/*      */       
/*      */ 
/* 5530 */       String sFlags = "";
/* 5531 */       if (this.open_for_seeding) {
/* 5532 */         sFlags = sFlags + "Opened for Seeding; ";
/*      */       }
/*      */       
/* 5535 */       if (this.data_already_allocated) {
/* 5536 */         sFlags = sFlags + "Data Already Allocated; ";
/*      */       }
/*      */       
/* 5539 */       if (this.assumedComplete) {
/* 5540 */         sFlags = sFlags + "onlySeeding; ";
/*      */       }
/*      */       
/* 5543 */       if (this.persistent) {
/* 5544 */         sFlags = sFlags + "persistent; ";
/*      */       }
/*      */       
/* 5547 */       if (sFlags.length() > 0) {
/* 5548 */         writer.println("Flags: " + sFlags);
/*      */       }
/*      */       
/* 5551 */       this.stats.generateEvidence(writer);
/*      */       
/* 5553 */       this.download_manager_state.generateEvidence(writer);
/*      */       
/* 5555 */       if (pm != null) {
/* 5556 */         pm.generateEvidence(writer);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 5561 */       this.controller.generateEvidence(writer);
/*      */       
/* 5563 */       TRTrackerAnnouncer announcer = this.tracker_client;
/*      */       
/* 5565 */       if (announcer != null)
/*      */       {
/* 5567 */         announcer.generateEvidence(writer);
/*      */       }
/*      */       
/* 5570 */       TRTrackerScraperResponse scrape = getTrackerScrapeResponse();
/*      */       
/* 5572 */       if (scrape == null)
/*      */       {
/* 5574 */         writer.println("Scrape: null");
/*      */       }
/*      */       else {
/* 5577 */         writer.println("Scrape: " + scrape.getString());
/*      */       }
/*      */     }
/*      */     finally {
/* 5581 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void destroy(boolean is_duplicate)
/*      */   {
/* 5589 */     this.destroyed = true;
/*      */     
/* 5591 */     if (is_duplicate)
/*      */     {
/*      */ 
/*      */ 
/* 5595 */       this.controller.destroy();
/*      */     }
/*      */     else
/*      */     {
/*      */       try
/*      */       {
/* 5601 */         if (!getSaveLocation().exists())
/*      */           return;
/* 5603 */         DiskManager dm = getDiskManager();
/* 5604 */         if (dm != null) {
/* 5605 */           dm.downloadRemoved();
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 5610 */           SaveLocationChange move_details = DownloadManagerMoveHandler.onRemoval(this);
/* 5611 */           if (move_details == null) {
/*      */             return;
/*      */           }
/*      */           
/* 5615 */           boolean can_move_torrent = move_details.hasTorrentChange();
/*      */           try
/*      */           {
/* 5618 */             if (move_details.hasDownloadChange()) {
/* 5619 */               moveDataFiles(move_details.download_location, move_details.download_name);
/*      */             }
/*      */           }
/*      */           catch (Exception e) {
/* 5623 */             can_move_torrent = false;
/* 5624 */             Logger.log(new LogAlert(this, true, "Problem moving files to removed download directory", e));
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 5629 */           if (can_move_torrent) {
/*      */             try {
/* 5631 */               moveTorrentFile(move_details.torrent_location, move_details.torrent_name);
/*      */             }
/*      */             catch (Exception e) {
/* 5634 */               Logger.log(new LogAlert(this, true, "Problem moving torrent to removed download directory", e));
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {
/* 5640 */         clearFileLinks();
/*      */         
/* 5642 */         this.controller.destroy();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isDestroyed()
/*      */   {
/* 5650 */     return (this.destroyed) || (this.removing);
/*      */   }
/*      */   
/*      */   public int[] getStorageType(DiskManagerFileInfo[] info) {
/* 5654 */     String[] types = org.gudy.azureus2.core3.disk.impl.DiskManagerImpl.getStorageTypes(this);
/* 5655 */     int[] result = new int[info.length];
/* 5656 */     for (int i = 0; i < info.length; i++) {
/* 5657 */       result[i] = org.gudy.azureus2.core3.disk.impl.DiskManagerUtil.convertDMStorageTypeFromString(types[info[i].getIndex()]);
/*      */     }
/* 5659 */     return result;
/*      */   }
/*      */   
/*      */   public boolean canMoveDataFiles() {
/* 5663 */     if (!isPersistent()) return false;
/* 5664 */     return true;
/*      */   }
/*      */   
/*      */   public void rename(String name) throws DownloadManagerException {
/* 5668 */     boolean paused = pause();
/*      */     try {
/* 5670 */       renameDownload(name);
/* 5671 */       getDownloadState().setAttribute("displayname", name);
/* 5672 */       renameTorrentSafe(name);
/*      */     }
/*      */     finally {
/* 5675 */       if (paused) resume();
/*      */     }
/*      */   }
/*      */   
/*      */   public void renameTorrent(String name) throws DownloadManagerException {
/* 5680 */     moveTorrentFile(null, name);
/*      */   }
/*      */   
/*      */   public void renameTorrentSafe(String name) throws DownloadManagerException {
/* 5684 */     String torrent_parent = new File(getTorrentFileName()).getParent();
/* 5685 */     String torrent_name = name;
/*      */     
/* 5687 */     File new_path = new File(torrent_parent, torrent_name + ".torrent");
/* 5688 */     if (new_path.exists()) { new_path = null;
/*      */     }
/* 5690 */     for (int i = 1; i < 10; i++) {
/* 5691 */       if (new_path != null) break;
/* 5692 */       new_path = new File(torrent_parent, torrent_name + "(" + i + ").torrent");
/* 5693 */       if (new_path.exists()) { new_path = null;
/*      */       }
/*      */     }
/* 5696 */     if (new_path == null) {
/* 5697 */       throw new DownloadManagerException("cannot rename torrent file - file already exists");
/*      */     }
/*      */     
/* 5700 */     renameTorrent(new_path.getName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void requestAttention()
/*      */   {
/* 5707 */     fireGlobalManagerEvent(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void fireGlobalManagerEvent(int eventType)
/*      */   {
/* 5714 */     this.globalManager.fireGlobalManagerEvent(eventType, this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setFilePriorities(DiskManagerFileInfo[] fileInfos, int priority)
/*      */   {
/* 5723 */     for (DiskManagerFileInfo fileInfo : fileInfos) {
/* 5724 */       fileInfo.setPriority(priority);
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */