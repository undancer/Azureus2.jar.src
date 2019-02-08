/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.BERSequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Pfx
/*    */   implements DEREncodable, PKCSObjectIdentifiers
/*    */ {
/*    */   private ContentInfo contentInfo;
/* 19 */   private MacData macData = null;
/*    */   
/*    */ 
/*    */   public Pfx(ASN1Sequence seq)
/*    */   {
/* 24 */     BigInteger version = ((DERInteger)seq.getObjectAt(0)).getValue();
/* 25 */     if (version.intValue() != 3)
/*    */     {
/* 27 */       throw new IllegalArgumentException("wrong version for PFX PDU");
/*    */     }
/*    */     
/* 30 */     this.contentInfo = ContentInfo.getInstance(seq.getObjectAt(1));
/*    */     
/* 32 */     if (seq.size() == 3)
/*    */     {
/* 34 */       this.macData = MacData.getInstance(seq.getObjectAt(2));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Pfx(ContentInfo contentInfo, MacData macData)
/*    */   {
/* 42 */     this.contentInfo = contentInfo;
/* 43 */     this.macData = macData;
/*    */   }
/*    */   
/*    */   public ContentInfo getAuthSafe()
/*    */   {
/* 48 */     return this.contentInfo;
/*    */   }
/*    */   
/*    */   public MacData getMacData()
/*    */   {
/* 53 */     return this.macData;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 58 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 60 */     v.add(new DERInteger(3));
/* 61 */     v.add(this.contentInfo);
/*    */     
/* 63 */     if (this.macData != null)
/*    */     {
/* 65 */       v.add(this.macData);
/*    */     }
/*    */     
/* 68 */     return new BERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/Pfx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */