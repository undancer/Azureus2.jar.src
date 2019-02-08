/*     */ package org.gudy.azureus2.core3.peer.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.GeneralUtils;
/*     */ import com.aelitis.azureus.core.util.average.MovingImmediateAverage;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerAdapter;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManagerStats;
/*     */ import org.gudy.azureus2.core3.peer.impl.control.PEPeerControlImpl;
/*     */ import org.gudy.azureus2.core3.util.Average;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class PEPeerManagerStatsImpl
/*     */   implements PEPeerManagerStats
/*     */ {
/*     */   private final PEPeerManagerAdapter adapter;
/*  38 */   private long total_data_bytes_received = 0L;
/*  39 */   private long total_protocol_bytes_received = 0L;
/*     */   
/*  41 */   private long total_data_bytes_sent = 0L;
/*  42 */   private long total_protocol_bytes_sent = 0L;
/*     */   
/*  44 */   private long total_data_bytes_received_lan = 0L;
/*  45 */   private long total_protocol_bytes_received_lan = 0L;
/*     */   
/*  47 */   private long total_data_bytes_sent_lan = 0L;
/*  48 */   private long total_protocol_bytes_sent_lan = 0L;
/*     */   
/*     */   private long totalDiscarded;
/*     */   
/*     */   private long hash_fail_bytes;
/*     */   
/*     */   private int last_data_received_seconds;
/*     */   private int last_data_sent_seconds;
/*  56 */   private final Average data_receive_speed = Average.getInstance(1000, 10);
/*  57 */   private final Average protocol_receive_speed = Average.getInstance(1000, 10);
/*     */   
/*  59 */   private final Average data_send_speed = Average.getInstance(1000, 10);
/*  60 */   private final Average protocol_send_speed = Average.getInstance(1000, 10);
/*     */   
/*  62 */   private final Average overallSpeed = Average.getInstance(5000, 100);
/*     */   
/*     */   private long smooth_last_sent;
/*     */   
/*     */   private long smooth_last_received;
/*  67 */   private int current_smoothing_window = GeneralUtils.getSmoothUpdateWindow();
/*  68 */   private int current_smoothing_interval = GeneralUtils.getSmoothUpdateInterval();
/*     */   
/*  70 */   private MovingImmediateAverage smoothed_receive_rate = GeneralUtils.getSmoothAverage();
/*  71 */   private MovingImmediateAverage smoothed_send_rate = GeneralUtils.getSmoothAverage();
/*     */   
/*     */   private long peak_receive_rate;
/*     */   
/*     */   private long peak_send_rate;
/*     */   
/*     */   private int total_incoming;
/*     */   
/*     */   private int total_outgoing;
/*     */   
/*     */   public PEPeerManagerStatsImpl(PEPeerControlImpl _manager)
/*     */   {
/*  83 */     this.adapter = _manager.getAdapter();
/*     */   }
/*     */   
/*     */   public void discarded(PEPeer peer, int length) {
/*  87 */     this.totalDiscarded += length;
/*     */     
/*  89 */     this.adapter.discarded(peer, length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void hashFailed(int length)
/*     */   {
/*  96 */     this.hash_fail_bytes += length;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalHashFailBytes()
/*     */   {
/* 102 */     return this.hash_fail_bytes;
/*     */   }
/*     */   
/*     */   public void dataBytesReceived(PEPeer peer, int length) {
/* 106 */     this.total_data_bytes_received += length;
/* 107 */     if (peer.isLANLocal()) {
/* 108 */       this.total_data_bytes_received_lan += length;
/*     */     }
/* 110 */     this.data_receive_speed.addValue(length);
/*     */     
/* 112 */     if (length > 0) {
/* 113 */       this.last_data_received_seconds = ((int)(SystemTime.getCurrentTime() / 1000L));
/*     */     }
/*     */     
/* 116 */     this.adapter.dataBytesReceived(peer, length);
/*     */   }
/*     */   
/*     */   public void protocolBytesReceived(PEPeer peer, int length) {
/* 120 */     this.total_protocol_bytes_received += length;
/* 121 */     if (peer.isLANLocal()) {
/* 122 */       this.total_protocol_bytes_received_lan += length;
/*     */     }
/* 124 */     this.protocol_receive_speed.addValue(length);
/*     */     
/* 126 */     this.adapter.protocolBytesReceived(peer, length);
/*     */   }
/*     */   
/*     */   public void dataBytesSent(PEPeer peer, int length)
/*     */   {
/* 131 */     this.total_data_bytes_sent += length;
/* 132 */     if (peer.isLANLocal()) {
/* 133 */       this.total_data_bytes_sent_lan += length;
/*     */     }
/* 135 */     this.data_send_speed.addValue(length);
/*     */     
/* 137 */     if (length > 0) {
/* 138 */       this.last_data_sent_seconds = ((int)(SystemTime.getCurrentTime() / 1000L));
/*     */     }
/*     */     
/* 141 */     this.adapter.dataBytesSent(peer, length);
/*     */   }
/*     */   
/*     */   public void protocolBytesSent(PEPeer peer, int length) {
/* 145 */     this.total_protocol_bytes_sent += length;
/* 146 */     if (peer.isLANLocal()) {
/* 147 */       this.total_protocol_bytes_sent_lan += length;
/*     */     }
/* 149 */     this.protocol_send_speed.addValue(length);
/*     */     
/* 151 */     this.adapter.protocolBytesSent(peer, length);
/*     */   }
/*     */   
/*     */   public void haveNewPiece(int pieceLength)
/*     */   {
/* 156 */     this.overallSpeed.addValue(pieceLength);
/*     */   }
/*     */   
/*     */   public long getDataReceiveRate() {
/* 160 */     return this.data_receive_speed.getAverage();
/*     */   }
/*     */   
/*     */   public long getProtocolReceiveRate() {
/* 164 */     return this.protocol_receive_speed.getAverage();
/*     */   }
/*     */   
/*     */   public long getDataSendRate()
/*     */   {
/* 169 */     return this.data_send_speed.getAverage();
/*     */   }
/*     */   
/*     */   public long getProtocolSendRate() {
/* 173 */     return this.protocol_send_speed.getAverage();
/*     */   }
/*     */   
/*     */   public long getTotalDiscarded() {
/* 177 */     return this.totalDiscarded;
/*     */   }
/*     */   
/*     */   public void setTotalDiscarded(long total) {
/* 181 */     this.totalDiscarded = total;
/*     */   }
/*     */   
/*     */   public long getTotalDataBytesSent() {
/* 185 */     return this.total_data_bytes_sent;
/*     */   }
/*     */   
/*     */   public long getTotalProtocolBytesSent() {
/* 189 */     return this.total_protocol_bytes_sent;
/*     */   }
/*     */   
/*     */   public long getTotalDataBytesReceived() {
/* 193 */     return this.total_data_bytes_received;
/*     */   }
/*     */   
/*     */   public long getTotalProtocolBytesReceived() {
/* 197 */     return this.total_protocol_bytes_received;
/*     */   }
/*     */   
/*     */   public long getTotalDataBytesSentNoLan()
/*     */   {
/* 202 */     return Math.max(this.total_data_bytes_sent - this.total_data_bytes_sent_lan, 0L);
/*     */   }
/*     */   
/*     */   public long getTotalProtocolBytesSentNoLan() {
/* 206 */     return Math.max(this.total_protocol_bytes_sent - this.total_protocol_bytes_sent_lan, 0L);
/*     */   }
/*     */   
/*     */   public long getTotalDataBytesReceivedNoLan() {
/* 210 */     return Math.max(this.total_data_bytes_received - this.total_data_bytes_received_lan, 0L);
/*     */   }
/*     */   
/*     */   public long getTotalProtocolBytesReceivedNoLan() {
/* 214 */     return Math.max(this.total_protocol_bytes_received - this.total_protocol_bytes_received_lan, 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getTotalAverage()
/*     */   {
/* 221 */     return this.overallSpeed.getAverage() + getDataReceiveRate();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTimeSinceLastDataReceivedInSeconds()
/*     */   {
/* 227 */     if (this.last_data_received_seconds == 0)
/*     */     {
/* 229 */       return -1;
/*     */     }
/*     */     
/* 232 */     int now = (int)(SystemTime.getCurrentTime() / 1000L);
/*     */     
/* 234 */     if (now < this.last_data_received_seconds)
/*     */     {
/* 236 */       this.last_data_received_seconds = now;
/*     */     }
/*     */     
/* 239 */     return now - this.last_data_received_seconds;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTimeSinceLastDataSentInSeconds()
/*     */   {
/* 245 */     if (this.last_data_sent_seconds == 0)
/*     */     {
/* 247 */       return -1;
/*     */     }
/*     */     
/* 250 */     int now = (int)(SystemTime.getCurrentTime() / 1000L);
/*     */     
/* 252 */     if (now < this.last_data_sent_seconds)
/*     */     {
/* 254 */       this.last_data_sent_seconds = now;
/*     */     }
/*     */     
/* 257 */     return now - this.last_data_sent_seconds;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void haveNewConnection(boolean incoming)
/*     */   {
/* 264 */     if (incoming)
/*     */     {
/* 266 */       this.total_incoming += 1;
/*     */     }
/*     */     else
/*     */     {
/* 270 */       this.total_outgoing += 1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTotalIncomingConnections()
/*     */   {
/* 277 */     return this.total_incoming;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getTotalOutgoingConnections()
/*     */   {
/* 283 */     return this.total_outgoing;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToReceive()
/*     */   {
/* 289 */     return this.adapter.getPermittedBytesToReceive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void permittedReceiveBytesUsed(int bytes)
/*     */   {
/* 296 */     this.adapter.permittedReceiveBytesUsed(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPermittedBytesToSend()
/*     */   {
/* 302 */     return this.adapter.getPermittedBytesToSend();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void permittedSendBytesUsed(int bytes)
/*     */   {
/* 309 */     this.adapter.permittedSendBytesUsed(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSmoothedDataReceiveRate()
/*     */   {
/* 315 */     return (this.smoothed_receive_rate.getAverage() / this.current_smoothing_interval);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSmoothedDataSendRate()
/*     */   {
/* 321 */     return (this.smoothed_send_rate.getAverage() / this.current_smoothing_interval);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPeakDataReceiveRate()
/*     */   {
/* 327 */     return this.peak_receive_rate;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getPeakDataSendRate()
/*     */   {
/* 333 */     return this.peak_send_rate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void update(int tick_count)
/*     */   {
/* 340 */     this.peak_receive_rate = Math.max(this.peak_receive_rate, this.data_receive_speed.getAverage());
/* 341 */     this.peak_send_rate = Math.max(this.peak_send_rate, this.data_send_speed.getAverage());
/*     */     
/* 343 */     if (tick_count % this.current_smoothing_interval == 0)
/*     */     {
/* 345 */       int current_window = GeneralUtils.getSmoothUpdateWindow();
/*     */       
/* 347 */       if (this.current_smoothing_window != current_window)
/*     */       {
/* 349 */         this.current_smoothing_window = current_window;
/* 350 */         this.current_smoothing_interval = GeneralUtils.getSmoothUpdateInterval();
/* 351 */         this.smoothed_receive_rate = GeneralUtils.getSmoothAverage();
/* 352 */         this.smoothed_send_rate = GeneralUtils.getSmoothAverage();
/*     */       }
/*     */       
/* 355 */       long up = this.total_data_bytes_sent;
/* 356 */       long down = this.total_data_bytes_received;
/*     */       
/* 358 */       this.smoothed_send_rate.update(up - this.smooth_last_sent);
/* 359 */       this.smoothed_receive_rate.update(down - this.smooth_last_received);
/*     */       
/* 361 */       this.smooth_last_sent = up;
/* 362 */       this.smooth_last_received = down;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/PEPeerManagerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */