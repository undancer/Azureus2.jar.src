/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PBKDF2Params
/*    */   extends KeyDerivationFunc
/*    */ {
/*    */   DERObjectIdentifier id;
/*    */   ASN1OctetString octStr;
/*    */   DERInteger iterationCount;
/*    */   DERInteger keyLength;
/*    */   
/*    */   PBKDF2Params(ASN1Sequence seq)
/*    */   {
/* 25 */     super(seq);
/*    */     
/* 27 */     Enumeration e = seq.getObjects();
/*    */     
/* 29 */     this.id = ((DERObjectIdentifier)e.nextElement());
/*    */     
/* 31 */     ASN1Sequence params = (ASN1Sequence)e.nextElement();
/*    */     
/* 33 */     e = params.getObjects();
/*    */     
/* 35 */     this.octStr = ((ASN1OctetString)e.nextElement());
/* 36 */     this.iterationCount = ((DERInteger)e.nextElement());
/*    */     
/* 38 */     if (e.hasMoreElements())
/*    */     {
/* 40 */       this.keyLength = ((DERInteger)e.nextElement());
/*    */     }
/*    */     else
/*    */     {
/* 44 */       this.keyLength = null;
/*    */     }
/*    */   }
/*    */   
/*    */   public byte[] getSalt()
/*    */   {
/* 50 */     return this.octStr.getOctets();
/*    */   }
/*    */   
/*    */   public BigInteger getIterationCount()
/*    */   {
/* 55 */     return this.iterationCount.getValue();
/*    */   }
/*    */   
/*    */   public BigInteger getKeyLength()
/*    */   {
/* 60 */     if (this.keyLength != null)
/*    */     {
/* 62 */       return this.keyLength.getValue();
/*    */     }
/*    */     
/* 65 */     return null;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 70 */     ASN1EncodableVector v = new ASN1EncodableVector();
/* 71 */     ASN1EncodableVector subV = new ASN1EncodableVector();
/*    */     
/* 73 */     v.add(this.id);
/* 74 */     subV.add(this.octStr);
/* 75 */     subV.add(this.iterationCount);
/*    */     
/* 77 */     if (this.keyLength != null)
/*    */     {
/* 79 */       subV.add(this.keyLength);
/*    */     }
/*    */     
/* 82 */     v.add(new DERSequence(subV));
/*    */     
/* 84 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/PBKDF2Params.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */