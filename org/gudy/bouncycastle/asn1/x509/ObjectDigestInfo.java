/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DERBitString;
/*     */ import org.gudy.bouncycastle.asn1.DEREnumerated;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
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
/*     */ public class ObjectDigestInfo
/*     */   extends ASN1Encodable
/*     */ {
/*     */   public static final int publicKey = 0;
/*     */   public static final int publicKeyCert = 1;
/*     */   public static final int otherObjectDigest = 2;
/*     */   DEREnumerated digestedObjectType;
/*     */   DERObjectIdentifier otherObjectTypeID;
/*     */   AlgorithmIdentifier digestAlgorithm;
/*     */   DERBitString objectDigest;
/*     */   
/*     */   public static ObjectDigestInfo getInstance(Object obj)
/*     */   {
/*  64 */     if ((obj == null) || ((obj instanceof ObjectDigestInfo)))
/*     */     {
/*  66 */       return (ObjectDigestInfo)obj;
/*     */     }
/*     */     
/*  69 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  71 */       return new ObjectDigestInfo((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  74 */     throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ObjectDigestInfo getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  82 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
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
/*     */   public ObjectDigestInfo(int digestedObjectType, String otherObjectTypeID, AlgorithmIdentifier digestAlgorithm, byte[] objectDigest)
/*     */   {
/* 104 */     this.digestedObjectType = new DEREnumerated(digestedObjectType);
/* 105 */     if (digestedObjectType == 2)
/*     */     {
/* 107 */       this.otherObjectTypeID = new DERObjectIdentifier(otherObjectTypeID);
/*     */     }
/*     */     
/* 110 */     this.digestAlgorithm = digestAlgorithm;
/*     */     
/* 112 */     this.objectDigest = new DERBitString(objectDigest);
/*     */   }
/*     */   
/*     */ 
/*     */   private ObjectDigestInfo(ASN1Sequence seq)
/*     */   {
/* 118 */     if ((seq.size() > 4) || (seq.size() < 3))
/*     */     {
/* 120 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*     */ 
/* 124 */     this.digestedObjectType = DEREnumerated.getInstance(seq.getObjectAt(0));
/*     */     
/* 126 */     int offset = 0;
/*     */     
/* 128 */     if (seq.size() == 4)
/*     */     {
/* 130 */       this.otherObjectTypeID = DERObjectIdentifier.getInstance(seq.getObjectAt(1));
/* 131 */       offset++;
/*     */     }
/*     */     
/* 134 */     this.digestAlgorithm = AlgorithmIdentifier.getInstance(seq.getObjectAt(1 + offset));
/*     */     
/* 136 */     this.objectDigest = DERBitString.getInstance(seq.getObjectAt(2 + offset));
/*     */   }
/*     */   
/*     */   public DEREnumerated getDigestedObjectType()
/*     */   {
/* 141 */     return this.digestedObjectType;
/*     */   }
/*     */   
/*     */   public DERObjectIdentifier getOtherObjectTypeID()
/*     */   {
/* 146 */     return this.otherObjectTypeID;
/*     */   }
/*     */   
/*     */   public AlgorithmIdentifier getDigestAlgorithm()
/*     */   {
/* 151 */     return this.digestAlgorithm;
/*     */   }
/*     */   
/*     */   public DERBitString getObjectDigest()
/*     */   {
/* 156 */     return this.objectDigest;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 180 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 182 */     v.add(this.digestedObjectType);
/*     */     
/* 184 */     if (this.otherObjectTypeID != null)
/*     */     {
/* 186 */       v.add(this.otherObjectTypeID);
/*     */     }
/*     */     
/* 189 */     v.add(this.digestAlgorithm);
/* 190 */     v.add(this.objectDigest);
/*     */     
/* 192 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/ObjectDigestInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */