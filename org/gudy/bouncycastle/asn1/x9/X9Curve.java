/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERBitString;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve.Fp;
/*    */ import org.gudy.bouncycastle.math.ec.ECFieldElement;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X9Curve
/*    */   implements DEREncodable, X9ObjectIdentifiers
/*    */ {
/*    */   private ECCurve curve;
/*    */   private byte[] seed;
/*    */   
/*    */   public X9Curve(ECCurve curve)
/*    */   {
/* 28 */     this.curve = curve;
/* 29 */     this.seed = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public X9Curve(ECCurve curve, byte[] seed)
/*    */   {
/* 36 */     this.curve = curve;
/* 37 */     this.seed = seed;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public X9Curve(X9FieldID fieldID, ASN1Sequence seq)
/*    */   {
/* 44 */     if (fieldID.getIdentifier().equals(prime_field))
/*    */     {
/* 46 */       BigInteger q = ((DERInteger)fieldID.getParameters()).getValue();
/* 47 */       X9FieldElement x9A = new X9FieldElement(true, q, (ASN1OctetString)seq.getObjectAt(0));
/* 48 */       X9FieldElement x9B = new X9FieldElement(true, q, (ASN1OctetString)seq.getObjectAt(1));
/* 49 */       this.curve = new ECCurve.Fp(q, x9A.getValue().toBigInteger(), x9B.getValue().toBigInteger());
/*    */     }
/*    */     else
/*    */     {
/* 53 */       throw new RuntimeException("not implemented");
/*    */     }
/*    */     
/* 56 */     if (seq.size() == 3)
/*    */     {
/* 58 */       this.seed = ((DERBitString)seq.getObjectAt(2)).getBytes();
/*    */     }
/*    */   }
/*    */   
/*    */   public ECCurve getCurve()
/*    */   {
/* 64 */     return this.curve;
/*    */   }
/*    */   
/*    */   public byte[] getSeed()
/*    */   {
/* 69 */     return this.seed;
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
/*    */   public DERObject getDERObject()
/*    */   {
/* 84 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 86 */     v.add(new X9FieldElement(this.curve.getA()).getDERObject());
/* 87 */     v.add(new X9FieldElement(this.curve.getB()).getDERObject());
/*    */     
/* 89 */     if (this.seed != null)
/*    */     {
/* 91 */       v.add(new DERBitString(this.seed));
/*    */     }
/*    */     
/* 94 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9Curve.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */