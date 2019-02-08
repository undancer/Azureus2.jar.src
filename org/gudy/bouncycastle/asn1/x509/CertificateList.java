/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
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
/*     */ public class CertificateList
/*     */   extends ASN1Encodable
/*     */ {
/*     */   TBSCertList tbsCertList;
/*     */   AlgorithmIdentifier sigAlgId;
/*     */   DERBitString sig;
/*     */   
/*     */   public static CertificateList getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  41 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static CertificateList getInstance(Object obj)
/*     */   {
/*  47 */     if ((obj instanceof CertificateList))
/*     */     {
/*  49 */       return (CertificateList)obj;
/*     */     }
/*  51 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  53 */       return new CertificateList((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  56 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public CertificateList(ASN1Sequence seq)
/*     */   {
/*  62 */     if (seq.size() == 3)
/*     */     {
/*  64 */       this.tbsCertList = TBSCertList.getInstance(seq.getObjectAt(0));
/*  65 */       this.sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
/*  66 */       this.sig = DERBitString.getInstance(seq.getObjectAt(2));
/*     */     }
/*     */     else
/*     */     {
/*  70 */       throw new IllegalArgumentException("sequence wrong size for CertificateList");
/*     */     }
/*     */   }
/*     */   
/*     */   public TBSCertList getTBSCertList()
/*     */   {
/*  76 */     return this.tbsCertList;
/*     */   }
/*     */   
/*     */   public TBSCertList.CRLEntry[] getRevokedCertificates()
/*     */   {
/*  81 */     return this.tbsCertList.getRevokedCertificates();
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getSignatureAlgorithm()
/*     */   {
/*  86 */     return this.sigAlgId;
/*     */   }
/*     */   
/*     */   public DERBitString getSignature()
/*     */   {
/*  91 */     return this.sig;
/*     */   }
/*     */   
/*     */   public int getVersion()
/*     */   {
/*  96 */     return this.tbsCertList.getVersion();
/*     */   }
/*     */   
/*     */   public X509Name getIssuer()
/*     */   {
/* 101 */     return this.tbsCertList.getIssuer();
/*     */   }
/*     */   
/*     */   public Time getThisUpdate()
/*     */   {
/* 106 */     return this.tbsCertList.getThisUpdate();
/*     */   }
/*     */   
/*     */   public Time getNextUpdate()
/*     */   {
/* 111 */     return this.tbsCertList.getNextUpdate();
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 116 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 118 */     v.add(this.tbsCertList);
/* 119 */     v.add(this.sigAlgId);
/* 120 */     v.add(this.sig);
/*     */     
/* 122 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/CertificateList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */