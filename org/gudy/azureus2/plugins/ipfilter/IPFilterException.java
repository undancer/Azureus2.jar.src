/*    */ package org.gudy.azureus2.plugins.ipfilter;
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
/*    */ public class IPFilterException
/*    */   extends Exception
/*    */ {
/*    */   public IPFilterException(String str)
/*    */   {
/* 37 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public IPFilterException(String str, Throwable cause)
/*    */   {
/* 45 */     super(str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ipfilter/IPFilterException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */