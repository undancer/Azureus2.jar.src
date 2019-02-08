/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class DERObject
/*    */   extends ASN1Encodable
/*    */   implements DERTags
/*    */ {
/*    */   public DERObject toASN1Object()
/*    */   {
/* 11 */     return this;
/*    */   }
/*    */   
/*    */   public abstract int hashCode();
/*    */   
/*    */   public abstract boolean equals(Object paramObject);
/*    */   
/*    */   abstract void encode(DEROutputStream paramDEROutputStream)
/*    */     throws IOException;
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/DERObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */