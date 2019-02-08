/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ECPrivateKeyParameters
/*    */   extends ECKeyParameters
/*    */ {
/*    */   BigInteger d;
/*    */   
/*    */   public ECPrivateKeyParameters(BigInteger d, ECDomainParameters params)
/*    */   {
/* 17 */     super(true, params);
/* 18 */     this.d = d;
/*    */   }
/*    */   
/*    */   public BigInteger getD()
/*    */   {
/* 23 */     return this.d;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ECPrivateKeyParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */