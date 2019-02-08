/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ public class TransferMode
/*     */ {
/*  38 */   private State mode = State.DOWNLOADING;
/*     */   
/*  40 */   private long lastTimeDownloadDetected = SystemTime.getCurrentTime();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long WAIT_TIME_FOR_SEEDING_MODE = 60000L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateStatus(SaturatedMode downloadBandwidth)
/*     */   {
/*  60 */     if (isConfTestingLimits()) {
/*  61 */       if (this.mode == State.DOWNLOAD_LIMIT_SEARCH) {
/*  62 */         this.lastTimeDownloadDetected = SystemTime.getCurrentTime();
/*     */       }
/*  64 */       return;
/*     */     }
/*     */     
/*  67 */     if (downloadBandwidth.compareTo(SaturatedMode.LOW) <= 0)
/*     */     {
/*     */ 
/*  70 */       long time = SystemTime.getCurrentTime();
/*     */       
/*  72 */       if (time > this.lastTimeDownloadDetected + 60000L) {
/*  73 */         this.mode = State.SEEDING;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  78 */       this.mode = State.DOWNLOADING;
/*  79 */       this.lastTimeDownloadDetected = SystemTime.getCurrentTime();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getString()
/*     */   {
/*  85 */     return this.mode.getString();
/*     */   }
/*     */   
/*     */   public State getMode() {
/*  89 */     return this.mode;
/*     */   }
/*     */   
/*     */   public void setMode(State newMode)
/*     */   {
/*  94 */     SpeedManagerLogger.trace(" setting transfer mode to: " + newMode.getString());
/*     */     
/*  96 */     this.mode = newMode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isDownloadMode()
/*     */   {
/* 105 */     return this.mode == State.DOWNLOADING;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isConfTestingLimits()
/*     */   {
/* 115 */     return (this.mode == State.DOWNLOAD_LIMIT_SEARCH) || (this.mode == State.UPLOAD_LIMIT_SEARCH);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class State
/*     */   {
/* 123 */     public static final State DOWNLOADING = new State("downloading");
/* 124 */     public static final State SEEDING = new State("seeding");
/* 125 */     public static final State DOWNLOAD_LIMIT_SEARCH = new State("download limit search");
/* 126 */     public static final State UPLOAD_LIMIT_SEARCH = new State("upload limit search");
/*     */     final String mode;
/*     */     
/*     */     private State(String _mode) {
/* 130 */       this.mode = _mode;
/*     */     }
/*     */     
/*     */     public String getString() {
/* 134 */       return this.mode;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/TransferMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */