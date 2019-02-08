/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*    */ 
/*    */ 
/*    */ public class EncryptionScheme
/*    */   extends AlgorithmIdentifier
/*    */ {
/*    */   DERObject objectId;
/*    */   DERObject obj;
/*    */   
/*    */   EncryptionScheme(ASN1Sequence seq)
/*    */   {
/* 18 */     super(seq);
/*    */     
/* 20 */     this.objectId = ((DERObject)seq.getObjectAt(0));
/* 21 */     this.obj = ((DERObject)seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */   public DERObject getObject()
/*    */   {
/* 26 */     return this.obj;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 31 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 33 */     v.add(this.objectId);
/* 34 */     v.add(this.obj);
/*    */     
/* 36 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/EncryptionScheme.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */