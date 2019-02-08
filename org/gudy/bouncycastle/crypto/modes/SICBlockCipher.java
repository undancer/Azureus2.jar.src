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
/*     */ public class SICBlockCipher
/*     */   implements BlockCipher
/*     */ {
/*  14 */   private BlockCipher cipher = null;
/*     */   
/*     */   private int blockSize;
/*     */   
/*     */   private byte[] IV;
/*     */   
/*     */   private byte[] counter;
/*     */   
/*     */   private byte[] counterOut;
/*     */   
/*     */   private boolean encrypting;
/*     */   
/*     */   public SICBlockCipher(BlockCipher c)
/*     */   {
/*  28 */     this.cipher = c;
/*  29 */     this.blockSize = this.cipher.getBlockSize();
/*  30 */     this.IV = new byte[this.blockSize];
/*  31 */     this.counter = new byte[this.blockSize];
/*  32 */     this.counterOut = new byte[this.blockSize];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BlockCipher getUnderlyingCipher()
/*     */   {
/*  42 */     return this.cipher;
/*     */   }
/*     */   
/*     */   public void init(boolean forEncryption, CipherParameters params)
/*     */     throws IllegalArgumentException
/*     */   {
/*  48 */     this.encrypting = forEncryption;
/*     */     
/*  50 */     if ((params instanceof ParametersWithIV)) {
/*  51 */       ParametersWithIV ivParam = (ParametersWithIV)params;
/*  52 */       byte[] iv = ivParam.getIV();
/*  53 */       System.arraycopy(iv, 0, this.IV, 0, this.IV.length);
/*     */       
/*  55 */       reset();
/*  56 */       this.cipher.init(true, ivParam.getParameters());
/*     */     }
/*     */   }
/*     */   
/*     */   public String getAlgorithmName()
/*     */   {
/*  62 */     return this.cipher.getAlgorithmName() + "/SIC";
/*     */   }
/*     */   
/*     */   public int getBlockSize()
/*     */   {
/*  67 */     return this.cipher.getBlockSize();
/*     */   }
/*     */   
/*     */   public int processBlock(byte[] in, int inOff, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/*  73 */     this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  78 */     for (int i = 0; i < this.counterOut.length; i++) {
/*  79 */       out[(outOff + i)] = ((byte)(this.counterOut[i] ^ in[(inOff + i)]));
/*     */     }
/*     */     
/*  82 */     int carry = 1;
/*     */     
/*  84 */     for (int i = this.counter.length - 1; i >= 0; i--)
/*     */     {
/*  86 */       int x = (this.counter[i] & 0xFF) + carry;
/*     */       
/*  88 */       if (x > 255)
/*     */       {
/*  90 */         carry = 1;
/*     */       }
/*     */       else
/*     */       {
/*  94 */         carry = 0;
/*     */       }
/*     */       
/*  97 */       this.counter[i] = ((byte)x);
/*     */     }
/*     */     
/* 100 */     return this.counter.length;
/*     */   }
/*     */   
/*     */   public void reset()
/*     */   {
/* 105 */     System.arraycopy(this.IV, 0, this.counter, 0, this.counter.length);
/* 106 */     this.cipher.reset();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/SICBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */