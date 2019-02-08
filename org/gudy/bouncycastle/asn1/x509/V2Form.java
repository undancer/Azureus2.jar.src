/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
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
/*     */ public class V2Form
/*     */   extends ASN1Encodable
/*     */ {
/*     */   GeneralNames issuerName;
/*     */   IssuerSerial baseCertificateID;
/*     */   ObjectDigestInfo objectDigestInfo;
/*     */   
/*     */   public static V2Form getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  26 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static V2Form getInstance(Object obj)
/*     */   {
/*  32 */     if ((obj == null) || ((obj instanceof V2Form)))
/*     */     {
/*  34 */       return (V2Form)obj;
/*     */     }
/*  36 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  38 */       return new V2Form((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  41 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public V2Form(GeneralNames issuerName)
/*     */   {
/*  47 */     this.issuerName = issuerName;
/*     */   }
/*     */   
/*     */ 
/*     */   public V2Form(ASN1Sequence seq)
/*     */   {
/*  53 */     if (seq.size() > 3)
/*     */     {
/*  55 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*  58 */     int index = 0;
/*     */     
/*  60 */     if (!(seq.getObjectAt(0) instanceof ASN1TaggedObject))
/*     */     {
/*  62 */       index++;
/*  63 */       this.issuerName = GeneralNames.getInstance(seq.getObjectAt(0));
/*     */     }
/*     */     
/*  66 */     for (int i = index; i != seq.size(); i++)
/*     */     {
/*  68 */       ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
/*  69 */       if (o.getTagNo() == 0)
/*     */       {
/*  71 */         this.baseCertificateID = IssuerSerial.getInstance(o, false);
/*     */       }
/*  73 */       else if (o.getTagNo() == 1)
/*     */       {
/*  75 */         this.objectDigestInfo = ObjectDigestInfo.getInstance(o, false);
/*     */       }
/*     */       else
/*     */       {
/*  79 */         throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public GeneralNames getIssuerName()
/*     */   {
/*  87 */     return this.issuerName;
/*     */   }
/*     */   
/*     */   public IssuerSerial getBaseCertificateID()
/*     */   {
/*  92 */     return this.baseCertificateID;
/*     */   }
/*     */   
/*     */   public ObjectDigestInfo getObjectDigestInfo()
/*     */   {
/*  97 */     return this.objectDigestInfo;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 115 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 117 */     if (this.issuerName != null)
/*     */     {
/* 119 */       v.add(this.issuerName);
/*     */     }
/*     */     
/* 122 */     if (this.baseCertificateID != null)
/*     */     {
/* 124 */       v.add(new DERTaggedObject(false, 0, this.baseCertificateID));
/*     */     }
/*     */     
/* 127 */     if (this.objectDigestInfo != null)
/*     */     {
/* 129 */       v.add(new DERTaggedObject(false, 1, this.objectDigestInfo));
/*     */     }
/*     */     
/* 132 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/V2Form.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */