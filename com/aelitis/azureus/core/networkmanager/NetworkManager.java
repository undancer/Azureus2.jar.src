/*     */ package com.aelitis.azureus.core.networkmanager;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.IncomingConnectionManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.IncomingConnectionManager.MatchListener;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.RateControlledEntity;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.ReadController;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransferProcessor;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.TransportHelper;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.WriteController;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.http.HTTPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.tcp.TCPNetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.udp.UDPNetworkManager;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamFactory;
/*     */ import com.aelitis.azureus.core.util.FeatureAvailability;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerListener;
/*     */ import org.gudy.azureus2.core3.util.AddressUtils;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkManager
/*     */ {
/*     */   public static final int UNLIMITED_RATE = 104857600;
/*  54 */   private static final NetworkManager instance = new NetworkManager();
/*     */   
/*     */   private static int max_download_rate_bps;
/*     */   
/*     */   private static int external_max_download_rate_bps;
/*     */   
/*     */   private static int max_upload_rate_bps_normal;
/*     */   
/*     */   private static int max_upload_rate_bps_seeding_only;
/*     */   private static int max_upload_rate_bps;
/*     */   private static boolean lan_rate_enabled;
/*     */   private static int max_lan_upload_rate_bps;
/*     */   private static int max_lan_download_rate_bps;
/*     */   private static boolean seeding_only_mode_allowed;
/*  68 */   private static boolean seeding_only_mode = false;
/*     */   public static boolean REQUIRE_CRYPTO_HANDSHAKE;
/*     */   public static boolean INCOMING_HANDSHAKE_FALLBACK_ALLOWED;
/*     */   public static boolean OUTGOING_HANDSHAKE_FALLBACK_ALLOWED;
/*     */   public static boolean INCOMING_CRYPTO_ALLOWED;
/*     */   private static boolean USE_REQUEST_LIMITING;
/*     */   private final List<WriteController> write_controllers;
/*     */   private final List<ReadController> read_controllers;
/*     */   
/*     */   static
/*     */   {
/*  79 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "network.transport.encrypted.require", "network.transport.encrypted.fallback.incoming", "network.transport.encrypted.fallback.outgoing", "network.transport.encrypted.allow.incoming", "LAN Speed Enabled", "Max Upload Speed KBs", "Max LAN Upload Speed KBs", "Max Upload Speed Seeding KBs", "enable.seedingonly.upload.rate", "Max Download Speed KBs", "Max LAN Download Speed KBs", "network.tcp.mtu.size", "network.udp.mtu.size", "Use Request Limiting" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String ignore)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */         NetworkManager.REQUIRE_CRYPTO_HANDSHAKE = COConfigurationManager.getBooleanParameter("network.transport.encrypted.require");
/*  98 */         NetworkManager.INCOMING_HANDSHAKE_FALLBACK_ALLOWED = COConfigurationManager.getBooleanParameter("network.transport.encrypted.fallback.incoming");
/*  99 */         NetworkManager.OUTGOING_HANDSHAKE_FALLBACK_ALLOWED = COConfigurationManager.getBooleanParameter("network.transport.encrypted.fallback.outgoing");
/* 100 */         NetworkManager.INCOMING_CRYPTO_ALLOWED = COConfigurationManager.getBooleanParameter("network.transport.encrypted.allow.incoming");
/*     */         
/* 102 */         NetworkManager.access$002(COConfigurationManager.getBooleanParameter("Use Request Limiting"));
/*     */         
/* 104 */         NetworkManager.access$102(COConfigurationManager.getIntParameter("Max Upload Speed KBs") * 1024);
/* 105 */         if (NetworkManager.max_upload_rate_bps_normal < 1024) NetworkManager.access$102(104857600);
/* 106 */         if (NetworkManager.max_upload_rate_bps_normal > 104857600) { NetworkManager.access$102(104857600);
/*     */         }
/* 108 */         NetworkManager.access$202(COConfigurationManager.getIntParameter("Max LAN Upload Speed KBs") * 1024);
/* 109 */         if (NetworkManager.max_lan_upload_rate_bps < 1024) NetworkManager.access$202(104857600);
/* 110 */         if (NetworkManager.max_lan_upload_rate_bps > 104857600) { NetworkManager.access$202(104857600);
/*     */         }
/*     */         
/* 113 */         NetworkManager.access$302(COConfigurationManager.getIntParameter("Max Upload Speed Seeding KBs") * 1024);
/* 114 */         if (NetworkManager.max_upload_rate_bps_seeding_only < 1024) NetworkManager.access$302(104857600);
/* 115 */         if (NetworkManager.max_upload_rate_bps_seeding_only > 104857600) { NetworkManager.access$302(104857600);
/*     */         }
/* 117 */         NetworkManager.access$402(COConfigurationManager.getBooleanParameter("enable.seedingonly.upload.rate"));
/*     */         
/*     */ 
/* 120 */         NetworkManager.access$502(NetworkManager.access$602(COConfigurationManager.getIntParameter("Max Download Speed KBs") * 1024));
/* 121 */         if ((NetworkManager.max_download_rate_bps < 1024) || (NetworkManager.max_download_rate_bps > 104857600)) {
/* 122 */           NetworkManager.access$602(104857600);
/* 123 */         } else if ((NetworkManager.USE_REQUEST_LIMITING) && (FeatureAvailability.isRequestLimitingEnabled())) {
/* 124 */           NetworkManager.access$618(Math.max(NetworkManager.max_download_rate_bps * 0.1D, 5120.0D));
/*     */         }
/* 126 */         NetworkManager.access$702(COConfigurationManager.getBooleanParameter("LAN Speed Enabled"));
/* 127 */         NetworkManager.access$802(COConfigurationManager.getIntParameter("Max LAN Download Speed KBs") * 1024);
/* 128 */         if (NetworkManager.max_lan_download_rate_bps < 1024) NetworkManager.access$802(104857600);
/* 129 */         if (NetworkManager.max_lan_download_rate_bps > 104857600) { NetworkManager.access$802(104857600);
/*     */         }
/* 131 */         NetworkManager.access$900();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private NetworkManager()
/*     */   {
/* 142 */     int num_read = COConfigurationManager.getIntParameter("network.control.read.processor.count");
/*     */     
/* 144 */     this.read_controllers = new ArrayList(num_read);
/*     */     
/* 146 */     for (int i = 0; i < num_read; i++)
/*     */     {
/* 148 */       this.read_controllers.add(new ReadController());
/*     */     }
/*     */     
/* 151 */     int num_write = COConfigurationManager.getIntParameter("network.control.write.processor.count");
/*     */     
/* 153 */     this.write_controllers = new ArrayList(num_write);
/*     */     
/* 155 */     for (int i = 0; i < num_write; i++)
/*     */     {
/* 157 */       this.write_controllers.add(new WriteController());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 162 */     this.upload_processor = new TransferProcessor(0, new LimitedRateGroup()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/*     */ 
/* 170 */         return "global_up";
/*     */       }
/*     */       
/*     */       public int getRateLimitBytesPerSecond()
/*     */       {
/* 175 */         return NetworkManager.max_upload_rate_bps;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 180 */       public boolean isDisabled() { return NetworkManager.max_upload_rate_bps == -1; } public void updateBytesUsed(int used) {} }, this.write_controllers.size() > 1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 190 */     this.download_processor = new TransferProcessor(1, new LimitedRateGroup()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/*     */ 
/* 198 */         return "global_down";
/*     */       }
/*     */       
/*     */       public int getRateLimitBytesPerSecond()
/*     */       {
/* 203 */         return NetworkManager.max_download_rate_bps;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 208 */       public boolean isDisabled() { return NetworkManager.max_download_rate_bps == -1; } public void updateBytesUsed(int used) {} }, this.read_controllers.size() > 1);
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
/* 219 */     this.lan_upload_processor = new TransferProcessor(0, new LimitedRateGroup()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/*     */ 
/* 227 */         return "global_lan_up";
/*     */       }
/*     */       
/*     */       public int getRateLimitBytesPerSecond()
/*     */       {
/* 232 */         return NetworkManager.max_lan_upload_rate_bps;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 237 */       public boolean isDisabled() { return NetworkManager.max_lan_upload_rate_bps == -1; } public void updateBytesUsed(int used) {} }, this.write_controllers.size() > 1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 247 */     this.lan_download_processor = new TransferProcessor(1, new LimitedRateGroup()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getName()
/*     */       {
/*     */ 
/*     */ 
/* 255 */         return "global_lan_down";
/*     */       }
/*     */       
/*     */       public int getRateLimitBytesPerSecond()
/*     */       {
/* 260 */         return NetworkManager.max_lan_download_rate_bps;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 265 */       public boolean isDisabled() { return NetworkManager.max_lan_download_rate_bps == -1; } public void updateBytesUsed(int used) {} }, this.read_controllers.size() > 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isLANRateEnabled()
/*     */   {
/* 278 */     return lan_rate_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */   public static int getMinMssSize()
/*     */   {
/* 284 */     return Math.min(TCPNetworkManager.getTcpMssSize(), UDPNetworkManager.getUdpMssSize());
/*     */   }
/*     */   
/*     */   private static void refreshRates() {
/* 288 */     if (isSeedingOnlyUploadRate()) {
/* 289 */       max_upload_rate_bps = max_upload_rate_bps_seeding_only;
/*     */     }
/*     */     else {
/* 292 */       max_upload_rate_bps = max_upload_rate_bps_normal;
/*     */     }
/*     */     
/* 295 */     if (max_upload_rate_bps < 1024) {
/* 296 */       Debug.out("max_upload_rate_bps < 1024=" + max_upload_rate_bps);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 301 */     int min_rate = Math.min(max_upload_rate_bps, Math.min(max_download_rate_bps, Math.min(max_lan_upload_rate_bps, max_lan_download_rate_bps)));
/*     */     
/*     */ 
/*     */ 
/* 305 */     TCPNetworkManager.refreshRates(min_rate);
/* 306 */     UDPNetworkManager.refreshRates(min_rate);
/*     */   }
/*     */   
/*     */   public static boolean isSeedingOnlyUploadRate()
/*     */   {
/* 311 */     return (seeding_only_mode_allowed) && (seeding_only_mode);
/*     */   }
/*     */   
/*     */   public static int getMaxUploadRateBPSNormal() {
/* 315 */     if (max_upload_rate_bps_normal == 104857600) return 0;
/* 316 */     return max_upload_rate_bps_normal;
/*     */   }
/*     */   
/*     */   public static int getMaxUploadRateBPSSeedingOnly() {
/* 320 */     if (max_upload_rate_bps_seeding_only == 104857600) return 0;
/* 321 */     return max_upload_rate_bps_seeding_only;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static int getMaxDownloadRateBPS()
/*     */   {
/* 328 */     if (max_download_rate_bps == 104857600) return 0;
/* 329 */     return external_max_download_rate_bps;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean getCryptoRequired(int override_level)
/*     */   {
/* 341 */     if (override_level == 0)
/*     */     {
/* 343 */       return REQUIRE_CRYPTO_HANDSHAKE;
/*     */     }
/* 345 */     if (override_level == 1)
/*     */     {
/* 347 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 351 */     return false;
/*     */   }
/*     */   
/*     */   public void initialize(AzureusCore core)
/*     */   {
/* 356 */     HTTPNetworkManager.getSingleton();
/*     */     
/* 358 */     core.getGlobalManager().addListener(new GlobalManagerListener() { public void downloadManagerAdded(DownloadManager dm) {}
/*     */       
/*     */       public void downloadManagerRemoved(DownloadManager dm) {}
/*     */       
/*     */       public void destroyInitiated() {}
/*     */       
/*     */       public void destroyed() {}
/* 365 */       public void seedingStatusChanged(boolean seeding_only, boolean b) { NetworkManager.access$1102(seeding_only);
/* 366 */         NetworkManager.access$900();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static NetworkManager getSingleton()
/*     */   {
/* 377 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetworkConnection createConnection(ConnectionEndpoint target, MessageStreamEncoder encoder, MessageStreamDecoder decoder, boolean connect_with_crypto, boolean allow_fallback, byte[][] shared_secrets)
/*     */   {
/* 389 */     return NetworkConnectionFactory.create(target, encoder, decoder, connect_with_crypto, allow_fallback, shared_secrets);
/*     */   }
/*     */   
/*     */ 
/*     */   private final TransferProcessor upload_processor;
/*     */   
/*     */   private final TransferProcessor download_processor;
/*     */   
/*     */   private final TransferProcessor lan_upload_processor;
/*     */   
/*     */   private final TransferProcessor lan_download_processor;
/*     */   
/*     */   public static final int CRYPTO_OVERRIDE_NONE = 0;
/*     */   public static final int CRYPTO_OVERRIDE_REQUIRED = 1;
/*     */   public static final int CRYPTO_OVERRIDE_NOT_REQUIRED = 2;
/*     */   public void requestIncomingConnectionRouting(ByteMatcher matcher, final RoutingListener listener, final MessageStreamFactory factory)
/*     */   {
/* 406 */     IncomingConnectionManager.getSingleton().registerMatchBytes(matcher, new IncomingConnectionManager.MatchListener()
/*     */     {
/*     */       public boolean autoCryptoFallback()
/*     */       {
/* 410 */         return listener.autoCryptoFallback();
/*     */       }
/*     */       
/* 413 */       public void connectionMatched(Transport transport, Object routing_data) { listener.connectionRouted(NetworkConnectionFactory.create(transport, factory.createEncoder(), factory.createDecoder()), routing_data); }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NetworkConnection bindTransport(Transport transport, MessageStreamEncoder encoder, MessageStreamDecoder decoder)
/*     */   {
/* 424 */     return NetworkConnectionFactory.create(transport, encoder, decoder);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cancelIncomingConnectionRouting(ByteMatcher matcher)
/*     */   {
/* 433 */     IncomingConnectionManager.getSingleton().deregisterMatchBytes(matcher);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addWriteEntity(RateControlledEntity entity, int partition_id)
/*     */   {
/* 444 */     if ((this.write_controllers.size() == 1) || (partition_id < 0))
/*     */     {
/* 446 */       ((WriteController)this.write_controllers.get(0)).addWriteEntity(entity);
/*     */     }
/*     */     else
/*     */     {
/* 450 */       WriteController controller = (WriteController)this.write_controllers.get(partition_id % (this.write_controllers.size() - 1) + 1);
/*     */       
/* 452 */       controller.addWriteEntity(entity);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeWriteEntity(RateControlledEntity entity)
/*     */   {
/* 462 */     if (this.write_controllers.size() == 1) {
/* 463 */       ((WriteController)this.write_controllers.get(0)).removeWriteEntity(entity);
/*     */     } else {
/* 465 */       for (WriteController write_controller : this.write_controllers) {
/* 466 */         write_controller.removeWriteEntity(entity);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addReadEntity(RateControlledEntity entity, int partition_id)
/*     */   {
/* 477 */     if ((this.read_controllers.size() == 1) || (partition_id < 0))
/*     */     {
/* 479 */       ((ReadController)this.read_controllers.get(0)).addReadEntity(entity);
/*     */     }
/*     */     else
/*     */     {
/* 483 */       ReadController controller = (ReadController)this.read_controllers.get(partition_id % (this.read_controllers.size() - 1) + 1);
/*     */       
/* 485 */       controller.addReadEntity(entity);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeReadEntity(RateControlledEntity entity)
/*     */   {
/* 495 */     if (this.read_controllers.size() == 1) {
/* 496 */       ((ReadController)this.read_controllers.get(0)).removeReadEntity(entity);
/*     */     } else {
/* 498 */       for (ReadController read_controller : this.read_controllers) {
/* 499 */         read_controller.removeReadEntity(entity);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Set<NetworkConnectionBase> getConnections()
/*     */   {
/* 507 */     Set<NetworkConnectionBase> result = new HashSet();
/*     */     
/* 509 */     result.addAll(this.lan_upload_processor.getConnections());
/* 510 */     result.addAll(this.lan_download_processor.getConnections());
/* 511 */     result.addAll(this.upload_processor.getConnections());
/* 512 */     result.addAll(this.download_processor.getConnections());
/*     */     
/* 514 */     return result;
/*     */   }
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
/*     */   public void startTransferProcessing(NetworkConnectionBase peer_connection)
/*     */   {
/* 529 */     if ((lan_rate_enabled) && ((peer_connection.isLANLocal()) || (AddressUtils.applyLANRateLimits(peer_connection.getEndpoint().getNotionalAddress()))))
/*     */     {
/*     */ 
/*     */ 
/* 533 */       this.lan_upload_processor.registerPeerConnection(peer_connection, true);
/* 534 */       this.lan_download_processor.registerPeerConnection(peer_connection, false);
/*     */     }
/*     */     else
/*     */     {
/* 538 */       this.upload_processor.registerPeerConnection(peer_connection, true);
/* 539 */       this.download_processor.registerPeerConnection(peer_connection, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stopTransferProcessing(NetworkConnectionBase peer_connection)
/*     */   {
/* 549 */     if (this.lan_upload_processor.isRegistered(peer_connection)) {
/* 550 */       this.lan_upload_processor.deregisterPeerConnection(peer_connection);
/* 551 */       this.lan_download_processor.deregisterPeerConnection(peer_connection);
/*     */     }
/*     */     else {
/* 554 */       this.upload_processor.deregisterPeerConnection(peer_connection);
/* 555 */       this.download_processor.deregisterPeerConnection(peer_connection);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void upgradeTransferProcessing(NetworkConnectionBase peer_connection, int partition_id)
/*     */   {
/* 565 */     if (this.lan_upload_processor.isRegistered(peer_connection)) {
/* 566 */       this.lan_upload_processor.upgradePeerConnection(peer_connection, partition_id);
/* 567 */       this.lan_download_processor.upgradePeerConnection(peer_connection, partition_id);
/*     */     }
/*     */     else {
/* 570 */       this.upload_processor.upgradePeerConnection(peer_connection, partition_id);
/* 571 */       this.download_processor.upgradePeerConnection(peer_connection, partition_id);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downgradeTransferProcessing(NetworkConnectionBase peer_connection)
/*     */   {
/* 580 */     if (this.lan_upload_processor.isRegistered(peer_connection)) {
/* 581 */       this.lan_upload_processor.downgradePeerConnection(peer_connection);
/* 582 */       this.lan_download_processor.downgradePeerConnection(peer_connection);
/*     */     }
/*     */     else {
/* 585 */       this.upload_processor.downgradePeerConnection(peer_connection);
/* 586 */       this.download_processor.downgradePeerConnection(peer_connection);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TransferProcessor getUploadProcessor()
/*     */   {
/* 593 */     return this.upload_processor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRateLimiter(NetworkConnectionBase peer_connection, LimitedRateGroup group, boolean upload)
/*     */   {
/* 602 */     if (upload) {
/* 603 */       if (this.lan_upload_processor.isRegistered(peer_connection))
/*     */       {
/* 605 */         this.lan_upload_processor.addRateLimiter(peer_connection, group);
/*     */       }
/*     */       else {
/* 608 */         this.upload_processor.addRateLimiter(peer_connection, group);
/*     */       }
/*     */     }
/* 611 */     else if (this.lan_download_processor.isRegistered(peer_connection))
/*     */     {
/* 613 */       this.lan_download_processor.addRateLimiter(peer_connection, group);
/*     */     }
/*     */     else {
/* 616 */       this.download_processor.addRateLimiter(peer_connection, group);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRateLimiter(NetworkConnectionBase peer_connection, LimitedRateGroup group, boolean upload)
/*     */   {
/* 627 */     if (upload) {
/* 628 */       if (this.lan_upload_processor.isRegistered(peer_connection))
/*     */       {
/* 630 */         this.lan_upload_processor.removeRateLimiter(peer_connection, group);
/*     */       }
/*     */       else {
/* 633 */         this.upload_processor.removeRateLimiter(peer_connection, group);
/*     */       }
/*     */     }
/* 636 */     else if (this.lan_download_processor.isRegistered(peer_connection))
/*     */     {
/* 638 */       this.lan_download_processor.removeRateLimiter(peer_connection, group);
/*     */     }
/*     */     else {
/* 641 */       this.download_processor.removeRateLimiter(peer_connection, group);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RateHandler getRateHandler(boolean upload, boolean lan)
/*     */   {
/* 651 */     if (upload)
/*     */     {
/* 653 */       if (lan)
/*     */       {
/* 655 */         return this.lan_upload_processor.getRateHandler();
/*     */       }
/*     */       
/* 658 */       return this.upload_processor.getRateHandler();
/*     */     }
/*     */     
/* 661 */     if (lan)
/*     */     {
/* 663 */       return this.lan_download_processor.getRateHandler();
/*     */     }
/*     */     
/* 666 */     return this.download_processor.getRateHandler();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RateHandler getRateHandler(NetworkConnectionBase peer_connection, boolean upload)
/*     */   {
/* 676 */     if (upload)
/*     */     {
/* 678 */       if (this.lan_upload_processor.isRegistered(peer_connection))
/*     */       {
/* 680 */         return this.lan_upload_processor.getRateHandler(peer_connection);
/*     */       }
/*     */       
/* 683 */       return this.upload_processor.getRateHandler(peer_connection);
/*     */     }
/*     */     
/* 686 */     if (this.lan_download_processor.isRegistered(peer_connection))
/*     */     {
/* 688 */       return this.lan_download_processor.getRateHandler(peer_connection);
/*     */     }
/*     */     
/* 691 */     return this.download_processor.getRateHandler(peer_connection);
/*     */   }
/*     */   
/*     */   public static abstract interface ByteMatcher
/*     */   {
/*     */     public abstract int matchThisSizeOrBigger();
/*     */     
/*     */     public abstract int maxSize();
/*     */     
/*     */     public abstract int minSize();
/*     */     
/*     */     public abstract Object matches(TransportHelper paramTransportHelper, ByteBuffer paramByteBuffer, int paramInt);
/*     */     
/*     */     public abstract Object minMatches(TransportHelper paramTransportHelper, ByteBuffer paramByteBuffer, int paramInt);
/*     */     
/*     */     public abstract byte[][] getSharedSecrets();
/*     */     
/*     */     public abstract int getSpecificPort();
/*     */   }
/*     */   
/*     */   public static abstract interface RoutingListener
/*     */   {
/*     */     public abstract boolean autoCryptoFallback();
/*     */     
/*     */     public abstract void connectionRouted(NetworkConnection paramNetworkConnection, Object paramObject);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/NetworkManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */