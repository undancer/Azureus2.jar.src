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
/*     */ public class OFBBlockCipher
/*     */   implements BlockCipher
/*     */ {
/*     */   private byte[] IV;
/*     */   private byte[] ofbV;
/*     */   private byte[] ofbOutV;
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
/*     */   public OFBBlockCipher(BlockCipher cipher, int blockSize)
/*     */   {
/*  33 */     this.cipher = cipher;
/*  34 */     this.blockSize = (blockSize / 8);
/*     */     
/*  36 */     this.IV = new byte[cipher.getBlockSize()];
/*  37 */     this.ofbV = new byte[cipher.getBlockSize()];
/*  38 */     this.ofbOutV = new byte[cipher.getBlockSize()];
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
/* 108 */     return this.cipher.getAlgorithmName() + "/OFB" + this.blockSize * 8;
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
/* 119 */     return this.blockSize;
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
/* 142 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 144 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 147 */     if (outOff + this.blockSize > out.length)
/*     */     {
/* 149 */       throw new DataLengthException("output buffer too short");
/*     */     }
/*     */     
/* 152 */     this.cipher.processBlock(this.ofbV, 0, this.ofbOutV, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 158 */     for (int i = 0; i < this.blockSize; i++)
/*     */     {
/* 160 */       out[(outOff + i)] = ((byte)(this.ofbOutV[i] ^ in[(inOff + i)]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 166 */     System.arraycopy(this.ofbV, this.blockSize, this.ofbV, 0, this.ofbV.length - this.blockSize);
/* 167 */     System.arraycopy(this.ofbOutV, 0, this.ofbV, this.ofbV.length - this.blockSize, this.blockSize);
/*     */     
/* 169 */     return this.blockSize;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 178 */     System.arraycopy(this.IV, 0, this.ofbV, 0, this.IV.length);
/*     */     
/* 180 */     this.cipher.reset();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/OFBBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */