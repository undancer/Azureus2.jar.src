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
/*     */ public class CFBBlockCipher
/*     */   implements BlockCipher
/*     */ {
/*     */   private byte[] IV;
/*     */   private byte[] cfbV;
/*     */   private byte[] cfbOutV;
/*     */   private int blockSize;
/*  19 */   private BlockCipher cipher = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean encrypting;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CFBBlockCipher(BlockCipher cipher, int bitBlockSize)
/*     */   {
/*  33 */     this.cipher = cipher;
/*  34 */     this.blockSize = (bitBlockSize / 8);
/*     */     
/*  36 */     this.IV = new byte[cipher.getBlockSize()];
/*  37 */     this.cfbV = new byte[cipher.getBlockSize()];
/*  38 */     this.cfbOutV = new byte[cipher.getBlockSize()];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockCipher getUnderlyingCipher()
/*     */   {
/*  48 */     return this.cipher;
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
/*     */   public void init(boolean encrypting, CipherParameters params)
/*     */     throws IllegalArgumentException
/*     */   {
/*  67 */     this.encrypting = encrypting;
/*     */     
/*  69 */     if ((params instanceof ParametersWithIV))
/*     */     {
/*  71 */       ParametersWithIV ivParam = (ParametersWithIV)params;
/*  72 */       byte[] iv = ivParam.getIV();
/*     */       
/*  74 */       if (iv.length < this.IV.length)
/*     */       {
/*     */ 
/*  77 */         System.arraycopy(iv, 0, this.IV, this.IV.length - iv.length, iv.length);
/*  78 */         for (int i = 0; i < this.IV.length - iv.length; i++)
/*     */         {
/*  80 */           this.IV[i] = 0;
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  85 */         System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
/*     */       }
/*     */       
/*  88 */       reset();
/*     */       
/*  90 */       this.cipher.init(true, ivParam.getParameters());
/*     */     }
/*     */     else
/*     */     {
/*  94 */       reset();
/*     */       
/*  96 */       this.cipher.init(true, params);
/*     */     }
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
/* 108 */     return this.cipher.getAlgorithmName() + "/CFB" + this.blockSize * 8;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getBlockSize()
/*     */   {
/* 118 */     return this.blockSize;
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
/* 141 */     return this.encrypting ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
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
/*     */   public int encryptBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 163 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 165 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 168 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 170 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 173 */     this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 178 */     for (int i = 0; i < this.blockSize; i++)
/*     */     {
/* 180 */       out[(outOff + i)] = ((byte)(this.cfbOutV[i] ^ in[(inOff + i)]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 186 */     System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
/* 187 */     System.arraycopy(out, outOff, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
/*     */     
/* 189 */     return this.blockSize;
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
/*     */   public int decryptBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 211 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 213 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 216 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 218 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 221 */     this.cipher.processBlock(this.cfbV, 0, this.cfbOutV, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 226 */     System.arraycopy(this.cfbV, this.blockSize, this.cfbV, 0, this.cfbV.length - this.blockSize);
/* 227 */     System.arraycopy(in, inOff, this.cfbV, this.cfbV.length - this.blockSize, this.blockSize);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 232 */     for (int i = 0; i < this.blockSize; i++)
/*     */     {
/* 234 */       out[(outOff + i)] = ((byte)(this.cfbOutV[i] ^ in[(inOff + i)]));
/*     */     }
/*     */     
/* 237 */     return this.blockSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 246 */     System.arraycopy(this.IV, 0, this.cfbV, 0, this.IV.length);
/*     */     
/* 248 */     this.cipher.reset();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/CFBBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */