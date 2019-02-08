/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.PBEParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.params.KeyParameter;
/*     */ import org.gudy.bouncycastle.crypto.params.ParametersWithIV;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PKCS5S1ParametersGenerator
/*     */   extends PBEParametersGenerator
/*     */ {
/*     */   private Digest digest;
/*     */   
/*     */   public PKCS5S1ParametersGenerator(Digest digest)
/*     */   {
/*  31 */     this.digest = digest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] generateDerivedKey()
/*     */   {
/*  39 */     byte[] digestBytes = new byte[this.digest.getDigestSize()];
/*     */     
/*  41 */     this.digest.update(this.password, 0, this.password.length);
/*  42 */     this.digest.update(this.salt, 0, this.salt.length);
/*     */     
/*  44 */     this.digest.doFinal(digestBytes, 0);
/*  45 */     for (int i = 1; i < this.iterationCount; i++)
/*     */     {
/*  47 */       this.digest.update(digestBytes, 0, digestBytes.length);
/*  48 */       this.digest.doFinal(digestBytes, 0);
/*     */     }
/*     */     
/*  51 */     return digestBytes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CipherParameters generateDerivedParameters(int keySize)
/*     */   {
/*  65 */     keySize /= 8;
/*     */     
/*  67 */     if (keySize > this.digest.getDigestSize())
/*     */     {
/*  69 */       throw new IllegalArgumentException("Can't generate a derived key " + keySize + " bytes long.");
/*     */     }
/*     */     
/*     */ 
/*  73 */     byte[] dKey = generateDerivedKey();
/*     */     
/*  75 */     return new KeyParameter(dKey, 0, keySize);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CipherParameters generateDerivedParameters(int keySize, int ivSize)
/*     */   {
/*  92 */     keySize /= 8;
/*  93 */     ivSize /= 8;
/*     */     
/*  95 */     if (keySize + ivSize > this.digest.getDigestSize())
/*     */     {
/*  97 */       throw new IllegalArgumentException("Can't generate a derived key " + (keySize + ivSize) + " bytes long.");
/*     */     }
/*     */     
/*     */ 
/* 101 */     byte[] dKey = generateDerivedKey();
/*     */     
/* 103 */     return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CipherParameters generateDerivedMacParameters(int keySize)
/*     */   {
/* 117 */     return generateDerivedParameters(keySize);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/PKCS5S1ParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */