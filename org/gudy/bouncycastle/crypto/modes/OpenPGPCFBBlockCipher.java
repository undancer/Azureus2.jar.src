/*     */ package org.gudy.bouncycastle.crypto.modes;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.BlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.CipherParameters;
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
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
/*     */ public class OpenPGPCFBBlockCipher
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
/*     */   
/*     */   public OpenPGPCFBBlockCipher(BlockCipher cipher)
/*     */   {
/*  38 */     this.cipher = cipher;
/*     */     
/*  40 */     this.blockSize = cipher.getBlockSize();
/*  41 */     this.IV = new byte[this.blockSize];
/*  42 */     this.FR = new byte[this.blockSize];
/*  43 */     this.FRE = new byte[this.blockSize];
/*  44 */     this.tmp = new byte[this.blockSize];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockCipher getUnderlyingCipher()
/*     */   {
/*  54 */     return this.cipher;
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
/*  65 */     return this.cipher.getAlgorithmName() + "/OpenPGPCFB";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getBlockSize()
/*     */   {
/*  75 */     return this.cipher.getBlockSize();
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
/*  98 */     return this.forEncryption ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 107 */     this.count = 0;
/*     */     
/* 109 */     for (int i = 0; i != this.FR.length; i++)
/*     */     {
/* 111 */       this.FR[i] = this.IV[i];
/*     */     }
/*     */     
/* 114 */     this.cipher.reset();
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
/* 133 */     this.forEncryption = forEncryption;
/*     */     
/* 135 */     reset();
/*     */     
/* 137 */     this.cipher.init(true, params);
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
/* 148 */     return (byte)(this.FRE[blockOff] ^ data);
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
/* 170 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 172 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 175 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 177 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 180 */     if (this.count > this.blockSize)
/*     */     {
/* 182 */       this.FR[(this.blockSize - 2)] = (out[outOff] = encryptByte(in[inOff], this.blockSize - 2));
/* 183 */       this.FR[(this.blockSize - 1)] = (out[(outOff + 1)] = encryptByte(in[(inOff + 1)], this.blockSize - 1));
/*     */       
/* 185 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 187 */       for (int n = 2; n < this.blockSize; n++)
/*     */       {
/* 189 */         out[(outOff + n)] = encryptByte(in[(inOff + n)], n - 2);
/*     */       }
/*     */       
/* 192 */       System.arraycopy(out, outOff + 2, this.FR, 0, this.blockSize - 2);
/*     */     }
/* 194 */     else if (this.count == 0)
/*     */     {
/* 196 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 198 */       for (int n = 0; n < this.blockSize; n++)
/*     */       {
/* 200 */         out[(outOff + n)] = encryptByte(in[(inOff + n)], n);
/*     */       }
/*     */       
/* 203 */       System.arraycopy(out, outOff, this.FR, 0, this.blockSize);
/*     */       
/* 205 */       this.count += this.blockSize;
/*     */     }
/* 207 */     else if (this.count == this.blockSize)
/*     */     {
/* 209 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 211 */       out[outOff] = encryptByte(in[inOff], 0);
/* 212 */       out[(outOff + 1)] = encryptByte(in[(inOff + 1)], 1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 217 */       System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
/* 218 */       System.arraycopy(out, outOff, this.FR, this.blockSize - 2, 2);
/*     */       
/* 220 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 222 */       for (int n = 2; n < this.blockSize; n++)
/*     */       {
/* 224 */         out[(outOff + n)] = encryptByte(in[(inOff + n)], n - 2);
/*     */       }
/*     */       
/* 227 */       System.arraycopy(out, outOff + 2, this.FR, 0, this.blockSize - 2);
/*     */       
/* 229 */       this.count += this.blockSize;
/*     */     }
/*     */     
/* 232 */     return this.blockSize;
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
/*     */   private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 254 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 256 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 259 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 261 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 264 */     if (this.count > this.blockSize)
/*     */     {
/*     */ 
/* 267 */       System.arraycopy(in, inOff, this.tmp, 0, this.blockSize);
/*     */       
/* 269 */       out[(outOff + 0)] = encryptByte(this.tmp[0], this.blockSize - 2);
/* 270 */       out[(outOff + 1)] = encryptByte(this.tmp[1], this.blockSize - 1);
/*     */       
/* 272 */       System.arraycopy(this.tmp, 0, this.FR, this.blockSize - 2, 2);
/*     */       
/* 274 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 276 */       for (int n = 2; n < this.blockSize; n++)
/*     */       {
/* 278 */         out[(outOff + n)] = encryptByte(this.tmp[n], n - 2);
/*     */       }
/*     */       
/* 281 */       System.arraycopy(this.tmp, 2, this.FR, 0, this.blockSize - 2);
/*     */     }
/* 283 */     else if (this.count == 0)
/*     */     {
/* 285 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 287 */       for (int n = 0; n < this.blockSize; n++)
/*     */       {
/* 289 */         this.FR[n] = in[(inOff + n)];
/* 290 */         out[n] = encryptByte(in[(inOff + n)], n);
/*     */       }
/*     */       
/* 293 */       this.count += this.blockSize;
/*     */     }
/* 295 */     else if (this.count == this.blockSize)
/*     */     {
/* 297 */       System.arraycopy(in, inOff, this.tmp, 0, this.blockSize);
/*     */       
/* 299 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 301 */       out[(outOff + 0)] = encryptByte(this.tmp[0], 0);
/* 302 */       out[(outOff + 1)] = encryptByte(this.tmp[1], 1);
/*     */       
/* 304 */       System.arraycopy(this.FR, 2, this.FR, 0, this.blockSize - 2);
/*     */       
/* 306 */       this.FR[(this.blockSize - 2)] = this.tmp[0];
/* 307 */       this.FR[(this.blockSize - 1)] = this.tmp[1];
/*     */       
/* 309 */       this.cipher.processBlock(this.FR, 0, this.FRE, 0);
/*     */       
/* 311 */       for (int n = 2; n < this.blockSize; n++)
/*     */       {
/* 313 */         this.FR[(n - 2)] = in[(inOff + n)];
/* 314 */         out[(outOff + n)] = encryptByte(in[(inOff + n)], n - 2);
/*     */       }
/*     */       
/* 317 */       this.count += this.blockSize;
/*     */     }
/*     */     
/* 320 */     return this.blockSize;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/OpenPGPCFBBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */