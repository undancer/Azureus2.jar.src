/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.ASN1OctetString;
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DEROctetString;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class X9ECPoint
/*    */   implements DEREncodable
/*    */ {
/*    */   ECPoint p;
/*    */   
/*    */   public X9ECPoint(ECPoint p)
/*    */   {
/* 21 */     this.p = p;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public X9ECPoint(ECCurve c, ASN1OctetString s)
/*    */   {
/* 28 */     this.p = c.decodePoint(s.getOctets());
/*    */   }
/*    */   
/*    */   public ECPoint getPoint()
/*    */   {
/* 33 */     return this.p;
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
/*    */   public DERObject getDERObject()
/*    */   {
/* 46 */     return new DEROctetString(this.p.getEncoded());
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X9ECPoint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */