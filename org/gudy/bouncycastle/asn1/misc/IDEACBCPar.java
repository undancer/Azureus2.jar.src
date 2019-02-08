/*    */ package org.gudy.bouncycastle.asn1.misc;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ 
/*    */ public class IDEACBCPar implements org.gudy.bouncycastle.asn1.DEREncodable
/*    */ {
/*    */   ASN1OctetString iv;
/*    */   
/*    */   public static IDEACBCPar getInstance(Object o)
/*    */   {
/* 13 */     if ((o instanceof IDEACBCPar))
/*    */     {
/* 15 */       return (IDEACBCPar)o;
/*    */     }
/* 17 */     if ((o instanceof ASN1Sequence))
/*    */     {
/* 19 */       return new IDEACBCPar((ASN1Sequence)o);
/*    */     }
/*    */     
/* 22 */     throw new IllegalArgumentException("unknown object in IDEACBCPar factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public IDEACBCPar(byte[] iv)
/*    */   {
/* 28 */     this.iv = new org.gudy.bouncycastle.asn1.DEROctetString(iv);
/*    */   }
/*    */   
/*    */ 
/*    */   public IDEACBCPar(ASN1Sequence seq)
/*    */   {
/* 34 */     if (seq.size() == 1)
/*    */     {
/* 36 */       this.iv = ((ASN1OctetString)seq.getObjectAt(0));
/*    */     }
/*    */     else
/*    */     {
/* 40 */       this.iv = null;
/*    */     }
/*    */   }
/*    */   
/*    */   public byte[] getIV()
/*    */   {
/* 46 */     if (this.iv != null)
/*    */     {
/* 48 */       return this.iv.getOctets();
/*    */     }
/*    */     
/*    */ 
/* 52 */     return null;
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
/*    */   public org.gudy.bouncycastle.asn1.DERObject getDERObject()
/*    */   {
/* 66 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 68 */     if (this.iv != null)
/*    */     {
/* 70 */       v.add(this.iv);
/*    */     }
/*    */     
/* 73 */     return new org.gudy.bouncycastle.asn1.DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/misc/IDEACBCPar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */