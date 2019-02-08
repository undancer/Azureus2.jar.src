/*      */ package org.gudy.azureus2.core3.tracker.host.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.logging.LogIDs;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerListener;
/*      */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHost;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostAuthenticationListener;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostException;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostListener;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostListener2;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentFinder;
/*      */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentRequest;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServer;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerFactory;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2.ExternalRequest;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequest;
/*      */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerTorrent;
/*      */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*      */ import org.gudy.azureus2.core3.util.AEMonitor;
/*      */ import org.gudy.azureus2.core3.util.AEThread;
/*      */ import org.gudy.azureus2.core3.util.AsyncController;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.HashWrapper;
/*      */ import org.gudy.azureus2.core3.util.ListenerManager;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ 
/*      */ public class TRHostImpl implements TRHost, org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactoryListener, org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2, org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener, org.gudy.azureus2.core3.tracker.server.TRTrackerServerFactoryListener, org.gudy.azureus2.core3.tracker.server.TRTrackerServerRequestListener, org.gudy.azureus2.core3.tracker.server.TRTrackerServerAuthenticationListener
/*      */ {
/*   51 */   private static final LogIDs LOGID = LogIDs.TRACKER;
/*      */   
/*      */   private static final int URL_DEFAULT_PORT = 80;
/*      */   
/*      */   private static final int URL_DEFAULT_PORT_SSL = 443;
/*      */   public static final int STATS_PERIOD_SECS = 60;
/*      */   private static final int TICK_PERIOD_SECS = 10;
/*      */   private static final int TICKS_PER_STATS_PERIOD = 6;
/*      */   private static TRHostImpl singleton;
/*   60 */   private static final AEMonitor class_mon = new AEMonitor("TRHost:class");
/*      */   
/*      */   private TRHostConfigImpl config;
/*      */   
/*   64 */   private final Hashtable server_map = new Hashtable();
/*      */   
/*   66 */   final List host_torrents = new ArrayList();
/*   67 */   private final Map host_torrent_hash_map = new HashMap();
/*      */   
/*   69 */   private final Map host_torrent_map = new HashMap();
/*   70 */   private final Map tracker_client_map = new HashMap();
/*      */   
/*      */   private static final int LDT_TORRENT_ADDED = 1;
/*      */   
/*      */   private static final int LDT_TORRENT_REMOVED = 2;
/*      */   private static final int LDT_TORRENT_CHANGED = 3;
/*   76 */   private final ListenerManager<TRHostListener> listeners = ListenerManager.createAsyncManager("TRHost:ListenDispatcher", new org.gudy.azureus2.core3.util.ListenerManagerDispatcher()
/*      */   {
/*      */ 
/*      */ 
/*      */ 
/*      */     public void dispatch(TRHostListener _listener, int type, Object value)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*   86 */       TRHostListener target = _listener;
/*      */       
/*   88 */       if (type == 1)
/*      */       {
/*   90 */         target.torrentAdded((TRHostTorrent)value);
/*      */       }
/*   92 */       else if (type == 2)
/*      */       {
/*   94 */         target.torrentRemoved((TRHostTorrent)value);
/*      */       }
/*   96 */       else if (type == 3)
/*      */       {
/*   98 */         target.torrentChanged((TRHostTorrent)value);
/*      */       }
/*      */     }
/*   76 */   });
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  103 */   private final CopyOnWriteList<TRHostListener2> listeners2 = new CopyOnWriteList();
/*      */   private static boolean host_add_announce_urls;
/*      */   
/*      */   static
/*      */   {
/*  108 */     COConfigurationManager.addAndFireParameterListener("Tracker Host Add Our Announce URLs", new org.gudy.azureus2.core3.config.ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  116 */         TRHostImpl.access$002(COConfigurationManager.getBooleanParameter(name));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*  121 */   private final List<TRHostAuthenticationListener> auth_listeners = new ArrayList();
/*      */   
/*      */   private boolean server_factory_listener_added;
/*      */   
/*  125 */   protected final AEMonitor this_mon = new AEMonitor("TRHost");
/*      */   
/*      */   private volatile boolean closed;
/*      */   
/*      */   public static TRHost create()
/*      */   {
/*      */     try
/*      */     {
/*  133 */       class_mon.enter();
/*      */       
/*  135 */       if (singleton == null)
/*      */       {
/*  137 */         singleton = new TRHostImpl();
/*      */       }
/*      */       
/*  140 */       return singleton;
/*      */     }
/*      */     finally
/*      */     {
/*  144 */       class_mon.exit();
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
/*      */   protected TRHostImpl()
/*      */   {
/*      */     try
/*      */     {
/*  159 */       this.this_mon.enter();
/*      */       
/*  161 */       this.config = new TRHostConfigImpl(this);
/*      */       
/*  163 */       org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerFactory.addListener(this);
/*      */       
/*  165 */       Thread t = new AEThread("TRHost::stats.loop")
/*      */       {
/*  167 */         private int tick_count = 0;
/*      */         
/*  169 */         private final java.util.Set failed_ports = new java.util.HashSet();
/*      */         
/*      */         /* Error */
/*      */         public void runSupport()
/*      */         {
/*      */           // Byte code:
/*      */           //   0: invokestatic 195	org/gudy/azureus2/core3/tracker/util/TRTrackerUtils:getAnnounceURLs	()[[Ljava/net/URL;
/*      */           //   3: astore_1
/*      */           //   4: iconst_0
/*      */           //   5: istore_2
/*      */           //   6: iload_2
/*      */           //   7: aload_1
/*      */           //   8: arraylength
/*      */           //   9: if_icmpge +227 -> 236
/*      */           //   12: aload_1
/*      */           //   13: iload_2
/*      */           //   14: aaload
/*      */           //   15: astore_3
/*      */           //   16: iconst_0
/*      */           //   17: istore 4
/*      */           //   19: iload 4
/*      */           //   21: aload_3
/*      */           //   22: arraylength
/*      */           //   23: if_icmpge +207 -> 230
/*      */           //   26: aload_3
/*      */           //   27: iload 4
/*      */           //   29: aaload
/*      */           //   30: astore 5
/*      */           //   32: aload 5
/*      */           //   34: invokevirtual 183	java/net/URL:getPort	()I
/*      */           //   37: istore 6
/*      */           //   39: iload 6
/*      */           //   41: iconst_m1
/*      */           //   42: if_icmpne +10 -> 52
/*      */           //   45: aload 5
/*      */           //   47: invokevirtual 182	java/net/URL:getDefaultPort	()I
/*      */           //   50: istore 6
/*      */           //   52: aload 5
/*      */           //   54: invokevirtual 184	java/net/URL:getProtocol	()Ljava/lang/String;
/*      */           //   57: invokevirtual 177	java/lang/String:toLowerCase	()Ljava/lang/String;
/*      */           //   60: astore 7
/*      */           //   62: aload 7
/*      */           //   64: ldc 4
/*      */           //   66: invokevirtual 176	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */           //   69: ifeq +18 -> 87
/*      */           //   72: aload_0
/*      */           //   73: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   76: iconst_1
/*      */           //   77: iload 6
/*      */           //   79: iconst_0
/*      */           //   80: invokevirtual 191	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:startServer	(IIZ)Lorg/gudy/azureus2/core3/tracker/server/TRTrackerServer;
/*      */           //   83: pop
/*      */           //   84: goto +81 -> 165
/*      */           //   87: aload 7
/*      */           //   89: ldc 6
/*      */           //   91: invokevirtual 176	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */           //   94: ifeq +18 -> 112
/*      */           //   97: aload_0
/*      */           //   98: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   101: iconst_2
/*      */           //   102: iload 6
/*      */           //   104: iconst_0
/*      */           //   105: invokevirtual 191	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:startServer	(IIZ)Lorg/gudy/azureus2/core3/tracker/server/TRTrackerServer;
/*      */           //   108: pop
/*      */           //   109: goto +56 -> 165
/*      */           //   112: aload 7
/*      */           //   114: ldc 5
/*      */           //   116: invokevirtual 176	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */           //   119: ifeq +18 -> 137
/*      */           //   122: aload_0
/*      */           //   123: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   126: iconst_1
/*      */           //   127: iload 6
/*      */           //   129: iconst_1
/*      */           //   130: invokevirtual 191	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:startServer	(IIZ)Lorg/gudy/azureus2/core3/tracker/server/TRTrackerServer;
/*      */           //   133: pop
/*      */           //   134: goto +31 -> 165
/*      */           //   137: new 93	java/lang/StringBuilder
/*      */           //   140: dup
/*      */           //   141: invokespecial 178	java/lang/StringBuilder:<init>	()V
/*      */           //   144: ldc 3
/*      */           //   146: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */           //   149: aload 7
/*      */           //   151: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */           //   154: ldc 1
/*      */           //   156: invokevirtual 180	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */           //   159: invokevirtual 179	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */           //   162: invokestatic 199	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*      */           //   165: goto +59 -> 224
/*      */           //   168: astore 8
/*      */           //   170: new 90	java/lang/Integer
/*      */           //   173: dup
/*      */           //   174: iload 6
/*      */           //   176: invokespecial 175	java/lang/Integer:<init>	(I)V
/*      */           //   179: astore 9
/*      */           //   181: aload_0
/*      */           //   182: getfield 173	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:failed_ports	Ljava/util/Set;
/*      */           //   185: aload 9
/*      */           //   187: invokeinterface 204 2 0
/*      */           //   192: ifne +32 -> 224
/*      */           //   195: aload_0
/*      */           //   196: getfield 173	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:failed_ports	Ljava/util/Set;
/*      */           //   199: aload 9
/*      */           //   201: invokeinterface 203 2 0
/*      */           //   206: pop
/*      */           //   207: new 100	org/gudy/azureus2/core3/logging/LogEvent
/*      */           //   210: dup
/*      */           //   211: invokestatic 189	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:access$100	()Lorg/gudy/azureus2/core3/logging/LogIDs;
/*      */           //   214: ldc 2
/*      */           //   216: aload 8
/*      */           //   218: invokespecial 186	org/gudy/azureus2/core3/logging/LogEvent:<init>	(Lorg/gudy/azureus2/core3/logging/LogIDs;Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */           //   221: invokestatic 187	org/gudy/azureus2/core3/logging/Logger:log	(Lorg/gudy/azureus2/core3/logging/LogEvent;)V
/*      */           //   224: iinc 4 1
/*      */           //   227: goto -208 -> 19
/*      */           //   230: iinc 2 1
/*      */           //   233: goto -227 -> 6
/*      */           //   236: ldc2_w 86
/*      */           //   239: invokestatic 181	java/lang/Thread:sleep	(J)V
/*      */           //   242: aload_0
/*      */           //   243: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   246: invokestatic 190	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:access$200	(Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;)Z
/*      */           //   249: ifeq +16 -> 265
/*      */           //   252: aload_0
/*      */           //   253: dup
/*      */           //   254: getfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   257: iconst_1
/*      */           //   258: iadd
/*      */           //   259: putfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   262: goto +190 -> 452
/*      */           //   265: aload_0
/*      */           //   266: getfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   269: bipush 6
/*      */           //   271: irem
/*      */           //   272: ifne +120 -> 392
/*      */           //   275: aload_0
/*      */           //   276: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   279: getfield 171	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */           //   282: invokevirtual 196	org/gudy/azureus2/core3/util/AEMonitor:enter	()V
/*      */           //   285: iconst_0
/*      */           //   286: istore_2
/*      */           //   287: iload_2
/*      */           //   288: aload_0
/*      */           //   289: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   292: getfield 170	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:host_torrents	Ljava/util/List;
/*      */           //   295: invokeinterface 201 1 0
/*      */           //   300: if_icmpge +50 -> 350
/*      */           //   303: aload_0
/*      */           //   304: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   307: getfield 170	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:host_torrents	Ljava/util/List;
/*      */           //   310: iload_2
/*      */           //   311: invokeinterface 202 2 0
/*      */           //   316: checkcast 102	org/gudy/azureus2/core3/tracker/host/TRHostTorrent
/*      */           //   319: astore_3
/*      */           //   320: aload_3
/*      */           //   321: instanceof 106
/*      */           //   324: ifeq +13 -> 337
/*      */           //   327: aload_3
/*      */           //   328: checkcast 106	org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentHostImpl
/*      */           //   331: invokevirtual 193	org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentHostImpl:updateStats	()V
/*      */           //   334: goto +10 -> 344
/*      */           //   337: aload_3
/*      */           //   338: checkcast 107	org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentPublishImpl
/*      */           //   341: invokevirtual 194	org/gudy/azureus2/core3/tracker/host/impl/TRHostTorrentPublishImpl:updateStats	()V
/*      */           //   344: iinc 2 1
/*      */           //   347: goto -60 -> 287
/*      */           //   350: aload_0
/*      */           //   351: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   354: getfield 171	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */           //   357: invokevirtual 197	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */           //   360: goto +18 -> 378
/*      */           //   363: astore 10
/*      */           //   365: aload_0
/*      */           //   366: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   369: getfield 171	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:this_mon	Lorg/gudy/azureus2/core3/util/AEMonitor;
/*      */           //   372: invokevirtual 197	org/gudy/azureus2/core3/util/AEMonitor:exit	()V
/*      */           //   375: aload 10
/*      */           //   377: athrow
/*      */           //   378: aload_0
/*      */           //   379: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   382: invokestatic 192	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:access$300	(Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;)Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostConfigImpl;
/*      */           //   385: iconst_1
/*      */           //   386: invokevirtual 188	org/gudy/azureus2/core3/tracker/host/impl/TRHostConfigImpl:saveConfig	(Z)V
/*      */           //   389: goto +14 -> 403
/*      */           //   392: aload_0
/*      */           //   393: getfield 174	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:this$0	Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;
/*      */           //   396: invokestatic 192	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl:access$300	(Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostImpl;)Lorg/gudy/azureus2/core3/tracker/host/impl/TRHostConfigImpl;
/*      */           //   399: iconst_0
/*      */           //   400: invokevirtual 188	org/gudy/azureus2/core3/tracker/host/impl/TRHostConfigImpl:saveConfig	(Z)V
/*      */           //   403: aload_0
/*      */           //   404: dup
/*      */           //   405: getfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   408: iconst_1
/*      */           //   409: iadd
/*      */           //   410: putfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   413: goto +36 -> 449
/*      */           //   416: astore_1
/*      */           //   417: aload_1
/*      */           //   418: invokestatic 200	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*      */           //   421: aload_0
/*      */           //   422: dup
/*      */           //   423: getfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   426: iconst_1
/*      */           //   427: iadd
/*      */           //   428: putfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   431: goto +21 -> 452
/*      */           //   434: astore 11
/*      */           //   436: aload_0
/*      */           //   437: dup
/*      */           //   438: getfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   441: iconst_1
/*      */           //   442: iadd
/*      */           //   443: putfield 172	org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl$3:tick_count	I
/*      */           //   446: aload 11
/*      */           //   448: athrow
/*      */           //   449: goto -449 -> 0
/*      */           //   452: return
/*      */           // Line number table:
/*      */           //   Java source line #178	-> byte code offset #0
/*      */           //   Java source line #180	-> byte code offset #4
/*      */           //   Java source line #182	-> byte code offset #12
/*      */           //   Java source line #184	-> byte code offset #16
/*      */           //   Java source line #186	-> byte code offset #26
/*      */           //   Java source line #188	-> byte code offset #32
/*      */           //   Java source line #190	-> byte code offset #39
/*      */           //   Java source line #192	-> byte code offset #45
/*      */           //   Java source line #195	-> byte code offset #52
/*      */           //   Java source line #198	-> byte code offset #62
/*      */           //   Java source line #200	-> byte code offset #72
/*      */           //   Java source line #202	-> byte code offset #87
/*      */           //   Java source line #204	-> byte code offset #97
/*      */           //   Java source line #206	-> byte code offset #112
/*      */           //   Java source line #208	-> byte code offset #122
/*      */           //   Java source line #212	-> byte code offset #137
/*      */           //   Java source line #227	-> byte code offset #165
/*      */           //   Java source line #215	-> byte code offset #168
/*      */           //   Java source line #217	-> byte code offset #170
/*      */           //   Java source line #219	-> byte code offset #181
/*      */           //   Java source line #221	-> byte code offset #195
/*      */           //   Java source line #223	-> byte code offset #207
/*      */           //   Java source line #184	-> byte code offset #224
/*      */           //   Java source line #180	-> byte code offset #230
/*      */           //   Java source line #231	-> byte code offset #236
/*      */           //   Java source line #233	-> byte code offset #242
/*      */           //   Java source line #276	-> byte code offset #252
/*      */           //   Java source line #238	-> byte code offset #265
/*      */           //   Java source line #241	-> byte code offset #275
/*      */           //   Java source line #243	-> byte code offset #285
/*      */           //   Java source line #245	-> byte code offset #303
/*      */           //   Java source line #247	-> byte code offset #320
/*      */           //   Java source line #249	-> byte code offset #327
/*      */           //   Java source line #253	-> byte code offset #337
/*      */           //   Java source line #243	-> byte code offset #344
/*      */           //   Java source line #259	-> byte code offset #350
/*      */           //   Java source line #260	-> byte code offset #360
/*      */           //   Java source line #259	-> byte code offset #363
/*      */           //   Java source line #262	-> byte code offset #378
/*      */           //   Java source line #266	-> byte code offset #392
/*      */           //   Java source line #276	-> byte code offset #403
/*      */           //   Java source line #277	-> byte code offset #413
/*      */           //   Java source line #269	-> byte code offset #416
/*      */           //   Java source line #271	-> byte code offset #417
/*      */           //   Java source line #276	-> byte code offset #421
/*      */           //   Java source line #279	-> byte code offset #452
/*      */           // Local variable table:
/*      */           //   start	length	slot	name	signature
/*      */           //   0	453	0	this	3
/*      */           //   3	10	1	url_sets	URL[][]
/*      */           //   416	2	1	e	InterruptedException
/*      */           //   5	226	2	i	int
/*      */           //   286	59	2	i	int
/*      */           //   15	12	3	urls	URL[]
/*      */           //   319	19	3	ht	TRHostTorrent
/*      */           //   17	208	4	j	int
/*      */           //   30	23	5	url	URL
/*      */           //   37	138	6	port	int
/*      */           //   60	90	7	protocol	String
/*      */           //   168	49	8	e	Throwable
/*      */           //   179	21	9	port_i	Integer
/*      */           //   363	13	10	localObject1	Object
/*      */           //   434	13	11	localObject2	Object
/*      */           // Exception table:
/*      */           //   from	to	target	type
/*      */           //   62	165	168	java/lang/Throwable
/*      */           //   275	350	363	finally
/*      */           //   363	365	363	finally
/*      */           //   0	252	416	java/lang/InterruptedException
/*      */           //   265	403	416	java/lang/InterruptedException
/*      */           //   0	252	434	finally
/*      */           //   265	403	434	finally
/*      */           //   416	421	434	finally
/*      */           //   434	436	434	finally
/*      */         }
/*  281 */       };
/*  282 */       t.setDaemon(true);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  287 */       t.setPriority(9);
/*      */       
/*  289 */       t.start();
/*      */     }
/*      */     finally
/*      */     {
/*  293 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void initialise(TRHostTorrentFinder finder)
/*      */   {
/*  301 */     this.config.loadConfig(finder);
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  307 */     return TRTrackerServer.DEFAULT_NAME;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TRHostTorrent hostTorrent(TOTorrent torrent, boolean persistent, boolean passive)
/*      */     throws TRHostException
/*      */   {
/*  318 */     return addTorrent(torrent, 2, persistent, passive, SystemTime.getCurrentTime());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public TRHostTorrent publishTorrent(TOTorrent torrent)
/*      */     throws TRHostException
/*      */   {
/*  327 */     return addTorrent(torrent, 3, true, false, SystemTime.getCurrentTime());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TRHostTorrent addTorrent(TOTorrent torrent, int state, boolean persistent, boolean passive, long date_added)
/*      */     throws TRHostException
/*      */   {
/*      */     try
/*      */     {
/*  341 */       this.this_mon.enter();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  346 */       if ((persistent) && (state != 3))
/*      */       {
/*  348 */         if (host_add_announce_urls)
/*      */         {
/*  350 */           addTrackerAnnounce(torrent);
/*      */         }
/*      */       }
/*      */       
/*  354 */       TRHostTorrent ht = lookupHostTorrent(torrent);
/*      */       
/*  356 */       if (ht != null)
/*      */       {
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*      */ 
/*  363 */           ht = lookupHostTorrentViaHash(torrent.getHash());
/*      */           
/*  365 */           if ((ht instanceof TRHostTorrentHostImpl))
/*      */           {
/*  367 */             TRHostTorrentHostImpl hti = (TRHostTorrentHostImpl)ht;
/*      */             
/*  369 */             if (hti.getTorrent() != torrent)
/*      */             {
/*  371 */               hti.setTorrentInternal(torrent);
/*      */               
/*  373 */               if ((persistent) && (!hti.isPersistent()))
/*      */               {
/*  375 */                 hti.setPersistent(true);
/*      */               }
/*      */               
/*  378 */               if ((passive) && (!hti.isPassive()))
/*      */               {
/*  380 */                 hti.setPassive(true);
/*      */               }
/*      */               
/*  383 */               if (state != 3)
/*      */               {
/*  385 */                 startHosting(hti);
/*      */                 
/*  387 */                 if (state == 2)
/*      */                 {
/*  389 */                   hti.start();
/*      */                 }
/*      */               }
/*      */               
/*  393 */               this.listeners.dispatch(3, ht);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (TOTorrentException e) {
/*  398 */           Debug.printStackTrace(e);
/*      */         }
/*      */         
/*  401 */         return ht;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  406 */       int protocol = 1;
/*      */       boolean ssl;
/*  408 */       boolean ssl; int port; if (state == 3)
/*      */       {
/*  410 */         int port = COConfigurationManager.getIntParameter("Tracker Port", 6969);
/*      */         
/*  412 */         ssl = false;
/*      */       }
/*      */       else {
/*  415 */         URL announce_url = torrent.getAnnounceURL();
/*      */         
/*  417 */         String protocol_str = announce_url.getProtocol();
/*      */         
/*  419 */         ssl = protocol_str.equalsIgnoreCase("https");
/*      */         
/*  421 */         if (protocol_str.equalsIgnoreCase("udp"))
/*      */         {
/*  423 */           protocol = 2;
/*      */         }
/*  425 */         else if (TorrentUtils.isDecentralised(torrent))
/*      */         {
/*  427 */           protocol = 3;
/*      */         }
/*      */         
/*  430 */         boolean force_external = COConfigurationManager.getBooleanParameter("Tracker Port Force External");
/*      */         
/*  432 */         port = announce_url.getPort();
/*      */         
/*  434 */         if (force_external)
/*      */         {
/*  436 */           String tracker_ip = COConfigurationManager.getStringParameter("Tracker IP", "");
/*      */           
/*  438 */           if ((tracker_ip.length() > 0) && (!announce_url.getHost().equalsIgnoreCase(tracker_ip)))
/*      */           {
/*      */ 
/*  441 */             if (ssl)
/*      */             {
/*  443 */               port = COConfigurationManager.getIntParameter("Tracker Port SSL", 7000);
/*      */             }
/*      */             else
/*      */             {
/*  447 */               port = COConfigurationManager.getIntParameter("Tracker Port", 6969);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  453 */         if (port == -1)
/*      */         {
/*  455 */           port = ssl ? 443 : 80;
/*      */         }
/*      */       }
/*      */       
/*  459 */       TRTrackerServer server = startServer(protocol, port, ssl);
/*      */       
/*      */       TRHostTorrent host_torrent;
/*      */       TRHostTorrent host_torrent;
/*  463 */       if (state == 3)
/*      */       {
/*  465 */         TRHostTorrentPublishImpl new_torrent = new TRHostTorrentPublishImpl(this, torrent, date_added);
/*      */         
/*  467 */         new_torrent.setPersistent(persistent);
/*      */         
/*  469 */         host_torrent = new_torrent;
/*      */       }
/*      */       else {
/*  472 */         TRHostTorrentHostImpl new_torrent = new TRHostTorrentHostImpl(this, server, torrent, port, date_added);
/*      */         
/*  474 */         new_torrent.setPersistent(persistent);
/*      */         
/*  476 */         new_torrent.setPassive(passive);
/*      */         
/*  478 */         host_torrent = new_torrent;
/*      */       }
/*      */       
/*  481 */       this.host_torrents.add(host_torrent);
/*      */       try
/*      */       {
/*  484 */         this.host_torrent_hash_map.put(new HashWrapper(torrent.getHash()), host_torrent);
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/*  488 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/*  491 */       this.host_torrent_map.put(torrent, host_torrent);
/*      */       
/*  493 */       if (state != 3)
/*      */       {
/*  495 */         startHosting((TRHostTorrentHostImpl)host_torrent);
/*      */         
/*  497 */         if (state == 2)
/*      */         {
/*  499 */           host_torrent.start();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  504 */         if (!persistent)
/*      */         {
/*  506 */           this.config.recoverStats((TRHostTorrentHostImpl)host_torrent);
/*      */         }
/*      */       }
/*      */       
/*  510 */       this.listeners.dispatch(1, host_torrent);
/*      */       
/*  512 */       this.config.saveRequired();
/*      */       
/*  514 */       return host_torrent;
/*      */     }
/*      */     finally
/*      */     {
/*  518 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void torrentUpdated(TRHostTorrentHostImpl hti)
/*      */   {
/*  526 */     int state = hti.getStatus();
/*      */     
/*  528 */     if (state != 3)
/*      */     {
/*  530 */       startHosting(hti);
/*      */       
/*  532 */       if (state == 2)
/*      */       {
/*  534 */         hti.start();
/*      */       }
/*      */     }
/*      */     
/*  538 */     this.listeners.dispatch(3, hti);
/*      */   }
/*      */   
/*      */ 
/*      */   public java.net.InetAddress getBindIP()
/*      */   {
/*  544 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected TRTrackerServer startServer(int protocol, int port, boolean ssl)
/*      */     throws TRHostException
/*      */   {
/*      */     try
/*      */     {
/*  556 */       this.this_mon.enter();
/*      */       
/*  558 */       String key = "" + protocol + ":" + port;
/*      */       
/*  560 */       TRTrackerServer server = (TRTrackerServer)this.server_map.get(key);
/*      */       
/*  562 */       if (server == null)
/*      */       {
/*      */         try
/*      */         {
/*  566 */           if (ssl)
/*      */           {
/*  568 */             server = TRTrackerServerFactory.createSSL("tracker", protocol, port, true, true);
/*      */           }
/*      */           else
/*      */           {
/*  572 */             server = TRTrackerServerFactory.create("tracker", protocol, port, true, true);
/*      */           }
/*      */           
/*  575 */           this.server_map.put(key, server);
/*      */           
/*  577 */           if (this.auth_listeners.size() > 0)
/*      */           {
/*  579 */             server.addAuthenticationListener(this);
/*      */           }
/*      */           
/*  582 */           server.addListener(this);
/*  583 */           server.addListener2(this);
/*      */         }
/*      */         catch (TRTrackerServerException e)
/*      */         {
/*  587 */           throw new TRHostException("startServer failed", e);
/*      */         }
/*      */       }
/*      */       
/*  591 */       return server;
/*      */     }
/*      */     finally
/*      */     {
/*  595 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected TRHostTorrent lookupHostTorrent(TOTorrent torrent)
/*      */   {
/*  603 */     if (torrent == null) {
/*  604 */       return null;
/*      */     }
/*      */     try {
/*  607 */       return (TRHostTorrent)this.host_torrent_hash_map.get(torrent.getHashWrapper());
/*      */     }
/*      */     catch (TOTorrentException e)
/*      */     {
/*  611 */       Debug.printStackTrace(e);
/*      */     }
/*      */     
/*  614 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void startHosting(TRHostTorrentHostImpl host_torrent)
/*      */   {
/*  621 */     TOTorrent torrent = host_torrent.getTorrent();
/*      */     
/*  623 */     TRTrackerAnnouncer tc = (TRTrackerAnnouncer)this.tracker_client_map.get(torrent);
/*      */     
/*  625 */     if (tc != null)
/*      */     {
/*  627 */       startHosting(host_torrent, tc);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void startHosting(TRTrackerAnnouncer tracker_client)
/*      */   {
/*  635 */     TRHostTorrent host_torrent = (TRHostTorrent)this.host_torrent_map.get(tracker_client.getTorrent());
/*      */     
/*  637 */     if ((host_torrent instanceof TRHostTorrentHostImpl))
/*      */     {
/*  639 */       startHosting((TRHostTorrentHostImpl)host_torrent, tracker_client);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void startHosting(TRHostTorrentHostImpl host_torrent, final TRTrackerAnnouncer tracker_client)
/*      */   {
/*  648 */     final TOTorrent torrent = host_torrent.getTorrent();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  653 */     URL announce = torrent.getAnnounceURL();
/*      */     
/*  655 */     if (host_add_announce_urls)
/*      */     {
/*  657 */       tracker_client.setIPOverride(announce.getHost());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*  663 */     else if (TRTrackerUtils.isHosting(announce))
/*      */     {
/*  665 */       tracker_client.setIPOverride(announce.getHost());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  673 */     TRTrackerAnnouncerListener listener = new TRTrackerAnnouncerListener()
/*      */     {
/*      */ 
/*      */       public void receivedTrackerResponse(TRTrackerAnnouncerResponse response)
/*      */       {
/*      */ 
/*      */         try
/*      */         {
/*  681 */           org.gudy.azureus2.core3.tracker.client.TRTrackerScraperFactory.getSingleton().scrape(torrent, true);
/*      */         }
/*      */         finally
/*      */         {
/*  685 */           tracker_client.removeListener(this);
/*      */         }
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
/*      */       public void urlRefresh() {}
/*  703 */     };
/*  704 */     tracker_client.addListener(listener);
/*      */     
/*  706 */     tracker_client.refreshListeners();
/*      */   }
/*      */   
/*      */ 
/*      */   protected void remove(TRHostTorrent host_torrent)
/*      */   {
/*      */     try
/*      */     {
/*  714 */       this.this_mon.enter();
/*      */       
/*  716 */       if (!this.host_torrents.contains(host_torrent)) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  721 */       this.host_torrents.remove(host_torrent);
/*      */       
/*  723 */       TOTorrent torrent = host_torrent.getTorrent();
/*      */       try
/*      */       {
/*  726 */         this.host_torrent_hash_map.remove(new HashWrapper(torrent.getHash()));
/*      */       }
/*      */       catch (TOTorrentException e)
/*      */       {
/*  730 */         Debug.printStackTrace(e);
/*      */       }
/*      */       
/*  733 */       this.host_torrent_map.remove(torrent);
/*      */       
/*  735 */       if ((host_torrent instanceof TRHostTorrentHostImpl))
/*      */       {
/*  737 */         stopHosting((TRHostTorrentHostImpl)host_torrent);
/*      */       }
/*      */       
/*  740 */       this.listeners.dispatch(2, host_torrent);
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*      */ 
/*  749 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void stopHosting(TRHostTorrentHostImpl host_torrent)
/*      */   {
/*  757 */     TOTorrent torrent = host_torrent.getTorrent();
/*      */     
/*  759 */     TRTrackerAnnouncer tc = (TRTrackerAnnouncer)this.tracker_client_map.get(torrent);
/*      */     
/*  761 */     if (tc != null)
/*      */     {
/*  763 */       stopHosting(host_torrent, tc);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void stopHosting(TRTrackerAnnouncer tracker_client)
/*      */   {
/*  771 */     TRHostTorrent host_torrent = (TRHostTorrent)this.host_torrent_map.get(tracker_client.getTorrent());
/*      */     
/*  773 */     if ((host_torrent instanceof TRHostTorrentHostImpl))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  782 */       stopHosting((TRHostTorrentHostImpl)host_torrent, tracker_client);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  787 */   final AsyncDispatcher dispatcher = new AsyncDispatcher("TRHost:stopHosting");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void stopHosting(final TRHostTorrentHostImpl host_torrent, final TRTrackerAnnouncer tracker_client)
/*      */   {
/*  801 */     org.gudy.azureus2.core3.util.SimpleTimer.addEvent("StopHosting", SystemTime.getOffsetTime(2500L), new org.gudy.azureus2.core3.util.TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  809 */         TRHostImpl.this.dispatcher.dispatch(new org.gudy.azureus2.core3.util.AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */             try
/*      */             {
/*  816 */               TRHostImpl.this.this_mon.enter();
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  821 */               TRHostTorrent ht = TRHostImpl.this.lookupHostTorrent(TRHostImpl.5.this.val$host_torrent.getTorrent());
/*      */               
/*      */ 
/*      */ 
/*  825 */               if ((ht == null) || ((ht == TRHostImpl.5.this.val$host_torrent) && (ht.getStatus() == 1)))
/*      */               {
/*      */ 
/*      */ 
/*  829 */                 TRHostImpl.5.this.val$tracker_client.clearIPOverride();
/*      */               }
/*      */             }
/*      */             finally {
/*  833 */               TRHostImpl.this.this_mon.exit();
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected TRTrackerAnnouncer getTrackerClient(TRHostTorrent host_torrent)
/*      */   {
/*      */     try
/*      */     {
/*  846 */       this.this_mon.enter();
/*      */       
/*  848 */       return (TRTrackerAnnouncer)this.tracker_client_map.get(host_torrent.getTorrent());
/*      */     }
/*      */     finally
/*      */     {
/*  852 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void hostTorrentStateChange(TRHostTorrent host_torrent)
/*      */   {
/*      */     try
/*      */     {
/*  861 */       this.this_mon.enter();
/*      */       
/*  863 */       TOTorrent torrent = host_torrent.getTorrent();
/*      */       
/*  865 */       TRTrackerAnnouncer tc = (TRTrackerAnnouncer)this.tracker_client_map.get(torrent);
/*      */       
/*  867 */       if (tc != null)
/*      */       {
/*  869 */         tc.refreshListeners();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/*      */ 
/*  878 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */   public TRHostTorrent[] getTorrents()
/*      */   {
/*      */     try
/*      */     {
/*  886 */       this.this_mon.enter();
/*      */       
/*  888 */       TRHostTorrent[] res = new TRHostTorrent[this.host_torrents.size()];
/*      */       
/*  890 */       this.host_torrents.toArray(res);
/*      */       
/*  892 */       return res;
/*      */     }
/*      */     finally
/*      */     {
/*  896 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void clientCreated(TRTrackerAnnouncer client)
/*      */   {
/*      */     try
/*      */     {
/*  905 */       this.this_mon.enter();
/*      */       
/*  907 */       this.tracker_client_map.put(client.getTorrent(), client);
/*      */       
/*  909 */       startHosting(client);
/*      */     }
/*      */     finally
/*      */     {
/*  913 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void clientDestroyed(TRTrackerAnnouncer client)
/*      */   {
/*      */     try
/*      */     {
/*  922 */       this.this_mon.enter();
/*      */       
/*  924 */       this.tracker_client_map.remove(client.getTorrent());
/*      */       
/*  926 */       stopHosting(client);
/*      */     }
/*      */     finally
/*      */     {
/*  930 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected TRHostTorrent lookupHostTorrentViaHash(byte[] hash)
/*      */   {
/*  938 */     return (TRHostTorrent)this.host_torrent_hash_map.get(new HashWrapper(hash));
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
/*      */   public boolean permitted(String originator, byte[] hash, boolean explicit)
/*      */   {
/*      */     try
/*      */     {
/*  953 */       this.this_mon.enter();
/*      */       
/*  955 */       TRHostTorrent ht = lookupHostTorrentViaHash(hash);
/*      */       boolean bool;
/*  957 */       if (ht != null)
/*      */       {
/*  959 */         if (!explicit)
/*      */         {
/*  961 */           if (ht.getStatus() != 2)
/*      */           {
/*  963 */             return false;
/*      */           }
/*      */         }
/*      */         
/*  967 */         return true;
/*      */       }
/*      */       
/*  970 */       addExternalTorrent(hash, 2, SystemTime.getCurrentTime());
/*      */       
/*  972 */       return true;
/*      */     }
/*      */     finally
/*      */     {
/*  976 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void addExternalTorrent(byte[] hash, int state, long date_added)
/*      */   {
/*      */     try
/*      */     {
/*  987 */       this.this_mon.enter();
/*      */       
/*  989 */       if (lookupHostTorrentViaHash(hash) != null) {
/*      */         return;
/*      */       }
/*      */       
/*      */ 
/*  994 */       String tracker_ip = COConfigurationManager.getStringParameter("Tracker IP", "127.0.0.1");
/*      */       
/*      */ 
/*      */ 
/*  998 */       int port = COConfigurationManager.getIntParameter("Tracker Port", 6969);
/*      */       try
/*      */       {
/* 1001 */         TOTorrent external_torrent = new TRHostExternalTorrent(hash, new URL("http://" + org.gudy.azureus2.core3.util.UrlUtils.convertIPV6Host(tracker_ip) + ":" + port + "/announce"));
/*      */         
/* 1003 */         addTorrent(external_torrent, state, true, false, date_added);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1007 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1012 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean denied(byte[] hash, boolean permitted)
/*      */   {
/* 1021 */     return true;
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
/*      */   public boolean handleExternalRequest(InetSocketAddress client_address, String user, String url, URL absolute_url, String header, InputStream is, OutputStream os, AsyncController async)
/*      */     throws IOException
/*      */   {
/* 1037 */     List<TRHostListener> listeners_copy = this.listeners.getListenersCopy();
/*      */     
/* 1039 */     for (int i = 0; i < listeners_copy.size(); i++)
/*      */     {
/* 1041 */       TRHostListener listener = (TRHostListener)listeners_copy.get(i);
/*      */       try
/*      */       {
/* 1044 */         if (listener.handleExternalRequest(client_address, user, url, absolute_url, header, is, os, async))
/*      */         {
/* 1046 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1050 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1054 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean handleExternalRequest(TRTrackerServerListener2.ExternalRequest request)
/*      */     throws IOException
/*      */   {
/* 1063 */     Iterator<TRHostListener2> it = this.listeners2.iterator();
/*      */     
/* 1065 */     while (it.hasNext()) {
/*      */       try
/*      */       {
/* 1068 */         if (((TRHostListener2)it.next()).handleExternalRequest(request))
/*      */         {
/* 1070 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1074 */         Debug.out(e);
/*      */       }
/*      */     }
/*      */     
/* 1078 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public TRHostTorrent getHostTorrent(TOTorrent torrent)
/*      */   {
/* 1085 */     return lookupHostTorrent(torrent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(TRHostListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1096 */       this.this_mon.enter();
/*      */       
/* 1098 */       this.listeners.addListener(l);
/*      */       
/* 1100 */       for (int i = 0; i < this.host_torrents.size(); i++)
/*      */       {
/* 1102 */         this.listeners.dispatch(l, 1, this.host_torrents.get(i));
/*      */       }
/*      */     }
/*      */     finally {
/* 1106 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(TRHostListener l)
/*      */   {
/* 1114 */     this.listeners.removeListener(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addListener2(TRHostListener2 l)
/*      */   {
/* 1121 */     this.listeners2.add(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener2(TRHostListener2 l)
/*      */   {
/* 1128 */     this.listeners2.remove(l);
/*      */   }
/*      */   
/*      */   protected void torrentListenerRegistered()
/*      */   {
/*      */     try
/*      */     {
/* 1135 */       this.this_mon.enter();
/*      */       
/* 1137 */       if (!this.server_factory_listener_added)
/*      */       {
/* 1139 */         this.server_factory_listener_added = true;
/*      */         
/* 1141 */         TRTrackerServerFactory.addListener(this);
/*      */       }
/*      */     }
/*      */     finally {
/* 1145 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void serverCreated(TRTrackerServer server)
/*      */   {
/* 1153 */     server.addRequestListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void serverDestroyed(TRTrackerServer server)
/*      */   {
/* 1160 */     server.removeRequestListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void preProcess(TRTrackerServerRequest request)
/*      */     throws TRTrackerServerException
/*      */   {
/* 1169 */     if ((request.getType() == 1) || (request.getType() == 2))
/*      */     {
/*      */ 
/* 1172 */       TRTrackerServerTorrent ts_torrent = request.getTorrent();
/*      */       
/* 1174 */       HashWrapper hash_wrapper = ts_torrent.getHash();
/*      */       
/* 1176 */       TRHostTorrent h_torrent = lookupHostTorrentViaHash(hash_wrapper.getHash());
/*      */       
/* 1178 */       if (h_torrent != null)
/*      */       {
/* 1180 */         TRHostTorrentRequest req = new TRHostTorrentRequestImpl(h_torrent, new TRHostPeerHostImpl(request.getPeer()), request);
/*      */         try
/*      */         {
/* 1183 */           if ((h_torrent instanceof TRHostTorrentHostImpl))
/*      */           {
/* 1185 */             ((TRHostTorrentHostImpl)h_torrent).preProcess(req);
/*      */           }
/*      */           else {
/* 1188 */             ((TRHostTorrentPublishImpl)h_torrent).preProcess(req);
/*      */           }
/*      */         }
/*      */         catch (TRHostException e) {
/* 1192 */           throw new TRTrackerServerException(e.getMessage(), e);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1196 */           throw new TRTrackerServerException("Pre-process fails", e);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void postProcess(TRTrackerServerRequest request)
/*      */     throws TRTrackerServerException
/*      */   {
/* 1208 */     if ((request.getType() == 1) || (request.getType() == 2))
/*      */     {
/*      */ 
/* 1211 */       TRTrackerServerTorrent ts_torrent = request.getTorrent();
/*      */       
/*      */ 
/*      */ 
/* 1215 */       if (ts_torrent != null)
/*      */       {
/* 1217 */         HashWrapper hash_wrapper = ts_torrent.getHash();
/*      */         
/* 1219 */         TRHostTorrent h_torrent = lookupHostTorrentViaHash(hash_wrapper.getHash());
/*      */         
/* 1221 */         if (h_torrent != null)
/*      */         {
/* 1223 */           TRHostTorrentRequest req = new TRHostTorrentRequestImpl(h_torrent, new TRHostPeerHostImpl(request.getPeer()), request);
/*      */           try
/*      */           {
/* 1226 */             if ((h_torrent instanceof TRHostTorrentHostImpl))
/*      */             {
/* 1228 */               ((TRHostTorrentHostImpl)h_torrent).postProcess(req);
/*      */             }
/*      */             else {
/* 1231 */               ((TRHostTorrentPublishImpl)h_torrent).postProcess(req);
/*      */             }
/*      */           }
/*      */           catch (TRHostException e) {
/* 1235 */             throw new TRTrackerServerException("Post process fails", e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void close()
/*      */   {
/* 1245 */     this.closed = true;
/*      */     
/* 1247 */     this.config.saveConfig(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean authenticate(String headers, URL resource, String user, String password)
/*      */   {
/* 1257 */     for (int i = 0; i < this.auth_listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1260 */         boolean res = ((TRHostAuthenticationListener)this.auth_listeners.get(i)).authenticate(headers, resource, user, password);
/*      */         
/* 1262 */         if (res)
/*      */         {
/* 1264 */           return true;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1268 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1272 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public byte[] authenticate(URL resource, String user)
/*      */   {
/* 1280 */     for (int i = 0; i < this.auth_listeners.size(); i++) {
/*      */       try
/*      */       {
/* 1283 */         byte[] res = ((TRHostAuthenticationListener)this.auth_listeners.get(i)).authenticate(resource, user);
/*      */         
/* 1285 */         if (res != null)
/*      */         {
/* 1287 */           return res;
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/* 1291 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */     
/* 1295 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public void addAuthenticationListener(TRHostAuthenticationListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1303 */       this.this_mon.enter();
/*      */       
/* 1305 */       this.auth_listeners.add(l);
/*      */       
/* 1307 */       if (this.auth_listeners.size() == 1)
/*      */       {
/* 1309 */         Iterator it = this.server_map.values().iterator();
/*      */         
/* 1311 */         while (it.hasNext())
/*      */         {
/* 1313 */           ((TRTrackerServer)it.next()).addAuthenticationListener(this);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1318 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeAuthenticationListener(TRHostAuthenticationListener l)
/*      */   {
/*      */     try
/*      */     {
/* 1327 */       this.this_mon.enter();
/*      */       
/* 1329 */       this.auth_listeners.remove(l);
/*      */       
/* 1331 */       if (this.auth_listeners.size() == 0)
/*      */       {
/* 1333 */         Iterator it = this.server_map.values().iterator();
/*      */         
/* 1335 */         while (it.hasNext())
/*      */         {
/* 1337 */           ((TRTrackerServer)it.next()).removeAuthenticationListener(this);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1342 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void startTorrent(TRHostTorrentHostImpl torrent)
/*      */   {
/*      */     try
/*      */     {
/* 1354 */       this.this_mon.enter();
/*      */       
/* 1356 */       torrent.startSupport();
/*      */     }
/*      */     finally
/*      */     {
/* 1360 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void stopTorrent(TRHostTorrentHostImpl torrent)
/*      */   {
/*      */     try
/*      */     {
/* 1369 */       this.this_mon.enter();
/*      */       
/* 1371 */       torrent.stopSupport();
/*      */     }
/*      */     finally
/*      */     {
/* 1375 */       this.this_mon.exit();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void addTrackerAnnounce(TOTorrent torrent)
/*      */   {
/* 1383 */     if (TorrentUtils.isDecentralised(torrent))
/*      */     {
/* 1385 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1390 */     URL[][] url_sets = TRTrackerUtils.getAnnounceURLs();
/*      */     
/* 1392 */     if (url_sets.length == 0)
/*      */     {
/*      */ 
/*      */ 
/* 1396 */       TorrentUtils.setDecentralised(torrent);
/*      */     }
/*      */     else
/*      */     {
/* 1400 */       URL[] primary_urls = url_sets[0];
/*      */       
/*      */ 
/*      */ 
/* 1404 */       for (int i = primary_urls.length - 1; i >= 0; i--)
/*      */       {
/* 1406 */         String url_str = primary_urls[i].toString();
/*      */         
/* 1408 */         if (TorrentUtils.announceGroupsContainsURL(torrent, url_str))
/*      */         {
/* 1410 */           TorrentUtils.announceGroupsSetFirst(torrent, url_str);
/*      */         }
/*      */         else
/*      */         {
/* 1414 */           TorrentUtils.announceGroupsInsertFirst(torrent, url_str);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */