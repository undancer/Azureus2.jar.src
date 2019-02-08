/*     */ package org.gudy.bouncycastle.asn1.x509;
/*     */ 
/*     */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*     */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*     */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*     */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*     */ import org.gudy.bouncycastle.asn1.DERIA5String;
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
/*     */ public class PolicyQualifierInfo
/*     */   extends ASN1Encodable
/*     */ {
/*     */   private DERObjectIdentifier policyQualifierId;
/*     */   private DEREncodable qualifier;
/*     */   
/*     */   public PolicyQualifierInfo(DERObjectIdentifier policyQualifierId, DEREncodable qualifier)
/*     */   {
/*  38 */     this.policyQualifierId = policyQualifierId;
/*  39 */     this.qualifier = qualifier;
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
/*     */   public PolicyQualifierInfo(String cps)
/*     */   {
/*  52 */     this.policyQualifierId = PolicyQualifierId.id_qt_cps;
/*  53 */     this.qualifier = new DERIA5String(cps);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PolicyQualifierInfo(ASN1Sequence as)
/*     */   {
/*  65 */     if (as.size() != 2)
/*     */     {
/*  67 */       throw new IllegalArgumentException("Bad sequence size: " + as.size());
/*     */     }
/*     */     
/*     */ 
/*  71 */     this.policyQualifierId = DERObjectIdentifier.getInstance(as.getObjectAt(0));
/*  72 */     this.qualifier = as.getObjectAt(1);
/*     */   }
/*     */   
/*     */ 
/*     */   public static PolicyQualifierInfo getInstance(Object as)
/*     */   {
/*  78 */     if ((as instanceof PolicyQualifierInfo))
/*     */     {
/*  80 */       return (PolicyQualifierInfo)as;
/*     */     }
/*  82 */     if ((as instanceof ASN1Sequence))
/*     */     {
/*  84 */       return new PolicyQualifierInfo((ASN1Sequence)as);
/*     */     }
/*     */     
/*  87 */     throw new IllegalArgumentException("unknown object in getInstance.");
/*     */   }
/*     */   
/*     */ 
/*     */   public DERObjectIdentifier getPolicyQualifierId()
/*     */   {
/*  93 */     return this.policyQualifierId;
/*     */   }
/*     */   
/*     */   public DEREncodable getQualifier()
/*     */   {
/*  98 */     return this.qualifier;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DERObject toASN1Object()
/*     */   {
/* 108 */     ASN1EncodableVector dev = new ASN1EncodableVector();
/* 109 */     dev.add(this.policyQualifierId);
/* 110 */     dev.add(this.qualifier);
/*     */     
/* 112 */     return new DERSequence(dev);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/PolicyQualifierInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */