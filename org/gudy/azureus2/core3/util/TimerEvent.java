/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimerEvent
/*     */   extends ThreadPoolTask
/*     */   implements Comparable<TimerEvent>
/*     */ {
/*     */   private String name;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final Timer timer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final long created;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long when;
/*     */   
/*     */ 
/*     */ 
/*     */   private final TimerEventPerformer performer;
/*     */   
/*     */ 
/*     */ 
/*     */   private final boolean absolute;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean cancelled;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean has_run;
/*     */   
/*     */ 
/*     */ 
/*  46 */   private long unique_id = 1L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TimerEvent(Timer _timer, long _unique_id, long _created, long _when, boolean _absolute, TimerEventPerformer _performer)
/*     */   {
/*  57 */     this.timer = _timer;
/*  58 */     this.unique_id = _unique_id;
/*  59 */     this.when = _when;
/*  60 */     this.absolute = _absolute;
/*  61 */     this.performer = _performer;
/*     */     
/*  63 */     this.created = _created;
/*     */     
/*  65 */     if (Constants.IS_CVS_VERSION)
/*     */     {
/*     */ 
/*     */ 
/*  69 */       if ((this.when != 0L) && (this.when <= 604800000L))
/*     */       {
/*  71 */         new Exception("You sure you want to schedule an event in the past? Time should be absolute!").printStackTrace();
/*     */       }
/*  73 */       else if (this.when > 94608000000000L)
/*     */       {
/*  75 */         new Exception("You sure you want to schedule an event so far in the future?! (" + this.when + ")").printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setName(String _name)
/*     */   {
/*  84 */     this.name = _name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  90 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreatedTime()
/*     */   {
/*  96 */     return this.created;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getWhen()
/*     */   {
/* 102 */     return this.when;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setWhen(long new_when)
/*     */   {
/* 109 */     this.when = new_when;
/*     */   }
/*     */   
/*     */ 
/*     */   protected AERunnable getRunnable()
/*     */   {
/* 115 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   protected TimerEventPerformer getPerformer()
/*     */   {
/* 121 */     return this.performer;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isAbsolute()
/*     */   {
/* 127 */     return this.absolute;
/*     */   }
/*     */   
/*     */ 
/*     */   public void runSupport()
/*     */   {
/* 133 */     this.performer.perform(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void cancel()
/*     */   {
/* 139 */     this.cancelled = true;
/*     */     
/* 141 */     this.timer.cancelEvent(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized boolean isCancelled()
/*     */   {
/* 147 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setHasRun()
/*     */   {
/* 153 */     this.has_run = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasRun()
/*     */   {
/* 159 */     return this.has_run;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getUniqueId()
/*     */   {
/* 165 */     return this.unique_id;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int compareTo(TimerEvent other)
/*     */   {
/* 172 */     long res = this.when - other.when;
/*     */     
/* 174 */     if (res == 0L)
/*     */     {
/* 176 */       res = this.unique_id - other.unique_id;
/*     */       
/* 178 */       if (res == 0L)
/*     */       {
/* 180 */         return 0;
/*     */       }
/*     */     }
/*     */     
/* 184 */     return res < 0L ? -1 : 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void interruptTask() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String getString()
/*     */   {
/* 195 */     if ((this.performer instanceof TimerEventPeriodic))
/*     */     {
/* 197 */       TimerEventPeriodic tep = (TimerEventPeriodic)this.performer;
/*     */       
/* 199 */       return "when=" + getWhen() + ",run=" + hasRun() + ", can=" + isCancelled() + "/" + tep.isCancelled() + ",freq=" + tep.getFrequency() + ",target=" + tep.getPerformer() + (this.name == null ? "" : new StringBuilder().append(",name=").append(this.name).toString());
/*     */     }
/*     */     
/*     */ 
/* 203 */     return "when=" + getWhen() + ",run=" + hasRun() + ", can=" + isCancelled() + ",target=" + getPerformer() + (this.name == null ? "" : new StringBuilder().append(",name=").append(this.name).toString());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/TimerEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */