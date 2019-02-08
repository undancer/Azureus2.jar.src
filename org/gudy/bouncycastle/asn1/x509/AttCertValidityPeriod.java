/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AttCertValidityPeriod
/*    */   extends ASN1Encodable
/*    */ {
/*    */   DERGeneralizedTime notBeforeTime;
/*    */   DERGeneralizedTime notAfterTime;
/*    */   
/*    */   public static AttCertValidityPeriod getInstance(Object obj)
/*    */   {
/* 20 */     if ((obj instanceof AttCertValidityPeriod))
/*    */     {
/* 22 */       return (AttCertValidityPeriod)obj;
/*    */     }
/* 24 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 26 */       return new AttCertValidityPeriod((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 29 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public AttCertValidityPeriod(ASN1Sequence seq)
/*    */   {
/* 35 */     if (seq.size() != 2)
/*    */     {
/* 37 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*    */     }
/*    */     
/*    */ 
/* 41 */     this.notBeforeTime = DERGeneralizedTime.getInstance(seq.getObjectAt(0));
/* 42 */     this.notAfterTime = DERGeneralizedTime.getInstance(seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public AttCertValidityPeriod(DERGeneralizedTime notBeforeTime, DERGeneralizedTime notAfterTime)
/*    */   {
/* 53 */     this.notBeforeTime = notBeforeTime;
/* 54 */     this.notAfterTime = notAfterTime;
/*    */   }
/*    */   
/*    */   public DERGeneralizedTime getNotBeforeTime()
/*    */   {
/* 59 */     return this.notBeforeTime;
/*    */   }
/*    */   
/*    */   public DERGeneralizedTime getNotAfterTime()
/*    */   {
/* 64 */     return this.notAfterTime;
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
/*    */   public DERObject toASN1Object()
/*    */   {
/* 78 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 80 */     v.add(this.notBeforeTime);
/* 81 */     v.add(this.notAfterTime);
/*    */     
/* 83 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AttCertValidityPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */