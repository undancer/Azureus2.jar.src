/*      */ package org.gudy.azureus2.core3.peer.impl.transport;
/*      */ 
/*      */ import com.aelitis.azureus.core.networkmanager.ConnectionEndpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.IncomingMessageQueue;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkConnection;
/*      */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*      */ import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
/*      */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpoint;
/*      */ import com.aelitis.azureus.core.networkmanager.ProtocolEndpointFactory;
/*      */ import com.aelitis.azureus.core.networkmanager.Transport;
/*      */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*      */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZBadPiece;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZHandshake;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZHave;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZRequestHint;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZStatReply;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZStatRequest;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZStylePeerExchange;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZUTMetaData;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTAllowedFast;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTBitfield;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTCancel;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTDHTPort;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHandshake;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHave;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHaveNone;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTInterested;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTPiece;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTRejectRequest;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTRequest;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.LTHandshake;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.LTMessageEncoder;
/*      */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTMetaData;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerExchangerItem;
/*      */ import com.aelitis.azureus.core.peermanager.peerdb.PeerItem;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;
/*      */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*      */ import com.aelitis.azureus.core.peermanager.utils.OutgoingBTHaveMessageAggregator;
/*      */ import com.aelitis.azureus.core.peermanager.utils.OutgoingBTPieceMessageHandler;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerPiece;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerReadRequest;
/*      */ import org.gudy.azureus2.core3.logging.LogEvent;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.logging.Logger;
/*      */ import org.gudy.azureus2.core3.peer.PEPeer;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerListener;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerAdapter;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*      */ import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
/*      */ import org.gudy.azureus2.core3.peer.util.PeerIdentityManager;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*      */ import org.gudy.azureus2.core3.util.StringInterner;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ public class PEPeerTransportProtocol extends org.gudy.azureus2.core3.logging.LogRelation implements PEPeerTransport
/*      */ {
/*   78 */   protected static final LogIDs LOGID = LogIDs.PEER;
/*      */   
/*   80 */   private volatile int _lastPiece = -1;
/*      */   
/*      */   protected final PEPeerControl manager;
/*      */   
/*      */   protected final DiskManager diskManager;
/*      */   
/*      */   protected final PiecePicker piecePicker;
/*      */   
/*      */   protected final int nbPieces;
/*      */   
/*      */   private final String peer_source;
/*      */   private byte[] peer_id;
/*      */   private final String ip;
/*      */   private final String network;
/*      */   protected String ip_resolved;
/*      */   private org.gudy.azureus2.core3.util.IPToHostNameResolverRequest ip_resolver_request;
/*      */   private final int port;
/*      */   private PeerItem peer_item_identity;
/*   98 */   private int tcp_listen_port = 0;
/*   99 */   private int udp_listen_port = 0;
/*  100 */   private int udp_non_data_port = 0;
/*      */   
/*      */   private InetAddress alternativeAddress;
/*      */   
/*      */   private byte crypto_level;
/*      */   
/*      */   protected PEPeerStats peer_stats;
/*      */   
/*  108 */   private final ArrayList<DiskManagerReadRequest> requested = new ArrayList();
/*  109 */   private final AEMonitor requested_mon = new AEMonitor("PEPeerTransportProtocol:Req");
/*      */   
/*      */   private Map data;
/*      */   
/*      */   private long lastNeededUndonePieceChange;
/*      */   
/*  115 */   private boolean really_choked_by_other_peer = true;
/*  116 */   private boolean effectively_choked_by_other_peer = true;
/*  117 */   private long effectively_unchoked_time = -1L;
/*      */   
/*  119 */   protected boolean choking_other_peer = true;
/*  120 */   private boolean interested_in_other_peer = false;
/*  121 */   private boolean other_peer_interested_in_me = false;
/*  122 */   private long snubbed = 0L;
/*      */   
/*      */ 
/*  125 */   private volatile BitFlags peerHavePieces = null;
/*  126 */   private volatile boolean availabilityAdded = false;
/*      */   
/*      */   private volatile boolean received_bitfield;
/*      */   
/*      */   private int[] piece_priority_offsets;
/*      */   
/*      */   private boolean handshake_sent;
/*  133 */   private boolean seeding = false;
/*      */   
/*      */   private static final byte RELATIVE_SEEDING_NONE = 0;
/*      */   
/*      */   private static final byte RELATIVE_SEEDING_UPLOAD_ONLY_INDICATED = 1;
/*      */   
/*      */   private static final byte RELATIVE_SEEDING_UPLOAD_ONLY_SEED = 2;
/*  140 */   private byte relativeSeeding = 0;
/*      */   
/*      */   private final boolean incoming;
/*      */   
/*  144 */   protected volatile boolean closing = false;
/*      */   
/*      */   private volatile int current_peer_state;
/*      */   
/*      */   private final NetworkConnection connection;
/*      */   private OutgoingBTPieceMessageHandler outgoing_piece_message_handler;
/*      */   private OutgoingBTHaveMessageAggregator outgoing_have_message_aggregator;
/*      */   private org.gudy.azureus2.plugins.network.Connection plugin_connection;
/*  152 */   private boolean identityAdded = false;
/*      */   
/*  154 */   protected int connection_state = 0;
/*      */   
/*  156 */   private String client = "";
/*  157 */   private String client_peer_id = "";
/*  158 */   private String client_handshake = "";
/*  159 */   private String client_handshake_version = "";
/*      */   
/*      */ 
/*  162 */   private int uniquePiece = -1;
/*      */   
/*      */ 
/*  165 */   private int[] reserved_pieces = null;
/*      */   
/*      */ 
/*  168 */   private int spreadTimeHint = 0;
/*      */   
/*  170 */   private long last_message_sent_time = 0L;
/*  171 */   private long last_message_received_time = 0L;
/*  172 */   private long last_data_message_received_time = -1L;
/*  173 */   private long last_good_data_time = -1L;
/*  174 */   private long last_data_message_sent_time = -1L;
/*      */   
/*  176 */   private long connection_established_time = 0L;
/*      */   
/*      */   private int consecutive_no_request_count;
/*      */   
/*  180 */   private int messaging_mode = 1;
/*  181 */   private Message[] supported_messages = null;
/*  182 */   private byte other_peer_bitfield_version = 1;
/*  183 */   private byte other_peer_cancel_version = 1;
/*  184 */   private byte other_peer_choke_version = 1;
/*  185 */   private byte other_peer_handshake_version = 1;
/*  186 */   private byte other_peer_bt_have_version = 1;
/*  187 */   private byte other_peer_az_have_version = 1;
/*  188 */   private byte other_peer_interested_version = 1;
/*  189 */   private byte other_peer_keep_alive_version = 1;
/*  190 */   private byte other_peer_pex_version = 1;
/*  191 */   private byte other_peer_piece_version = 1;
/*  192 */   private byte other_peer_unchoke_version = 1;
/*  193 */   private byte other_peer_uninterested_version = 1;
/*  194 */   private byte other_peer_request_version = 1;
/*  195 */   private byte other_peer_suggest_piece_version = 1;
/*  196 */   private byte other_peer_have_all_version = 1;
/*  197 */   private byte other_peer_have_none_version = 1;
/*  198 */   private byte other_peer_reject_request_version = 1;
/*  199 */   private byte other_peer_allowed_fast_version = 1;
/*  200 */   private final byte other_peer_bt_lt_ext_version = 1;
/*  201 */   private byte other_peer_az_request_hint_version = 1;
/*  202 */   private byte other_peer_az_bad_piece_version = 1;
/*  203 */   private byte other_peer_az_stats_request_version = 1;
/*  204 */   private byte other_peer_az_stats_reply_version = 1;
/*  205 */   private byte other_peer_az_metadata_version = 1;
/*      */   
/*      */   private static final boolean DEBUG_FAST = false;
/*      */   
/*  209 */   private boolean ut_pex_enabled = false;
/*  210 */   private boolean fast_extension_enabled = false;
/*  211 */   private boolean ml_dht_enabled = false;
/*      */   
/*      */   private static final int ALLOWED_FAST_PIECE_OFFERED_NUM = 10;
/*      */   
/*      */   private static final int ALLOWED_FAST_OTHER_PEER_PIECE_MAX = 10;
/*  216 */   private static final Object KEY_ALLOWED_FAST_RECEIVED = new Object();
/*  217 */   private static final Object KEY_ALLOWED_FAST_SENT = new Object();
/*      */   
/*  219 */   private final AEMonitor closing_mon = new AEMonitor("PEPeerTransportProtocol:closing");
/*  220 */   private final AEMonitor general_mon = new AEMonitor("PEPeerTransportProtocol:data");
/*      */   
/*  222 */   private byte[] handshake_reserved_bytes = null;
/*      */   
/*      */   private LinkedHashMap recent_outgoing_requests;
/*      */   
/*      */   private AEMonitor recent_outgoing_requests_mon;
/*  227 */   private boolean has_received_initial_pex = false;
/*      */   
/*      */   private static final boolean SHOW_DISCARD_RATE_STATS;
/*      */   
/*      */   private static int requests_discarded;
/*      */   
/*      */   private static int requests_discarded_endgame;
/*      */   
/*      */   private static int requests_recovered;
/*      */   
/*      */   private static int requests_completed;
/*      */   
/*      */   private static final int REQUEST_HINT_MAX_LIFE = 150000;
/*      */   
/*      */   private int[] request_hint;
/*      */   
/*      */   private List peer_listeners_cow;
/*      */   
/*  245 */   private final AEMonitor peer_listeners_mon = new AEMonitor("PEPeerTransportProtocol:PL");
/*      */   
/*      */   protected static boolean ENABLE_LAZY_BITFIELD;
/*      */   private boolean priority_connection;
/*      */   private int upload_priority_auto;
/*      */   private static final DisconnectedTransportQueue recentlyDisconnected;
/*      */   private static boolean fast_unchoke_new_peers;
/*      */   private static final Random rnd;
/*      */   private static final byte[] sessionSecret;
/*      */   
/*      */   private static final class DisconnectedTransportQueue
/*      */     extends LinkedHashMap
/*      */   {
/*      */     public DisconnectedTransportQueue()
/*      */     {
/*  260 */       super(0.75F); }
/*      */     
/*      */ 
/*      */ 
/*      */     private static final long MAX_CACHE_AGE = 120000L;
/*      */     
/*      */     private void performCleaning()
/*      */     {
/*  268 */       if (size() > 20)
/*      */       {
/*  270 */         Iterator it = values().iterator();
/*      */         
/*  272 */         long now = SystemTime.getMonotonousTime();
/*      */         
/*  274 */         while ((it.hasNext()) && (size() > 20))
/*      */         {
/*  276 */           QueueEntry eldest = (QueueEntry)it.next();
/*  277 */           if (now - eldest.addTime <= 120000L) break;
/*  278 */           it.remove();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     private static final class QueueEntry
/*      */     {
/*      */       final PEPeerTransportProtocol transport;
/*      */       
/*      */       public QueueEntry(PEPeerTransportProtocol trans)
/*      */       {
/*  289 */         this.transport = trans;
/*      */       }
/*      */       
/*      */ 
/*  293 */       final long addTime = SystemTime.getMonotonousTime();
/*      */     }
/*      */     
/*      */     protected boolean removeEldestEntry(java.util.Map.Entry eldest)
/*      */     {
/*  298 */       return size() > 100;
/*      */     }
/*      */     
/*      */     public synchronized Object put(HashWrapper key, PEPeerTransportProtocol value) {
/*  302 */       performCleaning();
/*  303 */       return super.put(key, new QueueEntry(value));
/*      */     }
/*      */     
/*      */     public synchronized PEPeerTransportProtocol remove(HashWrapper key) {
/*  307 */       performCleaning();
/*  308 */       QueueEntry entry = (QueueEntry)super.remove(key);
/*  309 */       if (entry != null) {
/*  310 */         return entry.transport;
/*      */       }
/*  312 */       return null;
/*      */     }
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*  231 */     String prop = System.getProperty("show.discard.rate.stats");
/*  232 */     SHOW_DISCARD_RATE_STATS = (prop != null) && (prop.equals("1"));
/*      */     
/*      */ 
/*  235 */     requests_discarded = 0;
/*  236 */     requests_discarded_endgame = 0;
/*  237 */     requests_recovered = 0;
/*  238 */     requests_completed = 0;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  317 */     recentlyDisconnected = new DisconnectedTransportQueue();
/*      */     
/*      */ 
/*      */ 
/*  321 */     rnd = org.gudy.azureus2.core3.util.RandomUtils.SECURE_RANDOM;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  328 */     rnd.setSeed(SystemTime.getHighPrecisionCounter());
/*  329 */     sessionSecret = new byte[20];
/*  330 */     rnd.nextBytes(sessionSecret);
/*      */     
/*  332 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Use Lazy Bitfield", "Peer.Fast.Initial.Unchoke.Enabled", "Bias Upload Enable" }, new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public final void parameterChanged(String ignore)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  343 */         String prop = System.getProperty("azureus.lazy.bitfield");
/*      */         
/*  345 */         PEPeerTransportProtocol.ENABLE_LAZY_BITFIELD = (prop != null) && (prop.equals("1"));
/*      */         
/*  347 */         PEPeerTransportProtocol.ENABLE_LAZY_BITFIELD |= COConfigurationManager.getBooleanParameter("Use Lazy Bitfield");
/*      */         
/*  349 */         PEPeerTransportProtocol.access$002(COConfigurationManager.getBooleanParameter("Peer.Fast.Initial.Unchoke.Enabled"));
/*      */         
/*  351 */         PEPeerTransportProtocol.access$102(COConfigurationManager.getBooleanParameter("Bias Upload Enable"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private static boolean enable_upload_bias;
/*      */   
/*      */   private HashWrapper peerSessionID;
/*      */   
/*      */   private HashWrapper mySessionID;
/*      */   
/*      */   private boolean allowReconnect;
/*      */   
/*      */   private Set<Object> upload_disabled_set;
/*      */   
/*      */   private Set<Object> download_disabled_set;
/*      */   
/*      */   private boolean is_upload_disabled;
/*      */   private boolean is_download_disabled;
/*  371 */   private boolean is_optimistic_unchoke = false;
/*      */   
/*  373 */   private PeerExchangerItem peer_exchange_item = null;
/*  374 */   private boolean peer_exchange_supported = false;
/*      */   
/*      */   protected com.aelitis.azureus.core.peermanager.utils.PeerMessageLimiter message_limiter;
/*      */   
/*      */   private boolean request_hint_supported;
/*      */   
/*      */   private boolean bad_piece_supported;
/*      */   
/*      */   private boolean stats_request_supported;
/*      */   
/*      */   private boolean stats_reply_supported;
/*      */   
/*      */   private boolean az_metadata_supported;
/*      */   
/*      */   private boolean have_aggregation_disabled;
/*      */   
/*      */   private volatile boolean manual_lazy_bitfield_control;
/*      */   
/*      */   private volatile int[] manual_lazy_haves;
/*      */   
/*      */   private final boolean is_metadata_download;
/*      */   
/*      */   private long request_latency;
/*      */   
/*      */ 
/*      */   public PEPeerTransportProtocol(PEPeerControl _manager, String _peer_source, NetworkConnection _connection, Map _initial_user_data)
/*      */   {
/*  401 */     this.manager = _manager;
/*  402 */     this.peer_source = _peer_source;
/*  403 */     this.connection = _connection;
/*  404 */     this.data = _initial_user_data;
/*      */     
/*  406 */     this.incoming = true;
/*      */     
/*  408 */     this.is_metadata_download = this.manager.isMetadataDownload();
/*      */     
/*  410 */     this.diskManager = this.manager.getDiskManager();
/*  411 */     this.piecePicker = this.manager.getPiecePicker();
/*  412 */     this.nbPieces = this.diskManager.getNbPieces();
/*      */     
/*      */ 
/*  415 */     InetSocketAddress notional_address = _connection.getEndpoint().getNotionalAddress();
/*      */     
/*  417 */     this.ip = org.gudy.azureus2.core3.util.AddressUtils.getHostAddress(notional_address);
/*      */     
/*  419 */     this.network = org.gudy.azureus2.core3.util.AENetworkClassifier.categoriseAddress(this.ip);
/*      */     
/*  421 */     this.port = notional_address.getPort();
/*      */     
/*  423 */     this.peer_item_identity = com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory.createPeerItem(this.ip, this.port, PeerItem.convertSourceID(_peer_source), (byte)0, 0, (byte)1, 0);
/*      */     
/*  425 */     this.plugin_connection = new org.gudy.azureus2.pluginsimpl.local.network.ConnectionImpl(this.connection, this.incoming);
/*      */     
/*  427 */     this.peer_stats = this.manager.createPeerStats(this);
/*      */     
/*  429 */     changePeerState(10);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void start()
/*      */   {
/*  438 */     if (this.incoming)
/*      */     {
/*      */ 
/*  441 */       this.connection.connect(3, new com.aelitis.azureus.core.networkmanager.NetworkConnection.ConnectionListener()
/*      */       {
/*      */ 
/*      */         public final int connectStarted(int ct)
/*      */         {
/*      */ 
/*  447 */           PEPeerTransportProtocol.this.connection_state = 1;
/*  448 */           return ct;
/*      */         }
/*      */         
/*      */         public final void connectSuccess(ByteBuffer remaining_initial_data) {
/*  452 */           if (Logger.isEnabled()) {
/*  453 */             Logger.log(new LogEvent(PEPeerTransportProtocol.this, PEPeerTransportProtocol.LOGID, "In: Established incoming connection"));
/*      */           }
/*      */           
/*  456 */           PEPeerTransportProtocol.this.generateSessionId();
/*      */           
/*  458 */           PEPeerTransportProtocol.this.initializeConnection();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  470 */           PEPeerTransportProtocol.this.sendBTHandshake();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public final void connectFailure(Throwable failure_msg)
/*      */         {
/*  477 */           Debug.out("ERROR: incoming connect failure: ", failure_msg);
/*  478 */           PEPeerTransportProtocol.this.closeConnectionInternally("ERROR: incoming connect failure [" + PEPeerTransportProtocol.this + "] : " + failure_msg.getMessage(), true, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public final void exceptionThrown(Throwable error)
/*      */         {
/*  485 */           if (error.getMessage() == null) {
/*  486 */             Debug.out(error);
/*      */           }
/*      */           
/*  489 */           PEPeerTransportProtocol.this.closeConnectionInternally("connection exception: " + error.getMessage(), false, true);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public Object getConnectionProperty(String property_name)
/*      */         {
/*  496 */           return null;
/*      */         }
/*      */         
/*      */ 
/*      */         public String getDescription()
/*      */         {
/*  502 */           return PEPeerTransportProtocol.this.getString();
/*      */         }
/*      */       });
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
/*      */   public PEPeerTransportProtocol(PEPeerControl _manager, String _peer_source, String _ip, int _tcp_port, int _udp_port, boolean _use_tcp, boolean _require_crypto_handshake, byte _crypto_level, Map _initial_user_data)
/*      */   {
/*  526 */     this.manager = _manager;
/*      */     
/*  528 */     this.is_metadata_download = this.manager.isMetadataDownload();
/*      */     
/*  530 */     this.diskManager = this.manager.getDiskManager();
/*  531 */     this.piecePicker = this.manager.getPiecePicker();
/*  532 */     this.nbPieces = this.diskManager.getNbPieces();
/*  533 */     this.lastNeededUndonePieceChange = Long.MIN_VALUE;
/*      */     
/*  535 */     this.peer_source = _peer_source;
/*  536 */     this.ip = _ip;
/*  537 */     this.port = _tcp_port;
/*  538 */     this.tcp_listen_port = _tcp_port;
/*  539 */     this.udp_listen_port = _udp_port;
/*  540 */     this.crypto_level = _crypto_level;
/*  541 */     this.data = _initial_user_data;
/*      */     
/*  543 */     this.network = org.gudy.azureus2.core3.util.AENetworkClassifier.categoriseAddress(this.ip);
/*      */     
/*  545 */     if (this.data != null)
/*      */     {
/*  547 */       Boolean pc = (Boolean)this.data.get(org.gudy.azureus2.plugins.peers.Peer.PR_PRIORITY_CONNECTION);
/*      */       
/*  549 */       if ((pc != null) && (pc.booleanValue()))
/*      */       {
/*  551 */         setPriorityConnection(true);
/*      */       }
/*      */     }
/*      */     
/*  555 */     this.udp_non_data_port = UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber();
/*      */     
/*  557 */     this.peer_item_identity = com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory.createPeerItem(this.ip, this.tcp_listen_port, PeerItem.convertSourceID(_peer_source), (byte)0, _udp_port, this.crypto_level, 0);
/*      */     
/*  559 */     this.incoming = false;
/*      */     
/*  561 */     this.peer_stats = this.manager.createPeerStats(this);
/*      */     
/*  563 */     if ((this.port < 0) || (this.port > 65535)) {
/*  564 */       closeConnectionInternally("given remote port is invalid: " + this.port);
/*  565 */       this.connection = null;
/*  566 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  571 */     boolean use_crypto = (_require_crypto_handshake) || (NetworkManager.getCryptoRequired(this.manager.getAdapter().getCryptoLevel()));
/*      */     
/*      */ 
/*      */ 
/*  575 */     boolean lan_local = isLANLocal();
/*      */     
/*  577 */     boolean public_net = this.peer_item_identity.getNetwork() == "Public";
/*      */     
/*  579 */     if ((lan_local) || (!public_net))
/*      */     {
/*  581 */       use_crypto = false;
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
/*  593 */     ProtocolEndpoint pe2 = null;
/*      */     InetSocketAddress endpoint_address;
/*  595 */     ProtocolEndpoint pe1; if (_use_tcp)
/*      */     {
/*  597 */       boolean utp_available = ProtocolEndpointFactory.isHandlerRegistered(3);
/*      */       
/*  599 */       boolean socks_active = NetworkAdmin.getSingleton().isSocksActive();
/*      */       InetSocketAddress endpoint_address;
/*  601 */       InetSocketAddress endpoint_address; if (public_net)
/*      */       {
/*  603 */         endpoint_address = new InetSocketAddress(this.ip, this.tcp_listen_port);
/*      */       }
/*      */       else
/*      */       {
/*  607 */         endpoint_address = InetSocketAddress.createUnresolved(this.ip, this.tcp_listen_port);
/*      */       }
/*      */       ProtocolEndpoint pe1;
/*  610 */       if ((lan_local) || (!utp_available) || (!public_net))
/*      */       {
/*  612 */         pe1 = ProtocolEndpointFactory.createEndpoint(1, endpoint_address);
/*      */       } else { ProtocolEndpoint pe1;
/*  614 */         if ((org.gudy.azureus2.core3.util.AERunStateHandler.isUDPNetworkOnly()) && (!socks_active))
/*      */         {
/*  616 */           pe1 = ProtocolEndpointFactory.createEndpoint(3, endpoint_address);
/*      */         }
/*      */         else
/*      */         {
/*  620 */           ProtocolEndpoint pe1 = ProtocolEndpointFactory.createEndpoint(1, endpoint_address);
/*      */           
/*  622 */           if (!socks_active)
/*      */           {
/*  624 */             if (org.gudy.azureus2.core3.util.RandomUtils.nextInt(2) == 1)
/*      */             {
/*  626 */               pe2 = ProtocolEndpointFactory.createEndpoint(3, endpoint_address); }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/*      */       InetSocketAddress endpoint_address;
/*  632 */       if (public_net)
/*      */       {
/*  634 */         endpoint_address = new InetSocketAddress(this.ip, this.udp_listen_port);
/*      */       }
/*      */       else
/*      */       {
/*  638 */         endpoint_address = InetSocketAddress.createUnresolved(this.ip, this.udp_listen_port);
/*      */       }
/*      */       
/*  641 */       pe1 = ProtocolEndpointFactory.createEndpoint(2, endpoint_address);
/*      */     }
/*      */     
/*  644 */     ConnectionEndpoint connection_endpoint = new ConnectionEndpoint(endpoint_address);
/*      */     
/*  646 */     connection_endpoint.addProtocol(pe1);
/*      */     
/*  648 */     if (pe2 != null)
/*      */     {
/*  650 */       connection_endpoint.addProtocol(pe2);
/*      */     }
/*      */     
/*  653 */     this.connection = NetworkManager.getSingleton().createConnection(connection_endpoint, new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageEncoder(), new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageDecoder(), use_crypto, !_require_crypto_handshake, this.manager.getSecrets(_crypto_level));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  662 */     this.plugin_connection = new org.gudy.azureus2.pluginsimpl.local.network.ConnectionImpl(this.connection, this.incoming);
/*      */     
/*  664 */     changePeerState(10);
/*      */     
/*  666 */     ByteBuffer initial_outbound_data = null;
/*      */     
/*  668 */     if (use_crypto)
/*      */     {
/*  670 */       BTHandshake handshake = new BTHandshake(this.manager.getHash(), this.manager.getPeerId(), this.manager.getExtendedMessagingMode(), this.other_peer_handshake_version);
/*      */       
/*      */ 
/*      */ 
/*  674 */       if (Logger.isEnabled()) {
/*  675 */         Logger.log(new LogEvent(this, LOGID, "Sending encrypted handshake with reserved bytes: " + org.gudy.azureus2.core3.util.ByteFormatter.nicePrint(handshake.getReserved(), false)));
/*      */       }
/*      */       
/*      */ 
/*  679 */       DirectByteBuffer[] ddbs = handshake.getRawData();
/*      */       
/*  681 */       int handshake_len = 0;
/*      */       
/*  683 */       for (int i = 0; i < ddbs.length; i++)
/*      */       {
/*  685 */         handshake_len += ddbs[i].remaining((byte)9);
/*      */       }
/*      */       
/*  688 */       initial_outbound_data = ByteBuffer.allocate(handshake_len);
/*      */       
/*  690 */       for (int i = 0; i < ddbs.length; i++)
/*      */       {
/*  692 */         DirectByteBuffer ddb = ddbs[i];
/*      */         
/*  694 */         initial_outbound_data.put(ddb.getBuffer((byte)9));
/*      */         
/*  696 */         ddb.returnToPool();
/*      */       }
/*      */       
/*  699 */       initial_outbound_data.flip();
/*      */       
/*  701 */       this.handshake_sent = true;
/*      */     }
/*      */     
/*      */     int priority;
/*      */     int priority;
/*  706 */     if (this.manager.isSeeding())
/*      */     {
/*  708 */       priority = 4;
/*      */     } else { int priority;
/*  710 */       if (this.manager.isRTA()) {
/*      */         int priority;
/*  712 */         if (com.aelitis.azureus.core.peermanager.utils.PeerClassifier.isAzureusIP(this.ip))
/*      */         {
/*  714 */           priority = 0;
/*      */         }
/*      */         else
/*      */         {
/*  718 */           priority = 1; }
/*      */       } else { int priority;
/*  720 */         if (com.aelitis.azureus.core.peermanager.utils.PeerClassifier.isAzureusIP(this.ip))
/*      */         {
/*  722 */           priority = 1;
/*      */         }
/*      */         else
/*      */         {
/*  726 */           priority = 3;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  731 */     if (this.peer_source == "Plugin")
/*      */     {
/*  733 */       if (priority > 2)
/*      */       {
/*  735 */         priority = 2;
/*      */       }
/*      */     }
/*      */     
/*  739 */     this.connection.connect(initial_outbound_data, priority, new com.aelitis.azureus.core.networkmanager.NetworkConnection.ConnectionListener()
/*      */     {
/*      */       private boolean connect_ok;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public final int connectStarted(int default_connect_timeout)
/*      */       {
/*  750 */         PEPeerTransportProtocol.this.connection_state = 1;
/*      */         
/*  752 */         if (default_connect_timeout <= 0)
/*      */         {
/*  754 */           return default_connect_timeout;
/*      */         }
/*      */         
/*  757 */         return PEPeerTransportProtocol.this.manager.getConnectTimeout(default_connect_timeout);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public final void connectSuccess(ByteBuffer remaining_initial_data)
/*      */       {
/*  764 */         this.connect_ok = true;
/*      */         
/*  766 */         if (PEPeerTransportProtocol.this.closing)
/*      */         {
/*  768 */           return;
/*      */         }
/*      */         
/*  771 */         PEPeerTransportProtocol.this.generateSessionId();
/*      */         
/*  773 */         if (Logger.isEnabled()) {
/*  774 */           Logger.log(new LogEvent(PEPeerTransportProtocol.this, PEPeerTransportProtocol.LOGID, "Out: Established outgoing connection"));
/*      */         }
/*      */         
/*  777 */         PEPeerTransportProtocol.this.initializeConnection();
/*      */         
/*  779 */         if ((remaining_initial_data != null) && (remaining_initial_data.remaining() > 0))
/*      */         {
/*      */ 
/*      */ 
/*  783 */           PEPeerTransportProtocol.this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTRawMessage(new DirectByteBuffer(remaining_initial_data)), false);
/*      */         }
/*      */         
/*      */ 
/*  787 */         PEPeerTransportProtocol.this.sendBTHandshake();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public final void connectFailure(Throwable failure_msg)
/*      */       {
/*  794 */         PEPeerTransportProtocol.this.closeConnectionInternally("failed to establish outgoing connection: " + failure_msg.getMessage(), true, true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public final void exceptionThrown(Throwable error)
/*      */       {
/*  801 */         if (error.getMessage() == null) {
/*  802 */           Debug.out("error.getMessage() == null", error);
/*      */         }
/*      */         
/*  805 */         PEPeerTransportProtocol.this.closeConnectionInternally("connection exception: " + error.getMessage(), !this.connect_ok, true);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public Object getConnectionProperty(String property_name)
/*      */       {
/*  812 */         if (property_name == "peer_networks")
/*      */         {
/*  814 */           return PEPeerTransportProtocol.this.manager.getAdapter().getEnabledNetworks();
/*      */         }
/*      */         
/*  817 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */       public String getDescription()
/*      */       {
/*  823 */         return PEPeerTransportProtocol.this.getString();
/*      */       }
/*      */     });
/*      */     
/*  827 */     if (Logger.isEnabled()) {
/*  828 */       Logger.log(new LogEvent(this, LOGID, "Out: Creating outgoing connection"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void initializeConnection()
/*      */   {
/*  836 */     if (this.closing) { return;
/*      */     }
/*  838 */     this.recent_outgoing_requests = new LinkedHashMap(16, 0.75F, true) {
/*      */       public final boolean removeEldestEntry(java.util.Map.Entry eldest) {
/*  840 */         return size() > 16;
/*      */       }
/*  842 */     };
/*  843 */     this.recent_outgoing_requests_mon = new AEMonitor("PEPeerTransportProtocol:ROR");
/*      */     
/*  845 */     this.message_limiter = new com.aelitis.azureus.core.peermanager.utils.PeerMessageLimiter();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  865 */     this.outgoing_have_message_aggregator = new OutgoingBTHaveMessageAggregator(this.connection.getOutgoingMessageQueue(), this.other_peer_bt_have_version, this.other_peer_az_have_version);
/*      */     
/*  867 */     this.connection_established_time = SystemTime.getCurrentTime();
/*      */     
/*  869 */     this.connection_state = 2;
/*  870 */     changePeerState(20);
/*      */     
/*  872 */     registerForMessageHandling();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getPeerSource()
/*      */   {
/*  882 */     return this.peer_source;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void closeConnectionInternally(String reason, boolean connect_failed, boolean network_failure)
/*      */   {
/*  892 */     performClose(reason, connect_failed, false, network_failure);
/*      */   }
/*      */   
/*      */   protected void closeConnectionInternally(String reason) {
/*  896 */     performClose(reason, false, false, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void closeConnection(String reason)
/*      */   {
/*  907 */     performClose(reason, false, true, false);
/*      */   }
/*      */   
/*      */   private void performClose(String reason, boolean connect_failed, boolean externally_closed, boolean network_failure)
/*      */   {
/*      */     try
/*      */     {
/*  914 */       this.closing_mon.enter();
/*      */       
/*  916 */       if (this.closing)
/*      */         return;
/*  918 */       this.closing = true;
/*      */       
/*      */ 
/*  921 */       this.interested_in_other_peer = false;
/*  922 */       this.lastNeededUndonePieceChange = Long.MAX_VALUE;
/*      */       
/*  924 */       if (isSnubbed()) {
/*  925 */         this.manager.decNbPeersSnubbed();
/*      */       }
/*  927 */       if (this.identityAdded) {
/*  928 */         if (this.peer_id != null) {
/*  929 */           PeerIdentityManager.removeIdentity(this.manager.getPeerIdentityDataID(), this.peer_id, getPort());
/*      */         } else
/*  931 */           Debug.out("PeerIdentity added but peer_id == null !!!");
/*  932 */         this.identityAdded = false;
/*      */       }
/*      */       
/*  935 */       changePeerState(40);
/*      */     }
/*      */     finally {
/*  938 */       this.closing_mon.exit();
/*      */     }
/*      */     
/*      */ 
/*  942 */     cancelRequests();
/*      */     
/*  944 */     if (this.outgoing_have_message_aggregator != null) {
/*  945 */       this.outgoing_have_message_aggregator.destroy();
/*      */     }
/*      */     
/*  948 */     if (this.peer_exchange_item != null) {
/*  949 */       this.peer_exchange_item.destroy();
/*      */     }
/*      */     
/*  952 */     if (this.outgoing_piece_message_handler != null) {
/*  953 */       this.outgoing_piece_message_handler.destroy();
/*      */     }
/*      */     
/*  956 */     if (this.connection != null) {
/*  957 */       this.connection.close(reason);
/*      */     }
/*      */     
/*  960 */     if (this.ip_resolver_request != null) {
/*  961 */       this.ip_resolver_request.cancel();
/*      */     }
/*      */     
/*  964 */     removeAvailability();
/*      */     
/*  966 */     changePeerState(50);
/*      */     
/*  968 */     if (Logger.isEnabled()) {
/*  969 */       Logger.log(new LogEvent(this, LOGID, "Peer connection closed: " + reason));
/*      */     }
/*  971 */     if (!externally_closed) {
/*  972 */       this.manager.peerConnectionClosed(this, connect_failed, network_failure);
/*      */     }
/*      */     
/*  975 */     setPriorityConnection(false);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  981 */     this.outgoing_have_message_aggregator = null;
/*  982 */     this.peer_exchange_item = null;
/*  983 */     this.outgoing_piece_message_handler = null;
/*  984 */     this.plugin_connection = null;
/*      */     
/*      */ 
/*  987 */     if ((this.peer_stats.getTotalDataBytesReceived() > 0L) || (this.peer_stats.getTotalDataBytesSent() > 0L) || (SystemTime.getCurrentTime() - this.connection_established_time > 30000L)) {
/*  988 */       recentlyDisconnected.put(this.mySessionID, this);
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isClosed()
/*      */   {
/*  994 */     return this.closing;
/*      */   }
/*      */   
/*      */   public PEPeerTransport reconnect(boolean tryUDP, boolean tryIPv6)
/*      */   {
/*  999 */     boolean use_tcp = (isTCP()) && ((!tryUDP) || (getUDPListenPort() <= 0));
/*      */     
/* 1001 */     if (((use_tcp) && (getTCPListenPort() > 0)) || ((!use_tcp) && (getUDPListenPort() > 0)))
/*      */     {
/* 1003 */       boolean use_crypto = getPeerItemIdentity().getHandshakeType() == 1;
/*      */       
/* 1005 */       PEPeerTransport new_conn = org.gudy.azureus2.core3.peer.impl.PEPeerTransportFactory.createTransport(this.manager, getPeerSource(), (tryIPv6) && (this.alternativeAddress != null) ? this.alternativeAddress.getHostAddress() : getIp(), getTCPListenPort(), getUDPListenPort(), use_tcp, use_crypto, this.crypto_level, null);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1018 */       Logger.log(new LogEvent(new Object[] { this, new_conn }, LOGID, "attempting to reconnect, creating new connection"));
/* 1019 */       if ((new_conn instanceof PEPeerTransportProtocol))
/*      */       {
/* 1021 */         PEPeerTransportProtocol pt = (PEPeerTransportProtocol)new_conn;
/* 1022 */         pt.checkForReconnect(this.mySessionID);
/*      */         
/* 1024 */         pt.alternativeAddress = this.alternativeAddress;
/*      */       }
/*      */       
/*      */ 
/* 1028 */       this.manager.addPeer(new_conn);
/*      */       
/* 1030 */       return new_conn;
/*      */     }
/*      */     
/* 1033 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isSafeForReconnect()
/*      */   {
/* 1041 */     return this.allowReconnect;
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkForReconnect(HashWrapper oldID)
/*      */   {
/* 1047 */     PEPeerTransportProtocol oldTransport = recentlyDisconnected.remove(oldID);
/*      */     
/* 1049 */     if (oldTransport != null)
/*      */     {
/* 1051 */       Logger.log(new LogEvent(this, LOGID, 0, "reassociating stats from " + oldTransport + " with this connection"));
/* 1052 */       this.peerSessionID = oldTransport.peerSessionID;
/* 1053 */       this.peer_stats = oldTransport.peer_stats;
/* 1054 */       this.peer_stats.setPeer(this);
/* 1055 */       setSnubbed(oldTransport.isSnubbed());
/* 1056 */       this.snubbed = oldTransport.snubbed;
/* 1057 */       this.last_good_data_time = oldTransport.last_good_data_time;
/*      */     }
/*      */   }
/*      */   
/*      */   private void generateSessionId()
/*      */   {
/* 1063 */     SHA1Hasher sha1 = new SHA1Hasher();
/* 1064 */     sha1.update(sessionSecret);
/* 1065 */     sha1.update(this.manager.getHash());
/* 1066 */     sha1.update(getIp().getBytes());
/* 1067 */     this.mySessionID = sha1.getHash();
/* 1068 */     checkForReconnect(this.mySessionID);
/*      */   }
/*      */   
/*      */   private void addAvailability()
/*      */   {
/* 1073 */     if ((!this.availabilityAdded) && (!this.closing) && (this.peerHavePieces != null) && (this.current_peer_state == 30))
/*      */     {
/* 1075 */       List peer_listeners_ref = this.peer_listeners_cow;
/* 1076 */       if (peer_listeners_ref != null)
/*      */       {
/* 1078 */         for (int i = 0; i < peer_listeners_ref.size(); i++)
/*      */         {
/* 1080 */           PEPeerListener peerListener = (PEPeerListener)peer_listeners_ref.get(i);
/* 1081 */           peerListener.addAvailability(this, this.peerHavePieces);
/*      */         }
/* 1083 */         this.availabilityAdded = true;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void removeAvailability()
/*      */   {
/* 1090 */     if ((this.availabilityAdded) && (this.peerHavePieces != null))
/*      */     {
/* 1092 */       List peer_listeners_ref = this.peer_listeners_cow;
/* 1093 */       if (peer_listeners_ref != null)
/*      */       {
/* 1095 */         for (int i = 0; i < peer_listeners_ref.size(); i++)
/*      */         {
/* 1097 */           PEPeerListener peerListener = (PEPeerListener)peer_listeners_ref.get(i);
/* 1098 */           peerListener.removeAvailability(this, this.peerHavePieces);
/*      */         }
/*      */       }
/* 1101 */       this.availabilityAdded = false;
/*      */     }
/* 1103 */     this.peerHavePieces = null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void sendBTHandshake()
/*      */   {
/* 1109 */     if (!this.handshake_sent)
/*      */     {
/* 1111 */       int msg_mode = this.manager.getExtendedMessagingMode();
/*      */       
/* 1113 */       Boolean disable_az = (Boolean)this.connection.getEndpoint().getProperty("AEProxyAddressMapper.disable.az.msg");
/*      */       
/* 1115 */       if ((disable_az != null) && (disable_az.booleanValue()))
/*      */       {
/* 1117 */         if (msg_mode == 2)
/*      */         {
/* 1119 */           msg_mode = 1;
/*      */         }
/*      */       }
/*      */       
/* 1123 */       BTHandshake handshake = new BTHandshake(this.manager.getHash(), this.manager.getPeerId(), msg_mode, this.other_peer_handshake_version);
/*      */       
/*      */ 
/* 1126 */       if (Logger.isEnabled()) {
/* 1127 */         Logger.log(new LogEvent(this, LOGID, "Sending handshake with reserved bytes: " + org.gudy.azureus2.core3.util.ByteFormatter.nicePrint(handshake.getReserved(), false)));
/*      */       }
/*      */       
/*      */ 
/* 1131 */       this.connection.getOutgoingMessageQueue().addMessage(handshake, false);
/*      */     }
/*      */   }
/*      */   
/*      */   private void sendLTHandshake() {
/* 1136 */     String client_name = (String)org.gudy.azureus2.pluginsimpl.local.clientid.ClientIDManagerImpl.getSingleton().getProperty(this.manager.getHash(), "Client-Name");
/* 1137 */     int localTcpPort = com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager.getSingleton().getTCPListeningPortNumber();
/* 1138 */     String tcpPortOverride = COConfigurationManager.getStringParameter("TCP.Listen.Port.Override");
/*      */     try
/*      */     {
/* 1141 */       localTcpPort = Integer.parseInt(tcpPortOverride);
/*      */     } catch (NumberFormatException e) {}
/* 1143 */     boolean require_crypto = NetworkManager.getCryptoRequired(this.manager.getAdapter().getCryptoLevel());
/*      */     
/* 1145 */     Map data_dict = new java.util.HashMap();
/*      */     
/* 1147 */     data_dict.put("v", client_name);
/* 1148 */     data_dict.put("p", new Integer(localTcpPort));
/* 1149 */     data_dict.put("e", new Long(require_crypto ? 1L : 0L));
/*      */     
/* 1151 */     boolean upload_only = (this.manager.isSeeding()) && (!ENABLE_LAZY_BITFIELD) && (!this.manual_lazy_bitfield_control) && (!this.manager.isSuperSeedMode());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1157 */     data_dict.put("upload_only", new Long(upload_only ? 1L : 0L));
/*      */     
/*      */     int metainfo_size;
/*      */     int metainfo_size;
/* 1161 */     if (this.manager.isPrivateTorrent())
/*      */     {
/* 1163 */       metainfo_size = 0;
/*      */     }
/*      */     else
/*      */     {
/* 1167 */       metainfo_size = this.is_metadata_download ? 0 : this.manager.getTorrentInfoDictSize();
/*      */     }
/*      */     
/* 1170 */     if (metainfo_size > 0)
/*      */     {
/* 1172 */       data_dict.put("metadata_size", new Integer(metainfo_size));
/*      */     }
/*      */     
/* 1175 */     NetworkAdmin na = NetworkAdmin.getSingleton();
/*      */     
/* 1177 */     if ((this.peer_item_identity.getNetwork() == "Public") && (!na.isSocksActive()))
/*      */     {
/* 1179 */       InetAddress defaultV6 = na.hasIPV6Potential(true) ? na.getDefaultPublicAddressV6() : null;
/*      */       
/* 1181 */       if (defaultV6 != null) {
/* 1182 */         data_dict.put("ipv6", defaultV6.getAddress());
/*      */       }
/*      */     }
/*      */     
/* 1186 */     LTHandshake lt_handshake = new LTHandshake(data_dict, (byte)1);
/*      */     
/* 1188 */     lt_handshake.addDefaultExtensionMappings(true, (this.is_metadata_download) || (metainfo_size > 0), true);
/*      */     
/* 1190 */     this.connection.getOutgoingMessageQueue().addMessage(lt_handshake, false);
/*      */   }
/*      */   
/*      */   private void sendAZHandshake() {
/* 1194 */     Message[] avail_msgs = com.aelitis.azureus.core.peermanager.messaging.MessageManager.getSingleton().getRegisteredMessages();
/* 1195 */     String[] avail_ids = new String[avail_msgs.length];
/* 1196 */     byte[] avail_vers = new byte[avail_msgs.length];
/*      */     
/* 1198 */     for (int i = 0; i < avail_msgs.length; i++)
/*      */     {
/* 1200 */       avail_ids[i] = avail_msgs[i].getID();
/* 1201 */       avail_vers[i] = avail_msgs[i].getVersion();
/*      */     }
/*      */     
/* 1204 */     int local_tcp_port = com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager.getSingleton().getTCPListeningPortNumber();
/* 1205 */     int local_udp_port = UDPNetworkManager.getSingleton().getUDPListeningPortNumber();
/* 1206 */     int local_udp2_port = UDPNetworkManager.getSingleton().getUDPNonDataListeningPortNumber();
/* 1207 */     String tcpPortOverride = COConfigurationManager.getStringParameter("TCP.Listen.Port.Override");
/*      */     
/*      */     try
/*      */     {
/* 1211 */       local_tcp_port = Integer.parseInt(tcpPortOverride);
/*      */     }
/*      */     catch (NumberFormatException e) {}
/*      */     
/* 1215 */     boolean require_crypto = NetworkManager.getCryptoRequired(this.manager.getAdapter().getCryptoLevel());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1222 */     if (this.peerSessionID != null) {
/* 1223 */       Logger.log(new LogEvent(this, LOGID, 0, "notifying peer of reconnect attempt"));
/*      */     }
/* 1225 */     InetAddress defaultV6 = null;
/*      */     
/* 1227 */     NetworkAdmin na = NetworkAdmin.getSingleton();
/*      */     
/* 1229 */     if ((this.peer_item_identity.getNetwork() == "Public") && (!na.isSocksActive()))
/*      */     {
/* 1231 */       defaultV6 = na.hasIPV6Potential(true) ? na.getDefaultPublicAddressV6() : null;
/*      */     }
/*      */     
/* 1234 */     String peer_name = "Vuze";
/*      */     
/* 1236 */     if ((this.peer_id[0] == 45) && (this.peer_id[1] == 65) && (this.peer_id[2] == 90) && (this.peer_id[7] == 45))
/*      */     {
/*      */ 
/*      */       try
/*      */       {
/*      */ 
/* 1242 */         int version = Integer.parseInt(new String(this.peer_id, 3, 4));
/*      */         
/* 1244 */         if (version < 4813)
/*      */         {
/* 1246 */           peer_name = "Azureus";
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {}
/*      */     }
/*      */     
/* 1252 */     AZHandshake az_handshake = new AZHandshake(com.aelitis.azureus.core.peermanager.utils.AZPeerIdentityManager.getAZPeerIdentity(), this.mySessionID, this.peerSessionID, peer_name, "5.7.6.0", local_tcp_port, local_udp_port, local_udp2_port, defaultV6, this.is_metadata_download ? 0 : this.manager.isPrivateTorrent() ? 0 : this.manager.getTorrentInfoDictSize(), avail_ids, avail_vers, require_crypto ? 1 : 0, this.other_peer_handshake_version, (this.manager.isSeeding()) && (!ENABLE_LAZY_BITFIELD) && (!this.manual_lazy_bitfield_control));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1269 */     this.connection.getOutgoingMessageQueue().addMessage(az_handshake, false);
/*      */   }
/*      */   
/* 1272 */   public int getPeerState() { return this.current_peer_state; }
/*      */   
/*      */   public boolean isDownloadPossible()
/*      */   {
/* 1276 */     if ((!this.closing) && (!this.effectively_choked_by_other_peer))
/*      */     {
/* 1278 */       if (this.lastNeededUndonePieceChange < this.piecePicker.getNeededUndonePieceChange())
/*      */       {
/* 1280 */         checkInterested();
/* 1281 */         this.lastNeededUndonePieceChange = this.piecePicker.getNeededUndonePieceChange();
/*      */       }
/* 1283 */       if ((this.interested_in_other_peer) && (this.current_peer_state == 30))
/* 1284 */         return true;
/*      */     }
/* 1286 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneInThousandNotation()
/*      */   {
/* 1292 */     long total_done = getBytesDownloaded();
/*      */     
/* 1294 */     return (int)(total_done * 1000L / this.diskManager.getTotalLength());
/*      */   }
/*      */   
/*      */   public boolean transferAvailable() {
/* 1298 */     return (!this.effectively_choked_by_other_peer) && (this.interested_in_other_peer);
/*      */   }
/*      */   
/*      */ 
/*      */   private void printRequestStats()
/*      */   {
/* 1304 */     if (SHOW_DISCARD_RATE_STATS) {
/* 1305 */       float discard_perc = requests_discarded * 100.0F / ((requests_completed + requests_recovered + requests_discarded) * 1.0F);
/* 1306 */       float discard_perc_end = requests_discarded_endgame * 100.0F / ((requests_completed + requests_recovered + requests_discarded_endgame) * 1.0F);
/* 1307 */       float recover_perc = requests_recovered * 100.0F / ((requests_recovered + requests_discarded) * 1.0F);
/* 1308 */       System.out.println("c=" + requests_completed + " d=" + requests_discarded + " de=" + requests_discarded_endgame + " r=" + requests_recovered + " dp=" + discard_perc + "% dpe=" + discard_perc_end + "% rp=" + recover_perc + "%");
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
/*      */   private void checkSeed()
/*      */   {
/* 1324 */     if ((this.peerHavePieces != null) && (this.nbPieces > 0)) {
/* 1325 */       setSeed(this.peerHavePieces.nbSet == this.nbPieces);
/*      */     } else {
/* 1327 */       setSeed(false);
/*      */     }
/*      */     
/* 1330 */     if ((this.manager.isSeeding()) && (isSeed()))
/*      */     {
/* 1332 */       this.relativeSeeding = ((byte)(this.relativeSeeding | 0x2));
/* 1333 */     } else if ((this.manager.isSeeding()) && ((this.relativeSeeding & 0x1) != 0))
/*      */     {
/* 1335 */       this.relativeSeeding = ((byte)(this.relativeSeeding | 0x2));
/* 1336 */     } else if ((this.peerHavePieces != null) && (this.nbPieces > 0))
/*      */     {
/* 1338 */       int piecesDone = this.manager.getPiecePicker().getNbPiecesDone();
/* 1339 */       DiskManagerPiece[] dmPieces = this.diskManager.getPieces();
/* 1340 */       boolean couldBeSeed = true;
/*      */       
/* 1342 */       if ((!this.manager.isSeeding()) && ((this.relativeSeeding & 0x1) != 0))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1348 */         for (int i = this.peerHavePieces.start; i <= this.peerHavePieces.end; i++)
/*      */         {
/*      */ 
/*      */ 
/* 1352 */           couldBeSeed &= ((this.peerHavePieces.flags[i] == 0) || (dmPieces[i].isDone()) || (!dmPieces[i].isNeeded()));
/*      */           
/* 1354 */           if (!couldBeSeed) {
/*      */             break;
/*      */           }
/*      */           
/*      */         }
/* 1359 */       } else if ((this.manager.isSeeding()) && (piecesDone <= this.peerHavePieces.nbSet))
/*      */       {
/*      */ 
/*      */ 
/* 1363 */         for (int i = this.peerHavePieces.start; i <= this.peerHavePieces.end; i++)
/*      */         {
/*      */ 
/*      */ 
/* 1367 */           couldBeSeed &= ((!dmPieces[i].isDone()) || (this.peerHavePieces.flags[i] != 0));
/*      */           
/* 1369 */           if (!couldBeSeed) {
/*      */             break;
/*      */           }
/*      */           
/*      */         }
/*      */       } else {
/* 1375 */         couldBeSeed = false;
/*      */       }
/*      */       
/* 1378 */       if (couldBeSeed) {
/* 1379 */         this.relativeSeeding = ((byte)(this.relativeSeeding | 0x2));
/*      */       } else {
/* 1381 */         this.relativeSeeding = ((byte)(this.relativeSeeding & 0xFFFFFFFD));
/*      */       }
/*      */     } else {
/* 1384 */       this.relativeSeeding = ((byte)(this.relativeSeeding & 0xFFFFFFFD));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DiskManagerReadRequest request(int pieceNumber, int pieceOffset, int pieceLength, boolean return_duplicates)
/*      */   {
/* 1396 */     DiskManagerReadRequest request = this.manager.createDiskManagerRequest(pieceNumber, pieceOffset, pieceLength);
/*      */     
/* 1398 */     if (this.current_peer_state != 30)
/*      */     {
/* 1400 */       this.manager.requestCanceled(request);
/*      */       
/* 1402 */       return null;
/*      */     }
/*      */     
/* 1405 */     boolean added = false;
/*      */     try
/*      */     {
/* 1408 */       this.requested_mon.enter();
/*      */       
/* 1410 */       if (!this.requested.contains(request))
/*      */       {
/* 1412 */         if (this.requested.size() == 0)
/*      */         {
/* 1414 */           request.setLatencyTest();
/*      */         }
/*      */         
/* 1417 */         this.requested.add(request);
/*      */         
/* 1419 */         added = true;
/*      */       }
/*      */     }
/*      */     finally {
/* 1423 */       this.requested_mon.exit();
/*      */     }
/*      */     
/* 1426 */     if (added)
/*      */     {
/* 1428 */       if (this.is_metadata_download)
/*      */       {
/* 1430 */         if (this.az_metadata_supported)
/*      */         {
/* 1432 */           this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.azureus.AZMetaData(pieceNumber, this.other_peer_request_version), false);
/*      */         }
/*      */         else
/*      */         {
/* 1436 */           this.connection.getOutgoingMessageQueue().addMessage(new UTMetaData(pieceNumber, this.other_peer_request_version), false);
/*      */         }
/*      */       }
/*      */       else {
/* 1440 */         this.connection.getOutgoingMessageQueue().addMessage(new BTRequest(pieceNumber, pieceOffset, pieceLength, this.other_peer_request_version), false);
/*      */       }
/*      */       
/* 1443 */       this._lastPiece = pieceNumber;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 1453 */         this.recent_outgoing_requests_mon.enter();
/*      */         
/* 1455 */         this.recent_outgoing_requests.put(request, null);
/*      */       }
/*      */       finally {
/* 1458 */         this.recent_outgoing_requests_mon.exit();
/*      */       }
/*      */       
/* 1461 */       return request;
/*      */     }
/*      */     
/*      */ 
/* 1465 */     if (return_duplicates)
/*      */     {
/* 1467 */       return request;
/*      */     }
/*      */     
/*      */ 
/* 1471 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getRequestIndex(DiskManagerReadRequest request)
/*      */   {
/*      */     try
/*      */     {
/* 1481 */       this.requested_mon.enter();
/*      */       
/* 1483 */       return this.requested.indexOf(request);
/*      */     }
/*      */     finally
/*      */     {
/* 1487 */       this.requested_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void sendCancel(DiskManagerReadRequest request) {
/* 1492 */     if (this.current_peer_state != 30) return;
/* 1493 */     if (hasBeenRequested(request)) {
/* 1494 */       removeRequest(request);
/* 1495 */       this.connection.getOutgoingMessageQueue().addMessage(new BTCancel(request.getPieceNumber(), request.getOffset(), request.getLength(), this.other_peer_cancel_version), false);
/*      */     }
/*      */   }
/*      */   
/*      */   private void sendHaveNone()
/*      */   {
/* 1501 */     this.connection.getOutgoingMessageQueue().addMessage(new BTHaveNone(this.other_peer_have_none_version), false);
/*      */   }
/*      */   
/*      */   public void sendHave(int pieceNumber) {
/* 1505 */     if ((this.current_peer_state != 30) || (pieceNumber == this.manager.getHiddenPiece())) { return;
/*      */     }
/*      */     
/* 1508 */     boolean force = (!this.other_peer_interested_in_me) && (this.peerHavePieces != null) && (this.peerHavePieces.flags[pieceNumber] == 0);
/*      */     
/* 1510 */     this.outgoing_have_message_aggregator.queueHaveMessage(pieceNumber, (force) || (this.have_aggregation_disabled));
/* 1511 */     checkInterested();
/*      */   }
/*      */   
/*      */   public void sendChoke()
/*      */   {
/* 1516 */     if (this.current_peer_state != 30) { return;
/*      */     }
/*      */     
/*      */ 
/* 1520 */     this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTChoke(this.other_peer_choke_version), false);
/* 1521 */     this.choking_other_peer = true;
/* 1522 */     this.is_optimistic_unchoke = false;
/*      */     
/* 1524 */     destroyPieceMessageHandler();
/*      */   }
/*      */   
/*      */   public void sendUnChoke()
/*      */   {
/* 1529 */     if (this.current_peer_state != 30) { return;
/*      */     }
/*      */     
/*      */ 
/* 1533 */     createPieceMessageHandler();
/*      */     
/* 1535 */     this.choking_other_peer = false;
/*      */     
/*      */ 
/* 1538 */     this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUnchoke(this.other_peer_unchoke_version), false);
/*      */   }
/*      */   
/*      */ 
/*      */   private void createPieceMessageHandler()
/*      */   {
/* 1544 */     if (this.outgoing_piece_message_handler == null)
/*      */     {
/* 1546 */       this.outgoing_piece_message_handler = new OutgoingBTPieceMessageHandler(this, this.connection.getOutgoingMessageQueue(), new com.aelitis.azureus.core.peermanager.utils.OutgoingBTPieceMessageHandlerAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void diskRequestCompleted(long bytes) {
/* 1555 */           PEPeerTransportProtocol.this.peer_stats.diskReadComplete(bytes); } }, this.other_peer_piece_version);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void destroyPieceMessageHandler()
/*      */   {
/* 1565 */     if (this.outgoing_piece_message_handler != null) {
/* 1566 */       this.outgoing_piece_message_handler.removeAllPieceRequests();
/* 1567 */       this.outgoing_piece_message_handler.destroy();
/* 1568 */       this.outgoing_piece_message_handler = null;
/*      */     }
/*      */   }
/*      */   
/*      */   private void sendKeepAlive() {
/* 1573 */     if (this.current_peer_state != 30) { return;
/*      */     }
/* 1575 */     if (this.outgoing_have_message_aggregator.hasPending()) {
/* 1576 */       this.outgoing_have_message_aggregator.forceSendOfPending();
/*      */     }
/*      */     else {
/* 1579 */       this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTKeepAlive(this.other_peer_keep_alive_version), false);
/*      */     }
/*      */   }
/*      */   
/*      */   private void sendMainlineDHTPort() {
/* 1584 */     if (!this.ml_dht_enabled) return;
/* 1585 */     org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider provider = getDHTProvider();
/* 1586 */     if (provider == null) return;
/* 1587 */     Message message = new BTDHTPort(provider.getDHTPort());
/* 1588 */     this.connection.getOutgoingMessageQueue().addMessage(message, false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void checkInterested()
/*      */   {
/* 1600 */     if ((this.closing) || (this.peerHavePieces == null) || (this.peerHavePieces.nbSet == 0))
/*      */     {
/* 1602 */       return;
/*      */     }
/*      */     
/* 1605 */     boolean is_interesting = false;
/*      */     
/* 1607 */     if (!this.is_download_disabled)
/*      */     {
/* 1609 */       if (this.piecePicker.hasDownloadablePiece())
/*      */       {
/*      */ 
/*      */ 
/* 1613 */         if ((!isSeed()) && (!isRelativeSeed()))
/*      */         {
/*      */ 
/*      */ 
/* 1617 */           for (int i = this.peerHavePieces.start; i <= this.peerHavePieces.end; i++)
/*      */           {
/* 1619 */             if ((this.peerHavePieces.flags[i] != 0) && (this.diskManager.isInteresting(i)))
/*      */             {
/* 1621 */               is_interesting = true;
/*      */               
/* 1623 */               break;
/*      */             }
/*      */             
/*      */           }
/*      */         } else {
/* 1628 */           is_interesting = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1633 */     if ((is_interesting) && (!this.interested_in_other_peer))
/*      */     {
/* 1635 */       this.connection.getOutgoingMessageQueue().addMessage(new BTInterested(this.other_peer_interested_version), false);
/*      */     }
/* 1637 */     else if ((!is_interesting) && (this.interested_in_other_peer))
/*      */     {
/* 1639 */       this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUninterested(this.other_peer_uninterested_version), false);
/*      */     }
/*      */     
/* 1642 */     this.interested_in_other_peer = ((is_interesting) || (this.is_metadata_download));
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
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   private void sendBitField()
/*      */   {
/* 1672 */     if (this.closing) {
/* 1673 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1678 */     if (this.manager.isSuperSeedMode())
/*      */     {
/*      */ 
/*      */ 
/* 1682 */       sendHaveNone();
/*      */       
/* 1684 */       return;
/*      */     }
/*      */     
/* 1687 */     if (this.is_metadata_download) {
/* 1688 */       return;
/*      */     }
/*      */     
/*      */ 
/* 1692 */     DirectByteBuffer buffer = org.gudy.azureus2.core3.util.DirectByteBufferPool.getBuffer((byte)12, (this.nbPieces + 7) / 8);
/* 1693 */     DiskManagerPiece[] pieces = this.diskManager.getPieces();
/*      */     
/*      */ 
/* 1696 */     int num_pieces = pieces.length;
/*      */     
/* 1698 */     HashSet lazies = null;
/* 1699 */     int[] lazy_haves = null;
/*      */     
/* 1701 */     if ((ENABLE_LAZY_BITFIELD) || (this.manual_lazy_bitfield_control))
/*      */     {
/* 1703 */       int bits_in_first_byte = Math.min(num_pieces, 8);
/* 1704 */       int last_byte_start_bit = num_pieces / 8 * 8;
/* 1705 */       int bits_in_last_byte = num_pieces - last_byte_start_bit;
/* 1706 */       if (bits_in_last_byte == 0)
/*      */       {
/* 1708 */         bits_in_last_byte = 8;
/* 1709 */         last_byte_start_bit -= 8;
/*      */       }
/* 1711 */       lazies = new HashSet();
/*      */       
/* 1713 */       int first_byte_entry = rnd.nextInt(bits_in_first_byte);
/* 1714 */       if (pieces[first_byte_entry].isDone())
/*      */       {
/* 1716 */         lazies.add(new MutableInteger(first_byte_entry));
/*      */       }
/*      */       
/* 1719 */       int last_byte_entry = last_byte_start_bit + rnd.nextInt(bits_in_last_byte);
/* 1720 */       if (pieces[last_byte_entry].isDone())
/*      */       {
/* 1722 */         lazies.add(new MutableInteger(last_byte_entry));
/*      */       }
/*      */       
/* 1725 */       int other_lazies = rnd.nextInt(16) + 4;
/* 1726 */       for (int i = 0; i < other_lazies; i++)
/*      */       {
/* 1728 */         int random_entry = rnd.nextInt(num_pieces);
/* 1729 */         if (pieces[random_entry].isDone())
/*      */         {
/* 1731 */           lazies.add(new MutableInteger(random_entry));
/*      */         }
/*      */       }
/* 1734 */       int num_lazy = lazies.size();
/* 1735 */       if (num_lazy == 0)
/*      */       {
/* 1737 */         lazies = null;
/*      */       }
/*      */       else {
/* 1740 */         lazy_haves = new int[num_lazy];
/* 1741 */         Iterator it = lazies.iterator();
/* 1742 */         for (int i = 0; i < num_lazy; i++)
/*      */         {
/* 1744 */           int lazy_have = ((MutableInteger)it.next()).getValue();
/* 1745 */           lazy_haves[i] = lazy_have;
/*      */         }
/* 1747 */         if (num_lazy > 1)
/*      */         {
/* 1749 */           for (int i = 0; i < num_lazy; i++)
/*      */           {
/* 1751 */             int swap = rnd.nextInt(num_lazy);
/* 1752 */             if (swap != i)
/*      */             {
/* 1754 */               int temp = lazy_haves[swap];
/* 1755 */               lazy_haves[swap] = lazy_haves[i];
/* 1756 */               lazy_haves[i] = temp;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1763 */     int bToSend = 0;
/* 1764 */     int i = 0;
/*      */     
/* 1766 */     MutableInteger mi = new MutableInteger(0);
/*      */     
/* 1768 */     int hidden_piece = this.manager.getHiddenPiece();
/* 1770 */     for (; 
/* 1770 */         i < num_pieces; i++)
/*      */     {
/* 1772 */       if (i % 8 == 0) {
/* 1773 */         bToSend = 0;
/*      */       }
/*      */       
/* 1776 */       bToSend <<= 1;
/*      */       
/* 1778 */       if ((pieces[i].isDone()) && (i != hidden_piece))
/*      */       {
/* 1780 */         if (lazies != null)
/*      */         {
/* 1782 */           mi.setValue(i);
/*      */           
/* 1784 */           if (!lazies.contains(mi))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1789 */             bToSend++;
/*      */           }
/*      */         } else {
/* 1792 */           bToSend++;
/*      */         }
/*      */       }
/*      */       
/* 1796 */       if (i % 8 == 7) {
/* 1797 */         buffer.put((byte)6, (byte)bToSend);
/*      */       }
/*      */     }
/*      */     
/* 1801 */     if (i % 8 != 0)
/*      */     {
/* 1803 */       bToSend <<= 8 - i % 8;
/* 1804 */       buffer.put((byte)6, (byte)bToSend);
/*      */     }
/*      */     
/* 1807 */     buffer.flip((byte)6);
/*      */     
/* 1809 */     this.connection.getOutgoingMessageQueue().addMessage(new BTBitfield(buffer, this.other_peer_bitfield_version), false);
/*      */     
/* 1811 */     if (lazy_haves != null)
/*      */     {
/* 1813 */       if (this.manual_lazy_bitfield_control)
/*      */       {
/* 1815 */         this.manual_lazy_haves = lazy_haves;
/*      */       }
/*      */       else
/*      */       {
/* 1819 */         sendLazyHaves(lazy_haves, false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendLazyHaves(final int[] lazy_haves, boolean immediate)
/*      */   {
/* 1829 */     if (immediate)
/*      */     {
/* 1831 */       if (this.current_peer_state == 30)
/*      */       {
/* 1833 */         for (int lazy_have : lazy_haves)
/*      */         {
/* 1835 */           this.connection.getOutgoingMessageQueue().addMessage(new BTHave(lazy_have, this.other_peer_bt_have_version), false);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/* 1840 */       org.gudy.azureus2.core3.util.SimpleTimer.addEvent("LazyHaveSender", SystemTime.getCurrentTime() + 1000L + rnd.nextInt(2000), new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1845 */         int next_have = 0;
/*      */         
/*      */         public void perform(org.gudy.azureus2.core3.util.TimerEvent event)
/*      */         {
/* 1849 */           if (PEPeerTransportProtocol.this.current_peer_state == 30)
/*      */           {
/* 1851 */             int lazy_have = lazy_haves[(this.next_have++)];
/*      */             
/* 1853 */             PEPeerTransportProtocol.this.connection.getOutgoingMessageQueue().addMessage(new BTHave(lazy_have, PEPeerTransportProtocol.this.other_peer_bt_have_version), false);
/*      */             
/* 1855 */             if ((this.next_have < lazy_haves.length) && (PEPeerTransportProtocol.this.current_peer_state == 30))
/*      */             {
/* 1857 */               org.gudy.azureus2.core3.util.SimpleTimer.addEvent("LazyHaveSender", SystemTime.getCurrentTime() + PEPeerTransportProtocol.rnd.nextInt(2000), this);
/*      */             }
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/* 1866 */   public byte[] getId() { return this.peer_id; }
/* 1867 */   public String getIp() { return this.ip; }
/* 1868 */   public InetAddress getAlternativeIPv6() { return this.alternativeAddress; }
/* 1869 */   public int getPort() { return this.port; }
/*      */   
/* 1871 */   public int getTCPListenPort() { return this.tcp_listen_port; }
/* 1872 */   public int getUDPListenPort() { return this.udp_listen_port; }
/* 1873 */   public int getUDPNonDataListenPort() { return this.udp_non_data_port; }
/*      */   
/*      */ 
/* 1876 */   public String getClient() { return this.client; }
/*      */   
/* 1878 */   public boolean isIncoming() { return this.incoming; }
/*      */   
/*      */ 
/* 1881 */   public boolean isOptimisticUnchoke() { return (this.is_optimistic_unchoke) && (!isChokedByMe()); }
/* 1882 */   public void setOptimisticUnchoke(boolean is_optimistic) { this.is_optimistic_unchoke = is_optimistic; }
/*      */   
/*      */ 
/* 1885 */   public PEPeerControl getControl() { return this.manager; }
/* 1886 */   public org.gudy.azureus2.core3.peer.PEPeerManager getManager() { return this.manager; }
/* 1887 */   public PEPeerStats getStats() { return this.peer_stats; }
/*      */   
/*      */ 
/*      */   public int[] getPriorityOffsets()
/*      */   {
/* 1892 */     return this.piece_priority_offsets;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean requestAllocationStarts(int[] base_priorities)
/*      */   {
/* 1899 */     return false;
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
/*      */   public BitFlags getAvailable()
/*      */   {
/* 1913 */     return this.peerHavePieces;
/*      */   }
/*      */   
/*      */   public boolean isPieceAvailable(int pieceNumber) {
/* 1917 */     if (this.peerHavePieces != null)
/* 1918 */       return this.peerHavePieces.flags[pieceNumber];
/* 1919 */     return false;
/*      */   }
/*      */   
/* 1922 */   public boolean isChokingMe() { return this.effectively_choked_by_other_peer; }
/* 1923 */   public boolean isUnchokeOverride() { return (this.really_choked_by_other_peer) && (!this.effectively_choked_by_other_peer); }
/* 1924 */   public boolean isChokedByMe() { return this.choking_other_peer; }
/*      */   
/*      */   public boolean isInteresting()
/*      */   {
/* 1928 */     return this.interested_in_other_peer;
/*      */   }
/*      */   
/*      */ 
/* 1932 */   public boolean isInterested() { return this.other_peer_interested_in_me; }
/* 1933 */   public boolean isSeed() { return this.seeding; }
/* 1934 */   public boolean isRelativeSeed() { return (this.relativeSeeding & 0x2) != 0; }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setSeed(boolean s)
/*      */   {
/* 1940 */     if (this.seeding != s)
/*      */     {
/* 1942 */       this.seeding = s;
/*      */       
/* 1944 */       PeerExchangerItem pex_item = this.peer_exchange_item;
/*      */       
/* 1946 */       if ((pex_item != null) && (s))
/*      */       {
/* 1948 */         pex_item.seedStatusChanged(); }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isSnubbed() {
/* 1953 */     return this.snubbed != 0L;
/*      */   }
/*      */   
/*      */   public long getSnubbedTime() {
/* 1957 */     if (this.snubbed == 0L)
/* 1958 */       return 0L;
/* 1959 */     long now = SystemTime.getCurrentTime();
/* 1960 */     if (now < this.snubbed)
/* 1961 */       this.snubbed = (now - 26L);
/* 1962 */     return now - this.snubbed;
/*      */   }
/*      */   
/*      */   public void setSnubbed(boolean b)
/*      */   {
/* 1967 */     if (!this.closing)
/*      */     {
/* 1969 */       long now = SystemTime.getCurrentTime();
/* 1970 */       if (!b)
/*      */       {
/* 1972 */         if (this.snubbed != 0L)
/*      */         {
/* 1974 */           this.snubbed = 0L;
/* 1975 */           this.manager.decNbPeersSnubbed();
/*      */         }
/* 1977 */       } else if (this.snubbed == 0L)
/*      */       {
/* 1979 */         this.snubbed = now;
/* 1980 */         this.manager.incNbPeersSnubbed();
/*      */       }
/*      */     } }
/*      */   
/* 1984 */   public void setUploadHint(int spreadTime) { this.spreadTimeHint = spreadTime; }
/* 1985 */   public int getUploadHint() { return this.spreadTimeHint; }
/* 1986 */   public void setUniqueAnnounce(int _uniquePiece) { this.uniquePiece = _uniquePiece; }
/* 1987 */   public int getUniqueAnnounce() { return this.uniquePiece; }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object getData(String key)
/*      */   {
/* 1993 */     return getUserData(key);
/*      */   }
/*      */   
/*      */ 
/*      */   public void setData(String key, Object value)
/*      */   {
/* 1999 */     setUserData(key, value);
/*      */   }
/*      */   
/*      */   public Object getUserData(Object key)
/*      */   {
/*      */     try {
/* 2005 */       this.general_mon.enter();
/*      */       Object localObject1;
/* 2007 */       if (this.data == null) return null;
/* 2008 */       return this.data.get(key);
/*      */     }
/*      */     finally {
/* 2011 */       this.general_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public void setUserData(Object key, Object value)
/*      */   {
/*      */     try {
/* 2018 */       this.general_mon.enter();
/*      */       
/* 2020 */       if (this.data == null) {
/* 2021 */         this.data = new org.gudy.azureus2.core3.util.LightHashMap();
/*      */       }
/* 2023 */       if (value == null) {
/* 2024 */         if (this.data.containsKey(key)) {
/* 2025 */           this.data.remove(key);
/* 2026 */           if (this.data.size() == 0) {
/* 2027 */             this.data = null;
/*      */           }
/*      */         }
/*      */       } else {
/* 2031 */         this.data.put(key, value);
/*      */       }
/*      */     }
/*      */     finally {
/* 2035 */       this.general_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getIPHostName()
/*      */   {
/* 2043 */     if (this.ip_resolved == null)
/*      */     {
/* 2045 */       this.ip_resolved = this.ip;
/*      */       
/* 2047 */       this.ip_resolver_request = org.gudy.azureus2.core3.util.IPToHostNameResolver.addResolverRequest(this.ip_resolved, new org.gudy.azureus2.core3.util.IPToHostNameResolverListener()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public final void IPResolutionComplete(String res, boolean ok)
/*      */         {
/*      */ 
/*      */ 
/* 2056 */           PEPeerTransportProtocol.this.ip_resolved = res;
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 2061 */     return this.ip_resolved;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void cancelRequests()
/*      */   {
/* 2068 */     if (!this.closing) {
/* 2069 */       Message[] type = { new BTRequest(-1, -1, -1, this.other_peer_request_version) };
/* 2070 */       this.connection.getOutgoingMessageQueue().removeMessagesOfType(type, false);
/*      */     }
/* 2072 */     if ((this.requested != null) && (this.requested.size() > 0)) {
/*      */       try {
/* 2074 */         this.requested_mon.enter();
/*      */         
/* 2076 */         if (!this.closing)
/*      */         {
/*      */ 
/* 2079 */           long timeSinceGoodData = getTimeSinceGoodDataReceived();
/* 2080 */           if ((timeSinceGoodData == -1L) || (timeSinceGoodData > 60000L))
/* 2081 */             setSnubbed(true);
/*      */         }
/* 2083 */         for (int i = this.requested.size() - 1; i >= 0; i--) {
/* 2084 */           DiskManagerReadRequest request = (DiskManagerReadRequest)this.requested.remove(i);
/* 2085 */           this.manager.requestCanceled(request);
/*      */         }
/*      */       }
/*      */       finally {
/* 2089 */         this.requested_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxNbRequests()
/*      */   {
/* 2097 */     return -1;
/*      */   }
/*      */   
/*      */   public int getNbRequests()
/*      */   {
/* 2102 */     return this.requested.size();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public List getExpiredRequests()
/*      */   {
/* 2112 */     List result = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 2119 */       for (int i = this.requested.size() - 1; i >= 0; i--)
/*      */       {
/* 2121 */         DiskManagerReadRequest request = (DiskManagerReadRequest)this.requested.get(i);
/*      */         
/* 2123 */         if (request.isExpired())
/*      */         {
/* 2125 */           if (result == null)
/*      */           {
/* 2127 */             result = new ArrayList();
/*      */           }
/*      */           
/* 2130 */           result.add(request);
/*      */         }
/*      */       }
/*      */       
/* 2134 */       return result;
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/* 2138 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private DiskManagerReadRequest lookupRequest(int piece_number, int piece_offset, int length)
/*      */   {
/*      */     try
/*      */     {
/* 2149 */       this.requested_mon.enter();
/*      */       
/* 2151 */       for (DiskManagerReadRequest r : this.requested)
/*      */       {
/* 2153 */         if ((r.getPieceNumber() == piece_number) && (r.getOffset() == piece_offset) && (r.getLength() == length))
/*      */         {
/*      */ 
/*      */ 
/* 2157 */           return r;
/*      */         }
/*      */       }
/*      */       
/* 2161 */       return null;
/*      */     }
/*      */     finally
/*      */     {
/* 2165 */       this.requested_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean hasBeenRequested(DiskManagerReadRequest request) {
/* 2170 */     try { this.requested_mon.enter();
/*      */       
/* 2172 */       return this.requested.contains(request);
/*      */     } finally {
/* 2174 */       this.requested_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void removeRequest(DiskManagerReadRequest request)
/*      */   {
/*      */     try
/*      */     {
/* 2182 */       this.requested_mon.enter();
/*      */       
/* 2184 */       this.requested.remove(request);
/*      */     }
/*      */     finally {
/* 2187 */       this.requested_mon.exit();
/*      */     }
/* 2189 */     BTRequest msg = new BTRequest(request.getPieceNumber(), request.getOffset(), request.getLength(), this.other_peer_request_version);
/* 2190 */     this.connection.getOutgoingMessageQueue().removeMessage(msg, false);
/* 2191 */     msg.destroy();
/*      */   }
/*      */   
/*      */   private void resetRequestsTime(long now)
/*      */   {
/*      */     try
/*      */     {
/* 2198 */       this.requested_mon.enter();
/*      */       
/* 2200 */       int requestedSize = this.requested.size();
/* 2201 */       for (int i = 0; i < requestedSize; i++)
/*      */       {
/* 2203 */         DiskManagerReadRequest request = (DiskManagerReadRequest)this.requested.get(i);
/* 2204 */         if (request != null) {
/* 2205 */           request.resetTime(now);
/*      */         }
/*      */       }
/*      */     } finally {
/* 2209 */       this.requested_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2219 */     return (isIncoming() ? "R: " : "L: ") + this.ip + ":" + this.port + (isTCP() ? " [" : "(UDP) [") + this.client + "]";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 2225 */     return toString();
/*      */   }
/*      */   
/*      */   public void doKeepAliveCheck()
/*      */   {
/* 2230 */     long now = SystemTime.getCurrentTime();
/* 2231 */     long wait_time = now - this.last_message_sent_time;
/*      */     
/* 2233 */     if ((this.last_message_sent_time == 0L) || (wait_time < 0L)) {
/* 2234 */       this.last_message_sent_time = now;
/* 2235 */       return;
/*      */     }
/*      */     
/* 2238 */     if (wait_time > 120000L) {
/* 2239 */       sendKeepAlive();
/* 2240 */       this.last_message_sent_time = now;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void updateAutoUploadPriority(Object key, boolean inc)
/*      */   {
/*      */     try
/*      */     {
/* 2251 */       this.general_mon.enter();
/*      */       
/* 2253 */       boolean key_exists = getUserData(key) != null;
/*      */       
/* 2255 */       if ((inc) && (!key_exists))
/*      */       {
/* 2257 */         this.upload_priority_auto += 1;
/*      */         
/* 2259 */         setUserData(key, "");
/*      */       }
/* 2261 */       else if ((!inc) && (key_exists))
/*      */       {
/* 2263 */         this.upload_priority_auto -= 1;
/*      */         
/* 2265 */         setUserData(key, null);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 2270 */       this.general_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean doTimeoutChecks()
/*      */   {
/* 2279 */     if (this.connection != null)
/*      */     {
/* 2281 */       this.connection.getOutgoingMessageQueue().setPriorityBoost((this.upload_priority_auto > 0) || (this.manager.getUploadPriority() > 0) || ((enable_upload_bias) && (!this.manager.isSeeding())));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2287 */     if (this.fast_extension_enabled)
/*      */     {
/* 2289 */       checkAllowedFast();
/*      */     }
/*      */     
/* 2292 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 2294 */     if (this.connection_state == 4) {
/* 2295 */       if (this.last_message_received_time > now)
/* 2296 */         this.last_message_received_time = now;
/* 2297 */       if (this.last_data_message_received_time > now)
/* 2298 */         this.last_data_message_received_time = now;
/* 2299 */       if ((now - this.last_message_received_time > 300000L) && (now - this.last_data_message_received_time > 300000L))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2304 */         closeConnectionInternally("timed out while waiting for messages", false, true);
/* 2305 */         return true;
/*      */       }
/*      */       
/*      */     }
/* 2309 */     else if (this.connection_state == 2) {
/* 2310 */       if (this.connection_established_time > now) {
/* 2311 */         this.connection_established_time = now;
/* 2312 */       } else if (now - this.connection_established_time > 180000L) {
/* 2313 */         closeConnectionInternally("timed out while waiting for handshake");
/* 2314 */         return true;
/*      */       }
/*      */     }
/*      */     
/* 2318 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void doPerformanceTuningCheck()
/*      */   {
/* 2324 */     Transport transport = this.connection.getTransport();
/*      */     
/* 2326 */     if ((transport != null) && (this.peer_stats != null) && (this.outgoing_piece_message_handler != null))
/*      */     {
/*      */ 
/* 2329 */       long send_rate = this.peer_stats.getDataSendRate() + this.peer_stats.getProtocolSendRate();
/*      */       
/* 2331 */       if (send_rate >= 3125000L) {
/* 2332 */         transport.setTransportMode(2);
/* 2333 */         this.outgoing_piece_message_handler.setRequestReadAhead(256);
/*      */       }
/* 2335 */       else if (send_rate >= 1250000L) {
/* 2336 */         transport.setTransportMode(2);
/* 2337 */         this.outgoing_piece_message_handler.setRequestReadAhead(128);
/*      */       }
/* 2339 */       else if (send_rate >= 125000L) {
/* 2340 */         if (transport.getTransportMode() < 1) {
/* 2341 */           transport.setTransportMode(1);
/*      */         }
/* 2343 */         this.outgoing_piece_message_handler.setRequestReadAhead(32);
/*      */       }
/* 2345 */       else if (send_rate >= 62500L) {
/* 2346 */         this.outgoing_piece_message_handler.setRequestReadAhead(16);
/*      */       }
/* 2348 */       else if (send_rate >= 31250L) {
/* 2349 */         this.outgoing_piece_message_handler.setRequestReadAhead(8);
/*      */       }
/* 2351 */       else if (send_rate >= 12500L) {
/* 2352 */         this.outgoing_piece_message_handler.setRequestReadAhead(4);
/*      */       }
/*      */       else {
/* 2355 */         this.outgoing_piece_message_handler.setRequestReadAhead(2);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2360 */       long receive_rate = this.peer_stats.getDataReceiveRate() + this.peer_stats.getProtocolReceiveRate();
/*      */       
/* 2362 */       if (receive_rate >= 1250000L) {
/* 2363 */         transport.setTransportMode(2);
/*      */       }
/* 2365 */       else if ((receive_rate >= 125000L) && 
/* 2366 */         (transport.getTransportMode() < 1)) {
/* 2367 */         transport.setTransportMode(1);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getConnectionState()
/*      */   {
/* 2377 */     return this.connection_state;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTimeSinceLastDataMessageReceived()
/*      */   {
/* 2383 */     if (this.last_data_message_received_time == -1L) {
/* 2384 */       return -1L;
/*      */     }
/*      */     
/* 2387 */     long now = SystemTime.getCurrentTime();
/*      */     
/* 2389 */     if (this.last_data_message_received_time > now)
/* 2390 */       this.last_data_message_received_time = now;
/* 2391 */     return now - this.last_data_message_received_time;
/*      */   }
/*      */   
/*      */   public long getTimeSinceGoodDataReceived()
/*      */   {
/* 2396 */     if (this.last_good_data_time == -1L)
/* 2397 */       return -1L;
/* 2398 */     long now = SystemTime.getCurrentTime();
/* 2399 */     if (this.last_good_data_time > now)
/* 2400 */       this.last_good_data_time = now;
/* 2401 */     return now - this.last_good_data_time;
/*      */   }
/*      */   
/*      */   public long getTimeSinceLastDataMessageSent()
/*      */   {
/* 2406 */     if (this.last_data_message_sent_time == -1L) {
/* 2407 */       return -1L;
/*      */     }
/* 2409 */     long now = SystemTime.getCurrentTime();
/* 2410 */     if (this.last_data_message_sent_time > now)
/* 2411 */       this.last_data_message_sent_time = now;
/* 2412 */     return now - this.last_data_message_sent_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getTimeSinceConnectionEstablished()
/*      */   {
/* 2420 */     if (this.connection_established_time == 0L) {
/* 2421 */       return 0L;
/*      */     }
/* 2423 */     long now = SystemTime.getCurrentTime();
/* 2424 */     if (this.connection_established_time > now)
/* 2425 */       this.connection_established_time = now;
/* 2426 */     return now - this.connection_established_time;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getConsecutiveNoRequestCount()
/*      */   {
/* 2432 */     return this.consecutive_no_request_count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setConsecutiveNoRequestCount(int num)
/*      */   {
/* 2439 */     this.consecutive_no_request_count = num;
/*      */   }
/*      */   
/*      */   protected void decodeBTHandshake(BTHandshake handshake)
/*      */   {
/* 2444 */     if (Logger.isEnabled()) {
/* 2445 */       Logger.log(new LogEvent(this, LOGID, "Received handshake with reserved bytes: " + org.gudy.azureus2.core3.util.ByteFormatter.nicePrint(handshake.getReserved(), false)));
/*      */     }
/*      */     
/*      */ 
/* 2449 */     org.gudy.azureus2.core3.peer.util.PeerIdentityDataID my_peer_data_id = this.manager.getPeerIdentityDataID();
/*      */     
/* 2451 */     if (getConnectionState() == 4)
/*      */     {
/* 2453 */       handshake.destroy();
/* 2454 */       closeConnectionInternally("peer sent another handshake after the initial connect");
/*      */     }
/*      */     
/* 2457 */     if (!java.util.Arrays.equals(this.manager.getHash(), handshake.getDataHash())) {
/* 2458 */       closeConnectionInternally("handshake has wrong infohash");
/* 2459 */       handshake.destroy();
/* 2460 */       return;
/*      */     }
/*      */     
/* 2463 */     this.peer_id = handshake.getPeerId();
/*      */     
/*      */ 
/* 2466 */     this.client_peer_id = (this.client = StringInterner.intern(com.aelitis.azureus.core.peermanager.utils.PeerClassifier.getClientDescription(this.peer_id)));
/*      */     
/*      */ 
/* 2469 */     if (!com.aelitis.azureus.core.peermanager.utils.PeerClassifier.isClientTypeAllowed(this.client)) {
/* 2470 */       closeConnectionInternally(this.client + " client type not allowed to connect, banned");
/* 2471 */       handshake.destroy();
/* 2472 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2476 */     if (java.util.Arrays.equals(this.manager.getPeerId(), this.peer_id)) {
/* 2477 */       this.manager.peerVerifiedAsSelf(this);
/* 2478 */       closeConnectionInternally("given peer id matches myself");
/* 2479 */       handshake.destroy();
/* 2480 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2484 */     boolean sameIdentity = PeerIdentityManager.containsIdentity(my_peer_data_id, this.peer_id, getPort());
/* 2485 */     boolean sameIP = false;
/*      */     
/*      */ 
/*      */ 
/* 2489 */     boolean same_allowed = (COConfigurationManager.getBooleanParameter("Allow Same IP Peers")) || (this.ip.equals("127.0.0.1"));
/* 2490 */     if ((!same_allowed) && 
/* 2491 */       (PeerIdentityManager.containsIPAddress(my_peer_data_id, this.ip))) {
/* 2492 */       sameIP = true;
/*      */     }
/*      */     
/*      */ 
/* 2496 */     if (sameIdentity) {
/* 2497 */       boolean close = true;
/*      */       
/* 2499 */       if (this.connection.isLANLocal())
/*      */       {
/* 2501 */         PEPeerTransport existing = this.manager.getTransportFromIdentity(this.peer_id);
/*      */         
/* 2503 */         if (existing != null)
/*      */         {
/* 2505 */           String existing_ip = existing.getIp();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2512 */           if ((!existing.isLANLocal()) || ((existing_ip.endsWith(".1")) && (!existing_ip.equals(this.ip))))
/*      */           {
/*      */ 
/* 2515 */             Debug.outNoStack("Dropping existing non-lanlocal peer connection [" + existing + "] in favour of [" + this + "]");
/* 2516 */             this.manager.removePeer(existing);
/* 2517 */             close = false;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2522 */       if (close) {
/* 2523 */         if (org.gudy.azureus2.core3.util.Constants.IS_CVS_VERSION) {
/*      */           try {
/* 2525 */             List<PEPeer> peers = this.manager.getPeers();
/* 2526 */             String dup_str = "?";
/* 2527 */             boolean dup_ip = false;
/* 2528 */             for (PEPeer p : peers)
/* 2529 */               if (p != this)
/*      */               {
/*      */ 
/* 2532 */                 byte[] id = p.getId();
/* 2533 */                 if (java.util.Arrays.equals(id, this.peer_id)) {
/* 2534 */                   dup_ip = p.getIp().equals(getIp());
/* 2535 */                   dup_str = p.getClient() + "/" + p.getClientNameFromExtensionHandshake() + "/" + p.getIp() + "/" + p.getPort();
/* 2536 */                   break;
/*      */                 }
/*      */               }
/* 2539 */             String my_str = getClient() + "/" + getIp() + "/" + getPort();
/*      */             
/* 2541 */             if (!dup_ip) {
/* 2542 */               Debug.outNoStack("Duplicate peer id detected: id=" + org.gudy.azureus2.core3.util.ByteFormatter.encodeString(this.peer_id) + ": this=" + my_str + ",other=" + dup_str);
/*      */             }
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */         
/* 2548 */         closeConnectionInternally("peer matches already-connected peer id");
/* 2549 */         handshake.destroy();
/* 2550 */         return;
/*      */       }
/*      */     }
/*      */     
/* 2554 */     if (sameIP) {
/* 2555 */       closeConnectionInternally("peer matches already-connected IP address, duplicate connections not allowed");
/* 2556 */       handshake.destroy();
/* 2557 */       return;
/*      */     }
/*      */     
/*      */ 
/* 2561 */     boolean max_reached = this.manager.getMaxNewConnectionsAllowed(this.network) == 0;
/*      */     
/* 2563 */     if ((max_reached) && (!this.manager.doOptimisticDisconnect(isLANLocal(), isPriorityConnection(), this.network)))
/*      */     {
/*      */ 
/* 2566 */       int[] _con_max = this.manager.getMaxConnections();
/* 2567 */       int con_max = _con_max[0] + _con_max[1];
/* 2568 */       String msg = "too many existing peer connections [p" + PeerIdentityManager.getIdentityCount(my_peer_data_id) + "/g" + PeerIdentityManager.getTotalIdentityCount() + ", pmx" + org.gudy.azureus2.core3.peer.util.PeerUtils.MAX_CONNECTIONS_PER_TORRENT + "/gmx" + org.gudy.azureus2.core3.peer.util.PeerUtils.MAX_CONNECTIONS_TOTAL + "/dmx" + con_max + "]";
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2574 */       closeConnectionInternally(msg);
/* 2575 */       handshake.destroy();
/* 2576 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2580 */       this.closing_mon.enter();
/*      */       
/* 2582 */       if (this.closing)
/*      */       {
/* 2584 */         String msg = "connection already closing";
/*      */         
/* 2586 */         closeConnectionInternally("connection already closing");
/*      */         
/* 2588 */         handshake.destroy(); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2593 */       if (!PeerIdentityManager.addIdentity(my_peer_data_id, this.peer_id, getPort(), this.ip))
/*      */       {
/* 2595 */         closeConnectionInternally("peer matches already-connected peer id");
/*      */         
/* 2597 */         handshake.destroy(); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 2602 */       this.identityAdded = true;
/*      */     }
/*      */     finally
/*      */     {
/* 2606 */       this.closing_mon.exit();
/*      */     }
/*      */     
/* 2609 */     if (Logger.isEnabled()) {
/* 2610 */       Logger.log(new LogEvent(this, LOGID, "In: has sent their handshake"));
/*      */     }
/*      */     
/* 2613 */     this.handshake_reserved_bytes = handshake.getReserved();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2627 */     this.ml_dht_enabled = ((this.handshake_reserved_bytes[7] & 0x1) == 1);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2633 */     this.fast_extension_enabled = ((this.manager.getUploadRateLimitBytesPerSecond() == 0) && ((this.handshake_reserved_bytes[7] & 0x4) != 0));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2638 */     this.messaging_mode = decideExtensionProtocol(handshake);
/*      */     
/*      */ 
/* 2641 */     if (this.messaging_mode == 2)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 2646 */       if ((Logger.isEnabled()) && (!this.client.contains("Azureus"))) {
/* 2647 */         Logger.log(new LogEvent(this, LOGID, "Handshake claims extended AZ messaging support... enabling AZ mode."));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2653 */       this.ml_dht_enabled = false;
/*      */       
/* 2655 */       Transport transport = this.connection.getTransport();
/*      */       int padding_mode;
/* 2657 */       int padding_mode; if (transport.isEncrypted()) { int padding_mode;
/* 2658 */         if (transport.isTCP()) {
/* 2659 */           padding_mode = 1;
/*      */         } else {
/* 2661 */           padding_mode = 2;
/*      */         }
/*      */       } else {
/* 2664 */         padding_mode = 0;
/*      */       }
/* 2666 */       this.connection.getIncomingMessageQueue().setDecoder(new com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageDecoder());
/* 2667 */       this.connection.getOutgoingMessageQueue().setEncoder(new com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessageEncoder(padding_mode));
/*      */       
/*      */ 
/*      */ 
/* 2671 */       sendAZHandshake();
/* 2672 */       handshake.destroy();
/*      */     }
/* 2674 */     else if (this.messaging_mode == 3) {
/* 2675 */       if (Logger.isEnabled()) {
/* 2676 */         Logger.log(new LogEvent(this, LOGID, "Enabling LT extension protocol support..."));
/*      */       }
/*      */       
/* 2679 */       this.connection.getIncomingMessageQueue().setDecoder(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.LTMessageDecoder());
/* 2680 */       this.connection.getOutgoingMessageQueue().setEncoder(new LTMessageEncoder(this));
/*      */       
/* 2682 */       generateSessionId();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2695 */       if (!this.is_metadata_download)
/*      */       {
/* 2697 */         initPostConnection(handshake);
/*      */       }
/*      */       
/* 2700 */       sendLTHandshake();
/*      */     }
/*      */     else {
/* 2703 */       this.client = com.aelitis.azureus.core.peermanager.utils.ClientIdentifier.identifyBTOnly(this.client_peer_id, this.handshake_reserved_bytes);
/*      */       
/* 2705 */       this.connection.getIncomingMessageQueue().getDecoder().resumeDecoding();
/*      */       
/* 2707 */       initPostConnection(handshake);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private int decideExtensionProtocol(BTHandshake handshake)
/*      */   {
/* 2714 */     int messaging_mode = this.manager.getExtendedMessagingMode();
/*      */     
/*      */ 
/*      */ 
/* 2718 */     if (messaging_mode == 0)
/*      */     {
/* 2720 */       return 1;
/*      */     }
/*      */     
/* 2723 */     boolean supports_ltep = (handshake.getReserved()[5] & 0x10) == 16;
/*      */     
/* 2725 */     if (messaging_mode == 1)
/*      */     {
/* 2727 */       return supports_ltep ? 3 : 1;
/*      */     }
/*      */     
/* 2730 */     boolean supports_azmp = (handshake.getReserved()[0] & 0x80) == 128;
/*      */     
/* 2732 */     if (!supports_azmp)
/*      */     {
/* 2734 */       if (supports_ltep)
/*      */       {
/* 2736 */         return 3;
/*      */       }
/*      */       
/* 2739 */       return 1;
/*      */     }
/*      */     
/* 2742 */     if (!supports_ltep)
/*      */     {
/*      */ 
/*      */ 
/* 2746 */       if (this.client.contains("Plus!"))
/*      */       {
/*      */ 
/* 2749 */         if (Logger.isEnabled()) {
/* 2750 */           Logger.log(new LogEvent(this, LOGID, "Handshake mistakingly indicates extended AZ messaging support...ignoring."));
/*      */         }
/*      */         
/* 2753 */         return 1;
/*      */       }
/*      */       
/* 2756 */       return 2;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2761 */     boolean enp_major_bit = (handshake.getReserved()[5] & 0x2) == 2;
/* 2762 */     boolean enp_minor_bit = (handshake.getReserved()[5] & 0x1) == 1;
/*      */     
/*      */ 
/* 2765 */     String their_ext_preference = (enp_major_bit == enp_minor_bit ? "Force " : "Prefer ") + (enp_major_bit ? "AZMP" : "LTEP");
/*      */     
/*      */ 
/* 2768 */     String our_ext_preference = "Force AZMP";
/* 2769 */     boolean use_azmp = (enp_major_bit) || (enp_minor_bit);
/* 2770 */     boolean we_decide = use_azmp;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2786 */     if (Logger.isEnabled()) {
/* 2787 */       String msg = "Peer supports both AZMP and LTEP: ";
/* 2788 */       msg = msg + "\"" + our_ext_preference + "\"" + (we_decide ? ">" : "<") + (our_ext_preference.equals(their_ext_preference) ? "= " : " ");
/* 2789 */       msg = msg + "\"" + their_ext_preference + "\" - using " + (use_azmp ? "AZMP" : "LTEP");
/* 2790 */       Logger.log(new LogEvent(this, LOGID, msg));
/*      */     }
/*      */     
/* 2793 */     return use_azmp ? 2 : 3;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void decodeLTHandshake(LTHandshake handshake)
/*      */   {
/* 2799 */     String lt_handshake_name = handshake.getClientName();
/* 2800 */     if (lt_handshake_name != null) {
/* 2801 */       this.client_handshake = StringInterner.intern(lt_handshake_name);
/* 2802 */       this.client = StringInterner.intern(com.aelitis.azureus.core.peermanager.utils.ClientIdentifier.identifyLTEP(this.client_peer_id, this.client_handshake, this.peer_id));
/*      */     }
/* 2804 */     if (handshake.getTCPListeningPort() > 0)
/*      */     {
/*      */ 
/*      */ 
/* 2808 */       Boolean crypto_requested = handshake.isCryptoRequested();
/* 2809 */       byte handshake_type = (crypto_requested != null) && (crypto_requested.booleanValue()) ? 1 : 0;
/* 2810 */       this.tcp_listen_port = handshake.getTCPListeningPort();
/* 2811 */       this.peer_item_identity = com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory.createPeerItem(this.ip, this.tcp_listen_port, PeerItem.convertSourceID(this.peer_source), handshake_type, this.udp_listen_port, this.crypto_level, 0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2821 */     if (handshake.isUploadOnly())
/*      */     {
/* 2823 */       this.relativeSeeding = ((byte)(this.relativeSeeding | 0x1));
/* 2824 */       checkSeed();
/*      */     }
/*      */     
/* 2827 */     if (org.gudy.azureus2.core3.util.AddressUtils.isGlobalAddressV6(handshake.getIPv6())) {
/* 2828 */       this.alternativeAddress = handshake.getIPv6();
/*      */     }
/* 2830 */     LTMessageEncoder encoder = (LTMessageEncoder)this.connection.getOutgoingMessageQueue().getEncoder();
/* 2831 */     encoder.updateSupportedExtensions(handshake.getExtensionMapping());
/* 2832 */     this.ut_pex_enabled = encoder.supportsUTPEX();
/*      */     
/* 2834 */     if (this.is_metadata_download)
/*      */     {
/* 2836 */       if (encoder.supportsUTMetaData())
/*      */       {
/* 2838 */         int mds = handshake.getMetadataSize();
/*      */         
/* 2840 */         if (mds > 0)
/*      */         {
/* 2842 */           spoofMDAvailability(mds);
/*      */         }
/*      */       }
/*      */       
/* 2846 */       if (this.current_peer_state != 30)
/*      */       {
/*      */ 
/*      */ 
/* 2850 */         initPostConnection(null);
/*      */       }
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
/*      */ 
/*      */ 
/* 2866 */     doPostHandshakeProcessing();
/*      */     
/* 2868 */     handshake.destroy();
/*      */   }
/*      */   
/*      */   protected void decodeAZHandshake(AZHandshake handshake) {
/* 2872 */     if (getConnectionState() == 4)
/*      */     {
/* 2874 */       handshake.destroy();
/* 2875 */       closeConnectionInternally("peer sent another az-handshake after the intial connect");
/*      */     }
/*      */     
/* 2878 */     this.client_handshake = StringInterner.intern(handshake.getClient());
/* 2879 */     this.client_handshake_version = StringInterner.intern(handshake.getClientVersion());
/* 2880 */     this.client = StringInterner.intern(com.aelitis.azureus.core.peermanager.utils.ClientIdentifier.identifyAZMP(this.client_peer_id, this.client_handshake, this.client_handshake_version, this.peer_id));
/*      */     
/* 2882 */     if (handshake.getTCPListenPort() > 0)
/*      */     {
/* 2884 */       this.tcp_listen_port = handshake.getTCPListenPort();
/* 2885 */       this.udp_listen_port = handshake.getUDPListenPort();
/* 2886 */       this.udp_non_data_port = handshake.getUDPNonDataListenPort();
/* 2887 */       byte type = handshake.getHandshakeType() == 1 ? 1 : 0;
/*      */       
/*      */ 
/*      */ 
/* 2891 */       this.peer_item_identity = com.aelitis.azureus.core.peermanager.peerdb.PeerItemFactory.createPeerItem(this.ip, this.tcp_listen_port, PeerItem.convertSourceID(this.peer_source), type, this.udp_listen_port, this.crypto_level, 0);
/*      */     }
/*      */     
/* 2894 */     if (org.gudy.azureus2.core3.util.AddressUtils.isGlobalAddressV6(handshake.getIPv6())) {
/* 2895 */       this.alternativeAddress = handshake.getIPv6();
/*      */     }
/*      */     
/* 2898 */     if (handshake.getReconnectSessionID() != null)
/*      */     {
/* 2900 */       if (Logger.isEnabled()) {
/* 2901 */         Logger.log(new LogEvent(this, LOGID, 0, "received reconnect request ID: " + handshake.getReconnectSessionID().toBase32String()));
/*      */       }
/* 2903 */       checkForReconnect(handshake.getReconnectSessionID());
/*      */     }
/*      */     
/* 2906 */     if (handshake.getRemoteSessionID() != null) {
/* 2907 */       this.peerSessionID = handshake.getRemoteSessionID();
/*      */     }
/* 2909 */     if (handshake.isUploadOnly())
/*      */     {
/* 2911 */       this.relativeSeeding = ((byte)(this.relativeSeeding | 0x1));
/* 2912 */       checkSeed();
/*      */     }
/*      */     
/*      */ 
/* 2916 */     String[] supported_message_ids = handshake.getMessageIDs();
/* 2917 */     byte[] supported_message_versions = handshake.getMessageVersions();
/*      */     
/*      */ 
/* 2920 */     ArrayList messages = new ArrayList();
/*      */     
/* 2922 */     for (int i = 0; i < handshake.getMessageIDs().length; i++)
/*      */     {
/* 2924 */       Message msg = com.aelitis.azureus.core.peermanager.messaging.MessageManager.getSingleton().lookupMessage(supported_message_ids[i]);
/* 2925 */       if (msg != null)
/*      */       {
/* 2927 */         messages.add(msg);
/*      */         
/* 2929 */         String id = msg.getID();
/* 2930 */         byte supported_version = supported_message_versions[i];
/*      */         
/*      */ 
/* 2933 */         if (id == "BT_BITFIELD") {
/* 2934 */           this.other_peer_bitfield_version = supported_version;
/* 2935 */         } else if (id == "BT_CANCEL") {
/* 2936 */           this.other_peer_cancel_version = supported_version;
/* 2937 */         } else if (id == "BT_CHOKE") {
/* 2938 */           this.other_peer_choke_version = supported_version;
/* 2939 */         } else if (id == "BT_HANDSHAKE") {
/* 2940 */           this.other_peer_handshake_version = supported_version;
/* 2941 */         } else if (id == "BT_HAVE") {
/* 2942 */           this.other_peer_bt_have_version = supported_version;
/* 2943 */         } else if (id == "BT_INTERESTED") {
/* 2944 */           this.other_peer_interested_version = supported_version;
/* 2945 */         } else if (id == "BT_KEEP_ALIVE") {
/* 2946 */           this.other_peer_keep_alive_version = supported_version;
/* 2947 */         } else if (id == "BT_PIECE") {
/* 2948 */           this.other_peer_piece_version = supported_version;
/* 2949 */         } else if (id == "BT_UNCHOKE") {
/* 2950 */           this.other_peer_unchoke_version = supported_version;
/* 2951 */         } else if (id == "BT_UNINTERESTED") {
/* 2952 */           this.other_peer_uninterested_version = supported_version;
/* 2953 */         } else if (id == "BT_REQUEST") {
/* 2954 */           this.other_peer_request_version = supported_version;
/* 2955 */         } else if (id == "BT_SUGGEST_PIECE") {
/* 2956 */           this.other_peer_suggest_piece_version = supported_version;
/* 2957 */         } else if (id == "BT_HAVE_ALL") {
/* 2958 */           this.other_peer_have_all_version = supported_version;
/* 2959 */         } else if (id == "BT_HAVE_NONE") {
/* 2960 */           this.other_peer_have_none_version = supported_version;
/* 2961 */         } else if (id == "BT_REJECT_REQUEST") {
/* 2962 */           this.other_peer_reject_request_version = supported_version;
/* 2963 */         } else if (id == "BT_ALLOWED_FAST") {
/* 2964 */           this.other_peer_allowed_fast_version = supported_version;
/* 2965 */         } else if (id == "AZ_PEER_EXCHANGE") {
/* 2966 */           this.other_peer_pex_version = supported_version;
/* 2967 */         } else if (id == "AZ_REQUEST_HINT") {
/* 2968 */           this.other_peer_az_request_hint_version = supported_version;
/* 2969 */         } else if (id == "AZ_HAVE") {
/* 2970 */           this.other_peer_az_have_version = supported_version;
/* 2971 */         } else if (id == "AZ_BAD_PIECE") {
/* 2972 */           this.other_peer_az_bad_piece_version = supported_version;
/* 2973 */         } else if (id == "AZ_STAT_REQ") {
/* 2974 */           this.other_peer_az_stats_request_version = supported_version;
/* 2975 */         } else if (id == "AZ_STAT_REP") {
/* 2976 */           this.other_peer_az_stats_reply_version = supported_version;
/* 2977 */         } else if (id == "AZ_METADATA") {
/* 2978 */           this.other_peer_az_metadata_version = supported_version;
/* 2979 */         } else if (id == "BT_DHT_PORT") {
/* 2980 */           this.ml_dht_enabled = true;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2990 */     if (this.is_metadata_download)
/*      */     {
/* 2992 */       int mds = handshake.getMetadataSize();
/*      */       
/* 2994 */       if (mds > 0)
/*      */       {
/* 2996 */         this.manager.setTorrentInfoDictSize(mds);
/*      */       }
/*      */     }
/*      */     
/* 3000 */     this.supported_messages = ((Message[])messages.toArray(new Message[messages.size()]));
/*      */     
/* 3002 */     if (this.outgoing_piece_message_handler != null) {
/* 3003 */       this.outgoing_piece_message_handler.setPieceVersion(this.other_peer_piece_version);
/*      */     }
/*      */     
/* 3006 */     if (this.outgoing_have_message_aggregator != null) {
/* 3007 */       this.outgoing_have_message_aggregator.setHaveVersion(this.other_peer_bt_have_version, this.other_peer_az_have_version);
/*      */     }
/*      */     
/* 3010 */     initPostConnection(handshake);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void spoofMDAvailability(int mds)
/*      */   {
/* 3017 */     int md_pieces = (mds + 16384 - 1) / 16384;
/*      */     
/* 3019 */     this.manager.setTorrentInfoDictSize(mds);
/*      */     
/* 3021 */     BitFlags tempHavePieces = new BitFlags(this.nbPieces);
/*      */     
/* 3023 */     for (int i = 0; i < md_pieces; i++)
/*      */     {
/* 3025 */       tempHavePieces.set(i);
/*      */     }
/*      */     
/* 3028 */     this.peerHavePieces = tempHavePieces;
/*      */     
/* 3030 */     addAvailability();
/*      */     
/* 3032 */     this.really_choked_by_other_peer = false;
/*      */     
/* 3034 */     calculatePiecePriorities();
/*      */   }
/*      */   
/*      */   private void initPostConnection(Message handshake) {
/* 3038 */     changePeerState(30);
/* 3039 */     this.connection_state = 4;
/* 3040 */     sendBitField();
/* 3041 */     if (handshake != null) {
/* 3042 */       handshake.destroy();
/*      */     }
/* 3044 */     addAvailability();
/* 3045 */     sendMainlineDHTPort();
/*      */     
/* 3047 */     if (this.is_metadata_download) {
/* 3048 */       this.interested_in_other_peer = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeHaveAll(com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHaveAll have_all)
/*      */   {
/* 3056 */     have_all.destroy();
/*      */     
/* 3058 */     this.received_bitfield = true;
/*      */     
/* 3060 */     if (this.is_metadata_download)
/*      */     {
/* 3062 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3066 */       this.closing_mon.enter();
/*      */       
/* 3068 */       if (!this.closing)
/*      */       {
/*      */         BitFlags tempHavePieces;
/*      */         BitFlags tempHavePieces;
/* 3072 */         if (this.peerHavePieces == null)
/*      */         {
/* 3074 */           tempHavePieces = new BitFlags(this.nbPieces);
/*      */         }
/*      */         else
/*      */         {
/* 3078 */           tempHavePieces = this.peerHavePieces;
/*      */           
/* 3080 */           removeAvailability();
/*      */         }
/*      */         
/* 3083 */         tempHavePieces.setAll();
/*      */         
/* 3085 */         for (int i = 0; i < this.nbPieces; i++)
/*      */         {
/* 3087 */           this.manager.updateSuperSeedPiece(this, i);
/*      */         }
/*      */         
/* 3090 */         this.peerHavePieces = tempHavePieces;
/*      */         
/* 3092 */         addAvailability();
/*      */         
/* 3094 */         checkSeed();
/*      */         
/* 3096 */         checkInterested();
/*      */       }
/*      */     }
/*      */     finally {
/* 3100 */       this.closing_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeHaveNone(BTHaveNone have_none)
/*      */   {
/* 3108 */     have_none.destroy();
/*      */     
/* 3110 */     this.received_bitfield = true;
/*      */     
/* 3112 */     if (this.is_metadata_download)
/*      */     {
/* 3114 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 3118 */       this.closing_mon.enter();
/*      */       
/* 3120 */       if (!this.closing)
/*      */       {
/*      */         BitFlags tempHavePieces;
/*      */         BitFlags tempHavePieces;
/* 3124 */         if (this.peerHavePieces == null)
/*      */         {
/* 3126 */           tempHavePieces = new BitFlags(this.nbPieces);
/*      */         }
/*      */         else
/*      */         {
/* 3130 */           tempHavePieces = this.peerHavePieces;
/*      */           
/* 3132 */           removeAvailability();
/*      */         }
/*      */         
/* 3135 */         tempHavePieces.clear();
/*      */         
/* 3137 */         this.peerHavePieces = tempHavePieces;
/*      */         
/*      */ 
/*      */ 
/* 3141 */         addAvailability();
/*      */         
/* 3143 */         checkSeed();
/*      */         
/* 3145 */         checkInterested();
/*      */         
/* 3147 */         checkFast(tempHavePieces);
/*      */       }
/*      */     }
/*      */     finally {
/* 3151 */       this.closing_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void decodeBitfield(BTBitfield bitfield)
/*      */   {
/* 3157 */     this.received_bitfield = true;
/*      */     
/* 3159 */     if (this.is_metadata_download)
/*      */     {
/* 3161 */       bitfield.destroy();
/*      */       
/* 3163 */       return;
/*      */     }
/*      */     
/* 3166 */     DirectByteBuffer field = bitfield.getBitfield();
/*      */     
/* 3168 */     byte[] dataf = new byte[(this.nbPieces + 7) / 8];
/*      */     
/* 3170 */     if (field.remaining((byte)9) < dataf.length) {
/* 3171 */       String error = toString() + " has sent invalid Bitfield: too short [" + field.remaining((byte)9) + "<" + dataf.length + "]";
/* 3172 */       Debug.out(error);
/* 3173 */       if (Logger.isEnabled())
/* 3174 */         Logger.log(new LogEvent(this, LOGID, 3, error));
/* 3175 */       bitfield.destroy();
/* 3176 */       return;
/*      */     }
/*      */     
/* 3179 */     field.get((byte)9, dataf);
/*      */     try
/*      */     {
/* 3182 */       this.closing_mon.enter();
/* 3183 */       if (this.closing) {
/* 3184 */         bitfield.destroy();
/*      */       } else {
/*      */         BitFlags tempHavePieces;
/*      */         BitFlags tempHavePieces;
/* 3188 */         if (this.peerHavePieces == null)
/*      */         {
/* 3190 */           tempHavePieces = new BitFlags(this.nbPieces);
/*      */         }
/*      */         else {
/* 3193 */           tempHavePieces = this.peerHavePieces;
/* 3194 */           removeAvailability();
/*      */         }
/* 3196 */         for (int i = 0; i < this.nbPieces; i++)
/*      */         {
/* 3198 */           int index = i / 8;
/* 3199 */           int bit = 7 - i % 8;
/* 3200 */           byte bData = dataf[index];
/* 3201 */           byte b = (byte)(bData >> bit);
/* 3202 */           if ((b & 0x1) == 1)
/*      */           {
/* 3204 */             tempHavePieces.set(i);
/* 3205 */             this.manager.updateSuperSeedPiece(this, i);
/*      */           }
/*      */         }
/*      */         
/* 3209 */         bitfield.destroy();
/*      */         
/* 3211 */         this.peerHavePieces = tempHavePieces;
/* 3212 */         addAvailability();
/*      */         
/* 3214 */         checkSeed();
/* 3215 */         checkInterested();
/*      */         
/* 3217 */         checkFast(tempHavePieces);
/*      */       }
/*      */     }
/*      */     finally {
/* 3221 */       this.closing_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSuspendedLazyBitFieldEnabled(boolean enable)
/*      */   {
/* 3229 */     this.manual_lazy_bitfield_control = enable;
/*      */     
/* 3231 */     if (!enable)
/*      */     {
/* 3233 */       int[] pending = this.manual_lazy_haves;
/*      */       
/* 3235 */       this.manual_lazy_haves = null;
/*      */       
/* 3237 */       if (pending != null)
/*      */       {
/* 3239 */         sendLazyHaves(pending, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void decodeMainlineDHTPort(BTDHTPort port)
/*      */   {
/* 3246 */     int i_port = port.getDHTPort();
/* 3247 */     port.destroy();
/*      */     
/* 3249 */     if (!this.ml_dht_enabled) return;
/* 3250 */     org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider provider = getDHTProvider();
/* 3251 */     if (provider == null) { return;
/*      */     }
/* 3253 */     if (this.network == "Public")
/* 3254 */       try { provider.notifyOfIncomingPort(getIp(), i_port);
/* 3255 */       } catch (Throwable t) { Debug.printStackTrace(t);
/*      */       }
/*      */   }
/*      */   
/*      */   protected void decodeChoke(com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTChoke choke) {
/* 3260 */     choke.destroy();
/*      */     
/* 3262 */     if (this.is_metadata_download) {
/* 3263 */       return;
/*      */     }
/*      */     
/* 3266 */     if (!this.really_choked_by_other_peer)
/*      */     {
/* 3268 */       this.really_choked_by_other_peer = true;
/* 3269 */       calculatePiecePriorities();
/* 3270 */       cancelRequests();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void decodeUnchoke(com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUnchoke unchoke)
/*      */   {
/* 3276 */     unchoke.destroy();
/*      */     
/* 3278 */     if (this.is_metadata_download) {
/* 3279 */       return;
/*      */     }
/*      */     
/* 3282 */     if (this.really_choked_by_other_peer)
/*      */     {
/* 3284 */       this.really_choked_by_other_peer = false;
/* 3285 */       calculatePiecePriorities();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeInterested(BTInterested interested)
/*      */   {
/* 3293 */     interested.destroy();
/*      */     
/*      */ 
/*      */ 
/* 3297 */     this.other_peer_interested_in_me = ((!isSeed()) && (!isRelativeSeed()));
/*      */     
/* 3299 */     if ((this.other_peer_interested_in_me) && (!this.is_upload_disabled) && (fast_unchoke_new_peers) && (isChokedByMe()) && (getData("fast_unchoke_done") == null))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3305 */       setData("fast_unchoke_done", "");
/*      */       
/* 3307 */       sendUnChoke();
/*      */     }
/*      */   }
/*      */   
/*      */   protected void decodeUninterested(com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUninterested uninterested)
/*      */   {
/* 3313 */     uninterested.destroy();
/* 3314 */     this.other_peer_interested_in_me = false;
/*      */     
/*      */ 
/* 3317 */     if (this.outgoing_have_message_aggregator != null) {
/* 3318 */       this.outgoing_have_message_aggregator.forceSendOfPending();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void decodeHave(BTHave have)
/*      */   {
/* 3327 */     int pieceNumber = have.getPieceNumber();
/* 3328 */     have.destroy();
/*      */     
/* 3330 */     if (this.is_metadata_download)
/*      */     {
/* 3332 */       return;
/*      */     }
/*      */     
/* 3335 */     if ((pieceNumber >= this.nbPieces) || (pieceNumber < 0)) {
/* 3336 */       closeConnectionInternally("invalid pieceNumber: " + pieceNumber);
/* 3337 */       return;
/*      */     }
/*      */     
/* 3340 */     if (this.closing) {
/* 3341 */       return;
/*      */     }
/* 3343 */     if (this.peerHavePieces == null) {
/* 3344 */       this.peerHavePieces = new BitFlags(this.nbPieces);
/*      */     }
/* 3346 */     if (this.peerHavePieces.flags[pieceNumber] == 0)
/*      */     {
/* 3348 */       if ((!this.interested_in_other_peer) && (this.diskManager.isInteresting(pieceNumber)) && (!this.is_download_disabled))
/*      */       {
/* 3350 */         this.connection.getOutgoingMessageQueue().addMessage(new BTInterested(this.other_peer_interested_version), false);
/* 3351 */         this.interested_in_other_peer = true;
/*      */       }
/* 3353 */       this.peerHavePieces.set(pieceNumber);
/*      */       
/* 3355 */       int pieceLength = this.manager.getPieceLength(pieceNumber);
/* 3356 */       this.manager.havePiece(pieceNumber, pieceLength, this);
/*      */       
/* 3358 */       checkSeed();
/* 3359 */       if ((this.other_peer_interested_in_me) && (
/* 3360 */         (isSeed()) || (isRelativeSeed()))) {
/* 3361 */         this.other_peer_interested_in_me = false;
/*      */       }
/*      */       
/* 3364 */       this.peer_stats.hasNewPiece(pieceLength);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeAZHave(AZHave have)
/*      */   {
/* 3372 */     int[] pieceNumbers = have.getPieceNumbers();
/*      */     
/* 3374 */     have.destroy();
/*      */     
/* 3376 */     if (this.closing)
/*      */     {
/* 3378 */       return;
/*      */     }
/*      */     
/* 3381 */     if (this.peerHavePieces == null)
/*      */     {
/* 3383 */       this.peerHavePieces = new BitFlags(this.nbPieces);
/*      */     }
/*      */     
/* 3386 */     boolean send_interested = false;
/* 3387 */     boolean new_have = false;
/*      */     
/* 3389 */     for (int i = 0; i < pieceNumbers.length; i++)
/*      */     {
/* 3391 */       int pieceNumber = pieceNumbers[i];
/*      */       
/* 3393 */       if ((pieceNumber >= this.nbPieces) || (pieceNumber < 0))
/*      */       {
/* 3395 */         closeConnectionInternally("invalid pieceNumber: " + pieceNumber);
/*      */         
/* 3397 */         return;
/*      */       }
/*      */       
/* 3400 */       if (this.peerHavePieces.flags[pieceNumber] == 0)
/*      */       {
/* 3402 */         new_have = true;
/*      */         
/* 3404 */         if ((!send_interested) && (!this.interested_in_other_peer) && (!this.is_download_disabled) && (this.diskManager.isInteresting(pieceNumber)))
/*      */         {
/*      */ 
/* 3407 */           send_interested = true;
/*      */         }
/*      */         
/* 3410 */         this.peerHavePieces.set(pieceNumber);
/*      */         
/* 3412 */         int pieceLength = this.manager.getPieceLength(pieceNumber);
/*      */         
/* 3414 */         this.manager.havePiece(pieceNumber, pieceLength, this);
/*      */         
/* 3416 */         this.peer_stats.hasNewPiece(pieceLength);
/*      */       }
/*      */     }
/*      */     
/* 3420 */     if (new_have)
/*      */     {
/* 3422 */       checkSeed();
/*      */       
/* 3424 */       if ((this.other_peer_interested_in_me) && (
/* 3425 */         (isSeed()) || (isRelativeSeed()))) {
/* 3426 */         this.other_peer_interested_in_me = false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 3431 */     if (send_interested)
/*      */     {
/* 3433 */       this.connection.getOutgoingMessageQueue().addMessage(new BTInterested(this.other_peer_interested_version), false);
/*      */       
/* 3435 */       this.interested_in_other_peer = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getBytesDownloaded()
/*      */   {
/* 3442 */     if ((this.peerHavePieces == null) || (this.peerHavePieces.flags.length == 0)) {
/* 3443 */       return 0L;
/*      */     }
/*      */     long total_done;
/*      */     long total_done;
/* 3447 */     if (this.peerHavePieces.flags[(this.nbPieces - 1)] != 0)
/*      */     {
/* 3449 */       total_done = (this.peerHavePieces.nbSet - 1) * this.diskManager.getPieceLength() + this.diskManager.getPieceLength(this.nbPieces - 1);
/*      */     }
/*      */     else
/*      */     {
/* 3453 */       total_done = this.peerHavePieces.nbSet * this.diskManager.getPieceLength();
/*      */     }
/*      */     
/* 3456 */     return Math.min(total_done, this.diskManager.getTotalLength());
/*      */   }
/*      */   
/*      */ 
/*      */   public long getBytesRemaining()
/*      */   {
/* 3462 */     return this.diskManager.getTotalLength() - getBytesDownloaded();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendBadPiece(int piece_number)
/*      */   {
/* 3469 */     if (this.bad_piece_supported)
/*      */     {
/* 3471 */       AZBadPiece bp = new AZBadPiece(piece_number, this.other_peer_az_bad_piece_version);
/*      */       
/* 3473 */       this.connection.getOutgoingMessageQueue().addMessage(bp, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeAZBadPiece(AZBadPiece bad_piece)
/*      */   {
/* 3481 */     int piece_number = bad_piece.getPieceNumber();
/*      */     
/* 3483 */     bad_piece.destroy();
/*      */     
/* 3485 */     this.manager.badPieceReported(this, piece_number);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendStatsRequest(Map request)
/*      */   {
/* 3492 */     if (this.stats_request_supported)
/*      */     {
/* 3494 */       AZStatRequest sr = new AZStatRequest(request, this.other_peer_az_stats_request_version);
/*      */       
/* 3496 */       this.connection.getOutgoingMessageQueue().addMessage(sr, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeAZStatsRequest(AZStatRequest request)
/*      */   {
/* 3504 */     Map req = request.getRequest();
/*      */     
/* 3506 */     request.destroy();
/*      */     
/* 3508 */     this.manager.statsRequest(this, req);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendStatsReply(Map reply)
/*      */   {
/* 3515 */     if (this.stats_reply_supported)
/*      */     {
/* 3517 */       AZStatReply sr = new AZStatReply(reply, this.other_peer_az_stats_reply_version);
/*      */       
/* 3519 */       this.connection.getOutgoingMessageQueue().addMessage(sr, false);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeAZStatsReply(AZStatReply reply)
/*      */   {
/* 3527 */     Map rep = reply.getReply();
/*      */     
/* 3529 */     reply.destroy();
/*      */     
/* 3531 */     this.manager.statsReply(this, rep);
/*      */   }
/*      */   
/*      */   protected void decodeRequest(BTRequest request) {
/* 3535 */     int number = request.getPieceNumber();
/* 3536 */     int offset = request.getPieceOffset();
/* 3537 */     int length = request.getLength();
/* 3538 */     request.destroy();
/*      */     
/* 3540 */     if (!this.manager.validateReadRequest(this, number, offset, length)) {
/* 3541 */       closeConnectionInternally("request for piece #" + number + ":" + offset + "->" + (offset + length - 1) + " is an invalid request");
/* 3542 */       return;
/*      */     }
/*      */     
/* 3545 */     if (this.manager.getHiddenPiece() == number) {
/* 3546 */       closeConnectionInternally("request for piece #" + number + " is invalid as piece is hidden");
/* 3547 */       return;
/*      */     }
/*      */     
/* 3550 */     boolean request_ok = !this.is_upload_disabled;
/*      */     
/* 3552 */     if (request_ok)
/*      */     {
/* 3554 */       if (this.choking_other_peer) {
/*      */         try
/*      */         {
/* 3557 */           this.general_mon.enter();
/*      */           
/* 3559 */           int[][] pieces = (int[][])getUserData(KEY_ALLOWED_FAST_SENT);
/*      */           
/* 3561 */           if (pieces != null)
/*      */           {
/* 3563 */             for (int i = 0; i < pieces.length; i++)
/*      */             {
/* 3565 */               if (pieces[i][0] == number)
/*      */               {
/* 3567 */                 if (pieces[i][1] >= length)
/*      */                 {
/* 3569 */                   pieces[i][1] -= length;
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3575 */                   request_ok = true;
/*      */                   
/* 3577 */                   createPieceMessageHandler();
/*      */                   
/* 3579 */                   break;
/*      */                 }
/*      */                 
/*      */ 
/* 3583 */                 this.manager.reportBadFastExtensionUse(this);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 3590 */           this.general_mon.exit();
/*      */         }
/*      */         
/*      */       } else {
/* 3594 */         request_ok = true;
/*      */       }
/*      */     }
/*      */     
/* 3598 */     if (request_ok)
/*      */     {
/* 3600 */       if ((this.outgoing_piece_message_handler == null) || (!this.outgoing_piece_message_handler.addPieceRequest(number, offset, length)))
/*      */       {
/* 3602 */         sendRejectRequest(number, offset, length);
/*      */       }
/*      */       
/* 3605 */       this.allowReconnect = true;
/*      */     }
/*      */     else {
/* 3608 */       if (Logger.isEnabled()) {
/* 3609 */         Logger.log(new LogEvent(this, LOGID, "decodeRequest(): peer request for piece #" + number + ":" + offset + "->" + (offset + length - 1) + " ignored as peer is currently choked."));
/*      */       }
/*      */       
/*      */ 
/* 3613 */       sendRejectRequest(number, offset, length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void sendRejectRequest(DiskManagerReadRequest request)
/*      */   {
/* 3621 */     sendRejectRequest(request.getPieceNumber(), request.getOffset(), request.getLength());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void sendRejectRequest(int number, int offset, int length)
/*      */   {
/* 3630 */     if ((this.fast_extension_enabled) && (!this.closing))
/*      */     {
/*      */ 
/*      */ 
/* 3634 */       BTRejectRequest reject = new BTRejectRequest(number, offset, length, this.other_peer_reject_request_version);
/*      */       
/* 3636 */       this.connection.getOutgoingMessageQueue().addMessage(reject, false);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void decodePiece(BTPiece piece) {
/* 3641 */     final int pieceNumber = piece.getPieceNumber();
/* 3642 */     final int offset = piece.getPieceOffset();
/* 3643 */     DirectByteBuffer payload = piece.getPieceData();
/* 3644 */     final int length = payload.remaining((byte)9);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3662 */     Object error_msg = new Object()
/*      */     {
/*      */ 
/*      */       public final String toString()
/*      */       {
/*      */ 
/* 3668 */         return "decodePiece(): Peer has sent piece #" + pieceNumber + ":" + offset + "->" + (offset + length - 1) + ", ";
/*      */       }
/*      */     };
/*      */     
/* 3672 */     if (!this.manager.validatePieceReply(this, pieceNumber, offset, payload)) {
/* 3673 */       this.peer_stats.bytesDiscarded(length);
/* 3674 */       this.manager.discarded(this, length);
/* 3675 */       requests_discarded += 1;
/* 3676 */       printRequestStats();
/* 3677 */       piece.destroy();
/* 3678 */       if (Logger.isEnabled()) {
/* 3679 */         Logger.log(new LogEvent(this, LOGID, 3, error_msg + "but piece block discarded as invalid."));
/*      */       }
/*      */       
/* 3682 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3691 */     DiskManagerReadRequest existing_request = lookupRequest(pieceNumber, offset, length);
/*      */     
/* 3693 */     boolean piece_error = true;
/*      */     
/* 3695 */     if (existing_request != null) {
/* 3696 */       if (existing_request.isLatencyTest()) {
/* 3697 */         long latency = SystemTime.getMonotonousTime() - existing_request.getTimeSent();
/*      */         
/* 3699 */         if (latency > 0L) {
/* 3700 */           this.request_latency = latency;
/*      */         }
/*      */       }
/* 3703 */       removeRequest(existing_request);
/* 3704 */       long now = SystemTime.getCurrentTime();
/* 3705 */       resetRequestsTime(now);
/*      */       
/* 3707 */       if (this.manager.isWritten(pieceNumber, offset)) {
/* 3708 */         this.peer_stats.bytesDiscarded(length);
/* 3709 */         this.manager.discarded(this, length);
/*      */         
/* 3711 */         if (this.manager.isInEndGameMode()) {
/* 3712 */           if ((this.last_good_data_time != -1L) && (now - this.last_good_data_time <= 60000L))
/* 3713 */             setSnubbed(false);
/* 3714 */           this.last_good_data_time = now;
/* 3715 */           requests_discarded_endgame += 1;
/* 3716 */           if (Logger.isEnabled()) {
/* 3717 */             Logger.log(new LogEvent(this, LogIDs.PIECES, 0, error_msg + "but piece block ignored as already written in end-game mode."));
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 3725 */           if (!isSnubbed())
/* 3726 */             this.last_good_data_time = now;
/* 3727 */           if (Logger.isEnabled()) {
/* 3728 */             Logger.log(new LogEvent(this, LogIDs.PIECES, 1, error_msg + "but piece block discarded as already written."));
/*      */           }
/*      */           
/* 3731 */           requests_discarded += 1;
/*      */         }
/*      */         
/* 3734 */         printRequestStats();
/*      */       }
/*      */       else {
/* 3737 */         this.manager.writeBlock(pieceNumber, offset, payload, this, false);
/* 3738 */         if ((this.last_good_data_time != -1L) && (now - this.last_good_data_time <= 60000L))
/* 3739 */           setSnubbed(false);
/* 3740 */         this.last_good_data_time = now;
/* 3741 */         requests_completed += 1;
/* 3742 */         piece_error = false;
/*      */       }
/*      */       
/*      */     }
/* 3746 */     else if (!this.manager.isWritten(pieceNumber, offset))
/*      */     {
/*      */ 
/* 3749 */       DiskManagerReadRequest request = this.manager.createDiskManagerRequest(pieceNumber, offset, length);
/*      */       boolean ever_requested;
/* 3751 */       try { this.recent_outgoing_requests_mon.enter();
/* 3752 */         ever_requested = this.recent_outgoing_requests.containsKey(request);
/*      */       } finally {
/* 3754 */         this.recent_outgoing_requests_mon.exit();
/*      */       }
/* 3756 */       if (ever_requested) {
/* 3757 */         this.manager.writeBlock(pieceNumber, offset, payload, this, true);
/* 3758 */         long now = SystemTime.getCurrentTime();
/* 3759 */         if ((this.last_good_data_time != -1L) && (now - this.last_good_data_time <= 60000L))
/* 3760 */           setSnubbed(false);
/* 3761 */         resetRequestsTime(now);
/* 3762 */         this.last_good_data_time = now;
/* 3763 */         requests_recovered += 1;
/* 3764 */         printRequestStats();
/* 3765 */         piece_error = false;
/* 3766 */         if (Logger.isEnabled()) {
/* 3767 */           Logger.log(new LogEvent(this, LogIDs.PIECES, 0, error_msg + "expired piece block data recovered as useful."));
/*      */         }
/*      */         
/*      */       }
/*      */       else
/*      */       {
/* 3773 */         System.out.println("[" + this.client + "]" + error_msg + "but expired piece block discarded as never requested.");
/*      */         
/* 3775 */         this.peer_stats.bytesDiscarded(length);
/* 3776 */         this.manager.discarded(this, length);
/* 3777 */         requests_discarded += 1;
/* 3778 */         printRequestStats();
/* 3779 */         if (Logger.isEnabled()) {
/* 3780 */           Logger.log(new LogEvent(this, LogIDs.PIECES, 3, error_msg + "but expired piece block discarded as never requested."));
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 3786 */       this.peer_stats.bytesDiscarded(length);
/* 3787 */       this.manager.discarded(this, length);
/* 3788 */       requests_discarded += 1;
/* 3789 */       printRequestStats();
/* 3790 */       if (Logger.isEnabled()) {
/* 3791 */         Logger.log(new LogEvent(this, LogIDs.PIECES, 1, error_msg + "but expired piece block discarded as already written."));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 3797 */     if (piece_error) {
/* 3798 */       piece.destroy();
/*      */     } else {
/* 3800 */       this.allowReconnect = true;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void decodeCancel(BTCancel cancel)
/*      */   {
/* 3809 */     int number = cancel.getPieceNumber();
/* 3810 */     int offset = cancel.getPieceOffset();
/* 3811 */     int length = cancel.getLength();
/* 3812 */     cancel.destroy();
/* 3813 */     if (this.outgoing_piece_message_handler != null) {
/* 3814 */       this.outgoing_piece_message_handler.removePieceRequest(number, offset, length);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void decodeRejectRequest(BTRejectRequest reject)
/*      */   {
/* 3821 */     int number = reject.getPieceNumber();
/* 3822 */     int offset = reject.getPieceOffset();
/* 3823 */     int length = reject.getLength();
/* 3824 */     reject.destroy();
/*      */     
/* 3826 */     DiskManagerReadRequest request = this.manager.createDiskManagerRequest(number, offset, length);
/*      */     
/* 3828 */     if (hasBeenRequested(request))
/*      */     {
/* 3830 */       removeRequest(request);
/*      */       
/* 3832 */       this.manager.requestCanceled(request);
/*      */       
/*      */ 
/*      */       try
/*      */       {
/* 3837 */         this.general_mon.enter();
/*      */         
/* 3839 */         List<Integer> pieces = (List)getUserData(KEY_ALLOWED_FAST_RECEIVED);
/*      */         
/* 3841 */         if (pieces != null)
/*      */         {
/* 3843 */           pieces.remove(new Integer(number));
/*      */           
/* 3845 */           if (pieces.size() == 0)
/*      */           {
/* 3847 */             setUserData(KEY_ALLOWED_FAST_RECEIVED, null);
/*      */           }
/*      */         }
/*      */         
/* 3851 */         int[] priorities = this.piece_priority_offsets;
/*      */         
/* 3853 */         if (priorities != null)
/*      */         {
/* 3855 */           priorities[number] = Integer.MIN_VALUE;
/*      */         }
/*      */         
/* 3858 */         calculatePiecePriorities();
/*      */       }
/*      */       finally
/*      */       {
/* 3862 */         this.general_mon.exit();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void decodeAllowedFast(BTAllowedFast allowed)
/*      */   {
/* 3871 */     int piece = allowed.getPieceNumber();
/*      */     
/* 3873 */     allowed.destroy();
/*      */     
/* 3875 */     if (this.piecePicker.getNbPiecesDone() > 10)
/*      */     {
/*      */ 
/*      */ 
/* 3879 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 3887 */       this.general_mon.enter();
/*      */       
/* 3889 */       List<Integer> pieces = (List)getUserData(KEY_ALLOWED_FAST_RECEIVED);
/*      */       
/* 3891 */       if (pieces == null)
/*      */       {
/* 3893 */         pieces = new ArrayList(10);
/*      */         
/* 3895 */         setUserData(KEY_ALLOWED_FAST_RECEIVED, pieces);
/*      */       }
/*      */       
/* 3898 */       if (pieces.size() < 20)
/*      */       {
/* 3900 */         Integer i = new Integer(piece);
/*      */         
/* 3902 */         if ((!pieces.contains(i)) && (i.intValue() >= 0) && (i.intValue() < this.nbPieces))
/*      */         {
/* 3904 */           pieces.add(i);
/*      */           
/* 3906 */           calculatePiecePriorities();
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 3911 */       this.general_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void sendAllowFast(int number)
/*      */   {
/* 3919 */     if (this.fast_extension_enabled)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3925 */       BTAllowedFast af = new BTAllowedFast(number, this.other_peer_allowed_fast_version);
/*      */       
/* 3927 */       this.connection.getOutgoingMessageQueue().addMessage(af, false);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void calculatePiecePriorities()
/*      */   {
/*      */     try
/*      */     {
/* 3935 */       this.general_mon.enter();
/*      */       
/* 3937 */       if (this.really_choked_by_other_peer)
/*      */       {
/* 3939 */         List<Integer> pieces = (List)getUserData(KEY_ALLOWED_FAST_RECEIVED);
/*      */         
/* 3941 */         if (pieces == null)
/*      */         {
/* 3943 */           this.effectively_choked_by_other_peer = true;
/*      */           
/* 3945 */           this.piece_priority_offsets = null;
/*      */         }
/*      */         else
/*      */         {
/* 3949 */           int[] priorities = this.piece_priority_offsets;
/*      */           
/* 3951 */           if (priorities == null)
/*      */           {
/* 3953 */             priorities = new int[this.nbPieces];
/*      */             
/* 3955 */             java.util.Arrays.fill(priorities, Integer.MIN_VALUE);
/*      */           }
/*      */           
/* 3958 */           for (Iterator i$ = pieces.iterator(); i$.hasNext();) { int i = ((Integer)i$.next()).intValue();
/*      */             
/* 3960 */             priorities[i] = 0;
/*      */           }
/*      */           
/* 3963 */           this.piece_priority_offsets = priorities;
/*      */           
/* 3965 */           if (this.effectively_choked_by_other_peer)
/*      */           {
/* 3967 */             this.effectively_choked_by_other_peer = false;
/*      */             
/* 3969 */             this.effectively_unchoked_time = SystemTime.getMonotonousTime();
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 3974 */         if (this.effectively_choked_by_other_peer)
/*      */         {
/* 3976 */           this.effectively_choked_by_other_peer = false;
/*      */           
/* 3978 */           this.effectively_unchoked_time = SystemTime.getMonotonousTime();
/*      */         }
/*      */         
/* 3981 */         this.piece_priority_offsets = null;
/*      */       }
/*      */     }
/*      */     finally {
/* 3985 */       this.general_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkFast(BitFlags flags)
/*      */   {
/* 3993 */     if ((this.fast_extension_enabled) && (!this.is_upload_disabled) && (!isSeed()) && (!isRelativeSeed()) && (com.aelitis.azureus.core.peermanager.utils.PeerClassifier.fullySupportsFE(this.client_peer_id)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4000 */       if (flags.nbSet >= 10)
/*      */       {
/* 4002 */         return;
/*      */       }
/*      */       
/* 4005 */       if (!this.manager.isFastExtensionPermitted(this)) {
/*      */         return;
/*      */       }
/*      */       
/*      */       int[][] pieces;
/*      */       
/*      */       try
/*      */       {
/* 4013 */         this.general_mon.enter();
/*      */         
/* 4015 */         pieces = (int[][])getUserData(KEY_ALLOWED_FAST_SENT);
/*      */         
/* 4017 */         if (pieces == null)
/*      */         {
/* 4019 */           List<Integer> l_pieces = generateFastSet(10);
/*      */           
/* 4021 */           pieces = new int[l_pieces.size()][2];
/*      */           
/* 4023 */           int piece_size = this.diskManager.getPieceLength();
/*      */           
/* 4025 */           for (int i = 0; i < l_pieces.size(); i++)
/*      */           {
/* 4027 */             int piece_number = ((Integer)l_pieces.get(i)).intValue();
/*      */             
/* 4029 */             pieces[i] = { piece_number, piece_size * 2 };
/*      */           }
/*      */           
/* 4032 */           setUserData(KEY_ALLOWED_FAST_SENT, pieces);
/*      */         }
/*      */       }
/*      */       finally {
/* 4036 */         this.general_mon.exit();
/*      */       }
/*      */       
/* 4039 */       for (int i = 0; i < pieces.length; i++)
/*      */       {
/* 4041 */         int piece_number = pieces[i][0];
/*      */         
/* 4043 */         if (flags.flags[piece_number] == 0)
/*      */         {
/* 4045 */           sendAllowFast(piece_number);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkAllowedFast()
/*      */   {
/*      */     try
/*      */     {
/* 4055 */       this.general_mon.enter();
/*      */       
/* 4057 */       if (this.piecePicker.getNbPiecesDone() > 10)
/*      */       {
/* 4059 */         List<Integer> pieces = (List)getUserData(KEY_ALLOWED_FAST_RECEIVED);
/*      */         
/* 4061 */         if (pieces != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4067 */           setUserData(KEY_ALLOWED_FAST_RECEIVED, null);
/*      */           
/* 4069 */           calculatePiecePriorities();
/*      */         }
/*      */       }
/*      */       
/* 4073 */       BitFlags flags = this.peerHavePieces;
/*      */       
/* 4075 */       if ((flags != null) && (flags.nbSet >= 10))
/*      */       {
/* 4077 */         int[][] pieces = (int[][])getUserData(KEY_ALLOWED_FAST_SENT);
/*      */         
/* 4079 */         if (pieces != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4085 */           setUserData(KEY_ALLOWED_FAST_SENT, null);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 4090 */       this.general_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void registerForMessageHandling()
/*      */   {
/* 4097 */     this.connection.getIncomingMessageQueue().registerQueueListener(new com.aelitis.azureus.core.networkmanager.IncomingMessageQueue.MessageQueueListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public final boolean messageReceived(Message message)
/*      */       {
/*      */ 
/* 4104 */         if (Logger.isEnabled()) {
/* 4105 */           Logger.log(new LogEvent(PEPeerTransportProtocol.this, LogIDs.NET, "Received [" + message.getDescription() + "] message"));
/*      */         }
/* 4107 */         long now = SystemTime.getCurrentTime();
/* 4108 */         PEPeerTransportProtocol.this.last_message_received_time = now;
/* 4109 */         if (message.getType() == 1) {
/* 4110 */           PEPeerTransportProtocol.this.last_data_message_received_time = now;
/*      */         }
/*      */         
/* 4113 */         String message_id = message.getID();
/*      */         
/* 4115 */         if (message_id.equals("BT_PIECE")) {
/* 4116 */           PEPeerTransportProtocol.this.decodePiece((BTPiece)message);
/* 4117 */           return true;
/*      */         }
/*      */         
/* 4120 */         if (PEPeerTransportProtocol.this.closing) {
/* 4121 */           message.destroy();
/* 4122 */           return true;
/*      */         }
/*      */         
/* 4125 */         if (message_id.equals("BT_KEEP_ALIVE")) {
/* 4126 */           message.destroy();
/*      */           
/*      */ 
/* 4129 */           if (!PEPeerTransportProtocol.this.message_limiter.countIncomingMessage(message.getID(), 6, 60000)) {
/* 4130 */             System.out.println(PEPeerTransportProtocol.this.manager.getDisplayName() + ": Incoming keep-alive message flood detected, dropping spamming peer connection." + PEPeerTransportProtocol.this);
/* 4131 */             PEPeerTransportProtocol.this.closeConnectionInternally("Incoming keep-alive message flood detected, dropping spamming peer connection.");
/*      */           }
/*      */           
/* 4134 */           return true;
/*      */         }
/*      */         
/* 4137 */         if (message_id.equals("BT_HANDSHAKE")) {
/* 4138 */           PEPeerTransportProtocol.this.decodeBTHandshake((BTHandshake)message);
/* 4139 */           return true;
/*      */         }
/*      */         
/* 4142 */         if (message_id.equals("AZ_HANDSHAKE")) {
/* 4143 */           PEPeerTransportProtocol.this.decodeAZHandshake((AZHandshake)message);
/* 4144 */           return true;
/*      */         }
/*      */         
/* 4147 */         if (message_id.equals("lt_handshake")) {
/* 4148 */           PEPeerTransportProtocol.this.decodeLTHandshake((LTHandshake)message);
/* 4149 */           return true;
/*      */         }
/*      */         
/* 4152 */         if (message_id.equals("BT_BITFIELD")) {
/* 4153 */           PEPeerTransportProtocol.this.decodeBitfield((BTBitfield)message);
/* 4154 */           return true;
/*      */         }
/*      */         
/* 4157 */         if (message_id.equals("BT_CHOKE")) {
/* 4158 */           PEPeerTransportProtocol.this.decodeChoke((com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTChoke)message);
/* 4159 */           if (PEPeerTransportProtocol.this.choking_other_peer) {
/* 4160 */             PEPeerTransportProtocol.this.connection.enableEnhancedMessageProcessing(false, PEPeerTransportProtocol.this.manager.getPartitionID());
/*      */           }
/* 4162 */           return true;
/*      */         }
/*      */         
/* 4165 */         if (message_id.equals("BT_UNCHOKE")) {
/* 4166 */           PEPeerTransportProtocol.this.decodeUnchoke((com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUnchoke)message);
/* 4167 */           PEPeerTransportProtocol.this.connection.enableEnhancedMessageProcessing(true, PEPeerTransportProtocol.this.manager.getPartitionID());
/* 4168 */           return true;
/*      */         }
/*      */         
/* 4171 */         if (message_id.equals("BT_INTERESTED")) {
/* 4172 */           PEPeerTransportProtocol.this.decodeInterested((BTInterested)message);
/* 4173 */           return true;
/*      */         }
/*      */         
/* 4176 */         if (message_id.equals("BT_UNINTERESTED")) {
/* 4177 */           PEPeerTransportProtocol.this.decodeUninterested((com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTUninterested)message);
/* 4178 */           return true;
/*      */         }
/*      */         
/* 4181 */         if (message_id.equals("BT_HAVE")) {
/* 4182 */           PEPeerTransportProtocol.this.decodeHave((BTHave)message);
/* 4183 */           return true;
/*      */         }
/*      */         
/* 4186 */         if (message_id.equals("BT_REQUEST")) {
/* 4187 */           PEPeerTransportProtocol.this.decodeRequest((BTRequest)message);
/* 4188 */           return true;
/*      */         }
/*      */         
/* 4191 */         if (message_id.equals("BT_CANCEL")) {
/* 4192 */           PEPeerTransportProtocol.this.decodeCancel((BTCancel)message);
/* 4193 */           return true;
/*      */         }
/*      */         
/* 4196 */         if (message_id.equals("BT_SUGGEST_PIECE")) {
/* 4197 */           PEPeerTransportProtocol.this.decodeSuggestPiece((com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTSuggestPiece)message);
/* 4198 */           return true;
/*      */         }
/*      */         
/* 4201 */         if (message_id.equals("BT_HAVE_ALL")) {
/* 4202 */           PEPeerTransportProtocol.this.decodeHaveAll((com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTHaveAll)message);
/* 4203 */           return true;
/*      */         }
/*      */         
/* 4206 */         if (message_id.equals("BT_HAVE_NONE")) {
/* 4207 */           PEPeerTransportProtocol.this.decodeHaveNone((BTHaveNone)message);
/* 4208 */           return true;
/*      */         }
/*      */         
/* 4211 */         if (message_id.equals("BT_REJECT_REQUEST")) {
/* 4212 */           PEPeerTransportProtocol.this.decodeRejectRequest((BTRejectRequest)message);
/* 4213 */           return true;
/*      */         }
/*      */         
/* 4216 */         if (message_id.equals("BT_ALLOWED_FAST")) {
/* 4217 */           PEPeerTransportProtocol.this.decodeAllowedFast((BTAllowedFast)message);
/* 4218 */           return true;
/*      */         }
/*      */         
/* 4221 */         if (message_id.equals("BT_DHT_PORT")) {
/* 4222 */           PEPeerTransportProtocol.this.decodeMainlineDHTPort((BTDHTPort)message);
/* 4223 */           return true;
/*      */         }
/*      */         
/* 4226 */         if (message_id.equals("AZ_PEER_EXCHANGE")) {
/* 4227 */           PEPeerTransportProtocol.this.decodePeerExchange((com.aelitis.azureus.core.peermanager.messaging.azureus.AZPeerExchange)message);
/* 4228 */           return true;
/*      */         }
/*      */         
/* 4231 */         if (message_id.equals("ut_pex")) {
/* 4232 */           PEPeerTransportProtocol.this.decodePeerExchange((com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTPeerExchange)message);
/* 4233 */           return true;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 4238 */         if ((message instanceof AZStylePeerExchange)) {
/* 4239 */           PEPeerTransportProtocol.this.decodePeerExchange((AZStylePeerExchange)message);
/* 4240 */           return true;
/*      */         }
/*      */         
/* 4243 */         if (message_id.equals("AZ_REQUEST_HINT")) {
/* 4244 */           PEPeerTransportProtocol.this.decodeAZRequestHint((AZRequestHint)message);
/* 4245 */           return true;
/*      */         }
/*      */         
/* 4248 */         if (message_id.equals("AZ_HAVE")) {
/* 4249 */           PEPeerTransportProtocol.this.decodeAZHave((AZHave)message);
/* 4250 */           return true;
/*      */         }
/*      */         
/* 4253 */         if (message_id.equals("AZ_BAD_PIECE")) {
/* 4254 */           PEPeerTransportProtocol.this.decodeAZBadPiece((AZBadPiece)message);
/* 4255 */           return true;
/*      */         }
/*      */         
/* 4258 */         if (message_id.equals("AZ_STAT_REQ")) {
/* 4259 */           PEPeerTransportProtocol.this.decodeAZStatsRequest((AZStatRequest)message);
/* 4260 */           return true;
/*      */         }
/*      */         
/* 4263 */         if (message_id.equals("AZ_STAT_REP")) {
/* 4264 */           PEPeerTransportProtocol.this.decodeAZStatsReply((AZStatReply)message);
/* 4265 */           return true;
/*      */         }
/*      */         
/* 4268 */         if (message_id.equals("ut_metadata")) {
/* 4269 */           PEPeerTransportProtocol.this.decodeMetaData((UTMetaData)message);
/* 4270 */           return true;
/*      */         }
/*      */         
/* 4273 */         if (message_id.equals("AZ_METADATA")) {
/* 4274 */           PEPeerTransportProtocol.this.decodeMetaData((com.aelitis.azureus.core.peermanager.messaging.azureus.AZMetaData)message);
/* 4275 */           return true;
/*      */         }
/*      */         
/* 4278 */         if (message_id.equals("upload_only")) {
/* 4279 */           PEPeerTransportProtocol.this.decodeUploadOnly((com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTUploadOnly)message);
/* 4280 */           return true;
/*      */         }
/*      */         
/* 4283 */         return false;
/*      */       }
/*      */       
/*      */       public final void protocolBytesReceived(int byte_count)
/*      */       {
/* 4288 */         PEPeerTransportProtocol.this.peer_stats.protocolBytesReceived(byte_count);
/* 4289 */         PEPeerTransportProtocol.this.manager.protocolBytesReceived(PEPeerTransportProtocol.this, byte_count);
/*      */       }
/*      */       
/*      */ 
/*      */       public final void dataBytesReceived(int byte_count)
/*      */       {
/* 4295 */         PEPeerTransportProtocol.this.last_data_message_received_time = SystemTime.getCurrentTime();
/*      */         
/*      */ 
/* 4298 */         PEPeerTransportProtocol.this.peer_stats.dataBytesReceived(byte_count);
/*      */         
/* 4300 */         PEPeerTransportProtocol.this.manager.dataBytesReceived(PEPeerTransportProtocol.this, byte_count);
/*      */       }
/*      */       
/*      */ 
/*      */       public boolean isPriority()
/*      */       {
/* 4306 */         return false;
/*      */ 
/*      */       }
/*      */       
/*      */ 
/* 4311 */     });
/* 4312 */     this.connection.getOutgoingMessageQueue().registerQueueListener(new com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue.MessageQueueListener() {
/* 4313 */       public final boolean messageAdded(Message message) { return true; }
/*      */       
/*      */       public final void messageQueued(Message message) {}
/*      */       
/*      */       public final void messageRemoved(Message message) {}
/*      */       
/*      */       public final void messageSent(Message message)
/*      */       {
/* 4321 */         long now = SystemTime.getCurrentTime();
/* 4322 */         PEPeerTransportProtocol.this.last_message_sent_time = now;
/*      */         
/* 4324 */         if (message.getType() == 1) {
/* 4325 */           PEPeerTransportProtocol.this.last_data_message_sent_time = now;
/*      */         }
/*      */         
/* 4328 */         String message_id = message.getID();
/*      */         
/* 4330 */         if (message_id.equals("BT_UNCHOKE"))
/*      */         {
/* 4332 */           PEPeerTransportProtocol.this.connection.enableEnhancedMessageProcessing(true, PEPeerTransportProtocol.this.manager.getPartitionID());
/*      */         }
/* 4334 */         else if (message_id.equals("BT_CHOKE"))
/*      */         {
/* 4336 */           if (PEPeerTransportProtocol.this.effectively_choked_by_other_peer)
/*      */           {
/* 4338 */             PEPeerTransportProtocol.this.connection.enableEnhancedMessageProcessing(false, PEPeerTransportProtocol.this.manager.getPartitionID());
/*      */           }
/* 4340 */         } else if (message_id.equals("BT_REQUEST"))
/*      */         {
/* 4342 */           BTRequest request = (BTRequest)message;
/*      */           
/* 4344 */           DiskManagerReadRequest dm_request = PEPeerTransportProtocol.this.lookupRequest(request.getPieceNumber(), request.getPieceOffset(), request.getLength());
/*      */           
/* 4346 */           if (dm_request != null)
/*      */           {
/* 4348 */             dm_request.setTimeSent(SystemTime.getMonotonousTime());
/*      */           }
/*      */         }
/*      */         
/* 4352 */         if (Logger.isEnabled()) {
/* 4353 */           Logger.log(new LogEvent(PEPeerTransportProtocol.this, LogIDs.NET, "Sent [" + message.getDescription() + "] message"));
/*      */         }
/*      */       }
/*      */       
/*      */       public final void protocolBytesSent(int byte_count)
/*      */       {
/* 4359 */         PEPeerTransportProtocol.this.peer_stats.protocolBytesSent(byte_count);
/* 4360 */         PEPeerTransportProtocol.this.manager.protocolBytesSent(PEPeerTransportProtocol.this, byte_count);
/*      */       }
/*      */       
/*      */       public final void dataBytesSent(int byte_count)
/*      */       {
/* 4365 */         PEPeerTransportProtocol.this.peer_stats.dataBytesSent(byte_count);
/* 4366 */         PEPeerTransportProtocol.this.manager.dataBytesSent(PEPeerTransportProtocol.this, byte_count);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void flush() {}
/* 4372 */     });
/* 4373 */     this.connection.addRateLimiter(this.manager.getUploadLimitedRateGroup(), true);
/* 4374 */     this.connection.addRateLimiter(this.manager.getDownloadLimitedRateGroup(), false);
/*      */     
/* 4376 */     this.connection.startMessageProcessing();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addRateLimiter(com.aelitis.azureus.core.networkmanager.LimitedRateGroup limiter, boolean upload)
/*      */   {
/* 4384 */     this.connection.addRateLimiter(limiter, upload);
/*      */   }
/*      */   
/*      */ 
/*      */   public com.aelitis.azureus.core.networkmanager.LimitedRateGroup[] getRateLimiters(boolean upload)
/*      */   {
/* 4390 */     return this.connection.getRateLimiters(upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeRateLimiter(com.aelitis.azureus.core.networkmanager.LimitedRateGroup limiter, boolean upload)
/*      */   {
/* 4398 */     this.connection.removeRateLimiter(limiter, upload);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setUploadDisabled(Object key, boolean disabled)
/*      */   {
/* 4406 */     synchronized (this)
/*      */     {
/* 4408 */       if (this.upload_disabled_set == null)
/*      */       {
/* 4410 */         if (disabled)
/*      */         {
/* 4412 */           this.upload_disabled_set = new HashSet();
/*      */           
/* 4414 */           this.upload_disabled_set.add(key);
/*      */         }
/*      */         else
/*      */         {
/* 4418 */           Debug.out("derp");
/*      */         }
/*      */         
/*      */       }
/* 4422 */       else if (disabled)
/*      */       {
/* 4424 */         if (!this.upload_disabled_set.add(key))
/*      */         {
/* 4426 */           Debug.out("derp");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 4431 */         if (!this.upload_disabled_set.remove(key))
/*      */         {
/* 4433 */           Debug.out("derp");
/*      */         }
/*      */         
/* 4436 */         if (this.upload_disabled_set.size() == 0)
/*      */         {
/* 4438 */           this.upload_disabled_set = null;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 4443 */       this.is_upload_disabled = (this.upload_disabled_set != null);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDownloadDisabled(Object key, boolean disabled)
/*      */   {
/* 4454 */     boolean check = false;
/*      */     
/* 4456 */     synchronized (this)
/*      */     {
/* 4458 */       if (this.download_disabled_set == null)
/*      */       {
/* 4460 */         if (disabled)
/*      */         {
/* 4462 */           this.download_disabled_set = new HashSet();
/*      */           
/* 4464 */           this.download_disabled_set.add(key);
/*      */         }
/*      */         else
/*      */         {
/* 4468 */           Debug.out("derp");
/*      */         }
/*      */         
/*      */       }
/* 4472 */       else if (disabled)
/*      */       {
/* 4474 */         if (!this.download_disabled_set.add(key))
/*      */         {
/* 4476 */           Debug.out("derp");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 4481 */         if (!this.download_disabled_set.remove(key))
/*      */         {
/* 4483 */           Debug.out("derp");
/*      */         }
/*      */         
/* 4486 */         if (this.download_disabled_set.size() == 0)
/*      */         {
/* 4488 */           this.download_disabled_set = null;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/* 4493 */       boolean old = this.is_download_disabled;
/*      */       
/* 4495 */       this.is_download_disabled = (this.download_disabled_set != null);
/*      */       
/* 4497 */       check = old != this.is_download_disabled;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 4502 */     if (check) {
/* 4503 */       checkInterested();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isUploadDisabled()
/*      */   {
/* 4510 */     return this.is_upload_disabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isUploadDisabled(Object key)
/*      */   {
/* 4517 */     synchronized (this)
/*      */     {
/* 4519 */       if (this.upload_disabled_set == null)
/*      */       {
/* 4521 */         return false;
/*      */       }
/*      */       
/* 4524 */       return this.upload_disabled_set.contains(key);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isDownloadDisabled()
/*      */   {
/* 4531 */     return this.is_download_disabled;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isDownloadDisabled(Object key)
/*      */   {
/* 4538 */     synchronized (this)
/*      */     {
/* 4540 */       if (this.download_disabled_set == null)
/*      */       {
/* 4542 */         return false;
/*      */       }
/*      */       
/* 4545 */       return this.download_disabled_set.contains(key);
/*      */     }
/*      */   }
/*      */   
/*      */   public org.gudy.azureus2.plugins.network.Connection getPluginConnection() {
/* 4550 */     return this.plugin_connection;
/*      */   }
/*      */   
/*      */   public Message[] getSupportedMessages()
/*      */   {
/* 4555 */     return this.supported_messages;
/*      */   }
/*      */   
/*      */   public boolean supportsMessaging()
/*      */   {
/* 4560 */     return this.supported_messages != null;
/*      */   }
/*      */   
/*      */   public int getMessagingMode()
/*      */   {
/* 4565 */     return this.messaging_mode;
/*      */   }
/*      */   
/*      */   public byte[] getHandshakeReservedBytes() {
/* 4569 */     return this.handshake_reserved_bytes;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setHaveAggregationEnabled(boolean enabled)
/*      */   {
/* 4576 */     this.have_aggregation_disabled = (!enabled);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean hasReceivedBitField()
/*      */   {
/* 4582 */     return this.received_bitfield;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getUnchokedForMillis()
/*      */   {
/* 4588 */     long time = this.effectively_unchoked_time;
/*      */     
/* 4590 */     if ((this.effectively_choked_by_other_peer) || (time < 0L))
/*      */     {
/* 4592 */       return -1L;
/*      */     }
/*      */     
/* 4595 */     return SystemTime.getMonotonousTime() - time;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLatency()
/*      */   {
/* 4601 */     return this.request_latency;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getEncryption()
/*      */   {
/* 4607 */     Transport transport = this.connection.getTransport();
/*      */     
/* 4609 */     if (transport == null)
/*      */     {
/* 4611 */       return "";
/*      */     }
/*      */     
/* 4614 */     return transport.getEncryption(false);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProtocol()
/*      */   {
/* 4620 */     Transport transport = this.connection.getTransport();
/*      */     
/*      */     String result;
/*      */     String result;
/* 4624 */     if (transport == null)
/*      */     {
/* 4626 */       result = "";
/*      */     }
/*      */     else
/*      */     {
/* 4630 */       result = transport.getProtocol();
/*      */     }
/*      */     
/* 4633 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProtocolQualifier()
/*      */   {
/* 4639 */     return (String)this.connection.getEndpoint().getProperty("AEProxyAddressMapper.prot.qual");
/*      */   }
/*      */   
/*      */ 
/*      */   public void addListener(PEPeerListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 4647 */       this.peer_listeners_mon.enter();
/*      */       
/* 4649 */       if (this.peer_listeners_cow == null)
/*      */       {
/* 4651 */         this.peer_listeners_cow = new ArrayList();
/*      */       }
/*      */       
/* 4654 */       List new_listeners = new ArrayList(this.peer_listeners_cow);
/*      */       
/* 4656 */       new_listeners.add(listener);
/*      */       
/* 4658 */       this.peer_listeners_cow = new_listeners;
/*      */     }
/*      */     finally
/*      */     {
/* 4662 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeListener(PEPeerListener listener)
/*      */   {
/*      */     try
/*      */     {
/* 4671 */       this.peer_listeners_mon.enter();
/*      */       
/* 4673 */       if (this.peer_listeners_cow != null)
/*      */       {
/* 4675 */         List new_listeners = new ArrayList(this.peer_listeners_cow);
/*      */         
/* 4677 */         new_listeners.remove(listener);
/*      */         
/* 4679 */         if (new_listeners.isEmpty())
/*      */         {
/* 4681 */           new_listeners = null;
/*      */         }
/*      */         
/* 4684 */         this.peer_listeners_cow = new_listeners;
/*      */       }
/*      */     }
/*      */     finally {
/* 4688 */       this.peer_listeners_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   private void changePeerState(int new_state)
/*      */   {
/* 4694 */     this.current_peer_state = new_state;
/*      */     
/* 4696 */     if (this.current_peer_state == 30) {
/* 4697 */       doPostHandshakeProcessing();
/*      */     }
/*      */     
/* 4700 */     List peer_listeners_ref = this.peer_listeners_cow;
/*      */     
/* 4702 */     if (peer_listeners_ref != null)
/*      */     {
/* 4704 */       for (int i = 0; i < peer_listeners_ref.size(); i++)
/*      */       {
/* 4706 */         PEPeerListener l = (PEPeerListener)peer_listeners_ref.get(i);
/*      */         
/* 4708 */         l.stateChanged(this, this.current_peer_state);
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
/*      */   private void doPostHandshakeProcessing()
/*      */   {
/* 4722 */     if (this.manager.isPeerExchangeEnabled())
/*      */     {
/* 4724 */       PeerExchangerItem pex_item = this.peer_exchange_item;
/*      */       
/* 4726 */       if ((pex_item == null) && (canBePeerExchanged())) {
/* 4727 */         pex_item = this.peer_exchange_item = this.manager.createPeerExchangeConnection(this);
/*      */       }
/*      */       
/* 4730 */       if (pex_item != null)
/*      */       {
/*      */ 
/*      */ 
/* 4734 */         if ((this.ut_pex_enabled) || (peerSupportsMessageType("AZ_PEER_EXCHANGE")))
/*      */         {
/* 4736 */           this.peer_exchange_supported = true;
/*      */           
/* 4738 */           pex_item.enableStateMaintenance();
/*      */         }
/*      */         else
/*      */         {
/* 4742 */           com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder encoder = this.connection.getOutgoingMessageQueue().getEncoder();
/*      */           
/* 4744 */           if (((encoder instanceof LTMessageEncoder)) && (((LTMessageEncoder)encoder).hasCustomExtensionHandler(1)))
/*      */           {
/*      */ 
/* 4747 */             this.peer_exchange_supported = true;
/*      */             
/* 4749 */             pex_item.enableStateMaintenance();
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 4755 */             pex_item.disableStateMaintenance();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 4761 */     this.request_hint_supported = peerSupportsMessageType("AZ_REQUEST_HINT");
/* 4762 */     this.bad_piece_supported = peerSupportsMessageType("AZ_BAD_PIECE");
/* 4763 */     this.stats_request_supported = peerSupportsMessageType("AZ_STAT_REQ");
/* 4764 */     this.stats_reply_supported = peerSupportsMessageType("AZ_STAT_REP");
/* 4765 */     this.az_metadata_supported = peerSupportsMessageType("AZ_METADATA");
/*      */     
/* 4767 */     if (this.is_metadata_download)
/*      */     {
/* 4769 */       if (this.az_metadata_supported)
/*      */       {
/* 4771 */         int mds = this.manager.getTorrentInfoDictSize();
/*      */         
/* 4773 */         if (mds > 0)
/*      */         {
/* 4775 */           spoofMDAvailability(mds);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean canBePeerExchanged()
/*      */   {
/* 4784 */     if (this.client_peer_id != null)
/*      */     {
/*      */ 
/*      */ 
/* 4788 */       boolean ok = !this.client_peer_id.startsWith("CacheLogic");
/*      */       
/*      */ 
/*      */ 
/* 4792 */       return ok;
/*      */     }
/*      */     
/*      */ 
/* 4796 */     Debug.out("No client peer id!");
/*      */     
/* 4798 */     return false;
/*      */   }
/*      */   
/*      */   private boolean peerSupportsMessageType(String message_id)
/*      */   {
/* 4803 */     if (this.supported_messages != null) {
/* 4804 */       for (int i = 0; i < this.supported_messages.length; i++) {
/* 4805 */         if (this.supported_messages[i].getID().equals(message_id)) return true;
/*      */       }
/*      */     }
/* 4808 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void updatePeerExchange()
/*      */   {
/* 4814 */     if (this.current_peer_state != 30) return;
/* 4815 */     if (!this.peer_exchange_supported) return;
/* 4816 */     PeerExchangerItem pex_item = this.peer_exchange_item;
/*      */     
/* 4818 */     if ((pex_item != null) && (this.manager.isPeerExchangeEnabled()))
/*      */     {
/* 4820 */       if (this.peer_item_identity.getNetwork() == "Public")
/*      */       {
/* 4822 */         PeerItem[] adds = pex_item.getNewlyAddedPeerConnections("Public");
/* 4823 */         PeerItem[] drops = pex_item.getNewlyDroppedPeerConnections("Public");
/*      */         
/* 4825 */         if (((adds != null) && (adds.length > 0)) || ((drops != null) && (drops.length > 0))) {
/* 4826 */           if (this.ut_pex_enabled) {
/* 4827 */             this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTPeerExchange(adds, drops, null, (byte)0), false);
/*      */           }
/*      */           else {
/* 4830 */             this.connection.getOutgoingMessageQueue().addMessage(new com.aelitis.azureus.core.peermanager.messaging.azureus.AZPeerExchange(this.manager.getHash(), adds, drops, this.other_peer_pex_version), false);
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/* 4835 */         com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder encoder = this.connection.getOutgoingMessageQueue().getEncoder();
/*      */         
/* 4837 */         if ((encoder instanceof LTMessageEncoder))
/*      */         {
/* 4839 */           ((LTMessageEncoder)encoder).handleCustomExtension(1, new Object[] { pex_item });
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void decodePeerExchange(AZStylePeerExchange exchange)
/*      */   {
/* 4851 */     PeerItem[] added = (exchange instanceof com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTPeerExchange) ? ((com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTPeerExchange)exchange).getAddedPeers((!this.manager.isSeeding()) && (!org.gudy.azureus2.core3.util.Constants.DOWNLOAD_SOURCES_PRETEND_COMPLETE)) : exchange.getAddedPeers();
/* 4852 */     PeerItem[] dropped = exchange.getDroppedPeers();
/*      */     
/* 4854 */     int max_added = exchange.getMaxAllowedPeersPerVolley(!this.has_received_initial_pex, true);
/* 4855 */     int max_dropped = exchange.getMaxAllowedPeersPerVolley(!this.has_received_initial_pex, false);
/*      */     
/* 4857 */     exchange.destroy();
/*      */     
/*      */ 
/*      */ 
/* 4861 */     if (!this.message_limiter.countIncomingMessage(exchange.getID(), 7, 120000)) {
/* 4862 */       System.out.println(this.manager.getDisplayName() + ": Incoming PEX message flood detected, dropping spamming peer connection." + this);
/* 4863 */       closeConnectionInternally("Incoming PEX message flood detected, dropping spamming peer connection.");
/* 4864 */       return;
/*      */     }
/*      */     
/* 4867 */     if (((added != null) && (added.length > max_added)) || ((dropped != null) && (dropped.length > max_dropped)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 4873 */       if (Logger.isEnabled()) {
/* 4874 */         Logger.log(new LogEvent(this, LOGID, "Invalid PEX message received: too large, ignoring this exchange. (added=" + (added == null ? 0 : added.length) + ",dropped=" + (dropped == null ? 0 : dropped.length) + ")"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 4879 */       added = null;
/* 4880 */       dropped = null;
/*      */     }
/*      */     
/* 4883 */     this.has_received_initial_pex = true;
/*      */     
/* 4885 */     PeerExchangerItem pex_item = this.peer_exchange_item;
/*      */     
/* 4887 */     if ((this.peer_exchange_supported) && (pex_item != null) && (this.manager.isPeerExchangeEnabled())) {
/* 4888 */       if (added != null) {
/* 4889 */         for (int i = 0; i < added.length; i++) {
/* 4890 */           PeerItem pi = added[i];
/* 4891 */           this.manager.peerDiscovered(this, pi);
/* 4892 */           pex_item.addConnectedPeer(pi);
/*      */         }
/*      */       }
/*      */       
/* 4896 */       if (dropped != null) {
/* 4897 */         for (int i = 0; i < dropped.length; i++) {
/* 4898 */           pex_item.dropConnectedPeer(dropped[i]);
/*      */         }
/*      */         
/*      */       }
/*      */     }
/* 4903 */     else if (Logger.isEnabled()) {
/* 4904 */       Logger.log(new LogEvent(this, LOGID, "Peer Exchange disabled for this download, dropping received exchange message"));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void decodeMetaData(AZUTMetaData metadata)
/*      */   {
/*      */     try
/*      */     {
/* 4915 */       int BLOCK_SIZE = 16384;
/*      */       
/* 4917 */       int type = metadata.getMessageType();
/*      */       
/* 4919 */       if (type == 0)
/*      */       {
/* 4921 */         if (!this.manager.isPrivateTorrent())
/*      */         {
/* 4923 */           int piece = metadata.getPiece();
/*      */           
/* 4925 */           int total_size = this.manager.getTorrentInfoDictSize();
/*      */           
/* 4927 */           byte[] data = total_size <= 0 ? null : this.manager.getAdapter().getTorrentInfoDict(this);
/*      */           
/*      */ 
/*      */ 
/* 4931 */           int offset = piece * 16384;
/*      */           UTMetaData reply;
/* 4933 */           UTMetaData reply; if ((this.is_metadata_download) || (data == null) || (offset >= data.length))
/*      */           {
/* 4935 */             reply = new UTMetaData(piece, null, 0, (byte)1);
/*      */           }
/*      */           else
/*      */           {
/* 4939 */             int to_send = Math.min(data.length - offset, 16384);
/*      */             
/*      */ 
/*      */ 
/* 4943 */             reply = new UTMetaData(piece, ByteBuffer.wrap(data, offset, to_send), total_size, (byte)1);
/*      */           }
/*      */           
/* 4946 */           this.connection.getOutgoingMessageQueue().addMessage(reply, false);
/*      */         }
/* 4948 */       } else if (type == 1)
/*      */       {
/* 4950 */         int piece_number = metadata.getPiece();
/* 4951 */         DirectByteBuffer data = metadata.getMetadata();
/*      */         
/* 4953 */         int data_size = data.remaining((byte)9);
/*      */         
/* 4955 */         int total_size = this.manager.getTorrentInfoDictSize();
/*      */         
/* 4957 */         int piece_count = (total_size + 16384 - 1) / 16384;
/* 4958 */         int last_piece_size = total_size % 16384;
/*      */         
/* 4960 */         if (last_piece_size == 0)
/*      */         {
/* 4962 */           last_piece_size = 16384;
/*      */         }
/*      */         
/* 4965 */         boolean good = false;
/*      */         
/* 4967 */         if (piece_number < piece_count)
/*      */         {
/* 4969 */           int expected_size = piece_number == piece_count - 1 ? last_piece_size : 16384;
/*      */           
/* 4971 */           if (data_size == expected_size)
/*      */           {
/* 4973 */             DiskManagerReadRequest request = this.manager.createDiskManagerRequest(piece_number, 0, 16384);
/*      */             
/* 4975 */             if (hasBeenRequested(request))
/*      */             {
/* 4977 */               good = true;
/*      */               
/* 4979 */               metadata.setMetadata(null);
/*      */               
/* 4981 */               removeRequest(request);
/*      */               
/* 4983 */               long now = SystemTime.getCurrentTime();
/*      */               
/* 4985 */               resetRequestsTime(now);
/*      */               
/* 4987 */               this.manager.writeBlock(piece_number, 0, data, this, false);
/*      */               
/* 4989 */               if ((this.last_good_data_time != -1L) && (now - this.last_good_data_time <= 60000L))
/*      */               {
/* 4991 */                 setSnubbed(false);
/*      */               }
/*      */               
/* 4994 */               this.last_good_data_time = now;
/*      */               
/* 4996 */               requests_completed += 1;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 5001 */         if (!good)
/*      */         {
/* 5003 */           this.peer_stats.bytesDiscarded(data_size);
/*      */           
/* 5005 */           this.manager.discarded(this, data_size);
/*      */           
/* 5007 */           requests_discarded += 1;
/*      */           
/* 5009 */           printRequestStats();
/*      */           
/* 5011 */           if (Logger.isEnabled()) {
/* 5012 */             Logger.log(new LogEvent(this, LOGID, 3, "metadata piece discarded as invalid."));
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 5018 */         int piece = metadata.getPiece();
/*      */         
/* 5020 */         DiskManagerReadRequest request = this.manager.createDiskManagerRequest(piece, 0, 16384);
/*      */         
/* 5022 */         if (hasBeenRequested(request))
/*      */         {
/* 5024 */           removeRequest(request);
/*      */           
/* 5026 */           this.manager.requestCanceled(request);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 5031 */       metadata.destroy();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void decodeUploadOnly(com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep.UTUploadOnly message)
/*      */   {
/*      */     try
/*      */     {
/* 5040 */       boolean ulo = message.isUploadOnly();
/*      */       
/* 5042 */       if (ulo)
/*      */       {
/* 5044 */         this.relativeSeeding = ((byte)(this.relativeSeeding | 0x1));
/*      */       }
/*      */       else
/*      */       {
/* 5048 */         this.relativeSeeding = ((byte)(this.relativeSeeding & 0xFFFFFFFE));
/*      */       }
/*      */     }
/*      */     finally {
/* 5052 */       message.destroy();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean sendRequestHint(int piece_number, int offset, int length, int life)
/*      */   {
/* 5063 */     if (this.request_hint_supported)
/*      */     {
/* 5065 */       AZRequestHint rh = new AZRequestHint(piece_number, offset, length, life, this.other_peer_az_request_hint_version);
/*      */       
/* 5067 */       this.connection.getOutgoingMessageQueue().addMessage(rh, false);
/*      */       
/* 5069 */       return true;
/*      */     }
/*      */     
/*      */ 
/* 5073 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void decodeSuggestPiece(com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTSuggestPiece hint)
/*      */   {
/* 5081 */     int piece_number = hint.getPieceNumber();
/* 5082 */     int offset = 0;
/* 5083 */     int length = this.manager.getPieceLength(piece_number);
/*      */     
/* 5085 */     hint.destroy();
/*      */     
/* 5087 */     if (this.manager.validateHintRequest(this, piece_number, offset, length))
/*      */     {
/* 5089 */       if (this.request_hint == null)
/*      */       {
/* 5091 */         this.request_hint = new int[] { piece_number, offset, length };
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void decodeAZRequestHint(AZRequestHint hint)
/*      */   {
/* 5100 */     int piece_number = hint.getPieceNumber();
/* 5101 */     int offset = hint.getOffset();
/* 5102 */     int length = hint.getLength();
/* 5103 */     int life = hint.getLife();
/*      */     
/* 5105 */     hint.destroy();
/*      */     
/* 5107 */     if (life > 150000)
/*      */     {
/* 5109 */       life = 150000;
/*      */     }
/*      */     
/* 5112 */     if (this.manager.validateHintRequest(this, piece_number, offset, length))
/*      */     {
/* 5114 */       if (this.request_hint == null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 5119 */         this.request_hint = new int[] { piece_number, offset, length };
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] getRequestHint()
/*      */   {
/* 5127 */     return this.request_hint;
/*      */   }
/*      */   
/*      */ 
/*      */   public void clearRequestHint()
/*      */   {
/* 5133 */     this.request_hint = null;
/*      */   }
/*      */   
/* 5136 */   public PeerItem getPeerItemIdentity() { return this.peer_item_identity; }
/*      */   
/*      */ 
/*      */   public int[] getReservedPieceNumbers()
/*      */   {
/* 5141 */     return this.reserved_pieces;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addReservedPieceNumber(int piece_number)
/*      */   {
/* 5147 */     int[] existing = this.reserved_pieces;
/*      */     
/* 5149 */     if (existing == null)
/*      */     {
/* 5151 */       this.reserved_pieces = new int[] { piece_number };
/*      */     }
/*      */     else
/*      */     {
/* 5155 */       int[] updated = new int[existing.length + 1];
/*      */       
/* 5157 */       System.arraycopy(existing, 0, updated, 0, existing.length);
/*      */       
/* 5159 */       updated[existing.length] = piece_number;
/*      */       
/* 5161 */       this.reserved_pieces = updated;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeReservedPieceNumber(int piece_number)
/*      */   {
/* 5168 */     int[] existing = this.reserved_pieces;
/*      */     
/* 5170 */     if (existing != null)
/*      */     {
/* 5172 */       if (existing.length == 1)
/*      */       {
/* 5174 */         if (existing[0] == piece_number)
/*      */         {
/* 5176 */           this.reserved_pieces = null;
/*      */         }
/*      */       }
/*      */       else {
/* 5180 */         int[] updated = new int[existing.length - 1];
/*      */         
/* 5182 */         int pos = 0;
/* 5183 */         boolean found = false;
/*      */         
/* 5185 */         for (int i = 0; i < existing.length; i++)
/*      */         {
/* 5187 */           int pn = existing[i];
/*      */           
/* 5189 */           if ((found) || (pn != piece_number))
/*      */           {
/* 5191 */             if (pos == updated.length)
/*      */             {
/* 5193 */               return;
/*      */             }
/*      */             
/* 5196 */             updated[(pos++)] = pn;
/*      */           }
/*      */           else
/*      */           {
/* 5200 */             found = true;
/*      */           }
/*      */         }
/*      */         
/* 5204 */         this.reserved_pieces = updated;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getIncomingRequestCount()
/*      */   {
/* 5212 */     if (this.outgoing_piece_message_handler == null) {
/* 5213 */       return 0;
/*      */     }
/*      */     
/* 5216 */     return this.outgoing_piece_message_handler.getRequestCount();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOutgoingRequestCount()
/*      */   {
/* 5222 */     return getNbRequests();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getOutboundDataQueueSize()
/*      */   {
/* 5228 */     return this.connection.getOutgoingMessageQueue().getTotalSize();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isStalledPendingLoad()
/*      */   {
/* 5234 */     if (this.outgoing_piece_message_handler == null)
/*      */     {
/* 5236 */       return false;
/*      */     }
/*      */     
/* 5239 */     return this.outgoing_piece_message_handler.isStalledPendingLoad();
/*      */   }
/*      */   
/*      */   public int[] getIncomingRequestedPieceNumbers() {
/* 5243 */     if (this.outgoing_piece_message_handler == null) {
/* 5244 */       return new int[0];
/*      */     }
/* 5246 */     return this.outgoing_piece_message_handler.getRequestedPieceNumbers();
/*      */   }
/*      */   
/*      */   public int[] getOutgoingRequestedPieceNumbers() {
/*      */     try {
/* 5251 */       this.requested_mon.enter();
/*      */       
/*      */ 
/* 5254 */       int iLastNumber = -1;
/*      */       
/*      */ 
/* 5257 */       int[] pieceNumbers = new int[this.requested.size()];
/* 5258 */       int pos = 0;
/*      */       DiskManagerReadRequest request;
/* 5260 */       for (int i = 0; i < this.requested.size(); i++) {
/* 5261 */         request = null;
/*      */         try {
/* 5263 */           request = (DiskManagerReadRequest)this.requested.get(i);
/*      */         } catch (Exception e) {
/* 5265 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/* 5268 */         if ((request != null) && (iLastNumber != request.getPieceNumber())) {
/* 5269 */           iLastNumber = request.getPieceNumber();
/* 5270 */           pieceNumbers[(pos++)] = iLastNumber;
/*      */         }
/*      */       }
/*      */       
/* 5274 */       int[] trimmed = new int[pos];
/* 5275 */       System.arraycopy(pieceNumbers, 0, trimmed, 0, pos);
/*      */       
/* 5277 */       return trimmed;
/*      */     }
/*      */     finally {
/* 5280 */       this.requested_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneOfCurrentIncomingRequest()
/*      */   {
/* 5287 */     return this.connection.getIncomingMessageQueue().getPercentDoneOfCurrentMessage();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneOfCurrentOutgoingRequest()
/*      */   {
/* 5293 */     return this.connection.getOutgoingMessageQueue().getPercentDoneOfCurrentMessage();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastMessageSentTime()
/*      */   {
/* 5299 */     return this.last_message_sent_time;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getRelationText()
/*      */   {
/* 5306 */     String text = "";
/* 5307 */     if ((this.manager instanceof org.gudy.azureus2.core3.logging.LogRelation))
/* 5308 */       text = ((org.gudy.azureus2.core3.logging.LogRelation)this.manager).getRelationText() + "; ";
/* 5309 */     text = text + "Peer: " + toString();
/* 5310 */     return text;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object[] getQueryableInterfaces()
/*      */   {
/* 5318 */     return new Object[] { this.manager };
/*      */   }
/*      */   
/*      */   public int getLastPiece()
/*      */   {
/* 5323 */     return this._lastPiece;
/*      */   }
/*      */   
/*      */   public void setLastPiece(int pieceNumber)
/*      */   {
/* 5328 */     this._lastPiece = pieceNumber;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isLANLocal()
/*      */   {
/* 5334 */     if (this.connection == null) return org.gudy.azureus2.core3.util.AddressUtils.isLANLocalAddress(this.ip) == 1;
/* 5335 */     return this.connection.isLANLocal();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isTCP()
/*      */   {
/* 5341 */     if (this.connection == null) {
/* 5342 */       return false;
/*      */     }
/*      */     
/* 5345 */     ProtocolEndpoint[] protocols = this.connection.getEndpoint().getProtocols();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5351 */     return protocols[0].getType() != 2;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNetwork()
/*      */   {
/* 5357 */     return this.network;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setUploadRateLimitBytesPerSecond(int bytes)
/*      */   {
/* 5364 */     if (bytes == -1)
/*      */     {
/* 5366 */       if (!isUploadDisabled(PEPeerTransport.class))
/*      */       {
/* 5368 */         setUploadDisabled(PEPeerTransport.class, true);
/*      */         
/* 5370 */         this.connection.setUploadLimit(0);
/*      */       }
/*      */     }
/*      */     else {
/* 5374 */       if (this.is_upload_disabled)
/*      */       {
/* 5376 */         if (isUploadDisabled(PEPeerTransport.class))
/*      */         {
/* 5378 */           setUploadDisabled(PEPeerTransport.class, false);
/*      */         }
/*      */       }
/*      */       
/* 5382 */       this.connection.setUploadLimit(bytes);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getUploadRateLimitBytesPerSecond()
/*      */   {
/* 5389 */     if (this.is_upload_disabled)
/*      */     {
/* 5391 */       if (isUploadDisabled(PEPeerTransport.class))
/*      */       {
/* 5393 */         return -1;
/*      */       }
/*      */     }
/*      */     
/* 5397 */     return this.connection.getUploadLimit();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDownloadRateLimitBytesPerSecond(int bytes)
/*      */   {
/* 5404 */     if (bytes == -1)
/*      */     {
/* 5406 */       if (!isDownloadDisabled(PEPeerTransport.class))
/*      */       {
/* 5408 */         setDownloadDisabled(PEPeerTransport.class, true);
/*      */         
/* 5410 */         this.connection.setDownloadLimit(0);
/*      */       }
/*      */     }
/*      */     else {
/* 5414 */       if (this.is_download_disabled)
/*      */       {
/* 5416 */         if (isDownloadDisabled(PEPeerTransport.class))
/*      */         {
/* 5418 */           setDownloadDisabled(PEPeerTransport.class, false);
/*      */         }
/*      */       }
/*      */       
/* 5422 */       this.connection.setDownloadLimit(bytes);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDownloadRateLimitBytesPerSecond()
/*      */   {
/* 5429 */     if (this.is_download_disabled)
/*      */     {
/* 5431 */       if (isDownloadDisabled(PEPeerTransport.class))
/*      */       {
/* 5433 */         return -1;
/*      */       }
/*      */     }
/*      */     
/* 5437 */     return this.connection.getDownloadLimit();
/*      */   }
/*      */   
/* 5440 */   public String getClientNameFromPeerID() { return this.client_peer_id; }
/*      */   
/* 5442 */   public String getClientNameFromExtensionHandshake() { if ((!this.client_handshake.equals("")) && (!this.client_handshake_version.equals(""))) {
/* 5443 */       return this.client_handshake + " " + this.client_handshake_version;
/*      */     }
/* 5445 */     return this.client_handshake;
/*      */   }
/*      */   
/*      */   private static org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider getDHTProvider() {
/* 5449 */     return com.aelitis.azureus.core.impl.AzureusCoreImpl.getSingleton().getGlobalManager().getMainlineDHTProvider();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPriorityConnection(boolean is_priority)
/*      */   {
/* 5456 */     synchronized (this)
/*      */     {
/* 5458 */       if (this.priority_connection == is_priority)
/*      */       {
/* 5460 */         return;
/*      */       }
/*      */       
/* 5463 */       this.priority_connection = is_priority;
/*      */     }
/*      */     
/* 5466 */     this.manager.getAdapter().priorityConnectionChanged(is_priority);
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
/*      */   protected List<Integer> generateFastSet(byte[] hash, String ip, int num_pieces, int num_required)
/*      */   {
/* 5485 */     List<Integer> res = new ArrayList();
/*      */     try
/*      */     {
/* 5488 */       if (this.network == "Public")
/*      */       {
/* 5490 */         byte[] address = InetAddress.getByName(ip).getAddress();
/*      */         
/*      */ 
/*      */ 
/* 5494 */         if (address.length == 4)
/*      */         {
/* 5496 */           byte[] bytes = new byte[24];
/*      */           
/* 5498 */           System.arraycopy(address, 0, bytes, 0, 3);
/* 5499 */           System.arraycopy(hash, 0, bytes, 4, 20);
/*      */           
/* 5501 */           num_required = Math.min(num_required, num_pieces);
/*      */           
/* 5503 */           while (res.size() < num_required)
/*      */           {
/* 5505 */             bytes = new org.gudy.azureus2.core3.util.SHA1Simple().calculateHash(bytes);
/*      */             
/* 5507 */             int pos = 0;
/*      */             
/* 5509 */             while ((pos < 20) && (res.size() < num_required))
/*      */             {
/* 5511 */               long index = bytes[(pos++)] << 24 & 0xFF000000 | bytes[(pos++)] << 16 & 0xFF0000 | bytes[(pos++)] << 8 & 0xFF00 | bytes[(pos++)] & 0xFF;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 5516 */               Integer i = new Integer((int)(index % num_pieces));
/*      */               
/* 5518 */               if (!res.contains(i))
/*      */               {
/* 5520 */                 res.add(i);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 5528 */       Debug.out("Fast set generation failed", e);
/*      */     }
/*      */     
/* 5531 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected List<Integer> generateFastSet(int num)
/*      */   {
/* 5538 */     return generateFastSet(this.manager.getHash(), getIp(), this.nbPieces, num);
/*      */   }
/*      */   
/* 5541 */   public int getTaggableType() { return 4; }
/* 5542 */   public String getTaggableID() { return null; }
/* 5543 */   public com.aelitis.azureus.core.tag.TaggableResolver getTaggableResolver() { return null; }
/*      */   
/*      */   public Object getTaggableTransientProperty(String key) {
/* 5546 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generateEvidence(IndentWriter writer)
/*      */   {
/* 5556 */     writer.println("ip=" + getIp() + ",in=" + isIncoming() + ",port=" + getPort() + ",cli=" + this.client + ",tcp=" + getTCPListenPort() + ",udp=" + getUDPListenPort() + ",oudp=" + getUDPNonDataListenPort() + ",prot=" + getProtocol() + ",p_state=" + getPeerState() + ",c_state=" + getConnectionState() + ",seed=" + isSeed() + ",partialSeed=" + isRelativeSeed() + ",pex=" + this.peer_exchange_supported + ",closing=" + this.closing);
/*      */     
/*      */ 
/* 5559 */     writer.println("    choked=" + this.effectively_choked_by_other_peer + "/" + this.really_choked_by_other_peer + ",choking=" + this.choking_other_peer + ",is_opt=" + this.is_optimistic_unchoke);
/* 5560 */     writer.println("    interested=" + this.interested_in_other_peer + ",interesting=" + this.other_peer_interested_in_me + ",snubbed=" + this.snubbed);
/* 5561 */     writer.println("    lp=" + this._lastPiece + ",up=" + this.uniquePiece + ",rp=" + this.reserved_pieces);
/* 5562 */     writer.println("    last_sent=" + this.last_message_sent_time + "/" + this.last_data_message_sent_time + ",last_recv=" + this.last_message_received_time + "/" + this.last_data_message_received_time + "/" + this.last_good_data_time);
/*      */     
/*      */ 
/* 5565 */     writer.println("    conn_at=" + this.connection_established_time + ",cons_no_reqs=" + this.consecutive_no_request_count + ",discard=" + requests_discarded + "/" + requests_discarded_endgame + ",recov=" + requests_recovered + ",comp=" + requests_completed + ",curr=" + this.requested.size());
/*      */   }
/*      */   
/*      */   public void requestAllocationComplete() {}
/*      */   
/*      */   /* Error */
/*      */   public boolean isPriorityConnection()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 2631	org/gudy/azureus2/core3/peer/impl/transport/PEPeerTransportProtocol:priority_connection	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #5472	-> byte code offset #0
/*      */     //   Java source line #5474	-> byte code offset #4
/*      */     //   Java source line #5475	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	PEPeerTransportProtocol
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   public void setTaggableTransientProperty(String key, Object value) {}
/*      */   
/*      */   protected static class MutableInteger
/*      */   {
/*      */     private int value;
/*      */     
/*      */     protected MutableInteger(int v)
/*      */     {
/* 5579 */       this.value = v;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setValue(int v)
/*      */     {
/* 5586 */       this.value = v;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getValue()
/*      */     {
/* 5592 */       return this.value;
/*      */     }
/*      */     
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 5598 */       return this.value;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/* 5605 */       if ((obj instanceof MutableInteger)) {
/* 5606 */         return this.value == ((MutableInteger)obj).value;
/*      */       }
/* 5608 */       return false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/transport/PEPeerTransportProtocol.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */