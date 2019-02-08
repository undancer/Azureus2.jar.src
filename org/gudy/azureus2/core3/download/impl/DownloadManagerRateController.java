/*     */ package org.gudy.azureus2.core3.download.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerStats;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
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
/*     */ public class DownloadManagerRateController
/*     */ {
/*     */   private static AzureusCore core;
/*     */   private static SpeedManager speed_manager;
/*  53 */   static final Map<PEPeerManager, PMState> pm_map = new HashMap();
/*     */   
/*     */   private static TimerEventPeriodic timer;
/*     */   
/*  57 */   static final AsyncDispatcher dispatcher = new AsyncDispatcher("DMCRateController");
/*     */   private static boolean enable;
/*     */   private static boolean enable_limit_handling;
/*     */   private static int slack_bytes_per_sec;
/*     */   
/*     */   static
/*     */   {
/*  64 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Bias Upload Enable", "Bias Upload Handle No Limit", "Bias Upload Slack KBs" }, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String parameterName)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */         DownloadManagerRateController.access$002(COConfigurationManager.getBooleanParameter("Bias Upload Enable"));
/*  77 */         DownloadManagerRateController.access$102((COConfigurationManager.getBooleanParameter("Bias Upload Handle No Limit")) && (DownloadManagerRateController.enable));
/*  78 */         DownloadManagerRateController.access$202(COConfigurationManager.getIntParameter("Bias Upload Slack KBs") * 1024);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*  84 */   private static volatile int rate_limit = 0;
/*     */   
/*     */ 
/*  87 */   static final LimitedRateGroup limiter = new LimitedRateGroup()
/*     */   {
/*     */ 
/*     */     public String getName()
/*     */     {
/*     */ 
/*  93 */       return "DMRC";
/*     */     }
/*     */     
/*     */ 
/*     */     public int getRateLimitBytesPerSecond()
/*     */     {
/*  99 */       return DownloadManagerRateController.rate_limit;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDisabled()
/*     */     {
/* 105 */       return DownloadManagerRateController.rate_limit == -1;
/*     */     }
/*     */     
/*     */ 
/*     */     public void updateBytesUsed(int used) {}
/*     */   };
/*     */   
/*     */   private static final int TIMER_MILLIS = 1000;
/*     */   
/*     */   private static final int WAIT_AFTER_CHOKE_PERIOD = 10000;
/*     */   
/*     */   private static final int WAIT_AFTER_CHOKE_TICKS = 10;
/*     */   
/*     */   private static final int DEFAULT_UP_LIMIT = 256000;
/*     */   
/*     */   private static final int MAX_UP_DIFF = 15360;
/*     */   
/*     */   private static final int MAX_DOWN_DIFF = 10240;
/*     */   
/*     */   private static final int MIN_DIFF = 2048;
/*     */   
/*     */   private static final int SAMPLE_COUNT = 5;
/*     */   
/*     */   private static int sample_num;
/*     */   
/*     */   private static double incomplete_samples;
/*     */   
/*     */   private static double complete_samples;
/*     */   
/*     */   private static int ticks_to_sample_start;
/*     */   private static int last_rate_limit;
/*     */   private static double last_incomplete_average;
/*     */   private static double last_complete_average;
/*     */   private static double last_overall_average;
/* 139 */   private static int tick_count = 0;
/* 140 */   private static int last_tick_processed = -1;
/*     */   
/*     */   private static long pm_last_bad_limit;
/*     */   
/*     */   private static int latest_choke;
/*     */   private static int wait_until_tick;
/*     */   
/*     */   public static String getString()
/*     */   {
/* 149 */     if (enable)
/*     */     {
/* 151 */       String str = "reserved=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(slack_bytes_per_sec);
/*     */       
/* 153 */       if (enable_limit_handling)
/*     */       {
/* 155 */         str = str + ", limit=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(rate_limit);
/*     */         
/* 157 */         str = str + ", last[choke=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(latest_choke) + ", ratio=" + DisplayFormatters.formatDecimal(last_incomplete_average / last_complete_average, 2) + "]";
/*     */         
/*     */ 
/* 160 */         return str;
/*     */       }
/*     */       
/*     */ 
/* 164 */       return str;
/*     */     }
/*     */     
/*     */ 
/* 168 */     return "Disabled";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addPeerManager(PEPeerManager pm)
/*     */   {
/* 176 */     dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 182 */         if (DownloadManagerRateController.core == null)
/*     */         {
/* 184 */           DownloadManagerRateController.access$402(AzureusCoreFactory.getSingleton());
/*     */           
/* 186 */           DownloadManagerRateController.access$502(DownloadManagerRateController.core.getSpeedManager());
/*     */         }
/*     */         
/* 189 */         boolean is_complete = !this.val$pm.hasDownloadablePiece();
/*     */         
/* 191 */         PEPeerManagerStats pm_stats = this.val$pm.getStats();
/*     */         
/* 193 */         long up_bytes = pm_stats.getTotalDataBytesSentNoLan() + pm_stats.getTotalProtocolBytesSentNoLan();
/*     */         
/* 195 */         if (is_complete)
/*     */         {
/* 197 */           this.val$pm.addRateLimiter(DownloadManagerRateController.limiter, true);
/*     */         }
/*     */         
/* 200 */         DownloadManagerRateController.pm_map.put(this.val$pm, new DownloadManagerRateController.PMState(this.val$pm, is_complete, up_bytes, null));
/*     */         
/* 202 */         if (DownloadManagerRateController.timer == null)
/*     */         {
/* 204 */           DownloadManagerRateController.access$702(SimpleTimer.addPeriodicEvent("DMRC", 1000L, new TimerEventPerformer()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public void perform(TimerEvent event)
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 214 */               DownloadManagerRateController.dispatcher.dispatch(new AERunnable()
/*     */               {
/*     */                 public void runSupport() {}
/*     */               });
/*     */             }
/*     */           }));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void removePeerManager(PEPeerManager pm)
/*     */   {
/* 234 */     dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 240 */         DownloadManagerRateController.pm_map.remove(this.val$pm);
/*     */         
/* 242 */         if (DownloadManagerRateController.pm_map.size() == 0)
/*     */         {
/* 244 */           DownloadManagerRateController.timer.cancel();
/*     */           
/* 246 */           DownloadManagerRateController.access$702(null);
/*     */           
/* 248 */           DownloadManagerRateController.access$302(0);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private static void update()
/*     */   {
/* 257 */     tick_count += 1;
/*     */     
/* 259 */     if ((!enable_limit_handling) || (pm_map.size() == 0) || (NetworkManager.isSeedingOnlyUploadRate()) || (NetworkManager.getMaxUploadRateBPSNormal() != 0) || (core == null) || (speed_manager == null) || (speed_manager.getSpeedTester() == null))
/*     */     {
/*     */ 
/*     */ 
/* 263 */       rate_limit = 0;
/*     */       
/* 265 */       return;
/*     */     }
/*     */     
/* 268 */     int num_complete = 0;
/* 269 */     int num_incomplete = 0;
/*     */     
/* 271 */     int num_interesting = 0;
/*     */     
/* 273 */     int i_up_total = 0;
/* 274 */     int c_up_total = 0;
/*     */     
/* 276 */     long mono_now = SystemTime.getMonotonousTime();
/*     */     
/* 278 */     for (Map.Entry<PEPeerManager, PMState> entry : pm_map.entrySet())
/*     */     {
/* 280 */       PEPeerManager pm = (PEPeerManager)entry.getKey();
/* 281 */       PMState state = (PMState)entry.getValue();
/*     */       
/* 283 */       boolean is_complete = !pm.hasDownloadablePiece();
/*     */       
/* 285 */       PEPeerManagerStats pm_stats = pm.getStats();
/*     */       
/* 287 */       long up_bytes = pm_stats.getTotalDataBytesSentNoLan() + pm_stats.getTotalProtocolBytesSentNoLan();
/*     */       
/* 289 */       long diff = state.setBytesUp(up_bytes);
/*     */       
/* 291 */       if (is_complete)
/*     */       {
/* 293 */         num_complete++;
/*     */         
/* 295 */         c_up_total = (int)(c_up_total + diff);
/*     */       }
/*     */       else
/*     */       {
/* 299 */         num_incomplete++;
/*     */         
/* 301 */         i_up_total = (int)(i_up_total + diff);
/*     */         
/* 303 */         if (state.isInteresting(mono_now))
/*     */         {
/* 305 */           num_interesting++;
/*     */         }
/*     */       }
/*     */       
/* 309 */       if (state.isComplete() != is_complete)
/*     */       {
/* 311 */         if (is_complete)
/*     */         {
/* 313 */           pm.addRateLimiter(limiter, true);
/*     */         }
/*     */         else
/*     */         {
/* 317 */           pm.removeRateLimiter(limiter, true);
/*     */         }
/*     */         
/* 320 */         state.setComplete(is_complete);
/*     */       }
/*     */     }
/*     */     
/* 324 */     if ((num_incomplete == 0) || (num_complete == 0) || (num_interesting == 0))
/*     */     {
/* 326 */       rate_limit = 0;
/*     */       
/* 328 */       return;
/*     */     }
/*     */     
/* 331 */     boolean skipped_tick = false;
/*     */     
/* 333 */     if (last_tick_processed != tick_count - 1)
/*     */     {
/* 335 */       pm_last_bad_limit = 0L;
/* 336 */       latest_choke = 0;
/* 337 */       wait_until_tick = 0;
/*     */       
/* 339 */       ticks_to_sample_start = 0;
/* 340 */       sample_num = 0;
/* 341 */       incomplete_samples = 0.0D;
/* 342 */       complete_samples = 0.0D;
/*     */       
/* 344 */       skipped_tick = true;
/*     */     }
/*     */     
/* 347 */     last_tick_processed = tick_count;
/*     */     
/* 349 */     if ((skipped_tick) || (tick_count < wait_until_tick))
/*     */     {
/* 351 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 355 */       long real_now = SystemTime.getCurrentTime();
/*     */       
/* 357 */       SpeedManagerPingMapper mapper = speed_manager.getActiveMapper();
/*     */       
/* 359 */       if (rate_limit == 0)
/*     */       {
/* 361 */         rate_limit = speed_manager.getEstimatedUploadCapacityBytesPerSec().getBytesPerSec();
/*     */         
/* 363 */         if (rate_limit == 0)
/*     */         {
/* 365 */           rate_limit = 256000;
/*     */         }
/*     */       }
/*     */       
/* 369 */       SpeedManagerLimitEstimate last_bad = mapper.getLastBadUploadLimit();
/*     */       
/* 371 */       if (last_bad != null)
/*     */       {
/* 373 */         int last_bad_limit = last_bad.getBytesPerSec();
/*     */         
/* 375 */         if (last_bad_limit != pm_last_bad_limit)
/*     */         {
/* 377 */           pm_last_bad_limit = last_bad_limit;
/*     */           
/* 379 */           SpeedManagerLimitEstimate[] bad_ups = mapper.getBadUploadHistory();
/*     */           
/* 381 */           int total = last_bad.getBytesPerSec();
/* 382 */           int count = 1;
/*     */           
/* 384 */           for (SpeedManagerLimitEstimate bad : bad_ups)
/*     */           {
/* 386 */             long t = bad.getWhen();
/*     */             
/* 388 */             if ((real_now - t <= 30000L) && (bad.getBytesPerSec() != last_bad_limit))
/*     */             {
/* 390 */               total += bad.getBytesPerSec();
/*     */               
/* 392 */               count++;
/*     */             }
/*     */           }
/*     */           
/* 396 */           latest_choke = total / count;
/*     */           
/*     */           int new_rate_limit;
/*     */           int new_rate_limit;
/* 400 */           if (rate_limit == 0)
/*     */           {
/* 402 */             new_rate_limit = latest_choke / 2;
/*     */           }
/*     */           else
/*     */           {
/* 406 */             new_rate_limit = rate_limit / 2;
/*     */           }
/*     */           
/* 409 */           if (new_rate_limit < slack_bytes_per_sec)
/*     */           {
/* 411 */             new_rate_limit = slack_bytes_per_sec;
/*     */           }
/*     */           
/* 414 */           rate_limit = new_rate_limit;
/*     */           
/* 416 */           wait_until_tick = tick_count + 10;
/*     */           
/* 418 */           ticks_to_sample_start = 0;
/* 419 */           sample_num = 0;
/* 420 */           complete_samples = 0.0D;
/* 421 */           incomplete_samples = 0.0D;
/* 422 */           last_rate_limit = 0;
/*     */           
/* 424 */           return;
/*     */         }
/*     */       }
/*     */       
/* 428 */       if (ticks_to_sample_start > 0)
/*     */       {
/* 430 */         ticks_to_sample_start -= 1;
/*     */       }
/* 432 */       else if (sample_num < 5)
/*     */       {
/* 434 */         complete_samples += c_up_total;
/* 435 */         incomplete_samples += i_up_total;
/*     */         
/* 437 */         sample_num += 1;
/*     */       }
/*     */       else
/*     */       {
/* 441 */         double incomplete_average = incomplete_samples / 5.0D;
/* 442 */         double complete_average = complete_samples / 5.0D;
/* 443 */         double overall_average = (complete_samples + incomplete_samples) / 5.0D;
/*     */         
/* 445 */         int action = -1;
/*     */         
/*     */         try
/*     */         {
/* 449 */           if (last_rate_limit == 0)
/*     */           {
/* 451 */             action = 1;
/*     */           }
/*     */           else
/*     */           {
/* 455 */             double overall_change = overall_average - last_overall_average;
/*     */             
/* 457 */             if (overall_change < 0.0D)
/*     */             {
/* 459 */               if (rate_limit < last_rate_limit)
/*     */               {
/*     */ 
/*     */ 
/* 463 */                 action = 1;
/*     */               }
/*     */               else
/*     */               {
/* 467 */                 action = 0;
/*     */               }
/*     */             }
/*     */             else {
/* 471 */               double last_ratio = last_incomplete_average / last_complete_average;
/* 472 */               double ratio = incomplete_average / complete_average;
/*     */               
/*     */ 
/*     */ 
/* 476 */               if ((rate_limit < last_rate_limit) && (ratio >= last_ratio))
/*     */               {
/* 478 */                 action = -1;
/*     */               }
/* 480 */               else if ((rate_limit > last_rate_limit) && (ratio <= last_ratio))
/*     */               {
/* 482 */                 double i_up_change = incomplete_average - last_incomplete_average;
/*     */                 
/* 484 */                 if (i_up_change >= 1024.0D)
/*     */                 {
/* 486 */                   action = -1;
/*     */                 }
/*     */                 else
/*     */                 {
/* 490 */                   action = 1;
/*     */                 }
/*     */               }
/*     */               else
/*     */               {
/* 495 */                 action = 1;
/*     */               }
/*     */             } } } finally { int ceiling;
/*     */           int diff;
/*     */           int new_rate_limit;
/*     */           int diff;
/*     */           int new_rate_limit;
/*     */           int new_rate_limit;
/*     */           int new_rate_limit;
/* 504 */           if (action > 0)
/*     */           {
/* 506 */             int ceiling = latest_choke == 0 ? 256000 : latest_choke;
/*     */             
/* 508 */             int diff = (ceiling - rate_limit) / 4;
/*     */             
/* 510 */             if (diff > 15360)
/*     */             {
/* 512 */               diff = 15360;
/*     */             }
/* 514 */             else if (diff < 2048)
/*     */             {
/* 516 */               diff = 2048;
/*     */             }
/*     */             
/* 519 */             int new_rate_limit = rate_limit + diff;
/*     */             
/* 521 */             if (new_rate_limit > 104857600)
/*     */             {
/* 523 */               new_rate_limit = 104857600;
/*     */             }
/* 525 */           } else if (action < 0)
/*     */           {
/* 527 */             int diff = rate_limit / 5;
/*     */             
/* 529 */             if (diff > 10240)
/*     */             {
/* 531 */               diff = 10240;
/*     */             }
/* 533 */             else if (diff < 2048)
/*     */             {
/* 535 */               diff = 2048;
/*     */             }
/*     */             
/* 538 */             int new_rate_limit = rate_limit - diff;
/*     */             
/* 540 */             if (new_rate_limit < slack_bytes_per_sec)
/*     */             {
/* 542 */               new_rate_limit = slack_bytes_per_sec;
/*     */             }
/*     */           }
/*     */           else {
/* 546 */             new_rate_limit = rate_limit;
/*     */           }
/*     */           
/* 549 */           last_rate_limit = rate_limit;
/* 550 */           last_overall_average = overall_average;
/* 551 */           last_complete_average = complete_average;
/* 552 */           last_incomplete_average = incomplete_average;
/*     */           
/* 554 */           rate_limit = new_rate_limit;
/*     */           
/* 556 */           sample_num = 0;
/* 557 */           complete_samples = 0.0D;
/* 558 */           incomplete_samples = 0.0D;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class PMState
/*     */   {
/*     */     private final PEPeerManager manager;
/*     */     
/*     */ 
/*     */     private boolean complete;
/*     */     
/*     */ 
/*     */     private long bytes_up;
/*     */     
/*     */ 
/*     */     private boolean interesting;
/*     */     
/*     */     private long last_interesting_calc;
/*     */     
/*     */ 
/*     */     private PMState(PEPeerManager _manager, boolean _complete, long _bytes_up)
/*     */     {
/* 585 */       this.manager = _manager;
/* 586 */       this.complete = _complete;
/* 587 */       this.bytes_up = _bytes_up;
/*     */     }
/*     */     
/*     */ 
/*     */     private boolean isComplete()
/*     */     {
/* 593 */       return this.complete;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setComplete(boolean c)
/*     */     {
/* 600 */       this.complete = c;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private long setBytesUp(long b)
/*     */     {
/* 607 */       long diff = b - this.bytes_up;
/*     */       
/* 609 */       this.bytes_up = b;
/*     */       
/* 611 */       return diff;
/*     */     }
/*     */     
/*     */ 
/*     */     private boolean isInteresting(long now)
/*     */     {
/*     */       boolean calc;
/*     */       
/*     */       boolean calc;
/* 620 */       if (this.last_interesting_calc == 0L)
/*     */       {
/* 622 */         calc = true;
/*     */       } else { boolean calc;
/* 624 */         if (!this.interesting)
/*     */         {
/* 626 */           calc = now - this.last_interesting_calc >= 5000L;
/*     */         }
/*     */         else
/*     */         {
/* 630 */           calc = now - this.last_interesting_calc >= 60000L;
/*     */         }
/*     */       }
/* 633 */       if (calc)
/*     */       {
/* 635 */         this.last_interesting_calc = now;
/*     */         
/* 637 */         PEPeerManagerStats stats = this.manager.getStats();
/*     */         
/*     */ 
/*     */ 
/* 641 */         long dl_rate = stats.getDataReceiveRate();
/*     */         
/* 643 */         if (dl_rate < 5120L)
/*     */         {
/* 645 */           this.interesting = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/* 651 */         else if (this.manager.getNbPeersUnchoked() < 3)
/*     */         {
/* 653 */           this.interesting = false;
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 660 */           int limit = this.manager.getUploadRateLimitBytesPerSecond();
/*     */           
/* 662 */           if ((limit > 0) && (stats.getDataSendRate() + stats.getProtocolSendRate() >= limit - 5120))
/*     */           {
/*     */ 
/* 665 */             this.interesting = false;
/*     */           }
/*     */           else
/*     */           {
/* 669 */             this.interesting = true;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 675 */       return this.interesting;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerRateController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */