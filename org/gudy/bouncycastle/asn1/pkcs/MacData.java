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
/*    */ import org.gudy.bouncycastle.asn1.x509.DigestInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MacData
/*    */   implements DEREncodable
/*    */ {
/*    */   DigestInfo digInfo;
/*    */   byte[] salt;
/*    */   BigInteger iterationCount;
/*    */   
/*    */   public static MacData getInstance(Object obj)
/*    */   {
/* 25 */     if ((obj instanceof MacData))
/*    */     {
/* 27 */       return (MacData)obj;
/*    */     }
/* 29 */     if ((obj instanceof ASN1Sequence))
/*    */     {
/* 31 */       return new MacData((ASN1Sequence)obj);
/*    */     }
/*    */     
/* 34 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public MacData(ASN1Sequence seq)
/*    */   {
/* 40 */     this.digInfo = DigestInfo.getInstance(seq.getObjectAt(0));
/*    */     
/* 42 */     this.salt = ((ASN1OctetString)seq.getObjectAt(1)).getOctets();
/*    */     
/* 44 */     if (seq.size() == 3)
/*    */     {
/* 46 */       this.iterationCount = ((DERInteger)seq.getObjectAt(2)).getValue();
/*    */     }
/*    */     else
/*    */     {
/* 50 */       this.iterationCount = BigInteger.valueOf(1L);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public MacData(DigestInfo digInfo, byte[] salt, int iterationCount)
/*    */   {
/* 59 */     this.digInfo = digInfo;
/* 60 */     this.salt = salt;
/* 61 */     this.iterationCount = BigInteger.valueOf(iterationCount);
/*    */   }
/*    */   
/*    */   public DigestInfo getMac()
/*    */   {
/* 66 */     return this.digInfo;
/*    */   }
/*    */   
/*    */   public byte[] getSalt()
/*    */   {
/* 71 */     return this.salt;
/*    */   }
/*    */   
/*    */   public BigInteger getIterationCount()
/*    */   {
/* 76 */     return this.iterationCount;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 81 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 83 */     v.add(this.digInfo);
/* 84 */     v.add(new DEROctetString(this.salt));
/* 85 */     v.add(new DERInteger(this.iterationCount));
/*    */     
/* 87 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/MacData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */