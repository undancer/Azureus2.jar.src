/*    */ package com.aelitis.azureus.core.diskmanager.file;
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
/*    */ public class FMFileManagerException
/*    */   extends Exception
/*    */ {
/* 34 */   private boolean recoverable = true;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public FMFileManagerException(String str)
/*    */   {
/* 41 */     super(str);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public FMFileManagerException(String str, Throwable cause)
/*    */   {
/* 49 */     super(str, cause);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setRecoverable(boolean _recoverable)
/*    */   {
/* 56 */     this.recoverable = _recoverable;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isRecoverable()
/*    */   {
/* 62 */     return this.recoverable;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/diskmanager/file/FMFileManagerException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */