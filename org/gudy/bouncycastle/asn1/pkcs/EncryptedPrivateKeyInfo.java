/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EncryptedPrivateKeyInfo
/*    */   implements PKCSObjectIdentifiers, DEREncodable
/*    */ {
/*    */   private AlgorithmIdentifier algId;
/*    */   private ASN1OctetString data;
/*    */   
/*    */   public EncryptedPrivateKeyInfo(ASN1Sequence seq)
/*    */   {
/* 23 */     Enumeration e = seq.getObjects();
/*    */     
/* 25 */     this.algId = new AlgorithmIdentifier((ASN1Sequence)e.nextElement());
/* 26 */     this.data = ((ASN1OctetString)e.nextElement());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public EncryptedPrivateKeyInfo(AlgorithmIdentifier algId, byte[] encoding)
/*    */   {
/* 33 */     this.algId = algId;
/* 34 */     this.data = new DEROctetString(encoding);
/*    */   }
/*    */   
/*    */   public AlgorithmIdentifier getEncryptionAlgorithm()
/*    */   {
/* 39 */     return this.algId;
/*    */   }
/*    */   
/*    */   public byte[] getEncryptedData()
/*    */   {
/* 44 */     return this.data.getOctets();
/*    */   }
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERObject getDERObject()
/*    */   {
/* 64 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 66 */     v.add(this.algId);
/* 67 */     v.add(this.data);
/*    */     
/* 69 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/EncryptedPrivateKeyInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */