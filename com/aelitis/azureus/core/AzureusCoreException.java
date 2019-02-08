/*    */ package com.aelitis.azureus.core;
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
/*    */ public class AzureusCoreException
/*    */   extends RuntimeException
/*    */ {
/*    */   public AzureusCoreException(String str)
/*    */   {
/* 36 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public AzureusCoreException(String str, Throwable cause)
/*    */   {
/* 44 */     super(str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/AzureusCoreException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */