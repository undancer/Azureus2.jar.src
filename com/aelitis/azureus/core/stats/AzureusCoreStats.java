/*     */ package com.aelitis.azureus.core.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.util.average.Average;
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.Timer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AzureusCoreStats
/*     */ {
/*     */   public static final int AVERAGE_PERIOD = 1000;
/*     */   public static final String ST_ALL = ".*";
/*     */   public static final String ST_DISK = "disk.*";
/*     */   public static final String ST_DISK_READ_QUEUE_LENGTH = "disk.read.queue.length";
/*     */   public static final String ST_DISK_READ_QUEUE_BYTES = "disk.read.queue.bytes";
/*     */   public static final String ST_DISK_READ_REQUEST_COUNT = "disk.read.request.count";
/*     */   public static final String ST_DISK_READ_REQUEST_SINGLE = "disk.read.request.single";
/*     */   public static final String ST_DISK_READ_REQUEST_MULTIPLE = "disk.read.request.multiple";
/*     */   public static final String ST_DISK_READ_REQUEST_BLOCKS = "disk.read.request.blocks";
/*     */   public static final String ST_DISK_READ_BYTES_TOTAL = "disk.read.bytes.total";
/*     */   public static final String ST_DISK_READ_BYTES_SINGLE = "disk.read.bytes.single";
/*     */   public static final String ST_DISK_READ_BYTES_MULTIPLE = "disk.read.bytes.multiple";
/*     */   public static final String ST_DISK_READ_IO_TIME = "disk.read.io.time";
/*     */   public static final String ST_DISK_READ_IO_COUNT = "disk.read.io.count";
/*     */   public static final String ST_DISK_WRITE_QUEUE_LENGTH = "disk.write.queue.length";
/*     */   public static final String ST_DISK_WRITE_QUEUE_BYTES = "disk.write.queue.bytes";
/*     */   public static final String ST_DISK_WRITE_REQUEST_COUNT = "disk.write.request.count";
/*     */   public static final String ST_DISK_WRITE_REQUEST_BLOCKS = "disk.write.request.blocks";
/*     */   public static final String ST_DISK_WRITE_BYTES_TOTAL = "disk.write.bytes.total";
/*     */   public static final String ST_DISK_WRITE_BYTES_SINGLE = "disk.write.bytes.single";
/*     */   public static final String ST_DISK_WRITE_BYTES_MULTIPLE = "disk.write.bytes.multiple";
/*     */   public static final String ST_DISK_WRITE_IO_TIME = "disk.write.io.time";
/*     */   public static final String ST_DISK_WRITE_IO_COUNT = "disk.write.io.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_WAIT_COUNT = "net.write.control.wait.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_NP_COUNT = "net.write.control.np.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_P_COUNT = "net.write.control.p.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_ENTITY_COUNT = "net.write.control.entity.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_CON_COUNT = "net.write.control.con.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_READY_CON_COUNT = "net.write.control.ready.con.count";
/*     */   public static final String ST_NET_WRITE_CONTROL_READY_BYTE_COUNT = "net.write.control.ready.byte.count";
/*     */   public static final String ST_NET_READ_CONTROL_LOOP_COUNT = "net.read.control.loop.count";
/*     */   public static final String ST_NET_READ_CONTROL_NP_COUNT = "net.read.control.np.count";
/*     */   public static final String ST_NET_READ_CONTROL_P_COUNT = "net.read.control.p.count";
/*     */   public static final String ST_NET_READ_CONTROL_WAIT_COUNT = "net.read.control.wait.count";
/*     */   public static final String ST_NET_READ_CONTROL_ENTITY_COUNT = "net.read.control.entity.count";
/*     */   public static final String ST_NET_READ_CONTROL_CON_COUNT = "net.read.control.con.count";
/*     */   public static final String ST_NET_READ_CONTROL_READY_CON_COUNT = "net.read.control.ready.con.count";
/*     */   public static final String ST_NET_TCP_OUT_CONNECT_QUEUE_LENGTH = "net.tcp.outbound.connect.queue.length";
/*     */   public static final String ST_NET_TCP_OUT_PENDING_QUEUE_LENGTH = "net.tcp.outbound.pending.queue.length";
/*     */   public static final String ST_NET_TCP_OUT_CANCEL_QUEUE_LENGTH = "net.tcp.outbound.cancel.queue.length";
/*     */   public static final String ST_NET_TCP_OUT_CLOSE_QUEUE_LENGTH = "net.tcp.outbound.close.queue.length";
/*     */   public static final String ST_NET_TCP_SELECT_WRITE_COUNT = "net.tcp.select.write.count";
/*     */   public static final String ST_NET_TCP_SELECT_READ_COUNT = "net.tcp.select.read.count";
/*     */   public static final String ST_NET_HTTP_IN_REQUEST_COUNT = "net.http.inbound.request.count";
/*     */   public static final String ST_NET_HTTP_IN_REQUEST_OK_COUNT = "net.http.inbound.request.ok.count";
/*     */   public static final String ST_NET_HTTP_IN_REQUEST_INVALID_COUNT = "net.http.inbound.request.invalid.count";
/*     */   public static final String ST_NET_HTTP_IN_REQUEST_WEBSEED_COUNT = "net.http.inbound.request.webseed.count";
/*     */   public static final String ST_NET_HTTP_IN_REQUEST_GETRIGHT_COUNT = "net.http.inbound.request.getright.count";
/*     */   public static final String ST_PEER_CONTROL_SCHEDULE_COUNT = "peer.control.schedule.count";
/*     */   public static final String ST_PEER_CONTROL_LOOP_COUNT = "peer.control.loop.count";
/*     */   public static final String ST_PEER_CONTROL_YIELD_COUNT = "peer.control.yield.count";
/*     */   public static final String ST_PEER_CONTROL_WAIT_COUNT = "peer.control.wait.count";
/*     */   public static final String ST_PEER_CONTROL_WAIT_TIME = "peer.control.wait.time";
/*     */   public static final String ST_PEER_MANAGER_COUNT = "peer.manager.count";
/*     */   public static final String ST_PEER_MANAGER_PEER_COUNT = "peer.manager.peer.count";
/*     */   public static final String ST_PEER_MANAGER_PEER_SNUBBED_COUNT = "peer.manager.peer.snubbed.count";
/*     */   public static final String ST_PEER_MANAGER_PEER_STALLED_DISK_COUNT = "peer.manager.peer.stalled.disk.count";
/*     */   public static final String ST_TRACKER_READ_BYTES = "tracker.read.bytes.total";
/*     */   public static final String ST_TRACKER_WRITE_BYTES = "tracker.write.bytes.total";
/*     */   public static final String ST_TRACKER_ANNOUNCE_COUNT = "tracker.announce.count";
/*     */   public static final String ST_TRACKER_ANNOUNCE_TIME = "tracker.announce.time";
/*     */   public static final String ST_TRACKER_SCRAPE_COUNT = "tracker.scrape.count";
/*     */   public static final String ST_TRACKER_SCRAPE_TIME = "tracker.scrape.time";
/*     */   public static final String ST_XFER_UPLOADED_PROTOCOL_BYTES = "xfer.upload.protocol.bytes.total";
/*     */   public static final String ST_XFER_UPLOADED_DATA_BYTES = "xfer.upload.data.bytes.total";
/*     */   public static final String ST_XFER_DOWNLOADED_PROTOCOL_BYTES = "xfer.download.protocol.bytes.total";
/*     */   public static final String ST_XFER_DOWNLOADED_DATA_BYTES = "xfer.download.data.bytes.total";
/*     */   public static final String POINT = "Point";
/*     */   public static final String CUMULATIVE = "Cumulative";
/* 142 */   private static final List stats_names = new ArrayList();
/* 143 */   private static final Map stats_types = new HashMap();
/*     */   
/* 145 */   private static final String[][] _ST_ALL = { { "disk.read.queue.length", "Point" }, { "disk.read.queue.bytes", "Point" }, { "disk.read.request.count", "Cumulative" }, { "disk.read.request.single", "Cumulative" }, { "disk.read.request.multiple", "Cumulative" }, { "disk.read.request.blocks", "Cumulative" }, { "disk.read.bytes.total", "Cumulative" }, { "disk.read.bytes.single", "Cumulative" }, { "disk.read.bytes.multiple", "Cumulative" }, { "disk.read.io.time", "Cumulative" }, { "disk.read.io.count", "Cumulative" }, { "disk.write.queue.length", "Point" }, { "disk.write.queue.bytes", "Point" }, { "disk.write.request.count", "Cumulative" }, { "disk.write.request.blocks", "Cumulative" }, { "disk.write.bytes.total", "Cumulative" }, { "disk.write.bytes.single", "Cumulative" }, { "disk.write.bytes.multiple", "Cumulative" }, { "disk.write.io.time", "Cumulative" }, { "disk.write.io.count", "Cumulative" }, { "net.write.control.wait.count", "Cumulative" }, { "net.write.control.p.count", "Cumulative" }, { "net.write.control.np.count", "Cumulative" }, { "net.write.control.entity.count", "Point" }, { "net.write.control.con.count", "Point" }, { "net.write.control.ready.con.count", "Point" }, { "net.write.control.ready.byte.count", "Point" }, { "net.read.control.loop.count", "Cumulative" }, { "net.read.control.p.count", "Cumulative" }, { "net.read.control.np.count", "Cumulative" }, { "net.read.control.wait.count", "Cumulative" }, { "net.read.control.entity.count", "Point" }, { "net.read.control.con.count", "Point" }, { "net.read.control.ready.con.count", "Point" }, { "net.tcp.outbound.connect.queue.length", "Point" }, { "net.tcp.outbound.pending.queue.length", "Point" }, { "net.tcp.outbound.cancel.queue.length", "Point" }, { "net.tcp.outbound.close.queue.length", "Point" }, { "net.tcp.select.write.count", "Cumulative" }, { "net.tcp.select.read.count", "Cumulative" }, { "net.http.inbound.request.count", "Cumulative" }, { "net.http.inbound.request.ok.count", "Cumulative" }, { "net.http.inbound.request.invalid.count", "Cumulative" }, { "net.http.inbound.request.webseed.count", "Cumulative" }, { "net.http.inbound.request.getright.count", "Cumulative" }, { "peer.control.schedule.count", "Cumulative" }, { "peer.control.loop.count", "Cumulative" }, { "peer.control.yield.count", "Cumulative" }, { "peer.control.wait.count", "Cumulative" }, { "peer.control.wait.time", "Cumulative" }, { "peer.manager.count", "Point" }, { "peer.manager.peer.count", "Point" }, { "peer.manager.peer.snubbed.count", "Point" }, { "peer.manager.peer.stalled.disk.count", "Point" }, { "tracker.read.bytes.total", "Cumulative" }, { "tracker.write.bytes.total", "Cumulative" }, { "tracker.announce.count", "Cumulative" }, { "tracker.announce.time", "Cumulative" }, { "tracker.scrape.count", "Cumulative" }, { "tracker.scrape.time", "Cumulative" }, { "xfer.upload.protocol.bytes.total", "Cumulative" }, { "xfer.upload.data.bytes.total", "Cumulative" }, { "xfer.download.protocol.bytes.total", "Cumulative" }, { "xfer.download.data.bytes.total", "Cumulative" } };
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
/*     */   static
/*     */   {
/* 225 */     addStatsDefinitions(_ST_ALL);
/*     */     
/* 227 */     AEDiagnostics.addEvidenceGenerator(new AEDiagnosticsEvidenceGenerator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void generate(IndentWriter writer)
/*     */       {
/*     */ 
/* 234 */         writer.println("Stats");
/*     */         
/* 236 */         boolean turn_on_averages = !AzureusCoreStats.getEnableAverages();
/*     */         try
/*     */         {
/* 239 */           writer.indent();
/*     */           
/* 241 */           if (turn_on_averages)
/*     */           {
/* 243 */             AzureusCoreStats.setEnableAverages(true);
/*     */             try
/*     */             {
/* 246 */               Thread.sleep(5000L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */ 
/* 252 */           Set types = new HashSet();
/*     */           
/* 254 */           types.add(".*");
/*     */           
/* 256 */           Map reply = AzureusCoreStats.getStats(types);
/*     */           
/* 258 */           Iterator it = reply.entrySet().iterator();
/*     */           
/* 260 */           List lines = new ArrayList();
/*     */           
/* 262 */           while (it.hasNext())
/*     */           {
/* 264 */             Map.Entry entry = (Map.Entry)it.next();
/*     */             
/* 266 */             lines.add(entry.getKey() + " -> " + entry.getValue());
/*     */           }
/*     */           
/* 269 */           Collections.sort(lines);
/*     */           
/* 271 */           for (int i = 0; i < lines.size(); i++)
/*     */           {
/* 273 */             writer.println((String)lines.get(i));
/*     */           }
/*     */         }
/*     */         finally
/*     */         {
/* 278 */           if (turn_on_averages)
/*     */           {
/* 280 */             AzureusCoreStats.setEnableAverages(false);
/*     */           }
/*     */           
/* 283 */           writer.exdent();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/* 289 */   private static final CopyOnWriteList providers = new CopyOnWriteList();
/*     */   
/* 291 */   private static Map averages = new HashMap();
/*     */   
/*     */   private static boolean enable_averages;
/*     */   
/*     */   private static Timer average_timer;
/* 296 */   private static final CopyOnWriteList provider_listeners = new CopyOnWriteList();
/* 297 */   private static final CopyOnWriteList derived_generators = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addStatsDefinitions(String[][] stats)
/*     */   {
/* 303 */     for (int i = 0; i < stats.length; i++)
/*     */     {
/* 305 */       String name = stats[i][0];
/*     */       
/* 307 */       stats_names.add(name);
/*     */       
/* 309 */       stats_types.put(name, stats[i][1]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static Map getStats(Set types)
/*     */   {
/* 317 */     Set expanded = new HashSet();
/*     */     
/* 319 */     Iterator it = types.iterator();
/*     */     
/* 321 */     while (it.hasNext())
/*     */     {
/* 323 */       String type = (String)it.next();
/*     */       
/* 325 */       if (type.endsWith(".average"))
/*     */       {
/* 327 */         type = type.substring(0, type.length() - 8);
/*     */       }
/*     */       
/* 330 */       if (!type.endsWith("*"))
/*     */       {
/* 332 */         type = type + ".*";
/*     */       }
/*     */       
/* 335 */       Pattern pattern = Pattern.compile(type);
/*     */       
/* 337 */       for (int i = 0; i < stats_names.size(); i++)
/*     */       {
/* 339 */         String s = (String)stats_names.get(i);
/*     */         
/* 341 */         if (pattern.matcher(s).matches())
/*     */         {
/* 343 */           expanded.add(s);
/*     */         }
/*     */       }
/*     */       
/* 347 */       Iterator provider_it = providers.iterator();
/*     */       
/* 349 */       while (provider_it.hasNext())
/*     */       {
/* 351 */         Object[] provider_entry = (Object[])provider_it.next();
/*     */         
/* 353 */         Set provider_types = (Set)provider_entry[0];
/*     */         
/* 355 */         Iterator pt_it = provider_types.iterator();
/*     */         
/* 357 */         while (pt_it.hasNext())
/*     */         {
/* 359 */           String s = (String)pt_it.next();
/*     */           
/* 361 */           if (pattern.matcher(s).matches())
/*     */           {
/* 363 */             expanded.add(s);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 368 */       Iterator derived_it = derived_generators.iterator();
/*     */       
/* 370 */       while (derived_it.hasNext())
/*     */       {
/*     */         try
/*     */         {
/* 374 */           ((derivedStatsGenerator)derived_it.next()).match(pattern, expanded);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 378 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 383 */     Map result = getStatsSupport(expanded);
/*     */     
/* 385 */     Map ave = averages;
/*     */     
/* 387 */     if (ave != null)
/*     */     {
/* 389 */       it = result.keySet().iterator();
/*     */       
/* 391 */       Map ave_results = new HashMap();
/*     */       
/* 393 */       while (it.hasNext())
/*     */       {
/* 395 */         String key = (String)it.next();
/*     */         
/* 397 */         Object[] a_entry = (Object[])ave.get(key);
/*     */         
/* 399 */         if (a_entry != null)
/*     */         {
/* 401 */           Average average = (Average)a_entry[0];
/*     */           
/* 403 */           ave_results.put(key + ".average", new Long(average.getAverage()));
/*     */         }
/*     */       }
/*     */       
/* 407 */       result.putAll(ave_results);
/*     */     }
/*     */     
/* 410 */     Iterator derived_it = derived_generators.iterator();
/*     */     
/* 412 */     while (derived_it.hasNext())
/*     */     {
/*     */       try
/*     */       {
/* 416 */         ((derivedStatsGenerator)derived_it.next()).generate(result);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 420 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 424 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static Map getStatsSupport(Set types)
/*     */   {
/* 431 */     Map result = new HashMap();
/*     */     
/* 433 */     Iterator it = providers.iterator();
/*     */     
/* 435 */     while (it.hasNext())
/*     */     {
/* 437 */       Object[] provider_entry = (Object[])it.next();
/*     */       
/* 439 */       Map provider_result = new HashMap();
/*     */       
/*     */       Set target_types;
/*     */       Set target_types;
/* 443 */       if (types == null)
/*     */       {
/* 445 */         target_types = (Set)provider_entry[0];
/*     */       }
/*     */       else {
/* 448 */         target_types = types;
/*     */       }
/*     */       try
/*     */       {
/* 452 */         ((AzureusCoreStatsProvider)provider_entry[1]).updateStats(target_types, provider_result);
/*     */         
/* 454 */         Iterator pit = provider_result.entrySet().iterator();
/*     */         
/* 456 */         while (pit.hasNext())
/*     */         {
/* 458 */           Map.Entry pe = (Map.Entry)pit.next();
/*     */           
/* 460 */           String key = (String)pe.getKey();
/* 461 */           Object obj = pe.getValue();
/*     */           
/* 463 */           if ((obj instanceof Long))
/*     */           {
/* 465 */             Long old = (Long)result.get(key);
/*     */             
/* 467 */             if (old == null)
/*     */             {
/* 469 */               result.put(key, obj);
/*     */             }
/*     */             else
/*     */             {
/* 473 */               long v = ((Long)obj).longValue();
/*     */               
/* 475 */               result.put(key, new Long(v + old.longValue()));
/*     */             }
/*     */           }
/*     */           else {
/* 479 */             result.put(key, obj);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 484 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 488 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void registerProvider(Set types, AzureusCoreStatsProvider provider)
/*     */   {
/* 496 */     synchronized (providers)
/*     */     {
/* 498 */       providers.add(new Object[] { types, provider });
/*     */     }
/*     */     
/* 501 */     fireProvidersChangeListeners();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addProvidersChangeListener(providersChangeListener l)
/*     */   {
/* 508 */     provider_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */   protected static void fireProvidersChangeListeners()
/*     */   {
/* 514 */     Iterator it = provider_listeners.iterator();
/*     */     
/* 516 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 519 */         ((providersChangeListener)it.next()).providersChanged();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 523 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void registerDerivedStatsGenerator(derivedStatsGenerator gen)
/*     */   {
/* 532 */     derived_generators.add(gen);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized void setEnableAverages(boolean enabled)
/*     */   {
/* 539 */     if (enabled == enable_averages)
/*     */     {
/* 541 */       return;
/*     */     }
/*     */     
/* 544 */     enable_averages = enabled;
/*     */     
/* 546 */     if (enabled)
/*     */     {
/* 548 */       if (average_timer == null)
/*     */       {
/* 550 */         average_timer = new Timer("AzureusCoreStats:average");
/*     */         
/* 552 */         averages = new HashMap();
/*     */         
/* 554 */         average_timer.addPeriodicEvent(1000L, new TimerEventPerformer()
/*     */         {
/*     */ 
/*     */ 
/* 558 */           private final Map ave = AzureusCoreStats.averages;
/*     */           
/*     */ 
/*     */ 
/*     */           public void perform(TimerEvent event)
/*     */           {
/* 564 */             Map stats = AzureusCoreStats.getStatsSupport(null);
/*     */             
/* 566 */             Iterator it = stats.entrySet().iterator();
/*     */             
/* 568 */             boolean new_averages = false;
/*     */             
/* 570 */             while (it.hasNext())
/*     */             {
/* 572 */               Map.Entry entry = (Map.Entry)it.next();
/*     */               
/* 574 */               String key = (String)entry.getKey();
/* 575 */               Object value = entry.getValue();
/*     */               
/* 577 */               if ((value instanceof Long))
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 583 */                 Object[] a_entry = (Object[])this.ave.get(key);
/*     */                 Average a;
/* 585 */                 long last_value; boolean new_average; if (a_entry == null)
/*     */                 {
/* 587 */                   Average a = AverageFactory.MovingImmediateAverage(10);
/* 588 */                   long last_value = 0L;
/*     */                   
/* 590 */                   a_entry = new Object[] { a, value };
/*     */                   
/* 592 */                   this.ave.put(key, a_entry);
/*     */                   boolean new_average;
/* 594 */                   new_averages = new_average = 1;
/*     */                 }
/*     */                 else {
/* 597 */                   a = (Average)a_entry[0];
/* 598 */                   last_value = ((Long)a_entry[1]).longValue();
/*     */                   
/* 600 */                   new_average = false;
/*     */                 }
/*     */                 
/* 603 */                 if (AzureusCoreStats.stats_types.get(key) == "Cumulative")
/*     */                 {
/*     */ 
/*     */ 
/* 607 */                   if (!new_average)
/*     */                   {
/* 609 */                     a.update(((Long)value).longValue() - last_value);
/*     */                   }
/*     */                 }
/*     */                 else {
/* 613 */                   a.update(((Long)value).longValue());
/*     */                 }
/*     */                 
/*     */ 
/* 617 */                 a_entry[1] = value;
/*     */               }
/*     */             }
/*     */             
/* 621 */             if (new_averages)
/*     */             {
/* 623 */               AzureusCoreStats.fireProvidersChangeListeners();
/*     */             }
/*     */             
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/* 630 */     else if (average_timer != null)
/*     */     {
/* 632 */       average_timer.destroy();
/*     */       
/* 634 */       average_timer = null;
/*     */       
/* 636 */       averages = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static synchronized boolean getEnableAverages()
/*     */   {
/* 644 */     return enable_averages;
/*     */   }
/*     */   
/*     */   public static abstract interface derivedStatsGenerator
/*     */   {
/*     */     public abstract void match(Pattern paramPattern, Set paramSet);
/*     */     
/*     */     public abstract void generate(Map paramMap);
/*     */   }
/*     */   
/*     */   public static abstract interface providersChangeListener
/*     */   {
/*     */     public abstract void providersChanged();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/stats/AzureusCoreStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */