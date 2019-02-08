/*    */ package org.gudy.bouncycastle.jce.spec;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.math.ec.ECCurve;
/*    */ import org.gudy.bouncycastle.math.ec.ECPoint;
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
/*    */ public class ECNamedCurveParameterSpec
/*    */   extends ECParameterSpec
/*    */ {
/*    */   private String name;
/*    */   
/*    */   public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n)
/*    */   {
/* 24 */     super(curve, G, n);
/*    */     
/* 26 */     this.name = name;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h)
/*    */   {
/* 36 */     super(curve, G, n, h);
/*    */     
/* 38 */     this.name = name;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ECNamedCurveParameterSpec(String name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed)
/*    */   {
/* 49 */     super(curve, G, n, h, seed);
/*    */     
/* 51 */     this.name = name;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getName()
/*    */   {
/* 59 */     return this.name;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/jce/spec/ECNamedCurveParameterSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */