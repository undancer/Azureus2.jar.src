/*     */ package org.gudy.azureus2.core3.tracker.host.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentException;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.host.TRHostTorrentFinder;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class TRHostConfigImpl
/*     */ {
/*     */   public static final String LOG_FILE_NAME = "tracker.log";
/*     */   public static final long BACKUP_RETENTION_PERIOD = 604800000L;
/*     */   private final TRHostImpl host;
/*  47 */   private final AEMonitor save_lock_mon = new AEMonitor("TRHostConfig:SL");
/*     */   
/*     */   private final String log_dir;
/*     */   
/*  51 */   private volatile boolean loading = false;
/*  52 */   private volatile boolean save_outstanding = false;
/*     */   
/*  54 */   private Map saved_stats = new HashMap();
/*  55 */   private final List saved_stats_to_delete = new ArrayList();
/*     */   
/*  57 */   private boolean config_exists = true;
/*     */   
/*  59 */   private final AEMonitor this_mon = new AEMonitor("TRHostConfig");
/*     */   
/*     */ 
/*     */ 
/*     */   protected TRHostConfigImpl(TRHostImpl _host)
/*     */   {
/*  65 */     this.host = _host;
/*     */     
/*  67 */     this.log_dir = SystemProperties.getUserPath();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void loadConfig(TRHostTorrentFinder finder)
/*     */   {
/*     */     try
/*     */     {
/*  75 */       this.this_mon.enter();
/*     */       
/*  77 */       this.loading = true;
/*     */       
/*  79 */       Map map = FileUtil.readResilientConfigFile("tracker.config");
/*     */       
/*  81 */       List torrents = (List)map.get("torrents");
/*     */       
/*  83 */       if (torrents == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*  88 */       Iterator iter = torrents.iterator();
/*     */       
/*  90 */       while (iter.hasNext())
/*     */       {
/*  92 */         Map t_map = (Map)iter.next();
/*     */         
/*  94 */         Long persistent_l = (Long)t_map.get("persistent");
/*     */         
/*  96 */         boolean persistent = (persistent_l == null) || (persistent_l.longValue() == 1L);
/*     */         
/*  98 */         Long passive_l = (Long)t_map.get("passive");
/*     */         
/* 100 */         boolean passive = (passive_l != null) && (passive_l.longValue() == 1L);
/*     */         
/* 102 */         Long dateadded_l = (Long)t_map.get("dateadded");
/*     */         
/* 104 */         long date_added = dateadded_l == null ? SystemTime.getCurrentTime() : dateadded_l.longValue();
/*     */         
/* 106 */         byte[] hash = (byte[])t_map.get("hash");
/*     */         
/* 108 */         if (persistent)
/*     */         {
/* 110 */           int state = ((Long)t_map.get("status")).intValue();
/*     */           
/* 112 */           if (state == 0)
/*     */           {
/* 114 */             state = 1;
/*     */           }
/*     */           
/* 117 */           TOTorrent torrent = finder.lookupTorrent(hash);
/*     */           
/* 119 */           if ((torrent == null) && (passive))
/*     */           {
/* 121 */             byte[] file_b = (byte[])t_map.get("torrent_file");
/*     */             
/* 123 */             if (file_b != null) {
/*     */               try
/*     */               {
/* 126 */                 File file = new File(new String(file_b, "ISO-8859-1"));
/*     */                 
/* 128 */                 torrent = TorrentUtils.readFromFile(file, true, true);
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 137 */           if (torrent != null)
/*     */           {
/* 139 */             TRHostTorrent ht = this.host.addTorrent(torrent, state, true, passive, date_added);
/*     */             
/* 141 */             if ((ht instanceof TRHostTorrentHostImpl))
/*     */             {
/* 143 */               TRHostTorrentHostImpl hth = (TRHostTorrentHostImpl)ht;
/*     */               
/* 145 */               recoverStats(hth, t_map);
/*     */             }
/*     */             
/*     */ 
/*     */           }
/* 150 */           else if (COConfigurationManager.getBooleanParameter("Tracker Public Enable"))
/*     */           {
/* 152 */             this.host.addExternalTorrent(hash, state, date_added);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 159 */           this.saved_stats.put(new HashWrapper(hash), t_map);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 165 */       Debug.printStackTrace(e);
/*     */     }
/*     */     finally
/*     */     {
/* 169 */       this.loading = false;
/*     */       
/* 171 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void recoverStats(TRHostTorrentHostImpl host_torrent)
/*     */   {
/*     */     try
/*     */     {
/* 180 */       HashWrapper hash = host_torrent.getTorrent().getHashWrapper();
/*     */       
/* 182 */       Map t_map = (Map)this.saved_stats.get(hash);
/*     */       
/* 184 */       if (t_map != null)
/*     */       {
/* 186 */         recoverStats(host_torrent, t_map);
/*     */         
/*     */ 
/*     */ 
/* 190 */         synchronized (this.saved_stats_to_delete)
/*     */         {
/* 192 */           this.saved_stats_to_delete.add(hash);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 198 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void recoverStats(TRHostTorrentHostImpl host_torrent, Map t_map)
/*     */   {
/* 207 */     long completed = 0L;
/* 208 */     long announces = 0L;
/* 209 */     long scrapes = 0L;
/* 210 */     long total_up = 0L;
/* 211 */     long total_down = 0L;
/* 212 */     long bytes_in = 0L;
/* 213 */     long bytes_out = 0L;
/*     */     
/* 215 */     Long dateadded_l = (Long)t_map.get("dateadded");
/*     */     
/* 217 */     long date_added = dateadded_l == null ? SystemTime.getCurrentTime() : dateadded_l.longValue();
/*     */     
/* 219 */     Map s_map = (Map)t_map.get("stats");
/*     */     
/* 221 */     if (s_map != null)
/*     */     {
/* 223 */       completed = ((Long)s_map.get("completed")).longValue();
/* 224 */       announces = ((Long)s_map.get("announces")).longValue();
/* 225 */       total_up = ((Long)s_map.get("uploaded")).longValue();
/* 226 */       total_down = ((Long)s_map.get("downloaded")).longValue();
/*     */       
/* 228 */       Long scrapes_l = (Long)s_map.get("scrapes");
/* 229 */       if (scrapes_l != null) {
/* 230 */         scrapes = scrapes_l.longValue();
/*     */       }
/* 232 */       Long bytes_in_l = (Long)s_map.get("bytesin");
/* 233 */       if (bytes_in_l != null) {
/* 234 */         bytes_in = bytes_in_l.longValue();
/*     */       }
/* 236 */       Long bytes_out_l = (Long)s_map.get("bytesout");
/* 237 */       if (bytes_out_l != null) {
/* 238 */         bytes_out = bytes_out_l.longValue();
/*     */       }
/*     */     }
/*     */     
/* 242 */     host_torrent.setStartOfDayValues(date_added, completed, announces, scrapes, total_up, total_down, bytes_in, bytes_out);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void saveConfig(boolean immediate)
/*     */   {
/* 249 */     if (this.loading)
/*     */     {
/* 251 */       return;
/*     */     }
/*     */     
/* 254 */     synchronized (this.saved_stats_to_delete)
/*     */     {
/* 256 */       if (this.saved_stats_to_delete.size() > 0)
/*     */       {
/* 258 */         Map saved_stats_copy = new HashMap(this.saved_stats);
/*     */         
/* 260 */         for (int i = 0; i < this.saved_stats_to_delete.size(); i++)
/*     */         {
/* 262 */           saved_stats_copy.remove(this.saved_stats_to_delete.get(i));
/*     */         }
/*     */         
/* 265 */         this.saved_stats_to_delete.clear();
/*     */         
/* 267 */         this.saved_stats = saved_stats_copy;
/*     */       }
/*     */     }
/*     */     
/* 271 */     if ((immediate) || (this.save_outstanding))
/*     */     {
/* 273 */       this.save_outstanding = false;
/*     */       try
/*     */       {
/* 276 */         Map map = new HashMap();
/*     */         
/* 278 */         List list = new ArrayList();
/*     */         
/* 280 */         TRHostTorrent[] torrents = this.host.getTorrents();
/*     */         
/* 282 */         Object stats_entries = new ArrayList();
/*     */         
/* 284 */         Set added = new HashSet();
/*     */         
/* 286 */         for (int i = 0; i < torrents.length; i++)
/*     */         {
/*     */           try
/*     */           {
/* 290 */             TRHostTorrent torrent = torrents[i];
/*     */             
/* 292 */             added.add(torrent.getTorrent().getHashWrapper());
/*     */             
/* 294 */             StringBuffer stats_entry = new StringBuffer(2048);
/*     */             
/* 296 */             byte[] hash = torrent.getTorrent().getHash();
/* 297 */             byte[] name = torrent.getTorrent().getName();
/* 298 */             int status = torrent.getStatus();
/* 299 */             long completed = torrent.getCompletedCount();
/* 300 */             long announces = torrent.getAnnounceCount();
/* 301 */             long scrapes = torrent.getScrapeCount();
/* 302 */             long uploaded = torrent.getTotalUploaded();
/* 303 */             long downloaded = torrent.getTotalDownloaded();
/* 304 */             long bytes_in = torrent.getTotalBytesIn();
/* 305 */             long bytes_out = torrent.getTotalBytesOut();
/* 306 */             long date_added = torrent.getDateAdded();
/*     */             
/* 308 */             int seed_count = torrent.getSeedCount();
/* 309 */             int non_seed_count = torrent.getLeecherCount();
/*     */             
/*     */ 
/* 312 */             Map t_map = new HashMap();
/*     */             
/* 314 */             t_map.put("persistent", new Long(torrent.isPersistent() ? 1L : 0L));
/* 315 */             t_map.put("passive", new Long(torrent.isPassive() ? 1L : 0L));
/*     */             
/* 317 */             if (torrent.isPassive()) {
/*     */               try
/*     */               {
/* 320 */                 String file = TorrentUtils.getTorrentFileName(torrent.getTorrent());
/*     */                 
/* 322 */                 t_map.put("torrent_file", file.getBytes("ISO-8859-1"));
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 326 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */             
/* 330 */             t_map.put("hash", hash);
/* 331 */             t_map.put("dateadded", new Long(date_added));
/* 332 */             t_map.put("status", new Long(status));
/*     */             
/* 334 */             list.add(t_map);
/*     */             
/* 336 */             Map s_map = new HashMap();
/*     */             
/* 338 */             t_map.put("stats", s_map);
/*     */             
/* 340 */             s_map.put("completed", new Long(completed));
/* 341 */             s_map.put("announces", new Long(announces));
/* 342 */             s_map.put("scrapes", new Long(scrapes));
/* 343 */             s_map.put("uploaded", new Long(uploaded));
/* 344 */             s_map.put("downloaded", new Long(downloaded));
/* 345 */             s_map.put("bytesin", new Long(bytes_in));
/* 346 */             s_map.put("bytesout", new Long(bytes_out));
/*     */             
/*     */ 
/* 349 */             stats_entry.append(new String(name, "UTF8"));
/* 350 */             stats_entry.append(",");
/* 351 */             stats_entry.append(ByteFormatter.nicePrint(hash, true));
/* 352 */             stats_entry.append(",");
/* 353 */             stats_entry.append(status);
/* 354 */             stats_entry.append(",");
/* 355 */             stats_entry.append(seed_count);
/* 356 */             stats_entry.append(",");
/* 357 */             stats_entry.append(non_seed_count);
/* 358 */             stats_entry.append(",");
/* 359 */             stats_entry.append(completed);
/* 360 */             stats_entry.append(",");
/* 361 */             stats_entry.append(announces);
/* 362 */             stats_entry.append(",");
/* 363 */             stats_entry.append(scrapes);
/* 364 */             stats_entry.append(",");
/* 365 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtc(uploaded));
/* 366 */             stats_entry.append(",");
/* 367 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtc(downloaded));
/* 368 */             stats_entry.append(",");
/* 369 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(torrent.getAverageUploaded()));
/* 370 */             stats_entry.append(",");
/* 371 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(torrent.getAverageDownloaded()));
/* 372 */             stats_entry.append(",");
/* 373 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtc(torrent.getTotalLeft()));
/* 374 */             stats_entry.append(",");
/* 375 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtc(bytes_in));
/* 376 */             stats_entry.append(",");
/* 377 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtc(bytes_out));
/* 378 */             stats_entry.append(",");
/* 379 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(torrent.getAverageBytesIn()));
/* 380 */             stats_entry.append(",");
/* 381 */             stats_entry.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(torrent.getAverageBytesOut()));
/*     */             
/* 383 */             stats_entry.append("\r\n");
/*     */             
/* 385 */             ((List)stats_entries).add(stats_entry);
/*     */           }
/*     */           catch (TOTorrentException e)
/*     */           {
/* 389 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 396 */         Iterator it = this.saved_stats.keySet().iterator();
/*     */         
/* 398 */         long now = SystemTime.getCurrentTime();
/*     */         
/* 400 */         while (it.hasNext())
/*     */         {
/* 402 */           HashWrapper hash = (HashWrapper)it.next();
/*     */           
/* 404 */           if (!added.contains(hash))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 409 */             Map t_map = (Map)this.saved_stats.get(hash);
/*     */             
/* 411 */             Long backup = (Long)t_map.get("backup_time");
/*     */             
/* 413 */             if (backup == null)
/*     */             {
/* 415 */               backup = new Long(now);
/*     */               
/* 417 */               t_map.put("backup_time", backup);
/*     */             }
/*     */             
/* 420 */             if (now - backup.longValue() < 604800000L)
/*     */             {
/* 422 */               list.add(t_map);
/*     */               
/* 424 */               added.add(hash);
/*     */             }
/*     */           }
/*     */         }
/* 428 */         map.put("torrents", list);
/*     */         try
/*     */         {
/* 431 */           this.save_lock_mon.enter();
/*     */           
/* 433 */           if (torrents.length == 0)
/*     */           {
/* 435 */             if (this.config_exists)
/*     */             {
/* 437 */               FileUtil.deleteResilientConfigFile("tracker.config");
/*     */               
/* 439 */               this.config_exists = false;
/*     */             }
/*     */           }
/*     */           else {
/* 443 */             this.config_exists = true;
/*     */             
/* 445 */             FileUtil.writeResilientConfigFile("tracker.config", map);
/*     */           }
/*     */           
/* 448 */           if ((COConfigurationManager.getBooleanParameter("Tracker Log Enable")) && (((List)stats_entries).size() > 0))
/*     */           {
/*     */             try
/*     */             {
/* 452 */               String timeStamp = "[" + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()) + "] ";
/*     */               
/* 454 */               PrintWriter pw = null;
/*     */               
/* 456 */               File file_name = new File(this.log_dir.concat(File.separator).concat("tracker.log"));
/*     */               
/*     */               try
/*     */               {
/* 460 */                 pw = new PrintWriter(new FileWriter(file_name, true));
/*     */                 
/* 462 */                 for (int i = 0; i < ((List)stats_entries).size(); i++)
/*     */                 {
/* 464 */                   StringBuffer stats_entry = (StringBuffer)((List)stats_entries).get(i);
/*     */                   
/* 466 */                   String str = timeStamp + stats_entry.toString();
/*     */                   
/* 468 */                   pw.print(str);
/*     */                 }
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 473 */                 Debug.printStackTrace(e);
/*     */               }
/*     */               finally
/*     */               {
/* 477 */                 if (pw != null)
/*     */                 {
/*     */                   try
/*     */                   {
/* 481 */                     pw.close();
/*     */                   }
/*     */                   catch (Throwable e) {}
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 488 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */         finally {
/* 493 */           this.save_lock_mon.exit();
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 497 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void saveRequired()
/*     */   {
/* 505 */     this.save_outstanding = true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/host/impl/TRHostConfigImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */