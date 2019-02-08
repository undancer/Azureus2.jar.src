/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ElGamalParameters
/*    */   implements CipherParameters
/*    */ {
/*    */   private BigInteger g;
/*    */   private BigInteger p;
/*    */   
/*    */   public ElGamalParameters(BigInteger p, BigInteger g)
/*    */   {
/* 18 */     this.g = g;
/* 19 */     this.p = p;
/*    */   }
/*    */   
/*    */   public BigInteger getP()
/*    */   {
/* 24 */     return this.p;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public BigInteger getG()
/*    */   {
/* 32 */     return this.g;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 38 */     if (!(obj instanceof ElGamalParameters))
/*    */     {
/* 40 */       return false;
/*    */     }
/*    */     
/* 43 */     ElGamalParameters pm = (ElGamalParameters)obj;
/*    */     
/* 45 */     return (pm.getP().equals(this.p)) && (pm.getG().equals(this.g));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/ElGamalParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */