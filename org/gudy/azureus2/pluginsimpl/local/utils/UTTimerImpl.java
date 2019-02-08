/*     */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*     */ 
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.Timer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimer;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEvent;
/*     */ import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;
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
/*     */ public class UTTimerImpl
/*     */   implements UTTimer
/*     */ {
/*     */   private PluginInterface plugin_interface;
/*     */   private Timer timer;
/*     */   private boolean destroyed;
/*     */   
/*     */   public UTTimerImpl(String name, boolean lightweight)
/*     */   {
/*  49 */     if (!lightweight)
/*     */     {
/*  51 */       this.timer = new Timer(name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UTTimerImpl(PluginInterface pi, String name, boolean lightweight)
/*     */   {
/*  61 */     this.plugin_interface = pi;
/*     */     
/*  63 */     if (!lightweight)
/*     */     {
/*  65 */       this.timer = new Timer("Plugin " + pi.getPluginID() + ":" + name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UTTimerImpl(PluginInterface pi, String name, int priority)
/*     */   {
/*  75 */     this.plugin_interface = pi;
/*     */     
/*  77 */     this.timer = new Timer("Plugin " + pi.getPluginID() + ":" + name, 1, priority);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UTTimerEvent addEvent(long when, final UTTimerEventPerformer ext_performer)
/*     */   {
/*  85 */     if (this.destroyed)
/*     */     {
/*  87 */       throw new RuntimeException("Timer has been destroyed");
/*     */     }
/*     */     
/*  90 */     final timerEvent res = new timerEvent(null);
/*     */     
/*  92 */     TimerEventPerformer performer = new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */ 
/*  99 */         UtilitiesImpl.callWithPluginThreadContext(UTTimerImpl.this.plugin_interface, new Runnable()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 106 */             UTTimerImpl.1.this.val$res.perform(UTTimerImpl.1.this.val$ext_performer);
/*     */           }
/*     */         });
/*     */       }
/*     */     };
/*     */     
/* 112 */     if (this.timer == null)
/*     */     {
/* 114 */       res.setEvent(SimpleTimer.addEvent("Plugin:" + ext_performer.getClass(), when, performer));
/*     */     }
/*     */     else
/*     */     {
/* 118 */       res.setEvent(this.timer.addEvent("Plugin:" + ext_performer.getClass(), when, performer));
/*     */     }
/*     */     
/*     */ 
/* 122 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UTTimerEvent addPeriodicEvent(long periodic_millis, final UTTimerEventPerformer ext_performer)
/*     */   {
/* 130 */     if (this.destroyed)
/*     */     {
/* 132 */       throw new RuntimeException("Timer has been destroyed");
/*     */     }
/*     */     
/* 135 */     final timerEvent res = new timerEvent(null);
/*     */     
/* 137 */     TimerEventPerformer performer = new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */ 
/* 144 */         UtilitiesImpl.callWithPluginThreadContext(UTTimerImpl.this.plugin_interface, new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/* 153 */               UTTimerImpl.2.this.val$res.perform(UTTimerImpl.2.this.val$ext_performer);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 157 */               Debug.out("Plugin '" + UTTimerImpl.this.plugin_interface.getPluginName() + " (" + UTTimerImpl.this.plugin_interface.getPluginID() + " " + UTTimerImpl.this.plugin_interface.getPluginVersion() + ") caused an error while processing a timer event", e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 167 */     if (this.timer == null)
/*     */     {
/* 169 */       res.setEvent(SimpleTimer.addPeriodicEvent("Plugin:" + ext_performer.getClass(), periodic_millis, performer));
/*     */     }
/*     */     else
/*     */     {
/* 173 */       res.setEvent(this.timer.addPeriodicEvent("Plugin:" + ext_performer.getClass(), periodic_millis, performer));
/*     */     }
/*     */     
/*     */ 
/* 177 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 183 */     this.destroyed = true;
/*     */     
/* 185 */     if (this.timer != null)
/*     */     {
/* 187 */       this.timer.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class timerEvent
/*     */     implements UTTimerEvent
/*     */   {
/*     */     protected TimerEvent ev;
/*     */     
/*     */     protected TimerEventPeriodic pev;
/*     */     
/*     */ 
/*     */     protected void setEvent(TimerEventPeriodic _ev)
/*     */     {
/* 202 */       this.pev = _ev;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setEvent(TimerEvent _ev)
/*     */     {
/* 209 */       this.ev = _ev;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void perform(UTTimerEventPerformer p)
/*     */     {
/* 216 */       p.perform(this);
/*     */     }
/*     */     
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 222 */       if (this.ev != null)
/*     */       {
/* 224 */         this.ev.cancel();
/*     */       }
/*     */       else
/*     */       {
/* 228 */         this.pev.cancel();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/UTTimerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */