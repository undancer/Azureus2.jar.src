/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SafeBag
/*    */   implements DEREncodable
/*    */ {
/*    */   DERObjectIdentifier bagId;
/*    */   DERObject bagValue;
/*    */   ASN1Set bagAttributes;
/*    */   
/*    */   public SafeBag(DERObjectIdentifier oid, DERObject obj)
/*    */   {
/* 23 */     this.bagId = oid;
/* 24 */     this.bagValue = obj;
/* 25 */     this.bagAttributes = null;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public SafeBag(DERObjectIdentifier oid, DERObject obj, ASN1Set bagAttributes)
/*    */   {
/* 33 */     this.bagId = oid;
/* 34 */     this.bagValue = obj;
/* 35 */     this.bagAttributes = bagAttributes;
/*    */   }
/*    */   
/*    */ 
/*    */   public SafeBag(ASN1Sequence seq)
/*    */   {
/* 41 */     this.bagId = ((DERObjectIdentifier)seq.getObjectAt(0));
/* 42 */     this.bagValue = ((DERTaggedObject)seq.getObjectAt(1)).getObject();
/* 43 */     if (seq.size() == 3)
/*    */     {
/* 45 */       this.bagAttributes = ((ASN1Set)seq.getObjectAt(2));
/*    */     }
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getBagId()
/*    */   {
/* 51 */     return this.bagId;
/*    */   }
/*    */   
/*    */   public DERObject getBagValue()
/*    */   {
/* 56 */     return this.bagValue;
/*    */   }
/*    */   
/*    */   public ASN1Set getBagAttributes()
/*    */   {
/* 61 */     return this.bagAttributes;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 66 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 68 */     v.add(this.bagId);
/* 69 */     v.add(new DERTaggedObject(0, this.bagValue));
/*    */     
/* 71 */     if (this.bagAttributes != null)
/*    */     {
/* 73 */       v.add(this.bagAttributes);
/*    */     }
/*    */     
/* 76 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/SafeBag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */