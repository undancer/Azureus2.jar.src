/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OIDTokenizer
/*    */ {
/*    */   private String oid;
/*    */   
/*    */ 
/*    */   private int index;
/*    */   
/*    */ 
/*    */ 
/*    */   public OIDTokenizer(String oid)
/*    */   {
/* 17 */     this.oid = oid;
/* 18 */     this.index = 0;
/*    */   }
/*    */   
/*    */   public boolean hasMoreTokens()
/*    */   {
/* 23 */     return this.index != -1;
/*    */   }
/*    */   
/*    */   public String nextToken()
/*    */   {
/* 28 */     if (this.index == -1)
/*    */     {
/* 30 */       return null;
/*    */     }
/*    */     
/*    */ 
/* 34 */     int end = this.oid.indexOf('.', this.index);
/*    */     
/* 36 */     if (end == -1)
/*    */     {
/* 38 */       String token = this.oid.substring(this.index);
/* 39 */       this.index = -1;
/* 40 */       return token;
/*    */     }
/*    */     
/* 43 */     String token = this.oid.substring(this.index, end);
/*    */     
/* 45 */     this.index = (end + 1);
/* 46 */     return token;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/OIDTokenizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */