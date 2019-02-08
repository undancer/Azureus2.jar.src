/*    */ package com.aelitis.azureus.core.drivedetector;
/*    */ 
/*    */ import com.aelitis.azureus.core.drivedetector.impl.DriveDetectorImpl;
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
/*    */ public class DriveDetectorFactory
/*    */ {
/*    */   private static DriveDetector dd;
/*    */   
/*    */   public static DriveDetector getDeviceDetector()
/*    */   {
/* 32 */     if (dd == null) {
/* 33 */       dd = new DriveDetectorImpl();
/*    */     }
/* 35 */     return dd;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/drivedetector/DriveDetectorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */