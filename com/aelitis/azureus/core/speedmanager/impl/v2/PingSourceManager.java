/*     */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManagerPingSource;
/*     */ import com.aelitis.azureus.core.util.average.Average;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ public class PingSourceManager
/*     */ {
/*  45 */   private final Map pingAverages = new HashMap();
/*  46 */   private long lastPingRemoval = 0L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long TIME_BETWEEN_BAD_PING_REMOVALS = 120000L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long TIME_BETWEEN_SLOW_PING_REMOVALS = 300000L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long TIME_BETWEEN_FORCED_CYCLE_REMOVALS = 1800000L;
/*     */   
/*     */ 
/*     */ 
/*     */   public void checkPingSources(SpeedManagerPingSource[] sources)
/*     */   {
/*  64 */     if (sources == null) {
/*  65 */       return;
/*     */     }
/*     */     
/*     */ 
/*  69 */     if (sources.length < 3) {
/*  70 */       return;
/*     */     }
/*     */     
/*     */ 
/*  74 */     if (checkForBadPing(sources)) {
/*  75 */       return;
/*     */     }
/*     */     
/*     */ 
/*  79 */     if (checkForSlowSource(sources)) {
/*  80 */       return;
/*     */     }
/*     */     
/*     */ 
/*  84 */     forcePingSourceChange(sources);
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
/*     */   private boolean forcePingSourceChange(SpeedManagerPingSource[] sources)
/*     */   {
/*  97 */     long currTime = SystemTime.getCurrentTime();
/*  98 */     if (currTime < this.lastPingRemoval + 1800000L) {
/*  99 */       return false;
/*     */     }
/*     */     
/* 102 */     if (sources.length < 3) {
/* 103 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 107 */     SpeedManagerPingSource slowestSource = null;
/* 108 */     double slowestPing = 0.0D;
/* 109 */     double fastestPing = 10000.0D;
/*     */     
/* 111 */     int len = sources.length;
/* 112 */     for (int i = 0; i < len; i++) {
/* 113 */       PingSourceStats pss = (PingSourceStats)this.pingAverages.get(sources[i]);
/* 114 */       Average ave = pss.getHistory();
/* 115 */       double pingTime = ave.getAverage();
/*     */       
/*     */ 
/* 118 */       if (pingTime > slowestPing) {
/* 119 */         slowestPing = pingTime;
/* 120 */         slowestSource = sources[i];
/*     */       }
/*     */       
/*     */ 
/* 124 */       if (pingTime < fastestPing) {
/* 125 */         fastestPing = pingTime;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 131 */     resetTimer();
/*     */     
/* 133 */     if ((slowestPing > 2.0D * fastestPing) && 
/* 134 */       (slowestSource != null)) {
/* 135 */       slowestSource.destroy();
/* 136 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 140 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean checkForSlowSource(SpeedManagerPingSource[] sources)
/*     */   {
/* 151 */     long currTime = SystemTime.getCurrentTime();
/* 152 */     if (currTime < this.lastPingRemoval + 300000L) {
/* 153 */       return false;
/*     */     }
/*     */     
/* 156 */     SpeedManagerPingSource slowestSource = null;
/* 157 */     if (sources.length < 3) {
/* 158 */       return false;
/*     */     }
/*     */     
/* 161 */     double fastA = 10000.0D;
/* 162 */     double fastB = 10000.0D;
/* 163 */     double slowest = 0.0D;
/* 164 */     int len = sources.length;
/* 165 */     for (int i = 0; i < len; i++) {
/* 166 */       PingSourceStats pss = (PingSourceStats)this.pingAverages.get(sources[i]);
/* 167 */       Average ave = pss.getHistory();
/* 168 */       double pingTime = ave.getAverage();
/*     */       
/*     */ 
/* 171 */       if (pingTime < fastA) {
/* 172 */         fastB = fastA;
/* 173 */         fastA = pingTime;
/* 174 */       } else if (pingTime < fastB) {
/* 175 */         fastB = pingTime;
/*     */       }
/*     */       
/*     */ 
/* 179 */       if (pingTime > slowest) {
/* 180 */         slowest = pingTime;
/* 181 */         slowestSource = sources[i];
/* 182 */         resetTimer();
/*     */       }
/*     */     }
/*     */     
/* 186 */     double sumFastest = fastA + fastB;
/*     */     
/* 188 */     boolean removedSource = false;
/* 189 */     if (sumFastest * 2.0D < slowest)
/*     */     {
/* 191 */       if (slowestSource != null) {
/* 192 */         slowestSource.destroy();
/* 193 */         SpeedManagerLogger.log("dropping ping source: " + slowestSource.getAddress() + " for being 2x slower then two fastest.");
/* 194 */         removedSource = true;
/* 195 */         resetTimer();
/*     */       }
/*     */     }
/*     */     
/* 199 */     return removedSource;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean checkForBadPing(SpeedManagerPingSource[] sources)
/*     */   {
/* 210 */     long currTime = SystemTime.getCurrentTime();
/* 211 */     if (currTime < this.lastPingRemoval + 120000L) {
/* 212 */       return false;
/*     */     }
/*     */     
/* 215 */     double highestLongTermPing = 0.0D;
/* 216 */     SpeedManagerPingSource highestSource = null;
/* 217 */     double lowestLongTermPing = 10000.0D;
/*     */     
/* 219 */     int len = sources.length;
/* 220 */     for (int i = 0; i < len; i++) {
/* 221 */       PingSourceStats pss = (PingSourceStats)this.pingAverages.get(sources[i]);
/*     */       
/* 223 */       if (pss != null)
/*     */       {
/*     */ 
/* 226 */         Average a = pss.getLongTermAve();
/* 227 */         double avePingTime = a.getAverage();
/*     */         
/*     */ 
/* 230 */         if (avePingTime > highestLongTermPing) {
/* 231 */           highestLongTermPing = avePingTime;
/* 232 */           highestSource = sources[i];
/*     */         }
/*     */         
/*     */ 
/* 236 */         if (avePingTime < lowestLongTermPing) {
/* 237 */           lowestLongTermPing = avePingTime;
/*     */         }
/*     */       }
/*     */     }
/* 241 */     boolean removedSource = false;
/*     */     
/* 243 */     if (lowestLongTermPing * 8.0D < highestLongTermPing)
/*     */     {
/* 245 */       if (highestSource != null) {
/* 246 */         SpeedManagerLogger.log("dropping ping source: " + highestSource.getAddress() + " for being 8x greater then min source.");
/* 247 */         highestSource.destroy();
/* 248 */         removedSource = true;
/* 249 */         resetTimer();
/*     */       }
/*     */     }
/*     */     
/* 253 */     return removedSource;
/*     */   }
/*     */   
/*     */   public void pingSourceFound(SpeedManagerPingSource source, boolean is_replacement)
/*     */   {
/* 258 */     PingSourceStats pss = new PingSourceStats(source);
/* 259 */     this.pingAverages.put(source, pss);
/*     */   }
/*     */   
/*     */   public void pingSourceFailed(SpeedManagerPingSource source) {
/* 263 */     if (this.pingAverages.remove(source) == null) {
/* 264 */       SpeedManagerLogger.log("didn't find source: " + source.getAddress().getHostName());
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPingTime(SpeedManagerPingSource source)
/*     */   {
/* 270 */     if (source == null) {
/* 271 */       return;
/*     */     }
/*     */     
/* 274 */     PingSourceStats pss = (PingSourceStats)this.pingAverages.get(source);
/*     */     
/* 276 */     if (pss == null) {
/* 277 */       pingSourceFound(source, false);
/* 278 */       pss = (PingSourceStats)this.pingAverages.get(source);
/* 279 */       SpeedManagerLogger.trace("added new source from addPingTime.");
/*     */     }
/*     */     
/* 282 */     int pingTime = source.getPingTime();
/* 283 */     if (pingTime > 0) {
/* 284 */       pss.addPingTime(source.getPingTime());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void resetTimer()
/*     */   {
/* 293 */     this.lastPingRemoval = SystemTime.getCurrentTime();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/PingSourceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */