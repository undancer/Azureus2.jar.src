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
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public class PaddedBlockCipher
/*     */   extends BufferedBlockCipher
/*     */ {
/*     */   public PaddedBlockCipher(BlockCipher cipher)
/*     */   {
/*  28 */     this.cipher = cipher;
/*     */     
/*  30 */     this.buf = new byte[cipher.getBlockSize()];
/*  31 */     this.bufOff = 0;
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
/*  45 */     int total = len + this.bufOff;
/*  46 */     int leftOver = total % this.buf.length;
/*     */     
/*  48 */     if (leftOver == 0)
/*     */     {
/*  50 */       if (this.forEncryption)
/*     */       {
/*  52 */         return total + this.buf.length;
/*     */       }
/*     */       
/*  55 */       return total;
/*     */     }
/*     */     
/*  58 */     return total - leftOver + this.buf.length;
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
/*  72 */     int total = len + this.bufOff;
/*  73 */     int leftOver = total % this.buf.length;
/*     */     
/*  75 */     if (leftOver == 0)
/*     */     {
/*  77 */       return total - this.buf.length;
/*     */     }
/*     */     
/*  80 */     return total - leftOver;
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
/*     */   public int processByte(byte in, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/*  98 */     int resultLen = 0;
/*     */     
/* 100 */     if (this.bufOff == this.buf.length)
/*     */     {
/* 102 */       resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
/* 103 */       this.bufOff = 0;
/*     */     }
/*     */     
/* 106 */     this.buf[(this.bufOff++)] = in;
/*     */     
/* 108 */     return resultLen;
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
/*     */   public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff)
/*     */     throws DataLengthException, IllegalStateException
/*     */   {
/* 130 */     if (len < 0)
/*     */     {
/* 132 */       throw new IllegalArgumentException("Can't have a negative input length!");
/*     */     }
/*     */     
/* 135 */     int blockSize = getBlockSize();
/* 136 */     int length = getUpdateOutputSize(len);
/*     */     
/* 138 */     if (length > 0)
/*     */     {
/* 140 */       if (outOff + length > out.length)
/*     */       {
/* 142 */         throw new DataLengthException("output buffer too short");
/*     */       }
/*     */     }
/*     */     
/* 146 */     int resultLen = 0;
/* 147 */     int gapLen = this.buf.length - this.bufOff;
/*     */     
/* 149 */     if (len > gapLen)
/*     */     {
/* 151 */       System.arraycopy(in, inOff, this.buf, this.bufOff, gapLen);
/*     */       
/* 153 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff);
/*     */       
/* 155 */       this.bufOff = 0;
/* 156 */       len -= gapLen;
/* 157 */       inOff += gapLen;
/*     */       
/* 159 */       while (len > this.buf.length)
/*     */       {
/* 161 */         resultLen += this.cipher.processBlock(in, inOff, out, outOff + resultLen);
/*     */         
/* 163 */         len -= blockSize;
/* 164 */         inOff += blockSize;
/*     */       }
/*     */     }
/*     */     
/* 168 */     System.arraycopy(in, inOff, this.buf, this.bufOff, len);
/*     */     
/* 170 */     this.bufOff += len;
/*     */     
/* 172 */     return resultLen;
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
/* 193 */     int blockSize = this.cipher.getBlockSize();
/* 194 */     int resultLen = 0;
/*     */     
/* 196 */     if (this.forEncryption)
/*     */     {
/* 198 */       if (this.bufOff == blockSize)
/*     */       {
/* 200 */         if (outOff + 2 * blockSize > out.length)
/*     */         {
/* 202 */           throw new DataLengthException("output buffer too short");
/*     */         }
/*     */         
/* 205 */         resultLen = this.cipher.processBlock(this.buf, 0, out, outOff);
/* 206 */         this.bufOff = 0;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 212 */       byte code = (byte)(blockSize - this.bufOff);
/*     */       
/* 214 */       while (this.bufOff < blockSize)
/*     */       {
/* 216 */         this.buf[this.bufOff] = code;
/* 217 */         this.bufOff += 1;
/*     */       }
/*     */       
/* 220 */       resultLen += this.cipher.processBlock(this.buf, 0, out, outOff + resultLen);
/*     */     }
/*     */     else
/*     */     {
/* 224 */       if (this.bufOff == blockSize)
/*     */       {
/* 226 */         resultLen = this.cipher.processBlock(this.buf, 0, this.buf, 0);
/* 227 */         this.bufOff = 0;
/*     */       }
/*     */       else
/*     */       {
/* 231 */         throw new DataLengthException("last block incomplete in decryption");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 237 */       int count = this.buf[(blockSize - 1)] & 0xFF;
/*     */       
/* 239 */       if ((count < 0) || (count > blockSize))
/*     */       {
/* 241 */         throw new InvalidCipherTextException("pad block corrupted");
/*     */       }
/*     */       
/* 244 */       resultLen -= count;
/*     */       
/* 246 */       System.arraycopy(this.buf, 0, out, outOff, resultLen);
/*     */     }
/*     */     
/* 249 */     reset();
/*     */     
/* 251 */     return resultLen;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/bouncycastle/crypto/modes/PaddedBlockCipher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */