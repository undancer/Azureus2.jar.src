/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.TranscodeActionVetoException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeAnalysisListener;
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeJob;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*     */ import com.aelitis.azureus.core.download.DiskManagerFileInfoFile;
/*     */ import com.aelitis.azureus.core.download.DiskManagerFileInfoURL;
/*     */ import com.aelitis.azureus.core.torrent.PlatformTorrentUtils;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadRemovalVetoException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadWillBeRemovedListener;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class TranscodeJobImpl
/*     */   implements TranscodeJob, DownloadWillBeRemovedListener
/*     */ {
/*     */   private static final int TRANSCODE_OK_DL_PERCENT = 90;
/*     */   private TranscodeQueueImpl queue;
/*     */   private TranscodeTarget target;
/*     */   private TranscodeProfile profile;
/*     */   private DiskManagerFileInfo file;
/*     */   private TranscodeFileImpl transcode_file;
/*     */   private boolean is_stream;
/*     */   private volatile InputStream stream;
/*  68 */   private AESemaphore stream_sem = new AESemaphore("TJ:s");
/*     */   
/*     */   private int transcode_requirement;
/*     */   
/*  72 */   private int state = 0;
/*  73 */   private int percent_complete = 0;
/*  74 */   private int eta = Integer.MAX_VALUE;
/*     */   
/*     */   private String error;
/*     */   
/*     */   private long started_on;
/*     */   private long paused_on;
/*     */   private long process_time;
/*     */   private boolean use_direct_input;
/*     */   private boolean prefer_direct_input;
/*  83 */   private boolean auto_retry_enabled = true;
/*     */   
/*     */ 
/*     */   private boolean auto_retry;
/*     */   
/*     */ 
/*     */   private int auto_retry_count;
/*     */   
/*     */ 
/*     */   private Download download;
/*     */   
/*     */ 
/*     */   private volatile boolean download_ok;
/*     */   
/*     */ 
/*     */ 
/*     */   protected TranscodeJobImpl(TranscodeQueueImpl _queue, TranscodeTarget _target, TranscodeProfile _profile, DiskManagerFileInfo _file, boolean _add_stopped, int _transcode_requirement, boolean _is_stream)
/*     */     throws TranscodeException
/*     */   {
/* 102 */     this.queue = _queue;
/* 103 */     this.target = _target;
/* 104 */     this.profile = _profile;
/* 105 */     this.file = _file;
/* 106 */     this.transcode_requirement = _transcode_requirement;
/* 107 */     this.is_stream = _is_stream;
/*     */     
/* 109 */     if (_add_stopped)
/*     */     {
/* 111 */       this.state = 6;
/*     */     }
/*     */     
/* 114 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TranscodeJobImpl(TranscodeQueueImpl _queue, Map<String, Object> map)
/*     */     throws IOException, TranscodeException
/*     */   {
/* 124 */     this.queue = _queue;
/*     */     
/* 126 */     this.state = ImportExportUtils.importInt(map, "state");
/*     */     
/* 128 */     if (this.state == 1)
/*     */     {
/* 130 */       this.state = 0;
/*     */     }
/*     */     
/* 133 */     this.error = ImportExportUtils.importString(map, "error", null);
/*     */     
/* 135 */     String target_id = ImportExportUtils.importString(map, "target");
/*     */     
/* 137 */     this.target = this.queue.lookupTarget(target_id);
/*     */     
/* 139 */     String profile_id = ImportExportUtils.importString(map, "profile");
/*     */     
/* 141 */     this.profile = this.queue.lookupProfile(profile_id);
/*     */     
/* 143 */     String file_str = ImportExportUtils.importString(map, "file");
/*     */     
/* 145 */     if (file_str == null)
/*     */     {
/* 147 */       byte[] dl_hash = ByteFormatter.decodeString(ImportExportUtils.importString(map, "dl_hash"));
/*     */       
/* 149 */       int file_index = ImportExportUtils.importInt(map, "file_index");
/*     */       
/* 151 */       this.file = this.queue.lookupFile(dl_hash, file_index);
/*     */     }
/*     */     else {
/* 154 */       this.file = new DiskManagerFileInfoFile(new File(file_str));
/*     */     }
/*     */     
/* 157 */     this.transcode_requirement = ImportExportUtils.importInt(map, "trans_req", -1);
/*     */     
/* 159 */     this.auto_retry_enabled = ImportExportUtils.importBoolean(map, "ar_enable", true);
/*     */     
/* 161 */     this.prefer_direct_input = ImportExportUtils.importBoolean(map, "pdi", false);
/*     */     
/* 163 */     init();
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map<String, Object> toMap()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 172 */       Map<String, Object> map = new HashMap();
/*     */       
/* 174 */       synchronized (this)
/*     */       {
/* 176 */         ImportExportUtils.exportInt(map, "state", this.state);
/* 177 */         ImportExportUtils.exportString(map, "error", this.error);
/*     */         
/* 179 */         ImportExportUtils.exportString(map, "target", this.target.getID());
/*     */         
/* 181 */         ImportExportUtils.exportString(map, "profile", this.profile.getUID());
/*     */         try
/*     */         {
/* 184 */           Download download = this.file.getDownload();
/*     */           
/* 186 */           ImportExportUtils.exportString(map, "dl_hash", ByteFormatter.encodeString(download.getTorrent().getHash()));
/*     */           
/* 188 */           ImportExportUtils.exportInt(map, "file_index", this.file.getIndex());
/*     */ 
/*     */         }
/*     */         catch (DownloadException e)
/*     */         {
/*     */ 
/* 194 */           ImportExportUtils.exportString(map, "file", this.file.getFile().getAbsolutePath());
/*     */         }
/*     */         
/* 197 */         ImportExportUtils.exportInt(map, "trans_req", this.transcode_requirement);
/*     */         
/* 199 */         ImportExportUtils.exportBoolean(map, "ar_enable", this.auto_retry_enabled);
/*     */         
/* 201 */         ImportExportUtils.exportBoolean(map, "pdi", this.prefer_direct_input);
/*     */       }
/*     */       
/* 204 */       return map;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 208 */       throw new IOException("Export failed: " + Debug.getNestedExceptionMessage(e));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void init()
/*     */     throws TranscodeException
/*     */   {
/* 217 */     this.transcode_file = ((DeviceImpl)this.target.getDevice()).allocateFile(this.profile, getTranscodeRequirement() == 1, this.file, true);
/*     */     try
/*     */     {
/* 220 */       this.download = this.file.getDownload();
/*     */       
/* 222 */       if (this.download != null)
/*     */       {
/* 224 */         this.download.addDownloadWillBeRemovedListener(this);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 229 */     updateStatus(false);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void updateStatus()
/*     */   {
/* 235 */     updateStatus(true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void updateStatus(boolean report_change)
/*     */   {
/* 242 */     synchronized (this)
/*     */     {
/* 244 */       if (this.download_ok)
/*     */       {
/* 246 */         return;
/*     */       }
/*     */       
/* 249 */       long downloaded = this.file.getDownloaded();
/* 250 */       long length = this.file.getLength();
/*     */       
/* 252 */       if ((this.download == null) || (downloaded == length))
/*     */       {
/* 254 */         this.download_ok = true;
/*     */       }
/*     */       else
/*     */       {
/* 258 */         Torrent torrent = this.download.getTorrent();
/*     */         
/* 260 */         if ((PlatformTorrentUtils.isContent(torrent, false)) || (PlatformTorrentUtils.getContentNetworkID(PluginCoreUtils.unwrap(torrent)) == 3L))
/*     */         {
/*     */ 
/* 263 */           this.download_ok = true;
/*     */         }
/*     */         else
/*     */         {
/* 267 */           int percent_done = (int)(100L * downloaded / length);
/*     */           
/* 269 */           if (percent_done >= 90)
/*     */           {
/* 271 */             this.download_ok = true;
/*     */           }
/*     */           else
/*     */           {
/* 275 */             PeerManager pm = this.download.getPeerManager();
/*     */             
/* 277 */             if (pm != null)
/*     */             {
/* 279 */               PeerManagerStats stats = pm.getStats();
/*     */               
/* 281 */               int connected_seeds = stats.getConnectedSeeds();
/* 282 */               int connected_leechers = stats.getConnectedLeechers();
/*     */               
/*     */ 
/* 285 */               if ((connected_seeds > 10) && (connected_seeds > connected_leechers))
/*     */               {
/* 287 */                 this.download_ok = true;
/*     */               }
/*     */             }
/*     */             else {
/* 291 */               int state = this.download.getState();
/*     */               
/* 293 */               if (state == 7) {
/*     */                 try
/*     */                 {
/* 296 */                   this.download.restart();
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 300 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 309 */     if ((this.download_ok) && (report_change))
/*     */     {
/* 311 */       this.queue.jobChanged(this, true, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDownloadETA()
/*     */   {
/* 318 */     if (this.download_ok)
/*     */     {
/* 320 */       return 0L;
/*     */     }
/*     */     
/* 323 */     if (this.file.getDownloaded() == this.file.getLength())
/*     */     {
/* 325 */       return 0L;
/*     */     }
/*     */     
/* 328 */     if ((this.file.isSkipped()) || (this.file.isDeleted()))
/*     */     {
/* 330 */       return Long.MAX_VALUE;
/*     */     }
/*     */     try
/*     */     {
/* 334 */       long eta = PluginCoreUtils.unwrap(this.download).getStats().getSmoothedETA();
/*     */       
/* 336 */       if (eta < 0L)
/*     */       {
/* 338 */         return Long.MAX_VALUE;
/*     */       }
/*     */       
/* 341 */       long adjusted = eta * 100L / 90L;
/*     */       
/* 343 */       if (adjusted == 0L) {}
/*     */       
/* 345 */       return 1L;
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */ 
/* 352 */       Debug.out(e);
/*     */     }
/* 354 */     return Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean canUseDirectInput()
/*     */   {
/* 361 */     if ((this.file instanceof DiskManagerFileInfoURL))
/*     */     {
/* 363 */       return true;
/*     */     }
/*     */     
/* 366 */     long length = this.file.getLength();
/*     */     
/* 368 */     return (this.file.getDownloaded() == length) && (this.file.getFile().length() == length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean useDirectInput()
/*     */   {
/* 375 */     synchronized (this)
/*     */     {
/* 377 */       return (this.use_direct_input) || ((getPreferDirectInput()) && (canUseDirectInput()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setUseDirectInput()
/*     */   {
/* 385 */     synchronized (this)
/*     */     {
/* 387 */       this.use_direct_input = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPreferDirectInput(boolean prefer)
/*     */   {
/* 395 */     synchronized (this)
/*     */     {
/* 397 */       this.prefer_direct_input = prefer;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean getPreferDirectInput()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 506	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:prefer_direct_input	Z
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #404	-> byte code offset #0
/*     */     //   Java source line #406	-> byte code offset #4
/*     */     //   Java source line #407	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	TranscodeJobImpl
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   protected void setAutoRetry(boolean _auto_retry)
/*     */   {
/* 414 */     synchronized (this)
/*     */     {
/* 416 */       if (_auto_retry)
/*     */       {
/* 418 */         this.auto_retry = true;
/*     */         
/* 420 */         this.auto_retry_count += 1;
/*     */       }
/*     */       else
/*     */       {
/* 424 */         this.auto_retry = false;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected boolean isAutoRetry()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 502	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:auto_retry	Z
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #432	-> byte code offset #0
/*     */     //   Java source line #434	-> byte code offset #4
/*     */     //   Java source line #435	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	TranscodeJobImpl
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   protected int getAutoRetryCount()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 494	com/aelitis/azureus/core/devices/impl/TranscodeJobImpl:auto_retry_count	I
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: ireturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #441	-> byte code offset #0
/*     */     //   Java source line #443	-> byte code offset #4
/*     */     //   Java source line #444	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	TranscodeJobImpl
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   public void setEnableAutoRetry(boolean enabled)
/*     */   {
/* 451 */     this.auto_retry_enabled = enabled;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getEnableAutoRetry()
/*     */   {
/* 457 */     return this.auto_retry_enabled;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isStream()
/*     */   {
/* 463 */     return this.is_stream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setStream(InputStream _stream)
/*     */   {
/* 470 */     this.stream = _stream;
/*     */     
/* 472 */     this.stream_sem.releaseForever();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InputStream getStream(int wait_for_millis)
/*     */     throws IOException
/*     */   {
/* 481 */     if (this.state == 5)
/*     */     {
/* 483 */       throw new IOException("Transcode job failed: " + this.error);
/*     */     }
/* 485 */     if (this.state == 4)
/*     */     {
/* 487 */       throw new IOException("Transcode job cancelled");
/*     */     }
/* 489 */     if (this.state == 7)
/*     */     {
/* 491 */       throw new IOException("Transcode job removed");
/*     */     }
/*     */     
/* 494 */     this.stream_sem.reserve(wait_for_millis);
/*     */     
/* 496 */     return this.stream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void downloadWillBeRemoved(Download download)
/*     */     throws DownloadRemovalVetoException
/*     */   {
/* 505 */     if ((this.queue.getIndex(this) == 0) || (this.state == 3))
/*     */     {
/* 507 */       download.removeDownloadWillBeRemovedListener(this);
/*     */     }
/*     */     else
/*     */     {
/* 511 */       throw new DownloadRemovalVetoException(MessageText.getString("devices.xcode.remove.vetoed", new String[] { download.getName() }));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 520 */     if (this.download != null)
/*     */     {
/* 522 */       if (this.download.getDiskManagerFileInfo().length == 1)
/*     */       {
/* 524 */         return this.download.getName();
/*     */       }
/*     */       
/* 527 */       return this.download.getName() + ": " + this.file.getFile().getName();
/*     */     }
/*     */     
/*     */ 
/* 531 */     return this.file.getFile().getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 538 */     this.state = 0;
/* 539 */     this.error = null;
/* 540 */     this.percent_complete = 0;
/* 541 */     this.eta = Integer.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void starts()
/*     */   {
/* 547 */     synchronized (this)
/*     */     {
/* 549 */       this.started_on = SystemTime.getMonotonousTime();
/* 550 */       this.paused_on = 0L;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 555 */       if (this.state != 2)
/*     */       {
/* 557 */         this.state = 1;
/*     */       }
/*     */     }
/*     */     
/* 561 */     this.queue.jobChanged(this, false, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void failed(Throwable e)
/*     */   {
/* 568 */     this.queue.log("Transcode failed", e);
/*     */     
/* 570 */     synchronized (this)
/*     */     {
/* 572 */       if (this.state != 6)
/*     */       {
/* 574 */         this.state = 5;
/*     */         
/* 576 */         this.error = Debug.getNestedExceptionMessage(e);
/*     */         
/*     */ 
/*     */ 
/* 580 */         this.process_time += SystemTime.getMonotonousTime() - this.started_on;
/*     */         
/* 582 */         this.started_on = (this.paused_on = 0L);
/*     */       }
/*     */     }
/*     */     
/* 586 */     this.queue.jobChanged(this, false, true);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void complete()
/*     */   {
/* 592 */     synchronized (this)
/*     */     {
/* 594 */       this.state = 3;
/*     */       
/*     */ 
/*     */ 
/* 598 */       this.process_time += SystemTime.getMonotonousTime() - this.started_on;
/*     */       
/* 600 */       this.started_on = (this.paused_on = 0L);
/*     */     }
/*     */     
/* 603 */     if (this.download != null)
/*     */     {
/* 605 */       this.download.removeDownloadWillBeRemovedListener(this);
/*     */     }
/*     */     
/* 608 */     this.transcode_file.setComplete(true);
/*     */     
/* 610 */     this.queue.jobChanged(this, false, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateProgress(int _done, int _eta)
/*     */   {
/* 618 */     if ((this.percent_complete != _done) || (this.eta != _eta))
/*     */     {
/* 620 */       this.percent_complete = _done;
/* 621 */       this.eta = _eta;
/*     */       
/* 623 */       this.queue.jobChanged(this, false, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public TranscodeTarget getTarget()
/*     */   {
/* 630 */     return this.target;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTranscodeRequirement()
/*     */   {
/* 636 */     if (this.transcode_requirement >= 0)
/*     */     {
/* 638 */       return this.transcode_requirement;
/*     */     }
/*     */     
/* 641 */     return getDevice().getTranscodeRequirement();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void analyseNow(TranscodeAnalysisListener listener)
/*     */     throws TranscodeException
/*     */   {
/* 650 */     this.queue.analyse(this, listener);
/*     */   }
/*     */   
/*     */ 
/*     */   protected DeviceImpl getDevice()
/*     */   {
/* 656 */     return (DeviceImpl)this.target;
/*     */   }
/*     */   
/*     */ 
/*     */   public TranscodeProfile getProfile()
/*     */   {
/* 662 */     return this.profile;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskManagerFileInfo getFile()
/*     */   {
/* 668 */     return this.file;
/*     */   }
/*     */   
/*     */ 
/*     */   public TranscodeFileImpl getTranscodeFile()
/*     */   {
/* 674 */     return this.transcode_file;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIndex()
/*     */   {
/* 680 */     return this.queue.getIndex(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getState()
/*     */   {
/* 686 */     return this.state;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPercentComplete()
/*     */   {
/* 692 */     return this.percent_complete;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getETASecs()
/*     */   {
/* 698 */     if (this.eta <= 0)
/*     */     {
/* 700 */       return 0L;
/*     */     }
/* 702 */     if (this.eta == Integer.MAX_VALUE)
/*     */     {
/* 704 */       return Long.MAX_VALUE;
/*     */     }
/*     */     
/*     */ 
/* 708 */     return this.eta;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getETA()
/*     */   {
/* 715 */     if (this.eta < 0)
/*     */     {
/* 717 */       return null;
/*     */     }
/* 719 */     if (this.eta == Integer.MAX_VALUE)
/*     */     {
/* 721 */       return "∞";
/*     */     }
/*     */     
/*     */ 
/* 725 */     return TimeFormatter.format(this.eta);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getError()
/*     */   {
/* 732 */     return this.error;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canPause()
/*     */   {
/* 738 */     synchronized (this)
/*     */     {
/* 740 */       return !this.use_direct_input;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void pause()
/*     */   {
/* 747 */     synchronized (this)
/*     */     {
/* 749 */       if (this.use_direct_input)
/*     */       {
/* 751 */         return;
/*     */       }
/*     */       
/* 754 */       if (this.state == 1)
/*     */       {
/* 756 */         this.state = 2;
/*     */         
/* 758 */         this.paused_on = SystemTime.getMonotonousTime();
/*     */       }
/*     */       else
/*     */       {
/* 762 */         return;
/*     */       }
/*     */     }
/*     */     
/* 766 */     this.queue.jobChanged(this, false, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void resume()
/*     */   {
/* 772 */     synchronized (this)
/*     */     {
/* 774 */       if (this.state == 2)
/*     */       {
/* 776 */         this.state = 1;
/*     */         
/* 778 */         if ((this.paused_on > 0L) && (this.started_on > 0L))
/*     */         {
/* 780 */           this.process_time -= SystemTime.getMonotonousTime() - this.paused_on;
/*     */         }
/*     */       }
/*     */       else {
/* 784 */         return;
/*     */       }
/*     */     }
/*     */     
/* 788 */     this.queue.jobChanged(this, false, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void queue()
/*     */   {
/*     */     boolean do_resume;
/*     */     
/* 796 */     synchronized (this)
/*     */     {
/* 798 */       do_resume = this.state == 2;
/*     */     }
/*     */     
/* 801 */     if (do_resume)
/*     */     {
/* 803 */       resume();
/*     */       
/* 805 */       return;
/*     */     }
/*     */     
/* 808 */     synchronized (this)
/*     */     {
/* 810 */       if (this.state != 0)
/*     */       {
/* 812 */         if ((this.state == 1) || (this.state == 2))
/*     */         {
/*     */ 
/* 815 */           stop();
/*     */         }
/*     */         
/* 818 */         reset();
/*     */         
/*     */ 
/*     */ 
/* 822 */         this.use_direct_input = false;
/* 823 */         this.auto_retry = false;
/* 824 */         this.auto_retry_count = 0;
/* 825 */         this.is_stream = false;
/*     */       }
/*     */       else
/*     */       {
/* 829 */         return;
/*     */       }
/*     */     }
/*     */     
/* 833 */     this.queue.jobChanged(this, true, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */   {
/* 839 */     synchronized (this)
/*     */     {
/* 841 */       if (this.state != 6)
/*     */       {
/* 843 */         this.state = 6;
/*     */         
/* 845 */         this.process_time = 0L;
/*     */         
/* 847 */         this.started_on = 0L;
/*     */       }
/*     */       else
/*     */       {
/* 851 */         return;
/*     */       }
/*     */     }
/*     */     
/* 855 */     this.queue.jobChanged(this, true, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */     throws TranscodeActionVetoException
/*     */   {
/* 863 */     this.queue.remove(this, false);
/*     */   }
/*     */   
/*     */   public void removeForce()
/*     */   {
/*     */     try
/*     */     {
/* 870 */       this.queue.remove(this, true);
/*     */     }
/*     */     catch (TranscodeActionVetoException e)
/*     */     {
/* 874 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/*     */     boolean delete_file;
/*     */     
/* 883 */     synchronized (this)
/*     */     {
/* 885 */       delete_file = this.state != 3;
/*     */       
/* 887 */       this.state = 7;
/*     */     }
/*     */     
/* 890 */     if ((delete_file) && (!isStream())) {
/*     */       try
/*     */       {
/* 893 */         this.transcode_file.delete(true);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 897 */         this.queue.log("Faile to destroy job", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void moveUp()
/*     */   {
/* 905 */     this.queue.moveUp(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void moveDown()
/*     */   {
/* 911 */     this.queue.moveDown(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getProcessTime()
/*     */   {
/* 917 */     if (this.state == 3)
/*     */     {
/* 919 */       return this.process_time;
/*     */     }
/*     */     
/* 922 */     if (this.started_on == 0L)
/*     */     {
/* 924 */       if (this.process_time > 0L)
/*     */       {
/* 926 */         return this.process_time;
/*     */       }
/*     */       
/* 929 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/* 933 */     return SystemTime.getMonotonousTime() - this.started_on + this.process_time;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 940 */     writer.println("target=" + this.target.getID() + ", profile=" + this.profile.getName() + ", file=" + this.file);
/* 941 */     writer.println("tfile=" + this.transcode_file.getString());
/* 942 */     writer.println("stream=" + this.is_stream + ", state=" + this.state + ", treq=" + this.transcode_requirement + ", %=" + this.percent_complete + ", error=" + this.error);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeJobImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */