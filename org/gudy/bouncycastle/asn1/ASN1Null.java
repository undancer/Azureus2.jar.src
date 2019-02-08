/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ASN1Null
/*    */   extends ASN1Object
/*    */ {
/*    */   public int hashCode()
/*    */   {
/* 17 */     return 0;
/*    */   }
/*    */   
/*    */ 
/*    */   boolean asn1Equals(DERObject o)
/*    */   {
/* 23 */     if (!(o instanceof ASN1Null))
/*    */     {
/* 25 */       return false;
/*    */     }
/*    */     
/* 28 */     return true;
/*    */   }
/*    */   
/*    */   abstract void encode(DEROutputStream paramDEROutputStream)
/*    */     throws IOException;
/*    */   
/*    */   public String toString()
/*    */   {
/* 36 */     return "NULL";
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1Null.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */