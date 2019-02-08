/*    */ package com.aelitis.azureus.core;
/*    */ 
/*    */ import com.aelitis.azureus.core.impl.AzureusCoreImpl;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AzureusCoreFactory
/*    */ {
/*    */   public static AzureusCore create()
/*    */     throws AzureusCoreException
/*    */   {
/* 43 */     return AzureusCoreImpl.create();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean isCoreAvailable()
/*    */   {
/* 55 */     return AzureusCoreImpl.isCoreAvailable();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean isCoreRunning()
/*    */   {
/* 67 */     return AzureusCoreImpl.isCoreRunning();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static AzureusCore getSingleton()
/*    */     throws AzureusCoreException
/*    */   {
/* 80 */     return AzureusCoreImpl.getSingleton();
/*    */   }
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
/*    */   public static void addCoreRunningListener(AzureusCoreRunningListener l)
/*    */   {
/* 96 */     AzureusCoreImpl.addCoreRunningListener(l);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/AzureusCoreFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */