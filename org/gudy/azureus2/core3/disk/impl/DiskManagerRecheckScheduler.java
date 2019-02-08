/*     */ package org.gudy.azureus2.core3.disk.impl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.RealTimeInfo;
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
/*     */ public class DiskManagerRecheckScheduler
/*     */ {
/*     */   private static boolean friendly_hashing;
/*     */   private static boolean smallest_first;
/*     */   
/*     */   static
/*     */   {
/*  40 */     ParameterListener param_listener = new ParameterListener()
/*     */     {
/*     */ 
/*     */       public void parameterChanged(String str)
/*     */       {
/*  45 */         DiskManagerRecheckScheduler.access$002(COConfigurationManager.getBooleanParameter("diskmanager.friendly.hashchecking"));
/*  46 */         DiskManagerRecheckScheduler.access$102(COConfigurationManager.getBooleanParameter("diskmanager.hashchecking.smallestfirst"));
/*     */       }
/*     */       
/*  49 */     };
/*  50 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "diskmanager.friendly.hashchecking", "diskmanager.hashchecking.smallestfirst" }, param_listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  57 */   private final List instances = new ArrayList();
/*  58 */   private final AEMonitor instance_mon = new AEMonitor("DiskManagerRecheckScheduler");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DiskManagerRecheckInstance register(DiskManagerHelper helper, boolean low_priority)
/*     */   {
/*     */     try
/*     */     {
/*  67 */       this.instance_mon.enter();
/*     */       
/*  69 */       DiskManagerRecheckInstance res = new DiskManagerRecheckInstance(this, helper.getTorrent().getSize(), (int)helper.getTorrent().getPieceLength(), low_priority);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */       this.instances.add(res);
/*     */       
/*  78 */       if (smallest_first)
/*     */       {
/*  80 */         Collections.sort(this.instances, new Comparator()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           public int compare(Object o1, Object o2)
/*     */           {
/*     */ 
/*     */ 
/*  89 */             long comp = ((DiskManagerRecheckInstance)o1).getMetric() - ((DiskManagerRecheckInstance)o2).getMetric();
/*     */             
/*  91 */             if (comp < 0L)
/*     */             {
/*  93 */               return -1;
/*     */             }
/*  95 */             if (comp == 0L)
/*     */             {
/*  97 */               return 0;
/*     */             }
/*     */             
/* 100 */             return 1;
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 106 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 110 */       this.instance_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean getPermission(DiskManagerRecheckInstance instance)
/*     */   {
/* 118 */     boolean result = false;
/* 119 */     int delay = 250;
/*     */     try
/*     */     {
/* 122 */       this.instance_mon.enter();
/*     */       
/* 124 */       if (this.instances.get(0) == instance)
/*     */       {
/* 126 */         boolean low_priority = instance.isLowPriority();
/*     */         
/*     */ 
/*     */ 
/* 130 */         if ((low_priority) && (RealTimeInfo.isRealTimeTaskActive()))
/*     */         {
/* 132 */           result = false;
/*     */         }
/*     */         else
/*     */         {
/* 136 */           if (friendly_hashing)
/*     */           {
/* 138 */             delay = 0;
/*     */           }
/* 140 */           else if (!low_priority)
/*     */           {
/* 142 */             delay = 1;
/*     */ 
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/* 149 */             delay = instance.getPieceLength() / 1024 / 10;
/*     */             
/* 151 */             delay = Math.min(delay, 409);
/*     */             
/* 153 */             delay = Math.max(delay, 12);
/*     */           }
/*     */           
/* 156 */           result = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 161 */       this.instance_mon.exit();
/*     */     }
/*     */     
/* 164 */     if (delay > 0) {
/*     */       try
/*     */       {
/* 167 */         Thread.sleep(delay);
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 174 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void unregister(DiskManagerRecheckInstance instance)
/*     */   {
/*     */     try
/*     */     {
/* 182 */       this.instance_mon.enter();
/*     */       
/* 184 */       this.instances.remove(instance);
/*     */     }
/*     */     finally {
/* 187 */       this.instance_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerRecheckScheduler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */