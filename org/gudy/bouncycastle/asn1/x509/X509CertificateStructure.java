/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
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
/*     */ public class X509CertificateStructure
/*     */   extends ASN1Encodable
/*     */   implements X509ObjectIdentifiers, PKCSObjectIdentifiers
/*     */ {
/*     */   ASN1Sequence seq;
/*     */   TBSCertificateStructure tbsCert;
/*     */   AlgorithmIdentifier sigAlgId;
/*     */   DERBitString sig;
/*     */   
/*     */   public static X509CertificateStructure getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  41 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static X509CertificateStructure getInstance(Object obj)
/*     */   {
/*  47 */     if ((obj instanceof X509CertificateStructure))
/*     */     {
/*  49 */       return (X509CertificateStructure)obj;
/*     */     }
/*  51 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  53 */       return new X509CertificateStructure((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  56 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public X509CertificateStructure(ASN1Sequence seq)
/*     */   {
/*  62 */     this.seq = seq;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  67 */     if (seq.size() == 3)
/*     */     {
/*  69 */       this.tbsCert = TBSCertificateStructure.getInstance(seq.getObjectAt(0));
/*  70 */       this.sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
/*     */       
/*  72 */       this.sig = DERBitString.getInstance(seq.getObjectAt(2));
/*     */     }
/*     */     else
/*     */     {
/*  76 */       throw new IllegalArgumentException("sequence wrong size for a certificate");
/*     */     }
/*     */   }
/*     */   
/*     */   public TBSCertificateStructure getTBSCertificate()
/*     */   {
/*  82 */     return this.tbsCert;
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/*  87 */     return this.tbsCert.getVersion();
/*     */   }
/*     */   
/*     */   public DERInteger getSerialNumber()
/*     */   {
/*  92 */     return this.tbsCert.getSerialNumber();
/*     */   }
/*     */   
/*     */   public X509Name getIssuer()
/*     */   {
/*  97 */     return this.tbsCert.getIssuer();
/*     */   }
/*     */   
/*     */   public Time getStartDate()
/*     */   {
/* 102 */     return this.tbsCert.getStartDate();
/*     */   }
/*     */   
/*     */   public Time getEndDate()
/*     */   {
/* 107 */     return this.tbsCert.getEndDate();
/*     */   }
/*     */   
/*     */   public X509Name getSubject()
/*     */   {
/* 112 */     return this.tbsCert.getSubject();
/*     */   }
/*     */   
/*     */   public SubjectPublicKeyInfo getSubjectPublicKeyInfo()
/*     */   {
/* 117 */     return this.tbsCert.getSubjectPublicKeyInfo();
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getSignatureAlgorithm()
/*     */   {
/* 122 */     return this.sigAlgId;
/*     */   }
/*     */   
/*     */   public DERBitString getSignature()
/*     */   {
/* 127 */     return this.sig;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 132 */     return this.seq;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/X509CertificateStructure.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */