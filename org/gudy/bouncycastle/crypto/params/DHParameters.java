/*     */ package org.gudy.bouncycastle.crypto.params;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHParameters
/*     */   implements CipherParameters
/*     */ {
/*     */   private BigInteger g;
/*     */   private BigInteger p;
/*     */   private BigInteger q;
/*     */   private int j;
/*     */   private DHValidationParameters validation;
/*     */   
/*     */   public DHParameters(BigInteger p, BigInteger g)
/*     */   {
/*  22 */     this.g = g;
/*  23 */     this.p = p;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHParameters(BigInteger p, BigInteger g, BigInteger q, int j)
/*     */   {
/*  32 */     this.g = g;
/*  33 */     this.p = p;
/*  34 */     this.q = q;
/*  35 */     this.j = j;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DHParameters(BigInteger p, BigInteger g, BigInteger q, int j, DHValidationParameters validation)
/*     */   {
/*  45 */     this.g = g;
/*  46 */     this.p = p;
/*  47 */     this.q = q;
/*  48 */     this.j = j;
/*     */   }
/*     */   
/*     */   public BigInteger getP()
/*     */   {
/*  53 */     return this.p;
/*     */   }
/*     */   
/*     */   public BigInteger getG()
/*     */   {
/*  58 */     return this.g;
/*     */   }
/*     */   
/*     */   public BigInteger getQ()
/*     */   {
/*  63 */     return this.q;
/*     */   }
/*     */   
/*     */   public int getJ()
/*     */   {
/*  68 */     return this.j;
/*     */   }
/*     */   
/*     */   public DHValidationParameters getValidationParameters()
/*     */   {
/*  73 */     return this.validation;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  79 */     if (!(obj instanceof DHParameters))
/*     */     {
/*  81 */       return false;
/*     */     }
/*     */     
/*  84 */     DHParameters pm = (DHParameters)obj;
/*     */     
/*  86 */     if (getValidationParameters() != null)
/*     */     {
/*  88 */       if (!getValidationParameters().equals(pm.getValidationParameters()))
/*     */       {
/*  90 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*  95 */     else if (pm.getValidationParameters() != null)
/*     */     {
/*  97 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 101 */     if (getQ() != null)
/*     */     {
/* 103 */       if (!getQ().equals(pm.getQ()))
/*     */       {
/* 105 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 110 */     else if (pm.getQ() != null)
/*     */     {
/* 112 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 116 */     return (this.j == pm.getJ()) && (pm.getP().equals(this.p)) && (pm.getG().equals(this.g));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/params/DHParameters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */