/*    */ package org.gudy.bouncycastle.crypto.params;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DSAParameters
/*    */   implements CipherParameters
/*    */ {
/*    */   private BigInteger g;
/*    */   private BigInteger q;
/*    */   private BigInteger p;
/*    */   private DSAValidationParameters validation;
/*    */   
/*    */   public DSAParameters(BigInteger p, BigInteger q, BigInteger g)
/*    */   {
/* 22 */     this.g = g;
/* 23 */     this.p = p;
/* 24 */     this.q = q;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DSAParameters(BigInteger p, BigInteger q, BigInteger g, DSAValidationParameters params)
/*    */   {
/* 33 */     this.g = g;
/* 34 */     this.p = p;
/* 35 */     this.q = q;
/* 36 */     this.validation = params;
/*    */   }
/*    */   
/*    */   public BigInteger getP()
/*    */   {
/* 41 */     return this.p;
/*    */   }
/*    */   
/*    */   public BigInteger getQ()
/*    */   {
/* 46 */     return this.q;
/*    */   }
/*    */   
/*    */   public BigInteger getG()
/*    */   {
/* 51 */     return this.g;
/*    */   }
/*    */   
/*    */   public DSAValidationParameters getValidationParameters()
/*    */   {
/* 56 */     return this.validation;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 62 */     if (!(obj instanceof DSAParameters))
/*    */     {
/* 64 */       return false;
/*    */     }
/*    */     
/* 67 */     DSAParameters pm = (DSAParameters)obj;
/*    */     
/* 69 */     return (pm.getP().equals(this.p)) && (pm.getQ().equals(this.q)) && (pm.getG().equals(this.g));
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DSAParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */