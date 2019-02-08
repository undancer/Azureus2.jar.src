/*      */ package org.gudy.azureus2.core3.download.impl;
/*      */ 
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.disk.DiskManager;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*      */ import org.gudy.azureus2.core3.peer.PEPeerManagerStats;
/*      */ import org.gudy.azureus2.core3.torrent.TOTorrent;
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
/*      */ public class DownloadManagerStatsImpl
/*      */   implements DownloadManagerStats
/*      */ {
/*      */   private static int share_ratio_progress_interval;
/*      */   private final DownloadManagerImpl download_manager;
/*      */   private int completed;
/*      */   private long saved_data_bytes_downloaded;
/*      */   private long saved_protocol_bytes_downloaded;
/*      */   private long saved_data_bytes_uploaded;
/*      */   private long saved_protocol_bytes_uploaded;
/*      */   private long session_start_data_bytes_downloaded;
/*      */   private long session_start_data_bytes_uploaded;
/*      */   
/*      */   static
/*      */   {
/*   45 */     COConfigurationManager.addAndFireParameterListener("Share Ratio Progress Interval", new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String name)
/*      */       {
/*      */ 
/*      */ 
/*   53 */         DownloadManagerStatsImpl.access$002(COConfigurationManager.getIntParameter(name));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   74 */   private long saved_discarded = 0L;
/*   75 */   private long saved_hashfails = 0L;
/*      */   
/*   77 */   private long saved_SecondsDownloading = 0L;
/*   78 */   private long saved_SecondsOnlySeeding = 0L;
/*      */   
/*   80 */   private int saved_SecondsSinceDownload = 0;
/*   81 */   private int saved_SecondsSinceUpload = 0;
/*      */   
/*   83 */   private long saved_peak_receive_rate = 0L;
/*   84 */   private long saved_peak_send_rate = 0L;
/*      */   
/*      */   private long saved_skipped_file_set_size;
/*      */   
/*      */   private long saved_skipped_but_downloaded;
/*   89 */   private long saved_completed_download_bytes = -1L;
/*      */   
/*   91 */   private int max_upload_rate_bps = 0;
/*   92 */   private int max_download_rate_bps = 0;
/*      */   
/*      */   private static final int HISTORY_MAX_SECS = 1800;
/*      */   
/*      */   private volatile boolean history_retention_required;
/*      */   private long[] history;
/*      */   private int history_pos;
/*      */   private boolean history_wrapped;
/*  100 */   private int last_sr_progress = -1;
/*      */   
/*      */   private static final int HISTORY_DIV = 64;
/*      */   
/*      */   protected DownloadManagerStatsImpl(DownloadManagerImpl dm)
/*      */   {
/*  106 */     this.download_manager = dm;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getDataReceiveRate()
/*      */   {
/*  112 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  114 */     if (pm != null)
/*      */     {
/*  116 */       return pm.getStats().getDataReceiveRate();
/*      */     }
/*      */     
/*  119 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getProtocolReceiveRate()
/*      */   {
/*  126 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  128 */     if (pm != null)
/*      */     {
/*  130 */       return pm.getStats().getProtocolReceiveRate();
/*      */     }
/*      */     
/*  133 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getDataSendRate()
/*      */   {
/*  141 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  143 */     if (pm != null)
/*      */     {
/*  145 */       return pm.getStats().getDataSendRate();
/*      */     }
/*      */     
/*  148 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getProtocolSendRate()
/*      */   {
/*  154 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  156 */     if (pm != null)
/*      */     {
/*  158 */       return pm.getStats().getProtocolSendRate();
/*      */     }
/*      */     
/*  161 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getPeakDataReceiveRate()
/*      */   {
/*  167 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  169 */     long result = this.saved_peak_receive_rate;
/*      */     
/*  171 */     if (pm != null)
/*      */     {
/*  173 */       result = Math.max(result, pm.getStats().getPeakDataReceiveRate());
/*      */     }
/*      */     
/*  176 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getPeakDataSendRate()
/*      */   {
/*  182 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  184 */     long result = this.saved_peak_send_rate;
/*      */     
/*  186 */     if (pm != null)
/*      */     {
/*  188 */       result = Math.max(result, pm.getStats().getPeakDataSendRate());
/*      */     }
/*      */     
/*  191 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSmoothedDataReceiveRate()
/*      */   {
/*  197 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  199 */     if (pm != null)
/*      */     {
/*  201 */       return pm.getStats().getSmoothedDataReceiveRate();
/*      */     }
/*      */     
/*  204 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSmoothedDataSendRate()
/*      */   {
/*  210 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  212 */     if (pm != null)
/*      */     {
/*  214 */       return pm.getStats().getSmoothedDataSendRate();
/*      */     }
/*      */     
/*  217 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public long getETA()
/*      */   {
/*  226 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  228 */     if (pm != null)
/*      */     {
/*  230 */       return pm.getETA(false);
/*      */     }
/*      */     
/*  233 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSmoothedETA()
/*      */   {
/*  239 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  241 */     if (pm != null)
/*      */     {
/*  243 */       return pm.getETA(true);
/*      */     }
/*      */     
/*  246 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCompleted()
/*      */   {
/*  255 */     DiskManager dm = this.download_manager.getDiskManager();
/*      */     
/*  257 */     if (dm == null) {
/*  258 */       int state = this.download_manager.getState();
/*  259 */       if ((state == 20) || (state == 30) || (state == 5))
/*      */       {
/*      */ 
/*  262 */         return this.completed;
/*      */       }
/*  264 */       return getDownloadCompleted(true);
/*      */     }
/*  266 */     if ((dm.getState() == 2) || (dm.getState() == 3) || (dm.getState() == 1))
/*      */     {
/*      */ 
/*  269 */       return dm.getPercentDone();
/*      */     }
/*  271 */     return getDownloadCompleted(true);
/*      */   }
/*      */   
/*      */   public void setCompleted(int _completed)
/*      */   {
/*  276 */     this.completed = _completed;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getDownloadCompleted(boolean bLive)
/*      */   {
/*  283 */     if (bLive) {
/*  284 */       DiskManager dm = this.download_manager.getDiskManager();
/*      */       
/*      */ 
/*      */ 
/*  288 */       if (dm != null)
/*      */       {
/*  290 */         int state = dm.getState();
/*      */         
/*  292 */         boolean transient_state = (state == 1) || (state == 2) || (state == 3);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  297 */         long total = dm.getTotalLength();
/*      */         
/*  299 */         long completed_download_bytes = total - dm.getRemaining();
/*  300 */         int computed_completion = total == 0L ? 0 : (int)(1000L * completed_download_bytes / total);
/*      */         
/*      */ 
/*  303 */         if (!transient_state)
/*      */         {
/*  305 */           this.saved_completed_download_bytes = completed_download_bytes;
/*      */         }
/*      */         
/*      */ 
/*  309 */         return computed_completion;
/*      */       }
/*      */     }
/*      */     
/*  313 */     long total = this.download_manager.getSize();
/*  314 */     int computed_completion = total == 0L ? 0 : (int)(1000L * getDownloadCompletedBytes() / total);
/*  315 */     return computed_completion;
/*      */   }
/*      */   
/*      */   public void setDownloadCompletedBytes(long completedBytes) {
/*  319 */     this.saved_completed_download_bytes = completedBytes;
/*      */   }
/*      */   
/*      */   public void recalcDownloadCompleteBytes() {
/*  323 */     DiskManager dm = getDiskManagerIfNotTransient();
/*  324 */     if (dm != null) {
/*  325 */       long total = dm.getTotalLength();
/*  326 */       this.saved_completed_download_bytes = (total - dm.getRemaining());
/*      */     }
/*  328 */     if (this.saved_completed_download_bytes < 0L)
/*      */     {
/*  330 */       DiskManagerFileInfo[] files = this.download_manager.getDiskManagerFileInfoSet().getFiles();
/*  331 */       long total_size = 0L;
/*  332 */       for (DiskManagerFileInfo file : files) {
/*  333 */         total_size += file.getDownloaded();
/*      */       }
/*  335 */       this.saved_completed_download_bytes = total_size;
/*      */     }
/*      */   }
/*      */   
/*      */   public long getDownloadCompletedBytes() {
/*  340 */     recalcDownloadCompleteBytes();
/*  341 */     return this.saved_completed_download_bytes;
/*      */   }
/*      */   
/*      */   public String getElapsedTime() {
/*  345 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  347 */     if (pm != null) {
/*  348 */       return pm.getElapsedTime();
/*      */     }
/*      */     
/*  351 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTimeStarted()
/*      */   {
/*  357 */     return getTimeStarted(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private long getTimeStarted(boolean mono)
/*      */   {
/*  364 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  366 */     if (pm != null)
/*      */     {
/*  368 */       return pm.getTimeStarted(mono);
/*      */     }
/*      */     
/*  371 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTimeStartedSeeding()
/*      */   {
/*  377 */     return getTimeStartedSeeding(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private long getTimeStartedSeeding(boolean mono)
/*      */   {
/*  384 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  386 */     if (pm != null)
/*      */     {
/*  388 */       return pm.getTimeStartedSeeding(mono);
/*      */     }
/*      */     
/*  391 */     return -1L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalDataBytesReceived()
/*      */   {
/*  397 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  399 */     if (pm != null) {
/*  400 */       return this.saved_data_bytes_downloaded + pm.getStats().getTotalDataBytesReceived();
/*      */     }
/*  402 */     return this.saved_data_bytes_downloaded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getSessionDataBytesReceived()
/*      */   {
/*  409 */     long total = getTotalDataBytesReceived();
/*      */     
/*  411 */     long res = total - this.session_start_data_bytes_downloaded;
/*      */     
/*  413 */     if (res < 0L)
/*      */     {
/*  415 */       this.session_start_data_bytes_downloaded = total;
/*      */       
/*  417 */       res = 0L;
/*      */     }
/*      */     
/*  420 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalGoodDataBytesReceived()
/*      */   {
/*  426 */     long downloaded = getTotalDataBytesReceived();
/*      */     
/*  428 */     downloaded -= getHashFailBytes() + getDiscarded();
/*      */     
/*  430 */     if (downloaded < 0L)
/*      */     {
/*  432 */       downloaded = 0L;
/*      */     }
/*      */     
/*  435 */     return downloaded;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalProtocolBytesReceived()
/*      */   {
/*  441 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  443 */     if (pm != null) {
/*  444 */       return this.saved_protocol_bytes_downloaded + pm.getStats().getTotalProtocolBytesReceived();
/*      */     }
/*      */     
/*  447 */     return this.saved_protocol_bytes_downloaded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void resetTotalBytesSentReceived(long new_sent, long new_received)
/*      */   {
/*  455 */     boolean running = this.download_manager.getPeerManager() != null;
/*      */     
/*  457 */     if (running)
/*      */     {
/*  459 */       this.download_manager.stopIt(70, false, false);
/*      */     }
/*      */     
/*      */ 
/*  463 */     if (new_sent >= 0L)
/*      */     {
/*  465 */       this.saved_data_bytes_uploaded = new_sent;
/*  466 */       this.session_start_data_bytes_uploaded = new_sent;
/*  467 */       this.saved_protocol_bytes_uploaded = 0L;
/*      */     }
/*      */     
/*  470 */     if (new_received >= 0L)
/*      */     {
/*  472 */       this.saved_data_bytes_downloaded = new_received;
/*  473 */       this.session_start_data_bytes_downloaded = new_received;
/*  474 */       this.saved_protocol_bytes_downloaded = 0L;
/*      */     }
/*      */     
/*  477 */     this.saved_discarded = 0L;
/*  478 */     this.saved_hashfails = 0L;
/*      */     
/*  480 */     if (running)
/*      */     {
/*  482 */       this.download_manager.setStateWaiting();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalDataBytesSent()
/*      */   {
/*  489 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  491 */     if (pm != null) {
/*  492 */       return this.saved_data_bytes_uploaded + pm.getStats().getTotalDataBytesSent();
/*      */     }
/*      */     
/*  495 */     return this.saved_data_bytes_uploaded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getTotalProtocolBytesSent()
/*      */   {
/*  502 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  504 */     if (pm != null)
/*      */     {
/*  506 */       return this.saved_protocol_bytes_uploaded + pm.getStats().getTotalProtocolBytesSent();
/*      */     }
/*      */     
/*  509 */     return this.saved_protocol_bytes_uploaded;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSessionDataBytesSent()
/*      */   {
/*  515 */     long total = getTotalDataBytesSent();
/*      */     
/*  517 */     long res = total - this.session_start_data_bytes_uploaded;
/*      */     
/*  519 */     if (res < 0L)
/*      */     {
/*  521 */       this.session_start_data_bytes_uploaded = total;
/*      */       
/*  523 */       res = 0L;
/*      */     }
/*      */     
/*  526 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRecentHistoryRetention(boolean required)
/*      */   {
/*  533 */     synchronized (this)
/*      */     {
/*  535 */       if (required)
/*      */       {
/*  537 */         if (!this.history_retention_required)
/*      */         {
/*  539 */           this.history = new long['܈'];
/*      */           
/*  541 */           this.history_pos = 0;
/*      */           
/*  543 */           this.history_retention_required = true;
/*      */         }
/*      */       }
/*      */       else {
/*  547 */         this.history = null;
/*      */         
/*  549 */         this.history_retention_required = false;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int[][] getRecentHistory()
/*      */   {
/*  559 */     synchronized (this)
/*      */     {
/*  561 */       if (this.history == null)
/*      */       {
/*  563 */         return new int[3][0];
/*      */       }
/*      */       
/*      */ 
/*  567 */       int entries = this.history_wrapped ? 1800 : this.history_pos;
/*  568 */       int start = this.history_wrapped ? this.history_pos : 0;
/*      */       
/*  570 */       int[][] result = new int[3][entries];
/*      */       
/*  572 */       int pos = start;
/*      */       
/*  574 */       for (int i = 0; i < entries; i++)
/*      */       {
/*  576 */         if (pos == 1800)
/*      */         {
/*  578 */           pos = 0;
/*      */         }
/*      */         
/*  581 */         long entry = this.history[(pos++)];
/*      */         
/*  583 */         int send_rate = (int)(entry >> 42 & 0x1FFFFF);
/*  584 */         int recv_rate = (int)(entry >> 21 & 0x1FFFFF);
/*  585 */         int swarm_rate = (int)(entry & 0x1FFFFF);
/*      */         
/*  587 */         result[0][i] = (send_rate * 64);
/*  588 */         result[1][i] = (recv_rate * 64);
/*  589 */         result[2][i] = (swarm_rate * 64);
/*      */       }
/*      */       
/*  592 */       return result;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void timerTick(int tick_count)
/*      */   {
/*  601 */     if (tick_count % 15 == 0)
/*      */     {
/*  603 */       if (this.last_sr_progress == -1)
/*      */       {
/*  605 */         long temp = this.download_manager.getDownloadState().getLongAttribute("sr.prog");
/*      */         
/*  607 */         this.last_sr_progress = ((int)temp);
/*      */       }
/*      */       
/*  610 */       if (share_ratio_progress_interval <= 0)
/*      */       {
/*      */ 
/*      */ 
/*  614 */         if (this.last_sr_progress != 0)
/*      */         {
/*  616 */           this.last_sr_progress = 0;
/*      */           
/*  618 */           this.download_manager.getDownloadState().setLongAttribute("sr.prog", 0L);
/*      */         }
/*      */       }
/*      */       else {
/*  622 */         int current_sr = getShareRatio();
/*      */         
/*  624 */         current_sr = current_sr / share_ratio_progress_interval * share_ratio_progress_interval;
/*      */         
/*  626 */         if (current_sr != this.last_sr_progress)
/*      */         {
/*  628 */           this.last_sr_progress = current_sr;
/*      */           
/*  630 */           long data = (SystemTime.getCurrentTime() / 1000L << 32) + this.last_sr_progress;
/*      */           
/*  632 */           this.download_manager.getDownloadState().setLongAttribute("sr.prog", data);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  637 */     if (!this.history_retention_required)
/*      */     {
/*  639 */       return;
/*      */     }
/*      */     
/*  642 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  644 */     if (pm == null)
/*      */     {
/*  646 */       return;
/*      */     }
/*      */     
/*  649 */     PEPeerManagerStats stats = pm.getStats();
/*      */     
/*  651 */     long send_rate = stats.getDataSendRate() + stats.getProtocolSendRate();
/*  652 */     long receive_rate = stats.getDataReceiveRate() + stats.getProtocolReceiveRate();
/*  653 */     long peer_swarm_average = getTotalAveragePerPeer();
/*      */     
/*  655 */     long entry = (send_rate - 1L + 32L) / 64L << 42 & 0x7FFFFC0000000000 | (receive_rate - 1L + 32L) / 64L << 21 & 0x3FFFFE00000 | (peer_swarm_average - 1L + 32L) / 64L & 0x1FFFFF;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  661 */     synchronized (this)
/*      */     {
/*  663 */       if (this.history != null)
/*      */       {
/*  665 */         this.history[(this.history_pos++)] = entry;
/*      */         
/*  667 */         if (this.history_pos == 1800)
/*      */         {
/*  669 */           this.history_pos = 0;
/*  670 */           this.history_wrapped = true;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getRemaining()
/*      */   {
/*  679 */     DiskManager disk_manager = getDiskManagerIfNotTransient();
/*      */     
/*  681 */     if (disk_manager == null)
/*      */     {
/*  683 */       long size = this.download_manager.getSize();
/*      */       
/*  685 */       return size - getDownloadCompletedBytes();
/*      */     }
/*      */     
/*      */ 
/*  689 */     return disk_manager.getRemaining();
/*      */   }
/*      */   
/*      */   private DiskManager getDiskManagerIfNotTransient()
/*      */   {
/*  694 */     DiskManager dm = this.download_manager.getDiskManager();
/*  695 */     if (dm == null) {
/*  696 */       return null;
/*      */     }
/*      */     
/*  699 */     int state = dm.getState();
/*      */     
/*  701 */     boolean transient_state = (state == 1) || (state == 2) || (state == 3);
/*      */     
/*  703 */     return transient_state ? null : dm;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getDiscarded()
/*      */   {
/*  709 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  711 */     if (pm != null)
/*      */     {
/*  713 */       return this.saved_discarded + pm.getStats().getTotalDiscarded();
/*      */     }
/*      */     
/*  716 */     return this.saved_discarded;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public long getHashFailCount()
/*      */   {
/*  723 */     TOTorrent t = this.download_manager.getTorrent();
/*      */     
/*  725 */     if (t == null)
/*      */     {
/*  727 */       return 0L;
/*      */     }
/*      */     
/*  730 */     long total = getHashFailBytes();
/*      */     
/*  732 */     long res = total / t.getPieceLength();
/*      */     
/*  734 */     if ((res == 0L) && (total > 0L))
/*      */     {
/*  736 */       res = 1L;
/*      */     }
/*      */     
/*  739 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getHashFailBytes()
/*      */   {
/*  745 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  747 */     if (pm != null)
/*      */     {
/*  749 */       return this.saved_hashfails + pm.getStats().getTotalHashFailBytes();
/*      */     }
/*      */     
/*  752 */     return this.saved_hashfails;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalAverage()
/*      */   {
/*  758 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  760 */     if (pm != null)
/*      */     {
/*  762 */       return pm.getStats().getTotalAverage();
/*      */     }
/*      */     
/*  765 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getTotalAveragePerPeer()
/*      */   {
/*  771 */     int div = this.download_manager.getNbPeers() + (this.download_manager.isDownloadComplete(false) ? 0 : 1);
/*      */     
/*  773 */     long average = div < 1 ? 0L : getTotalAverage() / div;
/*      */     
/*  775 */     return average;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getShareRatio()
/*      */   {
/*  781 */     long downloaded = getTotalGoodDataBytesReceived();
/*  782 */     long uploaded = getTotalDataBytesSent();
/*      */     
/*  784 */     if (downloaded <= 0L)
/*      */     {
/*  786 */       return -1;
/*      */     }
/*      */     
/*  789 */     return (int)(1000L * uploaded / downloaded);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setShareRatio(int ratio)
/*      */   {
/*  796 */     if (ratio < 0) {
/*  797 */       ratio = 0;
/*      */     }
/*      */     
/*  800 */     if (ratio > 1000000) {
/*  801 */       ratio = 1000000;
/*      */     }
/*      */     
/*  804 */     DiskManagerFileInfo[] files = this.download_manager.getDiskManagerFileInfoSet().getFiles();
/*      */     
/*  806 */     long total_size = 0L;
/*      */     
/*  808 */     for (DiskManagerFileInfo file : files)
/*      */     {
/*  810 */       if (!file.isSkipped())
/*      */       {
/*  812 */         total_size += file.getLength();
/*      */       }
/*      */     }
/*      */     
/*  816 */     if (total_size == 0L)
/*      */     {
/*      */ 
/*      */ 
/*  820 */       return;
/*      */     }
/*      */     
/*  823 */     this.saved_hashfails = 0L;
/*  824 */     this.saved_discarded = 0L;
/*  825 */     this.saved_data_bytes_downloaded = 0L;
/*  826 */     this.saved_data_bytes_uploaded = 0L;
/*      */     
/*  828 */     long downloaded = getTotalGoodDataBytesReceived();
/*  829 */     long uploaded = getTotalDataBytesSent();
/*      */     
/*      */ 
/*      */ 
/*  833 */     long target_downloaded = total_size;
/*  834 */     long target_uploaded = ratio * total_size / 1000L;
/*      */     
/*  836 */     this.saved_data_bytes_downloaded = (target_downloaded - downloaded);
/*  837 */     this.saved_data_bytes_uploaded = (target_uploaded - uploaded);
/*      */     
/*  839 */     if (this.download_manager.getPeerManager() == null)
/*      */     {
/*  841 */       saveSessionTotals();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSecondsDownloading()
/*      */   {
/*  848 */     long lTimeStartedDL = getTimeStarted(true);
/*  849 */     if (lTimeStartedDL >= 0L) {
/*  850 */       long lTimeEndedDL = getTimeStartedSeeding(true);
/*  851 */       if (lTimeEndedDL == -1L) {
/*  852 */         lTimeEndedDL = SystemTime.getMonotonousTime();
/*      */       }
/*  854 */       if (lTimeEndedDL > lTimeStartedDL) {
/*  855 */         return this.saved_SecondsDownloading + (lTimeEndedDL - lTimeStartedDL) / 1000L;
/*      */       }
/*      */     }
/*  858 */     return this.saved_SecondsDownloading;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSecondsOnlySeeding()
/*      */   {
/*  864 */     long lTimeStarted = getTimeStartedSeeding(true);
/*  865 */     if (lTimeStarted >= 0L) {
/*  866 */       return this.saved_SecondsOnlySeeding + (SystemTime.getMonotonousTime() - lTimeStarted) / 1000L;
/*      */     }
/*      */     
/*  869 */     return this.saved_SecondsOnlySeeding;
/*      */   }
/*      */   
/*      */ 
/*      */   public float getAvailability()
/*      */   {
/*  875 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  877 */     if (pm == null)
/*      */     {
/*  879 */       return -1.0F;
/*      */     }
/*      */     
/*  882 */     return pm.getMinAvailability();
/*      */   }
/*      */   
/*      */ 
/*      */   public long getBytesUnavailable()
/*      */   {
/*  888 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  890 */     if (pm == null)
/*      */     {
/*  892 */       return -1L;
/*      */     }
/*      */     
/*  895 */     return pm.getBytesUnavailable();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getUploadRateLimitBytesPerSecond()
/*      */   {
/*  902 */     return this.max_upload_rate_bps;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setUploadRateLimitBytesPerSecond(int max_rate_bps)
/*      */   {
/*  909 */     this.max_upload_rate_bps = max_rate_bps;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDownloadRateLimitBytesPerSecond()
/*      */   {
/*  915 */     return this.max_download_rate_bps;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setDownloadRateLimitBytesPerSecond(int max_rate_bps)
/*      */   {
/*  922 */     this.max_download_rate_bps = max_rate_bps;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTimeSinceLastDataReceivedInSeconds()
/*      */   {
/*  928 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  930 */     int res = this.saved_SecondsSinceDownload;
/*      */     
/*  932 */     if (pm != null)
/*      */     {
/*  934 */       int current = pm.getStats().getTimeSinceLastDataReceivedInSeconds();
/*      */       
/*  936 */       if (current >= 0)
/*      */       {
/*      */ 
/*      */ 
/*  940 */         res = current;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  947 */       else if (res >= 0)
/*      */       {
/*  949 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  951 */         long elapsed = now - pm.getTimeStarted(false);
/*      */         
/*  953 */         if (elapsed < 0L)
/*      */         {
/*  955 */           elapsed = 0L;
/*      */         }
/*      */         
/*  958 */         res = (int)(res + elapsed / 1000L);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  963 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getTimeSinceLastDataSentInSeconds()
/*      */   {
/*  969 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/*  971 */     int res = this.saved_SecondsSinceUpload;
/*      */     
/*  973 */     if (pm != null)
/*      */     {
/*  975 */       int current = pm.getStats().getTimeSinceLastDataSentInSeconds();
/*      */       
/*  977 */       if (current >= 0)
/*      */       {
/*      */ 
/*      */ 
/*  981 */         res = current;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*  988 */       else if (res >= 0)
/*      */       {
/*  990 */         long now = SystemTime.getCurrentTime();
/*      */         
/*  992 */         long elapsed = now - pm.getTimeStarted(false);
/*      */         
/*  994 */         if (elapsed < 0L)
/*      */         {
/*  996 */           elapsed = 0L;
/*      */         }
/*      */         
/*  999 */         res = (int)(res + elapsed / 1000L);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1004 */     return res;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getAvailWentBadTime()
/*      */   {
/* 1010 */     PEPeerManager pm = this.download_manager.getPeerManager();
/*      */     
/* 1012 */     if (pm != null)
/*      */     {
/* 1014 */       long bad_time = pm.getAvailWentBadTime();
/*      */       
/* 1016 */       if (bad_time > 0L)
/*      */       {
/*      */ 
/*      */ 
/* 1020 */         return bad_time;
/*      */       }
/*      */       
/* 1023 */       if (pm.getMinAvailability() >= 1.0D)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 1028 */         return 0L;
/*      */       }
/*      */     }
/*      */     
/* 1032 */     DownloadManagerState state = this.download_manager.getDownloadState();
/*      */     
/* 1034 */     return state.getLongAttribute("badavail");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void saveSessionTotals()
/*      */   {
/* 1042 */     this.saved_data_bytes_downloaded = getTotalDataBytesReceived();
/* 1043 */     this.saved_data_bytes_uploaded = getTotalDataBytesSent();
/*      */     
/* 1045 */     this.saved_protocol_bytes_downloaded = getTotalProtocolBytesReceived();
/* 1046 */     this.saved_protocol_bytes_uploaded = getTotalProtocolBytesSent();
/*      */     
/* 1048 */     this.saved_discarded = getDiscarded();
/* 1049 */     this.saved_hashfails = getHashFailBytes();
/*      */     
/* 1051 */     this.saved_SecondsDownloading = getSecondsDownloading();
/* 1052 */     this.saved_SecondsOnlySeeding = getSecondsOnlySeeding();
/*      */     
/* 1054 */     this.saved_SecondsSinceDownload = getTimeSinceLastDataReceivedInSeconds();
/* 1055 */     this.saved_SecondsSinceUpload = getTimeSinceLastDataSentInSeconds();
/*      */     
/* 1057 */     this.saved_peak_receive_rate = getPeakDataReceiveRate();
/* 1058 */     this.saved_peak_send_rate = getPeakDataSendRate();
/*      */     
/* 1060 */     DownloadManagerState state = this.download_manager.getDownloadState();
/*      */     
/* 1062 */     state.setIntAttribute("timesincedl", this.saved_SecondsSinceDownload);
/* 1063 */     state.setIntAttribute("timesinceul", this.saved_SecondsSinceUpload);
/*      */     
/* 1065 */     state.setLongAttribute("badavail", getAvailWentBadTime());
/*      */     
/* 1067 */     state.setLongAttribute("pkdo", this.saved_peak_receive_rate);
/* 1068 */     state.setLongAttribute("pkup", this.saved_peak_send_rate);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setSavedDownloadedUploaded(long d, long u)
/*      */   {
/* 1076 */     this.saved_data_bytes_downloaded = d;
/* 1077 */     this.saved_data_bytes_uploaded = u;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void restoreSessionTotals(long _saved_data_bytes_downloaded, long _saved_data_bytes_uploaded, long _saved_discarded, long _saved_hashfails, long _saved_SecondsDownloading, long _saved_SecondsOnlySeeding)
/*      */   {
/* 1089 */     this.saved_data_bytes_downloaded = _saved_data_bytes_downloaded;
/* 1090 */     this.saved_data_bytes_uploaded = _saved_data_bytes_uploaded;
/* 1091 */     this.saved_discarded = _saved_discarded;
/* 1092 */     this.saved_hashfails = _saved_hashfails;
/* 1093 */     this.saved_SecondsDownloading = _saved_SecondsDownloading;
/* 1094 */     this.saved_SecondsOnlySeeding = _saved_SecondsOnlySeeding;
/*      */     
/* 1096 */     this.session_start_data_bytes_downloaded = this.saved_data_bytes_downloaded;
/* 1097 */     this.session_start_data_bytes_uploaded = _saved_data_bytes_uploaded;
/*      */     
/* 1099 */     DownloadManagerState state = this.download_manager.getDownloadState();
/*      */     
/* 1101 */     this.saved_SecondsSinceDownload = state.getIntAttribute("timesincedl");
/* 1102 */     this.saved_SecondsSinceUpload = state.getIntAttribute("timesinceul");
/*      */     
/* 1104 */     this.saved_peak_receive_rate = state.getLongAttribute("pkdo");
/* 1105 */     this.saved_peak_send_rate = state.getLongAttribute("pkup");
/*      */     
/* 1107 */     if ((this.saved_data_bytes_downloaded > 0L) && (this.saved_completed_download_bytes == 0L))
/*      */     {
/*      */ 
/* 1110 */       this.saved_completed_download_bytes = -1L;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSkippedFileStats(long skipped_file_set_size, long skipped_but_downloaded)
/*      */   {
/* 1120 */     this.saved_skipped_file_set_size = skipped_file_set_size;
/* 1121 */     this.saved_skipped_but_downloaded = skipped_but_downloaded;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getRemainingExcludingDND()
/*      */   {
/* 1127 */     DiskManager dm = this.download_manager.getDiskManager();
/*      */     
/* 1129 */     if (dm != null) {
/* 1130 */       return dm.getRemainingExcludingDND();
/*      */     }
/*      */     
/* 1133 */     long remaining = getRemaining();
/* 1134 */     long rem = remaining - (this.saved_skipped_file_set_size - this.saved_skipped_but_downloaded);
/*      */     
/* 1136 */     if (rem < 0L)
/*      */     {
/* 1138 */       rem = 0L;
/*      */     }
/*      */     
/* 1141 */     return rem;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getSizeExcludingDND()
/*      */   {
/* 1147 */     DiskManager dm = this.download_manager.getDiskManager();
/*      */     
/* 1149 */     if (dm != null) {
/* 1150 */       return dm.getSizeExcludingDND();
/*      */     }
/*      */     
/* 1153 */     long totalLength = this.download_manager.getSize();
/* 1154 */     return totalLength - this.saved_skipped_file_set_size;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getPercentDoneExcludingDND()
/*      */   {
/* 1160 */     long sizeExcludingDND = getSizeExcludingDND();
/* 1161 */     if (sizeExcludingDND == 0L) {
/* 1162 */       return 1000;
/*      */     }
/* 1164 */     if (sizeExcludingDND < 0L) {
/* 1165 */       return 0;
/*      */     }
/* 1167 */     float pct = (float)(sizeExcludingDND - getRemainingExcludingDND()) / (float)sizeExcludingDND;
/*      */     
/* 1169 */     return (int)(1000.0F * pct);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void generateEvidence(IndentWriter writer)
/*      */   {
/* 1177 */     writer.println("DownloadManagerStats");
/*      */     try
/*      */     {
/* 1180 */       writer.indent();
/*      */       
/* 1182 */       writer.println("recv_d=" + getTotalDataBytesReceived() + ",recv_p=" + getTotalProtocolBytesReceived() + ",recv_g=" + getTotalGoodDataBytesReceived() + ",sent_d=" + getTotalDataBytesSent() + ",sent_p=" + getTotalProtocolBytesSent() + ",discard=" + getDiscarded() + ",hash_fails=" + getHashFailCount() + "/" + getHashFailBytes() + ",comp=" + getCompleted() + "[live:" + getDownloadCompleted(true) + "/" + getDownloadCompleted(false) + "]" + ",remaining=" + getRemaining());
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1191 */       writer.println("down_lim=" + getDownloadRateLimitBytesPerSecond() + ",up_lim=" + getUploadRateLimitBytesPerSecond());
/*      */     }
/*      */     finally
/*      */     {
/* 1195 */       writer.exdent();
/*      */     }
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */