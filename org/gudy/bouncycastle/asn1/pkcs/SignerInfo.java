/*     */ package org.gudy.bouncycastle.asn1.pkcs;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
/*     */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*     */ 
/*     */ public class SignerInfo implements org.gudy.bouncycastle.asn1.DEREncodable
/*     */ {
/*     */   private DERInteger version;
/*     */   private IssuerAndSerialNumber issuerAndSerialNumber;
/*     */   private AlgorithmIdentifier digAlgorithm;
/*     */   private ASN1Set authenticatedAttributes;
/*     */   private AlgorithmIdentifier digEncryptionAlgorithm;
/*     */   private ASN1OctetString encryptedDigest;
/*     */   private ASN1Set unauthenticatedAttributes;
/*     */   
/*     */   public static SignerInfo getInstance(Object o)
/*     */   {
/*  25 */     if ((o instanceof SignerInfo))
/*     */     {
/*  27 */       return (SignerInfo)o;
/*     */     }
/*  29 */     if ((o instanceof ASN1Sequence))
/*     */     {
/*  31 */       return new SignerInfo((ASN1Sequence)o);
/*     */     }
/*     */     
/*  34 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SignerInfo(DERInteger version, IssuerAndSerialNumber issuerAndSerialNumber, AlgorithmIdentifier digAlgorithm, ASN1Set authenticatedAttributes, AlgorithmIdentifier digEncryptionAlgorithm, ASN1OctetString encryptedDigest, ASN1Set unauthenticatedAttributes)
/*     */   {
/*  46 */     this.version = version;
/*  47 */     this.issuerAndSerialNumber = issuerAndSerialNumber;
/*  48 */     this.digAlgorithm = digAlgorithm;
/*  49 */     this.authenticatedAttributes = authenticatedAttributes;
/*  50 */     this.digEncryptionAlgorithm = digEncryptionAlgorithm;
/*  51 */     this.encryptedDigest = encryptedDigest;
/*  52 */     this.unauthenticatedAttributes = unauthenticatedAttributes;
/*     */   }
/*     */   
/*     */ 
/*     */   public SignerInfo(ASN1Sequence seq)
/*     */   {
/*  58 */     Enumeration e = seq.getObjects();
/*     */     
/*  60 */     this.version = ((DERInteger)e.nextElement());
/*  61 */     this.issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(e.nextElement());
/*  62 */     this.digAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
/*     */     
/*  64 */     Object obj = e.nextElement();
/*     */     
/*  66 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  68 */       this.authenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)obj, false);
/*     */       
/*  70 */       this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(e.nextElement());
/*     */     }
/*     */     else
/*     */     {
/*  74 */       this.authenticatedAttributes = null;
/*  75 */       this.digEncryptionAlgorithm = AlgorithmIdentifier.getInstance(obj);
/*     */     }
/*     */     
/*  78 */     this.encryptedDigest = org.gudy.bouncycastle.asn1.DEROctetString.getInstance(e.nextElement());
/*     */     
/*  80 */     if (e.hasMoreElements())
/*     */     {
/*  82 */       this.unauthenticatedAttributes = ASN1Set.getInstance((ASN1TaggedObject)e.nextElement(), false);
/*     */     }
/*     */     else
/*     */     {
/*  86 */       this.unauthenticatedAttributes = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public DERInteger getVersion()
/*     */   {
/*  92 */     return this.version;
/*     */   }
/*     */   
/*     */   public IssuerAndSerialNumber getIssuerAndSerialNumber()
/*     */   {
/*  97 */     return this.issuerAndSerialNumber;
/*     */   }
/*     */   
/*     */   public ASN1Set getAuthenticatedAttributes()
/*     */   {
/* 102 */     return this.authenticatedAttributes;
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getDigestAlgorithm()
/*     */   {
/* 107 */     return this.digAlgorithm;
/*     */   }
/*     */   
/*     */   public ASN1OctetString getEncryptedDigest()
/*     */   {
/* 112 */     return this.encryptedDigest;
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getDigestEncryptionAlgorithm()
/*     */   {
/* 117 */     return this.digEncryptionAlgorithm;
/*     */   }
/*     */   
/*     */   public ASN1Set getUnauthenticatedAttributes()
/*     */   {
/* 122 */     return this.unauthenticatedAttributes;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public org.gudy.bouncycastle.asn1.DERObject getDERObject()
/*     */   {
/* 147 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 149 */     v.add(this.version);
/* 150 */     v.add(this.issuerAndSerialNumber);
/* 151 */     v.add(this.digAlgorithm);
/*     */     
/* 153 */     if (this.authenticatedAttributes != null)
/*     */     {
/* 155 */       v.add(new DERTaggedObject(false, 0, this.authenticatedAttributes));
/*     */     }
/*     */     
/* 158 */     v.add(this.digEncryptionAlgorithm);
/* 159 */     v.add(this.encryptedDigest);
/*     */     
/* 161 */     if (this.unauthenticatedAttributes != null)
/*     */     {
/* 163 */       v.add(new DERTaggedObject(false, 1, this.unauthenticatedAttributes));
/*     */     }
/*     */     
/* 166 */     return new org.gudy.bouncycastle.asn1.DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/SignerInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */