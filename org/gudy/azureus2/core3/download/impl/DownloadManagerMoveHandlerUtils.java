/*    */ package org.gudy.azureus2.core3.download.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.logging.LogEvent;
/*    */ import org.gudy.azureus2.core3.logging.LogIDs;
/*    */ import org.gudy.azureus2.core3.logging.LogRelation;
/*    */ import org.gudy.azureus2.core3.logging.Logger;
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
/*    */ public class DownloadManagerMoveHandlerUtils
/*    */ {
/*    */   static void logInfo(String message, DownloadManager dm)
/*    */   {
/* 34 */     LogRelation lr = (dm instanceof LogRelation) ? (LogRelation)dm : null;
/* 35 */     if (lr == null) return;
/* 36 */     if (!Logger.isEnabled()) return;
/* 37 */     Logger.log(new LogEvent(lr, LogIDs.CORE, 0, message));
/*    */   }
/*    */   
/*    */   static void logWarn(String message, DownloadManager dm) {
/* 41 */     LogRelation lr = (dm instanceof LogRelation) ? (LogRelation)dm : null;
/* 42 */     if (lr == null) return;
/* 43 */     if (!Logger.isEnabled()) return;
/* 44 */     Logger.log(new LogEvent(lr, LogIDs.CORE, 1, message));
/*    */   }
/*    */   
/*    */   static void logError(String message, DownloadManager dm, Throwable e) {
/* 48 */     LogRelation lr = (dm instanceof LogRelation) ? (LogRelation)dm : null;
/* 49 */     if (lr == null) return;
/* 50 */     if (!Logger.isEnabled()) return;
/* 51 */     Logger.log(new LogEvent(lr, LogIDs.CORE, message, e));
/*    */   }
/*    */   
/*    */   static String describe(DownloadManager dm) {
/* 55 */     if (dm == null) return "";
/* 56 */     return "\"" + dm.getDisplayName() + "\"";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/download/impl/DownloadManagerMoveHandlerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */