/*      */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.dht.DHTLogger;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionProvider;
/*      */ import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionProviderListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeNetwork;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportException;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportFindValueReply;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportProgressListener;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportQueryStoreReply;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportRequestHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportStoreReply;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportTransferHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportValue;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDPContact;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketHandlerException;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketHandlerStub;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketReceiver;
/*      */ import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPRequestHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.util.DHTTransferHandler;
/*      */ import com.aelitis.azureus.core.dht.transport.util.DHTTransferHandler.Packet;
/*      */ import com.aelitis.azureus.core.dht.transport.util.DHTTransportRequestCounter;
/*      */ import com.aelitis.azureus.core.util.average.MovingImmediateAverage;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilter;
/*      */ import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
/*      */ import com.aelitis.azureus.core.versioncheck.VersionCheckClient;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.Inet4Address;
/*      */ import java.net.Inet6Address;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*      */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AESemaphore;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.Average;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DelayedEvent;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.utils.Utilities;
/*      */ 
/*      */ public class DHTTransportUDPImpl implements DHTTransportUDP, DHTUDPRequestHandler
/*      */ {
/*   80 */   public static boolean TEST_EXTERNAL_IP = false;
/*      */   
/*      */   public static final int MIN_ADDRESS_CHANGE_PERIOD_INIT_DEFAULT = 300000;
/*      */   
/*      */   public static final int MIN_ADDRESS_CHANGE_PERIOD_NEXT_DEFAULT = 600000;
/*      */   
/*      */   public static final int STORE_TIMEOUT_MULTIPLIER = 2;
/*      */   
/*      */   private String external_address;
/*   89 */   private int min_address_change_period = 300000;
/*      */   
/*      */   private final byte protocol_version;
/*      */   
/*      */   private final int network;
/*      */   
/*      */   private final boolean v6;
/*      */   
/*      */   private final String ip_override;
/*      */   
/*      */   private int port;
/*      */   
/*      */   private final int max_fails_for_live;
/*      */   
/*      */   private final int max_fails_for_unknown;
/*      */   private long request_timeout;
/*      */   private long store_timeout;
/*      */   private boolean reachable;
/*      */   private boolean reachable_accurate;
/*      */   private final int dht_send_delay;
/*      */   private final int dht_receive_delay;
/*      */   final DHTLogger logger;
/*      */   private DHTUDPPacketHandler packet_handler;
/*      */   private DHTTransportRequestHandler request_handler;
/*      */   private DHTTransportUDPContactImpl local_contact;
/*      */   private long last_address_change;
/*  115 */   final List listeners = new ArrayList();
/*      */   
/*  117 */   private final IpFilter ip_filter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*      */   
/*      */ 
/*      */   private DHTTransportUDPStatsImpl stats;
/*      */   
/*  122 */   private boolean bootstrap_node = false;
/*      */   
/*  124 */   private byte generic_flags = 0;
/*  125 */   private byte generic_flags2 = VersionCheckClient.getSingleton().getDHTFlags();
/*      */   
/*      */   private static final int CONTACT_HISTORY_MAX = 32;
/*      */   
/*      */   private static final int CONTACT_HISTORY_PING_SIZE = 24;
/*  130 */   final Map<InetSocketAddress, DHTTransportContact> contact_history = new LinkedHashMap(32, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<InetSocketAddress, DHTTransportContact> eldest)
/*      */     {
/*      */ 
/*  137 */       return size() > 32;
/*      */     }
/*      */   };
/*      */   
/*      */   private static final int ROUTABLE_CONTACT_HISTORY_MAX = 128;
/*      */   
/*  143 */   final Map<InetSocketAddress, DHTTransportContact> routable_contact_history = new LinkedHashMap(128, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<InetSocketAddress, DHTTransportContact> eldest)
/*      */     {
/*      */ 
/*  150 */       return size() > 128;
/*      */     }
/*      */   };
/*      */   
/*      */   private long other_routable_total;
/*      */   
/*      */   private long other_non_routable_total;
/*  157 */   private final MovingImmediateAverage routeable_percentage_average = com.aelitis.azureus.core.util.average.AverageFactory.MovingImmediateAverage(8);
/*      */   
/*      */   private static final int RECENT_REPORTS_HISTORY_MAX = 32;
/*      */   
/*  161 */   private final Map recent_reports = new LinkedHashMap(32, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry eldest)
/*      */     {
/*      */ 
/*  168 */       return size() > 32;
/*      */     }
/*      */   };
/*      */   
/*      */   private static final int STATS_PERIOD = 60000;
/*      */   
/*      */   private static final int STATS_DURATION_SECS = 600;
/*      */   
/*      */   private static final long STATS_INIT_PERIOD = 900000L;
/*  177 */   private long stats_start_time = SystemTime.getCurrentTime();
/*      */   
/*      */   private long last_alien_count;
/*      */   private long last_alien_fv_count;
/*  181 */   private final Average alien_average = Average.getInstance(60000, 600);
/*  182 */   private final Average alien_fv_average = Average.getInstance(60000, 600);
/*      */   
/*      */   private Random random;
/*      */   
/*      */   private static final int BAD_IP_BLOOM_FILTER_SIZE = 32000;
/*      */   
/*      */   private BloomFilter bad_ip_bloom_filter;
/*  189 */   private static final AEMonitor class_mon = new AEMonitor("DHTTransportUDP:class");
/*      */   
/*  191 */   final AEMonitor this_mon = new AEMonitor("DHTTransportUDP");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean initial_address_change_deferred;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean address_changing;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final DHTTransferHandler xfer_handler;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final Map<Integer, DHTTransportAlternativeNetworkImpl> alt_net_states;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private volatile Map<Integer, DHTTransportAlternativeNetwork> alt_net_providers;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final Object alt_net_providers_lock;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTTransportUDPImpl(byte _protocol_version, int _network, boolean _v6, String _ip, String _default_ip, int _port, int _max_fails_for_live, int _max_fails_for_unknown, long _timeout, int _dht_send_delay, int _dht_receive_delay, boolean _bootstrap_node, boolean _initial_reachability, DHTLogger _logger)
/*      */     throws DHTTransportException
/*      */   {
/* 3219 */     this.alt_net_states = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 3224 */     this.alt_net_providers = new HashMap();
/*      */     
/* 3226 */     this.alt_net_providers_lock = new Object();
/*      */     
/*      */ 
/* 3229 */     int[] arr$ = DHTTransportAlternativeNetwork.AT_ALL;int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Integer net = Integer.valueOf(arr$[i$]);
/*      */       
/* 3231 */       this.alt_net_states.put(net, new DHTTransportAlternativeNetworkImpl(net.intValue()));
/*      */     }
/*  217 */     this.protocol_version = _protocol_version;
/*  218 */     this.network = _network;
/*  219 */     this.v6 = _v6;
/*  220 */     this.ip_override = _ip;
/*  221 */     this.port = _port;
/*  222 */     this.max_fails_for_live = _max_fails_for_live;
/*  223 */     this.max_fails_for_unknown = _max_fails_for_unknown;
/*  224 */     this.request_timeout = _timeout;
/*  225 */     this.dht_send_delay = _dht_send_delay;
/*  226 */     this.dht_receive_delay = _dht_receive_delay;
/*  227 */     this.bootstrap_node = _bootstrap_node;
/*  228 */     this.reachable = _initial_reachability;
/*  229 */     this.logger = _logger;
/*      */     
/*  231 */     this.store_timeout = (this.request_timeout * 2L);
/*      */     try
/*      */     {
/*  234 */       this.random = RandomUtils.SECURE_RANDOM;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  238 */       this.random = new Random();
/*      */       
/*  240 */       this.logger.log(e);
/*      */     }
/*      */     
/*  243 */     this.xfer_handler = new DHTTransferHandler(new com.aelitis.azureus.core.dht.transport.util.DHTTransferHandler.Adapter()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void sendRequest(DHTTransportContact _contact, DHTTransferHandler.Packet packet)
/*      */       {
/*      */ 
/*      */ 
/*  252 */         DHTTransportUDPContactImpl contact = (DHTTransportUDPContactImpl)_contact;
/*      */         
/*  254 */         DHTUDPPacketData request = new DHTUDPPacketData(DHTTransportUDPImpl.this, packet.getConnectionId(), DHTTransportUDPImpl.this.local_contact, contact);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  261 */         request.setDetails(packet.getPacketType(), packet.getTransferKey(), packet.getRequestKey(), packet.getData(), packet.getStartPosition(), packet.getLength(), packet.getTotalLength());
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  271 */           DHTTransportUDPImpl.this.checkAddress(contact);
/*      */           
/*  273 */           DHTTransportUDPImpl.this.stats.dataSent(request);
/*      */           
/*  275 */           DHTTransportUDPImpl.this.packet_handler.send(request, contact.getTransportAddress());
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  287 */       public long getConnectionID() { return DHTTransportUDPImpl.this.getConnectionID(); } }, 1317, 1.0F, this.logger);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  294 */     int last_pct = COConfigurationManager.getIntParameter("dht.udp.net" + this.network + ".routeable_pct", -1);
/*      */     
/*  296 */     if (last_pct > 0)
/*      */     {
/*  298 */       this.routeable_percentage_average.update(last_pct);
/*      */     }
/*      */     
/*  301 */     DHTUDPUtils.registerTransport(this);
/*      */     
/*  303 */     createPacketHandler();
/*      */     
/*  305 */     SimpleTimer.addPeriodicEvent("DHTUDP:stats", 60000L, new TimerEventPerformer()
/*      */     {
/*      */       private int tick_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*  316 */         DHTTransportUDPImpl.this.updateStats(this.tick_count++);
/*      */         
/*  318 */         DHTTransportUDPImpl.this.checkAltContacts();
/*      */       }
/*      */       
/*  321 */     });
/*  322 */     String default_ip = _default_ip == null ? "127.0.0.1" : this.v6 ? "::1" : _default_ip;
/*      */     
/*  324 */     getExternalAddress(default_ip, this.logger);
/*      */     
/*  326 */     InetSocketAddress address = new InetSocketAddress(this.external_address, this.port);
/*      */     
/*  328 */     DHTNetworkPositionManager.addProviderListener(new DHTNetworkPositionProviderListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void providerAdded(DHTNetworkPositionProvider provider)
/*      */       {
/*      */ 
/*  335 */         if (DHTTransportUDPImpl.this.local_contact != null)
/*      */         {
/*  337 */           DHTTransportUDPImpl.this.local_contact.createNetworkPositions(true);
/*      */           try
/*      */           {
/*  340 */             DHTTransportUDPImpl.this.this_mon.enter();
/*      */             
/*  342 */             for (DHTTransportContact c : DHTTransportUDPImpl.this.contact_history.values())
/*      */             {
/*  344 */               c.createNetworkPositions(false);
/*      */             }
/*      */             
/*  347 */             for (DHTTransportContact c : DHTTransportUDPImpl.this.routable_contact_history.values())
/*      */             {
/*  349 */               c.createNetworkPositions(false);
/*      */             }
/*      */           }
/*      */           finally {
/*  353 */             DHTTransportUDPImpl.this.this_mon.exit();
/*      */           }
/*      */           
/*  356 */           for (int i = 0; i < DHTTransportUDPImpl.this.listeners.size(); i++) {
/*      */             try
/*      */             {
/*  359 */               ((DHTTransportListener)DHTTransportUDPImpl.this.listeners.get(i)).resetNetworkPositions();
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  363 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void providerRemoved(DHTNetworkPositionProvider provider) {}
/*  375 */     });
/*  376 */     this.logger.log("Initial external address: " + address);
/*      */     
/*  378 */     this.local_contact = new DHTTransportUDPContactImpl(true, this, address, address, this.protocol_version, this.random.nextInt(), 0L, (byte)0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void createPacketHandler()
/*      */     throws DHTTransportException
/*      */   {
/*      */     
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  392 */       if ((this.packet_handler != null) && (!this.packet_handler.isDestroyed()))
/*      */       {
/*  394 */         this.packet_handler.destroy();
/*      */       }
/*      */       
/*  397 */       this.packet_handler = com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketHandlerFactory.getHandler(this, this);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  401 */       throw new DHTTransportException("Failed to get packet handler", e);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  409 */     this.packet_handler.setDelays(this.dht_send_delay, this.dht_receive_delay, (int)this.request_timeout);
/*      */     
/*  411 */     this.stats_start_time = SystemTime.getCurrentTime();
/*      */     
/*  413 */     if (this.stats == null)
/*      */     {
/*  415 */       this.stats = new DHTTransportUDPStatsImpl(this, this.protocol_version, this.packet_handler.getStats());
/*      */     }
/*      */     else
/*      */     {
/*  419 */       this.stats.setStats(this.packet_handler.getStats());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTUDPRequestHandler getRequestHandler()
/*      */   {
/*  426 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTUDPPacketHandler getPacketHandler()
/*      */   {
/*  432 */     return this.packet_handler;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSuspended(boolean susp)
/*      */   {
/*  439 */     if (susp)
/*      */     {
/*  441 */       if (this.packet_handler != null)
/*      */       {
/*  443 */         this.packet_handler.destroy();
/*      */       }
/*      */     }
/*  446 */     else if ((this.packet_handler == null) || (this.packet_handler.isDestroyed())) {
/*      */       try
/*      */       {
/*  449 */         createPacketHandler();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  453 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateStats(int tick_count)
/*      */   {
/*  465 */     this.generic_flags2 = VersionCheckClient.getSingleton().getDHTFlags();
/*      */     
/*  467 */     long alien_count = 0L;
/*      */     
/*  469 */     long[] aliens = this.stats.getAliens();
/*      */     
/*  471 */     for (int i = 0; i < aliens.length; i++)
/*      */     {
/*  473 */       alien_count += aliens[i];
/*      */     }
/*      */     
/*  476 */     long alien_fv_count = aliens[1];
/*      */     
/*  478 */     this.alien_average.addValue((alien_count - this.last_alien_count) * 60000L / 1000L);
/*  479 */     this.alien_fv_average.addValue((alien_fv_count - this.last_alien_fv_count) * 60000L / 1000L);
/*      */     
/*  481 */     this.last_alien_count = alien_count;
/*  482 */     this.last_alien_fv_count = alien_fv_count;
/*      */     
/*  484 */     long now = SystemTime.getCurrentTime();
/*      */     
/*  486 */     if (now < this.stats_start_time)
/*      */     {
/*  488 */       this.stats_start_time = now;
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  495 */       if (Constants.isCVSVersion())
/*      */       {
/*  497 */         long fv_average = this.alien_fv_average.getAverage();
/*  498 */         long all_average = this.alien_average.getAverage();
/*      */         
/*  500 */         this.logger.log("Aliens for net " + this.network + ": " + fv_average + "/" + all_average);
/*      */       }
/*      */       
/*  503 */       if (now - this.stats_start_time > 900000L)
/*      */       {
/*  505 */         this.reachable_accurate = true;
/*      */         
/*  507 */         boolean old_reachable = this.reachable;
/*      */         
/*  509 */         if (this.alien_fv_average.getAverage() > 1L)
/*      */         {
/*  511 */           this.reachable = true;
/*      */         }
/*  513 */         else if (this.alien_average.getAverage() > 3L)
/*      */         {
/*  515 */           this.reachable = true;
/*      */         }
/*      */         else
/*      */         {
/*  519 */           this.reachable = false;
/*      */         }
/*      */         
/*  522 */         if (old_reachable != this.reachable)
/*      */         {
/*  524 */           for (int i = 0; i < this.listeners.size(); i++) {
/*      */             try
/*      */             {
/*  527 */               ((DHTTransportListener)this.listeners.get(i)).reachabilityChanged(this.reachable);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/*  531 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  538 */     int pct = getRouteablePercentage();
/*      */     
/*  540 */     if (pct > 0)
/*      */     {
/*  542 */       COConfigurationManager.setParameter("dht.udp.net" + this.network + ".routeable_pct", pct);
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
/*      */   protected void recordSkew(InetSocketAddress originator_address, long skew)
/*      */   {
/*  555 */     if (this.stats != null)
/*      */     {
/*  557 */       this.stats.recordSkew(originator_address, skew);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getNodeStatus()
/*      */   {
/*  564 */     if (this.bootstrap_node)
/*      */     {
/*      */ 
/*      */ 
/*  568 */       return 0;
/*      */     }
/*      */     
/*  571 */     if (this.reachable_accurate)
/*      */     {
/*  573 */       int status = this.reachable ? 1 : 0;
/*      */       
/*  575 */       return status;
/*      */     }
/*      */     
/*      */ 
/*  579 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isReachable()
/*      */   {
/*  586 */     return this.reachable;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getProtocolVersion()
/*      */   {
/*  592 */     return this.protocol_version;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getMinimumProtocolVersion()
/*      */   {
/*  598 */     return getNetwork() == 1 ? DHTTransportUDP.PROTOCOL_VERSION_MIN_CVS : DHTTransportUDP.PROTOCOL_VERSION_MIN;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  604 */     return this.port;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setPort(int new_port)
/*      */     throws DHTTransportException
/*      */   {
/*  613 */     if (new_port == this.port)
/*      */     {
/*  615 */       return;
/*      */     }
/*      */     
/*  618 */     this.port = new_port;
/*      */     
/*  620 */     createPacketHandler();
/*      */     
/*  622 */     setLocalContact();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTimeout()
/*      */   {
/*  628 */     return this.request_timeout;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setTimeout(long timeout)
/*      */   {
/*  635 */     if (this.request_timeout == timeout)
/*      */     {
/*  637 */       return;
/*      */     }
/*      */     
/*  640 */     this.request_timeout = timeout;
/*  641 */     this.store_timeout = (this.request_timeout * 2L);
/*      */     
/*  643 */     this.packet_handler.setDelays(this.dht_send_delay, this.dht_receive_delay, (int)this.request_timeout);
/*      */   }
/*      */   
/*      */ 
/*      */   public int getNetwork()
/*      */   {
/*  649 */     return this.network;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getGenericFlags()
/*      */   {
/*  655 */     return this.generic_flags;
/*      */   }
/*      */   
/*      */ 
/*      */   public byte getGenericFlags2()
/*      */   {
/*  661 */     return this.generic_flags2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setGenericFlag(byte flag, boolean value)
/*      */   {
/*  669 */     synchronized (this)
/*      */     {
/*  671 */       if (value)
/*      */       {
/*  673 */         this.generic_flags = ((byte)(this.generic_flags | flag));
/*      */       }
/*      */       else
/*      */       {
/*  677 */         this.generic_flags = ((byte)(this.generic_flags & (flag ^ 0xFFFFFFFF)));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isIPV6()
/*      */   {
/*  685 */     return this.v6;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void testInstanceIDChange()
/*      */     throws DHTTransportException
/*      */   {
/*  693 */     this.local_contact = new DHTTransportUDPContactImpl(true, this, this.local_contact.getTransportAddress(), this.local_contact.getExternalAddress(), this.protocol_version, this.random.nextInt(), 0L, (byte)0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void testTransportIDChange()
/*      */     throws DHTTransportException
/*      */   {
/*  701 */     if (this.external_address.equals("127.0.0.1"))
/*      */     {
/*  703 */       this.external_address = "192.168.0.2";
/*      */     }
/*      */     else {
/*  706 */       this.external_address = "127.0.0.1";
/*      */     }
/*      */     
/*  709 */     InetSocketAddress address = new InetSocketAddress(this.external_address, this.port);
/*      */     
/*  711 */     this.local_contact = new DHTTransportUDPContactImpl(true, this, address, address, this.protocol_version, this.local_contact.getInstanceID(), 0L, (byte)0);
/*      */     
/*  713 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/*  716 */         ((DHTTransportListener)this.listeners.get(i)).localContactChanged(this.local_contact);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  720 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void testExternalAddressChange()
/*      */   {
/*      */     try
/*      */     {
/*  729 */       Iterator it = this.contact_history.values().iterator();
/*      */       
/*  731 */       DHTTransportUDPContactImpl c1 = (DHTTransportUDPContactImpl)it.next();
/*  732 */       DHTTransportUDPContactImpl c2 = (DHTTransportUDPContactImpl)it.next();
/*      */       
/*  734 */       externalAddressChange(c1, c2.getExternalAddress(), true);
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  739 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void testNetworkAlive(boolean alive)
/*      */   {
/*  747 */     this.packet_handler.testNetworkAlive(alive);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void getExternalAddress(String default_address, DHTLogger log)
/*      */   {
/*      */     try
/*      */     {
/*  759 */       class_mon.enter();
/*      */       
/*  761 */       String new_external_address = null;
/*      */       try
/*      */       {
/*  764 */         log.log("Obtaining external address");
/*      */         
/*  766 */         if (TEST_EXTERNAL_IP)
/*      */         {
/*  768 */           new_external_address = this.v6 ? "::1" : "127.0.0.1";
/*      */           
/*  770 */           log.log("    External IP address obtained from test data: " + new_external_address);
/*      */         }
/*      */         
/*  773 */         if (this.ip_override != null)
/*      */         {
/*  775 */           new_external_address = this.ip_override;
/*      */           
/*  777 */           log.log("    External IP address explicitly overridden: " + new_external_address);
/*      */         }
/*      */         
/*  780 */         if (new_external_address == null)
/*      */         {
/*      */           List contacts;
/*      */           
/*      */ 
/*      */           try
/*      */           {
/*  787 */             this.this_mon.enter();
/*      */             
/*  789 */             contacts = new ArrayList(this.contact_history.values());
/*      */           }
/*      */           finally
/*      */           {
/*  793 */             this.this_mon.exit();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  799 */           String returned_address = null;
/*  800 */           int returned_matches = 0;
/*      */           
/*  802 */           int search_lim = Math.min(24, contacts.size());
/*      */           
/*  804 */           log.log("    Contacts to search = " + search_lim);
/*      */           
/*  806 */           for (int i = 0; i < search_lim; i++)
/*      */           {
/*  808 */             DHTTransportUDPContactImpl contact = (DHTTransportUDPContactImpl)contacts.remove(RandomUtils.nextInt(contacts.size()));
/*      */             
/*  810 */             InetSocketAddress a = askContactForExternalAddress(contact);
/*      */             
/*  812 */             if ((a != null) && (a.getAddress() != null))
/*      */             {
/*  814 */               String ip = a.getAddress().getHostAddress();
/*      */               
/*  816 */               if (returned_address == null)
/*      */               {
/*  818 */                 returned_address = ip;
/*      */                 
/*  820 */                 log.log("    : contact " + contact.getString() + " reported external address as '" + ip + "'");
/*      */                 
/*  822 */                 returned_matches++;
/*      */               }
/*  824 */               else if (returned_address.equals(ip))
/*      */               {
/*  826 */                 returned_matches++;
/*      */                 
/*  828 */                 log.log("    : contact " + contact.getString() + " also reported external address as '" + ip + "'");
/*      */                 
/*  830 */                 if (returned_matches == 3)
/*      */                 {
/*  832 */                   new_external_address = returned_address;
/*      */                   
/*  834 */                   log.log("    External IP address obtained from contacts: " + returned_address);
/*      */                   
/*  836 */                   break;
/*      */                 }
/*      */               }
/*      */               else {
/*  840 */                 log.log("    : contact " + contact.getString() + " reported external address as '" + ip + "', abandoning due to mismatch");
/*      */                 
/*      */ 
/*      */ 
/*  844 */                 break;
/*      */               }
/*      */             }
/*      */             else {
/*  848 */               log.log("    : contact " + contact.getString() + " didn't reply");
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  854 */         if (new_external_address == null)
/*      */         {
/*  856 */           InetAddress public_address = this.logger.getPluginInterface().getUtilities().getPublicAddress(this.v6);
/*      */           
/*  858 */           if (public_address != null)
/*      */           {
/*  860 */             new_external_address = public_address.getHostAddress();
/*      */             
/*  862 */             log.log("    External IP address obtained: " + new_external_address);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  868 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/*  871 */       if (new_external_address == null)
/*      */       {
/*  873 */         new_external_address = default_address;
/*      */         
/*  875 */         log.log("    External IP address defaulted:  " + new_external_address);
/*      */       }
/*      */       
/*  878 */       if ((this.external_address == null) || (!this.external_address.equals(new_external_address)))
/*      */       {
/*  880 */         informLocalAddress(new_external_address);
/*      */       }
/*      */       
/*  883 */       this.external_address = new_external_address;
/*      */     }
/*      */     finally
/*      */     {
/*  887 */       class_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void informLocalAddress(String address)
/*      */   {
/*  895 */     for (int i = 0; i < this.listeners.size(); i++) {
/*      */       try
/*      */       {
/*  898 */         ((DHTTransportListener)this.listeners.get(i)).currentAddress(address);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  902 */         Debug.printStackTrace(e);
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
/*      */   protected void externalAddressChange(final DHTTransportUDPContactImpl reporter, final InetSocketAddress new_address, boolean force)
/*      */     throws DHTTransportException
/*      */   {
/*  931 */     InetAddress ia = new_address.getAddress();
/*      */     
/*  933 */     if (ia == null)
/*      */     {
/*  935 */       Debug.out("reported new external address '" + new_address + "' is unresolved");
/*      */       
/*  937 */       throw new DHTTransportException("Address '" + new_address + "' is unresolved");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  942 */     if ((((ia instanceof Inet4Address)) && (this.v6)) || (((ia instanceof Inet6Address)) && (!this.v6)))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  949 */       return;
/*      */     }
/*      */     
/*  952 */     final String new_ip = ia.getHostAddress();
/*      */     
/*  954 */     if (new_ip.equals(this.external_address))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  959 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  963 */       this.this_mon.enter();
/*      */       
/*  965 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  967 */       if (now - this.last_address_change < this.min_address_change_period) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  972 */       if ((this.contact_history.size() < 32) && (!force))
/*      */       {
/*  974 */         if (!this.initial_address_change_deferred)
/*      */         {
/*  976 */           this.initial_address_change_deferred = true;
/*      */           
/*  978 */           this.logger.log("Node " + reporter.getString() + " has reported that the external IP address is '" + new_address + "': deferring new checks");
/*      */           
/*  980 */           new DelayedEvent("DHTTransportUDP:delayAC", 30000L, new AERunnable()
/*      */           {
/*      */ 
/*      */             public void runSupport()
/*      */             {
/*      */ 
/*      */               try
/*      */               {
/*      */ 
/*  989 */                 DHTTransportUDPImpl.this.externalAddressChange(reporter, new_address, true);
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           });
/*      */         }
/*      */         
/*      */ 
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/* 1001 */       this.logger.log("Node " + reporter.getString() + " has reported that the external IP address is '" + new_address + "'");
/*      */       
/*      */ 
/*      */ 
/* 1005 */       if (invalidExternalAddress(ia))
/*      */       {
/* 1007 */         this.logger.log("     This is invalid as it is a private address."); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1016 */       if (reporter.getExternalAddress().getAddress().getHostAddress().equals(new_ip))
/*      */       {
/* 1018 */         this.logger.log("     This is invalid as it is the same as the reporter's address."); return;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1023 */       this.last_address_change = now;
/*      */       
/*      */ 
/*      */ 
/* 1027 */       if (this.min_address_change_period == 300000)
/*      */       {
/* 1029 */         this.min_address_change_period = 600000;
/*      */       }
/*      */     }
/*      */     finally {
/* 1033 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1036 */     final String old_external_address = this.external_address;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1043 */     new AEThread2("DHTTransportUDP:getAddress", true)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/* 1049 */           DHTTransportUDPImpl.this.this_mon.enter();
/*      */           
/* 1051 */           if (DHTTransportUDPImpl.this.address_changing) {
/*      */             return;
/*      */           }
/*      */           
/*      */ 
/* 1056 */           DHTTransportUDPImpl.this.address_changing = true;
/*      */         }
/*      */         finally
/*      */         {
/* 1060 */           DHTTransportUDPImpl.this.this_mon.exit();
/*      */         }
/*      */         try
/*      */         {
/* 1064 */           DHTTransportUDPImpl.this.getExternalAddress(new_ip, DHTTransportUDPImpl.this.logger);
/*      */           
/* 1066 */           if (old_external_address.equals(DHTTransportUDPImpl.this.external_address)) {
/*      */             return;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1074 */           DHTTransportUDPImpl.this.setLocalContact();
/*      */         }
/*      */         finally
/*      */         {
/*      */           try {
/* 1079 */             DHTTransportUDPImpl.this.this_mon.enter();
/*      */             
/* 1081 */             DHTTransportUDPImpl.this.address_changing = false;
/*      */           }
/*      */           finally
/*      */           {
/* 1085 */             DHTTransportUDPImpl.this.this_mon.exit();
/*      */           }
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void contactAlive(DHTTransportUDPContactImpl contact)
/*      */   {
/*      */     try
/*      */     {
/* 1097 */       this.this_mon.enter();
/*      */       
/* 1099 */       this.contact_history.put(contact.getTransportAddress(), contact);
/*      */     }
/*      */     finally
/*      */     {
/* 1103 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public DHTTransportContact[] getReachableContacts()
/*      */   {
/*      */     try
/*      */     {
/* 1111 */       this.this_mon.enter();
/*      */       
/* 1113 */       Collection<DHTTransportContact> vals = this.routable_contact_history.values();
/*      */       
/* 1115 */       DHTTransportContact[] res = new DHTTransportContact[vals.size()];
/*      */       
/* 1117 */       vals.toArray(res);
/*      */       
/* 1119 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 1123 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public DHTTransportContact[] getRecentContacts()
/*      */   {
/*      */     try
/*      */     {
/* 1131 */       this.this_mon.enter();
/*      */       
/* 1133 */       Collection<DHTTransportContact> vals = this.contact_history.values();
/*      */       
/* 1135 */       DHTTransportContact[] res = new DHTTransportContact[vals.size()];
/*      */       
/* 1137 */       vals.toArray(res);
/*      */       
/* 1139 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/* 1143 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void updateContactStatus(DHTTransportUDPContactImpl contact, int status, boolean incoming)
/*      */   {
/*      */     try
/*      */     {
/* 1154 */       this.this_mon.enter();
/*      */       
/* 1156 */       contact.setNodeStatus(status);
/*      */       
/* 1158 */       if (contact.getProtocolVersion() >= 12)
/*      */       {
/* 1160 */         if (status != -1)
/*      */         {
/* 1162 */           boolean other_routable = (status & 0x1) != 0;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1168 */           if (other_routable)
/*      */           {
/* 1170 */             if (incoming)
/*      */             {
/* 1172 */               synchronized (this.routeable_percentage_average)
/*      */               {
/* 1174 */                 this.other_routable_total += 1L;
/*      */               }
/*      */             }
/*      */             
/* 1178 */             this.routable_contact_history.put(contact.getTransportAddress(), contact);
/*      */ 
/*      */ 
/*      */           }
/* 1182 */           else if (incoming)
/*      */           {
/* 1184 */             synchronized (this.routeable_percentage_average)
/*      */             {
/* 1186 */               this.other_non_routable_total += 1L;
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1195 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getRouteablePercentage()
/*      */   {
/* 1202 */     synchronized (this.routeable_percentage_average)
/*      */     {
/* 1204 */       double average = this.routeable_percentage_average.getAverage();
/*      */       
/* 1206 */       long both_total = this.other_routable_total + this.other_non_routable_total;
/*      */       
/*      */       int current_percent;
/*      */       int current_percent;
/* 1210 */       if (both_total == 0L)
/*      */       {
/* 1212 */         current_percent = 0;
/*      */       }
/*      */       else
/*      */       {
/* 1216 */         current_percent = (int)(this.other_routable_total * 100L / both_total);
/*      */       }
/*      */       
/* 1219 */       if (both_total >= 300L)
/*      */       {
/*      */ 
/*      */ 
/* 1223 */         if (current_percent > 0)
/*      */         {
/* 1225 */           average = this.routeable_percentage_average.update(current_percent);
/*      */           
/* 1227 */           this.other_routable_total = (this.other_non_routable_total = 0L);
/*      */         }
/* 1229 */       } else if (both_total >= 100L)
/*      */       {
/*      */ 
/*      */ 
/* 1233 */         if (average == 0.0D)
/*      */         {
/* 1235 */           average = current_percent;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1241 */           int samples = this.routeable_percentage_average.getSampleCount();
/*      */           
/* 1243 */           if (samples > 0)
/*      */           {
/* 1245 */             average = (samples * average + current_percent) / (samples + 1);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1250 */       int result = (int)average;
/*      */       
/* 1252 */       if (result == 0)
/*      */       {
/*      */ 
/*      */ 
/* 1256 */         result = -1;
/*      */       }
/*      */       
/* 1259 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected boolean invalidExternalAddress(InetAddress ia)
/*      */   {
/* 1267 */     return (ia.isLinkLocalAddress()) || (ia.isLoopbackAddress()) || (ia.isSiteLocalAddress());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getMaxFailForLiveCount()
/*      */   {
/* 1275 */     return this.max_fails_for_live;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getMaxFailForUnknownCount()
/*      */   {
/* 1281 */     return this.max_fails_for_unknown;
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTTransportContact getLocalContact()
/*      */   {
/* 1287 */     return this.local_contact;
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setLocalContact()
/*      */   {
/* 1293 */     InetSocketAddress s_address = new InetSocketAddress(this.external_address, this.port);
/*      */     try
/*      */     {
/* 1296 */       this.local_contact = new DHTTransportUDPContactImpl(true, this, s_address, s_address, this.protocol_version, this.random.nextInt(), 0L, (byte)0);
/*      */       
/* 1298 */       this.logger.log("External address changed: " + s_address);
/*      */       
/* 1300 */       Debug.out("DHTTransport: address changed to " + s_address);
/*      */       
/* 1302 */       for (int i = 0; i < this.listeners.size(); i++) {
/*      */         try
/*      */         {
/* 1305 */           ((DHTTransportListener)this.listeners.get(i)).localContactChanged(this.local_contact);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1309 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {
/* 1314 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTTransportContact importContact(DataInputStream is, boolean is_bootstrap)
/*      */     throws IOException, DHTTransportException
/*      */   {
/* 1325 */     DHTTransportUDPContactImpl contact = DHTUDPUtils.deserialiseContact(this, is);
/*      */     
/* 1327 */     importContact(contact, is_bootstrap);
/*      */     
/* 1329 */     return contact;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DHTTransportUDPContact importContact(InetSocketAddress _address, byte _protocol_version, boolean is_bootstrap)
/*      */     throws DHTTransportException
/*      */   {
/* 1342 */     DHTTransportUDPContactImpl contact = new DHTTransportUDPContactImpl(false, this, _address, _address, _protocol_version, 0, 0L, (byte)0);
/*      */     
/* 1344 */     importContact(contact, is_bootstrap);
/*      */     
/* 1346 */     return contact;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void importContact(DHTTransportUDPContactImpl contact, boolean is_bootstrap)
/*      */   {
/*      */     try
/*      */     {
/* 1355 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1361 */       if (this.contact_history.size() < 32)
/*      */       {
/* 1363 */         this.contact_history.put(contact.getTransportAddress(), contact);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1368 */       this.this_mon.exit();
/*      */     }
/*      */     
/* 1371 */     this.request_handler.contactImported(contact, is_bootstrap);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void exportContact(DHTTransportContact contact, DataOutputStream os)
/*      */     throws IOException, DHTTransportException
/*      */   {
/* 1383 */     DHTUDPUtils.serialiseContact(os, contact);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Map<String, Object> exportContactToMap(DHTTransportContact contact)
/*      */   {
/* 1390 */     Map<String, Object> result = new HashMap();
/*      */     
/* 1392 */     result.put("v", Byte.valueOf(contact.getProtocolVersion()));
/*      */     
/* 1394 */     InetSocketAddress address = contact.getExternalAddress();
/*      */     
/* 1396 */     result.put("p", Integer.valueOf(address.getPort()));
/*      */     
/* 1398 */     InetAddress ia = address.getAddress();
/*      */     
/* 1400 */     if (ia == null)
/*      */     {
/*      */ 
/* 1403 */       result.put("h", address.getHostName());
/*      */     }
/*      */     else
/*      */     {
/* 1407 */       result.put("a", ia.getAddress());
/*      */     }
/*      */     
/* 1410 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public DHTTransportUDPContact importContact(Map<String, Object> map)
/*      */   {
/* 1417 */     int version = ((Number)map.get("v")).intValue();
/*      */     
/* 1419 */     int port = ((Number)map.get("p")).intValue();
/*      */     
/* 1421 */     byte[] a = (byte[])map.get("a");
/*      */     try
/*      */     {
/*      */       InetSocketAddress address;
/*      */       InetSocketAddress address;
/* 1426 */       if (a == null)
/*      */       {
/* 1428 */         address = InetSocketAddress.createUnresolved(new String((byte[])map.get("h"), "UTF-8"), port);
/*      */       }
/*      */       else {
/* 1431 */         address = new InetSocketAddress(InetAddress.getByAddress(a), port);
/*      */       }
/*      */       
/* 1434 */       DHTTransportUDPContactImpl contact = new DHTTransportUDPContactImpl(false, this, address, address, (byte)version, 0, 0L, (byte)0);
/*      */       
/* 1436 */       importContact(contact, false);
/*      */       
/* 1438 */       return contact;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1442 */       Debug.out(e);
/*      */     }
/* 1444 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void removeContact(DHTTransportContact contact)
/*      */   {
/* 1452 */     this.request_handler.contactRemoved(contact);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRequestHandler(DHTTransportRequestHandler _request_handler)
/*      */   {
/* 1459 */     this.request_handler = new DHTTransportRequestCounter(_request_handler, this.stats);
/*      */   }
/*      */   
/*      */ 
/*      */   public DHTTransportStats getStats()
/*      */   {
/* 1465 */     return this.stats;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkAddress(DHTTransportUDPContactImpl contact)
/*      */     throws DHTUDPPacketHandlerException
/*      */   {
/* 1535 */     if (this.ip_filter.isEnabled())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1540 */       InetAddress ia = contact.getTransportAddress().getAddress();
/*      */       
/* 1542 */       if (ia != null)
/*      */       {
/*      */ 
/*      */ 
/* 1546 */         byte[] addr = ia.getAddress();
/*      */         
/* 1548 */         if (this.bad_ip_bloom_filter == null)
/*      */         {
/* 1550 */           this.bad_ip_bloom_filter = BloomFilterFactory.createAddOnly(32000);
/*      */ 
/*      */ 
/*      */         }
/* 1554 */         else if (this.bad_ip_bloom_filter.contains(addr))
/*      */         {
/* 1556 */           throw new DHTUDPPacketHandlerException("IPFilter check fails (repeat)");
/*      */         }
/*      */         
/*      */ 
/* 1560 */         if (this.ip_filter.isInRange(contact.getTransportAddress().getAddress(), "DHT", null, this.logger.isEnabled(2)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1567 */           if (this.bad_ip_bloom_filter.getEntryCount() >= 3200)
/*      */           {
/* 1569 */             this.bad_ip_bloom_filter = BloomFilterFactory.createAddOnly(32000);
/*      */           }
/*      */           
/* 1572 */           this.bad_ip_bloom_filter.add(addr);
/*      */           
/* 1574 */           throw new DHTUDPPacketHandlerException("IPFilter check fails");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendPing(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler, long timeout, int priority)
/*      */   {
/*      */     try
/*      */     {
/* 1588 */       checkAddress(contact);
/*      */       
/* 1590 */       final long connection_id = getConnectionID();
/*      */       
/* 1592 */       DHTUDPPacketRequestPing request = new DHTUDPPacketRequestPing(this, connection_id, this.local_contact, contact);
/*      */       
/*      */ 
/* 1595 */       requestAltContacts(request);
/*      */       
/* 1597 */       this.stats.pingSent(request);
/*      */       
/* 1599 */       requestSendRequestProcessor(contact, request);
/*      */       
/* 1601 */       this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 1613 */             if (packet.getConnectionId() != connection_id)
/*      */             {
/* 1615 */               throw new Exception("connection id mismatch");
/*      */             }
/*      */             
/* 1618 */             handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */             
/* 1620 */             DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, this.val$handler, packet, elapsed_time);
/*      */             
/* 1622 */             DHTTransportUDPImpl.this.receiveAltContacts((DHTUDPPacketReplyPing)packet);
/*      */             
/* 1624 */             DHTTransportUDPImpl.this.stats.pingOK();
/*      */             
/* 1626 */             long proc_time = packet.getProcessingTime();
/*      */             
/* 1628 */             if (proc_time > 0L)
/*      */             {
/* 1630 */               elapsed_time -= proc_time;
/*      */               
/* 1632 */               if (elapsed_time < 0L)
/*      */               {
/* 1634 */                 elapsed_time = 0L;
/*      */               }
/*      */             }
/*      */             
/* 1638 */             this.val$handler.pingReply(handler, (int)elapsed_time);
/*      */           }
/*      */           catch (DHTUDPPacketHandlerException e)
/*      */           {
/* 1642 */             error(e);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1646 */             Debug.printStackTrace(e);
/*      */             
/* 1648 */             error(new DHTUDPPacketHandlerException("ping failed", e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void error(DHTUDPPacketHandlerException e)
/*      */         {
/* 1656 */           DHTTransportUDPImpl.this.stats.pingFailed();
/*      */           
/* 1658 */           this.val$handler.failed(handler, e); } }, timeout, priority);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1665 */       this.stats.pingFailed();
/*      */       
/* 1667 */       handler.failed(contact, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendPing(DHTTransportUDPContactImpl contact, DHTTransportReplyHandler handler)
/*      */   {
/* 1676 */     sendPing(contact, handler, this.request_timeout, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendImmediatePing(DHTTransportUDPContactImpl contact, DHTTransportReplyHandler handler, long timeout)
/*      */   {
/* 1685 */     sendPing(contact, handler, timeout, 99);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendKeyBlockRequest(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler, byte[] block_request, byte[] block_signature)
/*      */   {
/*      */     try
/*      */     {
/* 1696 */       checkAddress(contact);
/*      */       
/* 1698 */       final long connection_id = getConnectionID();
/*      */       
/* 1700 */       DHTUDPPacketRequestKeyBlock request = new DHTUDPPacketRequestKeyBlock(this, connection_id, this.local_contact, contact);
/*      */       
/*      */ 
/* 1703 */       request.setKeyBlockDetails(block_request, block_signature);
/*      */       
/* 1705 */       this.stats.keyBlockSent(request);
/*      */       
/* 1707 */       request.setRandomID(contact.getRandomID());
/*      */       
/* 1709 */       requestSendRequestProcessor(contact, request);
/*      */       
/* 1711 */       this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 1723 */             if (packet.getConnectionId() != connection_id)
/*      */             {
/* 1725 */               throw new Exception("connection id mismatch");
/*      */             }
/*      */             
/* 1728 */             handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */             
/* 1730 */             DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, this.val$handler, packet, elapsed_time);
/*      */             
/* 1732 */             DHTTransportUDPImpl.this.stats.keyBlockOK();
/*      */             
/* 1734 */             this.val$handler.keyBlockReply(handler);
/*      */           }
/*      */           catch (DHTUDPPacketHandlerException e)
/*      */           {
/* 1738 */             error(e);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1742 */             Debug.printStackTrace(e);
/*      */             
/* 1744 */             error(new DHTUDPPacketHandlerException("send key block failed", e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void error(DHTUDPPacketHandlerException e)
/*      */         {
/* 1752 */           DHTTransportUDPImpl.this.stats.keyBlockFailed();
/*      */           
/* 1754 */           this.val$handler.failed(handler, e); } }, this.request_timeout, 1);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1761 */       this.stats.keyBlockFailed();
/*      */       
/* 1763 */       handler.failed(contact, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void sendStats(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler)
/*      */   {
/*      */     try
/*      */     {
/* 1775 */       checkAddress(contact);
/*      */       
/* 1777 */       final long connection_id = getConnectionID();
/*      */       
/* 1779 */       DHTUDPPacketRequestStats request = new DHTUDPPacketRequestStats(this, connection_id, this.local_contact, contact);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1784 */       this.stats.statsSent(request);
/*      */       
/* 1786 */       requestSendRequestProcessor(contact, request);
/*      */       
/* 1788 */       this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 1800 */             if (packet.getConnectionId() != connection_id)
/*      */             {
/* 1802 */               throw new Exception("connection id mismatch");
/*      */             }
/*      */             
/* 1805 */             handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */             
/* 1807 */             DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, this.val$handler, packet, elapsed_time);
/*      */             
/* 1809 */             DHTUDPPacketReplyStats reply = (DHTUDPPacketReplyStats)packet;
/*      */             
/* 1811 */             DHTTransportUDPImpl.this.stats.statsOK();
/*      */             
/* 1813 */             if (reply.getStatsType() == 1)
/*      */             {
/* 1815 */               this.val$handler.statsReply(handler, reply.getOriginalStats());
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1820 */               System.out.println("new stats reply:" + reply.getString());
/*      */             }
/*      */           }
/*      */           catch (DHTUDPPacketHandlerException e)
/*      */           {
/* 1825 */             error(e);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 1829 */             Debug.printStackTrace(e);
/*      */             
/* 1831 */             error(new DHTUDPPacketHandlerException("stats failed", e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void error(DHTUDPPacketHandlerException e)
/*      */         {
/* 1839 */           DHTTransportUDPImpl.this.stats.statsFailed();
/*      */           
/* 1841 */           this.val$handler.failed(handler, e); } }, this.request_timeout, 2);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1848 */       this.stats.statsFailed();
/*      */       
/* 1850 */       handler.failed(contact, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected InetSocketAddress askContactForExternalAddress(DHTTransportUDPContactImpl contact)
/*      */   {
/*      */     try
/*      */     {
/* 1861 */       checkAddress(contact);
/*      */       
/* 1863 */       long connection_id = getConnectionID();
/*      */       
/* 1865 */       DHTUDPPacketRequestPing request = new DHTUDPPacketRequestPing(this, connection_id, this.local_contact, contact);
/*      */       
/*      */ 
/* 1868 */       this.stats.pingSent(request);
/*      */       
/* 1870 */       final AESemaphore sem = new AESemaphore("DHTTransUDP:extping");
/*      */       
/* 1872 */       final InetSocketAddress[] result = new InetSocketAddress[1];
/*      */       
/* 1874 */       this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void packetReceived(DHTUDPPacketReply _packet, InetSocketAddress from_address, long elapsed_time)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 1887 */             if ((_packet instanceof DHTUDPPacketReplyPing))
/*      */             {
/*      */ 
/*      */ 
/* 1891 */               result[0] = DHTTransportUDPImpl.this.local_contact.getExternalAddress();
/*      */             }
/* 1893 */             else if ((_packet instanceof DHTUDPPacketReplyError))
/*      */             {
/* 1895 */               DHTUDPPacketReplyError packet = (DHTUDPPacketReplyError)_packet;
/*      */               
/* 1897 */               if (packet.getErrorType() == 1)
/*      */               {
/* 1899 */                 result[0] = packet.getOriginatingAddress();
/*      */               }
/*      */             }
/*      */           }
/*      */           finally {
/* 1904 */             sem.release();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */         public void error(DHTUDPPacketHandlerException e)
/*      */         {
/*      */           try
/*      */           {
/* 1913 */             DHTTransportUDPImpl.this.stats.pingFailed();
/*      */           }
/*      */           finally
/*      */           {
/* 1917 */             sem.release(); } } }, 5000L, 0);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1923 */       sem.reserve(5000L);
/*      */       
/* 1925 */       return result[0];
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/* 1929 */       this.stats.pingFailed();
/*      */     }
/* 1931 */     return null;
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
/*      */   public void sendStore(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler, byte[][] keys, DHTTransportValue[][] value_sets, int priority)
/*      */   {
/* 1945 */     final long connection_id = getConnectionID();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1957 */     int packet_count = 0;
/*      */     try
/*      */     {
/* 1960 */       checkAddress(contact);
/*      */       
/* 1962 */       int current_key_index = 0;
/* 1963 */       int current_value_index = 0;
/*      */       
/* 1965 */       while (current_key_index < keys.length)
/*      */       {
/* 1967 */         packet_count++;
/*      */         
/* 1969 */         int space = 1357;
/*      */         
/* 1971 */         List key_list = new ArrayList();
/* 1972 */         List values_list = new ArrayList();
/*      */         
/* 1974 */         key_list.add(keys[current_key_index]);
/*      */         
/* 1976 */         space -= keys[current_key_index].length + 1;
/*      */         
/* 1978 */         values_list.add(new ArrayList());
/*      */         
/*      */ 
/* 1981 */         while ((space > 0) && (current_key_index < keys.length))
/*      */         {
/* 1983 */           if (current_value_index == value_sets[current_key_index].length)
/*      */           {
/*      */ 
/*      */ 
/* 1987 */             current_key_index++;
/*      */             
/* 1989 */             current_value_index = 0;
/*      */             
/* 1991 */             if (key_list.size() == 255) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1998 */             if (current_key_index == keys.length) {
/*      */               break;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2005 */             key_list.add(keys[current_key_index]);
/*      */             
/* 2007 */             space -= keys[current_key_index].length + 1;
/*      */             
/* 2009 */             values_list.add(new ArrayList());
/*      */           }
/*      */           
/* 2012 */           DHTTransportValue value = value_sets[current_key_index][current_value_index];
/*      */           
/* 2014 */           int entry_size = 26 + value.getValue().length + 1;
/*      */           
/* 2016 */           List values = (List)values_list.get(values_list.size() - 1);
/*      */           
/* 2018 */           if ((space < entry_size) || (values.size() == 255)) {
/*      */             break;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2027 */           values.add(value);
/*      */           
/* 2029 */           space -= entry_size;
/*      */           
/* 2031 */           current_value_index++;
/*      */         }
/*      */         
/* 2034 */         int packet_entries = key_list.size();
/*      */         
/* 2036 */         if (packet_entries > 0)
/*      */         {
/*      */ 
/*      */ 
/* 2040 */           if (((List)values_list.get(packet_entries - 1)).size() == 0)
/*      */           {
/* 2042 */             packet_entries--;
/*      */           }
/*      */         }
/*      */         
/* 2046 */         if (packet_entries == 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 2051 */         byte[][] packet_keys = new byte[packet_entries][];
/* 2052 */         DHTTransportValue[][] packet_value_sets = new DHTTransportValue[packet_entries][];
/*      */         
/*      */ 
/*      */ 
/* 2056 */         for (int i = 0; i < packet_entries; i++)
/*      */         {
/* 2058 */           packet_keys[i] = ((byte[])(byte[])key_list.get(i));
/*      */           
/* 2060 */           List values = (List)values_list.get(i);
/*      */           
/* 2062 */           packet_value_sets[i] = new DHTTransportValue[values.size()];
/*      */           
/* 2064 */           for (int j = 0; j < values.size(); j++)
/*      */           {
/* 2066 */             packet_value_sets[i][j] = ((DHTTransportValue)values.get(j));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2075 */         DHTUDPPacketRequestStore request = new DHTUDPPacketRequestStore(this, connection_id, this.local_contact, contact);
/*      */         
/*      */ 
/* 2078 */         this.stats.storeSent(request);
/*      */         
/* 2080 */         request.setRandomID(contact.getRandomID());
/*      */         
/* 2082 */         request.setKeys(packet_keys);
/*      */         
/* 2084 */         request.setValueSets(packet_value_sets);
/*      */         
/* 2086 */         final int f_packet_count = packet_count;
/*      */         
/* 2088 */         requestSendRequestProcessor(contact, request);
/*      */         
/* 2090 */         this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */           {
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */ 
/* 2102 */               if (packet.getConnectionId() != connection_id)
/*      */               {
/* 2104 */                 throw new Exception("connection id mismatch: sender=" + from_address + ",packet=" + packet.getString());
/*      */               }
/*      */               
/* 2107 */               handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */               
/* 2109 */               DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, f_packet_count, packet, elapsed_time);
/*      */               
/* 2111 */               DHTUDPPacketReplyStore reply = (DHTUDPPacketReplyStore)packet;
/*      */               
/* 2113 */               DHTTransportUDPImpl.this.stats.storeOK();
/*      */               
/* 2115 */               if (this.val$f_packet_count == 1)
/*      */               {
/* 2117 */                 f_packet_count.storeReply(handler, reply.getDiversificationTypes());
/*      */               }
/*      */             }
/*      */             catch (DHTUDPPacketHandlerException e)
/*      */             {
/* 2122 */               error(e);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2126 */               Debug.printStackTrace(e);
/*      */               
/* 2128 */               error(new DHTUDPPacketHandlerException("store failed", e));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void error(DHTUDPPacketHandlerException e)
/*      */           {
/* 2136 */             DHTTransportUDPImpl.this.stats.storeFailed();
/*      */             
/* 2138 */             if (this.val$f_packet_count == 1)
/*      */             {
/* 2140 */               f_packet_count.failed(handler, e); } } }, this.store_timeout, priority);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 2150 */       this.stats.storeFailed();
/*      */       
/* 2152 */       if (packet_count <= 1)
/*      */       {
/* 2154 */         handler.failed(contact, e);
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
/*      */   public void sendQueryStore(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler, int header_size, List<Object[]> key_details)
/*      */   {
/*      */     try
/*      */     {
/* 2169 */       checkAddress(contact);
/*      */       
/* 2171 */       final long connection_id = getConnectionID();
/*      */       
/* 2173 */       Iterator<Object[]> it = key_details.iterator();
/*      */       
/* 2175 */       byte[] current_prefix = null;
/* 2176 */       Iterator<byte[]> current_suffixes = null;
/*      */       
/* 2178 */       List<DHTUDPPacketRequestQueryStorage> requests = new ArrayList();
/*      */       
/*      */ 
/* 2181 */       while (it.hasNext())
/*      */       {
/* 2183 */         int space = 1354;
/*      */         
/* 2185 */         DHTUDPPacketRequestQueryStorage request = new DHTUDPPacketRequestQueryStorage(this, connection_id, this.local_contact, contact);
/*      */         
/*      */ 
/* 2188 */         List<Object[]> packet_key_details = new ArrayList();
/*      */         for (;;) {
/* 2190 */           if ((space <= 0) || (!it.hasNext()))
/*      */             break label276;
/* 2192 */           if (current_prefix == null)
/*      */           {
/* 2194 */             Object[] entry = (Object[])it.next();
/*      */             
/* 2196 */             current_prefix = (byte[])entry[0];
/*      */             
/* 2198 */             List<byte[]> l = (List)entry[1];
/*      */             
/* 2200 */             current_suffixes = l.iterator();
/*      */           }
/*      */           
/* 2203 */           if (current_suffixes.hasNext())
/*      */           {
/* 2205 */             int min_space = header_size + 3;
/*      */             
/* 2207 */             if (space < min_space)
/*      */             {
/* 2209 */               request.setDetails(header_size, packet_key_details);
/*      */               
/* 2211 */               requests.add(request);
/*      */               
/* 2213 */               break;
/*      */             }
/*      */             
/* 2216 */             List<byte[]> s = new ArrayList();
/*      */             
/* 2218 */             packet_key_details.add(new Object[] { current_prefix, s });
/*      */             
/* 2220 */             int prefix_size = current_prefix.length;
/* 2221 */             int suffix_size = header_size - prefix_size;
/*      */             
/* 2223 */             space -= 3 + prefix_size;
/*      */             
/* 2225 */             while ((space >= suffix_size) && (current_suffixes.hasNext()))
/*      */             {
/* 2227 */               s.add(current_suffixes.next());
/*      */             }
/*      */             
/* 2230 */             continue;
/*      */           }
/* 2232 */           current_prefix = null;
/*      */         }
/*      */         
/*      */         label276:
/* 2236 */         if (!it.hasNext())
/*      */         {
/* 2238 */           request.setDetails(header_size, packet_key_details);
/*      */           
/* 2240 */           requests.add(request);
/*      */         }
/*      */       }
/*      */       
/* 2244 */       final Object[] replies = new Object[requests.size()];
/*      */       
/* 2246 */       for (int i = 0; i < requests.size(); i++)
/*      */       {
/* 2248 */         DHTUDPPacketRequestQueryStorage request = (DHTUDPPacketRequestQueryStorage)requests.get(i);
/*      */         
/* 2250 */         final int f_i = i;
/*      */         
/* 2252 */         this.stats.queryStoreSent(request);
/*      */         
/* 2254 */         requestSendRequestProcessor(contact, request);
/*      */         
/* 2256 */         this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */           {
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/*      */ 
/*      */ 
/* 2268 */               if (packet.getConnectionId() != connection_id)
/*      */               {
/* 2270 */                 throw new Exception("connection id mismatch");
/*      */               }
/*      */               
/* 2273 */               handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */               
/* 2275 */               DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, replies, packet, elapsed_time);
/*      */               
/* 2277 */               DHTUDPPacketReplyQueryStorage reply = (DHTUDPPacketReplyQueryStorage)packet;
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 2282 */               handler.setRandomID(reply.getRandomID());
/*      */               
/* 2284 */               DHTTransportUDPImpl.this.stats.queryStoreOK();
/*      */               
/* 2286 */               synchronized (f_i)
/*      */               {
/* 2288 */                 f_i[this.val$f_i] = reply;
/*      */                 
/* 2290 */                 checkComplete();
/*      */               }
/*      */             }
/*      */             catch (DHTUDPPacketHandlerException e)
/*      */             {
/* 2295 */               error(e);
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2299 */               Debug.printStackTrace(e);
/*      */               
/* 2301 */               error(new DHTUDPPacketHandlerException("queryStore failed", e));
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public void error(DHTUDPPacketHandlerException e)
/*      */           {
/* 2309 */             DHTTransportUDPImpl.this.stats.queryStoreFailed();
/*      */             
/* 2311 */             synchronized (f_i)
/*      */             {
/* 2313 */               f_i[this.val$f_i] = e;
/*      */               
/* 2315 */               checkComplete();
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */           protected void checkComplete()
/*      */           {
/* 2322 */             DHTUDPPacketHandlerException last_error = null;
/*      */             
/* 2324 */             for (int i = 0; i < f_i.length; i++)
/*      */             {
/* 2326 */               Object o = f_i[i];
/*      */               
/* 2328 */               if (o == null)
/*      */               {
/* 2330 */                 return;
/*      */               }
/*      */               
/* 2333 */               if ((o instanceof DHTUDPPacketHandlerException))
/*      */               {
/* 2335 */                 last_error = (DHTUDPPacketHandlerException)o;
/*      */               }
/*      */             }
/*      */             
/* 2339 */             if (last_error != null)
/*      */             {
/* 2341 */               replies.failed(handler, last_error);
/*      */ 
/*      */ 
/*      */             }
/* 2345 */             else if (f_i.length == 1)
/*      */             {
/* 2347 */               replies.queryStoreReply(handler, ((DHTUDPPacketReplyQueryStorage)f_i[0]).getResponse());
/*      */             }
/*      */             else
/*      */             {
/* 2351 */               List<byte[]> response = new ArrayList();
/*      */               
/* 2353 */               for (int i = 0; i < f_i.length; i++)
/*      */               {
/* 2355 */                 response.addAll(((DHTUDPPacketReplyQueryStorage)f_i[0]).getResponse());
/*      */               }
/*      */               
/* 2358 */               replies.queryStoreReply(handler, response); } } }, this.request_timeout, 1);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 2368 */       this.stats.queryStoreFailed();
/*      */       
/* 2370 */       handler.failed(contact, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sendFindNode(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler, byte[] nid)
/*      */   {
/*      */     try
/*      */     {
/* 2383 */       checkAddress(contact);
/*      */       
/* 2385 */       final long connection_id = getConnectionID();
/*      */       
/* 2387 */       DHTUDPPacketRequestFindNode request = new DHTUDPPacketRequestFindNode(this, connection_id, this.local_contact, contact);
/*      */       
/*      */ 
/* 2390 */       this.stats.findNodeSent(request);
/*      */       
/* 2392 */       request.setID(nid);
/*      */       
/* 2394 */       request.setNodeStatus(getNodeStatus());
/*      */       
/* 2396 */       request.setEstimatedDHTSize(this.request_handler.getTransportEstimatedDHTSize());
/*      */       
/* 2398 */       requestSendRequestProcessor(contact, request);
/*      */       
/* 2400 */       this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 2412 */             if (packet.getConnectionId() != connection_id)
/*      */             {
/* 2414 */               throw new Exception("connection id mismatch");
/*      */             }
/*      */             
/* 2417 */             handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */             
/* 2419 */             DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, this.val$handler, packet, elapsed_time);
/*      */             
/* 2421 */             DHTUDPPacketReplyFindNode reply = (DHTUDPPacketReplyFindNode)packet;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2426 */             handler.setRandomID(reply.getRandomID());
/*      */             
/* 2428 */             DHTTransportUDPImpl.this.updateContactStatus(handler, reply.getNodeStatus(), false);
/*      */             
/* 2430 */             DHTTransportUDPImpl.this.request_handler.setTransportEstimatedDHTSize(reply.getEstimatedDHTSize());
/*      */             
/* 2432 */             DHTTransportUDPImpl.this.stats.findNodeOK();
/*      */             
/* 2434 */             DHTTransportContact[] contacts = reply.getContacts();
/*      */             
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/* 2440 */               DHTTransportUDPImpl.this.this_mon.enter();
/*      */               
/* 2442 */               for (int i = 0; (DHTTransportUDPImpl.this.contact_history.size() < 32) && (i < contacts.length); i++)
/*      */               {
/* 2444 */                 DHTTransportUDPContact c = (DHTTransportUDPContact)contacts[i];
/*      */                 
/* 2446 */                 DHTTransportUDPImpl.this.contact_history.put(c.getTransportAddress(), c);
/*      */               }
/*      */             }
/*      */             finally {
/* 2450 */               DHTTransportUDPImpl.this.this_mon.exit();
/*      */             }
/*      */             
/* 2453 */             this.val$handler.findNodeReply(handler, contacts);
/*      */           }
/*      */           catch (DHTUDPPacketHandlerException e)
/*      */           {
/* 2457 */             error(e);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2461 */             Debug.printStackTrace(e);
/*      */             
/* 2463 */             error(new DHTUDPPacketHandlerException("findNode failed", e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void error(DHTUDPPacketHandlerException e)
/*      */         {
/* 2471 */           DHTTransportUDPImpl.this.stats.findNodeFailed();
/*      */           
/* 2473 */           this.val$handler.failed(handler, e); } }, this.request_timeout, 1);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 2480 */       this.stats.findNodeFailed();
/*      */       
/* 2482 */       handler.failed(contact, e);
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
/*      */   public void sendFindValue(DHTTransportUDPContactImpl contact, final DHTTransportReplyHandler handler, byte[] key, int max_values, short flags)
/*      */   {
/*      */     try
/*      */     {
/* 2497 */       checkAddress(contact);
/*      */       
/* 2499 */       final long connection_id = getConnectionID();
/*      */       
/* 2501 */       DHTUDPPacketRequestFindValue request = new DHTUDPPacketRequestFindValue(this, connection_id, this.local_contact, contact);
/*      */       
/*      */ 
/* 2504 */       this.stats.findValueSent(request);
/*      */       
/* 2506 */       request.setID(key);
/*      */       
/* 2508 */       request.setMaximumValues(max_values);
/*      */       
/* 2510 */       request.setFlags((byte)flags);
/*      */       
/* 2512 */       requestSendRequestProcessor(contact, request);
/*      */       
/* 2514 */       this.packet_handler.sendAndReceive(request, contact.getTransportAddress(), new DHTUDPPacketReceiver()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void packetReceived(DHTUDPPacketReply packet, InetSocketAddress from_address, long elapsed_time)
/*      */         {
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*      */ 
/*      */ 
/* 2526 */             if (packet.getConnectionId() != connection_id)
/*      */             {
/* 2528 */               throw new Exception("connection id mismatch");
/*      */             }
/*      */             
/* 2531 */             handler.setInstanceIDAndVersion(packet.getTargetInstanceID(), packet.getProtocolVersion());
/*      */             
/* 2533 */             DHTTransportUDPImpl.this.requestSendReplyProcessor(handler, this.val$handler, packet, elapsed_time);
/*      */             
/* 2535 */             DHTUDPPacketReplyFindValue reply = (DHTUDPPacketReplyFindValue)packet;
/*      */             
/* 2537 */             DHTTransportUDPImpl.this.stats.findValueOK();
/*      */             
/* 2539 */             DHTTransportValue[] res = reply.getValues();
/*      */             
/* 2541 */             if (res != null)
/*      */             {
/* 2543 */               boolean continuation = reply.hasContinuation();
/*      */               
/* 2545 */               this.val$handler.findValueReply(handler, res, reply.getDiversificationType(), continuation);
/*      */             }
/*      */             else
/*      */             {
/* 2549 */               this.val$handler.findValueReply(handler, reply.getContacts());
/*      */             }
/*      */           }
/*      */           catch (DHTUDPPacketHandlerException e) {
/* 2553 */             error(e);
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/* 2557 */             Debug.printStackTrace(e);
/*      */             
/* 2559 */             error(new DHTUDPPacketHandlerException("findValue failed", e));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void error(DHTUDPPacketHandlerException e)
/*      */         {
/* 2567 */           DHTTransportUDPImpl.this.stats.findValueFailed();
/*      */           
/* 2569 */           this.val$handler.failed(handler, e); } }, this.request_timeout, 0);
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 2576 */       if (!(e instanceof DHTUDPPacketHandlerException))
/*      */       {
/* 2578 */         this.stats.findValueFailed();
/*      */         
/* 2580 */         handler.failed(contact, e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected DHTTransportFullStats getFullStats(DHTTransportUDPContactImpl contact)
/*      */   {
/* 2589 */     if (contact == this.local_contact)
/*      */     {
/* 2591 */       return this.request_handler.statsRequest(contact);
/*      */     }
/*      */     
/* 2594 */     final DHTTransportFullStats[] res = { null };
/*      */     
/* 2596 */     final AESemaphore sem = new AESemaphore("DHTTransportUDP:getFullStats");
/*      */     
/* 2598 */     sendStats(contact, new DHTTransportReplyHandlerAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void statsReply(DHTTransportContact _contact, DHTTransportFullStats _stats)
/*      */       {
/*      */ 
/*      */ 
/* 2606 */         res[0] = _stats;
/*      */         
/* 2608 */         sem.release();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void failed(DHTTransportContact _contact, Throwable _error)
/*      */       {
/* 2616 */         sem.release();
/*      */       }
/*      */       
/*      */ 
/* 2620 */     });
/* 2621 */     sem.reserve();
/*      */     
/* 2623 */     return res[0];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler)
/*      */   {
/* 2631 */     this.xfer_handler.registerTransferHandler(handler_key, handler);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void registerTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler, Map<String, Object> options)
/*      */   {
/* 2640 */     this.xfer_handler.registerTransferHandler(handler_key, handler, options);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void unregisterTransferHandler(byte[] handler_key, DHTTransportTransferHandler handler)
/*      */   {
/* 2648 */     this.xfer_handler.unregisterTransferHandler(handler_key, handler);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] readTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/* 2661 */     InetAddress ia = target.getAddress().getAddress();
/*      */     
/* 2663 */     if ((((ia instanceof Inet4Address)) && (this.v6)) || (((ia instanceof Inet6Address)) && (!this.v6)))
/*      */     {
/*      */ 
/* 2666 */       throw new DHTTransportException("Incompatible address");
/*      */     }
/*      */     
/* 2669 */     return this.xfer_handler.readTransfer(listener, target, handler_key, key, timeout);
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
/*      */   public void writeTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] key, byte[] data, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/* 2683 */     InetAddress ia = target.getAddress().getAddress();
/*      */     
/* 2685 */     if ((((ia instanceof Inet4Address)) && (this.v6)) || (((ia instanceof Inet6Address)) && (!this.v6)))
/*      */     {
/*      */ 
/* 2688 */       throw new DHTTransportException("Incompatible address");
/*      */     }
/*      */     
/* 2691 */     this.xfer_handler.writeTransfer(listener, target, handler_key, key, data, timeout);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] writeReadTransfer(DHTTransportProgressListener listener, DHTTransportContact target, byte[] handler_key, byte[] data, long timeout)
/*      */     throws DHTTransportException
/*      */   {
/* 2704 */     InetAddress ia = target.getAddress().getAddress();
/*      */     
/* 2706 */     if ((((ia instanceof Inet4Address)) && (this.v6)) || (((ia instanceof Inet6Address)) && (!this.v6)))
/*      */     {
/*      */ 
/* 2709 */       throw new DHTTransportException("Incompatible address");
/*      */     }
/*      */     
/* 2712 */     return this.xfer_handler.writeReadTransfer(listener, target, handler_key, data, timeout);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void dataRequest(DHTTransportUDPContactImpl originator, DHTUDPPacketData req)
/*      */   {
/* 2720 */     this.stats.dataReceived();
/*      */     
/* 2722 */     this.xfer_handler.receivePacket(originator, new DHTTransferHandler.Packet(req.getConnectionId(), req.getPacketType(), req.getTransferKey(), req.getRequestKey(), req.getData(), req.getStartPosition(), req.getLength(), req.getTotalLength()));
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
/*      */   public void process(DHTUDPPacketRequest request, boolean alien)
/*      */   {
/* 2740 */     process(this.packet_handler, request, alien);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void process(DHTUDPPacketHandlerStub packet_handler_stub, DHTUDPPacketRequest request, boolean alien)
/*      */   {
/* 2749 */     if (this.request_handler == null)
/*      */     {
/* 2751 */       this.logger.log("Ignoring packet as not yet ready to process");
/*      */       
/* 2753 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 2757 */       this.stats.incomingRequestReceived(request, alien);
/*      */       
/* 2759 */       InetSocketAddress transport_address = request.getAddress();
/*      */       
/* 2761 */       DHTTransportUDPContactImpl originating_contact = new DHTTransportUDPContactImpl(false, this, transport_address, request.getOriginatorAddress(), request.getOriginatorVersion(), request.getOriginatorInstanceID(), request.getClockSkew(), request.getGenericFlags());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       try
/*      */       {
/* 2773 */         checkAddress(originating_contact);
/*      */       }
/*      */       catch (DHTUDPPacketHandlerException e)
/*      */       {
/* 2777 */         return;
/*      */       }
/*      */       
/* 2780 */       requestReceiveRequestProcessor(originating_contact, request);
/*      */       
/* 2782 */       boolean bad_originator = !originating_contact.addressMatchesID();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2787 */       if ((bad_originator) && (!this.bootstrap_node))
/*      */       {
/* 2789 */         String contact_string = originating_contact.getString();
/*      */         
/* 2791 */         if (this.recent_reports.get(contact_string) == null)
/*      */         {
/* 2793 */           this.recent_reports.put(contact_string, "");
/*      */           
/* 2795 */           this.logger.log("Node " + contact_string + " has incorrect ID, reporting it to them");
/*      */         }
/*      */         
/* 2798 */         DHTUDPPacketReplyError reply = new DHTUDPPacketReplyError(this, request, this.local_contact, originating_contact);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2805 */         reply.setErrorType(1);
/*      */         
/* 2807 */         reply.setOriginatingAddress(originating_contact.getTransportAddress());
/*      */         
/* 2809 */         requestReceiveReplyProcessor(originating_contact, reply);
/*      */         
/* 2811 */         packet_handler_stub.send(reply, request.getAddress());
/*      */       }
/*      */       else
/*      */       {
/* 2815 */         if (bad_originator)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 2820 */           originating_contact = new DHTTransportUDPContactImpl(false, this, transport_address, transport_address, request.getOriginatorVersion(), request.getOriginatorInstanceID(), request.getClockSkew(), request.getGenericFlags());
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
/* 2833 */           contactAlive(originating_contact);
/*      */         }
/*      */         
/* 2836 */         if ((request instanceof DHTUDPPacketRequestPing))
/*      */         {
/* 2838 */           if (!this.bootstrap_node)
/*      */           {
/* 2840 */             this.request_handler.pingRequest(originating_contact);
/*      */             
/* 2842 */             DHTUDPPacketRequestPing ping = (DHTUDPPacketRequestPing)request;
/*      */             
/* 2844 */             DHTUDPPacketReplyPing reply = new DHTUDPPacketReplyPing(this, ping, this.local_contact, originating_contact);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2851 */             sendAltContacts(ping, reply);
/*      */             
/* 2853 */             requestReceiveReplyProcessor(originating_contact, reply);
/*      */             
/* 2855 */             packet_handler_stub.send(reply, request.getAddress());
/*      */           }
/* 2857 */         } else if ((request instanceof DHTUDPPacketRequestKeyBlock))
/*      */         {
/* 2859 */           if (!this.bootstrap_node)
/*      */           {
/* 2861 */             DHTUDPPacketRequestKeyBlock kb_request = (DHTUDPPacketRequestKeyBlock)request;
/*      */             
/* 2863 */             originating_contact.setRandomID(kb_request.getRandomID());
/*      */             
/* 2865 */             this.request_handler.keyBlockRequest(originating_contact, kb_request.getKeyBlockRequest(), kb_request.getKeyBlockSignature());
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 2870 */             DHTUDPPacketReplyKeyBlock reply = new DHTUDPPacketReplyKeyBlock(this, kb_request, this.local_contact, originating_contact);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2877 */             requestReceiveReplyProcessor(originating_contact, reply);
/*      */             
/* 2879 */             packet_handler_stub.send(reply, request.getAddress());
/*      */           }
/* 2881 */         } else if ((request instanceof DHTUDPPacketRequestStats))
/*      */         {
/* 2883 */           DHTUDPPacketRequestStats stats_request = (DHTUDPPacketRequestStats)request;
/*      */           
/* 2885 */           DHTUDPPacketReplyStats reply = new DHTUDPPacketReplyStats(this, stats_request, this.local_contact, originating_contact);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2892 */           int type = stats_request.getStatsType();
/*      */           
/* 2894 */           if (type == 1)
/*      */           {
/* 2896 */             DHTTransportFullStats full_stats = this.request_handler.statsRequest(originating_contact);
/*      */             
/* 2898 */             reply.setOriginalStats(full_stats);
/*      */           }
/* 2900 */           else if (type == 2)
/*      */           {
/* 2902 */             DHTNetworkPositionProvider prov = DHTNetworkPositionManager.getProvider((byte)5);
/*      */             
/* 2904 */             byte[] data = new byte[0];
/*      */             
/* 2906 */             if (prov != null)
/*      */             {
/* 2908 */               ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */               
/* 2910 */               DataOutputStream dos = new DataOutputStream(baos);
/*      */               
/* 2912 */               prov.serialiseStats(dos);
/*      */               
/* 2914 */               dos.flush();
/*      */               
/* 2916 */               data = baos.toByteArray();
/*      */             }
/*      */             
/* 2919 */             reply.setNewStats(data, 5);
/*      */           }
/*      */           else
/*      */           {
/* 2923 */             throw new IOException("Uknown stats type '" + type + "'");
/*      */           }
/*      */           
/* 2926 */           requestReceiveReplyProcessor(originating_contact, reply);
/*      */           
/* 2928 */           packet_handler_stub.send(reply, request.getAddress());
/*      */         }
/* 2930 */         else if ((request instanceof DHTUDPPacketRequestStore))
/*      */         {
/* 2932 */           if (!this.bootstrap_node)
/*      */           {
/* 2934 */             DHTUDPPacketRequestStore store_request = (DHTUDPPacketRequestStore)request;
/*      */             
/* 2936 */             originating_contact.setRandomID(store_request.getRandomID());
/*      */             
/* 2938 */             DHTTransportStoreReply res = this.request_handler.storeRequest(originating_contact, store_request.getKeys(), store_request.getValueSets());
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2944 */             if (res.blocked())
/*      */             {
/* 2946 */               if (originating_contact.getProtocolVersion() >= 14)
/*      */               {
/* 2948 */                 DHTUDPPacketReplyError reply = new DHTUDPPacketReplyError(this, request, this.local_contact, originating_contact);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2955 */                 reply.setErrorType(2);
/*      */                 
/* 2957 */                 reply.setKeyBlockDetails(res.getBlockRequest(), res.getBlockSignature());
/*      */                 
/* 2959 */                 requestReceiveReplyProcessor(originating_contact, reply);
/*      */                 
/* 2961 */                 packet_handler_stub.send(reply, request.getAddress());
/*      */               }
/*      */               else {
/* 2964 */                 DHTUDPPacketReplyStore reply = new DHTUDPPacketReplyStore(this, store_request, this.local_contact, originating_contact);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2971 */                 reply.setDiversificationTypes(new byte[store_request.getKeys().length]);
/*      */                 
/* 2973 */                 requestReceiveReplyProcessor(originating_contact, reply);
/*      */                 
/* 2975 */                 packet_handler_stub.send(reply, request.getAddress());
/*      */               }
/*      */             }
/*      */             else {
/* 2979 */               DHTUDPPacketReplyStore reply = new DHTUDPPacketReplyStore(this, store_request, this.local_contact, originating_contact);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2986 */               reply.setDiversificationTypes(res.getDiversificationTypes());
/*      */               
/* 2988 */               requestReceiveReplyProcessor(originating_contact, reply);
/*      */               
/* 2990 */               packet_handler_stub.send(reply, request.getAddress());
/*      */             }
/*      */           }
/* 2993 */         } else if ((request instanceof DHTUDPPacketRequestQueryStorage))
/*      */         {
/* 2995 */           DHTUDPPacketRequestQueryStorage query_request = (DHTUDPPacketRequestQueryStorage)request;
/*      */           
/* 2997 */           DHTTransportQueryStoreReply res = this.request_handler.queryStoreRequest(originating_contact, query_request.getHeaderLength(), query_request.getKeys());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3003 */           DHTUDPPacketReplyQueryStorage reply = new DHTUDPPacketReplyQueryStorage(this, query_request, this.local_contact, originating_contact);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3010 */           reply.setRandomID(originating_contact.getRandomID());
/*      */           
/* 3012 */           reply.setResponse(res.getHeaderSize(), res.getEntries());
/*      */           
/* 3014 */           requestReceiveReplyProcessor(originating_contact, reply);
/*      */           
/* 3016 */           packet_handler_stub.send(reply, request.getAddress());
/*      */         }
/* 3018 */         else if ((request instanceof DHTUDPPacketRequestFindNode))
/*      */         {
/* 3020 */           DHTUDPPacketRequestFindNode find_request = (DHTUDPPacketRequestFindNode)request;
/*      */           
/*      */ 
/*      */           boolean acceptable;
/*      */           
/*      */           boolean acceptable;
/*      */           
/* 3027 */           if (this.bootstrap_node)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3033 */             acceptable = (bad_originator) || (Arrays.equals(find_request.getID(), originating_contact.getID()));
/*      */           }
/*      */           else
/*      */           {
/* 3037 */             acceptable = true;
/*      */           }
/*      */           
/* 3040 */           if (acceptable)
/*      */           {
/* 3042 */             if (find_request.getProtocolVersion() >= 22)
/*      */             {
/* 3044 */               updateContactStatus(originating_contact, find_request.getNodeStatus(), true);
/*      */               
/* 3046 */               this.request_handler.setTransportEstimatedDHTSize(find_request.getEstimatedDHTSize());
/*      */             }
/*      */             
/* 3049 */             DHTTransportContact[] res = this.request_handler.findNodeRequest(originating_contact, find_request.getID());
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 3054 */             DHTUDPPacketReplyFindNode reply = new DHTUDPPacketReplyFindNode(this, find_request, this.local_contact, originating_contact);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3061 */             reply.setRandomID(originating_contact.getRandomID());
/*      */             
/* 3063 */             reply.setNodeStatus(getNodeStatus());
/*      */             
/* 3065 */             reply.setEstimatedDHTSize(this.request_handler.getTransportEstimatedDHTSize());
/*      */             
/* 3067 */             reply.setContacts(res);
/*      */             
/* 3069 */             requestReceiveReplyProcessor(originating_contact, reply);
/*      */             
/* 3071 */             packet_handler_stub.send(reply, request.getAddress());
/*      */           }
/*      */         }
/* 3074 */         else if ((request instanceof DHTUDPPacketRequestFindValue))
/*      */         {
/* 3076 */           if (!this.bootstrap_node)
/*      */           {
/* 3078 */             DHTUDPPacketRequestFindValue find_request = (DHTUDPPacketRequestFindValue)request;
/*      */             
/* 3080 */             DHTTransportFindValueReply res = this.request_handler.findValueRequest(originating_contact, find_request.getID(), find_request.getMaximumValues(), (short)find_request.getFlags());
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3087 */             if (res.blocked())
/*      */             {
/* 3089 */               if (originating_contact.getProtocolVersion() >= 14)
/*      */               {
/* 3091 */                 DHTUDPPacketReplyError reply = new DHTUDPPacketReplyError(this, request, this.local_contact, originating_contact);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3098 */                 reply.setErrorType(2);
/*      */                 
/* 3100 */                 reply.setKeyBlockDetails(res.getBlockedKey(), res.getBlockedSignature());
/*      */                 
/* 3102 */                 requestReceiveReplyProcessor(originating_contact, reply);
/*      */                 
/* 3104 */                 packet_handler_stub.send(reply, request.getAddress());
/*      */               }
/*      */               else
/*      */               {
/* 3108 */                 DHTUDPPacketReplyFindValue reply = new DHTUDPPacketReplyFindValue(this, find_request, this.local_contact, originating_contact);
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3115 */                 reply.setValues(new DHTTransportValue[0], (byte)1, false);
/*      */                 
/* 3117 */                 requestReceiveReplyProcessor(originating_contact, reply);
/*      */                 
/* 3119 */                 packet_handler_stub.send(reply, request.getAddress());
/*      */               }
/*      */             }
/*      */             else {
/* 3123 */               DHTUDPPacketReplyFindValue reply = new DHTUDPPacketReplyFindValue(this, find_request, this.local_contact, originating_contact);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3130 */               if (res.hit())
/*      */               {
/* 3132 */                 DHTTransportValue[] res_values = res.getValues();
/*      */                 
/* 3134 */                 int max_size = 1370;
/*      */                 
/* 3136 */                 List values = new ArrayList();
/* 3137 */                 int values_size = 0;
/*      */                 
/* 3139 */                 int pos = 0;
/*      */                 
/* 3141 */                 while (pos < res_values.length)
/*      */                 {
/* 3143 */                   DHTTransportValue v = res_values[pos];
/*      */                   
/* 3145 */                   int v_len = v.getValue().length + 26;
/*      */                   
/* 3147 */                   if ((values_size > 0) && (values_size + v_len > max_size))
/*      */                   {
/*      */ 
/*      */ 
/*      */ 
/* 3152 */                     DHTTransportValue[] x = new DHTTransportValue[values.size()];
/*      */                     
/* 3154 */                     values.toArray(x);
/*      */                     
/* 3156 */                     reply.setValues(x, res.getDiversificationType(), true);
/*      */                     
/* 3158 */                     packet_handler_stub.send(reply, request.getAddress());
/*      */                     
/* 3160 */                     values_size = 0;
/*      */                     
/* 3162 */                     values = new ArrayList();
/*      */                   }
/*      */                   else
/*      */                   {
/* 3166 */                     values.add(v);
/*      */                     
/* 3168 */                     values_size += v_len;
/*      */                     
/* 3170 */                     pos++;
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/* 3176 */                 DHTTransportValue[] x = new DHTTransportValue[values.size()];
/*      */                 
/* 3178 */                 values.toArray(x);
/*      */                 
/* 3180 */                 reply.setValues(x, res.getDiversificationType(), false);
/*      */                 
/* 3182 */                 requestReceiveReplyProcessor(originating_contact, reply);
/*      */                 
/* 3184 */                 packet_handler_stub.send(reply, request.getAddress());
/*      */               }
/*      */               else
/*      */               {
/* 3188 */                 reply.setContacts(res.getContacts());
/*      */                 
/* 3190 */                 requestReceiveReplyProcessor(originating_contact, reply);
/*      */                 
/* 3192 */                 packet_handler_stub.send(reply, request.getAddress());
/*      */               }
/*      */             }
/*      */           }
/* 3196 */         } else if ((request instanceof DHTUDPPacketData))
/*      */         {
/* 3198 */           if (!this.bootstrap_node)
/*      */           {
/* 3200 */             dataRequest(originating_contact, (DHTUDPPacketData)request);
/*      */           }
/*      */         }
/*      */         else {
/* 3204 */           Debug.out("Unexpected packet:" + request.toString());
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (DHTUDPPacketHandlerException e) {}catch (Throwable e)
/*      */     {
/* 3213 */       Debug.printStackTrace(e);
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
/*      */   public DHTTransportAlternativeNetwork getAlternativeNetwork(int network_type)
/*      */   {
/* 3239 */     return (DHTTransportAlternativeNetwork)this.alt_net_states.get(Integer.valueOf(network_type));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void registerAlternativeNetwork(DHTTransportAlternativeNetwork network)
/*      */   {
/* 3246 */     synchronized (this.alt_net_providers_lock)
/*      */     {
/* 3248 */       Map<Integer, DHTTransportAlternativeNetwork> new_providers = new HashMap(this.alt_net_providers);
/*      */       
/* 3250 */       new_providers.put(Integer.valueOf(network.getNetworkType()), network);
/*      */       
/* 3252 */       this.alt_net_providers = new_providers;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void unregisterAlternativeNetwork(DHTTransportAlternativeNetwork network)
/*      */   {
/* 3260 */     synchronized (this.alt_net_providers_lock)
/*      */     {
/* 3262 */       Map<Integer, DHTTransportAlternativeNetwork> new_providers = new HashMap(this.alt_net_providers);
/*      */       
/* 3264 */       Iterator<Map.Entry<Integer, DHTTransportAlternativeNetwork>> it = new_providers.entrySet().iterator();
/*      */       
/* 3266 */       while (it.hasNext())
/*      */       {
/* 3268 */         if (((Map.Entry)it.next()).getValue() == network)
/*      */         {
/* 3270 */           it.remove();
/*      */         }
/*      */       }
/*      */       
/* 3274 */       this.alt_net_providers = new_providers;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void checkAltContacts()
/*      */   {
/* 3281 */     int total_required = 0;
/*      */     
/* 3283 */     for (DHTTransportAlternativeNetworkImpl net : this.alt_net_states.values())
/*      */     {
/* 3285 */       total_required += net.getRequiredContactCount();
/*      */     }
/*      */     
/* 3288 */     if (total_required > 0)
/*      */     {
/* 3290 */       List<DHTTransportContact> targets = new ArrayList(128);
/*      */       try
/*      */       {
/* 3293 */         this.this_mon.enter();
/*      */         
/* 3295 */         for (DHTTransportContact contact : this.routable_contact_history.values())
/*      */         {
/* 3297 */           if (contact.getProtocolVersion() >= 52)
/*      */           {
/* 3299 */             targets.add(contact);
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 3305 */         this.this_mon.exit();
/*      */       }
/*      */       
/* 3308 */       if (targets.size() > 0)
/*      */       {
/* 3310 */         ((DHTTransportContact)targets.get(RandomUtils.nextInt(targets.size()))).sendPing(new DHTTransportReplyHandlerAdapter()
/*      */         {
/*      */           public void pingReply(DHTTransportContact _contact) {}
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           public void failed(DHTTransportContact _contact, Throwable _error) {}
/*      */         });
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
/*      */   private void sendAltContacts(DHTUDPPacketRequestPing request, DHTUDPPacketReplyPing reply)
/*      */   {
/* 3337 */     if (request.getProtocolVersion() >= 52)
/*      */     {
/* 3339 */       int[] alt_nets = request.getAltNetworks();
/* 3340 */       int[] counts = request.getAltNetworkCounts();
/*      */       
/* 3342 */       if (alt_nets.length > 0)
/*      */       {
/* 3344 */         List<DHTTransportAlternativeContact> alt_contacts = new ArrayList();
/*      */         
/* 3346 */         Map<Integer, DHTTransportAlternativeNetwork> providers = this.alt_net_providers;
/*      */         
/* 3348 */         for (int i = 0; i < alt_nets.length; i++)
/*      */         {
/* 3350 */           int count = counts[i];
/*      */           
/* 3352 */           if (count != 0)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 3357 */             int net = alt_nets[i];
/*      */             
/* 3359 */             DHTTransportAlternativeNetworkImpl local = (DHTTransportAlternativeNetworkImpl)this.alt_net_states.get(Integer.valueOf(net));
/*      */             
/* 3361 */             if (local != null)
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/* 3366 */               int wanted = local.getRequiredContactCount();
/*      */               
/* 3368 */               if (wanted > 0)
/*      */               {
/* 3370 */                 DHTTransportAlternativeNetwork provider = (DHTTransportAlternativeNetwork)providers.get(Integer.valueOf(net));
/*      */                 
/* 3372 */                 if (provider != null)
/*      */                 {
/* 3374 */                   local.addContactsForSend(provider.getContacts(wanted));
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/* 3380 */               if (net == 3)
/*      */               {
/* 3382 */                 count = Math.min(2, count);
/*      */               }
/*      */               
/* 3385 */               alt_contacts.addAll(local.getContacts(count, true));
/*      */             }
/*      */           } }
/* 3388 */         if (alt_contacts.size() > 0)
/*      */         {
/* 3390 */           reply.setAltContacts(alt_contacts);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void requestAltContacts(DHTUDPPacketRequestPing request)
/*      */   {
/* 3400 */     if (request.getProtocolVersion() >= 52)
/*      */     {
/*      */ 
/*      */ 
/* 3404 */       List<int[]> wanted = null;
/*      */       
/* 3406 */       for (DHTTransportAlternativeNetworkImpl net : this.alt_net_states.values())
/*      */       {
/* 3408 */         int req = net.getRequiredContactCount();
/*      */         
/* 3410 */         if (req > 0)
/*      */         {
/* 3412 */           int net_type = net.getNetworkType();
/*      */           
/* 3414 */           if (net_type == 3)
/*      */           {
/* 3416 */             req = Math.min(2, req);
/*      */           }
/*      */           
/* 3419 */           if (wanted == null)
/*      */           {
/* 3421 */             wanted = new ArrayList(this.alt_net_states.size());
/*      */           }
/*      */           
/* 3424 */           wanted.add(new int[] { net_type, req });
/*      */         }
/*      */       }
/*      */       
/* 3428 */       if (wanted != null)
/*      */       {
/* 3430 */         int[] networks = new int[wanted.size()];
/* 3431 */         int[] counts = new int[networks.length];
/*      */         
/* 3433 */         for (int i = 0; i < networks.length; i++)
/*      */         {
/* 3435 */           int[] entry = (int[])wanted.get(i);
/*      */           
/* 3437 */           networks[i] = entry[0];
/* 3438 */           counts[i] = entry[1];
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 3444 */         request.setAltContactRequest(networks, counts);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void receiveAltContacts(DHTUDPPacketReplyPing reply)
/*      */   {
/* 3453 */     if (reply.getProtocolVersion() >= 52)
/*      */     {
/* 3455 */       for (DHTTransportAlternativeContact contact : reply.getAltContacts())
/*      */       {
/* 3457 */         DHTTransportAlternativeNetworkImpl net = (DHTTransportAlternativeNetworkImpl)this.alt_net_states.get(Integer.valueOf(contact.getNetworkType()));
/*      */         
/* 3459 */         if (net != null)
/*      */         {
/* 3461 */           net.addContactFromReply(contact);
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
/*      */   protected void requestReceiveRequestProcessor(DHTTransportUDPContactImpl contact, DHTUDPPacketRequest request) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void requestReceiveReplyProcessor(DHTTransportUDPContactImpl contact, DHTUDPPacketReply reply)
/*      */   {
/* 3482 */     int action = reply.getAction();
/*      */     
/* 3484 */     if ((action == 1025) || (action == 1029) || (action == 1031))
/*      */     {
/*      */ 
/*      */ 
/* 3488 */       reply.setNetworkPositions(this.local_contact.getNetworkPositions());
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
/*      */   protected void requestSendRequestProcessor(DHTTransportUDPContactImpl contact, DHTUDPPacketRequest request) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void requestSendReplyProcessor(DHTTransportUDPContactImpl remote_contact, DHTTransportReplyHandler handler, DHTUDPPacketReply reply, long elapsed_time)
/*      */     throws DHTUDPPacketHandlerException
/*      */   {
/* 3521 */     DHTNetworkPosition[] remote_nps = reply.getNetworkPositions();
/*      */     
/* 3523 */     if (remote_nps != null)
/*      */     {
/* 3525 */       long proc_time = reply.getProcessingTime();
/*      */       
/* 3527 */       if (proc_time > 0L)
/*      */       {
/*      */ 
/*      */ 
/* 3531 */         long rtt = elapsed_time - proc_time;
/*      */         
/* 3533 */         if (rtt < 0L)
/*      */         {
/* 3535 */           rtt = 0L;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3540 */         remote_contact.setNetworkPositions(remote_nps);
/*      */         
/*      */ 
/*      */ 
/* 3544 */         DHTNetworkPositionManager.update(this.local_contact.getNetworkPositions(), remote_contact.getID(), remote_nps, (float)rtt);
/*      */       }
/*      */     }
/*      */     
/* 3548 */     remote_contact.setGenericFlags(reply.getGenericFlags());
/*      */     
/* 3550 */     if (reply.getAction() == 1032)
/*      */     {
/* 3552 */       DHTUDPPacketReplyError error = (DHTUDPPacketReplyError)reply;
/*      */       
/* 3554 */       switch (error.getErrorType())
/*      */       {
/*      */       case 1: 
/*      */         try
/*      */         {
/* 3559 */           externalAddressChange(remote_contact, error.getOriginatingAddress(), false);
/*      */         }
/*      */         catch (DHTTransportException e)
/*      */         {
/* 3563 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/* 3566 */         throw new DHTUDPPacketHandlerException("address changed notification");
/*      */       
/*      */ 
/*      */       case 2: 
/* 3570 */         handler.keyBlockRequest(remote_contact, error.getKeyBlockRequest(), error.getKeyBlockSignature());
/*      */         
/* 3572 */         contactAlive(remote_contact);
/*      */         
/* 3574 */         throw new DHTUDPPacketHandlerException("key blocked");
/*      */       }
/*      */       
/*      */       
/* 3578 */       throw new DHTUDPPacketHandlerException("unknown error type " + error.getErrorType());
/*      */     }
/*      */     
/*      */ 
/* 3582 */     contactAlive(remote_contact);
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
/*      */   protected long getConnectionID()
/*      */   {
/* 3598 */     return 0x8000000000000000 | this.random.nextLong();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsStorage()
/*      */   {
/* 3604 */     return !this.bootstrap_node;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener(DHTTransportListener l)
/*      */   {
/* 3611 */     this.listeners.add(l);
/*      */     
/* 3613 */     if (this.external_address != null)
/*      */     {
/* 3615 */       l.currentAddress(this.external_address);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(DHTTransportListener l)
/*      */   {
/* 3623 */     this.listeners.remove(l);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTTransportUDPImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */