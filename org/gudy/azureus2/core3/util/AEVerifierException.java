/*    */ package org.gudy.azureus2.core3.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AEVerifierException
/*    */   extends Exception
/*    */ {
/*    */   public static final int FT_SIGNATURE_MISSING = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int FT_SIGNATURE_BAD = 2;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private final int failure_type;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AEVerifierException(int type, String str)
/*    */   {
/* 37 */     super(str);
/*    */     
/* 39 */     this.failure_type = type;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getFailureType()
/*    */   {
/* 45 */     return this.failure_type;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEVerifierException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */