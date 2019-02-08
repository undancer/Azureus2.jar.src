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
/*     */ 
/*     */ public class Holder
/*     */   extends ASN1Encodable
/*     */ {
/*     */   IssuerSerial baseCertificateID;
/*     */   GeneralNames entityName;
/*     */   ObjectDigestInfo objectDigestInfo;
/*  53 */   private int version = 1;
/*     */   
/*     */   public static Holder getInstance(Object obj)
/*     */   {
/*  57 */     if ((obj instanceof Holder))
/*     */     {
/*  59 */       return (Holder)obj;
/*     */     }
/*  61 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  63 */       return new Holder((ASN1Sequence)obj);
/*     */     }
/*  65 */     if ((obj instanceof ASN1TaggedObject))
/*     */     {
/*  67 */       return new Holder((ASN1TaggedObject)obj);
/*     */     }
/*     */     
/*  70 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Holder(ASN1TaggedObject tagObj)
/*     */   {
/*  80 */     switch (tagObj.getTagNo())
/*     */     {
/*     */     case 0: 
/*  83 */       this.baseCertificateID = IssuerSerial.getInstance(tagObj, false);
/*  84 */       break;
/*     */     case 1: 
/*  86 */       this.entityName = GeneralNames.getInstance(tagObj, false);
/*  87 */       break;
/*     */     default: 
/*  89 */       throw new IllegalArgumentException("unknown tag in Holder");
/*     */     }
/*  91 */     this.version = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Holder(ASN1Sequence seq)
/*     */   {
/* 101 */     if (seq.size() > 3)
/*     */     {
/* 103 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*     */ 
/* 107 */     for (int i = 0; i != seq.size(); i++)
/*     */     {
/* 109 */       ASN1TaggedObject tObj = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
/*     */       
/*     */ 
/* 112 */       switch (tObj.getTagNo())
/*     */       {
/*     */       case 0: 
/* 115 */         this.baseCertificateID = IssuerSerial.getInstance(tObj, false);
/* 116 */         break;
/*     */       case 1: 
/* 118 */         this.entityName = GeneralNames.getInstance(tObj, false);
/* 119 */         break;
/*     */       case 2: 
/* 121 */         this.objectDigestInfo = ObjectDigestInfo.getInstance(tObj, false);
/* 122 */         break;
/*     */       default: 
/* 124 */         throw new IllegalArgumentException("unknown tag in Holder");
/*     */       }
/*     */     }
/* 127 */     this.version = 1;
/*     */   }
/*     */   
/*     */   public Holder(IssuerSerial baseCertificateID)
/*     */   {
/* 132 */     this.baseCertificateID = baseCertificateID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Holder(IssuerSerial baseCertificateID, int version)
/*     */   {
/* 142 */     this.baseCertificateID = baseCertificateID;
/* 143 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 153 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Holder(GeneralNames entityName)
/*     */   {
/* 164 */     this.entityName = entityName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Holder(GeneralNames entityName, int version)
/*     */   {
/* 176 */     this.entityName = entityName;
/* 177 */     this.version = version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Holder(ObjectDigestInfo objectDigestInfo)
/*     */   {
/* 187 */     this.objectDigestInfo = objectDigestInfo;
/*     */   }
/*     */   
/*     */   public IssuerSerial getBaseCertificateID()
/*     */   {
/* 192 */     return this.baseCertificateID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GeneralNames getEntityName()
/*     */   {
/* 203 */     return this.entityName;
/*     */   }
/*     */   
/*     */   public ObjectDigestInfo getObjectDigestInfo()
/*     */   {
/* 208 */     return this.objectDigestInfo;
/*     */   }
/*     */   
/*     */   public DERObject toASN1Object()
/*     */   {
/* 213 */     if (this.version == 1)
/*     */     {
/* 215 */       ASN1EncodableVector v = new ASN1EncodableVector();
/*     */       
/* 217 */       if (this.baseCertificateID != null)
/*     */       {
/* 219 */         v.add(new DERTaggedObject(false, 0, this.baseCertificateID));
/*     */       }
/*     */       
/* 222 */       if (this.entityName != null)
/*     */       {
/* 224 */         v.add(new DERTaggedObject(false, 1, this.entityName));
/*     */       }
/*     */       
/* 227 */       if (this.objectDigestInfo != null)
/*     */       {
/* 229 */         v.add(new DERTaggedObject(false, 2, this.objectDigestInfo));
/*     */       }
/*     */       
/* 232 */       return new DERSequence(v);
/*     */     }
/*     */     
/*     */ 
/* 236 */     if (this.entityName != null)
/*     */     {
/* 238 */       return new DERTaggedObject(false, 1, this.entityName);
/*     */     }
/*     */     
/*     */ 
/* 242 */     return new DERTaggedObject(false, 0, this.baseCertificateID);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/Holder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */