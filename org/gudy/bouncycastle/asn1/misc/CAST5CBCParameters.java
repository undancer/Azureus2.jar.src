/*    */ package org.gudy.bouncycastle.asn1.misc;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ 
/*    */ public class CAST5CBCParameters implements org.gudy.bouncycastle.asn1.DEREncodable
/*    */ {
/*    */   DERInteger keyLength;
/*    */   org.gudy.bouncycastle.asn1.ASN1OctetString iv;
/*    */   
/*    */   public static CAST5CBCParameters getInstance(Object o)
/*    */   {
/* 14 */     if ((o instanceof CAST5CBCParameters))
/*    */     {
/* 16 */       return (CAST5CBCParameters)o;
/*    */     }
/* 18 */     if ((o instanceof ASN1Sequence))
/*    */     {
/* 20 */       return new CAST5CBCParameters((ASN1Sequence)o);
/*    */     }
/*    */     
/* 23 */     throw new IllegalArgumentException("unknown object in CAST5CBCParameter factory");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public CAST5CBCParameters(byte[] iv, int keyLength)
/*    */   {
/* 30 */     this.iv = new org.gudy.bouncycastle.asn1.DEROctetString(iv);
/* 31 */     this.keyLength = new DERInteger(keyLength);
/*    */   }
/*    */   
/*    */ 
/*    */   public CAST5CBCParameters(ASN1Sequence seq)
/*    */   {
/* 37 */     this.iv = ((org.gudy.bouncycastle.asn1.ASN1OctetString)seq.getObjectAt(0));
/* 38 */     this.keyLength = ((DERInteger)seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */   public byte[] getIV()
/*    */   {
/* 43 */     return this.iv.getOctets();
/*    */   }
/*    */   
/*    */   public int getKeyLength()
/*    */   {
/* 48 */     return this.keyLength.getValue().intValue();
/*    */   }
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
/*    */   public org.gudy.bouncycastle.asn1.DERObject getDERObject()
/*    */   {
/* 64 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 66 */     v.add(this.iv);
/* 67 */     v.add(this.keyLength);
/*    */     
/* 69 */     return new org.gudy.bouncycastle.asn1.DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/misc/CAST5CBCParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */