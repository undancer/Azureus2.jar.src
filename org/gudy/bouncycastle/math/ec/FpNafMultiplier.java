/*    */ package org.gudy.bouncycastle.math.ec;
/*    */ 
/*    */ import java.math.BigInteger;
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
/*    */ class FpNafMultiplier
/*    */   implements ECMultiplier
/*    */ {
/*    */   public ECPoint multiply(ECPoint p, BigInteger k, PreCompInfo preCompInfo)
/*    */   {
/* 20 */     BigInteger e = k;
/* 21 */     BigInteger h = e.multiply(BigInteger.valueOf(3L));
/*    */     
/* 23 */     ECPoint neg = p.negate();
/* 24 */     ECPoint R = p;
/*    */     
/* 26 */     for (int i = h.bitLength() - 2; i > 0; i--)
/*    */     {
/* 28 */       R = R.twice();
/*    */       
/* 30 */       boolean hBit = h.testBit(i);
/* 31 */       boolean eBit = e.testBit(i);
/*    */       
/* 33 */       if (hBit != eBit)
/*    */       {
/* 35 */         R = R.add(hBit ? p : neg);
/*    */       }
/*    */     }
/*    */     
/* 39 */     return R;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/math/ec/FpNafMultiplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */