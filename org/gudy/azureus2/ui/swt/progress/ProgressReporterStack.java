/*     */ package org.gudy.azureus2.ui.swt.progress;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Stack;
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
/*     */ class ProgressReporterStack
/*     */ {
/*  33 */   private Stack reporterStack = new Stack();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  38 */   private Object lockObject = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void push(IProgressReporter reporter)
/*     */   {
/*  45 */     if (null == reporter) {
/*  46 */       return;
/*     */     }
/*  48 */     synchronized (this.lockObject)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  53 */       if (this.reporterStack.contains(reporter)) {
/*  54 */         this.reporterStack.remove(reporter);
/*     */       }
/*     */       
/*  57 */       this.reporterStack.push(reporter);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IProgressReporter peek()
/*     */   {
/*  66 */     synchronized (this.lockObject) {
/*  67 */       if (!this.reporterStack.isEmpty()) {
/*  68 */         return (IProgressReporter)this.reporterStack.peek();
/*     */       }
/*  70 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean remove(IProgressReporter reporter)
/*     */   {
/*  80 */     synchronized (this.lockObject) {
/*  81 */       if ((null != reporter) && (this.reporterStack.contains(reporter))) {
/*  82 */         return this.reporterStack.remove(reporter);
/*     */       }
/*  84 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean contains(IProgressReporter reporter)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 86	org/gudy/azureus2/ui/swt/progress/ProgressReporterStack:lockObject	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_2
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 87	org/gudy/azureus2/ui/swt/progress/ProgressReporterStack:reporterStack	Ljava/util/Stack;
/*     */     //   11: aload_1
/*     */     //   12: invokevirtual 95	java/util/Stack:contains	(Ljava/lang/Object;)Z
/*     */     //   15: aload_2
/*     */     //   16: monitorexit
/*     */     //   17: ireturn
/*     */     //   18: astore_3
/*     */     //   19: aload_2
/*     */     //   20: monitorexit
/*     */     //   21: aload_3
/*     */     //   22: athrow
/*     */     // Line number table:
/*     */     //   Java source line #94	-> byte code offset #0
/*     */     //   Java source line #95	-> byte code offset #7
/*     */     //   Java source line #96	-> byte code offset #18
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	23	0	this	ProgressReporterStack
/*     */     //   0	23	1	reporter	IProgressReporter
/*     */     //   5	15	2	Ljava/lang/Object;	Object
/*     */     //   18	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	17	18	finally
/*     */     //   18	21	18	finally
/*     */   }
/*     */   
/*     */   public IProgressReporter pop()
/*     */   {
/* 104 */     synchronized (this.lockObject) {
/* 105 */       if (!this.reporterStack.isEmpty()) {
/* 106 */         return (IProgressReporter)this.reporterStack.pop();
/*     */       }
/* 108 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void trim()
/*     */   {
/*     */     Iterator iterator;
/* 116 */     synchronized (this.lockObject) {
/* 117 */       for (iterator = this.reporterStack.iterator(); iterator.hasNext();) {
/* 118 */         IProgressReporter reporter = (IProgressReporter)iterator.next();
/* 119 */         if (!reporter.getProgressReport().isActive()) {
/* 120 */           iterator.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List getReporters(boolean onlyActive)
/*     */   {
/* 132 */     synchronized (this.lockObject) {
/* 133 */       List reporters = new ArrayList();
/* 134 */       for (Iterator iterator = this.reporterStack.iterator(); iterator.hasNext();) {
/* 135 */         IProgressReporter reporter = (IProgressReporter)iterator.next();
/* 136 */         if (onlyActive) {
/* 137 */           if (reporter.getProgressReport().isActive()) {
/* 138 */             reporters.add(reporter);
/*     */           }
/*     */         } else {
/* 141 */           reporters.add(reporter);
/*     */         }
/*     */       }
/* 144 */       return reporters;
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int size()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 86	org/gudy/azureus2/ui/swt/progress/ProgressReporterStack:lockObject	Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 87	org/gudy/azureus2/ui/swt/progress/ProgressReporterStack:reporterStack	Ljava/util/Stack;
/*     */     //   11: invokevirtual 90	java/util/Stack:size	()I
/*     */     //   14: aload_1
/*     */     //   15: monitorexit
/*     */     //   16: ireturn
/*     */     //   17: astore_2
/*     */     //   18: aload_1
/*     */     //   19: monitorexit
/*     */     //   20: aload_2
/*     */     //   21: athrow
/*     */     // Line number table:
/*     */     //   Java source line #150	-> byte code offset #0
/*     */     //   Java source line #151	-> byte code offset #7
/*     */     //   Java source line #152	-> byte code offset #17
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	22	0	this	ProgressReporterStack
/*     */     //   5	14	1	Ljava/lang/Object;	Object
/*     */     //   17	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	16	17	finally
/*     */     //   17	20	17	finally
/*     */   }
/*     */   
/*     */   public int getActiveCount()
/*     */   {
/* 160 */     synchronized (this.lockObject) {
/* 161 */       int activeReporters = 0;
/* 162 */       for (Iterator iterator = this.reporterStack.iterator(); iterator.hasNext();) {
/* 163 */         IProgressReporter reporter = (IProgressReporter)iterator.next();
/* 164 */         if (reporter.getProgressReport().isActive()) {
/* 165 */           activeReporters++;
/*     */         }
/*     */       }
/* 168 */       return activeReporters;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getErrorCount()
/*     */   {
/* 177 */     synchronized (this.lockObject) {
/* 178 */       int reportersInErrorState = 0;
/* 179 */       for (Iterator iterator = this.reporterStack.iterator(); iterator.hasNext();) {
/* 180 */         IProgressReporter reporter = (IProgressReporter)iterator.next();
/* 181 */         if (reporter.getProgressReport().isInErrorState()) {
/* 182 */           reportersInErrorState++;
/*     */         }
/*     */       }
/* 185 */       return reportersInErrorState;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasMultipleActive()
/*     */   {
/*     */     int activeReporters;
/*     */     
/*     */     Iterator iterator;
/*     */     
/* 196 */     synchronized (this.lockObject) {
/* 197 */       activeReporters = 0;
/* 198 */       for (iterator = this.reporterStack.iterator(); iterator.hasNext();) {
/* 199 */         IProgressReporter reporter = (IProgressReporter)iterator.next();
/* 200 */         if (reporter.getProgressReport().isActive()) {
/* 201 */           activeReporters++;
/*     */         }
/* 203 */         if (activeReporters > 1) {
/* 204 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 208 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IProgressReporter getNextActiveReporter()
/*     */   {
/*     */     Iterator iterator;
/*     */     
/* 217 */     synchronized (this.lockObject) {
/* 218 */       for (iterator = this.reporterStack.iterator(); iterator.hasNext();) {
/* 219 */         IProgressReporter reporter = (IProgressReporter)iterator.next();
/* 220 */         if (reporter.getProgressReport().isActive()) {
/* 221 */           return reporter;
/*     */         }
/*     */       }
/*     */     }
/* 225 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressReporterStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */