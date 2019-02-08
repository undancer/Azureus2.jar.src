/*     */ package org.gudy.azureus2.core3.util.jman;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.average.AverageFactory;
/*     */ import com.aelitis.azureus.core.util.average.MovingAverage;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.management.ManagementFactory;
/*     */ import java.lang.management.ThreadInfo;
/*     */ import java.lang.management.ThreadMXBean;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.AEJavaManagement.ThreadStuff;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
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
/*     */ public class AEThreadMonitor
/*     */   implements AEJavaManagement.ThreadStuff
/*     */ {
/*  40 */   private boolean disable_getThreadCpuTime = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ThreadMXBean thread_bean;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getThreadCPUTime()
/*     */   {
/*  60 */     if (this.thread_bean == null)
/*     */     {
/*  62 */       return 0L;
/*     */     }
/*     */     
/*  65 */     return this.thread_bean.getCurrentThreadCpuTime();
/*     */   }
/*     */   
/*     */   public AEThreadMonitor()
/*     */   {
/*  46 */     ThreadMXBean threadMXBean = null;
/*     */     try {
/*  48 */       threadMXBean = ManagementFactory.getThreadMXBean();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  52 */       e.printStackTrace();
/*     */     }
/*  54 */     this.thread_bean = threadMXBean;
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
/*  71 */     String java_version = System.getProperty("java.runtime.version");
/*     */     
/*     */ 
/*  74 */     this.disable_getThreadCpuTime = ((Constants.isOSX) && (java_version.startsWith("1.5.0_06")));
/*     */     
/*     */ 
/*  77 */     AEDiagnostics.addEvidenceGenerator(new EvidenceGenerateor(null));
/*     */     
/*  79 */     if (!this.disable_getThreadCpuTime)
/*     */     {
/*  81 */       AEThread thread = new AEThread("AEThreadMonitor")
/*     */       {
/*     */         public void runSupport()
/*     */         {
/*     */           try
/*     */           {
/*     */             try
/*     */             {
/*  89 */               Class.forName("java.lang.management.ManagementFactory");
/*     */               
/*  91 */               AEThreadMonitor.access$100();
/*     */ 
/*     */ 
/*     */             }
/*     */             catch (Throwable e) {}
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         
/*     */ 
/* 103 */       };
/* 104 */       thread.setPriority(10);
/*     */       
/* 106 */       thread.setDaemon(true);
/*     */       
/* 108 */       thread.start();
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
/*     */   private static void monitor15()
/*     */   {
/* 134 */     AEDiagnosticsLogger log = AEDiagnostics.getLogger("thread");
/*     */     
/* 136 */     int num_processors = Runtime.getRuntime().availableProcessors();
/*     */     
/* 138 */     if (num_processors < 1)
/*     */     {
/* 140 */       num_processors = 1;
/*     */     }
/*     */     
/* 143 */     ThreadMXBean bean = ManagementFactory.getThreadMXBean();
/*     */     
/* 145 */     log.log("Monitoring starts (processors =" + num_processors + ")");
/*     */     
/* 147 */     if (!bean.isThreadCpuTimeSupported())
/*     */     {
/* 149 */       log.log("ThreadCpuTime not supported");
/*     */       
/* 151 */       return;
/*     */     }
/*     */     
/* 154 */     if (!bean.isThreadCpuTimeEnabled())
/*     */     {
/* 156 */       log.log("Enabling ThreadCpuTime");
/*     */       
/* 158 */       bean.setThreadCpuTimeEnabled(true);
/*     */     }
/*     */     
/* 161 */     Map<Long, Long> last_times = new HashMap();
/*     */     
/* 163 */     int time_available = 10000;
/*     */     
/* 165 */     long start_mono = SystemTime.getMonotonousTime();
/*     */     
/* 167 */     MovingAverage high_usage_history = AverageFactory.MovingAverage(12);
/* 168 */     boolean huh_mon_active = false;
/*     */     
/*     */     for (;;)
/*     */     {
/* 172 */       long start = System.currentTimeMillis();
/*     */       
/*     */       try
/*     */       {
/* 176 */         Thread.sleep(10000L);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 180 */         log.log(e);
/*     */       }
/*     */       
/* 183 */       long end = System.currentTimeMillis();
/*     */       
/* 185 */       long elapsed = end - start;
/*     */       
/* 187 */       long[] ids = bean.getAllThreadIds();
/*     */       
/* 189 */       long[] diffs = new long[ids.length];
/*     */       
/* 191 */       long total_diffs = 0L;
/* 192 */       long biggest_diff = 0L;
/* 193 */       int biggest_index = 0;
/*     */       
/* 195 */       Map<Long, Long> new_times = new HashMap();
/*     */       
/* 197 */       for (int i = 0; i < ids.length; i++)
/*     */       {
/* 199 */         long id = ids[i];
/*     */         
/* 201 */         long time = bean.getThreadCpuTime(id) / 1000000L;
/*     */         
/* 203 */         Long old_time = (Long)last_times.get(Long.valueOf(id));
/*     */         
/* 205 */         if (old_time != null)
/*     */         {
/* 207 */           long diff = time - old_time.longValue();
/*     */           
/* 209 */           if (diff > biggest_diff)
/*     */           {
/* 211 */             biggest_diff = diff;
/*     */             
/* 213 */             biggest_index = i;
/*     */           }
/*     */           
/* 216 */           diffs[i] = diff;
/*     */           
/* 218 */           total_diffs += diff;
/*     */         }
/*     */         
/* 221 */         new_times.put(Long.valueOf(id), Long.valueOf(time));
/*     */       }
/*     */       
/* 224 */       ThreadInfo info = bean.getThreadInfo(ids[biggest_index]);
/*     */       
/* 226 */       String thread_name = info == null ? "<dead>" : info.getThreadName();
/*     */       
/* 228 */       int percent = (int)(100L * biggest_diff / 10000L);
/*     */       
/* 230 */       Runtime rt = Runtime.getRuntime();
/*     */       
/* 232 */       log.log("Thread state: elapsed=" + elapsed + ",cpu=" + total_diffs + ",max=" + thread_name + "(" + biggest_diff + "/" + percent + "%),mem:max=" + rt.maxMemory() / 1024L + ",tot=" + rt.totalMemory() / 1024L + ",free=" + rt.freeMemory() / 1024L);
/*     */       
/* 234 */       if (huh_mon_active)
/*     */       {
/*     */ 
/*     */ 
/* 238 */         boolean interesting = (percent > 5) && (thread_name.equals("PRUDPPacketHandler:sender"));
/*     */         
/* 240 */         double temp = high_usage_history.update(interesting ? 1.0D : 0.0D);
/*     */         
/* 242 */         if (temp >= 0.5D)
/*     */         {
/* 244 */           Logger.log(new LogAlert(false, 1, "High CPU usage detected in networking code - see <a href=\"http://wiki.vuze.com/w/High_CPU_Usage\">The Wiki</a> for possible solutions"));
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 254 */         huh_mon_active = SystemTime.getMonotonousTime() - start_mono > 120000L;
/*     */       }
/*     */       
/* 257 */       if (biggest_diff > 2500L)
/*     */       {
/* 259 */         info = bean.getThreadInfo(ids[biggest_index], 255);
/*     */         
/* 261 */         if (info == null)
/*     */         {
/* 263 */           log.log("    no info for max thread");
/*     */         }
/*     */         else
/*     */         {
/* 267 */           StackTraceElement[] elts = info.getStackTrace();
/* 268 */           StringBuilder str = new StringBuilder(elts.length * 20);
/*     */           
/* 270 */           str.append("    ");
/* 271 */           for (int i = 0; i < elts.length; i++) {
/* 272 */             if (i != 0)
/* 273 */               str.append(", ");
/* 274 */             str.append(elts[i]);
/*     */           }
/*     */           
/* 277 */           log.log(str.toString());
/*     */         }
/*     */       }
/*     */       
/* 281 */       last_times = new_times;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void dumpThreads()
/*     */   {
/* 288 */     StringWriter sw = new StringWriter();
/*     */     
/* 290 */     IndentWriter iw = new IndentWriter(new PrintWriter(sw));
/*     */     
/* 292 */     dumpThreads(iw);
/*     */     
/* 294 */     iw.close();
/*     */     
/* 296 */     Debug.out(sw.toString());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void dumpThreads(IndentWriter writer)
/*     */   {
/* 303 */     final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
/*     */     
/* 305 */     long[] allThreadIds = threadBean.getAllThreadIds();
/* 306 */     writer.println("Threads " + allThreadIds.length);
/* 307 */     writer.indent();
/*     */     
/* 309 */     List<ThreadInfo> threadInfos = new ArrayList(allThreadIds.length);
/* 310 */     for (int i = 0; i < allThreadIds.length; i++) {
/* 311 */       ThreadInfo info = threadBean.getThreadInfo(allThreadIds[i], 32);
/* 312 */       if (info != null) {
/* 313 */         threadInfos.add(info);
/*     */       }
/*     */     }
/* 316 */     if (!this.disable_getThreadCpuTime) {
/* 317 */       Collections.sort(threadInfos, new Comparator()
/*     */       {
/*     */         public int compare(ThreadInfo o1, ThreadInfo o2) {
/* 320 */           long diff = threadBean.getThreadCpuTime(o2.getThreadId()) - threadBean.getThreadCpuTime(o1.getThreadId());
/*     */           
/* 322 */           if (diff == 0L) {
/* 323 */             return o1.getThreadName().compareToIgnoreCase(o2.getThreadName());
/*     */           }
/* 325 */           return diff > 0L ? 1 : -1;
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 330 */     for (int i = 0; i < threadInfos.size(); i++) {
/*     */       try {
/* 332 */         ThreadInfo threadInfo = (ThreadInfo)threadInfos.get(i);
/*     */         
/* 334 */         long lCpuTime = this.disable_getThreadCpuTime ? -1L : threadBean.getThreadCpuTime(threadInfo.getThreadId());
/*     */         
/* 336 */         if (lCpuTime == 0L) {
/*     */           break;
/*     */         }
/*     */         String sState;
/* 340 */         switch (threadInfo.getThreadState()) {
/*     */         case BLOCKED: 
/* 342 */           sState = "Blocked";
/* 343 */           break;
/*     */         case RUNNABLE: 
/* 345 */           sState = "Runnable";
/* 346 */           break;
/*     */         case NEW: 
/* 348 */           sState = "New";
/* 349 */           break;
/*     */         case TERMINATED: 
/* 351 */           sState = "Terminated";
/* 352 */           break;
/*     */         case TIMED_WAITING: 
/* 354 */           sState = "Timed Waiting";
/* 355 */           break;
/*     */         
/*     */         case WAITING: 
/* 358 */           sState = "Waiting";
/* 359 */           break;
/*     */         
/*     */         default: 
/* 362 */           sState = "" + threadInfo.getThreadState();
/*     */         }
/*     */         
/*     */         
/*     */ 
/* 367 */         String sName = threadInfo.getThreadName();
/* 368 */         String sLockName = threadInfo.getLockName();
/*     */         
/* 370 */         writer.println(sName + ": " + sState + ", " + lCpuTime / 1000000L + "ms CPU, " + "B/W: " + threadInfo.getBlockedCount() + "/" + threadInfo.getWaitedCount() + (sLockName == null ? "" : new StringBuilder().append("; Locked by ").append(sLockName).append("/").append(threadInfo.getLockOwnerName()).toString()));
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
/* 383 */         writer.indent();
/*     */         try {
/* 385 */           StackTraceElement[] stackTrace = threadInfo.getStackTrace();
/* 386 */           for (int j = 0; j < stackTrace.length; j++) {
/* 387 */             writer.println(stackTrace[j].toString());
/*     */           }
/*     */         } finally {
/* 390 */           writer.exdent();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 398 */     writer.exdent();
/*     */   }
/*     */   
/*     */   private class EvidenceGenerateor implements AEDiagnosticsEvidenceGenerator {
/*     */     private EvidenceGenerateor() {}
/*     */     
/*     */     public void generate(IndentWriter writer) {
/* 405 */       AEThreadMonitor.this.dumpThreads(writer);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/jman/AEThreadMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */