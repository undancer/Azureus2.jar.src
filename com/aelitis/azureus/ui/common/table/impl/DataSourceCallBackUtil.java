/*     */ package com.aelitis.azureus.ui.common.table.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.Timer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataSourceCallBackUtil
/*     */ {
/*     */   public static final long IMMEDIATE_ADDREMOVE_DELAY = 150L;
/*     */   private static final long IMMEDIATE_ADDREMOVE_MAXDELAY = 2000L;
/*  34 */   private static Timer timerProcessDataSources = new Timer("Process Data Sources");
/*     */   
/*     */   private static TimerEvent timerEventProcessDS;
/*     */   
/*  38 */   private static List processDataSourcesOutstanding = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean addDataSourceAggregated(addDataSourceCallback callback)
/*     */   {
/*  45 */     if (callback == null)
/*     */     {
/*  47 */       return true;
/*     */     }
/*     */     
/*  50 */     boolean processQueueImmediately = false;
/*     */     
/*  52 */     List to_do_now = null;
/*     */     
/*  54 */     synchronized (timerProcessDataSources)
/*     */     {
/*  56 */       if ((timerEventProcessDS != null) && (!timerEventProcessDS.hasRun()))
/*     */       {
/*     */ 
/*     */ 
/*  60 */         long now = SystemTime.getCurrentTime();
/*     */         
/*  62 */         if (now - timerEventProcessDS.getCreatedTime() < 2000L)
/*     */         {
/*  64 */           long lNextTime = now + 150L;
/*     */           
/*  66 */           timerProcessDataSources.adjustAllBy(lNextTime - timerEventProcessDS.getWhen());
/*     */           
/*  68 */           if (!processDataSourcesOutstanding.contains(callback))
/*     */           {
/*  70 */             processDataSourcesOutstanding.add(callback);
/*     */           }
/*     */         }
/*     */         else {
/*  74 */           timerEventProcessDS.cancel();
/*     */           
/*  76 */           timerEventProcessDS = null;
/*     */           
/*  78 */           processQueueImmediately = true;
/*     */           
/*  80 */           to_do_now = processDataSourcesOutstanding;
/*     */           
/*  82 */           processDataSourcesOutstanding = new ArrayList();
/*     */         }
/*     */       }
/*     */       else {
/*  86 */         if (!processDataSourcesOutstanding.contains(callback))
/*     */         {
/*  88 */           processDataSourcesOutstanding.add(callback);
/*     */         }
/*     */         
/*  91 */         timerEventProcessDS = timerProcessDataSources.addEvent(SystemTime.getCurrentTime() + 150L, new TimerEventPerformer()
/*     */         {
/*     */           public void perform(TimerEvent event)
/*     */           {
/*     */             List to_do;
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */             synchronized (DataSourceCallBackUtil.timerProcessDataSources)
/*     */             {
/* 104 */               DataSourceCallBackUtil.access$102(null);
/*     */               
/* 106 */               to_do = DataSourceCallBackUtil.processDataSourcesOutstanding;
/*     */               
/* 108 */               DataSourceCallBackUtil.access$202(new ArrayList());
/*     */             }
/*     */             
/* 111 */             for (int i = 0; i < to_do.size(); i++)
/*     */             {
/*     */               try
/*     */               {
/* 115 */                 DataSourceCallBackUtil.addDataSourceCallback this_callback = (DataSourceCallBackUtil.addDataSourceCallback)to_do.get(i);
/*     */                 
/* 117 */                 if (TableViewImpl.DEBUGADDREMOVE) {
/* 118 */                   this_callback.debug("processDataSourceQueue after " + (SystemTime.getCurrentTime() - event.getCreatedTime()) + "ms");
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/* 123 */                 this_callback.process();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 127 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 134 */       if (to_do_now != null)
/*     */       {
/*     */ 
/*     */ 
/* 138 */         to_do_now.remove(callback);
/*     */         
/* 140 */         for (int i = 0; i < to_do_now.size(); i++)
/*     */         {
/*     */           try
/*     */           {
/* 144 */             addDataSourceCallback this_callback = (addDataSourceCallback)to_do_now.get(i);
/*     */             
/* 146 */             if (TableViewImpl.DEBUGADDREMOVE)
/*     */             {
/* 148 */               this_callback.debug("Over immediate delay limit, processing queue now");
/*     */             }
/*     */             
/* 151 */             this_callback.process();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 155 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 161 */     return processQueueImmediately;
/*     */   }
/*     */   
/*     */   public static abstract interface addDataSourceCallback
/*     */   {
/*     */     public abstract void process();
/*     */     
/*     */     public abstract void debug(String paramString);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/DataSourceCallBackUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */