/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
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
/*     */ public class TBSCertificateStructure
/*     */   extends ASN1Encodable
/*     */   implements X509ObjectIdentifiers, PKCSObjectIdentifiers
/*     */ {
/*     */   ASN1Sequence seq;
/*     */   DERInteger version;
/*     */   DERInteger serialNumber;
/*     */   AlgorithmIdentifier signature;
/*     */   X509Name issuer;
/*     */   Time startDate;
/*     */   Time endDate;
/*     */   X509Name subject;
/*     */   SubjectPublicKeyInfo subjectPublicKeyInfo;
/*     */   DERBitString issuerUniqueId;
/*     */   DERBitString subjectUniqueId;
/*     */   X509Extensions extensions;
/*     */   
/*     */   public static TBSCertificateStructure getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  60 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static TBSCertificateStructure getInstance(Object obj)
/*     */   {
/*  66 */     if ((obj instanceof TBSCertificateStructure))
/*     */     {
/*  68 */       return (TBSCertificateStructure)obj;
/*     */     }
/*  70 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  72 */       return new TBSCertificateStructure((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  75 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public TBSCertificateStructure(ASN1Sequence seq)
/*     */   {
/*  81 */     int seqStart = 0;
/*     */     
/*  83 */     this.seq = seq;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  88 */     if ((seq.getObjectAt(0) instanceof DERTaggedObject))
/*     */     {
/*  90 */       this.version = DERInteger.getInstance(seq.getObjectAt(0));
/*     */     }
/*     */     else
/*     */     {
/*  94 */       seqStart = -1;
/*  95 */       this.version = new DERInteger(0);
/*     */     }
/*     */     
/*  98 */     this.serialNumber = DERInteger.getInstance(seq.getObjectAt(seqStart + 1));
/*     */     
/* 100 */     this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqStart + 2));
/* 101 */     this.issuer = X509Name.getInstance(seq.getObjectAt(seqStart + 3));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 106 */     ASN1Sequence dates = (ASN1Sequence)seq.getObjectAt(seqStart + 4);
/*     */     
/* 108 */     this.startDate = Time.getInstance(dates.getObjectAt(0));
/* 109 */     this.endDate = Time.getInstance(dates.getObjectAt(1));
/*     */     
/* 111 */     this.subject = X509Name.getInstance(seq.getObjectAt(seqStart + 5));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 116 */     this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(seqStart + 6));
/*     */     
/* 118 */     for (int extras = seq.size() - (seqStart + 6) - 1; extras > 0; extras--)
/*     */     {
/* 120 */       DERTaggedObject extra = (DERTaggedObject)seq.getObjectAt(seqStart + 6 + extras);
/*     */       
/* 122 */       switch (extra.getTagNo())
/*     */       {
/*     */       case 1: 
/* 125 */         this.issuerUniqueId = DERBitString.getInstance(extra, false);
/* 126 */         break;
/*     */       case 2: 
/* 128 */         this.subjectUniqueId = DERBitString.getInstance(extra, false);
/* 129 */         break;
/*     */       case 3: 
/* 131 */         this.extensions = X509Extensions.getInstance(extra);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/* 138 */     return this.version.getValue().intValue() + 1;
/*     */   }
/*     */   
/*     */   public DERInteger getVersionNumber()
/*     */   {
/* 143 */     return this.version;
/*     */   }
/*     */   
/*     */   public DERInteger getSerialNumber()
/*     */   {
/* 148 */     return this.serialNumber;
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getSignature()
/*     */   {
/* 153 */     return this.signature;
/*     */   }
/*     */   
/*     */   public X509Name getIssuer()
/*     */   {
/* 158 */     return this.issuer;
/*     */   }
/*     */   
/*     */   public Time getStartDate()
/*     */   {
/* 163 */     return this.startDate;
/*     */   }
/*     */   
/*     */   public Time getEndDate()
/*     */   {
/* 168 */     return this.endDate;
/*     */   }
/*     */   
/*     */   public X509Name getSubject()
/*     */   {
/* 173 */     return this.subject;
/*     */   }
/*     */   
/*     */   public SubjectPublicKeyInfo getSubjectPublicKeyInfo()
/*     */   {
/* 178 */     return this.subjectPublicKeyInfo;
/*     */   }
/*     */   
/*     */   public DERBitString getIssuerUniqueId()
/*     */   {
/* 183 */     return this.issuerUniqueId;
/*     */   }
/*     */   
/*     */   public DERBitString getSubjectUniqueId()
/*     */   {
/* 188 */     return this.subjectUniqueId;
/*     */   }
/*     */   
/*     */   public X509Extensions getExtensions()
/*     */   {
/* 193 */     return this.extensions;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 198 */     return this.seq;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/TBSCertificateStructure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */