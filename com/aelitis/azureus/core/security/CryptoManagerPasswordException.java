/*    */ package com.aelitis.azureus.core.security;
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
/*    */ public class CryptoManagerPasswordException
/*    */   extends CryptoManagerException
/*    */ {
/*    */   private final boolean incorrect;
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
/*    */   public CryptoManagerPasswordException(boolean _incorrect, String _str)
/*    */   {
/* 34 */     super(_str);
/*    */     
/* 36 */     this.incorrect = _incorrect;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public CryptoManagerPasswordException(boolean _incorrect, String _str, Throwable _cause)
/*    */   {
/* 45 */     super(_str, _cause);
/*    */     
/* 47 */     this.incorrect = _incorrect;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean wasIncorrect()
/*    */   {
/* 53 */     return this.incorrect;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/security/CryptoManagerPasswordException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */