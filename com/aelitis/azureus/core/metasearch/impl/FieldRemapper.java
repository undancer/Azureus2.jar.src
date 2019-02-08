/*    */ package com.aelitis.azureus.core.metasearch.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.metasearch.Result;
/*    */ import java.util.regex.Matcher;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FieldRemapper
/*    */ {
/*    */   private int inField;
/*    */   private int outField;
/*    */   private FieldRemapping[] fieldRemappings;
/*    */   
/*    */   public FieldRemapper(int inField, int outField, FieldRemapping[] fieldRemappings)
/*    */   {
/* 42 */     this.inField = inField;
/* 43 */     this.outField = outField;
/* 44 */     this.fieldRemappings = fieldRemappings;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getInField()
/*    */   {
/* 50 */     return this.inField;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getOutField()
/*    */   {
/* 56 */     return this.outField;
/*    */   }
/*    */   
/*    */ 
/*    */   public FieldRemapping[] getMappings()
/*    */   {
/* 62 */     return this.fieldRemappings;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void remap(Result r)
/*    */   {
/* 69 */     String input = null;
/* 70 */     switch (this.inField) {
/*    */     case 6: 
/* 72 */       input = r.getCategory();
/*    */     }
/*    */     
/*    */     
/* 76 */     String output = null;
/* 77 */     if (input != null) {
/* 78 */       for (int i = 0; i < this.fieldRemappings.length; i++) {
/* 79 */         if ((this.fieldRemappings[i].getMatch() != null) && (this.fieldRemappings[i].getReplacement() != null)) {
/* 80 */           Matcher matcher = this.fieldRemappings[i].getMatch().matcher(input);
/* 81 */           if (matcher.matches()) {
/* 82 */             output = matcher.replaceAll(this.fieldRemappings[i].getReplacement());
/* 83 */             i = this.fieldRemappings.length;
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */     
/* 89 */     if (output != null) {
/* 90 */       switch (this.outField) {
/*    */       case 6: 
/* 92 */         r.setCategory(output);
/* 93 */         break;
/*    */       case 8: 
/* 95 */         r.setContentType(output);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/FieldRemapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */