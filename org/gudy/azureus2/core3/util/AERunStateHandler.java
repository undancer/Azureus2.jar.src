/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*     */ public class AERunStateHandler
/*     */ {
/*     */   public static final long RS_DELAYED_UI = 1L;
/*     */   public static final long RS_UDP_NET_ONLY = 2L;
/*     */   public static final long RS_DHT_SLEEPING = 4L;
/*     */   public static final long RS_ALL_ACTIVE = 0L;
/*     */   public static final long RS_ALL_LOW = -1L;
/*  39 */   public static final long[] RS_MODES = { 1L, 2L, 4L };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  45 */   public static final String[] RS_MODE_NAMES = { "dui: Delay UI Initialisation", "uno: UDP Network Only", "ds:  DHT Sleeping" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  52 */   private static final boolean start_low = COConfigurationManager.getBooleanParameter("Start In Low Resource Mode");
/*     */   
/*  54 */   private static long current_mode = start_low ? -1L : 0L;
/*     */   
/*  56 */   private static final AsyncDispatcher dispatcher = new AsyncDispatcher(2500);
/*     */   
/*  58 */   private static final CopyOnWriteList<RunStateChangeListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */   public static boolean isDelayedUI()
/*     */   {
/*  63 */     return (current_mode & 1L) != 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isUDPNetworkOnly()
/*     */   {
/*  69 */     return (current_mode & 0x2) != 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public static boolean isDHTSleeping()
/*     */   {
/*  75 */     return (current_mode & 0x4) != 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public static long getResourceMode()
/*     */   {
/*  81 */     return current_mode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void setResourceMode(final long new_mode)
/*     */   {
/*  88 */     synchronized (dispatcher)
/*     */     {
/*  90 */       if (new_mode == current_mode)
/*     */       {
/*  92 */         return;
/*     */       }
/*     */       
/*  95 */       current_mode = new_mode;
/*     */       
/*  97 */       Iterator<RunStateChangeListener> it = listeners.iterator();
/*     */       
/*  99 */       dispatcher.dispatch(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 105 */           while (this.val$it.hasNext()) {
/*     */             try
/*     */             {
/* 108 */               ((AERunStateHandler.RunStateChangeListener)this.val$it.next()).runStateChanged(new_mode);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 112 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addListener(RunStateChangeListener l, boolean fire_now)
/*     */   {
/* 126 */     synchronized (dispatcher)
/*     */     {
/* 128 */       listeners.add(l);
/*     */       
/* 130 */       if (fire_now)
/*     */       {
/* 132 */         dispatcher.dispatch(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */             try
/*     */             {
/* 139 */               this.val$l.runStateChanged(AERunStateHandler.current_mode);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 143 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeListener(RunStateChangeListener l)
/*     */   {
/* 155 */     synchronized (dispatcher)
/*     */     {
/* 157 */       listeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface RunStateChangeListener
/*     */   {
/*     */     public abstract void runStateChanged(long paramLong);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AERunStateHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */