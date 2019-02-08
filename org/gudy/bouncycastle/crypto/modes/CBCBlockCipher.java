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
/*     */ public class CBCBlockCipher
/*     */   implements BlockCipher
/*     */ {
/*     */   private byte[] IV;
/*     */   private byte[] cbcV;
/*     */   private byte[] cbcNextV;
/*     */   private int blockSize;
/*  19 */   private BlockCipher cipher = null;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean encrypting;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CBCBlockCipher(BlockCipher cipher)
/*     */   {
/*  30 */     this.cipher = cipher;
/*  31 */     this.blockSize = cipher.getBlockSize();
/*     */     
/*  33 */     this.IV = new byte[this.blockSize];
/*  34 */     this.cbcV = new byte[this.blockSize];
/*  35 */     this.cbcNextV = new byte[this.blockSize];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockCipher getUnderlyingCipher()
/*     */   {
/*  45 */     return this.cipher;
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
/*     */   public void init(boolean encrypting, CipherParameters params)
/*     */     throws IllegalArgumentException
/*     */   {
/*  63 */     this.encrypting = encrypting;
/*     */     
/*  65 */     if ((params instanceof ParametersWithIV))
/*     */     {
/*  67 */       ParametersWithIV ivParam = (ParametersWithIV)params;
/*  68 */       byte[] iv = ivParam.getIV();
/*     */       
/*  70 */       if (iv.length != this.blockSize)
/*     */       {
/*  72 */         throw new IllegalArgumentException("initialisation vector must be the same length as block size");
/*     */       }
/*     */       
/*  75 */       System.arraycopy(iv, 0, this.IV, 0, iv.length);
/*     */       
/*  77 */       reset();
/*     */       
/*  79 */       this.cipher.init(encrypting, ivParam.getParameters());
/*     */     }
/*     */     else
/*     */     {
/*  83 */       reset();
/*     */       
/*  85 */       this.cipher.init(encrypting, params);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAlgorithmName()
/*     */   {
/*  96 */     return this.cipher.getAlgorithmName() + "/CBC";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getBlockSize()
/*     */   {
/* 106 */     return this.cipher.getBlockSize();
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
/* 129 */     return this.encrypting ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 138 */     System.arraycopy(this.IV, 0, this.cbcV, 0, this.IV.length);
/*     */     
/* 140 */     this.cipher.reset();
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
/* 162 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 164 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */     for (int i = 0; i < this.blockSize; i++)
/*     */     {
/* 173 */       int tmp39_37 = i; byte[] tmp39_34 = this.cbcV;tmp39_34[tmp39_37] = ((byte)(tmp39_34[tmp39_37] ^ in[(inOff + i)]));
/*     */     }
/*     */     
/* 176 */     int length = this.cipher.processBlock(this.cbcV, 0, out, outOff);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 181 */     System.arraycopy(out, outOff, this.cbcV, 0, this.cbcV.length);
/*     */     
/* 183 */     return length;
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
/* 205 */     if (inOff + this.blockSize > in.length)
/*     */     {
/* 207 */       throw new DataLengthException("input buffer too short");
/*     */     }
/*     */     
/* 210 */     System.arraycopy(in, inOff, this.cbcNextV, 0, this.blockSize);
/*     */     
/* 212 */     int length = this.cipher.processBlock(in, inOff, out, outOff);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 217 */     for (int i = 0; i < this.blockSize; i++)
/*     */     {
/* 219 */       int tmp69_68 = (outOff + i); byte[] tmp69_63 = out;tmp69_63[tmp69_68] = ((byte)(tmp69_63[tmp69_68] ^ this.cbcV[i]));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 227 */     byte[] tmp = this.cbcV;
/* 228 */     this.cbcV = this.cbcNextV;
/* 229 */     this.cbcNextV = tmp;
/*     */     
/* 231 */     return length;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/CBCBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */