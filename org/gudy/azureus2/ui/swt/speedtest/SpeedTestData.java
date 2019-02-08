/*    */ package org.gudy.azureus2.ui.swt.speedtest;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminSpeedTesterResult;
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
/*    */ public class SpeedTestData
/*    */ {
/* 28 */   private static SpeedTestData ourInstance = new SpeedTestData();
/*    */   
/*    */   private String lastTestData;
/*    */   
/*    */   private NetworkAdminSpeedTesterResult lastResult;
/*    */   
/*    */   private int highestDownloadOnlyResult;
/*    */   private int lastUploadOnlyResult;
/*    */   
/*    */   public static SpeedTestData getInstance()
/*    */   {
/* 39 */     return ourInstance;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setLastTestData(String text)
/*    */   {
/* 46 */     this.lastTestData = text;
/*    */   }
/*    */   
/*    */   public String getLastTestData() {
/* 50 */     return this.lastTestData;
/*    */   }
/*    */   
/*    */   public void setResult(NetworkAdminSpeedTesterResult result) {
/* 54 */     this.lastResult = result;
/*    */   }
/*    */   
/*    */   public NetworkAdminSpeedTesterResult getLastResult() {
/* 58 */     return this.lastResult;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setHighestDownloadResult(int currDownRateInKBytePerSec)
/*    */   {
/* 70 */     if (this.highestDownloadOnlyResult < currDownRateInKBytePerSec) {
/* 71 */       this.highestDownloadOnlyResult = currDownRateInKBytePerSec;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getHightestDownloadResult()
/*    */   {
/* 80 */     return this.highestDownloadOnlyResult;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setLastUploadOnlyResult(int currUpRateInKBytesPerSec)
/*    */   {
/* 90 */     if (currUpRateInKBytesPerSec < 20) {
/* 91 */       currUpRateInKBytesPerSec = 20;
/*    */     }
/*    */     
/* 94 */     this.lastUploadOnlyResult = currUpRateInKBytesPerSec;
/*    */   }
/*    */   
/*    */   public int getLastUploadOnlyResult()
/*    */   {
/* 99 */     return this.lastUploadOnlyResult;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/speedtest/SpeedTestData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */