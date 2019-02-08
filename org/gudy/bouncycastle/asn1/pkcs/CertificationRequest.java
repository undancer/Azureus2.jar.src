/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERBitString;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CertificationRequest
/*    */   implements DEREncodable
/*    */ {
/* 24 */   protected CertificationRequestInfo reqInfo = null;
/* 25 */   protected AlgorithmIdentifier sigAlgId = null;
/* 26 */   protected DERBitString sigBits = null;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected CertificationRequest() {}
/*    */   
/*    */ 
/*    */ 
/*    */   public CertificationRequest(CertificationRequestInfo requestInfo, AlgorithmIdentifier algorithm, DERBitString signature)
/*    */   {
/* 37 */     this.reqInfo = requestInfo;
/* 38 */     this.sigAlgId = algorithm;
/* 39 */     this.sigBits = signature;
/*    */   }
/*    */   
/*    */ 
/*    */   public CertificationRequest(ASN1Sequence seq)
/*    */   {
/* 45 */     this.reqInfo = CertificationRequestInfo.getInstance(seq.getObjectAt(0));
/* 46 */     this.sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
/* 47 */     this.sigBits = ((DERBitString)seq.getObjectAt(2));
/*    */   }
/*    */   
/*    */   public CertificationRequestInfo getCertificationRequestInfo()
/*    */   {
/* 52 */     return this.reqInfo;
/*    */   }
/*    */   
/*    */   public AlgorithmIdentifier getSignatureAlgorithm()
/*    */   {
/* 57 */     return this.sigAlgId;
/*    */   }
/*    */   
/*    */   public DERBitString getSignature()
/*    */   {
/* 62 */     return this.sigBits;
/*    */   }
/*    */   
/*    */ 
/*    */   public DERObject getDERObject()
/*    */   {
/* 68 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 70 */     v.add(this.reqInfo);
/* 71 */     v.add(this.sigAlgId);
/* 72 */     v.add(this.sigBits);
/*    */     
/* 74 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/CertificationRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */