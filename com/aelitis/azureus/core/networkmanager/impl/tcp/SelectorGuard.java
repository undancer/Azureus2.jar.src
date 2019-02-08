/*     */ package com.aelitis.azureus.core.networkmanager.impl.tcp;
/*     */ 
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class SelectorGuard
/*     */ {
/*     */   private static final int SELECTOR_SPIN_THRESHOLD = 200;
/*     */   private static final int SELECTOR_FAILURE_THRESHOLD = 10000;
/*     */   private static final int MAX_IGNORES = 5;
/*  43 */   private boolean marked = false;
/*  44 */   private int consecutiveZeroSelects = 0;
/*     */   
/*     */   private long beforeSelectTime;
/*     */   private long select_op_time;
/*     */   private final String type;
/*     */   private final GuardListener listener;
/*  50 */   private int ignores = 0;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public SelectorGuard(String _type, GuardListener _listener)
/*     */   {
/*  57 */     this.type = _type;
/*  58 */     this.listener = _listener;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getType()
/*     */   {
/*  64 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void markPreSelectTime()
/*     */   {
/*  72 */     this.beforeSelectTime = SystemTime.getCurrentTime();
/*  73 */     this.marked = true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void verifySelectorIntegrity(int num_keys_ready, long time_threshold)
/*     */   {
/*  81 */     if (num_keys_ready > 0) {
/*  82 */       this.ignores += 1;
/*  83 */       if (this.ignores > 5) {
/*  84 */         this.ignores = 0;
/*  85 */         this.consecutiveZeroSelects = 0;
/*     */       }
/*  87 */       return;
/*     */     }
/*     */     
/*  90 */     if (this.marked) this.marked = false; else {
/*  91 */       Debug.out("Error: You must run markPreSelectTime() before calling isSelectorOK");
/*     */     }
/*  93 */     this.select_op_time = (SystemTime.getCurrentTime() - this.beforeSelectTime);
/*     */     
/*  95 */     if ((this.select_op_time > time_threshold) || (this.select_op_time < 0L))
/*     */     {
/*  97 */       this.consecutiveZeroSelects = 0;
/*  98 */       return;
/*     */     }
/*     */     
/*     */ 
/* 102 */     this.consecutiveZeroSelects += 1;
/*     */     
/* 104 */     if ((this.consecutiveZeroSelects % 20 == 0) && (Constants.isWindows))
/*     */     {
/* 106 */       if (this.consecutiveZeroSelects > 40) {
/* 107 */         Debug.out("consecutiveZeroSelects=" + this.consecutiveZeroSelects);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 112 */     if (this.consecutiveZeroSelects > 200) {
/* 113 */       if ((Constants.isWindows) && ((Constants.JAVA_VERSION.startsWith("1.4")) || (Constants.JAVA_VERSION.startsWith("1.5"))))
/*     */       {
/*     */ 
/* 116 */         if (!this.listener.safeModeSelectEnabled()) {
/* 117 */           String msg = "Likely faulty socket selector detected: reverting to safe-mode socket selection. [JRE " + Constants.JAVA_VERSION + "]\n";
/* 118 */           msg = msg + "Please see http://wiki.vuze.com/w/LikelyFaultySocketSelector for help.";
/* 119 */           Debug.out(msg);
/* 120 */           Logger.log(new LogAlert(false, 1, msg));
/*     */           
/* 122 */           this.consecutiveZeroSelects = 0;
/* 123 */           this.listener.spinDetected();
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 129 */         this.consecutiveZeroSelects = 0;
/* 130 */         try { Thread.sleep(50L); } catch (Throwable t) { t.printStackTrace(); }
/* 131 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 136 */     if (this.consecutiveZeroSelects > 10000) {
/* 137 */       String msg = "Likely network disconnect/reconnect: Repairing socket channel selector. [JRE " + Constants.JAVA_VERSION + "]\n";
/* 138 */       msg = msg + "Please see http://wiki.vuze.com/w/LikelyNetworkDisconnectReconnect for help.";
/* 139 */       Debug.out(msg);
/* 140 */       Logger.log(new LogAlert(false, 1, msg));
/*     */       
/* 142 */       this.consecutiveZeroSelects = 0;
/* 143 */       this.listener.failureDetected();
/* 144 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface GuardListener
/*     */   {
/*     */     public abstract boolean safeModeSelectEnabled();
/*     */     
/*     */     public abstract void spinDetected();
/*     */     
/*     */     public abstract void failureDetected();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/tcp/SelectorGuard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */