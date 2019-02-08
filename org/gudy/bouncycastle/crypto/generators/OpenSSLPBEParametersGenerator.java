/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.PBEParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.digests.MD5Digest;
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
/*     */ public class OpenSSLPBEParametersGenerator
/*     */   extends PBEParametersGenerator
/*     */ {
/*  20 */   private Digest digest = new MD5Digest();
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
/*     */   public void init(byte[] password, byte[] salt)
/*     */   {
/*  39 */     super.init(password, salt, 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] generateDerivedKey(int bytesNeeded)
/*     */   {
/*  48 */     byte[] buf = new byte[this.digest.getDigestSize()];
/*  49 */     byte[] key = new byte[bytesNeeded];
/*  50 */     int offset = 0;
/*     */     
/*     */     for (;;)
/*     */     {
/*  54 */       this.digest.update(this.password, 0, this.password.length);
/*  55 */       this.digest.update(this.salt, 0, this.salt.length);
/*     */       
/*  57 */       this.digest.doFinal(buf, 0);
/*     */       
/*  59 */       int len = bytesNeeded > buf.length ? buf.length : bytesNeeded;
/*  60 */       System.arraycopy(buf, 0, key, offset, len);
/*  61 */       offset += len;
/*     */       
/*     */ 
/*  64 */       bytesNeeded -= len;
/*  65 */       if (bytesNeeded == 0) {
/*     */         break;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*  71 */       this.digest.reset();
/*  72 */       this.digest.update(buf, 0, buf.length);
/*     */     }
/*     */     
/*  75 */     return key;
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
/*  89 */     keySize /= 8;
/*     */     
/*  91 */     byte[] dKey = generateDerivedKey(keySize);
/*     */     
/*  93 */     return new KeyParameter(dKey, 0, keySize);
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
/* 110 */     keySize /= 8;
/* 111 */     ivSize /= 8;
/*     */     
/* 113 */     byte[] dKey = generateDerivedKey(keySize + ivSize);
/*     */     
/* 115 */     return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize);
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
/* 129 */     return generateDerivedParameters(keySize);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/OpenSSLPBEParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */