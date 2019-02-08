/*    */ package org.json.simple.parser;
/*    */ 
/*    */ 
/*    */ public class Yytoken
/*    */ {
/*    */   public static final int TYPE_VALUE = 0;
/*    */   
/*    */   public static final int TYPE_LEFT_BRACE = 1;
/*    */   
/*    */   public static final int TYPE_RIGHT_BRACE = 2;
/*    */   
/*    */   public static final int TYPE_LEFT_SQUARE = 3;
/*    */   
/*    */   public static final int TYPE_RIGHT_SQUARE = 4;
/*    */   
/*    */   public static final int TYPE_COMMA = 5;
/*    */   
/*    */   public static final int TYPE_COLON = 6;
/*    */   public static final int TYPE_EOF = -1;
/* 20 */   public int type = 0;
/* 21 */   public Object value = null;
/*    */   
/*    */   public Yytoken(int type, Object value) {
/* 24 */     this.type = type;
/* 25 */     this.value = value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 29 */     return String.valueOf(this.type + "=>|" + this.value + "|");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/json/simple/parser/Yytoken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */