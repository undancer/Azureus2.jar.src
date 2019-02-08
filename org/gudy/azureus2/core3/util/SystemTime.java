/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class SystemTime
/*     */ {
/*     */   public static final long TIME_GRANULARITY_MILLIS = 25L;
/*     */   private static final int STEPS_PER_SECOND = 40;
/*     */   private static SystemTimeProvider instance;
/*     */   private static final boolean SOD_IT_LETS_USE_HPC = false;
/*  39 */   private static volatile List<TickConsumer> systemTimeConsumers = new ArrayList();
/*  40 */   private static volatile List<TickConsumer> monotoneTimeConsumers = new ArrayList();
/*  41 */   private static volatile List<ChangeListener> clock_change_list = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  50 */       if (System.getProperty("azureus.time.use.raw.provider", "0").equals("1"))
/*     */       {
/*  52 */         System.out.println("Warning: Using Raw Provider");
/*  53 */         instance = new RawProvider(null);
/*     */       }
/*     */       else {
/*  56 */         instance = new SteppedProvider(null);
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  61 */       instance = new SteppedProvider(null);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void useRawProvider() {
/*  66 */     if (!(instance instanceof RawProvider))
/*     */     {
/*  68 */       Debug.out("Whoa, someone already created a non-raw provider!");
/*     */       
/*  70 */       instance = new RawProvider(null);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface TickConsumer { public abstract void consume(long paramLong);
/*     */   }
/*     */   
/*     */   protected static abstract interface SystemTimeProvider { public abstract long getTime();
/*     */     
/*     */     public abstract long getMonoTime();
/*     */     
/*     */     public abstract long getSteppedMonoTime();
/*     */   }
/*     */   
/*  84 */   private static class SteppedProvider implements SystemTime.SystemTimeProvider { private static final long HPC_START = SystemTime.getHighPrecisionCounter() / 1000000L;
/*     */     
/*     */     private final Thread updater;
/*     */     private volatile long stepped_time;
/*  88 */     private volatile long currentTimeOffset = System.currentTimeMillis();
/*  89 */     private final AtomicLong last_approximate_time = new AtomicLong();
/*     */     
/*     */     private volatile int access_count;
/*     */     
/*     */     private volatile int slice_access_count;
/*     */     
/*     */     private volatile int access_average_per_slice;
/*     */     
/*     */     private volatile int drift_adjusted_granularity;
/*     */     private volatile long stepped_mono_time;
/*     */     
/*     */     private SteppedProvider()
/*     */     {
/* 102 */       this.stepped_time = 0L;
/*     */       
/* 104 */       this.updater = new Thread("SystemTime")
/*     */       {
/*     */         public void run() {
/* 107 */           long adjustedTimeOffset = SystemTime.SteppedProvider.this.currentTimeOffset;
/*     */           
/* 109 */           Average access_average = Average.getInstance(1000, 10);
/* 110 */           Average drift_average = Average.getInstance(1000, 10);
/* 111 */           long lastOffset = adjustedTimeOffset;
/* 112 */           long lastSecond = -1000L;
/* 113 */           int tick_count = 0;
/*     */           for (;;)
/*     */           {
/* 116 */             long rawTime = System.currentTimeMillis();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */             long newMonotoneTime = rawTime - adjustedTimeOffset;
/* 123 */             long delta = newMonotoneTime - SystemTime.SteppedProvider.this.stepped_time;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 130 */             if ((delta < 0L) || (delta > 1000L))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */               SystemTime.SteppedProvider.access$314(SystemTime.SteppedProvider.this, 25L);
/* 138 */               adjustedTimeOffset = rawTime - SystemTime.SteppedProvider.this.stepped_time;
/*     */             }
/*     */             else {
/* 141 */               SystemTime.SteppedProvider.this.stepped_time = newMonotoneTime;
/*     */             }
/* 143 */             tick_count++;
/*     */             
/*     */             long change;
/*     */             
/* 147 */             if (tick_count == 40)
/*     */             {
/* 149 */               long change = adjustedTimeOffset - lastOffset;
/*     */               
/* 151 */               if (change != 0L)
/*     */               {
/* 153 */                 Iterator<SystemTime.ChangeListener> it = SystemTime.clock_change_list.iterator();
/*     */                 
/* 155 */                 while (it.hasNext()) {
/*     */                   try
/*     */                   {
/* 158 */                     ((SystemTime.ChangeListener)it.next()).clockChangeDetected(rawTime, change);
/*     */                   }
/*     */                   catch (Throwable e)
/*     */                   {
/* 162 */                     Debug.out(e);
/*     */                   }
/*     */                 }
/* 165 */                 lastOffset = adjustedTimeOffset;
/*     */                 
/* 167 */                 SystemTime.SteppedProvider.this.currentTimeOffset = adjustedTimeOffset;
/*     */               }
/*     */               
/* 170 */               long drift = SystemTime.SteppedProvider.this.stepped_time - lastSecond - 1000L;
/* 171 */               lastSecond = SystemTime.SteppedProvider.this.stepped_time;
/* 172 */               drift_average.addValue(drift);
/* 173 */               SystemTime.SteppedProvider.this.drift_adjusted_granularity = ((int)(25L + drift_average.getAverage() / 40L));
/* 174 */               access_average.addValue(SystemTime.SteppedProvider.this.access_count);
/* 175 */               SystemTime.SteppedProvider.this.access_average_per_slice = ((int)(access_average.getAverage() / 40L));
/*     */               
/* 177 */               SystemTime.SteppedProvider.this.access_count = 0;
/* 178 */               tick_count = 0;
/*     */             } else {
/* 180 */               change = 0L;
/*     */             }
/*     */             
/* 183 */             SystemTime.SteppedProvider.this.slice_access_count = 0;
/*     */             
/* 185 */             SystemTime.SteppedProvider.this.stepped_mono_time = SystemTime.SteppedProvider.this.stepped_time;
/*     */             
/* 187 */             long adjustedTime = SystemTime.SteppedProvider.this.stepped_time + SystemTime.SteppedProvider.this.currentTimeOffset;
/*     */             
/* 189 */             if (change != 0L) {
/* 190 */               Iterator<SystemTime.ChangeListener> it = SystemTime.clock_change_list.iterator();
/*     */               
/* 192 */               while (it.hasNext()) {
/*     */                 try
/*     */                 {
/* 195 */                   ((SystemTime.ChangeListener)it.next()).clockChangeCompleted(adjustedTime, change);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 199 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 205 */             List<SystemTime.TickConsumer> consumersRef = SystemTime.monotoneTimeConsumers;
/* 206 */             for (int i = 0; i < consumersRef.size(); i++)
/*     */             {
/* 208 */               SystemTime.TickConsumer cons = (SystemTime.TickConsumer)consumersRef.get(i);
/*     */               try
/*     */               {
/* 211 */                 cons.consume(SystemTime.SteppedProvider.this.stepped_time);
/*     */               }
/*     */               catch (Throwable e) {
/* 214 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 222 */             consumersRef = SystemTime.systemTimeConsumers;
/*     */             
/* 224 */             for (int i = 0; i < consumersRef.size(); i++)
/*     */             {
/* 226 */               SystemTime.TickConsumer cons = (SystemTime.TickConsumer)consumersRef.get(i);
/*     */               try
/*     */               {
/* 229 */                 cons.consume(adjustedTime);
/*     */               }
/*     */               catch (Throwable e) {
/* 232 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */             
/*     */             try
/*     */             {
/* 238 */               Thread.sleep(25L);
/*     */             }
/*     */             catch (Exception e) {
/* 241 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/* 245 */       };
/* 246 */       this.updater.setDaemon(true);
/*     */       
/* 248 */       this.updater.setPriority(10);
/* 249 */       this.updater.start();
/*     */     }
/*     */     
/*     */     public long getTime() {
/* 253 */       return getMonoTime() + this.currentTimeOffset;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getMonoTime()
/*     */     {
/* 263 */       long averageSliceStep = this.access_average_per_slice;
/* 264 */       long adjusted_time; long adjusted_time; if (averageSliceStep > 0L)
/*     */       {
/* 266 */         long sliceStep = this.drift_adjusted_granularity * this.slice_access_count / averageSliceStep;
/* 267 */         if (sliceStep >= this.drift_adjusted_granularity)
/*     */         {
/* 269 */           sliceStep = this.drift_adjusted_granularity - 1;
/*     */         }
/* 271 */         adjusted_time = sliceStep + this.stepped_time;
/*     */       } else {
/* 273 */         adjusted_time = this.stepped_time; }
/* 274 */       this.access_count += 1;
/* 275 */       this.slice_access_count += 1;
/*     */       
/*     */ 
/* 278 */       long approxBuffered = this.last_approximate_time.get();
/* 279 */       if (adjusted_time < approxBuffered) {
/* 280 */         adjusted_time = approxBuffered;
/*     */       } else {
/* 282 */         this.last_approximate_time.compareAndSet(approxBuffered, adjusted_time);
/*     */       }
/* 284 */       return adjusted_time;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getSteppedMonoTime()
/*     */     {
/* 296 */       return this.stepped_mono_time;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class RawProvider
/*     */     implements SystemTime.SystemTimeProvider
/*     */   {
/*     */     private final Thread updater;
/*     */     
/*     */     private RawProvider()
/*     */     {
/* 307 */       System.out.println("SystemTime: using raw time provider");
/*     */       
/* 309 */       this.updater = new Thread("SystemTime")
/*     */       {
/*     */         long last_time;
/*     */         
/*     */         public void run()
/*     */         {
/*     */           for (;;) {
/* 316 */             long current_time = SystemTime.RawProvider.this.getTime();
/*     */             long change;
/*     */             long change;
/* 319 */             if (this.last_time != 0L)
/*     */             {
/* 321 */               long offset = current_time - this.last_time;
/*     */               
/* 323 */               if ((offset < 0L) || (offset > 5000L))
/*     */               {
/* 325 */                 long change = offset;
/*     */                 
/*     */ 
/*     */ 
/* 329 */                 Iterator<SystemTime.ChangeListener> it = SystemTime.clock_change_list.iterator();
/*     */                 
/* 331 */                 while (it.hasNext()) {
/*     */                   try
/*     */                   {
/* 334 */                     ((SystemTime.ChangeListener)it.next()).clockChangeDetected(current_time, change);
/*     */                   }
/*     */                   catch (Throwable e) {
/* 337 */                     Debug.out(e);
/*     */                   }
/*     */                 }
/*     */               } else {
/* 341 */                 change = 0L;
/*     */               }
/*     */             } else {
/* 344 */               change = 0L;
/*     */             }
/*     */             
/* 347 */             this.last_time = current_time;
/*     */             
/* 349 */             if (change != 0L) {
/* 350 */               Iterator<SystemTime.ChangeListener> it = SystemTime.clock_change_list.iterator();
/* 351 */               while (it.hasNext()) {
/*     */                 try
/*     */                 {
/* 354 */                   ((SystemTime.ChangeListener)it.next()).clockChangeCompleted(current_time, change);
/*     */                 }
/*     */                 catch (Throwable e)
/*     */                 {
/* 358 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/* 363 */             List consumer_list_ref = SystemTime.systemTimeConsumers;
/*     */             
/* 365 */             for (int i = 0; i < consumer_list_ref.size(); i++)
/*     */             {
/* 367 */               SystemTime.TickConsumer cons = (SystemTime.TickConsumer)consumer_list_ref.get(i);
/*     */               try
/*     */               {
/* 370 */                 cons.consume(current_time);
/*     */               }
/*     */               catch (Throwable e) {
/* 373 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/* 376 */             consumer_list_ref = SystemTime.monotoneTimeConsumers;
/*     */             
/* 378 */             long mono_time = SystemTime.RawProvider.this.getMonoTime();
/*     */             
/* 380 */             for (int i = 0; i < consumer_list_ref.size(); i++)
/*     */             {
/* 382 */               SystemTime.TickConsumer cons = (SystemTime.TickConsumer)consumer_list_ref.get(i);
/*     */               try
/*     */               {
/* 385 */                 cons.consume(mono_time);
/*     */               }
/*     */               catch (Throwable e) {
/* 388 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */             
/*     */             try
/*     */             {
/* 394 */               Thread.sleep(25L);
/*     */             }
/*     */             catch (Exception e) {
/* 397 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/* 401 */       };
/* 402 */       this.updater.setDaemon(true);
/*     */       
/* 404 */       this.updater.setPriority(10);
/* 405 */       this.updater.start();
/*     */     }
/*     */     
/*     */     public long getTime() {
/* 409 */       return System.currentTimeMillis();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long getMonoTime()
/*     */     {
/* 418 */       return SystemTime.getHighPrecisionCounter() / 1000000L;
/*     */     }
/*     */     
/*     */     public long getSteppedMonoTime() {
/* 422 */       return getMonoTime();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getCurrentTime()
/*     */   {
/* 433 */     return instance.getTime();
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
/*     */   public static long getMonotonousTime()
/*     */   {
/* 447 */     return instance.getMonoTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getSteppedMonotonousTime()
/*     */   {
/* 457 */     return instance.getSteppedMonoTime();
/*     */   }
/*     */   
/*     */   public static long getOffsetTime(long offsetMS)
/*     */   {
/* 462 */     return instance.getTime() + offsetMS;
/*     */   }
/*     */   
/*     */   public static void registerConsumer(TickConsumer c) {
/* 466 */     synchronized (instance)
/*     */     {
/* 468 */       List new_list = new ArrayList(systemTimeConsumers);
/* 469 */       new_list.add(c);
/* 470 */       systemTimeConsumers = new_list;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void unregisterConsumer(TickConsumer c) {
/* 475 */     synchronized (instance)
/*     */     {
/* 477 */       List new_list = new ArrayList(systemTimeConsumers);
/* 478 */       new_list.remove(c);
/* 479 */       systemTimeConsumers = new_list;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void registerMonotonousConsumer(TickConsumer c) {
/* 484 */     synchronized (instance)
/*     */     {
/* 486 */       List new_list = new ArrayList(monotoneTimeConsumers);
/* 487 */       new_list.add(c);
/* 488 */       monotoneTimeConsumers = new_list;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void unregisterMonotonousConsumer(TickConsumer c) {
/* 493 */     synchronized (instance)
/*     */     {
/* 495 */       List new_list = new ArrayList(monotoneTimeConsumers);
/* 496 */       new_list.remove(c);
/* 497 */       monotoneTimeConsumers = new_list;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void registerClockChangeListener(ChangeListener c) {
/* 502 */     synchronized (instance)
/*     */     {
/* 504 */       List new_list = new ArrayList(clock_change_list);
/* 505 */       new_list.add(c);
/* 506 */       clock_change_list = new_list;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void unregisterClockChangeListener(ChangeListener c) {
/* 511 */     synchronized (instance)
/*     */     {
/* 513 */       List new_list = new ArrayList(clock_change_list);
/* 514 */       new_list.remove(c);
/* 515 */       clock_change_list = new_list;
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
/*     */   public static long getHighPrecisionCounter()
/*     */   {
/* 541 */     return System.nanoTime();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 545 */     for (int i = 0; i < 1; i++)
/*     */     {
/*     */ 
/* 548 */       new Thread()
/*     */       {
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
/*     */         public void run()
/*     */         {
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
/* 583 */           long cstart = SystemTime.getCurrentTime();
/* 584 */           long mstart = SystemTime.getMonotonousTime();
/* 585 */           System.out.println("alter system clock to see differences between monotonous and current time");
/* 586 */           long cLastRound = cstart;
/* 587 */           long mLastRound = mstart;
/*     */           for (;;)
/*     */           {
/* 590 */             long mnow = SystemTime.getMonotonousTime();
/* 591 */             long cnow = SystemTime.getCurrentTime();
/*     */             
/* 593 */             System.out.println("current: " + (cnow - cstart) + " monotonous:" + (mnow - mstart) + " delta current:" + (cnow - cLastRound) + " delta monotonous:" + (mnow - mLastRound));
/* 594 */             cLastRound = cnow;
/* 595 */             mLastRound = mnow;
/*     */             try
/*     */             {
/* 598 */               Thread.sleep(15L);
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */         }
/*     */       }.start();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface ChangeListener
/*     */   {
/*     */     public abstract void clockChangeDetected(long paramLong1, long paramLong2);
/*     */     
/*     */     public abstract void clockChangeCompleted(long paramLong1, long paramLong2);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/SystemTime.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */