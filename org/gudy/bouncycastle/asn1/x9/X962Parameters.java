/*    */ package org.gudy.bouncycastle.asn1.x9;
/*    */ 
/*    */ import org.gudy.bouncycastle.asn1.DEREncodable;
/*    */ import org.gudy.bouncycastle.asn1.DERObject;
/*    */ import org.gudy.bouncycastle.asn1.DERObjectIdentifier;
/*    */ 
/*    */ public class X962Parameters
/*    */   implements DEREncodable
/*    */ {
/* 10 */   private DERObject params = null;
/*    */   
/*    */ 
/*    */   public X962Parameters(X9ECParameters ecParameters)
/*    */   {
/* 15 */     this.params = ecParameters.getDERObject();
/*    */   }
/*    */   
/*    */ 
/*    */   public X962Parameters(DERObjectIdentifier namedCurve)
/*    */   {
/* 21 */     this.params = namedCurve;
/*    */   }
/*    */   
/*    */ 
/*    */   public X962Parameters(DERObject obj)
/*    */   {
/* 27 */     this.params = obj;
/*    */   }
/*    */   
/*    */   public boolean isNamedCurve()
/*    */   {
/* 32 */     return this.params instanceof DERObjectIdentifier;
/*    */   }
/*    */   
/*    */   public DERObject getParameters()
/*    */   {
/* 37 */     return this.params;
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
/* 52 */     return this.params;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/asn1/x9/X962Parameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */