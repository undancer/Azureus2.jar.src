/*    */ package com.aelitis.azureus.util;
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
/*    */ public class StringCompareUtils
/*    */ {
/*    */   public static boolean equals(String s0, String s1)
/*    */   {
/* 31 */     boolean s0Null = s0 == null;
/* 32 */     boolean s1Null = s1 == null;
/* 33 */     if ((s0Null) || (s1Null)) {
/* 34 */       return s0Null == s1Null;
/*    */     }
/* 36 */     return s0.equals(s1);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/StringCompareUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */