/*    */ package com.aelitis.azureus.core.metasearch.impl;
/*    */ 
/*    */ import java.util.regex.Pattern;
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
/*    */ public class FieldRemapping
/*    */ {
/*    */   private String match_str;
/*    */   private Pattern match;
/*    */   private String replace;
/*    */   
/*    */   public FieldRemapping(String match, String replace)
/*    */   {
/* 33 */     this.match_str = match;
/* 34 */     this.match = Pattern.compile(match);
/* 35 */     this.replace = replace;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getMatchString()
/*    */   {
/* 41 */     return this.match_str;
/*    */   }
/*    */   
/*    */   public Pattern getMatch() {
/* 45 */     return this.match;
/*    */   }
/*    */   
/*    */   public String getReplacement() {
/* 49 */     return this.replace;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/FieldRemapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */