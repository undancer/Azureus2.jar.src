/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ import org.gudy.bouncycastle.asn1.DERTaggedObject;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OtherInfo
/*    */   implements DEREncodable
/*    */ {
/*    */   private KeySpecificInfo keyInfo;
/*    */   private ASN1OctetString partyAInfo;
/*    */   private ASN1OctetString suppPubInfo;
/*    */   
/*    */   public OtherInfo(KeySpecificInfo keyInfo, ASN1OctetString partyAInfo, ASN1OctetString suppPubInfo)
/*    */   {
/* 29 */     this.keyInfo = keyInfo;
/* 30 */     this.partyAInfo = partyAInfo;
/* 31 */     this.suppPubInfo = suppPubInfo;
/*    */   }
/*    */   
/*    */ 
/*    */   public OtherInfo(ASN1Sequence seq)
/*    */   {
/* 37 */     Enumeration e = seq.getObjects();
/*    */     
/* 39 */     this.keyInfo = new KeySpecificInfo((ASN1Sequence)e.nextElement());
/*    */     
/* 41 */     while (e.hasMoreElements())
/*    */     {
/* 43 */       DERTaggedObject o = (DERTaggedObject)e.nextElement();
/*    */       
/* 45 */       if (o.getTagNo() == 0)
/*    */       {
/* 47 */         this.partyAInfo = ((ASN1OctetString)o.getObject());
/*    */       }
/* 49 */       else if (o.getTagNo() == 2)
/*    */       {
/* 51 */         this.suppPubInfo = ((ASN1OctetString)o.getObject());
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public KeySpecificInfo getKeyInfo()
/*    */   {
/* 58 */     return this.keyInfo;
/*    */   }
/*    */   
/*    */   public ASN1OctetString getPartyAInfo()
/*    */   {
/* 63 */     return this.partyAInfo;
/*    */   }
/*    */   
/*    */   public ASN1OctetString getSuppPubInfo()
/*    */   {
/* 68 */     return this.suppPubInfo;
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
/*    */   public DERObject getDERObject()
/*    */   {
/* 83 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 85 */     v.add(this.keyInfo);
/*    */     
/* 87 */     if (this.partyAInfo != null)
/*    */     {
/* 89 */       v.add(new DERTaggedObject(0, this.partyAInfo));
/*    */     }
/*    */     
/* 92 */     v.add(new DERTaggedObject(2, this.suppPubInfo));
/*    */     
/* 94 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/OtherInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */