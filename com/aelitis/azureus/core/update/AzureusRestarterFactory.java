/*    */ package com.aelitis.azureus.core.update;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import com.aelitis.azureus.core.update.impl.AzureusRestarterImpl;
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
/*    */ public class AzureusRestarterFactory
/*    */ {
/*    */   public static AzureusRestarter create(AzureusCore azureus_core)
/*    */   {
/* 37 */     return new AzureusRestarterImpl(azureus_core);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/update/AzureusRestarterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */