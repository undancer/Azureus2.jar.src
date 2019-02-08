/*    */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*    */ 
/*    */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*    */ import com.aelitis.azureus.core.speedmanager.SpeedManagerListener;
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
/*    */ public class SpeedLimitListener
/*    */   implements SpeedManagerListener
/*    */ {
/*    */   final SpeedLimitMonitor mon;
/*    */   
/*    */   public SpeedLimitListener(SpeedLimitMonitor limitMonitor)
/*    */   {
/* 30 */     this.mon = limitMonitor;
/*    */   }
/*    */   
/*    */ 
/*    */   public void propertyChanged(int property)
/*    */   {
/* 36 */     String type = "unknown";
/* 37 */     if (property == 1) {
/* 38 */       type = "ASN change";
/* 39 */       this.mon.readFromPersistentMap();
/* 40 */       this.mon.updateFromCOConfigManager();
/* 41 */       SMSearchLogger.log("ASN change.");
/* 42 */     } else if (property == 3) {
/* 43 */       type = "download capacity";
/* 44 */       SpeedManagerLimitEstimate pmEst = PingSpaceMon.getDownloadLimit();
/* 45 */       SpeedManagerLimitEstimate smEst = PingSpaceMon.getDownloadEstCapacity();
/*    */       
/* 47 */       SMSearchLogger.log(" download - persistent limit: " + pmEst.getString());
/* 48 */       SMSearchLogger.log(" download - estimated capacity: " + smEst.getString());
/*    */       
/* 50 */       this.mon.notifyDownload(smEst);
/* 51 */     } else if (property == 2) {
/* 52 */       type = "upload capacity";
/* 53 */       SpeedManagerLimitEstimate shortTermLimit = PingSpaceMon.getUploadLimit(false);
/* 54 */       SpeedManagerLimitEstimate pmEst = PingSpaceMon.getUploadLimit(true);
/* 55 */       SpeedManagerLimitEstimate smEst = PingSpaceMon.getUploadEstCapacity();
/*    */       
/* 57 */       SMSearchLogger.log(" upload - short term limit: " + shortTermLimit.getString());
/* 58 */       SMSearchLogger.log(" upload - persistent limit: " + pmEst.getString());
/* 59 */       SMSearchLogger.log(" upload - estimated capacity: " + smEst.getString());
/*    */       
/* 61 */       this.mon.notifyUpload(smEst);
/*    */     }
/*    */     
/* 64 */     SpeedManagerLogger.log("Updated from SpeedManagerPingMapper property=" + type);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedLimitListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */