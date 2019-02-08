/*     */ package org.gudy.bouncycastle.crypto.modes;
/*     */ 
/*     */ import org.gudy.bouncycastle.crypto.BlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.BufferedBlockCipher;
/*     */ import org.gudy.bouncycastle.crypto.DataLengthException;
/*     */ import org.gudy.bouncycastle.crypto.InvalidCipherTextException;
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
/*     */ public class CTSBlockCipher
/*     */   extends BufferedBlockCipher
/*     */ {
/*     */   private int blockSize;
/*     */   
/*     */   public CTSBlockCipher(BlockCipher cipher)
/*     */   {
/*  28 */     if (((cipher instanceof OFBBlockCipher)) || ((cipher instanceof CFBBlockCipher)))
/*     */     {
/*  30 */       throw new IllegalArgumentException("CTSBlockCipher can only accept ECB, or CBC ciphers");
/*     */     }
/*     */     
/*  33 */     this.cipher = cipher;
/*     */     
/*  35 */     this.blockSize = cipher.getBlockSize();
/*     */     
/*  37 */     this.buf = new byte[this.blockSize * 2];
/*  38 */     this.bufOff = 0;
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
/*     */   public int getUpdateOutputSize(int len)
/*     */   {
/*  52 */     int total = len + this.bufOff;
/*  53 */     int leftOver = total % this.buf.length;
/*     */     
/*  55 */     if (leftOver == 0)
/*     */     {
/*  57 */       return total - this.buf.length;
/*     */     }
/*     */     
/*  60 */     return total - leftOver;
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
/*     */   public int getOutputSize(int len)
/*     */   {
/*  74 */     return len + this.bufOff;
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
/*     */   public int processByte(byte in, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/*  93 */     int resultLen = 0;
/*     */     
/*  95 */     if (this.bufOff == this.buf.length)
/*     */     {
/*  97 */       resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
/*  98 */       System.arraycopy(this.buf, this.blockSize, this.buf, 0, this.blockSize);
/*     */       
/* 100 */       this.bufOff = this.blockSize;
/*     */     }
/*     */     
/* 103 */     this.buf[(this.bufOff++)] = in;
/*     */     
/* 105 */     return resultLen;
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
/*     */   public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 128 */     if (len < 0)
/*     */     {
/* 130 */       throw new IllegalArgumentException("Can't have a negative input length!");
/*     */     }
/*     */     
/* 133 */     int blockSize = getBlockSize();
/* 134 */     int length = getUpdateOutputSize(len);
/*     */     
/* 136 */     if (length > 0)
/*     */     {
/* 138 */       if (outOff + length > out.length)
/*     */       {
/* 140 */         throw new DataLengthException("output buffer too short");
/*     */       }
/*     */     }
/*     */     
/* 144 */     int resultLen = 0;
/* 145 */     int gapLen = this.buf.length - this.bufOff;
/*     */     
/* 147 */     if (len > gapLen)
/*     */     {
/* 149 */       System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
/*     */       
/* 151 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff);
/* 152 */       System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
/*     */       
/* 154 */       this.bufOff = blockSize;
/*     */       
/* 156 */       len -= gapLen;
/* 157 */       inOff += gapLen;
/*     */       
/* 159 */       while (len > blockSize)
/*     */       {
/* 161 */         System.arraycopy(in, inOff, this.buf, this.bufOff, blockSize);
/* 162 */         resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
/* 163 */         System.arraycopy(this.buf, blockSize, this.buf, 0, blockSize);
/*     */         
/* 165 */         len -= blockSize;
/* 166 */         inOff += blockSize;
/*     */       }
/*     */     }
/*     */     
/* 170 */     System.arraycopy(in, inOff, this.buf, this.bufOff, len);
/*     */     
/* 172 */     this.bufOff += len;
/*     */     
/* 174 */     return resultLen;
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
/*     */   public int doFinal(byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException, InvalidCipherTextException
/*     */   {
/* 195 */     if (this.bufOff + outOff > out.length)
/*     */     {
/* 197 */       throw new DataLengthException("output buffer to small in doFinal");
/*     */     }
/*     */     
/* 200 */     int blockSize = this.cipher.getBlockSize();
/* 201 */     int len = this.bufOff - blockSize;
/* 202 */     byte[] block = new byte[blockSize];
/*     */     
/* 204 */     if (this.forEncryption)
/*     */     {
/* 206 */       this.cipher.processBlock(this.buf, 0, block, 0);
/*     */       
/* 208 */       for (int i = this.bufOff; i != this.buf.length; i++)
/*     */       {
/* 210 */         this.buf[i] = block[(i - blockSize)];
/*     */       }
/*     */       
/* 213 */       for (int i = blockSize; i != this.bufOff; i++)
/*     */       {
/* 215 */         int tmp123_121 = i; byte[] tmp123_118 = this.buf;tmp123_118[tmp123_121] = ((byte)(tmp123_118[tmp123_121] ^ block[(i - blockSize)]));
/*     */       }
/*     */       
/* 218 */       if ((this.cipher instanceof CBCBlockCipher))
/*     */       {
/* 220 */         BlockCipher c = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
/*     */         
/* 222 */         c.processBlock(this.buf, blockSize, out, outOff);
/*     */       }
/*     */       else
/*     */       {
/* 226 */         this.cipher.processBlock(this.buf, blockSize, out, outOff);
/*     */       }
/*     */       
/* 229 */       System.arraycopy(block, 0, out, outOff + blockSize, len);
/*     */     }
/*     */     else
/*     */     {
/* 233 */       byte[] lastBlock = new byte[blockSize];
/*     */       
/* 235 */       if ((this.cipher instanceof CBCBlockCipher))
/*     */       {
/* 237 */         BlockCipher c = ((CBCBlockCipher)this.cipher).getUnderlyingCipher();
/*     */         
/* 239 */         c.processBlock(this.buf, 0, block, 0);
/*     */       }
/*     */       else
/*     */       {
/* 243 */         this.cipher.processBlock(this.buf, 0, block, 0);
/*     */       }
/*     */       
/* 246 */       for (int i = blockSize; i != this.bufOff; i++)
/*     */       {
/* 248 */         lastBlock[(i - blockSize)] = ((byte)(block[(i - blockSize)] ^ this.buf[i]));
/*     */       }
/*     */       
/* 251 */       System.arraycopy(this.buf, blockSize, block, 0, len);
/*     */       
/* 253 */       this.cipher.processBlock(block, 0, out, outOff);
/* 254 */       System.arraycopy(lastBlock, 0, out, outOff + blockSize, len);
/*     */     }
/*     */     
/* 257 */     int offset = this.bufOff;
/*     */     
/* 259 */     reset();
/*     */     
/* 261 */     return offset;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/CTSBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */