/*     */ package com.aelitis.azureus.core.dht.control.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlStats;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDB;
/*     */ import com.aelitis.azureus.core.dht.db.DHTDBStats;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouter;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportStats;
/*     */ import org.gudy.azureus2.core3.util.Average;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
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
/*     */ public class DHTControlStatsImpl
/*     */   implements DHTTransportFullStats, DHTControlStats
/*     */ {
/*     */   private static final int UPDATE_INTERVAL = 10000;
/*     */   private static final int UPDATE_PERIOD = 120;
/*     */   private final DHTControlImpl control;
/*  44 */   private final Average packets_in_average = Average.getInstance(10000, 120);
/*  45 */   private final Average packets_out_average = Average.getInstance(10000, 120);
/*  46 */   private final Average bytes_in_average = Average.getInstance(10000, 120);
/*  47 */   private final Average bytes_out_average = Average.getInstance(10000, 120);
/*     */   
/*     */   private DHTTransportStats transport_snapshot;
/*     */   
/*     */   private long[] router_snapshot;
/*     */   
/*     */   private int[] value_details_snapshot;
/*     */   
/*     */   protected DHTControlStatsImpl(DHTControlImpl _control)
/*     */   {
/*  57 */     this.control = _control;
/*     */     
/*  59 */     this.transport_snapshot = this.control.getTransport().getStats().snapshot();
/*     */     
/*  61 */     this.router_snapshot = this.control.getRouter().getStats().getStats();
/*     */     
/*  63 */     SimpleTimer.addPeriodicEvent("DHTCS:update", 10000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/*  72 */         DHTControlStatsImpl.this.update();
/*     */         
/*  74 */         DHTControlStatsImpl.this.control.poke();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void update()
/*     */   {
/*  82 */     DHTTransport transport = this.control.getTransport();
/*     */     
/*  84 */     DHTTransportStats t_stats = transport.getStats().snapshot();
/*     */     
/*  86 */     this.packets_in_average.addValue(t_stats.getPacketsReceived() - this.transport_snapshot.getPacketsReceived());
/*     */     
/*     */ 
/*  89 */     this.packets_out_average.addValue(t_stats.getPacketsSent() - this.transport_snapshot.getPacketsSent());
/*     */     
/*     */ 
/*  92 */     this.bytes_in_average.addValue(t_stats.getBytesReceived() - this.transport_snapshot.getBytesReceived());
/*     */     
/*     */ 
/*  95 */     this.bytes_out_average.addValue(t_stats.getBytesSent() - this.transport_snapshot.getBytesSent());
/*     */     
/*     */ 
/*  98 */     this.transport_snapshot = t_stats;
/*     */     
/* 100 */     this.router_snapshot = this.control.getRouter().getStats().getStats();
/*     */     
/* 102 */     this.value_details_snapshot = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesReceived()
/*     */   {
/* 108 */     return this.transport_snapshot.getBytesReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalBytesSent()
/*     */   {
/* 114 */     return this.transport_snapshot.getBytesSent();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalPacketsReceived()
/*     */   {
/* 120 */     return this.transport_snapshot.getPacketsReceived();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getTotalPacketsSent()
/*     */   {
/* 126 */     return this.transport_snapshot.getPacketsSent();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getTotalPingsReceived()
/*     */   {
/* 133 */     return this.transport_snapshot.getPings()[3];
/*     */   }
/*     */   
/*     */   public long getTotalFindNodesReceived()
/*     */   {
/* 138 */     return this.transport_snapshot.getFindNodes()[3];
/*     */   }
/*     */   
/*     */   public long getTotalFindValuesReceived()
/*     */   {
/* 143 */     return this.transport_snapshot.getFindValues()[3];
/*     */   }
/*     */   
/*     */   public long getTotalStoresReceived()
/*     */   {
/* 148 */     return this.transport_snapshot.getStores()[3];
/*     */   }
/*     */   
/*     */   public long getTotalKeyBlocksReceived()
/*     */   {
/* 153 */     return this.transport_snapshot.getKeyBlocks()[3];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getAverageBytesReceived()
/*     */   {
/* 161 */     return this.bytes_in_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAverageBytesSent()
/*     */   {
/* 167 */     return this.bytes_out_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAveragePacketsReceived()
/*     */   {
/* 173 */     return this.packets_in_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getAveragePacketsSent()
/*     */   {
/* 179 */     return this.packets_out_average.getAverage();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getIncomingRequests()
/*     */   {
/* 185 */     return this.transport_snapshot.getIncomingRequests();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int[] getValueDetails()
/*     */   {
/* 192 */     int[] vd = this.value_details_snapshot;
/*     */     
/* 194 */     if (vd == null)
/*     */     {
/* 196 */       vd = this.control.getDataBase().getStats().getValueDetails();
/*     */       
/* 198 */       this.value_details_snapshot = vd;
/*     */     }
/*     */     
/* 201 */     return vd;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBValuesStored()
/*     */   {
/* 207 */     int[] vd = getValueDetails();
/*     */     
/* 209 */     return vd[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBKeyCount()
/*     */   {
/* 215 */     return this.control.getDataBase().getStats().getKeyCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBValueCount()
/*     */   {
/* 221 */     return this.control.getDataBase().getStats().getValueCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBKeysBlocked()
/*     */   {
/* 227 */     return this.control.getDataBase().getStats().getKeyBlockCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBKeyDivSizeCount()
/*     */   {
/* 233 */     int[] vd = getValueDetails();
/*     */     
/* 235 */     return vd[5];
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBKeyDivFreqCount()
/*     */   {
/* 241 */     int[] vd = getValueDetails();
/*     */     
/* 243 */     return vd[4];
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDBStoreSize()
/*     */   {
/* 249 */     return this.control.getDataBase().getStats().getSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getRouterNodes()
/*     */   {
/* 257 */     return this.router_snapshot[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRouterLeaves()
/*     */   {
/* 263 */     return this.router_snapshot[1];
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRouterContacts()
/*     */   {
/* 269 */     return this.router_snapshot[2];
/*     */   }
/*     */   
/*     */ 
/*     */   public long getRouterUptime()
/*     */   {
/* 275 */     return this.control.getRouterUptime();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRouterCount()
/*     */   {
/* 281 */     return this.control.getRouterCount();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 287 */     return "5.7.6.0";
/*     */   }
/*     */   
/*     */ 
/*     */   public long getEstimatedDHTSize()
/*     */   {
/* 293 */     return this.control.getEstimatedDHTSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 299 */     return "transport:" + getTotalBytesReceived() + "," + getTotalBytesSent() + "," + getTotalPacketsReceived() + "," + getTotalPacketsSent() + "," + getTotalPingsReceived() + "," + getTotalFindNodesReceived() + "," + getTotalFindValuesReceived() + "," + getTotalStoresReceived() + "," + getTotalKeyBlocksReceived() + "," + getAverageBytesReceived() + "," + getAverageBytesSent() + "," + getAveragePacketsReceived() + "," + getAveragePacketsSent() + "," + getIncomingRequests() + ",router:" + getRouterNodes() + "," + getRouterLeaves() + "," + getRouterContacts() + ",database:" + getDBKeyCount() + "," + getDBValueCount() + "," + getDBValuesStored() + "," + getDBStoreSize() + "," + getDBKeyDivFreqCount() + "," + getDBKeyDivSizeCount() + "," + getDBKeysBlocked() + ",version:" + getVersion() + "," + getRouterUptime() + "," + getRouterCount();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/control/impl/DHTControlStatsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */