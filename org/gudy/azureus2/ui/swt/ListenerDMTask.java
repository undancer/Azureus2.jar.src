/*    */ package org.gudy.azureus2.ui.swt;
/*    */ 
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.util.AEThread2;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class ListenerDMTask
/*    */   implements Listener
/*    */ {
/*    */   private DownloadManager[] dms;
/*    */   private boolean ascending;
/*    */   private boolean async;
/*    */   
/*    */   public ListenerDMTask(DownloadManager[] dms)
/*    */   {
/* 42 */     this(dms, true);
/*    */   }
/*    */   
/*    */   public ListenerDMTask(DownloadManager[] dms, boolean ascending) {
/* 46 */     this.dms = dms;
/* 47 */     this.ascending = ascending;
/*    */   }
/*    */   
/*    */   public ListenerDMTask(DownloadManager[] dms, boolean ascending, boolean async) {
/* 51 */     this.dms = dms;
/* 52 */     this.ascending = ascending;
/* 53 */     this.async = async;
/*    */   }
/*    */   
/*    */ 
/*    */   public void run(DownloadManager dm) {}
/*    */   
/*    */ 
/*    */   public void run(DownloadManager[] dm) {}
/*    */   
/*    */   public void handleEvent(Event event)
/*    */   {
/* 64 */     if (this.async)
/*    */     {
/* 66 */       new AEThread2("DMTask:async", true) {
/*    */         public void run() {
/* 68 */           ListenerDMTask.this.go();
/*    */         }
/*    */         
/*    */       }.start();
/*    */     } else {
/* 73 */       go();
/*    */     }
/*    */   }
/*    */   
/*    */   public void go() {
/*    */     try {
/* 79 */       DownloadManager dm = null;
/* 80 */       for (int i = 0; i < this.dms.length; i++) {
/* 81 */         dm = this.dms[(this.dms.length - 1 - i)];
/* 82 */         if (dm != null)
/*    */         {
/*    */ 
/* 85 */           run(dm); }
/*    */       }
/* 87 */       run(this.dms);
/*    */     } catch (Exception e) {
/* 89 */       Debug.printStackTrace(e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ListenerDMTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */