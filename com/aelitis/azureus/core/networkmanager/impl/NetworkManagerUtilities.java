/*    */ package com.aelitis.azureus.core.networkmanager.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
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
/*    */ public class NetworkManagerUtilities
/*    */ {
/*    */   public static int getGroupRateLimit(LimitedRateGroup group)
/*    */   {
/* 36 */     int limit = group.getRateLimitBytesPerSecond();
/* 37 */     if (limit == 0) {
/* 38 */       limit = 104857600;
/*    */     }
/* 40 */     else if (limit < 0) {
/* 41 */       limit = 0;
/*    */     }
/* 43 */     return limit;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/NetworkManagerUtilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */