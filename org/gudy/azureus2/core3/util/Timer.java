/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ public class Timer
/*     */   extends AERunnable
/*     */   implements SystemTime.ChangeListener
/*     */ {
/*     */   private static final boolean DEBUG_TIMERS = true;
/*  38 */   private static ArrayList<WeakReference<Timer>> timers = null;
/*  39 */   static final AEMonitor timers_mon = new AEMonitor("timers list");
/*     */   
/*     */   private ThreadPool thread_pool;
/*     */   
/*  43 */   private Set<TimerEvent> events = new TreeSet();
/*     */   
/*  45 */   private long unique_id_next = 0L;
/*     */   
/*     */   private long current_when;
/*     */   
/*     */   private volatile boolean destroyed;
/*     */   
/*     */   private boolean indestructable;
/*     */   
/*     */   private boolean log;
/*     */   private int max_events_logged;
/*     */   
/*     */   public Timer(String name)
/*     */   {
/*  58 */     this(name, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Timer(String name, int thread_pool_size)
/*     */   {
/*  66 */     this(name, thread_pool_size, 5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Timer(String name, int thread_pool_size, int thread_priority)
/*     */   {
/*     */     try
/*     */     {
/*  77 */       timers_mon.enter();
/*  78 */       if (timers == null) {
/*  79 */         timers = new ArrayList();
/*  80 */         AEDiagnostics.addEvidenceGenerator(new evidenceGenerator(null));
/*     */       }
/*  82 */       timers.add(new WeakReference(this));
/*     */     } finally {
/*  84 */       timers_mon.exit();
/*     */     }
/*     */     
/*     */ 
/*  88 */     this.thread_pool = new ThreadPool(name, thread_pool_size);
/*     */     
/*  90 */     SystemTime.registerClockChangeListener(this);
/*     */     
/*  92 */     Thread t = new Thread(this, "Timer:" + name);
/*     */     
/*  94 */     t.setDaemon(true);
/*     */     
/*  96 */     t.setPriority(thread_priority);
/*     */     
/*  98 */     t.start();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIndestructable()
/*     */   {
/* 104 */     this.indestructable = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized List<TimerEvent> getEvents()
/*     */   {
/* 110 */     return new ArrayList(this.events);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLogging(boolean _log)
/*     */   {
/* 116 */     this.log = _log;
/*     */   }
/*     */   
/*     */   public boolean getLogging() {
/* 120 */     return this.log;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setWarnWhenFull()
/*     */   {
/* 126 */     this.thread_pool.setWarnWhenFull();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLogCPU()
/*     */   {
/* 132 */     this.thread_pool.setLogCPU();
/*     */   }
/*     */   
/*     */   public void runSupport()
/*     */   {
/*     */     try
/*     */     {
/*     */       for (;;)
/*     */       {
/* 141 */         TimerEvent event_to_run = null;
/*     */         
/* 143 */         synchronized (this)
/*     */         {
/* 145 */           if (this.destroyed) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 150 */           if (this.events.isEmpty())
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/* 155 */               this.current_when = 2147483647L;
/*     */               
/* 157 */               wait();
/*     */             }
/*     */             finally
/*     */             {
/* 161 */               this.current_when = 0L;
/*     */             }
/*     */           }
/*     */           else {
/* 165 */             long now = SystemTime.getCurrentTime();
/*     */             
/* 167 */             TimerEvent next_event = (TimerEvent)this.events.iterator().next();
/*     */             
/* 169 */             long when = next_event.getWhen();
/*     */             
/* 171 */             long delay = when - now;
/*     */             
/* 173 */             if (delay > 0L)
/*     */             {
/*     */               try
/*     */               {
/*     */ 
/* 178 */                 this.current_when = when;
/*     */                 
/* 180 */                 wait(delay);
/*     */               }
/*     */               finally
/*     */               {
/* 184 */                 this.current_when = 0L;
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 189 */           if (this.destroyed) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 194 */           if (this.events.isEmpty()) {
/*     */             continue;
/*     */           }
/*     */           
/*     */ 
/* 199 */           long now = SystemTime.getCurrentTime();
/*     */           
/* 201 */           Iterator<TimerEvent> it = this.events.iterator();
/*     */           
/* 203 */           TimerEvent next_event = (TimerEvent)it.next();
/*     */           
/* 205 */           long rem = next_event.getWhen() - now;
/*     */           
/* 207 */           if (rem <= 25L)
/*     */           {
/* 209 */             event_to_run = next_event;
/*     */             
/* 211 */             it.remove();
/*     */           }
/*     */         }
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
/* 224 */         if (event_to_run != null)
/*     */         {
/* 226 */           event_to_run.setHasRun();
/*     */           
/* 228 */           if (this.log) {
/* 229 */             System.out.println("running: " + event_to_run.getString());
/*     */           }
/*     */           
/* 232 */           this.thread_pool.run(event_to_run.getRunnable());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 237 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clockChangeDetected(long current_time, long offset)
/*     */   {
/* 247 */     if (Math.abs(offset) >= 60000L)
/*     */     {
/*     */ 
/*     */ 
/* 251 */       synchronized (this)
/*     */       {
/* 253 */         Iterator<TimerEvent> it = this.events.iterator();
/*     */         
/* 255 */         List<TimerEvent> updated_events = new ArrayList(this.events.size());
/*     */         
/* 257 */         while (it.hasNext())
/*     */         {
/* 259 */           TimerEvent event = (TimerEvent)it.next();
/*     */           
/*     */ 
/*     */ 
/* 263 */           if (!event.isAbsolute())
/*     */           {
/* 265 */             long old_when = event.getWhen();
/* 266 */             long new_when = old_when + offset;
/*     */             
/* 268 */             TimerEventPerformer performer = event.getPerformer();
/*     */             
/*     */ 
/*     */ 
/* 272 */             if ((performer instanceof TimerEventPeriodic))
/*     */             {
/* 274 */               TimerEventPeriodic periodic_event = (TimerEventPeriodic)performer;
/*     */               
/* 276 */               long freq = periodic_event.getFrequency();
/*     */               
/* 278 */               if (new_when > current_time + freq + 5000L)
/*     */               {
/* 280 */                 long adjusted_when = current_time + freq;
/*     */                 
/*     */ 
/*     */ 
/* 284 */                 new_when = adjusted_when;
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 290 */             if ((old_when <= 0L) || (new_when >= 0L) || (offset <= 0L))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 298 */               event.setWhen(new_when);
/*     */             }
/*     */           }
/*     */           
/* 302 */           updated_events.add(event);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 309 */         this.events = new TreeSet(updated_events);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clockChangeCompleted(long current_time, long offset)
/*     */   {
/* 319 */     if (Math.abs(offset) >= 60000L)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 326 */       synchronized (this)
/*     */       {
/* 328 */         Iterator<TimerEvent> it = this.events.iterator();
/*     */         
/* 330 */         boolean updated = false;
/*     */         
/* 332 */         while (it.hasNext())
/*     */         {
/* 334 */           TimerEvent event = (TimerEvent)it.next();
/*     */           
/*     */ 
/*     */ 
/* 338 */           if (!event.isAbsolute())
/*     */           {
/* 340 */             TimerEventPerformer performer = event.getPerformer();
/*     */             
/*     */ 
/*     */ 
/* 344 */             if ((performer instanceof TimerEventPeriodic))
/*     */             {
/* 346 */               TimerEventPeriodic periodic_event = (TimerEventPeriodic)performer;
/*     */               
/* 348 */               long freq = periodic_event.getFrequency();
/*     */               
/* 350 */               long old_when = event.getWhen();
/*     */               
/* 352 */               if (old_when > current_time + freq + 5000L)
/*     */               {
/* 354 */                 long adjusted_when = current_time + freq;
/*     */                 
/*     */ 
/*     */ 
/* 358 */                 event.setWhen(adjusted_when);
/*     */                 
/* 360 */                 updated = true;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 366 */         if (updated)
/*     */         {
/* 368 */           this.events = new TreeSet(new ArrayList(this.events));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 374 */         notify();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void adjustAllBy(long offset)
/*     */   {
/* 385 */     synchronized (this)
/*     */     {
/*     */ 
/*     */ 
/* 389 */       Iterator<TimerEvent> it = this.events.iterator();
/*     */       
/* 391 */       boolean resort = false;
/*     */       
/* 393 */       while (it.hasNext())
/*     */       {
/* 395 */         TimerEvent event = (TimerEvent)it.next();
/*     */         
/* 397 */         long old_when = event.getWhen();
/* 398 */         long new_when = old_when + offset;
/*     */         
/*     */ 
/*     */ 
/* 402 */         if ((old_when > 0L) && (new_when < 0L) && (offset > 0L))
/*     */         {
/*     */ 
/*     */ 
/* 406 */           resort = true;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 412 */           event.setWhen(new_when);
/*     */         }
/*     */       }
/*     */       
/* 416 */       if (resort)
/*     */       {
/* 418 */         this.events = new TreeSet(new ArrayList(this.events));
/*     */       }
/*     */       
/* 421 */       notify();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(long when, TimerEventPerformer performer)
/*     */   {
/* 430 */     return addEvent(SystemTime.getCurrentTime(), when, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(String name, long when, TimerEventPerformer performer)
/*     */   {
/* 439 */     return addEvent(name, SystemTime.getCurrentTime(), when, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(String name, long when, boolean absolute, TimerEventPerformer performer)
/*     */   {
/* 449 */     return addEvent(name, SystemTime.getCurrentTime(), when, absolute, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(long creation_time, long when, TimerEventPerformer performer)
/*     */   {
/* 458 */     return addEvent(null, creation_time, when, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(long creation_time, long when, boolean absolute, TimerEventPerformer performer)
/*     */   {
/* 468 */     return addEvent(null, creation_time, when, absolute, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(String name, long creation_time, long when, TimerEventPerformer performer)
/*     */   {
/* 478 */     return addEvent(name, creation_time, when, false, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEvent addEvent(String name, long creation_time, long when, boolean absolute, TimerEventPerformer performer)
/*     */   {
/* 489 */     TimerEvent event = new TimerEvent(this, this.unique_id_next++, creation_time, when, absolute, performer);
/*     */     
/* 491 */     if (name != null)
/*     */     {
/* 493 */       event.setName(name);
/*     */     }
/*     */     
/* 496 */     this.events.add(event);
/*     */     
/* 498 */     if (this.log)
/*     */     {
/* 500 */       if (this.events.size() > this.max_events_logged)
/*     */       {
/* 502 */         this.max_events_logged = this.events.size();
/*     */         
/* 504 */         System.out.println("Timer '" + this.thread_pool.getName() + "' - events = " + this.max_events_logged);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 510 */     if ((this.current_when == 2147483647L) || (when < this.current_when))
/*     */     {
/* 512 */       notify();
/*     */     }
/*     */     
/* 515 */     return event;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEventPeriodic addPeriodicEvent(long frequency, TimerEventPerformer performer)
/*     */   {
/* 523 */     return addPeriodicEvent(null, frequency, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEventPeriodic addPeriodicEvent(String name, long frequency, TimerEventPerformer performer)
/*     */   {
/* 532 */     return addPeriodicEvent(name, frequency, false, performer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized TimerEventPeriodic addPeriodicEvent(String name, long frequency, boolean absolute, TimerEventPerformer performer)
/*     */   {
/* 542 */     TimerEventPeriodic periodic_performer = new TimerEventPeriodic(this, frequency, absolute, performer);
/*     */     
/* 544 */     if (name != null)
/*     */     {
/* 546 */       periodic_performer.setName(name);
/*     */     }
/*     */     
/* 549 */     if (this.log)
/*     */     {
/* 551 */       System.out.println("Timer '" + this.thread_pool.getName() + "' - added " + periodic_performer.getString());
/*     */     }
/*     */     
/* 554 */     return periodic_performer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected synchronized void cancelEvent(TimerEvent event)
/*     */   {
/* 561 */     if (this.events.contains(event))
/*     */     {
/* 563 */       this.events.remove(event);
/*     */       
/*     */ 
/*     */ 
/* 567 */       notify();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void destroy()
/*     */   {
/* 574 */     if (this.indestructable)
/*     */     {
/* 576 */       Debug.out("Attempt to destroy indestructable timer '" + getName() + "'");
/*     */     }
/*     */     else
/*     */     {
/* 580 */       this.destroyed = true;
/*     */       
/* 582 */       notify();
/*     */       
/* 584 */       SystemTime.unregisterClockChangeListener(this);
/*     */     }
/*     */     
/*     */     try
/*     */     {
/* 589 */       timers_mon.enter();
/*     */       
/* 591 */       for (iter = timers.iterator(); iter.hasNext();) {
/* 592 */         WeakReference timerRef = (WeakReference)iter.next();
/* 593 */         Object timer = timerRef.get();
/* 594 */         if ((timer == null) || (timer == this))
/* 595 */           iter.remove();
/*     */       }
/*     */     } finally {
/*     */       Iterator iter;
/* 599 */       timers_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 607 */     return this.thread_pool.getName();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void dump()
/*     */   {
/* 613 */     System.out.println("Timer '" + this.thread_pool.getName() + "': dump");
/*     */     
/* 615 */     Iterator it = this.events.iterator();
/*     */     
/* 617 */     while (it.hasNext())
/*     */     {
/* 619 */       TimerEvent ev = (TimerEvent)it.next();
/*     */       
/* 621 */       System.out.println("\t" + ev.getString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class evidenceGenerator
/*     */     implements AEDiagnosticsEvidenceGenerator
/*     */   {
/*     */     public void generate(IndentWriter writer)
/*     */     {
/* 633 */       ArrayList lines = new ArrayList();
/* 634 */       int count = 0;
/*     */       try {
/*     */         try {
/* 637 */           Timer.timers_mon.enter();
/*     */           
/* 639 */           for (iter = Timer.timers.iterator(); iter.hasNext();) {
/* 640 */             WeakReference timerRef = (WeakReference)iter.next();
/* 641 */             Timer timer = (Timer)timerRef.get();
/* 642 */             if (timer == null) {
/* 643 */               iter.remove();
/*     */             } else {
/* 645 */               count++;
/*     */               
/* 647 */               List events = timer.getEvents();
/*     */               
/* 649 */               lines.add(timer.thread_pool.getName() + ", " + events.size() + " events:");
/*     */               
/*     */ 
/* 652 */               Iterator it = events.iterator();
/* 653 */               while (it.hasNext()) {
/* 654 */                 TimerEvent ev = (TimerEvent)it.next();
/*     */                 
/* 656 */                 lines.add("  " + ev.getString());
/*     */               }
/*     */             }
/*     */           }
/*     */         } finally { Iterator iter;
/* 661 */           Timer.timers_mon.exit();
/*     */         }
/*     */         
/* 664 */         writer.println("Timers: " + count + " (time=" + SystemTime.getCurrentTime() + "/" + SystemTime.getMonotonousTime() + ")");
/* 665 */         writer.indent();
/* 666 */         for (Iterator iter = lines.iterator(); iter.hasNext();) {
/* 667 */           String line = (String)iter.next();
/* 668 */           writer.println(line);
/*     */         }
/* 670 */         writer.exdent();
/*     */       } catch (Throwable e) {
/* 672 */         writer.println(e.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/Timer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */