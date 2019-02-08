/*     */ package org.gudy.bouncycastle.crypto;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferedAsymmetricBlockCipher
/*     */ {
/*     */   protected byte[] buf;
/*     */   
/*     */ 
/*     */ 
/*     */   protected int bufOff;
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean forEncryption;
/*     */   
/*     */ 
/*     */ 
/*     */   private AsymmetricBlockCipher cipher;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferedAsymmetricBlockCipher(AsymmetricBlockCipher cipher)
/*     */   {
/*  28 */     this.cipher = cipher;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AsymmetricBlockCipher getUnderlyingCipher()
/*     */   {
/*  38 */     return this.cipher;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getBufferPosition()
/*     */   {
/*  48 */     return this.bufOff;
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
/*     */   public void init(boolean forEncryption, CipherParameters params)
/*     */   {
/*  62 */     this.forEncryption = forEncryption;
/*     */     
/*  64 */     reset();
/*     */     
/*  66 */     this.cipher.init(forEncryption, params);
/*     */     
/*  68 */     this.buf = new byte[this.cipher.getInputBlockSize()];
/*  69 */     this.bufOff = 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInputBlockSize()
/*     */   {
/*  79 */     return this.cipher.getInputBlockSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getOutputBlockSize()
/*     */   {
/*  89 */     return this.cipher.getOutputBlockSize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void processByte(byte in)
/*     */   {
/* 100 */     if (this.bufOff > this.buf.length)
/*     */     {
/* 102 */       throw new DataLengthException("attempt to process message to long for cipher");
/*     */     }
/*     */     
/* 105 */     this.buf[(this.bufOff++)] = in;
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
/*     */   public void processBytes(byte[] in, int inOff, int len)
/*     */   {
/* 120 */     if (len == 0)
/*     */     {
/* 122 */       return;
/*     */     }
/*     */     
/* 125 */     if (len < 0)
/*     */     {
/* 127 */       throw new IllegalArgumentException("Can't have a negative input length!");
/*     */     }
/*     */     
/* 130 */     if (this.bufOff + len > this.buf.length)
/*     */     {
/* 132 */       throw new DataLengthException("attempt to process message to long for cipher");
/*     */     }
/*     */     
/* 135 */     System.arraycopy(in, inOff, this.buf, this.bufOff, len);
/* 136 */     this.bufOff += len;
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
/*     */   public byte[] doFinal()
/*     */     throws InvalidCipherTextException
/*     */   {
/* 150 */     byte[] out = this.cipher.processBlock(this.buf, 0, this.bufOff);
/*     */     
/* 152 */     reset();
/*     */     
/* 154 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 165 */     if (this.buf != null)
/*     */     {
/* 167 */       for (int i = 0; i < this.buf.length; i++)
/*     */       {
/* 169 */         this.buf[0] = 0;
/*     */       }
/*     */     }
/*     */     
/* 173 */     this.bufOff = 0;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/BufferedAsymmetricBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */