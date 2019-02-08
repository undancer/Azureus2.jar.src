/*    */ package org.gudy.azureus2.core3.global;
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
/*    */ public class GlobalManagerDownloadRemovalVetoException
/*    */   extends Exception
/*    */ {
/*    */   private final boolean silent;
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
/*    */   public GlobalManagerDownloadRemovalVetoException(String str, boolean silent)
/*    */   {
/* 41 */     super(str);
/* 42 */     this.silent = silent;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public GlobalManagerDownloadRemovalVetoException(String str)
/*    */   {
/* 49 */     this(str, false);
/*    */   }
/*    */   
/*    */   public boolean isSilent() {
/* 53 */     return this.silent;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/global/GlobalManagerDownloadRemovalVetoException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */