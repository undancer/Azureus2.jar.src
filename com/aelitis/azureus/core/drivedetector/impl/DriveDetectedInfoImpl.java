/*    */ package com.aelitis.azureus.core.drivedetector.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.drivedetector.DriveDetectedInfo;
/*    */ import java.io.File;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
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
/*    */ public class DriveDetectedInfoImpl
/*    */   implements DriveDetectedInfo
/*    */ {
/*    */   final File location;
/*    */   private final Map info;
/*    */   
/*    */   public DriveDetectedInfoImpl(File location, Map info)
/*    */   {
/* 37 */     this.location = location;
/* 38 */     this.info = info;
/*    */   }
/*    */   
/*    */   public File getLocation() {
/* 42 */     return this.location;
/*    */   }
/*    */   
/*    */   public Object getInfo(String key) {
/* 46 */     return this.info.get(key);
/*    */   }
/*    */   
/*    */   public Map<String, Object> getInfoMap() {
/* 50 */     return Collections.unmodifiableMap(this.info);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/drivedetector/impl/DriveDetectedInfoImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */