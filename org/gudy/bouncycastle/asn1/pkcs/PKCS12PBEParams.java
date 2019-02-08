/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PKCS12PBEParams
/*    */   implements DEREncodable
/*    */ {
/*    */   DERInteger iterations;
/*    */   ASN1OctetString iv;
/*    */   
/*    */   public PKCS12PBEParams(byte[] salt, int iterations)
/*    */   {
/* 24 */     this.iv = new DEROctetString(salt);
/* 25 */     this.iterations = new DERInteger(iterations);
/*    */   }
/*    */   
/*    */ 
/*    */   public PKCS12PBEParams(ASN1Sequence seq)
/*    */   {
/* 31 */     this.iv = ((ASN1OctetString)seq.getObjectAt(0));
/* 32 */     this.iterations = ((DERInteger)seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */ 
/*    */   public static PKCS12PBEParams getInstance(Object obj)
/*    */   {
/* 38 */     if ((obj instanceof PKCS12PBEParams))
/*    */     {
/* 40 */       return (PKCS12PBEParams)obj;
/*    */     }
/* 42 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 44 */       return new PKCS12PBEParams((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 47 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */   public BigInteger getIterations()
/*    */   {
/* 52 */     return this.iterations.getValue();
/*    */   }
/*    */   
/*    */   public byte[] getIV()
/*    */   {
/* 57 */     return this.iv.getOctets();
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 62 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 64 */     v.add(this.iv);
/* 65 */     v.add(this.iterations);
/*    */     
/* 67 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/PKCS12PBEParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */