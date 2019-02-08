/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1EncodableVector;
/*    */ import org.gudy.bouncycastle.asn1.ASN1Sequence;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERInteger;
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
/*    */ public class X9FieldID
/*    */   implements DEREncodable, X9ObjectIdentifiers
/*    */ {
/*    */   private DERObjectIdentifier id;
/*    */   private DERObject parameters;
/*    */   
/*    */   public X9FieldID(DERObjectIdentifier id, BigInteger primeP)
/*    */   {
/* 27 */     this.id = id;
/* 28 */     this.parameters = new DERInteger(primeP);
/*    */   }
/*    */   
/*    */ 
/*    */   public X9FieldID(ASN1Sequence seq)
/*    */   {
/* 34 */     this.id = ((DERObjectIdentifier)seq.getObjectAt(0));
/* 35 */     this.parameters = ((DERObject)seq.getObjectAt(1));
/*    */   }
/*    */   
/*    */   public DERObjectIdentifier getIdentifier()
/*    */   {
/* 40 */     return this.id;
/*    */   }
/*    */   
/*    */   public DERObject getParameters()
/*    */   {
/* 45 */     return this.parameters;
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
/* 59 */     ASN1EncodableVector v = new ASN1EncodableVector();
/*    */     
/* 61 */     v.add(this.id);
/* 62 */     v.add(this.parameters);
/*    */     
/* 64 */     return new DERSequence(v);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9FieldID.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */