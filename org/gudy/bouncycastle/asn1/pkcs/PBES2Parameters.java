/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PBES2Parameters
/*    */   implements PKCSObjectIdentifiers, DEREncodable
/*    */ {
/*    */   private KeyDerivationFunc func;
/*    */   private EncryptionScheme scheme;
/*    */   
/*    */   public PBES2Parameters(ASN1Sequence obj)
/*    */   {
/* 20 */     Enumeration e = obj.getObjects();
/* 21 */     ASN1Sequence funcSeq = (ASN1Sequence)e.nextElement();
/*    */     
/* 23 */     if (funcSeq.getObjectAt(0).equals(id_PBKDF2))
/*    */     {
/* 25 */       this.func = new PBKDF2Params(funcSeq);
/*    */     }
/*    */     else
/*    */     {
/* 29 */       this.func = new KeyDerivationFunc(funcSeq);
/*    */     }
/*    */     
/* 32 */     this.scheme = new EncryptionScheme((ASN1Sequence)e.nextElement());
/*    */   }
/*    */   
/*    */   public KeyDerivationFunc getKeyDerivationFunc()
/*    */   {
/* 37 */     return this.func;
/*    */   }
/*    */   
/*    */   public EncryptionScheme getEncryptionScheme()
/*    */   {
/* 42 */     return this.scheme;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 47 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 49 */     v.add(this.func);
/* 50 */     v.add(this.scheme);
/*    */     
/* 52 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/PBES2Parameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */