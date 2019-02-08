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
/*    */ public abstract class ASN1Object
/*    */   extends DERObject
/*    */ {
/*    */   public static ASN1Object fromByteArray(byte[] data)
/*    */     throws IOException
/*    */   {
/* 18 */     ASN1InputStream aIn = new ASN1InputStream(data);
/*    */     
/* 20 */     return (ASN1Object)aIn.readObject();
/*    */   }
/*    */   
/*    */   public final boolean equals(Object o)
/*    */   {
/* 25 */     if (this == o)
/*    */     {
/* 27 */       return true;
/*    */     }
/*    */     
/* 30 */     return ((o instanceof DEREncodable)) && (asn1Equals(((DEREncodable)o).getDERObject()));
/*    */   }
/*    */   
/*    */   public abstract int hashCode();
/*    */   
/*    */   abstract void encode(DEROutputStream paramDEROutputStream)
/*    */     throws IOException;
/*    */   
/*    */   abstract boolean asn1Equals(DERObject paramDERObject);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1Object.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */