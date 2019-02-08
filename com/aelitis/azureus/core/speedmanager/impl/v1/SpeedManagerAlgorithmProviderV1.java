/*     */ package com.aelitis.azureus.core.speedmanager.impl.v1;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ public class SpeedManagerAlgorithmProviderV1
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
/*  59 */   private static final String[] CONFIG_PARAMS = { "AutoSpeed Min Upload KBs", "AutoSpeed Max Upload KBs", "AutoSpeed Max Increment KBs", "AutoSpeed Max Decrement KBs", "AutoSpeed Choking Ping Millis", "AutoSpeed Download Adj Enable", "AutoSpeed Download Adj Ratio", "AutoSpeed Latency Factor", "AutoSpeed Forced Min KBs" };
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
/*  70 */     COConfigurationManager.addAndFireParameterListeners(CONFIG_PARAMS, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*  78 */         SpeedManagerAlgorithmProviderV1.access$002(COConfigurationManager.getIntParameter("AutoSpeed Choking Ping Millis"));
/*  79 */         SpeedManagerAlgorithmProviderV1.access$102(COConfigurationManager.getIntParameter("AutoSpeed Min Upload KBs") * 1024);
/*  80 */         SpeedManagerAlgorithmProviderV1.access$202(COConfigurationManager.getIntParameter("AutoSpeed Max Upload KBs") * 1024);
/*  81 */         SpeedManagerAlgorithmProviderV1.access$302(COConfigurationManager.getIntParameter("AutoSpeed Max Increment KBs") * 1024);
/*  82 */         SpeedManagerAlgorithmProviderV1.access$402(COConfigurationManager.getIntParameter("AutoSpeed Max Decrement KBs") * 1024);
/*  83 */         SpeedManagerAlgorithmProviderV1.access$502(COConfigurationManager.getBooleanParameter("AutoSpeed Download Adj Enable"));
/*  84 */         String str = COConfigurationManager.getStringParameter("AutoSpeed Download Adj Ratio");
/*  85 */         SpeedManagerAlgorithmProviderV1.access$602(COConfigurationManager.getIntParameter("AutoSpeed Latency Factor"));
/*     */         
/*  87 */         if (SpeedManagerAlgorithmProviderV1.LATENCY_FACTOR < 1) {
/*  88 */           SpeedManagerAlgorithmProviderV1.access$602(1);
/*     */         }
/*     */         
/*  91 */         SpeedManagerAlgorithmProviderV1.access$702(COConfigurationManager.getIntParameter("AutoSpeed Forced Min KBs") * 1024);
/*     */         
/*  93 */         if (SpeedManagerAlgorithmProviderV1.FORCED_MIN_SPEED < 1024) {
/*  94 */           SpeedManagerAlgorithmProviderV1.access$702(1024);
/*     */         }
/*     */         try
/*     */         {
/*  98 */           SpeedManagerAlgorithmProviderV1.access$802(Float.parseFloat(str));
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
/*     */ 
/* 128 */   private final Average upload_average = AverageFactory.MovingImmediateAverage(5);
/* 129 */   private final Average upload_short_average = AverageFactory.MovingImmediateAverage(2);
/* 130 */   private final Average upload_short_prot_average = AverageFactory.MovingImmediateAverage(2);
/*     */   
/* 132 */   private final Average ping_average_history = AverageFactory.MovingImmediateAverage(5);
/*     */   
/* 134 */   private final Average choke_speed_average = AverageFactory.MovingImmediateAverage(3);
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
/*     */   public SpeedManagerAlgorithmProviderV1(SpeedManagerAlgorithmProviderAdapter _adapter)
/*     */   {
/* 157 */     this.adapter = _adapter;
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
/* 168 */     int current_protocol_speed = this.adapter.getCurrentProtocolUploadSpeed();
/* 169 */     int current_data_speed = this.adapter.getCurrentDataUploadSpeed();
/*     */     
/* 171 */     int current_speed = current_protocol_speed + current_data_speed;
/*     */     
/* 173 */     this.upload_average.update(current_speed);
/*     */     
/* 175 */     this.upload_short_average.update(current_speed);
/*     */     
/* 177 */     this.upload_short_prot_average.update(current_protocol_speed);
/*     */     
/* 179 */     this.mode_ticks += 1;
/*     */     
/* 181 */     this.ticks += 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void reset()
/*     */   {
/* 187 */     this.ticks = 0;
/* 188 */     this.mode = 0;
/* 189 */     this.mode_ticks = 0;
/* 190 */     this.idle_ticks = 0;
/* 191 */     this.idle_average = 100;
/* 192 */     this.idle_average_set = false;
/* 193 */     this.max_upload_average = 0;
/* 194 */     this.direction = 1;
/* 195 */     this.max_ping = 0;
/* 196 */     this.replacement_contacts = 0;
/*     */     
/* 198 */     this.ping_sources = new HashMap();
/*     */     
/* 200 */     this.choke_speed_average.reset();
/* 201 */     this.upload_average.reset();
/* 202 */     this.upload_short_average.reset();
/* 203 */     this.upload_short_prot_average.reset();
/* 204 */     this.ping_average_history.reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement)
/*     */   {
/* 212 */     if (is_replacement)
/*     */     {
/* 214 */       this.replacement_contacts += 1;
/*     */     }
/*     */     
/* 217 */     synchronized (this.ping_sources)
/*     */     {
/* 219 */       this.ping_sources.put(source, new pingSource(source));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void pingSourceFailed(SpeedManagerPingSource source)
/*     */   {
/* 227 */     synchronized (this.ping_sources)
/*     */     {
/* 229 */       this.ping_sources.remove(source);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void calculate(SpeedManagerPingSource[] sources)
/*     */   {
/* 237 */     int min_rtt = Integer.MAX_VALUE;
/*     */     
/* 239 */     for (int i = 0; i < sources.length; i++)
/*     */     {
/* 241 */       int rtt = sources[i].getPingTime();
/*     */       
/* 243 */       if ((rtt >= 0) && (rtt < min_rtt))
/*     */       {
/* 245 */         min_rtt = rtt;
/*     */       }
/*     */     }
/*     */     
/* 249 */     String str = "";
/*     */     
/* 251 */     int ping_total = 0;
/* 252 */     int ping_count = 0;
/*     */     
/* 254 */     for (int i = 0; i < sources.length; i++)
/*     */     {
/*     */       pingSource ps;
/*     */       
/* 258 */       synchronized (this.ping_sources)
/*     */       {
/* 260 */         ps = (pingSource)this.ping_sources.get(sources[i]);
/*     */       }
/*     */       
/* 263 */       int rtt = sources[i].getPingTime();
/*     */       
/* 265 */       str = str + (i == 0 ? "" : ",") + rtt;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 270 */       if (ps != null)
/*     */       {
/* 272 */         boolean good_ping = rtt < 5 * Math.max(min_rtt, 75);
/*     */         
/* 274 */         ps.pingReceived(rtt, good_ping);
/*     */         
/* 276 */         if (!good_ping)
/*     */         {
/* 278 */           rtt = -1;
/*     */         }
/*     */       }
/*     */       
/* 282 */       if (rtt != -1)
/*     */       {
/* 284 */         ping_total += rtt;
/*     */         
/* 286 */         ping_count++;
/*     */       }
/*     */     }
/*     */     
/* 290 */     if (ping_count == 0)
/*     */     {
/*     */ 
/*     */ 
/* 294 */       return;
/*     */     }
/*     */     
/* 297 */     int ping_average = ping_total / ping_count;
/*     */     
/*     */ 
/*     */ 
/* 301 */     ping_average = (ping_average + min_rtt) / 2;
/*     */     
/* 303 */     int running_average = (int)this.ping_average_history.update(ping_average);
/*     */     
/* 305 */     if (ping_average > this.max_ping)
/*     */     {
/* 307 */       this.max_ping = ping_average;
/*     */     }
/*     */     
/* 310 */     int up_average = (int)this.upload_average.getAverage();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 315 */     if ((up_average <= 5120) || ((running_average < this.idle_average) && (!this.idle_average_set)))
/*     */     {
/* 317 */       this.idle_ticks += 1;
/*     */       
/* 319 */       if (this.idle_ticks >= 5)
/*     */       {
/* 321 */         this.idle_average = Math.max(running_average, 50);
/*     */         
/* 323 */         log("New idle average: " + this.idle_average);
/*     */         
/* 325 */         this.idle_average_set = true;
/*     */       }
/*     */     }
/*     */     else {
/* 329 */       if (up_average > this.max_upload_average)
/*     */       {
/* 331 */         this.max_upload_average = up_average;
/*     */         
/* 333 */         log("New max upload:" + this.max_upload_average);
/*     */       }
/*     */       
/* 336 */       this.idle_ticks = 0;
/*     */     }
/*     */     
/*     */ 
/* 340 */     if ((this.idle_average_set) && (running_average < this.idle_average))
/*     */     {
/*     */ 
/*     */ 
/* 344 */       this.idle_average = Math.max(running_average, 50);
/*     */     }
/*     */     
/* 347 */     int current_speed = this.adapter.getCurrentDataUploadSpeed() + this.adapter.getCurrentProtocolUploadSpeed();
/* 348 */     int current_limit = this.adapter.getCurrentUploadLimit();
/*     */     
/* 350 */     int new_limit = current_limit;
/*     */     
/* 352 */     log("Pings: " + str + ", average=" + ping_average + ", running_average=" + running_average + ",idle_average=" + this.idle_average + ", speed=" + current_speed + ",limit=" + current_limit + ",choke = " + (int)this.choke_speed_average.getAverage());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 359 */     if (this.mode == 2)
/*     */     {
/* 361 */       if (this.mode_ticks > 30)
/*     */       {
/* 363 */         this.mode = 0;
/*     */         
/* 365 */         current_limit = new_limit = this.saved_limit;
/*     */       }
/*     */     }
/* 368 */     else if (this.mode == 1)
/*     */     {
/* 370 */       if ((this.idle_average_set) || (this.mode_ticks > 60))
/*     */       {
/* 372 */         log("Mode -> running");
/*     */         
/* 374 */         if (!this.idle_average_set)
/*     */         {
/* 376 */           this.idle_average = Math.max(running_average, 50);
/*     */           
/* 378 */           this.idle_average_set = true;
/*     */         }
/*     */         
/* 381 */         this.mode = 0;
/* 382 */         this.mode_ticks = 0;
/*     */         
/* 384 */         current_limit = new_limit = this.saved_limit;
/*     */       }
/* 386 */       else if (this.mode_ticks == 5)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 391 */         this.ping_average_history.reset();
/*     */       }
/*     */     }
/*     */     
/* 395 */     if (this.mode == 0)
/*     */     {
/* 397 */       if (((this.ticks > 60) && (!this.idle_average_set)) || ((this.replacement_contacts >= 2) && (this.idle_average_set)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 403 */         log("Mode -> forced min");
/*     */         
/* 405 */         this.mode = 1;
/* 406 */         this.mode_ticks = 0;
/* 407 */         this.saved_limit = current_limit;
/*     */         
/* 409 */         this.idle_average_set = false;
/* 410 */         this.idle_ticks = 0;
/* 411 */         this.replacement_contacts = 0;
/*     */         
/* 413 */         new_limit = FORCED_MIN_SPEED;
/*     */       }
/*     */       else
/*     */       {
/* 417 */         int short_up = (int)this.upload_short_average.getAverage();
/*     */         
/* 419 */         int choke_speed = (int)this.choke_speed_average.getAverage();
/*     */         
/* 421 */         int choke_time = PING_CHOKE_TIME;
/* 422 */         int latency_factor = LATENCY_FACTOR;
/*     */         
/*     */ 
/* 425 */         if ((running_average < 2 * this.idle_average) && (ping_average < choke_time))
/*     */         {
/* 427 */           this.direction = 1;
/*     */           
/* 429 */           int diff = running_average - this.idle_average;
/*     */           
/* 431 */           if (diff < 100)
/*     */           {
/* 433 */             diff = 100;
/*     */           }
/*     */           
/* 436 */           int increment = 1024 * (diff / latency_factor);
/*     */           
/*     */ 
/*     */ 
/* 440 */           int max_inc = MAX_INCREMENT;
/*     */           
/* 442 */           if (new_limit + 2048 > choke_speed)
/*     */           {
/* 444 */             max_inc = 1024;
/*     */           }
/* 446 */           else if (new_limit + 5120 > choke_speed)
/*     */           {
/* 448 */             max_inc += 3072;
/*     */           }
/*     */           
/* 451 */           new_limit += Math.min(increment, max_inc);
/*     */         }
/* 453 */         else if ((ping_average > 4 * this.idle_average) || (ping_average > choke_time))
/*     */         {
/* 455 */           if (this.direction == 1)
/*     */           {
/* 457 */             if (this.idle_average_set)
/*     */             {
/* 459 */               this.choke_speed_average.update(short_up);
/*     */             }
/*     */           }
/*     */           
/* 463 */           this.direction = 2;
/*     */           
/* 465 */           int decrement = 1024 * ((ping_average - 3 * this.idle_average) / latency_factor);
/*     */           
/* 467 */           new_limit -= Math.min(decrement, MAX_DECREMENT);
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 473 */           if (new_limit < this.upload_short_prot_average.getAverage() + 1024.0D)
/*     */           {
/* 475 */             new_limit = (int)this.upload_short_prot_average.getAverage() + 1024;
/*     */           }
/*     */         }
/*     */         
/* 479 */         if (new_limit < 1024)
/*     */         {
/* 481 */           new_limit = 1024;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 487 */       int min_up = MIN_UP;
/* 488 */       int max_up = MAX_UP;
/*     */       
/* 490 */       if ((min_up > 0) && (new_limit < min_up) && (this.mode != 1))
/*     */       {
/* 492 */         new_limit = min_up;
/*     */       }
/* 494 */       else if ((max_up > 0) && (new_limit > max_up) && (this.mode != 2))
/*     */       {
/* 496 */         new_limit = max_up;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 502 */       if ((new_limit > current_limit) && (current_speed < current_limit - 10240))
/*     */       {
/* 504 */         new_limit = current_limit;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 511 */     new_limit = (new_limit + 1023) / 1024 * 1024;
/*     */     
/* 513 */     this.adapter.setCurrentUploadLimit(new_limit);
/*     */     
/* 515 */     if ((ADJUST_DOWNLOAD_ENABLE) && (!Float.isInfinite(ADJUST_DOWNLOAD_RATIO)) && (!Float.isNaN(ADJUST_DOWNLOAD_RATIO)))
/*     */     {
/* 517 */       int dl_limit = (int)(new_limit * ADJUST_DOWNLOAD_RATIO);
/*     */       
/* 519 */       this.adapter.setCurrentDownloadLimit(dl_limit);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIdlePingMillis()
/*     */   {
/* 526 */     return this.idle_average;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCurrentPingMillis()
/*     */   {
/* 532 */     return (int)this.ping_average_history.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxPingMillis()
/*     */   {
/* 538 */     return this.max_ping;
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
/* 549 */     return (int)this.choke_speed_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMaxUploadSpeed()
/*     */   {
/* 555 */     return this.max_upload_average;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getAdjustsDownloadLimits()
/*     */   {
/* 561 */     return ADJUST_DOWNLOAD_ENABLE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 568 */     this.adapter.log(str);
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
/* 583 */       this.source = _source;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void pingReceived(int time, boolean good_ping)
/*     */     {
/* 591 */       if (good_ping)
/*     */       {
/* 593 */         this.bad_pings = 0;
/*     */         
/* 595 */         this.last_good_ping = time;
/*     */       }
/*     */       else
/*     */       {
/* 599 */         this.bad_pings += 1;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 604 */       if (this.bad_pings == 3)
/*     */       {
/* 606 */         this.source.destroy();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v1/SpeedManagerAlgorithmProviderV1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */