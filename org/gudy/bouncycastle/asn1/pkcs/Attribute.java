/*    */ package org.gudy.bouncycastle.asn1.pkcs;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Set;
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
/*    */ public class Attribute
/*    */   implements DEREncodable
/*    */ {
/*    */   private DERObjectIdentifier attrType;
/*    */   private ASN1Set attrValues;
/*    */   
/*    */   public static Attribute getInstance(Object o)
/*    */   {
/* 26 */     if ((o == null) || ((o instanceof Attribute)))
/*    */     {
/* 28 */       return (Attribute)o;
/*    */     }
/*    */     
/* 31 */     if ((o instanceof ASN1Sequence))
/*    */     {
/* 33 */       return new Attribute((ASN1Sequence)o);
/*    */     }
/*    */     
/* 36 */     throw new IllegalArgumentException("unknown object in factory");
/*    */   }
/*    */   
/*    */ 
/*    */   public Attribute(ASN1Sequence seq)
/*    */   {
/* 42 */     this.attrType = ((DERObjectIdentifier)seq.getObjectAt(0));
/* 43 */     this.attrValues = ((ASN1Set)seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Attribute(DERObjectIdentifier attrType, ASN1Set attrValues)
/*    */   {
/* 50 */     this.attrType = attrType;
/* 51 */     this.attrValues = attrValues;
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getAttrType()
/*    */   {
/* 56 */     return this.attrType;
/*    */   }
/*    */   
/*    */   public ASN1Set getAttrValues()
/*    */   {
/* 61 */     return this.attrValues;
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
/* 75 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 77 */     v.add(this.attrType);
/* 78 */     v.add(this.attrValues);
/*    */     
/* 80 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/pkcs/Attribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */