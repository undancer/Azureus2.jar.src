/*    */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*    */ 
/*    */ import com.aelitis.azureus.core.speedmanager.SpeedManagerLimitEstimate;
/*    */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
/*    */ import org.gudy.azureus2.core3.logging.LogEvent;
/*    */ import org.gudy.azureus2.core3.logging.LogIDs;
/*    */ import org.gudy.azureus2.core3.logging.Logger;
/*    */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*    */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
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
/*    */ public class SMSearchLogger
/*    */ {
/* 35 */   private static final LogIDs ID = LogIDs.NWMAN;
/* 36 */   private static final AEDiagnosticsLogger dLog = AEDiagnostics.getLogger("AutoSpeedSearchHistory");
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void log(String str)
/*    */   {
/* 43 */     SpeedManagerAlgorithmProviderAdapter adpter = SMInstance.getInstance().getAdapter();
/* 44 */     int adptCurrUpLimit = adpter.getCurrentUploadLimit();
/* 45 */     int adptCurrDownLimit = adpter.getCurrentDownloadLimit();
/*    */     
/*    */ 
/* 48 */     SMConfigurationAdapter conf = SMInstance.getInstance().getConfigManager();
/* 49 */     SpeedManagerLimitEstimate uploadSetting = conf.getUploadLimit();
/* 50 */     SpeedManagerLimitEstimate downloadSetting = conf.getDownloadLimit();
/*    */     
/*    */ 
/* 53 */     StringBuilder sb = new StringBuilder(str);
/* 54 */     sb.append(", Download current =").append(adptCurrDownLimit);
/* 55 */     sb.append(", max limit =").append(downloadSetting.getString());
/*    */     
/* 57 */     sb.append(", Upload current = ").append(adptCurrUpLimit);
/* 58 */     sb.append(", max limit = ").append(uploadSetting.getString());
/*    */     
/* 60 */     String msg = sb.toString();
/*    */     
/* 62 */     LogEvent e = new LogEvent(ID, msg);
/* 63 */     Logger.log(e);
/*    */     
/* 65 */     if (dLog != null) {
/* 66 */       dLog.log(msg);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMSearchLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */