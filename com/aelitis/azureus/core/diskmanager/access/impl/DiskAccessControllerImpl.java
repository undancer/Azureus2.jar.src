/*     */ package com.aelitis.azureus.core.diskmanager.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessController;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessControllerStats;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequest;
/*     */ import com.aelitis.azureus.core.diskmanager.access.DiskAccessRequestListener;
/*     */ import com.aelitis.azureus.core.diskmanager.cache.CacheFile;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStatsProvider;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ public class DiskAccessControllerImpl
/*     */   implements DiskAccessController, AzureusCoreStatsProvider
/*     */ {
/*     */   final DiskAccessControllerInstance read_dispatcher;
/*     */   final DiskAccessControllerInstance write_dispatcher;
/*     */   
/*     */   public DiskAccessControllerImpl(String _name, int _max_read_threads, int _max_read_mb, int _max_write_threads, int _max_write_mb)
/*     */   {
/*  50 */     boolean enable_read_aggregation = COConfigurationManager.getBooleanParameter("diskmanager.perf.read.aggregate.enable");
/*  51 */     int read_aggregation_request_limit = COConfigurationManager.getIntParameter("diskmanager.perf.read.aggregate.request.limit", 4);
/*  52 */     int read_aggregation_byte_limit = COConfigurationManager.getIntParameter("diskmanager.perf.read.aggregate.byte.limit", 65536);
/*     */     
/*     */ 
/*  55 */     boolean enable_write_aggregation = COConfigurationManager.getBooleanParameter("diskmanager.perf.write.aggregate.enable");
/*  56 */     int write_aggregation_request_limit = COConfigurationManager.getIntParameter("diskmanager.perf.write.aggregate.request.limit", 8);
/*  57 */     int write_aggregation_byte_limit = COConfigurationManager.getIntParameter("diskmanager.perf.write.aggregate.byte.limit", 131072);
/*     */     
/*  59 */     this.read_dispatcher = new DiskAccessControllerInstance(_name + "/" + "read", enable_read_aggregation, read_aggregation_request_limit, read_aggregation_byte_limit, _max_read_threads, _max_read_mb);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  68 */     this.write_dispatcher = new DiskAccessControllerInstance(_name + "/" + "write", enable_write_aggregation, write_aggregation_request_limit, write_aggregation_byte_limit, _max_write_threads, _max_write_mb);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */     Set types = new HashSet();
/*     */     
/*  79 */     types.add("disk.read.queue.length");
/*  80 */     types.add("disk.read.queue.bytes");
/*  81 */     types.add("disk.read.request.count");
/*  82 */     types.add("disk.read.request.single");
/*  83 */     types.add("disk.read.request.multiple");
/*  84 */     types.add("disk.read.request.blocks");
/*  85 */     types.add("disk.read.bytes.total");
/*  86 */     types.add("disk.read.bytes.single");
/*  87 */     types.add("disk.read.bytes.multiple");
/*  88 */     types.add("disk.read.io.time");
/*  89 */     types.add("disk.read.io.count");
/*     */     
/*  91 */     types.add("disk.write.queue.length");
/*  92 */     types.add("disk.write.queue.bytes");
/*  93 */     types.add("disk.write.request.count");
/*  94 */     types.add("disk.write.request.blocks");
/*  95 */     types.add("disk.write.bytes.total");
/*  96 */     types.add("disk.write.bytes.single");
/*  97 */     types.add("disk.write.bytes.multiple");
/*  98 */     types.add("disk.write.io.time");
/*     */     
/* 100 */     AzureusCoreStats.registerProvider(types, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateStats(Set types, Map values)
/*     */   {
/* 110 */     if (types.contains("disk.read.queue.length"))
/*     */     {
/* 112 */       values.put("disk.read.queue.length", new Long(this.read_dispatcher.getQueueSize()));
/*     */     }
/*     */     
/* 115 */     if (types.contains("disk.read.queue.bytes"))
/*     */     {
/* 117 */       values.put("disk.read.queue.bytes", new Long(this.read_dispatcher.getQueuedBytes()));
/*     */     }
/*     */     
/* 120 */     if (types.contains("disk.read.request.count"))
/*     */     {
/* 122 */       values.put("disk.read.request.count", new Long(this.read_dispatcher.getTotalRequests()));
/*     */     }
/*     */     
/* 125 */     if (types.contains("disk.read.request.single"))
/*     */     {
/* 127 */       values.put("disk.read.request.single", new Long(this.read_dispatcher.getTotalSingleRequests()));
/*     */     }
/*     */     
/* 130 */     if (types.contains("disk.read.request.multiple"))
/*     */     {
/* 132 */       values.put("disk.read.request.multiple", new Long(this.read_dispatcher.getTotalAggregatedRequests()));
/*     */     }
/*     */     
/* 135 */     if (types.contains("disk.read.request.blocks"))
/*     */     {
/* 137 */       values.put("disk.read.request.blocks", new Long(this.read_dispatcher.getBlockCount()));
/*     */     }
/*     */     
/* 140 */     if (types.contains("disk.read.bytes.total"))
/*     */     {
/* 142 */       values.put("disk.read.bytes.total", new Long(this.read_dispatcher.getTotalBytes()));
/*     */     }
/*     */     
/* 145 */     if (types.contains("disk.read.bytes.single"))
/*     */     {
/* 147 */       values.put("disk.read.bytes.single", new Long(this.read_dispatcher.getTotalSingleBytes()));
/*     */     }
/*     */     
/* 150 */     if (types.contains("disk.read.bytes.multiple"))
/*     */     {
/* 152 */       values.put("disk.read.bytes.multiple", new Long(this.read_dispatcher.getTotalAggregatedBytes()));
/*     */     }
/*     */     
/* 155 */     if (types.contains("disk.read.io.time"))
/*     */     {
/* 157 */       values.put("disk.read.io.time", new Long(this.read_dispatcher.getIOTime()));
/*     */     }
/*     */     
/* 160 */     if (types.contains("disk.read.io.count"))
/*     */     {
/* 162 */       values.put("disk.read.io.count", new Long(this.read_dispatcher.getIOCount()));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 167 */     if (types.contains("disk.write.queue.length"))
/*     */     {
/* 169 */       values.put("disk.write.queue.length", new Long(this.write_dispatcher.getQueueSize()));
/*     */     }
/*     */     
/* 172 */     if (types.contains("disk.write.queue.bytes"))
/*     */     {
/* 174 */       values.put("disk.write.queue.bytes", new Long(this.write_dispatcher.getQueuedBytes()));
/*     */     }
/*     */     
/* 177 */     if (types.contains("disk.write.request.count"))
/*     */     {
/* 179 */       values.put("disk.write.request.count", new Long(this.write_dispatcher.getTotalRequests()));
/*     */     }
/*     */     
/* 182 */     if (types.contains("disk.write.request.blocks"))
/*     */     {
/* 184 */       values.put("disk.write.request.blocks", new Long(this.write_dispatcher.getBlockCount()));
/*     */     }
/*     */     
/* 187 */     if (types.contains("disk.write.bytes.total"))
/*     */     {
/* 189 */       values.put("disk.write.bytes.total", new Long(this.write_dispatcher.getTotalBytes()));
/*     */     }
/*     */     
/* 192 */     if (types.contains("disk.write.bytes.single"))
/*     */     {
/* 194 */       values.put("disk.write.bytes.single", new Long(this.write_dispatcher.getTotalSingleBytes()));
/*     */     }
/*     */     
/* 197 */     if (types.contains("disk.write.bytes.multiple"))
/*     */     {
/* 199 */       values.put("disk.write.bytes.multiple", new Long(this.write_dispatcher.getTotalAggregatedBytes()));
/*     */     }
/*     */     
/* 202 */     if (types.contains("disk.write.io.time"))
/*     */     {
/* 204 */       values.put("disk.write.io.time", new Long(this.write_dispatcher.getIOTime()));
/*     */     }
/*     */     
/* 207 */     if (types.contains("disk.write.io.count"))
/*     */     {
/* 209 */       values.put("disk.write.io.count", new Long(this.write_dispatcher.getIOCount()));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskAccessRequest queueReadRequest(CacheFile file, long offset, DirectByteBuffer buffer, short cache_policy, DiskAccessRequestListener listener)
/*     */   {
/* 222 */     DiskAccessRequestImpl request = new DiskAccessRequestImpl(file, offset, buffer, listener, (short)1, cache_policy);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 231 */     this.read_dispatcher.queueRequest(request);
/*     */     
/* 233 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskAccessRequest queueWriteRequest(CacheFile file, long offset, DirectByteBuffer buffer, boolean free_buffer, DiskAccessRequestListener listener)
/*     */   {
/* 246 */     DiskAccessRequestImpl request = new DiskAccessRequestImpl(file, offset, buffer, listener, (short)2, free_buffer ? 3 : (short)0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 255 */     this.write_dispatcher.queueRequest(request);
/*     */     
/* 257 */     return request;
/*     */   }
/*     */   
/*     */ 
/*     */   public DiskAccessControllerStats getStats()
/*     */   {
/* 263 */     new DiskAccessControllerStats()
/*     */     {
/*     */ 
/* 266 */       final long read_total_req = DiskAccessControllerImpl.this.read_dispatcher.getTotalRequests();
/* 267 */       final long read_total_bytes = DiskAccessControllerImpl.this.read_dispatcher.getTotalBytes();
/*     */       
/*     */ 
/*     */       public long getTotalReadRequests()
/*     */       {
/* 272 */         return this.read_total_req;
/*     */       }
/*     */       
/*     */ 
/*     */       public long getTotalReadBytes()
/*     */       {
/* 278 */         return this.read_total_bytes;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 286 */     return "read: " + this.read_dispatcher.getString() + ", write: " + this.write_dispatcher.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/access/impl/DiskAccessControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */