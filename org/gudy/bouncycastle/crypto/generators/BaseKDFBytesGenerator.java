/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
/*     */ import org.gudy.bouncycastle.crypto.DerivationFunction;
/*     */ import org.gudy.bouncycastle.crypto.DerivationParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.params.ISO18033KDFParameters;
/*     */ import org.gudy.bouncycastle.crypto.params.KDFParameters;
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
/*     */ public class BaseKDFBytesGenerator
/*     */   implements DerivationFunction
/*     */ {
/*     */   private int counterStart;
/*     */   private Digest digest;
/*     */   private byte[] shared;
/*     */   private byte[] iv;
/*     */   
/*     */   protected BaseKDFBytesGenerator(int counterStart, Digest digest)
/*     */   {
/*  33 */     this.counterStart = counterStart;
/*  34 */     this.digest = digest;
/*     */   }
/*     */   
/*     */ 
/*     */   public void init(DerivationParameters param)
/*     */   {
/*  40 */     if ((param instanceof KDFParameters))
/*     */     {
/*  42 */       KDFParameters p = (KDFParameters)param;
/*     */       
/*  44 */       this.shared = p.getSharedSecret();
/*  45 */       this.iv = p.getIV();
/*     */     }
/*  47 */     else if ((param instanceof ISO18033KDFParameters))
/*     */     {
/*  49 */       ISO18033KDFParameters p = (ISO18033KDFParameters)param;
/*     */       
/*  51 */       this.shared = p.getSeed();
/*  52 */       this.iv = null;
/*     */     }
/*     */     else
/*     */     {
/*  56 */       throw new IllegalArgumentException("KDF parameters required for KDF2Generator");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Digest getDigest()
/*     */   {
/*  65 */     return this.digest;
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
/*  81 */     if (out.length - len < outOff)
/*     */     {
/*  83 */       throw new DataLengthException("output buffer too small");
/*     */     }
/*     */     
/*  86 */     long oBytes = len;
/*  87 */     int outLen = this.digest.getDigestSize();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  95 */     if (oBytes > 8589934591L)
/*     */     {
/*  97 */       throw new IllegalArgumentException("Output length too large");
/*     */     }
/*     */     
/* 100 */     int cThreshold = (int)((oBytes + outLen - 1L) / outLen);
/*     */     
/* 102 */     byte[] dig = null;
/*     */     
/* 104 */     dig = new byte[this.digest.getDigestSize()];
/*     */     
/* 106 */     int counter = this.counterStart;
/*     */     
/* 108 */     for (int i = 0; i < cThreshold; i++)
/*     */     {
/* 110 */       this.digest.update(this.shared, 0, this.shared.length);
/*     */       
/* 112 */       this.digest.update((byte)(counter >> 24));
/* 113 */       this.digest.update((byte)(counter >> 16));
/* 114 */       this.digest.update((byte)(counter >> 8));
/* 115 */       this.digest.update((byte)counter);
/*     */       
/* 117 */       if (this.iv != null)
/*     */       {
/* 119 */         this.digest.update(this.iv, 0, this.iv.length);
/*     */       }
/*     */       
/* 122 */       this.digest.doFinal(dig, 0);
/*     */       
/* 124 */       if (len > outLen)
/*     */       {
/* 126 */         System.arraycopy(dig, 0, out, outOff, outLen);
/* 127 */         outOff += outLen;
/* 128 */         len -= outLen;
/*     */       }
/*     */       else
/*     */       {
/* 132 */         System.arraycopy(dig, 0, out, outOff, len);
/*     */       }
/*     */       
/* 135 */       counter++;
/*     */     }
/*     */     
/* 138 */     this.digest.reset();
/*     */     
/* 140 */     return len;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/BaseKDFBytesGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */