/*    */ package com.aelitis.azureus.core.proxy;
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
/*    */ public class AEProxyException
/*    */   extends Exception
/*    */ {
/*    */   public AEProxyException(String str)
/*    */   {
/* 36 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public AEProxyException(String str, Throwable e)
/*    */   {
/* 44 */     super(str, e);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/AEProxyException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */