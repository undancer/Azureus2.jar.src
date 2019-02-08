/*      */ package com.aelitis.azureus.core.speedmanager.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*      */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingZone;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class SpeedManagerPingMapperImpl
/*      */   implements SpeedManagerPingMapper
/*      */ {
/*      */   static final int VARIANCE_GOOD_VALUE = 50;
/*      */   static final int VARIANCE_BAD_VALUE = 150;
/*      */   static final int VARIANCE_MAX = 1500;
/*      */   static final int RTT_BAD_MIN = 350;
/*      */   static final int RTT_BAD_MAX = 500;
/*      */   static final int RTT_MAX = 30000;
/*      */   static final int MAX_BAD_LIMIT_HISTORY = 16;
/*      */   static final int SPEED_DIVISOR = 256;
/*      */   private static final int SPEED_HISTORY_PERIOD = 180000;
/*      */   private static final int SPEED_HISTORY_COUNT = 60;
/*      */   private final SpeedManagerImpl speed_manager;
/*      */   private final String name;
/*      */   private final boolean variance;
/*      */   private final boolean trans;
/*      */   private int ping_count;
/*      */   private pingValue[] pings;
/*      */   private final int max_pings;
/*      */   private pingValue prev_ping;
/*   79 */   private final int[] x_speeds = new int[60];
/*   80 */   private final int[] y_speeds = new int[60];
/*      */   
/*      */   private int speeds_next;
/*      */   
/*      */   private LinkedList regions;
/*      */   
/*      */   private int last_x;
/*      */   
/*      */   private int last_y;
/*   89 */   private final int[] recent_metrics = new int[3];
/*      */   
/*      */   private int recent_metrics_next;
/*      */   
/*      */   private limitEstimate up_estimate;
/*      */   
/*      */   private limitEstimate down_estimate;
/*      */   
/*      */   private LinkedList last_bad_ups;
/*      */   
/*      */   private LinkedList last_bad_downs;
/*      */   
/*      */   private static final int BAD_PROGRESS_COUNTDOWN = 5;
/*      */   
/*      */   private limitEstimate last_bad_up;
/*      */   private int bad_up_in_progress_count;
/*      */   private limitEstimate last_bad_down;
/*      */   private int bad_down_in_progress_count;
/*      */   private limitEstimate best_good_up;
/*      */   private limitEstimate best_good_down;
/*  109 */   private limitEstimate up_capacity = getNullLimit();
/*  110 */   private limitEstimate down_capacity = getNullLimit();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private File history_file;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SpeedManagerPingMapperImpl(SpeedManagerImpl _speed_manager, String _name, int _entries, boolean _variance, boolean _transient)
/*      */   {
/*  122 */     this.speed_manager = _speed_manager;
/*  123 */     this.name = _name;
/*  124 */     this.max_pings = _entries;
/*  125 */     this.variance = _variance;
/*  126 */     this.trans = _transient;
/*      */     
/*  128 */     init();
/*      */   }
/*      */   
/*      */ 
/*      */   protected synchronized void init()
/*      */   {
/*  134 */     this.pings = new pingValue[this.max_pings];
/*  135 */     this.ping_count = 0;
/*      */     
/*  137 */     this.regions = new LinkedList();
/*      */     
/*  139 */     this.up_estimate = getNullLimit();
/*  140 */     this.down_estimate = getNullLimit();
/*      */     
/*  142 */     this.last_bad_ups = new LinkedList();
/*  143 */     this.last_bad_downs = new LinkedList();
/*      */     
/*  145 */     this.last_bad_up = null;
/*  146 */     this.bad_up_in_progress_count = 0;
/*      */     
/*  148 */     this.last_bad_down = null;
/*  149 */     this.bad_down_in_progress_count = 0;
/*      */     
/*  151 */     this.best_good_up = null;
/*  152 */     this.best_good_down = null;
/*      */     
/*  154 */     this.up_capacity = getNullLimit();
/*  155 */     this.down_capacity = getNullLimit();
/*      */     
/*  157 */     this.prev_ping = null;
/*  158 */     this.recent_metrics_next = 0;
/*      */   }
/*      */   
/*      */ 
/*      */   protected synchronized void loadHistory(File file)
/*      */   {
/*      */     try
/*      */     {
/*  166 */       if ((this.history_file != null) && (this.history_file.equals(file)))
/*      */       {
/*  168 */         return;
/*      */       }
/*      */       
/*  171 */       if (this.history_file != null)
/*      */       {
/*  173 */         saveHistory();
/*      */       }
/*      */       
/*  176 */       this.history_file = file;
/*      */       
/*  178 */       init();
/*      */       
/*  180 */       if (this.history_file.exists())
/*      */       {
/*      */ 
/*      */ 
/*  184 */         Map map = FileUtil.readResilientFile(this.history_file.getParentFile(), this.history_file.getName(), false, false);
/*      */         
/*  186 */         List p = (List)map.get("pings");
/*      */         
/*  188 */         if (p != null)
/*      */         {
/*  190 */           for (int i = 0; i < p.size(); i++)
/*      */           {
/*  192 */             Map m = (Map)p.get(i);
/*      */             
/*  194 */             int x = ((Long)m.get("x")).intValue();
/*  195 */             int y = ((Long)m.get("y")).intValue();
/*  196 */             int metric = ((Long)m.get("m")).intValue();
/*      */             
/*  198 */             if (i == 0)
/*      */             {
/*  200 */               this.last_x = 0;
/*  201 */               this.last_y = 0;
/*      */             }
/*      */             
/*  204 */             if (this.variance)
/*      */             {
/*  206 */               if (metric > 1500)
/*      */               {
/*  208 */                 metric = 1500;
/*      */               }
/*      */               
/*      */             }
/*  212 */             else if (metric > 30000)
/*      */             {
/*  214 */               metric = 30000;
/*      */             }
/*      */             
/*      */ 
/*  218 */             addPingSupport(x, y, -1, metric);
/*      */           }
/*      */         }
/*      */         
/*  222 */         this.last_bad_ups = loadLimits(map, "lbus");
/*  223 */         this.last_bad_downs = loadLimits(map, "lbds");
/*      */         
/*  225 */         if (this.last_bad_ups.size() > 0)
/*      */         {
/*  227 */           this.last_bad_up = ((limitEstimate)this.last_bad_ups.get(this.last_bad_ups.size() - 1));
/*      */         }
/*      */         
/*  230 */         if (this.last_bad_downs.size() > 0)
/*      */         {
/*  232 */           this.last_bad_down = ((limitEstimate)this.last_bad_downs.get(this.last_bad_downs.size() - 1));
/*      */         }
/*      */         
/*  235 */         this.best_good_up = loadLimit((Map)map.get("bgu"));
/*  236 */         this.best_good_down = loadLimit((Map)map.get("bgd"));
/*      */         
/*  238 */         this.up_capacity = loadLimit((Map)map.get("upcap"));
/*  239 */         this.down_capacity = loadLimit((Map)map.get("downcap"));
/*      */         
/*  241 */         log("Loaded " + this.ping_count + " entries from " + this.history_file + ": bad_up=" + getLimitString(this.last_bad_ups) + ", bad_down=" + getLimitString(this.last_bad_downs));
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  248 */         setEstimatedUploadCapacityBytesPerSec(76800, 0.0F);
/*      */       }
/*      */       
/*  251 */       this.prev_ping = null;
/*  252 */       this.recent_metrics_next = 0;
/*      */       
/*  254 */       updateLimitEstimates();
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  258 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */   protected synchronized void saveHistory()
/*      */   {
/*      */     try
/*      */     {
/*  266 */       if (this.history_file == null)
/*      */       {
/*  268 */         return;
/*      */       }
/*      */       
/*  271 */       Map map = new HashMap();
/*      */       
/*  273 */       List p = new ArrayList(this.ping_count);
/*      */       
/*      */ 
/*      */ 
/*  277 */       map.put("pings", p);
/*      */       
/*  279 */       for (int i = 0; i < this.ping_count; i++)
/*      */       {
/*  281 */         pingValue ping = this.pings[i];
/*      */         
/*  283 */         Map m = new HashMap();
/*      */         
/*  285 */         p.add(m);
/*      */         
/*  287 */         m.put("x", new Long(ping.getX()));
/*  288 */         m.put("y", new Long(ping.getY()));
/*  289 */         m.put("m", new Long(ping.getMetric()));
/*      */       }
/*      */       
/*  292 */       saveLimits(map, "lbus", this.last_bad_ups);
/*  293 */       saveLimits(map, "lbds", this.last_bad_downs);
/*      */       
/*  295 */       if (this.best_good_up != null)
/*      */       {
/*  297 */         map.put("bgu", saveLimit(this.best_good_up));
/*      */       }
/*      */       
/*  300 */       if (this.best_good_down != null)
/*      */       {
/*  302 */         map.put("bgd", saveLimit(this.best_good_down));
/*      */       }
/*      */       
/*  305 */       map.put("upcap", saveLimit(this.up_capacity));
/*  306 */       map.put("downcap", saveLimit(this.down_capacity));
/*      */       
/*  308 */       FileUtil.writeResilientFile(this.history_file, map);
/*      */       
/*  310 */       log("Saved " + p.size() + " entries to " + this.history_file);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  314 */       Debug.printStackTrace(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected LinkedList loadLimits(Map map, String name)
/*      */   {
/*  323 */     LinkedList result = new LinkedList();
/*      */     
/*  325 */     List l = (List)map.get(name);
/*      */     
/*  327 */     if (l != null)
/*      */     {
/*  329 */       for (int i = 0; i < l.size(); i++)
/*      */       {
/*  331 */         Map m = (Map)l.get(i);
/*      */         
/*  333 */         result.add(loadLimit(m));
/*      */       }
/*      */     }
/*      */     
/*  337 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected limitEstimate loadLimit(Map m)
/*      */   {
/*  344 */     if (m == null)
/*      */     {
/*  346 */       return getNullLimit();
/*      */     }
/*      */     
/*  349 */     int speed = ((Long)m.get("s")).intValue();
/*      */     
/*  351 */     double metric = Double.parseDouble(new String((byte[])m.get("m")));
/*      */     
/*  353 */     int hits = ((Long)m.get("h")).intValue();
/*      */     
/*  355 */     long when = ((Long)m.get("w")).longValue();
/*      */     
/*  357 */     byte[] t_bytes = (byte[])m.get("t");
/*      */     
/*  359 */     double type = t_bytes == null ? 0.0D : Double.parseDouble(new String(t_bytes));
/*      */     
/*  361 */     return new limitEstimate(speed, type, metric, hits, when, new int[0][]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void saveLimits(Map map, String name, List limits)
/*      */   {
/*  370 */     List l = new ArrayList();
/*      */     
/*  372 */     for (int i = 0; i < limits.size(); i++)
/*      */     {
/*  374 */       limitEstimate limit = (limitEstimate)limits.get(i);
/*      */       
/*  376 */       Map m = saveLimit(limit);
/*      */       
/*  378 */       l.add(m);
/*      */     }
/*      */     
/*  381 */     map.put(name, l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Map saveLimit(limitEstimate limit)
/*      */   {
/*  388 */     if (limit == null)
/*      */     {
/*  390 */       limit = getNullLimit();
/*      */     }
/*      */     
/*  393 */     Map m = new HashMap();
/*      */     
/*  395 */     m.put("s", new Long(limit.getBytesPerSec()));
/*      */     
/*  397 */     m.put("m", String.valueOf(limit.getMetricRating()));
/*      */     
/*  399 */     m.put("t", String.valueOf(limit.getEstimateType()));
/*      */     
/*  401 */     m.put("h", new Long(limit.getHits()));
/*      */     
/*  403 */     m.put("w", new Long(limit.getWhen()));
/*      */     
/*  405 */     return m;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isActive()
/*      */   {
/*  411 */     return this.variance;
/*      */   }
/*      */   
/*      */ 
/*      */   protected limitEstimate getNullLimit()
/*      */   {
/*  417 */     return new limitEstimate(0, -0.10000000149011612D, 0.0D, 0, 0L, new int[0][]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getLimitString(List limits)
/*      */   {
/*  424 */     String str = "";
/*      */     
/*  426 */     for (int i = 0; i < limits.size(); i++)
/*      */     {
/*  428 */       str = str + (i == 0 ? "" : ",") + ((limitEstimate)limits.get(i)).getString();
/*      */     }
/*      */     
/*  431 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/*  438 */     if (this.speed_manager != null)
/*      */     {
/*  440 */       this.speed_manager.log(str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/*  447 */     return this.name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void addSpeed(int x, int y)
/*      */   {
/*  455 */     x /= 256;
/*  456 */     y /= 256;
/*      */     
/*  458 */     if (x > 65535) x = 65535;
/*  459 */     if (y > 65535) { y = 65535;
/*      */     }
/*  461 */     addSpeedSupport(x, y);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void addSpeedSupport(int x, int y)
/*      */   {
/*  469 */     this.x_speeds[this.speeds_next] = x;
/*  470 */     this.y_speeds[this.speeds_next] = y;
/*      */     
/*  472 */     this.speeds_next = ((this.speeds_next + 1) % 60);
/*      */     
/*  474 */     int min_x = Integer.MAX_VALUE;
/*  475 */     int min_y = Integer.MAX_VALUE;
/*      */     
/*  477 */     for (int i = 0; i < 60; i++)
/*      */     {
/*  479 */       min_x = Math.min(min_x, this.x_speeds[i]);
/*  480 */       min_y = Math.min(min_y, this.y_speeds[i]);
/*      */     }
/*      */     
/*  483 */     min_x *= 256;
/*  484 */     min_y *= 256;
/*      */     
/*  486 */     if (this.up_capacity.getEstimateType() != 1.0F)
/*      */     {
/*  488 */       if (min_x > this.up_capacity.getBytesPerSec())
/*      */       {
/*  490 */         this.up_capacity.setBytesPerSec(min_x);
/*      */         
/*  492 */         this.up_capacity.setMetricRating(0.0F);
/*      */         
/*  494 */         this.up_capacity.setEstimateType(0.0F);
/*      */         
/*  496 */         this.speed_manager.informUpCapChanged();
/*      */       }
/*      */     }
/*      */     
/*  500 */     if (this.down_capacity.getEstimateType() != 1.0F)
/*      */     {
/*  502 */       if (min_y > this.down_capacity.getBytesPerSec())
/*      */       {
/*  504 */         this.down_capacity.setBytesPerSec(min_y);
/*      */         
/*  506 */         this.down_capacity.setMetricRating(0.0F);
/*      */         
/*  508 */         this.down_capacity.setEstimateType(0.0F);
/*      */         
/*  510 */         this.speed_manager.informDownCapChanged();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void addPing(int x, int y, int rtt, boolean re_base)
/*      */   {
/*  522 */     x /= 256;
/*  523 */     y /= 256;
/*      */     
/*  525 */     if (x > 65535) x = 65535;
/*  526 */     if (y > 65535) y = 65535;
/*  527 */     if (rtt > 65535) rtt = this.variance ? 1500 : 30000;
/*  528 */     if (rtt == 0) { rtt = 1;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  533 */     int average_x = (x + this.last_x) / 2;
/*  534 */     int average_y = (y + this.last_y) / 2;
/*      */     
/*  536 */     this.last_x = x;
/*  537 */     this.last_y = y;
/*      */     
/*  539 */     x = average_x;
/*  540 */     y = average_y;
/*      */     
/*      */     int metric;
/*      */     
/*  544 */     if (this.variance)
/*      */     {
/*  546 */       if (re_base)
/*      */       {
/*  548 */         log("Re-based variance");
/*      */         
/*  550 */         this.recent_metrics_next = 0;
/*      */       }
/*      */       
/*  553 */       this.recent_metrics[(this.recent_metrics_next++ % this.recent_metrics.length)] = rtt;
/*      */       
/*  555 */       int var_metric = 0;
/*  556 */       int rtt_metric = 0;
/*      */       
/*  558 */       if (this.recent_metrics_next > 1)
/*      */       {
/*  560 */         int entries = Math.min(this.recent_metrics_next, this.recent_metrics.length);
/*      */         
/*  562 */         int total = 0;
/*      */         
/*  564 */         for (int i = 0; i < entries; i++)
/*      */         {
/*  566 */           total += this.recent_metrics[i];
/*      */         }
/*      */         
/*  569 */         int average = total / entries;
/*      */         
/*  571 */         int total_deviation = 0;
/*      */         
/*  573 */         for (int i = 0; i < entries; i++)
/*      */         {
/*  575 */           int deviation = this.recent_metrics[i] - average;
/*      */           
/*  577 */           total_deviation += deviation * deviation;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  582 */         var_metric = (int)Math.sqrt(total_deviation);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  588 */         if (entries == this.recent_metrics.length)
/*      */         {
/*  590 */           int total_rtt = 0;
/*      */           
/*  592 */           for (int i = 0; i < entries; i++)
/*      */           {
/*  594 */             total_rtt += this.recent_metrics[i];
/*      */           }
/*      */           
/*  597 */           int average_rtt = total_rtt / this.recent_metrics.length;
/*      */           
/*  599 */           if (average_rtt >= 500)
/*      */           {
/*  601 */             rtt_metric = 150;
/*      */           }
/*  603 */           else if (average_rtt > 350)
/*      */           {
/*  605 */             int rtt_diff = 150;
/*  606 */             int rtt_base = average_rtt - 350;
/*      */             
/*  608 */             rtt_metric = 50 + 100 * rtt_base / rtt_diff;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  613 */       int metric = Math.max(var_metric, rtt_metric);
/*      */       
/*  615 */       if (metric < 150)
/*      */       {
/*  617 */         addSpeedSupport(x, y);
/*      */       }
/*      */       else
/*      */       {
/*  621 */         addSpeedSupport(0, 0);
/*      */       }
/*      */     }
/*      */     else {
/*  625 */       metric = rtt;
/*      */     }
/*      */     
/*  628 */     region new_region = addPingSupport(x, y, rtt, metric);
/*      */     
/*  630 */     updateLimitEstimates();
/*      */     
/*  632 */     if (this.variance)
/*      */     {
/*  634 */       String up_e = getShortString(getEstimatedUploadLimit(false)) + "," + getShortString(getEstimatedUploadLimit(true)) + "," + getShortString(getEstimatedUploadCapacityBytesPerSec());
/*      */       
/*      */ 
/*      */ 
/*  638 */       String down_e = getShortString(getEstimatedDownloadLimit(false)) + "," + getShortString(getEstimatedDownloadLimit(true)) + "," + getShortString(getEstimatedDownloadCapacityBytesPerSec());
/*      */       
/*      */ 
/*      */ 
/*  642 */       log("Ping: rtt=" + rtt + ",x=" + x + ",y=" + y + ",m=" + metric + (new_region == null ? "" : new StringBuilder().append(",region=").append(new_region.getString()).toString()) + ",mr=" + getCurrentMetricRating() + ",up=[" + up_e + (this.best_good_up == null ? "" : new StringBuilder().append(":").append(getShortString(this.best_good_up)).toString()) + "],down=[" + down_e + (this.best_good_down == null ? "" : new StringBuilder().append(":").append(getShortString(this.best_good_down)).toString()) + "]" + ",bu=" + getLimitStr(this.last_bad_ups, true) + ",bd=" + getLimitStr(this.last_bad_downs, true));
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
/*      */   protected region addPingSupport(int x, int y, int rtt, int metric)
/*      */   {
/*  658 */     if (this.ping_count == this.pings.length)
/*      */     {
/*      */ 
/*      */ 
/*  662 */       int to_discard = this.pings.length / 10;
/*      */       
/*  664 */       if (to_discard < 3)
/*      */       {
/*  666 */         to_discard = 3;
/*      */       }
/*      */       
/*  669 */       this.ping_count = (this.pings.length - to_discard);
/*      */       
/*  671 */       System.arraycopy(this.pings, to_discard, this.pings, 0, this.ping_count);
/*      */       
/*  673 */       for (int i = 0; i < to_discard; i++)
/*      */       {
/*  675 */         this.regions.removeFirst();
/*      */       }
/*      */     }
/*      */     
/*  679 */     pingValue ping = new pingValue(x, y, metric);
/*      */     
/*  681 */     this.pings[(this.ping_count++)] = ping;
/*      */     
/*  683 */     region new_region = null;
/*      */     
/*  685 */     if (this.prev_ping != null)
/*      */     {
/*  687 */       new_region = new region(this.prev_ping, ping);
/*      */       
/*  689 */       this.regions.add(new_region);
/*      */     }
/*      */     
/*  692 */     this.prev_ping = ping;
/*      */     
/*  694 */     return new_region;
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized int[][] getHistory()
/*      */   {
/*  700 */     int[][] result = new int[this.ping_count][];
/*      */     
/*  702 */     for (int i = 0; i < this.ping_count; i++)
/*      */     {
/*  704 */       pingValue ping = this.pings[i];
/*      */       
/*  706 */       result[i] = { 256 * ping.getX(), 256 * ping.getY(), ping.getMetric() };
/*      */     }
/*      */     
/*  709 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized SpeedManagerPingZone[] getZones()
/*      */   {
/*  715 */     return (SpeedManagerPingZone[])this.regions.toArray(new SpeedManagerPingZone[this.regions.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized SpeedManagerLimitEstimate getEstimatedUploadLimit(boolean persistent)
/*      */   {
/*  722 */     return adjustForPersistence(this.up_estimate, this.best_good_up, this.last_bad_up, persistent);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized SpeedManagerLimitEstimate getEstimatedDownloadLimit(boolean persistent)
/*      */   {
/*  729 */     return adjustForPersistence(this.down_estimate, this.best_good_down, this.last_bad_down, persistent);
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerLimitEstimate getLastBadUploadLimit()
/*      */   {
/*  735 */     return this.last_bad_up;
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerLimitEstimate getLastBadDownloadLimit()
/*      */   {
/*  741 */     return this.last_bad_down;
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized SpeedManagerLimitEstimate[] getBadUploadHistory()
/*      */   {
/*  747 */     return (SpeedManagerLimitEstimate[])this.last_bad_ups.toArray(new SpeedManagerLimitEstimate[this.last_bad_ups.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized SpeedManagerLimitEstimate[] getBadDownloadHistory()
/*      */   {
/*  753 */     return (SpeedManagerLimitEstimate[])this.last_bad_downs.toArray(new SpeedManagerLimitEstimate[this.last_bad_downs.size()]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SpeedManagerLimitEstimate adjustForPersistence(limitEstimate estimate, limitEstimate best_good, limitEstimate last_bad, boolean persistent)
/*      */   {
/*  763 */     if (estimate == null)
/*      */     {
/*  765 */       return null;
/*      */     }
/*      */     
/*  768 */     if (persistent)
/*      */     {
/*      */ 
/*      */ 
/*  772 */       if (estimate.getMetricRating() == -1.0F)
/*      */       {
/*  774 */         return estimate;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  779 */       limitEstimate persistent_limit = null;
/*      */       
/*  781 */       if ((best_good != null) && (last_bad != null))
/*      */       {
/*  783 */         if (last_bad.getWhen() > best_good.getWhen())
/*      */         {
/*  785 */           persistent_limit = last_bad;
/*      */ 
/*      */ 
/*      */         }
/*  789 */         else if (best_good.getBytesPerSec() > last_bad.getBytesPerSec())
/*      */         {
/*  791 */           persistent_limit = best_good;
/*      */         }
/*      */         else
/*      */         {
/*  795 */           persistent_limit = last_bad;
/*      */         }
/*      */       }
/*  798 */       else if (best_good != null)
/*      */       {
/*  800 */         persistent_limit = best_good;
/*      */       }
/*  802 */       else if (last_bad != null)
/*      */       {
/*  804 */         persistent_limit = last_bad;
/*      */       }
/*      */       
/*  807 */       if (persistent_limit == null)
/*      */       {
/*  809 */         return estimate;
/*      */       }
/*      */       
/*  812 */       if (estimate.getBytesPerSec() > persistent_limit.getBytesPerSec())
/*      */       {
/*  814 */         return estimate;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  821 */       limitEstimate res = estimate.getClone();
/*      */       
/*  823 */       res.setBytesPerSec(persistent_limit.getBytesPerSec());
/*      */       
/*  825 */       return res;
/*      */     }
/*      */     
/*      */ 
/*  829 */     return estimate;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void updateLimitEstimates()
/*      */   {
/*  836 */     double cm = getCurrentMetricRating();
/*      */     
/*  838 */     this.up_estimate = getEstimatedLimit(true);
/*      */     
/*  840 */     if (this.up_estimate != null)
/*      */     {
/*  842 */       double metric = this.up_estimate.getMetricRating();
/*      */       
/*  844 */       if (metric == -1.0D)
/*      */       {
/*  846 */         if (this.bad_up_in_progress_count == 0)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  851 */           if ((this.last_bad_up == null) || (this.last_bad_up.getBytesPerSec() != this.up_estimate.getBytesPerSec()))
/*      */           {
/*  853 */             this.bad_up_in_progress_count = 5;
/*      */             
/*  855 */             this.last_bad_ups.addLast(this.up_estimate);
/*      */             
/*  857 */             if (this.last_bad_ups.size() > 16)
/*      */             {
/*  859 */               this.last_bad_ups.removeFirst();
/*      */             }
/*      */             
/*  862 */             checkCapacityDecrease(true, this.up_capacity, this.last_bad_ups);
/*      */           }
/*      */         }
/*      */         
/*  866 */         this.last_bad_up = this.up_estimate;
/*      */       }
/*  868 */       else if (metric == 1.0D)
/*      */       {
/*  870 */         if (this.best_good_up == null)
/*      */         {
/*  872 */           this.best_good_up = this.up_estimate;
/*      */ 
/*      */ 
/*      */         }
/*  876 */         else if (this.best_good_up.getBytesPerSec() < this.up_estimate.getBytesPerSec())
/*      */         {
/*  878 */           this.best_good_up = this.up_estimate;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  883 */       if (this.bad_up_in_progress_count > 0)
/*      */       {
/*  885 */         if (cm == -1.0D)
/*      */         {
/*  887 */           this.bad_up_in_progress_count = 5;
/*      */         }
/*  889 */         else if (cm == 1.0D)
/*      */         {
/*  891 */           this.bad_up_in_progress_count -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  897 */     this.down_estimate = getEstimatedLimit(false);
/*      */     
/*  899 */     if (this.down_estimate != null)
/*      */     {
/*  901 */       double metric = this.down_estimate.getMetricRating();
/*      */       
/*  903 */       if (metric == -1.0D)
/*      */       {
/*  905 */         if (this.bad_down_in_progress_count == 0)
/*      */         {
/*  907 */           if ((this.last_bad_down == null) || (this.last_bad_down.getBytesPerSec() != this.down_estimate.getBytesPerSec()))
/*      */           {
/*  909 */             this.bad_down_in_progress_count = 5;
/*      */             
/*  911 */             this.last_bad_downs.addLast(this.down_estimate);
/*      */             
/*  913 */             if (this.last_bad_downs.size() > 16)
/*      */             {
/*  915 */               this.last_bad_downs.removeFirst();
/*      */             }
/*      */             
/*  918 */             checkCapacityDecrease(false, this.down_capacity, this.last_bad_downs);
/*      */           }
/*      */         }
/*      */         
/*  922 */         this.last_bad_down = this.down_estimate;
/*      */       }
/*  924 */       else if (metric == 1.0D)
/*      */       {
/*  926 */         if (this.best_good_down == null)
/*      */         {
/*  928 */           this.best_good_down = this.down_estimate;
/*      */ 
/*      */ 
/*      */         }
/*  932 */         else if (this.best_good_down.getBytesPerSec() < this.down_estimate.getBytesPerSec())
/*      */         {
/*  934 */           this.best_good_down = this.down_estimate;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  939 */       if (this.bad_down_in_progress_count > 0)
/*      */       {
/*  941 */         if (cm == -1.0D)
/*      */         {
/*  943 */           this.bad_down_in_progress_count = 5;
/*      */         }
/*  945 */         else if (cm == 1.0D)
/*      */         {
/*  947 */           this.bad_down_in_progress_count -= 1;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void checkCapacityDecrease(boolean is_up, limitEstimate capacity, LinkedList bads)
/*      */   {
/*  959 */     if (capacity.getEstimateType() == 1.0F)
/*      */     {
/*  961 */       return;
/*      */     }
/*      */     
/*  964 */     if (bads.size() < 16)
/*      */     {
/*  966 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  971 */     int cap = capacity.getBytesPerSec();
/*      */     
/*      */ 
/*      */ 
/*  975 */     if ((cap > 0) && (cap < 10240))
/*      */     {
/*  977 */       return;
/*      */     }
/*      */     
/*  980 */     List b = new ArrayList(bads);
/*      */     
/*  982 */     Collections.sort(b, new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(Object o1, Object o2)
/*      */       {
/*      */ 
/*      */ 
/*  991 */         SpeedManagerPingMapperImpl.limitEstimate l1 = (SpeedManagerPingMapperImpl.limitEstimate)o1;
/*  992 */         SpeedManagerPingMapperImpl.limitEstimate l2 = (SpeedManagerPingMapperImpl.limitEstimate)o2;
/*      */         
/*  994 */         return l1.getBytesPerSec() - l2.getBytesPerSec();
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*  999 */     });
/* 1000 */     int start = 4;
/* 1001 */     int end = 16 - start;
/*      */     
/* 1003 */     int total = 0;
/* 1004 */     int num = 0;
/*      */     
/* 1006 */     for (int i = start; i < end; i++)
/*      */     {
/* 1008 */       int s = ((limitEstimate)b.get(i)).getBytesPerSec();
/*      */       
/* 1010 */       total += s;
/*      */       
/* 1012 */       num++;
/*      */     }
/*      */     
/* 1015 */     int average = total / num;
/*      */     
/*      */ 
/*      */ 
/* 1019 */     if ((cap > 0) && (average >= cap))
/*      */     {
/* 1021 */       log("Not reducing " + (is_up ? "up" : "down") + " capacity - average=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(average) + ",capacity=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(cap));
/*      */       
/* 1023 */       return;
/*      */     }
/*      */     
/* 1026 */     int total_deviation = 0;
/*      */     
/* 1028 */     for (int i = start; i < end; i++)
/*      */     {
/* 1030 */       int s = ((limitEstimate)b.get(i)).getBytesPerSec();
/*      */       
/* 1032 */       int deviation = s - average;
/*      */       
/* 1034 */       total_deviation += deviation * deviation;
/*      */     }
/*      */     
/* 1037 */     int deviation = (int)Math.sqrt(total_deviation / num);
/*      */     
/*      */ 
/*      */ 
/* 1041 */     if ((cap <= 0) || ((deviation < cap / 2) && (average < cap)))
/*      */     {
/* 1043 */       log("Reducing " + (is_up ? "up" : "down") + " capacity from " + cap + " to " + average + " due to frequent lower chokes (deviation=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(deviation) + ")");
/*      */       
/* 1045 */       capacity.setBytesPerSec(average);
/*      */       
/* 1047 */       capacity.setEstimateType(0.5F);
/*      */       
/*      */ 
/*      */ 
/* 1051 */       for (int i = 0; i < start; i++)
/*      */       {
/* 1053 */         bads.removeFirst();
/*      */       }
/*      */     }
/*      */     else {
/* 1057 */       log("Not reducing " + (is_up ? "up" : "down") + " capacity - deviation=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(deviation) + ",capacity=" + DisplayFormatters.formatByteCountToKiBEtcPerSec(cap));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized limitEstimate getEstimatedLimit(boolean up)
/*      */   {
/* 1066 */     if (!this.variance)
/*      */     {
/* 1068 */       return getNullLimit();
/*      */     }
/*      */     
/* 1071 */     int num_samples = this.regions.size();
/*      */     
/* 1073 */     if (num_samples == 0)
/*      */     {
/* 1075 */       return getNullLimit();
/*      */     }
/*      */     
/* 1078 */     Iterator it = this.regions.iterator();
/*      */     
/* 1080 */     int max_end = 0;
/*      */     
/* 1082 */     while (it.hasNext())
/*      */     {
/* 1084 */       region r = (region)it.next();
/*      */       
/* 1086 */       int end = (up ? r.getUploadEndBytesPerSec() : r.getDownloadEndBytesPerSec()) / 256;
/*      */       
/* 1088 */       if (end > max_end)
/*      */       {
/* 1090 */         max_end = end;
/*      */       }
/*      */     }
/*      */     
/* 1094 */     int sample_end = max_end + 1;
/*      */     
/* 1096 */     int[] totals = new int[sample_end];
/* 1097 */     short[] hits = new short[sample_end];
/* 1098 */     short[] worst_var_type = new short[sample_end];
/*      */     
/* 1100 */     ListIterator sample_it = this.regions.listIterator(0);
/*      */     
/*      */ 
/*      */ 
/* 1104 */     while (sample_it.hasNext())
/*      */     {
/* 1106 */       region r = (region)sample_it.next();
/*      */       
/* 1108 */       int start = (up ? r.getUploadStartBytesPerSec() : r.getDownloadStartBytesPerSec()) / 256;
/* 1109 */       int end = (up ? r.getUploadEndBytesPerSec() : r.getDownloadEndBytesPerSec()) / 256;
/* 1110 */       int metric = r.getMetric();
/*      */       
/*      */       short this_var_type;
/*      */       
/*      */       int weighted_start;
/*      */       int weighted_end;
/*      */       short this_var_type;
/* 1117 */       if (metric < 50)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1123 */         int weighted_start = 0;
/* 1124 */         int weighted_end = end;
/* 1125 */         this_var_type = 0;
/*      */       } else { short this_var_type;
/* 1127 */         if (metric < 150)
/*      */         {
/*      */ 
/*      */ 
/* 1131 */           int weighted_start = start;
/* 1132 */           int weighted_end = end;
/* 1133 */           this_var_type = 50;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1139 */           weighted_start = start;
/* 1140 */           weighted_end = max_end;
/* 1141 */           this_var_type = 150;
/*      */         }
/*      */       }
/* 1144 */       for (int j = weighted_start; j <= weighted_end; j++)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1150 */         if ((this_var_type == 150) && (worst_var_type[j] <= this_var_type))
/*      */         {
/* 1152 */           totals[j] = 0;
/* 1153 */           hits[j] = 0;
/*      */           
/* 1155 */           worst_var_type[j] = this_var_type;
/*      */         }
/*      */         
/* 1158 */         totals[j] += metric; int 
/* 1159 */           tmp324_322 = j; short[] tmp324_320 = hits;tmp324_320[tmp324_322] = ((short)(tmp324_320[tmp324_322] + 1));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1165 */     for (int i = 0; i < sample_end; i++)
/*      */     {
/* 1167 */       int hit = hits[i];
/*      */       
/* 1169 */       if (hit > 0)
/*      */       {
/* 1171 */         int average = totals[i] / hit;
/*      */         
/* 1173 */         totals[i] = average;
/*      */         
/* 1175 */         if (average < 50)
/*      */         {
/* 1177 */           worst_var_type[i] = 0;
/*      */         }
/* 1179 */         else if (average < 150)
/*      */         {
/* 1181 */           worst_var_type[i] = 50;
/*      */         }
/*      */         else
/*      */         {
/* 1185 */           worst_var_type[i] = 150;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1192 */     int last_average = -1;
/* 1193 */     int last_average_change = 0;
/* 1194 */     int last_average_worst_var = 0;
/* 1195 */     int last_max_hits = 0;
/*      */     
/* 1197 */     int worst_var = 0;
/*      */     
/* 1199 */     List segments = new ArrayList(totals.length);
/*      */     
/* 1201 */     for (int i = 0; i < sample_end; i++)
/*      */     {
/* 1203 */       int var = worst_var_type[i];
/* 1204 */       int hit = hits[i];
/*      */       
/* 1206 */       if (var > worst_var)
/*      */       {
/* 1208 */         worst_var = var;
/*      */       }
/*      */       
/* 1211 */       int average = totals[i];
/*      */       
/* 1213 */       if (i == 0)
/*      */       {
/* 1215 */         last_average = average;
/*      */       }
/* 1217 */       else if (last_average != average)
/*      */       {
/* 1219 */         segments.add(new int[] { last_average, last_average_change * 256, (i - 1) * 256, last_average_worst_var, last_max_hits });
/*      */         
/* 1221 */         last_average = average;
/* 1222 */         last_average_change = i;
/* 1223 */         last_average_worst_var = var;
/* 1224 */         last_max_hits = hit;
/*      */       }
/*      */       else {
/* 1227 */         last_average_worst_var = Math.max(var, last_average_worst_var);
/* 1228 */         last_max_hits = Math.max(hit, last_max_hits);
/*      */       }
/*      */     }
/*      */     
/* 1232 */     if (last_average_change != sample_end - 1)
/*      */     {
/* 1234 */       segments.add(new int[] { last_average, last_average_change * 256, (sample_end - 1) * 256, last_average_worst_var, last_max_hits });
/*      */     }
/*      */     
/* 1237 */     int[] estimate_seg = null;
/*      */     
/* 1239 */     int estimate_var = 0;
/*      */     
/*      */ 
/*      */ 
/* 1243 */     if (worst_var == 150)
/*      */     {
/* 1245 */       for (int i = segments.size() - 1; i >= 0; i--)
/*      */       {
/* 1247 */         int[] seg = (int[])segments.get(i);
/*      */         
/* 1249 */         int var = seg[3];
/*      */         
/* 1251 */         if (var >= worst_var)
/*      */         {
/* 1253 */           estimate_seg = seg;
/* 1254 */           estimate_var = var;
/*      */         }
/*      */       }
/*      */     } else {
/* 1258 */       for (int i = 0; i < segments.size(); i++)
/*      */       {
/* 1260 */         int[] seg = (int[])segments.get(i);
/*      */         
/* 1262 */         int var = seg[3];
/*      */         
/* 1264 */         if (var >= worst_var)
/*      */         {
/* 1266 */           estimate_seg = seg;
/* 1267 */           estimate_var = var;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     int estimate_hits;
/*      */     int estimate_speed;
/*      */     int estimate_hits;
/* 1275 */     if (estimate_seg == null)
/*      */     {
/* 1277 */       int estimate_speed = -1;
/* 1278 */       estimate_hits = 0;
/*      */     }
/*      */     else
/*      */     {
/* 1282 */       estimate_speed = -1;
/*      */       
/* 1284 */       if (worst_var == 0)
/*      */       {
/* 1286 */         estimate_speed = estimate_seg[2];
/*      */       }
/* 1288 */       else if (worst_var == 50)
/*      */       {
/* 1290 */         estimate_speed = (estimate_seg[1] + estimate_seg[2]) / 2;
/*      */       }
/*      */       else
/*      */       {
/* 1294 */         estimate_speed = estimate_seg[1];
/*      */       }
/*      */       
/* 1297 */       estimate_hits = estimate_seg[4];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1303 */     if (estimate_speed < 5120)
/*      */     {
/* 1305 */       estimate_var = 50;
/*      */       
/*      */ 
/*      */ 
/* 1309 */       if (estimate_speed <= 0)
/*      */       {
/* 1311 */         estimate_speed = 1;
/*      */       }
/*      */     }
/*      */     
/* 1315 */     limitEstimate result = new limitEstimate(estimate_speed, 0.0D, convertMetricToRating(estimate_var), estimate_hits, SystemTime.getCurrentTime(), (int[][])segments.toArray(new int[segments.size()][]));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1324 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public synchronized double getCurrentMetricRating()
/*      */   {
/* 1330 */     if (this.ping_count == 0)
/*      */     {
/* 1332 */       return 0.0D;
/*      */     }
/*      */     
/* 1335 */     int latest_metric = this.pings[(this.ping_count - 1)].getMetric();
/*      */     
/* 1337 */     if (this.variance)
/*      */     {
/* 1339 */       return convertMetricToRating(latest_metric);
/*      */     }
/*      */     
/*      */ 
/* 1343 */     return 0.0D;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public SpeedManagerLimitEstimate getEstimatedUploadCapacityBytesPerSec()
/*      */   {
/* 1350 */     return this.up_capacity;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEstimatedDownloadCapacityBytesPerSec(int bytes_per_sec, float estimate_type)
/*      */   {
/* 1358 */     if ((this.down_capacity.getBytesPerSec() != bytes_per_sec) || (this.down_capacity.getEstimateType() != estimate_type))
/*      */     {
/* 1360 */       this.down_capacity.setBytesPerSec(bytes_per_sec);
/* 1361 */       this.down_capacity.setEstimateType(estimate_type);
/*      */       
/* 1363 */       this.speed_manager.informDownCapChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public SpeedManagerLimitEstimate getEstimatedDownloadCapacityBytesPerSec()
/*      */   {
/* 1370 */     return this.down_capacity;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setEstimatedUploadCapacityBytesPerSec(int bytes_per_sec, float estimate_type)
/*      */   {
/* 1378 */     if ((this.up_capacity.getBytesPerSec() != bytes_per_sec) || (this.up_capacity.getEstimateType() != estimate_type))
/*      */     {
/* 1380 */       this.up_capacity.setBytesPerSec(bytes_per_sec);
/* 1381 */       this.up_capacity.setEstimateType(estimate_type);
/*      */       
/* 1383 */       this.speed_manager.informUpCapChanged();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected synchronized void reset()
/*      */   {
/* 1390 */     setEstimatedDownloadCapacityBytesPerSec(0, -0.1F);
/* 1391 */     setEstimatedUploadCapacityBytesPerSec(0, -0.1F);
/*      */     
/* 1393 */     this.ping_count = 0;
/* 1394 */     this.regions.clear();
/*      */     
/* 1396 */     this.last_bad_down = null;
/* 1397 */     this.last_bad_downs.clear();
/*      */     
/* 1399 */     this.last_bad_up = null;
/* 1400 */     this.last_bad_ups.clear();
/*      */     
/* 1402 */     saveHistory();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected double convertMetricToRating(int metric)
/*      */   {
/* 1409 */     if (metric < 50)
/*      */     {
/* 1411 */       return 1.0D;
/*      */     }
/* 1413 */     if (metric >= 150)
/*      */     {
/* 1415 */       return -1.0D;
/*      */     }
/*      */     
/*      */ 
/* 1419 */     double val = 1.0D - (metric - 50.0D) / 50.0D;
/*      */     
/*      */ 
/*      */ 
/* 1423 */     if (val < -1.0D)
/*      */     {
/* 1425 */       val = -1.0D;
/*      */     }
/* 1427 */     else if (val > 1.0D)
/*      */     {
/* 1429 */       val = 1.0D;
/*      */     }
/*      */     
/* 1432 */     return val;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getLimitStr(List limits, boolean short_form)
/*      */   {
/* 1441 */     String str = "";
/*      */     
/* 1443 */     if (limits != null)
/*      */     {
/* 1445 */       Iterator it = limits.iterator();
/*      */       
/* 1447 */       while (it.hasNext())
/*      */       {
/* 1449 */         str = str + (str.length() == 0 ? "" : ",");
/*      */         
/* 1451 */         limitEstimate l = (limitEstimate)it.next();
/*      */         
/* 1453 */         if (short_form) {
/* 1454 */           str = str + getShortString(l);
/*      */         } else {
/* 1456 */           str = str + l.getString();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1461 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getShortString(SpeedManagerLimitEstimate l)
/*      */   {
/* 1468 */     return DisplayFormatters.formatByteCountToKiBEtcPerSec(l.getBytesPerSec());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void generateEvidence(IndentWriter writer)
/*      */   {
/* 1475 */     writer.println("up_cap=" + this.up_capacity.getString());
/* 1476 */     writer.println("down_cap=" + this.down_capacity.getString());
/*      */     
/* 1478 */     writer.println("bad_up=" + getLimitStr(this.last_bad_ups, false));
/* 1479 */     writer.println("bad_down=" + getLimitStr(this.last_bad_downs, false));
/*      */     
/* 1481 */     if (this.best_good_up != null) {
/* 1482 */       writer.println("best_up=" + this.best_good_up.getString());
/*      */     }
/* 1484 */     if (this.best_good_down != null) {
/* 1485 */       writer.println("best_down=" + this.best_good_down.getString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void destroy()
/*      */   {
/* 1492 */     if (this.trans)
/*      */     {
/* 1494 */       this.speed_manager.destroy(this);
/*      */     }
/*      */     else
/*      */     {
/* 1498 */       Debug.out("Attempt to destroy non-transient mapper!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class pingValue
/*      */   {
/*      */     private final short x;
/*      */     
/*      */     private final short y;
/*      */     
/*      */     private final short metric;
/*      */     
/*      */ 
/*      */     protected pingValue(int _x, int _y, int _m)
/*      */     {
/* 1515 */       this.x = ((short)_x);
/* 1516 */       this.y = ((short)_y);
/* 1517 */       this.metric = ((short)_m);
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getX()
/*      */     {
/* 1523 */       return this.x & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getY()
/*      */     {
/* 1529 */       return this.y & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getMetric()
/*      */     {
/* 1535 */       return this.metric & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     protected String getString()
/*      */     {
/* 1541 */       return "x=" + getX() + ",y=" + getY() + ",m=" + getMetric();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private static class region
/*      */     implements SpeedManagerPingZone
/*      */   {
/*      */     private short x1;
/*      */     
/*      */     private short y1;
/*      */     
/*      */     private short x2;
/*      */     
/*      */     private short y2;
/*      */     private final short metric;
/*      */     
/*      */     protected region(SpeedManagerPingMapperImpl.pingValue p1, SpeedManagerPingMapperImpl.pingValue p2)
/*      */     {
/* 1560 */       this.x1 = ((short)p1.getX());
/* 1561 */       this.y1 = ((short)p1.getY());
/* 1562 */       this.x2 = ((short)p2.getX());
/* 1563 */       this.y2 = ((short)p2.getY());
/*      */       
/* 1565 */       if (this.x2 < this.x1) {
/* 1566 */         short t = this.x1;
/* 1567 */         this.x1 = this.x2;
/* 1568 */         this.x2 = t;
/*      */       }
/* 1570 */       if (this.y2 < this.y1) {
/* 1571 */         short t = this.y1;
/* 1572 */         this.y1 = this.y2;
/* 1573 */         this.y2 = t;
/*      */       }
/* 1575 */       this.metric = ((short)((p1.getMetric() + p2.getMetric()) / 2));
/*      */     }
/*      */     
/*      */ 
/*      */     public int getX1()
/*      */     {
/* 1581 */       return this.x1 & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getY1()
/*      */     {
/* 1587 */       return this.y1 & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getX2()
/*      */     {
/* 1593 */       return this.x2 & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getY2()
/*      */     {
/* 1599 */       return this.y2 & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getUploadStartBytesPerSec()
/*      */     {
/* 1605 */       return getX1() * 256;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getUploadEndBytesPerSec()
/*      */     {
/* 1611 */       return getX2() * 256 + 255;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getDownloadStartBytesPerSec()
/*      */     {
/* 1617 */       return getY1() * 256;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getDownloadEndBytesPerSec()
/*      */     {
/* 1623 */       return getY2() * 256 + 255;
/*      */     }
/*      */     
/*      */ 
/*      */     public int getMetric()
/*      */     {
/* 1629 */       return this.metric & 0xFFFF;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getString()
/*      */     {
/* 1636 */       return "x=" + getX1() + ",y=" + getY1() + ",w=" + (getX2() - getX1() + 1) + ",h=" + (getY2() - getY1() + 1);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private static class limitEstimate
/*      */     implements SpeedManagerLimitEstimate, Cloneable
/*      */   {
/*      */     private int speed;
/*      */     
/*      */ 
/*      */     private float estimate_type;
/*      */     
/*      */     private float metric_rating;
/*      */     
/*      */     private final long when;
/*      */     
/*      */     private final int hits;
/*      */     
/*      */     private final int[][] segs;
/*      */     
/*      */ 
/*      */     protected limitEstimate(int _speed, double _estimate_type, double _metric_rating, int _hits, long _when, int[][] _segs)
/*      */     {
/* 1661 */       this.speed = _speed;
/* 1662 */       this.estimate_type = ((float)_estimate_type);
/* 1663 */       this.metric_rating = ((float)_metric_rating);
/* 1664 */       this.hits = _hits;
/* 1665 */       this.when = _when;
/* 1666 */       this.segs = _segs;
/*      */       
/*      */ 
/*      */ 
/* 1670 */       if (this.metric_rating < -1.0F)
/*      */       {
/* 1672 */         this.metric_rating = -1.0F;
/*      */       }
/* 1674 */       else if (this.metric_rating > 1.0F)
/*      */       {
/* 1676 */         this.metric_rating = 1.0F;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public int getBytesPerSec()
/*      */     {
/* 1683 */       return this.speed;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setBytesPerSec(int s)
/*      */     {
/* 1690 */       this.speed = s;
/*      */     }
/*      */     
/*      */ 
/*      */     public float getEstimateType()
/*      */     {
/* 1696 */       return this.estimate_type;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void setEstimateType(float et)
/*      */     {
/* 1703 */       this.estimate_type = et;
/*      */     }
/*      */     
/*      */ 
/*      */     public float getMetricRating()
/*      */     {
/* 1709 */       return this.metric_rating;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     protected void setMetricRating(float mr)
/*      */     {
/* 1716 */       this.metric_rating = mr;
/*      */     }
/*      */     
/*      */ 
/*      */     public int[][] getSegments()
/*      */     {
/* 1722 */       return this.segs;
/*      */     }
/*      */     
/*      */ 
/*      */     protected int getHits()
/*      */     {
/* 1728 */       return this.hits;
/*      */     }
/*      */     
/*      */ 
/*      */     public long getWhen()
/*      */     {
/* 1734 */       return this.when;
/*      */     }
/*      */     
/*      */     public limitEstimate getClone()
/*      */     {
/*      */       try
/*      */       {
/* 1741 */         return (limitEstimate)clone();
/*      */       }
/*      */       catch (Throwable e) {}
/*      */       
/* 1745 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public String getString()
/*      */     {
/* 1752 */       return "speed=" + DisplayFormatters.formatByteCountToKiBEtc(this.speed) + ",metric=" + this.metric_rating + ",segs=" + this.segs.length + ",hits=" + this.hits + ",when=" + this.when;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void main(String[] args)
/*      */   {
/* 1762 */     SpeedManagerPingMapperImpl pm = new SpeedManagerPingMapperImpl(null, "test", 100, true, false);
/*      */     
/* 1764 */     Random rand = new Random();
/*      */     
/* 1766 */     int[][] phases = { { 50, 0, 100000, 50 }, { 50, 100000, 200000, 200 }, { 50, 50000, 50000, 200 }, { 50, 0, 100000, 50 } };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1774 */     for (int i = 0; i < phases.length; i++)
/*      */     {
/* 1776 */       int[] phase = phases[i];
/*      */       
/* 1778 */       System.out.println("**** phase " + i);
/*      */       
/* 1780 */       for (int j = 0; j < phase[0]; j++)
/*      */       {
/* 1782 */         int x_base = phase[1];
/* 1783 */         int x_var = phase[2];
/* 1784 */         int r = phase[3];
/*      */         
/* 1786 */         pm.addPing(x_base + rand.nextInt(x_var), x_base + rand.nextInt(x_var), rand.nextInt(r), false);
/*      */         
/* 1788 */         SpeedManagerLimitEstimate up = pm.getEstimatedUploadLimit(false);
/* 1789 */         SpeedManagerLimitEstimate down = pm.getEstimatedDownloadLimit(false);
/*      */         
/* 1791 */         if ((up != null) && (down != null))
/*      */         {
/* 1793 */           System.out.println(up.getString() + "," + down.getString());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/SpeedManagerPingMapperImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */