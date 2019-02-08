/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CertificatePair
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private X509CertificateStructure forward;
/*     */   private X509CertificateStructure reverse;
/*     */   
/*     */   public static CertificatePair getInstance(Object obj)
/*     */   {
/*  56 */     if ((obj == null) || ((obj instanceof CertificatePair)))
/*     */     {
/*  58 */       return (CertificatePair)obj;
/*     */     }
/*     */     
/*  61 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  63 */       return new CertificatePair((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  66 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private CertificatePair(ASN1Sequence seq)
/*     */   {
/*  86 */     if ((seq.size() != 1) && (seq.size() != 2))
/*     */     {
/*  88 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*     */ 
/*  92 */     Enumeration e = seq.getObjects();
/*     */     
/*  94 */     while (e.hasMoreElements())
/*     */     {
/*  96 */       ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement());
/*  97 */       if (o.getTagNo() == 0)
/*     */       {
/*  99 */         this.forward = X509CertificateStructure.getInstance(o, true);
/*     */       }
/* 101 */       else if (o.getTagNo() == 1)
/*     */       {
/* 103 */         this.reverse = X509CertificateStructure.getInstance(o, true);
/*     */       }
/*     */       else
/*     */       {
/* 107 */         throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificatePair(X509CertificateStructure forward, X509CertificateStructure reverse)
/*     */   {
/* 121 */     this.forward = forward;
/* 122 */     this.reverse = reverse;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 141 */     ASN1EncodableVector vec = new ASN1EncodableVector();
/*     */     
/* 143 */     if (this.forward != null)
/*     */     {
/* 145 */       vec.add(new DERTaggedObject(0, this.forward));
/*     */     }
/* 147 */     if (this.reverse != null)
/*     */     {
/* 149 */       vec.add(new DERTaggedObject(1, this.reverse));
/*     */     }
/*     */     
/* 152 */     return new DERSequence(vec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CertificateStructure getForward()
/*     */   {
/* 160 */     return this.forward;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public X509CertificateStructure getReverse()
/*     */   {
/* 168 */     return this.reverse;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/CertificatePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */