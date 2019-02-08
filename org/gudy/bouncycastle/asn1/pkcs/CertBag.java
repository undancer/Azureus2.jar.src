/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*    */ 
/*    */ 
/*    */ public class CertBag
/*    */   implements DEREncodable
/*    */ {
/*    */   ASN1Sequence seq;
/*    */   DERObjectIdentifier certId;
/*    */   DERObject certValue;
/*    */   
/*    */   public CertBag(ASN1Sequence seq)
/*    */   {
/* 21 */     this.seq = seq;
/* 22 */     this.certId = ((DERObjectIdentifier)seq.getObjectAt(0));
/* 23 */     this.certValue = ((DERTaggedObject)seq.getObjectAt(1)).getObject();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public CertBag(DERObjectIdentifier certId, DERObject certValue)
/*    */   {
/* 30 */     this.certId = certId;
/* 31 */     this.certValue = certValue;
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getCertId()
/*    */   {
/* 36 */     return this.certId;
/*    */   }
/*    */   
/*    */   public DERObject getCertValue()
/*    */   {
/* 41 */     return this.certValue;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 46 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 48 */     v.add(this.certId);
/* 49 */     v.add(new DERTaggedObject(0, this.certValue));
/*    */     
/* 51 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/CertBag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */