/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
/*     */ import org.gudy.bouncycastle.asn1.x509.X509Name;
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
/*     */ public class CertificationRequestInfo
/*     */   implements DEREncodable
/*     */ {
/*  35 */   DERInteger version = new DERInteger(0);
/*     */   X509Name subject;
/*     */   SubjectPublicKeyInfo subjectPKInfo;
/*  38 */   ASN1Set attributes = null;
/*     */   
/*     */ 
/*     */   public static CertificationRequestInfo getInstance(Object obj)
/*     */   {
/*  43 */     if ((obj instanceof CertificationRequestInfo))
/*     */     {
/*  45 */       return (CertificationRequestInfo)obj;
/*     */     }
/*  47 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  49 */       return new CertificationRequestInfo((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  52 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CertificationRequestInfo(X509Name subject, SubjectPublicKeyInfo pkInfo, ASN1Set attributes)
/*     */   {
/*  60 */     this.subject = subject;
/*  61 */     this.subjectPKInfo = pkInfo;
/*  62 */     this.attributes = attributes;
/*     */     
/*  64 */     if ((subject == null) || (this.version == null) || (this.subjectPKInfo == null))
/*     */     {
/*  66 */       throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public CertificationRequestInfo(ASN1Sequence seq)
/*     */   {
/*  73 */     this.version = ((DERInteger)seq.getObjectAt(0));
/*     */     
/*  75 */     this.subject = X509Name.getInstance(seq.getObjectAt(1));
/*  76 */     this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(2));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  82 */     if (seq.size() > 3)
/*     */     {
/*  84 */       DERTaggedObject tagobj = (DERTaggedObject)seq.getObjectAt(3);
/*  85 */       this.attributes = ASN1Set.getInstance(tagobj, false);
/*     */     }
/*     */     
/*  88 */     if ((this.subject == null) || (this.version == null) || (this.subjectPKInfo == null))
/*     */     {
/*  90 */       throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
/*     */     }
/*     */   }
/*     */   
/*     */   public DERInteger getVersion()
/*     */   {
/*  96 */     return this.version;
/*     */   }
/*     */   
/*     */   public X509Name getSubject()
/*     */   {
/* 101 */     return this.subject;
/*     */   }
/*     */   
/*     */   public SubjectPublicKeyInfo getSubjectPublicKeyInfo()
/*     */   {
/* 106 */     return this.subjectPKInfo;
/*     */   }
/*     */   
/*     */   public ASN1Set getAttributes()
/*     */   {
/* 111 */     return this.attributes;
/*     */   }
/*     */   
/*     */   public DERObject getDERObject()
/*     */   {
/* 116 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 118 */     v.add(this.version);
/* 119 */     v.add(this.subject);
/* 120 */     v.add(this.subjectPKInfo);
/*     */     
/* 122 */     if (this.attributes != null)
/*     */     {
/* 124 */       v.add(new DERTaggedObject(false, 0, this.attributes));
/*     */     }
/*     */     
/* 127 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/CertificationRequestInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */