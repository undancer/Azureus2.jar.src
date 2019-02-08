/*    */ package com.aelitis.azureus.core.metasearch;
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
/*    */ public class SearchException
/*    */   extends Exception
/*    */ {
/*    */   public SearchException(Throwable t)
/*    */   {
/* 25 */     super(t);
/*    */   }
/*    */   
/*    */   public SearchException(String description, Throwable t) {
/* 29 */     super(description, t);
/*    */   }
/*    */   
/*    */   public SearchException(String description) {
/* 33 */     super(description);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/SearchException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */