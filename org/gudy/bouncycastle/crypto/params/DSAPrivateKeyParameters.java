/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAPrivateKeyParameters
/*    */   extends DSAKeyParameters
/*    */ {
/*    */   private BigInteger x;
/*    */   
/*    */   public DSAPrivateKeyParameters(BigInteger x, DSAParameters params)
/*    */   {
/* 17 */     super(true, params);
/*    */     
/* 19 */     this.x = x;
/*    */   }
/*    */   
/*    */   public BigInteger getX()
/*    */   {
/* 24 */     return this.x;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DSAPrivateKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */