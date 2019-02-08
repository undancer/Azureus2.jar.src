/*    */ package org.gudy.azureus2.core3.disk;
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
/*    */ public class DiskManagerException
/*    */   extends Exception
/*    */ {
/*    */   public DiskManagerException(String message)
/*    */   {
/* 28 */     super(message);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DiskManagerException(Throwable cause)
/*    */   {
/* 35 */     super("", cause);
/*    */   }
/*    */   
/*    */   public DiskManagerException(String message, Throwable cause)
/*    */   {
/* 40 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManagerException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */