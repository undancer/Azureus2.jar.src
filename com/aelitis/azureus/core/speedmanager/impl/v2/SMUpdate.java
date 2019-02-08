/*    */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SMUpdate
/*    */ {
/*    */   public final int newUploadLimit;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int newDownloadLimit;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public final boolean hasNewUploadLimit;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public final boolean hasNewDownloadLimit;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SMUpdate(int upLimit, boolean newUpLimit, int downLimit, boolean newDownLimit)
/*    */   {
/* 33 */     this.newUploadLimit = upLimit;
/* 34 */     this.newDownloadLimit = downLimit;
/*    */     
/* 36 */     this.hasNewUploadLimit = newUpLimit;
/* 37 */     this.hasNewDownloadLimit = newDownLimit;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SMUpdate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */