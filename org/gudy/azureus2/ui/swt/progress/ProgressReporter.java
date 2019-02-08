/*     */ package org.gudy.azureus2.ui.swt.progress;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ public class ProgressReporter
/*     */   implements IProgressReporter, IProgressReportConstants
/*     */ {
/*  96 */   private ProgressReportingManager manager = null;
/*     */   
/*     */   private transient int ID;
/*     */   
/*     */   private int minimum;
/*     */   
/*     */   private int maximum;
/*     */   
/*     */   private int selection;
/*     */   
/*     */   private int percentage;
/* 107 */   private int latestReportType = 0;
/*     */   private boolean isIndeterminate;
/*     */   private boolean isDone;
/*     */   private boolean isPercentageInUse;
/*     */   private boolean isCancelAllowed;
/* 112 */   private boolean isCanceled; private boolean isRetryAllowed; private boolean isInErrorState; private boolean isDisposed; private String title = "";
/*     */   
/* 114 */   private String message = "";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */   private CopyOnWriteList messageHistory = new CopyOnWriteList();
/*     */   
/* 124 */   private String detailMessage = "";
/*     */   
/* 126 */   private String errorMessage = "";
/*     */   
/* 128 */   private String name = "";
/*     */   
/* 130 */   private Image image = null;
/*     */   
/* 132 */   private String reporterType = "default.reporter.type";
/*     */   
/* 134 */   private IProgressReport latestProgressReport = null;
/*     */   
/* 136 */   private CopyOnWriteList reporterListeners = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */   private Object objectData = null;
/*     */   
/* 144 */   private int messageHistoryLimit = 1000;
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
/* 155 */   private boolean isActive = true;
/*     */   
/* 157 */   private boolean cancelCloses = false;
/*     */   
/*     */ 
/*     */ 
/*     */   protected ProgressReporter(ProgressReportingManager manager)
/*     */   {
/* 163 */     this(manager, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ProgressReporter(ProgressReportingManager _manager, String name)
/*     */   {
/* 172 */     this.manager = _manager;
/* 173 */     this.name = name;
/* 174 */     this.ID = this.manager.getNextAvailableID();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setReporterType(String reporterType)
/*     */   {
/* 181 */     this.reporterType = reporterType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 189 */     synchronized (this)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 194 */       if (this.isDisposed) {
/* 195 */         return;
/*     */       }
/*     */       
/* 198 */       this.isDisposed = true;
/* 199 */       this.isActive = false;
/*     */     }
/*     */     
/* 202 */     this.latestReportType = 7;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 207 */     notifyListeners(getProgressReport());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */     if (null != this.reporterListeners) {
/* 215 */       this.reporterListeners.clear();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 221 */     this.manager.notifyManager(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void reInit()
/*     */   {
/* 230 */     this.isActive = true;
/* 231 */     this.isCanceled = false;
/* 232 */     this.isDone = false;
/* 233 */     this.isInErrorState = false;
/* 234 */     this.errorMessage = "";
/* 235 */     this.message = "";
/* 236 */     this.detailMessage = "";
/* 237 */     this.messageHistory.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void notifyListeners(IProgressReport report)
/*     */   {
/* 247 */     if ((null == this.reporterListeners) || (this.reporterListeners.size() < 1)) {
/* 248 */       return;
/*     */     }
/*     */     
/* 251 */     List removalList = new ArrayList();
/*     */     
/* 253 */     for (Iterator iterator = this.reporterListeners.iterator(); iterator.hasNext();) {
/* 254 */       IProgressReporterListener listener = (IProgressReporterListener)iterator.next();
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 260 */         if (1 == listener.report(report)) {
/* 261 */           removalList.add(listener);
/*     */         }
/*     */       } catch (Throwable e) {
/* 264 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 271 */     for (Iterator iterator = removalList.iterator(); iterator.hasNext();) {
/* 272 */       this.reporterListeners.remove(iterator.next());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateAndNotify(int eventType)
/*     */   {
/* 281 */     this.latestReportType = eventType;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 286 */     this.latestProgressReport = new ProgressReport(null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 293 */     this.manager.notifyManager(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 299 */     if ((eventType == 6) && (this.isCanceled)) {
/* 300 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 306 */     notifyListeners(this.latestProgressReport);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSelection(int selection, String message)
/*     */   {
/* 313 */     if (shouldIgnore()) {
/* 314 */       return;
/*     */     }
/* 316 */     this.message = message;
/*     */     
/* 318 */     if (selection >= this.maximum) {
/* 319 */       setDone();
/* 320 */       return;
/*     */     }
/* 322 */     if (selection < this.minimum) {
/* 323 */       this.percentage = 0;
/* 324 */       this.selection = this.minimum;
/* 325 */       this.isIndeterminate = true;
/* 326 */       return;
/*     */     }
/* 328 */     this.selection = selection;
/* 329 */     this.percentage = (selection * 100 / (this.maximum - this.minimum));
/* 330 */     this.isDone = false;
/* 331 */     this.isPercentageInUse = false;
/* 332 */     this.isIndeterminate = false;
/*     */     
/* 334 */     if ((null != message) && (message.length() > 0)) {
/* 335 */       addToMessageHistory(message, 2);
/*     */     }
/*     */     
/* 338 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPercentage(int percentage, String message)
/*     */   {
/* 345 */     if (shouldIgnore()) {
/* 346 */       return;
/*     */     }
/*     */     
/* 349 */     this.message = message;
/*     */     
/* 351 */     if (percentage >= 100) {
/* 352 */       setDone();
/* 353 */       return;
/*     */     }
/*     */     
/* 356 */     if (percentage < 0) {
/* 357 */       percentage = 0;
/* 358 */       this.selection = this.minimum;
/* 359 */       this.isIndeterminate = true;
/* 360 */       return;
/*     */     }
/* 362 */     this.minimum = 0;
/* 363 */     this.maximum = 100;
/* 364 */     this.percentage = percentage;
/* 365 */     this.selection = percentage;
/* 366 */     this.isDone = false;
/* 367 */     this.isPercentageInUse = true;
/* 368 */     this.isIndeterminate = false;
/*     */     
/* 370 */     if ((null != message) && (message.length() > 0)) {
/* 371 */       addToMessageHistory(message, 2);
/*     */     }
/*     */     
/* 374 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setIndeterminate(boolean isIndeterminate)
/*     */   {
/* 381 */     if (shouldIgnore()) {
/* 382 */       return;
/*     */     }
/*     */     
/* 385 */     this.isIndeterminate = isIndeterminate;
/* 386 */     if (isIndeterminate) {
/* 387 */       this.minimum = 0;
/* 388 */       this.maximum = 0;
/*     */     }
/* 390 */     updateAndNotify(3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDone()
/*     */   {
/* 397 */     synchronized (this) {
/* 398 */       if (shouldIgnore()) {
/* 399 */         return;
/*     */       }
/*     */       
/* 402 */       this.isDone = true;
/* 403 */       this.isActive = false;
/*     */     }
/*     */     
/* 406 */     this.selection = this.maximum;
/* 407 */     this.percentage = 100;
/* 408 */     this.isIndeterminate = false;
/* 409 */     this.message = MessageText.getString("Progress.reporting.status.finished");
/* 410 */     addToMessageHistory(this.message, 8);
/* 411 */     updateAndNotify(2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMessage(String message)
/*     */   {
/* 418 */     if (shouldIgnore()) {
/* 419 */       return;
/*     */     }
/* 421 */     this.message = message;
/* 422 */     addToMessageHistory(message, 2);
/* 423 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void appendDetailMessage(String detailMessage)
/*     */   {
/* 430 */     if (shouldIgnore()) {
/* 431 */       return;
/*     */     }
/* 433 */     this.detailMessage = detailMessage;
/*     */     
/* 435 */     addToMessageHistory(detailMessage, 8);
/*     */     
/* 437 */     updateAndNotify(6);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 445 */     this.detailMessage = "";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMinimum(int min)
/*     */   {
/* 452 */     if (shouldIgnore()) {
/* 453 */       return;
/*     */     }
/* 455 */     this.minimum = min;
/* 456 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaximum(int max)
/*     */   {
/* 463 */     if (shouldIgnore()) {
/* 464 */       return;
/*     */     }
/* 466 */     this.maximum = max;
/* 467 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 474 */     synchronized (this) {
/* 475 */       if ((this.isCanceled) || (shouldIgnore())) {
/* 476 */         return;
/*     */       }
/*     */       
/* 479 */       this.isCanceled = true;
/* 480 */       this.isActive = false;
/*     */     }
/* 482 */     this.message = MessageText.getString("Progress.reporting.status.canceled");
/* 483 */     addToMessageHistory(this.message, 8);
/* 484 */     updateAndNotify(1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void retry()
/*     */   {
/* 491 */     synchronized (this) {
/* 492 */       if (shouldIgnore()) {
/* 493 */         return;
/*     */       }
/* 495 */       reInit();
/*     */     }
/* 497 */     this.message = MessageText.getString("Progress.reporting.status.retrying");
/* 498 */     addToMessageHistory(this.message, 8);
/* 499 */     updateAndNotify(5);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCancelAllowed(boolean cancelAllowed)
/*     */   {
/* 506 */     if (shouldIgnore()) {
/* 507 */       return;
/*     */     }
/*     */     
/* 510 */     this.isCancelAllowed = cancelAllowed;
/* 511 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 518 */     if (shouldIgnore()) {
/* 519 */       return;
/*     */     }
/* 521 */     this.name = (name + "");
/* 522 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTitle(String title)
/*     */   {
/* 529 */     if (shouldIgnore()) {
/* 530 */       return;
/*     */     }
/* 532 */     this.title = title;
/* 533 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImage(Image image)
/*     */   {
/* 540 */     if (shouldIgnore()) {
/* 541 */       return;
/*     */     }
/* 543 */     this.image = image;
/* 544 */     updateAndNotify(6);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setErrorMessage(String errorMessage)
/*     */   {
/* 551 */     if (shouldIgnore()) {
/* 552 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 561 */     if ((null != this.errorMessage) && (this.errorMessage.equals(errorMessage)))
/*     */     {
/* 563 */       return;
/*     */     }
/*     */     
/* 566 */     if ((null == errorMessage) || (errorMessage.length() < 1)) {
/* 567 */       this.errorMessage = MessageText.getString("Progress.reporting.default.error");
/*     */     } else {
/* 569 */       this.errorMessage = errorMessage;
/*     */     }
/* 571 */     this.isInErrorState = true;
/* 572 */     this.isActive = false;
/* 573 */     addToMessageHistory(this.errorMessage, 4);
/* 574 */     updateAndNotify(4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setRetryAllowed(boolean retryOnError)
/*     */   {
/* 581 */     if (shouldIgnore()) {
/* 582 */       return;
/*     */     }
/* 584 */     this.isRetryAllowed = retryOnError;
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
/*     */   private boolean shouldIgnore()
/*     */   {
/* 604 */     return (this.isDisposed) || (this.isDone);
/*     */   }
/*     */   
/*     */   public boolean getCancelCloses() {
/* 608 */     return this.cancelCloses;
/*     */   }
/*     */   
/* 611 */   public void setCancelCloses(boolean b) { this.cancelCloses = b; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void addToMessageHistory(String value, int type)
/*     */   {
/* 623 */     if (this.messageHistory.size() >= this.messageHistoryLimit) {
/* 624 */       return;
/*     */     }
/*     */     
/* 627 */     if (this.messageHistory.size() < this.messageHistoryLimit) {
/* 628 */       this.messageHistory.add(new ProgressReportMessage(value, type));
/*     */     }
/*     */     
/* 631 */     if (this.messageHistory.size() == this.messageHistoryLimit) {
/* 632 */       Debug.out(MessageText.getString("Progress.reporting.detail.history.limit", new String[] { this.messageHistoryLimit + "" }));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObjectData(Object objectData)
/*     */   {
/* 643 */     if (shouldIgnore()) {
/* 644 */       return;
/*     */     }
/* 646 */     this.objectData = objectData;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IMessage[] getMessageHistory()
/*     */   {
/* 656 */     List tmp = this.messageHistory.getList();
/* 657 */     return (IMessage[])tmp.toArray(new IMessage[tmp.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(IProgressReporterListener listener)
/*     */   {
/* 664 */     if (shouldIgnore()) {
/* 665 */       return;
/*     */     }
/* 667 */     if (null != listener) {
/* 668 */       if (null == this.reporterListeners) {
/* 669 */         this.reporterListeners = new CopyOnWriteList();
/* 670 */         this.reporterListeners.add(listener);
/* 671 */       } else if (!this.reporterListeners.contains(listener)) {
/* 672 */         this.reporterListeners.add(listener);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(IProgressReporterListener listener)
/*     */   {
/* 681 */     if (null == this.reporterListeners) {
/* 682 */       return;
/*     */     }
/* 684 */     this.reporterListeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int compareTo(Object obj)
/*     */   {
/* 691 */     if ((obj instanceof IProgressReporter))
/*     */     {
/* 693 */       return this.ID == obj.hashCode() ? 0 : this.ID < obj.hashCode() ? -1 : 1;
/*     */     }
/* 695 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 702 */     if ((obj instanceof IProgressReporter))
/*     */     {
/* 704 */       return this.ID == obj.hashCode();
/*     */     }
/* 706 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 713 */     return this.ID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IProgressReport getProgressReport()
/*     */   {
/* 720 */     if (null == this.latestProgressReport) {
/* 721 */       this.latestProgressReport = new ProgressReport(null);
/*     */     }
/* 723 */     return this.latestProgressReport;
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
/*     */ 
/*     */   public class ProgressReport
/*     */     implements IProgressReport
/*     */   {
/* 746 */     private final String reporterType = ProgressReporter.this.reporterType;
/*     */     
/* 748 */     private final int reporterID = ProgressReporter.this.ID;
/*     */     
/* 750 */     private final int minimum = ProgressReporter.this.minimum;
/*     */     
/* 752 */     private final int maximum = ProgressReporter.this.maximum;
/*     */     
/* 754 */     private final int selection = ProgressReporter.this.selection;
/*     */     
/* 756 */     private final int percentage = ProgressReporter.this.percentage;
/*     */     
/* 758 */     private final boolean isActive = ProgressReporter.this.isActive;
/*     */     
/* 760 */     private final boolean isIndeterminate = ProgressReporter.this.isIndeterminate;
/*     */     
/* 762 */     private final boolean isDone = ProgressReporter.this.isDone;
/*     */     
/* 764 */     private final boolean isPercentageInUse = ProgressReporter.this.isPercentageInUse;
/*     */     
/* 766 */     private final boolean isCancelAllowed = ProgressReporter.this.isCancelAllowed;
/*     */     
/* 768 */     public final boolean isCanceled = ProgressReporter.this.isCanceled;
/*     */     
/* 770 */     private final boolean isRetryAllowed = ProgressReporter.this.isRetryAllowed;
/*     */     
/* 772 */     private final boolean isInErrorState = ProgressReporter.this.isInErrorState;
/*     */     
/* 774 */     private final boolean isDisposed = ProgressReporter.this.isDisposed;
/*     */     
/* 776 */     private final String title = ProgressReporter.this.title;
/*     */     
/* 778 */     private final String message = ProgressReporter.this.message;
/*     */     
/* 780 */     private final String detailMessage = ProgressReporter.this.detailMessage;
/*     */     
/* 782 */     private final String errorMessage = ProgressReporter.this.errorMessage;
/*     */     
/* 784 */     private final String name = ProgressReporter.this.name;
/*     */     
/* 786 */     private final Image image = ProgressReporter.this.image;
/*     */     
/* 788 */     private final Object objectData = ProgressReporter.this.objectData;
/*     */     
/* 790 */     private final int REPORT_TYPE = ProgressReporter.this.latestReportType;
/*     */     
/*     */ 
/*     */ 
/*     */     private ProgressReport() {}
/*     */     
/*     */ 
/*     */     public IProgressReporter getReporter()
/*     */     {
/* 799 */       return ProgressReporter.this;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getReporterType()
/*     */     {
/* 805 */       return this.reporterType;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getReporterID()
/*     */     {
/* 812 */       return this.reporterID;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getMinimum()
/*     */     {
/* 819 */       return this.minimum;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getMaximum()
/*     */     {
/* 826 */       return this.maximum;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getSelection()
/*     */     {
/* 833 */       return this.selection;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getPercentage()
/*     */     {
/* 840 */       return this.percentage;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isActive()
/*     */     {
/* 847 */       return this.isActive;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isIndeterminate()
/*     */     {
/* 854 */       return this.isIndeterminate;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isDone()
/*     */     {
/* 861 */       return this.isDone;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isPercentageInUse()
/*     */     {
/* 868 */       return this.isPercentageInUse;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isCancelAllowed()
/*     */     {
/* 875 */       return this.isCancelAllowed;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isCanceled()
/*     */     {
/* 882 */       return this.isCanceled;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isRetryAllowed()
/*     */     {
/* 889 */       return this.isRetryAllowed;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isInErrorState()
/*     */     {
/* 896 */       return this.isInErrorState;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isDisposed()
/*     */     {
/* 903 */       return this.isDisposed;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getTitle()
/*     */     {
/* 910 */       return this.title;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getMessage()
/*     */     {
/* 917 */       return this.message;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getDetailMessage()
/*     */     {
/* 924 */       return this.detailMessage;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getErrorMessage()
/*     */     {
/* 931 */       return this.errorMessage;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getName()
/*     */     {
/* 938 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Image getImage()
/*     */     {
/* 945 */       return this.image;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Object getObjectData()
/*     */     {
/* 952 */       return this.objectData;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int getReportType()
/*     */     {
/* 959 */       return this.REPORT_TYPE;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */