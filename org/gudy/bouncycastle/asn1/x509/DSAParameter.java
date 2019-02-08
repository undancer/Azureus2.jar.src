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
/*    */ public class DSAParameter
/*    */   extends ASN1Encodable
/*    */ {
/*    */   DERInteger p;
/*    */   DERInteger q;
/*    */   DERInteger g;
/*    */   
/*    */   public static DSAParameter getInstance(ASN1TaggedObject obj, boolean explicit)
/*    */   {
/* 24 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*    */   }
/*    */   
/*    */ 
/*    */   public static DSAParameter getInstance(Object obj)
/*    */   {
/* 30 */     if ((obj == null) || ((obj instanceof DSAParameter)))
/*    */     {
/* 32 */       return (DSAParameter)obj;
/*    */     }
/*    */     
/* 35 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 37 */       return new DSAParameter((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 40 */     throw new IllegalArgumentException("Invalid DSAParameter: " + obj.getClass().getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DSAParameter(BigInteger p, BigInteger q, BigInteger g)
/*    */   {
/* 48 */     this.p = new DERInteger(p);
/* 49 */     this.q = new DERInteger(q);
/* 50 */     this.g = new DERInteger(g);
/*    */   }
/*    */   
/*    */ 
/*    */   public DSAParameter(ASN1Sequence seq)
/*    */   {
/* 56 */     if (seq.size() != 3)
/*    */     {
/* 58 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*    */     }
/*    */     
/* 61 */     Enumeration e = seq.getObjects();
/*    */     
/* 63 */     this.p = DERInteger.getInstance(e.nextElement());
/* 64 */     this.q = DERInteger.getInstance(e.nextElement());
/* 65 */     this.g = DERInteger.getInstance(e.nextElement());
/*    */   }
/*    */   
/*    */   public BigInteger getP()
/*    */   {
/* 70 */     return this.p.getPositiveValue();
/*    */   }
/*    */   
/*    */   public BigInteger getQ()
/*    */   {
/* 75 */     return this.q.getPositiveValue();
/*    */   }
/*    */   
/*    */   public BigInteger getG()
/*    */   {
/* 80 */     return this.g.getPositiveValue();
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 85 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 87 */     v.add(this.p);
/* 88 */     v.add(this.q);
/* 89 */     v.add(this.g);
/*    */     
/* 91 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/DSAParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */