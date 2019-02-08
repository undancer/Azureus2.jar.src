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
/*    */ public class DHPublicKeyParameters
/*    */   extends DHKeyParameters
/*    */ {
/*    */   private BigInteger y;
/*    */   
/*    */   public DHPublicKeyParameters(BigInteger y, DHParameters params)
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
/* 31 */     if (!(obj instanceof DHPublicKeyParameters))
/*    */     {
/* 33 */       return false;
/*    */     }
/*    */     
/* 36 */     DHPublicKeyParameters pKey = (DHPublicKeyParameters)obj;
/*    */     
/* 38 */     if (!pKey.getY().equals(this.y))
/*    */     {
/* 40 */       return false;
/*    */     }
/*    */     
/* 43 */     return super.equals(obj);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DHPublicKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */