/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimerEventPeriodic
/*     */   implements TimerEventPerformer
/*     */ {
/*     */   private final Timer timer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final long frequency;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final boolean absolute;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final TimerEventPerformer performer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private String name;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private TimerEvent current_event;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean cancelled;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected TimerEventPeriodic(Timer _timer, long _frequency, boolean _absolute, TimerEventPerformer _performer)
/*     */   {
/*  49 */     this.timer = _timer;
/*  50 */     this.frequency = _frequency;
/*  51 */     this.absolute = _absolute;
/*  52 */     this.performer = _performer;
/*     */     
/*  54 */     long now = SystemTime.getCurrentTime();
/*     */     
/*  56 */     this.current_event = this.timer.addEvent(now, now + this.frequency, this.absolute, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setName(String _name)
/*     */   {
/*  63 */     this.name = _name;
/*     */     
/*  65 */     synchronized (this)
/*     */     {
/*  67 */       if (this.current_event != null)
/*     */       {
/*  69 */         this.current_event.setName(this.name);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  77 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   protected TimerEventPerformer getPerformer()
/*     */   {
/*  83 */     return this.performer;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getFrequency()
/*     */   {
/*  89 */     return this.frequency;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/*  95 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void perform(TimerEvent event)
/*     */   {
/* 102 */     if (!this.cancelled)
/*     */     {
/*     */       try {
/* 105 */         this.performer.perform(event);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 109 */         DebugLight.printStackTrace(e);
/*     */       }
/*     */       
/* 112 */       synchronized (this)
/*     */       {
/* 114 */         if (!this.cancelled)
/*     */         {
/* 116 */           long now = SystemTime.getCurrentTime();
/*     */           
/* 118 */           this.current_event = this.timer.addEvent(this.name, now, now + this.frequency, this.absolute, this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void cancel()
/*     */   {
/* 127 */     if (this.current_event != null)
/*     */     {
/* 129 */       this.current_event.cancel();
/*     */       
/* 131 */       this.cancelled = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 138 */     TimerEvent ce = this.current_event;
/*     */     
/*     */     String ev_data;
/*     */     String ev_data;
/* 142 */     if (ce == null)
/*     */     {
/* 144 */       ev_data = "?";
/*     */     }
/*     */     else {
/* 147 */       ev_data = "when=" + ce.getWhen() + ",run=" + ce.hasRun() + ", can=" + ce.isCancelled();
/*     */     }
/*     */     
/* 150 */     return ev_data + ",freq=" + getFrequency() + ",target=" + getPerformer() + (this.name == null ? "" : new StringBuilder().append(",name=").append(this.name).toString());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/TimerEventPeriodic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */