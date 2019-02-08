/*    */ package org.gudy.azureus2.core3.ipchecker.extipchecker;
/*    */ 
/*    */ import org.gudy.azureus2.core3.ipchecker.extipchecker.impl.ExternalIPCheckerImpl;
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
/*    */ public class ExternalIPCheckerFactory
/*    */ {
/*    */   public static ExternalIPChecker create()
/*    */   {
/* 38 */     return new ExternalIPCheckerImpl();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipchecker/extipchecker/ExternalIPCheckerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */