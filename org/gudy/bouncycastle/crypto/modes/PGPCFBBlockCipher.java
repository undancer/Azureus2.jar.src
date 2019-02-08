/*     */ package org.gudy.bouncycastle.crypto.modes;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.BlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
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
/*     */ public class PGPCFBBlockCipher
/*     */   implements BlockCipher
/*     */ {
/*     */   private byte[] IV;
/*     */   private byte[] FR;
/*     */   private byte[] FRE;
/*     */   private byte[] tmp;
/*     */   private BlockCipher cipher;
/*     */   private int count;
/*     */   private int blockSize;
/*     */   private boolean forEncryption;
/*     */   private boolean inlineIv;
/*     */   
/*     */   public PGPCFBBlockCipher(BlockCipher cipher, boolean inlineIv)
/*     */   {
/*  38 */     this.cipher = cipher;
/*  39 */     this.inlineIv = inlineIv;
/*     */     
/*  41 */     this.blockSize = cipher.getBlockSize();
/*  42 */     this.IV = new byte[this.blockSize];
/*  43 */     this.FR = new byte[this.blockSize];
/*  44 */     this.FRE = new byte[this.blockSize];
/*  45 */     this.tmp = new byte[this.blockSize];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockCipher getUnderlyingCipher()
/*     */   {
/*  55 */     return this.cipher;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAlgorithmName()
/*     */   {
/*  66 */     if (this.inlineIv)
/*     */     {
/*  68 */       return this.cipher.getAlgorithmName() + "/PGPCFBwithIV";
/*     */     }
/*     */     
/*     */ 
/*  72 */     return this.cipher.getAlgorithmName() + "/PGPCFB";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getBlockSize()
/*     */   {
/*  83 */     return this.cipher.getBlockSize();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int processBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 106 */     if (this.inlineIv)
/*     */     {
/* 108 */       return this.forEncryption ? encryptBlockWithIV(in, inOff, out, outOff) : decryptBlockWithIV(in, inOff, out, outOff);
/*     */     }
/*     */     
/*     */ 
/* 112 */     return this.forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 122 */     this.count = 0;
/*     */     
/* 124 */     for (int i = 0; i != this.FR.length; i++)
/*     */     {
/* 126 */       if (this.inlineIv) {
/* 127 */         this.FR[i] = 0;
/*     */       } else {
/* 129 */         this.FR[i] = this.IV[i];
/*     */       }
/*     */     }
/* 132 */     this.cipher.reset();
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
/*     */ 
/*     */   public void init(boolean forEncryption, CipherParameters params)
/*     */     throws IllegalArgumentException
/*     */   {
/* 151 */     this.forEncryption = forEncryption;
/*     */     
/* 153 */     if ((params instanceof ParametersWithIV))
/*     */     {
/* 155 */       ParametersWithIV ivParam = (ParametersWithIV)params;
/* 156 */       byte[] iv = ivParam.getIV();
/*     */       
/* 158 */       if (iv.length < this.IV.length)
/*     */       {
/*     */ 
/* 161 */         System.arraycopy(iv, 0, this.IV, this.IV.length - iv.length, iv.length);
/* 162 */         for (int i = 0; i < this.IV.length - iv.length; i++)
/*     */         {
/* 164 */           this.IV[i] = 0;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 169 */         System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
/*     */       }
/*     */       
/* 172 */       reset();
/*     */       
/* 174 */       this.cipher.init(true, ivParam.getParameters());
/*     */     }
/*     */     else
/*     */     {
/* 178 */       reset();
/*     */       
/* 180 */       this.cipher.init(true, params);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private byte encryptByte(byte data, int blockOff)
/*     */   {
/* 192 */     return (byte)(this.FRE[blockOff] ^ data);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int encryptBlockWithIV(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 214 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 216 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 219 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 221 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 224 */     if (this.count == 0)
/*     */     {
/* 226 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 228 */       for (int n = 0; n < this.blockSize; n++)
/*     */       {
/* 230 */         out[(outOff + n)] = encryptByte(this.IV[n], n);
/*     */       }
/*     */       
/* 233 */       System.arraycopy(out, outOff, this.FR, 0, this.blockSize);
/*     */       
/* 235 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 237 */       out[(outOff + this.blockSize)] = encryptByte(this.IV[(this.blockSize - 2)], 0);
/* 238 */       out[(outOff + this.blockSize + 1)] = encryptByte(this.IV[(this.blockSize - 1)], 1);
/*     */       
/* 240 */       System.arraycopy(out, outOff + 2, this.FR, 0, this.blockSize);
/*     */       
/* 242 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 244 */       for (int n = 0; n < this.blockSize; n++)
/*     */       {
/* 246 */         out[(outOff + this.blockSize + 2 + n)] = encryptByte(in[(inOff + n)], n);
/*     */       }
/*     */       
/* 249 */       System.arraycopy(out, outOff + this.blockSize + 2, this.FR, 0, this.blockSize);
/*     */       
/* 251 */       this.count += 2 * this.blockSize + 2;
/*     */       
/* 253 */       return 2 * this.blockSize + 2;
/*     */     }
/* 255 */     if (this.count >= this.blockSize + 2)
/*     */     {
/* 257 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 259 */       for (int n = 0; n < this.blockSize; n++)
/*     */       {
/* 261 */         out[(outOff + n)] = encryptByte(in[(inOff + n)], n);
/*     */       }
/*     */       
/* 264 */       System.arraycopy(out, outOff, this.FR, 0, this.blockSize);
/*     */     }
/*     */     
/* 267 */     return this.blockSize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int decryptBlockWithIV(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 289 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 291 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 294 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 296 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 299 */     if (this.count == 0)
/*     */     {
/* 301 */       System.arraycopy(in, inOff + 0, this.FR, 0, this.blockSize);
/*     */       
/* 303 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 305 */       this.count += this.blockSize;
/*     */       
/* 307 */       return 0;
/*     */     }
/* 309 */     if (this.count == this.blockSize)
/*     */     {
/*     */ 
/* 312 */       System.arraycopy(in, inOff, this.tmp, 0, this.blockSize);
/*     */       
/* 314 */       System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
/*     */       
/* 316 */       this.FR[(this.blockSize - 2)] = this.tmp[0];
/* 317 */       this.FR[(this.blockSize - 1)] = this.tmp[1];
/*     */       
/* 319 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 321 */       for (int n = 0; n < this.blockSize - 2; n++)
/*     */       {
/* 323 */         out[(outOff + n)] = encryptByte(this.tmp[(n + 2)], n);
/*     */       }
/*     */       
/* 326 */       System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
/*     */       
/* 328 */       this.count += 2;
/*     */       
/* 330 */       return this.blockSize - 2;
/*     */     }
/* 332 */     if (this.count >= this.blockSize + 2)
/*     */     {
/*     */ 
/* 335 */       System.arraycopy(in, inOff, this.tmp, 0, this.blockSize);
/*     */       
/* 337 */       out[(outOff + 0)] = encryptByte(this.tmp[0], this.blockSize - 2);
/* 338 */       out[(outOff + 1)] = encryptByte(this.tmp[1], this.blockSize - 1);
/*     */       
/* 340 */       System.arraycopy(this.tmp, 0, this.FR, this.blockSize - 2, 2);
/*     */       
/* 342 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 344 */       for (int n = 0; n < this.blockSize - 2; n++)
/*     */       {
/* 346 */         out[(outOff + n + 2)] = encryptByte(this.tmp[(n + 2)], n);
/*     */       }
/*     */       
/* 349 */       System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
/*     */     }
/*     */     
/*     */ 
/* 353 */     return this.blockSize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 375 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 377 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 380 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 382 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/*     */ 
/* 386 */     this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/* 387 */     for (int n = 0; n < this.blockSize; n++) {
/* 388 */       out[(outOff + n)] = encryptByte(in[(inOff + n)], n);
/*     */     }
/* 390 */     System.arraycopy(out, outOff + 0, this.FR, 0, this.blockSize);
/*     */     
/* 392 */     return this.blockSize;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 415 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 417 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 420 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 422 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 425 */     this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/* 426 */     for (int n = 0; n < this.blockSize; n++) {
/* 427 */       out[(outOff + n)] = encryptByte(in[(inOff + n)], n);
/*     */     }
/* 429 */     System.arraycopy(in, inOff + 0, this.FR, 0, this.blockSize);
/*     */     
/* 431 */     return this.blockSize;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/PGPCFBBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */