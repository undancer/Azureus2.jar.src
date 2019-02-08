/*    */ package org.gudy.bouncycastle.asn1.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Set;
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
/*    */   extends ASN1Encodable
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
/* 42 */     if (seq.size() != 2)
/*    */     {
/* 44 */       throw new IllegalArgumentException("Bad sequence size: " + seq.size());
/*    */     }
/*    */     
/* 47 */     this.attrType = DERObjectIdentifier.getInstance(seq.getObjectAt(0));
/* 48 */     this.attrValues = ASN1Set.getInstance(seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Attribute(DERObjectIdentifier attrType, ASN1Set attrValues)
/*    */   {
/* 55 */     this.attrType = attrType;
/* 56 */     this.attrValues = attrValues;
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getAttrType()
/*    */   {
/* 61 */     return this.attrType;
/*    */   }
/*    */   
/*    */   public ASN1Set getAttrValues()
/*    */   {
/* 66 */     return this.attrValues;
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
/*    */   public DERObject toASN1Object()
/*    */   {
/* 80 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 82 */     v.add(this.attrType);
/* 83 */     v.add(this.attrValues);
/*    */     
/* 85 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x509/Attribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */