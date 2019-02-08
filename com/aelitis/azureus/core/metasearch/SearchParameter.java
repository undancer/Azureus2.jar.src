/*    */ package com.aelitis.azureus.core.metasearch;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SearchParameter
/*    */ {
/*    */   private String matchPattern;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String value;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SearchParameter(String matchPattern, String value)
/*    */   {
/* 29 */     this.matchPattern = matchPattern;
/* 30 */     this.value = value;
/*    */   }
/*    */   
/*    */   public String getMatchPattern()
/*    */   {
/* 35 */     return this.matchPattern;
/*    */   }
/*    */   
/*    */   public String getValue()
/*    */   {
/* 40 */     return this.value;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/SearchParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */