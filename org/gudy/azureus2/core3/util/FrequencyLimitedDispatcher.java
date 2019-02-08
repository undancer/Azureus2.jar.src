/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FrequencyLimitedDispatcher
/*     */ {
/*     */   private AERunnable target;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final long min_millis;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private long last_run;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private DelayedEvent delay_event;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FrequencyLimitedDispatcher(AERunnable _target, int _min_frequency_millis)
/*     */   {
/*  36 */     this.target = _target;
/*  37 */     this.min_millis = _min_frequency_millis;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSingleThreaded()
/*     */   {
/*  43 */     final AERunnable old_target = this.target;
/*     */     
/*  45 */     this.target = new AERunnable()
/*     */     {
/*     */       private boolean running;
/*     */       
/*     */       private boolean pending;
/*     */       
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*  54 */         synchronized (this)
/*     */         {
/*  56 */           if (this.running)
/*     */           {
/*  58 */             this.pending = true;
/*     */             
/*  60 */             return;
/*     */           }
/*     */           
/*  63 */           this.running = true;
/*     */         }
/*     */         try
/*     */         {
/*  67 */           old_target.runSupport();
/*     */         }
/*     */         finally
/*     */         {
/*     */           boolean was_pending;
/*     */           
/*  73 */           synchronized (this) {
/*     */             boolean was_pending;
/*  75 */             this.running = false;
/*     */             
/*  77 */             was_pending = this.pending;
/*     */             
/*  79 */             this.pending = false;
/*     */           }
/*     */           
/*  82 */           if (was_pending)
/*     */           {
/*  84 */             FrequencyLimitedDispatcher.this.dispatch();
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public void dispatch()
/*     */   {
/*  94 */     long now = SystemTime.getMonotonousTime();
/*     */     
/*  96 */     boolean run_it = false;
/*     */     
/*  98 */     synchronized (this)
/*     */     {
/* 100 */       if (this.delay_event == null)
/*     */       {
/* 102 */         long delay = this.min_millis - (now - this.last_run);
/*     */         
/* 104 */         if ((now < this.last_run) || (delay <= 0L))
/*     */         {
/* 106 */           this.last_run = now;
/*     */           
/* 108 */           run_it = true;
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 114 */           this.delay_event = new DelayedEvent("FreqLimDisp", delay, new AERunnable()
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/*     */ 
/* 123 */               long now = SystemTime.getMonotonousTime();
/*     */               
/* 125 */               synchronized (FrequencyLimitedDispatcher.this)
/*     */               {
/* 127 */                 FrequencyLimitedDispatcher.this.last_run = now;
/*     */                 
/* 129 */                 FrequencyLimitedDispatcher.this.delay_event = null;
/*     */               }
/*     */               
/* 132 */               FrequencyLimitedDispatcher.this.target.run();
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 139 */     if (run_it)
/*     */     {
/* 141 */       this.target.run();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/FrequencyLimitedDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */