/*     */ package org.gudy.azureus2.core3.global.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.GeneralUtils;
/*     */ import com.aelitis.azureus.core.util.average.MovingImmediateAverage;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.util.Average;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer.TimerTickReceiver;
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
/*     */ public class GlobalManagerStatsImpl
/*     */   implements GlobalManagerStats, SimpleTimer.TimerTickReceiver
/*     */ {
/*     */   private final GlobalManagerImpl manager;
/*     */   private long smooth_last_sent;
/*     */   private long smooth_last_received;
/*  48 */   private int current_smoothing_window = GeneralUtils.getSmoothUpdateWindow();
/*  49 */   private int current_smoothing_interval = GeneralUtils.getSmoothUpdateInterval();
/*     */   
/*  51 */   private MovingImmediateAverage smoothed_receive_rate = GeneralUtils.getSmoothAverage();
/*  52 */   private MovingImmediateAverage smoothed_send_rate = GeneralUtils.getSmoothAverage();
/*     */   
/*     */   private long total_data_bytes_received;
/*     */   
/*     */   private long total_protocol_bytes_received;
/*     */   
/*     */   private long totalDiscarded;
/*     */   
/*     */   private long total_data_bytes_sent;
/*     */   
/*     */   private long total_protocol_bytes_sent;
/*     */   
/*     */   private int data_send_speed_at_close;
/*  65 */   private final Average data_receive_speed = Average.getInstance(1000, 10);
/*  66 */   private final Average protocol_receive_speed = Average.getInstance(1000, 10);
/*  67 */   private final Average data_receive_speed_no_lan = Average.getInstance(1000, 10);
/*  68 */   private final Average protocol_receive_speed_no_lan = Average.getInstance(1000, 10);
/*     */   
/*  70 */   private final Average data_send_speed = Average.getInstance(1000, 10);
/*  71 */   private final Average protocol_send_speed = Average.getInstance(1000, 10);
/*  72 */   private final Average data_send_speed_no_lan = Average.getInstance(1000, 10);
/*  73 */   private final Average protocol_send_speed_no_lan = Average.getInstance(1000, 10);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected GlobalManagerStatsImpl(GlobalManagerImpl _manager)
/*     */   {
/*  80 */     this.manager = _manager;
/*     */     
/*  82 */     load();
/*     */     
/*  84 */     SimpleTimer.addTickReceiver(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void load()
/*     */   {
/*  90 */     this.data_send_speed_at_close = COConfigurationManager.getIntParameter("globalmanager.stats.send.speed.at.close", 0);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void save()
/*     */   {
/*  96 */     COConfigurationManager.setParameter("globalmanager.stats.send.speed.at.close", getDataSendRate());
/*     */   }
/*     */   
/*     */ 
/*     */   public int getDataSendRateAtClose()
/*     */   {
/* 102 */     return this.data_send_speed_at_close;
/*     */   }
/*     */   
/*     */ 
/*     */   public void discarded(int length)
/*     */   {
/* 108 */     this.totalDiscarded += length;
/*     */   }
/*     */   
/*     */   public void dataBytesReceived(int length, boolean LAN) {
/* 112 */     this.total_data_bytes_received += length;
/* 113 */     if (!LAN) {
/* 114 */       this.data_receive_speed_no_lan.addValue(length);
/*     */     }
/* 116 */     this.data_receive_speed.addValue(length);
/*     */   }
/*     */   
/*     */   public void protocolBytesReceived(int length, boolean LAN)
/*     */   {
/* 121 */     this.total_protocol_bytes_received += length;
/* 122 */     if (!LAN) {
/* 123 */       this.protocol_receive_speed_no_lan.addValue(length);
/*     */     }
/* 125 */     this.protocol_receive_speed.addValue(length);
/*     */   }
/*     */   
/*     */   public void dataBytesSent(int length, boolean LAN) {
/* 129 */     this.total_data_bytes_sent += length;
/* 130 */     if (!LAN) {
/* 131 */       this.data_send_speed_no_lan.addValue(length);
/*     */     }
/* 133 */     this.data_send_speed.addValue(length);
/*     */   }
/*     */   
/*     */   public void protocolBytesSent(int length, boolean LAN) {
/* 137 */     this.total_protocol_bytes_sent += length;
/* 138 */     if (!LAN) {
/* 139 */       this.protocol_send_speed_no_lan.addValue(length);
/*     */     }
/* 141 */     this.protocol_send_speed.addValue(length);
/*     */   }
/*     */   
/*     */   public int getDataReceiveRate() {
/* 145 */     return (int)this.data_receive_speed.getAverage();
/*     */   }
/*     */   
/* 148 */   public int getDataReceiveRateNoLAN() { return (int)this.data_receive_speed_no_lan.getAverage(); }
/*     */   
/*     */   public int getDataReceiveRateNoLAN(int average_period) {
/* 151 */     return (int)(average_period <= 0 ? this.data_receive_speed_no_lan.getAverage() : this.data_receive_speed_no_lan.getAverage(average_period));
/*     */   }
/*     */   
/* 154 */   public int getProtocolReceiveRate() { return (int)this.protocol_receive_speed.getAverage(); }
/*     */   
/*     */   public int getProtocolReceiveRateNoLAN() {
/* 157 */     return (int)this.protocol_receive_speed_no_lan.getAverage();
/*     */   }
/*     */   
/* 160 */   public int getProtocolReceiveRateNoLAN(int average_period) { return (int)(average_period <= 0 ? this.protocol_receive_speed_no_lan.getAverage() : this.protocol_receive_speed_no_lan.getAverage(average_period)); }
/*     */   
/*     */   public int getDataAndProtocolReceiveRate()
/*     */   {
/* 164 */     return (int)(this.protocol_receive_speed.getAverage() + this.data_receive_speed.getAverage());
/*     */   }
/*     */   
/*     */   public int getDataSendRate() {
/* 168 */     return (int)this.data_send_speed.getAverage();
/*     */   }
/*     */   
/* 171 */   public int getDataSendRateNoLAN() { return (int)this.data_send_speed_no_lan.getAverage(); }
/*     */   
/*     */   public int getDataSendRateNoLAN(int average_period) {
/* 174 */     return (int)(average_period <= 0 ? this.data_send_speed_no_lan.getAverage() : this.data_send_speed_no_lan.getAverage(average_period));
/*     */   }
/*     */   
/*     */   public int getProtocolSendRate() {
/* 178 */     return (int)this.protocol_send_speed.getAverage();
/*     */   }
/*     */   
/* 181 */   public int getProtocolSendRateNoLAN() { return (int)this.protocol_send_speed_no_lan.getAverage(); }
/*     */   
/*     */   public int getProtocolSendRateNoLAN(int average_period) {
/* 184 */     return (int)(average_period <= 0 ? this.protocol_send_speed_no_lan.getAverage() : this.protocol_send_speed_no_lan.getAverage(average_period));
/*     */   }
/*     */   
/*     */   public int getDataAndProtocolSendRate() {
/* 188 */     return (int)(this.protocol_send_speed.getAverage() + this.data_send_speed.getAverage());
/*     */   }
/*     */   
/*     */   public long getTotalDataBytesSent() {
/* 192 */     return this.total_data_bytes_sent;
/*     */   }
/*     */   
/*     */   public long getTotalProtocolBytesSent() {
/* 196 */     return this.total_protocol_bytes_sent;
/*     */   }
/*     */   
/*     */   public long getTotalDataBytesReceived()
/*     */   {
/* 201 */     return this.total_data_bytes_received;
/*     */   }
/*     */   
/*     */   public long getTotalProtocolBytesReceived() {
/* 205 */     return this.total_protocol_bytes_received;
/*     */   }
/*     */   
/*     */   public long getTotalDiscardedRaw()
/*     */   {
/* 210 */     return this.totalDiscarded;
/*     */   }
/*     */   
/*     */   public long getTotalSwarmsPeerRate(boolean downloading, boolean seeding)
/*     */   {
/* 215 */     return this.manager.getTotalSwarmsPeerRate(downloading, seeding);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void tick(long mono_now, int tick_count)
/*     */   {
/* 223 */     if (tick_count % this.current_smoothing_interval == 0)
/*     */     {
/* 225 */       int current_window = GeneralUtils.getSmoothUpdateWindow();
/*     */       
/* 227 */       if (this.current_smoothing_window != current_window)
/*     */       {
/* 229 */         this.current_smoothing_window = current_window;
/* 230 */         this.current_smoothing_interval = GeneralUtils.getSmoothUpdateInterval();
/* 231 */         this.smoothed_receive_rate = GeneralUtils.getSmoothAverage();
/* 232 */         this.smoothed_send_rate = GeneralUtils.getSmoothAverage();
/*     */       }
/*     */       
/* 235 */       long up = this.total_data_bytes_sent + this.total_protocol_bytes_sent;
/* 236 */       long down = this.total_data_bytes_received + this.total_protocol_bytes_received;
/*     */       
/* 238 */       this.smoothed_send_rate.update(up - this.smooth_last_sent);
/* 239 */       this.smoothed_receive_rate.update(down - this.smooth_last_received);
/*     */       
/* 241 */       this.smooth_last_sent = up;
/* 242 */       this.smooth_last_received = down;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSmoothedSendRate()
/*     */   {
/* 249 */     return (this.smoothed_send_rate.getAverage() / this.current_smoothing_interval);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getSmoothedReceiveRate()
/*     */   {
/* 255 */     return (this.smoothed_receive_rate.getAverage() / this.current_smoothing_interval);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/impl/GlobalManagerStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */