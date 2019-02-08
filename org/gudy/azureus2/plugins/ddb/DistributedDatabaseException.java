/*    */ package org.gudy.azureus2.plugins.ddb;
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
/*    */ public class DistributedDatabaseException
/*    */   extends Exception
/*    */ {
/*    */   public DistributedDatabaseException(String str)
/*    */   {
/* 35 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DistributedDatabaseException(String str, Throwable cause)
/*    */   {
/* 43 */     super(str, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */