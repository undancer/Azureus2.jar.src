/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PolicyInformation
/*    */   extends ASN1Encodable
/*    */ {
/*    */   private DERObjectIdentifier policyIdentifier;
/*    */   private ASN1Sequence policyQualifiers;
/*    */   
/*    */   public PolicyInformation(ASN1Sequence seq)
/*    */   {
/* 20 */     if ((seq.size() < 1) || (seq.size() > 2))
/*    */     {
/* 22 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*    */     }
/*    */     
/*    */ 
/* 26 */     this.policyIdentifier = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
/*    */     
/* 28 */     if (seq.size() > 1)
/*    */     {
/* 30 */       this.policyQualifiers = ASN1Sequence.getInstance(seq.getObjectAt(1));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public PolicyInformation(DERObjectIdentifier policyIdentifier)
/*    */   {
/* 37 */     this.policyIdentifier = policyIdentifier;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public PolicyInformation(DERObjectIdentifier policyIdentifier, ASN1Sequence policyQualifiers)
/*    */   {
/* 44 */     this.policyIdentifier = policyIdentifier;
/* 45 */     this.policyQualifiers = policyQualifiers;
/*    */   }
/*    */   
/*    */ 
/*    */   public static PolicyInformation getInstance(Object obj)
/*    */   {
/* 51 */     if ((obj == null) || ((obj instanceof PolicyInformation)))
/*    */     {
/* 53 */       return (PolicyInformation)obj;
/*    */     }
/*    */     
/* 56 */     return new PolicyInformation(ASN1Sequence.getInstance(obj));
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getPolicyIdentifier()
/*    */   {
/* 61 */     return this.policyIdentifier;
/*    */   }
/*    */   
/*    */   public ASN1Sequence getPolicyQualifiers()
/*    */   {
/* 66 */     return this.policyQualifiers;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERObject toASN1Object()
/*    */   {
/* 77 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 79 */     v.add(this.policyIdentifier);
/*    */     
/* 81 */     if (this.policyQualifiers != null)
/*    */     {
/* 83 */       v.add(this.policyQualifiers);
/*    */     }
/*    */     
/* 86 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/PolicyInformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */