/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAPublicKeyParameters
/*    */   extends DSAKeyParameters
/*    */ {
/*    */   private BigInteger y;
/*    */   
/*    */   public DSAPublicKeyParameters(BigInteger y, DSAParameters params)
/*    */   {
/* 17 */     super(false, params);
/*    */     
/* 19 */     this.y = y;
/*    */   }
/*    */   
/*    */   public BigInteger getY()
/*    */   {
/* 24 */     return this.y;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DSAPublicKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */