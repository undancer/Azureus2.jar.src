/*     */ package org.gudy.azureus2.core3.stats.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.stats.StatsWriterPeriodic;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
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
/*     */ public class StatsWriterPeriodicImpl
/*     */   implements StatsWriterPeriodic, COConfigurationListener, TimerEventPerformer
/*     */ {
/*  44 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*     */   private static StatsWriterPeriodicImpl singleton;
/*     */   
/*     */   private boolean started;
/*     */   
/*  50 */   private long last_write_time = 0L;
/*     */   private final AzureusCore core;
/*     */   private TimerEventPeriodic event;
/*     */   private boolean config_enabled;
/*     */   private int config_period;
/*     */   private String config_dir;
/*     */   private String config_file;
/*     */   
/*     */   public static synchronized StatsWriterPeriodic create(AzureusCore _core)
/*     */   {
/*  60 */     synchronized (StatsWriterPeriodicImpl.class)
/*     */     {
/*  62 */       if (singleton == null)
/*     */       {
/*  64 */         singleton = new StatsWriterPeriodicImpl(_core);
/*     */       }
/*  66 */       return singleton;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected StatsWriterPeriodicImpl(AzureusCore _core)
/*     */   {
/*  74 */     this.core = _core;
/*     */   }
/*     */   
/*     */   public void perform(TimerEvent event)
/*     */   {
/*  79 */     update();
/*     */   }
/*     */   
/*     */   protected void update()
/*     */   {
/*     */     try
/*     */     {
/*  86 */       writeStats();
/*     */     }
/*     */     catch (Throwable e) {
/*  89 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected synchronized void readConfigValues()
/*     */   {
/*  96 */     this.config_enabled = COConfigurationManager.getBooleanParameter("Stats Enable");
/*     */     
/*  98 */     this.config_period = COConfigurationManager.getIntParameter("Stats Period");
/*     */     
/* 100 */     this.config_dir = COConfigurationManager.getStringParameter("Stats Dir");
/*     */     
/* 102 */     this.config_file = COConfigurationManager.getStringParameter("Stats File");
/*     */     
/* 104 */     if (this.config_enabled)
/*     */     {
/* 106 */       long targetFrequency = 'Ϩ' * (this.config_period < 30000 ? this.config_period : '田');
/* 107 */       if ((this.event != null) && (this.event.getFrequency() != targetFrequency))
/*     */       {
/* 109 */         this.event.cancel();
/* 110 */         this.event = null;
/*     */       }
/*     */       
/* 113 */       if (this.event == null) {
/* 114 */         this.event = SimpleTimer.addPeriodicEvent("StatsWriter", targetFrequency, this);
/*     */       }
/* 116 */     } else if (this.event != null)
/*     */     {
/* 118 */       this.event.cancel();
/* 119 */       this.event = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeStats()
/*     */   {
/* 129 */     synchronized (this)
/*     */     {
/* 131 */       if (!this.config_enabled)
/*     */       {
/* 133 */         return;
/*     */       }
/*     */       
/* 136 */       int period = this.config_period;
/*     */       
/* 138 */       long now = SystemTime.getMonotonousTime() / 1000L;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 145 */       if (now - this.last_write_time < period - 1)
/*     */       {
/* 147 */         return;
/*     */       }
/*     */       
/* 150 */       this.last_write_time = now;
/*     */       try
/*     */       {
/* 153 */         String dir = this.config_dir;
/*     */         
/* 155 */         dir = dir.trim();
/*     */         
/* 157 */         if (dir.length() == 0)
/*     */         {
/* 159 */           dir = File.separator;
/*     */         }
/*     */         
/* 162 */         String file_name = dir;
/*     */         
/* 164 */         if (!file_name.endsWith(File.separator))
/*     */         {
/* 166 */           file_name = file_name + File.separator;
/*     */         }
/*     */         
/* 169 */         String file = this.config_file;
/*     */         
/* 171 */         if (file.trim().length() == 0)
/*     */         {
/* 173 */           file = "Azureus_Stats.xml";
/*     */         }
/*     */         
/* 176 */         file_name = file_name + file;
/*     */         
/* 178 */         if (Logger.isEnabled()) {
/* 179 */           Logger.log(new LogEvent(LOGID, "Stats Logged to '" + file_name + "'"));
/*     */         }
/* 181 */         new StatsWriterImpl(this.core).write(file_name);
/*     */       }
/*     */       catch (Throwable e) {
/* 184 */         Logger.log(new LogEvent(LOGID, "Stats Logging fails", e));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void configurationSaved()
/*     */   {
/* 194 */     readConfigValues();
/*     */     
/* 196 */     writeStats();
/*     */   }
/*     */   
/*     */ 
/*     */   public void start()
/*     */   {
/* 202 */     if (this.started)
/* 203 */       return;
/* 204 */     this.started = true;
/* 205 */     COConfigurationManager.addListener(this);
/* 206 */     configurationSaved();
/*     */   }
/*     */   
/*     */ 
/*     */   public void stop()
/*     */   {
/* 212 */     COConfigurationManager.removeListener(this);
/*     */     
/* 214 */     synchronized (this) {
/* 215 */       if (this.event != null) {
/* 216 */         this.event.cancel();
/* 217 */         this.event = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/impl/StatsWriterPeriodicImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */