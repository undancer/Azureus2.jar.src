/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERUTCTime;
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
/*     */ public class V1TBSCertificateGenerator
/*     */ {
/*  31 */   DERTaggedObject version = new DERTaggedObject(0, new DERInteger(0));
/*     */   
/*     */   DERInteger serialNumber;
/*     */   
/*     */   AlgorithmIdentifier signature;
/*     */   
/*     */   X509Name issuer;
/*     */   
/*     */   Time startDate;
/*     */   
/*     */   Time endDate;
/*     */   X509Name subject;
/*     */   SubjectPublicKeyInfo subjectPublicKeyInfo;
/*     */   
/*     */   public void setSerialNumber(DERInteger serialNumber)
/*     */   {
/*  47 */     this.serialNumber = serialNumber;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSignature(AlgorithmIdentifier signature)
/*     */   {
/*  53 */     this.signature = signature;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIssuer(X509Name issuer)
/*     */   {
/*  59 */     this.issuer = issuer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setStartDate(Time startDate)
/*     */   {
/*  65 */     this.startDate = startDate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setStartDate(DERUTCTime startDate)
/*     */   {
/*  71 */     this.startDate = new Time(startDate);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEndDate(Time endDate)
/*     */   {
/*  77 */     this.endDate = endDate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEndDate(DERUTCTime endDate)
/*     */   {
/*  83 */     this.endDate = new Time(endDate);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSubject(X509Name subject)
/*     */   {
/*  89 */     this.subject = subject;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo pubKeyInfo)
/*     */   {
/*  95 */     this.subjectPublicKeyInfo = pubKeyInfo;
/*     */   }
/*     */   
/*     */   public TBSCertificateStructure generateTBSCertificate()
/*     */   {
/* 100 */     if ((this.serialNumber == null) || (this.signature == null) || (this.issuer == null) || (this.startDate == null) || (this.endDate == null) || (this.subject == null) || (this.subjectPublicKeyInfo == null))
/*     */     {
/*     */ 
/*     */ 
/* 104 */       throw new IllegalStateException("not all mandatory fields set in V1 TBScertificate generator");
/*     */     }
/*     */     
/* 107 */     ASN1EncodableVector seq = new ASN1EncodableVector();
/*     */     
/*     */ 
/* 110 */     seq.add(this.serialNumber);
/* 111 */     seq.add(this.signature);
/* 112 */     seq.add(this.issuer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 117 */     ASN1EncodableVector validity = new ASN1EncodableVector();
/*     */     
/* 119 */     validity.add(this.startDate);
/* 120 */     validity.add(this.endDate);
/*     */     
/* 122 */     seq.add(new DERSequence(validity));
/*     */     
/* 124 */     seq.add(this.subject);
/*     */     
/* 126 */     seq.add(this.subjectPublicKeyInfo);
/*     */     
/* 128 */     return new TBSCertificateStructure(new DERSequence(seq));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/V1TBSCertificateGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */