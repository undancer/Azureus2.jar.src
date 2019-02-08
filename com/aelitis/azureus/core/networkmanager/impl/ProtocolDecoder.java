/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public abstract class ProtocolDecoder
/*     */ {
/*  30 */   private static final LogIDs LOGID = LogIDs.NWMAN;
/*     */   
/*     */   private static final int TIMEOUT_CHECK = 5000;
/*     */   
/*     */   private static final int LOG_TICKS = 12;
/*  35 */   static final List<ProtocolDecoder> decoders = new ArrayList();
/*     */   
/*  37 */   static final AEMonitor class_mon = new AEMonitor("TCPProtocolDecoder:class");
/*     */   
/*  39 */   private static int loop = 0;
/*     */   
/*     */ 
/*     */   static
/*     */   {
/*  44 */     SimpleTimer.addPeriodicEvent("ProtocolDecoder:timeouts", 5000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */ 
/*     */ 
/*  52 */         ProtocolDecoder.access$008();
/*     */         
/*     */         List<ProtocolDecoder> copy;
/*     */         try
/*     */         {
/*  57 */           ProtocolDecoder.class_mon.enter();
/*     */           
/*  59 */           if (ProtocolDecoder.loop % 12 == 0)
/*     */           {
/*  61 */             if (Logger.isEnabled())
/*     */             {
/*  63 */               if (ProtocolDecoder.decoders.size() > 0)
/*     */               {
/*  65 */                 Logger.log(new LogEvent(ProtocolDecoder.LOGID, "Active protocol decoders = " + ProtocolDecoder.decoders.size()));
/*     */               }
/*     */             }
/*     */           }
/*     */           
/*  70 */           copy = new ArrayList(ProtocolDecoder.decoders);
/*     */         }
/*     */         finally
/*     */         {
/*  74 */           ProtocolDecoder.class_mon.exit();
/*     */         }
/*     */         
/*  77 */         if (copy.size() > 0)
/*     */         {
/*  79 */           Object to_remove = new ArrayList();
/*     */           
/*  81 */           long now = SystemTime.getCurrentTime();
/*     */           
/*  83 */           for (ProtocolDecoder decoder : copy)
/*     */           {
/*  85 */             if (decoder.isComplete(now))
/*     */             {
/*  87 */               ((List)to_remove).add(decoder);
/*     */             }
/*     */           }
/*     */           
/*  91 */           if (((List)to_remove).size() > 0) {
/*     */             try
/*     */             {
/*  94 */               ProtocolDecoder.class_mon.enter();
/*     */               
/*  96 */               for (ProtocolDecoder decoder : (List)to_remove)
/*     */               {
/*  98 */                 ProtocolDecoder.decoders.remove(decoder);
/*     */               }
/*     */             }
/*     */             finally {
/* 102 */               ProtocolDecoder.class_mon.exit();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ProtocolDecoder(boolean run_timer)
/*     */   {
/* 117 */     if (run_timer) {
/*     */       try
/*     */       {
/* 120 */         class_mon.enter();
/*     */         
/* 122 */         decoders.add(this);
/*     */       }
/*     */       finally
/*     */       {
/* 126 */         class_mon.exit();
/*     */       }
/*     */     }
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
/*     */ 
/*     */   public static void addSecrets(byte[][] secrets)
/*     */   {
/* 142 */     ProtocolDecoderPHE.addSecretsSupport(secrets);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void removeSecrets(byte[][] secrets)
/*     */   {
/* 149 */     ProtocolDecoderPHE.removeSecretsSupport(secrets);
/*     */   }
/*     */   
/*     */   public abstract boolean isComplete(long paramLong);
/*     */   
/*     */   public abstract TransportHelperFilter getFilter();
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/ProtocolDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */