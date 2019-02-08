/*    */ package com.aelitis.azureus.core.metasearch.impl.web;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FieldMapping
/*    */ {
/*    */   private String name;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int field;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public FieldMapping(String name, int field)
/*    */   {
/* 28 */     this.name = name;
/* 29 */     this.field = field;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 35 */     return this.name;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getField()
/*    */   {
/* 41 */     return this.field;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/web/FieldMapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */