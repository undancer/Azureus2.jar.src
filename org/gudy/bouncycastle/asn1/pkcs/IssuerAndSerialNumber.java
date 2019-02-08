/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.x509.X509Name;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IssuerAndSerialNumber
/*    */   implements DEREncodable
/*    */ {
/*    */   X509Name name;
/*    */   DERInteger certSerialNumber;
/*    */   
/*    */   public static IssuerAndSerialNumber getInstance(Object obj)
/*    */   {
/* 22 */     if ((obj instanceof IssuerAndSerialNumber))
/*    */     {
/* 24 */       return (IssuerAndSerialNumber)obj;
/*    */     }
/* 26 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 28 */       return new IssuerAndSerialNumber((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 31 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public IssuerAndSerialNumber(ASN1Sequence seq)
/*    */   {
/* 37 */     this.name = X509Name.getInstance(seq.getObjectAt(0));
/* 38 */     this.certSerialNumber = ((DERInteger)seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public IssuerAndSerialNumber(X509Name name, BigInteger certSerialNumber)
/*    */   {
/* 45 */     this.name = name;
/* 46 */     this.certSerialNumber = new DERInteger(certSerialNumber);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public IssuerAndSerialNumber(X509Name name, DERInteger certSerialNumber)
/*    */   {
/* 53 */     this.name = name;
/* 54 */     this.certSerialNumber = certSerialNumber;
/*    */   }
/*    */   
/*    */   public X509Name getName()
/*    */   {
/* 59 */     return this.name;
/*    */   }
/*    */   
/*    */   public DERInteger getCertificateSerialNumber()
/*    */   {
/* 64 */     return this.certSerialNumber;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 69 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 71 */     v.add(this.name);
/* 72 */     v.add(this.certSerialNumber);
/*    */     
/* 74 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/IssuerAndSerialNumber.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */