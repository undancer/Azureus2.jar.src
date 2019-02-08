/*    */ package org.gudy.bouncycastle.x509;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1Encodable;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Set;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ import org.gudy.bouncycastle.asn1.DERSet;
/*    */ import org.gudy.bouncycastle.asn1.x509.Attribute;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X509Attribute
/*    */   extends ASN1Encodable
/*    */ {
/*    */   Attribute attr;
/*    */   
/*    */   X509Attribute(ASN1Encodable at)
/*    */   {
/* 25 */     this.attr = Attribute.getInstance(at);
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
/*    */   public X509Attribute(String oid, ASN1Encodable value)
/*    */   {
/* 39 */     this.attr = new Attribute(new DERObjectIdentifier(oid), new DERSet(value));
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
/*    */   public X509Attribute(String oid, ASN1EncodableVector value)
/*    */   {
/* 53 */     this.attr = new Attribute(new DERObjectIdentifier(oid), new DERSet(value));
/*    */   }
/*    */   
/*    */   public String getOID()
/*    */   {
/* 58 */     return this.attr.getAttrType().getId();
/*    */   }
/*    */   
/*    */   public ASN1Encodable[] getValues()
/*    */   {
/* 63 */     ASN1Set s = this.attr.getAttrValues();
/* 64 */     ASN1Encodable[] values = new ASN1Encodable[s.size()];
/*    */     
/* 66 */     for (int i = 0; i != s.size(); i++)
/*    */     {
/* 68 */       values[i] = ((ASN1Encodable)s.getObjectAt(i));
/*    */     }
/*    */     
/* 71 */     return values;
/*    */   }
/*    */   
/*    */   public DERObject toASN1Object()
/*    */   {
/* 76 */     return this.attr.toASN1Object();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/x509/X509Attribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */