/*     */ package org.gudy.azureus2.ui.swt.progress;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class ProgressReportingManager
/*     */   implements IProgressReportConstants
/*     */ {
/*  41 */   private static ProgressReportingManager INSTANCE = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  46 */   private ProgressReporterStack progressReporters = new ProgressReporterStack();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  53 */   private int reporterCounter = Integer.MIN_VALUE;
/*     */   
/*     */ 
/*     */   public static final int COUNT_ALL = 0;
/*     */   
/*     */ 
/*     */   public static final int COUNT_ACTIVE = 1;
/*     */   
/*     */ 
/*     */   public static final int COUNT_ERROR = 2;
/*     */   
/*  64 */   private CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  69 */   private boolean isAutoRemove = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ProgressReportingManager()
/*     */   {
/*  78 */     this.isAutoRemove = COConfigurationManager.getBooleanParameter("auto_remove_inactive_items");
/*  79 */     COConfigurationManager.addParameterListener("auto_remove_inactive_items", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*  82 */         ProgressReportingManager.this.isAutoRemove = COConfigurationManager.getBooleanParameter("auto_remove_inactive_items");
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static final synchronized ProgressReportingManager getInstance() {
/*  88 */     if (null == INSTANCE) {
/*  89 */       INSTANCE = new ProgressReportingManager();
/*     */     }
/*  91 */     return INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */   public IProgressReporter addReporter()
/*     */   {
/*  97 */     return new ProgressReporter(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IProgressReporter addReporter(String name)
/*     */   {
/* 104 */     return new ProgressReporter(this, name);
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
/*     */   public int getReporterCount(int whatToCount)
/*     */   {
/* 118 */     if (whatToCount == 2) {
/* 119 */       return this.progressReporters.getErrorCount();
/*     */     }
/* 121 */     if (whatToCount == 1) {
/* 122 */       return this.progressReporters.getActiveCount();
/*     */     }
/*     */     
/* 125 */     return this.progressReporters.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasMultipleActive()
/*     */   {
/* 136 */     return this.progressReporters.hasMultipleActive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IProgressReporter getNextActiveReporter()
/*     */   {
/* 144 */     return this.progressReporters.getNextActiveReporter();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IProgressReporter getCurrentReporter()
/*     */   {
/* 152 */     return this.progressReporters.peek();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List getReporters(boolean onlyActive)
/*     */   {
/* 163 */     List reporters = this.progressReporters.getReporters(onlyActive);
/* 164 */     Collections.sort(reporters);
/* 165 */     return reporters;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IProgressReporter[] getReportersArray(boolean onlyActive)
/*     */   {
/* 176 */     List rpList = this.progressReporters.getReporters(onlyActive);
/* 177 */     IProgressReporter[] array = (IProgressReporter[])rpList.toArray(new IProgressReporter[rpList.size()]);
/* 178 */     Arrays.sort(array);
/* 179 */     return array;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean remove(IProgressReporter reporter)
/*     */   {
/* 189 */     boolean value = this.progressReporters.remove(reporter);
/* 190 */     notifyListeners(2, reporter);
/* 191 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(IProgressReportingListener listener)
/*     */   {
/* 199 */     if ((null != listener) && (!this.listeners.contains(listener))) {
/* 200 */       this.listeners.add(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(IProgressReportingListener listener)
/*     */   {
/* 209 */     if ((null != listener) && (this.listeners.contains(listener))) {
/* 210 */       this.listeners.remove(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void notifyListeners(int eventType, IProgressReporter reporter)
/*     */   {
/* 220 */     for (Iterator iterator = this.listeners.iterator(); iterator.hasNext();) {
/* 221 */       IProgressReportingListener listener = (IProgressReportingListener)iterator.next();
/* 222 */       if (null != listener) {
/*     */         try {
/* 224 */           listener.reporting(eventType, reporter);
/*     */         } catch (Throwable e) {
/* 226 */           Debug.out(e);
/*     */         }
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
/*     */   protected void notifyManager(IProgressReporter reporter)
/*     */   {
/* 242 */     IProgressReport pReport = reporter.getProgressReport();
/* 243 */     if (((this.isAutoRemove) && (!pReport.isActive())) || (pReport.isDisposed()))
/*     */     {
/* 245 */       this.progressReporters.remove(reporter);
/* 246 */       notifyListeners(2, reporter);
/* 247 */     } else if (this.progressReporters.contains(reporter)) {
/* 248 */       this.progressReporters.push(reporter);
/* 249 */       notifyListeners(3, reporter);
/*     */     } else {
/* 251 */       this.progressReporters.push(reporter);
/* 252 */       notifyListeners(1, reporter);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final synchronized int getNextAvailableID()
/*     */   {
/* 274 */     return this.reporterCounter++;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressReportingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */