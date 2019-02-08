/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingMapper;
/*     */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class PingSpaceMon
/*     */ {
/*     */   private static final long INTERVAL = 900000L;
/*     */   long nextCheck;
/*     */   TransferMode mode;
/*     */   final List listeners;
/*     */   
/*     */   public PingSpaceMon()
/*     */   {
/*  37 */     this.nextCheck = (System.currentTimeMillis() + 900000L);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  42 */     this.listeners = new ArrayList();
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(PSMonitorListener listener)
/*     */   {
/*  48 */     for (int i = 0; i < this.listeners.size(); i++) {
/*  49 */       PSMonitorListener t = (PSMonitorListener)this.listeners.get(i);
/*  50 */       if (t == listener) {
/*  51 */         SpeedManagerLogger.trace("Not logging same listener twice. listener=" + listener.toString());
/*  52 */         return;
/*     */       }
/*     */     }
/*     */     
/*  56 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */   public boolean removeListener(PSMonitorListener listener)
/*     */   {
/*  61 */     return this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   boolean checkForLowerLimits()
/*     */   {
/*  68 */     long curr = SystemTime.getCurrentTime();
/*  69 */     if (curr > this.nextCheck) {
/*  70 */       SpeedManagerLogger.trace("PingSpaceMon checking for lower limits.");
/*     */       
/*  72 */       for (int i = 0; i < this.listeners.size(); i++) {
/*  73 */         PSMonitorListener l = (PSMonitorListener)this.listeners.get(i);
/*     */         
/*  75 */         if (l != null) {
/*  76 */           l.notifyUpload(getUploadEstCapacity());
/*     */         } else {
/*  78 */           SpeedManagerLogger.trace("listener index _" + i + "_ was null.");
/*     */         }
/*     */       }
/*     */       
/*  82 */       resetTimer();
/*  83 */       return true;
/*     */     }
/*  85 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean updateStatus(TransferMode tMode)
/*     */   {
/*  95 */     if (this.mode == null) {
/*  96 */       this.mode = tMode;
/*  97 */       return true;
/*     */     }
/*     */     
/* 100 */     if (this.mode.getMode() != tMode.getMode()) {
/* 101 */       this.mode = tMode;
/* 102 */       resetTimer();
/* 103 */       return true;
/*     */     }
/* 105 */     return checkForLowerLimits();
/*     */   }
/*     */   
/*     */   void resetTimer() {
/* 109 */     long curr = SystemTime.getCurrentTime();
/* 110 */     this.nextCheck = (curr + 900000L);
/* 111 */     SpeedManagerLogger.trace("Monitor resetting time. Next check in interval.");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SpeedManagerLimitEstimate getUploadLimit(boolean persistent)
/*     */   {
/*     */     try
/*     */     {
/* 121 */       SMInstance pm = SMInstance.getInstance();
/* 122 */       SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 123 */       SpeedManagerPingMapper persistentMap = adapter.getPingMapper();
/* 124 */       return persistentMap.getEstimatedUploadLimit(true);
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 130 */       SpeedManagerLogger.log(t.toString());
/* 131 */       t.printStackTrace();
/*     */     }
/*     */     
/* 134 */     return new DefaultLimitEstimate();
/*     */   }
/*     */   
/*     */   public static SpeedManagerLimitEstimate getUploadEstCapacity()
/*     */   {
/*     */     try
/*     */     {
/* 141 */       SMInstance pm = SMInstance.getInstance();
/* 142 */       SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 143 */       SpeedManager sm = adapter.getSpeedManager();
/* 144 */       return sm.getEstimatedUploadCapacityBytesPerSec();
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 150 */       SpeedManagerLogger.log(t.toString());
/* 151 */       t.printStackTrace();
/*     */     }
/*     */     
/* 154 */     return new DefaultLimitEstimate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SpeedManagerLimitEstimate getDownloadLimit()
/*     */   {
/*     */     try
/*     */     {
/* 164 */       SMInstance pm = SMInstance.getInstance();
/* 165 */       SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 166 */       SpeedManagerPingMapper persistentMap = adapter.getPingMapper();
/* 167 */       return persistentMap.getEstimatedDownloadLimit(true);
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 173 */       SpeedManagerLogger.log(t.toString());
/* 174 */       t.printStackTrace();
/*     */     }
/*     */     
/* 177 */     return new DefaultLimitEstimate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static SpeedManagerLimitEstimate getDownloadEstCapacity()
/*     */   {
/*     */     try
/*     */     {
/* 188 */       SMInstance pm = SMInstance.getInstance();
/* 189 */       SpeedManagerAlgorithmProviderAdapter adapter = pm.getAdapter();
/* 190 */       SpeedManager sm = adapter.getSpeedManager();
/* 191 */       return sm.getEstimatedDownloadCapacityBytesPerSec();
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 197 */       SpeedManagerLogger.log(t.toString());
/* 198 */       t.printStackTrace();
/*     */     }
/*     */     
/* 201 */     return new DefaultLimitEstimate();
/*     */   }
/*     */   
/*     */   static class DefaultLimitEstimate
/*     */     implements SpeedManagerLimitEstimate
/*     */   {
/*     */     public int getBytesPerSec()
/*     */     {
/* 209 */       return 1;
/*     */     }
/*     */     
/*     */     public float getEstimateType() {
/* 213 */       return -1.0F;
/*     */     }
/*     */     
/* 216 */     public float getMetricRating() { return -1.0F; }
/*     */     
/*     */     public int[][] getSegments()
/*     */     {
/* 220 */       return new int[0][];
/*     */     }
/*     */     
/* 223 */     public long getWhen() { return 0L; }
/*     */     
/* 225 */     public String getString() { return "default"; }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/PingSpaceMon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */