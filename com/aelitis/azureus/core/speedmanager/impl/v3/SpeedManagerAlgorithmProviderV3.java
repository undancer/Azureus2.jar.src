/*     */ package com.aelitis.azureus.core.speedmanager.impl.v3;
/*     */ 
/*     */ import com.aelitis.azureus.core.neuronal.NeuralSpeedLimiter;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProvider;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
/*     */ import com.aelitis.azureus.core.util.average.Average;
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class SpeedManagerAlgorithmProviderV3
/*     */   implements SpeedManagerAlgorithmProvider
/*     */ {
/*     */   private static final String CONFIG_MIN_UP = "AutoSpeed Min Upload KBs";
/*     */   private static final String CONFIG_MAX_UP = "AutoSpeed Max Upload KBs";
/*     */   private static final String CONFIG_MAX_INC = "AutoSpeed Max Increment KBs";
/*     */   private static final String CONFIG_MAX_DEC = "AutoSpeed Max Decrement KBs";
/*     */   private static final String CONFIG_CHOKE_PING = "AutoSpeed Choking Ping Millis";
/*     */   private static final String CONFIG_DOWNADJ_ENABLE = "AutoSpeed Download Adj Enable";
/*     */   private static final String CONFIG_DOWNADJ_RATIO = "AutoSpeed Download Adj Ratio";
/*     */   private static final String CONFIG_LATENCY_FACTOR = "AutoSpeed Latency Factor";
/*     */   private static final String CONFIG_FORCED_MIN = "AutoSpeed Forced Min KBs";
/*     */   private static int PING_CHOKE_TIME;
/*     */   private static int MIN_UP;
/*     */   private static int MAX_UP;
/*     */   private static boolean ADJUST_DOWNLOAD_ENABLE;
/*     */   private static float ADJUST_DOWNLOAD_RATIO;
/*     */   private static int MAX_INCREMENT;
/*     */   private static int MAX_DECREMENT;
/*     */   private static int LATENCY_FACTOR;
/*     */   private static int FORCED_MIN_SPEED;
/*  60 */   private static final String[] CONFIG_PARAMS = { "AutoSpeed Min Upload KBs", "AutoSpeed Max Upload KBs", "AutoSpeed Max Increment KBs", "AutoSpeed Max Decrement KBs", "AutoSpeed Choking Ping Millis", "AutoSpeed Download Adj Enable", "AutoSpeed Download Adj Ratio", "AutoSpeed Latency Factor", "AutoSpeed Forced Min KBs" };
/*     */   private static final int UNLIMITED = Integer.MAX_VALUE;
/*     */   private static final int MODE_RUNNING = 0;
/*     */   private static final int MODE_FORCED_MIN = 1;
/*     */   private static final int MODE_FORCED_MAX = 2;
/*     */   private static final int FORCED_MAX_TICKS = 30;
/*     */   private static final int FORCED_MIN_TICKS = 60;
/*     */   private static final int FORCED_MIN_AT_START_TICK_LIMIT = 60;
/*     */   
/*     */   static
/*     */   {
/*  71 */     COConfigurationManager.addAndFireParameterListeners(CONFIG_PARAMS, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  79 */         SpeedManagerAlgorithmProviderV3.access$002(COConfigurationManager.getIntParameter("AutoSpeed Choking Ping Millis"));
/*  80 */         SpeedManagerAlgorithmProviderV3.access$102(COConfigurationManager.getIntParameter("AutoSpeed Min Upload KBs") * 1024);
/*  81 */         SpeedManagerAlgorithmProviderV3.access$202(COConfigurationManager.getIntParameter("AutoSpeed Max Upload KBs") * 1024);
/*  82 */         SpeedManagerAlgorithmProviderV3.access$302(COConfigurationManager.getIntParameter("AutoSpeed Max Increment KBs") * 1024);
/*  83 */         SpeedManagerAlgorithmProviderV3.access$402(COConfigurationManager.getIntParameter("AutoSpeed Max Decrement KBs") * 1024);
/*  84 */         SpeedManagerAlgorithmProviderV3.access$502(COConfigurationManager.getBooleanParameter("AutoSpeed Download Adj Enable"));
/*  85 */         String str = COConfigurationManager.getStringParameter("AutoSpeed Download Adj Ratio");
/*  86 */         SpeedManagerAlgorithmProviderV3.access$602(COConfigurationManager.getIntParameter("AutoSpeed Latency Factor"));
/*     */         
/*  88 */         if (SpeedManagerAlgorithmProviderV3.LATENCY_FACTOR < 1) {
/*  89 */           SpeedManagerAlgorithmProviderV3.access$602(1);
/*     */         }
/*     */         
/*  92 */         SpeedManagerAlgorithmProviderV3.access$702(COConfigurationManager.getIntParameter("AutoSpeed Forced Min KBs") * 1024);
/*     */         
/*  94 */         if (SpeedManagerAlgorithmProviderV3.FORCED_MIN_SPEED < 1024) {
/*  95 */           SpeedManagerAlgorithmProviderV3.access$702(1024);
/*     */         }
/*     */         try
/*     */         {
/*  99 */           SpeedManagerAlgorithmProviderV3.access$802(Float.parseFloat(str));
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int PING_AVERAGE_HISTORY_COUNT = 5;
/*     */   
/*     */ 
/*     */   private static final int IDLE_UPLOAD_SPEED = 5120;
/*     */   
/*     */ 
/*     */   private static final int INITIAL_IDLE_AVERAGE = 100;
/*     */   
/*     */ 
/*     */   private static final int MIN_IDLE_AVERAGE = 50;
/*     */   
/*     */ 
/*     */   private static final int INCREASING = 1;
/*     */   
/*     */ 
/*     */   private static final int DECREASING = 2;
/*     */   
/*     */ 
/*     */   private final SpeedManagerAlgorithmProviderAdapter adapter;
/*     */   
/*     */   private final NeuralSpeedLimiter limiter;
/*     */   
/* 130 */   private final Average upload_average = AverageFactory.MovingImmediateAverage(5);
/* 131 */   private final Average upload_short_average = AverageFactory.MovingImmediateAverage(2);
/* 132 */   private final Average upload_short_prot_average = AverageFactory.MovingImmediateAverage(2);
/*     */   
/* 134 */   private final Average ping_average_history = AverageFactory.MovingImmediateAverage(5);
/*     */   
/* 136 */   private final Average choke_speed_average = AverageFactory.MovingImmediateAverage(3);
/*     */   
/*     */   private Map ping_sources;
/*     */   
/*     */   private volatile int replacement_contacts;
/*     */   
/*     */   private int mode;
/*     */   
/*     */   private volatile int mode_ticks;
/*     */   
/*     */   private int saved_limit;
/*     */   
/*     */   private int direction;
/*     */   
/*     */   private int ticks;
/*     */   private int idle_ticks;
/*     */   private int idle_average;
/*     */   private boolean idle_average_set;
/*     */   private int max_ping;
/*     */   private int max_upload_average;
/*     */   
/*     */   public SpeedManagerAlgorithmProviderV3(SpeedManagerAlgorithmProviderAdapter _adapter)
/*     */   {
/* 159 */     this.adapter = _adapter;
/* 160 */     this.limiter = new NeuralSpeedLimiter();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateStats()
/*     */   {
/* 171 */     int current_protocol_speed = this.adapter.getCurrentProtocolUploadSpeed();
/* 172 */     int current_data_speed = this.adapter.getCurrentDataUploadSpeed();
/*     */     
/* 174 */     int current_speed = current_protocol_speed + current_data_speed;
/*     */     
/* 176 */     this.upload_average.update(current_speed);
/*     */     
/* 178 */     this.upload_short_average.update(current_speed);
/*     */     
/* 180 */     this.upload_short_prot_average.update(current_protocol_speed);
/*     */     
/* 182 */     this.mode_ticks += 1;
/*     */     
/* 184 */     this.ticks += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/* 190 */     this.ticks = 0;
/* 191 */     this.mode = 0;
/* 192 */     this.mode_ticks = 0;
/* 193 */     this.idle_ticks = 0;
/* 194 */     this.idle_average = 100;
/* 195 */     this.idle_average_set = false;
/* 196 */     this.max_upload_average = 0;
/* 197 */     this.direction = 1;
/* 198 */     this.max_ping = 0;
/* 199 */     this.replacement_contacts = 0;
/*     */     
/* 201 */     this.ping_sources = new HashMap();
/*     */     
/* 203 */     this.choke_speed_average.reset();
/* 204 */     this.upload_average.reset();
/* 205 */     this.upload_short_average.reset();
/* 206 */     this.upload_short_prot_average.reset();
/* 207 */     this.ping_average_history.reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement)
/*     */   {
/* 215 */     if (is_replacement)
/*     */     {
/* 217 */       this.replacement_contacts += 1;
/*     */     }
/*     */     
/* 220 */     synchronized (this.ping_sources)
/*     */     {
/* 222 */       this.ping_sources.put(source, new pingSource(source));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void pingSourceFailed(SpeedManagerPingSource source)
/*     */   {
/* 230 */     synchronized (this.ping_sources)
/*     */     {
/* 232 */       this.ping_sources.remove(source);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void calculate(SpeedManagerPingSource[] sources)
/*     */   {
/* 240 */     int min_rtt = Integer.MAX_VALUE;
/*     */     
/* 242 */     for (int i = 0; i < sources.length; i++)
/*     */     {
/* 244 */       int rtt = sources[i].getPingTime();
/*     */       
/* 246 */       if ((rtt >= 0) && (rtt < min_rtt))
/*     */       {
/* 248 */         min_rtt = rtt;
/*     */       }
/*     */     }
/*     */     
/* 252 */     String str = "";
/*     */     
/* 254 */     int ping_total = 0;
/* 255 */     int ping_count = 0;
/*     */     
/* 257 */     for (int i = 0; i < sources.length; i++)
/*     */     {
/*     */       pingSource ps;
/*     */       
/* 261 */       synchronized (this.ping_sources)
/*     */       {
/* 263 */         ps = (pingSource)this.ping_sources.get(sources[i]);
/*     */       }
/*     */       
/* 266 */       int rtt = sources[i].getPingTime();
/*     */       
/* 268 */       str = str + (i == 0 ? "" : ",") + rtt;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 273 */       if (ps != null)
/*     */       {
/* 275 */         boolean good_ping = rtt < 5 * Math.max(min_rtt, 75);
/*     */         
/* 277 */         ps.pingReceived(rtt, good_ping);
/*     */         
/* 279 */         if (!good_ping)
/*     */         {
/* 281 */           rtt = -1;
/*     */         }
/*     */       }
/*     */       
/* 285 */       if (rtt != -1)
/*     */       {
/* 287 */         ping_total += rtt;
/*     */         
/* 289 */         ping_count++;
/*     */       }
/*     */     }
/*     */     
/* 293 */     if (ping_count == 0)
/*     */     {
/*     */ 
/*     */ 
/* 297 */       return;
/*     */     }
/*     */     
/* 300 */     int ping_average = ping_total / ping_count;
/*     */     
/*     */ 
/*     */ 
/* 304 */     ping_average = (ping_average + min_rtt) / 2;
/*     */     
/* 306 */     int running_average = (int)this.ping_average_history.update(ping_average);
/*     */     
/* 308 */     if (ping_average > this.max_ping)
/*     */     {
/* 310 */       this.max_ping = ping_average;
/*     */     }
/*     */     
/* 313 */     int up_average = (int)this.upload_average.getAverage();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 318 */     if ((up_average <= 5120) || ((running_average < this.idle_average) && (!this.idle_average_set)))
/*     */     {
/* 320 */       this.idle_ticks += 1;
/*     */       
/* 322 */       if (this.idle_ticks >= 5)
/*     */       {
/* 324 */         this.idle_average = Math.max(running_average, 50);
/*     */         
/* 326 */         log("New idle average: " + this.idle_average);
/*     */         
/* 328 */         this.idle_average_set = true;
/*     */       }
/*     */     }
/*     */     else {
/* 332 */       if (up_average > this.max_upload_average)
/*     */       {
/* 334 */         this.max_upload_average = up_average;
/*     */         
/* 336 */         log("New max upload:" + this.max_upload_average);
/*     */       }
/*     */       
/* 339 */       this.idle_ticks = 0;
/*     */     }
/*     */     
/*     */ 
/* 343 */     if ((this.idle_average_set) && (running_average < this.idle_average))
/*     */     {
/*     */ 
/*     */ 
/* 347 */       this.idle_average = Math.max(running_average, 50);
/*     */     }
/*     */     
/* 350 */     int current_speed = this.adapter.getCurrentDataUploadSpeed() + this.adapter.getCurrentProtocolUploadSpeed();
/* 351 */     int current_limit = this.adapter.getCurrentUploadLimit();
/*     */     
/* 353 */     int new_limit = current_limit;
/*     */     
/* 355 */     log("Pings: " + str + ", average=" + ping_average + ", running_average=" + running_average + ",idle_average=" + this.idle_average + ", speed=" + current_speed + ",limit=" + current_limit + ",choke = " + (int)this.choke_speed_average.getAverage());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 362 */     if (this.mode == 2)
/*     */     {
/* 364 */       if (this.mode_ticks > 30)
/*     */       {
/* 366 */         this.mode = 0;
/*     */         
/* 368 */         current_limit = new_limit = this.saved_limit;
/*     */       }
/*     */     }
/* 371 */     else if (this.mode == 1)
/*     */     {
/* 373 */       if ((this.idle_average_set) || (this.mode_ticks > 60))
/*     */       {
/* 375 */         log("Mode -> running");
/*     */         
/* 377 */         if (!this.idle_average_set)
/*     */         {
/* 379 */           this.idle_average = Math.max(running_average, 50);
/*     */           
/* 381 */           this.idle_average_set = true;
/*     */         }
/*     */         
/* 384 */         this.mode = 0;
/* 385 */         this.mode_ticks = 0;
/*     */         
/* 387 */         current_limit = new_limit = this.saved_limit;
/*     */       }
/* 389 */       else if (this.mode_ticks == 5)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 394 */         this.ping_average_history.reset();
/*     */       }
/*     */     }
/*     */     
/* 398 */     if (this.mode == 0)
/*     */     {
/* 400 */       if (((this.ticks > 60) && (!this.idle_average_set)) || ((this.replacement_contacts >= 2) && (this.idle_average_set)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 406 */         log("Mode -> forced min");
/*     */         
/* 408 */         this.mode = 1;
/* 409 */         this.mode_ticks = 0;
/* 410 */         this.saved_limit = current_limit;
/*     */         
/* 412 */         this.idle_average_set = false;
/* 413 */         this.idle_ticks = 0;
/* 414 */         this.replacement_contacts = 0;
/*     */         
/* 416 */         new_limit = FORCED_MIN_SPEED;
/*     */       }
/*     */       else
/*     */       {
/* 420 */         this.limiter.setDlSpeed(this.adapter.getCurrentDataDownloadSpeed());
/* 421 */         this.limiter.setUlSpeed(this.adapter.getCurrentDataUploadSpeed());
/* 422 */         this.limiter.setMaxDlSpeed(this.adapter.getSpeedManager().getEstimatedDownloadCapacityBytesPerSec().getBytesPerSec());
/* 423 */         this.limiter.setMaxUlSpeed(this.adapter.getSpeedManager().getEstimatedUploadCapacityBytesPerSec().getBytesPerSec());
/* 424 */         this.limiter.setLatency(ping_average);
/* 425 */         this.limiter.setMinLatency(this.idle_average);
/* 426 */         this.limiter.setMaxLatency(1500L);
/*     */         
/* 428 */         if (this.limiter.shouldLimitDownload()) {
/* 429 */           this.adapter.setCurrentDownloadLimit((int)this.limiter.getDownloadLimit());
/*     */         } else {
/* 431 */           this.adapter.setCurrentDownloadLimit(0);
/*     */         }
/*     */         
/* 434 */         if (this.limiter.shouldLimitUpload()) {
/* 435 */           this.adapter.setCurrentUploadLimit((int)this.limiter.getUploadLimit());
/*     */         } else {
/* 437 */           this.adapter.setCurrentUploadLimit(0);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIdlePingMillis()
/*     */   {
/* 446 */     return this.idle_average;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCurrentPingMillis()
/*     */   {
/* 452 */     return (int)this.ping_average_history.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxPingMillis()
/*     */   {
/* 458 */     return this.max_ping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCurrentChokeSpeed()
/*     */   {
/* 469 */     return (int)this.choke_speed_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxUploadSpeed()
/*     */   {
/* 475 */     return this.max_upload_average;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAdjustsDownloadLimits()
/*     */   {
/* 481 */     return ADJUST_DOWNLOAD_ENABLE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 488 */     this.adapter.log(str);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy() {}
/*     */   
/*     */ 
/*     */   protected static class pingSource
/*     */   {
/*     */     private final SpeedManagerPingSource source;
/*     */     private int last_good_ping;
/*     */     private int bad_pings;
/*     */     
/*     */     protected pingSource(SpeedManagerPingSource _source)
/*     */     {
/* 503 */       this.source = _source;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void pingReceived(int time, boolean good_ping)
/*     */     {
/* 511 */       if (good_ping)
/*     */       {
/* 513 */         this.bad_pings = 0;
/*     */         
/* 515 */         this.last_good_ping = time;
/*     */       }
/*     */       else
/*     */       {
/* 519 */         this.bad_pings += 1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 524 */       if (this.bad_pings == 3)
/*     */       {
/* 526 */         this.source.destroy();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v3/SpeedManagerAlgorithmProviderV3.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */