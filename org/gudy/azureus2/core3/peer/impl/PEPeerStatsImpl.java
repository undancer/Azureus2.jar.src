/*     */ package org.gudy.azureus2.core3.peer.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.NetworkManager;
/*     */ import com.aelitis.azureus.core.networkmanager.RateHandler;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerStats;
/*     */ import org.gudy.azureus2.core3.util.Average;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.pluginsimpl.local.network.ConnectionImpl;
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
/*     */ public class PEPeerStatsImpl
/*     */   implements PEPeerStats
/*     */ {
/*     */   private PEPeer owner;
/*  42 */   private long total_data_bytes_received = 0L;
/*  43 */   private long total_protocol_bytes_received = 0L;
/*     */   
/*  45 */   private final Average data_receive_speed = Average.getInstance(1000, 10);
/*  46 */   private final Average protocol_receive_speed = Average.getInstance(1000, 10);
/*     */   
/*  48 */   private long total_data_bytes_sent = 0L;
/*  49 */   private long total_protocol_bytes_sent = 0L;
/*     */   
/*  51 */   private final Average data_send_speed = Average.getInstance(1000, 5);
/*  52 */   private final Average protocol_send_speed = Average.getInstance(1000, 5);
/*     */   
/*  54 */   private final Average receive_speed_for_choking = Average.getInstance(1000, 20);
/*  55 */   private final Average estimated_download_speed = Average.getInstance(5000, 100);
/*  56 */   private final Average estimated_upload_speed = Average.getInstance(3000, 60);
/*     */   
/*  58 */   private long total_bytes_discarded = 0L;
/*  59 */   private long total_bytes_downloaded = 0L;
/*     */   
/*  61 */   private long disk_read_bytes = 0L;
/*  62 */   private int disk_read_count = 0;
/*  63 */   private int disk_aggregated_read_count = 0;
/*     */   private long last_new_piece_time;
/*     */   
/*     */   public PEPeerStatsImpl(PEPeer _owner)
/*     */   {
/*  68 */     this.owner = _owner;
/*     */   }
/*     */   
/*     */ 
/*     */   public PEPeer getPeer()
/*     */   {
/*  74 */     return this.owner;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPeer(PEPeer peer)
/*     */   {
/*  81 */     this.owner = peer;
/*     */   }
/*     */   
/*     */   public void dataBytesSent(int num_bytes) {
/*  85 */     this.total_data_bytes_sent += num_bytes;
/*  86 */     this.data_send_speed.addValue(num_bytes);
/*     */   }
/*     */   
/*     */   public void protocolBytesSent(int num_bytes) {
/*  90 */     this.total_protocol_bytes_sent += num_bytes;
/*  91 */     this.protocol_send_speed.addValue(num_bytes);
/*     */   }
/*     */   
/*     */   public void dataBytesReceived(int num_bytes) {
/*  95 */     this.total_data_bytes_received += num_bytes;
/*  96 */     this.data_receive_speed.addValue(num_bytes);
/*  97 */     this.receive_speed_for_choking.addValue(num_bytes);
/*     */   }
/*     */   
/*     */   public void protocolBytesReceived(int num_bytes) {
/* 101 */     this.total_protocol_bytes_received += num_bytes;
/* 102 */     this.protocol_receive_speed.addValue(num_bytes);
/*     */   }
/*     */   
/*     */   public void bytesDiscarded(int num_bytes)
/*     */   {
/* 107 */     this.total_bytes_discarded += num_bytes;
/*     */   }
/*     */   
/*     */   public void hasNewPiece(int piece_size) {
/* 111 */     this.total_bytes_downloaded += piece_size;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 116 */     if (this.owner.getTimeSinceConnectionEstablished() > 5000L)
/*     */     {
/* 118 */       this.estimated_download_speed.addValue(piece_size);
/*     */       
/* 120 */       this.last_new_piece_time = SystemTime.getCurrentTime();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getEstimatedSecondsToCompletion()
/*     */   {
/* 131 */     long remaining = this.owner.getBytesRemaining();
/*     */     
/* 133 */     if (remaining == 0L)
/*     */     {
/* 135 */       return 0L;
/*     */     }
/*     */     
/* 138 */     long download_rate = this.estimated_download_speed.getAverage();
/*     */     
/* 140 */     long our_send_rate = getDataSendRate();
/*     */     
/*     */ 
/*     */ 
/* 144 */     if (download_rate < our_send_rate)
/*     */     {
/* 146 */       download_rate = our_send_rate;
/*     */     }
/*     */     
/* 149 */     if (download_rate == 0L)
/*     */     {
/* 151 */       return Long.MAX_VALUE;
/*     */     }
/*     */     
/* 154 */     if (this.last_new_piece_time > 0L)
/*     */     {
/* 156 */       long elapsed_secs = (SystemTime.getCurrentTime() - this.last_new_piece_time) / 1000L;
/*     */       
/* 158 */       remaining -= elapsed_secs * download_rate;
/*     */     }
/*     */     
/* 161 */     long secs_remaining = remaining / download_rate;
/*     */     
/* 163 */     if (secs_remaining <= 0L)
/*     */     {
/* 165 */       secs_remaining = 1L;
/*     */     }
/*     */     
/* 168 */     return secs_remaining;
/*     */   }
/*     */   
/*     */   public void statisticalSentPiece(int piece_size) {
/* 172 */     this.estimated_upload_speed.addValue(piece_size);
/*     */   }
/*     */   
/* 175 */   public long getDataReceiveRate() { return this.data_receive_speed.getAverage(); }
/* 176 */   public long getProtocolReceiveRate() { return this.protocol_receive_speed.getAverage(); }
/*     */   
/* 178 */   public long getDataSendRate() { return this.data_send_speed.getAverage(); }
/* 179 */   public long getProtocolSendRate() { return this.protocol_send_speed.getAverage(); }
/*     */   
/* 181 */   public long getSmoothDataReceiveRate() { return this.receive_speed_for_choking.getAverage(); }
/*     */   
/* 183 */   public long getTotalBytesDiscarded() { return this.total_bytes_discarded; }
/*     */   
/* 185 */   public long getTotalBytesDownloadedByPeer() { return this.total_bytes_downloaded; }
/*     */   
/* 187 */   public long getEstimatedDownloadRateOfPeer() { return this.estimated_download_speed.getAverage(); }
/* 188 */   public long getEstimatedUploadRateOfPeer() { return this.estimated_upload_speed.getAverage(); }
/*     */   
/* 190 */   public long getTotalDataBytesReceived() { return this.total_data_bytes_received; }
/* 191 */   public long getTotalProtocolBytesReceived() { return this.total_protocol_bytes_received; }
/*     */   
/* 193 */   public long getTotalDataBytesSent() { return this.total_data_bytes_sent; }
/* 194 */   public long getTotalProtocolBytesSent() { return this.total_protocol_bytes_sent; }
/*     */   
/*     */ 
/*     */ 
/*     */   public void diskReadComplete(long bytes)
/*     */   {
/* 200 */     this.disk_read_bytes += bytes;
/* 201 */     this.disk_read_count += 1;
/* 202 */     if (bytes > 0L) {
/* 203 */       this.disk_aggregated_read_count += 1;
/*     */     }
/*     */   }
/*     */   
/* 207 */   public int getTotalDiskReadCount() { return this.disk_read_count; }
/* 208 */   public int getAggregatedDiskReadCount() { return this.disk_aggregated_read_count; }
/* 209 */   public long getTotalDiskReadBytes() { return this.disk_read_bytes; }
/*     */   
/* 211 */   public void setUploadRateLimitBytesPerSecond(int bytes) { this.owner.setUploadRateLimitBytesPerSecond(bytes); }
/* 212 */   public void setDownloadRateLimitBytesPerSecond(int bytes) { this.owner.setDownloadRateLimitBytesPerSecond(bytes); }
/* 213 */   public int getUploadRateLimitBytesPerSecond() { return this.owner.getUploadRateLimitBytesPerSecond(); }
/* 214 */   public int getDownloadRateLimitBytesPerSecond() { return this.owner.getDownloadRateLimitBytesPerSecond(); }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToSend()
/*     */   {
/* 219 */     return NetworkManager.getSingleton().getRateHandler(((ConnectionImpl)this.owner.getPluginConnection()).getCoreConnection(), true).getCurrentNumBytesAllowed()[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void permittedSendBytesUsed(int num)
/*     */   {
/* 228 */     NetworkManager.getSingleton().getRateHandler(((ConnectionImpl)this.owner.getPluginConnection()).getCoreConnection(), true).bytesProcessed(num, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPermittedBytesToReceive()
/*     */   {
/* 236 */     return NetworkManager.getSingleton().getRateHandler(((ConnectionImpl)this.owner.getPluginConnection()).getCoreConnection(), false).getCurrentNumBytesAllowed()[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void permittedReceiveBytesUsed(int num)
/*     */   {
/* 245 */     NetworkManager.getSingleton().getRateHandler(((ConnectionImpl)this.owner.getPluginConnection()).getCoreConnection(), false).bytesProcessed(num, 0);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPeerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */