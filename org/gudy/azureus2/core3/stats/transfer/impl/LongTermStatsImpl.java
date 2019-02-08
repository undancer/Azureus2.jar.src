/*      */ package org.gudy.azureus2.core3.stats.transfer.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreComponent;
/*      */ import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.dht.DHT;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*      */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*      */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*      */ import com.aelitis.azureus.core.util.average.Average;
/*      */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*      */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*      */ import java.io.File;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.PrintStream;
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
/*      */ import org.gudy.azureus2.core3.global.GlobalManager;
/*      */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStats.RecordAccepter;
/*      */ import org.gudy.azureus2.core3.stats.transfer.LongTermStatsListener;
/*      */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.PluginManager;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class LongTermStatsImpl
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
/*      */   private AzureusCore core;
/*      */   private GlobalManagerStats gm_stats;
/*      */   private DHT[] dhts;
/*      */   private static final int STAT_ENTRY_COUNT = 6;
/*      */   private long st_p_sent;
/*      */   private long st_d_sent;
/*      */   private long st_p_received;
/*      */   private long st_d_received;
/*      */   private long st_dht_sent;
/*      */   private long st_dht_received;
/*      */   private long ss_p_sent;
/*      */   private long ss_d_sent;
/*      */   private long ss_p_received;
/*      */   private long ss_d_received;
/*      */   private long ss_dht_sent;
/*      */   private long ss_dht_received;
/*  100 */   private final long[] line_stats_prev = new long[6];
/*      */   
/*  102 */   private final Average[] stat_averages = new Average[6];
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean active;
/*      */   
/*      */ 
/*      */ 
/*      */   private boolean closing;
/*      */   
/*      */ 
/*      */ 
/*      */   private TimerEventPeriodic event;
/*      */   
/*      */ 
/*      */ 
/*      */   private PrintWriter writer;
/*      */   
/*      */ 
/*      */   private String writer_rel_file;
/*      */   
/*      */ 
/*      */   private DayCache day_cache;
/*      */   
/*      */ 
/*      */   private static final int MONTH_CACHE_MAX = 3;
/*      */   
/*      */ 
/*      */   private final Map<String, MonthCache> month_cache_map;
/*      */   
/*      */ 
/*  133 */   private static final SimpleDateFormat debug_utc_format = new SimpleDateFormat("yyyy,MM,dd:HH:mm");
/*  134 */   private static final SimpleDateFormat utc_date_format = new SimpleDateFormat("yyyy,MM,dd");
/*      */   private final File stats_dir;
/*      */   
/*  137 */   static { debug_utc_format.setTimeZone(TimeZone.getTimeZone("UTC"));
/*  138 */     utc_date_format.setTimeZone(TimeZone.getTimeZone("UTC"));
/*      */   }
/*      */   
/*      */   private LongTermStatsImpl(File _stats_dir)
/*      */   {
/*  105 */     for (int i = 0; i < 6; i++)
/*      */     {
/*  107 */       this.stat_averages[i] = AverageFactory.MovingImmediateAverage(3);
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
/*  122 */     this.month_cache_map = new LinkedHashMap(3, 0.75F, true)
/*      */     {
/*      */ 
/*      */ 
/*      */       protected boolean removeEldestEntry(Map.Entry<String, LongTermStatsImpl.MonthCache> eldest)
/*      */       {
/*      */ 
/*  129 */         return size() > 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  144 */     };
/*  145 */     this.listeners = new CopyOnWriteList();
/*      */     
/*  147 */     this.dispatcher = new AsyncDispatcher("lts", 5000);
/*      */     
/*  149 */     this.start_of_week = -1;
/*  150 */     this.start_of_month = -1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  158 */     this.stats_dir = _stats_dir;
/*      */   }
/*      */   
/*      */   public LongTermStatsImpl(AzureusCore _core, GlobalManagerStats _gm_stats)
/*      */   {
/*  105 */     for (int i = 0; i < 6; i++)
/*      */     {
/*  107 */       this.stat_averages[i] = AverageFactory.MovingImmediateAverage(3);
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
/*  122 */     this.month_cache_map = new LinkedHashMap(3, 0.75F, true)
/*      */     {
/*      */ 
/*      */ 
/*      */       protected boolean removeEldestEntry(Map.Entry<String, LongTermStatsImpl.MonthCache> eldest)
/*      */       {
/*      */ 
/*  129 */         return size() > 3;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  144 */     };
/*  145 */     this.listeners = new CopyOnWriteList();
/*      */     
/*  147 */     this.dispatcher = new AsyncDispatcher("lts", 5000);
/*      */     
/*  149 */     this.start_of_week = -1;
/*  150 */     this.start_of_month = -1;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  166 */     this.core = _core;
/*  167 */     this.gm_stats = _gm_stats;
/*      */     
/*  169 */     this.stats_dir = FileUtil.getUserFile("stats");
/*      */     
/*  171 */     COConfigurationManager.addParameterListener("long.term.stats.enable", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*  179 */         if (LongTermStatsImpl.this.destroyed)
/*      */         {
/*  181 */           COConfigurationManager.removeParameterListener("long.term.stats.enable", this);
/*      */           
/*  183 */           return;
/*      */         }
/*      */         
/*  186 */         boolean enabled = COConfigurationManager.getBooleanParameter(name);
/*      */         
/*  188 */         synchronized (LongTermStatsImpl.this)
/*      */         {
/*  190 */           if (enabled)
/*      */           {
/*  192 */             if (!LongTermStatsImpl.this.active)
/*      */             {
/*  194 */               LongTermStatsImpl.this.sessionStart();
/*      */             }
/*      */             
/*      */           }
/*  198 */           else if (LongTermStatsImpl.this.active)
/*      */           {
/*  200 */             LongTermStatsImpl.this.sessionEnd();
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*  206 */     });
/*  207 */     _core.addLifecycleListener(new AzureusCoreLifecycleAdapter()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void componentCreated(AzureusCore core, AzureusCoreComponent component)
/*      */       {
/*      */ 
/*      */ 
/*  215 */         if (LongTermStatsImpl.this.destroyed)
/*      */         {
/*  217 */           core.removeLifecycleListener(this);
/*      */           
/*  219 */           return;
/*      */         }
/*      */         
/*  222 */         if ((component instanceof GlobalManager))
/*      */         {
/*  224 */           synchronized (LongTermStatsImpl.this)
/*      */           {
/*  226 */             LongTermStatsImpl.this.sessionStart();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void stopped(AzureusCore core)
/*      */       {
/*  235 */         if (LongTermStatsImpl.this.destroyed)
/*      */         {
/*  237 */           core.removeLifecycleListener(this);
/*      */           
/*  239 */           return;
/*      */         }
/*      */         
/*  242 */         synchronized (LongTermStatsImpl.this)
/*      */         {
/*  244 */           LongTermStatsImpl.this.closing = true;
/*      */           
/*  246 */           if (LongTermStatsImpl.this.active)
/*      */           {
/*  248 */             LongTermStatsImpl.this.sessionEnd();
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   private long session_total;
/*      */   
/*      */   private final CopyOnWriteList<Object[]> listeners;
/*      */   
/*      */   private final AsyncDispatcher dispatcher;
/*      */   
/*      */   private int start_of_week;
/*      */   private int start_of_month;
/*      */   private volatile boolean destroyed;
/*      */   public void reset()
/*      */   {
/*  267 */     Debug.out("eh?");
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroyAndDeleteData()
/*      */   {
/*  273 */     synchronized (this)
/*      */     {
/*  275 */       this.destroyed = true;
/*      */       
/*  277 */       if (this.writer != null)
/*      */       {
/*  279 */         this.writer.close();
/*      */         
/*  281 */         this.writer = null;
/*      */       }
/*      */       
/*  284 */       File[] files = this.stats_dir.listFiles();
/*      */       
/*      */       label146:
/*  287 */       for (File file : files)
/*      */       {
/*  289 */         String name = file.getName();
/*      */         
/*  291 */         if ((name.length() == 4) && (Character.isDigit(name.charAt(0))))
/*      */         {
/*  293 */           for (int i = 0; i < 4; i++)
/*      */           {
/*  295 */             if (FileUtil.recursiveDeleteNoCheck(file)) {
/*      */               break label146;
/*      */             }
/*      */             
/*      */             try
/*      */             {
/*  301 */               Thread.sleep(250L);
/*      */             }
/*      */             catch (Throwable e) {}
/*      */           }
/*      */           
/*      */ 
/*  307 */           Debug.out("Failed to delete " + file);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private DHT[] getDHTs()
/*      */   {
/*  316 */     if (this.dhts == null) {
/*      */       try
/*      */       {
/*  319 */         PluginManager pm = this.core.getPluginManager();
/*      */         
/*  321 */         if (pm.isInitialized())
/*      */         {
/*  323 */           PluginInterface dht_pi = pm.getPluginInterfaceByClass(DHTPlugin.class);
/*      */           
/*  325 */           if (dht_pi == null)
/*      */           {
/*  327 */             this.dhts = new DHT[0];
/*      */           }
/*      */           else
/*      */           {
/*  331 */             DHTPlugin plugin = (DHTPlugin)dht_pi.getPlugin();
/*      */             
/*  333 */             if (!plugin.isInitialising())
/*      */             {
/*  335 */               if (plugin.isEnabled())
/*      */               {
/*  337 */                 this.dhts = ((DHTPlugin)dht_pi.getPlugin()).getDHTs();
/*      */               }
/*      */               else
/*      */               {
/*  341 */                 this.dhts = new DHT[0];
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  348 */         this.dhts = new DHT[0];
/*      */       }
/*      */     }
/*      */     
/*  352 */     return this.dhts;
/*      */   }
/*      */   
/*      */ 
/*      */   private void sessionStart()
/*      */   {
/*  358 */     OverallStatsImpl stats = (OverallStatsImpl)StatsFactory.getStats();
/*      */     
/*  360 */     synchronized (this)
/*      */     {
/*  362 */       if (this.closing)
/*      */       {
/*  364 */         return;
/*      */       }
/*      */       
/*  367 */       boolean enabled = COConfigurationManager.getBooleanParameter("long.term.stats.enable");
/*      */       
/*  369 */       if ((this.active) || (!enabled))
/*      */       {
/*  371 */         return;
/*      */       }
/*      */       
/*  374 */       this.active = true;
/*      */       
/*  376 */       long[] snap = stats.getLastSnapshot();
/*      */       
/*  378 */       this.ss_d_received = this.gm_stats.getTotalDataBytesReceived();
/*  379 */       this.ss_p_received = this.gm_stats.getTotalProtocolBytesReceived();
/*      */       
/*  381 */       this.ss_d_sent = this.gm_stats.getTotalDataBytesSent();
/*  382 */       this.ss_p_sent = this.gm_stats.getTotalProtocolBytesSent();
/*      */       
/*  384 */       this.ss_dht_sent = 0L;
/*  385 */       this.ss_dht_received = 0L;
/*      */       
/*  387 */       if (this.core.isStarted())
/*      */       {
/*  389 */         DHT[] dhts = getDHTs();
/*      */         
/*  391 */         if (dhts != null)
/*      */         {
/*  393 */           for (DHT dht : dhts)
/*      */           {
/*  395 */             DHTTransportStats dht_stats = dht.getTransport().getStats();
/*      */             
/*  397 */             this.ss_dht_sent += dht_stats.getBytesSent();
/*  398 */             this.ss_dht_received += dht_stats.getBytesReceived();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  403 */       this.st_p_sent = (snap[0] + (this.ss_p_sent - snap[6]));
/*  404 */       this.st_d_sent = (snap[1] + (this.ss_d_sent - snap[7]));
/*  405 */       this.st_p_received = (snap[2] + (this.ss_p_received - snap[8]));
/*  406 */       this.st_d_received = (snap[3] + (this.ss_d_received - snap[9]));
/*  407 */       this.st_dht_sent = (snap[4] + (this.ss_dht_sent - snap[10]));
/*  408 */       this.st_dht_received = (snap[5] + (this.ss_dht_received - snap[11]));
/*      */       
/*  410 */       write(1, new long[] { this.st_p_sent, this.st_d_sent, this.st_p_received, this.st_d_received, this.st_dht_sent, this.st_dht_received });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  419 */       if (this.event == null)
/*      */       {
/*  421 */         this.event = SimpleTimer.addPeriodicEvent("LongTermStats", 60000L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*  430 */             if (LongTermStatsImpl.this.destroyed)
/*      */             {
/*  432 */               event.cancel();
/*      */               
/*  434 */               return;
/*      */             }
/*      */             
/*  437 */             LongTermStatsImpl.this.updateStats();
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void sessionEnd()
/*      */   {
/*  447 */     synchronized (this)
/*      */     {
/*  449 */       if (!this.active)
/*      */       {
/*  451 */         return;
/*      */       }
/*      */       
/*  454 */       updateStats(3);
/*      */       
/*  456 */       this.active = false;
/*      */       
/*  458 */       if (this.event != null)
/*      */       {
/*  460 */         this.event.cancel();
/*      */         
/*  462 */         this.event = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void updateStats()
/*      */   {
/*  470 */     updateStats(2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void updateStats(int record_type)
/*      */   {
/*  477 */     long current_d_received = this.gm_stats.getTotalDataBytesReceived();
/*  478 */     long current_p_received = this.gm_stats.getTotalProtocolBytesReceived();
/*      */     
/*  480 */     long current_d_sent = this.gm_stats.getTotalDataBytesSent();
/*  481 */     long current_p_sent = this.gm_stats.getTotalProtocolBytesSent();
/*      */     
/*  483 */     long current_dht_sent = 0L;
/*  484 */     long current_dht_received = 0L;
/*      */     
/*  486 */     DHT[] dhts = getDHTs();
/*      */     
/*  488 */     if (dhts != null)
/*      */     {
/*  490 */       for (DHT dht : dhts)
/*      */       {
/*  492 */         DHTTransportStats dht_stats = dht.getTransport().getStats();
/*      */         
/*  494 */         current_dht_sent += dht_stats.getBytesSent();
/*  495 */         current_dht_received += dht_stats.getBytesReceived();
/*      */       }
/*      */     }
/*      */     
/*  499 */     write(record_type, new long[] { current_p_sent - this.ss_p_sent, current_d_sent - this.ss_d_sent, current_p_received - this.ss_p_received, current_d_received - this.ss_d_received, current_dht_sent - this.ss_dht_sent, current_dht_received - this.ss_dht_received });
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
/*      */   private void write(int record_type, long[] line_stats)
/*      */   {
/*  514 */     synchronized (this)
/*      */     {
/*  516 */       if (this.destroyed)
/*      */       {
/*  518 */         return;
/*      */       }
/*      */       try
/*      */       {
/*  522 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  524 */         long now_mins = now / 60000L;
/*      */         
/*  526 */         String[] bits = utc_date_format.format(new Date(now)).split(",");
/*      */         
/*  528 */         String year = bits[0];
/*  529 */         String month = bits[1];
/*  530 */         String day = bits[2];
/*      */         
/*  532 */         String current_rel_file = year + File.separator + month + File.separator + day + ".dat";
/*      */         
/*      */ 
/*      */ 
/*  536 */         String stats_str = "";
/*      */         
/*  538 */         if (record_type == 1)
/*      */         {
/*      */ 
/*      */ 
/*  542 */           for (int i = 0; i < line_stats.length; i++)
/*      */           {
/*  544 */             stats_str = stats_str + "," + line_stats[i];
/*      */             
/*  546 */             this.line_stats_prev[i] = 0L;
/*      */           }
/*      */           
/*  549 */           this.day_cache = null;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  555 */           long[] diffs = new long[6];
/*      */           
/*  557 */           for (int i = 0; i < line_stats.length; i++)
/*      */           {
/*  559 */             long diff = line_stats[i] - this.line_stats_prev[i];
/*      */             
/*  561 */             this.session_total += diff;
/*      */             
/*  563 */             diffs[i] = diff;
/*      */             
/*  565 */             stats_str = stats_str + "," + diff;
/*      */             
/*  567 */             this.line_stats_prev[i] = line_stats[i];
/*      */             
/*  569 */             this.stat_averages[i].update(diff);
/*      */           }
/*      */           
/*  572 */           if (this.day_cache != null)
/*      */           {
/*  574 */             if (this.day_cache.isForDay(year, month, day))
/*      */             {
/*  576 */               this.day_cache.addRecord(now_mins, diffs); }
/*      */           }
/*      */         }
/*      */         String line;
/*      */         String line;
/*  581 */         if (record_type != 2)
/*      */         {
/*  583 */           line = (record_type == 1 ? "s," : "e,") + 1 + "," + now_mins + stats_str;
/*      */         }
/*      */         else
/*      */         {
/*  587 */           line = stats_str.substring(1);
/*      */         }
/*      */         
/*      */ 
/*  591 */         if ((this.writer == null) || (!this.writer_rel_file.equals(current_rel_file)))
/*      */         {
/*      */ 
/*      */ 
/*  595 */           if (this.writer != null)
/*      */           {
/*      */ 
/*      */ 
/*  599 */             if (record_type != 1)
/*      */             {
/*  601 */               this.writer.println(line);
/*      */             }
/*      */             
/*  604 */             this.writer.close();
/*      */             
/*  606 */             if (this.writer.checkError())
/*      */             {
/*  608 */               this.writer = null;
/*      */               
/*  610 */               throw new IOException("Write faled");
/*      */             }
/*      */             
/*  613 */             this.writer = null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  618 */           if (record_type != 3)
/*      */           {
/*  620 */             File file = new File(this.stats_dir, current_rel_file);
/*      */             
/*  622 */             file.getParentFile().mkdirs();
/*      */             
/*  624 */             this.writer = new PrintWriter(new FileWriter(file, true));
/*      */             
/*  626 */             this.writer_rel_file = current_rel_file;
/*      */             
/*  628 */             if (record_type == 1)
/*      */             {
/*  630 */               this.writer.println(line);
/*      */ 
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*      */ 
/*  637 */               this.st_p_sent += line_stats[0];
/*  638 */               this.st_d_sent += line_stats[1];
/*  639 */               this.st_p_received += line_stats[2];
/*  640 */               this.st_d_received += line_stats[3];
/*  641 */               this.st_dht_sent += line_stats[4];
/*  642 */               this.st_dht_received += line_stats[5];
/*      */               
/*  644 */               this.ss_p_sent += line_stats[0];
/*  645 */               this.ss_d_sent += line_stats[1];
/*  646 */               this.ss_p_received += line_stats[2];
/*  647 */               this.ss_d_received += line_stats[3];
/*  648 */               this.ss_dht_sent += line_stats[4];
/*  649 */               this.ss_dht_received += line_stats[5];
/*      */               
/*  651 */               stats_str = "";
/*      */               
/*  653 */               long[] st_stats = { this.st_p_sent, this.st_d_sent, this.st_p_received, this.st_d_received, this.st_dht_sent, this.st_dht_received };
/*      */               
/*  655 */               for (int i = 0; i < st_stats.length; i++)
/*      */               {
/*  657 */                 stats_str = stats_str + "," + st_stats[i];
/*      */                 
/*  659 */                 this.line_stats_prev[i] = 0L;
/*      */               }
/*      */               
/*  662 */               line = "s,1," + now_mins + stats_str;
/*      */               
/*  664 */               this.writer.println(line);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  669 */           this.writer.println(line);
/*      */         }
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*  674 */         Debug.out("Failed to write long term stats", e);
/*      */       }
/*      */       finally
/*      */       {
/*  678 */         if (this.writer != null)
/*      */         {
/*  680 */           if (record_type == 3)
/*      */           {
/*  682 */             this.writer.close();
/*      */           }
/*      */           
/*  685 */           if (this.writer.checkError())
/*      */           {
/*  687 */             Debug.out("Failed to write long term stats");
/*      */             
/*  689 */             this.writer.close();
/*      */             
/*  691 */             this.writer = null;
/*      */ 
/*      */ 
/*      */           }
/*  695 */           else if (record_type == 3)
/*      */           {
/*  697 */             this.writer = null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  704 */     if (record_type != 3)
/*      */     {
/*  706 */       final List<LongTermStatsListener> to_fire = new ArrayList();
/*      */       
/*  708 */       for (Object[] entry : this.listeners)
/*      */       {
/*  710 */         long diff = this.session_total - ((Long)entry[2]).longValue();
/*      */         
/*  712 */         if (diff >= ((Long)entry[1]).longValue())
/*      */         {
/*  714 */           entry[2] = Long.valueOf(this.session_total);
/*      */           
/*  716 */           to_fire.add((LongTermStatsListener)entry[0]);
/*      */         }
/*      */       }
/*      */       
/*  720 */       if (to_fire.size() > 0)
/*      */       {
/*  722 */         this.dispatcher.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/*  729 */             for (LongTermStatsListener l : to_fire) {
/*      */               try
/*      */               {
/*  732 */                 l.updated(LongTermStatsImpl.this);
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  736 */                 Debug.out(e);
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
/*  749 */     String str = "";
/*      */     
/*  751 */     for (long s : stats)
/*      */     {
/*  753 */       str = str + (str.length() == 0 ? "" : ", ") + s;
/*      */     }
/*      */     
/*  756 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private MonthCache getMonthCache(String year, String month)
/*      */   {
/*  764 */     String key = year + "_" + month;
/*      */     
/*  766 */     MonthCache cache = (MonthCache)this.month_cache_map.get(key);
/*      */     
/*  768 */     if (cache == null)
/*      */     {
/*  770 */       cache = new MonthCache(year, month, null);
/*      */       
/*  772 */       this.month_cache_map.put(key, cache);
/*      */     }
/*      */     
/*  775 */     return cache;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(Date start_date, Date end_date)
/*      */   {
/*  783 */     return getTotalUsageInPeriod(start_date, end_date, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(Date start_date, Date end_date, LongTermStats.RecordAccepter accepter)
/*      */   {
/*  792 */     boolean enable_caching = accepter == null;
/*      */     
/*  794 */     synchronized (this)
/*      */     {
/*  796 */       long[] result = new long[6];
/*      */       
/*  798 */       long start_millis = start_date.getTime();
/*  799 */       long end_millis = end_date.getTime();
/*      */       
/*  801 */       long now = SystemTime.getCurrentTime();
/*      */       
/*  803 */       long now_day = now / 86400000L * 86400000L;
/*      */       
/*  805 */       if (end_millis > now)
/*      */       {
/*  807 */         end_millis = now;
/*      */       }
/*      */       
/*  810 */       long start_day = start_millis / 86400000L * 86400000L;
/*  811 */       long end_day = end_millis / 86400000L * 86400000L;
/*      */       
/*  813 */       if (start_day > end_day)
/*      */       {
/*  815 */         return result;
/*      */       }
/*      */       
/*  818 */       long start_offset = start_millis - start_day;
/*      */       
/*  820 */       start_offset /= 60000L;
/*      */       
/*  822 */       boolean offset_cachable = start_offset % 60L == 0L;
/*      */       
/*      */ 
/*      */ 
/*  826 */       MonthCache month_cache = null;
/*      */       
/*  828 */       for (long this_day = start_day; this_day <= end_day;)
/*      */       {
/*  830 */         String[] bits = utc_date_format.format(new Date(this_day)).split(",");
/*      */         
/*  832 */         String year_str = bits[0];
/*  833 */         String month_str = bits[1];
/*  834 */         String day_str = bits[2];
/*      */         
/*  836 */         int year = Integer.parseInt(year_str);
/*  837 */         int month = Integer.parseInt(month_str);
/*  838 */         int day = Integer.parseInt(day_str);
/*      */         
/*  840 */         long cache_offset = this_day == start_day ? start_offset : 0L;
/*      */         
/*      */         boolean can_cache;
/*  843 */         if (enable_caching)
/*      */         {
/*  845 */           if ((month_cache == null) || (!month_cache.isForMonth(year_str, month_str)))
/*      */           {
/*  847 */             if ((month_cache != null) && (month_cache.isDirty()))
/*      */             {
/*  849 */               month_cache.save();
/*      */             }
/*      */             
/*  852 */             month_cache = getMonthCache(year_str, month_str);
/*      */           }
/*      */           
/*  855 */           boolean can_cache = (this_day != now_day) && ((this_day > start_day) || ((this_day == start_day) && (offset_cachable))) && (this_day < end_day);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  860 */           if (can_cache)
/*      */           {
/*  862 */             long[] cached_totals = month_cache.getTotals(day, cache_offset);
/*      */             
/*  864 */             if (cached_totals != null)
/*      */             {
/*  866 */               for (int i = 0; i < cached_totals.length; i++)
/*      */               {
/*  868 */                 result[i] += cached_totals[i];
/*      */               }
/*      */               
/*      */ 
/*      */               break label1067;
/*      */             }
/*      */           }
/*  875 */           else if (this_day == now_day)
/*      */           {
/*  877 */             if (this.day_cache != null)
/*      */             {
/*  879 */               if (this.day_cache.isForDay(year_str, month_str, day_str))
/*      */               {
/*  881 */                 long[] cached_totals = this.day_cache.getTotals(cache_offset);
/*      */                 
/*  883 */                 if (cached_totals != null)
/*      */                 {
/*  885 */                   for (int i = 0; i < cached_totals.length; i++)
/*      */                   {
/*  887 */                     result[i] += cached_totals[i];
/*      */                   }
/*      */                   
/*      */                   break label1067;
/*      */                 }
/*      */               }
/*      */               else
/*      */               {
/*  895 */                 this.day_cache = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  902 */           can_cache = false;
/*      */         }
/*      */         
/*  905 */         String current_rel_file = bits[0] + File.separator + bits[1] + File.separator + bits[2] + ".dat";
/*      */         
/*  907 */         File stats_file = new File(this.stats_dir, current_rel_file);
/*      */         
/*  909 */         if (!stats_file.exists())
/*      */         {
/*  911 */           if (can_cache)
/*      */           {
/*  913 */             month_cache.setTotals(day, cache_offset, new long[0]);
/*      */           }
/*      */         }
/*      */         else {
/*  917 */           LineNumberReader lnr = null;
/*      */           
/*      */ 
/*      */           try
/*      */           {
/*  922 */             lnr = new LineNumberReader(new FileReader(stats_file));
/*      */             
/*  924 */             long file_start_time = 0L;
/*      */             
/*  926 */             long[] file_totals = null;
/*      */             
/*  928 */             long[] file_result_totals = new long[6];
/*      */             
/*  930 */             long[] session_start_stats = null;
/*  931 */             long session_start_time = 0L;
/*  932 */             long session_time = 0L;
/*      */             
/*      */             for (;;)
/*      */             {
/*  936 */               String line = lnr.readLine();
/*      */               
/*  938 */               if (line == null) {
/*      */                 break;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  945 */               String[] fields = line.split(",");
/*      */               
/*  947 */               if (fields.length >= 6)
/*      */               {
/*      */ 
/*      */                 try
/*      */                 {
/*      */ 
/*  953 */                   String first_field = fields[0];
/*      */                   
/*  955 */                   if (first_field.equals("s"))
/*      */                   {
/*  957 */                     session_start_time = Long.parseLong(fields[2]) * 60000L;
/*      */                     
/*  959 */                     if (file_totals == null)
/*      */                     {
/*  961 */                       file_totals = new long[6];
/*      */                       
/*  963 */                       file_start_time = session_start_time;
/*      */                     }
/*      */                     
/*  966 */                     session_time = session_start_time;
/*      */                     
/*  968 */                     session_start_stats = new long[6];
/*      */                     
/*  970 */                     for (int i = 3; i < 9; i++)
/*      */                     {
/*  972 */                       session_start_stats[(i - 3)] = Long.parseLong(fields[i]);
/*      */                     }
/*  974 */                   } else if (session_start_time > 0L)
/*      */                   {
/*  976 */                     session_time += 60000L;
/*      */                     
/*  978 */                     int field_offset = 0;
/*      */                     
/*  980 */                     if (first_field.equals("e"))
/*      */                     {
/*  982 */                       field_offset = 3;
/*      */                     }
/*      */                     
/*  985 */                     long[] line_stats = new long[6];
/*      */                     
/*  987 */                     for (int i = 0; i < 6; i++)
/*      */                     {
/*  989 */                       line_stats[i] = Long.parseLong(fields[(i + field_offset)]);
/*      */                       
/*  991 */                       file_totals[i] += line_stats[i];
/*      */                     }
/*      */                     
/*  994 */                     if ((session_time >= start_millis) && (session_time <= end_millis))
/*      */                     {
/*      */ 
/*  997 */                       if ((accepter == null) || (accepter.acceptRecord(session_time)))
/*      */                       {
/*  999 */                         for (int i = 0; i < 6; i++)
/*      */                         {
/* 1001 */                           result[i] += line_stats[i];
/*      */                           
/* 1003 */                           file_result_totals[i] += line_stats[i];
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 catch (Throwable e) {}
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1016 */             if (file_totals == null)
/*      */             {
/* 1018 */               file_totals = new long[0];
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1023 */             if (can_cache)
/*      */             {
/* 1025 */               month_cache.setTotals(day, cache_offset, file_result_totals);
/*      */               
/* 1027 */               if (cache_offset != 0L)
/*      */               {
/* 1029 */                 month_cache.setTotals(day, 0L, file_totals);
/*      */               }
/*      */               
/*      */             }
/* 1033 */             else if (enable_caching)
/*      */             {
/* 1035 */               if (this_day == now_day)
/*      */               {
/* 1037 */                 if (this.day_cache == null)
/*      */                 {
/*      */ 
/*      */ 
/* 1041 */                   this.day_cache = new DayCache(year_str, month_str, day_str, null);
/*      */                 }
/*      */                 
/* 1044 */                 this.day_cache.setTotals(cache_offset, file_result_totals);
/*      */                 
/* 1046 */                 if (cache_offset != 0L)
/*      */                 {
/* 1048 */                   this.day_cache.setTotals(0L, file_totals);
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
/* 1060 */             if (lnr != null) {
/*      */               try
/*      */               {
/* 1063 */                 lnr.close();
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*  828 */             this_day += 86400000L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*      */ 
/*      */ 
/*      */ 
/* 1056 */             Debug.out(e);
/*      */           }
/*      */           finally
/*      */           {
/* 1060 */             if (lnr != null) {
/*      */               try
/*      */               {
/* 1063 */                 lnr.close();
/*      */               }
/*      */               catch (Throwable e) {}
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */       label1067:
/* 1072 */       if (enable_caching)
/*      */       {
/* 1074 */         if ((month_cache != null) && (month_cache.isDirty()))
/*      */         {
/* 1076 */           month_cache.save();
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1082 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(int period_type, double multiplier)
/*      */   {
/* 1091 */     return getTotalUsageInPeriod(period_type, multiplier, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long[] getTotalUsageInPeriod(int period_type, double multiplier, LongTermStats.RecordAccepter accepter)
/*      */   {
/* 1100 */     if (this.start_of_week == -1)
/*      */     {
/* 1102 */       COConfigurationManager.addAndFireParameterListeners(new String[] { "long.term.stats.weekstart", "long.term.stats.monthstart" }, new ParameterListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(String name)
/*      */         {
/*      */ 
/*      */ 
/* 1110 */           LongTermStatsImpl.this.start_of_week = COConfigurationManager.getIntParameter("long.term.stats.weekstart");
/* 1111 */           LongTermStatsImpl.this.start_of_month = COConfigurationManager.getIntParameter("long.term.stats.monthstart");
/*      */         }
/*      */       });
/*      */     }
/*      */     
/* 1116 */     long now = SystemTime.getCurrentTime();
/*      */     
/*      */     long top_time;
/*      */     long top_time;
/*      */     long bottom_time;
/* 1121 */     if (period_type == 0)
/*      */     {
/* 1123 */       long bottom_time = now / 3600000L * 3600000L;
/* 1124 */       top_time = bottom_time + 3600000L - 1L;
/*      */     } else { long top_time;
/* 1126 */       if (period_type == 10)
/*      */       {
/* 1128 */         long bottom_time = now - (3600000.0D * multiplier);
/* 1129 */         top_time = now;
/*      */       } else { long top_time;
/* 1131 */         if (period_type == 11)
/*      */         {
/* 1133 */           long bottom_time = now - (8.64E7D * multiplier);
/* 1134 */           top_time = now;
/*      */         } else { long top_time;
/* 1136 */           if (period_type == 12)
/*      */           {
/* 1138 */             long bottom_time = now - (6.048E8D * multiplier);
/* 1139 */             top_time = now;
/*      */           }
/*      */           else
/*      */           {
/* 1143 */             Calendar calendar = new GregorianCalendar();
/*      */             
/* 1145 */             calendar.setTimeInMillis(SystemTime.getCurrentTime());
/*      */             
/* 1147 */             calendar.set(14, 0);
/* 1148 */             calendar.set(12, 0);
/* 1149 */             calendar.set(11, 0);
/*      */             
/* 1151 */             top_time = calendar.getTimeInMillis() + 86400000L - 1L;
/*      */             
/* 1153 */             if (period_type != 1)
/*      */             {
/* 1155 */               if (period_type == 2)
/*      */               {
/*      */ 
/*      */ 
/* 1159 */                 int day_of_week = calendar.get(7);
/*      */                 
/* 1161 */                 if (day_of_week != this.start_of_week)
/*      */                 {
/* 1163 */                   if (day_of_week > this.start_of_week)
/*      */                   {
/* 1165 */                     calendar.add(7, -(day_of_week - this.start_of_week));
/*      */                   }
/*      */                   else
/*      */                   {
/* 1169 */                     calendar.add(7, -(7 - (this.start_of_week - day_of_week)));
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/* 1174 */               else if (this.start_of_month == 1)
/*      */               {
/* 1176 */                 calendar.set(5, 1);
/*      */               }
/*      */               else
/*      */               {
/* 1180 */                 int day_of_month = calendar.get(5);
/*      */                 
/* 1182 */                 if (day_of_month != this.start_of_month)
/*      */                 {
/* 1184 */                   if (day_of_month > this.start_of_month)
/*      */                   {
/* 1186 */                     calendar.set(5, this.start_of_month);
/*      */                   }
/*      */                   else
/*      */                   {
/* 1190 */                     calendar.add(2, -1);
/*      */                     
/* 1192 */                     calendar.set(5, this.start_of_month);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 1197 */             bottom_time = calendar.getTimeInMillis();
/*      */           }
/*      */         } } }
/* 1200 */     return getTotalUsageInPeriod(new Date(bottom_time), new Date(top_time), accepter);
/*      */   }
/*      */   
/*      */ 
/*      */   public long[] getCurrentRateBytesPerSecond()
/*      */   {
/* 1206 */     long[] result = new long[6];
/*      */     
/* 1208 */     for (int i = 0; i < 6; i++)
/*      */     {
/* 1210 */       result[i] = ((this.stat_averages[i].getAverage() / 60.0D));
/*      */     }
/*      */     
/* 1213 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addListener(long min_delta_bytes, final LongTermStatsListener listener)
/*      */   {
/* 1221 */     this.listeners.add(new Object[] { listener, Long.valueOf(min_delta_bytes), Long.valueOf(this.session_total) });
/*      */     
/* 1223 */     this.dispatcher.dispatch(new AERunnable()
/*      */     {
/*      */ 
/*      */       public void runSupport()
/*      */       {
/*      */ 
/* 1229 */         listener.updated(LongTermStatsImpl.this);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void removeListener(LongTermStatsListener listener)
/*      */   {
/* 1238 */     for (Object[] entry : this.listeners)
/*      */     {
/* 1240 */       if (entry[0] == listener)
/*      */       {
/* 1242 */         this.listeners.remove(entry);
/*      */         
/* 1244 */         break;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class DayCache
/*      */   {
/*      */     private final String year;
/*      */     
/*      */     private final String month;
/*      */     private final String day;
/* 1256 */     private final Map<Long, long[]> contents = new HashMap();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private DayCache(String _year, String _month, String _day)
/*      */     {
/* 1264 */       this.year = _year;
/* 1265 */       this.month = _month;
/* 1266 */       this.day = _day;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean isForDay(String _year, String _month, String _day)
/*      */     {
/* 1275 */       return (this.year.equals(_year)) && (this.month.equals(_month)) && (this.day.equals(_day));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void addRecord(long offset, long[] stats)
/*      */     {
/* 1283 */       for (Map.Entry<Long, long[]> entry : this.contents.entrySet())
/*      */       {
/* 1285 */         if (offset >= ((Long)entry.getKey()).longValue())
/*      */         {
/* 1287 */           long[] old = (long[])entry.getValue();
/*      */           
/* 1289 */           for (int i = 0; i < old.length; i++)
/*      */           {
/* 1291 */             old[i] += stats[i];
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private long[] getTotals(long offset)
/*      */     {
/* 1301 */       return (long[])this.contents.get(Long.valueOf(offset));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setTotals(long offset, long[] value)
/*      */     {
/* 1309 */       this.contents.put(Long.valueOf(offset), value);
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
/* 1328 */       this.year = _year;
/* 1329 */       this.month = _month;
/*      */     }
/*      */     
/*      */ 
/*      */     private File getCacheFile()
/*      */     {
/* 1335 */       return new File(LongTermStatsImpl.this.stats_dir, this.year + File.separator + this.month + File.separator + "cache.dat");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean isForMonth(String _year, String _month)
/*      */     {
/* 1343 */       return (this.year.equals(_year)) && (this.month.equals(_month));
/*      */     }
/*      */     
/*      */ 
/*      */     private Map<String, List<Long>> getContents()
/*      */     {
/* 1349 */       if (this.contents == null)
/*      */       {
/* 1351 */         File file = getCacheFile();
/*      */         
/* 1353 */         if (file.exists())
/*      */         {
/*      */ 
/*      */ 
/* 1357 */           this.contents = FileUtil.readResilientFile(file);
/*      */         }
/*      */         else
/*      */         {
/* 1361 */           this.contents = new HashMap();
/*      */         }
/*      */       }
/*      */       
/* 1365 */       return this.contents;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private long[] getTotals(int day)
/*      */     {
/* 1372 */       List<Long> records = (List)getContents().get(String.valueOf(day));
/*      */       
/* 1374 */       if (records != null)
/*      */       {
/* 1376 */         long[] result = new long[6];
/*      */         
/* 1378 */         if (records.size() == 6)
/*      */         {
/* 1380 */           for (int i = 0; i < 6; i++)
/*      */           {
/* 1382 */             result[i] = ((Long)records.get(i)).longValue();
/*      */           }
/*      */         }
/*      */         
/* 1386 */         return result;
/*      */       }
/*      */       
/* 1389 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private long[] getTotals(int day, long start_offset)
/*      */     {
/* 1397 */       if (start_offset == 0L)
/*      */       {
/* 1399 */         return getTotals(day);
/*      */       }
/*      */       
/*      */ 
/* 1403 */       List<Long> records = (List)getContents().get(day + "." + start_offset);
/*      */       
/* 1405 */       if (records != null)
/*      */       {
/* 1407 */         long[] result = new long[6];
/*      */         
/* 1409 */         if (records.size() == 6)
/*      */         {
/* 1411 */           for (int i = 0; i < 6; i++)
/*      */           {
/* 1413 */             result[i] = ((Long)records.get(i)).longValue();
/*      */           }
/*      */         }
/*      */         
/* 1417 */         return result;
/*      */       }
/*      */       
/* 1420 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setTotals(int day, long[] totals)
/*      */     {
/* 1429 */       List<Long> records = new ArrayList();
/*      */       
/* 1431 */       long[] arr$ = totals;int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Long l = Long.valueOf(arr$[i$]);
/*      */         
/* 1433 */         records.add(l);
/*      */       }
/*      */       
/* 1436 */       getContents().put(String.valueOf(day), records);
/*      */       
/* 1438 */       this.dirty = true;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void setTotals(int day, long start_offset, long[] totals)
/*      */     {
/* 1447 */       if (start_offset == 0L)
/*      */       {
/* 1449 */         setTotals(day, totals);
/*      */       }
/*      */       else
/*      */       {
/* 1453 */         List<Long> records = new ArrayList();
/*      */         
/* 1455 */         long[] arr$ = totals;int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { Long l = Long.valueOf(arr$[i$]);
/*      */           
/* 1457 */           records.add(l);
/*      */         }
/*      */         
/* 1460 */         getContents().put(day + "." + start_offset, records);
/*      */         
/* 1462 */         this.dirty = true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private boolean isDirty()
/*      */     {
/* 1469 */       return this.dirty;
/*      */     }
/*      */     
/*      */ 
/*      */     private void save()
/*      */     {
/* 1475 */       File file = getCacheFile();
/*      */       
/* 1477 */       file.getParentFile().mkdirs();
/*      */       
/*      */ 
/*      */ 
/* 1481 */       FileUtil.writeResilientFile(file, this.contents);
/*      */       
/* 1483 */       this.dirty = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/*      */     try
/*      */     {
/* 1492 */       LongTermStatsImpl impl = new LongTermStatsImpl(new File("C:\\Test\\plus6b\\stats"));
/*      */       
/* 1494 */       SimpleDateFormat local_format = new SimpleDateFormat("yyyy,MM,dd");
/*      */       
/* 1496 */       Date start_date = local_format.parse("2013,07,10");
/* 1497 */       Date end_date = local_format.parse("2013,07,16");
/*      */       
/* 1499 */       long[] usage = impl.getTotalUsageInPeriod(start_date, end_date, new LongTermStats.RecordAccepter()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public boolean acceptRecord(long timestamp)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1509 */           System.out.println(new Date(timestamp));
/*      */           
/* 1511 */           return true;
/*      */         }
/*      */         
/* 1514 */       });
/* 1515 */       System.out.println(getString(usage));
/*      */       
/* 1517 */       System.out.println(getString(impl.getTotalUsageInPeriod(0, 1.0D)));
/* 1518 */       System.out.println(getString(impl.getTotalUsageInPeriod(10, 1.0D)));
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/* 1524 */       e.printStackTrace();
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
/*      */     //   5: getfield 739	org/gudy/azureus2/core3/stats/transfer/impl/LongTermStatsImpl:active	Z
/*      */     //   8: aload_1
/*      */     //   9: monitorexit
/*      */     //   10: ireturn
/*      */     //   11: astore_2
/*      */     //   12: aload_1
/*      */     //   13: monitorexit
/*      */     //   14: aload_2
/*      */     //   15: athrow
/*      */     // Line number table:
/*      */     //   Java source line #258	-> byte code offset #0
/*      */     //   Java source line #260	-> byte code offset #4
/*      */     //   Java source line #261	-> byte code offset #11
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	16	0	this	LongTermStatsImpl
/*      */     //   2	11	1	Ljava/lang/Object;	Object
/*      */     //   11	4	2	localObject1	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   4	10	11	finally
/*      */     //   11	14	11	finally
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/transfer/impl/LongTermStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */