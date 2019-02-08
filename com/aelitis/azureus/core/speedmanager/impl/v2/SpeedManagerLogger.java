/*    */ package com.aelitis.azureus.core.speedmanager.impl.v2;
/*    */ 
/*    */ import com.aelitis.azureus.core.speedmanager.impl.SpeedManagerAlgorithmProviderAdapter;
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
/*    */ public class SpeedManagerLogger
/*    */ {
/*    */   private static String prefix;
/*    */   private static SpeedManagerAlgorithmProviderAdapter adapter;
/*    */   
/*    */   protected static void setAdapter(String _prefix, SpeedManagerAlgorithmProviderAdapter _adapter)
/*    */   {
/* 36 */     prefix = _prefix;
/* 37 */     adapter = _adapter;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static void log(String str)
/*    */   {
/* 44 */     if (adapter != null)
/*    */     {
/* 46 */       adapter.log(prefix + ": " + str);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void trace(String str) {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/impl/v2/SpeedManagerLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */