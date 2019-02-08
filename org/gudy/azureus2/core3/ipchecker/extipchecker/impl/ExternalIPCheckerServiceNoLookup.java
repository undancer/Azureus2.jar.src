/*    */ package org.gudy.azureus2.core3.ipchecker.extipchecker.impl;
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
/*    */ public class ExternalIPCheckerServiceNoLookup
/*    */   extends ExternalIPCheckerServiceImpl
/*    */ {
/*    */   protected ExternalIPCheckerServiceNoLookup(String key)
/*    */   {
/* 37 */     super(key);
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean supportsCheck()
/*    */   {
/* 43 */     return false;
/*    */   }
/*    */   
/*    */   protected void initiateCheckSupport() {}
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/impl/ExternalIPCheckerServiceNoLookup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */