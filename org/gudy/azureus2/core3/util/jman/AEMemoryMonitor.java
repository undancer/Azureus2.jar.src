/*     */ package org.gudy.azureus2.core3.util.jman;
/*     */ 
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.MemoryMXBean;
/*     */ import java.lang.management.MemoryPoolMXBean;
/*     */ import java.lang.management.MemoryType;
/*     */ import java.lang.management.MemoryUsage;
/*     */ import java.util.List;
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationEmitter;
/*     */ import javax.management.NotificationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEJavaManagement;
/*     */ import org.gudy.azureus2.core3.util.AEJavaManagement.MemoryStuff;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
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
/*     */ public class AEMemoryMonitor
/*     */   implements AEJavaManagement.MemoryStuff
/*     */ {
/*  46 */   private static AEMemoryMonitor singleton = new AEMemoryMonitor();
/*     */   
/*     */   private static final long MB = 1048576L;
/*     */   
/*     */   private static long max_heap_mb;
/*     */   
/*     */   public AEMemoryMonitor()
/*     */   {
/*     */     try
/*     */     {
/*  56 */       List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
/*     */       
/*  58 */       MemoryPoolMXBean pool_to_monitor = null;
/*  59 */       long ptm_size = 0L;
/*     */       
/*  61 */       long overall_max = 0L;
/*     */       
/*  63 */       for (MemoryPoolMXBean pool : pools)
/*     */       {
/*  65 */         long pool_max = pool.getUsage().getMax();
/*     */         
/*  67 */         if (pool_max > 0L)
/*     */         {
/*  69 */           if (pool.getType() == MemoryType.HEAP)
/*     */           {
/*  71 */             overall_max += pool_max;
/*     */           }
/*     */         }
/*     */         
/*  75 */         if ((pool.getType() == MemoryType.HEAP) && (pool.isCollectionUsageThresholdSupported()))
/*     */         {
/*  77 */           long max = pool.getUsage().getMax();
/*     */           
/*  79 */           if (max > ptm_size)
/*     */           {
/*  81 */             pool_to_monitor = pool;
/*  82 */             ptm_size = max;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  87 */       max_heap_mb = (overall_max + 1048576L - 1L) / 1048576L;
/*     */       
/*  89 */       if (pool_to_monitor != null)
/*     */       {
/*  91 */         long max = pool_to_monitor.getUsage().getMax();
/*     */         
/*  93 */         long threshold = max * 3L / 4L;
/*     */         
/*  95 */         threshold = Math.min(threshold, 5242880L);
/*     */         
/*  97 */         MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
/*     */         
/*  99 */         NotificationEmitter emitter = (NotificationEmitter)mbean;
/*     */         
/*     */ 
/* 102 */         emitter.addNotificationListener(new NotificationListener()
/*     */         {
/*     */ 
/* 105 */           private long last_mb_log = Long.MAX_VALUE;
/*     */           
/*     */ 
/*     */           private boolean increase_tried;
/*     */           
/*     */ 
/*     */ 
/*     */           public void handleNotification(Notification notification, Object handback)
/*     */           {
/* 114 */             MemoryPoolMXBean pool = (MemoryPoolMXBean)handback;
/*     */             
/* 116 */             long used = pool.getCollectionUsage().getUsed();
/* 117 */             long max = pool.getUsage().getMax();
/*     */             
/* 119 */             long avail = max - used;
/*     */             
/* 121 */             if (avail < 0L) {
/* 122 */               avail = 0L;
/*     */             }
/*     */             
/* 125 */             long mb = (avail + 1048576L - 1L) / 1048576L;
/*     */             
/* 127 */             if (mb <= 4L)
/*     */             {
/* 129 */               synchronized (this)
/*     */               {
/* 131 */                 if (mb >= this.last_mb_log)
/*     */                 {
/* 133 */                   return;
/*     */                 }
/*     */                 
/* 136 */                 this.last_mb_log = mb;
/*     */               }
/*     */               
/* 139 */               Runtime runtime = Runtime.getRuntime();
/*     */               
/* 141 */               Debug.out("MemMon: notify triggered: pool=" + pool.getName() + ", used=" + used + ", max=" + max + ": runtime free=" + runtime.freeMemory() + ", tot=" + runtime.totalMemory() + ", max=" + runtime.maxMemory());
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 146 */               Logger.logTextResource(new LogAlert(true, 1, "memmon.low.warning"), new String[] { (mb == 0L ? "< " : "") + DisplayFormatters.formatByteCountToKiBEtc(Math.max(1L, mb) * 1048576L, true), DisplayFormatters.formatByteCountToKiBEtc(AEMemoryMonitor.max_heap_mb * 1048576L, true) });
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 155 */               if ((mb == 1L) && (!this.increase_tried))
/*     */               {
/* 157 */                 this.increase_tried = true;
/*     */                 
/* 159 */                 if (COConfigurationManager.getBooleanParameter("jvm.heap.auto.increase.enable", true))
/*     */                 {
/* 161 */                   PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */                   
/* 163 */                   if (platform.hasCapability(PlatformManagerCapabilities.AccessExplicitVMOptions))
/*     */                     try
/*     */                     {
/* 166 */                       String[] options = platform.getExplicitVMOptions();
/*     */                       
/* 168 */                       long max_mem = AEJavaManagement.getJVMLongOption(options, "-Xmx");
/*     */                       
/* 170 */                       if (max_mem <= 0L)
/*     */                       {
/* 172 */                         max_mem = AEMemoryMonitor.this.getMaxHeapMB() * 1048576L;
/*     */                       }
/*     */                       
/* 175 */                       long HEAP_AUTO_INCREASE_MAX = (Constants.is64Bit ? 'ࠀ' : '̀') * 1048576L;
/* 176 */                       long HEAP_AUTO_INCREASE_BY = 67108864L;
/*     */                       
/* 178 */                       if ((max_mem > 0L) && (max_mem < HEAP_AUTO_INCREASE_MAX))
/*     */                       {
/* 180 */                         max_mem += 67108864L;
/*     */                         
/* 182 */                         if (max_mem > HEAP_AUTO_INCREASE_MAX)
/*     */                         {
/* 184 */                           max_mem = HEAP_AUTO_INCREASE_MAX;
/*     */                         }
/*     */                         
/* 187 */                         long last_increase = COConfigurationManager.getLongParameter("jvm.heap.auto.increase.last", 0L);
/*     */                         
/* 189 */                         if (max_mem > last_increase)
/*     */                         {
/* 191 */                           COConfigurationManager.setParameter("jvm.heap.auto.increase.last", max_mem);
/*     */                           
/* 193 */                           options = AEJavaManagement.setJVMLongOption(options, "-Xmx", max_mem);
/*     */                           
/* 195 */                           platform.setExplicitVMOptions(options);
/*     */                           
/* 197 */                           Logger.logTextResource(new LogAlert(true, 1, "memmon.heap.auto.increase.warning"), new String[] { DisplayFormatters.formatByteCountToKiBEtc(max_mem, true) });
/*     */ 
/*     */                         }
/*     */                         
/*     */ 
/*     */                       }
/*     */                       
/*     */ 
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/*     */ 
/* 209 */                       Debug.out(e); } } } } } }, null, pool_to_monitor);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 219 */         pool_to_monitor.setCollectionUsageThreshold(threshold);
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 224 */       Debug.out(e);
/*     */     }
/*     */     
/* 227 */     if (max_heap_mb == 0L)
/*     */     {
/* 229 */       max_heap_mb = (Runtime.getRuntime().maxMemory() + 1048576L - 1L) / 1048576L;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long getMaxHeapMB()
/*     */   {
/* 236 */     return max_heap_mb;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/jman/AEMemoryMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */