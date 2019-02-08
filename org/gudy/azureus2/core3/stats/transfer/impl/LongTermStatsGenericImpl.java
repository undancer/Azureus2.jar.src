/*      */ package org.gudy.azureus2.core3.stats.transfer.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.average.Average;
/*      */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*      */ import java.io.File;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.PrintWriter;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TimeZone;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.GenericStatsSource;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.RecordAccepter;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStatsListener;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class LongTermStatsGenericImpl
/*      */   implements LongTermStatsWrapper.LongTermStatsWrapperHelper
/*      */ {
/*      */   private static final int VERSION = 1;
/*      */   private static final long MIN_IN_MILLIS = 60000L;
/*      */   private static final long HOUR_IN_MILLIS = 3600000L;
/*      */   private static final long DAY_IN_MILLIS = 86400000L;
/*      */   private static final long WEEK_IN_MILLIS = 604800000L;
/*      */   public static final int RT_SESSION_START = 1;
/*      */   public static final int RT_SESSION_STATS = 2;
/*      */   public static final int RT_SESSION_END = 3;
/*      */   private final int STAT_ENTRY_COUNT;
/*      */   private final long[] st;
/*      */   private final long[] ss;
/*      */   private final long[] line_stats_prev;
/*      */   private final Average[] stat_averages;
/*      */   private boolean active;
/*      */   private boolean closing;
/*      */   private TimerEventPeriodic event;
/*      */   private PrintWriter writer;
/*      */   private String writer_rel_file;
/*      */   private DayCache day_cache;
/*      */   private static final int MONTH_CACHE_MAX = 3;
/*  101 */   private final Map<String, MonthCache> month_cache_map = new LinkedHashMap(3, 0.75F, true)
/*      */   {
/*      */ 
/*      */ 
/*      */     protected boolean removeEldestEntry(Map.Entry<String, LongTermStatsGenericImpl.MonthCache> eldest)
/*      */     {
/*      */ 
/*  108 */       return size() > 3;
/*      */     }
/*      */   };
/*      */   
/*  112 */   private static final SimpleDateFormat debug_utc_format = new SimpleDateFormat("yyyy,MM,dd:HH:mm");
/*  113 */   private static final SimpleDateFormat utc_date_format = new SimpleDateFormat("yyyy,MM,dd");
/*      */   private File stats_dir;
/*      */   
/*  116 */   static { debug_utc_format.setTimeZone(TimeZone.getTimeZone("UTC"));
/*  117 */     utc_date_format.setTimeZone(TimeZone.getTimeZone("UTC"));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private long session_total;
/*      */   
/*  124 */   private final CopyOnWriteList<Object[]> listeners = new CopyOnWriteList();
/*      */   
/*  126 */   private final AsyncDispatcher dispatcher = new AsyncDispatcher("lts", 5000);
/*      */   
/*  128 */   private int start_of_week = -1;
/*  129 */   private int start_of_month = -1;
/*      */   
/*      */ 
/*      */   private volatile boolean destroyed;
/*      */   
/*      */   private final String generic_id;
/*      */   
/*      */   private final LongTermStats.GenericStatsSource generic_source;
/*      */   
/*      */ 
/*      */   public LongTermStatsGenericImpl(String id, LongTermStats.GenericStatsSource source)
/*      */   {
/*  141 */     this.generic_id = id;
/*  142 */     this.generic_source = source;
/*      */     
/*  144 */     this.STAT_ENTRY_COUNT = source.getEntryCount();
/*      */     
/*  146 */     this.ss = new long[this.STAT_ENTRY_COUNT];
/*  147 */     this.st = new long[this.STAT_ENTRY_COUNT];
/*      */     
/*  149 */     this.line_stats_prev = new long[this.STAT_ENTRY_COUNT];
/*      */     
/*  151 */     this.stat_averages = new Average[this.STAT_ENTRY_COUNT];
/*      */     
/*  153 */     for (int i = 0; i < this.STAT_ENTRY_COUNT; i++)
/*      */     {
/*  155 */       this.stat_averages[i] = AverageFactory.MovingImmediateAverage(3);
/*      */     }
/*      */     
/*  158 */     this.stats_dir = FileUtil.getUserFile("stats");
/*      */     
/*  160 */     this.stats_dir = new File(this.stats_dir, "gen." + id);
/*      */     
/*  162 */     COConfigurationManager.addParameterListener("long.term.stats.enable", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  170 */         if (LongTermStatsGenericImpl.this.destroyed)
/*      */         {
/*  172 */           COConfigurationManager.removeParameterListener("long.term.stats.enable", this);
/*      */           
/*  174 */           return;
/*      */         }
/*      */         
/*  177 */         boolean enabled = COConfigurationManager.getBooleanParameter(name);
/*      */         
/*  179 */         synchronized (LongTermStatsGenericImpl.this)
/*      */         {
/*  181 */           if (enabled)
/*      */           {
/*  183 */             if (!LongTermStatsGenericImpl.this.active)
/*      */             {
/*  185 */               LongTermStatsGenericImpl.this.sessionStart();
/*      */             }
/*      */             
/*      */           }
/*  189 */           else if (LongTermStatsGenericImpl.this.active)
/*      */           {
/*  191 */             LongTermStatsGenericImpl.this.sessionEnd();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*  197 */     });
/*  198 */     sessionStart();
/*      */     
/*  200 */     AzureusCoreFactory.getSingleton().addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void stopped(AzureusCore core)
/*      */       {
/*      */ 
/*  207 */         if (LongTermStatsGenericImpl.this.destroyed)
/*      */         {
/*  209 */           core.removeLifecycleListener(this);
/*      */           
/*  211 */           return;
/*      */         }
/*      */         
/*  214 */         synchronized (LongTermStatsGenericImpl.this)
/*      */         {
/*  216 */           LongTermStatsGenericImpl.this.closing = true;
/*      */           
/*  218 */           if (LongTermStatsGenericImpl.this.active)
/*      */           {
/*  220 */             LongTermStatsGenericImpl.this.sessionEnd();
/*      */           }
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
/*      */   public void reset()
/*      */   {
/*  239 */     Debug.out("eh?");
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroyAndDeleteData()
/*      */   {
/*  245 */     synchronized (this)
/*      */     {
/*  247 */       this.destroyed = true;
/*      */       
/*  249 */       if (this.writer != null)
/*      */       {
/*  251 */         this.writer.close();
/*      */         
/*  253 */         this.writer = null;
/*      */       }
/*      */       
/*  256 */       for (int i = 0; i < 4; i++)
/*      */       {
/*  258 */         if (FileUtil.recursiveDeleteNoCheck(this.stats_dir))
/*      */         {
/*  260 */           return;
/*      */         }
/*      */         try
/*      */         {
/*  264 */           Thread.sleep(250L);
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/*  270 */       Debug.out("Failed to delete " + this.stats_dir);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void sessionStart()
/*      */   {
/*  277 */     synchronized (this)
/*      */     {
/*  279 */       if (this.closing)
/*      */       {
/*  281 */         return;
/*      */       }
/*      */       
/*  284 */       boolean enabled = COConfigurationManager.getBooleanParameter("long.term.stats.enable");
/*      */       
/*  286 */       if ((this.active) || (!enabled))
/*      */       {
/*  288 */         return;
/*      */       }
/*      */       
/*  291 */       this.active = true;
/*      */       
/*  293 */       long[] current = this.generic_source.getStats(this.generic_id);
/*      */       
/*  295 */       for (int i = 0; i < current.length; i++)
/*      */       {
/*  297 */         this.ss[i] = current[i];
/*      */         
/*  299 */         this.st[i] = this.ss[i];
/*      */       }
/*      */       
/*  302 */       write(1, this.st);
/*      */       
/*      */ 
/*  305 */       if (this.event == null)
/*      */       {
/*  307 */         this.event = SimpleTimer.addPeriodicEvent("LongTermStats:" + this.generic_id, 60000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*  316 */             if (LongTermStatsGenericImpl.this.destroyed)
/*      */             {
/*  318 */               event.cancel();
/*      */               
/*  320 */               return;
/*      */             }
/*      */             
/*  323 */             LongTermStatsGenericImpl.this.updateStats();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void sessionEnd()
/*      */   {
/*  333 */     synchronized (this)
/*      */     {
/*  335 */       if (!this.active)
/*      */       {
/*  337 */         return;
/*      */       }
/*      */       
/*  340 */       updateStats(3);
/*      */       
/*  342 */       this.active = false;
/*      */       
/*  344 */       if (this.event != null)
/*      */       {
/*  346 */         this.event.cancel();
/*      */         
/*  348 */         this.event = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateStats()
/*      */   {
/*  356 */     updateStats(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void updateStats(int record_type)
/*      */   {
/*  363 */     long[] current = this.generic_source.getStats(this.generic_id);
/*      */     
/*  365 */     long[] diffs = new long[this.STAT_ENTRY_COUNT];
/*      */     
/*  367 */     for (int i = 0; i < this.STAT_ENTRY_COUNT; i++)
/*      */     {
/*  369 */       current[i] -= this.ss[i];
/*      */     }
/*      */     
/*  372 */     write(record_type, diffs);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void write(int record_type, long[] line_stats)
/*      */   {
/*  380 */     synchronized (this)
/*      */     {
/*  382 */       if (this.destroyed)
/*      */       {
/*  384 */         return;
/*      */       }
/*      */       try
/*      */       {
/*  388 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  390 */         long now_mins = now / 60000L;
/*      */         
/*  392 */         String[] bits = utc_date_format.format(new Date(now)).split(",");
/*      */         
/*  394 */         String year = bits[0];
/*  395 */         String month = bits[1];
/*  396 */         String day = bits[2];
/*      */         
/*  398 */         String current_rel_file = year + File.separator + month + File.separator + day + ".dat";
/*      */         
/*      */ 
/*      */ 
/*  402 */         String stats_str = "";
/*      */         
/*  404 */         if (record_type == 1)
/*      */         {
/*      */ 
/*      */ 
/*  408 */           for (int i = 0; i < line_stats.length; i++)
/*      */           {
/*  410 */             stats_str = stats_str + "," + line_stats[i];
/*      */             
/*  412 */             this.line_stats_prev[i] = 0L;
/*      */           }
/*      */           
/*  415 */           this.day_cache = null;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  421 */           long[] diffs = new long[this.STAT_ENTRY_COUNT];
/*      */           
/*  423 */           for (int i = 0; i < line_stats.length; i++)
/*      */           {
/*  425 */             long diff = line_stats[i] - this.line_stats_prev[i];
/*      */             
/*  427 */             this.session_total += diff;
/*      */             
/*  429 */             diffs[i] = diff;
/*      */             
/*  431 */             stats_str = stats_str + "," + diff;
/*      */             
/*  433 */             this.line_stats_prev[i] = line_stats[i];
/*      */             
/*  435 */             this.stat_averages[i].update(diff);
/*      */           }
/*      */           
/*  438 */           if (this.day_cache != null)
/*      */           {
/*  440 */             if (this.day_cache.isForDay(year, month, day))
/*      */             {
/*  442 */               this.day_cache.addRecord(now_mins, diffs); }
/*      */           }
/*      */         }
/*      */         String line;
/*      */         String line;
/*  447 */         if (record_type != 2)
/*      */         {
/*  449 */           line = (record_type == 1 ? "s," : "e,") + 1 + "," + now_mins + stats_str;
/*      */         }
/*      */         else
/*      */         {
/*  453 */           line = stats_str.substring(1);
/*      */         }
/*      */         
/*      */ 
/*  457 */         if ((this.writer == null) || (!this.writer_rel_file.equals(current_rel_file)))
/*      */         {
/*      */ 
/*      */ 
/*  461 */           if (this.writer != null)
/*      */           {
/*      */ 
/*      */ 
/*  465 */             if (record_type != 1)
/*      */             {
/*  467 */               this.writer.println(line);
/*      */             }
/*      */             
/*  470 */             this.writer.close();
/*      */             
/*  472 */             if (this.writer.checkError())
/*      */             {
/*  474 */               this.writer = null;
/*      */               
/*  476 */               throw new IOException("Write faled");
/*      */             }
/*      */             
/*  479 */             this.writer = null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  484 */           if (record_type != 3)
/*      */           {
/*  486 */             File file = new File(this.stats_dir, current_rel_file);
/*      */             
/*  488 */             file.getParentFile().mkdirs();
/*      */             
/*  490 */             this.writer = new PrintWriter(new FileWriter(file, true));
/*      */             
/*  492 */             this.writer_rel_file = current_rel_file;
/*      */             
/*  494 */             if (record_type == 1)
/*      */             {
/*  496 */               this.writer.println(line);
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  503 */               for (int i = 0; i < this.STAT_ENTRY_COUNT; i++) {
/*  504 */                 this.st[i] += line_stats[i];
/*  505 */                 this.ss[i] += line_stats[i];
/*      */               }
/*      */               
/*  508 */               stats_str = "";
/*      */               
/*  510 */               long[] st_stats = this.st;
/*      */               
/*  512 */               for (int i = 0; i < st_stats.length; i++)
/*      */               {
/*  514 */                 stats_str = stats_str + "," + st_stats[i];
/*      */                 
/*  516 */                 this.line_stats_prev[i] = 0L;
/*      */               }
/*      */               
/*  519 */               line = "s,1," + now_mins + stats_str;
/*      */               
/*  521 */               this.writer.println(line);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  526 */           this.writer.println(line);
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  531 */         Debug.out("Failed to write long term stats", e);
/*      */       }
/*      */       finally
/*      */       {
/*  535 */         if (this.writer != null)
/*      */         {
/*  537 */           if (record_type == 3)
/*      */           {
/*  539 */             this.writer.close();
/*      */           }
/*      */           
/*  542 */           if (this.writer.checkError())
/*      */           {
/*  544 */             Debug.out("Failed to write long term stats");
/*      */             
/*  546 */             this.writer.close();
/*      */             
/*  548 */             this.writer = null;
/*      */ 
/*      */ 
/*      */           }
/*  552 */           else if (record_type == 3)
/*      */           {
/*  554 */             this.writer = null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  561 */     if (record_type != 3)
/*      */     {
/*  563 */       final List<LongTermStatsListener> to_fire = new ArrayList();
/*      */       
/*  565 */       for (Object[] entry : this.listeners)
/*      */       {
/*  567 */         long diff = this.session_total - ((Long)entry[2]).longValue();
/*      */         
/*  569 */         if (diff >= ((Long)entry[1]).longValue())
/*      */         {
/*  571 */           entry[2] = Long.valueOf(this.session_total);
/*      */           
/*  573 */           to_fire.add((LongTermStatsListener)entry[0]);
/*      */         }
/*      */       }
/*      */       
/*  577 */       if (to_fire.size() > 0)
/*      */       {
/*  579 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  586 */             for (LongTermStatsListener l : to_fire) {
/*      */               try
/*      */               {
/*  589 */                 l.updated(LongTermStatsGenericImpl.this);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  593 */                 Debug.out(e);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static String getString(long[] stats)
/*      */   {
/*  606 */     String str = "";
/*      */     
/*  608 */     for (long s : stats)
/*      */     {
/*  610 */       str = str + (str.length() == 0 ? "" : ", ") + s;
/*      */     }
/*      */     
/*  613 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private MonthCache getMonthCache(String year, String month)
/*      */   {
/*  621 */     String key = year + "_" + month;
/*      */     
/*  623 */     MonthCache cache = (MonthCache)this.month_cache_map.get(key);
/*      */     
/*  625 */     if (cache == null)
/*      */     {
/*  627 */       cache = new MonthCache(year, month, null);
/*      */       
/*  629 */       this.month_cache_map.put(key, cache);
/*      */     }
/*      */     
/*  632 */     return cache;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(Date start_date, Date end_date)
/*      */   {
/*  640 */     return getTotalUsageInPeriod(start_date, end_date, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(Date start_date, Date end_date, LongTermStats.RecordAccepter accepter)
/*      */   {
/*  649 */     boolean enable_caching = accepter == null;
/*      */     
/*  651 */     synchronized (this)
/*      */     {
/*  653 */       long[] result = new long[this.STAT_ENTRY_COUNT];
/*      */       
/*  655 */       long start_millis = start_date.getTime();
/*  656 */       long end_millis = end_date.getTime();
/*      */       
/*  658 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  660 */       long now_day = now / 86400000L * 86400000L;
/*      */       
/*  662 */       if (end_millis > now)
/*      */       {
/*  664 */         end_millis = now;
/*      */       }
/*      */       
/*  667 */       long start_day = start_millis / 86400000L * 86400000L;
/*  668 */       long end_day = end_millis / 86400000L * 86400000L;
/*      */       
/*  670 */       if (start_day > end_day)
/*      */       {
/*  672 */         return result;
/*      */       }
/*      */       
/*  675 */       long start_offset = start_millis - start_day;
/*      */       
/*  677 */       start_offset /= 60000L;
/*      */       
/*  679 */       boolean offset_cachable = start_offset % 60L == 0L;
/*      */       
/*      */ 
/*      */ 
/*  683 */       MonthCache month_cache = null;
/*      */       
/*  685 */       for (long this_day = start_day; this_day <= end_day;)
/*      */       {
/*  687 */         String[] bits = utc_date_format.format(new Date(this_day)).split(",");
/*      */         
/*  689 */         String year_str = bits[0];
/*  690 */         String month_str = bits[1];
/*  691 */         String day_str = bits[2];
/*      */         
/*  693 */         int year = Integer.parseInt(year_str);
/*  694 */         int month = Integer.parseInt(month_str);
/*  695 */         int day = Integer.parseInt(day_str);
/*      */         
/*  697 */         long cache_offset = this_day == start_day ? start_offset : 0L;
/*      */         
/*      */         boolean can_cache;
/*  700 */         if (enable_caching)
/*      */         {
/*  702 */           if ((month_cache == null) || (!month_cache.isForMonth(year_str, month_str)))
/*      */           {
/*  704 */             if ((month_cache != null) && (month_cache.isDirty()))
/*      */             {
/*  706 */               month_cache.save();
/*      */             }
/*      */             
/*  709 */             month_cache = getMonthCache(year_str, month_str);
/*      */           }
/*      */           
/*  712 */           boolean can_cache = (this_day != now_day) && ((this_day > start_day) || ((this_day == start_day) && (offset_cachable))) && (this_day < end_day);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  717 */           if (can_cache)
/*      */           {
/*  719 */             long[] cached_totals = month_cache.getTotals(day, cache_offset);
/*      */             
/*  721 */             if (cached_totals != null)
/*      */             {
/*  723 */               for (int i = 0; i < cached_totals.length; i++)
/*      */               {
/*  725 */                 result[i] += cached_totals[i];
/*      */               }
/*      */               
/*      */ 
/*      */               break label1082;
/*      */             }
/*      */           }
/*  732 */           else if (this_day == now_day)
/*      */           {
/*  734 */             if (this.day_cache != null)
/*      */             {
/*  736 */               if (this.day_cache.isForDay(year_str, month_str, day_str))
/*      */               {
/*  738 */                 long[] cached_totals = this.day_cache.getTotals(cache_offset);
/*      */                 
/*  740 */                 if (cached_totals != null)
/*      */                 {
/*  742 */                   for (int i = 0; i < cached_totals.length; i++)
/*      */                   {
/*  744 */                     result[i] += cached_totals[i];
/*      */                   }
/*      */                   
/*      */                   break label1082;
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  752 */                 this.day_cache = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  759 */           can_cache = false;
/*      */         }
/*      */         
/*  762 */         String current_rel_file = bits[0] + File.separator + bits[1] + File.separator + bits[2] + ".dat";
/*      */         
/*  764 */         File stats_file = new File(this.stats_dir, current_rel_file);
/*      */         
/*  766 */         if (!stats_file.exists())
/*      */         {
/*  768 */           if (can_cache)
/*      */           {
/*  770 */             month_cache.setTotals(day, cache_offset, new long[0]);
/*      */           }
/*      */         }
/*      */         else {
/*  774 */           LineNumberReader lnr = null;
/*      */           
/*      */ 
/*      */           try
/*      */           {
/*  779 */             lnr = new LineNumberReader(new FileReader(stats_file));
/*      */             
/*  781 */             long file_start_time = 0L;
/*      */             
/*  783 */             long[] file_totals = null;
/*      */             
/*  785 */             long[] file_result_totals = new long[this.STAT_ENTRY_COUNT];
/*      */             
/*  787 */             long[] session_start_stats = null;
/*  788 */             long session_start_time = 0L;
/*  789 */             long session_time = 0L;
/*      */             
/*      */             for (;;)
/*      */             {
/*  793 */               String line = lnr.readLine();
/*      */               
/*  795 */               if (line == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  802 */               String[] fields = line.split(",");
/*      */               
/*  804 */               if (fields.length >= this.STAT_ENTRY_COUNT)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  809 */                 String first_field = fields[0];
/*      */                 
/*  811 */                 if (first_field.equals("s"))
/*      */                 {
/*  813 */                   session_start_time = Long.parseLong(fields[2]) * 60000L;
/*      */                   
/*  815 */                   if (file_totals == null)
/*      */                   {
/*  817 */                     file_totals = new long[this.STAT_ENTRY_COUNT];
/*      */                     
/*  819 */                     file_start_time = session_start_time;
/*      */                   }
/*      */                   
/*  822 */                   session_time = session_start_time;
/*      */                   
/*  824 */                   session_start_stats = new long[this.STAT_ENTRY_COUNT];
/*      */                   
/*  826 */                   for (int i = 3; i < 3 + this.STAT_ENTRY_COUNT; i++)
/*      */                   {
/*  828 */                     session_start_stats[(i - 3)] = Long.parseLong(fields[i]);
/*      */                   }
/*  830 */                 } else if (session_start_time > 0L)
/*      */                 {
/*  832 */                   session_time += 60000L;
/*      */                   
/*  834 */                   int field_offset = 0;
/*      */                   
/*  836 */                   if (first_field.equals("e"))
/*      */                   {
/*  838 */                     field_offset = 3;
/*      */                   }
/*      */                   
/*  841 */                   long[] line_stats = new long[this.STAT_ENTRY_COUNT];
/*      */                   
/*  843 */                   for (int i = 0; i < this.STAT_ENTRY_COUNT; i++)
/*      */                   {
/*  845 */                     line_stats[i] = Long.parseLong(fields[(i + field_offset)]);
/*      */                     
/*  847 */                     file_totals[i] += line_stats[i];
/*      */                   }
/*      */                   
/*  850 */                   if ((session_time >= start_millis) && (session_time <= end_millis))
/*      */                   {
/*      */ 
/*  853 */                     if ((accepter == null) || (accepter.acceptRecord(session_time)))
/*      */                     {
/*  855 */                       for (int i = 0; i < this.STAT_ENTRY_COUNT; i++)
/*      */                       {
/*  857 */                         result[i] += line_stats[i];
/*      */                         
/*  859 */                         file_result_totals[i] += line_stats[i];
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*  868 */             if (file_totals == null)
/*      */             {
/*  870 */               file_totals = new long[0];
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*  875 */             if (can_cache)
/*      */             {
/*  877 */               month_cache.setTotals(day, cache_offset, file_result_totals);
/*      */               
/*  879 */               if (cache_offset != 0L)
/*      */               {
/*  881 */                 month_cache.setTotals(day, 0L, file_totals);
/*      */               }
/*      */               
/*      */             }
/*  885 */             else if (enable_caching)
/*      */             {
/*  887 */               if (this_day == now_day)
/*      */               {
/*  889 */                 if (this.day_cache == null)
/*      */                 {
/*      */ 
/*      */ 
/*  893 */                   this.day_cache = new DayCache(year_str, month_str, day_str, null);
/*      */                 }
/*      */                 
/*  896 */                 this.day_cache.setTotals(cache_offset, file_result_totals);
/*      */                 
/*  898 */                 if (cache_offset != 0L)
/*      */                 {
/*  900 */                   this.day_cache.setTotals(0L, file_totals);
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  912 */             if (lnr != null) {
/*      */               try
/*      */               {
/*  915 */                 lnr.close();
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*  685 */             this_day += 86400000L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */           catch (Throwable e)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  908 */             Debug.out(e);
/*      */           }
/*      */           finally
/*      */           {
/*  912 */             if (lnr != null) {
/*      */               try
/*      */               {
/*  915 */                 lnr.close();
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       label1082:
/*  924 */       if (enable_caching)
/*      */       {
/*  926 */         if ((month_cache != null) && (month_cache.isDirty()))
/*      */         {
/*  928 */           month_cache.save();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  934 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(int period_type, double multiplier)
/*      */   {
/*  943 */     return getTotalUsageInPeriod(period_type, multiplier, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(int period_type, double multiplier, LongTermStats.RecordAccepter accepter)
/*      */   {
/*  952 */     if (this.start_of_week == -1)
/*      */     {
/*  954 */       COConfigurationManager.addAndFireParameterListeners(new String[] { "long.term.stats.weekstart", "long.term.stats.monthstart" }, new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String name)
/*      */         {
/*      */ 
/*      */ 
/*  962 */           LongTermStatsGenericImpl.this.start_of_week = COConfigurationManager.getIntParameter("long.term.stats.weekstart");
/*  963 */           LongTermStatsGenericImpl.this.start_of_month = COConfigurationManager.getIntParameter("long.term.stats.monthstart");
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*  968 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */     long top_time;
/*      */     long top_time;
/*      */     long bottom_time;
/*  973 */     if (period_type == 0)
/*      */     {
/*  975 */       long bottom_time = now / 3600000L * 3600000L;
/*  976 */       top_time = bottom_time + 3600000L - 1L;
/*      */     } else { long top_time;
/*  978 */       if (period_type == 10)
/*      */       {
/*  980 */         long bottom_time = now - (multiplier * 3600000.0D);
/*  981 */         top_time = now;
/*      */       } else { long top_time;
/*  983 */         if (period_type == 11)
/*      */         {
/*  985 */           long bottom_time = now - (multiplier * 8.64E7D);
/*  986 */           top_time = now;
/*      */         } else { long top_time;
/*  988 */           if (period_type == 12)
/*      */           {
/*  990 */             long bottom_time = now - (multiplier * 6.048E8D);
/*  991 */             top_time = now;
/*      */           }
/*      */           else
/*      */           {
/*  995 */             Calendar calendar = new GregorianCalendar();
/*      */             
/*  997 */             calendar.setTimeInMillis(now);
/*      */             
/*  999 */             calendar.set(14, 0);
/* 1000 */             calendar.set(12, 0);
/* 1001 */             calendar.set(11, 0);
/*      */             
/* 1003 */             top_time = calendar.getTimeInMillis() + 86400000L - 1L;
/*      */             
/* 1005 */             if (period_type != 1)
/*      */             {
/* 1007 */               if (period_type == 2)
/*      */               {
/*      */ 
/*      */ 
/* 1011 */                 int day_of_week = calendar.get(7);
/*      */                 
/* 1013 */                 if (day_of_week != this.start_of_week)
/*      */                 {
/* 1015 */                   if (day_of_week > this.start_of_week)
/*      */                   {
/* 1017 */                     calendar.add(7, -(day_of_week - this.start_of_week));
/*      */                   }
/*      */                   else
/*      */                   {
/* 1021 */                     calendar.add(7, -(7 - (this.start_of_week - day_of_week)));
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/* 1026 */               else if (this.start_of_month == 1)
/*      */               {
/* 1028 */                 calendar.set(5, 1);
/*      */               }
/*      */               else
/*      */               {
/* 1032 */                 int day_of_month = calendar.get(5);
/*      */                 
/* 1034 */                 if (day_of_month != this.start_of_month)
/*      */                 {
/* 1036 */                   if (day_of_month > this.start_of_month)
/*      */                   {
/* 1038 */                     calendar.set(5, this.start_of_month);
/*      */                   }
/*      */                   else
/*      */                   {
/* 1042 */                     calendar.add(2, -1);
/*      */                     
/* 1044 */                     calendar.set(5, this.start_of_month);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 1049 */             bottom_time = calendar.getTimeInMillis();
/*      */           }
/*      */         } } }
/* 1052 */     return getTotalUsageInPeriod(new Date(bottom_time), new Date(top_time), accepter);
/*      */   }
/*      */   
/*      */ 
/*      */   public long[] getCurrentRateBytesPerSecond()
/*      */   {
/* 1058 */     long[] result = new long[this.STAT_ENTRY_COUNT];
/*      */     
/* 1060 */     for (int i = 0; i < this.STAT_ENTRY_COUNT; i++)
/*      */     {
/* 1062 */       result[i] = ((this.stat_averages[i].getAverage() / 60.0D));
/*      */     }
/*      */     
/* 1065 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(long min_delta_bytes, final LongTermStatsListener listener)
/*      */   {
/* 1073 */     this.listeners.add(new Object[] { listener, Long.valueOf(min_delta_bytes), Long.valueOf(this.session_total) });
/*      */     
/* 1075 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 1081 */         listener.updated(LongTermStatsGenericImpl.this);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(LongTermStatsListener listener)
/*      */   {
/* 1090 */     for (Object[] entry : this.listeners)
/*      */     {
/* 1092 */       if (entry[0] == listener)
/*      */       {
/* 1094 */         this.listeners.remove(entry);
/*      */         
/* 1096 */         break;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean isEnabled()
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: dup
/*      */     //   2: astore_1
/*      */     //   3: monitorenter
/*      */     //   4: aload_0
/*      */     //   5: getfield 597	org/gudy/azureus2/core3/stats/transfer/impl/LongTermStatsGenericImpl:active	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #230	-> byte code offset #0
/*      */     //   Java source line #232	-> byte code offset #4
/*      */     //   Java source line #233	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	LongTermStatsGenericImpl
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */   
/*      */   private static class DayCache
/*      */   {
/*      */     private final String year;
/*      */     private final String month;
/*      */     private final String day;
/* 1108 */     private final Map<Long, long[]> contents = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private DayCache(String _year, String _month, String _day)
/*      */     {
/* 1116 */       this.year = _year;
/* 1117 */       this.month = _month;
/* 1118 */       this.day = _day;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean isForDay(String _year, String _month, String _day)
/*      */     {
/* 1127 */       return (this.year.equals(_year)) && (this.month.equals(_month)) && (this.day.equals(_day));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addRecord(long offset, long[] stats)
/*      */     {
/* 1135 */       for (Map.Entry<Long, long[]> entry : this.contents.entrySet())
/*      */       {
/* 1137 */         if (offset >= ((Long)entry.getKey()).longValue())
/*      */         {
/* 1139 */           long[] old = (long[])entry.getValue();
/*      */           
/* 1141 */           for (int i = 0; i < old.length; i++)
/*      */           {
/* 1143 */             old[i] += stats[i];
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private long[] getTotals(long offset)
/*      */     {
/* 1153 */       return (long[])this.contents.get(Long.valueOf(offset));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setTotals(long offset, long[] value)
/*      */     {
/* 1161 */       this.contents.put(Long.valueOf(offset), value);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class MonthCache
/*      */   {
/*      */     private final String year;
/*      */     
/*      */     private final String month;
/*      */     
/*      */     private boolean dirty;
/*      */     
/*      */     private Map<String, List<Long>> contents;
/*      */     
/*      */ 
/*      */     private MonthCache(String _year, String _month)
/*      */     {
/* 1180 */       this.year = _year;
/* 1181 */       this.month = _month;
/*      */     }
/*      */     
/*      */ 
/*      */     private File getCacheFile()
/*      */     {
/* 1187 */       return new File(LongTermStatsGenericImpl.this.stats_dir, this.year + File.separator + this.month + File.separator + "cache.dat");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean isForMonth(String _year, String _month)
/*      */     {
/* 1195 */       return (this.year.equals(_year)) && (this.month.equals(_month));
/*      */     }
/*      */     
/*      */ 
/*      */     private Map<String, List<Long>> getContents()
/*      */     {
/* 1201 */       if (this.contents == null)
/*      */       {
/* 1203 */         File file = getCacheFile();
/*      */         
/* 1205 */         if (file.exists())
/*      */         {
/*      */ 
/*      */ 
/* 1209 */           this.contents = FileUtil.readResilientFile(file);
/*      */         }
/*      */         else
/*      */         {
/* 1213 */           this.contents = new HashMap();
/*      */         }
/*      */       }
/*      */       
/* 1217 */       return this.contents;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private long[] getTotals(int day)
/*      */     {
/* 1224 */       List<Long> records = (List)getContents().get(String.valueOf(day));
/*      */       
/* 1226 */       if (records != null)
/*      */       {
/* 1228 */         long[] result = new long[LongTermStatsGenericImpl.this.STAT_ENTRY_COUNT];
/*      */         
/* 1230 */         if (records.size() == LongTermStatsGenericImpl.this.STAT_ENTRY_COUNT)
/*      */         {
/* 1232 */           for (int i = 0; i < LongTermStatsGenericImpl.this.STAT_ENTRY_COUNT; i++)
/*      */           {
/* 1234 */             result[i] = ((Long)records.get(i)).longValue();
/*      */           }
/*      */         }
/*      */         
/* 1238 */         return result;
/*      */       }
/*      */       
/* 1241 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private long[] getTotals(int day, long start_offset)
/*      */     {
/* 1249 */       if (start_offset == 0L)
/*      */       {
/* 1251 */         return getTotals(day);
/*      */       }
/*      */       
/*      */ 
/* 1255 */       List<Long> records = (List)getContents().get(day + "." + start_offset);
/*      */       
/* 1257 */       if (records != null)
/*      */       {
/* 1259 */         long[] result = new long[LongTermStatsGenericImpl.this.STAT_ENTRY_COUNT];
/*      */         
/* 1261 */         if (records.size() == LongTermStatsGenericImpl.this.STAT_ENTRY_COUNT)
/*      */         {
/* 1263 */           for (int i = 0; i < LongTermStatsGenericImpl.this.STAT_ENTRY_COUNT; i++)
/*      */           {
/* 1265 */             result[i] = ((Long)records.get(i)).longValue();
/*      */           }
/*      */         }
/*      */         
/* 1269 */         return result;
/*      */       }
/*      */       
/* 1272 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setTotals(int day, long[] totals)
/*      */     {
/* 1281 */       List<Long> records = new ArrayList();
/*      */       
/* 1283 */       long[] arr$ = totals;int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Long l = Long.valueOf(arr$[i$]);
/*      */         
/* 1285 */         records.add(l);
/*      */       }
/*      */       
/* 1288 */       getContents().put(String.valueOf(day), records);
/*      */       
/* 1290 */       this.dirty = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setTotals(int day, long start_offset, long[] totals)
/*      */     {
/* 1299 */       if (start_offset == 0L)
/*      */       {
/* 1301 */         setTotals(day, totals);
/*      */       }
/*      */       else
/*      */       {
/* 1305 */         List<Long> records = new ArrayList();
/*      */         
/* 1307 */         long[] arr$ = totals;int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Long l = Long.valueOf(arr$[i$]);
/*      */           
/* 1309 */           records.add(l);
/*      */         }
/*      */         
/* 1312 */         getContents().put(day + "." + start_offset, records);
/*      */         
/* 1314 */         this.dirty = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean isDirty()
/*      */     {
/* 1321 */       return this.dirty;
/*      */     }
/*      */     
/*      */ 
/*      */     private void save()
/*      */     {
/* 1327 */       File file = getCacheFile();
/*      */       
/* 1329 */       file.getParentFile().mkdirs();
/*      */       
/*      */ 
/*      */ 
/* 1333 */       FileUtil.writeResilientFile(file, this.contents);
/*      */       
/* 1335 */       this.dirty = false;
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/impl/LongTermStatsGenericImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */