/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RC2CBCParameter
/*    */   implements DEREncodable
/*    */ {
/*    */   DERInteger version;
/*    */   ASN1OctetString iv;
/*    */   
/*    */   public static RC2CBCParameter getInstance(Object o)
/*    */   {
/* 23 */     if ((o instanceof ASN1Sequence))
/*    */     {
/* 25 */       return new RC2CBCParameter((ASN1Sequence)o);
/*    */     }
/*    */     
/* 28 */     throw new IllegalArgumentException("unknown object in RC2CBCParameter factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public RC2CBCParameter(byte[] iv)
/*    */   {
/* 34 */     this.version = null;
/* 35 */     this.iv = new DEROctetString(iv);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RC2CBCParameter(int parameterVersion, byte[] iv)
/*    */   {
/* 42 */     this.version = new DERInteger(parameterVersion);
/* 43 */     this.iv = new DEROctetString(iv);
/*    */   }
/*    */   
/*    */ 
/*    */   public RC2CBCParameter(ASN1Sequence seq)
/*    */   {
/* 49 */     if (seq.size() == 1)
/*    */     {
/* 51 */       this.version = null;
/* 52 */       this.iv = ((ASN1OctetString)seq.getObjectAt(0));
/*    */     }
/*    */     else
/*    */     {
/* 56 */       this.version = ((DERInteger)seq.getObjectAt(0));
/* 57 */       this.iv = ((ASN1OctetString)seq.getObjectAt(1));
/*    */     }
/*    */   }
/*    */   
/*    */   public BigInteger getRC2ParameterVersion()
/*    */   {
/* 63 */     if (this.version == null)
/*    */     {
/* 65 */       return null;
/*    */     }
/*    */     
/* 68 */     return this.version.getValue();
/*    */   }
/*    */   
/*    */   public byte[] getIV()
/*    */   {
/* 73 */     return this.iv.getOctets();
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 78 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 80 */     if (this.version != null)
/*    */     {
/* 82 */       v.add(this.version);
/*    */     }
/*    */     
/* 85 */     v.add(this.iv);
/*    */     
/* 87 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/RC2CBCParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */