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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V3TBSCertificateGenerator
/*     */ {
/*  36 */   DERTaggedObject version = new DERTaggedObject(0, new DERInteger(2));
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
/*     */   
/*     */   X509Name subject;
/*     */   SubjectPublicKeyInfo subjectPublicKeyInfo;
/*     */   X509Extensions extensions;
/*     */   private boolean altNamePresentAndCritical;
/*     */   
/*     */   public void setSerialNumber(DERInteger serialNumber)
/*     */   {
/*  55 */     this.serialNumber = serialNumber;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSignature(AlgorithmIdentifier signature)
/*     */   {
/*  61 */     this.signature = signature;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIssuer(X509Name issuer)
/*     */   {
/*  67 */     this.issuer = issuer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setStartDate(DERUTCTime startDate)
/*     */   {
/*  73 */     this.startDate = new Time(startDate);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setStartDate(Time startDate)
/*     */   {
/*  79 */     this.startDate = startDate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEndDate(DERUTCTime endDate)
/*     */   {
/*  85 */     this.endDate = new Time(endDate);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEndDate(Time endDate)
/*     */   {
/*  91 */     this.endDate = endDate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSubject(X509Name subject)
/*     */   {
/*  97 */     this.subject = subject;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo pubKeyInfo)
/*     */   {
/* 103 */     this.subjectPublicKeyInfo = pubKeyInfo;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setExtensions(X509Extensions extensions)
/*     */   {
/* 109 */     this.extensions = extensions;
/* 110 */     if (extensions != null)
/*     */     {
/* 112 */       X509Extension altName = extensions.getExtension(X509Extensions.SubjectAlternativeName);
/*     */       
/* 114 */       if ((altName != null) && (altName.isCritical()))
/*     */       {
/* 116 */         this.altNamePresentAndCritical = true;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public TBSCertificateStructure generateTBSCertificate()
/*     */   {
/* 123 */     if ((this.serialNumber == null) || (this.signature == null) || (this.issuer == null) || (this.startDate == null) || (this.endDate == null) || ((this.subject == null) && (!this.altNamePresentAndCritical)) || (this.subjectPublicKeyInfo == null))
/*     */     {
/*     */ 
/*     */ 
/* 127 */       throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
/*     */     }
/*     */     
/* 130 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 132 */     v.add(this.version);
/* 133 */     v.add(this.serialNumber);
/* 134 */     v.add(this.signature);
/* 135 */     v.add(this.issuer);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 140 */     ASN1EncodableVector validity = new ASN1EncodableVector();
/*     */     
/* 142 */     validity.add(this.startDate);
/* 143 */     validity.add(this.endDate);
/*     */     
/* 145 */     v.add(new DERSequence(validity));
/*     */     
/* 147 */     if (this.subject != null)
/*     */     {
/* 149 */       v.add(this.subject);
/*     */     }
/*     */     else
/*     */     {
/* 153 */       v.add(new DERSequence());
/*     */     }
/*     */     
/* 156 */     v.add(this.subjectPublicKeyInfo);
/*     */     
/* 158 */     if (this.extensions != null)
/*     */     {
/* 160 */       v.add(new DERTaggedObject(3, this.extensions));
/*     */     }
/*     */     
/* 163 */     return new TBSCertificateStructure(new DERSequence(v));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/V3TBSCertificateGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */