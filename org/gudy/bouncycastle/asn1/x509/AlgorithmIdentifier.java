/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.ASN1TaggedObject;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERObject;
/*     */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*     */ import org.gudy.bouncycastle.asn1.DERSequence;
/*     */ 
/*     */ 
/*     */ public class AlgorithmIdentifier
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private DERObjectIdentifier objectId;
/*     */   private DEREncodable parameters;
/*  18 */   private boolean parametersDefined = false;
/*     */   
/*     */ 
/*     */ 
/*     */   public static AlgorithmIdentifier getInstance(ASN1TaggedObject obj, boolean explicit)
/*     */   {
/*  24 */     return getInstance(ASN1Sequence.getInstance(obj, explicit));
/*     */   }
/*     */   
/*     */ 
/*     */   public static AlgorithmIdentifier getInstance(Object obj)
/*     */   {
/*  30 */     if ((obj == null) || ((obj instanceof AlgorithmIdentifier)))
/*     */     {
/*  32 */       return (AlgorithmIdentifier)obj;
/*     */     }
/*     */     
/*  35 */     if ((obj instanceof DERObjectIdentifier))
/*     */     {
/*  37 */       return new AlgorithmIdentifier((DERObjectIdentifier)obj);
/*     */     }
/*     */     
/*  40 */     if ((obj instanceof String))
/*     */     {
/*  42 */       return new AlgorithmIdentifier((String)obj);
/*     */     }
/*     */     
/*  45 */     if ((obj instanceof ASN1Sequence))
/*     */     {
/*  47 */       return new AlgorithmIdentifier((ASN1Sequence)obj);
/*     */     }
/*     */     
/*  50 */     throw new IllegalArgumentException("unknown object in factory");
/*     */   }
/*     */   
/*     */ 
/*     */   public AlgorithmIdentifier(DERObjectIdentifier objectId)
/*     */   {
/*  56 */     this.objectId = objectId;
/*     */   }
/*     */   
/*     */ 
/*     */   public AlgorithmIdentifier(String objectId)
/*     */   {
/*  62 */     this.objectId = new DERObjectIdentifier(objectId);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AlgorithmIdentifier(DERObjectIdentifier objectId, DEREncodable parameters)
/*     */   {
/*  69 */     this.parametersDefined = true;
/*  70 */     this.objectId = objectId;
/*  71 */     this.parameters = parameters;
/*     */   }
/*     */   
/*     */ 
/*     */   public AlgorithmIdentifier(ASN1Sequence seq)
/*     */   {
/*  77 */     if ((seq.size() < 1) || (seq.size() > 2))
/*     */     {
/*  79 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*     */     }
/*     */     
/*     */ 
/*  83 */     this.objectId = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
/*     */     
/*  85 */     if (seq.size() == 2)
/*     */     {
/*  87 */       this.parametersDefined = true;
/*  88 */       this.parameters = seq.getObjectAt(1);
/*     */     }
/*     */     else
/*     */     {
/*  92 */       this.parameters = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public DERObjectIdentifier getObjectId()
/*     */   {
/*  98 */     return this.objectId;
/*     */   }
/*     */   
/*     */   public DEREncodable getParameters()
/*     */   {
/* 103 */     return this.parameters;
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
/*     */   public DERObject toASN1Object()
/*     */   {
/* 116 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*     */     
/* 118 */     v.add(this.objectId);
/*     */     
/* 120 */     if (this.parametersDefined)
/*     */     {
/* 122 */       v.add(this.parameters);
/*     */     }
/*     */     
/* 125 */     return new DERSequence(v);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/AlgorithmIdentifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */