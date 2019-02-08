/*     */ package org.gudy.bouncycastle.crypto.generators;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.Digest;
/*     */ import org.gudy.bouncycastle.crypto.PBEParametersGenerator;
/*     */ import org.gudy.bouncycastle.crypto.digests.MD5Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.RIPEMD160Digest;
/*     */ import org.gudy.bouncycastle.crypto.digests.SHA1Digest;
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
/*     */ 
/*     */ 
/*     */ public class PKCS12ParametersGenerator
/*     */   extends PBEParametersGenerator
/*     */ {
/*     */   public static final int KEY_MATERIAL = 1;
/*     */   public static final int IV_MATERIAL = 2;
/*     */   public static final int MAC_MATERIAL = 3;
/*     */   private Digest digest;
/*     */   private int u;
/*     */   private int v;
/*     */   
/*     */   public PKCS12ParametersGenerator(Digest digest)
/*     */   {
/*  41 */     this.digest = digest;
/*  42 */     if ((digest instanceof MD5Digest))
/*     */     {
/*  44 */       this.u = 16;
/*  45 */       this.v = 64;
/*     */     }
/*  47 */     else if ((digest instanceof SHA1Digest))
/*     */     {
/*  49 */       this.u = 20;
/*  50 */       this.v = 64;
/*     */     }
/*  52 */     else if ((digest instanceof RIPEMD160Digest))
/*     */     {
/*  54 */       this.u = 20;
/*  55 */       this.v = 64;
/*     */     }
/*     */     else
/*     */     {
/*  59 */       throw new IllegalArgumentException("Digest " + digest.getAlgorithmName() + " unsupported");
/*     */     }
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
/*     */   private void adjust(byte[] a, int aOff, byte[] b)
/*     */   {
/*  73 */     int x = (b[(b.length - 1)] & 0xFF) + (a[(aOff + b.length - 1)] & 0xFF) + 1;
/*     */     
/*  75 */     a[(aOff + b.length - 1)] = ((byte)x);
/*  76 */     x >>>= 8;
/*     */     
/*  78 */     for (int i = b.length - 2; i >= 0; i--)
/*     */     {
/*  80 */       x += (b[i] & 0xFF) + (a[(aOff + i)] & 0xFF);
/*  81 */       a[(aOff + i)] = ((byte)x);
/*  82 */       x >>>= 8;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte[] generateDerivedKey(int idByte, int n)
/*     */   {
/*  93 */     byte[] D = new byte[this.v];
/*  94 */     byte[] dKey = new byte[n];
/*     */     
/*  96 */     for (int i = 0; i != D.length; i++)
/*     */     {
/*  98 */       D[i] = ((byte)idByte);
/*     */     }
/*     */     
/*     */     byte[] S;
/*     */     
/* 103 */     if ((this.salt != null) && (this.salt.length != 0))
/*     */     {
/* 105 */       byte[] S = new byte[this.v * ((this.salt.length + this.v - 1) / this.v)];
/*     */       
/* 107 */       for (int i = 0; i != S.length; i++)
/*     */       {
/* 109 */         S[i] = this.salt[(i % this.salt.length)];
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 114 */       S = new byte[0];
/*     */     }
/*     */     
/*     */     byte[] P;
/*     */     
/* 119 */     if ((this.password != null) && (this.password.length != 0))
/*     */     {
/* 121 */       byte[] P = new byte[this.v * ((this.password.length + this.v - 1) / this.v)];
/*     */       
/* 123 */       for (int i = 0; i != P.length; i++)
/*     */       {
/* 125 */         P[i] = this.password[(i % this.password.length)];
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 130 */       P = new byte[0];
/*     */     }
/*     */     
/* 133 */     byte[] I = new byte[S.length + P.length];
/*     */     
/* 135 */     System.arraycopy(S, 0, I, 0, S.length);
/* 136 */     System.arraycopy(P, 0, I, S.length, P.length);
/*     */     
/* 138 */     byte[] B = new byte[this.v];
/* 139 */     int c = (n + this.u - 1) / this.u;
/*     */     
/* 141 */     for (int i = 1; i <= c; i++)
/*     */     {
/* 143 */       byte[] A = new byte[this.u];
/*     */       
/* 145 */       this.digest.update(D, 0, D.length);
/* 146 */       this.digest.update(I, 0, I.length);
/* 147 */       this.digest.doFinal(A, 0);
/* 148 */       for (int j = 1; j != this.iterationCount; j++)
/*     */       {
/* 150 */         this.digest.update(A, 0, A.length);
/* 151 */         this.digest.doFinal(A, 0);
/*     */       }
/*     */       
/* 154 */       for (int j = 0; j != B.length; j++)
/*     */       {
/* 156 */         B[j] = A[(j % A.length)];
/*     */       }
/*     */       
/* 159 */       for (int j = 0; j != I.length / this.v; j++)
/*     */       {
/* 161 */         adjust(I, j * this.v, B);
/*     */       }
/*     */       
/* 164 */       if (i == c)
/*     */       {
/* 166 */         System.arraycopy(A, 0, dKey, (i - 1) * this.u, dKey.length - (i - 1) * this.u);
/*     */       }
/*     */       else
/*     */       {
/* 170 */         System.arraycopy(A, 0, dKey, (i - 1) * this.u, A.length);
/*     */       }
/*     */     }
/*     */     
/* 174 */     return dKey;
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
/*     */   public CipherParameters generateDerivedParameters(int keySize)
/*     */   {
/* 187 */     keySize /= 8;
/*     */     
/* 189 */     byte[] dKey = generateDerivedKey(1, keySize);
/*     */     
/* 191 */     return new KeyParameter(dKey, 0, keySize);
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
/*     */   public CipherParameters generateDerivedParameters(int keySize, int ivSize)
/*     */   {
/* 207 */     keySize /= 8;
/* 208 */     ivSize /= 8;
/*     */     
/* 210 */     byte[] dKey = generateDerivedKey(1, keySize);
/*     */     
/* 212 */     byte[] iv = generateDerivedKey(2, ivSize);
/*     */     
/* 214 */     return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), iv, 0, ivSize);
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
/*     */   public CipherParameters generateDerivedMacParameters(int keySize)
/*     */   {
/* 227 */     keySize /= 8;
/*     */     
/* 229 */     byte[] dKey = generateDerivedKey(3, keySize);
/*     */     
/* 231 */     return new KeyParameter(dKey, 0, keySize);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/generators/PKCS12ParametersGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */