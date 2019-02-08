/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
/*     */ import org.gudy.bouncycastle.crypto.DerivationFunction;
/*     */ import org.gudy.bouncycastle.crypto.DerivationParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.params.MGFParameters;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MGF1BytesGenerator
/*     */   implements DerivationFunction
/*     */ {
/*     */   private Digest digest;
/*     */   private byte[] seed;
/*     */   private int hLen;
/*     */   
/*     */   public MGF1BytesGenerator(Digest digest)
/*     */   {
/*  25 */     this.digest = digest;
/*  26 */     this.hLen = digest.getDigestSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public void init(DerivationParameters param)
/*     */   {
/*  32 */     if (!(param instanceof MGFParameters))
/*     */     {
/*  34 */       throw new IllegalArgumentException("MGF parameters required for MGF1Generator");
/*     */     }
/*     */     
/*  37 */     MGFParameters p = (MGFParameters)param;
/*     */     
/*  39 */     this.seed = p.getSeed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Digest getDigest()
/*     */   {
/*  47 */     return this.digest;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void ItoOSP(int i, byte[] sp)
/*     */   {
/*  57 */     sp[0] = ((byte)(i >>> 24));
/*  58 */     sp[1] = ((byte)(i >>> 16));
/*  59 */     sp[2] = ((byte)(i >>> 8));
/*  60 */     sp[3] = ((byte)(i >>> 0));
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
/*     */   public int generateBytes(byte[] out, int outOff, int len)
/*     */     throws DataLengthException, IllegalArgumentException
/*     */   {
/*  76 */     byte[] hashBuf = new byte[this.hLen];
/*  77 */     byte[] C = new byte[4];
/*  78 */     int counter = 0;
/*     */     
/*  80 */     this.digest.reset();
/*     */     
/*     */     do
/*     */     {
/*  84 */       ItoOSP(counter, C);
/*     */       
/*  86 */       this.digest.update(this.seed, 0, this.seed.length);
/*  87 */       this.digest.update(C, 0, C.length);
/*  88 */       this.digest.doFinal(hashBuf, 0);
/*     */       
/*  90 */       System.arraycopy(hashBuf, 0, out, outOff + counter * this.hLen, this.hLen);
/*     */       
/*  92 */       counter++; } while (counter < len / this.hLen);
/*     */     
/*  94 */     if (counter * this.hLen < len)
/*     */     {
/*  96 */       ItoOSP(counter, C);
/*     */       
/*  98 */       this.digest.update(this.seed, 0, this.seed.length);
/*  99 */       this.digest.update(C, 0, C.length);
/* 100 */       this.digest.doFinal(hashBuf, 0);
/*     */       
/* 102 */       System.arraycopy(hashBuf, 0, out, outOff + counter * this.hLen, len - counter * this.hLen);
/*     */     }
/*     */     
/* 105 */     return len;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/MGF1BytesGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */