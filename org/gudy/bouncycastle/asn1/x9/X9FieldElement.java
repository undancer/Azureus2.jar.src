/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.math.ec.ECFieldElement;
/*    */ import org.gudy.bouncycastle.math.ec.ECFieldElement.Fp;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X9FieldElement
/*    */   implements DEREncodable
/*    */ {
/*    */   private ECFieldElement f;
/*    */   
/*    */   public X9FieldElement(ECFieldElement f)
/*    */   {
/* 22 */     this.f = f;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public X9FieldElement(boolean fP, BigInteger q, ASN1OctetString s)
/*    */   {
/* 30 */     if (fP)
/*    */     {
/* 32 */       this.f = new ECFieldElement.Fp(q, new BigInteger(1, s.getOctets()));
/*    */     }
/*    */     else
/*    */     {
/* 36 */       throw new RuntimeException("not implemented");
/*    */     }
/*    */   }
/*    */   
/*    */   public ECFieldElement getValue()
/*    */   {
/* 42 */     return this.f;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DERObject getDERObject()
/*    */   {
/* 63 */     return new DEROctetString(this.f.toBigInteger().toByteArray());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9FieldElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */