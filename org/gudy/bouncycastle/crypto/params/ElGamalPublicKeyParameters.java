/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalPublicKeyParameters
/*    */   extends ElGamalKeyParameters
/*    */ {
/*    */   private BigInteger y;
/*    */   
/*    */   public ElGamalPublicKeyParameters(BigInteger y, ElGamalParameters params)
/*    */   {
/* 18 */     super(false, params);
/*    */     
/* 20 */     this.y = y;
/*    */   }
/*    */   
/*    */   public BigInteger getY()
/*    */   {
/* 25 */     return this.y;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 31 */     if (!(obj instanceof ElGamalPublicKeyParameters))
/*    */     {
/* 33 */       return false;
/*    */     }
/*    */     
/* 36 */     ElGamalPublicKeyParameters pKey = (ElGamalPublicKeyParameters)obj;
/*    */     
/* 38 */     if (!pKey.getY().equals(this.y))
/*    */     {
/* 40 */       return false;
/*    */     }
/*    */     
/* 43 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ElGamalPublicKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */