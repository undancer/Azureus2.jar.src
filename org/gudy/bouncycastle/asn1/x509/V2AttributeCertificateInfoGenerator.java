/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERGeneralizedTime;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERSet;
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
/*     */ public class V2AttributeCertificateInfoGenerator
/*     */ {
/*     */   private DERInteger version;
/*     */   private Holder holder;
/*     */   private AttCertIssuer issuer;
/*     */   private AlgorithmIdentifier signature;
/*     */   private DERInteger serialNumber;
/*     */   private ASN1EncodableVector attributes;
/*     */   private DERBitString issuerUniqueID;
/*     */   private X509Extensions extensions;
/*     */   private DERGeneralizedTime startDate;
/*     */   private DERGeneralizedTime endDate;
/*     */   
/*     */   public V2AttributeCertificateInfoGenerator()
/*     */   {
/*  52 */     this.version = new DERInteger(1);
/*  53 */     this.attributes = new ASN1EncodableVector();
/*     */   }
/*     */   
/*     */   public void setHolder(Holder holder)
/*     */   {
/*  58 */     this.holder = holder;
/*     */   }
/*     */   
/*     */   public void addAttribute(String oid, ASN1Encodable value)
/*     */   {
/*  63 */     this.attributes.add(new Attribute(new DERObjectIdentifier(oid), new DERSet(value)));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAttribute(Attribute attribute)
/*     */   {
/*  71 */     this.attributes.add(attribute);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSerialNumber(DERInteger serialNumber)
/*     */   {
/*  77 */     this.serialNumber = serialNumber;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setSignature(AlgorithmIdentifier signature)
/*     */   {
/*  83 */     this.signature = signature;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIssuer(AttCertIssuer issuer)
/*     */   {
/*  89 */     this.issuer = issuer;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setStartDate(DERGeneralizedTime startDate)
/*     */   {
/*  95 */     this.startDate = startDate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEndDate(DERGeneralizedTime endDate)
/*     */   {
/* 101 */     this.endDate = endDate;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setIssuerUniqueID(DERBitString issuerUniqueID)
/*     */   {
/* 107 */     this.issuerUniqueID = issuerUniqueID;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setExtensions(X509Extensions extensions)
/*     */   {
/* 113 */     this.extensions = extensions;
/*     */   }
/*     */   
/*     */   public AttributeCertificateInfo generateAttributeCertificateInfo()
/*     */   {
/* 118 */     if ((this.serialNumber == null) || (this.signature == null) || (this.issuer == null) || (this.startDate == null) || (this.endDate == null) || (this.holder == null) || (this.attributes == null))
/*     */     {
/*     */ 
/*     */ 
/* 122 */       throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator");
/*     */     }
/*     */     
/* 125 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 127 */     v.add(this.version);
/* 128 */     v.add(this.holder);
/* 129 */     v.add(this.issuer);
/* 130 */     v.add(this.signature);
/* 131 */     v.add(this.serialNumber);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 136 */     AttCertValidityPeriod validity = new AttCertValidityPeriod(this.startDate, this.endDate);
/* 137 */     v.add(validity);
/*     */     
/*     */ 
/* 140 */     v.add(new DERSequence(this.attributes));
/*     */     
/* 142 */     if (this.issuerUniqueID != null)
/*     */     {
/* 144 */       v.add(this.issuerUniqueID);
/*     */     }
/*     */     
/* 147 */     if (this.extensions != null)
/*     */     {
/* 149 */       v.add(this.extensions);
/*     */     }
/*     */     
/* 152 */     return new AttributeCertificateInfo(new DERSequence(v));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/V2AttributeCertificateInfoGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */