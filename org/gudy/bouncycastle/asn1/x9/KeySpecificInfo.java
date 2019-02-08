/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSequence;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KeySpecificInfo
/*    */   implements DEREncodable
/*    */ {
/*    */   private DERObjectIdentifier algorithm;
/*    */   private ASN1OctetString counter;
/*    */   
/*    */   public KeySpecificInfo(DERObjectIdentifier algorithm, ASN1OctetString counter)
/*    */   {
/* 27 */     this.algorithm = algorithm;
/* 28 */     this.counter = counter;
/*    */   }
/*    */   
/*    */ 
/*    */   public KeySpecificInfo(ASN1Sequence seq)
/*    */   {
/* 34 */     Enumeration e = seq.getObjects();
/*    */     
/* 36 */     this.algorithm = ((DERObjectIdentifier)e.nextElement());
/* 37 */     this.counter = ((ASN1OctetString)e.nextElement());
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getAlgorithm()
/*    */   {
/* 42 */     return this.algorithm;
/*    */   }
/*    */   
/*    */   public ASN1OctetString getCounter()
/*    */   {
/* 47 */     return this.counter;
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
/*    */   public DERObject getDERObject()
/*    */   {
/* 61 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 63 */     v.add(this.algorithm);
/* 64 */     v.add(this.counter);
/*    */     
/* 66 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/KeySpecificInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */