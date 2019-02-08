/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DHParameter
/*    */   implements DEREncodable
/*    */ {
/*    */   DERInteger p;
/*    */   DERInteger g;
/*    */   DERInteger l;
/*    */   
/*    */   public DHParameter(BigInteger p, BigInteger g, int l)
/*    */   {
/* 23 */     this.p = new DERInteger(p);
/* 24 */     this.g = new DERInteger(g);
/*    */     
/* 26 */     if (l != 0)
/*    */     {
/* 28 */       this.l = new DERInteger(l);
/*    */     }
/*    */     else
/*    */     {
/* 32 */       this.l = null;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public DHParameter(ASN1Sequence seq)
/*    */   {
/* 39 */     Enumeration e = seq.getObjects();
/*    */     
/* 41 */     this.p = ((DERInteger)e.nextElement());
/* 42 */     this.g = ((DERInteger)e.nextElement());
/*    */     
/* 44 */     if (e.hasMoreElements())
/*    */     {
/* 46 */       this.l = ((DERInteger)e.nextElement());
/*    */     }
/*    */     else
/*    */     {
/* 50 */       this.l = null;
/*    */     }
/*    */   }
/*    */   
/*    */   public BigInteger getP()
/*    */   {
/* 56 */     return this.p.getPositiveValue();
/*    */   }
/*    */   
/*    */   public BigInteger getG()
/*    */   {
/* 61 */     return this.g.getPositiveValue();
/*    */   }
/*    */   
/*    */   public BigInteger getL()
/*    */   {
/* 66 */     if (this.l == null)
/*    */     {
/* 68 */       return null;
/*    */     }
/*    */     
/* 71 */     return this.l.getPositiveValue();
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 76 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 78 */     v.add(this.p);
/* 79 */     v.add(this.g);
/*    */     
/* 81 */     if (getL() != null)
/*    */     {
/* 83 */       v.add(this.l);
/*    */     }
/*    */     
/* 86 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/DHParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */