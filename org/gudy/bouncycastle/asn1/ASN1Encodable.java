/*    */ package org.gudy.bouncycastle.asn1;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public abstract class ASN1Encodable
/*    */   implements DEREncodable
/*    */ {
/*    */   public static final String DER = "DER";
/*    */   public static final String BER = "BER";
/*    */   
/*    */   public byte[] getEncoded()
/*    */     throws IOException
/*    */   {
/* 15 */     ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 16 */     ASN1OutputStream aOut = new ASN1OutputStream(bOut);
/*    */     
/* 18 */     aOut.writeObject(this);
/*    */     
/* 20 */     return bOut.toByteArray();
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getEncoded(String encoding)
/*    */     throws IOException
/*    */   {
/* 27 */     if (encoding.equals("DER"))
/*    */     {
/* 29 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream();
/* 30 */       DEROutputStream dOut = new DEROutputStream(bOut);
/*    */       
/* 32 */       dOut.writeObject(this);
/*    */       
/* 34 */       return bOut.toByteArray();
/*    */     }
/*    */     
/* 37 */     return getEncoded();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public byte[] getDEREncoded()
/*    */   {
/*    */     try
/*    */     {
/* 49 */       return getEncoded("DER");
/*    */     }
/*    */     catch (IOException e) {}
/*    */     
/* 53 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     return toASN1Object().hashCode();
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 65 */     if (this == o)
/*    */     {
/* 67 */       return true;
/*    */     }
/*    */     
/* 70 */     if (!(o instanceof DEREncodable))
/*    */     {
/* 72 */       return false;
/*    */     }
/*    */     
/* 75 */     DEREncodable other = (DEREncodable)o;
/*    */     
/* 77 */     return toASN1Object().equals(other.getDERObject());
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 82 */     return toASN1Object();
/*    */   }
/*    */   
/*    */   public abstract DERObject toASN1Object();
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/ASN1Encodable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */