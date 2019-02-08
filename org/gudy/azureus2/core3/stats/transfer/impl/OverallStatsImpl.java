/*     */ package org.gudy.azureus2.core3.stats.transfer.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*     */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerAdapter;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class OverallStatsImpl
/*     */   extends GlobalManagerAdapter
/*     */   implements OverallStats
/*     */ {
/*     */   private static final long TEN_YEARS = 315360000L;
/*     */   private static final int STATS_PERIOD = 60000;
/*     */   private static final int SAVE_PERIOD = 600000;
/*     */   private static final int SAVE_TICKS = 10;
/*     */   final AzureusCore core;
/*     */   final GlobalManagerStats gm_stats;
/*     */   private DHT[] dhts;
/*     */   private long totalDownloaded;
/*     */   private long totalUploaded;
/*     */   private long totalUptime;
/*     */   private long markTime;
/*     */   private long markTotalDownloaded;
/*     */   private long markTotalUploaded;
/*     */   private long markTotalUptime;
/*     */   private long totalDHTUploaded;
/*     */   private long totalDHTDownloaded;
/*     */   private long lastDownloaded;
/*     */   private long lastUploaded;
/*     */   private long lastUptime;
/*     */   private long lastDHTUploaded;
/*     */   private long lastDHTDownloaded;
/*     */   private long totalProtocolUploaded;
/*     */   private long totalDataUploaded;
/*     */   private long totalProtocolDownloaded;
/*     */   private long totalDataDownloaded;
/*     */   private long lastProtocolUploaded;
/*     */   private long lastDataUploaded;
/*     */   private long lastProtocolDownloaded;
/*     */   private long lastDataDownloaded;
/*     */   private long[] lastSnapshot;
/* 102 */   private final long session_start_time = SystemTime.getCurrentTime();
/*     */   
/* 104 */   protected final AEMonitor this_mon = new AEMonitor("OverallStats");
/*     */   
/*     */   private int tick_count;
/*     */   
/*     */ 
/*     */   private Map load(String filename)
/*     */   {
/* 111 */     return FileUtil.readResilientConfigFile(filename);
/*     */   }
/*     */   
/*     */   private Map load() {
/* 115 */     return load("azureus.statistics");
/*     */   }
/*     */   
/*     */ 
/*     */   private void save(String filename, Map map)
/*     */   {
/*     */     try
/*     */     {
/* 123 */       this.this_mon.enter();
/*     */       
/* 125 */       FileUtil.writeResilientConfigFile(filename, map);
/*     */     }
/*     */     finally
/*     */     {
/* 129 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private void save(Map map) {
/* 134 */     save("azureus.statistics", map);
/*     */   }
/*     */   
/*     */ 
/*     */   private void validateAndLoadValues(Map statisticsMap)
/*     */   {
/* 140 */     this.lastUptime = (SystemTime.getCurrentTime() / 1000L);
/*     */     
/* 142 */     Map overallMap = (Map)statisticsMap.get("all");
/*     */     
/* 144 */     this.totalDownloaded = getLong(overallMap, "downloaded");
/* 145 */     this.totalUploaded = getLong(overallMap, "uploaded");
/* 146 */     this.totalUptime = getLong(overallMap, "uptime");
/*     */     
/* 148 */     this.markTime = getLong(overallMap, "mark_time");
/* 149 */     this.markTotalDownloaded = getLong(overallMap, "mark_downloaded");
/* 150 */     this.markTotalUploaded = getLong(overallMap, "mark_uploaded");
/* 151 */     this.markTotalUptime = getLong(overallMap, "mark_uptime");
/*     */     
/* 153 */     this.totalDHTDownloaded = getLong(overallMap, "dht_down");
/* 154 */     this.totalDHTUploaded = getLong(overallMap, "dht_up");
/*     */     
/* 156 */     this.totalProtocolUploaded = getLong(overallMap, "p_uploaded");
/* 157 */     this.totalDataUploaded = getLong(overallMap, "d_uploaded");
/* 158 */     this.totalProtocolDownloaded = getLong(overallMap, "p_downloaded");
/* 159 */     this.totalDataDownloaded = getLong(overallMap, "d_downloaded");
/*     */     
/*     */ 
/* 162 */     long current_total_d_received = this.gm_stats.getTotalDataBytesReceived();
/* 163 */     long current_total_p_received = this.gm_stats.getTotalProtocolBytesReceived();
/*     */     
/* 165 */     long current_total_d_sent = this.gm_stats.getTotalDataBytesSent();
/* 166 */     long current_total_p_sent = this.gm_stats.getTotalProtocolBytesSent();
/*     */     
/* 168 */     this.lastSnapshot = new long[] { this.totalProtocolUploaded, this.totalDataUploaded, this.totalProtocolDownloaded, this.totalDataDownloaded, this.totalDHTUploaded, this.totalDHTDownloaded, current_total_p_sent, current_total_d_sent, current_total_p_received, current_total_d_received, 0L, 0L };
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
/*     */   protected long getLong(Map map, String name)
/*     */   {
/* 182 */     if (map == null) {
/* 183 */       return 0L;
/*     */     }
/*     */     
/* 186 */     Object obj = map.get(name);
/*     */     
/* 188 */     if (!(obj instanceof Long)) {
/* 189 */       return 0L;
/*     */     }
/*     */     
/* 192 */     return ((Long)obj).longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public OverallStatsImpl(AzureusCore _core, GlobalManagerStats _gm_stats)
/*     */   {
/* 200 */     this.core = _core;
/* 201 */     this.gm_stats = _gm_stats;
/*     */     
/* 203 */     Map stats = load();
/*     */     
/* 205 */     validateAndLoadValues(stats);
/*     */     
/* 207 */     Set types = new HashSet();
/*     */     
/* 209 */     types.add("xfer.upload.protocol.bytes.total");
/* 210 */     types.add("xfer.upload.data.bytes.total");
/* 211 */     types.add("xfer.download.protocol.bytes.total");
/* 212 */     types.add("xfer.download.data.bytes.total");
/*     */     
/* 214 */     AzureusCoreStats.registerProvider(types, new AzureusCoreStatsProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void updateStats(Set types, Map values)
/*     */       {
/*     */ 
/*     */         try
/*     */         {
/*     */ 
/* 224 */           OverallStatsImpl.this.this_mon.enter();
/*     */           
/* 226 */           if (OverallStatsImpl.this.core.isStarted())
/*     */           {
/* 228 */             if (types.contains("xfer.upload.protocol.bytes.total"))
/*     */             {
/* 230 */               values.put("xfer.upload.protocol.bytes.total", new Long(OverallStatsImpl.this.totalProtocolUploaded + (OverallStatsImpl.this.gm_stats.getTotalProtocolBytesSent() - OverallStatsImpl.this.lastProtocolUploaded)));
/*     */             }
/*     */             
/*     */ 
/* 234 */             if (types.contains("xfer.upload.data.bytes.total"))
/*     */             {
/* 236 */               values.put("xfer.upload.data.bytes.total", new Long(OverallStatsImpl.this.totalDataUploaded + (OverallStatsImpl.this.gm_stats.getTotalDataBytesSent() - OverallStatsImpl.this.lastDataUploaded)));
/*     */             }
/*     */             
/*     */ 
/* 240 */             if (types.contains("xfer.download.protocol.bytes.total"))
/*     */             {
/* 242 */               values.put("xfer.download.protocol.bytes.total", new Long(OverallStatsImpl.this.totalProtocolDownloaded + (OverallStatsImpl.this.gm_stats.getTotalProtocolBytesReceived() - OverallStatsImpl.this.lastProtocolDownloaded)));
/*     */             }
/*     */             
/*     */ 
/* 246 */             if (types.contains("xfer.download.data.bytes.total"))
/*     */             {
/* 248 */               values.put("xfer.download.data.bytes.total", new Long(OverallStatsImpl.this.totalDataDownloaded + (OverallStatsImpl.this.gm_stats.getTotalDataBytesReceived() - OverallStatsImpl.this.lastDataDownloaded)));
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 255 */           OverallStatsImpl.this.this_mon.exit();
/*     */         }
/*     */         
/*     */       }
/* 259 */     });
/* 260 */     this.core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*     */       {
/*     */ 
/*     */ 
/* 268 */         if ((component instanceof GlobalManager))
/*     */         {
/* 270 */           GlobalManager gm = (GlobalManager)component;
/*     */           
/* 272 */           gm.addListener(OverallStatsImpl.this, false);
/*     */           
/* 274 */           SimpleTimer.addPeriodicEvent("OverallStats", 60000L, new TimerEventPerformer()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void perform(TimerEvent event)
/*     */             {
/*     */ 
/*     */ 
/* 282 */               OverallStatsImpl.this.updateStats(false);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public int getAverageDownloadSpeed()
/*     */   {
/* 292 */     if (this.totalUptime > 1L) {
/* 293 */       return (int)(this.totalDownloaded / this.totalUptime);
/*     */     }
/* 295 */     return 0;
/*     */   }
/*     */   
/*     */   public int getAverageUploadSpeed() {
/* 299 */     if (this.totalUptime > 1L) {
/* 300 */       return (int)(this.totalUploaded / this.totalUptime);
/*     */     }
/* 302 */     return 0;
/*     */   }
/*     */   
/*     */   public long getDownloadedBytes() {
/* 306 */     return this.totalDownloaded;
/*     */   }
/*     */   
/*     */   public long getUploadedBytes() {
/* 310 */     return this.totalUploaded;
/*     */   }
/*     */   
/*     */   public long getTotalUpTime() {
/* 314 */     return this.totalUptime;
/*     */   }
/*     */   
/*     */   public long getDownloadedBytes(boolean since_mark)
/*     */   {
/* 319 */     if (since_mark) {
/* 320 */       if (this.markTotalDownloaded > this.totalDownloaded) {
/* 321 */         this.markTotalDownloaded = this.totalDownloaded;
/*     */       }
/* 323 */       return this.totalDownloaded - this.markTotalDownloaded;
/*     */     }
/* 325 */     return this.totalDownloaded;
/*     */   }
/*     */   
/*     */   public long getUploadedBytes(boolean since_mark)
/*     */   {
/* 330 */     if (since_mark) {
/* 331 */       if (this.markTotalUploaded > this.totalUploaded) {
/* 332 */         this.markTotalUploaded = this.totalUploaded;
/*     */       }
/* 334 */       return this.totalUploaded - this.markTotalUploaded;
/*     */     }
/* 336 */     return this.totalUploaded;
/*     */   }
/*     */   
/*     */   public long getTotalUpTime(boolean since_mark) {
/* 340 */     if (since_mark) {
/* 341 */       if (this.markTotalUptime > this.totalUptime) {
/* 342 */         this.markTotalUptime = this.totalUptime;
/*     */       }
/* 344 */       return this.totalUptime - this.markTotalUptime;
/*     */     }
/* 346 */     return this.totalUptime;
/*     */   }
/*     */   
/*     */   public int getAverageDownloadSpeed(boolean since_mark)
/*     */   {
/* 351 */     if (since_mark) {
/* 352 */       long up_time = getTotalUpTime(true);
/* 353 */       long down = getDownloadedBytes(true);
/* 354 */       if (up_time > 1L) {
/* 355 */         return (int)(down / up_time);
/*     */       }
/* 357 */       return 0;
/*     */     }
/* 359 */     return getAverageDownloadSpeed();
/*     */   }
/*     */   
/*     */   public int getAverageUploadSpeed(boolean since_mark)
/*     */   {
/* 364 */     if (since_mark) {
/* 365 */       long up_time = getTotalUpTime(true);
/* 366 */       long up = getUploadedBytes(true);
/* 367 */       if (up_time > 1L) {
/* 368 */         return (int)(up / up_time);
/*     */       }
/* 370 */       return 0;
/*     */     }
/* 372 */     return getAverageUploadSpeed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getMarkTime()
/*     */   {
/* 379 */     return this.markTime;
/*     */   }
/*     */   
/*     */   public void setMark()
/*     */   {
/* 384 */     this.markTime = SystemTime.getCurrentTime();
/* 385 */     this.markTotalDownloaded = this.totalDownloaded;
/* 386 */     this.markTotalUploaded = this.totalUploaded;
/* 387 */     this.markTotalUptime = this.totalUptime;
/*     */   }
/*     */   
/*     */ 
/*     */   public void clearMark()
/*     */   {
/* 393 */     this.markTime = 0L;
/* 394 */     this.markTotalDownloaded = 0L;
/* 395 */     this.markTotalUploaded = 0L;
/* 396 */     this.markTotalUptime = 0L;
/*     */   }
/*     */   
/*     */   public long getSessionUpTime() {
/* 400 */     return (SystemTime.getCurrentTime() - this.session_start_time) / 1000L;
/*     */   }
/*     */   
/*     */   public void destroyInitiated() {
/* 404 */     updateStats(true);
/*     */   }
/*     */   
/*     */   protected long[] getLastSnapshot()
/*     */   {
/*     */     try
/*     */     {
/* 411 */       this.this_mon.enter();
/*     */       
/* 413 */       return this.lastSnapshot;
/*     */     }
/*     */     finally
/*     */     {
/* 417 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateStats(boolean force) {
/*     */     try {
/* 423 */       this.this_mon.enter();
/*     */       
/* 425 */       long current_time = SystemTime.getCurrentTime() / 1000L;
/*     */       
/* 427 */       if (current_time < this.lastUptime) {
/* 428 */         this.lastUptime = current_time;
/*     */       }
/*     */       else
/*     */       {
/* 432 */         long current_total_d_received = this.gm_stats.getTotalDataBytesReceived();
/* 433 */         long current_total_p_received = this.gm_stats.getTotalProtocolBytesReceived();
/*     */         
/* 435 */         long current_total_d_sent = this.gm_stats.getTotalDataBytesSent();
/* 436 */         long current_total_p_sent = this.gm_stats.getTotalProtocolBytesSent();
/*     */         
/* 438 */         long current_total_received = current_total_d_received + current_total_p_received;
/* 439 */         long current_total_sent = current_total_d_sent + current_total_p_sent;
/*     */         
/*     */ 
/*     */ 
/* 443 */         this.totalDownloaded += current_total_received - this.lastDownloaded;
/* 444 */         this.lastDownloaded = current_total_received;
/* 445 */         if (this.totalDownloaded < 0L) { this.totalDownloaded = 0L;
/*     */         }
/* 447 */         this.totalUploaded += current_total_sent - this.lastUploaded;
/* 448 */         this.lastUploaded = current_total_sent;
/* 449 */         if (this.totalUploaded < 0L) { this.totalUploaded = 0L;
/*     */         }
/*     */         
/*     */ 
/* 453 */         this.totalDataDownloaded += current_total_d_received - this.lastDataDownloaded;
/* 454 */         this.lastDataDownloaded = current_total_d_received;
/* 455 */         if (this.totalDataDownloaded < 0L) { this.totalDataDownloaded = 0L;
/*     */         }
/* 457 */         this.totalProtocolDownloaded += current_total_p_received - this.lastProtocolDownloaded;
/* 458 */         this.lastProtocolDownloaded = current_total_p_received;
/* 459 */         if (this.totalProtocolDownloaded < 0L) { this.totalProtocolDownloaded = 0L;
/*     */         }
/* 461 */         this.totalDataUploaded += current_total_d_sent - this.lastDataUploaded;
/* 462 */         this.lastDataUploaded = current_total_d_sent;
/* 463 */         if (this.totalDataUploaded < 0L) { this.totalDataUploaded = 0L;
/*     */         }
/* 465 */         this.totalProtocolUploaded += current_total_p_sent - this.lastProtocolUploaded;
/* 466 */         this.lastProtocolUploaded = current_total_p_sent;
/* 467 */         if (this.totalProtocolUploaded < 0L) { this.totalProtocolUploaded = 0L;
/*     */         }
/*     */         
/*     */ 
/* 471 */         if (this.dhts == null) {
/*     */           try
/*     */           {
/* 474 */             PluginManager pm = this.core.getPluginManager();
/*     */             
/* 476 */             if (pm.isInitialized())
/*     */             {
/* 478 */               PluginInterface dht_pi = pm.getPluginInterfaceByClass(DHTPlugin.class);
/*     */               
/* 480 */               if (dht_pi == null)
/*     */               {
/* 482 */                 this.dhts = new DHT[0];
/*     */               }
/*     */               else
/*     */               {
/* 486 */                 DHTPlugin plugin = (DHTPlugin)dht_pi.getPlugin();
/*     */                 
/* 488 */                 if (!plugin.isInitialising())
/*     */                 {
/* 490 */                   if (plugin.isEnabled())
/*     */                   {
/* 492 */                     this.dhts = ((DHTPlugin)dht_pi.getPlugin()).getDHTs();
/*     */                   }
/*     */                   else
/*     */                   {
/* 496 */                     this.dhts = new DHT[0];
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 503 */             this.dhts = new DHT[0];
/*     */           }
/*     */         }
/*     */         
/* 507 */         long current_total_dht_up = 0L;
/* 508 */         long current_total_dht_down = 0L;
/*     */         
/* 510 */         if (this.dhts != null)
/*     */         {
/* 512 */           for (DHT dht : this.dhts)
/*     */           {
/* 514 */             DHTTransportStats stats = dht.getTransport().getStats();
/*     */             
/* 516 */             current_total_dht_up += stats.getBytesSent();
/* 517 */             current_total_dht_down += stats.getBytesReceived();
/*     */           }
/*     */         }
/*     */         
/* 521 */         this.totalDHTUploaded += current_total_dht_up - this.lastDHTUploaded;
/* 522 */         this.lastDHTUploaded = current_total_dht_up;
/* 523 */         if (this.totalDHTUploaded < 0L) { this.totalDHTUploaded = 0L;
/*     */         }
/* 525 */         this.totalDHTDownloaded += current_total_dht_down - this.lastDHTDownloaded;
/* 526 */         this.lastDHTDownloaded = current_total_dht_down;
/* 527 */         if (this.totalDHTDownloaded < 0L) { this.totalDHTDownloaded = 0L;
/*     */         }
/*     */         
/*     */ 
/* 531 */         long delta = current_time - this.lastUptime;
/*     */         
/* 533 */         if ((delta > 100L) || (delta < 0L)) {
/* 534 */           this.lastUptime = current_time;
/*     */         }
/*     */         else
/*     */         {
/* 538 */           if (this.totalUptime > 315360000L) {
/* 539 */             this.totalUptime = 0L;
/*     */           }
/*     */           
/* 542 */           if (this.totalUptime < 0L) { this.totalUptime = 0L;
/*     */           }
/* 544 */           this.totalUptime += delta;
/* 545 */           this.lastUptime = current_time;
/*     */           
/* 547 */           this.lastSnapshot = new long[] { this.totalProtocolUploaded, this.totalDataUploaded, this.totalProtocolDownloaded, this.totalDataDownloaded, this.totalDHTUploaded, this.totalDHTDownloaded, current_total_p_sent, current_total_d_sent, current_total_p_received, current_total_d_received, current_total_dht_up, current_total_dht_down };
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 557 */           HashMap overallMap = new HashMap();
/*     */           
/* 559 */           overallMap.put("downloaded", new Long(this.totalDownloaded));
/* 560 */           overallMap.put("uploaded", new Long(this.totalUploaded));
/* 561 */           overallMap.put("uptime", new Long(this.totalUptime));
/*     */           
/* 563 */           overallMap.put("mark_time", new Long(this.markTime));
/* 564 */           overallMap.put("mark_downloaded", new Long(this.markTotalDownloaded));
/* 565 */           overallMap.put("mark_uploaded", new Long(this.markTotalUploaded));
/* 566 */           overallMap.put("mark_uptime", new Long(this.markTotalUptime));
/*     */           
/*     */ 
/* 569 */           overallMap.put("dht_down", new Long(this.totalDHTDownloaded));
/* 570 */           overallMap.put("dht_up", new Long(this.totalDHTUploaded));
/*     */           
/* 572 */           overallMap.put("p_uploaded", new Long(this.totalProtocolUploaded));
/* 573 */           overallMap.put("d_uploaded", new Long(this.totalDataUploaded));
/* 574 */           overallMap.put("p_downloaded", new Long(this.totalProtocolDownloaded));
/* 575 */           overallMap.put("d_downloaded", new Long(this.totalDataDownloaded));
/*     */           
/* 577 */           Map map = new HashMap();
/*     */           
/* 579 */           map.put("all", overallMap);
/*     */           
/* 581 */           this.tick_count += 1;
/*     */           
/* 583 */           if ((force) || (this.tick_count % 10 == 0))
/*     */           {
/* 585 */             save(map); }
/*     */         }
/*     */       }
/*     */     } finally {
/* 589 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/impl/OverallStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */