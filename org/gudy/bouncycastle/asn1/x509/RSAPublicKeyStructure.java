/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RSAPublicKeyStructure
/*    */   extends ASN1Encodable
/*    */ {
/*    */   private BigInteger modulus;
/*    */   private BigInteger publicExponent;
/*    */   
/*    */   public static RSAPublicKeyStructure getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 25 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*    */   }
/*    */   
/*    */ 
/*    */   public static RSAPublicKeyStructure getInstance(Object obj)
/*    */   {
/* 31 */     if ((obj == null) || ((obj instanceof RSAPublicKeyStructure)))
/*    */     {
/* 33 */       return (RSAPublicKeyStructure)obj;
/*    */     }
/*    */     
/* 36 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 38 */       return new RSAPublicKeyStructure((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 41 */     throw new IllegalArgumentException("Invalid RSAPublicKeyStructure: " + obj.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RSAPublicKeyStructure(BigInteger modulus, BigInteger publicExponent)
/*    */   {
/* 48 */     this.modulus = modulus;
/* 49 */     this.publicExponent = publicExponent;
/*    */   }
/*    */   
/*    */ 
/*    */   public RSAPublicKeyStructure(ASN1Sequence seq)
/*    */   {
/* 55 */     if (seq.size() != 2)
/*    */     {
/* 57 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*    */     }
/*    */     
/*    */ 
/* 61 */     Enumeration e = seq.getObjects();
/*    */     
/* 63 */     this.modulus = DERInteger.getInstance(e.nextElement()).getPositiveValue();
/* 64 */     this.publicExponent = DERInteger.getInstance(e.nextElement()).getPositiveValue();
/*    */   }
/*    */   
/*    */   public BigInteger getModulus()
/*    */   {
/* 69 */     return this.modulus;
/*    */   }
/*    */   
/*    */   public BigInteger getPublicExponent()
/*    */   {
/* 74 */     return this.publicExponent;
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
/*    */   public DERObject toASN1Object()
/*    */   {
/* 89 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 91 */     v.add(new DERInteger(getModulus()));
/* 92 */     v.add(new DERInteger(getPublicExponent()));
/*    */     
/* 94 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/RSAPublicKeyStructure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */