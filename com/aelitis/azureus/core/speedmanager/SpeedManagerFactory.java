/*    */ package com.aelitis.azureus.core.speedmanager;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerImpl;
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
/*    */ public class SpeedManagerFactory
/*    */ {
/*    */   public static SpeedManager createSpeedManager(AzureusCore core, SpeedManagerAdapter adapter)
/*    */   {
/* 33 */     return new SpeedManagerImpl(core, adapter);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */