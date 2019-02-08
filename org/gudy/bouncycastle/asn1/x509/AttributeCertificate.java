/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERBitString;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AttributeCertificate
/*    */   extends ASN1Encodable
/*    */ {
/*    */   AttributeCertificateInfo acinfo;
/*    */   AlgorithmIdentifier signatureAlgorithm;
/*    */   DERBitString signatureValue;
/*    */   
/*    */   public static AttributeCertificate getInstance(Object obj)
/*    */   {
/* 26 */     if ((obj instanceof AttributeCertificate))
/*    */     {
/* 28 */       return (AttributeCertificate)obj;
/*    */     }
/* 30 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 32 */       return new AttributeCertificate((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 35 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public AttributeCertificate(AttributeCertificateInfo acinfo, AlgorithmIdentifier signatureAlgorithm, DERBitString signatureValue)
/*    */   {
/* 43 */     this.acinfo = acinfo;
/* 44 */     this.signatureAlgorithm = signatureAlgorithm;
/* 45 */     this.signatureValue = signatureValue;
/*    */   }
/*    */   
/*    */ 
/*    */   public AttributeCertificate(ASN1Sequence seq)
/*    */   {
/* 51 */     if (seq.size() != 3)
/*    */     {
/* 53 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*    */     }
/*    */     
/*    */ 
/* 57 */     this.acinfo = AttributeCertificateInfo.getInstance(seq.getObjectAt(0));
/* 58 */     this.signatureAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
/* 59 */     this.signatureValue = DERBitString.getInstance(seq.getObjectAt(2));
/*    */   }
/*    */   
/*    */   public AttributeCertificateInfo getAcinfo()
/*    */   {
/* 64 */     return this.acinfo;
/*    */   }
/*    */   
/*    */   public AlgorithmIdentifier getSignatureAlgorithm()
/*    */   {
/* 69 */     return this.signatureAlgorithm;
/*    */   }
/*    */   
/*    */   public DERBitString getSignatureValue()
/*    */   {
/* 74 */     return this.signatureValue;
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
/* 91 */     v.add(this.acinfo);
/* 92 */     v.add(this.signatureAlgorithm);
/* 93 */     v.add(this.signatureValue);
/*    */     
/* 95 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AttributeCertificate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */