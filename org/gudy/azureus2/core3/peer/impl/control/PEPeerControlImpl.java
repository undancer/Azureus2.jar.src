/*      */ package org.gudy.azureus2.core3.peer.impl.control;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPConnectionManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.peermanager.PeerManagerRegistration;
/*      */ import com.aelitis.azureus.core.peermanager.control.PeerControlScheduler;
/*      */ import com.aelitis.azureus.core.peermanager.control.PeerControlSchedulerFactory;
/*      */ import com.aelitis.azureus.core.peermanager.nat.PeerNATTraverser;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerDatabase;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*      */ import com.aelitis.azureus.core.peermanager.unchoker.Unchoker;
/*      */ import com.aelitis.azureus.core.peermanager.unchoker.UnchokerFactory;
/*      */ import com.aelitis.azureus.core.peermanager.unchoker.UnchokerUtil;
/*      */ import com.aelitis.azureus.core.peermanager.uploadslots.UploadHelper;
/*      */ import com.aelitis.azureus.core.peermanager.uploadslots.UploadSlotManager;
/*      */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import java.io.PrintStream;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager.GettingThere;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerWriteRequest;
/*      */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*      */ import org.gudy.azureus2.core3.ipfilter.IPFilterListener;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.LogRelation;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager.StatsReceiver;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerAdapter;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerListener;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.peer.PEPiece;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerManagerStatsImpl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerStatsImpl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransportFactory;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPieceImpl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPieceWriteImpl;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerIdentityDataID;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerIdentityManager;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerUtils;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponsePeer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AddressUtils;
/*      */ import org.gudy.azureus2.core3.util.Average;
/*      */ import org.gudy.azureus2.core3.util.BrokenMd5Hasher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.plugins.network.Connection;
/*      */ import org.gudy.azureus2.plugins.network.OutgoingMessageQueue;
/*      */ import org.gudy.azureus2.plugins.peers.PeerDescriptor;
/*      */ 
/*      */ public class PEPeerControlImpl extends LogRelation implements PEPeerControl, org.gudy.azureus2.core3.disk.DiskManagerWriteRequestListener, com.aelitis.azureus.core.peermanager.control.PeerControlInstance, com.aelitis.azureus.core.peermanager.nat.PeerNATInitiator, org.gudy.azureus2.core3.disk.DiskManagerCheckRequestListener, IPFilterListener
/*      */ {
/*   96 */   private static final LogIDs LOGID = LogIDs.PEER;
/*      */   
/*      */   private static final boolean TEST_PERIODIC_SEEDING_SCAN_FAIL_HANDLING = false;
/*      */   
/*      */   private static final int WARNINGS_LIMIT = 2;
/*      */   
/*      */   private static final int CHECK_REASON_DOWNLOADED = 1;
/*      */   
/*      */   private static final int CHECK_REASON_COMPLETE = 2;
/*      */   
/*      */   private static final int CHECK_REASON_SCAN = 3;
/*      */   
/*      */   private static final int CHECK_REASON_SEEDING_CHECK = 4;
/*      */   
/*      */   private static final int CHECK_REASON_BAD_PIECE_CHECK = 5;
/*      */   
/*      */   private static final int SEED_CHECK_WAIT_MARKER = 65526;
/*      */   
/*      */   private static boolean disconnect_seeds_when_seeding;
/*      */   
/*      */   private static boolean enable_seeding_piece_rechecks;
/*      */   
/*      */   private static int stalled_piece_timeout;
/*      */   
/*      */   private static boolean fast_unchoke_new_peers;
/*      */   
/*      */   private static float ban_peer_discard_ratio;
/*      */   private static int ban_peer_discard_min_kb;
/*      */   private static boolean udp_fallback_for_failed_connection;
/*      */   private static boolean udp_fallback_for_dropped_connection;
/*      */   private static boolean udp_probe_enabled;
/*      */   private static boolean hide_a_piece;
/*      */   private static boolean prefer_udp_default;
/*      */   
/*      */   static
/*      */   {
/*  132 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Disconnect Seed", "Seeding Piece Check Recheck Enable", "peercontrol.stalled.piece.write.timeout", "Peer.Fast.Initial.Unchoke.Enabled", "Ip Filter Ban Discard Ratio", "Ip Filter Ban Discard Min KB", "peercontrol.udp.fallback.connect.fail", "peercontrol.udp.fallback.connect.drop", "peercontrol.udp.probe.enable", "peercontrol.hide.piece", "peercontrol.hide.piece.ds", "peercontrol.prefer.udp" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  153 */         PEPeerControlImpl.access$002(COConfigurationManager.getBooleanParameter("Disconnect Seed"));
/*  154 */         PEPeerControlImpl.access$102(COConfigurationManager.getBooleanParameter("Seeding Piece Check Recheck Enable"));
/*  155 */         PEPeerControlImpl.access$202(COConfigurationManager.getIntParameter("peercontrol.stalled.piece.write.timeout", 60000));
/*  156 */         PEPeerControlImpl.access$302(COConfigurationManager.getBooleanParameter("Peer.Fast.Initial.Unchoke.Enabled"));
/*  157 */         PEPeerControlImpl.access$402(COConfigurationManager.getFloatParameter("Ip Filter Ban Discard Ratio"));
/*  158 */         PEPeerControlImpl.access$502(COConfigurationManager.getIntParameter("Ip Filter Ban Discard Min KB"));
/*  159 */         PEPeerControlImpl.access$602(COConfigurationManager.getBooleanParameter("peercontrol.udp.fallback.connect.fail"));
/*  160 */         PEPeerControlImpl.access$702(COConfigurationManager.getBooleanParameter("peercontrol.udp.fallback.connect.drop"));
/*  161 */         PEPeerControlImpl.access$802(COConfigurationManager.getBooleanParameter("peercontrol.udp.probe.enable"));
/*  162 */         PEPeerControlImpl.access$902(COConfigurationManager.getBooleanParameter("peercontrol.hide.piece"));
/*  163 */         boolean hide_a_piece_ds = COConfigurationManager.getBooleanParameter("peercontrol.hide.piece.ds");
/*      */         
/*  165 */         if ((PEPeerControlImpl.hide_a_piece) && (!hide_a_piece_ds))
/*      */         {
/*  167 */           PEPeerControlImpl.access$002(false);
/*      */         }
/*      */         
/*  170 */         PEPeerControlImpl.access$1002(COConfigurationManager.getBooleanParameter("peercontrol.prefer.udp"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*  175 */   private static final IpFilter ip_filter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*      */   
/*  177 */   private volatile boolean is_running = false;
/*  178 */   private volatile boolean is_destroyed = false;
/*      */   
/*  180 */   private volatile ArrayList<PEPeerTransport> peer_transports_cow = new ArrayList();
/*  181 */   private final AEMonitor peer_transports_mon = new AEMonitor("PEPeerControl:PT");
/*      */   
/*      */   protected final PEPeerManagerAdapter adapter;
/*      */   
/*      */   private final DiskManager disk_mgr;
/*      */   
/*      */   private final DiskManagerPiece[] dm_pieces;
/*      */   
/*      */   private final boolean is_private_torrent;
/*      */   
/*      */   private PEPeerManager.StatsReceiver stats_receiver;
/*      */   
/*      */   private final PiecePicker piecePicker;
/*      */   
/*      */   private long lastNeededUndonePieceChange;
/*      */   private boolean seeding_mode;
/*      */   private boolean restart_initiated;
/*      */   private final int _nbPieces;
/*      */   private final PEPieceImpl[] pePieces;
/*      */   private int nbPiecesActive;
/*      */   private int nbPeersSnubbed;
/*      */   private PeerIdentityDataID _hash;
/*      */   private final byte[] _myPeerId;
/*      */   private PEPeerManagerStatsImpl _stats;
/*      */   private int stats_tick_count;
/*      */   private int _seeds;
/*      */   private int _peers;
/*      */   private int _remotesTCPNoLan;
/*      */   private int _remotesUDPNoLan;
/*      */   private int _remotesUTPNoLan;
/*      */   private int _tcpPendingConnections;
/*      */   private int _tcpConnectingConnections;
/*      */   private long last_remote_time;
/*      */   private long _timeStarted;
/*      */   private long _timeStarted_mono;
/*  216 */   private long _timeStartedSeeding = -1L;
/*  217 */   private long _timeStartedSeeding_mono = -1L;
/*      */   
/*      */   private long _timeFinished;
/*      */   
/*      */   private Average _averageReceptionSpeed;
/*      */   private long mainloop_loop_count;
/*  223 */   private static final int MAINLOOP_ONE_SECOND_INTERVAL = 1000 / PeerControlScheduler.SCHEDULE_PERIOD_MILLIS;
/*  224 */   private static final int MAINLOOP_FIVE_SECOND_INTERVAL = MAINLOOP_ONE_SECOND_INTERVAL * 5;
/*  225 */   private static final int MAINLOOP_TEN_SECOND_INTERVAL = MAINLOOP_ONE_SECOND_INTERVAL * 10;
/*  226 */   private static final int MAINLOOP_TWENTY_SECOND_INTERVAL = MAINLOOP_ONE_SECOND_INTERVAL * 20;
/*  227 */   private static final int MAINLOOP_THIRTY_SECOND_INTERVAL = MAINLOOP_ONE_SECOND_INTERVAL * 30;
/*  228 */   private static final int MAINLOOP_SIXTY_SECOND_INTERVAL = MAINLOOP_ONE_SECOND_INTERVAL * 60;
/*  229 */   private static final int MAINLOOP_TEN_MINUTE_INTERVAL = MAINLOOP_SIXTY_SECOND_INTERVAL * 10;
/*      */   
/*      */ 
/*  232 */   private volatile ArrayList<PEPeerManagerListener> peer_manager_listeners_cow = new ArrayList();
/*      */   
/*      */ 
/*  235 */   private final List<Object[]> piece_check_result_list = new ArrayList();
/*  236 */   private final AEMonitor piece_check_result_list_mon = new AEMonitor("PEPeerControl:PCRL");
/*      */   
/*      */   private boolean superSeedMode;
/*      */   
/*      */   private int superSeedModeCurrentPiece;
/*      */   
/*      */   private int superSeedModeNumberOfAnnounces;
/*      */   private SuperSeedPiece[] superSeedPieces;
/*      */   private int hidden_piece;
/*  245 */   private final AEMonitor this_mon = new AEMonitor("PEPeerControl");
/*      */   
/*      */   private long ip_filter_last_update_time;
/*      */   
/*      */   private Map<Object, Object> user_data;
/*      */   
/*      */   private Unchoker unchoker;
/*      */   
/*      */   private List<Object[]> external_rate_limiters_cow;
/*      */   
/*      */   private int bytes_queued_for_upload;
/*      */   
/*      */   private int connections_with_queued_data;
/*      */   
/*      */   private int connections_with_queued_data_blocked;
/*      */   private int connections_unchoked;
/*  261 */   private List<PEPeerTransport> sweepList = Collections.emptyList();
/*  262 */   private int nextPEXSweepIndex = 0;
/*      */   
/*      */ 
/*  265 */   private final UploadHelper upload_helper = new UploadHelper() {
/*      */     public int getPriority() {
/*  267 */       return 4;
/*      */     }
/*      */     
/*      */     public ArrayList<PEPeer> getAllPeers() {
/*  271 */       return PEPeerControlImpl.this.peer_transports_cow;
/*      */     }
/*      */     
/*      */     public boolean isSeeding() {
/*  275 */       return PEPeerControlImpl.this.seeding_mode;
/*      */     }
/*      */   };
/*      */   
/*      */ 
/*      */ 
/*  281 */   private final PeerDatabase peer_database = com.aelitis.azureus.core.peermanager.peerdb.PeerDatabaseFactory.createPeerDatabase();
/*      */   
/*  283 */   private int bad_piece_reported = -1;
/*      */   
/*  285 */   private int next_rescan_piece = -1;
/*  286 */   private long rescan_piece_time = -1L;
/*      */   
/*      */   private long last_eta;
/*      */   
/*      */   private long last_eta_smoothed;
/*      */   
/*      */   private long last_eta_calculation;
/*      */   
/*      */   private static final int MAX_UDP_CONNECTIONS = 16;
/*      */   private static final int PENDING_NAT_TRAVERSAL_MAX = 32;
/*      */   private static final int MAX_UDP_TRAVERSAL_COUNT = 3;
/*  297 */   private static final String PEER_NAT_TRAVERSE_DONE_KEY = PEPeerControlImpl.class.getName() + "::nat_trav_done";
/*      */   
/*  299 */   private final Map<String, PEPeerTransport> pending_nat_traversals = new LinkedHashMap(32, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, PEPeerTransport> eldest)
/*      */     {
/*      */ 
/*  306 */       return size() > 32;
/*      */     }
/*      */   };
/*      */   
/*      */   private int udp_traversal_count;
/*      */   
/*      */   private static final int UDP_RECONNECT_MAX = 16;
/*      */   
/*  314 */   private final Map<String, PEPeerTransport> udp_reconnects = new LinkedHashMap(16, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, PEPeerTransport> eldest)
/*      */     {
/*      */ 
/*  321 */       return size() > 16;
/*      */     }
/*      */   };
/*      */   
/*      */   private static final int UDP_RECONNECT_MIN_MILLIS = 10000;
/*      */   
/*      */   private long last_udp_reconnect;
/*      */   
/*      */   private boolean prefer_udp;
/*      */   
/*      */   private static final int PREFER_UDP_BLOOM_SIZE = 10000;
/*      */   private volatile BloomFilter prefer_udp_bloom;
/*  333 */   private final LimitedRateGroup upload_limited_rate_group = new LimitedRateGroup()
/*      */   {
/*      */     public String getName()
/*      */     {
/*  337 */       return "per_dl_up: " + PEPeerControlImpl.this.getDisplayName();
/*      */     }
/*      */     
/*  340 */     public int getRateLimitBytesPerSecond() { return PEPeerControlImpl.this.adapter.getUploadRateLimitBytesPerSecond(); }
/*      */     
/*      */ 
/*      */     public boolean isDisabled()
/*      */     {
/*  345 */       return PEPeerControlImpl.this.adapter.getUploadRateLimitBytesPerSecond() == -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void updateBytesUsed(int used) {}
/*      */   };
/*      */   
/*      */ 
/*  354 */   private final LimitedRateGroup download_limited_rate_group = new LimitedRateGroup()
/*      */   {
/*      */     public String getName()
/*      */     {
/*  358 */       return "per_dl_down: " + PEPeerControlImpl.this.getDisplayName();
/*      */     }
/*      */     
/*  361 */     public int getRateLimitBytesPerSecond() { return PEPeerControlImpl.this.adapter.getDownloadRateLimitBytesPerSecond(); }
/*      */     
/*      */ 
/*      */     public boolean isDisabled()
/*      */     {
/*  366 */       return PEPeerControlImpl.this.adapter.getDownloadRateLimitBytesPerSecond() == -1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void updateBytesUsed(int used) {}
/*      */   };
/*      */   
/*      */   private final int partition_id;
/*      */   
/*      */   private final boolean is_metadata_download;
/*      */   
/*      */   private int metadata_infodict_size;
/*      */   
/*      */   private DiskManager.GettingThere finish_in_progress;
/*      */   
/*      */   private long last_seed_disconnect_time;
/*      */   
/*  384 */   private final BloomFilter naughty_fast_extension_bloom = BloomFilterFactory.createRotating(BloomFilterFactory.createAddRemove4Bit(2000), 2);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final int FE_EVENT_LIMIT = 5;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PEPeerControlImpl(byte[] _peer_id, PEPeerManagerAdapter _adapter, DiskManager _diskManager, int _partition_id)
/*      */   {
/*  398 */     this._myPeerId = _peer_id;
/*  399 */     this.adapter = _adapter;
/*  400 */     this.disk_mgr = _diskManager;
/*  401 */     this.partition_id = _partition_id;
/*      */     
/*  403 */     boolean is_private = false;
/*      */     try
/*      */     {
/*  406 */       is_private = this.disk_mgr.getTorrent().getPrivate();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  410 */       Debug.out(e);
/*      */     }
/*      */     
/*  413 */     this.is_private_torrent = is_private;
/*      */     
/*  415 */     this.is_metadata_download = this.adapter.isMetadataDownload();
/*      */     
/*  417 */     if (!this.is_metadata_download) {
/*  418 */       this.metadata_infodict_size = this.adapter.getTorrentInfoDictSize();
/*      */     }
/*      */     
/*  421 */     this._nbPieces = this.disk_mgr.getNbPieces();
/*  422 */     this.dm_pieces = this.disk_mgr.getPieces();
/*      */     
/*  424 */     this.pePieces = new PEPieceImpl[this._nbPieces];
/*      */     
/*  426 */     this.hidden_piece = (hide_a_piece ? (int)(Math.abs(this.adapter.getRandomSeed()) % this._nbPieces) : -1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  435 */     this.piecePicker = com.aelitis.azureus.core.peermanager.piecepicker.PiecePickerFactory.create(this);
/*      */     
/*  437 */     ip_filter.addListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void start()
/*      */   {
/*      */     try
/*      */     {
/*  448 */       this._hash = PeerIdentityManager.createDataID(this.disk_mgr.getTorrent().getHash());
/*      */ 
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*      */ 
/*  454 */       Debug.printStackTrace(e);
/*      */       
/*  456 */       this._hash = PeerIdentityManager.createDataID(new byte[20]);
/*      */     }
/*      */     
/*      */ 
/*  460 */     for (int i = 0; i < this._nbPieces; i++)
/*      */     {
/*  462 */       DiskManagerPiece dmPiece = this.dm_pieces[i];
/*  463 */       if ((!dmPiece.isDone()) && (dmPiece.getNbWritten() > 0))
/*      */       {
/*  465 */         addPiece(new PEPieceImpl(this, dmPiece, 0), i, true, null);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  470 */     this.peer_transports_cow = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  475 */     this.mainloop_loop_count = 0L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  480 */     this._averageReceptionSpeed = Average.getInstance(1000, 30);
/*      */     
/*      */ 
/*  483 */     this._stats = new PEPeerManagerStatsImpl(this);
/*      */     
/*  485 */     this.superSeedMode = ((COConfigurationManager.getBooleanParameter("Use Super Seeding")) && (getRemaining() == 0L));
/*      */     
/*  487 */     this.superSeedModeCurrentPiece = 0;
/*      */     
/*  489 */     if (this.superSeedMode)
/*      */     {
/*  491 */       initialiseSuperSeedMode();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  499 */     checkFinished(true);
/*      */     
/*  501 */     UploadSlotManager.getSingleton().registerHelper(this.upload_helper);
/*      */     
/*  503 */     this.lastNeededUndonePieceChange = Long.MIN_VALUE;
/*  504 */     this._timeStarted = SystemTime.getCurrentTime();
/*  505 */     this._timeStarted_mono = SystemTime.getMonotonousTime();
/*      */     
/*  507 */     this.is_running = true;
/*      */     
/*      */ 
/*      */ 
/*  511 */     PeerManagerRegistration reg = this.adapter.getPeerManagerRegistration();
/*      */     
/*  513 */     if (reg != null)
/*      */     {
/*  515 */       reg.activate(this);
/*      */     }
/*      */     
/*  518 */     PeerNATTraverser.getSingleton().register(this);
/*      */     
/*  520 */     PeerControlSchedulerFactory.getSingleton(this.partition_id).register(this);
/*      */   }
/*      */   
/*      */   public void stopAll()
/*      */   {
/*  525 */     this.is_running = false;
/*      */     
/*  527 */     UploadSlotManager.getSingleton().deregisterHelper(this.upload_helper);
/*      */     
/*  529 */     PeerControlSchedulerFactory.getSingleton(this.partition_id).unregister(this);
/*      */     
/*  531 */     PeerNATTraverser.getSingleton().unregister(this);
/*      */     
/*      */ 
/*      */ 
/*  535 */     PeerManagerRegistration reg = this.adapter.getPeerManagerRegistration();
/*      */     
/*  537 */     if (reg != null)
/*      */     {
/*  539 */       reg.deactivate();
/*      */     }
/*      */     
/*  542 */     closeAndRemoveAllPeers("download stopped", false);
/*      */     
/*      */ 
/*  545 */     for (int i = 0; i < this._nbPieces; i++)
/*      */     {
/*  547 */       if (this.pePieces[i] != null) {
/*  548 */         removePiece(this.pePieces[i], i);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  553 */     ip_filter.removeListener(this);
/*      */     
/*  555 */     this.piecePicker.destroy();
/*      */     
/*  557 */     ArrayList<PEPeerManagerListener> peer_manager_listeners = this.peer_manager_listeners_cow;
/*      */     
/*  559 */     for (int i = 0; i < peer_manager_listeners.size(); i++)
/*      */     {
/*  561 */       ((PEPeerManagerListener)peer_manager_listeners.get(i)).destroyed();
/*      */     }
/*      */     
/*  564 */     this.sweepList = Collections.emptyList();
/*      */     
/*  566 */     this.pending_nat_traversals.clear();
/*      */     
/*  568 */     this.udp_reconnects.clear();
/*      */     
/*  570 */     this.is_destroyed = true;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPartitionID()
/*      */   {
/*  576 */     return this.partition_id;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isDestroyed()
/*      */   {
/*  582 */     return this.is_destroyed;
/*      */   }
/*      */   
/*  585 */   public DiskManager getDiskManager() { return this.disk_mgr; }
/*      */   
/*      */   public PiecePicker getPiecePicker() {
/*  588 */     return this.piecePicker;
/*      */   }
/*      */   
/*  591 */   public PEPeerManagerAdapter getAdapter() { return this.adapter; }
/*      */   
/*  593 */   public String getDisplayName() { return this.adapter.getDisplayName(); }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  598 */     return getDisplayName();
/*      */   }
/*      */   
/*      */ 
/*      */   public void schedule()
/*      */   {
/*  604 */     if (this.finish_in_progress != null)
/*      */     {
/*      */ 
/*      */ 
/*  608 */       if (this.finish_in_progress.hasGotThere())
/*      */       {
/*  610 */         this.finish_in_progress = null;
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  615 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  622 */       updateStats();
/*      */       
/*  624 */       updateTrackerAnnounceInterval();
/*      */       
/*  626 */       doConnectionChecks();
/*      */       
/*  628 */       processPieceChecks();
/*      */       
/*  630 */       if (this.finish_in_progress != null)
/*      */       {
/*      */ 
/*      */ 
/*  634 */         return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  641 */       if (!this.seeding_mode)
/*      */       {
/*  643 */         checkCompletedPieces();
/*      */       }
/*      */       
/*  646 */       checkBadPieces();
/*      */       
/*  648 */       checkInterested();
/*      */       
/*  650 */       this.piecePicker.updateAvailability();
/*      */       
/*  652 */       checkCompletionState();
/*      */       
/*  654 */       if (this.finish_in_progress != null)
/*      */       {
/*      */ 
/*      */ 
/*  658 */         return;
/*      */       }
/*      */       
/*  661 */       checkSeeds();
/*      */       
/*  663 */       if (!this.seeding_mode)
/*      */       {
/*      */ 
/*  666 */         checkRequests();
/*      */         
/*  668 */         this.piecePicker.allocateRequests();
/*      */         
/*  670 */         checkRescan();
/*  671 */         checkSpeedAndReserved();
/*      */         
/*  673 */         check99PercentBug();
/*      */       }
/*      */       
/*      */ 
/*  677 */       updatePeersInSuperSeedMode();
/*  678 */       doUnchokes();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  682 */       Debug.printStackTrace(e);
/*      */     }
/*  684 */     this.mainloop_loop_count += 1L;
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
/*      */   private void analyseTrackerResponse(TRTrackerAnnouncerResponse tracker_response)
/*      */   {
/*  701 */     TRTrackerAnnouncerResponsePeer[] peers = tracker_response.getPeers();
/*      */     
/*  703 */     if (peers != null) {
/*  704 */       addPeersFromTracker(tracker_response.getPeers());
/*      */     }
/*      */     
/*  707 */     Map extensions = tracker_response.getExtensions();
/*      */     
/*  709 */     if (extensions != null) {
/*  710 */       addExtendedPeersFromTracker(extensions);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void processTrackerResponse(TRTrackerAnnouncerResponse response)
/*      */   {
/*  719 */     if (this.is_running) {
/*  720 */       analyseTrackerResponse(response);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addExtendedPeersFromTracker(Map extensions)
/*      */   {
/*  728 */     Map protocols = (Map)extensions.get("protocols");
/*      */     
/*  730 */     if (protocols != null)
/*      */     {
/*  732 */       System.out.println("PEPeerControl: tracker response contained protocol extensions");
/*      */       
/*  734 */       Iterator protocol_it = protocols.keySet().iterator();
/*      */       
/*  736 */       while (protocol_it.hasNext())
/*      */       {
/*  738 */         String protocol_name = (String)protocol_it.next();
/*      */         
/*  740 */         Map protocol = (Map)protocols.get(protocol_name);
/*      */         
/*  742 */         List transports = PEPeerTransportFactory.createExtendedTransports(this, protocol_name, protocol);
/*      */         
/*  744 */         for (int i = 0; i < transports.size(); i++)
/*      */         {
/*  746 */           PEPeer transport = (PEPeer)transports.get(i);
/*      */           
/*  748 */           addPeer(transport);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public List<PEPeer> getPeers()
/*      */   {
/*  757 */     return this.peer_transports_cow;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public List<PEPeer> getPeers(String address)
/*      */   {
/*  764 */     List<PEPeer> result = new ArrayList();
/*      */     
/*  766 */     Iterator<PEPeerTransport> it = this.peer_transports_cow.iterator();
/*      */     
/*  768 */     if (address.contains(":"))
/*      */     {
/*      */       try
/*      */       {
/*      */ 
/*  773 */         byte[] address_bytes = InetAddress.getByName(address).getAddress();
/*      */         
/*  775 */         while (it.hasNext())
/*      */         {
/*  777 */           PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */           
/*  779 */           String peer_address = peer.getIp();
/*      */           
/*  781 */           if (peer_address.contains(":"))
/*      */           {
/*  783 */             byte[] peer_bytes = (byte[])peer.getUserData("ipv6.bytes");
/*      */             
/*  785 */             if (peer_bytes == null)
/*      */             {
/*  787 */               peer_bytes = InetAddress.getByName(peer_address).getAddress();
/*      */               
/*  789 */               peer.setUserData("ipv6.bytes", peer_bytes);
/*      */             }
/*      */             
/*  792 */             if (Arrays.equals(address_bytes, peer_bytes))
/*      */             {
/*  794 */               result.add(peer);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  799 */         return result;
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  807 */     while (it.hasNext())
/*      */     {
/*  809 */       PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */       
/*  811 */       if (peer.getIp().equals(address))
/*      */       {
/*  813 */         result.add(peer);
/*      */       }
/*      */     }
/*      */     
/*  817 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPendingPeerCount()
/*      */   {
/*  823 */     return this.peer_database.getDiscoveredPeerCount();
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerDescriptor[] getPendingPeers()
/*      */   {
/*  829 */     return (PeerDescriptor[])this.peer_database.getDiscoveredPeers();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PeerDescriptor[] getPendingPeers(String address)
/*      */   {
/*  836 */     return (PeerDescriptor[])this.peer_database.getDiscoveredPeers(address);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPeer(PEPeer _transport)
/*      */   {
/*  843 */     if (!(_transport instanceof PEPeerTransport))
/*      */     {
/*  845 */       throw new RuntimeException("invalid class");
/*      */     }
/*      */     
/*  848 */     PEPeerTransport transport = (PEPeerTransport)_transport;
/*      */     
/*  850 */     if (!ip_filter.isInRange(transport.getIp(), getDisplayName(), getTorrentHash()))
/*      */     {
/*  852 */       ArrayList<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/*  854 */       if (!peer_transports.contains(transport))
/*      */       {
/*  856 */         addToPeerTransports(transport);
/*      */         
/*  858 */         transport.start();
/*      */       }
/*      */       else {
/*  861 */         Debug.out("addPeer():: peer_transports.contains(transport): SHOULD NEVER HAPPEN !");
/*  862 */         transport.closeConnection("already connected");
/*      */       }
/*      */     }
/*      */     else {
/*  866 */       transport.closeConnection("IP address blocked by filters");
/*      */     }
/*      */   }
/*      */   
/*      */   protected byte[] getTorrentHash()
/*      */   {
/*      */     try
/*      */     {
/*  874 */       return this.disk_mgr.getTorrent().getHash();
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*  878 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removePeer(PEPeer _transport)
/*      */   {
/*  885 */     removePeer(_transport, "remove peer");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removePeer(PEPeer _transport, String reason)
/*      */   {
/*  893 */     if (!(_transport instanceof PEPeerTransport))
/*      */     {
/*  895 */       throw new RuntimeException("invalid class");
/*      */     }
/*      */     
/*  898 */     PEPeerTransport transport = (PEPeerTransport)_transport;
/*      */     
/*  900 */     closeAndRemovePeer(transport, reason, true);
/*      */   }
/*      */   
/*      */   private void closeAndRemovePeer(PEPeerTransport peer, String reason, boolean log_if_not_found)
/*      */   {
/*  905 */     boolean removed = false;
/*      */     
/*      */     try
/*      */     {
/*  909 */       this.peer_transports_mon.enter();
/*      */       
/*  911 */       if (this.peer_transports_cow.contains(peer))
/*      */       {
/*  913 */         ArrayList new_peer_transports = new ArrayList(this.peer_transports_cow);
/*      */         
/*  915 */         new_peer_transports.remove(peer);
/*  916 */         this.peer_transports_cow = new_peer_transports;
/*  917 */         removed = true;
/*      */       }
/*      */     }
/*      */     finally {
/*  921 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/*  924 */     if (removed) {
/*  925 */       peer.closeConnection(reason);
/*  926 */       peerRemoved(peer);
/*      */ 
/*      */     }
/*  929 */     else if (!log_if_not_found) {}
/*      */   }
/*      */   
/*      */ 
/*      */   private void closeAndRemoveAllPeers(String reason, boolean reconnect)
/*      */   {
/*      */     List<PEPeerTransport> peer_transports;
/*      */     
/*      */     try
/*      */     {
/*  939 */       this.peer_transports_mon.enter();
/*      */       
/*  941 */       peer_transports = this.peer_transports_cow;
/*      */       
/*  943 */       this.peer_transports_cow = new ArrayList(0);
/*      */     }
/*      */     finally {
/*  946 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/*  949 */     for (int i = 0; i < peer_transports.size(); i++) {
/*  950 */       PEPeerTransport peer = (PEPeerTransport)peer_transports.get(i);
/*      */       
/*      */       try
/*      */       {
/*  954 */         peer.closeConnection(reason);
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */ 
/*  961 */         Debug.printStackTrace(e);
/*      */       }
/*      */       try
/*      */       {
/*  965 */         peerRemoved(peer);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  969 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/*  973 */     if (reconnect) { PEPeerTransport reconnected_peer;
/*  974 */       for (int i = 0; i < peer_transports.size(); i++) {
/*  975 */         PEPeerTransport peer = (PEPeerTransport)peer_transports.get(i);
/*      */         
/*  977 */         reconnected_peer = peer.reconnect(false, false);
/*      */       }
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
/*      */   public void addPeer(String ip_address, int tcp_port, int udp_port, boolean use_crypto, Map user_data)
/*      */   {
/*  993 */     byte type = use_crypto ? 1 : 0;
/*  994 */     PeerItem peer_item = PeerItemFactory.createPeerItem(ip_address, tcp_port, PeerItem.convertSourceID("Plugin"), type, udp_port, (byte)1, 0);
/*      */     
/*  996 */     byte crypto_level = 1;
/*      */     
/*  998 */     if (!isAlreadyConnected(peer_item))
/*      */     {
/*      */ 
/*      */ 
/* 1002 */       boolean tcp_ok = (TCPNetworkManager.TCP_OUTGOING_ENABLED) && (tcp_port > 0);
/* 1003 */       boolean udp_ok = (UDPNetworkManager.UDP_OUTGOING_ENABLED) && (udp_port > 0);
/*      */       String fail_reason;
/* 1005 */       String fail_reason; if ((tcp_ok) && (((!this.prefer_udp) && (!prefer_udp_default)) || (!udp_ok)))
/*      */       {
/* 1007 */         fail_reason = makeNewOutgoingConnection("Plugin", ip_address, tcp_port, udp_port, true, use_crypto, crypto_level, user_data);
/*      */       } else { String fail_reason;
/* 1009 */         if (udp_ok)
/*      */         {
/* 1011 */           fail_reason = makeNewOutgoingConnection("Plugin", ip_address, tcp_port, udp_port, false, use_crypto, crypto_level, user_data);
/*      */         }
/*      */         else
/*      */         {
/* 1015 */           fail_reason = "No usable protocol";
/*      */         }
/*      */       }
/* 1018 */       if (fail_reason != null) { Debug.out("Injected peer " + ip_address + ":" + tcp_port + " was not added - " + fail_reason);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerDiscovered(String peer_source, String ip_address, int tcp_port, int udp_port, boolean use_crypto)
/*      */   {
/* 1030 */     if (this.peer_database != null)
/*      */     {
/* 1032 */       ArrayList<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 1034 */       for (int x = 0; x < peer_transports.size(); x++)
/*      */       {
/* 1036 */         PEPeer transport = (PEPeer)peer_transports.get(x);
/*      */         
/*      */ 
/*      */ 
/* 1040 */         if (ip_address.equals(transport.getIp()))
/*      */         {
/* 1042 */           boolean same_allowed = (COConfigurationManager.getBooleanParameter("Allow Same IP Peers")) || (transport.getIp().equals("127.0.0.1"));
/*      */           
/*      */ 
/*      */ 
/* 1046 */           if ((!same_allowed) || (tcp_port == transport.getPort()))
/*      */           {
/* 1048 */             return;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1053 */       byte type = use_crypto ? 1 : 0;
/*      */       
/* 1055 */       PeerItem item = PeerItemFactory.createPeerItem(ip_address, tcp_port, PeerItem.convertSourceID(peer_source), type, udp_port, (byte)1, 0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1064 */       peerDiscovered(null, item);
/*      */       
/* 1066 */       this.peer_database.addDiscoveredPeer(item);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addPeersFromTracker(TRTrackerAnnouncerResponsePeer[] peers)
/*      */   {
/* 1075 */     for (int i = 0; i < peers.length; i++) {
/* 1076 */       TRTrackerAnnouncerResponsePeer peer = peers[i];
/*      */       
/* 1078 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 1080 */       boolean already_connected = false;
/*      */       
/* 1082 */       for (int x = 0; x < peer_transports.size(); x++) {
/* 1083 */         PEPeerTransport transport = (PEPeerTransport)peer_transports.get(x);
/*      */         
/*      */ 
/*      */ 
/* 1087 */         if (peer.getAddress().equals(transport.getIp()))
/*      */         {
/* 1089 */           boolean same_allowed = (COConfigurationManager.getBooleanParameter("Allow Same IP Peers")) || (transport.getIp().equals("127.0.0.1"));
/*      */           
/*      */ 
/*      */ 
/* 1093 */           if ((!same_allowed) || (peer.getPort() == transport.getPort())) {
/* 1094 */             already_connected = true;
/* 1095 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1100 */       if (!already_connected)
/*      */       {
/* 1102 */         if (this.peer_database != null)
/*      */         {
/* 1104 */           byte type = peer.getProtocol() == 2 ? 1 : 0;
/*      */           
/* 1106 */           byte crypto_level = peer.getAZVersion() < 3 ? 1 : 2;
/*      */           
/* 1108 */           PeerItem item = PeerItemFactory.createPeerItem(peer.getAddress(), peer.getPort(), PeerItem.convertSourceID(peer.getSource()), type, peer.getUDPPort(), crypto_level, peer.getUploadSpeed());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1117 */           peerDiscovered(null, item);
/*      */           
/* 1119 */           this.peer_database.addDiscoveredPeer(item);
/*      */         }
/*      */         
/* 1122 */         int http_port = peer.getHTTPPort();
/*      */         
/* 1124 */         if ((http_port != 0) && (!this.seeding_mode))
/*      */         {
/* 1126 */           this.adapter.addHTTPSeed(peer.getAddress(), http_port);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private String makeNewOutgoingConnection(String peer_source, String address, int tcp_port, int udp_port, boolean use_tcp, boolean require_crypto, byte crypto_level, Map user_data)
/*      */   {
/* 1152 */     if (ip_filter.isInRange(address, getDisplayName(), getTorrentHash()))
/*      */     {
/* 1154 */       return "IPFilter block";
/*      */     }
/*      */     
/* 1157 */     String net_cat = AENetworkClassifier.categoriseAddress(address);
/*      */     
/* 1159 */     if (!this.adapter.isNetworkEnabled(net_cat))
/*      */     {
/* 1161 */       return "Network '" + net_cat + "' is not enabled";
/*      */     }
/*      */     
/* 1164 */     if (!this.adapter.isPeerSourceEnabled(peer_source))
/*      */     {
/* 1166 */       return "Peer source '" + peer_source + "' is not enabled";
/*      */     }
/*      */     
/* 1169 */     boolean is_priority_connection = false;
/*      */     
/* 1171 */     if (user_data != null)
/*      */     {
/* 1173 */       Boolean pc = (Boolean)user_data.get(org.gudy.azureus2.plugins.peers.Peer.PR_PRIORITY_CONNECTION);
/*      */       
/* 1175 */       if ((pc != null) && (pc.booleanValue()))
/*      */       {
/* 1177 */         is_priority_connection = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1183 */     boolean max_reached = getMaxNewConnectionsAllowed(net_cat) == 0;
/*      */     
/* 1185 */     if (max_reached)
/*      */     {
/* 1187 */       if (peer_source == "Plugin") { if (doOptimisticDisconnect(AddressUtils.isLANLocalAddress(address) != 2, is_priority_connection, net_cat)) {}
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1193 */         return "Too many connections";
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1199 */     boolean same_allowed = (COConfigurationManager.getBooleanParameter("Allow Same IP Peers")) || (address.equals("127.0.0.1"));
/*      */     
/* 1201 */     if ((!same_allowed) && (PeerIdentityManager.containsIPAddress(this._hash, address)))
/*      */     {
/* 1203 */       return "Already connected to IP";
/*      */     }
/*      */     
/* 1206 */     if (PeerUtils.ignorePeerPort(tcp_port)) {
/* 1207 */       if (Logger.isEnabled()) {
/* 1208 */         Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Skipping connect with " + address + ":" + tcp_port + " as peer port is in ignore list."));
/*      */       }
/*      */       
/*      */ 
/* 1212 */       return "TCP port '" + tcp_port + "' is in ignore list";
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1218 */     PEPeerTransport real = PEPeerTransportFactory.createTransport(this, peer_source, address, tcp_port, udp_port, use_tcp, require_crypto, crypto_level, user_data);
/*      */     
/* 1220 */     addToPeerTransports(real);
/*      */     
/* 1222 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkCompletedPieces()
/*      */   {
/* 1234 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L) {
/* 1235 */       return;
/*      */     }
/*      */     
/* 1238 */     for (int i = 0; i < this._nbPieces; i++) {
/* 1239 */       DiskManagerPiece dmPiece = this.dm_pieces[i];
/*      */       
/* 1241 */       if (dmPiece.isNeedsCheck())
/*      */       {
/*      */ 
/* 1244 */         dmPiece.setChecking();
/*      */         
/* 1246 */         DiskManagerCheckRequest req = this.disk_mgr.createCheckRequest(i, new Integer(1));
/*      */         
/*      */ 
/*      */ 
/* 1250 */         req.setAdHoc(false);
/*      */         
/* 1252 */         this.disk_mgr.enqueueCheckRequest(req, this);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean checkEmptyPiece(int pieceNumber)
/*      */   {
/* 1265 */     if (this.piecePicker.isInEndGameMode())
/*      */     {
/* 1267 */       return false;
/*      */     }
/*      */     
/* 1270 */     PEPiece pePiece = this.pePieces[pieceNumber];
/* 1271 */     DiskManagerPiece dmPiece = this.dm_pieces[pieceNumber];
/*      */     
/* 1273 */     if ((pePiece == null) || (pePiece.isRequested())) {
/* 1274 */       return false;
/*      */     }
/* 1276 */     if ((dmPiece.getNbWritten() > 0) || (pePiece.getNbUnrequested() < pePiece.getNbBlocks()) || (pePiece.getReservedBy() != null)) {
/* 1277 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1281 */     pePiece.reset();
/*      */     
/* 1283 */     removePiece(pePiece, pieceNumber);
/* 1284 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkSpeedAndReserved()
/*      */   {
/* 1294 */     if (this.mainloop_loop_count % MAINLOOP_FIVE_SECOND_INTERVAL != 0L) {
/* 1295 */       return;
/*      */     }
/* 1297 */     int nbPieces = this._nbPieces;
/* 1298 */     PEPieceImpl[] pieces = this.pePieces;
/*      */     
/* 1300 */     for (int i = 0; i < nbPieces; i++)
/*      */     {
/*      */ 
/* 1303 */       checkEmptyPiece(i);
/*      */       
/*      */ 
/* 1306 */       PEPieceImpl pePiece = pieces[i];
/*      */       
/*      */ 
/* 1309 */       if (pePiece != null)
/*      */       {
/* 1311 */         long timeSinceActivity = pePiece.getTimeSinceLastActivity() / 1000L;
/*      */         
/* 1313 */         int pieceSpeed = pePiece.getSpeed();
/*      */         
/* 1315 */         if ((pieceSpeed > 0) && (timeSinceActivity * pieceSpeed * 0.25D > 16.0D))
/*      */         {
/* 1317 */           if (pePiece.getNbUnrequested() > 2) {
/* 1318 */             pePiece.setSpeed(pieceSpeed - 1);
/*      */           } else {
/* 1320 */             pePiece.setSpeed(0);
/*      */           }
/*      */         }
/*      */         
/* 1324 */         if (timeSinceActivity > 120L)
/*      */         {
/* 1326 */           pePiece.setSpeed(0);
/*      */           
/* 1328 */           String reservingPeer = pePiece.getReservedBy();
/* 1329 */           if (reservingPeer != null)
/*      */           {
/* 1331 */             PEPeerTransport pt = getTransportFromAddress(reservingPeer);
/*      */             
/*      */ 
/*      */ 
/* 1335 */             if (needsMD5CheckOnCompletion(i)) {
/* 1336 */               badPeerDetected(reservingPeer, i);
/* 1337 */             } else if (pt != null) {
/* 1338 */               closeAndRemovePeer(pt, "Reserved piece data timeout; 120 seconds", true);
/*      */             }
/* 1340 */             pePiece.setReservedBy(null);
/*      */           }
/*      */           
/* 1343 */           if (!this.piecePicker.isInEndGameMode()) {
/* 1344 */             pePiece.checkRequests();
/*      */           }
/*      */           
/* 1347 */           checkEmptyPiece(i);
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
/*      */ 
/*      */   private void check99PercentBug()
/*      */   {
/* 1361 */     if (this.mainloop_loop_count % MAINLOOP_SIXTY_SECOND_INTERVAL == 0L)
/*      */     {
/* 1363 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 1365 */       for (int i = 0; i < this.pePieces.length; i++)
/*      */       {
/* 1367 */         PEPiece pe_piece = this.pePieces[i];
/*      */         
/* 1369 */         if (pe_piece != null)
/*      */         {
/* 1371 */           DiskManagerPiece dm_piece = this.dm_pieces[i];
/*      */           
/* 1373 */           if (!dm_piece.isDone())
/*      */           {
/* 1375 */             if (pe_piece.isDownloaded())
/*      */             {
/* 1377 */               if (now - pe_piece.getLastDownloadTime(now) > stalled_piece_timeout)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1387 */                 if ((!this.disk_mgr.hasOutstandingWriteRequestForPiece(i)) && (!this.disk_mgr.hasOutstandingReadRequestForPiece(i)) && (!this.disk_mgr.hasOutstandingCheckRequestForPiece(i)))
/*      */                 {
/*      */ 
/*      */ 
/* 1391 */                   Debug.out("Fully downloaded piece stalled pending write, resetting p_piece " + i);
/*      */                   
/* 1393 */                   pe_piece.reset();
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 1402 */       if (this.hidden_piece >= 0)
/*      */       {
/* 1404 */         int hp_avail = this.piecePicker.getAvailability(this.hidden_piece);
/*      */         
/* 1406 */         if (hp_avail < (this.dm_pieces[this.hidden_piece].isDone() ? 2 : 1))
/*      */         {
/* 1408 */           int[] avails = this.piecePicker.getAvailability();
/*      */           
/* 1410 */           int num = 0;
/*      */           
/* 1412 */           for (int i = 0; i < avails.length; i++)
/*      */           {
/* 1414 */             if ((avails[i] > 0) && (!this.dm_pieces[i].isDone()) && (this.pePieces[i] == null))
/*      */             {
/* 1416 */               num++;
/*      */             }
/*      */           }
/*      */           
/* 1420 */           if (num > 0)
/*      */           {
/* 1422 */             num = RandomUtils.nextInt(num);
/*      */             
/* 1424 */             int backup = -1;
/*      */             
/* 1426 */             for (int i = 0; i < avails.length; i++)
/*      */             {
/* 1428 */               if ((avails[i] > 0) && (!this.dm_pieces[i].isDone()) && (this.pePieces[i] == null))
/*      */               {
/* 1430 */                 if (backup == -1)
/*      */                 {
/* 1432 */                   backup = i;
/*      */                 }
/*      */                 
/* 1435 */                 if (num == 0)
/*      */                 {
/* 1437 */                   this.hidden_piece = i;
/*      */                   
/* 1439 */                   backup = -1;
/*      */                   
/* 1441 */                   break;
/*      */                 }
/*      */                 
/* 1444 */                 num--;
/*      */               }
/*      */             }
/*      */             
/* 1448 */             if (backup != -1)
/*      */             {
/* 1450 */               this.hidden_piece = backup;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkInterested()
/*      */   {
/* 1460 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L) {
/* 1461 */       return;
/*      */     }
/*      */     
/* 1464 */     if (this.lastNeededUndonePieceChange >= this.piecePicker.getNeededUndonePieceChange()) {
/* 1465 */       return;
/*      */     }
/* 1467 */     this.lastNeededUndonePieceChange = this.piecePicker.getNeededUndonePieceChange();
/*      */     
/* 1469 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 1470 */     int cntPeersSnubbed = 0;
/* 1471 */     for (int i = 0; i < peer_transports.size(); i++)
/*      */     {
/* 1473 */       PEPeerTransport peer = (PEPeerTransport)peer_transports.get(i);
/* 1474 */       peer.checkInterested();
/* 1475 */       if (peer.isSnubbed())
/* 1476 */         cntPeersSnubbed++;
/*      */     }
/* 1478 */     setNbPeersSnubbed(cntPeersSnubbed);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void processPieceChecks()
/*      */   {
/* 1489 */     if (this.piece_check_result_list.size() > 0)
/*      */     {
/*      */       List pieces;
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 1496 */         this.piece_check_result_list_mon.enter();
/*      */         
/* 1498 */         pieces = new ArrayList(this.piece_check_result_list);
/*      */         
/* 1500 */         this.piece_check_result_list.clear();
/*      */       }
/*      */       finally
/*      */       {
/* 1504 */         this.piece_check_result_list_mon.exit();
/*      */       }
/*      */       
/* 1507 */       Iterator it = pieces.iterator();
/*      */       
/* 1509 */       while (it.hasNext())
/*      */       {
/* 1511 */         Object[] data = (Object[])it.next();
/*      */         
/*      */ 
/*      */ 
/* 1515 */         processPieceCheckResult((DiskManagerCheckRequest)data[0], ((Integer)data[1]).intValue());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkBadPieces()
/*      */   {
/* 1524 */     if (this.mainloop_loop_count % MAINLOOP_SIXTY_SECOND_INTERVAL == 0L)
/*      */     {
/* 1526 */       if (this.bad_piece_reported != -1)
/*      */       {
/* 1528 */         DiskManagerCheckRequest req = this.disk_mgr.createCheckRequest(this.bad_piece_reported, new Integer(5));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1533 */         req.setLowPriority(true);
/*      */         
/* 1535 */         if (Logger.isEnabled())
/*      */         {
/* 1537 */           Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Rescanning reported-bad piece " + this.bad_piece_reported));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1544 */         this.bad_piece_reported = -1;
/*      */         try
/*      */         {
/* 1547 */           this.disk_mgr.enqueueCheckRequest(req, this);
/*      */ 
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1552 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkRescan()
/*      */   {
/* 1561 */     if (this.rescan_piece_time == 0L)
/*      */     {
/*      */ 
/*      */ 
/* 1565 */       return;
/*      */     }
/*      */     
/* 1568 */     if (this.next_rescan_piece == -1)
/*      */     {
/* 1570 */       if (this.mainloop_loop_count % MAINLOOP_FIVE_SECOND_INTERVAL == 0L)
/*      */       {
/* 1572 */         if (this.adapter.isPeriodicRescanEnabled())
/*      */         {
/* 1574 */           this.next_rescan_piece = 0;
/*      */         }
/*      */         
/*      */       }
/*      */     }
/* 1579 */     else if (this.mainloop_loop_count % MAINLOOP_TEN_MINUTE_INTERVAL == 0L)
/*      */     {
/* 1581 */       if (!this.adapter.isPeriodicRescanEnabled())
/*      */       {
/* 1583 */         this.next_rescan_piece = -1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1588 */     if (this.next_rescan_piece == -1)
/*      */     {
/* 1590 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1595 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 1597 */     if (this.rescan_piece_time > now)
/*      */     {
/* 1599 */       this.rescan_piece_time = now;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1604 */     long piece_size = this.disk_mgr.getPieceLength();
/*      */     
/* 1606 */     long millis_per_piece = piece_size / 250L;
/*      */     
/* 1608 */     if (now - this.rescan_piece_time < millis_per_piece)
/*      */     {
/* 1610 */       return;
/*      */     }
/*      */     
/* 1613 */     while (this.next_rescan_piece != -1)
/*      */     {
/* 1615 */       int this_piece = this.next_rescan_piece;
/*      */       
/* 1617 */       this.next_rescan_piece += 1;
/*      */       
/* 1619 */       if (this.next_rescan_piece == this._nbPieces)
/*      */       {
/* 1621 */         this.next_rescan_piece = -1;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1628 */       if ((this.pePieces[this_piece] == null) && (!this.dm_pieces[this_piece].isDone()) && (this.dm_pieces[this_piece].isNeeded()))
/*      */       {
/* 1630 */         DiskManagerCheckRequest req = this.disk_mgr.createCheckRequest(this_piece, new Integer(3));
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1635 */         req.setLowPriority(true);
/*      */         
/* 1637 */         if (Logger.isEnabled())
/*      */         {
/* 1639 */           Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Rescanning piece " + this_piece));
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1646 */         this.rescan_piece_time = 0L;
/*      */         try
/*      */         {
/* 1649 */           this.disk_mgr.enqueueCheckRequest(req, this);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1653 */           this.rescan_piece_time = now;
/*      */           
/* 1655 */           Debug.printStackTrace(e);
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
/*      */   public void badPieceReported(PEPeerTransport originator, int piece_number)
/*      */   {
/* 1668 */     Debug.outNoStack(getDisplayName() + ": bad piece #" + piece_number + " reported by " + originator.getIp());
/*      */     
/* 1670 */     if ((piece_number < 0) || (piece_number >= this._nbPieces))
/*      */     {
/* 1672 */       return;
/*      */     }
/*      */     
/* 1675 */     this.bad_piece_reported = piece_number;
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
/*      */   public boolean isFastExtensionPermitted(PEPeerTransport originator)
/*      */   {
/*      */     try
/*      */     {
/* 1692 */       byte[] key = originator.getIp().getBytes("ISO-8859-1");
/*      */       
/* 1694 */       synchronized (this.naughty_fast_extension_bloom)
/*      */       {
/* 1696 */         int events = this.naughty_fast_extension_bloom.add(key);
/*      */         
/* 1698 */         if (events < 5)
/*      */         {
/* 1700 */           return true;
/*      */         }
/*      */         
/* 1703 */         Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Fast extension disabled for " + originator.getIp() + " due to repeat connections"));
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/* 1710 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void reportBadFastExtensionUse(PEPeerTransport originator)
/*      */   {
/*      */     try
/*      */     {
/* 1718 */       byte[] key = originator.getIp().getBytes("ISO-8859-1");
/*      */       
/* 1720 */       synchronized (this.naughty_fast_extension_bloom)
/*      */       {
/* 1722 */         if (this.naughty_fast_extension_bloom.add(key) == 5)
/*      */         {
/* 1724 */           Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Fast extension disabled for " + originator.getIp() + " due to repeat requests for the same pieces"));
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setStatsReceiver(PEPeerManager.StatsReceiver receiver)
/*      */   {
/* 1736 */     this.stats_receiver = receiver;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void statsRequest(PEPeerTransport originator, Map request)
/*      */   {
/* 1744 */     Map reply = new HashMap();
/*      */     
/* 1746 */     this.adapter.statsRequest(originator, request, reply);
/*      */     
/* 1748 */     if (reply.size() > 0)
/*      */     {
/* 1750 */       originator.sendStatsReply(reply);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void statsReply(PEPeerTransport originator, Map reply)
/*      */   {
/* 1759 */     PEPeerManager.StatsReceiver receiver = this.stats_receiver;
/*      */     
/* 1761 */     if (receiver != null)
/*      */     {
/* 1763 */       receiver.receiveStats(originator, reply);
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
/*      */   private void checkFinished(boolean start_of_day)
/*      */   {
/* 1776 */     boolean all_pieces_done = this.disk_mgr.getRemainingExcludingDND() == 0L;
/*      */     
/* 1778 */     if (all_pieces_done)
/*      */     {
/* 1780 */       this.seeding_mode = true;
/*      */       
/* 1782 */       this.prefer_udp_bloom = null;
/*      */       
/* 1784 */       this.piecePicker.clearEndGameChunks();
/*      */       
/* 1786 */       if (!start_of_day) {
/* 1787 */         this.adapter.setStateFinishing();
/*      */       }
/* 1789 */       this._timeFinished = SystemTime.getCurrentTime();
/* 1790 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/*      */ 
/* 1793 */       for (int i = 0; i < peer_transports.size(); i++)
/*      */       {
/* 1795 */         PEPeerTransport pc = (PEPeerTransport)peer_transports.get(i);
/* 1796 */         pc.setSnubbed(false);
/*      */       }
/* 1798 */       setNbPeersSnubbed(0);
/*      */       
/* 1800 */       boolean checkPieces = COConfigurationManager.getBooleanParameter("Check Pieces on Completion");
/*      */       
/*      */ 
/* 1803 */       if ((checkPieces) && (!start_of_day))
/*      */       {
/* 1805 */         DiskManagerCheckRequest req = this.disk_mgr.createCheckRequest(-1, new Integer(2));
/* 1806 */         this.disk_mgr.enqueueCompleteRecheckRequest(req, this);
/*      */       }
/*      */       
/* 1809 */       this._timeStartedSeeding = SystemTime.getCurrentTime();
/* 1810 */       this._timeStartedSeeding_mono = SystemTime.getMonotonousTime();
/*      */       try
/*      */       {
/* 1813 */         this.disk_mgr.saveResumeData(false);
/*      */       }
/*      */       catch (Throwable e) {
/* 1816 */         Debug.out("Failed to save resume data", e);
/*      */       }
/*      */       
/* 1819 */       this.adapter.setStateSeeding(start_of_day);
/*      */       
/* 1821 */       final AESemaphore waiting_it = new AESemaphore("PEC:DE");
/*      */       
/* 1823 */       new AEThread2("PEC:DE")
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/* 1828 */             PEPeerControlImpl.this.disk_mgr.downloadEnded(new org.gudy.azureus2.core3.disk.DiskManager.OperationStatus()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void gonnaTakeAWhile(DiskManager.GettingThere gt)
/*      */               {
/*      */ 
/* 1835 */                 boolean async_set = false;
/*      */                 
/* 1837 */                 synchronized (PEPeerControlImpl.this)
/*      */                 {
/* 1839 */                   if (PEPeerControlImpl.this.finish_in_progress == null)
/*      */                   {
/* 1841 */                     PEPeerControlImpl.this.finish_in_progress = gt;
/*      */                     
/* 1843 */                     async_set = true;
/*      */                   }
/*      */                 }
/*      */                 
/* 1847 */                 if (async_set)
/*      */                 {
/* 1849 */                   PEPeerControlImpl.7.this.val$waiting_it.release();
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */           finally {
/* 1855 */             waiting_it.release();
/*      */           }
/*      */           
/*      */         }
/* 1859 */       }.start();
/* 1860 */       waiting_it.reserve();
/*      */     }
/*      */     else
/*      */     {
/* 1864 */       this.seeding_mode = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void checkCompletionState()
/*      */   {
/* 1871 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L)
/*      */     {
/* 1873 */       return;
/*      */     }
/*      */     
/* 1876 */     boolean dm_done = this.disk_mgr.getRemainingExcludingDND() == 0L;
/*      */     
/* 1878 */     if (this.seeding_mode)
/*      */     {
/* 1880 */       if (!dm_done)
/*      */       {
/* 1882 */         this.seeding_mode = false;
/*      */         
/* 1884 */         this._timeStartedSeeding = -1L;
/* 1885 */         this._timeStartedSeeding_mono = -1L;
/* 1886 */         this._timeFinished = 0L;
/*      */         
/* 1888 */         Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Turning off seeding mode for PEPeerManager"));
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/* 1895 */     else if (dm_done)
/*      */     {
/* 1897 */       checkFinished(false);
/*      */       
/* 1899 */       if (this.seeding_mode)
/*      */       {
/* 1901 */         Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Turning on seeding mode for PEPeerManager"));
/*      */       }
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
/*      */   private void checkRequests()
/*      */   {
/* 1921 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L)
/*      */     {
/* 1923 */       return;
/*      */     }
/*      */     
/* 1926 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */ 
/* 1929 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 1930 */     for (int i = peer_transports.size() - 1; i >= 0; i--)
/*      */     {
/* 1932 */       PEPeerTransport pc = (PEPeerTransport)peer_transports.get(i);
/* 1933 */       if (pc.getPeerState() == 30)
/*      */       {
/* 1935 */         List expired = pc.getExpiredRequests();
/* 1936 */         if ((expired != null) && (expired.size() > 0))
/*      */         {
/* 1938 */           boolean isSeed = pc.isSeed();
/*      */           
/* 1940 */           long timeSinceGoodData = pc.getTimeSinceGoodDataReceived();
/* 1941 */           if ((timeSinceGoodData < 0L) || (timeSinceGoodData > 60000L)) {
/* 1942 */             pc.setSnubbed(true);
/*      */           }
/*      */           
/* 1945 */           DiskManagerReadRequest request = (DiskManagerReadRequest)expired.get(0);
/*      */           
/* 1947 */           long timeSinceData = pc.getTimeSinceLastDataMessageReceived();
/* 1948 */           if (timeSinceData >= 0L) {} boolean noData = timeSinceData > 'Ϩ' * (isSeed ? 120 : 60);
/* 1949 */           long timeSinceOldestRequest = now - request.getTimeCreated(now);
/*      */           
/*      */ 
/*      */ 
/* 1953 */           for (int j = (timeSinceOldestRequest > 120000L) && (noData) ? 0 : 1; j < expired.size(); j++)
/*      */           {
/*      */ 
/* 1956 */             request = (DiskManagerReadRequest)expired.get(j);
/*      */             
/* 1958 */             pc.sendCancel(request);
/*      */             
/* 1960 */             int pieceNumber = request.getPieceNumber();
/* 1961 */             PEPiece pe_piece = this.pePieces[pieceNumber];
/*      */             
/* 1963 */             if (pe_piece != null) {
/* 1964 */               pe_piece.clearRequested(request.getOffset() / 16384);
/*      */             }
/* 1966 */             if (!this.piecePicker.isInEndGameMode()) {
/* 1967 */               checkEmptyPiece(pieceNumber);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void updateTrackerAnnounceInterval()
/*      */   {
/* 1977 */     if (this.mainloop_loop_count % MAINLOOP_FIVE_SECOND_INTERVAL != 0L) {
/* 1978 */       return;
/*      */     }
/*      */     
/* 1981 */     int WANT_LIMIT = 100;
/*      */     
/* 1983 */     int[] _num_wanted = getMaxNewConnectionsAllowed();
/*      */     
/*      */     int num_wanted;
/*      */     int num_wanted;
/* 1987 */     if (_num_wanted[0] < 0)
/*      */     {
/* 1989 */       num_wanted = 100;
/*      */     }
/*      */     else
/*      */     {
/* 1993 */       num_wanted = _num_wanted[0] + _num_wanted[1];
/*      */       
/* 1995 */       if (num_wanted > 100)
/*      */       {
/* 1997 */         num_wanted = 100;
/*      */       }
/*      */     }
/*      */     
/* 2001 */     boolean has_remote = this.adapter.isNATHealthy();
/*      */     
/* 2003 */     if (has_remote)
/*      */     {
/*      */ 
/* 2006 */       num_wanted = (int)(num_wanted / 1.5D);
/*      */     }
/*      */     
/*      */ 
/* 2010 */     int current_connection_count = PeerIdentityManager.getIdentityCount(this._hash);
/*      */     
/* 2012 */     TRTrackerScraperResponse tsr = this.adapter.getTrackerScrapeResponse();
/*      */     
/* 2014 */     if ((tsr != null) && (tsr.isValid())) {
/* 2015 */       int num_seeds = tsr.getSeeds();
/* 2016 */       int num_peers = tsr.getPeers();
/*      */       
/*      */       int swarm_size;
/*      */       int swarm_size;
/* 2020 */       if (this.seeding_mode)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2025 */         float ratio = num_peers / (num_seeds + num_peers);
/* 2026 */         swarm_size = (int)(num_peers * ratio);
/*      */       }
/*      */       else {
/* 2029 */         swarm_size = num_peers + num_seeds;
/*      */       }
/*      */       
/* 2032 */       if (swarm_size < num_wanted) {
/* 2033 */         num_wanted = swarm_size;
/*      */       }
/*      */     }
/*      */     
/* 2037 */     if (num_wanted < 1) {
/* 2038 */       this.adapter.setTrackerRefreshDelayOverrides(100);
/* 2039 */       return;
/*      */     }
/*      */     
/* 2042 */     if (current_connection_count == 0) { current_connection_count = 1;
/*      */     }
/* 2044 */     int current_percent = current_connection_count * 100 / (current_connection_count + num_wanted);
/*      */     
/* 2046 */     this.adapter.setTrackerRefreshDelayOverrides(current_percent);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasDownloadablePiece()
/*      */   {
/* 2052 */     return this.piecePicker.hasDownloadablePiece();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getBytesQueuedForUpload()
/*      */   {
/* 2058 */     return this.bytes_queued_for_upload;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPeersWithUploadQueued()
/*      */   {
/* 2064 */     return this.connections_with_queued_data;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPeersWithUploadBlocked()
/*      */   {
/* 2070 */     return this.connections_with_queued_data_blocked;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPeersUnchoked()
/*      */   {
/* 2076 */     return this.connections_unchoked;
/*      */   }
/*      */   
/*      */   public int[] getAvailability()
/*      */   {
/* 2081 */     return this.piecePicker.getAvailability();
/*      */   }
/*      */   
/*      */ 
/*      */   public float getMinAvailability()
/*      */   {
/* 2087 */     return this.piecePicker.getMinAvailability();
/*      */   }
/*      */   
/*      */   public float getMinAvailability(int file_index)
/*      */   {
/* 2092 */     return this.piecePicker.getMinAvailability(file_index);
/*      */   }
/*      */   
/*      */   public long getBytesUnavailable() {
/* 2096 */     return this.piecePicker.getBytesUnavailable();
/*      */   }
/*      */   
/*      */   public float getAvgAvail()
/*      */   {
/* 2101 */     return this.piecePicker.getAvgAvail();
/*      */   }
/*      */   
/*      */   public long getAvailWentBadTime()
/*      */   {
/* 2106 */     long went_bad = this.piecePicker.getAvailWentBadTime();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2111 */     if ((this.piecePicker.getMinAvailability() < 1.0D) && (this.last_seed_disconnect_time > went_bad - 5000L))
/*      */     {
/* 2113 */       went_bad = this.last_seed_disconnect_time;
/*      */     }
/*      */     
/* 2116 */     return went_bad;
/*      */   }
/*      */   
/*      */   public void addPeerTransport(PEPeerTransport transport) {
/* 2120 */     if (!ip_filter.isInRange(transport.getIp(), getDisplayName(), getTorrentHash())) {
/* 2121 */       ArrayList peer_transports = this.peer_transports_cow;
/*      */       
/* 2123 */       if (!peer_transports.contains(transport)) {
/* 2124 */         addToPeerTransports(transport);
/*      */       }
/*      */       else {
/* 2127 */         Debug.out("addPeerTransport():: peer_transports.contains(transport): SHOULD NEVER HAPPEN !");
/* 2128 */         transport.closeConnection("already connected");
/*      */       }
/*      */     }
/*      */     else {
/* 2132 */       transport.closeConnection("IP address blocked by filters");
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
/*      */   private void doUnchokes()
/*      */   {
/* 2146 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L) {
/* 2147 */       return;
/*      */     }
/*      */     
/* 2150 */     int max_to_unchoke = this.adapter.getMaxUploads();
/* 2151 */     ArrayList peer_transports = this.peer_transports_cow;
/*      */     
/*      */ 
/* 2154 */     if (this.seeding_mode) {
/* 2155 */       if ((this.unchoker == null) || (!this.unchoker.isSeedingUnchoker())) {
/* 2156 */         this.unchoker = UnchokerFactory.getSingleton().getUnchoker(true);
/*      */       }
/*      */       
/*      */     }
/* 2160 */     else if ((this.unchoker == null) || (this.unchoker.isSeedingUnchoker())) {
/* 2161 */       this.unchoker = UnchokerFactory.getSingleton().getUnchoker(false);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2167 */     if (this.mainloop_loop_count % MAINLOOP_TEN_SECOND_INTERVAL == 0L)
/*      */     {
/* 2169 */       boolean refresh = this.mainloop_loop_count % MAINLOOP_THIRTY_SECOND_INTERVAL == 0L;
/*      */       
/* 2171 */       boolean do_high_latency_peers = this.mainloop_loop_count % MAINLOOP_TWENTY_SECOND_INTERVAL == 0L;
/*      */       
/* 2173 */       if (do_high_latency_peers)
/*      */       {
/* 2175 */         boolean ok = false;
/*      */         
/* 2177 */         for (String net : AENetworkClassifier.AT_NON_PUBLIC)
/*      */         {
/* 2179 */           if (this.adapter.isNetworkEnabled(net))
/*      */           {
/* 2181 */             ok = true;
/*      */             
/* 2183 */             break;
/*      */           }
/*      */         }
/*      */         
/* 2187 */         if (!ok)
/*      */         {
/* 2189 */           do_high_latency_peers = false;
/*      */         }
/*      */       }
/*      */       
/* 2193 */       this.unchoker.calculateUnchokes(max_to_unchoke, peer_transports, refresh, this.adapter.hasPriorityConnection(), do_high_latency_peers);
/*      */       
/* 2195 */       ArrayList chokes = this.unchoker.getChokes();
/* 2196 */       ArrayList unchokes = this.unchoker.getUnchokes();
/*      */       
/* 2198 */       addFastUnchokes(unchokes);
/*      */       
/* 2200 */       UnchokerUtil.performChokes(chokes, unchokes);
/*      */     }
/* 2202 */     else if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL == 0L)
/*      */     {
/* 2204 */       ArrayList unchokes = this.unchoker.getImmediateUnchokes(max_to_unchoke, peer_transports);
/*      */       
/* 2206 */       addFastUnchokes(unchokes);
/*      */       
/* 2208 */       UnchokerUtil.performChokes(null, unchokes);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void addFastUnchokes(ArrayList peers_to_unchoke)
/*      */   {
/* 2217 */     for (Iterator<PEPeerTransport> it = this.peer_transports_cow.iterator(); it.hasNext();)
/*      */     {
/* 2219 */       PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */       
/* 2221 */       if ((peer.getConnectionState() == 4) && (UnchokerUtil.isUnchokable(peer, true)) && (!peers_to_unchoke.contains(peer)))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2228 */         if (peer.isLANLocal())
/*      */         {
/* 2230 */           peers_to_unchoke.add(peer);
/*      */         }
/* 2232 */         else if ((fast_unchoke_new_peers) && (peer.getData("fast_unchoke_done") == null))
/*      */         {
/*      */ 
/* 2235 */           peer.setData("fast_unchoke_done", "");
/*      */           
/* 2237 */           peers_to_unchoke.add(peer);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void sendHave(int pieceNumber)
/*      */   {
/* 2245 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */     
/* 2247 */     for (int i = 0; i < peer_transports.size(); i++)
/*      */     {
/* 2249 */       PEPeerTransport pc = (PEPeerTransport)peer_transports.get(i);
/*      */       
/* 2251 */       pc.sendHave(pieceNumber);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void checkSeeds()
/*      */   {
/* 2259 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L) {
/* 2260 */       return;
/*      */     }
/* 2262 */     if (!disconnect_seeds_when_seeding) {
/* 2263 */       return;
/*      */     }
/*      */     
/* 2266 */     List<PEPeerTransport> to_close = null;
/*      */     
/* 2268 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 2269 */     for (int i = 0; i < peer_transports.size(); i++) {
/* 2270 */       PEPeerTransport pc = (PEPeerTransport)peer_transports.get(i);
/*      */       
/* 2272 */       if ((pc != null) && (pc.getPeerState() == 30) && (((isSeeding()) && (pc.isSeed())) || (pc.isRelativeSeed()))) {
/* 2273 */         if (to_close == null) to_close = new ArrayList();
/* 2274 */         to_close.add(pc);
/*      */       }
/*      */     }
/*      */     
/* 2278 */     if (to_close != null) {
/* 2279 */       for (int i = 0; i < to_close.size(); i++) {
/* 2280 */         closeAndRemovePeer((PEPeerTransport)to_close.get(i), "disconnect other seed when seeding", false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void updateStats()
/*      */   {
/* 2290 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL != 0L) {
/* 2291 */       return;
/*      */     }
/*      */     
/* 2294 */     this.stats_tick_count += 1;
/*      */     
/*      */ 
/* 2297 */     ArrayList<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */     
/* 2299 */     int new_pending_tcp_connections = 0;
/* 2300 */     int new_connecting_tcp_connections = 0;
/*      */     
/* 2302 */     int new_seeds = 0;
/* 2303 */     int new_peers = 0;
/* 2304 */     int new_tcp_incoming = 0;
/* 2305 */     int new_udp_incoming = 0;
/* 2306 */     int new_utp_incoming = 0;
/*      */     
/* 2308 */     int bytes_queued = 0;
/* 2309 */     int con_queued = 0;
/* 2310 */     int con_blocked = 0;
/* 2311 */     int con_unchoked = 0;
/*      */     
/* 2313 */     for (Iterator<PEPeerTransport> it = peer_transports.iterator(); it.hasNext();)
/*      */     {
/* 2315 */       PEPeerTransport pc = (PEPeerTransport)it.next();
/*      */       
/* 2317 */       if (pc.getPeerState() == 30)
/*      */       {
/* 2319 */         if (!pc.isChokedByMe())
/*      */         {
/* 2321 */           con_unchoked++;
/*      */         }
/*      */         
/* 2324 */         Connection connection = pc.getPluginConnection();
/*      */         
/* 2326 */         if (connection != null)
/*      */         {
/* 2328 */           OutgoingMessageQueue mq = connection.getOutgoingMessageQueue();
/*      */           
/* 2330 */           int q = mq.getDataQueuedBytes() + mq.getProtocolQueuedBytes();
/*      */           
/* 2332 */           bytes_queued += q;
/*      */           
/* 2334 */           if (q > 0)
/*      */           {
/* 2336 */             con_queued++;
/*      */             
/* 2338 */             if (mq.isBlocked())
/*      */             {
/* 2340 */               con_blocked++;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 2345 */         if (pc.isSeed()) {
/* 2346 */           new_seeds++;
/*      */         } else {
/* 2348 */           new_peers++;
/*      */         }
/* 2350 */         if ((pc.isIncoming()) && (!pc.isLANLocal()))
/*      */         {
/* 2352 */           if (pc.isTCP())
/*      */           {
/* 2354 */             new_tcp_incoming++;
/*      */           }
/*      */           else
/*      */           {
/* 2358 */             String protocol = pc.getProtocol();
/*      */             
/* 2360 */             if (protocol.equals("UDP"))
/*      */             {
/* 2362 */               new_udp_incoming++;
/*      */             }
/*      */             else
/*      */             {
/* 2366 */               new_utp_incoming++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2371 */       else if (pc.isTCP())
/*      */       {
/* 2373 */         int c_state = pc.getConnectionState();
/*      */         
/* 2375 */         if (c_state == 0)
/*      */         {
/* 2377 */           new_pending_tcp_connections++;
/*      */         }
/* 2379 */         else if (c_state == 1)
/*      */         {
/* 2381 */           new_connecting_tcp_connections++;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2387 */     this._seeds = new_seeds;
/* 2388 */     this._peers = new_peers;
/* 2389 */     this._remotesTCPNoLan = new_tcp_incoming;
/* 2390 */     this._remotesUDPNoLan = new_udp_incoming;
/* 2391 */     this._remotesUTPNoLan = new_utp_incoming;
/* 2392 */     this._tcpPendingConnections = new_pending_tcp_connections;
/* 2393 */     this._tcpConnectingConnections = new_connecting_tcp_connections;
/*      */     
/* 2395 */     this.bytes_queued_for_upload = bytes_queued;
/* 2396 */     this.connections_with_queued_data = con_queued;
/* 2397 */     this.connections_with_queued_data_blocked = con_blocked;
/* 2398 */     this.connections_unchoked = con_unchoked;
/*      */     
/* 2400 */     this._stats.update(this.stats_tick_count);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void requestCanceled(DiskManagerReadRequest request)
/*      */   {
/* 2409 */     int pieceNumber = request.getPieceNumber();
/* 2410 */     PEPiece pe_piece = this.pePieces[pieceNumber];
/* 2411 */     if (pe_piece != null) {
/* 2412 */       pe_piece.clearRequested(request.getOffset() / 16384);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PEPeerControl getControl()
/*      */   {
/* 2420 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public byte[][] getSecrets(int crypto_level)
/*      */   {
/* 2427 */     return this.adapter.getSecrets(crypto_level);
/*      */   }
/*      */   
/*      */   public byte[] getHash()
/*      */   {
/* 2432 */     return this._hash.getDataID();
/*      */   }
/*      */   
/*      */ 
/*      */   public PeerIdentityDataID getPeerIdentityDataID()
/*      */   {
/* 2438 */     return this._hash;
/*      */   }
/*      */   
/*      */   public byte[] getPeerId()
/*      */   {
/* 2443 */     return this._myPeerId;
/*      */   }
/*      */   
/*      */   public long getRemaining()
/*      */   {
/* 2448 */     return this.disk_mgr.getRemaining();
/*      */   }
/*      */   
/*      */   public void discarded(PEPeer peer, int length)
/*      */   {
/* 2453 */     if (length > 0) {
/* 2454 */       this._stats.discarded(peer, length);
/*      */       
/*      */ 
/*      */ 
/* 2458 */       if ((ban_peer_discard_ratio > 0.0F) && (!this.piecePicker.isInEndGameMode()) && (!this.piecePicker.hasEndGameModeBeenAbandoned()))
/*      */       {
/* 2460 */         long received = peer.getStats().getTotalDataBytesReceived();
/* 2461 */         long discarded = peer.getStats().getTotalBytesDiscarded();
/*      */         
/* 2463 */         long non_discarded = received - discarded;
/*      */         
/* 2465 */         if (non_discarded < 0L)
/*      */         {
/* 2467 */           non_discarded = 0L;
/*      */         }
/*      */         
/* 2470 */         if (discarded >= ban_peer_discard_min_kb * 1024L)
/*      */         {
/* 2472 */           if ((non_discarded == 0L) || ((float)discarded / (float)non_discarded >= ban_peer_discard_ratio))
/*      */           {
/*      */ 
/* 2475 */             badPeerDetected(peer.getIp(), -1);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void dataBytesReceived(PEPeer peer, int length) {
/* 2483 */     if (length > 0) {
/* 2484 */       this._stats.dataBytesReceived(peer, length);
/*      */       
/* 2486 */       this._averageReceptionSpeed.addValue(length);
/*      */     }
/*      */   }
/*      */   
/*      */   public void protocolBytesReceived(PEPeer peer, int length)
/*      */   {
/* 2492 */     if (length > 0) {
/* 2493 */       this._stats.protocolBytesReceived(peer, length);
/*      */     }
/*      */   }
/*      */   
/*      */   public void dataBytesSent(PEPeer peer, int length) {
/* 2498 */     if (length > 0) {
/* 2499 */       this._stats.dataBytesSent(peer, length);
/*      */     }
/*      */   }
/*      */   
/*      */   public void protocolBytesSent(PEPeer peer, int length)
/*      */   {
/* 2505 */     if (length > 0) {
/* 2506 */       this._stats.protocolBytesSent(peer, length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void writeCompleted(DiskManagerWriteRequest request)
/*      */   {
/* 2515 */     int pieceNumber = request.getPieceNumber();
/*      */     
/* 2517 */     DiskManagerPiece dm_piece = this.dm_pieces[pieceNumber];
/*      */     
/* 2519 */     if (!dm_piece.isDone())
/*      */     {
/* 2521 */       PEPiece pePiece = this.pePieces[pieceNumber];
/*      */       
/* 2523 */       if (pePiece != null)
/*      */       {
/* 2525 */         Object user_data = request.getUserData();
/*      */         
/*      */         String key;
/*      */         String key;
/* 2529 */         if ((user_data instanceof String))
/*      */         {
/* 2531 */           key = (String)user_data;
/*      */         } else { String key;
/* 2533 */           if ((user_data instanceof PEPeer))
/*      */           {
/* 2535 */             key = ((PEPeer)user_data).getIp();
/*      */           }
/*      */           else
/*      */           {
/* 2539 */             key = "<none>";
/*      */           }
/*      */         }
/* 2542 */         pePiece.setWritten(key, request.getOffset() / 16384);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2554 */         dm_piece.setWritten(request.getOffset() / 16384);
/*      */       }
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
/*      */   public void writeBlock(int pieceNumber, int offset, DirectByteBuffer data, Object sender, boolean cancel)
/*      */   {
/* 2579 */     int blockNumber = offset / 16384;
/* 2580 */     DiskManagerPiece dmPiece = this.dm_pieces[pieceNumber];
/* 2581 */     if (dmPiece.isWritten(blockNumber))
/*      */     {
/* 2583 */       data.returnToPool();
/* 2584 */       return;
/*      */     }
/*      */     
/* 2587 */     PEPiece pe_piece = this.pePieces[pieceNumber];
/*      */     
/* 2589 */     if (pe_piece != null)
/*      */     {
/* 2591 */       pe_piece.setDownloaded(offset);
/*      */     }
/*      */     
/* 2594 */     DiskManagerWriteRequest request = this.disk_mgr.createWriteRequest(pieceNumber, offset, data, sender);
/* 2595 */     this.disk_mgr.enqueueWriteRequest(request, this);
/*      */     
/* 2597 */     if (this.piecePicker.isInEndGameMode())
/* 2598 */       this.piecePicker.removeFromEndGameModeChunks(pieceNumber, offset);
/* 2599 */     if ((cancel) || (this.piecePicker.isInEndGameMode()))
/*      */     {
/*      */ 
/* 2602 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 2603 */       for (int i = 0; i < peer_transports.size(); i++)
/*      */       {
/* 2605 */         PEPeerTransport connection = (PEPeerTransport)peer_transports.get(i);
/* 2606 */         DiskManagerReadRequest dmr = this.disk_mgr.createReadRequest(pieceNumber, offset, dmPiece.getBlockSize(blockNumber));
/* 2607 */         connection.sendCancel(dmr);
/*      */       }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isWritten(int piece_number, int offset)
/*      */   {
/* 2643 */     return this.dm_pieces[piece_number].isWritten(offset / 16384);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean validateReadRequest(PEPeerTransport originator, int pieceNumber, int offset, int length)
/*      */   {
/* 2653 */     if (this.disk_mgr.checkBlockConsistencyForRead(originator.getClient() + ": " + originator.getIp(), true, pieceNumber, offset, length))
/*      */     {
/* 2655 */       if ((enable_seeding_piece_rechecks) && (isSeeding()))
/*      */       {
/* 2657 */         DiskManagerPiece dm_piece = this.dm_pieces[pieceNumber];
/*      */         
/* 2659 */         int read_count = dm_piece.getReadCount() & 0xFFFF;
/*      */         
/* 2661 */         if (read_count < 65525)
/*      */         {
/* 2663 */           read_count++;
/*      */           
/* 2665 */           dm_piece.setReadCount((short)read_count);
/*      */         }
/*      */       }
/*      */       
/* 2669 */       return true;
/*      */     }
/*      */     
/* 2672 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean validateHintRequest(PEPeerTransport originator, int pieceNumber, int offset, int length)
/*      */   {
/* 2683 */     return this.disk_mgr.checkBlockConsistencyForHint(originator.getClient() + ": " + originator.getIp(), pieceNumber, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean validatePieceReply(PEPeerTransport originator, int pieceNumber, int offset, DirectByteBuffer data)
/*      */   {
/* 2693 */     return this.disk_mgr.checkBlockConsistencyForWrite(originator.getClient() + ": " + originator.getIp(), pieceNumber, offset, data);
/*      */   }
/*      */   
/*      */   public int getAvailability(int pieceNumber)
/*      */   {
/* 2698 */     return this.piecePicker.getAvailability(pieceNumber);
/*      */   }
/*      */   
/*      */   public void havePiece(int pieceNumber, int pieceLength, PEPeer pcOrigin) {
/* 2702 */     this.piecePicker.addHavePiece(pcOrigin, pieceNumber);
/* 2703 */     this._stats.haveNewPiece(pieceLength);
/*      */     
/* 2705 */     if (this.superSeedMode) {
/* 2706 */       this.superSeedPieces[pieceNumber].peerHasPiece(pcOrigin);
/* 2707 */       if (pieceNumber == pcOrigin.getUniqueAnnounce()) {
/* 2708 */         pcOrigin.setUniqueAnnounce(-1);
/* 2709 */         this.superSeedModeNumberOfAnnounces -= 1;
/*      */       }
/*      */     }
/* 2712 */     int availability = this.piecePicker.getAvailability(pieceNumber) - 1;
/* 2713 */     if (availability < 4) {
/* 2714 */       if (this.dm_pieces[pieceNumber].isDone())
/* 2715 */         availability--;
/* 2716 */       if (availability <= 0) {
/* 2717 */         return;
/*      */       }
/*      */       
/* 2720 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 2722 */       for (int i = peer_transports.size() - 1; i >= 0; i--) {
/* 2723 */         PEPeerTransport pc = (PEPeerTransport)peer_transports.get(i);
/* 2724 */         if ((pc != pcOrigin) && (pc.getPeerState() == 30) && (pc.isPieceAvailable(pieceNumber))) {
/* 2725 */           ((PEPeerStatsImpl)pc.getStats()).statisticalSentPiece(pieceLength / availability);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int getPieceLength(int pieceNumber) {
/* 2732 */     return this.disk_mgr.getPieceLength(pieceNumber);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getNbPeers()
/*      */   {
/* 2739 */     return this._peers;
/*      */   }
/*      */   
/*      */   public int getNbSeeds()
/*      */   {
/* 2744 */     return this._seeds;
/*      */   }
/*      */   
/*      */   public int getNbRemoteTCPConnections()
/*      */   {
/* 2749 */     return this._remotesTCPNoLan;
/*      */   }
/*      */   
/*      */   public int getNbRemoteUDPConnections()
/*      */   {
/* 2754 */     return this._remotesUDPNoLan;
/*      */   }
/*      */   
/*      */   public int getNbRemoteUTPConnections() {
/* 2758 */     return this._remotesUTPNoLan;
/*      */   }
/*      */   
/*      */   public long getLastRemoteConnectionTime()
/*      */   {
/* 2763 */     return this.last_remote_time;
/*      */   }
/*      */   
/*      */   public org.gudy.azureus2.core3.peer.PEPeerManagerStats getStats() {
/* 2767 */     return this._stats;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNbPeersStalledPendingLoad()
/*      */   {
/* 2773 */     int res = 0;
/*      */     
/* 2775 */     Iterator<PEPeerTransport> it = this.peer_transports_cow.iterator();
/*      */     
/* 2777 */     while (it.hasNext())
/*      */     {
/* 2779 */       PEPeerTransport transport = (PEPeerTransport)it.next();
/*      */       
/* 2781 */       if (transport.isStalledPendingLoad())
/*      */       {
/* 2783 */         res++;
/*      */       }
/*      */     }
/*      */     
/* 2787 */     return res;
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
/*      */   public long getETA(boolean smoothed)
/*      */   {
/* 2805 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 2807 */     if ((now < this.last_eta_calculation) || (now - this.last_eta_calculation > 900L))
/*      */     {
/* 2809 */       long dataRemaining = this.disk_mgr.getRemainingExcludingDND();
/*      */       
/* 2811 */       if (dataRemaining > 0L)
/*      */       {
/* 2813 */         int writtenNotChecked = 0;
/*      */         
/* 2815 */         for (int i = 0; i < this._nbPieces; i++)
/*      */         {
/* 2817 */           if (this.dm_pieces[i].isInteresting()) {
/* 2818 */             writtenNotChecked += this.dm_pieces[i].getNbWritten() * 16384;
/*      */           }
/*      */         }
/*      */         
/* 2822 */         dataRemaining -= writtenNotChecked;
/*      */         
/* 2824 */         if (dataRemaining < 0L) {
/* 2825 */           dataRemaining = 0L;
/*      */         }
/*      */       }
/*      */       
/*      */       long smooth_result;
/*      */       long jagged_result;
/*      */       long smooth_result;
/* 2832 */       if (dataRemaining == 0L) {
/* 2833 */         long timeElapsed = (this._timeFinished - this._timeStarted) / 1000L;
/*      */         long jagged_result;
/* 2835 */         long jagged_result; if (timeElapsed > 1L) {
/* 2836 */           jagged_result = timeElapsed * -1L;
/*      */         } else {
/* 2838 */           jagged_result = 0L;
/*      */         }
/* 2840 */         smooth_result = jagged_result;
/*      */       }
/*      */       else
/*      */       {
/* 2844 */         long averageSpeed = this._averageReceptionSpeed.getAverage();
/* 2845 */         long lETA = averageSpeed == 0L ? 1827387392L : dataRemaining / averageSpeed;
/*      */         
/*      */ 
/* 2848 */         if (lETA == 0L)
/* 2849 */           lETA = 1L;
/* 2850 */         jagged_result = lETA;
/*      */         
/*      */ 
/* 2853 */         long averageSpeed = this._stats.getSmoothedDataReceiveRate();
/* 2854 */         long lETA = averageSpeed == 0L ? 1827387392L : dataRemaining / averageSpeed;
/*      */         
/*      */ 
/* 2857 */         if (lETA == 0L)
/* 2858 */           lETA = 1L;
/* 2859 */         smooth_result = lETA;
/*      */       }
/*      */       
/*      */ 
/* 2863 */       this.last_eta = jagged_result;
/* 2864 */       this.last_eta_smoothed = smooth_result;
/* 2865 */       this.last_eta_calculation = now;
/*      */     }
/*      */     
/* 2868 */     return smoothed ? this.last_eta_smoothed : this.last_eta;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isRTA()
/*      */   {
/* 2874 */     return this.piecePicker.getRTAProviders().size() > 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addToPeerTransports(PEPeerTransport peer)
/*      */   {
/* 2881 */     boolean added = false;
/*      */     
/*      */     List limiters;
/*      */     try
/*      */     {
/* 2886 */       this.peer_transports_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2891 */       if (peer.getPeerState() == 50) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 2896 */       if (this.peer_transports_cow.contains(peer)) {
/* 2897 */         Debug.out("Transport added twice"); return;
/*      */       }
/*      */       
/*      */ 
/* 2901 */       if (this.is_running)
/*      */       {
/* 2903 */         ArrayList new_peer_transports = new ArrayList(this.peer_transports_cow.size() + 1);
/*      */         
/* 2905 */         new_peer_transports.addAll(this.peer_transports_cow);
/*      */         
/* 2907 */         new_peer_transports.add(peer);
/*      */         
/* 2909 */         this.peer_transports_cow = new_peer_transports;
/*      */         
/* 2911 */         added = true;
/*      */       }
/*      */       
/* 2914 */       limiters = this.external_rate_limiters_cow;
/*      */     }
/*      */     finally {
/* 2917 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/* 2920 */     if (added) {
/* 2921 */       boolean incoming = peer.isIncoming();
/*      */       
/* 2923 */       this._stats.haveNewConnection(incoming);
/*      */       
/* 2925 */       if (incoming) {
/* 2926 */         long connect_time = SystemTime.getCurrentTime();
/*      */         
/* 2928 */         if (connect_time > this.last_remote_time)
/*      */         {
/* 2930 */           this.last_remote_time = connect_time;
/*      */         }
/*      */       }
/*      */       
/* 2934 */       if (limiters != null)
/*      */       {
/* 2936 */         for (int i = 0; i < limiters.size(); i++)
/*      */         {
/* 2938 */           Object[] entry = (Object[])limiters.get(i);
/*      */           
/* 2940 */           peer.addRateLimiter((LimitedRateGroup)entry[0], ((Boolean)entry[1]).booleanValue());
/*      */         }
/*      */       }
/*      */       
/* 2944 */       peerAdded(peer);
/*      */     }
/*      */     else {
/* 2947 */       peer.closeConnection("PeerTransport added when manager not running");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(LimitedRateGroup group, boolean upload)
/*      */   {
/*      */     List<PEPeerTransport> transports;
/*      */     
/*      */     try
/*      */     {
/* 2959 */       this.peer_transports_mon.enter();
/*      */       
/* 2961 */       ArrayList<Object[]> new_limiters = new ArrayList(this.external_rate_limiters_cow == null ? 1 : this.external_rate_limiters_cow.size() + 1);
/*      */       
/* 2963 */       if (this.external_rate_limiters_cow != null)
/*      */       {
/* 2965 */         new_limiters.addAll(this.external_rate_limiters_cow);
/*      */       }
/*      */       
/* 2968 */       new_limiters.add(new Object[] { group, Boolean.valueOf(upload) });
/*      */       
/* 2970 */       this.external_rate_limiters_cow = new_limiters;
/*      */       
/* 2972 */       transports = this.peer_transports_cow;
/*      */     }
/*      */     finally
/*      */     {
/* 2976 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/* 2979 */     for (int i = 0; i < transports.size(); i++)
/*      */     {
/* 2981 */       ((PEPeerTransport)transports.get(i)).addRateLimiter(group, upload);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(LimitedRateGroup group, boolean upload)
/*      */   {
/*      */     List<PEPeerTransport> transports;
/*      */     
/*      */     try
/*      */     {
/* 2993 */       this.peer_transports_mon.enter();
/*      */       
/* 2995 */       if (this.external_rate_limiters_cow != null)
/*      */       {
/* 2997 */         ArrayList new_limiters = new ArrayList(this.external_rate_limiters_cow.size() - 1);
/*      */         
/* 2999 */         for (int i = 0; i < this.external_rate_limiters_cow.size(); i++)
/*      */         {
/* 3001 */           Object[] entry = (Object[])this.external_rate_limiters_cow.get(i);
/*      */           
/* 3003 */           if (entry[0] != group)
/*      */           {
/* 3005 */             new_limiters.add(entry);
/*      */           }
/*      */         }
/*      */         
/* 3009 */         if (new_limiters.size() == 0)
/*      */         {
/* 3011 */           this.external_rate_limiters_cow = null;
/*      */         }
/*      */         else
/*      */         {
/* 3015 */           this.external_rate_limiters_cow = new_limiters;
/*      */         }
/*      */       }
/*      */       
/* 3019 */       transports = this.peer_transports_cow;
/*      */     }
/*      */     finally
/*      */     {
/* 3023 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/* 3026 */     for (int i = 0; i < transports.size(); i++)
/*      */     {
/* 3028 */       ((PEPeerTransport)transports.get(i)).removeRateLimiter(group, upload);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUploadRateLimitBytesPerSecond()
/*      */   {
/* 3035 */     return this.adapter.getUploadRateLimitBytesPerSecond();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDownloadRateLimitBytesPerSecond()
/*      */   {
/* 3041 */     return this.adapter.getDownloadRateLimitBytesPerSecond();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerConnectionClosed(PEPeerTransport peer, boolean connect_failed, boolean network_failed)
/*      */   {
/* 3052 */     boolean connection_found = false;
/*      */     
/* 3054 */     boolean tcpReconnect = false;
/* 3055 */     boolean ipv6reconnect = false;
/*      */     try
/*      */     {
/* 3058 */       this.peer_transports_mon.enter();
/*      */       
/* 3060 */       int udpPort = peer.getUDPListenPort();
/*      */       
/* 3062 */       boolean canTryUDP = (UDPNetworkManager.UDP_OUTGOING_ENABLED) && (peer.getUDPListenPort() > 0);
/* 3063 */       boolean canTryIpv6 = (NetworkAdmin.getSingleton().hasIPV6Potential(true)) && (peer.getAlternativeIPv6() != null);
/*      */       
/* 3065 */       if (this.is_running)
/*      */       {
/* 3067 */         PeerItem peer_item = peer.getPeerItemIdentity();
/* 3068 */         PeerItem self_item = this.peer_database.getSelfPeer();
/*      */         
/*      */ 
/* 3071 */         if ((self_item == null) || (!self_item.equals(peer_item)))
/*      */         {
/* 3073 */           String ip = peer.getIp();
/*      */           boolean wasIPv6;
/* 3075 */           if (peer.getNetwork() == "Public")
/*      */           {
/*      */             try
/*      */             {
/* 3079 */               wasIPv6 = AddressUtils.getByName(ip) instanceof java.net.Inet6Address;
/*      */             }
/*      */             catch (UnknownHostException e) {
/* 3082 */               boolean wasIPv6 = false;
/*      */               
/* 3084 */               canTryIpv6 = false;
/*      */             }
/*      */           } else {
/* 3087 */             wasIPv6 = false;
/* 3088 */             canTryIpv6 = false;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 3093 */           String key = ip + ":" + udpPort;
/*      */           
/* 3095 */           if (peer.isTCP())
/*      */           {
/* 3097 */             String net = AENetworkClassifier.categoriseAddress(ip);
/*      */             
/* 3099 */             if (connect_failed)
/*      */             {
/*      */ 
/*      */ 
/* 3103 */               if ((canTryUDP) && (udp_fallback_for_failed_connection))
/*      */               {
/* 3105 */                 this.pending_nat_traversals.put(key, peer);
/* 3106 */               } else if ((canTryIpv6) && (!wasIPv6))
/*      */               {
/* 3108 */                 tcpReconnect = true;
/* 3109 */                 ipv6reconnect = true;
/*      */               }
/* 3111 */             } else if ((canTryUDP) && (udp_fallback_for_dropped_connection) && (network_failed) && (this.seeding_mode) && (peer.isInterested()) && (!peer.isSeed()) && (!peer.isRelativeSeed()) && (peer.getStats().getEstimatedSecondsToCompletion() > 60L) && (FeatureAvailability.isUDPPeerReconnectEnabled()))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3120 */               if (Logger.isEnabled()) {
/* 3121 */                 Logger.log(new LogEvent(peer, LOGID, 1, "Unexpected stream closure detected, attempting recovery"));
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 3126 */               this.udp_reconnects.put(key, peer);
/*      */             }
/* 3128 */             else if ((network_failed) && (peer.isSafeForReconnect()) && ((!this.seeding_mode) || ((!peer.isSeed()) && (!peer.isRelativeSeed()) && (peer.getStats().getEstimatedSecondsToCompletion() >= 60L))) && (getMaxConnections(net) > 0) && ((getMaxNewConnectionsAllowed(net) < 0) || (getMaxNewConnectionsAllowed(net) > getMaxConnections(net) / 3)) && (FeatureAvailability.isGeneralPeerReconnectEnabled()))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3135 */               tcpReconnect = true;
/*      */             }
/* 3137 */           } else if (connect_failed)
/*      */           {
/*      */ 
/*      */ 
/* 3141 */             if (udp_fallback_for_failed_connection)
/*      */             {
/* 3143 */               if (peer.getData(PEER_NAT_TRAVERSE_DONE_KEY) == null)
/*      */               {
/*      */ 
/*      */ 
/* 3147 */                 this.pending_nat_traversals.put(key, peer);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 3154 */       if (this.peer_transports_cow.contains(peer)) {
/* 3155 */         ArrayList new_peer_transports = new ArrayList(this.peer_transports_cow);
/* 3156 */         new_peer_transports.remove(peer);
/* 3157 */         this.peer_transports_cow = new_peer_transports;
/* 3158 */         connection_found = true;
/*      */       }
/*      */     }
/*      */     finally {
/* 3162 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/* 3165 */     if (connection_found) {
/* 3166 */       if (peer.getPeerState() != 50) {
/* 3167 */         System.out.println("peer.getPeerState() != PEPeer.DISCONNECTED: " + peer.getPeerState());
/*      */       }
/*      */       
/* 3170 */       peerRemoved(peer);
/*      */     }
/*      */     
/* 3173 */     if (tcpReconnect) {
/* 3174 */       peer.reconnect(false, ipv6reconnect);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerAdded(PEPeer pc)
/*      */   {
/* 3184 */     this.adapter.addPeer(pc);
/*      */     
/*      */ 
/* 3187 */     ArrayList peer_manager_listeners = this.peer_manager_listeners_cow;
/*      */     
/* 3189 */     for (int i = 0; i < peer_manager_listeners.size(); i++) {
/* 3190 */       ((PEPeerManagerListener)peer_manager_listeners.get(i)).peerAdded(this, pc);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerRemoved(PEPeer pc)
/*      */   {
/* 3199 */     if ((this.is_running) && (!this.seeding_mode) && ((this.prefer_udp) || (prefer_udp_default)))
/*      */     {
/*      */ 
/*      */ 
/* 3203 */       int udp = pc.getUDPListenPort();
/*      */       
/* 3205 */       if ((udp != 0) && (udp == pc.getTCPListenPort()))
/*      */       {
/* 3207 */         BloomFilter filter = this.prefer_udp_bloom;
/*      */         
/* 3209 */         if (filter == null)
/*      */         {
/* 3211 */           filter = this.prefer_udp_bloom = BloomFilterFactory.createAddOnly(10000);
/*      */         }
/*      */         
/* 3214 */         if (filter.getEntryCount() < 1000)
/*      */         {
/* 3216 */           filter.add(pc.getIp().getBytes());
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3221 */     int piece = pc.getUniqueAnnounce();
/* 3222 */     if ((piece != -1) && (this.superSeedMode)) {
/* 3223 */       this.superSeedModeNumberOfAnnounces -= 1;
/* 3224 */       this.superSeedPieces[piece].peerLeft();
/*      */     }
/*      */     
/* 3227 */     int[] reserved_pieces = pc.getReservedPieceNumbers();
/*      */     
/* 3229 */     if (reserved_pieces != null)
/*      */     {
/* 3231 */       for (int reserved_piece : reserved_pieces)
/*      */       {
/* 3233 */         PEPiece pe_piece = this.pePieces[reserved_piece];
/*      */         
/* 3235 */         if (pe_piece != null)
/*      */         {
/* 3237 */           String reserved_by = pe_piece.getReservedBy();
/*      */           
/* 3239 */           if ((reserved_by != null) && (reserved_by.equals(pc.getIp())))
/*      */           {
/* 3241 */             pe_piece.setReservedBy(null);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3247 */     if (pc.isSeed())
/*      */     {
/* 3249 */       this.last_seed_disconnect_time = SystemTime.getCurrentTime();
/*      */     }
/*      */     
/* 3252 */     this.adapter.removePeer(pc);
/*      */     
/*      */ 
/* 3255 */     ArrayList peer_manager_listeners = this.peer_manager_listeners_cow;
/*      */     
/* 3257 */     for (int i = 0; i < peer_manager_listeners.size(); i++) {
/* 3258 */       ((PEPeerManagerListener)peer_manager_listeners.get(i)).peerRemoved(this, pc);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addPiece(PEPiece piece, int pieceNumber, PEPeer for_peer)
/*      */   {
/* 3268 */     addPiece(piece, pieceNumber, false, for_peer);
/*      */   }
/*      */   
/*      */   protected void addPiece(PEPiece piece, int pieceNumber, boolean force_add, PEPeer for_peer)
/*      */   {
/* 3273 */     if ((piece == null) || (this.pePieces[pieceNumber] != null)) {
/* 3274 */       Debug.out("piece state inconsistent");
/*      */     }
/* 3276 */     this.pePieces[pieceNumber] = ((PEPieceImpl)piece);
/* 3277 */     this.nbPiecesActive += 1;
/* 3278 */     if ((this.is_running) || (force_add))
/*      */     {
/* 3280 */       this.adapter.addPiece(piece);
/*      */     }
/*      */     
/* 3283 */     ArrayList peer_manager_listeners = this.peer_manager_listeners_cow;
/*      */     
/* 3285 */     for (int i = 0; i < peer_manager_listeners.size(); i++) {
/*      */       try {
/* 3287 */         ((PEPeerManagerListener)peer_manager_listeners.get(i)).pieceAdded(this, piece, for_peer);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3291 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removePiece(PEPiece pePiece, int pieceNumber)
/*      */   {
/* 3302 */     if (pePiece != null) {
/* 3303 */       this.adapter.removePiece(pePiece);
/*      */     } else {
/* 3305 */       pePiece = this.pePieces[pieceNumber];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3310 */     if (this.pePieces[pieceNumber] != null) {
/* 3311 */       this.pePieces[pieceNumber] = null;
/* 3312 */       this.nbPiecesActive -= 1;
/*      */     }
/*      */     
/* 3315 */     if (pePiece == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 3320 */       return;
/*      */     }
/*      */     
/* 3323 */     ArrayList peer_manager_listeners = this.peer_manager_listeners_cow;
/*      */     
/* 3325 */     for (int i = 0; i < peer_manager_listeners.size(); i++) {
/*      */       try {
/* 3327 */         ((PEPeerManagerListener)peer_manager_listeners.get(i)).pieceRemoved(this, pePiece);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 3331 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public int getNbActivePieces()
/*      */   {
/* 3338 */     return this.nbPiecesActive;
/*      */   }
/*      */   
/*      */   public String getElapsedTime() {
/* 3342 */     return org.gudy.azureus2.core3.util.TimeFormatter.format((SystemTime.getCurrentTime() - this._timeStarted) / 1000L);
/*      */   }
/*      */   
/*      */   public long getTimeStarted(boolean mono)
/*      */   {
/* 3347 */     return mono ? this._timeStarted_mono : this._timeStarted;
/*      */   }
/*      */   
/*      */   public long getTimeStartedSeeding(boolean mono) {
/* 3351 */     return mono ? this._timeStartedSeeding_mono : this._timeStartedSeeding;
/*      */   }
/*      */   
/*      */   private byte[] computeMd5Hash(DirectByteBuffer buffer)
/*      */   {
/* 3356 */     BrokenMd5Hasher md5 = new BrokenMd5Hasher();
/*      */     
/* 3358 */     md5.reset();
/* 3359 */     int position = buffer.position((byte)8);
/* 3360 */     md5.update(buffer.getBuffer((byte)8));
/* 3361 */     buffer.position((byte)8, position);
/* 3362 */     ByteBuffer md5Result = ByteBuffer.allocate(16);
/* 3363 */     md5Result.position(0);
/* 3364 */     md5.finalDigest(md5Result);
/*      */     
/* 3366 */     byte[] result = new byte[16];
/* 3367 */     md5Result.position(0);
/* 3368 */     for (int i = 0; i < result.length; i++)
/*      */     {
/* 3370 */       result[i] = md5Result.get();
/*      */     }
/*      */     
/* 3373 */     return result;
/*      */   }
/*      */   
/*      */   private void MD5CheckPiece(PEPiece piece, boolean correct)
/*      */   {
/* 3378 */     String[] writers = piece.getWriters();
/* 3379 */     int offset = 0;
/* 3380 */     for (int i = 0; i < writers.length; i++)
/*      */     {
/* 3382 */       int length = piece.getBlockSize(i);
/* 3383 */       String peer = writers[i];
/* 3384 */       if (peer != null)
/*      */       {
/* 3386 */         DirectByteBuffer buffer = this.disk_mgr.readBlock(piece.getPieceNumber(), offset, length);
/*      */         
/* 3388 */         if (buffer != null)
/*      */         {
/* 3390 */           byte[] hash = computeMd5Hash(buffer);
/* 3391 */           buffer.returnToPool();
/* 3392 */           buffer = null;
/* 3393 */           piece.addWrite(i, peer, hash, correct);
/*      */         }
/*      */       }
/* 3396 */       offset += length;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void checkCompleted(DiskManagerCheckRequest request, boolean passed)
/*      */   {
/*      */     try
/*      */     {
/* 3409 */       this.piece_check_result_list_mon.enter();
/*      */       
/* 3411 */       this.piece_check_result_list.add(new Object[] { request, new Integer(passed ? 1 : 0) });
/*      */     }
/*      */     finally {
/* 3414 */       this.piece_check_result_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void checkCancelled(DiskManagerCheckRequest request)
/*      */   {
/*      */     try
/*      */     {
/* 3422 */       this.piece_check_result_list_mon.enter();
/*      */       
/* 3424 */       this.piece_check_result_list.add(new Object[] { request, new Integer(2) });
/*      */     }
/*      */     finally
/*      */     {
/* 3428 */       this.piece_check_result_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void checkFailed(DiskManagerCheckRequest request, Throwable cause)
/*      */   {
/*      */     try
/*      */     {
/* 3436 */       this.piece_check_result_list_mon.enter();
/*      */       
/* 3438 */       this.piece_check_result_list.add(new Object[] { request, new Integer(0) });
/*      */     }
/*      */     finally
/*      */     {
/* 3442 */       this.piece_check_result_list_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean needsMD5CheckOnCompletion(int pieceNumber)
/*      */   {
/* 3448 */     PEPieceImpl piece = this.pePieces[pieceNumber];
/* 3449 */     if (piece == null)
/* 3450 */       return false;
/* 3451 */     return piece.getPieceWrites().size() > 0;
/*      */   }
/*      */   
/*      */   private void processPieceCheckResult(DiskManagerCheckRequest request, int outcome)
/*      */   {
/* 3456 */     int check_type = ((Integer)request.getUserData()).intValue();
/*      */     
/*      */     try
/*      */     {
/* 3460 */       int pieceNumber = request.getPieceNumber();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3467 */       if (check_type == 2)
/*      */       {
/*      */ 
/* 3470 */         if (outcome == 0)
/*      */         {
/*      */ 
/* 3473 */           Debug.out(getDisplayName() + ": Piece #" + pieceNumber + " failed final re-check. Re-downloading...");
/*      */           
/* 3475 */           if (!this.restart_initiated)
/*      */           {
/* 3477 */             this.restart_initiated = true;
/* 3478 */             this.adapter.restartDownload(true);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/* 3484 */       else if ((check_type == 4) || (check_type == 5))
/*      */       {
/* 3486 */         if (outcome == 0)
/*      */         {
/* 3488 */           if (check_type == 4)
/*      */           {
/* 3490 */             Debug.out(getDisplayName() + "Piece #" + pieceNumber + " failed recheck while seeding. Re-downloading...");
/*      */           }
/*      */           else
/*      */           {
/* 3494 */             Debug.out(getDisplayName() + "Piece #" + pieceNumber + " failed recheck after being reported as bad. Re-downloading...");
/*      */           }
/*      */           
/* 3497 */           Logger.log(new org.gudy.azureus2.core3.logging.LogAlert(this, true, 3, "Download '" + getDisplayName() + "': piece " + pieceNumber + " has been corrupted, re-downloading"));
/*      */           
/*      */ 
/*      */ 
/* 3501 */           if (!this.restart_initiated)
/*      */           {
/* 3503 */             this.restart_initiated = true;
/*      */             
/* 3505 */             this.adapter.restartDownload(true);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 3515 */         PEPieceImpl pePiece = this.pePieces[pieceNumber];
/*      */         
/* 3517 */         if ((outcome == 1) || (this.is_metadata_download))
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/* 3522 */             if (pePiece != null)
/*      */             {
/* 3524 */               if (needsMD5CheckOnCompletion(pieceNumber)) {
/* 3525 */                 MD5CheckPiece(pePiece, true);
/*      */               }
/*      */               
/* 3528 */               List list = pePiece.getPieceWrites();
/*      */               
/* 3530 */               if (list.size() > 0)
/*      */               {
/*      */ 
/* 3533 */                 for (int i = 0; i < pePiece.getNbBlocks(); i++)
/*      */                 {
/*      */ 
/*      */ 
/* 3537 */                   List listPerBlock = pePiece.getPieceWrites(i);
/* 3538 */                   byte[] correctHash = null;
/*      */                   
/* 3540 */                   Iterator iterPerBlock = listPerBlock.iterator();
/* 3541 */                   while (iterPerBlock.hasNext())
/*      */                   {
/* 3543 */                     PEPieceWriteImpl write = (PEPieceWriteImpl)iterPerBlock.next();
/* 3544 */                     if (write.isCorrect())
/*      */                     {
/* 3546 */                       correctHash = write.getHash();
/*      */                     }
/*      */                   }
/*      */                   
/*      */ 
/*      */ 
/* 3552 */                   if (correctHash != null)
/*      */                   {
/* 3554 */                     iterPerBlock = listPerBlock.iterator();
/* 3555 */                     while (iterPerBlock.hasNext())
/*      */                     {
/* 3557 */                       PEPieceWriteImpl write = (PEPieceWriteImpl)iterPerBlock.next();
/* 3558 */                       if (!Arrays.equals(write.getHash(), correctHash))
/*      */                       {
/*      */ 
/* 3561 */                         badPeerDetected(write.getSender(), pieceNumber);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 3571 */             removePiece(pePiece, pieceNumber);
/*      */             
/*      */ 
/* 3574 */             sendHave(pieceNumber);
/*      */           }
/* 3576 */         } else if (outcome == 0)
/*      */         {
/*      */ 
/*      */ 
/* 3580 */           Iterator<PEPeerManagerListener> it = this.peer_manager_listeners_cow.iterator();
/*      */           
/* 3582 */           while (it.hasNext()) {
/*      */             try
/*      */             {
/* 3585 */               ((PEPeerManagerListener)it.next()).pieceCorrupted(this, pieceNumber);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3589 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */           
/* 3593 */           if (pePiece != null) {
/*      */             try
/*      */             {
/* 3596 */               MD5CheckPiece(pePiece, false);
/*      */               
/* 3598 */               String[] writers = pePiece.getWriters();
/* 3599 */               List uniqueWriters = new ArrayList();
/* 3600 */               int[] writesPerWriter = new int[writers.length];
/* 3601 */               for (int i = 0; i < writers.length; i++)
/*      */               {
/* 3603 */                 String writer = writers[i];
/* 3604 */                 if (writer != null)
/*      */                 {
/* 3606 */                   int writerId = uniqueWriters.indexOf(writer);
/* 3607 */                   if (writerId == -1)
/*      */                   {
/* 3609 */                     uniqueWriters.add(writer);
/* 3610 */                     writerId = uniqueWriters.size() - 1;
/*      */                   }
/* 3612 */                   writesPerWriter[writerId] += 1;
/*      */                 }
/*      */               }
/* 3615 */               int nbWriters = uniqueWriters.size();
/* 3616 */               if (nbWriters == 1)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 3621 */                 String bad_ip = (String)uniqueWriters.get(0);
/*      */                 
/* 3623 */                 PEPeerTransport bad_peer = getTransportFromAddress(bad_ip);
/*      */                 
/* 3625 */                 if (bad_peer != null)
/*      */                 {
/* 3627 */                   bad_peer.sendBadPiece(pieceNumber);
/*      */                 }
/*      */                 
/* 3630 */                 badPeerDetected(bad_ip, pieceNumber);
/*      */                 
/*      */ 
/* 3633 */                 pePiece.reset();
/*      */               }
/* 3635 */               else if (nbWriters > 1)
/*      */               {
/* 3637 */                 int maxWrites = 0;
/* 3638 */                 String bestWriter = null;
/*      */                 
/* 3640 */                 PEPeerTransport bestWriter_transport = null;
/*      */                 
/* 3642 */                 for (int i = 0; i < uniqueWriters.size(); i++)
/*      */                 {
/* 3644 */                   int writes = writesPerWriter[i];
/*      */                   
/* 3646 */                   if (writes > maxWrites)
/*      */                   {
/* 3648 */                     String writer = (String)uniqueWriters.get(i);
/*      */                     
/* 3650 */                     PEPeerTransport pt = getTransportFromAddress(writer);
/*      */                     
/* 3652 */                     if ((pt != null) && (pt.getReservedPieceNumbers() == null) && (!ip_filter.isInRange(writer, getDisplayName(), getTorrentHash())))
/*      */                     {
/*      */ 
/*      */ 
/* 3656 */                       bestWriter = writer;
/* 3657 */                       maxWrites = writes;
/*      */                       
/* 3659 */                       bestWriter_transport = pt;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/* 3664 */                 if (bestWriter != null)
/*      */                 {
/* 3666 */                   pePiece.setReservedBy(bestWriter);
/*      */                   
/* 3668 */                   bestWriter_transport.addReservedPieceNumber(pePiece.getPieceNumber());
/*      */                   
/* 3670 */                   pePiece.setRequestable();
/*      */                   
/* 3672 */                   for (int i = 0; i < pePiece.getNbBlocks(); i++)
/*      */                   {
/*      */ 
/*      */ 
/* 3676 */                     if ((writers[i] == null) || (!writers[i].equals(bestWriter)))
/*      */                     {
/* 3678 */                       pePiece.reDownloadBlock(i);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/* 3684 */                   pePiece.reset();
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/* 3689 */                 pePiece.reset();
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 3695 */               this.piecePicker.addEndGameChunks(pePiece);
/* 3696 */               this._stats.hashFailed(pePiece.getLength());
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 3700 */               Debug.printStackTrace(e);
/*      */               
/*      */ 
/*      */ 
/* 3704 */               pePiece.reset();
/*      */ 
/*      */ 
/*      */             }
/*      */             
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 3714 */             this.dm_pieces[pieceNumber].reset();
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 3722 */       if (check_type == 3) {
/* 3723 */         this.rescan_piece_time = SystemTime.getCurrentTime();
/*      */       }
/*      */       
/* 3726 */       if (!this.seeding_mode) {
/* 3727 */         checkFinished(false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void badPeerDetected(String ip, int piece_number)
/*      */   {
/* 3735 */     boolean hash_fail = piece_number >= 0;
/*      */     
/*      */ 
/*      */ 
/* 3739 */     PEPeerTransport peer = getTransportFromAddress(ip);
/*      */     
/* 3741 */     if ((hash_fail) && (peer != null))
/*      */     {
/* 3743 */       Iterator<PEPeerManagerListener> it = this.peer_manager_listeners_cow.iterator();
/*      */       
/* 3745 */       while (it.hasNext()) {
/*      */         try
/*      */         {
/* 3748 */           ((PEPeerManagerListener)it.next()).peerSentBadData(this, peer, piece_number);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 3752 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3758 */     IpFilterManager filter_manager = IpFilterManagerFactory.getSingleton();
/*      */     
/*      */ 
/*      */ 
/* 3762 */     int nbWarnings = filter_manager.getBadIps().addWarningForIp(ip);
/*      */     
/* 3764 */     boolean disconnect_peer = false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3771 */     if (nbWarnings > 2)
/*      */     {
/* 3773 */       if (COConfigurationManager.getBooleanParameter("Ip Filter Enable Banning"))
/*      */       {
/*      */ 
/*      */ 
/* 3777 */         if (ip_filter.ban(ip, getDisplayName(), false))
/*      */         {
/* 3779 */           checkForBannedConnections();
/*      */         }
/*      */         
/*      */ 
/* 3783 */         if (Logger.isEnabled()) {
/* 3784 */           Logger.log(new LogEvent(peer, LOGID, 3, ip + " : has been banned and won't be able " + "to connect until you restart azureus"));
/*      */         }
/*      */         
/*      */ 
/* 3788 */         disconnect_peer = true;
/*      */       }
/* 3790 */     } else if (!hash_fail)
/*      */     {
/*      */ 
/*      */ 
/* 3794 */       disconnect_peer = true;
/*      */     }
/*      */     
/*      */ 
/* 3798 */     if (disconnect_peer)
/*      */     {
/* 3800 */       if (peer != null)
/*      */       {
/* 3802 */         int ps = peer.getPeerState();
/*      */         
/*      */ 
/*      */ 
/* 3806 */         if ((ps != 40) && (ps != 50))
/*      */         {
/*      */ 
/* 3809 */           closeAndRemovePeer(peer, "has sent too many " + (hash_fail ? "bad pieces" : "discarded blocks") + ", " + 2 + " max.", true);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public PEPiece[] getPieces()
/*      */   {
/* 3817 */     return this.pePieces;
/*      */   }
/*      */   
/*      */   public PEPiece getPiece(int pieceNumber)
/*      */   {
/* 3822 */     return this.pePieces[pieceNumber];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public PEPeerStats createPeerStats(PEPeer owner)
/*      */   {
/* 3829 */     return new PEPeerStatsImpl(owner);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DiskManagerReadRequest createDiskManagerRequest(int pieceNumber, int offset, int length)
/*      */   {
/* 3839 */     return this.disk_mgr.createReadRequest(pieceNumber, offset, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean requestExists(String peer_ip, int piece_number, int offset, int length)
/*      */   {
/* 3849 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */     
/* 3851 */     DiskManagerReadRequest request = null;
/*      */     
/* 3853 */     for (int i = 0; i < peer_transports.size(); i++)
/*      */     {
/* 3855 */       PEPeerTransport conn = (PEPeerTransport)peer_transports.get(i);
/*      */       
/* 3857 */       if (conn.getIp().equals(peer_ip))
/*      */       {
/* 3859 */         if (request == null)
/*      */         {
/* 3861 */           request = createDiskManagerRequest(piece_number, offset, length);
/*      */         }
/*      */         
/* 3864 */         if (conn.getRequestIndex(request) != -1)
/*      */         {
/* 3866 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3871 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean seedPieceRecheck()
/*      */   {
/* 3877 */     if ((!enable_seeding_piece_rechecks) && (!isSeeding()))
/*      */     {
/* 3879 */       return false;
/*      */     }
/*      */     
/* 3882 */     int max_reads = 0;
/* 3883 */     int max_reads_index = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3892 */     for (int i = 0; i < this.dm_pieces.length; i++)
/*      */     {
/*      */ 
/*      */ 
/* 3896 */       DiskManagerPiece dm_piece = this.dm_pieces[i];
/*      */       
/* 3898 */       if (dm_piece.isDone())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 3903 */         int num = dm_piece.getReadCount() & 0xFFFF;
/*      */         
/* 3905 */         if (num > 65526)
/*      */         {
/*      */ 
/*      */ 
/* 3909 */           num--;
/*      */           
/* 3911 */           if (num == 65526)
/*      */           {
/* 3913 */             num = 0;
/*      */           }
/*      */           
/* 3916 */           dm_piece.setReadCount((short)num);
/*      */ 
/*      */ 
/*      */         }
/* 3920 */         else if (num > max_reads)
/*      */         {
/* 3922 */           max_reads = num;
/* 3923 */           max_reads_index = i;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3929 */     if (max_reads > 0)
/*      */     {
/* 3931 */       DiskManagerPiece dm_piece = this.dm_pieces[max_reads_index];
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 3936 */       if (max_reads >= dm_piece.getNbBlocks() * 3)
/*      */       {
/* 3938 */         DiskManagerCheckRequest req = this.disk_mgr.createCheckRequest(max_reads_index, new Integer(4));
/*      */         
/* 3940 */         req.setAdHoc(true);
/*      */         
/* 3942 */         req.setLowPriority(true);
/*      */         
/* 3944 */         if (Logger.isEnabled()) {
/* 3945 */           Logger.log(new LogEvent(this.disk_mgr.getTorrent(), LOGID, "Rechecking piece " + max_reads_index + " while seeding as most active"));
/*      */         }
/*      */         
/* 3948 */         this.disk_mgr.enqueueCheckRequest(req, this);
/*      */         
/* 3950 */         dm_piece.setReadCount((short)-1);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 3955 */         for (int i = 0; i < this.dm_pieces.length; i++)
/*      */         {
/* 3957 */           if (i != max_reads_index)
/*      */           {
/* 3959 */             int num = this.dm_pieces[i].getReadCount() & 0xFFFF;
/*      */             
/* 3961 */             if (num < 65526)
/*      */             {
/* 3963 */               this.dm_pieces[i].setReadCount((short)0);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 3968 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 3972 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(PEPeerManagerListener l)
/*      */   {
/*      */     try
/*      */     {
/* 3980 */       this.this_mon.enter();
/*      */       
/*      */ 
/* 3983 */       ArrayList peer_manager_listeners = new ArrayList(this.peer_manager_listeners_cow.size() + 1);
/* 3984 */       peer_manager_listeners.addAll(this.peer_manager_listeners_cow);
/* 3985 */       peer_manager_listeners.add(l);
/* 3986 */       this.peer_manager_listeners_cow = peer_manager_listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 3990 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(PEPeerManagerListener l)
/*      */   {
/*      */     try
/*      */     {
/* 3999 */       this.this_mon.enter();
/*      */       
/*      */ 
/* 4002 */       ArrayList peer_manager_listeners = new ArrayList(this.peer_manager_listeners_cow);
/* 4003 */       peer_manager_listeners.remove(l);
/* 4004 */       this.peer_manager_listeners_cow = peer_manager_listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 4008 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkForBannedConnections()
/*      */   {
/* 4015 */     if (ip_filter.isEnabled()) {
/* 4016 */       List<PEPeerTransport> to_close = null;
/*      */       
/* 4018 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 4020 */       String name = getDisplayName();
/* 4021 */       byte[] hash = getTorrentHash();
/*      */       
/* 4023 */       for (int i = 0; i < peer_transports.size(); i++) {
/* 4024 */         PEPeerTransport conn = (PEPeerTransport)peer_transports.get(i);
/*      */         
/* 4026 */         if (ip_filter.isInRange(conn.getIp(), name, hash)) {
/* 4027 */           if (to_close == null) to_close = new ArrayList();
/* 4028 */           to_close.add(conn);
/*      */         }
/*      */       }
/*      */       
/* 4032 */       if (to_close != null) {
/* 4033 */         for (int i = 0; i < to_close.size(); i++) {
/* 4034 */           closeAndRemovePeer((PEPeerTransport)to_close.get(i), "IPFilter banned IP address", true);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isSeeding()
/*      */   {
/* 4043 */     return this.seeding_mode;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isMetadataDownload()
/*      */   {
/* 4049 */     return this.is_metadata_download;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTorrentInfoDictSize()
/*      */   {
/* 4055 */     return this.metadata_infodict_size;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTorrentInfoDictSize(int size)
/*      */   {
/* 4062 */     this.metadata_infodict_size = size;
/*      */   }
/*      */   
/*      */   public boolean isInEndGameMode() {
/* 4066 */     return this.piecePicker.isInEndGameMode();
/*      */   }
/*      */   
/*      */   public boolean isSuperSeedMode() {
/* 4070 */     return this.superSeedMode;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean canToggleSuperSeedMode()
/*      */   {
/* 4076 */     if (this.superSeedMode)
/*      */     {
/* 4078 */       return true;
/*      */     }
/*      */     
/* 4081 */     return (this.superSeedPieces == null) && (getRemaining() == 0L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSuperSeedMode(boolean _superSeedMode)
/*      */   {
/* 4088 */     if (_superSeedMode == this.superSeedMode)
/*      */     {
/* 4090 */       return;
/*      */     }
/*      */     
/* 4093 */     boolean kick_peers = false;
/*      */     
/* 4095 */     if (_superSeedMode)
/*      */     {
/* 4097 */       if ((this.superSeedPieces == null) && (getRemaining() == 0L))
/*      */       {
/* 4099 */         this.superSeedMode = true;
/*      */         
/* 4101 */         initialiseSuperSeedMode();
/*      */         
/* 4103 */         kick_peers = true;
/*      */       }
/*      */     }
/*      */     else {
/* 4107 */       this.superSeedMode = false;
/*      */       
/* 4109 */       kick_peers = true;
/*      */     }
/*      */     
/* 4112 */     if (kick_peers)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 4117 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 4119 */       for (int i = 0; i < peer_transports.size(); i++)
/*      */       {
/* 4121 */         PEPeerTransport conn = (PEPeerTransport)peer_transports.get(i);
/*      */         
/* 4123 */         closeAndRemovePeer(conn, "Turning on super-seeding", false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void initialiseSuperSeedMode()
/*      */   {
/* 4131 */     this.superSeedPieces = new SuperSeedPiece[this._nbPieces];
/* 4132 */     for (int i = 0; i < this._nbPieces; i++) {
/* 4133 */       this.superSeedPieces[i] = new SuperSeedPiece(this, i);
/*      */     }
/*      */   }
/*      */   
/*      */   private void updatePeersInSuperSeedMode() {
/* 4138 */     if (!this.superSeedMode) {
/* 4139 */       return;
/*      */     }
/*      */     
/*      */ 
/* 4143 */     for (int i = 0; i < this.superSeedPieces.length; i++) {
/* 4144 */       this.superSeedPieces[i].updateTime();
/*      */     }
/*      */     
/*      */ 
/* 4148 */     int nbUnchoke = this.adapter.getMaxUploads();
/* 4149 */     if (this.superSeedModeNumberOfAnnounces >= 2 * nbUnchoke) {
/* 4150 */       return;
/*      */     }
/*      */     
/*      */ 
/* 4154 */     PEPeerTransport selectedPeer = null;
/* 4155 */     List<SuperSeedPeer> sortedPeers = null;
/*      */     
/* 4157 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */     
/* 4159 */     sortedPeers = new ArrayList(peer_transports.size());
/* 4160 */     Iterator<PEPeerTransport> iter1 = peer_transports.iterator();
/* 4161 */     while (iter1.hasNext()) {
/* 4162 */       sortedPeers.add(new SuperSeedPeer((PEPeerTransport)iter1.next()));
/*      */     }
/*      */     
/* 4165 */     Collections.sort(sortedPeers);
/* 4166 */     Iterator<SuperSeedPeer> iter2 = sortedPeers.iterator();
/* 4167 */     while (iter2.hasNext()) {
/* 4168 */       PEPeerTransport peer = ((SuperSeedPeer)iter2.next()).peer;
/* 4169 */       if ((peer.getUniqueAnnounce() == -1) && (peer.getPeerState() == 30)) {
/* 4170 */         selectedPeer = peer;
/* 4171 */         break;
/*      */       }
/*      */     }
/*      */     
/* 4175 */     if ((selectedPeer == null) || (selectedPeer.getPeerState() >= 40)) {
/* 4176 */       return;
/*      */     }
/* 4178 */     if (selectedPeer.getUploadHint() == 0)
/*      */     {
/* 4180 */       selectedPeer.setUploadHint(31536000);
/*      */     }
/*      */     
/*      */ 
/* 4184 */     boolean found = false;
/* 4185 */     SuperSeedPiece piece = null;
/* 4186 */     boolean loopdone = false;
/*      */     
/* 4188 */     while (!found) {
/* 4189 */       piece = this.superSeedPieces[this.superSeedModeCurrentPiece];
/* 4190 */       if (piece.getLevel() > 0) {
/* 4191 */         piece = null;
/* 4192 */         this.superSeedModeCurrentPiece += 1;
/* 4193 */         if (this.superSeedModeCurrentPiece >= this._nbPieces) {
/* 4194 */           this.superSeedModeCurrentPiece = 0;
/*      */           
/* 4196 */           if (loopdone)
/*      */           {
/* 4198 */             this.superSeedMode = false;
/* 4199 */             closeAndRemoveAllPeers("quiting SuperSeed mode", true);
/* 4200 */             return;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 4205 */           loopdone = true;
/*      */         }
/*      */       }
/*      */       else {
/* 4209 */         found = true;
/*      */       }
/*      */     }
/*      */     
/* 4213 */     if (piece == null) {
/* 4214 */       return;
/*      */     }
/*      */     
/*      */ 
/* 4218 */     if (selectedPeer.isPieceAvailable(piece.getPieceNumber())) {
/* 4219 */       return;
/*      */     }
/*      */     
/* 4222 */     selectedPeer.setUniqueAnnounce(piece.getPieceNumber());
/* 4223 */     this.superSeedModeNumberOfAnnounces += 1;
/* 4224 */     piece.pieceRevealedToPeer();
/* 4225 */     selectedPeer.sendHave(piece.getPieceNumber());
/*      */   }
/*      */   
/*      */   public void updateSuperSeedPiece(PEPeer peer, int pieceNumber)
/*      */   {
/* 4230 */     if (!this.superSeedMode)
/* 4231 */       return;
/* 4232 */     this.superSeedPieces[pieceNumber].peerHasPiece(null);
/* 4233 */     if (peer.getUniqueAnnounce() == pieceNumber)
/*      */     {
/* 4235 */       peer.setUniqueAnnounce(-1);
/* 4236 */       this.superSeedModeNumberOfAnnounces -= 1;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPrivateTorrent()
/*      */   {
/* 4243 */     return this.is_private_torrent;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getExtendedMessagingMode()
/*      */   {
/* 4249 */     return this.adapter.getExtendedMessagingMode();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPeerExchangeEnabled()
/*      */   {
/* 4255 */     return this.adapter.isPeerExchangeEnabled();
/*      */   }
/*      */   
/* 4258 */   public LimitedRateGroup getUploadLimitedRateGroup() { return this.upload_limited_rate_group; }
/*      */   
/* 4260 */   public LimitedRateGroup getDownloadLimitedRateGroup() { return this.download_limited_rate_group; }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object getData(String key)
/*      */   {
/*      */     try
/*      */     {
/* 4269 */       this.this_mon.enter();
/*      */       Object localObject1;
/* 4271 */       if (this.user_data == null) { return null;
/*      */       }
/* 4273 */       return this.user_data.get(key);
/*      */     }
/*      */     finally
/*      */     {
/* 4277 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setData(String key, Object value)
/*      */   {
/*      */     try
/*      */     {
/* 4289 */       this.this_mon.enter();
/*      */       
/* 4291 */       if (this.user_data == null) {
/* 4292 */         this.user_data = new HashMap();
/*      */       }
/* 4294 */       if (value == null) {
/* 4295 */         if (this.user_data.containsKey(key))
/* 4296 */           this.user_data.remove(key);
/*      */       } else {
/* 4298 */         this.user_data.put(key, value);
/*      */       }
/*      */     } finally {
/* 4301 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getConnectTimeout(int ct_def)
/*      */   {
/* 4309 */     if (ct_def <= 0)
/*      */     {
/* 4311 */       return ct_def;
/*      */     }
/*      */     
/* 4314 */     if (this.seeding_mode)
/*      */     {
/*      */ 
/*      */ 
/* 4318 */       return ct_def;
/*      */     }
/*      */     
/* 4321 */     int max_sim_con = TCPConnectionManager.MAX_SIMULTANIOUS_CONNECT_ATTEMPTS;
/*      */     
/*      */ 
/*      */ 
/* 4325 */     if (max_sim_con >= 50)
/*      */     {
/* 4327 */       return ct_def;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4334 */     int connected = this._seeds + this._peers;
/* 4335 */     int connecting = this._tcpConnectingConnections;
/* 4336 */     int queued = this._tcpPendingConnections;
/*      */     
/* 4338 */     int not_yet_connected = this.peer_database.getDiscoveredPeerCount();
/*      */     
/* 4340 */     int max = getMaxConnections("");
/*      */     
/* 4342 */     int potential = connecting + queued + not_yet_connected;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4356 */     int lower_limit = max / 4;
/*      */     
/* 4358 */     if ((potential <= lower_limit) || (max == lower_limit))
/*      */     {
/* 4360 */       return ct_def;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4365 */     int MIN_CT = 7500;
/*      */     
/* 4367 */     if (potential >= max)
/*      */     {
/* 4369 */       return 7500;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4374 */     int pos = potential - lower_limit;
/* 4375 */     int scale = max - lower_limit;
/*      */     
/* 4377 */     int res = 7500 + (ct_def - 7500) * (scale - pos) / scale;
/*      */     
/*      */ 
/*      */ 
/* 4381 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doConnectionChecks()
/*      */   {
/* 4392 */     if (this.mainloop_loop_count % MAINLOOP_ONE_SECOND_INTERVAL == 0L) {
/* 4393 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 4395 */       int num_waiting_establishments = 0;
/*      */       
/* 4397 */       int udp_connections = 0;
/*      */       
/* 4399 */       for (int i = 0; i < peer_transports.size(); i++) {
/* 4400 */         PEPeerTransport transport = (PEPeerTransport)peer_transports.get(i);
/*      */         
/*      */ 
/* 4403 */         int state = transport.getConnectionState();
/* 4404 */         if ((state == 0) || (state == 1)) {
/* 4405 */           num_waiting_establishments++;
/*      */         }
/*      */         
/* 4408 */         if (!transport.isTCP())
/*      */         {
/* 4410 */           udp_connections++;
/*      */         }
/*      */       }
/*      */       
/* 4414 */       int[] allowed_seeds_info = getMaxSeedConnections();
/*      */       
/* 4416 */       int base_allowed_seeds = allowed_seeds_info[0];
/*      */       
/*      */ 
/*      */ 
/* 4420 */       if (base_allowed_seeds > 0)
/*      */       {
/* 4422 */         int extra_seeds = allowed_seeds_info[1];
/*      */         
/* 4424 */         int to_disconnect = this._seeds - base_allowed_seeds;
/*      */         
/* 4426 */         if (to_disconnect > 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4432 */           Set<PEPeerTransport> to_retain = new java.util.HashSet();
/*      */           
/* 4434 */           if (extra_seeds > 0)
/*      */           {
/*      */ 
/*      */ 
/* 4438 */             for (PEPeerTransport transport : peer_transports)
/*      */             {
/* 4440 */               if ((transport.isSeed()) && (transport.getNetwork() != "Public"))
/*      */               {
/* 4442 */                 to_retain.add(transport);
/*      */                 
/* 4444 */                 if (to_retain.size() == extra_seeds) {
/*      */                   break;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/* 4451 */             to_disconnect -= to_retain.size();
/*      */           }
/*      */           
/* 4454 */           for (int i = peer_transports.size() - 1; (i >= 0) && (to_disconnect > 0); i--)
/*      */           {
/* 4456 */             PEPeerTransport transport = (PEPeerTransport)peer_transports.get(i);
/*      */             
/* 4458 */             if (transport.isSeed())
/*      */             {
/* 4460 */               if (!to_retain.contains(transport))
/*      */               {
/* 4462 */                 closeAndRemovePeer(transport, "Too many seeds", false);
/*      */                 
/* 4464 */                 to_disconnect--;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 4471 */       int[] allowed_info = getMaxNewConnectionsAllowed();
/*      */       
/* 4473 */       int allowed_base = allowed_info[0];
/*      */       
/*      */ 
/*      */ 
/* 4477 */       if ((allowed_base < 0) || (allowed_base > 1000))
/*      */       {
/* 4479 */         allowed_base = 1000;
/*      */         
/* 4481 */         allowed_info[0] = allowed_base;
/*      */       }
/*      */       
/* 4484 */       if (this.adapter.isNATHealthy())
/*      */       {
/* 4486 */         int free = getMaxConnections()[0] / 20;
/*      */         
/* 4488 */         allowed_base -= free;
/*      */         
/* 4490 */         allowed_info[0] = allowed_base;
/*      */       }
/*      */       
/* 4493 */       for (int i = 0; i < allowed_info.length; i++)
/*      */       {
/* 4495 */         int allowed = allowed_info[i];
/*      */         
/* 4497 */         if (allowed > 0)
/*      */         {
/*      */ 
/*      */ 
/* 4501 */           int wanted = TCPConnectionManager.MAX_SIMULTANIOUS_CONNECT_ATTEMPTS - num_waiting_establishments;
/*      */           
/* 4503 */           if (wanted > allowed) {
/* 4504 */             num_waiting_establishments += wanted - allowed;
/*      */           }
/*      */           
/* 4507 */           int remaining = allowed;
/*      */           
/* 4509 */           int tcp_remaining = TCPNetworkManager.getSingleton().getConnectDisconnectManager().getMaxOutboundPermitted();
/*      */           
/* 4511 */           int udp_remaining = UDPNetworkManager.getSingleton().getConnectionManager().getMaxOutboundPermitted();
/*      */           
/* 4513 */           while ((num_waiting_establishments < TCPConnectionManager.MAX_SIMULTANIOUS_CONNECT_ATTEMPTS) && ((tcp_remaining > 0) || (udp_remaining > 0)))
/*      */           {
/* 4515 */             if (!this.is_running)
/*      */               break;
/* 4517 */             PeerItem item = this.peer_database.getNextOptimisticConnectPeer(i == 1);
/*      */             
/* 4519 */             if ((item == null) || (!this.is_running))
/*      */               break;
/* 4521 */             PeerItem self = this.peer_database.getSelfPeer();
/* 4522 */             if ((self == null) || (!self.equals(item)))
/*      */             {
/*      */ 
/*      */ 
/* 4526 */               if (!isAlreadyConnected(item)) {
/* 4527 */                 String source = PeerItem.convertSourceString(item.getSource());
/*      */                 
/* 4529 */                 boolean use_crypto = item.getHandshakeType() == 1;
/*      */                 
/* 4531 */                 int tcp_port = item.getTCPPort();
/* 4532 */                 int udp_port = item.getUDPPort();
/*      */                 
/* 4534 */                 if ((udp_port == 0) && (udp_probe_enabled))
/*      */                 {
/*      */ 
/*      */ 
/* 4538 */                   udp_port = tcp_port;
/*      */                 }
/*      */                 
/* 4541 */                 boolean prefer_udp_overall = (this.prefer_udp) || (prefer_udp_default);
/*      */                 
/* 4543 */                 if ((prefer_udp_overall) && (udp_port == 0))
/*      */                 {
/*      */ 
/*      */ 
/* 4547 */                   byte[] address = item.getIP().getBytes();
/*      */                   
/* 4549 */                   BloomFilter bloom = this.prefer_udp_bloom;
/*      */                   
/* 4551 */                   if ((bloom != null) && (bloom.contains(address)))
/*      */                   {
/* 4553 */                     udp_port = tcp_port;
/*      */                   }
/*      */                 }
/*      */                 
/* 4557 */                 boolean tcp_ok = (TCPNetworkManager.TCP_OUTGOING_ENABLED) && (tcp_port > 0) && (tcp_remaining > 0);
/* 4558 */                 boolean udp_ok = (UDPNetworkManager.UDP_OUTGOING_ENABLED) && (udp_port > 0) && (udp_remaining > 0);
/*      */                 
/* 4560 */                 if ((tcp_ok) && ((!prefer_udp_overall) || (!udp_ok)))
/*      */                 {
/* 4562 */                   if (makeNewOutgoingConnection(source, item.getAddressString(), tcp_port, udp_port, true, use_crypto, item.getCryptoLevel(), null) == null)
/*      */                   {
/* 4564 */                     tcp_remaining--;
/*      */                     
/* 4566 */                     num_waiting_establishments++;
/* 4567 */                     remaining--;
/*      */                   }
/* 4569 */                 } else if (udp_ok)
/*      */                 {
/* 4571 */                   if (makeNewOutgoingConnection(source, item.getAddressString(), tcp_port, udp_port, false, use_crypto, item.getCryptoLevel(), null) == null)
/*      */                   {
/* 4573 */                     udp_remaining--;
/*      */                     
/* 4575 */                     num_waiting_establishments++;
/*      */                     
/* 4577 */                     remaining--;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 4583 */           if (i == 0)
/*      */           {
/* 4585 */             if ((UDPNetworkManager.UDP_OUTGOING_ENABLED) && (remaining > 0) && (udp_remaining > 0) && (udp_connections < 16))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 4590 */               doUDPConnectionChecks(remaining);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4598 */     if (this.mainloop_loop_count % MAINLOOP_FIVE_SECOND_INTERVAL == 0L) {
/* 4599 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 4601 */       for (int i = 0; i < peer_transports.size(); i++) {
/* 4602 */         PEPeerTransport transport = (PEPeerTransport)peer_transports.get(i);
/*      */         
/*      */ 
/* 4605 */         if (!transport.doTimeoutChecks())
/*      */         {
/*      */ 
/* 4608 */           transport.doKeepAliveCheck();
/*      */           
/*      */ 
/* 4611 */           transport.doPerformanceTuningCheck();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4616 */     if (this.mainloop_loop_count % MAINLOOP_TEN_SECOND_INTERVAL == 0L)
/*      */     {
/* 4618 */       long last_update = ip_filter.getLastUpdateTime();
/* 4619 */       if (last_update != this.ip_filter_last_update_time)
/*      */       {
/* 4621 */         this.ip_filter_last_update_time = last_update;
/* 4622 */         checkForBannedConnections();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4627 */     if (this.mainloop_loop_count % MAINLOOP_THIRTY_SECOND_INTERVAL == 0L)
/*      */     {
/*      */ 
/* 4630 */       this.optimisticDisconnectCount = 0;
/* 4631 */       int[] allowed = getMaxNewConnectionsAllowed();
/* 4632 */       if (allowed[0] + allowed[1] == 0) {
/* 4633 */         doOptimisticDisconnect(false, false, "");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4639 */     float percentage = ((float)(this.mainloop_loop_count % MAINLOOP_SIXTY_SECOND_INTERVAL) + 1.0F) / (1.0F * MAINLOOP_SIXTY_SECOND_INTERVAL);
/*      */     int goal;
/* 4641 */     if (this.mainloop_loop_count % MAINLOOP_SIXTY_SECOND_INTERVAL == 0L)
/*      */     {
/* 4643 */       int goal = 0;
/* 4644 */       this.sweepList = this.peer_transports_cow;
/*      */     } else {
/* 4646 */       goal = (int)Math.floor(percentage * this.sweepList.size());
/*      */     }
/* 4648 */     for (int i = this.nextPEXSweepIndex; (i < goal) && (i < this.sweepList.size()); i++)
/*      */     {
/* 4650 */       PEPeerTransport peer = (PEPeerTransport)this.sweepList.get(i);
/* 4651 */       peer.updatePeerExchange();
/*      */     }
/*      */     
/* 4654 */     this.nextPEXSweepIndex = goal;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4661 */     if (this.mainloop_loop_count % MAINLOOP_SIXTY_SECOND_INTERVAL == 0L)
/*      */     {
/* 4663 */       List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */       
/* 4665 */       if (peer_transports.size() > 1)
/*      */       {
/* 4667 */         Map<String, List<PEPeerTransport>> peer_map = new HashMap();
/*      */         
/* 4669 */         for (PEPeerTransport peer : peer_transports)
/*      */         {
/* 4671 */           if (!peer.isIncoming())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 4676 */             if ((peer.getPeerState() == 10) && (peer.getConnectionState() == 1) && (peer.getLastMessageSentTime() != 0L))
/*      */             {
/*      */ 
/*      */ 
/* 4680 */               String key = peer.getIp() + ":" + peer.getPort();
/*      */               
/* 4682 */               List<PEPeerTransport> list = (List)peer_map.get(key);
/*      */               
/* 4684 */               if (list == null)
/*      */               {
/* 4686 */                 list = new ArrayList(1);
/*      */                 
/* 4688 */                 peer_map.put(key, list);
/*      */               }
/*      */               
/* 4691 */               list.add(peer);
/*      */             }
/*      */           }
/*      */         }
/* 4695 */         for (List<PEPeerTransport> list : peer_map.values())
/*      */         {
/* 4697 */           if (list.size() >= 2)
/*      */           {
/* 4699 */             long newest_time = Long.MIN_VALUE;
/* 4700 */             newest_peer = null;
/*      */             
/* 4702 */             for (PEPeerTransport peer : list)
/*      */             {
/* 4704 */               long last_sent = peer.getLastMessageSentTime();
/*      */               
/* 4706 */               if (last_sent > newest_time)
/*      */               {
/* 4708 */                 newest_time = last_sent;
/* 4709 */                 newest_peer = peer;
/*      */               }
/*      */             }
/*      */             
/* 4713 */             for (PEPeerTransport peer : list)
/*      */             {
/* 4715 */               if (peer != newest_peer)
/*      */               {
/* 4717 */                 if ((peer.getPeerState() == 10) && (peer.getConnectionState() == 1))
/*      */                 {
/*      */ 
/* 4720 */                   closeAndRemovePeer(peer, "Removing old duplicate connection", false);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     PEPeerTransport newest_peer;
/*      */   }
/*      */   
/*      */   private void doUDPConnectionChecks(int number)
/*      */   {
/* 4734 */     List<PEPeerTransport> new_connections = null;
/*      */     try
/*      */     {
/* 4737 */       this.peer_transports_mon.enter();
/*      */       
/* 4739 */       long now = SystemTime.getCurrentTime();
/*      */       
/* 4741 */       if ((this.udp_reconnects.size() > 0) && (now - this.last_udp_reconnect >= 10000L))
/*      */       {
/* 4743 */         this.last_udp_reconnect = now;
/*      */         
/* 4745 */         Iterator<PEPeerTransport> it = this.udp_reconnects.values().iterator();
/*      */         
/* 4747 */         PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */         
/* 4749 */         it.remove();
/*      */         
/* 4751 */         if (Logger.isEnabled()) {
/* 4752 */           Logger.log(new LogEvent(this, LOGID, 0, "Reconnecting to previous failed peer " + peer.getPeerItemIdentity().getAddressString()));
/*      */         }
/*      */         
/* 4755 */         if (new_connections == null)
/*      */         {
/* 4757 */           new_connections = new ArrayList();
/*      */         }
/*      */         
/* 4760 */         new_connections.add(peer);
/*      */         
/* 4762 */         number--;
/*      */         
/* 4764 */         if (number <= 0) {
/*      */           int i;
/*      */           PEPeerTransport peer_item;
/*      */           return;
/*      */         }
/*      */       }
/* 4770 */       if (this.pending_nat_traversals.size() == 0) {
/*      */         int i;
/*      */         PEPeerTransport peer_item;
/*      */         return;
/*      */       }
/* 4775 */       int max = 3;
/*      */       
/*      */ 
/*      */ 
/* 4779 */       if (this.seeding_mode)
/*      */       {
/* 4781 */         if (this._peers > 8)
/*      */         {
/* 4783 */           max = 0;
/*      */         }
/*      */         else
/*      */         {
/* 4787 */           max = 1;
/*      */         }
/* 4789 */       } else if (this._seeds > 8)
/*      */       {
/* 4791 */         max = 0;
/*      */       }
/* 4793 */       else if (this._seeds > 4)
/*      */       {
/* 4795 */         max = 1;
/*      */       }
/*      */       
/* 4798 */       int avail = max - this.udp_traversal_count;
/*      */       
/* 4800 */       int to_do = Math.min(number, avail);
/*      */       
/* 4802 */       Iterator<PEPeerTransport> it = this.pending_nat_traversals.values().iterator();
/*      */       
/* 4804 */       while ((to_do > 0) && (it.hasNext()))
/*      */       {
/* 4806 */         final PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */         
/* 4808 */         it.remove();
/*      */         
/* 4810 */         String peer_ip = peer.getPeerItemIdentity().getAddressString();
/*      */         
/* 4812 */         if (AENetworkClassifier.categoriseAddress(peer_ip) == "Public")
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 4817 */           to_do--;
/*      */           
/* 4819 */           PeerNATTraverser.getSingleton().create(this, new InetSocketAddress(peer_ip, peer.getPeerItemIdentity().getUDPPort()), new com.aelitis.azureus.core.peermanager.nat.PeerNATTraversalAdapter()
/*      */           {
/*      */             private boolean done;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             public void success(InetSocketAddress target)
/*      */             {
/* 4830 */               complete();
/*      */               
/* 4832 */               PEPeerTransport newTransport = peer.reconnect(true, false);
/*      */               
/* 4834 */               if (newTransport != null)
/*      */               {
/* 4836 */                 newTransport.setData(PEPeerControlImpl.PEER_NAT_TRAVERSE_DONE_KEY, "");
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */             public void failed()
/*      */             {
/* 4843 */               complete();
/*      */             }
/*      */             
/*      */             protected void complete()
/*      */             {
/*      */               try
/*      */               {
/* 4850 */                 PEPeerControlImpl.this.peer_transports_mon.enter();
/*      */                 
/* 4852 */                 if (!this.done)
/*      */                 {
/* 4854 */                   this.done = true;
/*      */                   
/* 4856 */                   PEPeerControlImpl.access$1710(PEPeerControlImpl.this);
/*      */                 }
/*      */               }
/*      */               finally {
/* 4860 */                 PEPeerControlImpl.this.peer_transports_mon.exit();
/*      */               }
/*      */               
/*      */             }
/* 4864 */           });
/* 4865 */           this.udp_traversal_count += 1;
/*      */         }
/*      */       } } finally { int i;
/*      */       PEPeerTransport peer_item;
/* 4869 */       this.peer_transports_mon.exit();
/*      */       
/* 4871 */       if (new_connections != null)
/*      */       {
/* 4873 */         for (int i = 0; i < new_connections.size(); i++)
/*      */         {
/* 4875 */           PEPeerTransport peer_item = (PEPeerTransport)new_connections.get(i);
/*      */           
/*      */ 
/* 4878 */           peer_item.reconnect(true, false);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/* 4886 */   private int optimisticDisconnectCount = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean doOptimisticDisconnect(boolean pending_lan_local_peer, boolean force, String network)
/*      */   {
/*      */     int non_pub_extra;
/*      */     
/*      */ 
/*      */     int non_pub_extra;
/*      */     
/*      */ 
/* 4899 */     if (network != "I2P")
/*      */     {
/* 4901 */       int[] max_con = getMaxConnections();
/*      */       
/* 4903 */       non_pub_extra = max_con[1];
/*      */     }
/*      */     else
/*      */     {
/* 4907 */       non_pub_extra = 0;
/*      */     }
/*      */     
/* 4910 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/*      */     
/* 4912 */     PEPeerTransport max_transport = null;
/* 4913 */     PEPeerTransport max_seed_transport = null;
/* 4914 */     PEPeerTransport max_non_lan_transport = null;
/*      */     
/* 4916 */     PEPeerTransport max_pub_transport = null;
/* 4917 */     PEPeerTransport max_pub_seed_transport = null;
/* 4918 */     PEPeerTransport max_pub_non_lan_transport = null;
/*      */     
/* 4920 */     long max_time = 0L;
/* 4921 */     long max_seed_time = 0L;
/* 4922 */     long max_non_lan_time = 0L;
/* 4923 */     long max_pub_time = 0L;
/* 4924 */     long max_pub_seed_time = 0L;
/* 4925 */     long max_pub_non_lan_time = 0L;
/*      */     
/* 4927 */     int non_pub_found = 0;
/*      */     
/* 4929 */     List<Long> activeConnectionTimes = new ArrayList(peer_transports.size());
/*      */     
/* 4931 */     int lan_peer_count = 0;
/*      */     
/* 4933 */     for (int i = 0; i < peer_transports.size(); i++)
/*      */     {
/* 4935 */       PEPeerTransport peer = (PEPeerTransport)peer_transports.get(i);
/*      */       
/* 4937 */       if (peer.getConnectionState() == 4)
/*      */       {
/* 4939 */         long timeSinceConnection = peer.getTimeSinceConnectionEstablished();
/* 4940 */         long timeSinceSentData = peer.getTimeSinceLastDataMessageSent();
/*      */         
/* 4942 */         activeConnectionTimes.add(Long.valueOf(timeSinceConnection));
/*      */         
/* 4944 */         long peerTestTime = 0L;
/* 4945 */         if (this.seeding_mode) {
/* 4946 */           if (timeSinceSentData != -1L)
/* 4947 */             peerTestTime = timeSinceSentData;
/*      */         } else {
/* 4949 */           long timeSinceGoodData = peer.getTimeSinceGoodDataReceived();
/*      */           
/*      */ 
/* 4952 */           if (timeSinceGoodData == -1L) {
/* 4953 */             peerTestTime += timeSinceConnection;
/*      */           } else {
/* 4955 */             peerTestTime += timeSinceGoodData;
/*      */           }
/*      */           
/* 4958 */           if (!peer.isInteresting())
/*      */           {
/* 4960 */             if (!peer.isInterested()) {
/* 4961 */               peerTestTime += timeSinceConnection + timeSinceSentData;
/*      */             } else {
/* 4963 */               peerTestTime += timeSinceConnection - timeSinceSentData;
/*      */             }
/* 4965 */             peerTestTime *= 2L;
/*      */           }
/*      */           
/* 4968 */           peerTestTime += peer.getSnubbedTime();
/*      */         }
/*      */         
/* 4971 */         if (!peer.isIncoming()) {
/* 4972 */           peerTestTime *= 2L;
/*      */         }
/*      */         
/* 4975 */         boolean count_pubs = (non_pub_extra > 0) && (peer.getNetwork() == "Public");
/*      */         
/* 4977 */         if (peer.isLANLocal())
/*      */         {
/* 4979 */           lan_peer_count++;
/*      */         }
/*      */         else
/*      */         {
/* 4983 */           if (peerTestTime > max_non_lan_time)
/*      */           {
/* 4985 */             max_non_lan_time = peerTestTime;
/* 4986 */             max_non_lan_transport = peer;
/*      */           }
/*      */           
/* 4989 */           if (count_pubs)
/*      */           {
/* 4991 */             if (peerTestTime > max_pub_non_lan_time)
/*      */             {
/* 4993 */               max_pub_non_lan_time = peerTestTime;
/* 4994 */               max_pub_non_lan_transport = peer;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 5001 */         if (!this.seeding_mode)
/*      */         {
/*      */ 
/* 5004 */           peerTestTime += peer.getSnubbedTime();
/* 5005 */           if (peer.getSnubbedTime() > 120L) { peerTestTime = (peerTestTime * 1.5D);
/*      */           }
/* 5007 */           PEPeerStats pestats = peer.getStats();
/*      */           
/* 5009 */           if (pestats.getTotalDataBytesReceived() + pestats.getTotalDataBytesSent() > 524288L) {
/* 5010 */             boolean goodPeer = true;
/*      */             
/*      */ 
/* 5013 */             if ((peer.isSnubbed()) && (pestats.getTotalDataBytesReceived() < pestats.getTotalDataBytesSent())) {
/* 5014 */               peerTestTime = (peerTestTime * 1.5D);
/* 5015 */               goodPeer = false;
/*      */             }
/*      */             
/* 5018 */             if (pestats.getTotalDataBytesSent() > pestats.getTotalDataBytesReceived() * 10L) {
/* 5019 */               peerTestTime *= 2L;
/* 5020 */               goodPeer = false;
/*      */             }
/*      */             
/* 5023 */             if ((pestats.getTotalDataBytesReceived() > 0L) && (pestats.getTotalBytesDiscarded() > 0L)) {
/* 5024 */               peerTestTime = (peerTestTime * (1.0D + pestats.getTotalBytesDiscarded() / pestats.getTotalDataBytesReceived()));
/*      */             }
/*      */             
/*      */ 
/* 5028 */             if (goodPeer) {
/* 5029 */               peerTestTime = (peerTestTime * 0.7D);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5034 */         if (peerTestTime > max_time)
/*      */         {
/* 5036 */           max_time = peerTestTime;
/* 5037 */           max_transport = peer;
/*      */         }
/*      */         
/* 5040 */         if (count_pubs)
/*      */         {
/* 5042 */           if (peerTestTime > max_pub_time)
/*      */           {
/* 5044 */             max_pub_time = peerTestTime;
/* 5045 */             max_pub_transport = peer;
/*      */           }
/*      */         }
/*      */         else {
/* 5049 */           non_pub_found++;
/*      */         }
/*      */         
/* 5052 */         if ((peer.isSeed()) || (peer.isRelativeSeed()))
/*      */         {
/* 5054 */           if (peerTestTime > max_seed_time)
/*      */           {
/* 5056 */             max_seed_time = peerTestTime;
/* 5057 */             max_seed_transport = peer;
/*      */           }
/*      */           
/* 5060 */           if (count_pubs)
/*      */           {
/* 5062 */             if (peerTestTime > max_pub_seed_time)
/*      */             {
/* 5064 */               max_pub_seed_time = peerTestTime;
/* 5065 */               max_pub_seed_transport = peer;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 5072 */     if (non_pub_extra > 0)
/*      */     {
/* 5074 */       if (non_pub_found <= non_pub_extra)
/*      */       {
/*      */ 
/*      */ 
/* 5078 */         if ((max_transport != null) && (max_transport.getNetwork() != "Public"))
/*      */         {
/* 5080 */           max_time = max_pub_time;
/* 5081 */           max_transport = max_pub_transport;
/*      */         }
/*      */         
/* 5084 */         if ((max_seed_transport != null) && (max_seed_transport.getNetwork() != "Public"))
/*      */         {
/* 5086 */           max_seed_time = max_pub_seed_time;
/* 5087 */           max_seed_transport = max_pub_seed_transport;
/*      */         }
/*      */         
/* 5090 */         if ((max_non_lan_transport != null) && (max_non_lan_transport.getNetwork() != "Public"))
/*      */         {
/* 5092 */           max_non_lan_time = max_pub_non_lan_time;
/* 5093 */           max_non_lan_transport = max_pub_non_lan_transport;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     long medianConnectionTime;
/*      */     long medianConnectionTime;
/* 5100 */     if (activeConnectionTimes.size() > 0) {
/* 5101 */       Collections.sort(activeConnectionTimes);
/* 5102 */       medianConnectionTime = ((Long)activeConnectionTimes.get(activeConnectionTimes.size() / 2)).longValue();
/*      */     } else {
/* 5104 */       medianConnectionTime = 0L;
/*      */     }
/*      */     
/* 5107 */     int max_con = getMaxConnections(network);
/*      */     
/*      */ 
/* 5110 */     int maxOptimistics = max_con == 0 ? 8 : Math.max(max_con / 30, 2);
/*      */     
/*      */ 
/* 5113 */     if ((!pending_lan_local_peer) && (!force) && (this.optimisticDisconnectCount >= maxOptimistics) && (medianConnectionTime < 300000L)) {
/* 5114 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 5119 */     if (max_transport != null)
/*      */     {
/* 5121 */       int LAN_PEER_MAX = 4;
/*      */       
/* 5123 */       if ((max_transport.isLANLocal()) && (lan_peer_count < 4) && (max_non_lan_transport != null))
/*      */       {
/*      */ 
/*      */ 
/* 5127 */         max_transport = max_non_lan_transport;
/* 5128 */         max_time = max_non_lan_time;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 5133 */       if ((getMaxSeedConnections(network) > 0) && (max_seed_transport != null) && (max_time > 300000L)) {
/* 5134 */         closeAndRemovePeer(max_seed_transport, "timed out by doOptimisticDisconnect()", true);
/* 5135 */         this.optimisticDisconnectCount += 1;
/* 5136 */         return true;
/*      */       }
/*      */       
/* 5139 */       if ((max_transport != null) && (max_time > 300000L)) {
/* 5140 */         closeAndRemovePeer(max_transport, "timed out by doOptimisticDisconnect()", true);
/* 5141 */         this.optimisticDisconnectCount += 1;
/* 5142 */         return true;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 5147 */       if ((pending_lan_local_peer) && (lan_peer_count < 4)) {
/* 5148 */         closeAndRemovePeer(max_transport, "making space for LAN peer in doOptimisticDisconnect()", true);
/* 5149 */         this.optimisticDisconnectCount += 1;
/* 5150 */         return true;
/*      */       }
/*      */       
/* 5153 */       if (force)
/*      */       {
/* 5155 */         closeAndRemovePeer(max_transport, "force removal of worst peer in doOptimisticDisconnect()", true);
/*      */         
/* 5157 */         return true;
/*      */       }
/* 5159 */     } else if (force)
/*      */     {
/* 5161 */       if (peer_transports.size() > 0)
/*      */       {
/* 5163 */         PEPeerTransport pt = (PEPeerTransport)peer_transports.get(new Random().nextInt(peer_transports.size()));
/*      */         
/* 5165 */         closeAndRemovePeer(pt, "force removal of random peer in doOptimisticDisconnect()", true);
/*      */         
/* 5167 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 5171 */     return false;
/*      */   }
/*      */   
/*      */   public com.aelitis.azureus.core.peermanager.peerdb.PeerExchangerItem createPeerExchangeConnection(final PEPeerTransport base_peer)
/*      */   {
/* 5176 */     if (base_peer.getTCPListenPort() > 0) {
/* 5177 */       PeerItem peer = PeerItemFactory.createPeerItem(base_peer.getIp(), base_peer.getTCPListenPort(), (byte)2, base_peer.getPeerItemIdentity().getHandshakeType(), base_peer.getUDPListenPort(), (byte)1, 0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5186 */       this.peer_database.registerPeerConnection(peer, new com.aelitis.azureus.core.peermanager.peerdb.PeerExchangerItem.Helper()
/*      */       {
/*      */         public boolean isSeed()
/*      */         {
/* 5190 */           return base_peer.isSeed();
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 5195 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean isAlreadyConnected(PeerItem peer_id)
/*      */   {
/* 5201 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 5202 */     for (int i = 0; i < peer_transports.size(); i++) {
/* 5203 */       PEPeerTransport peer = (PEPeerTransport)peer_transports.get(i);
/* 5204 */       if (peer.getPeerItemIdentity().equals(peer_id)) return true;
/*      */     }
/* 5206 */     return false;
/*      */   }
/*      */   
/*      */   public void peerVerifiedAsSelf(PEPeerTransport self)
/*      */   {
/* 5211 */     if (self.getTCPListenPort() > 0) {
/* 5212 */       PeerItem peer = PeerItemFactory.createPeerItem(self.getIp(), self.getTCPListenPort(), PeerItem.convertSourceID(self.getPeerSource()), self.getPeerItemIdentity().getHandshakeType(), self.getUDPListenPort(), (byte)2, 0);
/*      */       
/* 5214 */       this.peer_database.setSelfPeer(peer);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void IPFilterEnabledChanged(boolean is_enabled)
/*      */   {
/* 5222 */     if (is_enabled)
/*      */     {
/* 5224 */       checkForBannedConnections();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canIPBeBanned(String ip)
/*      */   {
/* 5232 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canIPBeBlocked(String ip, byte[] torrent_hash)
/*      */   {
/* 5240 */     return true;
/*      */   }
/*      */   
/*      */   public void IPBlockedListChanged(IpFilter filter) {
/* 5244 */     Iterator<PEPeerTransport> it = this.peer_transports_cow.iterator();
/*      */     
/* 5246 */     String name = getDisplayName();
/* 5247 */     byte[] hash = getTorrentHash();
/*      */     
/* 5249 */     while (it.hasNext()) {
/*      */       try {
/* 5251 */         PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */         
/* 5253 */         if (filter.isInRange(peer.getIp(), name, hash)) {
/* 5254 */           peer.closeConnection("IP address blocked by filters");
/*      */         }
/*      */       }
/*      */       catch (Exception e) {}
/*      */     }
/*      */   }
/*      */   
/*      */   public void IPBanned(BannedIp ip)
/*      */   {
/* 5263 */     for (int i = 0; i < this._nbPieces; i++)
/*      */     {
/* 5265 */       if (this.pePieces[i] != null) {
/* 5266 */         this.pePieces[i].reDownloadBlocks(ip.getIp());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public long getHiddenBytes()
/*      */   {
/* 5273 */     if (this.hidden_piece < 0)
/*      */     {
/* 5275 */       return 0L;
/*      */     }
/*      */     
/* 5278 */     return this.dm_pieces[this.hidden_piece].getLength();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getHiddenPiece()
/*      */   {
/* 5284 */     return this.hidden_piece;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUploadPriority()
/*      */   {
/* 5290 */     return this.adapter.getUploadPriority();
/*      */   }
/*      */   
/*      */   public int getAverageCompletionInThousandNotation()
/*      */   {
/* 5295 */     ArrayList peer_transports = this.peer_transports_cow;
/*      */     
/* 5297 */     if (peer_transports != null)
/*      */     {
/* 5299 */       long total = this.disk_mgr.getTotalLength();
/*      */       
/* 5301 */       int my_completion = total == 0L ? 1000 : (int)(1000L * (total - this.disk_mgr.getRemainingExcludingDND()) / total);
/*      */       
/*      */ 
/*      */ 
/* 5305 */       int sum = my_completion == 1000 ? 0 : my_completion;
/* 5306 */       int num = my_completion == 1000 ? 0 : 1;
/*      */       
/* 5308 */       for (int i = 0; i < peer_transports.size(); i++)
/*      */       {
/* 5310 */         PEPeer peer = (PEPeer)peer_transports.get(i);
/*      */         
/* 5312 */         if ((peer.getPeerState() == 30) && (!peer.isSeed()))
/*      */         {
/* 5314 */           num++;
/* 5315 */           sum += peer.getPercentDoneInThousandNotation();
/*      */         }
/*      */       }
/*      */       
/* 5319 */       return num > 0 ? sum / num : 0;
/*      */     }
/*      */     
/* 5322 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getMaxConnections()
/*      */   {
/* 5328 */     return this.adapter.getMaxConnections();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getMaxConnections(String net)
/*      */   {
/* 5335 */     int[] data = getMaxConnections();
/*      */     
/* 5337 */     int result = data[0];
/*      */     
/*      */ 
/*      */ 
/* 5341 */     if (result > 0)
/*      */     {
/* 5343 */       if (net != "Public")
/*      */       {
/* 5345 */         result += data[1];
/*      */       }
/*      */     }
/*      */     
/* 5349 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getMaxSeedConnections()
/*      */   {
/* 5355 */     return this.adapter.getMaxSeedConnections();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private int getMaxSeedConnections(String net)
/*      */   {
/* 5362 */     int[] data = getMaxSeedConnections();
/*      */     
/* 5364 */     int result = data[0];
/*      */     
/*      */ 
/*      */ 
/* 5368 */     if (result > 0)
/*      */     {
/* 5370 */       if (net != "Public")
/*      */       {
/* 5372 */         result += data[1];
/*      */       }
/*      */     }
/*      */     
/* 5376 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxNewConnectionsAllowed(String network)
/*      */   {
/* 5388 */     int[] max_con = getMaxConnections();
/*      */     
/* 5390 */     int dl_max = max_con[0];
/*      */     
/* 5392 */     if (network != "Public")
/*      */     {
/* 5394 */       dl_max += max_con[1];
/*      */     }
/*      */     
/* 5397 */     int allowed_peers = PeerUtils.numNewConnectionsAllowed(getPeerIdentityDataID(), dl_max);
/*      */     
/* 5399 */     return allowed_peers;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int[] getMaxNewConnectionsAllowed()
/*      */   {
/* 5410 */     int[] max_con = getMaxConnections();
/*      */     
/* 5412 */     int dl_max = max_con[0];
/* 5413 */     int extra = max_con[1];
/*      */     
/* 5415 */     int allowed_peers = PeerUtils.numNewConnectionsAllowed(getPeerIdentityDataID(), dl_max + extra);
/*      */     
/*      */ 
/*      */ 
/* 5419 */     if (allowed_peers >= 0)
/*      */     {
/* 5421 */       allowed_peers -= extra;
/*      */       
/* 5423 */       if (allowed_peers < 0)
/*      */       {
/* 5425 */         extra += allowed_peers;
/*      */         
/* 5427 */         if (extra < 0)
/*      */         {
/* 5429 */           extra = 0;
/*      */         }
/*      */         
/* 5432 */         allowed_peers = 0;
/*      */       }
/*      */     }
/*      */     
/* 5436 */     return new int[] { allowed_peers, extra };
/*      */   }
/*      */   
/*      */   public int getSchedulePriority() {
/* 5440 */     return isSeeding() ? Integer.MAX_VALUE : this.adapter.getPosition();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasPotentialConnections()
/*      */   {
/* 5446 */     return this.pending_nat_traversals.size() + this.peer_database.getDiscoveredPeerCount() > 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 5452 */     return this.adapter.getLogRelation().getRelationText();
/*      */   }
/*      */   
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/* 5458 */     return this.adapter.getLogRelation().getQueryableInterfaces();
/*      */   }
/*      */   
/*      */ 
/*      */   public PEPeerTransport getTransportFromIdentity(byte[] peer_id)
/*      */   {
/* 5464 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 5465 */     for (int i = 0; i < peer_transports.size(); i++) {
/* 5466 */       PEPeerTransport conn = (PEPeerTransport)peer_transports.get(i);
/* 5467 */       if (Arrays.equals(peer_id, conn.getId())) return conn;
/*      */     }
/* 5469 */     return null;
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
/*      */   public PEPeerTransport getTransportFromAddress(String peer)
/*      */   {
/* 5489 */     List<PEPeerTransport> peer_transports = this.peer_transports_cow;
/* 5490 */     for (int i = 0; i < peer_transports.size(); i++)
/*      */     {
/* 5492 */       PEPeerTransport pt = (PEPeerTransport)peer_transports.get(i);
/* 5493 */       if (peer.equals(pt.getIp()))
/* 5494 */         return pt;
/*      */     }
/* 5496 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public void incNbPeersSnubbed()
/*      */   {
/* 5502 */     this.nbPeersSnubbed += 1;
/*      */   }
/*      */   
/*      */   public void decNbPeersSnubbed()
/*      */   {
/* 5507 */     this.nbPeersSnubbed -= 1;
/*      */   }
/*      */   
/*      */   public void setNbPeersSnubbed(int n)
/*      */   {
/* 5512 */     this.nbPeersSnubbed = n;
/*      */   }
/*      */   
/*      */   public int getNbPeersSnubbed()
/*      */   {
/* 5517 */     return this.nbPeersSnubbed;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean getPreferUDP()
/*      */   {
/* 5523 */     return this.prefer_udp;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPreferUDP(boolean prefer)
/*      */   {
/* 5530 */     this.prefer_udp = prefer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isPeerSourceEnabled(String peer_source)
/*      */   {
/* 5537 */     return this.adapter.isPeerSourceEnabled(peer_source);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isNetworkEnabled(String net)
/*      */   {
/* 5544 */     return this.adapter.isNetworkEnabled(net);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void peerDiscovered(PEPeerTransport finder, PeerItem pi)
/*      */   {
/* 5552 */     ArrayList peer_manager_listeners = this.peer_manager_listeners_cow;
/*      */     
/* 5554 */     for (int i = 0; i < peer_manager_listeners.size(); i++) {
/*      */       try {
/* 5556 */         ((PEPeerManagerListener)peer_manager_listeners.get(i)).peerDiscovered(this, pi, finder);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 5560 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.core.tracker.TrackerPeerSource getTrackerPeerSource()
/*      */   {
/* 5568 */     new com.aelitis.azureus.core.tracker.TrackerPeerSourceAdapter()
/*      */     {
/*      */ 
/*      */       public int getType()
/*      */       {
/*      */ 
/* 5574 */         return 5;
/*      */       }
/*      */       
/*      */ 
/*      */       public int getStatus()
/*      */       {
/* 5580 */         return PEPeerControlImpl.this.isPeerExchangeEnabled() ? 5 : 1;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getName()
/*      */       {
/* 5586 */         return org.gudy.azureus2.core3.internat.MessageText.getString("tps.pex.details", new String[] { String.valueOf(PEPeerControlImpl.this.peer_transports_cow.size()), String.valueOf(PEPeerControlImpl.this.peer_database.getExchangedPeerCount()), String.valueOf(PEPeerControlImpl.this.peer_database.getDiscoveredPeerCount()) });
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public int getPeers()
/*      */       {
/* 5597 */         return PEPeerControlImpl.this.isPeerExchangeEnabled() ? PEPeerControlImpl.this.peer_database.getExchangedPeersUsed() : -1;
/*      */       }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 5606 */     writer.println("PeerManager: seeding=" + this.seeding_mode);
/*      */     
/* 5608 */     writer.println("    udp_fb=" + this.pending_nat_traversals.size() + ",udp_tc=" + this.udp_traversal_count + ",pd=[" + this.peer_database.getString() + "]");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5613 */     String pending_udp = "";
/*      */     try
/*      */     {
/* 5616 */       this.peer_transports_mon.enter();
/*      */       
/* 5618 */       Iterator<PEPeerTransport> it = this.pending_nat_traversals.values().iterator();
/*      */       
/* 5620 */       while (it.hasNext())
/*      */       {
/* 5622 */         PEPeerTransport peer = (PEPeerTransport)it.next();
/*      */         
/* 5624 */         pending_udp = pending_udp + (pending_udp.length() == 0 ? "" : ",") + peer.getPeerItemIdentity().getAddressString() + ":" + peer.getPeerItemIdentity().getUDPPort();
/*      */       }
/*      */     }
/*      */     finally {
/* 5628 */       this.peer_transports_mon.exit();
/*      */     }
/*      */     
/* 5631 */     if (pending_udp.length() > 0)
/*      */     {
/* 5633 */       writer.println("    pending_udp=" + pending_udp);
/*      */     }
/*      */     
/* 5636 */     List traversals = PeerNATTraverser.getSingleton().getTraversals(this);
/*      */     
/* 5638 */     String active_udp = "";
/*      */     
/* 5640 */     Iterator it1 = traversals.iterator();
/*      */     
/* 5642 */     while (it1.hasNext())
/*      */     {
/* 5644 */       InetSocketAddress ad = (InetSocketAddress)it1.next();
/*      */       
/* 5646 */       active_udp = active_udp + (active_udp.length() == 0 ? "" : ",") + AddressUtils.getHostAddress(ad) + ":" + ad.getPort();
/*      */     }
/*      */     
/* 5649 */     if (active_udp.length() > 0)
/*      */     {
/* 5651 */       writer.println("    active_udp=" + active_udp);
/*      */     }
/*      */     
/* 5654 */     if (!this.seeding_mode)
/*      */     {
/* 5656 */       writer.println("  Active Pieces");
/*      */       
/* 5658 */       int num_active = 0;
/*      */       try
/*      */       {
/* 5661 */         writer.indent();
/*      */         
/* 5663 */         String str = "";
/* 5664 */         int num = 0;
/*      */         
/* 5666 */         for (int i = 0; i < this.pePieces.length; i++)
/*      */         {
/* 5668 */           PEPiece piece = this.pePieces[i];
/*      */           
/* 5670 */           if (piece != null)
/*      */           {
/* 5672 */             num_active++;
/*      */             
/* 5674 */             str = str + (str.length() == 0 ? "" : ",") + "#" + i + " " + this.dm_pieces[i].getString() + ": " + piece.getString();
/*      */             
/* 5676 */             num++;
/*      */             
/* 5678 */             if (num == 20)
/*      */             {
/* 5680 */               writer.println(str);
/* 5681 */               str = "";
/* 5682 */               num = 0;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5687 */         if (num > 0) {
/* 5688 */           writer.println(str);
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 5693 */         writer.exdent();
/*      */       }
/*      */       
/* 5696 */       if (num_active == 0)
/*      */       {
/* 5698 */         writer.println("  Inactive Pieces (excluding done/skipped)");
/*      */         try
/*      */         {
/* 5701 */           writer.indent();
/*      */           
/* 5703 */           String str = "";
/* 5704 */           int num = 0;
/*      */           
/* 5706 */           for (int i = 0; i < this.dm_pieces.length; i++)
/*      */           {
/* 5708 */             DiskManagerPiece dm_piece = this.dm_pieces[i];
/*      */             
/* 5710 */             if (dm_piece.isInteresting())
/*      */             {
/* 5712 */               str = str + (str.length() == 0 ? "" : ",") + "#" + i + " " + this.dm_pieces[i].getString();
/*      */               
/* 5714 */               num++;
/*      */               
/* 5716 */               if (num == 20)
/*      */               {
/* 5718 */                 writer.println(str);
/* 5719 */                 str = "";
/* 5720 */                 num = 0;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 5725 */           if (num > 0)
/*      */           {
/* 5727 */             writer.println(str);
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 5732 */           writer.exdent();
/*      */         }
/*      */       }
/*      */       
/* 5736 */       this.piecePicker.generateEvidence(writer);
/*      */     }
/*      */     try
/*      */     {
/* 5740 */       this.peer_transports_mon.enter();
/*      */       
/* 5742 */       writer.println("Peers: total = " + this.peer_transports_cow.size());
/*      */       
/* 5744 */       writer.indent();
/*      */       try
/*      */       {
/* 5747 */         writer.indent();
/*      */         
/* 5749 */         Iterator<PEPeerTransport> it2 = this.peer_transports_cow.iterator();
/*      */         
/* 5751 */         while (it2.hasNext())
/*      */         {
/* 5753 */           PEPeerTransport peer = (PEPeerTransport)it2.next();
/*      */           
/* 5755 */           peer.generateEvidence(writer);
/*      */         }
/*      */       }
/*      */       finally {
/* 5759 */         writer.exdent();
/*      */       }
/*      */     }
/*      */     finally {
/* 5763 */       this.peer_transports_mon.exit();
/*      */       
/* 5765 */       writer.exdent();
/*      */     }
/*      */     
/* 5768 */     this.disk_mgr.generateEvidence(writer);
/*      */   }
/*      */   
/*      */   public void writeFailed(DiskManagerWriteRequest request, Throwable cause) {}
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/control/PEPeerControlImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */