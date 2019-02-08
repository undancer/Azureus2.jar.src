/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.BERSequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ 
/*    */ 
/*    */ public class AuthenticatedSafe
/*    */   implements DEREncodable
/*    */ {
/*    */   ContentInfo[] info;
/*    */   
/*    */   public AuthenticatedSafe(ASN1Sequence seq)
/*    */   {
/* 17 */     this.info = new ContentInfo[seq.size()];
/*    */     
/* 19 */     for (int i = 0; i != this.info.length; i++)
/*    */     {
/* 21 */       this.info[i] = ContentInfo.getInstance(seq.getObjectAt(i));
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public AuthenticatedSafe(ContentInfo[] info)
/*    */   {
/* 28 */     this.info = info;
/*    */   }
/*    */   
/*    */   public ContentInfo[] getContentInfo()
/*    */   {
/* 33 */     return this.info;
/*    */   }
/*    */   
/*    */   public DERObject getDERObject()
/*    */   {
/* 38 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 40 */     for (int i = 0; i != this.info.length; i++)
/*    */     {
/* 42 */       v.add(this.info[i]);
/*    */     }
/*    */     
/* 45 */     return new BERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/AuthenticatedSafe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */