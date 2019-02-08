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
/*    */ public class DHPrivateKeyParameters
/*    */   extends DHKeyParameters
/*    */ {
/*    */   private BigInteger x;
/*    */   
/*    */   public DHPrivateKeyParameters(BigInteger x, DHParameters params)
/*    */   {
/* 18 */     super(true, params);
/*    */     
/* 20 */     this.x = x;
/*    */   }
/*    */   
/*    */   public BigInteger getX()
/*    */   {
/* 25 */     return this.x;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 31 */     if (!(obj instanceof DHPrivateKeyParameters))
/*    */     {
/* 33 */       return false;
/*    */     }
/*    */     
/* 36 */     DHPrivateKeyParameters pKey = (DHPrivateKeyParameters)obj;
/*    */     
/* 38 */     if (!pKey.getX().equals(this.x))
/*    */     {
/* 40 */       return false;
/*    */     }
/*    */     
/* 43 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DHPrivateKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */