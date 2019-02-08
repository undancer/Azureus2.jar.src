/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DERInteger;
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
/*     */ public class AttributeCertificateInfo
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private DERInteger version;
/*     */   private Holder holder;
/*     */   private AttCertIssuer issuer;
/*     */   private AlgorithmIdentifier signature;
/*     */   private DERInteger serialNumber;
/*     */   private AttCertValidityPeriod attrCertValidityPeriod;
/*     */   private ASN1Sequence attributes;
/*     */   private DERBitString issuerUniqueID;
/*     */   private X509Extensions extensions;
/*     */   
/*     */   public static AttributeCertificateInfo getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  35 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static AttributeCertificateInfo getInstance(Object obj)
/*     */   {
/*  41 */     if ((obj instanceof AttributeCertificateInfo))
/*     */     {
/*  43 */       return (AttributeCertificateInfo)obj;
/*     */     }
/*  45 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  47 */       return new AttributeCertificateInfo((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  50 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public AttributeCertificateInfo(ASN1Sequence seq)
/*     */   {
/*  56 */     if ((seq.size() < 7) || (seq.size() > 9))
/*     */     {
/*  58 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*  61 */     this.version = DERInteger.getInstance(seq.getObjectAt(0));
/*  62 */     this.holder = Holder.getInstance(seq.getObjectAt(1));
/*  63 */     this.issuer = AttCertIssuer.getInstance(seq.getObjectAt(2));
/*  64 */     this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(3));
/*  65 */     this.serialNumber = DERInteger.getInstance(seq.getObjectAt(4));
/*  66 */     this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(seq.getObjectAt(5));
/*  67 */     this.attributes = ASN1Sequence.getInstance(seq.getObjectAt(6));
/*     */     
/*  69 */     for (int i = 7; i < seq.size(); i++)
/*     */     {
/*  71 */       ASN1Encodable obj = (ASN1Encodable)seq.getObjectAt(i);
/*     */       
/*  73 */       if ((obj instanceof DERBitString))
/*     */       {
/*  75 */         this.issuerUniqueID = DERBitString.getInstance(seq.getObjectAt(i));
/*     */       }
/*  77 */       else if (((obj instanceof ASN1Sequence)) || ((obj instanceof X509Extensions)))
/*     */       {
/*  79 */         this.extensions = X509Extensions.getInstance(seq.getObjectAt(i));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public DERInteger getVersion()
/*     */   {
/*  86 */     return this.version;
/*     */   }
/*     */   
/*     */   public Holder getHolder()
/*     */   {
/*  91 */     return this.holder;
/*     */   }
/*     */   
/*     */   public AttCertIssuer getIssuer()
/*     */   {
/*  96 */     return this.issuer;
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getSignature()
/*     */   {
/* 101 */     return this.signature;
/*     */   }
/*     */   
/*     */   public DERInteger getSerialNumber()
/*     */   {
/* 106 */     return this.serialNumber;
/*     */   }
/*     */   
/*     */   public AttCertValidityPeriod getAttrCertValidityPeriod()
/*     */   {
/* 111 */     return this.attrCertValidityPeriod;
/*     */   }
/*     */   
/*     */   public ASN1Sequence getAttributes()
/*     */   {
/* 116 */     return this.attributes;
/*     */   }
/*     */   
/*     */   public DERBitString getIssuerUniqueID()
/*     */   {
/* 121 */     return this.issuerUniqueID;
/*     */   }
/*     */   
/*     */   public X509Extensions getExtensions()
/*     */   {
/* 126 */     return this.extensions;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 149 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 151 */     v.add(this.version);
/* 152 */     v.add(this.holder);
/* 153 */     v.add(this.issuer);
/* 154 */     v.add(this.signature);
/* 155 */     v.add(this.serialNumber);
/* 156 */     v.add(this.attrCertValidityPeriod);
/* 157 */     v.add(this.attributes);
/*     */     
/* 159 */     if (this.issuerUniqueID != null)
/*     */     {
/* 161 */       v.add(this.issuerUniqueID);
/*     */     }
/*     */     
/* 164 */     if (this.extensions != null)
/*     */     {
/* 166 */       v.add(this.extensions);
/*     */     }
/*     */     
/* 169 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AttributeCertificateInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */