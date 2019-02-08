/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.x509.AlgorithmIdentifier;
/*    */ 
/*    */ 
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public class PBES2Algorithms
/*    */   extends AlgorithmIdentifier
/*    */   implements PKCSObjectIdentifiers
/*    */ {
/*    */   private DERObjectIdentifier objectId;
/*    */   private KeyDerivationFunc func;
/*    */   private EncryptionScheme scheme;
/*    */   
/*    */   public PBES2Algorithms(ASN1Sequence obj)
/*    */   {
/* 25 */     super(obj);
/*    */     
/* 27 */     Enumeration e = obj.getObjects();
/*    */     
/* 29 */     this.objectId = ((DERObjectIdentifier)e.nextElement());
/*    */     
/* 31 */     ASN1Sequence seq = (ASN1Sequence)e.nextElement();
/*    */     
/* 33 */     e = seq.getObjects();
/*    */     
/* 35 */     ASN1Sequence funcSeq = (ASN1Sequence)e.nextElement();
/*    */     
/* 37 */     if (funcSeq.getObjectAt(0).equals(id_PBKDF2))
/*    */     {
/* 39 */       this.func = new PBKDF2Params(funcSeq);
/*    */     }
/*    */     else
/*    */     {
/* 43 */       this.func = new KeyDerivationFunc(funcSeq);
/*    */     }
/*    */     
/* 46 */     this.scheme = new EncryptionScheme((ASN1Sequence)e.nextElement());
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getObjectId()
/*    */   {
/* 51 */     return this.objectId;
/*    */   }
/*    */   
/*    */   public KeyDerivationFunc getKeyDerivationFunc()
/*    */   {
/* 56 */     return this.func;
/*    */   }
/*    */   
/*    */   public EncryptionScheme getEncryptionScheme()
/*    */   {
/* 61 */     return this.scheme;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 66 */     ASN1EncodableVector v = new ASN1EncodableVector();
/* 67 */     ASN1EncodableVector subV = new ASN1EncodableVector();
/*    */     
/* 69 */     v.add(this.objectId);
/*    */     
/* 71 */     subV.add(this.func);
/* 72 */     subV.add(this.scheme);
/* 73 */     v.add(new DERSequence(subV));
/*    */     
/* 75 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/PBES2Algorithms.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */